package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.V2CheckItem
import dev.russiancompanion.v2.curriculum.V2CurriculumPack
import dev.russiancompanion.v2.curriculum.V2LearnerView
import dev.russiancompanion.v2.curriculum.V2PackLoader
import dev.russiancompanion.v2.curriculum.V2ProvenanceRecord
import dev.russiancompanion.v2.curriculum.V2StartingPoint
import dev.russiancompanion.v2.curriculum.V2Validator
import dev.russiancompanion.v2.curriculum.advancementDecision
import dev.russiancompanion.v2.curriculum.unit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.security.MessageDigest

class Units1115InstructionalValidityTest {
    @Test
    fun everyRecognitionObjectiveHasAnIndependentFeatureValidCheck() {
        val pack = pack()
        val validation = V2Validator.validate(pack)
        assertTrue(validation.errors.joinToString("\n"), validation.ok)
        val englishCue = Regex("(?i)which admitted\\b.{0,80}\\brecorded for\\b")
        (11..15).forEach { number ->
            val unit = pack.unit(number)!!
            assertEquals("CONTENT_COMPLETE_REVIEW_PENDING", unit.readiness)
            unit.objectives.filter { it.kind == "RECOGNITION" }.forEach { objective ->
                val checks = objective.checkItemIds.map { id -> pack.items.first { it.id == id } }
                val qualifying = checks.filter { V2Validator.featureValid(pack, it) }
                assertTrue("${objective.id} has no independent feature-valid check", qualifying.isNotEmpty())
                qualifying.forEach { item ->
                    assertTrue(item.assessment!!.independent)
                    assertFalse("${item.id} is an English-cue translation match", V2Validator.isDirectEnglishMatch(item))
                    assertFalse("${item.id} keeps a recorded-for prompt", englishCue.containsMatchIn(item.prompt))
                    assertTrue(item.assessment.whyAnswerDemonstratesObjective.isNotBlank())
                }
            }
        }
        assertEquals("NOT_PREPARED", pack.unit(7)!!.readiness)
        assertTrue(pack.units.drop(15).all { it.readiness == "NOT_PREPARED" && it.sessions.isEmpty() })
    }

    @Test
    fun changingOnlySourceIdWhileRetainingSourceRecordIdFails() {
        val pack = pack()
        val seed = pack.items.first { it.id == "u11-i-past-read-1" }
        assertEquals("SAME_LEMMA_DIFFERENT_FIELD", seed.assessment!!.relationKind)
        val broken = rewriteRationale(seed)
        val cueId = broken.assessment!!.relationParticipantIds[0]
        val mutated = retarget(pack, cueId) { it.copy(sourceId = "src-wikibooks-russian-cc-by-sa") }
        assertEquals(pack.provenanceRecords.first { it.itemId == cueId }.sourceRecordId, mutated.provenanceRecords.first { it.itemId == cueId }.sourceRecordId)
        assertFalse(V2Validator.featureValid(mutated, broken))
    }

    @Test
    fun changingSourceRevisionWhileRetainingSourceRecordIdFails() {
        val pack = pack()
        val seed = pack.items.first { it.id == "u12-i-future-imp-1" }
        assertEquals("SOURCE_SUPPLIED_PAIR", seed.assessment!!.relationKind)
        val broken = rewriteRationale(seed)
        val answerId = broken.assessment!!.relationParticipantIds[1]
        val mutated = retarget(pack, answerId) { it.copy(sourceRevision = "not-the-frozen-oldid") }
        assertEquals(pack.provenanceRecords.first { it.itemId == answerId }.sourceRecordId, mutated.provenanceRecords.first { it.itemId == answerId }.sourceRecordId)
        assertFalse(V2Validator.featureValid(mutated, broken))
    }

    @Test
    fun changingOriginalSourceTextAndHashWhileRetainingSourceRecordIdFails() {
        val pack = pack()
        val seed = pack.items.first { it.id == "u15-i-aspect-consolidate-1" }
        assertEquals("RULE_CLASSIFIES_SOURCE_EXAMPLE", seed.assessment!!.relationKind)
        val broken = rewriteRationale(seed)
        val cueId = broken.assessment!!.relationParticipantIds[0]
        val replacement = "different frozen content that keeps the truncated id"
        val mutated = retarget(pack, cueId) {
            it.copy(originalSourceText = replacement, sourceHash = sha256(replacement))
        }
        val cue = mutated.provenanceRecords.first { it.itemId == cueId }
        assertEquals(pack.provenanceRecords.first { it.itemId == cueId }.sourceRecordId, cue.sourceRecordId)
        assertEquals(sha256(cue.originalSourceText), cue.sourceHash)
        assertFalse(V2Validator.featureValid(mutated, broken))
    }

