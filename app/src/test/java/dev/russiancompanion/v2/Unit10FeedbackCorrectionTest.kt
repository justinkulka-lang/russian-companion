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
class Unit10FeedbackCorrectionTest {
    @get:Rule val compose = createComposeRule()
    @Test fun feedbackExplainsPronounReferentsAndAnimateObjectCase() {
        val p = pack()
        val ids = listOf("u10-g-pronoun-0", "u10-i-pronoun-1", "u10-i-pronoun-2", "u10-i-pronoun-3", "u10-i-pronoun-4", "u10-g-animate-0", "u10-i-animate-1")
        for (id in ids) {
            val item = p.item(id)!!
            assertFalse(id, item.feedback.startsWith("Does"))
            assertEquals(item.feedback, item.assessment!!.whyAnswerDemonstratesObjective)
            assertEquals(item.distractorRationales, item.assessment.distractorDifference)
        }
        assertTrue(p.item("u10-g-pronoun-0")!!.feedback.contains("first-person singular"))
        assertTrue(p.item("u10-i-pronoun-1")!!.distractorRationales[1].contains("first-person singular"))
        assertTrue(p.item("u10-i-pronoun-2")!!.feedback.contains("singular informal"))
        assertTrue(p.item("u10-i-pronoun-3")!!.feedback.contains("polite address to one person"))
        assertTrue(p.item("u10-i-pronoun-4")!!.feedback.contains("third-person forms can change after prepositions"))
        for (id in ids.takeLast(2)) {
            val feedback = p.item(id)!!.feedback
            assertTrue(feedback.contains("accusative object") && feedback.contains("nominative"))
            assertTrue(feedback.contains("genitive"))
        }
        val validation = V2Validator.validate(p)
        assertTrue(validation.errors.joinToString("\n"), validation.ok)
    }
    @Test fun wrongObjectSelectionDisplaysTheSubjectContrast() {
        val p = pack(); val item = p.item("u10-i-animate-1")!!
        val step = p.unit(10)!!.sessions.flatMap { it.steps }.first { item.id in it.itemIds }
        val owner = p.step(step.id)!!
        val context: Context = ApplicationProvider.getApplicationContext()
        LocalStore.openInMemory(context).use { store ->
            store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = 10, startingPoint = "RESUME_FROM_BOOK",
                currentSessionId = owner.first.id, currentStepIndex = owner.first.steps.indexOf(step)).verifiedResume(p))
            compose.setContent { V2App(store = store, pack = p, onRetryOpen = {}) }
            compose.onNodeWithTag("v2-continue").performClick()
            compose.onNodeWithTag("v2-option-${item.id}-этих женщин").performScrollTo().performClick()
            compose.onNodeWithTag("v2-item-feedback-${item.id}").performScrollTo().assertTextEquals("Not the reviewed answer. ${item.feedback}")
            assertFalse(item.id in creditItemIds(p, store.v2LearnerView()))
        }
    }
    private fun root() = File(if (File("app").isDirectory) "." else "..")
    private fun pack() = V2PackLoader.parse(File(root(), "app/src/main/assets/content/v2-curriculum.json").readText())
}
