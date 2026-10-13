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
class Unit14FeedbackCorrectionTest {
    @get:Rule val compose = createComposeRule()
    @Test fun feedbackExplainsFutureLimitsAndImpersonalExperiencers() {
        val p = pack()
        val ids = listOf("u14-g-future-aspect-0", "u14-i-future-aspect-1", "u14-i-future-aspect-2", "u14-g-future-person-0", "u14-i-future-person-1", "u14-i-future-person-2", "u14-g-impersonal-0", "u14-i-impersonal-1", "u14-i-impersonal-2")
        for (id in ids) {
            val item = p.item(id)!!
            assertFalse(id, item.feedback.startsWith("Does"))
            assertEquals(item.feedback, item.assessment!!.whyAnswerDemonstratesObjective)
            assertEquals(item.distractorRationales, item.assessment.distractorDifference)
            assertTrue(id, item.distractorRationales.drop(1).all { !it.startsWith("Does") })
        }
        assertTrue(p.item("u14-g-future-aspect-0")!!.feedback.contains("does not automatically require perfective"))
        assertTrue(p.item("u14-i-future-aspect-1")!!.feedback.contains("not necessarily completion of a task"))
        assertTrue(p.item("u14-i-future-aspect-2")!!.feedback.contains("does not mean the activity lasts forever"))
        assertTrue(p.item("u14-g-future-person-0")!!.feedback.contains("auxiliary agrees with that plural subject"))
        assertTrue(p.item("u14-i-future-person-1")!!.feedback.contains("third-person plural future"))
        assertTrue(p.item("u14-i-future-person-2")!!.distractorRationales[1].contains("first-person plural"))
        for (id in ids.takeLast(3)) {
            val item = p.item(id)!!
            assertTrue(id, item.feedback.contains("Both options are impersonal past"))
            assertTrue(id, item.feedback.contains("neuter singular"))
            assertTrue(id, item.distractorRationales[1].contains("also impersonal past"))
        }
        assertTrue(p.item("u14-i-impersonal-1")!!.feedback.contains("experiencers are dative plural"))
        assertTrue(p.item("u14-i-impersonal-2")!!.feedback.contains("feminine dative singular"))
        val validation = V2Validator.validate(p)
        assertTrue(validation.errors.joinToString("\n"), validation.ok)
    }
    @Test fun maleColdExampleDoesNotAnswerFemaleColdQuestion() {
        val p = pack(); val item = p.item("u14-i-impersonal-2")!!
        val step = p.unit(14)!!.sessions.flatMap { it.steps }.first { item.id in it.itemIds }
        val owner = p.step(step.id)!!
        val context: Context = ApplicationProvider.getApplicationContext()
        LocalStore.openInMemory(context).use { store ->
            store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = 14, startingPoint = "RESUME_FROM_BOOK",
                currentSessionId = owner.first.id, currentStepIndex = owner.first.steps.indexOf(step)).verifiedResume(p))
            compose.setContent { V2App(store = store, pack = p, onRetryOpen = {}) }
            compose.onNodeWithTag("v2-continue").performClick()
            compose.onNodeWithTag("v2-option-${item.id}-Ему было холодно").performScrollTo().performClick()
            compose.onNodeWithTag("v2-item-feedback-${item.id}").performScrollTo().assertTextEquals("Not the reviewed answer. ${item.feedback}")
            assertFalse(item.id in creditItemIds(p, store.v2LearnerView()))
        }
    }
    @Test fun durationFreeOptionDoesNotAnswerBoundedWorkQuestion() {
        val p = pack(); val item = p.item("u14-i-future-aspect-1")!!
        val step = p.unit(14)!!.sessions.flatMap { it.steps }.first { item.id in it.itemIds }
        val owner = p.step(step.id)!!
        val context: Context = ApplicationProvider.getApplicationContext()
        LocalStore.openInMemory(context).use { store ->
            store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = 14, startingPoint = "RESUME_FROM_BOOK",
                currentSessionId = owner.first.id, currentStepIndex = owner.first.steps.indexOf(step)).verifiedResume(p))
            compose.setContent { V2App(store = store, pack = p, onRetryOpen = {}) }
            compose.onNodeWithTag("v2-continue").performClick()
            compose.onNodeWithTag("v2-option-${item.id}-Я бу́ду рабо́тать").performScrollTo().performClick()
            compose.onNodeWithTag("v2-item-feedback-${item.id}").performScrollTo().assertTextEquals("Not the reviewed answer. ${item.feedback}")
            assertFalse(item.id in creditItemIds(p, store.v2LearnerView()))
        }
    }
    @Test fun historicalQuestionFeedbackCreditIsRetainedButStale() {
        val p = pack(); val item = p.item("u14-i-impersonal-2")!!
        val ledger = Json.parseToJsonElement(File(root(), "audit/content-corrections/UNIT14_FEEDBACK_CORRECTION.json").readText()).jsonObject
        val before = ledger.getValue("changes").jsonArray.map { it.jsonObject.getValue("before").jsonObject }.single { it.getValue("id").jsonPrimitive.content == item.id }
        val oldItem = Json { ignoreUnknownKeys = true }.decodeFromJsonElement<V2CheckItem>(before)
        val old = p.copy(items = p.items.map { if (it.id == item.id) oldItem else it })
        val step = p.unit(14)!!.sessions.flatMap { it.steps }.first { item.id in it.itemIds }
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
