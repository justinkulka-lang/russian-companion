package dev.russiancompanion.v2.curriculum

import kotlinx.serialization.Serializable
import java.security.MessageDigest

internal const val CUMULATIVE_REVIEW_SCHEMA = 1
internal const val CUMULATIVE_REVIEW_ROLE = "CUMULATIVE_REVIEW"

@Serializable
internal data class CumulativeReviewSlot(
    val objectiveId: String, val skill: String, val itemId: String,
    val ownerUnit: Int, val itemDigest: String, val unitDigest: String, val teachingDigest: String,
)

@Serializable
internal data class CumulativeReviewAssignment(
    val id: String, val targetUnit: Int, val policyId: String, val policyDigest: String,
    val responseRuleDigest: String, val boundary: Long, val slots: List<CumulativeReviewSlot>, val digest: String,
    val schema: Int = CUMULATIVE_REVIEW_SCHEMA,
)

@Serializable
internal data class CumulativeReviewAnswer(
    val id: String, val assignmentId: String, val assignmentDigest: String,
    val slotIndex: Int, val selected: String, val acceptedOrder: Long,
    val role: String = CUMULATIVE_REVIEW_ROLE, val schema: Int = CUMULATIVE_REVIEW_SCHEMA,
)

/** Skill labels describe only the bounded evidence supported by the authored checks. */
internal enum class CumulativeReviewSkill { BOUNDED_READING, PRINTED_LETTER_RECOGNITION, NOUN_GENDER_RECOGNITION, SUPPLIED_NOMINATIVE_SUBJECT_RECOGNITION, SUPPLIED_NOUN_BEHAVIOR_RECOGNITION, SUPPLIED_INANIMATE_ACCUSATIVE_RECOGNITION, SUPPLIED_NOMINATIVE_AGREEMENT_RECOGNITION, SUPPLIED_NOMINATIVE_PLURAL_RECOGNITION, SUPPLIED_NUMERAL_GENDER_RECOGNITION }

internal data class CumulativeReviewObjectivePool(
    val objectiveId: String, val skill: CumulativeReviewSkill, val candidateItemIds: List<String>,
    val remediationStepId: String? = null,
)

/** Explicit declarations only; adding a type or passing a target number never activates a gate. */
internal data class CumulativeReviewPolicy(
    val id: String, val targetUnit: Int, val pool: List<CumulativeReviewObjectivePool>,
) {
    init {
        require(id.isNotBlank() && targetUnit in 3..30)
        require(pool.size in 2..6 && pool.map { it.objectiveId }.distinct().size == pool.size)
        require(pool.all { it.objectiveId.isNotBlank() && it.candidateItemIds.isNotEmpty() &&
            it.candidateItemIds.all(String::isNotBlank) &&
            it.candidateItemIds.distinct().size == it.candidateItemIds.size })
    }
    // Keep the original Unit3 canonical form: persisted policy/assignment identities remain valid.
    val digest by lazy {
        reviewHash(canonicalParts(listOf(id, "target:$targetUnit", "slots:${pool.size}") + pool.map {
            canonicalParts(listOf(it.objectiveId, it.skill.name, canonicalParts(it.candidateItemIds)) +
                listOfNotNull(it.remediationStepId))
        }))
    }
}

internal object Unit3CumulativeReviewPolicy {
    const val id = "unit3-prior-literacy-v1"
    val definition = CumulativeReviewPolicy(id, 3, listOf(
        CumulativeReviewObjectivePool("obj-u1-decoding", CumulativeReviewSkill.BOUNDED_READING, listOf("u1-i-decode-pair")),
        CumulativeReviewObjectivePool("obj-u1-confusable", CumulativeReviewSkill.PRINTED_LETTER_RECOGNITION, listOf(
            "u1-i-р-not-p", "u1-i-н-not-h", "u1-i-с-not-c", "u1-i-в-not-b", "u1-i-у-not-y", "u1-i-х-not-x")),
    ))
    val digest get() = definition.digest
}

internal object Unit4CumulativeReviewPolicy {
    val definition = CumulativeReviewPolicy("unit4-prior-reading-gender-v1", 4, listOf(
        CumulativeReviewObjectivePool("obj-u1-decoding", CumulativeReviewSkill.BOUNDED_READING, listOf("u1-i-decode-pair")),
        CumulativeReviewObjectivePool("obj-u3-gender", CumulativeReviewSkill.NOUN_GENDER_RECOGNITION,
            listOf("u3-i-gender-город"), "u3-st-teach-gender-2"),
    ))
}

