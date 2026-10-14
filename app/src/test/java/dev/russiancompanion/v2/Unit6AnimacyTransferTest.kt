package dev.russiancompanion.v2
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import dev.russiancompanion.v2.ui.V2App
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.curriculum.*
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
class Unit6AnimacyTransferTest {
 @get:Rule val compose=createComposeRule()
 @Test fun completeTeachingPrecedesPracticeAndRemediationRestoresElevenExamples() {
  val p=pack();val s=p.unit(6)!!.sessions.single{it.id=="u6-s-animacy"};val t=s.steps.filter{it.kind=="TEACH"};val r=s.steps.single{it.kind=="REMEDIATE"}
  assertEquals(6,s.steps.takeWhile{it.kind!="GUIDED"}.count{it.kind=="TEACH"});assertEquals(t.joinToString("\n\n"){it.body},r.body);assertEquals(t.flatMap{it.exampleIds}.distinct(),r.exampleIds);assertEquals(11,r.exampleIds.size)
  assertTrue(r.body.contains("Matching a genitive form does not change the accusative"));assertTrue(r.body.contains("does not establish completed action"));assertTrue(r.body.contains("not unseen or unrestricted productive mastery"))
  val v=V2Validator.validate(p);assertTrue(v.errors.joinToString("\n"),v.ok)
 }
 @Test fun onlyDuplicateChangesAndAllPriorNativeAndProvenanceRemainWhole() {
  val p=pack();val old=baseline();assertEquals(old.items.filter{it.id!="u6-i-animacy-3"},p.items.filter{it.id!="u6-i-animacy-3"});assertEquals(old.sourceCapabilities,p.sourceCapabilities)
  assertEquals(old.examples,p.examples.filter{it.id!="ex-u6-animacy-transfer-chair"});val records=p.provenanceRecords.associateBy{it.itemId};old.provenanceRecords.forEach{assertEquals(it,records[it.itemId])};assertEquals(25,p.provenanceRecords.size-old.provenanceRecords.size)
  assertEquals(old.units.filter{it.number!=6},p.units.filter{it.number!=6});assertEquals(old.unit(6)!!.objectives.filter{it.id!="obj-u6-animacy"},p.unit(6)!!.objectives.filter{it.id!="obj-u6-animacy"});assertEquals(old.unit(6)!!.sessions.filter{it.id!="u6-s-animacy"},p.unit(6)!!.sessions.filter{it.id!="u6-s-animacy"})
 }
 @Test fun teacherAndFriendAnswersCannotReplaceRequiredInanimateClassification() {
  val p=pack();val o=obj(p);LocalStore.openInMemory(context()).use { store ->
   record(p,store,listOf("u6-g-animacy-0","u6-i-animacy-1","u6-i-animacy-2"));assertNotEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS,objectiveState(o,store.v2LearnerView(),p));val i=p.item("u6-i-animacy-3")!!
   val step=p.unit(6)!!.sessions.flatMap{it.steps}.single{i.id in it.itemIds};store.v2RecordGovernedAttempt(prepareGovernedAttempt(p,i.id,i.options[1],PRESCRIBED_CHECK,step.id,"2026-10-14T00:00")!!);assertFalse(i.id in creditItemIds(p,store.v2LearnerView()))
   record(p,store,listOf(i.id));assertEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS,objectiveState(o,store.v2LearnerView(),p));assertEquals(4,creditItemIds(p,store.v2LearnerView()).size)
  }
 }
 @Test fun restoredDuplicateAndCoherentNativeForgeryAreRejected() {
  val p=pack();val old=baseline().item("u6-i-animacy-3")!!;assertFalse(V2Validator.validate(p.copy(items=p.items.map{if(it.id==old.id)old else it})).ok)
  val r=p.provenanceRecords.single{it.itemId=="prov-u6-animacy-transfer-native-chair"};val text=r.originalSourceText.replace("стул", "кот");val forged=r.copy(originalSourceText=text,sourceHash=sha(text));assertFalse(V2Validator.validate(p.copy(provenanceRecords=p.provenanceRecords.map{if(it.itemId==r.itemId)forged else it})).ok)
 }
 @Test fun coordinatedKeyAndApplicationReversalCannotRedefineInanimateCase() {
  val p=pack();val i=p.item("u6-i-animacy-3")!!;val wrong=i.options[1];val a=i.assessment!!;val flipped=i.copy(answer=wrong,assessment=a.copy(answerSourceIds=listOf(a.relationParticipantIds[2]),distractorSourceIds=listOf(a.relationParticipantIds[1],"")))
  val changed=p.copy(items=p.items.map{if(it.id==i.id)flipped else it},provenanceRecords=p.provenanceRecords.map{r->if(r.sourceRecordId=="ca-rule-context:${i.id}")r.copy(synthesis=r.synthesis!!.copy(applications=r.synthesis.applications.map{it.copy(answer=wrong,distractors=listOf(i.answer))}))else r})
  assertFalse(V2Validator.validate(changed).ok)
 }
 @Test fun authenticOldFourAnswerHistoryIsPreservedButStaleAfterRenewal() {
  val old=baseline();val p=pack();LocalStore.openInMemory(context()).use { store ->
   record(old,store,listOf("u6-g-animacy-0","u6-i-animacy-1","u6-i-animacy-2","u6-i-animacy-3"));assertEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS,objectiveState(obj(old),store.v2LearnerView(),old));assertTrue(creditItemIds(p,store.v2LearnerView()).isEmpty());assertEquals(4,store.v2Attempts().size)
  }
 }
 @Test fun wrongInanimateCaseDisplaysFeedbackAndFullRemediation() {
  val p=pack();val i=p.item("u6-i-animacy-3")!!;val step=p.unit(6)!!.sessions.flatMap{it.steps}.single{i.id in it.itemIds};val owner=p.step(step.id)!!
  LocalStore.openInMemory(context()).use { store ->
   store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted=true,currentUnitNumber=6,startingPoint="RESUME_FROM_BOOK",currentSessionId=owner.first.id,currentStepIndex=owner.first.steps.indexOf(step)).verifiedResume(p));compose.setContent{V2App(store=store,pack=p,onRetryOpen={})};compose.onNodeWithTag("v2-continue").performClick()
   compose.onNodeWithTag("v2-option-${i.id}-${i.options[1]}").performScrollTo().performClick();compose.onNodeWithTag("v2-item-feedback-${i.id}").performScrollTo().assertTextEquals("Not the reviewed answer. ${i.feedback}");assertFalse(i.id in creditItemIds(p,store.v2LearnerView()))
   compose.onNodeWithTag("v2-remediation-${i.id}").performScrollTo().performClick();compose.onNodeWithTag("v2-step-title").assertTextEquals("Review animate pairs and the inanimate chair");compose.onNodeWithTag("v2-example-ex-u6-animacy-transfer-chair").performScrollTo().assertIsDisplayed()
  }
 }
 private fun obj(p:V2CurriculumPack)=p.unit(6)!!.objectives.single{it.id=="obj-u6-animacy"}
 private fun record(p:V2CurriculumPack,s:LocalStore,ids:List<String>){for(id in ids){val step=p.unit(6)!!.sessions.flatMap{it.steps}.single{id in it.itemIds};s.v2RecordGovernedAttempt(prepareGovernedAttempt(p,id,p.item(id)!!.answer,PRESCRIBED_CHECK,step.id,"2026-10-14T00:00")!!)}}
 private fun root()=File(if(File("app").isDirectory)"." else "..")
 private fun pack()=V2PackLoader.parse(File(root(),"app/src/main/assets/content/v2-curriculum.json").readText())
 private fun baseline():V2CurriculumPack{val x=ProcessBuilder("git","show","550049c63a590c70c0d11cd47258452a1b03c50a:app/src/main/assets/content/v2-curriculum.json").directory(root()).start();val p=V2PackLoader.parse(x.inputStream.bufferedReader().readText());assertEquals(0,x.waitFor());return p}
 private fun context():Context=ApplicationProvider.getApplicationContext()
 private fun sha(s:String)=MessageDigest.getInstance("SHA-256").digest(s.toByteArray()).joinToString(""){"%02x".format(it.toInt() and 255)}
}
