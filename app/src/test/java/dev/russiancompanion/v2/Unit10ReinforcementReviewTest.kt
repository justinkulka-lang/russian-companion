package dev.russiancompanion.v2
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.curriculum.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
@RunWith(RobolectricTestRunner::class)
@Config(sdk=[33])
class Unit10ReinforcementReviewTest {
 @Test fun earlierPossessionCompletionCannotCreditTheLaterRepeatedGuideOrAbsenceChecks() {
  val p=pack();val u=p.unit(10)!!;val have=objective(p,"have");val absence=objective(p,"absence")
  assertTrue(u.sessions.indexOfFirst{it.id=="u10-s-have"}<u.sessions.indexOfFirst{it.id=="u10-s-absence"})
  assertEquals(p.item("u10-i-have-3")!!.promptLetter,p.item("u10-g-absence-0")!!.promptLetter)
  LocalStore.openInMemory(context()).use { store ->
   record(p,store,have.practiceStepIds.flatMap{p.step(it)!!.second.itemIds}+have.checkItemIds)
   assertEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS,objectiveState(have,store.v2LearnerView(),p))
   assertEquals(5,creditItemIds(p,store.v2LearnerView()).size)
   assertFalse("u10-g-absence-0" in creditItemIds(p,store.v2LearnerView()))
   assertTrue(absence.checkItemIds.none{it in creditItemIds(p,store.v2LearnerView())})
   assertNotEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS,objectiveState(absence,store.v2LearnerView(),p))
  }
 }
 @Test fun repeatedGuideAloneCannotReplaceDistinctPlaceAbsenceAndPositiveCounterexampleEvidence() {
  val p=pack();val o=objective(p,"absence");val guided=o.practiceStepIds.flatMap{p.step(it)!!.second.itemIds}
  assertEquals(listOf("u10-g-absence-0"),guided)
  assertEquals("Нет рестора́нов",p.item("u10-i-absence-1")!!.promptLetter)
  assertEquals("В Москве́ нет ста́рых зда́ний",p.item("u10-i-absence-2")!!.promptLetter)
  assertEquals("Да, у меня есть сын.",p.item("u10-i-absence-3")!!.promptLetter)
  val guideCue=p.item(guided.single())!!.promptLetter
  assertTrue(listOf("u10-i-absence-1","u10-i-absence-2","u10-i-absence-3").all{p.item(it)!!.promptLetter!=guideCue})
  LocalStore.openInMemory(context()).use { store ->
   record(p,store,guided+listOf("u10-i-absence-4"));assertNotEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS,objectiveState(o,store.v2LearnerView(),p))
   record(p,store,listOf("u10-i-absence-1","u10-i-absence-2","u10-i-absence-3"));assertEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS,objectiveState(o,store.v2LearnerView(),p))
   assertEquals(5,store.v2Attempts().size)
  }
 }
 @Test fun wrongPositiveCaseExplanationCannotCreditAnyNegativeContext() {
  val p=pack();val positive="In this line, the possessor is genitive and the possessed thing is nominative."
  LocalStore.openInMemory(context()).use { store ->
   for(id in listOf("u10-i-have-3","u10-g-absence-0","u10-i-absence-1","u10-i-absence-2","u10-i-absence-4")) {
    val i=p.item(id)!!;assertEquals("In this line, the absent thing is in the genitive case.",i.answer);assertTrue(positive in i.options)
    val step=p.unit(10)!!.sessions.flatMap{it.steps}.single{id in it.itemIds};store.v2RecordGovernedAttempt(prepareGovernedAttempt(p,id,positive,PRESCRIBED_CHECK,step.id,"2026-10-13T23:00")!!)
   }
   assertTrue(creditItemIds(p,store.v2LearnerView()).isEmpty());assertEquals(5,store.v2Attempts().size)
  }
 }
 @Test fun allTeachingPrecedesPracticeAndRetainedCaseChainsRemainValid() {
  val p=pack()
  for(kind in listOf("have","absence")) {
   val s=p.unit(10)!!.sessions.single{it.id=="u10-s-$kind"};val teach=s.steps.filter{it.kind=="TEACH"};val rem=s.steps.single{it.kind=="REMEDIATE"}
   assertEquals(teach.size,s.steps.takeWhile{it.kind!="GUIDED"}.count{it.kind=="TEACH"});assertEquals(teach.joinToString("\n\n"){it.body},rem.body);assertEquals(teach.flatMap{it.exampleIds}.distinct(),rem.exampleIds)
  }
  val v=V2Validator.validate(p);assertTrue(v.errors.joinToString("\n"),v.ok)
 }
 private fun objective(p:V2CurriculumPack,kind:String)=p.unit(10)!!.objectives.single{it.id=="obj-u10-$kind"}
 private fun record(p:V2CurriculumPack,store:LocalStore,ids:List<String>) {for(id in ids){val step=p.unit(10)!!.sessions.flatMap{it.steps}.single{id in it.itemIds};store.v2RecordGovernedAttempt(prepareGovernedAttempt(p,id,p.item(id)!!.answer,PRESCRIBED_CHECK,step.id,"2026-10-13T23:00")!!)}}
 private fun context():Context=ApplicationProvider.getApplicationContext()
 private fun pack()=V2PackLoader.parse(File(if(File("app").isDirectory)"." else "..","app/src/main/assets/content/v2-curriculum.json").readText())
}
