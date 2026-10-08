package dev.russiancompanion.v2

import android.content.Context
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.lifecycle.ViewModelProvider
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.data.appearance.AppearanceStore
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.data.local.MIGRATION_8_9
import dev.russiancompanion.domain.appearance.AppearancePreference
import dev.russiancompanion.v2.backup.BackupReadResult
import dev.russiancompanion.v2.backup.BackupAttempt
import dev.russiancompanion.v2.backup.RESTORE_CANCELLED
import dev.russiancompanion.v2.backup.RESTORE_DONE
import dev.russiancompanion.v2.backup.RESTORE_FAILED
import dev.russiancompanion.v2.backup.RESTORE_LEARNER_REPLACED_APPEARANCE_FAILED
import dev.russiancompanion.v2.backup.V2BackupFaults
import dev.russiancompanion.v2.backup.backupJson
import dev.russiancompanion.v2.backup.readGovernedBackup
import dev.russiancompanion.v2.backup.restoreStoreBackup
import dev.russiancompanion.v2.backup.writeStoreBackup
import dev.russiancompanion.v2.curriculum.PRESCRIBED_CHECK
import dev.russiancompanion.v2.curriculum.STEP_IDENTITY_SCHEMA
import dev.russiancompanion.v2.curriculum.V2CurriculumPack
import dev.russiancompanion.v2.curriculum.V2OwnedEditionMapping
import dev.russiancompanion.v2.curriculum.V2PackLoader
import dev.russiancompanion.v2.curriculum.V2Session
import dev.russiancompanion.v2.curriculum.V2Step
import dev.russiancompanion.v2.curriculum.V2UnitReviewRecord
import dev.russiancompanion.v2.curriculum.V2Validator
import dev.russiancompanion.v2.curriculum.canonicalParts
import dev.russiancompanion.v2.curriculum.completedStepCounts
import dev.russiancompanion.v2.curriculum.creditItemIds
import dev.russiancompanion.v2.curriculum.example
import dev.russiancompanion.v2.curriculum.governedSelectionKey
import dev.russiancompanion.v2.curriculum.governedSessionSelections
import dev.russiancompanion.v2.curriculum.item
import dev.russiancompanion.v2.curriculum.lastGovernedIdentityThreadForTests
import dev.russiancompanion.v2.curriculum.lastGovernedSnapshotLabelForTests
import dev.russiancompanion.v2.curriculum.prepareGovernedAttempt
import dev.russiancompanion.v2.curriculum.reference
import dev.russiancompanion.v2.curriculum.step
import dev.russiancompanion.v2.curriculum.stepContentDigest
import dev.russiancompanion.v2.curriculum.unit
import dev.russiancompanion.v2.data.V2AttemptEntity
import dev.russiancompanion.v2.ui.StoreV2Persistence
import dev.russiancompanion.v2.ui.V2App
import dev.russiancompanion.v2.ui.V2BackupCoordinator
import dev.russiancompanion.v2.ui.V2BackupPhase
import dev.russiancompanion.v2.ui.V2OutcomeStatus
import dev.russiancompanion.v2.ui.V2UiWork
import dev.russiancompanion.v2.ui.V2WriteCoordinator
import dev.russiancompanion.v2.ui.sweepOrphanIncoming
import kotlinx.coroutines.CompletableDeferred
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
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33], qualifiers = "w411dp-h914dp-xxhdpi")
class ReauditCorrectionTest {
    @get:Rule
    val compose = createAndroidComposeRule<ComponentActivity>()

    private val stores = mutableListOf<LocalStore>()
    private val pack: V2CurriculumPack by lazy {
        V2PackLoader.parse(File(repo(), "app/src/main/assets/content/v2-curriculum.json").readText())
    }

    @After
    fun tearDown() {
        V2UiWork.blockForTests = null
        V2UiWork.reconstructionDelay = null
        V2UiWork.reconstructionEnteredForTests = null
        V2BackupFaults.reset()
        lastGovernedIdentityThreadForTests = null
        lastGovernedSnapshotLabelForTests = null
        LocalStore.learnerReadFailuresRemainingForTests.set(0)
        stores.forEach { store ->
            store.holdBeforeProfileSaveForTests?.complete(Unit)
            store.holdBeforeCompletionWriteForTests?.complete(Unit)
            store.holdGovernedAttemptForTests?.complete(Unit)
            store.close()
        }
    }

    @Test
    fun r01_delayedProfileSaveAndCompletionCannotOverwriteRestore() {
        V2UiWork.blockForTests = false
        val store = memory()
        val sessions = pack.unit(1)!!.sessions
        val kept = sessions.first()
        val other = sessions[1]
        place(store, kept.id, 0)
        store.v2AddNote("kept", "backup note", "2026-10-07T12:00:00")
        val backup = tempZip()
        backup.outputStream().use { writeStoreBackup(store, it, APP, pack.packId, pack.packVersion, "DARK") }
        store.v2AddNote("kept", "live note", "2026-10-07T12:06:00")
        val profileHold = CompletableDeferred<Unit>()
        val profileEntered = CountDownLatch(1)
        store.holdBeforeProfileSaveForTests = profileHold
        store.profileSaveEnteredForTests = profileEntered
        compose.setContent { V2App(store = store, pack = pack, onRetryOpen = {}) }
        awaitTag("v2-continue")
        compose.onNodeWithText("Course").performClick()
        compose.onNodeWithTag("v2-unit-row-1").performScrollTo().performClick()
        compose.onNodeWithText(other.title).performScrollTo().performClick()
        awaitLatch(profileEntered)

        val restoredFingerprint = backupFingerprint(backup)
        restoreBackup(store, backup)
        profileHold.complete(Unit)
        idleUntil("stale position write finished") { !writeCoordinator().hasActiveWork() }
        assertEquals(kept.id, store.v2Profile().currentSessionId)
        assertEquals(0, store.v2Profile().currentStepIndex)
        assertEquals(listOf("backup note"), store.v2Notes().map { it.text })
        assertEquals(restoredFingerprint, store.governedLearnerFingerprint())

        store.holdBeforeProfileSaveForTests = null
        store.profileSaveEnteredForTests = null
        val completionHold = CompletableDeferred<Unit>()
        val completionEntered = CountDownLatch(1)
        store.holdBeforeCompletionWriteForTests = completionHold
        store.completionWriteEnteredForTests = completionEntered
        compose.onNodeWithTag("v2-back").performClick()
        compose.onNodeWithText("Today").performClick()
        awaitTag("v2-continue")
        compose.onNodeWithTag("v2-continue").performClick()
        awaitTag("v2-session-continue")
        compose.onNodeWithTag("v2-session-continue").performClick()
        awaitLatch(completionEntered)
        assertTrue(store.v2StepProgress().none { it.stepId == kept.steps.first().id })
        restoreBackup(store, backup)
        completionHold.complete(Unit)
        idleUntil("stale completion finished") { !writeCoordinator().hasActiveWork() }
        assertTrue(store.v2StepProgress().none { it.stepId == kept.steps.first().id })
        assertEquals(restoredFingerprint, store.governedLearnerFingerprint())
    }

