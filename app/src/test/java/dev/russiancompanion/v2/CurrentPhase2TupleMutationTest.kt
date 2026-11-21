package dev.russiancompanion.v2
import dev.russiancompanion.v2.curriculum.*
import org.junit.Assert.*
import org.junit.Test
import java.io.File
/** CC008: actual full Kotlin validation, never a JSON-only projection. */
class CurrentPhase2TupleMutationTest {
    @Test fun unchangedCurrentPackIsAccepted() {
        val result = V2Validator.validate(pack())
        assertTrue("Unchanged positive control: ${result.errors}", result.ok)
    }
    @Test fun fullValidatorRejectsSwappedCompletionAnswersWithFrozenNarrative() {
        rejectsMutableTupleRebinding("u23-i-completion-1", "u23-i-completion-2", "application tuple")
    }
    @Test fun fullValidatorRejectsSwappedNameAnswersEvenWhenAllMutableApplicationsAgree() {
        rejectsMutableTupleRebinding("u25-i-name-form-1", "u25-i-name-form-2", "conflicting governed descriptions")
    }
    @Test fun fullValidatorRejectsSwappedMannerLabelsWithFrozenNarrative() {
        val p = pack()
        assertTrue(V2Validator.validate(p).ok)
        val original = p.item("u7-g-manner-1")!!
        val records = p.provenanceRecords.filter { it.sourceRecordId == "phase2:bounded-manner-adverb-formation" }
        val applications = records.first().synthesis!!.applications
        val current = applications.single { it.cue == original.promptLetter }
        val other = applications.single { it.answer == "admitted fast manner-adverb relationship" }
        val swaps = mapOf(
            current.answer to other.answer, other.answer to current.answer,
            "The documented creative pair is a separate admitted manner-adverb relationship." to
                "The documented fast pair is one admitted manner-adverb relationship, not a rule for every adjective.",
            "The documented fast pair is one admitted manner-adverb relationship, not a rule for every adjective." to
                "The documented creative pair is a separate admitted manner-adverb relationship.",
        )
        fun replaceLabels(text: String): String {
            // Simultaneous substitutions; no newly generated native text.
            return Regex(swaps.keys.joinToString("|") { Regex.escape(it) })
                .replace(text) { swaps.getValue(it.value) }
        }
        val ids = records.filter { it.shippedText in setOf(current.answer,other.answer) }.associate { record ->
            record.itemId to records.single { it.shippedText == swaps.getValue(record.shippedText) }.itemId
        }
        fun source(id: String) = ids[id] ?: id
        val a = original.assessment!!
        val forgedItem = original.copy(
            options=original.options.map(::replaceLabels), answer=replaceLabels(original.answer),
            feedback=replaceLabels(original.feedback),
            distractorRationales=original.distractorRationales.map(::replaceLabels),
            assessment=a.copy(
                answerSourceIds=a.answerSourceIds.map(::source),
                distractorSourceIds=a.distractorSourceIds.map(::source),
                distractorDifference=a.distractorDifference.map(::replaceLabels),
                whyAnswerDemonstratesObjective=replaceLabels(a.whyAnswerDemonstratesObjective),
                contrastMembers=a.contrastMembers.map(::replaceLabels),
                relationParticipantIds=a.relationParticipantIds.map(::source),
            ),
        )
        val forgedRecords = p.provenanceRecords.map { record ->
            if (record.sourceRecordId != records.first().sourceRecordId) record else
                record.copy(synthesis=record.synthesis!!.copy(applications=record.synthesis.applications.map { application ->
                    if (application.cue !in setOf(current.cue,other.cue)) application else
                        application.copy(answer=replaceLabels(application.answer),distractors=application.distractors.map(::replaceLabels))
                }))
        }
        val forged = p.copy(items=p.items.map { if(it.id==original.id) forgedItem else it },provenanceRecords=forgedRecords)
        assertEquals(p.provenanceRecords.map { it.copy(synthesis=null) },forgedRecords.map { it.copy(synthesis=null) })
        assertEquals(p,forged.copy(items=p.items,provenanceRecords=p.provenanceRecords))
        val result=V2Validator.validate(forged)
        println("CC008 manner active guided cue accepted=${result.ok}; errors=${result.errors}")
        assertFalse("Active creative cue must not be rebound to the fast label: ${result.errors}",result.ok)
    }
    private fun rejectsMutableTupleRebinding(firstId:String,lastId:String, expectedGuard:String="application tuple") {
        val p=pack()
        assertTrue(V2Validator.validate(p).ok)
        val first=p.item(firstId)!!
        val last=p.item(lastId)!!
        val cap=p.sourceCapabilities.single { it.objectiveId==first.objectiveId }
        val forgedItems=p.items.map { item ->
            val donor=when(item.id) { first.id->last;last.id->first;else->null }
            if(donor==null)item else item.copy(
                options=donor.options,answer=donor.answer,feedback=donor.feedback,distractorRationales=donor.distractorRationales,
                assessment=item.assessment!!.copy(
                    answerSourceIds=donor.assessment!!.answerSourceIds,
                    distractorSourceIds=donor.assessment.distractorSourceIds,
                    distractorDifference=donor.assessment.distractorDifference,
                    whyAnswerDemonstratesObjective=donor.assessment.whyAnswerDemonstratesObjective,
                    contrastMembers=donor.assessment.contrastMembers,
                    relationParticipantIds=listOf(item.assessment.relationParticipantIds.first())+donor.assessment.relationParticipantIds.drop(1),
                ),
            )
        }
        val records=p.provenanceRecords.map { r ->
            if(r.sourceRecordId!=cap.sourceRecordId || r.sourceHash!=cap.sourceHash)r
            else r.copy(synthesis=r.synthesis!!.copy(applications=r.synthesis.applications.map { application ->
                val donor=when(application.cue) { first.promptLetter->last;last.promptLetter->first;else->null }
                if(donor==null)application else application.copy(answer=donor.answer,distractors=donor.options.filter { it!=donor.answer })
            }))
        }
        val forged=p.copy(items=forgedItems,provenanceRecords=records)
        assertEquals(cap,forged.sourceCapabilities.single { it.id==cap.id })
        assertTrue(records.zip(p.provenanceRecords).all { (a,b)->a.originalSourceText==b.originalSourceText && a.sourceHash==b.sourceHash && a.shippedText==b.shippedText && a.contentHash==b.contentHash })
        assertEquals(p.provenanceRecords.map { it.copy(synthesis=null) }, records.map { it.copy(synthesis=null) })
        assertEquals(p, forged.copy(items=p.items,provenanceRecords=p.provenanceRecords))
        val result=V2Validator.validate(forged)
        println("CC008 $firstId/$lastId accepted=${result.ok}; errors=${result.errors}")
        assertFalse("Keys for $firstId and $lastId must stay bound to their frozen cues: ${result.errors}",result.ok)
        assertTrue("The frozen application tuples must be the reason for rejection: ${result.errors}",result.errors.any { it.contains(expectedGuard) })
    }
    private fun pack()=V2PackLoader.parse(File(if(File("app").isDirectory) "." else "..","app/src/main/assets/content/v2-curriculum.json").readText())
}
