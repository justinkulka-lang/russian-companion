package dev.russiancompanion.v2

import dev.russiancompanion.release.ReleaseDecision
import dev.russiancompanion.release.assessRepositoryRelease
import dev.russiancompanion.release.readAcceptanceCandidate
import dev.russiancompanion.v2.curriculum.RemainingSources
import dev.russiancompanion.v2.curriculum.V2CurriculumPack
import dev.russiancompanion.v2.curriculum.V2LearnerView
import dev.russiancompanion.v2.curriculum.V2Objective
import dev.russiancompanion.v2.curriculum.V2PackLoader
import dev.russiancompanion.v2.curriculum.V2SourceCapability
import dev.russiancompanion.v2.curriculum.V2StartingPoint
import dev.russiancompanion.v2.curriculum.V2Validator
import dev.russiancompanion.v2.curriculum.advancementDecision
import dev.russiancompanion.v2.curriculum.distinctCorrectCheckCount
import dev.russiancompanion.v2.curriculum.unit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * AUD-53 corrections: distractor rationales cite real source relationships,
 * ReadyRussian spans stay free of embedded media, and listed-pair, category,
 * field-label, or greeting recognition cannot clear a broader capability.
 */
class Aud53CorrectionTest {
    @Test
    fun livePackKeepsTheCorrectedClaimsAndRejectsEmbeddedMedia() {
        val pack = pack()
        val errors = V2Validator.validate(pack).errors
        assertTrue(errors.joinToString("\n"), errors.isEmpty())
        assertFalse(errors.any { it.contains("false self-pair") || it.contains("swapped source relationship") })
        val clock = pack.sourceCapabilities.single { it.supportedClaim == "productive-clock-time" }
        assertFalse(clock.exactSpan.contains("<img"))
        assertFalse(clock.exactSpan.contains("data:image"))
        assertFalse(clock.exactSpan.contains("base64"))
        assertFalse(clock.exactSpan.contains("<script"))
        assertFalse(clock.exactSpan.contains("<audio"))
        assertEquals(
            setOf("CLOCK_MINUTES_INTO_HOUR", "CLOCK_MINUTES_REMAINING", "CLOCK_DIRECT_READING"),
            pack.items.filter { it.objectiveId == "obj-u17-clock-productive" }
                .map { it.assessment?.testedFeature }
                .toSet(),
        )
        assertEquals(
            setOf("LETTER_ORGANIZATION", "LETTER_ADDRESS_LIMIT", "LETTER_STRUCTURE"),
            pack.items.filter { it.objectiveId == "obj-u26-letter-writing" }
                .map { it.assessment?.testedFeature }
                .toSet(),
        )
        assertTrue(pack.items.filter { it.objectiveId == "obj-u17-clock-productive" || it.objectiveId == "obj-u26-letter-writing" }
            .all { it.assessment?.relationKind == "STATED_APPLICATION" })
        assertFalse(unresolved(pack, 17).contains("productive-clock-time"))
        assertFalse(unresolved(pack, 24).contains("negation-scope"))
        assertFalse(unresolved(pack, 26).contains("letter-writing"))
        assertTrue(unresolved(pack, 25).contains("productive-diminutive"))
        assertTrue(unresolved(pack, 26).contains("postal-address"))
        assertTrue(unresolved(pack, 27).contains("participle-stress"))
        assertTrue(unresolved(pack, 27).contains("participle-comprehension"))
        assertTrue(unresolved(pack, 30).contains("dictionary-use"))
        assertEquals("NOT_PREPARED", pack.unit(25)!!.readiness)
        assertEquals("NOT_PREPARED", pack.unit(27)!!.readiness)
        assertEquals("NOT_PREPARED", pack.unit(30)!!.readiness)
        assertEquals("CONTENT_COMPLETE_REVIEW_PENDING", pack.unit(17)!!.readiness)
        assertTrue(pack.sourceCapabilities.none { it.supportedClaim == "productive-diminutive" })
        assertTrue(pack.sourceCapabilities.none { it.supportedClaim == "participle-stress" })
        assertTrue(pack.sourceCapabilities.none { it.supportedClaim == "dictionary-use" })
    }

