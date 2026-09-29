package dev.russiancompanion.release

import dev.russiancompanion.v2.audit.loadCurriculumPack
import dev.russiancompanion.v2.curriculum.AUTHORIZED_FINAL_AUDIT_DIRECTORY
import dev.russiancompanion.v2.curriculum.FINAL_CURRICULUM_AUDIT_PASSED
import dev.russiancompanion.v2.curriculum.IMPLEMENTED_PENDING_FINAL_AUDIT
import dev.russiancompanion.v2.curriculum.ORIGINAL_RESEARCH_SYNTHESIS
import dev.russiancompanion.v2.curriculum.OWNER_ACCEPTED
import dev.russiancompanion.v2.curriculum.PRIOR_REVIEW_STAGES
import dev.russiancompanion.v2.curriculum.QUOTED_OR_ADAPTED_SOURCE
import dev.russiancompanion.v2.curriculum.RESEARCH_VERIFIED
import dev.russiancompanion.v2.curriculum.RETAINED_BYTE_STATUS
import dev.russiancompanion.v2.curriculum.RETAINED_FINAL_AUDIT_SCHEMA
import dev.russiancompanion.v2.curriculum.RetainedFinalAuditDocument
import dev.russiancompanion.v2.curriculum.RetainedUnitAuditDisposition
import dev.russiancompanion.v2.curriculum.V2CurriculumPack
import dev.russiancompanion.v2.curriculum.V2FinalAuditIdentity
import dev.russiancompanion.v2.curriculum.V2OriginalSynthesisEvidence
import dev.russiancompanion.v2.curriculum.V2PersistedEvidence
import dev.russiancompanion.v2.curriculum.V2QuotedSourceEvidence
import dev.russiancompanion.v2.curriculum.V2ReviewRecord
import dev.russiancompanion.v2.curriculum.V2UnitReviewRecord
import dev.russiancompanion.v2.curriculum.V2Validator
import dev.russiancompanion.v2.curriculum.encodeRetainedFinalAudit
import dev.russiancompanion.v2.curriculum.sha256Bytes
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.nio.file.Files

class LinguisticReleaseGateTest {
    @Test
    fun livePackKeepsLinguisticReviewAbsentWithoutFabricatingAcceptance() {
        val pack = loadCurriculumPack(root())
        assertTrue(pack.unitReviews.all { it.status == IMPLEMENTED_PENDING_FINAL_AUDIT })
        assertTrue(pack.linguisticReview.status == IMPLEMENTED_PENDING_FINAL_AUDIT)
        assertTrue(pack.unitReviews.all { it.evidence == null && it.finalAudit == null && it.reviewChainStages.isEmpty() })
        assertFalse(File(root(), "$AUTHORIZED_FINAL_AUDIT_DIRECTORY/final-curriculum-audit.json").exists())
        assertTrue(curriculumBlockers(pack, root()).contains(ReleaseBlocker.LINGUISTIC_REVIEW_ABSENT))
        assertFalse(V2Validator.linguisticReleaseAccepted(pack, root()))
        assertTrue(V2Validator.validate(pack, root()).ok)
    }

    @Test
    fun completeCurrentAcceptanceClearsTheLinguisticBlockerOnlyForTheRetainedArtifact() {
        val live = loadCurriculumPack(root())
        val staged = stage(live)
        assertTrue(curriculumBlockers(staged.pack, root()).contains(ReleaseBlocker.LINGUISTIC_REVIEW_ABSENT))
        assertFalse(V2Validator.validate(staged.pack, root()).ok)
        assertFalse(curriculumBlockers(staged.pack, staged.root).contains(ReleaseBlocker.LINGUISTIC_REVIEW_ABSENT))
        assertTrue(V2Validator.linguisticReleaseAccepted(staged.pack, staged.root))
        val validation = V2Validator.validate(staged.pack, staged.root)
        assertTrue(validation.errors.joinToString("\n"), validation.ok)
        val synthesis = stage(live, synthesis = true)
        assertFalse(curriculumBlockers(synthesis.pack, synthesis.root).contains(ReleaseBlocker.LINGUISTIC_REVIEW_ABSENT))
        val synthesisValidation = V2Validator.validate(synthesis.pack, synthesis.root)
        assertTrue(synthesisValidation.errors.joinToString("\n"), synthesisValidation.ok)
    }

