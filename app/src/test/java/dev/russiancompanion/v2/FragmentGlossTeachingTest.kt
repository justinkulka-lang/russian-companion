package dev.russiancompanion.v2

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.backup.*
import dev.russiancompanion.v2.curriculum.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.ByteArrayOutputStream
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class FragmentGlossTeachingTest {
    @Test fun threeTeachingBodiesDescribePhrasesWithoutAbsentPredicates() {
        val p = FragmentGlossFixture.current
        val bad = FragmentGlossFixture.teachingIds.filter { id ->
            val body = p.step(id)!!.second.body
            body.contains("этих женщин — I like these women") || body.contains("Эти женщины — These women like me") ||
                body.trim() == "I like these women."
        }
        assertEquals("Obsolete whole-sentence meanings on displayed fragments", emptyList<String>(), bad)
        assertTrue(p.step("u7-st-teach-objectadj-3")!!.second.body.contains("accusative or genitive"))
        assertTrue(p.step("u7-st-teach-objectadj-3")!!.second.body.contains("not a complete sentence"))
        assertTrue(p.step("u10-st-teach-animate-1")!!.second.body.contains("nominative plural"))
        assertTrue(p.step("u10-st-teach-animate-1")!!.second.body.contains("no predicate"))
        assertTrue(p.step("u10-st-teach-animate-2")!!.second.body.contains("only the noun phrases"))
        assertEquals("these women, accusative or genitive plural, context not a full sentence", p.example("ex-u7-objectadj-3")!!.note)
        assertEquals(p.example("ex-u7-objectadj-3")!!.note, p.example("ex-u10-animate-0")!!.note)
        assertEquals("these women, nominative plural", p.example("ex-u10-animate-1")!!.note)
    }

    @Test fun bothRepairsRestoreCompleteTeachingAndAllSevenExamples() {
        val p = FragmentGlossFixture.current
        for ((number, stem) in listOf(7 to "objectadj", 10 to "animate")) {
            val session = p.unit(number)!!.sessions.single { it.id == "u$number-s-$stem" }
            val rem = p.step("u$number-st-rem-$stem")!!.second
            val objective = p.unit(number)!!.objectives.single { it.id == "obj-u$number-$stem" }
            assertEquals(session.steps.filter { it.kind == "TEACH" }.joinToString("\n\n") { it.body }, rem.body)
            assertEquals(objective.exampleIds, rem.exampleIds)
            assertEquals(7, rem.exampleIds.size)
            assertEquals(objective.practiceStepIds, reviewedGuidedPractice(p, rem.id))
            assertFalse(rem.body.contains("этих женщин — I like these women"))
        }
    }

    @Test fun everyNativeItemExampleHistoricalRecordAndUnrelatedUnitRemainsExact() {
        val live = FragmentGlossFixture.current
        val p = RecordedCurriculumConservationScope.historicalTransition(FragmentGlossFixture.root, live, "7efff9e18328c612de235236d98e834bb85cd1db", 7, 10)
        val old = FragmentGlossFixture.previous
        assertEquals(old.items, p.items); assertEquals(626, p.items.size)
        assertEquals(old.examples, p.examples); assertEquals(821, p.examples.size)
        assertEquals(old.provenanceRecords, p.provenanceRecords.take(old.provenanceRecords.size))
        assertEquals(6984, old.provenanceRecords.size)
        assertEquals(5, p.provenanceRecords.size - old.provenanceRecords.size)
        assertTrue(p.provenanceRecords.drop(old.provenanceRecords.size).all {
            it.reviewStatus == IMPLEMENTED_PENDING_FINAL_AUDIT && it.reviewedDigest.isEmpty() &&
                it.synthesis?.applications.orEmpty().isEmpty()
        })
        assertEquals(old, p.copy(units = old.units, provenanceRecords = old.provenanceRecords))
        assertEquals(old.units.filter { it.number !in setOf(7, 10) }, p.units.filter { it.number !in setOf(7, 10) })
        val changed = FragmentGlossFixture.teachingIds.toSet() + setOf("u7-st-rem-objectadj", "u10-st-rem-animate")
        for (number in listOf(7, 10)) {
            val before = old.unit(number)!!; val after = p.unit(number)!!
            assertEquals(before.objectives, after.objectives)
            assertEquals(before.advancement, after.advancement)
            for (session in before.sessions) {
                val now = after.sessions.single { it.id == session.id }
                assertEquals(session, now.copy(steps = session.steps))
                assertEquals(session.steps.filter { it.id !in changed }, now.steps.filter { it.id !in changed })
                for (step in session.steps.filter { it.id in changed }) {
                    val next = now.steps.single { it.id == step.id }
                    assertEquals(step, next.copy(body = step.body, exampleIds = step.exampleIds))
                }
            }
        }
        val valid = V2Validator.validate(live)
        assertTrue(valid.errors.joinToString("\n"), valid.ok)
    }

    @Test fun oldTeachingCreditAndResumeStayStoredButAreStaleUntilFreshWork() {
        val p = FragmentGlossFixture.current; val old = FragmentGlossFixture.previous
        for ((number, itemId, teachId) in listOf(Triple(7, "u7-i-objectadj-3", "u7-st-teach-objectadj-3"),
            Triple(10, "u10-i-animate-1", "u10-st-teach-animate-1"))) {
            LocalStore.openInMemory(context()).use { store ->
                FragmentGlossFixture.record(store, old, itemId, old.item(itemId)!!.answer)
                store.v2CompleteGovernedStep(teachId, governedStepCompletionDigest(old, store.v2LearnerView(), teachId)!!, "old")
                val at = old.step(teachId)!!
                store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = number,
                    currentSessionId = at.first.id, currentStepIndex = at.first.steps.indexOf(at.second)).verifiedResume(old))
                val before = store.governedLearnerDocument()
                assertTrue(itemId in creditItemIds(old, store.v2LearnerView()))
                assertTrue(completedStepCounts(old, store.v2LearnerView(), teachId))
                assertTrue(resumeIsCurrent(old, store.v2LearnerView()))
                assertFalse(itemId in creditItemIds(p, store.v2LearnerView()))
                assertFalse(completedStepCounts(p, store.v2LearnerView(), teachId))
                assertFalse(resumeIsCurrent(p, store.v2LearnerView()))
                assertEquals(before, store.governedLearnerDocument())
                val item = p.item(itemId)!!
                FragmentGlossFixture.record(store, p, itemId, item.options.first { it != item.answer })
                assertFalse(itemId in creditItemIds(p, store.v2LearnerView()))
                FragmentGlossFixture.record(store, p, itemId, item.answer)
                assertTrue(itemId in creditItemIds(p, store.v2LearnerView()))
                assertEquals(before.attempts, store.governedLearnerDocument().attempts.take(before.attempts.size))
                assertFalse(advancementDecision(p, p.unit(number)!!, store.v2LearnerView()).allowed)
            }
        }
    }

    @Test fun backupAndNamedDatabaseReopenPreserveOldRowsAndFreshResumeIdentity() {
        val p = FragmentGlossFixture.current; val old = FragmentGlossFixture.previous
        val name = "fragments-${System.nanoTime()}.db"; val temp = File(context().cacheDir, "fragments-${System.nanoTime()}")
        try {
            LocalStore.openInMemory(context()).use { s ->
                val ids = listOf("u7-i-objectadj-3", "u10-i-animate-1")
                for (id in ids) FragmentGlossFixture.record(s, old, id, old.item(id)!!.answer)
                val oldRows = s.governedLearnerDocument().attempts
                FragmentGlossFixture.record(s, p, ids[0], p.item(ids[0])!!.answer)
                val at = p.step("u10-st-teach-animate-1")!!
                s.v2SaveProfile(s.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = 10,
                    currentSessionId = at.first.id, currentStepIndex = at.first.steps.indexOf(at.second)).verifiedResume(p))
                val bytes = ByteArrayOutputStream()
                writeGovernedBackup(bytes, "dev.russiancompanion", p.packId, p.packVersion, "FOLLOW_SYSTEM", s.governedLearnerDocument(), emptyList())
                val read = readGovernedBackup(bytes.toByteArray().inputStream(), "dev.russiancompanion", p.packId, p.packVersion, temp)
                assertTrue(read is BackupReadResult.Valid)
                LocalStore.openNamed(context(), name).use { target ->
                    assertEquals(GovernedReplaceOutcome.REPLACED, target.replaceGovernedLearner((read as BackupReadResult.Valid).backup.document, emptyList()))
                }
                LocalStore.openNamed(context(), name).use { target ->
                    assertEquals(s.governedLearnerDocument(), target.governedLearnerDocument())
                    assertEquals(oldRows, target.governedLearnerDocument().attempts.take(oldRows.size))
                    assertEquals(setOf(ids[0]), creditItemIds(p, target.v2LearnerView()))
                    assertTrue(resumeIsCurrent(p, target.v2LearnerView()))
                }
            }
        } finally { context().deleteDatabase(name); temp.deleteRecursively() }
    }

    private fun context(): Context = ApplicationProvider.getApplicationContext()
}

internal object FragmentGlossFixture {
    val root get() = File(if (File("app").isDirectory) "." else "..")
    val teachingIds = listOf("u7-st-teach-objectadj-3", "u10-st-teach-animate-1", "u10-st-teach-animate-2")
    val current by lazy { V2PackLoader.parse(File(root, "app/src/main/assets/content/v2-curriculum.json").readText()).also { V2PackIdentityIndex.warm(it) } }
    val previous by lazy {
        val x = ProcessBuilder("git", "show", "37419b9c20c72eaf48ef2e5244520973ed29de8c:app/src/main/assets/content/v2-curriculum.json").directory(root).start()
        V2PackLoader.parse(x.inputStream.bufferedReader().readText()).also { assertEquals(0, x.waitFor()); V2PackIdentityIndex.warm(it) }
    }
    fun record(s: LocalStore, p: V2CurriculumPack, id: String, selected: String) {
        val at = p.units.flatMap { it.sessions }.flatMap { it.steps }.single { id in it.itemIds }
        s.v2RecordGovernedAttempt(prepareGovernedAttempt(p, id, selected, PRESCRIBED_CHECK, at.id,
            "row-${s.v2Attempts().size}")!!)
    }
}
