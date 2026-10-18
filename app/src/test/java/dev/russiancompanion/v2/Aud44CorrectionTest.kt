package dev.russiancompanion.v2

import dev.russiancompanion.release.readAcceptanceCandidate
import dev.russiancompanion.v2.curriculum.CompleteRuleContextPins
import dev.russiancompanion.v2.curriculum.Unit17QuestionPins
import dev.russiancompanion.v2.curriculum.V2CurriculumPack
import dev.russiancompanion.v2.curriculum.V2LearnerView
import dev.russiancompanion.v2.curriculum.V2PackLoader
import dev.russiancompanion.v2.curriculum.V2SourceCapability
import dev.russiancompanion.v2.curriculum.V2StartingPoint
import dev.russiancompanion.v2.curriculum.V2Validator
import dev.russiancompanion.v2.curriculum.advancementDecision
import dev.russiancompanion.v2.curriculum.ordinaryUnitRequirements
import dev.russiancompanion.v2.curriculum.unit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * AUD-44 corrections: bounded recognition does not clear a paradigm or a
 * productive clock rule, and unaffected units keep stable identities.
 */
class Aud44CorrectionTest {
    @Test
    fun threeAdverbGlossPairsCannotClearTheParadigm() {
        val pack = pack()
        val pairs = pack.items.filter { it.objectiveId == "obj-u7-adverb" }
        assertEquals(listOf("quickly", "well", "wonderfully"), pairs.map { it.answer })
        assertTrue(pack.sourceCapabilities.none { it.supportedClaim == "adverb-paradigm" })
        assertFalse(unresolved(pack, 7).contains("adverb-paradigm"))
        assertFalse(unresolved(pack, 7).contains("bounded-manner-adverb-formation"))
        assertFalse(unresolved(pack, 7).contains("adverb-form-gloss"))
        val gloss = pack.sourceCapabilities.first { it.supportedClaim == "adverb-form-gloss" }
        val relabeled = pack.copy(
            sourceCapabilities = pack.sourceCapabilities.map { capability ->
                if (capability.id == gloss.id) capability.copy(supportedClaim = "adverb-paradigm") else capability
            },
        )
        val errors = V2Validator.validate(relabeled).errors
        assertTrue(errors.joinToString("\n"), errors.any { it.contains(gloss.id) && it.contains("does not clear adverb-paradigm") })
        assertTrue(unresolved(relabeled, 7).contains("adverb-form-gloss"))
        assertFalse(unresolved(relabeled, 7).contains("adverb-paradigm"))
        val decision = ordinaryUnitRequirements(pack, pack.unit(7)!!, learner(pack, 7))
        assertTrue(decision.remaining.joinToString("\n"), decision.allowed)
        assertAssignedReviewStillRequired(pack, 7)
    }

    @Test
    fun faultySuffixFormulationIsNotAnApprovedRussianRule() {
        val pack = pack()
        val steps = pack.unit(7)!!.sessions.flatMap { it.steps }
        val lesson = steps.joinToString("\n") { it.title + "\n" + it.body + "\n" + (it.bookDirection ?: "") }
        val checks = pack.items.filter { it.objectiveId == "obj-u7-adverb" }.joinToString("\n") { item ->
            item.prompt + "\n" + item.feedback + "\n" + item.assessment!!.whyAnswerDemonstratesObjective + "\n" +
                item.distractorRationales.joinToString("\n")
        }
        listOf("most adverbs are formed", "adjectival suffixes", "Ukrainian").forEach { banned ->
            assertFalse(lesson.contains(banned))
            assertFalse(checks.contains(banned))
        }
        assertTrue(
            pack.provenanceRecords.any {
                it.objectiveId == "obj-u7-adverb" && it.originalSourceText.contains("most adverbs are formed")
            },
        )
        assertTrue(lesson.contains("the correspondence is not a universal generator"))
    }

    @Test
    fun threeClockPhrasesCannotClearProductiveClockTime() {
        val pack = pack()
        val answers = pack.items.filter { it.objectiveId == "obj-u17-clock-answers" }.map { it.answer }
        assertEquals(listOf("7:15", "7:30", "7:45"), answers.distinct())
        assertTrue(pack.sourceCapabilities.any {
            it.supportedClaim == "productive-clock-time" && it.objectiveId == "obj-u17-clock-productive"
        })
        assertFalse(unresolved(pack, 17).contains("productive-clock-time"))
        assertFalse(unresolved(pack, 17).contains("clock-expression-recognition"))
        val recognition = pack.sourceCapabilities.first { it.supportedClaim == "clock-expression-recognition" }
        val relabeled = pack.copy(
            sourceCapabilities = pack.sourceCapabilities.map { capability ->
                if (capability.id == recognition.id) capability.copy(supportedClaim = "productive-clock-time") else capability
            },
        )
        val errors = V2Validator.validate(relabeled).errors
        assertTrue(
            errors.joinToString("\n"),
            errors.any { it.contains(recognition.id) && it.contains("do not clear productive-clock-time") },
        )
        assertFalse(unresolved(relabeled, 17).contains("productive-clock-time"))
    }

