package dev.russiancompanion.v2

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.RussianCompanionApplication
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.backup.*
import dev.russiancompanion.v2.curriculum.*
import kotlinx.serialization.json.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import java.security.MessageDigest
import java.util.zip.ZipFile

/** Actual current-source controls alongside separately scoped historical contracts. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class CurrentFoundationHistoryTest {
    @Test fun allEightyFiveLearnerBackupsReopenRawButCannotBorrowChangedFoundationCredit() {
        val context = context()
        val con = Json.parseToJsonElement(File(root,
            "audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-literacy-foundation-history-regression-2026-10-29/CONSERVATION.json").readText()).jsonObject
        val archives = con.getValue("archives").jsonObject
        val evidenceOnly = setOf(
            "audit/independent-gpt61-2026-10-12/broader-regression-2026-10-18/CURRENT_RAW_EVIDENCE.zip",
            "audit/independent-gpt61-2026-10-12/broader-regression-2026-10-18/INITIAL/ABORTED_BINARY_RESULTS.zip")
        var learners = 0; var diagnostic = 0
        for ((path, expectedHash) in archives.toSortedMap()) {
            val file = File(root, path)
            assertEquals(path, expectedHash.jsonPrimitive.content, hash(file.readBytes()))
            val hasLearner = ZipFile(file).use { it.getEntry(BACKUP_LEARNER_ENTRY) != null }
            if (path in evidenceOnly) {
                assertFalse(path, hasLearner); diagnostic++; continue
            }
            assertTrue(path, hasLearner)
            val name = "current-foundation-${System.nanoTime()}.db"
            val stage = File(context.cacheDir, name)
            try {
                val read = file.inputStream().use { readGovernedBackup(it, "dev.russiancompanion", current.packId, current.packVersion, stage) }
                assertTrue(path, read is BackupReadResult.Valid)
                val original = (read as BackupReadResult.Valid).backup.document
                assertEquals(V2StartingPoint.NEW_TO_CYRILLIC.name, original.profile.startingPoint)
                fun verify(store: LocalStore) {
                    assertEquals(path, original, store.governedLearnerDocument())
                    assertEquals(path, original.reviewAssignments to original.reviewAnswers, store.cumulativeReviewHistory())
                    assertEquals(path, emptySet<Int>(), currentCompletedUnitNumbers(current, store.v2LearnerView()))
                    assertFalse(path, ordinaryUnitRequirements(current, current.unit(1)!!, store.v2LearnerView()).allowed)
                    original.reviewAssignments.filter { a -> a.slots.any { it.ownerUnit in 1..2 } }.forEach { a ->
                        assertFalse(path, reviewAssignmentCurrent(current, a))
                        assertTrue(path, completedReviewSlots(current, a, store.v2LearnerView().reviewAnswers).isEmpty())
                    }
                    assertEquals(path, original, store.governedLearnerDocument())
                }
                LocalStore.openNamed(context, name).use { store ->
                    assertEquals(GovernedReplaceOutcome.REPLACED, store.replaceGovernedLearner(original, emptyList())); verify(store)
                }
                LocalStore.openNamed(context, name).use { verify(it) }
                assertEquals(path, expectedHash.jsonPrimitive.content, hash(file.readBytes()))
                System.getProperty("audit.history.output")?.let { output ->
                    File(output).appendText(buildJsonObject {
                        put("kind", "actual_current_foundation_archive"); put("path", path)
                        put("modelCurriculumSource", RecordedLiteracyEpochScope.latestCommit)
                        put("restoredAndReopenedRaw", true); put("obsoleteCreditRefused", true)
                    }.toString() + "\n")
                }
                learners++
            } finally { stage.deleteRecursively(); context.deleteDatabase(name); File(context.filesDir, "recordings-$name").deleteRecursively() }
        }
        assertEquals(87, archives.size); assertEquals(85, learners); assertEquals(2, diagnostic)
    }

    @Test fun genuineFreshCurrentPrerequisitesReachUnitFourteenWithCurrentOwnerBindings() {
        val context = context(); val document = FreshCurrentPrerequisiteHistory.document(current, 14, context)
        LocalStore.openInMemory(context).use { store ->
            assertEquals(GovernedReplaceOutcome.REPLACED, store.replaceGovernedLearner(document, emptyList()))
            assertEquals((1..13).toSet(), currentCompletedUnitNumbers(current, store.v2LearnerView()))
            document.reviewAssignments.forEach { a ->
                assertTrue(reviewAssignmentCurrent(current, a))
                a.slots.forEach { slot -> assertEquals(V2Validator.unitContentReviewDigest(current, slot.ownerUnit), slot.unitDigest) }
            }
            FreshCurrentPrerequisiteHistory.ordinary(store, current, 14)
            assertTrue(ordinaryUnitRequirements(current, current.unit(14)!!, store.v2LearnerView()).allowed)
            val task = store.openCumulativeReview(current, 14, store.learnerMutationEpoch())!!
            assertEquals(16, task.schema)
            assertTrue(reviewAssignmentCurrent(current, task))
        }
    }

    @Test fun historicalEpochProofCannotAcceptUnrelatedCurrentTeachingChanges() {
        val before = RecordedLiteracyEpochScope.beforeCorrection(root, current)
        val changed = current.copy(units = current.units.map { u -> if (u.number != 4) u else
            u.copy(sessions = u.sessions.map { session -> session.copy(steps = session.steps.map { step ->
                if (step.kind == "TEACH") step.copy(body = step.body + " changed") else step
            }) }) })
        assertTrue(runCatching { RecordedLiteracyEpochScope.beforeCorrection(root, changed) }.exceptionOrNull() is AssertionError)
        assertTrue(runCatching { RecordedCurriculumConservationScope.assertCurrent(root, before) }.exceptionOrNull() is AssertionError)
    }
    private fun context(): Context = ApplicationProvider.getApplicationContext<Context>().also {
        (it.applicationContext as RussianCompanionApplication).closeStartupStoreForTests()
    }
    private fun hash(bytes: ByteArray) = MessageDigest.getInstance("SHA-256").digest(bytes).joinToString("") { "%02x".format(it) }
    companion object {
        private val root = File(if (File("app").isDirectory) "." else "..")
        private val current by lazy { V2PackLoader.parse(File(root, "app/src/main/assets/content/v2-curriculum.json").readText()).also { V2PackIdentityIndex.warm(it) } }
    }
}
