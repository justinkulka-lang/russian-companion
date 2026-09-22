package dev.russiancompanion.domain.navigation

import dev.russiancompanion.domain.gate.GateDetailView
import dev.russiancompanion.domain.plan.ActivitySummary
import dev.russiancompanion.domain.plan.DailyPlan
import dev.russiancompanion.domain.plan.GateSummary
import dev.russiancompanion.domain.progress.ProgressReview

internal data class LessonOption(
    val id: String,
    val lessonReference: String,
    val planCount: Int,
)

internal data class AudioOption(
    val id: String,
    val stressMarkedText: String,
    val localAudioReference: String = "",
    val playable: Boolean = true,
    val licenseName: String = "",
    val attribution: String = "",
    val hasRecording: Boolean = false,
    val completed: Boolean = false,
)

internal data class RecordingOption(
    val id: String,
    val audioModelId: String,
    val localFileReference: String,
    val markedForTutorReview: Boolean,
    val completed: Boolean,
    val readable: Boolean,
)

internal data class GateOption(
    val id: String,
    val displayedRuleText: String,
)

internal data class AttributionOption(
    val sourceId: String,
    val licenseName: String,
    val attribution: String,
    val modificationStatus: String,
    val shareAlikeConfirmation: String,
    val provenance: String = "",
    val redistributionBasis: String = "",
    val shareAlikeTreatment: String = "",
)

internal data class AssetInventoryOption(
    val rightsId: String?,
    val sourceId: String?,
    val assetIdentifier: String,
    val licenseStatus: String,
    val compatibilityDecision: String,
    val attributionRequired: Boolean,
    val inclusionResult: String,
    val audioModelId: String?,
    val localAudioReference: String,
    val audioEligible: Boolean,
    val rightsMissing: Boolean,
)

internal data class LicenseNoticeOption(
    val id: String,
    val title: String,
    val body: String,
)

internal data class NavigationSnapshot(
    val localDataOpen: Boolean,
    val seedCorrupt: Boolean,
    val seedAbsent: Boolean,
    val onboardingCompleted: Boolean,
    val activeSegmentId: String?,
    val activeLessonReference: String?,
    val lessons: List<LessonOption>,
    val audio: List<AudioOption>,
    val recordings: List<RecordingOption> = emptyList(),
    val gates: List<GateOption>,
    val eligibleDrillCount: Int,
    val attributions: List<AttributionOption>,
    val assetRecords: List<AssetInventoryOption> = emptyList(),
    val shareAlikeBlocksRelease: Boolean = false,
    val catalogNotice: String = "",
    val licenseNotices: List<LicenseNoticeOption> = emptyList(),
    val dailyPlan: DailyPlan = DailyPlan.empty(),
    val gateSummaries: List<GateSummary> = emptyList(),
    val activitySummary: ActivitySummary = ActivitySummary.none(),
    val gateDetails: List<GateDetailView> = emptyList(),
    val progress: ProgressReview = ProgressReview.empty(),
) {
    companion object {
        fun closed(): NavigationSnapshot {
            return NavigationSnapshot(
                localDataOpen = false,
                seedCorrupt = false,
                seedAbsent = false,
                onboardingCompleted = false,
                activeSegmentId = null,
                activeLessonReference = null,
                lessons = emptyList(),
                audio = emptyList(),
                gates = emptyList(),
                eligibleDrillCount = 0,
                attributions = emptyList(),
            )
        }
    }
}

internal enum class LaunchDestination {
    ONBOARDING,
    HOME,
    LOCAL_DATA_PROBLEM,
}

internal enum class OnboardingConfirmation {
    READY,
    LESSON_REQUIRED,
    PLAN_DEFINITIONS_UNAVAILABLE,
    CATALOG_UNAVAILABLE,
}
