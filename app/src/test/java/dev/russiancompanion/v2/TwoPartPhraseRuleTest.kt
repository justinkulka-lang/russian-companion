package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.V2PackLoader
import org.junit.Assert.*
import org.junit.Test
import java.io.File

/** T15f (owner decisions D7 prerequisite): Wiktionary piped links resolve to their display text; the rule stays as strict. */
class TwoPartPhraseRuleTest {
    private val root = listOf(File("."), File("..")).first { File(it, "app/src/main").isDirectory }
    private val stress by lazy { TwoPartPhraseRule.frozenStressForms(root) }
    private val pack by lazy { V2PackLoader.parse(File(root, "app/src/main/assets/content/v2-curriculum.json").readText()) }

    private fun line(path: String, number: Int) = File(root, path).readLines()[number - 1]
    private val dolzhen41 get() = line("catalog/sources/units-16-20/wiktionary/dolzhen-84965088.txt", 41)
    private fun poltora(number: Int) = line("catalog/sources/units-21-25/supplemental/poltora-89737719.txt", number)

    /** The matcher before T15f: [[ and ]] were removed, so a piped link read "target|display". */
    private fun legacyPresent(line: String, phrase: String): Boolean {
        fun strip(text: String): String {
            val noMarkup = Regex("'{2,}|\\[\\[|\\]\\]").replace(Regex("<[^>]+>").replace(text, " "), "")
            val marks = Regex("(?<=[\\u0400-\\u04FF])'(?!')").replace(noMarkup, "́").replace("́", "")
            return Regex("[\\s\\u00a0\\u2009\\u202f]+").replace(marks, " ").lowercase()
        }
        return Regex("(?<![\\u0400-\\u04FF])${Regex.escape(strip(phrase))}(?![\\u0400-\\u04FF])").containsMatchIn(strip(line))
    }

    private fun shown(unit: Int): String = pack.items.filter { it.objectiveId.startsWith("obj-u$unit-") }
        .joinToString("\n") { listOf(it.prompt, it.promptLetter.orEmpty(), it.feedback, it.answer).joinToString("\n") + it.options.joinToString("\n") } +
        pack.units.first { it.number == unit }.sessions.flatMap { it.steps }.joinToString("\n") { it.body } +
        pack.examples.joinToString("\n") { it.display }

    @Test fun unitNineteenPipedHourLinkNowAttestsTheShownSentence() {
        val phrase = "Затме́ние должно́ наступи́ть в три часа́"
        assertTrue(phrase in shown(19))
        assertTrue("[[час|часа́]]" in dolzhen41)
        assertFalse("regression: the old matcher missed the piped link", legacyPresent(dolzhen41, phrase))
        assertTrue(TwoPartPhraseRule.attests(dolzhen41, phrase, stress))
    }

    @Test fun unitTwentyTwoOneAndAHalfPhrasesNowAttest() {
        for ((number, phrase) in listOf(14 to "полтора́ рубля́", 16 to "из полу́тора рубле́й", 17 to "к полу́тора рубля́м")) {
            assertTrue(phrase, phrase in shown(22))
            assertFalse("regression: the old matcher missed $phrase", legacyPresent(poltora(number), phrase))
            assertTrue(phrase, TwoPartPhraseRule.attests(poltora(number), phrase, stress))
        }
    }

