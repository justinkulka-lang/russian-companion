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
class Unit13ContextTeachingTest {
    @get:Rule val compose = createComposeRule()
    private fun session(p: V2CurriculumPack, key: String) = p.unit(13)!!.sessions.single { it.id == "u13-s-$key" }
    @Test fun bothChainsTeachBeforePracticeAndRepairEveryOriginalExample() {
        for (key in keys) {
            val se = session(current, key); val prior = session(previous, key)
            assertEquals(1, prior.steps.takeWhile { it.kind != "GUIDED" }.count { it.kind == "TEACH" })
            assertEquals(3, se.steps.takeWhile { it.kind != "GUIDED" }.count { it.kind == "TEACH" })
            val teaching = se.steps.filter { it.kind == "TEACH" }; val rem = current.step("u13-st-rem-$key")!!.second
            assertEquals(teaching.joinToString("\n\n") { it.body }, rem.body)
            assertEquals(teaching.flatMap { it.exampleIds }.distinct(), rem.exampleIds)
            assertEquals(if (key == "tense-keep") 3 else 4, rem.exampleIds.size)
            assertTrue(current.provenanceRecords.any { it.objectiveId == "obj-u13-$key" && it.shippedText == rem.body })
        }
        assertTrue(current.step("u13-st-rem-tense-keep")!!.second.body.contains("Backshift is not mandatory"))
        assertTrue(current.step("u13-st-rem-infinitive")!!.second.body.contains("context-dependent counterparts"))
        // Historical conservation stays above; current full-pack validation has current rule pins.
        val validationPack = V2PackLoader.parse(File(root(), "app/src/main/assets/content/v2-curriculum.json").readText())
            .also { V2PackIdentityIndex.warm(it) }
        val historicalUnit = current.unit(13)!!
        // Preserve the old whole-unit comparison on its full-pack-proven v2 declaration only.
        RecordedCurriculumConservationScope.beforeUnit13PresentReview(root(), validationPack).let { validationPack ->
            assertEquals(historicalUnit, validationPack.unit(13))
        }
        // Actual current unit identity and all teaching/content/validation assertions below remain current.
        RecordedIntakeCorrectionScope.assertCurrentUnitMatches(validationPack,
            RecordedCurriculumConservationScope.source(root(), RecordedCurriculumConservationScope.currentPackImplementation), 13)
        val objectiveIds = historicalUnit.objectives.map { it.id }.toSet()
        assertEquals(current.items.filter { it.objectiveId in objectiveIds }, validationPack.items.filter { it.objectiveId in objectiveIds })
        assertEquals(current.provenanceRecords.filter { it.unitNumber == 13 }, validationPack.provenanceRecords.filter { it.unitNumber == 13 })
        assertEquals(current.sourceCapabilities.filter { it.unitNumber == 13 }, validationPack.sourceCapabilities.filter { it.unitNumber == 13 })
        val exampleIds = (historicalUnit.objectives.flatMap { it.exampleIds } +
            historicalUnit.sessions.flatMap { it.steps }.flatMap { it.exampleIds }).toSet()
        assertEquals(current.examples.filter { it.id in exampleIds }, validationPack.examples.filter { it.id in exampleIds })
        val result = V2Validator.validate(validationPack); assertTrue(result.errors.joinToString("\n"), result.ok)
    }
    @Test fun oldGuidedPositionsCannotResumeAsDifferentTeachingCards() {
        for (key in keys) LocalStore.openInMemory(context()).use { store ->
            val profile = store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = 13,
                currentSessionId = "u13-s-$key", currentStepIndex = 1).verifiedResume(previous)
            store.v2SaveProfile(profile)
            assertTrue(resumeIsCurrent(previous, store.v2LearnerView()))
            assertFalse(resumeIsCurrent(current, store.v2LearnerView()))
            assertEquals(profile, store.v2Profile())
            assertEquals("GUIDED", session(previous, key).steps[1].kind)
            assertEquals("u13-st-teach-$key-2", session(current, key).steps[1].id)
        }
    }
    @Test fun oldAnswersStayRawButDoNotSupplyChangedInstructionCredit() {
        for (key in keys) for (n in 1..2) LocalStore.openInMemory(context()).use { store ->
            val id = "u13-i-$key-$n"; val step = "u13-st-check-$key"
            store.v2RecordGovernedAttempt(prepareGovernedAttempt(previous, id, previous.item(id)!!.answer, PRESCRIBED_CHECK, step, "same-clock")!!)
            assertTrue(id in creditItemIds(previous, store.v2LearnerView()))
            assertFalse(id in creditItemIds(current, store.v2LearnerView())); assertEquals(1, store.v2Attempts().size)
            store.v2RecordGovernedAttempt(prepareGovernedAttempt(current, id, current.item(id)!!.answer, PRESCRIBED_CHECK, step, "same-clock")!!)
            assertTrue(id in creditItemIds(current, store.v2LearnerView())); assertEquals(2, store.v2Attempts().size)
            assertNotEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS, objectiveState(current.unit(13)!!.objectives.single { it.id == "obj-u13-$key" }, store.v2LearnerView(), current))
        }
    }
    @Test fun wrongReportedEveningReturnsFromCompleteRepairToTheSameCheck() = repairUi("tense-keep", 1)
    @Test fun wrongEnglishBackshiftLabelReturnsFromCompleteRepairToTheSameCheck() = repairUi("tense-keep", 2)
    @Test fun wrongPerfectivePartnerReturnsFromCompleteRepairToTheSameCheck() = repairUi("infinitive", 1)
    @Test fun wrongImperfectivePartnerReturnsFromCompleteRepairToTheSameCheck() = repairUi("infinitive", 2)
    private fun repairUi(key: String, n: Int) {
        val p = current; val id = "u13-i-$key-$n"; val item = p.item(id)!!; val check = p.step("u13-st-check-$key")!!
        LocalStore.openInMemory(context()).use { store ->
            store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = 13,
                startingPoint = "RESUME_FROM_BOOK", currentSessionId = check.first.id,
                currentStepIndex = check.first.steps.indexOf(check.second)).verifiedResume(p))
            compose.setContent { V2App(store, p, {}) }
            awaitTag("v2-continue"); compose.onNodeWithTag("v2-continue").performClick()
            val wrong = item.options.single { it != item.answer }
            awaitTag("v2-option-$id-$wrong"); compose.onNodeWithTag("v2-option-$id-$wrong").performScrollTo().performClick()
            awaitTag("v2-item-feedback-$id"); compose.onNodeWithTag("v2-item-feedback-$id").performScrollTo().assertTextEquals("Not the reviewed answer. ${item.feedback}")
            compose.onNodeWithTag("v2-remediation-$id").performScrollTo().performClick()
            val rem = p.step("u13-st-rem-$key")!!.second
            rem.exampleIds.forEach { e -> awaitTag("v2-example-$e"); compose.onNodeWithTag("v2-example-$e").performScrollTo().assertIsDisplayed() }
            val qualifier = if (key == "tense-keep") "Backshift is not mandatory" else "context-dependent counterparts"
            compose.onNodeWithText(qualifier, substring = true).performScrollTo().assertIsDisplayed()
            compose.onNodeWithTag("v2-review-practice").performClick()
            val guide = p.item("u13-g-$key-0")!!
            awaitTag("v2-option-${guide.id}-${guide.answer}"); compose.onNodeWithTag("v2-option-${guide.id}-${guide.answer}").performScrollTo().performClick()
            awaitTag("v2-return-to-check"); compose.onNodeWithTag("v2-return-to-check").performClick()
            awaitTag("v2-option-$id-$wrong"); compose.onNodeWithTag("v2-option-$id-$wrong").performScrollTo().assertIsSelected()
            assertFalse(id in creditItemIds(p, store.v2LearnerView()))
            compose.onNodeWithTag("v2-option-$id-${item.answer}").performScrollTo().performClick()
            compose.waitUntil(30000) { compose.waitForIdle(); id in creditItemIds(p, store.v2LearnerView()) }
            assertFalse("u13-i-$key-${if (n == 1) 2 else 1}" in creditItemIds(p, store.v2LearnerView()))
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
    private fun context(): Context = ApplicationProvider.getApplicationContext()
    private fun awaitTag(tag: String) = compose.waitUntil(conditionDescription = tag, timeoutMillis = 30000) {
        compose.waitForIdle(); compose.onAllNodesWithTag(tag).fetchSemanticsNodes().isNotEmpty()
    }
    companion object {
        private val keys = listOf("tense-keep", "infinitive")
        private fun root() = File(if (File("app").isDirectory) "." else "..")
        private val current by lazy { Unit7AgreementTeachingTest.beforeTeachingCorrectionPack() } // Exact post-Unit13-policy source2ff71f19; this historical transition predates the later Unit7 teaching correction.
        private val previous by lazy {
            val process = ProcessBuilder("git", "show", "1730c1999a81e1d9d5df8bc3b957339fce1a65b4:app/src/main/assets/content/v2-curriculum.json").directory(root()).start()
            V2PackLoader.parse(process.inputStream.bufferedReader().readText()).also { assertEquals(0, process.waitFor()); V2PackIdentityIndex.warm(it) }
        }
        internal fun beforeTeachingCorrectionPack() = previous
    }
}
