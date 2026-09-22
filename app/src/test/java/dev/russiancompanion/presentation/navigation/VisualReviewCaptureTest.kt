package dev.russiancompanion.presentation.navigation

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.unit.dp
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.curriculum.V2PackLoader
import dev.russiancompanion.v2.ui.V2App
import dev.russiancompanion.v2.ui.V2ContentInset
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
        assertAtContentInset("v2-today-unit")
        assertAtContentInset("v2-today-objective")
        capture("04-today")
        compose.onNodeWithText("Course").performClick()
        assertAtContentInset("v2-course-intro")
        capture("05-course")
        compose.onNodeWithText("Unit 1 · Printed letters", substring = true).performClick()
        assertAtContentInset("v2-back")
        assertAtContentInset("v2-unit-heading")
        assertAtContentInset("v2-unit-state")
        assertAtContentInset("v2-unit-install")
        compose.onNodeWithTag("v2-section-letter-board").performScrollTo()
        assertAtContentInset("v2-section-letter-board")
        compose.onNodeWithTag("v2-section-sessions").performScrollTo()
        assertAtContentInset("v2-section-sessions")
        capture("06-unit-1")
        compose.onNodeWithText("Back").performClick()
        compose.onNodeWithText("Unit 2 · Letter review", substring = true).performClick()
        capture("07-unit-2")
        compose.onNodeWithText("Practice").performClick()
        assertAtContentInset("v2-practice-heading")
        capture("08-practice")
        compose.onNodeWithText("Alphabet Lab").performClick()
        assertAtContentInset("v2-section-confusable")
        compose.onNodeWithTag("v2-confusable-Р").performScrollTo()
        assertAtContentInset("v2-confusable-Р")
        capture("08b-alphabet-lab")
        compose.onNodeWithText("Back").performClick()
        compose.onNodeWithText("Reference").performClick()
        assertAtContentInset("v2-reference-heading")
        capture("09-reference")
        compose.onNodeWithText("Progress").performClick()
        assertAtContentInset("v2-progress-heading")
        capture("10-progress")
        compose.onNodeWithText("Settings, appearance, and attributions").performScrollTo().performClick()
        assertAtContentInset("v2-settings-heading")
        assertAtContentInset("v2-back")
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
        assertAtContentInset("v2-today-unit")
        assertAtContentInset("v2-today-objective")
        capture("12-today-large-font")
        compose.onNodeWithText("Course").performClick()
        compose.onNodeWithTag("v2-course-intro").performScrollTo()
        assertAtContentInset("v2-course-intro")
        capture("13-course-large-font")
        compose.onNodeWithText("Unit 1 · Printed letters", substring = true).performClick()
        assertAtContentInset("v2-back")
        assertAtContentInset("v2-unit-heading")
        assertAtContentInset("v2-unit-state")
        compose.onNodeWithTag("v2-section-letter-board").performScrollTo()
        assertAtContentInset("v2-section-letter-board")
        capture("14-unit-1-large-font")
        compose.onNodeWithTag("v2-back").performClick()
        compose.onNodeWithText("Practice").performClick()
        assertAtContentInset("v2-practice-heading")
        capture("15-practice-large-font")
        compose.onNodeWithText("Alphabet Lab").performClick()
        assertAtContentInset("v2-section-confusable")
        compose.onNodeWithTag("v2-confusable-Р").performScrollTo()
        assertAtContentInset("v2-confusable-Р")
        capture("16-alphabet-lab-large-font")
        compose.onNodeWithTag("v2-back").performClick()
        compose.onNodeWithText("Reference").performClick()
        assertAtContentInset("v2-reference-heading")
        capture("17-reference-large-font")
        compose.onNodeWithText("Progress").performClick()
        assertAtContentInset("v2-progress-heading")
        capture("18-progress-large-font")
        compose.onNodeWithText("Settings, appearance, and attributions").performScrollTo().performClick()
        assertAtContentInset("v2-settings-heading")
        capture("19-settings-large-font")
    }

    private fun assertAtContentInset(tag: String) {
        val node = compose.onNodeWithTag(tag)
        val unclipped = node.getUnclippedBoundsInRoot()
        assertTrue("$tag starts at ${unclipped.left}", unclipped.left >= V2ContentInset)
        val visible = node.getBoundsInRoot()
        if (visible.right - visible.left > 1.dp && visible.bottom - visible.top > 1.dp) {
            assertEquals("$tag visible left", unclipped.left, visible.left)
            assertTrue("$tag visible at ${visible.left}", visible.left >= V2ContentInset)
        }
    }

    private fun capture(name: String) {
        compose.waitForIdle()
        val bitmap = compose.onRoot().captureToImage().asAndroidBitmap()
        assertBitmapContentGutter(name, bitmap)
        writePng(bitmap, name)
    }

    private fun assertBitmapContentGutter(name: String, bitmap: Bitmap) {
        val density = bitmap.width / 411f
        val gutterPx = (V2ContentInset.value * density).toInt() - 2
        val top = (bitmap.height * 0.18f).toInt()
        val bottom = (bitmap.height * 0.72f).toInt()
        for (y in top until bottom step 12) {
            for (x in 0 until gutterPx) {
                val pixel = bitmap.getPixel(x, y)
                val ink = Color.red(pixel) + Color.green(pixel) + Color.blue(pixel)
                assertTrue("$name has ink at $x,$y", ink < 90)
            }
        }
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
