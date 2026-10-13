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
class Unit9QuantityRuleCorrectionTest {
    @get:Rule val compose = createComposeRule()
    @Test fun completeRulesTeachTeenExceptionsAndScopeBeforePractice() {
        val p = pack(); val items = p.items.filter { it.objectiveId in objectives }
        assertEquals(15, items.size)
        items.forEach {
            assertTrue(it.id, it.options.all { option -> option.startsWith("In this") && option.endsWith(".") })
            assertEquals("STATED_APPLICATION", it.assessment!!.relationKind)
            assertFalse(it.feedback.startsWith("Does"))
        }
        val sessions = p.unit(9)!!.sessions.filter { it.id in setOf("u9-s-quantity", "u9-s-pattern") }
        sessions.forEach { session ->
            val teaching = session.steps.filter { it.kind == "TEACH" }
            assertTrue(session.steps.indexOf(teaching.last()) < session.steps.indexOfFirst { it.kind == "GUIDED" })
            val remediation = session.steps.single { it.kind == "REMEDIATE" }
            teaching.forEach { assertTrue(remediation.body.contains(it.body)) }
            assertEquals(teaching.flatMap { it.exampleIds }.toSet(), remediation.exampleIds.toSet())
            assertTrue(teaching.any { it.body.contains("11, 12, 13 or 14") })
            assertTrue(teaching.any { it.body.contains("inanimate accusative") })
        }
        assertTrue(p.item("u9-i-quantity-6")!!.feedback.contains("213") && p.item("u9-i-quantity-6")!!.feedback.contains("last two digits"))
        assertTrue(p.item("u9-i-quantity-9")!!.feedback.contains("203"))
        assertTrue(p.item("u9-i-pattern-4")!!.feedback.contains("animate accusative"))
        val result = V2Validator.validate(p); assertTrue(result.errors.joinToString("\n"), result.ok)
    }
    @Test fun wrongTeenAnswerShowsTheCompleteExplanation() {
        val p = pack(); val item = p.item("u9-i-quantity-6")!!
        val step = p.unit(9)!!.sessions.flatMap { it.steps }.first { item.id in it.itemIds };val owner = p.step(step.id)!!
        val context: Context = ApplicationProvider.getApplicationContext()
        LocalStore.openInMemory(context).use { store ->
            store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = 9, startingPoint = "RESUME_FROM_BOOK",
                currentSessionId = owner.first.id, currentStepIndex = owner.first.steps.indexOf(step)).verifiedResume(p))
            compose.setContent { V2App(store = store, pack = p, onRetryOpen = {}) };compose.onNodeWithTag("v2-continue").performClick()
            compose.onNodeWithTag("v2-option-${item.id}-${item.options.first { it != item.answer }}").performScrollTo().performClick()
            compose.onNodeWithTag("v2-item-feedback-${item.id}").performScrollTo().assertTextEquals("Not the reviewed answer. ${item.feedback}")
            assertFalse(item.id in creditItemIds(p, store.v2LearnerView()))
        }
    }
    @Test fun coordinatedKeyAndApplicationEditsCannotReverseTheTeenContext() {
        val p = pack(); val item = p.item("u9-i-quantity-6")!!; val a = item.assessment!!
        val wrong = item.options.single { it != item.answer };val ids = a.relationParticipantIds
        val rationale = p.item("u9-i-quantity-8")!!.distractorRationales.first { it.isNotBlank() }
        val rationales = item.options.map { if (it == wrong) "" else rationale }
        val changed = item.copy(answer = wrong, distractorRationales = rationales, assessment = a.copy(
            distractorDifference = rationales, relationParticipantIds = listOf(ids[0], ids[2], ids[1]), answerSourceIds = listOf(ids[2]),
            distractorSourceIds = item.options.map { if (it == wrong) "" else ids[1] }))
        val records = p.provenanceRecords.map { record ->
            if (record.sourceRecordId != "ca-rule-context:${item.id}") record else record.copy(synthesis = record.synthesis!!.copy(
                applications = record.synthesis.applications.map { it.copy(answer = wrong, distractors = listOf(item.answer)) }))
        }
        assertTrue(records.zip(p.provenanceRecords).all { (x,y) -> x.originalSourceText == y.originalSourceText && x.sourceHash == y.sourceHash && x.shippedText == y.shippedText && x.contentHash == y.contentHash })
        val result = V2Validator.validate(p.copy(items = p.items.map { if (it.id == item.id) changed else it }, provenanceRecords = records))
        assertFalse("Reversed teen context must not pass full validator: ${result.errors}", result.ok)
        assertTrue(result.errors.joinToString("\n"), result.errors.any { it.contains("complete rule context") })
    }
    @Test fun threeChoiceContextCannotSwapItsAnswerAndApplicationTogether() {
        val p = pack(); val item = p.item("u9-i-quantity-3")!!; val a = item.assessment!!
        assertEquals(3, item.options.size)
        val wrong = item.options.first { it != item.answer }; val ids = a.relationParticipantIds
        val byText = ids.drop(1).associateBy { id -> p.provenanceRecords.single { it.itemId == id }.shippedText }
        val distractors = item.options.filter { it != wrong }
        val changed = item.copy(answer = wrong, assessment = a.copy(answerSourceIds = listOf(byText.getValue(wrong)),
            relationParticipantIds = listOf(ids[0], byText.getValue(wrong)) + distractors.map { byText.getValue(it) },
            distractorSourceIds = item.options.map { if (it == wrong) "" else byText.getValue(it) }))
        val records = p.provenanceRecords.map { r -> if (r.sourceRecordId != "ca-rule-context:${item.id}") r else r.copy(synthesis = r.synthesis!!.copy(
            applications = r.synthesis.applications.map { it.copy(answer = wrong, distractors = distractors) })) }
        val result = V2Validator.validate(p.copy(items = p.items.map { if (it.id == item.id) changed else it }, provenanceRecords = records))
        assertFalse(result.ok)
        assertTrue(result.errors.joinToString("\n"), result.errors.any { it.contains("complete rule context") })
    }
    @Test fun historicalFragmentAttemptIsRetainedButCannotCreditTheCompleteRule() {
        val p = pack(); val item = p.item("u9-i-quantity-6")!!
        val root = File(if (File("app").isDirectory) "." else "..")
        val ledger = Json.parseToJsonElement(File(root, "audit/content-corrections/UNIT9_QUANTITY_RULE_CORRECTION.json").readText()).jsonObject
        val before = ledger.getValue("changes").jsonArray.map { it.jsonObject.getValue("before").jsonObject }.single { it["id"]?.jsonPrimitive?.content == item.id }
        val oldItem = historicalJson.decodeFromJsonElement<V2CheckItem>(before)
        val old = p.copy(items = p.items.map { if (it.id == item.id) oldItem else it })
        assertTrue(V2Validator.validate(old).errors.any { it.contains("complete rule context") })
        val step = p.unit(9)!!.sessions.flatMap { it.steps }.first { item.id in it.itemIds }
        val context: Context = ApplicationProvider.getApplicationContext()
        LocalStore.openInMemory(context).use { store ->
            store.v2RecordGovernedAttempt(prepareGovernedAttempt(old, item.id, oldItem.answer, PRESCRIBED_CHECK, step.id, "2026-10-12T21:00")!!)
            assertTrue(item.id in creditItemIds(old, store.v2LearnerView()))
            assertFalse(item.id in creditItemIds(p, store.v2LearnerView()))
            assertEquals(1, store.v2Attempts().size)
        }
    }
    private val historicalJson = Json { ignoreUnknownKeys = true }
    private val objectives = setOf("obj-u9-quantity", "obj-u9-pattern")
    private fun pack() = V2PackLoader.parse(File(if (File("app").isDirectory) "." else "..", "app/src/main/assets/content/v2-curriculum.json").readText())
}
