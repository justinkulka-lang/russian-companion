package dev.russiancompanion.v2

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.curriculum.*
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
class Unit14ImpersonalRemediationTest {
    @get:Rule val compose = createComposeRule()
    @Test fun allThreeStateCardsPrecedePracticeAndRepairRestoresEveryExample() {
        val p = pack(); val s = session(p); val old = session(baseline())
        assertEquals(1, old.steps.takeWhile { it.kind != "GUIDED" }.count { it.kind == "TEACH" })
        assertEquals(3, s.steps.takeWhile { it.kind != "GUIDED" }.count { it.kind == "TEACH" })
        val teaching = s.steps.filter { it.kind == "TEACH" }; val rem = p.step("u14-st-rem-impersonal")!!.second
        assertEquals(teaching.joinToString("\n\n") { it.body }, rem.body)
        assertEquals(listOf("ex-u14-impersonal-0", "ex-u14-impersonal-1", "ex-u14-impersonal-2", "ex-or-u14-impersonal-3"), rem.exampleIds)
        assertEquals(listOf("Ему было холодно", "Им было скучно", "ей было холодно", "бы́ло"), rem.exampleIds.map { p.example(it)!!.display })
        assertTrue(rem.body.contains("To refer to these states in the past tense, always use '''было'''"))
        assertEquals(baseline().provenanceRecords, p.provenanceRecords)
        rem.exampleIds.forEach { id -> assertTrue(p.provenanceRecords.any { it.objectiveId == "obj-u14-impersonal" && it.shippedText == p.example(id)!!.display }) }
        val valid = V2Validator.validate(p); assertTrue(valid.errors.joinToString("\n"), valid.ok)
    }
    @Test fun unchangedQuestionsRecordsAndRequirementsHaveAnExactSessionOnlyException() {
        val p = pack(); val old = baseline()
        assertEquals(declaredAfterSession(), session(p))
        val restored = p.copy(units = p.units.map { unit ->
            if (unit.number == 14) unit.copy(sessions = unit.sessions.map { current ->
                if (current.id == "u14-s-impersonal") session(old) else current
            }) else if (unit.number == 15) old.unit(15)!! else if (unit.number in 16..28) unit.copy(advancement = old.unit(unit.number)!!.advancement) else unit
        })
        assertEquals(Unit15ImperativeRemediationTest.declaredAfterUnit(), p.unit(15))
        assertEquals(old, restored)
        assertEquals(old.unit(14)!!.advancement, p.unit(14)!!.advancement)
        assertEquals(6, p.unit(14)!!.advancement!!.minimumDistinctChecks)
        assertEquals(old.items, p.items); assertEquals(old.examples, p.examples)
        assertEquals(old.provenanceRecords, p.provenanceRecords)
        assertEquals(old.unit(16)!!.copy(advancement = old.unit(16)!!.advancement!!.copy(requireDueReview = true, cumulativeReviewPolicyId = Unit16CumulativeReviewPolicy.definition.id)), p.unit(16))
        assertEquals(old.unit(17)!!.copy(advancement = old.unit(17)!!.advancement!!.copy(requireDueReview = true, cumulativeReviewPolicyId = Unit17CumulativeReviewPolicy.definition.id)), p.unit(17))
        assertEquals(old.unit(18)!!.copy(advancement = old.unit(18)!!.advancement!!.copy(requireDueReview = true, cumulativeReviewPolicyId = Unit18CumulativeReviewPolicy.definition.id)), p.unit(18))
        assertEquals(old.unit(19)!!.copy(advancement = old.unit(19)!!.advancement!!.copy(requireDueReview = true, cumulativeReviewPolicyId = Unit19CumulativeReviewPolicy.definition.id)), p.unit(19))
        assertEquals(old.unit(20)!!.copy(advancement = old.unit(20)!!.advancement!!.copy(requireDueReview = true, cumulativeReviewPolicyId = Unit20CumulativeReviewPolicy.definition.id)), p.unit(20))
        assertEquals(old.unit(21)!!.copy(advancement = old.unit(21)!!.advancement!!.copy(requireDueReview = true, cumulativeReviewPolicyId = Unit21CumulativeReviewPolicy.definition.id)), p.unit(21))
        assertEquals(old.unit(22)!!.copy(advancement = old.unit(22)!!.advancement!!.copy(requireDueReview = true, cumulativeReviewPolicyId = Unit22CumulativeReviewPolicy.definition.id)), p.unit(22))
        assertEquals(old.unit(23)!!.copy(advancement = old.unit(23)!!.advancement!!.copy(requireDueReview = true, cumulativeReviewPolicyId = Unit23CumulativeReviewPolicy.definition.id)), p.unit(23))
        assertEquals(old.unit(24)!!.copy(advancement = old.unit(24)!!.advancement!!.copy(requireDueReview = true, cumulativeReviewPolicyId = Unit24CumulativeReviewPolicy.definition.id)), p.unit(24))
        assertEquals(old.unit(25)!!.copy(advancement = old.unit(25)!!.advancement!!.copy(requireDueReview = true, cumulativeReviewPolicyId = Unit25CumulativeReviewPolicy.definition.id)), p.unit(25))
        assertEquals(old.unit(26)!!.copy(advancement = old.unit(26)!!.advancement!!.copy(requireDueReview = true, cumulativeReviewPolicyId = Unit26CumulativeReviewPolicy.definition.id)), p.unit(26))
        assertEquals(old.unit(27)!!.copy(advancement = old.unit(27)!!.advancement!!.copy(requireDueReview = true, cumulativeReviewPolicyId = Unit27CumulativeReviewPolicy.definition.id)), p.unit(27))
        assertEquals(old.unit(28)!!.copy(advancement = old.unit(28)!!.advancement!!.copy(requireDueReview = true, cumulativeReviewPolicyId = Unit28CumulativeReviewPolicy.definition.id)), p.unit(28))
        assertNull(cumulativeReviewPolicy(29))
    }
    @Test fun oldGuidePositionDoesNotResumeAsTheNewSecondTeachingCard() {
        val p = pack(); val old = baseline()
        withStore { store ->
            val profile = store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = 14,
                currentSessionId = "u14-s-impersonal", currentStepIndex = 1).verifiedResume(old)
            store.v2SaveProfile(profile)
            assertTrue(resumeIsCurrent(old, store.v2LearnerView()))
            assertFalse(resumeIsCurrent(p, store.v2LearnerView()))
            assertEquals(profile, store.v2Profile())
            assertEquals("GUIDED", session(old).steps[1].kind)
            assertEquals("u14-st-teach-impersonal-2", session(p).steps[1].id)
        }
    }
    @Test fun oldPrescribedAttemptsStayStoredButCannotCreditChangedUnitInstruction() {
        val p = pack(); val old = baseline(); val id = "u14-i-impersonal-1"; val step = "u14-st-check-impersonal"
        withStore { store ->
            store.v2RecordGovernedAttempt(prepareGovernedAttempt(old, id, old.item(id)!!.answer, PRESCRIBED_CHECK, step, "same-clock")!!)
            assertTrue(id in creditItemIds(old, store.v2LearnerView()))
            assertFalse(id in creditItemIds(p, store.v2LearnerView()))
            assertEquals(1, store.v2Attempts().size)
            store.v2RecordGovernedAttempt(prepareGovernedAttempt(p, id, p.item(id)!!.answer, PRESCRIBED_CHECK, step, "same-clock")!!)
            assertTrue(id in creditItemIds(p, store.v2LearnerView()))
            assertEquals(2, store.v2Attempts().size)
            val objective = p.unit(14)!!.objectives.single { it.id == "obj-u14-impersonal" }
            assertNotEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS, objectiveState(objective, store.v2LearnerView(), p))
        }
    }
    @Test fun wrongIndependentAnswerShowsCompleteRepairAndGuidedSuccessStillNeedsANewCheckAnswer() {
        val p = pack(); val item = p.item("u14-i-impersonal-1")!!; val check = p.step("u14-st-check-impersonal")!!
        withStore { store ->
            store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = 14,
                startingPoint = "RESUME_FROM_BOOK", currentSessionId = check.first.id,
                currentStepIndex = check.first.steps.indexOf(check.second)).verifiedResume(p))
            compose.setContent { V2App(store, p, {}) }
            awaitTag("v2-continue"); compose.onNodeWithTag("v2-continue").performClick()
            val wrong = item.options.single { it != item.answer }
            awaitTag("v2-option-${item.id}-$wrong"); compose.onNodeWithTag("v2-option-${item.id}-$wrong").performScrollTo().performClick()
            awaitTag("v2-remediation-${item.id}"); compose.onNodeWithTag("v2-remediation-${item.id}").performScrollTo().performClick()
            for (id in listOf("ex-u14-impersonal-0", "ex-u14-impersonal-1", "ex-u14-impersonal-2", "ex-or-u14-impersonal-3")) {
                awaitTag("v2-example-$id"); compose.onNodeWithTag("v2-example-$id").performScrollTo().assertIsDisplayed()
            }
            compose.onNodeWithText("To refer to these states in the past tense, always use '''было'''", substring = true).performScrollTo().assertIsDisplayed()
            awaitTag("v2-review-practice"); compose.onNodeWithTag("v2-review-practice").performClick()
            val guide = p.item("u14-g-impersonal-0")!!
            awaitTag("v2-option-${guide.id}-${guide.answer}"); compose.onNodeWithTag("v2-option-${guide.id}-${guide.answer}").performScrollTo().performClick()
            awaitTag("v2-return-to-check"); compose.onNodeWithTag("v2-return-to-check").performClick()
            awaitTag("v2-option-${item.id}-$wrong"); compose.onNodeWithTag("v2-option-${item.id}-$wrong").performScrollTo().assertIsSelected()
            assertFalse(item.id in creditItemIds(p, store.v2LearnerView()))
            compose.onNodeWithTag("v2-option-${item.id}-${item.answer}").performScrollTo().performClick()
            compose.waitUntil(30000) { compose.waitForIdle(); item.id in creditItemIds(p, store.v2LearnerView()) }
            assertFalse("u14-i-impersonal-2" in creditItemIds(p, store.v2LearnerView()))
            assertFalse(advancementDecision(p, p.unit(14)!!, store.v2LearnerView()).allowed)
        }
    }
    private fun withStore(block: (LocalStore) -> Unit) {
        val name = "unit14-impersonal-${System.nanoTime()}.db"
        try { LocalStore.openNamed(context(), name).use(block) }
        finally { context().deleteDatabase(name); File(context().filesDir, "recordings-$name").deleteRecursively() }
    }
    private fun session(p: V2CurriculumPack) = p.unit(14)!!.sessions.single { it.id == "u14-s-impersonal" }
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
            val command = ProcessBuilder("git", "show", "2546331775c783813bd2c4785a5e08e6dfe42ed0:app/src/main/assets/content/v2-curriculum.json").directory(root()).start()
            V2PackLoader.parse(command.inputStream.bufferedReader().readText()).also { assertEquals(0, command.waitFor()); V2PackIdentityIndex.warm(it) }
        }
        internal fun declaredAfterSession(): V2Session {
            val ledger = Json.parseToJsonElement(File(root(), "audit/independent-gpt61-2026-10-12/ia003-unit14-impersonal-repair/CORRECTION.json").readText()).jsonObject
            return Json { ignoreUnknownKeys = true }.decodeFromJsonElement<V2Session>(ledger.getValue("afterSession"))
        }
    }
}
