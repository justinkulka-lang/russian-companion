package dev.russiancompanion.v2

import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.backup.*
import dev.russiancompanion.v2.curriculum.*
import dev.russiancompanion.v2.Unit11NounFixtures.root
import dev.russiancompanion.v2.Unit11NounFixtures.pack
import dev.russiancompanion.v2.Unit11NounFixtures.context
import dev.russiancompanion.v2.Unit11NounFixtures.document
import dev.russiancompanion.v2.Unit11NounFixtures.advance
import dev.russiancompanion.v2.Unit11NounFixtures.ordinary
import dev.russiancompanion.v2.Unit11NounFixtures.prepare
import dev.russiancompanion.v2.Unit11NounFixtures.open
import dev.russiancompanion.v2.Unit11NounFixtures.answer
import dev.russiancompanion.v2.Unit11NounFixtures.archive
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class Unit11NounCompatibilityTest {

    @Test fun wrongSchemasResponseTargetAndEpochCannotBorrowNewCredit() {
        LocalStore.openInMemory(context()).use { store ->
            prepare(store)
            val task = open(store)
            assertEquals(13, task.schema)
            for (schema in (1..12) + 14) {
                val bad = task.copy(schema = schema).let { it.copy(digest = reviewAssignmentDigest(it)) }
                assertFalse("schema $schema", reviewAssignmentCurrent(pack, bad))
                assertNotNull(reviewHistoryDefects(listOf(bad.copy(boundary = 0).let { it.copy(digest = reviewAssignmentDigest(it)) }), emptyList()))
            }
            for (bad in listOf(task.copy(targetUnit = 6), task.copy(responseRuleDigest = "0".repeat(64)), task.copy(policyDigest = "0".repeat(64)))) {
                val signed = bad.copy(digest = reviewAssignmentDigest(bad))
                assertFalse(reviewAssignmentCurrent(pack, signed))
            }
            val unit9 = store.cumulativeReviewHistory().first.single { it.targetUnit == 9 }
            val wrongUnit9Schema = unit9.copy(schema = 13).let { it.copy(digest = reviewAssignmentDigest(it)) }
            assertFalse(reviewAssignmentCurrent(pack, wrongUnit9Schema))
            assertNotNull(reviewHistoryDefects(listOf(wrongUnit9Schema.copy(boundary = 0).let { it.copy(digest = reviewAssignmentDigest(it)) }), emptyList()))
            val epoch = store.learnerMutationEpoch()
            val before = store.governedLearnerDocument()
            store.openLearnerReplacement()
            try {
                assertNull(store.openCumulativeReview(pack, 11, epoch))
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
            for (owner in listOf(5, 10)) {
                assertNull(createCumulativeReviewAssignment(pack, learner.copy(completedUnitNumbers = learner.completedUnitNumbers - owner), 11, "missing-$owner", 0))
                val slot = task.slots.single { it.ownerUnit == owner }
                val changed = pack.copy(items = pack.items.map { if (it.id == slot.itemId) it.copy(feedback = it.feedback + " changed") else it })
                assertFalse(reviewAssignmentCurrent(changed, task))
                val stepId = pack.unit(owner)!!.objectives.single { it.id == slot.objectiveId }.instructionStepIds.last()
                val teaching = pack.copy(units = pack.units.map { u -> if (u.number != owner) u else u.copy(sessions = u.sessions.map { session ->
                    session.copy(steps = session.steps.map { if (it.id == stepId) it.copy(body = it.body + " changed") else it })
                }) })
                assertFalse(reviewAssignmentCurrent(teaching, task))
            }
            assertNull(createCumulativeReviewAssignment(pack, learner.copy(governedStepDigests = emptyMap()), 11, "missing-teaching", 0))
        }
    }

    @Test fun recentNounMissRemainsAssignedThroughCorrectionRollbackBackupAndReopen() {
        val context = context(); val name = "unit11-noun-miss-${System.nanoTime()}"; val stage = File(context.cacheDir, name)
        try {
            LocalStore.openNamed(context, name).use { store ->
                prepare(store)
                val item = pack.item("u5-i-decl-окно")!!
                val step = pack.unit(5)!!.sessions.flatMap { it.steps }.single { it.kind == "CHECK" && item.id in it.itemIds }
                store.v2RecordGovernedAttempt(prepareGovernedAttempt(pack, item.id, item.options.first { it != item.answer }, PRESCRIBED_CHECK, step.id, "noun-miss")!!)
                val task = open(store)
                assertEquals(item.id, task.slots[0].itemId)
                store.v2RecordGovernedAttempt(prepareGovernedAttempt(pack, item.id, item.answer, PRESCRIBED_CHECK, step.id, "noun-corrected")!!)
                assertEquals(task, open(store))
                assertTrue(completedReviewSlots(pack, task, store.v2LearnerView().reviewAnswers).isEmpty())
                task.slots.indices.forEach { answer(store, task, it) }
                val before = store.governedLearnerDocument()
                answer(store, task, 0)
                assertEquals(before, store.governedLearnerDocument())
                store.failAdvanceAfterCompletionForTests = true
                assertTrue(runCatching { advance(store, 11) }.isFailure)
                assertEquals(before, store.governedLearnerDocument())
                assertTrue(advance(store, 11))
                val document = store.governedLearnerDocument()
                val decoded = readGovernedBackup(archive(store).inputStream(), "dev.russiancompanion", pack.packId, pack.packVersion, stage)
                assertTrue(decoded is BackupReadResult.Valid)
                LocalStore.openInMemory(context).use { target ->
                    assertEquals(GovernedReplaceOutcome.REPLACED, target.replaceGovernedLearner((decoded as BackupReadResult.Valid).backup.document, emptyList()))
                    assertEquals(document, target.governedLearnerDocument())
                    assertEquals(task, open(target))
                    assertEquals((1..11).toSet(), currentCompletedUnitNumbers(pack, target.v2LearnerView()))
                }
            }
            LocalStore.openNamed(context, name).use { store ->
                assertEquals("u5-i-decl-окно", open(store).slots[0].itemId)
                assertEquals((1..11).toSet(), currentCompletedUnitNumbers(pack, store.v2LearnerView()))
            }
        } finally { stage.deleteRecursively(); context.deleteDatabase(name); File(context.filesDir, "recordings-$name").deleteRecursively() }
    }


    @Test fun eachNounMissCanAllocateWhileOptionalAndMalformedHistoryCannotSelect() {
        LocalStore.openInMemory(context()).use { store ->
            prepare(store)
            val ids = Unit11NounCumulativeReviewPolicy.definition.pool[0].candidateItemIds
            for (id in ids) {
                recordMiss(store, id)
                assertEquals(id, createCumulativeReviewAssignment(pack, store.v2LearnerView(), 11, "miss-$id", 0)!!.slots[0].itemId)
            }
            val contrastIds = Unit11NounCumulativeReviewPolicy.definition.pool[1].candidateItemIds
            assertEquals(listOf("u10-i-have-1", "u10-i-have-2", "u10-i-have-3", "u10-i-have-4"), contrastIds)
            for (id in contrastIds) {
                recordMiss(store, id)
                assertEquals(id, createCumulativeReviewAssignment(pack, store.v2LearnerView(), 11, "contrast-$id", 0)!!.slots[1].itemId)
            }
            for (slot in 0..1) {
                val selectedIds = Unit11NounCumulativeReviewPolicy.definition.pool[slot].candidateItemIds
                val item = pack.item(selectedIds.first())!!
                store.v2RecordGovernedAttempt(prepareGovernedAttempt(pack, item.id, item.options.first { it != item.answer },
                    OPTIONAL_PRACTICE, practiceStepId("mixed"), "optional-miss")!!)
                val view = store.v2LearnerView(); val candidates = selectedIds.map { pack.item(it)!! }
                assertEquals(selectedIds.last(), createCumulativeReviewAssignment(pack, view, 11, "ignore-optional-$slot", 0)!!.slots[slot].itemId)
                assertEquals(selectedIds.first(), recentReviewCandidate(pack, view.copy(governedAttempts = view.governedAttempts!!.map { it.copy(itemDigest = "0".repeat(64)) }), candidates)!!.id)
                assertEquals(selectedIds.first(), recentReviewCandidate(pack, view.copy(governedAttempts = view.governedAttempts!!.map { it.copy(acceptedOrder = 1) }), candidates)!!.id)
            }
        }
    }
    private fun recordMiss(store: LocalStore, id: String) {
        val item = pack.item(id)!!
        val step = pack.units.flatMap { it.sessions }.flatMap { it.steps }.single { it.kind == "CHECK" && id in it.itemIds }
        store.v2RecordGovernedAttempt(prepareGovernedAttempt(pack, id, item.options.first { it != item.answer }, PRESCRIBED_CHECK, step.id, "noun-miss")!!)
    }
}
