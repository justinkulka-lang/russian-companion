package dev.russiancompanion.presentation.navigation

import dev.russiancompanion.domain.navigation.ContinueDestination
import org.junit.Assert.assertEquals
import org.junit.Test

class ContinueNavigationTest {
    @Test
    fun continueRoutesCarryTheNamedPlanOrAudioIdentity() {
        assertEquals(
            TodaysPlanRoute("plan-book"),
            continueRoute(ContinueDestination.BookPractice("plan-book")),
        )
        assertEquals(
            TodaysPlanRoute("plan-anki"),
            continueRoute(ContinueDestination.AnkiPractice("plan-anki")),
        )
        assertEquals(
            PracticeSessionRoute("audio-1"),
            continueRoute(ContinueDestination.ListenRecordCompare("audio-1", "plan-listen")),
        )
        assertEquals(
            GateDetailRoute("gate-1"),
            continueRoute(ContinueDestination.GateEvidence("gate-1")),
        )
        assertEquals(ProgressRoute, continueRoute(ContinueDestination.Progress))
        assertEquals(OnboardingRoute, continueRoute(ContinueDestination.SelectLesson))
        assertEquals(null, continueRoute(ContinueDestination.Unavailable))
    }
}
