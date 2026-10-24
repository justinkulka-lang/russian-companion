package dev.russiancompanion.v2

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.backup.*
import dev.russiancompanion.v2.curriculum.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.ByteArrayOutputStream
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk=[33])
class Unit7InvariancePreparationHistoryTest {
    @Test fun originalItemsExamplesSourcesPoliciesAndOtherUnitsStayExact() {
        val p=current();val old=previous()
        assertEquals(old.items,p.items.take(old.items.size));assertEquals(old.examples,p.examples.take(old.examples.size))
        assertEquals(old.provenanceRecords,p.provenanceRecords.take(old.provenanceRecords.size))
        // Preserve the original Unit7-only transition before the later5/6 declarations.
        RecordedCurriculumConservationScope.beforeUnit5GrammarReview(root(),p).let { p ->
            assertEquals(old.units.filter { it.number!=7 },p.units.filter { it.number!=7 })
        }
        RecordedCurriculumConservationScope.assertCurrent(root(),p)
        val a=old.unit(7)!!;val b=p.unit(7)!!
        RecordedCurriculumConservationScope.beforeUnit7GenderReview(root(),p).let { historical ->
            val b=historical.unit(7)!!
            assertEquals(a.advancement,b.advancement);assertEquals(a.prerequisiteUnitNumbers,b.prerequisiteUnitNumbers)
        }
        assertEquals(Unit7GenderCumulativeReviewPolicy.definition.id,b.advancement!!.cumulativeReviewPolicyId)
        assertNotEquals(a.advancement!!.cumulativeReviewPolicyId,b.advancement!!.cumulativeReviewPolicyId)
        assertEquals(a.objectives.filter { it.id!="obj-u7-adverb-invariance" },b.objectives.filter { it.id!="obj-u7-adverb-invariance" })
        assertEquals(a.sessions.filter { it.id!="u7-s-adverb-invariance" },b.sessions.filter { it.id!="u7-s-adverb-invariance" })
        assertEquals(old.sourceCapabilities,p.sourceCapabilities)
        assertEquals(old.items,p.items);assertEquals(old.examples,p.examples)
        assertNotEquals(V2Validator.unitContentReviewDigest(old,7),V2Validator.unitContentReviewDigest(p,7))
        val v=V2Validator.validate(p);assertTrue(v.errors.joinToString("\n"),v.ok)
    }
    @Test fun oldAcceptedWorkAndResumeStayStoredThroughBackupReopenButNeedCurrentWork() {
        val p=current();val old=previous();val id="u7-i-adverb-invariance-1";val teach="u7-st-teach-adverb-invariance-2"
        val repair="u7-st-rem-adverb-invariance";val english="u7-st-teach-adverb-invariance-english"
        val name="invariance-preparation-${System.nanoTime()}.db";val dir=File(context().cacheDir,"invariance-preparation-${System.nanoTime()}")
        try {
            LocalStore.openInMemory(context()).use { s ->
                record(old,s,id,old.item(id)!!.answer)
                s.v2CompleteGovernedStep(teach,governedStepCompletionDigest(old,s.v2LearnerView(),teach)!!,"old")
                s.v2CompleteGovernedStep(repair,governedStepCompletionDigest(old,s.v2LearnerView(),repair)!!,"old")
                val at=old.step(teach)!!
                s.v2SaveProfile(s.v2Profile().copy(onboardingCompleted=true,currentUnitNumber=7,startingPoint="RESUME_FROM_BOOK",currentSessionId=at.first.id,currentStepIndex=at.first.steps.indexOf(at.second)).verifiedResume(old))
                val before=s.governedLearnerDocument()
                assertTrue(id in creditItemIds(old,s.v2LearnerView()));assertTrue(resumeIsCurrent(old,s.v2LearnerView()))
                assertTrue(completedStepCounts(old,s.v2LearnerView(),teach))
                assertTrue(completedStepCounts(old,s.v2LearnerView(),repair))
                assertFalse(id in creditItemIds(p,s.v2LearnerView()));assertFalse(resumeIsCurrent(p,s.v2LearnerView()))
                // Step completion binds the step's own content, not the entire changed unit.
                assertTrue(completedStepCounts(p,s.v2LearnerView(),teach))
                assertFalse(completedStepCounts(p,s.v2LearnerView(),repair))
                assertFalse(completedStepCounts(p,s.v2LearnerView(),english));assertEquals(before,s.governedLearnerDocument())
                val bytes=ByteArrayOutputStream();writeGovernedBackup(bytes,"dev.russiancompanion",p.packId,p.packVersion,"FOLLOW_SYSTEM",before,emptyList())
                val read=readGovernedBackup(bytes.toByteArray().inputStream(),"dev.russiancompanion",p.packId,p.packVersion,dir) as BackupReadResult.Valid
                LocalStore.openNamed(context(),name).use { t->assertEquals(GovernedReplaceOutcome.REPLACED,t.replaceGovernedLearner(read.backup.document,emptyList())) }
                LocalStore.openNamed(context(),name).use { t->
                    assertEquals(before,t.governedLearnerDocument());assertFalse(resumeIsCurrent(p,t.v2LearnerView()))
                    assertTrue(completedStepCounts(p,t.v2LearnerView(),teach))
                    assertFalse(completedStepCounts(p,t.v2LearnerView(),repair))
                    assertFalse(completedStepCounts(p,t.v2LearnerView(),english))
                    t.v2CompleteGovernedStep(english,governedStepCompletionDigest(p,t.v2LearnerView(),english)!!,"new")
                    assertTrue(completedStepCounts(p,t.v2LearnerView(),english))
                    record(p,t,id,p.item(id)!!.answer);assertTrue(id in creditItemIds(p,t.v2LearnerView()))
                    assertEquals(before.attempts,t.governedLearnerDocument().attempts.take(before.attempts.size))
                    val now=p.step("u7-st-teach-adverb-invariance-1")!!
                    t.v2SaveProfile(t.v2Profile().copy(currentSessionId=now.first.id,currentStepIndex=now.first.steps.indexOf(now.second)).verifiedResume(p))
                    assertTrue(resumeIsCurrent(p,t.v2LearnerView()))
                }
            }
        } finally { context().deleteDatabase(name);dir.deleteRecursively() }
    }
    private fun record(p:V2CurriculumPack,s:LocalStore,id:String,answer:String) {
        val step=p.unit(7)!!.sessions.flatMap { it.steps }.single { id in it.itemIds }
        s.v2RecordGovernedAttempt(prepareGovernedAttempt(p,id,answer,PRESCRIBED_CHECK,step.id,"2026-10-20T00:00:${s.v2Attempts().size}")!!)
    }
    private fun previous():V2CurriculumPack {
        val proc=ProcessBuilder("git","show","c6484856a632b034b8636da79f000b5b1bbf6a14:app/src/main/assets/content/v2-curriculum.json").directory(root()).start()
        val p=V2PackLoader.parse(proc.inputStream.bufferedReader().readText());assertEquals(0,proc.waitFor());return p
    }
    private fun current()=V2PackLoader.parse(File(root(),"app/src/main/assets/content/v2-curriculum.json").readText())
    private fun root()=File(if(File("app").isDirectory)"." else "..")
    private fun context():Context=ApplicationProvider.getApplicationContext()
}