internal object Unit5CumulativeReviewPolicy {
    val definition = CumulativeReviewPolicy("unit5-prior-reading-subject-v1", 5, listOf(
        CumulativeReviewObjectivePool("obj-u1-decoding", CumulativeReviewSkill.BOUNDED_READING, listOf("u1-i-decode-pair")),
        CumulativeReviewObjectivePool("obj-u4-nominative", CumulativeReviewSkill.SUPPLIED_NOMINATIVE_SUBJECT_RECOGNITION,
            listOf("u4-i-nom-role-train", "u4-i-nom-role-student"), "u4-st-rem-nominative"),
    ))
}

internal object Unit6CumulativeReviewPolicy {
    val definition = CumulativeReviewPolicy("unit6-prior-reading-noun-behavior-v1", 6, listOf(
        CumulativeReviewObjectivePool("obj-u1-decoding", CumulativeReviewSkill.BOUNDED_READING, listOf("u1-i-decode-pair")),
        CumulativeReviewObjectivePool("obj-u5-indeclinable", CumulativeReviewSkill.SUPPLIED_NOUN_BEHAVIOR_RECOGNITION,
            listOf("u5-i-indecl-такси", "u5-i-indecl-метро", "u5-i-indecl-кино", "u5-i-decl-книга", "u5-i-decl-окно"),
            "u5-st-rem-indeclinable"),
    ))
}

internal object Unit7CumulativeReviewPolicy {
    val definition = CumulativeReviewPolicy("unit7-prior-reading-inanimate-case-v1", 7, listOf(
        CumulativeReviewObjectivePool("obj-u1-decoding", CumulativeReviewSkill.BOUNDED_READING, listOf("u1-i-decode-pair")),
        CumulativeReviewObjectivePool("obj-u6-animacy", CumulativeReviewSkill.SUPPLIED_INANIMATE_ACCUSATIVE_RECOGNITION,
            listOf("u6-i-animacy-3"), "u6-st-rem-animacy"),
    ))
}

internal object Unit8CumulativeReviewPolicy {
    val definition = CumulativeReviewPolicy("unit8-prior-reading-nominative-agreement-v1", 8, listOf(
        CumulativeReviewObjectivePool("obj-u1-decoding", CumulativeReviewSkill.BOUNDED_READING, listOf("u1-i-decode-pair")),
        CumulativeReviewObjectivePool("obj-u7-endings", CumulativeReviewSkill.SUPPLIED_NOMINATIVE_AGREEMENT_RECOGNITION,
            listOf("u7-i-endings-4"), "u7-st-teach-endings-1"),
    ))
}

internal object Unit9CumulativeReviewPolicy {
    val definition = CumulativeReviewPolicy("unit9-prior-reading-nominative-plural-v1", 9, listOf(
        CumulativeReviewObjectivePool("obj-u1-decoding", CumulativeReviewSkill.BOUNDED_READING, listOf("u1-i-decode-pair")),
        CumulativeReviewObjectivePool("obj-u8-plural", CumulativeReviewSkill.SUPPLIED_NOMINATIVE_PLURAL_RECOGNITION,
            listOf("u8-i-plural-2"), "u8-st-rem-plural"),
    ))
}

internal object Unit10CumulativeReviewPolicy {
    val definition = CumulativeReviewPolicy("unit10-prior-reading-numeral-gender-v1", 10, listOf(
        CumulativeReviewObjectivePool("obj-u1-decoding", CumulativeReviewSkill.BOUNDED_READING, listOf("u1-i-decode-pair")),
        CumulativeReviewObjectivePool("obj-u9-odindva", CumulativeReviewSkill.SUPPLIED_NUMERAL_GENDER_RECOGNITION,
            listOf("u9-i-odindva-4"), "u9-st-rem-odindva"),
    ))
}

/** Authored engineering slices only; every remaining target has no policy. */
internal fun cumulativeReviewPolicy(targetUnit: Int): CumulativeReviewPolicy? = when (targetUnit) {
    3 -> Unit3CumulativeReviewPolicy.definition
    4 -> Unit4CumulativeReviewPolicy.definition
    5 -> Unit5CumulativeReviewPolicy.definition
    6 -> Unit6CumulativeReviewPolicy.definition
    7 -> Unit7CumulativeReviewPolicy.definition
    8 -> Unit8CumulativeReviewPolicy.definition
    9 -> Unit9CumulativeReviewPolicy.definition
    10 -> Unit10CumulativeReviewPolicy.definition
    else -> null
}

