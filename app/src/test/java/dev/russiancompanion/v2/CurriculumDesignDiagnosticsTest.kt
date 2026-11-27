package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.*
import org.junit.Assert.*
import org.junit.Test
import java.io.File

/** Synthetic authoring fixtures are not admitted curriculum, source evidence or learner credit. */
class CurriculumDesignDiagnosticsTest {
    @Test fun currentCorpusHasLocatedSignalsWhileCompatibilityValidationStillPasses() {
        val report = V2DesignDiagnostics.audit(current)
        assertEquals(30, report.counts["units"])
        // T18c-1 (D57a, D58b): 873 -> 870 items; 664 -> 662 required checks.
        assertEquals(870, report.counts["items"]) // T18b expectation change, old -> new: 867 -> 873 (six typed Unit 22 checks, D56c); T18a-2 expectation change, old -> new: 852 -> 867 (fifteen typed Unit 21 checks, D54e); T17e appends 12 typed Unit 20 checks and 2 typed Unit 20 guided practice items (838 -> 852); T17d appends 12 typed Unit 19 checks and 2 typed Unit 19 guided practice items (824 -> 838); T17c appends 16 typed Unit 18 checks and 2 typed Unit 18 guided practice items (806 -> 824); T17b appends 11 typed Unit 17 checks (795 -> 806); T17a appends 8 typed Unit 16 checks (787 -> 795); T16e appends 14 typed Unit 15 checks (773 -> 787); T16d appends 14 typed Unit 14 checks (759 -> 773); T16c appends 11 typed Unit 13 checks (748 -> 759); T16b appends 8 typed Unit 12 checks (740 -> 748); T16 appends 12 typed Unit 11 checks (728 -> 740); T15i retires u7-i-manner-4 (729 -> 728); T15h appends u10-i-absence-5 (728 -> 729); T15b appends 15 typed Unit 7 checks; T15c 9 typed Unit 8 checks; T15d 15 typed Unit 9 checks; T15e 10 typed Unit 10 checks; T15g retires u10-i-absence-3 and u10-i-absence-4 (730 -> 728)
        assertEquals(662, report.counts["requiredChecks"]) // T18b expectation change, old -> new: 658 -> 664 (six typed Unit 22 checks). T18a-2 expectation change, old -> new: 643 -> 658 (fifteen typed Unit 21 checks). T17e: 631 -> 643 (twelve typed Unit 20 checks). T17d: 619 -> 631 (12 typed Unit 19 checks; the 2 guided items are not checks); T17c: 603 -> 619 (16 typed Unit 18 checks; the 2 guided items are not checks); T17b: 592 -> 603 (11 typed Unit 17 checks); T17a: 584 -> 592 (8 typed Unit 16 checks); T16e: 570 -> 584 (14 typed Unit 15 checks); T16d: 556 -> 570 (14 typed Unit 14 checks); T16c: 545 -> 556 (11 typed Unit 13 checks); T16b: 537 -> 545 (8 typed Unit 12 checks); T16: 525 -> 537 (12 typed Unit 11 checks); T15g: 527 -> 525, the two retired duplicates; T15h: 525 -> 526 (u10-i-absence-5); T15i: 526 -> 525 (u7-i-manner-4 retired)
        assertEquals("NEEDS_CORRECTION_OR_REVIEW", report.verdict)
        // T16d: the wiki emphasis left u14-st-teach-impersonal-2 (prep section 5), so the finding is gone (was assertTrue).
        assertFalse(report.signals.any { it.rule == "LEARNER_MARKUP" && "u14-st-teach-impersonal-2" in it.entityIds })
        // T16c: u13-i-tense-keep-2 now chooses between two Russian reports (prep section 1), so the finding is gone (was assertTrue).
        assertFalse(report.signals.any { it.rule == "NO_CYRILLIC_IN_REQUIRED_DISPLAY" && "u13-i-tense-keep-2" in it.entityIds })
        // New authoring standards do not silently change app startup or claim the course is good.
        assertTrue(V2Validator.validate(current).ok)
    }

