package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.ContentAuditSources
import dev.russiancompanion.v2.curriculum.Phase2Sources
import dev.russiancompanion.v2.curriculum.PhraseAllowlist
import dev.russiancompanion.v2.curriculum.V2PackLoader
import dev.russiancompanion.v2.curriculum.V2Validator
import dev.russiancompanion.v2.curriculum.V2LearnerFields
import org.junit.Assert.*
import org.junit.Test
import java.io.File

/**
 * D7b (approved rule changes; owner decisions D7, D9, D10, D11a, D15c, D15d, D18, D19): the phrase audit is blocking
 * for every unit, and its allowlist only shrinks. The allowlist is audit/d7b-phrase-allowlist.txt; V2Validator reads
 * the same bytes from its packaged resource.
 */
class PhraseAuditTest {
    private val root = PhraseAudit.root
    private val allowlistFile = File(root, "audit/d7b-phrase-allowlist.txt")
    private val pack by lazy { V2PackLoader.parse(File(root, "app/src/main/assets/content/v2-curriculum.json").readText()) }
    private val allowlist by lazy { PhraseAllowlist.parse(allowlistFile.readText()) }

    private fun key(e: PhraseAllowlist.Entry) = PhraseAudit.Failure(e.unit, e.ownerField, e.text, e.type)

    @Test fun packagedAllowlistIsByteIdenticalToTheAuditFile() {
        val packaged = PhraseAllowlist.resourceBytes()
        assertNotNull("the validator resource ${PhraseAllowlist.RESOURCE} is not packaged", packaged)
        assertArrayEquals(allowlistFile.readBytes(), packaged)
        assertEquals(allowlist, PhraseAllowlist.entries)
        // D11a + D15d + D19b: 183 baseline lines, 44 COMPOSED additions and the one сущ line; no duplicates.
        // S1: 78 lines removed (was 228).
        assertEquals(150, allowlist.size)
        assertEquals(allowlist.size, allowlist.toSet().size)
        // S1 removed 54 UNATTESTED (was 161), 4 MISSING_STRESS (was 23) and 20 COMPOSED (was 44).
        assertEquals(mapOf("UNATTESTED" to 107, "MISSING_STRESS" to 19, "COMPOSED" to 24), allowlist.groupingBy { it.type }.eachCount())
    }

    @Test fun everyUnitPassesThePhraseAuditExceptExactlyTheAllowlist() {
        val fields = PhraseAudit.fields(pack)
        val failures = PhraseAudit.failures(fields)
        val listed = allowlist.map(::key).toSet()
        // (a) Every failure is on the allowlist.
        val unlisted = failures - listed
        assertTrue("Russian with no frozen witness, not on the allowlist:\n" + unlisted.joinToString("\n"), unlisted.isEmpty())
        // (b) Every allowlisted entry the audit re-checks still fails; one that passes must be removed (the list only
        // shrinks). A single unstressed word is outside the phrase rule (only runs of two or more words, stressed forms
        // and clause sequences are re-checked), so the D19b entry for сущ, whose witness the owner ruled out by meaning,
        // stays until its text leaves the pack (rule c).
        val recheckable = allowlist.filterNot { it.type == "UNATTESTED" && ' ' !in it.text && '́' !in it.text }
        val nowPassing = recheckable.map(::key).toSet() - failures
        assertTrue("Allowlisted entries now pass; remove them:\n" + nowPassing.joinToString("\n"), nowPassing.isEmpty())
        assertEquals(listOf("сущ"), (allowlist - recheckable.toSet()).map { it.text })
        // (c) Every allowlisted owner.field exists and still shows its text.
        val byOwnerField = fields.groupBy { it.unit to it.ownerField }
        val stale = allowlist.filter { e ->
            byOwnerField[e.unit to e.ownerField].orEmpty().none { field ->
                Regex("(?<![\\u0400-\\u04FF\\u0301])${Regex.escape(e.text)}(?![\\u0400-\\u04FF\\u0301])").containsMatchIn(field.text)
            }
        }
        assertTrue("Allowlisted owner.field or text no longer exists:\n" + stale.joinToString("\n"), stale.isEmpty())
        // S1: 78 entries now pass against the frozen S1 pages and were removed (was 227).
        assertEquals(149, failures.size)
        assertTrue(fields.map { it.unit }.toSet().containsAll((1..30).toSet()))
    }

