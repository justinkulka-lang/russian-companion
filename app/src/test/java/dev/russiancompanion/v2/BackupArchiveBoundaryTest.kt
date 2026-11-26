package dev.russiancompanion.v2

import dev.russiancompanion.v2.backup.*
import org.junit.Assert.*
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.file.Files
import java.util.zip.CRC32
import java.util.zip.ZipInputStream

/** Archive-format boundaries only; the retained learner is not current-curriculum acceptance. */
class BackupArchiveBoundaryTest {
    @Test fun canonicalRecordingArchiveStagesExactBytesWithoutTouchingUnownedFiles() = fixture { stage, work ->
        assertValid(read(archive(work), stage), stage, work)
    }

    @Test fun storedZipFixtureIsAcceptedBeforeItsDuplicateEntryMutations() = fixture { stage, work ->
        assertValid(read(storedZip(entries(archive(work))), stage), stage, work)
    }

    @Test fun traversalAndNonCanonicalEntryNamesRejectWithoutWritingOutsideStaging() = fixture { stage, work ->
        val parts = entries(archive(work))
        val forbidden = listOf(
            "../outside.bin", File(work, "outside.bin").absolutePath,
            "recordings/../outside.bin", "recordings/nested/name.m4a",
            "recordings\\outside.m4a", "recordings/.hidden.m4a",
            "recordings/record..m4a", "recordings/record.m4a/extra",
        )
        for (name in forbidden) {
            assertEquals(name, BackupReadResult.Rejected(BackupRejection.UNAUTHORIZED),
                read(storedZip(parts + (name to byteArrayOf(9))), stage))
            assertRejectedCleanup(stage, work)
        }
    }

    @Test fun duplicateZipNamesRejectEvenWhenEachEntryHasAValidCrc() = fixture { stage, work ->
        val parts = entries(archive(work))
        for ((name, _) in parts) {
            val ambiguous = storedZip(parts + (name to "different duplicate payload".toByteArray()))
            assertEquals(name, BackupReadResult.Rejected(BackupRejection.UNAUTHORIZED), read(ambiguous, stage))
            assertRejectedCleanup(stage, work)
        }
    }

    @Test fun missingManifestLearnerOrDeclaredRecordingRejectsWithoutLeavingStaging() = fixture { stage, work ->
        val parts = entries(archive(work))
        for ((name, _) in parts) {
            assertEquals(name, BackupReadResult.Rejected(BackupRejection.INCOMPLETE),
                read(storedZip(parts.filterNot { it.first == name }), stage))
            assertRejectedCleanup(stage, work)
        }
    }

    @Test fun correctlyHashedLearnerCannotRedirectADeclaredRecordingPath() = fixture { stage, work ->
        val parts = entries(archive(work))
        val document = backupJson.decodeFromString(GovernedLearnerDocument.serializer(),
            parts.single { it.first == BACKUP_LEARNER_ENTRY }.second.decodeToString())
        val changed = backupJson.encodeToString(GovernedLearnerDocument.serializer(), document.copy(
            recordings = document.recordings.map { it.copy(id = "../outside") },
        )).toByteArray()
        val manifest = backupJson.decodeFromString(BackupManifest.serializer(),
            parts.single { it.first == BACKUP_MANIFEST_ENTRY }.second.decodeToString())
        val changedManifest = backupJson.encodeToString(BackupManifest.serializer(), manifest.copy(
            files = manifest.files.map {
                if (it.path == BACKUP_LEARNER_ENTRY) it.copy(bytes = changed.size.toLong(), sha256 = sha256(changed)) else it
            },
        )).toByteArray()
        val forged = parts.map { (name, bytes) -> name to when (name) {
            BACKUP_LEARNER_ENTRY -> changed
            BACKUP_MANIFEST_ENTRY -> changedManifest
            else -> bytes
        } }
        assertEquals(BackupReadResult.Rejected(BackupRejection.INCOMPLETE), read(storedZip(forged), stage))
        assertRejectedCleanup(stage, work)
    }

    @Test fun preexistingRecordingStagingFileIsNeitherOverwrittenNorDeleted() = fixture { stage, work ->
        val occupied = File(stage, "$RECORDING_ID.bin").apply { writeText("unowned recording") }
        assertEquals(BackupReadResult.Rejected(BackupRejection.UNAUTHORIZED), read(archive(work), stage))
        assertEquals("unowned recording", occupied.readText())
        assertEquals(setOf("sentinel", occupied.name), stage.listFiles()!!.map { it.name }.toSet())
        assertUnownedSentinels(stage, work)
    }

    @Test fun recordingHashFailureRemovesOnlyFilesCreatedByTheRejectedImport() = fixture { stage, work ->
        val parts = entries(archive(work)).map { (name, bytes) ->
            name to if (name.startsWith("recordings/")) bytes.reversedArray() else bytes
        }
        assertEquals(BackupReadResult.Rejected(BackupRejection.INTEGRITY), read(storedZip(parts), stage))
        assertRejectedCleanup(stage, work)
    }