    @Test
    fun falseSelfPairUnsupportedSwapAndCoOccurrenceFail() {
        val pack = pack()
        val item = pack.items.first { it.assessment?.testedFeature == "CLOCK_MINUTES_INTO_HOUR" }
        val gloss = "at [one] hour fifteen minutes"
        val selfPair = withRationale(
            pack,
            item.id,
            """The frozen source pairs the shown form "$gloss" with the admitted gloss "$gloss".""",
        )
        assertTrue(errors(selfPair).any { it.contains(item.id) && it.contains("false self-pair") })

        val unsupported = withRationale(
            pack,
            item.id,
            """The frozen source pairs the shown form "${item.answer}" with the admitted gloss "banana".""",
        )
        assertTrue(errors(unsupported).any { it.contains(item.id) && it.contains("unsupported by the cited frozen record") })

        val swapped = withRationale(
            pack,
            item.id,
            """The frozen source pairs the shown form "${item.answer}" with the admitted gloss "$gloss".""",
        )
        assertTrue(errors(swapped).any { it.contains(item.id) && it.contains("swapped source relationship") })

        val together = withRationale(pack, item.id, "These strings appear together on the page.")
        assertTrue(errors(together).any { it.contains(item.id) && it.contains("co-occurrence without the stated relationship") })
    }

    @Test
    fun embeddedMediaCannotEnterGovernedReadyRussianSpans() {
        val pack = pack()
        val record = pack.provenanceRecords.first { it.sourceId.startsWith("src-readyrussian-") }
        listOf("<img", "data:image", "base64", "<script", "<audio", ".ogg", "data:audio").forEach { token ->
            val poisoned = pack.copy(
                provenanceRecords = pack.provenanceRecords.map { current ->
                    if (current.itemId == record.itemId) {
                        current.copy(originalSourceText = current.originalSourceText + " " + token)
                    } else {
                        current
                    }
                },
            )
            val found = errors(poisoned)
            assertTrue(found.joinToString("\n"), found.any {
                it.contains("governed ReadyRussian span contains embedded media") && it.contains(record.itemId)
            })
        }
    }

    @Test
    fun semanticDuplicatesDoNotSatisfyDistinctCheckMinimums() {
        val pack = pack()
        val unit = pack.unit(21)!!
        val step = unit.sessions.flatMap { it.steps }.first { it.itemIds.isNotEmpty() }
        val original = pack.items.first { it.id == step.itemIds.first() }
        val clone = original.copy(id = "aud53-duplicate-check", feedback = original.feedback + " Different feedback.")
        val mutated = pack.copy(
            items = pack.items + clone,
            units = pack.units.map { current ->
                if (current.number != 21) {
                    current
                } else {
                    current.copy(
                        sessions = current.sessions.map { session ->
                            session.copy(steps = session.steps.map { currentStep ->
                                if (currentStep.id == step.id) {
                                    currentStep.copy(itemIds = currentStep.itemIds + clone.id)
                                } else {
                                    currentStep
                                }
                            })
                        },
                        advancement = current.advancement!!.copy(
                            minimumDistinctChecks = distinctCorrectCheckCount(
                                pack,
                                current.sessions.flatMap { it.steps }.flatMap { it.itemIds },
                                current.sessions.flatMap { it.steps }.flatMap { it.itemIds }.toSet(),
                            ) + 1,
                        ),
                    )
                }
            },
        )
        assertEquals(
            1,
            distinctCorrectCheckCount(mutated, listOf(original.id, clone.id), setOf(original.id, clone.id)),
        )
        val ids = mutated.unit(21)!!.sessions.flatMap { it.steps }.flatMap { it.itemIds }
        val withoutClone = ids.filter { it != clone.id }
        assertEquals(
            distinctCorrectCheckCount(mutated, withoutClone, withoutClone.toSet()),
            distinctCorrectCheckCount(mutated, ids, ids.toSet()),
        )
        val decision = advancementDecision(mutated, mutated.unit(21)!!, learner(mutated, 21))
        assertFalse(decision.allowed)
        assertTrue(decision.remaining.joinToString("\n"), decision.remaining.any { it.contains("distinct in-app checks") })
    }

