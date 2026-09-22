package dev.russiancompanion.data.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import dev.russiancompanion.domain.practice.PracticeAudio
import java.io.File
import java.io.IOException
import java.util.UUID

internal class AndroidPracticeAudio(
    private val context: Context,
    private val audioManager: AudioManager = context.applicationContext
        .getSystemService(Context.AUDIO_SERVICE) as AudioManager,
) : PracticeAudio {
    private val appContext: Context
        get() = context.applicationContext
    private var player: MediaPlayer? = null
    private var recorder: MediaRecorder? = null
    private var captureFile: File? = null
    private var hasFocus: Boolean = false
    private val packagedAssets: Set<String> by lazy { listPackagedAssets() }
    private val focusListener = AudioManager.OnAudioFocusChangeListener { change ->
        if (change == AudioManager.AUDIOFOCUS_LOSS ||
            change == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ||
            change == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK
        ) {
            releaseForBackground()
        }
    }
    private val focusRequest: AudioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build(),
        )
        .setOnAudioFocusChangeListener(focusListener)
        .build()

    override fun modelPlayable(localAudioReference: String): Boolean {
        if (localAudioReference.isBlank()) return false
        if (localAudioReference in packagedAssets) return true
        return try {
            appContext.assets.open(localAudioReference).use { input -> input.read() >= 0 }
        } catch (_: IOException) {
            false
        }
    }

    override fun playableReferences(references: Collection<String>): Set<String> {
        return references.filterTo(linkedSetOf(), ::modelPlayable)
    }

    override fun recordingReadable(localFileReference: String): Boolean {
        val file = recordingFile(localFileReference)
        return file.isFile && file.length() > 0L
    }

    override fun playModel(localAudioReference: String, onFinished: (Boolean) -> Unit) {
        if (!requestFocus()) {
            onFinished(false)
            return
        }
        val cached = cacheModel(localAudioReference)
        if (cached == null) {
            onFinished(false)
            return
        }
        playFile(cached, onFinished)
    }

    override fun playRecording(localFileReference: String, onFinished: (Boolean) -> Unit) {
        if (!requestFocus()) {
            onFinished(false)
            return
        }
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

    override fun releaseForBackground() {
        stopCapture(keep = false)
        stopPlayback()
        abandonFocus()
    }

    override fun startCapture(): String? {
        stopCapture(keep = false)
        if (!requestFocus()) return null
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

    override fun isCapturing(): Boolean = recorder != null

    private fun requestFocus(): Boolean {
        val granted = audioManager.requestAudioFocus(focusRequest) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        hasFocus = granted
        return granted
    }

    private fun abandonFocus() {
        if (!hasFocus) return
        audioManager.abandonAudioFocusRequest(focusRequest)
        hasFocus = false
    }

    private fun listPackagedAssets(): Set<String> {
        return try {
            listAssets("audio").toSet()
        } catch (_: IOException) {
            emptySet()
        }
    }

    private fun listAssets(directory: String): List<String> {
        val names = appContext.assets.list(directory) ?: return emptyList()
        return names.flatMap { name ->
            val path = "$directory/$name"
            val children = appContext.assets.list(path)
            if (children.isNullOrEmpty()) listOf(path) else listAssets(path)
        }
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

    @Suppress("DEPRECATION")
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
