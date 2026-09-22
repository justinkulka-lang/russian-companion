package dev.russiancompanion.presentation.navigation

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.data.navigation.RoomNavigationGateway
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [33], qualifiers = "w411dp-h914dp-xxhdpi")
class VisualReviewCaptureTest {
    @get:Rule
    val compose = createComposeRule()

    private val stores = mutableListOf<LocalStore>()

    @After
    fun closeStores() {
        stores.forEach { it.close() }
    }

    @Test
    fun captureGalaxyS26UltraEquivalentScreens() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val store = LocalStore.openInMemory(context).also { stores += it }
        store.applySeed(LocalStore.readPackagedSeed(context))
        compose.setContent {
            CompanionApp(
                gateway = RoomNavigationGateway(store, { LocalStore.readPackagedSeed(context) }, PermissivePracticeAudio()),
                onRetryOpen = {},
                today = { "2026-09-21" },
                practiceAudio = PermissivePracticeAudio(),
            )
        }
        capture("01-onboarding-introduction")
        compose.onNodeWithText("Next").performClick()
        compose.onNodeWithText("Lesson 1", substring = false).performClick()
        capture("02-lesson-selection-lesson-1")
        compose.onNodeWithText("Lesson 30", substring = false).performScrollTo().performClick()
        capture("03-lesson-selection-lesson-30")
        compose.onNodeWithText("Confirm lesson").performClick()
        capture("04-home")
        compose.onNodeWithText("Today's Plan").performClick()
        capture("05-todays-plan")
        compose.onNodeWithText("Home").performClick()
        compose.onNodeWithText("Practice").performClick()
        capture("06-listen-record-compare")
        compose.onNodeWithText("Home").performClick()
        compose.onNodeWithText("Gates").performClick()
        capture("07-gates")
        compose.onNodeWithText("Home").performClick()
        compose.onNodeWithText("Progress").performClick()
        capture("08-progress")
        compose.onNodeWithText("Home").performClick()
        compose.onNodeWithText("Attributions and licenses").performClick()
        capture("09-attributions")
        assertTrue(outputDir().listFiles()?.any { it.extension == "png" } == true)
    }

    @Test
    @Config(sdk = [33], qualifiers = "w411dp-h914dp-xxhdpi", fontScale = 1.6f)
    fun captureLargeFontOnboardingAndLessonSelection() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val store = LocalStore.openInMemory(context).also { stores += it }
        store.applySeed(LocalStore.readPackagedSeed(context))
        compose.setContent {
            CompanionApp(
                gateway = RoomNavigationGateway(store, { LocalStore.readPackagedSeed(context) }, PermissivePracticeAudio()),
                onRetryOpen = {},
                today = { "2026-09-21" },
                practiceAudio = PermissivePracticeAudio(),
            )
        }
        capture("10-onboarding-introduction-large-font")
        compose.onNodeWithText("Next").performClick()
        compose.onNodeWithText("Lesson 1", substring = false).performClick()
        capture("11-lesson-selection-large-font")
    }

    private fun capture(name: String) {
        compose.waitForIdle()
        val bitmap = compose.onRoot().captureToImage().asAndroidBitmap()
        writePng(bitmap, name)
    }

    private fun writePng(bitmap: Bitmap, name: String) {
        val destinations = listOf(outputDir(), File("/opt/cursor/artifacts/screenshots"))
        for (dir in destinations) {
            dir.mkdirs()
            val file = File(dir, "$name.png")
            file.outputStream().use { stream ->
                assertTrue(name, bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream))
            }
            assertTrue(file.length() > 0)
        }
    }

    private fun outputDir(): File {
        val repo = listOf(File("."), File("..")).first { File(it, "release/acceptance").isDirectory }
        return File(repo, "release/acceptance/visual-review").apply { mkdirs() }
    }
}
