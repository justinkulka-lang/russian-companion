package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.*
import org.junit.Assert.*
import org.junit.Test
import java.io.File

class Unit17NumberInstructionTest {
    @Test fun meaningsRulesAndLimitsPrecedePractice() {
        val p = pack()
        val claims = mapOf(
            "ordinals" to listOf("first", "second", "third", "fourth", "gender, number and case", "fragment alone"),
            "hours" to listOf("one hour", "two hours", "five hours", "genitive singular", "11, 12, 13 or 14", "clock"),
            "years" to listOf("five years", "different stem", "forty-one years", "11 through 14", "calendar-year"),
            "minutes" to listOf("feminine", "genitive plural", "11 through 14", "minutes past"),
        )
        for ((slug, required) in claims) {
            val session = p.unit(17)!!.sessions.first { it.id == "u17-s-$slug" }
            val teaches = session.steps.takeWhile { it.kind != "GUIDED" }.filter { it.kind == "TEACH" }
            assertEquals(3, teaches.size)
            val body = teaches.joinToString("\n") { it.body }
            required.forEach { assertTrue("$slug missing $it", body.contains(it)) }
            val remediation = session.steps.first { it.kind == "REMEDIATE" }
            assertEquals(teaches.joinToString("\n\n") { it.body }, remediation.body)
            assertEquals(teaches.flatMap { it.exampleIds }.distinct(), remediation.exampleIds)
        }
    }

    @Test fun choicesReceiveSpecificMeaningFeedback() {
        val p = pack()
        val items = p.items.filter { it.objectiveId in listOf("ordinals", "hours", "years", "minutes").map { "obj-u17-$it" } }
        assertEquals(12, items.size)
        for (i in items) {
            assertTrue(i.feedback.contains(i.answer))
            assertTrue(i.feedback.contains("means"))
            assertTrue(i.feedback.contains("shown cue differs"))
            assertEquals(i.feedback, i.assessment!!.whyAnswerDemonstratesObjective)
            assertEquals(i.distractorRationales, i.assessment!!.distractorDifference)
            i.options.forEachIndexed { index, option -> if (option != i.answer) assertTrue(i.distractorRationales[index].contains(option)) }
        }
    }

    @Test fun coverageIsOriginalPendingAndCorpusValid() {
        val p = pack()
        val records = p.provenanceRecords.filter { it.itemId.startsWith("prov-ca-u17-numbers-") }
        assertEquals(83, records.size)
        records.forEach { assertContentAuditResearch(it) }
        assertTrue(records.all { it.synthesis!!.bibliography.contains("oldid 4371358") })
        val v = V2Validator.validate(p)
        assertTrue(v.errors.joinToString("\n"), v.ok)
    }

    private fun pack(): V2CurriculumPack {
        val file = System.getProperty("unit17.numbers.baseline")?.let(::File)
            ?: File(if (File("app").isDirectory) "." else "..", "app/src/main/assets/content/v2-curriculum.json")
        return V2PackLoader.parse(file.readText())
    }
}
