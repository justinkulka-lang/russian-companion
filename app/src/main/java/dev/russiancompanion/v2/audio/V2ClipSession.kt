package dev.russiancompanion.v2.audio

import dev.russiancompanion.domain.practice.AbPlaybackDriver
import dev.russiancompanion.domain.practice.PracticeAudio

/**
 * Unscored model playback, slowed playback, local recording, replay, and A/B comparison.
 * Slowed playback is a player speed. It does not rewrite the source clip.
 */
internal class V2ClipSession(
    private val audio: PracticeAudio,
    val modelReference: String,
) {
    var takeReference: String? = null
        private set

    var modelHeard: Boolean = false
        private set

    var takeHeard: Boolean = false
        private set

    var compared: Boolean = false
        private set

    fun play(speed: Float, onFinished: (Boolean) -> Unit = {}) {
        audio.playModel(modelReference, speed.coerceIn(SLOW_SPEED, NORMAL_SPEED)) { succeeded ->
            if (succeeded) modelHeard = true
            onFinished(succeeded)
        }
    }

    fun recordStart(): Boolean {
        val reference = audio.startCapture() ?: return false
        takeReference = reference
        return true
    }

    fun recordStop(): String? {
        val kept = audio.stopCapture(keep = true) ?: return null
        takeReference = kept
        return kept
    }

    fun replay(onFinished: (Boolean) -> Unit = {}) {
        val reference = takeReference
        if (reference == null) {
            onFinished(false)
            return
        }
        audio.playRecording(reference) { succeeded ->
            if (succeeded) takeHeard = true
            onFinished(succeeded)
        }
    }

    fun compare(speed: Float = SLOW_SPEED) {
        val take = takeReference ?: return
        AbPlaybackDriver(
            audio = audio,
            modelReference = modelReference,
            recordingReference = take,
            modelSpeed = { speed.coerceIn(SLOW_SPEED, NORMAL_SPEED) },
            onModelHeard = { modelHeard = true },
            onRecordingHeard = {
                takeHeard = true
                compared = true
            },
        ).startOnce()
    }

    private companion object {
        const val SLOW_SPEED = 0.75f
        const val NORMAL_SPEED = 1.0f
    }
}
