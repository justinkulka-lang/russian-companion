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
class Unit30LanguageCorrectionTest {
    @get:Rule val compose = createComposeRule()
    @Test fun languageCompletionMustNotDependOnEnglishTableOrHeadingAnswers() {
        val p = pack()
        LocalStore.openInMemory(context()).use { store ->
            for (oid in listOf("obj-u30-letter-names", "obj-u30-abbrev-examples")) {
                val o = p.unit(30)!!.objectives.single { it.id == oid }
                val ids = o.practiceStepIds.flatMap { p.step(it)!!.second.itemIds } + o.checkItemIds
                for (id in ids) {
                    val step = p.unit(30)!!.sessions.flatMap { it.steps }.first { id in it.itemIds }
                    store.v2RecordGovernedAttempt(prepareGovernedAttempt(p,id,p.item(id)!!.answer,PRESCRIBED_CHECK,step.id,"2026-10-13T10:00")!!)
                }
                assertEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS, objectiveState(o,store.v2LearnerView(),p))
            }
            val names = p.unit(30)!!.objectives.single { it.id == "obj-u30-letter-names" }
            assertTrue("Independent names must be Russian names of shown letters, without English table equivalents",
                names.checkItemIds.all { p.item(it)!!.answer in setOf("бэ","вэ") && p.item(it)!!.promptLetter in setOf("Б","В") })
            val abbreviation = p.unit(30)!!.objectives.single { it.id == "obj-u30-abbrev-examples" }
            assertTrue(abbreviation.checkItemIds.all { p.item(it)!!.assessment!!.testedFeature == "ABBREVIATION_TREATMENT" })
        }
    }
    @Test fun optionalReferenceAnswersCannotCreditTheLanguageObjectives() {
        val p = pack()
        LocalStore.openInMemory(context()).use { store ->
            store.v2SaveProfile(store.v2Profile().copy(currentUnitNumber = 30, startingPoint = "RESUME_FROM_BOOK"))
            val queue = practiceQueue(p, store.v2LearnerView(), "vocabulary")
            val refs = p.items.filter { it.objectiveId == "obj-u30-reference-literacy" }
            assertEquals(6, refs.size)
            for (item in refs) {
                assertTrue(queue.any { it.id == item.id && it.kind == "SOURCE_QUESTION" })
                assertEquals(4, labExplanationSteps(p, item).size)
                store.v2RecordGovernedAttempt(prepareGovernedAttempt(p,item.id,item.answer,OPTIONAL_PRACTICE,practiceStepId("vocabulary"),"2026-10-13T10:01")!!)
                assertNotNull(optionalPracticeSelection(p,store.v2LearnerView(),"vocabulary",item.id))
            }
            assertTrue(creditItemIds(p,store.v2LearnerView()).isEmpty())
            assertEquals(6,store.v2Attempts().size)
        }
        val result = V2Validator.validate(p)
        assertTrue(result.errors.joinToString("\n"),result.ok)
    }
    @Test fun playerRejectsAnotherLettersRussianName() = wrong("u30-i-native-name-b", "вэ")
    @Test fun playerRejectsSingularAgreementAndCaseEndingsForUSA() = wrong("u30-i-initialism-usa",
        "USA; singular agreement; the initialism takes case endings.")
    private fun wrong(id: String, answer: String) {
        val p = pack(); val item = p.item(id)!!
        val step = p.unit(30)!!.sessions.flatMap { it.steps }.first { id in it.itemIds }
        val owner = p.step(step.id)!!
        LocalStore.openInMemory(context()).use { store ->
            store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true,currentUnitNumber = 30,startingPoint = "RESUME_FROM_BOOK",
                currentSessionId = owner.first.id,currentStepIndex = owner.first.steps.indexOf(step)).verifiedResume(p))
            compose.setContent { V2App(store = store,pack = p,onRetryOpen = {}) }
            compose.onNodeWithTag("v2-continue").performClick()
            compose.onNodeWithTag("v2-option-$id-$answer").performScrollTo().performClick()
            compose.onNodeWithTag("v2-item-feedback-$id").performScrollTo().assertTextEquals("Not the reviewed answer. ${item.feedback}")
            assertFalse(id in creditItemIds(p,store.v2LearnerView()))
        }
    }
    @Test fun restoringValidOriginalContrastItemsCannotClearTheLanguageChain() {
        val p = pack(); val old = baseline(); val oldUnit = old.unit(30)!!
        val ids = setOf("obj-u30-letter-names","obj-u30-abbrev-examples")
        val legacy = old.items.filter { it.objectiveId in ids }.associateBy { it.id }
        assertEquals(6,legacy.size)
        assertTrue(legacy.values.all { it.kind == "CONTRAST_SELECTION" && it.assessment != null })
        val forged = p.unit(30)!!.copy(objectives = p.unit(30)!!.objectives.map { o ->
            if (o.id in ids) oldUnit.objectives.single { it.id == o.id } else o })
        val result = V2Validator.validate(p.copy(units = p.units.map { if (it.number == 30) forged else it },
            items = p.items.map { legacy[it.id] ?: it }))
        assertFalse(result.ok)
        assertTrue(result.errors.joinToString("\n"),result.errors.any { it.contains("cap-obj-u30-letter-names has no reviewable instruction and check chain") })
        assertTrue(result.errors.any { it.contains("obj-u30-abbrev-examples has no frozen native language application chain") })
    }
    @Test fun coordinatedKeyAndApplicationMutationCannotRenameB() {
        val p = pack(); val item = p.item("u30-i-native-name-b")!!; val a = item.assessment!!
        val ids = a.relationParticipantIds; val wrong = item.options[1]
        val rationales = listOf(item.distractorRationales[1],"",item.distractorRationales[2])
        val changed = item.copy(answer = wrong,distractorRationales = rationales,assessment = a.copy(
            relationParticipantIds = listOf(ids[0],ids[2],ids[1],ids[3]),answerSourceIds = listOf(ids[2]),
            distractorSourceIds = listOf(ids[1],"",ids[3]),distractorDifference = rationales))
        val records = p.provenanceRecords.map { r -> if (r.sourceRecordId != "ca-rule-context:${item.id}") r else r.copy(
            synthesis = r.synthesis!!.copy(applications = r.synthesis.applications.map { it.copy(answer = wrong,distractors = listOf(item.answer,item.options[2])) })) }
        assertTrue(records.zip(p.provenanceRecords).all { (x,y) -> x.originalSourceText == y.originalSourceText && x.sourceHash == y.sourceHash && x.shippedText == y.shippedText && x.contentHash == y.contentHash })
        val result = V2Validator.validate(p.copy(items = p.items.map { if (it.id == item.id) changed else it },provenanceRecords = records))
        assertFalse(result.ok)
        assertTrue(result.errors.joinToString("\n"),result.errors.any { it.contains("complete rule context") })
    }
    @Test fun coordinatedPrimaryNameTextAndHashForgeryIsRejected() {
        val p = pack()
        val forged = p.provenanceRecords.map { r -> if (r.itemId != "prov-u30-language-native-b-name") r else {
            val text = r.originalSourceText.replace("бэ","вэ")
            r.copy(originalSourceText = text,sourceHash = sha(text),shippedText = "вэ",contentHash = sha("вэ"))
        } }
        val result = V2Validator.validate(p.copy(provenanceRecords = forged))
        assertFalse(result.ok)
        assertTrue(result.errors.joinToString("\n"),result.errors.any { it.contains("no frozen native language application chain") })
    }
    @Test fun authenticReferenceHistorySurvivesButCannotCreditNewLanguageContent() {
        val p = pack(); val old = baseline()
        LocalStore.openInMemory(context()).use { store ->
            for (oid in listOf("obj-u30-letter-names","obj-u30-abbrev-examples")) {
                val o = old.unit(30)!!.objectives.single { it.id == oid }
                val ids = o.practiceStepIds.flatMap { old.step(it)!!.second.itemIds } + o.checkItemIds
                for (id in ids) {
                    val step = old.unit(30)!!.sessions.flatMap { it.steps }.first { id in it.itemIds }
                    store.v2RecordGovernedAttempt(prepareGovernedAttempt(old,id,old.item(id)!!.answer,PRESCRIBED_CHECK,step.id,"2026-10-13T09:59")!!)
                }
                assertEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS,objectiveState(o,store.v2LearnerView(),old))
                assertNotEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS,objectiveState(p.unit(30)!!.objectives.single { it.id == oid },store.v2LearnerView(),p))
            }
            assertTrue(creditItemIds(p,store.v2LearnerView()).isEmpty())
            assertEquals(6,store.v2Attempts().size)
        }
    }
    private fun baseline(): V2CurriculumPack {
        val process = ProcessBuilder("git","show","52274f15b17b7f1f46bf4684006437e9661dab31:app/src/main/assets/content/v2-curriculum.json").directory(root()).start()
        val p = V2PackLoader.parse(process.inputStream.bufferedReader().readText()); assertEquals(0,process.waitFor()); return p
    }
    private fun sha(s: String) = java.security.MessageDigest.getInstance("SHA-256").digest(s.toByteArray(Charsets.UTF_8)).joinToString("") { "%02x".format(it.toInt() and 255) }
    private fun context(): Context = ApplicationProvider.getApplicationContext()
    private fun root() = File(if (File("app").isDirectory) "." else "..")
    private fun pack() = V2PackLoader.parse(File(root(), "app/src/main/assets/content/v2-curriculum.json").readText())
}
