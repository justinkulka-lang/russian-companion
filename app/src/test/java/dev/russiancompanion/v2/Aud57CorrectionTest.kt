package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.ContentAuditCapabilityPins
import dev.russiancompanion.v2.curriculum.Phase3Sources
import dev.russiancompanion.v2.curriculum.V2CurriculumPack
import dev.russiancompanion.v2.curriculum.V2PackLoader
import dev.russiancompanion.v2.curriculum.V2SourceCapability
import dev.russiancompanion.v2.curriculum.V2Validator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.security.MessageDigest

/**
 * AUD-57-01 and AUD-57-02. Dictionary use reads a governed entry.
 * Postal checks are bound to the supplied component model and format.
 * Component recall alone, a retargeted sample, or a neighboring claim does not clear either.
 */
class Aud57CorrectionTest {
    @Test
    fun governedEntryAndCompleteAddressResolve() {
        val pack = pack()
        assertTrue(errors(pack), errors(pack).isEmpty())
        assertFalse(unresolved(pack, 26).contains("postal-address"))
        assertFalse(unresolved(pack, 30).contains("dictionary-use"))
        assertFalse(unresolved(pack, 27).contains("participle-stress"))
        assertFalse(unresolved(pack, 27).contains("participle-comprehension"))
        assertFalse(unresolved(pack, 29).contains("bookish-register"))
        assertFalse(unresolved(pack, 29).contains("bookish-reading"))
        val postal = pack.sourceCapabilities.single { it.supportedClaim == "postal-address" }
        val dictionary = pack.sourceCapabilities.single { it.supportedClaim == "dictionary-use" }
        assertEquals(ContentAuditCapabilityPins.all.single { it.claim == "postal-address" }.hash, postal.sourceHash)
        assertTrue(postal.exactSpan.contains("not a universal international rule"))
        assertTrue(postal.exactSpan.contains("not declare every shorter domestic address invalid"))
        assertTrue(pack.items.single { it.id == "u26-i-postal-5" }.prompt.contains("supplied full model"))
        assertTrue(postal.exactSpan.contains("Headword").not())
        assertTrue(dictionary.exactSpan.contains("Headword стол"))
        assertTrue(dictionary.exactSpan.contains("Part of speech noun"))
        assertTrue(dictionary.exactSpan.contains("Forms стол, стола, столу, столом, столе"))
        assertTrue(dictionary.exactSpan.contains("Sense a piece of furniture with a flat top"))
        assertTrue(dictionary.exactSpan.contains("Example Книга лежит на столе."))
        assertTrue(dictionary.exactSpan.contains("Headword стоять"))
        assertTrue(dictionary.exactSpan.contains("Part of speech verb"))
        assertTrue(dictionary.exactSpan.contains("Sense remaining upright on a base"))
        val complete = pack.items.single { it.id == "u26-i-postal-11" }
        assertEquals("COMPLETE_GOVERNED_ADDRESS", complete.assessment!!.testedFeature)
        assertTrue(complete.answer.contains("locality"))
        assertTrue(complete.answer.contains("region"))
        assertTrue(complete.answer.contains("country"))
        assertEquals(Phase3Sources.STRESS.sourceHash, pack.sourceCapabilities.single { it.supportedClaim == "participle-stress" }.sourceHash)
        assertEquals(ContentAuditCapabilityPins.all.single { it.claim == "participle-comprehension" }.hash,
            pack.sourceCapabilities.single { it.supportedClaim == "participle-comprehension" }.sourceHash)
        assertEquals(Phase3Sources.REGISTER.sourceHash, pack.sourceCapabilities.single { it.supportedClaim == "bookish-register" }.sourceHash)
        assertEquals(Phase3Sources.READING.sourceHash, pack.sourceCapabilities.single { it.supportedClaim == "bookish-reading" }.sourceHash)
    }

    @Test
    fun deletingTheDisplayedEntryLeavesDictionaryUseUnresolved() {
        val pack = pack()
        val capability = pack.sourceCapabilities.single { it.supportedClaim == "dictionary-use" }
        val stripped = capability.exactSpan.lineSequence().filter { line ->
            val trimmed = line.trim()
            !trimmed.startsWith("Headword ") &&
                !trimmed.startsWith("Part of speech ") &&
                !trimmed.startsWith("Forms ") &&
                !trimmed.startsWith("Sense ") &&
                !trimmed.startsWith("Example ")
        }.joinToString("\n")
        val planted = replaceSpan(pack, "dictionary-use", stripped)
        val found = errors(planted)
        assertTrue(found, found.contains("field-label recall does not clear dictionary-use"))
        assertTrue(unresolved(planted, 30).contains("dictionary-use"))
        assertFalse(unresolved(pack, 30).contains("dictionary-use"))
    }

