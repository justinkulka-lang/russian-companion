package dev.russiancompanion.v2

import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.backup.*
import dev.russiancompanion.v2.curriculum.*
import dev.russiancompanion.v2.Unit7GenderFixtures.root
import dev.russiancompanion.v2.Unit7GenderFixtures.pack
import dev.russiancompanion.v2.Unit7GenderFixtures.context
import dev.russiancompanion.v2.Unit7GenderFixtures.document
import dev.russiancompanion.v2.Unit7GenderFixtures.advance
import dev.russiancompanion.v2.Unit7GenderFixtures.ordinary
import dev.russiancompanion.v2.Unit7GenderFixtures.prepare
import dev.russiancompanion.v2.Unit7GenderFixtures.open
import dev.russiancompanion.v2.Unit7GenderFixtures.answer
import dev.russiancompanion.v2.Unit7GenderFixtures.archive
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class Unit7GenderCompatibilityTest {

    @Test fun wrongSchemasResponseTargetAndEpochCannotBorrowNewCredit() {
        LocalStore.openInMemory(context()).use { store ->
            prepare(store)
            val task = open(store)
            assertEquals(1, task.schema)
            for (schema in 2..17) {
                val bad = task.copy(schema = schema).let { it.copy(digest = reviewAssignmentDigest(it)) }
                assertFalse("schema $schema", reviewAssignmentCurrent(pack, bad))
                assertNotNull(reviewHistoryDefects(listOf(bad.copy(boundary = 0).let { it.copy(digest = reviewAssignmentDigest(it)) }), emptyList()))
            }
            for (bad in listOf(task.copy(targetUnit = 6), task.copy(responseRuleDigest = "0".repeat(64)), task.copy(policyDigest = "0".repeat(64)))) {
                val signed = bad.copy(digest = reviewAssignmentDigest(bad))
                assertFalse(reviewAssignmentCurrent(pack, signed))
            }
            val epoch = store.learnerMutationEpoch()
            val before = store.governedLearnerDocument()
            store.openLearnerReplacement()
            try {
                assertNull(store.openCumulativeReview(pack, 7, epoch))
                assertFalse(store.answerCumulativeReview(pack, task.id, 0, pack.item(task.slots[0].itemId)!!.answer, epoch))
                assertEquals(before, store.governedLearnerDocument())
            } finally { store.closeLearnerReplacement() }
        }
    }

    @Test fun missingOwnersAndChangedTeachingOrItemsRefuseFreshAndSavedTasks() {
        LocalStore.openInMemory(context()).use { store ->
            prepare(store)
            val task = open(store)
            val learner = store.v2LearnerView()
            for (owner in listOf(3, 6)) {
                assertNull(createCumulativeReviewAssignment(pack, learner.copy(completedUnitNumbers = learner.completedUnitNumbers - owner), 7, "missing-$owner", 0))
                val slot = task.slots.single { it.ownerUnit == owner }
                val changed = pack.copy(items = pack.items.map { if (it.id == slot.itemId) it.copy(feedback = it.feedback + " changed") else it })
                assertFalse(reviewAssignmentCurrent(changed, task))
                val stepId = pack.unit(owner)!!.objectives.single { it.id == slot.objectiveId }.instructionStepIds.last()
                val teaching = pack.copy(units = pack.units.map { u -> if (u.number != owner) u else u.copy(sessions = u.sessions.map { session ->
                    session.copy(steps = session.steps.map { if (it.id == stepId) it.copy(body = it.body + " changed") else it })
                }) })
                assertFalse(reviewAssignmentCurrent(teaching, task))
            }
            assertNull(createCumulativeReviewAssignment(pack, learner.copy(governedStepDigests = emptyMap()), 7, "missing-teaching", 0))
        }
    }

    @Test fun recentGenderMissRemainsAssignedThroughCorrectionRollbackBackupAndReopen() {
        val context = context(); val name = "unit7-gender-miss-${System.nanoTime()}"; val stage = File(context.cacheDir, name)
        try {
            LocalStore.openNamed(context, name).use { store ->
                prepare(store)
                val item = pack.item("u3-i-gender-дверь")!!
                val step = pack.unit(3)!!.sessions.flatMap { it.steps }.single { it.kind == "CHECK" && item.id in it.itemIds }
                store.v2RecordGovernedAttempt(prepareGovernedAttempt(pack, item.id, item.options.first { it != item.answer }, PRESCRIBED_CHECK, step.id, "gender-miss")!!)
                val task = open(store)
                assertEquals(item.id, task.slots[0].itemId)
                store.v2RecordGovernedAttempt(prepareGovernedAttempt(pack, item.id, item.answer, PRESCRIBED_CHECK, step.id, "gender-corrected")!!)
                assertEquals(task, open(store))
                assertTrue(completedReviewSlots(pack, task, store.v2LearnerView().reviewAnswers).isEmpty())
                task.slots.indices.forEach { answer(store, task, it) }
                val before = store.governedLearnerDocument()
                store.failAdvanceAfterCompletionForTests = true
                assertTrue(runCatching { advance(store, 7) }.isFailure)
                assertEquals(before, store.governedLearnerDocument())
                assertTrue(advance(store, 7))
                val document = store.governedLearnerDocument()
                val decoded = readGovernedBackup(archive(store).inputStream(), "dev.russiancompanion", pack.packId, pack.packVersion, stage)
                assertTrue(decoded is BackupReadResult.Valid)
                LocalStore.openInMemory(context).use { target ->
                    assertEquals(GovernedReplaceOutcome.REPLACED, target.replaceGovernedLearner((decoded as BackupReadResult.Valid).backup.document, emptyList()))
                    assertEquals(document, target.governedLearnerDocument())
                    assertEquals(task, open(target))
                    assertEquals((1..7).toSet(), currentCompletedUnitNumbers(pack, target.v2LearnerView()))
                }
            }
            LocalStore.openNamed(context, name).use { store ->
                assertEquals("u3-i-gender-дверь", open(store).slots[0].itemId)
                assertEquals((1..7).toSet(), currentCompletedUnitNumbers(pack, store.v2LearnerView()))
            }
        } finally { stage.deleteRecursively(); context.deleteDatabase(name); File(context.filesDir, "recordings-$name").deleteRecursively() }
    }

}
