package dev.russiancompanion.v2.curriculum

import kotlinx.serialization.Serializable
import java.security.MessageDigest

// Keep schema1 defaults for decoding historical documents and for unchanged answers.
internal const val CUMULATIVE_REVIEW_SCHEMA = 1
// New Unit3 assignments use governed latest-miss/least-recent selection; pools stay v1.
internal const val UNIT3_RECENT_SELECTION_ASSIGNMENT_SCHEMA = 2
// Separate extension: schema2 remains Unit3-only, schema3 allocates Units5/6.
internal const val UNITS5_6_RECENT_SELECTION_ASSIGNMENT_SCHEMA = 3
// New Unit6 v2 obligations only; legacy Unit6 schema1/3 meanings remain unchanged.
internal const val UNIT6_WIDER_REVIEW_ASSIGNMENT_SCHEMA = 4
// Unit9 v2 only; all earlier assignment version meanings remain unchanged.
internal const val UNIT9_WIDER_REVIEW_ASSIGNMENT_SCHEMA = 5
// Unit13 v2 only; schemas1–5 retain their exact meanings.
internal const val UNIT13_WIDER_REVIEW_ASSIGNMENT_SCHEMA = 6
// Unit5 gender/subject obligation only; schemas1-6 retain their exact meanings.
internal const val UNIT5_GRAMMAR_REVIEW_ASSIGNMENT_SCHEMA = 7
// Unit6 supplied-present v3 only; all saved schema1-7 obligations remain immutable.
internal const val UNIT6_PRESENT_REVIEW_ASSIGNMENT_SCHEMA = 8
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
internal enum class CumulativeReviewSkill { BOUNDED_READING, PRINTED_LETTER_RECOGNITION, NOUN_GENDER_RECOGNITION, SUPPLIED_NOMINATIVE_SUBJECT_RECOGNITION, SUPPLIED_NOUN_BEHAVIOR_RECOGNITION, SUPPLIED_PRESENT_FORM_RECOGNITION, SUPPLIED_INANIMATE_ACCUSATIVE_RECOGNITION, SUPPLIED_NOMINATIVE_AGREEMENT_RECOGNITION, SUPPLIED_NOMINATIVE_PLURAL_RECOGNITION, SUPPLIED_NUMERAL_GENDER_RECOGNITION, SUPPLIED_POSSESSION_ABSENCE_RECOGNITION, SUPPLIED_PAST_GENDER_RECOGNITION, SUPPLIED_ASPECT_PARTNER_RECOGNITION, SUPPLIED_REPORTED_FRAME_TIME_RECOGNITION, SUPPLIED_IMPERSONAL_STATE_RECOGNITION, SUPPLIED_IMPERATIVE_FORM_RECOGNITION, SUPPLIED_INSTRUMENTAL_PLURAL_RECOGNITION, SUPPLIED_QUESTION_INTERPRETATION, SUPPLIED_RELATIVE_CLAUSE_RECOGNITION, SUPPLIED_CONDITIONAL_AGREEMENT_RECOGNITION, SUPPLIED_MOTION_CONTEXT_RECOGNITION, SUPPLIED_REFLEXIVE_OBJECT_RECOGNITION, SUPPLIED_HALF_QUANTITY_PHRASE_RECOGNITION, SUPPLIED_PERIOD_POSITION_PHRASE_RECOGNITION, SUPPLIED_NEGATIVE_SENTENCE_RECOGNITION, SUPPLIED_NAME_REPLY_RECOGNITION, SUPPLIED_ADDRESS_COMPONENT_RECOGNITION, SUPPLIED_PAST_PASSIVE_MEANING_RECOGNITION, SUPPLIED_RELATIVE_FUTURE_TIMING_RECOGNITION, SUPPLIED_ACTIVE_PARTICIPLE_TIME_RECOGNITION }

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

