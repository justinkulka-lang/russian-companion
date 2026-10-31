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
import java.io.File
import kotlinx.serialization.json.*

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class Unit12FeedbackCorrectionTest {
    @get:Rule val compose = createComposeRule()
    @Test fun feedbackExplainsFutureCaseAndSpellingContrasts() {
        val p = pack()
        val ids = listOf("u12-g-future-imp-0", "u12-i-future-imp-1", "u12-i-future-imp-2", "u12-g-future-perf-0", "u12-i-future-perf-1", "u12-i-future-perf-2", "u12-g-dative-0", "u12-i-dative-1", "u12-i-dative-2", "u12-g-spelling-0", "u12-i-spelling-1", "u12-i-spelling-2", "u12-g-prep-plural-0", "u12-i-prep-plural-1", "u12-i-prep-plural-2")
        for (id in ids) {
            val item = p.item(id)!!
            assertFalse(id, item.feedback.startsWith("Does"))
            assertEquals(item.feedback, item.assessment!!.whyAnswerDemonstratesObjective)
            assertEquals(item.distractorRationales, item.assessment.distractorDifference)
            assertTrue(id, item.distractorRationales.drop(1).all { !it.startsWith("Does") })
        }
        assertTrue(p.item("u12-g-future-imp-0")!!.feedback.contains("auxiliary plus the same imperfective infinitive"))
        assertTrue(p.item("u12-i-future-imp-2")!!.feedback.contains("address one person politely"))
        assertTrue(p.item("u12-g-future-perf-0")!!.feedback.contains("single conjugated verb"))
        assertTrue(p.item("u12-i-future-perf-1")!!.distractorRationales[1].contains("second-person singular"))
        assertTrue(p.item("u12-g-dative-0")!!.feedback.contains("also be prepositional singular"))
        for (id in listOf("u12-g-dative-0", "u12-i-dative-1", "u12-i-dative-2"))
            assertTrue(p.item(id)!!.distractorRationales[1].contains("nominative plural"))
        assertTrue(p.item("u12-g-spelling-0")!!.feedback.contains("unstressed") && p.item("u12-g-spelling-0")!!.feedback.contains("five-letter"))
        assertTrue(p.item("u12-i-spelling-1")!!.feedback.contains("neuter form") && p.item("u12-i-spelling-1")!!.distractorRationales[1].contains("feminine singular"))
        assertTrue(p.item("u12-i-spelling-2")!!.feedback.contains("incorrect Russian, not a form to use"))
        for (id in listOf("u12-g-prep-plural-0", "u12-i-prep-plural-1", "u12-i-prep-plural-2"))
            assertTrue(p.item(id)!!.feedback.contains("governing preposition"))
        val validation = V2Validator.validate(p)
        assertTrue(validation.errors.joinToString("\n"), validation.ok)
    }
    @Test fun correctSpellingDoesNotAnswerRejectedSpellingQuestion() {
        val p = pack(); val item = p.item("u12-i-spelling-2")!!
        val step = p.unit(12)!!.sessions.flatMap { it.steps }.first { item.id in it.itemIds }
        val owner = p.step(step.id)!!
        val context: Context = ApplicationProvider.getApplicationContext()
        LocalStore.openInMemory(context).use { store ->
            store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = 12, startingPoint = "RESUME_FROM_BOOK",
                currentSessionId = owner.first.id, currentStepIndex = owner.first.steps.indexOf(step)).verifiedResume(p))
            compose.setContent { V2App(store = store, pack = p, onRetryOpen = {}) }
            compose.onNodeWithTag("v2-continue").performClick()
            compose.onNodeWithTag("v2-option-${item.id}-хорошее письмо").performScrollTo().performClick()
            compose.onNodeWithTag("v2-item-feedback-${item.id}").performScrollTo().assertTextEquals("Not the reviewed answer. ${item.feedback}")
            assertFalse(item.id in creditItemIds(p, store.v2LearnerView()))
        }
    }
    @Test fun firstPersonChoiceDoesNotAnswerSecondPersonFutureQuestion() {
        val p = pack(); val item = p.item("u12-i-future-imp-2")!!
        val step = p.unit(12)!!.sessions.flatMap { it.steps }.first { item.id in it.itemIds }
        val owner = p.step(step.id)!!
        val context: Context = ApplicationProvider.getApplicationContext()
        LocalStore.openInMemory(context).use { store ->
            store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = 12, startingPoint = "RESUME_FROM_BOOK",
                currentSessionId = owner.first.id, currentStepIndex = owner.first.steps.indexOf(step)).verifiedResume(p))
            compose.setContent { V2App(store = store, pack = p, onRetryOpen = {}) }
            compose.onNodeWithTag("v2-continue").performClick()
            compose.onNodeWithTag("v2-option-${item.id}-Я бу́ду говори́ть").performScrollTo().performClick()
            compose.onNodeWithTag("v2-item-feedback-${item.id}").performScrollTo().assertTextEquals("Not the reviewed answer. ${item.feedback}")
            assertFalse(item.id in creditItemIds(p, store.v2LearnerView()))
        }
    }
    private fun root() = File(if (File("app").isDirectory) "." else "..")
    private fun pack() = V2PackLoader.parse(File(root(), "app/src/main/assets/content/v2-curriculum.json").readText())
}
