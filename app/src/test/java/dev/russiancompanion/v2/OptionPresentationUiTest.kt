package dev.russiancompanion.v2

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.junit4.StateRestorationTester
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

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class OptionPresentationUiTest {
    @get:Rule val compose = createComposeRule()

    @Test fun lessonRendersPermutedOrderAndKeepsValueSelectionAfterStateRestore() {
        val p = OptionPresentationFixture.pack
        val session = p.unit(1)!!.sessions.first { s -> s.steps.any { step -> step.itemIds.any { id ->
            val i = p.item(id)!!; lessonPresentedOptions(i, step.id) != i.options
        } } }
        val index = session.steps.indexOfFirst { step -> step.itemIds.any { id ->
            val i = p.item(id)!!; lessonPresentedOptions(i, step.id) != i.options
        } }
        val step = session.steps[index]
        val item = p.item(step.itemIds.first())!!
        val selections = mutableStateOf<Map<String, String>>(emptyMap())
        LocalStore.openInMemory(context()).use { store ->
            val restore = StateRestorationTester(compose)
            restore.setContent {
                V2SessionScreen(p, store.v2LearnerView(), session.id, index, false, selections.value,
                    emptySet(), emptySet(), emptyMap(), {}, { _, _ -> }, { id, value, stepId ->
                        val w = prepareGovernedAttempt(p, id, value, PRESCRIBED_CHECK, stepId, "fixed")!!
                        store.v2RecordGovernedAttempt(w)
                        selections.value = mapOf(governedSelectionKey(stepId, id) to value)
                        true
                    }, { _, _ -> }, { _, _ -> }, { _, _ -> }, { _, _ -> }, {}, {}, {}, { _, _ -> }, {})
            }
            val prefix = "v2-option-${item.id}-"
            assertEquals(lessonPresentedOptions(item, step.id), displayed(prefix))
            val wrong = item.options.first { it != item.answer }
            compose.onNodeWithTag(prefix + wrong).performScrollTo().performClick()
            compose.onNodeWithTag(prefix + wrong).assertIsSelected()
            assertFalse(store.v2Attempts().last().correct)
            restore.emulateSavedInstanceStateRestore()
            assertEquals(lessonPresentedOptions(item, step.id), displayed(prefix))
            compose.onNodeWithTag(prefix + wrong).performScrollTo().assertIsSelected()
            compose.onNodeWithTag(prefix + item.answer).performScrollTo().performClick()
            assertTrue(store.v2Attempts().last().correct)
        }
    }

    @Test fun labRendersPermutedOrderAndRetainsUnsubmittedSelectionAcrossStateRestore() {
        val p = OptionPresentationFixture.pack
        LocalStore.openInMemory(context()).use { store ->
            store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = 8,
                startingPoint = "RESUME_FROM_BOOK"))
            val learner = store.v2LearnerView()
            val lab = "grammar"
            val item = practiceQueue(p, learner, lab).first()
            val restore = StateRestorationTester(compose)
            restore.setContent { V2LabScreen(p, learner, lab, emptySet(), {}, { _, _ -> true }) }
            compose.waitUntil(30_000) { displayed("v2-lab-option-").isNotEmpty() }
            val before = displayed("v2-lab-option-")
            assertEquals(labPresentedOptions(item, lab), before)
            val option = item.options.first { it != item.answer }
            compose.onNodeWithTag("v2-lab-option-$option").performScrollTo().performClick()
            restore.emulateSavedInstanceStateRestore()
            compose.waitUntil(30_000) { displayed("v2-lab-option-").isNotEmpty() }
            assertEquals(before, displayed("v2-lab-option-"))
            compose.onNodeWithTag("v2-lab-option-$option").performScrollTo().assertIsSelected()
            assertTrue(store.v2Attempts().isEmpty())
        }
    }

    @Test fun newReviewRendersVersionedOrderAndRecordsClickedOptionValues() = review(false)
    @Test fun existingLegacyReviewKeepsAuthoredOrderAndClickedOptionMeaning() = review(true)

    private fun review(legacy: Boolean) {
        val p = OptionPresentationFixture.pack
        LocalStore.openInMemory(context()).use { store ->
            OptionPresentationFixture.prepare(store)
            val fresh = store.openCumulativeReview(p, 3, store.learnerMutationEpoch())!!
            val a = if (!legacy) fresh else fresh.copy(responseRuleDigest = closedResponseRuleDigest())
                .let { it.copy(digest = reviewAssignmentDigest(it)) }
            if (legacy) {
                val doc = store.governedLearnerDocument().copy(reviewAssignments = listOf(a))
                assertEquals(dev.russiancompanion.v2.backup.GovernedReplaceOutcome.REPLACED,
                    store.replaceGovernedLearner(doc, emptyList()))
            }
            val learner = mutableStateOf(store.v2LearnerView())
            val restore = StateRestorationTester(compose)
            restore.setContent { V2CumulativeReviewScreen(p, learner.value, 3, false, {}, {}, {},
                { id, index, value ->
                    assertTrue(store.answerCumulativeReview(p, id, index, value, store.learnerMutationEpoch()))
                    learner.value = store.v2LearnerView()
                }, {}, {}, {}) }
            compose.waitUntil(30_000) { displayed("v2-review-option-0-").isNotEmpty() }
            val item = p.item(a.slots[0].itemId)!!
            val order = displayed("v2-review-option-0-")
            assertEquals(if (legacy) item.options else reviewPresentedOptions(item, a, 0), order)
            val wrong = item.options.first { it != item.answer }
            compose.onNodeWithTag("v2-review-option-0-$wrong").performScrollTo().performClick()
            compose.waitForIdle()
            assertEquals(wrong, store.cumulativeReviewHistory().second.last().selected)
            assertFalse(0 in completedReviewSlots(p, a, store.cumulativeReviewHistory().second))
            restore.emulateSavedInstanceStateRestore()
            compose.waitUntil(30_000) { displayed("v2-review-option-0-").isNotEmpty() }
            assertEquals(order, displayed("v2-review-option-0-"))
            compose.onNodeWithTag("v2-review-option-0-${item.answer}").performScrollTo().performClick()
            compose.waitForIdle()
            assertTrue(0 in completedReviewSlots(p, a, store.cumulativeReviewHistory().second))
            assertEquals(a, store.openCumulativeReview(p, 3, store.learnerMutationEpoch()))
        }
    }

    private fun displayed(prefix: String): List<String> = compose.onAllNodes(SemanticsMatcher("option prefix") {
        it.config.getOrNull(SemanticsProperties.TestTag)?.startsWith(prefix) == true
    }).fetchSemanticsNodes().map { it.config[SemanticsProperties.TestTag].removePrefix(prefix) }
    private fun context(): Context = ApplicationProvider.getApplicationContext()
}