internal fun cumulativeReviewTitle(targetUnit: Int): String = when (targetUnit) {
    3 -> "Review earlier reading and letters"
    4 -> "Review earlier reading and noun genders"
    5 -> "Review earlier reading and supplied subjects"
    6 -> "Review earlier reading and noun behavior"
    7 -> "Review earlier reading and supplied object forms"
    8 -> "Review earlier reading and supplied agreement"
    9 -> "Review earlier reading and supplied noun plurals"
    10 -> "Review earlier reading and supplied numeral agreement"
    else -> "Review earlier objectives"
}

internal fun cumulativeReviewDeclarationValid(pack: V2CurriculumPack, targetUnit: Int): Boolean {
    val policy = cumulativeReviewPolicy(targetUnit) ?: return false
    return pack.unit(targetUnit)?.advancement?.let {
        it.requireDueReview && it.cumulativeReviewPolicyId == policy.id
    } == true
}

internal fun reviewHash(text: String): String = MessageDigest.getInstance("SHA-256")
    .digest(text.toByteArray()).joinToString("") { "%02x".format(it.toInt() and 255) }

internal fun reviewAssignmentDigest(a: CumulativeReviewAssignment): String = reviewHash(canonicalParts(
    listOf(a.schema.toString(), a.id, a.targetUnit.toString(), a.policyId, a.policyDigest, a.responseRuleDigest, a.boundary.toString()) +
        a.slots.map { s -> canonicalParts(listOf(s.objectiveId, s.skill, s.itemId, s.ownerUnit.toString(),
            s.itemDigest, s.unitDigest, s.teachingDigest)) },
))

private fun reviewUnitDigest(pack: V2CurriculumPack, unit: Int): String =
    V2PackIdentityIndex.unitDigest(pack, unit) ?: V2Validator.unitContentReviewDigest(pack, unit)

private fun teachingDigest(pack: V2CurriculumPack, objective: V2Objective): String = reviewHash(canonicalParts(
    (objective.instructionStepIds + objective.practiceStepIds).map { id ->
        val step = pack.step(id)?.second ?: return ""
        stepContentDigest(pack, step)
    },
))

/** Requires actual taught evidence; placement and bare historical completion are insufficient. */
internal fun createUnit3ReviewAssignment(pack: V2CurriculumPack, learner: V2LearnerView,
                                       id: String, boundary: Long): CumulativeReviewAssignment? {
    return createCumulativeReviewAssignment(pack, learner, 3, id, boundary)
}

internal fun createCumulativeReviewAssignment(pack: V2CurriculumPack, learner: V2LearnerView,
                                             targetUnit: Int, id: String, boundary: Long): CumulativeReviewAssignment? {
    val policy = cumulativeReviewPolicy(targetUnit) ?: return null
    if (id.isBlank() || boundary < 0 || pack.schemaVersion != 1 ||
        !cumulativeReviewDeclarationValid(pack, targetUnit)) return null
    V2PackIdentityIndex.warm(pack)
    val target = pack.unit(targetUnit) ?: return null
    if (!target.openable() || unmetCurrentPrerequisites(pack, target, learner).isNotEmpty()) return null
    val taught = currentCompletedUnitNumbers(pack, learner)
    val slots = policy.pool.map { (objectiveId, skill, candidates, remediationStepId) ->
        val unit = pack.units.singleOrNull { u -> u.objectives.any { it.id == objectiveId } } ?: return null
        val o = unit.objectives.single { it.id == objectiveId }
        if (remediationStepId != null && (remediationStepId !in o.instructionStepIds + o.remediationStepIds ||
            pack.step(remediationStepId) == null)) return null
        if (unit.number !in taught || !unit.openable() || unit.number >= targetUnit || !o.gating ||
            o.kind != "RECOGNITION" || o.instructionStepIds.isEmpty() || o.practiceStepIds.isEmpty() ||
            (o.instructionStepIds + o.practiceStepIds).any { !completedStepCounts(pack, learner, it) } ||
            objectiveState(o, learner, pack) != V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS) return null
        val item = candidates.mapNotNull { pack.item(it) }.firstOrNull {
            it.id in o.checkItemIds && it.objectiveId == o.id && it.options.size >= 2 && it.answer in it.options
        } ?: return null
        CumulativeReviewSlot(o.id, skill.name, item.id, unit.number, V2Validator.checkItemContentDigest(item),
            reviewUnitDigest(pack, unit.number), teachingDigest(pack, o))
    }
    val a = CumulativeReviewAssignment(id, targetUnit, policy.id,
        policy.digest, closedResponseRuleDigest(), boundary, slots, "")
    return a.copy(digest = reviewAssignmentDigest(a))
}

