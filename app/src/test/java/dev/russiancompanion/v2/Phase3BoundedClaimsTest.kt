package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.ContentAuditCapabilityPins
import dev.russiancompanion.v2.curriculum.ContentAuditSources
import dev.russiancompanion.v2.curriculum.Phase3Sources
import dev.russiancompanion.v2.curriculum.RemainingSources
import dev.russiancompanion.v2.curriculum.V2CurriculumPack
import dev.russiancompanion.v2.curriculum.V2LearnerView
import dev.russiancompanion.v2.curriculum.V2PackLoader
import dev.russiancompanion.v2.curriculum.V2SourceCapability
import dev.russiancompanion.v2.curriculum.V2StartingPoint
import dev.russiancompanion.v2.curriculum.V2Validator
import dev.russiancompanion.v2.curriculum.advancementDecision
import dev.russiancompanion.v2.curriculum.unit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.security.MessageDigest

/**
 * Phase 3 admits six bounded claims. A wrong identity, a paired claim,
 * or a readiness edit does not clear them. Activated boundaries are not qualified review.
 */
class Phase3BoundedClaimsTest {
    @Test
    fun liveClaimsResolveOnlyTheBoundedSynthesis() {
        val pack = pack()
        assertTrue(V2Validator.validate(pack).errors.joinToString("\n"), V2Validator.validate(pack).ok)
        assertEquals(emptySet<String>(), unresolved(pack, 26))
        assertEquals(emptySet<String>(), unresolved(pack, 27))
        assertEquals(emptySet<String>(), unresolved(pack, 29))
        assertEquals(emptySet<String>(), unresolved(pack, 30))
        listOf(26, 27, 29, 30).forEach { number ->
            assertEquals("CONTENT_COMPLETE_REVIEW_PENDING", pack.unit(number)!!.readiness)
        }
        listOf(26, 27, 29, 30).forEach { number ->
            val unit = pack.unit(number)!!
            assertEquals("DEVELOPMENT", unit.installState)
            assertEquals("PENDING_CURRICULUM_REVIEWER", unit.mappingStatus)
            assertTrue(V2Validator.contentCompletenessErrors(pack, unit).joinToString("\n"), V2Validator.contentCompletenessErrors(pack, unit).isEmpty())
        }
        val unit26 = pack.unit(26)!!
        assertTrue(advancementDecision(pack, unit26, learner(unit26)).remaining.joinToString("\n"), advancementDecision(pack, unit26, learner(unit26)).allowed)
        listOf(27, 29, 30).forEach { number ->
            val unit = pack.unit(number)!!
            val decision = advancementDecision(pack, unit, learner(unit))
            assertTrue(decision.remaining.joinToString("\n"), decision.allowed)
            assertFalse(decision.remaining.any { it.contains("unresolved authority variance") })
            assertTrue(V2Validator.activatedAuthorityVariance(number))
            assertFalse(V2Validator.authorityVarianceApplies(number))
        }
        assertFalse(V2Validator.authorityVarianceApplies(26))
        assertFalse(V2Validator.activatedAuthorityVariance(26))
        Phase3Sources.ADMISSIONS.forEach { admission ->
            val records = pack.provenanceRecords.filter { it.sourceRecordId == admission.recordId }
            assertTrue(records.isNotEmpty())
            records.forEach { record ->
                val synthesis = record.synthesis!!
                assertEquals(Phase3Sources.SOURCE_ID, record.sourceId)
                assertEquals(Phase3Sources.REVISION, record.sourceRevision)
                assertEquals(Phase3Sources.DATE, synthesis.sourceDateOrVersion)
                assertEquals(admission.binding, synthesis.objectiveOrClaimBinding)
                assertTrue(synthesis.bibliography.isNotBlank())
                assertTrue(synthesis.authorshipDeclaration.contains("original research synthesis"))
                assertEquals("", synthesis.finalAudit.artifactIdentity)
                assertEquals("", synthesis.finalAudit.disposition)
                assertEquals("", synthesis.finalAudit.date)
                assertTrue(synthesis.applications.isNotEmpty())
                assertEquals(REVIEW, record.reviewStatus)
            }
            val checks = pack.items.filter { it.objectiveId == admission.objectiveId && it.assessment?.independent == true }
            assertTrue(checks.map { it.assessment!!.testedFeature }.containsAll(ContentAuditCapabilityPins.all.firstOrNull { it.claim == admission.claim }?.features ?: admission.independentFeatures))
            checks.forEach { item ->
                assertEquals("STATED_APPLICATION", item.assessment!!.relationKind)
                assertTrue(item.feedback.contains(item.answer))
                item.options.forEachIndexed { index, option ->
                    if (option != item.answer) {
                        assertTrue(item.id, item.distractorRationales[index].contains(option))
                    }
                }
            }
        }
        assertTrue(V2Validator.ADMITTED_PARTICIPLE_COMPREHENSION.isEmpty())
        assertTrue(V2Validator.ADMITTED_BOOKISH_READING.isEmpty())
        assertFalse(File(repo(), "audit/final-curriculum-audit").exists())
        assertTrue(pack.unitReviews.all { it.status == REVIEW })
        assertTrue(pack.units.all { it.installState == "DEVELOPMENT" })
    }

