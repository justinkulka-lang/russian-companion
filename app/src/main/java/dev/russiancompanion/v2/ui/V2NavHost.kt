package dev.russiancompanion.v2.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import dev.russiancompanion.domain.appearance.AppearancePreference
import dev.russiancompanion.v2.curriculum.continueTarget

@Composable
internal fun V2NavHost(
    navController: NavHostController,
    state: V2UiState,
    appearance: AppearancePreference,
    appearanceSaveFailed: Boolean,
    onRetryOpen: () -> Unit,
    onAppearance: (AppearancePreference) -> Unit,
    onCompleteOnboarding: (V2OnboardingDraft) -> Unit,
    onCompleteStep: (String) -> Unit,
    onAttempt: (String, String, Boolean) -> Unit,
    onBookmark: (String, String) -> Unit,
    onNote: (String, String) -> Unit,
    onTutor: (String, String) -> Unit,
    onAttestBook: (Int) -> Unit,
    onAdvance: (Int) -> Unit,
    onResumeAt: (String, Int) -> Unit,
    onTransliteration: (Boolean) -> Unit,
) {
    val start = when {
        !state.localDataOpen || state.pack == null -> V2LocalDataRoute
        state.learner?.onboardingCompleted != true -> V2OnboardingRoute
        else -> V2TodayRoute
    }
    fun goTab(tab: V2Tab) {
        val route: Any = when (tab) {
            V2Tab.TODAY -> V2TodayRoute
            V2Tab.COURSE -> V2CourseRoute
            V2Tab.PRACTICE -> V2PracticeRoute
            V2Tab.REFERENCE -> V2ReferenceRoute
            V2Tab.PROGRESS -> V2ProgressRoute
        }
        navController.navigate(route) {
            popUpTo(V2TodayRoute) { inclusive = tab == V2Tab.TODAY }
            launchSingleTop = true
        }
    }
    NavHost(navController = navController, startDestination = start) {
        composable<V2LocalDataRoute> {
            V2LocalDataScreen(message = state.packError ?: "Local data could not be opened.", onRetry = onRetryOpen)
        }
        composable<V2OnboardingRoute> {
            V2OnboardingScreen(saveFailed = state.saveFailed, onFinished = onCompleteOnboarding)
        }
        composable<V2TodayRoute> {
            val pack = state.pack
            val learner = state.learner
            if (pack == null || learner == null) {
                V2LocalDataScreen(message = "Today is unavailable.", onRetry = onRetryOpen)
            } else {
                V2TodayScreen(
                    pack = pack,
                    learner = learner,
                    target = continueTarget(pack, learner),
                    onTab = ::goTab,
                    onContinue = { target ->
                        if (target.sessionId != null) {
                            onResumeAt(target.sessionId, target.stepIndex)
                            navController.navigate(V2SessionRoute(target.sessionId, target.stepIndex))
                        }
                    },
                )
            }
        }
        composable<V2CourseRoute> {
            val pack = state.pack
            val learner = state.learner
            if (pack == null || learner == null) return@composable
            V2CourseScreen(
                pack = pack,
                learner = learner,
                onTab = ::goTab,
                onOpenUnit = { number -> navController.navigate(V2UnitRoute(number)) },
            )
        }
        composable<V2UnitRoute> { entry ->
            val pack = state.pack
            val learner = state.learner
            if (pack == null || learner == null) return@composable
            val number = entry.toRoute<V2UnitRoute>().unitNumber
            V2UnitScreen(
                pack = pack,
                learner = learner,
                unitNumber = number,
                onBack = { navController.popBackStack() },
                onTab = ::goTab,
                onOpenSession = { sessionId ->
                    onResumeAt(sessionId, 0)
                    navController.navigate(V2SessionRoute(sessionId, 0))
                },
                onAttestBook = onAttestBook,
                onAdvance = onAdvance,
            )
        }
        composable<V2SessionRoute> { entry ->
            val pack = state.pack
            val learner = state.learner
            if (pack == null || learner == null) return@composable
            val route = entry.toRoute<V2SessionRoute>()
            V2SessionScreen(
                pack = pack,
                learner = learner,
                sessionId = route.sessionId,
                initialIndex = route.stepIndex,
                onBack = { navController.popBackStack() },
                onCompleteStep = onCompleteStep,
                onAttempt = onAttempt,
                onBookmark = onBookmark,
                onNote = onNote,
                onTutor = onTutor,
                onOpenReference = { id -> navController.navigate(V2ReferenceEntryRoute(id)) },
                onFinished = { navController.popBackStack() },
            )
        }
        composable<V2PracticeRoute> {
            val pack = state.pack
            val learner = state.learner
            if (pack == null || learner == null) return@composable
            V2PracticeScreen(
                pack = pack,
                learner = learner,
                onTab = ::goTab,
                onAlphabet = { navController.navigate(V2AlphabetLabRoute) },
            )
        }
        composable<V2AlphabetLabRoute> {
            val pack = state.pack
            val learner = state.learner
            if (pack == null || learner == null) return@composable
            V2AlphabetLabScreen(
                pack = pack,
                learner = learner,
                onBack = { navController.popBackStack() },
            )
        }
        composable<V2ReferenceRoute> {
            val pack = state.pack
            val learner = state.learner
            if (pack == null || learner == null) return@composable
            V2ReferenceScreen(
                pack = pack,
                learner = learner,
                onTab = ::goTab,
                onOpen = { id -> navController.navigate(V2ReferenceEntryRoute(id)) },
            )
        }
        composable<V2ReferenceEntryRoute> { entry ->
            val pack = state.pack
            if (pack == null) return@composable
            V2ReferenceDetailScreen(
                pack = pack,
                entryId = entry.toRoute<V2ReferenceEntryRoute>().entryId,
                onBack = { navController.popBackStack() },
                onBookmark = onBookmark,
            )
        }
        composable<V2ProgressRoute> {
            val pack = state.pack
            val learner = state.learner
            if (pack == null || learner == null) return@composable
            V2ProgressScreen(
                pack = pack,
                learner = learner,
                onTab = ::goTab,
                onSettings = { navController.navigate(V2SettingsRoute) },
            )
        }
        composable<V2SettingsRoute> {
            val learner = state.learner
            V2SettingsScreen(
                appearance = appearance,
                appearanceSaveFailed = appearanceSaveFailed,
                transliterationEnabled = learner?.transliterationEnabled == true,
                onBack = { navController.popBackStack() },
                onAppearance = onAppearance,
                onTransliteration = onTransliteration,
                onAttributions = { navController.navigate(V2AttributionsRoute) },
            )
        }
        composable<V2AttributionsRoute> {
            V2AttributionsScreen(onBack = { navController.popBackStack() })
        }
    }
}
