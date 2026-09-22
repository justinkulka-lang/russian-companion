package dev.russiancompanion.presentation.navigation

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import dev.russiancompanion.R
import dev.russiancompanion.domain.navigation.AudioOption
import dev.russiancompanion.domain.navigation.NavigationSnapshot
import dev.russiancompanion.domain.navigation.RecordingOption
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
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item(key = "practice-header") {
            Text(
                text = stringResource(R.string.practice_title),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.semantics { heading() },
            )
            if (onBack != null) {
                TextButton(onClick = onBack) { Text(stringResource(R.string.navigate_back)) }
            }
            if (onHome != null) {
                TextButton(onClick = onHome) { Text(stringResource(R.string.navigate_home)) }
            }
            if (snapshot.audio.isEmpty()) {
                Text(stringResource(R.string.practice_none))
            }
        }
        items(snapshot.audio, key = { it.id }) { model ->
            PracticeLibraryRow(model = model, onOpen = onOpen, onInspectAttribution = onInspectAttribution)
        }
        item(key = "practice-footer") {
            TextButton(onClick = onTodaysPlan) {
                Text(stringResource(R.string.open_todays_plan_from_practice))
            }
            TextButton(onClick = onAttributions) {
                Text(stringResource(R.string.open_attributions))
            }
        }
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
        onClick = { modelSpeed = 0.75f },
        enabled = !playbackLocked,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp),
    ) {
        Text(
            stringResource(
                if (modelSpeed == 0.75f) R.string.practice_speed_075_selected else R.string.practice_speed_075,
            ),
        )
    }
    TextButton(
        onClick = { modelSpeed = 1.0f },
        enabled = !playbackLocked,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp),
    ) {
        Text(
            stringResource(
                if (modelSpeed == 1.0f) R.string.practice_speed_100_selected else R.string.practice_speed_100,
            ),
        )
    }
    Text(stringResource(R.string.practice_recording_speed, RECORDING_PLAYBACK_SPEED.toString()))
    TextButton(
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
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp),
    ) {
        Text(stringResource(if (readable) R.string.practice_replay_model else R.string.practice_play_model))
    }
    if (microphoneDenied) {
        Text(
            stringResource(R.string.practice_mic_denied),
            modifier = Modifier.semantics { liveRegion = LiveRegionMode.Assertive },
        )
    }
    if (recordingNow) {
        TextButton(
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
        ) { Text(stringResource(R.string.practice_stop)) }
    } else if (!completed || !readable) {
        TextButton(
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
        ) { Text(stringResource(R.string.practice_record)) }
    }
    if (captureFailed) {
        Text(
            stringResource(R.string.practice_not_saved),
            modifier = Modifier.semantics { liveRegion = LiveRegionMode.Assertive },
        )
    }
    if (latest != null && !readable) {
        Text(stringResource(R.string.practice_recording_unavailable))
    }
    if (readable && latest != null) {
        Text(stringResource(R.string.practice_recorded))
        TextButton(
            onClick = {
                abDriver?.stop(AbStopReason.USER_STOP)
                practiceAudio.playRecording(latest.localFileReference) { succeeded ->
                    if (succeeded) userPlayed = true else captureFailed = true
                }
            },
            enabled = !playbackLocked,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp),
        ) { Text(stringResource(R.string.practice_play_recording)) }
        TextButton(
            onClick = { abDriver?.startOnce() },
            enabled = !playbackLocked,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp),
        ) { Text(stringResource(R.string.practice_play_ab)) }
        TextButton(
            onClick = { abDriver?.startLoop() },
            enabled = !playbackLocked,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp),
        ) { Text(stringResource(R.string.practice_repeat_ab)) }
        if (abSession.active) {
            Text(
                stringResource(
                    if (abSession.looping) R.string.practice_ab_looping else R.string.practice_ab_playing,
                ),
            )
            TextButton(
                onClick = { abDriver?.stop(AbStopReason.USER_STOP) },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp),
            ) { Text(stringResource(R.string.practice_stop_loop)) }
        }
        TextButton(
            onClick = { onTutorReview(latest.id, !latest.markedForTutorReview) { } },
            enabled = !operationInProgress,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp),
        ) {
            Text(
                stringResource(
                    if (latest.markedForTutorReview) R.string.practice_unmark_tutor else R.string.practice_mark_tutor,
                ),
            )
        }
        if (latest.markedForTutorReview) {
            TextButton(
                onClick = { onTutorReview(latest.id, false) { } },
                enabled = !operationInProgress,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp),
            ) { Text(stringResource(R.string.practice_clear_tutor)) }
        }
    }
    if (canReplaceTake(progress) && !recordingNow) {
        TextButton(
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
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp),
        ) { Text(stringResource(R.string.practice_replace_take)) }
    }
    if (canCompletePractice(progress)) {
        TextButton(
            onClick = { onComplete(model.id) { } },
            enabled = !operationInProgress,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp),
        ) {
            Text(stringResource(R.string.practice_complete))
        }
    }
}
