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
class Unit29AdjectiveApplicationTest {
    @get:Rule val compose = createComposeRule()
    @Test fun classificationCreditMustAlsoRequireAgreementAndSentenceFunctionChecks() {
        val p = pack(); val o = p.unit(29)!!.objectives.single { it.id == "obj-u29-short-adjective" }
        val ids = o.practiceStepIds.flatMap { p.step(it)!!.second.itemIds } + o.checkItemIds
        LocalStore.openInMemory(context()).use { store ->
            for (id in ids) {
                val step = p.unit(29)!!.sessions.flatMap { it.steps }.first { id in it.itemIds }
                store.v2RecordGovernedAttempt(prepareGovernedAttempt(p, id, p.item(id)!!.answer, PRESCRIBED_CHECK, step.id, "2026-10-13T09:00")!!)
            }
            assertEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS, objectiveState(o, store.v2LearnerView(), p))
            val features = o.checkItemIds.map { p.item(it)!!.assessment!!.testedFeature }.toSet()
            assertTrue("A completed adjective objective must have independent agreement and sentence-function checks, not classification alone",
                features.containsAll(setOf("SHORT_AGREEMENT", "ADJECTIVE_FUNCTION")))
        }
        val validation = V2Validator.validate(p)
        assertTrue(validation.errors.joinToString("\n"), validation.ok)
    }
    @Test fun originalClassificationItemsAloneCannotCompleteTheExpandedObjective() {
        val p = pack(); val o = p.unit(29)!!.objectives.single { it.id == "obj-u29-short-adjective" }
        LocalStore.openInMemory(context()).use { store ->
            for (id in listOf("u29-g-adjective-form-0", "u29-i-adjective-form-1", "u29-i-adjective-form-2")) {
                val step = p.unit(29)!!.sessions.flatMap { it.steps }.first { id in it.itemIds }
                store.v2RecordGovernedAttempt(prepareGovernedAttempt(p, id, p.item(id)!!.answer, PRESCRIBED_CHECK, step.id, "2026-10-13T09:01")!!)
            }
            assertEquals(3, creditItemIds(p, store.v2LearnerView()).size)
            assertEquals(V2ObjectiveState.REVIEW_REQUIRED, objectiveState(o, store.v2LearnerView(), p))
            assertTrue(currentUnitRequirements(p, p.unit(29)!!, store.v2LearnerView()).remaining.contains("Demonstrate every check for ${o.title}"))
        }
    }
    @Test fun playerRejectsNeuterShortFormForTheFeminineHeadNoun() = wrong(
        "u29-i-short-agreement-f", "но́во")
    @Test fun playerRejectsAttributiveAnswerForTheCompleteLongFormPredicate() = wrong(
        "u29-i-adjective-function-p", "An attributive adjective modifying the noun.")
    private fun wrong(id: String, answer: String) {
        val p = pack(); val item = p.item(id)!!
        val step = p.unit(29)!!.sessions.flatMap { it.steps }.first { id in it.itemIds }
        val owner = p.step(step.id)!!
        LocalStore.openInMemory(context()).use { store ->
            store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = 29, startingPoint = "RESUME_FROM_BOOK",
                currentSessionId = owner.first.id, currentStepIndex = owner.first.steps.indexOf(step)).verifiedResume(p))
            compose.setContent { V2App(store = store, pack = p, onRetryOpen = {}) }
            compose.onNodeWithTag("v2-continue").performClick()
            compose.onNodeWithTag("v2-option-$id-$answer").performScrollTo().performClick()
            compose.onNodeWithTag("v2-item-feedback-$id").performScrollTo().assertTextEquals("Not the reviewed answer. ${item.feedback}")
            assertFalse(id in creditItemIds(p, store.v2LearnerView()))
        }
    }
    @Test fun coordinatedApplicationAndKeyEditsCannotMakeTheFeminineSubjectNeuter() {
        val p = pack(); val item = p.item("u29-i-short-agreement-f")!!; val a = item.assessment!!
        val ids = a.relationParticipantIds; val wrong = "но́во"; val donor = p.item("u29-i-short-agreement-n")!!
        val rationales = listOf(donor.distractorRationales[1], "", item.distractorRationales[2])
        val changed = item.copy(answer = wrong, feedback = donor.feedback, distractorRationales = rationales, assessment = a.copy(
            relationParticipantIds = listOf(ids[0], ids[2], ids[1], ids[3]), answerSourceIds = listOf(ids[2]),
            distractorSourceIds = listOf(ids[1], "", ids[3]), distractorDifference = rationales, whyAnswerDemonstratesObjective = donor.feedback))
        val attacked = p.provenanceRecords.map { r -> if (r.sourceRecordId != "ca-rule-context:${item.id}") r else r.copy(
            synthesis = r.synthesis!!.copy(applications = r.synthesis.applications.map { it.copy(answer = wrong, distractors = listOf(item.answer, item.options[2])) })) }
        assertTrue(attacked.zip(p.provenanceRecords).all { (x,y) -> x.originalSourceText == y.originalSourceText && x.sourceHash == y.sourceHash && x.shippedText == y.shippedText && x.contentHash == y.contentHash })
        val result = V2Validator.validate(p.copy(items = p.items.map { if (it.id == item.id) changed else it }, provenanceRecords = attacked))
        assertFalse(result.ok)
        assertTrue(result.errors.joinToString("\n"), result.errors.any { it.contains("complete rule context") })
    }
    @Test fun coordinatedNativeTextAndHashForgeryCannotReplaceThePredicateExample() {
        val p = pack(); val id = "prov-u29-adjective-native-student-ru"
        val forged = p.provenanceRecords.map { r -> if (r.itemId != id) r else {
            val text = r.originalSourceText.replace("Студент новый", "Студент старый")
            r.copy(originalSourceText = text, sourceHash = sha(text), shippedText = "Студент старый", contentHash = sha("Студент старый"))
        } }
        val result = V2Validator.validate(p.copy(provenanceRecords = forged))
        assertFalse(result.ok)
        assertTrue(result.errors.joinToString("\n"), result.errors.any { it.contains("cap-obj-u29-short-adjective has no reviewable instruction and check chain") })
    }
    @Test fun authenticHistoricalClassificationAttemptsSurviveButCannotCreditNewContent() {
        val p = pack(); val process = ProcessBuilder("git", "show", "98b5b30ffc224b1119bc975fc698386ce8b91d3f:app/src/main/assets/content/v2-curriculum.json").directory(root()).start()
        val old = V2PackLoader.parse(process.inputStream.bufferedReader().readText()); assertEquals(0, process.waitFor())
        val o = old.unit(29)!!.objectives.single { it.id == "obj-u29-short-adjective" }
        LocalStore.openInMemory(context()).use { store ->
            val ids = o.practiceStepIds.flatMap { old.step(it)!!.second.itemIds } + o.checkItemIds
            for (id in ids) {
                val step = old.unit(29)!!.sessions.flatMap { it.steps }.first { id in it.itemIds }
                store.v2RecordGovernedAttempt(prepareGovernedAttempt(old, id, old.item(id)!!.answer, PRESCRIBED_CHECK, step.id, "2026-10-13T08:00")!!)
            }
            assertEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS, objectiveState(o, store.v2LearnerView(), old))
            assertTrue(creditItemIds(p, store.v2LearnerView()).isEmpty())
            assertNotEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS, objectiveState(p.unit(29)!!.objectives.single { it.id == o.id }, store.v2LearnerView(), p))
            assertEquals(3, store.v2Attempts().size)
        }
    }
    private fun sha(s: String) = java.security.MessageDigest.getInstance("SHA-256").digest(s.toByteArray(Charsets.UTF_8)).joinToString("") { "%02x".format(it.toInt() and 255) }
    private fun context(): Context = ApplicationProvider.getApplicationContext()
    private fun root() = File(if (File("app").isDirectory) "." else "..")
    private fun pack() = V2PackLoader.parse(File(root(), "app/src/main/assets/content/v2-curriculum.json").readText())
}
