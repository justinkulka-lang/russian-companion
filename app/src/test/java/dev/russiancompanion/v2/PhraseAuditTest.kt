package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.ContentAuditSources
import dev.russiancompanion.v2.curriculum.Phase2Sources
import dev.russiancompanion.v2.curriculum.PhraseAllowlist
import dev.russiancompanion.v2.curriculum.V2PackLoader
import dev.russiancompanion.v2.curriculum.V2Validator
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
        assertEquals(228, allowlist.size)
        assertEquals(allowlist.size, allowlist.toSet().size)
        assertEquals(mapOf("UNATTESTED" to 161, "MISSING_STRESS" to 23, "COMPOSED" to 44), allowlist.groupingBy { it.type }.eachCount())
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
        assertEquals(227, failures.size)
        assertTrue(fields.map { it.unit }.toSet().containsAll((1..30).toSet()))
    }

    @Test fun eachAuditRuleCatchesWhatItShould() {
        fun fail(text: String) = PhraseAudit.failures(listOf(PhraseAudit.Field(16, "probe.body", text))).map { it.type to it.text }.toSet()
        // UNATTESTED: a run not present in any frozen line; an attested run passes.
        assertTrue(("UNATTESTED" to "Он поранил себя") in fail("Он поранил себя."))
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
        assertTrue(fail("In мой стол, стол is masculine singular.").none { it.first == "COMPOSED" })
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
