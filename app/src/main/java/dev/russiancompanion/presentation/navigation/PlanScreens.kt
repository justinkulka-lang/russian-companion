package dev.russiancompanion.presentation.navigation

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import dev.russiancompanion.R
import dev.russiancompanion.domain.navigation.NavigationSnapshot
import dev.russiancompanion.domain.navigation.activeLessonHasPlan
import dev.russiancompanion.domain.navigation.practicePlayable
import dev.russiancompanion.domain.plan.ChecklistActivity
import dev.russiancompanion.domain.plan.ChecklistItem
import dev.russiancompanion.domain.plan.RecordedCategory
import dev.russiancompanion.domain.plan.RecordedGateStatus
import dev.russiancompanion.domain.plan.canMarkComplete

@Composable
internal fun HomePlanSection(
    snapshot: NavigationSnapshot,
    onBegin: (ChecklistItem) -> Unit,
    onTodaysPlan: () -> Unit,
    onSelectLesson: () -> Unit,
) {
    val lesson = snapshot.activeLessonReference
    if (lesson != null) {
        Text(stringResource(R.string.home_current_lesson, lesson))
    }
    if (!activeLessonHasPlan(snapshot)) {
        Text(stringResource(R.string.home_no_plan))
        TextButton(onClick = onSelectLesson) { Text(stringResource(R.string.home_select_lesson)) }
    } else {
        val plan = snapshot.dailyPlan
        if (!plan.requiredWorkPrescribed) {
            Text(stringResource(R.string.plan_no_work))
        } else {
            Text(
                stringResource(
                    R.string.home_required_progress,
                    plan.requiredCompletedCount,
                    plan.requiredItems.size,
                ),
            )
            val next = plan.nextIncompleteRequired
            if (next == null) {
                Text(stringResource(R.string.home_plan_complete))
                Text(stringResource(R.string.home_completion_not_proficiency))
            } else {
                Text(stringResource(R.string.home_next_activity))
                Text(next.instruction)
                val practiceReady = practicePlayable(snapshot)
                val canBegin = next.activity != ChecklistActivity.LISTEN_RECORD_COMPARE || practiceReady
                TextButton(onClick = { onBegin(next) }, enabled = canBegin) {
                    Text(stringResource(R.string.home_begin))
                }
                if (!canBegin) {
                    Text(
                        stringResource(
                            if (snapshot.audio.isEmpty()) {
                                R.string.practice_none
                            } else {
                                R.string.practice_session_unavailable
                            },
                        ),
                    )
                }
            }
        }
        TextButton(onClick = onTodaysPlan) { Text(stringResource(R.string.open_todays_plan)) }
    }
    GateSummarySection(snapshot)
    ActivitySummarySection(snapshot)
}

@Composable
internal fun TodaysPlanBody(
    snapshot: NavigationSnapshot,
    saveFailed: Boolean,
    onSelectLesson: () -> Unit,
    onStartPractice: () -> Unit,
    onComplete: (String) -> Unit,
    onIncludeTutor: (String) -> Unit,
    onRemoveTutor: (String) -> Unit,
    onRetrySave: () -> Unit,
) {
    val lesson = snapshot.activeLessonReference
    if (lesson != null) {
        Text(stringResource(R.string.home_current_lesson, lesson))
    }
    if (!activeLessonHasPlan(snapshot)) {
        Text(stringResource(R.string.home_no_plan))
        TextButton(onClick = onSelectLesson) { Text(stringResource(R.string.home_select_lesson)) }
        return
    }
    val plan = snapshot.dailyPlan
    if (!plan.requiredWorkPrescribed) {
        Text(stringResource(R.string.plan_no_work))
    }
    plan.items.forEach { item ->
        ChecklistRow(
            item = item,
            practiceAvailable = practicePlayable(snapshot),
            admittedAudio = snapshot.audio.isNotEmpty(),
            onStartPractice = onStartPractice,
            onComplete = onComplete,
            onRemoveTutor = onRemoveTutor,
        )
    }
    plan.addableTutorTasks.forEach { item ->
        Text(item.instruction)
        TextButton(onClick = { onIncludeTutor(item.planDefinitionId) }) {
            Text(stringResource(R.string.plan_add_tutor))
        }
    }
    if (saveFailed) {
        Text(stringResource(R.string.plan_not_saved))
        TextButton(onClick = onRetrySave) { Text(stringResource(R.string.navigate_retry)) }
    }
}

