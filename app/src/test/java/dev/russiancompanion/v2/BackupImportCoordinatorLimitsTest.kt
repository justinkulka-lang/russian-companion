package dev.russiancompanion.v2

import android.content.Context
import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.data.appearance.AppearanceStore
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.domain.appearance.AppearancePreference
import dev.russiancompanion.v2.backup.*
import dev.russiancompanion.v2.curriculum.V2PackLoader
import dev.russiancompanion.v2.ui.V2BackupCoordinator
import dev.russiancompanion.v2.ui.V2BackupPhase
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class BackupImportCoordinatorLimitsTest {
    @Test fun oversizedImportPreservesLearnerCleansStagingAndAllowsTheNextValidPreview() {
        val large = retainedArchive.readBytes()
        val limit = large.size.toLong() - 1
        exercise(BackupImportLimits(inputBytes = limit)) { store, coordinator, work ->
            val before = store.governedLearnerFingerprint()
            assertTrue(coordinator.begin(V2BackupPhase.PREPARING))
            coordinator.openBackup { large.inputStream() }
            await { coordinator.phase == V2BackupPhase.FAILED }
            assertEquals(RESTORE_TOO_LARGE, coordinator.message)
            assertNull(coordinator.preview)
            assertEquals(before, store.governedLearnerFingerprint())
            await { incoming(work).isEmpty() }
            val small = ByteArrayOutputStream().also { writeStoreBackup(store, it, "dev.russiancompanion", pack.packId, pack.packVersion, "DARK") }.toByteArray()
            assertTrue("Positive control must be below the same injected limit", small.size < limit)
            assertTrue(coordinator.begin(V2BackupPhase.PREPARING))
            coordinator.openBackup { small.inputStream() }
            await { coordinator.phase == V2BackupPhase.AWAITING_CONFIRMATION || coordinator.phase == V2BackupPhase.FAILED }
            assertEquals(coordinator.message, V2BackupPhase.AWAITING_CONFIRMATION, coordinator.phase)
            assertEquals(0, coordinator.preview!!.additions)
            assertEquals(0, coordinator.preview!!.omissions)
            assertEquals(0, coordinator.preview!!.conflicts)
            coordinator.cancel()
            await { incoming(work).isEmpty() }
            assertEquals(before, store.governedLearnerFingerprint())
        }
    }

    @Test fun cancellationDuringProviderReadStopsImportWithoutReplacingTheLearner() {
        exercise(BackupImportLimits()) { store, coordinator, work ->
            val entered = CountDownLatch(1); val release = CountDownLatch(1)
            val input = object : java.io.FilterInputStream(retainedArchive.inputStream()) {
                override fun read(b: ByteArray, off: Int, len: Int): Int {
                    entered.countDown()
                    check(release.await(10, TimeUnit.SECONDS))
                    return super.read(b, off, len)
                }
            }
            val before = store.governedLearnerFingerprint()
            try {
                assertTrue(coordinator.begin(V2BackupPhase.PREPARING))
                coordinator.openBackup { input }
                await { entered.count == 0L }
                coordinator.cancel()
                release.countDown()
                await { coordinator.phase == V2BackupPhase.CANCELLED }
                await { incoming(work).isEmpty() }
                assertEquals(RESTORE_CANCELLED, coordinator.message)
                assertNull(coordinator.preview)
                assertEquals(before, store.governedLearnerFingerprint())
            } finally { release.countDown(); input.close() }
        }
    }

    private fun exercise(limits: BackupImportLimits, action: (LocalStore, V2BackupCoordinator, File) -> Unit) {
        val context: Context = ApplicationProvider.getApplicationContext()
        val work = File(context.cacheDir, "coordinator-limit-${System.nanoTime()}").apply {
            mkdirs(); File(this, "sentinel").writeText("keep")
        }
        try {
            LocalStore.openInMemory(context).use { store ->
                store.v2AddNote("u1-st-orient", "existing learner note", "test-time")
                val coordinator = V2BackupCoordinator(store, "dev.russiancompanion", pack.packId, pack.packVersion,
                    AppearanceStore.memory(AppearancePreference.DARK), work, limits)
                try { action(store, coordinator, work); assertEquals("keep", File(work, "sentinel").readText()) }
                finally { coordinator.teardownForTests() }
            }
        } finally { work.deleteRecursively() }
    }
    private fun incoming(work: File) = work.listFiles().orEmpty().filter { it.name.startsWith("incoming-") }
    private fun await(condition: () -> Boolean) {
        val end = System.nanoTime() + TimeUnit.SECONDS.toNanos(20)
        while (System.nanoTime() < end) {
            Shadows.shadowOf(Looper.getMainLooper()).idle()
            if (condition()) return
            Thread.sleep(10)
        }
        fail("Timed out waiting for import boundary")
    }
    private val pack by lazy { V2PackLoader.parse(File(root(), "app/src/main/assets/content/v2-curriculum.json").readText()) }
    private val retainedArchive get() = File(root(), "audit/independent-gpt61-2026-10-12/ia003-unit3-gender-correction/PARTIAL_REVIEW.zip")
    private fun root() = File(if (File("app").isDirectory) "." else "..")
}
