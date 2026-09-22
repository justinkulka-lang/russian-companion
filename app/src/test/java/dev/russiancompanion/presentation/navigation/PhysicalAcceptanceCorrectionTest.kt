package dev.russiancompanion.presentation.navigation

import android.content.Context
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isSelected
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.data.navigation.RoomNavigationGateway
import dev.russiancompanion.domain.appearance.AppearancePreference
import dev.russiancompanion.domain.navigation.LessonOption
import dev.russiancompanion.domain.navigation.NavigationGateway
import dev.russiancompanion.domain.navigation.NavigationSnapshot
import dev.russiancompanion.domain.navigation.onboardingConfirmation
import dev.russiancompanion.presentation.theme.CompanionTheme
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33], qualifiers = "w411dp-h914dp-xxhdpi")
class PhysicalAcceptanceCorrectionTest {
    @get:Rule
    val compose = createComposeRule()

    private val stores = mutableListOf<LocalStore>()

    @After
    fun closeStores() {
        stores.forEach { it.close() }
    }

    @Test
    fun darkThemeUsesDarkBackgroundAndReadableForegroundRoles() {
        var background = 0f
        var onBackground = 0f
        var onSurface = 0f
        var onPrimary = 0f
        compose.setContent {
            CompanionTheme(AppearancePreference.DARK) {
                val colors = MaterialTheme.colorScheme
                background = colors.background.luminance()
                onBackground = colors.onBackground.luminance()
                onSurface = colors.onSurface.luminance()
                onPrimary = colors.onPrimary.luminance()
            }
        }
        assertTrue(background < 0.2f)
        assertTrue(onBackground > 0.7f)
        assertTrue(onSurface > 0.7f)
        assertTrue(onPrimary < 0.3f)
    }

    @Test
    fun onboardingObservesSafeSystemInsetsAndUsesTheApplicationShell() {
        val activity = source("src/main/java/dev/russiancompanion/presentation/MainActivity.kt")
        assertTrue(activity.contains("enableEdgeToEdge()"))
        assertTrue(activity.contains("WindowCompat.setDecorFitsSystemWindows(window, false)"))
        val theme = source("src/main/java/dev/russiancompanion/presentation/theme/CompanionTheme.kt")
        assertTrue(theme.contains("isAppearanceLightStatusBars = !useDark"))
        assertTrue(theme.contains("contentColor = MaterialTheme.colorScheme.onBackground"))
        val shell = source("src/main/java/dev/russiancompanion/presentation/navigation/ShellPage.kt")
        assertTrue(shell.contains("contentWindowInsets = WindowInsets.safeDrawing"))
        assertTrue(shell.contains("CenterAlignedTopAppBar"))
        assertTrue(shell.contains("PersistentActionBar"))
    }

    @Test
    fun disallowedOnboardingPhrasesAreAbsentFromTheVisibleIntroduction() {
        showPackaged()
        compose.onNodeWithText(
            "This offline companion coordinates your book work, AnkiDroid practice, listening and recording, advancement gates, and local progress.",
        ).assertIsDisplayed()
        compose.onNodeWithText("Book work uses your copy.").assertIsDisplayed()
        compose.onNodeWithText("independent adult beginner").assertDoesNotExist()
        compose.onNodeWithText("The New Penguin Russian Course").assertDoesNotExist()
    }

    @Test
    fun allThirtyLessonRowsAreAvailableAndConfirmStaysVisible() {
        showPackaged()
        compose.onNodeWithText("Next").performClick()
        compose.onNodeWithText("Confirm lesson").assertIsDisplayed()
        val list = compose.onNodeWithTag("lesson-selection-list")
        for (index in 0 until 30) {
            list.performScrollToIndex(index)
            compose.onNodeWithText("Lesson ${index + 1}", substring = false).assertIsDisplayed()
            compose.onNodeWithText("Confirm lesson").assertIsDisplayed()
        }
    }

