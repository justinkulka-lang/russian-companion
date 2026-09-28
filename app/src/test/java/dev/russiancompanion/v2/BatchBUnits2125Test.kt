package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.BatchBSources
import dev.russiancompanion.v2.curriculum.GovernedSourceAdmission
import dev.russiancompanion.v2.curriculum.V2CurriculumPack
import dev.russiancompanion.v2.curriculum.V2LearnerView
import dev.russiancompanion.v2.curriculum.V2PackLoader
import dev.russiancompanion.v2.curriculum.V2StartingPoint
import dev.russiancompanion.v2.curriculum.V2ProvenanceRecord
import dev.russiancompanion.v2.curriculum.V2SourceCapability
import dev.russiancompanion.v2.curriculum.V2Validator
import dev.russiancompanion.v2.curriculum.advancementDecision
import dev.russiancompanion.v2.curriculum.unit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.security.MessageDigest

/**
 * Batch B fail-closed checks for Units 21-25.
 * A co-occurring word, a neighboring gloss, or a bounded example is not a capability.
 */
class BatchBUnits2125Test {
    @Test
    fun admittedCapabilitiesMatchFrozenIdentityAndLeaveUnresolvedClaimsClosed() {
        val pack = pack()
        val errors = V2Validator.validate(pack).errors
        assertTrue(errors.joinToString("\n"), errors.isEmpty())
        val expected = listOf(
            "reflexive-object" to "obj-u21-object-reference",
            "purpose-clause" to "obj-u21-purpose",
            "declined-numeral" to "obj-u22-declension",
            "both-forms" to "obj-u22-both-forms",
            "one-and-a-half" to "obj-u22-half-forms",
            "collective-numeral" to "obj-u22-collective",
            "negation-alternation" to "obj-u24-alternation",
            "negative-pronoun" to "obj-u24-stand-ins",
            "diminutive-form-classification" to "obj-u25-nickname-rule",
            "register" to "obj-u25-address",
            "elapsed-time-duration" to "obj-u23-duration",
        )
        expected.forEach { (claim, objectiveId) ->
            val capability = pack.sourceCapabilities.single { it.supportedClaim == claim }
            assertEquals(objectiveId, capability.objectiveId)
            val record = pack.provenanceRecords.first {
                it.objectiveId == objectiveId && it.originalSourceText == capability.exactSpan
            }
            assertEquals(capability.sourceId, record.sourceId)
            assertEquals(capability.sourceRevision, record.sourceRevision)
            assertEquals(capability.sourceRecordId, record.sourceRecordId)
            assertEquals(capability.sourceHash, record.sourceHash)
            assertEquals(capability.sourceHash, sha256(record.originalSourceText))
            if (capability.sourceId == "src-pressbooks-diverse-russian-usa-grammar") {
                assertEquals("https://wisc.pb.unizin.org/diverserussian/chapter/usa-grammar/", record.sourceUrl)
                assertFalse(record.sourceUrl.contains("oldid="))
                assertTrue(record.attribution.contains(capability.sourceRevision))
                assertTrue(record.attribution.contains("Anna Tumarkin"))
                assertTrue(record.attribution.contains("Shannon Donnally Quinn"))
                assertFalse(record.attribution.contains("Wikipedia:Copyrights"))
                assertFalse(record.attribution.contains("Wiktionary:Copyrights"))
                assertFalse(record.attribution.contains("Wikibooks:Copyrights"))
                assertTrue(capability.exactSpan.contains("emphasizes the duration"))
            } else {
                assertTrue(record.sourceUrl.contains("oldid=${capability.sourceRevision}"))
            }
        }
        assertTrue(
            pack.sourceCapabilities.none {
                it.supportedClaim in setOf("completion-over-period", "negation-scope")
            },
        )
        val classification = pack.sourceCapabilities.single { it.supportedClaim == "diminutive-form-classification" }
        assertEquals("obj-u25-nickname-rule", classification.objectiveId)
        assertEquals(25, classification.unitNumber)
        assertFalse(BatchBSources.clearsProductiveDiminutive(classification.exactSpan))
        assertTrue(BatchBSources.boundedDiminutiveRecognition(classification.exactSpan))
        assertTrue(BatchBSources.admits(classification, BatchBSources.ADMITTED_DIMINUTIVE_FORM_CLASSIFICATION, "diminutive-form-classification"))
        assertFalse(BatchBSources.admits(classification, BatchBSources.ADMITTED_PRODUCTIVE_DIMINUTIVE, "productive-diminutive"))
        assertTrue(BatchBSources.ADMITTED_COMPLETION_OVER_PERIOD.isEmpty())
        assertTrue(BatchBSources.ADMITTED_NEGATION_SCOPE.isEmpty())
        assertTrue(BatchBSources.ADMITTED_PRODUCTIVE_DIMINUTIVE.isEmpty())
        assertTrue(V2Validator.unresolvedRequiredSourceCapabilities(pack, 23).any { it.claim == "completion-over-period" && it.objectiveId == "obj-u23-completion" })
        assertTrue(V2Validator.unresolvedRequiredSourceCapabilities(pack, 7).any { it.claim == "adverb-paradigm" })
        assertTrue(V2Validator.unresolvedRequiredSourceCapabilities(pack, 17).any { it.claim == "productive-clock-time" })
        assertTrue(V2Validator.unresolvedRequiredSourceCapabilities(pack, 24).any { it.claim == "negation-scope" && it.objectiveId == "obj-u24-placement" })
        assertTrue(V2Validator.unresolvedRequiredSourceCapabilities(pack, 25).any { it.claim == "productive-diminutive" && it.objectiveId == "obj-u25-nickname-rule" })
        assertTrue(V2Validator.unresolvedRequiredSourceCapabilities(pack, 25).none { it.claim == "diminutive-form-classification" })
        listOf(21, 22).forEach { number ->
            assertEquals("CONTENT_COMPLETE_REVIEW_PENDING", pack.unit(number)!!.readiness)
            assertEquals("DEVELOPMENT", pack.unit(number)!!.installState)
        }
        listOf(23, 24, 25).forEach { number ->
            assertEquals("NOT_PREPARED", pack.unit(number)!!.readiness)
            assertEquals("DEVELOPMENT", pack.unit(number)!!.installState)
        }
        assertTrue(pack.unitReviews.filter { it.unitNumber in 21..25 }.all {
            it.status == "PENDING_QUALIFIED_REVIEW" && it.reviewer.isNullOrBlank()
        })
    }

