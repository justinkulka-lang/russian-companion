package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.*
import org.junit.Assert.*
import org.junit.Test
import java.io.File

class ContentAuditCheckpointRepairTest {
    @Test fun imperfectiveChecksAssessTheirOwnAspectAndASeparateSubjectContrast() {
        val pack = pack()
        val unit = pack.unit(28)!!
        val objective = unit.objectives.first { it.id == "obj-u28-imperfective-adverb" }
        val checks = objective.checkItemIds.map { pack.item(it)!! }
        assertEquals(setOf("IMPERFECTIVE_ADVERB_SIMULTANEOUS", "VERBAL_ADVERB_SHARED_SUBJECT"),
            checks.map { it.assessment!!.testedFeature }.toSet())
        assertTrue(checks.all { it.promptLetter!!.contains("читая") })
        assertTrue(checks.none { it.promptLetter!!.contains("написав") })
        assertEquals(checks.size, checks.map { Triple(it.promptLetter, it.options, it.answer) }.toSet().size)
        assertFalse(pack.examples.filter { it.objectiveId.startsWith("obj-u28-") }
            .any { it.note == "usually present" || it.note == "can only be past" })
    }

    @Test fun optionalStressHasItsOwnProvenanceAndActualResponseShape() {
        val pack = pack()
        for (id in listOf("u27-g-stress-1", "u27-i-stress-1")) {
            val item = pack.item(id)!!
            assertEquals("obj-u27-present-passive", item.objectiveId)
            assertEquals("RUSSIAN_TOKEN", item.assessment!!.responseKind)
            val records = item.assessment.relationParticipantIds.map { pid ->
                pack.provenanceRecords.first { it.itemId == pid }
            }
            assertTrue(records.all { it.objectiveId == item.objectiveId && it.unitNumber == 27 })
        }
    }

    @Test fun correctedAdmissionRejectsHashScopeMissingFeatureAndClonedCheck() {
        val pack = pack()
        val cap = pack.sourceCapabilities.first { it.id == "cap-obj-u28-imperfective-adverb" }
        assertTrue(correctedCapabilityDefects(pack, cap).isEmpty())
        assertFalse(correctedCapabilityDefects(pack, cap.copy(exactSpan = cap.exactSpan + "changed")).isEmpty())
        assertFalse(correctedCapabilityDefects(pack, cap.copy(unitNumber = 27)).isEmpty())
        assertFalse(correctedCapabilityDefects(pack, cap.copy(objectiveId = "obj-u28-perfective-adverb")).isEmpty())
        val original = pack.item("u28-i-imperfective-adverb-1")!!
        val missing = pack.copy(items = pack.items.map { item ->
            if (item.id == original.id) item.copy(assessment = item.assessment!!.copy(testedFeature = "UNRELATED")) else item
        })
        assertFalse(correctedCapabilityDefects(missing, cap).isEmpty())
        val clone = original.copy(id = "test-cloned-independent-check")
        val duplicated = pack.copy(items = pack.items + clone, units = pack.units.map { unit ->
            if (unit.number != 28) unit else unit.copy(objectives = unit.objectives.map { objective ->
                if (objective.id != cap.objectiveId) objective else
                    objective.copy(checkItemIds = objective.checkItemIds + clone.id)
            })
        })
        assertTrue(correctedCapabilityDefects(duplicated, cap).any { "duplicates" in it })
    }

    private fun pack(): V2CurriculumPack {
        val root = listOf(File("."), File("..")).first { File(it, "app/src/main").isDirectory }
        return V2PackLoader.parse(File(root, "app/src/main/assets/content/v2-curriculum.json").readText())
    }
}
