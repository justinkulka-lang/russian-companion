package dev.russiancompanion.v2.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.compose.rememberNavController
import dev.russiancompanion.data.appearance.AppearanceStore
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.domain.appearance.defaultAppearancePreference
import dev.russiancompanion.presentation.navigation.snapshotDispatcher
import dev.russiancompanion.presentation.theme.CompanionTheme
import dev.russiancompanion.v2.curriculum.V2CurriculumPack
import dev.russiancompanion.v2.curriculum.V2LearnerView
import dev.russiancompanion.v2.curriculum.V2StartingPoint
import dev.russiancompanion.v2.curriculum.unit
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

internal enum class V2PersistencePhase {
    LOADING,
    READY,
    SAVING,
    SAVED,
    FAILED,
}

internal data class V2UiState(
    val pack: V2CurriculumPack?,
    val learner: V2LearnerView?,
    val localDataOpen: Boolean,
    val packError: String? = null,
    val saveFailed: Boolean = false,
    val phase: V2PersistencePhase = V2PersistencePhase.READY,
)

internal fun persistenceStatusText(phase: V2PersistencePhase): String = when (phase) {
    V2PersistencePhase.LOADING -> "Loading saved progress"
    V2PersistencePhase.SAVING -> "Saving"
    V2PersistencePhase.SAVED -> "Saved"
    V2PersistencePhase.FAILED -> "Not saved. The previous record is unchanged."
    V2PersistencePhase.READY -> ""
}

internal data class V2OnboardingDraft(
    val hasPenguin: Boolean,
    val hasAnki: Boolean,
    val hasBinder: Boolean,
    val hasTutor: Boolean,
    val startingPoint: V2StartingPoint,
    val dailyMinutes: Int,
)

internal data class V2StartupResult(
    val store: LocalStore?,
    val pack: V2CurriculumPack?,
    val message: String,
)

@Composable
internal fun V2StartupHost(
    load: suspend () -> V2StartupResult,
    onRetryOpen: () -> Unit,
    content: @Composable (store: LocalStore?, pack: V2CurriculumPack) -> Unit,
) {
    var attempt by remember { mutableIntStateOf(0) }
    var phase by remember { mutableStateOf(V2PersistencePhase.LOADING) }
    var result by remember { mutableStateOf<V2StartupResult?>(null) }
    LaunchedEffect(attempt) {
        if (attempt > 0) onRetryOpen()
        val loaded = runCatching { withContext(kotlinx.coroutines.Dispatchers.IO) { load() } }
        val value = loaded.getOrNull()
        result = value
        phase = if (loaded.isSuccess && value?.store != null && value.pack != null) {
            V2PersistencePhase.READY
        } else {
            V2PersistencePhase.FAILED
        }
    }
    when (phase) {
        V2PersistencePhase.LOADING -> {
            CompositionLocalProvider(LocalV2PersistenceStatus provides "Loading curriculum") {
                V2LocalDataScreen(message = "The course pack is opening on this device.", onRetry = {})
            }
        }
        V2PersistencePhase.FAILED -> {
            V2LocalDataScreen(
                message = result?.message ?: "Local data could not be opened.",
                onRetry = { attempt += 1 },
            )
        }
        else -> content(result?.store, result?.pack!!)
    }
    if (phase == V2PersistencePhase.FAILED && onRetryOpen != {}) Unit
}

