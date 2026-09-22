package dev.russiancompanion.data.local

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.data.navigation.RoomNavigationGateway
import dev.russiancompanion.domain.gate.AnkiStatusValue
import dev.russiancompanion.domain.gate.PRESENCE_RECORDED
import dev.russiancompanion.domain.pathway.PathwaySegment
import dev.russiancompanion.domain.pathway.PENGUIN_BEGINNER_ARC_LESSON_COUNT
import dev.russiancompanion.domain.pathway.validatePathwaySegments
import dev.russiancompanion.domain.practice.PracticeAudio
import dev.russiancompanion.domain.progress.ExportUpdate
import dev.russiancompanion.domain.progress.reproducedProgressMatches
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.ByteArrayOutputStream

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class PackagedPathwayStoreTest {
    @Test
    fun packagedPlaceholderPathwayCompletesATutorFreeLessonCycleOnce() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        context.deleteDatabase(LocalStore.DATABASE_NAME)
        LocalStore.open(context).use { store ->
            val segments = store.eligibleSegments()
            assertEquals(listOf(1, 2, 3), segments.map { it.lessonNumber })
            assertEquals(listOf("Lesson 1", "Lesson 2", "Lesson 3"), segments.map { it.lessonReference })
            val pathway = validatePathwaySegments(
                segments.map { segment ->
                    PathwaySegment(segment.id, segment.lessonReference, segment.lessonNumber, segment.sortOrder)
                },
            )
            assertFalse(pathway.coversFullArc)
            assertEquals(3, pathway.segments.size)
            assertTrue(pathway.segments.size < PENGUIN_BEGINNER_ARC_LESSON_COUNT)
            store.eligiblePlans().forEach { plan ->
                assertFalse(plan.instruction.contains("Penguin"))
                assertTrue(plan.instruction.none { it.code in 0x0400..0x04FF })
            }
            assertTrue(store.eligibleGates().none { it.displayedRuleText.contains("Tutor") })

            store.confirmLesson("segment-1", "2026-09-21")
            assertTrue(store.completeExternalActivity("plan-book-1", "2026-09-21", "2026-09-21T09:00"))
            assertTrue(store.completeExternalActivity("plan-anki-1", "2026-09-21", "2026-09-21T09:01"))
            val listen = store.eligiblePlans().single { it.id == "plan-listen-1" }
            val audioId = listen.audioModelIds.single()
            store.commitRecording(
                audioModelId = audioId,
                localFileReference = "recordings/placeholder.m4a",
                createdAtLocal = "2026-09-21T09:02",
                markedForTutorReview = true,
                completionState = CompletionState.NOT_COMPLETED,
            )
            assertTrue(store.completeListenPractice(audioId, "2026-09-21", "2026-09-21T09:03"))
            val decision = store.recordGateEvidenceAndDecide(
                gateDefinitionId = "gate-1",
                entries = listOf(
                    EvidenceType.BOOK_REVISION_SCORE to "1",
                    EvidenceType.ANKI_DROID_STATUS to AnkiStatusValue.MATURE.name,
                    EvidenceType.PRESCRIBED_WORK_COMPLETION to PRESENCE_RECORDED,
                ),
                recordedAtLocal = "2026-09-21T09:04",
            )
            assertEquals(GateDecisionStatus.SATISFIED, decision.status)
            assertEquals(3, store.activityLogs().size)
            assertTrue(store.activityLogs().single { it.category == ActivityCategory.GRAMMAR }.selfAttested)
            assertTrue(store.activityLogs().single { it.category == ActivityCategory.VOCABULARY_AND_FORMS }.selfAttested)
            assertFalse(store.activityLogs().single { it.category == ActivityCategory.SPEAKING_AND_PRONUNCIATION }.selfAttested)

            val snapshot = store.storedProgressSnapshot()
            store.recordActivity(
                category = ActivityCategory.WRITING,
                durationMinutes = 20,
                completed = true,
                recordedAtLocal = "2026-09-21T11:00",
                segmentId = null,
                selfAttested = false,
            )
            val reproduced = store.reproduceStoredProgress(snapshot)
            assertTrue(reproducedProgressMatches(snapshot, reproduced))
            assertEquals(3, reproduced.totalCompletions)

            val out = ByteArrayOutputStream()
            val update = RoomNavigationGateway(store, { null }, UnusedPracticeAudio()).exportLocal(
                out,
                "2026-09-21T12:00",
                "placeholder-cycle.zip",
                null,
            )
            assertTrue(update is ExportUpdate.Completed)
            assertEquals(false, (update as ExportUpdate.Completed).empty)
            assertEquals(ExportStatus.COMPLETED, store.exports().single().status)
        }
    }

    private class UnusedPracticeAudio : PracticeAudio {
        override fun modelPlayable(localAudioReference: String): Boolean = false

        override fun recordingReadable(localFileReference: String): Boolean = false

        override fun playModel(localAudioReference: String, onFinished: (Boolean) -> Unit) = Unit

        override fun playRecording(localFileReference: String, onFinished: (Boolean) -> Unit) = Unit

        override fun stopPlayback() = Unit

        override fun startCapture(): String = ""

        override fun stopCapture(keep: Boolean): String? = null
    }
}
