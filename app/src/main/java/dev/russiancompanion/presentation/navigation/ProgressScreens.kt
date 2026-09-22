package dev.russiancompanion.presentation.navigation

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import dev.russiancompanion.R
import dev.russiancompanion.domain.navigation.NavigationSnapshot
import dev.russiancompanion.domain.plan.PlanUpdate
import dev.russiancompanion.domain.plan.RecordedCategory
import dev.russiancompanion.domain.progress.ExportUpdate
import dev.russiancompanion.domain.progress.PROGRESS_ACTIVITY_ONLY_STATEMENT
import dev.russiancompanion.domain.progress.activityAttestationLabel
import dev.russiancompanion.domain.progress.categoryShowsImbalance
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
            Text(PROGRESS_ACTIVITY_ONLY_STATEMENT)
            Text(
                stringResource(
                    R.string.progress_cumulative,
                    progress.totalMinutes.toInt(),
                    progress.totalCompletions,
                ),
            )
            for (balance in progress.categories) {
                Text(recordedWorkLabel(balance.category, balance.recordedMinutes, balance.completionCount))
                if (categoryShowsImbalance(balance)) {
                    Text(stringResource(R.string.progress_imbalance))
                }
            }
            if (progress.totalMinutes == 0L && progress.totalCompletions == 0) {
                Text(stringResource(R.string.home_no_activity))
            }
            BackupStatusSection(snapshot)
            StorageHealthSection(snapshot)
            StudyBalanceSection(snapshot)
            for (log in progress.recentLogs) {
                TextButton(onClick = { selectedLogId = log.id }) {
                    Text(stringResource(R.string.progress_log_entry, categoryLabel(log.category), log.recordedAtLocal))
                }
            }
            val selected = progress.recentLogs.firstOrNull { it.id == selectedLogId }
            if (selected != null) {
                Text(categoryLabel(selected.category))
                if (selected.completed && selected.minutes == 0) {
                    Text(stringResource(R.string.progress_completed_zero))
                } else {
                    Text(stringResource(R.string.progress_log_minutes, selected.minutes))
                    Text(
                        stringResource(
                            if (selected.completed) R.string.plan_completed else R.string.plan_not_completed,
                        ),
                    )
                }
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
    operationInProgress: Boolean = false,
    onSave: (RecordedCategory, Int, Boolean, String, Boolean, (PlanUpdate) -> Unit) -> Unit,
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
    ShellPage(title = stringResource(R.string.log_activity_title), onBack = onProgress, onHome = onHome, busy = operationInProgress) {
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
            RecordedCategory.GRAMMAR -> Text(stringResource(R.string.log_book_practice))
            RecordedCategory.VOCABULARY_AND_FORMS -> Text(stringResource(R.string.log_anki_practice))
            else -> Unit
        }
        OutlinedTextField(
            value = durationText,
            onValueChange = { next -> durationText = next.filter { it.isDigit() } },
            label = { Text(stringResource(R.string.log_duration_label)) },
        )
        if (durationText.isBlank()) {
            Text(
                stringResource(R.string.log_duration_required),
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Assertive },
            )
        } else if (!durationAccepted) {
            Text(
                stringResource(R.string.log_duration_invalid),
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Assertive },
            )
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
            Text(
                stringResource(R.string.plan_not_saved),
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Assertive },
            )
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
                enabled = !operationInProgress,
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
            enabled = !operationInProgress && selected != null && durationAccepted,
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
    onSave: (RecordedCategory, Int, Boolean, String, Boolean, (PlanUpdate) -> Unit) -> Unit,
    onFailed: () -> Unit,
    onProgress: () -> Unit,
) {
    val category = selected ?: return
    val duration = minutes ?: return
    if (!durationAccepted) return
    onSave(category, duration, completed, recorded, associate) { update ->
        if (update is PlanUpdate.Applied || update is PlanUpdate.Unchanged) {
            onProgress()
        } else {
            onFailed()
        }
    }
}

internal class ExportSelection(
    val fileName: String?,
    val documentReference: String?,
    val open: () -> OutputStream,
)

@Composable
internal fun LocalExportScreen(
    operationInProgress: Boolean = false,
    onExport: (OutputStream, String?, String?, (ExportUpdate) -> Unit) -> Unit,
    chooseExportDestination: (() -> ExportSelection?)?,
    onProgress: () -> Unit,
    onHome: (() -> Unit)?,
) {
    var status by remember { mutableStateOf<ExportUpdate?>(null) }
    var exporting by remember { mutableStateOf(false) }
    val context = LocalContext.current
    fun finishExport(stream: OutputStream, result: ExportUpdate) {
        try {
            stream.close()
        } catch (_: Exception) {
            // Destination close is best-effort after the export result is known.
        }
        exporting = false
        status = result
    }
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
            exporting = true
            onExport(stream, visibleExportFileName(context, uri), uri.toString()) { result ->
                finishExport(stream, result)
            }
        }
    }
    val busy = operationInProgress || exporting
    ShellPage(title = stringResource(R.string.local_export_title), onBack = onProgress, onHome = onHome) {
        Text(stringResource(R.string.local_export_scope))
        if (busy) {
            Text(
                stringResource(R.string.work_in_progress),
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
            )
        }
        TextButton(
            onClick = {
                if (busy) return@TextButton
                val choose = chooseExportDestination
                if (choose != null) {
                    val selection = choose()
                    if (selection == null) {
                        status = ExportUpdate.DestinationUnavailable
                    } else {
                        exporting = true
                        val stream = selection.open()
                        onExport(stream, selection.fileName, selection.documentReference) { result ->
                            finishExport(stream, result)
                        }
                    }
                } else {
                    launcher.launch("russian-companion-export.zip")
                }
            },
            enabled = !busy,
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

private fun visibleExportFileName(context: Context, uri: Uri): String? {
    return try {
        context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
            if (!cursor.moveToFirst()) return@use null
            val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (index < 0) null else cursor.getString(index)?.trim()?.takeIf { it.isNotEmpty() }
        }
    } catch (_: Exception) {
        null
    }
}
