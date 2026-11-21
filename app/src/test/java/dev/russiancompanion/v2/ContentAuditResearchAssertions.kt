package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.ContentAuditSources
import dev.russiancompanion.v2.curriculum.V2ProvenanceRecord
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import java.security.MessageDigest

/** Pending synthesis is admitted for development; it is never accepted review evidence. */
internal fun assertContentAuditResearch(record: V2ProvenanceRecord) {
    assertEquals(ContentAuditSources.SOURCE_ID, record.sourceId)
    assertEquals(ContentAuditSources.REVISION, record.sourceRevision)
    assertEquals("urn:russian-companion:content-audit-research:2026-10-08", record.sourceUrl)
    assertEquals(ContentAuditSources.LICENSE, record.licenseId)
    assertEquals("urn:russian-companion:license:original-app-text", record.licenseUrl)
    assertEquals(ContentAuditSources.TRANSFORMATION, record.transformation)
    assertEquals("IMPLEMENTED_PENDING_FINAL_AUDIT", record.reviewStatus)
    assertTrue(record.reviewerIdentity.isBlank() && record.reviewedDigest.isBlank())
    assertEquals(researchHash(record.originalSourceText), record.sourceHash)
    assertEquals(researchHash(record.shippedText), record.contentHash)
    val evidence = record.synthesis
    assertNotNull(evidence)
    checkNotNull(evidence)
    assertTrue(evidence.bibliography.isNotBlank())
    assertTrue(evidence.authorshipDeclaration.isNotBlank())
    assertTrue(evidence.objectiveOrClaimBinding.startsWith(record.objectiveId))
    assertTrue(evidence.finalAudit.disposition.isBlank())
    assertTrue(evidence.finalAudit.artifactIdentity.isBlank())
}

private fun researchHash(text: String): String =
    MessageDigest.getInstance("SHA-256").digest(text.toByteArray(Charsets.UTF_8))
        .joinToString("") { "%02x".format(it.toInt() and 255) }
