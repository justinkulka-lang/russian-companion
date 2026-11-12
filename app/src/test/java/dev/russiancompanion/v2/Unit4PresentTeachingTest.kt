package dev.russiancompanion.v2

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.backup.*
import dev.russiancompanion.v2.curriculum.*
import kotlinx.serialization.json.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.ByteArrayOutputStream
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class Unit4PresentTeachingTest {
    @Test fun everyRequiredTargetIsTaughtWithExactAdmittedAlternativesBeforeDistinctGuidedWork() {
        val p = current; val session = present(p); val objective = objective(p)
        assertEquals(4, objective.checkItemIds.size)
        for (id in objective.checkItemIds) {
            val item = p.item(id)!!
            val bare = id.removePrefix("u4-i-verb-").substringBeforeLast('-')
            val teaching = p.step("u4-st-teach-present-$bare")!!.second
            val guide = p.item("u4-g-present-$bare-я")!!
            assertTrue(item.promptLetter!! in teaching.body)
            for (option in item.options) {
                assertTrue(option, option in teaching.body)
                assertTrue(p.provenanceRecords.any { it.unitNumber == 4 && it.sourceId == item.sourceId && it.shippedText == option })
            }
            assertEquals(item.options, guide.options)
            assertNotEquals(item.answer, guide.answer)
            assertEquals(item.promptLetter, guide.promptLetter)
            assertTrue(session.steps.indexOf(teaching) < session.steps.indexOfFirst { guide.id in it.itemIds })
            // Guided work precedes the independent check in the unit's step order; since T14-b the check sits in
            // the end-of-unit check session (owner's narrowed A2-24 reading).
            val order = p.unit(4)!!.sessions.flatMap { it.steps }
            assertTrue(order.indexOfFirst { guide.id in it.itemIds } >= 0)
            assertTrue(order.indexOfFirst { guide.id in it.itemIds } < order.indexOfFirst { id in it.itemIds })
            assertTrue(guide.distractorRationales.filter { it.isNotBlank() }.size == 3)
        }
        val rem = p.step("u4-st-rem-present")!!.second
        assertEquals(session.steps.filter { it.kind == "TEACH" }.joinToString("\n\n") { it.body }, rem.body)
        assertEquals(objective.exampleIds, rem.exampleIds)
        assertEquals(6, rem.exampleIds.size)
        assertEquals(6, reviewedGuidedPractice(p, rem.id).size)
        val valid = V2Validator.validate(p)
        assertTrue(valid.errors.joinToString("\n"), valid.ok)
    }

    @Test fun guidedAnswersCannotReplaceRequiredTargetsAndWrongThenRightStillUsesExactKeys() {
        LocalStore.openInMemory(context()).use { s ->
            val p = current
            for (stepId in objective(p).practiceStepIds) {
                for (id in p.step(stepId)!!.second.itemIds) record(s, p, id, p.item(id)!!.answer)
            }
            assertTrue(objective(p).checkItemIds.none { it in creditItemIds(p, s.v2LearnerView()) })
            for (id in objective(p).checkItemIds) {
                val item = p.item(id)!!
                record(s, p, id, item.options.first { it != item.answer })
                assertFalse(id in creditItemIds(p, s.v2LearnerView()))
                record(s, p, id, item.answer)
                assertTrue(id in creditItemIds(p, s.v2LearnerView()))
            }
            assertEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS, objectiveState(objective(p), s.v2LearnerView(), p))
            assertFalse(advancementDecision(p, p.unit(4)!!, s.v2LearnerView()).allowed)
        }
    }

    @Test fun correctTargetsCannotWaiveCurrentRequiredPresentTeaching() {
        val p = current
        val unit = p.unit(4)!!
        val presentIds = present(p).steps.filter { it.kind == "TEACH" }.map { it.id }.toSet()
        val teaching = requiredInstructionSteps(unit).filter { it.id in presentIds }
        assertTrue(teaching.isNotEmpty())
        assertEquals(presentIds, teaching.map { it.id }.toSet())
        LocalStore.openInMemory(context()).use { s ->
            s.v2SaveProfile(s.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = 4))
            for (step in unit.sessions.flatMap { it.steps }) {
                for (id in step.itemIds) s.v2RecordGovernedAttempt(prepareGovernedAttempt(p, id,
                    p.item(id)!!.answer, PRESCRIBED_CHECK, step.id, "current-check")!!)
                if (step !in teaching) s.v2CompleteGovernedStep(step.id,
                    governedStepCompletionDigest(p, s.v2LearnerView(), step.id)!!, "current-instruction")
            }
            s.v2AttestBook(4, "current-host-attestation")
            assertEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS,
                objectiveState(objective(p), s.v2LearnerView(), p))
            assertEquals(listOf("Finish the required instruction steps"),
                ordinaryUnitRequirements(p, unit, s.v2LearnerView()).remaining)
            for (step in teaching) s.v2CompleteGovernedStep(step.id,
                governedStepCompletionDigest(p, s.v2LearnerView(), step.id)!!, "current-instruction")
            assertTrue(ordinaryUnitRequirements(p, unit, s.v2LearnerView()).allowed)
            assertFalse(cumulativeReviewComplete(p, s.v2LearnerView(), 4))
            assertEquals(listOf("Complete the assigned earlier-objective review"),
                currentUnitRequirements(p, unit, s.v2LearnerView()).remaining)
            assertFalse(advancementDecision(p, unit, s.v2LearnerView()).allowed)
        }
    }

    private fun record(s: LocalStore, p: V2CurriculumPack, id: String, answer: String) {
        // Since T14-b the independent checks sit in the end-of-unit check session; search the whole unit.
        val step = p.unit(4)!!.sessions.flatMap { it.steps }.single { id in it.itemIds }
        s.v2RecordGovernedAttempt(prepareGovernedAttempt(p, id, answer, PRESCRIBED_CHECK, step.id, "row-${s.v2Attempts().size}")!!)
    }
    private fun context(): Context = ApplicationProvider.getApplicationContext()
    companion object {
        val root get() = File(if (File("app").isDirectory) "." else "..")
        internal val current by lazy { V2PackLoader.parse(File(root, "app/src/main/assets/content/v2-curriculum.json").readText()).also { V2PackIdentityIndex.warm(it) } }
        internal fun present(p: V2CurriculumPack) = p.unit(4)!!.sessions.single { it.id == "u4-s-present" }
        internal fun objective(p: V2CurriculumPack) = p.unit(4)!!.objectives.single { it.id == "obj-u4-present" }
    }
}
