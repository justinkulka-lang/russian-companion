package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.*
import dev.russiancompanion.v2.ui.selectedWrongOptionRationale
import org.junit.Assert.*
import org.junit.Test
import java.io.File

/** Presentation-boundary regressions from WC001; no linguistic or rights acceptance. */
class RationaleQualityBoundaryTest {
    @Test fun sourceTemplateMustNotBePresentedAsAnOptionExplanation() {
        // T17f (WC001) expectation change, old -> new: the test read the template splice from the live u17-g-clock-0 rationale;
        // T17f rewrote all 26 Unit 17 splices ("Which book do you want? asks which book someone wants, not the current clock
        // time."), so no live item carries it. As in differentShownFormMustNotBePresentedAsTheDisplayedCue, the retired splice is
        // kept as a fixture on a copy of the item; the boundary assertion (template text falls back to null) is unchanged, and the
        // live rewritten rationale is now presented.
        val live = pack.item("u17-g-clock-0")!!
        val wrong = live.options.first { it != live.answer }
        val retired = "$wrong is a different meaning or construction and does not match the reading of the shown form."
        val item = live.copy(distractorRationales = live.options.map { if (it == wrong) retired else "" })
        assertTrue(item.distractorRationales[item.options.indexOf(wrong)].contains("is a different meaning or construction and does not match"))
        assertNull("Known boilerplate must use explanation/reference fallback", selectedWrongOptionRationale(item, wrong))
        assertEquals(live.distractorRationales[live.options.indexOf(wrong)], selectedWrongOptionRationale(live, wrong))
    }

    @Test fun rejectedReadingRestatementMustNotBePresentedAsAnExplanation() {
        val item = pack.item("u27-g-stress-1")!!
        val wrong = item.options.first { it != item.answer }
        assertTrue(item.distractorRationales[item.options.indexOf(wrong)].startsWith("Rejected reading:"))
        assertNull(selectedWrongOptionRationale(item, wrong))
    }

    @Test fun differentShownFormMustNotBePresentedAsTheDisplayedCue() {
        // T15b rewrote the live u7-g-adverb-0 rationales; the boundary keeps the retired pair-frame rationale as a fixture.
        val live = pack.item("u7-g-adverb-0")!!
        val wrong = live.options.first { it != live.answer }
        val retired = "The frozen source pairs the shown form \"хорошо\" with the admitted gloss \"well\" (\"while in Russian\")."
        val item = live.copy(distractorRationales = live.options.map { if (it == wrong) retired else "" })
        val rationale = item.distractorRationales[item.options.indexOf(wrong)]
        assertEquals(live.distractorRationales[live.options.indexOf(wrong)], selectedWrongOptionRationale(live, wrong))
        assertTrue(rationale.contains("pairs the shown form"))
        assertFalse(rationale.contains("\"${item.promptLetter}\""))
        assertNull(selectedWrongOptionRationale(item, wrong))
    }

    @Test fun specificExistingExplanationStillFollowsTheAuthoredOptionValue() {
        val item = pack.item("u7-i-endings-4")!!
        val reordered = item.copy(options = item.options.reversed(), distractorRationales = item.distractorRationales.reversed())
        for (wrong in item.options.filter { it != item.answer }) {
            val expected = item.distractorRationales[item.options.indexOf(wrong)]
            assertTrue(expected.isNotBlank())
            assertEquals(expected, selectedWrongOptionRationale(item, wrong))
            assertEquals(expected, selectedWrongOptionRationale(reordered, wrong))
        }
    }

    @Test fun correctSelectionDoesNotAcquireAWrongOptionExplanation() {
        val item = pack.item("u7-i-endings-4")!!
        assertNull(selectedWrongOptionRationale(item, item.answer))
    }

    @Test fun unknownSelectionDoesNotAcquireAnotherOptionsExplanation() {
        assertNull(selectedWrongOptionRationale(pack.item("u7-i-endings-4")!!, "not-a-listed-option"))
    }

    @Test fun missingAuthoredExplanationRetainsTheExistingFallback() {
        val item = pack.item("u7-i-endings-4")!!
        val missing = item.copy(distractorRationales = item.options.map { "" })
        assertNull(selectedWrongOptionRationale(missing, item.options.first { it != item.answer }))
    }

    companion object {
        private val root = File(if (File("app").isDirectory) "." else "..")
        private val pack by lazy { V2PackLoader.parse(File(root, "app/src/main/assets/content/v2-curriculum.json").readText()) }
    }
}
