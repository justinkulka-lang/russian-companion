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
class Unit8ExistenceHistoryTest {
    @Test fun frozenEvidenceOtherObjectivesAndLaterGenitiveStayExact() {
        val live=current();val p=RecordedCurriculumConservationScope.historicalTransition(root(),live,"fe919656bfa2775b5cc2c22f2496d4258343cfea",8);val old=previous();val changed=setOf("u8-g-availability-0","u8-i-availability-1","u8-i-availability-2","u8-i-availability-3")
        assertEquals(old.examples,p.examples)
        assertEquals(old.items.filter { it.id !in changed },p.items.filter { it.id !in changed })
        assertEquals(old.items.map { it.id },p.items.map { it.id })
        assertEquals(old.provenanceRecords,p.provenanceRecords.take(old.provenanceRecords.size))
        assertTrue(old.provenanceRecords.filter { it.sourceRecordId.startsWith("ca-rule-context:u8-") }.all { CompleteRuleContextPins.matchesIfPinned(it) })
        assertEquals(old.units.filter { it.number!=8 },p.units.filter { it.number!=8 })
        val a=old.unit(8)!!;val b=p.unit(8)!!
        assertEquals(a.advancement,b.advancement);assertEquals(a.prerequisiteUnitNumbers,b.prerequisiteUnitNumbers)
        assertEquals(a.objectives.filter { it.id!="obj-u8-availability" },b.objectives.filter { it.id!="obj-u8-availability" })
        assertEquals(a.sessions.filter { it.id!="u8-s-availability" },b.sessions.filter { it.id!="u8-s-availability" })
        assertEquals(old.sourceCapabilities,p.sourceCapabilities)
        assertNotEquals(V2Validator.unitContentReviewDigest(old,8),V2Validator.unitContentReviewDigest(p,8))
        val result=V2Validator.validate(live);assertTrue(result.errors.joinToString("\n"),result.ok)
    }
    @Test fun historicalCreditStepAndResumeSurviveBackupButCannotCreditTheNewUnit() {
        val p=current();val old=previous();val id="u8-i-availability-1";val teach="u8-st-teach-availability-2"
        val name="existence-${System.nanoTime()}.db";val dir=File(context().cacheDir,"existence-${System.nanoTime()}")
        try {
            LocalStore.openInMemory(context()).use { s ->
                record(old,s,id,old.item(id)!!.answer)
                s.v2CompleteGovernedStep(teach,governedStepCompletionDigest(old,s.v2LearnerView(),teach)!!,"old")
                val at=old.step(teach)!!
                s.v2SaveProfile(s.v2Profile().copy(onboardingCompleted=true,currentUnitNumber=8,startingPoint="RESUME_FROM_BOOK",currentSessionId=at.first.id,currentStepIndex=at.first.steps.indexOf(at.second)).verifiedResume(old))
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
                    record(p,t,id,p.item(id)!!.options.first { it!=p.item(id)!!.answer });assertFalse(id in creditItemIds(p,t.v2LearnerView()))
                    record(p,t,id,p.item(id)!!.answer);assertTrue(id in creditItemIds(p,t.v2LearnerView()))
                    assertEquals(before.attempts,t.governedLearnerDocument().attempts.take(before.attempts.size))
                    assertFalse(advancementDecision(p,p.unit(8)!!,t.v2LearnerView()).allowed)
                }
            }
        } finally { context().deleteDatabase(name);dir.deleteRecursively() }
    }
    @Test fun positiveObjectiveNeedsEachIndependentAnswerAndLabsUseCurrentItems() {
        val p=current();val o=p.unit(8)!!.objectives.single { it.id=="obj-u8-availability" }
        LocalStore.openInMemory(context()).use { s->
            val guide=p.step(o.practiceStepIds.single())!!.second.itemIds.single();record(p,s,guide,p.item(guide)!!.answer)
            for(id in o.checkItemIds.dropLast(1))record(p,s,id,p.item(id)!!.answer)
            assertNotEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS,objectiveState(o,s.v2LearnerView(),p))
            val last=o.checkItemIds.last();record(p,s,last,p.item(last)!!.answer)
            assertEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS,objectiveState(o,s.v2LearnerView(),p))
            val queue=practiceQueue(p,s.v2LearnerView().copy(currentUnitNumber=8),"grammar").filter { it.objectiveId==o.id }
            assertEquals((listOf(guide)+o.checkItemIds).toSet(),queue.map { it.id }.toSet())
            assertFalse(queue.any { Regex("genitive|absence|absent|нет",RegexOption.IGNORE_CASE).containsMatchIn(it.toString()) })
            assertFalse(advancementDecision(p,p.unit(8)!!,s.v2LearnerView()).allowed)
        }
    }
    @Test fun restoringTheOriginalAnalysisOrForgingTheNewTupleFailsClosed() {
        val p=current();val old=previous();val id="u8-i-availability-1"
        val restored=V2Validator.validate(p.copy(items=p.items.map { if(it.id==id)old.item(id)!! else it }))
        assertFalse(restored.ok);assertTrue(restored.errors.any { it.contains("complete rule context") })
        val item=p.item(id)!!;val changed=item.copy(answer=item.options.first { it!=item.answer })
        val rows=p.provenanceRecords.map { r->if(r.sourceRecordId!="ca-rule-context:$id:existence-v1")r else r.copy(synthesis=r.synthesis!!.copy(applications=r.synthesis.applications.map { it.copy(answer=changed.answer,distractors=listOf(item.answer)) })) }
        val forged=V2Validator.validate(p.copy(items=p.items.map { if(it.id==id)changed else it },provenanceRecords=rows))
        assertFalse(forged.ok);assertTrue(forged.errors.any { it.contains("complete rule context") })
    }
    private fun record(p:V2CurriculumPack,s:LocalStore,id:String,answer:String) {
        val step=p.unit(8)!!.sessions.flatMap { it.steps }.single { id in it.itemIds }
        s.v2RecordGovernedAttempt(prepareGovernedAttempt(p,id,answer,PRESCRIBED_CHECK,step.id,"2026-10-19T23:00:${s.v2Attempts().size}")!!)
    }
    private fun previous():V2CurriculumPack {
        val proc=ProcessBuilder("git","show","7c369a8ce8adf7571046ec83331de0630765b759:app/src/main/assets/content/v2-curriculum.json").directory(root()).start()
        val pack=V2PackLoader.parse(proc.inputStream.bufferedReader().readText());assertEquals(0,proc.waitFor());return pack
    }
    private fun current()=V2PackLoader.parse(File(root(),"app/src/main/assets/content/v2-curriculum.json").readText())
    private fun root()=File(if(File("app").isDirectory)"." else "..")
    private fun context():Context=ApplicationProvider.getApplicationContext()
}
