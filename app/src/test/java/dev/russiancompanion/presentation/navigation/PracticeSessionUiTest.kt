package dev.russiancompanion.presentation.navigation

import android.Manifest
import android.app.Application
import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.data.local.ActivityCategory
import dev.russiancompanion.data.local.CompletionState
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.data.local.audio
import dev.russiancompanion.data.local.minimalDocument
import dev.russiancompanion.data.local.rights
import dev.russiancompanion.data.local.seed.PlanDefinitionSeed
import dev.russiancompanion.data.local.seed.SeedJson
import dev.russiancompanion.data.local.text
import dev.russiancompanion.data.navigation.RoomNavigationGateway
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class PracticeSessionUiTest {
    @get:Rule
    val compose = createComposeRule()

    private val stores = mutableListOf<LocalStore>()

    @After
    fun closeStores() {
        stores.forEach { it.close() }
    }

    @Test
    fun sessionPlaysRecordsReplaysAndCompletesWithoutAScore() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        Shadows.shadowOf(context as Application).grantPermissions(Manifest.permission.RECORD_AUDIO)
        val store = LocalStore.openInMemory(context).also { stores += it }
        store.applySeed(SeedJson.encode(practiceDocument()))
        store.confirmLesson("segment-1", "2026-09-21")
        val practiceAudio = PermissivePracticeAudio()
        compose.setContent {
            CompanionApp(
                gateway = RoomNavigationGateway(store, { null }, practiceAudio),
                onRetryOpen = {},
                today = { "2026-09-21" },
                recordedAt = { "2026-09-21T09:00" },
                practiceAudio = practiceAudio,
            )
        }

        click("Practice")
        click("SOURCE_TEXT_TOKEN")
        compose.onNodeWithText("No pronunciation score is provided.").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("fluency").assertDoesNotExist()
        click("Play model")
        compose.onNodeWithText("Played").performScrollTo().assertIsDisplayed()
        click("Record")
        click("Stop")
        click("Replay model")
        click("Play your recording")
        click("Mark for tutor review")
        compose.onNodeWithText("Remove tutor review mark").performScrollTo().assertIsDisplayed()
        click("Complete practice")
        compose.onNodeWithText("Completed").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Complete practice").assertDoesNotExist()

        val recording = store.recordings().single()
        assertTrue(recording.markedForTutorReview)
        assertEquals(CompletionState.COMPLETED, recording.completionState)
        val log = store.activityLogs().single()
        assertEquals(ActivityCategory.SPEAKING_AND_PRONUNCIATION, log.category)
        assertFalse(log.selfAttested)
        assertEquals(0, log.durationMinutes)
    }

    @Test
    fun missingModelFileStaysOutOfTheSessionAndKeepsAttribution() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val store = LocalStore.openInMemory(context).also { stores += it }
        store.applySeed(SeedJson.encode(practiceDocument()))
        store.confirmLesson("segment-1", "2026-09-21")
        compose.setContent {
            CompanionApp(
                gateway = RoomNavigationGateway(store, { null }, dev.russiancompanion.data.audio.AndroidPracticeAudio(context)),
                onRetryOpen = {},
                today = { "2026-09-21" },
                practiceAudio = dev.russiancompanion.data.audio.AndroidPracticeAudio(context),
            )
        }
        click("Practice")
        compose.onNodeWithText("This practice model is unavailable.").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("TEST-LICENSE: Test attribution").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Practice session").assertDoesNotExist()
    }

    @Test
    fun unmatchedTextNeverEntersTheLibraryAndNoRecordingOnlySubstituteAppears() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        Shadows.shadowOf(context as Application).grantPermissions(Manifest.permission.RECORD_AUDIO)
        val store = LocalStore.openInMemory(context).also { stores += it }
        store.applySeed(
            SeedJson.encode(
                minimalDocument().copy(
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
                    sourceTexts = listOf(text(matchVerified = false)),
                    assetRightsRecords = listOf(rights()),
                    audioModels = listOf(audio()),
                ),
            ),
        )
        store.confirmLesson("segment-1", "2026-09-21")
        compose.setContent {
            CompanionApp(
                gateway = RoomNavigationGateway(store, { null }, PermissivePracticeAudio()),
                onRetryOpen = {},
                today = { "2026-09-21" },
                practiceAudio = PermissivePracticeAudio(),
            )
        }
        compose.onNodeWithText("No licensed practice audio is available.").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("SOURCE_TEXT_TOKEN").assertDoesNotExist()
        compose.onNodeWithText("Record").assertDoesNotExist()
        compose.onNodeWithText("Practice").performScrollTo().assertIsDisplayed()
    }

    private fun click(text: String) {
        compose.onNodeWithText(text).performScrollTo().performClick()
    }

    private fun practiceDocument() = minimalDocument().copy(
        planDefinitions = listOf(
            PlanDefinitionSeed(
                id = "plan-listen",
                segmentId = "segment-1",
                activityType = "LISTEN_RECORD_COMPARE",
                instruction = "Listen to the licensed model",
                sortOrder = 1,
                required = true,
                audioModelIds = listOf("audio-1"),
            ),
        ),
        sourceTexts = listOf(text(matchVerified = true)),
        assetRightsRecords = listOf(rights()),
        audioModels = listOf(audio()),
    )
}
