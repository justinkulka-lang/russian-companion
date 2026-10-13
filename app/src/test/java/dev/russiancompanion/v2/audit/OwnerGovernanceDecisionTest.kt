package dev.russiancompanion.v2.audit

import dev.russiancompanion.release.computeReleaseSourceDigest
import dev.russiancompanion.release.readAcceptanceCandidate
import dev.russiancompanion.v2.curriculum.AUTHORIZED_FINAL_AUDIT_DIRECTORY
import dev.russiancompanion.v2.curriculum.V2Validator
import dev.russiancompanion.v2.curriculum.unit
import dev.russiancompanion.v2.review.buildUnitReviewPackage
import dev.russiancompanion.v2.review.governedMappingFacts
import dev.russiancompanion.v2.review.mappingPackageDigest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.security.MessageDigest

class OwnerGovernanceDecisionTest {
    @Test
    fun unresolvedSourceCapabilitiesStayEmptyAndVarianceUnitsStayUnprepared() {
        val pack = pack()
        val unresolved = (1..30).flatMap { number ->
            V2Validator.unresolvedRequiredSourceCapabilities(pack, number).map { "$number:${it.claim}" }
        }
        assertEquals(emptyList<String>(), unresolved)
        assertEquals(
            "7b8428298d5896c2c50ece4191bc39f87ddb45e07320fbdf3ff14d0e1ab97c88",
            V2Validator.contentReviewDigest(pack),
        )
        val readiness = pack.units.groupBy { it.readiness }.mapValues { it.value.size }
        assertEquals(30, readiness["CONTENT_COMPLETE_REVIEW_PENDING"])
        assertEquals(null, readiness["NOT_PREPARED"])
        listOf(27, 29, 30).forEach { number ->
            val unit = pack.unit(number)!!
            assertEquals("CONTENT_COMPLETE_REVIEW_PENDING", unit.readiness)
            assertEquals("DEVELOPMENT", unit.installState)
            assertEquals("PENDING_CURRICULUM_REVIEWER", unit.mappingStatus)
            assertFalse(V2Validator.authorityVarianceApplies(number))
            assertTrue(V2Validator.activatedAuthorityVariance(number))
            assertEquals(emptyList<String>(), V2Validator.contentCompletenessErrors(pack, unit))
            val blockers = V2Validator.unresolvedLateUnitAdvancementBlockers(pack, unit)
            assertFalse(blockers.joinToString(), blockers.any { it.contains("unresolved authority variance") })
            assertFalse(blockers.any { it.contains("source capabilit") })
            val promoted = pack.copy(
                units = pack.units.map { current ->
                    if (current.number == number) current.copy(readiness = "INSTALLABLE") else current
                },
            )
            val errors = V2Validator.validate(promoted).errors
            assertFalse(
                errors.joinToString("\n"),
                errors.any { it.contains("unresolved authority variance") },
            )
            assertTrue(errors.any { it.contains("approved review and mapping") })
            assertFalse(V2Validator.authorityVarianceApplies(number))
        }
    }

    @Test
    fun blankMismatchedAndNonOwnerDecisionsFailClosedWithoutChangingProduction() {
        val pack = pack()
        val root = root()
        val liveDigest = computeReleaseSourceDigest(root)
        val blank = evaluateVarianceDecision(
            pack,
            root,
            VarianceDecisionAttempt(
                unitNumber = 27,
                disposition = "",
                ownerName = "",
                date = "",
                signature = "",
                contentDigest = "",
                mappingPackageDigest = "",
                repositorySourceDigest = "",
                support = DecisionSupport.OWNER_SIGNATURE,
            ),
        )
        assertFalse(blank.accepted)
        assertTrue(blank.defects.contains("variance disposition is blank or incomplete"))
        assertTrue(blank.defects.contains("owner name is blank"))
        assertTrue(blank.defects.contains("owner date is blank or incomplete"))
        assertTrue(blank.defects.contains("owner signature is blank"))
        assertTrue(blank.productionUnchanged)
        listOf(
            DecisionSupport.CI_SUCCESS,
            DecisionSupport.COUNTER,
            DecisionSupport.REVIEW_STATUS,
            DecisionSupport.MODEL_NAME,
        ).forEach { support ->
            val rejected = evaluateVarianceDecision(
                pack,
                root,
                signedVariance(pack, root, 27, "ACTIVATE").copy(support = support),
            )
            assertFalse(rejected.accepted)
            assertTrue(rejected.defects.contains("non-owner signal cannot activate a variance"))
            assertTrue(V2Validator.activatedAuthorityVariance(27))
            assertFalse(V2Validator.authorityVarianceApplies(27))
        }
        val mismatched = evaluateVarianceDecision(
            pack,
            root,
            signedVariance(pack, root, 29, "REVISE").copy(contentDigest = "0".repeat(64)),
        )
        assertFalse(mismatched.accepted)
        assertTrue(mismatched.defects.contains("content digest does not match the packaged unit"))
        val signed = evaluateVarianceDecision(pack, root, signedVariance(pack, root, 30, "ACTIVATE"))
        assertTrue(signed.defects.joinToString(), signed.accepted)
        assertTrue(signed.productionUnchanged)
        assertTrue(V2Validator.activatedAuthorityVariance(27))
        assertTrue(V2Validator.activatedAuthorityVariance(29))
        assertTrue(V2Validator.activatedAuthorityVariance(30))
        assertFalse(V2Validator.authorityVarianceApplies(30))
        assertEquals("CONTENT_COMPLETE_REVIEW_PENDING", pack.unit(30)!!.readiness)
        assertEquals("PENDING_CURRICULUM_REVIEWER", pack.unit(30)!!.mappingStatus)
        assertEquals(liveDigest, computeReleaseSourceDigest(root))
    }

