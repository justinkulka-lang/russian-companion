package dev.russiancompanion.v2

import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.backup.*
import dev.russiancompanion.v2.curriculum.*
import dev.russiancompanion.v2.Unit6PresentFixtures.context
import dev.russiancompanion.v2.Unit6PresentFixtures.prepare
import dev.russiancompanion.v2.Unit6PresentFixtures.open
import dev.russiancompanion.v2.Unit6PresentFixtures.answer
import dev.russiancompanion.v2.Unit6PresentFixtures.advance
import dev.russiancompanion.v2.Unit6PresentFixtures.archive
import dev.russiancompanion.v2.Unit6PresentFixtures.document
import dev.russiancompanion.v2.Unit6PresentFixtures.v2Capture
import dev.russiancompanion.v2.Unit6PresentFixtures.pack
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class Unit6PresentCompatibilityTest {
    @Test fun wrongVersionsUntaughtChangedOwnersAndOldEpochCannotBorrowNewCredit() {
        LocalStore.openInMemory(context()).use { store ->
            prepare(store)
            val task = open(store)
            assertEquals(8, task.schema)
            for (schema in (1..7) + 9) {
                val bad = task.copy(schema = schema).let { it.copy(digest = reviewAssignmentDigest(it)) }
                assertFalse("schema $schema", reviewAssignmentCurrent(pack, bad))
                assertNotNull(reviewHistoryDefects(listOf(bad.copy(boundary = 0).let { it.copy(digest = reviewAssignmentDigest(it)) }), emptyList()))
            }
            val wrongTarget = task.copy(targetUnit = 5).let { it.copy(digest = reviewAssignmentDigest(it)) }
            assertFalse(reviewAssignmentCurrent(pack, wrongTarget))
            val learner = store.v2LearnerView()
            assertNull(createCumulativeReviewAssignment(pack, learner.copy(completedUnitNumbers = learner.completedUnitNumbers - 4), 6, "missing-owner", 0))
            assertNull(createCumulativeReviewAssignment(pack, learner.copy(governedStepDigests = emptyMap()), 6, "missing-teaching", 0))
            val changedItem = pack.copy(items = pack.items.map { if (it.id == task.slots[0].itemId) it.copy(feedback = it.feedback + " changed") else it })
            assertFalse(reviewAssignmentCurrent(changedItem, task))
            val changedTeaching = pack.copy(units = pack.units.map { unit -> if (unit.number != 4) unit else unit.copy(sessions = unit.sessions.map { session ->
                session.copy(steps = session.steps.map { if (it.id == "u4-st-teach-present-говорить") it.copy(body = it.body + " changed") else it })
            }) })
            assertFalse(reviewAssignmentCurrent(changedTeaching, task))
            val epoch = store.learnerMutationEpoch()
            val before = store.cumulativeReviewHistory()
            store.openLearnerReplacement()
            try {
                assertNull(store.openCumulativeReview(pack, 6, epoch))
                assertFalse(store.answerCumulativeReview(pack, task.id, 0, pack.item(task.slots[0].itemId)!!.answer, epoch))
                assertEquals(before, store.cumulativeReviewHistory())
            } finally { store.closeLearnerReplacement() }
        }
    }

    @Test fun recentMissIsImmutableThroughOrdinaryCorrectionBackupAndActualRollback() {
        val context = context()
        val name = "unit6-miss-${System.nanoTime()}"
        val stage = File(context.cacheDir, name)
        try {
            LocalStore.openNamed(context, name).use { store ->
                prepare(store)
                val item = pack.item("u4-i-verb-писать-вы")!!
                val step = pack.unit(4)!!.sessions.flatMap { it.steps }.single { it.kind == "CHECK" && item.id in it.itemIds }
                store.v2RecordGovernedAttempt(prepareGovernedAttempt(pack, item.id, item.options.first { it != item.answer }, PRESCRIBED_CHECK, step.id, "miss")!!)
                val task = open(store)
                assertEquals(item.id, task.slots[0].itemId)
                store.v2RecordGovernedAttempt(prepareGovernedAttempt(pack, item.id, item.answer, PRESCRIBED_CHECK, step.id, "correction")!!)
                assertEquals(task, open(store))
                assertTrue(completedReviewSlots(pack, task, store.v2LearnerView().reviewAnswers).isEmpty())
                task.slots.indices.forEach { answer(store, task, it) }
                val before = store.governedLearnerDocument()
                store.failAdvanceAfterCompletionForTests = true
                assertTrue(runCatching { advance(store, 6) }.isFailure)
                assertEquals(before, store.governedLearnerDocument())
                assertTrue(advance(store, 6))
                val document = store.governedLearnerDocument()
                val decoded = readGovernedBackup(archive(store).inputStream(), "dev.russiancompanion", pack.packId, pack.packVersion, stage)
                assertTrue(decoded is BackupReadResult.Valid)
                LocalStore.openInMemory(context).use { target ->
                    assertEquals(GovernedReplaceOutcome.REPLACED, target.replaceGovernedLearner((decoded as BackupReadResult.Valid).backup.document, emptyList()))
                    assertEquals(document, target.governedLearnerDocument())
                    assertEquals(task, open(target))
                    assertEquals((1..6).toSet(), currentCompletedUnitNumbers(pack, target.v2LearnerView()))
                }
            }
            LocalStore.openNamed(context, name).use { store ->
                assertEquals("u4-i-verb-писать-вы", open(store).slots[0].itemId)
                assertEquals((1..6).toSet(), currentCompletedUnitNumbers(pack, store.v2LearnerView()))
            }
        } finally { stage.deleteRecursively(); context.deleteDatabase(name); File(context.filesDir, "recordings-$name").deleteRecursively() }
    }

    @Test fun genuineV2PartialCompletesWithoutRelabellingUnderNewDeclaration() {
        val original = document(v2Capture)
        val task = original.reviewAssignments.single { it.targetUnit == 6 }
        assertEquals(4, task.schema)
        assertEquals(Unit6WiderCumulativeReviewPolicy.definition.id, task.policyId)
        val context = context(); val name = "unit6-v2-${System.nanoTime()}"
        try {
            LocalStore.openNamed(context, name).use { store ->
                assertEquals(GovernedReplaceOutcome.REPLACED, store.replaceGovernedLearner(original, emptyList()))
                assertEquals(original, store.governedLearnerDocument())
                assertEquals(task, open(store))
                assertEquals(setOf(0), completedReviewSlots(pack, task, store.v2LearnerView().reviewAnswers))
                val relabelled = task.copy(schema = 8).let { it.copy(digest = reviewAssignmentDigest(it)) }
                assertFalse(reviewAssignmentCurrent(pack, relabelled))
                answer(store, task, 1); answer(store, task, 2)
                assertTrue(advance(store, 6))
                assertEquals(task, open(store))
                val row = store.governedLearnerDocument().unitCompletions.single { it.unitNumber == 6 }
                assertEquals(task.id, row.reviewAssignmentId); assertEquals(task.policyDigest, row.reviewPolicyDigest)
                assertEquals((1..6).toSet(), currentCompletedUnitNumbers(pack, store.v2LearnerView()))
                // A current v3 pack accepts the exact v2 declaration identity, not changed content.
                val changed = pack.copy(items = pack.items.map { if (it.id == "u6-i-object-1") it.copy(feedback = it.feedback + " changed") else it })
                assertFalse(6 in currentCompletedUnitNumbers(changed, store.v2LearnerView()))
            }
            LocalStore.openNamed(context, name).use { store ->
                assertEquals(task, open(store)); assertEquals((1..6).toSet(), currentCompletedUnitNumbers(pack, store.v2LearnerView()))
            }
        } finally { context.deleteDatabase(name); File(context.filesDir, "recordings-$name").deleteRecursively() }
    }

    @Test fun originalV1SchemaOneAndThreeTasksRetainTheirOwnObligationAfterCurrentReteaching() {
        for ((prefix, schema) in listOf("" to 3, "predecessor-schema1/" to 1)) {
            val original = document("audit/independent-gpt61-2026-10-12/ia003-policy-history-transition/${prefix}PARTIAL_REVIEW.zip")
            val task = original.reviewAssignments.single { it.targetUnit == 6 }
            assertEquals(schema, task.schema)
            assertEquals(Unit6CumulativeReviewPolicy.definition.id, task.policyId)
            LocalStore.openInMemory(context()).use { store ->
                assertEquals(GovernedReplaceOutcome.REPLACED, store.replaceGovernedLearner(original, emptyList()))
                prepare(store, 5) // Actual current prerequisites; do not reuse stale early-unit flags.
                assertTrue(reviewAssignmentCurrent(pack, task))
                assertEquals(task, open(store))
                assertEquals(2, task.slots.size)
                task.slots.indices.forEach { answer(store, task, it) }
                assertTrue(advance(store, 6))
                assertEquals(task.id, store.governedLearnerDocument().unitCompletions.single { it.unitNumber == 6 }.reviewAssignmentId)
                assertEquals(task, open(store))
            }
        }
    }
}