    @Test
    fun r01_openReplacementRejectsQueuedWritesBeforeMutation() = runBlocking {
        val store = memory()
        val epoch = store.learnerMutationEpoch()
        val profile = store.v2Profile().copy(currentUnitNumber = 4, currentSessionId = "stale-session")
        store.openLearnerReplacement()
        assertFalse(store.v2SaveProfileIfCurrentAsync(epoch, profile))
        assertFalse(store.v2CompleteStepIfCurrentAsync(epoch, "u1-st-orient", "2026-10-07T12:00:00", "ab".repeat(32)))
        assertFalse(store.v2AddNoteIfCurrent(epoch, "step", "nope", "2026-10-07T12:00:00", "ab".repeat(32)))
        assertEquals(1, store.v2Profile().currentUnitNumber)
        assertTrue(store.v2StepProgress().isEmpty())
        assertTrue(store.v2Notes().isEmpty())
        store.closeLearnerReplacement()
        assertFalse(store.learnerMutationCurrent(epoch))
    }

    @Test
    fun r02_laterUnitInstallationUsesTheFullValidator() {
        val root = repo()
        assertTrue(pack.units.all { it.installState == "DEVELOPMENT" })
        assertFalse(V2Validator.installationRequiresPronunciationAudio(3))
        assertTrue(V2Validator.installationRequiresPronunciationAudio(1))
        assertTrue(V2Validator.installationRequiresHandwritingAsset(2))
        val shipped = V2Validator.validate(pack, root)
        assertTrue(shipped.errors.joinToString("\n"), shipped.ok)

        val missingReview = pack.copy(
            units = pack.units.map { unit ->
                if (unit.number == 3) unit.copy(installState = "INSTALLED") else unit
            },
        )
        val reviewErrors = V2Validator.validate(missingReview, root).errors
        assertTrue(reviewErrors.joinToString("\n"), reviewErrors.any { it.contains("review") })
        assertTrue(reviewErrors.joinToString("\n"), reviewErrors.any { it.contains("mapping") })
        assertFalse(reviewErrors.any { it.contains("Only Units 1-2 may be installed") })

        val missingMapping = admissibleUnit(pack, 3).let { prepared ->
            prepared.copy(
                units = prepared.units.map { unit ->
                    if (unit.number == 3) unit.copy(mappingStatus = "PENDING_CURRICULUM_REVIEWER") else unit
                },
                ownedEditionMappings = prepared.ownedEditionMappings.filter { it.unitNumber != 3 },
            )
        }
        val mappingErrors = V2Validator.validate(missingMapping, root).errors
        assertTrue(mappingErrors.joinToString("\n"), mappingErrors.any { it.contains("mapping") })
        assertFalse(mappingErrors.any { it.contains("Only Units 1-2 may be installed") })

        val missingAudio = V2Validator.validate(admissibleUnit(pack, 1), root).errors
        assertTrue(missingAudio.joinToString("\n"), missingAudio.any { it.contains("requires a mapped pronunciation audio record") })
        assertFalse(missingAudio.any { it.contains("Only Units 1-2 may be installed") })

        val later = admissibleUnit(pack, 3)
        val laterResult = V2Validator.validate(later, root)
        assertTrue(laterResult.errors.joinToString("\n"), laterResult.ok)
        assertEquals("INSTALLED", later.unit(3)!!.installState)
        assertFalse(laterResult.errors.any { it.contains("pronunciation audio") })
        assertTrue(later.units.filter { it.number != 3 }.all { it.installState == "DEVELOPMENT" })
        assertTrue(pack.units.all { it.installState == "DEVELOPMENT" })
    }

    @Test
    fun r03_acceptanceProceduresMatchNavigationAndStayUnexecuted() {
        val root = repo()
        val instructions = File(root, "release/acceptance/DEVICE_ACCEPTANCE.md").readText()
        assertTrue(instructions.contains("Progress` → `Settings, appearance, and attributions` →"))
        assertTrue(instructions.contains("`Attributions and licenses`"))
        assertTrue(instructions.contains("The exact sentence `Share-alike confirmation: Confirmed` is not a"))
        assertTrue(instructions.contains("Displayed label is `Share-alike:`") || instructions.contains("the displayed label is `Share-alike:`"))
        assertTrue(instructions.contains("Alphabet recognition"))
        assertTrue(instructions.contains("optional practice does not complete a required check"))
        assertTrue(instructions.contains("Obsolete label `Optional drills`") || instructions.contains("obsolete label `Optional drills`"))
        assertTrue(instructions.contains("Retrieval is mandatory"))
        assertTrue(instructions.contains("Restore replaces the learner records"))
        assertTrue(instructions.contains("not current prescribed credit"))
        assertTrue(instructions.contains("Physical reboot is a power-cycle"))
        assertTrue(instructions.contains("not an in-memory screen reload"))
        assertTrue(instructions.contains("Do not rename a Markdown form"))
        assertTrue(instructions.contains("schemaVersion` 2"))
        assertTrue(instructions.contains("Automated unit"))
        assertTrue(instructions.contains("are not physical evidence"))
        listOf("AT-001", "AT-002", "AT-004", "AT-008", "AT-009", "AT-010", "AT-020", "AT-030").forEach { id ->
            val form = File(root, "release/acceptance/forms/$id.md").readText()
            val template = File(root, "release/acceptance/templates/$id.md").readText()
            assertTrue(form.contains("NOT_EXECUTED"))
            assertTrue(template.contains("NOT_EXECUTED"))
            assertTrue(template.contains("Do not rename"))
            assertTrue(template.contains("schemaVersion 2"))
            assertFalse(form.contains("Result: **PASS**"))
            assertFalse(template.contains("copy this completed form"))
        }
    }