    @Test
    fun mappingAcceptanceFailsForInventedOrMismatchedFacts() {
        val facts = governedMappingFacts(27)
        val digest = mappingPackageDigest(facts)
        val edition = checkNotNull(facts.editionTitle)
        val inventedIsbn = evaluateMappingAcceptance(
            signedMapping(27, digest, edition, facts.printedPageStart, facts.printedPageEnd)
                .copy(isbn = "978-0-14-000000-0"),
        )
        assertFalse(inventedIsbn.accepted)
        assertTrue(inventedIsbn.defects.contains("invented ISBN"))
        val mismatchedPages = evaluateMappingAcceptance(
            signedMapping(27, digest, edition, 1, 2),
        )
        assertTrue(mismatchedPages.defects.contains("mismatched page range"))
        val mismatchedEdition = evaluateMappingAcceptance(
            signedMapping(27, digest, "Invented Edition", facts.printedPageStart, facts.printedPageEnd),
        )
        assertTrue(mismatchedEdition.defects.contains("mismatched edition identity"))
        val mismatchedDigest = evaluateMappingAcceptance(
            signedMapping(27, "ab".repeat(32), edition, facts.printedPageStart, facts.printedPageEnd),
        )
        assertTrue(mismatchedDigest.defects.contains("mismatched mapping digest"))
        val ownership = evaluateMappingAcceptance(
            signedMapping(27, digest, edition, facts.printedPageStart, facts.printedPageEnd)
                .copy(ownedCopyVerified = "yes"),
        )
        assertTrue(ownership.defects.contains("ownership assertion is not retained verification"))
        val blank = evaluateMappingAcceptance(
            MappingAcceptanceAttempt(
                unitNumber = 1,
                disposition = "",
                isbn = "",
                editionTitle = "",
                printedPageStart = null,
                printedPageEnd = null,
                mappingPackageDigest = "",
                ownedCopyVerified = "",
                ownerName = "",
                date = "",
                signature = "",
                support = DecisionSupport.CI_SUCCESS,
            ),
        )
        assertFalse(blank.accepted)
        assertTrue(blank.defects.contains("non-owner signal cannot approve a mapping"))
        assertTrue(blank.defects.contains("owner signature is blank"))
        assertTrue(blank.defects.contains("mapping remains PENDING_CURRICULUM_REVIEWER"))
        (1..30).forEach { number ->
            val row = governedMappingFacts(number)
            assertEquals("PENDING_CURRICULUM_REVIEWER", row.mappingDisposition)
            assertEquals(null, row.isbn)
            assertEquals("UNRESOLVED", row.isbnObservation)
        }
    }

    @Test
    fun preparationManifestBindsFactsAndDoesNotPassAFinalAudit() {
        val pack = pack()
        val root = root()
        val rendered = renderFinalAuditPreparationManifest(pack, root)
        if (System.getenv("WRITE_OWNER_GOVERNANCE") == "1") {
            val file = File(root, OWNER_GOVERNANCE_PREPARATION_PATH)
            checkNotNull(file.parentFile).mkdirs()
            file.writeText(rendered)
        }
        val committed = File(root, OWNER_GOVERNANCE_PREPARATION_PATH).readText()
        assertEquals(rendered, committed)
        assertTrue(preparationManifestIsNotAPassingAudit(committed).joinToString(), preparationManifestIsNotAPassingAudit(committed).isEmpty())
        assertFalse(File(root, AUTHORIZED_FINAL_AUDIT_DIRECTORY).exists())
        assertTrue(committed.contains("\"disposition\": \"NOT_RECORDED\""))
        assertTrue(committed.contains("\"modelVersion\": \"\""))
        assertTrue(committed.contains("\"varianceDecision\": \"\""))
        assertTrue(committed.contains("\"mappingDecision\": \"\""))
        assertTrue(committed.contains(V2Validator.contentReviewDigest(pack)))
        assertTrue(committed.contains(computeReleaseSourceDigest(root)))
        (1..30).forEach { number ->
            assertTrue(committed.contains(V2Validator.unitContentReviewDigest(pack, number)))
        }
        assertFalse(committed.contains("INDEPENDENT_AUDIT_VERIFIED"))
        assertFalse(committed.contains("OWNER_ACCEPTED"))
        assertFalse(committed.contains("FINAL_CURRICULUM_AUDIT_PASSED"))
    }

