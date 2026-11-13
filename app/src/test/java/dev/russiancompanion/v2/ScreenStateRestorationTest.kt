package dev.russiancompanion.v2

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.RussianCompanionApplication
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.curriculum.*
import dev.russiancompanion.v2.ui.*
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** Saved-instance-state restoration; no process-death or device-acceptance claim. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class ScreenStateRestorationTest {
    @get:Rule val compose = createComposeRule()

    @Test fun referenceQuerySurvivesRestoreWithoutOpeningOrWritingAnything() = withStore { store ->
        val p = OptionPresentationFixture.pack
        val before = store.governedLearnerDocument()
        var opens = 0
        val restore = StateRestorationTester(compose)
        restore.setContent { V2ReferenceScreen(p, store.v2LearnerView(), {}, { opens++ }) }
        val query = "unmatched-reference-query"
        compose.onNodeWithTag("v2-reference-search").performTextInput(query)
        compose.onNodeWithText("No installed reference matches that search.").assertIsDisplayed()
        restore.emulateSavedInstanceStateRestore()
        compose.onNodeWithTag("v2-reference-search").assertTextContains(query)
        compose.onNodeWithText("No installed reference matches that search.").assertIsDisplayed()
        assertEquals(0, opens)
        assertEquals(before, store.governedLearnerDocument())
    }

    @Test fun alphabetChoiceSurvivesRestoreAndRemainsSelectableByItsLabel() = withStore { store ->
        val p = OptionPresentationFixture.pack
        val before = store.governedLearnerDocument()
        val letter = p.letters.first()
        var recognitions = 0
        val restore = StateRestorationTester(compose)
        restore.setContent { V2AlphabetLabScreen(p, store.v2LearnerView(), {}, { recognitions++ }) }
        val tile = compose.onNodeWithTag("v2-letter-${letter.uppercase}")
        tile.performScrollTo().assertTextContains(letter.uppercase).assertHasClickAction().assertIsNotSelected()
        tile.performClick().assertIsSelected()
        restore.emulateSavedInstanceStateRestore()
        tile.performScrollTo().assertTextContains(letter.uppercase).assertIsSelected()
        assertEquals(0, recognitions)
        assertEquals(before, store.governedLearnerDocument())
    }

    @Test fun expandedTodayPlanSurvivesRestoreAndResetsForANewUnit() = withStore { store ->
        val p = OptionPresentationFixture.pack
        val learner = mutableStateOf(store.v2LearnerView().copy(onboardingCompleted = true, currentUnitNumber = 1))
        val before = store.governedLearnerDocument()
        var continues = 0
        val restore = StateRestorationTester(compose)
        restore.setContent {
            V2TodayScreen(p, learner.value, continueTarget(p, learner.value), {}, { continues++ }, {})
        }
        compose.onNodeWithText("Show full unit plan").performScrollTo().performClick()
        compose.onNodeWithText("Hide full unit plan").assertIsDisplayed()
        restore.emulateSavedInstanceStateRestore()
        compose.onNodeWithText("Hide full unit plan").performScrollTo().assertIsDisplayed()
        compose.runOnIdle { learner.value = learner.value.copy(currentUnitNumber = 2) }
        compose.onNodeWithText("Show full unit plan").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Hide full unit plan").assertDoesNotExist()
        assertEquals(0, continues)
        assertEquals(before, store.governedLearnerDocument())
    }

    private fun withStore(block: (LocalStore) -> Unit) {
        val context = ApplicationProvider.getApplicationContext<Context>()
        (context.applicationContext as RussianCompanionApplication).closeStartupStoreForTests()
        LocalStore.openInMemory(context).use(block)
    }
}