    private fun assertValid(result: BackupReadResult, stage: File, work: File) {
        assertTrue(result.toString(), result is BackupReadResult.Valid)
        val backup = (result as BackupReadResult.Valid).backup
        assertEquals(1, backup.recordings.size)
        val recording = backup.recordings.single()
        assertEquals(RECORDING_ID, recording.recording.id)
        assertEquals(File(stage, "$RECORDING_ID.bin").canonicalFile, recording.file.canonicalFile)
        assertEquals(stage.canonicalFile, recording.file.canonicalFile.parentFile)
        assertArrayEquals(RECORDING_BYTES, recording.file.readBytes())
        assertEquals(setOf("sentinel", "incoming.zip", "$RECORDING_ID.bin"),
            stage.listFiles()!!.map { it.name }.toSet())
        assertUnownedSentinels(stage, work)
    }

    private fun assertRejectedCleanup(stage: File, work: File) {
        assertEquals(setOf("sentinel"), stage.listFiles()!!.map { it.name }.toSet())
        assertUnownedSentinels(stage, work)
    }

    private fun assertUnownedSentinels(stage: File, work: File) {
        assertEquals("keep staging", File(stage, "sentinel").readText())
        assertEquals("keep outside", File(work, "outside.bin").readText())
        assertEquals(setOf("stage", "outside.bin", "source.m4a"), work.listFiles()!!.map { it.name }.toSet())
    }

    private fun archive(work: File): ByteArray {
        val historical = blankLearner()
        val recording = BackupRecording(RECORDING_ID, "parser-fixture-model", "recordings/$RECORDING_ID.m4a",
            "test-time", false, "NOT_COMPLETED", null)
        val file = File(work, "source.m4a").apply { writeBytes(RECORDING_BYTES) }
        return ByteArrayOutputStream().also { output ->
            writeGovernedBackup(output, APP, PACK, 1, "DARK", historical.copy(recordings = listOf(recording)),
                listOf(recording to file))
        }.toByteArray()
    }

    private fun read(bytes: ByteArray, stage: File) = bytes.inputStream().use {
        readGovernedBackup(it, APP, PACK, 1, stage)
    }

    private fun entries(bytes: ByteArray): List<Pair<String, ByteArray>> = buildList {
        ZipInputStream(bytes.inputStream()).use { zip ->
            while (true) {
                val entry = zip.nextEntry ?: break
                add(entry.name to zip.readBytes())
                zip.closeEntry()
            }
        }
    }

    /** STORED entries with independent CRCs; unlike ZipOutputStream this permits duplicate names. */
    private fun storedZip(parts: List<Pair<String, ByteArray>>): ByteArray {
        val output = ByteArrayOutputStream()
        val directory = ByteArrayOutputStream()
        fun ByteArrayOutputStream.number(value: Long, count: Int) {
            repeat(count) { write((value ushr (it * 8)).toInt() and 255) }
        }
        for ((name, bytes) in parts) {
            val encoded = name.toByteArray(Charsets.UTF_8)
            val crc = CRC32().apply { update(bytes) }.value
            val offset = output.size().toLong()
            output.number(0x04034b50, 4)
            listOf(20, 0, 0, 0, 0).forEach { output.number(it.toLong(), 2) }
            output.number(crc, 4)
            repeat(2) { output.number(bytes.size.toLong(), 4) }
            output.number(encoded.size.toLong(), 2); output.number(0, 2)
            output.write(encoded); output.write(bytes)
            directory.number(0x02014b50, 4)
            listOf(20, 20, 0, 0, 0, 0).forEach { directory.number(it.toLong(), 2) }
            directory.number(crc, 4)
            repeat(2) { directory.number(bytes.size.toLong(), 4) }
            directory.number(encoded.size.toLong(), 2)
            repeat(4) { directory.number(0, 2) }
            directory.number(0, 4); directory.number(offset, 4); directory.write(encoded)
        }
        val directoryOffset = output.size().toLong()
        output.write(directory.toByteArray())
        output.number(0x06054b50, 4); repeat(2) { output.number(0, 2) }
        repeat(2) { output.number(parts.size.toLong(), 2) }
        output.number(directory.size().toLong(), 4); output.number(directoryOffset, 4); output.number(0, 2)
        return output.toByteArray()
    }

    private fun fixture(block: (File, File) -> Unit) {
        val work = Files.createTempDirectory("backup-archive-boundary-").toFile()
        val stage = File(work, "stage").apply { mkdirs(); File(this, "sentinel").writeText("keep staging") }
        File(work, "outside.bin").writeText("keep outside")
        try { block(stage, work) } finally { work.deleteRecursively() }
    }

    private fun blankLearner() = GovernedLearnerDocument(
        schemaVersion = BACKUP_SCHEMA_VERSION,
        profile = BackupProfile(
            id = "local", onboardingCompleted = false, currentUnitNumber = 1, startingPoint = "NEW_TO_CYRILLIC",
            hasPenguin = false, hasAnki = false, hasBinder = false, hasTutor = false, dailyMinutes = 20,
            transliterationEnabled = false, currentSessionId = null, currentStepIndex = 0,
        ),
        steps = emptyList(), attempts = emptyList(), bookmarks = emptyList(), notes = emptyList(),
        tutorQueue = emptyList(), bookAttestations = emptyList(), unitCompletions = emptyList(),
        learnerState = null, planInstances = emptyList(), activityLogs = emptyList(), evidence = emptyList(),
        tutorRequirements = emptyList(), decisions = emptyList(), recordings = emptyList(),
    )
    private fun root() = File(if (File("app").isDirectory) "." else "..")

    companion object {
        private const val APP = "test.app"
        private const val PACK = "test-pack"
        private const val RECORDING_ID = "boundary_1-A"
        private val RECORDING_BYTES = byteArrayOf(0, 42, 7, 127)
    }
}
