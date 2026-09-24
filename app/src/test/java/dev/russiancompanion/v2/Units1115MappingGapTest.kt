package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.V2OwnedEditionMapping
import dev.russiancompanion.v2.curriculum.V2PackLoader
import dev.russiancompanion.v2.curriculum.V2UnitReviewRecord
import dev.russiancompanion.v2.curriculum.V2Validator
import dev.russiancompanion.v2.curriculum.unit
import dev.russiancompanion.v2.review.governedMappingFacts
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.security.MessageDigest

class Units1115MappingGapTest {
    private val pages = mapOf(
        11 to "122-132",
        12 to "133-148",
        13 to "149-161",
        14 to "162-170",
        15 to "171-187",
    )

    private val authorityTitles = mapOf(
        11 to "Past tense and reflexive verbs",
        12 to "Future, aspect introduction, dative case, additional spelling rules, and prepositional plural",
        13 to "Aspect in the past, tense use, infinitive aspect, and reported speech",
        14 to "Aspect in the future and impersonal constructions",
        15 to "Requests, the imperative, and consolidation of aspect use",
    )

    private val preservedContentDigests = mapOf(
        1 to "9adca20544f28770210fe3029c2e0e4c437e3b409216e9288227c316e9ebf61a",
        2 to "d51310f9ac1596ab50d2c5df7dffd9b10db23d712875680dbfb942bf577e056b",
        3 to "deadcf10b8af7efca034b8d89921412f309d291180c09c4af490eb132b1bbb86",
        4 to "5a9be201954e4a4c37188ed7aca9fae1ee67e20622c7134271c1a68cd520e59c",
        5 to "9470cc61d50236425fb01771a4d20788c88d6c98f8a8694420609f86d7b9710d",
        6 to "fa6501022bd7ff4a2eca1e47c0ad488d96254cab0eafbecc61a8420835a3ab9c",
        7 to "56f1f67b9bda3e58eedd8f7bbb390c23fce2cff39b36a5d964a028cc2eeb33d1",
        8 to "e1dd456abf8a7b961f2f8471f35d4178961a53147c354f3e410e88d75f025c78",
        9 to "d0f135c408fc429392cf695a8122134013fca69637a4f9c1587db3fec6b3c65f",
        10 to "6845b398da6ad901b06443dd0f56ee6ace4ba4e2da6af9e36d33934dc286c526",
    )

    @Test
    fun unitsElevenThroughFifteenRecordPendingMappingAndReviewPendingContent() {
        val pack = pack()
        assertTrue(V2Validator.validate(pack).errors.joinToString("\n"), V2Validator.validate(pack).ok)
        (11..15).forEach { number ->
            val unit = pack.unit(number)!!
            assertEquals(authorityTitles.getValue(number), unit.title)
            assertEquals("DEVELOPMENT", unit.installState)
            assertEquals("CONTENT_COMPLETE_REVIEW_PENDING", unit.readiness)
            assertEquals("PENDING_CURRICULUM_REVIEWER", unit.mappingStatus)
            assertEquals(number, unit.penguinLesson)
            assertTrue(unit.mappingNote.contains(pages.getValue(number)))
            assertTrue(unit.mappingNote.contains("PENDING_CURRICULUM_REVIEWER"))
            assertFalse(unit.mappingNote.contains("APPROVED"))
            assertTrue(unit.sessions.isNotEmpty())
            assertTrue(unit.objectives.any { it.kind == "RECOGNITION" })
            val facts = governedMappingFacts(number)
            assertEquals(pages.getValue(number), "${facts.printedPageStart}-${facts.printedPageEnd}")
            assertEquals(number, facts.proposedLessonNumber)
            assertEquals("PENDING_CURRICULUM_REVIEWER", facts.mappingDisposition)
            assertNull(facts.isbn)
            assertEquals("UNRESOLVED", facts.isbnObservation)
            assertTrue(pack.ownedEditionMappings.none { it.unitNumber == number })
            assertTrue(pack.provenanceRecords.any { it.unitNumber == number })
        }
        assertTrue(pack.units.drop(25).all { it.installState == "DEVELOPMENT" && it.readiness == "NOT_PREPARED" && it.sessions.isNotEmpty() })
        assertFalse(File(repo(), "app/src/main/AndroidManifest.xml").readText().contains("android.permission.INTERNET"))
        assertEquals(0, pack.admissions.generatedRussianWords)
        assertEquals(0, pack.admissions.penguinProseAssets)
    }

    @Test
    fun preservedUnitDigestsDoNotMoveWhenTheLaterGapIsRecorded() {
        val pack = pack()
        preservedContentDigests.forEach { (number, digest) ->
            assertEquals("unit $number", digest, V2Validator.unitContentReviewDigest(pack, number))
        }
        val shifted = pack.copy(
            units = pack.units.map { unit ->
                if (unit.number != 11) unit else unit.copy(mappingNote = unit.mappingNote + " extra gap note")
            },
        )
        preservedContentDigests.keys.forEach { number ->
            assertEquals(V2Validator.unitContentReviewDigest(pack, number), V2Validator.unitContentReviewDigest(shifted, number))
        }
        assertFalse(V2Validator.unitContentReviewDigest(pack, 11) == V2Validator.unitContentReviewDigest(shifted, 11))
    }

