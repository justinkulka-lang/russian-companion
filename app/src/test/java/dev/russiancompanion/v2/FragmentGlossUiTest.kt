package dev.russiancompanion.v2

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.curriculum.*
import dev.russiancompanion.v2.ui.V2App
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class FragmentGlossUiTest {
    @get:Rule val compose = createComposeRule()
    @Test fun unit7TeachingDisplaysBoundedPhraseMeaningAndUnchangedNote() = teaching(7, "u7-st-teach-objectadj-3")
    @Test fun unit10FirstTeachingDisplaysTwoPhrasesWithoutInventedPredicates() = teaching(10, "u10-st-teach-animate-1")
    @Test fun unit10SecondTeachingExplainsTheSourceContextBoundary() = teaching(10, "u10-st-teach-animate-2")
    @Test fun unit7WrongAnswerRepairsAllExamplesAndReturnsToItsSavedSelection() = repair(7, "objectadj", "u7-i-objectadj-3")
    @Test fun unit10WrongAnswerRepairsAllExamplesAndReturnsToItsSavedSelection() = repair(10, "animate", "u10-i-animate-1")

    private fun teaching(number: Int, id: String) {
        val p = FragmentGlossFixture.current; val step = p.step(id)!!
        LocalStore.openInMemory(context()).use { store ->
            resume(store, p, number, step)
            compose.setContent { V2App(store, p, {}) }
            tag("v2-continue").performClick()
            compose.onNodeWithText(step.second.body).performScrollTo().assertIsDisplayed()
            compose.onNodeWithText(FragmentGlossFixture.previous.step(id)!!.second.body).assertDoesNotExist()
            for (exampleId in step.second.exampleIds) {
                tag("v2-example-$exampleId").performScrollTo().assertTextEquals(p.example(exampleId)!!.display)
                tag("v2-example-note-$exampleId").performScrollTo().assertTextEquals(p.example(exampleId)!!.note)
            }
            assertTrue(store.v2Attempts().isEmpty())
        }
    }

    private fun repair(number: Int, stem: String, id: String) {
        val p = FragmentGlossFixture.current; val item = p.item(id)!!
        val check = p.step("u$number-st-check-$stem")!!
        LocalStore.openInMemory(context()).use { store ->
            resume(store, p, number, check)
            compose.setContent { V2App(store, p, {}) }
            tag("v2-continue").performClick()
            val wrong = item.options.first { it != item.answer }
            tag("v2-option-$id-$wrong").performScrollTo().performClick()
            tag("v2-remediation-$id").performScrollTo().performClick()
            val rem = p.step("u$number-st-rem-$stem")!!.second
            compose.onNodeWithText(rem.body).performScrollTo().assertIsDisplayed()
            for (exampleId in rem.exampleIds) {
                tag("v2-example-$exampleId").performScrollTo().assertTextEquals(p.example(exampleId)!!.display)
                tag("v2-example-note-$exampleId").performScrollTo().assertTextEquals(p.example(exampleId)!!.note)
            }
            tag("v2-review-practice").performClick()
            for (guideId in reviewedGuidedPractice(p, rem.id)) {
                for (guidedItemId in p.step(guideId)!!.second.itemIds) {
                    val guide = p.item(guidedItemId)!!
                    tag("v2-option-$guidedItemId-${guide.answer}").performScrollTo().performClick()
                }
            }
            assertFalse(id in creditItemIds(p, store.v2LearnerView()))
            tag("v2-return-to-check").performClick()
            tag("v2-option-$id-$wrong").performScrollTo().assertIsSelected()
            assertFalse(id in creditItemIds(p, store.v2LearnerView()))
            tag("v2-option-$id-${item.answer}").performScrollTo().performClick()
            compose.waitUntil(30_000) { id in creditItemIds(p, store.v2LearnerView()) }
            val objective = p.unit(number)!!.objectives.single { it.id == item.objectiveId }
            assertTrue(objective.checkItemIds.filter { it != id }.none { it in creditItemIds(p, store.v2LearnerView()) })
            assertFalse(advancementDecision(p, p.unit(number)!!, store.v2LearnerView()).allowed)
        }
    }

    private fun resume(store: LocalStore, p: V2CurriculumPack, number: Int, step: Pair<V2Session, V2Step>) {
        store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = number,
            startingPoint = "RESUME_FROM_BOOK", currentSessionId = step.first.id,
            currentStepIndex = step.first.steps.indexOf(step.second)).verifiedResume(p))
    }
    private fun tag(value: String): SemanticsNodeInteraction {
        compose.waitUntil(30_000) { compose.onAllNodesWithTag(value).fetchSemanticsNodes().isNotEmpty() }
        return compose.onNodeWithTag(value)
    }
    private fun context(): Context = ApplicationProvider.getApplicationContext()
}
