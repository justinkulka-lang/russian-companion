package dev.russiancompanion.v2

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.RussianCompanionApplication
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.backup.*
import dev.russiancompanion.v2.curriculum.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.ByteArrayOutputStream
import java.io.File
import java.security.MessageDigest
import java.util.zip.ZipFile

/** Replay the original archives against the actual current pack without rewriting their identities. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class CurrentPackHistoricalBackupTest {
    @Test fun allOriginalArchivesRestoreAndReopenButDoNotAcquireNewCredit() {
        // Original contract on actual pre-literacy source; current refusal has separate direct coverage.
        with(RecordedLiteracyEpochScope.context(Companion.pack, 6)) {
        val allowlist = Json.parseToJsonElement(File(root,
            "audit/independent-gpt61-2026-10-12/current-comprehensive-audit/RETAINED_ARCHIVE_ALLOWLIST.json").readText())
            .jsonObject.getValue("sha256").jsonObject
        val context = context()
        (context.applicationContext as RussianCompanionApplication).closeStartupStoreForTests()
        var cases = 0
        for ((path, expectedHash) in allowlist.toSortedMap()) {
            val archive = File(root, path)
            val expected = ZipFile(archive).use { zip ->
                if (zip.getEntry(BACKUP_MANIFEST_ENTRY) == null) null
                else backupJson.decodeFromString(GovernedLearnerDocument.serializer(),
                    zip.getInputStream(zip.getEntry(BACKUP_LEARNER_ENTRY)).use { it.readBytes() }.decodeToString())
            } ?: continue // Two allowlisted ZIPs contain historical test reports, not learner backups.
            assertEquals(path, expectedHash.jsonPrimitive.content, hash(archive.readBytes()))
            // Capture identities are reconciled in CAPTURE_IDENTITY_RECONCILIATION.json.
            // The policy-transition and Unit3-baseline captures predate corrected Unit3 teaching.
            // The five later capture families retain corrected Unit3, but predate corrected Unit4.
            val lastCurrent = when {
                "/ia003-policy-history-transition/" in path || "/ia003-unit3-gender-correction/" in path -> 2
                listOf("ia003-unit13-context-teaching", "ia003-unit13-history-transition",
                    "ia003-unit13-past-teaching", "ia003-unit7-agreement-teaching",
                    "ia003-unit9-history-transition").any { "/$it/" in path } -> 3
                else -> error("Unreconciled historical capture: $path")
            }
            val expectedCredit = (1..lastCurrent).toSet()
            val name = "current-history-${System.nanoTime()}.db"
            val stage = File(context.cacheDir, name)
            try {
                val decoded = archive.inputStream().use {
                    readGovernedBackup(it, "dev.russiancompanion", pack.packId, pack.packVersion, stage)
                }
                assertTrue(path, decoded is BackupReadResult.Valid)
                assertEquals(path, expected, (decoded as BackupReadResult.Valid).backup.document)
                fun check(store: LocalStore) {
                    assertEquals(path, expected, store.governedLearnerDocument())
                    val learner = store.v2LearnerView()
                    assertEquals(path, expectedCredit, currentCompletedUnitNumbers(pack, learner))
                    assertFalse(path, ordinaryUnitRequirements(pack, pack.unit(lastCurrent + 1)!!, learner).allowed)
                    assertNull(path, store.openCumulativeReview(pack, 6, store.learnerMutationEpoch()))
                    for (task in expected.reviewAssignments) {
                        if (!reviewAssignmentCurrent(pack, task)) {
                            assertTrue(path, completedReviewSlots(pack, task, learner.reviewAnswers).isEmpty())
                        }
                    }
                    assertEquals(path, expected, store.governedLearnerDocument())
                }
                LocalStore.openNamed(context, name).use { store ->
                    assertEquals(path, GovernedReplaceOutcome.REPLACED, store.replaceGovernedLearner(expected, emptyList()))
                    check(store)
                }
                LocalStore.openNamed(context, name).use { check(it) }
                assertEquals(path, expectedHash.jsonPrimitive.content, hash(archive.readBytes()))
                record(buildJsonObject {
                    put("kind", "original_archive")
                    put("path", path)
                    put("sha256", expectedHash.jsonPrimitive.content)
                    put("restoredAndReopenedUnchanged", true)
                    put("currentCompletedUnits", JsonArray(expectedCredit.sorted().map(::JsonPrimitive)))
                    put("reviewAssignments", expected.reviewAssignments.size)
                    put("reviewAnswers", expected.reviewAnswers.size)
                    put("unit6ReviewRefusedWithoutCurrentPrerequisites", true)
                })
                cases++
            } finally {
                stage.deleteRecursively()
                context.deleteDatabase(name)
                File(context.filesDir, "recordings-$name").deleteRecursively()
            }
        }
        assertEquals(64, cases)
        }
    }

    @Test fun legacyUnit5CanLeadIntoFreshUnit6WithoutRelabellingEitherObligation() {
        // Original v2 contract, with a whole-pack proof of its declaration-only fixture.
        val pack = RecordedCurriculumConservationScope.beforeUnit6PresentReview(root, Companion.pack)
        val context = context()
        (context.applicationContext as RussianCompanionApplication).closeStartupStoreForTests()
        val archive = File(root,
            "audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-cc020-unit5-grammar-2026-10-22/BASELINE_LEGACY_UNIT5.zip")
        val originalHash = hash(archive.readBytes())
        val name = "unit5-to-unit6-${System.nanoTime()}.db"
        val stage = File(context.cacheDir, name)
        var completed: GovernedLearnerDocument? = null
        try {
            val decoded = archive.inputStream().use {
                readGovernedBackup(it, "dev.russiancompanion", pack.packId, pack.packVersion, stage)
            }
            assertTrue(decoded is BackupReadResult.Valid)
            val original = (decoded as BackupReadResult.Valid).backup.document
            val legacy = original.reviewAssignments.single { it.targetUnit == 5 }
            assertEquals(3, legacy.schema)
            assertEquals("unit5-prior-reading-subject-v1", legacy.policyId)
            LocalStore.openNamed(context, name).use { store ->
                assertEquals(GovernedReplaceOutcome.REPLACED, store.replaceGovernedLearner(original, emptyList()))
                assertEquals(legacy, store.openCumulativeReview(pack, 5, store.learnerMutationEpoch()))
                val relabelled = legacy.copy(schema = 7).let { it.copy(digest = reviewAssignmentDigest(it)) }
                assertFalse(reviewAssignmentCurrent(pack, relabelled))
                assertNotNull(reviewHistoryDefects(listOf(relabelled), original.reviewAnswers.filter { it.assignmentId == legacy.id }))
                legacy.slots.indices.forEach { answer(store, legacy, it, pack) }
                assertTrue(advance(store, 5, pack))
                ordinary(store, 6, pack)
                val fresh = store.openCumulativeReview(pack, 6, store.learnerMutationEpoch())!!
                assertEquals(4, fresh.schema)
                assertEquals("unit6-prior-reading-subject-noun-behavior-v2", fresh.policyId)
                assertEquals(3, fresh.slots.size)
                val owner5 = fresh.slots.single { it.ownerUnit == 5 }
                assertEquals(V2Validator.unitContentReviewDigest(pack, 5), owner5.unitDigest)
                assertFalse(advancementDecision(pack, pack.unit(6)!!, store.v2LearnerView()).allowed)
                fresh.slots.indices.forEach { answer(store, fresh, it, pack) }
                assertTrue(advance(store, 6, pack))
                assertEquals((1..6).toSet(), currentCompletedUnitNumbers(pack, store.v2LearnerView()))
                completed = store.governedLearnerDocument()
                assertTrue(completed!!.reviewAssignments.containsAll(original.reviewAssignments))
                assertEquals(legacy, completed!!.reviewAssignments.single { it.targetUnit == 5 })
                val changedOwner = pack.copy(items = pack.items.map {
                    if (it.id == owner5.itemId) it.copy(feedback = it.feedback + " changed") else it
                })
                assertFalse(reviewAssignmentCurrent(changedOwner, fresh))
                assertFalse(6 in currentCompletedUnitNumbers(changedOwner, store.v2LearnerView()))
                val bytes = ByteArrayOutputStream().also {
                    writeGovernedBackup(it, "dev.russiancompanion", pack.packId, pack.packVersion,
                        "DARK", completed!!, emptyList())
                }.toByteArray()
                val roundtripStage = File(stage.parentFile, "$name-roundtrip")
                try {
                    val read = readGovernedBackup(bytes.inputStream(), "dev.russiancompanion", pack.packId, pack.packVersion, roundtripStage)
                    assertTrue(read is BackupReadResult.Valid)
                    LocalStore.openInMemory(context).use { target ->
                        assertEquals(GovernedReplaceOutcome.REPLACED, target.replaceGovernedLearner((read as BackupReadResult.Valid).backup.document, emptyList()))
                        assertEquals(completed, target.governedLearnerDocument())
                        assertEquals((1..6).toSet(), currentCompletedUnitNumbers(pack, target.v2LearnerView()))
                    }
                } finally { roundtripStage.deleteRecursively() }
            }
            LocalStore.openNamed(context, name).use { store ->
                assertEquals(completed, store.governedLearnerDocument())
                assertEquals((1..6).toSet(), currentCompletedUnitNumbers(pack, store.v2LearnerView()))
            }
            assertEquals(originalHash, hash(archive.readBytes()))
            record(buildJsonObject {
                put("kind", "legacy_unit5_to_current_unit6")
                put("scope", "HISTORICAL_PRE_UNIT6_V3_PACK")
                put("fixtureSource", RecordedCurriculumConservationScope.beforeUnit6PackImplementation)
                put("legacyArchiveSha256", originalHash)
                put("legacyUnit5Schema", 3)
                put("freshUnit6Schema", 4)
                put("restoredRoundtripAndReopenedUnchanged", true)
                put("wrongSchemaAndChangedOwnerRejected", true)
            })
        } finally {
            stage.deleteRecursively()
            context.deleteDatabase(name)
            File(context.filesDir, "recordings-$name").deleteRecursively()
        }
    }

    @Test fun legacyUnit5CanLeadIntoCurrentUnit6V3WithoutRelabellingEitherObligation() {
        // Original contract on actual pre-literacy source; current refusal has separate direct coverage.
        with(RecordedLiteracyEpochScope.context(Companion.pack, 6)) {
        val context = context()
        (context.applicationContext as RussianCompanionApplication).closeStartupStoreForTests()
        val archive = File(root,
            "audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-cc020-unit5-grammar-2026-10-22/BASELINE_LEGACY_UNIT5.zip")
        val originalHash = hash(archive.readBytes())
        val name = "unit5-to-unit6-v3-${System.nanoTime()}.db"
        val stage = File(context.cacheDir, name)
        var completed: GovernedLearnerDocument? = null
        try {
            val decoded = archive.inputStream().use {
                readGovernedBackup(it, "dev.russiancompanion", pack.packId, pack.packVersion, stage)
            }
            assertTrue(decoded is BackupReadResult.Valid)
            val original = (decoded as BackupReadResult.Valid).backup.document
            val legacy = original.reviewAssignments.single { it.targetUnit == 5 }
            assertEquals(3, legacy.schema)
            assertEquals("unit5-prior-reading-subject-v1", legacy.policyId)
            LocalStore.openNamed(context, name).use { store ->
                assertEquals(GovernedReplaceOutcome.REPLACED, store.replaceGovernedLearner(original, emptyList()))
                assertEquals(legacy, store.openCumulativeReview(pack, 5, store.learnerMutationEpoch()))
                val relabelled = legacy.copy(schema = 7).let { it.copy(digest = reviewAssignmentDigest(it)) }
                assertFalse(reviewAssignmentCurrent(pack, relabelled))
                assertNotNull(reviewHistoryDefects(listOf(relabelled), original.reviewAnswers.filter { it.assignmentId == legacy.id }))
                legacy.slots.indices.forEach { answer(store, legacy, it) }
                assertTrue(advance(store, 5))
                ordinary(store, 6)
                val fresh = store.openCumulativeReview(pack, 6, store.learnerMutationEpoch())!!
                assertEquals(8, fresh.schema)
                assertEquals("unit6-prior-present-subject-noun-behavior-v3", fresh.policyId)
                assertEquals(3, fresh.slots.size)
                assertEquals(listOf("obj-u4-present", "obj-u5-indeclinable", "obj-u4-nominative"), fresh.slots.map { it.objectiveId })
                assertTrue(fresh.slots.none { it.itemId == "u1-i-decode-pair" })
                val owner5 = fresh.slots.single { it.ownerUnit == 5 }
                assertEquals(V2Validator.unitContentReviewDigest(pack, 5), owner5.unitDigest)
                assertFalse(advancementDecision(pack, pack.unit(6)!!, store.v2LearnerView()).allowed)
                fresh.slots.indices.forEach { answer(store, fresh, it) }
                assertTrue(advance(store, 6))
                assertEquals((1..6).toSet(), currentCompletedUnitNumbers(pack, store.v2LearnerView()))
                completed = store.governedLearnerDocument()
                assertTrue(completed!!.reviewAssignments.containsAll(original.reviewAssignments))
                assertEquals(legacy, completed!!.reviewAssignments.single { it.targetUnit == 5 })
                val changedOwner = pack.copy(items = pack.items.map {
                    if (it.id == owner5.itemId) it.copy(feedback = it.feedback + " changed") else it
                })
                assertFalse(reviewAssignmentCurrent(changedOwner, fresh))
                assertFalse(6 in currentCompletedUnitNumbers(changedOwner, store.v2LearnerView()))
                val bytes = ByteArrayOutputStream().also {
                    writeGovernedBackup(it, "dev.russiancompanion", pack.packId, pack.packVersion,
                        "DARK", completed!!, emptyList())
                }.toByteArray()
                val roundtripStage = File(stage.parentFile, "$name-roundtrip")
                try {
                    val read = readGovernedBackup(bytes.inputStream(), "dev.russiancompanion", pack.packId, pack.packVersion, roundtripStage)
                    assertTrue(read is BackupReadResult.Valid)
                    LocalStore.openInMemory(context).use { target ->
                        assertEquals(GovernedReplaceOutcome.REPLACED, target.replaceGovernedLearner((read as BackupReadResult.Valid).backup.document, emptyList()))
                        assertEquals(completed, target.governedLearnerDocument())
                        assertEquals((1..6).toSet(), currentCompletedUnitNumbers(pack, target.v2LearnerView()))
                    }
                } finally { roundtripStage.deleteRecursively() }
            }
            LocalStore.openNamed(context, name).use { store ->
                assertEquals(completed, store.governedLearnerDocument())
                assertEquals((1..6).toSet(), currentCompletedUnitNumbers(pack, store.v2LearnerView()))
            }
            assertEquals(originalHash, hash(archive.readBytes()))
            record(buildJsonObject {
                put("kind", "legacy_unit5_to_current_unit6_v3")
                put("legacyArchiveSha256", originalHash)
                put("legacyUnit5Schema", 3)
                put("freshUnit6Schema", 8)
                put("restoredRoundtripAndReopenedUnchanged", true)
                put("wrongSchemaAndChangedOwnerRejected", true)
            })
        } finally {
            stage.deleteRecursively()
            context.deleteDatabase(name)
            File(context.filesDir, "recordings-$name").deleteRecursively()
        }
        }
    }

    private fun ordinary(store: LocalStore, number: Int, scope: V2CurriculumPack = pack) {
        val pack = scope
        store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = number, currentSessionId = null))
        for (step in pack.unit(number)!!.sessions.flatMap { it.steps }) {
            step.itemIds.forEach { id ->
                store.v2RecordGovernedAttempt(prepareGovernedAttempt(pack, id, pack.item(id)!!.answer, PRESCRIBED_CHECK, step.id, "compatibility")!!)
            }
            store.v2CompleteGovernedStep(step.id, governedStepCompletionDigest(pack, store.v2LearnerView(), step.id)!!, "compatibility")
        }
        store.v2AttestBook(number, "compatibility")
    }
    private fun answer(store: LocalStore, task: CumulativeReviewAssignment, index: Int, scope: V2CurriculumPack = pack) {
        val pack = scope
        assertTrue(store.answerCumulativeReview(pack, task.id, index, pack.item(task.slots[index].itemId)!!.answer, store.learnerMutationEpoch()))
    }
    private fun advance(store: LocalStore, number: Int, scope: V2CurriculumPack = pack): Boolean = runBlocking {
        val pack = scope
        val next = pack.unit(number + 1)!!.sessions.first().id
        store.v2AdvanceUnitIfCurrentAsync(store.learnerMutationEpoch(), number, "compatibility", number + 1,
            next, resumePositionDigest(pack, next, 0)!!, GOVERNED_IDENTITY_SCHEMA, pack)
    }
    private fun record(value: JsonObject) {
        System.getProperty("audit.history.output")?.let { path ->
            require(path.startsWith("/workspace/artifacts/"))
            File(path).appendText(value.toString() + "\n")
        }
    }
    private fun context(): Context = ApplicationProvider.getApplicationContext()
    companion object {
        private val root = File(if (File("app").isDirectory) "." else "..")
        private val pack by lazy {
            V2PackLoader.parse(File(root, "app/src/main/assets/content/v2-curriculum.json").readText()).also {
                V2PackIdentityIndex.warm(it)
                RecordedCurriculumConservationScope.assertCurrent(root, it)
            }
        }
        private fun hash(bytes: ByteArray) = MessageDigest.getInstance("SHA-256").digest(bytes).joinToString("") { "%02x".format(it) }
    }
}