    @Test
    fun r04_realCancellationBeforeDuringAndAfterReplacement() {
        val store = memory()
        store.v2AddNote("kept", "backup note", "2026-10-07T12:00:00")
        val backup = tempZip()
        backup.outputStream().use { writeStoreBackup(store, it, APP, pack.packId, pack.packVersion, "DARK") }
        store.v2AddNote("kept", "live note", "2026-10-07T12:05:00")
        val before = store.governedLearnerFingerprint()
        val activity = Robolectric.buildActivity(ComponentActivity::class.java).setup().get()
        val appearance = AppearanceStore.memory(AppearancePreference.DARK)
        val coordinator = V2BackupCoordinator.get(activity, store, APP, pack.packId, pack.packVersion, appearance)

        val beforeMutation = CompletableDeferred<Unit>()
        V2BackupFaults.beforeMutationHold = beforeMutation
        assertTrue(coordinator.begin(V2BackupPhase.PREPARING))
        coordinator.openBackup { backup.inputStream() }
        awaitPhase(coordinator, V2BackupPhase.AWAITING_CONFIRMATION)
        var restored = 0
        coordinator.confirmRestore { restored += 1 }
        idleUntil("waiting before mutation") { coordinator.phase == V2BackupPhase.RESTORING }
        coordinator.cancelWorkForTests()
        idleUntil("cancelled before mutation") { coordinator.phase == V2BackupPhase.CANCELLED }
        assertEquals(RESTORE_CANCELLED, coordinator.message)
        assertEquals(0, restored)
        assertEquals(before, store.governedLearnerFingerprint())
        assertEquals(listOf("backup note", "live note"), store.v2Notes().map { it.text })

        V2BackupFaults.reset()
        val entered = CountDownLatch(1)
        val during = CountDownLatch(1)
        V2BackupFaults.replacementEntered = entered
        V2BackupFaults.holdDuringReplacement = during
        val duringCoordinator = newCoordinator(store, appearance)
        assertTrue(duringCoordinator.begin(V2BackupPhase.PREPARING))
        duringCoordinator.openBackup { backup.inputStream() }
        awaitPhase(duringCoordinator, V2BackupPhase.AWAITING_CONFIRMATION)
        duringCoordinator.confirmRestore { restored += 1 }
        awaitLatch(entered)
        duringCoordinator.cancelWorkForTests()
        during.countDown()
        awaitPhase(duringCoordinator, V2BackupPhase.COMPLETED)
        assertEquals(RESTORE_DONE, duringCoordinator.message)
        assertNotEquals(RESTORE_CANCELLED, duringCoordinator.message)
        assertEquals(listOf("backup note"), store.v2Notes().map { it.text })
        assertNotEquals(before, store.governedLearnerFingerprint())

        store.v2AddNote("kept", "after commit", "2026-10-07T12:07:00")
        val afterLive = store.governedLearnerFingerprint()
        V2BackupFaults.reset()
        val afterHold = CompletableDeferred<Unit>()
        V2BackupFaults.holdAfterCommit = afterHold
        val afterCoordinator = newCoordinator(store, appearance)
        assertTrue(afterCoordinator.begin(V2BackupPhase.PREPARING))
        afterCoordinator.openBackup { backup.inputStream() }
        awaitPhase(afterCoordinator, V2BackupPhase.AWAITING_CONFIRMATION)
        afterCoordinator.confirmRestore { restored += 1 }
        idleUntil("replacement committed") {
            store.governedLearnerFingerprint() != afterLive && afterCoordinator.phase == V2BackupPhase.RESTORING
        }
        afterCoordinator.cancelWorkForTests()
        awaitPhase(afterCoordinator, V2BackupPhase.COMPLETED)
        assertEquals(RESTORE_DONE, afterCoordinator.message)
        assertEquals(listOf("backup note"), store.v2Notes().map { it.text })
        assertTrue(restored >= 2)
    }