    @Test
    fun identitySpanAndRelabelMutationsStayClosed() {
        val pack = pack()
        Phase3Sources.ADMISSIONS.forEach { admission ->
            val live = pack.sourceCapabilities.single { it.supportedClaim == admission.claim }
            listOf(
                live.copy(id = "cap-phase3-source", sourceId = "src-openrussian-50e210c4"),
                live.copy(id = "cap-phase3-revision", sourceRevision = "phase2-2026-09-29"),
                live.copy(id = "cap-phase3-record", sourceRecordId = "other-record"),
                live.copy(id = "cap-phase3-hash", sourceHash = "0".repeat(64)),
                live.copy(id = "cap-phase3-span", exactSpan = "altered text that is not the frozen span"),
                live.copy(id = "cap-phase3-unit", unitNumber = if (live.unitNumber == 26) 28 else 26),
                live.copy(id = "cap-phase3-objective", objectiveId = "obj-u1-alphabet"),
                live.copy(id = "cap-phase3-claim", supportedClaim = "register"),
            ).forEach { broken ->
                val replaced = replace(pack, live.id, broken)
                val found = errors(replaced)
                assertTrue(broken.id + "\n" + found, found.contains("no independent") || found.contains("wrong unit or objective") || found.contains("revision does not match") || found.contains("span is not in the frozen record") || found.contains("does not clear") || found.contains("Source capability ${broken.id} corrected intake"))
                assertTrue(broken.id, unresolved(replaced, admission.unitNumber).contains(admission.claim) || broken.supportedClaim != admission.claim || broken.unitNumber != admission.unitNumber)
            }
            val dictionary = live.copy(
                id = "cap-phase3-dictionary",
                sourceId = "src-openrussian-50e210c4",
                sourceRecordId = "dictionary-row",
                exactSpan = "dictionary row",
            )
            assertTrue(errors(replace(pack, live.id, dictionary)).contains("a dictionary row does not clear ${admission.claim}"))
            val book = live.copy(id = "cap-phase3-book", exactSpan = "Self-attest only after the Lesson work in your copy.")
            val bookErrors = errors(replace(pack, live.id, book))
            assertTrue(bookErrors, bookErrors.contains("book self-attestation does not clear ${admission.claim}") || bookErrors.contains("Source capability ${book.id} corrected intake"))
            assertTrue(unresolved(replace(pack, live.id, book), admission.unitNumber).contains(admission.claim))
            val keywordSpan = "${admission.claim} co-occurrence"
            val keyword = live.copy(
                id = "cap-phase3-keyword",
                sourceId = "src-phase3-keyword",
                sourceRevision = "keyword",
                sourceRecordId = "keyword:${admission.claim}",
                sourceHash = sha256(keywordSpan),
                exactSpan = keywordSpan,
            )
            val keywordHost = pack.provenanceRecords.first { it.sourceRecordId == admission.recordId }.copy(
                itemId = "prov-${keyword.id}",
                sourceId = keyword.sourceId,
                sourceRevision = keyword.sourceRevision,
                sourceRecordId = keyword.sourceRecordId,
                sourceHash = keyword.sourceHash,
                originalSourceText = keywordSpan,
                shippedText = keywordSpan,
                contentHash = sha256(keywordSpan),
                licenseId = "CC BY-SA 4.0",
                synthesis = null,
            )
            val keywordPack = replace(pack, live.id, keyword).copy(provenanceRecords = pack.provenanceRecords + keywordHost)
            val keywordErrors = errors(keywordPack)
            assertTrue(keywordErrors, keywordErrors.contains("keyword or co-occurrence evidence does not clear ${admission.claim}"))
            assertTrue(keywordErrors, keywordErrors.contains("no independent ${admission.claim} admission"))
            assertTrue(unresolved(keywordPack, admission.unitNumber).contains(admission.claim))
            val stamped = pack.copy(
                provenanceRecords = pack.provenanceRecords.map { it.copy(reviewStatus = "APPROVED") },
                admissions = pack.admissions.copy(penguinProseAssets = 3, generatedRussianWords = 2),
            )
            assertFalse(unresolved(stamped, admission.unitNumber).contains(admission.claim))
            val removed = pack.copy(sourceCapabilities = pack.sourceCapabilities.filter { it.id != live.id })
            val stampedRemoved = removed.copy(
                provenanceRecords = removed.provenanceRecords.map { it.copy(reviewStatus = "APPROVED") },
                admissions = removed.admissions.copy(penguinProseAssets = 4, generatedRussianWords = 5),
            )
            assertTrue(unresolved(stampedRemoved, admission.unitNumber).contains(admission.claim))
            val flipped = removed.copy(
                units = removed.units.map { unit ->
                    if (unit.number == admission.unitNumber) unit.copy(readiness = "CONTENT_COMPLETE_REVIEW_PENDING") else unit
                },
            )
            assertTrue(unresolved(flipped, admission.unitNumber).contains(admission.claim))
            assertFalse(advancementDecision(flipped, flipped.unit(admission.unitNumber)!!, learner(flipped.unit(admission.unitNumber)!!)).allowed)
        }
    }