/** Reuses taught lexical gender and supplied subject-role evidence; not a reading claim. */
internal object Unit5GrammarCumulativeReviewPolicy {
    val definition = CumulativeReviewPolicy("unit5-prior-gender-subject-v2", 5, listOf(
        CumulativeReviewObjectivePool("obj-u3-gender", CumulativeReviewSkill.NOUN_GENDER_RECOGNITION,
            listOf("u3-i-gender-город", "u3-i-gender-друг", "u3-i-gender-дверь", "u3-i-gender-море", "u3-i-gender-дядя"),
            "u3-st-rem-gender"),
        Unit5CumulativeReviewPolicy.definition.pool[1],
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

/** Separate authored obligation; retain the v1 definition and all its saved tasks. */
internal object Unit6WiderCumulativeReviewPolicy {
    val definition = CumulativeReviewPolicy("unit6-prior-reading-subject-noun-behavior-v2", 6,
        Unit6CumulativeReviewPolicy.definition.pool + Unit5CumulativeReviewPolicy.definition.pool[1])
}

/** Finite supplied-form/person recognition; not productive conjugation or reading. */
internal object Unit6PresentCumulativeReviewPolicy {
    val definition = CumulativeReviewPolicy("unit6-prior-present-subject-noun-behavior-v3", 6, listOf(
        CumulativeReviewObjectivePool("obj-u4-present", CumulativeReviewSkill.SUPPLIED_PRESENT_FORM_RECOGNITION,
            listOf("u4-i-verb-говорить-мы", "u4-i-verb-видеть-он", "u4-i-verb-писать-вы", "u4-i-verb-идти-они"),
            "u4-st-rem-present"),
        Unit6CumulativeReviewPolicy.definition.pool[1],
        Unit5CumulativeReviewPolicy.definition.pool[1],
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

/** Revisits supplied lexical gender in the later numeral-agreement context. */
internal object Unit9WiderCumulativeReviewPolicy {
    val definition = CumulativeReviewPolicy("unit9-prior-reading-plural-gender-v2", 9,
        Unit9CumulativeReviewPolicy.definition.pool + CumulativeReviewObjectivePool(
            "obj-u3-gender", CumulativeReviewSkill.NOUN_GENDER_RECOGNITION,
            listOf("u3-i-gender-город", "u3-i-gender-друг", "u3-i-gender-дверь", "u3-i-gender-море", "u3-i-gender-дядя"),
            "u3-st-rem-gender"))
}

internal object Unit10CumulativeReviewPolicy {
    val definition = CumulativeReviewPolicy("unit10-prior-reading-numeral-gender-v1", 10, listOf(
        CumulativeReviewObjectivePool("obj-u1-decoding", CumulativeReviewSkill.BOUNDED_READING, listOf("u1-i-decode-pair")),
        CumulativeReviewObjectivePool("obj-u9-odindva", CumulativeReviewSkill.SUPPLIED_NUMERAL_GENDER_RECOGNITION,
            listOf("u9-i-odindva-4"), "u9-st-rem-odindva"),
    ))
}

internal object Unit11CumulativeReviewPolicy {
    val definition = CumulativeReviewPolicy("unit11-prior-reading-possession-absence-v1", 11, listOf(
        CumulativeReviewObjectivePool("obj-u1-decoding", CumulativeReviewSkill.BOUNDED_READING, listOf("u1-i-decode-pair")),
        CumulativeReviewObjectivePool("obj-u10-have", CumulativeReviewSkill.SUPPLIED_POSSESSION_ABSENCE_RECOGNITION,
            listOf("u10-i-have-4"), "u10-st-rem-have"),
    ))
}

internal object Unit12CumulativeReviewPolicy {
    val definition = CumulativeReviewPolicy("unit12-prior-reading-past-gender-v1", 12, listOf(
        CumulativeReviewObjectivePool("obj-u1-decoding", CumulativeReviewSkill.BOUNDED_READING, listOf("u1-i-decode-pair")),
        CumulativeReviewObjectivePool("obj-u11-past-aspect", CumulativeReviewSkill.SUPPLIED_PAST_GENDER_RECOGNITION,
            listOf("u11-i-past-aspect-1"), "u11-st-rem-past-aspect"),
    ))
}

internal object Unit13CumulativeReviewPolicy {
    val definition = CumulativeReviewPolicy("unit13-prior-reading-aspect-partner-v1", 13, listOf(
        CumulativeReviewObjectivePool("obj-u1-decoding", CumulativeReviewSkill.BOUNDED_READING, listOf("u1-i-decode-pair")),
        CumulativeReviewObjectivePool("obj-u12-aspect", CumulativeReviewSkill.SUPPLIED_ASPECT_PARTNER_RECOGNITION,
            listOf("u12-i-aspect-1"), "u12-st-rem-aspect"),
    ))
}

/** Revisits already-taught past agreement in the later past/aspect context. */
internal object Unit13WiderCumulativeReviewPolicy {
    val definition = CumulativeReviewPolicy("unit13-prior-reading-aspect-past-gender-v2", 13,
        Unit13CumulativeReviewPolicy.definition.pool + CumulativeReviewObjectivePool(
            "obj-u11-past-aspect", CumulativeReviewSkill.SUPPLIED_PAST_GENDER_RECOGNITION,
            listOf("u11-i-past-aspect-1", "u11-i-past-aspect-2"), "u11-st-rem-past-aspect"))
}

internal object Unit14CumulativeReviewPolicy {
    val definition = CumulativeReviewPolicy("unit14-prior-reading-reported-time-v1", 14, listOf(
        CumulativeReviewObjectivePool("obj-u1-decoding", CumulativeReviewSkill.BOUNDED_READING, listOf("u1-i-decode-pair")),
        CumulativeReviewObjectivePool("obj-u13-reported", CumulativeReviewSkill.SUPPLIED_REPORTED_FRAME_TIME_RECOGNITION,
            listOf("u13-i-report-frame-time"), "u13-st-rem-reported"),
    ))
}

internal object Unit15CumulativeReviewPolicy {
    val definition = CumulativeReviewPolicy("unit15-prior-reading-impersonal-state-v1", 15, listOf(
        CumulativeReviewObjectivePool("obj-u1-decoding", CumulativeReviewSkill.BOUNDED_READING, listOf("u1-i-decode-pair")),
        CumulativeReviewObjectivePool("obj-u14-impersonal", CumulativeReviewSkill.SUPPLIED_IMPERSONAL_STATE_RECOGNITION,
            listOf("u14-i-impersonal-2"), "u14-st-rem-impersonal"),
    ))
}

internal object Unit16CumulativeReviewPolicy {
    val definition = CumulativeReviewPolicy("unit16-prior-reading-imperative-form-v1", 16, listOf(
        CumulativeReviewObjectivePool("obj-u1-decoding", CumulativeReviewSkill.BOUNDED_READING, listOf("u1-i-decode-pair")),
        CumulativeReviewObjectivePool("obj-u15-imperative", CumulativeReviewSkill.SUPPLIED_IMPERATIVE_FORM_RECOGNITION,
            listOf("u15-i-imperative-1"), "u15-st-rem-imperative"),
    ))
}

internal object Unit17CumulativeReviewPolicy {
    val definition = CumulativeReviewPolicy("unit17-prior-reading-instrumental-plural-v1", 17, listOf(
        CumulativeReviewObjectivePool("obj-u1-decoding", CumulativeReviewSkill.BOUNDED_READING, listOf("u1-i-decode-pair")),
        CumulativeReviewObjectivePool("obj-u16-inst-pl", CumulativeReviewSkill.SUPPLIED_INSTRUMENTAL_PLURAL_RECOGNITION,
            listOf("u16-i-inst-pl-1"), "u16-st-rem-inst-pl"),
    ))
}

internal object Unit18CumulativeReviewPolicy {
    val definition = CumulativeReviewPolicy("unit18-prior-reading-question-meaning-v1", 18, listOf(
        CumulativeReviewObjectivePool("obj-u1-decoding", CumulativeReviewSkill.BOUNDED_READING, listOf("u1-i-decode-pair")),
        CumulativeReviewObjectivePool("obj-u17-clock", CumulativeReviewSkill.SUPPLIED_QUESTION_INTERPRETATION,
            listOf("u17-i-question-book"), "u17-st-rem-clock"),
    ))
}

internal object Unit19CumulativeReviewPolicy {
    val definition = CumulativeReviewPolicy("unit19-prior-reading-relative-meaning-v1", 19, listOf(
        CumulativeReviewObjectivePool("obj-u1-decoding", CumulativeReviewSkill.BOUNDED_READING, listOf("u1-i-decode-pair")),
        CumulativeReviewObjectivePool("obj-u18-relative", CumulativeReviewSkill.SUPPLIED_RELATIVE_CLAUSE_RECOGNITION,
            listOf("u18-i-relative-1"), "u18-st-rem-relative"),
    ))
}

internal object Unit20CumulativeReviewPolicy {
    val definition = CumulativeReviewPolicy("unit20-prior-reading-conditional-agreement-v1", 20, listOf(
        CumulativeReviewObjectivePool("obj-u1-decoding", CumulativeReviewSkill.BOUNDED_READING, listOf("u1-i-decode-pair")),
        CumulativeReviewObjectivePool("obj-u19-conditional", CumulativeReviewSkill.SUPPLIED_CONDITIONAL_AGREEMENT_RECOGNITION,
            listOf("u19-i-conditional-1"), "u19-st-rem-conditional"),
    ))
}

internal object Unit21CumulativeReviewPolicy {
    val definition = CumulativeReviewPolicy("unit21-prior-reading-motion-context-v1", 21, listOf(
        CumulativeReviewObjectivePool("obj-u1-decoding", CumulativeReviewSkill.BOUNDED_READING, listOf("u1-i-decode-pair")),
        CumulativeReviewObjectivePool("obj-u20-motion", CumulativeReviewSkill.SUPPLIED_MOTION_CONTEXT_RECOGNITION,
            listOf("u20-i-direction-city"), "u20-st-rem-motion"),
    ))
}

internal object Unit22CumulativeReviewPolicy {
    val definition = CumulativeReviewPolicy("unit22-prior-reading-reflexive-object-v1", 22, listOf(
        CumulativeReviewObjectivePool("obj-u1-decoding", CumulativeReviewSkill.BOUNDED_READING, listOf("u1-i-decode-pair")),
        CumulativeReviewObjectivePool("obj-u21-object-reference", CumulativeReviewSkill.SUPPLIED_REFLEXIVE_OBJECT_RECOGNITION,
            listOf("u21-i-object-reference-2"), "u21-st-rem-object-reference"),
    ))
}

internal object Unit23CumulativeReviewPolicy {
    val definition = CumulativeReviewPolicy("unit23-prior-reading-half-quantity-phrase-v1", 23, listOf(
        CumulativeReviewObjectivePool("obj-u1-decoding", CumulativeReviewSkill.BOUNDED_READING, listOf("u1-i-decode-pair")),
        CumulativeReviewObjectivePool("obj-u22-half-forms", CumulativeReviewSkill.SUPPLIED_HALF_QUANTITY_PHRASE_RECOGNITION,
            listOf("u22-i-half-forms-2"), "u22-st-rem-half-forms"),
    ))
}

internal object Unit24CumulativeReviewPolicy {
    val definition = CumulativeReviewPolicy("unit24-prior-reading-period-position-v1", 24, listOf(
        CumulativeReviewObjectivePool("obj-u1-decoding", CumulativeReviewSkill.BOUNDED_READING, listOf("u1-i-decode-pair")),
        CumulativeReviewObjectivePool("obj-u23-beginning", CumulativeReviewSkill.SUPPLIED_PERIOD_POSITION_PHRASE_RECOGNITION,
            listOf("u23-i-beginning-2"), "u23-st-rem-beginning"),
    ))
}

internal object Unit25CumulativeReviewPolicy {
    val definition = CumulativeReviewPolicy("unit25-prior-reading-negative-sentence-v1", 25, listOf(
        CumulativeReviewObjectivePool("obj-u1-decoding", CumulativeReviewSkill.BOUNDED_READING, listOf("u1-i-decode-pair")),
        CumulativeReviewObjectivePool("obj-u24-verbal-ne", CumulativeReviewSkill.SUPPLIED_NEGATIVE_SENTENCE_RECOGNITION,
            listOf("u24-i-verbal-ne-2"), "u24-st-rem-verbal-ne"),
    ))
}

internal object Unit26CumulativeReviewPolicy {
    val definition = CumulativeReviewPolicy("unit26-prior-reading-name-reply-v1", 26, listOf(
        CumulativeReviewObjectivePool("obj-u1-decoding", CumulativeReviewSkill.BOUNDED_READING, listOf("u1-i-decode-pair")),
        CumulativeReviewObjectivePool("obj-u25-name-question", CumulativeReviewSkill.SUPPLIED_NAME_REPLY_RECOGNITION,
            listOf("u25-i-name-question-2"), "u25-st-rem-name-question"),
    ))
}

internal object Unit27CumulativeReviewPolicy {
    val definition = CumulativeReviewPolicy("unit27-prior-reading-address-component-v1", 27, listOf(
        CumulativeReviewObjectivePool("obj-u1-decoding", CumulativeReviewSkill.BOUNDED_READING, listOf("u1-i-decode-pair")),
        CumulativeReviewObjectivePool("obj-u26-address-notation", CumulativeReviewSkill.SUPPLIED_ADDRESS_COMPONENT_RECOGNITION,
            listOf("u26-i-address-notation-2"), "u26-st-rem-address-notation"),
    ))
}

internal object Unit28CumulativeReviewPolicy {
    val definition = CumulativeReviewPolicy("unit28-prior-reading-past-passive-meaning-v1", 28, listOf(
        CumulativeReviewObjectivePool("obj-u1-decoding", CumulativeReviewSkill.BOUNDED_READING, listOf("u1-i-decode-pair")),
        CumulativeReviewObjectivePool("obj-u27-passive", CumulativeReviewSkill.SUPPLIED_PAST_PASSIVE_MEANING_RECOGNITION,
            listOf("u27-i-passive-1"), "u27-st-rem-passive"),
    ))
}

internal object Unit29CumulativeReviewPolicy {
    val definition = CumulativeReviewPolicy("unit29-prior-reading-relative-future-timing-v1", 29, listOf(
        CumulativeReviewObjectivePool("obj-u1-decoding", CumulativeReviewSkill.BOUNDED_READING, listOf("u1-i-decode-pair")),
        CumulativeReviewObjectivePool("obj-u28-perfective-adverb", CumulativeReviewSkill.SUPPLIED_RELATIVE_FUTURE_TIMING_RECOGNITION,
            listOf("u28-i-perfective-adverb-1"), "u28-st-rem-perfective-adverb"),
    ))
}

internal object Unit30CumulativeReviewPolicy {
    val definition = CumulativeReviewPolicy("unit30-prior-reading-active-participle-time-v1", 30, listOf(
        CumulativeReviewObjectivePool("obj-u1-decoding", CumulativeReviewSkill.BOUNDED_READING, listOf("u1-i-decode-pair")),
        CumulativeReviewObjectivePool("obj-u29-active", CumulativeReviewSkill.SUPPLIED_ACTIVE_PARTICIPLE_TIME_RECOGNITION,
            listOf("u29-i-active-2"), "u29-st-rem-active"),
    ))
}

/** Authored engineering slices only; every remaining target has no policy. */
internal fun cumulativeReviewPolicy(targetUnit: Int): CumulativeReviewPolicy? = when (targetUnit) {
    3 -> Unit3CumulativeReviewPolicy.definition
    4 -> Unit4CumulativeReviewPolicy.definition
    5 -> Unit5GrammarCumulativeReviewPolicy.definition
    6 -> Unit6PresentCumulativeReviewPolicy.definition
    7 -> Unit7CumulativeReviewPolicy.definition
    8 -> Unit8CumulativeReviewPolicy.definition
    9 -> Unit9WiderCumulativeReviewPolicy.definition
    10 -> Unit10CumulativeReviewPolicy.definition
    11 -> Unit11CumulativeReviewPolicy.definition
    12 -> Unit12CumulativeReviewPolicy.definition
    13 -> Unit13WiderCumulativeReviewPolicy.definition
    14 -> Unit14CumulativeReviewPolicy.definition
    15 -> Unit15CumulativeReviewPolicy.definition
    16 -> Unit16CumulativeReviewPolicy.definition
    17 -> Unit17CumulativeReviewPolicy.definition
    18 -> Unit18CumulativeReviewPolicy.definition
    19 -> Unit19CumulativeReviewPolicy.definition
    20 -> Unit20CumulativeReviewPolicy.definition
    21 -> Unit21CumulativeReviewPolicy.definition
    22 -> Unit22CumulativeReviewPolicy.definition
    23 -> Unit23CumulativeReviewPolicy.definition
    24 -> Unit24CumulativeReviewPolicy.definition
    25 -> Unit25CumulativeReviewPolicy.definition
    26 -> Unit26CumulativeReviewPolicy.definition
    27 -> Unit27CumulativeReviewPolicy.definition
    28 -> Unit28CumulativeReviewPolicy.definition
    29 -> Unit29CumulativeReviewPolicy.definition
    30 -> Unit30CumulativeReviewPolicy.definition
    else -> null
}

internal fun cumulativeReviewTitle(targetUnit: Int, policyId: String? = null): String = when (targetUnit) {
    3 -> "Review earlier reading and letters"
    4 -> "Review earlier reading and noun genders"
    5 -> if (policyId == Unit5CumulativeReviewPolicy.definition.id) "Review earlier reading and supplied subjects"
        else "Review earlier noun genders and supplied subjects"
    6 -> if (policyId == Unit6CumulativeReviewPolicy.definition.id || policyId == Unit6WiderCumulativeReviewPolicy.definition.id)
        "Review earlier reading and nouns"
        else "Review supplied present forms, noun behavior and subjects"
    7 -> "Review earlier reading and supplied object forms"
    8 -> "Review earlier reading and supplied agreement"
    9 -> "Review earlier reading, supplied plurals and noun genders"
    10 -> "Review earlier reading and supplied numeral agreement"
    11 -> "Review earlier reading and supplied possession or absence"
    12 -> "Review earlier reading and supplied past gender"
    13 -> "Review earlier reading, supplied aspect partners and past agreement"
    14 -> "Review earlier reading and supplied reporting times"
    15 -> "Review earlier reading and supplied impersonal states"
    16 -> "Review earlier reading and supplied imperative forms"
    17 -> "Review earlier reading and supplied instrumental plurals"
    18 -> "Review earlier reading and supplied question meanings"
    19 -> "Review earlier reading and supplied relative-clause meanings"
    20 -> "Review earlier reading and supplied conditional agreement"
    21 -> "Review earlier reading and supplied motion contexts"
    22 -> "Review earlier reading and supplied reflexive objects"
    23 -> "Review earlier reading and supplied quantity phrases"
    24 -> "Review earlier reading and supplied time expressions"
    25 -> "Review earlier reading and supplied negative sentences"
    26 -> "Review earlier reading and supplied conversation formulas"
    27 -> "Review earlier reading and supplied address components"
    28 -> "Review earlier reading and supplied past passive meanings"
    29 -> "Review earlier reading and supplied relative timing"
    30 -> "Review earlier reading and supplied active participle forms"
    else -> "Review earlier objectives"
}

/** Explicit supported declarations, including genuine prior source packs. */
internal fun cumulativeReviewDeclaredPolicy(pack: V2CurriculumPack, targetUnit: Int): CumulativeReviewPolicy? {
    val rule = pack.unit(targetUnit)?.advancement ?: return null
    if (!rule.requireDueReview) return null
    val active = cumulativeReviewPolicy(targetUnit) ?: return null
    return when {
        rule.cumulativeReviewPolicyId == active.id -> active
        targetUnit == 5 && rule.cumulativeReviewPolicyId == Unit5CumulativeReviewPolicy.definition.id ->
            Unit5CumulativeReviewPolicy.definition
        targetUnit == 6 && rule.cumulativeReviewPolicyId == Unit6WiderCumulativeReviewPolicy.definition.id ->
            Unit6WiderCumulativeReviewPolicy.definition
        targetUnit == 6 && rule.cumulativeReviewPolicyId == Unit6CumulativeReviewPolicy.definition.id ->
            Unit6CumulativeReviewPolicy.definition
        targetUnit == 9 && rule.cumulativeReviewPolicyId == Unit9CumulativeReviewPolicy.definition.id ->
            Unit9CumulativeReviewPolicy.definition
        targetUnit == 13 && rule.cumulativeReviewPolicyId == Unit13CumulativeReviewPolicy.definition.id ->
            Unit13CumulativeReviewPolicy.definition
        else -> null
    }
}

internal fun cumulativeReviewDeclarationValid(pack: V2CurriculumPack, targetUnit: Int): Boolean =
    cumulativeReviewDeclaredPolicy(pack, targetUnit) != null

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

private fun newReviewAssignmentSchema(policy: CumulativeReviewPolicy): Int = when (policy.targetUnit) {
    3 -> UNIT3_RECENT_SELECTION_ASSIGNMENT_SCHEMA
    5 -> if (policy.id == Unit5GrammarCumulativeReviewPolicy.definition.id) UNIT5_GRAMMAR_REVIEW_ASSIGNMENT_SCHEMA
        else UNITS5_6_RECENT_SELECTION_ASSIGNMENT_SCHEMA
    6 -> when (policy.id) {
        Unit6PresentCumulativeReviewPolicy.definition.id -> UNIT6_PRESENT_REVIEW_ASSIGNMENT_SCHEMA
        Unit6WiderCumulativeReviewPolicy.definition.id -> UNIT6_WIDER_REVIEW_ASSIGNMENT_SCHEMA
        else -> UNITS5_6_RECENT_SELECTION_ASSIGNMENT_SCHEMA
    }
    9 -> if (policy.id == Unit9WiderCumulativeReviewPolicy.definition.id) UNIT9_WIDER_REVIEW_ASSIGNMENT_SCHEMA
        else CUMULATIVE_REVIEW_SCHEMA
    13 -> if (policy.id == Unit13WiderCumulativeReviewPolicy.definition.id) UNIT13_WIDER_REVIEW_ASSIGNMENT_SCHEMA
        else CUMULATIVE_REVIEW_SCHEMA
    else -> CUMULATIVE_REVIEW_SCHEMA
}

/** Preserve each version's target scope; unknown combinations cannot acquire credit. */
private fun reviewAssignmentSchemaSupported(a: CumulativeReviewAssignment): Boolean = when (a.schema) {
    CUMULATIVE_REVIEW_SCHEMA -> a.policyId != Unit5GrammarCumulativeReviewPolicy.definition.id &&
        a.policyId != Unit6WiderCumulativeReviewPolicy.definition.id &&
        a.policyId != Unit6PresentCumulativeReviewPolicy.definition.id &&
        a.policyId != Unit9WiderCumulativeReviewPolicy.definition.id &&
        a.policyId != Unit13WiderCumulativeReviewPolicy.definition.id
    UNIT3_RECENT_SELECTION_ASSIGNMENT_SCHEMA -> a.targetUnit == 3
    UNITS5_6_RECENT_SELECTION_ASSIGNMENT_SCHEMA -> (a.targetUnit == 5 && a.policyId == Unit5CumulativeReviewPolicy.definition.id) ||
        (a.targetUnit == 6 && a.policyId == Unit6CumulativeReviewPolicy.definition.id)
    UNIT6_WIDER_REVIEW_ASSIGNMENT_SCHEMA -> a.targetUnit == 6 &&
        a.policyId == Unit6WiderCumulativeReviewPolicy.definition.id
    UNIT9_WIDER_REVIEW_ASSIGNMENT_SCHEMA -> a.targetUnit == 9 &&
        a.policyId == Unit9WiderCumulativeReviewPolicy.definition.id
    UNIT13_WIDER_REVIEW_ASSIGNMENT_SCHEMA -> a.targetUnit == 13 &&
        a.policyId == Unit13WiderCumulativeReviewPolicy.definition.id
    UNIT5_GRAMMAR_REVIEW_ASSIGNMENT_SCHEMA -> a.targetUnit == 5 &&
        a.policyId == Unit5GrammarCumulativeReviewPolicy.definition.id
    UNIT6_PRESENT_REVIEW_ASSIGNMENT_SCHEMA -> a.targetUnit == 6 &&
        a.policyId == Unit6PresentCumulativeReviewPolicy.definition.id
    else -> false
}

/** Explicit history registry: an unknown policy/version never supplies current credit. */
internal fun cumulativeReviewAssignmentPolicy(a: CumulativeReviewAssignment): CumulativeReviewPolicy? {
    if (!reviewAssignmentSchemaSupported(a)) return null
    val definitions = listOfNotNull(cumulativeReviewPolicy(a.targetUnit)) +
        when (a.targetUnit) {
            5 -> listOf(Unit5CumulativeReviewPolicy.definition)
            6 -> listOf(Unit6CumulativeReviewPolicy.definition, Unit6WiderCumulativeReviewPolicy.definition)
            9 -> listOf(Unit9CumulativeReviewPolicy.definition)
            13 -> listOf(Unit13CumulativeReviewPolicy.definition)
            else -> emptyList()
        }
    return definitions.singleOrNull { it.id == a.policyId && it.digest == a.policyDigest }
}

/** Allocation only: these attempts never supply assignment-bound due credit. */
internal fun recentReviewCandidate(pack: V2CurriculumPack, learner: V2LearnerView,
                                  candidates: List<V2CheckItem>): V2CheckItem? {
    val attempts = learner.governedAttempts.orEmpty()
    // Malformed duplicate sequence numbers have no deterministic insertion order.
    val ambiguous = attempts.groupingBy { it.acceptedOrder }.eachCount().filterValues { it > 1 }.keys
    val ids = candidates.map { it.id }.toSet()
    val latest = attempts.filter { a ->
        a.itemId in ids && a.acceptedOrder > 0 && a.acceptedOrder !in ambiguous &&
            pack.step(a.stepId)?.second?.kind == "CHECK" &&
            attemptSuppliesStepSelection(a, pack, a.stepId) &&
            pack.item(a.itemId)?.let { a.objectiveId == it.objectiveId && a.correct == (a.selected == it.answer) } == true
    }.groupBy { it.itemId }.mapValues { (_, rows) -> rows.maxBy { it.acceptedOrder } }
    return candidates.withIndex().minWithOrNull(compareBy<IndexedValue<V2CheckItem>>(
        { if (latest[it.value.id]?.correct == false) 0 else 1 },
        { latest[it.value.id]?.let { a -> if (a.correct) a.acceptedOrder else -a.acceptedOrder } ?: 0L },
        { it.index },
    ))?.value
}

/** Requires actual taught evidence; placement and bare historical completion are insufficient. */
internal fun createUnit3ReviewAssignment(pack: V2CurriculumPack, learner: V2LearnerView,
                                       id: String, boundary: Long): CumulativeReviewAssignment? {
    return createCumulativeReviewAssignment(pack, learner, 3, id, boundary)
}

internal fun createCumulativeReviewAssignment(pack: V2CurriculumPack, learner: V2LearnerView,
                                             targetUnit: Int, id: String, boundary: Long): CumulativeReviewAssignment? {
    val policy = cumulativeReviewDeclaredPolicy(pack, targetUnit) ?: return null
    val assignmentSchema = newReviewAssignmentSchema(policy)
    if (id.isBlank() || boundary < 0 || pack.schemaVersion != 1 ||
        !cumulativeReviewDeclarationValid(pack, targetUnit)) return null
    val candidates = eligibleReviewCandidates(pack, learner, policy) ?: return null
    val slots = policy.pool.zip(candidates).map { (spec, eligible) ->
        val item = (if (assignmentSchema != CUMULATIVE_REVIEW_SCHEMA) recentReviewCandidate(pack, learner, eligible)
            else eligible.firstOrNull()) ?: return null
        val unit = pack.units.single { u -> u.objectives.any { it.id == spec.objectiveId } }
        val o = unit.objectives.single { it.id == spec.objectiveId }
        CumulativeReviewSlot(o.id, spec.skill.name, item.id, unit.number, V2Validator.checkItemContentDigest(item),
            reviewUnitDigest(pack, unit.number), teachingDigest(pack, o))
    }
    val a = CumulativeReviewAssignment(id, targetUnit, policy.id,
        policy.digest, presentedReviewResponseRuleDigest(), boundary, slots, "",
        schema = assignmentSchema)
    return a.copy(digest = reviewAssignmentDigest(a))
}

/** Shared taught eligibility, evaluated against the task's own registered obligation. */
private fun eligibleReviewCandidates(pack: V2CurriculumPack, learner: V2LearnerView,
                                     policy: CumulativeReviewPolicy): List<List<V2CheckItem>>? {
    if (pack.schemaVersion != 1 || !cumulativeReviewDeclarationValid(pack, policy.targetUnit)) return null
    V2PackIdentityIndex.warm(pack)
    val target = pack.unit(policy.targetUnit) ?: return null
    if (!target.openable() || unmetCurrentPrerequisites(pack, target, learner).isNotEmpty()) return null
    val taught = currentCompletedUnitNumbers(pack, learner)
    return policy.pool.map { (objectiveId, _, candidates, remediationStepId) ->
        val unit = pack.units.singleOrNull { u -> u.objectives.any { it.id == objectiveId } } ?: return null
        val o = unit.objectives.single { it.id == objectiveId }
        if (remediationStepId != null && (remediationStepId !in o.instructionStepIds + o.remediationStepIds ||
            pack.step(remediationStepId) == null)) return null
        if (unit.number !in taught || !unit.openable() || unit.number >= policy.targetUnit || !o.gating ||
            o.kind != "RECOGNITION" || o.instructionStepIds.isEmpty() || o.practiceStepIds.isEmpty() ||
            (o.instructionStepIds + o.practiceStepIds).any { !completedStepCounts(pack, learner, it) } ||
            objectiveState(o, learner, pack) != V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS) return null
        val eligible = candidates.mapNotNull { pack.item(it) }.filter {
            it.id in o.checkItemIds && it.objectiveId == o.id && it.options.size >= 2 && it.answer in it.options
        }
        if (eligible.isEmpty()) return null
        eligible
    }
}

internal fun reviewAssignmentEligible(pack: V2CurriculumPack, learner: V2LearnerView,
                                      a: CumulativeReviewAssignment): Boolean {
    if (!reviewAssignmentCurrent(pack, a)) return false
    val policy = cumulativeReviewAssignmentPolicy(a) ?: return false
    val eligible = eligibleReviewCandidates(pack, learner, policy) ?: return false
    return a.slots.zip(eligible).all { (slot, items) -> items.any { it.id == slot.itemId } }
}

internal fun reviewAssignmentCurrent(pack: V2CurriculumPack, a: CumulativeReviewAssignment): Boolean {
    val policy = cumulativeReviewAssignmentPolicy(a) ?: return false
    val declared = cumulativeReviewDeclaredPolicy(pack, a.targetUnit) ?: return false
    if (policy.id != declared.id && !(a.targetUnit == 5 &&
        declared.id == Unit5GrammarCumulativeReviewPolicy.definition.id && policy.id == Unit5CumulativeReviewPolicy.definition.id) &&
        !(a.targetUnit == 6 && (
        declared.id == Unit6WiderCumulativeReviewPolicy.definition.id && policy.id == Unit6CumulativeReviewPolicy.definition.id ||
        declared.id == Unit6PresentCumulativeReviewPolicy.definition.id && policy.id in setOf(
            Unit6CumulativeReviewPolicy.definition.id, Unit6WiderCumulativeReviewPolicy.definition.id))) &&
        !(a.targetUnit == 9 && declared.id == Unit9WiderCumulativeReviewPolicy.definition.id &&
            policy.id == Unit9CumulativeReviewPolicy.definition.id) &&
        !(a.targetUnit == 13 && declared.id == Unit13WiderCumulativeReviewPolicy.definition.id &&
            policy.id == Unit13CumulativeReviewPolicy.definition.id)) return false
    if (!cumulativeReviewDeclarationValid(pack, a.targetUnit) || pack.schemaVersion != 1 ||
        !reviewAssignmentSchemaSupported(a) || a.id.isBlank() || a.boundary < 0 ||
        a.policyId != policy.id || a.policyDigest != policy.digest ||
        !reviewResponseRuleSupported(a.responseRuleDigest) ||
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
            currentOrPriorActiveReviewUnitIdentityMatches(pack, s.ownerUnit, s.unitDigest) &&
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
        if (!reviewAssignmentSchemaSupported(a) || a.id.isBlank() || a.targetUnit !in 1..30 ||
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
    if (!reviewAssignmentEligible(pack, learner, a)) return false
    return completedReviewSlots(pack, a, learner.reviewAnswers).size == a.slots.size
}
internal fun unit3ReviewComplete(pack: V2CurriculumPack, learner: V2LearnerView): Boolean = cumulativeReviewComplete(pack, learner, 3)

/** No learner/prerequisite recursion: validate only ordinary old proof or a recorded review snapshot. */
internal fun historicalReviewCompletionCurrent(pack: V2CurriculumPack, unit: V2Unit, learner: V2LearnerView): Boolean {
    if (cumulativeReviewPolicy(unit.number) == null) return true
    if (!cumulativeReviewDeclarationValid(pack, unit.number)) return false
    val record = learner.unitReviewCompletions[unit.number] ?: return false
    val emptyLink = record.policyDigest.isEmpty() && record.assignmentId.isEmpty() && record.completionOrder == 0L
    // Schema11 already wrote ordinary Unit4–30 rows with preActivation=false before their activation.
    // Only exact pre-policy governed work may preserve them; current-source raw flags cannot.
    if (emptyLink && (record.preActivation || unit.number in 4..30)) {
        val legacy = preReviewUnitDigest(pack, unit.number)
        val oldEvidence = learner.copy(governedAttempts = learner.governedAttempts?.filter {
            it.unitNumber != unit.number || it.unitContentDigest == legacy
        })
        return ordinaryUnitRequirements(pack, unit, oldEvidence).allowed
    }
    if (record.preActivation) return false
    val a = learner.reviewAssignments.singleOrNull { it.id == record.assignmentId } ?: return false
    if (record.policyDigest != a.policyDigest) return false
    return a.targetUnit == unit.number && record.completionOrder > a.boundary &&
        completedReviewSlots(pack, a, learner.reviewAnswers.filter { it.acceptedOrder <= record.completionOrder }).size == a.slots.size
}

/** Exact pre-activation rule identity, with every current item/source/teaching field retained. */
private val preReviewUnitDigests = IdentityWeakCache<Map<Int, String>>()
internal fun prescribedUnitIdentityMatches(pack: V2CurriculumPack, number: Int, recorded: String): Boolean {
    if (currentOrPriorActiveReviewUnitIdentityMatches(pack, number, recorded)) return true
    if (cumulativeReviewPolicy(number) == null || !cumulativeReviewDeclarationValid(pack, number)) return false
    return recorded == preReviewUnitDigest(pack, number)
}

/** Unlike prescribed work, review owner bindings do not accept pre-activation identities. */
private fun currentOrPriorActiveReviewUnitIdentityMatches(pack: V2CurriculumPack, number: Int, recorded: String): Boolean {
    val current = V2PackIdentityIndex.unitDigest(pack, number) ?: V2Validator.unitContentReviewDigest(pack, number)
    if (recorded == current) return true
    return when (number) {
        5 -> cumulativeReviewDeclarationValid(pack, 5) && recorded == priorActiveUnit5Digest(pack)
        6 -> cumulativeReviewDeclarationValid(pack, 6) && recorded in priorActiveUnit6Digests(pack)
        9 -> cumulativeReviewDeclarationValid(pack, 9) && recorded == priorActiveUnit9Digest(pack)
        13 -> cumulativeReviewDeclarationValid(pack, 13) && recorded == priorActiveUnit13Digest(pack)
        else -> false
    }
}

private val priorActiveUnit5Digests = IdentityWeakCache<String>()
private fun priorActiveUnit5Digest(pack: V2CurriculumPack): String = priorActiveUnit5Digests.get(pack) ?: run {
    val old = pack.copy(units = pack.units.map { unit -> if (unit.number == 5) unit.copy(
        advancement = unit.advancement!!.copy(requireDueReview = true,
            cumulativeReviewPolicyId = Unit5CumulativeReviewPolicy.definition.id)) else unit })
    V2Validator.unitContentReviewDigest(old, 5).also { priorActiveUnit5Digests.put(pack, it) }
}

private val priorActiveUnit6DigestCache = IdentityWeakCache<Set<String>>()
private fun priorActiveUnit6Digests(pack: V2CurriculumPack): Set<String> = priorActiveUnit6DigestCache.get(pack) ?: run {
    val ids = listOf(Unit6CumulativeReviewPolicy.definition.id) +
        if (pack.unit(6)?.advancement?.cumulativeReviewPolicyId == Unit6PresentCumulativeReviewPolicy.definition.id)
            listOf(Unit6WiderCumulativeReviewPolicy.definition.id) else emptyList()
    ids.map { policyId ->
        val old = pack.copy(units = pack.units.map { unit -> if (unit.number == 6) unit.copy(
            advancement = unit.advancement!!.copy(requireDueReview = true, cumulativeReviewPolicyId = policyId)) else unit })
        V2Validator.unitContentReviewDigest(old, 6)
    }.toSet().also { priorActiveUnit6DigestCache.put(pack, it) }
}

private val priorActiveUnit9Digests = IdentityWeakCache<String>()
private fun priorActiveUnit9Digest(pack: V2CurriculumPack): String = priorActiveUnit9Digests.get(pack) ?: run {
    val old = pack.copy(units = pack.units.map { unit -> if (unit.number == 9) unit.copy(
        advancement = unit.advancement!!.copy(requireDueReview = true,
            cumulativeReviewPolicyId = Unit9CumulativeReviewPolicy.definition.id)) else unit })
    V2Validator.unitContentReviewDigest(old, 9).also { priorActiveUnit9Digests.put(pack, it) }
}

private val priorActiveUnit13Digests = IdentityWeakCache<String>()
private fun priorActiveUnit13Digest(pack: V2CurriculumPack): String = priorActiveUnit13Digests.get(pack) ?: run {
    val old = pack.copy(units = pack.units.map { unit -> if (unit.number == 13) unit.copy(
        advancement = unit.advancement!!.copy(requireDueReview = true,
            cumulativeReviewPolicyId = Unit13CumulativeReviewPolicy.definition.id)) else unit })
    V2Validator.unitContentReviewDigest(old, 13).also { priorActiveUnit13Digests.put(pack, it) }
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
