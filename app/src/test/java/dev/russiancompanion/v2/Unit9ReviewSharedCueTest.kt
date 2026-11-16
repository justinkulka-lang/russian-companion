package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.*
import org.junit.Assert.*
import org.junit.Test
import java.io.File

/** T15d (owner direction 11/16/2026): no two items that can share one Unit 9 cumulative-review assignment show the same
 * cue, so neither can give away the other's answer; Unit 9's own checks do not repeat one example either. */
class Unit9ReviewSharedCueTest {
    private val pack by lazy {
        V2PackLoader.parse(File(listOf(File("."), File("..")).first { File(it, "app/src/main").isDirectory },
            "app/src/main/assets/content/v2-curriculum.json").readText())
    }

    /** Lower case without stress marks, so a stressed and an unstressed spelling of one word compare equal. */
    private fun cue(item: V2CheckItem) = item.promptLetter.orEmpty().replace("́", "").trim().lowercase()

    /** Every candidate any Unit 9 assignment can draw: the three anchor pools and every A2-28 supplement pool. */
    private fun candidates(policy: CumulativeReviewPolicy) = policy.pool.flatMap { it.candidateItemIds }.map { pack.item(it)!! }

    private fun sharedCues(items: List<V2CheckItem>) = items.groupBy(::cue).filterValues { it.size > 1 }.mapValues { (_, v) -> v.map { it.id } }

    @Test fun noTwoItemsThatCanShareAUnitNineReviewAssignmentShowTheSameCue() {
        val policy = cumulativeReviewDeclaredPolicy(pack, 9)!!
        assertEquals("unit9-prior-noun-behavior-plural-gender-v4", policy.id)
        assertEquals(3, policy.anchorCount)
        assertEquals(listOf("u8-i-plural-3"), policy.pool.single { it.objectiveId == "obj-u8-plural" }.candidateItemIds)
        assertEquals("u8-st-rem-plural", policy.pool.single { it.objectiveId == "obj-u8-plural" }.remediationStepId)
        // The noun-behavior pool is kept whole, and the replaced anchor stays a Unit 8 check.
        assertEquals(Unit6CumulativeReviewPolicy.definition.pool[1], policy.pool[0])
        assertTrue("u8-i-plural-2" in pack.unit(8)!!.objectives.single { it.id == "obj-u8-plural" }.checkItemIds)
        val items = candidates(policy)
        assertTrue(items.all { it.promptLetter.orEmpty().isNotBlank() })
        assertEquals(emptyMap<String, List<String>>(), sharedCues(items))
    }

    @Test fun theFormerPluralAnchorWouldShareItsCueWithTheNounBehaviorPool() {
        // Regression: the v3 anchor u8-i-plural-2 shows the same window noun as a noun-behavior candidate.
        val policy = cumulativeReviewDeclaredPolicy(pack, 9)!!
        val former = policy.pool.map { if (it.objectiveId == "obj-u8-plural") it.copy(candidateItemIds = listOf("u8-i-plural-2")) else it }
        val shared = sharedCues(former.flatMap { it.candidateItemIds }.map { pack.item(it)!! })
        assertEquals(1, shared.size)
        assertTrue("u8-i-plural-2" in shared.values.single())
    }

    @Test fun actualUnitNineAssignmentsFromEveryMissedCandidateHaveDistinctCues() {
        val policy = cumulativeReviewDeclaredPolicy(pack, 9)!!
        // Any two items of one assignment come from the candidate set, so every pair is checked here.
        val items = candidates(policy)
        for (i in items.indices) for (j in i + 1 until items.size) assertNotEquals("${items[i].id} ${items[j].id}", cue(items[i]), cue(items[j]))
    }

    @Test fun unitNineChecksDoNotRepeatOneExampleWithTheSameTask() {
        val checks = pack.unit(9)!!.objectives.flatMap { it.checkItemIds }.map { pack.item(it)!! }
        val repeated = checks.groupBy { listOf(it.prompt, cue(it), it.answer) }.filterValues { it.size > 1 }
        assertEquals(emptyMap<List<String>, List<V2CheckItem>>(), repeated)
        assertNotEquals(cue(pack.item("u9-i-quantity-4")!!), cue(pack.item("u9-i-pattern-3")!!))
        assertEquals("пять лет", pack.item("u9-i-pattern-3")!!.promptLetter)
    }
}
