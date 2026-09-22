package dev.russiancompanion.presentation.navigation

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import dev.russiancompanion.R
import dev.russiancompanion.domain.navigation.AudioOption
import dev.russiancompanion.domain.navigation.NavigationSnapshot
import dev.russiancompanion.domain.plan.PlanUpdate
import dev.russiancompanion.domain.practice.AbPlaybackDriver
import dev.russiancompanion.domain.practice.AbSession
import dev.russiancompanion.domain.practice.AbStopReason
import dev.russiancompanion.domain.practice.PRACTICE_NO_SCORE_STATEMENT
import dev.russiancompanion.domain.practice.PracticeAudio
import dev.russiancompanion.domain.practice.PracticeProgress
import dev.russiancompanion.domain.practice.RECORDING_PLAYBACK_SPEED
import dev.russiancompanion.domain.practice.canCompletePractice
import dev.russiancompanion.domain.practice.canReplaceTake
import dev.russiancompanion.domain.practice.canStartRecording
import dev.russiancompanion.domain.practice.conflictingPlaybackBlocked

@Composable
internal fun PracticeLibraryBody(
    snapshot: NavigationSnapshot,
    onOpen: (String) -> Unit,
    onInspectAttribution: (String) -> Unit,
    onBack: (() -> Unit)?,
    onHome: (() -> Unit)?,
    onTodaysPlan: () -> Unit,
    onAttributions: () -> Unit,
) {
    ShellPage(
        title = stringResource(R.string.practice_title),
        onBack = onBack,
        onHome = onHome,
        scrollable = false,
    ) {
        if (snapshot.audio.isEmpty()) {
            Text(
                stringResource(R.string.practice_none),
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = true),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(snapshot.audio, key = { it.id }) { model ->
                PracticeLibraryRow(model = model, onOpen = onOpen, onInspectAttribution = onInspectAttribution)
            }
        }
        SecondaryAction(
            text = stringResource(R.string.open_todays_plan_from_practice),
            onClick = onTodaysPlan,
        )
        SecondaryAction(
            text = stringResource(R.string.open_attributions),
            onClick = onAttributions,
        )
    }
}

