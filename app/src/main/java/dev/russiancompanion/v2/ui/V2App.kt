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
import dev.russiancompanion.v2.curriculum.GOVERNED_IDENTITY_SCHEMA
import dev.russiancompanion.v2.curriculum.OPTIONAL_PRACTICE
import dev.russiancompanion.v2.curriculum.PRESCRIBED_CHECK
import dev.russiancompanion.v2.curriculum.V2CurriculumPack
import dev.russiancompanion.v2.curriculum.V2StartingPoint
import dev.russiancompanion.v2.curriculum.V2PackIdentityIndex
import dev.russiancompanion.v2.curriculum.governedStepCompletionDigest
import dev.russiancompanion.v2.curriculum.lastGovernedIdentityThreadForTests
import dev.russiancompanion.v2.curriculum.recordGovernedIdentityForTests
import dev.russiancompanion.v2.curriculum.prepareGovernedAttempt
import dev.russiancompanion.v2.curriculum.practiceStepId
import dev.russiancompanion.v2.curriculum.resumePositionDigest
import dev.russiancompanion.v2.curriculum.targetContentDigest
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
    persistence: V2Persistence? = store?.let { StoreV2Persistence(it, pack) },
) {
    val dispatcher = snapshotDispatcher()
    val scope = androidx.compose.runtime.rememberCoroutineScope()
    var appearance by remember(appearanceStore) { mutableStateOf(defaultAppearancePreference()) }
    var appearanceSaveFailed by remember { mutableStateOf(false) }
    val activity = androidx.compose.ui.platform.LocalContext.current.findActivity()
    val writes = remember(persistence?.bindingKey) {
        persistence?.let { V2WriteCoordinator.get(activity, it) }
    }
    var noteDrafts by rememberSaveable(stateSaver = NoteDraftSaver) { mutableStateOf<Map<String, String>>(emptyMap()) }
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(appearanceStore, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            appearanceStore.preference.collect { appearance = it }
        }
    }
    val rawLearner = writes?.learner
    val completionSnapshot = if (pack != null && rawLearner != null) {
        rememberGoverned(pack, rawLearner, label = "completion-evidence") {
            dev.russiancompanion.v2.curriculum.currentCompletedUnitNumbers(pack, rawLearner)
            pack to rawLearner
        }
    } else null
    // Publish only after worker-side verification; a different pack cannot reuse old evidence.
    val learner = completionSnapshot?.takeIf { it.first === pack }?.second
    val ready = learner != null && pack != null && writes != null
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
            val backup = V2BackupCoordinator.get(
                activity = activity,
                store = checkNotNull(store),
                applicationId = dev.russiancompanion.BuildConfig.APPLICATION_ID,
                packId = pack.packId,
                packVersion = pack.packVersion,
                appearanceStore = appearanceStore,
            )
            writes.bindCurriculum(pack)
            CompositionLocalProvider(LocalV2PersistenceStatus provides banner) {
                V2NavHost(
                    navController = rememberNavController(),
                    state = state,
                    appearance = appearance,
                    appearanceSaveFailed = appearanceSaveFailed,
                    selections = writes.selections,
                    lockedItemIds = pendingAttemptItemIds(writes.pending),
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
                        val epoch = store.learnerMutationEpoch()
                        writes.submit("onboarding") {
                            val prepared = withContext(Dispatchers.Default) {
                                recordGovernedIdentityForTests("onboarding-resume")
                                V2PackIdentityIndex.warm(pack)
                                val sessionId = pack.unit(1)?.sessions?.firstOrNull()?.id
                                val digest = sessionId?.let { resumePositionDigest(pack, it, 0) }.orEmpty()
                                sessionId to digest
                            }
                            val (sessionId, digest) = prepared
                            withCapturedLearnerEpoch(epoch) {
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
                                        currentSessionId = sessionId,
                                        currentStepIndex = 0,
                                        resumeContentDigest = digest,
                                        resumeIdentitySchema = if (digest.isEmpty()) 0 else GOVERNED_IDENTITY_SCHEMA,
                                    ),
                                )
                            }
                        }
                        Unit
                    },
                    onCompleteStep = { stepId, afterSaved ->
                        val epoch = store.learnerMutationEpoch()
                        writes.submit("step:$stepId", onSuccess = afterSaved) {
                            val digest = withContext(Dispatchers.Default) {
                                lastGovernedIdentityThreadForTests = Thread.currentThread()
                                V2PackIdentityIndex.warm(pack)
                                val current = store.v2LearnerViewForValidation()
                                governedStepCompletionDigest(pack, current, stepId)
                            } ?: error("unverified step")
                            store.awaitBeforeCompletionWriteForTests()
                            withCapturedLearnerEpoch(epoch) {
                                api.completeStep(stepId, recordedAt(), digest)
                            }
                        }
                        Unit
                    },
                    onAttempt = { itemId, selected, stepId ->
                        if (writes.pending.any { it.startsWith("attempt:$stepId:$itemId:") }) {
                            return@V2NavHost false
                        }
                        val epoch = store.learnerMutationEpoch()
                        val at = recordedAt()
                        writes.submit(
                            key = "attempt:$stepId:$itemId:$selected",
                            onCommitted = { writes.confirmSelection(stepId, itemId, selected) },
                        ) {
                            val write = withContext(Dispatchers.Default) {
                                lastGovernedIdentityThreadForTests = Thread.currentThread()
                                V2PackIdentityIndex.warm(pack)
                                prepareGovernedAttempt(pack, itemId, selected, PRESCRIBED_CHECK, stepId, at)
                            } ?: error("unverified attempt")
                            withCapturedLearnerEpoch(epoch) { api.recordAttempt(write) }
                        }
                    },
                    onOpenCumulativeReview = { targetUnit ->
                        val epoch = store.learnerMutationEpoch()
                        writes.submit("review:open:$targetUnit") {
                            withContext(Dispatchers.Default) { V2PackIdentityIndex.warm(pack) }
                            withCapturedLearnerEpoch(epoch) { api.openCumulativeReview(pack, targetUnit) }
                        }
                        Unit
                    },
                    onCumulativeAnswer = { assignment, slot, selected ->
                        val epoch = store.learnerMutationEpoch()
                        writes.submit("review:answer:$assignment:$slot") {
                            withCapturedLearnerEpoch(epoch) { api.answerCumulativeReview(pack, assignment, slot, selected) }
                        }
                        Unit
                    },
                    onPracticeAttempt = { labId, itemId, selected ->
                        if (writes.pending.any { it.startsWith("practice:$labId:$itemId:") }) {
                            return@V2NavHost false
                        }
                        val epoch = store.learnerMutationEpoch()
                        val at = recordedAt()
                        writes.submit(key = "practice:$labId:$itemId:$selected") {
                            val write = withContext(Dispatchers.Default) {
                                lastGovernedIdentityThreadForTests = Thread.currentThread()
                                V2PackIdentityIndex.warm(pack)
                                prepareGovernedAttempt(
                                    pack,
                                    itemId,
                                    selected,
                                    OPTIONAL_PRACTICE,
                                    practiceStepId(labId),
                                    at,
                                )
                            } ?: error("unverified attempt")
                            withCapturedLearnerEpoch(epoch) { api.recordAttempt(write) }
                        }
                    },
                    onBookmark = { targetId, kind ->
                        val epoch = store.learnerMutationEpoch()
                        writes.submit("bookmark:$targetId") {
                            val digest = withContext(Dispatchers.Default) {
                                V2PackIdentityIndex.warm(pack)
                                targetContentDigest(pack, targetId, kind)
                            } ?: error("unverified target")
                            withCapturedLearnerEpoch(epoch) {
                                api.toggleBookmark(targetId, kind, recordedAt(), digest)
                            }
                        }
                        Unit
                    },
                    onNoteDraft = { stepId, text -> noteDrafts = noteDrafts + (stepId to text) },
                    onNote = { targetId, text ->
                        val epoch = store.learnerMutationEpoch()
                        writes.submit("note:$targetId") {
                            val digest = withContext(Dispatchers.Default) {
                                V2PackIdentityIndex.warm(pack)
                                targetContentDigest(pack, targetId, "STEP")
                            } ?: error("unverified target")
                            withCapturedLearnerEpoch(epoch) {
                                api.addNote(targetId, text, recordedAt(), digest)
                            }
                        }
                        Unit
                    },
                    onTutor = { targetId, note ->
                        val epoch = store.learnerMutationEpoch()
                        writes.submit("tutor:$targetId") {
                            val digest = withContext(Dispatchers.Default) {
                                V2PackIdentityIndex.warm(pack)
                                targetContentDigest(pack, targetId, "STEP")
                            } ?: error("unverified target")
                            withCapturedLearnerEpoch(epoch) {
                                api.addTutor(targetId, note, recordedAt(), digest)
                            }
                        }
                        Unit
                    },
                    onAttestBook = { unit ->
                        val epoch = store.learnerMutationEpoch()
                        writes.submit("book:$unit") {
                            withCapturedLearnerEpoch(epoch) { api.attestBook(unit, recordedAt()) }
                        }
                        Unit
                    },
                    onAdvance = { unit ->
                        val epoch = store.learnerMutationEpoch()
                        writes.submit("advance:$unit") {
                            val latest = api.learner()
                            withContext(Dispatchers.Default) {
                                V2PackIdentityIndex.warm(pack)
                                val candidate = pack.unit(unit) ?: error("missing unit")
                                if (!dev.russiancompanion.v2.curriculum.canOpenUnit(
                                        dev.russiancompanion.v2.curriculum.unitState(pack, candidate, latest)) ||
                                    !dev.russiancompanion.v2.curriculum.advancementDecision(pack, candidate, latest).allowed) {
                                    error("current advancement evidence is incomplete")
                                }
                            }
                            val nextUnit = (unit + 1).coerceAtMost(30)
                            val nextSessionId = pack.unit(nextUnit)?.sessions?.firstOrNull()?.id
                            val digest = withContext(Dispatchers.Default) {
                                V2PackIdentityIndex.warm(pack)
                                nextSessionId?.let { resumePositionDigest(pack, it, 0) }
                            } ?: error("unverified position")
                            withCapturedLearnerEpoch(epoch) {
                                api.advanceUnit(
                                    unit,
                                    recordedAt(),
                                    nextUnit,
                                    nextSessionId,
                                    digest,
                                    GOVERNED_IDENTITY_SCHEMA,
                                )
                            }
                        }
                        Unit
                    },
                    onResumeAt = { sessionId, index ->
                        val ticket = writes.nextPositionTicket()
                        val epoch = store.learnerMutationEpoch()
                        writes.submit("position:$sessionId:$index", singleFlight = false, quiet = true) {
                            val digest = withContext(Dispatchers.Default) {
                                V2PackIdentityIndex.warm(pack)
                                resumePositionDigest(pack, sessionId, index)
                            } ?: return@submit
                            val profile = api.profile()
                            store.awaitBeforeProfileSaveForTests()
                            if (!writes.positionStillCurrent(ticket) || !store.learnerMutationCurrent(epoch)) {
                                return@submit
                            }
                            if (profile.currentSessionId == sessionId &&
                                profile.currentStepIndex == index &&
                                profile.resumeContentDigest == digest &&
                                profile.resumeIdentitySchema == GOVERNED_IDENTITY_SCHEMA
                            ) {
                                return@submit
                            }
                            withCapturedLearnerEpoch(epoch) {
                                api.saveProfile(
                                    profile.copy(
                                        currentSessionId = sessionId,
                                        currentStepIndex = index,
                                        resumeContentDigest = digest,
                                        resumeIdentitySchema = GOVERNED_IDENTITY_SCHEMA,
                                    ),
                                )
                            }
                        }
                        Unit
                    },
                    onTransliteration = { enabled ->
                        val epoch = store.learnerMutationEpoch()
                        writes.submit("transliteration") {
                            withCapturedLearnerEpoch(epoch) {
                                val profile = api.profile()
                                api.saveProfile(profile.copy(transliterationEnabled = enabled))
                            }
                        }
                        Unit
                    },
                    backup = backup,
                    recordedAt = recordedAt,
                    writesBusy = writes.pending.isNotEmpty() || writes.hasActiveWork(),
                    onRestored = { writes.reloadAfterRestore() },
                )
            }
        }
    }
}

internal fun pendingAttemptItemIds(pending: Set<String>): Set<String> {
    return pending.mapNotNull { key ->
        if (!key.startsWith("attempt:")) return@mapNotNull null
        val parts = key.removePrefix("attempt:").split(":", limit = 3)
        parts.getOrNull(1)?.takeIf { it.isNotEmpty() }
    }.toSet()
}

private fun Context.findActivity(): ComponentActivity {
    var current = this
    while (current is ContextWrapper) {
        if (current is ComponentActivity) return current
        current = current.baseContext
    }
    error("V2App requires a ComponentActivity")
}
