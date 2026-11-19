package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.V2CurriculumPack
import java.io.File

/**
 * D7b (owner decisions D7, D9, D10, D11a, D15c, D15d, D18, D19): the blocking phrase audit of every learner-facing
 * Russian phrase, clause sequence and stressed form in Units 1-30 against the frozen files under catalog/sources/.
 *
 * Rules, all against the frozen lines as displayed (TwoPartPhraseRule.unstressed: inline HTML joined and block tags as
 * boundaries, piped links read as display text, wiki markup removed):
 *  - UNATTESTED: a run of two or more Cyrillic words separated only by spaces (punctuation separates runs) is not present
 *    as whole words in any one frozen line (case and stress ignored, ё and е kept distinct);
 *  - MISSING_STRESS: a word shown with a stress mark has no frozen form with exactly that stress (OpenRussian apostrophes
 *    and Wikibooks bold stress, under the T15f bold-vowel rule, count as stress marks);
 *  - COMPOSED (D15c, D21c): a displayed clause sequence (word runs joined by a comma, semicolon, colon or dash) is not one
 *    contiguous substring of a single frozen line. Form enumerations (every part a single word) are not clause sequences,
 *    and (D21b) when a sequence runs straight into English text, a final comma part that is one word already shown
 *    earlier in the sequence is that English sentence's subject, not part of the sequence;
 *  - D9: a run or sequence made only of single letters, each attested on the frozen Russian-alphabet page, is exempt;
 *    D22c extends this to sequences whose items are single letters, upper/lower pairs or alphabet-page letter names.
 */
internal object PhraseAudit {
    data class Field(val unit: Int, val ownerField: String, val text: String)
    data class Failure(val unit: Int, val ownerField: String, val text: String, val type: String)

    private const val CYR = "\\u0400-\\u04FF"
    private const val ACUTE = '́'
    private const val WORD = "[$CYR][$CYR\\u0301]*(?:-[$CYR][$CYR\\u0301]*)?"
    private val span = Regex("$WORD(?:[ \\u00a0]+$WORD)+")
    // T15g (owner decision D21c): commas, semicolons, colons and dashes between words all join a clause sequence.
    private const val JOIN = "[,;:][ \\u00a0]+|[ \\u00a0]*[\\u2014\\u2013][ \\u00a0]*|[ \\u00a0]+-[ \\u00a0]+"
    private val join = Regex(JOIN)
    private val sequence = Regex("$WORD(?:(?:[ \\u00a0]+|$JOIN)$WORD)+")
    private val word = Regex(WORD)
    private val form = Regex("[$CYR][$CYR\\u0301]*")
    private val apostrophe = Regex("(?<=[$CYR])'(?!')")
    private val block = TwoPartPhraseRule.blockTag
    private val tag = Regex("<[^>]+>")
    private val bold = Regex("'''([\\u0430\\u0435\\u0451\\u0438\\u043e\\u0443\\u044b\\u044d\\u044e\\u044f\\u0410\\u0415\\u0401\\u0418\\u041e\\u0423\\u042b\\u042d\\u042e\\u042f])'''")

    val root: File = listOf(File("."), File("..")).first { File(it, "app/src/main").isDirectory }

    private val frozenFiles: List<File> by lazy {
        File(root, "catalog/sources").walkTopDown().filter { it.isFile && it.extension in setOf("txt", "csv", "html") }
            .sortedBy { it.path }.toList()
    }
    private val rawLines: List<String> by lazy { frozenFiles.flatMap { it.readText().split('\n') } }
    private val unstressedCorpus: String by lazy { rawLines.joinToString("\n") { TwoPartPhraseRule.unstressed(it) } }

    /** Every stressed OpenRussian form (apostrophe as acute), for the T15f word-initial bold-vowel rule. */
    private val openRussianStressed: Set<String> by lazy {
        frozenFiles.filter { it.extension == "csv" && it.parentFile.name == "openrussian" }.flatMap { file ->
            file.readText().split(Regex("[\\t,;()\\s]+")).filter { '\'' in it }.mapNotNull { token ->
                if (Regex("(?<![$CYR])'|''").containsMatchIn(token)) null else apostrophe.replace(token, "́").lowercase()
            }
        }.toSet()
    }

    /** catalog/units_3_5_intake.py format_wikibooks_emphasis (T15f rule). */
    fun formatWikibooksEmphasis(text: String): String {
        fun letter(c: Char?) = c != null && c.code in 0x0400..0x04FF && c.isLetter()
        val marked = bold.replace(text) { m ->
            val vowel = m.groupValues[1]
            val before = text.getOrNull(m.range.first - 1)
            val after = text.getOrNull(m.range.last + 1)
            when {
                vowel == "ё" || vowel == "Ё" -> vowel
                letter(before) -> vowel + ACUTE
                letter(after) -> {
                    val rest = text.substring(m.range.last + 1).takeWhile { letter(it) }
                    if ((vowel + ACUTE + rest).lowercase() in openRussianStressed) vowel + ACUTE else vowel
                }
                else -> vowel
            }
        }
        return marked.replace("'''", "").replace("''", "")
    }

