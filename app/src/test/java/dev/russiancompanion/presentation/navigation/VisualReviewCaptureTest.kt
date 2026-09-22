package dev.russiancompanion.presentation.navigation

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.curriculum.V2PackLoader
import dev.russiancompanion.v2.ui.V2App
import org.junit.After
import org.junit.Assert.assertEquals
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
        val pack = V2PackLoader.parse(File(repo(), "app/src/main/assets/content/v2-curriculum.json").readText())
        compose.setContent { V2App(store = store, pack = pack, onRetryOpen = {}) }
        capture("01-onboarding-welcome")
        compose.onNodeWithTag("v2-onboarding-next").performClick()
        capture("02-onboarding-study-system")
        repeat(3) { compose.onNodeWithTag("v2-onboarding-next").performClick() }
        capture("03-onboarding-ready")
        compose.onNodeWithText("Start Unit 1").performClick()
        capture("04-today")
        compose.onNodeWithText("Course").performClick()
        capture("05-course")
        compose.onNodeWithText("Unit 1 · Printed letters", substring = true).performClick()
        assertUnclipped("v2-back")
        assertUnclipped("v2-unit-heading")
        capture("06-unit-1")
        compose.onNodeWithText("Back").performClick()
        compose.onNodeWithText("Unit 2 · Letter review", substring = true).performClick()
        capture("07-unit-2")
        compose.onNodeWithText("Practice").performClick()
        capture("08-practice")
        compose.onNodeWithText("Alphabet Lab").performClick()
        capture("08b-alphabet-lab")
        compose.onNodeWithText("Back").performClick()
        compose.onNodeWithText("Reference").performClick()
        capture("09-reference")
        compose.onNodeWithText("Progress").performClick()
        capture("10-progress")
        compose.onNodeWithText("Settings, appearance, and attributions").performClick()
        capture("11-settings")
        assertTrue(outputDir().listFiles()?.any { it.extension == "png" } == true)
    }

    @Test
    @Config(sdk = [33], qualifiers = "w411dp-h914dp-xxhdpi", fontScale = 2.0f)
    fun captureLargeFontTodayAndCourse() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val store = LocalStore.openInMemory(context).also { stores += it }
        val pack = V2PackLoader.parse(File(repo(), "app/src/main/assets/content/v2-curriculum.json").readText())
        store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = 1))
        compose.setContent { V2App(store = store, pack = pack, onRetryOpen = {}) }
        assertUnclipped("v2-today-unit")
        capture("12-today-large-font")
        compose.onNodeWithText("Course").performClick()
        capture("13-course-large-font")
        compose.onNodeWithText("Unit 1 · Printed letters", substring = true).performClick()
        assertUnclipped("v2-back")
        assertUnclipped("v2-unit-heading")
    }

    private fun assertUnclipped(tag: String) {
        val unclipped = compose.onNodeWithTag(tag).getUnclippedBoundsInRoot()
        val visible = compose.onNodeWithTag(tag).getBoundsInRoot()
        assertTrue(tag, unclipped.left >= 0.dp)
        assertTrue(tag, visible.left >= 0.dp)
        assertEquals(tag, unclipped.left, visible.left)
        assertEquals(tag, unclipped.top, visible.top)
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
        val repo = repo()
        return File(repo, "release/acceptance/visual-review").apply { mkdirs() }
    }

    private fun repo(): File = listOf(File("."), File("..")).first { File(it, "release/acceptance").isDirectory }
}
