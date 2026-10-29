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

/** Synthetic ordinary work; actual current assignments, store advancement and backup APIs. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class Unit9NounBehaviorReviewBaselineTest {
    @Test fun printPluralAndGenderAnswersCannotReplaceFreshAssignedNounBehaviorAnswer() {
        LocalStore.openInMemory(context()).use { store ->
            prepare(store)
            val task = open(store)
            assertTrue(ordinaryUnitRequirements(pack, pack.unit(9)!!, store.v2LearnerView()).allowed)
            // Earlier noun-behavior knowledge and Unit6 review are already present. The
            // proposed contrast concerns a new assigned answer, not missing ordinary knowledge.
            assertTrue(store.cumulativeReviewHistory().first.single { it.targetUnit == 6 }
                .slots.any { it.objectiveId == "obj-u5-indeclinable" })
            task.slots.forEach { slot ->
                val item = pack.item(slot.itemId)!!
                store.v2RecordGovernedAttempt(prepareGovernedAttempt(pack, item.id, item.answer,
                    OPTIONAL_PRACTICE, practiceStepId("mixed"), "unit9-noun-behavior")!!)
            }
            assertTrue(completedReviewSlots(pack, task, store.v2LearnerView().reviewAnswers).isEmpty())
            assertFalse(advancementDecision(pack, pack.unit(9)!!, store.v2LearnerView()).allowed)
            task.slots.forEachIndexed { index, slot ->
                if (slot.objectiveId != "obj-u5-indeclinable") answer(store, task, index)
            }
            val beforeAttemptedAdvance = store.governedLearnerDocument()
            val advancedWithoutNounBehavior = try {
                advance(store, 9)
            } catch (refusal: IllegalStateException) {
                if (refusal.message != "current advancement evidence is incomplete") throw refusal
                false
            }
            if (!advancedWithoutNounBehavior) assertEquals(beforeAttemptedAdvance, store.governedLearnerDocument())
            println("UNIT9_BASELINE_OBSERVATION policy=${task.policyId} schema=${task.schema} " +
                "objectives=${task.slots.map { it.objectiveId }} actualAdvanceWithoutNounBehavior=$advancedWithoutNounBehavior")
            // Intentionally fails on valid v2: it does not declare this proposed new slot.
            assertFalse("The proposed Unit9 obligation must refuse actual advancement without its assigned noun-behavior answer",
                advancedWithoutNounBehavior)
            assertEquals("unit9-prior-noun-behavior-plural-gender-v3", task.policyId)
            assertEquals(11, task.schema)
            assertEquals(listOf("obj-u5-indeclinable", "obj-u8-plural", "obj-u3-gender"), task.slots.map { it.objectiveId })
            assertEquals(listOf(5, 8, 3), task.slots.map { it.ownerUnit })
            assertFalse(task.slots.any { it.itemId == "u1-i-decode-pair" })
            assertFalse(advancementDecision(pack, pack.unit(9)!!, store.v2LearnerView()).allowed)
            answer(store, task, 0)
            assertTrue(advancementDecision(pack, pack.unit(9)!!, store.v2LearnerView()).allowed)
            assertTrue(advance(store, 9))
            val completion = store.governedLearnerDocument().unitCompletions.single { it.unitNumber == 9 }
            assertEquals(task.id, completion.reviewAssignmentId)
            assertEquals(task.policyDigest, completion.reviewPolicyDigest)
            assertTrue(completion.reviewCompletionOrder > task.boundary)
            answer(store, task, 0, false)
            assertFalse(cumulativeReviewComplete(pack, store.v2LearnerView(), 9))
            assertTrue(9 in currentCompletedUnitNumbers(pack, store.v2LearnerView()))
        }
    }

    @Test fun actualCurrentPartialTaskRoundtripsReopensAndCompletesItsOwnObligation() {
        val context = context()
        val name = "unit9-current-partial-${System.nanoTime()}"
        val stage = File(context.cacheDir, name)
        var partial: GovernedLearnerDocument? = null
        var completed: GovernedLearnerDocument? = null
        try {
            LocalStore.openNamed(context, name).use { store ->
                prepare(store)
                val task = open(store)
                assertEquals(3, task.slots.size)
                assertEquals(cumulativeReviewDeclaredPolicy(pack, 9)!!.id, task.policyId)
                assertEquals((1..8).toSet(), currentCompletedUnitNumbers(pack, store.v2LearnerView()))
                val prerequisites = store.cumulativeReviewHistory().first
                assertEquals("unit5-prior-gender-subject-v2", prerequisites.single { it.targetUnit == 5 }.policyId)
                assertEquals("unit6-prior-present-subject-noun-behavior-v3", prerequisites.single { it.targetUnit == 6 }.policyId)
                assertEquals("unit7-prior-gender-inanimate-case-v2", prerequisites.single { it.targetUnit == 7 }.policyId)
                assertEquals("unit8-prior-present-nominative-agreement-v2", prerequisites.single { it.targetUnit == 8 }.policyId)
                assertEquals(10, prerequisites.single { it.targetUnit == 8 }.schema)
                answer(store, task, 0)
                assertEquals(setOf(0), completedReviewSlots(pack, task, store.v2LearnerView().reviewAnswers))
                assertFalse(advancementDecision(pack, pack.unit(9)!!, store.v2LearnerView()).allowed)
                partial = store.governedLearnerDocument()
                assertTrue(partial!!.unitCompletions.none { it.unitNumber == 9 })
                assertTrue(partial!!.recordings.isEmpty())
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
                assertEquals(partial!!.reviewAssignments.single { it.targetUnit == 9 }, task)
                for (index in 1 until task.slots.size) answer(store, task, index)
                assertTrue(advance(store, 9))
                assertEquals((1..9).toSet(), currentCompletedUnitNumbers(pack, store.v2LearnerView()))
                completed = store.governedLearnerDocument()
                assertEquals(task.id, completed!!.unitCompletions.single { it.unitNumber == 9 }.reviewAssignmentId)
            }
            LocalStore.openNamed(context, name).use { store ->
                assertEquals(completed, store.governedLearnerDocument())
                assertEquals((1..9).toSet(), currentCompletedUnitNumbers(pack, store.v2LearnerView()))
            }
        } finally {
            stage.deleteRecursively(); context.deleteDatabase(name)
            File(context.filesDir, "recordings-$name").deleteRecursively()
        }
    }

    private fun captureLegacy(task: CumulativeReviewAssignment, document: GovernedLearnerDocument, bytes: ByteArray) {
        val path = System.getProperty("audit.unit9.capture.dir") ?: return
        if (task.policyId != "unit9-prior-reading-plural-gender-v2") return
        assertEquals(5, task.schema)
        assertEquals(listOf("obj-u1-decoding", "obj-u8-plural", "obj-u3-gender"), task.slots.map { it.objectiveId })
        assertEquals(listOf(1, 8, 3), task.slots.map { it.ownerUnit })
        require(path.startsWith("/workspace/artifacts/"))
        val directory = File(path).also { check(it.mkdirs()) }
        val documentText = backupJson.encodeToString(GovernedLearnerDocument.serializer(), document)
        File(directory, "PARTIAL_UNIT9_V2.zip").writeBytes(bytes)
        File(directory, "PARTIAL_UNIT9_V2.json").writeText(documentText)
        val git = ProcessBuilder("git", "rev-parse", "HEAD").directory(root).start()
        val commit = git.inputStream.bufferedReader().readText().trim()
        check(git.waitFor() == 0)
        File(directory, "IDENTITY.json").writeText(buildJsonObject {
            put("producerCommit", commit); put("policyId", task.policyId); put("schema", task.schema)
            put("policyDigest", task.policyDigest); put("assignmentDigest", task.digest)
            put("documentSchemaVersion", document.schemaVersion)
            put("recordingCount", document.recordings.size)
            put("rawCurriculumSHA256", hash(File(root, "app/src/main/assets/content/v2-curriculum.json").readBytes()))
            put("zipSHA256", hash(bytes)); put("zipBytes", bytes.size)
            put("documentSHA256", hash(documentText.toByteArray()))
            put("syntheticLearner", true); put("originalAllowlistReplaced", false)
        }.toString() + "\n")
    }

    private fun prepare(store: LocalStore) {
        for (n in 1..9) {
            store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = n, currentSessionId = null))
            for (step in pack.unit(n)!!.sessions.flatMap { it.steps }) {
                step.itemIds.forEach { id -> store.v2RecordGovernedAttempt(prepareGovernedAttempt(pack,
                    id, pack.item(id)!!.answer, PRESCRIBED_CHECK, step.id, "unit9-noun-behavior")!!) }
                store.v2CompleteGovernedStep(step.id, governedStepCompletionDigest(pack, store.v2LearnerView(), step.id)!!, "unit9-noun-behavior")
            }
            store.v2AttestBook(n, "unit9-noun-behavior")
            if (n < 9) {
                if (n >= 3) {
                    val task = store.openCumulativeReview(pack, n, store.learnerMutationEpoch())!!
                    task.slots.indices.forEach { answer(store, task, it) }
                }
                assertTrue(advance(store, n))
            }
        }
    }
    private fun open(store: LocalStore) = store.openCumulativeReview(pack, 9, store.learnerMutationEpoch())!!
    private fun answer(store: LocalStore, task: CumulativeReviewAssignment, index: Int, correct: Boolean = true) {
        val item = pack.item(task.slots[index].itemId)!!
        assertTrue(store.answerCumulativeReview(pack, task.id, index,
            if (correct) item.answer else item.options.first { it != item.answer }, store.learnerMutationEpoch()))
    }
    private fun advance(store: LocalStore, n: Int): Boolean = runBlocking {
        val session = pack.unit(n + 1)!!.sessions.first().id
        store.v2AdvanceUnitIfCurrentAsync(store.learnerMutationEpoch(), n, "unit9-noun-behavior", n + 1,
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
