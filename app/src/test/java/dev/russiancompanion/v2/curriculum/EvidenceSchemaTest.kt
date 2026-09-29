package dev.russiancompanion.v2.curriculum

import dev.russiancompanion.v2.audit.loadCurriculumPack
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class EvidenceSchemaTest {
    @Test
    fun automatedSignalsAndAModelNameDoNotApprove() {
        assertFalse(automatedSignalsGrantApproval(ciGreen = true, countersComplete = false, selfAttested = false, modelName = null))
        assertFalse(automatedSignalsGrantApproval(ciGreen = false, countersComplete = true, selfAttested = false, modelName = null))
        assertFalse(automatedSignalsGrantApproval(ciGreen = false, countersComplete = false, selfAttested = true, modelName = null))
        assertFalse(automatedSignalsGrantApproval(ciGreen = false, countersComplete = false, selfAttested = false, modelName = "Claude"))
        assertFalse(automatedSignalsGrantApproval(ciGreen = true, countersComplete = true, selfAttested = true, modelName = "Codex"))

        val pending = ApprovalAttempt(
            stage = IMPLEMENTED_PENDING_FINAL_AUDIT,
            ciGreen = true,
            countersComplete = true,
            selfAttested = true,
            modelName = "Claude",
        )
        assertFalse(approvalGranted(pending))
        assertTrue(reviewChainDefects(pending).isEmpty())

        val legacy = ApprovalAttempt(
            stage = "APPROVED",
            reviewedContentDigest = DIGEST,
            currentContentDigest = DIGEST,
            priorStages = PRIOR_REVIEW_STAGES,
            evidence = quotedEvidence(),
            modelName = "Qualified Reviewer",
        )
        assertFalse(approvalGranted(legacy))
        assertFalse(releaseLinguisticReviewSatisfied("APPROVED", DIGEST, PRIOR_REVIEW_STAGES))
        assertFalse(mappingApprovedBySignals(ciGreen = true, countersComplete = true, selfAttested = true, ownerDispositionRecorded = false))
        assertTrue(mappingApprovedBySignals(ciGreen = false, countersComplete = false, selfAttested = false, ownerDispositionRecorded = true))
        assertFalse(
            varianceActivationPermitted(
                ciGreen = true,
                exactSequenceDifferenceRecorded = false,
                boundedResolutionRecorded = false,
                finalAuditPassed = false,
                ownerAccepted = false,
                versionedDigest = null,
            ),
        )
        assertTrue(
            varianceActivationPermitted(
                ciGreen = false,
                exactSequenceDifferenceRecorded = true,
                boundedResolutionRecorded = true,
                finalAuditPassed = true,
                ownerAccepted = true,
                versionedDigest = DIGEST,
            ),
        )
    }

    @Test
    fun ownerAcceptanceRequiresThePriorChainAMatchingDigestAndEvidence() {
        val quoted = quotedEvidence()
        val complete = ApprovalAttempt(
            stage = OWNER_ACCEPTED,
            priorStages = PRIOR_REVIEW_STAGES,
            reviewedContentDigest = DIGEST,
            currentContentDigest = DIGEST,
            evidence = quoted,
            ciGreen = true,
        )
        assertTrue(approvalGranted(complete))
        assertTrue(reviewChainDefects(complete).isEmpty())

        assertFalse(
            approvalGranted(
                complete.copy(priorStages = listOf(IMPLEMENTED_PENDING_FINAL_AUDIT)),
            ),
        )
        assertFalse(approvalGranted(complete.copy(currentContentDigest = "other")))
        assertFalse(approvalGranted(complete.copy(evidence = null)))
        assertFalse(approvalGranted(complete.copy(stage = FINAL_CURRICULUM_AUDIT_PASSED)))
        assertFalse(approvalGranted(complete.copy(stage = INDEPENDENT_AUDIT_VERIFIED)))
        assertFalse(releaseLinguisticReviewSatisfied(OWNER_ACCEPTED, DIGEST, emptyList()))
        assertTrue(releaseLinguisticReviewSatisfied(OWNER_ACCEPTED, DIGEST, PRIOR_REVIEW_STAGES))
        assertFalse(releaseLinguisticReviewSatisfied(IMPLEMENTED_PENDING_FINAL_AUDIT, DIGEST, PRIOR_REVIEW_STAGES))
    }

    @Test
    fun quotedAndOriginalEvidenceAreValidatedDifferently() {
        val quoted = quotedEvidence()
        assertTrue(evidenceDefects(quoted).isEmpty())
        assertTrue(evidenceDefects(quoted.copy(exactSpanOrAssetHash = null)).any { it.contains("exact span or asset hash") })
        assertTrue(evidenceDefects(EvidenceRecord()).any { it.contains("evidence class is not recorded") })
        assertTrue(evidenceDefects(EvidenceRecord(evidenceClass = "PROFESSOR")).any { it.contains("not recognized") })

        val synthesis = EvidenceRecord(
            evidenceClass = ORIGINAL_RESEARCH_SYNTHESIS,
            bibliography = "claim bibliography",
            sourceDateOrVersion = "2026-09-29",
            authorshipDeclaration = "independently authored",
            objectiveOrClaimBinding = "obj-u7-adverb",
            finalCurriculumAudit = "not yet passed",
        )
        assertTrue(evidenceDefects(synthesis).isEmpty())
        assertTrue(synthesis.exactSpanOrAssetHash.isNullOrBlank())
        assertTrue(evidenceDefects(synthesis.copy(bibliography = null)).any { it.contains("claim-level bibliography") })

        val pack = loadCurriculumPack(root())
        assertTrue(pack.unitReviews.all { it.status == IMPLEMENTED_PENDING_FINAL_AUDIT })
        assertTrue(pack.unitReviews.all { it.evidenceClass == null && it.reviewChainStages.isEmpty() && it.reviewer.isNullOrBlank() })
        assertFalse(File(root(), "app/src/main/assets/content/v2-curriculum.json").readText().contains("PENDING_QUALIFIED_REVIEW"))
        val legacy = V2Validator.validate(
            pack.copy(linguisticReview = pack.linguisticReview.copy(status = "PENDING_QUALIFIED_REVIEW")),
        )
        assertFalse(legacy.ok)
        assertTrue(legacy.errors.any { it.contains("Unrecognized linguistic review status") })
        assertTrue(File(root(), "docs/governance/UNITS_26_30_AUTHORITY_VARIANCE_PROPOSAL.md").readText().contains("PROPOSED_NOT_ACTIVATED"))
    }

    @Test
    fun governedCurriculumChangesInvalidateDownstreamAuditStates() {
        val pack = loadCurriculumPack(root())
        val unitThree = V2Validator.unitContentReviewDigest(pack, 3)
        val unitFour = V2Validator.unitContentReviewDigest(pack, 4)
        val displayLine = pack.units.count { it.readiness == "CONTENT_COMPLETE_REVIEW_PENDING" }.toString() + " content-complete"
        val rewrittenDisplayLine = displayLine + " display only"
        assertNotEquals(displayLine, rewrittenDisplayLine)
        assertEquals(unitThree, V2Validator.unitContentReviewDigest(pack, 3))
        assertEquals(unitFour, V2Validator.unitContentReviewDigest(pack, 4))

        val workflowOnly = pack.copy(
            units = pack.units.map { unit ->
                if (unit.number == 3) unit.copy(readiness = "NOT_PREPARED") else unit
            },
            unitReviews = pack.unitReviews.map { review ->
                if (review.unitNumber == 3) review.copy(status = "REJECTED", notes = "workflow only") else review
            },
        )
        assertEquals(unitThree, V2Validator.unitContentReviewDigest(workflowOnly, 3))
        assertEquals(unitFour, V2Validator.unitContentReviewDigest(workflowOnly, 4))
        assertEquals(V2Validator.contentReviewDigest(pack), V2Validator.contentReviewDigest(workflowOnly))

        val governed = pack.copy(
            units = pack.units.map { unit ->
                if (unit.number != 3) {
                    unit
                } else {
                    val session = unit.sessions.first()
                    val step = session.steps.first()
                    unit.copy(
                        sessions = listOf(
                            session.copy(steps = listOf(step.copy(body = step.body + " governed change")) + session.steps.drop(1)),
                        ) + unit.sessions.drop(1),
                    )
                }
            },
        )
        val changed = V2Validator.unitContentReviewDigest(governed, 3)
        assertNotEquals(unitThree, changed)
        assertEquals(unitFour, V2Validator.unitContentReviewDigest(governed, 4))
        assertFalse(downstreamAuditRemainsBound(unitThree, changed, FINAL_CURRICULUM_AUDIT_PASSED))
        assertFalse(downstreamAuditRemainsBound(unitThree, changed, INDEPENDENT_AUDIT_VERIFIED))
        assertFalse(downstreamAuditRemainsBound(unitThree, changed, OWNER_ACCEPTED))
        assertTrue(downstreamAuditRemainsBound(unitThree, changed, IMPLEMENTED_PENDING_FINAL_AUDIT))
        assertTrue(downstreamAuditRemainsBound(unitThree, unitThree, OWNER_ACCEPTED))
    }

    private fun quotedEvidence() = EvidenceRecord(
        evidenceClass = QUOTED_OR_ADAPTED_SOURCE,
        sourceIdentity = "src-example",
        retainedBytes = true,
        exactSpanOrAssetHash = "span-1",
        licenseOrRights = "CC BY-SA 4.0",
        transformation = "quoted",
        attribution = "example attribution",
        claimBinding = "example-claim",
    )

    private fun root(): File = listOf(File("."), File("..")).first { File(it, "app/src/main").isDirectory }

    private companion object {
        const val DIGEST = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
    }
}
