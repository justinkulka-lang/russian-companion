package dev.russiancompanion.presentation.navigation

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import dev.russiancompanion.R
import dev.russiancompanion.domain.appearance.AppearancePreference
import dev.russiancompanion.domain.navigation.ContinueDestination
import dev.russiancompanion.domain.navigation.NavigationSnapshot
import dev.russiancompanion.domain.navigation.activeLessonHasPlan
import dev.russiancompanion.domain.navigation.practicePlayable
import dev.russiancompanion.domain.navigation.resolveContinueDestination
import dev.russiancompanion.domain.plan.ChecklistActivity
import dev.russiancompanion.domain.plan.ChecklistItem
import dev.russiancompanion.domain.plan.RecordedCategory
import dev.russiancompanion.domain.plan.canMarkComplete
import dev.russiancompanion.domain.progress.BackupOutcome
import dev.russiancompanion.domain.progress.PROGRESS_ACTIVITY_ONLY_STATEMENT
import dev.russiancompanion.domain.progress.categoryHasRecordedActivity

@Composable
internal fun HomePlanSection(
    snapshot: NavigationSnapshot,
    appearance: AppearancePreference = AppearancePreference.DARK,
    onAppearanceChange: (AppearancePreference) -> Unit = {},
    onContinue: () -> Unit = {},
    onBegin: (ChecklistItem) -> Unit,
    onTodaysPlan: () -> Unit,
    onSelectLesson: () -> Unit,
) {
    val lesson = snapshot.activeLessonReference
    if (lesson != null) {
        Text(stringResource(R.string.home_current_lesson, lesson))
    }
    AppearanceControls(appearance = appearance, onAppearanceChange = onAppearanceChange)
    ContinueCard(
        snapshot = snapshot,
        onContinue = onContinue,
        onSelectLesson = onSelectLesson,
    )
    if (activeLessonHasPlan(snapshot)) {
        TextButton(onClick = onTodaysPlan) { Text(stringResource(R.string.open_todays_plan)) }
    }
    GateSummarySection(snapshot)
    ActivitySummarySection(snapshot)
    BackupStatusSection(snapshot)
    StorageHealthSection(snapshot)
    StudyBalanceSection(snapshot)
}

@Composable
private fun AppearanceControls(
    appearance: AppearancePreference,
    onAppearanceChange: (AppearancePreference) -> Unit,
) {
    Text(stringResource(R.string.appearance_title), modifier = Modifier.semantics { heading() })
    TextButton(
        onClick = { onAppearanceChange(AppearancePreference.DARK) },
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp),
    ) {
        Text(
            stringResource(
                if (appearance == AppearancePreference.DARK) {
                    R.string.appearance_dark_selected
                } else {
                    R.string.appearance_dark
                },
            ),
        )
    }
    TextButton(
        onClick = { onAppearanceChange(AppearancePreference.FOLLOW_SYSTEM) },
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp),
    ) {
        Text(
            stringResource(
                if (appearance == AppearancePreference.FOLLOW_SYSTEM) {
                    R.string.appearance_follow_system_selected
                } else {
                    R.string.appearance_follow_system
                },
            ),
        )
    }
}

@Composable
private fun ContinueCard(
    snapshot: NavigationSnapshot,
    onContinue: () -> Unit,
    onSelectLesson: () -> Unit,
) {
    val destination = resolveContinueDestination(snapshot)
    Text(stringResource(R.string.continue_card_title), modifier = Modifier.semantics { heading() })
    if (!snapshot.localDataOpen || snapshot.seedCorrupt) {
        Text(stringResource(R.string.continue_unavailable))
        return
    }
    if (!activeLessonHasPlan(snapshot)) {
        Text(stringResource(R.string.home_no_plan))
        TextButton(
            onClick = onSelectLesson,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp),
        ) { Text(stringResource(R.string.home_select_lesson)) }
        return
    }
    val plan = snapshot.dailyPlan
    if (!plan.requiredWorkPrescribed) {
        Text(stringResource(R.string.plan_no_work))
        return
    }
    snapshot.activeLessonReference?.let { lesson ->
        Text(stringResource(R.string.continue_lesson, lesson))
    }
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
    }
    val unavailable = destination == ContinueDestination.Unavailable
    if (unavailable) {
        Text(stringResource(R.string.continue_unavailable))
        if (next?.activity == ChecklistActivity.LISTEN_RECORD_COMPARE) {
            Text(
                stringResource(
                    if (snapshot.audio.isEmpty()) {
                        R.string.practice_none
                    } else {
                        R.string.plan_prescribed_audio_unavailable
                    },
                ),
            )
        }
    }
    TextButton(
        onClick = onContinue,
        enabled = !unavailable,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp),
    ) {
        Text(stringResource(R.string.continue_action))
    }
}

