package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.FILLED_GUIDED_PRACTICE_ROUTES
import dev.russiancompanion.v2.curriculum.V2LearnerView
import dev.russiancompanion.v2.curriculum.V2PackLoader
import dev.russiancompanion.v2.curriculum.V2StartingPoint
import dev.russiancompanion.v2.curriculum.V2Validator
import dev.russiancompanion.v2.curriculum.advancementDecision
import dev.russiancompanion.v2.curriculum.item
import dev.russiancompanion.v2.curriculum.requiredInstructionSteps
import dev.russiancompanion.v2.curriculum.step
import dev.russiancompanion.v2.curriculum.unit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class ContentAuditCorrectionTest {
    @Test
    fun packValidatesAndRejectsTheAuditedFalseKeys() {
        val pack = pack()
        val result = V2Validator.validate(pack)
        assertTrue(result.errors.joinToString("\n"), result.ok)
        assertNotEquals("не́кто", pack.item("u24-i-stand-ins-2")!!.answer)
        assertEquals("there is nobody to ask", pack.item("u24-i-stand-ins-2")!!.answer)
        assertEquals("учи́теля", pack.item("u6-i-animacy-3")!!.answer)
        assertEquals("на́шу сестру́", pack.item("u6-i-possessive-2")!!.answer)
        assertEquals("но́вую", pack.item("u7-i-objectadj-3")!!.answer)
        assertFalse(pack.item("u27-i-comp-2")!!.answer.contains("she had read"))
        assertEquals("to become", pack.item("u16-g-complements-0")!!.answer)
        assertFalse(pack.items.any { it.answer == "usually present" || it.answer == "can only be past" })
        assertEquals("The boy stepped back from the stranger.", pack.item("u20-i-motion-4")!!.answer)
    }

    @Test
    fun gatingChangeInvalidatesUnitAndPackIdentitiesOnly() {
        val pack = pack()
        assertEquals(3, V2Validator.CONTENT_REVIEW_DIGEST_SCHEMA)
        val obsolete = "ff2d4cd848036f7b164f9a68d24ef3d26d3442f4673c2bf1cf560e8d1b8662f1"
        assertNotEquals(obsolete, V2Validator.contentReviewDigest(pack))
        val flipped = pack.copy(
            units = pack.units.map { unit ->
                if (unit.number != 27) {
                    unit
                } else {
                    unit.copy(
                        objectives = unit.objectives.map { objective ->
                            if (objective.id == "obj-u27-stress") objective.copy(gating = !objective.gating) else objective
                        },
                    )
                }
            },
        )
        assertNotEquals(V2Validator.contentReviewDigest(pack), V2Validator.contentReviewDigest(flipped))
        assertNotEquals(V2Validator.unitContentReviewDigest(pack, 27), V2Validator.unitContentReviewDigest(flipped, 27))
        assertEquals(V2Validator.unitContentReviewDigest(pack, 1), V2Validator.unitContentReviewDigest(flipped, 1))
        val approvedOnObsolete = pack.copy(
            linguisticReview = pack.linguisticReview.copy(
                status = "OWNER_ACCEPTED",
                reviewedContentDigest = obsolete,
            ),
        )
        assertFalse(V2Validator.linguisticReleaseAccepted(approvedOnObsolete, repo()))
    }

    @Test
    fun unit27AdvancesWithoutOptionalPresentPassive() {
        val pack = pack()
        val unit = pack.unit(27)!!
        assertFalse(unit.objectives.first { it.id == "obj-u27-present-passive" }.gating)
        assertTrue(unit.objectives.first { it.id == "obj-u27-stress" }.gating)
        assertFalse(pack.item("u27-i-stress-2")!!.promptLetter!!.contains("present passive"))
        assertFalse(pack.item("u27-i-stress-3")!!.promptLetter!!.contains("present passive"))
        assertEquals("obj-u27-present-passive", pack.item("u27-i-stress-1")!!.objectiveId)
        assertEquals("obj-u27-present-passive", pack.item("u27-g-stress-1")!!.objectiveId)
        val requiredSteps = requiredInstructionSteps(unit).map { it.id }.toSet()
        assertFalse("u27-st-teach-present-passive-1" in requiredSteps)
        assertFalse("u27-st-teach-present-passive-2" in requiredSteps)
        assertTrue("u27-st-teach-passive-2" in requiredSteps)
        val optionalObjectiveIds = unit.objectives.filter { !it.gating }.map { it.id }.toSet()
        val requiredItems = pack.items.filter { it.objectiveId !in optionalObjectiveIds && it.objectiveId.startsWith("obj-u27-") }.map { it.id }.toSet()
        val learner = V2LearnerView(
            onboardingCompleted = true,
            currentUnitNumber = 27,
            startingPoint = V2StartingPoint.NEW_TO_CYRILLIC,
            hasPenguin = true,
            hasAnki = false,
            hasBinder = false,
            hasTutor = false,
            dailyMinutes = 25,
            transliterationEnabled = false,
            currentSessionId = unit.sessions.last().id,
            currentStepIndex = 0,
            completedStepIds = requiredSteps,
            correctItemIds = requiredItems,
            bookAttestedUnits = setOf(27),
            completedUnitNumbers = emptySet(),
        )
        val decision = advancementDecision(pack, unit, learner)
        assertTrue(decision.remaining.joinToString("\n"), decision.allowed)
        FILLED_GUIDED_PRACTICE_ROUTES.forEach { (_, guidedId) ->
            val guided = pack.step(guidedId)!!.second
            assertEquals("GUIDED", guided.kind)
            assertTrue(guided.itemIds.isNotEmpty())
        }
    }

    private fun pack() = V2PackLoader.parse(File(repo(), "app/src/main/assets/content/v2-curriculum.json").readText())

    private fun repo(): File = listOf(File("."), File("..")).first { File(it, "app/src/main").isDirectory }
}
