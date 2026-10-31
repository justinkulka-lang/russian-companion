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
class Unit6FeedbackCorrectionTest {
    @get:Rule val compose = createComposeRule()
    @Test fun feedbackExplainsPossessorRolesPersonNumberAndDestinationCase() {
        val p = pack()
        val items = p.items.filter { it.objectiveId in objectives }
        assertEquals(15, items.size)
        items.forEach { item ->
            assertFalse(item.id, item.feedback.startsWith("Does"))
            assertEquals(item.feedback, item.assessment!!.whyAnswerDemonstratesObjective)
            assertTrue(item.id, item.distractorRationales.filter { it.isNotBlank() }.all { it.contains("instead") || it.contains("repeats") })
            when (item.objectiveId) {
                "obj-u6-third" -> {
                    assertTrue(item.feedback.contains("possessor"))
                    assertTrue(item.feedback.contains("possessed noun"))
                }
                "obj-u6-object" -> {
                    assertTrue(item.feedback.contains("subject") && item.feedback.contains("object"))
                    if (item.assessment.testedFeature == "OBJECT_ORDER") assertTrue(item.feedback.contains("same meaning"))
                    else assertTrue(item.feedback.contains("roles"))
                }
                "obj-u6-pronoun" -> assertTrue(item.feedback.contains("accusative") && item.feedback.contains("person"))
                "obj-u6-direction" -> assertTrue(item.feedback.contains(if (item.answer == "direction") "accusative" else "prepositional"))
            }
        }
        val validation = V2Validator.validate(p)
        assertTrue(validation.errors.joinToString("\n"), validation.ok)
    }
    @Test fun wrongPossessiveSelectionDisplaysThePossessorExplanation() {
        val p = pack(); val item = p.item("u6-i-third-1")!!
        val step = p.unit(6)!!.sessions.flatMap { it.steps }.first { item.id in it.itemIds }
        val owner = p.step(step.id)!!
        val context: Context = ApplicationProvider.getApplicationContext()
        LocalStore.openInMemory(context).use { store ->
            store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = 6, startingPoint = "RESUME_FROM_BOOK",
                currentSessionId = owner.first.id, currentStepIndex = owner.first.steps.indexOf(step)).verifiedResume(p))
            compose.setContent { V2App(store = store, pack = p, onRetryOpen = {}) }
            compose.onNodeWithTag("v2-continue").performClick()
            compose.onNodeWithTag("v2-option-${item.id}-его").performScrollTo().performClick()
            compose.onNodeWithTag("v2-item-feedback-${item.id}").performScrollTo().assertTextEquals("Not the reviewed answer. ${item.feedback}")
            assertFalse(item.id in creditItemIds(p, store.v2LearnerView()))
        }
    }
    private val objectives = setOf("obj-u6-third", "obj-u6-object", "obj-u6-pronoun", "obj-u6-direction")
    private fun root() = File(if (File("app").isDirectory) "." else "..")
    private fun pack() = V2PackLoader.parse(File(root(), "app/src/main/assets/content/v2-curriculum.json").readText())
}