    @Test
    fun pairedClaimsAndNeighboringEvidenceStayIndependent() {
        val pack = pack()
        swap(pack, "participle-stress", "participle-comprehension", "participle-stress evidence does not clear participle-comprehension")
        swap(pack, "participle-comprehension", "participle-stress", "participle-comprehension evidence does not clear participle-stress")
        swap(pack, "bookish-register", "bookish-reading", "bookish-register evidence does not clear bookish-reading")
        swap(pack, "bookish-reading", "bookish-register", "bookish-reading evidence does not clear bookish-register")

        val postal = pack.sourceCapabilities.single { it.supportedClaim == "postal-address" }
        val letter = pack.sourceCapabilities.single { it.supportedClaim == "letter-writing" }
        val correspondence = pack.sourceCapabilities.single { it.supportedClaim == "correspondence-work" }
        val letterPack = replace(pack, postal.id, letter.copy(id = postal.id, objectiveId = postal.objectiveId, supportedClaim = postal.supportedClaim, unitNumber = 26))
        val letterErrors = errors(letterPack)
        assertTrue(letterErrors, letterErrors.contains("letter-writing evidence does not clear postal-address"))
        assertTrue(unresolved(letterPack, 26).contains("postal-address"))
        val correspondencePack = replace(pack, postal.id, correspondence.copy(id = postal.id, objectiveId = postal.objectiveId, supportedClaim = postal.supportedClaim, unitNumber = 26))
        val correspondenceErrors = errors(correspondencePack)
        assertTrue(correspondenceErrors, correspondenceErrors.contains("correspondence-work evidence does not clear postal-address"))
        assertTrue(unresolved(correspondencePack, 26).contains("postal-address"))

        val dictionary = pack.sourceCapabilities.single { it.supportedClaim == "dictionary-use" }
        val capstone = pack.sourceCapabilities.single { it.supportedClaim == "capstone-reading" }
        val capstonePack = replace(pack, dictionary.id, capstone.copy(id = dictionary.id, objectiveId = dictionary.objectiveId, supportedClaim = dictionary.supportedClaim, unitNumber = 30))
        val capstoneErrors = errors(capstonePack)
        assertTrue(capstoneErrors, capstoneErrors.contains("capstone-reading evidence does not clear dictionary-use"))
        assertTrue(unresolved(capstonePack, 30).contains("dictionary-use"))
        assertFalse(unresolved(pack, 30).contains("capstone-reading"))
        assertEquals(RemainingSources.CAPSTONE_SPAN, capstone.exactSpan)
    }

