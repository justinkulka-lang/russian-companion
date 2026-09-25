package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.BatchARemainingSources
import dev.russiancompanion.v2.curriculum.V2CurriculumPack
import dev.russiancompanion.v2.curriculum.V2PackLoader
import dev.russiancompanion.v2.curriculum.V2ProvenanceRecord
import dev.russiancompanion.v2.curriculum.V2SourceCapability
import dev.russiancompanion.v2.curriculum.V2Validator
import dev.russiancompanion.v2.curriculum.unit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Fail-closed checks for the bounded Unit 7, 17, and 19 intake.
 * A nearby sentence, a keyword, or another project's license does not admit a claim.
 */
class BatchARemainingSourceIntakeTest {
    @Test
    fun admittedSpansStayBoundAndBroaderClaimsStayUnresolved() {
        val pack = pack()
        assertTrue(V2Validator.validate(pack).errors.joinToString("\n"), V2Validator.validate(pack).ok)
        assertEquals("NOT_PREPARED", pack.unit(7)!!.readiness)
        assertEquals("NOT_PREPARED", pack.unit(17)!!.readiness)
        assertEquals("CONTENT_COMPLETE_REVIEW_PENDING", pack.unit(19)!!.readiness)
        assertEquals("DEVELOPMENT", pack.unit(19)!!.installState)
        assertTrue(unresolved(pack, 7).contains("adverb-paradigm"))
        assertFalse(unresolved(pack, 7).contains("adverb-invariance"))
        assertFalse(unresolved(pack, 7).contains("adverb-bounded-pairs"))
        assertTrue(unresolved(pack, 17).contains("productive-clock-time"))
        assertFalse(unresolved(pack, 17).contains("productive-quarter-hour-time"))
        assertFalse(unresolved(pack, 19).contains("probability-dolzhen"))
        assertTrue(pack.unitReviews.filter { it.unitNumber in setOf(7, 17, 19) }.all {
            it.status == "PENDING_QUALIFIED_REVIEW" &&
                it.reviewer.isNullOrBlank() &&
                it.reviewerQualification.isNullOrBlank() &&
                it.reviewedContentDigest.isNullOrBlank()
        })
        pack.provenanceRecords.filter {
            it.objectiveId in setOf(
                "obj-u7-adverb-invariance",
                "obj-u7-adverb-pairs",
                "obj-u17-quarter-hour",
                "obj-u19-probability",
            )
        }.forEach { record ->
            assertEquals("PENDING_QUALIFIED_REVIEW", record.reviewStatus)
            assertEquals("", record.reviewerIdentity)
            assertEquals("", record.reviewerQualification)
            assertEquals("", record.reviewDate)
            assertEquals("", record.reviewedDigest)
            assertNull(record.audioAssetId)
        }
    }

    @Test
    fun wrongRevisionChangedHashWrongSpanAndWrongPageFailClosed() {
        val pack = pack()
        val probability = capability(pack, "probability-dolzhen")
        val wrongRevision = replace(pack, probability.copy(sourceRevision = "1"))
        assertFails(wrongRevision, probability.id, "revision does not match")
        assertTrue(unresolved(wrongRevision, 19).contains("probability-dolzhen"))

        val wrongHash = replace(pack, probability.copy(sourceHash = "0".repeat(64)))
        assertFails(wrongHash, probability.id, "revision does not match")

        val wrongSpan = replace(pack, probability.copy(exactSpan = probability.exactSpan.dropLast(1)))
        assertFails(wrongSpan, probability.id, "span is not in the frozen record")

        val clock = capability(pack, "clock-expression-recognition")
        val wrongPage = replace(
            pack,
            probability.copy(
                sourceId = clock.sourceId,
                sourceRevision = clock.sourceRevision,
                sourceRecordId = clock.sourceRecordId,
                sourceHash = clock.sourceHash,
                exactSpan = clock.exactSpan,
            ),
        )
        assertFails(wrongPage, probability.id, "no independent probability-dolzhen admission")
        assertTrue(unresolved(wrongPage, 19).contains("probability-dolzhen"))
    }

