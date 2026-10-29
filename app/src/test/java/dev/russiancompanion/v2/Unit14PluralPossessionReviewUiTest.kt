package dev.russiancompanion.v2

import androidx.compose.runtime.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.backup.*
import dev.russiancompanion.v2.curriculum.*
import dev.russiancompanion.v2.ui.*
import dev.russiancompanion.v2.Unit14PluralPossessionFixtures.context
import dev.russiancompanion.v2.Unit14PluralPossessionFixtures.pack
import dev.russiancompanion.v2.Unit14PluralPossessionFixtures.prepare
import dev.russiancompanion.v2.Unit14PluralPossessionFixtures.document
import dev.russiancompanion.v2.Unit14PluralPossessionFixtures.partial14
import dev.russiancompanion.v2.Unit14PluralPossessionFixtures.completed14
import dev.russiancompanion.v2.Unit14PluralPossessionFixtures.ordinary
import dev.russiancompanion.v2.Unit14PluralPossessionFixtures.answer
import dev.russiancompanion.v2.Unit14PluralPossessionFixtures.advance
import dev.russiancompanion.v2.Unit14PluralPossessionFixtures.miss
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class Unit14PluralPossessionReviewUiTest {
    @get:Rule val compose=createComposeRule()

    @Test fun unpreparedFiniteReviewRoutesToAllThreeOwnersWithoutCredit() {
        LocalStore.openInMemory(context()).use { store ->
            var requested=0;val before=store.governedLearnerDocument()
            compose.setContent { V2CumulativeReviewScreen(pack,store.v2LearnerView(),14,false,{},{},{},{_,_,_->},{requested=it},{},{}) }
            waitTag("v2-review-insufficient");compose.onNodeWithText(newTitle).assertExists()
            compose.onNodeWithText("These finite recognitions do not demonstrate unseen reading",substring=true).performScrollTo().assertIsDisplayed()
            compose.onNodeWithTag("v2-review-start").assertDoesNotExist()
            for(owner in listOf(8,10,13)) {
                val tag=if(owner==8) "v2-review-repair" else "v2-review-repair-$owner"
                compose.onNodeWithTag(tag).performScrollTo().assertTextEquals("Open earlier Unit $owner instruction and checks").performClick()
                assertEquals(owner,requested)
            }
            assertEquals(before,store.governedLearnerDocument())
        }
    }

    @Test fun freshDetailEntryAndUnstartedReviewUseDeclaredPluralPossessionTitle() {
        LocalStore.openInMemory(context()).use { store ->
            prepare(store);val before=store.governedLearnerDocument();var review by mutableStateOf(false)
            compose.setContent {
                if(review) V2CumulativeReviewScreen(pack,store.v2LearnerView(),14,false,{},{},{},{_,_,_->},{},{},{})
                else V2UnitScreen(pack,store.v2LearnerView(),14,{},{},{},{},{},{review=true})
            }
            compose.onNodeWithTag("v2-open-cumulative-review").performScrollTo().assertTextContains(newTitle).performClick()
            waitTag("v2-review-start");compose.onNodeWithText(newTitle).assertExists()
            assertEquals(before,store.governedLearnerDocument())
        }
    }

    @Test fun actualPluralRepairRestoresAllTeachingAndReturnsWithoutDueCreditThenAdvances() =
        freshRepair(0,8,"u8-i-plural-6","u8-st-rem-plural",8,14)

    @Test fun actualPossessionRepairRestoresAllTeachingAndReturnsWithoutDueCreditThenAdvances() =
        freshRepair(1,10,"u10-i-have-4","u10-st-rem-have",5,7)

    @Test fun actualReportedTimeRepairRestoresAllTeachingAndReturnsWithoutDueCreditThenAdvances() =
        freshRepair(2,13,"u13-i-report-frame-time","u13-st-rem-reported",3,4)

    private fun freshRepair(slotIndex:Int,owner:Int,itemId:String,repairId:String,cards:Int,examples:Int) {
        LocalStore.openInMemory(context()).use { store ->
            prepare(store);miss(store,itemId)
            assertEquals(newTitle,continueTarget(pack,store.v2LearnerView()).title)
            compose.setContent { V2App(store,pack,{}) }
            waitTag("v2-continue");compose.onNodeWithTag("v2-continue").performClick()
            waitTag("v2-review-start");compose.onNodeWithTag("v2-review-start").performScrollTo().performClick()
            waitTag("v2-review-count")
            val task=store.cumulativeReviewHistory().first.single { it.targetUnit==14 };val item=pack.item(itemId)!!
            assertEquals(16,task.schema);assertEquals(3,task.slots.size);assertEquals(itemId,task.slots[slotIndex].itemId)
            val options=reviewPresentedOptions(item,task,slotIndex)
            assertEquals(options,reviewPresentedOptions(item.copy(answer=item.options.first { it!=item.answer }),task,slotIndex))
            assertEquals(item.options.size,compose.onAllNodes(hasTestTagPrefix("v2-review-option-$slotIndex-")).fetchSemanticsNodes().size)
            val wrong=item.options.first { it!=item.answer }
            compose.onNodeWithTag("v2-review-option-$slotIndex-$wrong").performScrollTo().performClick()
            waitTag("v2-review-feedback-$slotIndex")
            compose.onNodeWithTag("v2-review-feedback-$slotIndex").assertTextEquals("Not the reviewed answer. ${item.feedback}")
            compose.onNodeWithTag("v2-review-remediate-$slotIndex").performScrollTo().performClick()
            waitTag("v2-review-practice")
            val remediation=pack.step(repairId)!!.second;val repair=remediation.body
            compose.onNodeWithText(repair).assertExists()
            val objective=pack.unit(owner)!!.objectives.single { it.id==task.slots[slotIndex].objectiveId }
            val teaching=objective.instructionStepIds.map { pack.step(it)!!.second }.filter { it.kind=="TEACH" }
            assertEquals(cards,teaching.size);assertTrue(teaching.all { repair.contains(it.body) })
            assertEquals(examples,remediation.exampleIds.size)
            assertEquals(teaching.flatMap { it.exampleIds }.toSet(),remediation.exampleIds.toSet())
            compose.onNodeWithTag("v2-review-practice").performClick()
            val practice=reviewedGuidedPractice(pack,repairId);assertEquals(1,practice.size)
            for(sid in practice) for(id in pack.step(sid)!!.second.itemIds) {
                val guided=pack.item(id)!!
                waitTag("v2-option-$id-${guided.answer}")
                compose.onNodeWithTag("v2-option-$id-${guided.answer}").performScrollTo().performClick()
            }
            waitTag("v2-return-to-check");compose.onNodeWithTag("v2-return-to-check").performClick()
            waitTag("v2-review-count")
            assertEquals(task,store.cumulativeReviewHistory().first.single { it.targetUnit==14 })
            assertTrue(completedReviewSlots(pack,task,store.v2LearnerView().reviewAnswers).isEmpty())
            assertEquals(options,reviewPresentedOptions(item,task,slotIndex))
            compose.onNodeWithTag("v2-review-count").assertTextEquals("0 of 3 review checks recorded")
            for((index,slot) in task.slots.withIndex()) {
                compose.onNodeWithTag("v2-review-option-$index-${pack.item(slot.itemId)!!.answer}").performScrollTo().performClick()
                compose.waitUntil(30_000) { index in completedReviewSlots(pack,task,store.v2LearnerView().reviewAnswers) }
            }
            waitTag("v2-review-gate");compose.onNodeWithTag("v2-review-gate").performScrollTo().performClick()
            waitTag("v2-advance");compose.onNodeWithTag("v2-advance").performScrollTo().assertIsEnabled().performClick()
            compose.waitUntil(30_000) { store.governedLearnerDocument().unitCompletions.any { it.unitNumber==14 && it.reviewAssignmentId==task.id } }
            assertEquals(15,store.v2Profile().currentUnitNumber)
        }
    }

    @Test fun genuinePartialV1EntryAndContinueRetainOldTitleAndOneOfTwoAnswers() = legacyEntry(partial14,1)

    @Test fun genuineCompletedV1DetailRetainsOldTitleAndTwoOfTwoAnswers() = legacyEntry(completed14,2)

    private fun legacyEntry(path:String,count:Int) {
        val original=document(path)
        LocalStore.openInMemory(context()).use { store ->
            assertEquals(GovernedReplaceOutcome.REPLACED,store.replaceGovernedLearner(original,emptyList()))
            val task=original.reviewAssignments.single { it.targetUnit==14 }
            assertTrue(reviewAssignmentCurrent(pack,task));assertEquals(1,task.schema)
            if(count==1) assertEquals(oldTitle,continueTarget(pack,store.v2LearnerView()).title)
            var review by mutableStateOf(false)
            compose.setContent {
                if(review) V2CumulativeReviewScreen(pack,store.v2LearnerView(),14,false,{},{},{},{_,_,_->},{},{},{})
                else V2UnitScreen(pack,store.v2LearnerView(),14,{},{},{},{},{},{review=true})
            }
            compose.onNodeWithTag("v2-open-cumulative-review").performScrollTo().assertTextContains(oldTitle).performClick()
            waitTag("v2-review-count");compose.onNodeWithText(oldTitle).assertExists()
            compose.onNodeWithText("Review earlier printed letter pairs",substring=true).assertExists()
            compose.onNodeWithTag("v2-review-count").assertTextEquals("$count of 2 review checks recorded")
            assertEquals(original,store.governedLearnerDocument());assertEquals(task,currentCumulativeAssignment(pack,store.v2LearnerView(),14))
        }
    }

    @Test fun actuallyCompletedUnit8DoesNotHideMissingUnit10And13OrCreateReviewCredit() {
        LocalStore.openInMemory(context()).use { store ->
            store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted=true))
            for(n in 1..8) {
                ordinary(store,n)
                if(n>=3) {
                    val task=store.openCumulativeReview(pack,n,store.learnerMutationEpoch())!!
                    task.slots.indices.forEach { answer(store,task,it) }
                }
                assertTrue(advance(store,n))
            }
            assertTrue(8 in currentCompletedUnitNumbers(pack,store.v2LearnerView()))
            assertFalse(10 in currentCompletedUnitNumbers(pack,store.v2LearnerView()))
            store.v2SaveProfile(store.v2Profile().copy(currentUnitNumber=14)) // Placement alone earns no omitted work.
            val before=store.governedLearnerDocument();var requested=0
            compose.setContent { V2CumulativeReviewScreen(pack,store.v2LearnerView(),14,false,{},{},{},{_,_,_->},{requested=it},{},{}) }
            waitTag("v2-review-insufficient");compose.onNodeWithTag("v2-review-start").assertDoesNotExist()
            compose.onNodeWithTag("v2-review-repair").performScrollTo().assertTextEquals("Open earlier Unit 10 instruction and checks").performClick()
            assertEquals(10,requested)
            compose.onNodeWithTag("v2-review-repair-13").performScrollTo().assertTextEquals("Open earlier Unit 13 instruction and checks").performClick()
            assertEquals(13,requested);assertEquals(before,store.governedLearnerDocument())
            assertFalse(store.cumulativeReviewHistory().first.any { it.targetUnit==14 })
        }
    }
    private fun hasTestTagPrefix(prefix:String)=SemanticsMatcher("tag starts with $prefix") {
        it.config.getOrElse(androidx.compose.ui.semantics.SemanticsProperties.TestTag) { "" }.startsWith(prefix)
    }
    private fun waitTag(tag:String)=compose.waitUntil(conditionDescription=tag,timeoutMillis=30_000) {
        compose.onAllNodesWithTag(tag).fetchSemanticsNodes().isNotEmpty()
    }
    companion object {
        private const val newTitle="Review supplied plurals, possession and reported time"
        private const val oldTitle="Review earlier reading and supplied reporting times"
    }
}
