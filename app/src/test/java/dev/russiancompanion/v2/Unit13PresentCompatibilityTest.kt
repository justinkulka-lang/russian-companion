package dev.russiancompanion.v2

import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.backup.*
import dev.russiancompanion.v2.curriculum.*
import dev.russiancompanion.v2.Unit13PresentFixtures.root
import dev.russiancompanion.v2.Unit13PresentFixtures.pack
import dev.russiancompanion.v2.Unit13PresentFixtures.context
import dev.russiancompanion.v2.Unit13PresentFixtures.document
import dev.russiancompanion.v2.Unit13PresentFixtures.advance
import dev.russiancompanion.v2.Unit13PresentFixtures.ordinary
import dev.russiancompanion.v2.Unit13PresentFixtures.prepare
import dev.russiancompanion.v2.Unit13PresentFixtures.open
import dev.russiancompanion.v2.Unit13PresentFixtures.answer
import dev.russiancompanion.v2.Unit13PresentFixtures.archive
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import kotlinx.serialization.json.*
import dev.russiancompanion.v2.Unit13PresentFixtures.before13

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class Unit13PresentCompatibilityTest {


    @Test fun wrongSchemasResponseTargetAndEpochCannotBorrowNewCredit() {
        LocalStore.openInMemory(context()).use { store ->
            prepare(store)
            val task = open(store)
            assertEquals(15, task.schema)
            for (schema in (1..14) + 16) {
                val bad = task.copy(schema = schema).let { it.copy(digest = reviewAssignmentDigest(it)) }
                assertFalse("schema $schema", reviewAssignmentCurrent(pack, bad))
                assertNotNull(reviewHistoryDefects(listOf(bad.copy(boundary = 0).let { it.copy(digest = reviewAssignmentDigest(it)) }), emptyList()))
            }
            for (bad in listOf(task.copy(targetUnit = 6), task.copy(responseRuleDigest = "0".repeat(64)), task.copy(policyDigest = "0".repeat(64)))) {
                val signed = bad.copy(digest = reviewAssignmentDigest(bad))
                assertFalse(reviewAssignmentCurrent(pack, signed))
            }
            val unit9 = store.cumulativeReviewHistory().first.single { it.targetUnit == 9 }
            val wrongUnit9Schema = unit9.copy(schema = 15).let { it.copy(digest = reviewAssignmentDigest(it)) }
            assertFalse(reviewAssignmentCurrent(pack, wrongUnit9Schema))
            assertNotNull(reviewHistoryDefects(listOf(wrongUnit9Schema.copy(boundary = 0).let { it.copy(digest = reviewAssignmentDigest(it)) }), emptyList()))
            val epoch = store.learnerMutationEpoch()
            val before = store.governedLearnerDocument()
            store.openLearnerReplacement()
            try {
                assertNull(store.openCumulativeReview(pack, 13, epoch))
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
            for (owner in listOf(4, 12, 11)) {
                assertNull(createCumulativeReviewAssignment(pack, learner.copy(completedUnitNumbers = learner.completedUnitNumbers - owner), 13, "missing-$owner", 0))
                val slot = task.slots.single { it.ownerUnit == owner }
                val changed = pack.copy(items = pack.items.map { if (it.id == slot.itemId) it.copy(feedback = it.feedback + " changed") else it })
                assertFalse(reviewAssignmentCurrent(changed, task))
                val stepId = pack.unit(owner)!!.objectives.single { it.id == slot.objectiveId }.instructionStepIds.last()
                val teaching = pack.copy(units = pack.units.map { u -> if (u.number != owner) u else u.copy(sessions = u.sessions.map { session ->
                    session.copy(steps = session.steps.map { if (it.id == stepId) it.copy(body = it.body + " changed") else it })
                }) })
                assertFalse(reviewAssignmentCurrent(teaching, task))
            }
            assertNull(createCumulativeReviewAssignment(pack, learner.copy(governedStepDigests = emptyMap()), 13, "missing-teaching", 0))
        }
    }

    @Test fun recentPresentMissRemainsAssignedThroughCorrectionRollbackBackupAndReopen() {
        val context = context(); val name = "unit13-gender-miss-${System.nanoTime()}"; val stage = File(context.cacheDir, name)
        try {
            LocalStore.openNamed(context, name).use { store ->
                prepare(store)
                val item = pack.item("u4-i-verb-писать-вы")!!
                val step = pack.unit(4)!!.sessions.flatMap { it.steps }.single { it.kind == "CHECK" && item.id in it.itemIds }
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
                assertTrue(runCatching { advance(store, 13) }.isFailure)
                assertEquals(before, store.governedLearnerDocument())
                assertTrue(advance(store, 13))
                val document = store.governedLearnerDocument()
                val decoded = readGovernedBackup(archive(store).inputStream(), "dev.russiancompanion", pack.packId, pack.packVersion, stage)
                assertTrue(decoded is BackupReadResult.Valid)
                LocalStore.openInMemory(context).use { target ->
                    assertEquals(GovernedReplaceOutcome.REPLACED, target.replaceGovernedLearner((decoded as BackupReadResult.Valid).backup.document, emptyList()))
                    assertEquals(document, target.governedLearnerDocument())
                    assertEquals(task, open(target))
                    assertEquals((1..13).toSet(), currentCompletedUnitNumbers(pack, target.v2LearnerView()))
                }
            }
            LocalStore.openNamed(context, name).use { store ->
                assertEquals("u4-i-verb-писать-вы", open(store).slots[0].itemId)
                assertEquals((1..13).toSet(), currentCompletedUnitNumbers(pack, store.v2LearnerView()))
            }
        } finally { stage.deleteRecursively(); context.deleteDatabase(name); File(context.filesDir, "recordings-$name").deleteRecursively() }
    }


    @Test fun allEightCandidatesAllocateFromGovernedMissesWhilePracticeMalformedAndDuplicateOrdersCannotSelect() {
        LocalStore.openInMemory(context()).use { store ->
            prepare(store)
            val policy = Unit13PresentCumulativeReviewPolicy.definition
            assertEquals(listOf(4,2,2), policy.pool.map { it.candidateItemIds.size })
            for ((slot, spec) in policy.pool.withIndex()) {
                val ids = spec.candidateItemIds
                val viewBefore = store.v2LearnerView()
                assertEquals(ids.first(), createCumulativeReviewAssignment(pack, viewBefore, 13, "least-$slot", 0)!!.slots[slot].itemId)
                for (id in ids) {
                    recordMiss(store,id)
                    val task = createCumulativeReviewAssignment(pack, store.v2LearnerView(),13,"miss-$id",0)!!
                    assertEquals(id, task.slots[slot].itemId)
                    val item=pack.item(id)!!;val presented=reviewPresentedOptions(item,task,slot)
                    assertEquals(item.options.toSet(),presented.toSet());assertEquals(item.options.size,presented.size)
                    assertEquals(presented,reviewPresentedOptions(item.copy(answer=item.options.first { it!=item.answer }),task,slot))
                }
                val item=pack.item(ids.first())!!
                store.v2RecordGovernedAttempt(prepareGovernedAttempt(pack,item.id,item.options.first { it!=item.answer },OPTIONAL_PRACTICE,practiceStepId("mixed"),"optional")!!)
                val view=store.v2LearnerView();val candidates=ids.map { pack.item(it)!! }
                assertEquals(ids.last(),createCumulativeReviewAssignment(pack,view,13,"ignore-optional-$slot",0)!!.slots[slot].itemId)
                assertEquals(ids.first(),recentReviewCandidate(pack,view.copy(governedAttempts=view.governedAttempts!!.map { it.copy(itemDigest="0".repeat(64)) }),candidates)!!.id)
                assertEquals(ids.first(),recentReviewCandidate(pack,view.copy(governedAttempts=view.governedAttempts!!.map { it.copy(acceptedOrder=1) }),candidates)!!.id)
            }
        }
    }
    private fun recordMiss(store: LocalStore, id: String) {
        val item=pack.item(id)!!;val step=pack.units.flatMap { it.sessions }.flatMap { it.steps }.single { it.kind=="CHECK" && id in it.itemIds }
        store.v2RecordGovernedAttempt(prepareGovernedAttempt(pack,id,item.options.first { it!=item.answer },PRESCRIBED_CHECK,step.id,"unit13-miss")!!)
    }
}
