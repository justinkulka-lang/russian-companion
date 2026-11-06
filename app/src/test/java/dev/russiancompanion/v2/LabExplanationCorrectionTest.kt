package dev.russiancompanion.v2

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.curriculum.*
import dev.russiancompanion.v2.ui.V2App
import dev.russiancompanion.v2.ui.labExplanationSteps
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class LabExplanationCorrectionTest {
    @get:Rule val compose = createComposeRule()
    @Test fun wrongGrammarAnswerCanReviewDeclaredTeachingWithoutWritingProgress() = check(8, "grammar", "Grammar Lab", false)
    @Test fun correctVocabularyAnswerCanReviewTeachingAndKeepItsSavedSelection() = check(3, "vocabulary", "Vocabulary and forms", true)
    @Test fun crossUnitDeclarationsCannotRevealFutureTeaching() {
        val p = V2PackLoader.parse(File(if(File("app").isDirectory) "." else "..", "app/src/main/assets/content/v2-curriculum.json").readText())
        val item = p.item("u8-g-availability-0")!!
        val future = p.unit(30)!!.sessions.flatMap { it.steps }.first { it.kind == "TEACH" }
        val changed = p.copy(units = p.units.map { unit -> if (unit.number != 8) unit else unit.copy(objectives = unit.objectives.map {
            if (it.id != item.objectiveId) it else it.copy(instructionStepIds = listOf(future.id), remediationStepIds = listOf(future.id))
        }) })
        assertTrue(labExplanationSteps(changed, item).isEmpty())
    }
    private fun check(number: Int, lab: String, title: String, correct: Boolean) {
        val p = V2PackLoader.parse(File(if(File("app").isDirectory) "." else "..", "app/src/main/assets/content/v2-curriculum.json").readText())
        val context: Context = ApplicationProvider.getApplicationContext()
        LocalStore.openInMemory(context).use { store ->
            store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = number, startingPoint = "RESUME_FROM_BOOK"))
            val item = practiceQueue(p, store.v2LearnerView(), lab).first()
            val answer = if (correct) item.answer else item.options.first { it != item.answer }
            store.v2RecordGovernedAttempt(prepareGovernedAttempt(p, item.id, answer, OPTIONAL_PRACTICE, practiceStepId(lab), "2026-10-12T22:00")!!)
            val before = store.v2LearnerView()
            val objective = p.unit(number)!!.objectives.single { it.id == item.objectiveId }
            val teaching = p.unit(number)!!.sessions.flatMap { it.steps }.filter { it.id in objective.instructionStepIds && it.kind == "TEACH" }
            assertTrue(teaching.isNotEmpty())
            compose.setContent { V2App(store = store, pack = p, onRetryOpen = {}) }
            compose.onNodeWithText("Practice").performClick()
            compose.onNodeWithText(title).performScrollTo().performClick()
            compose.onNodeWithTag("v2-lab-review").performScrollTo().performClick()
            teaching.forEach { compose.onNodeWithTag("v2-lab-explanation-${it.id}").performScrollTo().assertTextEquals(it.body) }
            teaching.flatMap { it.exampleIds }.distinct().forEach { id ->
                compose.onNodeWithTag("v2-lab-explanation-example-$id").performScrollTo().assertTextEquals(p.example(id)!!.display)
            }
            compose.onNodeWithTag("v2-lab-close-explanation").performScrollTo().performClick()
            compose.onNodeWithTag("v2-lab-option-$answer").performScrollTo().assertIsSelected()
            assertEquals(before, store.v2LearnerView())
            assertTrue(store.v2StepProgress().isEmpty())
            assertTrue(creditItemIds(p, store.v2LearnerView()).isEmpty())
        }
    }
}
