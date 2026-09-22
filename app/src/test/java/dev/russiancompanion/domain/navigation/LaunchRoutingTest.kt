package dev.russiancompanion.domain.navigation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LaunchRoutingTest {
    @Test
    fun closedDataRoutesToTheLocalDataProblem() {
        assertEquals(LaunchDestination.LOCAL_DATA_PROBLEM, launchDestination(NavigationSnapshot.closed()))
    }

    @Test
    fun incompleteOnboardingRoutesToOnboarding() {
        assertEquals(LaunchDestination.ONBOARDING, launchDestination(openSnapshot(onboardingCompleted = false)))
    }

    @Test
    fun completedOnboardingRoutesHome() {
        assertEquals(LaunchDestination.HOME, launchDestination(openSnapshot(onboardingCompleted = true)))
    }

    @Test
    fun optionalDrillsRequireAnAdmittedPackage() {
        assertFalse(optionalDrillsAvailable(openSnapshot(drills = 0)))
        assertTrue(optionalDrillsAvailable(openSnapshot(drills = 1)))
    }

    @Test
    fun confirmationRequiresALessonThatHasPlanDefinitions() {
        val snapshot = openSnapshot()
        assertEquals(OnboardingConfirmation.LESSON_REQUIRED, onboardingConfirmation(snapshot, null))
        assertEquals(OnboardingConfirmation.LESSON_REQUIRED, onboardingConfirmation(snapshot, "missing"))
        assertEquals(
            OnboardingConfirmation.PLAN_DEFINITIONS_UNAVAILABLE,
            onboardingConfirmation(
                snapshot.copy(lessons = listOf(LessonOption("segment-1", "Lesson 1", planCount = 0))),
                "segment-1",
            ),
        )
        assertEquals(OnboardingConfirmation.READY, onboardingConfirmation(snapshot, "segment-1"))
        assertEquals(
            OnboardingConfirmation.CATALOG_UNAVAILABLE,
            onboardingConfirmation(snapshot.copy(seedCorrupt = true), "segment-1"),
        )
        assertEquals(
            OnboardingConfirmation.CATALOG_UNAVAILABLE,
            onboardingConfirmation(snapshot.copy(seedAbsent = true), "segment-1"),
        )
    }

    @Test
    fun activeLessonHasAPlanOnlyForTheSelectedSegment() {
        val snapshot = openSnapshot(activeSegmentId = "segment-1")
        assertTrue(activeLessonHasPlan(snapshot))
        assertFalse(activeLessonHasPlan(snapshot.copy(activeSegmentId = null)))
        assertFalse(
            activeLessonHasPlan(
                snapshot.copy(lessons = listOf(LessonOption("segment-1", "Lesson 1", planCount = 0))),
            ),
        )
    }

    @Test
    fun routeNamesSelectScreenSpecificSurfaces() {
        assertEquals(NavigationSurface.SHELL, navigationSurfaceFor(null))
        assertEquals(NavigationSurface.SHELL, navigationSurfaceFor("HomeRoute"))
        assertEquals(
            NavigationSurface.PRACTICE,
            navigationSurfaceFor("dev.russiancompanion.presentation.navigation.PracticeLibraryRoute"),
        )
        assertEquals(
            NavigationSurface.PRACTICE,
            navigationSurfaceFor("PracticeSessionRoute/{audioModelId}"),
        )
        assertEquals(
            NavigationSurface.ATTRIBUTIONS,
            navigationSurfaceFor("AttributionsRoute/{practiceAudioId}"),
        )
    }

    @Test
    fun pendingSnapshotDoesNotRouteToTheLocalDataProblem() {
        assertEquals(LaunchDestination.ONBOARDING, launchDestination(NavigationSnapshot.pending()))
        assertTrue(practiceLibraryAvailable(openSnapshot().copy(eligibleAudioCount = 1)))
    }

    private fun openSnapshot(
        onboardingCompleted: Boolean = false,
        activeSegmentId: String? = null,
        drills: Int = 0,
    ): NavigationSnapshot {
        return NavigationSnapshot(
            localDataOpen = true,
            seedCorrupt = false,
            seedAbsent = false,
            onboardingCompleted = onboardingCompleted,
            activeSegmentId = activeSegmentId,
            activeLessonReference = if (activeSegmentId == null) null else "Lesson 1",
            lessons = listOf(LessonOption("segment-1", "Lesson 1", planCount = 1)),
            audio = emptyList(),
            gates = emptyList(),
            eligibleDrillCount = drills,
            attributions = emptyList(),
        )
    }
}
