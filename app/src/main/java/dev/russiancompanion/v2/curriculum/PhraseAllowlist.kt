package dev.russiancompanion.v2.curriculum

/**
 * D7b (owner decisions D7b, D18b, D18c, D19b): the shrinking allowlist of learner-facing Russian that has no frozen
 * witness yet. The single source file is audit/d7b-phrase-allowlist.txt; the build packages its exact bytes as a Java
 * resource of this package (never an asset, string or screen), so the runtime validator and the blocking phrase audit
 * read the same entries. Each line is "unit | owner.field | text | type".
 *
 * A missing or unreadable resource yields no entries, so the validator fails closed.
 */
internal object PhraseAllowlist {
    const val RESOURCE = "d7b-phrase-allowlist.txt"

    data class Entry(val unit: Int, val ownerField: String, val text: String, val type: String)

    val entries: List<Entry> by lazy { load() }

    fun resourceBytes(): ByteArray? =
        PhraseAllowlist::class.java.getResourceAsStream(RESOURCE)?.use { it.readBytes() }

    fun parse(text: String): List<Entry> = text.split('\n').filter { it.isNotBlank() }.map { line ->
        val parts = line.split(" | ")
        require(parts.size == 4) { "Malformed allowlist line: $line" }
        Entry(parts[0].trim().toInt(), parts[1].trim(), parts[2].trim(), parts[3].trim())
    }

    private fun load(): List<Entry> = resourceBytes()?.let { parse(it.toString(Charsets.UTF_8)) } ?: emptyList()

    /**
     * D18b: a Russian region is exempt when it equals an allowlisted text of the same unit or is a whole-word substring
     * of one. Matching is exact otherwise: case, stress marks and the yo letter are kept.
     */
    fun exempts(unit: Int, region: String, allowlist: List<Entry> = entries): Boolean {
        if (region.isBlank()) return false
        return allowlist.any { it.unit == unit && (it.text == region || containsWholeWord(it.text, region)) }
    }

    /** Whole-word containment: no Cyrillic letter or combining acute directly before or after the match. */
    private fun containsWholeWord(text: String, region: String): Boolean {
        var index = text.indexOf(region)
        while (index >= 0) {
            if (!wordChar(text.getOrNull(index - 1)) && !wordChar(text.getOrNull(index + region.length))) return true
            index = text.indexOf(region, index + 1)
        }
        return false
    }

    private fun wordChar(c: Char?): Boolean = c != null && (c.code in 0x0400..0x04FF || c.code == 0x0301)
}
