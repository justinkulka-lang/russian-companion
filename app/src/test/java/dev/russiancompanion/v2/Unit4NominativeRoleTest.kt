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
 @Test fun correctGlossesStayOptionalAndCannotReplaceCaseRoleChecks() {
  val p=pack();val old=baseline();for(id in listOf("u4-g-nominative-1","u4-i-nom-muzh","u4-i-nom-poezd")){val a=old.item(id)!!;val b=p.item(id)!!;assertEquals(a.copy(kind="SOURCE_MEANING",objectiveId="obj-u4-nominative-gloss-reference"),b)}
  assertTrue(stepCompletionItemIds(p,"u4-st-optional-nominative-gloss").isEmpty());val ref=p.unit(4)!!.objectives.single{it.id=="obj-u4-nominative-gloss-reference"};assertFalse(ref.gating);assertTrue(ref.checkItemIds.isEmpty() && ref.practiceStepIds.isEmpty())
  LocalStore.openInMemory(context()).use{s->record(p,s,listOf("u4-g-nom-role","u4-i-nom-role-train"));assertNotEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS,objectiveState(obj(p),s.v2LearnerView(),p));record(p,s,listOf("u4-i-nom-role-student"));assertEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS,objectiveState(obj(p),s.v2LearnerView(),p));assertEquals(3,creditItemIds(p,s.v2LearnerView()).size)}
 }
 @Test fun oldTranslationCompletionHistoryIsPreservedButStaleForTheNewConstruct() {
  val p=pack();val old=baseline();LocalStore.openInMemory(context()).use{s->record(old,s,listOf("u4-g-nominative-1","u4-i-nom-muzh","u4-i-nom-poezd"));assertEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS,objectiveState(obj(old),s.v2LearnerView(),old));assertEquals(3,s.v2Attempts().size);assertTrue(creditItemIds(p,s.v2LearnerView()).isEmpty());assertNotEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS,objectiveState(obj(p),s.v2LearnerView(),p))}
 }
 @Test fun allPriorExamplesRecordsCapabilitiesAndOtherUnitsRemainWhole() {
  val p=pack();val old=baseline();assertEquals(old.examples,p.examples.filter{e->old.examples.any{it.id==e.id}});val records=p.provenanceRecords.associateBy{it.itemId};old.provenanceRecords.forEach{assertEquals(it,records[it.itemId])};assertEquals(44,p.provenanceRecords.size-old.provenanceRecords.size);assertEquals(old.sourceCapabilities,p.sourceCapabilities);old.units.filter{it.number!=4}.forEach{u->val expected=if(u.number==3)u.copy(advancement=u.advancement!!.copy(requireDueReview=true,cumulativeReviewPolicyId=Unit3CumulativeReviewPolicy.id))else u;assertEquals(expected,p.unit(u.number))};assertEquals(old.items.filter{!it.objectiveId.startsWith("obj-u4-nominative")},p.items.filter{!it.objectiveId.startsWith("obj-u4-nominative")});assertEquals(old.unit(4)!!.advancement,p.unit(4)!!.advancement)
 }
 @Test fun legacyGlossRestorationAndCoherentNativeForgeryCannotBypassTheRoleGuard() {
  val p=pack();val old=baseline();val restored=p.copy(units=p.units.map{if(it.number==4)it.copy(objectives=it.objectives.map{if(it.id==obj(p).id)obj(old) else it})else it});assertFalse(V2Validator.validate(restored).ok)
  val r=p.provenanceRecords.single{it.itemId=="prov-u4-role-native-student"};val text=r.originalSourceText.replace("Студент новый","Муж говорит");val bad=p.copy(provenanceRecords=p.provenanceRecords.map{if(it.itemId==r.itemId)r.copy(originalSourceText=text,sourceHash=sha(text))else it});assertFalse(V2Validator.validate(bad).ok)
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
 private fun baseline():V2CurriculumPack{val x=ProcessBuilder("git","show","b5610846cae953c3655466794c83cdcb4063ac4e:app/src/main/assets/content/v2-curriculum.json").directory(root()).start();val p=V2PackLoader.parse(x.inputStream.bufferedReader().readText());assertEquals(0,x.waitFor());return p}
 private fun sha(s:String)=MessageDigest.getInstance("SHA-256").digest(s.toByteArray()).joinToString(""){"%02x".format(it.toInt() and 255)}
}