    @Test
    fun calendarDateCompletionCannotConcealTheClockGap() {
        val pack = pack()
        assertTrue(pack.sourceCapabilities.any { it.supportedClaim == "calendar-date" && it.unitNumber == 17 })
        assertFalse(unresolved(pack, 17).contains("calendar-date"))
        assertFalse(unresolved(pack, 17).contains("productive-clock-time"))
        assertEquals("CONTENT_COMPLETE_REVIEW_PENDING", pack.unit(17)!!.readiness)
        val decision = ordinaryUnitRequirements(pack, pack.unit(17)!!, learner(pack, 17))
        assertTrue(decision.remaining.joinToString("\n"), decision.allowed)
        assertAssignedReviewStillRequired(pack, 17)
        assertFalse(decision.remaining.any { it.contains("clock-time") || it.contains("calendar-date") })
        val stripped = pack.copy(
            sourceCapabilities = pack.sourceCapabilities.filter { it.supportedClaim != "productive-clock-time" },
        )
        val flipped = stripped.unit(17)!!.copy(readiness = "CONTENT_COMPLETE_REVIEW_PENDING")
        val stillBlocked = advancementDecision(stripped, flipped, learner(stripped, 17))
        assertFalse(stillBlocked.allowed)
        assertTrue(stillBlocked.remaining.any { it.contains("productive clock-time") })
        assertFalse(stillBlocked.remaining.joinToString("\n"), stillBlocked.remaining.any { it.contains("calendar-date") })
        val ordinaryBlocked = ordinaryUnitRequirements(stripped, flipped, learner(stripped, 17))
        assertFalse(ordinaryBlocked.allowed)
        assertTrue(ordinaryBlocked.remaining.any { it.contains("productive clock-time") })
        assertFalse(ordinaryBlocked.remaining.any { it.contains("calendar-date") })
        assertEquals(ordinaryBlocked.remaining + "Complete the assigned earlier-objective review", stillBlocked.remaining)
    }

    @Test
    fun unit17AdvancementNamesOnlyUnresolvedSourceCapabilities() {
        val pack = pack()
        val live = ordinaryUnitRequirements(pack, pack.unit(17)!!, learner(pack, 17))
        assertEquals("CONTENT_COMPLETE_REVIEW_PENDING", pack.unit(17)!!.readiness)
        assertTrue(live.remaining.joinToString("\n"), live.allowed)
        assertAssignedReviewStillRequired(pack, 17)
        assertEquals(emptyList<String>(), live.remaining)
        assertFalse(unresolved(pack, 17).contains("calendar-date"))
        assertNoResolvedClaimCalledMissing(pack, live.remaining)

        assertEquals(
            listOf("Unit 17 is missing a productive clock-time source capability"),
            V2Validator.unit17SourceBlockers(setOf("productive-clock-time")),
        )
        assertEquals(
            listOf("Unit 17 is missing a calendar-date source capability"),
            V2Validator.unit17SourceBlockers(setOf("calendar-date")),
        )
        assertEquals(
            listOf("Unit 17 is missing productive clock-time and calendar-date source capabilities"),
            V2Validator.unit17SourceBlockers(setOf("productive-clock-time", "calendar-date")),
        )
        assertEquals(emptyList<String>(), V2Validator.unit17SourceBlockers(emptySet()))
        assertFalse(V2Validator.unit17SourceBlockers(setOf("productive-clock-time")).any { it.contains("calendar-date") })
        assertFalse(V2Validator.unit17SourceBlockers(setOf("calendar-date")).any { it.contains("clock-time") })
        assertEquals("obj-u17-clock-productive", V2Validator.ADMITTED_PRODUCTIVE_CLOCK_TIME.single().objectiveId)
        assertEquals("productive-clock-time", V2Validator.ADMITTED_PRODUCTIVE_CLOCK_TIME.single().governedClaim)
    }