@Composable
private fun ChecklistRow(
    item: ChecklistItem,
    practiceAvailable: Boolean,
    admittedAudio: Boolean,
    onStartPractice: () -> Unit,
    onComplete: (String) -> Unit,
    onRemoveTutor: (String) -> Unit,
) {
    Text(
        stringResource(
            when (item.activity) {
                ChecklistActivity.BOOK -> R.string.plan_book_reference
                ChecklistActivity.ANKI_DROID -> R.string.plan_anki_separate
                ChecklistActivity.LISTEN_RECORD_COMPARE -> R.string.plan_listen
                ChecklistActivity.TUTOR -> R.string.plan_tutor_optional
            },
        ),
    )
    Text(item.instruction)
    if (item.activity == ChecklistActivity.BOOK || item.activity == ChecklistActivity.ANKI_DROID) {
        Text(stringResource(R.string.plan_self_attested))
    }
    Text(
        stringResource(
            if (item.completed) R.string.plan_completed else R.string.plan_not_completed,
        ),
    )
    if (canMarkComplete(item)) {
        TextButton(onClick = { onComplete(item.planDefinitionId) }) {
            Text(stringResource(R.string.plan_mark_complete))
        }
    }
    if (item.activity == ChecklistActivity.LISTEN_RECORD_COMPARE) {
        TextButton(onClick = onStartPractice, enabled = practiceAvailable) {
            Text(stringResource(R.string.plan_start_practice))
        }
        if (!practiceAvailable) {
            Text(
                stringResource(
                    if (admittedAudio) R.string.practice_session_unavailable else R.string.practice_none,
                ),
            )
        }
    }
    if (item.activity == ChecklistActivity.TUTOR) {
        TextButton(onClick = { onRemoveTutor(item.planDefinitionId) }) {
            Text(stringResource(R.string.plan_remove_tutor))
        }
    }
}

@Composable
private fun GateSummarySection(snapshot: NavigationSnapshot) {
    Text(stringResource(R.string.gates_not_proficiency))
    if (snapshot.gateSummaries.isEmpty()) {
        Text(stringResource(R.string.gates_none))
        return
    }
    snapshot.gateSummaries.forEach { gate ->
        val status = gate.status
        if (status == null) {
            Text(stringResource(R.string.home_gate_no_decision))
        } else {
            Text(stringResource(R.string.home_gate_status, gateStatusLabel(status)))
        }
    }
}

@Composable
private fun ActivitySummarySection(snapshot: NavigationSnapshot) {
    if (!snapshot.progress.readable) {
        Text(stringResource(R.string.local_data_message))
        return
    }
    Text(stringResource(R.string.progress_activity_only))
    val summary = snapshot.activitySummary
    val latest = summary.latest
    if (latest == null) {
        Text(stringResource(R.string.home_no_activity))
        return
    }
    if (latest.completed && latest.minutes == 0) {
        Text(stringResource(R.string.home_latest_completed_zero, categoryLabel(latest.category)))
    } else {
        Text(
            stringResource(
                R.string.home_latest_activity,
                categoryLabel(latest.category),
                latest.minutes,
                stringResource(if (latest.completed) R.string.plan_completed else R.string.plan_not_completed),
            ),
        )
    }
    for (total in summary.totals) {
        Text(recordedWorkLabel(total.category, total.recordedMinutes, total.completionCount))
    }
}

@Composable
internal fun recordedWorkLabel(category: RecordedCategory, minutes: Long, completions: Int): String {
    val name = categoryLabel(category)
    return if (minutes == 0L && completions > 0) {
        if (completions == 1) {
            stringResource(R.string.progress_category_completed_zero, name)
        } else {
            stringResource(R.string.progress_category_completed_zero_count, name, completions)
        }
    } else {
        stringResource(R.string.home_activity_total, name, minutes.toInt(), completions)
    }
}

@Composable
internal fun categoryLabel(category: RecordedCategory): String {
    return stringResource(
        when (category) {
            RecordedCategory.READING -> R.string.category_reading
            RecordedCategory.LISTENING -> R.string.category_listening
            RecordedCategory.VOCABULARY_AND_FORMS -> R.string.category_vocabulary
            RecordedCategory.GRAMMAR -> R.string.category_grammar
            RecordedCategory.SPEAKING_AND_PRONUNCIATION -> R.string.category_speaking
            RecordedCategory.WRITING -> R.string.category_writing
            RecordedCategory.TUTOR_WORK -> R.string.category_tutor
        },
    )
}

@Composable
private fun gateStatusLabel(status: RecordedGateStatus): String {
    return stringResource(
        when (status) {
            RecordedGateStatus.SATISFIED -> R.string.gate_status_satisfied
            RecordedGateStatus.NOT_YET_SATISFIED -> R.string.gate_status_not_yet
            RecordedGateStatus.NOT_EVALUABLE -> R.string.gate_status_not_evaluable
        },
    )
}