    private val stressForms: Set<String> by lazy {
        rawLines.flatMap { line ->
            val formatted = formatWikibooksEmphasis(tag.replace(block.replace(line, " "), ""))
            listOf(line, formatted).flatMap { text -> form.findAll(apostrophe.replace(text, "́")).map { it.value.lowercase() } }
        }.toSet()
    }

    private val alphabetLetters: Set<String> by lazy {
        frozenFiles.filter { "russian-alphabet" in it.name }.flatMap { it.readText().split('\n') }.flatMap { line ->
            Regex("(?<![$CYR])[$CYR](?![$CYR])").findAll(TwoPartPhraseRule.unstressed(line)).map { it.value }
        }.toSet()
    }

    private val presentCache = HashMap<String, Boolean>()

    /** Present as whole words in one frozen line: case and stress ignored, ё and е distinct. */
    fun present(phrase: String): Boolean = presentCache.getOrPut(TwoPartPhraseRule.unstressed(phrase)) {
        val q = TwoPartPhraseRule.unstressed(phrase)
        var index = unstressedCorpus.indexOf(q)
        while (index >= 0) {
            val before = unstressedCorpus.getOrNull(index - 1)
            val after = unstressedCorpus.getOrNull(index + q.length)
            if ((before == null || before.code !in 0x0400..0x04FF) && (after == null || after.code !in 0x0400..0x04FF)) return@getOrPut true
            index = unstressedCorpus.indexOf(q, index + 1)
        }
        false
    }

    fun stressAttested(stressed: String): Boolean = stressed.lowercase() in stressForms

    /** The letter rows of the frozen Russian-alphabet page (one row per letter, with its image), for D22c names. */
    private val alphabetText: String by lazy {
        frozenFiles.filter { "russian-alphabet" in it.name }.flatMap { it.readText().split('\n') }
            .filter { "Russian alphabet-" in it }.joinToString("\n") { TwoPartPhraseRule.unstressed(it) }
    }

    private fun onAlphabetPage(item: String): Boolean {
        val q = TwoPartPhraseRule.unstressed(item)
        var index = alphabetText.indexOf(q)
        while (index >= 0) {
            val before = alphabetText.getOrNull(index - 1); val after = alphabetText.getOrNull(index + q.length)
            if ((before == null || before.code !in 0x0400..0x04FF) && (after == null || after.code !in 0x0400..0x04FF)) return true
            index = alphabetText.indexOf(q, index + 1)
        }
        return false
    }

    /**
     * T15g (owner decision D22c, extending D9): a sequence whose every item (between commas, semicolons, colons, dashes or
     * spaces) is a single letter, an upper/lower letter pair or a letter name attested on the alphabet page is a list,
     * not a clause sequence.
     */
    private fun letterOrNameList(sequence: String): Boolean = sequence.split(join).map { it.trim() }.filter { it.isNotEmpty() }.let { items ->
        items.isNotEmpty() && items.all { item -> letterList(item) || onAlphabetPage(item) }
    }

    private fun letterList(text: String): Boolean = word.findAll(text).toList().let { words ->
        words.isNotEmpty() && words.all { w -> w.value.replace("́", "").length == 1 && w.value.replace("́", "").lowercase() in alphabetLetters }
    }

    /**
     * T15g (owner decision D21e): the audit reads the shared learner field set, V2LearnerFields.fields, which the
     * validator's Russian-coverage scan also reads; V2LearnerFieldsTest derives it from the UI rendering code.
     */
    fun fields(pack: V2CurriculumPack): List<Field> =
        dev.russiancompanion.v2.curriculum.V2LearnerFields.fields(pack).map { Field(it.unit, it.ownerField, it.text) }

    fun failures(fields: List<Field>): Set<Failure> {
        val failures = linkedSetOf<Failure>()
        fields.forEach { (n, owner, text) ->
            sequence.findAll(text).forEach { m ->
                var sq = m.value
                // T15g (owner decision D21b): when a sequence runs straight into English, its final comma part is dropped
                // only if it is one word that already appears earlier in the same sequence ("In мой стол, стол is ...").
                val runsIntoEnglish = Regex("^[ \\u00a0]+[A-Za-z]").containsMatchIn(text.substring(m.range.last + 1))
                if (runsIntoEnglish && ',' in sq) {
                    val last = sq.substring(sq.lastIndexOf(',') + 1).trim()
                    val head = sq.substring(0, sq.lastIndexOf(','))
                    val earlier = word.findAll(head).map { TwoPartPhraseRule.unstressed(it.value) }.toSet()
                    if (word.matches(last) && TwoPartPhraseRule.unstressed(last) in earlier) sq = head
                }
                val parts = sq.split(join)
                if (parts.size < 2) return@forEach
                if (parts.all { it.trim().split(Regex("\\s+")).size == 1 }) return@forEach
                if (letterList(sq) || letterOrNameList(sq)) return@forEach
                if (!present(sq)) failures += Failure(n, owner, sq, "COMPOSED")
            }
            span.findAll(text).forEach { m ->
                if (!letterList(m.value) && !present(m.value)) failures += Failure(n, owner, m.value, "UNATTESTED")
            }
            form.findAll(text).forEach { m ->
                if (ACUTE in m.value && !stressAttested(m.value)) failures += Failure(n, owner, m.value, "MISSING_STRESS")
            }
        }
        return failures
    }
}