    @Test
    fun governedPairsRejectCoOccurrenceAndASubstitutedGloss() {
        val pack = pack()
        val self = shipped(pack, "obj-u21-object-reference", "\u042f \u043b\u044e\u0431\u043b\u044e\u0301 \u0441\u0435\u0431\u044f\u0301")
        val myself = shipped(pack, "obj-u21-object-reference", "I love myself")
        val wife = shipped(pack, "obj-u21-object-reference", "I love my wife")
        assertEquals("EXPLICIT_SOURCE_PAIR", V2Validator.classifyRelation(self, myself, listOf(wife)))
        assertNull(V2Validator.classifyRelation(self, wife, listOf(myself)))

        val purpose = "obj-u21-purpose"
        val soThat = shipped(pack, purpose, "so that")
        val ifGloss = shipped(pack, purpose, "if")
        val because = shipped(pack, purpose, "because")
        val chtoby = shipped(pack, purpose, "\u0447\u0442\u043e\u0431\u044b")
        assertEquals("EXPLICIT_SOURCE_PAIR", V2Validator.classifyRelation(chtoby, soThat, listOf(ifGloss, because)))
        assertNull(V2Validator.classifyRelation(chtoby, ifGloss, listOf(soThat, because)))
        assertNull(V2Validator.classifyRelation(chtoby, because, listOf(soThat)))

        val collective = "obj-u22-collective"
        val two = shipped(pack, collective, "\u0434\u0432\u0430")
        val collectiveTwo = shipped(pack, collective, "\u0434\u0432\u043e\u0301\u0435")
        val ordinalTwo = shipped(pack, collective, "\u0432\u0442\u043e\u0440\u043e\u0301\u0439")
        assertEquals("EXPLICIT_SOURCE_PAIR", V2Validator.classifyRelation(two, collectiveTwo, listOf(ordinalTwo)))
        assertNull(V2Validator.classifyRelation(two, ordinalTwo, listOf(collectiveTwo)))

        val ranges = "obj-u23-ranges"
        val sixties = shipped(pack, ranges, "\u0412 \u0448\u0435\u0441\u0442\u0438\u0434\u0435\u0441\u044f\u0442\u044b\u0445 \u0433\u043e\u0434\u0430\u0445")
        val in1960 = shipped(pack, ranges, "in the 1960s")
        val in1990 = shipped(pack, ranges, "in the 1990s")
        assertEquals("EXPLICIT_SOURCE_PAIR", V2Validator.classifyRelation(sixties, in1960, listOf(in1990)))
        assertNull(V2Validator.classifyRelation(sixties, in1990, listOf(in1960)))

        val item = pack.items.single { it.id == "u21-g-purpose-0" }
        val distractor = item.options.first { it != item.answer }
        val substituted = V2Validator.validate(replaceItem(pack, item.copy(answer = distractor))).errors.joinToString("\n")
        assertTrue(substituted, substituted.contains("${item.id} answer has no contrast provenance"))
        assertTrue(substituted, substituted.contains("${item.id} explanation does not name the keyed answer"))
        val assessment = item.assessment!!
        val swappedWhy = assessment.whyAnswerDemonstratesObjective.replace(item.answer, distractor)
        val swappedFeedback = item.feedback.replace(item.answer, distractor)
        val mutated = V2Validator.validate(
            replaceItem(
                pack,
                item.copy(
                    feedback = swappedFeedback,
                    assessment = assessment.copy(whyAnswerDemonstratesObjective = swappedWhy),
                ),
            ),
        ).errors.joinToString("\n")
        assertTrue(mutated, mutated.contains("${item.id} explanation does not name the keyed answer"))
        assertTrue(mutated, mutated.contains("${item.id} explanation names a different gloss"))
    }

    @Test
    fun reflexiveSentencePairRejectsIsolatedTokenAndWifeCitation() {
        val pack = pack()
        val self = shipped(pack, "obj-u21-object-reference", BatchBSources.SELF_SENTENCE)
        val myself = shipped(pack, "obj-u21-object-reference", "I love myself")
        val wife = shipped(pack, "obj-u21-object-reference", BatchBSources.WIFE_SENTENCE)
        val wifeGloss = shipped(pack, "obj-u21-object-reference", "I love my wife")
        assertEquals("EXPLICIT_SOURCE_PAIR", V2Validator.classifyRelation(self, myself, listOf(wifeGloss)))
        assertEquals("EXPLICIT_SOURCE_PAIR", V2Validator.classifyRelation(wife, wifeGloss, listOf(myself)))
        assertNull(V2Validator.classifyRelation(self, wifeGloss, listOf(myself)))
        assertNull(V2Validator.classifyRelation(wife, myself, listOf(wifeGloss)))
        val isolated = self.copy(itemId = "probe-isolated-reflexive", shippedText = BatchBSources.ISOLATED_REFLEXIVE)
        assertNull(V2Validator.classifyRelation(isolated, myself, listOf(wifeGloss)))
        assertTrue(pack.items.none { it.promptLetter == BatchBSources.ISOLATED_REFLEXIVE })

        val selfItem = pack.items.single {
            it.objectiveId == "obj-u21-object-reference" &&
                it.promptLetter == BatchBSources.SELF_SENTENCE &&
                it.assessment?.independent == true
        }
        val assessment = checkNotNull(selfItem.assessment)
        val why = assessment.whyAnswerDemonstratesObjective
        assertTrue(why, why.contains(BatchBSources.SELF_LINE_CITE))
        assertFalse(why, why.contains(BatchBSources.WIFE_LINE_CITE))
        val wifeCited = why.replace(BatchBSources.SELF_LINE_CITE, BatchBSources.WIFE_LINE_CITE)
        val feedback = selfItem.feedback.replace(BatchBSources.SELF_LINE_CITE, BatchBSources.WIFE_LINE_CITE)
        val swapped = V2Validator.validate(
            replaceItem(
                pack,
                selfItem.copy(
                    feedback = feedback,
                    assessment = assessment.copy(whyAnswerDemonstratesObjective = wifeCited),
                ),
            ),
        ).errors.joinToString("\n")
        assertTrue(swapped, swapped.contains("wife-line citation does not validate the self relationship"))
        assertTrue(swapped, swapped.contains("self relationship citation is not the self line"))
    }

