package dev.russiancompanion.v2

import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.backup.*
import dev.russiancompanion.v2.curriculum.*
import dev.russiancompanion.v2.Unit9NounBehaviorFixtures.root
import dev.russiancompanion.v2.Unit9NounBehaviorFixtures.pack
import dev.russiancompanion.v2.Unit9NounBehaviorFixtures.context
import dev.russiancompanion.v2.Unit9NounBehaviorFixtures.document
import dev.russiancompanion.v2.Unit9NounBehaviorFixtures.advance
import dev.russiancompanion.v2.Unit9NounBehaviorFixtures.ordinary
import dev.russiancompanion.v2.Unit9NounBehaviorFixtures.prepare
import dev.russiancompanion.v2.Unit9NounBehaviorFixtures.open
import dev.russiancompanion.v2.Unit9NounBehaviorFixtures.answer
import dev.russiancompanion.v2.Unit9NounBehaviorFixtures.archive
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class Unit9NounBehaviorCompatibilityTest {




    @Test fun missingOwnersAndChangedTeachingOrItemsRefuseFreshAndSavedTasks() {
        LocalStore.openInMemory(context()).use { store ->
            prepare(store); val task = open(store); val learner = store.v2LearnerView()
            for (owner in listOf(5, 8, 3)) {
                assertNull(createCumulativeReviewAssignment(pack, learner.copy(completedUnitNumbers = learner.completedUnitNumbers - owner), 9, "missing-$owner", 0))
                val slot = task.slots.single { it.ownerUnit == owner }
                val changed = pack.copy(items = pack.items.map { if (it.id == slot.itemId) it.copy(feedback = it.feedback + " changed") else it })
                assertFalse(reviewAssignmentCurrent(changed, task))
                val sid = pack.unit(owner)!!.objectives.single { it.id == slot.objectiveId }.instructionStepIds.last()
                val teaching = pack.copy(units = pack.units.map { u -> if (u.number != owner) u else u.copy(sessions = u.sessions.map { session ->
                    session.copy(steps = session.steps.map { if (it.id == sid) it.copy(body = it.body + " changed") else it })
                }) })
                assertFalse(reviewAssignmentCurrent(teaching, task))
            }
            assertNull(createCumulativeReviewAssignment(pack, learner.copy(governedStepDigests = emptyMap()), 9, "no-teaching", 0))
        }
    }

    @Test fun eachNounMissCanAllocateWhileOptionalAndMalformedHistoryCannotSelect() {
        LocalStore.openInMemory(context()).use { store ->
            prepare(store)
            val ids = Unit9NounBehaviorCumulativeReviewPolicy.definition.pool[0].candidateItemIds
            for (id in ids) {
                recordMiss(store, id)
                assertEquals(id, createCumulativeReviewAssignment(pack, store.v2LearnerView(), 9, "miss-$id", 0)!!.slots[0].itemId)
            }
            val item = pack.item(ids.first())!!
            store.v2RecordGovernedAttempt(prepareGovernedAttempt(pack, item.id, item.options.first { it != item.answer }, OPTIONAL_PRACTICE, practiceStepId("mixed"), "optional-miss")!!)
            val view = store.v2LearnerView(); val candidates = ids.map { pack.item(it)!! }
            assertEquals(ids.last(), createCumulativeReviewAssignment(pack, view, 9, "ignore-optional", 0)!!.slots[0].itemId)
            assertEquals(ids.first(), recentReviewCandidate(pack, view.copy(governedAttempts = view.governedAttempts!!.map { it.copy(itemDigest = "0".repeat(64)) }), candidates)!!.id)
            assertEquals(ids.first(), recentReviewCandidate(pack, view.copy(governedAttempts = view.governedAttempts!!.map { it.copy(acceptedOrder = 1) }), candidates)!!.id)
        }
    }

    @Test fun recentNounMissRemainsFixedThroughCorrectionIdempotenceRollbackBackupAndReopen() {
        val ctx = context(); val name = "unit9-noun-miss-${System.nanoTime()}"; val stage = File(ctx.cacheDir, name)
        try {
            LocalStore.openNamed(ctx, name).use { store ->
                prepare(store); val id = "u5-i-decl-окно"; recordMiss(store, id); val task = open(store)
                assertEquals(id, task.slots[0].itemId)
                val item = pack.item(id)!!; val step = pack.unit(5)!!.sessions.flatMap { it.steps }.single { it.kind == "CHECK" && id in it.itemIds }
                store.v2RecordGovernedAttempt(prepareGovernedAttempt(pack, id, item.answer, PRESCRIBED_CHECK, step.id, "corrected")!!)
                assertEquals(task, open(store)); assertTrue(completedReviewSlots(pack, task, store.v2LearnerView().reviewAnswers).isEmpty())
                task.slots.indices.forEach { answer(store, task, it) }
                val before = store.governedLearnerDocument(); answer(store, task, 0); assertEquals(before, store.governedLearnerDocument())
                store.failAdvanceAfterCompletionForTests = true
                assertTrue(runCatching { advance(store, 9) }.isFailure); assertEquals(before, store.governedLearnerDocument())
                assertTrue(advance(store, 9)); val completed = store.governedLearnerDocument()
                val decoded = readGovernedBackup(archive(store).inputStream(), "dev.russiancompanion", pack.packId, pack.packVersion, stage)
                assertTrue(decoded is BackupReadResult.Valid)
                LocalStore.openInMemory(ctx).use { target ->
                    assertEquals(GovernedReplaceOutcome.REPLACED, target.replaceGovernedLearner((decoded as BackupReadResult.Valid).backup.document, emptyList()))
                    assertEquals(completed, target.governedLearnerDocument()); assertEquals(task, open(target))
                    assertEquals((1..9).toSet(), currentCompletedUnitNumbers(pack, target.v2LearnerView()))
                }
            }
            LocalStore.openNamed(ctx, name).use { store ->
                assertEquals("u5-i-decl-окно", open(store).slots[0].itemId); assertEquals((1..9).toSet(), currentCompletedUnitNumbers(pack, store.v2LearnerView()))
            }
        } finally { stage.deleteRecursively(); ctx.deleteDatabase(name); File(ctx.filesDir, "recordings-$name").deleteRecursively() }
    }

    private fun recordMiss(store: LocalStore, id: String) {
        val item = pack.item(id)!!; val step = pack.unit(5)!!.sessions.flatMap { it.steps }.single { it.kind == "CHECK" && id in it.itemIds }
        store.v2RecordGovernedAttempt(prepareGovernedAttempt(pack, id, item.options.first { it != item.answer }, PRESCRIBED_CHECK, step.id, "noun-miss")!!)
    }
}