    @Test
    fun copiedSourceRecordIdWithDifferentFrozenContentCannotShareASpan() {
        val pack = pack()
        val seed = pack.items.first { it.id == "u14-i-impersonal-1" }
        assertEquals("SOURCE_SUPPLIED_PAIR", seed.assessment!!.relationKind)
        val replacement = "a different frozen span that keeps the truncated record id"
        val answerId = seed.assessment!!.relationParticipantIds[1]
        val mutated = retarget(pack, answerId) {
            it.copy(originalSourceText = replacement, sourceHash = sha256(replacement))
        }
        val answer = mutated.provenanceRecords.first { it.itemId == answerId }
        val cue = mutated.provenanceRecords.first { it.itemId == seed.assessment.relationParticipantIds[0] }
        assertEquals(cue.sourceRecordId, answer.sourceRecordId)
        assertNotEquals(cue.originalSourceText, answer.originalSourceText)
        assertFalse(V2Validator.featureValid(mutated, rewriteRationale(seed)))
    }

    @Test
    fun anUnrelatedAdmittedDistractorFails() {
        val pack = pack()
        val seed = pack.items.first { it.id == "u12-i-dative-1" }
        val unrelated = pack.provenanceRecords.first {
            it.unitNumber == 11 && it.shippedText != seed.answer && it.shippedText !in seed.options
        }
        val assessment = seed.assessment!!
        val options = seed.options.map { if (it == seed.answer) it else unrelated.shippedText }
        val swapped = seed.copy(
            options = options,
            distractorRationales = seed.distractorRationales.map { if (it.isEmpty()) it else "This is an unrelated admitted example." },
            assessment = assessment.copy(
                contrastMembers = options,
                distractorSourceIds = assessment.distractorSourceIds.mapIndexed { index, id ->
                    if (options[index] == seed.answer) id else unrelated.itemId
                },
                distractorDifference = assessment.distractorDifference.mapIndexed { index, difference ->
                    if (options[index] == seed.answer) difference else "This is an unrelated admitted example."
                },
            ),
        )
        assertTrue(unrelated.shippedText in swapped.options)
        assertFalse(V2Validator.featureValid(replace(pack, swapped), swapped))
        val errors = V2Validator.validate(replace(pack, swapped)).errors
        assertTrue(
            errors.joinToString("\n"),
            errors.any { it.contains(swapped.id) && it.contains("distractor is unrelated") },
        )
    }

    @Test
    fun aShownLineContainingItsAnswerFailsAsQualifyingEvidence() {
        val pack = pack()
        val seed = pack.items.first { it.id == "u11-i-past-read-2" }
        val assessment = seed.assessment!!
        val cueId = assessment.relationParticipantIds[0]
        val shown = "Он чита́л ${seed.answer}"
        val mutated = retarget(pack, cueId) { it.copy(shippedText = shown) }
        val bad = seed.copy(
            promptLetter = shown,
            assessment = assessment.copy(
                whyAnswerDemonstratesObjective = "The contrast is the same word in the shown line.",
                distractorDifference = assessment.distractorDifference.map { difference ->
                    if (difference.isEmpty()) difference else "The contrast is the same word."
                },
            ),
        )
        assertEquals("answer-in-cue", V2Validator.qualifyingOverlap(shown, seed.answer))
        assertFalse(V2Validator.featureValid(mutated, bad))
    }

    @Test
    fun retainingADirectEnglishCueWithTheSameAnswerFails() {
        val pack = pack()
        val seed = pack.items.first { it.id == "u15-i-imperative-1" }
        val retained = seed.copy(
            prompt = "Which admitted form is recorded for Read?",
            promptLetter = "Read",
        )
        assertEquals(seed.answer, retained.answer)
        assertTrue(V2Validator.isDirectEnglishMatch(retained))
        assertFalse(V2Validator.featureValid(replace(pack, retained), retained))
    }

    @Test
    fun changingProseRationaleCannotValidateAnInvalidContrast() {
        val pack = pack()
        val seed = pack.items.first { it.id == "u11-i-reflexive-2" }
        val valid = rewriteRationale(seed)
        assertTrue(V2Validator.featureValid(replace(pack, valid), valid))
        val invalid = valid.copy(
            assessment = valid.assessment!!.copy(
                relationParticipantIds = listOf("missing-provenance-id") + valid.assessment.relationParticipantIds.drop(1),
            ),
        )
        assertFalse(V2Validator.featureValid(replace(pack, invalid), invalid))
    }

