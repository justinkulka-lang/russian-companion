package dev.russiancompanion.v2.curriculum

import kotlinx.serialization.Serializable
import java.security.MessageDigest

internal const val CUMULATIVE_REVIEW_SCHEMA = 1
internal const val CUMULATIVE_REVIEW_ROLE = "CUMULATIVE_REVIEW"

@Serializable
internal data class CumulativeReviewSlot(
    val objectiveId: String, val skill: String, val itemId: String,
    val ownerUnit: Int, val itemDigest: String, val unitDigest: String, val teachingDigest: String,
)

@Serializable
internal data class CumulativeReviewAssignment(
    val id: String, val targetUnit: Int, val policyId: String, val policyDigest: String,
    val responseRuleDigest: String, val boundary: Long, val slots: List<CumulativeReviewSlot>, val digest: String,
    val schema: Int = CUMULATIVE_REVIEW_SCHEMA,
)

@Serializable
internal data class CumulativeReviewAnswer(
    val id: String, val assignmentId: String, val assignmentDigest: String,
    val slotIndex: Int, val selected: String, val acceptedOrder: Long,
    val role: String = CUMULATIVE_REVIEW_ROLE, val schema: Int = CUMULATIVE_REVIEW_SCHEMA,
)

/** Explicit first policy; no later gate is activated by this infrastructure. */
internal object Unit3CumulativeReviewPolicy {
    const val id = "unit3-prior-literacy-v1"
    val pool = listOf(
        Triple("obj-u1-decoding", "BOUNDED_READING", listOf("u1-i-decode-pair")),
        Triple("obj-u1-confusable", "PRINTED_LETTER_RECOGNITION", listOf(
            "u1-i-р-not-p", "u1-i-н-not-h", "u1-i-с-not-c", "u1-i-в-not-b", "u1-i-у-not-y", "u1-i-х-not-x")),
    )
    val digest = reviewHash(canonicalParts(listOf(id, "target:3", "slots:2") + pool.map {
        canonicalParts(listOf(it.first, it.second, canonicalParts(it.third)))
    }))
}

internal fun reviewHash(text: String): String = MessageDigest.getInstance("SHA-256")
    .digest(text.toByteArray()).joinToString("") { "%02x".format(it.toInt() and 255) }

internal fun reviewAssignmentDigest(a: CumulativeReviewAssignment): String = reviewHash(canonicalParts(
    listOf(a.schema.toString(), a.id, a.targetUnit.toString(), a.policyId, a.policyDigest, a.responseRuleDigest, a.boundary.toString()) +
        a.slots.map { s -> canonicalParts(listOf(s.objectiveId, s.skill, s.itemId, s.ownerUnit.toString(),
            s.itemDigest, s.unitDigest, s.teachingDigest)) },
))

private fun reviewUnitDigest(pack: V2CurriculumPack, unit: Int): String =
    V2PackIdentityIndex.unitDigest(pack, unit) ?: V2Validator.unitContentReviewDigest(pack, unit)

private fun teachingDigest(pack: V2CurriculumPack, objective: V2Objective): String = reviewHash(canonicalParts(
    (objective.instructionStepIds + objective.practiceStepIds).map { id ->
        val step = pack.step(id)?.second ?: return ""
        stepContentDigest(pack, step)
    },
))

/** Requires actual taught evidence; placement and bare historical completion are insufficient. */
internal fun createUnit3ReviewAssignment(pack: V2CurriculumPack, learner: V2LearnerView,
                                       id: String, boundary: Long): CumulativeReviewAssignment? {
    if (id.isBlank() || boundary < 0 || pack.schemaVersion != 1) return null
    V2PackIdentityIndex.warm(pack)
    val target = pack.unit(3) ?: return null
    if (!target.openable() || unmetCurrentPrerequisites(pack, target, learner).isNotEmpty()) return null
    val taught = currentCompletedUnitNumbers(pack, learner)
    val slots = Unit3CumulativeReviewPolicy.pool.map { (objectiveId, skill, candidates) ->
        val unit = pack.units.singleOrNull { u -> u.objectives.any { it.id == objectiveId } } ?: return null
        val o = unit.objectives.single { it.id == objectiveId }
        if (unit.number !in taught || !unit.openable() || unit.number >= 3 || !o.gating ||
            o.kind != "RECOGNITION" || o.instructionStepIds.isEmpty() || o.practiceStepIds.isEmpty() ||
            (o.instructionStepIds + o.practiceStepIds).any { !completedStepCounts(pack, learner, it) } ||
            objectiveState(o, learner, pack) != V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS) return null
        val item = candidates.mapNotNull { pack.item(it) }.firstOrNull {
            it.id in o.checkItemIds && it.objectiveId == o.id && it.options.size >= 2 && it.answer in it.options
        } ?: return null
        CumulativeReviewSlot(o.id, skill, item.id, unit.number, V2Validator.checkItemContentDigest(item),
            reviewUnitDigest(pack, unit.number), teachingDigest(pack, o))
    }
    val a = CumulativeReviewAssignment(id, 3, Unit3CumulativeReviewPolicy.id,
        Unit3CumulativeReviewPolicy.digest, closedResponseRuleDigest(), boundary, slots, "")
    return a.copy(digest = reviewAssignmentDigest(a))
}

