package dev.russiancompanion.v2.curriculum

/** Historical completion rows are retained; only current governed evidence counts. */
internal object V2CompletionEvidence {
    private val cache = IdentityWeakCache<IdentityWeakCache<Set<Int>>>()

    fun current(pack: V2CurriculumPack, learner: V2LearnerView): Set<Int> {
        val learners = cache.get(pack) ?: IdentityWeakCache<Set<Int>>().also { cache.put(pack, it) }
        learners.get(learner)?.let { return it }
        if (learner.completedUnitNumbers.isEmpty() || learner.governedAttempts == null ||
            learner.governedStepDigests == null || learner.governedStepSchemas == null) {
            return emptySet<Int>().also { learners.put(learner, it) }
        }
        val eligible = pack.units.filter { unit ->
            unit.number in learner.completedUnitNumbers && unit.openable() &&
                ordinaryUnitRequirements(pack, unit, learner).allowed &&
                historicalReviewCompletionCurrent(pack, unit, learner)
        }
        val verified = linkedSetOf<Int>()
        // Fixed point also fails closed for unknown or cyclic prerequisite claims.
        var changed: Boolean
        do {
            changed = false
            eligible.forEach { unit ->
                if (unit.number !in verified && unit.prerequisiteUnitNumbers.all { prerequisite ->
                        prerequisite in verified || explicitlySkippedPrerequisite(prerequisite, learner)
                    }) {
                    verified += unit.number
                    changed = true
                }
            }
        } while (changed)
        return verified.toSet().also { learners.put(learner, it) }
    }

    fun clearForTests() = cache.clear()
}

internal fun currentCompletedUnitNumbers(pack: V2CurriculumPack, learner: V2LearnerView): Set<Int> =
    V2CompletionEvidence.current(pack, learner)

/** Placement is an explicit user choice, never proof that a claimed completion is current. */
private fun explicitlySkippedPrerequisite(number: Int, learner: V2LearnerView): Boolean =
    number !in learner.completedUnitNumbers && when (learner.startingPoint) {
        V2StartingPoint.RESUME_FROM_BOOK -> number < learner.currentUnitNumber
        V2StartingPoint.CAN_READ_CYRILLIC -> number <= 2 && learner.currentUnitNumber >= 3
        V2StartingPoint.NEW_TO_CYRILLIC -> false
    }

internal fun unmetCurrentPrerequisites(pack: V2CurriculumPack, unit: V2Unit, learner: V2LearnerView): List<Int> {
    val current = currentCompletedUnitNumbers(pack, learner)
    return unit.prerequisiteUnitNumbers.filter { it !in current && !explicitlySkippedPrerequisite(it, learner) }
}
