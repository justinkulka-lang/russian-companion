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
@RunWith(RobolectricTestRunner::class)
@Config(sdk=[33])
class Unit13ReportedTransferTest {
 @get:Rule val compose=createComposeRule()
 @Test fun sevenCurrentAnswersCompleteBothObjectivesWithoutInventingAnEarlierGuideHistory() {
  val p=pack();val u=p.unit(13)!!;assertTrue(u.sessions.indexOfFirst{it.id=="u13-s-tense-keep"}<u.sessions.indexOfFirst{it.id=="u13-s-reported"})
  LocalStore.openInMemory(context()).use { store ->
   for(o in u.objectives.filter{it.id in listOf("obj-u13-tense-keep","obj-u13-reported")}) {
    record(p,store,o.practiceStepIds.flatMap{p.step(it)!!.second.itemIds}+o.checkItemIds)
    assertEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS,objectiveState(o,store.v2LearnerView(),p))
   }
   assertEquals(7,store.v2Attempts().size);assertEquals(7,creditItemIds(p,store.v2LearnerView()).size)
  }
 }
 @Test fun laterGuideUsesDifferentContextFromEarlierEveningCheck() {
  val p=pack();assertNotEquals(p.item("u13-i-tense-keep-1")!!.promptLetter,p.item("u13-g-reported-0")!!.promptLetter)
  assertEquals("Он сказа́л, что он англича́нин",p.item("u13-g-reported-0")!!.promptLetter)
 }
 @Test fun reportedObjectiveRequiresItsOwnIndependentFrameAndEmbeddedTimeInterpretation() {
  val p=pack();val o=objective(p);assertTrue(o.checkItemIds.any{p.item(it)!!.assessment?.testedFeature=="REPORTED_FRAME_EMBEDDED_TIME"})
  LocalStore.openInMemory(context()).use { store ->
   record(p,store,listOf("u13-g-reported-0","u13-i-reported-1","u13-i-reported-2"));assertNotEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS,objectiveState(o,store.v2LearnerView(),p))
   record(p,store,listOf("u13-i-report-frame-time"));assertEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS,objectiveState(o,store.v2LearnerView(),p))
  }
 }
 @Test fun originalIndependentItemsEarlierObjectiveAndAllExamplesRemainUnchanged() {
  val p=pack();val old=baseline();assertEquals(old.examples,p.examples)
  for(id in listOf("u13-g-tense-keep-0","u13-i-tense-keep-1","u13-i-tense-keep-2","u13-i-reported-1","u13-i-reported-2"))assertEquals(old.item(id),p.item(id))
  assertEquals(old.unit(13)!!.objectives.single{it.id=="obj-u13-tense-keep"},p.unit(13)!!.objectives.single{it.id=="obj-u13-tense-keep"})
  val v=V2Validator.validate(p);assertTrue(v.errors.joinToString("\n"),v.ok)
 }
 @Test fun threeCardsPrecedePracticeAndFullRemediationQualifiesBothLanguages() {
  val p=pack();val s=p.unit(13)!!.sessions.single{it.id=="u13-s-reported"};val teach=s.steps.filter{it.kind=="TEACH"};val rem=s.steps.single{it.kind=="REMEDIATE"}
  assertEquals(3,s.steps.takeWhile{it.kind!="GUIDED"}.count{it.kind=="TEACH"});assertEquals(teach.joinToString("\n\n"){it.body},rem.body);assertEquals(teach.flatMap{it.exampleIds}.distinct(),rem.exampleIds)
  assertTrue(rem.body.contains("not compulsory"));assertTrue(rem.body.contains("not unseen evidence"));assertTrue(rem.body.contains("present moment"));assertTrue(rem.body.contains("not unrestricted"))
 }
 @Test fun playerRejectsReversingPastReportingAndFutureEmbeddedTime() {
  val p=pack();val i=p.item("u13-i-report-frame-time")!!;val step=p.unit(13)!!.sessions.flatMap{it.steps}.single{i.id in it.itemIds};val owner=p.step(step.id)!!
  LocalStore.openInMemory(context()).use { store ->
   store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted=true,currentUnitNumber=13,startingPoint="RESUME_FROM_BOOK",currentSessionId=owner.first.id,currentStepIndex=owner.first.steps.indexOf(step)).verifiedResume(p))
   compose.setContent { V2App(store=store,pack=p,onRetryOpen={}) };compose.onNodeWithTag("v2-continue").performClick()
   // The existing direct/Moscow checks share this step; the final item remains reachable.
   compose.onNodeWithTag("v2-option-${i.id}-${i.options[1]}").performScrollTo().performClick()
   compose.onNodeWithTag("v2-item-feedback-${i.id}").performScrollTo().assertTextEquals("Not the reviewed answer. ${i.feedback}")
   assertFalse(i.id in creditItemIds(p,store.v2LearnerView()))
  }
 }
 @Test fun originalRepeatedGuideCannotRestoreTheOldReportedChain() {
  val p=pack();val old=baseline().item("u13-g-reported-0")!!;val v=V2Validator.validate(p.copy(items=p.items.map{if(it.id==old.id)old else it}));assertFalse(v.ok);assertTrue(v.errors.joinToString("\n"),v.errors.any{it.contains("frozen supplied reported-context")})
 }
 @Test fun coherentNativeContextAndHashForgeryCannotIntroducePastNationality() {
  val p=pack();val id="prov-u13-report-transfer-native-nationality"
  val rs=p.provenanceRecords.map{r->if(r.itemId!=id)r else {val s=r.originalSourceText.replace("Он сказа́л, что он англича́нин","Он сказа́л, что он был англича́нином");val text="Он сказа́л, что он был англича́нином";r.copy(originalSourceText=s,sourceHash=sha(s),shippedText=text,contentHash=sha(text))}}
  val v=V2Validator.validate(p.copy(provenanceRecords=rs));assertFalse(v.ok);assertTrue(v.errors.joinToString("\n"),v.errors.any{it.contains("frozen supplied reported-context")})
 }
 @Test fun coordinatedKeyAndApplicationCannotReverseTheReportedFiniteTimes() {
  val p=pack();val id="u13-i-report-frame-time";val i=p.item(id)!!;val a=i.assessment!!;val ids=a.relationParticipantIds;val wrong=i.options[1];val reasons=listOf(i.distractorRationales[1],"")
  val forged=i.copy(answer=wrong,distractorRationales=reasons,assessment=a.copy(relationParticipantIds=listOf(ids[0],ids[2],ids[1]),answerSourceIds=listOf(ids[2]),distractorSourceIds=listOf(ids[1],""),distractorDifference=reasons))
  val rs=p.provenanceRecords.map{r->if(r.sourceRecordId!="ca-rule-context:$id")r else r.copy(synthesis=r.synthesis!!.copy(applications=r.synthesis.applications.map{it.copy(answer=wrong,distractors=listOf(i.answer))}))}
  val v=V2Validator.validate(p.copy(items=p.items.map{if(it.id==id)forged else it},provenanceRecords=rs));assertFalse(v.ok);assertTrue(v.errors.joinToString("\n"),v.errors.any{it.contains("complete rule context")})
 }
 @Test fun authenticSixAnswerHistoryStaysSavedButCannotCreditTheRenewedPack() {
  val p=pack();val old=baseline();LocalStore.openInMemory(context()).use { store ->
   for(o in old.unit(13)!!.objectives.filter{it.id in listOf("obj-u13-tense-keep","obj-u13-reported")}) {record(old,store,o.practiceStepIds.flatMap{old.step(it)!!.second.itemIds}+o.checkItemIds);assertEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS,objectiveState(o,store.v2LearnerView(),old))}
   assertEquals(6,store.v2Attempts().size);assertTrue(creditItemIds(p,store.v2LearnerView()).isEmpty());assertNotEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS,objectiveState(objective(p),store.v2LearnerView(),p))
  }
 }
 private fun record(p:V2CurriculumPack,store:LocalStore,ids:List<String>) {for(id in ids){val step=p.unit(13)!!.sessions.flatMap{it.steps}.single{id in it.itemIds};store.v2RecordGovernedAttempt(prepareGovernedAttempt(p,id,p.item(id)!!.answer,PRESCRIBED_CHECK,step.id,"2026-10-13T23:00")!!)}}
 private fun objective(p:V2CurriculumPack)=p.unit(13)!!.objectives.single{it.id=="obj-u13-reported"}
 private fun baseline():V2CurriculumPack {val proc=ProcessBuilder("git","show","d7d84571fa8374c91561e1cdfde52d43ff79a787:app/src/main/assets/content/v2-curriculum.json").directory(File(if(File("app").isDirectory)"." else "..")).start();val p=V2PackLoader.parse(proc.inputStream.bufferedReader().readText());assertEquals(0,proc.waitFor());return p}
 private fun context():Context=ApplicationProvider.getApplicationContext()
 private fun sha(s:String)=java.security.MessageDigest.getInstance("SHA-256").digest(s.toByteArray(Charsets.UTF_8)).joinToString(""){"%02x".format(it.toInt() and 255)}
 private fun pack()=V2PackLoader.parse(File(if(File("app").isDirectory)"." else "..","app/src/main/assets/content/v2-curriculum.json").readText())
}
