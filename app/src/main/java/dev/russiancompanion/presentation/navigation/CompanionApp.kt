package dev.russiancompanion.presentation.navigation

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.rememberNavController
import dev.russiancompanion.domain.navigation.LaunchDestination
import dev.russiancompanion.domain.navigation.NavigationGateway
import dev.russiancompanion.domain.navigation.NavigationSnapshot
import dev.russiancompanion.domain.navigation.launchDestination
import dev.russiancompanion.domain.navigation.optionalDrillsAvailable
import dev.russiancompanion.domain.plan.PlanUpdate
import dev.russiancompanion.domain.practice.PracticeAudio
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

@Composable
internal fun CompanionApp(
    gateway: NavigationGateway?,
    onRetryOpen: () -> Unit,
    today: () -> String = { LocalDate.now().toString() },
    recordedAt: () -> String = { LocalDateTime.now().toString() },
    practiceAudio: PracticeAudio,
) {
    var snapshot by remember(gateway) {
        mutableStateOf(gateway?.current(today()) ?: NavigationSnapshot.closed())
    }
    var failedAction by remember(gateway) { mutableStateOf<FailedPlanAction?>(null) }
    fun refresh() {
        snapshot = gateway?.current(today()) ?: NavigationSnapshot.closed()
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
        val update = when (action.kind) {
            PlanActionKind.COMPLETE -> gateway?.completeExternalActivity(action.planDefinitionId, today(), recordedAt())
            PlanActionKind.INCLUDE_TUTOR -> gateway?.includeTutorTask(action.planDefinitionId, today())
            PlanActionKind.REMOVE_TUTOR -> gateway?.removeTutorTask(action.planDefinitionId, today())
        }
        apply(update, action)
    }

    val launch = launchDestination(snapshot)
    MaterialTheme {
        key(launch, optionalDrillsAvailable(snapshot)) {
            val navController = rememberNavController()
            CompanionNavHost(
                navController = navController,
                launch = launch,
                snapshot = snapshot,
                saveFailed = failedAction != null,
                onConfirmLesson = { segmentId ->
                    gateway?.confirmLesson(segmentId, today())
                    refresh()
                },
                onReloadSeed = {
                    gateway?.reloadPackagedSeed()
                    refresh()
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
                onSaveRecording = { audioId, file ->
                    val update = gateway?.saveRecording(audioId, file, recordedAt())
                    if (update is PlanUpdate.Applied || update is PlanUpdate.Unchanged) refresh()
                    update ?: PlanUpdate.NotSaved(audioId)
                },
                onTutorReview = { recordingId, marked ->
                    val update = gateway?.setRecordingTutorReview(recordingId, marked)
                    if (update is PlanUpdate.Applied || update is PlanUpdate.Unchanged) refresh()
                    update ?: PlanUpdate.NotSaved(recordingId)
                },
                onCompletePractice = { audioId ->
                    val update = gateway?.completeListenPractice(audioId, today(), recordedAt())
                    if (update is PlanUpdate.Applied || update is PlanUpdate.Unchanged) refresh()
                    update ?: PlanUpdate.NotSaved(audioId)
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