    @Test fun learnerVoiceLintFindsProvenanceNarrationAndCheckTalk() {
        // T14-n2: Units 1-5 teach in plain English; attribution stays in the provenance records.
        assertTrue(V2DesignDiagnostics.audit(current).signals.none { it.rule == "LEARNER_PROVENANCE_NARRATION" && (it.unit ?: 0) in 1..5 })
        for (text in listOf("The dictionary gives this meaning: yes.", "Form glossed I: read.", "These examples are quoted exactly.",
                "When the check requests we, choose the form.", "the role check asks whether it is a subject", "Whither, where to")) {
            val narrated = current.copy(units = current.units.map { u -> if (u.number != 3) u else u.copy(topicSummary = text) })
            assertTrue(text, V2DesignDiagnostics.audit(narrated).signals.any { it.rule == "LEARNER_PROVENANCE_NARRATION" && it.unit == 3 })
        }
        val plain = current.copy(units = current.units.map { u -> if (u.number != 3) u else u.copy(topicSummary = "These words are masculine.") })
        assertFalse(V2DesignDiagnostics.audit(plain).signals.any { it.rule == "LEARNER_PROVENANCE_NARRATION" && it.unit == 3 })
    }

    @Test fun identicalRequiredDisplaysAreLocatedWithoutRewritingTheirKeys() {
        val a = question("a")
        val b = a.copy(id = "b")
        val report = audit(listOf(a, b))
        val signal = report.signals.single { it.rule == "IDENTICAL_REQUIRED_DISPLAY_TASK" }
        assertEquals(listOf("a", "b"), signal.entityIds)
        assertEquals(V2DesignSignalKind.REVIEW_SIGNAL, signal.kind)
        assertEquals(a.answer, b.answer)
    }

    @Test fun sameSurfaceFormsInDifferentGrammaticalQuestionsAreNotIdenticalTasks() {
        val a = current.item("u6-i-animacy-2")!!.copy(id = "a", objectiveId = objectiveId)
        // T15g (CC030): the live Unit 10 item moved to муж, so this fixture keeps the Unit 10 genitive question and gives
        // it the Unit 6 surface forms explicitly; the diagnostic must still separate the two grammatical questions.
        val live = current.item("u10-i-preposition-2")!!
        assertEquals("муж", live.promptLetter)
        assertEquals(listOf("му́жа", "муж", "му́жу"), live.options)
        val b = live.copy(id = "b", objectiveId = objectiveId, promptLetter = a.promptLetter, options = a.options, answer = a.answer)
        // Expected values are stated independently (T15a-n1): T15a added the frozen dative дру́гу to the Unit 6 item.
        assertEquals(listOf("дру́га", "друг", "дру́гу"), a.options)
        // T15e adds the frozen dative дру́гу (nouns.csv:14) to the Unit 10 item as its A2-24 third option.
        assertEquals(listOf("дру́га", "друг", "дру́гу"), b.options)
        assertEquals("друг", a.promptLetter)
        assertEquals(a.promptLetter, b.promptLetter)
        assertEquals("дру́га", a.answer)
        assertEquals(a.answer, b.answer)
        assertTrue(b.options.all { it in a.options })
        assertNotEquals(a.prompt, b.prompt)
        assertFalse(audit(listOf(a, b)).signals.any { it.rule == "IDENTICAL_REQUIRED_DISPLAY_TASK" })
    }

    @Test fun pluralAndGenitiveStressContrastsAreNotCollapsed() {
        val a = current.item("u8-i-plural-2")!!.copy(id = "a", objectiveId = objectiveId)
        val b = current.item("u10-i-preposition-1")!!.copy(id = "b", objectiveId = objectiveId, prompt = a.prompt)
        assertNotEquals(a.answer, b.answer) // о́кна versus окна́ is not optional decoration.
        assertFalse(audit(listOf(a, b)).signals.any { it.rule == "IDENTICAL_REQUIRED_DISPLAY_TASK" })
    }

    @Test fun caseSensitiveLetterCuesAreNotLowercasedIntoCollisions() {
        val a = question("a").copy(promptLetter = "А", options = listOf("а", "Б"), answer = "Б")
        assertFalse(audit(listOf(a)).signals.any { it.rule == "CUE_AS_DISTRACTOR" })
    }

    @Test fun anExactVisibleCueDistractorIsAReviewSignal() {
        val a = question("a")
        val signal = audit(listOf(a)).signals.single { it.rule == "CUE_AS_DISTRACTOR" }
        assertEquals(listOf("a"), signal.entityIds)
        assertEquals(V2DesignSignalKind.REVIEW_SIGNAL, signal.kind)
    }

