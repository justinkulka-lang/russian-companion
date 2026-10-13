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
class Unit11FeedbackCorrectionTest {
    @get:Rule val compose = createComposeRule()
    @Test fun feedbackExplainsPastAgreementAndInfinitiveContrast() {
        val p = pack()
        val ids = listOf("u11-g-past-read-0", "u11-i-past-read-1", "u11-i-past-read-2", "u11-g-past-be-0", "u11-i-past-be-1", "u11-i-past-be-2", "u11-g-reflexive-0", "u11-i-reflexive-1", "u11-i-reflexive-2")
        for (id in ids) {
            val item = p.item(id)!!
            assertFalse(id, item.feedback.startsWith("Does"))
            assertEquals(item.feedback, item.assessment!!.whyAnswerDemonstratesObjective)
            assertEquals(item.distractorRationales, item.assessment.distractorDifference)
            assertTrue(id, item.feedback.contains("plural"))
            assertTrue(id, item.distractorRationales.drop(1).all { !it.startsWith("Does") })
        }
        assertTrue(p.item("u11-g-past-read-0")!!.distractorRationales[1].contains("neuter singular"))
        assertTrue(p.item("u11-i-past-read-1")!!.feedback.contains("masculine singular"))
        assertTrue(p.item("u11-i-past-read-2")!!.feedback.contains("subject matches"))
        assertTrue(p.item("u11-g-past-be-0")!!.feedback.contains("feminine singular"))
        for (id in listOf("u11-i-past-be-1", "u11-i-past-be-2"))
            assertTrue(p.item(id)!!.distractorRationales[1].contains("masculine singular"))
        val reflexive = p.item("u11-g-reflexive-0")!!
        assertTrue(reflexive.distractorRationales[2].contains("infinitive"))
        assertTrue(p.item("u11-i-reflexive-2")!!.feedback.contains("ending alone does not distinguish"))
        assertTrue(ids.none { p.item(it)!!.feedback.contains("perfective") })
        val validation = V2Validator.validate(p)
        assertTrue(validation.errors.joinToString("\n"), validation.ok)
    }
    @Test fun wrongPluralSelectionDisplaysFemininePastExplanation() {
        val p = pack(); val item = p.item("u11-i-reflexive-2")!!
        val step = p.unit(11)!!.sessions.flatMap { it.steps }.first { item.id in it.itemIds }
        val owner = p.step(step.id)!!
        val context: Context = ApplicationProvider.getApplicationContext()
        LocalStore.openInMemory(context).use { store ->
            store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = 11, startingPoint = "RESUME_FROM_BOOK",
                currentSessionId = owner.first.id, currentStepIndex = owner.first.steps.indexOf(step)).verifiedResume(p))
            compose.setContent { V2App(store = store, pack = p, onRetryOpen = {}) }
            compose.onNodeWithTag("v2-continue").performClick()
            compose.onNodeWithTag("v2-option-${item.id}-учи́лись").performScrollTo().performClick()
            compose.onNodeWithTag("v2-item-feedback-${item.id}").performScrollTo().assertTextEquals("Not the reviewed answer. ${item.feedback}")
            assertFalse(item.id in creditItemIds(p, store.v2LearnerView()))
        }
    }
    @Test fun historicalQuestionFeedbackCreditIsRetainedButStale() {
        val p = pack(); val item = p.item("u11-i-reflexive-2")!!
        val ledger = Json.parseToJsonElement(File(root(), "audit/content-corrections/UNIT11_FEEDBACK_CORRECTION.json").readText()).jsonObject
        val before = ledger.getValue("changes").jsonArray.map { it.jsonObject.getValue("before").jsonObject }.single { it.getValue("id").jsonPrimitive.content == item.id }
        val oldItem = Json { ignoreUnknownKeys = true }.decodeFromJsonElement<V2CheckItem>(before)
        val old = p.copy(items = p.items.map { if (it.id == item.id) oldItem else it })
        val step = p.unit(11)!!.sessions.flatMap { it.steps }.first { item.id in it.itemIds }
        val context: Context = ApplicationProvider.getApplicationContext()
        LocalStore.openInMemory(context).use { store ->
            store.v2RecordGovernedAttempt(prepareGovernedAttempt(old, item.id, oldItem.answer, PRESCRIBED_CHECK, step.id, "2026-10-12T23:00")!!)
            assertTrue(item.id in creditItemIds(old, store.v2LearnerView()))
            assertFalse(item.id in creditItemIds(p, store.v2LearnerView()))
            assertEquals(1, store.v2Attempts().size)
        }
    }
    private fun root() = File(if (File("app").isDirectory) "." else "..")
    private fun pack() = V2PackLoader.parse(File(root(), "app/src/main/assets/content/v2-curriculum.json").readText())
}
