package dev.russiancompanion.v2.audio

import dev.russiancompanion.v2.curriculum.V2CurriculumPack
import kotlinx.serialization.Serializable
import java.security.MessageDigest
import java.text.Normalizer

/**
 * A2-17 per-clip verification. A clip may be bound to text only when a person listened and
 * confirmed the match (OWNER_ or REVIEWER_LISTEN_VERIFIED) or a reviewed dataset text-match record
 * ties the clip to the text (DATASET_TEXT_MATCH). File names, catalog flags and similarity never
 * establish verification; they only propose candidates for the listen-check sheet.
 */
internal const val AUDIO_VERIFICATION_SCHEMA = 1
internal const val OWNER_LISTEN_VERIFIED = "OWNER_LISTEN_VERIFIED"
internal const val REVIEWER_LISTEN_VERIFIED = "REVIEWER_LISTEN_VERIFIED"
internal const val DATASET_TEXT_MATCH = "DATASET_TEXT_MATCH"
internal val AUDIO_VERIFICATION_STATUSES = setOf(OWNER_LISTEN_VERIFIED, REVIEWER_LISTEN_VERIFIED, DATASET_TEXT_MATCH)
internal val AUDIO_TARGET_KINDS = setOf("LETTER", "EXAMPLE", "ITEM")

@Serializable
internal data class V2AudioVerificationRecord(
    val clipId: String,
    val clipSha256: String,
    val text: String,
    val unitNumber: Int,
    val targetKind: String,
    val targetId: String,
    val status: String,
    val verifier: String,
    val recordedAt: String,
    /** The listen-check sheet the decision came from, when it came from one. */
    val sheet: String? = null,
)

@Serializable
internal data class V2AudioVerificationRegistry(
    val schema: Int = AUDIO_VERIFICATION_SCHEMA,
    val records: List<V2AudioVerificationRecord> = emptyList(),
)

/** One bundled clip as the catalog describes it: id, its catalog text and the file's SHA-256. */
internal data class V2CatalogClip(val id: String, val text: String, val sha256: String)

internal fun sha256Hex(bytes: ByteArray): String =
    MessageDigest.getInstance("SHA-256").digest(bytes).joinToString("") { "%02x".format(it.toInt() and 255) }

/** Stress marks (U+0301) are ignored; nothing else is normalized (A2-21 limits apply to text too). */
internal fun stressFree(text: String): String =
    Normalizer.normalize(Normalizer.normalize(text, Normalizer.Form.NFD).replace("́", ""), Normalizer.Form.NFC).trim()

/** The Russian vowel letters, lowercase and uppercase, as code points (no Cyrillic literal in main sources). */
private val VOWEL_LETTERS = setOf(0x0430, 0x0435, 0x0451, 0x0438, 0x043E, 0x0443, 0x044B, 0x044D, 0x044E, 0x044F,
    0x0410, 0x0415, 0x0401, 0x0418, 0x041E, 0x0423, 0x042B, 0x042D, 0x042E, 0x042F)

/**
 * T-AUDIO-1a (owner decision D48, approved rule change): a stress-mark difference is allowed only in a word with exactly one
 * vowel letter, where the mark cannot move the stress. A text with more than one vowel letter keeps its stress marks, so
 * zamok stressed on the first syllable (castle) and on the second (lock), or either and the unmarked form, are different texts. Whitespace is trimmed at the ends only; yo and ye stay
 * distinct (was: stressFree, which dropped every stress mark whatever the word).
 */
internal fun stressNormalized(text: String): String {
    val trimmed = Normalizer.normalize(text, Normalizer.Form.NFC).trim()
    val plain = stressFree(trimmed)
    return if (plain.codePoints().filter { it in VOWEL_LETTERS }.count() == 1L) plain else trimmed
}

/**
 * Text used to match a clip to a target (owner ruling 2026-11-24, T-AUDIO-1): stress marks aside, the two texts are equal
 * or differ only in the case of their first letter (a sentence-initial question word or pronoun shows a capital the clip's
 * catalog text does not have). Nothing else is normalized; any other case difference is a different text.
 * D48: "stress marks aside" holds only for a one-vowel word (stressNormalized); a longer word must match its stress exactly.
 */
internal fun audioMatchKey(text: String): String {
    val plain = stressNormalized(text)
    if (plain.isEmpty()) return plain
    val first = plain.codePointAt(0)
    return String(Character.toChars(Character.toLowerCase(first))) + plain.substring(Character.charCount(first))
}

/** The learner-visible text a target carries, used to confirm the verified text is really there. */
internal fun audioTargetTexts(pack: V2CurriculumPack, kind: String, id: String): List<String>? = when (kind) {
    "LETTER" -> pack.letters.singleOrNull { it.id == id }?.let { listOf(it.lowercase, it.uppercase) }
    "EXAMPLE" -> pack.examples.singleOrNull { it.id == id }?.let { listOf(it.display) }
    "ITEM" -> pack.items.singleOrNull { it.id == id }?.let { listOf(it.answer) + it.options }
    else -> null
}

/** Structural and one-to-one checks; an empty list means the registry may feed audio binding. */
internal fun audioVerificationDefects(registry: V2AudioVerificationRegistry, pack: V2CurriculumPack,
                                      clips: Map<String, V2CatalogClip>): List<String> {
    val errors = mutableListOf<String>()
    if (registry.schema != AUDIO_VERIFICATION_SCHEMA) errors += "Unsupported audio verification schema ${registry.schema}"
    val keys = registry.records.map { Triple(it.clipId, it.targetKind, it.targetId) }
    if (keys.distinct().size != keys.size) errors += "Duplicate audio verification record"
    for (r in registry.records) {
        val label = "Audio verification ${r.clipId} -> ${r.targetKind} ${r.targetId}"
        if (r.status !in AUDIO_VERIFICATION_STATUSES) errors += "$label has unsupported status ${r.status}"
        if (r.targetKind !in AUDIO_TARGET_KINDS) errors += "$label has unsupported target kind"
        if (r.unitNumber !in 1..30 || r.verifier.isBlank() || r.recordedAt.isBlank() || r.text.isBlank()) errors += "$label is incomplete"
        if (!Regex("[0-9a-f]{64}").matches(r.clipSha256)) errors += "$label has a malformed clip SHA-256"
        val clip = clips[r.clipId]
        if (clip == null) { errors += "$label names a missing clip"; continue }
        if (clip.sha256 != r.clipSha256) errors += "$label does not match the bundled clip bytes"
        // D48: the record's text is the clip's catalog text; a stress mark may differ only in a one-vowel word.
        if (stressNormalized(clip.text) != stressNormalized(r.text)) errors += "$label text differs from the clip's catalog text"
        val targets = audioTargetTexts(pack, r.targetKind, r.targetId)
        if (targets == null) errors += "$label names a missing target"
        else if (targets.none { audioMatchKey(it) == audioMatchKey(r.text) }) errors += "$label text is not the target's text"
    }
    return errors
}

/** Unit to verified clip ids, for the A2-23 dictation diagnostics. */
internal fun verifiedAudioByUnit(registry: V2AudioVerificationRegistry): Map<Int, Set<String>> =
    registry.records.filter { it.status in AUDIO_VERIFICATION_STATUSES }
        .groupBy { it.unitNumber }.mapValues { (_, rows) -> rows.map { it.clipId }.toSet() }
