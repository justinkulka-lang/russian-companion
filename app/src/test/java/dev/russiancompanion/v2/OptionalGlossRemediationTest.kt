package dev.russiancompanion.v2

import android.content.Context
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.curriculum.*
import dev.russiancompanion.v2.ui.V2App
import dev.russiancompanion.v2.ui.V2UiWork
import java.io.File
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class OptionalGlossRemediationTest {
    @get:Rule val compose = createComposeRule()

    @Test fun optionalMeaningExplanationReturnsToItsSelectionWithoutGrantingCaseRoleCredit() {
        val root = File(if (File("app").isDirectory) "." else "..")
        val pack = V2PackLoader.parse(File(root, "app/src/main/assets/content/v2-curriculum.json").readText())
        val located = pack.step("u4-st-optional-nominative-gloss")!!
        val step = located.second
        val explanation = pack.step(step.remediationStepId!!)!!.second
        val context: Context = ApplicationProvider.getApplicationContext()
        val name = "optional-gloss-${System.nanoTime()}.db"
        val previousBlock = V2UiWork.blockForTests
        V2UiWork.blockForTests = false
        try {
            LocalStore.openNamed(context, name).use { store ->
                store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = 4,
                    startingPoint = "RESUME_FROM_BOOK", currentSessionId = located.first.id,
                    currentStepIndex = located.first.steps.indexOf(step)).verifiedResume(pack))
                compose.setContent { V2App(store, pack, onRetryOpen = {}) }
                waitTag("v2-continue"); compose.onNodeWithTag("v2-continue").performClick()
                val item = pack.item(step.itemIds.first())!!
                val wrong = item.options.first { it != item.answer }
                waitTag("v2-option-${item.id}-$wrong")
                compose.onNodeWithTag("v2-option-${item.id}-$wrong").performScrollTo().performClick()
                waitText("Not the reviewed answer. ${item.feedback}")
                val before = store.governedLearnerDocument()
                assertEquals(1, before.attempts.size)
                compose.onNodeWithTag("v2-remediation-${item.id}").performScrollTo().performClick()
                waitText(explanation.title)
                explanation.exampleIds.forEach { id ->
                    compose.onNodeWithTag("v2-example-$id").performScrollTo().assertIsDisplayed()
                }
                compose.onNodeWithTag("v2-review-practice").assertDoesNotExist()
                compose.onNodeWithTag("v2-return-to-check").performClick()
                waitTag("v2-option-${item.id}-$wrong")
                compose.onNodeWithTag("v2-option-${item.id}-$wrong").performScrollTo().assertIsSelected()
                assertEquals(before.attempts, store.governedLearnerDocument().attempts)
                assertEquals(wrong, governedSessionSelections(pack, store.v2LearnerView())[governedSelectionKey(step.id, item.id)])

                step.itemIds.forEach { id ->
                    val optional = pack.item(id)!!
                    compose.onNodeWithTag("v2-option-$id-${optional.answer}").performScrollTo().performClick()
                    waitTag("v2-item-feedback-$id")
                    waitText("Recorded as correct. ${optional.feedback}")
                    compose.onNodeWithTag("v2-item-feedback-$id").assertTextEquals("Recorded as correct. ${optional.feedback}")
                }
                val learner = store.v2LearnerView()
                val document = store.governedLearnerDocument()
                assertEquals(4, document.attempts.size)
                assertEquals(step.itemIds.toSet(), document.attempts.filter { it.correct }.map { it.itemId }.toSet())
                assertTrue(document.steps.isEmpty())
                assertTrue(document.unitCompletions.isEmpty())
                assertTrue(document.reviewAssignments.isEmpty() && document.reviewAnswers.isEmpty())
                assertTrue(stepCompletionItemIds(pack, step.id).isEmpty())
                val role = pack.unit(4)!!.objectives.single { it.id == "obj-u4-nominative" }
                assertTrue(role.checkItemIds.none { it in creditItemIds(pack, learner) })
                assertNotEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS, objectiveState(role, learner, pack))
                assertFalse(advancementDecision(pack, pack.unit(4)!!, learner).allowed)
            }
        } finally {
            V2UiWork.blockForTests = previousBlock
            context.deleteDatabase(name)
            File(context.filesDir, "recordings-$name").deleteRecursively()
        }
    }

    private fun waitTag(tag: String) = compose.waitUntil(30_000) {
        compose.onAllNodesWithTag(tag).fetchSemanticsNodes().isNotEmpty()
    }
    private fun waitText(text: String) = compose.waitUntil(30_000) {
        compose.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()
    }
}