@Composable
internal fun TodaysPlanBody(
    snapshot: NavigationSnapshot,
    saveFailed: Boolean,
    onSelectLesson: () -> Unit,
    onStartPractice: (ChecklistItem) -> Unit,
    onComplete: (String) -> Unit,
    onIncludeTutor: (String) -> Unit,
    onRemoveTutor: (String) -> Unit,
    onRetrySave: () -> Unit,
    actionsEnabled: Boolean = true,
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
            practiceAvailable = practicePlayable(snapshot, item.audioModelIds),
            admittedAudio = snapshot.audio.isNotEmpty(),
            onStartPractice = { onStartPractice(item) },
            onComplete = onComplete,
            onRemoveTutor = onRemoveTutor,
            actionsEnabled = actionsEnabled,
        )
    }
    plan.addableTutorTasks.forEach { item ->
        Text(item.instruction)
        TextButton(
            onClick = { onIncludeTutor(item.planDefinitionId) },
            enabled = actionsEnabled,
        ) {
            Text(stringResource(R.string.plan_add_tutor))
        }
    }
    if (saveFailed) {
        Text(stringResource(R.string.plan_not_saved))
        TextButton(onClick = onRetrySave, enabled = actionsEnabled) { Text(stringResource(R.string.navigate_retry)) }
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
    actionsEnabled: Boolean = true,
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
    val lessonNumber = item.lessonNumber
    if (lessonNumber != null && (item.activity == ChecklistActivity.BOOK || item.activity == ChecklistActivity.ANKI_DROID)) {
        Text(stringResource(R.string.plan_lesson_number, lessonNumber))
    }
    if (item.activity == ChecklistActivity.LISTEN_RECORD_COMPARE) {
        Text(stringResource(R.string.plan_prescribed_audio_count, item.audioModelIds.size))
    }
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
        TextButton(onClick = { onComplete(item.planDefinitionId) }, enabled = actionsEnabled) {
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
                    if (admittedAudio) R.string.plan_prescribed_audio_unavailable else R.string.practice_none,
                ),
            )
        }
    }
    if (item.activity == ChecklistActivity.TUTOR) {
        TextButton(onClick = { onRemoveTutor(item.planDefinitionId) }, enabled = actionsEnabled) {
            Text(stringResource(R.string.plan_remove_tutor))
        }
    }
}

@Composable
private fun GateSummarySection(snapshot: NavigationSnapshot) {
    Text(stringResource(R.string.gates_not_proficiency))
    if (snapshot.gateDetails.isEmpty()) {
        Text(stringResource(R.string.gates_none))
        return
    }
    snapshot.gateDetails.forEach { detail ->
        GateStatusLine(detail.recordedStatus)
        GateLockedRemainder(detail)
    }
}

