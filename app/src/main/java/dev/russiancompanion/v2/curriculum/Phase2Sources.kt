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
        sourceHash = "c4620b1e9d8fe14fadaadd56675612a2c5d614a1eeeb8a1576061a63b3dd535c",
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
        sourceHash = "e8d0887f6c8a9fe6f9a34216f705654cbc18422e8e6a0f9e78b8222fa0847e99",
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
        sourceHash = "b1fcd8cc924b79626bbda105f91b2bbe4327d75448da71ed99c9798e7e266af0",
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
