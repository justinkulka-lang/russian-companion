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
    val attemptSelections: Map<String, String> = emptyMap(),
    val governedAttempts: List<GovernedAttemptView>? = null,
    val governedStepDigests: Map<String, String>? = null,
    val governedStepSchemas: Map<String, Int>? = null,
    val resumeContentDigest: String = "",
    val resumeIdentitySchema: Int = 0,
    val bookmarks: List<GovernedBookmarkView> = emptyList(),
    val notes: List<GovernedNoteView> = emptyList(),
    val tutorItems: List<GovernedTutorView> = emptyList(),
    val reviewAssignments: List<CumulativeReviewAssignment> = emptyList(),
    val reviewAnswers: List<CumulativeReviewAnswer> = emptyList(),
    val unitReviewCompletions: Map<Int, CumulativeUnitCompletion> = emptyMap(),
)

internal data class V2ContinueTarget(
    val title: String,
    val reason: String,
    val sessionId: String?,
    val stepIndex: Int,
    val unitNumber: Int,
    val unavailable: Boolean = false,
    val opensUnitReview: Boolean = false,
    val opensCumulativeReview: Boolean = false,
)

internal fun unitState(
    pack: V2CurriculumPack,
    unit: V2Unit,
    learner: V2LearnerView,
): V2UnitState {
    if (!unit.openable()) return V2UnitState.NOT_YET_INSTALLED
    if (unit.number in currentCompletedUnitNumbers(pack, learner)) return V2UnitState.COMPLETED
    if (unit.number == learner.currentUnitNumber && unmetCurrentPrerequisites(pack, unit, learner).isEmpty()) return V2UnitState.CURRENT
    return if (unmetCurrentPrerequisites(pack, unit, learner).isEmpty()) {
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

internal fun todaysSession(pack: V2CurriculumPack, unit: V2Unit, learner: V2LearnerView): V2Session? {
    return requiredPathSessions(unit).firstOrNull { session ->
        session.steps.any { !completedStepCounts(pack, learner, it.id) }
    }
}

/** Unknown or shared objective ownership remains on the required path. */
internal fun requiredPathSessions(unit: V2Unit): List<V2Session> {
    val optionalIds = unit.objectives.filter { !it.gating }.map { it.id }.toSet()
    return unit.sessions.filter { session ->
        session.objectiveIds.isEmpty() || session.objectiveIds.any { it !in optionalIds }
    }
}

internal fun currentObjective(
    pack: V2CurriculumPack,
    unit: V2Unit,
    learner: V2LearnerView,
): V2Objective? {
    val requiredObjectives = unit.objectives.filter { it.gating }
    return requiredObjectives.firstOrNull { objective ->
        objectiveState(objective, learner, pack) in setOf(
            V2ObjectiveState.NOT_STARTED,
            V2ObjectiveState.LEARNING,
            V2ObjectiveState.REVIEW_REQUIRED,
        )
    } ?: requiredObjectives.lastOrNull()
}

internal fun recordedSessionCount(pack: V2CurriculumPack, unit: V2Unit, learner: V2LearnerView): Int {
    return unit.sessions.count { session -> session.steps.all { completedStepCounts(pack, learner, it.id) } }
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
    val missingPrerequisites = unmetCurrentPrerequisites(pack, unit, learner)
    if (missingPrerequisites.isNotEmpty()) {
        var review = pack.unit(missingPrerequisites.first())
        val visited = mutableSetOf<Int>()
        while (review != null && visited.add(review.number)) {
            val earlier = unmetCurrentPrerequisites(pack, review, learner).firstOrNull() ?: break
            review = pack.unit(earlier)
        }
        val reviewable = review != null && unmetCurrentPrerequisites(pack, review, learner).isEmpty()
        return V2ContinueTarget(
            title = "Review earlier completion",
            reason = "Current prerequisite evidence is needed before Unit ${unit.number} can open.",
            sessionId = null,
            stepIndex = 0,
            unitNumber = review?.number ?: unit.number,
            unavailable = !reviewable,
            opensUnitReview = reviewable,
        )
    }
    val sessions = unit.sessions
    if (!learner.currentSessionId.isNullOrBlank() && !resumeIsCurrent(pack, learner)) {
        val mismatch = learner.resumeIdentitySchema == GOVERNED_IDENTITY_SCHEMA
        return V2ContinueTarget(
            title = if (mismatch) "Saved lesson does not match" else "Saved lesson cannot be verified",
            reason = if (mismatch) {
                "This saved position was recorded for different lesson content. It will not be opened or scored."
            } else {
                "This saved position has no verified lesson identity. It will not be opened or scored."
            },
            sessionId = null,
            stepIndex = 0,
            unitNumber = unit.number,
            unavailable = true,
        )
    }
    val resumeId = learner.currentSessionId
    val resumeSession = sessions.firstOrNull { it.id == resumeId }
        ?: requiredPathSessions(unit).firstOrNull()
        ?: return V2ContinueTarget(
            title = "Review advancement",
            reason = "This unit has no required sessions. Check advancement before moving on.",
            sessionId = null,
            stepIndex = 0,
            unitNumber = unit.number,
            opensUnitReview = true,
        )
    val resumeIndex = learner.currentStepIndex.coerceIn(0, resumeSession.steps.lastIndex)
    val remaining = resumeSession.steps.drop(resumeIndex).firstOrNull { !completedStepCounts(pack, learner, it.id) }
    if (remaining != null) {
        return V2ContinueTarget(
            title = remaining.title,
            reason = "Resume ${unit.title}: ${resumeSession.title}.",
            sessionId = resumeSession.id,
            stepIndex = resumeSession.steps.indexOf(remaining),
            unitNumber = unit.number,
        )
    }
    val nextSession = requiredPathSessions(unit).firstOrNull { session -> session.steps.any { !completedStepCounts(pack, learner, it.id) } }
    if (nextSession != null) {
        val step = nextSession.steps.first { !completedStepCounts(pack, learner, it.id) }
        return V2ContinueTarget(
            title = step.title,
            reason = "Next in ${unit.title}: ${nextSession.title}.",
            sessionId = nextSession.id,
            stepIndex = nextSession.steps.indexOf(step),
            unitNumber = unit.number,
        )
    }
    if (cumulativeReviewPolicy(unit.number) != null && !cumulativeReviewComplete(pack, learner, unit.number)) return V2ContinueTarget(
        title = cumulativeReviewTitle(unit.number), reason = "Complete the assigned review before advancement.",
        sessionId = null, stepIndex = 0, unitNumber = unit.number, opensCumulativeReview = true,
    )
    return V2ContinueTarget(
        title = "Review advancement",
        reason = "Required Unit ${unit.number} steps are recorded. Check advancement before moving on.",
        sessionId = null,
        stepIndex = 0,
        unitNumber = unit.number,
        opensUnitReview = true,
    )
}

/**
 * Guided practice already listed on the objective that owns this remediation
 * step. Check and teach steps listed as practice are not a second journey back
 * to the failed check.
 */
internal fun reviewedGuidedPractice(pack: V2CurriculumPack, remediationStepId: String): List<String> {
    val explicitReviewTeaching = listOfNotNull(cumulativeReviewPolicy(3), cumulativeReviewPolicy(4), cumulativeReviewPolicy(5), cumulativeReviewPolicy(6), cumulativeReviewPolicy(7), cumulativeReviewPolicy(8), cumulativeReviewPolicy(9), cumulativeReviewPolicy(10), cumulativeReviewPolicy(11), cumulativeReviewPolicy(12), cumulativeReviewPolicy(13), cumulativeReviewPolicy(14), cumulativeReviewPolicy(15), cumulativeReviewPolicy(16), cumulativeReviewPolicy(17), cumulativeReviewPolicy(18), cumulativeReviewPolicy(19), cumulativeReviewPolicy(20), cumulativeReviewPolicy(21))
        .any { policy -> policy.pool.any { it.remediationStepId == remediationStepId } }
    val practiceIds = pack.units.flatMap { it.objectives }
        .filter { remediationStepId in it.remediationStepIds ||
            (explicitReviewTeaching && remediationStepId in it.instructionStepIds) }
        .flatMap { it.practiceStepIds }
        .distinct()
    return practiceIds.filter { id ->
        id != remediationStepId && pack.step(id)?.second?.kind == "GUIDED"
    }
}

internal fun advancementRuleSummary(rule: V2AdvancementRule): String {
    val parts = mutableListOf<String>()
    if (rule.requireInstruction) parts += "Finish the required instruction steps"
    if (rule.requireBookSelfAttestation) parts += "Self-attest the book work for this unit"
    if (rule.requireAppChecks) parts += "Demonstrate at least ${rule.minimumDistinctChecks} distinct in-app checks"
    if (rule.requireDueReview) parts += "Complete the scheduled review"
    if (rule.tutorOptional) parts += "Tutor review is optional"
    return if (parts.isEmpty()) "This unit has no advancement requirements." else parts.joinToString("; ")
}

internal fun objectiveState(
    objective: V2Objective,
    learner: V2LearnerView,
    pack: V2CurriculumPack,
): V2ObjectiveState {
    val credit = creditItemIds(pack, learner)
    val relatedItems = pack.items.filter { it.objectiveId == objective.id }.map { it.id }
    val relatedSteps = pack.units.flatMap { it.sessions }.flatMap { it.steps }
        .filter { objective.id in (pack.session(it.id)?.objectiveIds ?: emptyList()) || it.itemIds.any { id -> id in relatedItems } }
    val stepsForObjective = pack.unit(objective.unitNumber)?.sessions
        ?.filter { objective.id in it.objectiveIds }
        ?.flatMap { it.steps }
        .orEmpty()
    val started = stepsForObjective.any { completedStepCounts(pack, learner, it.id) } ||
        relatedItems.any { it in credit }
    if (!started) return V2ObjectiveState.NOT_STARTED
    if (objective.kind == "SELF_ATTESTATION") {
        return if (objective.unitNumber in learner.bookAttestedUnits) {
            V2ObjectiveState.COMPLETED_BY_SELF_ATTESTATION_ONLY
        } else {
            V2ObjectiveState.LEARNING
        }
    }
    if (relatedItems.isNotEmpty()) {
        val correct = relatedItems.count { it in credit }
        return when {
            correct == 0 -> V2ObjectiveState.LEARNING
            correct < relatedItems.size -> V2ObjectiveState.REVIEW_REQUIRED
            else -> V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS
        }
    }
    return if (stepsForObjective.all { completedStepCounts(pack, learner, it.id) }) {
        V2ObjectiveState.LEARNING
    } else {
        V2ObjectiveState.LEARNING
    }
}

internal data class V2AdvancementDecision(
    val allowed: Boolean,
    val remaining: List<String>,
)

/**
 * Advancement counts a governed contrast once. A second item with the same
 * prompt, cue, answer, options, and relationship is not a new check.
 */
internal fun advancementSignature(item: V2CheckItem): String {
    val options = item.options.sorted().joinToString("\u0001")
    val relation = item.assessment?.relationKind.orEmpty()
    return listOf(item.prompt, item.promptLetter.orEmpty(), item.answer, options, relation).joinToString("\u0002")
}

internal fun distinctCorrectCheckCount(
    pack: V2CurriculumPack,
    checkIds: List<String>,
    correctItemIds: Set<String>,
): Int {
    return checkIds.filter { it in correctItemIds }
        .mapNotNull { pack.item(it) }
        .map { advancementSignature(it) }
        .toSet()
        .size
}

/**
 * Orient and teach steps that gate advancement.
 * A step listed only on a non-gating objective is optional and does not block the unit.
 */
internal fun requiredInstructionSteps(unit: V2Unit): List<V2Step> {
    val requiredIds = unit.objectives.filter { it.gating }.flatMap { it.instructionStepIds }.toSet()
    val optionalOnly = unit.objectives.filter { !it.gating }.flatMap { it.instructionStepIds }.toSet() - requiredIds
    return unit.sessions.flatMap { it.steps }.filter { step ->
        step.kind in setOf("ORIENT", "TEACH") && step.id !in optionalOnly
    }
}

internal fun advancementDecision(
    pack: V2CurriculumPack,
    unit: V2Unit,
    learner: V2LearnerView,
): V2AdvancementDecision {
    val requirements = currentUnitRequirements(pack, unit, learner)
    val missing = unmetCurrentPrerequisites(pack, unit, learner).filter { it in learner.completedUnitNumbers }
    val remaining = requirements.remaining + missing.map { "Review current evidence for prerequisite Unit $it" }
    return V2AdvancementDecision(remaining.isEmpty(), remaining)
}

internal fun currentUnitRequirements(pack: V2CurriculumPack, unit: V2Unit, learner: V2LearnerView): V2AdvancementDecision {
    val base = ordinaryUnitRequirements(pack, unit, learner)
    val review = if (cumulativeReviewPolicy(unit.number) != null) {
        when {
            !cumulativeReviewDeclarationValid(pack, unit.number) -> listOf("The cumulative-review rule cannot be verified")
            !cumulativeReviewComplete(pack, learner, unit.number) -> listOf("Complete the assigned earlier-objective review")
            else -> emptyList()
        }
    } else if ((unit.advancement?.cumulativeReviewPolicyId != null ||
        (unit.number != 2 && unit.advancement?.requireDueReview == true)) &&
        !cumulativeReviewDeclarationValid(pack, unit.number)) {
        listOf("The cumulative-review rule cannot be verified")
    } else emptyList()
    val remaining = base.remaining + review
    return V2AdvancementDecision(remaining.isEmpty(), remaining)
}

/** Does not call completion/prerequisite derivation, avoiding recursive evidence evaluation. */
internal fun ordinaryUnitRequirements(
    pack: V2CurriculumPack,
    unit: V2Unit,
    learner: V2LearnerView,
): V2AdvancementDecision {
    val rule = unit.advancement ?: return V2AdvancementDecision(false, listOf("This unit has no advancement rule."))
    val remaining = mutableListOf<String>()
    if (unit.readiness == "NOT_PREPARED") {
        remaining += "This unit is not prepared"
    }
    if (unit.number in 26..30) {
        remaining += V2Validator.unresolvedLateUnitAdvancementBlockers(pack, unit)
    }
    remaining += V2Validator.unresolvedPartialUnitAdvancementBlockers(pack, unit)
    if (rule.requireInstruction) {
        val instructionSteps = requiredInstructionSteps(unit)
        if (instructionSteps.any { !completedStepCounts(pack, learner, it.id) }) {
            remaining += "Finish the required instruction steps"
        }
    }
    if (rule.requireBookSelfAttestation && unit.number !in learner.bookAttestedUnits) {
        remaining += "Self-attest the book work for this unit"
    }
    if (rule.requireAppChecks) {
        val requiredObjectiveIds = unit.objectives.filter { it.gating }.map { it.id }.toSet()
        val checkIds = unit.sessions.flatMap { it.steps }.flatMap { it.itemIds }.distinct()
            .filter { pack.item(it)?.objectiveId in requiredObjectiveIds }
        val correct = distinctCorrectCheckCount(pack, checkIds, creditItemIds(pack, learner))
        if (correct < rule.minimumDistinctChecks) {
            remaining += "Demonstrate at least ${rule.minimumDistinctChecks} distinct in-app checks"
        }
        unit.objectives.filter { it.kind == "RECOGNITION" && it.gating }.forEach { objective ->
            if (objectiveState(objective, learner, pack) != V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS) {
                remaining += "Demonstrate every check for ${objective.title}"
            }
        }
    }
    if (rule.requireDueReview && cumulativeReviewPolicy(unit.number) == null) {
        val reviewSteps = unit.sessions.flatMap { it.steps }.filter { step ->
            pack.items.any { it.id in step.itemIds && it.objectiveId == "obj-u2-review" } ||
                step.id.contains("review")
        }
        if (reviewSteps.any { !completedStepCounts(pack, learner, it.id) }) {
            remaining += "Complete the scheduled Unit 1 review"
        }
    }
    return V2AdvancementDecision(remaining.isEmpty(), remaining)
}

internal fun visibleReferences(pack: V2CurriculumPack, learner: V2LearnerView): List<V2ReferenceEntry> {
    val reached = (currentCompletedUnitNumbers(pack, learner) + learner.currentUnitNumber).maxOrNull() ?: 1
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

