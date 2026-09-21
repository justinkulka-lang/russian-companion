package dev.russiancompanion.data.export

import dev.russiancompanion.domain.progress.BOOK_ATTESTATION
import kotlinx.serialization.json.Json
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.util.zip.ZipFile

class ExportPackageTest {
    @Test
    fun packageContainsManifestCsvAndRecordingBytes() {
        val audioA = byteArrayOf(1, 2, 3)
        val audioB = byteArrayOf(4, 5)
        val built = buildExportZip(
            logs = listOf(
                ExportLogRow(
                    id = "log-1",
                    category = "READING",
                    durationMinutes = 15,
                    completed = true,
                    recordedAtLocal = "2026-09-21T09:00",
                    segmentId = "",
                    planInstanceId = "",
                    selfAttested = false,
                    attestationLabel = "",
                ),
            ),
            evidence = listOf(
                ExportEvidenceRow(
                    id = "evidence-1",
                    gateDefinitionId = "gate-1",
                    evidenceType = "BOOK_REVISION_SCORE",
                    enteredValue = "1",
                    selfAttestationStatus = "SELF_ATTESTED",
                    attestationLabel = BOOK_ATTESTATION,
                    recordedAtLocal = "2026-09-21T09:01",
                ),
            ),
            decisions = listOf(
                ExportDecisionRow(
                    id = "decision-1",
                    gateDefinitionId = "gate-1",
                    ruleVersion = "1",
                    evidenceIds = "evidence-1",
                    conditionOutcomes = "left,right",
                    status = "SATISFIED",
                    evaluatedAtLocal = "2026-09-21T09:02",
                ),
            ),
            progress = listOf(ExportProgressRow("READING", 15, 1)),
            recordings = listOf(
                ExportAudio(
                    id = "rec-1",
                    audioModelId = "audio-1",
                    createdAtLocal = "2026-09-21T09:03",
                    markedForTutorReview = true,
                    completionState = "COMPLETED",
                    fileName = "recordings/rec-1.m4a",
                    bytes = audioA,
                ),
                ExportAudio(
                    id = "rec-2",
                    audioModelId = "audio-2",
                    createdAtLocal = "2026-09-21T09:04",
                    markedForTutorReview = false,
                    completionState = "NOT_COMPLETED",
                    fileName = "recordings/rec-2.m4a",
                    bytes = audioB,
                ),
            ),
        )
        val ready = built as BuiltPackage.Ready
        assertFalse(ready.empty)
        assertEquals(1, ready.exportVersionOrNull())
        val copy = File.createTempFile("export-copy", ".zip")
        copy.writeBytes(ready.bytes)
        ZipFile(copy).use { zip ->
            val manifest = zip.readText("manifest.json")
            val decoded = Json.decodeFromString<ExportManifest>(manifest)
            assertEquals(EXPORT_VERSION, decoded.exportVersion)
            assertFalse(decoded.empty)
            assertTrue(decoded.files.contains("recordings/rec-1.m4a"))
            val logs = zip.readText("activity-logs.csv")
            assertTrue(logs.startsWith("id,category,durationMinutes,completed,recordedAtLocal,segmentId,planInstanceId,selfAttested,attestationLabel\n"))
            assertTrue(logs.contains("READING"))
            assertTrue(logs.contains("15"))
            val evidence = zip.readText("gate-evidence.csv")
            assertTrue(evidence.contains(BOOK_ATTESTATION))
            val decisions = zip.readText("gate-decisions.csv")
            assertTrue(decisions.contains("\"left,right\""))
            val recordings = zip.readText("recordings.csv")
            assertTrue(
                recordings.startsWith(
                    "fileName,recordingId,audioModelId,createdAtLocal,markedForTutorReview,completionState\n",
                ),
            )
            assertFalse(recordings.contains("activityLog"))
            assertTrue(recordings.contains("recordings/rec-1.m4a,rec-1,audio-1,2026-09-21T09:03,true,COMPLETED"))
            assertArrayEquals(audioA, zip.readBytes("recordings/rec-1.m4a"))
            assertArrayEquals(audioB, zip.readBytes("recordings/rec-2.m4a"))
        }
        copy.delete()
    }

    @Test
    fun unreadableRecordingProducesNoPackage() {
        val missing = buildExportZip(
            logs = emptyList(),
            evidence = emptyList(),
            decisions = emptyList(),
            progress = emptyList(),
            recordings = listOf(audio("rec-1", null)),
        )
        assertEquals(BuiltPackage.RecordingUnreadable("rec-1"), missing)
        val emptyBytes = buildExportZip(
            logs = emptyList(),
            evidence = emptyList(),
            decisions = emptyList(),
            progress = emptyList(),
            recordings = listOf(audio("rec-2", byteArrayOf())),
        )
        assertEquals(BuiltPackage.RecordingUnreadable("rec-2"), emptyBytes)
    }

    @Test
    fun emptyExportKeepsHeaderRowsAndZeroProgress() {
        val ready = buildExportZip(
            logs = emptyList(),
            evidence = emptyList(),
            decisions = emptyList(),
            progress = listOf(ExportProgressRow("READING", 0, 0)),
            recordings = emptyList(),
        ) as BuiltPackage.Ready
        assertTrue(ready.empty)
        ZipFile(bytesFile(ready.bytes)).use { zip ->
            val manifest = Json.decodeFromString<ExportManifest>(zip.readText("manifest.json"))
            assertTrue(manifest.empty)
            assertEquals(1, manifest.progressCount)
            assertTrue(zip.readText("activity-logs.csv").lines().filter { it.isNotBlank() }.size == 1)
            assertTrue(zip.readText("gate-evidence.csv").lines().filter { it.isNotBlank() }.size == 1)
            assertTrue(zip.readText("progress.csv").contains("READING,0,0"))
        }
    }

    @Test
    fun recordingNamesStayUnderTheRecordingsDirectory() {
        assertEquals("recordings/rec-1.m4a", exportAudioName("rec-1", "recordings/rec-1.M4A"))
        assertEquals("recordings/rec-1.mp3", exportAudioName("rec-1", "clip.mp3"))
        assertEquals("recordings/rec-1.m4a", exportAudioName("rec-1", "no-extension"))
        assertEquals("recordings/rec-1.m4a", exportAudioName("rec-1", "../escape.exe"))
    }

    private fun audio(id: String, bytes: ByteArray?): ExportAudio {
        return ExportAudio(
            id = id,
            audioModelId = "audio-1",
            createdAtLocal = "2026-09-21T09:00",
            markedForTutorReview = false,
            completionState = "NOT_COMPLETED",
            fileName = "recordings/$id.m4a",
            bytes = bytes,
        )
    }

    private fun bytesFile(bytes: ByteArray): File {
        val file = File.createTempFile("export", ".zip")
        file.writeBytes(bytes)
        file.deleteOnExit()
        return file
    }

    private fun BuiltPackage.Ready.exportVersionOrNull(): Int {
        return ZipFile(bytesFile(bytes)).use { zip ->
            Json.decodeFromString<ExportManifest>(zip.readText("manifest.json")).exportVersion
        }
    }

    private fun ZipFile.readText(name: String): String {
        return getInputStream(getEntry(name)).bufferedReader(Charsets.UTF_8).use { it.readText() }
    }

    private fun ZipFile.readBytes(name: String): ByteArray {
        return getInputStream(getEntry(name)).use { it.readBytes() }
    }
}
