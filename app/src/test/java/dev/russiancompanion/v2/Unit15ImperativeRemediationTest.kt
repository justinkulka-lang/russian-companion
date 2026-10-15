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
class Unit15ImperativeRemediationTest {
    @get:Rule val compose = createComposeRule()
    @Test fun completeImperativeContrastsPrecedePracticeAndRepairRestoresEveryExample() {
        val p = pack(); val s = session(p); val old = session(baseline())
        assertEquals(1, old.steps.takeWhile { it.kind != "GUIDED" }.count { it.kind == "TEACH" })
        assertEquals(4, s.steps.takeWhile { it.kind != "GUIDED" }.count { it.kind == "TEACH" })
        val teaching = s.steps.filter { it.kind == "TEACH" }; val rem = p.step("u15-st-rem-imperative")!!.second
        assertEquals(teaching.joinToString("\n\n") { it.body }, rem.body)
        assertEquals(listOf("ex-or-u15-imperative-0", "ex-or-u15-imperative-1", "ex-or-u15-imperative-2", "ex-u15-imperative-3"), rem.exampleIds)
        assertEquals(listOf("чита́й", "чита́йте", "чита́ть", "Купи, пожалуйста, свежие фрукты."), rem.exampleIds.map { p.example(it)!!.display })
        assertTrue(rem.body.contains("used for multiple addressees or polite address to one person"))
        assertEquals(baseline().provenanceRecords, p.provenanceRecords)
        rem.exampleIds.forEach { id -> assertTrue(p.provenanceRecords.any { it.objectiveId == "obj-u15-imperative" && it.shippedText == p.example(id)!!.display }) }
        val valid = V2Validator.validate(p); assertTrue(valid.errors.joinToString("\n"), valid.ok)
    }
    @Test fun exactDeclaredUnitExceptionPreservesEveryQuestionRecordAndRequirement() {
        val p = pack(); val old = baseline()
        assertEquals(declaredAfterUnit(), p.unit(15))
        assertEquals(old, p.copy(units = p.units.map { if (it.number == 15) old.unit(15)!! else if (it.number in 16..21) it.copy(advancement = old.unit(it.number)!!.advancement) else it }))
        assertEquals(old.unit(15)!!.advancement, p.unit(15)!!.advancement)
        assertEquals(6, p.unit(15)!!.advancement!!.minimumDistinctChecks)
        assertEquals(10, p.unit(16)!!.advancement!!.minimumDistinctChecks)
        assertEquals(old.unit(16)!!.copy(advancement = old.unit(16)!!.advancement!!.copy(requireDueReview = true, cumulativeReviewPolicyId = Unit16CumulativeReviewPolicy.definition.id)), p.unit(16))
        assertEquals(old.unit(17)!!.copy(advancement = old.unit(17)!!.advancement!!.copy(requireDueReview = true, cumulativeReviewPolicyId = Unit17CumulativeReviewPolicy.definition.id)), p.unit(17))
        assertEquals(old.unit(18)!!.copy(advancement = old.unit(18)!!.advancement!!.copy(requireDueReview = true, cumulativeReviewPolicyId = Unit18CumulativeReviewPolicy.definition.id)), p.unit(18))
        assertEquals(old.unit(19)!!.copy(advancement = old.unit(19)!!.advancement!!.copy(requireDueReview = true, cumulativeReviewPolicyId = Unit19CumulativeReviewPolicy.definition.id)), p.unit(19))
        assertEquals(old.unit(20)!!.copy(advancement = old.unit(20)!!.advancement!!.copy(requireDueReview = true, cumulativeReviewPolicyId = Unit20CumulativeReviewPolicy.definition.id)), p.unit(20))
        assertEquals(old.unit(21)!!.copy(advancement = old.unit(21)!!.advancement!!.copy(requireDueReview = true, cumulativeReviewPolicyId = Unit21CumulativeReviewPolicy.definition.id)), p.unit(21))
        assertNull(cumulativeReviewPolicy(22))
        val teaching = p.step("u15-st-teach-imperative-contrast")!!.second
        assertTrue("u15-st-teach-imperative-contrast" in p.unit(15)!!.objectives.single { it.id == "obj-u15-imperative" }.instructionStepIds)
        assertEquals(listOf("u15-g-imperative-0", "u15-i-imperative-1", "u15-i-imperative-2").joinToString("\n\n") { p.item(it)!!.feedback }, teaching.body)
    }
    @Test fun oldGuidePositionDoesNotResumeAsTheNewSecondTeachingCard() {
        val p = pack(); val old = baseline()
        withStore { store ->
            val profile = store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = 15,
                currentSessionId = "u15-s-imperative", currentStepIndex = 1).verifiedResume(old)
            store.v2SaveProfile(profile)
            assertTrue(resumeIsCurrent(old, store.v2LearnerView()))
            assertFalse(resumeIsCurrent(p, store.v2LearnerView()))
            assertEquals(profile, store.v2Profile())
            assertEquals("GUIDED", session(old).steps[1].kind)
            assertEquals("u15-st-teach-imperative-contrast", session(p).steps[1].id)
        }
    }
    @Test fun oldPrescribedAttemptsStayStoredButCannotCreditChangedUnitInstruction() {
        val p = pack(); val old = baseline(); val id = "u15-i-imperative-1"; val step = "u15-st-check-imperative"
        withStore { store ->
            store.v2RecordGovernedAttempt(prepareGovernedAttempt(old, id, old.item(id)!!.answer, PRESCRIBED_CHECK, step, "same-clock")!!)
            assertTrue(id in creditItemIds(old, store.v2LearnerView()))
            assertFalse(id in creditItemIds(p, store.v2LearnerView()))
            assertEquals(1, store.v2Attempts().size)
            store.v2RecordGovernedAttempt(prepareGovernedAttempt(p, id, p.item(id)!!.answer, PRESCRIBED_CHECK, step, "same-clock")!!)
            assertTrue(id in creditItemIds(p, store.v2LearnerView()))
            assertEquals(2, store.v2Attempts().size)
            val objective = p.unit(15)!!.objectives.single { it.id == "obj-u15-imperative" }
            assertNotEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS, objectiveState(objective, store.v2LearnerView(), p))
        }
    }
    @Test fun wrongIndependentAnswerShowsCompleteRepairAndGuidedSuccessStillNeedsANewCheckAnswer() {
        val p = pack(); val item = p.item("u15-i-imperative-1")!!; val check = p.step("u15-st-check-imperative")!!
        withStore { store ->
            store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = 15,
                startingPoint = "RESUME_FROM_BOOK", currentSessionId = check.first.id,
                currentStepIndex = check.first.steps.indexOf(check.second)).verifiedResume(p))
            compose.setContent { V2App(store, p, {}) }
            awaitTag("v2-continue"); compose.onNodeWithTag("v2-continue").performClick()
            val wrong = item.options.single { it != item.answer }
            awaitTag("v2-option-${item.id}-$wrong"); compose.onNodeWithTag("v2-option-${item.id}-$wrong").performScrollTo().performClick()
            awaitTag("v2-remediation-${item.id}"); compose.onNodeWithTag("v2-remediation-${item.id}").performScrollTo().performClick()
            for (id in listOf("ex-or-u15-imperative-0", "ex-or-u15-imperative-1", "ex-or-u15-imperative-2", "ex-u15-imperative-3")) {
                awaitTag("v2-example-$id"); compose.onNodeWithTag("v2-example-$id").performScrollTo().assertIsDisplayed()
            }
            compose.onNodeWithText("used for multiple addressees or polite address to one person", substring = true).performScrollTo().assertIsDisplayed()
            awaitTag("v2-review-practice"); compose.onNodeWithTag("v2-review-practice").performClick()
            val guide = p.item("u15-g-imperative-0")!!
            awaitTag("v2-option-${guide.id}-${guide.answer}"); compose.onNodeWithTag("v2-option-${guide.id}-${guide.answer}").performScrollTo().performClick()
            awaitTag("v2-return-to-check"); compose.onNodeWithTag("v2-return-to-check").performClick()
            awaitTag("v2-option-${item.id}-$wrong"); compose.onNodeWithTag("v2-option-${item.id}-$wrong").performScrollTo().assertIsSelected()
            assertFalse(item.id in creditItemIds(p, store.v2LearnerView()))
            compose.onNodeWithTag("v2-option-${item.id}-${item.answer}").performScrollTo().performClick()
            compose.waitUntil(30000) { compose.waitForIdle(); item.id in creditItemIds(p, store.v2LearnerView()) }
            assertFalse("u15-i-imperative-2" in creditItemIds(p, store.v2LearnerView()))
            assertFalse(advancementDecision(p, p.unit(15)!!, store.v2LearnerView()).allowed)
        }
    }
    private fun withStore(block: (LocalStore) -> Unit) {
        val name = "unit15-imperative-${System.nanoTime()}.db"
        try { LocalStore.openNamed(context(), name).use(block) }
        finally { context().deleteDatabase(name); File(context().filesDir, "recordings-$name").deleteRecursively() }
    }
    private fun session(p: V2CurriculumPack) = p.unit(15)!!.sessions.single { it.id == "u15-s-imperative" }
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
            val command = ProcessBuilder("git", "show", "6975ead6e11b4eac4e56b9d538e026b577760385:app/src/main/assets/content/v2-curriculum.json").directory(root()).start()
            V2PackLoader.parse(command.inputStream.bufferedReader().readText()).also { assertEquals(0, command.waitFor()); V2PackIdentityIndex.warm(it) }
        }
        internal fun declaredAfterUnit(): V2Unit {
            val ledger = Json.parseToJsonElement(File(root(), "audit/independent-gpt61-2026-10-12/ia003-unit15-imperative-repair/CORRECTION.json").readText()).jsonObject
            return Json { ignoreUnknownKeys = true }.decodeFromJsonElement<V2Unit>(ledger.getValue("afterUnit"))
        }
        internal fun declaredAfterSession(): V2Session {
            val ledger = Json.parseToJsonElement(File(root(), "audit/independent-gpt61-2026-10-12/ia003-unit15-imperative-repair/CORRECTION.json").readText()).jsonObject
            return Json { ignoreUnknownKeys = true }.decodeFromJsonElement<V2Session>(ledger.getValue("afterSession"))
        }
    }
}