    @Test
    fun unrelatedRemediationCannotSatisfyARecognitionObjective() {
        val pack = pack()
        val objective = pack.unit(13)!!.objectives.first { it.kind == "RECOGNITION" }
        val stripped = pack.copy(
            units = pack.units.map { unit ->
                if (unit.number != 13) {
                    unit
                } else {
                    unit.copy(
                        sessions = unit.sessions.map { session ->
                            session.copy(
                                steps = session.steps.map { step ->
                                    if (step.id in objective.remediationStepIds) step.copy(body = "Return to the explanation.") else step
                                },
                            )
                        },
                    )
                }
            },
        )
        val errors = V2Validator.validate(stripped).errors
        assertTrue(
            errors.joinToString("\n"),
            errors.any { it.contains(objective.id) && it.contains("remediation") },
        )
    }

    @Test
    fun aSkippedRecognitionObjectiveDoesNotAdvance() {
        val pack = pack()
        (11..15).forEach { number ->
            val unit = pack.unit(number)!!
            val attested = advancementDecision(pack, unit, learner(number, book = true, correct = emptySet(), steps = emptySet()))
            assertFalse(attested.allowed)
            val recognition = unit.objectives.filter { it.kind == "RECOGNITION" }
            val skipped = recognition.first()
            val rest = recognition.drop(1).flatMap { it.checkItemIds }.toSet()
            val partial = advancementDecision(
                pack,
                unit,
                learner(number, book = true, correct = rest, steps = unit.sessions.flatMap { it.steps }.map { it.id }.toSet()),
            )
            assertFalse(partial.allowed)
            assertTrue(partial.remaining.joinToString("\n"), partial.remaining.any { it.contains(skipped.title) || it.contains("Demonstrate") })
        }
    }

    @Test
    fun alteringUnitElevenDoesNotMoveEarlierUnitDigests() {
        val pack = pack()
        val before = (1..10).associateWith { V2Validator.unitContentReviewDigest(pack, it) }
        val shifted = pack.copy(
            units = pack.units.map { unit ->
                if (unit.number != 12) unit else unit.copy(topicSummary = unit.topicSummary + " extra")
            },
        )
        before.forEach { (number, digest) ->
            assertEquals(digest, V2Validator.unitContentReviewDigest(shifted, number))
        }
        assertNotEquals(V2Validator.unitContentReviewDigest(pack, 12), V2Validator.unitContentReviewDigest(shifted, 12))
        assertEquals(V2Validator.unitContentReviewDigest(pack, 11), V2Validator.unitContentReviewDigest(shifted, 11))
    }

    private fun replace(pack: V2CurriculumPack, item: V2CheckItem) =
        pack.copy(items = pack.items.map { if (it.id == item.id) item else it })

    private fun rewriteRationale(item: V2CheckItem): V2CheckItem {
        val assessment = item.assessment!!
        return item.copy(
            distractorRationales = item.distractorRationales.map { if (it.isEmpty()) it else "The contrast is the same word." },
            assessment = assessment.copy(
                whyAnswerDemonstratesObjective = "The contrast is the same word in the shown line.",
                distractorDifference = assessment.distractorDifference.map { difference ->
                    if (difference.isEmpty()) difference else "The contrast is the same word."
                },
            ),
        )
    }

    private fun retarget(
        pack: V2CurriculumPack,
        itemId: String,
        edit: (V2ProvenanceRecord) -> V2ProvenanceRecord,
    ) = pack.copy(provenanceRecords = pack.provenanceRecords.map { if (it.itemId == itemId) edit(it) else it })

    private fun sha256(text: String): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(text.toByteArray(Charsets.UTF_8))
        return digest.joinToString("") { byte -> "%02x".format(byte) }
    }

    private fun learner(number: Int, book: Boolean, correct: Set<String>, steps: Set<String>) = V2LearnerView(
        onboardingCompleted = true,
        currentUnitNumber = number,
        startingPoint = V2StartingPoint.NEW_TO_CYRILLIC,
        hasPenguin = true,
        hasAnki = false,
        hasBinder = false,
        hasTutor = false,
        dailyMinutes = 25,
        transliterationEnabled = true,
        currentSessionId = null,
        currentStepIndex = 0,
        completedStepIds = steps,
        correctItemIds = correct,
        bookAttestedUnits = if (book) setOf(number) else emptySet(),
        completedUnitNumbers = emptySet(),
    )

    private fun pack() = V2PackLoader.parse(File(repo(), "app/src/main/assets/content/v2-curriculum.json").readText())

    private fun repo(): File = listOf(File("."), File("..")).first { File(it, "app/src/main").isDirectory }
}
