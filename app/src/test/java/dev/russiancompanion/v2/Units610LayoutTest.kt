package dev.russiancompanion.v2

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.unit.dp
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.curriculum.V2PackLoader
import dev.russiancompanion.v2.curriculum.unit
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
class Units610LayoutTest {
    @get:Rule
    val compose = createComposeRule()

    private val stores = mutableListOf<LocalStore>()

    @After
    fun closeStores() {
        stores.forEach { it.close() }
    }

    @Test
    fun unitSixConceptCardAtNormalFont() = openAndCheckLayout(6, "unit6-concept-card-normal")

    @Test
    fun unitSevenConceptCardAtNormalFont() = openAndCheckLayout(7, "unit7-concept-card-normal")

    @Test
    fun unitEightConceptCardAtNormalFont() = openAndCheckLayout(8, "unit8-concept-card-normal")

    @Test
    fun unitNineConceptCardAtNormalFont() = openAndCheckLayout(9, "unit9-concept-card-normal")

    @Test
    fun unitTenConceptCardAtNormalFont() = openAndCheckLayout(10, "unit10-concept-card-normal")

    @Test
    @Config(sdk = [33], qualifiers = "w411dp-h914dp-xxhdpi", fontScale = 2.0f)
    fun unitSixConceptCardAtLargeFont() = openAndCheckLayout(6, "unit6-concept-card-large-font")

    @Test
    @Config(sdk = [33], qualifiers = "w411dp-h914dp-xxhdpi", fontScale = 2.0f)
    fun unitSevenConceptCardAtLargeFont() = openAndCheckLayout(7, "unit7-concept-card-large-font")

    @Test
    @Config(sdk = [33], qualifiers = "w411dp-h914dp-xxhdpi", fontScale = 2.0f)
    fun unitEightConceptCardAtLargeFont() = openAndCheckLayout(8, "unit8-concept-card-large-font")

    @Test
    @Config(sdk = [33], qualifiers = "w411dp-h914dp-xxhdpi", fontScale = 2.0f)
    fun unitNineConceptCardAtLargeFont() = openAndCheckLayout(9, "unit9-concept-card-large-font")

    @Test
    @Config(sdk = [33], qualifiers = "w411dp-h914dp-xxhdpi", fontScale = 2.0f)
    fun unitTenConceptCardAtLargeFont() = openAndCheckLayout(10, "unit10-concept-card-large-font")

    private fun openAndCheckLayout(number: Int, name: String) {
        openFirstConcept(number)
        assertRenderedContentGutter(name)
    }

    private fun openFirstConcept(number: Int) {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val store = LocalStore.openInMemory(context).also { stores += it }
        val pack = V2PackLoader.parse(File(repo(), "app/src/main/assets/content/v2-curriculum.json").readText())
        val unit = pack.unit(number)!!
        val session = unit.sessions.first { it.steps.any { step -> step.kind == "TEACH" } }
        val card = session.steps.first { it.kind == "TEACH" }
        assertTrue(card.body.length < 500)
        store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = number, startingPoint = "RESUME_FROM_BOOK"))
        compose.setContent { V2App(store = store, pack = pack, onRetryOpen = {}) }
        compose.onNodeWithText("Course").performClick()
        openUnitRow(number)
        compose.onNodeWithText(session.title).performScrollTo().performClick()
        compose.onNodeWithTag("v2-step-title").assertIsDisplayed()
        assertAtContentInset("v2-step-title")
        assertAtContentInset("v2-session-continue")
    }

    private fun openUnitRow(number: Int) {
        compose.onNodeWithTag("v2-content").performScrollToIndex(number)
        compose.onNodeWithTag("v2-unit-row-$number").performScrollTo().performClick()
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

    private fun assertRenderedContentGutter(name: String) {
        compose.waitForIdle()
        val bitmap = compose.onRoot().captureToImage().asAndroidBitmap()
        assertBitmapContentGutter(name, bitmap)
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