    @Test
    fun exactPairMemorizationCannotClearAProductiveCapability() {
        val pack = pack()
        val recalled = retarget(
            pack,
            pack.items.filter { it.objectiveId == "obj-u17-clock-productive" }.map { it.id }.toSet(),
        ) { item ->
            item.copy(assessment = item.assessment!!.copy(testedFeature = "CLOCK_GLOSS_RECALL"))
        }
        val clockErrors = errors(recalled)
        assertTrue(clockErrors.joinToString("\n"), clockErrors.any { it.contains("exact-pair memorization does not clear productive-clock-time") })
        assertTrue(unresolved(recalled, 17).contains("productive-clock-time"))

        val classification = pack.sourceCapabilities.single { it.supportedClaim == "diminutive-form-classification" }
        val template = pack.items.first { it.objectiveId == classification.objectiveId && it.assessment != null }
        val listed = template.copy(
            id = "aud53-listed-pair",
            objectiveId = "obj-u25-productive-formation",
            assessment = template.assessment!!.copy(
                testedFeature = "LISTED_NAME_PAIR",
                relationKind = "EXPLICIT_SOURCE_PAIR",
                evidenceMode = "EXPLICIT_SOURCE_PAIR",
            ),
        )
        val host = pack.provenanceRecords.first {
            it.originalSourceText == classification.exactSpan && it.objectiveId == classification.objectiveId
        }
        val planted = pack.copy(
            items = pack.items + listed,
            provenanceRecords = pack.provenanceRecords + host.copy(
                itemId = "prov-aud53-diminutive",
                objectiveId = "obj-u25-productive-formation",
            ),
            sourceCapabilities = pack.sourceCapabilities + classification.copy(
                id = "cap-aud53-listed-pair",
                objectiveId = "obj-u25-productive-formation",
                supportedClaim = "productive-diminutive",
            ),
            units = pack.units.map { current ->
                if (current.number != 25) {
                    current
                } else {
                    current.copy(objectives = current.objectives + V2Objective(
                        id = "obj-u25-productive-formation",
                        unitNumber = 25,
                        title = "Listed pair",
                        kind = "RECOGNITION",
                        checkItemIds = listOf(listed.id),
                    ))
                }
            },
        )
        val diminutiveErrors = errors(planted)
        assertTrue(diminutiveErrors.joinToString("\n"), diminutiveErrors.any {
            it.contains("exact-pair memorization does not clear productive-diminutive")
        })
        assertTrue(unresolved(planted, 25).contains("productive-diminutive"))
    }

    @Test
    fun headwordSelectionCannotClearParticipleStress() {
        val pack = pack()
        val reference = pack.provenanceRecords.first {
            it.objectiveId == "obj-u27-stress-reference" && it.shippedText.any { char -> char == '\u0301' }
        }
        val infinitive = "\u043e\u0441\u0442\u0430\u0432\u043b\u044f\u0442\u044c"
        val moved = reference.copy(itemId = "prov-aud53-stress", objectiveId = "obj-u27-stress")
        val planted = pack.copy(
            provenanceRecords = pack.provenanceRecords + moved,
            sourceCapabilities = pack.sourceCapabilities + V2SourceCapability(
                id = "cap-aud53-stress",
                sourceId = moved.sourceId,
                sourceRevision = moved.sourceRevision,
                sourceRecordId = moved.sourceRecordId,
                sourceHash = moved.sourceHash,
                exactSpan = moved.originalSourceText,
                unitNumber = 27,
                objectiveId = "obj-u27-stress",
                supportedClaim = "participle-stress",
            ),
        )
        val found = errors(planted)
        assertTrue(found.joinToString("\n"), found.any { it.contains("headword-versus-infinitive selection does not clear participle-stress") })
        assertTrue(unresolved(planted, 27).contains("participle-stress"))
        assertTrue(reference.shippedText != infinitive)
    }

