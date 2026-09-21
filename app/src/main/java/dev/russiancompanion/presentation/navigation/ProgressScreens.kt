package dev.russiancompanion.presentation.navigation

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import dev.russiancompanion.R
import dev.russiancompanion.domain.navigation.NavigationSnapshot
import dev.russiancompanion.domain.plan.PlanUpdate
import dev.russiancompanion.domain.plan.RecordedCategory
import dev.russiancompanion.domain.progress.ExportUpdate
import dev.russiancompanion.domain.progress.activityAttestationLabel
import java.io.OutputStream

@Composable
internal fun ProgressScreen(
    snapshot: NavigationSnapshot,
    onLogActivity: () -> Unit,
    onExport: () -> Unit,
    onHome: (() -> Unit)?,
    onGates: () -> Unit,
    onBack: () -> Unit,
) {
    var selectedLogId by remember { mutableStateOf<String?>(null) }
    val progress = snapshot.progress
    ShellPage(title = stringResource(R.string.progress_title), onBack = onBack, onHome = onHome) {
        if (!progress.readable) {
            Text(stringResource(R.string.local_data_message))
        } else {
            Text(stringResource(R.string.progress_activity_only))
            Text(
                stringResource(
                    R.string.progress_cumulative,
                    progress.totalMinutes.toInt(),
                    progress.totalCompletions,
                ),
            )
            for (balance in progress.categories) {
                Text(
                    stringResource(
                        R.string.home_activity_total,
                        categoryLabel(balance.category),
                        balance.recordedMinutes.toInt(),
                        balance.completionCount,
                    ),
                )
                if (balance.recordedMinutes == 0L && balance.completionCount == 0) {
                    Text(stringResource(R.string.progress_imbalance))
                }
            }
            if (progress.totalMinutes == 0L && progress.totalCompletions == 0) {
                Text(stringResource(R.string.home_no_activity))
            }
            for (log in progress.recentLogs) {
                TextButton(onClick = { selectedLogId = log.id }) {
                    Text(stringResource(R.string.progress_log_entry, categoryLabel(log.category), log.recordedAtLocal))
                }
            }
            val selected = progress.recentLogs.firstOrNull { it.id == selectedLogId }
            if (selected != null) {
                Text(categoryLabel(selected.category))
                Text(stringResource(R.string.progress_log_minutes, selected.minutes))
                Text(
                    stringResource(
                        if (selected.completed) R.string.plan_completed else R.string.plan_not_completed,
                    ),
                )
                Text(stringResource(R.string.log_recorded_at, selected.recordedAtLocal))
                val lesson = snapshot.lessons.firstOrNull { it.id == selected.segmentId }
                if (lesson != null) {
                    Text(stringResource(R.string.progress_lesson, lesson.lessonReference))
                }
                val attestation = activityAttestationLabel(selected.category, selected.selfAttested)
                if (attestation.isNotBlank()) {
                    Text(attestation)
                }
                TextButton(onClick = { selectedLogId = null }) {
                    Text(stringResource(R.string.progress_close_log))
                }
            }
        }
        TextButton(onClick = onLogActivity) { Text(stringResource(R.string.log_activity_title)) }
        TextButton(onClick = onExport) { Text(stringResource(R.string.local_export_title)) }
        TextButton(onClick = onGates) { Text(stringResource(R.string.open_gates)) }
    }
}