    @Test fun hiddenProvenanceIsNeitherAVisibleCueNorRussianQuestionContent() {
        val a = question("a").copy(prompt = "Choose a label", promptLetter = "город",
            promptLetterRole = "ANSWER_PROVENANCE", options = listOf("place", "person"), answer = "place")
        val report = audit(listOf(a))
        assertFalse(report.signals.any { it.rule == "CUE_AS_DISTRACTOR" })
        assertTrue(report.signals.any { it.rule == "NO_CYRILLIC_IN_REQUIRED_DISPLAY" })
    }

    @Test fun nongatingOrientationIsNotTreatedAsRequiredRussianAssessment() {
        val a = question("a").copy(prompt = "Choose a study aid", promptLetter = null,
            options = listOf("binder", "cards"), answer = "binder")
        val report = audit(listOf(a), gating = false)
        assertEquals(0, report.counts["requiredChecks"])
        assertFalse(report.signals.any { it.rule in setOf("FEWER_THAN_THREE_OPTIONS", "NO_CYRILLIC_IN_REQUIRED_DISPLAY", "CONSTANT_REQUIRED_KEY") })
    }

    @Test fun twoOptionChecksAndConstantKeysNeedReviewRatherThanAutomaticCondemnation() {
        val a = question("a")
        val b = a.copy(id = "b", prompt = "Apply the form in another context")
        val report = audit(listOf(a, b))
        for (rule in listOf("FEWER_THAN_THREE_OPTIONS", "CONSTANT_REQUIRED_KEY")) {
            val matching = report.signals.filter { it.rule == rule }
            assertTrue(matching.isNotEmpty())
            assertTrue(matching.all { it.kind == V2DesignSignalKind.REVIEW_SIGNAL })
        }
        // A typed item has no options by design; the option-count review does not apply to it.
        val typed = a.copy(id = "t", kind = "TYPED_FORM", options = emptyList())
        assertFalse(audit(listOf(typed)).signals.any { it.rule == "FEWER_THAN_THREE_OPTIONS" })
        // English-to-Russian typing shows no Cyrillic but checks the Russian the learner types (T14-b).
        val toRussian = typed.copy(id = "r", prompt = "Type the word that has this meaning.", promptLetter = "book",
            answer = "книга", typedAnswerKey = V2TypedAnswerKey("k-r", 1, listOf("книга")))
        assertFalse(audit(listOf(toRussian)).signals.any { it.rule == "NO_CYRILLIC_IN_REQUIRED_DISPLAY" })
        val englishOnly = typed.copy(id = "e", prompt = "Type the word that has this meaning.", promptLetter = "book",
            answer = "book", typedAnswerKey = V2TypedAnswerKey("k-e", 1, listOf("book")))
        assertTrue(audit(listOf(englishOnly)).signals.any { it.rule == "NO_CYRILLIC_IN_REQUIRED_DISPLAY" })
    }

    @Test fun aWorkedExampleContainingTheAnswerIsNotAutomaticallyAnAnswerScript() {
        val a = question("a")
        val report = audit(listOf(a), teaching = "Plural forms follow the taught pattern. Worked example: города́.")
        assertFalse(report.signals.any { it.rule == "ANSWER_IN_INSTRUCTION_SIGNAL" })
    }

    @Test fun explicitAnswerSelectionScriptsHaveInstructionLocators() {
        val report = audit(listOf(question("a")), teaching = "When the check asks for a plural, choose города́.")
        val signal = report.signals.single { it.rule == "ANSWER_IN_INSTRUCTION_SIGNAL" }
        assertEquals(listOf("teach"), signal.relatedStepIds)
        assertEquals(V2DesignSignalKind.REVIEW_SIGNAL, signal.kind)
    }

    @Test fun lexicalAbsenceCanBeTransferAndIsNotProofOfUntaughtContent() {
        val report = audit(listOf(question("a")), teaching = "Apply the pattern to a new noun.", remediation = "Compare the endings using the rule.")
        for (rule in listOf("TEACHING_TRANSFER_OR_GAP", "REMEDIATION_TRANSFER_OR_GAP")) {
            val signal = report.signals.single { it.rule == rule }
            assertEquals(V2DesignSignalKind.REVIEW_SIGNAL, signal.kind)
        }
        assertEquals(0, report.structuralErrors)
    }

