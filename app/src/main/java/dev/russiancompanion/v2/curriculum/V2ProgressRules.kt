package dev.russiancompanion.v2.curriculum

internal data class V2LearnerView(
    val onboardingCompleted: Boolean,
    val currentUnitNumber: Int,
    val startingPoint: V2StartingPoint,
    val hasPenguin: Boolean,
    val hasAnki: Boolean,
    val hasBinder: Boolean,
    val hasTutor: Boolean,
    val dailyMinutes: Int,
    val transliterationEnabled: Boolean,
    val currentSessionId: String?,
    val currentStepIndex: Int,
    val completedStepIds: Set<String>,
    val correctItemIds: Set<String>,
    val bookAttestedUnits: Set<Int>,
    val completedUnitNumbers: Set<Int>,
)

internal data class V2ContinueTarget(
    val title: String,
    val reason: String,
    val sessionId: String?,
    val stepIndex: Int,
    val unitNumber: Int,
    val unavailable: Boolean = false,
)

internal fun unitState(
    pack: V2CurriculumPack,
    unit: V2Unit,
    learner: V2LearnerView,
): V2UnitState {
    if (!unit.openable()) return V2UnitState.NOT_YET_INSTALLED
    if (unit.number in learner.completedUnitNumbers) return V2UnitState.COMPLETED
    if (unit.number == learner.currentUnitNumber) return V2UnitState.CURRENT
    val prereqs = unit.prerequisiteUnitNumbers
    return if (prereqs.all { it in learner.completedUnitNumbers }) {
        V2UnitState.AVAILABLE
    } else {
        V2UnitState.LOCKED
    }
}

internal fun installLabel(unit: V2Unit): String = when (unit.installState) {
    "INSTALLED" -> "Installed"
    "DEVELOPMENT" -> "In development"
    else -> "Not yet installed"
}

internal fun readinessLabel(unit: V2Unit): String = when (unit.readiness) {
    "NOT_PREPARED" -> "Not prepared"
    "CONTENT_COMPLETE_REVIEW_PENDING" -> "Content complete, review pending"
    "REVIEW_APPROVED" -> "Review approved"
    "MAPPING_APPROVED" -> "Mapping approved"
    "INSTALLABLE" -> "Ready to install"
    else -> ""
}

internal fun todaysSession(unit: V2Unit, learner: V2LearnerView): V2Session? {
    return unit.sessions.firstOrNull { session ->
        session.steps.any { it.id !in learner.completedStepIds }
    }
}

internal fun currentObjective(
    pack: V2CurriculumPack,
    unit: V2Unit,
    learner: V2LearnerView,
): V2Objective? {
    return unit.objectives.firstOrNull { objective ->
        objectiveState(objective, learner, pack) in setOf(
            V2ObjectiveState.NOT_STARTED,
            V2ObjectiveState.LEARNING,
            V2ObjectiveState.REVIEW_REQUIRED,
        )
    } ?: unit.objectives.lastOrNull()
}

internal fun recordedSessionCount(unit: V2Unit, learner: V2LearnerView): Int {
    return unit.sessions.count { session -> session.steps.all { it.id in learner.completedStepIds } }
}

internal fun canOpenUnit(state: V2UnitState): Boolean {
    return state == V2UnitState.CURRENT || state == V2UnitState.AVAILABLE || state == V2UnitState.COMPLETED
}

internal fun continueTarget(pack: V2CurriculumPack, learner: V2LearnerView): V2ContinueTarget {
    val unit = pack.unit(learner.currentUnitNumber) ?: return V2ContinueTarget(
        title = "Curriculum unavailable",
        reason = "The current unit is missing from the pack.",
        sessionId = null,
        stepIndex = 0,
        unitNumber = learner.currentUnitNumber,
        unavailable = true,
    )
    if (!unit.openable()) {
        return V2ContinueTarget(
            title = "Not yet installed",
            reason = "Unit ${unit.number} is on the map but has no lesson body.",
            sessionId = null,
            stepIndex = 0,
            unitNumber = unit.number,
            unavailable = true,
        )
    }
    val sessions = unit.sessions
    val resumeId = learner.currentSessionId
    val resumeSession = sessions.firstOrNull { it.id == resumeId } ?: sessions.first()
    val resumeIndex = learner.currentStepIndex.coerceIn(0, resumeSession.steps.lastIndex)
    val remaining = resumeSession.steps.drop(resumeIndex).firstOrNull { it.id !in learner.completedStepIds }
    if (remaining != null) {
        return V2ContinueTarget(
            title = remaining.title,
            reason = "Resume ${unit.title}: ${resumeSession.title}.",
            sessionId = resumeSession.id,
            stepIndex = resumeSession.steps.indexOf(remaining),
            unitNumber = unit.number,
        )
    }
    val nextSession = sessions.firstOrNull { session -> session.steps.any { it.id !in learner.completedStepIds } }
    if (nextSession != null) {
        val step = nextSession.steps.first { it.id !in learner.completedStepIds }
        return V2ContinueTarget(
            title = step.title,
            reason = "Next in ${unit.title}: ${nextSession.title}.",
            sessionId = nextSession.id,
            stepIndex = nextSession.steps.indexOf(step),
            unitNumber = unit.number,
        )
    }
    return V2ContinueTarget(
        title = "Review advancement",
        reason = "Required Unit ${unit.number} steps are recorded. Check advancement before moving on.",
        sessionId = sessions.last().id,
        stepIndex = sessions.last().steps.lastIndex,
        unitNumber = unit.number,
    )
}

