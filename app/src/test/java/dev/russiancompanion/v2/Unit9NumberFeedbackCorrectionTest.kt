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
class Unit9NumberFeedbackCorrectionTest {
    @get:Rule val compose = createComposeRule()
    @Test fun numberFeedbackDistinguishesActualValuesAndPlaceValue() {
        val p = pack()
        val objectives = setOf("obj-u9-cardinal", "obj-u9-teens", "obj-u9-tens", "obj-u9-hundreds")
        val items = p.items.filter { it.objectiveId in objectives }
        assertEquals(17, items.size)
        for (item in items) {
            assertFalse(item.id, item.feedback.startsWith("Does"))
            assertTrue(item.id, item.feedback.contains("(${item.promptLetter})"))
            assertEquals(item.feedback, item.assessment!!.whyAnswerDemonstratesObjective)
            assertEquals(item.distractorRationales, item.assessment.distractorDifference)
            assertTrue(item.id, item.distractorRationales.filter { it.isNotBlank() }.all { it.contains("instead of") })
        }
        assertTrue(p.item("u9-i-teens-3")!!.feedback.contains("twelve (12)") && p.item("u9-i-teens-3")!!.feedback.contains("twenty (20)"))
        assertTrue(p.item("u9-i-hundreds-1")!!.feedback.contains("three hundreds") && p.item("u9-i-hundreds-1")!!.feedback.contains("three tens"))
        assertTrue(p.item("u9-i-hundreds-2")!!.feedback.contains("five hundred (500)") && p.item("u9-i-hundreds-2")!!.feedback.contains("five (5)"))
        val validation = V2Validator.validate(p)
        assertTrue(validation.errors.joinToString("\n"), validation.ok)
    }
    @Test fun wrongHundredsSelectionDisplaysTheDifferentValues() {
        val p = pack(); val item = p.item("u9-i-hundreds-1")!!
        val step = p.unit(9)!!.sessions.flatMap { it.steps }.first { item.id in it.itemIds }
        val owner = p.step(step.id)!!
        val context: Context = ApplicationProvider.getApplicationContext()
        LocalStore.openInMemory(context).use { store ->
            store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = 9, startingPoint = "RESUME_FROM_BOOK",
                currentSessionId = owner.first.id, currentStepIndex = owner.first.steps.indexOf(step)).verifiedResume(p))
            compose.setContent { V2App(store = store, pack = p, onRetryOpen = {}) }
            compose.onNodeWithTag("v2-continue").performClick()
            compose.onNodeWithTag("v2-option-${item.id}-три́дцать").performScrollTo().performClick()
            compose.onNodeWithTag("v2-item-feedback-${item.id}").performScrollTo().assertTextEquals("Not the reviewed answer. ${item.feedback}")
            assertFalse(item.id in creditItemIds(p, store.v2LearnerView()))
        }
    }
    @Test fun historicalGenericFeedbackCreditIsRetainedButStale() {
        val p = pack(); val item = p.item("u9-i-hundreds-1")!!
        val ledger = Json.parseToJsonElement(File(root(), "audit/content-corrections/UNIT9_NUMBER_FEEDBACK_CORRECTION.json").readText()).jsonObject
        val before = ledger.getValue("changes").jsonArray.map { it.jsonObject.getValue("before").jsonObject }.single { it.getValue("id").jsonPrimitive.content == item.id }
        val oldItem = Json { ignoreUnknownKeys = true }.decodeFromJsonElement<V2CheckItem>(before)
        val old = p.copy(items = p.items.map { if (it.id == item.id) oldItem else it })
        val step = p.unit(9)!!.sessions.flatMap { it.steps }.first { item.id in it.itemIds }
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
