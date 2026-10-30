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
class Unit13PastTeachingTest {
    @get:Rule val compose = createComposeRule()
    @Test fun completePastTeachingPrecedesPracticeAndRepairRestoresAllFiveExamples() {
        val historical = pack()
        val p = V2PackLoader.parse(File(root(), "app/src/main/assets/content/v2-curriculum.json").readText())
            .also { V2PackIdentityIndex.warm(it) }
        // Current whole-pack validation must retain the exact historical Unit13 teaching target.
        val historicalUnit = historical.unit(13)!!
        // Preserve the old whole-unit comparison on its full-pack-proven v2 declaration only.
        RecordedCurriculumConservationScope.beforeUnit13PresentReview(root(), p).let { p ->
            assertEquals(historicalUnit, p.unit(13))
        }
        // Actual current unit identity and all teaching/content/validation assertions below remain current.
        RecordedIntakeCorrectionScope.assertCurrentUnitMatches(p,
            RecordedCurriculumConservationScope.source(root(), RecordedCurriculumConservationScope.currentPackImplementation), 13)
        val objectiveIds = historicalUnit.objectives.map { it.id }.toSet()
        assertEquals(historical.items.filter { it.objectiveId in objectiveIds }, p.items.filter { it.objectiveId in objectiveIds })
        assertEquals(historical.provenanceRecords.filter { it.unitNumber == 13 }, p.provenanceRecords.filter { it.unitNumber == 13 })
        val exampleIds = (historicalUnit.objectives.flatMap { it.exampleIds } +
            historicalUnit.sessions.flatMap { it.steps }.flatMap { it.exampleIds }).toSet()
        assertEquals(historical.examples.filter { it.id in exampleIds }, p.examples.filter { it.id in exampleIds })
        val s = session(p); val old = session(baseline())
        assertEquals(1, old.steps.takeWhile { it.kind != "GUIDED" }.count { it.kind == "TEACH" })
        assertEquals(4, s.steps.takeWhile { it.kind != "GUIDED" }.count { it.kind == "TEACH" })
        val teaching = s.steps.filter { it.kind == "TEACH" }; val rem = p.step("u13-st-rem-past-aspect")!!.second
        assertEquals(teaching.joinToString("\n\n") { it.body }, rem.body)
        assertEquals(listOf("ex-u13-past-aspect-0", "ex-u13-past-aspect-1", "ex-or-u13-past-aspect-2", "ex-or-u13-past-aspect-3", "ex-or-u13-past-aspect-4"), rem.exampleIds)
        assertEquals(listOf("я читал", "я прочитал", "чита́ть", "чита́л", "чита́ла"), rem.exampleIds.map { p.example(it)!!.display })
        assertTrue(rem.body.contains("The pronoun I does not choose one past-tense gender for every speaker;"))
        assertTrue(p.provenanceRecords.any { it.objectiveId == "obj-u13-past-aspect" && it.shippedText == rem.body && it.itemId == "prov-ca-u13-past-u13-st-rem-past-aspect-complete-v1" })
        val valid = V2Validator.validate(p); assertTrue(valid.errors.joinToString("\n"), valid.ok)
    }
    @Test fun oldGuidePositionDoesNotResumeAsTheNewSecondTeachingCard() {
        val p = pack(); val old = baseline()
        LocalStore.openInMemory(context()).use { store ->
            val profile = store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = 13,
                currentSessionId = "u13-s-past-aspect", currentStepIndex = 1).verifiedResume(old)
            store.v2SaveProfile(profile)
            assertTrue(resumeIsCurrent(old, store.v2LearnerView()))
            assertFalse(resumeIsCurrent(p, store.v2LearnerView()))
            assertEquals(profile, store.v2Profile())
            assertEquals("GUIDED", session(old).steps[1].kind)
            assertEquals("u13-st-teach-past-aspect-2", session(p).steps[1].id)
        }
    }
    @Test fun oldPrescribedAttemptsStayStoredButCannotCreditChangedUnitInstruction() {
        val p = pack(); val old = baseline(); val id = "u13-i-past-aspect-1"; val step = "u13-st-check-past-aspect"
        LocalStore.openInMemory(context()).use { store ->
            store.v2RecordGovernedAttempt(prepareGovernedAttempt(old, id, old.item(id)!!.answer, PRESCRIBED_CHECK, step, "same-clock")!!)
            assertTrue(id in creditItemIds(old, store.v2LearnerView()))
            assertFalse(id in creditItemIds(p, store.v2LearnerView()))
            assertEquals(1, store.v2Attempts().size)
            store.v2RecordGovernedAttempt(prepareGovernedAttempt(p, id, p.item(id)!!.answer, PRESCRIBED_CHECK, step, "same-clock")!!)
            assertTrue(id in creditItemIds(p, store.v2LearnerView()))
            assertEquals(2, store.v2Attempts().size)
            val objective = p.unit(13)!!.objectives.single { it.id == "obj-u13-past-aspect" }
            assertNotEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS, objectiveState(objective, store.v2LearnerView(), p))
        }
    }
    @Test fun wrongAspectChoiceRestoresCompleteRepairAndRequiresFreshIndependentAnswer() = repairUi("u13-i-past-aspect-1")
    @Test fun wrongGenderChoiceRestoresCompleteRepairAndRequiresFreshIndependentAnswer() = repairUi("u13-i-past-aspect-2")
    private fun repairUi(itemId: String) {
        val p = pack(); val item = p.item(itemId)!!; val check = p.step("u13-st-check-past-aspect")!!
        LocalStore.openInMemory(context()).use { store ->
            store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = 13,
                startingPoint = "RESUME_FROM_BOOK", currentSessionId = check.first.id,
                currentStepIndex = check.first.steps.indexOf(check.second)).verifiedResume(p))
            compose.setContent { V2App(store, p, {}) }
            awaitTag("v2-continue"); compose.onNodeWithTag("v2-continue").performClick()
            val wrong = item.options.single { it != item.answer }
            awaitTag("v2-option-${item.id}-$wrong"); compose.onNodeWithTag("v2-option-${item.id}-$wrong").performScrollTo().performClick()
            awaitTag("v2-remediation-${item.id}"); compose.onNodeWithTag("v2-remediation-${item.id}").performScrollTo().performClick()
            for (id in listOf("ex-u13-past-aspect-0", "ex-u13-past-aspect-1", "ex-or-u13-past-aspect-2", "ex-or-u13-past-aspect-3", "ex-or-u13-past-aspect-4")) {
                awaitTag("v2-example-$id"); compose.onNodeWithTag("v2-example-$id").performScrollTo().assertIsDisplayed()
            }
            compose.onNodeWithText("The pronoun I does not choose one past-tense gender for every speaker;", substring = true).performScrollTo().assertIsDisplayed()
            awaitTag("v2-review-practice"); compose.onNodeWithTag("v2-review-practice").performClick()
            val guide = p.item("u13-g-past-aspect-0")!!
            awaitTag("v2-option-${guide.id}-${guide.answer}"); compose.onNodeWithTag("v2-option-${guide.id}-${guide.answer}").performScrollTo().performClick()
            awaitTag("v2-return-to-check"); compose.onNodeWithTag("v2-return-to-check").performClick()
            awaitTag("v2-option-${item.id}-$wrong"); compose.onNodeWithTag("v2-option-${item.id}-$wrong").performScrollTo().assertIsSelected()
            assertFalse(item.id in creditItemIds(p, store.v2LearnerView()))
            compose.onNodeWithTag("v2-option-${item.id}-${item.answer}").performScrollTo().performClick()
            compose.waitUntil(30000) { compose.waitForIdle(); item.id in creditItemIds(p, store.v2LearnerView()) }
            assertFalse(listOf("u13-i-past-aspect-1", "u13-i-past-aspect-2").single { it != itemId } in creditItemIds(p, store.v2LearnerView()))
            assertFalse(advancementDecision(p, p.unit(13)!!, store.v2LearnerView()).allowed)
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
    private fun session(p: V2CurriculumPack) = p.unit(13)!!.sessions.single { it.id == "u13-s-past-aspect" }
    private fun pack() = current
    private fun baseline() = previous
    private fun context(): Context = ApplicationProvider.getApplicationContext()
    private fun awaitTag(tag: String) = compose.waitUntil(conditionDescription = tag, timeoutMillis = 30000) {
        compose.waitForIdle(); compose.onAllNodesWithTag(tag).fetchSemanticsNodes().isNotEmpty()
    }
    companion object {
        private fun root() = File(if (File("app").isDirectory) "." else "..")
        private val current by lazy { Unit7AgreementTeachingTest.beforeTeachingCorrectionPack() } // Exact post-Unit13-policy source2ff71f19; this historical transition predates the later Unit7 teaching correction.
        private val previous by lazy {
            val command = ProcessBuilder("git", "show", "03647d041d3d5068d4c77eb88f62403daf6e54ff:app/src/main/assets/content/v2-curriculum.json").directory(root()).start()
            V2PackLoader.parse(command.inputStream.bufferedReader().readText()).also { assertEquals(0, command.waitFor()); V2PackIdentityIndex.warm(it) }
        }
        internal fun beforeTeachingCorrectionPack(): V2CurriculumPack = previous
    }
}