    @Test fun brokenDeclaredTeachingAndRemediationLinksAreStructuralErrors() {
        val p = scenario(listOf(question("a")))
        val u = p.units.single()
        val o = u.objectives.single().copy(instructionStepIds = listOf("missing-teach"), remediationStepIds = listOf("missing-repair"))
        val report = V2DesignDiagnostics.audit(p.copy(units = listOf(u.copy(objectives = listOf(o)))))
        for (rule in listOf("MISSING_INSTRUCTION_LINK", "MISSING_REMEDIATION_LINK")) {
            assertEquals(V2DesignSignalKind.STRUCTURAL_ERROR, report.signals.single { it.rule == rule }.kind)
        }
    }

    @Test fun requiredChecksBeforeTheirLinkedTeachingAreLocated() {
        val report = audit(listOf(question("a")), checkFirst = true)
        val signal = report.signals.single { it.rule == "CHECK_BEFORE_LINKED_TEACHING" }
        assertEquals(V2DesignSignalKind.STRUCTURAL_ERROR, signal.kind)
        assertEquals(listOf("check"), signal.relatedStepIds)
    }

    @Test fun sourceMarkupIsFlaggedOnlyWhenItReachesLearnerFields() {
        val marked = audit(listOf(question("a")), teaching = "Read '''this''' and [[case|the ending]].")
        assertTrue(marked.signals.any { it.rule == "LEARNER_MARKUP" && it.entityIds == listOf("teach") })
        // Inherited provenance and capability source spans still contain legitimate frozen markup.
        val ordinary = audit(listOf(question("a")), teaching = "A learner's example retains a single apostrophe.")
        assertFalse(ordinary.signals.any { it.rule == "LEARNER_MARKUP" })
    }

    @Test fun diagnosticsAreDeterministicAndDoNotCalculateCanonicalUnitDigests() {
        val p = scenario(listOf(question("a")))
        val before = V2Validator.unitDigestExecutionsForTests.get()
        val first = V2DesignDiagnostics.audit(p)
        assertEquals(first, V2DesignDiagnostics.audit(p))
        assertEquals(before, V2Validator.unitDigestExecutionsForTests.get())
        assertEquals(mapOf(0 to 1), first.authoredRequiredKeyPositions)
        assertTrue(first.limitations.any { it.contains("runtime permutation") })
    }

    @Test fun governanceTermsAndExactSourceIdsAreLocatedInLearnerTextOnly() {
        val base = scenario(listOf(question("english").copy(prompt = "Use the admitted form", promptLetter = null)))
        val source = base.sources.first().copy(id = "synthetic-source", title = "Frozen source metadata")
        val unit = base.units.single()
        val p = base.copy(sources = listOf(source), units = listOf(unit.copy(
            title = "Retained lesson", topicSummary = "review-pending summary",
            sessions = unit.sessions.map { session -> session.copy(steps = session.steps.map { step ->
                if (step.id == "teach") step.copy(body = "Read synthetic-source", dominantAction = "Open frozen explanation") else step
            }) })))
        val report = V2DesignDiagnostics.audit(p)
        for ((id, field) in listOf("english" to "prompt", unit.id to "title", unit.id to "topicSummary",
            "teach" to "body", "teach" to "dominantAction")) {
            assertTrue("$id / $field", report.signals.any { it.rule == "LEARNER_GOVERNANCE_TERM" && id in it.entityIds && it.field == field && it.unit == 3 })
        }
        assertFalse(report.signals.any { it.rule == "LEARNER_GOVERNANCE_TERM" && source.id in it.entityIds })
    }

    @Test fun allSelectedOptionRationalesAndRevealedTypedKeysAreLinted() {
        val key = V2TypedAnswerKey("fixture", 1, listOf("retained answer"))
        val a = question("choice").copy(prompt = "Use the rule", options = listOf("key", "wrong"), answer = "key",
            distractorRationales = listOf("", "This frozen source ID is wrong"))
        val b = a.copy(id = "typed", kind = "TYPED_FORM", options = emptyList(), distractorRationales = emptyList(),
            typedAnswerKey = key)
        val report = audit(listOf(a, b))
        assertTrue(report.signals.any { it.rule == "LEARNER_GOVERNANCE_TERM" && it.entityIds == listOf("choice") && it.field == "distractorRationales[1]" })
        assertTrue(report.signals.any { it.rule == "LEARNER_GOVERNANCE_TERM" && it.entityIds == listOf("typed") && it.field == "acceptedAnswers[0]" })
    }

