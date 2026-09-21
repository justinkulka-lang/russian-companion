package dev.russiancompanion.presentation.navigation

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import dev.russiancompanion.domain.navigation.OnboardingConfirmation
import dev.russiancompanion.domain.navigation.onboardingConfirmation
import dev.russiancompanion.domain.navigation.optionalDrillsAvailable
import dev.russiancompanion.domain.navigation.practiceLibraryAvailable
import dev.russiancompanion.domain.plan.ChecklistActivity
import dev.russiancompanion.domain.plan.ChecklistItem
import dev.russiancompanion.domain.plan.PlanUpdate
import dev.russiancompanion.domain.practice.PracticeAudio

@Composable
internal fun CompanionNavHost(
    navController: NavHostController,
    launch: LaunchDestination,
    snapshot: NavigationSnapshot,
    saveFailed: Boolean,
    onConfirmLesson: (String) -> Unit,
    onReloadSeed: () -> Unit,
    onRetryOpen: () -> Unit,
    onCompleteItem: (String) -> Unit,
    onIncludeTutor: (String) -> Unit,
    onRemoveTutor: (String) -> Unit,
    onRetryPlanAction: () -> Unit,
    practiceAudio: PracticeAudio,
    onSaveRecording: (String, String) -> PlanUpdate,
    onTutorReview: (String, Boolean) -> PlanUpdate,
    onCompletePractice: (String) -> PlanUpdate,
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
                onAttributions = { navController.navigate(AttributionsRoute()) },
                onConfirmLesson = onConfirmLesson,
                onReloadSeed = onReloadSeed,
            )
        }
        composable<LocalDataRoute> {
            LocalDataScreen(
                onRetry = onRetryOpen,
                onAttributions = { navController.navigate(AttributionsRoute()) },
            )
        }
        composable<HomeRoute> {
            HomeScreen(
                snapshot = snapshot,
                onBegin = { item -> beginPlanItem(navController, snapshot, item) },
                onTodaysPlan = { navController.navigate(TodaysPlanRoute) },
                onPractice = { navController.navigate(PracticeLibraryRoute) },
                onGates = { navController.navigate(GatesRoute) },
                onProgress = { navController.navigate(ProgressRoute) },
                onAttributions = { navController.navigate(AttributionsRoute()) },
                onOptionalDrills = { navController.navigate(OptionalDrillsRoute) },
                onSelectLesson = { navController.navigate(OnboardingRoute) },
            )
        }
        composable<TodaysPlanRoute> {
            TodaysPlanScreen(
                snapshot = snapshot,
                saveFailed = saveFailed,
                onHome = onHome,
                onPractice = { navController.navigate(PracticeLibraryRoute) },
                onStartPractice = { openPractice(navController, snapshot) },
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
            PracticeLibraryScreen(
                snapshot = snapshot,
                onOpen = { audioId ->
                    if (snapshot.audio.firstOrNull { it.id == audioId }?.playable == true) {
                        navController.navigate(PracticeSessionRoute(audioId))
                    }
                },
                onHome = onHome,
                onTodaysPlan = { navController.navigate(TodaysPlanRoute) },
                onAttributions = { navController.navigate(AttributionsRoute()) },
                onBack = { navController.popBackStack() },
            )
        }
        composable<PracticeSessionRoute> { entry ->
            val route = entry.toRoute<PracticeSessionRoute>()
            PracticeSessionScreen(
                snapshot = snapshot,
                audioModelId = route.audioModelId,
                practiceAudio = practiceAudio,
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
                onTodaysPlan = { navController.navigate(TodaysPlanRoute) },
                onAttributions = { navController.navigate(AttributionsRoute(route.audioModelId)) },
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
                onGates = { navController.popBackStack() },
                onTodaysPlan = { navController.navigate(TodaysPlanRoute) },
                onProgress = { navController.navigate(ProgressRoute) },
                onHome = onHome,
            )
        }
        composable<ProgressRoute> {
            ProgressScreen(
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
                fromPlan = route.fromPlan,
                onProgress = { navController.popBackStack() },
                onTodaysPlan = { navController.navigate(TodaysPlanRoute) },
                onHome = onHome,
            )
        }
        composable<LocalExportRoute> {
            LocalExportScreen(
                onProgress = { navController.popBackStack() },
                onHome = onHome,
            )
        }
        composable<AttributionsRoute> { entry ->
            val route = entry.toRoute<AttributionsRoute>()
            AttributionsScreen(
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
                    onAttributions = { navController.navigate(AttributionsRoute()) },
                    onBack = { navController.popBackStack() },
                )
            }
        }
    }
}