    @Test
    fun r05_delayedReconstructionCannotReplaceANewerOrRestoredSelection() {
        V2UiWork.blockForTests = true
        val located = pack.step("u1-st-vowels-check")!!
        val step = located.second
        val item = pack.item(step.itemIds.single())!!
        val first = item.options[0]
        val second = item.options[1]
        val store = memory()
        store.v2RecordGovernedAttempt(
            prepareGovernedAttempt(pack, item.id, first, PRESCRIBED_CHECK, step.id, "2026-10-07T12:00:01")!!,
        )
        val activity = Robolectric.buildActivity(ComponentActivity::class.java).setup().get()
        val coordinator = V2WriteCoordinator.get(activity, StoreV2Persistence(store))
        coordinator.bindCurriculum(pack)
        val key = governedSelectionKey(step.id, item.id)
        assertEquals(first, coordinator.selections[key])

        V2UiWork.blockForTests = false
        val older = CompletableDeferred<Unit>()
        val started = AtomicInteger()
        V2UiWork.reconstructionDelay = { if (started.incrementAndGet() == 1) older.await() }
        val entered = CountDownLatch(1)
        V2UiWork.reconstructionEnteredForTests = entered
        coordinator.bindCurriculum(pack.copy())
        awaitLatch(entered)
        coordinator.confirmSelection(step.id, item.id, second)
        LocalStore.learnerReadFailuresRemainingForTests.set(1)
        var committed = false
        assertTrue(coordinator.submit("refresh-fail", onCommitted = { committed = true }) { })
        idleUntil("refresh failed") { committed && coordinator.banner == V2OutcomeStatus.REFRESH_FAILED }
        assertEquals(second, coordinator.selections[key])
        older.complete(Unit)
        idleUntil("older reconstruction settled") { coordinator.selections[key] == second }
        assertEquals(second, coordinator.selections[key])
        assertEquals(V2OutcomeStatus.REFRESH_FAILED, coordinator.banner)

        val firstHold = CompletableDeferred<Unit>()
        val secondHold = CompletableDeferred<Unit>()
        val queue = LinkedBlockingQueue<CompletableDeferred<Unit>>()
        queue.add(firstHold)
        queue.add(secondHold)
        val both = CountDownLatch(2)
        V2UiWork.reconstructionEnteredForTests = both
        V2UiWork.reconstructionDelay = { queue.take().await() }
        coordinator.bindCurriculum(pack.copy())
        coordinator.bindCurriculum(pack.copy())
        awaitLatch(both)
        coordinator.confirmSelection(step.id, item.id, second)
        secondHold.complete(Unit)
        idleUntil("newer reconstruction published") { coordinator.selections[key] == second }
        firstHold.complete(Unit)
        idleUntil("older reconstruction discarded") { coordinator.selections[key] == second }
        assertEquals(second, coordinator.selections[key])

        coordinator.confirmSelection(step.id, item.id, second)
        val restoreHold = CompletableDeferred<Unit>()
        val restoreEntered = CountDownLatch(1)
        V2UiWork.reconstructionEnteredForTests = restoreEntered
        V2UiWork.reconstructionDelay = { restoreHold.await() }
        coordinator.reloadAfterRestore()
        awaitLatch(restoreEntered)
        restoreHold.complete(Unit)
        idleUntil("restored selection published") {
            coordinator.learner != null && coordinator.selections[key] == first
        }
    }

    @Test
    fun r06_previewFailuresResetTheCoordinatorAndRemoveStaging() {
        val store = memory()
        store.v2AddNote("kept", "still here", "2026-10-07T12:00:00")
        val before = store.governedLearnerFingerprint()
        val backup = tempZip()
        backup.outputStream().use { writeStoreBackup(store, it, APP, pack.packId, pack.packVersion, "DARK") }
        val activity = Robolectric.buildActivity(ComponentActivity::class.java).setup().get()
        val coordinator = V2BackupCoordinator.get(
            activity,
            store,
            APP,
            pack.packId,
            pack.packVersion,
            AppearanceStore.memory(AppearancePreference.DARK),
        )
        val recovery = File(activity.applicationContext.filesDir, "backup-recovery")
        assertTrue(coordinator.begin(V2BackupPhase.PREPARING))
        coordinator.openBackup { error("provider-open") }
        awaitPhase(coordinator, V2BackupPhase.FAILED)
        assertEquals(RESTORE_FAILED, coordinator.message)
        assertTrue(incomingStaging(recovery).isEmpty())
        assertEquals(before, store.governedLearnerFingerprint())
        assertTrue(coordinator.begin(V2BackupPhase.PREPARING))
        coordinator.cancel()

        V2BackupFaults.throwOnCurrentDocument = IllegalStateException("current-document")
        assertTrue(coordinator.begin(V2BackupPhase.PREPARING))
        coordinator.openBackup { backup.inputStream() }
        awaitPhase(coordinator, V2BackupPhase.FAILED)
        assertEquals(RESTORE_FAILED, coordinator.message)
        assertTrue(incomingStaging(recovery).isEmpty())
        assertEquals(before, store.governedLearnerFingerprint())
        assertEquals(listOf("still here"), store.v2Notes().map { it.text })
        assertTrue(coordinator.begin(V2BackupPhase.PREPARING))
    }

    @Test
    fun r07_appearanceFailureTeardownAndOrphanSweep() {
        val store = memory()
        store.v2AddNote("kept", "backup note", "2026-10-07T12:00:00")
        val backup = tempZip()
        backup.outputStream().use {
            writeStoreBackup(store, it, APP, pack.packId, pack.packVersion, AppearancePreference.FOLLOW_SYSTEM.name)
        }
        store.v2AddNote("kept", "live note", "2026-10-07T12:05:00")
        val activity = Robolectric.buildActivity(ComponentActivity::class.java).setup().get()
        val recovery = File(activity.applicationContext.filesDir, "backup-recovery")
        val orphan = File(recovery, "incoming-orphan").apply { mkdirs() }
        val marker = File(recovery, "rollback/keep-recovery.txt")
        marker.parentFile?.mkdirs()
        marker.writeText("keep")
        val appearance = AppearanceStore.failing(AppearancePreference.DARK, writeFails = true)
        val coordinator = V2BackupCoordinator.get(activity, store, APP, pack.packId, pack.packVersion, appearance)
        assertFalse(orphan.exists())
        assertEquals("keep", marker.readText())
        assertTrue(coordinator.begin(V2BackupPhase.PREPARING))
        coordinator.openBackup { backup.inputStream() }
        awaitPhase(coordinator, V2BackupPhase.AWAITING_CONFIRMATION)
        val staged = incomingStaging(recovery).single()
        val other = File(recovery, "incoming-other").apply { mkdirs() }
        coordinator.sweepOrphansForTests()
        assertTrue(staged.exists())
        assertFalse(other.exists())
        assertEquals("keep", marker.readText())
        activity.viewModelStore.clear()
        assertTrue(incomingStaging(recovery).isEmpty())
        assertEquals("keep", marker.readText())

        val again = newCoordinator(store, appearance)
        assertTrue(again.begin(V2BackupPhase.PREPARING))
        again.openBackup { backup.inputStream() }
        awaitPhase(again, V2BackupPhase.AWAITING_CONFIRMATION)
        again.confirmRestore { }
        awaitPhase(again, V2BackupPhase.COMPLETED)
        assertEquals(RESTORE_LEARNER_REPLACED_APPEARANCE_FAILED, again.message)
        assertEquals(AppearancePreference.DARK, runBlocking { appearance.preference.first() })
        assertEquals(listOf("backup note"), store.v2Notes().map { it.text })
        assertTrue(incomingStaging(recovery).isEmpty())
        assertEquals("keep", marker.readText())
        sweepOrphanIncoming(recovery, null)
        assertEquals("keep", marker.readText())
    }