    @Test
    fun releaseGateAndValidatorRejectIncompleteAcceptance() {
        val live = loadCurriculumPack(root())
        val staged = stage(live)
        val base = staged.pack
        val repository = staged.root
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
                val quoted = checkNotNull(review.evidence).quotedSource!!
                review.copy(evidence = checkNotNull(review.evidence).copy(quotedSource = edit(quoted)))
            }
        }
        val synthesis = stage(live, synthesis = true)
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
            synthesis.pack.withUnit(1) { review ->
                val body = checkNotNull(review.evidence).originalSynthesis!!
                review.copy(evidence = checkNotNull(review.evidence).copy(originalSynthesis = edit(body)))
            }
        }
        val chain = PRIOR_REVIEW_STAGES
        val cases = listOf(
            base.withUnit(1) { it.copy(reviewedContentDigest = "not yet passed") },
            base.withUnit(1) { it.copy(reviewedContentDigest = EARLIER, finalAudit = it.finalAudit?.copy(auditedContentDigest = EARLIER)) },
            base.withUnit(1) { it.copy(evidence = null) },
            base.withUnit(1) { it.copy(evidence = V2PersistedEvidence(evidenceClass = QUOTED_OR_ADAPTED_SOURCE)) },
            base.withUnit(1) { it.copy(finalAudit = null) },
            base.withUnit(1) { it.copy(finalAudit = it.finalAudit?.copy(artifactDigest = "")) },
            base.withUnit(1) { it.copy(finalAudit = it.finalAudit?.copy(artifactDigest = "not yet passed")) },
            base.withUnit(1) { it.copy(finalAudit = it.finalAudit?.copy(auditedContentDigest = EARLIER)) },
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
        )
        cases.forEach { mutated -> assertRejected(mutated, repository) }
        synthesisMutations.forEach { mutated -> assertRejected(mutated, synthesis.root) }
    }

    @Test
    fun retainedArtifactBytesPathAndDocumentMustMatch() {
        val live = loadCurriculumPack(root())
        val staged = stage(live)
        val packDigest = V2Validator.contentReviewDigest(live)
        val unitDigest = V2Validator.unitContentReviewDigest(live, 1)
        assertRejected(point(staged, "$AUTHORIZED_FINAL_AUDIT_DIRECTORY/missing.json", staged.hash), staged.root)
        val empty = stage(live)
        File(empty.root, RELATIVE).writeBytes(ByteArray(0))
        val emptyHash = sha256Bytes(ByteArray(0))
        assertRejected(retarget(empty, empty.hash.let { emptyHash }), empty.root, "empty")
        assertRejected(retarget(staged, FABRICATED), staged.root, "do not match")
        assertRejected(point(staged, "/tmp/$RELATIVE", staged.hash), staged.root, "absolute")
        assertRejected(point(staged, "C:/$RELATIVE", staged.hash), staged.root, "drive prefix")
        assertRejected(
            point(staged, "$AUTHORIZED_FINAL_AUDIT_DIRECTORY/../final-curriculum-audit/final-curriculum-audit.json", staged.hash),
            staged.root,
            "traversal",
        )
        assertRejected(
            point(staged, "$AUTHORIZED_FINAL_AUDIT_DIRECTORY/./final-curriculum-audit.json", staged.hash),
            staged.root,
            "traversal",
        )
        assertRejected(point(staged, RELATIVE.replace("/", "\\"), staged.hash), staged.root, "backslash")
        val outside = stage(live)
        val outsidePath = "audit/other-audit/final-curriculum-audit.json"
        val outsideHash = writeBytes(outside.root, outsidePath, File(outside.root, RELATIVE).readBytes())
        assertRejected(point(outside, outsidePath, outsideHash), outside.root, "outside the authorized directory")
        assertRejected(rewrite(staged, staged.document.copy(auditedContentDigest = EARLIER)), staged.root, "audited content digest")
        assertRejected(
            rewrite(staged, staged.document.copy(disposition = "NOT_PASSED")),
            staged.root,
            "audit disposition",
        )
        assertRejected(rewrite(staged, staged.document.copy(modelVersion = "other-model")), staged.root, "model version")
        assertRejected(rewrite(staged, staged.document.copy(date = "2026-01-02")), staged.root, "audit date")
        assertRejected(rewrite(staged, staged.document.copy(date = "2026-99-99")), staged.root, "audit date")
        val malformed = stage(live)
        val badBytes = "{".toByteArray()
        File(malformed.root, RELATIVE).writeBytes(badBytes)
        assertRejected(retarget(malformed, sha256Bytes(badBytes)), malformed.root, "malformed")
        val split = stage(live)
        val otherPath = "$AUTHORIZED_FINAL_AUDIT_DIRECTORY/unit-1.json"
        val otherDocument = split.document.copy(
            auditedContentDigest = unitDigest,
            unitDispositions = listOf(
                RetainedUnitAuditDisposition(1, unitDigest, FINAL_CURRICULUM_AUDIT_PASSED),
            ),
        )
        val otherHash = writeDocument(split.root, otherPath, otherDocument)
        val otherIdentity = identity(unitDigest, otherHash, otherPath)
        val splitPack = split.pack.withUnit(1) { review ->
            review.copy(finalAudit = otherIdentity, evidence = review.evidence?.copy())
        }
        assertRejected(splitPack, split.root, "does not match the pack-wide")
        assertRejected(
            staged.pack.copy(
                linguisticReview = staged.pack.linguisticReview.copy(
                    finalAudit = staged.pack.linguisticReview.finalAudit?.copy(date = "2026-02-31"),
                ),
            ),
            staged.root,
            "audit date",
        )
        assertTrue(packDigest.isNotBlank())
    }

    @Test
    fun packWideChainStaysBlockedWhenOnlyUnitsAreAccepted() {
        val live = loadCurriculumPack(root())
        val staged = stage(live)
        val unitsOnly = staged.pack.copy(linguisticReview = live.linguisticReview)
        assertTrue(curriculumBlockers(unitsOnly, staged.root).contains(ReleaseBlocker.LINGUISTIC_REVIEW_ABSENT))
        assertFalse(V2Validator.linguisticReleaseAccepted(unitsOnly, staged.root))
        assertFalse(V2Validator.validate(unitsOnly, staged.root).ok)
    }

    private fun assertRejected(pack: V2CurriculumPack, repositoryRoot: File, expected: String? = null) {
        assertTrue(curriculumBlockers(pack, repositoryRoot).contains(ReleaseBlocker.LINGUISTIC_REVIEW_ABSENT))
        assertFalse(V2Validator.linguisticReleaseAccepted(pack, repositoryRoot))
        val validation = V2Validator.validate(pack, repositoryRoot)
        assertFalse(validation.errors.joinToString("\n"), validation.ok)
        if (expected != null) {
            assertTrue(validation.errors.joinToString("\n"), validation.errors.any { it.contains(expected) })
        }
    }

    private data class Fixture(
        val root: File,
        val pack: V2CurriculumPack,
        val document: RetainedFinalAuditDocument,
        val hash: String,
    )

    private fun stage(live: V2CurriculumPack, synthesis: Boolean = false): Fixture {
        val root = Files.createTempDirectory("retained-final-audit").toFile()
        val packDigest = V2Validator.contentReviewDigest(live)
        val document = RetainedFinalAuditDocument(
            schemaVersion = RETAINED_FINAL_AUDIT_SCHEMA,
            disposition = FINAL_CURRICULUM_AUDIT_PASSED,
            modelVersion = MODEL,
            date = DATE,
            auditedContentDigest = packDigest,
            unitDispositions = (1..30).map { number ->
                RetainedUnitAuditDisposition(
                    unitNumber = number,
                    auditedContentDigest = V2Validator.unitContentReviewDigest(live, number),
                    disposition = FINAL_CURRICULUM_AUDIT_PASSED,
                )
            },
        )
        val hash = writeDocument(root, RELATIVE, document)
        val pack = live.copy(
            linguisticReview = acceptPack(live.linguisticReview, packDigest, hash, synthesis),
            unitReviews = live.unitReviews.map { review ->
                acceptUnit(
                    review,
                    V2Validator.unitContentReviewDigest(live, review.unitNumber),
                    hash,
                    synthesis,
                )
            },
        )
        return Fixture(root, pack, document, hash)
    }

    private fun point(fixture: Fixture, path: String, hash: String): V2CurriculumPack {
        return retarget(fixture.copy(hash = hash), hash).let { pack ->
            pack.copy(
                linguisticReview = pack.linguisticReview.copy(
                    finalAudit = pack.linguisticReview.finalAudit?.copy(artifactIdentity = path, artifactDigest = hash),
                ),
                unitReviews = pack.unitReviews.map { review ->
                    review.copy(finalAudit = review.finalAudit?.copy(artifactIdentity = path, artifactDigest = hash))
                },
            )
        }
    }

    private fun retarget(fixture: Fixture, hash: String): V2CurriculumPack {
        return fixture.pack.copy(
            linguisticReview = fixture.pack.linguisticReview.copy(
                finalAudit = fixture.pack.linguisticReview.finalAudit?.copy(artifactDigest = hash),
            ),
            unitReviews = fixture.pack.unitReviews.map { review ->
                review.copy(finalAudit = review.finalAudit?.copy(artifactDigest = hash))
            },
        )
    }

    private fun rewrite(fixture: Fixture, document: RetainedFinalAuditDocument): V2CurriculumPack {
        val hash = writeDocument(fixture.root, RELATIVE, document)
        return retarget(fixture, hash)
    }

    private fun writeDocument(root: File, path: String, document: RetainedFinalAuditDocument): String {
        return writeBytes(root, path, encodeRetainedFinalAudit(document))
    }

    private fun writeBytes(root: File, path: String, bytes: ByteArray): String {
        val file = File(root, path)
        checkNotNull(file.parentFile).mkdirs()
        file.writeBytes(bytes)
        return sha256Bytes(bytes)
    }

    private fun acceptPack(review: V2ReviewRecord, digest: String, hash: String, synthesis: Boolean): V2ReviewRecord {
        val audit = identity(digest, hash, RELATIVE)
        return review.copy(
            status = OWNER_ACCEPTED,
            reviewChainStages = PRIOR_REVIEW_STAGES,
            reviewedContentDigest = digest,
            evidence = if (synthesis) synthesisEvidence(audit) else quotedEvidence(),
            finalAudit = audit,
        )
    }

    private fun acceptUnit(review: V2UnitReviewRecord, digest: String, hash: String, synthesis: Boolean): V2UnitReviewRecord {
        val audit = identity(digest, hash, RELATIVE)
        return review.copy(
            status = OWNER_ACCEPTED,
            reviewChainStages = PRIOR_REVIEW_STAGES,
            reviewedContentDigest = digest,
            evidence = if (synthesis) synthesisEvidence(audit) else quotedEvidence(),
            finalAudit = audit,
        )
    }

    private fun V2CurriculumPack.withUnit(
        number: Int,
        edit: (V2UnitReviewRecord) -> V2UnitReviewRecord,
    ): V2CurriculumPack {
        return copy(unitReviews = unitReviews.map { if (it.unitNumber == number) edit(it) else it })
    }

    private fun identity(digest: String, hash: String, path: String) = V2FinalAuditIdentity(
        artifactIdentity = path,
        artifactDigest = hash,
        auditedContentDigest = digest,
        disposition = FINAL_CURRICULUM_AUDIT_PASSED,
        modelVersion = MODEL,
        date = DATE,
    )

    private fun quotedEvidence() = V2PersistedEvidence(
        evidenceClass = QUOTED_OR_ADAPTED_SOURCE,
        quotedSource = V2QuotedSourceEvidence(
            sourceIdentity = "src-governed-example",
            retainedByteStatus = RETAINED_BYTE_STATUS,
            retainedByteIdentity = FABRICATED,
            exactSpanOrAssetHash = "span-1",
            licenseOrRights = "CC BY-SA 4.0",
            transformation = "quoted",
            attribution = "example attribution",
            claimBinding = "governed-claim",
        ),
    )

    private fun synthesisEvidence(audit: V2FinalAuditIdentity) = V2PersistedEvidence(
        evidenceClass = ORIGINAL_RESEARCH_SYNTHESIS,
        originalSynthesis = V2OriginalSynthesisEvidence(
            bibliography = "claim bibliography",
            sourceDateOrVersion = DATE,
            authorshipDeclaration = "independently authored",
            objectiveOrClaimBinding = "governed-claim",
            finalAudit = audit,
        ),
    )

    private fun root(): File = listOf(File("."), File("..")).first { File(it, "app/src/main").isDirectory }

    private companion object {
        const val RELATIVE = "$AUTHORIZED_FINAL_AUDIT_DIRECTORY/final-curriculum-audit.json"
        const val MODEL = "claude-opus-4.1"
        const val DATE = "2026-09-29"
        const val EARLIER = "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb"
        const val FABRICATED = "dddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd"
    }
}
