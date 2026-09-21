package dev.russiancompanion.data.local

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.data.audio.AndroidPracticeAudio
import dev.russiancompanion.data.local.seed.PlanDefinitionSeed
import dev.russiancompanion.data.local.seed.SeedJson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class PracticeStoreTest {
    @Test
    fun missingPackagedAudioIsNotPlayable() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val audio = AndroidPracticeAudio(context)
        assertFalse(audio.modelPlayable("audio/missing-model"))
        assertFalse(audio.recordingReadable("recordings/missing"))
    }

    @Test
    fun completingPracticeStoresTheRecordingWithoutAPronunciationJudgment() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        LocalStore.openInMemory(context).use { store ->
            store.applySeed(
                SeedJson.encode(
                    minimalDocument().copy(
                        planDefinitions = listOf(
                            PlanDefinitionSeed(
                                id = "plan-listen",
                                segmentId = "segment-1",
                                activityType = "LISTEN_RECORD_COMPARE",
                                instruction = "Listen to the licensed model",
                                sortOrder = 1,
                                required = true,
                            ),
                        ),
                        sourceTexts = listOf(text(matchVerified = true)),
                        assetRightsRecords = listOf(rights()),
                        audioModels = listOf(audio()),
                    ),
                ),
            )
            store.confirmLesson("segment-1", "2026-09-21")
            try {
                store.completeExternalActivity("plan-listen", "2026-09-21", "2026-09-21T09:00")
                error("listen-record-compare was completed from the external checklist")
            } catch (_: IllegalArgumentException) {
                assertTrue(store.activityLogs().isEmpty())
            }
            val recording = store.commitRecording(
                audioModelId = "audio-1",
                localFileReference = "recordings/local-file",
                createdAtLocal = "2026-09-21T09:01",
                markedForTutorReview = false,
                completionState = CompletionState.NOT_COMPLETED,
            )
            store.setRecordingTutorReview(recording.id, true)
            assertTrue(store.completeListenPractice("audio-1", "2026-09-21", "2026-09-21T09:02"))
            assertFalse(store.completeListenPractice("audio-1", "2026-09-21", "2026-09-21T09:03"))
            val saved = store.recordings().single()
            assertTrue(saved.markedForTutorReview)
            assertEquals(CompletionState.COMPLETED, saved.completionState)
            val log = store.activityLogs().single()
            assertEquals(ActivityCategory.SPEAKING_AND_PRONUNCIATION, log.category)
            assertEquals(0, log.durationMinutes)
            assertFalse(log.selfAttested)
            assertEquals(
                CompletionState.COMPLETED,
                store.planInstances("2026-09-21", "segment-1").single().completionState,
            )
        }
    }
}
