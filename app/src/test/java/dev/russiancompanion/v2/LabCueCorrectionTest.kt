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
import org.junit.After
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
@RunWith(RobolectricTestRunner::class)
@Config(sdk=[33])
class LabCueCorrectionTest {
    @get:Rule val compose=createComposeRule()
    private val stores=mutableListOf<LocalStore>()
    @After fun close() { stores.forEach { it.close() } }
    @Test fun readingShowsTheGovernedStressCue() = checkCue(4,"reading","Reading Lab")
    @Test fun vocabularyShowsTheGovernedWordCue() = checkCue(3,"vocabulary","Vocabulary and forms")
    @Test fun grammarShowsTheCompleteSentenceCue() = checkCue(28,"grammar","Grammar Lab")
    @Test fun unsupportedCompletionIsVisibleAsHistoryWithoutCurrentCredit() {
        val p=V2PackLoader.parse(File(if(File("app").isDirectory) "." else "..","app/src/main/assets/content/v2-curriculum.json").readText())
        val context:Context=ApplicationProvider.getApplicationContext()
        val store=LocalStore.openInMemory(context).also { stores+=it }
        store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted=true))
        store.v2CompleteUnit(6,"2026-10-12T19:00")
        compose.setContent { V2App(store=store,pack=p,onRetryOpen={}) }
        compose.onNodeWithText("Progress").performClick()
        compose.onNodeWithText("Completed units: none").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Past completion records need current evidence: 6").performScrollTo().assertIsDisplayed()
        org.junit.Assert.assertTrue(store.governedLearnerDocument().unitCompletions.any { it.unitNumber==6 })
    }
    private fun checkCue(n:Int,lab:String,title:String) {
        val p=V2PackLoader.parse(File(if(File("app").isDirectory) "." else "..","app/src/main/assets/content/v2-curriculum.json").readText())
        val context:Context=ApplicationProvider.getApplicationContext()
        val store=LocalStore.openInMemory(context).also { stores+=it }
        store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted=true,currentUnitNumber=n,startingPoint="RESUME_FROM_BOOK"))
        val item=practiceQueue(p,store.v2LearnerView(),lab).first()
        compose.setContent { V2App(store=store,pack=p,onRetryOpen={}) }
        compose.onNodeWithText("Practice").performClick()
        compose.onNodeWithText(title).performScrollTo().performClick()
        // assertTextEquals reads the Text semantics exposed to accessibility, not implementation state.
        compose.onNodeWithTag("v2-lab-cue").performScrollTo().assertIsDisplayed().assertTextEquals(requireNotNull(item.promptLetter))
        org.junit.Assert.assertTrue(store.v2Attempts().isEmpty())
    }
}
