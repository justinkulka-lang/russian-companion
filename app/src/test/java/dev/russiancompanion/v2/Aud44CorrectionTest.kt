package dev.russiancompanion.v2

import dev.russiancompanion.release.readAcceptanceCandidate
import dev.russiancompanion.v2.curriculum.V2CurriculumPack
import dev.russiancompanion.v2.curriculum.V2LearnerView
import dev.russiancompanion.v2.curriculum.V2PackLoader
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
            16 to "1b483e3b93f57c2f7b70be9e544698f6946cb9b2152f11cd02447355ad307efb",
            18 to "405c2ea8e232528baf6d2b4a078c7df0f93c19383433fa9409f5b6244789041a",
            19 to "f1d0f31a2e04e1eb880c09910bc1162e8f43707cdd8c028f2aea2dd32fc9a855",
            20 to "b2f23ebd7c015fd2d8b5799bf1a620ca037ef9bdf86d5670e6346fedc4249213",
        )
    }
}
