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

/** Every state is produced now through current store APIs; archives live only in test memory. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class CurrentFoundationPersistenceTest {
    private enum class State(val completed: Set<Int>, val unit: Int) {
        EMPTY(emptySet(), 1), WRONG_CHECK(emptySet(), 1), CORRECT_CHECK(emptySet(), 1),
        GUIDED_WORK(emptySet(), 1), COMPLETED_STEP(emptySet(), 1),
        UNIT1_COMPLETE(setOf(1), 2), UNIT2_PARTIAL(setOf(1), 2),
        UNIT2_COMPLETE(setOf(1, 2), 3), UNIT3_REVIEW_PARTIAL(setOf(1, 2), 3),
        UNIT3_COMPLETE(setOf(1, 2, 3), 4),
    }

    @Test fun tenFreshCurrentStatesKeepProgressAcrossBackupRestoreAndNamedReopen() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        (context.applicationContext as RussianCompanionApplication).closeStartupStoreForTests()
        for (state in State.entries) {
            val source = "foundation-${state.name}-${System.nanoTime()}"
            val target = "$source-import"
            val stage = File(context.cacheDir, target)
            try {
                val expected = LocalStore.openNamed(context, source).use { store ->
                    populate(store, state, context)
                    assertState(store, state)
                    val document = store.governedLearnerDocument()
                    val bytes = ByteArrayOutputStream().also {
                        writeGovernedBackup(it, "dev.russiancompanion", pack.packId, pack.packVersion,
                            "DARK", document, emptyList())
                    }.toByteArray()
                    val read = readGovernedBackup(bytes.inputStream(), "dev.russiancompanion",
                        pack.packId, pack.packVersion, stage)
                    assertTrue(state.name, read is BackupReadResult.Valid)
                    assertEquals(document, (read as BackupReadResult.Valid).backup.document)
                    LocalStore.openNamed(context, target).use { restored ->
                        assertEquals(GovernedReplaceOutcome.REPLACED,
                            restored.replaceGovernedLearner(read.backup.document, emptyList()))
                        assertEquals(document, restored.governedLearnerDocument())
                        assertState(restored, state)
                    }
                    document
                }
                for (name in listOf(source, target)) {
                    LocalStore.openNamed(context, name).use { reopened ->
                        assertEquals(expected, reopened.governedLearnerDocument())
                        assertState(reopened, state)
                        assertEquals(expected, reopened.governedLearnerDocument())
                    }
                }
            } finally {
                stage.deleteRecursively()
                for (name in listOf(source, target)) {
                    context.deleteDatabase(name)
                    File(context.filesDir, "recordings-$name").deleteRecursively()
                }
            }
        }
    }

    private fun populate(store: LocalStore, state: State, context: Context) {
        if (state.unit >= 2) {
            val target = if (state.unit == 2) 2 else 3
            assertEquals(GovernedReplaceOutcome.REPLACED, store.replaceGovernedLearner(
                FreshCurrentPrerequisiteHistory.document(pack, target, context), emptyList()))
        } else if (state != State.EMPTY) {
            store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true))
        }
        when (state) {
            State.WRONG_CHECK -> record(store, check(1), correct = false)
            State.CORRECT_CHECK -> record(store, check(1))
            State.GUIDED_WORK -> {
                val step = pack.unit(1)!!.sessions.flatMap { it.steps }
                    .first { it.kind == "GUIDED" && it.itemIds.isNotEmpty() }
                step.itemIds.forEach { id -> record(store, step, itemId = id) }
                store.v2CompleteGovernedStep(step.id,
                    governedStepCompletionDigest(pack, store.v2LearnerView(), step.id)!!, "current-state")
            }
            State.COMPLETED_STEP -> {
                val step = teaching()
                store.v2CompleteGovernedStep(step.id,
                    governedStepCompletionDigest(pack, store.v2LearnerView(), step.id)!!, "current-state")
            }
            State.UNIT2_PARTIAL -> record(store, check(2))
            State.UNIT3_REVIEW_PARTIAL, State.UNIT3_COMPLETE -> {
                FreshCurrentPrerequisiteHistory.ordinary(store, pack, 3)
                val task = store.openUnit3CumulativeReview(pack, store.learnerMutationEpoch())!!
                val indices = if (state == State.UNIT3_COMPLETE) task.slots.indices else 0..0
                for (index in indices) assertTrue(store.answerCumulativeReview(pack, task.id, index,
                    pack.item(task.slots[index].itemId)!!.answer, store.learnerMutationEpoch()))
                if (state == State.UNIT3_COMPLETE) assertTrue(runBlocking {
                    val session = pack.unit(4)!!.sessions.first().id
                    store.v2AdvanceUnitIfCurrentAsync(store.learnerMutationEpoch(), 3, "current-state", 4,
                        session, resumePositionDigest(pack, session, 0)!!, GOVERNED_IDENTITY_SCHEMA, pack)
                })
            }
            else -> Unit
        }
    }

    private fun record(store: LocalStore, step: V2Step, correct: Boolean = true,
                       itemId: String = step.itemIds.first()) {
        val item = pack.item(itemId)!!
        store.v2RecordGovernedAttempt(prepareGovernedAttempt(pack, item.id,
            if (correct) item.answer else item.options.first { it != item.answer },
            PRESCRIBED_CHECK, step.id, "current-state")!!)
        val located = pack.step(step.id)!!
        store.v2SaveProfile(store.v2Profile().copy(currentSessionId = located.first.id,
            currentStepIndex = located.first.steps.indexOf(step)).verifiedResume(pack))
    }

    private fun assertState(store: LocalStore, state: State) {
        val learner = store.v2LearnerView()
        assertEquals(state.name, state.completed, currentCompletedUnitNumbers(pack, learner))
        assertEquals(state.name, state.unit, store.v2Profile().currentUnitNumber)
        val document = store.governedLearnerDocument()
        val credits = creditItemIds(pack, learner)
        when (state) {
            State.EMPTY -> {
                assertFalse(learner.onboardingCompleted)
                assertTrue(document.attempts.isEmpty()); assertTrue(document.steps.isEmpty())
            }
            State.WRONG_CHECK -> {
                assertFalse(document.attempts.single().correct)
                assertFalse(check(1).itemIds.first() in credits)
                assertTrue(resumeIsCurrent(pack, learner))
            }
            State.CORRECT_CHECK -> {
                assertTrue(document.attempts.single().correct)
                assertTrue(check(1).itemIds.first() in credits)
                assertTrue(resumeIsCurrent(pack, learner))
            }
            State.GUIDED_WORK -> {
                val step = pack.unit(1)!!.sessions.flatMap { it.steps }
                    .first { it.kind == "GUIDED" && it.itemIds.isNotEmpty() }
                assertTrue(completedStepCounts(pack, learner, step.id))
                val selections = governedSessionSelections(pack, learner)
                step.itemIds.forEach { id ->
                    assertEquals(pack.item(id)!!.answer, selections[governedSelectionKey(step.id, id)])
                }
                assertFalse(advancementDecision(pack, pack.unit(1)!!, learner).allowed)
                assertTrue(resumeIsCurrent(pack, learner))
            }
            State.COMPLETED_STEP -> assertTrue(completedStepCounts(pack, learner, teaching().id))
            State.UNIT2_PARTIAL -> {
                assertTrue(check(2).itemIds.first() in credits)
                assertFalse(ordinaryUnitRequirements(pack, pack.unit(2)!!, learner).allowed)
                assertTrue(resumeIsCurrent(pack, learner))
            }
            State.UNIT3_REVIEW_PARTIAL -> {
                val task = document.reviewAssignments.single { it.targetUnit == 3 }
                assertTrue(reviewAssignmentCurrent(pack, task))
                assertEquals(setOf(0), completedReviewSlots(pack, task, document.reviewAnswers))
                assertFalse(advancementDecision(pack, pack.unit(3)!!, learner).allowed)
            }
            State.UNIT3_COMPLETE -> {
                val task = document.reviewAssignments.single { it.targetUnit == 3 }
                val completion = document.unitCompletions.single { it.unitNumber == 3 }
                assertEquals(task.id, completion.reviewAssignmentId)
                assertEquals(task.policyDigest, completion.reviewPolicyDigest)
                assertTrue(completion.reviewCompletionOrder > task.boundary)
                assertTrue(cumulativeReviewComplete(pack, learner, 3))
            }
            else -> Unit
        }
    }

    private fun check(number: Int) = pack.unit(number)!!.sessions.flatMap { it.steps }
        .first { it.kind == "CHECK" && it.itemIds.isNotEmpty() }
    private fun teaching() = pack.unit(1)!!.sessions.flatMap { it.steps }.first { it.kind == "TEACH" }

    companion object {
        private val root = File(if (File("app").isDirectory) "." else "..")
        private val pack by lazy {
            V2PackLoader.parse(File(root, "app/src/main/assets/content/v2-curriculum.json").readText())
                .also { V2PackIdentityIndex.warm(it) }
        }
    }
}
