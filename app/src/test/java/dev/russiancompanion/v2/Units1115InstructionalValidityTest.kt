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
    private val lessonBodyDenial = Regex(
        "(?i)do not contain lesson bodies|does not contain (?:a )?lesson bod(?:y|ies)|lack(?:s|ing)? (?:a )?lesson bod(?:y|ies)|no lesson bod(?:y|ies)",
    )

    private fun deniedLessonBodyUnits(sentence: String, maxUnit: Int): Set<Int> {
        if (!lessonBodyDenial.containsMatchIn(sentence)) return emptySet()
        val denied = mutableSetOf<Int>()
        var named = false
        Regex("(?i)units\\s+(\\d+)\\s+and later").findAll(sentence).forEach { match ->
            named = true
            val start = match.groupValues[1].toInt()
            if (start <= maxUnit) denied += (start..maxUnit).toSet()
        }
        Regex("(?i)units\\s+(\\d+)\\s*(?:–|-|through|to)\\s*(\\d+)").findAll(sentence).forEach { match ->
            named = true
            val start = match.groupValues[1].toInt()
            val end = match.groupValues[2].toInt()
            denied += (minOf(start, end)..maxOf(start, end)).toSet()
        }
        Regex("(?i)\\bunit\\s+(\\d+)\\b").findAll(sentence).forEach { match ->
            named = true
            denied += match.groupValues[1].toInt()
        }
        if (!named) denied += (1..maxUnit).toSet()
        return denied
    }

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
        assertTrue(pack.units.drop(25).all { it.readiness == "NOT_PREPARED" && it.installState == "DEVELOPMENT" && it.sessions.isNotEmpty() })
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
        assertEquals("SOURCE_SUPPLIED_PAIR", seed.assessment!!.relationKind)
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
        val answerId = seed.assessment.relationParticipantIds[1]
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

    @Test
    fun learnerFacingReferencesDoNotDenyAPackagedLessonBody() {
        val pack = pack()
        val withLessonBody = pack.units.filter { unit ->
            unit.sessions.any { session -> session.steps.any { it.kind == "TEACH" || it.kind == "CHECK" } }
        }.map { it.number }.toSet()
        assertTrue(withLessonBody.isNotEmpty())
        val maxUnit = pack.units.maxOf { it.number }
        val sample = withLessonBody.min()
        assertEquals(setOf(sample), deniedLessonBodyUnits("Unit $sample does not contain a lesson body.", maxUnit) intersect withLessonBody)
        assertTrue(
            deniedLessonBodyUnits("Units $sample and later do not contain lesson bodies.", maxUnit).containsAll(withLessonBody.filter { it >= sample }),
        )
        pack.references.forEach { reference ->
            val text = "${reference.title}. ${reference.summary}. ${reference.detail}"
            text.split(Regex("(?<=[.!?])\\s+")).forEach { sentence ->
                if (!lessonBodyDenial.containsMatchIn(sentence)) return@forEach
                val overlap = deniedLessonBodyUnits(sentence, maxUnit) intersect withLessonBody
                assertTrue("${reference.id} denies a packaged lesson body in $overlap: $sentence", overlap.isEmpty())
            }
        }
    }

    @Test
    fun learnerFacingReferenceDoesNotClaimUnpreparedUnitsHaveLessonBodies() {
        val pack = pack()
        val unprepared = pack.units.filter { unit ->
            unit.readiness == "NOT_PREPARED" || unit.installState == "NOT_YET_INSTALLED"
        }.map { it.number }.toSet()
        assertTrue(unprepared.contains(7))
        assertTrue(unprepared.contains(17))
        assertFalse(unprepared.contains(19))
        assertFalse(unprepared.contains(21))
        assertFalse(unprepared.contains(22))
        assertTrue(unprepared.contains(23))
        assertFalse(unprepared.contains(24))
        assertTrue(unprepared.contains(25))
        assertEquals("NOT_PREPARED", pack.unit(7)!!.readiness)
        val falseClaim = "Units 19 through 23 contain proposed lesson bodies that still need qualified review."
        assertTrue(claimedPreparedUnits(falseClaim).intersect(unprepared).contains(23))
        assertFalse(claimedPreparedUnits(falseClaim).intersect(unprepared).contains(19))
        val reference = pack.references.first { it.id == "ref-book-topics" }
        assertEquals(
            "Some units contain proposed lesson bodies that still require qualified review.",
            reference.summary,
        )
        assertEquals(
            "A unit's Course status shows whether its lesson body is prepared. Units marked Not prepared or Not yet installed do not contain a usable lesson body. Open the matching lesson in the copy you own. This reference does not approve the packaged records.",
            reference.detail,
        )
        val text = "${reference.title}. ${reference.summary}. ${reference.detail}"
        text.split(Regex("(?<=[.!?])\\s+")).forEach { sentence ->
            val overlap = claimedPreparedUnits(sentence).intersect(unprepared)
            assertTrue("${reference.id} claims an unprepared unit has a lesson body in $overlap: $sentence", overlap.isEmpty())
        }
    }

    @Test
    fun responseOptionsMustShareTheDeclaredKind() {
        val pack = pack()
        val pluralFuture = pack.items.first { it.id == "u12-i-future-imp-2" }
        assertEquals("Вы бу́дете говори́ть", pluralFuture.answer)
        assertEquals("RUSSIAN_CLAUSE", pluralFuture.assessment!!.responseKind)
        assertFalse(pluralFuture.options.contains("бу́дешь"))
        assertTrue(pluralFuture.options.contains("Я бу́ду говори́ть"))
        assertTrue(pluralFuture.options.all { V2Validator.structuralResponseKind(it) == "RUSSIAN_CLAUSE" })
        assertTrue(V2Validator.featureValid(pack, pluralFuture))
        val bareAuxiliary = pluralFuture.copy(
            options = listOf(pluralFuture.answer, "бу́дешь"),
            assessment = pluralFuture.assessment.copy(contrastMembers = listOf(pluralFuture.answer, "бу́дешь")),
        )
        assertEquals("RUSSIAN_TOKEN", V2Validator.structuralResponseKind("бу́дешь"))
        assertFalse(V2Validator.featureValid(replace(pack, bareAuxiliary), bareAuxiliary))
        assertTrue(errorsFor(pack, bareAuxiliary).any { it.contains("response type") })

        val woman = pack.items.first { it.id == "u14-i-impersonal-2" }
        assertEquals("ей было холодно", woman.answer)
        assertEquals("RUSSIAN_CLAUSE", woman.assessment!!.responseKind)
        assertFalse(woman.options.contains("было"))
        assertTrue(woman.options.contains("Ему было холодно"))
        assertTrue(V2Validator.featureValid(pack, woman))
        val component = woman.copy(
            options = listOf(woman.answer, "было"),
            assessment = woman.assessment.copy(contrastMembers = listOf(woman.answer, "было")),
        )
        assertEquals("RUSSIAN_TOKEN", V2Validator.structuralResponseKind("было"))
        assertFalse(V2Validator.featureValid(replace(pack, component), component))
        assertTrue(errorsFor(pack, component).any { it.contains("response type") })

        val unequal = pack.items.first { it.id == "u13-i-tense-keep-1" }
        assertEquals("RUSSIAN_CLAUSE", unequal.assessment!!.responseKind)
        val lengths = unequal.options.map { V2Validator.structuralResponseKind(it) to it.split(Regex("\\s+")).size }
        assertTrue(lengths.all { it.first == "RUSSIAN_CLAUSE" })
        assertTrue(lengths.map { it.second }.distinct().size > 1)
        assertTrue(V2Validator.featureValid(pack, unequal))
    }

    private fun claimedPreparedUnits(sentence: String): Set<Int> {
        if (Regex("(?i)not prepared|not yet installed|do not contain|does not contain").containsMatchIn(sentence)) {
            return emptySet()
        }
        if (!Regex("(?i)lesson bod").containsMatchIn(sentence)) return emptySet()
        if (!Regex("(?i)contain|prepared|packaged").containsMatchIn(sentence)) return emptySet()
        val claimed = mutableSetOf<Int>()
        Regex("(?i)units\\s+(\\d+)\\s*(?:–|-|through|to)\\s*(\\d+)").findAll(sentence).forEach { match ->
            val start = match.groupValues[1].toInt()
            val end = match.groupValues[2].toInt()
            claimed += (minOf(start, end)..maxOf(start, end)).toSet()
        }
        return claimed
    }

    @Test
    fun everyAdvancementCheckNamesOneExplicitTarget() {
        val pack = pack()
        val validation = V2Validator.validate(pack)
        assertTrue(validation.errors.joinToString("\n"), validation.ok)
        val colocated = Regex("(?i)\\bother row\\b|\\bother line\\b|same frozen record|another cell of the same")
        (11..15).forEach { number ->
            val unit = pack.unit(number)!!
            assertEquals("CONTENT_COMPLETE_REVIEW_PENDING", unit.readiness)
            unit.objectives.filter { it.kind == "RECOGNITION" }.forEach { objective ->
                objective.checkItemIds.forEach { id ->
                    val item = pack.items.first { it.id == id }
                    assertTrue("${item.id} is not feature-valid", V2Validator.featureValid(pack, item))
                    assertFalse("${item.id} uses a source co-location prompt", colocated.containsMatchIn(item.prompt))
                    assertEquals(1, item.options.count { it == item.answer })
                    assertEquals("PENDING_QUALIFIED_REVIEW", item.reviewStatus)
                    val answerCyrillic = item.answer.any { it.code in 0x0400..0x04FF }
                    assertTrue(
                        "${item.id} mixes response types",
                        item.options.filter { it != item.answer }.all { option ->
                            option.any { it.code in 0x0400..0x04FF } == answerCyrillic
                        },
                    )
                }
            }
        }
    }

    @Test
    fun impersonalObjectiveCannotBeSatisfiedByAnUnrelatedInflectedForm() {
        val pack = pack()
        val impersonal = pack.items.filter { it.objectiveId == "obj-u14-impersonal" }
        assertTrue(impersonal.isNotEmpty())
        impersonal.forEach { item ->
            assertTrue(item.prompt.contains("impersonal past"))
            assertFalse(item.answer.contains("была"))
            assertFalse(item.answer.contains("были"))
            assertTrue(V2Validator.featureValid(pack, item) || !item.assessment!!.independent)
        }
        val seed = impersonal.first { it.assessment!!.independent }
        val feminine = pack.provenanceRecords.first { it.sourceRecordId == "verbs.csv:быть#past_f" }
        val distractor = seed.options.first { it != seed.answer }
        val options = listOf(feminine.shippedText, distractor)
        val assessment = seed.assessment!!
        val swapped = seed.copy(
            answer = feminine.shippedText,
            options = options,
            assessment = assessment.copy(
                contrastMembers = options,
                answerSourceIds = listOf(feminine.itemId),
                relationParticipantIds = listOf(assessment.relationParticipantIds[0], feminine.itemId) +
                    assessment.relationParticipantIds.drop(2),
            ),
        )
        assertFalse(V2Validator.featureValid(replace(pack, swapped), swapped))
        val errors = V2Validator.validate(replace(pack, swapped)).errors
        assertTrue(errors.joinToString("\n"), errors.any { it.contains(swapped.id) && it.contains("unrelated inflected form") })
    }

    @Test
    fun englishGlossCannotAnswerARussianSpellingQuestion() {
        val pack = pack()
        val spelling = pack.items.first { it.objectiveId == "obj-u12-spelling" && it.prompt.contains("accepted spelling") }
        assertTrue(spelling.answer.any { it.code in 0x0400..0x04FF })
        assertTrue(spelling.options.all { it.any { char -> char.code in 0x0400..0x04FF } })
        val gloss = "the good letter"
        val options = listOf(gloss, spelling.options.first { it != spelling.answer })
        val bad = spelling.copy(
            answer = gloss,
            options = options,
            assessment = spelling.assessment!!.copy(contrastMembers = options),
        )
        assertFalse(V2Validator.featureValid(replace(pack, bad), bad))
        val errors = V2Validator.validate(replace(pack, bad)).errors
        assertTrue(errors.joinToString("\n"), errors.any { it.contains(bad.id) && it.contains("English gloss") })
    }

    @Test
    fun knownAmbiguityClassesFailClosed() {
        val pack = pack()
        val past = pack.items.first { it.id == "u11-i-past-read-1" }
        val targetlessPast = past.copy(prompt = "Which admitted row is the other row of the shown form?")
        val pastErrors = errorsFor(pack, targetlessPast)
        assertTrue(pastErrors.joinToString("\n"), pastErrors.any { it.contains("past target") })
        assertTrue(pastErrors.joinToString("\n"), pastErrors.any { it.contains("source co-location prompt") })

        val imperative = pack.items.first { it.id == "u15-i-imperative-1" }
        val unspecified = imperative.copy(prompt = "Which admitted form is the imperative?")
        assertTrue(errorsFor(pack, unspecified).any { it.contains("unspecified imperative") })

        val objective = "obj-u15-aspect-consolidate"
        val cue = pack.provenanceRecords.first { it.objectiveId == objective && it.shippedText == "делать/сделать" }
        val toDo = pack.provenanceRecords.first { it.objectiveId == objective && it.shippedText == "to do" }
        val toMake = pack.provenanceRecords.first { it.objectiveId == objective && it.shippedText == "to make" }
        val aspect = pack.items.first { it.id == "u15-i-aspect-consolidate-1" }
        val options = listOf("to do", "to make")
        val glossDistractor = aspect.copy(
            prompt = "Which admitted label is the perfective partner?",
            promptLetter = cue.shippedText,
            options = options,
            answer = "to do",
            assessment = aspect.assessment!!.copy(
                testedFeature = "PERFECTIVE_PARTNER",
                cueLanguage = "ru",
                evidenceMode = "RULE_CLASSIFIES_SOURCE_EXAMPLE",
                relationKind = "RULE_CLASSIFIES_SOURCE_EXAMPLE",
                contrastMembers = options,
                answerSourceIds = listOf(toDo.itemId),
                distractorSourceIds = listOf("", toMake.itemId),
                distractorDifference = listOf("", "Does this row fail the perfective partner?"),
                whyAnswerDemonstratesObjective = "Does this answer show the perfective partner?",
                relationParticipantIds = listOf(cue.itemId, toDo.itemId, toMake.itemId),
            ),
        )
        assertTrue(errorsFor(pack, glossDistractor).any { it.contains("distractor is also supported") })

        val spelling = pack.items.first { it.id == "u12-i-spelling-2" }
        val acceptedNegative = spelling.copy(
            prompt = "Which admitted line is the accepted spelling?",
            assessment = spelling.assessment!!.copy(
                testedFeature = "ACCEPTED_SPELLING",
                whyAnswerDemonstratesObjective = "Does this answer show the accepted spelling?",
            ),
        )
        assertTrue(errorsFor(pack, acceptedNegative).any { it.contains("rejected spelling") })

        val impersonal = pack.items.first { it.objectiveId == "obj-u14-impersonal" && it.assessment!!.independent }
        val feminine = pack.provenanceRecords.first { it.sourceRecordId == "verbs.csv:быть#past_f" }
        val impersonalOptions = listOf(feminine.shippedText, impersonal.options.first { it != impersonal.answer })
        val impersonalSwap = impersonal.copy(
            answer = feminine.shippedText,
            options = impersonalOptions,
            assessment = impersonal.assessment!!.copy(
                contrastMembers = impersonalOptions,
                answerSourceIds = listOf(feminine.itemId),
                relationParticipantIds = listOf(impersonal.assessment.relationParticipantIds[0], feminine.itemId) +
                    impersonal.assessment.relationParticipantIds.drop(2),
            ),
        )
        assertTrue(errorsFor(pack, impersonalSwap).any { it.contains("unrelated inflected form") })

        val englishSpelling = spelling.copy(
            answer = "the good letter",
            options = listOf("the good letter", spelling.options.first { it.any { char -> char.code in 0x0400..0x04FF } }),
        )
        assertTrue(errorsFor(pack, englishSpelling).any { it.contains("English gloss") })
    }

    @Test
    fun aGenericRationaleIsNotTiedToTheTarget() {
        val pack = pack()
        val seed = pack.items.first { it.id == "u11-i-past-read-1" }
        val generic = seed.copy(
            assessment = seed.assessment!!.copy(
                whyAnswerDemonstratesObjective = "The contrast is the same word in the shown line.",
            ),
        )
        assertFalse(V2Validator.featureValid(replace(pack, generic), generic))
        assertTrue(errorsFor(pack, generic).any { it.contains("answer rationale is not tied to the target") })
    }

    private fun errorsFor(pack: V2CurriculumPack, item: V2CheckItem): List<String> =
        V2Validator.validate(replace(pack, item)).errors.filter { it.contains(item.id) }

    private fun replace(pack: V2CurriculumPack, item: V2CheckItem) =
        pack.copy(items = pack.items.map { if (it.id == item.id) item else it })

    private fun rewriteRationale(item: V2CheckItem): V2CheckItem {
        val assessment = item.assessment!!
        return item.copy(
            distractorRationales = item.distractorRationales.map { if (it.isEmpty()) it else "The contrast is the same word." },
            assessment = assessment.copy(
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
