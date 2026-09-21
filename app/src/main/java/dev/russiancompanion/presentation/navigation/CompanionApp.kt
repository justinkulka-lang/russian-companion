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
import java.time.LocalDate

@Composable
internal fun CompanionApp(
    gateway: NavigationGateway?,
    onRetryOpen: () -> Unit,
    today: () -> String = { LocalDate.now().toString() },
) {
    var snapshot by remember(gateway) {
        mutableStateOf(gateway?.current() ?: NavigationSnapshot.closed())
    }
    fun refresh() {
        snapshot = gateway?.current() ?: NavigationSnapshot.closed()
    }

    val launch = launchDestination(snapshot)
    MaterialTheme {
        key(launch, optionalDrillsAvailable(snapshot)) {
            val navController = rememberNavController()
            CompanionNavHost(
                navController = navController,
                launch = launch,
                snapshot = snapshot,
                onConfirmLesson = { segmentId ->
                    gateway?.confirmLesson(segmentId, today())
                    refresh()
                },
                onReloadSeed = {
                    gateway?.reloadPackagedSeed()
                    refresh()
                },
                onRetryOpen = onRetryOpen,
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
