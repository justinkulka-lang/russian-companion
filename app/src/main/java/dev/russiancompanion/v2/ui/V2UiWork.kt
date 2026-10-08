package dev.russiancompanion.v2.ui

import dev.russiancompanion.presentation.navigation.isRobolectricRuntime

/**
 * Robolectric UI tests run on the main thread and cannot pump it while a
 * coroutine is waiting. They complete store work inline unless a test opts
 * into the asynchronous path. Production never takes the inline path.
 */
internal object V2UiWork {
    @Volatile
    var blockForTests: Boolean? = null

    @Volatile
    var reconstructionDelay: (suspend () -> Unit)? = null

    @Volatile
    var reconstructionEnteredForTests: java.util.concurrent.CountDownLatch? = null

    private val reconstructionTokenForTests = ThreadLocal<Long>()

    /**
     * Fires on the main thread after a delayed reconstruction either publishes
     * or is discarded. The boolean is true only when that job's snapshot was stored.
     */
    @Volatile
    var reconstructionOutcomeForTests: ((token: Long, published: Boolean) -> Unit)? = null

    fun blocking(): Boolean = blockForTests ?: isRobolectricRuntime()

    internal suspend fun awaitReconstruction(token: Long) {
        reconstructionEnteredForTests?.countDown()
        reconstructionTokenForTests.set(token)
        try {
            reconstructionDelay?.invoke()
        } finally {
            reconstructionTokenForTests.remove()
        }
    }

    /** Token of the reconstruction waiting on this worker thread. Valid inside [reconstructionDelay]. */
    fun currentReconstructionTokenForTests(): Long = reconstructionTokenForTests.get() ?: -1L
}
