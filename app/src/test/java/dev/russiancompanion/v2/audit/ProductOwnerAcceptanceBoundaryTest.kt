package dev.russiancompanion.v2.audit

import dev.russiancompanion.release.readAcceptanceCandidate
import dev.russiancompanion.v2.curriculum.AUTHORIZED_FINAL_AUDIT_DIRECTORY
import dev.russiancompanion.v2.curriculum.V2Validator
import dev.russiancompanion.v2.curriculum.advancementDecision
import dev.russiancompanion.v2.curriculum.unit
import dev.russiancompanion.v2.review.governedMappingFacts
import dev.russiancompanion.v2.review.structuralMappingWorksheet
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Product Owner acceptance of edition identity, page boundaries, and the
 * Units 27, 29, and 30 pedagogical boundaries is not a qualified-reviewer
 * signature. These checks fail if that acceptance is recorded as mapping
 * approval, review approval, installation, freeze, or public release.
 */
class ProductOwnerAcceptanceBoundaryTest {
    @Test
    fun productOwnerAcceptanceCannotMasqueradeAsQualifiedReview() {
        val root = root()
        val pack = pack()
        val acceptance = File(root, "docs/governance/OWNER_MAPPING_AND_VARIANCE_ACCEPTANCE.md").readText()
        assertTrue(acceptance.contains("Status: APPLIED_NOT_QUALIFIED_REVIEW"))
        assertTrue(acceptance.contains("9780140120417"))
        assertTrue(acceptance.contains("Binary-identity continuity is not established") || acceptance.contains("not established by binary identity"))
        assertTrue(acceptance.contains("not evidence of altered instructional content"))
        assertTrue(acceptance.contains("PENDING_CURRICULUM_REVIEWER"))
        assertFalse(acceptance.contains("MAPPING_APPROVED"))
        assertTrue(acceptance.contains("Product Owner signature:"))
        assertTrue(acceptance.contains("Reviewer signature:"))

        val candidate = readAcceptanceCandidate(root)
        assertEquals("NOT_APPROVED", candidate?.publicRelease)
        assertEquals(false, candidate?.frozen)
        assertEquals(null, candidate?.sourceCommit)
        assertEquals("NOT_RECORDED", candidate?.artifact?.status)
        assertFalse(File(root, AUTHORIZED_FINAL_AUDIT_DIRECTORY).exists())

        val pages = mapOf(
            21 to "257-268",
            22 to "269-282",
            23 to "283-293",
            24 to "294-304",
            25 to "305-319",
            26 to "320-331",
        )
        val obsolete = mapOf(
            21 to "259-270",
            22 to "271-284",
            23 to "285-295",
            24 to "296-306",
            25 to "307-321",
        )
        pages.forEach { (number, range) ->
            val facts = governedMappingFacts(number)
            assertEquals(range, "${facts.printedPageStart}-${facts.printedPageEnd}")
            if (number in 21..25) {
                assertTrue(pack.unit(number)!!.mappingNote.contains(range))
            }
        }
        obsolete.forEach { (number, range) ->
            assertFalse(pack.unit(number)!!.mappingNote.contains(range))
        }
        assertTrue(pack.unit(27)!!.mappingNote.contains("Active participle formation is not in this lesson"))
        assertTrue(pack.unit(29)!!.mappingNote.contains("shipped participial-phrase punctuation"))
        assertFalse(pack.unit(29)!!.mappingNote.contains("optional punctuation"))
        assertFalse(pack.unit(30)!!.mappingNote.contains("unresolved authority variance remains unactivated"))

        (1..30).forEach { number ->
            val unit = pack.unit(number)!!
            val review = pack.unitReviews.single { it.unitNumber == number }
            val facts = governedMappingFacts(number)
            assertEquals("DEVELOPMENT", unit.installState)
            assertEquals("CONTENT_COMPLETE_REVIEW_PENDING", unit.readiness)
            assertEquals("PENDING_CURRICULUM_REVIEWER", unit.mappingStatus)
            assertEquals("IMPLEMENTED_PENDING_FINAL_AUDIT", review.status)
            assertTrue(review.reviewer.isNullOrBlank())
            assertTrue(review.reviewerQualification.isNullOrBlank())
            assertTrue(review.reviewedContentDigest.isNullOrBlank())
            assertFalse(review.status == "APPROVED" || review.status == "OWNER_ACCEPTED")
            assertEquals("UNRESOLVED", facts.isbnObservation)
            assertNull(facts.isbn)
            assertEquals("PENDING_CURRICULUM_REVIEWER", facts.mappingDisposition)
            val worksheet = structuralMappingWorksheet(pack, number)
            assertFalse(worksheet.contains("9780140120417"))
            assertFalse(worksheet.contains("\tAPPROVED\t"))
            assertTrue(worksheet.contains("PENDING_CURRICULUM_REVIEWER"))
            val row = worksheet.lines().first { it.startsWith("unit-$number\t") || it.startsWith("obj-u$number-") }.split("\t")
            assertEquals("", row[9])
            assertEquals("", row[10])
            val notes = facts.observedEditionNotes.orEmpty()
            assertTrue(notes.contains("Binary-identity continuity is not established"))
            assertTrue(notes.contains("not evidence of altered instructional content"))
            assertFalse(notes.contains("9780140120417"))
        }

        listOf(27, 29, 30).forEach { number ->
            assertTrue(V2Validator.activatedAuthorityVariance(number))
            assertFalse(V2Validator.authorityVarianceApplies(number))
            val unit = pack.unit(number)!!
            val decision = advancementDecision(pack, unit, learner(unit))
            assertFalse(decision.remaining.any { it.contains("unresolved authority variance") })
        }
        assertFalse(V2Validator.activatedAuthorityVariance(26))

        val facts = governedMappingFacts(21)
        val rejected = evaluateMappingAcceptance(
            MappingAcceptanceAttempt(
                unitNumber = 21,
                disposition = "MAPPING_APPROVED",
                isbn = "",
                editionTitle = facts.editionTitle!!,
                printedPageStart = facts.printedPageStart,
                printedPageEnd = facts.printedPageEnd,
                mappingPackageDigest = dev.russiancompanion.v2.review.mappingPackageDigest(facts),
                ownedCopyVerified = "yes",
                ownerName = "Justin Kulka",
                date = "2026-10-02",
                signature = "written-disposition",
                support = DecisionSupport.OWNER_SIGNATURE,
            ),
        )
        assertFalse(rejected.accepted)
        assertTrue(rejected.productionUnchanged)
        assertTrue(rejected.defects.contains("mapping remains PENDING_CURRICULUM_REVIEWER"))
        assertEquals("PENDING_CURRICULUM_REVIEWER", pack.unit(21)!!.mappingStatus)
    }

    private fun learner(unit: dev.russiancompanion.v2.curriculum.V2Unit) =
        dev.russiancompanion.v2.curriculum.V2LearnerView(
            onboardingCompleted = true,
            currentUnitNumber = unit.number,
            startingPoint = dev.russiancompanion.v2.curriculum.V2StartingPoint.NEW_TO_CYRILLIC,
            hasPenguin = true,
            hasAnki = false,
            hasBinder = false,
            hasTutor = false,
            dailyMinutes = 25,
            transliterationEnabled = true,
            currentSessionId = null,
            currentStepIndex = 0,
            completedStepIds = emptySet(),
            correctItemIds = emptySet(),
            bookAttestedUnits = emptySet(),
            completedUnitNumbers = emptySet(),
        )

    private fun pack() = dev.russiancompanion.v2.curriculum.V2PackLoader.parse(
        File(root(), "app/src/main/assets/content/v2-curriculum.json").readText(),
    )

    private fun root(): File = listOf(File("."), File("..")).first { File(it, "app/src/main").isDirectory }
}
