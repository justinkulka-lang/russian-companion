package dev.russiancompanion.presentation.navigation

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import dev.russiancompanion.R
import dev.russiancompanion.domain.navigation.LaunchDestination
import dev.russiancompanion.domain.navigation.NavigationSnapshot
import dev.russiancompanion.domain.navigation.NavigationSurface
import dev.russiancompanion.domain.navigation.optionalDrillsAvailable
import dev.russiancompanion.domain.navigation.practiceLibraryAvailable
import dev.russiancompanion.domain.appearance.AppearancePreference
import dev.russiancompanion.domain.navigation.ContinueDestination
import dev.russiancompanion.domain.navigation.ContinueNavigation
import dev.russiancompanion.domain.navigation.continueNavigation
import dev.russiancompanion.domain.navigation.resolveContinueDestination
import dev.russiancompanion.domain.plan.ChecklistActivity
import dev.russiancompanion.domain.plan.ChecklistItem
import dev.russiancompanion.domain.gate.GateEvidenceEntry
import dev.russiancompanion.domain.plan.PlanUpdate
import dev.russiancompanion.domain.plan.RecordedCategory
import dev.russiancompanion.domain.pathway.firstPrescribedPlayable
import dev.russiancompanion.domain.practice.PracticeAudio
import dev.russiancompanion.domain.practice.nextAdmittedModelId
import dev.russiancompanion.domain.practice.previousAdmittedModelId
import dev.russiancompanion.domain.progress.ExportUpdate
import java.io.OutputStream

