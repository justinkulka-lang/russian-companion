package dev.russiancompanion.v2

import android.content.Context
import android.graphics.Insets
import android.view.View
import android.view.WindowInsets as AndroidInsets
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
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

/** Host geometry and semantics only; not TalkBack or physical-device acceptance. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33], qualifiers = "w411dp-h914dp-xxhdpi")
class V2InsetsAccessibilityTest {
    @get:Rule val compose = createComposeRule()

    @Test fun onboardingRadioHasOneLabelledActionAndChangesSelection() {
        compose.setContent { V2OnboardingScreen(false, {}) }
        repeat(2) { compose.onNodeWithTag("v2-onboarding-next").performClick() }
        val choice = compose.onNodeWithText("I can already read Cyrillic")
        choice.performScrollTo().assertHasClickAction()
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.RadioButton))
            .assertIsNotSelected().performClick().assertIsSelected()
        compose.onAllNodes(hasText("I can already read Cyrillic") and hasClickAction()).assertCountEquals(1)
    }

    @Test fun onboardingCheckboxLabelOwnsToggleActionAndCheckedState() {
        compose.setContent { V2OnboardingScreen(false, {}) }
        compose.onNodeWithTag("v2-onboarding-next").performClick()
        compose.onNodeWithText("I have the book").performScrollTo().assertHasClickAction()
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Checkbox))
            .assertIsOn().performClick().assertIsOff()
        compose.onAllNodes(hasText("I have the book") and hasClickAction()).assertCountEquals(1)
    }

    @Test fun lessonOptionLabelSelectsValueOnceAndExposesSelectedState() = lesson(false)
    @Test fun lockedLessonOptionRemainsLabelledAndCannotSubmit() = lesson(true)

    private fun lesson(locked: Boolean) {
        val pack = OptionPresentationFixture.pack
        val session = pack.unit(1)!!.sessions.first { s -> s.steps.any { it.itemIds.isNotEmpty() } }
        val index = session.steps.indexOfFirst { it.itemIds.isNotEmpty() }
        val step = session.steps[index]
        val item = pack.item(step.itemIds.first())!!
        val selected = mutableStateOf<Map<String, String>>(emptyMap())
        var calls = 0
        val option = item.options.first { it != item.answer }
        LocalStore.openInMemory(context()).use { store ->
            compose.setContent {
                V2SessionScreen(pack, store.v2LearnerView(), session.id, index, false,
                    selected.value, if (locked) setOf(item.id) else emptySet(), emptySet(), emptyMap(),
                    {}, { _, _ -> }, { id, value, stepId ->
                        calls++
                        assertEquals(option, value)
                        selected.value = mapOf(governedSelectionKey(stepId, id) to value)
                        true
                    }, { _, _ -> }, { _, _ -> }, { _, _ -> }, { _, _ -> }, {}, {}, {}, { _, _ -> }, {})
            }
            val node = compose.onNodeWithTag("v2-option-${item.id}-$option")
            node.performScrollTo().assertTextContains(option).assertHasClickAction()
                .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.RadioButton))
            if (locked) {
                node.assertIsNotEnabled().performClick()
                assertEquals(0, calls)
            } else {
                node.assertIsEnabled().assertIsNotSelected().performClick().assertIsSelected()
                node.performClick()
                assertEquals(1, calls)
            }
        }
    }

    @Test fun practiceOptionHasLabelledActionAndKeepsSubmissionSeparate() {
        val pack = OptionPresentationFixture.pack
        LocalStore.openInMemory(context()).use { store ->
            store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true,
                currentUnitNumber = 8, startingPoint = "RESUME_FROM_BOOK"))
            val learner = store.v2LearnerView()
            val item = practiceQueue(pack, learner, "grammar").first()
            var submissions = 0
            compose.setContent { V2LabScreen(pack, learner, "grammar", emptySet(), {}, { _, _ ->
                submissions++; true
            }) }
            val tag = "v2-lab-option-${item.options.first()}"
            compose.waitUntil(30_000) { compose.onAllNodesWithTag(tag).fetchSemanticsNodes().isNotEmpty() }
            compose.onNodeWithTag(tag).performScrollTo().assertTextContains(item.options.first())
                .assertHasClickAction().assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.RadioButton))
                .performClick().assertIsSelected()
            assertEquals(0, submissions)
        }
    }

    @Test fun scrollingScaffoldRespectsSystemCutoutAndKeyboardInsetsWithoutDoublePadding() = insets(false)
    @Test fun lazyScaffoldRespectsSystemCutoutAndKeyboardInsetsWithoutDoublePadding() = insets(true)

    private fun insets(lazy: Boolean) {
        lateinit var view: View
        var observedTop = -1
        var observedIme = -1
        compose.setContent {
            val localView = LocalView.current
            val density = LocalDensity.current
            val top = WindowInsets.safeDrawing.getTop(density)
            val ime = WindowInsets.ime.getBottom(density)
            SideEffect { view = localView; observedTop = top; observedIme = ime }
            val footer: @androidx.compose.runtime.Composable () -> Unit = {
                Text("Footer", Modifier.semantics { testTag = "inset-footer" })
            }
            if (lazy) V2LazyScaffold("Insets", V2Tab.TODAY, {}, footer = footer) {
                item { Text("Content", Modifier.semantics { testTag = "inset-body" }) }
            } else V2Scaffold("Insets", V2Tab.TODAY, {}, footer = footer) {
                Text("Content", Modifier.semantics { testTag = "inset-body" })
            }
        }
        val root = compose.onRoot().fetchSemanticsNode().boundsInRoot
        fun dispatch(top: Int, bottom: Int, keyboard: Int) {
            compose.runOnIdle {
                view.dispatchApplyWindowInsets(AndroidInsets.Builder()
                    .setInsets(AndroidInsets.Type.statusBars(), Insets.of(0, top, 0, 0))
                    .setInsets(AndroidInsets.Type.navigationBars(), Insets.of(0, 0, 0, bottom))
                    .setInsets(AndroidInsets.Type.displayCutout(), Insets.of(36, 0, 24, 0))
                    .setInsets(AndroidInsets.Type.ime(), Insets.of(0, 0, 0, keyboard))
                    .setVisible(AndroidInsets.Type.systemBars(), true)
                    .setVisible(AndroidInsets.Type.ime(), keyboard > 0).build())
            }
            compose.waitForIdle()
            assertEquals("Fixture delivered status-bar insets", top, observedTop)
            assertEquals("Fixture delivered IME insets", keyboard, observedIme)
            val title = compose.onNodeWithText("Insets").fetchSemanticsNode().boundsInRoot
            val body = compose.onNodeWithTag("inset-body").fetchSemanticsNode().boundsInRoot
            val nav = compose.onNodeWithTag("v2-bottom-nav").fetchSemanticsNode().boundsInRoot
            val footer = compose.onNodeWithTag("inset-footer").fetchSemanticsNode().boundsInRoot
            assertTrue("Title must clear status bar: $title", title.top >= top)
            assertTrue("Body must clear cutout: $body", body.left >= 36 && body.right <= root.right - 24)
            val safeBottom = root.bottom - maxOf(bottom, keyboard)
            assertEquals("Bottom inset applied exactly once", safeBottom, nav.bottom, 1f)
            assertTrue("Footer remains above navigation and keyboard", footer.bottom <= nav.top)
        }
        dispatch(90, 72, 0)
        dispatch(90, 72, 540)
        dispatch(90, 72, 0)
    }

    private fun context(): Context = ApplicationProvider.getApplicationContext()
}
