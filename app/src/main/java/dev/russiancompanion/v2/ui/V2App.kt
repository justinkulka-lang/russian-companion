package dev.russiancompanion.v2.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import dev.russiancompanion.presentation.navigation.isRobolectricRuntime
import dev.russiancompanion.presentation.navigation.snapshotDispatcher
import dev.russiancompanion.presentation.theme.CompanionTheme
import dev.russiancompanion.v2.curriculum.V2CurriculumPack
import dev.russiancompanion.v2.curriculum.V2LearnerView
import dev.russiancompanion.v2.curriculum.V2StartingPoint
import dev.russiancompanion.v2.curriculum.unit
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

internal data class V2UiState(
    val pack: V2CurriculumPack?,
    val learner: V2LearnerView?,
    val localDataOpen: Boolean,
    val packError: String? = null,
    val saveFailed: Boolean = false,
)

internal data class V2OnboardingDraft(
    val hasPenguin: Boolean,
    val hasAnki: Boolean,
    val hasBinder: Boolean,
    val hasTutor: Boolean,
    val startingPoint: V2StartingPoint,
    val dailyMinutes: Int,
)

@Composable
internal fun V2App(
    store: LocalStore?,
    pack: V2CurriculumPack?,
    onRetryOpen: () -> Unit,
    appearanceStore: AppearanceStore = AppearanceStore.memory(defaultAppearancePreference()),
    recordedAt: () -> String = { LocalDateTime.now().toString() },
) {
    val dispatcher = snapshotDispatcher()
    val scope = rememberCoroutineScope()
    val testRuntime = isRobolectricRuntime()
    var appearance by remember(appearanceStore) { mutableStateOf(defaultAppearancePreference()) }
    var appearanceSaveFailed by remember { mutableStateOf(false) }
    var state by remember(store, pack) {
        mutableStateOf(V2UiState(
            pack = pack,
            learner = store?.v2LearnerView(),
            localDataOpen = store != null,
            packError = when {
                store == null -> "Local data could not be opened."
                pack == null -> "The curriculum pack is unavailable."
                else -> null
            },
        ))
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(appearanceStore, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            appearanceStore.preference.collect { appearance = it }
        }
    }
    fun refresh() {
        val next = V2UiState(
            pack = pack,
            learner = store?.v2LearnerView(),
            localDataOpen = store != null,
            packError = when {
                store == null -> "Local data could not be opened."
                pack == null -> "The curriculum pack is unavailable."
                else -> null
            },
        )
        if (testRuntime) {
            state = next
            return
        }
        scope.launch {
            state = withContext(dispatcher) { next }
        }
    }
    CompanionTheme(appearance = appearance) {
        V2NavHost(
            navController = rememberNavController(),
            state = state,
            appearance = appearance,
            appearanceSaveFailed = appearanceSaveFailed,
            onRetryOpen = onRetryOpen,
            onAppearance = { next ->
                scope.launch {
                    val result = withContext(dispatcher) { appearanceStore.set(next) }
                    appearanceSaveFailed = result.isFailure
                }
            },
            onCompleteOnboarding = { draft ->
                val current = store?.v2Profile()
                if (current != null) {
                store.v2SaveProfile(
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
                refresh()
                }
            },
            onCompleteStep = { stepId ->
                store?.v2CompleteStep(stepId, recordedAt())
                refresh()
            },
            onAttempt = { itemId, selected, correct ->
                store?.v2RecordAttempt(itemId, selected, correct, recordedAt())
                refresh()
            },
            onBookmark = { targetId, kind ->
                store?.v2ToggleBookmark(targetId, kind, recordedAt())
                refresh()
            },
            onNote = { targetId, text ->
                store?.v2AddNote(targetId, text, recordedAt())
                refresh()
            },
            onTutor = { targetId, note ->
                store?.v2AddTutorItem(targetId, note, recordedAt())
                refresh()
            },
            onAttestBook = { unit ->
                store?.v2AttestBook(unit, recordedAt())
                refresh()
            },
            onAdvance = { unit ->
                store?.v2CompleteUnit(unit, recordedAt())
                val profile = store?.v2Profile()
                if (profile != null) {
                val nextUnit = (unit + 1).coerceAtMost(30)
                store.v2SaveProfile(
                    profile.copy(
                        currentUnitNumber = nextUnit,
                        currentSessionId = pack?.unit(nextUnit)?.sessions?.firstOrNull()?.id,
                        currentStepIndex = 0,
                    ),
                )
                refresh()
                }
            },
            onResumeAt = { sessionId, index ->
                val profile = store?.v2Profile()
                if (profile != null) {
                store.v2SaveProfile(profile.copy(currentSessionId = sessionId, currentStepIndex = index))
                refresh()
                }
            },
            onTransliteration = { enabled ->
                val profile = store?.v2Profile()
                if (profile != null) {
                store.v2SaveProfile(profile.copy(transliterationEnabled = enabled))
                refresh()
                }
            },
        )
    }
}
