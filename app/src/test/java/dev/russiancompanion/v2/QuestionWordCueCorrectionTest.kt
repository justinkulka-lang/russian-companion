package dev.russiancompanion.v2
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.curriculum.*
import dev.russiancompanion.v2.ui.V2App
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
@RunWith(RobolectricTestRunner::class)
@Config(sdk=[33])
class QuestionWordCueCorrectionTest {
    @get:Rule val compose=createComposeRule()
    @Test fun questionWordLessonAsksFromEnglishPurposeWithoutPrintingTheAnswerAsACue() {
        val p=pack();val question=p.items.first { it.kind=="SOURCE_QUESTION" }
        val owner=p.step(p.unit(5)!!.sessions.flatMap { it.steps }.first { question.id in it.itemIds }.id)!!
        val context:Context=ApplicationProvider.getApplicationContext()
        LocalStore.openInMemory(context).use { store ->
            store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted=true,currentUnitNumber=5,startingPoint="RESUME_FROM_BOOK",currentSessionId=owner.first.id,currentStepIndex=owner.first.steps.indexOf(owner.second)).verifiedResume(p))
            compose.setContent { V2App(store=store,pack=p,onRetryOpen={}) }
            compose.onNodeWithTag("v2-continue").performClick()
            compose.onNodeWithTag("v2-item-prompt-${question.id}").performScrollTo().assertIsDisplayed().assertTextEquals(question.prompt)
            // Answers remain available as choices; they must not also be shown as pre-answer cues.
            compose.onAllNodesWithTag("v2-prompt-letter").assertCountEquals(0)
        }
    }
    @Test fun displayRoleIsDigestBoundAndCannotBeUsedToHideARequiredCue() {
        val p=pack();val questions=p.items.filter { it.kind=="SOURCE_QUESTION" && it.promptLetter==it.answer }
        org.junit.Assert.assertEquals(6,questions.size)
        questions.forEach { item ->
            org.junit.Assert.assertEquals("ANSWER_REFERENCE",item.promptLetterRole)
            val old=item.copy(promptLetterRole="CUE")
            org.junit.Assert.assertNotEquals(V2Validator.checkItemContentDigest(old),V2Validator.checkItemContentDigest(item))
            val broken=p.copy(items=p.items.map { if(it.id==item.id)old else it })
            org.junit.Assert.assertTrue(V2Validator.validate(broken).errors.any { it.contains("discloses its answer") })
        }
        val grammar=p.items.first { it.kind=="CASE_RECOGNITION" }
        val hidden=p.copy(items=p.items.map { if(it.id==grammar.id)it.copy(promptLetterRole="ANSWER_REFERENCE") else it })
        org.junit.Assert.assertTrue(V2Validator.validate(hidden).errors.any { it.contains("invalid cue role") })
        org.junit.Assert.assertTrue(V2Validator.validate(p).ok)
    }
    @Test fun answerExposingHistoricalAttemptsAndStepCompletionDoNotSupplyCurrentCredit() {
        val p=pack();val old=p.copy(items=p.items.map { if(it.promptLetterRole=="ANSWER_REFERENCE")it.copy(promptLetterRole="CUE") else it })
        val item=p.items.first { it.promptLetterRole=="ANSWER_REFERENCE" }
        val step=p.unit(5)!!.sessions.flatMap { it.steps }.first { item.id in it.itemIds }
        val context:Context=ApplicationProvider.getApplicationContext()
        LocalStore.openInMemory(context).use { store ->
            store.v2RecordGovernedAttempt(prepareGovernedAttempt(old,item.id,item.answer,PRESCRIBED_CHECK,step.id,"2026-10-12T19:00")!!)
            store.v2CompleteGovernedStep(step.id,stepContentDigest(old,old.step(step.id)!!.second),"2026-10-12T19:00")
            val learner=store.v2LearnerView()
            org.junit.Assert.assertTrue(item.id in creditItemIds(old,learner))
            org.junit.Assert.assertFalse(item.id in creditItemIds(p,learner))
            org.junit.Assert.assertFalse(completedStepCounts(p,learner,step.id))
            org.junit.Assert.assertEquals(1,store.v2Attempts().size)
            org.junit.Assert.assertEquals(1,store.v2StepProgress().size)
        }
    }
    private fun pack()=V2PackLoader.parse(File(if(File("app").isDirectory) "." else "..","app/src/main/assets/content/v2-curriculum.json").readText())
}