    @Test
    fun unrelatedEvidenceCannotImpersonateProductiveClockTime() {
        val pack = pack()
        assertTrue(V2Validator.validate(pack).errors.joinToString("\n"), V2Validator.validate(pack).ok)
        listOf("age", "clock-question", "calendar-date", "clock-expression-recognition").forEach { claim ->
            val impersonated = impersonateProductiveClock(pack, claim, fabricateRecordId = false)
            assertProductiveClockStaysUnresolved(impersonated, claim)
        }
        val fabricated = impersonateProductiveClock(pack, "weekdays", fabricateRecordId = true)
        assertProductiveClockStaysUnresolved(fabricated, "weekdays")
        val recognition = pack.sourceCapabilities.first { it.supportedClaim == "clock-expression-recognition" }
        val relabeled = impersonateProductiveClock(pack, "clock-expression-recognition", fabricateRecordId = false)
        val errors = V2Validator.validate(relabeled).errors
        assertTrue(
            errors.joinToString("\n"),
            errors.any { it.contains(recognition.id) || it.contains("cap-u17-productive-clock-clock-expression-recognition") },
        )
        assertTrue(errors.any { it.contains("do not clear productive-clock-time") })
    }

    @Test
    fun currentQuestionImpersonationUsesOnlyTheRealPinnedPacketWithoutInventingAHost() {
        val live = pack()
        val donor = live.items.single { it.id == "u17-i-question-clock" }
        val source = live.sourceCapabilities.single { it.supportedClaim == "clock-question" }
        val records = donor.assessment!!.relationParticipantIds.map { id -> live.provenanceRecords.single { it.itemId == id } }
        assertTrue(Unit17QuestionPins.matches(live))
        assertTrue(CompleteRuleContextPins.itemMatches(live, donor))
        assertTrue(records.none { it.originalSourceText == source.exactSpan })
        val forged = impersonateProductiveClock(live, "clock-question", fabricateRecordId = false)
        val cloned = forged.provenanceRecords.filter { it.itemId.startsWith("prov-u17-impersonate-clock-question-") }
        assertEquals(records.size, cloned.size)
        records.zip(cloned).forEach { (original, copy) ->
            assertEquals(original.copy(itemId = "prov-u17-impersonate-clock-question-${original.itemId}",
                objectiveId = "obj-u17-clock-answers"), copy)
        }
        val cap = forged.sourceCapabilities.single { it.id == "cap-u17-productive-clock-clock-question" }
        val host = records.first()
        assertEquals(host.sourceId, cap.sourceId)
        assertEquals(host.sourceRevision, cap.sourceRevision)
        assertEquals(host.sourceRecordId, cap.sourceRecordId)
        assertEquals(host.sourceHash, cap.sourceHash)
        assertEquals(host.originalSourceText, cap.exactSpan)
        assertProductiveClockStaysUnresolved(forged, "clock-question")
    }

    @Test
    fun unit17RecordEditsLeaveLaterUnitIdentitiesAndDigestsUnchanged() {
        val pack = pack()
        val stable = listOf(16, 18, 19, 20)
        val digests = stable.associateWith { V2Validator.unitContentReviewDigest(pack, it) }
        assertEquals(PRIOR_CONTENT_DIGESTS, digests)
        val identities = stable.associateWith { number ->
            pack.authoredEnglishRecords.filter { it.unitNumber == number }.map { it.id to it.text }
        }
        val probe = pack.authoredEnglishRecords.first { it.unitNumber == 17 }
        val inserted = pack.copy(
            authoredEnglishRecords = pack.authoredEnglishRecords + probe.copy(
                id = "en-u17-probe-body-0123456789ab",
                text = "Probe insert record.",
            ),
        )
        val deleted = pack.copy(authoredEnglishRecords = pack.authoredEnglishRecords.filter { it.id != probe.id })
        val reordered = pack.copy(authoredEnglishRecords = pack.authoredEnglishRecords.reversed())
        listOf(inserted, deleted, reordered).forEach { mutated ->
            stable.forEach { number ->
                assertEquals(digests.getValue(number), V2Validator.unitContentReviewDigest(mutated, number))
                assertEquals(
                    identities.getValue(number).toSet(),
                    mutated.authoredEnglishRecords.filter { it.unitNumber == number }.map { it.id to it.text }.toSet(),
                )
            }
        }
        assertNotEquals(
            V2Validator.unitContentReviewDigest(pack, 17),
            V2Validator.unitContentReviewDigest(inserted, 17),
        )
        assertFalse(pack.authoredEnglishRecords.any { it.id == "en-u18-108" })
        assertTrue(pack.authoredEnglishRecords.any { it.id == "en-u18-112" })
        val byId = pack.authoredEnglishRecords.groupBy { it.id }
        assertTrue(byId.all { (_, records) -> records.map { it.text }.distinct().size == 1 })
        val process = ProcessBuilder("python3", "-c", "from authored_identity import isolation_errors; errors = isolation_errors(); assert not errors, errors; print('ok')")
            .directory(File(repo(), "catalog"))
            .redirectErrorStream(true)
            .start()
        val output = process.inputStream.bufferedReader().readText()
        assertEquals(output, 0, process.waitFor())
    }