@Composable
internal fun LogActivityScreen(
    snapshot: NavigationSnapshot,
    fromPlan: Boolean,
    recordedAt: () -> String,
    onSave: (RecordedCategory, Int, Boolean, String, Boolean) -> PlanUpdate,
    onProgress: () -> Unit,
    onTodaysPlan: () -> Unit,
    onHome: (() -> Unit)?,
) {
    var selected by remember { mutableStateOf<RecordedCategory?>(null) }
    var durationText by remember { mutableStateOf("") }
    var completed by remember { mutableStateOf(false) }
    var associate by remember { mutableStateOf(false) }
    var failed by remember { mutableStateOf(false) }
    val recorded = remember { recordedAt() }
    val minutes = durationText.toIntOrNull()
    val durationAccepted = durationText.matches(Regex("[0-9]+")) && minutes != null
    ShellPage(title = stringResource(R.string.log_activity_title), onBack = onProgress, onHome = onHome) {
        Text(stringResource(R.string.log_recorded_at, recorded))
        if (selected == null) {
            Text(stringResource(R.string.log_category_required))
        }
        for (category in RecordedCategory.entries) {
            TextButton(onClick = { selected = category }) {
                Text(categoryLabel(category))
            }
        }
        when (selected) {
            RecordedCategory.GRAMMAR -> Text(stringResource(R.string.gate_book_attestation))
            RecordedCategory.VOCABULARY_AND_FORMS -> Text(stringResource(R.string.gate_anki_attestation))
            else -> Unit
        }
        OutlinedTextField(
            value = durationText,
            onValueChange = { next -> durationText = next.filter { it.isDigit() } },
            label = { Text(stringResource(R.string.log_duration_label)) },
        )
        if (durationText.isBlank()) {
            Text(stringResource(R.string.log_duration_required))
        } else if (!durationAccepted) {
            Text(stringResource(R.string.log_duration_invalid))
        }
        TextButton(onClick = { completed = !completed }) {
            Text(stringResource(R.string.log_mark_completed))
        }
        if (completed) {
            Text(stringResource(R.string.plan_completed))
        }
        if (snapshot.activeSegmentId != null) {
            TextButton(onClick = { associate = !associate }) {
                Text(stringResource(R.string.log_associate_lesson))
            }
        }
        if (fromPlan) {
            TextButton(onClick = onTodaysPlan) { Text(stringResource(R.string.open_todays_plan)) }
        }
        if (failed) {
            Text(stringResource(R.string.plan_not_saved))
            TextButton(
                onClick = {
                    saveLog(
                        selected = selected,
                        minutes = minutes,
                        durationAccepted = durationAccepted,
                        completed = completed,
                        recorded = recorded,
                        associate = associate && snapshot.activeSegmentId != null,
                        onSave = onSave,
                        onFailed = { failed = true },
                        onProgress = onProgress,
                    )
                },
            ) { Text(stringResource(R.string.navigate_retry)) }
        }
        TextButton(
            onClick = {
                saveLog(
                    selected = selected,
                    minutes = minutes,
                    durationAccepted = durationAccepted,
                    completed = completed,
                    recorded = recorded,
                    associate = associate && snapshot.activeSegmentId != null,
                    onSave = onSave,
                    onFailed = { failed = true },
                    onProgress = onProgress,
                )
            },
            enabled = selected != null && durationAccepted,
        ) { Text(stringResource(R.string.log_save)) }
    }
}

private fun saveLog(
    selected: RecordedCategory?,
    minutes: Int?,
    durationAccepted: Boolean,
    completed: Boolean,
    recorded: String,
    associate: Boolean,
    onSave: (RecordedCategory, Int, Boolean, String, Boolean) -> PlanUpdate,
    onFailed: () -> Unit,
    onProgress: () -> Unit,
) {
    val category = selected ?: return
    val duration = minutes ?: return
    if (!durationAccepted) return
    val update = onSave(category, duration, completed, recorded, associate)
    if (update is PlanUpdate.Applied || update is PlanUpdate.Unchanged) {
        onProgress()
    } else {
        onFailed()
    }
}

@Composable
internal fun LocalExportScreen(
    onExport: (OutputStream) -> ExportUpdate,
    chooseExportDestination: (((OutputStream) -> Unit) -> Unit)?,
    onProgress: () -> Unit,
    onHome: (() -> Unit)?,
) {
    var status by remember { mutableStateOf<ExportUpdate?>(null) }
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/zip")) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        val stream = try {
            context.contentResolver.openOutputStream(uri)
        } catch (_: Exception) {
            null
        }
        if (stream == null) {
            status = ExportUpdate.DestinationUnavailable
        } else {
            stream.use { status = onExport(it) }
        }
    }
    ShellPage(title = stringResource(R.string.local_export_title), onBack = onProgress, onHome = onHome) {
        Text(stringResource(R.string.local_export_scope))
        TextButton(
            onClick = {
                val opener = chooseExportDestination
                if (opener != null) {
                    opener { stream -> stream.use { status = onExport(it) } }
                } else {
                    launcher.launch("russian-companion-export.zip")
                }
            },
        ) { Text(stringResource(R.string.local_export_choose)) }
        when (val current = status) {
            is ExportUpdate.Completed -> {
                Text(stringResource(R.string.export_completed))
                if (current.empty) {
                    Text(stringResource(R.string.export_empty))
                }
            }
            is ExportUpdate.RecordingUnreadable -> {
                Text(stringResource(R.string.export_failed))
                Text(stringResource(R.string.export_unusable))
                Text(stringResource(R.string.export_recording_unreadable, current.recordingId))
            }
            ExportUpdate.DestinationUnavailable -> {
                Text(stringResource(R.string.export_failed))
                Text(stringResource(R.string.export_unusable))
                Text(stringResource(R.string.export_destination_unavailable))
            }
            ExportUpdate.NotConfirmed -> {
                Text(stringResource(R.string.export_failed))
                Text(stringResource(R.string.export_unusable))
                Text(stringResource(R.string.export_not_confirmed))
            }
            null -> Unit
        }
    }
}