internal fun reviewAssignmentCurrent(pack: V2CurriculumPack, a: CumulativeReviewAssignment): Boolean {
    val policy = cumulativeReviewPolicy(a.targetUnit) ?: return false
    if (!cumulativeReviewDeclarationValid(pack, a.targetUnit) || pack.schemaVersion != 1 ||
        a.schema != CUMULATIVE_REVIEW_SCHEMA || a.id.isBlank() || a.boundary < 0 ||
        a.policyId != policy.id || a.policyDigest != policy.digest ||
        a.responseRuleDigest != closedResponseRuleDigest() ||
        a.slots.size != policy.pool.size || a.digest != reviewAssignmentDigest(a)) return false
    return a.slots.zip(policy.pool).all { (s, spec) ->
        val unit = pack.unit(s.ownerUnit) ?: return@all false
        val o = unit.objectives.singleOrNull { it.id == s.objectiveId } ?: return@all false
        val item = pack.item(s.itemId) ?: return@all false
        unit.openable() && unit.number < a.targetUnit && o.gating && o.kind == "RECOGNITION" &&
            s.objectiveId == spec.objectiveId && s.skill == spec.skill.name && s.itemId in spec.candidateItemIds &&
            (spec.remediationStepId == null || spec.remediationStepId in o.instructionStepIds + o.remediationStepIds) &&
            s.itemId in o.checkItemIds && item.objectiveId == o.id &&
            s.itemDigest == V2Validator.checkItemContentDigest(item) &&
            s.unitDigest == reviewUnitDigest(pack, s.ownerUnit) &&
            s.teachingDigest == teachingDigest(pack, o)
    }
}

internal fun completedReviewSlots(pack: V2CurriculumPack, a: CumulativeReviewAssignment,
                                 answers: List<CumulativeReviewAnswer>): Set<Int> {
    if (!reviewAssignmentCurrent(pack, a) ||
        reviewHistoryDefects(listOf(a), answers.filter { it.assignmentId == a.id }) != null) return emptySet()
    return a.slots.indices.filter { index ->
        val answer = answers.filter { it.assignmentId == a.id && it.assignmentDigest == a.digest &&
            it.slotIndex == index && it.schema == CUMULATIVE_REVIEW_SCHEMA && it.role == CUMULATIVE_REVIEW_ROLE &&
            it.acceptedOrder > a.boundary }.maxByOrNull { it.acceptedOrder } ?: return@filter false
        answer.selected == pack.item(a.slots[index].itemId)?.answer
    }.toSet()
}

/** Structural backup validation preserves stale histories but refuses forged links/order/hash. */
internal fun reviewHistoryDefects(assignments: List<CumulativeReviewAssignment>,
                                 answers: List<CumulativeReviewAnswer>): String? {
    if (assignments.map { it.id }.distinct().size != assignments.size ||
        answers.map { it.id }.distinct().size != answers.size ||
        answers.map { it.acceptedOrder }.distinct().size != answers.size) return "duplicate review identity/order"
    val maximum = answers.maxOfOrNull { it.acceptedOrder } ?: 0
    val hashValid: (String) -> Boolean = { it.length == 64 && it.all { c -> c in '0'..'9' || c in 'a'..'f' } }
    for (a in assignments) {
        if (a.schema != CUMULATIVE_REVIEW_SCHEMA || a.id.isBlank() || a.targetUnit !in 1..30 ||
            a.boundary < 0 || a.boundary > maximum || a.policyId.isBlank() || !hashValid(a.policyDigest) || !hashValid(a.responseRuleDigest) || a.slots.size !in 2..6 || a.slots.map { it.objectiveId }.distinct().size != a.slots.size ||
            a.digest != reviewAssignmentDigest(a) || a.slots.any { it.ownerUnit !in 1 until a.targetUnit ||
                !hashValid(it.itemDigest) || !hashValid(it.unitDigest) || !hashValid(it.teachingDigest) ||
                it.skill.isBlank() || it.itemId.isBlank() || it.objectiveId.isBlank() }) return "invalid review assignment"
    }
    for (answer in answers) {
        val a = assignments.singleOrNull { it.id == answer.assignmentId } ?: return "missing review assignment"
        if (answer.schema != CUMULATIVE_REVIEW_SCHEMA || answer.role != CUMULATIVE_REVIEW_ROLE ||
            answer.assignmentDigest != a.digest || answer.slotIndex !in a.slots.indices ||
            answer.acceptedOrder <= a.boundary || answer.selected.isBlank() || answer.id.isBlank()) return "invalid review answer"
    }
    return null
}

