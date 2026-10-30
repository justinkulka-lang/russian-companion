package dev.russiancompanion.v2

import dev.russiancompanion.v2.backup.*
import org.junit.Assert.*
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.nio.file.Files
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

class BackupImportLimitsTest {
    @Test fun exactLimitsAcceptCompatibleArchiveAndRecordingBytes() = fixture { stage, work ->
        val archive = archive(work, recordings = 2)
        val entries = entries(archive)
        val limits = BackupImportLimits(inputBytes = archive.size.toLong(), entryCount = entries.size,
            manifestBytes = entries.getValue(BACKUP_MANIFEST_ENTRY).size.toLong(),
            learnerBytes = entries.getValue(BACKUP_LEARNER_ENTRY).size.toLong(),
            recordingBytes = 32768, expandedBytes = entries.values.sumOf { it.size.toLong() })
        val read = read(archive, stage, limits)
        assertTrue(read.toString(), read is BackupReadResult.Valid)
        val validated = (read as BackupReadResult.Valid).backup
        assertEquals(2, validated.recordings.size)
        validated.recordings.forEach { assertArrayEquals(recordingData, it.file.readBytes()) }
    }

    @Test fun unknownLengthInputStopsAtOneExcessByteAndCleansOnlyOwnedFiles() = fixture { stage, work ->
        val archive = archive(work)
        var consumed = 0
        val input = object : InputStream() {
            override fun read(): Int = if (consumed < archive.size) archive[consumed++].toInt() and 255 else -1
            override fun read(b: ByteArray, off: Int, len: Int): Int {
                if (consumed == archive.size) return -1
                val count = minOf(len, archive.size - consumed)
                archive.copyInto(b, off, consumed, consumed + count); consumed += count
                return count
            }
        }
        val result = readGovernedBackup(input, APP, PACK, 1, stage, BackupImportLimits(inputBytes = 31))
        assertEquals(BackupReadResult.Rejected(BackupRejection.RESOURCE_LIMIT), result)
        assertEquals(32, consumed)
        assertOnlySentinel(stage)
    }

    @Test fun entryCountLimitRejectsBeforeBuildingAnUnboundedNameCollection() = fixture { stage, work ->
        assertResource(read(archive(work), stage, BackupImportLimits(entryCount = 1)), stage)
    }

    @Test fun manifestExpansionIsBoundedEvenWhenCentralDirectorySizeLies() = fixture { stage, work ->
        val archive = withZeroDeclaredSize(archive(work), BACKUP_MANIFEST_ENTRY)
        assertResource(read(archive, stage, BackupImportLimits(manifestBytes = 32)), stage)
    }

    @Test fun learnerExpansionIsBoundedEvenWhenBothDeclaredSizesLie() = fixture { stage, work ->
        val parts = entries(archive(work)).toMutableMap()
        val manifest = backupJson.decodeFromString(BackupManifest.serializer(), parts.getValue(BACKUP_MANIFEST_ENTRY).decodeToString())
        parts[BACKUP_MANIFEST_ENTRY] = backupJson.encodeToString(BackupManifest.serializer(),
            manifest.copy(files = manifest.files.map { if (it.path == BACKUP_LEARNER_ENTRY) it.copy(bytes = 0) else it })).toByteArray()
        val archive = withZeroDeclaredSize(zip(parts), BACKUP_LEARNER_ENTRY)
        assertResource(read(archive, stage, BackupImportLimits(learnerBytes = 32)), stage)
    }

    @Test fun recordingExpansionIsBoundedWhenCompressedSizeMetadataLies() = fixture { stage, work ->
        val archive = withZeroDeclaredSize(archive(work, recordings = 1), "recordings/sample-0.m4a")
        assertResource(read(archive, stage, BackupImportLimits(recordingBytes = 32)), stage)
    }

    @Test fun aggregateExpansionRejectsTheSecondRecordingAndRemovesEarlierStaging() = fixture { stage, work ->
        val archive = archive(work, recordings = 2)
        val total = entries(archive).values.sumOf { it.size.toLong() }
        assertResource(read(archive, stage, BackupImportLimits(expandedBytes = total - 1)), stage)
    }

    @Test fun cancellationAfterInputReadRejectsBeforeStagingData() = fixture { stage, work ->
        val archive = archive(work)
        var cancel = false
        val source = object : java.io.FilterInputStream(archive.inputStream()) {
            override fun read(b: ByteArray, off: Int, len: Int): Int = super.read(b, off, len).also { cancel = true }
        }
        assertEquals(BackupReadResult.Rejected(BackupRejection.CANCELLED),
            readGovernedBackup(source, APP, PACK, 1, stage, cancelled = { cancel }))
        assertOnlySentinel(stage)
    }