    @Test
    fun independentChecksRejectSourceLayoutRecall() {
        val pack = pack()
        val objectives = listOf(
            "obj-u21-object-reference",
            "obj-u21-purpose",
            "obj-u22-declension",
            "obj-u22-both-forms",
            "obj-u22-half-forms",
            "obj-u22-collective",
            "obj-u23-ranges",
            "obj-u23-beginning",
            "obj-u23-during-event",
            "obj-u23-duration",
            "obj-u23-elapsed",
            "obj-u24-alternation",
            "obj-u24-stand-ins",
            "obj-u24-concord",
            "obj-u24-word-order-contrast",
            "obj-u28-imperfective-adverb",
            "obj-u28-perfective-adverb",
        )
        val rephrasings = listOf(
            "Which admitted gloss is written after the equals sign?",
            "Which admitted gloss is written for the shown form?",
            "Which admitted gloss is written on the frozen line?",
            "Which admitted form is written in the column?",
            "Which admitted form is written beside the tag?",
        )
        objectives.forEach { objectiveId ->
            val independent = pack.items.filter {
                it.objectiveId == objectiveId && it.assessment?.independent == true
            }
            assertTrue(objectiveId, independent.size >= 2)
            independent.forEach { item ->
                assertFalse(item.id + " " + item.prompt, sourceLayout(item.prompt))
                assertFalse(item.id, item.prompt.contains("g=f") || item.prompt.contains("g=m"))
            }
            val donor = independent.first()
            rephrasings.forEach { prompt ->
                val errors = V2Validator.validate(replaceItem(pack, donor.copy(prompt = prompt))).errors.joinToString("\n")
                assertTrue("$objectiveId $prompt\n$errors", errors.contains("${donor.id} tests line-label recall"))
            }
        }
        val layout = "Which admitted gloss is written after the equals sign?"
        val blocked = pack.copy(
            items = pack.items.map { item ->
                if (item.objectiveId == "obj-u21-object-reference" && item.assessment?.independent == true) {
                    item.copy(prompt = layout)
                } else {
                    item
                }
            },
        )
        val blockedErrors = V2Validator.validate(blocked).errors.joinToString("\n")
        assertTrue(blockedErrors, blockedErrors.contains("tests line-label recall"))
        assertTrue(blockedErrors, blockedErrors.contains("cap-u21-reflexive-object has no reviewable instruction and check chain"))
        val learner = pack.items.filter { it.objectiveId == "obj-u22-half-forms" }.flatMap { item ->
            listOf(item.prompt, item.promptLetter.orEmpty(), item.answer, item.feedback) + item.options
        }
        assertTrue(learner.none { it.contains("g=f") || it.contains("g=m") })
    }

    @Test
    fun wrongSourceRevisionUnitAndObjectiveFail() {
        val pack = pack()
        val capability = pack.sourceCapabilities.single { it.supportedClaim == "one-and-a-half" }
        listOf(
            capability.copy(sourceId = "src-wikibooks-russian-cc-by-sa"),
            capability.copy(sourceRevision = "9999999"),
            capability.copy(sourceHash = "0".repeat(64)),
            capability.copy(exactSpan = "1.5 roubles"),
            capability.copy(unitNumber = 21, objectiveId = "obj-u21-purpose", supportedClaim = "purpose-clause"),
            capability.copy(objectiveId = "obj-u22-declension", supportedClaim = "declined-numeral"),
        ).forEach { mutated ->
            val errors = V2Validator.validate(replaceCapability(pack, capability.id, mutated)).errors.joinToString("\n")
            assertTrue(errors, errors.contains(capability.id))
            assertFalse(V2Validator.validate(replaceCapability(pack, capability.id, mutated)).ok)
        }
    }

    @Test
    fun boundedEvidenceCannotClearUnresolvedCapabilities() {
        val pack = pack()
        val svoj = overreach(
            pack,
            "\u0441\u0432\u043e\u044e \u0436\u0435\u043d\u0443",
            21,
            "obj-u21-object-reference",
            "reflexive-object",
            "cap-svoj-only",
        )
        assertTrue(svoj, svoj.contains("no independent reflexive-object admission"))
        assertTrue(svoj, svoj.contains("\u0441\u0432\u043e\u0439 evidence does not clear reflexive-object"))

        val study = overreach(
            pack,
            BatchBSources.STUDY_SPAN,
            21,
            "obj-u21-purpose",
            "purpose-clause",
            "cap-study-span",
        )
        assertTrue(study, study.contains("no independent purpose-clause admission"))
        assertTrue(study, study.contains("a sentence containing \u0447\u0442\u043e\u0431\u044b does not clear purpose-clause"))

        val bareHalf = overreach(
            pack,
            "\u043f\u043e\u043b\u0442\u043e\u0440\u0430\u0301 \u0440\u0443\u0431\u043b\u044f\u0301",
            22,
            "obj-u22-half-forms",
            "one-and-a-half",
            "cap-half-without-note",
        )
        assertTrue(bareHalf, bareHalf.contains("no independent one-and-a-half admission"))
        assertTrue(bareHalf, bareHalf.contains("\u043f\u043e\u043b\u0442\u043e\u0440\u0430 without the usage note does not clear one-and-a-half"))

        val cardinal = overreach(
            pack,
            "cardinal numbers \u0434\u0432\u0430",
            22,
            "obj-u22-collective",
            "collective-numeral",
            "cap-cardinal-list",
        )
        assertTrue(cardinal, cardinal.contains("no independent collective-numeral admission"))
        assertTrue(cardinal, cardinal.contains("a cardinal list does not clear collective-numeral"))

        val ago = overreach(
            pack,
            BatchBSources.AGO_SPAN,
            23,
            "obj-u23-duration",
            "elapsed-time-duration",
            "cap-ago-as-duration",
        )
        assertTrue(ago, ago.contains("no independent elapsed-time-duration admission"))
        assertTrue(ago, ago.contains("bounded time expression does not clear elapsed-time-duration"))
        assertTrue(ago, ago.contains("partial duration evidence does not clear elapsed-time-duration"))

        val partial = overreach(
            pack,
            "\u0412 \u0442\u0435\u0447\u0435\u043d\u0438\u0435 \u0443\u0440\u043e\u043a\u0430 \u0443\u0447\u0435\u043d\u0438\u043a\u0438 \u0440\u0430\u0431\u043e\u0442\u0430\u043b\u0438 \u043d\u0430\u0434 \u043f\u0440\u043e\u0435\u043a\u0442\u043e\u043c.",
            23,
            "obj-u23-duration",
            "elapsed-time-duration",
            "cap-partial-duration",
        )
        assertTrue(partial, partial.contains("no independent elapsed-time-duration admission"))
        assertTrue(partial, partial.contains("partial duration evidence does not clear elapsed-time-duration"))
        assertFalse(partial, partial.contains("bounded time expression does not clear elapsed-time-duration"))

        val isolated = overreach(
            pack,
            "\u0434\u0443\u043c\u0430\u044f",
            28,
            "obj-u28-imperfective-adverb",
            "imperfective-verbal-adverb",
            "cap-isolated-adverb",
        )
        assertTrue(isolated, isolated.contains("an isolated adverbial example does not state the present-versus-past relationship"))
        assertTrue(isolated, isolated.contains("is not an admitted frozen source"))

        val concord = overreach(
            pack,
            BatchBSources.CONCORD_SPAN,
            24,
            "obj-u24-placement",
            "negation-scope",
            "cap-concord-as-scope",
        )
        assertTrue(concord, concord.contains("no independent negation-scope admission"))
        assertTrue(concord, concord.contains("negative concord does not clear negation-scope"))

        val compare = overreach(
            pack,
            BatchBSources.NEGATION_SPAN,
            24,
            "obj-u24-placement",
            "negation-scope",
            "cap-compare-as-scope",
        )
        assertTrue(compare, compare.contains("no independent negation-scope admission"))
        assertTrue(compare, compare.contains("the genitive and accusative compare does not clear negation-scope"))

        val diminutive = overreach(
            pack,
            BatchBSources.ADDRESS_SPAN,
            25,
            "obj-u25-nickname-rule",
            "productive-diminutive",
            "cap-listed-diminutive",
        )
        assertTrue(diminutive, diminutive.contains("no independent productive-diminutive admission"))
        assertTrue(diminutive, diminutive.contains("bounded diminutive recognition does not clear productive-diminutive"))

        val durationSpan = BatchBSources.ADMITTED_ELAPSED_TIME.single().exactSpan
        val duration = overreach(
            pack,
            durationSpan,
            23,
            "obj-u23-completion",
            "completion-over-period",
            "cap-duration-as-completion",
        )
        assertTrue(duration, duration.contains("no independent completion-over-period admission"))
        assertTrue(duration, duration.contains("duration evidence does not clear completion-over-period"))
        assertFalse(duration, duration.contains("ago, during, or range evidence does not clear completion-over-period"))

        val agoAsCompletion = overreach(
            pack,
            BatchBSources.AGO_SPAN,
            23,
            "obj-u23-completion",
            "completion-over-period",
            "cap-ago-as-completion",
        )
        assertTrue(agoAsCompletion, agoAsCompletion.contains("no independent completion-over-period admission"))
        assertTrue(agoAsCompletion, agoAsCompletion.contains("ago, during, or range evidence does not clear completion-over-period"))

        val duringSpan = pack.provenanceRecords.first { it.objectiveId == "obj-u23-during-event" }.originalSourceText
        val during = overreach(
            pack,
            duringSpan,
            23,
            "obj-u23-completion",
            "completion-over-period",
            "cap-during-as-completion",
        )
        assertTrue(during, during.contains("ago, during, or range evidence does not clear completion-over-period"))

        val rangeSpan = pack.provenanceRecords.first { it.objectiveId == "obj-u23-ranges" }.originalSourceText
        val range = overreach(
            pack,
            rangeSpan,
            23,
            "obj-u23-completion",
            "completion-over-period",
            "cap-range-as-completion",
        )
        assertTrue(range, range.contains("ago, during, or range evidence does not clear completion-over-period"))

        val keyword = overreach(
            pack,
            "completion over a period during ago",
            23,
            "obj-u23-completion",
            "completion-over-period",
            "cap-keyword-completion",
        )
        assertTrue(keyword, keyword.contains("no independent completion-over-period admission"))
        assertTrue(keyword, keyword.contains("keyword or co-occurrence evidence does not clear completion-over-period"))
        assertTrue(keyword, keyword.contains("has no reviewable instruction and check chain"))
    }

