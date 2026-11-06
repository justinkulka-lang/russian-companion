package dev.russiancompanion.v2

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.data.local.CompletionState
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.data.local.audio
import dev.russiancompanion.data.local.minimalDocument
import dev.russiancompanion.data.local.rights
import dev.russiancompanion.data.local.seed.SeedJson
import dev.russiancompanion.data.local.text
import dev.russiancompanion.v2.backup.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.file.Files

/** Real named-store restore boundaries; synthetic audio is not admitted curriculum media. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class BackupRecordingFilenameBoundaryTest {
    @Test fun ordinaryRecordingFilenameRestoresItsBytesAndSurvivesNamedStoreReopen() =
        exercise("recordings/restored.m4a", GovernedReplaceOutcome.REPLACED)

    @Test fun incomingSuffixRejectsBeforeReplacingRowsOrRecordingBytes() =
        exercise("recordings/restored.m4a.incoming", GovernedReplaceOutcome.UNCHANGED)

    @Test fun retiredSuffixRejectsBeforeReplacingRowsOrRecordingBytes() =
        exercise("recordings/restored.m4a.retired", GovernedReplaceOutcome.UNCHANGED)

    private fun exercise(reference: String, expected: GovernedReplaceOutcome) {
        val context: Context = ApplicationProvider.getApplicationContext()
        val name = "recording-name-boundary-${System.nanoTime()}"
        var store = LocalStore.openNamed(context, name)
        val root = checkNotNull(store.recordingDirectory())
        val work = Files.createTempDirectory("recording-name-boundary-").toFile()
        try {
            V2BackupFaults.reset()
            store.applySeed(SeedJson.encode(minimalDocument().copy(
                sourceTexts = listOf(text(matchVerified = true)),
                assetRightsRecords = listOf(rights()),
                audioModels = listOf(audio()),
            )))
            store.v2AddNote("step", "keep original learner", "test-time")
            store.commitRecording("audio-1", "recordings/original.m4a", "test-time", false,
                CompletionState.NOT_COMPLETED)
            root.mkdirs()
            val original = File(root, "original.m4a").apply { writeBytes(byteArrayOf(8, 7, 6, 5)) }
            val before = store.governedLearnerFingerprint()
            val incoming = BackupRecording("incoming-id", "audio-1", reference, "test-time", false,
                "NOT_COMPLETED", null)
            val document = store.governedLearnerDocument().copy(notes = emptyList(), recordings = listOf(incoming))
            val bytes = byteArrayOf(1, 2, 3, 4)
            val source = File(work, "source.m4a").apply { writeBytes(bytes) }
            val archive = ByteArrayOutputStream().also {
                writeGovernedBackup(it, "test.app", "test-pack", 1, "DARK", document, listOf(incoming to source))
            }.toByteArray()
            val read = archive.inputStream().use {
                readGovernedBackup(it, "test.app", "test-pack", 1, File(work, "staging"))
            }
            assertTrue("Structurally valid, correctly hashed fixture: $read", read is BackupReadResult.Valid)
            val outcome = restoreStoreBackup(store, (read as BackupReadResult.Valid).backup, File(work, "recovery"))
            assertEquals("$reference: incomingFileExists=${File(root, File(reference).name).isFile}, " +
                "originalFileExists=${original.isFile}, noteCount=${store.v2Notes().size}", expected, outcome)
            fun assertState() {
                if (expected == GovernedReplaceOutcome.REPLACED) {
                    assertEquals(listOf("incoming-id"), store.recordings().map { it.id })
                    assertTrue(store.v2Notes().isEmpty())
                    assertArrayEquals(bytes, checkNotNull(store.recordingFile(reference)).readBytes())
                    assertFalse(original.exists())
                } else {
                    assertEquals(before, store.governedLearnerFingerprint())
                    assertArrayEquals(byteArrayOf(8, 7, 6, 5), checkNotNull(store.recordingFile("recordings/original.m4a")).readBytes())
                    assertFalse(File(root, File(reference).name).exists())
                }
                assertFalse(File(context.filesDir, "backup-replace-${root.name}.json").exists())
                assertFalse(File(context.filesDir, "backup-replace-${root.name}-workspace").exists())
            }
            assertState()
            store.close()
            store = LocalStore.openNamed(context, name)
            assertState()
        } finally {
            V2BackupFaults.reset()
            store.close()
            context.deleteDatabase(name)
            root.deleteRecursively()
            File(context.filesDir, "backup-replace-${root.name}.json").delete()
            File(context.filesDir, "backup-replace-${root.name}-workspace").deleteRecursively()
            work.deleteRecursively()
        }
    }
}