    @Test
    fun duplicateChecksMissingChainAndLayoutRecallDoNotClearTheClaim() {
        val pack = pack()
        val duplicated = pack.copy(items = pack.items.map { item ->
            if (item.id == "u26-i-postal-2") item.copy(prompt = pack.items.first { it.id == "u26-i-postal-1" }.prompt) else item
        })
        val duplicateErrors = errors(duplicated)
        assertTrue(duplicateErrors, duplicateErrors.contains("duplicate assessment checks do not clear postal-address"))
        assertTrue(unresolved(duplicated, 26).contains("postal-address"))

        val layout = pack.copy(items = pack.items.map { item ->
            if (item.id == "u29-i-reading-1") item.copy(prompt = "Which admitted line label is written beside the shown form?") else item
        })
        val layoutErrors = errors(layout)
        assertTrue(layoutErrors, layoutErrors.contains("label or source-layout recall does not clear bookish-reading"))
        assertTrue(unresolved(layout, 29).contains("bookish-reading"))

        val generic = pack.copy(items = pack.items.map { item ->
            if (item.id != "u30-i-dictionary-1") {
                item
            } else {
                item.copy(distractorRationales = item.distractorRationales.map { if (it.isBlank()) it else "The other option is different." })
            }
        })
        val genericErrors = errors(generic)
        assertTrue(genericErrors, genericErrors.contains("generic feedback does not clear dictionary-use"))
        assertTrue(unresolved(generic, 30).contains("dictionary-use"))

        listOf(
            "instructionStepIds" to "missing instruction",
            "practiceStepIds" to "missing guided practice",
            "checkItemIds" to "missing independent check",
            "remediationStepIds" to "missing remediation",
        ).forEach { (field, message) ->
            val stripped = pack.copy(units = pack.units.map { unit ->
                if (unit.number != 27) {
                    unit
                } else {
                    unit.copy(objectives = unit.objectives.map { objective ->
                        if (objective.id != "obj-u27-stress") {
                            objective
                        } else when (field) {
                            "instructionStepIds" -> objective.copy(instructionStepIds = emptyList())
                            "practiceStepIds" -> objective.copy(practiceStepIds = emptyList())
                            "checkItemIds" -> objective.copy(checkItemIds = emptyList())
                            else -> objective.copy(remediationStepIds = emptyList())
                        }
                    })
                }
            })
            val found = errors(stripped)
            assertTrue(field + "\n" + found, found.contains(message))
            assertTrue(unresolved(stripped, 27).contains("participle-stress"))
            assertFalse(unresolved(stripped, 27).contains("participle-comprehension"))
        }

        val silent = pack.copy(items = pack.items.map { item ->
            if (item.id == "u29-i-register-1") item.copy(feedback = "") else item
        })
        val silentErrors = errors(silent)
        assertTrue(silentErrors, silentErrors.contains("missing feedback"))
        assertTrue(unresolved(silent, 29).contains("bookish-register"))
        assertFalse(unresolved(silent, 29).contains("bookish-reading"))
    }

    private fun swap(pack: V2CurriculumPack, fromClaim: String, ontoClaim: String, message: String) {
        val from = pack.sourceCapabilities.single { it.supportedClaim == fromClaim }
        val onto = pack.sourceCapabilities.single { it.supportedClaim == ontoClaim }
        val moved = replace(
            pack,
            onto.id,
            from.copy(
                id = onto.id,
                unitNumber = onto.unitNumber,
                objectiveId = onto.objectiveId,
                supportedClaim = onto.supportedClaim,
            ),
        )
        val found = errors(moved)
        assertTrue(found, found.contains(message) ||
            ((from.sourceId == ContentAuditSources.SOURCE_ID || onto.sourceId == ContentAuditSources.SOURCE_ID) &&
                found.contains("Source capability ${onto.id} corrected intake")))
        assertTrue(unresolved(moved, onto.unitNumber).contains(ontoClaim))
        assertFalse(unresolved(pack, from.unitNumber).contains(fromClaim))
    }

    private fun replace(pack: V2CurriculumPack, id: String, capability: V2SourceCapability): V2CurriculumPack =
        pack.copy(sourceCapabilities = pack.sourceCapabilities.filter { it.id != id } + capability)

    private fun errors(pack: V2CurriculumPack): String = V2Validator.validate(pack).errors.joinToString("\n")

    private fun unresolved(pack: V2CurriculumPack, number: Int): Set<String> =
        V2Validator.unresolvedRequiredSourceCapabilities(pack, number).map { it.claim }.toSet()

    private fun learner(unit: dev.russiancompanion.v2.curriculum.V2Unit) = V2LearnerView(
        onboardingCompleted = true,
        currentUnitNumber = unit.number,
        startingPoint = V2StartingPoint.NEW_TO_CYRILLIC,
        hasPenguin = true,
        hasAnki = false,
        hasBinder = false,
        hasTutor = false,
        dailyMinutes = 25,
        transliterationEnabled = true,
        currentSessionId = null,
        currentStepIndex = 0,
        completedStepIds = unit.sessions.flatMap { it.steps }.map { it.id }.toSet(),
        correctItemIds = (
            unit.objectives.flatMap { it.checkItemIds } +
                unit.sessions.flatMap { it.steps }.flatMap { it.itemIds }
            ).toSet(),
        bookAttestedUnits = setOf(unit.number),
        completedUnitNumbers = emptySet(),
    )

    private fun pack() = V2PackLoader.parse(File(repo(), "app/src/main/assets/content/v2-curriculum.json").readText())

    private fun repo(): File = listOf(File("."), File("..")).first { File(it, "app/src/main").isDirectory }

    private fun sha256(text: String): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(text.toByteArray(Charsets.UTF_8))
        return digest.joinToString("") { "%02x".format(it) }
    }

    private companion object {
        const val REVIEW = "IMPLEMENTED_PENDING_FINAL_AUDIT"
    }
}
