package dev.russiancompanion.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
data object OnboardingRoute

@Serializable
data object LocalDataRoute

@Serializable
data object HomeRoute

@Serializable
data object TodaysPlanRoute

@Serializable
data object PracticeLibraryRoute

@Serializable
data class PracticeSessionRoute(val audioModelId: String)

@Serializable
data object GatesRoute

@Serializable
data class GateDetailRoute(val gateId: String)

@Serializable
data object ProgressRoute

@Serializable
data class LogActivityRoute(val fromPlan: Boolean = false)

@Serializable
data object LocalExportRoute

@Serializable
data class AttributionsRoute(val practiceAudioId: String? = null)

@Serializable
data object OptionalDrillsRoute