    @Test
    fun completionOverPeriodClearsOnlyForACompleteMatchingAdmission() {
        val pack = pack()
        val span = "A frozen source states the completion-over-period distinction."
        val admission = dev.russiancompanion.v2.curriculum.GovernedSourceAdmission(
            sourceId = "src-pressbooks-diverse-russian-usa-grammar",
            sourceRevision = "87815b8724cb11aecba5bb6d8b6438f783408f2354e2560a3c0fe026fac1e171",
            sourceRecordId = "usa-grammar:completion-over-period",
            sourceHash = sha256(span),
            exactSpan = span,
            unitNumber = 23,
            objectiveId = "obj-u23-completion",
            governedClaim = "completion-over-period",
        )
        val matching = dev.russiancompanion.v2.curriculum.V2SourceCapability(
            id = "cap-matching-completion",
            sourceId = admission.sourceId,
            sourceRevision = admission.sourceRevision,
            sourceRecordId = admission.sourceRecordId,
            sourceHash = admission.sourceHash,
            exactSpan = admission.exactSpan,
            unitNumber = admission.unitNumber,
            objectiveId = admission.objectiveId,
            supportedClaim = admission.governedClaim,
        )
        assertTrue(BatchBSources.admits(matching, setOf(admission), "completion-over-period"))
        assertFalse(BatchBSources.admits(matching.copy(sourceId = "src-openrussian-50e210c4"), setOf(admission), "completion-over-period"))
        assertFalse(BatchBSources.admits(matching.copy(sourceRevision = "1"), setOf(admission), "completion-over-period"))
        assertFalse(BatchBSources.admits(matching.copy(sourceRecordId = "other"), setOf(admission), "completion-over-period"))
        assertFalse(BatchBSources.admits(matching.copy(sourceHash = "0".repeat(64)), setOf(admission), "completion-over-period"))
        assertFalse(BatchBSources.admits(matching.copy(exactSpan = BatchBSources.ADMITTED_ELAPSED_TIME.single().exactSpan), setOf(admission), "completion-over-period"))
        assertFalse(BatchBSources.admits(matching.copy(unitNumber = 24), setOf(admission), "completion-over-period"))
        assertFalse(BatchBSources.admits(matching.copy(objectiveId = "obj-u23-duration"), setOf(admission), "completion-over-period"))
        assertFalse(BatchBSources.admits(matching, setOf(admission), "elapsed-time-duration"))
        assertFalse(BatchBSources.admits(matching, BatchBSources.ADMITTED_COMPLETION_OVER_PERIOD, "completion-over-period"))
        val claimed = pack.copy(sourceCapabilities = pack.sourceCapabilities + matching)
        assertTrue(V2Validator.unresolvedRequiredSourceCapabilities(claimed, 23).any { it.claim == "completion-over-period" })
        val durationHost = pack.provenanceRecords.first { it.originalSourceText == BatchBSources.ADMITTED_ELAPSED_TIME.single().exactSpan }
        val wrong = matching.copy(
            sourceId = durationHost.sourceId,
            sourceRevision = durationHost.sourceRevision,
            sourceRecordId = durationHost.sourceRecordId,
            sourceHash = durationHost.sourceHash,
            exactSpan = durationHost.originalSourceText,
        )
        val rebound = pack.copy(
            provenanceRecords = pack.provenanceRecords.map { record ->
                if (record.itemId == durationHost.itemId) record.copy(objectiveId = "obj-u23-completion") else record
            },
            sourceCapabilities = pack.sourceCapabilities + wrong,
        )
        assertTrue(V2Validator.unresolvedRequiredSourceCapabilities(rebound, 23).any { it.claim == "completion-over-period" })
        assertTrue(V2Validator.contentCompletenessErrors(rebound, rebound.unit(23)!!).any {
            it.contains("without an admitted completion-over-period source capability")
        })
    }