internal fun reviewAssignmentCurrent(pack: V2CurriculumPack, a: CumulativeReviewAssignment): Boolean {
    if (pack.schemaVersion != 1 || a.schema != CUMULATIVE_REVIEW_SCHEMA || a.id.isBlank() || a.targetUnit != 3 || a.boundary < 0 ||
        a.policyId != Unit3CumulativeReviewPolicy.id || a.policyDigest != Unit3CumulativeReviewPolicy.digest ||
        a.responseRuleDigest != closedResponseRuleDigest() ||
        a.slots.size != 2 || a.digest != reviewAssignmentDigest(a)) return false
    return a.slots.zip(Unit3CumulativeReviewPolicy.pool).all { (s, spec) ->
        val unit = pack.unit(s.ownerUnit) ?: return@all false
        val o = unit.objectives.singleOrNull { it.id == s.objectiveId } ?: return@all false
        val item = pack.item(s.itemId) ?: return@all false
        unit.openable() && unit.number < a.targetUnit && o.gating && o.kind == "RECOGNITION" &&
            s.objectiveId == spec.first && s.skill == spec.second && s.itemId in spec.third &&
            s.itemId in o.checkItemIds && item.objectiveId == o.id &&
            s.itemDigest == V2Validator.checkItemContentDigest(item) &&
            s.unitDigest == reviewUnitDigest(pack, s.ownerUnit) &&
            s.teachingDigest == teachingDigest(pack, o)
    }
}

internal fun completedReviewSlots(pack: V2CurriculumPack, a: CumulativeReviewAssignment,
                                 answers: List<CumulativeReviewAnswer>): Set<Int> {
    if (!reviewAssignmentCurrent(pack, a) ||
        reviewHistoryDefects(listOf(a), answers.filter { it.assignmentId == a.id }) != null) return emptySet()
    return a.slots.indices.filter { index ->
        val answer = answers.filter { it.assignmentId == a.id && it.assignmentDigest == a.digest &&
            it.slotIndex == index && it.schema == CUMULATIVE_REVIEW_SCHEMA && it.role == CUMULATIVE_REVIEW_ROLE &&
            it.acceptedOrder > a.boundary }.maxByOrNull { it.acceptedOrder } ?: return@filter false
        answer.selected == pack.item(a.slots[index].itemId)?.answer
    }.toSet()
}

/** Structural backup validation preserves stale histories but refuses forged links/order/hash. */
internal fun reviewHistoryDefects(assignments: List<CumulativeReviewAssignment>,
                                 answers: List<CumulativeReviewAnswer>): String? {
    if (assignments.map { it.id }.distinct().size != assignments.size ||
        answers.map { it.id }.distinct().size != answers.size ||
        answers.map { it.acceptedOrder }.distinct().size != answers.size) return "duplicate review identity/order"
    val maximum = answers.maxOfOrNull { it.acceptedOrder } ?: 0
    val hashValid: (String) -> Boolean = { it.length == 64 && it.all { c -> c in '0'..'9' || c in 'a'..'f' } }
    for (a in assignments) {
        if (a.schema != CUMULATIVE_REVIEW_SCHEMA || a.id.isBlank() || a.targetUnit !in 1..30 ||
            a.boundary < 0 || a.boundary > maximum || a.policyId.isBlank() || !hashValid(a.policyDigest) || !hashValid(a.responseRuleDigest) || a.slots.size !in 2..6 || a.slots.map { it.objectiveId }.distinct().size != a.slots.size ||
            a.digest != reviewAssignmentDigest(a) || a.slots.any { it.ownerUnit !in 1 until a.targetUnit ||
                !hashValid(it.itemDigest) || !hashValid(it.unitDigest) || !hashValid(it.teachingDigest) ||
                it.skill.isBlank() || it.itemId.isBlank() || it.objectiveId.isBlank() }) return "invalid review assignment"
    }
    for (answer in answers) {
        val a = assignments.singleOrNull { it.id == answer.assignmentId } ?: return "missing review assignment"
        if (answer.schema != CUMULATIVE_REVIEW_SCHEMA || answer.role != CUMULATIVE_REVIEW_ROLE ||
            answer.assignmentDigest != a.digest || answer.slotIndex !in a.slots.indices ||
            answer.acceptedOrder <= a.boundary || answer.selected.isBlank() || answer.id.isBlank()) return "invalid review answer"
    }
    return null
}
