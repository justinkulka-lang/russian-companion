package dev.russiancompanion.v2

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.curriculum.*
import dev.russiancompanion.v2.backup.*
import kotlinx.coroutines.runBlocking
import dev.russiancompanion.v2.ui.V2App
import kotlinx.serialization.json.*
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class Unit7AgreementTeachingTest {
    @get:Rule val compose = createComposeRule()
    @Test fun completeAgreementTeachingPrecedesPracticeAndRepairRestoresAllElevenExamples() {
        val p = pack(); val s = session(p); val old = session(baseline())
        assertEquals(1, old.steps.takeWhile { it.kind != "GUIDED" }.count { it.kind == "TEACH" })
        assertEquals(7, s.steps.takeWhile { it.kind != "GUIDED" }.count { it.kind == "TEACH" })
        val teaching = s.steps.filter { it.kind == "TEACH" }; val rem = p.step("u7-st-rem-endings")!!.second
        assertEquals(teaching.joinToString("\n\n") { it.body }, rem.body)
        val originalExamples = baseline().unit(7)!!.objectives.single { it.id == "obj-u7-endings" }.exampleIds
        assertEquals(11, originalExamples.size)
        assertEquals(originalExamples, rem.exampleIds)
        assertEquals(originalExamples.map { baseline().example(it) }, rem.exampleIds.map { p.example(it) })
        assertTrue(s.steps[0].body.contains("но́вая книга") && s.steps[0].body.contains("но́вую книгу"))
        assertTrue(s.steps[1].body.contains("но́вое окно") && s.steps[1].body.contains("но́вого окна"))
        assertTrue(rem.body.contains("Both words change in these supplied case pairs."))
        assertTrue(p.provenanceRecords.any { it.objectiveId == "obj-u7-endings" && it.shippedText == rem.body && it.itemId == "prov-ca-u7-agreement-u7-st-rem-endings-complete-v1" })
        val valid = V2Validator.validate(p); assertTrue(valid.errors.joinToString("\n"), valid.ok)
    }
    @Test fun oldGuidePositionDoesNotResumeAsTheNewSecondTeachingCard() {
        val p = pack(); val old = baseline()
        LocalStore.openInMemory(context()).use { store ->
            val profile = store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = 7,
                currentSessionId = "u7-s-endings", currentStepIndex = 1).verifiedResume(old)
            store.v2SaveProfile(profile)
            assertTrue(resumeIsCurrent(old, store.v2LearnerView()))
            assertFalse(resumeIsCurrent(p, store.v2LearnerView()))
            assertEquals(profile, store.v2Profile())
            assertEquals("GUIDED", session(old).steps[1].kind)
            assertEquals("u7-st-teach-endings-2", session(p).steps[1].id)
        }
    }
    @Test fun oldPrescribedAttemptsStayStoredButCannotCreditChangedUnitInstruction() {
        val p = pack(); val old = baseline(); val id = "u7-i-endings-1"; val step = "u7-st-check-endings"
        LocalStore.openInMemory(context()).use { store ->
            store.v2RecordGovernedAttempt(prepareGovernedAttempt(old, id, old.item(id)!!.answer, PRESCRIBED_CHECK, step, "same-clock")!!)
            assertTrue(id in creditItemIds(old, store.v2LearnerView()))
            assertFalse(id in creditItemIds(p, store.v2LearnerView()))
            assertEquals(1, store.v2Attempts().size)
            store.v2RecordGovernedAttempt(prepareGovernedAttempt(p, id, p.item(id)!!.answer, PRESCRIBED_CHECK, step, "same-clock")!!)
            assertTrue(id in creditItemIds(p, store.v2LearnerView()))
            assertEquals(2, store.v2Attempts().size)
            val objective = p.unit(7)!!.objectives.single { it.id == "obj-u7-endings" }
            assertNotEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS, objectiveState(objective, store.v2LearnerView(), p))
        }
    }
    @Test fun wrongGenitiveChoiceRestoresCompleteRepairAndRequiresFreshIndependentAnswer() = repairUi("u7-i-endings-1")
    @Test fun wrongNominativeChoiceRestoresCompleteRepairAndRequiresFreshIndependentAnswer() = repairUi("u7-i-endings-4")
    private fun repairUi(itemId: String) {
        val p = pack(); val item = p.item(itemId)!!; val check = p.step("u7-st-check-endings")!!
        LocalStore.openInMemory(context()).use { store ->
            store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = 7,
                startingPoint = "RESUME_FROM_BOOK", currentSessionId = check.first.id,
                currentStepIndex = check.first.steps.indexOf(check.second)).verifiedResume(p))
            compose.setContent { V2App(store, p, {}) }
            awaitTag("v2-continue"); compose.onNodeWithTag("v2-continue").performClick()
            val wrong = item.options.first { it != item.answer }
            awaitTag("v2-option-${item.id}-$wrong"); compose.onNodeWithTag("v2-option-${item.id}-$wrong").performScrollTo().performClick()
            awaitTag("v2-remediation-${item.id}"); compose.onNodeWithTag("v2-remediation-${item.id}").performScrollTo().performClick()
            for (id in p.unit(7)!!.objectives.single { it.id == "obj-u7-endings" }.exampleIds) {
                awaitTag("v2-example-$id"); compose.onNodeWithTag("v2-example-$id").performScrollTo().assertIsDisplayed()
            }
            compose.onNodeWithText("Both words change in these supplied case pairs.", substring = true).performScrollTo().assertIsDisplayed()
            awaitTag("v2-review-practice"); compose.onNodeWithTag("v2-review-practice").performClick()
            val guide = p.item("u7-g-endings-0")!!
            awaitTag("v2-option-${guide.id}-${guide.answer}"); compose.onNodeWithTag("v2-option-${guide.id}-${guide.answer}").performScrollTo().performClick()
            awaitTag("v2-return-to-check"); compose.onNodeWithTag("v2-return-to-check").performClick()
            awaitTag("v2-option-${item.id}-$wrong"); compose.onNodeWithTag("v2-option-${item.id}-$wrong").performScrollTo().assertIsSelected()
            assertFalse(item.id in creditItemIds(p, store.v2LearnerView()))
            compose.onNodeWithTag("v2-option-${item.id}-${item.answer}").performScrollTo().performClick()
            compose.waitUntil(30000) { compose.waitForIdle(); item.id in creditItemIds(p, store.v2LearnerView()) }
            p.unit(7)!!.objectives.single { it.id == "obj-u7-endings" }.checkItemIds.filter { it != itemId }.forEach { assertFalse(it in creditItemIds(p, store.v2LearnerView())) }
            assertFalse(advancementDecision(p, p.unit(7)!!, store.v2LearnerView()).allowed)
        }
    }


    private fun ordinary(p: V2CurriculumPack, store: LocalStore, number: Int) {
        store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = number, currentSessionId = null))
        p.unit(number)!!.sessions.flatMap { it.steps }.forEach { step ->
            step.itemIds.forEach { id -> store.v2RecordGovernedAttempt(prepareGovernedAttempt(p, id, p.item(id)!!.answer, PRESCRIBED_CHECK, step.id, "same-clock")!!) }
            store.v2CompleteGovernedStep(step.id, governedStepCompletionDigest(p, store.v2LearnerView(), step.id)!!, "same-clock")
        }
        store.v2AttestBook(number, "same-clock")
    }
    private fun session(p: V2CurriculumPack) = p.unit(7)!!.sessions.single { it.id == "u7-s-endings" }
    private fun pack() = current
    private fun baseline() = previous
    private fun context(): Context = ApplicationProvider.getApplicationContext()
    private fun awaitTag(tag: String) = compose.waitUntil(conditionDescription = tag, timeoutMillis = 30000) {
        compose.waitForIdle(); compose.onAllNodesWithTag(tag).fetchSemanticsNodes().isNotEmpty()
    }
    companion object {
        private fun root() = File(if (File("app").isDirectory) "." else "..")
        private val current by lazy { V2PackLoader.parse(File(root(), "app/src/main/assets/content/v2-curriculum.json").readText()).also { V2PackIdentityIndex.warm(it) } }
        private val previous by lazy {
            val command = ProcessBuilder("git", "show", "2ff71f197efb73d99662bc8a64180dc01a79aec3:app/src/main/assets/content/v2-curriculum.json").directory(root()).start()
            V2PackLoader.parse(command.inputStream.bufferedReader().readText()).also { assertEquals(0, command.waitFor()); V2PackIdentityIndex.warm(it) }
        }
        // These two original transition scenarios predate the later Unit4/7/8 corrections.
        // Keep every original assertion on its actual implementation; current prerequisite
        // refusal and repair are exercised by Unit7CurrentPrerequisiteHistoryTest.
        private val agreementCorrection by lazy {
            val command = ProcessBuilder("git", "show", "f1dad5d93d7e9d9d0f2680866cb70227a4480cc3:app/src/main/assets/content/v2-curriculum.json")
                .directory(root()).start()
            V2PackLoader.parse(command.inputStream.bufferedReader().readText()).also {
                assertEquals(0, command.waitFor()); V2PackIdentityIndex.warm(it)
            }
        }
        private fun agreementCorrectionPack(): V2CurriculumPack = agreementCorrection
        internal fun beforeTeachingCorrectionPack(): V2CurriculumPack = previous
    }
}
