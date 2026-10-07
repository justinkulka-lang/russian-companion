package dev.russiancompanion.v2.ui

import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import dev.russiancompanion.v2.curriculum.V2CurriculumPack
import dev.russiancompanion.v2.curriculum.V2LearnerView
import dev.russiancompanion.v2.curriculum.governedSelectionKey
import dev.russiancompanion.v2.curriculum.governedSessionSelections
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.atomic.AtomicInteger

internal enum class V2OutcomeStatus {
    SAVING,
    SAVED,
    REJECTED,
    REFRESH_FAILED,
}

internal fun outcomeStatusText(status: V2OutcomeStatus?): String = when (status) {
    null -> ""
    V2OutcomeStatus.SAVING -> "Saving"
    V2OutcomeStatus.SAVED -> "Saved"
    V2OutcomeStatus.REJECTED -> "Not saved. The previous record is unchanged."
    V2OutcomeStatus.REFRESH_FAILED -> "Saved. The screen could not reload the new record."
}

/**
 * Survives Activity recreation so an in-flight write is not cancelled and
 * started again. Navigation callbacks stay with the screen that requested them.
 * A reopened database replaces [persistence]; work from the previous store
 * cannot publish into the recovered screen.
 */
internal class V2WriteCoordinator(
    persistence: V2Persistence,
) : ViewModel() {
    @Volatile
    private var persistence: V2Persistence = persistence
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val gate = Mutex()
    private val positionTicket = AtomicInteger()
    private var nextGeneration = 0L
    private var appliedGeneration = 0L
    private var outcomeGeneration = 0L

    @Volatile
    private var bindingEpoch = 0L

    private var curriculumPack: V2CurriculumPack? = null

    var learner by mutableStateOf<V2LearnerView?>(null)
        private set
    var loadFailed by mutableStateOf(false)
        private set
    var pending by mutableStateOf<Set<String>>(emptySet())
        private set
    var banner by mutableStateOf<V2OutcomeStatus?>(null)
        private set
    var selections by mutableStateOf<Map<String, String>>(emptyMap())
        private set

    init {
        startLoad()
    }

    /**
     * Keeps this activity-scoped coordinator. A new wrapper around the same
     * database does not reload. A different [V2Persistence.bindingKey] drops
     * the closed store and reads again.
     */
    fun reloadAfterRestore() {
        startLoad()
    }

    fun rebind(next: V2Persistence) {
        val previous = persistence.bindingKey
        persistence = next
        if (next.bindingKey != previous) startLoad()
    }

    /**
     * Stored answers are rebuilt from the pack whenever the learner is loaded.
     * The same pack instance does not rebuild again, so a failed reload can
     * keep a selection that was confirmed after its write.
     */
    fun bindCurriculum(pack: V2CurriculumPack) {
        if (curriculumPack === pack) return
        curriculumPack = pack
        learner?.let { rebuildSelections(it) }
    }

    fun nextPositionTicket(): Int = positionTicket.incrementAndGet()

    fun positionStillCurrent(ticket: Int): Boolean = ticket == positionTicket.get()

    fun confirmSelection(stepId: String, itemId: String, selected: String) {
        selections = selections + (governedSelectionKey(stepId, itemId) to selected)
    }

    /**
     * @return false when a single-flight operation is already pending or there is no store.
     * True means the write was accepted. A later refresh failure is still a committed write.
     */
    fun submit(
        key: String,
        singleFlight: Boolean = true,
        quiet: Boolean = false,
        onSuccess: () -> Unit = {},
        onCommitted: () -> Unit = {},
        block: suspend () -> Unit,
    ): Boolean {
        if (singleFlight && key in pending) return false
        val generation = ++nextGeneration
        val epoch = bindingEpoch
        if (singleFlight) pending = pending + key
        publish(generation, quiet, V2OutcomeStatus.SAVING)
        if (V2UiWork.blocking()) {
            return finishBlocking(key, singleFlight, quiet, generation, epoch, onSuccess, onCommitted, block)
        }
        scope.launch {
            val write = gate.withLock { runCatching { block() } }
            if (epoch != bindingEpoch) return@launch
            if (write.isFailure) {
                if (singleFlight) pending = pending - key
                publish(generation, quiet, V2OutcomeStatus.REJECTED)
                return@launch
            }
            onCommitted()
            val refreshed = runCatching { persistence.learner() }
            if (epoch != bindingEpoch) return@launch
            if (refreshed.isSuccess) {
                applyLearner(generation, refreshed.getOrThrow())
                publish(generation, quiet, V2OutcomeStatus.SAVED)
                onSuccess()
            } else {
                // A failed reload still happened after this generation. An older
                // read must not replace the selection this write already confirmed.
                if (generation > appliedGeneration) appliedGeneration = generation
                publish(generation, quiet, V2OutcomeStatus.REFRESH_FAILED)
            }
            if (singleFlight) pending = pending - key
        }
        return true
    }

    private fun finishBlocking(
        key: String,
        singleFlight: Boolean,
        quiet: Boolean,
        generation: Long,
        epoch: Long,
        onSuccess: () -> Unit,
        onCommitted: () -> Unit,
        block: suspend () -> Unit,
    ): Boolean {
        val write = runCatching { runBlocking { block() } }
        if (epoch != bindingEpoch) return false
        if (write.isFailure) {
            if (singleFlight) pending = pending - key
            publish(generation, quiet, V2OutcomeStatus.REJECTED)
            return false
        }
        onCommitted()
        val refreshed = runCatching { runBlocking { persistence.learner() } }
        if (epoch != bindingEpoch) return true
        if (refreshed.isSuccess) {
            applyLearner(generation, refreshed.getOrThrow())
            publish(generation, quiet, V2OutcomeStatus.SAVED)
            onSuccess()
        } else {
            if (generation > appliedGeneration) appliedGeneration = generation
            publish(generation, quiet, V2OutcomeStatus.REFRESH_FAILED)
        }
        if (singleFlight) pending = pending - key
        return true
    }

    private fun startLoad() {
        val epoch = ++bindingEpoch
        pending = emptySet()
        loadFailed = false
        learner = null
        if (V2UiWork.blocking()) {
            val loaded = runCatching { runBlocking { persistence.learner() } }
            if (epoch != bindingEpoch) return
            applyLoad(epoch, loaded)
        } else {
            scope.launch {
                val loaded = runCatching { persistence.learner() }
                if (epoch != bindingEpoch) return@launch
                applyLoad(epoch, loaded)
            }
        }
    }

    private fun applyLoad(epoch: Long, loaded: Result<V2LearnerView>) {
        if (epoch != bindingEpoch) return
        if (loaded.isSuccess) {
            val view = loaded.getOrThrow()
            learner = view
            rebuildSelections(view)
            loadFailed = false
            appliedGeneration = nextGeneration
            banner = null
            outcomeGeneration = nextGeneration
        } else {
            loadFailed = true
            learner = null
        }
    }

    /**
     * Quiet work never takes the banner. An older completion cannot replace a
     * newer pending or failure outcome. A newer action may supersede an older one.
     */
    private fun publish(generation: Long, quiet: Boolean, status: V2OutcomeStatus) {
        if (quiet || generation < outcomeGeneration) return
        outcomeGeneration = generation
        banner = status
    }

    private fun applyLearner(generation: Long, loaded: V2LearnerView) {
        if (generation < appliedGeneration) return
        appliedGeneration = generation
        learner = loaded
        rebuildSelections(loaded)
    }

    private fun selectionPending(selectionKey: String): Boolean {
        val parts = selectionKey.split('\u001f', limit = 2)
        if (parts.size != 2 || parts[0].isEmpty() || parts[1].isEmpty()) return false
        val prefix = "attempt:${parts[0]}:${parts[1]}:"
        return pending.any { it.startsWith(prefix) }
    }

    private fun rebuildSelections(view: V2LearnerView) {
        val pack = curriculumPack
        val inFlight = selections.filterKeys { selectionPending(it) }
        if (pack == null || V2UiWork.blocking()) {
            val stored = if (pack == null) emptyMap() else governedSessionSelections(pack, view)
            selections = stored + inFlight
            return
        }
        scope.launch(Dispatchers.Default) {
            val stored = governedSessionSelections(pack, view)
            withContext(Dispatchers.Main.immediate) {
                if (curriculumPack !== pack) return@withContext
                selections = stored + selections.filterKeys { selectionPending(it) }
            }
        }
    }

    override fun onCleared() {
        scope.cancel()
    }

    companion object {
        fun get(activity: ComponentActivity, persistence: V2Persistence): V2WriteCoordinator {
            val coordinator = ViewModelProvider(
                activity,
                object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                        @Suppress("UNCHECKED_CAST")
                        return V2WriteCoordinator(persistence) as T
                    }
                },
            )[V2WriteCoordinator::class.java]
            coordinator.rebind(persistence)
            return coordinator
        }
    }
}
