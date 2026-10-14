package dev.russiancompanion.v2

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.curriculum.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

/** Historical defect reproductions, NOT correction acceptance tests. Kept outside app sources. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class Ia003CumulativeReviewBaselineTest {
    @Test fun onlyUnitTwoDeclaresDueReviewInAllThirtyRules() {
        val p = pack()
        assertEquals((1..30).toList(), p.units.map { it.number })
        assertEquals(listOf(2), p.units.filter { it.advancement!!.requireDueReview }.map { it.number })
        assertTrue(p.units.filter { it.number >= 3 }.all { !it.advancement!!.requireDueReview })
    }

    @Test fun unitThreeAdvancesWithRealEarlierCompletionAndNoFreshEarlierReviewAnswers() {
        val p = pack()
        store().use { s ->
            finish(p, s, 1); finish(p, s, 2)
            val boundary = s.v2Attempts().maxOf { it.acceptedOrder }
            finish(p, s, 3, completed = false)
            val learner = s.v2LearnerView()
            assertEquals(setOf(1, 2), currentCompletedUnitNumbers(p, learner))
            assertTrue(learner.governedAttempts!!.filter { it.acceptedOrder > boundary }.all { it.unitNumber == 3 })
            val decision = advancementDecision(p, p.unit(3)!!, learner)
            assertTrue(decision.remaining.joinToString("\n"), decision.allowed)
        }
    }

    @Test fun changingOnlyUnitThreeDueFlagStillSilentlyPassesAnEmptyReview() {
        val original = pack()
        val p = original.copy(units = original.units.map { u ->
            if (u.number == 3) u.copy(advancement = u.advancement!!.copy(requireDueReview = true)) else u
        })
        val u = p.unit(3)!!
        val matching = u.sessions.flatMap { it.steps }.filter { step ->
            step.id.contains("review") || p.items.any { it.id in step.itemIds && it.objectiveId == "obj-u2-review" }
        }
        assertTrue(matching.isEmpty())
        store().use { s ->
            finish(p, s, 1); finish(p, s, 2); finish(p, s, 3, completed = false)
            val decision = advancementDecision(p, u, s.v2LearnerView())
            assertTrue(decision.remaining.joinToString("\n"), decision.allowed)
        }
    }

    @Test fun unitTwoGateActuallyRequiresItsHardCodedUnitOneReviewSteps() {
        val p = pack()
        store().use { s ->
            finish(p, s, 1); finish(p, s, 2, completed = false, omitReviewCompletion = true)
            val before = advancementDecision(p, p.unit(2)!!, s.v2LearnerView())
            assertFalse(before.allowed)
            assertTrue(before.remaining.toString(), "Complete the scheduled Unit 1 review" in before.remaining)
            finish(p, s, 2, completed = false)
            val after = advancementDecision(p, p.unit(2)!!, s.v2LearnerView())
            assertTrue(after.remaining.joinToString("\n"), after.allowed)
        }
    }

    @Test fun mixedPracticeReordersRecentMissesWithoutCreatingAnAdvancementObligation() {
        val p = pack()
        store().use { s ->
            finish(p, s, 1); finish(p, s, 2); finish(p, s, 3, completed = false)
            val before = practiceQueue(p, s.v2LearnerView(), "mixed")
            assertTrue(before.size > 2)
            assertEquals(before.map { it.id }.sorted(), before.map { it.id })
            val item = before.last()
            val wrong = item.options.first { it != item.answer }
            val write = prepareGovernedAttempt(p, item.id, wrong, OPTIONAL_PRACTICE,
                practiceStepId("mixed"), "2026-10-14T01:00:00")!!
            s.v2RecordGovernedAttempt(write)
            val learner = s.v2LearnerView()
            assertEquals(item.id, practiceQueue(p, learner, "mixed").first().id)
            assertEquals(before.map { it.id }.toSet(), practiceQueue(p, learner, "mixed").map { it.id }.toSet())
            val decision = advancementDecision(p, p.unit(3)!!, learner)
            assertTrue(decision.remaining.joinToString("\n"), decision.allowed)
        }
    }

    @Test fun noCumulativeReviewAttemptRoleCanBePreparedOrWritten() {
        val p = pack()
        val item = p.items.first { it.options.size > 1 }
        assertNull(prepareGovernedAttempt(p, item.id, item.answer, "CUMULATIVE_REVIEW",
            "cumulative:unit-3:assignment-1", "2026-10-14T01:01:00"))
        val valid = prepareGovernedAttempt(p, item.id, item.answer, OPTIONAL_PRACTICE,
            practiceStepId("mixed"), "2026-10-14T01:01:00")!!
        store().use { s ->
            val failure = runCatching { s.v2RecordGovernedAttempt(valid.copy(attemptRole = "CUMULATIVE_REVIEW")) }
            assertTrue(failure.isFailure)
            assertTrue(s.v2Attempts().isEmpty())
        }
    }

    private fun finish(p: V2CurriculumPack, s: LocalStore, number: Int,
                       completed: Boolean = true, omitReviewCompletion: Boolean = false) {
        s.v2SaveProfile(s.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = number))
        val u = p.unit(number)!!
        for (step in u.sessions.flatMap { it.steps }) {
            for (id in step.itemIds) {
                val item = p.item(id)!!
                s.v2RecordGovernedAttempt(prepareGovernedAttempt(p, id, item.answer, PRESCRIBED_CHECK,
                    step.id, "2026-10-14T00:00:00")!!)
            }
            val review = step.id.contains("review") || step.itemIds.any { p.item(it)!!.objectiveId == "obj-u2-review" }
            if (!omitReviewCompletion || !review) {
                s.v2CompleteGovernedStep(step.id, governedStepCompletionDigest(p, s.v2LearnerView(), step.id)!!,
                    "2026-10-14T00:00:00")
            }
        }
        s.v2AttestBook(number, "2026-10-14T00:00:00")
        if (completed) {
            val decision = advancementDecision(p, u, s.v2LearnerView())
            assertTrue("Unit $number: ${decision.remaining}", decision.allowed)
            s.v2CompleteUnit(number, "2026-10-14T00:00:00")
        }
    }
    private fun store() = LocalStore.openInMemory(ApplicationProvider.getApplicationContext<Context>())
    private fun pack(): V2CurriculumPack {
        val root = File(if (File("app").isDirectory) "." else "..")
        return V2PackLoader.parse(File(root, "app/src/main/assets/content/v2-curriculum.json").readText())
    }
}
