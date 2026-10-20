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
class Unit15RequestAspectHistoryTest {
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
        val added=p.items.drop(old.items.size);assertEquals(4,added.size)
        assertTrue(added.all { it.sourceId=="src-content-audit-research" && it.assessment!!.cueLanguage=="en" && !Regex("[А-Яа-яЁё]").containsMatchIn(it.toString()) })
        assertNotEquals(V2Validator.unitContentReviewDigest(old,15),V2Validator.unitContentReviewDigest(p,15))
        val v=V2Validator.validate(p);assertTrue(v.errors.joinToString("\n"),v.ok)
    }
    @Test fun pairRecallAndOptionalContextSuccessCannotReplaceTheRequiredContextAnswers() {
        val p=current();val old=previous();val oid="obj-u15-aspect-consolidate";val pairIds=listOf("u15-g-aspect-consolidate-0","u15-i-aspect-consolidate-1","u15-i-aspect-consolidate-2")
        LocalStore.openInMemory(context()).use { s ->
            for(id in pairIds)record(old,s,id,old.item(id)!!.answer)
            assertEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS,objectiveState(old.unit(15)!!.objectives.single { it.id==oid },s.v2LearnerView(),old))
        }
        LocalStore.openInMemory(context()).use { s ->
            val o=p.unit(15)!!.objectives.single { it.id==oid }
            for(id in pairIds)record(p,s,id,p.item(id)!!.answer)
            val contexts=o.checkItemIds.filter { it !in pairIds }
            assertEquals(3,contexts.size)
            assertNotEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS,objectiveState(o,s.v2LearnerView(),p))
            for(id in contexts)s.v2RecordGovernedAttempt(prepareGovernedAttempt(p,id,p.item(id)!!.answer,OPTIONAL_PRACTICE,practiceStepId("grammar"),"optional")!!)
            assertNotEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS,objectiveState(o,s.v2LearnerView(),p))
            val guide="u15-g-request-aspect-repeat";record(p,s,guide,p.item(guide)!!.answer)
            for(id in contexts.dropLast(1))record(p,s,id,p.item(id)!!.answer)
            val last=contexts.last();record(p,s,last,p.item(last)!!.options.single { it!=p.item(last)!!.answer })
            assertNotEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS,objectiveState(o,s.v2LearnerView(),p))
            record(p,s,last,p.item(last)!!.answer)
            assertEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS,objectiveState(o,s.v2LearnerView(),p))
            assertFalse(advancementDecision(p,p.unit(15)!!,s.v2LearnerView()).allowed)
            val queue=practiceQueue(p,s.v2LearnerView().copy(currentUnitNumber=15),"grammar")
            assertTrue(queue.map { it.id }.containsAll(contexts+guide))
        }
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
                    val now=p.step("u15-st-teach-request-aspect-1")!!
                    t.v2SaveProfile(t.v2Profile().copy(currentSessionId=now.first.id,currentStepIndex=now.first.steps.indexOf(now.second)).verifiedResume(p))
                    assertTrue(resumeIsCurrent(p,t.v2LearnerView()))
                }
            }
        } finally { context().deleteDatabase(name);dir.deleteRecursively() }
    }
    @Test fun coherentAnswerAndApplicationSwapCannotForgeTheBoundContext() {
        val p=current();val id="u15-i-request-aspect-prohibit";val item=p.item(id)!!;val other=item.options.single { it!=item.answer }
        val altered=item.copy(answer=other)
        val rows=p.provenanceRecords.map { r->if(r.sourceRecordId!="ca-rule-context:$id")r else r.copy(synthesis=r.synthesis!!.copy(applications=r.synthesis.applications.map { it.copy(answer=other,distractors=listOf(item.answer)) })) }
        assertEquals(p.provenanceRecords.map { it.copy(synthesis=null) },rows.map { it.copy(synthesis=null) })
        val v=V2Validator.validate(p.copy(items=p.items.map { if(it.id==id)altered else it },provenanceRecords=rows))
        assertFalse(v.ok);assertTrue(v.errors.joinToString("\n"),v.errors.any { it.contains("complete rule context") })
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
