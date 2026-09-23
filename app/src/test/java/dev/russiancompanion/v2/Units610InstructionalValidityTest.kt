package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.V2CheckItem
import dev.russiancompanion.v2.curriculum.V2CurriculumPack
import dev.russiancompanion.v2.curriculum.V2LearnerView
import dev.russiancompanion.v2.curriculum.V2PackLoader
import dev.russiancompanion.v2.curriculum.V2StartingPoint
import dev.russiancompanion.v2.curriculum.V2Validator
import dev.russiancompanion.v2.curriculum.advancementDecision
import dev.russiancompanion.v2.curriculum.unit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.security.MessageDigest

class Units610InstructionalValidityTest {
    @Test
    fun everyRecognitionObjectiveHasAnIndependentFeatureValidCheck() {
        val pack = pack()
        val validation = V2Validator.validate(pack)
        assertTrue(validation.errors.joinToString("\n"), validation.ok)
        val englishCue = Regex("(?i)which admitted\\b.{0,80}\\brecorded for\\b")
        (6..10).forEach { number ->
            val unit = pack.unit(number)!!
            unit.objectives.filter { it.kind == "RECOGNITION" }.forEach { objective ->
                val checks = objective.checkItemIds.map { id -> pack.items.first { it.id == id } }
                val qualifying = checks.filter { V2Validator.featureValid(pack, it) }
                assertTrue("${objective.id} has no independent feature-valid check", qualifying.isNotEmpty())
                qualifying.forEach { item ->
                    assertTrue(item.assessment!!.independent)
                    assertFalse("${item.id} is an English-cue translation match", V2Validator.isDirectEnglishMatch(item))
                    assertFalse("${item.id} keeps a recorded-for prompt", englishCue.containsMatchIn(item.prompt))
                    assertTrue("${item.id} rationale does not name the distinction", item.assessment.whyAnswerDemonstratesObjective.isNotBlank())
                }
                objective.exampleIds.forEach { id ->
                    val example = pack.examples.first { it.id == id }
                    assertTrue("${example.id} is English prose", example.display.any { it.code in 0x0400..0x04FF })
                }
            }
        }
    }

    @Test
    fun renamingSourceMeaningToFormSelectionDoesNotValidateATranslationMatch() {
        val pack = pack()
        val seed = pack.items.first { it.id == "u6-i-possessive-1" }
        val translation = seed.copy(
            kind = "SOURCE_MEANING",
            prompt = "Which admitted line is recorded for I like dogs?",
            promptLetter = "I like dogs",
            distractorRationales = emptyList(),
            assessment = null,
        )
        val renamed = translation.copy(
            kind = "FORM_SELECTION",
            distractorRationales = seed.distractorRationales,
            assessment = seed.assessment,
        )
        assertEquals(translation.prompt, renamed.prompt)
        assertEquals(translation.options, renamed.options)
        assertEquals(translation.answer, renamed.answer)
        assertTrue(V2Validator.isDirectEnglishMatch(renamed))
        assertFalse(V2Validator.featureValid(replace(pack, renamed), renamed))
        val errors = V2Validator.validate(replace(pack, renamed)).errors
        assertTrue(
            errors.joinToString("\n"),
            errors.any { it.contains(renamed.id) && it.contains("direct English-to-Russian matching") },
        )
    }

    @Test
    fun retainingADirectEnglishCueWithTheSameAnswerFails() {
        val pack = pack()
        val seed = pack.items.first { it.id == "u6-i-possessive-2" }
        val retained = seed.copy(
            prompt = "Which admitted form is recorded for Our?",
            promptLetter = "Our",
        )
        assertEquals(seed.options, retained.options)
        assertEquals(seed.answer, retained.answer)
        assertEquals(seed.kind, retained.kind)
        assertTrue(V2Validator.isDirectEnglishMatch(retained))
        assertFalse(V2Validator.featureValid(replace(pack, retained), retained))
        val errors = V2Validator.validate(replace(pack, retained)).errors
        assertTrue(
            errors.joinToString("\n"),
            errors.any { it.contains("direct English-to-Russian matching") },
        )
    }

