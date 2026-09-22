package dev.russiancompanion.presentation.navigation

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
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
import dev.russiancompanion.domain.progress.categoryHasRecordedActivity

@Composable
internal fun HomePlanSection(
    snapshot: NavigationSnapshot,
    appearance: AppearancePreference = AppearancePreference.DARK,
    appearanceSaveFailed: Boolean = false,
    onAppearanceChange: (AppearancePreference) -> Unit = {},
    onContinue: () -> Unit = {},
    onBegin: (ChecklistItem) -> Unit,
    onTodaysPlan: () -> Unit,
    onSelectLesson: () -> Unit,
) {
    val lesson = snapshot.activeLessonReference
    if (lesson != null) {
        Text(
            stringResource(R.string.home_current_lesson, lesson),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
    ContinueCard(
        snapshot = snapshot,
        onContinue = onContinue,
        onSelectLesson = onSelectLesson,
    )
    HomeBackupSummary(snapshot)
    HomeHealthSummary(snapshot)
    AppearanceControls(
        appearance = appearance,
        saveFailed = appearanceSaveFailed,
        onAppearanceChange = onAppearanceChange,
    )
}

@Composable
private fun AppearanceControls(
    appearance: AppearancePreference,
    saveFailed: Boolean = false,
    onAppearanceChange: (AppearancePreference) -> Unit,
) {
    Text(
        stringResource(R.string.appearance_title),
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.semantics { heading() },
    )
    SecondaryAction(
        text = stringResource(
            if (appearance == AppearancePreference.DARK) {
                R.string.appearance_dark_selected
            } else {
                R.string.appearance_dark
            },
        ),
        onClick = { onAppearanceChange(AppearancePreference.DARK) },
    )
    SecondaryAction(
        text = stringResource(
            if (appearance == AppearancePreference.FOLLOW_SYSTEM) {
                R.string.appearance_follow_system_selected
            } else {
                R.string.appearance_follow_system
            },
        ),
        onClick = { onAppearanceChange(AppearancePreference.FOLLOW_SYSTEM) },
    )
    if (saveFailed) {
        Text(
            stringResource(R.string.appearance_save_failed),
            color = MaterialTheme.colorScheme.error,
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
    Text(
        stringResource(R.string.continue_card_title),
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.semantics { heading() },
    )
    if (!snapshot.localDataOpen || snapshot.seedCorrupt) {
        Text(
            stringResource(R.string.continue_unavailable),
            color = MaterialTheme.colorScheme.error,
        )
        return
    }
    if (!activeLessonHasPlan(snapshot)) {
        Text(
            stringResource(R.string.home_no_plan),
            color = MaterialTheme.colorScheme.onBackground,
        )
        PrimaryAction(
            text = stringResource(R.string.home_select_lesson),
            onClick = onSelectLesson,
        )
        return
    }
    val plan = snapshot.dailyPlan
    if (!plan.requiredWorkPrescribed) {
        Text(
            stringResource(R.string.plan_no_work),
            color = MaterialTheme.colorScheme.onBackground,
        )
        return
    }
    snapshot.activeLessonReference?.let { lesson ->
        Text(
            stringResource(R.string.continue_lesson, lesson),
            color = MaterialTheme.colorScheme.onBackground,
        )
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
    PrimaryAction(
        text = stringResource(R.string.continue_action),
        onClick = onContinue,
        enabled = !unavailable,
    )
}

@Composable
internal fun TodaysPlanBody(
    snapshot: NavigationSnapshot,
    saveFailed: Boolean,
    focusPlanDefinitionId: String? = null,
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
        Text(
            stringResource(R.string.home_no_plan),
            color = MaterialTheme.colorScheme.onBackground,
        )
        PrimaryAction(text = stringResource(R.string.home_select_lesson), onClick = onSelectLesson)
        return
    }
    val plan = snapshot.dailyPlan
    if (!plan.requiredWorkPrescribed) {
        Text(stringResource(R.string.plan_no_work))
    }
    val focused = plan.items.firstOrNull { it.planDefinitionId == focusPlanDefinitionId }
    val remaining = if (focused == null) plan.items else plan.items.filter { it.planDefinitionId != focused.planDefinitionId }
    if (focused != null) {
        Text(
            stringResource(
                when (focused.activity) {
                    ChecklistActivity.BOOK -> R.string.plan_continue_focus_book
                    ChecklistActivity.ANKI_DROID -> R.string.plan_continue_focus_anki
                    ChecklistActivity.LISTEN_RECORD_COMPARE -> R.string.plan_continue_focus_listen
                    ChecklistActivity.TUTOR -> R.string.plan_continue_focus_book
                },
            ),
        )
        ChecklistRow(
            item = focused,
            practiceAvailable = practicePlayable(snapshot, focused.audioModelIds),
            admittedAudio = snapshot.audio.isNotEmpty(),
            onStartPractice = { onStartPractice(focused) },
            onComplete = onComplete,
            onRemoveTutor = onRemoveTutor,
            actionsEnabled = actionsEnabled,
        )
    }
    remaining.forEach { item ->
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
        Text(item.instruction, color = MaterialTheme.colorScheme.onBackground)
        SecondaryAction(
            text = stringResource(R.string.plan_add_tutor),
            onClick = { onIncludeTutor(item.planDefinitionId) },
            enabled = actionsEnabled,
        )
    }
    if (saveFailed) {
        Text(
            stringResource(R.string.plan_not_saved),
            color = MaterialTheme.colorScheme.error,
        )
        PrimaryAction(
            text = stringResource(R.string.navigate_retry),
            onClick = onRetrySave,
            enabled = actionsEnabled,
        )
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
        PrimaryAction(
            text = stringResource(R.string.plan_mark_complete),
            onClick = { onComplete(item.planDefinitionId) },
            enabled = actionsEnabled,
        )
    }
    if (item.activity == ChecklistActivity.LISTEN_RECORD_COMPARE) {
        PrimaryAction(
            text = stringResource(R.string.plan_start_practice),
            onClick = onStartPractice,
            enabled = practiceAvailable && actionsEnabled,
        )
        if (!practiceAvailable) {
            Text(
                stringResource(
                    if (admittedAudio) R.string.plan_prescribed_audio_unavailable else R.string.practice_none,
                ),
                color = MaterialTheme.colorScheme.error,
            )
        }
    }
    if (item.activity == ChecklistActivity.TUTOR) {
        SecondaryAction(
            text = stringResource(R.string.plan_remove_tutor),
            onClick = { onRemoveTutor(item.planDefinitionId) },
            enabled = actionsEnabled,
        )
    }
}

@Composable
private fun HomeBackupSummary(snapshot: NavigationSnapshot) {
    val backup = snapshot.backup
    Text(
        when (backup.outcome) {
            BackupOutcome.NEVER_EXPORTED -> stringResource(R.string.backup_summary_never)
            BackupOutcome.COMPLETED -> stringResource(
                R.string.backup_summary_completed,
                backup.lastSuccessfulAt ?: stringResource(R.string.backup_summary_time_unavailable),
            )
            BackupOutcome.FAILED -> stringResource(R.string.backup_summary_failed)
            BackupOutcome.NOT_CONFIRMED -> stringResource(R.string.backup_summary_not_confirmed)
        },
    )
}

@Composable
private fun HomeHealthSummary(snapshot: NavigationSnapshot) {
    val health = snapshot.storageHealth
    Text(
        when (health.databaseReadable) {
            true -> stringResource(
                R.string.health_summary_ok,
                health.userRecordingCount ?: 0,
            )
            false -> stringResource(R.string.health_summary_unreadable)
            null -> stringResource(R.string.health_summary_unavailable)
        },
    )
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
