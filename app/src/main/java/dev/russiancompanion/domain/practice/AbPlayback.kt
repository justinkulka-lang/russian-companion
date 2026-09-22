package dev.russiancompanion.domain.practice

internal enum class AbStep {
    MODEL,
    RECORDING,
}

internal data class AbPlaybackState(
    val looping: Boolean,
    val step: AbStep,
    val modelSpeed: Float,
)

internal val MODEL_PLAYBACK_SPEEDS = listOf(0.75f, 1.0f)

internal const val RECORDING_PLAYBACK_SPEED = 1.0f

internal fun modelPlaybackSpeedAccepted(speed: Float): Boolean {
    return MODEL_PLAYBACK_SPEEDS.any { it == speed }
}

internal fun nextAbStep(current: AbStep): AbStep {
    return when (current) {
        AbStep.MODEL -> AbStep.RECORDING
        AbStep.RECORDING -> AbStep.MODEL
    }
}

internal fun shouldContinueAbLoop(state: AbPlaybackState, completedStep: AbStep, succeeded: Boolean): Boolean {
    if (!succeeded || !state.looping) return false
    return true
}

internal fun canReplaceTake(progress: PracticeProgress): Boolean {
    return progress.modelPlayable && progress.microphoneGranted &&
        (progress.hasReadableRecording || progress.alreadyCompleted)
}

internal fun packagedAudioAlteredBySpeed(speed: Float): Boolean {
    return false && modelPlaybackSpeedAccepted(speed)
}
