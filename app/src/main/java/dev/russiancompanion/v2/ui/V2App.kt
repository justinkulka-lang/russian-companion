package dev.russiancompanion.v2.ui

import android.content.Context
import android.content.ContextWrapper
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
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
import dev.russiancompanion.v2.curriculum.V2StartingPoint
import dev.russiancompanion.v2.curriculum.unit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import android.os.Bundle

internal enum class V2PersistencePhase {
    LOADING,
    READY,
    SAVING,
    SAVED,
    FAILED,
}

internal data class V2UiState(
    val pack: V2CurriculumPack?,
    val learner: dev.russiancompanion.v2.curriculum.V2LearnerView?,
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

private val NoteDraftSaver = Saver<Map<String, String>, Bundle>(
    save = { drafts ->
        Bundle().apply {
            putStringArray("keys", drafts.keys.toTypedArray())
            putStringArray("values", drafts.values.toTypedArray())
        }
    },
    restore = { bundle ->
        val keys = bundle.getStringArray("keys") ?: return@Saver emptyMap()
        val values = bundle.getStringArray("values") ?: return@Saver emptyMap()
        keys.zip(values).toMap()
    },
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
        val loaded = runCatching { withContext(Dispatchers.IO) { load() } }
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
    val scope = androidx.compose.runtime.rememberCoroutineScope()
    var appearance by remember(appearanceStore) { mutableStateOf(defaultAppearancePreference()) }
    var appearanceSaveFailed by remember { mutableStateOf(false) }
    val activity = androidx.compose.ui.platform.LocalContext.current.findActivity()
    val writes = remember(persistence) {
        persistence?.let { V2WriteCoordinator.get(activity, it) }
    }
    var noteDrafts by rememberSaveable(stateSaver = NoteDraftSaver) { mutableStateOf<Map<String, String>>(emptyMap()) }
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(appearanceStore, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            appearanceStore.preference.collect { appearance = it }
        }
    }
    val learner = writes?.learner
    val ready = learner != null && pack != null
    val banner = when {
        !ready && writes?.loadFailed == true -> "Saved progress could not be read."
        !ready && pack != null && store != null -> "Loading saved progress"
        store == null -> "Local data could not be opened."
        pack == null -> "The curriculum pack is unavailable."
        else -> outcomeStatusText(writes?.banner)
    }
    CompanionTheme(appearance = appearance) {
        if (!ready) {
            CompositionLocalProvider(LocalV2PersistenceStatus provides banner) {
                V2LocalDataScreen(message = banner, onRetry = onRetryOpen)
            }
        } else {
            val api = checkNotNull(persistence)
            val state = V2UiState(
                pack = pack,
                learner = learner,
                localDataOpen = true,
                saveFailed = writes.banner == V2OutcomeStatus.REJECTED,
                phase = when (writes.banner) {
                    V2OutcomeStatus.SAVING -> V2PersistencePhase.SAVING
                    V2OutcomeStatus.SAVED -> V2PersistencePhase.SAVED
                    V2OutcomeStatus.REJECTED, V2OutcomeStatus.REFRESH_FAILED -> V2PersistencePhase.FAILED
                    null -> V2PersistencePhase.READY
                },
            )
            CompositionLocalProvider(LocalV2PersistenceStatus provides banner) {
                V2NavHost(
                    navController = rememberNavController(),
                    state = state,
                    appearance = appearance,
                    appearanceSaveFailed = appearanceSaveFailed,
                    selections = writes.selections,
                    lockedItemIds = writes.pending.filter { it.startsWith("attempt:") }.map { it.removePrefix("attempt:") }.toSet(),
                    pendingKeys = writes.pending,
                    noteDrafts = noteDrafts,
                    onRetryOpen = onRetryOpen,
                    onAppearance = { next ->
                        if (V2UiWork.blocking()) {
                            appearanceSaveFailed = runCatching { runBlocking { appearanceStore.set(next) } }.isFailure
                            return@V2NavHost
                        }
                        scope.launch {
                            appearanceSaveFailed = withContext(dispatcher) { appearanceStore.set(next) }.isFailure
                        }
                    },
                    onCompleteOnboarding = { draft ->
                        writes.submit("onboarding") {
                            val current = api.profile()
                            api.saveProfile(
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
                                    currentSessionId = pack.unit(1)?.sessions?.firstOrNull()?.id,
                                    currentStepIndex = 0,
                                ),
                            )
                        }
                        Unit
                    },
                    onCompleteStep = { stepId, afterSaved ->
                        writes.submit("step:$stepId", onSuccess = afterSaved) {
                            api.completeStep(stepId, recordedAt())
                        }
                        Unit
                    },
                    onAttempt = { itemId, selected, correct ->
                        writes.submit(
                            key = "attempt:$itemId",
                            onCommitted = { writes.confirmSelection(itemId, selected) },
                        ) {
                            api.recordAttempt(itemId, selected, correct, recordedAt())
                        }
                    },
                    onBookmark = { targetId, kind ->
                        writes.submit("bookmark:$targetId") {
                            api.toggleBookmark(targetId, kind, recordedAt())
                        }
                        Unit
                    },
                    onNoteDraft = { stepId, text -> noteDrafts = noteDrafts + (stepId to text) },
                    onNote = { targetId, text ->
                        writes.submit("note:$targetId") {
                            api.addNote(targetId, text, recordedAt())
                        }
                        Unit
                    },
                    onTutor = { targetId, note ->
                        writes.submit("tutor:$targetId") {
                            api.addTutor(targetId, note, recordedAt())
                        }
                        Unit
                    },
                    onAttestBook = { unit ->
                        writes.submit("book:$unit") {
                            api.attestBook(unit, recordedAt())
                        }
                        Unit
                    },
                    onAdvance = { unit ->
                        writes.submit("advance:$unit") {
                            val nextUnit = (unit + 1).coerceAtMost(30)
                            api.advanceUnit(
                                unit,
                                recordedAt(),
                                nextUnit,
                                pack.unit(nextUnit)?.sessions?.firstOrNull()?.id,
                            )
                        }
                        Unit
                    },
                    onResumeAt = { sessionId, index ->
                        val ticket = writes.nextPositionTicket()
                        writes.submit("position:$sessionId:$index", singleFlight = false, quiet = true) {
                            if (!writes.positionStillCurrent(ticket)) return@submit
                            val profile = api.profile()
                            if (profile.currentSessionId == sessionId && profile.currentStepIndex == index) return@submit
                            api.saveProfile(profile.copy(currentSessionId = sessionId, currentStepIndex = index))
                        }
                        Unit
                    },
                    onTransliteration = { enabled ->
                        writes.submit("transliteration") {
                            val profile = api.profile()
                            api.saveProfile(profile.copy(transliterationEnabled = enabled))
                        }
                        Unit
                    },
                )
            }
        }
    }
}

private fun Context.findActivity(): ComponentActivity {
    var current = this
    while (current is ContextWrapper) {
        if (current is ComponentActivity) return current
        current = current.baseContext
    }
    error("V2App requires a ComponentActivity")
}