    @Test fun eachAuditRuleCatchesWhatItShould() {
        fun fail(text: String) = PhraseAudit.failures(listOf(PhraseAudit.Field(16, "probe.body", text))).map { it.type to it.text }.toSet()
        // UNATTESTED: a run not present in any frozen line; an attested run passes. S1: "Он поранил себя" is attested by
        // the frozen Reflexive pronoun page (oldid 1374712982), so the unattested probe is now "Кошка читает газету".
        assertTrue(("UNATTESTED" to "Кошка читает газету") in fail("Кошка читает газету."))
        assertTrue(fail("Он поранил себя.").isEmpty())
        assertTrue(fail("Ваза стоит на столе.").isEmpty())
        // ё and е stay distinct; case is ignored.
        assertTrue(fail("Без неё").isEmpty())
        assertTrue(("UNATTESTED" to "Без нее") in fail("Без нее"))
        assertTrue(fail("ВАЗА СТОИТ НА СТОЛЕ").isEmpty())
        // MISSING_STRESS: a stress shown that no frozen form has.
        assertTrue(("MISSING_STRESS" to "написа́нный") in fail("написа́нный"))
        assertTrue(fail("напи́санный").isEmpty())
        // COMPOSED (D15c): separately attested spans joined into one sequence fail; a contiguous excerpt passes.
        assertTrue(PhraseAudit.present("читая письмо") && PhraseAudit.present("он улыбался"))
        assertTrue(("COMPOSED" to "читая письмо, он улыбался") in fail("читая письмо, он улыбался"))
        assertTrue(fail("Пообедав, я пошёл гулять").isEmpty())
        // Form enumerations are not clause sequences; a quoted example's subject word in English is not part of one.
        assertTrue(fail("стол, стола, столу").none { it.first == "COMPOSED" })
        // T15h (D25b): the English-boundary rule is deleted, so a repeated word after the comma no longer leaves the
        // sequence (these two were expected to pass under D21b); the reworded Unit 6 form keeps English between them.
        assertTrue(("COMPOSED" to "мой стол, стол") in fail("In мой стол, стол is masculine singular."))
        assertTrue(("COMPOSED" to "моя собака, собака") in fail("In моя собака, собака is feminine singular."))
        assertTrue(fail("In мой стол, the noun стол is masculine singular.").none { it.first == "COMPOSED" })
        assertTrue(("COMPOSED" to "он улыбался, улыбался") in fail("он улыбался, улыбался is the complete Russian example."))
        assertTrue(("COMPOSED" to "он улыбался, читая") in fail("он улыбался, читая is the complete Russian example."))
        assertTrue(("COMPOSED" to "он улыбался, читая") in fail("он улыбался, читая"))
        // T15h (D25a): a comma, semicolon or colon joins with or without following spaces.
        assertTrue(("COMPOSED" to "читая письмо;он улыбался") in fail("читая письмо;он улыбался"))
        assertTrue(("COMPOSED" to "читая письмо,он улыбался") in fail("читая письмо,он улыбался"))
        assertTrue(("COMPOSED" to "читая письмо:он улыбался") in fail("читая письмо:он улыбался"))
        // T15g (D21c): dashes, semicolons and colons between words join a sequence too.
        assertTrue(("COMPOSED" to "читая письмо — он улыбался") in fail("читая письмо — он улыбался"))
        assertTrue(("COMPOSED" to "читая письмо – он улыбался") in fail("читая письмо – он улыбался"))
        assertTrue(("COMPOSED" to "читая письмо - он улыбался") in fail("читая письмо - он улыбался"))
        assertTrue(("COMPOSED" to "читая письмо; он улыбался") in fail("читая письмо; он улыбался"))
        assertTrue(("COMPOSED" to "читая письмо: он улыбался") in fail("читая письмо: он улыбался"))
        // T15g (D22c): a list of letters, upper/lower pairs or alphabet-page letter names is a list, whatever the separator.
        assertTrue(fail("Й is named и краткое; К is named ка; Ъ is named твёрдый знак; Ы").none { it.first == "COMPOSED" })
        assertTrue(fail("Е, Ё; Ю — Я").isEmpty())
        assertTrue(fail("М м; Т т").isEmpty())
        // T15h (D25f): only exact letter and letter-name cells count; a word inside a name cell is not a letter name.
        assertTrue(("COMPOSED" to "твёрдый знак; знак") in fail("твёрдый знак; знак"))
        assertTrue(fail("твёрдый знак; мягкий знак").none { it.first == "COMPOSED" })
        // A name list item next to ordinary words is still a clause join (D21c).
        assertTrue(("COMPOSED" to "и краткое; читая письмо") in fail("и краткое; читая письмо"))
        // A dash pair contiguous on one frozen line still passes (narechie-152437423.txt:44 and :8).
        assertTrue(fail("быстрый — быстро").isEmpty())
        assertTrue(fail("Наречия — неизменяемые слова").isEmpty())
        // D9: single-letter lists attested on the alphabet page are exempt; a word list is not.
        assertTrue(fail("Е Ё Ю Я").isEmpty())
        assertTrue(fail("М м, Т т, К к").isEmpty())
        // The three extraction fixes: piped link, inline HTML join, bold-vowel stress.
        assertTrue(fail("Затме́ние должно́ наступи́ть в три часа́").isEmpty())
        assertTrue(fail("Уважаемый Иван Иванович").isEmpty())
        assertTrue(PhraseAudit.formatWikibooksEmphasis("Они '''у'''летят").contains("улетят"))
    }

