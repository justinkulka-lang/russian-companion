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
class Unit9ReinforcementReviewTest {
 @Test fun earlierQuantityCompletionCannotCreditTheLaterRepeatedGuideOrPatternChecks() {
  val p=pack();val u=p.unit(9)!!;val quantity=objective(p,"quantity");val pattern=objective(p,"pattern")
  assertTrue(u.sessions.indexOfFirst{it.id=="u9-s-quantity"}<u.sessions.indexOfFirst{it.id=="u9-s-pattern"})
  val first=p.item("u9-g-quantity-0")!!;val later=p.item("u9-g-pattern-0")!!
  assertEquals(first.prompt,later.prompt);assertEquals(first.promptLetter,later.promptLetter);assertEquals(first.answer,later.answer);assertEquals(first.options,later.options)
  LocalStore.openInMemory(context()).use { store ->
   record(p,store,quantity.practiceStepIds.flatMap{p.step(it)!!.second.itemIds}+quantity.checkItemIds)
   assertEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS,objectiveState(quantity,store.v2LearnerView(),p));assertEquals(10,creditItemIds(p,store.v2LearnerView()).size)
   assertFalse(later.id in creditItemIds(p,store.v2LearnerView()));assertTrue(pattern.checkItemIds.none{it in creditItemIds(p,store.v2LearnerView())});assertNotEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS,objectiveState(pattern,store.v2LearnerView(),p))
  }
 }
 @Test fun repeatedGuideAndBasicCountsCannotReplaceRequiredAnimateAccusativeContext() {
  val p=pack();val o=objective(p,"pattern");val guided=o.practiceStepIds.flatMap{p.step(it)!!.second.itemIds};assertEquals(listOf("u9-g-pattern-0"),guided)
  assertEquals(listOf("три кошки","пять девушек","шестнадцать рублей","Я видел двух друзей"),o.checkItemIds.map{p.item(it)!!.promptLetter})
  assertTrue(o.checkItemIds.all{p.item(it)!!.promptLetter!=p.item(guided.single())!!.promptLetter})
  LocalStore.openInMemory(context()).use { store ->
   record(p,store,guided+o.checkItemIds.dropLast(1));assertNotEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS,objectiveState(o,store.v2LearnerView(),p))
   val id=o.checkItemIds.last();val i=p.item(id)!!;assertEquals("In this line, the animate object is accusative and both numeral and noun are declined.",i.answer)
   val wrong="In this line, use the basic nominative or inanimate accusative counting pattern.";assertTrue(wrong in i.options);attempt(p,store,id,wrong);assertFalse(id in creditItemIds(p,store.v2LearnerView()));assertNotEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS,objectiveState(o,store.v2LearnerView(),p))
   record(p,store,listOf(id));assertEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS,objectiveState(o,store.v2LearnerView(),p));assertEquals(5,creditItemIds(p,store.v2LearnerView()).size);assertEquals(6,store.v2Attempts().size)
  }
 }
 @Test fun oneHourNominativeExplanationCannotCreditOtherBasicCountingPatterns() {
  val p=pack();val wrong=p.item("u9-g-pattern-0")!!.answer
  LocalStore.openInMemory(context()).use { store ->
   for(id in listOf("u9-i-pattern-1","u9-i-pattern-3","u9-i-quantity-1","u9-i-quantity-7")) {
    val i=p.item(id)!!;assertTrue(wrong in i.options);assertNotEquals(wrong,i.answer);attempt(p,store,id,wrong)
   }
   assertTrue(creditItemIds(p,store.v2LearnerView()).isEmpty());assertEquals(4,store.v2Attempts().size)
  }
 }
 @Test fun allTeachingAndWorkedExamplesPrecedePracticeAndReturnInRemediation() {
  val p=pack()
  for(kind in listOf("quantity","pattern")) {
   val s=p.unit(9)!!.sessions.single{it.id=="u9-s-$kind"};val t=s.steps.filter{it.kind=="TEACH"};val r=s.steps.single{it.kind=="REMEDIATE"}
   assertEquals(if(kind=="quantity")10 else 5,t.size);assertEquals(t.size,s.steps.takeWhile{it.kind!="GUIDED"}.count{it.kind=="TEACH"});assertEquals(t.joinToString("\n\n"){it.body},r.body);assertEquals(t.flatMap{it.exampleIds}.distinct(),r.exampleIds);assertEquals(if(kind=="quantity")17 else 7,r.exampleIds.size)
  }
  val v=V2Validator.validate(p);assertTrue(v.errors.joinToString("\n"),v.ok)
 }
 private fun objective(p:V2CurriculumPack,k:String)=p.unit(9)!!.objectives.single{it.id=="obj-u9-$k"}
 private fun record(p:V2CurriculumPack,s:LocalStore,ids:List<String>){for(id in ids)attempt(p,s,id,p.item(id)!!.answer)}
 private fun attempt(p:V2CurriculumPack,s:LocalStore,id:String,answer:String){val t=p.unit(9)!!.sessions.flatMap{it.steps}.single{id in it.itemIds};s.v2RecordGovernedAttempt(prepareGovernedAttempt(p,id,answer,PRESCRIBED_CHECK,t.id,"2026-10-14T00:00")!!)}
 private fun context():Context=ApplicationProvider.getApplicationContext()
 private fun pack()=V2PackLoader.parse(File(if(File("app").isDirectory)"." else "..","app/src/main/assets/content/v2-curriculum.json").readText())
}
