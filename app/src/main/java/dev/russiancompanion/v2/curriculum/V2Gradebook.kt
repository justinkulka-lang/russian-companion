package dev.russiancompanion.v2.curriculum

/** "x of y correct (z percent)" counts; percent is null when nothing was recorded. */
internal data class V2Tally(val correct: Int, val total: Int) {
    val percent: Int? get() = if (total == 0) null else correct * 100 / total
    fun label(): String = if (total == 0) "none recorded" else "$correct of $total correct ($percent percent)"
    operator fun plus(other: V2Tally) = V2Tally(correct + other.correct, total + other.total)
}

internal data class V2GradebookObjective(
    val objectiveId: String,
    val title: String,
    /** MA2-083 label; never "mastered" (MA2-084). */
    val stateLabel: String,
    val attempts: V2Tally,
    val review: V2Tally,
    val queueItems: Int,
)

internal data class V2GradebookUnit(
    val unitNumber: Int,
    val title: String,
    val attempts: V2Tally,
    val review: V2Tally,
    val queueItems: Int,
    /** Submitted unit-test attempts, oldest first. */
    val unitTests: List<V2UnitTestAttempt>,
    val objectives: List<V2GradebookObjective>,
)

internal fun objectiveStateLabel(state: V2ObjectiveState): String = when (state) {
    V2ObjectiveState.NOT_STARTED -> "Not started"
    V2ObjectiveState.LEARNING -> "Learning"
    V2ObjectiveState.REVIEW_REQUIRED -> "Review required"
    V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS -> "Demonstrated in app checks"
    V2ObjectiveState.COMPLETED_BY_SELF_ATTESTATION_ONLY -> "Completed by self-attestation only"
}

internal const val GRADEBOOK_NOTE =
    "Counts of recorded app checks. Accuracy here is not mastery or proficiency, and one passed check never marks an objective mastered."

/**
 * A2-35 gradebook for every reached unit (completed or current): per unit and objective, recorded
 * check attempts and accuracy, unit-test results, cumulative-review accuracy and review-queue items.
 * Only rows with a recorded insertion order count; nothing is inferred.
 */
internal fun gradebook(pack: V2CurriculumPack, learner: V2LearnerView): List<V2GradebookUnit> {
    val reached = (currentCompletedUnitNumbers(pack, learner) + learner.currentUnitNumber).toSortedSet()
    val attempts = learner.governedAttempts.orEmpty().filter { it.acceptedOrder > 0 }
    val assignments = learner.reviewAssignments.associateBy { it.id }
    // Each review answer resolves to its slot's objective; correctness uses the current reviewed item.
    val reviewRows = learner.reviewAnswers.mapNotNull { answer ->
        val slot = assignments[answer.assignmentId]?.slots?.getOrNull(answer.slotIndex) ?: return@mapNotNull null
        val correct = pack.item(slot.itemId)?.let { scoreCheckedResponse(it, answer.selected) == true } ?: return@mapNotNull null
        slot.objectiveId to correct
    }
    return reached.mapNotNull { number ->
        val unit = pack.unit(number) ?: return@mapNotNull null
        val objectives = unit.objectives.filter { it.kind == "RECOGNITION" }.map { o ->
            val own = attempts.filter { it.objectiveId == o.id && it.unitNumber == number }
            val review = reviewRows.filter { it.first == o.id }
            V2GradebookObjective(o.id, o.title, objectiveStateLabel(objectiveState(o, learner, pack)),
                V2Tally(own.count { it.correct }, own.size), V2Tally(review.count { it.second }, review.size),
                learner.spacedReviews.count { it.objectiveId == o.id && it.unitNumber == number })
        }
        V2GradebookUnit(number, unit.title,
            objectives.fold(V2Tally(0, 0)) { sum, o -> sum + o.attempts },
            objectives.fold(V2Tally(0, 0)) { sum, o -> sum + o.review },
            objectives.sumOf { it.queueItems },
            learner.unitTestAttempts.filter { it.unitNumber == number && it.submittedOrder > 0 }.sortedBy { it.submittedOrder },
            objectives)
    }
}