    @Test
    fun r08_stepIdentityIsUnambiguousAndBindsDisplayedSessionContext() {
        assertNotEquals(
            canonicalParts(listOf("A\nB", "C")),
            canonicalParts(listOf("A", "B\nC")),
        )
        val orient = pack.step("u1-st-orient")!!
        val joined = replaceStep(pack, orient.second.id) { it.copy(bookDirection = "A\nB", companionDirection = "C") }
        val split = replaceStep(pack, orient.second.id) { it.copy(bookDirection = "A", companionDirection = "B\nC") }
        assertNotEquals(
            stepContentDigest(joined, joined.step(orient.second.id)!!.second),
            stepContentDigest(split, split.step(orient.second.id)!!.second),
        )
        val titled = replaceSession(pack, orient.first.id) { it.copy(title = it.title + " extra") }
        assertNotEquals(stepContentDigest(pack, orient.second), stepContentDigest(titled, orient.second))
        val minutes = replaceSession(pack, orient.first.id) { it.copy(estimatedMinutes = it.estimatedMinutes + 17) }
        assertEquals(stepContentDigest(pack, orient.second), stepContentDigest(minutes, orient.second))
        val objectives = replaceSession(pack, orient.first.id) { it.copy(objectiveIds = it.objectiveIds + "extra-objective") }
        assertNotEquals(stepContentDigest(pack, orient.second), stepContentDigest(objectives, orient.second))

        val exampleStep = pack.units.flatMap { it.sessions }.flatMap { it.steps }.first { it.exampleIds.isNotEmpty() }
        val example = pack.example(exampleStep.exampleIds.first())!!
        val exampleLeft = replaceExample(pack, example.id) { it.copy(display = "A\nB", note = "C") }
        val exampleRight = replaceExample(pack, example.id) { it.copy(display = "A", note = "B\nC") }
        assertNotEquals(
            stepContentDigest(exampleLeft, exampleLeft.step(exampleStep.id)!!.second),
            stepContentDigest(exampleRight, exampleRight.step(exampleStep.id)!!.second),
        )
        val referenceStep = pack.units.flatMap { it.sessions }.flatMap { it.steps }.first { it.referenceIds.isNotEmpty() }
        val reference = pack.reference(referenceStep.referenceIds.first())!!
        val referenced = replaceReference(pack, reference.id) { it.copy(detail = it.detail + "\nadded") }
        assertNotEquals(
            stepContentDigest(pack, referenceStep),
            stepContentDigest(referenced, referenced.step(referenceStep.id)!!.second),
        )
        val remediated = replaceStep(pack, exampleStep.id) { it.copy(remediationStepId = "u1-st-orient") }
        assertNotEquals(
            stepContentDigest(pack, exampleStep),
            stepContentDigest(remediated, remediated.step(exampleStep.id)!!.second),
        )

        val store = memory()
        val current = stepContentDigest(pack, orient.second)
        store.v2UpsertStepForTests(orient.second.id, current, 2, "2026-10-07T12:00:00")
        assertFalse(completedStepCounts(pack, store.v2LearnerView(), orient.second.id))
        assertEquals(2, store.v2StepProgress().single().identitySchema)
        store.v2UpsertStepForTests(orient.second.id, current, 1, "2026-10-07T12:01:00")
        assertFalse(completedStepCounts(pack, store.v2LearnerView(), orient.second.id))
        store.v2UpsertStepForTests(orient.second.id, current, STEP_IDENTITY_SCHEMA, "2026-10-07T12:02:00")
        assertTrue(completedStepCounts(pack, store.v2LearnerView(), orient.second.id))
    }

