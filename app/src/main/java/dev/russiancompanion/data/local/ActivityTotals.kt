package dev.russiancompanion.data.local

internal data class CategoryTotal(
    val category: ActivityCategory,
    val recordedMinutes: Long,
    val completionCount: Int,
)

/**
 * Progress totals are recomputed from activity logs. A ProgressSnapshot names
 * those log identities; it is not an ability score.
 */
internal fun totalsFrom(logs: List<ActivityLogEntity>): List<CategoryTotal> {
    return ActivityCategory.entries.map { category ->
        val matching = logs.filter { it.category == category }
        CategoryTotal(
            category = category,
            recordedMinutes = matching.sumOf { it.durationMinutes.toLong() },
            completionCount = matching.count { it.completed },
        )
    }
}