    @Test
    fun tappingLesson12SelectsOnlyLesson12AndEnablesConfirm() {
        val snapshot = lessonSnapshot()
        compose.setContent {
            var selected by remember { mutableStateOf<String?>(null) }
            CompanionTheme(AppearancePreference.DARK) {
                OnboardingLessonSelection(
                    snapshot = snapshot,
                    selectedLessonId = selected,
                    confirmation = onboardingConfirmation(snapshot, selected),
                    operationInProgress = false,
                    confirmFailed = false,
                    onSelectLesson = { selected = it },
                    onConfirm = {},
                    onReloadSeed = {},
                    onBack = {},
                )
            }
        }
        compose.onNodeWithText("Confirm lesson").assertIsNotEnabled()
        compose.onNodeWithTag("lesson-selection-list").performScrollToIndex(11)
        compose.onNodeWithText("Lesson 12", substring = false).performClick()
        compose.waitForIdle()
        compose.onNodeWithText("Selected: Lesson 12").assertIsDisplayed()
        compose.onNode(hasText("Lesson 12") and isSelected()).assertExists()
        compose.onNodeWithText("Confirm lesson").assertIsDisplayed().assertIsEnabled()
        compose.onNodeWithTag("lesson-selection-list").performScrollToIndex(0)
        compose.onNode(hasText("Lesson 1") and isSelected()).assertDoesNotExist()
        compose.onNodeWithTag("lesson-selection-list").performScrollToIndex(11)
        val selectedRows = compose.onAllNodes(selectedMatcher(), useUnmergedTree = true)
            .fetchSemanticsNodes()
            .count { node ->
                node.config.getOrElse(SemanticsProperties.Selected) { false }
            }
        assertEquals(1, selectedRows)
    }

