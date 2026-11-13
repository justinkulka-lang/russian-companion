package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.*
import dev.russiancompanion.v2.ui.selectedWrongOptionRationale
import org.junit.Assert.*
import org.junit.Test
import java.io.File

/** Presentation-boundary regressions from WC001; no linguistic or rights acceptance. */
class RationaleQualityBoundaryTest {
    @Test fun sourceTemplateMustNotBePresentedAsAnOptionExplanation() {
        val item = pack.item("u17-g-clock-0")!!
        val wrong = item.options.first { it != item.answer }
        assertTrue(item.distractorRationales[item.options.indexOf(wrong)].contains("is a different meaning or construction and does not match"))
        assertNull("Known boilerplate must use explanation/reference fallback", selectedWrongOptionRationale(item, wrong))
    }

    @Test fun rejectedReadingRestatementMustNotBePresentedAsAnExplanation() {
        val item = pack.item("u27-g-stress-1")!!
        val wrong = item.options.first { it != item.answer }
        assertTrue(item.distractorRationales[item.options.indexOf(wrong)].startsWith("Rejected reading:"))
        assertNull(selectedWrongOptionRationale(item, wrong))
    }

    @Test fun differentShownFormMustNotBePresentedAsTheDisplayedCue() {
        val item = pack.item("u7-g-adverb-0")!!
        val wrong = item.options.first { it != item.answer }
        val rationale = item.distractorRationales[item.options.indexOf(wrong)]
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
