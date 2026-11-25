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
    // T16c-n3: the filter also keeps the D46b accepted-answer messages ("accepted answer ... citation", "... does not equal").
    private fun citationErrors(p: V2CurriculumPack, id: String) =
        V2Validator.validate(p).errors.filter { "Typed item $id " in it && ("cites" in it || "citation" in it || "does not equal" in it) }

    @Test fun everyTypedItemIsPinnedToItsCitation() {
        val clean = { s: String -> s.replace("\t", " ").replace("\n", "\\n") }
        // T16c-n3 expectation change (owner decision D46b): old: one pinned row per typed item (its key). New: the key row, then
        // one row per other accepted answer with its own citation (197 key rows + 4 accepted-answer rows = 201).
        val rows = typed.flatMap { item ->
            val cited = listOf(item.answer to item.keyCitation!!) + item.acceptedAnswerCitations.toList()
            cited.map { (text, citation) ->
                val record = pack.provenanceRecords.single { it.itemId == citation }
                listOf(item.id, item.prompt, text, record.itemId, record.sourceId, record.sourceRecordId).joinToString("\t") { clean(it) }
            }
        }
        val pinned = File(root, "app/src/test/resources/typed-key-citations.tsv").readLines().drop(1).filter { it.isNotEmpty() }
        // T17e expectation change: 197 -> 211 typed items and 201 -> 215 pinned rows (Unit 20's twelve typed checks and two typed
        // guided items, each citing the frozen G line or OpenRussian cell its key comes from).
        assertEquals(211, typed.size)
        assertEquals(215, rows.size)
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

    @Test fun everyAcceptedAnswerMeetsTheKeyRule() {
        // D46b audit: the only accepted answers other than a key are the four unstressed Unit 5 question words, each an exact
        // frozen OpenRussian cell (first-letter case under the question-word prompt). No shipped item accepts an uncited form.
        val extra = typed.flatMap { i -> i.typedAnswerKey!!.acceptedAnswers.filter { it != i.answer }.map { i.id to it } }
        assertEquals(listOf("u5-i-type-kuda" to "Куда", "u5-i-type-otkuda" to "Откуда", "u5-i-type-skolko" to "Сколько",
            "u5-i-type-kakoy" to "Какой"), extra)
        for ((id, answer) in extra) {
            val item = pack.item(id)!!
            assertEquals(setOf(answer), item.acceptedAnswerCitations.keys)
            assertEquals(answer, pack.provenanceRecords.single { it.itemId == item.acceptedAnswerCitations.getValue(answer) }.shippedText)
        }
        val kuda = pack.item("u5-i-type-kuda")!!
        fun with(item: V2CheckItem) = pack.copy(items = pack.items.map { if (it.id == item.id) item else it })
        fun acceptAlso(answer: String, citations: Map<String, String> = kuda.acceptedAnswerCitations) = kuda.copy(
            typedAnswerKey = kuda.typedAnswerKey!!.copy(acceptedAnswers = kuda.typedAnswerKey!!.acceptedAnswers + answer),
            acceptedAnswerCitations = citations)
        // Probes: an uncited extra form; a wrong form cited to a real record; a form cited to a record that ships other text;
        // a citation to a content-audit record; the citation removed from a shipped variant; a stray citation.
        val otherForm = "Откуда"
        val otkudaCell = pack.item("u5-i-type-otkuda")!!.acceptedAnswerCitations.getValue(otherForm)
        val synthesis = pack.provenanceRecords.first { it.sourceId == "src-content-audit-research" }.itemId
        val probes = listOf(
            acceptAlso("Кудаа"),
            acceptAlso(otherForm),
            acceptAlso("Кудаа", kuda.acceptedAnswerCitations + ("Кудаа" to otkudaCell)),
            acceptAlso("Кудаа", kuda.acceptedAnswerCitations + ("Кудаа" to synthesis)),
            kuda.copy(acceptedAnswerCitations = emptyMap()),
            kuda.copy(acceptedAnswerCitations = kuda.acceptedAnswerCitations + ("Куда́" to kuda.keyCitation!!)),
            kuda.copy(acceptedAnswerCitations = kuda.acceptedAnswerCitations + ("Куды" to otkudaCell)),
        )
        for (probe in probes) {
            assertTrue(probe.toString(), V2Validator.typedKeyCitationErrors(with(probe), probe).isNotEmpty())
            assertTrue(citationErrors(with(probe), probe.id).isNotEmpty())
        }
        // The same answer under another prompt loses the first-letter allowance and fails too.
        assertTrue(V2Validator.typedKeyCitationErrors(pack, kuda.copy(prompt = "Type the word that has this meaning.")).isNotEmpty())
        assertTrue(V2Validator.typedKeyCitationErrors(pack, kuda).isEmpty())
    }

    @Test fun letterCellCitationsCheckTheLetterAndKeepWikiMarkupOutOfLearnerText() {
        // D43d (owner approval of the raw letter-cell citation, with two conditions).
        // (1) Typed-key equality is checked against the letter within the cited line (V2Validator.typedKeyCitationErrors: the
        //     record's shipped text must equal the key, and the key must occur as a whole word in the frozen line): a letter
        //     key cited to another letter's cell fails, as does a key that is not the cell's letter.
        val ids = listOf("u1-i-type-й", "u1-i-type-ъ", "u1-i-type-ь")
        val letters = ids.map { pack.item(it)!! }
        for (letter in letters) {
            val own = pack.provenanceRecords.single { it.itemId == letter.keyCitation }
            assertEquals(letter.answer, own.shippedText)
            for (other in letters.filter { it.id != letter.id }) {
                assertTrue("${letter.id} cited to ${other.keyCitation}",
                    V2Validator.typedKeyCitationErrors(pack, letter.copy(keyCitation = other.keyCitation)).isNotEmpty())
                val moved = pack.copy(provenanceRecords = pack.provenanceRecords.map {
                    if (it.itemId == own.itemId) it.copy(shippedText = other.answer) else it
                })
                assertTrue("${letter.id} keyed ${other.answer}", V2Validator.typedKeyCitationErrors(moved,
                    letter.copy(answer = other.answer)).isNotEmpty())
            }
        }
        // (2) No [[ or ]] reaches learner-visible text: the frozen line and record locator are provenance data, never a
        //     learner field (the offline notice shows only a record's licence URL and share-alike text), and no learner
        //     field anywhere in the pack (rendered or declared-but-scanned, Units 1-30 and the notices) contains either marker.
        assertTrue(V2LearnerFields.KINDS.none { "originalSourceText" in it || "sourceRecordId" in it || "provenance" in it })
        val wiki = Regex("\\[\\[|\\]\\]")
        val leaks = V2LearnerFields.fields(pack).filter { wiki.containsMatchIn(it.text) }.map { "${it.unit} ${it.ownerField}" }
        assertTrue(leaks.joinToString("\n"), leaks.isEmpty())
        val typedFields = typed.flatMap { listOf(it.prompt, it.answer, it.feedback, it.promptLetter.orEmpty()) +
            it.options + it.typedAnswerKey!!.acceptedAnswers }
        assertTrue(typedFields.none { wiki.containsMatchIn(it) })
        // Regression: the cited line itself does carry the markup, so a leak of it would be caught above.
        assertTrue(wiki.containsMatchIn(pack.provenanceRecords.single { it.itemId == letters.first().keyCitation }.originalSourceText))
    }
}
