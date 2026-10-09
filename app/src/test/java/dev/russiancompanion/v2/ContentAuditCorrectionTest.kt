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
import dev.russiancompanion.v2.curriculum.stepCompletionItemIds
import dev.russiancompanion.v2.curriculum.unit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class ContentAuditCorrectionTest {
    @Test
    fun unitElevenChecksPastAgreementBeforeAspectIntroductionWithoutLoweringItsGate() {
        val pack = pack()
        val unit = pack.unit(11)!!
        val earlyTerms = Regex("\\b(aspect|imperfective|perfective)\\b", RegexOption.IGNORE_CASE)
        val hosts = unit.objectives.map { it.title } + unit.sessions.flatMap { session ->
            listOf(session.title) + session.steps.flatMap { listOf(it.title, it.body) }
        } + pack.items.filter { it.objectiveId.startsWith("obj-u11-") }.flatMap {
            listOf(it.prompt, it.feedback) + it.distractorRationales
        }
        assertFalse("Unit 11 still introduces an aspect label", hosts.any { earlyTerms.containsMatchIn(it) })
        assertTrue(pack.items.any { it.objectiveId.startsWith("obj-u12-") && it.assessment?.testedFeature?.contains("PERFECTIVE") == true })
        val checks = listOf("u11-g-past-aspect-0", "u11-i-past-aspect-1", "u11-i-past-aspect-2").map { pack.item(it)!! }
        assertEquals(listOf("NEUTER_PAST", "MASCULINE_PAST", "FEMININE_PAST"), checks.map { it.assessment!!.testedFeature })
        checks.forEach { item ->
            val assessment = item.assessment!!
            assertEquals("SAME_LEMMA_DIFFERENT_FIELD", assessment.relationKind)
            val participants = assessment.relationParticipantIds.map { id -> pack.provenanceRecords.single { it.itemId == id } }
            assertEquals(setOf("verbs.csv:прочита'ть"), participants.map { it.sourceRecordId.substringBefore('#') }.toSet())
            assertEquals(item.answer, pack.provenanceRecords.single { it.itemId == assessment.answerSourceIds.single() }.shippedText)
        }
        val required = unit.objectives.filter { it.gating }.flatMap { it.checkItemIds }.toSet()
        assertEquals(8, required.size)
        assertEquals(8, unit.advancement!!.minimumDistinctChecks)
        val correct = pack.items.filter { it.objectiveId in unit.objectives.filter { objective -> objective.gating }.map { objective -> objective.id } }
            .map { it.id }.toSet()
        val learner = V2LearnerView(
            onboardingCompleted = true, currentUnitNumber = 11, startingPoint = V2StartingPoint.NEW_TO_CYRILLIC,
            hasPenguin = true, hasAnki = false, hasBinder = false, hasTutor = false, dailyMinutes = 25,
            transliterationEnabled = false, currentSessionId = null, currentStepIndex = 0,
            completedStepIds = requiredInstructionSteps(unit).map { it.id }.toSet(), correctItemIds = correct,
            bookAttestedUnits = setOf(11), completedUnitNumbers = emptySet(),
        )
        val decision = advancementDecision(pack, unit, learner)
        assertTrue(decision.remaining.joinToString("\n"), decision.allowed)
        assertFalse(advancementDecision(pack, unit, learner.copy(correctItemIds = correct - "u11-i-past-aspect-1")).allowed)
        assertTrue(V2Validator.validate(pack).errors.joinToString("\n"), V2Validator.validate(pack).ok)
    }

    @Test
    fun wrongAnswerRoutesRetainTheCorrectedObjectiveExplanation() {
        val pack = pack()
        val targets = listOf(
            Triple(3, "gender", 5), Triple(6, "feminine", 2), Triple(7, "spelling", 2),
            Triple(8, "spelling", 2), Triple(11, "past-read", 2), Triple(11, "past-aspect", 2),
            Triple(12, "future-imp", 2), Triple(12, "aspect", 2), Triple(13, "past-aspect", 2),
            Triple(16, "inst-sg", 2), Triple(16, "inst-adj", 2),
            Triple(28, "imperfective-adverb", 2), Triple(28, "perfective-adverb", 2),
            Triple(24, "stand-ins", 3), Triple(24, "word-order-contrast", 2),
            Triple(26, "letter-writing", 2), Triple(21, "purpose", 2), Triple(16, "complements", 1),
        )
        targets.forEach { (number, topic, suffix) ->
            val teach = pack.step("u$number-st-teach-$topic-$suffix")!!.second
            val remediation = pack.step("u$number-st-rem-$topic")!!.second
            val objective = pack.unit(number)!!.objectives.single {
                teach.id in it.instructionStepIds && remediation.id in it.remediationStepIds
            }
            assertEquals(remediation.id, teach.body, remediation.body)
            assertTrue("Missing exact objective-bound coverage for ${remediation.id}", pack.provenanceRecords.any {
                it.unitNumber == number && it.objectiveId == objective.id && it.shippedText == remediation.body
            })
        }
        val future = pack.step("u12-st-rem-future-imp")!!.second.body
        assertTrue(future.contains("быть is imperfective"))
        assertFalse(future.contains("perfective verb"))
        assertTrue(pack.step("u12-st-rem-aspect")!!.second.body.contains("not every verb"))
        listOf(11, 13).forEach { number ->
            assertFalse(pack.step("u$number-st-rem-past-aspect")!!.second.body.contains("wasn't completed"))
        }
        assertTrue(pack.step("u7-st-rem-spelling")!!.second.body.contains("After ж, ц, ч, ш, or щ"))
        assertTrue(pack.step("u8-st-rem-spelling")!!.second.body.contains("seven-letter condition"))
        assertFalse(pack.step("u28-st-rem-perfective-adverb")!!.second.body.contains("can only be past"))
    }

    @Test
    fun auditedCaseContrastsExplainAnimacyAndPreserveFeminineAgreement() {
        val pack = pack()
        val teacher = pack.item("u6-i-animacy-3")!!
        assertEquals("учи́тель", teacher.promptLetter)
        assertEquals("учи́теля", teacher.answer)
        assertTrue(teacher.prompt.contains("singular accusative"))
        assertTrue(teacher.prompt.contains("masculine animate"))
        assertTrue(teacher.feedback.contains("same form as the genitive"))
        assertTrue(teacher.distractorRationales[1].contains("singular nominative"))
        val feminine = pack.item("u7-i-objectadj-3")!!
        assertEquals("но́вая", feminine.promptLetter)
        assertEquals("но́вую", feminine.answer)
        assertTrue(feminine.prompt.contains("feminine singular accusative"))
        assertTrue(feminine.feedback.contains("Keep feminine singular agreement"))
        assertTrue(feminine.distractorRationales[1].contains("masculine singular nominative"))
        val masculine = pack.item("u7-g-objectadj-0")!!
        assertTrue(masculine.prompt.contains("masculine animate"))
        assertTrue(masculine.feedback.contains("accusative singular"))
        assertTrue(masculine.feedback.contains("same form as the masculine singular genitive"))
        val generic = pack.items.filter {
            it.assessment?.relationKind == "SAME_LEMMA_DIFFERENT_FIELD" &&
                it.objectiveId.startsWith("obj-u")
        }
        assertTrue(generic.isNotEmpty())
        assertFalse(generic.any { it.feedback.startsWith("Does the admitted record show") })
    }

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
        unit.sessions.filter { session -> session.objectiveIds.any { it !in optionalObjectiveIds } }
            .flatMap { it.steps }.forEach { step ->
                assertTrue(stepCompletionItemIds(pack, step.id).none { pack.item(it)!!.objectiveId in optionalObjectiveIds })
            }
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
