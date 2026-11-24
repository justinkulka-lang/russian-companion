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

    /**
     * T16c-n1 (owner decision D38a, approved rule change): the quantity is matched as a whole expression and any other quantity
     * in the prompt is competing, whatever the number. Codex's probe: "twenty-two hours" against the two-hour key.
     */
    @Test fun aLongerOrUnlistedQuantityIsRejected() {
        // Twenty-two vs two: no longer read as the two-hour target, and named as a competing count.
        val twentyTwo = targetErrors("u17-i-hours-1", "Which phrase means twenty-two hours?", "This phrase means two hours.")
        assertTrue(twentyTwo.toString(), twentyTwo.any { "no explicit target" in it })
        assertTrue(twentyTwo.toString(), twentyTwo.any { "competing count" in it })
        // The probe no longer counts as a feature-valid check of its objective (it did before D38a); the shipped item still does.
        // (Unit 17 objectives take item structure through the feature-valid count, not as per-item validate() errors.)
        val item = pack.item("u17-i-hours-1")!!
        val probe = item.copy(prompt = "Which phrase means twenty-two hours?")
        assertTrue(V2Validator.featureValid(pack, item))
        assertFalse(V2Validator.featureValid(pack.copy(items = pack.items.map { if (it.id == item.id) probe else it }), probe))
        // A competing four, outside the one/two/five set.
        assertTrue(targetErrors("u17-i-hours-1", "Which phrase means two hours rather than four hours?", "This phrase means two hours.")
            .any { "competing count" in it })
        // An unlisted compound count.
        val compound = targetErrors("u17-i-years-2", "Which phrase means one hundred and five years?", "This phrase means five years.")
        assertTrue(compound.toString(), compound.any { "no explicit target" in it })
        assertTrue(compound.toString(), compound.any { "competing count" in it })
        // A rationale that names only a longer numeral is not tied to the target.
        assertTrue(targetErrors("u17-i-hours-1", "Which phrase means two hours?", "This phrase means twenty-two hours.")
            .any { "rationale is not tied" in it })
        // The existing valid case still passes.
        assertEquals(emptyList<String>(), targetErrors("u17-i-hours-1", "Which phrase means two hours?", "This phrase means two hours."))
        assertEquals(listOf("twenty-two hours"), V2Validator.quantityExpressions("Which phrase means twenty-two hours?"))
    }

    /**
     * T16c-n2 (owner decision D41a, approved rule change): spaces, dashes, hyphens and line breaks are normalized to ASCII before
     * the quantity is read, any number word (scale words included), digit or joined token makes a different quantity, and every
     * count item's expected phrase is pinned. Codex's five T16c-n1-check probes all fail and no longer count as feature-valid
     * checks; the shipped prompts pass.
     */
    @Test fun codexQuantityProbesFailAndEveryCountItemIsPinned() {
        val item = pack.item("u17-i-hours-1")!!
        val why = "This phrase means two hours."
        for (prompt in listOf("Which phrase means one billion two hours?", "Which phrase means twenty\u2013two hours?",
                "Which phrase means twenty\u2011two hours?", "Which phrase means twenty\u00a0two hours?", "Which phrase means twenty\ntwo hours?",
                "Which phrase means 22 hours?", "Which phrase means x-two hours?", "Which phrase means two hours or 3 hours?",
                "Which phrase means one trillion and two hours?")) {
            val errors = targetErrors("u17-i-hours-1", prompt, why)
            assertTrue("$prompt: $errors", errors.any { "no explicit target" in it || "competing count" in it })
            val probe = item.copy(prompt = prompt)
            assertFalse(prompt, V2Validator.featureValid(pack.copy(items = pack.items.map { if (it.id == item.id) probe else it }), probe))
        }
        assertEquals("twenty-two hours", V2Validator.normalizeQuantityText("twenty\u2011two\u00a0hours"))
        // Every count item is pinned to its exact phrase, and every shipped count prompt names exactly that phrase.
        val countItems = pack.items.filter { it.assessment?.testedFeature?.matches(Regex("(ONE|TWO|FIVE)_(HOUR|YEAR|MINUTE)_ROW")) == true }
        assertEquals(9, countItems.size)
        assertEquals(countItems.map { it.id }.toSet(), V2Validator.COUNT_ITEM_PHRASES.keys)
        countItems.forEach { count ->
            val phrase = V2Validator.COUNT_ITEM_PHRASES.getValue(count.id)
            assertTrue(count.id, V2Validator.namesExactQuantity(count.prompt, phrase))
            assertFalse(count.id, V2Validator.namesCompetingQuantity(count.prompt, phrase))
            assertEquals(count.id, emptyList<String>(), V2Validator.structureValid(pack, count))
        }
    }
}
