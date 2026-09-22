package dev.russiancompanion.data.local

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.data.local.seed.PlanDefinitionSeed
import dev.russiancompanion.data.local.seed.SeedJson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class DailyPlanStoreTest {
    @Test
    fun externalCompletionPersistsForThatDayAndTutorCanBeRemoved() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        LocalStore.openInMemory(context).use { store ->
            store.applySeed(SeedJson.encode(checklistDocument()))
            store.confirmLesson("segment-1", "2026-09-21")

            assertTrue(store.completeExternalActivity("plan-book", "2026-09-21", "2026-09-21T09:00"))
            assertFalse(store.completeExternalActivity("plan-book", "2026-09-21", "2026-09-21T09:05"))
            val book = store.activityLogs().single()
            assertEquals(ActivityCategory.GRAMMAR, book.category)
            assertEquals(0, book.durationMinutes)
            assertTrue(book.selfAttested)
            assertTrue(book.completed)
            assertEquals(
                CompletionState.COMPLETED,
                store.planInstances("2026-09-21", "segment-1").single().completionState,
            )
            assertTrue(store.planInstances("2026-09-22", "segment-1").isEmpty())

            try {
                store.completeExternalActivity("plan-listen", "2026-09-21", "2026-09-21T09:10")
                error("listen-record-compare was completed from the checklist")
            } catch (_: IllegalArgumentException) {
                assertEquals(1, store.activityLogs().size)
            }

            store.includeTutorTask("plan-tutor", "2026-09-21")
            assertEquals(1, store.activityLogs().size)
            assertTrue(store.completeExternalActivity("plan-tutor", "2026-09-21", "2026-09-21T09:15"))
            store.removeTutorTask("plan-tutor", "2026-09-21")
            val tutor = store.activityLogs().single { it.category == ActivityCategory.TUTOR_WORK }
            assertFalse(tutor.selfAttested)
            assertNull(tutor.planInstanceId)
            assertTrue(store.planInstances("2026-09-21", "segment-1").none { it.planDefinitionId == "plan-tutor" })
            assertEquals(listOf(book.id), store.activityLogs().filter { it.category == ActivityCategory.GRAMMAR }.map { it.id })
        }
    }

    @Test
    fun ankiCompletionIsSelfAttestedVocabularyWork() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        LocalStore.openInMemory(context).use { store ->
            store.applySeed(SeedJson.encode(checklistDocument()))
            store.confirmLesson("segment-1", "2026-09-21")
            store.completeExternalActivity("plan-anki", "2026-09-21", "2026-09-21T09:20")
            val anki = store.activityLogs().single()
            assertEquals(ActivityCategory.VOCABULARY_AND_FORMS, anki.category)
            assertTrue(anki.selfAttested)
            assertEquals(0, anki.durationMinutes)
        }
    }

    private fun checklistDocument() = minimalDocument().copy(
        planDefinitions = listOf(
            plan("plan-book", "BOOK", 1),
            plan("plan-anki", "ANKI_DROID", 2),
            plan("plan-listen", "LISTEN_RECORD_COMPARE", 3),
            plan("plan-tutor", "TUTOR", 4, required = false),
        ),
        sourceTexts = listOf(text(matchVerified = true)),
        assetRightsRecords = listOf(rights()),
        audioModels = listOf(audio()),
    )

    private fun plan(
        id: String,
        activityType: String,
        sortOrder: Int,
        required: Boolean = true,
    ): PlanDefinitionSeed {
        return PlanDefinitionSeed(
            id = id,
            segmentId = "segment-1",
            activityType = activityType,
            instruction = "Instruction $id",
            sortOrder = sortOrder,
            required = required,
            lessonNumber = if (activityType == "BOOK" || activityType == "ANKI_DROID") 1 else null,
            audioModelIds = if (activityType == "LISTEN_RECORD_COMPARE") listOf("audio-1") else emptyList(),
        )
    }
}
