package dev.russiancompanion.domain.progress

import dev.russiancompanion.domain.plan.ActivityLogView
import dev.russiancompanion.domain.plan.RecordedCategory

internal data class CategoryBalance(
    val category: RecordedCategory,
    val recordedMinutes: Long,
    val completionCount: Int,
)

internal data class ProgressReview(
    val readable: Boolean,
    val totalMinutes: Long,
    val totalCompletions: Int,
    val categories: List<CategoryBalance>,
    val recentLogs: List<ActivityLogView>,
) {
    companion object {
        fun empty(): ProgressReview {
            return ProgressReview(
                readable = true,
                totalMinutes = 0,
                totalCompletions = 0,
                categories = RecordedCategory.entries.map { category ->
                    CategoryBalance(category, recordedMinutes = 0, completionCount = 0)
                },
                recentLogs = emptyList(),
            )
        }

        fun unreadable(): ProgressReview {
            return ProgressReview(
                readable = false,
                totalMinutes = 0,
                totalCompletions = 0,
                categories = emptyList(),
                recentLogs = emptyList(),
            )
        }
    }
}

/**
 * Totals are the sum of recorded logs. A negative duration makes the whole review unreadable.
 */
internal fun reviewProgress(logs: List<ActivityLogView>): ProgressReview {
    if (logs.any { it.minutes < 0 }) return ProgressReview.unreadable()
    val categories = RecordedCategory.entries.map { category ->
        val matching = logs.filter { it.category == category }
        CategoryBalance(
            category = category,
            recordedMinutes = matching.sumOf { it.minutes.toLong() },
            completionCount = matching.count { it.completed },
        )
    }
    val recent = logs.sortedWith(
        compareByDescending<ActivityLogView> { it.recordedAtLocal }.thenByDescending { it.id },
    )
    return ProgressReview(
        readable = true,
        totalMinutes = categories.sumOf { it.recordedMinutes },
        totalCompletions = categories.sumOf { it.completionCount },
        categories = categories,
        recentLogs = recent,
    )
}

internal fun activityAttestationLabel(category: RecordedCategory, selfAttested: Boolean): String {
    if (!selfAttested) return ""
    return when (category) {
        RecordedCategory.GRAMMAR -> BOOK_ATTESTATION
        RecordedCategory.VOCABULARY_AND_FORMS -> ANKI_ATTESTATION
        else -> "Self-attested"
    }
}

internal fun evidenceAttestationLabel(evidenceType: String): String {
    return when (evidenceType) {
        "BOOK_REVISION_SCORE" -> BOOK_ATTESTATION
        "ANKI_DROID_STATUS" -> ANKI_ATTESTATION
        "PRESCRIBED_WORK_COMPLETION",
        "TUTOR_SIGN_OFF",
        -> "User recorded"
        else -> ""
    }
}

internal const val BOOK_ATTESTATION = "Self-attested — not independently verified"
internal const val ANKI_ATTESTATION = "Self-attested — no AnkiDroid integration; not independently verified"
