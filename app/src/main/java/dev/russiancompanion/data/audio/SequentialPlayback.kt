package dev.russiancompanion.data.audio

import android.media.MediaPlayer

internal fun interface PlaybackPlayerFactory {
    fun create(): PlaybackPlayer
}

internal interface PlaybackPlayer {
    fun setDataSource(path: String)
    fun setCompletionListener(listener: (() -> Unit)?)
    fun setErrorListener(listener: (() -> Unit)?)
    fun prepare()
    fun setSpeed(speed: Float)
    fun start()
    fun release()
}

internal class MediaPlaybackPlayer(
    private val player: MediaPlayer = MediaPlayer(),
) : PlaybackPlayer {
    private var released: Boolean = false

    override fun setDataSource(path: String) {
        player.setDataSource(path)
    }

    override fun setCompletionListener(listener: (() -> Unit)?) {
        if (listener == null) {
            player.setOnCompletionListener(null)
        } else {
            player.setOnCompletionListener { listener() }
        }
    }

    override fun setErrorListener(listener: (() -> Unit)?) {
        if (listener == null) {
            player.setOnErrorListener(null)
        } else {
            player.setOnErrorListener { _, _, _ ->
                listener()
                true
            }
        }
    }

    override fun prepare() {
        player.prepare()
    }

    override fun setSpeed(speed: Float) {
        if (speed != 1.0f) {
            player.playbackParams = player.playbackParams.setSpeed(speed)
        }
    }

    override fun start() {
        player.start()
    }

    override fun release() {
        if (released) return
        released = true
        try {
            player.setOnCompletionListener(null)
            player.setOnErrorListener(null)
            player.release()
        } catch (_: Exception) {
            // The player is already unusable.
        }
    }
}

/**
 * Owns at most one player. A completion or error listener always detaches and
 * releases the exact instance that finished before invoking a callback that may
 * synchronously start the next player.
 */
internal class SequentialPlayback(
    private val factory: PlaybackPlayerFactory = PlaybackPlayerFactory { MediaPlaybackPlayer() },
) {
    private var player: PlaybackPlayer? = null
    private var pendingFinished: ((Boolean) -> Unit)? = null

    fun play(path: String, speed: Float, onFinished: (Boolean) -> Unit) {
        retireCurrent(succeeded = false)
        val created = factory.create()
        var callback: ((Boolean) -> Unit)? = onFinished
        player = created
        pendingFinished = onFinished
        fun complete(succeeded: Boolean) {
            created.setCompletionListener(null)
            created.setErrorListener(null)
            if (player === created) {
                player = null
            }
            if (pendingFinished === onFinished) {
                pendingFinished = null
            }
            created.release()
            val toNotify = callback
            callback = null
            toNotify?.invoke(succeeded)
        }
        try {
            created.setDataSource(path)
            created.setCompletionListener { complete(true) }
            created.setErrorListener { complete(false) }
            created.prepare()
            created.setSpeed(speed)
            created.start()
        } catch (_: Exception) {
            complete(false)
        }
    }

    fun stop() {
        retireCurrent(succeeded = false)
    }

    private fun retireCurrent(succeeded: Boolean) {
        val active = player
        player = null
        val callback = pendingFinished
        pendingFinished = null
        if (active != null) {
            active.setCompletionListener(null)
            active.setErrorListener(null)
            active.release()
        }
        callback?.invoke(succeeded)
    }
}
