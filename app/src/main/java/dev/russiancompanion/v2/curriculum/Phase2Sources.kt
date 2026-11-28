package dev.russiancompanion.v2.curriculum

/**
 * Exact identities for the Phase 2 original-synthesis lessons.
 * A keyword, a neighboring quoted span, or a narrower pair does not enter this set.
 */
internal object Phase2Sources {
    const val SOURCE_ID = "src-original-research-synthesis"
    const val REVISION = "phase2-2026-09-29"
    const val TRANSFORMATION = "original research synthesis"
    const val DATE = "2026-09-29"

    internal data class Admission(
        val recordId: String,
        val sourceHash: String,
        val unitNumber: Int,
        val objectiveId: String,
        val claim: String,
        val binding: String,
        val independentFeatures: Set<String>,
    )

    val MANNER = Admission(
        recordId = "phase2:bounded-manner-adverb-formation",
        sourceHash = "80b6af48cd45fa98cad8161561676e3e1f26b3d82f9e6dade12d60d297ce6594",
        unitNumber = 7,
        objectiveId = "obj-u7-adverb-paradigm",
        claim = "bounded-manner-adverb-formation",
        binding = "obj-u7-adverb-paradigm:bounded-manner-adverb-formation",
        independentFeatures = setOf(
            "DOCUMENTED_MANNER_PAIR",
            "ADVERBIAL_FUNCTION",
            "PREDICATIVE_CONTRAST",
        ),
    )
    val COMPLETION = Admission(
        recordId = "phase2:completion-over-period",
        sourceHash = "80f3d6eccfd1ee3d8a50179253b50e6b81c6db6db34b87c1d1f0429b55d1af22",
        unitNumber = 23,
        objectiveId = "obj-u23-completion",
        claim = "completion-over-period",
        binding = "obj-u23-completion:completion-over-period",
        independentFeatures = setOf(
            "DURATION_WITHOUT_COMPLETION",
            "COMPLETED_WITHIN_PERIOD",
        ),
    )
    // T18e (D64, D65): the exact L:284–286 span replaces the synthesis capability.
    const val NAME_FORM_SOURCE = "src-ruwiki-lichnoe-imya-154915954"
    const val NAME_FORM_REVISION = "154915954"
    const val RETIRED_NAME_FORM_SYNTHESIS_HASH = "3441b6389e6d686cae944787bb7459e2d6ef7581d7deb06b582293c46daee1bb"
    val NAME_FORM = Admission(
        recordId = "lichnoe-imya:\u0412 \u0441\u043b\u043e\u0432\u043e\u043e\u0431\u0440\u0430\u0437\u043e\u0432\u0430\u043d\u0438\u0438 \u0443\u043c\u0435\u043d\u044c\u0448\u0438\u0442\u0435\u043b\u044c\u043d\u044b\u0445 \u0438 \u043b\u0430\u0441\u043a\u0430\u0442\u0435\u043b\u044c\u043d\u044b\u0445 \u0444\u043e\u0440\u043c \u0438\u043c\u0451\u043d \u0443\u0447\u0430\u0441\u0442\u0432\u0443\u044e\u0442 \u0440\u0430\u0437\u043d\u043e\u043e\u0431\u0440\u0430\u0437\u043d",
        sourceHash = "c1f43fe3614ab1d4e16ee53f8bdd1a0bb94c7181dee6077954d84e523f0d8f10",
        unitNumber = 25,
        objectiveId = "obj-u25-documented-name-form",
        claim = "documented-name-form",
        binding = "obj-u25-documented-name-form:documented-name-form",
        independentFeatures = setOf(
            "DOCUMENTED_SHORT_FORM_DERIVATIVE",
            "DOCUMENTED_DISMISSIVE_COLORING",
            "DOCUMENTED_BASE_NAME",
        ),
    )
    val ADMISSIONS = listOf(MANNER, COMPLETION)

    private val CAPABILITY_ADMISSIONS = ADMISSIONS + NAME_FORM

    fun admission(claim: String): Admission? = CAPABILITY_ADMISSIONS.firstOrNull { it.claim == claim }

    fun matches(capability: V2SourceCapability): Boolean {
        val admitted = admission(capability.supportedClaim) ?: return false
        val sourceId = if (admitted == NAME_FORM) NAME_FORM_SOURCE else SOURCE_ID
        val revision = if (admitted == NAME_FORM) NAME_FORM_REVISION else REVISION
        return capability.sourceId == sourceId &&
            capability.sourceRevision == revision &&
            capability.sourceRecordId == admitted.recordId &&
            capability.sourceHash == admitted.sourceHash &&
            capability.unitNumber == admitted.unitNumber &&
            capability.objectiveId == admitted.objectiveId &&
            capability.supportedClaim == admitted.claim
    }
}
