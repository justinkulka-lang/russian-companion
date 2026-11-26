package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.*
import org.junit.Assert.*
import org.junit.Test
import java.io.File

/**
 * T17f (owner decision D49b, Codex T16c-n3 final check LOW-3): an item that fails its item-level check (structure or feature
 * validity) fails full-pack validation. Before D49b, Codex's "half of two hours" and "double two hours" prompts on
 * u17-i-hours-1 failed the item check but the pack still validated, because the objective's other checks met the aggregate
 * requirements.
 */
class ItemInvalidityFailsValidationTest {
    private val pack by lazy { V2PackLoader.parse(File(if (File("app").isDirectory) "." else "..", "app/src/main/assets/content/v2-curriculum.json").readText()) }

    private fun with(item: V2CheckItem) = pack.copy(items = pack.items.map { if (it.id == item.id) item else it })

    @Test fun theShippedPackValidatesAndEveryAssessedItemIsStructureValid() {
        val result = V2Validator.validate(pack)
        assertTrue(result.errors.joinToString("\n"), result.ok)
        val invalid = pack.items.filter { (it.kind in GRAMMAR_ITEM_KINDS || it.assessment != null) && V2Validator.structureValid(pack, it).isNotEmpty() }
        assertEquals(invalid.map { it.id }.toString(), emptyList<V2CheckItem>(), invalid)
        // The one shipped item that failed its check unreported (since the T17a prompt rewording) now passes it.
        val pencil = pack.item("u16-i-inst-sg-2")!!
        assertEquals("Which instrumental singular noun means pencil?", pencil.prompt)
        assertTrue(V2Validator.featureValid(pack, pencil))
    }

    @Test fun codexQuantityProbesOnTheHoursItemFailFullValidation() {
        val item = pack.item("u17-i-hours-1")!!
        for (prompt in listOf("Which phrase means half of two hours?", "Which phrase means double two hours?")) {
            val probe = item.copy(prompt = prompt)
            val changed = with(probe)
            assertTrue(prompt, V2Validator.structureValid(changed, probe).isNotEmpty())
            assertFalse(prompt, V2Validator.featureValid(changed, probe))
            val result = V2Validator.validate(changed)
            assertFalse("$prompt passed full validation", result.ok)
            assertTrue(prompt, result.errors.any { "Item u17-i-hours-1 " in it })
        }
        // The control: the shipped two-hour prompt passes both.
        assertTrue(V2Validator.validate(pack).errors.none { "Item u17-i-hours-1 " in it })
    }

    @Test fun anItemCheckFailureInAnyUnitFailsFullValidation() {
        // One assessed item from each block, including units outside the old 6-15 / 21-30 / Batch A scope (Units 1-5, 16-20).
        val probes = pack.items.filter { it.assessment != null }.groupBy { it.objectiveId.removePrefix("obj-u").substringBefore("-").toIntOrNull() ?: 0 }
            .filterKeys { it in listOf(3, 4, 5, 16, 17, 18, 19, 20) }.mapValues { (_, items) -> items.first() }
        assertTrue(probes.keys.toString(), probes.keys.containsAll(listOf(16, 17, 18, 19, 20)))
        for ((unit, item) in probes) {
            // An empty answer rationale fails the item check; the objective's other checks would still meet the aggregate.
            val probe = item.copy(assessment = item.assessment!!.copy(whyAnswerDemonstratesObjective = ""))
            val changed = with(probe)
            assertTrue("Unit $unit ${item.id}", V2Validator.structureValid(changed, probe).isNotEmpty())
            assertFalse("Unit $unit ${item.id} passed full validation", V2Validator.validate(changed).ok)
        }
        // A grammar-kind item with its assessment removed fails too.
        val item = pack.item("u16-i-inst-sg-2")!!
        assertFalse(V2Validator.validate(with(item.copy(assessment = null))).ok)
    }
}