    @Test
    fun historicalRecordsAndPremiumBlueprintStayUnchanged() {
        val root = root()
        assertEquals(
            "479c6c317212a7b6497c83579d4c006f938361692b0f2611e6f33b4614485b99",
            sha256(File(root, "audit/post-pr53-readiness/POST_PR53_CURRICULUM_READINESS_RECONCILIATION.md")),
        )
        assertEquals(
            "1f872537425502a489fe26e39b5234886cc7132ba051fc5cc7a58d49c0d8920c",
            sha256(File(root, "audit/post-pr53-readiness/POST_PR53_CURRICULUM_READINESS_RECONCILIATION.json")),
        )
        assertEquals(
            "2736372535c82dc5a2c5ac42ab42fad0756169095f799da758c36c88f9b2af4c",
            sha256(File(root, "docs/governance/MASTER_AUTHORITY_v2.0.md")),
        )
        assertEquals(
            "9e941010465fb2b81bf21e7d18eb6b2b681b528d176f477d299ae8d91bf56253",
            sha256(File(root, "docs/governance/MASTER_AUTHORITY_v2.0_AMENDMENT_1.md")),
        )
        assertEquals(
            "851f5558e64a18a4885e6f667695bf16c1b7a1c2d75af4b8ee831dd1bca0f41a",
            sha256(File(root, "docs/governance/UNITS_26_30_AUTHORITY_VARIANCE_PROPOSAL.md")),
        )
        assertEquals(
            "839435bc1cdf259ba639824722b1fb8fdbcf8f79d80dc5f0bbb3c71dd955ade0",
            sha256(File(root, "docs/governance/UNITS_26_30_AUTHORITY_VARIANCE_PROPOSED_RESOLUTION.md")),
        )
        assertEquals(
            "f4c4b770516e2d65624346fff6ee4e0f3ce8017150528cee21df8be64bc666f8",
            sha256(File(root, "docs/design/PREMIUM_OFFLINE_APP_BLUEPRINT_v2.0.md")),
        )
        assertEquals(
            "33b8a4a09fac919a39b438406fa70d9b2dd88179e3f6ff1af432597c6e43c421",
            sha256(File(root, "docs/research/RESEARCH_FOUNDATION_v2.0.md")),
        )
        val manifest = File(root, "app/src/main/AndroidManifest.xml").readText()
        assertFalse(manifest.contains("android.permission.INTERNET"))
        File(root, "app/src/main").walkTopDown().filter { it.isFile }.forEach { file ->
            val text = file.readText()
            assertFalse(file.path, text.contains("PREMIUM_OFFLINE_APP_BLUEPRINT"))
        }
        val candidate = readAcceptanceCandidate(root)
        assertEquals("NOT_APPROVED", candidate?.publicRelease)
        assertEquals(false, candidate?.frozen)
        assertEquals(null, candidate?.sourceCommit)
        assertEquals("NOT_RECORDED", candidate?.artifact?.status)
    }

    @Test
    fun referenceFileComparisonCorrectsLateBoundariesAndStaysUnsigned() {
        val pack = pack()
        val corrected = mapOf(21 to "257-268", 22 to "269-282", 23 to "283-293", 24 to "294-304", 25 to "305-319")
        corrected.forEach { (number, range) ->
            val facts = governedMappingFacts(number)
            assertEquals(range, "${facts.printedPageStart}-${facts.printedPageEnd}")
            assertEquals("PENDING_CURRICULUM_REVIEWER", facts.mappingDisposition)
            assertNull(facts.isbn)
        }
        assertEquals("320-331", "${governedMappingFacts(26).printedPageStart}-${governedMappingFacts(26).printedPageEnd}")
        assertTrue(governedMappingFacts(1).observedEditionNotes.orEmpty().contains("does not match"))
        assertTrue(governedMappingFacts(27).topicDescription.orEmpty().contains("Active participle formation is not in this lesson"))
        assertFalse(governedMappingFacts(29).topicDescription.orEmpty().contains("optional punctuation"))
        listOf(27, 29, 30).forEach {
            assertTrue(V2Validator.activatedAuthorityVariance(it))
            assertFalse(V2Validator.authorityVarianceApplies(it))
        }
        assertFalse(V2Validator.authorityVarianceApplies(26))
        assertFalse(V2Validator.activatedAuthorityVariance(26))
        val lines = StringBuilder()
        (1..30).forEach { number ->
            val review = buildUnitReviewPackage(pack, number)
            lines.append("$number ${review.contentReviewDigest} ${review.mappingPackageDigest} ${review.reviewPackageDigest}\n")
        }
        assertEquals(30, lines.lines().filter { it.isNotBlank() }.size)
    }

