package dev.russiancompanion.v2

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.RussianCompanionApplication
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.backup.*
import dev.russiancompanion.v2.curriculum.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.ByteArrayOutputStream
import java.io.File

/** Fresh current learner records through governed progression, backup and named-store APIs. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class Unit13PresentReviewPersistenceTest {
    @Test fun currentPartialAndCompletedTasksRestoreReopenAndKeepTheirOwnObligation() {
        val pack = currentPack
        val context = context()
        val name = "unit13-current-partial-${System.nanoTime()}"
        val stage = File(context.cacheDir, name)
        var partial: GovernedLearnerDocument? = null
        var completed: GovernedLearnerDocument? = null
        try {
            LocalStore.openNamed(context, name).use { store ->
                prepare(store, pack)
                assertTrue(ordinaryUnitRequirements(pack, pack.unit(13)!!, store.v2LearnerView()).allowed)
                assertEquals(8, pack.unit(13)!!.advancement!!.minimumDistinctChecks)
                assertEquals(9, pack.unit(13)!!.sessions.flatMap { it.steps }.filter { it.kind == "CHECK" }.flatMap { it.itemIds }.distinct().size)
                assertEquals(4, pack.unit(13)!!.sessions.flatMap { it.steps }.filter { it.kind == "GUIDED" }.flatMap { it.itemIds }.distinct().size)
                assertEquals((1..12).toSet(), currentCompletedUnitNumbers(pack, store.v2LearnerView()))
                val prerequisites = store.cumulativeReviewHistory().first
                assertEquals("unit11-prior-noun-behavior-possession-absence-v2", prerequisites.single { it.targetUnit == 11 }.policyId)
                assertEquals(1, prerequisites.single { it.targetUnit == 11 }.schema)
                assertEquals("unit12-prior-gender-past-gender-v2", prerequisites.single { it.targetUnit == 12 }.policyId)
                assertEquals(1, prerequisites.single { it.targetUnit == 12 }.schema)
                val task = open(store, pack)
                assertEquals("unit13-prior-present-aspect-past-gender-v3", task.policyId); assertEquals(1, task.schema)
                assertEquals(3, task.slots.size)
                assertEquals(listOf("obj-u4-present", "obj-u12-aspect", "obj-u11-past-aspect"), task.slots.map { it.objectiveId })
                assertEquals(listOf(4, 12, 11), task.slots.map { it.ownerUnit })
                assertTrue(task.slots[0].itemId in cumulativeReviewDeclaredPolicy(pack, 13)!!.pool[0].candidateItemIds)
                assertTrue(reviewAssignmentCurrent(pack, task))
                assertEquals("u12-i-aspect-1", task.slots[1].itemId)
                assertTrue(task.slots[2].itemId in listOf("u11-i-past-aspect-1", "u11-i-past-aspect-2"))
                task.slots.forEach { assertEquals(V2Validator.unitContentReviewDigest(pack, it.ownerUnit), it.unitDigest) }
                answer(store, task, 0, pack = pack)
                assertEquals(setOf(0), completedReviewSlots(pack, task, store.v2LearnerView().reviewAnswers))
                assertFalse(advancementDecision(pack, pack.unit(13)!!, store.v2LearnerView()).allowed)
                partial = store.governedLearnerDocument()
                assertTrue(partial!!.unitCompletions.none { it.unitNumber == 13 })
                assertTrue(partial!!.recordings.isEmpty())
                val bytes = archive(store, pack)
                restoreAndReopen(context, pack, partial!!, bytes, task)
                val read = readGovernedBackup(bytes.inputStream(), "dev.russiancompanion", pack.packId, pack.packVersion, stage)
                assertTrue(read is BackupReadResult.Valid)
                assertEquals(partial, (read as BackupReadResult.Valid).backup.document)
                LocalStore.openInMemory(context).use { target ->
                    assertEquals(GovernedReplaceOutcome.REPLACED, target.replaceGovernedLearner(read.backup.document, emptyList()))
                    assertEquals(partial, target.governedLearnerDocument())
                    assertEquals(task, open(target, pack))
                }
            }
            LocalStore.openNamed(context, name).use { store ->
                assertEquals(partial, store.governedLearnerDocument())
                val task = open(store, pack)
                assertEquals(partial!!.reviewAssignments.single { it.targetUnit == 13 }, task)
                assertEquals(setOf(0), completedReviewSlots(pack, task, store.v2LearnerView().reviewAnswers))
                for (index in 1 until task.slots.size) answer(store, task, index, pack = pack)
                assertTrue(advance(store, 13, pack))
                assertEquals(14, store.v2Profile().currentUnitNumber)
                assertEquals((1..13).toSet(), currentCompletedUnitNumbers(pack, store.v2LearnerView()))
                completed = store.governedLearnerDocument()
                val row = completed!!.unitCompletions.single { it.unitNumber == 13 }
                assertEquals(task.id, row.reviewAssignmentId); assertEquals(task.policyDigest, row.reviewPolicyDigest)
                assertTrue(row.reviewCompletionOrder > task.boundary)
                restoreAndReopen(context, pack, completed!!, archive(store, pack), task)
            }
            LocalStore.openNamed(context, name).use { store ->
                assertEquals(completed, store.governedLearnerDocument())
                assertEquals((1..13).toSet(), currentCompletedUnitNumbers(pack, store.v2LearnerView()))
                assertEquals(14, store.v2Profile().currentUnitNumber)
            }
        } finally {
            stage.deleteRecursively(); context.deleteDatabase(name)
            File(context.filesDir, "recordings-$name").deleteRecursively()
        }
    }

    @Test fun priorPrintAspectAndPastAnswersCannotReplaceFreshAssignedPresentAnswer() {
        val pack = currentPack
        LocalStore.openInMemory(context()).use { store ->
            prepare(store, pack)
            assertTrue(ordinaryUnitRequirements(pack, pack.unit(13)!!, store.v2LearnerView()).allowed)
            val task = open(store, pack)
            // Earlier practice cannot satisfy this new assignment-bound obligation.
            assertTrue(store.cumulativeReviewHistory().first.single { it.targetUnit == 10 }
                .slots.any { it.objectiveId == "obj-u4-present" })
            task.slots.forEach { slot ->
                val item = pack.item(slot.itemId)!!
                store.v2RecordGovernedAttempt(prepareGovernedAttempt(pack, item.id, item.answer,
                    OPTIONAL_PRACTICE, practiceStepId("mixed"), "unit13-present")!!)
            }
            assertTrue(completedReviewSlots(pack, task, store.v2LearnerView().reviewAnswers).isEmpty())
            assertFalse(advancementDecision(pack, pack.unit(13)!!, store.v2LearnerView()).allowed)
            task.slots.forEachIndexed { index, slot ->
                if (slot.objectiveId != "obj-u4-present") answer(store, task, index, pack = pack)
            }
            val before = store.governedLearnerDocument()
            val advancedWithoutPresent = try { advance(store, 13, pack) } catch (refusal: IllegalStateException) {
                if (refusal.message != "current advancement evidence is incomplete") throw refusal
                false
            }
            if (!advancedWithoutPresent) assertEquals(before, store.governedLearnerDocument())
            assertFalse("The current Unit13 obligation must refuse advancement without its fresh assigned present-form answer", advancedWithoutPresent)
            assertEquals("unit13-prior-present-aspect-past-gender-v3", task.policyId)
            assertEquals(1, task.schema)
            val policy = cumulativeReviewDeclaredPolicy(pack, 13)!!
            assertEquals(listOf("obj-u4-present", "obj-u12-aspect", "obj-u11-past-aspect"), policy.pool.map { it.objectiveId })
            assertEquals(listOf("u4-i-verb-говорить-мы", "u4-i-verb-видеть-он", "u4-i-verb-писать-вы", "u4-i-verb-идти-они"), policy.pool[0].candidateItemIds)
            assertEquals(listOf("u12-i-aspect-1", "u12-i-aspect-2"), policy.pool[1].candidateItemIds)
            assertEquals(listOf("u11-i-past-aspect-1", "u11-i-past-aspect-2"), policy.pool[2].candidateItemIds)
            assertEquals(listOf(4, 12, 11), task.slots.map { it.ownerUnit })
            answer(store, task, 0, pack = pack)
            assertTrue(advance(store, 13, pack))
        }
    }

    private fun prepare(store: LocalStore, pack: V2CurriculumPack) {
        for (n in 1..13) {
            store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = n, currentSessionId = null))
            for (step in pack.unit(n)!!.sessions.flatMap { it.steps }) {
                step.itemIds.forEach { id -> store.v2RecordGovernedAttempt(prepareGovernedAttempt(pack,
                    id, pack.item(id)!!.answer, PRESCRIBED_CHECK, step.id, "unit13-present")!!) }
                store.v2CompleteGovernedStep(step.id, governedStepCompletionDigest(pack, store.v2LearnerView(), step.id)!!, "unit13-present")
            }
            store.v2AttestBook(n, "unit13-present")
            if (n < 13) {
                if (n >= 3) {
                    val task = store.openCumulativeReview(pack, n, store.learnerMutationEpoch())!!
                    task.slots.indices.forEach { answer(store, task, it, pack = pack) }
                }
                assertTrue(advance(store, n, pack))
            }
        }
    }
    private fun open(store: LocalStore, pack: V2CurriculumPack) = store.openCumulativeReview(pack, 13, store.learnerMutationEpoch())!!
    private fun answer(store: LocalStore, task: CumulativeReviewAssignment, index: Int, correct: Boolean = true, pack: V2CurriculumPack) {
        val item = pack.item(task.slots[index].itemId)!!
        assertTrue(store.answerCumulativeReview(pack, task.id, index,
            if (correct) item.answer else item.options.first { it != item.answer }, store.learnerMutationEpoch()))
    }
    private fun advance(store: LocalStore, n: Int, pack: V2CurriculumPack): Boolean = runBlocking {
        val session = pack.unit(n + 1)!!.sessions.first().id
        store.v2AdvanceUnitIfCurrentAsync(store.learnerMutationEpoch(), n, "unit13-present", n + 1,
            session, resumePositionDigest(pack, session, 0)!!, GOVERNED_IDENTITY_SCHEMA, pack)
    }
    private fun restoreAndReopen(context: Context, pack: V2CurriculumPack,
                                 document: GovernedLearnerDocument, bytes: ByteArray,
                                 task: CumulativeReviewAssignment) {
        val name = "unit13-import-${System.nanoTime()}"; val stage = File(context.cacheDir, name)
        try {
            val read = readGovernedBackup(bytes.inputStream(), "dev.russiancompanion", pack.packId, pack.packVersion, stage)
            assertTrue(read is BackupReadResult.Valid)
            assertEquals(document, (read as BackupReadResult.Valid).backup.document)
            LocalStore.openNamed(context, name).use { target ->
                assertEquals(GovernedReplaceOutcome.REPLACED, target.replaceGovernedLearner(read.backup.document, emptyList()))
                assertEquals(document, target.governedLearnerDocument())
            }
            LocalStore.openNamed(context, name).use { target ->
                assertEquals(document, target.governedLearnerDocument())
                assertEquals(task, open(target, pack))
                task.slots.forEach { assertEquals(V2Validator.unitContentReviewDigest(pack, it.ownerUnit), it.unitDigest) }
                val completed = document.unitCompletions.any { it.unitNumber == 13 }
                assertEquals(completed, cumulativeReviewComplete(pack, target.v2LearnerView(), 13))
                assertEquals(completed, 13 in currentCompletedUnitNumbers(pack, target.v2LearnerView()))
                assertEquals(document, target.governedLearnerDocument())
            }
        } finally {
            stage.deleteRecursively(); context.deleteDatabase(name)
            File(context.filesDir, "recordings-$name").deleteRecursively()
        }
    }
    private fun archive(store: LocalStore, pack: V2CurriculumPack): ByteArray = ByteArrayOutputStream().also {
        writeGovernedBackup(it, "dev.russiancompanion", pack.packId, pack.packVersion, "DARK", store.governedLearnerDocument(), emptyList())
    }.toByteArray()
    private fun context(): Context = ApplicationProvider.getApplicationContext<Context>().also {
        (it.applicationContext as RussianCompanionApplication).closeStartupStoreForTests()
    }
    companion object {
        private val root = File(if (File("app").isDirectory) "." else "..")
        private val currentPack by lazy { V2PackLoader.parse(File(root, "app/src/main/assets/content/v2-curriculum.json").readText()).also { V2PackIdentityIndex.warm(it) } }
    }
}