    @Test
    fun unit17SourceAdmissionDoesNotReachOtherUnits() {
        val pack = pack()
        val before = (16..20).associateWith { reachable(pack, it) }
        val clock = "src-wikipedia-date-time-country-1366690592"
        val date = "src-wikipedia-date-notation-russia-1357134172"
        listOf(16, 18, 19, 20).forEach { number ->
            assertFalse(before.getValue(number).second.contains(clock))
            assertFalse(before.getValue(number).second.contains(date))
            assertFalse(before.getValue(number).first.contains("ref-u17-attribution"))
        }
        assertTrue(before.getValue(17).first.contains("ref-u17-attribution"))
        assertTrue(before.getValue(17).second.contains(clock))
        assertTrue(before.getValue(17).second.contains(date))
        val added = pack.copy(
            references = pack.references.map { reference ->
                if (reference.id == "ref-u17-attribution") reference.copy(sourceId = "src-probe-unit17-only") else reference
            },
        )
        listOf(16, 18, 19, 20).forEach { number ->
            assertEquals(before.getValue(number), reachable(added, number))
        }
        assertNotEquals(before.getValue(17), reachable(added, 17))
    }

    @Test
    fun sharedLicenseNoticeDoesNotAnnounceUnitReadiness() {
        val pack = pack()
        val sharedIds = pack.units.filter { it.number in 16..20 }.flatMap { unit ->
            unit.sessions.flatMap { session -> session.steps.flatMap { it.referenceIds } }
                .distinct()
                .map { referenceId -> referenceId to unit.number }
        }.groupBy({ it.first }, { it.second }).filter { (_, units) -> units.distinct().size > 1 }.keys
        assertTrue(sharedIds.contains("ref-u1620-attribution"))
        assertFalse(sharedIds.contains("ref-u17-attribution"))
        pack.references.filter { it.id in sharedIds }.forEach { reference ->
            val text = reference.title + "\n" + reference.summary + "\n" + reference.detail
            listOf("not prepared", "unresolved", "productive", "calendar-date", "probability", "clock-time").forEach { banned ->
                assertFalse(reference.id + " " + banned, text.contains(banned))
            }
        }
        val unit17 = pack.references.first { it.id == "ref-u17-attribution" }
        assertTrue(unit17.detail.contains("calendar-date relationships remain admitted"))
        assertTrue(unit17.detail.contains("bounded recognition"))
        assertTrue(unit17.detail.contains("Productive clock-time is the ReadyRussian Grammar 20 system"))
        assertFalse(unit17.detail.contains("remains unresolved"))
        assertTrue(pack.sourceCapabilities.any { it.unitNumber == 17 && it.supportedClaim == "calendar-date" })
        assertFalse(unresolved(pack, 17).contains("calendar-date"))
        assertFalse(unresolved(pack, 17).contains("productive-clock-time"))
        assertEquals("CONTENT_COMPLETE_REVIEW_PENDING", pack.unit(17)!!.readiness)
        val advanced = ordinaryUnitRequirements(pack, pack.unit(17)!!, learner(pack, 17))
        assertTrue(advanced.remaining.joinToString("\n"), advanced.allowed)
        assertAssignedReviewStillRequired(pack, 17)
        assertFalse(unresolved(pack, 19).contains("probability-dolzhen"))
        assertFalse(
            pack.unit(19)!!.sessions.flatMap { it.steps }.any {
                it.unavailableReason == "Probability use has no admitted frozen span."
            },
        )
        assertEquals("CONTENT_COMPLETE_REVIEW_PENDING", pack.unit(19)!!.readiness)
        assertEquals("DEVELOPMENT", pack.unit(19)!!.installState)
        assertTrue(pack.unitReviews.first { it.unitNumber == 19 }.reviewer.isNullOrBlank())
        val recognition = pack.sourceCapabilities.first { it.supportedClaim == "clock-expression-recognition" }
        val relabeled = pack.copy(
            sourceCapabilities = pack.sourceCapabilities.map { capability ->
                if (capability.id == recognition.id) capability.copy(supportedClaim = "productive-clock-time") else capability
            },
        )
        val errors = V2Validator.validate(relabeled).errors
        assertTrue(errors.joinToString("\n"), errors.any { it.contains("do not clear productive-clock-time") })
        assertFalse(unresolved(relabeled, 17).contains("productive-clock-time"))
    }