    @Test
    fun r09_acceptedWriteOrderIgnoresTheClockAndSurvivesReopenBackupAndMigration() {
        val located = pack.step("u1-st-vowels-check")!!
        val step = located.second
        val item = pack.item(step.itemIds.single())!!
        val first = item.options[0]
        val second = item.options[1]
        val store = memory()
        fun prescribed(selected: String, at: String) =
            prepareGovernedAttempt(pack, item.id, selected, PRESCRIBED_CHECK, step.id, at)!!
        store.v2RecordGovernedAttempt(prescribed(first, "2026-10-07T12:00:03"))
        store.v2RecordGovernedAttempt(prescribed(second, "2026-10-07T12:00:02"))
        store.v2RecordGovernedAttempt(prescribed(first, "2026-10-07T12:00:01"))
        val backward = store.v2Attempts()
        assertEquals(listOf(first, second, first), backward.map { it.selected })
        assertEquals(listOf(1L, 2L, 3L), backward.map { it.acceptedOrder })
        assertEquals(listOf("2026-10-07T12:00:03", "2026-10-07T12:00:02", "2026-10-07T12:00:01"), backward.map { it.recordedAtLocal })
        val key = governedSelectionKey(step.id, item.id)
        assertEquals(first, governedSessionSelections(pack, store.v2LearnerView())[key])
        store.v2RecordGovernedAttempt(prescribed(first, "2026-10-07T11:00:00"))
        assertEquals(3, store.v2Attempts().size)

        val equalStore = memory()
        equalStore.v2RecordGovernedAttempt(prescribed(first, "2026-10-07T12:00:00"))
        equalStore.v2RecordGovernedAttempt(prescribed(second, "2026-10-07T12:00:00"))
        equalStore.v2RecordGovernedAttempt(prescribed(first, "2026-10-07T12:00:00"))
        equalStore.v2RecordGovernedAttempt(prescribed(first, "2026-10-07T12:00:00"))
        assertEquals(listOf(first, second, first), equalStore.v2Attempts().map { it.selected })
        assertEquals(first, governedSessionSelections(pack, equalStore.v2LearnerView())[key])

        val legacy = prescribed(item.answer, "2026-10-07T18:00:00")
        store.v2InsertUnorderedAttemptForTests(
            V2AttemptEntity(
                id = "legacy-order",
                itemId = legacy.itemId,
                selected = legacy.selected,
                correct = true,
                recordedAtLocal = legacy.recordedAtLocal,
                itemDigest = legacy.itemDigest,
                unitContentDigest = legacy.unitContentDigest,
                ruleDigest = legacy.ruleDigest,
                identitySchema = legacy.identitySchema,
                attemptRole = legacy.attemptRole,
                objectiveId = legacy.objectiveId,
                unitNumber = legacy.unitNumber,
                stepId = legacy.stepId,
                acceptedOrder = 99,
            ),
        )
        val learner = store.v2LearnerView()
        assertTrue(learner.correctItemIds.contains(item.id))
        assertEquals(first, governedSessionSelections(pack, learner)[key])
        assertEquals(0L, store.v2Attempts().first { it.id == "legacy-order" }.acceptedOrder)
        val unknownOnly = memory()
        unknownOnly.v2InsertUnorderedAttemptForTests(
            V2AttemptEntity(
                id = "legacy-only",
                itemId = legacy.itemId,
                selected = legacy.selected,
                correct = true,
                recordedAtLocal = legacy.recordedAtLocal,
                itemDigest = legacy.itemDigest,
                unitContentDigest = legacy.unitContentDigest,
                ruleDigest = legacy.ruleDigest,
                identitySchema = legacy.identitySchema,
                attemptRole = legacy.attemptRole,
                objectiveId = legacy.objectiveId,
                unitNumber = legacy.unitNumber,
                stepId = legacy.stepId,
                acceptedOrder = 99,
            ),
        )
        val unknownLearner = unknownOnly.v2LearnerView()
        assertTrue(unknownLearner.correctItemIds.contains(item.id))
        assertFalse(item.id in creditItemIds(pack, unknownLearner))
        assertEquals(null, governedSessionSelections(pack, unknownLearner)[key])
        assertEquals(0L, unknownOnly.v2Attempts().single().acceptedOrder)

        val backup = tempZip()
        backup.outputStream().use { writeStoreBackup(store, it, APP, pack.packId, pack.packVersion, "DARK") }
        val restored = memory()
        val read = readGovernedBackup(backup.inputStream(), APP, pack.packId, pack.packVersion, tempDir())
        assertTrue(read is BackupReadResult.Valid)
        assertEquals(
            dev.russiancompanion.v2.backup.GovernedReplaceOutcome.REPLACED,
            restoreStoreBackup(restored, (read as BackupReadResult.Valid).backup, tempDir()),
        )
        assertEquals(
            store.v2Attempts().map { it.id to it.acceptedOrder },
            restored.v2Attempts().map { it.id to it.acceptedOrder },
        )
        val old = backupJson.decodeFromString(
            BackupAttempt.serializer(),
            """{"id":"old","itemId":"item","selected":"yes","correct":true,"recordedAtLocal":"t"}""",
        )
        assertEquals(0L, old.acceptedOrder)

        val name = "reaudit-order-${System.nanoTime()}"
        val context = context()
        var named = LocalStore.openNamed(context, name)
        try {
            named.v2RecordGovernedAttempt(prescribed(second, "2026-10-07T09:00:00"))
            named.v2RecordGovernedAttempt(prescribed(first, "2026-10-07T08:00:00"))
            val orders = named.v2Attempts().map { it.selected to it.acceptedOrder }
            named.close()
            named = LocalStore.openNamed(context, name)
            assertEquals(orders, named.v2Attempts().map { it.selected to it.acceptedOrder })
            assertEquals(first, governedSessionSelections(pack, named.v2LearnerView())[key])
        } finally {
            named.close()
            context.deleteDatabase(name)
        }

        migrateLeavesUnknownOrderAtZero()

        place(store, located.first.id, located.first.steps.indexOfFirst { it.id == step.id })
        compose.setContent { V2App(store = store, pack = pack, onRetryOpen = {}) }
        compose.onNodeWithText("Progress").performClick()
        compose.onNodeWithText("This count includes historical rows and is not current prescribed credit.", substring = true)
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun r10_attemptPreparationAndCourseSnapshotLeaveTheMainThread() {
        V2UiWork.blockForTests = false
        val located = pack.step("u1-st-vowels-check")!!
        val step = located.second
        val item = pack.item(step.itemIds.single())!!
        val first = item.options[0]
        val second = item.options[1]
        val store = memory()
        place(store, located.first.id, located.first.steps.indexOfFirst { it.id == step.id })
        val hold = CompletableDeferred<Unit>()
        store.holdGovernedAttemptForTests = hold
        compose.setContent { V2App(store = store, pack = pack, onRetryOpen = {}) }
        awaitTag("v2-continue")
        lastGovernedIdentityThreadForTests = null
        lastGovernedSnapshotLabelForTests = null
        compose.onNodeWithText("Course").performClick()
        awaitTag("v2-course-intro")
        idleUntil("course snapshot") { lastGovernedSnapshotLabelForTests == "course" }
        val courseThread = lastGovernedIdentityThreadForTests
        assertNotEquals(Looper.getMainLooper().thread, courseThread)
        assertFalse(courseThread!!.name.startsWith("russian-companion-store"))

        compose.onNodeWithText("Today").performClick()
        awaitTag("v2-continue")
        compose.onNodeWithTag("v2-continue").performClick()
        awaitTag("v2-option-${item.id}-$first")
        lastGovernedIdentityThreadForTests = null
        compose.onNodeWithTag("v2-option-${item.id}-$first").performScrollTo().performClick()
        idleUntil("attempt preparation") { lastGovernedIdentityThreadForTests != null }
        val attemptThread = lastGovernedIdentityThreadForTests
        assertNotEquals(Looper.getMainLooper().thread, attemptThread)
        assertFalse(attemptThread!!.name.startsWith("russian-companion-store"))
        assertTrue(store.v2Attempts().isEmpty())
        compose.onNodeWithTag("v2-option-${item.id}-$second").performScrollTo().assertIsNotEnabled()
        hold.complete(Unit)
        idleUntil("attempt stored") { store.v2Attempts().size == 1 }
        assertEquals(1L, store.v2Attempts().single().acceptedOrder)
        assertEquals(first, store.v2Attempts().single().selected)
    }

    private fun migrateLeavesUnknownOrderAtZero() {
        val context = context()
        val helper = FrameworkSQLiteOpenHelperFactory().create(
            SupportSQLiteOpenHelper.Configuration.builder(context)
                .name("reaudit-migration-${System.nanoTime()}.db")
                .callback(object : SupportSQLiteOpenHelper.Callback(8) {
                    override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                        db.execSQL(
                            "CREATE TABLE IF NOT EXISTS `v2_attempts` (`id` TEXT NOT NULL, `itemId` TEXT NOT NULL, " +
                                "`selected` TEXT NOT NULL, `correct` INTEGER NOT NULL, `recordedAtLocal` TEXT NOT NULL, " +
                                "`itemDigest` TEXT NOT NULL DEFAULT 'UNRECOVERABLE', `unitContentDigest` TEXT NOT NULL DEFAULT 'UNRECOVERABLE', " +
                                "`ruleDigest` TEXT NOT NULL DEFAULT 'UNRECOVERABLE', `identitySchema` INTEGER NOT NULL DEFAULT 0, " +
                                "`attemptRole` TEXT NOT NULL DEFAULT 'LEGACY_UNVERIFIED', `objectiveId` TEXT NOT NULL DEFAULT '', " +
                                "`unitNumber` INTEGER NOT NULL DEFAULT 0, `stepId` TEXT NOT NULL DEFAULT '', PRIMARY KEY(`id`))",
                        )
                    }

                    override fun onUpgrade(
                        db: androidx.sqlite.db.SupportSQLiteDatabase,
                        oldVersion: Int,
                        newVersion: Int,
                    ) = Unit
                })
                .build(),
        )
        val db = helper.writableDatabase
        db.execSQL("INSERT INTO v2_attempts (id, itemId, selected, correct, recordedAtLocal) VALUES ('late', 'item', 'A', 1, '2026-10-07T12:00:02')")
        db.execSQL("INSERT INTO v2_attempts (id, itemId, selected, correct, recordedAtLocal) VALUES ('early', 'item', 'B', 1, '2026-10-07T12:00:01')")
        MIGRATION_8_9.migrate(db)
        db.query("SELECT id, acceptedOrder FROM v2_attempts ORDER BY recordedAtLocal ASC").use { cursor ->
            assertTrue(cursor.moveToFirst())
            assertEquals("early", cursor.getString(0))
            assertEquals(0L, cursor.getLong(1))
            assertTrue(cursor.moveToNext())
            assertEquals("late", cursor.getString(0))
            assertEquals(0L, cursor.getLong(1))
            assertFalse(cursor.moveToNext())
        }
        helper.close()
    }

