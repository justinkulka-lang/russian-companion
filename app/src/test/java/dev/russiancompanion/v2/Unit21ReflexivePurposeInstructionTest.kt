package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.*
import kotlinx.serialization.json.*
import org.junit.Assert.*
import org.junit.Test
import java.io.File

class Unit21ReflexivePurposeInstructionTest {
    @Test fun completeInstructionAndRemediationCoverEveryCheckedCueBeforePractice() {
        val p = pack()
        for ((slug, count) in mapOf("svoy" to 4, "object-reference" to 3, "purpose" to 3)) {
            val s = p.unit(21)!!.sessions.first { it.id == "u21-s-$slug" }
            val teaches = s.steps.takeWhile { it.kind != "GUIDED" }.filter { it.kind == "TEACH" }
            assertEquals(count, teaches.size)
            val body = teaches.joinToString("\n") { it.body }
            // T18a-2 expectation change (owner decision D54e), old -> new: every item's cue and options -> every closed item's cue and
            // options, and every typed item's key; a typed cue is a frozen example with its key blanked or a sibling form, so its
            // key, not the cue text, is what the teaching shows before the checks.
            p.items.filter { it.objectiveId == "obj-u21-$slug" }.forEach { item ->
                if (item.isTypedResponse()) { assertTrue("${item.id} key not taught", body.contains(item.answer)); return@forEach }
                assertTrue("${item.id} missing cue", body.contains(item.promptLetter!!))
                item.options.forEach { assertTrue("${item.id} missing option $it", body.contains(it)) }
            }
            val rem = s.steps.first { it.kind == "REMEDIATE" }
            assertEquals(teaches.joinToString("\n\n") { it.body }, rem.body)
            assertEquals(teaches.flatMap { it.exampleIds }.distinct(), rem.exampleIds)
            assertFalse(rem.body.contains("жену́ = I love"))
        }
    }
    @Test fun possessionTracksSubjectButAgreementTracksThePossessedNoun() {
        val p = pack()
        val text = p.unit(21)!!.sessions.first { it.id == "u21-s-svoy" }.steps.joinToString("\n") { it.body }
        assertTrue(text.contains("subject as the owner"))
        assertTrue(text.contains("possessed noun, not with the owner"))
        // T18a-2 expectation change (sweep row, A2-26), old -> new: "do not demonstrate independently declining" -> the learner-voice
        // instruction that replaced that assessment commentary.
        assertTrue(text.contains("practise declining the possessive and constructing other ownership sentences separately"))
        val i = p.item("u21-i-object-reference-1")!!
        assertEquals(BatchBSources.WIFE_SENTENCE + " = I love my wife", BatchBSources.WIFE_LINE_CITE)
        assertTrue(i.feedback.contains(BatchBSources.WIFE_LINE_CITE))
        val bad = i.copy(feedback=i.feedback.replace(BatchBSources.WIFE_LINE_CITE,"жену́ = I love"),
            assessment=i.assessment!!.copy(whyAnswerDemonstratesObjective=i.assessment!!.whyAnswerDemonstratesObjective.replace(BatchBSources.WIFE_LINE_CITE,"жену́ = I love")))
        assertTrue(V2Validator.validate(p.copy(items=p.items.map { if(it.id==i.id) bad else it })).errors.any {
            it.contains(i.id) && it.contains("wife relationship citation is not the wife line")
        })
    }
    @Test fun causalCheckHasNoSynonymousSinceDistractorAndAdmittedBindingsStayValid() {
        val p = pack()
        val i = p.item("u21-i-purpose-2")!!
        assertEquals(listOf("because","although","after"), i.options)
        assertEquals("because", i.answer)
        assertEquals("prov-ex-u21-purpose-3-en", i.assessment!!.distractorSourceIds[1])
        assertEquals("EXPLICIT_SOURCE_PAIR", i.assessment!!.relationKind)
        assertTrue(V2Validator.featureValid(p,i))
        assertFalse(p.item("u21-g-object-reference-0")!!.prompt.contains("equals sign"))
        val records = p.provenanceRecords.filter { it.itemId.startsWith("prov-ca-u21-") }
        assertEquals(75,records.count { !it.itemId.startsWith("prov-ca-u21-object-transfer-") })
        assertEquals(10,records.count { it.itemId.startsWith("prov-ca-u21-object-transfer-") })
        assertEquals(85,records.size)
        records.forEach { assertContentAuditResearch(it) }
        assertTrue(V2Validator.validate(p).errors.joinToString("\n"), V2Validator.validate(p).ok)
        assertEquals("DEVELOPMENT",p.unit(21)!!.installState)
    }
    @Test fun omittedEmptyFinalAuditObjectsDecodeToIdenticalTypedEvidence() {
        val file=File(if(File("app").isDirectory) "." else "..","app/src/main/assets/content/v2-curriculum.json")
        val raw=Json.parseToJsonElement(file.readText()).jsonObject
        val full=JsonObject(raw + ("provenanceRecords" to JsonArray(raw.getValue("provenanceRecords").jsonArray.map { r ->
            val obj=r.jsonObject
            val sy=obj["synthesis"] as? JsonObject
            if(sy==null || "finalAudit" in sy) obj else JsonObject(obj + ("synthesis" to JsonObject(sy + ("finalAudit" to JsonObject(
                listOf("artifactIdentity","artifactDigest","auditedContentDigest","disposition","modelVersion","date").associateWith { JsonPrimitive("") }
            )))))
        })))
        val current=V2PackLoader.parse(file.readText());val expanded=V2PackLoader.parse(full.toString())
        assertEquals(expanded,current)
        assertEquals(V2Validator.contentReviewDigest(expanded),V2Validator.contentReviewDigest(current))
    }
    /** T18a-2 (owner decision D54e; CC022): every required check sits in u21-s-checks after all teaching, with no A2-24 finding. */
    @Test fun unit21ChecksSitInTheEndOfUnitSessionWithNoKeyExposure() {
        val p = pack()
        val unit = p.unit(21)!!
        val checks = unit.sessions.single { it.id == "u21-s-checks" }
        val required = unit.objectives.flatMap { it.checkItemIds }
        assertEquals(21, required.size)
        assertTrue(unit.sessions.filter { it.id != checks.id }.flatMap { it.steps }.none { it.kind == "CHECK" })
        assertTrue(required.all { id -> checks.steps.any { id in it.itemIds } })
        val order = unit.sessions.map { it.id }
        assertTrue(order.indexOf("u21-s-checks") > order.indexOf("u21-s-purpose") && order.last() == "u21-s-book")
        assertTrue(checks.steps.all { it.kind == "CHECK" })
        val (summaries, signals) = V2AssessmentDiagnostics.audit(p)
        val summary = summaries.single { it.unit == 21 }
        assertEquals(listOf(21, 21, 15), listOf(summary.checkedItems, summary.requiredChecks, summary.typedRequiredChecks))
        assertEquals(emptyList<V2DesignSignal>(), signals.filter { it.unit == 21 && it.rule.startsWith("A2_2") })
        // Typed keys: the exact frozen text of their cited record, taught first, and no two spellings of one form.
        val typed = required.map { p.item(it)!! }.filter { it.isTypedResponse() }
        assertEquals(15, typed.size)
        typed.forEach { assertEquals(it.id, emptyList<String>(), V2Validator.typedKeyCitationErrors(p, it)) }
        assertEquals(15, typed.map { java.text.Normalizer.normalize(it.answer, java.text.Normalizer.Form.NFD).replace("\u0301", "").lowercase() }.distinct().size)
    }

