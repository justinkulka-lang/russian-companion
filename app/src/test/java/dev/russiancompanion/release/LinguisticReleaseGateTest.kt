package dev.russiancompanion.release

import dev.russiancompanion.v2.audit.loadCurriculumPack
import dev.russiancompanion.v2.curriculum.FINAL_CURRICULUM_AUDIT_PASSED
import dev.russiancompanion.v2.curriculum.IMPLEMENTED_PENDING_FINAL_AUDIT
import dev.russiancompanion.v2.curriculum.ORIGINAL_RESEARCH_SYNTHESIS
import dev.russiancompanion.v2.curriculum.OWNER_ACCEPTED
import dev.russiancompanion.v2.curriculum.PRIOR_REVIEW_STAGES
import dev.russiancompanion.v2.curriculum.QUOTED_OR_ADAPTED_SOURCE
import dev.russiancompanion.v2.curriculum.RESEARCH_VERIFIED
import dev.russiancompanion.v2.curriculum.RETAINED_BYTE_STATUS
import dev.russiancompanion.v2.curriculum.V2CurriculumPack
import dev.russiancompanion.v2.curriculum.V2FinalAuditIdentity
import dev.russiancompanion.v2.curriculum.V2OriginalSynthesisEvidence
import dev.russiancompanion.v2.curriculum.V2PersistedEvidence
import dev.russiancompanion.v2.curriculum.V2QuotedSourceEvidence
import dev.russiancompanion.v2.curriculum.V2ReviewRecord
import dev.russiancompanion.v2.curriculum.V2UnitReviewRecord
import dev.russiancompanion.v2.curriculum.V2Validator
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class LinguisticReleaseGateTest {
    @Test
    fun livePackKeepsLinguisticReviewAbsentWithoutFabricatingAcceptance() {
        val pack = loadCurriculumPack(root())
        assertTrue(pack.unitReviews.all { it.status == IMPLEMENTED_PENDING_FINAL_AUDIT })
        assertTrue(pack.linguisticReview.status == IMPLEMENTED_PENDING_FINAL_AUDIT)
        assertTrue(pack.unitReviews.all { it.evidence == null && it.finalAudit == null && it.reviewChainStages.isEmpty() })
        assertTrue(curriculumBlockers(pack).contains(ReleaseBlocker.LINGUISTIC_REVIEW_ABSENT))
        assertFalse(V2Validator.linguisticReleaseAccepted(pack))
        assertTrue(V2Validator.validate(pack).ok)
    }

    @Test
    fun completeCurrentAcceptanceClearsTheLinguisticBlocker() {
        val pack = accepted(loadCurriculumPack(root()))
        assertFalse(curriculumBlockers(pack).contains(ReleaseBlocker.LINGUISTIC_REVIEW_ABSENT))
        assertTrue(V2Validator.linguisticReleaseAccepted(pack))
        val validation = V2Validator.validate(pack)
        assertTrue(validation.errors.joinToString("\n"), validation.ok)
        val synthesis = accepted(loadCurriculumPack(root()), synthesis = true)
        assertFalse(curriculumBlockers(synthesis).contains(ReleaseBlocker.LINGUISTIC_REVIEW_ABSENT))
        val synthesisValidation = V2Validator.validate(synthesis)
        assertTrue(synthesisValidation.errors.joinToString("\n"), synthesisValidation.ok)
    }

    @Test
    fun releaseGateAndValidatorRejectIncompleteAcceptance() {
        val live = loadCurriculumPack(root())
        val base = accepted(live)
        val quotedMutations = listOf<(V2QuotedSourceEvidence) -> V2QuotedSourceEvidence>(
            { it.copy(sourceIdentity = "") },
            { it.copy(retainedByteStatus = "NOT_RETAINED") },
            { it.copy(retainedByteIdentity = "") },
            { it.copy(exactSpanOrAssetHash = "") },
            { it.copy(licenseOrRights = "") },
            { it.copy(transformation = "") },
            { it.copy(attribution = "") },
            { it.copy(claimBinding = "not yet passed") },
        ).map { edit ->
            base.withUnit(1) { review ->
                val quoted = review.evidence?.quotedSource!!
                review.copy(evidence = review.evidence?.copy(quotedSource = edit(quoted)))
            }
        }
        val synthesisBase = accepted(live, synthesis = true)
        val synthesisMutations = listOf<(V2OriginalSynthesisEvidence) -> V2OriginalSynthesisEvidence>(
            { it.copy(bibliography = "") },
            { it.copy(sourceDateOrVersion = "not yet passed") },
            { it.copy(authorshipDeclaration = "") },
            { it.copy(objectiveOrClaimBinding = "") },
            { it.copy(finalAudit = it.finalAudit.copy(artifactIdentity = "")) },
            { it.copy(finalAudit = it.finalAudit.copy(artifactDigest = "")) },
            { it.copy(finalAudit = it.finalAudit.copy(auditedContentDigest = EARLIER)) },
            { it.copy(finalAudit = it.finalAudit.copy(disposition = "not yet passed")) },
            { it.copy(finalAudit = it.finalAudit.copy(modelVersion = "")) },
            { it.copy(finalAudit = it.finalAudit.copy(date = "not yet passed")) },
        ).map { edit ->
            synthesisBase.withUnit(1) { review ->
                val synthesis = review.evidence?.originalSynthesis!!
                review.copy(evidence = review.evidence?.copy(originalSynthesis = edit(synthesis)))
            }
        }
        val chain = PRIOR_REVIEW_STAGES
        val cases = listOf(
            base.withUnit(1) { it.copy(reviewedContentDigest = "not yet passed") },
            base.withUnit(1) { it.copy(reviewedContentDigest = EARLIER, finalAudit = audit(EARLIER)) },
            base.withUnit(1) { it.copy(evidence = null) },
            base.withUnit(1) { it.copy(evidence = V2PersistedEvidence(evidenceClass = QUOTED_OR_ADAPTED_SOURCE)) },
            base.withUnit(1) { it.copy(finalAudit = null) },
            base.withUnit(1) { it.copy(finalAudit = audit(V2Validator.unitContentReviewDigest(live, 1)).copy(artifactDigest = "")) },
            base.withUnit(1) { it.copy(finalAudit = audit(V2Validator.unitContentReviewDigest(live, 1)).copy(artifactDigest = "not yet passed")) },
            base.withUnit(1) { it.copy(finalAudit = audit(EARLIER)) },
            base.withUnit(1) { it.copy(reviewChainStages = chain.reversed()) },
            base.withUnit(1) { it.copy(reviewChainStages = chain + RESEARCH_VERIFIED) },
            base.withUnit(1) { it.copy(reviewChainStages = listOf(RESEARCH_VERIFIED, FINAL_CURRICULUM_AUDIT_PASSED)) },
            base.withUnit(1) { it.copy(reviewChainStages = chain + "EXTRA_STAGE") },
            base.withUnit(1) { it.copy(reviewChainStages = chain + OWNER_ACCEPTED) },
            base.withUnit(1) { it.copy(status = IMPLEMENTED_PENDING_FINAL_AUDIT, reviewChainStages = chain) },
            base.copy(linguisticReview = base.linguisticReview.copy(reviewChainStages = chain.reversed())),
            base.copy(
                linguisticReview = base.linguisticReview.copy(
                    reviewChainStages = listOf(RESEARCH_VERIFIED),
                    finalAudit = null,
                ),
            ),
        ) + quotedMutations + synthesisMutations
        cases.forEach { mutated -> assertRejected(mutated) }
    }

    @Test
    fun packWideChainStaysBlockedWhenOnlyUnitsAreAccepted() {
        val live = loadCurriculumPack(root())
        val unitsOnly = accepted(live).copy(linguisticReview = live.linguisticReview)
        assertTrue(curriculumBlockers(unitsOnly).contains(ReleaseBlocker.LINGUISTIC_REVIEW_ABSENT))
        assertFalse(V2Validator.linguisticReleaseAccepted(unitsOnly))
        val validation = V2Validator.validate(unitsOnly)
        assertTrue(validation.errors.joinToString("\n"), validation.ok)
    }

    private fun assertRejected(pack: V2CurriculumPack) {
        assertTrue(curriculumBlockers(pack).contains(ReleaseBlocker.LINGUISTIC_REVIEW_ABSENT))
        assertFalse(V2Validator.linguisticReleaseAccepted(pack))
        val validation = V2Validator.validate(pack)
        assertFalse(validation.errors.joinToString("\n"), validation.ok)
    }

    private fun accepted(pack: V2CurriculumPack, synthesis: Boolean = false): V2CurriculumPack {
        val contentDigest = V2Validator.contentReviewDigest(pack)
        return pack.copy(
            linguisticReview = acceptPack(pack.linguisticReview, contentDigest, synthesis),
            unitReviews = pack.unitReviews.map { review ->
                acceptUnit(review, V2Validator.unitContentReviewDigest(pack, review.unitNumber), synthesis)
            },
        )
    }

    private fun acceptPack(review: V2ReviewRecord, digest: String, synthesis: Boolean): V2ReviewRecord {
        return review.copy(
            status = OWNER_ACCEPTED,
            reviewChainStages = PRIOR_REVIEW_STAGES,
            reviewedContentDigest = digest,
            evidence = if (synthesis) synthesisEvidence(digest) else quotedEvidence(),
            finalAudit = audit(digest),
        )
    }

    private fun acceptUnit(review: V2UnitReviewRecord, digest: String, synthesis: Boolean): V2UnitReviewRecord {
        return review.copy(
            status = OWNER_ACCEPTED,
            reviewChainStages = PRIOR_REVIEW_STAGES,
            reviewedContentDigest = digest,
            evidence = if (synthesis) synthesisEvidence(digest) else quotedEvidence(),
            finalAudit = audit(digest),
        )
    }

    private fun V2CurriculumPack.withUnit(
        number: Int,
        edit: (V2UnitReviewRecord) -> V2UnitReviewRecord,
    ): V2CurriculumPack {
        return copy(unitReviews = unitReviews.map { if (it.unitNumber == number) edit(it) else it })
    }

    private fun audit(digest: String) = V2FinalAuditIdentity(
        artifactIdentity = "audit/final-curriculum-audit-v1.json",
        artifactDigest = ARTIFACT,
        auditedContentDigest = digest,
        disposition = FINAL_CURRICULUM_AUDIT_PASSED,
        modelVersion = "claude-opus-4.1",
        date = "2026-09-29",
    )

    private fun quotedEvidence() = V2PersistedEvidence(
        evidenceClass = QUOTED_OR_ADAPTED_SOURCE,
        quotedSource = V2QuotedSourceEvidence(
            sourceIdentity = "src-governed-example",
            retainedByteStatus = RETAINED_BYTE_STATUS,
            retainedByteIdentity = ARTIFACT,
            exactSpanOrAssetHash = "span-1",
            licenseOrRights = "CC BY-SA 4.0",
            transformation = "quoted",
            attribution = "example attribution",
            claimBinding = "governed-claim",
        ),
    )

    private fun synthesisEvidence(digest: String) = V2PersistedEvidence(
        evidenceClass = ORIGINAL_RESEARCH_SYNTHESIS,
        originalSynthesis = V2OriginalSynthesisEvidence(
            bibliography = "claim bibliography",
            sourceDateOrVersion = "2026-09-29",
            authorshipDeclaration = "independently authored",
            objectiveOrClaimBinding = "governed-claim",
            finalAudit = audit(digest),
        ),
    )

    private fun root(): File = listOf(File("."), File("..")).first { File(it, "app/src/main").isDirectory }

    private companion object {
        const val EARLIER = "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb"
        const val ARTIFACT = "cccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc"
    }
}
