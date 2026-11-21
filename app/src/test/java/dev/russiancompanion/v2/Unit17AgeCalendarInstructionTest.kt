package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.*
import org.junit.Assert.*
import org.junit.Test
import java.io.File

class Unit17AgeCalendarInstructionTest {
    @Test fun checkedNamesAndAgeStructureAreTaughtBeforePractice() {
        val p = pack()
        for (slug in listOf("age", "weekdays", "months")) {
            val s = p.unit(17)!!.sessions.first { it.id == "u17-s-$slug" }
            val t = s.steps.takeWhile { it.kind != "GUIDED" }.filter { it.kind == "TEACH" }
            assertEquals(if (slug == "age") 2 else 3, t.size)
            val body = t.joinToString("\n") { it.body }
            p.items.filter { it.objectiveId == "obj-u17-$slug" }.forEach { i ->
                assertTrue("${i.id} cue not taught", body.contains(i.promptLetter!!))
                assertTrue("${i.id} meaning not taught", body.contains(i.answer))
                if (slug != "age") i.options.forEach { assertTrue("${i.id} alternative $it not taught", body.contains(it)) }
            }
            val rem = s.steps.first { it.kind == "REMEDIATE" }
            assertEquals(t.joinToString("\n\n") { it.body }, rem.body)
            assertEquals(t.flatMap { it.exampleIds }.distinct(), rem.exampleIds)
            assertTrue(body.contains(if (slug == "age") "dative" else "lowercase"))
            assertTrue(body.contains(if (slug == "age") "no expressed verb" else "sentence"))
        }
    }
    @Test fun learnerFeedbackExplainsMeaningsAndRetainsSourceCitation() {
        val p = pack()
        val items = p.items.filter { it.objectiveId in listOf("obj-u17-age", "obj-u17-weekdays", "obj-u17-months") }
        assertEquals(9, items.size)
        for (i in items) {
            assertTrue(i.feedback.contains(i.answer))
            assertFalse(i.feedback.contains("||"))
            assertFalse(i.feedback.contains("frozen source"))
            assertTrue(i.assessment!!.whyAnswerDemonstratesObjective.endsWith(i.feedback))
            assertEquals(i.distractorRationales, i.assessment!!.distractorDifference)
            i.options.forEachIndexed { n, option -> if (option != i.answer) assertTrue(i.distractorRationales[n].contains(option)) }
        }
        assertTrue(p.item("u17-i-age-2")!!.feedback.contains("does not by itself"))
    }
    @Test fun newCoverageIsPendingAndSourceRelationshipsStillValidate() {
        val p = pack()
        val records = p.provenanceRecords.filter { it.itemId.startsWith("prov-ca-u17-calendar-") }
        assertEquals(203, records.size)
        records.forEach { assertContentAuditResearch(it) }
        val v = V2Validator.validate(p)
        assertTrue(v.errors.joinToString("\n"), v.ok)
    }
    @Test fun clockAndDateTargetsHaveCompletePriorInstruction() {
        val p = pack()
        val counts = mapOf("clock" to 2, "clock-answers" to 3, "quarter-hour" to 5, "clock-productive" to 3, "calendar-date" to 2)
        for ((slug, count) in counts) {
            val session = p.unit(17)!!.sessions.first { it.id == "u17-s-$slug" }
            val teaches = session.steps.takeWhile { it.kind != "GUIDED" }.filter { it.kind == "TEACH" }
            assertEquals(count, teaches.size)
            val body = teaches.joinToString("\n") { it.body }
            p.items.filter { it.objectiveId == "obj-u17-$slug" }.forEach { item ->
                val russianTarget = item.promptLetter!!.takeIf { text -> text.any { it.code in 0x0400..0x04ff } } ?: item.answer
                assertTrue("${item.id} missing target", body.contains(russianTarget))
                assertTrue(item.feedback.contains(item.answer))
                assertTrue(item.assessment!!.whyAnswerDemonstratesObjective.endsWith(item.feedback))
            }
            val rem = session.steps.first { it.kind == "REMEDIATE" }
            assertEquals(teaches.joinToString("\n\n") { it.body }, rem.body)
            assertEquals(teaches.flatMap { it.exampleIds }.distinct(), rem.exampleIds)
        }
        val quarter = p.unit(17)!!.sessions.first { it.id == "u17-s-quarter-hour" }.steps.joinToString("\n") { it.body }
        assertTrue(quarter.contains("do not themselves distinguish evening from morning"))
        val date = p.unit(17)!!.sessions.first { it.id == "u17-s-calendar-date" }.steps.joinToString("\n") { it.body }
        assertTrue(date.contains("neuter ordinal"))
        assertTrue(date.contains("15 May 1996"))
        val production = p.unit(17)!!.sessions.first { it.id == "u17-s-clock-productive" }.steps.joinToString("\n") { it.body }
        assertTrue(production.contains("do not demonstrate independently producing"))
    }

    private fun pack(): V2CurriculumPack {
        val file = System.getProperty("unit17.calendar.baseline")?.let(::File)
            ?: File(if (File("app").isDirectory) "." else "..", "app/src/main/assets/content/v2-curriculum.json")
        return V2PackLoader.parse(file.readText())
    }
}