    @Test
    fun wordOrderContrastDoesNotClearNegationScope() {
        val pack = pack()
        assertTrue(pack.sourceCapabilities.none { it.supportedClaim == "negation-scope" })
        assertTrue(BatchBSources.ADMITTED_NEGATION_SCOPE.isEmpty())
        val contrast = pack.unit(24)!!.objectives.first { it.id == "obj-u24-word-order-contrast" }
        assertEquals("RECOGNITION", contrast.kind)
        assertTrue(contrast.checkItemIds.size >= 2)
        assertTrue(pack.items.filter { it.objectiveId == contrast.id }.all { item ->
            !item.prompt.contains("negation scope") && !item.prompt.contains("movement rule")
        })
        assertEquals("GUIDANCE", pack.unit(24)!!.objectives.first { it.id == "obj-u24-placement" }.kind)
        assertEquals("NOT_PREPARED", pack.unit(24)!!.readiness)
        assertEquals("DEVELOPMENT", pack.unit(24)!!.installState)
        assertTrue(V2Validator.unresolvedRequiredSourceCapabilities(pack, 24).any { it.claim == "negation-scope" })

        val frozen = overreach(
            pack,
            BatchBSources.SCOPE_SPAN,
            24,
            "obj-u24-placement",
            "negation-scope",
            "cap-frozen-order-as-scope",
        )
        assertTrue(frozen, frozen.contains("word-order evidence does not clear negation-scope"))
        assertTrue(frozen, frozen.contains("no independent negation-scope admission"))
        val frozenHost = pack.provenanceRecords.first { it.originalSourceText == BatchBSources.SCOPE_SPAN }
        assertEquals(sha256(BatchBSources.SCOPE_SPAN), frozenHost.sourceHash)
        val identity = dev.russiancompanion.v2.curriculum.GovernedSourceAdmission(
            sourceId = frozenHost.sourceId,
            sourceRevision = frozenHost.sourceRevision,
            sourceRecordId = frozenHost.sourceRecordId,
            sourceHash = frozenHost.sourceHash,
            exactSpan = BatchBSources.SCOPE_SPAN,
            unitNumber = 24,
            objectiveId = "obj-u24-placement",
            governedClaim = "negation-scope",
        )
        val identityCapability = V2SourceCapability(
            id = "cap-identity-as-scope",
            sourceId = identity.sourceId,
            sourceRevision = identity.sourceRevision,
            sourceRecordId = identity.sourceRecordId,
            sourceHash = identity.sourceHash,
            exactSpan = identity.exactSpan,
            unitNumber = 24,
            objectiveId = "obj-u24-placement",
            supportedClaim = "negation-scope",
        )
        assertFalse(BatchBSources.clearsNegationScope(identityCapability, setOf(identity)))

        val moved = "\u041d\u0435 \u043d\u0430\u0434\u043e \u043c\u0435\u043d\u044f \u0443\u0433\u043e\u0432\u0430\u0440\u0438\u0432\u0430\u0442\u044c. \u041c\u0435\u043d\u044f \u043d\u0435 \u043d\u0430\u0434\u043e \u0443\u0433\u043e\u0432\u0430\u0440\u0438\u0432\u0430\u0442\u044c."
        val movedErrors = overreach(pack, moved, 24, "obj-u24-placement", "negation-scope", "cap-moved-object")
        assertTrue(movedErrors, movedErrors.contains("word-order evidence does not clear negation-scope"))
        assertTrue(BatchBSources.wordOrderMeaningContrast(moved))

        val generic = overreach(
            pack,
            "alternative word order can change the meaning entirely",
            24,
            "obj-u24-placement",
            "negation-scope",
            "cap-generic-order",
        )
        assertTrue(generic, generic.contains("word-order evidence does not clear negation-scope"))

        val glosses = overreach(
            pack,
            "\u041d\u0435 \u043d\u0430\u0434\u043e \u043c\u0435\u043d\u044f \u0443\u0433\u043e\u0432\u0430\u0440\u0438\u0432\u0430\u0442\u044c. One should not persuade me. \u041c\u0435\u043d\u044f \u043d\u0435 \u043d\u0430\u0434\u043e \u0443\u0433\u043e\u0432\u0430\u0440\u0438\u0432\u0430\u0442\u044c. There is no need to persuade me.",
            24,
            "obj-u24-placement",
            "negation-scope",
            "cap-gloss-recognition",
        )
        assertTrue(glosses, glosses.contains("word-order evidence does not clear negation-scope"))

        val emphasis = overreach(
            pack,
            "Word order can express logical stress, and degree of definiteness.",
            24,
            "obj-u24-placement",
            "negation-scope",
            "cap-emphasis-as-scope",
        )
        assertTrue(emphasis, emphasis.contains("no independent negation-scope admission"))
        assertTrue(emphasis, emphasis.contains("word-order emphasis does not clear negation-scope"))

        val keyword = overreach(
            pack,
            "negation scope placement co-occurrence",
            24,
            "obj-u24-placement",
            "negation-scope",
            "cap-scope-keyword",
        )
        assertTrue(keyword, keyword.contains("keyword or co-occurrence evidence does not clear negation-scope"))
        assertTrue(keyword, keyword.contains("has no reviewable instruction and check chain"))

        val book = overreach(
            pack,
            "Self-attest only after the Lesson 24 work in your copy.",
            24,
            "obj-u24-placement",
            "negation-scope",
            "cap-book-as-scope",
        )
        assertTrue(book, book.contains("book self-attestation does not clear negation-scope"))
        assertFalse(book, book.contains("book self-attestation clears negation-scope"))

        val durationAsScope = overreach(
            pack,
            BatchBSources.ADMITTED_ELAPSED_TIME.single().exactSpan,
            24,
            "obj-u24-placement",
            "negation-scope",
            "cap-duration-as-scope",
        )
        assertTrue(durationAsScope, durationAsScope.contains("duration evidence does not clear negation-scope"))
        assertTrue(durationAsScope, durationAsScope.contains("no independent negation-scope admission"))

        val scopeAsCompletion = overreach(
            pack,
            BatchBSources.SCOPE_SPAN,
            23,
            "obj-u23-completion",
            "completion-over-period",
            "cap-scope-as-completion",
        )
        assertTrue(scopeAsCompletion, scopeAsCompletion.contains("word-order evidence does not clear completion-over-period"))
        assertTrue(scopeAsCompletion, scopeAsCompletion.contains("no independent completion-over-period admission"))

        listOf(
            Triple(7, "obj-u7-adverb", "adverb-paradigm"),
            Triple(17, "obj-u17-clock-answers", "productive-clock-time"),
            Triple(25, "obj-u25-nickname-rule", "productive-diminutive"),
        ).forEach { (unitNumber, objectiveId, claim) ->
            val transferred = overreach(pack, BatchBSources.SCOPE_SPAN, unitNumber, objectiveId, claim, "cap-scope-as-$claim")
            assertTrue(transferred, transferred.contains("word-order evidence does not clear $claim"))
            assertTrue(transferred, transferred.contains("no independent $claim admission"))
        }
        listOf(
            Triple(7, "obj-u7-adverb", "adverb-paradigm"),
            Triple(17, "obj-u17-clock-answers", "productive-clock-time"),
        ).forEach { (unitNumber, objectiveId, claim) ->
            assertTrue(V2Validator.unresolvedRequiredSourceCapabilities(
                pack.copy(sourceCapabilities = pack.sourceCapabilities + identityCapability.copy(id = "cap-moved-$claim", unitNumber = unitNumber, objectiveId = objectiveId, supportedClaim = claim)),
                unitNumber,
            ).any { it.claim == claim })
        }
        val withoutDiminutive = pack.copy(
            sourceCapabilities = pack.sourceCapabilities.filter { it.supportedClaim != "productive-diminutive" },
        )
        assertTrue(V2Validator.unresolvedRequiredSourceCapabilities(
            withoutDiminutive.copy(
                sourceCapabilities = withoutDiminutive.sourceCapabilities + identityCapability.copy(
                    id = "cap-moved-productive-diminutive",
                    unitNumber = 25,
                    objectiveId = "obj-u25-nickname-rule",
                    supportedClaim = "productive-diminutive",
                ),
            ),
            25,
        ).any { it.claim == "productive-diminutive" })

        val intended = overreach(
            pack,
            "How long something takes vs. For how long. The intended length of a trip uses \u041d\u0430 \u0441\u043a\u043e\u043b\u044c\u043a\u043e \u0432\u0440\u0435\u043c\u0435\u043d\u0438.",
            23,
            "obj-u23-completion",
            "completion-over-period",
            "cap-intended-length",
        )
        assertTrue(intended, intended.contains("intended-length evidence does not clear completion-over-period"))
        assertTrue(intended, intended.contains("no independent completion-over-period admission"))

        val paradigmKeyword = overreach(pack, "adverb paradigm co-occurrence", 7, "obj-u7-adverb", "adverb-paradigm", "cap-adverb-keyword")
        assertTrue(paradigmKeyword, paradigmKeyword.contains("keyword or co-occurrence evidence does not clear adverb-paradigm"))
        val clockKeyword = overreach(pack, "productive clock co-occurrence", 17, "obj-u17-clock-answers", "productive-clock-time", "cap-clock-keyword")
        assertTrue(clockKeyword, clockKeyword.contains("keyword or co-occurrence evidence does not clear productive-clock-time"))
        val diminutiveKeyword = overreach(pack, "productive diminutive co-occurrence", 25, "obj-u25-nickname-rule", "productive-diminutive", "cap-diminutive-keyword")
        assertTrue(diminutiveKeyword, diminutiveKeyword.contains("keyword or co-occurrence evidence does not clear productive-diminutive"))
        assertTrue(diminutiveKeyword, diminutiveKeyword.contains("bounded diminutive recognition does not clear productive-diminutive"))

        listOf(
            Triple(7, "obj-u7-adverb", "adverb-paradigm"),
            Triple(17, "obj-u17-clock-answers", "productive-clock-time"),
            Triple(23, "obj-u23-completion", "completion-over-period"),
            Triple(24, "obj-u24-placement", "negation-scope"),
            Triple(25, "obj-u25-nickname-rule", "productive-diminutive"),
        ).forEach { (unitNumber, objectiveId, claim) ->
            val row = V2SourceCapability(
                id = "cap-dict-$claim",
                sourceId = "src-openrussian-50e210c4",
                sourceRevision = "50e210c4803237779cb562bc1abcea529066031c",
                sourceRecordId = "dictionary-row",
                sourceHash = "ab".repeat(32),
                exactSpan = "dictionary row",
                unitNumber = unitNumber,
                objectiveId = objectiveId,
                supportedClaim = claim,
            )
            val errors = V2Validator.validate(pack.copy(sourceCapabilities = pack.sourceCapabilities + row)).errors.joinToString("\n")
            assertTrue(errors, errors.contains("a dictionary row does not clear $claim"))
            val attested = row.copy(id = "cap-book-$claim", sourceId = frozenHost.sourceId, exactSpan = "Self-attest only after the Lesson work in your copy.")
            val bookErrors = V2Validator.validate(pack.copy(sourceCapabilities = pack.sourceCapabilities + attested)).errors.joinToString("\n")
            assertTrue(bookErrors, bookErrors.contains("book self-attestation does not clear $claim"))
        }

        val explicit = "Scope-sensitive placement of the particle distinguishes which phrase is negated."
        val explicitCapability = identityCapability.copy(
            id = "cap-future-scope",
            sourceRecordId = "future:scope",
            sourceHash = sha256(explicit),
            exactSpan = explicit,
        )
        val explicitAdmission = identity.copy(
            sourceRecordId = explicitCapability.sourceRecordId,
            sourceHash = explicitCapability.sourceHash,
            exactSpan = explicit,
        )
        assertFalse(BatchBSources.wordOrderMeaningContrast(explicit))
        assertFalse(BatchBSources.clearsNegationScope(explicitCapability))
        assertTrue(BatchBSources.clearsNegationScope(explicitCapability, setOf(explicitAdmission)))
        val liveExplicit = overreach(pack, explicit, 24, "obj-u24-placement", "negation-scope", "cap-future-live")
        assertTrue(liveExplicit, liveExplicit.contains("no independent negation-scope admission"))
        assertFalse(liveExplicit.contains("word-order evidence does not clear negation-scope"))

        val stamped = pack.copy(
            provenanceRecords = pack.provenanceRecords.map { it.copy(reviewStatus = "APPROVED") },
            admissions = pack.admissions.copy(penguinProseAssets = 4, generatedRussianWords = 0),
        )
        assertTrue(V2Validator.unresolvedRequiredSourceCapabilities(stamped, 7).any { it.claim == "adverb-paradigm" })
        assertTrue(V2Validator.unresolvedRequiredSourceCapabilities(stamped, 17).any { it.claim == "productive-clock-time" })
        assertTrue(V2Validator.unresolvedRequiredSourceCapabilities(stamped, 23).any { it.claim == "completion-over-period" })
        assertTrue(V2Validator.unresolvedRequiredSourceCapabilities(stamped, 24).any { it.claim == "negation-scope" })
        assertTrue(V2Validator.unresolvedRequiredSourceCapabilities(stamped, 25).any { it.claim == "productive-diminutive" })
        assertEquals("NOT_PREPARED", stamped.unit(24)!!.readiness)
        assertEquals("NOT_PREPARED", stamped.unit(25)!!.readiness)
        assertEquals("DEVELOPMENT", stamped.unit(25)!!.installState)
        val formation = "Наиболее продуктивно образование от прилагательных суффиксальным или префиксально-суффиксальным способами: умело"
        val adverbParagraph = overreach(pack, formation, 7, "obj-u7-adverb", "adverb-paradigm", "cap-adverb-formation")
        assertTrue(adverbParagraph, adverbParagraph.contains("no independent adverb-paradigm admission"))
        assertTrue(V2Validator.unresolvedRequiredSourceCapabilities(pack, 7).any { it.claim == "adverb-paradigm" })
        val live = pack.sourceCapabilities.single { it.supportedClaim == "diminutive-form-classification" }
        val brokenHash = live.copy(sourceHash = "0".repeat(64))
        val hashReplaced = pack.copy(sourceCapabilities = pack.sourceCapabilities.filter { it.id != live.id } + brokenHash)
        assertTrue(V2Validator.unresolvedRequiredSourceCapabilities(hashReplaced, 25).any { it.claim == "productive-diminutive" })
        assertTrue(V2Validator.unresolvedRequiredSourceCapabilities(hashReplaced, 25).any { it.claim == "diminutive-form-classification" })
        val wrongObjective = live.copy(id = "cap-diminutive-wrong-objective", objectiveId = "obj-u25-address", supportedClaim = "register")
        val wrongObjectiveErrors = V2Validator.validate(pack.copy(sourceCapabilities = pack.sourceCapabilities + wrongObjective)).errors.joinToString("\n")
        assertTrue(wrongObjectiveErrors, wrongObjectiveErrors.contains("wrong unit or objective"))
        val missingLimitation = live.exactSpan.substringBefore("оттенок пренебрежения")
        val limited = overreach(pack, missingLimitation, 25, "obj-u25-nickname-rule", "productive-diminutive", "cap-diminutive-without-limit")
        assertTrue(limited, limited.contains("no independent productive-diminutive admission"))
        assertTrue(limited, limited.contains("bounded diminutive recognition does not clear productive-diminutive"))
    }

