package dev.russiancompanion.v2
import dev.russiancompanion.v2.curriculum.*
import org.junit.Assert.*
import org.junit.Test
import java.io.File
class FullSynthesisTupleCorrectionTest {
    @Test fun fullValidatorRejectsSwappedReadingAnswersEvenWhenAllMutableApplicationsAgree() {
        val p=pack()
        assertTrue(V2Validator.validate(p).ok)
        val first=p.item("u29-i-reading-1")!!
        val last=p.item("u29-i-reading-3")!!
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
        val result=V2Validator.validate(forged)
        assertTrue("The frozen reading tuples must be the reason for rejection",result.errors.any { it.contains("application tuple") })
        assertFalse("The before-leaving cue must not be keyed as sending, or the sending cue as finishing: ${result.errors}",result.ok)
    }
    private fun pack()=V2PackLoader.parse(File(if(File("app").isDirectory) "." else "..","app/src/main/assets/content/v2-curriculum.json").readText())
}
