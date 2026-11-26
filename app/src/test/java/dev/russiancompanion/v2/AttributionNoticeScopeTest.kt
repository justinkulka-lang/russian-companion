package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.V2PackLoader
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * T16c-n2 (owner decision D38c): a ref-*-attribution detail carries attribution only (sources, revisions, licence routes, what
 * material is and is not used, the share-alike and book notices). Assessment-scope narration ("bounded recognition", "not
 * complete mastery", "review-pending", "does not clear ...") lives in the internal dispositions file
 * audit/T16c-n2-attribution-dispositions.csv, not in learner text.
 */
class AttributionNoticeScopeTest {
    private val root = listOf(File("."), File("..")).first { File(it, "app/src/main").isDirectory }
    private val pack by lazy { V2PackLoader.parse(File(root, "app/src/main/assets/content/v2-curriculum.json").readText()) }
    private val scope = Regex(
        "\\b(?:bounded|recogni[sz]\\w*|master\\w*|proficien\\w*|productive|review-pending|implemented|" +
            "(?:not an?|is not an?) [\\w-]+ generator|does(?: not|n't) (?:establish|clear|demonstrate|gate|activate|state)|" +
            "do(?: not|n't) (?:establish|state|clear)|not supplied|not every|assessment)\\b",
        RegexOption.IGNORE_CASE,
    )

    @Test fun everyAttributionNoticeCarriesAttributionOnly() {
        val notices = pack.references.filter { it.id.startsWith("ref-") && it.id.endsWith("-attribution") }
        assertTrue(notices.size >= 9)
        val hits = notices.flatMap { ref -> scope.findAll(ref.detail).map { "${ref.id}: ${it.value}" }.toList() }
        assertTrue(hits.joinToString("\n"), hits.isEmpty())
        // The Unit 17 notice still names every source it attributes.
        val unit17 = notices.single { it.id == "ref-u17-attribution" }.detail
        for (source in listOf("Wikipedia oldid 1366690592", "Wikipedia oldid 1357134172", "Pressbooks notice", "ReadyRussian Grammar 20",
                "ReadyRussian Grammar 24")) assertTrue(source, unit17.contains(source))
    }

    @Test fun theMovedSentencesAreRecordedAsInternalDispositions() {
        val rows = File(root, "audit/T16c-n2-attribution-dispositions.csv").readLines()
        assertEquals("owner,field,removed_text,replacement,disposition,note,decision,task", rows.first())
        assertEquals(10, rows.size - 1)
        assertTrue(rows.drop(1).all { "MOVED_TO_INTERNAL_NOTES" in it && "D38c" in it })
        // Regression: the Unit 17 assessment-scope sentences Codex found would be caught if they came back.
        for (sentence in listOf("The three clock phrases are bounded recognition.",
                "Productive clock-time is the ReadyRussian Grammar 20 system applied to exact expressions.")) {
            assertTrue(sentence, scope.containsMatchIn(sentence))
        }
    }
}
