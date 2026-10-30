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
class Unit9CompositionFeedbackCorrectionTest {
    @get:Rule val compose = createComposeRule()
    @Test fun compositionAndAgreementFeedbackExplainsTheSourceDistinctions() {
        val p = pack()
        val items = p.items.filter { it.objectiveId in setOf("obj-u9-thousands", "obj-u9-odindva") }
        assertEquals(11, items.size)
        items.forEach { item ->
            assertFalse(item.id, item.feedback.startsWith("Does"))
            assertEquals(item.feedback, item.assessment!!.whyAnswerDemonstratesObjective)
            assertEquals(item.distractorRationales, item.assessment.distractorDifference)
            if (item.id == "u9-i-odindva-4") {
                // This earlier distinct-noun correction explains gender without numeric instead/omits wording.
                assertEquals("час", item.promptLetter)
                assertEquals(listOf("один", "одна"), item.options)
                assertEquals("один", item.answer)
                assertEquals("ODIN_MASCULINE", item.assessment.testedFeature)
                assertEquals(listOf("", "одна is the feminine singular form; the supplied example with час uses masculine singular один."), item.distractorRationales)
            } else {
                assertTrue(item.id, item.distractorRationales.filter { it.isNotBlank() }.all { it.contains("instead") || it.contains("omits") })
            }
        }
        assertTrue(p.item("u9-i-thousands-3")!!.feedback.contains("1,000 + 900 + 80 + 4"))
        assertTrue(p.item("u9-i-thousands-4")!!.feedback.contains("genitive singular") && p.item("u9-i-thousands-4")!!.feedback.contains("genitive plural"))
        assertTrue(p.item("u9-i-odindva-2")!!.feedback.contains("neuter singular"))
        assertTrue(p.item("u9-i-odindva-3")!!.feedback.contains("one pair") && p.item("u9-i-odindva-3")!!.feedback.contains("plural"))
        assertTrue(p.item("u9-i-odindva-5")!!.feedback.contains("feminine") && p.item("u9-i-odindva-5")!!.feedback.contains("masculine or neuter"))
        val validation = V2Validator.validate(p)
        assertTrue(validation.errors.joinToString("\n"), validation.ok)
    }
    @Test fun wrongThousandsGroupDisplaysTheCaseDistinction() {
        val p = pack(); val item = p.item("u9-i-thousands-4")!!
        val step = p.unit(9)!!.sessions.flatMap { it.steps }.first { item.id in it.itemIds }
        val owner = p.step(step.id)!!
        val context: Context = ApplicationProvider.getApplicationContext()
        LocalStore.openInMemory(context).use { store ->
            store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = 9, startingPoint = "RESUME_FROM_BOOK",
                currentSessionId = owner.first.id, currentStepIndex = owner.first.steps.indexOf(step)).verifiedResume(p))
            compose.setContent { V2App(store = store, pack = p, onRetryOpen = {}) }
            compose.onNodeWithTag("v2-continue").performClick()
            compose.onNodeWithTag("v2-option-${item.id}-пять ~ де́вять ты́сяч").performScrollTo().performClick()
            compose.onNodeWithTag("v2-item-feedback-${item.id}").performScrollTo().assertTextEquals("Not the reviewed answer. ${item.feedback}")
            assertFalse(item.id in creditItemIds(p, store.v2LearnerView()))
        }
    }
    private fun root() = File(if (File("app").isDirectory) "." else "..")
    private fun pack() = V2PackLoader.parse(File(root(), "app/src/main/assets/content/v2-curriculum.json").readText())
}