@Composable
private fun ActivitySummarySection(snapshot: NavigationSnapshot) {
    if (!snapshot.progress.readable) {
        Text(stringResource(R.string.local_data_message))
        return
    }
    Text(PROGRESS_ACTIVITY_ONLY_STATEMENT)
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
internal fun BackupStatusSection(snapshot: NavigationSnapshot) {
    val backup = snapshot.backup
    Text(stringResource(R.string.backup_title), modifier = Modifier.semantics { heading() })
    when (backup.outcome) {
        BackupOutcome.NEVER_EXPORTED -> Text(stringResource(R.string.backup_never))
        BackupOutcome.COMPLETED -> {
            Text(stringResource(R.string.backup_completed))
            backup.lastSuccessfulAt?.let { Text(stringResource(R.string.backup_last_success, it)) }
            backup.lastSuccessfulFileName?.let { Text(stringResource(R.string.backup_filename, it)) }
        }
        BackupOutcome.FAILED -> Text(stringResource(R.string.backup_failed))
        BackupOutcome.NOT_CONFIRMED -> Text(stringResource(R.string.backup_not_confirmed))
    }
    if (backup.successfulExportExists && backup.outcome != BackupOutcome.COMPLETED) {
        backup.lastSuccessfulAt?.let { Text(stringResource(R.string.backup_last_success, it)) }
        backup.lastSuccessfulFileName?.let { Text(stringResource(R.string.backup_filename, it)) }
    }
    if (backup.logCount != null) {
        Text(
            stringResource(
                R.string.backup_counts,
                backup.logCount,
                backup.evidenceCount ?: 0,
                backup.progressCount ?: 0,
                backup.recordingCount ?: 0,
            ),
        )
    }
    when (backup.expectedRecordingsIncluded) {
        true -> Text(stringResource(R.string.backup_recordings_included))
        false -> Text(stringResource(R.string.backup_recordings_missing))
        null -> Unit
    }
}

@Composable
internal fun StorageHealthSection(snapshot: NavigationSnapshot) {
    val health = snapshot.storageHealth
    Text(stringResource(R.string.health_title), modifier = Modifier.semantics { heading() })
    Text(
        stringResource(
            when (health.databaseReadable) {
                true -> R.string.health_database_readable
                false -> R.string.health_database_unreadable
                null -> R.string.health_database_unavailable
            },
        ),
    )
    Text(
        stringResource(
            when (health.catalogAvailable) {
                true -> R.string.health_catalog_available
                false -> R.string.health_catalog_unavailable
                null -> R.string.health_catalog_unknown
            },
        ),
    )
    Text(
        health.packagedAudioCount?.let { stringResource(R.string.health_packaged_audio, it) }
            ?: stringResource(R.string.health_packaged_audio_unavailable),
    )
    Text(
        health.userRecordingCount?.let { stringResource(R.string.health_user_recordings, it) }
            ?: stringResource(R.string.health_user_recordings_unavailable),
    )
    Text(
        health.recordingBytes?.let { stringResource(R.string.health_recording_bytes, it) }
            ?: stringResource(R.string.health_recording_bytes_unavailable),
    )
    Text(
        health.missingRecordingCount?.let { stringResource(R.string.health_missing_recordings, it) }
            ?: stringResource(R.string.health_missing_recordings_unavailable),
    )
    Text(
        health.availableBytes?.let { stringResource(R.string.health_available_bytes, it) }
            ?: stringResource(R.string.health_available_bytes_unavailable),
    )
    Text(
        health.applicationVersion?.let { stringResource(R.string.health_app_version, it) }
            ?: stringResource(R.string.health_app_version_unavailable),
    )
    Text(
        health.bundledContentIdentity?.let { stringResource(R.string.health_content_identity, it) }
            ?: stringResource(R.string.health_content_identity_unavailable),
    )
}

@Composable
internal fun StudyBalanceSection(snapshot: NavigationSnapshot) {
    Text(stringResource(R.string.balance_title), modifier = Modifier.semantics { heading() })
    WindowBalanceBlock(title = stringResource(R.string.balance_seven_day), snapshot.sevenDayBalance)
    WindowBalanceBlock(title = stringResource(R.string.balance_thirty_day), snapshot.thirtyDayBalance)
}

@Composable
private fun WindowBalanceBlock(title: String, window: dev.russiancompanion.domain.progress.WindowBalance) {
    Text(title)
    if (!window.readable) {
        Text(stringResource(R.string.balance_unreadable))
        return
    }
    if (window.categories.isEmpty()) {
        return
    }
    for (category in window.categories) {
        Text(
            stringResource(
                R.string.balance_category_line,
                categoryLabel(category.category),
                category.completionCount,
                category.recordedMinutes.toInt(),
            ),
        )
        if (categoryHasRecordedActivity(category)) {
            Text(stringResource(R.string.balance_recorded_category, categoryLabel(category.category)))
        } else {
            Text(stringResource(R.string.balance_unrecorded_category, categoryLabel(category.category)))
        }
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
