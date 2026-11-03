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
            assertTrue(session.steps.indexOfFirst { guide.id in it.itemIds } < session.steps.indexOfFirst { id in it.itemIds })
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

    private fun record(s: LocalStore, p: V2CurriculumPack, id: String, answer: String) {
        val step = present(p).steps.single { id in it.itemIds }
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