    /** T18a-2 (CC021): each remediation repeats its session's teaching and carries every check's cue or key. */
    @Test fun unit21RemediationRepeatsTheTeachingAndCarriesEachCheck() {
        val p = pack()
        val unit = p.unit(21)!!
        for (objective in unit.objectives.filter { it.checkItemIds.isNotEmpty() }) {
            val rem = objective.remediationStepIds.map { p.step(it)!!.second }.joinToString("\n") { it.body }
            for (item in objective.checkItemIds.map { p.item(it)!! }) {
                val cue = item.promptLetter.orEmpty().takeIf { !item.isTypedResponse() }.orEmpty()
                assertTrue(item.id, rem.contains(item.answer) || (cue.isNotEmpty() && rem.contains(cue)))
            }
        }
        // D54b and D54c: the purpose infinitive rule and the register explanation are gone; G:2479's own wording stays.
        val text = unit.sessions.flatMap { it.steps }.joinToString("\n") { it.body }
        assertFalse(text.contains("same subject for both actions"))
        assertFalse(text.contains("ordinary"))
        assertFalse(text.contains("do not infer an archaic register"))
        assertTrue(p.step("u21-st-teach-purpose-2")!!.second.body.contains("(bookish, archaic) — so that"))
        assertFalse(p.item("u21-g-purpose-0")!!.feedback.contains("ordinary"))
    }

