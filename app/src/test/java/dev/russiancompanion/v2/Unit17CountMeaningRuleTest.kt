package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.*
import org.junit.Assert.*
import org.junit.Test
import java.io.File

/**
 * T17b (owner decision D1, approved rule change): the six Unit 17 count questions (and their guided counterparts) check the
 * counting meaning, not the "two-hour row" table layout. A prompt that names its count passes; layout wording is no longer
 * what the rule asks for; a wrong count or a missing count context is still rejected.
 */
class Unit17CountMeaningRuleTest {
    private val pack by lazy { V2PackLoader.parse(File(if (File("app").isDirectory) "." else "..", "app/src/main/assets/content/v2-curriculum.json").readText()) }

    private fun targetErrors(id: String, prompt: String, why: String? = null): List<String> {
        val item = pack.item(id)!!
        val changed = item.copy(prompt = prompt, feedback = why ?: item.feedback,
            assessment = item.assessment!!.copy(whyAnswerDemonstratesObjective = why ?: item.assessment!!.whyAnswerDemonstratesObjective))
        val changedPack = pack.copy(items = pack.items.map { if (it.id == id) changed else it })
        // The item-level structure errors feed V2Validator's feature-valid count for the objective.
        val errors = V2Validator.structureValid(changedPack, changed)
        assertEquals(errors.isEmpty(), V2Validator.featureValid(changedPack, changed))
        return errors.filter { "explicit target" in it || "competing count" in it || "rationale is not tied" in it }
    }

    @Test fun aMeaningPromptPassesAndLayoutWordingIsNoLongerRequired() {
        val why = "This phrase means two hours."
        assertEquals(emptyList<String>(), targetErrors("u17-i-hours-1", "Which phrase means two hours?", why))
        // The table-layout wording alone no longer names the count the rule checks.
        assertTrue(targetErrors("u17-i-hours-1", "Which admitted line is the two-hour row?", why).any { "no explicit target" in it })
        for ((id, count) in listOf("u17-i-years-2" to "five years", "u17-i-minutes-1" to "two minutes")) {
            assertEquals(id, emptyList<String>(), targetErrors(id, "Which phrase means $count?", "This phrase means $count."))
        }
    }

    @Test fun aWrongCountOrAMissingCountContextIsStillRejected() {
        // Wrong count: the two-hour question asks for five hours.
        val wrong = targetErrors("u17-i-hours-1", "Which phrase means five hours?", "This phrase means five hours.")
        assertTrue(wrong.toString(), wrong.any { "no explicit target" in it })
        assertTrue(wrong.toString(), wrong.any { "competing count" in it })
        assertTrue(wrong.toString(), wrong.any { "rationale is not tied" in it })
        // A prompt that names its own count and a competing one is rejected too.
        assertTrue(targetErrors("u17-i-minutes-1", "Which phrase means two minutes rather than five minutes?", "This phrase means two minutes.")
            .any { "competing count" in it })
        // Missing count context in the prompt or in the rationale.
        assertTrue(targetErrors("u17-i-years-1", "Which phrase is correct?", "This phrase means two years.").any { "no explicit target" in it })
        assertTrue(targetErrors("u17-i-years-1", "Which phrase means two years?", "This phrase is correct.").any { "rationale is not tied" in it })
    }
}