@Composable
internal fun CompanionNavHost(
    navController: NavHostController,
    launch: LaunchDestination,
    snapshot: NavigationSnapshot,
    saveFailed: Boolean,
    operationInProgress: Boolean = false,
    lessonConfirmFailed: Boolean = false,
    onConfirmLesson: (String) -> Unit,
    onReloadSeed: () -> Unit,
    onRetryOpen: () -> Unit,
    onCompleteItem: (String) -> Unit,
    onIncludeTutor: (String) -> Unit,
    onRemoveTutor: (String) -> Unit,
    onRetryPlanAction: () -> Unit,
    practiceAudio: PracticeAudio,
    onSaveRecording: (String, String, (PlanUpdate) -> Unit) -> Unit,
    onTutorReview: (String, Boolean, (PlanUpdate) -> Unit) -> Unit,
    onCompletePractice: (String, (PlanUpdate) -> Unit) -> Unit,
    onSaveGateEvidence: (String, List<GateEvidenceEntry>, (PlanUpdate) -> Unit) -> Unit,
    onAddTutorSignOff: (String, (PlanUpdate) -> Unit) -> Unit,
    recordedAt: () -> String,
    onSaveActivity: (RecordedCategory, Int, Boolean, String, Boolean, (PlanUpdate) -> Unit) -> Unit,
    onExport: (OutputStream, String?, String?, (ExportUpdate) -> Unit) -> Unit,
    chooseExportDestination: (() -> ExportSelection?)? = null,
    onNeedSurface: (NavigationSurface, (NavigationSnapshot) -> Unit) -> Unit = { _, then -> then(snapshot) },
    appearance: AppearancePreference = AppearancePreference.DARK,
    appearanceSaveFailed: Boolean = false,
    onAppearanceChange: (AppearancePreference) -> Unit = {},
) {
    val onHome: (() -> Unit)? = if (launch == LaunchDestination.HOME) {
        { goHome(navController) }
    } else {
        null
    }
    NavHost(navController = navController, startDestination = startRoute(launch)) {
        composable<OnboardingRoute> {
            OnboardingScreen(
                snapshot = snapshot,
                onAttributions = {
                    onNeedSurface(NavigationSurface.ATTRIBUTIONS) {
                        navController.navigate(AttributionsRoute())
                    }
                },
                onConfirmLesson = onConfirmLesson,
                onReloadSeed = onReloadSeed,
                operationInProgress = operationInProgress,
                confirmFailed = lessonConfirmFailed,
            )
        }
        composable<LocalDataRoute> {
            LocalDataScreen(
                onRetry = onRetryOpen,
                onAttributions = {
                    onNeedSurface(NavigationSurface.ATTRIBUTIONS) {
                        navController.navigate(AttributionsRoute())
                    }
                },
            )
        }
        composable<HomeRoute> {
            HomeScreen(
                snapshot = snapshot,
                appearance = appearance,
                appearanceSaveFailed = appearanceSaveFailed,
                onAppearanceChange = onAppearanceChange,
                onContinue = {
                    openContinueDestination(navController, snapshot, onNeedSurface)
                },
                onBegin = { item ->
                    if (item.activity == ChecklistActivity.LISTEN_RECORD_COMPARE) {
                        onNeedSurface(NavigationSurface.PRACTICE) { loaded ->
                            beginPlanItem(navController, loaded, item)
                        }
                    } else {
                        beginPlanItem(navController, snapshot, item)
                    }
                },
                onTodaysPlan = { navController.navigate(TodaysPlanRoute()) },
                onPractice = {
                    onNeedSurface(NavigationSurface.PRACTICE) {
                        navController.navigate(PracticeLibraryRoute)
                    }
                },
                onGates = { navController.navigate(GatesRoute) },
                onProgress = { navController.navigate(ProgressRoute) },
                onAttributions = {
                    onNeedSurface(NavigationSurface.ATTRIBUTIONS) {
                        navController.navigate(AttributionsRoute())
                    }
                },
                onOptionalDrills = { navController.navigate(OptionalDrillsRoute) },
                onSelectLesson = { navController.navigate(OnboardingRoute) },
            )
        }
        composable<TodaysPlanRoute> { entry ->
            val route = entry.toRoute<TodaysPlanRoute>()
            TodaysPlanScreen(
                snapshot = snapshot,
                saveFailed = saveFailed,
                operationInProgress = operationInProgress,
                focusPlanDefinitionId = route.focusPlanDefinitionId,
                onHome = onHome,
                onPractice = {
                    onNeedSurface(NavigationSurface.PRACTICE) {
                        navController.navigate(PracticeLibraryRoute)
                    }
                },
                onStartPractice = { item ->
                    onNeedSurface(NavigationSurface.PRACTICE) { loaded ->
                        openPrescribedPractice(navController, loaded, item.audioModelIds)
                    }
                },
                onProgress = { navController.navigate(ProgressRoute) },
                onSelectLesson = { navController.navigate(OnboardingRoute) },
                onComplete = onCompleteItem,
                onIncludeTutor = onIncludeTutor,
                onRemoveTutor = onRemoveTutor,
                onRetrySave = onRetryPlanAction,
                onBack = { navController.popBackStack() },
            )
        }
        composable<PracticeLibraryRoute> {
            PracticeLibraryBody(
                snapshot = snapshot,
                onOpen = { audioId ->
                    if (snapshot.audio.firstOrNull { it.id == audioId }?.playable == true) {
                        navController.navigate(PracticeSessionRoute(audioId))
                    }
                },
                onInspectAttribution = { audioId ->
                    onNeedSurface(NavigationSurface.ATTRIBUTIONS) {
                        navController.navigate(AttributionsRoute(audioId))
                    }
                },
                onHome = onHome,
                onTodaysPlan = { navController.navigate(TodaysPlanRoute()) },
                onAttributions = {
                    onNeedSurface(NavigationSurface.ATTRIBUTIONS) {
                        navController.navigate(AttributionsRoute())
                    }
                },
                onBack = { navController.popBackStack() },
            )
        }
        composable<PracticeSessionRoute> { entry ->
            val route = entry.toRoute<PracticeSessionRoute>()
            PracticeSessionScreen(
                snapshot = snapshot,
                audioModelId = route.audioModelId,
                practiceAudio = practiceAudio,
                operationInProgress = operationInProgress,
                onSaveRecording = onSaveRecording,
                onTutorReview = onTutorReview,
                onCompletePractice = onCompletePractice,
                onPrevious = { audioId ->
                    navController.navigate(PracticeSessionRoute(audioId)) {
                        popUpTo<PracticeLibraryRoute>()
                    }
                },
                onNext = { audioId ->
                    navController.navigate(PracticeSessionRoute(audioId)) {
                        popUpTo<PracticeLibraryRoute>()
                    }
                },
                onLibrary = { navController.popBackStack() },
                onTodaysPlan = { navController.navigate(TodaysPlanRoute()) },
                onAttributions = {
                    onNeedSurface(NavigationSurface.ATTRIBUTIONS) {
                        navController.navigate(AttributionsRoute(route.audioModelId))
                    }
                },
                onHome = onHome,
            )
        }
        composable<GatesRoute> {
            GatesScreen(
                snapshot = snapshot,
                onOpen = { gateId -> navController.navigate(GateDetailRoute(gateId)) },
                onHome = onHome,
                onProgress = { navController.navigate(ProgressRoute) },
                onBack = { navController.popBackStack() },
            )
        }
        composable<GateDetailRoute> { entry ->
            val route = entry.toRoute<GateDetailRoute>()
            GateDetailScreen(
                snapshot = snapshot,
                gateId = route.gateId,
                operationInProgress = operationInProgress,
                onSaveGateEvidence = { entries, done -> onSaveGateEvidence(route.gateId, entries, done) },
                onAddTutorSignOff = { done -> onAddTutorSignOff(route.gateId, done) },
                onGates = { navController.popBackStack() },
                onTodaysPlan = { navController.navigate(TodaysPlanRoute()) },
                onProgress = { navController.navigate(ProgressRoute) },
                onHome = onHome,
            )
        }
        composable<ProgressRoute> {
            ProgressScreen(
                snapshot = snapshot,
                onLogActivity = { navController.navigate(LogActivityRoute(fromPlan = false)) },
                onExport = { navController.navigate(LocalExportRoute) },
                onHome = onHome,
                onGates = { navController.navigate(GatesRoute) },
                onBack = { navController.popBackStack() },
            )
        }
        composable<LogActivityRoute> { entry ->
            val route = entry.toRoute<LogActivityRoute>()
            LogActivityScreen(
                snapshot = snapshot,
                fromPlan = route.fromPlan,
                recordedAt = recordedAt,
                operationInProgress = operationInProgress,
                onSave = onSaveActivity,
                onProgress = { navController.popBackStack() },
                onTodaysPlan = { navController.navigate(TodaysPlanRoute()) },
                onHome = onHome,
            )
        }
        composable<LocalExportRoute> {
            LocalExportScreen(
                operationInProgress = operationInProgress,
                onExport = onExport,
                chooseExportDestination = chooseExportDestination,
                onProgress = { navController.popBackStack() },
                onHome = onHome,
            )
        }
        composable<AttributionsRoute> { entry ->
            val route = entry.toRoute<AttributionsRoute>()
            AttributionInventoryScreen(
                snapshot = snapshot,
                practiceAudioId = route.practiceAudioId,
                onHome = onHome,
                onBack = { navController.popBackStack() },
            )
        }
        if (optionalDrillsAvailable(snapshot)) {
            composable<OptionalDrillsRoute> {
                OptionalDrillsScreen(
                    onHome = onHome,
                    onAttributions = {
                        onNeedSurface(NavigationSurface.ATTRIBUTIONS) {
                            navController.navigate(AttributionsRoute())
                        }
                    },
                    onBack = { navController.popBackStack() },
                )
            }
        }
    }
}

