package dev.russiancompanion.v2

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.RussianCompanionApplication
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.backup.*
import dev.russiancompanion.v2.curriculum.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

/** Fresh current-source prerequisites and invalidation checks; no historical pack dependency. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class CurrentFoundationHistoryTest {

    @Test fun genuineFreshCurrentPrerequisitesReachUnitFourteenWithCurrentOwnerBindings() {
        val context = context(); val document = FreshCurrentPrerequisiteHistory.document(current, 14, context)
        LocalStore.openInMemory(context).use { store ->
            assertEquals(GovernedReplaceOutcome.REPLACED, store.replaceGovernedLearner(document, emptyList()))
            assertEquals((1..13).toSet(), currentCompletedUnitNumbers(current, store.v2LearnerView()))
            document.reviewAssignments.forEach { a ->
                assertTrue(reviewAssignmentCurrent(current, a))
                a.slots.forEach { slot -> assertEquals(V2Validator.unitContentReviewDigest(current, slot.ownerUnit), slot.unitDigest) }
            }
            FreshCurrentPrerequisiteHistory.ordinary(store, current, 14)
            assertTrue(ordinaryUnitRequirements(current, current.unit(14)!!, store.v2LearnerView()).allowed)
            val task = store.openCumulativeReview(current, 14, store.learnerMutationEpoch())!!
            assertEquals(1, task.schema)
            assertTrue(reviewAssignmentCurrent(current, task))
        }
    }

    @Test fun changedCurrentInstructionInvalidatesCreditStepAndResumeUntilFreshWork() {
        val p = current
        val unit = p.unit(1)!!
        val session = unit.sessions.first { it.steps.any { step -> step.kind == "TEACH" } }
        val step = session.steps.first { it.kind == "TEACH" }
        val check = unit.sessions.flatMap { it.steps }.first { it.kind == "CHECK" && it.itemIds.isNotEmpty() }
        val item = p.item(check.itemIds.first())!!
        val changed = p.copy(units = p.units.map { u ->
            if (u.number != 1) u else u.copy(sessions = u.sessions.map { s ->
                if (s.id != session.id) s else s.copy(steps = s.steps.map {
                    if (it.id == step.id) it.copy(body = it.body + " Synthetic instruction change.") else it
                })
            })
        })
        LocalStore.openInMemory(context()).use { store ->
            store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true,
                currentSessionId = session.id, currentStepIndex = session.steps.indexOf(step)).verifiedResume(p))
            store.v2RecordGovernedAttempt(prepareGovernedAttempt(p, item.id, item.answer,
                PRESCRIBED_CHECK, check.id, "current-work")!!)
            store.v2CompleteGovernedStep(step.id, governedStepCompletionDigest(p, store.v2LearnerView(), step.id)!!, "current-work")
            assertTrue(item.id in creditItemIds(p, store.v2LearnerView()))
            assertTrue(completedStepCounts(p, store.v2LearnerView(), step.id))
            assertTrue(resumeIsCurrent(p, store.v2LearnerView()))
            val before = store.governedLearnerDocument()
            assertFalse(item.id in creditItemIds(changed, store.v2LearnerView()))
            assertFalse(completedStepCounts(changed, store.v2LearnerView(), step.id))
            assertFalse(resumeIsCurrent(changed, store.v2LearnerView()))
            assertEquals(before, store.governedLearnerDocument())
            store.v2RecordGovernedAttempt(prepareGovernedAttempt(changed, item.id, item.answer,
                PRESCRIBED_CHECK, check.id, "fresh-work")!!)
            store.v2CompleteGovernedStep(step.id, governedStepCompletionDigest(changed, store.v2LearnerView(), step.id)!!, "fresh-work")
            store.v2SaveProfile(store.v2Profile().verifiedResume(changed))
            assertTrue(item.id in creditItemIds(changed, store.v2LearnerView()))
            assertTrue(completedStepCounts(changed, store.v2LearnerView(), step.id))
            assertTrue(resumeIsCurrent(changed, store.v2LearnerView()))
            assertEquals(before.attempts.size + 1, store.governedLearnerDocument().attempts.size)
        }
    }
    private fun context(): Context = ApplicationProvider.getApplicationContext<Context>().also {
        (it.applicationContext as RussianCompanionApplication).closeStartupStoreForTests()
    }
    companion object {
        private val root = File(if (File("app").isDirectory) "." else "..")
        private val current by lazy { V2PackLoader.parse(File(root, "app/src/main/assets/content/v2-curriculum.json").readText()).also { V2PackIdentityIndex.warm(it) } }
    }
}
