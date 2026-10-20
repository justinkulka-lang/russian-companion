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
class Unit8PluralInstructionTest {
 @get:Rule val compose=createComposeRule()
 @Test fun everyPluralCardPrecedesPracticeAndFullRemediationRestoresFourteenForms() {
  val p=pack();val s=p.unit(8)!!.sessions.single{it.id=="u8-s-plural"};val t=s.steps.filter{it.kind=="TEACH"};val rem=s.steps.single{it.kind=="REMEDIATE"}
  assertEquals(8,t.size);assertEquals(8,s.steps.takeWhile{it.kind!="GUIDED"}.count{it.kind=="TEACH"});assertEquals(t.joinToString("\n\n"){it.body},rem.body);assertEquals(t.flatMap{it.exampleIds}.distinct(),rem.exampleIds);assertEquals(14,rem.exampleIds.size)
  assertTrue(rem.body.contains("dictionary labels, not plural English translations"));assertTrue(rem.body.contains("retains its dictionary gender"));assertTrue(rem.body.contains("not establish an unrestricted productive plural algorithm"))
 }
 @Test fun unit8ScoredObjectsAndAllPriorNativeExamplesAndProvenanceRemainIdentical() {
  val p=historicalPack("4a67bce3a8c94efd7e63f49d911f91746376ecb2");val old=baseline();assertEquals(old.items.filter{it.objectiveId.startsWith("obj-u8-")},p.items.filter{it.objectiveId.startsWith("obj-u8-")});assertEquals(old.examples,p.examples.filter{e->old.examples.any{it.id==e.id}});assertEquals(old.sourceCapabilities,p.sourceCapabilities)
  val records=p.provenanceRecords.associateBy{it.itemId};old.provenanceRecords.forEach{assertEquals(it,records[it.itemId])};assertEquals(21,p.provenanceRecords.count{it.unitNumber==8}-old.provenanceRecords.count{it.unitNumber==8})
  assertEquals(old.unit(8)!!.sessions.single{it.id=="u8-s-spelling"},p.unit(8)!!.sessions.single{it.id=="u8-s-spelling"})
  // Retain the plural correction's exact historical contract above; bind current instruction below.
  val current=pack().also{V2PackIdentityIndex.warm(it)}
  val objectiveIds=setOf("obj-u8-plural","obj-u8-spelling")
  val sessionIds=setOf("u8-s-plural","u8-s-spelling")
  val historicalUnit=p.unit(8)!!;val currentUnit=current.unit(8)!!
  assertEquals(historicalUnit.objectives.filter{it.id in objectiveIds},currentUnit.objectives.filter{it.id in objectiveIds})
  assertEquals(historicalUnit.sessions.filter{it.id in sessionIds},currentUnit.sessions.filter{it.id in sessionIds})
  assertEquals(p.items.filter{it.objectiveId in objectiveIds},current.items.filter{it.objectiveId in objectiveIds})
  assertEquals(p.provenanceRecords.filter{it.objectiveId in objectiveIds},current.provenanceRecords.filter{it.objectiveId in objectiveIds})
  val exampleIds=(historicalUnit.objectives.filter{it.id in objectiveIds}.flatMap{it.exampleIds}+
   historicalUnit.sessions.filter{it.id in sessionIds}.flatMap{it.steps}.flatMap{it.exampleIds}).toSet()
  assertEquals(p.examples.filter{it.id in exampleIds},current.examples.filter{it.id in exampleIds})
  assertEquals(old.examples,current.examples.filter{e->old.examples.any{it.id==e.id}})
  assertEquals(old.sourceCapabilities,current.sourceCapabilities)
  val currentRecords=current.provenanceRecords.associateBy{it.itemId}
  old.provenanceRecords.forEach{assertEquals(it,currentRecords[it.itemId])}
  // Availability has a separately recorded correction; compare every Unit8 item to that exact source.
  val availabilitySource=historicalPack("fe919656bfa2775b5cc2c22f2496d4258343cfea")
  assertEquals(availabilitySource.items.filter{it.objectiveId.startsWith("obj-u8-")},current.items.filter{it.objectiveId.startsWith("obj-u8-")})
  val v=V2Validator.validate(current);assertTrue(v.errors.joinToString("\n"),v.ok)
 }
 @Test fun earlierSevenAnswerPluralCompletionCannotCreditLaterSpellingReinforcement() {
  val p=pack();val u=p.unit(8)!!;assertTrue(u.sessions.indexOfFirst{it.id=="u8-s-plural"}<u.sessions.indexOfFirst{it.id=="u8-s-spelling"});val o=objective(p,"plural")
  assertEquals(p.item("u8-i-plural-6")!!.promptLetter,p.item("u8-g-spelling-0")!!.promptLetter)
  LocalStore.openInMemory(context()).use { store ->
   record(p,store,o.practiceStepIds.flatMap{p.step(it)!!.second.itemIds}+o.checkItemIds);assertEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS,objectiveState(o,store.v2LearnerView(),p));assertEquals(7,creditItemIds(p,store.v2LearnerView()).size)
   assertFalse("u8-g-spelling-0" in creditItemIds(p,store.v2LearnerView()));assertTrue(objective(p,"spelling").checkItemIds.none{it in creditItemIds(p,store.v2LearnerView())});assertNotEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS,objectiveState(objective(p,"spelling"),store.v2LearnerView(),p))
  }
 }
 @Test fun laterSpellingNeedsItsOwnReverseNounAndAdjectiveEvidence() {
  val p=pack();val o=objective(p,"spelling");LocalStore.openInMemory(context()).use { store ->
   record(p,store,o.practiceStepIds.flatMap{p.step(it)!!.second.itemIds});assertNotEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS,objectiveState(o,store.v2LearnerView(),p))
   assertEquals("кни́га",p.item("u8-i-spelling-1")!!.answer);assertEquals("хоро́шие",p.item("u8-i-spelling-2")!!.answer);assertEquals("хоро́шее",p.item("u8-i-spelling-3")!!.answer);assertEquals("хоро́ший",p.item("u8-i-spelling-4")!!.answer)
   record(p,store,o.checkItemIds);assertEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS,objectiveState(o,store.v2LearnerView(),p))
  }
 }
 @Test fun playerRejectsSingularWordForPluralAndOffersFullPluralRemediation() {
  val p=pack();val i=p.item("u8-i-plural-5")!!;val step=p.unit(8)!!.sessions.flatMap{it.steps}.single{i.id in it.itemIds};val owner=p.step(step.id)!!
  LocalStore.openInMemory(context()).use { store ->
   store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted=true,currentUnitNumber=8,startingPoint="RESUME_FROM_BOOK",currentSessionId=owner.first.id,currentStepIndex=owner.first.steps.indexOf(step)).verifiedResume(p))
   compose.setContent { V2App(store=store,pack=p,onRetryOpen={}) };compose.onNodeWithTag("v2-continue").performClick()
   compose.onNodeWithTag("v2-option-${i.id}-${i.promptLetter}").performScrollTo().performClick();compose.onNodeWithTag("v2-item-feedback-${i.id}").performScrollTo().assertTextEquals("Not the reviewed answer. ${i.feedback}")
   assertFalse(i.id in creditItemIds(p,store.v2LearnerView()))
   compose.onNodeWithTag("v2-remediation-${i.id}").performScrollTo().performClick()
   compose.onNodeWithTag("v2-step-title").assertTextEquals("Review all seven supplied nominative pairs")
   compose.onNodeWithTag("v2-example-ex-or-u8-plural-13").performScrollTo().assertIsDisplayed()
   assertEquals(14,p.unit(8)!!.sessions.single{it.id=="u8-s-plural"}.steps.single{it.kind=="REMEDIATE"}.exampleIds.size)
  }
 }
 @Test fun authenticOldSevenAnswerHistoryIsPreservedButStaleAfterInstructionRenewal() {
  val old=baseline();val p=pack();val o=objective(old,"plural");LocalStore.openInMemory(context()).use { store ->
   record(old,store,o.practiceStepIds.flatMap{old.step(it)!!.second.itemIds}+o.checkItemIds);assertEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS,objectiveState(o,store.v2LearnerView(),old));assertEquals(7,store.v2Attempts().size)
   assertTrue(creditItemIds(p,store.v2LearnerView()).isEmpty());assertNotEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS,objectiveState(objective(p,"plural"),store.v2LearnerView(),p))
  }
 }
 private fun objective(p:V2CurriculumPack,k:String)=p.unit(8)!!.objectives.single{it.id=="obj-u8-$k"}
 private fun record(p:V2CurriculumPack,store:LocalStore,ids:List<String>) {for(id in ids){val step=p.unit(8)!!.sessions.flatMap{it.steps}.single{id in it.itemIds};store.v2RecordGovernedAttempt(prepareGovernedAttempt(p,id,p.item(id)!!.answer,PRESCRIBED_CHECK,step.id,"2026-10-13T23:00")!!)}}
 private fun historicalPack(commit:String):V2CurriculumPack {
  val process=ProcessBuilder("git","show","$commit:app/src/main/assets/content/v2-curriculum.json")
   .directory(File(if(File("app").isDirectory)"." else "..")).start()
  val text=process.inputStream.bufferedReader().readText();assertEquals(0,process.waitFor())
  return V2PackLoader.parse(text).also{V2PackIdentityIndex.warm(it)}
 }
 private fun baseline():V2CurriculumPack {val x=ProcessBuilder("git","show","3812f0e793ac6478a809fe683b303048e6f1c69c:app/src/main/assets/content/v2-curriculum.json").directory(File(if(File("app").isDirectory)"." else "..")).start();val p=V2PackLoader.parse(x.inputStream.bufferedReader().readText());assertEquals(0,x.waitFor());return p}
 private fun context():Context=ApplicationProvider.getApplicationContext()
 private fun pack()=V2PackLoader.parse(File(if(File("app").isDirectory)"." else "..","app/src/main/assets/content/v2-curriculum.json").readText())
}
