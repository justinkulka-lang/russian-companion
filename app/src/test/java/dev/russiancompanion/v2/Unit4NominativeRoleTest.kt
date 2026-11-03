package dev.russiancompanion.v2
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.curriculum.*
import dev.russiancompanion.v2.ui.V2App
import org.junit.Assert.*
import org.junit.Test
import org.junit.Rule
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import java.security.MessageDigest
@RunWith(RobolectricTestRunner::class)
@Config(sdk=[33])
class Unit4NominativeRoleTest {
 @get:Rule val compose=createComposeRule()
 @Test fun twoDistinctIndependentSubjectsAndFullTeachingAreRequired() {
  val p=pack();val o=obj(p);assertEquals(listOf("u4-i-nom-role-train","u4-i-nom-role-student"),o.checkItemIds)
  val s=p.unit(4)!!.sessions.single{it.id=="u4-s-nominative"};val t=s.steps.filter{it.kind=="TEACH"};val r=s.steps.single{it.id=="u4-st-rem-nominative"};assertEquals(3,t.size);assertEquals(3,s.steps.takeWhile{it.kind!="GUIDED"}.count{it.kind=="TEACH"});assertEquals(t.joinToString("\n\n"){it.body},r.body);assertEquals(o.exampleIds,r.exampleIds);assertEquals(3,r.exampleIds.size)
  assertTrue(r.body.contains("not every nominative noun is a subject"));assertTrue(r.body.contains("not unseen evidence"));assertEquals(listOf("Поезд старый","Студент новый"),o.checkItemIds.map{p.item(it)!!.promptLetter});assertTrue(o.checkItemIds.all{V2Validator.featureValid(p,p.item(it)!!)})
  val v=V2Validator.validate(p);assertTrue(v.errors.joinToString("\n"),v.ok)
 }
 @Test fun coordinatedRoleKeyAndCanonicalTupleHashReversalIsRejected() {
  val p=pack();for(id in listOf("u4-g-nom-role","u4-i-nom-role-train","u4-i-nom-role-student")){val i=p.item(id)!!;val a=i.assessment!!;val wrong=i.options[1];val flip=i.copy(answer=wrong,assessment=a.copy(relationParticipantIds=listOf(a.relationParticipantIds[0],a.relationParticipantIds[2],a.relationParticipantIds[1]),answerSourceIds=listOf(a.relationParticipantIds[2]),distractorSourceIds=listOf(a.relationParticipantIds[1],"")))
   val records=p.provenanceRecords.map{r->if(r.sourceRecordId=="ca-rule-context:$id"){val apps=r.synthesis!!.applications.map{it.copy(answer=wrong,distractors=listOf(i.answer))};val field:(String)->String={s->s.toByteArray().size.toString()+":"+s};val tuple="tuple-v1:1;"+field(i.promptLetter!!)+field(wrong)+"1;"+field(i.answer);val text=r.originalSourceText.substringBeforeLast("\nTuple-v1:")+"\nTuple-v1:"+tuple;r.copy(originalSourceText=text,sourceHash=sha(text),synthesis=r.synthesis.copy(applications=apps))}else r}
   val v=V2Validator.validate(p.copy(items=p.items.map{if(it.id==id)flip else it},provenanceRecords=records));assertFalse(id,v.ok);assertTrue(v.errors.joinToString("\n"),v.errors.any{it.contains("frozen")})
  }
 }
 @Test fun wrongCaseRoleShowsTargetedFeedbackAndReturnsToEveryExample() {
  val p=pack();val i=p.item("u4-i-nom-role-student")!!;val step=p.unit(4)!!.sessions.flatMap{it.steps}.single{i.id in it.itemIds};val owner=p.step(step.id)!!
  LocalStore.openInMemory(context()).use{s->s.v2SaveProfile(s.v2Profile().copy(onboardingCompleted=true,currentUnitNumber=4,startingPoint="RESUME_FROM_BOOK",currentSessionId=owner.first.id,currentStepIndex=owner.first.steps.indexOf(step)).verifiedResume(p));compose.setContent{V2App(store=s,pack=p,onRetryOpen={})};compose.onNodeWithTag("v2-continue").performClick();compose.onNodeWithTag("v2-option-${i.id}-${i.options[1]}").performScrollTo().performClick();compose.onNodeWithTag("v2-item-feedback-${i.id}").performScrollTo().assertTextEquals("Not the reviewed answer. ${i.feedback}");assertFalse(i.id in creditItemIds(p,s.v2LearnerView()));compose.onNodeWithTag("v2-remediation-${i.id}").performScrollTo().performClick();compose.onNodeWithTag("v2-step-title").assertTextEquals("Review the supplied nominative subjects");compose.onNodeWithTag("v2-example-ex-u4-nom-student").performScrollTo().assertIsDisplayed()}
 }
 private fun obj(p:V2CurriculumPack)=p.unit(4)!!.objectives.single{it.id=="obj-u4-nominative"}
 private fun record(p:V2CurriculumPack,s:LocalStore,ids:List<String>){for(id in ids){val t=p.unit(4)!!.sessions.flatMap{it.steps}.first{id in it.itemIds};s.v2RecordGovernedAttempt(prepareGovernedAttempt(p,id,p.item(id)!!.answer,PRESCRIBED_CHECK,t.id,"2026-10-14T00:00")!!)}}
 private fun context():Context=ApplicationProvider.getApplicationContext()
 private fun root()=File(if(File("app").isDirectory)"." else "..")
 private fun pack()=V2PackLoader.parse(File(root(),"app/src/main/assets/content/v2-curriculum.json").readText())
 private fun sha(s:String)=MessageDigest.getInstance("SHA-256").digest(s.toByteArray()).joinToString(""){"%02x".format(it.toInt() and 255)}
}
