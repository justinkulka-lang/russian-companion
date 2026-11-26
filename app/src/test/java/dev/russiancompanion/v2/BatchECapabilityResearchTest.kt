package dev.russiancompanion.v2

import dev.russiancompanion.release.computeReleaseSourceDigest
import dev.russiancompanion.release.readAcceptanceCandidate
import dev.russiancompanion.v2.audit.loadCurriculumPack
import dev.russiancompanion.v2.curriculum.V2LearnerView
import dev.russiancompanion.v2.curriculum.V2SourceCapability
import dev.russiancompanion.v2.curriculum.V2StartingPoint
import dev.russiancompanion.v2.curriculum.V2Validator
import dev.russiancompanion.v2.curriculum.advancementDecision
import dev.russiancompanion.v2.curriculum.ordinaryUnitRequirements
import dev.russiancompanion.v2.curriculum.unit
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.security.MessageDigest

/**
 * Batch E searched five unresolved capabilities and retained the pages it
 * inspected. None of those pages is an admission. Readiness and advancement
 * stay closed, and Units 26–30 stay unchanged.
 */
class BatchECapabilityResearchTest {

    @Test
    fun identityMutationsSubstitutionsAndRelabelingFailClosed() {
        val pack = loadCurriculumPack(root())
        val claims = listOf(
            Triple(7, "obj-u7-adverb", "adverb-paradigm"),
            Triple(17, "obj-u17-clock-answers", "productive-clock-time"),
            Triple(23, "obj-u23-completion", "completion-over-period"),
            Triple(24, "obj-u24-placement", "negation-scope"),
            Triple(25, "obj-u25-nickname-rule", "productive-diminutive"),
        )
        claims.forEach { (unitNumber, objectiveId, claim) ->
            val keyword = when (claim) {
                "adverb-paradigm" -> "adverb paradigm co-occurrence"
                "productive-clock-time" -> "productive clock co-occurrence"
                "completion-over-period" -> "completion co-occurrence"
                "negation-scope" -> "negation scope co-occurrence"
                else -> "productive diminutive co-occurrence"
            }
            val keywordErrors = planted(pack, probe(unitNumber, objectiveId, claim, keyword))
            assertTrue(keywordErrors, keywordErrors.contains("keyword or co-occurrence evidence does not clear $claim"))
            assertTrue(keywordErrors, keywordErrors.contains("no independent $claim admission"))
            val dictionary = probe(unitNumber, objectiveId, claim, "dictionary row").copy(
                sourceId = "src-openrussian-50e210c4",
                sourceRecordId = "dictionary-row",
            )
            assertTrue(errors(pack, dictionary).contains("a dictionary row does not clear $claim"))
            val book = probe(unitNumber, objectiveId, claim, "Self-attest only after the Lesson work in your copy.")
            assertTrue(errors(pack, book).contains("book self-attestation does not clear $claim"))
            val wrongObjective = probe(unitNumber, "obj-u1-alphabet", claim, "wrong objective")
            assertTrue(errors(pack, wrongObjective).contains("wrong unit or objective"))
            val wrongUnit = probe(if (unitNumber == 7) 8 else 7, objectiveId, claim, "wrong unit")
            assertTrue(errors(pack, wrongUnit).contains("wrong unit or objective"))
            val wrongClaim = probe(unitNumber, objectiveId, "register", "wrong claim")
            assertTrue(errors(pack, wrongClaim).contains("wrong unit or objective"))
        }

        val gloss = pack.sourceCapabilities.single { it.id == "cap-u7-adverb-form-gloss" }
        assertTrue(errors(pack, gloss.copy(id = "cap-e-rev", sourceRevision = "1")).contains("revision does not match the frozen record"))
        assertTrue(errors(pack, gloss.copy(id = "cap-e-record", sourceRecordId = "other-record")).contains("revision does not match the frozen record"))
        assertTrue(errors(pack, gloss.copy(id = "cap-e-source", sourceId = "src-ruwiki-batch-e-117812844")).contains("revision does not match the frozen record"))
        assertTrue(errors(pack, gloss.copy(id = "cap-e-hash", sourceHash = "0".repeat(64))).contains("revision does not match the frozen record"))
        assertTrue(errors(pack, gloss.copy(id = "cap-e-span", exactSpan = "not the frozen span")).contains("span is not in the frozen record"))
        assertTrue(errors(pack, gloss.copy(id = "cap-e-unit", unitNumber = 17, objectiveId = "obj-u17-clock-answers", supportedClaim = "productive-clock-time")).contains("revision does not match the frozen record") ||
            errors(pack, gloss.copy(id = "cap-e-unit", unitNumber = 17, objectiveId = "obj-u17-clock-answers", supportedClaim = "productive-clock-time")).contains("wrong unit or objective") ||
            errors(pack, gloss.copy(id = "cap-e-unit", unitNumber = 17, objectiveId = "obj-u17-clock-answers", supportedClaim = "productive-clock-time")).contains("does not clear"))
        val relabeledGloss = gloss.copy(id = "cap-e-relabel-adverb", supportedClaim = "adverb-paradigm")
        val glossErrors = planted(pack, relabeledGloss)
        assertTrue(glossErrors, glossErrors.contains("does not clear adverb-paradigm"))
        assertFalse(V2Validator.unresolvedRequiredSourceCapabilities(
            pack.copy(sourceCapabilities = pack.sourceCapabilities + relabeledGloss),
            7,
        ).any { it.claim == "adverb-paradigm" || it.claim == "bounded-manner-adverb-formation" })

        val quarter = pack.sourceCapabilities.single { it.supportedClaim == "productive-quarter-hour-time" }
        val relabeledQuarter = quarter.copy(
            id = "cap-e-relabel-clock",
            objectiveId = "obj-u17-clock-answers",
            supportedClaim = "productive-clock-time",
        )
        assertTrue(planted(pack, relabeledQuarter).contains("quarter-hour patterns do not clear productive-clock-time"))
        val duration = pack.sourceCapabilities.single { it.supportedClaim == "elapsed-time-duration" }
        val relabeledDuration = duration.copy(
            id = "cap-e-relabel-completion",
            objectiveId = "obj-u23-completion",
            supportedClaim = "completion-over-period",
        )
        assertTrue(planted(pack, relabeledDuration).contains("duration evidence does not clear completion-over-period"))
        val alternation = pack.sourceCapabilities.single { it.supportedClaim == "negation-alternation" }
        val relabeledAlternation = alternation.copy(
            id = "cap-e-relabel-scope",
            objectiveId = "obj-u24-placement",
            supportedClaim = "negation-scope",
        )
        assertTrue(planted(pack, relabeledAlternation).contains("does not clear negation-scope"))
        val classification = pack.sourceCapabilities.single { it.supportedClaim == "diminutive-form-classification" }
        val relabeledClassification = classification.copy(id = "cap-e-relabel-diminutive", supportedClaim = "productive-diminutive")
        val diminutiveErrors = errors(pack, relabeledClassification)
        assertTrue(diminutiveErrors, diminutiveErrors.contains("bounded diminutive recognition does not clear productive-diminutive"))
        assertTrue(diminutiveErrors, diminutiveErrors.contains("no independent application check for productive-diminutive"))

        val stamped = pack.copy(
            provenanceRecords = pack.provenanceRecords.map { it.copy(reviewStatus = "APPROVED") },
            admissions = pack.admissions.copy(penguinProseAssets = 9, generatedRussianWords = 4),
        )
        claims.forEach { (unitNumber, _, claim) ->
            assertFalse(
                V2Validator.unresolvedRequiredSourceCapabilities(stamped, unitNumber).any { it.claim == claim },
            )
        }
    }

