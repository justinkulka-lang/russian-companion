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

    @Test fun defaultLimitsAcceptAllSixtyFourRetainedArchivesWithoutRewritingThem() = fixture { _, work ->
        val allowlist = File(root(), "audit/independent-gpt61-2026-10-12/current-comprehensive-audit/RETAINED_ARCHIVE_ALLOWLIST.json")
        val json = kotlinx.serialization.json.Json.parseToJsonElement(allowlist.readText()) as kotlinx.serialization.json.JsonObject
        val paths = (json.getValue("sha256") as kotlinx.serialization.json.JsonObject).keys.sorted()
        var accepted = 0
        for ((i, path) in paths.withIndex()) {
            val file = File(root(), path)
            val manifest = ZipFile(file).use { z -> z.getEntry(BACKUP_MANIFEST_ENTRY)?.let { entry ->
                backupJson.decodeFromString(BackupManifest.serializer(), z.getInputStream(entry).use { it.readBytes() }.decodeToString())
            } } ?: continue // The other two archives are historical test-result evidence.
            val originalHash = sha256(file)
            val result = file.inputStream().use { readGovernedBackup(it, manifest.applicationId, manifest.packId,
                manifest.packVersion, File(work, "retained-$i")) }
            assertTrue(path + ": " + result, result is BackupReadResult.Valid)
            assertEquals(originalHash, sha256(file))
            accepted++
        }
        assertEquals(64, accepted)
    }

    private fun archive(work: File, recordings: Int = 0): ByteArray {
        val base = backupJson.decodeFromString(GovernedLearnerDocument.serializer(), File(root(),
            "audit/independent-gpt61-2026-10-12/ia003-unit3-gender-correction/UNSTARTED_REVIEW.json").readText())
        val models = (0 until recordings).map { BackupRecording("sample-$it", "test-model", "recordings/sample-$it.m4a", "test-time", false, "NOT_COMPLETED", null) }
        val files = models.map { recording -> recording to File(work, recording.id + ".m4a").apply { writeBytes(recordingData) } }
        return ByteArrayOutputStream().also { writeGovernedBackup(it, APP, PACK, 1, "DARK", base.copy(recordings = models), files) }.toByteArray()
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
    private fun root() = File(if (File("app").isDirectory) "." else "..")
    private val recordingData = ByteArray(32768) { 0 }
    companion object { private const val APP = "test.app"; private const val PACK = "test-pack" }
}