@Composable
private fun PracticeLibraryRow(
    model: AudioOption,
    onOpen: (String) -> Unit,
    onInspectAttribution: (String) -> Unit,
) {
    if (model.licenseName.isNotBlank() || model.attribution.isNotBlank()) {
        SecondaryAction(
            text = "${model.licenseName}: ${model.attribution}",
            onClick = { onInspectAttribution(model.id) },
        )
    }
    Text(
        stringResource(if (model.completed) R.string.plan_completed else R.string.plan_not_completed),
        color = MaterialTheme.colorScheme.onBackground,
    )
    if (model.hasRecording) {
        Text(
            stringResource(R.string.practice_recorded),
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
    if (model.playable) {
        PrimaryAction(text = model.stressMarkedText, onClick = { onOpen(model.id) })
    } else {
        Text(model.stressMarkedText, color = MaterialTheme.colorScheme.onBackground)
        Text(
            stringResource(R.string.practice_session_unavailable),
            color = MaterialTheme.colorScheme.error,
        )
    }
}

@Composable
internal fun PracticeSessionBody(
    snapshot: NavigationSnapshot,
    audioModelId: String,
    practiceAudio: PracticeAudio,
    operationInProgress: Boolean = false,
    onSaveRecording: (String, String, (PlanUpdate) -> Unit) -> Unit,
    onTutorReview: (String, Boolean, (PlanUpdate) -> Unit) -> Unit,
    onComplete: (String, (PlanUpdate) -> Unit) -> Unit,
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
    var modelSpeed by rememberSaveable(audioModelId) { mutableStateOf(1.0f) }
    var abSession by remember(audioModelId) { mutableStateOf(AbSession()) }
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
    val lifecycleOwner = LocalLifecycleOwner.current
    val modelRef = model?.localAudioReference.orEmpty()
    val recordingRef = latest?.takeIf { it.readable }?.localFileReference.orEmpty()
    val abDriver = remember(audioModelId, modelRef, recordingRef) {
        if (modelRef.isBlank() || recordingRef.isBlank()) {
            null
        } else {
            AbPlaybackDriver(
                audio = practiceAudio,
                modelReference = modelRef,
                recordingReference = recordingRef,
                modelSpeed = { modelSpeed },
                onSession = { next ->
                    abSession = next
                    if (next.lastStopReason == AbStopReason.ERROR) playbackFailed = true
                },
                onModelHeard = {
                    modelPlayed = true
                    modelReplayed = true
                },
                onRecordingHeard = { userPlayed = true },
            )
        }
    }
    DisposableEffect(audioModelId, lifecycleOwner, abDriver) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                abDriver?.stop(AbStopReason.BACKGROUND)
                practiceAudio.releaseForBackground()
                recordingNow = false
                abSession = AbSession()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            abDriver?.stop(AbStopReason.NAVIGATION)
            practiceAudio.stopPlayback()
            if (recordingNowState.value) practiceAudio.stopCapture(keep = false)
            abSession = AbSession()
        }
    }
    val playbackLocked = conflictingPlaybackBlocked(abSession)
    Text(PRACTICE_NO_SCORE_STATEMENT, color = MaterialTheme.colorScheme.onBackground)
    if (model == null) {
        Text(
            stringResource(R.string.practice_session_unavailable),
            color = MaterialTheme.colorScheme.error,
        )
        return
    }
    Text(model.stressMarkedText, color = MaterialTheme.colorScheme.onBackground)
    if (model.licenseName.isNotBlank() || model.attribution.isNotBlank()) {
        Text(
            "${model.licenseName}: ${model.attribution}",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
    Text(
        stringResource(if (completed) R.string.plan_completed else R.string.plan_not_completed),
        color = MaterialTheme.colorScheme.onBackground,
    )
    if (!playable) {
        Text(
            stringResource(R.string.practice_cannot_play),
            color = MaterialTheme.colorScheme.error,
        )
        return
    }
    if (modelPlayed) {
        Text(stringResource(R.string.practice_played), color = MaterialTheme.colorScheme.onBackground)
    }
    SecondaryAction(
        text = stringResource(
            if (modelSpeed == 0.75f) R.string.practice_speed_075_selected else R.string.practice_speed_075,
        ),
        onClick = { modelSpeed = 0.75f },
        enabled = !playbackLocked,
    )
    SecondaryAction(
        text = stringResource(
            if (modelSpeed == 1.0f) R.string.practice_speed_100_selected else R.string.practice_speed_100,
        ),
        onClick = { modelSpeed = 1.0f },
        enabled = !playbackLocked,
    )
    Text(
        stringResource(R.string.practice_recording_speed, RECORDING_PLAYBACK_SPEED.toString()),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    PrimaryAction(
        text = stringResource(if (readable) R.string.practice_replay_model else R.string.practice_play_model),
        onClick = {
            abDriver?.stop(AbStopReason.USER_STOP)
            practiceAudio.playModel(model.localAudioReference, modelSpeed) { succeeded ->
                if (succeeded) {
                    modelPlayed = true
                    if (readable) modelReplayed = true
                } else {
                    playbackFailed = true
                }
            }
        },
        enabled = !playbackLocked,
    )
    if (microphoneDenied) {
        Text(
            stringResource(R.string.practice_mic_denied),
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.semantics { liveRegion = LiveRegionMode.Assertive },
        )
    }
    if (recordingNow) {
        PrimaryAction(
            text = stringResource(R.string.practice_stop),
            onClick = {
                val saved = practiceAudio.stopCapture(keep = true)
                recordingNow = false
                if (saved == null) {
                    captureFailed = true
                } else {
                    onSaveRecording(model.id, saved) { update ->
                        captureFailed = update is PlanUpdate.NotSaved
                    }
                }
            },
            enabled = !operationInProgress,
        )
    } else if (!completed || !readable) {
        PrimaryAction(
            text = stringResource(R.string.practice_record),
            onClick = {
                if (!microphoneGranted) {
                    microphoneDenied = true
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
            enabled = !operationInProgress && (!microphoneGranted || canStartRecording(progress)),
        )
    }
    if (captureFailed) {
        Text(
            stringResource(R.string.practice_not_saved),
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.semantics { liveRegion = LiveRegionMode.Assertive },
        )
    }
    if (latest != null && !readable) {
        Text(
            stringResource(R.string.practice_recording_unavailable),
            color = MaterialTheme.colorScheme.error,
        )
    }
    if (readable && latest != null) {
        Text(stringResource(R.string.practice_recorded), color = MaterialTheme.colorScheme.onBackground)
        SecondaryAction(
            text = stringResource(R.string.practice_play_recording),
            onClick = {
                abDriver?.stop(AbStopReason.USER_STOP)
                practiceAudio.playRecording(latest.localFileReference) { succeeded ->
                    if (succeeded) userPlayed = true else captureFailed = true
                }
            },
            enabled = !playbackLocked,
        )
        SecondaryAction(
            text = stringResource(R.string.practice_play_ab),
            onClick = { abDriver?.startOnce() },
            enabled = !playbackLocked,
        )
        SecondaryAction(
            text = stringResource(R.string.practice_repeat_ab),
            onClick = { abDriver?.startLoop() },
            enabled = !playbackLocked,
        )
        if (abSession.active) {
            Text(
                stringResource(
                    if (abSession.looping) R.string.practice_ab_looping else R.string.practice_ab_playing,
                ),
                color = MaterialTheme.colorScheme.onBackground,
            )
            SecondaryAction(
                text = stringResource(R.string.practice_stop_loop),
                onClick = { abDriver?.stop(AbStopReason.USER_STOP) },
            )
        }
        SecondaryAction(
            text = stringResource(
                if (latest.markedForTutorReview) R.string.practice_unmark_tutor else R.string.practice_mark_tutor,
            ),
            onClick = { onTutorReview(latest.id, !latest.markedForTutorReview) { } },
            enabled = !operationInProgress,
        )
        if (latest.markedForTutorReview) {
            SecondaryAction(
                text = stringResource(R.string.practice_clear_tutor),
                onClick = { onTutorReview(latest.id, false) { } },
                enabled = !operationInProgress,
            )
        }
    }
    if (canReplaceTake(progress) && !recordingNow) {
        SecondaryAction(
            text = stringResource(R.string.practice_replace_take),
            onClick = {
                if (canStartRecording(progress.copy(alreadyCompleted = false, hasReadableRecording = false)) ||
                    canReplaceTake(progress)
                ) {
                    val started = practiceAudio.startCapture()
                    if (started == null) {
                        captureFailed = true
                    } else {
                        captureFailed = false
                        recordingNow = true
                    }
                }
            },
            enabled = !operationInProgress,
        )
    }
    if (canCompletePractice(progress)) {
        PrimaryAction(
            text = stringResource(R.string.practice_complete),
            onClick = { onComplete(model.id) { } },
            enabled = !operationInProgress,
        )
    }
}
