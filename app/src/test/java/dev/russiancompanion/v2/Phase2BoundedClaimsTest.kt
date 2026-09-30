package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.Phase2Sources
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
 * Phase 2 admits three bounded claims and keeps the retired broad claims closed.
 * A wrong identity, a cloned check, or a readiness edit does not clear them.
 */
class Phase2BoundedClaimsTest {
    @Test
    fun liveClaimsResolveOnlyTheBoundedSynthesis() {
        val pack = pack()
        assertTrue(V2Validator.validate(pack).errors.joinToString("\n"), V2Validator.validate(pack).ok)
        assertEquals(
            emptySet<String>(),
            unresolved(pack, 7),
        )
        assertEquals(emptySet<String>(), unresolved(pack, 23))
        assertEquals(emptySet<String>(), unresolved(pack, 25))
        assertEquals(emptySet<String>(), unresolved(pack, 26))
        assertEquals(emptySet<String>(), unresolved(pack, 27))
        assertEquals(emptySet<String>(), unresolved(pack, 29))
        assertEquals(emptySet<String>(), unresolved(pack, 30))
        listOf(7, 23, 25).forEach { number ->
            val unit = pack.unit(number)!!
            assertEquals("CONTENT_COMPLETE_REVIEW_PENDING", unit.readiness)
            assertEquals("DEVELOPMENT", unit.installState)
            assertEquals("PENDING_CURRICULUM_REVIEWER", unit.mappingStatus)
            assertTrue(advancementDecision(pack, unit, learner(unit)).remaining.joinToString("\n"), advancementDecision(pack, unit, learner(unit)).allowed)
        }
        assertTrue(pack.sourceCapabilities.none { it.supportedClaim == "adverb-paradigm" })
        assertTrue(pack.sourceCapabilities.none { it.supportedClaim == "productive-diminutive" })
        assertTrue(pack.unit(25)!!.objectives.none { it.id == "obj-u25-productive-formation" })
        Phase2Sources.ADMISSIONS.forEach { admission ->
            val records = pack.provenanceRecords.filter { it.sourceRecordId == admission.recordId }
            assertTrue(records.isNotEmpty())
            records.forEach { record ->
                val synthesis = record.synthesis!!
                assertEquals(Phase2Sources.SOURCE_ID, record.sourceId)
                assertEquals(Phase2Sources.REVISION, record.sourceRevision)
                assertEquals(Phase2Sources.DATE, synthesis.sourceDateOrVersion)
                assertEquals(admission.binding, synthesis.objectiveOrClaimBinding)
                assertTrue(synthesis.bibliography.isNotBlank())
                assertTrue(synthesis.authorshipDeclaration.contains("original research synthesis"))
                assertEquals("", synthesis.finalAudit.artifactIdentity)
                assertEquals("", synthesis.finalAudit.disposition)
                assertTrue(synthesis.applications.isNotEmpty())
            }
        }
        assertFalse(File(repo(), "audit/final-curriculum-audit").exists())
    }