private fun beginPlanItem(
    navController: NavHostController,
    snapshot: NavigationSnapshot,
    item: ChecklistItem,
) {
    if (item.activity == ChecklistActivity.LISTEN_RECORD_COMPARE) {
        openPractice(navController, snapshot)
    } else {
        navController.navigate(TodaysPlanRoute)
    }
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
private fun OnboardingScreen(
    snapshot: NavigationSnapshot,
    onAttributions: () -> Unit,
    onConfirmLesson: (String) -> Unit,
    onReloadSeed: () -> Unit,
) {
    var page by rememberSaveable { mutableIntStateOf(0) }
    var selectedLessonId by rememberSaveable { mutableStateOf<String?>(null) }
    val confirmation = onboardingConfirmation(snapshot, selectedLessonId)
    val plansUnavailable = confirmation == OnboardingConfirmation.PLAN_DEFINITIONS_UNAVAILABLE ||
        confirmation == OnboardingConfirmation.CATALOG_UNAVAILABLE ||
        snapshot.lessons.none { it.planCount > 0 }
    ShellPage(
        title = stringResource(R.string.onboarding_title),
        onBack = if (page == 0) null else ({ page -= 1 }),
    ) {
        when (page) {
            0 -> Text(stringResource(R.string.onboarding_role))
            1 -> Text(stringResource(R.string.onboarding_limits))
            2 -> Text(stringResource(R.string.onboarding_local_data))
            else -> {
                if (selectedLessonId == null || snapshot.lessons.isEmpty()) {
                    Text(stringResource(R.string.onboarding_lesson_required))
                }
                if (plansUnavailable) {
                    Text(stringResource(R.string.onboarding_plans_unavailable))
                    TextButton(onClick = onReloadSeed) { Text(stringResource(R.string.navigate_retry)) }
                }
                snapshot.lessons.forEach { lesson ->
                    TextButton(onClick = { selectedLessonId = lesson.id }) {
                        Text(lesson.lessonReference)
                    }
                }
                TextButton(
                    onClick = { selectedLessonId?.let(onConfirmLesson) },
                    enabled = confirmation == OnboardingConfirmation.READY,
                ) {
                    Text(stringResource(R.string.onboarding_confirm))
                }
            }
        }
        if (page < 3) {
            TextButton(onClick = { page += 1 }) { Text(stringResource(R.string.navigate_next)) }
        }
        TextButton(onClick = onAttributions) { Text(stringResource(R.string.open_attributions)) }
    }
}

@Composable
private fun LocalDataScreen(onRetry: () -> Unit, onAttributions: () -> Unit) {
    ShellPage(title = stringResource(R.string.local_data_title)) {
        Text(stringResource(R.string.local_data_message))
        TextButton(onClick = onRetry) { Text(stringResource(R.string.navigate_retry)) }
        TextButton(onClick = onAttributions) { Text(stringResource(R.string.open_attributions)) }
    }
}

@Composable
private fun HomeScreen(
    snapshot: NavigationSnapshot,
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
        HomePlanSection(
            snapshot = snapshot,
            onBegin = onBegin,
            onTodaysPlan = onTodaysPlan,
            onSelectLesson = onSelectLesson,
        )
        TextButton(onClick = onPractice, enabled = libraryAvailable) {
            Text(stringResource(R.string.open_practice))
        }
        if (!libraryAvailable) {
            Text(stringResource(R.string.practice_none))
        }
        TextButton(onClick = onGates) { Text(stringResource(R.string.open_gates)) }
        TextButton(onClick = onProgress) { Text(stringResource(R.string.open_progress)) }
        TextButton(onClick = onAttributions) { Text(stringResource(R.string.open_attributions)) }
        if (optionalDrillsAvailable(snapshot)) {
            TextButton(onClick = onOptionalDrills) { Text(stringResource(R.string.open_optional_drills)) }
        }
    }
}

