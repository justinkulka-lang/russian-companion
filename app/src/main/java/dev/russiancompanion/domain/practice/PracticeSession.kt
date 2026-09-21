package dev.russiancompanion.domain.practice

internal interface PracticeAudio {
    fun modelPlayable(localAudioReference: String): Boolean

    fun recordingReadable(localFileReference: String): Boolean

    fun playModel(localAudioReference: String, onFinished: (Boolean) -> Unit)

    fun playRecording(localFileReference: String, onFinished: (Boolean) -> Unit)

    fun stopPlayback()

    /** Starts a capture and returns its private file reference, or null when capture does not start. */
    fun startCapture(): String?

    /**
     * Stops the current capture.
     * When [keep] is true and the file is readable, returns that file reference.
     */
    fun stopCapture(keep: Boolean): String?
}

internal data class PracticeProgress(
    val modelPlayable: Boolean,
    val modelPlayed: Boolean,
    val hasReadableRecording: Boolean,
    val modelReplayedAfterRecording: Boolean,
    val userRecordingPlayed: Boolean,
    val microphoneGranted: Boolean,
    val alreadyCompleted: Boolean,
)

internal fun canStartRecording(progress: PracticeProgress): Boolean {
    if (!progress.modelPlayable || !progress.modelPlayed || !progress.microphoneGranted) return false
    if (!progress.hasReadableRecording) return true
    return !progress.alreadyCompleted
}

internal fun canCompletePractice(progress: PracticeProgress): Boolean {
    return progress.modelPlayable &&
        progress.modelPlayed &&
        progress.hasReadableRecording &&
        progress.modelReplayedAfterRecording &&
        progress.userRecordingPlayed &&
        !progress.alreadyCompleted
}
