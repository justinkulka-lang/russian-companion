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
        1 to "161a578ba556d709b621b61f138ad36290da57869a87ed57979e0bc7058b0aa7",
        2 to "4fd67bff0431a11416115de023e076c52b7879304fc4a98b508d871170703e9c",
        3 to "29121dfbfb6a2e07d413e86b01462a76a62b9659f52597300ccd91c569224d7c",
        4 to "9f255afab766ef14cbf8ef2688a512f74c00d3c45efc772d6b4506a431a21121",
        5 to "2d0bd5ce23de6a68c10be5a349554b768f551faaf0a90c3432bd5d6e00d1cd73",
        6 to "ca4ad8b37d632b7d9c9008c387a1efe57938026c96275995e22b5c3e24a843a5",
        7 to "0a4df65c918a87854c5088faf14ad2f6b9338d0b395452429ce6fd2a4caa1a6d",
        8 to "cd04cb2a10f58b72f853bf5070f7e436d65ce601b979ab8337eb1e4f0c1329c7",
        9 to "4b8f99197e95f754e7178c381cf6b143a958f2064a7dac230f3f6f5cbe200836",
        10 to "0937a4c6577bff3eca3a0570f0213631fc45b6941d1f400e7dff93212f31f055",
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
            assertTrue(pack.ownedEditionMappings.filter { it.unitNumber == number }.all { it.status == "PENDING_CURRICULUM_REVIEWER" && it.reviewer.isNullOrBlank() && it.reviewedContentDigest.isNullOrBlank() })
            assertEquals(1, pack.ownedEditionMappings.count { it.unitNumber == number })
            assertTrue(pack.provenanceRecords.any { it.unitNumber == number })
        }
        assertTrue(pack.units.drop(25).all { it.installState == "DEVELOPMENT" && it.sessions.isNotEmpty() })
        assertEquals("CONTENT_COMPLETE_REVIEW_PENDING", pack.unit(28)!!.readiness)
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
        val shown = installErrors.joinToString("\n")
        assertTrue(shown, installErrors.any { it.contains("review chain is not OWNER_ACCEPTED") })
        assertTrue(shown, installErrors.any { it.contains("mapping record does not resolve") })
        assertTrue(shown, installErrors.any { it.contains("requires approved review and mapping") })
        assertFalse(installErrors.any { it.contains("Only Units 1-2 may be installed") })
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