    @Test
    fun fieldLabelRecallCannotClearDictionaryUse() {
        val pack = pack()
        val span = RemainingSources.DICTIONARY_SPAN
        val template = pack.provenanceRecords.first()
        val record = template.copy(
            itemId = "prov-aud53-dictionary",
            unitNumber = 30,
            objectiveId = "obj-u30-dictionary",
            sourceId = RemainingSources.GUIDE_SOURCE,
            sourceRevision = RemainingSources.GUIDE_REVISION,
            sourceRecordId = RemainingSources.recordId(RemainingSources.GUIDE_PAGE, span),
            originalSourceText = span,
            shippedText = "Headword",
            sourceHash = RemainingSources.sha256(span),
            contentHash = RemainingSources.sha256("Headword"),
        )
        val planted = pack.copy(
            provenanceRecords = pack.provenanceRecords + record,
            sourceCapabilities = pack.sourceCapabilities + V2SourceCapability(
                id = "cap-aud53-dictionary",
                sourceId = record.sourceId,
                sourceRevision = record.sourceRevision,
                sourceRecordId = record.sourceRecordId,
                sourceHash = record.sourceHash,
                exactSpan = span,
                unitNumber = 30,
                objectiveId = "obj-u30-dictionary",
                supportedClaim = "dictionary-use",
            ),
        )
        val found = errors(planted)
        assertTrue(found.joinToString("\n"), found.any { it.contains("field-label recall does not clear dictionary-use") })
        assertTrue(unresolved(planted, 30).contains("dictionary-use"))
    }

    @Test
    fun greetingRecognitionCannotClearLetterWriting() {
        val pack = pack()
        val recalled = retarget(
            pack,
            pack.items.filter { it.objectiveId == "obj-u26-letter-writing" }.map { it.id }.toSet(),
        ) { item ->
            item.copy(assessment = item.assessment!!.copy(testedFeature = "GREETING_RECOGNITION"))
        }
        val found = errors(recalled)
        assertTrue(found.joinToString("\n"), found.any { it.contains("greeting recognition does not clear letter-writing") })
        assertTrue(unresolved(recalled, 26).contains("letter-writing"))
    }

    @Test
    fun wrongIdentityAndWorkflowFieldsFailClosed() {
        val pack = pack()
        val clock = pack.sourceCapabilities.single { it.supportedClaim == "productive-clock-time" }
        listOf(
            clock.copy(id = "cap-aud53-source", sourceId = "src-openrussian-50e210c4"),
            clock.copy(id = "cap-aud53-revision", sourceRevision = "1"),
            clock.copy(id = "cap-aud53-record", sourceRecordId = "other-record"),
            clock.copy(id = "cap-aud53-hash", sourceHash = "0".repeat(64)),
            clock.copy(id = "cap-aud53-span", exactSpan = "not the frozen span"),
            clock.copy(id = "cap-aud53-unit", unitNumber = 16, objectiveId = "obj-u16-complements"),
            clock.copy(id = "cap-aud53-objective", objectiveId = "obj-u17-age"),
            clock.copy(id = "cap-aud53-claim", supportedClaim = "age"),
        ).forEach { broken ->
            val replaced = pack.copy(
                sourceCapabilities = pack.sourceCapabilities.filter { it.id != clock.id } + broken,
            )
            val found = errors(replaced)
            assertTrue(broken.id, found.any {
                it.contains("revision does not match the frozen record") ||
                    it.contains("span is not in the frozen record") ||
                    it.contains("wrong unit or objective") ||
                    it.contains("does not clear")
            })
            assertTrue(broken.id, unresolved(replaced, 17).contains("productive-clock-time"))
        }

        val stamped = pack.copy(
            units = pack.units.map { current ->
                if (current.number == 25) current.copy(readiness = "CONTENT_COMPLETE_REVIEW_PENDING") else current
            },
            unitReviews = pack.unitReviews.map { it.copy(status = "APPROVED") },
            provenanceRecords = pack.provenanceRecords.map { it.copy(reviewStatus = "APPROVED") },
            admissions = pack.admissions.copy(penguinProseAssets = 9, generatedRussianWords = 4),
        )
        assertTrue(unresolved(stamped, 25).contains("productive-diminutive"))
        assertTrue(unresolved(stamped, 27).contains("participle-stress"))
        assertTrue(unresolved(stamped, 30).contains("dictionary-use"))
        assertEquals("DEVELOPMENT", stamped.unit(25)!!.installState)
        val decision = advancementDecision(stamped, stamped.unit(25)!!, learner(stamped, 25))
        assertFalse(decision.allowed)
        assertTrue(decision.remaining.any { it.contains("productive-diminutive") })

        val keyword = pack.copy(
            sourceCapabilities = pack.sourceCapabilities + clock.copy(
                id = "cap-aud53-cooccurrence",
                exactSpan = "productive diminutive co-occurrence",
                unitNumber = 25,
                objectiveId = "obj-u25-nickname-rule",
                supportedClaim = "productive-diminutive",
                sourceHash = "1".repeat(64),
            ),
        )
        assertTrue(unresolved(keyword, 25).contains("productive-diminutive"))
    }

