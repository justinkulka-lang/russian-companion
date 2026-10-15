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
class Unit11AgreementRemediationTest {
    @get:Rule val compose = createComposeRule()
    @Test fun completeAgreementTeachingPrecedesPracticeAndRepairRestoresTheCheckedForms() {
        val p = pack(); val s = session(p); val old = session(baseline())
        assertEquals(1, old.steps.takeWhile { it.kind != "GUIDED" }.count { it.kind == "TEACH" })
        assertEquals(4, s.steps.takeWhile { it.kind != "GUIDED" }.count { it.kind == "TEACH" })
        val teaching = s.steps.filter { it.kind == "TEACH" }; val rem = p.step("u11-st-rem-past-aspect")!!.second
        assertEquals(teaching.joinToString("\n\n") { it.body }, rem.body)
        assertEquals(listOf("ex-or-u11-past-aspect-2", "ex-or-u11-past-aspect-3", "ex-or-u11-past-aspect-4"), rem.exampleIds)
        assertEquals(listOf("прочита́л", "прочита́ла", "прочита́ло"), rem.exampleIds.map { p.example(it)!!.display })
        assertTrue(rem.body.contains("The pronoun I does not select one past-tense gender for every speaker."))
        assertTrue(p.provenanceRecords.any { it.objectiveId == "obj-u11-past-aspect" && it.shippedText == rem.body && it.itemId == "prov-ca-u11-agreement-remediation-complete-v1" })
        val valid = V2Validator.validate(p); assertTrue(valid.errors.joinToString("\n"), valid.ok)
    }
    @Test fun unchangedQuestionsRecordsAndRequirementsHaveAnExactSessionOnlyException() {
        val p = pack(); val old = baseline()
        assertEquals(declaredAfterSession(), session(p))
        assertEquals(old, p.copy(units = old.units, provenanceRecords = p.provenanceRecords.dropLast(1)))
        assertEquals(old.unit(11)!!.advancement, p.unit(11)!!.advancement)
        assertEquals(8, p.unit(11)!!.advancement!!.minimumDistinctChecks)
        assertEquals(old.items, p.items); assertEquals(old.examples, p.examples)
        assertEquals(old.provenanceRecords, p.provenanceRecords.dropLast(1))
        assertNull(cumulativeReviewPolicy(15))
    }
    @Test fun oldGuidePositionDoesNotResumeAsTheNewSecondTeachingCard() {
        val p = pack(); val old = baseline()
        LocalStore.openInMemory(context()).use { store ->
            val profile = store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = 11,
                currentSessionId = "u11-s-past-aspect", currentStepIndex = 1).verifiedResume(old)
            store.v2SaveProfile(profile)
            assertTrue(resumeIsCurrent(old, store.v2LearnerView()))
            assertFalse(resumeIsCurrent(p, store.v2LearnerView()))
            assertEquals(profile, store.v2Profile())
            assertEquals("GUIDED", session(old).steps[1].kind)
            assertEquals("u11-st-teach-past-aspect-2", session(p).steps[1].id)
        }
    }
    @Test fun oldPrescribedAttemptsStayStoredButCannotCreditChangedUnitInstruction() {
        val p = pack(); val old = baseline(); val id = "u11-i-past-aspect-1"; val step = "u11-st-check-past-aspect"
        LocalStore.openInMemory(context()).use { store ->
            store.v2RecordGovernedAttempt(prepareGovernedAttempt(old, id, old.item(id)!!.answer, PRESCRIBED_CHECK, step, "same-clock")!!)
            assertTrue(id in creditItemIds(old, store.v2LearnerView()))
            assertFalse(id in creditItemIds(p, store.v2LearnerView()))
            assertEquals(1, store.v2Attempts().size)
            store.v2RecordGovernedAttempt(prepareGovernedAttempt(p, id, p.item(id)!!.answer, PRESCRIBED_CHECK, step, "same-clock")!!)
            assertTrue(id in creditItemIds(p, store.v2LearnerView()))
            assertEquals(2, store.v2Attempts().size)
            val objective = p.unit(11)!!.objectives.single { it.id == "obj-u11-past-aspect" }
            assertNotEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS, objectiveState(objective, store.v2LearnerView(), p))
        }
    }
    @Test fun wrongIndependentAnswerShowsCompleteRepairAndGuidedSuccessStillNeedsANewCheckAnswer() {
        val p = pack(); val item = p.item("u11-i-past-aspect-1")!!; val check = p.step("u11-st-check-past-aspect")!!
        LocalStore.openInMemory(context()).use { store ->
            store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = 11,
                startingPoint = "RESUME_FROM_BOOK", currentSessionId = check.first.id,
                currentStepIndex = check.first.steps.indexOf(check.second)).verifiedResume(p))
            compose.setContent { V2App(store, p, {}) }
            awaitTag("v2-continue"); compose.onNodeWithTag("v2-continue").performClick()
            val wrong = item.options.single { it != item.answer }
            awaitTag("v2-option-${item.id}-$wrong"); compose.onNodeWithTag("v2-option-${item.id}-$wrong").performScrollTo().performClick()
            awaitTag("v2-remediation-${item.id}"); compose.onNodeWithTag("v2-remediation-${item.id}").performScrollTo().performClick()
            for (id in listOf("ex-or-u11-past-aspect-2", "ex-or-u11-past-aspect-3", "ex-or-u11-past-aspect-4")) {
                awaitTag("v2-example-$id"); compose.onNodeWithTag("v2-example-$id").performScrollTo().assertIsDisplayed()
            }
            compose.onNodeWithText("The pronoun I does not select one past-tense gender for every speaker.", substring = true).performScrollTo().assertIsDisplayed()
            awaitTag("v2-review-practice"); compose.onNodeWithTag("v2-review-practice").performClick()
            val guide = p.item("u11-g-past-aspect-0")!!
            awaitTag("v2-option-${guide.id}-${guide.answer}"); compose.onNodeWithTag("v2-option-${guide.id}-${guide.answer}").performScrollTo().performClick()
            awaitTag("v2-return-to-check"); compose.onNodeWithTag("v2-return-to-check").performClick()
            awaitTag("v2-option-${item.id}-$wrong"); compose.onNodeWithTag("v2-option-${item.id}-$wrong").performScrollTo().assertIsSelected()
            assertFalse(item.id in creditItemIds(p, store.v2LearnerView()))
            compose.onNodeWithTag("v2-option-${item.id}-${item.answer}").performScrollTo().performClick()
            compose.waitUntil(30000) { compose.waitForIdle(); item.id in creditItemIds(p, store.v2LearnerView()) }
            assertFalse("u11-i-past-aspect-2" in creditItemIds(p, store.v2LearnerView()))
            assertFalse(advancementDecision(p, p.unit(11)!!, store.v2LearnerView()).allowed)
        }
    }
    private fun session(p: V2CurriculumPack) = p.unit(11)!!.sessions.single { it.id == "u11-s-past-aspect" }
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
            val command = ProcessBuilder("git", "show", "ea3ef13a866edab83c024a317a52fc750e359787:app/src/main/assets/content/v2-curriculum.json").directory(root()).start()
            V2PackLoader.parse(command.inputStream.bufferedReader().readText()).also { assertEquals(0, command.waitFor()); V2PackIdentityIndex.warm(it) }
        }
        internal fun declaredAfterSession(): V2Session {
            val ledger = Json.parseToJsonElement(File(root(), "audit/independent-gpt61-2026-10-12/ia003-unit11-agreement-repair/CORRECTION.json").readText()).jsonObject
            return Json { ignoreUnknownKeys = true }.decodeFromJsonElement<V2Session>(ledger.getValue("afterSession"))
        }
    }
}
