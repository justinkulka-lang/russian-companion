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

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class IndeclinableCorrectionTest {
    @get:Rule val compose = createComposeRule()
    private val unchanged = "Indeclinable: unchanged for case or number"
    private val changeable = "Declinable: can change for case"

    @Test fun choicesTeachLanguageBehaviorAndRemediationRestoresAllInstruction() {
        val p = pack()
        val items = p.items.filter { it.kind == "SOURCE_INDECLINABLE" }
        assertEquals(6, items.size)
        items.forEach {
            assertEquals(listOf(unchanged, changeable), it.options)
            assertEquals(if (it.id.startsWith("u5-i-indecl-")) unchanged else changeable, it.answer)
            assertFalse(it.prompt.contains("flag"))
            assertFalse(it.feedback.contains("flag"))
            assertTrue(it.feedback.contains("case"))
        }
        val session = p.unit(5)!!.sessions.first { s -> s.steps.any { it.id == "u5-st-teach-indeclinable-1" } }
        val teaching = session.steps.filter { it.kind == "TEACH" }
        val remediation = session.steps.single { it.id == "u5-st-rem-indeclinable" }
        teaching.forEach { assertTrue(remediation.body.contains(it.body)) }
        assertTrue(remediation.body.contains("кни́га"))
        assertTrue(remediation.body.contains("окно́"))
        assertTrue(V2Validator.validate(p).ok)
    }

    @Test fun lessonDisplaysBehaviorChoicesAndTheRequiredWordCue() {
        val p = pack()
        val item = p.item("u5-i-indecl-такси")!!
        val step = p.unit(5)!!.sessions.flatMap { it.steps }.first { item.id in it.itemIds }
        val owner = p.step(step.id)!!
        val context: Context = ApplicationProvider.getApplicationContext()
        LocalStore.openInMemory(context).use { store ->
            store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = 5,
                startingPoint = "RESUME_FROM_BOOK", currentSessionId = owner.first.id,
                currentStepIndex = owner.first.steps.indexOf(step)).verifiedResume(p))
            compose.setContent { V2App(store = store, pack = p, onRetryOpen = {}) }
            compose.onNodeWithTag("v2-continue").performClick()
            compose.onNode(hasTestTag("v2-prompt-letter") and hasText(item.promptLetter!!)).performScrollTo().assertTextEquals(item.promptLetter)
            item.options.forEach { compose.onNodeWithTag("v2-option-${item.id}-$it").performScrollTo().assertIsDisplayed() }
        }
    }

    @Test fun forgedBehaviorKeyCannotOverrideTheFrozenLexicalClassification() {
        val p = pack()
        val item = p.item("u5-i-indecl-такси")!!
        val zeroHash = java.security.MessageDigest.getInstance("SHA-256").digest("0".toByteArray()).joinToString("") { "%02x".format(it) }
        val forged = p.copy(items = p.items.map { if (it.id == item.id) it.copy(answer = changeable) else it },
            provenanceRecords = p.provenanceRecords.map {
                if (it.sourceRecordId == "nouns.csv:такси#indeclinable")
                    it.copy(originalSourceText = "0", shippedText = "0", sourceHash = zeroHash, contentHash = zeroHash) else it
            })
        assertTrue(V2Validator.validate(forged).errors.any { it.contains("indeclinability binding") })
    }

    @Test fun historicalFlagAttemptRemainsStoredButDoesNotSupplyCurrentCredit() {
        val p = pack()
        val item = p.item("u5-i-indecl-такси")!!
        val oldItem = item.copy(prompt = "Which flag does the admitted row record?", options = listOf("1", "0"),
            answer = "1", feedback = "The frozen row sets the flag to 1.")
        val old = p.copy(items = p.items.map { if (it.id == item.id) oldItem else it })
        val step = p.unit(5)!!.sessions.flatMap { it.steps }.first { item.id in it.itemIds }
        val context: Context = ApplicationProvider.getApplicationContext()
        LocalStore.openInMemory(context).use { store ->
            store.v2RecordGovernedAttempt(prepareGovernedAttempt(old, item.id, "1", PRESCRIBED_CHECK, step.id, "2026-10-12T20:00")!!)
            assertTrue(item.id in creditItemIds(old, store.v2LearnerView()))
            assertFalse(item.id in creditItemIds(p, store.v2LearnerView()))
            assertEquals(1, store.v2Attempts().size)
        }
    }
    private fun pack() = V2PackLoader.parse(File(if (File("app").isDirectory) "." else "..", "app/src/main/assets/content/v2-curriculum.json").readText())
}