@Composable
internal fun V2App(
    store: LocalStore?,
    pack: V2CurriculumPack?,
    onRetryOpen: () -> Unit,
    appearanceStore: AppearanceStore = AppearanceStore.memory(defaultAppearancePreference()),
    recordedAt: () -> String = { LocalDateTime.now().toString() },
    persistence: V2Persistence? = store?.let { StoreV2Persistence(it) },
) {
    val dispatcher = snapshotDispatcher()
    val scope = rememberCoroutineScope()
    var appearance by remember(appearanceStore) { mutableStateOf(defaultAppearancePreference()) }
    var appearanceSaveFailed by remember { mutableStateOf(false) }
    val blocking = V2UiWork.blocking()
    var state by remember(store, pack) {
        mutableStateOf(
            if (blocking && store != null && pack != null && persistence != null) {
                val loaded = runCatching { runBlocking { persistence.learner() } }
                V2UiState(
                    pack = pack,
                    learner = loaded.getOrNull(),
                    localDataOpen = loaded.isSuccess,
                    packError = if (loaded.isSuccess) null else "Saved progress could not be read.",
                    phase = if (loaded.isSuccess) V2PersistencePhase.READY else V2PersistencePhase.FAILED,
                    saveFailed = loaded.isFailure,
                )
            } else {
                V2UiState(
                    pack = pack,
                    learner = null,
                    localDataOpen = store != null && pack != null,
                    packError = when {
                        store == null -> "Local data could not be opened."
                        pack == null -> "The curriculum pack is unavailable."
                        else -> null
                    },
                    phase = if (store == null || pack == null) V2PersistencePhase.FAILED else V2PersistencePhase.LOADING,
                )
            },
        )
    }
    var selections by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var noteDrafts by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    val pending = remember { mutableSetOf<String>() }
    val gate = remember { Mutex() }
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(appearanceStore, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            appearanceStore.preference.collect { appearance = it }
        }
    }
    LaunchedEffect(persistence) {
        if (persistence == null || blocking) {
            if (blocking) selections = state.learner?.attemptSelections.orEmpty()
            return@LaunchedEffect
        }
        val loaded = runCatching { persistence.learner() }
        if (loaded.isSuccess) {
            val learner = loaded.getOrThrow()
            selections = learner.attemptSelections
            state = state.copy(learner = learner, phase = V2PersistencePhase.READY, saveFailed = false, packError = null)
        } else {
            state = state.copy(
                phase = V2PersistencePhase.FAILED,
                saveFailed = true,
                packError = "Saved progress could not be read.",
            )
        }
    }
    fun mergeSelections(learner: V2LearnerView) {
        val inFlight = selections.filterKeys { itemId -> "attempt:$itemId" in pending }
        selections = learner.attemptSelections + inFlight
    }
    fun submit(
        key: String,
        singleFlight: Boolean = true,
        onSuccess: () -> Unit = {},
        onFailure: () -> Unit = {},
        block: suspend () -> Unit,
    ): Boolean {
        val active = persistence ?: return false
        if (singleFlight && key in pending) return false
        if (blocking) {
            val result = runCatching { runBlocking { block() } }
            if (result.isSuccess) {
                val refreshed = runCatching { runBlocking { active.learner() } }
                if (refreshed.isSuccess) {
                    val learner = refreshed.getOrThrow()
                    selections = learner.attemptSelections
                    state = state.copy(learner = learner, phase = V2PersistencePhase.SAVED, saveFailed = false)
                    onSuccess()
                    return true
                }
            }
            state = state.copy(phase = V2PersistencePhase.FAILED, saveFailed = true)
            onFailure()
            return false
        }
        if (singleFlight) pending += key
        scope.launch {
            state = state.copy(phase = V2PersistencePhase.SAVING, saveFailed = false)
            val result = gate.withLock { runCatching { block() } }
            if (singleFlight) pending.remove(key)
            if (result.isSuccess) {
                val refreshed = runCatching { active.learner() }
                if (refreshed.isSuccess) {
                    val learner = refreshed.getOrThrow()
                    mergeSelections(learner)
                    state = state.copy(learner = learner, phase = V2PersistencePhase.SAVED, saveFailed = false)
                    onSuccess()
                } else {
                    state = state.copy(phase = V2PersistencePhase.FAILED, saveFailed = true)
                    onFailure()
                }
            } else {
                state = state.copy(phase = V2PersistencePhase.FAILED, saveFailed = true)
                onFailure()
            }
        }
        return true
    }
    CompanionTheme(appearance = appearance) {
        val learner = state.learner
        val readyPack = state.pack
        if (learner == null || readyPack == null) {
            val message = when (state.phase) {
                V2PersistencePhase.LOADING -> persistenceStatusText(state.phase)
                V2PersistencePhase.FAILED -> state.packError ?: "Local data could not be opened."
                else -> state.packError ?: "Local data could not be opened."
            }
            CompositionLocalProvider(LocalV2PersistenceStatus provides message) {
                V2LocalDataScreen(message = message, onRetry = onRetryOpen)
            }
        } else {
            CompositionLocalProvider(LocalV2PersistenceStatus provides persistenceStatusText(state.phase)) {
                V2NavHost(
                    navController = rememberNavController(),
                    state = state,
                    appearance = appearance,
                    appearanceSaveFailed = appearanceSaveFailed,
                    selections = selections,
                    lockedItemIds = pending.filter { it.startsWith("attempt:") }.map { it.removePrefix("attempt:") }.toSet(),
                    noteDrafts = noteDrafts,
                    onRetryOpen = onRetryOpen,
                    onAppearance = { next ->
                        if (blocking) {
                            val result = runCatching { runBlocking { appearanceStore.set(next) } }
                            appearanceSaveFailed = result.isFailure
                            state = state.copy(
                                phase = if (result.isFailure) V2PersistencePhase.FAILED else V2PersistencePhase.SAVED,
                                saveFailed = result.isFailure,
                            )
                            return@V2NavHost
                        }
                        scope.launch {
                            state = state.copy(phase = V2PersistencePhase.SAVING, saveFailed = false)
                            val result = withContext(dispatcher) { appearanceStore.set(next) }
                            appearanceSaveFailed = result.isFailure
                            state = state.copy(
                                phase = if (result.isFailure) V2PersistencePhase.FAILED else V2PersistencePhase.SAVED,
                                saveFailed = result.isFailure,
                            )
                        }
                    },
                    onCompleteOnboarding = { draft ->
                        submit("onboarding") {
                            val current = persistence?.profile() ?: return@submit
                            persistence.saveProfile(
                                current.copy(
                                    onboardingCompleted = true,
                                    currentUnitNumber = 1,
                                    startingPoint = draft.startingPoint.name,
                                    hasPenguin = draft.hasPenguin,
                                    hasAnki = draft.hasAnki,
                                    hasBinder = draft.hasBinder,
                                    hasTutor = draft.hasTutor,
                                    dailyMinutes = draft.dailyMinutes,
                                    transliterationEnabled = draft.startingPoint == V2StartingPoint.NEW_TO_CYRILLIC,
                                    currentSessionId = pack?.unit(1)?.sessions?.firstOrNull()?.id,
                                    currentStepIndex = 0,
                                ),
                            )
                        }
                        Unit
                    },
                    onCompleteStep = { stepId, afterSaved ->
                        submit("step:$stepId", onSuccess = afterSaved) {
                            persistence?.completeStep(stepId, recordedAt())
                        }
                        Unit
                    },
                    onAttempt = { itemId, selected, correct ->
                        val accepted = submit(
                            key = "attempt:$itemId",
                            onFailure = { selections = selections - itemId },
                        ) {
                            persistence?.recordAttempt(itemId, selected, correct, recordedAt())
                        }
                        if (accepted) selections = selections + (itemId to selected)
                        accepted
                    },
                    onBookmark = { targetId, kind ->
                        submit("bookmark:$targetId") {
                            persistence?.toggleBookmark(targetId, kind, recordedAt())
                        }
                        Unit
                    },
                    onNoteDraft = { stepId, text -> noteDrafts = noteDrafts + (stepId to text) },
                    onNote = { targetId, text ->
                        submit("note:$targetId:${text.hashCode()}") {
                            persistence?.addNote(targetId, text, recordedAt())
                        }
                        Unit
                    },
                    onTutor = { targetId, note ->
                        submit("tutor:$targetId:${recordedAt()}") {
                            persistence?.addTutor(targetId, note, recordedAt())
                        }
                        Unit
                    },
                    onAttestBook = { unit ->
                        submit("book:$unit") {
                            persistence?.attestBook(unit, recordedAt())
                        }
                        Unit
                    },
                    onAdvance = { unit ->
                        submit("advance:$unit") {
                            val active = persistence ?: return@submit
                            active.completeUnit(unit, recordedAt())
                            val profile = active.profile()
                            val nextUnit = (unit + 1).coerceAtMost(30)
                            active.saveProfile(
                                profile.copy(
                                    currentUnitNumber = nextUnit,
                                    currentSessionId = pack?.unit(nextUnit)?.sessions?.firstOrNull()?.id,
                                    currentStepIndex = 0,
                                ),
                            )
                        }
                        Unit
                    },
                    onResumeAt = { sessionId, index ->
                        submit("position:$sessionId:$index", singleFlight = false) {
                            val active = persistence ?: return@submit
                            val profile = active.profile()
                            active.saveProfile(profile.copy(currentSessionId = sessionId, currentStepIndex = index))
                        }
                        Unit
                    },
                    onTransliteration = { enabled ->
                        submit("transliteration") {
                            val active = persistence ?: return@submit
                            val profile = active.profile()
                            active.saveProfile(profile.copy(transliterationEnabled = enabled))
                        }
                        Unit
                    },
                )
            }
        }
    }
}