    @Test
    fun missingChainAndReadinessFlipKeepAdvancementBlocked() {
        val pack = loadCurriculumPack(root())
        val gaps = mapOf(
            7 to "without an admitted bounded manner-adverb formation source capability",
            17 to "Unit 17 is missing productive clock-time and calendar-date source capabilities",
            23 to "completion-over-period",
            24 to "scope source capabilities",
            25 to "without admitted documented-name-form and register source capabilities",
        )
        val blockers = mapOf(
            7 to "bounded manner-adverb formation",
            17 to "productive clock-time",
            23 to "completion-over-period",
            24 to "negation-scope",
            25 to "documented-name-form",
        )
        val closed = setOf(7, 17, 23, 24, 25)
        gaps.forEach { (unitNumber, gap) ->
            val unit = pack.unit(unitNumber)!!
            assertEquals("DEVELOPMENT", unit.installState)
            val learner = prepared(unit)
            if (unitNumber in closed) {
                assertEquals("CONTENT_COMPLETE_REVIEW_PENDING", unit.readiness)
                val advanced = ordinaryUnitRequirements(pack, unit, learner)
                assertTrue(advanced.remaining.joinToString("\n"), advanced.allowed)
                assertAssignedReviewStillRequired(pack, unit, learner)
            } else {
                assertEquals("NOT_PREPARED", unit.readiness)
                val blocked = advancementDecision(pack, unit, learner)
                assertFalse(blocked.allowed)
                assertTrue(blocked.remaining.any { it.contains("not prepared") })
                assertTrue(blocked.remaining.any { it.contains(blockers.getValue(unitNumber)) })
            }

            val stripped = pack.copy(
                units = pack.units.map { current ->
                    if (current.number != unitNumber) {
                        current
                    } else {
                        current.copy(
                            readiness = "CONTENT_COMPLETE_REVIEW_PENDING",
                            objectives = current.objectives.map { objective ->
                                objective.copy(
                                    instructionStepIds = emptyList(),
                                    exampleIds = emptyList(),
                                    practiceStepIds = emptyList(),
                                    checkItemIds = emptyList(),
                                    remediationStepIds = emptyList(),
                                )
                            },
                        )
                    }
                },
            )
            val flippedErrors = V2Validator.validate(stripped).errors.joinToString("\n")
            assertTrue(flippedErrors, flippedErrors.contains(gap))
            assertFalse(flippedErrors, flippedErrors.contains("telling-time, weekday, date, and age"))
            assertTrue(flippedErrors, flippedErrors.contains("missing instruction, examples, guided practice, independent checks, or remediation"))
            val decision = advancementDecision(stripped, stripped.unit(unitNumber)!!, learner)
            assertFalse(decision.allowed)
            assertTrue(decision.remaining.joinToString("\n"), decision.remaining.any { it.contains(blockers.getValue(unitNumber)) })
            val ordinary = ordinaryUnitRequirements(stripped, stripped.unit(unitNumber)!!, learner)
            assertFalse(ordinary.allowed)
            assertTrue(ordinary.remaining.joinToString("\n"), ordinary.remaining.any { it.contains(blockers.getValue(unitNumber)) })
            assertEquals(ordinary.remaining + "Complete the assigned earlier-objective review", decision.remaining)
        }
    }

