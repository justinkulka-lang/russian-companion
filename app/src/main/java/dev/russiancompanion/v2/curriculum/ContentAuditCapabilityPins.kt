package dev.russiancompanion.v2.curriculum

/** Exact pending corpus intake. This is not a final review or installation approval. */
internal object ContentAuditCapabilityPins {
    data class Pin(val id: String, val recordId: String, val hash: String, val unit: Int,
        val objective: String, val claim: String, val features: Set<String>)
    val all = listOf(
        Pin("cap-u20-motion-po-setting-out", "ca-corrected-capability:cap-u20-motion-po-setting-out", "a54fc1be4b51edd65b4c248fd52afc504b299f42b962c1748bf8ce094e453abf", 20, "obj-u20-motion", "motion-po-setting-out", setOf("MOTION_SETTING_OUT")),
        Pin("cap-u20-motion-prefixed-path", "ca-corrected-capability:cap-u20-motion-prefixed-path", "1104e32f10aed0bf6954dbe92b28f1299bc5047ca84dab81f9e8074aa152c95f", 20, "obj-u20-motion", "motion-prefixed-path", setOf("MOTION_NO_ADDED_CLAUSE")),
        Pin("cap-obj-u28-imperfective-adverb", "ca-corrected-capability:cap-obj-u28-imperfective-adverb", "3ec1b25855c9c37e0dd1ad494c532fa84e6a7f646a0b79fa3da4c58d578ff3f0", 28, "obj-u28-imperfective-adverb", "imperfective-verbal-adverb", setOf("IMPERFECTIVE_ADVERB_SIMULTANEOUS", "VERBAL_ADVERB_SHARED_SUBJECT")),
        Pin("cap-obj-u28-perfective-adverb", "ca-corrected-capability:cap-obj-u28-perfective-adverb", "29472cae0668a27d911dc90458ef77b06d145c822d0f3f944666ebdd91b0c8e7", 28, "obj-u28-perfective-adverb", "perfective-verbal-adverb", setOf("PERFECTIVE_ADVERB_ANTERIOR", "VERBAL_ADVERB_SHARED_SUBJECT")),
        Pin("cap-u7-bounded-manner-adverb", "ca-corrected-capability:cap-u7-bounded-manner-adverb", "d3378d4a5a98ce0fc8186bdb789285f0b702988a20fb583c0b412cf7799b38b2", 7, "obj-u7-adverb-paradigm", "bounded-manner-adverb-formation", setOf("ADJECTIVE_ADVERB_CONTRAST", "ADVERBIAL_FUNCTION", "DOCUMENTED_MANNER_PAIR", "REJECTED_ARBITRARY_ADVERB")),
        Pin("cap-obj-u27-comprehension", "ca-corrected-capability:cap-obj-u27-comprehension", "30e63fa8b8357c075e51ec64285936905a62e8acc627099b340f4d16f38da202", 27, "obj-u27-comprehension", "participle-comprehension", setOf("ALREADY_READ", "PASSIVE_READER_UNSPECIFIED", "WRITER_OF_THE_LETTER"))
    )
}
