package dev.russiancompanion.data.local

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.data.local.seed.SeedJson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class CatalogReseedGuardTest {
    @Test
    fun unchangedCatalogHashDoesNotReplaceBundledRows() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        LocalStore.openInMemory(context).use { store ->
            val json = SeedJson.encode(
                minimalDocument().copy(
                    sourceTexts = listOf(text(matchVerified = true)),
                    assetRightsRecords = listOf(rights()),
                    audioModels = listOf(audio()),
                ),
            )
            store.applySeed(json)
            assertEquals(1, store.eligibleAudio().size)
            val firstVersion = store.seedOutcome()?.contentVersion
            store.applySeed(json)
            assertEquals(firstVersion, store.seedOutcome()?.contentVersion)
            assertEquals(1, store.eligibleAudio().size)
        }
    }

    @Test
    fun supersededIncompleteRecordingsDeleteHiddenFiles() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        LocalStore.openInMemory(context).use { store ->
            store.applySeed(SeedJson.encode(minimalDocument().copy(
                sourceTexts = listOf(text(matchVerified = true)),
                assetRightsRecords = listOf(rights()),
                audioModels = listOf(audio()),
            )))
            val dir = File(context.filesDir, "recordings")
            dir.mkdirs()
            val first = File(dir, "first.m4a")
            val second = File(dir, "second.m4a")
            first.writeBytes(byteArrayOf(1, 2, 3))
            store.commitRecording(
                audioModelId = "audio-1",
                localFileReference = "recordings/first.m4a",
                createdAtLocal = "2026-09-21T09:00",
                markedForTutorReview = false,
                completionState = CompletionState.NOT_COMPLETED,
            )
            second.writeBytes(byteArrayOf(4, 5, 6))
            store.commitRecording(
                audioModelId = "audio-1",
                localFileReference = "recordings/second.m4a",
                createdAtLocal = "2026-09-21T09:01",
                markedForTutorReview = false,
                completionState = CompletionState.NOT_COMPLETED,
            )
            assertEquals(1, store.recordings().size)
            assertEquals("recordings/second.m4a", store.recordings().single().localFileReference)
            assertTrue(!first.exists())
            assertTrue(second.exists())
        }
    }
}
