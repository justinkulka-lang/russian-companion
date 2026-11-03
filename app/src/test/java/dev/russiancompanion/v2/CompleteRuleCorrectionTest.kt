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
class CompleteRuleCorrectionTest {
    @get:Rule val compose = createComposeRule()
    @Test fun availabilityPlayerShowsCompleteRulesWithItsFullRussianCue() = show("u8-i-availability-1", 8)
    @Test fun possessionPlayerShowsCompleteRulesWithItsFullRussianCue() = show("u10-i-have-1", 10)
    private fun show(id: String, number: Int) {
        val p = pack(); val item = p.item(id)!!
        val step = p.unit(number)!!.sessions.flatMap { it.steps }.first { id in it.itemIds }
        val owner = p.step(step.id)!!
        val context: Context = ApplicationProvider.getApplicationContext()
        LocalStore.openInMemory(context).use { store ->
            store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = number, startingPoint = "RESUME_FROM_BOOK",
                currentSessionId = owner.first.id, currentStepIndex = owner.first.steps.indexOf(step)).verifiedResume(p))
            compose.setContent { V2App(store = store, pack = p, onRetryOpen = {}) }
            compose.onNodeWithTag("v2-continue").performClick()
            // Multiple distinct questions can legitimately use the same Russian cue.
            // Check every expected instance and its own prompt/options; selecting first()
            // would hide a missing or duplicated question.
            val cueItems=step.itemIds.mapNotNull { p.item(it) }.filter {
                it.promptLetterRole=="CUE" && it.promptLetter==item.promptLetter
            }
            assertTrue(cueItems.any { it.id==item.id })
            val cue=hasTestTag("v2-prompt-letter") and hasText(item.promptLetter!!)
            compose.onAllNodes(cue).assertCountEquals(cueItems.size)
            cueItems.forEachIndexed { index,cued ->
                compose.onAllNodes(cue)[index].performScrollTo().assertIsDisplayed()
                compose.onNodeWithTag("v2-item-prompt-${cued.id}").performScrollTo().assertIsDisplayed().assertTextEquals(cued.prompt)
                cued.options.forEach { option -> compose.onNodeWithTag("v2-option-${cued.id}-$option").performScrollTo().assertIsDisplayed() }
            }
        }
    }
    @Test fun coordinatedKeyAndApplicationEditsCannotReverseTheFrozenContext() {
        val p = pack(); val item = p.item("u8-i-availability-1")!!
        val wrong = item.options.single { it != item.answer }
        val ids = item.assessment!!.relationParticipantIds
        val a = item.assessment
        val donor = p.item("u8-i-availability-3")!!
        val rationale = donor.distractorRationales.first { it.isNotBlank() }
        val rationales = item.options.map { if (it == wrong) "" else rationale }
        val changed = item.copy(answer = wrong, feedback = donor.feedback, distractorRationales = rationales, assessment = a.copy(
            whyAnswerDemonstratesObjective = donor.assessment!!.whyAnswerDemonstratesObjective, distractorDifference = rationales,
            relationParticipantIds = listOf(ids[0], ids[2], ids[1]), answerSourceIds = listOf(ids[2]),
            distractorSourceIds = item.options.map { if (it == wrong) "" else ids[1] }))
        val records = p.provenanceRecords.map { record ->
            if (record.sourceRecordId != "ca-rule-context:${item.id}") record else record.copy(synthesis = record.synthesis!!.copy(
                applications = record.synthesis.applications.map { it.copy(answer = wrong, distractors = listOf(item.answer)) }))
        }
        assertTrue(records.zip(p.provenanceRecords).all { (x,y) -> x.originalSourceText == y.originalSourceText && x.sourceHash == y.sourceHash && x.shippedText == y.shippedText && x.contentHash == y.contentHash })
        val result = V2Validator.validate(p.copy(items = p.items.map { if (it.id == item.id) changed else it }, provenanceRecords = records))
        assertFalse("Reversed context must not pass the full validator: ${result.errors}", result.ok)
        assertTrue(result.errors.joinToString("\n"), result.errors.any { it.contains("complete rule context") })
    }
    private val objectives = setOf("obj-u8-availability", "obj-u10-have", "obj-u10-absence")
    private fun pack() = V2PackLoader.parse(File(if (File("app").isDirectory) "." else "..", "app/src/main/assets/content/v2-curriculum.json").readText())
}
