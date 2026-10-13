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
class Unit13FeedbackCorrectionTest {
    @get:Rule val compose = createComposeRule()
    @Test fun feedbackExplainsAspectAndReportedSpeechWithoutUniversalRules() {
        val p = pack()
        val ids = listOf("u13-g-past-aspect-0", "u13-i-past-aspect-1", "u13-i-past-aspect-2", "u13-g-tense-keep-0", "u13-i-tense-keep-1", "u13-i-tense-keep-2", "u13-g-infinitive-0", "u13-i-infinitive-1", "u13-i-infinitive-2", "u13-g-reported-0", "u13-i-reported-1", "u13-i-reported-2")
        for (id in ids) {
            val item = p.item(id)!!
            assertFalse(id, item.feedback.startsWith("Does"))
            assertEquals(item.feedback, item.assessment!!.whyAnswerDemonstratesObjective)
            assertEquals(item.distractorRationales, item.assessment.distractorDifference)
            assertTrue(id, item.distractorRationales.drop(1).all { !it.startsWith("Does") })
        }
        assertTrue(p.item("u13-g-past-aspect-0")!!.feedback.contains("English simple past wording alone does not determine Russian aspect"))
        assertTrue(p.item("u13-i-past-aspect-1")!!.feedback.contains("completed activity can still be described with imperfective"))
        assertTrue(p.item("u13-i-past-aspect-2")!!.feedback.contains("gender agreement rather than selecting a different aspect"))
        assertTrue(p.item("u13-g-tense-keep-0")!!.feedback.contains("present-time nationality"))
        assertTrue(p.item("u13-i-tense-keep-1")!!.feedback.contains("embedded verb remains future"))
        assertTrue(p.item("u13-i-tense-keep-2")!!.feedback.contains("not mandatory in every report"))
        for (id in listOf("u13-g-infinitive-0", "u13-i-infinitive-2"))
            assertTrue(p.item(id)!!.feedback.contains("say/tell meaning"))
        assertTrue(p.item("u13-i-infinitive-1")!!.feedback.contains("rather than assuming that adding any prefix"))
        assertTrue(p.item("u13-g-reported-0")!!.feedback.contains("not automatic rules for every report"))
        assertTrue(p.item("u13-i-reported-1")!!.feedback.contains("without a past reporting frame"))
        assertTrue(p.item("u13-i-reported-2")!!.feedback.contains("Both clauses refer to the future"))
        val validation = V2Validator.validate(p)
        assertTrue(validation.errors.joinToString("\n"), validation.ok)
    }
    @Test fun moscowPlanDoesNotAnswerDirectEveningQuestion() {
        val p = pack(); val item = p.item("u13-i-reported-1")!!
        val step = p.unit(13)!!.sessions.flatMap { it.steps }.first { item.id in it.itemIds }
        val owner = p.step(step.id)!!
        val context: Context = ApplicationProvider.getApplicationContext()
        LocalStore.openInMemory(context).use { store ->
            store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = 13, startingPoint = "RESUME_FROM_BOOK",
                currentSessionId = owner.first.id, currentStepIndex = owner.first.steps.indexOf(step)).verifiedResume(p))
            compose.setContent { V2App(store = store, pack = p, onRetryOpen = {}) }
            compose.onNodeWithTag("v2-continue").performClick()
            compose.onNodeWithTag("v2-option-${item.id}-Когда́ я бу́ду в Москве́, я бу́ду говори́ть то́лько по-ру́сски").performScrollTo().performClick()
            compose.onNodeWithTag("v2-item-feedback-${item.id}").performScrollTo().assertTextEquals("Not the reviewed answer. ${item.feedback}")
            assertFalse(item.id in creditItemIds(p, store.v2LearnerView()))
        }
    }
    @Test fun perfectiveChoiceDoesNotAnswerImperfectivePastQuestion() {
        val p = pack(); val item = p.item("u13-i-past-aspect-1")!!
        val step = p.unit(13)!!.sessions.flatMap { it.steps }.first { item.id in it.itemIds }
        val owner = p.step(step.id)!!
        val context: Context = ApplicationProvider.getApplicationContext()
        LocalStore.openInMemory(context).use { store ->
            store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = 13, startingPoint = "RESUME_FROM_BOOK",
                currentSessionId = owner.first.id, currentStepIndex = owner.first.steps.indexOf(step)).verifiedResume(p))
            compose.setContent { V2App(store = store, pack = p, onRetryOpen = {}) }
            compose.onNodeWithTag("v2-continue").performClick()
            compose.onNodeWithTag("v2-option-${item.id}-я прочитал").performScrollTo().performClick()
            compose.onNodeWithTag("v2-item-feedback-${item.id}").performScrollTo().assertTextEquals("Not the reviewed answer. ${item.feedback}")
            assertFalse(item.id in creditItemIds(p, store.v2LearnerView()))
        }
    }
    @Test fun historicalQuestionFeedbackCreditIsRetainedButStale() {
        val p = pack(); val item = p.item("u13-i-reported-1")!!
        val ledger = Json.parseToJsonElement(File(root(), "audit/content-corrections/UNIT13_FEEDBACK_CORRECTION.json").readText()).jsonObject
        val before = ledger.getValue("changes").jsonArray.map { it.jsonObject.getValue("before").jsonObject }.single { it.getValue("id").jsonPrimitive.content == item.id }
        val oldItem = Json { ignoreUnknownKeys = true }.decodeFromJsonElement<V2CheckItem>(before)
        val old = p.copy(items = p.items.map { if (it.id == item.id) oldItem else it })
        val step = p.unit(13)!!.sessions.flatMap { it.steps }.first { item.id in it.itemIds }
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
