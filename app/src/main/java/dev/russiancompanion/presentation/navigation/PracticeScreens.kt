package dev.russiancompanion.presentation.navigation

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import dev.russiancompanion.R
import dev.russiancompanion.domain.navigation.AudioOption
import dev.russiancompanion.domain.navigation.NavigationSnapshot
import dev.russiancompanion.domain.navigation.RecordingOption
import dev.russiancompanion.domain.plan.PlanUpdate
import dev.russiancompanion.domain.practice.PRACTICE_NO_SCORE_STATEMENT
import dev.russiancompanion.domain.practice.PracticeAudio
import dev.russiancompanion.domain.practice.PracticeProgress
import dev.russiancompanion.domain.practice.canCompletePractice
import dev.russiancompanion.domain.practice.canStartRecording

@Composable
internal fun PracticeLibraryBody(
    snapshot: NavigationSnapshot,
    onOpen: (String) -> Unit,
    onInspectAttribution: (String) -> Unit,
) {
    if (snapshot.audio.isEmpty()) {
        Text(stringResource(R.string.practice_none))
    }
    snapshot.audio.forEach { model ->
        PracticeLibraryRow(model = model, onOpen = onOpen, onInspectAttribution = onInspectAttribution)
    }
}

@Composable
private fun PracticeLibraryRow(
    model: AudioOption,
    onOpen: (String) -> Unit,
    onInspectAttribution: (String) -> Unit,
) {
    if (model.licenseName.isNotBlank() || model.attribution.isNotBlank()) {
        TextButton(onClick = { onInspectAttribution(model.id) }) {
            Text("${model.licenseName}: ${model.attribution}")
        }
    }
    Text(stringResource(if (model.completed) R.string.plan_completed else R.string.plan_not_completed))
    if (model.hasRecording) {
        Text(stringResource(R.string.practice_recorded))
    }
    if (model.playable) {
        TextButton(onClick = { onOpen(model.id) }) { Text(model.stressMarkedText) }
    } else {
        Text(model.stressMarkedText)
        Text(stringResource(R.string.practice_session_unavailable))
    }
}

@Composable
internal fun PracticeSessionBody(
    snapshot: NavigationSnapshot,
    audioModelId: String,
    practiceAudio: PracticeAudio,
    onSaveRecording: (String, String) -> PlanUpdate,
    onTutorReview: (String, Boolean) -> PlanUpdate,
    onComplete: (String) -> PlanUpdate,
) {
    val model = snapshot.audio.firstOrNull { it.id == audioModelId }
    val latest = snapshot.recordings.lastOrNull { it.audioModelId == audioModelId }
    val context = LocalContext.current
    var microphoneGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) ==
                PackageManager.PERMISSION_GRANTED,
        )
    }
    var microphoneDenied by rememberSaveable(audioModelId) { mutableStateOf(false) }
    val requestMicrophone = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        microphoneGranted = granted
        microphoneDenied = !granted
    }
    var modelPlayed by rememberSaveable(audioModelId) { mutableStateOf(false) }
    var modelReplayed by rememberSaveable(audioModelId) { mutableStateOf(false) }
    var userPlayed by rememberSaveable(audioModelId) { mutableStateOf(false) }
    var recordingNow by rememberSaveable(audioModelId) { mutableStateOf(false) }
    var captureFailed by rememberSaveable(audioModelId) { mutableStateOf(false) }
    var playbackFailed by rememberSaveable(audioModelId) { mutableStateOf(false) }
    val playable = model?.playable == true && !playbackFailed
    val readable = latest?.readable == true
    val completed = latest?.completed == true || model?.completed == true
    val progress = PracticeProgress(
        modelPlayable = playable,
        modelPlayed = modelPlayed,
        hasReadableRecording = readable,
        modelReplayedAfterRecording = modelReplayed,
        userRecordingPlayed = userPlayed,
        microphoneGranted = microphoneGranted,
        alreadyCompleted = completed,
    )
    val recordingNowState = rememberUpdatedState(recordingNow)
    DisposableEffect(audioModelId) {
        onDispose {
            practiceAudio.stopPlayback()
            if (recordingNowState.value) practiceAudio.stopCapture(keep = false)
        }
    }
    Text(PRACTICE_NO_SCORE_STATEMENT)
    if (model == null) {
        Text(stringResource(R.string.practice_session_unavailable))
        return
    }
    Text(model.stressMarkedText)
    if (model.licenseName.isNotBlank() || model.attribution.isNotBlank()) {
        Text("${model.licenseName}: ${model.attribution}")
    }
    Text(stringResource(if (completed) R.string.plan_completed else R.string.plan_not_completed))
    if (!playable) {
        Text(stringResource(R.string.practice_cannot_play))
        return
    }
    if (modelPlayed) {
        Text(stringResource(R.string.practice_played))
    }
    TextButton(
        onClick = {
            practiceAudio.playModel(model.localAudioReference) { succeeded ->
                if (succeeded) {
                    modelPlayed = true
                    if (readable) modelReplayed = true
                } else {
                    playbackFailed = true
                }
            }
        },
    ) {
        Text(stringResource(if (readable) R.string.practice_replay_model else R.string.practice_play_model))
    }
    if (microphoneDenied) {
        Text(stringResource(R.string.practice_mic_denied))
    }
    if (recordingNow) {
        TextButton(
            onClick = {
                val saved = practiceAudio.stopCapture(keep = true)
                recordingNow = false
                if (saved == null || onSaveRecording(model.id, saved) is PlanUpdate.NotSaved) {
                    captureFailed = true
                } else {
                    captureFailed = false
                }
            },
        ) { Text(stringResource(R.string.practice_stop)) }
    } else if (!completed || !readable) {
        TextButton(
            onClick = {
                if (!microphoneGranted) {
                    requestMicrophone.launch(Manifest.permission.RECORD_AUDIO)
                } else if (canStartRecording(progress)) {
                    val started = practiceAudio.startCapture()
                    if (started == null) {
                        captureFailed = true
                    } else {
                        captureFailed = false
                        recordingNow = true
                    }
                }
            },
            enabled = !microphoneGranted || canStartRecording(progress),
        ) { Text(stringResource(R.string.practice_record)) }
    }
    if (captureFailed) {
        Text(stringResource(R.string.practice_not_saved))
    }
    if (latest != null && !readable) {
        Text(stringResource(R.string.practice_recording_unavailable))
    }
    if (readable && latest != null) {
        Text(stringResource(R.string.practice_recorded))
        TextButton(
            onClick = {
                practiceAudio.playRecording(latest.localFileReference) { succeeded ->
                    if (succeeded) userPlayed = true else captureFailed = true
                }
            },
        ) { Text(stringResource(R.string.practice_play_recording)) }
        TextButton(onClick = { onTutorReview(latest.id, !latest.markedForTutorReview) }) {
            Text(
                stringResource(
                    if (latest.markedForTutorReview) R.string.practice_unmark_tutor else R.string.practice_mark_tutor,
                ),
            )
        }
    }
    if (canCompletePractice(progress)) {
        TextButton(onClick = { onComplete(model.id) }) {
            Text(stringResource(R.string.practice_complete))
        }
    }
}
