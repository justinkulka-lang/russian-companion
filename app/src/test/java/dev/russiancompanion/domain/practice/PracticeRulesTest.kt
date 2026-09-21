package dev.russiancompanion.domain.practice

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PracticeRulesTest {
    @Test
    fun recordingWaitsForAPlayableModelAndAGrantedMicrophone() {
        val idle = PracticeProgress(
            modelPlayable = true,
            modelPlayed = false,
            hasReadableRecording = false,
            modelReplayedAfterRecording = false,
            userRecordingPlayed = false,
            microphoneGranted = true,
            alreadyCompleted = false,
        )
        assertFalse(canStartRecording(idle))
        assertFalse(canCompletePractice(idle))
        assertFalse(canStartRecording(idle.copy(modelPlayed = true, microphoneGranted = false)))
        assertFalse(canStartRecording(idle.copy(modelPlayed = true, modelPlayable = false)))
        assertTrue(canStartRecording(idle.copy(modelPlayed = true)))
    }

    @Test
    fun completionRequiresSeparateReplayAndHasNoShortcut() {
        val ready = PracticeProgress(
            modelPlayable = true,
            modelPlayed = true,
            hasReadableRecording = true,
            modelReplayedAfterRecording = true,
            userRecordingPlayed = true,
            microphoneGranted = true,
            alreadyCompleted = false,
        )
        assertTrue(canCompletePractice(ready))
        assertFalse(canCompletePractice(ready.copy(modelReplayedAfterRecording = false)))
        assertFalse(canCompletePractice(ready.copy(userRecordingPlayed = false)))
        assertFalse(canCompletePractice(ready.copy(hasReadableRecording = false)))
        assertFalse(canCompletePractice(ready.copy(alreadyCompleted = true)))
        assertTrue(canStartRecording(ready.copy(hasReadableRecording = false, alreadyCompleted = true)))
    }
}
