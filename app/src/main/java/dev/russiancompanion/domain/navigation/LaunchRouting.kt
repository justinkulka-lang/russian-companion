package dev.russiancompanion.domain.navigation

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
    fun current(): NavigationSnapshot

    fun confirmLesson(segmentId: String, localDate: String)

    fun reloadPackagedSeed()
}
