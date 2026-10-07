package dev.russiancompanion.v2

import android.content.Context
import android.os.Looper
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.onAllNodesWithText
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.data.appearance.AppearanceStore
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.domain.appearance.AppearancePreference
import dev.russiancompanion.release.ReleaseBlocker
import dev.russiancompanion.release.curriculumBlockers
import dev.russiancompanion.v2.backup.BackupReadResult
import dev.russiancompanion.v2.backup.BackupRejection
import dev.russiancompanion.v2.backup.RESTORE_CANCELLED
import dev.russiancompanion.v2.backup.RESTORE_DONE
import dev.russiancompanion.v2.backup.RESTORE_FAILED
import dev.russiancompanion.v2.backup.RESTORE_RECOVERY_REQUIRED
import dev.russiancompanion.v2.backup.V2BackupFaults
import dev.russiancompanion.v2.backup.readGovernedBackup
import dev.russiancompanion.v2.backup.writeGovernedBackup
import dev.russiancompanion.v2.backup.writeStoreBackup
import dev.russiancompanion.v2.curriculum.GOVERNED_IDENTITY_SCHEMA
import dev.russiancompanion.v2.curriculum.OPTIONAL_PRACTICE
import dev.russiancompanion.v2.curriculum.PRESCRIBED_CHECK
import dev.russiancompanion.v2.curriculum.STEP_IDENTITY_SCHEMA
import dev.russiancompanion.v2.curriculum.UNRECOVERABLE_GOVERNED_IDENTITY
import dev.russiancompanion.v2.curriculum.V2CurriculumPack
import dev.russiancompanion.v2.curriculum.V2ObjectiveState
import dev.russiancompanion.v2.curriculum.V2PackLoader
import dev.russiancompanion.v2.curriculum.completedStepCounts
import dev.russiancompanion.v2.curriculum.continueTarget
import dev.russiancompanion.v2.curriculum.example
import dev.russiancompanion.v2.curriculum.governedSessionSelections
import dev.russiancompanion.v2.curriculum.item
import dev.russiancompanion.v2.curriculum.governedSelectionKey
import dev.russiancompanion.v2.curriculum.lastGovernedIdentityThreadForTests
import dev.russiancompanion.v2.curriculum.objectiveState
import dev.russiancompanion.v2.curriculum.openable
import dev.russiancompanion.v2.curriculum.practiceQueue
import dev.russiancompanion.v2.curriculum.practiceStepId
import dev.russiancompanion.v2.curriculum.prepareGovernedAttempt
import dev.russiancompanion.v2.curriculum.recordedSessionCount
import dev.russiancompanion.v2.curriculum.resumePositionDigest
import dev.russiancompanion.v2.curriculum.savedWorkEntries
import dev.russiancompanion.v2.curriculum.step
import dev.russiancompanion.v2.curriculum.stepContentDigest
import dev.russiancompanion.v2.curriculum.todaysSession
import dev.russiancompanion.v2.curriculum.unit
import dev.russiancompanion.v2.curriculum.verifiedResume
import androidx.lifecycle.ViewModelProvider
import dev.russiancompanion.v2.ui.V2App
import dev.russiancompanion.v2.ui.V2WriteCoordinator
import dev.russiancompanion.v2.ui.V2BackupCoordinator
import dev.russiancompanion.v2.ui.V2BackupPhase
import dev.russiancompanion.v2.ui.V2UiWork
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import java.io.File
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33], qualifiers = "w411dp-h914dp-xxhdpi")
class FullAuditCorrectionTest {
    @get:Rule
    val compose = createAndroidComposeRule<ComponentActivity>()

    private val stores = mutableListOf<LocalStore>()
    private val pack: V2CurriculumPack by lazy {
        V2PackLoader.parse(File(repo(), "app/src/main/assets/content/v2-curriculum.json").readText())
    }

    @After
    fun tearDown() {
        V2UiWork.blockForTests = null
        V2BackupFaults.reset()
        lastGovernedIdentityThreadForTests = null
        LocalStore.holdNextLearnerRefreshForTests?.countDown()
        LocalStore.holdNextLearnerRefreshForTests = null
        LocalStore.learnerRefreshEnteredForTests = null
        stores.forEach { store ->
            store.delayLearnerReadMillisForTests = 0
            store.holdGovernedAttemptForTests?.complete(Unit)
            store.holdGovernedAttemptForTests = null
            store.close()
        }
    }