@Composable
private fun TodaysPlanScreen(
    snapshot: NavigationSnapshot,
    saveFailed: Boolean,
    onHome: (() -> Unit)?,
    onPractice: () -> Unit,
    onStartPractice: () -> Unit,
    onProgress: () -> Unit,
    onSelectLesson: () -> Unit,
    onComplete: (String) -> Unit,
    onIncludeTutor: (String) -> Unit,
    onRemoveTutor: (String) -> Unit,
    onRetrySave: () -> Unit,
    onBack: () -> Unit,
) {
    val libraryAvailable = practiceLibraryAvailable(snapshot)
    ShellPage(title = stringResource(R.string.todays_plan_title), onBack = onBack, onHome = onHome) {
        TodaysPlanBody(
            snapshot = snapshot,
            saveFailed = saveFailed,
            onSelectLesson = onSelectLesson,
            onStartPractice = onStartPractice,
            onComplete = onComplete,
            onIncludeTutor = onIncludeTutor,
            onRemoveTutor = onRemoveTutor,
            onRetrySave = onRetrySave,
        )
        TextButton(onClick = onPractice, enabled = libraryAvailable) {
            Text(stringResource(R.string.open_practice))
        }
        TextButton(onClick = onProgress) { Text(stringResource(R.string.open_progress)) }
    }
}

@Composable
private fun PracticeLibraryScreen(
    snapshot: NavigationSnapshot,
    onOpen: (String) -> Unit,
    onHome: (() -> Unit)?,
    onTodaysPlan: () -> Unit,
    onAttributions: () -> Unit,
    onBack: () -> Unit,
) {
    ShellPage(title = stringResource(R.string.practice_title), onBack = onBack, onHome = onHome) {
        PracticeLibraryBody(snapshot = snapshot, onOpen = onOpen)
        TextButton(onClick = onTodaysPlan) { Text(stringResource(R.string.open_todays_plan_from_practice)) }
        TextButton(onClick = onAttributions) { Text(stringResource(R.string.open_attributions)) }
    }
}

@Composable
private fun PracticeSessionScreen(
    snapshot: NavigationSnapshot,
    audioModelId: String,
    practiceAudio: PracticeAudio,
    onSaveRecording: (String, String) -> PlanUpdate,
    onTutorReview: (String, Boolean) -> PlanUpdate,
    onCompletePractice: (String) -> PlanUpdate,
    onPrevious: (String) -> Unit,
    onNext: (String) -> Unit,
    onLibrary: () -> Unit,
    onTodaysPlan: () -> Unit,
    onAttributions: () -> Unit,
    onHome: (() -> Unit)?,
) {
    val index = snapshot.audio.indexOfFirst { it.id == audioModelId }
    ShellPage(title = stringResource(R.string.practice_session_title), onBack = onLibrary, onHome = onHome) {
        PracticeSessionBody(
            snapshot = snapshot,
            audioModelId = audioModelId,
            practiceAudio = practiceAudio,
            onSaveRecording = onSaveRecording,
            onTutorReview = onTutorReview,
            onComplete = onCompletePractice,
        )
        if (index > 0) {
            TextButton(onClick = { onPrevious(snapshot.audio[index - 1].id) }) {
                Text(stringResource(R.string.practice_previous))
            }
        }
        if (index >= 0 && index < snapshot.audio.lastIndex) {
            TextButton(onClick = { onNext(snapshot.audio[index + 1].id) }) {
                Text(stringResource(R.string.practice_next))
            }
        }
        TextButton(onClick = onTodaysPlan) { Text(stringResource(R.string.open_todays_plan_from_practice)) }
        TextButton(onClick = onAttributions) { Text(stringResource(R.string.open_attributions)) }
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
        Text(stringResource(R.string.gates_not_proficiency))
        if (snapshot.gates.isEmpty()) {
            Text(stringResource(R.string.gates_none))
        }
        snapshot.gates.forEach { gate ->
            TextButton(onClick = { onOpen(gate.id) }) { Text(gate.displayedRuleText) }
        }
        TextButton(onClick = onProgress) { Text(stringResource(R.string.open_progress)) }
    }
}

