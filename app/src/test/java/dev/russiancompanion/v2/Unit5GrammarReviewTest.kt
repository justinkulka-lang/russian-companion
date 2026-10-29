package dev.russiancompanion.v2

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.backup.*
import dev.russiancompanion.v2.curriculum.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.ByteArrayOutputStream
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class Unit5GrammarReviewTest {
    @Test fun freshGenderAndSubjectNeedSeparateReviewAnswersBeforeAdvance() {
        val p = current
        LocalStore.openInMemory(context()).use { s ->
            prepare(p, s)
            val a = open(p, s)
            assertTrue(ordinaryUnitRequirements(p, p.unit(5)!!, s.v2LearnerView()).allowed)
            a.slots.forEach { slot ->
                val item = p.item(slot.itemId)!!
                s.v2RecordGovernedAttempt(prepareGovernedAttempt(p, item.id, item.answer,
                    OPTIONAL_PRACTICE, practiceStepId("mixed"), "test")!!)
            }
            assertTrue(completedReviewSlots(p, a, s.v2LearnerView().reviewAnswers).isEmpty())
            assertFalse(advancementDecision(p, p.unit(5)!!, s.v2LearnerView()).allowed)
            val glyph = a.slots.indexOfFirst { it.itemId == "u1-i-decode-pair" }
            if (glyph >= 0) answer(p, s, a, glyph)
            val subject = a.slots.indexOfFirst { it.objectiveId == "obj-u4-nominative" }
            answer(p, s, a, subject)
            assertFalse("Subject plus print matching must not replace a gender review answer",
                advancementDecision(p, p.unit(5)!!, s.v2LearnerView()).allowed)
            assertTrue(runCatching { advance(p, s, 5) }.isFailure)
            assertEquals(newPolicy, a.policyId)
            assertEquals(7, a.schema)
            assertEquals(listOf("obj-u3-gender", "obj-u4-nominative"), a.slots.map { it.objectiveId })
            assertEquals(listOf(3, 4), a.slots.map { it.ownerUnit })
            assertTrue(a.slots.none { it.itemId == "u1-i-decode-pair" })
            answer(p, s, a, 0)
            assertTrue(advancementDecision(p, p.unit(5)!!, s.v2LearnerView()).allowed)
            assertTrue(advance(p, s, 5))
            val completion = s.governedLearnerDocument().unitCompletions.single { it.unitNumber == 5 }
            assertEquals(a.id, completion.reviewAssignmentId)
            assertEquals(a.policyDigest, completion.reviewPolicyDigest)
            assertTrue(completion.reviewCompletionOrder > a.boundary)
            answer(p, s, a, 1, false)
            assertFalse(cumulativeReviewComplete(p, s.v2LearnerView(), 5))
            assertTrue(5 in currentCompletedUnitNumbers(p, s.v2LearnerView()))
        }
    }

    @Test fun newTaskKeepsChosenGenderThroughReopenAndBackupRoundtrip() {
        val p = current
        assertEquals(newPolicy, cumulativeReviewDeclaredPolicy(p, 5)!!.id)
        val name = "unit5-grammar-${System.nanoTime()}"
        var store = LocalStore.openNamed(context(), name)
        val staging = File(context().cacheDir, name)
        try {
            prepare(p, store)
            val item = p.item("u3-i-gender-дверь")!!
            store.v2RecordGovernedAttempt(prepareGovernedAttempt(p, item.id,
                item.options.first { it != item.answer }, PRESCRIBED_CHECK,
                "u3-st-check-gender", "test")!!)
            val a = open(p, store)
            assertEquals(7, a.schema)
            assertEquals(item.id, a.slots.first().itemId)
            store.v2RecordGovernedAttempt(prepareGovernedAttempt(p, item.id, item.answer,
                PRESCRIBED_CHECK, "u3-st-check-gender", "test")!!)
            assertEquals(a, open(p, store))
            answer(p, store, a, 0)
            val before = store.cumulativeReviewHistory()
            val bytes = archive(p, store)
            store.close()
            store = LocalStore.openNamed(context(), name)
            assertEquals(before, store.cumulativeReviewHistory())
            assertEquals(a, open(p, store))
            val read = readGovernedBackup(bytes.inputStream(), "dev.russiancompanion", p.packId, p.packVersion, staging)
            assertTrue(read is BackupReadResult.Valid)
            LocalStore.openInMemory(context()).use { target ->
                assertEquals(GovernedReplaceOutcome.REPLACED, target.replaceGovernedLearner((read as BackupReadResult.Valid).backup.document, emptyList()))
                assertEquals(before, target.cumulativeReviewHistory())
                assertEquals(a, open(p, target))
                assertFalse(advancementDecision(p, p.unit(5)!!, target.v2LearnerView()).allowed)
                answer(p, target, a, 1)
                assertTrue(advance(p, target, 5))
            }
        } finally {
            store.close(); context().deleteDatabase(name)
            File(context().filesDir, "recordings-$name").deleteRecursively(); staging.deleteRecursively()
        }
    }

    @Test fun wrongSchemaAndUntaughtOwnerCannotBorrowFreshCredit() {
        val p = current
        assertEquals(newPolicy, cumulativeReviewDeclaredPolicy(p, 5)!!.id)
        LocalStore.openInMemory(context()).use { s ->
            prepare(p, s)
            val a = open(p, s)
            for (schema in listOf(1, 2, 3, 4, 5, 6, 8)) {
                val bad = a.copy(schema = schema).let { it.copy(digest = reviewAssignmentDigest(it)) }
                assertFalse("schema $schema cannot acquire the new policy", reviewAssignmentCurrent(p, bad))
                assertNotNull(reviewHistoryDefects(listOf(bad.copy(boundary = 0).let { it.copy(digest = reviewAssignmentDigest(it)) }), emptyList()))
            }
            val wrongTarget = a.copy(targetUnit = 6).let { it.copy(digest = reviewAssignmentDigest(it)) }
            assertFalse(reviewAssignmentCurrent(p, wrongTarget))
            val learner = s.v2LearnerView()
            assertNull(createCumulativeReviewAssignment(p, learner.copy(completedUnitNumbers = learner.completedUnitNumbers - 3), 5, "untaught", 0))
            assertNull(createCumulativeReviewAssignment(p, learner.copy(governedStepDigests = emptyMap()), 5, "no-teaching", 0))
            val changed = p.copy(items = p.items.map { if (it.id == a.slots.first().itemId) it.copy(feedback = it.feedback + " changed") else it })
            assertFalse(reviewAssignmentCurrent(changed, a))
            val epoch = s.learnerMutationEpoch()
            s.openLearnerReplacement()
            try { assertFalse(s.answerCumulativeReview(p, a.id, 0, p.item(a.slots[0].itemId)!!.answer, epoch)) }
            finally { s.closeLearnerReplacement() }
        }
    }

    /** Baseline-only capture is opt-in; later runs consume those exact pre-change bytes. */
    @Test fun legacyPartialTaskRestoresCompletesAndReopensWithItsOwnVersion() {
        // Original contract on actual pre-literacy source; current refusal has separate direct coverage.
        with(RecordedLiteracyEpochScope.context(current, 5)) {
        val p = current
        val capture = System.getProperty("audit.unit5.legacy.capture")?.let(::File)
        val file = capture ?: File(root, "audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-cc020-unit5-grammar-2026-10-22/BASELINE_LEGACY_UNIT5.zip")
        if (capture != null) {
            check(!capture.exists())
            val old = historical
            LocalStore.openInMemory(context()).use { original ->
                prepare(old, original)
                val a = open(old, original)
                assertEquals(3, a.schema)
                assertEquals("unit5-prior-reading-subject-v1", a.policyId)
                answer(old, original, a, 0)
                capture.writeBytes(archive(old, original))
            }
        }
        assertTrue("Genuine baseline backup is required", file.isFile)
        val name = "unit5-legacy-${System.nanoTime()}"
        val staging = File(context().cacheDir, name)
        try {
            val read = readGovernedBackup(file.inputStream(), "dev.russiancompanion", p.packId, p.packVersion, staging)
            assertTrue(read is BackupReadResult.Valid)
            val document = (read as BackupReadResult.Valid).backup.document
            val old = document.reviewAssignments.single { it.targetUnit == 5 }
            assertEquals(3, old.schema)
            LocalStore.openNamed(context(), name).use { s ->
                assertEquals(GovernedReplaceOutcome.REPLACED, s.replaceGovernedLearner(document, emptyList()))
                assertEquals(document.reviewAssignments to document.reviewAnswers, s.cumulativeReviewHistory())
                assertEquals(old, open(p, s))
                assertEquals(setOf(0), completedReviewSlots(p, old, s.cumulativeReviewHistory().second))
                answer(p, s, old, 1)
                assertTrue(advance(p, s, 5))
            }
            LocalStore.openNamed(context(), name).use { s ->
                assertEquals(old, open(p, s))
                assertEquals((1..5).toSet(), currentCompletedUnitNumbers(p, s.v2LearnerView()))
                assertEquals(old.id, s.governedLearnerDocument().unitCompletions.single { it.unitNumber == 5 }.reviewAssignmentId)
            }
        } finally {
            context().deleteDatabase(name); File(context().filesDir, "recordings-$name").deleteRecursively(); staging.deleteRecursively()
        }
        }
    }

    private fun prepare(p: V2CurriculumPack, s: LocalStore) {
        for (n in 1..5) {
            s.v2SaveProfile(s.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = n, currentSessionId = null))
            for (step in p.unit(n)!!.sessions.flatMap { it.steps }) {
                for (id in step.itemIds) s.v2RecordGovernedAttempt(prepareGovernedAttempt(p, id, p.item(id)!!.answer, PRESCRIBED_CHECK, step.id, "test")!!)
                s.v2CompleteGovernedStep(step.id, governedStepCompletionDigest(p, s.v2LearnerView(), step.id)!!, "test")
            }
            s.v2AttestBook(n, "test")
            if (n <= 2) s.v2CompleteUnit(n, "test")
            else if (n < 5) {
                val a = s.openCumulativeReview(p, n, s.learnerMutationEpoch())!!
                a.slots.indices.forEach { answer(p, s, a, it) }; assertTrue(advance(p, s, n))
            }
        }
    }
    private fun answer(p: V2CurriculumPack, s: LocalStore, a: CumulativeReviewAssignment, index: Int, correct: Boolean = true) {
        val i = p.item(a.slots[index].itemId)!!
        assertTrue(s.answerCumulativeReview(p, a.id, index, if (correct) i.answer else i.options.first { it != i.answer }, s.learnerMutationEpoch()))
    }
    private fun open(p: V2CurriculumPack, s: LocalStore) = s.openCumulativeReview(p, 5, s.learnerMutationEpoch())!!
    private fun advance(p: V2CurriculumPack, s: LocalStore, n: Int): Boolean = runBlocking {
        val session = p.unit(n + 1)!!.sessions.first().id
        s.v2AdvanceUnitIfCurrentAsync(s.learnerMutationEpoch(), n, "test", n + 1, session, resumePositionDigest(p, session, 0)!!, GOVERNED_IDENTITY_SCHEMA, p)
    }
    private fun archive(p: V2CurriculumPack, s: LocalStore): ByteArray = ByteArrayOutputStream().also {
        writeGovernedBackup(it, "dev.russiancompanion", p.packId, p.packVersion, "DARK", s.governedLearnerDocument(), emptyList())
    }.toByteArray()
    private fun context(): Context = ApplicationProvider.getApplicationContext()
    companion object {
        private const val newPolicy = "unit5-prior-gender-subject-v2"
        private val root = File(if (File("app").isDirectory) "." else "..")
        private val current by lazy { V2PackLoader.parse(File(root, "app/src/main/assets/content/v2-curriculum.json").readText()).also { V2PackIdentityIndex.warm(it) } }
        private val historical by lazy {
            val process = ProcessBuilder("git", "show", "b869da02869840b575b7a803fdeebb27eaabb5d6:app/src/main/assets/content/v2-curriculum.json").directory(root).start()
            V2PackLoader.parse(process.inputStream.bufferedReader().readText()).also { assertEquals(0, process.waitFor()); V2PackIdentityIndex.warm(it) }
        }
    }
}