    @Test
    fun confirmationPersistsOnceAndOpensHome() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val store = LocalStore.openInMemory(context).also { stores += it }
        store.applySeed(LocalStore.readPackagedSeed(context))
        val inner = RoomNavigationGateway(store, { LocalStore.readPackagedSeed(context) }, PermissivePracticeAudio())
        val gateway = CountingGateway(inner)
        show(gateway)
        compose.onNodeWithText("Next").performClick()
        compose.onNodeWithTag("lesson-selection-list").performScrollToIndex(11)
        compose.onNodeWithText("Lesson 12", substring = false).performClick()
        compose.onNodeWithText("Selected: Lesson 12").assertIsDisplayed()
        compose.onNodeWithText("Confirm lesson").assertIsEnabled().performClick()
        compose.onNodeWithText("Current lesson: Lesson 12").assertIsDisplayed()
        compose.onNodeWithText("Home", substring = false).assertIsDisplayed()
        assertEquals(1, gateway.confirms)
        compose.onNodeWithText("Confirm lesson").assertDoesNotExist()
    }

    @Test
    fun relaunchRestoresTheSelectedLessonAndSkipsOnboarding() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val store = LocalStore.openInMemory(context).also { stores += it }
        store.applySeed(LocalStore.readPackagedSeed(context))
        val gateway = RoomNavigationGateway(store, { LocalStore.readPackagedSeed(context) }, PermissivePracticeAudio())
        val lesson = gateway.current("2026-09-21").lessons.first { it.lessonReference == "Lesson 12" }
        gateway.confirmLesson(lesson.id, "2026-09-21")
        show(gateway)
        compose.onNodeWithText("Current lesson: Lesson 12").assertIsDisplayed()
        compose.onNodeWithText("Home", substring = false).assertIsDisplayed()
        compose.onNodeWithText("Next").assertDoesNotExist()
        compose.onNodeWithText("Confirm lesson").assertDoesNotExist()
    }

    @Test
    fun persistenceFailureShowsAnErrorAndDoesNotCompleteOnboarding() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val store = LocalStore.openInMemory(context).also { stores += it }
        store.applySeed(LocalStore.readPackagedSeed(context))
        val inner = RoomNavigationGateway(store, { LocalStore.readPackagedSeed(context) }, PermissivePracticeAudio())
        show(ThrowingConfirmGateway(inner))
        compose.onNodeWithText("Next").performClick()
        compose.onNodeWithTag("lesson-selection-list").performScrollToIndex(11)
        compose.onNodeWithText("Lesson 12", substring = false).performClick()
        compose.onNodeWithText("Confirm lesson").performClick()
        compose.onNodeWithText("The lesson selection was not saved. Stay on this screen and try again.")
            .assertIsDisplayed()
        compose.onNodeWithText("Selected: Lesson 12").assertIsDisplayed()
        compose.onNodeWithText("Confirm lesson").assertIsDisplayed()
        compose.onNodeWithText("Home", substring = false).assertDoesNotExist()
        compose.onNodeWithText("Current lesson: Lesson 12").assertDoesNotExist()
    }

    @Test
    @Config(sdk = [33], qualifiers = "w411dp-h914dp-xxhdpi", fontScale = 1.6f)
    fun largeFontScaleKeepsSelectionAndConfirmVisible() {
        showPackaged()
        compose.onNodeWithText("Next").performClick()
        compose.onNodeWithText("Lesson 1", substring = false).performClick()
        compose.onNodeWithText("Selected: Lesson 1").assertIsDisplayed()
        compose.onNodeWithText("Confirm lesson").assertIsDisplayed().assertIsEnabled()
        compose.onNode(hasText("Lesson 1") and isSelected()).assertExists()
    }

    @Test
    fun noInternetPermissionAndReleaseTasksStayGated() {
        val manifest = source("src/main/AndroidManifest.xml")
        assertFalse(manifest.contains("android.permission.INTERNET"))
        val gradle = if (File("app/build.gradle.kts").isFile) {
            File("app/build.gradle.kts").readText()
        } else {
            File("build.gradle.kts").readText()
        }
        assertTrue(gradle.contains("dependsOn(\"checkReleaseGate\")"))
        assertTrue(gradle.contains("verifyReleaseGateAttachment"))
        assertTrue(gradle.contains("versionName = \"2.0.0-dev\""))
        assertTrue(gradle.contains("versionCode = 5"))
    }

    private fun showPackaged() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val store = LocalStore.openInMemory(context).also { stores += it }
        store.applySeed(LocalStore.readPackagedSeed(context))
        show(RoomNavigationGateway(store, { LocalStore.readPackagedSeed(context) }, PermissivePracticeAudio()))
    }

    private fun show(gateway: NavigationGateway) {
        compose.setContent {
            CompanionApp(
                gateway = gateway,
                onRetryOpen = {},
                today = { "2026-09-21" },
                practiceAudio = PermissivePracticeAudio(),
            )
        }
    }

    private fun lessonSnapshot(): NavigationSnapshot {
        return NavigationSnapshot.pending().copy(
            lessons = (1..30).map { lesson ->
                LessonOption("segment-$lesson", "Lesson $lesson", planCount = 1)
            },
        )
    }

    private fun source(path: String): String {
        val direct = File(path)
        if (direct.isFile) return direct.readText()
        val nested = File("app", path)
        if (nested.isFile) return nested.readText()
        error("Missing $path")
    }

    private fun selectedMatcher(): SemanticsMatcher {
        return SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.RadioButton)
    }

    private class CountingGateway(
        private val inner: NavigationGateway,
    ) : NavigationGateway by inner {
        var confirms = 0
        override fun confirmLesson(segmentId: String, localDate: String) {
            confirms += 1
            inner.confirmLesson(segmentId, localDate)
        }
    }

    private class ThrowingConfirmGateway(
        private val inner: NavigationGateway,
    ) : NavigationGateway by inner {
        override fun confirmLesson(segmentId: String, localDate: String) {
            error("lesson persistence failed")
        }
    }
}
