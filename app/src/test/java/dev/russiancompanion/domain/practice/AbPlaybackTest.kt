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
        val looping = AbPlaybackState(looping = true, step = AbStep.MODEL, modelSpeed = 0.75f)
        assertTrue(shouldContinueAbLoop(looping, AbStep.RECORDING, succeeded = true))
        assertFalse(shouldContinueAbLoop(looping.copy(looping = false), AbStep.RECORDING, succeeded = true))
        assertFalse(shouldContinueAbLoop(looping, AbStep.RECORDING, succeeded = false))
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
}
