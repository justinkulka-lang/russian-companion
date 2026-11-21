package dev.russiancompanion.v2.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import dev.russiancompanion.domain.practice.PracticeAudio
import dev.russiancompanion.v2.audio.V2ClipSession

@Composable
internal fun V2ListenPanel(
    modelReference: String?,
    audio: PracticeAudio?,
) {
    if (modelReference == null || audio == null || !audio.modelPlayable(modelReference)) {
        V2Status("No lesson clip is assigned. Listening is not scored.")
        return
    }
    val session = remember(audio, modelReference) { V2ClipSession(audio, modelReference) }
    val context = LocalContext.current
    val owner = LocalLifecycleOwner.current
    var capturing by remember(session) { mutableStateOf(false) }
    var takeReady by remember(session) { mutableStateOf(false) }
    var permissionPending by remember(session) { mutableStateOf(false) }
    var active by remember(session, owner) { mutableStateOf(owner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) }
    var message by remember(session) { mutableStateOf<String?>(null) }
    fun startRecording() {
        takeReady = false
        capturing = session.recordStart()
        message = if (capturing) null else "Recording could not start. Try again."
    }
    val permission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (permissionPending) {
            permissionPending = false
            if (granted && active) startRecording()
            else if (!granted) message = "Microphone permission is needed to record. Listening still works."
        }
    }
    DisposableEffect(session, owner) {
        fun release() {
            if (capturing) message = "Recording stopped. No take was saved."
            active = false
            permissionPending = false
            session.release()
            capturing = false
            takeReady = false
        }
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> active = true
                Lifecycle.Event.ON_STOP, Lifecycle.Event.ON_DESTROY -> release()
                else -> Unit
            }
        }
        owner.lifecycle.addObserver(observer)
        onDispose {
            owner.lifecycle.removeObserver(observer)
            release()
        }
    }
    V2Status("Listening is not scored.")
    message?.let { V2Status(it) }
    V2SecondaryButton("Play model", { session.play(1.0f) }, tag = "v2-play-model")
    V2SecondaryButton("Play slowly", { session.play(0.75f) }, tag = "v2-play-slowly")
    if (!capturing) {
        V2SecondaryButton(
            "Record",
            {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                    startRecording()
                } else {
                    permissionPending = true
                    permission.launch(Manifest.permission.RECORD_AUDIO)
                }
            },
            enabled = active && !permissionPending,
            tag = "v2-record",
        )
    } else {
        V2SecondaryButton(
            "Stop recording",
            {
                val kept = session.recordStop()
                capturing = false
                takeReady = kept != null
                message = if (takeReady) null else "No recording was saved. Try again."
            },
            tag = "v2-stop-recording",
        )
    }
    if (takeReady) {
        V2SecondaryButton("Replay take", { session.replay() }, tag = "v2-replay-take")
        V2SecondaryButton("Hear model, then take", { session.compare() }, tag = "v2-compare-take")
    }
}