    /** T18a-2 (owner decision D54g; CC029): no governance, narration or markup in Unit 21 learner text or ref-u2125-attribution. */
    @Test fun unit21LearnerTextHasNoGovernanceOrNarration() {
        val p = pack()
        val learner = setOf("LEARNER_GOVERNANCE_TERM", "LEARNER_PROVENANCE_NARRATION", "LEARNER_MARKUP", "LEARNER_TABLE_CELL_MARKUP")
        val hits = V2DesignDiagnostics.audit(p).signals.filter { it.rule in learner && (it.unit == 21 || "ref-u2125-attribution" in it.entityIds) }
        assertTrue(hits.joinToString("\n") { "${it.rule} ${it.entityIds} ${it.field}" }, hits.isEmpty())
        val notice = p.references.single { it.id == "ref-u2125-attribution" }
        assertTrue(notice.detail.contains("Unit 23 time expressions"))
        assertTrue(notice.detail.contains("Reflexive pronoun oldid 1374712982"))
        assertFalse(Regex("(?i)admitted|frozen|review-pending").containsMatchIn(notice.summary + notice.detail))
    }

    /** T18a-2 (owner decision D54f; CC036): the two-option checks stay two-option and each has its own CC036 row. */
    @Test fun unit21TwoOptionChecksEachHaveACc036Row() {
        val p = pack()
        val binary = p.unit(21)!!.objectives.flatMap { it.checkItemIds }.map { p.item(it)!! }
            .filter { !it.isTypedResponse() && it.options.size == 2 }.map { it.id }.toSet()
        assertEquals(setOf("u21-i-object-reference-1", "u21-i-object-reference-2"), binary)
        val root = File(if (File("app").isDirectory) "." else "..")
        val rows = File(root, "audit/cc036-binary-required-checks.csv").readLines().filter { it.startsWith("21,") }
        assertEquals(binary, rows.map { it.split(",")[1] }.toSet())
        assertTrue(rows.all { ",BOUNDED_SCOPE," in it })
        assertFalse(p.item("u21-i-object-reference-1")!!.options.contains("He loves his (own) wife"))
    }

    /** T18a-2 (WC011): Unit 21 has its own binder and tutor task, without PDF, index or review wording. */
    @Test fun unit21HasItsOwnBinderAndTutorTask() {
        val unit = pack().unit(21)!!
        val binder = unit.binderTask!!
        val tutor = unit.tutorTask!!
        assertEquals("binder-u21-svoy-conjunctions", binder.id)
        assertEquals("tutor-u21-svoy", tutor.id)
        val text = listOfNotNull(binder.prompt, binder.modelAnswer, tutor.bring)
        assertTrue(text.none { Regex("(?i)\\bpdf\\b|\\bindex\\b|\\breview(?:er|ed)?\\b").containsMatchIn(it) })
        assertTrue(binder.modelAnswer!!.contains("Я люблю́ (свою́) жену́"))
    }
    private fun pack(): V2CurriculumPack = V2PackLoader.parse(
        (System.getProperty("unit21.instruction.baseline")?.let(::File)
            ?: File(if(File("app").isDirectory) "." else "..","app/src/main/assets/content/v2-curriculum.json")).readText())
}
