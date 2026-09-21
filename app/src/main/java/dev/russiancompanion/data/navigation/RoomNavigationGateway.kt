package dev.russiancompanion.data.navigation

import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.data.local.seed.SeedApplyStatus
import dev.russiancompanion.domain.navigation.AttributionOption
import dev.russiancompanion.domain.navigation.AudioOption
import dev.russiancompanion.domain.navigation.GateOption
import dev.russiancompanion.domain.navigation.LessonOption
import dev.russiancompanion.domain.navigation.NavigationGateway
import dev.russiancompanion.domain.navigation.NavigationSnapshot
import dev.russiancompanion.domain.navigation.OnboardingConfirmation
import dev.russiancompanion.domain.navigation.onboardingConfirmation

internal class RoomNavigationGateway(
    private val store: LocalStore,
    private val readPackagedSeed: () -> String?,
) : NavigationGateway {
    override fun current(): NavigationSnapshot {
        val outcome = store.seedOutcome()
        val plans = store.eligiblePlans()
        val lessons = store.eligibleSegments().map { segment ->
            LessonOption(
                id = segment.id,
                lessonReference = segment.lessonReference,
                planCount = plans.count { it.segmentId == segment.id },
            )
        }
        val learner = store.learner()
        val audio = store.eligibleAudio().mapNotNull { model ->
            val textId = model.sourceTextId ?: return@mapNotNull null
            val text = store.sourceText(textId) ?: return@mapNotNull null
            AudioOption(id = model.id, stressMarkedText = text.stressMarkedText)
        }
        return NavigationSnapshot(
            localDataOpen = true,
            seedCorrupt = outcome?.status == SeedApplyStatus.CORRUPT,
            seedAbsent = outcome?.status == SeedApplyStatus.ABSENT,
            onboardingCompleted = learner.onboardingCompleted,
            activeSegmentId = learner.activeSegmentId,
            activeLessonReference = lessons.firstOrNull { it.id == learner.activeSegmentId }?.lessonReference,
            lessons = lessons,
            audio = audio,
            gates = store.eligibleGates().map { gate ->
                GateOption(id = gate.id, displayedRuleText = gate.displayedRuleText)
            },
            eligibleDrillCount = store.eligibleDrills().size,
            attributions = store.sources().map { source ->
                AttributionOption(
                    sourceId = source.id,
                    licenseName = source.licenseName,
                    attribution = source.attribution,
                    modificationStatus = source.modificationStatus.name,
                    shareAlikeConfirmation = source.shareAlikeConfirmation.name,
                )
            },
        )
    }

    override fun confirmLesson(segmentId: String, localDate: String) {
        if (onboardingConfirmation(current(), segmentId) != OnboardingConfirmation.READY) {
            throw IllegalArgumentException("Onboarding cannot finish")
        }
        store.confirmLesson(segmentId, localDate)
    }

    override fun reloadPackagedSeed() {
        store.applySeed(readPackagedSeed())
    }
}
