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

    /**
     * T16c-n2 (owner decision D37c): an introduction is a plain instruction. The real assignment size depends on the learner's
     * history (A2-28 records 8, 8, 8, 8, 2 and 1), so an introduction names no count: no digit outside a unit reference, and no
     * number word ("both", "three", "all eight"). It carries no assessment commentary either.
     */
    @Test fun introductionsNameNoCountAndNoAssessmentCommentary() {
        val p = pack()
        val unitReference = Regex("\\bUnits? \\d+(?:(?:,| and| or) \\d+)*\\b")
        val numberWord = Regex("\\b(?:zero|one|two|three|four|five|six|seven|eight|nine|ten|eleven|twelve|thirteen|fourteen|fifteen|" +
            "sixteen|seventeen|eighteen|nineteen|twenty|thirty|forty|fifty|hundred|both|single|dozen)\\b", RegexOption.IGNORE_CASE)
        val commentary = Regex("\\b(?:does(?: not|n't) (?:complete|establish|demonstrate)|do(?: not|n't) establish|restores?|pools?|" +
            "fresh review check|counts? toward)\\b", RegexOption.IGNORE_CASE)
        p.reviewCopy.introductions.forEach { (unit, text) ->
            val rest = unitReference.replace(text, "Unit")
            assertFalse("$unit: $text", rest.any(Char::isDigit))
            assertFalse("$unit: $text", numberWord.containsMatchIn(rest))
            assertFalse("$unit: $text", commentary.containsMatchIn(text))
            assertTrue("$unit: $text", V2DesignDiagnostics.learnerVoiceRules(text).isEmpty())
        }
        // Regressions: the stale counts and the intro 14 commentary D37c names are refused.
        for (stale in listOf("Repairs restore all eight plural cards and fourteen examples.", "Complete one assigned answer from each of the three pools.",
                "Complete both review checks before continuing.", "Guided practice does not complete any fresh review check.")) {
            assertTrue(stale, numberWord.containsMatchIn(stale) || commentary.containsMatchIn(stale))
        }
    }

    /**
     * T16c-n3 (owner decision D46e, approved rule change): the validator (reviewCopyDefects) rejects an introduction naming any
     * numeral or number word, by a complete cardinal pattern. Probes cover each class the old test list missed (tens above
     * fifty, compounds, joined compounds, scale words, Unicode digits) and the classes it had.
     */
    @Test fun introductionsRejectEveryCardinalClass() {
        val p = pack()
        p.reviewCopy.introductions.forEach { (unit, text) -> assertFalse("$unit: $text", introductionNamesACount(text)) }
        assertEquals(emptyList<String>(), reviewCopyDefects(p.reviewCopy))
        val probes = listOf("Answer sixty review questions.", "Answer seventy-five review questions.", "Answer ninety nine questions.",
            "Answer eightyfour questions.", "Answer twenty-one questions.", "Answer one hundred questions.", "Answer a thousand cards.",
            "Answer a million cards.", "Answer zero cards.", "Answer the four cards.", "Answer the thirteen cards.", "Answer the 12 cards.",
            "Answer the ١٢ cards.", "Answer a dozen cards.", "Answer both cards.", "Answer a couple of cards.",
            "Answer a single card.", "Answer the Eleven cards.")
        for (probe in probes) {
            assertTrue(probe, introductionNamesACount(probe))
            val changed = p.reviewCopy.copy(introductions = p.reviewCopy.introductions + (14 to probe))
            assertTrue(probe, reviewCopyDefects(changed).any { "Unit 14 review introduction names a count" in it })
        }
        // A unit reference and ordinary words that contain number letters pass ("someone", "often", "tonight", "none").
        for (text in listOf("Review what Unit 4 taught before Units 5 and 6.", "Someone often reviews tonight; none are skipped.")) {
            assertFalse(text, introductionNamesACount(text))
        }
    }

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