    @Test
    fun f01_advancementSavesTheNextPositionWithItsDigestAndContinueSurvivesRestart() {
        val store = memory()
        satisfyUnitOne(store)
        val next = pack.unit(2)!!.sessions.first()
        val expected = checkNotNull(resumePositionDigest(pack, next.id, 0))
        compose.setContent { V2App(store = store, pack = pack, onRetryOpen = {}) }
        compose.onNodeWithText("Review advancement").assertIsDisplayed()
        compose.onNodeWithTag("v2-continue").performClick()
        compose.onNodeWithTag("v2-advance").performScrollTo().performClick()
        compose.onNodeWithText("Today").performClick()
        val profile = store.v2Profile()
        assertEquals(2, profile.currentUnitNumber)
        assertEquals(next.id, profile.currentSessionId)
        assertEquals(0, profile.currentStepIndex)
        assertEquals(expected, profile.resumeContentDigest)
        assertEquals(GOVERNED_IDENTITY_SCHEMA, profile.resumeIdentitySchema)
        assertTrue(1 in store.v2LearnerView().completedUnitNumbers)
        compose.onNodeWithText("Saved lesson cannot be verified").assertDoesNotExist()
        compose.onNodeWithTag("v2-today-unit").assertTextContains("Unit 2", substring = true)
        compose.onNodeWithTag("v2-continue").performClick()
        compose.onNodeWithText(next.steps.first().title).assertIsDisplayed()

        compose.onNodeWithTag("v2-back").performClick()
        reloadLearner()
        compose.onNodeWithText("Saved lesson cannot be verified").assertDoesNotExist()
        compose.onNodeWithTag("v2-today-unit").assertTextContains("Unit 2", substring = true)
        compose.onNodeWithTag("v2-continue").performClick()
        compose.onNodeWithText(next.steps.first().title).assertIsDisplayed()
        assertEquals(expected, store.v2Profile().resumeContentDigest)
    }

