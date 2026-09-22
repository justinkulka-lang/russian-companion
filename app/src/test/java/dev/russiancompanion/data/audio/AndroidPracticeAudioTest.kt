package dev.russiancompanion.data.audio

import android.app.Application
import android.content.Context
import android.media.AudioManager
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class AndroidPracticeAudioTest {
    @Test
    fun backgroundReleaseStopsCaptureAndZeroLengthFilesAreRejected() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val audio = AndroidPracticeAudio(context)
        audio.releaseForBackground()
        assertFalse(audio.isCapturing())
        val empty = File(context.filesDir, "recordings/empty.m4a")
        empty.parentFile?.mkdirs()
        empty.writeBytes(byteArrayOf())
        assertFalse(audio.recordingReadable("recordings/empty.m4a"))
        assertNull(audio.stopCapture(keep = true))
        var playFailed = false
        audio.playRecording("recordings/empty.m4a") { succeeded -> playFailed = !succeeded }
        assertTrue(playFailed)
        audio.releaseForBackground()
        assertFalse(audio.isCapturing())
    }

    @Test
    fun audioFocusLossReleasesPlaybackAndCapture() {
        val context = ApplicationProvider.getApplicationContext<Application>()
        val manager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val audio = AndroidPracticeAudio(context, manager)
        audio.releaseForBackground()
        Shadows.shadowOf(manager)
        assertFalse(audio.isCapturing())
        assertFalse(audio.modelPlayable("audio/missing-model"))
    }
}
