package dev.russiancompanion.presentation.navigation

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import dev.russiancompanion.data.appearance.AppearanceStore
import dev.russiancompanion.domain.appearance.AppearancePreference
import dev.russiancompanion.domain.appearance.defaultAppearancePreference
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.navigation.compose.rememberNavController
import dev.russiancompanion.domain.navigation.LaunchDestination
import dev.russiancompanion.domain.navigation.NavigationGateway
import dev.russiancompanion.domain.navigation.NavigationSnapshot
import dev.russiancompanion.domain.navigation.NavigationSurface
import dev.russiancompanion.domain.navigation.launchDestination
import dev.russiancompanion.domain.navigation.optionalDrillsAvailable
import dev.russiancompanion.domain.plan.PlanUpdate
import dev.russiancompanion.domain.practice.PracticeAudio
import dev.russiancompanion.domain.progress.ExportUpdate
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime

internal enum class PlanActionKind {
    COMPLETE,
    INCLUDE_TUTOR,
    REMOVE_TUTOR,
}

internal data class FailedPlanAction(
    val kind: PlanActionKind,
    val planDefinitionId: String,
)

internal fun snapshotDispatcher(): CoroutineDispatcher {
    return if (isRobolectricRuntime()) {
        Dispatchers.Unconfined
    } else {
        Dispatchers.IO
    }
}

internal fun isRobolectricRuntime(): Boolean {
    return Build.FINGERPRINT.contains("robolectric", ignoreCase = true)
}

