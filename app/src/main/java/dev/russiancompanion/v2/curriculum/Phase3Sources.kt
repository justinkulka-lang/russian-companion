package dev.russiancompanion.v2.curriculum

/**
 * Exact identities for the Phase 3 original-synthesis lessons.
 * A keyword, a neighboring quoted span, or a paired claim does not enter this set.
 */
internal object Phase3Sources {
    const val SOURCE_ID = "src-original-research-synthesis"
    const val REVISION = "phase3-2026-09-30"
    const val TRANSFORMATION = "original research synthesis"
    const val DATE = "2026-09-30"

    internal data class Admission(
        val recordId: String,
        val sourceHash: String,
        val unitNumber: Int,
        val objectiveId: String,
        val claim: String,
        val binding: String,
        val independentFeatures: Set<String>,
    )

    val POSTAL = Admission(
        recordId = "phase3:postal-address",
        sourceHash = "546e52bec4a95e4454791c68858a19e6d713d3218d6dde3f43c041fbd3f83a97",
        unitNumber = 26,
        objectiveId = "obj-u26-postal-address",
        claim = "postal-address",
        binding = "obj-u26-postal-address:postal-address",
        independentFeatures = setOf(
            "ADDRESSEE_ROLE",
            "STREET_AND_PREMISES",
            "SIX_DIGIT_POSTCODE",
            "REJECTED_GREETING",
            "REGIONAL_UNIT_AS_APPLICABLE",
            "FEDERAL_CITY_WITHOUT_OBLAST",
            "COUNTRY_COMPONENT",
            "POST_OFFICE_BOX_CONDITIONAL",
            "APARTMENT_CONDITIONAL",
            "COMPLETE_GOVERNED_ADDRESS",
            "FOUR_COMPONENT_INCOMPLETE",
        ),
    )
    val STRESS = Admission(
        recordId = "phase3:participle-stress",
        sourceHash = "e4dad2bc4f594d302dce65ba8a62029020023ef247c0984eb98378b6784f53a7",
        unitNumber = 27,
        objectiveId = "obj-u27-stress",
        claim = "participle-stress",
        binding = "obj-u27-stress:participle-stress",
        independentFeatures = setOf(
            "STRESS_OBSUZHDAEMY",
            "STRESS_NAPISANNY",
            "STRESS_PROCHITANNY",
            "REJECTED_PRODUCTIVE_STRESS",
        ),
    )
    val COMPREHENSION = Admission(
        recordId = "phase3:participle-comprehension",
        sourceHash = "3d78d8b453239f09ad10b9e321bec583b6650ae572b7c729ea536a9b46a487fc",
        unitNumber = 27,
        objectiveId = "obj-u27-comprehension",
        claim = "participle-comprehension",
        binding = "obj-u27-comprehension:participle-comprehension",
        independentFeatures = setOf(
            "WRITER_OF_THE_LETTER",
            "PUT_AWAY_AFTER_READING",
            "ALREADY_READ",
        ),
    )
    val REGISTER = Admission(
        recordId = "phase3:bookish-register",
        sourceHash = "c178b9065ba0fd977aa2913922c4c18d3fa18d196a89f65e7ef13e54ce1637e6",
        unitNumber = 29,
        objectiveId = "obj-u29-register",
        claim = "bookish-register",
        binding = "obj-u29-register:bookish-register",
        independentFeatures = setOf(
            "ADVERBIAL_BOOKISH_OPTION",
            "COORDINATED_ORDINARY_OPTION",
            "REJECTED_PASSIVE_PARTICIPLE",
            "REJECTED_WRITTEN_TEXT",
            "REJECTED_UNIVERSAL_RULE",
        ),
    )
    val READING = Admission(
        recordId = "phase3:bookish-reading",
        sourceHash = "3228475d8bfaee13e41ef66f0a2a5e9f01640ce0b4fc8a6cc79df8abf8cadae3",
        unitNumber = 29,
        objectiveId = "obj-u29-reading",
        claim = "bookish-reading",
        binding = "obj-u29-reading:bookish-reading",
        independentFeatures = setOf(
            "FINISHED_BEFORE_LEAVING",
            "WENT_TO_POST_OFFICE",
            "SENT_THE_LETTER",
        ),
    )
    val DICTIONARY = Admission(
        recordId = "phase3:dictionary-use",
        sourceHash = "3ff0ca9b4aab3a3208b6fbd40edffd5e7308f3fbd6023ae482a421b45c29ea71",
        unitNumber = 30,
        objectiveId = "obj-u30-dictionary",
        claim = "dictionary-use",
        binding = "obj-u30-dictionary:dictionary-use",
        independentFeatures = setOf(
            "ENTRY_FORM_TO_SENSE",
            "ENTRY_VERB_FORM_TO_SENSE",
            "ENTRY_EXAMPLE_TO_SENSE",
            "POS_LABEL_INSUFFICIENT",
            "FORMS_ROW_INSUFFICIENT",
        ),
    )
    val ADMISSIONS = listOf(POSTAL, STRESS, COMPREHENSION, REGISTER, READING, DICTIONARY)

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
