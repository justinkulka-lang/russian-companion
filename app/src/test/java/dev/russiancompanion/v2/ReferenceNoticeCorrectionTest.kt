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
        assertTrue(earlier.detail.contains("Bounded practice for completion over a period is implemented and review-pending"))
        assertFalse(later.detail.contains("dictionary use are not prepared"))
        assertTrue(later.detail.contains("broad dictionary proficiency are not supplied"))
        assertTrue(later.detail.contains("Bounded postal practice, past-passive stress, and a two-entry dictionary task are implemented and review-pending"))
        assertEquals("LICENSE_OR_ATTRIBUTION",earlier.classification)
        assertEquals("LICENSE_OR_ATTRIBUTION",later.classification)
        assertTrue(earlier.detail.contains("CC BY-SA 4.0"));assertTrue(later.detail.contains("CC BY-SA 4.0"))
        val result=V2Validator.validate(p)
        assertTrue(result.errors.joinToString("\n"),result.ok)
    }
}
