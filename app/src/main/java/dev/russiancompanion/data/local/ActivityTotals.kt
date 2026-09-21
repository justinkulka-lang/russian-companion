package dev.russiancompanion.data.local

internal data class CategoryTotal(
    val category: ActivityCategory,
    val recordedMinutes: Long,
    val completionCount: Int,
)

/**
 * Progress is recomputed from activity logs. No snapshot and no ability score is stored.
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
