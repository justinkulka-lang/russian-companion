package dev.russiancompanion.domain.practice

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AbPlaybackTest {
    @Test
    fun abOrderingSpeedsAndReplaceStayGoverned() {
        assertEquals(AbStep.RECORDING, nextAbStep(AbStep.MODEL))
        assertEquals(AbStep.MODEL, nextAbStep(AbStep.RECORDING))
        assertTrue(modelPlaybackSpeedAccepted(0.75f))
        assertTrue(modelPlaybackSpeedAccepted(1.0f))
        assertFalse(modelPlaybackSpeedAccepted(1.25f))
        assertEquals(1.0f, RECORDING_PLAYBACK_SPEED)
        assertFalse(packagedAudioAlteredBySpeed(0.75f))
        val looping = AbSession(status = AbSessionStatus.PLAYING_RECORDING, looping = true)
        assertTrue(shouldContinueAbLoop(looping, succeeded = true))
        assertFalse(shouldContinueAbLoop(looping.copy(looping = false), succeeded = true))
        assertFalse(shouldContinueAbLoop(looping, succeeded = false))
        val progress = PracticeProgress(
            modelPlayable = true,
            modelPlayed = true,
            hasReadableRecording = true,
            modelReplayedAfterRecording = true,
            userRecordingPlayed = true,
            microphoneGranted = true,
            alreadyCompleted = true,
        )
        assertTrue(canReplaceTake(progress))
        assertFalse(canStartRecording(progress))
    }

    @Test
    fun repeatAbCompletesTwoFullLoopsAndStopsOnEachTerminationPath() {
        val audio = ScriptedPracticeAudio()
        val driver = AbPlaybackDriver(
            audio = audio,
            modelReference = "model.ogg",
            recordingReference = "take.m4a",
            modelSpeed = { 0.75f },
        )

        driver.startLoop()
        assertTrue(conflictingPlaybackBlocked(driver.session()))
        assertEquals(listOf("model:model.ogg"), audio.plays)
        assertEquals(0.75f, audio.lastSpeed)
        audio.completeCurrent(true)
        assertEquals(listOf("model:model.ogg", "recording:take.m4a"), audio.plays)
        audio.completeCurrent(true)
        assertEquals(1, driver.session().cyclesCompleted)
        assertEquals(AbSessionStatus.PLAYING_MODEL, driver.session().status)
        audio.completeCurrent(true)
        audio.completeCurrent(true)
        assertEquals(2, driver.session().cyclesCompleted)
        assertTrue(driver.session().looping)
        assertEquals(
            listOf(
                "model:model.ogg",
                "recording:take.m4a",
                "model:model.ogg",
                "recording:take.m4a",
                "model:model.ogg",
            ),
            audio.plays,
        )

        driver.stop(AbStopReason.USER_STOP)
        assertFalse(driver.session().active)
        assertFalse(driver.session().looping)
        assertEquals(AbStopReason.USER_STOP, driver.session().lastStopReason)
        assertEquals(1.0f, audio.recordingSpeed)

        val duringModel = newDriver()
        duringModel.startLoop()
        duringModel.stop(AbStopReason.USER_STOP)
        assertEquals(AbSessionStatus.IDLE, duringModel.session().status)
        assertEquals(AbStopReason.USER_STOP, duringModel.session().lastStopReason)

        val duringRecording = newDriver()
        duringRecording.startLoop()
        duringRecording.audio.completeCurrent(true)
        assertEquals(AbSessionStatus.PLAYING_RECORDING, duringRecording.session().status)
        duringRecording.stop(AbStopReason.USER_STOP)
        assertFalse(duringRecording.session().active)

        val focusLoss = newDriver()
        focusLoss.startLoop()
        focusLoss.stop(AbStopReason.FOCUS_LOSS)
        assertEquals(AbStopReason.FOCUS_LOSS, focusLoss.session().lastStopReason)
        assertFalse(focusLoss.session().looping)

        val navigation = newDriver()
        navigation.startOnce()
        navigation.stop(AbStopReason.NAVIGATION)
        assertEquals(AbStopReason.NAVIGATION, navigation.session().lastStopReason)

        val background = newDriver()
        background.startLoop()
        background.audio.completeCurrent(true)
        background.stop(AbStopReason.BACKGROUND)
        assertEquals(AbStopReason.BACKGROUND, background.session().lastStopReason)
        assertFalse(background.session().active)

        val error = newDriver()
        error.startLoop()
        error.audio.completeCurrent(false)
        assertEquals(AbStopReason.ERROR, error.session().lastStopReason)
        assertFalse(error.session().active)
        assertFalse(error.session().looping)
    }

    private class Driven(
        val audio: ScriptedPracticeAudio,
        val driver: AbPlaybackDriver,
    ) {
        fun startLoop() = driver.startLoop()
        fun startOnce() = driver.startOnce()
        fun stop(reason: AbStopReason) = driver.stop(reason)
        fun session() = driver.session()
    }

    private fun newDriver(): Driven {
        val audio = ScriptedPracticeAudio()
        return Driven(
            audio = audio,
            driver = AbPlaybackDriver(
                audio = audio,
                modelReference = "model.ogg",
                recordingReference = "take.m4a",
                modelSpeed = { 1.0f },
            ),
        )
    }
}

internal class ScriptedPracticeAudio : PracticeAudio {
    val plays = mutableListOf<String>()
    var lastSpeed: Float? = null
    val recordingSpeed = RECORDING_PLAYBACK_SPEED
    private var pending: ((Boolean) -> Unit)? = null

    override fun modelPlayable(localAudioReference: String): Boolean = true

    override fun recordingReadable(localFileReference: String): Boolean = localFileReference.isNotBlank()

    override fun playModel(localAudioReference: String, onFinished: (Boolean) -> Unit) {
        playModel(localAudioReference, 1.0f, onFinished)
    }

    override fun playModel(localAudioReference: String, speed: Float, onFinished: (Boolean) -> Unit) {
        interrupt()
        lastSpeed = speed
        plays += "model:$localAudioReference"
        pending = onFinished
    }

    override fun playRecording(localFileReference: String, onFinished: (Boolean) -> Unit) {
        interrupt()
        plays += "recording:$localFileReference"
        pending = onFinished
    }

    override fun stopPlayback() {
        interrupt()
    }

    override fun startCapture(): String = "recordings/test-capture"

    override fun stopCapture(keep: Boolean): String? = if (keep) "recordings/test-capture" else null

    fun completeCurrent(success: Boolean) {
        val callback = pending ?: throw IllegalStateException("nothing is playing")
        pending = null
        callback(success)
    }

    private fun interrupt() {
        val callback = pending
        pending = null
        callback?.invoke(false)
    }
}