    @Test fun presenceStressAndYoStayAsStrictAsBefore() {
        // The link target is not displayed, so it does not attest a phrase.
        assertFalse(TwoPartPhraseRule.present(dolzhen41, "в три час"))
        assertFalse(TwoPartPhraseRule.present(poltora(14), "полтора рубль"))
        // Whole words only.
        assertFalse(TwoPartPhraseRule.present(poltora(14), "полтора рубл"))
        // A stress the frozen files never show fails even when the letters are present.
        assertTrue(TwoPartPhraseRule.present(poltora(14), "полто́ра рубля́"))
        assertFalse(TwoPartPhraseRule.attests(poltora(14), "полто́ра рубля́", stress))
        // ё and е stay distinct (genitive-4567338.txt:143 shows Без неё).
        val genitive143 = line("catalog/sources/units-6-10/wikibooks/genitive-4567338.txt", 143)
        assertTrue(TwoPartPhraseRule.present(genitive143, "Без неё"))
        assertFalse(TwoPartPhraseRule.present(genitive143, "Без нее"))
        // A phrase absent from the line fails.
        assertFalse(TwoPartPhraseRule.present(poltora(14), "полтора́ мину́ты"))
        // Unpiped links and bold markup read as before.
        assertEquals(legacyPresent(poltora(16), "полу́тора"), TwoPartPhraseRule.present(poltora(16), "полу́тора"))
    }

    /** T15f (owner decision D13a): adjacent inline HTML text nodes join directly; block boundaries still separate words. */
    @Test fun inlineHtmlSpansJoinAndBlockBoundariesStaySeparate() {
        val letter76 = line("catalog/sources/readyrussian/letter.html", 76)
        assertTrue("Иван</span><span class=\"T10\" lang=\"ru\">ов</span>" in letter76)
        // Regression: the old matcher put a space for every tag, giving "Иван Иван ов ич".
        assertFalse(legacyPresentHtml(letter76, "Уважаемый Иван Иванович"))
        assertTrue(TwoPartPhraseRule.present(letter76, "Уважаемый Иван Иванович"))
        assertTrue(TwoPartPhraseRule.attests(letter76, "Уважаемый Иван Иванович", stress))
        assertFalse(TwoPartPhraseRule.present(letter76, "Иван ов"))
        // Words from two separate block elements are not joined, whatever the tag.
        for (tag in listOf("p", "li", "td", "h3", "div")) {
            val blocks = "<$tag>Уважаемый</$tag><$tag>Иван</$tag>"
            assertFalse(tag, TwoPartPhraseRule.present(blocks, "Уважаемый Иван"))
            assertFalse(tag, TwoPartPhraseRule.present(blocks, "УважаемыйИван"))
        }
        assertFalse(TwoPartPhraseRule.present("Уважаемый<br>Иван", "Уважаемый Иван"))
        assertFalse(TwoPartPhraseRule.present("Уважаемый<br/>Иван", "Уважаемый Иван"))
        // Real whitespace between inline nodes is kept.
        assertTrue(TwoPartPhraseRule.present("<span>Уважаемый</span> <span>Иван</span>", "Уважаемый Иван"))
        assertFalse(TwoPartPhraseRule.present("<span>Уважаемый</span> <span>Иван</span>", "УважаемыйИван"))
        // Stress and ё/е stay as strict: an unattested stress still fails, ё is not е.
        assertFalse(TwoPartPhraseRule.attests("<span>Ива́н</span>", "Ива́н", setOf()))
        assertFalse(TwoPartPhraseRule.present("<span>нё</span>", "не"))
    }

    /** The matcher before D13a: every HTML tag became a space. */
    private fun legacyPresentHtml(line: String, phrase: String): Boolean {
        fun strip(text: String): String {
            val noHtml = Regex("<[^>]+>").replace(text, " ")
            val displayed = Regex("\\[\\[[^\\[\\]|]*\\|([^\\[\\]|]*)\\]\\]").replace(noHtml) { it.groupValues[1] }
            val noMarkup = Regex("'{2,}|\\[\\[|\\]\\]").replace(displayed, "")
            val marks = Regex("(?<=[\\u0400-\\u04FF])'(?!')").replace(noMarkup, "\u0301").replace("\u0301", "")
            return Regex("[\\s\\u00a0\\u2009\\u202f]+").replace(marks, " ").lowercase()
        }
        return Regex("(?<![\\u0400-\\u04FF])${Regex.escape(strip(phrase))}(?![\\u0400-\\u04FF])").containsMatchIn(strip(line))
    }
}
