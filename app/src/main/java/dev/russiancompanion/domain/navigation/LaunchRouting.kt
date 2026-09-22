package dev.russiancompanion.domain.navigation

import dev.russiancompanion.domain.gate.GateEvidenceEntry
import dev.russiancompanion.domain.plan.PlanUpdate
import dev.russiancompanion.domain.plan.RecordedCategory
import dev.russiancompanion.domain.progress.ExportUpdate
import java.io.OutputStream

internal fun practiceLibraryAvailable(snapshot: NavigationSnapshot): Boolean {
    return snapshot.audio.isNotEmpty()
}

internal fun practicePlayable(snapshot: NavigationSnapshot): Boolean {
    return snapshot.audio.any { it.playable }
}

internal fun practicePlayable(
    snapshot: NavigationSnapshot,
    prescribedAudioModelIds: List<String>,
): Boolean {
    if (prescribedAudioModelIds.isEmpty()) return practicePlayable(snapshot)
    val playable = snapshot.audio.filter { it.playable }.map { it.id }.toSet()
    return prescribedAudioModelIds.any { it in playable }
}

internal fun launchDestination(snapshot: NavigationSnapshot): LaunchDestination {
    if (!snapshot.localDataOpen) return LaunchDestination.LOCAL_DATA_PROBLEM
    if (!snapshot.onboardingCompleted) return LaunchDestination.ONBOARDING
    return LaunchDestination.HOME
}

internal fun optionalDrillsAvailable(snapshot: NavigationSnapshot): Boolean {
    return snapshot.eligibleDrillCount > 0
}

internal fun onboardingConfirmation(
    snapshot: NavigationSnapshot,
    selectedLessonId: String?,
): OnboardingConfirmation {
    if (snapshot.seedCorrupt || snapshot.seedAbsent) return OnboardingConfirmation.CATALOG_UNAVAILABLE
    if (selectedLessonId == null) return OnboardingConfirmation.LESSON_REQUIRED
    val lesson = snapshot.lessons.firstOrNull { it.id == selectedLessonId }
        ?: return OnboardingConfirmation.LESSON_REQUIRED
    if (lesson.planCount == 0) return OnboardingConfirmation.PLAN_DEFINITIONS_UNAVAILABLE
    return OnboardingConfirmation.READY
}

internal fun activeLessonHasPlan(snapshot: NavigationSnapshot): Boolean {
    val segmentId = snapshot.activeSegmentId ?: return false
    return snapshot.lessons.any { it.id == segmentId && it.planCount > 0 }
}

internal interface NavigationGateway {
    fun current(localDate: String): NavigationSnapshot

    fun confirmLesson(segmentId: String, localDate: String)

    fun reloadPackagedSeed()

    fun completeExternalActivity(
        planDefinitionId: String,
        localDate: String,
        recordedAtLocal: String,
    ): PlanUpdate

    fun includeTutorTask(planDefinitionId: String, localDate: String): PlanUpdate

    fun removeTutorTask(planDefinitionId: String, localDate: String): PlanUpdate

    fun saveRecording(audioModelId: String, localFileReference: String, recordedAtLocal: String): PlanUpdate

    fun setRecordingTutorReview(recordingId: String, marked: Boolean): PlanUpdate

    fun completeListenPractice(audioModelId: String, localDate: String, recordedAtLocal: String): PlanUpdate

    fun saveGateEvidence(
        gateId: String,
        entries: List<GateEvidenceEntry>,
        recordedAtLocal: String,
    ): PlanUpdate

    fun addTutorSignOff(gateId: String, recordedAtLocal: String): PlanUpdate

    fun saveActivityLog(
        category: RecordedCategory,
        durationMinutes: Int,
        completed: Boolean,
        recordedAtLocal: String,
        associateWithLesson: Boolean,
    ): PlanUpdate

    fun exportLocal(
        destination: OutputStream,
        recordedAtLocal: String,
        destinationFileName: String?,
        documentReference: String?,
    ): ExportUpdate
}
