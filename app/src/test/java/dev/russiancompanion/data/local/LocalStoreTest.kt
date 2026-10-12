package dev.russiancompanion.data.local

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.RussianCompanionApplication
import dev.russiancompanion.data.local.seed.PlanDefinitionSeed
import dev.russiancompanion.data.local.seed.SeedApplyStatus
import dev.russiancompanion.data.local.seed.SeedJson
import dev.russiancompanion.domain.pathway.PENGUIN_BEGINNER_ARC_LESSON_COUNT
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class LocalStoreTest {
    @Test
    fun shippedSeedOpensANewLearnerAfterShareAlikeConfirmation() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        (context.applicationContext as RussianCompanionApplication).closeStartupStoreForTests()
        LocalStore.open(context).use { store ->
            val outcome = store.applySeed(shippedSeed())
            assertEquals(SeedApplyStatus.APPLIED, outcome.status)
            assertTrue(outcome.eligibleAudioCount > 0)
            assertEquals(false, outcome.shareAlikeBlocksRelease)
            assertEquals(1, store.learnerCount())
            assertFalse(store.learner().onboardingCompleted)
            assertEquals((1..PENGUIN_BEGINNER_ARC_LESSON_COUNT).toList(), store.eligibleSegments().map { it.lessonNumber })
            assertEquals(PENGUIN_BEGINNER_ARC_LESSON_COUNT, store.eligibleGates().size)
        }
    }

    @Test
    fun reseedKeepsUserRecordsAndDropsRemovedAudioFromUse() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        LocalStore.openInMemory(context).use { store ->
            val withAudio = minimalDocument().copy(
                planDefinitions = listOf(bookPlan()),
                sourceTexts = listOf(text(matchVerified = true)),
                assetRightsRecords = listOf(rights()),
                audioModels = listOf(audio()),
            )
            val first = store.applySeed(SeedJson.encode(withAudio))
            assertEquals(1, first.eligibleAudioCount)
            store.completeOnboarding("segment-1")
            val log = store.recordPlanCompletion(
                localDate = "2026-09-21",
                planDefinitionId = "plan-book",
                category = ActivityCategory.GRAMMAR,
                durationMinutes = 25,
                recordedAtLocal = "2026-09-21T09:00",
            )
            assertTrue(log.selfAttested)
            val recording = store.commitRecording(
                audioModelId = "audio-1",
                localFileReference = "recordings/local-file",
                createdAtLocal = "2026-09-21T09:05",
                markedForTutorReview = true,
                completionState = CompletionState.NOT_COMPLETED,
            )
            assertTrue(store.completeListenPractice("audio-1", "2026-09-21", "2026-09-21T09:06"))

            val removedAudio = withAudio.copy(audioModels = emptyList(), assetRightsRecords = emptyList())
            val second = store.applySeed(SeedJson.encode(removedAudio))
            assertEquals(SeedApplyStatus.APPLIED, second.status)
            assertEquals(0, second.eligibleAudioCount)
            assertEquals(EligibilityStatus.EXCLUDED, store.audio("audio-1")?.eligibilityStatus)
            assertEquals(1, store.learnerCount())
            assertTrue(store.learner().onboardingCompleted)
            assertEquals(listOf(log.id), store.activityLogs().map { it.id })
            assertEquals(listOf(recording.id), store.recordings().map { it.id })
            assertEquals(25L, store.activityTotals().first { it.category == ActivityCategory.GRAMMAR }.recordedMinutes)

            val corrupt = store.applySeed("{")
            assertEquals(SeedApplyStatus.CORRUPT, corrupt.status)
            assertEquals(listOf(log.id), store.activityLogs().map { it.id })
            assertTrue(store.learner().onboardingCompleted)
        }
    }

    @Test
    fun fileDatabaseKeepsActivityAfterTheStoreIsClosed() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        (context.applicationContext as RussianCompanionApplication).closeStartupStoreForTests()
        val created = LocalStore.open(context).use { store ->
            store.recordActivity(
                category = ActivityCategory.READING,
                durationMinutes = 15,
                completed = true,
                recordedAtLocal = "2026-09-21T08:00",
                segmentId = null,
                selfAttested = true,
            )
        }
        LocalStore.open(context).use { reopened ->
            assertEquals(listOf(created.id), reopened.activityLogs().map { it.id })
            assertEquals(15L, reopened.activityTotals().first { it.category == ActivityCategory.READING }.recordedMinutes)
        }
    }

    @Test
    fun bookEvidenceCannotBeStoredAsVerifiedAndBlankRecordingIsNotCommitted() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        LocalStore.openInMemory(context).use { store ->
            val document = minimalDocument().copy(
                gateDefinitions = listOf(gate()),
                sourceTexts = listOf(text(matchVerified = true)),
                assetRightsRecords = listOf(rights()),
                audioModels = listOf(audio()),
            )
            store.applySeed(SeedJson.encode(document))
            val evidence = store.recordEvidence(
                gateDefinitionId = "gate-1",
                evidenceType = EvidenceType.BOOK_REVISION_SCORE,
                enteredValue = "user-entered",
                recordedAtLocal = "2026-09-21T10:00",
            )
            assertEquals(SelfAttestationStatus.SELF_ATTESTED, evidence.selfAttestationStatus)
            val before = store.recordings().size
            try {
                store.commitRecording(
                    audioModelId = "audio-1",
                    localFileReference = "",
                    createdAtLocal = "2026-09-21T10:01",
                    markedForTutorReview = false,
                    completionState = CompletionState.NOT_COMPLETED,
                )
                error("blank recording was committed")
            } catch (_: IllegalArgumentException) {
                assertEquals(before, store.recordings().size)
            }
        }
    }

    @Test
    fun negativeDurationIsRejectedAtEntry() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        LocalStore.openInMemory(context).use { store ->
            try {
                store.recordActivity(
                    category = ActivityCategory.READING,
                    durationMinutes = -1,
                    completed = false,
                    recordedAtLocal = "2026-09-21T09:00",
                    segmentId = null,
                    selfAttested = false,
                )
                error("A negative duration was stored")
            } catch (_: IllegalArgumentException) {
                assertTrue(store.activityLogs().isEmpty())
            }
        }
    }

    private fun shippedSeed(): String {
        return ApplicationProvider.getApplicationContext<Context>()
            .assets
            .open(LocalStore.SEED_ASSET)
            .bufferedReader()
            .use { it.readText() }
    }

    private fun bookPlan(): PlanDefinitionSeed {
        return PlanDefinitionSeed(
            id = "plan-book",
            segmentId = "segment-1",
            activityType = "BOOK",
            instruction = "Use your copy of the book for this lesson",
            sortOrder = 1,
            required = true,
            lessonNumber = 1,
        )
    }
}