internal fun objectiveState(
    objective: V2Objective,
    learner: V2LearnerView,
    pack: V2CurriculumPack,
): V2ObjectiveState {
    val relatedItems = pack.items.filter { it.objectiveId == objective.id }.map { it.id }
    val relatedSteps = pack.units.flatMap { it.sessions }.flatMap { it.steps }
        .filter { objective.id in (pack.session(it.id)?.objectiveIds ?: emptyList()) || it.itemIds.any { id -> id in relatedItems } }
    val stepsForObjective = pack.unit(objective.unitNumber)?.sessions
        ?.filter { objective.id in it.objectiveIds }
        ?.flatMap { it.steps }
        .orEmpty()
    val started = stepsForObjective.any { it.id in learner.completedStepIds } ||
        relatedItems.any { it in learner.correctItemIds }
    if (!started) return V2ObjectiveState.NOT_STARTED
    if (objective.kind == "SELF_ATTESTATION") {
        return if (objective.unitNumber in learner.bookAttestedUnits) {
            V2ObjectiveState.COMPLETED_BY_SELF_ATTESTATION_ONLY
        } else {
            V2ObjectiveState.LEARNING
        }
    }
    if (relatedItems.isNotEmpty()) {
        val correct = relatedItems.count { it in learner.correctItemIds }
        return when {
            correct == 0 -> V2ObjectiveState.LEARNING
            correct < relatedItems.size -> V2ObjectiveState.REVIEW_REQUIRED
            else -> V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS
        }
    }
    return if (stepsForObjective.all { it.id in learner.completedStepIds }) {
        V2ObjectiveState.LEARNING
    } else {
        V2ObjectiveState.LEARNING
    }
}

internal data class V2AdvancementDecision(
    val allowed: Boolean,
    val remaining: List<String>,
)

internal fun advancementDecision(
    pack: V2CurriculumPack,
    unit: V2Unit,
    learner: V2LearnerView,
): V2AdvancementDecision {
    val rule = unit.advancement ?: return V2AdvancementDecision(false, listOf("This unit has no advancement rule."))
    val remaining = mutableListOf<String>()
    if (unit.number in 16..25 && unit.readiness == "NOT_PREPARED") {
        remaining += "This unit is not prepared"
    }
    if (unit.number in 26..30 && unit.readiness == "NOT_PREPARED") {
        remaining += "This unit is not prepared"
    }
    if (unit.number in 26..30) {
        remaining += V2Validator.unresolvedLateUnitAdvancementBlockers(pack, unit)
    }
    if (rule.requireInstruction) {
        val instructionSteps = unit.sessions.flatMap { it.steps }.filter { it.kind in setOf("ORIENT", "TEACH") }
        if (instructionSteps.any { it.id !in learner.completedStepIds }) {
            remaining += "Finish the required instruction steps"
        }
    }
    if (rule.requireBookSelfAttestation && unit.number !in learner.bookAttestedUnits) {
        remaining += "Self-attest the book work for this unit"
    }
    if (rule.requireAppChecks) {
        val checkIds = unit.sessions.flatMap { it.steps }.flatMap { it.itemIds }.distinct()
        val correct = checkIds.count { it in learner.correctItemIds }
        if (correct < rule.minimumDistinctChecks) {
            remaining += "Demonstrate at least ${rule.minimumDistinctChecks} distinct in-app checks"
        }
        unit.objectives.filter { it.kind == "RECOGNITION" }.forEach { objective ->
            if (objectiveState(objective, learner, pack) != V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS) {
                remaining += "Demonstrate every check for ${objective.title}"
            }
        }
    }
    if (rule.requireDueReview) {
        val reviewSteps = unit.sessions.flatMap { it.steps }.filter { step ->
            pack.items.any { it.id in step.itemIds && it.objectiveId == "obj-u2-review" } ||
                step.id.contains("review")
        }
        if (reviewSteps.any { it.id !in learner.completedStepIds }) {
            remaining += "Complete the scheduled Unit 1 review"
        }
    }
    return V2AdvancementDecision(remaining.isEmpty(), remaining)
}

internal fun visibleReferences(pack: V2CurriculumPack, learner: V2LearnerView): List<V2ReferenceEntry> {
    val reached = (learner.completedUnitNumbers + learner.currentUnitNumber).maxOrNull() ?: 1
    return pack.references.filter { it.introducedInUnit <= reached }
}

internal fun searchReferences(entries: List<V2ReferenceEntry>, query: String): List<V2ReferenceEntry> {
    val needle = query.trim().lowercase()
    if (needle.isEmpty()) return entries
    return entries.filter { entry ->
        entry.title.lowercase().contains(needle) ||
            entry.summary.lowercase().contains(needle) ||
            entry.searchTerms.any { it.contains(needle) }
    }
}
