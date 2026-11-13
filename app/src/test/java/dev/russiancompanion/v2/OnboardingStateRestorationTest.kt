package dev.russiancompanion.v2

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.v2.createComposeRule
import dev.russiancompanion.v2.curriculum.V2StartingPoint
import dev.russiancompanion.v2.ui.V2OnboardingDraft
import dev.russiancompanion.v2.ui.V2OnboardingScreen
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** Saved-instance-state recreation only; not process death or physical reboot. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class OnboardingStateRestorationTest {
    @get:Rule val compose = createComposeRule()

    @Test fun currentPageAndAllDraftChoicesSurviveSavedInstanceStateRestore() {
        var saved: V2OnboardingDraft? = null
        val restore = StateRestorationTester(compose)
        restore.setContent { V2OnboardingScreen(false) { saved = it } }
        next()
        compose.onNodeWithText("I have the book").performScrollTo().performClick()
        compose.onNodeWithText("I use AnkiDroid").performScrollTo().performClick()
        compose.onNodeWithText("I keep a binder").performScrollTo().performClick()
        compose.onNodeWithText("I have a tutor now").performScrollTo().performClick()
        next()
        compose.onNodeWithText("I can already read Cyrillic").performScrollTo().performClick()
        next()
        compose.onNodeWithText("50 minutes").performScrollTo().performClick()
        assertNull(saved)
        restore.emulateSavedInstanceStateRestore()
        compose.onNodeWithText("50 minutes").performScrollTo().assertIsOn()
        next()
        assertNull(saved)
        compose.onNodeWithTag("v2-start-unit-1").performClick()
        assertEquals(V2OnboardingDraft(false, true, true, true, V2StartingPoint.CAN_READ_CYRILLIC, 50), saved)
    }

    @Test fun failedSaveAndRestoreKeepReadyDraftWithoutSubmittingItAutomatically() {
        var calls = 0
        var saved: V2OnboardingDraft? = null
        val restore = StateRestorationTester(compose)
        restore.setContent { V2OnboardingScreen(true) { calls += 1; saved = it } }
        next(); next()
        compose.onNodeWithText("Resume from a book lesson").performScrollTo().performClick()
        next()
        compose.onNodeWithText("20 minutes").performScrollTo().performClick()
        next()
        compose.onNodeWithText("The setup was not saved. Stay here and try again.").performScrollTo().assertIsDisplayed()
        restore.emulateSavedInstanceStateRestore()
        assertEquals(0, calls)
        compose.onNodeWithTag("v2-start-unit-1").assertIsDisplayed().performClick()
        assertEquals(1, calls)
        assertEquals(V2OnboardingDraft(true, false, false, false, V2StartingPoint.RESUME_FROM_BOOK, 20), saved)
    }

    private fun next() { compose.onNodeWithTag("v2-onboarding-next").performClick() }
}
