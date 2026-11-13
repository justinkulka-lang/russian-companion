package dev.russiancompanion.data.audio

import android.content.Context
import android.media.MediaRecorder
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.v2.audio.V2ClipSession
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowMediaRecorder
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class ProductionCaptureTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Test fun productionCaptureConfiguresRecorderKeepsReplacementAndReplaysTheLatestTake() {
        val probe = RecorderProbe(context)
        val players = RecordingPlayerFactory()
        val audio = AndroidPracticeAudio(context, playerFactory = players, recorderFactory = probe::create)
        val session = V2ClipSession(audio, "audio/model.ogg")
        assertTrue(session.recordStart())
        assertTrue(audio.isCapturing())
        assertNull(session.takeReference)
        val recorder = shadowOf(probe.created.single())
        assertEquals(MediaRecorder.AudioSource.MIC, recorder.audioSource)
        assertEquals(MediaRecorder.OutputFormat.MPEG_4, recorder.outputFormat)
        assertEquals(MediaRecorder.AudioEncoder.AAC, recorder.audioEncoder)
        assertEquals(ShadowMediaRecorder.STATE_RECORDING, recorder.state)
        var prematureReplay = true
        session.replay { prematureReplay = it }
        assertFalse(prematureReplay)
        assertTrue(players.created.isEmpty())
        probe.writeTake()
        val first = session.recordStop()!!
        assertFalse(audio.isCapturing())
        assertEquals(ShadowMediaRecorder.STATE_RELEASED, recorder.state)
        assertTrue(audio.recordingReadable(first))
        session.replay()
        assertEquals(File(context.filesDir, first).absolutePath, players.created.last().path)
        players.created.last().complete()
        assertTrue(session.takeHeard)

        assertTrue(session.recordStart())
        assertNull(session.takeReference)
        assertFalse(session.takeHeard)
        assertFalse(session.compared)
        probe.writeTake(byteArrayOf(8, 9))
        val second = session.recordStop()!!
        assertNotEquals(first, second)
        assertTrue(audio.recordingReadable(second))
        session.replay()
        assertEquals(File(context.filesDir, second).absolutePath, players.created.last().path)
        players.created.last().complete()
        assertTrue(session.takeHeard)
        audio.releaseForBackground()
    }

    @Test fun prepareAndStartFailuresReleaseThePartlyCreatedRecorderAndAllowRetry() {
        for (failure in listOf("prepare", "start")) {
            val probe = RecorderProbe(context).also { it.failure = failure }
            val audio = AndroidPracticeAudio(context, recorderFactory = probe::create)
            assertNull(audio.startCapture())
            assertFalse(audio.isCapturing())
            assertEquals(ShadowMediaRecorder.STATE_RELEASED, shadowOf(probe.created.last()).state)
            assertFalse(probe.output().exists())
            probe.failure = null
            assertNotNull(audio.startCapture())
            assertTrue(audio.isCapturing())
            probe.writeTake()
            assertNotNull(audio.stopCapture(true))
            audio.releaseForBackground()
        }
    }

    @Test fun failedAndEmptyStopsDiscardTheFileAndDoNotExposeATakeThenRetrySucceeds() {
        for (failure in listOf("stop", "empty")) {
            val probe = RecorderProbe(context).also { it.failure = failure }
            val audio = AndroidPracticeAudio(context, recorderFactory = probe::create)
            val session = V2ClipSession(audio, "audio/model.ogg")
            assertTrue(session.recordStart())
            probe.writeTake(if (failure == "empty") byteArrayOf() else byteArrayOf(1, 2))
            val failed = probe.output()
            assertNull(session.recordStop())
            assertNull(session.takeReference)
            assertFalse(audio.isCapturing())
            assertFalse(failed.exists())
            assertEquals(ShadowMediaRecorder.STATE_RELEASED, shadowOf(probe.created.last()).state)
            probe.failure = null
            assertTrue(session.recordStart())
            probe.writeTake()
            assertNotNull(session.recordStop())
            session.release()
        }
    }

    @Test fun defaultRecorderFactoryCanStartAndBackgroundReleaseDiscardsTheCapture() {
        val audio = AndroidPracticeAudio(context)
        val reference = audio.startCapture()!!
        assertTrue(audio.isCapturing())
        val output = File(context.filesDir, reference)
        output.writeBytes(byteArrayOf(1, 2, 3))
        audio.releaseForBackground()
        assertFalse(audio.isCapturing())
        assertFalse(output.exists())
        assertNull(audio.stopCapture(true))
    }
}

/** Only native recorder behavior is simulated; production start/stop/file handling runs unchanged. */
internal class RecorderProbe(private val context: Context) {
    var failure: String? = null
    val created = mutableListOf<MediaRecorder>()
    fun create(): MediaRecorder = object : MediaRecorder(context) {
        override fun prepare() {
            if (failure == "prepare") throw IllegalStateException("Recorder prepare failed")
            super.prepare()
        }
        override fun start() {
            if (failure == "start") throw IllegalStateException("Recorder start failed")
            super.start()
        }
        override fun stop() {
            if (failure == "stop") throw IllegalStateException("Recorder stop failed")
            super.stop()
        }
    }.also { created += it }
    fun output() = File(shadowOf(created.last()).outputPath)
    fun writeTake(bytes: ByteArray = byteArrayOf(4, 5, 6, 7)) { output().writeBytes(bytes) }
}
