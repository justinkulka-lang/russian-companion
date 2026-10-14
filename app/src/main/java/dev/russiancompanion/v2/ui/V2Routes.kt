package dev.russiancompanion.v2.ui

import kotlinx.serialization.Serializable

@Serializable
internal data object V2OnboardingRoute

@Serializable
internal data object V2LocalDataRoute

@Serializable
internal data object V2TodayRoute

@Serializable
internal data object V2CourseRoute

@Serializable
internal data class V2UnitRoute(val unitNumber: Int)

@Serializable
internal data class V2SessionRoute(
    val sessionId: String,
    val stepIndex: Int = 0,
    val remedial: Boolean = false,
)

@Serializable
internal data object V2CumulativeReviewRoute

@Serializable
internal data object V2PracticeRoute

@Serializable
internal data object V2AlphabetLabRoute

@Serializable
internal data class V2LabRoute(val labId: String)

@Serializable
internal data object V2SavedWorkRoute

@Serializable
internal data object V2ReferenceRoute

@Serializable
internal data class V2ReferenceEntryRoute(val entryId: String)

@Serializable
internal data object V2ProgressRoute

@Serializable
internal data object V2SettingsRoute

@Serializable
internal data object V2AttributionsRoute

@Serializable
internal data object V2BackupRoute

internal enum class V2Tab {
    TODAY,
    COURSE,
    PRACTICE,
    REFERENCE,
    PROGRESS,
}
