package dev.russiancompanion.domain.practice

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
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
        assertFalse(canStartRecording(idle.copy(modelPlayed = true, hasReadableRecording = true, alreadyCompleted = true)))
        assertTrue(canStartRecording(idle.copy(modelPlayed = true, alreadyCompleted = true)))
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
        assertEquals(PRACTICE_NO_SCORE_STATEMENT, "No pronunciation score is provided.")
    }

    @Test
    fun writesRejectFabricatedCompletionAndWrongEvidence() {
        assertEquals("ineligible-model", recordingCommitAccepted(false, "recordings/one.m4a", false).reason)
        assertEquals("file-required", recordingCommitAccepted(true, "", false).reason)
        assertEquals("completion-not-committed", recordingCommitAccepted(true, "recordings/one.m4a", true).reason)
        assertTrue(recordingCommitAccepted(true, "recordings/one.m4a", false).accepted)
        assertEquals(
            "recording-required",
            listenCompletionAccepted(true, "audio-1", recording = null).reason,
        )
        assertEquals(
            "wrong-model",
            listenCompletionAccepted(true, "audio-1", StoredRecordingRef("audio-2", "recordings/one.m4a", false)).reason,
        )
        assertEquals(
            "already-completed",
            listenCompletionAccepted(true, "audio-1", StoredRecordingRef("audio-1", "recordings/one.m4a", true)).reason,
        )
        assertTrue(
            listenCompletionAccepted(true, "audio-1", StoredRecordingRef("audio-1", "recordings/one.m4a", false)).accepted,
        )
    }

    @Test
    fun neighborsStayInsideTheAdmittedSequence() {
        val admitted = listOf("audio-a", "audio-b", "audio-c")
        assertNull(previousAdmittedModelId(admitted, "audio-a"))
        assertEquals("audio-a", previousAdmittedModelId(admitted, "audio-b"))
        assertEquals("audio-c", nextAdmittedModelId(admitted, "audio-b"))
        assertNull(nextAdmittedModelId(admitted, "audio-c"))
        assertNull(previousAdmittedModelId(admitted, "missing"))
        assertNull(nextAdmittedModelId(admitted, "missing"))
    }

    @Test
    fun aListenPlanCompletesOnlyWhenEveryPrescribedModelIsRecorded() {
        val plan = ListenPlanRef("plan-listen", "segment-1", listOf("audio-a", "audio-b"))
        val library = ListenPlanRef("plan-listen", "segment-1", listOf("audio-a"))
        assertNull(listenPlanPrescribing(listOf(plan), activeSegmentId = null, audioModelId = "audio-a"))
        assertNull(listenPlanPrescribing(listOf(plan), "segment-1", "audio-library"))
        assertNull(listenPlanPrescribing(emptyList(), "segment-1", "audio-a"))
        assertEquals(plan, listenPlanPrescribing(listOf(plan), "segment-1", "audio-b"))
        assertEquals(library.id, listenPlanPrescribing(listOf(library), "segment-1", "audio-a")?.id)
        assertFalse(prescribedListenPlanComplete(plan.audioModelIds, emptySet(), "audio-library"))
        assertFalse(prescribedListenPlanComplete(plan.audioModelIds, emptySet(), "audio-a"))
        assertFalse(prescribedListenPlanComplete(plan.audioModelIds, setOf("audio-a"), "audio-a"))
        assertTrue(prescribedListenPlanComplete(plan.audioModelIds, setOf("audio-a"), "audio-b"))
        assertTrue(prescribedListenPlanComplete(plan.audioModelIds, setOf("audio-b"), "audio-a"))
        assertFalse(completedRecordingCountsTowardPlan(StoredRecordingRef("audio-a", "recordings/one.m4a", false)))
        assertFalse(completedRecordingCountsTowardPlan(StoredRecordingRef("audio-a", "", true)))
        assertTrue(completedRecordingCountsTowardPlan(StoredRecordingRef("audio-a", "recordings/one.m4a", true)))
    }
}
