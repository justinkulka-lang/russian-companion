package dev.russiancompanion.v2

import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.backup.*
import dev.russiancompanion.v2.curriculum.*
import dev.russiancompanion.v2.Unit9NounBehaviorFixtures.root
import dev.russiancompanion.v2.Unit9NounBehaviorFixtures.pack
import dev.russiancompanion.v2.Unit9NounBehaviorFixtures.context
import dev.russiancompanion.v2.Unit9NounBehaviorFixtures.document
import dev.russiancompanion.v2.Unit9NounBehaviorFixtures.v1Capture
import dev.russiancompanion.v2.Unit9NounBehaviorFixtures.v2Capture
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
    @Test fun exactPriorV2DeclarationRefusesSchemaElevenAndPreservesOwnerNineDownstream() {
        // Original contract on actual pre-literacy source; current refusal has separate direct coverage.
        with(RecordedLiteracyEpochScope.context(pack, 9)) {
        val old = RecordedIntakeCorrectionScope.load(root, "7e2296f067da861cbebdc9cf63bfd8e0ddd62436")
        // Preserve the exact original Unit9-only declaration proof before Unit10 v2.
        RecordedCurriculumConservationScope.beforeUnit10PresentReview(root, pack).let { pack ->
            val restoredDeclaration = pack.copy(units = pack.units.map { u -> if (u.number != 9) u else
                u.copy(advancement = u.advancement!!.copy(cumulativeReviewPolicyId = Unit9WiderCumulativeReviewPolicy.definition.id)) })
            assertEquals(old, restoredDeclaration)
        }
        LocalStore.openInMemory(context()).use { store ->
            assertEquals(GovernedReplaceOutcome.REPLACED, store.replaceGovernedLearner(document(v2Capture), emptyList()))
            val future = createCumulativeReviewAssignment(pack, store.v2LearnerView(), 9, "fresh-noun", 0)!!
            assertEquals(11, future.schema)
            assertTrue(reviewAssignmentCurrent(pack, future))
            assertFalse(reviewAssignmentCurrent(old, future))
            assertFalse(reviewAssignmentEligible(old, store.v2LearnerView(), future))
            val saved = store.openCumulativeReview(old, 9, store.learnerMutationEpoch())!!
            saved.slots.indices.forEach { answer(store, saved, it) }
            assertTrue(advance(store, 9, old))
            ordinary(store, 10, old)
            val downstream = store.openCumulativeReview(old, 10, store.learnerMutationEpoch())!!
            val owner = downstream.slots.single { it.ownerUnit == 9 }
            assertEquals(V2Validator.unitContentReviewDigest(old, 9), owner.unitDigest)
            assertNotEquals(V2Validator.unitContentReviewDigest(pack, 9), owner.unitDigest)
            assertTrue(reviewAssignmentCurrent(pack, downstream))
            assertTrue(reviewAssignmentEligible(pack, store.v2LearnerView(), downstream))
            val changed = pack.copy(items = pack.items.map { if (it.id == owner.itemId) it.copy(feedback = it.feedback + " changed") else it })
            assertFalse(reviewAssignmentCurrent(changed, downstream))
            val changedRule = pack.copy(units = pack.units.map { u -> if (u.number != 9) u else
                u.copy(advancement = u.advancement!!.copy(minimumDistinctChecks = u.advancement.minimumDistinctChecks + 1)) })
            assertFalse(reviewAssignmentCurrent(changedRule, downstream))
            assertFalse(9 in currentCompletedUnitNumbers(changedRule, store.v2LearnerView()))
        }
        }
    }

    @Test fun genuineV2PartialKeepsItsOldThreeSlotsAnswersAndCompletionAcrossReopen() {
        // Original contract on actual pre-literacy source; current refusal has separate direct coverage.
        with(RecordedLiteracyEpochScope.context(pack, 9)) {
        val original = document(v2Capture); val saved = original.reviewAssignments.single { it.targetUnit == 9 }
        assertEquals(5, saved.schema)
        assertEquals(listOf("obj-u1-decoding", "obj-u8-plural", "obj-u3-gender"), saved.slots.map { it.objectiveId })
        val ctx = context(); val name = "unit9-old-v2-${System.nanoTime()}"; val stage = File(ctx.cacheDir, name)
        try {
            LocalStore.openNamed(ctx, name).use { store ->
                assertEquals(GovernedReplaceOutcome.REPLACED, store.replaceGovernedLearner(original, emptyList()))
                assertEquals(original, store.governedLearnerDocument())
                assertEquals(saved, open(store))
                assertEquals(setOf(0), completedReviewSlots(pack, saved, store.v2LearnerView().reviewAnswers))
                for (n in 1..2) answer(store, saved, n)
                assertTrue(advance(store, 9))
                val completed = store.governedLearnerDocument()
                assertEquals(saved, completed.reviewAssignments.single { it.targetUnit == 9 })
                assertEquals(original.reviewAnswers, completed.reviewAnswers.take(original.reviewAnswers.size))
                assertEquals(saved.policyDigest, completed.unitCompletions.single { it.unitNumber == 9 }.reviewPolicyDigest)
                assertEquals((1..9).toSet(), currentCompletedUnitNumbers(pack, store.v2LearnerView()))
                val decoded = readGovernedBackup(archive(store).inputStream(), "dev.russiancompanion", pack.packId, pack.packVersion, stage)
                assertTrue(decoded is BackupReadResult.Valid)
                LocalStore.openInMemory(ctx).use { target ->
                    assertEquals(GovernedReplaceOutcome.REPLACED, target.replaceGovernedLearner((decoded as BackupReadResult.Valid).backup.document, emptyList()))
                    assertEquals(completed, target.governedLearnerDocument())
                    assertEquals(saved, open(target))
                }
            }
            LocalStore.openNamed(ctx, name).use { store ->
                assertEquals(saved, open(store)); assertEquals((1..9).toSet(), currentCompletedUnitNumbers(pack, store.v2LearnerView()))
            }
        } finally { stage.deleteRecursively(); ctx.deleteDatabase(name); File(ctx.filesDir, "recordings-$name").deleteRecursively() }
        }
    }

    @Test fun genuineV1StaysRegisteredUnderItsOwnHistoricalSourceAndCannotBeRelabelled() {
        val original = document(v1Capture); val saved = original.reviewAssignments.single { it.targetUnit == 9 }
        val old = RecordedIntakeCorrectionScope.load(root, "61c68cfcf20cd92a4b6c310c4abf8ff2284e36e5")
        assertEquals(1, saved.schema); assertEquals(Unit9CumulativeReviewPolicy.definition, cumulativeReviewAssignmentPolicy(saved))
        assertTrue(reviewAssignmentCurrent(old, saved))
        assertEquals(2, saved.slots.size)
        val relabelled = saved.copy(schema = 11).let { it.copy(digest = reviewAssignmentDigest(it)) }
        assertNull(cumulativeReviewAssignmentPolicy(relabelled))
        assertFalse(reviewAssignmentCurrent(pack, relabelled))
    }

    @Test fun wrongSchemasTargetsResponseRulesAndEpochsCannotBorrowNewCredit() {
        LocalStore.openInMemory(context()).use { store ->
            prepare(store); val task = open(store); assertEquals(11, task.schema)
            assertEquals(Unit6CumulativeReviewPolicy.definition.pool[1], Unit9NounBehaviorCumulativeReviewPolicy.definition.pool[0])
            assertEquals(Unit9WiderCumulativeReviewPolicy.definition.pool.drop(1), Unit9NounBehaviorCumulativeReviewPolicy.definition.pool.drop(1))
            for (schema in (1..10) + 12) {
                val bad = task.copy(schema = schema).let { it.copy(digest = reviewAssignmentDigest(it)) }
                assertFalse("schema $schema", reviewAssignmentCurrent(pack, bad))
                assertNotNull(reviewHistoryDefects(listOf(bad.copy(boundary = 0).let { it.copy(digest = reviewAssignmentDigest(it)) }), emptyList()))
            }
            for (bad in listOf(task.copy(targetUnit = 8), task.copy(responseRuleDigest = "0".repeat(64)), task.copy(policyDigest = "0".repeat(64))))
                assertFalse(reviewAssignmentCurrent(pack, bad.copy(digest = reviewAssignmentDigest(bad))))
            val old = document(v2Capture).reviewAssignments.single { it.targetUnit == 9 }
            val invalidOld = old.copy(schema = 11).let { it.copy(digest = reviewAssignmentDigest(it)) }
            assertNull(cumulativeReviewAssignmentPolicy(invalidOld))
            val unit8 = store.cumulativeReviewHistory().first.single { it.targetUnit == 8 }
            val invalid8 = unit8.copy(schema = 11).let { it.copy(digest = reviewAssignmentDigest(it)) }
            assertNull(cumulativeReviewAssignmentPolicy(invalid8))
            val epoch = store.learnerMutationEpoch(); val before = store.governedLearnerDocument()
            store.openLearnerReplacement()
            try {
                assertNull(store.openCumulativeReview(pack, 9, epoch))
                assertFalse(store.answerCumulativeReview(pack, task.id, 0, pack.item(task.slots[0].itemId)!!.answer, epoch))
                assertEquals(before, store.governedLearnerDocument())
            } finally { store.closeLearnerReplacement() }
        }
    }

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
