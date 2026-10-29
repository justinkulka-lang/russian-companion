package dev.russiancompanion.v2

import android.content.Context
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.RussianCompanionApplication
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.backup.*
import dev.russiancompanion.v2.curriculum.*
import dev.russiancompanion.v2.ui.V2UnitScreen
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import java.util.concurrent.atomic.AtomicInteger

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class SavedReviewEntryLabelTest {
    @get:Rule val compose = createComposeRule()

    @Test fun genuineSavedLegacyTaskLabelsItsOwnReviewAtTheUnitEntry() = withArchive { original, store ->
        val task = original.reviewAssignments.single { it.targetUnit == 5 }
        assertEquals(3, task.schema)
        assertEquals("unit5-prior-reading-subject-v1", task.policyId)
        assertEquals(task, currentCumulativeAssignment(pack, store.v2LearnerView(), 5))
        checkEntry(store, "Review earlier reading and supplied subjects")
    }

    @Test fun noSavedTaskUsesTheCurrentDeclarationWithoutCreatingAnAssignment() = withArchive { original, store ->
        val unstarted = original.copy(
            reviewAssignments = original.reviewAssignments.filter { it.targetUnit != 5 },
            reviewAnswers = original.reviewAnswers.filter { answer ->
                original.reviewAssignments.none { it.targetUnit == 5 && it.id == answer.assignmentId }
            },
        )
        assertEquals(GovernedReplaceOutcome.REPLACED, store.replaceGovernedLearner(unstarted, emptyList()))
        assertNull(currentCumulativeAssignment(pack, store.v2LearnerView(), 5))
        checkEntry(store, "Review earlier noun genders and supplied subjects")
    }

    private fun checkEntry(store: LocalStore, expected: String) {
        val before = store.governedLearnerDocument()
        val credits = currentCompletedUnitNumbers(pack, store.v2LearnerView())
        val opened = AtomicInteger()
        compose.setContent {
            V2UnitScreen(pack, store.v2LearnerView(), 5, {}, {}, {}, {}, {}, { opened.incrementAndGet() })
        }
        compose.onNodeWithTag("v2-open-cumulative-review").performScrollTo().assertTextContains(expected).performClick()
        compose.runOnIdle { assertEquals(1, opened.get()) }
        assertEquals(before, store.governedLearnerDocument())
        assertEquals(credits, currentCompletedUnitNumbers(pack, store.v2LearnerView()))
    }

    private fun withArchive(block: (GovernedLearnerDocument, LocalStore) -> Unit) {
        val context: Context = ApplicationProvider.getApplicationContext()
        (context.applicationContext as RussianCompanionApplication).closeStartupStoreForTests()
        val stage = File(context.cacheDir, "entry-label-${System.nanoTime()}")
        try {
            val file = File(root, "audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-cc020-unit5-grammar-2026-10-22/BASELINE_LEGACY_UNIT5.zip")
            val read = file.inputStream().use {
                readGovernedBackup(it, "dev.russiancompanion", pack.packId, pack.packVersion, stage)
            }
            assertTrue(read is BackupReadResult.Valid)
            val original = (read as BackupReadResult.Valid).backup.document
            LocalStore.openInMemory(context).use { store ->
                assertEquals(GovernedReplaceOutcome.REPLACED, store.replaceGovernedLearner(original, emptyList()))
                block(original, store)
            }
        } finally { stage.deleteRecursively() }
    }

    companion object {
        private val root = File(if (File("app").isDirectory) "." else "..")
        private val pack by lazy {
            V2PackLoader.parse(File(root, "app/src/main/assets/content/v2-curriculum.json").readText()).also {
                V2PackIdentityIndex.warm(it)
                RecordedCurriculumConservationScope.assertCurrent(root, it)
            }
        }
    }
}
