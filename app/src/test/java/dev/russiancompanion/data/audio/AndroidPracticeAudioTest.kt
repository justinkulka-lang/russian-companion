package dev.russiancompanion.data.audio

import android.app.Application
import android.content.Context
import android.media.AudioManager
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.domain.practice.AbPlaybackDriver
import dev.russiancompanion.domain.practice.AbSessionStatus
import dev.russiancompanion.domain.progress.sha256File
import dev.russiancompanion.domain.progress.sha256Hex
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import java.io.File
import java.security.MessageDigest

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

    @Test
    fun completingTheModelDoesNotReleaseTheRecordingPlayer() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val factory = RecordingPlayerFactory()
        val audio = preparedAudio(context, factory)
        val driver = AbPlaybackDriver(
            audio = audio,
            modelReference = MODEL_REF,
            recordingReference = RECORDING_REF,
            modelSpeed = { 0.75f },
        )

        driver.startOnce()
        assertEquals(1, factory.created.size)
        assertTrue(factory.created[0].started)
        assertEquals(0.75f, factory.created[0].appliedSpeed)

        factory.created[0].complete()
        assertEquals(2, factory.created.size)
        assertTrue(factory.created[0].released)
        assertFalse("the model listener must not release the recording player", factory.created[1].released)
        assertTrue(factory.created[1].started)
        assertEquals(1.0f, factory.created[1].appliedSpeed)
        assertEquals(AbSessionStatus.PLAYING_RECORDING, driver.session().status)

        factory.created[0].complete()
        factory.created[0].error()
        assertFalse(factory.created[1].released)
        assertEquals(1, factory.created[0].notifiedCount)

        factory.created[1].complete()
        assertTrue(factory.created[1].released)
        assertEquals(1, driver.session().cyclesCompleted)
        assertFalse(driver.session().active)
    }

    @Test
    fun loopingRecordingCompletionDoesNotReleaseTheNextModelPlayer() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val factory = RecordingPlayerFactory()
        val audio = preparedAudio(context, factory)
        val driver = AbPlaybackDriver(
            audio = audio,
            modelReference = MODEL_REF,
            recordingReference = RECORDING_REF,
            modelSpeed = { 1.0f },
        )

        driver.startLoop()
        factory.created[0].complete()
        factory.created[1].complete()
        assertEquals(3, factory.created.size)
        assertTrue(factory.created[1].released)
        assertFalse(factory.created[2].released)
        assertEquals(AbSessionStatus.PLAYING_MODEL, driver.session().status)
        assertTrue(driver.session().looping)
    }

    @Test
    fun errorCleanupReleasesOnlyTheFailedPlayer() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val factory = RecordingPlayerFactory()
        val audio = preparedAudio(context, factory)
        writeRecording(context, "recordings/second.m4a")

        audio.playRecording(RECORDING_REF) { succeeded ->
            if (!succeeded) audio.playRecording("recordings/second.m4a") { }
        }
        assertEquals(1, factory.created.size)
        factory.created[0].error()
        assertEquals(2, factory.created.size)
        assertTrue(factory.created[0].released)
        assertFalse(factory.created[1].released)
        assertEquals(1, factory.created[0].notifiedCount)

        factory.created[0].error()
        factory.created[0].complete()
        assertFalse(factory.created[1].released)
    }

    @Test
    fun streamedRecordingDigestMatchesStandardSha256AndLeavesMissingFilesNull() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val audio = AndroidPracticeAudio(context)
        val bytes = ByteArray(20_000) { index -> (index % 251).toByte() }
        val file = File(context.filesDir, "recordings/chunked.m4a")
        file.parentFile?.mkdirs()
        file.writeBytes(bytes)
        val expected = MessageDigest.getInstance("SHA-256").digest(bytes)
            .joinToString("") { byte -> "%02x".format(byte) }
        assertEquals(expected, sha256Hex(bytes))
        assertEquals(expected, sha256File(file, bufferSize = 64))
        assertEquals(expected, audio.recordingDigest("recordings/chunked.m4a"))
        assertNull(audio.recordingDigest("recordings/missing.m4a"))
        val empty = File(context.filesDir, "recordings/empty-digest.m4a")
        empty.writeBytes(byteArrayOf())
        assertNull(audio.recordingDigest("recordings/empty-digest.m4a"))
        assertNull(sha256File(empty))
    }

    private fun preparedAudio(context: Context, factory: RecordingPlayerFactory): AndroidPracticeAudio {
        writeModelCache(context, MODEL_REF)
        writeRecording(context, RECORDING_REF)
        return AndroidPracticeAudio(context, playerFactory = factory)
    }

    private fun writeModelCache(context: Context, reference: String) {
        val cached = File(context.cacheDir, "models/${reference.replace('/', '_')}")
        cached.parentFile?.mkdirs()
        cached.writeBytes(byteArrayOf(1, 2, 3, 4))
    }

    private fun writeRecording(context: Context, reference: String) {
        val file = File(context.filesDir, reference)
        file.parentFile?.mkdirs()
        file.writeBytes(byteArrayOf(9, 8, 7, 6))
    }

    private companion object {
        const val MODEL_REF = "audio/model.ogg"
        const val RECORDING_REF = "recordings/take.m4a"
    }
}

internal class RecordingPlayerFactory : PlaybackPlayerFactory {
    val created = mutableListOf<FakePlaybackPlayer>()

    override fun create(): PlaybackPlayer {
        return FakePlaybackPlayer().also { created += it }
    }
}

internal class FakePlaybackPlayer : PlaybackPlayer {
    var path: String? = null
    var appliedSpeed: Float = 1.0f
    var started: Boolean = false
    var released: Boolean = false
    var notifiedCount: Int = 0
    private var onComplete: (() -> Unit)? = null
    private var onError: (() -> Unit)? = null

    override fun setDataSource(path: String) {
        this.path = path
    }

    override fun setCompletionListener(listener: (() -> Unit)?) {
        onComplete = listener
    }

    override fun setErrorListener(listener: (() -> Unit)?) {
        onError = listener
    }

    override fun prepare() = Unit

    override fun setSpeed(speed: Float) {
        appliedSpeed = speed
    }

    override fun start() {
        started = true
    }

    override fun release() {
        released = true
        onComplete = null
        onError = null
    }

    fun complete() {
        val listener = onComplete ?: return
        notifiedCount += 1
        listener()
    }

    fun error() {
        val listener = onError ?: return
        notifiedCount += 1
        listener()
    }
}
