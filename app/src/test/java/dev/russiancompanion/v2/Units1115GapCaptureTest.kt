package dev.russiancompanion.v2

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToIndex
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.curriculum.V2PackLoader
import dev.russiancompanion.v2.ui.V2App
import dev.russiancompanion.v2.ui.V2ContentInset
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
class Units1115GapCaptureTest {
    @get:Rule
    val compose = createComposeRule()

    private val stores = mutableListOf<LocalStore>()

    @After
    fun closeStores() {
        stores.forEach { it.close() }
    }

    @Test
    fun unitElevenGapAtNormalFont() = openAndCapture("unit11-mapping-gap-normal")

    @Test
    @Config(sdk = [33], qualifiers = "w411dp-h914dp-xxhdpi", fontScale = 2.0f)
    fun unitElevenGapAtLargeFont() = openAndCapture("unit11-mapping-gap-large-font")

    private fun openAndCapture(name: String) {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val store = LocalStore.openInMemory(context).also { stores += it }
        val pack = V2PackLoader.parse(File(repo(), "app/src/main/assets/content/v2-curriculum.json").readText())
        store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = 1))
        compose.setContent { V2App(store = store, pack = pack, onRetryOpen = {}) }
        compose.onNodeWithText("Course").performClick()
        compose.onNodeWithTag("v2-content").performScrollToIndex(11)
        compose.onNodeWithTag("v2-unit-row-11").performScrollTo().performClick()
        compose.onNodeWithTag("v2-unit-heading").assertIsDisplayed()
        compose.onNodeWithText("Not prepared").assertIsDisplayed()
        compose.onNodeWithTag("v2-mapping-gap").assertIsDisplayed()
        compose.onNodeWithText("Missing input: printed page range", substring = true).assertIsDisplayed()
        capture(name)
    }

    private fun capture(name: String) {
        compose.waitForIdle()
        val bitmap = compose.onRoot().captureToImage().asAndroidBitmap()
        assertBitmapContentGutter(name, bitmap)
        val destinations = listOf(File(repo(), "release/acceptance/visual-review"), File("/opt/cursor/artifacts/screenshots"))
        for (dir in destinations) {
            dir.mkdirs()
            val file = File(dir, "$name.png")
            file.outputStream().use { stream ->
                assertTrue(name, bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream))
            }
            assertTrue(file.length() > 0)
        }
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

    private fun repo(): File = listOf(File("."), File("..")).first { File(it, "app/src/main").isDirectory }
}
