package dev.russiancompanion.v2.curriculum

import kotlinx.serialization.Serializable

internal const val SPACED_REVIEW_SCHEMA = 1
internal const val SPACED_REVIEW_MISS_DELAY_MILLIS = 10 * 60 * 1000L
internal const val SPACED_REVIEW_DAY_MILLIS = 24 * 60 * 60 * 1000L
internal const val SPACED_REVIEW_MAX_INTERVAL_DAYS = 3650
// Last millisecond of year 9999 UTC. Never wrap a due date or serialize an unrenderable date.
internal const val SPACED_REVIEW_MAX_TIME = 253402300799999L
private const val MAX_STREAK = 13

/** One current record per item. No old development format or scoring meaning is imported. */
@Serializable
internal data class V2SpacedReviewRecord(
    val itemId: String,
    val objectiveId: String,
    val unitNumber: Int,
    val itemDigest: String,
    val unitContentDigest: String,
    val responseRuleDigest: String,
    val schedulerRuleDigest: String,
    val consecutiveCorrect: Int,
    val intervalDays: Int,
    val dueAtEpochMillis: Long,
    val lastAnsweredAtEpochMillis: Long,
    val lastEventId: String,
    /** Monotonic per-item schedule-write order, allocated by storage, never a caller's clock. */
    val acceptedOrder: Long,
    val schema: Int = SPACED_REVIEW_SCHEMA,
)

/** Bounded engineering policy, not a score of proficiency or an Anki replacement. */
internal fun spacedReviewRuleDigest(): String = reviewHash(canonicalParts(listOf(
    "app-spaced-review-v1", "miss:10-minutes; reset correct streak",
    "due correct:1-day then double; maximum 3650 days and streak 13",
    "early correct:keep due date and streak; never graduate an immediate retry",
    "clock rollback:clamp to prior accepted time; monotonic per-item storage order; current identities only",
)))

private fun intervalForStreak(streak: Int): Int = if (streak == 0) 0 else
    (1L shl (streak - 1)).coerceAtMost(SPACED_REVIEW_MAX_INTERVAL_DAYS.toLong()).toInt()

/** Structural check for the single current serialization, usable without loading curriculum. */
internal fun spacedReviewRecordDefect(record: V2SpacedReviewRecord): String? {
    val hash = Regex("[a-f0-9]{64}")
    return when {
        record.schema != SPACED_REVIEW_SCHEMA -> "Unsupported spaced-review schema"
        record.itemId.isBlank() || record.objectiveId.isBlank() || record.unitNumber !in 1..30 -> "Invalid spaced-review item identity"
        listOf(record.itemDigest, record.unitContentDigest, record.responseRuleDigest, record.schedulerRuleDigest)
            .any { !hash.matches(it) } -> "Invalid spaced-review digest"
        record.schedulerRuleDigest != spacedReviewRuleDigest() -> "Changed spaced-review scheduling rule"
        record.consecutiveCorrect !in 0..MAX_STREAK || record.intervalDays != intervalForStreak(record.consecutiveCorrect) ->
            "Invalid spaced-review interval or streak"
        record.lastAnsweredAtEpochMillis !in 0..SPACED_REVIEW_MAX_TIME ||
            record.dueAtEpochMillis !in record.lastAnsweredAtEpochMillis..SPACED_REVIEW_MAX_TIME -> "Invalid spaced-review time"
        record.dueAtEpochMillis - record.lastAnsweredAtEpochMillis >
            (if (record.consecutiveCorrect == 0) SPACED_REVIEW_MISS_DELAY_MILLIS else record.intervalDays * SPACED_REVIEW_DAY_MILLIS) ->
            "Spaced-review due date exceeds its interval"
        record.acceptedOrder <= 0 ||
            !(record.lastEventId.startsWith("attempt:") || record.lastEventId.startsWith("review:")) ||
            record.lastEventId.substringAfter(':').isBlank() -> "Invalid spaced-review accepted event"
        else -> null
    }
}

private data class SpacedItem(val item: V2CheckItem, val unit: V2Unit)