    @Test fun rawHtmlEntitiesAndWikiMarkupAreReportedButOrdinaryPunctuationIsNot() {
        for (body in listOf("Use <em>this form</em>", "Read &eacute; here", "Read &#123; here", "Read [[noun|form]]", "[https://example.org noun]", "== Source heading ==")) {
            assertTrue(body, audit(listOf(question("a")), teaching = body).signals.any { it.rule == "LEARNER_MARKUP" && it.entityIds == listOf("teach") })
        }
        val ordinary = audit(listOf(question("a")), teaching = "A learner's result: 2 < 3 and 4 > 3; {count} forms.")
        assertFalse(ordinary.signals.any { it.rule == "LEARNER_MARKUP" })
    }

    @Test fun sourceIdSubstringsAndHiddenProvenanceAreNotLearnerLeaks() {
        val item = question("english").copy(prompt = "Use the pattern", promptLetter = "retained synthetic-source", promptLetterRole = "ANSWER_PROVENANCE")
        val p = scenario(listOf(item), teaching = "Read xsynthetic-source-y as an English engineering token.")
        val report = V2DesignDiagnostics.audit(p.copy(sources = listOf(p.sources.first().copy(id = "synthetic-source", title = "Frozen metadata"))))
        assertFalse(report.signals.any { it.rule == "LEARNER_GOVERNANCE_TERM" && it.entityIds.any { id -> id in setOf("english", "teach", "synthetic-source") } })
    }

    @Test fun reviewCopyAndLetterNotesAreAlsoLearnerText() {
        val p = scenario(listOf(question("a")))
        val report = V2DesignDiagnostics.audit(p.copy(reviewCopy = p.reviewCopy.copy(
            titles = mapOf(3 to "Frozen review"), messages = mapOf("example" to "Read <b>this</b>")),
            letters = p.letters.take(1).map { it.copy(englishNote = "Retained example") }))
        assertTrue(report.signals.any { it.rule == "LEARNER_GOVERNANCE_TERM" && it.entityIds == listOf("review-title-3") })
        assertTrue(report.signals.any { it.rule == "LEARNER_MARKUP" && it.entityIds == listOf("review-message-example") })
        assertTrue(report.signals.any { it.rule == "LEARNER_GOVERNANCE_TERM" && it.entityIds == listOf(p.letters.first().id) })
    }

    private fun question(id: String): V2CheckItem = current.item("u8-i-plural-1")!!.copy(
        id = id, objectiveId = objectiveId, prompt = "Choose the plural", promptLetter = "го́род",
        options = listOf("города́", "го́род"), answer = "города́",
        feedback = "Apply the noun rule.", distractorRationales = emptyList(),
    )

    private fun audit(items: List<V2CheckItem>, gating: Boolean = true,
                      teaching: String = "Compare го́род and города́ using the noun rule.",
                      remediation: String = "Review го́род and города́.", checkFirst: Boolean = false): V2DesignReport =
        V2DesignDiagnostics.audit(scenario(items, gating, teaching, remediation, checkFirst))

    private fun scenario(items: List<V2CheckItem>, gating: Boolean = true,
                         teaching: String = "Compare го́род and города́ using the noun rule.",
                         remediation: String = "Review го́род and города́.", checkFirst: Boolean = false): V2CurriculumPack {
        val teach = V2Step("teach", "TEACH", "Rule", teaching)
        val check = V2Step("check", "CHECK", "Check", "Apply the rule.", itemIds = items.map { it.id })
        val repair = V2Step("repair", "REMEDIATE", "Explanation", remediation)
        val o = V2Objective(objectiveId, 3, "Synthetic diagnostic fixture", "RECOGNITION",
            instructionStepIds = listOf("teach"), checkItemIds = items.map { it.id }, remediationStepIds = listOf("repair"), gating = gating)
        val session = V2Session("session", "Diagnostic fixture", 1, objectiveIds = listOf(objectiveId),
            steps = if (checkFirst) listOf(check, teach, repair) else listOf(teach, check, repair))
        val unit = current.unit(3)!!.copy(objectives = listOf(o), sessions = listOf(session))
        return current.copy(units = listOf(unit), items = items, examples = emptyList(), references = emptyList())
    }

    companion object {
        private const val objectiveId = "fixture-objective"
        private val root = File(if (File("app").isDirectory) "." else "..")
        private val current by lazy { V2PackLoader.parse(File(root, "app/src/main/assets/content/v2-curriculum.json").readText()) }
    }
}