    @Test
    fun anUnrelatedAdmittedDistractorFails() {
        val pack = pack()
        val seed = pack.items.first { it.id == "u6-i-possessive-1" }
        val unrelated = pack.provenanceRecords.first {
            it.objectiveId == seed.objectiveId && it.shippedText == "собака"
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
    fun guidedOnlyContrastsDoNotSatisfyARecognitionObjective() {
        val pack = pack()
        val objective = pack.unit(6)!!.objectives.first { it.id == "obj-u6-possessive" }
        val guidedOnly = pack.copy(
            items = pack.items.map { item ->
                if (item.id in objective.checkItemIds) {
                    item.copy(assessment = item.assessment!!.copy(independent = false))
                } else {
                    item
                }
            },
        )
        objective.checkItemIds.forEach { id ->
            val item = guidedOnly.items.first { it.id == id }
            assertFalse(V2Validator.featureValid(guidedOnly, item))
        }
        val errors = V2Validator.validate(guidedOnly).errors
        assertTrue(
            errors.joinToString("\n"),
            errors.any { it.contains(objective.id) && it.contains("no independent feature-valid check") },
        )
    }

    @Test
    fun anUnrelatedDistractorCarryingSameWordStillFails() {
        val pack = pack()
        val seed = pack.items.first { it.id == "u6-i-possessive-1" }
        val unrelated = pack.provenanceRecords.first {
            it.objectiveId == seed.objectiveId && it.shippedText == "собака"
        }
        val assessment = seed.assessment!!
        val options = seed.options.map { if (it == seed.answer) it else unrelated.shippedText }
        val participants = assessment.relationParticipantIds.toMutableList()
        var distractorSlot = 0
        options.forEachIndexed { index, option ->
            if (option != seed.answer) {
                participants[distractorSlot + 2] = unrelated.itemId
                distractorSlot += 1
            }
        }
        val swapped = seed.copy(
            options = options,
            distractorRationales = seed.distractorRationales.map { if (it.isEmpty()) it else "This contrast is the same word." },
            assessment = assessment.copy(
                contrastMembers = options,
                evidenceMode = "SAME_LEMMA_DIFFERENT_FIELD",
                relationKind = "SAME_LEMMA_DIFFERENT_FIELD",
                relationParticipantIds = participants,
                distractorSourceIds = assessment.distractorSourceIds.mapIndexed { index, id ->
                    if (options[index] == seed.answer) id else unrelated.itemId
                },
                distractorDifference = assessment.distractorDifference.mapIndexed { index, difference ->
                    if (options[index] == seed.answer) difference else "This contrast is the same word."
                },
                whyAnswerDemonstratesObjective = "Does the same word still explain this contrast?",
            ),
        )
        assertFalse(V2Validator.featureValid(replace(pack, swapped), swapped))
    }

    @Test
    fun aDifferentLemmaWithTheSameRelationLabelFails() {
        val pack = pack()
        val seed = pack.items.first { it.id == "u6-i-possessive-1" }
        val otherLemma = pack.provenanceRecords.first {
            it.objectiveId == seed.objectiveId && it.sourceRecordId.startsWith("adjectives.csv:твой#")
        }
        val assessment = seed.assessment!!
        val bad = seed.copy(
            options = listOf(seed.answer, otherLemma.shippedText),
            distractorRationales = listOf("", "This contrast is the same word."),
            assessment = assessment.copy(
                evidenceMode = "SAME_LEMMA_DIFFERENT_FIELD",
                relationKind = "SAME_LEMMA_DIFFERENT_FIELD",
                contrastMembers = listOf(seed.answer, otherLemma.shippedText),
                relationParticipantIds = listOf(assessment.relationParticipantIds[0], assessment.relationParticipantIds[1], otherLemma.itemId),
                distractorSourceIds = listOf("", otherLemma.itemId),
                distractorDifference = listOf("", "This contrast is the same word."),
                whyAnswerDemonstratesObjective = "Does the same word still explain this contrast?",
            ),
        )
        assertFalse(V2Validator.featureValid(replace(pack, bad), bad))
    }

    @Test
    fun aShownLineContainingItsAnswerFailsAsQualifyingEvidence() {
        assertEquals("answer-in-cue", V2Validator.qualifyingOverlap("Я хочу его котов", "его́"))
        assertEquals("answer-in-cue", V2Validator.qualifyingOverlap("Я хочу его котов", "его"))
        val pack = pack()
        val seed = pack.items.first { it.id == "u6-g-third-0" }
        val assessment = seed.assessment!!
        val cueId = assessment.relationParticipantIds[0]
        val shown = "Я хочу его котов"
        val mutated = pack.copy(
            provenanceRecords = pack.provenanceRecords.map { record ->
                if (record.itemId == cueId) record.copy(shippedText = shown) else record
            },
        )
        val bad = seed.copy(
            promptLetter = shown,
            assessment = assessment.copy(
                independent = true,
                whyAnswerDemonstratesObjective = "The contrast is the same word in the shown line.",
                distractorDifference = assessment.distractorDifference.map { difference ->
                    if (difference.isEmpty()) difference else "$difference The contrast is the same word."
                },
            ),
        )
        assertFalse(V2Validator.featureValid(mutated, bad))
    }

    @Test
    fun anAnswerSentenceSelectedByCueTokenFails() {
        assertEquals("cue-in-answer", V2Validator.qualifyingOverlap("нет", "Нет рестора́нов"))
        val pack = pack()
        val seed = pack.items.first { it.id == "u8-i-availability-2" }
        val assessment = seed.assessment!!
        val cueId = assessment.relationParticipantIds[0]
        val answerId = assessment.relationParticipantIds[1]
        val shown = "нет"
        val answer = "Нет рестора́нов"
        val mutated = pack.copy(
            provenanceRecords = pack.provenanceRecords.map { record ->
                when (record.itemId) {
                    cueId -> record.copy(shippedText = shown)
                    answerId -> record.copy(shippedText = answer)
                    else -> record
                }
            },
        )
        val bad = seed.copy(
            promptLetter = shown,
            answer = answer,
            options = listOf(answer, seed.options.first { it != seed.answer }),
            assessment = assessment.copy(
                evidenceMode = "SOURCE_SUPPLIED_PAIR",
                relationKind = "SOURCE_SUPPLIED_PAIR",
                contrastMembers = listOf(answer, seed.options.first { it != seed.answer }),
                whyAnswerDemonstratesObjective = "The contrast is the same word in the shown line.",
                distractorDifference = listOf("", "The contrast is the same word."),
            ),
        )
        assertFalse(V2Validator.featureValid(mutated, bad))
    }

    @Test
    fun falsifiedRelationshipIdsFail() {
        val pack = pack()
        val seed = pack.items.first { it.assessment?.independent == true && it.id.startsWith("u6-i-") }
        val foreign = pack.provenanceRecords.first { it.objectiveId.startsWith("obj-u9-") }
        val assessment = seed.assessment!!
        val falsified = seed.copy(
            assessment = assessment.copy(
                relationParticipantIds = listOf(foreign.itemId) + assessment.relationParticipantIds.drop(1),
                whyAnswerDemonstratesObjective = "Does this rewritten rationale create a valid contrast?",
            ),
        )
        assertFalse(V2Validator.featureValid(replace(pack, falsified), falsified))
    }

    @Test
    fun changingProseRationaleCannotValidateAnInvalidContrast() {
        val pack = pack()
        val seed = pack.items.first { it.id == "u6-i-possessive-1" }
        val valid = seed.copy(
            assessment = seed.assessment!!.copy(
                whyAnswerDemonstratesObjective = "Is the governed contrast still the admitted one?",
                distractorDifference = seed.assessment.distractorDifference.map { difference ->
                    if (difference.isEmpty()) difference else "This row is a different admitted row."
                },
            ),
        )
        assertTrue(V2Validator.featureValid(replace(pack, valid), valid))
        val invalid = valid.copy(
            assessment = valid.assessment!!.copy(
                relationParticipantIds = listOf("missing-provenance-id") + valid.assessment.relationParticipantIds.drop(1),
                whyAnswerDemonstratesObjective = "The contrast is the same word in the shown line.",
                distractorDifference = valid.assessment.distractorDifference.map { difference ->
                    if (difference.isEmpty()) difference else "The contrast is the same word."
                },
            ),
        )
        assertFalse(V2Validator.featureValid(replace(pack, invalid), invalid))
    }

    @Test
    fun changingOnlySourceIdWhileRetainingSourceRecordIdFails() {
        val pack = pack()
        val seed = pack.items.first { it.id == "u6-i-third-1" }
        val broken = rewriteRationale(seed)
        val cueId = broken.assessment!!.relationParticipantIds[0]
        val mutated = retarget(pack, cueId) { it.copy(sourceId = "src-openrussian-50e210c4") }
        assertEquals(pack.provenanceRecords.first { it.itemId == cueId }.sourceRecordId, mutated.provenanceRecords.first { it.itemId == cueId }.sourceRecordId)
        assertFalse(V2Validator.featureValid(mutated, broken))
    }

    @Test
    fun changingSourceRevisionWhileRetainingSourceRecordIdFails() {
        val pack = pack()
        val seed = pack.items.first { it.id == "u8-i-availability-1" }
        val broken = rewriteRationale(seed)
        val answerId = broken.assessment!!.relationParticipantIds[1]
        val mutated = retarget(pack, answerId) { it.copy(sourceRevision = "not-the-frozen-oldid") }
        assertEquals(pack.provenanceRecords.first { it.itemId == answerId }.sourceRecordId, mutated.provenanceRecords.first { it.itemId == answerId }.sourceRecordId)
        assertFalse(V2Validator.featureValid(mutated, broken))
    }

    @Test
    fun changingOriginalSourceTextAndHashWhileRetainingSourceRecordIdFails() {
        val pack = pack()
        val seed = pack.items.first { it.id == "u9-i-cardinal-1" }
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
    fun aWikibooksRecordShapedLikeAnOpenRussianLemmaCannotBeTheSameLemma() {
        val pack = pack()
        val seed = pack.items.first { it.id == "u6-i-possessive-3" }
        assertEquals("SAME_LEMMA_DIFFERENT_FIELD", seed.assessment!!.relationKind)
        val answerId = seed.assessment.relationParticipantIds[1]
        val wikibooks = pack.provenanceRecords.first { it.sourceId == "src-wikibooks-russian-cc-by-sa" }
        val mutated = retarget(pack, answerId) {
            it.copy(
                sourceId = wikibooks.sourceId,
                sourceUrl = wikibooks.sourceUrl,
                sourceRevision = wikibooks.sourceRevision,
            )
        }
        val shaped = mutated.provenanceRecords.first { it.itemId == answerId }
        assertTrue(shaped.sourceRecordId.contains(".csv:") && shaped.sourceRecordId.contains("#"))
        assertEquals("src-wikibooks-russian-cc-by-sa", shaped.sourceId)
        assertFalse(V2Validator.featureValid(mutated, rewriteRationale(seed)))
    }

    @Test
    fun sameTruncatedSourceRecordIdWithDifferentFrozenContentCannotShareASpan() {
        val pack = pack()
        val replacement = "a different frozen span that keeps the truncated record id"
        val hash = sha256(replacement)
        listOf("u6-i-third-1" to "SOURCE_SUPPLIED_PAIR", "u8-i-availability-2" to "RULE_CLASSIFIES_SOURCE_EXAMPLE").forEach { (itemId, kind) ->
            val seed = pack.items.first { it.id == itemId }
            assertEquals(kind, seed.assessment!!.relationKind)
            val answerId = seed.assessment.relationParticipantIds[1]
            val mutated = retarget(pack, answerId) {
                it.copy(originalSourceText = replacement, sourceHash = hash)
            }
            val answer = mutated.provenanceRecords.first { it.itemId == answerId }
            val cue = mutated.provenanceRecords.first { it.itemId == seed.assessment.relationParticipantIds[0] }
            assertEquals(cue.sourceRecordId, answer.sourceRecordId)
            assertFalse(cue.originalSourceText == answer.originalSourceText)
            assertFalse(V2Validator.featureValid(mutated, rewriteRationale(seed)))
        }
    }

    @Test
    fun contentCompleteRecognitionObjectivesKeepAnIndependentCheck() {
        val pack = pack()
        (6..10).mapNotNull { pack.unit(it) }
            .filter { it.readiness == "CONTENT_COMPLETE_REVIEW_PENDING" }
            .forEach { unit ->
                unit.objectives.filter { it.kind == "RECOGNITION" }.forEach { objective ->
                    val qualifying = objective.checkItemIds
                        .map { id -> pack.items.first { it.id == id } }
                        .filter { V2Validator.featureValid(pack, it) }
                    assertTrue("${objective.id} has no qualifying independent check", qualifying.isNotEmpty())
                }
            }
        assertEquals("NOT_PREPARED", pack.unit(7)!!.readiness)
    }

    @Test
    fun aRationaleThatDoesNotExplainTheDistinctionFails() {
        val pack = pack()
        val seed = pack.items.first { it.id == "u6-i-possessive-1" }
        val unlabeled = seed.copy(
            assessment = seed.assessment!!.copy(whyAnswerDemonstratesObjective = "The admitted answer is recorded."),
        )
        assertFalse(V2Validator.featureValid(replace(pack, unlabeled), unlabeled))
        val errors = V2Validator.validate(replace(pack, unlabeled)).errors
        assertTrue(
            errors.joinToString("\n"),
            errors.any { it.contains(unlabeled.id) && it.contains("does not explain the governed distinction") },
        )
    }

    @Test
    fun sourceMeaningAloneCannotSatisfyAGrammarObjective() {
        val pack = pack()
        val objective = pack.unit(6)!!.objectives.first { it.kind == "RECOGNITION" }
        val stripped = pack.copy(
            items = pack.items.map { item ->
                if (item.id in objective.checkItemIds) {
                    item.copy(kind = "SOURCE_MEANING", distractorRationales = emptyList(), assessment = null)
                } else {
                    item
                }
            },
        )
        val errors = V2Validator.validate(stripped).errors
        assertTrue(errors.joinToString("\n"), errors.any { it.contains("SOURCE_MEANING alone cannot satisfy") })
    }

    @Test
    fun englishRuleProseCannotBeStoredAsARussianExample() {
        val pack = pack()
        val seed = pack.examples.first { it.objectiveId.startsWith("obj-u9-") }
        val injected = pack.copy(
            examples = pack.examples + seed.copy(id = "ex-u9-english-rule", display = "1 -> nominative", note = "rule"),
        )
        val errors = V2Validator.validate(injected).errors
        assertTrue(errors.joinToString("\n"), errors.any { it.contains("English instructional prose") })
    }

    @Test
    fun bookAttestationAndPartialObjectivesDoNotAdvance() {
        val pack = pack()
        (6..10).forEach { number ->
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
    fun unitNineBoundaryCasesAreIndependentQualifyingChecks() {
        val pack = pack()
        data class Boundary(val feature: String, val shown: String, val answer: String)
        val required = listOf(
            Boundary("FINAL_DIGIT_ONE", "сорок один год", "1 -> nominative"),
            Boundary("COMPOUND_22", "двадцать два часа", "2, 3, 4 -> genitive singular"),
            Boundary("FINAL_DIGIT_FIVE_PLUS", "пять часов", "5 and more -> genitive plural"),
            Boundary("FINAL_ZERO_OR_TEN", "десять копеек", "5 and more -> genitive plural"),
            Boundary("TEENS_OVERRIDE", "двести тринадцать собак", "5 and more -> genitive plural"),
            Boundary("TEEN_VALUE", "шестнадцать рублей", "5 and more -> genitive plural"),
            Boundary("COMPOUND_21", "двадцать один час", "1 -> nominative"),
            Boundary("HUNDREDS_COMPOSITION", "215", "две́сти пятна́дцать"),
            Boundary("THOUSANDS_COMPOSITION", "1,984", "ты́сяча девятьсо́т во́семьдесят четы́ре"),
            Boundary("ODIN_MASCULINE", "мальчик", "один"),
            Boundary("ODIN_FEMININE", "девушка", "одна"),
            Boundary("ODIN_NEUTER", "животное", "одно"),
            Boundary("ODIN_PLURAL", "джинсы", "одни"),
            Boundary("DVA_DVE", "девушки", "две"),
        )
        val unit9 = pack.unit(9)!!
        val independent = unit9.objectives
            .filter { it.kind == "RECOGNITION" }
            .flatMap { it.checkItemIds }
            .map { id -> pack.items.first { item -> item.id == id } }
            .filter { it.assessment?.independent == true }
        required.forEach { boundary ->
            val match = independent.firstOrNull { item ->
                val assessment = item.assessment!!
                assessment.testedFeature == boundary.feature &&
                    item.promptLetter == boundary.shown &&
                    item.answer == boundary.answer
            }
            assertTrue("missing independent ${boundary.feature} for ${boundary.shown}", match != null)
            assertTrue(V2Validator.featureValid(pack, match!!))
            assertFalse(V2Validator.isDirectEnglishMatch(match))
        }
        val pattern = independent.filter { it.objectiveId == "obj-u9-pattern" }
        assertTrue(pattern.any { it.answer.contains("must be declined") })
        val unit7 = pack.unit(7)!!
        assertEquals("NOT_PREPARED", unit7.readiness)
        val lesson = unit7.sessions.flatMap { it.steps }.joinToString("\n") { it.body + (it.bookDirection ?: "") }
        assertTrue(lesson.contains("introductory adverb objective is not prepared"))
        assertFalse(lesson.contains("хорошое"))
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
        edit: (dev.russiancompanion.v2.curriculum.V2ProvenanceRecord) -> dev.russiancompanion.v2.curriculum.V2ProvenanceRecord,
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
