package dev.russiancompanion.v2

import dev.russiancompanion.testing.pythonProcess
import dev.russiancompanion.v2.curriculum.V2PackLoader
import org.junit.Assert.*
import org.junit.Test
import java.io.File

/**
 * T15f Part A2 (owner decisions D7 prerequisite): the Wikibooks formatter (catalog/units_3_5_intake.py,
 * used by catalog/units_16_20_batch_a.py) reads a bold vowel inside a word as stress, but a bold
 * word-initial vowel is stress only when a frozen OpenRussian form gives that word with that stress.
 * A bold prefix or letter (russian-grammar-1369617627.txt:1383) is not turned into an acute.
 */
class BoldVowelStressTest {
    private val root = listOf(File("."), File("..")).first { File(it, "app/src/main").isDirectory }
    private val acute = "́"

    private fun format(path: String, line: Int): String {
        val script = """
            import sys, pathlib
            sys.path.insert(0, '.')
            from units_3_5_intake import format_wikibooks_emphasis
            text = pathlib.Path('sources/$path').read_text(encoding='utf-8').splitlines()[${line - 1}]
            sys.stdout.buffer.write(format_wikibooks_emphasis(text).encode('utf-8'))
        """.trimIndent()
        val process = pythonProcess("-I", "-c", script).directory(File(root, "catalog")).start()
        val output = process.inputStream.readBytes().toString(Charsets.UTF_8)
        val errors = process.errorStream.readBytes().toString(Charsets.UTF_8)
        assertEquals(errors, 0, process.waitFor())
        return output
    }

    @Test fun boldPrefixLetterIsNotStress() {
        val text = format("units-26-30/wikipedia/russian-grammar-1369617627.txt", 1383)
        val fly = "улетят"
        val left = "ушёл"
        assertTrue(text, fly in text && left in text)
        assertFalse("regression: the bold prefix became an acute", "у${acute}" in text)
    }

    @Test fun boldLetterInTheAlphabetTableIsNotStress() {
        val text = format("units-26-30/wikipedia/russian-alphabet-1371795481.txt", 116)
        assertFalse(text, "о${acute}н" in text)
        assertTrue(text, "он" in text)
    }

    @Test fun realStressEvidenceIsKept() {
        // interrogative-4220316.txt:187 marks stress inside words and on the first vowel of е́здят, which
        // OpenRussian gives with that stress (verbs.csv presfut_pl3).
        val text = format("units-3-5/wikibooks/interrogative-4220316.txt", 187)
        assertTrue(text, "е${acute}здят" in text)
        assertTrue(text, "Когда${acute}" in text)
        assertTrue(text, "Па${acute}вел" in text)
    }

    @Test fun theUnitTwentyLearnerTextUsesTheFrozenSpelling() {
        val pack = V2PackLoader.parse(File(root, "app/src/main/assets/content/v2-curriculum.json").readText())
        val misread = "у${acute}летят"
        val shown = pack.examples.map { it.display } + pack.units.flatMap { u -> u.sessions.flatMap { it.steps } }.map { it.body } +
            pack.items.flatMap { listOf(it.prompt, it.feedback) + it.options } + pack.provenanceRecords.map { it.shippedText }
        assertTrue(shown.none { misread in it })
        assertTrue(pack.examples.any { "Они улетят" in it.display })
    }
}
