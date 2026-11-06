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
class Unit29ActiveTimeCorrectionTest {
    @get:Rule val compose = createComposeRule()
    @Test fun creditedCheckMustInterpretADistinctAdmittedFormRatherThanTheOppositeLabel() {
        val p = pack(); val item = p.item("u29-i-active-2")!!
        val step = p.unit(29)!!.sessions.flatMap { it.steps }.first { item.id in it.itemIds }
        LocalStore.openInMemory(context()).use { store ->
            store.v2RecordGovernedAttempt(prepareGovernedAttempt(p, item.id, item.answer, PRESCRIBED_CHECK, step.id, "2026-10-13T00:00")!!)
            assertTrue("The selected answer earns objective credit", item.id in creditItemIds(p, store.v2LearnerView()))
            assertEquals("That credit must interpret the distinct present form, not complement a displayed past form", "живу́щие", item.promptLetter)
        }
        assertFalse(item.prompt.contains("other time"))
        assertEquals("in the present", item.answer)
        assertTrue(p.items.filter { it.objectiveId == item.objectiveId && it.id != item.id }.none { it.promptLetter == item.promptLetter })
        val validation = V2Validator.validate(p)
        assertTrue(validation.errors.joinToString("\n"), validation.ok)
    }
    @Test fun playerRejectsPastLabelForLivingAndKeepsTheCorrectExplanation() {
        val p = pack(); val item = p.item("u29-i-active-2")!!
        val step = p.unit(29)!!.sessions.flatMap { it.steps }.first { item.id in it.itemIds }
        val owner = p.step(step.id)!!
        LocalStore.openInMemory(context()).use { store ->
            store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = 29, startingPoint = "RESUME_FROM_BOOK",
                currentSessionId = owner.first.id, currentStepIndex = owner.first.steps.indexOf(step)).verifiedResume(p))
            compose.setContent { V2App(store = store, pack = p, onRetryOpen = {}) }
            compose.onNodeWithTag("v2-continue").performClick()
            compose.onNodeWithTag("v2-option-${item.id}-in the past").performScrollTo().performClick()
            compose.onNodeWithTag("v2-item-feedback-${item.id}").performScrollTo().assertTextEquals("Not the reviewed answer. ${item.feedback}")
            assertFalse(item.id in creditItemIds(p, store.v2LearnerView()))
        }
    }
    @Test fun coordinatedApplicationAndKeyEditsCannotMakeLivingPast() {
        val p = pack(); val item = p.item("u29-i-active-2")!!; val a = item.assessment!!
        val ids = a.relationParticipantIds; val wrong = "in the past"
        val rationales = listOf(item.distractorRationales[1], "", item.distractorRationales[2])
        val changed = item.copy(answer = wrong, distractorRationales = rationales, assessment = a.copy(
            relationParticipantIds = listOf(ids[0], ids[2], ids[1], ids[3]), answerSourceIds = listOf(ids[2]),
            distractorSourceIds = listOf(ids[1], "", ids[3]), distractorDifference = rationales))
        val records = p.provenanceRecords.map { r -> if (r.sourceRecordId != "ca-rule-context:${item.id}") r else r.copy(
            synthesis = r.synthesis!!.copy(applications = r.synthesis.applications.map { it.copy(answer = wrong, distractors = listOf(item.answer, "living")) })) }
        val validation = V2Validator.validate(p.copy(items = p.items.map { if (it.id == item.id) changed else it }, provenanceRecords = records))
        assertFalse(validation.ok)
        assertTrue(validation.errors.joinToString("\n"), validation.errors.any { it.contains("complete rule context") })
    }
    private fun context(): Context = ApplicationProvider.getApplicationContext()
    private fun root() = File(if (File("app").isDirectory) "." else "..")
    private fun pack() = V2PackLoader.parse(File(root(), "app/src/main/assets/content/v2-curriculum.json").readText())
}
