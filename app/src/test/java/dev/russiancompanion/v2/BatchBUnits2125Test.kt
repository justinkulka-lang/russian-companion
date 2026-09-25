package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.BatchBSources
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
import java.security.MessageDigest

/**
 * Batch B fail-closed checks for Units 21-25.
 * A co-occurring word, a neighboring gloss, or a bounded example is not a capability.
 */
class BatchBUnits2125Test {
    @Test
    fun admittedCapabilitiesMatchFrozenIdentityAndLeaveUnresolvedClaimsClosed() {
        val pack = pack()
        val errors = V2Validator.validate(pack).errors
        assertTrue(errors.joinToString("\n"), errors.isEmpty())
        val expected = listOf(
            "reflexive-object" to "obj-u21-object-reference",
            "purpose-clause" to "obj-u21-purpose",
            "declined-numeral" to "obj-u22-declension",
            "both-forms" to "obj-u22-both-forms",
            "one-and-a-half" to "obj-u22-half-forms",
            "collective-numeral" to "obj-u22-collective",
            "negation-alternation" to "obj-u24-alternation",
            "negative-pronoun" to "obj-u24-stand-ins",
            "register" to "obj-u25-address",
        )
        expected.forEach { (claim, objectiveId) ->
            val capability = pack.sourceCapabilities.single { it.supportedClaim == claim }
            assertEquals(objectiveId, capability.objectiveId)
            val record = pack.provenanceRecords.first {
                it.objectiveId == objectiveId && it.originalSourceText == capability.exactSpan
            }
            assertEquals(capability.sourceId, record.sourceId)
            assertEquals(capability.sourceRevision, record.sourceRevision)
            assertEquals(capability.sourceRecordId, record.sourceRecordId)
            assertEquals(capability.sourceHash, record.sourceHash)
            assertEquals(capability.sourceHash, sha256(record.originalSourceText))
            assertTrue(record.sourceUrl.contains("oldid=${capability.sourceRevision}"))
        }
        assertTrue(
            pack.sourceCapabilities.none {
                it.supportedClaim in setOf("elapsed-time-duration", "negation-scope", "productive-diminutive")
            },
        )
        listOf(21, 22).forEach { number ->
            assertEquals("CONTENT_COMPLETE_REVIEW_PENDING", pack.unit(number)!!.readiness)
            assertEquals("DEVELOPMENT", pack.unit(number)!!.installState)
        }
        listOf(23, 24, 25).forEach { number ->
            assertEquals("NOT_PREPARED", pack.unit(number)!!.readiness)
            assertEquals("DEVELOPMENT", pack.unit(number)!!.installState)
        }
        assertTrue(pack.unitReviews.filter { it.unitNumber in 21..25 }.all {
            it.status == "PENDING_QUALIFIED_REVIEW" && it.reviewer.isNullOrBlank()
        })
    }

    @Test
    fun governedPairsRejectCoOccurrenceAndASubstitutedGloss() {
        val pack = pack()
        val self = shipped(pack, "obj-u21-object-reference", "\u042f \u043b\u044e\u0431\u043b\u044e\u0301 \u0441\u0435\u0431\u044f\u0301")
        val myself = shipped(pack, "obj-u21-object-reference", "I love myself")
        val wife = shipped(pack, "obj-u21-object-reference", "I love my wife")
        assertEquals("EXPLICIT_SOURCE_PAIR", V2Validator.classifyRelation(self, myself, listOf(wife)))
        assertNull(V2Validator.classifyRelation(self, wife, listOf(myself)))

        val purpose = "obj-u21-purpose"
        val soThat = shipped(pack, purpose, "so that")
        val ifGloss = shipped(pack, purpose, "if")
        val because = shipped(pack, purpose, "because")
        val chtoby = shipped(pack, purpose, "\u0447\u0442\u043e\u0431\u044b")
        assertEquals("EXPLICIT_SOURCE_PAIR", V2Validator.classifyRelation(chtoby, soThat, listOf(ifGloss, because)))
        assertNull(V2Validator.classifyRelation(chtoby, ifGloss, listOf(soThat, because)))
        assertNull(V2Validator.classifyRelation(chtoby, because, listOf(soThat)))

        val collective = "obj-u22-collective"
        val two = shipped(pack, collective, "\u0434\u0432\u0430")
        val collectiveTwo = shipped(pack, collective, "\u0434\u0432\u043e\u0301\u0435")
        val ordinalTwo = shipped(pack, collective, "\u0432\u0442\u043e\u0440\u043e\u0301\u0439")
        assertEquals("EXPLICIT_SOURCE_PAIR", V2Validator.classifyRelation(two, collectiveTwo, listOf(ordinalTwo)))
        assertNull(V2Validator.classifyRelation(two, ordinalTwo, listOf(collectiveTwo)))

        val item = pack.items.single { it.id == "u21-g-purpose-0" }
        val distractor = item.options.first { it != item.answer }
        val substituted = V2Validator.validate(replaceItem(pack, item.copy(answer = distractor))).errors.joinToString("\n")
        assertTrue(substituted, substituted.contains("${item.id} answer has no contrast provenance"))
        assertTrue(substituted, substituted.contains("${item.id} explanation does not name the keyed answer"))
        val assessment = item.assessment!!
        val swappedWhy = assessment.whyAnswerDemonstratesObjective.replace(item.answer, distractor)
        val swappedFeedback = item.feedback.replace(item.answer, distractor)
        val mutated = V2Validator.validate(
            replaceItem(
                pack,
                item.copy(
                    feedback = swappedFeedback,
                    assessment = assessment.copy(whyAnswerDemonstratesObjective = swappedWhy),
                ),
            ),
        ).errors.joinToString("\n")
        assertTrue(mutated, mutated.contains("${item.id} explanation does not name the keyed answer"))
        assertTrue(mutated, mutated.contains("${item.id} explanation names a different gloss"))
    }