    @Test
    fun unit17MessagesFollowTheUnresolvedCapabilitySet() {
        val pack = loadCurriculumPack(root())
        val clockOnly = "Unit 17 is missing a productive clock-time source capability"
        val dateOnly = "Unit 17 is missing a calendar-date source capability"
        val both = "Unit 17 is missing productive clock-time and calendar-date source capabilities"
        assertEquals(listOf(clockOnly), V2Validator.unit17SourceBlockers(setOf("productive-clock-time")))
        assertEquals(listOf(dateOnly), V2Validator.unit17SourceBlockers(setOf("calendar-date")))
        assertEquals(
            listOf(both),
            V2Validator.unit17SourceBlockers(setOf("productive-clock-time", "calendar-date")),
        )
        assertEquals(emptyList<String>(), V2Validator.unit17SourceBlockers(emptySet()))
        assertFalse(V2Validator.unit17SourceBlockers(setOf("productive-clock-time")).any { it.contains("calendar-date") })
        assertFalse(V2Validator.unit17SourceBlockers(setOf("calendar-date")).any { it.contains("clock-time") })
        assertFalse(V2Validator.unit17SourceBlockers(setOf("productive-clock-time")).any { it.contains("weekday") || it.contains("age") })

        val liveClaims = V2Validator.unresolvedRequiredSourceCapabilities(pack, 17).map { it.claim }.toSet()
        assertEquals(emptySet<String>(), liveClaims)
        val liveUnit = pack.unit(17)!!
        val liveErrors = V2Validator.validate(pack).errors
        assertEquals(emptyList<String>(), liveErrors.filter { it.startsWith("Unit 17 is missing") })
        assertFalse(liveErrors.joinToString("\n"), liveErrors.any { it.contains("telling-time") || it.contains("weekday") || it.contains("age sources") })
        val liveDecision = ordinaryUnitRequirements(pack, liveUnit, prepared(liveUnit))
        assertTrue(liveDecision.remaining.joinToString("\n"), liveDecision.allowed)
        assertAssignedReviewStillRequired(pack, liveUnit, prepared(liveUnit))
        assertFalse(liveDecision.remaining.any { it.contains("calendar-date") || it.contains("weekday") || it.contains("age") || it.contains("clock-time") })

        val withoutClock = pack.copy(
            sourceCapabilities = pack.sourceCapabilities.filter { it.supportedClaim != "productive-clock-time" },
        )
        val clockErrors = V2Validator.validate(withoutClock).errors
        assertEquals(listOf(clockOnly), clockErrors.filter { it.startsWith("Unit 17 is missing") })
        val withoutDate = pack.copy(
            sourceCapabilities = pack.sourceCapabilities.filter { it.supportedClaim != "calendar-date" },
        )
        val dateErrors = V2Validator.validate(withoutDate).errors
        assertEquals(listOf(dateOnly), dateErrors.filter { it.startsWith("Unit 17 is missing") })
        val withoutBoth = pack.copy(
            sourceCapabilities = pack.sourceCapabilities.filter {
                it.supportedClaim != "productive-clock-time" && it.supportedClaim != "calendar-date"
            },
        )
        val bothErrors = V2Validator.validate(withoutBoth).errors
        assertEquals(listOf(both), bothErrors.filter { it.startsWith("Unit 17 is missing") })

        listOf(
            "clock-expression-recognition",
            "clock-question",
            "age",
            "weekdays",
            "calendar-date",
            "productive-quarter-hour-time",
        ).forEach { claim ->
            val source = pack.sourceCapabilities.single { it.unitNumber == 17 && it.supportedClaim == claim }
            val relabeled = source.copy(
                id = "cap-e-relabel-$claim",
                objectiveId = "obj-u17-clock-answers",
                supportedClaim = "productive-clock-time",
            )
            val errors = planted(pack, relabeled)
            assertTrue(errors, errors.contains("no independent productive-clock-time admission"))
            if (claim == "clock-expression-recognition") {
                assertTrue(errors, errors.contains("bounded clock phrases do not clear productive-clock-time"))
            }
            if (claim == "productive-quarter-hour-time") {
                assertTrue(errors, errors.contains("quarter-hour patterns do not clear productive-clock-time"))
            }
            val hosted = pack.copy(sourceCapabilities = pack.sourceCapabilities + relabeled)
            assertFalse(
                V2Validator.unresolvedRequiredSourceCapabilities(hosted, 17).any { it.claim == "productive-clock-time" },
            )
        }
    }