    @Test
    fun reviewsMappingsAndReleaseStayUnsigned() {
        val pack = pack()
        assertTrue(pack.units.all { it.installState == "DEVELOPMENT" })
        assertTrue(pack.unitReviews.all {
            it.status == "IMPLEMENTED_PENDING_FINAL_AUDIT" && it.reviewer.isNullOrBlank() && it.reviewedContentDigest.isNullOrBlank()
        })
        assertTrue(pack.units.all { it.mappingStatus == "PENDING_CURRICULUM_REVIEWER" })
        assertEquals(30, pack.ownedEditionMappings.size)
        assertTrue(pack.ownedEditionMappings.all { it.status == "PENDING_CURRICULUM_REVIEWER" && it.reviewer.isNullOrBlank() && it.reviewedContentDigest.isNullOrBlank() })
        val candidate = readAcceptanceCandidate(repo())
        assertEquals("NOT_APPROVED", candidate?.publicRelease)
        assertEquals(false, candidate?.frozen)
        assertEquals(null, candidate?.sourceCommit)
        assertEquals("NOT_RECORDED", candidate?.artifact?.status)
        assertTrue(V2Validator.validate(pack).errors.joinToString("\n"), V2Validator.validate(pack).ok)
    }

    private fun unresolved(pack: V2CurriculumPack, number: Int): Set<String> =
        V2Validator.unresolvedRequiredSourceCapabilities(pack, number).map { it.claim }.toSet()

    /**
     * Rebind an admitted Unit 17 span onto the clock-answer objective and label
     * it productive-clock-time. The rewritten objective, record id, check chain,
     * and supportedClaim are not an independent admission.
     */
    private fun impersonateProductiveClock(
        pack: V2CurriculumPack,
        sourceClaim: String,
        fabricateRecordId: Boolean,
    ): V2CurriculumPack {
        val source = pack.sourceCapabilities.first { it.unitNumber == 17 && it.supportedClaim == sourceClaim }
        // The current question check uses its real pinned English application packet,
        // while the other donors still use direct participants from their public span.
        val questionPacket = sourceClaim == "clock-question"
        val template = if (questionPacket) {
            assertTrue(Unit17QuestionPins.matches(pack))
            pack.items.single { it.id == "u17-i-question-clock" }.also {
                assertEquals(source.objectiveId, it.objectiveId)
                assertTrue(it.assessment!!.independent)
                assertEquals("QUESTION_CLOCK_RECOGNITION", it.assessment.testedFeature)
                assertTrue(CompleteRuleContextPins.itemMatches(pack, it))
            }
        } else {
            pack.items.first { item ->
                item.objectiveId == source.objectiveId &&
                    item.assessment?.independent == true &&
                    item.assessment.relationParticipantIds.any { id ->
                        pack.provenanceRecords.any { it.itemId == id && it.originalSourceText == source.exactSpan }
                    }
            }
        }
        val assessment = template.assessment!!
        val suffix = if (fabricateRecordId) "|fabricated-record" else ""
        val idMap = assessment.relationParticipantIds.associateWith { "prov-u17-impersonate-$sourceClaim-$it" }
        val cloned = assessment.relationParticipantIds.map { oldId ->
            val record = pack.provenanceRecords.first { it.itemId == oldId }
            record.copy(
                itemId = idMap.getValue(oldId),
                objectiveId = "obj-u17-clock-answers",
                sourceRecordId = record.sourceRecordId + suffix,
            )
        }
        val host = if (questionPacket) {
            cloned.first().also {
                assertEquals("src-content-audit-research", it.sourceId)
                assertEquals("ca-rule-context:${template.id}" + suffix, it.sourceRecordId)
            }
        } else cloned.first { it.originalSourceText == source.exactSpan }
        val itemId = "u17-i-impersonate-$sourceClaim"
        val item = template.copy(
            id = itemId,
            objectiveId = "obj-u17-clock-answers",
            assessment = assessment.copy(
                relationParticipantIds = assessment.relationParticipantIds.map { idMap.getValue(it) },
                answerSourceIds = assessment.answerSourceIds.map { idMap[it] ?: it },
                distractorSourceIds = assessment.distractorSourceIds.map { idMap[it] ?: it },
            ),
        )
        val capability = V2SourceCapability(
            id = "cap-u17-productive-clock-$sourceClaim",
            sourceId = host.sourceId,
            sourceRevision = host.sourceRevision,
            sourceRecordId = host.sourceRecordId,
            sourceHash = host.sourceHash,
            exactSpan = host.originalSourceText,
            unitNumber = 17,
            objectiveId = "obj-u17-clock-answers",
            supportedClaim = "productive-clock-time",
        )
        return pack.copy(
            provenanceRecords = pack.provenanceRecords + cloned,
            items = pack.items + item,
            sourceCapabilities = pack.sourceCapabilities + capability,
            units = pack.units.map { unit ->
                if (unit.number != 17) {
                    unit
                } else {
                    unit.copy(
                        objectives = unit.objectives.map { objective ->
                            if (objective.id != "obj-u17-clock-answers") {
                                objective
                            } else {
                                objective.copy(checkItemIds = objective.checkItemIds + itemId)
                            }
                        },
                    )
                }
            },
        )
    }

