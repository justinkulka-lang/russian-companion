package dev.russiancompanion.domain.progress

import dev.russiancompanion.domain.plan.ActivityLogView
import dev.russiancompanion.domain.plan.RecordedCategory
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Full v1 log, review, and preserve schema.
 *
 * Activity logs are admitted only in the seven governed categories, with a
 * nonnegative duration and a required local timestamp. Grammar and vocabulary
 * entries are self-attested book and AnkiDroid practice; they never become
 * independently verified records. A ProgressSnapshot names the contributing
 * ActivityLog identities and category totals so later logs cannot rewrite a
 * captured review. Export completion is accepted only when recorded counts
 * match a built package; a failed or unreadable write cannot be stored as
 * complete.
 */
internal const val PROGRESS_ACTIVITY_ONLY_STATEMENT =
    "Recorded activity is work completed, not certified ability."

internal const val PROGRESS_SNAPSHOT_VERSION = "1"

internal data class ProgressWriteAdmission(
    val accepted: Boolean,
    val reason: String? = null,
)

internal data class ExportCounts(
    val logCount: Int,
    val evidenceCount: Int,
    val progressCount: Int,
    val recordingCount: Int,
)

@Serializable
internal data class ProgressCategoryTotal(
    val category: String,
    val recordedMinutes: Long,
    val completionCount: Int,
)

@Serializable
internal data class ProgressSnapshot(
    val version: String,
    val activityLogIds: List<String>,
    val totals: List<ProgressCategoryTotal>,
)

internal fun categoryRequiresSelfAttestation(category: RecordedCategory): Boolean {
    return category == RecordedCategory.GRAMMAR || category == RecordedCategory.VOCABULARY_AND_FORMS
}

internal fun activityLogAccepted(
    category: RecordedCategory,
    durationMinutes: Int,
    recordedAtLocal: String,
    selfAttested: Boolean,
    bookOrAnkiActivity: Boolean = false,
): ProgressWriteAdmission {
    if (recordedAtLocal.isBlank()) return ProgressWriteAdmission(false, "timestamp-required")
    if (durationMinutes < 0) return ProgressWriteAdmission(false, "duration-invalid")
    if ((bookOrAnkiActivity || categoryRequiresSelfAttestation(category)) && !selfAttested) {
        return ProgressWriteAdmission(false, "attestation-required")
    }
    return ProgressWriteAdmission(true)
}

/**
 * Captures named log identities and category totals. Negative durations make
 * the set unreadable, so no snapshot is produced.
 */
internal fun captureProgressSnapshot(logs: List<ActivityLogView>): ProgressSnapshot? {
    val review = reviewProgress(logs)
    if (!review.readable) return null
    return ProgressSnapshot(
        version = PROGRESS_SNAPSHOT_VERSION,
        activityLogIds = logs.map { it.id }.distinct().sorted(),
        totals = review.categories.map { balance ->
            ProgressCategoryTotal(
                category = balance.category.name,
                recordedMinutes = balance.recordedMinutes,
                completionCount = balance.completionCount,
            )
        },
    )
}

/**
 * Recomputes a captured snapshot from only the named log identities. Later
 * logs, unnamed logs, and missing named identities cannot be substituted.
 */
internal fun reproduceProgress(
    snapshot: ProgressSnapshot,
    logs: List<ActivityLogView>,
): ProgressReview {
    if (snapshot.version != PROGRESS_SNAPSHOT_VERSION) return ProgressReview.unreadable()
    if (snapshot.activityLogIds.any { it.isBlank() }) return ProgressReview.unreadable()
    if (snapshot.activityLogIds.distinct().size != snapshot.activityLogIds.size) {
        return ProgressReview.unreadable()
    }
    val byId = logs.associateBy { it.id }
    val selected = snapshot.activityLogIds.map { id -> byId[id] ?: return ProgressReview.unreadable() }
    return reviewProgress(selected)
}

internal fun reproducedProgressMatches(
    snapshot: ProgressSnapshot,
    reproduced: ProgressReview,
): Boolean {
    if (!reproduced.readable) return false
    if (reproduced.categories.size != snapshot.totals.size) return false
    val expected = snapshot.totals.associateBy { it.category }
    return reproduced.categories.all { balance ->
        val total = expected[balance.category.name] ?: return@all false
        total.recordedMinutes == balance.recordedMinutes &&
            total.completionCount == balance.completionCount
    }
}

private val ProgressJson = Json {
    ignoreUnknownKeys = false
    explicitNulls = true
    encodeDefaults = true
}

internal fun encodeProgressSnapshot(snapshot: ProgressSnapshot): String {
    return ProgressJson.encodeToString(ProgressSnapshot.serializer(), snapshot)
}

internal fun parseProgressSnapshot(raw: String): ProgressSnapshot? {
    if (raw.isBlank()) return null
    return try {
        val snapshot = ProgressJson.decodeFromString(ProgressSnapshot.serializer(), raw)
        if (snapshot.version != PROGRESS_SNAPSHOT_VERSION) return null
        if (snapshot.activityLogIds.any { it.isBlank() }) return null
        if (snapshot.activityLogIds.distinct().size != snapshot.activityLogIds.size) return null
        if (snapshot.totals.map { it.category } != RecordedCategory.entries.map { it.name }) return null
        snapshot
    } catch (_: SerializationException) {
        null
    } catch (_: IllegalArgumentException) {
        null
    }
}

/**
 * COMPLETED is accepted only when recorded counts match a built package.
 * FAILED and NOT_CONFIRMED may be stored without claiming success.
 */
internal fun exportCompletionAccepted(
    statusName: String,
    recorded: ExportCounts,
    packageCounts: ExportCounts?,
    exportedAtLocal: String,
): ProgressWriteAdmission {
    if (exportedAtLocal.isBlank()) return ProgressWriteAdmission(false, "timestamp-required")
    if (!countsNonnegative(recorded)) return ProgressWriteAdmission(false, "counts-invalid")
    if (packageCounts != null && !countsNonnegative(packageCounts)) {
        return ProgressWriteAdmission(false, "counts-invalid")
    }
    return when (statusName) {
        "COMPLETED" -> {
            if (packageCounts == null) return ProgressWriteAdmission(false, "package-required")
            if (recorded != packageCounts) return ProgressWriteAdmission(false, "counts-mismatch")
            ProgressWriteAdmission(true)
        }
        "FAILED", "NOT_CONFIRMED" -> ProgressWriteAdmission(true)
        else -> ProgressWriteAdmission(false, "status-invalid")
    }
}

private fun countsNonnegative(counts: ExportCounts): Boolean {
    return counts.logCount >= 0 &&
        counts.evidenceCount >= 0 &&
        counts.progressCount >= 0 &&
        counts.recordingCount >= 0
}
