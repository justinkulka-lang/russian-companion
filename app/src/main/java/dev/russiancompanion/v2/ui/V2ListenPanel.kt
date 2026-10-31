package dev.russiancompanion.v2.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    var capturing by remember { mutableStateOf(false) }
    var takeReady by remember { mutableStateOf(false) }
    V2Status("Listening is not scored.")
    V2SecondaryButton("Play model", { session.play(1.0f) }, tag = "v2-play-model")
    V2SecondaryButton("Play slowly", { session.play(0.75f) }, tag = "v2-play-slowly")
    if (!capturing) {
        V2SecondaryButton(
            "Record",
            {
                if (session.recordStart()) capturing = true
            },
            tag = "v2-record",
        )
    } else {
        V2SecondaryButton(
            "Stop recording",
            {
                val kept = session.recordStop()
                if (kept != null) {
                    capturing = false
                    takeReady = true
                }
            },
            tag = "v2-stop-recording",
        )
    }
    if (takeReady) {
        V2SecondaryButton("Replay take", { session.replay() }, tag = "v2-replay-take")
        V2SecondaryButton("Hear model, then take", { session.compare() }, tag = "v2-compare-take")
    }
}
