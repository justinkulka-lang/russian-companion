package dev.russiancompanion.domain.progress

import dev.russiancompanion.domain.plan.ActivityLogView
import dev.russiancompanion.domain.plan.RecordedCategory
import java.time.LocalDate
import java.time.LocalDateTime

internal data class WindowBalance(
    val days: Int,
    val readable: Boolean,
    val categories: List<CategoryBalance>,
    val recordedCategories: List<RecordedCategory>,
    val unrecordedCategories: List<RecordedCategory>,
)

internal fun studyBalance(
    logs: List<ActivityLogView>,
    today: LocalDate,
    days: Int,
): WindowBalance {
    val windowLogs = logs.filter { log ->
        val recorded = parseLogDate(log.recordedAtLocal) ?: return@filter false
        !recorded.isBefore(today.minusDays((days - 1).toLong())) && !recorded.isAfter(today)
    }
    if (windowLogs.any { it.minutes < 0 }) {
        return WindowBalance(
            days = days,
            readable = false,
            categories = emptyList(),
            recordedCategories = emptyList(),
            unrecordedCategories = emptyList(),
        )
    }
    val categories = RecordedCategory.entries.map { category ->
        val matching = windowLogs.filter { it.category == category }
        CategoryBalance(
            category = category,
            recordedMinutes = matching.sumOf { it.minutes.toLong() },
            completionCount = matching.count { it.completed },
        )
    }
    val recorded = categories.filter { categoryHasRecordedActivity(it) }.map { it.category }
    val unrecorded = categories.filter { !categoryHasRecordedActivity(it) }.map { it.category }
    return WindowBalance(
        days = days,
        readable = true,
        categories = categories,
        recordedCategories = recorded,
        unrecordedCategories = unrecorded,
    )
}

internal fun categoryHasRecordedActivity(balance: CategoryBalance): Boolean {
    return balance.completionCount > 0 || balance.recordedMinutes > 0
}

internal fun parseLogDate(recordedAtLocal: String): LocalDate? {
    return try {
        LocalDateTime.parse(recordedAtLocal).toLocalDate()
    } catch (_: Exception) {
        try {
            LocalDate.parse(recordedAtLocal.take(10))
        } catch (_: Exception) {
            null
        }
    }
}
