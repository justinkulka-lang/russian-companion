package dev.russiancompanion.v2

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.curriculum.*
import dev.russiancompanion.v2.ui.*
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** Actual UI regressions, not qualified assessment or physical accessibility acceptance. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class SelectedOptionRationaleUiTest {
    @get:Rule val compose = createComposeRule()

    @Test fun lessonExplainsEachSelectedWrongValueWithoutChangingGradingOrCommonFeedback() {
        val p = Unit8PresentFixtures.pack
        val item = p.item("u7-i-endings-4")!!
        val located = p.units.flatMap { it.sessions }.first { s ->
            s.steps.any { item.id in it.itemIds && it.kind == "CHECK" }
        }
        val index = located.steps.indexOfFirst { item.id in it.itemIds && it.kind == "CHECK" }
        val step = located.steps[index]
        val selected = mutableStateOf<Map<String,String>>(emptyMap())
        LocalStore.openInMemory(context()).use { store ->
            store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = 7))
            compose.setContent {
                V2SessionScreen(p, store.v2LearnerView(), located.id, index, false, selected.value,
                    emptySet(), emptySet(), emptyMap(), {}, { _, _ -> }, { id, value, stepId ->
                        store.v2RecordGovernedAttempt(prepareGovernedAttempt(p, id, value,
                            PRESCRIBED_CHECK, stepId, "rationale-host")!!)
                        selected.value = mapOf(governedSelectionKey(stepId, id) to value)
                        true
                    }, { _, _ -> }, { _, _ -> }, { _, _ -> }, { _, _ -> }, {}, {}, {}, { _, _ -> }, {})
            }
            for (wrong in item.options.filter { it != item.answer }) {
                val rationale = diagnosticRationale(item, wrong)
                val tag = "v2-option-${item.id}-$wrong"
                compose.onNodeWithTag(tag).performScrollTo().performClick()
                compose.onNodeWithTag(tag).assertIsSelected()
                compose.onNodeWithTag("v2-item-feedback-${item.id}")
                    .performScrollTo().assertTextContains(rationale).assertTextContains(item.feedback)
                assertFalse(store.v2Attempts().last().correct)
                assertFalse(item.id in creditItemIds(p, store.v2LearnerView()))
            }
            compose.onNodeWithTag("v2-option-${item.id}-${item.answer}").performScrollTo().performClick()
            compose.onNodeWithTag("v2-item-feedback-${item.id}").performScrollTo().assertTextContains(item.feedback)
            assertTrue(store.v2Attempts().last().correct)
            assertEquals(item.answer, store.v2Attempts().last().selected)
            assertEquals(step.id, store.v2Attempts().last().stepId)
        }
    }

    @Test fun optionalPracticeExplainsTheSubmittedWrongValueWithoutRequiredCredit() {
        val p = Unit8PresentFixtures.pack
        LocalStore.openInMemory(context()).use { store ->
            // A placed current unit is available for optional practice without fabricated earlier credit.
            store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = 7))
            val learner = mutableStateOf(store.v2LearnerView())
            val item = practiceQueue(p, learner.value, "grammar").first()
            val wrong = item.options.first { it != item.answer }
            val rationale = diagnosticRationale(item, wrong)
            compose.setContent {
                V2LabScreen(p, learner.value, "grammar", emptySet(), {}, { id, value ->
                    store.v2RecordGovernedAttempt(prepareGovernedAttempt(p, id, value,
                        OPTIONAL_PRACTICE, practiceStepId("grammar"), "rationale-host")!!)
                    learner.value = store.v2LearnerView()
                    true
                })
            }
            compose.waitUntil(30_000) { compose.onAllNodesWithTag("v2-lab-option-$wrong").fetchSemanticsNodes().isNotEmpty() }
            compose.onNodeWithTag("v2-lab-option-$wrong").performScrollTo().performClick()
            assertTrue(store.v2Attempts().isEmpty())
            compose.onNodeWithTag("v2-lab-submit").performClick()
            compose.onNodeWithTag("v2-lab-feedback").performScrollTo()
                .assertTextContains(rationale).assertTextContains(item.feedback)
            assertEquals(wrong, store.v2Attempts().last().selected)
            assertFalse(store.v2Attempts().last().correct)
            assertTrue(creditItemIds(p, store.v2LearnerView()).isEmpty())
            assertTrue(store.v2StepProgress().isEmpty())
        }
    }

    @Test fun assignedReviewExplainsTheSelectedWrongValueWithoutCompletingItsSlot() {
        val p = Unit8PresentFixtures.pack
        LocalStore.openInMemory(context()).use { store ->
            Unit8PresentFixtures.prepare(store)
            val assignment = store.openCumulativeReview(p, 8, store.learnerMutationEpoch())!!
            val index = assignment.slots.indexOfFirst { it.itemId == "u7-i-endings-4" }
            assertTrue(index >= 0)
            val item = p.item(assignment.slots[index].itemId)!!
            val wrong = item.options.first { it != item.answer }
            val rationale = diagnosticRationale(item, wrong)
            val learner = mutableStateOf(store.v2LearnerView())
            compose.setContent {
                V2CumulativeReviewScreen(p, learner.value, 8, false, {}, {}, {}, { id, slot, value ->
                    assertTrue(store.answerCumulativeReview(p, id, slot, value, store.learnerMutationEpoch()))
                    learner.value = store.v2LearnerView()
                }, {}, {}, {})
            }
            val tag = "v2-review-option-$index-$wrong"
            compose.waitUntil(30_000) { compose.onAllNodesWithTag(tag).fetchSemanticsNodes().isNotEmpty() }
            compose.onNodeWithTag(tag).performScrollTo().performClick()
            compose.onNodeWithTag("v2-review-feedback-$index").performScrollTo()
                .assertTextContains(rationale).assertTextContains(item.feedback)
            assertEquals(wrong, store.cumulativeReviewHistory().second.last().selected)
            assertFalse(index in completedReviewSlots(p, assignment, store.cumulativeReviewHistory().second))
            assertEquals(assignment, store.cumulativeReviewHistory().first.single { it.id == assignment.id })
        }
    }

    @Test fun lessonOptionHasMergedLabelRoleSelectionAndClickSemantics() {
        val p = Unit8PresentFixtures.pack
        val item = p.item("u7-i-endings-4")!!
        val session = p.units.flatMap { it.sessions }.first { s -> s.steps.any { item.id in it.itemIds && it.kind == "CHECK" } }
        val index = session.steps.indexOfFirst { item.id in it.itemIds && it.kind == "CHECK" }
        val selected = mutableStateOf<Map<String,String>>(emptyMap())
        LocalStore.openInMemory(context()).use { store ->
            compose.setContent {
                V2SessionScreen(p, store.v2LearnerView(), session.id, index, false, selected.value,
                    emptySet(), emptySet(), emptyMap(), {}, { _, _ -> }, { id, value, stepId ->
                        selected.value = mapOf(governedSelectionKey(stepId, id) to value); true
                    }, { _, _ -> }, { _, _ -> }, { _, _ -> }, { _, _ -> }, {}, {}, {}, { _, _ -> }, {})
            }
            for (option in item.options) {
                val row = compose.onNodeWithTag("v2-option-${item.id}-$option").performScrollTo()
                val config = row.fetchSemanticsNode().config
                assertEquals(option, config[SemanticsProperties.Text].joinToString("") { it.text })
                assertEquals(Role.RadioButton, config[SemanticsProperties.Role])
                assertFalse(config[SemanticsProperties.Selected])
                assertNotNull(config[SemanticsActions.OnClick].action)
            }
            compose.onNodeWithTag("v2-option-${item.id}-${item.answer}").performScrollTo().performClick().assertIsSelected()
            assertTrue(store.v2Attempts().isEmpty())
        }
    }

    private fun diagnosticRationale(item: V2CheckItem, wrong: String): String {
        val rationale = item.distractorRationales[item.options.indexOf(wrong)]
        assertTrue("Fixture needs a distinct existing wrong-option explanation", rationale.isNotBlank() && rationale !in item.feedback)
        return rationale
    }
    private fun context(): Context = ApplicationProvider.getApplicationContext()
}