    @Test
    fun governanceStaysUnapproved() {
        val root = repo()
        val pack = pack()
        val candidate = readAcceptanceCandidate(root)
        val assessment = assessRepositoryRelease(root)
        assertTrue(pack.units.all { it.installState == "DEVELOPMENT" })
        assertTrue(pack.unitReviews.all { it.status == "PENDING_QUALIFIED_REVIEW" && it.reviewer.isNullOrBlank() })
        assertTrue(pack.units.all { it.mappingStatus == "PENDING_CURRICULUM_REVIEWER" })
        assertEquals(false, candidate?.frozen)
        assertEquals(null, candidate?.sourceCommit)
        assertEquals("NOT_RECORDED", candidate?.artifact?.status)
        assertEquals("NOT_APPROVED", candidate?.publicRelease)
        assertEquals(ReleaseDecision.NOT_APPROVED, assessment.decision)
        val proposal = File(root, "docs/governance/UNITS_26_30_AUTHORITY_VARIANCE_PROPOSAL.md").readText()
        assertTrue(proposal.contains("Status: PROPOSED_NOT_ACTIVATED"))
        assertFalse(proposal.contains("Status: ACTIVATED"))
        val acceptance = File(root, "release/acceptance/DEVICE_ACCEPTANCE.md").readText()
        assertTrue(acceptance.contains("UNEXECUTED") || acceptance.contains("AT-031"))
    }

    private fun unresolved(pack: V2CurriculumPack, number: Int): Set<String> =
        V2Validator.unresolvedRequiredSourceCapabilities(pack, number).map { it.claim }.toSet()

    private fun errors(pack: V2CurriculumPack): List<String> = V2Validator.validate(pack).errors

    private fun withRationale(pack: V2CurriculumPack, itemId: String, rationale: String): V2CurriculumPack {
        return retarget(pack, setOf(itemId)) { item ->
            item.copy(distractorRationales = listOf(rationale), feedback = rationale)
        }
    }

    private fun retarget(
        pack: V2CurriculumPack,
        ids: Set<String>,
        edit: (dev.russiancompanion.v2.curriculum.V2CheckItem) -> dev.russiancompanion.v2.curriculum.V2CheckItem,
    ): V2CurriculumPack {
        return pack.copy(items = pack.items.map { item -> if (item.id in ids) edit(item) else item })
    }

    private fun learner(pack: V2CurriculumPack, number: Int): V2LearnerView {
        val unit = pack.unit(number)!!
        val steps = unit.sessions.flatMap { it.steps }
        return V2LearnerView(
            onboardingCompleted = true,
            currentUnitNumber = number,
            startingPoint = V2StartingPoint.NEW_TO_CYRILLIC,
            hasPenguin = true,
            hasAnki = false,
            hasBinder = false,
            hasTutor = false,
            dailyMinutes = 25,
            transliterationEnabled = true,
            currentSessionId = null,
            currentStepIndex = 0,
            completedStepIds = steps.map { it.id }.toSet(),
            correctItemIds = (unit.objectives.flatMap { it.checkItemIds } + steps.flatMap { it.itemIds }).toSet(),
            bookAttestedUnits = setOf(number),
            completedUnitNumbers = emptySet(),
        )
    }

    private fun pack() = V2PackLoader.parse(File(repo(), "app/src/main/assets/content/v2-curriculum.json").readText())

    private fun repo(): File = listOf(File("."), File("..")).first { File(it, "app/src/main").isDirectory }
}
