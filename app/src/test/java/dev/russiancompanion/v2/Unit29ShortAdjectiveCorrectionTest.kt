package dev.russiancompanion.v2

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.curriculum.*
import dev.russiancompanion.v2.ui.V2App
import dev.russiancompanion.v2.ui.labExplanationSteps
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
class Unit29ShortAdjectiveCorrectionTest {
    @get:Rule val compose = createComposeRule()
    @Test fun grammarGateMustNotBeSatisfiedByVocabularyGlosses() {
        val p = pack(); val o = p.unit(29)!!.objectives.single { it.id == "obj-u29-short-adjective" }
        LocalStore.openInMemory(context()).use { store ->
            for (id in o.checkItemIds) {
                val item = p.item(id)!!
                val step = p.unit(29)!!.sessions.flatMap { it.steps }.first { id in it.itemIds }
                store.v2RecordGovernedAttempt(prepareGovernedAttempt(p, id, item.answer, PRESCRIBED_CHECK, step.id, "2026-10-13T01:00")!!)
            }
            assertTrue(o.checkItemIds.all { it in creditItemIds(p, store.v2LearnerView()) })
            assertTrue("The credited grammar checks must distinguish form/function rather than English glosses",
                o.checkItemIds.all { p.item(it)!!.answer !in setOf("new", "beige", "khaki-colored") })
        }
        val validation = V2Validator.validate(p)
        assertTrue(validation.errors.joinToString("\n"), validation.ok)
    }
    @Test fun vocabularyLabPreservesMeaningsWithoutSatisfyingGrammarChecks() {
        val p = pack(); val o = p.unit(29)!!.objectives.single { it.id == "obj-u29-short-adjective" }
        LocalStore.openInMemory(context()).use { store ->
            store.v2SaveProfile(store.v2Profile().copy(currentUnitNumber = 29, startingPoint = "RESUME_FROM_BOOK"))
            val vocabulary = practiceQueue(p, store.v2LearnerView(), "vocabulary")
            for ((id, answer) in listOf("u29-g-short-adjective-0" to "new", "u29-i-short-adjective-1" to "new", "u29-i-short-adjective-2" to "beige")) {
                assertTrue(vocabulary.any { it.id == id && it.answer == answer && it.kind == "SOURCE_MEANING" })
                assertEquals(3, labExplanationSteps(p, p.item(id)!!).size)
                store.v2RecordGovernedAttempt(prepareGovernedAttempt(p, id, answer, OPTIONAL_PRACTICE, practiceStepId("vocabulary"), "2026-10-13T01:01")!!)
                assertNotNull(optionalPracticeSelection(p, store.v2LearnerView(), "vocabulary", id))
            }
            assertTrue(o.checkItemIds.none { it in creditItemIds(p, store.v2LearnerView()) })
            assertEquals(3, store.v2Attempts().size)
        }
    }
    @Test fun grammarStateCanBeDemonstratedWithoutOptionalGlossAttempts() {
        val p = pack(); val o = p.unit(29)!!.objectives.single { it.id == "obj-u29-short-adjective" }
        val ids = o.practiceStepIds.flatMap { p.step(it)!!.second.itemIds } + o.checkItemIds
        LocalStore.openInMemory(context()).use { store ->
            for (id in ids) {
                val step = p.unit(29)!!.sessions.flatMap { it.steps }.first { id in it.itemIds }
                store.v2RecordGovernedAttempt(prepareGovernedAttempt(p, id, p.item(id)!!.answer, PRESCRIBED_CHECK, step.id, "2026-10-13T01:02")!!)
            }
            assertEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS, objectiveState(o, store.v2LearnerView(), p))
            assertTrue(store.v2Attempts().none { it.itemId.contains("short-adjective") })
        }
    }
    @Test fun playerRejectsLongClassificationForTheShortForm() {
        val p = pack(); val item = p.item("u29-i-adjective-form-1")!!
        val step = p.unit(29)!!.sessions.flatMap { it.steps }.first { item.id in it.itemIds }
        val owner = p.step(step.id)!!
        LocalStore.openInMemory(context()).use { store ->
            store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = 29, startingPoint = "RESUME_FROM_BOOK",
                currentSessionId = owner.first.id, currentStepIndex = owner.first.steps.indexOf(step)).verifiedResume(p))
            compose.setContent { V2App(store = store, pack = p, onRetryOpen = {}) }
            compose.onNodeWithTag("v2-option-${item.id}-${item.options.single { it.startsWith("Long adjective") }}").assertDoesNotExist()
            compose.onNodeWithTag("v2-continue").performClick()
            compose.onNodeWithTag("v2-option-${item.id}-${item.options.single { it.startsWith("Long adjective") }}").performScrollTo().performClick()
            compose.onNodeWithTag("v2-item-feedback-${item.id}").performScrollTo().assertTextEquals("Not the reviewed answer. ${item.feedback}")
            assertFalse(item.id in creditItemIds(p, store.v2LearnerView()))
        }
    }
    @Test fun historicalGlossCreditIsRetainedButCannotCreditTheCurrentGrammarGate() {
        val p = pack()
        val process = ProcessBuilder("git", "show", "aa70217d4b956d7954c5671900e11cf2672573b3:app/src/main/assets/content/v2-curriculum.json").directory(root()).start()
        val old = V2PackLoader.parse(process.inputStream.bufferedReader().readText()); assertEquals(0, process.waitFor())
        val step = old.unit(29)!!.sessions.flatMap { it.steps }.single { it.id == "u29-st-check-short-adjective" }
        LocalStore.openInMemory(context()).use { store ->
            for (id in step.itemIds) store.v2RecordGovernedAttempt(prepareGovernedAttempt(old, id, old.item(id)!!.answer, PRESCRIBED_CHECK, step.id, "2026-10-12T23:00")!!)
            assertTrue(step.itemIds.all { it in creditItemIds(old, store.v2LearnerView()) })
            assertTrue(p.unit(29)!!.objectives.single { it.id == "obj-u29-short-adjective" }.checkItemIds.none { it in creditItemIds(p, store.v2LearnerView()) })
            assertTrue(step.itemIds.none { it in creditItemIds(p, store.v2LearnerView()) })
            assertEquals(2, store.v2Attempts().size)
        }
    }
    @Test fun restoringGlossChecksCannotClearTheShortAdjectiveCapability() {
        val p = pack(); val unit = p.unit(29)!!
        val forged = unit.copy(objectives = unit.objectives.map { if (it.id != "obj-u29-short-adjective") it else it.copy(
            checkItemIds = listOf("u29-i-short-adjective-1", "u29-i-short-adjective-2")) })
        val ledger = Json.parseToJsonElement(File(root(), "audit/content-corrections/UNIT29_SHORT_ADJECTIVE_CORRECTION.json").readText()).jsonObject
        val legacy = ledger.getValue("changes").jsonArray.map { it.jsonObject.getValue("before").jsonObject }
            .filter { it["id"]?.jsonPrimitive?.content in setOf("u29-i-short-adjective-1", "u29-i-short-adjective-2") }
            .map { Json { ignoreUnknownKeys = true }.decodeFromJsonElement<V2CheckItem>(it) }.associateBy { it.id }
        assertEquals(2, legacy.size)
        assertTrue(legacy.values.all { it.kind == "CONTRAST_SELECTION" && it.assessment?.independent == true })
        val result = V2Validator.validate(p.copy(units = p.units.map { if (it.number == 29) forged else it },
            items = p.items.map { legacy[it.id] ?: it }))
        assertFalse(result.ok)
        assertTrue(result.errors.joinToString("\n"), result.errors.any { it.contains("cap-obj-u29-short-adjective has no reviewable instruction and check chain") })
    }
    @Test fun coordinatedKeyAndApplicationEditsCannotMakeTheShortFormLong() {
        val p = pack(); val item = p.item("u29-i-adjective-form-1")!!; val a = item.assessment!!
        val wrong = item.options.single { it.startsWith("Long adjective") }; val ids = a.relationParticipantIds
        val rationales = listOf(item.distractorRationales[1], "", item.distractorRationales[2])
        val changed = item.copy(answer = wrong, distractorRationales = rationales, assessment = a.copy(
            relationParticipantIds = listOf(ids[0], ids[2], ids[1], ids[3]), answerSourceIds = listOf(ids[2]),
            distractorSourceIds = listOf(ids[1], "", ids[3]), distractorDifference = rationales))
        val records = p.provenanceRecords.map { r -> if (r.sourceRecordId != "ca-rule-context:${item.id}") r else r.copy(
            synthesis = r.synthesis!!.copy(applications = r.synthesis.applications.map { it.copy(answer = wrong, distractors = listOf(item.answer, item.options[2])) })) }
        val result = V2Validator.validate(p.copy(items = p.items.map { if (it.id == item.id) changed else it }, provenanceRecords = records))
        assertFalse(result.ok)
        assertTrue(result.errors.joinToString("\n"), result.errors.any { it.contains("complete rule context") })
    }
    private fun context(): Context = ApplicationProvider.getApplicationContext()
    private fun root() = File(if (File("app").isDirectory) "." else "..")
    private fun pack() = V2PackLoader.parse(File(root(), "app/src/main/assets/content/v2-curriculum.json").readText())
}
