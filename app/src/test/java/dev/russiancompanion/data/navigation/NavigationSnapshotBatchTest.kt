package dev.russiancompanion.data.navigation

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.data.local.audio
import dev.russiancompanion.data.local.minimalDocument
import dev.russiancompanion.data.local.rights
import dev.russiancompanion.data.local.seed.SeedJson
import dev.russiancompanion.data.local.text
import dev.russiancompanion.domain.navigation.NavigationSurface
import dev.russiancompanion.domain.practice.PracticeAudio
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class NavigationSnapshotBatchTest {
    @Test
    fun shellSnapshotDoesNotOpenEachAudioAsset() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        LocalStore.openInMemory(context).use { store ->
            store.applySeed(
                SeedJson.encode(
                    minimalDocument().copy(
                        sourceTexts = listOf(text(matchVerified = true)),
                        assetRightsRecords = listOf(rights()),
                        audioModels = listOf(audio()),
                    ),
                ),
            )
            val audio = CountingPracticeAudio()
            val gateway = RoomNavigationGateway(store, { null }, audio)
            val shell = gateway.snapshot("2026-09-21", NavigationSurface.SHELL)
            assertEquals(0, audio.playableCalls)
            assertEquals(1, audio.batchCalls)
            assertEquals(1, shell.audio.size)
            assertEquals(1, shell.eligibleAudioCount)
            val practice = gateway.snapshot("2026-09-21", NavigationSurface.PRACTICE)
            assertEquals(2, audio.batchCalls)
            assertEquals(1, practice.audio.size)
            val full = gateway.current("2026-09-21")
            assertTrue(full.audio.isNotEmpty())
            assertTrue(full.attributions.isNotEmpty() || full.assetRecords.isNotEmpty())
        }
    }

    private class CountingPracticeAudio : PracticeAudio {
        var playableCalls: Int = 0
        var batchCalls: Int = 0

        override fun modelPlayable(localAudioReference: String): Boolean {
            playableCalls += 1
            return true
        }

        override fun playableReferences(references: Collection<String>): Set<String> {
            batchCalls += 1
            return references.toSet()
        }

        override fun recordingReadable(localFileReference: String) = localFileReference.isNotBlank()

        override fun playModel(localAudioReference: String, onFinished: (Boolean) -> Unit) = onFinished(true)

        override fun playRecording(localFileReference: String, onFinished: (Boolean) -> Unit) = onFinished(true)

        override fun stopPlayback() = Unit

        override fun startCapture(): String? = null

        override fun stopCapture(keep: Boolean): String? = null
    }
}
