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
class Unit15AspectPreparationHistoryTest {
    @Test fun originalItemsExamplesSourcesPoliciesAndOtherUnitsStayExact() {
        val p=current();val old=previous()
        assertEquals(old.items,p.items.take(old.items.size));assertEquals(old.examples,p.examples.take(old.examples.size))
        assertEquals(old.provenanceRecords,p.provenanceRecords.take(old.provenanceRecords.size))
        assertEquals(old.units.filter { it.number!=15 },p.units.filter { it.number!=15 })
        val a=old.unit(15)!!;val b=p.unit(15)!!
        assertEquals(a.advancement,b.advancement);assertEquals(a.prerequisiteUnitNumbers,b.prerequisiteUnitNumbers)
        assertEquals(a.objectives.filter { it.id!="obj-u15-aspect-consolidate" },b.objectives.filter { it.id!="obj-u15-aspect-consolidate" })
        assertEquals(a.sessions.filter { it.id!="u15-s-aspect-consolidate" },b.sessions.filter { it.id!="u15-s-aspect-consolidate" })
        assertEquals(old.sourceCapabilities,p.sourceCapabilities)
        assertEquals(old.items,p.items);assertEquals(old.examples,p.examples)
        assertNotEquals(V2Validator.unitContentReviewDigest(old,15),V2Validator.unitContentReviewDigest(p,15))
        val v=V2Validator.validate(p);assertTrue(v.errors.joinToString("\n"),v.ok)
    }
    @Test fun oldAcceptedWorkAndResumeStayStoredThroughBackupReopenButNeedCurrentWork() {
        val p=current();val old=previous();val id="u15-i-aspect-consolidate-1";val teach="u15-st-teach-aspect-consolidate-2"
        val name="request-aspect-${System.nanoTime()}.db";val dir=File(context().cacheDir,"request-aspect-${System.nanoTime()}")
        try {
            LocalStore.openInMemory(context()).use { s ->
                record(old,s,id,old.item(id)!!.answer)
                s.v2CompleteGovernedStep(teach,governedStepCompletionDigest(old,s.v2LearnerView(),teach)!!,"old")
                val at=old.step(teach)!!
                s.v2SaveProfile(s.v2Profile().copy(onboardingCompleted=true,currentUnitNumber=15,startingPoint="RESUME_FROM_BOOK",currentSessionId=at.first.id,currentStepIndex=at.first.steps.indexOf(at.second)).verifiedResume(old))
                val before=s.governedLearnerDocument()
                assertTrue(id in creditItemIds(old,s.v2LearnerView()));assertTrue(resumeIsCurrent(old,s.v2LearnerView()))
                assertTrue(completedStepCounts(old,s.v2LearnerView(),teach))
                assertFalse(id in creditItemIds(p,s.v2LearnerView()));assertFalse(resumeIsCurrent(p,s.v2LearnerView()))
                assertFalse(completedStepCounts(p,s.v2LearnerView(),teach));assertEquals(before,s.governedLearnerDocument())
                val bytes=ByteArrayOutputStream();writeGovernedBackup(bytes,"dev.russiancompanion",p.packId,p.packVersion,"FOLLOW_SYSTEM",before,emptyList())
                val read=readGovernedBackup(bytes.toByteArray().inputStream(),"dev.russiancompanion",p.packId,p.packVersion,dir) as BackupReadResult.Valid
                LocalStore.openNamed(context(),name).use { t->assertEquals(GovernedReplaceOutcome.REPLACED,t.replaceGovernedLearner(read.backup.document,emptyList())) }
                LocalStore.openNamed(context(),name).use { t->
                    assertEquals(before,t.governedLearnerDocument());assertFalse(resumeIsCurrent(p,t.v2LearnerView()))
                    record(p,t,id,p.item(id)!!.answer);assertTrue(id in creditItemIds(p,t.v2LearnerView()))
                    assertEquals(before.attempts,t.governedLearnerDocument().attempts.take(before.attempts.size))
                    val now=p.step("u15-st-teach-aspect-consolidate-1")!!
                    t.v2SaveProfile(t.v2Profile().copy(currentSessionId=now.first.id,currentStepIndex=now.first.steps.indexOf(now.second)).verifiedResume(p))
                    assertTrue(resumeIsCurrent(p,t.v2LearnerView()))
                }
            }
        } finally { context().deleteDatabase(name);dir.deleteRecursively() }
    }
    @Test fun overlappingEnglishLabelsRejectWithoutThrowingAndDoNotBecomeNativeEvidence() {
        val p=current();val id="u15-i-aspect-consolidate-1";val item=p.item(id)!!
        val options=listOf("imperfective","perfective")
        val bad=item.copy(answer="perfective",options=options,assessment=item.assessment!!.copy(contrastMembers=options))
        val v=V2Validator.validate(p.copy(items=p.items.map { if(it.id==id)bad else it }))
        assertFalse(v.ok)
        assertTrue(v.errors.joinToString("\n"),v.errors.any { it.contains(id) && (it.contains("response type") || it.contains("explicit target")) })
    }
    private fun record(p:V2CurriculumPack,s:LocalStore,id:String,answer:String) {
        val step=p.unit(15)!!.sessions.flatMap { it.steps }.single { id in it.itemIds }
        s.v2RecordGovernedAttempt(prepareGovernedAttempt(p,id,answer,PRESCRIBED_CHECK,step.id,"2026-10-20T00:00:${s.v2Attempts().size}")!!)
    }
    private fun previous():V2CurriculumPack {
        val proc=ProcessBuilder("git","show","175a793020d85b8760a9255cf8b2b6b0091745f4:app/src/main/assets/content/v2-curriculum.json").directory(root()).start()
        val p=V2PackLoader.parse(proc.inputStream.bufferedReader().readText());assertEquals(0,proc.waitFor());return p
    }
    private fun current()=V2PackLoader.parse(File(root(),"app/src/main/assets/content/v2-curriculum.json").readText())
    private fun root()=File(if(File("app").isDirectory)"." else "..")
    private fun context():Context=ApplicationProvider.getApplicationContext()
}