    @Test
    fun correctSpanOnTheWrongUnitObjectiveOrClaimFailsClosed() {
        val pack = pack()
        val probability = capability(pack, "probability-dolzhen")
        val wrongUnit = replace(pack, probability.copy(unitNumber = 16, objectiveId = "obj-u16-complements"))
        assertFails(wrongUnit, probability.id, "wrong unit or objective")

        val wrongObjective = replace(pack, probability.copy(objectiveId = "obj-u19-obligation"))
        assertFails(wrongObjective, probability.id, "wrong unit or objective")

        val obligation = pack.provenanceRecords.first {
            it.originalSourceText == BatchARemainingSources.OBLIGATION_EXAMPLE_SPAN
        }
        val substituted = replace(
            pack,
            probability.copy(
                sourceRecordId = obligation.sourceRecordId,
                sourceHash = obligation.sourceHash,
                exactSpan = obligation.originalSourceText,
            ),
        )
        val errors = V2Validator.validate(substituted).errors.joinToString("\n")
        assertTrue(errors, errors.contains("obligation evidence does not clear probability-dolzhen"))
        assertTrue(errors, errors.contains("no independent probability-dolzhen admission"))
        assertTrue(unresolved(substituted, 19).contains("probability-dolzhen"))
    }

    @Test
    fun keywordOnlyMutableUrlAndForeignLicenseFailClosed() {
        val pack = pack()
        val probability = capability(pack, "probability-dolzhen")
        val keyword = replace(pack, probability.copy(exactSpan = "likelihood"))
        assertFails(keyword, probability.id, "span is not in the frozen record")
        assertTrue(unresolved(keyword, 19).contains("probability-dolzhen"))

        val mutable = pack.copy(
            provenanceRecords = pack.provenanceRecords.map { record ->
                if (record.sourceId == BatchARemainingSources.WIKTIONARY_SOURCE) {
                    record.copy(sourceUrl = "https://en.wiktionary.org/wiki/%D0%B4%D0%BE%D0%BB%D0%B6%D0%B5%D0%BD")
                } else {
                    record
                }
            },
        )
        assertFails(mutable, "Wiktionary license route is missing")

        val wikipediaLicense = pack.copy(
            provenanceRecords = pack.provenanceRecords.map { record ->
                if (record.sourceId == BatchARemainingSources.WIKTIONARY_SOURCE) {
                    record.copy(attribution = "Wikipedia contributors, CC BY-SA 4.0, via Wikipedia:Copyrights oldid 1369466282.")
                } else {
                    record
                }
            },
        )
        assertFails(wikipediaLicense, "Wiktionary license route is missing")

        val wikibooksLicense = pack.copy(
            provenanceRecords = pack.provenanceRecords.map { record ->
                if (record.sourceId == BatchARemainingSources.RU_SOURCE) {
                    record.copy(attribution = "Wikibooks contributors, CC BY-SA 4.0, via Wikibooks:Copyrights oldid 4622060.")
                } else {
                    record
                }
            },
        )
        assertFails(wikibooksLicense, "Russian Wikipedia license route is missing")
    }

