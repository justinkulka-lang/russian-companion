package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.*
import dev.russiancompanion.v2.ui.selectedWrongOptionRationale
import org.junit.Assert.*
import org.junit.Test
import java.io.File

/** WC001/CC030 correction boundaries, not linguistic acceptance or approval. */
class LiteracyAssessmentQualityTest {
    @Test fun unitOneRequiredChecksExplainEachWrongOptionByItsValue() = explanations(1)
    @Test fun unitTwoRequiredChecksExplainEachWrongOptionByItsValue() = explanations(2)

    @Test fun unitTwoRequiredReviewDoesNotRepeatAnEntireUnitOneDisplayTask() {
        val first = required(1).map(::signature).toSet()
        val repeated = required(2).filter { signature(it) in first }.map { it.id }
        assertTrue("Literal task replays are not new assessment variants: $repeated", repeated.isEmpty())
    }

    @Test fun latinAndCyrillicLookalikesRemainDifferentUnicodeIdentities() {
        assertNotEquals("Latin A is not Cyrillic А", 'A', 'А')
        assertNotEquals("Latin B is not Cyrillic В", 'B', 'В')
        assertNotEquals("Latin X is not Cyrillic Х", 'X', 'Х')
        assertEquals('а', 'А'.lowercaseChar())
        assertEquals('в', 'В'.lowercaseChar())
    }

    @Test fun currentCompatibilityValidationStillAcceptsThePack() {
        val result = V2Validator.validate(pack)
        assertTrue(result.errors.joinToString("\n"), result.ok)
    }

    @Test fun literacyWorkRemainsRequiredAndItsReviewRemainsPending() {
        for (n in 1..2) {
            assertTrue(required(n).isNotEmpty())
            assertTrue(required(n).all { it.reviewStatus == "IMPLEMENTED_PENDING_FINAL_AUDIT" })
            assertEquals("DEVELOPMENT", pack.unit(n)!!.installState)
        }
        assertFalse(pack.publicReleaseEligible)
    }

    private fun explanations(n: Int) {
        val missing = required(n).flatMap { item ->
            item.options.filter { it != item.answer }.mapNotNull { wrong ->
                if (selectedWrongOptionRationale(item, wrong).isNullOrBlank()) "${item.id}: $wrong" else null
            }
        }
        assertTrue("Every required wrong option needs an authored explanation: $missing", missing.isEmpty())
        for (item in required(n)) {
            val reversed = item.copy(options = item.options.reversed(), distractorRationales = item.distractorRationales.reversed())
            for (wrong in item.options.filter { it != item.answer }) {
                assertEquals(selectedWrongOptionRationale(item, wrong), selectedWrongOptionRationale(reversed, wrong))
            }
            assertNull(selectedWrongOptionRationale(item, item.answer))
        }
    }
    private fun required(n: Int): List<V2CheckItem> = pack.unit(n)!!.objectives
        .filter { it.gating && it.kind == "RECOGNITION" }.flatMap { it.checkItemIds }.distinct().map { pack.item(it)!! }
    // Preserve the complete visible task, including stress and case. Hidden provenance is excluded.
    private fun signature(i: V2CheckItem): List<Any> = listOf(i.kind, i.language, i.prompt,
        if (i.promptLetterRole == "CUE") i.promptLetter.orEmpty() else "", i.options.sorted(), i.answer)
    companion object {
        private val root = File(if (File("app").isDirectory) "." else "..")
        private val pack by lazy { V2PackLoader.parse(File(root, "app/src/main/assets/content/v2-curriculum.json").readText()) }
    }
}
