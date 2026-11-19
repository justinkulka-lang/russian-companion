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
class Unit10IndependentReinforcementTest {
 // T15g (CC010/IA016): the possession and absence objectives no longer repeat each other's tasks. u10-i-have-3 uses the
 // attested husband line and the two cross-objective duplicates (u10-i-absence-3, u10-i-absence-4) are retired, so each
 // objective is credited only by its own distinct checks.
 @Test fun possessionAndAbsenceChecksShareNoTask() {
  val p=pack();val have=obj(p,"have").checkItemIds.map{p.item(it)!!};val absence=obj(p,"absence").checkItemIds.map{p.item(it)!!}
  assertEquals(listOf("u10-i-have-1","u10-i-have-2","u10-i-have-3","u10-i-have-4"),have.map{it.id})
  assertEquals(listOf("u10-i-absence-1","u10-i-absence-2"),absence.filterNot{it.isTypedResponse()}.map{it.id})
  assertEquals("У твоего́ му́жа есть оди́н друг",p.item("u10-i-have-3")!!.promptLetter);assertEquals(positive,p.item("u10-i-have-3")!!.answer)
  assertNull(p.item("u10-i-absence-3"));assertNull(p.item("u10-i-absence-4"))
  for(x in have)for(y in absence)assertFalse("${x.id} ${y.id}",x.promptLetter==y.promptLetter&&x.answer==y.answer&&x.options==y.options)
  LocalStore.openInMemory(context()).use { store ->
   record(p,store,have.map{it.id});assertEquals(have.map{it.id}.toSet(),creditItemIds(p,store.v2LearnerView()))
   assertTrue(absence.none{it.id in creditItemIds(p,store.v2LearnerView())})
   assertNotEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS,objectiveState(obj(p,"absence"),store.v2LearnerView(),p))
  }
 }
 @Test fun earlierPossessionCompletionStillRequiresBothPlaceAbsenceContexts() {
  val p=pack();val have=obj(p,"have");val absence=obj(p,"absence")
  LocalStore.openInMemory(context()).use { store ->
   record(p,store,have.practiceStepIds.flatMap{p.step(it)!!.second.itemIds}+have.checkItemIds.filterNot{p.item(it)!!.isTypedResponse()})
   assertEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS,objectiveState(have,store.v2LearnerView(),p));assertEquals(5,creditItemIds(p,store.v2LearnerView()).size)
   record(p,store,absence.practiceStepIds.flatMap{p.step(it)!!.second.itemIds})
   assertNotEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS,objectiveState(absence,store.v2LearnerView(),p))
   assertEquals("Нет рестора́нов",p.item("u10-i-absence-1")!!.promptLetter);assertEquals("В Москве́ нет ста́рых зда́ний",p.item("u10-i-absence-2")!!.promptLetter)
   attempt(p,store,"u10-i-absence-1",positive);assertFalse("u10-i-absence-1" in creditItemIds(p,store.v2LearnerView()));record(p,store,listOf("u10-i-absence-2"));assertNotEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS,objectiveState(absence,store.v2LearnerView(),p))
   record(p,store,listOf("u10-i-absence-1"));assertEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS,objectiveState(absence,store.v2LearnerView(),p))
   assertEquals(8,creditItemIds(p,store.v2LearnerView()).size);assertEquals(9,store.v2Attempts().size)
  }
 }
 @Test fun wrongPolarityCannotCreditAnyIndependentPossessionOrAbsenceCheck() {
  val p=pack();LocalStore.openInMemory(context()).use { store ->
   for(id in independent) {
    val i=p.item(id)!!;val wrong=if(i.answer==positive)negative else positive;assertTrue(wrong in i.options);assertNotEquals(wrong,i.answer);attempt(p,store,id,wrong)
   }
   assertEquals(independent.size,store.v2Attempts().size);assertTrue(creditItemIds(p,store.v2LearnerView()).isEmpty())
  }
 }
 @Test fun frozenApplicationsCannotBeReversedWithCoherentTupleHashUpdates() {
  val p=pack()
  for(id in independent) {
   val i=p.item(id)!!;val a=i.assessment!!;val wrong=i.options.single{it!=i.answer};val flip=i.copy(answer=wrong,assessment=a.copy(relationParticipantIds=listOf(a.relationParticipantIds[0],a.relationParticipantIds[2],a.relationParticipantIds[1]),answerSourceIds=listOf(a.relationParticipantIds[2]),distractorSourceIds=i.options.map{if(it==wrong)"" else a.relationParticipantIds[1]}))
   val records=p.provenanceRecords.map{r->if(r.sourceRecordId=="ca-rule-context:$id") {
    val apps=r.synthesis!!.applications.map{it.copy(answer=wrong,distractors=listOf(i.answer))};val tuple=buildString{append("tuple-v1:1;");fun field(s:String){append(s.toByteArray().size).append(':').append(s)};field(i.promptLetter!!);field(wrong);append("1;");field(i.answer)}
    val text=r.originalSourceText.substringBeforeLast("\nTuple-v1:")+"\nTuple-v1:"+tuple
    r.copy(originalSourceText=text,sourceHash=sha(text),synthesis=r.synthesis.copy(applications=apps))
   }else r}
   val bad=p.copy(items=p.items.map{if(it.id==id)flip else it},provenanceRecords=records);val result=V2Validator.validate(bad);assertFalse(id,result.ok);assertTrue(result.errors.joinToString("\n"),result.errors.any{it.contains("frozen") || it.contains("context")})
  }
 }
 private val independent=listOf("u10-i-have-1","u10-i-have-2","u10-i-have-3","u10-i-have-4","u10-i-absence-1","u10-i-absence-2")
 private val positive="In this line, the possessor is genitive and the possessed thing is nominative."
 private val negative="In this line, the absent thing is in the genitive case."
 private fun obj(p:V2CurriculumPack,k:String)=p.unit(10)!!.objectives.single{it.id=="obj-u10-$k"}
 private fun record(p:V2CurriculumPack,s:LocalStore,ids:List<String>){for(id in ids)attempt(p,s,id,p.item(id)!!.answer)}
 private fun attempt(p:V2CurriculumPack,s:LocalStore,id:String,answer:String){val t=p.unit(10)!!.sessions.flatMap{it.steps}.single{id in it.itemIds};s.v2RecordGovernedAttempt(prepareGovernedAttempt(p,id,answer,PRESCRIBED_CHECK,t.id,"2026-10-14T00:00")!!)}
 private fun context():Context=ApplicationProvider.getApplicationContext()
 private fun pack()=V2PackLoader.parse(File(if(File("app").isDirectory)"." else "..","app/src/main/assets/content/v2-curriculum.json").readText())
 private fun sha(s:String)=java.security.MessageDigest.getInstance("SHA-256").digest(s.toByteArray()).joinToString(""){"%02x".format(it.toInt() and 255)}
}