    private fun probe(unitNumber: Int, objectiveId: String, claim: String, span: String) = V2SourceCapability(
        id = "cap-e-$unitNumber-$claim-${span.length}",
        sourceId = "src-batch-e-probe",
        sourceRevision = "0",
        sourceRecordId = "probe",
        sourceHash = "ab".repeat(32),
        exactSpan = span,
        unitNumber = unitNumber,
        objectiveId = objectiveId,
        supportedClaim = claim,
    )

    private fun errors(pack: dev.russiancompanion.v2.curriculum.V2CurriculumPack, capability: V2SourceCapability): String {
        return V2Validator.validate(
            pack.copy(sourceCapabilities = pack.sourceCapabilities + capability),
        ).errors.joinToString("\n")
    }

    private fun planted(pack: dev.russiancompanion.v2.curriculum.V2CurriculumPack, capability: V2SourceCapability): String {
        val matched = pack.provenanceRecords.filter {
            it.sourceId == capability.sourceId &&
                it.sourceRevision == capability.sourceRevision &&
                it.sourceRecordId == capability.sourceRecordId &&
                it.sourceHash == capability.sourceHash &&
                it.originalSourceText == capability.exactSpan
        }
        val hosted = if (matched.isNotEmpty()) {
            pack.copy(
                provenanceRecords = pack.provenanceRecords.map { record ->
                    if (record in matched) {
                        record.copy(unitNumber = capability.unitNumber, objectiveId = capability.objectiveId)
                    } else {
                        record
                    }
                },
            )
        } else {
            val template = pack.provenanceRecords.first { it.unitNumber == capability.unitNumber }
            val record = template.copy(
                itemId = "prov-${capability.id}",
                unitNumber = capability.unitNumber,
                objectiveId = capability.objectiveId,
                originalSourceText = capability.exactSpan,
                shippedText = capability.exactSpan.lineSequence().first { it.any(Char::isLetter) }.take(48),
                sourceId = capability.sourceId,
                sourceRevision = capability.sourceRevision,
                sourceRecordId = capability.sourceRecordId,
                sourceHash = sha256(capability.exactSpan.toByteArray(Charsets.UTF_8)),
            )
            pack.copy(provenanceRecords = pack.provenanceRecords + record)
        }
        val bound = if (matched.isNotEmpty()) {
            capability
        } else {
            capability.copy(sourceHash = sha256(capability.exactSpan.toByteArray(Charsets.UTF_8)))
        }
        return errors(hosted, bound)
    }

    /** Prepared unit-local evidence does not include assigned cumulative-review work. */
    private fun assertAssignedReviewStillRequired(
        pack: dev.russiancompanion.v2.curriculum.V2CurriculumPack,
        unit: dev.russiancompanion.v2.curriculum.V2Unit,
        learner: V2LearnerView,
    ) {
        val decision = advancementDecision(pack, unit, learner)
        assertFalse(decision.allowed)
        assertEquals(listOf("Complete the assigned earlier-objective review"), decision.remaining)
    }

    private fun prepared(unit: dev.russiancompanion.v2.curriculum.V2Unit) = V2LearnerView(
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

    private fun sha256(bytes: ByteArray): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(bytes)
        return digest.joinToString("") { "%02x".format(it) }
    }

    private fun root(): File = listOf(File("."), File("..")).first { File(it, "app/src/main").isDirectory }

    private companion object {
        val REQUIRED = mapOf(
            "adverb-paradigm" to "obj-u7-adverb",
            "productive-clock-time" to "obj-u17-clock-answers",
            "completion-over-period" to "obj-u23-completion",
            "negation-scope" to "obj-u24-placement",
            "productive-diminutive" to "obj-u25-nickname-rule",
        )
    }
}
