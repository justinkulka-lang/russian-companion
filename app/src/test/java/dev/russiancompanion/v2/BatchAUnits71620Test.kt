package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.ContentAuditSources
import dev.russiancompanion.v2.curriculum.V2CheckItem
import dev.russiancompanion.v2.curriculum.V2CurriculumPack
import dev.russiancompanion.v2.curriculum.V2LearnerView
import dev.russiancompanion.v2.curriculum.V2PackLoader
import dev.russiancompanion.v2.curriculum.V2ProvenanceRecord
import dev.russiancompanion.v2.curriculum.V2StartingPoint
import dev.russiancompanion.v2.curriculum.V2Validator
import dev.russiancompanion.v2.curriculum.advancementDecision
import dev.russiancompanion.v2.curriculum.ordinaryUnitRequirements
import dev.russiancompanion.v2.curriculum.unit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class BatchAUnits71620Test {
    @Test
    fun wikipediaLicenseRouteIsNotTheWikibooksSnapshot() {
        val root = repo()
        val script = """
            import json
            from pathlib import Path
            from units_26_30_intake import license_admission_errors
            root = Path('.').resolve().parent
            freeze_path = root / 'catalog/sources/units-16-20/FREEZE.json'
            source_root = freeze_path.parent
            freeze = json.loads(freeze_path.read_text())
            assert license_admission_errors(freeze, source_root) == [], license_admission_errors(freeze, source_root)
            wikibooks_only = json.loads(json.dumps(freeze))
            for page in wikibooks_only['pages']:
                if page['sourceId'].startswith('src-wikipedia-'):
                    page['licenseRoute'] = 'wikibooks'
            wikibooks_errors = license_admission_errors(wikibooks_only, source_root)
            assert any('only Wikibooks license evidence' in error for error in wikibooks_errors), wikibooks_errors
            wikipedia_only = json.loads(json.dumps(freeze))
            for page in wikipedia_only['pages']:
                if page['sourceId'].startswith('src-wikibooks-'):
                    page['licenseRoute'] = 'wikipedia'
            wikipedia_errors = license_admission_errors(wikipedia_only, source_root)
            assert any('only Wikipedia license evidence' in error for error in wikipedia_errors), wikipedia_errors
            print('ok')
        """.trimIndent()
        val process = ProcessBuilder("python3", "-c", script)
            .directory(File(root, "catalog"))
            .redirectErrorStream(true)
            .start()
        val output = process.inputStream.bufferedReader().readText()
        assertEquals(output, 0, process.waitFor())
    }

    @Test
    fun exactSpanIdentityRejectsKeywordAndCrossClaimSubstitution() {
        val pack = pack()
        val complement = pack.sourceCapabilities.first { it.supportedClaim == "instrumental-complements" }
        listOf(
            complement.copy(exactSpan = "January"),
            complement.copy(sourceRevision = "4292379"),
            complement.copy(sourceHash = "0".repeat(64)),
            complement.copy(unitNumber = 18, objectiveId = "obj-u18-comparison", supportedClaim = "comparison"),
            complement.copy(objectiveId = "obj-u17-age", supportedClaim = "age"),
        ).forEach { mutated ->
            val next = replaceCapability(pack, complement.id, mutated)
            val errors = V2Validator.validate(next).errors
            assertTrue(errors.joinToString("\n"), errors.any { complement.id in it })
        }

        val comparison = pack.sourceCapabilities.first { it.supportedClaim == "comparison" }
        val relativeSwap = pack.copy(
            sourceCapabilities = pack.sourceCapabilities.filter { it.supportedClaim != "relative-kotoryy" } +
                comparison.copy(id = "cap-swapped-relative", objectiveId = "obj-u18-relative", supportedClaim = "relative-kotoryy"),
        )
        val relativeErrors = V2Validator.validate(claimComplete(relativeSwap, 18)).errors
        assertTrue(relativeErrors.joinToString("\n"), relativeErrors.any { it.contains("without comparison and relative-clause sources") })

        val keyword = pack.copy(
            sourceCapabilities = pack.sourceCapabilities.filter { it.unitNumber != 17 },
            provenanceRecords = pack.provenanceRecords + keywordRecord(pack),
        )
        val keywordErrors = V2Validator.validate(claimComplete(keyword, 17)).errors
        assertTrue(
            keywordErrors.joinToString("\n"),
            keywordErrors.any { it.contains("Unit 17 is missing productive clock-time and calendar-date source capabilities") },
        )
        assertFalse(keywordErrors.joinToString("\n"), keywordErrors.any { it.contains("telling-time, weekday, date, and age") })

        val clockOnly = pack.sourceCapabilities.filter {
            it.unitNumber != 17 || it.supportedClaim == "clock-question" || it.supportedClaim == "age" ||
                it.supportedClaim == "weekdays" || it.supportedClaim == "months"
        }
        val clockPack = pack.copy(sourceCapabilities = clockOnly)
        val clockErrors = V2Validator.validate(claimComplete(clockPack, 17)).errors
        assertTrue(
            clockErrors.joinToString("\n"),
            clockErrors.any { it.contains("Unit 17 is missing productive clock-time and calendar-date source capabilities") },
        )
        assertFalse(clockErrors.joinToString("\n"), clockErrors.any { it.contains("telling-time, weekday, date, and age") })

        val conditional = pack.sourceCapabilities.first { it.supportedClaim == "conditional-by" }
        val probabilitySwap = pack.copy(
            sourceCapabilities = pack.sourceCapabilities.filter { it.supportedClaim != "obligation-dolzhen" } +
                conditional.copy(id = "cap-swapped-obligation", objectiveId = "obj-u19-obligation", supportedClaim = "obligation-dolzhen"),
        )
        val obligationErrors = V2Validator.validate(claimComplete(probabilitySwap, 19)).errors
        assertTrue(obligationErrors.joinToString("\n"), obligationErrors.any { it.contains("without conditional and obligation sources") })

        val motion = pack.sourceCapabilities.first { it.supportedClaim == "motion-directionality" }
        val foreign = pack.copy(
            sourceCapabilities = pack.sourceCapabilities.filter { it.supportedClaim != "instrumental-complements" } +
                motion.copy(id = complement.id, unitNumber = 16, objectiveId = "obj-u16-complements", supportedClaim = "instrumental-complements"),
        )
        val foreignErrors = V2Validator.validate(claimComplete(foreign, 16)).errors
        assertTrue(foreignErrors.joinToString("\n"), foreignErrors.any { it.contains("without an admitted instrumental complement span") })
    }

    @Test
    fun partialUnitsStayBlockedAndClosedChainsCanBeReviewPending() {
        val pack = pack()
        listOf(16, 18, 19, 20).forEach { number ->
            assertEquals("CONTENT_COMPLETE_REVIEW_PENDING", pack.unit(number)!!.readiness)
            val decision = ordinaryUnitRequirements(pack, pack.unit(number)!!, learner(pack, number))
            assertTrue(decision.remaining.joinToString("\n"), decision.allowed)
            assertAssignedReviewStillRequired(pack, number)
        }
        assertEquals("CONTENT_COMPLETE_REVIEW_PENDING", pack.unit(7)!!.readiness)
        val unitSeven = ordinaryUnitRequirements(pack, pack.unit(7)!!, learner(pack, 7))
        assertTrue(unitSeven.remaining.joinToString("\n"), unitSeven.allowed)
        assertAssignedReviewStillRequired(pack, 7)
        assertTrue(V2Validator.contentCompletenessErrors(pack, pack.unit(7)!!).isEmpty())
        assertEquals("CONTENT_COMPLETE_REVIEW_PENDING", pack.unit(17)!!.readiness)
        val unitSeventeen = ordinaryUnitRequirements(pack, pack.unit(17)!!, learner(pack, 17))
        assertTrue(unitSeventeen.remaining.joinToString("\n"), unitSeventeen.allowed)
        assertAssignedReviewStillRequired(pack, 17)
        assertTrue(V2Validator.contentCompletenessErrors(pack, pack.unit(17)!!).isEmpty())
        val strippedManner = pack.copy(
            sourceCapabilities = pack.sourceCapabilities.filter { it.supportedClaim != "bounded-manner-adverb-formation" },
        )
        val paradigm = ordinaryUnitRequirements(strippedManner, strippedManner.unit(7)!!, learner(strippedManner, 7))
        assertFalse(paradigm.allowed)
        assertTrue(paradigm.remaining.any { it.contains("bounded manner-adverb formation") })
        val strippedClock = pack.copy(sourceCapabilities = pack.sourceCapabilities.filter { it.supportedClaim != "productive-clock-time" })
        val clock = ordinaryUnitRequirements(strippedClock, strippedClock.unit(17)!!, learner(strippedClock, 17))
        assertFalse(clock.allowed)
        assertTrue(clock.remaining.any { it.contains("productive clock-time") })
        val strippedDate = pack.copy(sourceCapabilities = pack.sourceCapabilities.filter { it.supportedClaim != "calendar-date" })
        val dateDecision = ordinaryUnitRequirements(strippedDate, strippedDate.unit(17)!!, learner(strippedDate, 17))
        assertFalse(dateDecision.allowed)
    }

    @Test
    fun admittedCapabilitiesRejectForgeryCrossBindingAndWrongSpans() {
        val pack = pack()
        listOf("adverb-form-gloss", "clock-expression-recognition", "calendar-date").forEach { claim ->
            val capability = pack.sourceCapabilities.first { it.supportedClaim == claim }
            val record = pack.provenanceRecords.first {
                it.objectiveId == capability.objectiveId && it.originalSourceText == capability.exactSpan
            }
            assertEquals(capability.sourceId, record.sourceId)
            assertEquals(capability.sourceRevision, record.sourceRevision)
            assertEquals(capability.sourceHash, record.sourceHash)
            assertTrue(record.sourceUrl.contains("oldid=${capability.sourceRevision}"))
            listOf(
                capability.copy(sourceHash = "0".repeat(64)),
                capability.copy(sourceRevision = "1"),
                capability.copy(exactSpan = "January"),
                capability.copy(unitNumber = if (capability.unitNumber == 7) 17 else 7, objectiveId = "obj-u16-complements", supportedClaim = "instrumental-complements"),
                capability.copy(objectiveId = "obj-u19-obligation", supportedClaim = "obligation-dolzhen", unitNumber = 19),
            ).forEach { mutated ->
                val errors = V2Validator.validate(replaceCapability(pack, capability.id, mutated)).errors
                assertTrue(errors.joinToString("\n"), errors.any { capability.id in it || mutated.supportedClaim in it })
            }
            val absent = pack.copy(sourceCapabilities = pack.sourceCapabilities.filter { it.id != capability.id })
            val absentDecision = ordinaryUnitRequirements(absent, absent.unit(capability.unitNumber)!!, learner(absent, capability.unitNumber))
            if (claim == "clock-expression-recognition") {
                assertTrue(absentDecision.remaining.joinToString("\n"), absentDecision.allowed)
                assertAssignedReviewStillRequired(absent, capability.unitNumber)
            } else {
                assertFalse(absentDecision.allowed)
                val full = advancementDecision(absent, absent.unit(capability.unitNumber)!!, learner(absent, capability.unitNumber))
                assertFalse(full.allowed)
                assertEquals(absentDecision.remaining + "Complete the assigned earlier-objective review", full.remaining)
            }
        }
        val adverb = pack.provenanceRecords.first { it.objectiveId == "obj-u7-adverb" && it.shippedText == "быстро" }
        val quickly = pack.provenanceRecords.first { it.objectiveId == "obj-u7-adverb" && it.shippedText == "quickly" }
        val well = pack.provenanceRecords.first { it.objectiveId == "obj-u7-adverb" && it.shippedText == "well" }
        val wonderfully = pack.provenanceRecords.first { it.objectiveId == "obj-u7-adverb" && it.shippedText == "wonderfully" }
        assertEquals("EXPLICIT_SOURCE_PAIR", V2Validator.classifyRelation(adverb, quickly, listOf(well, wonderfully)))
        assertEquals(null, V2Validator.classifyRelation(adverb, well, listOf(quickly, wonderfully)))
        val adjective = adverb.copy(itemId = "prov-adjective-cue", shippedText = "быстрый")
        assertEquals(null, V2Validator.classifyRelation(adjective, quickly, listOf(well, wonderfully)))
    }

    @Test
    fun batchAExplanationsNameTheKeyedRelationshipAndRejectASwap() {
        val pack = pack()
        val items = pack.items.filter { it.objectiveId in BATCH_A_OBJECTIVES }
        assertEquals(41, items.size)
        val liveErrors = V2Validator.validate(pack).errors
        val clock = """The frozen source pairs the shown form "Который час?" with the admitted gloss "What time is it?"."""
        val book = """The frozen source pairs the shown form "Которую книгу вы хотите?" with the admitted gloss "Which book do you want?"."""
        val apple = """The frozen source pairs the shown form "Которое яблоко моё?" with the admitted gloss "Which apple is mine?"."""
        items.filter { it.promptLetter == "Который час?" }.forEach { item ->
            assertTrue(item.id, item.assessment!!.whyAnswerDemonstratesObjective.startsWith(clock + "\n"))
            assertEquals(item.id, item.feedback, item.assessment.whyAnswerDemonstratesObjective.substringAfter("\n"))
            val feedbackRecord = pendingExplanation(pack, item, item.feedback)
            assertContentAuditResearch(checkNotNull(feedbackRecord))
            assertEquals(item.id, item.distractorRationales[1], item.assessment.distractorDifference[1])
            assertTrue(item.id, item.distractorRationales[1].contains("Which book do you want?"))
            assertContentAuditResearch(checkNotNull(pendingExplanation(pack, item, item.distractorRationales[1])))
        }
        val appleItem = items.single { it.promptLetter == "Которое яблоко моё?" }
        assertTrue(appleItem.assessment!!.whyAnswerDemonstratesObjective.startsWith(apple + "\n"))
        assertEquals(appleItem.feedback, appleItem.assessment.whyAnswerDemonstratesObjective.substringAfter("\n"))
        assertContentAuditResearch(checkNotNull(pendingExplanation(pack, appleItem, appleItem.feedback)))
        assertTrue(appleItem.distractorRationales[1].contains("Which book do you want?"))
        assertContentAuditResearch(checkNotNull(pendingExplanation(pack, appleItem, appleItem.distractorRationales[1])))
        pack.authoredEnglishRecords.filter { it.unitNumber in 16..20 }.forEach { record ->
            assertFalse(record.text == "The frozen line writes" || record.text == "beside")
        }
        items.groupBy { it.objectiveId }.forEach { (_, group) ->
            group.forEach { item ->
                val assessment = item.assessment!!
                val why = assessment.whyAnswerDemonstratesObjective
                val shown = item.promptLetter!!
                val corrected = item.sourceId == ContentAuditSources.SOURCE_ID
                val expandedExplanation = pendingExplanation(pack, item, item.feedback)
                if (corrected) {
                    assertEquals(item.id, "STATED_APPLICATION", assessment.relationKind)
                    val records = assessment.relationParticipantIds.map { id ->
                        pack.provenanceRecords.single { it.itemId == id }
                    }
                    assertTrue(item.id, records.isNotEmpty())
                    assertTrue(item.id, records.all { it.sourceId == ContentAuditSources.SOURCE_ID })
                    assertTrue(item.id, records.any { record ->
                        record.synthesis!!.applications.any { application ->
                            application.cue == shown && application.answer == item.answer &&
                                application.distractors.toSet() == (item.options - item.answer).toSet()
                        }
                    })
                    assertTrue(item.id, why.contains(shown) && why.contains(item.answer))
                    assertTrue(item.id, item.feedback.contains(item.answer))
                    if (expandedExplanation != null) assertContentAuditResearch(expandedExplanation)
                    else assertTrue(item.id, item.feedback.contains(shown))
                } else if (expandedExplanation != null) {
                    assertContentAuditResearch(expandedExplanation)
                    assertTrue(item.id, why == item.feedback || why.endsWith("\n" + item.feedback))
                    if (why != item.feedback) {
                        val rationaleRecord = pendingExplanation(pack, item, why)
                        if (rationaleRecord != null) assertContentAuditResearch(rationaleRecord)
                        else assertCompleteRelationship(item.id, why.substringBefore("\n"), shown, item.answer)
                    }
                    assertTrue(item.id, why.contains(shown) && why.contains(item.answer))
                    assertTrue(item.id, item.feedback.contains(item.answer))
                } else {
                    assertEquals(item.id, why, item.feedback)
                    assertCompleteRelationship(item.id, why, shown, item.answer)
                }
                val concealed = why.replace(item.answer, item.answer + " leftover") + " " + item.answer
                assertTrue(concealed.contains(item.answer))
                assertFalse(
                    "${item.id} accepted a trailing substring",
                    relationshipNames(concealed, shown, item.answer),
                )
                group.map { it.answer }.filter { other ->
                    other != item.answer && !item.answer.contains(other) && !other.contains(item.answer)
                }.forEach { other ->
                    assertFalse("${item.id} names $other", why.contains(other) || item.feedback.contains(other))
                }
                item.options.forEachIndexed { index, option ->
                    if (option == item.answer) return@forEachIndexed
                    val rationale = item.distractorRationales[index]
                    val difference = assessment.distractorDifference[index]
                    assertEquals("${item.id} rationale", rationale, difference)
                    val shownFormGloss = glossWhenOptionIsShownForm(rationale, option)
                    if (corrected || expandedExplanation != null) {
                        assertTrue(item.id, rationale.contains(option))
                    } else if (shownFormGloss != null) {
                        assertCompleteRelationship(item.id, rationale, option, shownFormGloss)
                    } else {
                        assertCompleteRelationship(item.id, rationale, competingForm(rationale, option), option)
                    }
                    if (!option.contains(item.answer)) {
                        assertFalse("${item.id} rationale names ${item.answer}", rationale.contains(item.answer) || difference.contains(item.answer))
                    }
                    if (!item.answer.contains(option)) {
                        assertFalse("${item.id} explanation names $option", why.contains(option))
                    }
                }
                assertTrue(
                    liveErrors.filter { it.contains(item.id) }.joinToString("\n"),
                    liveErrors.none { error ->
                        error.contains(item.id) && (
                            error.contains("explanation") || error.contains("gloss") || error.contains("distractor rationale")
                            )
                    },
                )
                val distractor = item.options.first { option ->
                    option != item.answer && !item.answer.contains(option) && !option.contains(item.answer)
                }
                val replacedWhy = why.replace(item.answer, distractor)
                val replacedFeedback = item.feedback.replace(item.answer, distractor)
                assertFalse(replacedWhy.contains(item.answer))
                assertFalse(relationshipNames(replacedWhy, shown, item.answer))
                val replacedErrors = V2Validator.validate(
                    replaceItem(
                        pack,
                        item.copy(
                            feedback = replacedFeedback,
                            assessment = assessment.copy(whyAnswerDemonstratesObjective = replacedWhy),
                        ),
                    ),
                ).errors
                assertTrue(
                    replacedErrors.filter { it.contains(item.id) }.joinToString("\n"),
                    replacedErrors.any { it.contains(item.id) && (it.contains("keyed answer") || it.contains("different gloss")) },
                )
            }
            group.forEach { item ->
                val other = group.firstOrNull { candidate ->
                    candidate.answer != item.answer &&
                        !candidate.assessment!!.whyAnswerDemonstratesObjective.contains(item.answer)
                } ?: return@forEach
                val swappedErrors = V2Validator.validate(
                    replaceItem(
                        pack,
                        item.copy(
                            feedback = other.feedback,
                            assessment = item.assessment!!.copy(
                                whyAnswerDemonstratesObjective = other.assessment!!.whyAnswerDemonstratesObjective,
                            ),
                        ),
                    ),
                ).errors
                assertTrue(
                    swappedErrors.filter { it.contains(item.id) }.joinToString("\n"),
                    swappedErrors.any {
                        it.contains(item.id) && (
                            it.contains("explanation") || it.contains("gloss") || it.contains("frozen evidence")
                            )
                    },
                )
            }
        }
    }

    /** This synthetic view has ordinary evidence only; it never carries an assigned review. */
    private fun assertAssignedReviewStillRequired(pack: V2CurriculumPack, number: Int) {
        val decision = advancementDecision(pack, pack.unit(number)!!, learner(pack, number))
        assertFalse(decision.allowed)
        assertEquals(listOf("Complete the assigned earlier-objective review"), decision.remaining)
    }

    private fun pendingExplanation(pack: V2CurriculumPack, item: V2CheckItem, text: String): V2ProvenanceRecord? =
        pack.provenanceRecords.firstOrNull {
            it.sourceId == ContentAuditSources.SOURCE_ID && it.objectiveId == item.objectiveId &&
                it.itemId.startsWith("prov-ca-") && it.shippedText == text
        }

    private fun assertCompleteRelationship(id: String, text: String, shown: String, answer: String) {
        assertFalse("$id ends with a scaffold fragment: $text", text.trimEnd().endsWith("The frozen line writes"))
        assertFalse("$id repeats a scaffold: $text", text.contains("The frozen line writes"))
        val pairFrames = text.split("The frozen source pairs").size - 1
        assertTrue("$id repeats the pair frame: $text", pairFrames == 0 || pairFrames == 1)
        assertTrue("$id is not the keyed relationship: $text", relationshipNames(text, shown, answer))
        assertFalse("$id explanation ends as a question: $text", text.trimEnd().endsWith("?"))
    }

    private fun relationshipNames(text: String, shown: String, answer: String): Boolean {
        val pair = PAIR_SENTENCE.matchEntire(text)
        if (pair != null) {
            return pair.groupValues[1] == shown && pair.groupValues[2] == answer
        }
        if (text.contains("||")) {
            val cells = text.split("||").map { it.trim().trim('|').trim() }.filter { it.isNotEmpty() }
            return cells.contains(shown) && cells.contains(answer)
        }
        val quoted = QUOTED_GLOSS.matchEntire(text)
        if (quoted != null) {
            return quoted.groupValues[1] == shown && quoted.groupValues[2] == answer
        }
        val speaker = SPEAKER_LINE.matchEntire(text)
        if (speaker != null) {
            val forms = speaker.groupValues[2].removeSuffix(")").split(" (or ")
            return speaker.groupValues[1] == answer && forms.any { it == shown }
        }
        val have = HAVE_LINE.matchEntire(text)
        if (have != null) {
            return have.groupValues[1] == answer && have.groupValues[2] == shown
        }
        val subject = SUBJECT_LINE.matchEntire(text)
        if (subject != null) {
            return subject.groupValues[1] == answer && subject.groupValues[2] == shown
        }
        val label = SOURCE_LABEL.matchEntire(text)
        if (label != null) return label.groupValues[1] == answer
        return shown.isEmpty() && text == answer && (answer.trimEnd().endsWith(".") || answer.split(Regex("\\s+")).size >= 4)
    }

    /** The option is the shown form of a real pair, so the keyed gloss is the other side. */
    private fun glossWhenOptionIsShownForm(text: String, option: String): String? {
        val pair = PAIR_SENTENCE.matchEntire(text) ?: return null
        val form = pair.groupValues[1]
        val gloss = pair.groupValues[2]
        if (form == option && form != gloss) return gloss
        return null
    }

    private fun competingForm(text: String, answer: String): String {
        val pair = PAIR_SENTENCE.matchEntire(text)
        if (pair != null && pair.groupValues[2] == answer) return pair.groupValues[1]
        if (text.contains("||")) {
            val cells = text.split("||").map { it.trim().trim('|').trim() }.filter { it.isNotEmpty() }
            if (cells.contains(answer)) return cells.first { it != answer && it != "noun" }
        }
        val quoted = QUOTED_GLOSS.matchEntire(text)
        if (quoted != null && quoted.groupValues[2] == answer) return quoted.groupValues[1]
        val speaker = SPEAKER_LINE.matchEntire(text)
        if (speaker != null && speaker.groupValues[1] == answer) {
            return speaker.groupValues[2].removeSuffix(")").split(" (or ").first()
        }
        val have = HAVE_LINE.matchEntire(text)
        if (have != null && have.groupValues[1] == answer) return have.groupValues[2]
        val subject = SUBJECT_LINE.matchEntire(text)
        if (subject != null && subject.groupValues[1] == answer) return subject.groupValues[2]
        return ""
    }

    @Test
    fun generatedRussianAndInternetStayClosed() {
        val pack = pack()
        val marked = pack.copy(admissions = pack.admissions.copy(generatedRussianWords = 1))
        assertTrue(V2Validator.validate(marked).errors.any { it.contains("Generated Russian words are forbidden") })
        val item = pack.items.first { it.objectiveId == "obj-u16-complements" }
        val flagged = pack.copy(items = pack.items.map { if (it.id == item.id) it.copy(containsGeneratedRussian = true) else it })
        assertTrue(V2Validator.validate(flagged).errors.any { it.contains("marks generated Russian") })
        val manifest = File(repo(), "app/src/main/AndroidManifest.xml").readText()
        assertFalse(manifest.contains("android.permission.INTERNET"))
        assertTrue(pack.provenanceRecords.none { it.originalSourceText.contains("The New Penguin Russian Course") })
        assertTrue(pack.unitReviews.all { it.reviewer.isNullOrBlank() && it.status == "IMPLEMENTED_PENDING_FINAL_AUDIT" })
        assertEquals((1..30).toSet(), pack.ownedEditionMappings.map { it.unitNumber }.toSet())
        assertEquals(30, pack.ownedEditionMappings.size)
        assertTrue(pack.ownedEditionMappings.all {
            it.status == "PENDING_CURRICULUM_REVIEWER" && it.reviewer.isNullOrBlank() &&
                it.reviewerQualification.isNullOrBlank() && it.reviewedContentDigest.isNullOrBlank() &&
                it.isbn.isNullOrBlank() && it.isbnObservation == "UNRESOLVED"
        })
    }

    private fun replaceItem(pack: V2CurriculumPack, mutated: dev.russiancompanion.v2.curriculum.V2CheckItem) =
        pack.copy(items = pack.items.map { if (it.id == mutated.id) mutated else it })

    private fun replaceCapability(pack: V2CurriculumPack, id: String, mutated: dev.russiancompanion.v2.curriculum.V2SourceCapability) =
        pack.copy(sourceCapabilities = pack.sourceCapabilities.map { if (it.id == id) mutated else it })

    private fun keywordRecord(pack: V2CurriculumPack): V2ProvenanceRecord {
        return pack.provenanceRecords.first().copy(
            itemId = "prov-keyword-calendar",
            unitNumber = 17,
            objectiveId = "obj-u17-calendar-date",
            originalSourceText = "Monday January years old который час o'clock",
            shippedText = "Monday",
        )
    }

    private fun claimComplete(pack: V2CurriculumPack, number: Int) = pack.copy(
        units = pack.units.map { unit ->
            if (unit.number != number) {
                unit
            } else {
                unit.copy(
                    readiness = "CONTENT_COMPLETE_REVIEW_PENDING",
                    sessions = unit.sessions.map { session ->
                        session.copy(
                            steps = session.steps.map { step ->
                                if (step.kind == "UNAVAILABLE" || !step.unavailableReason.isNullOrBlank()) {
                                    step.copy(kind = "ORIENT", unavailableReason = null)
                                } else {
                                    step
                                }
                            },
                        )
                    },
                )
            }
        },
    )

    private fun learner(pack: V2CurriculumPack, number: Int): V2LearnerView {
        val unit = pack.unit(number)!!
        val steps = unit.sessions.flatMap { it.steps }
        return V2LearnerView(
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
            completedStepIds = steps.map { it.id }.toSet(),
            correctItemIds = (unit.objectives.flatMap { it.checkItemIds } + steps.flatMap { it.itemIds }).toSet(),
            bookAttestedUnits = setOf(number),
            completedUnitNumbers = emptySet(),
        )
    }

    private fun pack() = V2PackLoader.parse(File(repo(), "app/src/main/assets/content/v2-curriculum.json").readText())

    private fun repo(): File = listOf(File("."), File("..")).first { File(it, "app/src/main").isDirectory }

    private companion object {
        val PAIR_SENTENCE = Regex(
            """^The frozen source pairs the shown form "(.*)" with the admitted gloss "(.*)"(?: \("([^"]*)"\))?\.$""",
        )
        val QUOTED_GLOSS = Regex("""^(.+) '(.+)'$""")
        val SPEAKER_LINE = Regex("""^(?:while )?(a (?:male|female) speaker) would say (.+)$""")
        val HAVE_LINE = Regex("""^"([^"]+)" translates to (.+)$""")
        val SUBJECT_LINE = Regex("""^(If the subject is (?:feminine|neuter|plural)), it's (.+)\.$""")
        val SOURCE_LABEL = Regex(
            """^(?:The heading|The table header|The source quotes) "([^"]+)" (?:labels the section\. It is not the gloss of the shown form|names the other motion column|as an English restatement)\.$""",
        )
        val BATCH_A_OBJECTIVES = setOf(
            "obj-u16-complements",
            "obj-u17-age",
            "obj-u17-weekdays",
            "obj-u17-months",
            "obj-u17-clock",
            "obj-u17-clock-answers",
            "obj-u17-calendar-date",
            "obj-u7-adverb",
            "obj-u18-comparison",
            "obj-u18-relative",
            "obj-u19-conditional",
            "obj-u19-obligation",
            "obj-u20-motion",
        )
    }
}
