package dev.russiancompanion.v2

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.backup.*
import dev.russiancompanion.v2.curriculum.*
import dev.russiancompanion.v2.ui.V2App
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.*
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class Unit3GenderTeachingTest {
    @get:Rule val compose = createComposeRule()

    @Before fun finishApplicationStartupBeforeOwningTheRecordingRoot() {
        (context().applicationContext as dev.russiancompanion.RussianCompanionApplication).closeStartupStoreForTests()
    }

    @Test fun allFiveIndependentRecordsHaveExplicitTeachingAndCompleteRepair() {
        val p = current; val unit = p.unit(3)!!; val objective = unit.objectives.single { it.id == "obj-u3-gender" }
        val teaching = objective.instructionStepIds.mapNotNull { p.step(it)?.second }.filter { it.kind == "TEACH" }
        val repair = p.step("u3-st-rem-gender")!!.second
        assertEquals(5, teaching.size)
        assertEquals(teaching.joinToString("\n\n") { it.body }, repair.body)
        assertEquals(teaching.flatMap { it.exampleIds }.distinct(), repair.exampleIds)
        objective.checkItemIds.forEach { id ->
            val item = p.item(id)!!
            assertTrue(id, teaching.any { step -> step.exampleIds.any { p.example(it)!!.display == item.promptLetter } })
            assertTrue(id, repair.exampleIds.any { p.example(it)!!.display == item.promptLetter })
        }
        assertTrue(p.step("u3-st-teach-gender-2")!!.second.body.contains("друг — friend is also masculine"))
        assertTrue(p.step("u3-st-teach-gender-3")!!.second.body.contains("дверь — door is feminine"))
        assertEquals(listOf("u3-st-guided-gender-2", "u3-st-guided-gender-3", "u3-st-guided-gender-4", "u3-st-guided-gender-5"), reviewedGuidedPractice(p, repair.id))
        val validation = V2Validator.validate(p); assertTrue(validation.errors.joinToString("\n"), validation.ok)
    }





    @Test fun wrongDoorAnswerRestoresTheFullContrastAndStillNeedsFreshIndependentWork() = repairUi("u3-i-gender-дверь")
    @Test fun wrongNeuterAnswerRestoresTheFullContrastAndStillNeedsFreshIndependentWork() = repairUi("u3-i-gender-море")

    private fun repairUi(id: String) {
        val p = current; val item = p.item(id)!!; val check = p.step("u3-st-check-gender")!!
        LocalStore.openInMemory(context()).use { store ->
            store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = 3,
                startingPoint = "RESUME_FROM_BOOK", currentSessionId = check.first.id,
                currentStepIndex = check.first.steps.indexOf(check.second)).verifiedResume(p))
            compose.setContent { V2App(store, p, {}) }
            awaitTag("v2-continue"); compose.onNodeWithTag("v2-continue").performClick()
            val wrong = item.options.first { it != item.answer }
            awaitTag("v2-option-$id-$wrong"); compose.onNodeWithTag("v2-option-$id-$wrong").performScrollTo().performClick()
            awaitTag("v2-item-feedback-$id"); compose.onNodeWithTag("v2-item-feedback-$id").performScrollTo().assertTextEquals("Not the reviewed answer. ${item.feedback}")
            compose.onNodeWithTag("v2-remediation-$id").performScrollTo().performClick()
            awaitTag("v2-step-title"); compose.onNodeWithTag("v2-step-title").assertTextEquals("Review the admitted noun genders")
            for (example in listOf("ex-u3-друг", "ex-u3-дверь", "ex-u3-море", "ex-u3-дядя")) compose.onNodeWithTag("v2-example-$example").performScrollTo().assertIsDisplayed()
            compose.onNodeWithTag("v2-review-practice").performClick()
            val guides = reviewedGuidedPractice(p, "u3-st-rem-gender")
            guides.forEachIndexed { index, step ->
                p.step(step)!!.second.itemIds.forEach { guideId ->
                    val guide = p.item(guideId)!!
                    awaitTag("v2-option-$guideId-${guide.answer}")
                    compose.onNodeWithTag("v2-option-$guideId-${guide.answer}").performScrollTo().performClick()
                }
                val tag = if (index == guides.lastIndex) "v2-return-to-check" else "v2-next-practice"
                awaitTag(tag); compose.onNodeWithTag(tag).performClick()
            }
            awaitTag("v2-option-$id-$wrong"); compose.onNodeWithTag("v2-option-$id-$wrong").performScrollTo().assertIsSelected()
            assertFalse(id in creditItemIds(p, store.v2LearnerView()))
            assertTrue(store.cumulativeReviewHistory().first.isEmpty())
            compose.onNodeWithTag("v2-option-$id-${item.answer}").performScrollTo().performClick()
            compose.waitUntil(30000) { compose.waitForIdle(); id in creditItemIds(p, store.v2LearnerView()) }
            assertFalse(advancementDecision(p, p.unit(3)!!, store.v2LearnerView()).allowed)
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
    private fun awaitTag(tag: String) = compose.waitUntil(conditionDescription = tag, timeoutMillis = 30000) {
        compose.waitForIdle(); compose.onAllNodesWithTag(tag).fetchSemanticsNodes().isNotEmpty()
    }
    private fun context(): Context = ApplicationProvider.getApplicationContext()
    companion object {
        private fun root() = File(if (File("app").isDirectory) "." else "..")
        private val current by lazy { V2PackLoader.parse(File(root(), "app/src/main/assets/content/v2-curriculum.json").readText()).also { V2PackIdentityIndex.warm(it) } }
        private val previous by lazy {
            val command = ProcessBuilder("git", "show", "547457b63b317dea560e4bdc36083c1a4dce315e:app/src/main/assets/content/v2-curriculum.json").directory(root()).start()
            V2PackLoader.parse(command.inputStream.bufferedReader().readText()).also { assertEquals(0, command.waitFor()); V2PackIdentityIndex.warm(it) }
        }
        private val postTeachingCorrection by lazy {
            val command = ProcessBuilder("git", "show", "61c68cfcf20cd92a4b6c310c4abf8ff2284e36e5:app/src/main/assets/content/v2-curriculum.json").directory(root()).start()
            V2PackLoader.parse(command.inputStream.bufferedReader().readText()).also { assertEquals(0, command.waitFor()); V2PackIdentityIndex.warm(it) }
        }
        internal fun preTeachingCorrectionPack(): V2CurriculumPack = previous
    }
}
