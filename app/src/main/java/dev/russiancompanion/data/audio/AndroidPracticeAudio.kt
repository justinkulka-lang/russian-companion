package dev.russiancompanion.data.audio

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import dev.russiancompanion.domain.practice.PracticeAudio
import java.io.File
import java.io.IOException
import java.util.UUID

internal class AndroidPracticeAudio(
    context: Context,
) : PracticeAudio {
    private val appContext = context.applicationContext
    private var player: MediaPlayer? = null
    private var recorder: MediaRecorder? = null
    private var captureFile: File? = null

    override fun modelPlayable(localAudioReference: String): Boolean {
        return try {
            appContext.assets.open(localAudioReference).use { input -> input.read() >= 0 }
        } catch (_: IOException) {
            false
        }
    }

    override fun recordingReadable(localFileReference: String): Boolean {
        val file = recordingFile(localFileReference)
        return file.isFile && file.length() > 0L
    }

    override fun playModel(localAudioReference: String, onFinished: (Boolean) -> Unit) {
        val cached = cacheModel(localAudioReference)
        if (cached == null) {
            onFinished(false)
            return
        }
        playFile(cached, onFinished)
    }

    override fun playRecording(localFileReference: String, onFinished: (Boolean) -> Unit) {
        val file = recordingFile(localFileReference)
        if (!file.isFile || file.length() == 0L) {
            onFinished(false)
            return
        }
        playFile(file, onFinished)
    }

    override fun stopPlayback() {
        releasePlayer()
    }

    override fun startCapture(): String? {
        stopCapture(keep = false)
        val directory = File(appContext.filesDir, RECORDINGS_DIR)
        if (!directory.exists() && !directory.mkdirs()) return null
        val file = File(directory, "${UUID.randomUUID()}.m4a")
        val created = try {
            val mediaRecorder = newRecorder()
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            mediaRecorder.setOutputFile(file.absolutePath)
            mediaRecorder.prepare()
            mediaRecorder.start()
            recorder = mediaRecorder
            captureFile = file
            true
        } catch (_: Exception) {
            file.delete()
            false
        }
        if (!created) return null
        return "$RECORDINGS_DIR/${file.name}"
    }

    override fun stopCapture(keep: Boolean): String? {
        val file = captureFile
        val active = recorder
        recorder = null
        captureFile = null
        try {
            active?.stop()
        } catch (_: Exception) {
            file?.delete()
            releaseRecorder(active)
            return null
        }
        releaseRecorder(active)
        if (file == null || !file.isFile || file.length() == 0L) {
            file?.delete()
            return null
        }
        if (!keep) {
            file.delete()
            return null
        }
        return "$RECORDINGS_DIR/${file.name}"
    }

    private fun cacheModel(localAudioReference: String): File? {
        val cached = File(appContext.cacheDir, "models/${localAudioReference.replace('/', '_')}")
        if (cached.isFile && cached.length() > 0L) return cached
        return try {
            cached.parentFile?.mkdirs()
            appContext.assets.open(localAudioReference).use { input ->
                cached.outputStream().use { output -> input.copyTo(output) }
            }
            cached.takeIf { it.isFile && it.length() > 0L }
        } catch (_: IOException) {
            cached.delete()
            null
        }
    }

    private fun playFile(file: File, onFinished: (Boolean) -> Unit) {
        releasePlayer()
        val mediaPlayer = MediaPlayer()
        player = mediaPlayer
        try {
            mediaPlayer.setDataSource(file.absolutePath)
            mediaPlayer.setOnCompletionListener {
                releasePlayer()
                onFinished(true)
            }
            mediaPlayer.setOnErrorListener { _, _, _ ->
                releasePlayer()
                onFinished(false)
                true
            }
            mediaPlayer.prepare()
            mediaPlayer.start()
        } catch (_: Exception) {
            releasePlayer()
            onFinished(false)
        }
    }

    private fun releasePlayer() {
        val active = player
        player = null
        active?.release()
    }

    private fun releaseRecorder(active: MediaRecorder?) {
        try {
            active?.release()
        } catch (_: Exception) {
            // The recorder is already unusable.
        }
    }

    private fun recordingFile(localFileReference: String): File {
        return File(appContext.filesDir, localFileReference)
    }

    private fun newRecorder(): MediaRecorder {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(appContext)
        } else {
            MediaRecorder()
        }
    }

    private companion object {
        const val RECORDINGS_DIR = "recordings"
    }
}
