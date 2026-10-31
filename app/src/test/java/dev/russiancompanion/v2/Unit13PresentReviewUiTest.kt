package dev.russiancompanion.v2

import androidx.compose.runtime.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.backup.*
import dev.russiancompanion.v2.curriculum.*
import dev.russiancompanion.v2.ui.*
import dev.russiancompanion.v2.Unit13PresentFixtures.context
import dev.russiancompanion.v2.Unit13PresentFixtures.prepare
import dev.russiancompanion.v2.Unit13PresentFixtures.pack
import dev.russiancompanion.v2.Unit13PresentFixtures.document
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class Unit13PresentReviewUiTest {
    @get:Rule val compose = createComposeRule()

    @Test fun unpreparedReviewStatesFiniteScopeAndRoutesToAllThreeRealOwnersWithoutCredit() {
        LocalStore.openInMemory(context()).use { store ->
            var requested = 0
            val before = store.governedLearnerDocument()
            compose.setContent { V2CumulativeReviewScreen(pack, store.v2LearnerView(), 13, false, {}, {}, {}, { _, _, _ -> }, { requested = it }, {}, {}) }
            waitTag("v2-review-insufficient")
            compose.onNodeWithText(newTitle).assertExists()
            compose.onNodeWithText("These finite recognitions do not demonstrate unseen reading", substring = true).performScrollTo().assertIsDisplayed()
            compose.onNodeWithTag("v2-review-start").assertDoesNotExist()
            compose.onNodeWithTag("v2-review-repair").performScrollTo().assertTextEquals("Open earlier Unit 4 instruction and checks").performClick()
            assertEquals(4, requested)
            compose.onNodeWithTag("v2-review-repair-12").performScrollTo().assertTextEquals("Open earlier Unit 12 instruction and checks").performClick()
            assertEquals(12, requested)
            compose.onNodeWithTag("v2-review-repair-11").performScrollTo().assertTextEquals("Open earlier Unit 11 instruction and checks").performClick()
            assertEquals(11, requested)
            assertEquals(before, store.governedLearnerDocument())
        }
    }

    @Test fun freshUnitDetailEntryAndUnstartedScreenUseTheDeclaredPresentPolicy() {
        LocalStore.openInMemory(context()).use { store ->
            prepare(store)
            val before = store.governedLearnerDocument()
            var review by mutableStateOf(false)
            compose.setContent {
                if (review) V2CumulativeReviewScreen(pack, store.v2LearnerView(), 13, false, {}, {}, {}, { _, _, _ -> }, {}, {}, {})
                else V2UnitScreen(pack, store.v2LearnerView(), 13, {}, {}, {}, {}, {}, { review = true })
            }
            compose.onNodeWithTag("v2-open-cumulative-review").performScrollTo().assertTextContains(newTitle).performClick()
            waitTag("v2-review-start")
            compose.onNodeWithText(newTitle).assertExists()
            assertEquals(before, store.governedLearnerDocument())
        }
    }

    @Test fun actualFreshReviewRepairsPresentTeachingWithoutPracticeCreditAndAdvances() =
        freshRepair(0, 4, "u4-i-verb-писать-вы", "u4-st-rem-present", 11, 6)

    @Test fun actualFreshReviewRepairsAspectTeachingWithoutPracticeCreditAndAdvances() =
        freshRepair(1, 12, "u12-i-aspect-2", "u12-st-rem-aspect", 3, 4)

    @Test fun actualFreshReviewRepairsPastGenderTeachingWithoutPracticeCreditAndAdvances() =
        freshRepair(2, 11, "u11-i-past-aspect-2", "u11-st-rem-past-aspect", 4, 3)

    private fun freshRepair(slotIndex: Int, owner: Int, itemId: String, repairId: String, cards: Int, examples: Int) {
        LocalStore.openInMemory(context()).use { store ->
            prepare(store)
            val item = pack.item(itemId)!!
            val step = pack.unit(owner)!!.sessions.flatMap { it.steps }.single { it.kind == "CHECK" && item.id in it.itemIds }
            store.v2RecordGovernedAttempt(prepareGovernedAttempt(pack, item.id, item.options.first { it != item.answer }, PRESCRIBED_CHECK, step.id, "ui-miss")!!)
            assertEquals(newTitle, continueTarget(pack, store.v2LearnerView()).title)
            compose.setContent { V2App(store, pack, {}) }
            waitTag("v2-continue"); compose.onNodeWithTag("v2-continue").performClick()
            waitTag("v2-review-start"); compose.onNodeWithTag("v2-review-start").performScrollTo().performClick()
            waitTag("v2-review-count")
            val task = store.cumulativeReviewHistory().first.single { it.targetUnit == 13 }
            assertEquals(1, task.schema); assertEquals(item.id, task.slots[slotIndex].itemId)
            assertEquals(3, task.slots.size)
            val options = reviewPresentedOptions(item, task, slotIndex)
            assertEquals(options, reviewPresentedOptions(item.copy(answer = item.options.first { it != item.answer }), task, slotIndex))
            val beforeTags = compose.onAllNodes(hasTestTagPrefix("v2-review-option-$slotIndex-")).fetchSemanticsNodes().map { it.id }
            assertEquals(item.options.size, beforeTags.size)
            val wrong = item.options.first { it != item.answer }
            compose.onNodeWithTag("v2-review-option-$slotIndex-$wrong").performScrollTo().performClick()
            waitTag("v2-review-feedback-$slotIndex")
            compose.onNodeWithTag("v2-review-feedback-$slotIndex").assertTextEquals("Not the reviewed answer. ${item.feedback}")
            compose.onNodeWithTag("v2-review-remediate-$slotIndex").performScrollTo().performClick()
            waitTag("v2-review-practice")
            val repair = pack.step(repairId)!!.second.body
            compose.onNodeWithText(repair).assertExists()
            assertTrue(repair.isNotBlank())
            val objective = pack.unit(owner)!!.objectives.single { it.id == task.slots[slotIndex].objectiveId }
            val teaching = objective.instructionStepIds.map { pack.step(it)!!.second }.filter { it.kind == "TEACH" }
            assertEquals(cards, teaching.size)
            assertTrue(teaching.all { repair.contains(it.body) })
            val remediation = pack.step(repairId)!!.second
            assertEquals(examples, remediation.exampleIds.size)
            assertEquals(teaching.flatMap { it.exampleIds }.toSet(), remediation.exampleIds.toSet())
            compose.onNodeWithTag("v2-review-practice").performClick()
            val practice = reviewedGuidedPractice(pack, repairId)
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
            assertEquals(task, store.cumulativeReviewHistory().first.single { it.targetUnit == 13 })
            assertTrue(completedReviewSlots(pack, task, store.v2LearnerView().reviewAnswers).isEmpty())
            assertEquals(options, reviewPresentedOptions(item, task, slotIndex))
            compose.onNodeWithTag("v2-review-count").assertTextEquals("0 of 3 review checks recorded")
            task.slots.forEachIndexed { index, slot ->
                val answer = pack.item(slot.itemId)!!.answer
                compose.onNodeWithTag("v2-review-option-$index-$answer").performScrollTo().performClick()
                compose.waitUntil(30_000) { index in completedReviewSlots(pack, task, store.v2LearnerView().reviewAnswers) }
            }
            waitTag("v2-review-gate"); compose.onNodeWithTag("v2-review-gate").performScrollTo().performClick()
            waitTag("v2-advance"); compose.onNodeWithTag("v2-advance").performScrollTo().assertIsEnabled().performClick()
            compose.waitUntil(30_000) { store.governedLearnerDocument().unitCompletions.any { it.unitNumber == 13 && it.reviewAssignmentId == task.id } }
            assertEquals(14, store.v2Profile().currentUnitNumber)
        }
    }



    @Test fun genuinelyCompletedUnit4RoutesMissingUnit12And11WithoutCreatingReviewCredit() {
        LocalStore.openInMemory(context()).use { store ->
            prepare(store, 4)
            assertTrue(4 in currentCompletedUnitNumbers(pack, store.v2LearnerView()))
            assertFalse(11 in currentCompletedUnitNumbers(pack, store.v2LearnerView()))
            store.v2SaveProfile(store.v2Profile().copy(currentUnitNumber = 13))
            val before = store.governedLearnerDocument()
            var requested = 0
            compose.setContent { V2CumulativeReviewScreen(pack, store.v2LearnerView(), 13, false, {}, {}, {}, { _, _, _ -> }, { requested = it }, {}, {}) }
            waitTag("v2-review-insufficient")
            compose.onNodeWithTag("v2-review-start").assertDoesNotExist()
            compose.onNodeWithTag("v2-review-repair").performScrollTo().assertTextEquals("Open earlier Unit 12 instruction and checks").performClick()
            assertEquals(12, requested)
            compose.onNodeWithTag("v2-review-repair-11").performScrollTo().assertTextEquals("Open earlier Unit 11 instruction and checks").performClick()
            assertEquals(11, requested)
            assertEquals(before, store.governedLearnerDocument())
            assertFalse(store.cumulativeReviewHistory().first.any { it.targetUnit == 13 })
        }
    }

    private fun legacyEntry(path: String, reteach: Boolean, slots: Int) {
        val original = document(path)
        LocalStore.openInMemory(context()).use { store ->
            assertEquals(GovernedReplaceOutcome.REPLACED, store.replaceGovernedLearner(original, emptyList()))
            if (reteach) prepare(store)
            val before = store.governedLearnerDocument()
            val saved = before.reviewAssignments.single { it.targetUnit == 13 }
            assertTrue(reviewAssignmentCurrent(pack, saved))
            assertEquals("Review earlier reading, supplied aspect partners and past agreement", continueTarget(pack, store.v2LearnerView()).title)
            var review by mutableStateOf(false)
            compose.setContent {
                if (review) V2CumulativeReviewScreen(pack, store.v2LearnerView(), 13, false, {}, {}, {}, { _, _, _ -> }, {}, {}, {})
                else V2UnitScreen(pack, store.v2LearnerView(), 13, {}, {}, {}, {}, {}, { review = true })
            }
            compose.onNodeWithTag("v2-open-cumulative-review").performScrollTo().assertTextContains("Review earlier reading, supplied aspect partners and past agreement").performClick()
            waitTag("v2-review-count")
            compose.onNodeWithText("Review earlier reading, supplied aspect partners and past agreement").assertExists()
            compose.onNodeWithText("Review earlier printed letter pairs", substring = true).assertExists()
            compose.onNodeWithTag("v2-review-count").assertTextEquals("1 of $slots review checks recorded")
            assertEquals(before, store.governedLearnerDocument())
            assertEquals(saved, currentCumulativeAssignment(pack, store.v2LearnerView(), 13))
        }
    }
    private fun hasTestTagPrefix(prefix: String) = SemanticsMatcher("tag starts with $prefix") {
        it.config.getOrElse(androidx.compose.ui.semantics.SemanticsProperties.TestTag) { "" }.startsWith(prefix)
    }
    private fun waitTag(tag: String) = compose.waitUntil(conditionDescription = tag, timeoutMillis = 30_000) {
        compose.onAllNodesWithTag(tag).fetchSemanticsNodes().isNotEmpty()
    }
    companion object { private const val newTitle = "Review supplied present forms, aspect partners and past agreement" }
}