    @Test
    fun classificationChecksCannotClearProductiveDiminutive() {
        val pack = pack()
        val checks = pack.items.filter {
            it.objectiveId == "obj-u25-nickname-rule" &&
                it.assessment?.testedFeature == "DIMINUTIVE_FORM_CLASSIFICATION"
        }
        assertEquals(3, checks.size)
        assertEquals(2, checks.count { it.assessment?.independent == true })
        assertTrue(checks.all { it.assessment!!.testedFeature == "DIMINUTIVE_FORM_CLASSIFICATION" })
        assertTrue(checks.all { BatchBSources.diminutiveClassificationLabel(it.answer) })
        assertTrue(checks.all { it.prompt.contains("classifies") })
        assertTrue(pack.sourceCapabilities.none { it.supportedClaim == "productive-diminutive" })
        assertTrue(V2Validator.unresolvedRequiredSourceCapabilities(pack, 25).any { it.claim == "productive-diminutive" })
        assertEquals("NOT_PREPARED", pack.unit(25)!!.readiness)
        assertEquals("DEVELOPMENT", pack.unit(25)!!.installState)
        val unit = pack.unit(25)!!
        val preparedLearner = V2LearnerView(
            onboardingCompleted = true,
            currentUnitNumber = 25,
            startingPoint = V2StartingPoint.NEW_TO_CYRILLIC,
            hasPenguin = true,
            hasAnki = false,
            hasBinder = false,
            hasTutor = false,
            dailyMinutes = 25,
            transliterationEnabled = true,
            currentSessionId = null,
            currentStepIndex = 0,
            completedStepIds = unit.sessions.flatMap { it.steps }.map { it.id }.toSet(),
            correctItemIds = unit.objectives.flatMap { it.checkItemIds }.toSet(),
            bookAttestedUnits = setOf(25),
            completedUnitNumbers = emptySet(),
        )
        val blocked = advancementDecision(pack, unit, preparedLearner)
        assertFalse(blocked.allowed)
        assertTrue(blocked.remaining.any { it.contains("not prepared") })
        assertTrue(blocked.remaining.any { it.contains("productive-diminutive") })

        val flipped = pack.copy(
            units = pack.units.map { current ->
                if (current.number == 25) current.copy(readiness = "CONTENT_COMPLETE_REVIEW_PENDING") else current
            },
        )
        val flippedErrors = V2Validator.validate(flipped).errors.joinToString("\n")
        assertTrue(flippedErrors, flippedErrors.contains("without admitted productive-diminutive and register source capabilities"))
        assertTrue(V2Validator.contentCompletenessErrors(flipped, flipped.unit(25)!!).any {
            it.contains("without admitted productive-diminutive and register source capabilities")
        })
        val flippedDecision = advancementDecision(flipped, flipped.unit(25)!!, preparedLearner)
        assertFalse(flippedDecision.allowed)
        assertTrue(flippedDecision.remaining.any { it.contains("productive-diminutive") })

        val bounded = pack.sourceCapabilities.single { it.supportedClaim == "diminutive-form-classification" }
        val relabeled = bounded.copy(supportedClaim = "productive-diminutive")
        val relabeledPack = pack.copy(
            sourceCapabilities = pack.sourceCapabilities.map { if (it.id == bounded.id) relabeled else it },
        )
        val relabeledErrors = V2Validator.validate(relabeledPack).errors.joinToString("\n")
        assertTrue(relabeledErrors, relabeledErrors.contains("bounded diminutive recognition does not clear productive-diminutive"))
        assertTrue(relabeledErrors, relabeledErrors.contains("no independent productive-diminutive admission"))
        assertTrue(relabeledErrors, relabeledErrors.contains("no independent application check for productive-diminutive"))
        assertFalse(relabeledErrors, relabeledErrors.contains("cap-u25-diminutive-form-classification has no reviewable instruction and check chain"))
        assertTrue(V2Validator.unresolvedRequiredSourceCapabilities(relabeledPack, 25).any { it.claim == "productive-diminutive" })
        val forgedAdmission = GovernedSourceAdmission(
            sourceId = bounded.sourceId,
            sourceRevision = bounded.sourceRevision,
            sourceRecordId = bounded.sourceRecordId,
            sourceHash = bounded.sourceHash,
            exactSpan = bounded.exactSpan,
            unitNumber = bounded.unitNumber,
            objectiveId = bounded.objectiveId,
            governedClaim = "productive-diminutive",
        )
        assertTrue(BatchBSources.admits(relabeled, setOf(forgedAdmission), "productive-diminutive"))
        assertFalse(BatchBSources.clearsProductiveDiminutive(relabeled, setOf(forgedAdmission)))
        assertFalse(BatchBSources.clearsProductiveDiminutive(relabeled))

        val stamped = pack.copy(
            provenanceRecords = pack.provenanceRecords.map { it.copy(reviewStatus = "APPROVED") },
            admissions = pack.admissions.copy(penguinProseAssets = 9, generatedRussianWords = 4),
        )
        assertTrue(V2Validator.unresolvedRequiredSourceCapabilities(stamped, 25).any { it.claim == "productive-diminutive" })
        val coOccurrence = overreach(
            pack,
            "productive diminutive co-occurrence",
            25,
            "obj-u25-nickname-rule",
            "productive-diminutive",
            "cap-diminutive-cooccurrence",
        )
        assertTrue(coOccurrence, coOccurrence.contains("keyword or co-occurrence evidence does not clear productive-diminutive"))
        assertTrue(coOccurrence, coOccurrence.contains("no independent productive-diminutive admission"))
        assertTrue(V2Validator.unresolvedRequiredSourceCapabilities(pack, 25).any { it.claim == "productive-diminutive" })

        val stripped = pack.copy(
            units = pack.units.map { current ->
                if (current.number != 25) {
                    current
                } else {
                    current.copy(
                        objectives = current.objectives.map { objective ->
                            if (objective.id != "obj-u25-nickname-rule") {
                                objective
                            } else {
                                objective.copy(
                                    instructionStepIds = emptyList(),
                                    exampleIds = emptyList(),
                                    practiceStepIds = emptyList(),
                                    checkItemIds = emptyList(),
                                    remediationStepIds = emptyList(),
                                )
                            }
                        },
                    )
                }
            },
            sourceCapabilities = pack.sourceCapabilities.map { capability ->
                if (capability.id == bounded.id) relabeled else capability
            },
        )
        val strippedErrors = V2Validator.validate(stripped).errors.joinToString("\n")
        assertTrue(strippedErrors, strippedErrors.contains("no reviewable instruction and check chain"))
        assertTrue(strippedErrors, strippedErrors.contains("no independent application check for productive-diminutive"))
        assertTrue(strippedErrors, strippedErrors.contains("no independent productive-diminutive admission"))

        listOf(
            relabeled.copy(sourceId = "src-openrussian-50e210c4"),
            relabeled.copy(sourceRevision = "1"),
            relabeled.copy(sourceRecordId = "other-record"),
            relabeled.copy(sourceHash = "0".repeat(64)),
            relabeled.copy(exactSpan = "not the frozen span"),
            relabeled.copy(unitNumber = 24, objectiveId = "obj-u24-placement"),
            relabeled.copy(objectiveId = "obj-u25-address"),
            relabeled.copy(supportedClaim = "register"),
        ).forEach { broken ->
            val errors = V2Validator.validate(
                pack.copy(sourceCapabilities = pack.sourceCapabilities + broken.copy(id = "cap-closed-${broken.sourceRevision}-${broken.supportedClaim}")),
            ).errors.joinToString("\n")
            assertTrue(errors, errors.contains("cap-closed-") && (
                errors.contains("revision does not match the frozen record") ||
                    errors.contains("span is not in the frozen record") ||
                    errors.contains("wrong unit or objective") ||
                    errors.contains("no independent")
                ))
            assertTrue(V2Validator.unresolvedRequiredSourceCapabilities(
                pack.copy(sourceCapabilities = pack.sourceCapabilities + broken.copy(id = "cap-closed-live")),
                25,
            ).any { it.claim == "productive-diminutive" })
        }
    }

