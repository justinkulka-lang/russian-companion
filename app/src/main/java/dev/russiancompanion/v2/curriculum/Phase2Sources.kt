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
            "REJECTED_ARBITRARY_ADVERB",
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
    val NAME_FORM = Admission(
        recordId = "phase2:documented-name-form",
        sourceHash = "0bd6814d914fc80c3e61edd47f52f75a9607dc1f371795afc71e80ca11a87ce9",
        unitNumber = 25,
        objectiveId = "obj-u25-documented-name-form",
        claim = "documented-name-form",
        binding = "obj-u25-documented-name-form:documented-name-form",
        independentFeatures = setOf(
            "DOCUMENTED_AFFECTIONATE_FORM",
            "DOCUMENTED_FAMILIAR_FORM",
            "REJECTED_ARBITRARY_NAME",
        ),
    )
    val ADMISSIONS = listOf(MANNER, COMPLETION, NAME_FORM)

    fun admission(claim: String): Admission? = ADMISSIONS.firstOrNull { it.claim == claim }

    fun matches(capability: V2SourceCapability): Boolean {
        val admitted = admission(capability.supportedClaim) ?: return false
        return capability.sourceId == SOURCE_ID &&
            capability.sourceRevision == REVISION &&
            capability.sourceRecordId == admitted.recordId &&
            capability.sourceHash == admitted.sourceHash &&
            capability.unitNumber == admitted.unitNumber &&
            capability.objectiveId == admitted.objectiveId &&
            capability.supportedClaim == admitted.claim
    }
}