    @Test
    fun identitySpanAndRelabelMutationsStayClosed() {
        val pack = pack()
        Phase2Sources.ADMISSIONS.forEach { admission ->
            val live = pack.sourceCapabilities.single { it.supportedClaim == admission.claim }
            listOf(
                live.copy(id = "cap-phase2-source", sourceId = "src-openrussian-50e210c4"),
                live.copy(id = "cap-phase2-revision", sourceRevision = "1"),
                live.copy(id = "cap-phase2-record", sourceRecordId = "other-record"),
                live.copy(id = "cap-phase2-hash", sourceHash = "0".repeat(64)),
                live.copy(id = "cap-phase2-span", exactSpan = "altered text that is not the frozen span"),
                live.copy(id = "cap-phase2-unit", unitNumber = if (live.unitNumber == 7) 8 else 7),
                live.copy(id = "cap-phase2-objective", objectiveId = "obj-u1-alphabet"),
                live.copy(id = "cap-phase2-claim", supportedClaim = "register"),
            ).forEach { broken ->
                val replaced = replace(pack, live.id, broken)
                val errors = V2Validator.validate(replaced).errors.joinToString("\n")
                assertTrue(broken.id + "\n" + errors, errors.contains("no independent") || errors.contains("wrong unit or objective") || errors.contains("revision does not match") || errors.contains("span is not in the frozen record") || errors.contains("does not clear"))
                assertTrue(broken.id, unresolved(replaced, admission.unitNumber).contains(admission.claim) || broken.supportedClaim != admission.claim || broken.unitNumber != admission.unitNumber)
            }
            val dictionary = live.copy(
                id = "cap-phase2-dictionary",
                sourceId = "src-openrussian-50e210c4",
                sourceRecordId = "dictionary-row",
                exactSpan = "dictionary row",
            )
            assertTrue(errors(replace(pack, live.id, dictionary)).contains("a dictionary row does not clear ${admission.claim}"))
            val book = live.copy(id = "cap-phase2-book", exactSpan = "Self-attest only after the Lesson work in your copy.")
            val bookErrors = errors(replace(pack, live.id, book))
            assertTrue(bookErrors, bookErrors.contains("book self-attestation does not clear ${admission.claim}"))
            assertTrue(bookErrors, bookErrors.contains("span is not in the frozen record"))
            assertTrue(unresolved(replace(pack, live.id, book), admission.unitNumber).contains(admission.claim))
            val keywordSpan = "${admission.claim} co-occurrence"
            val keyword = live.copy(
                id = "cap-phase2-keyword",
                sourceId = "src-phase2-keyword",
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
            )
            val keywordPack = replace(pack, live.id, keyword).copy(
                provenanceRecords = pack.provenanceRecords + keywordHost,
            )
            val keywordErrors = errors(keywordPack)
            assertTrue(keywordErrors, keywordErrors.contains("keyword or co-occurrence evidence does not clear ${admission.claim}"))
            assertTrue(keywordErrors, keywordErrors.contains("no independent ${admission.claim} admission"))
            val stamped = pack.copy(
                provenanceRecords = pack.provenanceRecords.map { it.copy(reviewStatus = "APPROVED") },
                admissions = pack.admissions.copy(penguinProseAssets = 3, generatedRussianWords = 2),
            )
            assertFalse(unresolved(stamped, admission.unitNumber).contains(admission.claim))
            val removed = pack.copy(sourceCapabilities = pack.sourceCapabilities.filter { it.id != live.id })
            val flipped = removed.copy(
                units = removed.units.map { unit ->
                    if (unit.number == admission.unitNumber) unit.copy(readiness = "CONTENT_COMPLETE_REVIEW_PENDING") else unit
                },
            )
            assertTrue(unresolved(flipped, admission.unitNumber).contains(admission.claim))
            assertTrue(errors(flipped), V2Validator.contentCompletenessErrors(flipped, flipped.unit(admission.unitNumber)!!).isNotEmpty())
            val decision = advancementDecision(flipped, flipped.unit(admission.unitNumber)!!, learner(flipped.unit(admission.unitNumber)!!))
            assertFalse(decision.allowed)
        }

        val invariance = pack.sourceCapabilities.single { it.supportedClaim == "adverb-invariance" }
        val manner = pack.sourceCapabilities.single { it.supportedClaim == "bounded-manner-adverb-formation" }
        val relabeledManner = replace(
            retarget(pack, invariance.exactSpan, manner.objectiveId),
            manner.id,
            invariance.copy(id = manner.id, objectiveId = manner.objectiveId, supportedClaim = manner.supportedClaim),
        )
        val mannerErrors = errors(relabeledManner)
        assertTrue(mannerErrors, mannerErrors.contains("bounded adverb evidence does not clear bounded-manner-adverb-formation"))
        assertTrue(unresolved(relabeledManner, 7).contains("bounded-manner-adverb-formation"))

        val classification = pack.sourceCapabilities.single { it.supportedClaim == "diminutive-form-classification" }
        val nameForm = pack.sourceCapabilities.single { it.supportedClaim == "documented-name-form" }
        val relabeledName = replace(
            retarget(pack, classification.exactSpan, nameForm.objectiveId),
            nameForm.id,
            classification.copy(id = nameForm.id, objectiveId = nameForm.objectiveId, supportedClaim = nameForm.supportedClaim),
        )
        val nameErrors = errors(relabeledName)
        assertTrue(nameErrors, nameErrors.contains("bounded diminutive recognition does not clear documented-name-form"))
        assertTrue(unresolved(relabeledName, 25).contains("documented-name-form"))

        val duration = pack.sourceCapabilities.single { it.supportedClaim == "elapsed-time-duration" }
        val completion = pack.sourceCapabilities.single { it.supportedClaim == "completion-over-period" }
        val relabeledDuration = replace(
            retarget(pack, duration.exactSpan, completion.objectiveId),
            completion.id,
            duration.copy(id = completion.id, objectiveId = completion.objectiveId, supportedClaim = completion.supportedClaim),
        )
        val durationErrors = errors(relabeledDuration)
        assertTrue(durationErrors, durationErrors.contains("duration evidence does not clear completion-over-period"))
        assertTrue(unresolved(relabeledDuration, 23).contains("completion-over-period"))
    }

