package dev.russiancompanion.v2

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.curriculum.*
import dev.russiancompanion.v2.ui.V2App
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class Unit4PresentRepairUiTest {
    @get:Rule val compose = createComposeRule()
    @Test fun speakingWeMissRestoresTeachingAndGuideWithoutReplacingIndependentAnswer() = repair("u4-i-verb-говорить-мы")
    @Test fun seeingHeMissRestoresTeachingAndGuideWithoutReplacingIndependentAnswer() = repair("u4-i-verb-видеть-он")
    @Test fun writingYouMissRestoresTeachingAndGuideWithoutReplacingIndependentAnswer() = repair("u4-i-verb-писать-вы")
    @Test fun goingTheyMissRestoresTeachingAndGuideWithoutReplacingIndependentAnswer() = repair("u4-i-verb-идти-они")

    private fun repair(id: String) {
        val p = Unit4PresentTeachingTest.current
        val item = p.item(id)!!
        val check = p.step("u4-st-check-present")!!
        val context: Context = ApplicationProvider.getApplicationContext()
        LocalStore.openInMemory(context).use { s ->
            s.v2SaveProfile(s.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = 4,
                startingPoint = "RESUME_FROM_BOOK", currentSessionId = check.first.id,
                currentStepIndex = check.first.steps.indexOf(check.second)).verifiedResume(p))
            compose.setContent { V2App(s, p, {}) }
            tag("v2-continue").performClick()
            val wrong = item.options.first { it != item.answer }
            tag("v2-option-$id-$wrong").performScrollTo().performClick()
            tag("v2-item-feedback-$id").performScrollTo().assertTextEquals("Not the reviewed answer. ${item.feedback}")
            tag("v2-remediation-$id").performScrollTo().performClick()
            val rem = p.step("u4-st-rem-present")!!.second
            compose.onNodeWithText(rem.body).performScrollTo().assertIsDisplayed()
            for (eid in rem.exampleIds) tag("v2-example-$eid").performScrollTo().assertTextEquals(p.example(eid)!!.display)
            tag("v2-review-practice").performClick()
            val guides = reviewedGuidedPractice(p, rem.id)
            for ((index, guide) in guides.withIndex()) {
                for (gid in p.step(guide)!!.second.itemIds) {
                    val choice = p.item(gid)!!
                    tag("v2-option-$gid-${choice.answer}").performScrollTo().performClick()
                }
                assertFalse(id in creditItemIds(p, s.v2LearnerView()))
                tag(if (index == guides.lastIndex) "v2-return-to-check" else "v2-next-practice").performClick()
            }
            tag("v2-option-$id-$wrong").performScrollTo().assertIsSelected()
            assertFalse(id in creditItemIds(p, s.v2LearnerView()))
            tag("v2-option-$id-${item.answer}").performScrollTo().performClick()
            compose.waitUntil(30_000) { id in creditItemIds(p, s.v2LearnerView()) }
            assertTrue(Unit4PresentTeachingTest.objective(p).checkItemIds.filter { it != id }
                .none { it in creditItemIds(p, s.v2LearnerView()) })
            assertFalse(advancementDecision(p, p.unit(4)!!, s.v2LearnerView()).allowed)
        }
    }

    private fun tag(value: String): SemanticsNodeInteraction {
        compose.waitUntil(30_000) { compose.onAllNodesWithTag(value).fetchSemanticsNodes().isNotEmpty() }
        return compose.onNodeWithTag(value)
    }
}
