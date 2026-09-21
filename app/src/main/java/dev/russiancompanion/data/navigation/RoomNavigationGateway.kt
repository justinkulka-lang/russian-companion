package dev.russiancompanion.data.navigation

import dev.russiancompanion.data.local.ActivityCategory
import dev.russiancompanion.data.local.CompletionState
import dev.russiancompanion.data.local.GateDecisionStatus
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.data.local.PlanActivityType
import dev.russiancompanion.data.local.seed.SeedApplyStatus
import dev.russiancompanion.domain.navigation.AttributionOption
import dev.russiancompanion.domain.navigation.AudioOption
import dev.russiancompanion.domain.navigation.GateOption
import dev.russiancompanion.domain.navigation.LessonOption
import dev.russiancompanion.domain.navigation.NavigationGateway
import dev.russiancompanion.domain.navigation.NavigationSnapshot
import dev.russiancompanion.domain.navigation.OnboardingConfirmation
import dev.russiancompanion.domain.navigation.onboardingConfirmation
import dev.russiancompanion.domain.plan.ActivityLogView
import dev.russiancompanion.domain.plan.ChecklistActivity
import dev.russiancompanion.domain.plan.DecisionView
import dev.russiancompanion.domain.plan.GateStatusInput
import dev.russiancompanion.domain.plan.PlanDefinitionView
import dev.russiancompanion.domain.plan.PlanInstanceView
import dev.russiancompanion.domain.plan.PlanUpdate
import dev.russiancompanion.domain.plan.RecordedCategory
import dev.russiancompanion.domain.plan.RecordedGateStatus
import dev.russiancompanion.domain.plan.assembleDailyPlan
import dev.russiancompanion.domain.plan.summarizeActivity
import dev.russiancompanion.domain.plan.summarizeGates

internal class RoomNavigationGateway(
    private val store: LocalStore,
    private val readPackagedSeed: () -> String?,
) : NavigationGateway {
    override fun current(localDate: String): NavigationSnapshot {
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
        val segmentId = learner.activeSegmentId
        val segmentPlans = if (segmentId == null) emptyList() else plans.filter { it.segmentId == segmentId }
        val instances = if (segmentId == null) {
            emptyList()
        } else {
            store.planInstances(localDate, segmentId).map { instance ->
                PlanInstanceView(
                    planDefinitionId = instance.planDefinitionId,
                    completed = instance.completionState == CompletionState.COMPLETED,
                )
            }
        }
        val gates = store.eligibleGates().filter { segmentId == null || it.segmentId == segmentId }
        return NavigationSnapshot(
            localDataOpen = true,
            seedCorrupt = outcome?.status == SeedApplyStatus.CORRUPT,
            seedAbsent = outcome?.status == SeedApplyStatus.ABSENT,
            onboardingCompleted = learner.onboardingCompleted,
            activeSegmentId = segmentId,
            activeLessonReference = lessons.firstOrNull { it.id == segmentId }?.lessonReference,
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
            dailyPlan = assembleDailyPlan(
                definitions = segmentPlans.mapNotNull { plan ->
                    val activity = checklistActivity(plan.activityType) ?: return@mapNotNull null
                    PlanDefinitionView(
                        id = plan.id,
                        activity = activity,
                        instruction = plan.instruction,
                        sortOrder = plan.sortOrder,
                        required = plan.required,
                    )
                },
                instances = instances,
            ),
            gateSummaries = summarizeGates(
                gates = gates.map { GateStatusInput(it.id, it.displayedRuleText) },
                decisions = store.decisions().mapNotNull { decision ->
                    val status = recordedStatus(decision.status) ?: return@mapNotNull null
                    DecisionView(
                        id = decision.id,
                        gateId = decision.gateDefinitionId,
                        status = status,
                        evaluatedAtLocal = decision.evaluatedAtLocal,
                    )
                },
            ),
            activitySummary = summarizeActivity(
                store.activityLogs().mapNotNull { log ->
                    val category = recordedCategory(log.category) ?: return@mapNotNull null
                    ActivityLogView(
                        id = log.id,
                        category = category,
                        minutes = log.durationMinutes,
                        completed = log.completed,
                        recordedAtLocal = log.recordedAtLocal,
                    )
                },
            ),
        )
    }

    override fun confirmLesson(segmentId: String, localDate: String) {
        if (onboardingConfirmation(current(localDate), segmentId) != OnboardingConfirmation.READY) {
            throw IllegalArgumentException("Onboarding cannot finish")
        }
        store.confirmLesson(segmentId, localDate)
    }

    override fun reloadPackagedSeed() {
        store.applySeed(readPackagedSeed())
    }

    override fun completeExternalActivity(
        planDefinitionId: String,
        localDate: String,
        recordedAtLocal: String,
    ): PlanUpdate {
        return update(planDefinitionId) {
            if (store.completeExternalActivity(planDefinitionId, localDate, recordedAtLocal)) {
                PlanUpdate.Applied
            } else {
                PlanUpdate.Unchanged
            }
        }
    }

    override fun includeTutorTask(planDefinitionId: String, localDate: String): PlanUpdate {
        return update(planDefinitionId) {
            store.includeTutorTask(planDefinitionId, localDate)
            PlanUpdate.Applied
        }
    }

    override fun removeTutorTask(planDefinitionId: String, localDate: String): PlanUpdate {
        return update(planDefinitionId) {
            store.removeTutorTask(planDefinitionId, localDate)
            PlanUpdate.Applied
        }
    }

    private fun update(planDefinitionId: String, block: () -> PlanUpdate): PlanUpdate {
        return try {
            block()
        } catch (_: Exception) {
            PlanUpdate.NotSaved(planDefinitionId)
        }
    }

    private fun checklistActivity(type: PlanActivityType): ChecklistActivity? {
        return when (type) {
            PlanActivityType.BOOK -> ChecklistActivity.BOOK
            PlanActivityType.ANKI_DROID -> ChecklistActivity.ANKI_DROID
            PlanActivityType.LISTEN_RECORD_COMPARE -> ChecklistActivity.LISTEN_RECORD_COMPARE
            PlanActivityType.TUTOR -> ChecklistActivity.TUTOR
        }
    }

    private fun recordedCategory(category: ActivityCategory): RecordedCategory? {
        return when (category) {
            ActivityCategory.READING -> RecordedCategory.READING
            ActivityCategory.LISTENING -> RecordedCategory.LISTENING
            ActivityCategory.VOCABULARY_AND_FORMS -> RecordedCategory.VOCABULARY_AND_FORMS
            ActivityCategory.GRAMMAR -> RecordedCategory.GRAMMAR
            ActivityCategory.SPEAKING_AND_PRONUNCIATION -> RecordedCategory.SPEAKING_AND_PRONUNCIATION
            ActivityCategory.WRITING -> RecordedCategory.WRITING
            ActivityCategory.TUTOR_WORK -> RecordedCategory.TUTOR_WORK
        }
    }

    private fun recordedStatus(status: GateDecisionStatus): RecordedGateStatus? {
        return when (status) {
            GateDecisionStatus.SATISFIED -> RecordedGateStatus.SATISFIED
            GateDecisionStatus.NOT_YET_SATISFIED -> RecordedGateStatus.NOT_YET_SATISFIED
            GateDecisionStatus.NOT_EVALUABLE -> RecordedGateStatus.NOT_EVALUABLE
        }
    }
}