    @Test fun cancellationDuringRecordingExpansionCleansPartialRecording() = fixture { stage, work ->
        val archive = archive(work, recordings = 1)
        assertEquals(BackupReadResult.Rejected(BackupRejection.CANCELLED),
            readGovernedBackup(archive.inputStream(), APP, PACK, 1, stage,
                cancelled = { File(stage, "sample-0.bin").length() > 0 }))
        assertOnlySentinel(stage)
    }

    @Test fun existingStagingInputsAreNeverOverwrittenOrDeleted() = fixture { stage, work ->
        val original = "unowned".toByteArray(); File(stage, "incoming.zip").writeBytes(original)
        assertEquals(BackupReadResult.Rejected(BackupRejection.UNAUTHORIZED), read(archive(work), stage))
        assertArrayEquals(original, File(stage, "incoming.zip").readBytes())
    }

    @Test fun directoryAllocationLimitRejectsBeforeOpeningTheZip() = fixture { stage, work ->
        assertResource(read(archive(work), stage, BackupImportLimits(directoryBytes = 1)), stage)
    }

    @Test fun forgedDirectoryCountIsRejectedBeforeZipMetadataAllocation() = fixture { stage, work ->
        val bytes = archive(work).copyOf()
        val end = bytes.size - 22
        for (offset in listOf(8, 10)) { bytes[end + offset] = 1; bytes[end + offset + 1] = 8 }
        assertResource(read(bytes, stage), stage) // Declares 2049 entries in a tiny fixture.
    }

    @Test fun smallZip64ArchiveRemainsCompatibleWithDefaultPolicy() = fixture { stage, work ->
        assertTrue(read(zip64(archive(work)), stage) is BackupReadResult.Valid)
    }

    @Test fun writerRefusesLearnerAndRecordingSizesOutsideTheImportPolicy() = fixture { _, work ->
        for (limits in listOf(BackupImportLimits(learnerBytes = 32), BackupImportLimits(recordingBytes = 32),
            BackupImportLimits(expandedBytes = 32), BackupImportLimits(entryCount = 1), BackupImportLimits(directoryBytes = 1))) {
            val output = ByteArrayOutputStream()
            try { archive(work, recordings = 1, limits = limits, output = output); fail("Oversized export reported success") }
            catch (_: BackupWriteException) { assertEquals(0, output.size()) }
        }
    }

    @Test fun writerStopsBeforeItsCompressedOutputExceedsTheImportInputCap() = fixture { _, work ->
        val output = ByteArrayOutputStream()
        try { archive(work, limits = BackupImportLimits(inputBytes = 31), output = output); fail("Oversized output accepted") }
        catch (_: BackupWriteException) { assertTrue(output.size() <= 31) }
    }

    @Test fun writerAndReaderAcceptTheSameExactBoundaries() = fixture { stage, work ->
        val control = archive(work, recordings = 2)
        val parts = entries(control)
        val limits = BackupImportLimits(inputBytes = control.size.toLong(), entryCount = parts.size,
            directoryBytes = parts.keys.sumOf { 46L + it.toByteArray().size },
            manifestBytes = parts.getValue(BACKUP_MANIFEST_ENTRY).size.toLong(),
            learnerBytes = parts.getValue(BACKUP_LEARNER_ENTRY).size.toLong(), recordingBytes = recordingData.size.toLong(),
            expandedBytes = parts.values.sumOf { it.size.toLong() })
        val written = archive(work, recordings = 2, limits = limits)
        assertEquals(control.size, written.size)
        assertTrue(read(written, stage, limits) is BackupReadResult.Valid)
    }

    @Test fun conflictingDeclaredPathsRemainRejectedAndStagingIsCleaned() = fixture { stage, work ->
        val parts = entries(archive(work)).toMutableMap()
        val manifest = backupJson.decodeFromString(BackupManifest.serializer(), parts.getValue(BACKUP_MANIFEST_ENTRY).decodeToString())
        parts[BACKUP_MANIFEST_ENTRY] = backupJson.encodeToString(BackupManifest.serializer(),
            manifest.copy(files = manifest.files + manifest.files.first())).toByteArray()
        assertEquals(BackupReadResult.Rejected(BackupRejection.UNAUTHORIZED), read(zip(parts), stage))
        assertOnlySentinel(stage)
    }

    @Test fun zip64CannotHideAnOversizedPrimaryDirectoryDeclaration() = fixture { stage, work ->
        val bytes = zip64(archive(work))
        val end = bytes.size - 22
        bytes[end + 12] = 0; bytes[end + 13] = 0; bytes[end + 14] = 32; bytes[end + 15] = 0
        assertResource(read(bytes, stage), stage)
    }