    @Test
    fun wrongSourceRevisionUnitAndObjectiveFail() {
        val pack = pack()
        val capability = pack.sourceCapabilities.single { it.supportedClaim == "one-and-a-half" }
        listOf(
            capability.copy(sourceId = "src-wikibooks-russian-cc-by-sa"),
            capability.copy(sourceRevision = "9999999"),
            capability.copy(sourceHash = "0".repeat(64)),
            capability.copy(exactSpan = "1.5 roubles"),
            capability.copy(unitNumber = 21, objectiveId = "obj-u21-purpose", supportedClaim = "purpose-clause"),
            capability.copy(objectiveId = "obj-u22-declension", supportedClaim = "declined-numeral"),
        ).forEach { mutated ->
            val errors = V2Validator.validate(replaceCapability(pack, capability.id, mutated)).errors.joinToString("\n")
            assertTrue(errors, errors.contains(capability.id))
            assertFalse(V2Validator.validate(replaceCapability(pack, capability.id, mutated)).ok)
        }
    }

    @Test
    fun boundedEvidenceCannotClearUnresolvedCapabilities() {
        val pack = pack()
        val svoj = overreach(
            pack,
            "\u0441\u0432\u043e\u044e \u0436\u0435\u043d\u0443",
            21,
            "obj-u21-object-reference",
            "reflexive-object",
            "cap-svoj-only",
        )
        assertTrue(svoj, svoj.contains("no independent reflexive-object admission"))
        assertTrue(svoj, svoj.contains("\u0441\u0432\u043e\u0439 evidence does not clear reflexive-object"))

        val study = overreach(
            pack,
            BatchBSources.STUDY_SPAN,
            21,
            "obj-u21-purpose",
            "purpose-clause",
            "cap-study-span",
        )
        assertTrue(study, study.contains("no independent purpose-clause admission"))
        assertTrue(study, study.contains("a sentence containing \u0447\u0442\u043e\u0431\u044b does not clear purpose-clause"))

        val bareHalf = overreach(
            pack,
            "\u043f\u043e\u043b\u0442\u043e\u0440\u0430\u0301 \u0440\u0443\u0431\u043b\u044f\u0301",
            22,
            "obj-u22-half-forms",
            "one-and-a-half",
            "cap-half-without-note",
        )
        assertTrue(bareHalf, bareHalf.contains("no independent one-and-a-half admission"))
        assertTrue(bareHalf, bareHalf.contains("\u043f\u043e\u043b\u0442\u043e\u0440\u0430 without the usage note does not clear one-and-a-half"))

        val cardinal = overreach(
            pack,
            "cardinal numbers \u0434\u0432\u0430",
            22,
            "obj-u22-collective",
            "collective-numeral",
            "cap-cardinal-list",
        )
        assertTrue(cardinal, cardinal.contains("no independent collective-numeral admission"))
        assertTrue(cardinal, cardinal.contains("a cardinal list does not clear collective-numeral"))

        val ago = overreach(
            pack,
            BatchBSources.AGO_SPAN,
            23,
            "obj-u23-duration",
            "elapsed-time-duration",
            "cap-ago-as-duration",
        )
        assertTrue(ago, ago.contains("no independent elapsed-time-duration admission"))
        assertTrue(ago, ago.contains("bounded time expression does not clear elapsed-time-duration"))

        val concord = overreach(
            pack,
            BatchBSources.CONCORD_SPAN,
            24,
            "obj-u24-placement",
            "negation-scope",
            "cap-concord-as-scope",
        )
        assertTrue(concord, concord.contains("no independent negation-scope admission"))
        assertTrue(concord, concord.contains("negative concord does not clear negation-scope"))

        val compare = overreach(
            pack,
            BatchBSources.NEGATION_SPAN,
            24,
            "obj-u24-placement",
            "negation-scope",
            "cap-compare-as-scope",
        )
        assertTrue(compare, compare.contains("no independent negation-scope admission"))
        assertTrue(compare, compare.contains("the genitive and accusative compare does not clear negation-scope"))

        val diminutive = overreach(
            pack,
            BatchBSources.ADDRESS_SPAN,
            25,
            "obj-u25-nickname-rule",
            "productive-diminutive",
            "cap-listed-diminutive",
        )
        assertTrue(diminutive, diminutive.contains("no independent productive-diminutive admission"))
        assertTrue(diminutive, diminutive.contains("bounded diminutive recognition does not clear productive-diminutive"))
    }

