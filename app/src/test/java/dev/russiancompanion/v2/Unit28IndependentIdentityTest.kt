package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.*
import org.junit.Test
import java.io.File
import java.security.MessageDigest

/** Identity/rejection checks only. These do not qualify the independent research clauses. */
class Unit28IndependentIdentityTest {
    @Test fun bothIndependentFamiliesKeepTheirExactPendingBindings() {
        val p = pack()
        for (item in independent(p)) {
            val cap = capability(p, item)
            val applications = applications(cap)
            assertEquals(1, applications.count { it.cue == item.promptLetter && it.answer == item.answer &&
                it.distractors == item.options.filter { option -> option != item.answer } })
            assertEquals(IMPLEMENTED_PENDING_FINAL_AUDIT, item.reviewStatus)
            assertEquals(sha(cap.exactSpan), cap.sourceHash)
            assertTrue(hosts(p, cap).isNotEmpty())
            hosts(p, cap).forEach { record ->
                val synthesis = requireNotNull(record.synthesis)
                assertEquals(applications, synthesis.applications)
                assertEquals(V2FinalAuditIdentity(), synthesis.finalAudit)
            }
        }
        val validation = V2Validator.validate(p)
        assertTrue(validation.errors.joinToString("\n"), validation.ok)
    }

    @Test fun imperfectiveAuthoritativeApplicationsChangeIdentityAndRejectUnreviewedRewrites() =
        authoritativeChanges("imperfective")

    @Test fun perfectiveAuthoritativeApplicationsChangeIdentityAndRejectUnreviewedRewrites() =
        authoritativeChanges("perfective")

    @Test fun mutableMirrorsCannotOverrideEitherFrozenIndependentFamily() {
        val p = pack()
        val identity = V2Validator.unitContentReviewDigest(p, 28)
        for (item in independent(p)) for (field in listOf("cue", "answer", "distractors")) {
            val cap = capability(p, item)
            val changed = mutate(applications(cap), item, field)
            val ids = hosts(p, cap).map { it.itemId }.toSet()
            val forged = p.copy(provenanceRecords = p.provenanceRecords.map { record ->
                if (record.itemId !in ids) record else record.copy(synthesis = record.synthesis!!.copy(applications = changed))
            })
            assertEquals(cap, forged.sourceCapabilities.single { it.id == cap.id })
            assertEquals(identity, V2Validator.unitContentReviewDigest(forged, 28))
            assertEquals(p.provenanceRecords.map { it.originalSourceText to it.sourceHash },
                forged.provenanceRecords.map { it.originalSourceText to it.sourceHash })
            val validation = V2Validator.validate(forged)
            assertFalse(validation.ok)
            assertTrue(validation.errors.joinToString("\n"), validation.errors.any {
                it.contains(cap.id) && it.contains("exact guided and independent contextual applications")
            })
            println("CC009_CASE item=${item.id} field=$field mode=MIRROR rejected=true unitIdentityChanged=false")
        }
    }

    private fun authoritativeChanges(kind: String) {
        val p = pack()
        val identity = V2Validator.unitContentReviewDigest(p, 28)
        for (item in independent(p).filter { it.objectiveId == "obj-u28-$kind-adverb" })
            for (field in listOf("cue", "answer", "distractors")) {
                val cap = capability(p, item)
                val changed = mutate(applications(cap), item, field)
                val span = changedCorpusSpan(cap, item, changed)
                val hash = sha(span)
                val ids = hosts(p, cap).map { it.itemId }.toSet()
                val forged = p.copy(
                    sourceCapabilities = p.sourceCapabilities.map { if (it.id != cap.id) it else it.copy(exactSpan = span, sourceHash = hash) },
                    provenanceRecords = p.provenanceRecords.map { record ->
                        if (record.itemId !in ids) record else record.copy(originalSourceText = span, sourceHash = hash,
                            synthesis = record.synthesis!!.copy(applications = changed))
                    },
                )
                assertNotEquals(identity, V2Validator.unitContentReviewDigest(forged, 28))
                assertNotEquals(cap.sourceHash, hash)
                val validation = V2Validator.validate(forged)
                assertFalse(validation.ok)
                assertTrue(validation.errors.joinToString("\n"), validation.errors.any {
                    it.contains(cap.id) && it.contains("corrected intake identity does not match")
                })
                println("CC009_CASE item=${item.id} field=$field mode=FROZEN_CORPUS rejected=true unitIdentityChanged=true")
            }
    }

    private fun mutate(applications: List<V2SynthesisApplication>, item: V2CheckItem, field: String): List<V2SynthesisApplication> {
        var count = 0
        val result = applications.map { application ->
            if (application.cue != item.promptLetter || application.answer != item.answer) application else {
                count++
                when (field) {
                    "cue" -> application.copy(cue = application.cue.substringBefore(","))
                    "answer" -> application.copy(answer = application.distractors.first())
                    else -> application.copy(distractors = application.distractors.reversed())
                }
            }
        }
        assertEquals(1, count)
        assertNotEquals(applications, result)
        return result
    }
    private fun changedCorpusSpan(cap: V2SourceCapability, item: V2CheckItem, changed: List<V2SynthesisApplication>): String {
        val original = applications(cap)
        val index = original.indexOfFirst { it.cue == item.promptLetter && it.answer == item.answer }
        assertTrue(index >= 0)
        // Preserve the retained Python JSON spacing and every byte outside this one tuple.
        fun tuple(application: V2SynthesisApplication): String =
            "{\"cue\": ${Json.encodeToString(application.cue)}, \"answer\": ${Json.encodeToString(application.answer)}, \"distractors\": " +
                application.distractors.joinToString(", ", "[", "]") { Json.encodeToString(it) } + "}"
        val line = cap.exactSpan.substringAfterLast('\n')
        val old = tuple(original[index])
        val at = line.indexOf(old)
        assertTrue(at >= 0)
        assertEquals(at, line.lastIndexOf(old))
        val newLine = line.replaceRange(at, at + old.length, tuple(changed[index]))
        assertEquals(changed, Json.decodeFromString<List<V2SynthesisApplication>>(newLine))
        return cap.exactSpan.substringBeforeLast('\n') + "\n" + newLine
    }
    private fun independent(p: V2CurriculumPack) = listOf("imperfective", "perfective").flatMap { kind ->
        listOf(1, 2).map { p.item("u28-i-$kind-adverb-$it")!! }
    }
    private fun capability(p: V2CurriculumPack, item: V2CheckItem) =
        p.sourceCapabilities.single { it.id == "cap-${item.objectiveId}" }
    private fun applications(cap: V2SourceCapability) =
        Json.decodeFromString<List<V2SynthesisApplication>>(cap.exactSpan.substringAfterLast('\n'))
    private fun hosts(p: V2CurriculumPack, cap: V2SourceCapability) = p.provenanceRecords.filter {
        it.sourceRecordId == cap.sourceRecordId && it.sourceHash == cap.sourceHash && it.originalSourceText == cap.exactSpan
    }
    private fun sha(text: String) = MessageDigest.getInstance("SHA-256").digest(text.toByteArray())
        .joinToString("") { "%02x".format(it.toInt() and 255) }
    private fun pack() = V2PackLoader.parse(File(if (File("app").isDirectory) "." else "..",
        "app/src/main/assets/content/v2-curriculum.json").readText())
}
