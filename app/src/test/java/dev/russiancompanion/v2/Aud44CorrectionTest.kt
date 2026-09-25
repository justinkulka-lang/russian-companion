package dev.russiancompanion.v2

import dev.russiancompanion.release.readAcceptanceCandidate
import dev.russiancompanion.v2.curriculum.ExplicitSourcePairs
import dev.russiancompanion.v2.curriculum.V2CurriculumPack
import dev.russiancompanion.v2.curriculum.V2LearnerView
import dev.russiancompanion.v2.curriculum.V2PackLoader
import dev.russiancompanion.v2.curriculum.V2SourceCapability
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
        assertTrue(unresolved(pack, 7).contains("adverb-paradigm"))
        assertFalse(unresolved(pack, 7).contains("adverb-form-gloss"))
        val gloss = pack.sourceCapabilities.first { it.supportedClaim == "adverb-form-gloss" }
        val relabeled = pack.copy(
            sourceCapabilities = pack.sourceCapabilities.map { capability ->
                if (capability.id == gloss.id) capability.copy(supportedClaim = "adverb-paradigm") else capability
            },
        )
        val errors = V2Validator.validate(relabeled).errors
        assertTrue(errors.joinToString("\n"), errors.any { it.contains(gloss.id) && it.contains("does not clear adverb-paradigm") })
        assertTrue(unresolved(relabeled, 7).contains("adverb-paradigm"))
        val decision = advancementDecision(pack, pack.unit(7)!!, learner(pack, 7))
        assertFalse(decision.allowed)
        assertTrue(decision.remaining.any { it.contains("not prepared") || it.contains("adverb paradigm") })
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
        assertTrue(lesson.contains("An adjective-to-adverb paradigm has no admitted frozen span."))
    }

    @Test
    fun threeClockPhrasesCannotClearProductiveClockTime() {
        val pack = pack()
        val answers = pack.items.filter { it.objectiveId == "obj-u17-clock-answers" }.map { it.answer }
        assertEquals(listOf("7:15", "7:30", "7:45"), answers.distinct())
        assertTrue(pack.sourceCapabilities.none { it.supportedClaim == "productive-clock-time" })
        assertTrue(unresolved(pack, 17).contains("productive-clock-time"))
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
        assertTrue(unresolved(relabeled, 17).contains("productive-clock-time"))
    }

    @Test
    fun calendarDateCompletionCannotConcealTheClockGap() {
        val pack = pack()
        assertTrue(pack.sourceCapabilities.any { it.supportedClaim == "calendar-date" && it.unitNumber == 17 })
        assertFalse(unresolved(pack, 17).contains("calendar-date"))
        assertTrue(unresolved(pack, 17).contains("productive-clock-time"))
        assertEquals("NOT_PREPARED", pack.unit(17)!!.readiness)
        val decision = advancementDecision(pack, pack.unit(17)!!, learner(pack, 17))
        assertFalse(decision.remaining.joinToString("\n"), decision.allowed)
        val flipped = pack.unit(17)!!.copy(readiness = "CONTENT_COMPLETE_REVIEW_PENDING")
        val stillBlocked = advancementDecision(pack, flipped, learner(pack, 17))
        assertFalse(stillBlocked.allowed)
        assertTrue(stillBlocked.remaining.any { it.contains("productive clock-time") })
        assertFalse(stillBlocked.remaining.joinToString("\n"), stillBlocked.remaining.any { it.contains("calendar-date") })
    }

    @Test
    fun unit17AdvancementNamesOnlyUnresolvedSourceCapabilities() {
        val pack = pack()
        val live = advancementDecision(pack, pack.unit(17)!!, learner(pack, 17))
        assertEquals("NOT_PREPARED", pack.unit(17)!!.readiness)
        assertFalse(live.allowed)
        assertEquals(
            listOf(
                "This unit is not prepared",
                "Unit 17 is missing a productive clock-time source capability",
            ),
            live.remaining,
        )
        assertFalse(unresolved(pack, 17).contains("calendar-date"))
        assertNoResolvedClaimCalledMissing(pack, live.remaining)

        val bothMissing = withoutClaim(pack, "calendar-date")
        val both = advancementDecision(bothMissing, bothMissing.unit(17)!!, learner(bothMissing, 17))
        assertTrue(unresolved(bothMissing, 17).containsAll(listOf("productive-clock-time", "calendar-date")))
        assertFalse(both.allowed)
        assertTrue(
            both.remaining.joinToString("\n"),
            both.remaining.contains("Unit 17 is missing productive clock-time and calendar-date source capabilities"),
        )
        assertNoResolvedClaimCalledMissing(bothMissing, both.remaining)

        val productive = withResolvedProductiveClock(pack)
        assertTrue(V2Validator.validate(productive).errors.joinToString("\n"), V2Validator.validate(productive).ok)
        assertFalse(unresolved(productive, 17).contains("productive-clock-time"))
        assertFalse(unresolved(productive, 17).contains("calendar-date"))
        val neither = advancementDecision(productive, productive.unit(17)!!, learner(productive, 17))
        assertFalse(neither.remaining.joinToString("\n"), neither.remaining.any { it.contains("source capability") })
        assertNoResolvedClaimCalledMissing(productive, neither.remaining)

        val dateOnly = withoutClaim(productive, "calendar-date")
        assertTrue(V2Validator.validate(dateOnly).errors.joinToString("\n"), V2Validator.validate(dateOnly).ok)
        assertEquals(setOf("calendar-date"), unresolved(dateOnly, 17))
        val date = advancementDecision(dateOnly, dateOnly.unit(17)!!, learner(dateOnly, 17))
        assertFalse(date.allowed)
        assertTrue(date.remaining.contains("Unit 17 is missing a calendar-date source capability"))
        assertFalse(date.remaining.joinToString("\n"), date.remaining.any { it.contains("clock-time") })
        assertNoResolvedClaimCalledMissing(dateOnly, date.remaining)
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
        assertTrue(unit17.detail.contains("Productive clock-time generation remains unresolved"))
        assertTrue(pack.sourceCapabilities.any { it.unitNumber == 17 && it.supportedClaim == "calendar-date" })
        assertFalse(unresolved(pack, 17).contains("calendar-date"))
        assertTrue(unresolved(pack, 17).contains("productive-clock-time"))
        assertEquals("NOT_PREPARED", pack.unit(17)!!.readiness)
        val blocked = advancementDecision(pack, pack.unit(17)!!, learner(pack, 17))
        assertFalse(blocked.remaining.joinToString("\n"), blocked.allowed)
        assertTrue(unresolved(pack, 19).contains("probability-dolzhen"))
        assertTrue(
            pack.unit(19)!!.sessions.flatMap { it.steps }.any {
                it.unavailableReason == "Probability use has no admitted frozen span."
            },
        )
        val recognition = pack.sourceCapabilities.first { it.supportedClaim == "clock-expression-recognition" }
        val relabeled = pack.copy(
            sourceCapabilities = pack.sourceCapabilities.map { capability ->
                if (capability.id == recognition.id) capability.copy(supportedClaim = "productive-clock-time") else capability
            },
        )
        val errors = V2Validator.validate(relabeled).errors
        assertTrue(errors.joinToString("\n"), errors.any { it.contains("do not clear productive-clock-time") })
        assertTrue(unresolved(relabeled, 17).contains("productive-clock-time"))
    }

    @Test
    fun reviewsMappingsAndReleaseStayUnsigned() {
        val pack = pack()
        assertTrue(pack.units.all { it.installState == "DEVELOPMENT" })
        assertTrue(pack.unitReviews.all {
            it.status == "PENDING_QUALIFIED_REVIEW" && it.reviewer.isNullOrBlank() && it.reviewedContentDigest.isNullOrBlank()
        })
        assertTrue(pack.units.all { it.mappingStatus == "PENDING_CURRICULUM_REVIEWER" })
        assertTrue(pack.ownedEditionMappings.isEmpty())
        val candidate = readAcceptanceCandidate(repo())
        assertEquals("NOT_APPROVED", candidate?.publicRelease)
        assertEquals(false, candidate?.frozen)
        assertEquals(null, candidate?.sourceCommit)
        assertEquals("NOT_RECORDED", candidate?.artifact?.status)
        assertTrue(V2Validator.validate(pack).errors.joinToString("\n"), V2Validator.validate(pack).ok)
    }

    private fun unresolved(pack: V2CurriculumPack, number: Int): Set<String> =
        V2Validator.unresolvedRequiredSourceCapabilities(pack, number).map { it.claim }.toSet()

    private fun withoutClaim(pack: V2CurriculumPack, claim: String): V2CurriculumPack =
        pack.copy(sourceCapabilities = pack.sourceCapabilities.filter { it.supportedClaim != claim })

    /**
     * A defect-free productive-clock-time capability on an admitted age span.
     * The frozen clock span stays bound to recognition and cannot clear the claim.
     */
    private fun withResolvedProductiveClock(pack: V2CurriculumPack): V2CurriculumPack {
        val template = pack.items.first { it.id == "u17-i-age-1" }
        val assessment = template.assessment!!
        val idMap = assessment.relationParticipantIds.associateWith { "prov-u17-productive-clock-$it" }
        val cloned = assessment.relationParticipantIds.map { oldId ->
            val record = pack.provenanceRecords.first { it.itemId == oldId }
            record.copy(
                itemId = idMap.getValue(oldId),
                objectiveId = "obj-u17-clock-answers",
                sourceRecordId = record.sourceRecordId + "|productive-clock-fixture",
            )
        }
        val host = cloned.first()
        assertNotEquals(ExplicitSourcePairs.clockSpan, host.originalSourceText)
        val itemId = "u17-i-productive-clock-1"
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
            id = "cap-u17-productive-clock-fixture",
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
            16 to "6797fdb9c88927be744444861238a7eb3c2574e25b059df57dde02f45a8015c5",
            18 to "6e6faca384de88b45092fcbdfe63d86a244f4bcc8459f290c2821d05faa083ae",
            19 to "7daabf2fa0295a3f54d1b38603d57d1b888555c2ca2b5d04af25af5b09654d73",
            20 to "7eca8b47f1286dc2b1718c03f4cefdfefe96f76979d9bf44616501e00faa71f3",
        )
    }
}