internal fun continueRoute(destination: ContinueDestination): Any? {
    return when (val navigation = continueNavigation(destination)) {
        ContinueNavigation.SelectLesson -> OnboardingRoute
        is ContinueNavigation.PlanItem -> TodaysPlanRoute(navigation.planDefinitionId)
        is ContinueNavigation.ListenRecordCompare -> PracticeSessionRoute(navigation.audioModelId)
        is ContinueNavigation.GateEvidence -> GateDetailRoute(navigation.gateId)
        ContinueNavigation.Progress -> ProgressRoute
        ContinueNavigation.Unavailable -> null
    }
}

private fun openContinueDestination(
    navController: NavHostController,
    snapshot: NavigationSnapshot,
    onNeedSurface: (NavigationSurface, (NavigationSnapshot) -> Unit) -> Unit,
) {
    val destination = resolveContinueDestination(snapshot)
    val route = continueRoute(destination) ?: return
    if (destination is ContinueDestination.ListenRecordCompare) {
        onNeedSurface(NavigationSurface.PRACTICE) {
            navController.navigate(route)
        }
    } else {
        navController.navigate(route)
    }
}

private fun beginPlanItem(
    navController: NavHostController,
    snapshot: NavigationSnapshot,
    item: ChecklistItem,
) {
    if (item.activity == ChecklistActivity.LISTEN_RECORD_COMPARE) {
        openPrescribedPractice(navController, snapshot, item.audioModelIds)
    } else {
        navController.navigate(TodaysPlanRoute(item.planDefinitionId))
    }
}

private fun openPrescribedPractice(
    navController: NavHostController,
    snapshot: NavigationSnapshot,
    prescribedAudioModelIds: List<String>,
) {
    val playableIds = snapshot.audio.filter { it.playable }.map { it.id }.toSet()
    val first = firstPrescribedPlayable(prescribedAudioModelIds, playableIds) ?: return
    navController.navigate(PracticeSessionRoute(first))
}

private fun openPractice(navController: NavHostController, snapshot: NavigationSnapshot) {
    val audio = snapshot.audio.filter { it.playable }
    if (audio.size == 1) {
        navController.navigate(PracticeSessionRoute(audio.single().id))
    } else if (audio.isNotEmpty()) {
        navController.navigate(PracticeLibraryRoute)
    }
}

