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

/** Actual current pack; synthetic ordinary work, real assignments and actual store advancement. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class Unit6PresentReviewTest {
    @Test fun nounAndSubjectAnswersCannotReplaceFreshAssignedPresentAnswer() {
        LocalStore.openInMemory(context()).use { store ->
            prepare(store)
            val task = open(store)
            assertTrue(ordinaryUnitRequirements(pack, pack.unit(6)!!, store.v2LearnerView()).allowed)
            task.slots.forEach { slot ->
                val item = pack.item(slot.itemId)!!
                store.v2RecordGovernedAttempt(prepareGovernedAttempt(pack, item.id, item.answer,
                    OPTIONAL_PRACTICE, practiceStepId("mixed"), "unit6-present")!!)
            }
            assertTrue(completedReviewSlots(pack, task, store.v2LearnerView().reviewAnswers).isEmpty())
            assertFalse(advancementDecision(pack, pack.unit(6)!!, store.v2LearnerView()).allowed)
            task.slots.forEachIndexed { index, slot ->
                if (slot.objectiveId != "obj-u4-present") answer(store, task, index)
            }
            // Execute the actual attempted advance before the intended assertion. On the old
            // policy this succeeds, proving the shortcut instead of merely missing a future ID.
            val advancedWithoutPresent = runCatching { advance(store, 6) }.getOrDefault(false)
            println("UNIT6_BASELINE_OBSERVATION policy=${task.policyId} schema=${task.schema} " +
                "objectives=${task.slots.map { it.objectiveId }} actualAdvanceWithoutPresent=$advancedWithoutPresent")
            assertFalse("Unit6 must refuse actual advancement without its assigned present-form answer", advancedWithoutPresent)
            assertEquals("unit6-prior-present-subject-noun-behavior-v3", task.policyId)
            assertEquals(8, task.schema)
            assertEquals(listOf("obj-u4-present", "obj-u5-indeclinable", "obj-u4-nominative"), task.slots.map { it.objectiveId })
            assertEquals(listOf(4, 5, 4), task.slots.map { it.ownerUnit })
            assertFalse(task.slots.any { it.itemId == "u1-i-decode-pair" })
            assertFalse(advancementDecision(pack, pack.unit(6)!!, store.v2LearnerView()).allowed)
            answer(store, task, 0)
            assertTrue(advancementDecision(pack, pack.unit(6)!!, store.v2LearnerView()).allowed)
            assertTrue(advance(store, 6))
            val completion = store.governedLearnerDocument().unitCompletions.single { it.unitNumber == 6 }
            assertEquals(task.id, completion.reviewAssignmentId)
            assertEquals(task.policyDigest, completion.reviewPolicyDigest)
            assertTrue(completion.reviewCompletionOrder > task.boundary)
            answer(store, task, 0, false)
            assertFalse(cumulativeReviewComplete(pack, store.v2LearnerView(), 6))
            assertTrue(6 in currentCompletedUnitNumbers(pack, store.v2LearnerView()))
        }
    }

    @Test fun actualCurrentPartialTaskRoundtripsReopensAndCompletesItsOwnObligation() {
        val context = context()
        val name = "unit6-current-partial-${System.nanoTime()}"
        val stage = File(context.cacheDir, name)
        var partial: GovernedLearnerDocument? = null
        var completed: GovernedLearnerDocument? = null
        try {
            LocalStore.openNamed(context, name).use { store ->
                prepare(store)
                val task = open(store)
                assertEquals(3, task.slots.size)
                assertEquals(cumulativeReviewDeclaredPolicy(pack, 6)!!.id, task.policyId)
                assertEquals(setOf(1, 2, 3, 4, 5), currentCompletedUnitNumbers(pack, store.v2LearnerView()))
                assertEquals("unit5-prior-gender-subject-v2", store.cumulativeReviewHistory().first.single { it.targetUnit == 5 }.policyId)
                answer(store, task, 0)
                assertEquals(setOf(0), completedReviewSlots(pack, task, store.v2LearnerView().reviewAnswers))
                assertFalse(advancementDecision(pack, pack.unit(6)!!, store.v2LearnerView()).allowed)
                partial = store.governedLearnerDocument()
                val bytes = archive(store)
                captureLegacy(task, partial!!, bytes)
                val decoded = readGovernedBackup(bytes.inputStream(), "dev.russiancompanion", pack.packId, pack.packVersion, stage)
                assertTrue(decoded is BackupReadResult.Valid)
                assertEquals(partial, (decoded as BackupReadResult.Valid).backup.document)
                LocalStore.openInMemory(context).use { target ->
                    assertEquals(GovernedReplaceOutcome.REPLACED, target.replaceGovernedLearner(decoded.backup.document, emptyList()))
                    assertEquals(partial, target.governedLearnerDocument())
                    assertEquals(task, open(target))
                }
            }
            LocalStore.openNamed(context, name).use { store ->
                assertEquals(partial, store.governedLearnerDocument())
                val task = open(store)
                assertEquals(partial!!.reviewAssignments.single { it.targetUnit == 6 }, task)
                answer(store, task, 1); answer(store, task, 2)
                assertTrue(advance(store, 6))
                assertEquals((1..6).toSet(), currentCompletedUnitNumbers(pack, store.v2LearnerView()))
                completed = store.governedLearnerDocument()
                assertEquals(task.id, completed!!.unitCompletions.single { it.unitNumber == 6 }.reviewAssignmentId)
            }
            LocalStore.openNamed(context, name).use { store ->
                assertEquals(completed, store.governedLearnerDocument())
                assertEquals((1..6).toSet(), currentCompletedUnitNumbers(pack, store.v2LearnerView()))
            }
        } finally {
            stage.deleteRecursively(); context.deleteDatabase(name)
            File(context.filesDir, "recordings-$name").deleteRecursively()
        }
    }

    private fun captureLegacy(task: CumulativeReviewAssignment, document: GovernedLearnerDocument, bytes: ByteArray) {
        val path = System.getProperty("audit.unit6.capture.dir") ?: return
        if (task.policyId != "unit6-prior-reading-subject-noun-behavior-v2") return
        require(path.startsWith("/workspace/artifacts/"))
        val directory = File(path).also { check(it.mkdirs()) }
        val documentText = backupJson.encodeToString(GovernedLearnerDocument.serializer(), document)
        File(directory, "PARTIAL_UNIT6_V2.zip").writeBytes(bytes)
        File(directory, "PARTIAL_UNIT6_V2.json").writeText(documentText)
        val git = ProcessBuilder("git", "rev-parse", "HEAD").directory(root).start()
        val commit = git.inputStream.bufferedReader().readText().trim()
        check(git.waitFor() == 0)
        File(directory, "IDENTITY.json").writeText(buildJsonObject {
            put("producerCommit", commit); put("policyId", task.policyId); put("schema", task.schema)
            put("policyDigest", task.policyDigest); put("assignmentDigest", task.digest)
            put("rawCurriculumSHA256", hash(File(root, "app/src/main/assets/content/v2-curriculum.json").readBytes()))
            put("zipSHA256", hash(bytes)); put("zipBytes", bytes.size)
            put("documentSHA256", hash(documentText.toByteArray()))
            put("syntheticLearner", true); put("originalAllowlistReplaced", false)
        }.toString() + "\n")
    }

    private fun prepare(store: LocalStore) {
        for (n in 1..6) {
            store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = n, currentSessionId = null))
            for (step in pack.unit(n)!!.sessions.flatMap { it.steps }) {
                step.itemIds.forEach { id -> store.v2RecordGovernedAttempt(prepareGovernedAttempt(pack,
                    id, pack.item(id)!!.answer, PRESCRIBED_CHECK, step.id, "unit6-present")!!) }
                store.v2CompleteGovernedStep(step.id, governedStepCompletionDigest(pack, store.v2LearnerView(), step.id)!!, "unit6-present")
            }
            store.v2AttestBook(n, "unit6-present")
            if (n < 6) {
                if (n >= 3) {
                    val task = store.openCumulativeReview(pack, n, store.learnerMutationEpoch())!!
                    task.slots.indices.forEach { answer(store, task, it) }
                }
                assertTrue(advance(store, n))
            }
        }
    }
    private fun open(store: LocalStore) = store.openCumulativeReview(pack, 6, store.learnerMutationEpoch())!!
    private fun answer(store: LocalStore, task: CumulativeReviewAssignment, index: Int, correct: Boolean = true) {
        val item = pack.item(task.slots[index].itemId)!!
        assertTrue(store.answerCumulativeReview(pack, task.id, index,
            if (correct) item.answer else item.options.first { it != item.answer }, store.learnerMutationEpoch()))
    }
    private fun advance(store: LocalStore, n: Int): Boolean = runBlocking {
        val session = pack.unit(n + 1)!!.sessions.first().id
        store.v2AdvanceUnitIfCurrentAsync(store.learnerMutationEpoch(), n, "unit6-present", n + 1,
            session, resumePositionDigest(pack, session, 0)!!, GOVERNED_IDENTITY_SCHEMA, pack)
    }
    private fun archive(store: LocalStore): ByteArray = ByteArrayOutputStream().also {
        writeGovernedBackup(it, "dev.russiancompanion", pack.packId, pack.packVersion, "DARK", store.governedLearnerDocument(), emptyList())
    }.toByteArray()
    private fun context(): Context = ApplicationProvider.getApplicationContext<Context>().also {
        (it.applicationContext as RussianCompanionApplication).closeStartupStoreForTests()
    }
    companion object {
        private val root = File(if (File("app").isDirectory) "." else "..")
        private val pack by lazy { V2PackLoader.parse(File(root, "app/src/main/assets/content/v2-curriculum.json").readText()).also { V2PackIdentityIndex.warm(it) } }
        private fun hash(bytes: ByteArray) = MessageDigest.getInstance("SHA-256").digest(bytes).joinToString("") { "%02x".format(it) }
    }
}
