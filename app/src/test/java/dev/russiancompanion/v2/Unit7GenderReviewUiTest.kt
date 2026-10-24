package dev.russiancompanion.v2

import androidx.compose.runtime.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.backup.*
import dev.russiancompanion.v2.curriculum.*
import dev.russiancompanion.v2.ui.*
import dev.russiancompanion.v2.Unit7GenderFixtures.context
import dev.russiancompanion.v2.Unit7GenderFixtures.prepare
import dev.russiancompanion.v2.Unit7GenderFixtures.pack
import dev.russiancompanion.v2.Unit7GenderFixtures.document
import dev.russiancompanion.v2.Unit7GenderFixtures.v1Capture
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class Unit7GenderReviewUiTest {
    @get:Rule val compose = createComposeRule()

    @Test fun unpreparedReviewStatesFiniteScopeAndRoutesToBothRealOwnersWithoutCredit() {
        LocalStore.openInMemory(context()).use { store ->
            var requested = 0
            val before = store.governedLearnerDocument()
            compose.setContent { V2CumulativeReviewScreen(pack, store.v2LearnerView(), 7, false, {}, {}, {}, { _, _, _ -> }, { requested = it }, {}, {}) }
            waitTag("v2-review-insufficient")
            compose.onNodeWithText(newTitle).assertExists()
            compose.onNodeWithText("These finite recognitions do not demonstrate unseen reading", substring = true).performScrollTo().assertIsDisplayed()
            compose.onNodeWithTag("v2-review-start").assertDoesNotExist()
            compose.onNodeWithTag("v2-review-repair").performScrollTo().assertTextEquals("Open earlier Unit 3 instruction and checks").performClick()
            assertEquals(3, requested)
            compose.onNodeWithTag("v2-review-repair-6").performScrollTo().assertTextEquals("Open earlier Unit 6 instruction and checks").performClick()
            assertEquals(6, requested)
            assertEquals(before, store.governedLearnerDocument())
        }
    }

    @Test fun freshUnitDetailEntryAndUnstartedScreenUseTheDeclaredGenderPolicy() {
        LocalStore.openInMemory(context()).use { store ->
            prepare(store)
            val before = store.governedLearnerDocument()
            var review by mutableStateOf(false)
            compose.setContent {
                if (review) V2CumulativeReviewScreen(pack, store.v2LearnerView(), 7, false, {}, {}, {}, { _, _, _ -> }, {}, {}, {})
                else V2UnitScreen(pack, store.v2LearnerView(), 7, {}, {}, {}, {}, {}, { review = true })
            }
            compose.onNodeWithTag("v2-open-cumulative-review").performScrollTo().assertTextContains(newTitle).performClick()
            waitTag("v2-review-start")
            compose.onNodeWithText(newTitle).assertExists()
            assertEquals(before, store.governedLearnerDocument())
        }
    }

    @Test fun actualFreshReviewRepairsGenderTeachingWithoutPracticeCreditAndAdvances() {
        LocalStore.openInMemory(context()).use { store ->
            prepare(store)
            val item = pack.item("u3-i-gender-дверь")!!
            val step = pack.unit(3)!!.sessions.flatMap { it.steps }.single { it.kind == "CHECK" && item.id in it.itemIds }
            store.v2RecordGovernedAttempt(prepareGovernedAttempt(pack, item.id, item.options.first { it != item.answer }, PRESCRIBED_CHECK, step.id, "ui-miss")!!)
            assertEquals(newTitle, continueTarget(pack, store.v2LearnerView()).title)
            compose.setContent { V2App(store, pack, {}) }
            waitTag("v2-continue"); compose.onNodeWithTag("v2-continue").performClick()
            waitTag("v2-review-start"); compose.onNodeWithTag("v2-review-start").performScrollTo().performClick()
            waitTag("v2-review-count")
            val task = store.cumulativeReviewHistory().first.single { it.targetUnit == 7 }
            assertEquals(9, task.schema); assertEquals(item.id, task.slots[0].itemId)
            assertEquals(2, task.slots.size)
            val options = reviewPresentedOptions(item, task, 0)
            assertEquals(options, reviewPresentedOptions(item.copy(answer = item.options.first { it != item.answer }), task, 0))
            val beforeTags = compose.onAllNodes(hasTestTagPrefix("v2-review-option-0-")).fetchSemanticsNodes().map { it.id }
            assertEquals(3, beforeTags.size)
            val wrong = item.options.first { it != item.answer }
            compose.onNodeWithTag("v2-review-option-0-$wrong").performScrollTo().performClick()
            waitTag("v2-review-feedback-0")
            compose.onNodeWithTag("v2-review-feedback-0").assertTextEquals("Not the reviewed answer. ${item.feedback}")
            compose.onNodeWithTag("v2-review-remediate-0").performScrollTo().performClick()
            waitTag("v2-review-practice")
            val repair = pack.step("u3-st-rem-gender")!!.second.body
            compose.onNodeWithText(repair).assertExists()
            assertTrue(repair.contains("дверь") && repair.contains("дя́дя"))
            compose.onNodeWithTag("v2-review-practice").performClick()
            val practice = reviewedGuidedPractice(pack, "u3-st-rem-gender")
            assertTrue(practice.isNotEmpty())
            practice.forEachIndexed { index, sid ->
                pack.step(sid)!!.second.itemIds.forEach { id ->
                    val guided = pack.item(id)!!
                    waitTag("v2-option-$id-${guided.answer}")
                    compose.onNodeWithTag("v2-option-$id-${guided.answer}").performScrollTo().performClick()
                }
                if (index < practice.lastIndex) {
                    waitTag("v2-next-practice"); compose.onNodeWithTag("v2-next-practice").performClick()
                }
            }
            waitTag("v2-return-to-check"); compose.onNodeWithTag("v2-return-to-check").performClick()
            waitTag("v2-review-count")
            assertEquals(task, store.cumulativeReviewHistory().first.single { it.targetUnit == 7 })
            assertTrue(completedReviewSlots(pack, task, store.v2LearnerView().reviewAnswers).isEmpty())
            assertEquals(options, reviewPresentedOptions(item, task, 0))
            compose.onNodeWithTag("v2-review-count").assertTextEquals("0 of 2 review checks recorded")
            task.slots.forEachIndexed { index, slot ->
                val answer = pack.item(slot.itemId)!!.answer
                compose.onNodeWithTag("v2-review-option-$index-$answer").performScrollTo().performClick()
                compose.waitUntil(30_000) { index in completedReviewSlots(pack, task, store.v2LearnerView().reviewAnswers) }
            }
            waitTag("v2-review-gate"); compose.onNodeWithTag("v2-review-gate").performScrollTo().performClick()
            waitTag("v2-advance"); compose.onNodeWithTag("v2-advance").performScrollTo().assertIsEnabled().performClick()
            compose.waitUntil(30_000) { store.governedLearnerDocument().unitCompletions.any { it.unitNumber == 7 && it.reviewAssignmentId == task.id } }
            assertEquals(8, store.v2Profile().currentUnitNumber)
        }
    }

    @Test fun genuineSchemaOneEntryContinueAndReviewKeepTheirOwnPartialMeaning() {
        legacyEntry(v1Capture, false, 2)
    }

    private fun legacyEntry(path: String, reteach: Boolean, slots: Int) {
        val original = document(path)
        LocalStore.openInMemory(context()).use { store ->
            assertEquals(GovernedReplaceOutcome.REPLACED, store.replaceGovernedLearner(original, emptyList()))
            if (reteach) prepare(store, 5)
            val before = store.governedLearnerDocument()
            val saved = before.reviewAssignments.single { it.targetUnit == 7 }
            assertTrue(reviewAssignmentCurrent(pack, saved))
            assertEquals("Review earlier reading and supplied object forms", continueTarget(pack, store.v2LearnerView()).title)
            var review by mutableStateOf(false)
            compose.setContent {
                if (review) V2CumulativeReviewScreen(pack, store.v2LearnerView(), 7, false, {}, {}, {}, { _, _, _ -> }, {}, {}, {})
                else V2UnitScreen(pack, store.v2LearnerView(), 7, {}, {}, {}, {}, {}, { review = true })
            }
            compose.onNodeWithTag("v2-open-cumulative-review").performScrollTo().assertTextContains("Review earlier reading and supplied object forms").performClick()
            waitTag("v2-review-count")
            compose.onNodeWithText("Review earlier reading and supplied object forms").assertExists()
            compose.onNodeWithText("Review earlier print-pair matching", substring = true).assertExists()
            compose.onNodeWithTag("v2-review-count").assertTextEquals("1 of $slots review checks recorded")
            assertEquals(before, store.governedLearnerDocument())
            assertEquals(saved, currentCumulativeAssignment(pack, store.v2LearnerView(), 7))
        }
    }
    private fun hasTestTagPrefix(prefix: String) = SemanticsMatcher("tag starts with $prefix") {
        it.config.getOrElse(androidx.compose.ui.semantics.SemanticsProperties.TestTag) { "" }.startsWith(prefix)
    }
    private fun waitTag(tag: String) = compose.waitUntil(conditionDescription = tag, timeoutMillis = 30_000) {
        compose.onAllNodesWithTag(tag).fetchSemanticsNodes().isNotEmpty()
    }
    companion object { private const val newTitle = "Review noun genders and supplied object classification" }
}