internal data class CumulativeUnitCompletion(val preActivation: Boolean, val policyDigest: String, val assignmentId: String, val completionOrder: Long)

internal fun unit3ReviewDeclarationValid(pack: V2CurriculumPack): Boolean = cumulativeReviewDeclarationValid(pack, 3)

internal fun currentCumulativeAssignment(pack: V2CurriculumPack, learner: V2LearnerView, targetUnit: Int): CumulativeReviewAssignment? =
    learner.reviewAssignments.lastOrNull { it.targetUnit == targetUnit && reviewAssignmentCurrent(pack, it) }

internal fun currentUnit3Assignment(pack: V2CurriculumPack, learner: V2LearnerView): CumulativeReviewAssignment? =
    currentCumulativeAssignment(pack, learner, 3)

internal fun cumulativeReviewComplete(pack: V2CurriculumPack, learner: V2LearnerView, targetUnit: Int): Boolean {
    if (!cumulativeReviewDeclarationValid(pack, targetUnit)) return false
    val a = currentCumulativeAssignment(pack, learner, targetUnit) ?: return false
    return completedReviewSlots(pack, a, learner.reviewAnswers).size == a.slots.size
}
internal fun unit3ReviewComplete(pack: V2CurriculumPack, learner: V2LearnerView): Boolean = cumulativeReviewComplete(pack, learner, 3)

/** No learner/prerequisite recursion: validate only ordinary old proof or a recorded review snapshot. */
internal fun historicalReviewCompletionCurrent(pack: V2CurriculumPack, unit: V2Unit, learner: V2LearnerView): Boolean {
    val policy = cumulativeReviewPolicy(unit.number) ?: return true
    if (!cumulativeReviewDeclarationValid(pack, unit.number)) return false
    val record = learner.unitReviewCompletions[unit.number] ?: return false
    val emptyLink = record.policyDigest.isEmpty() && record.assignmentId.isEmpty() && record.completionOrder == 0L
    // Schema11 already wrote ordinary Unit4–10 rows with preActivation=false before their activation.
    // Only exact pre-policy governed work may preserve them; current-source raw flags cannot.
    if (emptyLink && (record.preActivation || unit.number in 4..10)) {
        val legacy = preReviewUnitDigest(pack, unit.number)
        val oldEvidence = learner.copy(governedAttempts = learner.governedAttempts?.filter {
            it.unitNumber != unit.number || it.unitContentDigest == legacy
        })
        return ordinaryUnitRequirements(pack, unit, oldEvidence).allowed
    }
    if (record.preActivation || record.policyDigest != policy.digest) return false
    val a = learner.reviewAssignments.singleOrNull { it.id == record.assignmentId } ?: return false
    return a.targetUnit == unit.number && record.completionOrder > a.boundary &&
        completedReviewSlots(pack, a, learner.reviewAnswers.filter { it.acceptedOrder <= record.completionOrder }).size == a.slots.size
}

/** Exact pre-activation rule identity, with every current item/source/teaching field retained. */
private val preReviewUnitDigests = IdentityWeakCache<Map<Int, String>>()
internal fun prescribedUnitIdentityMatches(pack: V2CurriculumPack, number: Int, recorded: String): Boolean {
    val current = V2PackIdentityIndex.unitDigest(pack, number) ?: V2Validator.unitContentReviewDigest(pack, number)
    if (recorded == current) return true
    if (cumulativeReviewPolicy(number) == null || !cumulativeReviewDeclarationValid(pack, number)) return false
    return recorded == preReviewUnitDigest(pack, number)
}

private fun preReviewUnitDigest(pack: V2CurriculumPack, number: Int): String =
    preReviewUnitDigests.get(pack)?.get(number) ?: run {
        val unit = pack.unit(number)!!
        val old = pack.copy(units = pack.units.map { if (it.number == number) unit.copy(advancement = unit.advancement!!.copy(
            requireDueReview = false, cumulativeReviewPolicyId = null)) else it })
        V2Validator.unitContentReviewDigest(old, number).also { digest ->
            preReviewUnitDigests.put(pack, preReviewUnitDigests.get(pack).orEmpty() + (number to digest))
        }
    }
