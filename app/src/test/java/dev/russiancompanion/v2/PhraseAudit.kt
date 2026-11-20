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
 *  - COMPOSED (D15c, D21c, D25a): a displayed clause sequence (word runs joined by a comma, semicolon or colon, with or
 *    without following spaces, or by a dash) is not one contiguous substring of a single frozen line. Form enumerations
 *    (every part a single word) are not clause sequences. (The D21b English-boundary rule is deleted under D25b.)
 *  - D9: a run or sequence made only of single letters, each attested on the frozen Russian-alphabet page, is exempt;
 *    D22c extends this to sequences whose items are single letters, upper/lower pairs or exact letter-name cells of the
 *    alphabet table (D25f).
 */
internal object PhraseAudit {
    data class Field(val unit: Int, val ownerField: String, val text: String, val kind: String = "")
    data class Failure(val unit: Int, val ownerField: String, val text: String, val type: String)

    private const val CYR = "\\u0400-\\u04FF"
    private const val ACUTE = '́'
    private const val WORD = "[$CYR][$CYR\\u0301]*(?:-[$CYR][$CYR\\u0301]*)?"
    private val span = Regex("$WORD(?:[ \\u00a0]+$WORD)+")
    // T15g (owner decision D21c): commas, semicolons, colons and dashes between words all join a clause sequence.
    // T15h (owner decision D25a): a comma, semicolon or colon joins with or without following spaces.
    private const val JOIN = "[ \\u00a0]*[,;:][ \\u00a0]*|[ \\u00a0]*[\\u2014\\u2013][ \\u00a0]*|[ \\u00a0]+-[ \\u00a0]+"
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

    /**
     * T15h (owner decision D25f): the exact cells of the frozen alphabet table's letter column (the pair, e.g. "Б б", and
     * each of its letters) and modern letter-name column (each {{lang|ru|...}} value, e.g. "бэ", "твёрдый знак"). Only a
     * whole cell value counts, never a word inside one; the pre-1708 name column is not included.
     */
    private val alphabetCells: Set<String> by lazy {
        val langValue = Regex("\\{\\{lang\\|ru\\|([^}|]+)\\}\\}")
        frozenFiles.filter { "russian-alphabet" in it.name }.flatMap { it.readText().split('\n') }
            .filter { it.startsWith("|") && "Russian alphabet-" in it }
            .flatMap { row ->
                val cells = row.split("||")
                val pair = TwoPartPhraseRule.unstressed(cells[1].replace("''", "")).trim()
                listOf(pair) + pair.split(' ') + langValue.findAll(cells[2]).map { TwoPartPhraseRule.unstressed(it.groupValues[1]).trim() }
            }.filter { it.isNotEmpty() }.toSet()
    }

    private fun onAlphabetPage(item: String): Boolean = TwoPartPhraseRule.unstressed(item).trim() in alphabetCells

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
        dev.russiancompanion.v2.curriculum.V2LearnerFields.fields(pack).map { Field(it.unit, it.ownerField, it.text, it.kind) }

    /**
     * T15h (owner decision D25e): Cyrillic in an offline notice field is accepted only if the whole field value exactly
     * equals a title or attribution that a catalog/sources FREEZE.json records for that source (its page entry, or the
     * title of the licence route that names it). A licence-URL field may also equal the url that licence route records
     * (owner decision 2026-11-20).
     */
    private val routeUrls = HashMap<String, MutableSet<String>>()
    private val recordedNoticeText: Map<String, Set<String>> by lazy {
        val out = HashMap<String, MutableSet<String>>()
        fun walk(element: kotlinx.serialization.json.JsonElement) {
            when (element) {
                is kotlinx.serialization.json.JsonObject -> {
                    val source = (element["sourceId"] as? kotlinx.serialization.json.JsonPrimitive)?.content
                    val values = listOfNotNull(element["title"], element["attribution"]).mapNotNull { (it as? kotlinx.serialization.json.JsonPrimitive)?.content }
                    if (source != null) out.getOrPut(source) { HashSet() } += values
                    (element["appliesToSourceIds"] as? kotlinx.serialization.json.JsonArray)?.forEach { id ->
                        val sid = (id as kotlinx.serialization.json.JsonPrimitive).content
                        (element["title"] as? kotlinx.serialization.json.JsonPrimitive)?.content?.let { out.getOrPut(sid) { HashSet() } += it }
                        (element["url"] as? kotlinx.serialization.json.JsonPrimitive)?.content?.let { routeUrls.getOrPut(sid) { HashSet() } += it }
                    }
                    element.values.forEach(::walk)
                }
                is kotlinx.serialization.json.JsonArray -> element.forEach(::walk)
                else -> Unit
            }
        }
        File(root, "catalog/sources").listFiles()!!.mapNotNull { File(it, "FREEZE.json").takeIf(File::isFile) }
            .forEach { walk(kotlinx.serialization.json.Json.parseToJsonElement(it.readText())) }
        out
    }

    fun noticeAccepted(sourceId: String, value: String, kind: String = ""): Boolean =
        value.none { it.code in 0x0400..0x04FF } || value in recordedNoticeText[sourceId].orEmpty() ||
            (kind == "notice.licenseUrl" && value in recordedNoticeText.let { routeUrls[sourceId].orEmpty() })

    fun failures(fields: List<Field>): Set<Failure> {
        val failures = linkedSetOf<Failure>()
        fields.forEach { (n, owner, text, kind) ->
            if (kind.startsWith("notice.")) {
                // Notice fields are not phrase-audited; their Cyrillic must be recorded text (D25e).
                val sourceId = owner.substringBeforeLast('.')
                if (!noticeAccepted(sourceId, text, kind)) failures += Failure(n, owner, text, "NOTICE_UNRECORDED")
                return@forEach
            }
            sequence.findAll(text).forEach { m ->
                // T15h (owner decision D25b): the D21b English-boundary rule is deleted; every sequence is audited whole.
                val sq = m.value
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
