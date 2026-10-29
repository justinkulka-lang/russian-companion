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

/** Synthetic answers and book attestations, with genuine governed progression and backup APIs.
 * The positive preserves the full actual-current-at-capture pack, not an ordinal-only fixture.
 * The separate current-production contrast intentionally fails before the proposed v2 exists.
 * Neither host lifecycle nor a self-attestation demonstrates physical or linguistic acceptance.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class Unit14PluralPossessionReviewBaselineTest {
    @Test fun actualCurrentV1AndDownstreamUnit15RoundtripReopenAndCompleteTheirOwnObligations() {
        val p = baselinePack
        if (currentPack.unit(14)!!.advancement!!.cumulativeReviewPolicyId == v1) assertEquals(currentPack, p)
        val context = context()
        val name = "unit14-current-baseline-${System.nanoTime()}.db"
        var partial14: GovernedLearnerDocument? = null
        var partial15: GovernedLearnerDocument? = null
        var completed15: GovernedLearnerDocument? = null
        try {
            LocalStore.openNamed(context, name).use { store ->
                prepareThrough14(store, p)
                assertEquals(14, store.v2Profile().currentUnitNumber)
                assertEquals((1..13).toSet(), currentCompletedUnitNumbers(p, store.v2LearnerView()))
                val prior = store.cumulativeReviewHistory().first
                for ((unit, policy, schema) in listOf(
                    Triple(11, "unit11-prior-noun-behavior-possession-absence-v2", 13),
                    Triple(12, "unit12-prior-gender-past-gender-v2", 14),
                    Triple(13, "unit13-prior-present-aspect-past-gender-v3", 15))) {
                    val task = prior.single { it.targetUnit == unit }
                    assertEquals(policy, task.policyId); assertEquals(schema, task.schema)
                    assertTrue(cumulativeReviewComplete(p, store.v2LearnerView(), unit))
                }
                assertOrdinary14(store, p)
                val task = open(store, p, 14)
                assertEquals(v1, task.policyId); assertEquals(1, task.schema)
                assertEquals(listOf("obj-u1-decoding", "obj-u13-reported"), task.slots.map { it.objectiveId })
                assertEquals(listOf(1, 13), task.slots.map { it.ownerUnit })
                assertEquals(listOf("u1-i-decode-pair", "u13-i-report-frame-time"), task.slots.map { it.itemId })
                assertOwnerDigests(p, task)
                answer(store, p, task, 0)
                assertEquals(setOf(0), completedReviewSlots(p, task, store.v2LearnerView().reviewAnswers))
                assertFalse(advancementDecision(p, p.unit(14)!!, store.v2LearnerView()).allowed)
                partial14 = store.governedLearnerDocument()
                assertTrue(partial14!!.unitCompletions.none { it.unitNumber == 14 })
                assertTrue(partial14!!.recordings.isEmpty())
                val bytes = capture("PARTIAL_UNIT14_V1", store, p, task)
                roundtripNamed(context, p, partial14!!, bytes, task)
            }
            LocalStore.openNamed(context, name).use { store ->
                assertEquals(partial14, store.governedLearnerDocument())
                val task = open(store, p, 14)
                assertEquals(partial14!!.reviewAssignments.single { it.targetUnit == 14 }, task)
                assertEquals(setOf(0), completedReviewSlots(p, task, store.v2LearnerView().reviewAnswers))
                answer(store, p, task, 1)
                assertTrue(advance(store, p, 14))
                assertEquals(15, store.v2Profile().currentUnitNumber)
                assertEquals((1..14).toSet(), currentCompletedUnitNumbers(p, store.v2LearnerView()))
                val completed14 = store.governedLearnerDocument()
                val row = completed14.unitCompletions.single { it.unitNumber == 14 }
                assertEquals(task.id, row.reviewAssignmentId); assertEquals(task.policyDigest, row.reviewPolicyDigest)
                assertTrue(row.reviewCompletionOrder > task.boundary)
                val completedBytes = capture("COMPLETED_UNIT14_V1", store, p, task)
                roundtripNamed(context, p, completed14, completedBytes, null)

                ordinary(store, p, 15)
                assertTrue(ordinaryUnitRequirements(p, p.unit(15)!!, store.v2LearnerView()).allowed)
                val downstream = open(store, p, 15)
                assertEquals("unit15-prior-reading-impersonal-state-v1", downstream.policyId)
                assertEquals(1, downstream.schema)
                assertEquals(listOf("obj-u1-decoding", "obj-u14-impersonal"), downstream.slots.map { it.objectiveId })
                assertEquals(listOf(1, 14), downstream.slots.map { it.ownerUnit })
                assertEquals("u14-i-impersonal-2", downstream.slots[1].itemId)
                assertOwnerDigests(p, downstream)
                answer(store, p, downstream, 0)
                assertEquals(setOf(0), completedReviewSlots(p, downstream, store.v2LearnerView().reviewAnswers))
                assertFalse(advancementDecision(p, p.unit(15)!!, store.v2LearnerView()).allowed)
                partial15 = store.governedLearnerDocument()
                assertTrue(partial15!!.unitCompletions.none { it.unitNumber == 15 })
                assertEquals(completed14.unitCompletions, partial15!!.unitCompletions)
                val bytes = capture("PARTIAL_UNIT15_OWNER14_V1", store, p, downstream)
                roundtripNamed(context, p, partial15!!, bytes, downstream)
            }
            LocalStore.openNamed(context, name).use { store ->
                assertEquals(partial15, store.governedLearnerDocument())
                val task = open(store, p, 15)
                assertEquals(partial15!!.reviewAssignments.single { it.targetUnit == 15 }, task)
                assertEquals(setOf(0), completedReviewSlots(p, task, store.v2LearnerView().reviewAnswers))
                answer(store, p, task, 1)
                assertTrue(advance(store, p, 15))
                assertEquals(16, store.v2Profile().currentUnitNumber)
                assertEquals((1..15).toSet(), currentCompletedUnitNumbers(p, store.v2LearnerView()))
                completed15 = store.governedLearnerDocument()
                val row = completed15!!.unitCompletions.single { it.unitNumber == 15 }
                assertEquals(task.id, row.reviewAssignmentId); assertEquals(task.policyDigest, row.reviewPolicyDigest)
                assertTrue(row.reviewCompletionOrder > task.boundary)
            }
            LocalStore.openNamed(context, name).use { store ->
                assertEquals(completed15, store.governedLearnerDocument())
                assertEquals(16, store.v2Profile().currentUnitNumber)
                assertEquals((1..15).toSet(), currentCompletedUnitNumbers(p, store.v2LearnerView()))
                captureDirectory()?.let { directory ->
                    File(directory, "COMPLETED_UNIT15_V1.json").writeText(
                        backupJson.encodeToString(GovernedLearnerDocument.serializer(), completed15!!))
                    File(directory, "LIFECYCLE_VERIFIED.json").writeText(buildJsonObject {
                        put("producerCommit", producerCommit()); put("curriculumSourceCommit", baselineCommit)
                        put("partial14ReadReplaceNamedReopen", true); put("completed14ReadReplaceNamedReopen", true)
                        put("partial15ReadReplaceNamedReopen", true); put("actualAdvance14To15", true)
                        put("actualAdvance15To16", true); put("completed15NamedReopen", true)
                        put("physicalAcceptance", "NOT_EXECUTED_OUTSTANDING_LAST")
                    }.toString() + "\n")
                }
            }
        } finally { deleteStore(context, name) }
    }

    @Test fun earlierPluralPossessionAndOptionalAnswersCannotReplaceFreshAssignedDueAnswers() {
        val p = currentPack
        val context = context(); val name = "unit14-future-contrast-${System.nanoTime()}.db"
        try {
            LocalStore.openNamed(context, name).use { store ->
                prepareThrough14(store, p)
                assertOrdinary14(store, p)
                val prior = store.cumulativeReviewHistory().first
                assertTrue(prior.single { it.targetUnit == 9 }.slots.any { it.objectiveId == "obj-u8-plural" })
                assertTrue(prior.single { it.targetUnit == 11 }.slots.any { it.objectiveId == "obj-u10-have" })
                val task = open(store, p, 14)
                for (id in (1..6).map { "u8-i-plural-$it" } + (1..4).map { "u10-i-have-$it" }) {
                    store.v2RecordGovernedAttempt(prepareGovernedAttempt(p, id, p.item(id)!!.answer,
                        OPTIONAL_PRACTICE, practiceStepId("mixed"), "unit14-future-contrast")!!)
                }
                assertTrue(completedReviewSlots(p, task, store.v2LearnerView().reviewAnswers).isEmpty())
                assertFalse(advancementDecision(p, p.unit(14)!!, store.v2LearnerView()).allowed)
                task.slots.forEachIndexed { index, slot ->
                    if (slot.objectiveId !in setOf("obj-u8-plural", "obj-u10-have")) answer(store, p, task, index)
                }
                val before = store.governedLearnerDocument()
                val advancedWithoutNewAnswers = try { advance(store, p, 14) } catch (refusal: IllegalStateException) {
                    if (refusal.message != "current advancement evidence is incomplete") throw refusal
                    false
                }
                if (!advancedWithoutNewAnswers) assertEquals(before, store.governedLearnerDocument())
                println("UNIT14_BASELINE_OBSERVATION policy=${task.policyId} schema=${task.schema} objectives=${task.slots.map { it.objectiveId }} actualAdvanceWithoutNewPluralPossession=$advancedWithoutNewAnswers")
                // Expected failure on valid v1: these proposed assignment-bound slots do not exist.
                assertFalse("The proposed Unit14 obligation must refuse advancement without its fresh assigned plural and possession answers", advancedWithoutNewAnswers)
                assertEquals("unit14-prior-plural-possession-reported-time-v2", task.policyId)
                assertEquals(16, task.schema)
                assertEquals(listOf("obj-u8-plural", "obj-u10-have", "obj-u13-reported"), task.slots.map { it.objectiveId })
                assertEquals(listOf(8, 10, 13), task.slots.map { it.ownerUnit })
                answer(store, p, task, 0); answer(store, p, task, 1)
                assertTrue(advance(store, p, 14))
            }
        } finally { deleteStore(context, name) }
    }

    private fun prepareThrough14(store: LocalStore, p: V2CurriculumPack) {
        assertEquals(1, store.v2Profile().currentUnitNumber)
        store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true))
        for (n in 1..14) {
            assertEquals(n, store.v2Profile().currentUnitNumber)
            ordinary(store, p, n)
            assertTrue(ordinaryUnitRequirements(p, p.unit(n)!!, store.v2LearnerView()).allowed)
            if (n < 14) {
                if (n >= 3) {
                    val task = open(store, p, n)
                    task.slots.indices.forEach { answer(store, p, task, it) }
                }
                assertTrue(advance(store, p, n))
            }
        }
    }
    private fun ordinary(store: LocalStore, p: V2CurriculumPack, n: Int) {
        for (step in p.unit(n)!!.sessions.flatMap { it.steps }) {
            step.itemIds.forEach { id -> store.v2RecordGovernedAttempt(prepareGovernedAttempt(p, id,
                p.item(id)!!.answer, PRESCRIBED_CHECK, step.id, "unit14-baseline")!!) }
            store.v2CompleteGovernedStep(step.id,
                governedStepCompletionDigest(p, store.v2LearnerView(), step.id)!!, "unit14-baseline")
        }
        store.v2AttestBook(n, "unit14-baseline")
    }
    private fun assertOrdinary14(store: LocalStore, p: V2CurriculumPack) {
        val unit = p.unit(14)!!; val steps = unit.sessions.flatMap { it.steps }
        assertEquals(6, unit.advancement!!.minimumDistinctChecks)
        assertEquals(6, steps.filter { it.kind == "CHECK" }.flatMap { it.itemIds }.distinct().size)
        assertEquals(3, steps.filter { it.kind == "GUIDED" }.flatMap { it.itemIds }.distinct().size)
        assertTrue(ordinaryUnitRequirements(p, unit, store.v2LearnerView()).allowed)
    }
    private fun assertOwnerDigests(p: V2CurriculumPack, task: CumulativeReviewAssignment) {
        task.slots.forEach { assertEquals(V2Validator.unitContentReviewDigest(p, it.ownerUnit), it.unitDigest) }
        assertTrue(reviewAssignmentCurrent(p, task))
    }
    private fun open(store: LocalStore, p: V2CurriculumPack, n: Int) =
        store.openCumulativeReview(p, n, store.learnerMutationEpoch())!!
    private fun answer(store: LocalStore, p: V2CurriculumPack, task: CumulativeReviewAssignment, index: Int) {
        assertTrue(store.answerCumulativeReview(p, task.id, index,
            p.item(task.slots[index].itemId)!!.answer, store.learnerMutationEpoch()))
    }
    private fun advance(store: LocalStore, p: V2CurriculumPack, n: Int): Boolean = runBlocking {
        val next = p.unit(n + 1)!!.sessions.first().id
        store.v2AdvanceUnitIfCurrentAsync(store.learnerMutationEpoch(), n, "unit14-baseline", n + 1,
            next, resumePositionDigest(p, next, 0)!!, GOVERNED_IDENTITY_SCHEMA, p)
    }
    private fun roundtripNamed(context: Context, p: V2CurriculumPack, document: GovernedLearnerDocument,
                               bytes: ByteArray, task: CumulativeReviewAssignment?) {
        val name = "unit14-import-${System.nanoTime()}.db"; val stage = File(context.cacheDir, name)
        try {
            val read = readGovernedBackup(bytes.inputStream(), "dev.russiancompanion", p.packId, p.packVersion, stage)
            assertTrue(read is BackupReadResult.Valid)
            assertEquals(document, (read as BackupReadResult.Valid).backup.document)
            LocalStore.openNamed(context, name).use { target ->
                assertEquals(GovernedReplaceOutcome.REPLACED, target.replaceGovernedLearner(read.backup.document, emptyList()))
                assertEquals(document, target.governedLearnerDocument())
            }
            LocalStore.openNamed(context, name).use { target ->
                assertEquals(document, target.governedLearnerDocument())
                if (task != null) assertEquals(task, open(target, p, task.targetUnit))
                assertEquals(document, target.governedLearnerDocument())
            }
        } finally { stage.deleteRecursively(); deleteStore(context, name) }
    }
    private fun capture(name: String, store: LocalStore, p: V2CurriculumPack, task: CumulativeReviewAssignment): ByteArray {
        val document = store.governedLearnerDocument()
        val bytes = ByteArrayOutputStream().also {
            writeGovernedBackup(it, "dev.russiancompanion", p.packId, p.packVersion, "DARK", document, emptyList())
        }.toByteArray()
        captureDirectory()?.let { directory ->
            File(directory, "$name.zip").writeBytes(bytes)
            File(directory, "$name.IDENTITY.json").writeText(buildJsonObject {
                put("producerCommit", producerCommit()); put("curriculumSourceCommit", baselineCommit)
                put("targetUnit", task.targetUnit); put("policyId", task.policyId); put("assignmentSchema", task.schema)
                put("policyDigest", task.policyDigest); put("assignmentDigest", task.digest)
                put("backupFormatVersion", BACKUP_FORMAT_VERSION); put("documentSchemaVersion", document.schemaVersion)
                put("recordingCount", document.recordings.size); put("profileUnit", document.profile.currentUnitNumber)
                put("rawCurriculumSHA256", hash(File(root, "app/src/main/assets/content/v2-curriculum.json").readBytes()))
                put("zipSHA256", hash(bytes)); put("zipBytes", bytes.size)
                put("syntheticLearner", true); put("oldArchiveRelabelled", false)
            }.toString() + "\n")
        }
        return bytes
    }
    private fun captureDirectory(): File? = System.getProperty("audit.unit14.capture.dir")?.let {
        require(it.startsWith("/workspace/artifacts/"))
        File(it).also { directory -> check(directory.isDirectory || directory.mkdirs()) }
    }
    private fun producerCommit(): String {
        val git = ProcessBuilder("git", "rev-parse", "HEAD").directory(root).start()
        return git.inputStream.bufferedReader().readText().trim().also { check(git.waitFor() == 0) }
    }
    private fun context(): Context = ApplicationProvider.getApplicationContext<Context>().also {
        (it.applicationContext as RussianCompanionApplication).closeStartupStoreForTests()
    }
    private fun deleteStore(context: Context, name: String) {
        context.deleteDatabase(name); File(context.filesDir, "recordings-$name").deleteRecursively()
    }
    companion object {
        private const val baselineCommit = "9101af8c182b3415426ed9226cb211a707a2c393"
        private const val v1 = "unit14-prior-reading-reported-time-v1"
        private val root = File(if (File("app").isDirectory) "." else "..")
        private val currentPack by lazy { V2PackLoader.parse(File(root, "app/src/main/assets/content/v2-curriculum.json").readText()).also { V2PackIdentityIndex.warm(it) } }
        private val baselinePack by lazy { RecordedIntakeCorrectionScope.load(root, baselineCommit) }
        private fun hash(bytes: ByteArray) = MessageDigest.getInstance("SHA-256").digest(bytes).joinToString("") { "%02x".format(it) }
    }
}
