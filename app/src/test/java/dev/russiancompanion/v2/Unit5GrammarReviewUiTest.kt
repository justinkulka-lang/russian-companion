package dev.russiancompanion.v2

import android.content.Context
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.backup.*
import dev.russiancompanion.v2.curriculum.*
import dev.russiancompanion.v2.ui.V2CumulativeReviewScreen
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class Unit5GrammarReviewUiTest {
    @get:Rule val compose = createComposeRule()
    @Test fun currentDeclarationDescribesGenderAndSubjectsWithoutClaimingReading() {
        LocalStore.openInMemory(context()).use { store ->
            assertEquals("unit5-prior-gender-subject-v2", cumulativeReviewDeclaredPolicy(pack, 5)!!.id)
            show(store)
            compose.onNodeWithText("Review earlier noun genders and supplied subjects").assertExists()
            compose.onNodeWithText("Review recorded noun genders from Unit 3", substring = true).performScrollTo().assertIsDisplayed()
            compose.onNodeWithTag("v2-review-start").assertDoesNotExist()
        }
    }
    @Test fun genuineLegacyTaskKeepsItsOwnTitleInstructionsAndQuestionMeaning() {
        val file = File(root, "audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-cc020-unit5-grammar-2026-10-22/BASELINE_LEGACY_UNIT5.zip")
        val temp = File(context().cacheDir, "unit5-legacy-ui-${System.nanoTime()}")
        try {
            val read = readGovernedBackup(file.inputStream(), "dev.russiancompanion", pack.packId, pack.packVersion, temp)
            assertTrue(read is BackupReadResult.Valid)
            val document = (read as BackupReadResult.Valid).backup.document
            LocalStore.openInMemory(context()).use { store ->
                assertEquals(GovernedReplaceOutcome.REPLACED, store.replaceGovernedLearner(document, emptyList()))
                show(store)
                compose.waitUntil(30_000) { compose.onAllNodesWithTag("v2-review-count").fetchSemanticsNodes().isNotEmpty() }
                compose.onNodeWithText("Review earlier reading and supplied subjects").assertExists()
                compose.onNodeWithText("Review earlier print-pair matching from Unit 1", substring = true).performScrollTo().assertIsDisplayed()
                compose.onNodeWithTag("v2-review-count").assertTextEquals("1 of 2 review checks recorded")
                assertEquals(document.reviewAssignments to document.reviewAnswers, store.cumulativeReviewHistory())
            }
        } finally { temp.deleteRecursively() }
    }
    private fun show(store: LocalStore) {
        compose.setContent { V2CumulativeReviewScreen(pack, store.v2LearnerView(), 5, false,
            {}, {}, {}, { _, _, _ -> }, {}, {}, {}) }
    }
    private fun context(): Context = ApplicationProvider.getApplicationContext()
    companion object {
        private val root = File(if (File("app").isDirectory) "." else "..")
        private val pack by lazy { V2PackLoader.parse(File(root, "app/src/main/assets/content/v2-curriculum.json").readText()).also { V2PackIdentityIndex.warm(it) } }
    }
}
