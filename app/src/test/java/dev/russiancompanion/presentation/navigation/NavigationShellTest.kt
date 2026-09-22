package dev.russiancompanion.presentation.navigation

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.data.local.audio
import dev.russiancompanion.data.local.gate
import dev.russiancompanion.data.local.minimalDocument
import dev.russiancompanion.data.local.rights
import dev.russiancompanion.data.local.seed.PlanDefinitionSeed
import dev.russiancompanion.data.local.seed.SeedJson
import dev.russiancompanion.data.local.text
import dev.russiancompanion.data.navigation.RoomNavigationGateway
import dev.russiancompanion.domain.gate.defaultDisplayedRuleText
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class NavigationShellTest {
    @get:Rule
    val compose = createComposeRule()

    private val stores = mutableListOf<LocalStore>()

    @After
    fun closeStores() {
        stores.forEach { it.close() }
    }

    @Test
    fun packagedCatalogWithoutPathwayStaysInOnboardingAndReportsShareAlikeBlock() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val store = LocalStore.openInMemory(context).also { stores += it }
        store.applySeed(LocalStore.readPackagedSeed(context))
        show(RoomNavigationGateway(store, { LocalStore.readPackagedSeed(context) }, PermissivePracticeAudio()))

        compose.onNodeWithText("This offline app is a study companion for an independent adult beginner working through The New Penguin Russian Course.")
            .assertIsDisplayed()
        compose.onNodeWithText("This app coordinates your book, AnkiDroid work, listening and recording, gates, and logs.")
            .assertIsDisplayed()
        compose.onNodeWithText("Home", substring = false).assertDoesNotExist()
        compose.onNodeWithText("Optional drills").assertDoesNotExist()
        compose.onNodeWithText("Next").performClick()
        compose.onNodeWithText(
            "This app does not teach from reproduced Penguin content, connect to AnkiDroid, judge Russian, or certify proficiency.",
        ).assertIsDisplayed()
        compose.onNodeWithText("Next").performClick()
        compose.onNodeWithText(
            "Default gates do not require a tutor. Tutor sign-off can be added later only as stricter evidence you choose. All data stays on this device. Backup is a local export you start.",
        ).assertIsDisplayed()
        compose.onNodeWithText("Next").performClick()
        compose.onNodeWithText("A lesson selection is required to create the plan.").assertIsDisplayed()
        compose.onNodeWithText("Onboarding cannot finish because plan definitions are unavailable.").assertIsDisplayed()
        compose.onNodeWithText("Confirm lesson").assertIsNotEnabled()
        compose.onNodeWithText("Attributions and licenses").performClick()
        compose.onNodeWithText("Public release is blocked until share-alike confirmation is recorded.")
            .assertIsDisplayed()
        compose.onNodeWithText("No attribution inventory is installed. This installation is not release-ready.")
            .assertDoesNotExist()
        compose.onNodeWithText("Back").performClick()
        compose.onNodeWithText("Confirm lesson").assertIsDisplayed()
    }

    @Test
    fun missingGatewayShowsTheLocalDataProblem() {
        show(null)
        compose.onNodeWithText("Local data could not be opened. Bundled content and user data are affected.")
            .assertIsDisplayed()
        compose.onNodeWithText("Retry").assertIsDisplayed()
        compose.onNodeWithText("Home", substring = false).assertDoesNotExist()
        compose.onNodeWithText("Optional drills").assertDoesNotExist()
    }

    @Test
    fun admittedLessonOpensHomeAndTheShellDestinations() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val store = LocalStore.openInMemory(context).also { stores += it }
        store.applySeed(SeedJson.encode(admittedCatalog()))
        show(RoomNavigationGateway(store, { null }, PermissivePracticeAudio()))

        repeat(3) { compose.onNodeWithText("Next").performClick() }
        compose.onNodeWithText("Lesson 1", substring = false).performClick()
        compose.onNodeWithText("Confirm lesson").performClick()

        compose.onNodeWithText("Current lesson: Lesson 1").assertIsDisplayed()
        compose.onNodeWithText("Optional drills").assertDoesNotExist()
        clickText("Today's Plan", substring = false)
        compose.onNodeWithText("Current lesson: Lesson 1").assertIsDisplayed()
        compose.onNodeWithText("Back").performClick()

        clickText("Practice", substring = false)
        clickText("SOURCE_TEXT_TOKEN")
        compose.onNodeWithText("Practice session").assertIsDisplayed()
        compose.onNodeWithText("Next model").assertDoesNotExist()
        clickText("Attributions and licenses")
        showText("TEST-LICENSE: Test attribution")
        clickText("Back to practice")
        showText("Practice session")
        compose.onNodeWithText("Back").performScrollTo().performClick()
        compose.onNodeWithText("No licensed practice audio is available.").assertDoesNotExist()
        clickText("Home", substring = false)

        clickText("Gates", substring = false)
        showText("Activity gate, not proficiency certification.")
        clickText(defaultDisplayedRuleText())
        compose.onNodeWithText("This gate definition is unreadable. No advancement decision is shown.")
            .assertDoesNotExist()
        clickText("Progress", substring = false)
        showText("Recorded activity is work completed, not certified ability.")
        clickText("Log activity")
        compose.onNodeWithText("Log activity").assertIsDisplayed()
        compose.onNodeWithText("Back").performClick()
        clickText("Local export")
        showText(
            "Export will include logs, gate evidence, progress data, and recordings. This is local export, not cloud backup.",
        )
        clickText("Home", substring = false)
        compose.onNodeWithText("Current lesson: Lesson 1").assertIsDisplayed()
    }

    private fun clickText(text: String, substring: Boolean = true) {
        compose.onNodeWithText(text, substring = substring).performScrollTo().performClick()
    }

    private fun showText(text: String) {
        compose.onNodeWithText(text).performScrollTo().assertIsDisplayed()
    }

    private fun show(gateway: dev.russiancompanion.domain.navigation.NavigationGateway?) {
        compose.setContent {
            CompanionApp(
                gateway = gateway,
                onRetryOpen = {},
                today = { "2026-09-21" },
                practiceAudio = PermissivePracticeAudio(),
            )
        }
    }

    private fun admittedCatalog() = minimalDocument().copy(
        planDefinitions = listOf(
            PlanDefinitionSeed(
                id = "plan-book",
                segmentId = "segment-1",
                activityType = "BOOK",
                instruction = "Use your copy of the book for this lesson",
                sortOrder = 1,
                required = true,
                lessonNumber = 1,
            ),
        ),
        gateDefinitions = listOf(gate()),
        sourceTexts = listOf(text(matchVerified = true)),
        assetRightsRecords = listOf(rights()),
        audioModels = listOf(audio()),
    )
}