    @Test
    fun anEmptyUnitCannotBeMarkedContentComplete() {
        val pack = pack()
        val claimed = pack.unit(26)!!.copy(
            installState = "DEVELOPMENT",
            readiness = "CONTENT_COMPLETE_REVIEW_PENDING",
            mappingStatus = "PENDING_CURRICULUM_REVIEWER",
            sessions = emptyList(),
            objectives = emptyList(),
        )
        val errors = V2Validator.validate(replaceUnit(pack, claimed)).errors
        assertTrue(errors.joinToString("\n"), errors.any { it.contains("empty sessions or objectives do not count") })
    }

    @Test
    fun aSkippedRecognitionObjectiveCannotSatisfyContentComplete() {
        val pack = pack()
        val claimed = pack.unit(12)!!.copy(
            installState = "DEVELOPMENT",
            readiness = "CONTENT_COMPLETE_REVIEW_PENDING",
            mappingStatus = "PENDING_CURRICULUM_REVIEWER",
            sessions = pack.unit(6)!!.sessions,
            objectives = emptyList(),
        )
        val errors = V2Validator.validate(replaceUnit(pack, claimed)).errors
        assertTrue(
            errors.joinToString("\n"),
            errors.any { it.contains("book direction does not replace instruction") || it.contains("empty sessions or objectives") },
        )
    }

    @Test
    fun fakeApprovalCannotInstallOrMapUnitEleven() {
        val pack = pack()
        val installed = pack.unit(11)!!.copy(installState = "INSTALLED", readiness = "INSTALLABLE", mappingStatus = "APPROVED")
        val installErrors = V2Validator.validate(replaceUnit(pack, installed)).errors
        assertTrue(installErrors.joinToString("\n"), installErrors.any { it.contains("Only Units 1-2 may be installed") || it.contains("NOT_PREPARED") || it.contains("APPROVED") })
        val fakeMapping = V2OwnedEditionMapping(
            id = "map-fake-unit-11",
            unitNumber = 11,
            status = "APPROVED",
            reviewer = "Unsigned",
            reviewerQualification = "",
            reviewedContentDigest = "not-the-unit-digest",
            isbnObservation = "UNRESOLVED",
        )
        val mappingErrors = V2Validator.validate(pack.copy(ownedEditionMappings = pack.ownedEditionMappings + fakeMapping)).errors
        assertTrue(
            mappingErrors.joinToString("\n"),
            mappingErrors.any { it.contains("map-fake-unit-11") && (it.contains("blank page range") || it.contains("edition identity") || it.contains("qualified reviewer") || it.contains("invented ISBN") || it.contains("linguistic review")) },
        )
        val fakeReview = V2UnitReviewRecord(
            id = "rev-fake-unit-11",
            unitNumber = 11,
            status = "APPROVED",
            reviewer = "",
            reviewerQualification = "",
            reviewedContentDigest = "not-the-unit-digest",
        )
        val reviewErrors = V2Validator.validate(pack.copy(unitReviews = pack.unitReviews + fakeReview)).errors
        assertFalse(reviewErrors.isEmpty())
    }

    @Test
    fun alteredFrozenIdentityStillCannotValidateAQualifyingCheck() {
        val pack = pack()
        val seed = pack.items.first { it.id == "u6-i-third-1" }
        assertTrue(V2Validator.featureValid(pack, seed))
        val cueId = seed.assessment!!.relationParticipantIds[0]
        val replacement = "different frozen content that keeps the truncated id"
        val mutated = pack.copy(
            provenanceRecords = pack.provenanceRecords.map { record ->
                if (record.itemId == cueId) {
                    record.copy(originalSourceText = replacement, sourceHash = sha256(replacement))
                } else {
                    record
                }
            },
        )
        val rewritten = seed.copy(
            assessment = seed.assessment.copy(
                whyAnswerDemonstratesObjective = "The contrast is the same word in the shown line.",
            ),
        )
        assertFalse(V2Validator.featureValid(mutated, rewritten))
        val wrongSource = pack.copy(
            provenanceRecords = pack.provenanceRecords.map { record ->
                if (record.itemId == cueId) record.copy(sourceId = "src-openrussian-50e210c4") else record
            },
        )
        assertFalse(V2Validator.featureValid(wrongSource, rewritten))
        val wrongRevision = pack.copy(
            provenanceRecords = pack.provenanceRecords.map { record ->
                if (record.itemId == cueId) record.copy(sourceRevision = "not-the-frozen-oldid") else record
            },
        )
        assertFalse(V2Validator.featureValid(wrongRevision, rewritten))
        assertEquals("answer-in-cue", V2Validator.qualifyingOverlap("Я хочу его котов", "его"))
        val english = seed.copy(prompt = "Which admitted form is recorded for the feminine?", promptLetter = "the feminine")
        assertTrue(V2Validator.isDirectEnglishMatch(english))
        assertFalse(V2Validator.featureValid(pack, english))
    }

    private fun replaceUnit(pack: dev.russiancompanion.v2.curriculum.V2CurriculumPack, unit: dev.russiancompanion.v2.curriculum.V2Unit) =
        pack.copy(units = pack.units.map { if (it.number == unit.number) unit else it })

    private fun sha256(text: String): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(text.toByteArray(Charsets.UTF_8))
        return digest.joinToString("") { byte -> "%02x".format(byte) }
    }

    private fun pack() = V2PackLoader.parse(File(repo(), "app/src/main/assets/content/v2-curriculum.json").readText())

    private fun repo(): File = listOf(File("."), File("..")).first { File(it, "app/src/main").isDirectory }
}