    @Test
    fun isolatedHeadwordTranslationAndLookupPrefixCannotClearDictionaryUse() {
        var planted = pack()
        planted = retargetCue(planted, "u30-i-dictionary-1", "стол", "DICTIONARY_LOOKUP_TABLE_NOUN")
        planted = retargetCue(planted, "u30-i-dictionary-2", "стоять", "DICTIONARY_LOOKUP_STAND_VERB")
        planted = retargetCue(planted, "u30-i-dictionary-3", "стою", "ENTRY_EXAMPLE_TO_SENSE")
        planted = retargetCue(planted, "u30-i-dictionary-4", "сущ.", "ENTRY_FORM_TO_SENSE")
        planted = retargetCue(planted, "u30-i-dictionary-5", "стол, стола, столу, столом, столе", "ENTRY_VERB_FORM_TO_SENSE")
        val found = errors(planted)
        assertTrue(found, found.contains("field-label recall does not clear dictionary-use"))
        assertTrue(unresolved(planted, 30).contains("dictionary-use"))
    }

    @Test
    fun partOfSpeechLabelAloneCannotClearDictionaryUse() {
        val planted = onlyChecks(pack(), "obj-u30-dictionary", listOf("u30-i-dictionary-4"))
        val found = errors(planted)
        assertTrue(found, found.contains("field-label recall does not clear dictionary-use"))
        assertTrue(unresolved(planted, 30).contains("dictionary-use"))
    }

    @Test
    fun formsRowAloneCannotClearDictionaryUse() {
        val planted = onlyChecks(pack(), "obj-u30-dictionary", listOf("u30-i-dictionary-5"))
        val found = errors(planted)
        assertTrue(found, found.contains("field-label recall does not clear dictionary-use"))
        assertTrue(unresolved(planted, 30).contains("dictionary-use"))
    }

    @Test
    fun swappingSenseHeadwordOrApplicationAnswerFailsClosed() {
        val pack = pack()
        val swappedSense = replaceSpan(
            pack,
            "dictionary-use",
            pack.sourceCapabilities.single { it.supportedClaim == "dictionary-use" }.exactSpan
                .replace("Sense a piece of furniture with a flat top", "Sense a flat board used for meals")
                .replace("Sense remaining upright on a base", "Sense moving from one place to another"),
        )
        assertTrue(errors(swappedSense), errors(swappedSense).contains("field-label recall does not clear dictionary-use"))
        assertTrue(unresolved(swappedSense, 30).contains("dictionary-use"))

        val swappedHeadword = replaceSpan(
            pack,
            "dictionary-use",
            pack.sourceCapabilities.single { it.supportedClaim == "dictionary-use" }.exactSpan
                .replace("Headword стол", "Headword доска")
                .replace("Headword стоять", "Headword сидеть"),
        )
        assertTrue(errors(swappedHeadword), errors(swappedHeadword).contains("field-label recall does not clear dictionary-use"))
        assertTrue(unresolved(swappedHeadword, 30).contains("dictionary-use"))

        var swappedAnswer = pack()
        swappedAnswer = pointAnswer(swappedAnswer, "u30-i-dictionary-1", "remaining upright on a base")
        swappedAnswer = pointAnswer(swappedAnswer, "u30-i-dictionary-2", "a piece of furniture with a flat top")
        swappedAnswer = pointAnswer(swappedAnswer, "u30-i-dictionary-3", "the example uses remaining upright on a base")
        val found = errors(swappedAnswer)
        assertTrue(found, found.contains("field-label recall does not clear dictionary-use"))
        assertTrue(unresolved(swappedAnswer, 30).contains("dictionary-use"))
        assertFalse(found.contains("has no independent dictionary-use admission"))
    }

    @Test
    fun capstoneReadingCannotClearDictionaryUse() {
        val pack = pack()
        val dictionary = pack.sourceCapabilities.single { it.supportedClaim == "dictionary-use" }
        val capstone = pack.sourceCapabilities.single { it.supportedClaim == "capstone-reading" }
        val planted = replace(
            pack,
            dictionary.id,
            capstone.copy(id = dictionary.id, objectiveId = dictionary.objectiveId, supportedClaim = dictionary.supportedClaim, unitNumber = 30),
        )
        val found = errors(planted)
        assertTrue(found, found.contains("capstone-reading evidence does not clear dictionary-use"))
        assertTrue(unresolved(planted, 30).contains("dictionary-use"))
        assertFalse(unresolved(pack, 30).contains("capstone-reading"))
    }