@Composable
internal fun CompanionApp(
    gateway: NavigationGateway?,
    onRetryOpen: () -> Unit,
    today: () -> String = { LocalDate.now().toString() },
    recordedAt: () -> String = { LocalDateTime.now().toString() },
    practiceAudio: PracticeAudio,
    chooseExportDestination: (() -> ExportSelection?)? = null,
    appearanceStore: AppearanceStore = AppearanceStore.memory(defaultAppearancePreference()),
    onAppearanceChange: (AppearancePreference) -> Unit = {},
) {
    val dispatcher = snapshotDispatcher()
    val scope = rememberCoroutineScope()
    val testRuntime = isRobolectricRuntime()
    val requestGuard = remember(gateway) { SnapshotRequestGuard() }
    var snapshot by remember(gateway) {
        mutableStateOf(
            when {
                gateway == null -> NavigationSnapshot.closed()
                testRuntime -> gateway.snapshot(today(), NavigationSurface.FULL)
                else -> NavigationSnapshot.pending()
            },
        )
    }
    var failedAction by remember(gateway) { mutableStateOf<FailedPlanAction?>(null) }
    var requestedSurface by remember(gateway) { mutableStateOf(NavigationSurface.SHELL) }
    var operationInProgress by remember(gateway) { mutableStateOf(false) }
    val work = remember(gateway, dispatcher, scope, testRuntime) {
        UiWorkCoordinator(
            dispatcher = dispatcher,
            scope = scope,
            runSynchronously = testRuntime,
            onBusyChange = { operationInProgress = it },
        )
    }
    fun loadSurface(surface: NavigationSurface): NavigationSnapshot {
        val requested = if (testRuntime) NavigationSurface.FULL else surface
        return gateway?.snapshot(today(), requested) ?: NavigationSnapshot.closed()
    }
    fun applySnapshot(loaded: NavigationSnapshot, then: ((NavigationSnapshot) -> Unit)? = null) {
        snapshot = loaded
        then?.invoke(loaded)
    }
    fun refresh(surface: NavigationSurface = requestedSurface) {
        requestedSurface = surface
        if (testRuntime) {
            applySnapshot(loadSurface(surface))
            return
        }
        val token = requestGuard.begin()
        scope.launch {
            val loaded = withContext(dispatcher) { loadSurface(surface) }
            if (requestGuard.isCurrent(token)) applySnapshot(loaded)
        }
    }
    fun requestSurface(surface: NavigationSurface, then: (NavigationSnapshot) -> Unit) {
        requestedSurface = surface
        if (testRuntime) {
            applySnapshot(loadSurface(surface), then)
            return
        }
        val token = requestGuard.begin()
        scope.launch {
            val loaded = withContext(dispatcher) { loadSurface(surface) }
            if (requestGuard.isCurrent(token)) applySnapshot(loaded, then)
        }
    }
    fun apply(update: PlanUpdate?, action: FailedPlanAction) {
        when (update) {
            PlanUpdate.Applied, PlanUpdate.Unchanged -> {
                failedAction = null
                refresh()
            }
            is PlanUpdate.NotSaved -> failedAction = action
            null -> failedAction = action
        }
    }
    fun runAction(action: FailedPlanAction) {
        work.submit(
            work = {
                when (action.kind) {
                    PlanActionKind.COMPLETE -> {
                        gateway?.completeExternalActivity(action.planDefinitionId, today(), recordedAt())
                    }
                    PlanActionKind.INCLUDE_TUTOR -> gateway?.includeTutorTask(action.planDefinitionId, today())
                    PlanActionKind.REMOVE_TUTOR -> gateway?.removeTutorTask(action.planDefinitionId, today())
                }
            },
            onFailure = { PlanUpdate.NotSaved(action.planDefinitionId) },
        ) { update -> apply(update, action) }
    }

    LaunchedEffect(gateway) {
        if (gateway == null) {
            snapshot = NavigationSnapshot.closed()
        } else if (!testRuntime) {
            val token = requestGuard.begin()
            val loaded = withContext(dispatcher) { loadSurface(NavigationSurface.SHELL) }
            if (requestGuard.isCurrent(token)) applySnapshot(loaded)
        }
    }

    val launch = launchDestination(snapshot)
    var appearance by remember(appearanceStore) { mutableStateOf(appearanceStore.current()) }
    val followSystemDark = isSystemInDarkTheme()
    val useDark = appearance == AppearancePreference.DARK ||
        (appearance == AppearancePreference.FOLLOW_SYSTEM && followSystemDark)
    val colors = if (useDark) darkColorScheme() else lightColorScheme()
    MaterialTheme(colorScheme = colors) {
        key(launch, optionalDrillsAvailable(snapshot)) {
            val navController = rememberNavController()
            CompanionNavHost(
                navController = navController,
                launch = launch,
                snapshot = snapshot,
                saveFailed = failedAction != null,
                operationInProgress = operationInProgress,
                onConfirmLesson = { segmentId ->
                    work.submit(
                        work = {
                            gateway?.confirmLesson(segmentId, today())
                            true
                        },
                        onFailure = { false },
                    ) { succeeded -> if (succeeded) refresh() }
                },
                onReloadSeed = {
                    work.submit(
                        work = {
                            gateway?.reloadPackagedSeed()
                            true
                        },
                        onFailure = { false },
                    ) { succeeded -> if (succeeded) refresh() }
                },
                onRetryOpen = onRetryOpen,
                onCompleteItem = { planId ->
                    runAction(FailedPlanAction(PlanActionKind.COMPLETE, planId))
                },
                onIncludeTutor = { planId ->
                    runAction(FailedPlanAction(PlanActionKind.INCLUDE_TUTOR, planId))
                },
                onRemoveTutor = { planId ->
                    runAction(FailedPlanAction(PlanActionKind.REMOVE_TUTOR, planId))
                },
                onRetryPlanAction = { failedAction?.let(::runAction) },
                practiceAudio = practiceAudio,
                onSaveRecording = { audioId, file, done ->
                    work.submit(
                        work = {
                            gateway?.saveRecording(audioId, file, recordedAt()) ?: PlanUpdate.NotSaved(audioId)
                        },
                        onFailure = { PlanUpdate.NotSaved(audioId) },
                    ) { update ->
                        if (update is PlanUpdate.Applied || update is PlanUpdate.Unchanged) refresh()
                        done(update)
                    }
                },
                onTutorReview = { recordingId, marked, done ->
                    work.submit(
                        work = {
                            gateway?.setRecordingTutorReview(recordingId, marked)
                                ?: PlanUpdate.NotSaved(recordingId)
                        },
                        onFailure = { PlanUpdate.NotSaved(recordingId) },
                    ) { update ->
                        if (update is PlanUpdate.Applied || update is PlanUpdate.Unchanged) refresh()
                        done(update)
                    }
                },
                onCompletePractice = { audioId, done ->
                    work.submit(
                        work = {
                            gateway?.completeListenPractice(audioId, today(), recordedAt())
                                ?: PlanUpdate.NotSaved(audioId)
                        },
                        onFailure = { PlanUpdate.NotSaved(audioId) },
                    ) { update ->
                        if (update is PlanUpdate.Applied || update is PlanUpdate.Unchanged) refresh()
                        done(update)
                    }
                },
                onSaveGateEvidence = { gateId, entries, done ->
                    work.submit(
                        work = {
                            gateway?.saveGateEvidence(gateId, entries, recordedAt())
                                ?: PlanUpdate.NotSaved(gateId)
                        },
                        onFailure = { PlanUpdate.NotSaved(gateId) },
                    ) { update ->
                        if (update is PlanUpdate.Applied || update is PlanUpdate.Unchanged) refresh()
                        done(update)
                    }
                },
                onAddTutorSignOff = { gateId, done ->
                    work.submit(
                        work = {
                            gateway?.addTutorSignOff(gateId, recordedAt()) ?: PlanUpdate.NotSaved(gateId)
                        },
                        onFailure = { PlanUpdate.NotSaved(gateId) },
                    ) { update ->
                        if (update is PlanUpdate.Applied || update is PlanUpdate.Unchanged) refresh()
                        done(update)
                    }
                },
                recordedAt = recordedAt,
                onSaveActivity = { category, minutes, completed, at, associate, done ->
                    work.submit(
                        work = {
                            gateway?.saveActivityLog(category, minutes, completed, at, associate)
                                ?: PlanUpdate.NotSaved("activity")
                        },
                        onFailure = { PlanUpdate.NotSaved("activity") },
                    ) { update ->
                        if (update is PlanUpdate.Applied || update is PlanUpdate.Unchanged) refresh()
                        done(update)
                    }
                },
                onExport = { stream, fileName, documentReference, done ->
                    work.submit(
                        work = {
                            gateway?.exportLocal(stream, recordedAt(), fileName, documentReference)
                                ?: ExportUpdate.DestinationUnavailable
                        },
                        onFailure = { ExportUpdate.NotConfirmed },
                        onResult = done,
                    )
                },
                chooseExportDestination = chooseExportDestination,
                onNeedSurface = { surface, then -> requestSurface(surface, then) },
                appearance = appearance,
                onAppearanceChange = { next ->
                    appearanceStore.set(next)
                    appearance = next
                    onAppearanceChange(next)
                },
            )
        }
    }
}

internal fun startRoute(launch: LaunchDestination): Any {
    return when (launch) {
        LaunchDestination.ONBOARDING -> OnboardingRoute
        LaunchDestination.HOME -> HomeRoute
        LaunchDestination.LOCAL_DATA_PROBLEM -> LocalDataRoute
    }
}
