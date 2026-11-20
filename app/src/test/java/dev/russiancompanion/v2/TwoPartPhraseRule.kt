package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.V2Validator
import java.io.File

/**
 * The T15a-n3 two-part phrase rule, shared by the unit fact-provenance tests (T15f, owner decisions D7 prerequisite).
 * (a) presence: a frozen line, after stripping HTML and wiki markup, collapsing whitespace, ignoring case and removing
 * stress marks, contains the phrase as whole words, with ё and е kept distinct;
 * (b) stress: every stressed word of the phrase has that exact stress in some frozen row or line, compared as the B3
 * marker check compares forms (V2Validator.factWordForms).
 * T15f: a Wiktionary piped link [[target|display]] reads as its display text, which is what the page shows; the target is a
 * lemma the page does not display. Presence, exact stress and the ё/е distinction are otherwise unchanged.
 */
internal object TwoPartPhraseRule {
    private const val CYR = "\\u0400-\\u04FF"
    private val pipedLink = Regex("\\[\\[[^\\[\\]|]*\\|([^\\[\\]|]*)\\]\\]")

    // T15f (owner decision D13a): block-level HTML boundaries and <br> separate text; inline tags (span, a, b, i, em, ...)
    // join their adjacent text nodes directly, as BatchARemainingSources.htmlVisible does. Real whitespace is kept.
    // T15h (owner decision D25c, replacing the D18d block list): every HTML tag is a boundary except this fixed inline
    // list, so an unlisted or unknown element (center, dir, search, ...) never joins the words on either side of it.
    val INLINE_TAGS: List<String> = listOf(
        "span", "b", "i", "em", "strong", "a", "small", "sub", "sup", "u", "s", "abbr", "cite", "code", "mark", "q", "bdi",
        "bdo", "font", "wbr",
    )
    val blockTag = Regex("(?i)</?(?!(?:${INLINE_TAGS.joinToString("|")})(?![A-Za-z0-9-]))[A-Za-z][^>]*>")

    /** True when the named element separates text (anything outside INLINE_TAGS). */
    fun isBoundary(tag: String): Boolean = tag.lowercase() !in INLINE_TAGS

    private val anyTag = Regex("<[^>]+>")

    /** A block boundary becomes U+2029, which the whitespace collapse below keeps, so no phrase spans two blocks. */
    fun htmlText(text: String): String = anyTag.replace(blockTag.replace(text, "\u2029"), "")

    fun unstressed(text: String): String {
        val noHtml = htmlText(text)
        val displayed = pipedLink.replace(noHtml) { it.groupValues[1] }
        val noMarkup = Regex("'{2,}|\\[\\[|\\]\\]").replace(displayed, "")
        val stressMarks = Regex("(?<=[$CYR])'(?!')").replace(noMarkup, "́").replace("́", "")
        return Regex("[\\s\\u00a0\\u2009\\u202f]+").replace(stressMarks, " ").lowercase()
    }

    fun present(line: String, phrase: String): Boolean =
        Regex("(?<![$CYR])${Regex.escape(unstressed(phrase))}(?![$CYR])").containsMatchIn(unstressed(line))

    fun attests(line: String, phrase: String, stressForms: Set<String>): Boolean =
        present(line, phrase) && V2Validator.factWordForms(phrase).filter { '́' in it }.all { it in stressForms }

    fun frozenLines(root: File): List<String> = File(root, "catalog/sources").walkTopDown()
        .filter { it.isFile && it.extension in setOf("txt", "csv", "html") }.flatMap { it.readLines() }.toList()

    fun frozenStressForms(root: File): Set<String> = frozenLines(root).flatMap { V2Validator.factWordForms(it) }.toSet()
}
