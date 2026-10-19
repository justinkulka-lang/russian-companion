package dev.russiancompanion.v2.curriculum

import java.security.MessageDigest

/** Curated tuple identity supplements the unchanged frozen narrative hash. No audit approval. */
internal object OriginalApplicationTuplePins {
    private const val READING_APPLICATIONS = "065d267c4a94d93576e4152ea262cda300ab0b862fe078a977e1b81ae2d582ab"

    private const val DICTIONARY_APPLICATIONS = "94c3b6da1505f6c48d832fd39d2b477b5f9e0175991db7e1e3c73f4a6043aa29"

    private const val COMPLETION_APPLICATIONS = "6751f6b0ce9cc15fcc38a3d326f1e65a30a5866081bd19d6bc6b4fdd49cfabcc"

    /** Other synthesis paths retain their existing checks pending separate negative regressions. */
    fun matchesIfPinned(record: V2ProvenanceRecord): Boolean {
        val (sourceHash, tupleHash) = when(record.sourceRecordId) {
            Phase2Sources.COMPLETION.recordId -> Phase2Sources.COMPLETION.sourceHash to COMPLETION_APPLICATIONS
            Phase3Sources.READING.recordId -> Phase3Sources.READING.sourceHash to READING_APPLICATIONS
            Phase3Sources.DICTIONARY.recordId -> Phase3Sources.DICTIONARY.sourceHash to DICTIONARY_APPLICATIONS
            else -> return true
        }
        if (record.sourceHash != sourceHash) return false
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
        return hash == tupleHash
    }
}