    @Test
    fun incompleteAddressCannotMasqueradeAsTheGovernedFormat() {
        val planted = pointAnswer(
            pack(),
            "u26-i-postal-11",
            "recipient, premises, city, and postcode alone are not the complete order",
        )
        val found = errors(planted)
        assertTrue(found, found.contains("corrected intake is missing its exact guided and independent contextual postal applications"))
        assertTrue(unresolved(planted, 26).contains("postal-address"))
    }

    @Test
    fun optionalComponentsAreNotUniversallyMandatory() {
        val planted = pointAnswer(
            pack(),
            "u26-i-postal-9",
            "every home-delivery address must include a post-office box",
        )
        val found = errors(planted)
        assertTrue(found, found.contains("corrected intake is missing its exact guided and independent contextual postal applications"))
        assertTrue(unresolved(planted, 26).contains("postal-address"))
    }

    @Test
    fun letterWritingCorrespondenceGreetingAndSampleLineCannotClearPostalAddress() {
        val pack = pack()
        val postal = pack.sourceCapabilities.single { it.supportedClaim == "postal-address" }
        val letter = pack.sourceCapabilities.single { it.supportedClaim == "letter-writing" }
        val correspondence = pack.sourceCapabilities.single { it.supportedClaim == "correspondence-work" }
        val letterPack = replace(
            pack,
            postal.id,
            letter.copy(id = postal.id, objectiveId = postal.objectiveId, supportedClaim = postal.supportedClaim, unitNumber = 26),
        )
        assertTrue(errors(letterPack), errors(letterPack).contains("letter-writing evidence does not clear postal-address"))
        assertTrue(unresolved(letterPack, 26).contains("postal-address"))
        val correspondencePack = replace(
            pack,
            postal.id,
            correspondence.copy(id = postal.id, objectiveId = postal.objectiveId, supportedClaim = postal.supportedClaim, unitNumber = 26),
        )
        assertTrue(errors(correspondencePack), errors(correspondencePack).contains("correspondence-work evidence does not clear postal-address"))
        assertTrue(unresolved(correspondencePack, 26).contains("postal-address"))

        val greeting = onlyChecks(pack, "obj-u26-postal-address", listOf("u26-i-postal-4"))
        assertTrue(errors(greeting), errors(greeting).contains("corrected intake is missing its exact guided and independent contextual postal applications"))
        assertTrue(unresolved(greeting, 26).contains("postal-address"))

        val sample = retargetCue(pack, "u26-i-postal-11", "Анна Петрова", "COMPLETE_GOVERNED_ADDRESS")
        assertTrue(errors(sample), errors(sample).contains("corrected intake is missing its exact guided and independent contextual postal applications"))
        assertTrue(unresolved(sample, 26).contains("postal-address"))
    }

    @Test
    fun componentRecognitionAndWrongOrderCannotClearPostalAddress() {
        val components = onlyChecks(
            pack(),
            "obj-u26-postal-address",
            listOf("u26-i-postal-1", "u26-i-postal-2", "u26-i-postal-3", "u26-i-postal-4"),
        )
        assertTrue(errors(components), errors(components).contains("corrected intake is missing its exact guided and independent contextual postal applications"))
        assertTrue(unresolved(components, 26).contains("postal-address"))

        val wrongOrder = pointAnswer(
            pack(),
            "u26-i-postal-11",
            "postcode, then country, then recipient, then premises, then locality, then region",
        )
        assertTrue(errors(wrongOrder), errors(wrongOrder).contains("corrected intake is missing its exact guided and independent contextual postal applications"))
        assertTrue(unresolved(wrongOrder, 26).contains("postal-address"))
    }

