package dev.russiancompanion.presentation.navigation

import android.content.Context
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertDoesNotExist
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextReplacement
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.data.local.minimalDocument
import dev.russiancompanion.data.local.seed.PlanDefinitionSeed
import dev.russiancompanion.data.local.seed.SeedJson
import dev.russiancompanion.data.navigation.RoomNavigationGateway
import dev.russiancompanion.domain.progress.BOOK_ATTESTATION
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import java.util.zip.ZipFile

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class ProgressUiTest {
    @get:Rule
    val compose = createComposeRule()

    private val stores = mutableListOf<LocalStore>()
    private var stamp = 0

    @After
    fun closeStores() {
        stores.forEach { it.close() }
    }

    @Test
    fun progressShowsAllCategoriesAndALocalExportOfTheLogs() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val store = LocalStore.openInMemory(context).also { stores += it }
        store.applySeed(SeedJson.encode(planDocument()))
        val exportFile = File(context.cacheDir, "export.zip")
        compose.setContent {
            CompanionApp(
                gateway = RoomNavigationGateway(store, { null }, PermissivePracticeAudio()),
                onRetryOpen = {},
                today = { "2026-09-21" },
                recordedAt = { "2026-09-21T09:%02d".format(stamp++) },
                practiceAudio = PermissivePracticeAudio(),
                chooseExportDestination = { write ->
                    exportFile.outputStream().use { write(it) }
                },
            )
        }

        repeat(3) { compose.onNodeWithText("Next").performClick() }
        compose.onNodeWithText("Lesson 1", substring = false).performClick()
        compose.onNodeWithText("Confirm lesson").performClick()
        click("Progress")
        show("Recorded activity is work completed, not certified ability.")
        compose.onAllNodesWithText("Activity imbalance").assertCountEquals(7)
        show("No activity is recorded.")
        compose.onNodeWithText("fluency").assertDoesNotExist()

        click("Log activity")
        click("Reading")
        compose.onNodeWithText("An activity category is required.").assertDoesNotExist()
        compose.onNode(hasSetTextAction()).performScrollTo().performTextReplacement("15")
        click("Mark completed")
        click("Save activity")

        show("Reading: 15 minutes recorded, 1 completed")
        compose.onAllNodesWithText("Activity imbalance").assertCountEquals(6)
        show("Recorded time: 15 minutes. Recorded completions: 1.")

        click("Log activity")
        click("Grammar")
        show(BOOK_ATTESTATION)
        compose.onNode(hasSetTextAction()).performScrollTo().performTextReplacement("5")
        click("Save activity")
        click("Grammar,", substring = true)
        show(BOOK_ATTESTATION)
        click("Close log")

        click("Local export")
        click("Choose a local destination")
        show("Export completed.")
        compose.onNodeWithText("This export contains no logs, gate evidence, or recordings.").assertDoesNotExist()
        ZipFile(exportFile).use { zip ->
            val logs = zip.getInputStream(zip.getEntry("activity-logs.csv")).bufferedReader().use { it.readText() }
            assertTrue(logs.contains("READING"))
            assertTrue(logs.contains(",15,"))
            assertTrue(logs.contains(BOOK_ATTESTATION))
        }
        compose.onNodeWithText("fluency").assertDoesNotExist()
    }

    private fun click(text: String, substring: Boolean = false) {
        compose.onNodeWithText(text, substring = substring).performScrollTo().performClick()
    }

    private fun show(text: String) {
        compose.onNodeWithText(text, substring = true).performScrollTo().assertIsDisplayed()
    }

    private fun planDocument() = minimalDocument().copy(
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
    )
}