    private fun assertProductiveClockStaysUnresolved(pack: V2CurriculumPack, sourceClaim: String) {
        val errors = V2Validator.validate(pack).errors
        assertFalse(sourceClaim + "\n" + errors.joinToString("\n"), errors.isEmpty())
        assertTrue(
            sourceClaim + "\n" + errors.joinToString("\n"),
            errors.any { it.contains("cap-u17-productive-clock-$sourceClaim") && it.contains("no independent productive-clock-time admission") },
        )
        assertFalse(unresolved(pack, 17).contains("productive-clock-time"))
        assertFalse(unresolved(pack, 17).contains("calendar-date"))
        assertFalse(unresolved(pack, 17).contains(sourceClaim) && sourceClaim != "productive-clock-time")
        val decision = advancementDecision(pack, pack.unit(17)!!, learner(pack, 17))
        assertFalse(decision.remaining.joinToString("\n"), decision.remaining.any { it.contains("productive clock-time") })
        assertFalse(decision.remaining.joinToString("\n"), decision.remaining.any { it.contains("calendar-date") })
    }

    private fun assertNoResolvedClaimCalledMissing(pack: V2CurriculumPack, remaining: List<String>) {
        val text = remaining.joinToString("\n")
        val unresolved = unresolved(pack, 17)
        if ("calendar-date" !in unresolved) {
            assertFalse(text, text.contains("calendar-date"))
        }
        if ("productive-clock-time" !in unresolved) {
            assertFalse(text, text.contains("clock-time"))
        }
    }

    private fun reachable(pack: V2CurriculumPack, number: Int): Pair<Set<String>, Set<String>> {
        val unit = pack.unit(number)!!
        val references = unit.sessions.flatMap { session -> session.steps.flatMap { it.referenceIds } }.toSet()
        val objectives = unit.objectives.map { it.id }.toSet()
        val sources = mutableSetOf<String>()
        sources += pack.examples.filter { it.objectiveId in objectives }.map { it.sourceId }
        sources += pack.items.filter { it.objectiveId in objectives }.map { it.sourceId }
        sources += pack.references.filter { it.id in references }.map { it.sourceId }
        return references to sources
    }

    /** This unit-local synthetic fixture has no assigned cumulative-review answers. */
    private fun assertAssignedReviewStillRequired(pack: V2CurriculumPack, number: Int) {
        val decision = advancementDecision(pack, pack.unit(number)!!, learner(pack, number))
        assertFalse(decision.allowed)
        assertEquals(listOf("Complete the assigned earlier-objective review"), decision.remaining)
    }

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
        val PRIOR_CONTENT_DIGESTS = mapOf(
            16 to "31893860b30df7c38248356c9014db1a361110f7a8a51ce53ae7ed6fd61bae87",
            18 to "603827ede7e8394cef33bf1af9fb4ef6217a80f0e6624f1bd385e4a0766b69ac",
            19 to "3096baba4cdc192ac7d701f6eedd3c8ca0be12c54ab64efa52b0c25fdbf83a5e",
            20 to "bb408265fb5239daeb86d43c007061bff330e02a0a9afc1aa677526ccf57302b",
        )
    }
}
