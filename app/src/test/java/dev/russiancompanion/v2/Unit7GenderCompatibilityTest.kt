package dev.russiancompanion.v2

import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.backup.*
import dev.russiancompanion.v2.curriculum.*
import dev.russiancompanion.v2.Unit7GenderFixtures.root
import dev.russiancompanion.v2.Unit7GenderFixtures.pack
import dev.russiancompanion.v2.Unit7GenderFixtures.context
import dev.russiancompanion.v2.Unit7GenderFixtures.document
import dev.russiancompanion.v2.Unit7GenderFixtures.v1Capture
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
    @Test fun exactPriorDeclarationRefusesSchemaNineAndPreservesDownstreamOwnerIdentity() {
        // Original contract on actual pre-literacy source; current refusal has separate direct coverage.
        with(RecordedLiteracyEpochScope.context(pack, 7)) {
        val old = RecordedIntakeCorrectionScope.load(root, "38542882cd9ab2e075691eca19442ddfabd17b6b")
        RecordedCurriculumConservationScope.beforeUnit8PresentReview(root, pack).let { pack ->
        val priorDeclarationOnly = pack.copy(units = pack.units.map { u -> if (u.number != 7) u else
            u.copy(advancement = u.advancement!!.copy(cumulativeReviewPolicyId = Unit7CumulativeReviewPolicy.definition.id)) })
        assertEquals("The genuine old pack differs only by the Unit7 declaration", old, priorDeclarationOnly)
        }
        LocalStore.openInMemory(context()).use { store ->
            val original = document(v1Capture)
            assertEquals(GovernedReplaceOutcome.REPLACED, store.replaceGovernedLearner(original, emptyList()))
            val future = createCumulativeReviewAssignment(pack, store.v2LearnerView(), 7, "future-unit7", 0)!!
            assertEquals(9, future.schema)
            assertTrue(reviewAssignmentCurrent(pack, future))
            assertFalse(reviewAssignmentCurrent(old, future))
            assertFalse(reviewAssignmentEligible(old, store.v2LearnerView(), future))
            val saved = original.reviewAssignments.single { it.targetUnit == 7 }
            assertEquals(saved, store.openCumulativeReview(old, 7, store.learnerMutationEpoch()))
            saved.slots.forEachIndexed { index, slot -> assertTrue(store.answerCumulativeReview(old, saved.id, index,
                old.item(slot.itemId)!!.answer, store.learnerMutationEpoch())) }
            assertTrue(advance(store, 7, old))
            ordinary(store, 8, old)
            val downstream = store.openCumulativeReview(old, 8, store.learnerMutationEpoch())!!
            assertEquals(V2Validator.unitContentReviewDigest(old, 7), downstream.slots.single { it.ownerUnit == 7 }.unitDigest)
            assertNotEquals(V2Validator.unitContentReviewDigest(pack, 7), downstream.slots.single { it.ownerUnit == 7 }.unitDigest)
            assertTrue(reviewAssignmentCurrent(pack, downstream))
            assertTrue(reviewAssignmentEligible(pack, store.v2LearnerView(), downstream))
            val changed = pack.copy(items = pack.items.map { if (it.id == downstream.slots[1].itemId) it.copy(feedback = it.feedback + " changed") else it })
            assertFalse(reviewAssignmentCurrent(changed, downstream))
            val changedRule = pack.copy(units = pack.units.map { u -> if (u.number != 7) u else
                u.copy(advancement = u.advancement!!.copy(minimumDistinctChecks = u.advancement.minimumDistinctChecks + 1)) })
            assertFalse(reviewAssignmentCurrent(changedRule, downstream))
            assertFalse(7 in currentCompletedUnitNumbers(changedRule, store.v2LearnerView()))
        }
        }
    }

    @Test fun wrongSchemasResponseTargetAndEpochCannotBorrowNewCredit() {
        LocalStore.openInMemory(context()).use { store ->
            prepare(store)
            val task = open(store)
            assertEquals(9, task.schema)
            for (schema in (1..8) + 10) {
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

    @Test fun genuineSchemaOnePartialCompletesWithoutAddingGenderOrRelabelling() {
        // Original contract on actual pre-literacy source; current refusal has separate direct coverage.
        with(RecordedLiteracyEpochScope.context(pack, 7)) {
        val original = document(v1Capture); val saved = original.reviewAssignments.single { it.targetUnit == 7 }
        assertEquals(1, saved.schema)
        assertEquals(listOf("obj-u1-decoding", "obj-u6-animacy"), saved.slots.map { it.objectiveId })
        val context = context(); val name = "unit7-genuine-v1-${System.nanoTime()}"
        try {
            LocalStore.openNamed(context, name).use { store ->
                assertEquals(GovernedReplaceOutcome.REPLACED, store.replaceGovernedLearner(original, emptyList()))
                assertEquals(original, store.governedLearnerDocument())
                assertEquals(saved, open(store))
                assertEquals(setOf(0), completedReviewSlots(pack, saved, store.v2LearnerView().reviewAnswers))
                val relabelled = saved.copy(schema = 9).let { it.copy(digest = reviewAssignmentDigest(it)) }
                assertFalse(reviewAssignmentCurrent(pack, relabelled))
                answer(store, saved, 1)
                assertTrue(advance(store, 7))
                val current = store.governedLearnerDocument()
                assertEquals(saved, current.reviewAssignments.single { it.targetUnit == 7 })
                assertEquals(original.reviewAnswers, current.reviewAnswers.take(original.reviewAnswers.size))
                assertEquals(saved.policyDigest, current.unitCompletions.single { it.unitNumber == 7 }.reviewPolicyDigest)
                assertEquals((1..7).toSet(), currentCompletedUnitNumbers(pack, store.v2LearnerView()))
            }
            LocalStore.openNamed(context, name).use { store ->
                assertEquals(saved, open(store)); assertEquals((1..7).toSet(), currentCompletedUnitNumbers(pack, store.v2LearnerView()))
            }
        } finally { context.deleteDatabase(name); File(context.filesDir, "recordings-$name").deleteRecursively() }
        }
    }
}
