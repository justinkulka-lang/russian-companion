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
    @Test fun completeChoicesExplainTheCasesAndAllTeachingPrecedesPractice() {
        val p = sourcePack("1ab25bc6813963cbb3f41da92f2c4ffe8383a1ae")
        val items = p.items.filter { it.objectiveId in objectives }
        assertEquals(14, items.size)
        items.forEach { item ->
            assertTrue(item.options.all { it.startsWith("In this line,") && it.endsWith(".") })
            assertEquals("STATED_APPLICATION", item.assessment!!.relationKind)
            assertTrue(item.feedback.contains("nominative") || item.feedback.contains("genitive"))
            assertFalse(item.feedback.startsWith("Does the admitted"))
        }
        for (n in listOf(8, 10)) p.unit(n)!!.sessions.filter { it.steps.any { st -> st.itemIds.any { id -> id in items.map { it.id } } } }.forEach { session ->
            val teaching = session.steps.filter { it.kind == "TEACH" }
            assertTrue(session.steps.indexOf(teaching.last()) < session.steps.indexOfFirst { it.kind == "GUIDED" })
            val remediation = session.steps.single { it.kind == "REMEDIATE" }
            teaching.forEach { assertTrue(remediation.body.contains(it.body)) }
            assertEquals(teaching.flatMap { it.exampleIds }.toSet(), remediation.exampleIds.toSet())
        }
        // The historical Unit8 absence contract is retained above, not readmitted.
        // Current source must pass the full current validator.
        val result = V2Validator.validate(pack())
        assertTrue(result.errors.joinToString("\n"), result.ok)
    }
    @Test fun currentRulesRetainUnit10CasesAndExactPositiveExistenceTeaching() {
        val p=pack()
        val root=File(if(File("app").isDirectory) "." else "..")
        // Prove the actual current asset before restricting only this historical equality.
        RecordedCurriculumConservationScope.beforeUnit5GrammarReview(root,p).let { p ->
            assertEquals(sourcePack("b7e53230e2119b1e57d781b1e1b955f074a38601"),p)
        }
        assertNotEquals("The two versioned review declarations give the actual pack its own identity",
            sourcePack("b7e53230e2119b1e57d781b1e1b955f074a38601"),p)
        val old=sourcePack("1ab25bc6813963cbb3f41da92f2c4ffe8383a1ae")
        val existence=sourcePack("fe919656bfa2775b5cc2c22f2496d4258343cfea")
        val changed=setOf("u8-g-availability-0","u8-i-availability-1","u8-i-availability-2","u8-i-availability-3")
        val items=p.items.filter { it.objectiveId in objectives }
        assertEquals(14,items.size)
        assertEquals(old.items.filter { it.objectiveId in objectives && it.id !in changed },items.filter { it.id !in changed })
        assertEquals(existence.items.filter { it.id in changed },items.filter { it.id in changed })
        assertEquals(existence.unit(8),p.unit(8))
        assertEquals(existence.provenanceRecords.filter { it.unitNumber==8 },p.provenanceRecords.filter { it.unitNumber==8 })
        items.filter { it.id !in changed }.forEach { item ->
            assertTrue(item.options.all { it.startsWith("In this line,") && it.endsWith(".") })
            assertEquals("STATED_APPLICATION",item.assessment!!.relationKind)
            assertTrue(item.feedback.contains("nominative") || item.feedback.contains("genitive"))
            assertFalse(item.feedback.startsWith("Does the admitted"))
        }
        for(number in listOf(8,10)) p.unit(number)!!.sessions.filter { session->session.steps.any { step->step.itemIds.any { id->items.any { it.id==id } } } }.forEach { session->
            val teaching=session.steps.filter { it.kind=="TEACH" }
            assertTrue(session.steps.indexOf(teaching.last())<session.steps.indexOfFirst { it.kind=="GUIDED" })
            val remediation=session.steps.single { it.kind=="REMEDIATE" }
            teaching.forEach { assertTrue(remediation.body.contains(it.body)) }
            assertEquals(teaching.flatMap { it.exampleIds }.toSet(),remediation.exampleIds.toSet())
        }
        val validation=V2Validator.validate(p)
        assertTrue(validation.errors.joinToString("\n"),validation.ok)
    }
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
    @Test fun historicalFragmentAttemptIsRetainedButCannotCreditTheCompleteRule() {
        val p = pack(); val item = p.item("u8-i-availability-1")!!
        val root = File(if (File("app").isDirectory) "." else "..")
        val ledger = Json.parseToJsonElement(File(root, "audit/content-corrections/UNITS8_10_COMPLETE_RULE_CORRECTION.json").readText()).jsonObject
        val before = ledger.getValue("changes").jsonArray.map { it.jsonObject.getValue("before").jsonObject }.single { it["id"]?.jsonPrimitive?.content == item.id }
        val oldItem = historicalJson.decodeFromJsonElement<V2CheckItem>(before)
        val old = p.copy(items = p.items.map { if (it.id == item.id) oldItem else it })
        assertTrue(V2Validator.validate(old).errors.any { it.contains("complete rule context") })
        val step = p.unit(8)!!.sessions.flatMap { it.steps }.first { item.id in it.itemIds }
        val context: Context = ApplicationProvider.getApplicationContext()
        LocalStore.openInMemory(context).use { store ->
            store.v2RecordGovernedAttempt(prepareGovernedAttempt(old, item.id, oldItem.answer, PRESCRIBED_CHECK, step.id, "2026-10-12T21:00")!!)
            assertTrue(item.id in creditItemIds(old, store.v2LearnerView()))
            assertFalse(item.id in creditItemIds(p, store.v2LearnerView()))
            assertEquals(1, store.v2Attempts().size)
        }
    }
    private val sourcePacks=mutableMapOf<String,V2CurriculumPack>()
    private fun sourcePack(commit:String)=sourcePacks.getOrPut(commit) {
        val root=File(if(File("app").isDirectory) "." else "..")
        val command=ProcessBuilder("git","show","$commit:app/src/main/assets/content/v2-curriculum.json").directory(root).start()
        V2PackLoader.parse(command.inputStream.bufferedReader().readText()).also {
            assertEquals(0,command.waitFor());V2PackIdentityIndex.warm(it)
        }
    }
    private val historicalJson = Json { ignoreUnknownKeys = true }
    private val objectives = setOf("obj-u8-availability", "obj-u10-have", "obj-u10-absence")
    private fun pack() = V2PackLoader.parse(File(if (File("app").isDirectory) "." else "..", "app/src/main/assets/content/v2-curriculum.json").readText())
}
