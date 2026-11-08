package dev.russiancompanion.v2

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.RussianCompanionApplication
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.backup.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.ByteArrayOutputStream
import java.io.File
import java.security.MessageDigest
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class BackupVersionBoundaryTest {
    @Test fun currentWriterAndReaderRoundtripTheCurrentDocument() {
        withCurrentDocument { document ->
            val bytes = archive(document)
            val entries = entries(bytes)
            val manifest = backupJson.decodeFromString(BackupManifest.serializer(),
                entries.getValue(BACKUP_MANIFEST_ENTRY).decodeToString())
            assertEquals(BACKUP_FORMAT_VERSION, manifest.formatVersion)
            assertEquals(BACKUP_SCHEMA_VERSION, document.schemaVersion)
            val read = read(bytes)
            assertTrue(read is BackupReadResult.Valid)
            assertEquals(document, (read as BackupReadResult.Valid).backup.document)
        }
    }

    @Test fun obsoleteFutureAndMismatchedVersionsAreRejectedWithCorrectHashes() {
        withCurrentDocument { document ->
            val valid = archive(document)
            val unsupported = listOf(Int.MIN_VALUE, 0, 1, 2, BACKUP_FORMAT_VERSION + 1, Int.MAX_VALUE)
            for (version in unsupported) {
                val combinations = listOf(
                    version to BACKUP_SCHEMA_VERSION,
                    BACKUP_FORMAT_VERSION to version,
                    version to version,
                )
                for ((format, schema) in combinations) {
                    val forged = rewrite(valid, format) { it.copy(schemaVersion = schema) }
                    assertEquals("format=$format schema=$schema",
                        BackupReadResult.Rejected(BackupRejection.UNSUPPORTED_VERSION), read(forged))
                }
            }
        }
    }

    @Test fun currentVersionStillRejectsBadHashesAndForgedCompletionLinks() {
        withCurrentDocument { document ->
            val valid = archive(document)
            val forged = rewrite(valid) {
                it.copy(unitCompletions = listOf(BackupUnitCompletion(3, "forged", false,
                    "missing-policy", "missing-assignment", 1)))
            }
            assertEquals(BackupReadResult.Rejected(BackupRejection.MALFORMED), read(forged))
            val badHash = rewrite(valid, correctHash = false) {
                it.copy(profile = it.profile.copy(dailyMinutes = it.profile.dailyMinutes + 1))
            }
            assertEquals(BackupReadResult.Rejected(BackupRejection.INTEGRITY), read(badHash))
        }
    }

    private fun withCurrentDocument(block: (GovernedLearnerDocument) -> Unit) {
        (context().applicationContext as RussianCompanionApplication).closeStartupStoreForTests()
        LocalStore.openInMemory(context()).use { store ->
            store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = 2))
            store.v2AttestBook(2, "current-fixture")
            block(store.governedLearnerDocument())
        }
    }

    private fun archive(document: GovernedLearnerDocument): ByteArray = ByteArrayOutputStream().also {
        writeGovernedBackup(it, APP, PACK, 1, "DARK", document, emptyList())
    }.toByteArray()

    /** Corrupt serialized bytes independently of the production writer's version contract. */
    private fun rewrite(bytes: ByteArray, format: Int = BACKUP_FORMAT_VERSION,
                        correctHash: Boolean = true,
                        change: (GovernedLearnerDocument) -> GovernedLearnerDocument): ByteArray {
        val entries = entries(bytes).toMutableMap()
        val original = backupJson.decodeFromString(GovernedLearnerDocument.serializer(),
            entries.getValue(BACKUP_LEARNER_ENTRY).decodeToString())
        val learner = backupJson.encodeToString(GovernedLearnerDocument.serializer(), change(original)).encodeToByteArray()
        val manifest = backupJson.decodeFromString(BackupManifest.serializer(),
            entries.getValue(BACKUP_MANIFEST_ENTRY).decodeToString())
        val files = manifest.files.map { file ->
            if (file.path != BACKUP_LEARNER_ENTRY || !correctHash) file else file.copy(
                bytes = learner.size.toLong(),
                sha256 = MessageDigest.getInstance("SHA-256").digest(learner)
                    .joinToString("") { "%02x".format(it) },
            )
        }
        entries[BACKUP_LEARNER_ENTRY] = learner
        entries[BACKUP_MANIFEST_ENTRY] = backupJson.encodeToString(BackupManifest.serializer(),
            manifest.copy(formatVersion = format, files = files)).encodeToByteArray()
        return ByteArrayOutputStream().also { output ->
            ZipOutputStream(output).use { zip ->
                entries.forEach { (name, content) ->
                    zip.putNextEntry(ZipEntry(name)); zip.write(content); zip.closeEntry()
                }
            }
        }.toByteArray()
    }

    private fun entries(bytes: ByteArray): Map<String, ByteArray> {
        val entries = linkedMapOf<String, ByteArray>()
        ZipInputStream(bytes.inputStream()).use { zip ->
            while (true) {
                val entry = zip.nextEntry ?: break
                entries[entry.name] = zip.readBytes()
            }
        }
        return entries
    }

    private fun read(bytes: ByteArray): BackupReadResult {
        val stage = File(context().cacheDir, "backup-version-${System.nanoTime()}")
        return try { readGovernedBackup(bytes.inputStream(), APP, PACK, 1, stage) }
        finally { stage.deleteRecursively() }
    }

    private fun context(): Context = ApplicationProvider.getApplicationContext()

    companion object {
        private const val APP = "dev.russiancompanion"
        private const val PACK = "backup-version-fixture"
    }
}
