package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.*
import org.junit.Assert.*
import org.junit.Test
import java.io.File

/**
 * T16c-n2 (owner decisions D39b, D40a, D42a, approved rule change): every typed item in Units 1-30 cites the frozen cell or
 * line its key comes from (keyCitation), and the validator requires the key to equal that cited text exactly, stress included.
 * The (item, prompt, key, citation) tuples are pinned in typed-key-citations.tsv, so swapping a key for another taught form
 * fails validation, and swapping the citation with it fails this pin.
 */
class TypedKeyCitationTest {
    private val root = listOf(File("."), File("..")).first { File(it, "app/src/main").isDirectory }
    private val pack by lazy { V2PackLoader.parse(File(root, "app/src/main/assets/content/v2-curriculum.json").readText()) }
    private val typed by lazy { pack.items.filter { it.isTypedResponse() } }
    private fun citationErrors(p: V2CurriculumPack, id: String) =
        V2Validator.validate(p).errors.filter { "Typed item $id " in it && ("cites" in it || "key citation" in it || "key does not equal" in it) }

    @Test fun everyTypedItemIsPinnedToItsCitation() {
        val clean = { s: String -> s.replace("\t", " ").replace("\n", "\\n") }
        val rows = typed.map { item ->
            val record = pack.provenanceRecords.single { it.itemId == item.keyCitation }
            listOf(item.id, item.prompt, item.answer, record.itemId, record.sourceId, record.sourceRecordId).joinToString("\t") { clean(it) }
        }
        val pinned = File(root, "app/src/test/resources/typed-key-citations.tsv").readLines().drop(1).filter { it.isNotEmpty() }
        assertEquals(197, typed.size)
        assertEquals(pinned, rows)
        typed.forEach { assertEquals(it.id, emptyList<String>(), V2Validator.typedKeyCitationErrors(pack, it)) }
        assertTrue(V2Validator.validate(pack).errors.joinToString("\n"), V2Validator.validate(pack).ok)
    }

    @Test fun codexKeySwapAndStressChangesFail() {
        // Codex's T17c/T17d-check probe: u18-i-type-luchshii's key changed to another taught form (it passed before D39b).
        val item = pack.item("u18-i-type-luchshii")!!
        val other = "бо́льший"
        fun swap(answer: String, citation: String? = item.keyCitation) = pack.copy(items = pack.items.map {
            if (it.id != item.id) it else it.copy(answer = answer, keyCitation = citation,
                typedAnswerKey = it.typedAnswerKey!!.copy(acceptedAnswers = listOf(answer)))
        })
        assertTrue(citationErrors(swap(other), item.id).isNotEmpty())
        // The same key without its stress mark is not the cited text either.
        assertTrue(citationErrors(swap(item.answer.replace("́", "")), item.id).isNotEmpty())
        // A missing citation, or one naming a content-audit record, fails.
        assertTrue(citationErrors(swap(item.answer, null), item.id).isNotEmpty())
        val synthesis = pack.provenanceRecords.first { it.sourceId == "src-content-audit-research" }
        assertTrue(citationErrors(swap(item.answer, synthesis.itemId), item.id).isNotEmpty())
        // If the citation is swapped together with the key, the pin catches it.
        val otherRecord = pack.provenanceRecords.firstOrNull { it.shippedText == other && V2Validator.isFrozenSourceRecord(pack, it) }
        if (otherRecord != null) {
            val swapped = swap(other, otherRecord.itemId).items.single { it.id == item.id }
            val pinned = File(root, "app/src/test/resources/typed-key-citations.tsv").readLines()
            assertFalse(pinned.any { it.startsWith("${swapped.id}\t") && it.contains("\t${swapped.answer}\t${otherRecord.itemId}\t") })
        }
    }

    @Test fun firstLetterCaseOnlyWhereThePromptAsksForAQuestionWord() {
        // D42a: the capitalized Unit 5 question words cite lowercase OpenRussian cells; only the first letter's case differs.
        val kuda = pack.item("u5-i-type-kuda")!!
        assertEquals("Type the question word that has this meaning.", kuda.prompt)
        assertTrue(V2Validator.typedKeyCitationErrors(pack, kuda).isEmpty())
        // The same key under any other prompt fails.
        assertTrue(V2Validator.typedKeyCitationErrors(pack, kuda.copy(prompt = "Type the word that has this meaning.")).isNotEmpty())
        // A difference beyond the first letter's case fails (all capitals; a lost stress mark).
        val record = pack.provenanceRecords.single { it.itemId == kuda.keyCitation }
        val shouting = kuda.answer.uppercase()
        val p = pack.copy(provenanceRecords = pack.provenanceRecords.map { if (it.itemId == record.itemId) it.copy(shippedText = shouting) else it })
        assertTrue(V2Validator.typedKeyCitationErrors(p, kuda.copy(answer = shouting)).isNotEmpty())
        // D40a: the three letter items cite their letter cells on the frozen alphabet page (lines 59-171).
        val alphabet = File(root, "catalog/sources/units-26-30/wikipedia/russian-alphabet-1371795481.txt").readLines()
        for ((id, line) in listOf("u1-i-type-й" to 94, "u1-i-type-ъ" to 162, "u1-i-type-ь" to 170)) {
            val letter = pack.item(id)!!
            val cited = pack.provenanceRecords.single { it.itemId == letter.keyCitation }
            assertEquals(id, alphabet[line - 1], cited.originalSourceText)
            assertEquals(letter.answer, cited.shippedText)
            assertTrue(V2Validator.containsWhole(cited.originalSourceText, letter.answer))
        }
    }
}
