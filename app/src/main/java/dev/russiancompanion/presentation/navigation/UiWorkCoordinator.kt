package dev.russiancompanion.presentation.navigation

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Runs persistence and export off the caller thread and rejects overlapping submissions.
 * Every accepted submission invokes [onResult] exactly once with a success or mapped failure.
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

    fun <T> submit(
        work: () -> T,
        onFailure: (Exception) -> T,
        onResult: (T) -> Unit,
    ): Boolean {
        if (!markBusy()) return false
        if (runSynchronously) {
            val result = try {
                work()
            } catch (error: Exception) {
                onFailure(error)
            }
            finish(result, onResult)
            return true
        }
        scope.launch {
            val result = try {
                withContext(dispatcher) { work() }
            } catch (error: Exception) {
                onFailure(error)
            }
            finish(result, onResult)
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
