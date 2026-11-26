package dev.russiancompanion.v2.audio

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.data.audio.AndroidPracticeAudio
import dev.russiancompanion.data.audio.RecordingPlayerFactory
import dev.russiancompanion.domain.practice.PracticeAudio
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class V2ClipPlaybackTest {
    @Test
    fun bundledClipPlaysRecordsAndReplaysWithoutChangingTheSource() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val clip = context.assets.list("audio").orEmpty().first { it.endsWith(".ogg") }
        val model = "audio/$clip"
        val sourceBefore = context.assets.open(model).use { it.readBytes() }
        assertTrue(sourceBefore.isNotEmpty())
        val factory = RecordingPlayerFactory()
        val engine = AndroidPracticeAudio(context, playerFactory = factory)
        val audio = TakeAudio(engine, context)
        val session = V2ClipSession(audio, model)

        session.play(1.0f)
        factory.created[0].complete()
        session.play(0.75f)
        factory.created[1].complete()

        val sourceAfter = context.assets.open(model).use { it.readBytes() }
        assertTrue(sourceBefore.contentEquals(sourceAfter))
        val cached = File(context.cacheDir, "models/${model.replace('/', '_')}")
        assertTrue(cached.readBytes().contentEquals(sourceBefore))
        assertEquals(1.0f, factory.created[0].appliedSpeed)
        assertEquals(0.75f, factory.created[1].appliedSpeed)
        assertTrue(session.modelHeard)

        assertTrue(session.recordStart())
        val take = session.recordStop()
        assertNotNull(take)
        assertTrue(engine.recordingReadable(take!!))
        session.replay()
        factory.created[2].complete()
        assertTrue(session.takeHeard)
        assertTrue(factory.created[2].path!!.endsWith("v2-take.m4a"))

        session.compare(0.75f)
        assertEquals(0.75f, factory.created[3].appliedSpeed)
        factory.created[3].complete()
        factory.created[4].complete()
        assertTrue(session.compared)
        assertTrue(session.takeHeard)
        assertEquals(1.0f, factory.created[4].appliedSpeed)
        val sourceAfterCompare = context.assets.open(model).use { it.readBytes() }
        assertTrue(sourceBefore.contentEquals(sourceAfterCompare))
        assertTrue(cached.readBytes().contentEquals(sourceBefore))
        val names = V2ClipSession::class.java.declaredFields.map { it.name } +
            V2ClipSession::class.java.declaredMethods.map { it.name }
        assertTrue(names.none { it.contains("score", ignoreCase = true) })
        assertFalse(names.any { it.contains("pronunciation", ignoreCase = true) })
    }
}

private class TakeAudio(
    private val real: AndroidPracticeAudio,
    private val context: Context,
) : PracticeAudio by real {
    private var captured: String? = null

    override fun startCapture(): String? {
        val directory = File(context.filesDir, "recordings")
        if (!directory.exists() && !directory.mkdirs()) return null
        val file = File(directory, "v2-take.m4a")
        file.writeBytes(byteArrayOf(4, 5, 6, 7))
        captured = "recordings/${file.name}"
        return captured
    }

    override fun stopCapture(keep: Boolean): String? {
        val reference = captured
        if (!keep) {
            reference?.let { File(context.filesDir, it).delete() }
            captured = null
            return null
        }
        return reference?.takeIf { real.recordingReadable(it) }
    }
}