@Composable
private fun GateDetailScreen(
    snapshot: NavigationSnapshot,
    gateId: String,
    onGates: () -> Unit,
    onTodaysPlan: () -> Unit,
    onProgress: () -> Unit,
    onHome: (() -> Unit)?,
) {
    val gate = snapshot.gates.firstOrNull { it.id == gateId }
    ShellPage(title = stringResource(R.string.gate_detail_title), onBack = onGates, onHome = onHome) {
        Text(stringResource(R.string.gates_not_proficiency))
        if (gate == null) {
            Text(stringResource(R.string.gate_unreadable))
        } else {
            Text(gate.displayedRuleText)
        }
        TextButton(onClick = onTodaysPlan) { Text(stringResource(R.string.open_todays_plan)) }
        TextButton(onClick = onProgress) { Text(stringResource(R.string.open_progress)) }
    }
}

@Composable
private fun ProgressScreen(
    onLogActivity: () -> Unit,
    onExport: () -> Unit,
    onHome: (() -> Unit)?,
    onGates: () -> Unit,
    onBack: () -> Unit,
) {
    ShellPage(title = stringResource(R.string.progress_title), onBack = onBack, onHome = onHome) {
        Text(stringResource(R.string.progress_activity_only))
        TextButton(onClick = onLogActivity) { Text(stringResource(R.string.log_activity_title)) }
        TextButton(onClick = onExport) { Text(stringResource(R.string.local_export_title)) }
        TextButton(onClick = onGates) { Text(stringResource(R.string.open_gates)) }
    }
}

@Composable
private fun LogActivityScreen(
    fromPlan: Boolean,
    onProgress: () -> Unit,
    onTodaysPlan: () -> Unit,
    onHome: (() -> Unit)?,
) {
    ShellPage(title = stringResource(R.string.log_activity_title), onBack = onProgress, onHome = onHome) {
        if (fromPlan) {
            TextButton(onClick = onTodaysPlan) { Text(stringResource(R.string.open_todays_plan)) }
        }
    }
}

@Composable
private fun LocalExportScreen(onProgress: () -> Unit, onHome: (() -> Unit)?) {
    ShellPage(title = stringResource(R.string.local_export_title), onBack = onProgress, onHome = onHome) {
        Text(stringResource(R.string.local_export_scope))
    }
}

@Composable
private fun AttributionsScreen(
    snapshot: NavigationSnapshot,
    practiceAudioId: String?,
    onHome: (() -> Unit)?,
    onBack: () -> Unit,
) {
    ShellPage(title = stringResource(R.string.attributions_title), onBack = onBack, onHome = onHome) {
        if (snapshot.attributions.isEmpty()) {
            Text(stringResource(R.string.attributions_not_ready))
        }
        snapshot.attributions.forEach { record ->
            Text("${record.licenseName}: ${record.attribution}")
        }
        if (practiceAudioId != null) {
            TextButton(onClick = onBack) {
                Text(stringResource(R.string.back_to_practice))
            }
        }
    }
}

@Composable
private fun OptionalDrillsScreen(
    onHome: (() -> Unit)?,
    onAttributions: () -> Unit,
    onBack: () -> Unit,
) {
    ShellPage(title = stringResource(R.string.optional_drills_title), onBack = onBack, onHome = onHome) {
        Text(stringResource(R.string.optional_drills_not_evidence))
        TextButton(onClick = onAttributions) { Text(stringResource(R.string.open_attributions)) }
    }
}