    @Test
    fun boundedEvidenceCannotClearTheBroaderClockOrAdverbClaims() {
        val pack = pack()
        val quarter = capability(pack, "productive-quarter-hour-time")
        val clockPack = pack.copy(
            provenanceRecords = retarget(pack, quarter.exactSpan, "obj-u17-clock-answers"),
            sourceCapabilities = pack.sourceCapabilities + quarter.copy(
                id = "cap-forged-clock",
                objectiveId = "obj-u17-clock-answers",
                supportedClaim = "productive-clock-time",
            ),
        )
        val clockErrors = V2Validator.validate(clockPack).errors.joinToString("\n")
        assertTrue(clockErrors, clockErrors.contains("quarter-hour patterns do not clear productive-clock-time"))
        assertTrue(clockErrors, clockErrors.contains("no independent productive-clock-time admission"))
        assertTrue(unresolved(clockPack, 17).contains("productive-clock-time"))

        val pairs = capability(pack, "adverb-bounded-pairs")
        val paradigmPack = pack.copy(
            provenanceRecords = retarget(pack, pairs.exactSpan, "obj-u7-adverb"),
            sourceCapabilities = pack.sourceCapabilities + pairs.copy(
                id = "cap-forged-paradigm",
                objectiveId = "obj-u7-adverb",
                supportedClaim = "adverb-paradigm",
            ),
        )
        val paradigmErrors = V2Validator.validate(paradigmPack).errors.joinToString("\n")
        assertTrue(paradigmErrors, paradigmErrors.contains("bounded adverb pairs do not clear adverb-paradigm"))
        assertTrue(paradigmErrors, paradigmErrors.contains("no independent adverb-paradigm admission"))
        assertTrue(unresolved(paradigmPack, 7).contains("adverb-paradigm"))

        val age = capability(pack, "age")
        val ageAsClock = pack.copy(
            sourceCapabilities = pack.sourceCapabilities + age.copy(
                id = "cap-forged-age-clock",
                objectiveId = "obj-u17-clock-answers",
                supportedClaim = "productive-clock-time",
            ),
        )
        assertFails(ageAsClock, "no independent productive-clock-time admission")
        assertTrue(unresolved(ageAsClock, 17).contains("productive-clock-time"))
    }

    @Test
    fun sourceCoOccurrenceWithoutAGovernedRelationshipFailsClosed() {
        val pack = pack()
        val fast = record(pack, "obj-u7-adverb-pairs", BatchARemainingSources.FAST)
        val creativeAdverb = record(pack, "obj-u7-adverb-pairs", BatchARemainingSources.CREATIVE_ADV)
        val distractor = record(pack, "obj-u7-adverb-pairs", BatchARemainingSources.FAST_ADV)
        assertNull(V2Validator.classifyRelation(fast, creativeAdverb, listOf(distractor)))

        val sense = record(pack, "obj-u19-probability", BatchARemainingSources.LIKELIHOOD_SENSE)
        val obligation = record(pack, "obj-u19-probability", BatchARemainingSources.OBLIGATION_RUSSIAN)
        val gloss = record(pack, "obj-u19-probability", BatchARemainingSources.LIKELIHOOD_GLOSS)
        assertEquals("GOVERNED_SENSE_CONTRAST", V2Validator.classifyRelation(sense, record(pack, "obj-u19-probability", BatchARemainingSources.LIKELIHOOD_RUSSIAN), listOf(obligation)))
        assertNull(V2Validator.classifyRelation(sense, obligation, listOf(gloss)))
    }

    private fun retarget(pack: V2CurriculumPack, span: String, objectiveId: String): List<V2ProvenanceRecord> {
        return pack.provenanceRecords.map { record ->
            if (record.originalSourceText == span) record.copy(objectiveId = objectiveId) else record
        }
    }

    private fun record(pack: V2CurriculumPack, objectiveId: String, shipped: String): V2ProvenanceRecord {
        return pack.provenanceRecords.first { it.objectiveId == objectiveId && it.shippedText == shipped }
    }

    private fun capability(pack: V2CurriculumPack, claim: String): V2SourceCapability {
        return pack.sourceCapabilities.first { it.supportedClaim == claim }
    }

    private fun replace(pack: V2CurriculumPack, capability: V2SourceCapability): V2CurriculumPack {
        return pack.copy(
            sourceCapabilities = pack.sourceCapabilities.map { item ->
                if (item.id == capability.id) capability else item
            },
        )
    }

    private fun unresolved(pack: V2CurriculumPack, number: Int): Set<String> {
        return V2Validator.unresolvedRequiredSourceCapabilities(pack, number).map { it.claim }.toSet()
    }

    private fun assertFails(pack: V2CurriculumPack, vararg needles: String) {
        val errors = V2Validator.validate(pack).errors.joinToString("\n")
        assertFalse(errors, V2Validator.validate(pack).ok)
        needles.forEach { needle -> assertTrue(errors, errors.contains(needle)) }
    }

    private fun pack() = V2PackLoader.parse(File(repo(), "app/src/main/assets/content/v2-curriculum.json").readText())

    private fun repo(): File = listOf(File("."), File("..")).first { File(it, "app/src/main").isDirectory }
}
