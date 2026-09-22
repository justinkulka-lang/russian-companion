package dev.russiancompanion.data.export

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.data.local.CompletionState
import dev.russiancompanion.data.local.ExportStatus
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.data.local.audio
import dev.russiancompanion.data.local.minimalDocument
import dev.russiancompanion.data.local.rights
import dev.russiancompanion.data.local.seed.SeedJson
import dev.russiancompanion.data.local.text
import dev.russiancompanion.data.navigation.RoomNavigationGateway
import dev.russiancompanion.domain.practice.PracticeAudio
import dev.russiancompanion.domain.progress.ExportUpdate
import org.junit.After
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.zip.ZipFile

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class ProgressExportTest {
    private val stores = mutableListOf<LocalStore>()

    @After
    fun closeStores() {
        stores.forEach { it.close() }
    }

    @Test
    fun exportCopiesEveryReadableRecordingAndFailsWhenOneIsMissing() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val store = openStore(context)
        store.commitRecording("audio-1", "recordings/one.m4a", "2026-09-21T09:00", true, CompletionState.NOT_COMPLETED)
        assertTrue(store.completeListenPractice("audio-1", "2026-09-21", "2026-09-21T09:00"))
        store.commitRecording("audio-1", "recordings/two.m4a", "2026-09-21T09:01", false, CompletionState.NOT_COMPLETED)
        val recordings = store.recordings()
        val bytes = recordings.associate { it.localFileReference to "bytes-${it.id}".encodeToByteArray() }
        val gateway = RoomNavigationGateway(store, { null }, UnusedPracticeAudio(), openRecording = { reference ->
            bytes[reference]?.inputStream()
        })
        val out = ByteArrayOutputStream()
        val update = gateway.exportLocal(
            out,
            "2026-09-21T10:00",
            "export.zip",
            "content://documents/export.zip",
        )
        assertTrue(update is ExportUpdate.Completed)
        assertEquals(false, (update as ExportUpdate.Completed).empty)
        val saved = store.exports().single()
        assertEquals(ExportStatus.COMPLETED, saved.status)
        assertEquals(2, saved.recordingCount)
        assertEquals("export.zip", saved.destinationReference)
        assertEquals("content://documents/export.zip", saved.documentReference)
        ZipFile(tempZip(out.toByteArray())).use { zip ->
            recordings.forEach { recording ->
                val name = "recordings/${recording.id}.m4a"
                assertArrayEquals(bytes.getValue(recording.localFileReference), zip.getInputStream(zip.getEntry(name)).use { it.readBytes() })
            }
            val index = zip.getInputStream(zip.getEntry("recordings.csv")).bufferedReader().use { it.readText() }
            assertTrue(index.contains("true,COMPLETED,null"))
            assertTrue(index.contains("false,NOT_COMPLETED,null"))
            assertTrue(!index.contains("invented-log"))
        }

        val broken = ByteArrayOutputStream()
        val failed = RoomNavigationGateway(store, { null }, UnusedPracticeAudio(), openRecording = { reference ->
            if (reference == recordings.first().localFileReference) null else bytes[reference]?.inputStream()
        }).exportLocal(broken, "2026-09-21T10:01", "export.zip", "content://documents/export.zip")
        assertEquals(ExportUpdate.RecordingUnreadable(recordings.first().id), failed)
        assertEquals(0, broken.size())
        val failure = store.exports().last()
        assertEquals(ExportStatus.FAILED, failure.status)
        assertEquals(0, failure.recordingCount)
        assertEquals(0, failure.logCount)
    }

    @Test
    fun emptyExportIsIdentifiedAndReadableRecordingsStayInsideTheAppDirectory() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val store = openStore(context)
        val out = ByteArrayOutputStream()
        val update = RoomNavigationGateway(store, { null }, UnusedPracticeAudio()).exportLocal(
            out,
            "2026-09-21T10:00",
            "empty-export.zip",
            null,
        )
        assertEquals(ExportUpdate.Completed(empty = true), update)
        assertEquals("empty-export.zip", store.exports().single().destinationReference)
        assertNull(store.exports().single().documentReference)
        ZipFile(tempZip(out.toByteArray())).use { zip ->
            val manifest = zip.getInputStream(zip.getEntry("manifest.json")).bufferedReader().use { it.readText() }
            assertTrue(manifest.contains("\"empty\": true"))
            assertTrue(zip.getInputStream(zip.getEntry("progress.csv")).bufferedReader().use { it.readText() }.contains("READING,0,0"))
        }

        val dir = File(context.filesDir, "recordings").apply { mkdirs() }
        File(dir, "clip.m4a").writeBytes(byteArrayOf(9, 8, 7))
        File(dir, "empty.m4a").writeBytes(byteArrayOf())
        assertArrayEquals(byteArrayOf(9, 8, 7), readAppRecording(context, "recordings/clip.m4a"))
        assertNull(readAppRecording(context, "recordings/empty.m4a"))
        assertNull(readAppRecording(context, "recordings/missing.m4a"))
        assertNull(readAppRecording(context, "../clip.m4a"))
        assertNull(readAppRecording(context, "/etc/passwd"))
    }

    private fun openStore(context: Context): LocalStore {
        val store = LocalStore.openInMemory(context).also { stores += it }
        store.applySeed(
            SeedJson.encode(
                minimalDocument().copy(
                    sourceTexts = listOf(text(matchVerified = true)),
                    assetRightsRecords = listOf(rights()),
                    audioModels = listOf(audio()),
                ),
            ),
        )
        return store
    }

    private fun tempZip(bytes: ByteArray): File {
        val file = File.createTempFile("gateway-export", ".zip")
        file.writeBytes(bytes)
        file.deleteOnExit()
        return file
    }

    private class UnusedPracticeAudio : PracticeAudio {
        override fun modelPlayable(localAudioReference: String): Boolean = false

        override fun recordingReadable(localFileReference: String): Boolean = false

        override fun playModel(localAudioReference: String, onFinished: (Boolean) -> Unit) = Unit

        override fun playRecording(localFileReference: String, onFinished: (Boolean) -> Unit) = Unit

        override fun stopPlayback() = Unit

        override fun startCapture(): String = ""

        override fun stopCapture(keep: Boolean): String? = null
    }
}
