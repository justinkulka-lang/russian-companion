package dev.russiancompanion.v2

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.backup.*
import dev.russiancompanion.v2.curriculum.*
import kotlinx.serialization.json.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.ByteArrayOutputStream
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class Unit4PresentTeachingTest {
    @Test fun everyRequiredTargetIsTaughtWithExactAdmittedAlternativesBeforeDistinctGuidedWork() {
        val p = current; val session = present(p); val objective = objective(p)
        assertEquals(4, objective.checkItemIds.size)
        for (id in objective.checkItemIds) {
            val item = p.item(id)!!
            val bare = id.removePrefix("u4-i-verb-").substringBeforeLast('-')
            val teaching = p.step("u4-st-teach-present-$bare")!!.second
            val guide = p.item("u4-g-present-$bare-я")!!
            assertTrue(item.promptLetter!! in teaching.body)
            for (option in item.options) {
                assertTrue(option, option in teaching.body)
                assertTrue(p.provenanceRecords.any { it.unitNumber == 4 && it.sourceId == item.sourceId && it.shippedText == option })
            }
            assertEquals(item.options, guide.options)
            assertNotEquals(item.answer, guide.answer)
            assertEquals(item.promptLetter, guide.promptLetter)
            assertTrue(session.steps.indexOf(teaching) < session.steps.indexOfFirst { guide.id in it.itemIds })
            assertTrue(session.steps.indexOfFirst { guide.id in it.itemIds } < session.steps.indexOfFirst { id in it.itemIds })
            assertTrue(guide.distractorRationales.filter { it.isNotBlank() }.size == 3)
        }
        val rem = p.step("u4-st-rem-present")!!.second
        assertEquals(session.steps.filter { it.kind == "TEACH" }.joinToString("\n\n") { it.body }, rem.body)
        assertEquals(objective.exampleIds, rem.exampleIds)
        assertEquals(6, rem.exampleIds.size)
        assertEquals(6, reviewedGuidedPractice(p, rem.id).size)
        val valid = V2Validator.validate(p)
        assertTrue(valid.errors.joinToString("\n"), valid.ok)
    }

    @Test fun existingNativeItemsExamplesRecordsAndOtherUnitContentsRemainExact() {
        val p = current; val old = previous
        assertEquals(622, old.items.size); assertEquals(626, p.items.size)
        assertEquals(old.items, p.items.take(622))
        assertEquals(old.examples, p.examples.take(old.examples.size))
        assertEquals(old.provenanceRecords, p.provenanceRecords.take(old.provenanceRecords.size))
        assertEquals(41, p.provenanceRecords.size - old.provenanceRecords.size)
        assertTrue(p.provenanceRecords.drop(old.provenanceRecords.size).all {
            it.reviewStatus == IMPLEMENTED_PENDING_FINAL_AUDIT && it.reviewedDigest.isEmpty() &&
                it.synthesis?.applications.orEmpty().isEmpty()
        })
        assertEquals(old, p.copy(units = old.units, items = old.items, examples = old.examples, provenanceRecords = old.provenanceRecords))
        assertEquals(old.units.filter { it.number != 4 }, p.units.filter { it.number != 4 })
        assertEquals(old.unit(4)!!.sessions.filter { it.id != "u4-s-present" }, p.unit(4)!!.sessions.filter { it.id != "u4-s-present" })
        assertEquals(old.unit(4)!!.objectives.filter { it.id != "obj-u4-present" }, p.unit(4)!!.objectives.filter { it.id != "obj-u4-present" })
        assertEquals(declaredAfterUnit(), p.unit(4))
        assertEquals(old.unit(4)!!.advancement, p.unit(4)!!.advancement)
        // First-conjugation controls retain every native field and their original teaching.
        for (id in listOf("u4-i-verb-читать-ты", "u4-i-verb-делать-я")) assertEquals(old.item(id), p.item(id))
        for (id in listOf("u4-st-teach-present-2", "u4-st-teach-present-3", "u4-st-teach-present-4"))
            assertEquals(old.step(id)!!.second, p.step(id)!!.second)
    }

    @Test fun oldCorrectAnswersAndResumeStayStoredButCannotSupplyChangedUnitCredit() {
        LocalStore.openInMemory(context()).use { s ->
            val old = previous; val p = current
            for (id in objective(old).checkItemIds) record(s, old, id, old.item(id)!!.answer)
            val step = old.step("u4-st-check-present")!!
            val profile = s.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = 4,
                currentSessionId = step.first.id, currentStepIndex = step.first.steps.indexOf(step.second)).verifiedResume(old)
            s.v2SaveProfile(profile)
            val before = s.governedLearnerDocument()
            assertTrue(objective(old).checkItemIds.all { it in creditItemIds(old, s.v2LearnerView()) })
            assertTrue(resumeIsCurrent(old, s.v2LearnerView()))
            assertFalse(resumeIsCurrent(p, s.v2LearnerView()))
            assertTrue(creditItemIds(p, s.v2LearnerView()).isEmpty())
            assertNotEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS, objectiveState(objective(p), s.v2LearnerView(), p))
            assertEquals(before, s.governedLearnerDocument())
        }
    }

    @Test fun guidedAnswersCannotReplaceRequiredTargetsAndWrongThenRightStillUsesExactKeys() {
        LocalStore.openInMemory(context()).use { s ->
            val p = current
            for (stepId in objective(p).practiceStepIds) {
                for (id in p.step(stepId)!!.second.itemIds) record(s, p, id, p.item(id)!!.answer)
            }
            assertTrue(objective(p).checkItemIds.none { it in creditItemIds(p, s.v2LearnerView()) })
            for (id in objective(p).checkItemIds) {
                val item = p.item(id)!!
                record(s, p, id, item.options.first { it != item.answer })
                assertFalse(id in creditItemIds(p, s.v2LearnerView()))
                record(s, p, id, item.answer)
                assertTrue(id in creditItemIds(p, s.v2LearnerView()))
            }
            assertEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS, objectiveState(objective(p), s.v2LearnerView(), p))
            assertFalse(advancementDecision(p, p.unit(4)!!, s.v2LearnerView()).allowed)
        }
    }

    @Test fun correctTargetsCannotFinishOrdinaryRequirementsUntilNewTeachingIsRecorded() {
        val p = current; val unit = p.unit(4)!!
        val newTeaching = present(p).steps.filter { it.kind == "TEACH" &&
            it.id !in present(previous).steps.map { old -> old.id } }
        assertEquals(4, newTeaching.size)
        LocalStore.openInMemory(context()).use { s ->
            s.v2SaveProfile(s.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = 4))
            for (step in unit.sessions.flatMap { it.steps }) {
                for (id in step.itemIds) s.v2RecordGovernedAttempt(prepareGovernedAttempt(p, id,
                    p.item(id)!!.answer, PRESCRIBED_CHECK, step.id, "fixed")!!)
                if (step !in newTeaching) s.v2CompleteGovernedStep(step.id,
                    governedStepCompletionDigest(p, s.v2LearnerView(), step.id)!!, "fixed")
            }
            s.v2AttestBook(4, "synthetic-host-attestation")
            assertEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS, objectiveState(objective(p), s.v2LearnerView(), p))
            assertEquals(listOf("Finish the required instruction steps"), ordinaryUnitRequirements(p, unit, s.v2LearnerView()).remaining)
            for (step in newTeaching) s.v2CompleteGovernedStep(step.id,
                governedStepCompletionDigest(p, s.v2LearnerView(), step.id)!!, "fixed")
            assertTrue(ordinaryUnitRequirements(p, unit, s.v2LearnerView()).allowed)
            // Ordinary instruction is not the independently assigned review or prior-unit evidence.
            assertFalse(advancementDecision(p, unit, s.v2LearnerView()).allowed)
        }
    }

    @Test fun oldAndFreshAnswersSurviveBackupReplacementAndNamedDatabaseReopenWithoutRelabelling() {
        val p = current; val name = "present-reopen-${System.nanoTime()}.db"
        val temp = File(context().cacheDir, "present-backup-${System.nanoTime()}")
        try {
            LocalStore.openInMemory(context()).use { s ->
                for (id in objective(previous).checkItemIds) record(s, previous, id, previous.item(id)!!.answer)
                val oldRows = s.governedLearnerDocument().attempts
                val id = objective(p).checkItemIds.first()
                record(s, p, id, p.item(id)!!.answer)
                val check = p.step("u4-st-check-present")!!
                s.v2SaveProfile(s.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = 4,
                    currentSessionId = check.first.id, currentStepIndex = check.first.steps.indexOf(check.second)).verifiedResume(p))
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
                    assertEquals(setOf(id), creditItemIds(p, target.v2LearnerView()))
                    assertTrue(resumeIsCurrent(p, target.v2LearnerView()))
                    assertNotEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS, objectiveState(objective(p), target.v2LearnerView(), p))
                }
            }
        } finally { context().deleteDatabase(name); temp.deleteRecursively() }
    }

    private fun record(s: LocalStore, p: V2CurriculumPack, id: String, answer: String) {
        val step = present(p).steps.single { id in it.itemIds }
        s.v2RecordGovernedAttempt(prepareGovernedAttempt(p, id, answer, PRESCRIBED_CHECK, step.id, "row-${s.v2Attempts().size}")!!)
    }
    private fun context(): Context = ApplicationProvider.getApplicationContext()
    companion object {
        val root get() = File(if (File("app").isDirectory) "." else "..")
        internal val current by lazy { V2PackLoader.parse(File(root, "app/src/main/assets/content/v2-curriculum.json").readText()).also { V2PackIdentityIndex.warm(it) } }
        internal val previous by lazy {
            val process = ProcessBuilder("git", "show", "b5e1daf50ac8597d00aeb6976f894d3ce44c03ca:app/src/main/assets/content/v2-curriculum.json").directory(root).start()
            V2PackLoader.parse(process.inputStream.bufferedReader().readText()).also { assertEquals(0, process.waitFor()); V2PackIdentityIndex.warm(it) }
        }
        internal fun present(p: V2CurriculumPack) = p.unit(4)!!.sessions.single { it.id == "u4-s-present" }
        internal fun objective(p: V2CurriculumPack) = p.unit(4)!!.objectives.single { it.id == "obj-u4-present" }
        internal fun declaredAfterUnit(): V2Unit {
            val correction = backupJson.parseToJsonElement(File(root,
                "audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-cc015/CORRECTION.json").readText()).jsonObject
            return backupJson.decodeFromJsonElement(V2Unit.serializer(), correction.getValue("afterUnit"))
        }
    }
}
