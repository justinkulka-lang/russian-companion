package dev.russiancompanion.presentation.navigation

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Runs persistence and export off the caller thread and rejects overlapping submissions.
 */
internal class UiWorkCoordinator(
    private val dispatcher: CoroutineDispatcher,
    private val scope: CoroutineScope,
    private val runSynchronously: Boolean,
    private val onBusyChange: (Boolean) -> Unit,
) {
    private var busy = false

    @Synchronized
    fun isBusy(): Boolean = busy

    fun <T> submit(work: () -> T, onResult: (T) -> Unit): Boolean {
        if (!markBusy()) return false
        if (runSynchronously) {
            try {
                finish(work(), onResult)
            } catch (_: Exception) {
                clearBusy()
            }
            return true
        }
        scope.launch {
            try {
                val result = withContext(dispatcher) { work() }
                finish(result, onResult)
            } catch (_: Exception) {
                clearBusy()
            }
        }
        return true
    }

    @Synchronized
    private fun markBusy(): Boolean {
        if (busy) return false
        busy = true
        onBusyChange(true)
        return true
    }

    @Synchronized
    private fun clearBusy() {
        busy = false
        onBusyChange(false)
    }

    private fun <T> finish(result: T, onResult: (T) -> Unit) {
        clearBusy()
        onResult(result)
    }
}
