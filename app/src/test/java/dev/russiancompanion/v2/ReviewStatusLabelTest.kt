package dev.russiancompanion.v2

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.assertIsDisplayed
import dev.russiancompanion.v2.curriculum.CLOSED_REVIEW_STATES
import dev.russiancompanion.v2.curriculum.V2PackLoader
import dev.russiancompanion.v2.ui.V2ReferenceDetailScreen
import dev.russiancompanion.v2.ui.V2ReviewStatusLabels
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

/**
 * T16-n2 (owner decision D31b): reference.reviewStatus stays visible as one plain learner label per enum value. The
 * metadata is unchanged; the UI maps each value, and no raw enum value reaches a rendered field.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class ReviewStatusLabelTest {
    @get:Rule val compose = createComposeRule()
    private val root = listOf(File("."), File("..")).first { File(it, "app/src/main").isDirectory }
    private val pack by lazy { V2PackLoader.parse(File(root, "app/src/main/assets/content/v2-curriculum.json").readText()) }
    private val rawToken = Regex("\\b[A-Z][A-Z0-9]*_[A-Z0-9_]+\\b")

    @Test fun everyReviewStatusHasOnePlainLabel() {
        assertEquals(CLOSED_REVIEW_STATES, V2ReviewStatusLabels.LABELS.keys)
        assertEquals("Not yet checked by a qualified reviewer.", V2ReviewStatusLabels.label("IMPLEMENTED_PENDING_FINAL_AUDIT"))
        V2ReviewStatusLabels.LABELS.forEach { (status, label) ->
            assertTrue(status, label.isNotBlank() && label.endsWith("."))
            assertFalse("$status: $label", rawToken.containsMatchIn(label))
            CLOSED_REVIEW_STATES.forEach { raw -> assertFalse("$status label shows $raw", raw in label) }
        }
        // An unknown value never reaches the learner as itself.
        assertEquals(V2ReviewStatusLabels.NOT_YET_CHECKED, V2ReviewStatusLabels.label("SOME_FUTURE_STATE"))
        // The pack metadata is unchanged: every reference still carries a recognized raw status.
        assertTrue(pack.references.all { it.reviewStatus in CLOSED_REVIEW_STATES })
    }

    @Test fun noRawEnumValueReachesTheRenderedReferenceScreen() {
        val base = pack.references.first { it.id == "ref-ca-letter-names" }
        val statuses = CLOSED_REVIEW_STATES.toList()
        var current by mutableStateOf(statuses.first())
        compose.setContent {
            val probe = pack.copy(references = pack.references.map { if (it.id == base.id) it.copy(reviewStatus = current) else it })
            MaterialTheme { V2ReferenceDetailScreen(probe, base.id, {}, { _, _ -> }) }
        }
        statuses.forEach { status ->
            current = status
            compose.waitForIdle()
            compose.onNodeWithText(V2ReviewStatusLabels.label(status)).performScrollTo().assertIsDisplayed()
            CLOSED_REVIEW_STATES.forEach { raw ->
                compose.onAllNodes(hasText(raw, substring = true)).assertCountEquals(0)
            }
            compose.onAllNodesWithText("Review status:", substring = true).assertCountEquals(0)
        }
    }
}
