package dev.russiancompanion.v2.ui

import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.russiancompanion.data.appearance.AppearanceStore
import dev.russiancompanion.data.local.ExportRecordEntity
import dev.russiancompanion.data.local.ExportStatus
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.domain.appearance.AppearancePreference
import dev.russiancompanion.domain.appearance.appearancePreferenceFromStored
import dev.russiancompanion.domain.progress.ExportCounts
import dev.russiancompanion.v2.backup.BACKUP_CANCELLED
import dev.russiancompanion.v2.backup.BACKUP_FAILED
import dev.russiancompanion.v2.backup.BACKUP_SAVED
import dev.russiancompanion.v2.backup.GovernedReplaceOutcome
import dev.russiancompanion.v2.backup.BackupWorkRegistry
import dev.russiancompanion.v2.backup.RESTORE_CANCELLED
import dev.russiancompanion.v2.backup.RESTORE_CLEANUP_PENDING
import dev.russiancompanion.v2.backup.RESTORE_CLEANUP_PENDING_APPEARANCE_FAILED
import dev.russiancompanion.v2.backup.RESTORE_DONE
import dev.russiancompanion.v2.backup.RESTORE_FAILED
import dev.russiancompanion.v2.backup.RESTORE_RECOVERY_REQUIRED
import dev.russiancompanion.v2.backup.RestorePreview
import dev.russiancompanion.v2.backup.ValidatedBackup
import dev.russiancompanion.v2.backup.readGovernedBackup
import dev.russiancompanion.v2.backup.restorePreview
import dev.russiancompanion.v2.backup.restoreStoreBackup
import dev.russiancompanion.v2.backup.writeStoreBackup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.isActive
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

internal enum class V2BackupPhase {
    IDLE,
    PREPARING,
    WRITING,
    VALIDATING,
    AWAITING_CONFIRMATION,
    RESTORING,
    COMPLETED,
    CANCELLED,
    FAILED,
}

/**
 * One backup or restore at a time. The activity retains this model across
 * recreation so a confirmation or finished outcome is not started again.
 */
