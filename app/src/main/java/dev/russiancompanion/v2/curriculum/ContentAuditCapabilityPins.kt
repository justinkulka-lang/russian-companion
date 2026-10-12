package dev.russiancompanion.v2.curriculum

/** Exact pending corpus intake. This is not a final review or installation approval. */
internal object ContentAuditCapabilityPins {
    data class Pin(val id: String, val recordId: String, val hash: String, val unit: Int,
        val objective: String, val claim: String, val features: Set<String>)
    val all = listOf(
        Pin("cap-obj-u26-letter-writing", "ca-corrected-capability:cap-obj-u26-letter-writing", "9eb8191a1f3f63e03e646fb305215ba8401e5a1c31ae1f6d2aa79f670ed6af3c", 26, "obj-u26-letter-writing", "letter-writing", setOf("LETTER_ADDRESS_LIMIT", "LETTER_STRUCTURE")),
        Pin("cap-u20-motion-po-setting-out", "ca-corrected-capability:cap-u20-motion-po-setting-out", "3dad37f220e468c450c6500ebb4db6c92fe4c7bd0a51adae5b47a76f7730df0e", 20, "obj-u20-motion", "motion-po-setting-out", setOf("MOTION_SETTING_OUT")),
        Pin("cap-u20-motion-prefixed-path", "ca-corrected-capability:cap-u20-motion-prefixed-path", "971f537a658a6102d6f9906a7e8bc70a6cf51bd69e5c9db73c7026b8c36e2a52", 20, "obj-u20-motion", "motion-prefixed-path", setOf("MOTION_NO_ADDED_CLAUSE")),
        Pin("cap-obj-u28-imperfective-adverb", "ca-corrected-capability:cap-obj-u28-imperfective-adverb", "3ec1b25855c9c37e0dd1ad494c532fa84e6a7f646a0b79fa3da4c58d578ff3f0", 28, "obj-u28-imperfective-adverb", "imperfective-verbal-adverb", setOf("IMPERFECTIVE_ADVERB_SIMULTANEOUS", "VERBAL_ADVERB_SHARED_SUBJECT")),
        Pin("cap-obj-u28-perfective-adverb", "ca-corrected-capability:cap-obj-u28-perfective-adverb", "29472cae0668a27d911dc90458ef77b06d145c822d0f3f944666ebdd91b0c8e7", 28, "obj-u28-perfective-adverb", "perfective-verbal-adverb", setOf("PERFECTIVE_ADVERB_ANTERIOR", "VERBAL_ADVERB_SHARED_SUBJECT")),
        Pin("cap-u7-bounded-manner-adverb", "ca-corrected-capability:cap-u7-bounded-manner-adverb", "d3378d4a5a98ce0fc8186bdb789285f0b702988a20fb583c0b412cf7799b38b2", 7, "obj-u7-adverb-paradigm", "bounded-manner-adverb-formation", setOf("ADJECTIVE_ADVERB_CONTRAST", "ADVERBIAL_FUNCTION", "DOCUMENTED_MANNER_PAIR", "REJECTED_ARBITRARY_ADVERB")),
        Pin("cap-obj-u27-comprehension", "ca-corrected-capability:cap-obj-u27-comprehension", "30e63fa8b8357c075e51ec64285936905a62e8acc627099b340f4d16f38da202", 27, "obj-u27-comprehension", "participle-comprehension", setOf("ALREADY_READ", "PASSIVE_READER_UNSPECIFIED", "WRITER_OF_THE_LETTER"))
    )
}
