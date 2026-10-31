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

/** Genuine synthetic-learner history generated with the pre-correction curriculum. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class LiteracyHistoryCaptureTest {
    @Test fun preCorrectionLiteracyAndDependentReviewHistoryRoundTripWithoutRelabelling() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        (context.applicationContext as RussianCompanionApplication).closeStartupStoreForTests()
        val p = RecordedIntakeCorrectionScope.load(root, baselineCommit)
        V2PackIdentityIndex.warm(p)
        val output = System.getProperty("audit.literacy.capture.dir")?.let {
            require(it.startsWith("/workspace/artifacts/"))
            File(it).also { d -> check(!d.exists()); check(d.mkdirs()) }
        }
        if (output != null) assertEquals(baselineAssetHash, hash(File(root, "app/src/main/assets/content/v2-curriculum.json").readBytes()))
        var captures = 0
        LocalStore.openInMemory(context).use { store ->
            store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = 1))
            fun capture(label: String, completed: Set<Int>) {
                val original = store.governedLearnerDocument()
                assertEquals(completed, currentCompletedUnitNumbers(p, store.v2LearnerView()))
                val bytes = ByteArrayOutputStream().also {
                    writeGovernedBackup(it, "dev.russiancompanion", p.packId, p.packVersion, "DARK", original, emptyList())
                }.toByteArray()
                val stage = File(context.cacheDir, "literacy-$label-${System.nanoTime()}")
                val name = "literacy-$label-${System.nanoTime()}.db"
                try {
                    val read = readGovernedBackup(bytes.inputStream(), "dev.russiancompanion", p.packId, p.packVersion, stage)
                    assertTrue(label, read is BackupReadResult.Valid)
                    val decoded = (read as BackupReadResult.Valid).backup.document
                    assertEquals(label, original, decoded)
                    LocalStore.openNamed(context, name).use { target ->
                        assertEquals(GovernedReplaceOutcome.REPLACED, target.replaceGovernedLearner(decoded, emptyList()))
                        assertEquals(original, target.governedLearnerDocument())
                    }
                    LocalStore.openNamed(context, name).use { target ->
                        assertEquals(label, original, target.governedLearnerDocument())
                        assertEquals(label, completed, currentCompletedUnitNumbers(p, target.v2LearnerView()))
                        assertEquals(original.reviewAssignments to original.reviewAnswers, target.cumulativeReviewHistory())
                    }
                } finally {
                    stage.deleteRecursively(); context.deleteDatabase(name)
                    File(context.filesDir, "recordings-$name").deleteRecursively()
                }
                output?.let { d ->
                    File(d, "$label.zip").writeBytes(bytes)
                    File(d, "$label.IDENTITY.json").writeText(buildJsonObject {
                        put("producerCommit", producerCommit()); put("curriculumSourceCommit", baselineCommit)
                        put("rawCurriculumSHA256", baselineAssetHash); put("syntheticLearner", true)
                        put("oldArchiveRelabelled", false); put("learnerAcceptanceEvidence", false)
                        put("backupFormatVersion", BACKUP_FORMAT_VERSION); put("zipSHA256", hash(bytes)); put("zipBytes", bytes.size)
                        put("profileUnit", original.profile.currentUnitNumber); put("recordingCount", original.recordings.size)
                        putJsonArray("completedUnits") { completed.sorted().forEach { add(it) } }
                        putJsonArray("reviewSchemas") { original.reviewAssignments.forEach { add(it.schema) } }
                        putJsonArray("reviewAssignments") { original.reviewAssignments.forEach { add(it.id) } }
                        putJsonArray("unitDigests") { (1..3).forEach { add(V2Validator.unitContentReviewDigest(p, it)) } }
                    }.toString() + "\n")
                }
                captures++
            }
            capture("BEFORE_UNIT1", emptySet())
            val step = p.unit(1)!!.sessions.flatMap { it.steps }.first { it.itemIds.isNotEmpty() }
            val item = p.item(step.itemIds.first())!!
            store.v2RecordGovernedAttempt(prepareGovernedAttempt(p, item.id, item.answer, PRESCRIBED_CHECK, step.id, "literacy-partial")!!)
            store.v2CompleteGovernedStep(step.id, governedStepCompletionDigest(p, store.v2LearnerView(), step.id)!!, "literacy-partial")
            capture("PARTIAL_UNIT1", emptySet())
            Unit8PresentFixtures.ordinary(store, 1, p); assertTrue(advance(store, p, 1))
            capture("COMPLETED_UNIT1", setOf(1))
            val second = p.unit(2)!!.sessions.flatMap { it.steps }.first { it.kind == "TEACH" }
            store.v2CompleteGovernedStep(second.id, governedStepCompletionDigest(p, store.v2LearnerView(), second.id)!!, "literacy-partial")
            capture("PARTIAL_UNIT2", setOf(1))
            Unit8PresentFixtures.ordinary(store, 2, p)
            assertTrue(advancementDecision(p, p.unit(2)!!, store.v2LearnerView()).allowed)
            capture("READY_UNIT2", setOf(1)); assertTrue(advance(store, p, 2))
            capture("COMPLETED_UNIT2", setOf(1, 2))
            Unit8PresentFixtures.ordinary(store, 3, p)
            val task = store.openCumulativeReview(p, 3, store.learnerMutationEpoch())!!
            assertTrue(task.slots.size > 1)
            assertTrue(task.slots.all { it.ownerUnit in 1..2 })
            capture("UNSTARTED_REVIEW3", setOf(1, 2))
            task.slots.indices.forEach { index ->
                val answer = p.item(task.slots[index].itemId)!!.answer
                assertTrue(store.answerCumulativeReview(p, task.id, index, answer, store.learnerMutationEpoch()))
                if (index == 0) capture("PARTIAL_REVIEW3", setOf(1, 2))
            }
            capture("READY_UNIT3", setOf(1, 2)); assertTrue(advance(store, p, 3))
            capture("COMPLETED_UNIT3", setOf(1, 2, 3))
        }
        assertEquals(10, captures) // Ten archive scenarios, one JUnit method.
    }
    private fun advance(store: LocalStore, p: V2CurriculumPack, n: Int): Boolean = runBlocking {
        val next = p.unit(n + 1)!!.sessions.first().id
        store.v2AdvanceUnitIfCurrentAsync(store.learnerMutationEpoch(), n, "literacy-baseline", n + 1,
            next, resumePositionDigest(p, next, 0)!!, GOVERNED_IDENTITY_SCHEMA, p)
    }
    private fun producerCommit(): String {
        val process = ProcessBuilder("git", "rev-parse", "HEAD").directory(root).start()
        return process.inputStream.bufferedReader().readText().trim().also { check(process.waitFor() == 0) }
    }
    private fun hash(bytes: ByteArray) = MessageDigest.getInstance("SHA-256").digest(bytes).joinToString("") { "%02x".format(it) }
    companion object {
        private val root = File(if (File("app").isDirectory) "." else "..")
        private const val baselineCommit = "f653da03d1bc657e8acaeef52141552c531e42ce"
        private const val baselineAssetHash = "02202d37e0aa2c51182e4db22feff177124b6db2c270a67a93ef85e188916c19"
    }
}