    private fun pointAnswer(pack: V2CurriculumPack, itemId: String, newAnswer: String): V2CurriculumPack {
        val item = pack.items.first { it.id == itemId }
        val oldAnswer = item.answer
        val objectiveId = item.objectiveId
        val answerRecord = pack.provenanceRecords.first { it.objectiveId == objectiveId && it.shippedText == newAnswer }
        val oldAnswerRecord = pack.provenanceRecords.first { it.objectiveId == objectiveId && it.shippedText == oldAnswer }
        val assessment = item.assessment!!
        val options = item.options.map { option ->
            when (option) {
                oldAnswer -> newAnswer
                newAnswer -> oldAnswer
                else -> option
            }
        }
        val distractors = options.filter { it != newAnswer }
        val distractorIds = distractors.map { text ->
            if (text == oldAnswer) oldAnswerRecord.itemId else pack.provenanceRecords.first { it.objectiveId == objectiveId && it.shippedText == text }.itemId
        }
        val rationales = listOf("") + distractors.map { "Rejected reading: $it" }
        val updated = item.copy(
            answer = newAnswer,
            options = options,
            feedback = "Admitted reading: $newAnswer",
            distractorRationales = rationales,
            assessment = assessment.copy(
                answerSourceIds = listOf(answerRecord.itemId),
                distractorSourceIds = listOf("") + distractorIds,
                distractorDifference = rationales,
                whyAnswerDemonstratesObjective = "Admitted reading: $newAnswer",
                contrastMembers = options,
                relationParticipantIds = listOf(assessment.relationParticipantIds.first(), answerRecord.itemId) + distractorIds,
            ),
        )
        return pack.copy(
            items = pack.items.map { if (it.id == itemId) updated else it },
            provenanceRecords = pack.provenanceRecords.map { record ->
                val synthesis = record.synthesis
                if (record.objectiveId != objectiveId || synthesis == null) {
                    record
                } else {
                    record.copy(
                        synthesis = synthesis.copy(
                            applications = synthesis.applications.map { application ->
                                if (application.cue == item.promptLetter) {
                                    application.copy(answer = newAnswer, distractors = distractors)
                                } else {
                                    application
                                }
                            },
                        ),
                    )
                }
            },
        )
    }

    private fun retargetCue(pack: V2CurriculumPack, itemId: String, cue: String, feature: String): V2CurriculumPack {
        val item = pack.items.first { it.id == itemId }
        val oldCue = item.promptLetter.orEmpty()
        val cueId = item.assessment!!.relationParticipantIds.first()
        return pack.copy(
            items = pack.items.map { current ->
                if (current.id != itemId) {
                    current
                } else {
                    current.copy(
                        promptLetter = cue,
                        assessment = current.assessment!!.copy(testedFeature = feature),
                    )
                }
            },
            provenanceRecords = pack.provenanceRecords.map { record ->
                val synthesis = record.synthesis
                val shipped = if (record.itemId == cueId) cue else record.shippedText
                if (record.objectiveId != item.objectiveId || synthesis == null) {
                    if (record.itemId == cueId) record.copy(shippedText = cue, contentHash = sha256(cue)) else record
                } else {
                    record.copy(
                        shippedText = shipped,
                        contentHash = sha256(shipped),
                        synthesis = synthesis.copy(
                            applications = synthesis.applications.map { application ->
                                if (application.cue == oldCue) application.copy(cue = cue) else application
                            },
                        ),
                    )
                }
            },
        )
    }

    private fun onlyChecks(pack: V2CurriculumPack, objectiveId: String, checkIds: List<String>): V2CurriculumPack {
        return pack.copy(
            units = pack.units.map { unit ->
                unit.copy(
                    objectives = unit.objectives.map { objective ->
                        if (objective.id == objectiveId) objective.copy(checkItemIds = checkIds) else objective
                    },
                    sessions = unit.sessions.map { session ->
                        session.copy(
                            steps = session.steps.map { step ->
                                if (objectiveId in session.objectiveIds && step.kind == "CHECK") step.copy(itemIds = checkIds) else step
                            },
                        )
                    },
                )
            },
        )
    }

    private fun replaceSpan(pack: V2CurriculumPack, claim: String, span: String): V2CurriculumPack {
        val capability = pack.sourceCapabilities.single { it.supportedClaim == claim }
        val hash = sha256(span)
        return pack.copy(
            sourceCapabilities = pack.sourceCapabilities.map { current ->
                if (current.supportedClaim == claim) current.copy(exactSpan = span, sourceHash = hash) else current
            },
            provenanceRecords = pack.provenanceRecords.map { record ->
                if (record.sourceRecordId == capability.sourceRecordId) {
                    record.copy(originalSourceText = span, sourceHash = hash)
                } else {
                    record
                }
            },
        )
    }

    private fun replace(pack: V2CurriculumPack, id: String, capability: V2SourceCapability): V2CurriculumPack =
        pack.copy(sourceCapabilities = pack.sourceCapabilities.filter { it.id != id } + capability)

    private fun errors(pack: V2CurriculumPack): String = V2Validator.validate(pack).errors.joinToString("\n")

    private fun unresolved(pack: V2CurriculumPack, number: Int): Set<String> =
        V2Validator.unresolvedRequiredSourceCapabilities(pack, number).map { it.claim }.toSet()

    private fun pack() = V2PackLoader.parse(File(repo(), "app/src/main/assets/content/v2-curriculum.json").readText())

    private fun repo(): File = listOf(File("."), File("..")).first { File(it, "app/src/main").isDirectory }

    private fun sha256(text: String): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(text.toByteArray(Charsets.UTF_8))
        return digest.joinToString("") { "%02x".format(it) }
    }
}