    private fun admissibleUnit(source: V2CurriculumPack, unitNumber: Int): V2CurriculumPack {
        val mappingId = "map-reaudit-unit-$unitNumber"
        val mapped = source.copy(
            units = source.units.map { unit ->
                if (unit.number != unitNumber) {
                    unit
                } else {
                    unit.copy(installState = "INSTALLED", mappingStatus = "APPROVED", readiness = "INSTALLABLE")
                }
            },
            ownedEditionMappings = source.ownedEditionMappings + V2OwnedEditionMapping(
                id = mappingId,
                unitNumber = unitNumber,
                status = "APPROVED",
                reviewer = "Qualified Reviewer",
                reviewerQualification = "Advanced Russian and curriculum review attested.",
                editionTitle = "Fixture edition",
                author = "Fixture author",
                publisherImprint = "Fixture imprint",
                proposedLessonNumber = unitNumber,
                printedPageStart = 1,
                printedPageEnd = 12,
                topicDescription = "Fixture topic",
                observationSource = "Fixture observation",
                isbn = "FIXTURE-ISBN-$unitNumber",
                isbnObservation = "OBSERVED",
            ),
        )
        val digest = V2Validator.unitContentReviewDigest(mapped, unitNumber)
        val reviewed = mapped.copy(
            ownedEditionMappings = mapped.ownedEditionMappings.map { mapping ->
                if (mapping.id == mappingId) mapping.copy(reviewedContentDigest = digest) else mapping
            },
            unitReviews = mapped.unitReviews.filter { it.unitNumber != unitNumber } + V2UnitReviewRecord(
                id = "rev-reaudit-unit-$unitNumber",
                unitNumber = unitNumber,
                status = "APPROVED",
                reviewer = "Qualified Reviewer",
                reviewerQualification = "Advanced Russian and curriculum review attested.",
                packVersion = mapped.packVersion,
                reviewedContentDigest = digest,
                curriculumMappingId = mappingId,
            ),
        )
        val unit = checkNotNull(reviewed.unit(unitNumber))
        val steps = unit.sessions.flatMap { it.steps }
        val itemIds = (steps.flatMap { it.itemIds } + unit.objectives.flatMap { it.checkItemIds }).toSet()
        val exampleIds = (steps.flatMap { it.exampleIds } + unit.objectives.flatMap { it.exampleIds }).toSet()
        val referenceIds = steps.flatMap { it.referenceIds }.toSet()
        val letterIds = exampleIds.mapNotNull { reviewed.example(it)?.letterId?.takeIf(String::isNotBlank) }.toSet()
        return reviewed.copy(
            items = reviewed.items.map { if (it.id in itemIds) it.copy(reviewStatus = "APPROVED") else it },
            examples = reviewed.examples.map { if (it.id in exampleIds) it.copy(reviewStatus = "APPROVED") else it },
            letters = reviewed.letters.map { if (it.id in letterIds) it.copy(reviewStatus = "APPROVED") else it },
            references = reviewed.references.map { if (it.id in referenceIds) it.copy(reviewStatus = "APPROVED") else it },
        )
    }

