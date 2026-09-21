package dev.russiancompanion.presentation.navigation

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.data.local.audio
import dev.russiancompanion.data.local.gate
import dev.russiancompanion.data.local.minimalDocument
import dev.russiancompanion.data.local.rights
import dev.russiancompanion.data.local.seed.GateConditionSeed
import dev.russiancompanion.data.local.seed.PlanDefinitionSeed
import dev.russiancompanion.data.local.seed.SeedJson
import dev.russiancompanion.data.local.text
import dev.russiancompanion.data.navigation.RoomNavigationGateway
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
    fun emptyCatalogStaysInOnboardingAndReportsMissingAttributions() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val store = LocalStore.openInMemory(context).also { stores += it }
        store.applySeed(LocalStore.readPackagedSeed(context))
        show(RoomNavigationGateway(store) { LocalStore.readPackagedSeed(context) })

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
        compose.onNodeWithText("No attribution inventory is installed. This installation is not release-ready.")
            .assertIsDisplayed()
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
        show(RoomNavigationGateway(store) { null })

        repeat(3) { compose.onNodeWithText("Next").performClick() }
        compose.onNodeWithText("Lesson 1", substring = false).performClick()
        compose.onNodeWithText("Confirm lesson").performClick()

        compose.onNodeWithText("Current lesson: Lesson 1").assertIsDisplayed()
        compose.onNodeWithText("Optional drills").assertDoesNotExist()
        compose.onNodeWithText("Today's Plan", substring = false).performClick()
        compose.onNodeWithText("Current lesson: Lesson 1").assertIsDisplayed()
        compose.onNodeWithText("Back").performClick()

        compose.onNodeWithText("Practice", substring = false).performClick()
        compose.onNodeWithText("SOURCE_TEXT_TOKEN").performClick()
        compose.onNodeWithText("Practice session").assertIsDisplayed()
        compose.onNodeWithText("Next model").assertDoesNotExist()
        compose.onNodeWithText("Attributions and licenses").performClick()
        compose.onNodeWithText("TEST-LICENSE: Test attribution").assertIsDisplayed()
        compose.onNodeWithText("Back to practice").performClick()
        compose.onNodeWithText("Practice session").assertIsDisplayed()
        compose.onNodeWithText("Back").performClick()
        compose.onNodeWithText("No licensed practice audio is available.").assertDoesNotExist()
        compose.onNodeWithText("Home", substring = false).performClick()

        compose.onNodeWithText("Gates", substring = false).performClick()
        compose.onNodeWithText("Activity gate, not proficiency certification.").assertIsDisplayed()
        compose.onNodeWithText("Displayed test rule").performClick()
        compose.onNodeWithText("This gate definition is unreadable. No advancement decision is shown.")
            .assertDoesNotExist()
        compose.onNodeWithText("Progress", substring = false).performClick()
        compose.onNodeWithText("Recorded activity is work completed, not certified ability.").assertIsDisplayed()
        compose.onNodeWithText("Log activity").performClick()
        compose.onNodeWithText("Log activity").assertIsDisplayed()
        compose.onNodeWithText("Back").performClick()
        compose.onNodeWithText("Local export").performClick()
        compose.onNodeWithText(
            "Export will include logs, gate evidence, progress data, and recordings. This is local export, not cloud backup.",
        ).assertIsDisplayed()
        compose.onNodeWithText("Home", substring = false).performClick()
        compose.onNodeWithText("Current lesson: Lesson 1").assertIsDisplayed()
    }

    private fun show(gateway: dev.russiancompanion.domain.navigation.NavigationGateway?) {
        compose.setContent {
            CompanionApp(
                gateway = gateway,
                onRetryOpen = {},
                today = { "2026-09-21" },
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
            ),
        ),
        gateDefinitions = listOf(
            gate(
                conditions = listOf(
                    GateConditionSeed(
                        id = "c-book",
                        evidenceType = "BOOK_REVISION_SCORE",
                        operator = "AT_LEAST",
                        operand = "1",
                        sortOrder = 0,
                    ),
                ),
            ),
        ),
        sourceTexts = listOf(text(matchVerified = true)),
        assetRightsRecords = listOf(rights()),
        audioModels = listOf(audio()),
    )
}