    @Test
    fun f01_unverifiedPositionDoesNotMutateTheProfile() = runBlocking {
        val store = memory()
        store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = 1))
        val before = store.governedLearnerFingerprint()
        listOf(
            "" to GOVERNED_IDENTITY_SCHEMA,
            "ab" to GOVERNED_IDENTITY_SCHEMA,
            UNRECOVERABLE_GOVERNED_IDENTITY to GOVERNED_IDENTITY_SCHEMA,
            "a".repeat(64) to 0,
            "a".repeat(64) to 2,
        ).forEach { (digest, schema) ->
            val failed = runCatching {
                store.v2AdvanceUnitAsync(1, "2026-10-07T12:00:00", 2, "u2-s-next", digest, schema)
            }
            assertTrue(failed.exceptionOrNull()?.message, failed.isFailure)
        }
        assertEquals(before, store.governedLearnerFingerprint())
        assertEquals(1, store.v2Profile().currentUnitNumber)
        assertEquals("", store.v2Profile().resumeContentDigest)
    }

    @Test
    fun f02_correctlyHashedInvalidProfilesAreRejectedBeforeMutation() {
        val store = memory()
        store.v2AddNote("step", "keep the usable database", "2026-10-07T12:00:00")
        val before = store.governedLearnerFingerprint()
        val valid = store.governedLearnerDocument()
        val validFile = tempZip()
        validFile.outputStream().use {
            writeGovernedBackup(it, APP, pack.packId, pack.packVersion, "DARK", valid, emptyList())
        }
        assertTrue(readBackup(validFile) is BackupReadResult.Valid)
        listOf(
            valid.copy(profile = valid.profile.copy(currentUnitNumber = 99)),
            valid.copy(profile = valid.profile.copy(dailyMinutes = 15)),
            valid.copy(profile = valid.profile.copy(startingPoint = "SKIP_AHEAD")),
            valid.copy(profile = valid.profile.copy(currentStepIndex = 99)),
            valid.copy(profile = valid.profile.copy(id = "remote")),
            valid.copy(profile = valid.profile.copy(resumeIdentitySchema = 2, resumeContentDigest = "a".repeat(64))),
            valid.copy(profile = valid.profile.copy(resumeIdentitySchema = 1, resumeContentDigest = "not-a-digest")),
            valid.copy(profile = valid.profile.copy(resumeIdentitySchema = 0, resumeContentDigest = "a".repeat(64))),
        ).forEach { invalid ->
            val file = tempZip()
            file.outputStream().use {
                writeGovernedBackup(it, APP, pack.packId, pack.packVersion, "DARK", invalid, emptyList())
            }
            val read = readBackup(file)
            assertTrue(read.toString(), read is BackupReadResult.Rejected)
            assertEquals(BackupRejection.MALFORMED, (read as BackupReadResult.Rejected).reason)
            assertEquals(before, store.governedLearnerFingerprint())
            assertEquals("keep the usable database", store.v2Notes().single().text)
        }
    }

    @Test
    fun f04_productionInstallAndReleaseValidationStayClosed() {
        assertTrue(pack.units.all { it.installState == "DEVELOPMENT" })
        assertTrue(pack.units.all { !it.openable(developmentVisible = false) })
        assertTrue(pack.units.all { it.openable(developmentVisible = true) })
        val installed = pack.units.first().copy(installState = "INSTALLED")
        assertTrue(installed.openable(developmentVisible = false))
        val root = repo()
        val current = curriculumBlockers(pack, root)
        assertFalse(current.contains(ReleaseBlocker.CURRICULUM_VALIDATION_FAILED))
        assertTrue(current.contains(ReleaseBlocker.V2_UNITS_NOT_INSTALLED))
        assertTrue(current.contains(ReleaseBlocker.LINGUISTIC_REVIEW_ABSENT))
        assertTrue(current.contains(ReleaseBlocker.PENGUIN_MAPPING_UNAPPROVED))
        val broken = curriculumBlockers(pack.copy(schemaVersion = 2), root)
        assertTrue(broken.contains(ReleaseBlocker.CURRICULUM_VALIDATION_FAILED))
        assertTrue(pack.units.all { it.installState == "DEVELOPMENT" })
        assertTrue(pack.unitReviews.all { it.status == "IMPLEMENTED_PENDING_FINAL_AUDIT" })
    }

    @Test
    fun f05_consecutiveDuplicatesAreSuppressedAndTheFinalSelectionSurvivesReload() {
        val step = pack.step("u1-st-vowels-check")!!.second
        val item = pack.item(step.itemIds.single())!!
        val first = item.options[0]
        val second = item.options[1]
        val store = memory()
        val prescribedStep = practiceStepId("prescribed-check")
        fun prescribed(selected: String, at: String) =
            prepareGovernedAttempt(pack, item.id, selected, PRESCRIBED_CHECK, step.id, at)!!
        store.v2RecordGovernedAttempt(prescribed(first, "2026-10-07T12:00:01"))
        store.v2RecordGovernedAttempt(prescribed(first, "2026-10-07T12:00:02"))
        assertEquals(1, store.v2Attempts().count { it.attemptRole == PRESCRIBED_CHECK })
        store.v2RecordGovernedAttempt(prescribed(second, "2026-10-07T12:00:03"))
        store.v2RecordGovernedAttempt(prescribed(first, "2026-10-07T12:00:04"))
        val prescribedRows = store.v2Attempts().filter { it.attemptRole == PRESCRIBED_CHECK }
        assertEquals(listOf(first, second, first), prescribedRows.map { it.selected })

        fun optional(selected: String, at: String) =
            prepareGovernedAttempt(pack, item.id, selected, OPTIONAL_PRACTICE, prescribedStep, at)!!
        store.v2RecordGovernedAttempt(optional(first, "2026-10-07T12:01:01"))
        store.v2RecordGovernedAttempt(optional(first, "2026-10-07T12:01:02"))
        store.v2RecordGovernedAttempt(optional(second, "2026-10-07T12:01:03"))
        store.v2RecordGovernedAttempt(optional(first, "2026-10-07T12:01:04"))
        val optionalRows = store.v2Attempts().filter { it.attemptRole == OPTIONAL_PRACTICE }
        assertEquals(listOf(first, second, first), optionalRows.map { it.selected })
        assertFalse(step.id in store.v2LearnerView().completedStepIds)

        remember(store, step.id)
        compose.setContent { V2App(store = store, pack = pack, onRetryOpen = {}) }
        compose.onNodeWithTag("v2-continue").performClick()
        compose.onNodeWithTag("v2-option-${item.id}-$first").performScrollTo().assertIsSelected()
        compose.onNodeWithTag("v2-option-${item.id}-$second").performScrollTo().performClick()
        compose.onNodeWithTag("v2-option-${item.id}-$first").performScrollTo().performClick()
        val afterScreen = store.v2Attempts().filter { it.attemptRole == PRESCRIBED_CHECK && it.stepId == step.id }
        assertEquals(first, afterScreen.last().selected)
        assertEquals(second, afterScreen[afterScreen.lastIndex - 1].selected)

        compose.onNodeWithTag("v2-back").performClick()
        reloadLearner()
        compose.onNodeWithTag("v2-continue").performClick()
        compose.onNodeWithTag("v2-option-${item.id}-$first").performScrollTo().assertIsSelected()
        val selections = governedSessionSelections(pack, store.v2LearnerView())
        assertEquals(first, selections[governedSelectionKey(step.id, item.id)])

        val labItem = practiceQueue(pack, store.v2LearnerView(), "alphabet").first()
        val labA = labItem.options[0]
        val labB = labItem.options[1]
        assertTrue(labItem.options.size >= 2)
        compose.onNodeWithTag("v2-back").performClick()
        compose.onNodeWithText("Practice").performClick()
        compose.onNodeWithText("Alphabet recognition").performScrollTo().performClick()
        chooseLab(labA)
        compose.onNodeWithTag("v2-lab-submit").performClick()
        chooseLab(labB)
        compose.onNodeWithTag("v2-lab-submit").performClick()
        chooseLab(labA)
        compose.onNodeWithTag("v2-lab-submit").performClick()
        val labRows = store.v2Attempts().filter { it.itemId == labItem.id && it.attemptRole == OPTIONAL_PRACTICE }
        assertEquals(listOf(labA, labB, labA), labRows.map { it.selected })
        assertTrue(store.v2StepProgress().none { it.stepId == step.id && it.identitySchema == STEP_IDENTITY_SCHEMA && it.contentDigest == stepContentDigest(pack, step) })
        compose.onNodeWithTag("v2-back").performClick()
        reloadLearner()
        compose.onNodeWithText("Alphabet recognition").performScrollTo().performClick()
        compose.onNodeWithTag("v2-lab-option-$labA").performScrollTo().assertIsSelected()
    }

    @Test
    fun f06_legacyStepRowsDoNotCountAsCurrentCompletion() {
        val unit = pack.unit(1)!!
        val session = unit.sessions.first()
        val first = session.steps.first()
        val store = memory()
        store.v2UpsertStepForTests(first.id, UNRECOVERABLE_GOVERNED_IDENTITY, 0, "2026-10-07T12:00:00")
        store.v2SaveProfile(
            store.v2Profile().copy(
                onboardingCompleted = true,
                currentUnitNumber = 1,
                currentSessionId = session.id,
                currentStepIndex = 0,
            ).verifiedResume(pack),
        )
        val legacy = store.v2LearnerView()
        assertFalse(completedStepCounts(pack, legacy, first.id))
        assertEquals(first.title, continueTarget(pack, legacy).title)
        assertEquals(session.id, todaysSession(pack, unit, legacy)?.id)
        assertEquals(0, recordedSessionCount(pack, unit, legacy))
        assertTrue(savedWorkEntries(pack, legacy).any { it.kind == "RESUME" && it.resumable && it.stepIndex == 0 })
        val objective = unit.objectives.first { candidate ->
            unit.sessions.any { candidate.id in it.objectiveIds && it.steps.isNotEmpty() }
        }
        val objectiveSteps = unit.sessions.filter { objective.id in it.objectiveIds }.flatMap { it.steps }
        objectiveSteps.forEach { step ->
            store.v2UpsertStepForTests(step.id, UNRECOVERABLE_GOVERNED_IDENTITY, 0, "2026-10-07T12:00:00")
        }
        assertEquals(V2ObjectiveState.NOT_STARTED, objectiveState(objective, store.v2LearnerView(), pack))
        assertEquals(0, store.v2StepProgress().first { it.stepId == first.id }.identitySchema)

        objectiveSteps.forEach { step ->
            store.v2UpsertStepForTests(step.id, stepContentDigest(pack, step), STEP_IDENTITY_SCHEMA, "2026-10-07T12:05:00")
        }
        val current = store.v2LearnerView()
        assertTrue(objectiveSteps.all { completedStepCounts(pack, current, it.id) })
        assertNotEquals(V2ObjectiveState.NOT_STARTED, objectiveState(objective, current, pack))
        if (session.steps.all { completedStepCounts(pack, current, it.id) }) {
            assertFalse(savedWorkEntries(pack, current).any { it.kind == "RESUME" && it.sessionId == session.id })
        }

        compose.setContent { V2App(store = store, pack = pack, onRetryOpen = {}) }
        val target = continueTarget(pack, store.v2LearnerView())
        compose.onNodeWithText(target.title).assertIsDisplayed()
        compose.onNodeWithText("Progress").performClick()
        compose.onNodeWithText(objective.title, substring = true).performScrollTo().assertIsDisplayed()
    }

    @Test
    fun f07_stepIdentityCoversDisplayedDependenciesAndFailsClosed() {
        val located = pack.step("u1-st-iotated")!!
        val step = located.second
        val original = stepContentDigest(pack, step)
        val example = pack.example(step.exampleIds.first())!!
        val editedExample = pack.copy(
            examples = pack.examples.map { if (it.id == example.id) it.copy(display = example.display + " ·") else it },
        )
        assertNotEquals(original, stepContentDigest(editedExample, step))
        val objective = pack.unit(1)!!.objectives.first { step.id in it.instructionStepIds || step.id in it.practiceStepIds || step.id in it.remediationStepIds }
        val editedRelationship = pack.copy(
            units = pack.units.map { unit ->
                if (unit.number != 1) {
                    unit
                } else {
                    unit.copy(
                        objectives = unit.objectives.map { item ->
                            if (item.id != objective.id) {
                                item
                            } else {
                                item.copy(
                                    instructionStepIds = item.instructionStepIds - step.id,
                                    practiceStepIds = item.practiceStepIds - step.id,
                                    remediationStepIds = item.remediationStepIds - step.id,
                                )
                            }
                        },
                    )
                }
            },
        )
        assertNotEquals(original, stepContentDigest(editedRelationship, step))

        val store = memory()
        store.v2UpsertStepForTests(step.id, original, GOVERNED_IDENTITY_SCHEMA, "2026-10-07T12:00:00")
        assertFalse(completedStepCounts(pack, store.v2LearnerView(), step.id))
        assertEquals(GOVERNED_IDENTITY_SCHEMA, store.v2StepProgress().single().identitySchema)
        store.v2UpsertStepForTests(step.id, "not-the-current-step", STEP_IDENTITY_SCHEMA, "2026-10-07T12:01:00")
        assertFalse(completedStepCounts(pack, store.v2LearnerView(), step.id))
        store.v2UpsertStepForTests(step.id, original, STEP_IDENTITY_SCHEMA, "2026-10-07T12:02:00")
        assertTrue(completedStepCounts(pack, store.v2LearnerView(), step.id))
        assertEquals(STEP_IDENTITY_SCHEMA, store.v2StepProgress().single().identitySchema)
        val written = store.v2CompleteGovernedStep(step.id, stepContentDigest(pack, step), "2026-10-07T12:03:00")
        assertEquals(Unit, written)
        assertEquals(STEP_IDENTITY_SCHEMA, store.v2StepProgress().single { it.stepId == step.id }.identitySchema)
    }

    @Test
    fun f08_and_f12_restoreOutcomesResetTheCoordinatorAndCleanStaging() = runBlocking {
        val store = memory()
        store.v2AddNote("step", "live note", "2026-10-07T12:00:00")
        val appearance = AppearanceStore.memory(AppearancePreference.DARK)
        val backup = tempZip()
        backup.outputStream().use {
            writeStoreBackup(store, it, APP, pack.packId, pack.packVersion, AppearancePreference.FOLLOW_SYSTEM.name)
        }
        store.v2AddNote("step", "only on this device", "2026-10-07T12:05:00")
        val before = store.governedLearnerFingerprint()
        val activity = Robolectric.buildActivity(androidx.activity.ComponentActivity::class.java).setup().get()
        val coordinator = V2BackupCoordinator.get(activity, store, APP, pack.packId, pack.packVersion, appearance)
        val recovery = File(context().filesDir, "backup-recovery")
        val marker = File(recovery, "rollback/keep-recovery.txt")
        marker.parentFile?.mkdirs()
        marker.writeText("keep")
        var restored = 0

        assertTrue(coordinator.begin(V2BackupPhase.PREPARING))
        coordinator.openBackup { backup.inputStream() }
        awaitPhase(coordinator, V2BackupPhase.AWAITING_CONFIRMATION)
        assertTrue(incomingStaging(recovery).isNotEmpty())
        coordinator.cancel()
        awaitPhase(coordinator, V2BackupPhase.CANCELLED)
        assertEquals(RESTORE_CANCELLED, coordinator.message)
        assertTrue(incomingStaging(recovery).isEmpty())
        assertEquals("keep", marker.readText())
        assertEquals(before, store.governedLearnerFingerprint())
        assertEquals(AppearancePreference.DARK, appearance.preference.first())
        assertTrue(coordinator.begin(V2BackupPhase.PREPARING))
        coordinator.cancel()

        assertTrue(coordinator.begin(V2BackupPhase.PREPARING))
        coordinator.openBackup { backup.inputStream() }
        awaitPhase(coordinator, V2BackupPhase.AWAITING_CONFIRMATION)
        V2BackupFaults.throwOnRestore = IllegalStateException("restore-broke")
        coordinator.confirmRestore { restored += 1 }
        awaitPhase(coordinator, V2BackupPhase.FAILED)
        assertEquals(RESTORE_FAILED, coordinator.message)
        assertEquals(1, restored)
        assertTrue(incomingStaging(recovery).isEmpty())
        assertEquals("keep", marker.readText())
        assertEquals(before, store.governedLearnerFingerprint())
        assertEquals(AppearancePreference.DARK, appearance.preference.first())
        assertTrue(coordinator.begin(V2BackupPhase.PREPARING))
        coordinator.cancel()

        assertTrue(coordinator.begin(V2BackupPhase.PREPARING))
        coordinator.openBackup { backup.inputStream() }
        awaitPhase(coordinator, V2BackupPhase.AWAITING_CONFIRMATION)
        V2BackupFaults.throwOnRestore = CancellationException("restore-cancelled")
        coordinator.confirmRestore { restored += 1 }
        awaitPhase(coordinator, V2BackupPhase.CANCELLED)
        assertEquals(RESTORE_CANCELLED, coordinator.message)
        assertTrue(incomingStaging(recovery).isEmpty())
        assertEquals(before, store.governedLearnerFingerprint())
        assertEquals(AppearancePreference.DARK, appearance.preference.first())
        assertTrue(coordinator.begin(V2BackupPhase.PREPARING))
        coordinator.cancel()

        assertTrue(coordinator.begin(V2BackupPhase.PREPARING))
        coordinator.openBackup { backup.inputStream() }
        awaitPhase(coordinator, V2BackupPhase.AWAITING_CONFIRMATION)
        V2BackupFaults.crashAfterFileSwapBeforeCommit = true
        coordinator.confirmRestore { restored += 1 }
        awaitPhase(coordinator, V2BackupPhase.FAILED)
        assertEquals(RESTORE_RECOVERY_REQUIRED, coordinator.message)
        val journal = journalFile(store)
        assertTrue(journal.isFile)
        assertTrue(journal.readText().isNotBlank())
        assertTrue(incomingStaging(recovery).isEmpty())
        assertEquals("keep", marker.readText())
        assertEquals(AppearancePreference.DARK, appearance.preference.first())
        store.sweepOrphanRecordingFiles()
        assertEquals(before, store.governedLearnerFingerprint())
        assertTrue(coordinator.begin(V2BackupPhase.PREPARING))
        coordinator.cancel()
        V2BackupFaults.reset()

        assertTrue(coordinator.begin(V2BackupPhase.PREPARING))
        coordinator.openBackup { backup.inputStream() }
        awaitPhase(coordinator, V2BackupPhase.AWAITING_CONFIRMATION)
        coordinator.confirmRestore { restored += 1 }
        awaitPhase(coordinator, V2BackupPhase.COMPLETED)
        assertEquals(RESTORE_DONE, coordinator.message)
        assertEquals(AppearancePreference.FOLLOW_SYSTEM, appearance.preference.first())
        assertTrue(incomingStaging(recovery).isEmpty())
        assertEquals("keep", marker.readText())
        assertEquals(listOf("live note"), store.v2Notes().map { it.text })
        assertTrue(restored >= 4)
        assertTrue(coordinator.begin(V2BackupPhase.PREPARING))
        coordinator.cancel()
    }

    @Test
    fun f09_savedNotesAndTutorQuestionsRemainReadableAfterRestart() {
        val located = pack.step("u1-st-orient")!!
        val note = "Private note kept in full after restart. " + "n".repeat(240)
        val question = "Tutor question kept in full after restart. " + "q".repeat(240)
        val store = memory()
        val at = "2026-10-07T15:04:00"
        remember(store, located.second.id)
        compose.setContent {
            V2App(store = store, pack = pack, onRetryOpen = {}, recordedAt = { at })
        }
        compose.onNodeWithTag("v2-continue").performClick()
        compose.onNodeWithTag("v2-note-draft").performScrollTo().performTextInput(question)
        compose.onNodeWithTag("v2-tutor").performScrollTo().performClick()
        compose.onNodeWithTag("v2-note-draft").performScrollTo().performTextReplacement(note)
        compose.onNodeWithTag("v2-save-note").performScrollTo().performClick()
        assertEquals(note, store.v2Notes().single().text)
        assertEquals(question, store.v2TutorQueue().single().note)
        assertTrue(store.v2Attempts().isEmpty())
        compose.onNodeWithText("Saved note, not scored: $note").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Tutor question, not scored: $question").performScrollTo().assertIsDisplayed()

        reloadLearner()
        compose.onNodeWithText("Saved note, not scored: $note").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Tutor question, not scored: $question").performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("v2-back").performClick()
        compose.onNodeWithTag("v2-open-saved-work").performScrollTo().performClick()
        compose.onNodeWithText("Not course content and not scored. $note").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Optional and not scored. This is not advancement evidence. $question")
            .performScrollTo()
            .assertIsDisplayed()
        compose.onNodeWithTag("v2-back").performClick()
        compose.onNodeWithTag("v2-continue").performClick()
        compose.onNodeWithText("Saved note, not scored: $note").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Tutor question, not scored: $question").performScrollTo().assertIsDisplayed()
        assertTrue(store.v2Attempts().isEmpty())
        assertFalse(located.second.id in store.v2StepProgress().map { it.stepId })
    }

    @Test
    fun f10_repeatedTapsDuringADelayedWriteLockTheItem() {
        V2UiWork.blockForTests = false
        val step = pack.step("u1-st-vowels-check")!!.second
        val item = pack.item(step.itemIds.single())!!
        val first = item.options[0]
        val second = item.options[1]
        val store = memory()
        val hold = kotlinx.coroutines.CompletableDeferred<Unit>()
        store.holdGovernedAttemptForTests = hold
        remember(store, step.id)
        compose.setContent { V2App(store = store, pack = pack, onRetryOpen = {}) }
        compose.onNodeWithTag("v2-continue").performClick()
        compose.onNodeWithTag("v2-option-${item.id}-$first").performScrollTo().performClick()
        compose.onNodeWithTag("v2-option-${item.id}-$second").performScrollTo().assertIsNotEnabled()
        compose.onNodeWithTag("v2-option-${item.id}-$first").performScrollTo().assertIsNotEnabled()
        assertTrue(store.v2Attempts().isEmpty())
        hold.complete(Unit)
        awaitAttempts(store, 1)
        assertEquals(first, store.v2Attempts().single().selected)

        val labItem = practiceQueue(pack, store.v2LearnerView(), "alphabet").first { it.options.size >= 2 }
        val labHold = kotlinx.coroutines.CompletableDeferred<Unit>()
        store.holdGovernedAttemptForTests = labHold
        compose.onNodeWithTag("v2-back").performClick()
        compose.onNodeWithText("Practice").performClick()
        compose.onNodeWithText("Alphabet recognition").performScrollTo().performClick()
        val labA = labItem.options[0]
        val labB = labItem.options[1]
        chooseLab(labA)
        compose.onNodeWithTag("v2-lab-submit").performClick()
        compose.onNodeWithTag("v2-lab-option-$labB").performScrollTo().assertIsNotEnabled()
        compose.onNodeWithTag("v2-lab-submit").assertIsNotEnabled()
        assertEquals(1, store.v2Attempts().size)
        labHold.complete(Unit)
        awaitAttempts(store, 2)
        assertEquals(1, store.v2Attempts().count { it.itemId == labItem.id })
    }

    @Test
    fun f11_delayedLearnerReadLeavesTheMainThreadFree() {
        V2UiWork.blockForTests = false
        val store = memory()
        store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = 1))
        store.delayLearnerReadMillisForTests = 1_200
        compose.setContent { V2App(store = store, pack = pack, onRetryOpen = {}) }
        assertTrue(compose.onAllNodesWithText("Loading saved progress").fetchSemanticsNodes().isNotEmpty())
        val reader = store.lastLearnerReadThreadForTests
        assertNotEquals(Looper.getMainLooper().thread, reader)
        assertTrue(reader!!.name.startsWith("russian-companion-store"))
        store.delayLearnerReadMillisForTests = 0
        awaitTag("v2-continue")
    }

    @Test
    fun f11_completionValidationReadsAndIdentifiesOffTheMainThread() {
        V2UiWork.blockForTests = false
        val located = pack.step("u1-st-orient")!!
        val store = memory()
        remember(store, located.second.id)
        compose.setContent { V2App(store = store, pack = pack, onRetryOpen = {}) }
        awaitTag("v2-continue")
        val gate = CountDownLatch(1)
        val entered = CountDownLatch(1)
        LocalStore.holdNextLearnerRefreshForTests = gate
        LocalStore.learnerRefreshEnteredForTests = entered
        store.delayLearnerReadMillisForTests = 400
        compose.onNodeWithTag("v2-continue").performClick()
        compose.onNodeWithTag("v2-session-continue").performClick()
        val started = System.currentTimeMillis()
        while (store.v2StepProgress().none { it.stepId == located.second.id } && System.currentTimeMillis() - started < 8_000) {
            compose.waitForIdle()
            Thread.sleep(20)
        }
        assertEquals(listOf(located.second.id), store.v2StepProgress().map { it.stepId })
        assertEquals(STEP_IDENTITY_SCHEMA, store.v2StepProgress().single().identitySchema)
        assertNotEquals(Looper.getMainLooper().thread, store.lastLearnerReadThreadForTests)
        assertTrue(store.lastLearnerReadThreadForTests!!.name.startsWith("russian-companion-store"))
        val identity = lastGovernedIdentityThreadForTests
        assertNotEquals(Looper.getMainLooper().thread, identity)
        assertFalse(identity!!.name.startsWith("russian-companion-store"))
        assertTrue(entered.await(5, TimeUnit.SECONDS))
        gate.countDown()
        awaitTag("v2-step-title")
    }

    private fun satisfyUnitOne(store: LocalStore) {
        val unit = pack.unit(1)!!
        unit.sessions.flatMap { it.steps }.forEach { step ->
            store.v2CompleteGovernedStep(step.id, stepContentDigest(pack, step), "2026-10-07T12:00:00")
        }
        unit.sessions.flatMap { it.steps }.flatMap { it.itemIds }.distinct().forEach { id ->
            val item = pack.item(id)!!
            val stepId = unit.sessions.flatMap { it.steps }.first { id in it.itemIds }.id
            store.v2RecordGovernedAttempt(
                prepareGovernedAttempt(pack, item.id, item.answer, PRESCRIBED_CHECK, stepId, "2026-10-07T12:00:00")!!,
            )
        }
        store.v2AttestBook(1, "2026-10-07T12:00:00")
        store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = 1))
    }

    private fun remember(store: LocalStore, stepId: String) {
        val located = pack.step(stepId)!!
        store.v2SaveProfile(
            store.v2Profile().copy(
                onboardingCompleted = true,
                currentUnitNumber = 1,
                currentSessionId = located.first.id,
                currentStepIndex = located.first.steps.indexOfFirst { it.id == stepId },
            ).verifiedResume(pack),
        )
    }

    private fun reloadLearner() {
        val writes = ViewModelProvider(compose.activity)[V2WriteCoordinator::class.java]
        writes.reloadAfterRestore()
        compose.waitForIdle()
    }

    private fun chooseLab(option: String) {
        compose.onNodeWithTag("v2-lab-option-$option").performScrollTo().performClick()
    }

    private fun awaitAttempts(store: LocalStore, count: Int) {
        val started = System.currentTimeMillis()
        while (store.v2Attempts().size < count && System.currentTimeMillis() - started < 8_000) {
            compose.waitForIdle()
            Thread.sleep(20)
        }
        assertEquals(count, store.v2Attempts().size)
    }

    private fun awaitTag(tag: String) {
        val started = System.currentTimeMillis()
        while (System.currentTimeMillis() - started < 8_000) {
            compose.waitForIdle()
            if (compose.onAllNodes(androidx.compose.ui.test.hasTestTag(tag), useUnmergedTree = true).fetchSemanticsNodes().isNotEmpty()) {
                return
            }
            Thread.sleep(20)
        }
        compose.onNodeWithTag(tag).assertExists()
    }

    private fun awaitPhase(coordinator: V2BackupCoordinator, phase: V2BackupPhase) {
        val started = System.currentTimeMillis()
        while (coordinator.phase != phase && System.currentTimeMillis() - started < 10_000) {
            Shadows.shadowOf(Looper.getMainLooper()).idle()
            Thread.sleep(15)
        }
        assertEquals(phase, coordinator.phase)
    }

    private fun incomingStaging(recovery: File): List<File> {
        return recovery.listFiles().orEmpty().filter { it.name.startsWith("incoming-") }
    }

    private fun journalFile(store: LocalStore): File {
        val root = checkNotNull(store.recordingDirectory())
        return File(checkNotNull(root.parentFile), "backup-replace-${root.name}.json")
    }

    private fun readBackup(file: File) =
        readGovernedBackup(file.inputStream(), APP, pack.packId, pack.packVersion, tempDir())

    private fun memory(): LocalStore = LocalStore.openInMemory(context()).also { stores += it }

    private fun context(): Context = ApplicationProvider.getApplicationContext()

    private fun tempZip(): File = File.createTempFile("audit-backup", ".zip").apply { deleteOnExit() }

    private fun tempDir(): File = File.createTempFile("audit-stage", "").apply {
        delete()
        mkdirs()
        deleteOnExit()
    }

    private fun repo(): File {
        var dir = File(checkNotNull(System.getProperty("user.dir")))
        while (!File(dir, "settings.gradle.kts").isFile) dir = checkNotNull(dir.parentFile)
        return dir
    }

    private companion object {
        const val APP = "dev.russiancompanion"
    }
}