    private fun overreach(
        pack: V2CurriculumPack,
        span: String,
        unitNumber: Int,
        objectiveId: String,
        claim: String,
        id: String,
    ): String {
        val existing = pack.provenanceRecords.any { it.originalSourceText == span }
        val base = if (existing) {
            pack.copy(
                provenanceRecords = pack.provenanceRecords.map { record ->
                    if (record.originalSourceText == span) {
                        record.copy(unitNumber = unitNumber, objectiveId = objectiveId)
                    } else {
                        record
                    }
                },
            )
        } else {
            val template = pack.provenanceRecords.first { it.unitNumber == unitNumber }
            val visible = span.lineSequence().first { it.any { char -> char.isLetter() } }.take(48)
            val record = template.copy(
                itemId = "prov-$id",
                unitNumber = unitNumber,
                objectiveId = objectiveId,
                originalSourceText = span,
                shippedText = visible,
                sourceRecordId = "probe:$id",
                sourceHash = sha256(span),
                contentHash = sha256(visible),
            )
            pack.copy(provenanceRecords = pack.provenanceRecords + record)
        }
        val host = base.provenanceRecords.first { it.originalSourceText == span && it.objectiveId == objectiveId }
        val capability = V2SourceCapability(
            id = id,
            sourceId = host.sourceId,
            sourceRevision = host.sourceRevision,
            sourceRecordId = host.sourceRecordId,
            sourceHash = host.sourceHash,
            exactSpan = span,
            unitNumber = unitNumber,
            objectiveId = objectiveId,
            supportedClaim = claim,
        )
        return V2Validator.validate(base.copy(sourceCapabilities = base.sourceCapabilities + capability)).errors.joinToString("\n")
    }

    private fun shipped(pack: V2CurriculumPack, objectiveId: String, text: String): V2ProvenanceRecord {
        return pack.provenanceRecords.first { it.objectiveId == objectiveId && it.shippedText == text }
    }

    private fun replaceItem(pack: V2CurriculumPack, mutated: dev.russiancompanion.v2.curriculum.V2CheckItem) =
        pack.copy(items = pack.items.map { if (it.id == mutated.id) mutated else it })

    private fun replaceCapability(pack: V2CurriculumPack, id: String, mutated: V2SourceCapability) =
        pack.copy(sourceCapabilities = pack.sourceCapabilities.map { if (it.id == id) mutated else it })

    private fun sha256(text: String): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(text.toByteArray(Charsets.UTF_8))
        return digest.joinToString("") { byte -> "%02x".format(byte) }
    }

    private fun pack() = V2PackLoader.parse(File(repo(), "app/src/main/assets/content/v2-curriculum.json").readText())

    private fun repo(): File = listOf(File("."), File("..")).first { File(it, "app/src/main").isDirectory }
}
