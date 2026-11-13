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
class RepeatedTaskCreditTest {
    @Test fun repeatedTaskIdsNeedTheirOwnAttemptsAndOtherRequiredEvidence() {
        val p = pack()
        for ((index, pair) in pairs.withIndex()) {
            val first = p.item(pair.first)!!; val second = p.item(pair.second)!!
            assertEquals(first.promptLetter, second.promptLetter)
            assertEquals(first.answer, second.answer)
            assertEquals(first.options.toSet(), second.options.toSet())
            val distinct = distinctCorrectCheckCount(p, listOf(first.id, second.id), setOf(first.id, second.id))
            println("CC010_GROUP group=${index + 1} kind=COUNTER value=$distinct")
            LocalStore.openInMemory(context()).use { store ->
                attempt(p, store, first.id, first.answer)
                assertEquals(setOf(first.id), creditItemIds(p, store.v2LearnerView()))
                attempt(p, store, second.id, second.answer)
                assertEquals(setOf(first.id, second.id), creditItemIds(p, store.v2LearnerView()))
                val objective = p.unit(unit(p, second.id))!!.objectives.single { it.id == second.objectiveId }
                assertNotEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS, objectiveState(objective, store.v2LearnerView(), p))
                val required = objective.practiceStepIds.flatMap { p.step(it)!!.second.itemIds } + objective.checkItemIds
                val rest = required.filter { it !in creditItemIds(p, store.v2LearnerView()) }
                assertTrue(rest.isNotEmpty())
                rest.forEach { attempt(p, store, it, p.item(it)!!.answer) }
                assertEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS, objectiveState(objective, store.v2LearnerView(), p))
                println("CC010_GROUP group=${index + 1} kind=CREDIT noLeak=true otherWorkRequired=true")
            }
        }
    }

    @Test fun wrongResponsesRemainSeparateAndSurviveBackupResumeAndReopen() {
        val p = pack()
        for ((index, pair) in pairs.withIndex()) {
            val name = "repeat-credit-${System.nanoTime()}.db"
            val dir = File(context().cacheDir, "repeat-credit-${System.nanoTime()}")
            val target = p.item(pair.second)!!; val at = owner(p, target.id)
            try {
                LocalStore.openInMemory(context()).use { store ->
                    store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true,
                        currentUnitNumber = unit(p, target.id), startingPoint = "RESUME_FROM_BOOK",
                        currentSessionId = at.first.id, currentStepIndex = at.first.steps.indexOf(at.second)).verifiedResume(p))
                    for (id in listOf(pair.first, pair.second)) {
                        val item = p.item(id)!!
                        attempt(p, store, id, item.options.first { it != item.answer })
                    }
                    assertTrue(creditItemIds(p, store.v2LearnerView()).isEmpty())
                    assertTrue(resumeIsCurrent(p, store.v2LearnerView()))
                    val before = store.governedLearnerDocument()
                    val bytes = ByteArrayOutputStream()
                    writeGovernedBackup(bytes, "dev.russiancompanion", p.packId, p.packVersion, "FOLLOW_SYSTEM", before, emptyList())
                    val read = readGovernedBackup(bytes.toByteArray().inputStream(), "dev.russiancompanion", p.packId, p.packVersion, dir) as BackupReadResult.Valid
                    LocalStore.openNamed(context(), name).use { restored ->
                        assertEquals(GovernedReplaceOutcome.REPLACED, restored.replaceGovernedLearner(read.backup.document, emptyList()))
                    }
                    LocalStore.openNamed(context(), name).use { restored ->
                        assertEquals(before, restored.governedLearnerDocument())
                        assertTrue(creditItemIds(p, restored.v2LearnerView()).isEmpty())
                        assertTrue(resumeIsCurrent(p, restored.v2LearnerView()))
                        val selected = governedSessionSelections(p, restored.v2LearnerView())
                        for (id in listOf(pair.first, pair.second)) {
                            val item = p.item(id)!!
                            assertEquals(item.options.first { it != item.answer }, selected[governedSelectionKey(owner(p, id).second.id, id)])
                        }
                    }
                    println("CC010_GROUP group=${index + 1} kind=BACKUP wrongRowsPreserved=true resumeCurrent=true")
                }
            } finally { context().deleteDatabase(name); dir.deleteRecursively() }
        }
    }

    @Test fun retainedRecognitionChainsRestoreAllTeachingAndKeepOtherRequiredChecks() {
        val p = pack()
        val repeated = pairs.flatMap { listOf(it.first, it.second) }.toSet()
        val objectives = pairs.flatMap { listOf(p.item(it.first)!!.objectiveId, p.item(it.second)!!.objectiveId) }.toSet()
        assertEquals(8, objectives.size)
        for (id in objectives) {
            val objective = p.units.flatMap { it.objectives }.single { it.id == id }
            assertTrue(objective.checkItemIds.any { it !in repeated })
            val session = p.unit(objective.unitNumber)!!.sessions.single { id in it.objectiveIds && it.steps.any { step -> step.kind == "TEACH" } }
            val teaching = session.steps.filter { it.kind == "TEACH" }
            assertEquals(teaching, session.steps.takeWhile { it.kind != "GUIDED" }.filter { it.kind == "TEACH" })
            val repair = session.steps.single { it.kind == "REMEDIATE" }
            assertEquals(teaching.joinToString("\n\n") { it.body }, repair.body)
            assertEquals(teaching.flatMap { it.exampleIds }.distinct(), repair.exampleIds)
            assertEquals(objective.practiceStepIds, reviewedGuidedPractice(p, repair.id))
        }
        val validation = V2Validator.validate(p)
        assertTrue(validation.errors.joinToString("\n"), validation.ok)
    }

    private fun attempt(p: V2CurriculumPack, store: LocalStore, id: String, selected: String) {
        store.v2RecordGovernedAttempt(prepareGovernedAttempt(p, id, selected, PRESCRIBED_CHECK,
            owner(p, id).second.id, "2026-10-20T00:00:00Z")!!)
    }
    private fun owner(p: V2CurriculumPack, id: String) = p.units.flatMap { it.sessions }
        .flatMap { s -> s.steps.filter { id in it.itemIds }.map { s to it } }.single()
    private fun unit(p: V2CurriculumPack, id: String) = p.units.single { u -> u.sessions.any { it.id == owner(p, id).first.id } }.number
    private fun context(): Context = ApplicationProvider.getApplicationContext()
    private fun pack() = V2PackLoader.parse(File(if (File("app").isDirectory) "." else "..",
        "app/src/main/assets/content/v2-curriculum.json").readText())
    private val pairs = listOf(
        "u8-g-spelling-0" to "u8-i-plural-6", "u10-i-have-1" to "u10-i-absence-3",
        "u10-g-absence-0" to "u10-i-have-3", "u10-i-have-4" to "u10-i-absence-4",
        "u23-g-ranges-0" to "u23-i-ranges-2", "u23-g-during-event-0" to "u23-i-during-event-2",
        "u23-g-duration-0" to "u23-i-duration-2", "u24-g-concord-0" to "u24-i-concord-2",
    )
}