    @Test
    fun duplicateChecksMissingChainAndLayoutRecallDoNotClearTheClaim() {
        val pack = pack()
        val duplicated = pack.copy(items = pack.items.map { item ->
            if (item.id == "u7-i-manner-2") item.copy(prompt = pack.items.first { it.id == "u7-i-manner-1" }.prompt) else item
        })
        val duplicateErrors = errors(duplicated)
        assertTrue(duplicateErrors, duplicateErrors.contains("duplicate assessment checks do not clear bounded-manner-adverb-formation"))
        assertTrue(unresolved(duplicated, 7).contains("bounded-manner-adverb-formation"))

        val layout = pack.copy(items = pack.items.map { item ->
            if (item.id == "u23-i-completion-1") item.copy(prompt = "Which admitted line label is written beside the shown form?") else item
        })
        val layoutErrors = errors(layout)
        assertTrue(layoutErrors, layoutErrors.contains("label or source-layout recall does not clear completion-over-period"))
        assertTrue(unresolved(layout, 23).contains("completion-over-period"))

        val thinDistractor = pack.copy(items = pack.items.map { item ->
            if (item.id != "u23-i-completion-1") {
                item
            } else {
                item.copy(distractorRationales = item.distractorRationales.map { if (it.isBlank()) it else "The other sentence uses a different verb." })
            }
        })
        val thinErrors = errors(thinDistractor)
        assertTrue(thinErrors, thinErrors.contains("distractor does not explain aspect and construction"))
        assertTrue(unresolved(thinDistractor, 23).contains("completion-over-period"))

        listOf(
            "instructionStepIds" to "missing instruction",
            "practiceStepIds" to "missing guided practice",
            "checkItemIds" to "missing independent check",
            "remediationStepIds" to "missing remediation",
        ).forEach { (field, message) ->
            val stripped = pack.copy(units = pack.units.map { unit ->
                if (unit.number != 25) {
                    unit
                } else {
                    unit.copy(objectives = unit.objectives.map { objective ->
                        if (objective.id != "obj-u25-documented-name-form") {
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
            assertTrue(unresolved(stripped, 25).contains("documented-name-form"))
        }

        val silent = pack.copy(items = pack.items.map { item ->
            if (item.id == "u25-i-name-form-1") item.copy(feedback = "") else item
        })
        val silentErrors = errors(silent)
        assertTrue(silentErrors, silentErrors.contains("missing feedback"))
        assertTrue(unresolved(silent, 25).contains("documented-name-form"))
    }

    @Test
    fun sashkaStaysDismissiveAndNameReadingsCannotBeSwapped() {
        val pack = pack()
        val unit = pack.unit(25)!!
        assertEquals("CONTENT_COMPLETE_REVIEW_PENDING", unit.readiness)
        assertEquals("DEVELOPMENT", unit.installState)
        assertTrue(V2Validator.contentCompletenessErrors(pack, unit).joinToString("\n"), V2Validator.contentCompletenessErrors(pack, unit).isEmpty())
        assertFalse(unresolved(pack, 25).contains("documented-name-form"))
        assertTrue(advancementDecision(pack, unit, learner(unit)).allowed)
        assertTrue(pack.sourceCapabilities.none { it.supportedClaim == "productive-diminutive" })
        assertTrue(pack.unit(25)!!.objectives.none { it.id == "obj-u25-productive-formation" })
        val governed = pack.provenanceRecords.single { it.itemId == "prov-ex-u25-nickname-rule-2-en" }
        assertEquals("\u043e\u0442\u0442\u0435\u043d\u043e\u043a \u043f\u0440\u0435\u043d\u0435\u0431\u0440\u0435\u0436\u0435\u043d\u0438\u044f", governed.shippedText)
        val sashka = pack.items.single { it.id == "u25-i-name-form-2" }
        val mashenka = pack.items.single { it.id == "u25-i-name-form-1" }
        val maryunya = pack.items.single { it.id == "u25-g-name-form-1" }
        val rejected = pack.items.single { it.id == "u25-i-name-form-3" }
        assertEquals("\u0421\u0430\u0448\u043a\u0430", sashka.promptLetter)
        assertTrue(sashka.answer.contains("dismissive"))
        assertFalse(sashka.answer == "documented familiar short-form coloring")
        assertTrue(sashka.feedback.contains(sashka.answer))
        assertTrue(mashenka.answer.contains("short form"))
        assertTrue(maryunya.answer.contains("full name"))
        assertTrue(rejected.answer.contains("names dictionary"))
        assertEquals(setOf(sashka.answer, mashenka.answer, maryunya.answer).size, 3)
        val application = pack.provenanceRecords.first { it.sourceRecordId == "phase2:documented-name-form" }
            .synthesis!!.applications.single { it.cue == "\u0421\u0430\u0448\u043a\u0430" }
        assertEquals(sashka.answer, application.answer)
        assertEquals("documented dismissive coloring", pack.examples.single { it.id == "ex-u25-name-form-1" }.note)

        val familiar = replaceNameAnswer(pack, "u25-i-name-form-2", "documented familiar short-form coloring")
        val familiarErrors = errors(familiar)
        assertTrue(familiarErrors, familiarErrors.contains("familiar short-form coloring does not preserve the dismissive reading"))
        assertTrue(familiarErrors, familiarErrors.contains("conflicting governed descriptions"))
        assertTrue(unresolved(familiar, 25).contains("documented-name-form"))
        assertFalse(advancementDecision(familiar, familiar.unit(25)!!, learner(familiar.unit(25)!!)).allowed)

        val silentFeedback = replaceNameAnswer(pack, "u25-i-name-form-2", sashka.answer, feedback = "This reading is conventional.")
        assertTrue(errors(silentFeedback), errors(silentFeedback).contains("feedback does not agree with the keyed answer"))

        val swapped = replaceNameAnswer(pack, "u25-i-name-form-2", mashenka.answer)
        val swappedErrors = errors(swapped)
        assertTrue(swappedErrors, swappedErrors.contains("swaps the"))
        assertTrue(unresolved(swapped, 25).contains("documented-name-form"))
        val swappedWithMaryunya = replaceNameAnswer(pack, "u25-i-name-form-2", maryunya.answer)
        assertTrue(errors(swappedWithMaryunya), errors(swappedWithMaryunya).contains("swaps the"))

        val generated = replaceNameAnswer(
            pack,
            "u25-i-name-form-3",
            "the suffix mechanically generates an acceptable name",
        )
        val generatedErrors = errors(generated)
        assertTrue(generatedErrors, generatedErrors.contains("arbitrary name-form generation is not rejected"))
        assertTrue(unresolved(generated, 25).contains("documented-name-form"))
    }

    private fun replaceNameAnswer(
        pack: V2CurriculumPack,
        itemId: String,
        answer: String,
        feedback: String? = null,
    ): V2CurriculumPack {
        return pack.copy(items = pack.items.map { item ->
            if (item.id != itemId) {
                item
            } else {
                item.copy(
                    answer = answer,
                    feedback = feedback ?: item.feedback,
                )
            }
        })
    }

    private fun retarget(pack: V2CurriculumPack, span: String, objectiveId: String): V2CurriculumPack =
        pack.copy(provenanceRecords = pack.provenanceRecords.map { record ->
            if (record.originalSourceText == span) record.copy(objectiveId = objectiveId) else record
        })

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
}
