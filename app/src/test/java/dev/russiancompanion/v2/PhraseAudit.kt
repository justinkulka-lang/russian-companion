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
 *  - COMPOSED (D15c): a displayed clause sequence (word runs joined by ", ") is not one contiguous substring of a single
 *    frozen line. Form enumerations (every comma part a single word) are not clause sequences, and when a sequence runs
 *    straight into English text, a single-word last part is that English sentence's subject, not part of the sequence;
 *  - D9: a run or sequence made only of single letters, each attested on the frozen Russian-alphabet page, is exempt.
 */
internal object PhraseAudit {
    data class Field(val unit: Int, val ownerField: String, val text: String)
    data class Failure(val unit: Int, val ownerField: String, val text: String, val type: String)

    private const val CYR = "\\u0400-\\u04FF"
    private const val ACUTE = '́'
    private const val WORD = "[$CYR][$CYR\\u0301]*(?:-[$CYR][$CYR\\u0301]*)?"
    private val span = Regex("$WORD(?:[ \\u00a0]+$WORD)+")
    private val sequence = Regex("$WORD(?:(?:[ \\u00a0]+|,[ \\u00a0]+)$WORD)+")
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

    private fun letterList(text: String): Boolean = word.findAll(text).toList().let { words ->
        words.isNotEmpty() && words.all { w -> w.value.replace("́", "").length == 1 && w.value.replace("́", "").lowercase() in alphabetLetters }
    }

    /** Learner-facing fields with their "owner.field" names, each attributed to the unit that shows it. */
    fun fields(pack: V2CurriculumPack): List<Field> {
        val out = mutableListOf<Field>()
        val objectiveUnit = pack.units.flatMap { u -> u.objectives.map { it.id to u.number } }.toMap()
        val exampleUnits = mutableMapOf<String, MutableSet<Int>>()
        val referenceUnits = mutableMapOf<String, MutableSet<Int>>()
        fun add(unit: Int, owner: String, field: String, text: String?) { if (!text.isNullOrEmpty()) out += Field(unit, "$owner.$field", text) }
        pack.units.forEach { u ->
            val n = u.number
            add(n, u.id, "title", u.title); add(n, u.id, "shortLabel", u.shortLabel); add(n, u.id, "topicSummary", u.topicSummary)
            // T15g (D20): the mapping note is shown on the unit screen (V2Screens), so it is learner-facing.
            add(n, u.id, "mappingNote", u.mappingNote)
            u.objectives.forEach { o -> add(n, o.id, "title", o.title); o.exampleIds.forEach { exampleUnits.getOrPut(it) { mutableSetOf() } += n } }
            u.sessions.forEach { s ->
                add(n, s.id, "title", s.title)
                s.steps.forEach { st ->
                    add(n, st.id, "title", st.title); add(n, st.id, "body", st.body); add(n, st.id, "bookDirection", st.bookDirection)
                    add(n, st.id, "companionDirection", st.companionDirection); add(n, st.id, "unavailableReason", st.unavailableReason)
                    add(n, st.id, "dominantAction", st.dominantAction)
                    st.exampleIds.forEach { exampleUnits.getOrPut(it) { mutableSetOf() } += n }
                    st.referenceIds.forEach { referenceUnits.getOrPut(it) { mutableSetOf() } += n }
                }
            }
        }
        pack.items.forEach { item ->
            val n = objectiveUnit[item.objectiveId] ?: return@forEach
            add(n, item.id, "prompt", item.prompt); add(n, item.id, "promptLetter", item.promptLetter)
            add(n, item.id, "answer", item.answer); add(n, item.id, "feedback", item.feedback)
            item.options.forEachIndexed { i, v -> add(n, item.id, "options[$i]", v) }
            item.distractorRationales.forEachIndexed { i, v -> add(n, item.id, "distractorRationales[$i]", v) }
            item.typedAnswerKey?.acceptedAnswers?.forEachIndexed { i, v -> add(n, item.id, "acceptedAnswers[$i]", v) }
        }
        pack.examples.forEach { e ->
            val units = exampleUnits[e.id] ?: setOfNotNull(objectiveUnit[e.objectiveId])
            units.sorted().forEach { n -> add(n, e.id, "display", e.display); add(n, e.id, "note", e.note) }
        }
        pack.references.forEach { r ->
            referenceUnits[r.id].orEmpty().sorted().forEach { n -> add(n, r.id, "title", r.title); add(n, r.id, "summary", r.summary); add(n, r.id, "detail", r.detail) }
        }
        return out
    }

    fun failures(fields: List<Field>): Set<Failure> {
        val failures = linkedSetOf<Failure>()
        fields.forEach { (n, owner, text) ->
            sequence.findAll(text).forEach { m ->
                var sq = m.value
                val runsIntoEnglish = Regex("^[ \\u00a0]+[A-Za-z]").containsMatchIn(text.substring(m.range.last + 1))
                if (runsIntoEnglish && ',' in sq && sq.substring(sq.lastIndexOf(',') + 1).trim().split(Regex("\\s+")).size == 1) {
                    sq = sq.substring(0, sq.lastIndexOf(','))
                }
                if (',' !in sq) return@forEach
                if (sq.split(',').all { it.trim().split(Regex("\\s+")).size == 1 }) return@forEach
                if (letterList(sq)) return@forEach
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