    @Test fun conflictingZip64AndPrimaryCountsAreRejectedRatherThanChoosingAHeader() = fixture { stage, work ->
        val bytes = zip64(archive(work))
        val end = bytes.size - 22
        bytes[end + 10] = 0; bytes[end + 11] = 0
        assertEquals(BackupReadResult.Rejected(BackupRejection.TRUNCATED), read(bytes, stage))
        assertOnlySentinel(stage)
    }

    private fun archive(work: File, recordings: Int = 0, limits: BackupImportLimits = BackupImportLimits(),
        output: ByteArrayOutputStream = ByteArrayOutputStream()): ByteArray {
        val base = blankLearner()
        val models = (0 until recordings).map { BackupRecording("sample-$it", "test-model", "recordings/sample-$it.m4a", "test-time", false, "NOT_COMPLETED", null) }
        val files = models.map { recording -> recording to File(work, recording.id + ".m4a").apply { writeBytes(recordingData) } }
        writeGovernedBackup(output, APP, PACK, 1, "DARK", base.copy(recordings = models), files, limits = limits)
        return output.toByteArray()
    }
    private fun read(bytes: ByteArray, stage: File, limits: BackupImportLimits = BackupImportLimits()) =
        readGovernedBackup(bytes.inputStream(), APP, PACK, 1, stage, limits)
    private fun assertResource(result: BackupReadResult, stage: File) {
        assertEquals(BackupReadResult.Rejected(BackupRejection.RESOURCE_LIMIT), result); assertOnlySentinel(stage)
    }
    private fun assertOnlySentinel(stage: File) {
        assertEquals(setOf("sentinel"), stage.listFiles()!!.map { it.name }.toSet())
        assertEquals("keep", File(stage, "sentinel").readText())
    }
    private fun fixture(block: (File, File) -> Unit) {
        val work = Files.createTempDirectory("backup-import-limits-").toFile()
        val stage = File(work, "stage").apply { mkdirs(); File(this, "sentinel").writeText("keep") }
        try { block(stage, work) } finally { work.deleteRecursively() }
    }
    private fun entries(bytes: ByteArray): Map<String, ByteArray> = linkedMapOf<String, ByteArray>().apply {
        ZipInputStream(bytes.inputStream()).use { z -> while (true) {
            val entry = z.nextEntry ?: break; put(entry.name, z.readBytes()); z.closeEntry()
        } }
    }
    private fun zip(parts: Map<String, ByteArray>) = ByteArrayOutputStream().also { output ->
        ZipOutputStream(output).use { z -> parts.forEach { (name, bytes) ->
            z.putNextEntry(ZipEntry(name)); z.write(bytes); z.closeEntry()
        } }
    }.toByteArray()
    private fun zip64(bytes: ByteArray): ByteArray {
        val end = bytes.size - 22
        fun unsigned(at: Int, count: Int): Long = (0 until count).fold(0L) { n, i ->
            n or ((bytes[end + at + i].toLong() and 255) shl (8 * i))
        }
        val output = ByteArrayOutputStream()
        fun put(n: Long, count: Int) { for (i in 0 until count) output.write((n ushr (8 * i)).toInt() and 255) }
        output.write(bytes, 0, end)
        put(0x06064b50, 4); put(44, 8); put(45, 2); put(45, 2); put(0, 4); put(0, 4)
        put(unsigned(8, 2), 8); put(unsigned(10, 2), 8); put(unsigned(12, 4), 8); put(unsigned(16, 4), 8)
        put(0x07064b50, 4); put(0, 4); put(end.toLong(), 8); put(1, 4)
        val legacyEnd = bytes.copyOfRange(end, bytes.size)
        for (i in 8..19) legacyEnd[i] = 255.toByte()
        output.write(legacyEnd)
        return output.toByteArray()
    }
    private fun withZeroDeclaredSize(bytes: ByteArray, name: String): ByteArray = bytes.copyOf().also { copy ->
        var found = false
        for (i in 0..copy.size - 46) {
            if (copy[i] == 0x50.toByte() && copy[i + 1] == 0x4b.toByte() && copy[i + 2] == 1.toByte() && copy[i + 3] == 2.toByte()) {
                val length = (copy[i + 28].toInt() and 255) or ((copy[i + 29].toInt() and 255) shl 8)
                if (i + 46 + length <= copy.size && copy.copyOfRange(i + 46, i + 46 + length).decodeToString() == name) {
                    for (n in 24..27) copy[i + n] = 0; found = true; break
                }
            }
        }
        assertTrue("Missing central directory entry: $name", found)
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
    private val recordingData = ByteArray(32768) { 0 }
    companion object { private const val APP = "test.app"; private const val PACK = "test-pack" }
}
