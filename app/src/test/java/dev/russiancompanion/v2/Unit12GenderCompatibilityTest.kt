package dev.russiancompanion.v2

import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.backup.*
import dev.russiancompanion.v2.curriculum.*
import dev.russiancompanion.v2.Unit12GenderFixtures.root
import dev.russiancompanion.v2.Unit12GenderFixtures.pack
import dev.russiancompanion.v2.Unit12GenderFixtures.context
import dev.russiancompanion.v2.Unit12GenderFixtures.document
import dev.russiancompanion.v2.Unit12GenderFixtures.v1Capture
import dev.russiancompanion.v2.Unit12GenderFixtures.advance
import dev.russiancompanion.v2.Unit12GenderFixtures.ordinary
import dev.russiancompanion.v2.Unit12GenderFixtures.prepare
import dev.russiancompanion.v2.Unit12GenderFixtures.open
import dev.russiancompanion.v2.Unit12GenderFixtures.answer
import dev.russiancompanion.v2.Unit12GenderFixtures.archive
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class Unit12GenderCompatibilityTest {
    @Test fun exactPriorDeclarationRefusesSchemaFourteenAndPreservesBothDownstreamVersions() {
        val old = RecordedIntakeCorrectionScope.load(root, "23b9179eaf54844f0b38074b2db56c7113c38c00")
        val priorDeclarationOnly = pack.copy(units = pack.units.map { u -> if (u.number != 12) u else
            u.copy(advancement = u.advancement!!.copy(cumulativeReviewPolicyId = Unit12CumulativeReviewPolicy.definition.id)) })
        assertEquals("The genuine old pack differs only by the Unit12 declaration", old, priorDeclarationOnly)
        for (legacy13 in listOf(false, true)) {
            val producer = if (!legacy13) old else old.copy(units = old.units.map { u -> if (u.number != 13) u else
                u.copy(advancement = u.advancement!!.copy(cumulativeReviewPolicyId = Unit13CumulativeReviewPolicy.definition.id)) })
            LocalStore.openInMemory(context()).use { store ->
                val original = document(v1Capture)
                assertEquals(GovernedReplaceOutcome.REPLACED, store.replaceGovernedLearner(original, emptyList()))
                val future = createCumulativeReviewAssignment(pack, store.v2LearnerView(), 12, "future-unit12", 0)!!
                assertEquals(14, future.schema)
                assertTrue(reviewAssignmentCurrent(pack, future))
                assertFalse(reviewAssignmentCurrent(old, future))
                assertFalse(reviewAssignmentEligible(old, store.v2LearnerView(), future))
                val saved = original.reviewAssignments.single { it.targetUnit == 12 }
                assertEquals(saved, store.openCumulativeReview(producer, 12, store.learnerMutationEpoch()))
                saved.slots.forEachIndexed { index, slot -> assertTrue(store.answerCumulativeReview(producer,
                    saved.id, index, producer.item(slot.itemId)!!.answer, store.learnerMutationEpoch())) }
                assertTrue(advance(store, 12, producer))
                ordinary(store, 13, producer)
                val downstream = store.openCumulativeReview(producer, 13, store.learnerMutationEpoch())!!
                assertEquals(if (legacy13) 1 else 6, downstream.schema)
                assertEquals(V2Validator.unitContentReviewDigest(producer, 12), downstream.slots.single { it.ownerUnit == 12 }.unitDigest)
                assertNotEquals(V2Validator.unitContentReviewDigest(pack, 12), downstream.slots.single { it.ownerUnit == 12 }.unitDigest)
                assertTrue(reviewAssignmentCurrent(pack, downstream))
                assertTrue(reviewAssignmentEligible(pack, store.v2LearnerView(), downstream))
                val changed = pack.copy(items = pack.items.map { if (it.id == downstream.slots[1].itemId) it.copy(feedback = it.feedback + " changed") else it })
                assertFalse(reviewAssignmentCurrent(changed, downstream))
                val changedRule = pack.copy(units = pack.units.map { u -> if (u.number != 12) u else
                    u.copy(advancement = u.advancement!!.copy(minimumDistinctChecks = u.advancement.minimumDistinctChecks + 1)) })
                assertFalse(reviewAssignmentCurrent(changedRule, downstream))
                val changedTeaching = pack.copy(units = pack.units.map { u -> if (u.number != 12) u else
                    u.copy(sessions = u.sessions.map { session -> session.copy(steps = session.steps.map { step ->
                        if (step.kind == "TEACH") step.copy(body = step.body + " changed") else step }) }) })
                assertFalse(reviewAssignmentCurrent(changedTeaching, downstream))
                assertFalse(12 in currentCompletedUnitNumbers(changedRule, store.v2LearnerView()))
                downstream.slots.indices.forEach { answer(store, downstream, it) }
                assertTrue(advance(store, 13))
                assertTrue(13 in currentCompletedUnitNumbers(pack, store.v2LearnerView()))
            }
        }
    }

    @Test fun genuineHistoricalUnit13SchemaOneOwner12BindingsStayCurrentWithoutRelabelling() {
        for (name in listOf("PARTIAL_REVIEW", "COMPLETED_UNIT13")) {
            val original = document("audit/independent-gpt61-2026-10-12/ia003-unit13-past-teaching/$name.zip")
            val saved = original.reviewAssignments.single { it.targetUnit == 13 }
            assertEquals(1, saved.schema)
            assertEquals(Unit13CumulativeReviewPolicy.definition.id, saved.policyId)
            assertTrue(reviewAssignmentCurrent(pack, saved))
            LocalStore.openInMemory(context()).use { store ->
                assertEquals(GovernedReplaceOutcome.REPLACED, store.replaceGovernedLearner(original, emptyList()))
                assertEquals(original, store.governedLearnerDocument())
                assertEquals(saved, store.cumulativeReviewHistory().first.single { it.targetUnit == 13 })
                // Old prerequisites may be stale; current eligibility/advancement is not inferred.
            }
        }
    }

    @Test fun wrongSchemasResponseTargetAndEpochCannotBorrowNewCredit() {
        LocalStore.openInMemory(context()).use { store ->
            prepare(store)
            val task = open(store)
            assertEquals(14, task.schema)
            for (schema in (1..13) + 15) {
                val bad = task.copy(schema = schema).let { it.copy(digest = reviewAssignmentDigest(it)) }
                assertFalse("schema $schema", reviewAssignmentCurrent(pack, bad))
                assertNotNull(reviewHistoryDefects(listOf(bad.copy(boundary = 0).let { it.copy(digest = reviewAssignmentDigest(it)) }), emptyList()))
            }
            for (bad in listOf(task.copy(targetUnit = 6), task.copy(responseRuleDigest = "0".repeat(64)), task.copy(policyDigest = "0".repeat(64)))) {
                val signed = bad.copy(digest = reviewAssignmentDigest(bad))
                assertFalse(reviewAssignmentCurrent(pack, signed))
            }
            val unit9 = store.cumulativeReviewHistory().first.single { it.targetUnit == 9 }
            val wrongUnit9Schema = unit9.copy(schema = 14).let { it.copy(digest = reviewAssignmentDigest(it)) }
            assertFalse(reviewAssignmentCurrent(pack, wrongUnit9Schema))
            assertNotNull(reviewHistoryDefects(listOf(wrongUnit9Schema.copy(boundary = 0).let { it.copy(digest = reviewAssignmentDigest(it)) }), emptyList()))
            val epoch = store.learnerMutationEpoch()
            val before = store.governedLearnerDocument()
            store.openLearnerReplacement()
            try {
                assertNull(store.openCumulativeReview(pack, 12, epoch))
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
            for (owner in listOf(3, 11)) {
                assertNull(createCumulativeReviewAssignment(pack, learner.copy(completedUnitNumbers = learner.completedUnitNumbers - owner), 12, "missing-$owner", 0))
                val slot = task.slots.single { it.ownerUnit == owner }
                val changed = pack.copy(items = pack.items.map { if (it.id == slot.itemId) it.copy(feedback = it.feedback + " changed") else it })
                assertFalse(reviewAssignmentCurrent(changed, task))
                val stepId = pack.unit(owner)!!.objectives.single { it.id == slot.objectiveId }.instructionStepIds.last()
                val teaching = pack.copy(units = pack.units.map { u -> if (u.number != owner) u else u.copy(sessions = u.sessions.map { session ->
                    session.copy(steps = session.steps.map { if (it.id == stepId) it.copy(body = it.body + " changed") else it })
                }) })
                assertFalse(reviewAssignmentCurrent(teaching, task))
            }
            assertNull(createCumulativeReviewAssignment(pack, learner.copy(governedStepDigests = emptyMap()), 12, "missing-teaching", 0))
        }
    }

    @Test fun recentGenderMissRemainsAssignedThroughCorrectionRollbackBackupAndReopen() {
        val context = context(); val name = "unit12-gender-miss-${System.nanoTime()}"; val stage = File(context.cacheDir, name)
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
                answer(store, task, 0)
                assertEquals(before, store.governedLearnerDocument())
                store.failAdvanceAfterCompletionForTests = true
                assertTrue(runCatching { advance(store, 12) }.isFailure)
                assertEquals(before, store.governedLearnerDocument())
                assertTrue(advance(store, 12))
                val document = store.governedLearnerDocument()
                val decoded = readGovernedBackup(archive(store).inputStream(), "dev.russiancompanion", pack.packId, pack.packVersion, stage)
                assertTrue(decoded is BackupReadResult.Valid)
                LocalStore.openInMemory(context).use { target ->
                    assertEquals(GovernedReplaceOutcome.REPLACED, target.replaceGovernedLearner((decoded as BackupReadResult.Valid).backup.document, emptyList()))
                    assertEquals(document, target.governedLearnerDocument())
                    assertEquals(task, open(target))
                    assertEquals((1..12).toSet(), currentCompletedUnitNumbers(pack, target.v2LearnerView()))
                }
            }
            LocalStore.openNamed(context, name).use { store ->
                assertEquals("u3-i-gender-дверь", open(store).slots[0].itemId)
                assertEquals((1..12).toSet(), currentCompletedUnitNumbers(pack, store.v2LearnerView()))
            }
        } finally { stage.deleteRecursively(); context.deleteDatabase(name); File(context.filesDir, "recordings-$name").deleteRecursively() }
    }

    @Test fun genuineSchemaOnePartialCompletesWithoutAddingNounGenderOrRelabelling() {
        val original = document(v1Capture); val saved = original.reviewAssignments.single { it.targetUnit == 12 }
        assertEquals(1, saved.schema)
        assertEquals(listOf("obj-u1-decoding", "obj-u11-past-aspect"), saved.slots.map { it.objectiveId })
        val context = context(); val name = "unit12-genuine-v1-${System.nanoTime()}"
        try {
            LocalStore.openNamed(context, name).use { store ->
                assertEquals(GovernedReplaceOutcome.REPLACED, store.replaceGovernedLearner(original, emptyList()))
                assertEquals(original, store.governedLearnerDocument())
                assertEquals(saved, open(store))
                assertEquals(setOf(0), completedReviewSlots(pack, saved, store.v2LearnerView().reviewAnswers))
                val relabelled = saved.copy(schema = 14).let { it.copy(digest = reviewAssignmentDigest(it)) }
                assertFalse(reviewAssignmentCurrent(pack, relabelled))
                answer(store, saved, 1)
                assertTrue(advance(store, 12))
                val current = store.governedLearnerDocument()
                assertEquals(saved, current.reviewAssignments.single { it.targetUnit == 12 })
                assertEquals(original.reviewAnswers, current.reviewAnswers.take(original.reviewAnswers.size))
                assertEquals(saved.policyDigest, current.unitCompletions.single { it.unitNumber == 12 }.reviewPolicyDigest)
                assertEquals((1..12).toSet(), currentCompletedUnitNumbers(pack, store.v2LearnerView()))
            }
            LocalStore.openNamed(context, name).use { store ->
                assertEquals(saved, open(store)); assertEquals((1..12).toSet(), currentCompletedUnitNumbers(pack, store.v2LearnerView()))
            }
        } finally { context.deleteDatabase(name); File(context.filesDir, "recordings-$name").deleteRecursively() }
    }

    @Test fun eachGenderMissCanAllocateWhileOptionalAndMalformedHistoryCannotSelect() {
        LocalStore.openInMemory(context()).use { store ->
            prepare(store)
            val ids = Unit12GenderCumulativeReviewPolicy.definition.pool[0].candidateItemIds
            assertEquals(ids.first(), createCumulativeReviewAssignment(pack, store.v2LearnerView(), 12, "least-recent-gender", 0)!!.slots[0].itemId)
            assertEquals("u11-i-past-aspect-1", createCumulativeReviewAssignment(pack, store.v2LearnerView(), 12, "least-recent-past", 0)!!.slots[1].itemId)
            for (id in ids) {
                recordMiss(store, id)
                assertEquals(id, createCumulativeReviewAssignment(pack, store.v2LearnerView(), 12, "miss-$id", 0)!!.slots[0].itemId)
            }
            val contrastIds = Unit12GenderCumulativeReviewPolicy.definition.pool[1].candidateItemIds
            assertEquals(listOf("u11-i-past-aspect-1", "u11-i-past-aspect-2"), contrastIds)
            for (id in contrastIds) {
                recordMiss(store, id)
                assertEquals(id, createCumulativeReviewAssignment(pack, store.v2LearnerView(), 12, "contrast-$id", 0)!!.slots[1].itemId)
            }
            for (slot in 0..1) {
                val selectedIds = Unit12GenderCumulativeReviewPolicy.definition.pool[slot].candidateItemIds
                val item = pack.item(selectedIds.first())!!
                store.v2RecordGovernedAttempt(prepareGovernedAttempt(pack, item.id, item.options.first { it != item.answer },
                    OPTIONAL_PRACTICE, practiceStepId("mixed"), "optional-miss")!!)
                val view = store.v2LearnerView(); val candidates = selectedIds.map { pack.item(it)!! }
                assertEquals(selectedIds.last(), createCumulativeReviewAssignment(pack, view, 12, "ignore-optional-$slot", 0)!!.slots[slot].itemId)
                assertEquals(selectedIds.first(), recentReviewCandidate(pack, view.copy(governedAttempts = view.governedAttempts!!.map { it.copy(itemDigest = "0".repeat(64)) }), candidates)!!.id)
                assertEquals(selectedIds.first(), recentReviewCandidate(pack, view.copy(governedAttempts = view.governedAttempts!!.map { it.copy(acceptedOrder = 1) }), candidates)!!.id)
            }
        }
    }
    private fun recordMiss(store: LocalStore, id: String) {
        val item = pack.item(id)!!
        val step = pack.units.flatMap { it.sessions }.flatMap { it.steps }.single { it.kind == "CHECK" && id in it.itemIds }
        store.v2RecordGovernedAttempt(prepareGovernedAttempt(pack, id, item.options.first { it != item.answer }, PRESCRIBED_CHECK, step.id, "gender-miss")!!)
    }
}