    @Test fun validatorAllowlistMatchesByUnitAndWholeWords() {
        // D18b: equal to an allowlisted text of the same unit, or a whole-word substring of one.
        assertTrue(PhraseAllowlist.exempts(28, "написав письмо"))
        assertTrue(PhraseAllowlist.exempts(28, "написав"))
        assertTrue(PhraseAllowlist.exempts(29, "Закончив"))
        assertFalse(PhraseAllowlist.exempts(28, "писав"))
        assertFalse(PhraseAllowlist.exempts(27, "написав"))
        assertFalse(PhraseAllowlist.exempts(28, "написав", emptyList()))
    }

    /** T15g (owner decision D20): the validator's Russian-coverage scan reads exactly the audit's learner-facing fields. */
    @Test fun validatorScanAndPhraseAuditUseTheSameLearnerFields() {
        val audit = PhraseAudit.fields(pack).map { Triple(it.unit, it.ownerField, it.text) }
        val validator = V2LearnerFields.fields(pack).map { Triple(it.unit, it.ownerField, it.text) }
        assertEquals(audit.toSet(), validator.toSet())
        assertEquals(audit.size, validator.size)
        // The four field kinds D20 adds are in the set.
        val names = validator.map { it.second }
        assertTrue(names.any { it.endsWith(".shortLabel") })
        assertTrue(names.any { it.endsWith(".dominantAction") })
        val placed = pack.units.flatMap { u -> u.objectives.flatMap { it.checkItemIds } + u.sessions.flatMap { s -> s.steps.flatMap { it.itemIds } } }.toSet()
        val objectiveOnlyItems = pack.items.map { it.id }.filterNot { it in placed }
        assertTrue(objectiveOnlyItems.isNotEmpty() && objectiveOnlyItems.all { id -> names.any { it.startsWith("$id.") } })
        val linkedExamples = pack.units.flatMap { u -> u.objectives.flatMap { it.exampleIds } + u.sessions.flatMap { s -> s.steps.flatMap { it.exampleIds } } }.toSet()
        val objectiveOnlyExamples = pack.examples.map { it.id }.filterNot { it in linkedExamples }
        assertTrue(objectiveOnlyExamples.isNotEmpty() && objectiveOnlyExamples.all { id -> "$id.display" in names })
    }

