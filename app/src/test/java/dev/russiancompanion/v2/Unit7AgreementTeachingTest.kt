package dev.russiancompanion.v2

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.curriculum.*
import dev.russiancompanion.v2.ui.V2App
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
        val p = pack(); val s = session(p)
        assertEquals(7, s.steps.takeWhile { it.kind != "GUIDED" }.count { it.kind == "TEACH" })
        val teaching = s.steps.filter { it.kind == "TEACH" }; val rem = p.step("u7-st-rem-endings")!!.second
        assertEquals(teaching.joinToString("\n\n") { it.body }, rem.body)
        val examples = p.unit(7)!!.objectives.single { it.id == "obj-u7-endings" }.exampleIds
        assertEquals(11, examples.size)
        assertEquals(examples, rem.exampleIds)
        assertTrue(s.steps[0].body.contains("но́вая книга") && s.steps[0].body.contains("но́вую книгу"))
        assertTrue(s.steps[1].body.contains("но́вое окно") && s.steps[1].body.contains("но́вого окна"))
        assertTrue(rem.body.contains("Both words change in these supplied case pairs."))
        assertTrue(p.provenanceRecords.any { it.objectiveId == "obj-u7-endings" && it.shippedText == rem.body && it.itemId == "prov-ca-u7-agreement-u7-st-rem-endings-complete-v1" })
        val valid = V2Validator.validate(p); assertTrue(valid.errors.joinToString("\n"), valid.ok)
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

    private fun session(p: V2CurriculumPack) = p.unit(7)!!.sessions.single { it.id == "u7-s-endings" }
    private fun pack() = current
    private fun context(): Context = ApplicationProvider.getApplicationContext()
    private fun awaitTag(tag: String) = compose.waitUntil(conditionDescription = tag, timeoutMillis = 30000) {
        compose.waitForIdle(); compose.onAllNodesWithTag(tag).fetchSemanticsNodes().isNotEmpty()
    }
    companion object {
        private fun root() = File(if (File("app").isDirectory) "." else "..")
        private val current by lazy { V2PackLoader.parse(File(root(), "app/src/main/assets/content/v2-curriculum.json").readText()).also { V2PackIdentityIndex.warm(it) } }
    }
}