    private fun overreach(
        pack: V2CurriculumPack,
        span: String,
        unitNumber: Int,
        objectiveId: String,
        claim: String,
        id: String,
    ): String {
        val existing = pack.provenanceRecords.any { it.originalSourceText == span }
        val base = if (existing) {
            pack.copy(
                provenanceRecords = pack.provenanceRecords.map { record ->
                    if (record.originalSourceText == span) {
                        record.copy(unitNumber = unitNumber, objectiveId = objectiveId)
                    } else {
                        record
                    }
                },
            )
        } else {
            val template = pack.provenanceRecords.first { it.unitNumber == unitNumber }
            val visible = span.lineSequence().first { it.any { char -> char.isLetter() } }.take(48)
            val record = template.copy(
                itemId = "prov-$id",
                unitNumber = unitNumber,
                objectiveId = objectiveId,
                originalSourceText = span,
                shippedText = visible,
                sourceRecordId = "probe:$id",
                sourceHash = sha256(span),
                contentHash = sha256(visible),
            )
            pack.copy(provenanceRecords = pack.provenanceRecords + record)
        }
        val host = base.provenanceRecords.first { it.originalSourceText == span && it.objectiveId == objectiveId }
        val capability = V2SourceCapability(
            id = id,
            sourceId = host.sourceId,
            sourceRevision = host.sourceRevision,
            sourceRecordId = host.sourceRecordId,
            sourceHash = host.sourceHash,
            exactSpan = span,
            unitNumber = unitNumber,
            objectiveId = objectiveId,
            supportedClaim = claim,
        )
        return V2Validator.validate(base.copy(sourceCapabilities = base.sourceCapabilities + capability)).errors.joinToString("\n")
    }

