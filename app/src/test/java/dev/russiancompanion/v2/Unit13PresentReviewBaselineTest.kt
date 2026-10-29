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

/** Synthetic ordinary answers; genuine current store/backup APIs. No device acceptance.
 * Positive baseline deliberately preserves the full pre-v3 production pack, not an older Unit13
 * fixture. At first execution that pack equals the actual current producer's entire curriculum.
 * The separate contrast always uses current production and is expected to fail before v3.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class Unit13PresentReviewBaselineTest {
    @Test fun actualCurrentV2PartialTaskRoundtripsReopensAndCompletesItsOwnObligation() {
        val pack = baselinePack
        if (currentPack.unit(13)!!.advancement!!.cumulativeReviewPolicyId == v2) assertEquals(currentPack, pack)
        val context = context()
        val name = "unit13-current-partial-${System.nanoTime()}"
        val stage = File(context.cacheDir, name)
        var partial: GovernedLearnerDocument? = null
        var completed: GovernedLearnerDocument? = null
        try {
            LocalStore.openNamed(context, name).use { store ->
                prepare(store, pack)
                assertTrue(ordinaryUnitRequirements(pack, pack.unit(13)!!, store.v2LearnerView()).allowed)
                assertEquals(8, pack.unit(13)!!.advancement!!.minimumDistinctChecks)
                assertEquals(9, pack.unit(13)!!.sessions.flatMap { it.steps }.filter { it.kind == "CHECK" }.flatMap { it.itemIds }.distinct().size)
                assertEquals(4, pack.unit(13)!!.sessions.flatMap { it.steps }.filter { it.kind == "GUIDED" }.flatMap { it.itemIds }.distinct().size)
                assertEquals((1..12).toSet(), currentCompletedUnitNumbers(pack, store.v2LearnerView()))
                val prerequisites = store.cumulativeReviewHistory().first
                assertEquals("unit11-prior-noun-behavior-possession-absence-v2", prerequisites.single { it.targetUnit == 11 }.policyId)
                assertEquals(13, prerequisites.single { it.targetUnit == 11 }.schema)
                assertEquals("unit12-prior-gender-past-gender-v2", prerequisites.single { it.targetUnit == 12 }.policyId)
                assertEquals(14, prerequisites.single { it.targetUnit == 12 }.schema)
                val task = open(store, pack)
                assertEquals(v2, task.policyId); assertEquals(6, task.schema)
                assertEquals(3, task.slots.size)
                assertEquals(listOf("obj-u1-decoding", "obj-u12-aspect", "obj-u11-past-aspect"), task.slots.map { it.objectiveId })
                assertEquals(listOf(1, 12, 11), task.slots.map { it.ownerUnit })
                assertEquals("u1-i-decode-pair", task.slots[0].itemId)
                assertEquals("u12-i-aspect-1", task.slots[1].itemId)
                assertTrue(task.slots[2].itemId in listOf("u11-i-past-aspect-1", "u11-i-past-aspect-2"))
                task.slots.forEach { assertEquals(V2Validator.unitContentReviewDigest(pack, it.ownerUnit), it.unitDigest) }
                answer(store, task, 0, pack = pack)
                assertEquals(setOf(0), completedReviewSlots(pack, task, store.v2LearnerView().reviewAnswers))
                assertFalse(advancementDecision(pack, pack.unit(13)!!, store.v2LearnerView()).allowed)
                partial = store.governedLearnerDocument()
                assertTrue(partial!!.unitCompletions.none { it.unitNumber == 13 })
                assertTrue(partial!!.recordings.isEmpty())
                val bytes = archive(store, pack)
                capture(task, partial!!, bytes)
                val read = readGovernedBackup(bytes.inputStream(), "dev.russiancompanion", pack.packId, pack.packVersion, stage)
                assertTrue(read is BackupReadResult.Valid)
                assertEquals(partial, (read as BackupReadResult.Valid).backup.document)
                LocalStore.openInMemory(context).use { target ->
                    assertEquals(GovernedReplaceOutcome.REPLACED, target.replaceGovernedLearner(read.backup.document, emptyList()))
                    assertEquals(partial, target.governedLearnerDocument())
                    assertEquals(task, open(target, pack))
                }
            }
            LocalStore.openNamed(context, name).use { store ->
                assertEquals(partial, store.governedLearnerDocument())
                val task = open(store, pack)
                assertEquals(partial!!.reviewAssignments.single { it.targetUnit == 13 }, task)
                assertEquals(setOf(0), completedReviewSlots(pack, task, store.v2LearnerView().reviewAnswers))
                for (index in 1 until task.slots.size) answer(store, task, index, pack = pack)
                assertTrue(advance(store, 13, pack))
                assertEquals(14, store.v2Profile().currentUnitNumber)
                assertEquals((1..13).toSet(), currentCompletedUnitNumbers(pack, store.v2LearnerView()))
                completed = store.governedLearnerDocument()
                val row = completed!!.unitCompletions.single { it.unitNumber == 13 }
                assertEquals(task.id, row.reviewAssignmentId); assertEquals(task.policyDigest, row.reviewPolicyDigest)
                assertTrue(row.reviewCompletionOrder > task.boundary)
            }
            LocalStore.openNamed(context, name).use { store ->
                assertEquals(completed, store.governedLearnerDocument())
                assertEquals((1..13).toSet(), currentCompletedUnitNumbers(pack, store.v2LearnerView()))
                assertEquals(14, store.v2Profile().currentUnitNumber)
                System.getProperty("audit.unit13.capture.dir")?.let { path ->
                    require(path.startsWith("/workspace/artifacts/")); check(File(path).isDirectory)
                    File(path, "COMPLETED_UNIT13_V2.json").writeText(backupJson.encodeToString(GovernedLearnerDocument.serializer(), completed!!))
                }
            }
        } finally {
            stage.deleteRecursively(); context.deleteDatabase(name)
            File(context.filesDir, "recordings-$name").deleteRecursively()
        }
    }

    @Test fun priorPrintAspectAndPastAnswersCannotReplaceFreshAssignedPresentAnswer() {
        val pack = currentPack
        LocalStore.openInMemory(context()).use { store ->
            prepare(store, pack)
            assertTrue(ordinaryUnitRequirements(pack, pack.unit(13)!!, store.v2LearnerView()).allowed)
            val task = open(store, pack)
            // Earlier present knowledge is already demonstrated; this is a new assignment-bound
            // proposed requirement, not a missing ordinary prerequisite or existing gate bypass.
            assertTrue(store.cumulativeReviewHistory().first.single { it.targetUnit == 10 }
                .slots.any { it.objectiveId == "obj-u4-present" })
            task.slots.forEach { slot ->
                val item = pack.item(slot.itemId)!!
                store.v2RecordGovernedAttempt(prepareGovernedAttempt(pack, item.id, item.answer,
                    OPTIONAL_PRACTICE, practiceStepId("mixed"), "unit13-present")!!)
            }
            assertTrue(completedReviewSlots(pack, task, store.v2LearnerView().reviewAnswers).isEmpty())
            assertFalse(advancementDecision(pack, pack.unit(13)!!, store.v2LearnerView()).allowed)
            task.slots.forEachIndexed { index, slot ->
                if (slot.objectiveId != "obj-u4-present") answer(store, task, index, pack = pack)
            }
            val before = store.governedLearnerDocument()
            val advancedWithoutPresent = try { advance(store, 13, pack) } catch (refusal: IllegalStateException) {
                if (refusal.message != "current advancement evidence is incomplete") throw refusal
                false
            }
            if (!advancedWithoutPresent) assertEquals(before, store.governedLearnerDocument())
            println("UNIT13_BASELINE_OBSERVATION policy=${task.policyId} schema=${task.schema} objectives=${task.slots.map { it.objectiveId }} actualAdvanceWithoutPresent=$advancedWithoutPresent")
            // Intentional failure on legitimate current v2: no present-form slot is declared.
            assertFalse("The proposed Unit13 obligation must refuse advancement without its fresh assigned present-form answer", advancedWithoutPresent)
            assertEquals("unit13-prior-present-aspect-past-gender-v3", task.policyId)
            assertEquals(15, task.schema)
            val policy = cumulativeReviewDeclaredPolicy(pack, 13)!!
            assertEquals(listOf("obj-u4-present", "obj-u12-aspect", "obj-u11-past-aspect"), policy.pool.map { it.objectiveId })
            assertEquals(listOf("u4-i-verb-говорить-мы", "u4-i-verb-видеть-он", "u4-i-verb-писать-вы", "u4-i-verb-идти-они"), policy.pool[0].candidateItemIds)
            assertEquals(listOf("u12-i-aspect-1", "u12-i-aspect-2"), policy.pool[1].candidateItemIds)
            assertEquals(listOf("u11-i-past-aspect-1", "u11-i-past-aspect-2"), policy.pool[2].candidateItemIds)
            assertEquals(listOf(4, 12, 11), task.slots.map { it.ownerUnit })
            answer(store, task, 0, pack = pack)
            assertTrue(advance(store, 13, pack))
        }
    }

    private fun capture(task: CumulativeReviewAssignment, document: GovernedLearnerDocument, bytes: ByteArray) {
        val path = System.getProperty("audit.unit13.capture.dir") ?: return
        require(path.startsWith("/workspace/artifacts/"))
        val directory = File(path).also { check(it.mkdirs()) }
        val text = backupJson.encodeToString(GovernedLearnerDocument.serializer(), document)
        File(directory, "PARTIAL_UNIT13_V2.zip").writeBytes(bytes)
        File(directory, "PARTIAL_UNIT13_V2.json").writeText(text)
        val git = ProcessBuilder("git", "rev-parse", "HEAD").directory(root).start()
        val commit = git.inputStream.bufferedReader().readText().trim(); check(git.waitFor() == 0)
        File(directory, "IDENTITY.json").writeText(buildJsonObject {
            put("producerCommit", commit); put("curriculumSourceCommit", baselineCommit)
            put("policyId", task.policyId); put("assignmentSchema", task.schema); put("targetUnit", task.targetUnit)
            put("policyDigest", task.policyDigest); put("assignmentDigest", task.digest)
            put("documentSchemaVersion", document.schemaVersion); put("recordingCount", document.recordings.size)
            put("rawCurriculumSHA256", hash(File(root, "app/src/main/assets/content/v2-curriculum.json").readBytes()))
            put("zipSHA256", hash(bytes)); put("zipBytes", bytes.size); put("documentSHA256", hash(text.toByteArray()))
            put("syntheticLearner", true); put("oldArchiveRelabelled", false)
        }.toString() + "\n")
    }
    private fun prepare(store: LocalStore, pack: V2CurriculumPack) {
        for (n in 1..13) {
            store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = n, currentSessionId = null))
            for (step in pack.unit(n)!!.sessions.flatMap { it.steps }) {
                step.itemIds.forEach { id -> store.v2RecordGovernedAttempt(prepareGovernedAttempt(pack,
                    id, pack.item(id)!!.answer, PRESCRIBED_CHECK, step.id, "unit13-present")!!) }
                store.v2CompleteGovernedStep(step.id, governedStepCompletionDigest(pack, store.v2LearnerView(), step.id)!!, "unit13-present")
            }
            store.v2AttestBook(n, "unit13-present")
            if (n < 13) {
                if (n >= 3) {
                    val task = store.openCumulativeReview(pack, n, store.learnerMutationEpoch())!!
                    task.slots.indices.forEach { answer(store, task, it, pack = pack) }
                }
                assertTrue(advance(store, n, pack))
            }
        }
    }
    private fun open(store: LocalStore, pack: V2CurriculumPack) = store.openCumulativeReview(pack, 13, store.learnerMutationEpoch())!!
    private fun answer(store: LocalStore, task: CumulativeReviewAssignment, index: Int, correct: Boolean = true, pack: V2CurriculumPack) {
        val item = pack.item(task.slots[index].itemId)!!
        assertTrue(store.answerCumulativeReview(pack, task.id, index,
            if (correct) item.answer else item.options.first { it != item.answer }, store.learnerMutationEpoch()))
    }
    private fun advance(store: LocalStore, n: Int, pack: V2CurriculumPack): Boolean = runBlocking {
        val session = pack.unit(n + 1)!!.sessions.first().id
        store.v2AdvanceUnitIfCurrentAsync(store.learnerMutationEpoch(), n, "unit13-present", n + 1,
            session, resumePositionDigest(pack, session, 0)!!, GOVERNED_IDENTITY_SCHEMA, pack)
    }
    private fun archive(store: LocalStore, pack: V2CurriculumPack): ByteArray = ByteArrayOutputStream().also {
        writeGovernedBackup(it, "dev.russiancompanion", pack.packId, pack.packVersion, "DARK", store.governedLearnerDocument(), emptyList())
    }.toByteArray()
    private fun context(): Context = ApplicationProvider.getApplicationContext<Context>().also {
        (it.applicationContext as RussianCompanionApplication).closeStartupStoreForTests()
    }
    companion object {
        private const val baselineCommit = "31ac807387253082c44bd2f6d67021478f0df66e"
        private const val v2 = "unit13-prior-reading-aspect-past-gender-v2"
        private val root = File(if (File("app").isDirectory) "." else "..")
        private val currentPack by lazy { V2PackLoader.parse(File(root, "app/src/main/assets/content/v2-curriculum.json").readText()).also { V2PackIdentityIndex.warm(it) } }
        private val baselinePack by lazy { RecordedIntakeCorrectionScope.load(root, baselineCommit) }
        private fun hash(bytes: ByteArray) = MessageDigest.getInstance("SHA-256").digest(bytes).joinToString("") { "%02x".format(it) }
    }
}
