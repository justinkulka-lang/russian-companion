package dev.russiancompanion.v2

import android.content.Context
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.backup.*
import dev.russiancompanion.v2.curriculum.*
import dev.russiancompanion.v2.ui.*
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.ByteArrayOutputStream
import java.io.File

/** A2-30 assignment checklist and A2-31 target dates, with English-only fixtures (MA2-162). */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class CourseworkChecklistTest {
    @get:Rule val compose = createComposeRule()
    @After fun reset() { V2TodayClock.epochDayForTests = null; V2UiWork.blockForTests = null }

    @Test fun checklistListsBookPagesExercisesSessionsBinderAndReviewFromRecordedEvidence() {
        val p = pack()
        val unit = p.unit(4)!!
        LocalStore.openInMemory(context()).use { s ->
            ready(p, s, 4)
            val before = unitAssignmentChecklist(p, unit, s.v2LearnerView())
            val kinds = before.map { it.kind }
            assertEquals(V2AssignmentKind.entries.toList(), kinds.distinct())
            // Pages come from the edition mapping; exercises are not yet named in the current pack.
            val pages = before.single { it.kind == V2AssignmentKind.BOOK_PAGES }
            val mapping = p.ownedEditionMappings.single { it.unitNumber == 4 }
            assertEquals("Book: Lesson ${mapping.proposedLessonNumber}, printed pages ${mapping.printedPageStart}–${mapping.printedPageEnd}", pages.label)
            assertEquals(V2AssignmentStatus.TODO, pages.status)
            assertEquals(V2AssignmentStatus.NOT_ASSIGNED, before.single { it.kind == V2AssignmentKind.BOOK_EXERCISE }.status)
            assertEquals(unit.sessions.count { session -> session.steps.any { it.kind !in setOf("BINDER", "TUTOR") } },
                before.count { it.kind == V2AssignmentKind.APP_SESSION })
            assertTrue(before.filter { it.kind == V2AssignmentKind.APP_SESSION }.all { it.status == V2AssignmentStatus.TODO })
            assertEquals(V2AssignmentStatus.TODO, before.single { it.id == "review-due" }.status)
            assertTrue(before.single { it.kind == V2AssignmentKind.BINDER_TASK }.detail!!.contains("Self-check"))

            // English-only fixture: named exercises bind into the checklist and the pack identity.
            val fixture = p.copy(ownedEditionMappings = p.ownedEditionMappings.map {
                if (it.unitNumber == 4) it.copy(assignedExercises = listOf("Exercise A (practice fixture)", "Exercise B (practice fixture)")) else it })
            val named = unitAssignmentChecklist(fixture, fixture.unit(4)!!, s.v2LearnerView())
                .filter { it.kind == V2AssignmentKind.BOOK_EXERCISE }
            assertEquals(listOf("Book exercise: Exercise A (practice fixture)", "Book exercise: Exercise B (practice fixture)"), named.map { it.label })
            assertNotEquals(V2Validator.unitContentReviewDigest(p, 4), V2Validator.unitContentReviewDigest(fixture, 4))
            assertEquals(V2Validator.unitContentReviewDigest(p, 5), V2Validator.unitContentReviewDigest(fixture, 5))

            ordinary(p, s, 4)
            val after = unitAssignmentChecklist(p, unit, s.v2LearnerView(), dueSpacedItems = 2)
            assertEquals(V2AssignmentStatus.DONE, after.single { it.kind == V2AssignmentKind.BOOK_PAGES }.status)
            assertTrue(after.filter { it.kind == V2AssignmentKind.APP_SESSION }.all { it.status == V2AssignmentStatus.DONE })
            assertEquals(V2AssignmentStatus.DONE, after.single { it.kind == V2AssignmentKind.BINDER_TASK }.status)
            assertEquals(V2AssignmentStatus.TODO, after.single { it.id == "review-due" }.status)
            assertEquals("Review due: 2 items in Due review", after.single { it.id == "spaced-due" }.label)
            val a = s.openCumulativeReview(p, 4, s.learnerMutationEpoch())!!
            a.slots.indices.forEach { index -> val i = p.item(a.slots[index].itemId)!!
                assertTrue(s.answerCumulativeReview(p, a.id, index, i.answer, s.learnerMutationEpoch())) }
            assertEquals(V2AssignmentStatus.DONE, unitAssignmentChecklist(p, unit, s.v2LearnerView()).single { it.id == "review-due" }.status)
        }
    }

    @Test fun targetDatesShowDaysRemainingNeverPenalizeAndSurviveCurrentBackupRestore() {
        val p = pack()
        assertEquals("No target date set.", unitTargetLabel(null, 100))
        assertEquals("Target date: 1970-04-21 · 10 days remaining", unitTargetLabel(110, 100))
        assertEquals("Target date: 1970-04-11 · Due today", unitTargetLabel(100, 100))
        assertEquals("Target date: 1970-04-12 · 1 day remaining", unitTargetLabel(101, 100))
        assertEquals("Target date: 1970-04-08 · Passed 3 days ago. Nothing is reset.", unitTargetLabel(97, 100))
        LocalStore.openInMemory(context()).use { s ->
            ready(p, s, 4); ordinary(p, s, 4)
            val withoutTarget = advancementDecision(p, p.unit(4)!!, s.v2LearnerView())
            assertTrue(s.v2SetUnitTarget(4, 20_000))
            assertEquals(mapOf(4 to 20_000L), s.v2LearnerView().unitTargets)
            assertTrue(s.v2SetUnitTarget(4, 10))
            assertEquals(mapOf(4 to 10L), s.v2LearnerView().unitTargets)
            // A passed date changes no requirement, progress, or completion (MA2-091).
            assertEquals(withoutTarget, advancementDecision(p, p.unit(4)!!, s.v2LearnerView()))
            assertEquals(currentCompletedUnitNumbers(p, s.v2LearnerView().copy(unitTargets = emptyMap())), currentCompletedUnitNumbers(p, s.v2LearnerView()))
            assertFalse(s.v2SetUnitTarget(31, 10)); assertFalse(s.v2SetUnitTarget(0, 10))
            assertFalse(s.v2SetUnitTarget(4, -1)); assertFalse(s.v2SetUnitTarget(4, UNIT_TARGET_MAX_EPOCH_DAY + 1))
            assertEquals(mapOf(4 to 10L), s.v2LearnerView().unitTargets)
            assertTrue(s.v2SetUnitTarget(2, 12))
            val document = s.governedLearnerDocument()
            assertEquals(listOf(V2UnitTarget(2, 12), V2UnitTarget(4, 10)), document.unitTargets)
            val bytes = ByteArrayOutputStream()
            writeGovernedBackup(bytes, "dev.russiancompanion", p.packId, p.packVersion, "FOLLOW_SYSTEM", document, emptyList())
            val temp = File(context().cacheDir, "targets-${System.nanoTime()}")
            try {
                val read = readGovernedBackup(bytes.toByteArray().inputStream(), "dev.russiancompanion", p.packId, p.packVersion, temp) as BackupReadResult.Valid
                assertEquals(document, read.backup.document)
                LocalStore.openInMemory(context()).use { target ->
                    assertTrue(target.v2SetUnitTarget(9, 99))
                    assertEquals(GovernedReplaceOutcome.REPLACED, target.replaceGovernedLearner(read.backup.document, emptyList()))
                    assertEquals(mapOf(2 to 12L, 4 to 10L), target.v2LearnerView().unitTargets)
                    val before = target.governedLearnerFingerprint()
                    for (bad in listOf(document.unitTargets + V2UnitTarget(4, 11), listOf(V2UnitTarget(31, 1)), listOf(V2UnitTarget(3, -5)))) {
                        assertNotNull(unitTargetsDefect(bad))
                        assertEquals(GovernedReplaceOutcome.UNCHANGED, target.replaceGovernedLearner(document.copy(unitTargets = bad), emptyList()))
                        assertEquals(before, target.governedLearnerFingerprint())
                    }
                }
            } finally { temp.deleteRecursively() }
            assertTrue(s.v2SetUnitTarget(4, null))
            assertEquals(mapOf(2 to 12L), s.v2LearnerView().unitTargets)
        }
    }

    @Test fun todayShowsChecklistAndSetsAndClearsTargetDateThroughTheDialog() {
        val p = pack()
        V2TodayClock.epochDayForTests = 20_000
        LocalStore.openInMemory(context()).use { s ->
            ready(p, s, 4)
            compose.setContent { V2App(s, p, {}) }
            waitTag("v2-checklist")
            compose.onNodeWithTag("v2-today-target").assertTextEquals("No target date set.")
            compose.onNodeWithTag("v2-checklist-book-exercises").performScrollTo().assertTextContains("Not yet assigned", substring = true)
            compose.onNodeWithTag("v2-checklist-review-due").performScrollTo().assertTextContains("To do · Review due: cumulative review", substring = true)
            compose.onNodeWithTag("v2-target-set").performScrollTo().performClick()
            waitTag("v2-target-save"); compose.onNodeWithTag("v2-target-save").performClick()
            compose.waitUntil(30_000) { s.v2LearnerView().unitTargets[4] == 20_014L }
            waitText("Target date: ${java.time.LocalDate.ofEpochDay(20_014)} · 14 days remaining")
            compose.onNodeWithTag("v2-target-clear").performScrollTo().performClick()
            compose.waitUntil(30_000) { s.v2LearnerView().unitTargets.isEmpty() }
            waitText("No target date set.")
        }
    }

    private fun ready(p: V2CurriculumPack, s: LocalStore, current: Int) {
        for (n in 1..2) { ordinary(p, s, n); s.v2CompleteUnit(n, "done") }
        for (n in 3 until current) {
            ordinary(p, s, n)
            val a = s.openCumulativeReview(p, n, s.learnerMutationEpoch())!!
            a.slots.indices.forEach { index -> val i = p.item(a.slots[index].itemId)!!
                assertTrue(s.answerCumulativeReview(p, a.id, index, i.answer, s.learnerMutationEpoch())) }
            val session = p.unit(n + 1)!!.sessions.first().id
            assertTrue(kotlinx.coroutines.runBlocking { s.v2AdvanceUnitIfCurrentAsync(s.learnerMutationEpoch(), n, "advance", n + 1,
                session, resumePositionDigest(p, session, 0)!!, GOVERNED_IDENTITY_SCHEMA, p) })
        }
        s.v2SaveProfile(s.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = current, currentSessionId = null))
    }
    private fun ordinary(p: V2CurriculumPack, s: LocalStore, n: Int) {
        s.v2SaveProfile(s.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = n, currentSessionId = null))
        for (step in p.unit(n)!!.sessions.flatMap { it.steps }) {
            for (id in step.itemIds) s.v2RecordGovernedAttempt(prepareGovernedAttempt(p, id, p.item(id)!!.answer, PRESCRIBED_CHECK, step.id, "same-clock")!!)
            s.v2CompleteGovernedStep(step.id, governedStepCompletionDigest(p, s.v2LearnerView(), step.id)!!, "same-clock")
        }
        s.v2AttestBook(n, "same-clock")
    }
    private fun waitTag(tag: String) = compose.waitUntil(conditionDescription = tag, timeoutMillis = 30000) {
        compose.waitForIdle(); compose.onAllNodesWithTag(tag).fetchSemanticsNodes().isNotEmpty()
    }
    private fun waitText(text: String) = compose.waitUntil(conditionDescription = text, timeoutMillis = 30000) {
        compose.waitForIdle(); compose.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()
    }
    private fun context(): Context = ApplicationProvider.getApplicationContext()
    private fun pack() = sharedPack
    companion object { private val sharedPack by lazy {
        val root = File(if (File("app").isDirectory) "." else "..")
        V2PackLoader.parse(File(root, "app/src/main/assets/content/v2-curriculum.json").readText()).also { V2PackIdentityIndex.warm(it) }
    } }
}