    private fun shipped(pack: V2CurriculumPack, objectiveId: String, text: String): V2ProvenanceRecord {
        return pack.provenanceRecords.first { it.objectiveId == objectiveId && it.shippedText == text }
    }

    private fun replaceItem(pack: V2CurriculumPack, mutated: dev.russiancompanion.v2.curriculum.V2CheckItem) =
        pack.copy(items = pack.items.map { if (it.id == mutated.id) mutated else it })

    private fun replaceCapability(pack: V2CurriculumPack, id: String, mutated: V2SourceCapability) =
        pack.copy(sourceCapabilities = pack.sourceCapabilities.map { if (it.id == id) mutated else it })

    private fun sourceLayout(prompt: String): Boolean = Regex(
        "(?i)(?:" +
            "equals\\s+sign|" +
            "frozen\\s+line|" +
            "\\bwritten\\s+(?:after|for|on|in|beside|next\\s+to)\\b|" +
            "\\bcolumn\\b|" +
            "\\bbeside\\s+(?:the\\s+)?(?:shown\\s+)?tag\\b|" +
            "\\bg\\s*=\\s*[fmn]\\b" +
            ")",
    ).containsMatchIn(prompt)

    private fun sha256(text: String): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(text.toByteArray(Charsets.UTF_8))
        return digest.joinToString("") { byte -> "%02x".format(byte) }
    }

    private fun pack() = V2PackLoader.parse(File(repo(), "app/src/main/assets/content/v2-curriculum.json").readText())

    private fun repo(): File = listOf(File("."), File("..")).first { File(it, "app/src/main").isDirectory }
}