internal class V2BackupCoordinator(
    private val store: LocalStore,
    private val applicationId: String,
    private val packId: String,
    private val packVersion: Int,
    private val appearanceStore: AppearanceStore,
    private val recoveryDir: File,
) : ViewModel() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val running = AtomicBoolean(false)

    @Volatile
    private var cancelRequested = false

    @Volatile
    private var appearanceName = AppearancePreference.DARK.name

    private var validated: ValidatedBackup? = null
    private var disposableStaging: File? = null
    private val stagingLock = Any()
    private val stagingReaders = AtomicInteger(0)
    private val cleared = AtomicBoolean(false)
    private val stagingIo = Executors.newSingleThreadExecutor { runnable ->
        Thread(runnable, "russian-companion-staging").apply { isDaemon = true }
    }

    var phase by mutableStateOf(V2BackupPhase.IDLE)
        private set
    var message by mutableStateOf("")
        private set
    var preview by mutableStateOf<RestorePreview?>(null)
        private set

    init {
        stagingIo.execute { sweepOrphanIncoming(recoveryDir, null) }
        scope.launch {
            appearanceStore.preference.collect { appearanceName = it.name }
        }
    }

    fun begin(phase: V2BackupPhase): Boolean {
        if (!running.compareAndSet(false, true)) return false
        cancelRequested = false
        this.phase = phase
        message = ""
        return true
    }

    fun createBackup(openOutput: () -> OutputStream?, recordedAt: String, onFinished: () -> Unit = {}) {
        if (phase != V2BackupPhase.PREPARING || !running.get()) return
        phase = V2BackupPhase.WRITING
        scope.launch {
            val outcome = withContext(Dispatchers.IO) {
                runCatching {
                    if (cancelRequested) return@runCatching false
                    val stream = openOutput() ?: return@runCatching false
                    stream.use { output ->
                        if (cancelRequested) return@runCatching false
                        writeStoreBackup(
                            store = store,
                            output = output,
                            applicationId = applicationId,
                            packId = packId,
                            packVersion = packVersion,
                            appearance = appearanceName,
                        )
                    }
                    if (cancelRequested) return@runCatching false
                    val document = store.governedLearnerDocument()
                    val counts = ExportCounts(
                        logCount = document.activityLogs.size,
                        evidenceCount = document.evidence.size + document.attempts.size,
                        progressCount = document.steps.size + document.unitCompletions.size,
                        recordingCount = document.recordings.size,
                    )
                    store.saveExport(
                        ExportRecordEntity(
                            id = "backup-${System.nanoTime()}",
                            exportedAtLocal = recordedAt,
                            destinationReference = "local-backup",
                            logCount = counts.logCount,
                            evidenceCount = counts.evidenceCount,
                            progressCount = counts.progressCount,
                            recordingCount = counts.recordingCount,
                            status = ExportStatus.COMPLETED,
                            documentReference = "backup-format-1",
                        ),
                        packageCounts = counts,
                    )
                    true
                }
            }
            val succeeded = outcome.getOrNull() == true && !cancelRequested
            finish(succeeded, BACKUP_FAILED, BACKUP_CANCELLED, BACKUP_SAVED)
            onFinished()
        }
    }

    fun openBackup(openInput: () -> InputStream?) {
        if (phase != V2BackupPhase.PREPARING || !running.get()) return
        phase = V2BackupPhase.VALIDATING
        scope.launch {
            var keepStaging = false
            try {
                val opened = withContext(Dispatchers.IO) {
                    if (cancelRequested) return@withContext null
                    val stream = openInput() ?: return@withContext null
                    stream.use { input ->
                        if (cancelRequested) return@use null
                        val staging = File(recoveryDir, "incoming-${System.nanoTime()}")
                        acquireStaging(staging)
                        usingStaging {
                            readGovernedBackup(input, applicationId, packId, packVersion, staging)
                        }
                    }
                }
                if (!coroutineContext.isActive || cancelRequested || opened == null) {
                    finish(false, RESTORE_FAILED, RESTORE_CANCELLED, RESTORE_DONE)
                    return@launch
                }
                when (opened) {
                    is dev.russiancompanion.v2.backup.BackupReadResult.Rejected -> {
                        validated = null
                        preview = null
                        message = RESTORE_FAILED
                        phase = V2BackupPhase.FAILED
                        running.set(false)
                    }
                    is dev.russiancompanion.v2.backup.BackupReadResult.Valid -> {
                        if (!coroutineContext.isActive || cancelRequested) {
                            finish(false, RESTORE_FAILED, RESTORE_CANCELLED, RESTORE_DONE)
                            return@launch
                        }
                        val fault = dev.russiancompanion.v2.backup.V2BackupFaults.throwOnCurrentDocument
                        if (fault != null) {
                            dev.russiancompanion.v2.backup.V2BackupFaults.throwOnCurrentDocument = null
                            throw fault
                        }
                        val current = withContext(Dispatchers.IO) { store.governedLearnerDocument() }
                        if (!coroutineContext.isActive || cancelRequested) {
                            finish(false, RESTORE_FAILED, RESTORE_CANCELLED, RESTORE_DONE)
                            return@launch
                        }
                        if (cleared.get()) {
                            finish(false, RESTORE_FAILED, RESTORE_CANCELLED, RESTORE_DONE)
                            return@launch
                        }
                        validated = opened.backup
                        preview = restorePreview(current, opened.backup.document)
                        message = dev.russiancompanion.v2.backup.REPLACE_EXPLANATION
                        phase = V2BackupPhase.AWAITING_CONFIRMATION
                        keepStaging = disposableStaging != null
                    }
                }
            } catch (cancelled: CancellationException) {
                message = RESTORE_CANCELLED
                phase = V2BackupPhase.CANCELLED
                running.set(false)
                throw cancelled
            } catch (_: Exception) {
                validated = null
                preview = null
                message = RESTORE_FAILED
                phase = V2BackupPhase.FAILED
                running.set(false)
            } finally {
                if (!keepStaging) releaseStaging()
            }
        }
    }

    fun confirmRestore(onRestored: () -> Unit) {
        val accepted = validated ?: return
        if (phase != V2BackupPhase.AWAITING_CONFIRMATION) return
        phase = V2BackupPhase.RESTORING
        val savedAppearance = appearanceName
        val leased = synchronized(stagingLock) {
            if (disposableStaging == null) {
                false
            } else {
                stagingReaders.incrementAndGet()
                true
            }
        }
        val job = scope.launch {
            val parent = coroutineContext.job
            var openedBoundary = false
            var outcome: GovernedReplaceOutcome? = null
            var failed: Exception? = null
            try {
                if (!parent.isActive || cancelRequested) {
                    outcome = null
                } else {
                    try {
                        dev.russiancompanion.v2.backup.V2BackupFaults.beforeMutationHold?.await()
                    } catch (cancelled: CancellationException) {
                        throw cancelled
                    }
                    if (!parent.isActive || cancelRequested) {
                        outcome = null
                    } else {
                        outcome = withContext(NonCancellable) {
                            dev.russiancompanion.v2.backup.V2BackupFaults.boundaryQueuedForTests?.countDown()
                            withContext(store.ioDispatcher) {
                                store.openLearnerReplacement()
                                openedBoundary = true
                                dev.russiancompanion.v2.backup.V2BackupFaults.boundaryOpenedForTests?.countDown()
                            }
                            dev.russiancompanion.v2.backup.V2BackupFaults.afterBoundaryBeforeIo?.await()
                            withContext(Dispatchers.IO) {
                                usingStaging {
                                    restoreStoreBackup(store, accepted, File(recoveryDir, "rollback"))
                                }
                            }
                        }
                        dev.russiancompanion.v2.backup.V2BackupFaults.holdAfterCommit?.await()
                    }
                }
            } catch (cancelled: CancellationException) {
                failed = cancelled
            } catch (error: Exception) {
                failed = error
            }
            withContext(NonCancellable) {
                validated = null
                preview = null
                if (openedBoundary) {
                    withContext(store.ioDispatcher) { store.closeLearnerReplacement() }
                }
                val replaced = outcome == GovernedReplaceOutcome.REPLACED ||
                    outcome == GovernedReplaceOutcome.CLEANUP_PENDING
                when {
                    replaced -> {
                        val appearanceSaved = persistAppearance(appearancePreferenceFromStored(accepted.manifest.appearance))
                        message = when {
                            outcome == GovernedReplaceOutcome.CLEANUP_PENDING && appearanceSaved -> RESTORE_CLEANUP_PENDING
                            outcome == GovernedReplaceOutcome.CLEANUP_PENDING -> RESTORE_CLEANUP_PENDING_APPEARANCE_FAILED
                            appearanceSaved -> RESTORE_DONE
                            else -> dev.russiancompanion.v2.backup.RESTORE_LEARNER_REPLACED_APPEARANCE_FAILED
                        }
                        phase = V2BackupPhase.COMPLETED
                        runCatching { onRestored() }
                    }
                    outcome == GovernedReplaceOutcome.RECOVERY_REQUIRED -> {
                        persistAppearance(appearancePreferenceFromStored(savedAppearance))
                        message = RESTORE_RECOVERY_REQUIRED
                        phase = V2BackupPhase.FAILED
                        runCatching { onRestored() }
                    }
                    failed is CancellationException || !parent.isActive || cancelRequested -> {
                        persistAppearance(appearancePreferenceFromStored(savedAppearance))
                        message = RESTORE_CANCELLED
                        phase = V2BackupPhase.CANCELLED
                        if (openedBoundary) runCatching { onRestored() }
                    }
                    else -> {
                        persistAppearance(appearancePreferenceFromStored(savedAppearance))
                        message = RESTORE_FAILED
                        phase = V2BackupPhase.FAILED
                        if (openedBoundary) runCatching { onRestored() }
                    }
                }
                running.set(false)
            }
            if (failed is CancellationException || !parent.isActive) {
                throw CancellationException("restore-cancelled")
            }
        }
        job.invokeOnCompletion { releaseConfirmLease(leased) }
    }

    internal fun cancelWorkForTests() {
        scope.cancel()
    }

    fun cancel() {
        cancelRequested = true
        if (phase == V2BackupPhase.AWAITING_CONFIRMATION || phase == V2BackupPhase.PREPARING) {
            validated = null
            preview = null
            releaseStaging()
            message = if (phase == V2BackupPhase.PREPARING) BACKUP_CANCELLED else RESTORE_CANCELLED
            phase = V2BackupPhase.CANCELLED
            running.set(false)
        }
    }

    private suspend fun persistAppearance(preference: dev.russiancompanion.domain.appearance.AppearancePreference): Boolean {
        return try {
            appearanceStore.set(preference).isSuccess
        } catch (_: CancellationException) {
            false
        } catch (_: Exception) {
            false
        }
    }

    private fun acquireStaging(file: File) {
        try {
            BackupWorkRegistry.createOwnedIncoming(file)
        } catch (error: Throwable) {
            if (!BackupWorkRegistry.incomingHeld(file) && file.isDirectory) {
                deleteOffMain(file)
            }
            throw error
        }
        synchronized(stagingLock) {
            disposableStaging = file
        }
    }

    private fun <T> usingStaging(block: () -> T): T {
        synchronized(stagingLock) { stagingReaders.incrementAndGet() }
        try {
            return block()
        } finally {
            val releaseAfter = synchronized(stagingLock) {
                stagingReaders.decrementAndGet()
                cleared.get() && stagingReaders.get() == 0
            }
            if (releaseAfter) releaseStaging()
        }
    }

    private fun releaseStaging() {
        val file = synchronized(stagingLock) {
            val current = disposableStaging ?: return
            disposableStaging = null
            BackupWorkRegistry.releaseIncoming(current)
            current
        }
        deleteOffMain(file)
    }

    private fun releaseConfirmLease(leased: Boolean) {
        val file = synchronized(stagingLock) {
            if (leased) stagingReaders.updateAndGet { (it - 1).coerceAtLeast(0) }
            if (stagingReaders.get() > 0) return@synchronized null
            val current = disposableStaging ?: return@synchronized null
            disposableStaging = null
            BackupWorkRegistry.releaseIncoming(current)
            current
        }
        if (file != null) deleteOffMain(file)
    }

    private fun deleteOffMain(file: File) {
        stagingIo.execute {
            BackupWorkRegistry.lastDeleteThreadForTests = Thread.currentThread()
            file.deleteRecursively()
        }
    }

    override fun onCleared() {
        cancelRequested = true
        scope.cancel()
        val file = synchronized(stagingLock) {
            cleared.set(true)
            if (stagingReaders.get() > 0) return@synchronized null
            val current = disposableStaging ?: return@synchronized null
            disposableStaging = null
            BackupWorkRegistry.releaseIncoming(current)
            current
        }
        if (file != null) deleteOffMain(file)
    }

    internal fun sweepOrphansForTests() {
        sweepOrphanIncoming(recoveryDir, disposableStaging)
    }

    private fun finish(succeeded: Boolean, failed: String, cancelled: String, saved: String) {
        when {
            cancelRequested -> {
                message = cancelled
                phase = V2BackupPhase.CANCELLED
            }
            succeeded -> {
                message = saved
                phase = V2BackupPhase.COMPLETED
            }
            else -> {
                message = failed
                phase = V2BackupPhase.FAILED
            }
        }
        running.set(false)
    }

    companion object {
        fun get(
            activity: ComponentActivity,
            store: LocalStore,
            applicationId: String,
            packId: String,
            packVersion: Int,
            appearanceStore: AppearanceStore,
        ): V2BackupCoordinator {
            val factory = object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return V2BackupCoordinator(
                        store = store,
                        applicationId = applicationId,
                        packId = packId,
                        packVersion = packVersion,
                        appearanceStore = appearanceStore,
                        recoveryDir = File(activity.applicationContext.filesDir, "backup-recovery"),
                    ) as T
                }
            }
            val key = "v2-backup-${System.identityHashCode(store)}"
            return ViewModelProvider(activity, factory)[key, V2BackupCoordinator::class.java]
        }
    }
}

internal fun sweepOrphanIncoming(recoveryDir: File, active: File?) {
    BackupWorkRegistry.sweepUnownedIncoming(recoveryDir, active)
}
