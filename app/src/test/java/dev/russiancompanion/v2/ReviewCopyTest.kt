package dev.russiancompanion.v2

import android.content.Context
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.RussianCompanionApplication
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.curriculum.*
import dev.russiancompanion.v2.ui.V2CumulativeReviewScreen
import kotlinx.serialization.json.Json
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class ReviewCopyTest {
    @get:Rule val compose = createComposeRule()
    private val root = listOf(File("."), File("..")).first { File(it, "app/src/main").isDirectory }
    private fun pack() = V2PackLoader.parse(File(root, "app/src/main/assets/content/v2-curriculum.json").readText())

    @Test fun catalogAndPackCopyAgreeAndCurrentCopyIsBoundIntoTheReviewDigest() {
        val p = pack()
        val data = Json.decodeFromString(V2ReviewCopy.serializer(), File(root, "catalog/review-copy.json").readText())
        assertEquals(data, p.reviewCopy)
        assertEquals(emptyList<String>(), reviewCopyDefects(data))
        assertEquals((3..30).toSet(), data.titles.keys)
        assertEquals((3..30).toSet(), data.introductions.keys)
        assertEquals("2 of 3 review checks recorded", data.text("count", "completed" to 2, "total" to 3))
        assertEquals("Return to Unit 12 advancement", data.text("returnToGate", "unit" to 12))
        assertEquals("Review answer recorded. Current feedback", data.text("correctFeedback", "feedback" to "Current feedback"))
        assertNotEquals(V2Validator.contentReviewDigest(p), V2Validator.contentReviewDigest(p.copy(
            reviewCopy = data.copy(titles = data.titles + (3 to "Changed review heading")))))
    }

    @Test fun missingBlankUnknownAndMalformedCopyIsRejected() {
        val p = pack()
        val c = p.reviewCopy
        val invalid = listOf(c.copy(titles = c.titles - 3), c.copy(introductions = c.introductions - 30),
            c.copy(titles = c.titles + (31 to "Unknown unit")), c.copy(introductions = c.introductions + (4 to " ")),
            c.copy(messages = c.messages - "startAssigned"), c.copy(messages = c.messages + ("unused" to "Unknown")),
            c.copy(messages = c.messages + ("count" to "{completed} recorded")),
            c.copy(messages = c.messages + ("count" to "{completed} of {total} and {unknown}")),
            c.copy(messages = c.messages + ("preparing" to "Preparing {")), c.copy(messages = c.messages + ("preparing" to "")))
        for (changed in invalid) assertTrue(reviewCopyDefects(changed).isNotEmpty())
        val result = V2Validator.validate(p.copy(reviewCopy = c.copy(messages = c.messages - "required")))
        assertFalse(result.ok)
        assertTrue(result.errors.any { it.contains("cumulative-review messages") || it.contains("message required") })
    }

    @Test fun reviewPanelRendersPackCopyAndPreservesRepairActionWithoutStartingAnAssignment() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        (context.applicationContext as RussianCompanionApplication).closeStartupStoreForTests()
        LocalStore.openInMemory(context).use { store ->
            val original = pack()
            val copy = original.reviewCopy.copy(titles = original.reviewCopy.titles + (3 to "Current review heading"),
                introductions = original.reviewCopy.introductions + (3 to "Current review instructions"),
                messages = original.reviewCopy.messages + ("insufficient" to "Current earlier-work reminder") +
                    ("openEarlierUnit" to "Open current Unit {unit}"))
            val p = original.copy(reviewCopy = copy)
            val before = store.governedLearnerDocument()
            var opened: Int? = null
            var starts = 0
            compose.setContent {
                V2CumulativeReviewScreen(p, store.v2LearnerView(), 3, false, {}, {}, { starts++ },
                    { _, _, _ -> }, { opened = it }, {}, {})
            }
            compose.onNodeWithText("Current review heading").assertIsDisplayed()
            compose.onNodeWithText("Current review instructions").assertIsDisplayed()
            compose.onNodeWithTag("v2-review-insufficient").assertTextEquals("Current earlier-work reminder")
            compose.onNodeWithTag("v2-review-repair").performScrollTo().assertTextContains("Open current Unit", substring = true).performClick()
            assertEquals(1, opened)
            assertEquals(0, starts)
            assertEquals(before, store.governedLearnerDocument())
            compose.onNodeWithTag("v2-review-start").assertDoesNotExist()
        }
    }
}
