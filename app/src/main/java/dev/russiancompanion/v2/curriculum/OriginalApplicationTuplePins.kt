package dev.russiancompanion.v2.curriculum

import java.security.MessageDigest

/** Curated tuple identity supplements the unchanged frozen narrative hash. No audit approval. */
internal object OriginalApplicationTuplePins {
    private const val READING_APPLICATIONS = "065d267c4a94d93576e4152ea262cda300ab0b862fe078a977e1b81ae2d582ab"

    /** Other synthesis paths retain their existing checks pending separate negative regressions. */
    fun matchesIfPinned(record: V2ProvenanceRecord): Boolean {
        if (record.sourceRecordId != Phase3Sources.READING.recordId) return true
        if (record.sourceHash != Phase3Sources.READING.sourceHash) return false
        val applications = record.synthesis?.applications ?: return false
        val canonical = buildString {
            append("tuple-v1:").append(applications.size).append(';')
            fun field(value: String) {
                append(value.toByteArray(Charsets.UTF_8).size).append(':').append(value)
            }
            applications.forEach { application ->
                field(application.cue)
                field(application.answer)
                append(application.distractors.size).append(';')
                application.distractors.forEach { field(it) }
            }
        }
        val hash = MessageDigest.getInstance("SHA-256").digest(canonical.toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it.toInt() and 255) }
        return hash == READING_APPLICATIONS
    }
}