    private fun restoreBackup(store: LocalStore, backup: File) {
        val coordinator = V2BackupCoordinator.get(
            compose.activity,
            store,
            APP,
            pack.packId,
            pack.packVersion,
            AppearanceStore.memory(),
        )
        assertTrue(coordinator.begin(V2BackupPhase.PREPARING))
        coordinator.openBackup { backup.inputStream() }
        awaitPhase(coordinator, V2BackupPhase.AWAITING_CONFIRMATION)
        coordinator.confirmRestore { writeCoordinator().reloadAfterRestore() }
        awaitPhase(coordinator, V2BackupPhase.COMPLETED)
        assertEquals(RESTORE_DONE, coordinator.message)
    }

    private fun backupFingerprint(backup: File): String {
        val read = readGovernedBackup(backup.inputStream(), APP, pack.packId, pack.packVersion, tempDir())
        val validated = read as BackupReadResult.Valid
        return backupJson.encodeToString(
            dev.russiancompanion.v2.backup.GovernedLearnerDocument.serializer(),
            validated.backup.document,
        )
    }

    private fun newCoordinator(store: LocalStore, appearance: AppearanceStore): V2BackupCoordinator {
        val activity = Robolectric.buildActivity(ComponentActivity::class.java).setup().get()
        return V2BackupCoordinator.get(activity, store, APP, pack.packId, pack.packVersion, appearance)
    }

    private fun writeCoordinator(): V2WriteCoordinator {
        return ViewModelProvider(compose.activity)[V2WriteCoordinator::class.java]
    }

    private fun place(store: LocalStore, sessionId: String, index: Int) {
        val digest = dev.russiancompanion.v2.curriculum.resumePositionDigest(pack, sessionId, index).orEmpty()
        store.v2SaveProfile(
            store.v2Profile().copy(
                onboardingCompleted = true,
                currentUnitNumber = 1,
                currentSessionId = sessionId,
                currentStepIndex = index,
                resumeContentDigest = digest,
                resumeIdentitySchema = if (digest.isEmpty()) 0 else dev.russiancompanion.v2.curriculum.GOVERNED_IDENTITY_SCHEMA,
            ),
        )
    }

    private fun replaceStep(source: V2CurriculumPack, stepId: String, transform: (V2Step) -> V2Step): V2CurriculumPack {
        return source.copy(
            units = source.units.map { unit ->
                unit.copy(
                    sessions = unit.sessions.map { session ->
                        session.copy(steps = session.steps.map { if (it.id == stepId) transform(it) else it })
                    },
                )
            },
        )
    }

    private fun replaceSession(source: V2CurriculumPack, sessionId: String, transform: (V2Session) -> V2Session): V2CurriculumPack {
        return source.copy(
            units = source.units.map { unit ->
                unit.copy(sessions = unit.sessions.map { if (it.id == sessionId) transform(it) else it })
            },
        )
    }

    private fun replaceExample(
        source: V2CurriculumPack,
        exampleId: String,
        transform: (dev.russiancompanion.v2.curriculum.V2Example) -> dev.russiancompanion.v2.curriculum.V2Example,
    ): V2CurriculumPack {
        return source.copy(examples = source.examples.map { if (it.id == exampleId) transform(it) else it })
    }

    private fun replaceReference(
        source: V2CurriculumPack,
        referenceId: String,
        transform: (dev.russiancompanion.v2.curriculum.V2ReferenceEntry) -> dev.russiancompanion.v2.curriculum.V2ReferenceEntry,
    ): V2CurriculumPack {
        return source.copy(references = source.references.map { if (it.id == referenceId) transform(it) else it })
    }

    private fun awaitLatch(latch: CountDownLatch, timeoutMs: Long = 8_000) {
        val started = System.currentTimeMillis()
        while (latch.count > 0 && System.currentTimeMillis() - started < timeoutMs) {
            Shadows.shadowOf(Looper.getMainLooper()).idle()
            Thread.sleep(15)
        }
        assertEquals(0, latch.count)
    }

    private fun idleUntil(message: String, timeoutMs: Long = 8_000, condition: () -> Boolean) {
        val started = System.currentTimeMillis()
        while (!condition() && System.currentTimeMillis() - started < timeoutMs) {
            Shadows.shadowOf(Looper.getMainLooper()).idle()
            Thread.sleep(15)
        }
        assertTrue(message, condition())
    }

    private fun awaitPhase(coordinator: V2BackupCoordinator, phase: V2BackupPhase) {
        idleUntil("phase $phase, was ${coordinator.phase}: ${coordinator.message}") { coordinator.phase == phase }
        assertEquals(phase, coordinator.phase)
    }

    private fun awaitTag(tag: String) {
        idleUntil("tag $tag") {
            compose.onAllNodes(androidx.compose.ui.test.hasTestTag(tag), useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun incomingStaging(recovery: File): List<File> {
        return recovery.listFiles().orEmpty().filter { it.isDirectory && it.name.startsWith("incoming-") }
    }

    private fun memory(): LocalStore = LocalStore.openInMemory(context()).also { stores += it }

    private fun context(): Context = ApplicationProvider.getApplicationContext()

    private fun tempZip(): File = File.createTempFile("reaudit-backup", ".zip").apply { deleteOnExit() }

    private fun tempDir(): File = File.createTempFile("reaudit-stage", "").apply {
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