private fun spacedItem(pack: V2CurriculumPack, itemId: String): SpacedItem? {
    if (pack.schemaVersion != 1 || itemId.isBlank()) return null
    val item = pack.items.singleOrNull { it.id == itemId } ?: return null
    if (scoreCheckedResponse(item, item.answer) != true) return null
    val unit = pack.units.singleOrNull { candidate -> candidate.objectives.any { it.id == item.objectiveId } } ?: return null
    val objective = unit.objectives.singleOrNull { it.id == item.objectiveId } ?: return null
    if (unit.number !in 1..30 || item.objectiveId.isBlank() || !unit.openable() || !objective.gating || objective.unitNumber != unit.number) return null
    val checkedHere = item.id in objective.checkItemIds || unit.sessions.flatMap { it.steps }.any {
        it.kind in setOf("CHECK", "PRACTICE") && item.id in it.itemIds
    }
    return if (checkedHere) SpacedItem(item, unit) else null
}

internal fun spacedReviewRecordCurrent(pack: V2CurriculumPack, record: V2SpacedReviewRecord): Boolean {
    if (spacedReviewRecordDefect(record) != null) return false
    val (item, unit) = spacedItem(pack, record.itemId) ?: return false
    return record.objectiveId == item.objectiveId && record.unitNumber == unit.number &&
        record.itemDigest == V2Validator.checkItemContentDigest(item) &&
        record.responseRuleDigest == responseRuleDigest(item) &&
        record.unitContentDigest == V2PackIdentityIndex.unitDigest(pack, unit.number)
}

/**
 * Call only inside the accepted-answer transaction (T11-b). Recompute correctness from the current
 * reviewed item; accept no caller-provided correct flag. Storage supplies the durable event id/order.
 * An early correct retry keeps the earlier due time, including a miss's ten-minute return.
 */
internal fun scheduleSpacedReviewAnswer(
    pack: V2CurriculumPack,
    previous: V2SpacedReviewRecord?,
    itemId: String,
    response: String,
    eventId: String,
    acceptedOrder: Long,
    answeredAtEpochMillis: Long,
): V2SpacedReviewRecord? {
    if (acceptedOrder <= 0 || answeredAtEpochMillis !in 0..SPACED_REVIEW_MAX_TIME) return null
    if (!(eventId.startsWith("attempt:") || eventId.startsWith("review:")) || eventId.substringAfter(':').isBlank()) return null
    if (previous != null && (previous.itemId != itemId || spacedReviewRecordDefect(previous) != null)) return null
    if (previous != null && (acceptedOrder <= previous.acceptedOrder || eventId == previous.lastEventId)) return null
    val (item, unit) = spacedItem(pack, itemId) ?: return null
    val correct = scoreCheckedResponse(item, response) ?: return null
    val prior = previous?.takeIf { spacedReviewRecordCurrent(pack, it) }
    val now = maxOf(answeredAtEpochMillis, prior?.lastAnsweredAtEpochMillis ?: 0)
    val earlyCorrect = correct && prior != null && now < prior.dueAtEpochMillis
    val streak = when {
        !correct -> 0
        earlyCorrect -> prior!!.consecutiveCorrect
        else -> ((prior?.consecutiveCorrect ?: 0) + 1).coerceAtMost(MAX_STREAK)
    }
    val intervalDays = intervalForStreak(streak)
    val delay = if (streak == 0) SPACED_REVIEW_MISS_DELAY_MILLIS else intervalDays * SPACED_REVIEW_DAY_MILLIS
    val due = if (earlyCorrect) prior!!.dueAtEpochMillis else minOf(SPACED_REVIEW_MAX_TIME, now + delay)
    return V2SpacedReviewRecord(item.id, item.objectiveId, unit.number,
        V2Validator.checkItemContentDigest(item), V2PackIdentityIndex.unitDigest(pack, unit.number) ?: return null,
        responseRuleDigest(item), spacedReviewRuleDigest(), streak, intervalDays, due, now, eventId, acceptedOrder)
}

/** Pure query: never seeds, answers, mutates progress, or substitutes for the required unit review. */
internal fun dueSpacedReviews(
    pack: V2CurriculumPack,
    records: List<V2SpacedReviewRecord>,
    reachedUnitNumbers: Set<Int>,
    nowEpochMillis: Long,
): List<V2SpacedReviewRecord> {
    if (nowEpochMillis !in 0..SPACED_REVIEW_MAX_TIME) return emptyList()
    // Storage has an item primary key. Ambiguous imported duplicates must never become two questions.
    return records.groupBy { it.itemId }.values.filter { it.size == 1 }.map { it.single() }
        .filter { it.unitNumber in reachedUnitNumbers && it.dueAtEpochMillis <= nowEpochMillis && spacedReviewRecordCurrent(pack, it) }
        .sortedWith(compareBy({ it.dueAtEpochMillis }, { it.unitNumber }, { it.itemId }))
}