    @Test
    fun decisionPackageStatesRecommendationsAndLeavesOwnerFieldsBlank() {
        val root = root()
        val packet = File(root, "docs/governance/OWNER_VARIANCE_DECISION_PACKET.md").readText()
        assertTrue(packet.contains("Status: ACTIVATED_PEDAGOGICAL_BOUNDARY_NOT_QUALIFIED_REVIEW"))
        assertTrue(packet.contains("## Unit 27\n\nRecommendation: ACTIVATE\n"))
        assertTrue(packet.contains("## Unit 29\n\nRecommendation: REVISE\n"))
        assertTrue(packet.contains("## Unit 30\n\nRecommendation: ACTIVATE\n"))
        assertEquals(3, Regex("(?m)^Product Owner disposition: ACTIVATE$").findAll(packet).count())
        assertEquals(3, Regex("(?m)^Product Owner name: Justin Kulka$").findAll(packet).count())
        assertEquals(3, Regex("(?m)^Product Owner date: 2026-10-02$").findAll(packet).count())
        assertEquals(3, Regex("(?m)^Product Owner signature:$").findAll(packet).count())
        assertFalse(packet.contains("MAPPING_APPROVED"))
        val dossier = File(root, "docs/governance/OWNER_MAPPING_DECISION_DOSSIER.md").readText()
        assertTrue(dossier.contains("Status: PAGE_BOUNDARIES_ACCEPTED_REVIEW_STILL_PENDING"))
        assertTrue(dossier.contains("PENDING_CURRICULUM_REVIEWER"))
        assertTrue(dossier.contains(RETAINED_PRIVATE_FILE_SHA_UNITS_11_20))
        (1..30).forEach { number ->
            val facts = governedMappingFacts(number)
            assertTrue(dossier.contains("review/unit-$number/UNIT_${number}_OWNED_EDITION_MAPPING_WORKSHEET.tsv"))
            assertTrue(dossier.contains("${facts.printedPageStart}-${facts.printedPageEnd}"))
            assertTrue(dossier.contains(facts.topicDescription!!))
        }
        assertTrue(dossier.contains("307-321"))
        assertTrue(dossier.contains("320-331"))
        val checklist = File(root, "docs/governance/OWNER_MAPPING_CONFIRMATION_CHECKLIST.md").readText()
        assertTrue(checklist.contains(RETAINED_PRIVATE_FILE_SHA_UNITS_11_20))
        assertTrue(checklist.contains("Product Owner signature:"))
        assertFalse(checklist.contains("MAPPING_APPROVED"))
    }

    private fun signedVariance(
        pack: dev.russiancompanion.v2.curriculum.V2CurriculumPack,
        root: File,
        unitNumber: Int,
        disposition: String,
    ) = VarianceDecisionAttempt(
        unitNumber = unitNumber,
        disposition = disposition,
        ownerName = "Justin",
        date = "2026-10-01",
        signature = "signed-in-test-only",
        contentDigest = V2Validator.unitContentReviewDigest(pack, unitNumber),
        mappingPackageDigest = mappingPackageDigest(governedMappingFacts(unitNumber)),
        repositorySourceDigest = computeReleaseSourceDigest(root),
        support = DecisionSupport.OWNER_SIGNATURE,
    )

    private fun signedMapping(
        unitNumber: Int,
        digest: String,
        editionTitle: String,
        pageStart: Int?,
        pageEnd: Int?,
    ) = MappingAcceptanceAttempt(
        unitNumber = unitNumber,
        disposition = "MAPPING_APPROVED",
        isbn = "",
        editionTitle = editionTitle,
        printedPageStart = pageStart,
        printedPageEnd = pageEnd,
        mappingPackageDigest = digest,
        ownedCopyVerified = "",
        ownerName = "Justin",
        date = "2026-10-01",
        signature = "signed-in-test-only",
        support = DecisionSupport.OWNER_SIGNATURE,
    )

    private fun pack() = loadCurriculumPack(root())

    private fun root(): File = listOf(File("."), File("..")).first { File(it, "app/src/main").isDirectory }

    private fun sha256(file: File): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(file.readBytes())
        return digest.joinToString("") { byte -> "%02x".format(byte) }
    }
}