private fun goHome(navController: NavHostController) {
    navController.navigate(HomeRoute) {
        popUpTo<HomeRoute> { inclusive = true }
        launchSingleTop = true
    }
}

@Composable
private fun LocalDataScreen(onRetry: () -> Unit, onAttributions: () -> Unit) {
    ShellPage(title = stringResource(R.string.local_data_title)) {
        Text(stringResource(R.string.local_data_message), color = MaterialTheme.colorScheme.onBackground)
        PrimaryAction(text = stringResource(R.string.navigate_retry), onClick = onRetry)
        SecondaryAction(text = stringResource(R.string.open_attributions), onClick = onAttributions)
    }
}

@Composable
private fun HomeScreen(
    snapshot: NavigationSnapshot,
    appearance: AppearancePreference,
    appearanceSaveFailed: Boolean,
    onAppearanceChange: (AppearancePreference) -> Unit,
    onContinue: () -> Unit,
    onBegin: (ChecklistItem) -> Unit,
    onTodaysPlan: () -> Unit,
    onPractice: () -> Unit,
    onGates: () -> Unit,
    onProgress: () -> Unit,
    onAttributions: () -> Unit,
    onOptionalDrills: () -> Unit,
    onSelectLesson: () -> Unit,
) {
    val libraryAvailable = practiceLibraryAvailable(snapshot)
    ShellPage(title = stringResource(R.string.home_title)) {
        if (!snapshot.progress.readable) {
            Text(
                stringResource(R.string.local_data_message),
                color = MaterialTheme.colorScheme.error,
            )
        }
        HomePlanSection(
            snapshot = snapshot,
            appearance = appearance,
            appearanceSaveFailed = appearanceSaveFailed,
            onAppearanceChange = onAppearanceChange,
            onContinue = onContinue,
            onBegin = onBegin,
            onTodaysPlan = onTodaysPlan,
            onSelectLesson = onSelectLesson,
        )
        PrimaryAction(text = stringResource(R.string.open_todays_plan), onClick = onTodaysPlan)
        SecondaryAction(text = stringResource(R.string.open_practice), onClick = onPractice, enabled = libraryAvailable)
        if (!libraryAvailable) {
            Text(stringResource(R.string.practice_none), color = MaterialTheme.colorScheme.onBackground)
        }
        SecondaryAction(text = stringResource(R.string.open_gates), onClick = onGates)
        SecondaryAction(text = stringResource(R.string.open_progress), onClick = onProgress)
        SecondaryAction(text = stringResource(R.string.open_attributions), onClick = onAttributions)
        if (optionalDrillsAvailable(snapshot)) {
            SecondaryAction(text = stringResource(R.string.open_optional_drills), onClick = onOptionalDrills)
        }
        Text(
            stringResource(
                R.string.home_build_identity,
                dev.russiancompanion.BuildConfig.VERSION_NAME,
                shortCommit(dev.russiancompanion.BuildConfig.GIT_COMMIT),
            ),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun TodaysPlanScreen(
    snapshot: NavigationSnapshot,
    saveFailed: Boolean,
    operationInProgress: Boolean = false,
    focusPlanDefinitionId: String? = null,
    onHome: (() -> Unit)?,
    onPractice: () -> Unit,
    onStartPractice: (ChecklistItem) -> Unit,
    onProgress: () -> Unit,
    onSelectLesson: () -> Unit,
    onComplete: (String) -> Unit,
    onIncludeTutor: (String) -> Unit,
    onRemoveTutor: (String) -> Unit,
    onRetrySave: () -> Unit,
    onBack: () -> Unit,
) {
    val libraryAvailable = practiceLibraryAvailable(snapshot)
    ShellPage(title = stringResource(R.string.todays_plan_title), onBack = onBack, onHome = onHome, busy = operationInProgress) {
        TodaysPlanBody(
            snapshot = snapshot,
            saveFailed = saveFailed,
            focusPlanDefinitionId = focusPlanDefinitionId,
            onSelectLesson = onSelectLesson,
            onStartPractice = onStartPractice,
            onComplete = onComplete,
            onIncludeTutor = onIncludeTutor,
            onRemoveTutor = onRemoveTutor,
            onRetrySave = onRetrySave,
            actionsEnabled = !operationInProgress,
        )
        SecondaryAction(
            text = stringResource(R.string.open_practice),
            onClick = onPractice,
            enabled = libraryAvailable && !operationInProgress,
        )
        SecondaryAction(
            text = stringResource(R.string.open_progress),
            onClick = onProgress,
            enabled = !operationInProgress,
        )
    }
}

@Composable
private fun PracticeSessionScreen(
    snapshot: NavigationSnapshot,
    audioModelId: String,
    practiceAudio: PracticeAudio,
    operationInProgress: Boolean = false,
    onSaveRecording: (String, String, (PlanUpdate) -> Unit) -> Unit,
    onTutorReview: (String, Boolean, (PlanUpdate) -> Unit) -> Unit,
    onCompletePractice: (String, (PlanUpdate) -> Unit) -> Unit,
    onPrevious: (String) -> Unit,
    onNext: (String) -> Unit,
    onLibrary: () -> Unit,
    onTodaysPlan: () -> Unit,
    onAttributions: () -> Unit,
    onHome: (() -> Unit)?,
) {
    val admittedIds = snapshot.audio.map { it.id }
    val previousId = previousAdmittedModelId(admittedIds, audioModelId)
    val nextId = nextAdmittedModelId(admittedIds, audioModelId)
    ShellPage(title = stringResource(R.string.practice_session_title), onBack = onLibrary, onHome = onHome, busy = operationInProgress) {
        PracticeSessionBody(
            snapshot = snapshot,
            audioModelId = audioModelId,
            practiceAudio = practiceAudio,
            operationInProgress = operationInProgress,
            onSaveRecording = onSaveRecording,
            onTutorReview = onTutorReview,
            onComplete = onCompletePractice,
        )
        if (previousId != null) {
            SecondaryAction(
                text = stringResource(R.string.practice_previous),
                onClick = { onPrevious(previousId) },
                enabled = !operationInProgress,
            )
        }
        if (nextId != null) {
            SecondaryAction(
                text = stringResource(R.string.practice_next),
                onClick = { onNext(nextId) },
                enabled = !operationInProgress,
            )
        }
        SecondaryAction(
            text = stringResource(R.string.open_todays_plan_from_practice),
            onClick = onTodaysPlan,
        )
        SecondaryAction(
            text = stringResource(R.string.open_attributions),
            onClick = onAttributions,
        )
    }
}

@Composable
private fun GatesScreen(
    snapshot: NavigationSnapshot,
    onOpen: (String) -> Unit,
    onHome: (() -> Unit)?,
    onProgress: () -> Unit,
    onBack: () -> Unit,
) {
    ShellPage(title = stringResource(R.string.gates_title), onBack = onBack, onHome = onHome) {
        Text(
            stringResource(R.string.gates_not_proficiency),
            color = MaterialTheme.colorScheme.onBackground,
        )
        GateListBody(details = snapshot.gateDetails, onOpen = onOpen)
        SecondaryAction(text = stringResource(R.string.open_progress), onClick = onProgress)
    }
}

@Composable
private fun GateDetailScreen(
    snapshot: NavigationSnapshot,
    gateId: String,
    operationInProgress: Boolean = false,
    onSaveGateEvidence: (List<GateEvidenceEntry>, (PlanUpdate) -> Unit) -> Unit,
    onAddTutorSignOff: ((PlanUpdate) -> Unit) -> Unit,
    onGates: () -> Unit,
    onTodaysPlan: () -> Unit,
    onProgress: () -> Unit,
    onHome: (() -> Unit)?,
) {
    val detail = snapshot.gateDetails.firstOrNull { it.id == gateId }
    ShellPage(title = stringResource(R.string.gate_detail_title), onBack = onGates, onHome = onHome, busy = operationInProgress) {
        Text(
            stringResource(R.string.gates_not_proficiency),
            color = MaterialTheme.colorScheme.onBackground,
        )
        if (detail == null) {
            Text(
                stringResource(R.string.gate_unreadable),
                color = MaterialTheme.colorScheme.error,
            )
        } else {
            GateDetailBody(
                detail = detail,
                actionsEnabled = !operationInProgress,
                onSave = onSaveGateEvidence,
                onAddTutor = onAddTutorSignOff,
            )
        }
        SecondaryAction(text = stringResource(R.string.open_todays_plan), onClick = onTodaysPlan)
        SecondaryAction(text = stringResource(R.string.open_progress), onClick = onProgress)
    }
}

@Composable
private fun OptionalDrillsScreen(
    onHome: (() -> Unit)?,
    onAttributions: () -> Unit,
    onBack: () -> Unit,
) {
    ShellPage(title = stringResource(R.string.optional_drills_title), onBack = onBack, onHome = onHome) {
        Text(
            stringResource(R.string.optional_drills_not_evidence),
            color = MaterialTheme.colorScheme.onBackground,
        )
        SecondaryAction(text = stringResource(R.string.open_attributions), onClick = onAttributions)
    }
}

private fun shortCommit(commit: String): String {
    return if (commit.length >= 7) commit.take(7) else commit
}
