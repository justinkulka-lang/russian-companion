package dev.russiancompanion.v2.curriculum

/** Exact pending corpus intake. This is not a final review or installation approval. */
internal object ContentAuditCapabilityPins {
    data class Pin(val id: String, val recordId: String, val hash: String, val unit: Int,
        val objective: String, val claim: String, val features: Set<String>)
    val all = listOf(
        Pin("cap-obj-u26-postal-address", "ca-corrected-capability:cap-obj-u26-postal-address", "494257b8f11671d1643aefc7533fbb374966c84bc8a399dadfbe7973403d69f1", 26, "obj-u26-postal-address", "postal-address", setOf("ADDRESSEE_ROLE", "APARTMENT_CONDITIONAL", "COMPLETE_GOVERNED_ADDRESS", "COUNTRY_COMPONENT", "FEDERAL_CITY_WITHOUT_OBLAST", "FOUR_COMPONENT_INCOMPLETE", "POST_OFFICE_BOX_CONDITIONAL", "REGIONAL_UNIT_AS_APPLICABLE", "REJECTED_GREETING", "SIX_DIGIT_POSTCODE", "STREET_AND_PREMISES")),
        Pin("cap-obj-u26-word-order", "ca-corrected-capability:cap-obj-u26-word-order", "763d91eb12ba1c8ef6553d9c700f800fb8ccfb93bdf41ff5a6ae83a19d1fd94b", 26, "obj-u26-word-order", "word-order", setOf("WORD_ORDER")),
        Pin("cap-obj-u26-letter-writing", "ca-corrected-capability:cap-obj-u26-letter-writing", "9eb8191a1f3f63e03e646fb305215ba8401e5a1c31ae1f6d2aa79f670ed6af3c", 26, "obj-u26-letter-writing", "letter-writing", setOf("LETTER_ADDRESS_LIMIT", "LETTER_STRUCTURE")),
        Pin("cap-u20-motion-po-setting-out", "ca-corrected-capability:cap-u20-motion-po-setting-out", "543855e3604c8812cbc0bc2b2a22361f3165472708bc771019db79a534c5aedc", 20, "obj-u20-motion", "motion-po-setting-out", setOf("MOTION_SETTING_OUT")),
        Pin("cap-u20-motion-prefixed-path", "ca-corrected-capability:cap-u20-motion-prefixed-path", "ee7cfc22128ea694fcc77ec8da9d348ee9d1f2e72b50e69bea8754ca4970f753", 20, "obj-u20-motion", "motion-prefixed-path", setOf("MOTION_NO_ADDED_CLAUSE")),
        Pin("cap-obj-u28-imperfective-adverb", "ca-corrected-capability:cap-obj-u28-imperfective-adverb", "a21d6efac164e3442a34b198308777bc559f44a0d43524eded2d618a19e779bf", 28, "obj-u28-imperfective-adverb", "imperfective-verbal-adverb", setOf("IMPERFECTIVE_ADVERB_SIMULTANEOUS", "VERBAL_ADVERB_SHARED_SUBJECT")),
        Pin("cap-obj-u28-perfective-adverb", "ca-corrected-capability:cap-obj-u28-perfective-adverb", "bf5eb9cff399832fa6133f59b73b987e64b4882ec513f98f200b1a000c4716e0", 28, "obj-u28-perfective-adverb", "perfective-verbal-adverb", setOf("PERFECTIVE_ADVERB_ANTERIOR", "VERBAL_ADVERB_SHARED_SUBJECT")),
        Pin("cap-u7-bounded-manner-adverb", "ca-corrected-capability:cap-u7-bounded-manner-adverb", "d3378d4a5a98ce0fc8186bdb789285f0b702988a20fb583c0b412cf7799b38b2", 7, "obj-u7-adverb-paradigm", "bounded-manner-adverb-formation", setOf("ADJECTIVE_ADVERB_CONTRAST", "ADVERBIAL_FUNCTION", "DOCUMENTED_MANNER_PAIR", "REJECTED_ARBITRARY_ADVERB")),
        Pin("cap-obj-u27-comprehension", "ca-corrected-capability:cap-obj-u27-comprehension", "74f96df33d743da02fde83beabed4e1b27e654b0af43185ffb803817a5b10a3e", 27, "obj-u27-comprehension", "participle-comprehension", setOf("ALREADY_READ", "PASSIVE_READER_UNSPECIFIED", "WRITER_OF_THE_LETTER"))
    )
}