    /** T15g (D20) regression: сущ (allowlisted for Unit 30 only) placed in each newly scanned field kind of Unit 29 is rejected. */
    @Test fun aWordWithoutWitnessInAnyNewlyScannedFieldIsRejected() {
        val word = "сущ"
        val error = "Russian text has no admitted source record: $word"
        fun rejected(changed: dev.russiancompanion.v2.curriculum.V2CurriculumPack) = V2Validator.validate(changed).errors.contains(error)
        assertFalse(rejected(pack))
        val u29 = pack.units.single { it.number == 29 }
        // 1. unit shortLabel
        assertTrue(rejected(pack.copy(units = pack.units.map { if (it.number == 29) it.copy(shortLabel = it.shortLabel + " ($word)") else it })))
        // 2. step dominantAction
        val step = u29.sessions.flatMap { it.steps }.first { it.dominantAction != null }
        assertTrue(rejected(pack.copy(units = pack.units.map { u -> if (u.number != 29) u else u.copy(sessions = u.sessions.map { s ->
            s.copy(steps = s.steps.map { if (it.id == step.id) it.copy(dominantAction = it.dominantAction + " ($word)") else it }) }) })))
        // 3. an item reached only through its objective
        val placed = pack.units.flatMap { u -> u.objectives.flatMap { it.checkItemIds } + u.sessions.flatMap { s -> s.steps.flatMap { it.itemIds } } }.toSet()
        val objective29 = u29.objectives.map { it.id }.toSet()
        val item = pack.items.first { it.objectiveId in objective29 && it.id !in placed }
        assertTrue(rejected(pack.copy(items = pack.items.map { if (it.id == item.id) it.copy(feedback = it.feedback + " ($word)") else it })))
        // 4. an example linked only by its objectiveId
        val linked = pack.units.flatMap { u -> u.objectives.flatMap { it.exampleIds } + u.sessions.flatMap { s -> s.steps.flatMap { it.exampleIds } } }.toSet()
        val example = pack.examples.first { it.id !in linked }
        val exampleUnit = pack.units.single { u -> u.objectives.any { it.id == example.objectiveId } }.number
        assertTrue(exampleUnit != 30)
        assertTrue(rejected(pack.copy(examples = pack.examples.map { if (it.id == example.id) it.copy(note = it.note + " ($word)") else it })))
        // Control: in Unit 30, where сущ is allowlisted, the same insertion passes the coverage rule.
        assertFalse(rejected(pack.copy(units = pack.units.map { if (it.number == 30) it.copy(shortLabel = it.shortLabel + " ($word)") else it })))
    }

    /**
     * Regression: an invented Russian sentence, backed only by a synthesis record that ships it exactly (which the old
     * line-5263 membership accepted), is rejected by both the validator and the audit.
     */
    @Test fun anInventedSentenceBackedOnlyBySynthesisIsRejected() {
        val invented = "Кошка читает газету под столом"
        assertFalse(PhraseAudit.present(invented))
        listOf(16 to ContentAuditSources.SOURCE_ID, 27 to Phase2Sources.SOURCE_ID).forEach { (unitNumber, sourceId) ->
            val synthesis = pack.provenanceRecords.first { it.unitNumber == unitNumber && it.sourceId == sourceId }
            assertFalse(V2Validator.isFrozenSourceRecord(pack, synthesis))
            val backing = synthesis.copy(itemId = "prov-d7b-regression-$unitNumber", shippedText = invented)
            val step = pack.units.single { it.number == unitNumber }.sessions.flatMap { it.steps }.first { it.kind == "TEACH" }
            val changed = pack.copy(
                provenanceRecords = pack.provenanceRecords + backing,
                units = pack.units.map { u ->
                    if (u.number != unitNumber) u else u.copy(sessions = u.sessions.map { s ->
                        s.copy(steps = s.steps.map { if (it.id == step.id) it.copy(body = it.body + " $invented.") else it })
                    })
                },
            )
            // The synthesis record ships the sentence exactly: the old rule's membership test would have admitted it.
            assertTrue(changed.provenanceRecords.any { it.unitNumber == unitNumber && it.shippedText == invented })
            val errors = V2Validator.validate(changed).errors
            assertTrue("unit $unitNumber: $errors", errors.contains("Russian text has no admitted source record: $invented."))
            val audit = PhraseAudit.failures(PhraseAudit.fields(changed))
            assertTrue(PhraseAudit.Failure(unitNumber, "${step.id}.body", invented, "UNATTESTED") in audit)
        }
        // The unchanged pack passes the full validator.
        assertTrue(V2Validator.validate(pack).errors.joinToString("\n"), V2Validator.validate(pack).ok)
    }
}
