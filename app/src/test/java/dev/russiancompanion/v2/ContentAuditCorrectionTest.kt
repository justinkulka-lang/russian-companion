package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.FILLED_GUIDED_PRACTICE_ROUTES
import dev.russiancompanion.v2.curriculum.V2LearnerView
import dev.russiancompanion.v2.curriculum.V2PackLoader
import dev.russiancompanion.v2.curriculum.V2StartingPoint
import dev.russiancompanion.v2.curriculum.V2Validator
import dev.russiancompanion.v2.curriculum.advancementDecision
import dev.russiancompanion.v2.curriculum.ordinaryUnitRequirements
import dev.russiancompanion.v2.curriculum.item
import dev.russiancompanion.v2.curriculum.requiredInstructionSteps
import dev.russiancompanion.v2.curriculum.step
import dev.russiancompanion.v2.curriculum.GOVERNED_IDENTITY_SCHEMA
import dev.russiancompanion.v2.curriculum.resumePositionDigest
import dev.russiancompanion.v2.curriculum.resumeIsCurrent
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
    fun possessiveFormsAreAvailableBeforeTheGuidedAgreementChoice() {
        assertPrerequisiteTeachingBeforeGuided(6, "possessive", 6)
    }

    @Test
    fun purposeRegisterExplanationPrecedesTheOrdinaryPurposeChoice() {
        assertPrerequisiteTeachingBeforeGuided(21, "purpose", 3)
    }

    @Test
    fun imperfectiveRelativeTimeExplanationPrecedesTheSentenceChoice() {
        assertPrerequisiteTeachingBeforeGuided(28, "imperfective-adverb", 2,
            additionalTeachingIds = listOf("u28-st-teach-imperfective-timing-context"))
    }

    @Test
    fun perfectiveRelativeTimeExplanationPrecedesTheFutureSentenceChoice() {
        assertPrerequisiteTeachingBeforeGuided(28, "perfective-adverb", 3,
            additionalTeachingIds = listOf("u28-st-teach-perfective-timing-context"))
    }

    @Test
    fun reorderedSessionsRejectResumeIdentityBoundToThePreviousPosition() {
        val current = pack()
        listOf(6 to "possessive", 21 to "purpose", 28 to "imperfective-adverb", 28 to "perfective-adverb",
            6 to "feminine", 7 to "spelling", 8 to "spelling", 12 to "aspect", 16 to "inst-sg", 16 to "inst-adj").forEach { (number, topic) ->
            val sessionId = "u$number-s-$topic"
            val session = current.unit(number)!!.sessions.single { it.id == sessionId }
            val teaching = session.steps.filter { it.kind == "TEACH" }
            val guided = session.steps.single { it.kind == "GUIDED" }
            val previousOrder = listOf(teaching.first(), guided) + teaching.drop(1) +
                session.steps.filter { it.kind != "TEACH" && it.kind != "GUIDED" }
            val previous = current.copy(units = current.units.map { unit ->
                if (unit.number != number) unit else unit.copy(sessions = unit.sessions.map {
                    if (it.id != sessionId) it else it.copy(steps = previousOrder)
                })
            })
            val previousDigest = resumePositionDigest(previous, sessionId, 1)!!
            val learner = V2LearnerView(
                onboardingCompleted = true, currentUnitNumber = number, startingPoint = V2StartingPoint.NEW_TO_CYRILLIC,
                hasPenguin = true, hasAnki = false, hasBinder = false, hasTutor = false, dailyMinutes = 25,
                transliterationEnabled = false, currentSessionId = sessionId, currentStepIndex = 1,
                completedStepIds = emptySet(), correctItemIds = emptySet(), bookAttestedUnits = emptySet(),
                completedUnitNumbers = emptySet(), resumeIdentitySchema = GOVERNED_IDENTITY_SCHEMA,
                resumeContentDigest = previousDigest,
            )
            assertTrue(resumeIsCurrent(previous, learner))
            assertFalse("A saved guided position must not reopen a different teaching card", resumeIsCurrent(current, learner))
            assertTrue(resumeIsCurrent(current, learner.copy(resumeContentDigest = resumePositionDigest(current, sessionId, 1)!!)))
        }
    }

    @Test
    fun accusativeDeclensionExplanationPrecedesTheNounChoice() {
        assertPrerequisiteTeachingBeforeGuided(6, "feminine", 4)
    }

    @Test
    fun adjectivePluralFormPrecedesTheSpellingChoice() {
        assertPrerequisiteTeachingBeforeGuided(7, "spelling", 4)
    }

    @Test
    fun nounPluralFormAndSevenLetterRulePrecedeTheChoice() {
        assertPrerequisiteTeachingBeforeGuided(8, "spelling", 5)
    }

    @Test
    fun aspectCounterpartExplanationPrecedesThePartnerChoice() {
        assertPrerequisiteTeachingBeforeGuided(12, "aspect", 3)
    }

    @Test
    fun instrumentalNounExplanationPrecedesTheCaseChoice() {
        assertPrerequisiteTeachingBeforeGuided(16, "inst-sg", 7)
    }

    @Test
    fun instrumentalAdjectiveExplanationPrecedesTheCaseChoice() {
        assertPrerequisiteTeachingBeforeGuided(16, "inst-adj", 4)
    }

    private fun assertPrerequisiteTeachingBeforeGuided(
        number: Int,
        topic: String,
        teachingCount: Int,
        additionalTeachingIds: List<String> = emptyList(),
        pack: dev.russiancompanion.v2.curriculum.V2CurriculumPack = pack(),
    ) {
        val unit = pack.unit(number)!!
        val session = unit.sessions.single { it.id == "u$number-s-$topic" }
        val guidedIndex = session.steps.indexOfFirst { it.kind == "GUIDED" }
        assertEquals("Prerequisite teaching still follows guided practice in ${session.id}",
            teachingCount + additionalTeachingIds.size, session.steps.take(guidedIndex).count { it.kind == "TEACH" })
        assertTrue(session.steps.drop(guidedIndex).none { it.kind == "TEACH" })
        assertEquals((1..teachingCount).map { "u$number-st-teach-$topic-$it" } + additionalTeachingIds,
            session.steps.take(guidedIndex).map { it.id })
        val objectiveForCheck = unit.objectives.single { it.id == "obj-u$number-$topic" }
        val checkStep = unit.sessions.flatMap { it.steps }.single { it.kind == "CHECK" && objectiveForCheck.checkItemIds.any { id -> id in it.itemIds } }
        // The topic session holds GUIDED then REMEDIATE, and the topic's CHECK follows both in the unit's step order:
        // at the end of that session or in a later end-of-unit check session (A2-24, T15a; T15a-n1 order rule).
        val topicTail = session.steps.drop(guidedIndex).map { it.kind }
        assertEquals(if (checkStep in session.steps) listOf("GUIDED", "REMEDIATE", "CHECK") else listOf("GUIDED", "REMEDIATE"), topicTail)
        val order = unit.sessions.flatMap { it.steps }.map { it.id }
        val guidedAt = order.indexOf(session.steps[guidedIndex].id)
        val remediationAt = order.indexOf(session.steps.single { it.kind == "REMEDIATE" }.id)
        val teachingAt = session.steps.take(guidedIndex).map { order.indexOf(it.id) }
        val checkAt = order.indexOf(checkStep.id)
        assertTrue("${checkStep.id} must follow ${session.id} guided practice and remediation", checkAt > guidedAt && checkAt > remediationAt)
        assertTrue("${checkStep.id} must follow the topic teaching", teachingAt.all { checkAt > it })
        assertEquals(listOf("u$number-g-$topic-0"), session.steps[guidedIndex].itemIds)
        val objective = unit.objectives.single { it.id == "obj-u$number-$topic" }
        assertTrue(objective.gating)
        assertTrue(objective.checkItemIds.isNotEmpty())
        assertTrue(objective.checkItemIds.all { id -> checkStep.itemIds.contains(id) })
    }

    @Test
    fun aUnitSevenCheckPlacedBeforeItsTeachingFails() {
        // T15a-n1 regression: moving the Unit 7 spelling CHECK into an earlier session, ahead of its teaching, must fail.
        val pack = pack()
        val unit = pack.unit(7)!!
        val objective = unit.objectives.single { it.id == "obj-u7-spelling" }
        val check = unit.sessions.flatMap { it.steps }.single { it.kind == "CHECK" && objective.checkItemIds.any { id -> id in it.itemIds } }
        val first = unit.sessions.first()
        val moved = pack.copy(units = pack.units.map { u ->
            if (u.number != 7) u else u.copy(sessions = u.sessions.map { s ->
                when {
                    s.id == first.id -> s.copy(steps = listOf(check) + s.steps.filter { it.id != check.id })
                    else -> s.copy(steps = s.steps.filter { it.id != check.id })
                }
            })
        })
        assertPrerequisiteTeachingBeforeGuided(7, "spelling", 4)
        val failure = runCatching { assertPrerequisiteTeachingBeforeGuided(7, "spelling", 4, pack = moved) }.exceptionOrNull()
        assertTrue("A CHECK before its teaching must fail the order check", failure is AssertionError)
    }

    @Test
    fun possessiveInstructionLabelsFormsAndWorksTheRetainedFeminineCaseChange() {
        val pack = pack()
        val unit = pack.unit(6)!!
        val session = unit.sessions.single { it.id == "u6-s-possessive" }
        val rule = session.steps.single { it.id == "u6-st-teach-possessive-2" }.body
        assertTrue("Possessive instruction must state the agreement rule", rule.contains("gender, number and case"))
        assertTrue(rule.contains("noun, not from the gender of its owner"))
        val forms = session.steps.filter { it.kind == "TEACH" }.joinToString("\n") { it.body }
        assertTrue(forms.contains("мо́й is masculine and моя́ is feminine"))
        assertTrue(forms.contains("моё is the neuter singular nominative"))
        assertTrue(forms.contains("мои́ is the plural nominative"))
        assertTrue(forms.contains("тво́й is masculine singular nominative; твоя́ is feminine singular nominative"))
        val worked = session.steps.single { it.id == "u6-st-teach-possessive-3" }
        // T15a-n2: the worked change shows single attested forms, not an unattested phrase.
        assertTrue(worked.body.contains("change both words: на́ша becomes на́шу, and сестра́ becomes сестру́"))
        assertTrue(worked.body.contains("на́ша is the feminine singular nominative form of наш"))
        assertTrue(worked.body.contains("на́шего is the masculine and neuter genitive form"))
        assertTrue(worked.body.contains("на́ши is plural"))
        assertFalse(worked.body.contains("на́шу сестру́"))
        assertTrue(session.steps.indexOf(worked) < session.steps.indexOfFirst { it.kind == "GUIDED" })
        assertEquals(18, unit.advancement!!.minimumDistinctChecks)
    }

    @Test
    fun possessiveFeedbackAndRemediationExplainTheSameGenderNumberAndCaseRule() {
        val pack = pack()
        val unit = pack.unit(6)!!
        val session = unit.sessions.single { it.id == "u6-s-possessive" }
        val guided = pack.item("u6-g-possessive-0")!!
        assertTrue("Guided feedback must explain why the feminine form fits", guided.feedback.contains("собака is feminine singular"))
        assertEquals("моя́", guided.answer)
        assertEquals(listOf("моя́", "мо́й", "мои́"), guided.options)
        assertTrue(guided.distractorRationales[1].contains("masculine singular nominative"))
        assertTrue(guided.distractorRationales[2].contains("plural nominative; the noun here is singular"))
        assertEquals(guided.distractorRationales, guided.assessment!!.distractorDifference)
        assertEquals(guided.feedback, guided.assessment!!.whyAnswerDemonstratesObjective)
        val masculine = pack.item("u6-i-possessive-1")!!
        assertEquals("мо́й", masculine.answer)
        assertTrue(masculine.feedback.contains("стол is masculine singular"))
        assertTrue(masculine.distractorRationales[1].contains("feminine singular nominative"))
        val rem = session.steps.single { it.kind == "REMEDIATE" }
        val teaching = session.steps.filter { it.kind == "TEACH" }
        assertTrue(teaching.all { rem.body.contains(it.body) })
        assertEquals(unit.objectives.single { it.id == "obj-u6-possessive" }.exampleIds.toSet(), rem.exampleIds.toSet())
        assertTrue(session.steps.filter { it.kind == "GUIDED" || it.kind == "CHECK" }.all { it.remediationStepId == rem.id })
        assertEquals("на́шу", pack.item("u6-i-possessive-2")!!.answer)
        assertEquals("на́ша", pack.item("u6-i-possessive-2")!!.promptLetter)
        assertEquals("твоя́", pack.item("u6-i-possessive-3")!!.answer)
    }

    @Test
    fun possessiveWorkedExplanationRecordsStayPendingAndRejectAChangedHash() {
        val pack = pack()
        val records = pack.provenanceRecords.filter { it.itemId.startsWith("prov-ca-u6-worked-") }
        assertEquals("New explanations need exact pending coverage records", 23, records.size)
        records.forEach { record ->
            assertContentAuditResearch(record)
            assertEquals(6, record.unitNumber)
            assertEquals("obj-u6-possessive", record.objectiveId)
            assertTrue(record.synthesis!!.bibliography.contains("Leed_Less_9-12.pdf"))
            assertTrue(record.synthesis!!.bibliography.contains("printed pages 31 and 37"))
            listOf("prov-ex-u6-possessive-0-ru", "prov-ex-u6-possessive-1-ru", "prov-ca-6-190",
                "prov-ca-6-191", "prov-ca-6-195", "prov-ca-6-196").forEach { id ->
                assertTrue(record.synthesis!!.bibliography.contains(id))
                assertTrue("Unresolved retained record $id", pack.provenanceRecords.any { it.itemId == id })
            }
        }
        val valid = V2Validator.validate(pack)
        assertTrue(valid.errors.joinToString("\n"), valid.ok)
        val bad = pack.copy(provenanceRecords = pack.provenanceRecords.map {
            if (it.itemId != records.first().itemId) it else it.copy(contentHash = "0".repeat(64))
        })
        assertTrue(V2Validator.validate(bad).errors.any { it.contains(records.first().itemId) && it.contains("source hash") })
    }

    @Test
    fun spellingInstructionExplainsFormsBeforePracticeAndWorksTheAccusative() {
        val pack = pack()
        val unit = pack.unit(7)!!
        val session = unit.sessions.single { it.id == "u7-s-spelling" }
        val guided = session.steps.indexOfFirst { it.kind == "GUIDED" }
        val teaching = session.steps.take(guided).filter { it.kind == "TEACH" }
        assertEquals(4, teaching.size)
        val text = teaching.joinToString("\n") { it.body }
        assertTrue(text.contains("gender, number and case"))
        assertTrue(text.contains("хоро́ший is the dictionary form: masculine singular nominative"))
        assertTrue(text.contains("хоро́шая is feminine singular nominative; хоро́шее is neuter singular nominative"))
        assertTrue(text.contains("хоро́шие is plural nominative"))
        assertTrue(text.contains("an unstressed о in an ending is written е"))
        assertTrue(text.contains("write и rather than ы after ш"))
        // T15b: the unattested phrases хоро́шая книга / хоро́шую книгу become single sourced forms.
        assertTrue(text.contains("the adjective хоро́шая becomes хоро́шую, and the noun кни́га becomes кни́гу"))
        assertTrue(text.contains("change both words"))
        // T15g (owner decision D18d): the exercise commentary "All the options are already correctly spelled." is removed;
        // the teaching keeps the forms and the spelling rules only.
        assertFalse(text.contains("All the options are already correctly spelled"))
        assertFalse(text.contains("options"))
        assertEquals(22, unit.advancement!!.minimumDistinctChecks)
    }

    @Test
    fun spellingQuestionsNameTheActualFormEvidenceAndRemediationRetainsAllTeaching() {
        val pack = pack()
        listOf("u7-g-spelling-0", "u7-i-spelling-1", "u7-i-spelling-2").forEach { id ->
            val item = pack.item(id)!!
            assertEquals("ADJECTIVE_NOMINATIVE_FORM", item.assessment!!.testedFeature)
            assertEquals("SAME_LEMMA_DIFFERENT_FIELD", item.assessment!!.relationKind)
        }
        val item = pack.item("u7-i-spelling-3")!!
        assertEquals("FEMININE_ADJECTIVE_ACCUSATIVE", item.assessment!!.testedFeature)
        assertTrue(item.prompt.contains("feminine singular accusative"))
        assertTrue(item.feedback.contains("Both words change"))
        assertEquals(item.feedback, item.assessment!!.whyAnswerDemonstratesObjective)
        // T15b: single OpenRussian forms of хороший replace the unattested phrase options.
        assertEquals("SAME_LEMMA_DIFFERENT_FIELD", item.assessment!!.relationKind)
        assertEquals("хоро́шая", item.promptLetter)
        assertEquals("хоро́шую", item.answer)
        assertEquals(listOf("хоро́шую", "хоро́шего", "хоро́шие"), item.options)
        assertEquals(listOf("prov-ex-or-u7-spelling-2", "prov-t15b-u7-spelling-khoroshiy-decl-f-acc",
            "prov-t15b-u7-spelling-khoroshiy-decl-m-gen", "prov-ex-or-u7-spelling-4"), item.assessment!!.relationParticipantIds)
        val unit = pack.unit(7)!!
        val session = unit.sessions.single { it.id == "u7-s-spelling" }
        val remediation = session.steps.single { it.kind == "REMEDIATE" }
        assertTrue(session.steps.filter { it.kind == "TEACH" }.all { remediation.body.contains(it.body) })
        assertEquals(unit.objectives.single { it.id == "obj-u7-spelling" }.exampleIds, remediation.exampleIds)
        assertTrue(session.steps.filter { it.kind == "GUIDED" || it.kind == "CHECK" }.all { it.remediationStepId == remediation.id })
    }

    @Test
    fun spellingExplanationRecordsResolveRetainedFormsAndStayPending() {
        val pack = pack()
        val records = pack.provenanceRecords.filter { it.itemId.startsWith("prov-ca-u7-worked-") }
        assertEquals(17, records.size)
        records.forEach { record ->
            assertContentAuditResearch(record)
            assertEquals(7, record.unitNumber)
            assertEquals("obj-u7-spelling", record.objectiveId)
            listOf("prov-ex-u7-spelling-0-ru", "prov-ex-or-u7-spelling-1", "prov-ex-or-u7-spelling-2", "prov-ex-or-u7-spelling-3", "prov-ex-or-u7-spelling-4", "prov-ca-7-489", "prov-ca-7-490", "prov-ca-7-494", "prov-ca-7-495").forEach { id ->
                assertTrue(record.synthesis!!.bibliography.contains(id))
                assertTrue("Unresolved retained record $id", pack.provenanceRecords.any { it.itemId == id })
            }
        }
        val valid = V2Validator.validate(pack)
        assertTrue(valid.errors.joinToString("\n"), valid.ok)
        val bad = pack.copy(provenanceRecords = pack.provenanceRecords.map {
            if (it.itemId != records.first().itemId) it else it.copy(contentHash = "0".repeat(64))
        })
        assertTrue(V2Validator.validate(bad).errors.any { it.contains(records.first().itemId) && it.contains("source hash") })
    }

    @Test
    fun unitEightTeachingWorksTheNounPluralAndSeparatesOtherPatterns() {
        val pack = pack()
        val unit = pack.unit(8)!!
        val session = unit.sessions.single { it.id == "u8-s-spelling" }
        val text = session.steps.takeWhile { it.kind != "GUIDED" }.joinToString("\n") { it.body }
        assertEquals(5, session.steps.takeWhile { it.kind != "GUIDED" }.count { it.kind == "TEACH" })
        assertTrue(text.contains("After г, к, х, ж, ч, ш, or щ, write и rather than ы"))
        assertTrue(text.contains("кни́га becomes кни́ги"))
        assertTrue(text.contains("stem ends in г"))
        assertTrue(text.contains("хоро́шее is neuter singular nominative; хоро́шие is plural nominative"))
        assertTrue(text.contains("хоро́ший is the dictionary form: masculine singular nominative"))
        assertTrue(text.contains("дни is also plural nominative and means days"))
        assertTrue(text.contains("и does not follow a letter in the seven-letter set"))
        assertTrue(text.contains("Not every plural ending in и comes from the seven-letter rule"))
        // T15c: inventory entry 847 (assessment commentary) is reworded to an instruction.
        assertFalse(text.contains("All these options are correctly spelled"))
        assertTrue(text.contains("Choose the form with the specified grammatical features.")) // T16-n2 (D31c): was "Choose the requested form."
        assertEquals(18, unit.advancement!!.minimumDistinctChecks)
    }

    @Test
    fun unitEightChecksAssessNominativeFormsAndRetainCompleteRemediation() {
        val pack = pack()
        val ids = listOf("u8-g-spelling-0", "u8-i-spelling-1", "u8-i-spelling-2", "u8-i-spelling-3", "u8-i-spelling-4")
        // T15g (CC010/IA016): the guided spelling check moved from кни́га (the plural check u8-i-plural-6's exact tuple) to
        // the adjective хоро́ший, so only u8-i-spelling-1 still assesses a noun number form.
        val answers = listOf("хоро́шие", "кни́га", "хоро́шие", "хоро́шее", "хоро́ший")
        assertEquals("хоро́ший", pack.item("u8-g-spelling-0")!!.promptLetter)
        ids.forEachIndexed { index, id ->
            val item = pack.item(id)!!
            assertEquals(if (index == 1) "NOUN_NOMINATIVE_NUMBER_FORM" else "ADJECTIVE_NOMINATIVE_FORM", item.assessment!!.testedFeature)
            assertEquals(answers[index], item.answer)
            assertEquals("SAME_LEMMA_DIFFERENT_FIELD", item.assessment!!.relationKind)
            assertEquals(index != 0, item.assessment!!.independent)
        }
        val unit = pack.unit(8)!!
        val session = unit.sessions.single { it.id == "u8-s-spelling" }
        val remediation = session.steps.single { it.kind == "REMEDIATE" }
        assertTrue(session.steps.filter { it.kind == "TEACH" }.all { remediation.body.contains(it.body) })
        assertEquals(unit.objectives.single { it.id == "obj-u8-spelling" }.exampleIds, remediation.exampleIds)
        assertEquals(7, remediation.exampleIds.size)
        assertTrue(session.steps.filter { it.kind == "GUIDED" || it.kind == "CHECK" }.all { it.remediationStepId == remediation.id })
    }

    @Test
    fun unitEightExplanationCoverageStaysPendingAndRejectsTamperedHashes() {
        val pack = pack()
        val records = pack.provenanceRecords.filter { it.itemId.startsWith("prov-ca-u8-worked-") }
        assertEquals(17, records.size)
        records.forEach { record ->
            assertContentAuditResearch(record)
            assertEquals(8, record.unitNumber)
            assertEquals("obj-u8-spelling", record.objectiveId)
            assertTrue(record.synthesis!!.bibliography.contains("https://russian.cornell.edu/grammar/html/spelling_rules.htm"))
            (listOf("prov-ex-u8-spelling-0-ru", "prov-ca-8-324") + (1..6).map { "prov-ex-or-u8-spelling-$it" }).forEach { id ->
                assertTrue(record.synthesis!!.bibliography.contains(id))
                assertTrue("Unresolved retained record $id", pack.provenanceRecords.any { it.itemId == id })
            }
        }
        val valid = V2Validator.validate(pack)
        assertTrue(valid.errors.joinToString("\n"), valid.ok)
        val bad = pack.copy(provenanceRecords = pack.provenanceRecords.map {
            if (it.itemId != records.first().itemId) it else it.copy(contentHash = "0".repeat(64))
        })
        assertTrue(V2Validator.validate(bad).errors.any { it.contains(records.first().itemId) && it.contains("source hash") })
    }

    @Test
    fun unitTwelveAspectTeachingLabelsPairsAndAvoidsAnUnfinishedActionRule() {
        val pack = pack()
        val unit = pack.unit(12)!!
        val session = unit.sessions.single { it.id == "u12-s-aspect" }
        val teaching = session.steps.takeWhile { it.kind != "GUIDED" }
        assertEquals(3, teaching.count { it.kind == "TEACH" })
        val text = teaching.joinToString("\n") { it.body }
        assertTrue(text.contains("Aspect describes how an action is viewed, not when it happens"))
        assertTrue(text.contains("Imperfective does not by itself mean unfinished"))
        assertTrue(text.contains("говори́ть is imperfective; сказа́ть is perfective"))
        assertTrue(text.contains("де́лать is imperfective; сде́лать is perfective"))
        assertTrue(text.contains("not a universal pair for the English verb speak"))
        // T16b (prep section 1 and A2-26): the check-commentary sentence and the sentence that states u12-i-aspect-2's answer as
        // an instruction are removed (were assertTrue(contains(...)) for both); the pairs stay taught above.
        assertFalse(text.contains("not choose aspect in a sentence"))
        assertFalse(text.contains("Given де́лать and a request for its perfective counterpart, choose сде́лать"))
        assertTrue(text.contains("rather than assuming every prefix changes only aspect"))
        assertEquals(12, unit.advancement!!.minimumDistinctChecks)
    }

    @Test
    fun unitTwelveAspectFeedbackExplainsBothOptionsAndRemediationRetainsThePairs() {
        val pack = pack()
        val ids = listOf("u12-g-aspect-0", "u12-i-aspect-1", "u12-i-aspect-2")
        val answers = listOf("сказа́ть", "говори́ть", "сде́лать")
        val differences = listOf("де́лать is imperfective", "сде́лать is perfective", "говори́ть is imperfective")
        ids.forEachIndexed { index, id ->
            val item = pack.item(id)!!
            assertEquals(answers[index], item.answer)
            assertTrue(item.prompt.contains(if (index < 2) "saying/telling pair shown here" else "doing/making pair shown here"))
            assertTrue(item.feedback.contains("partner of"))
            assertTrue(item.distractorRationales[1].contains(differences[index]))
            assertEquals(item.feedback, item.assessment!!.whyAnswerDemonstratesObjective)
            assertEquals(item.distractorRationales, item.assessment!!.distractorDifference)
            assertEquals("SOURCE_SUPPLIED_PAIR", item.assessment!!.relationKind)
            assertEquals(index != 0, item.assessment!!.independent)
        }
        val unit = pack.unit(12)!!
        val session = unit.sessions.single { it.id == "u12-s-aspect" }
        val remediation = session.steps.single { it.kind == "REMEDIATE" }
        assertTrue(session.steps.filter { it.kind == "TEACH" }.all { remediation.body.contains(it.body) })
        assertEquals(unit.objectives.single { it.id == "obj-u12-aspect" }.exampleIds, remediation.exampleIds)
        assertEquals(4, remediation.exampleIds.size)
        assertTrue(session.steps.filter { it.kind == "GUIDED" || it.kind == "CHECK" }.all { it.remediationStepId == remediation.id })
    }

    @Test
    fun unitTwelveAspectCoverageResolvesOriginalPairsAndRejectsTamperedHashes() {
        val pack = pack()
        val records = pack.provenanceRecords.filter { it.itemId.startsWith("prov-ca-u12-worked-") }
        assertEquals(20, records.size)
        records.forEach { record ->
            assertContentAuditResearch(record)
            assertEquals(12, record.unitNumber)
            assertEquals("obj-u12-aspect", record.objectiveId)
            assertTrue(record.synthesis!!.bibliography.contains("https://russian.cornell.edu/grammar/html/gr04_d.htm"))
            (listOf("prov-ca-12-371") + (0..3).map { "prov-ex-u12-aspect-$it-ru" }).forEach { id ->
                assertTrue(record.synthesis!!.bibliography.contains(id))
                assertTrue("Unresolved retained record $id", pack.provenanceRecords.any { it.itemId == id })
            }
        }
        val valid = V2Validator.validate(pack)
        assertTrue(valid.errors.joinToString("\n"), valid.ok)
        val bad = pack.copy(provenanceRecords = pack.provenanceRecords.map {
            if (it.itemId != records.first().itemId) it else it.copy(contentHash = "0".repeat(64))
        })
        assertTrue(V2Validator.validate(bad).errors.any { it.contains(records.first().itemId) && it.contains("source hash") })
    }

    @Test
    fun negativeAbsenceCheckDoesNotInferGenitiveFromTheMeaningOrSurfaceForm() {
        val pack = pack()
        val unit = pack.unit(24)!!
        val session = unit.sessions.single { it.id == "u24-s-stand-ins" }
        // T18de (D62a, D66): unsupported absence sentence/example -> absent; cited single-form retrieval keeps the case boundary.
        assertNull(pack.item("u24-i-stand-ins-2"))
        assertTrue(pack.examples.none { it.id == "ex-u24-stand-ins-2" })
        val item = pack.item("u24-i-type-nobody-available")!!
        assertEquals("не́кого", item.answer)
        assertTrue(item.isTypedResponse())
        assertFalse(item.prompt.contains("genitive", ignoreCase = true))
        assertFalse(item.promptLetter.orEmpty().contains("genitive", ignoreCase = true))
        val cited = pack.provenanceRecords.single { it.itemId == item.keyCitation }
        assertEquals(item.answer, cited.shippedText)
        assertTrue(V2Validator.isFrozenSourceRecord(pack, cited))
        val explanation = session.steps.single { it.id == "u24-st-teach-stand-ins-2" }.body
        assertTrue(explanation.contains("This negative form belongs to a set with no nominative."))
        val remediation = session.steps.single { it.id == "u24-st-rem-stand-ins" }
        val teaching = session.steps.filter { it.kind == "TEACH" }
        assertEquals(teaching.joinToString("\n\n") { it.body }, remediation.body)
        assertEquals(teaching.flatMap { it.exampleIds }.distinct(), remediation.exampleIds)
        val guidedIndex = session.steps.indexOfFirst { it.kind == "GUIDED" }
        assertTrue(session.steps.take(guidedIndex).count { it.kind == "TEACH" } == 3)
        assertTrue(session.steps.drop(guidedIndex).none { it.kind == "TEACH" })
        assertEquals(6, unit.advancement!!.minimumDistinctChecks)
        assertTrue(V2Validator.validate(pack).errors.joinToString("\n"), V2Validator.validate(pack).ok)
    }

    @Test
    fun courtesyRegisterCorrectionIsTaughtBeforePracticeAndSurvivesRemediation() {
        val pack = pack()
        val unit = pack.unit(25)!!
        val session = unit.sessions.single { it.id == "u25-s-courtesy" }
        val teaching = session.steps.filter { it.kind == "TEACH" }
        val worked = session.steps.single { it.id == "u25-st-teach-courtesy-2" }
        val guidedIndex = session.steps.indexOfFirst { it.kind == "GUIDED" }
        assertTrue(session.steps.indexOf(worked) < guidedIndex)
        assertTrue(session.steps.drop(guidedIndex).none { it.kind == "TEACH" })
        // T18de (D64a, D66): synthesis wording and English options -> current frozen singular/plural forms.
        val corrected = "The ending -те marks polite or plural address. Use извини́ in familiar singular address."
        assertTrue(worked.body.contains("Извини́те means excuse me with polite or plural address"))
        assertTrue(worked.body.contains("извини́ is the familiar singular form"))
        val remediation = pack.step("u25-st-rem-courtesy")!!.second
        assertEquals(teaching.joinToString("\n\n") { it.body }, remediation.body)
        assertEquals(teaching.flatMap { it.exampleIds }.distinct(), remediation.exampleIds)
        assertEquals(setOf("ex-u25-courtesy-0", "ex-u25-courtesy-1", "ex-u25-courtesy-2"), remediation.exampleIds.toSet())
        val guided = pack.item("u25-g-courtesy-0")!!
        assertEquals("Пожа́луйста", guided.answer)
        assertTrue(guided.distractorRationales[2].contains("polite or plural address"))
        assertTrue(guided.distractorRationales[2].contains("Извини́те"))
        assertEquals(guided.distractorRationales[2], guided.assessment!!.distractorDifference[2])
        val apology = pack.item("u25-i-courtesy-2")!!
        assertEquals("извини́те", apology.answer)
        assertTrue(apology.options.contains("извини́"))
        assertEquals(corrected, pack.examples.single { it.id == "ex-u25-courtesy-2" }.note)
        assertTrue(pack.provenanceRecords.any {
            it.unitNumber == 25 && it.objectiveId == "obj-u25-courtesy" && it.shippedText == corrected
        })
        assertTrue(unit.objectives.single { it.id == "obj-u25-courtesy" }.gating)
        assertEquals(listOf("u25-i-courtesy-1", "u25-i-courtesy-2"),
            unit.objectives.single { it.id == "obj-u25-courtesy" }.checkItemIds.filterNot { pack.item(it)!!.isTypedResponse() })
    }

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
        // T16: Unit 11 has 20 required checks (8 closed, 12 typed; was 8); its gate is unchanged.
        assertEquals(20, required.size)
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
        val ordinary = dev.russiancompanion.v2.curriculum.ordinaryUnitRequirements(pack, unit, learner)
        assertTrue(ordinary.remaining.joinToString("\n"), ordinary.allowed)
        assertFalse(advancementDecision(pack, unit, learner).allowed) // Its separate cumulative review is still required.
        assertFalse(dev.russiancompanion.v2.curriculum.ordinaryUnitRequirements(pack, unit, learner.copy(correctItemIds = correct - "u11-i-past-aspect-1")).allowed)
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
            // T16: Unit 11 past-read remediation repeats its whole session's teaching (was equal to u11-st-teach-past-read-2).
            // T16f (CC021): Unit 12 future-imp remediation repeats its whole session's teaching (was equal to u12-st-teach-future-imp-2).
            if ((number == 3 && topic == "gender") || (number == 13 && topic == "past-aspect") || (number == 12 && topic == "future-imp") ||
                (number == 11 && topic in listOf("past-aspect", "past-read")) || (number == 28 && topic in listOf("imperfective-adverb", "perfective-adverb")) || (number == 21 && topic == "purpose") ||
                (number == 24 && topic in listOf("stand-ins", "word-order-contrast")) ||
                (number == 26 && topic == "letter-writing")) {
                val session = pack.unit(number)!!.sessions.single { it.steps.any { step -> step.id == teach.id } }
                val teaching = session.steps.filter { it.kind == "TEACH" }
                assertEquals(remediation.id, teaching.joinToString("\n\n") { it.body }, remediation.body)
                assertEquals(remediation.id, teaching.flatMap { it.exampleIds }.distinct(), remediation.exampleIds)
            } else if ((number in listOf(7, 8) && topic == "spelling") || (number == 12 && topic == "aspect") ||
                (number == 16 && topic in listOf("inst-sg", "inst-adj", "complements"))) {
                assertTrue(remediation.body.contains(teach.body))
                val session = pack.unit(number)!!.sessions.single { it.steps.any { step -> step.id == teach.id } }
                val teaching = session.steps.filter { it.kind == "TEACH" }
                assertEquals(remediation.id, teaching.joinToString("\n\n") { it.body }, remediation.body)
                assertEquals(remediation.id, teaching.flatMap { it.exampleIds }.distinct(), remediation.exampleIds)
            }
            else assertEquals(remediation.id, teach.body, remediation.body)
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
        val teacher = pack.item("u6-i-animacy-1")!!
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
        // T18de (D62a, D66): retired absent-person sentence -> frozen single-form key, kept distinct from an indefinite person.
        assertNull(pack.item("u24-i-stand-ins-2"))
        val absentPerson = pack.item("u24-i-type-nobody-available")!!
        assertNotEquals("не́кто", absentPerson.answer)
        assertEquals("не́кого", absentPerson.answer)
        assertEquals("учи́теля", pack.item("u6-i-animacy-1")!!.answer)
        assertEquals("на́шу", pack.item("u6-i-possessive-2")!!.answer)
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
        val decision = ordinaryUnitRequirements(pack, unit, learner)
        assertTrue(decision.remaining.joinToString("\n"), decision.allowed)
        val full = advancementDecision(pack, unit, learner)
        assertFalse(full.allowed)
        assertEquals(listOf("Complete the assigned earlier-objective review"), full.remaining)
        FILLED_GUIDED_PRACTICE_ROUTES.forEach { (_, guidedId) ->
            val guided = pack.step(guidedId)!!.second
            assertEquals("GUIDED", guided.kind)
            assertTrue(guided.itemIds.isNotEmpty())
        }
    }

    private fun pack() = V2PackLoader.parse(File(repo(), "app/src/main/assets/content/v2-curriculum.json").readText())

    private fun repo(): File = listOf(File("."), File("..")).first { File(it, "app/src/main").isDirectory }
}
