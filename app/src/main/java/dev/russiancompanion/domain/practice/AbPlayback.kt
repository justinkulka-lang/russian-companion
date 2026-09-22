package dev.russiancompanion.domain.practice

internal enum class AbStep {
    MODEL,
    RECORDING,
}

internal enum class AbSessionStatus {
    IDLE,
    PLAYING_MODEL,
    PLAYING_RECORDING,
}

internal enum class AbStopReason {
    USER_STOP,
    NAVIGATION,
    BACKGROUND,
    FOCUS_LOSS,
    ERROR,
}

internal enum class AbCommand {
    NONE,
    PLAY_MODEL,
    PLAY_RECORDING,
    STOP_PLAYBACK,
}

internal data class AbSession(
    val status: AbSessionStatus = AbSessionStatus.IDLE,
    val looping: Boolean = false,
    val cyclesCompleted: Int = 0,
    val lastStopReason: AbStopReason? = null,
    val generation: Int = 0,
) {
    val active: Boolean
        get() = status != AbSessionStatus.IDLE

    val step: AbStep?
        get() = when (status) {
            AbSessionStatus.PLAYING_MODEL -> AbStep.MODEL
            AbSessionStatus.PLAYING_RECORDING -> AbStep.RECORDING
            AbSessionStatus.IDLE -> null
        }
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

internal fun shouldContinueAbLoop(session: AbSession, succeeded: Boolean): Boolean {
    return succeeded && session.looping && session.active
}

internal fun conflictingPlaybackBlocked(session: AbSession): Boolean = session.active

internal fun canReplaceTake(progress: PracticeProgress): Boolean {
    return progress.modelPlayable && progress.microphoneGranted &&
        (progress.hasReadableRecording || progress.alreadyCompleted)
}

internal fun packagedAudioAlteredBySpeed(speed: Float): Boolean {
    return false && modelPlaybackSpeedAccepted(speed)
}

internal class AbPlaybackMachine {
    private var session = AbSession()

    fun snapshot(): AbSession = session

    fun startOnce(): AbCommand = begin(looping = false)

    fun startLoop(): AbCommand = begin(looping = true)

    fun stop(reason: AbStopReason): AbCommand {
        if (!session.active && session.lastStopReason == reason) return AbCommand.NONE
        session = session.copy(
            status = AbSessionStatus.IDLE,
            looping = false,
            lastStopReason = reason,
            generation = session.generation + 1,
        )
        return AbCommand.STOP_PLAYBACK
    }

    fun onPlaybackFinished(generation: Int, succeeded: Boolean): AbCommand {
        if (generation != session.generation) return AbCommand.NONE
        if (!succeeded) return stop(AbStopReason.ERROR)
        return when (session.status) {
            AbSessionStatus.PLAYING_MODEL -> {
                session = session.copy(status = AbSessionStatus.PLAYING_RECORDING)
                AbCommand.PLAY_RECORDING
            }
            AbSessionStatus.PLAYING_RECORDING -> {
                val completed = session.cyclesCompleted + 1
                if (session.looping) {
                    session = session.copy(
                        status = AbSessionStatus.PLAYING_MODEL,
                        cyclesCompleted = completed,
                    )
                    AbCommand.PLAY_MODEL
                } else {
                    session = session.copy(
                        status = AbSessionStatus.IDLE,
                        looping = false,
                        cyclesCompleted = completed,
                    )
                    AbCommand.NONE
                }
            }
            AbSessionStatus.IDLE -> AbCommand.NONE
        }
    }

    private fun begin(looping: Boolean): AbCommand {
        session = AbSession(
            status = AbSessionStatus.PLAYING_MODEL,
            looping = looping,
            generation = session.generation + 1,
        )
        return AbCommand.PLAY_MODEL
    }
}

internal class AbPlaybackDriver(
    private val audio: PracticeAudio,
    private val modelReference: String,
    private val recordingReference: String,
    private val modelSpeed: () -> Float,
    private val onSession: (AbSession) -> Unit = {},
    private val onModelHeard: () -> Unit = {},
    private val onRecordingHeard: () -> Unit = {},
) {
    private val machine = AbPlaybackMachine()

    fun session(): AbSession = machine.snapshot()

    fun startOnce() = dispatch(machine.startOnce())

    fun startLoop() = dispatch(machine.startLoop())

    fun stop(reason: AbStopReason) = dispatch(machine.stop(reason))

    private fun dispatch(command: AbCommand) {
        onSession(machine.snapshot())
        when (command) {
            AbCommand.PLAY_MODEL -> {
                val generation = machine.snapshot().generation
                audio.playModel(modelReference, modelSpeed()) { succeeded ->
                    if (succeeded) onModelHeard()
                    dispatch(machine.onPlaybackFinished(generation, succeeded))
                }
            }
            AbCommand.PLAY_RECORDING -> {
                val generation = machine.snapshot().generation
                audio.playRecording(recordingReference) { succeeded ->
                    if (succeeded) onRecordingHeard()
                    dispatch(machine.onPlaybackFinished(generation, succeeded))
                }
            }
            AbCommand.STOP_PLAYBACK -> audio.stopPlayback()
            AbCommand.NONE -> Unit
        }
    }
}
