package dev.russiancompanion.v2
import dev.russiancompanion.v2.curriculum.*
import org.junit.Assert.*
import org.junit.Test
import java.io.File
class ReferenceNoticeCorrectionTest {
    @Test fun currentBoundedAvailabilityDoesNotContradictItsOwnSourceNotice() {
        val p=V2PackLoader.parse((System.getProperty("reference.notice.baseline")?.let(::File) ?: File(if(File("app").isDirectory) "." else "..","app/src/main/assets/content/v2-curriculum.json")).readText())
        val earlier=p.references.single { it.id=="ref-u2125-attribution" }
        val later=p.references.single { it.id=="ref-u2630-attribution" }
        assertFalse(earlier.detail.contains("Completion over a period is not prepared"))
        // T16-n2 (D30): was "Bounded practice for completion over a period is implemented and review-pending". T16c-n2 (D38c): the
        // assessment-scope sentence "Practice for completion over a period covers the bounded examples here, not every temporal
        // clause." moved to audit/T16c-n2-attribution-dispositions.csv (was assertTrue).
        assertFalse(earlier.detail.contains("Practice for completion over a period"))
        assertFalse(later.detail.contains("dictionary use are not prepared"))
        // T16c-n2 (D38c): both assessment-scope sentences moved to audit/T16c-n2-attribution-dispositions.csv (was assertTrue).
        assertFalse(later.detail.contains("broad dictionary proficiency are not supplied"))
        assertFalse(later.detail.contains("implemented and review-pending"))
        val moved = File(if (File("app").isDirectory) "." else "..", "audit/T16c-n2-attribution-dispositions.csv").readText()
        assertTrue(moved.contains("broad dictionary proficiency are not supplied by these bounded tasks"))
        assertTrue(moved.contains("Practice for completion over a period covers the bounded examples here, not every temporal clause."))
        assertEquals("LICENSE_OR_ATTRIBUTION",earlier.classification)
        assertEquals("LICENSE_OR_ATTRIBUTION",later.classification)
        assertTrue(earlier.detail.contains("CC BY-SA 4.0"));assertTrue(later.detail.contains("CC BY-SA 4.0"))
        val result=V2Validator.validate(p)
        assertTrue(result.errors.joinToString("\n"),result.ok)
    }
}
