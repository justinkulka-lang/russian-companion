package dev.russiancompanion.v2
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import dev.russiancompanion.v2.ui.V2App
import dev.russiancompanion.v2.ui.labExplanationSteps
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
class Unit24ContextTransferTest {
    @get:Rule val compose=createComposeRule()
    @Test fun completedContextReadingNeedsDistinctGuidedAndIndependentExamples() {
        val p=pack();val o=objective(p)
        LocalStore.openInMemory(context()).use { store ->
            record(p,store,o.practiceStepIds.flatMap { p.step(it)!!.second.itemIds }+o.checkItemIds)
            assertEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS,objectiveState(o,store.v2LearnerView(),p))
            val guided=o.practiceStepIds.flatMap { p.step(it)!!.second.itemIds }.map { p.item(it)!!.promptLetter }
            assertTrue("Independent persuasion evidence must use a different complete source sentence from guided practice",o.checkItemIds.all { p.item(it)!!.promptLetter !in guided })
        }
    }
    @Test fun correctIndependentPersuasionItemsAndOptionalOriginalReadingArePreserved() {
        val p=pack();val old=baseline()
        for(id in listOf("u24-i-word-order-contrast-1","u24-i-word-order-contrast-2"))assertEquals(old.item(id),p.item(id))
        val was=old.item("u24-g-word-order-contrast-0")!!;val now=p.item(was.id)!!
        assertEquals(was.copy(kind="SOURCE_MEANING",objectiveId="obj-u24-persuasion-reference",assessment=null),now)
        assertEquals("В магазин пошёл я.",p.item("u24-g-context-subject-focus")!!.promptLetter)
    }
    @Test fun optionalReferenceKeepsTeachingAndSavedSelectionButCannotCreditOrBlockRequiredChecks() {
        val p=pack();val id="u24-g-word-order-contrast-0";val i=p.item(id)!!
        LocalStore.openInMemory(context()).use { store ->
            store.v2SaveProfile(store.v2Profile().copy(currentUnitNumber=24,startingPoint="RESUME_FROM_BOOK"))
            assertTrue(practiceQueue(p,store.v2LearnerView(),"vocabulary").any { it.id==id && it.kind=="SOURCE_MEANING" })
            assertEquals(1,labExplanationSteps(p,i).size)
            store.v2RecordGovernedAttempt(prepareGovernedAttempt(p,id,i.options[1],OPTIONAL_PRACTICE,practiceStepId("vocabulary"),"2026-10-13T21:00")!!)
            assertNotNull(optionalPracticeSelection(p,store.v2LearnerView(),"vocabulary",id));assertTrue(creditItemIds(p,store.v2LearnerView()).isEmpty())
            val o=objective(p);record(p,store,o.practiceStepIds.flatMap { p.step(it)!!.second.itemIds }+o.checkItemIds)
            assertEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS,objectiveState(o,store.v2LearnerView(),p));assertEquals(4,store.v2Attempts().size)
        }
    }
    @Test fun guidedSubjectFocusAndOneReadingCannotCompleteBothPersuasionRequirements() {
        val p=pack();LocalStore.openInMemory(context()).use { store ->
            record(p,store,listOf("u24-g-context-subject-focus","u24-i-word-order-contrast-1"));assertEquals(2,creditItemIds(p,store.v2LearnerView()).size)
            assertEquals(V2ObjectiveState.REVIEW_REQUIRED,objectiveState(objective(p),store.v2LearnerView(),p))
        }
    }
    @Test fun playerRejectsLackOfNeedInTheNeverAgreeContext() {
        val p=pack();val i=p.item("u24-i-word-order-contrast-2")!!;val step=p.unit(24)!!.sessions.flatMap { it.steps }.first { i.id in it.itemIds };val owner=p.step(step.id)!!
        LocalStore.openInMemory(context()).use { store ->
            store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted=true,currentUnitNumber=24,startingPoint="RESUME_FROM_BOOK",currentSessionId=owner.first.id,currentStepIndex=owner.first.steps.indexOf(step)).verifiedResume(p))
            compose.setContent { V2App(store=store,pack=p,onRetryOpen={}) };compose.onNodeWithTag("v2-continue").performClick()
            compose.onNodeWithTag("v2-option-${i.id}-There is no need to persuade me").performScrollTo().performClick()
            compose.onNodeWithTag("v2-item-feedback-${i.id}").performScrollTo().assertTextEquals("Not the reviewed answer. ${i.feedback}")
            assertFalse(i.id in creditItemIds(p,store.v2LearnerView()))
        }
    }
    @Test fun allThreeCardsPrecedePracticeAndFullRemediationKeepsAmbiguity() {
        val p=pack();val s=p.unit(24)!!.sessions.single { it.id=="u24-s-word-order-contrast" };val teach=s.steps.filter { it.kind=="TEACH" };assertEquals(3,teach.size);assertEquals(3,s.steps.takeWhile { it.kind!="GUIDED" }.count { it.kind=="TEACH" })
        val rem=s.steps.single { it.kind=="REMEDIATE" };assertEquals(teach.joinToString("\n\n") { it.body },rem.body);assertEquals(teach.flatMap { it.exampleIds }.distinct(),rem.exampleIds)
        assertTrue(rem.body.contains("Intonation and discourse context"));assertTrue(teach.last().body.contains("not an independently demonstrated skill"));assertEquals(2,rem.exampleIds.size)
    }
    @Test fun originalRepeatedGuidedContrastCannotBeRestoredAsRequiredPractice() {
        val p=pack();val old=baseline().item("u24-g-word-order-contrast-0")!!
        val u=p.unit(24)!!.copy(sessions=p.unit(24)!!.sessions.map { if(it.id!="u24-s-word-order-contrast")it else it.copy(steps=it.steps.map { s -> if(s.kind=="GUIDED")s.copy(itemIds=listOf(old.id)) else s }) })
        val v=V2Validator.validate(p.copy(units=p.units.map { if(it.number==24)u else it },items=p.items.map { if(it.id==old.id)old else it }));assertFalse(v.ok)
        assertTrue(v.errors.joinToString("\n"),v.errors.any { it.contains("frozen distinct guided context and preserved persuasion chain") })
    }
    @Test fun coordinatedGuidedKeyAndApplicationCannotReverseSubjectAndDestinationFocus() {
        val p=pack();val i=p.item("u24-g-context-subject-focus")!!;val a=i.assessment!!;val ids=a.relationParticipantIds;val wrong=i.options[1];val reasons=listOf(i.distractorRationales[1],"")
        val changed=i.copy(answer=wrong,distractorRationales=reasons,assessment=a.copy(relationParticipantIds=listOf(ids[0],ids[2],ids[1]),answerSourceIds=listOf(ids[2]),distractorSourceIds=listOf(ids[1],""),distractorDifference=reasons))
        val rs=p.provenanceRecords.map { r -> if(r.sourceRecordId!="ca-rule-context:${i.id}")r else r.copy(synthesis=r.synthesis!!.copy(applications=r.synthesis.applications.map { it.copy(answer=wrong,distractors=listOf(i.answer)) })) }
        val v=V2Validator.validate(p.copy(items=p.items.map { if(it.id==i.id)changed else it },provenanceRecords=rs));assertFalse(v.ok);assertTrue(v.errors.joinToString("\n"),v.errors.any { it.contains("complete rule context") })
    }
    @Test fun coherentNativeHashForgeryCannotChangeTheGuidedSource() {
        val p=pack();val rs=p.provenanceRecords.map { r -> if(r.itemId!="prov-u24-context-transfer-native-guide-ru")r else { val text=r.originalSourceText.replace("В магазин пошёл я.","В магазин я пошёл.");r.copy(originalSourceText=text,sourceHash=sha(text),shippedText="В магазин я пошёл.",contentHash=sha("В магазин я пошёл.")) } }
        val v=V2Validator.validate(p.copy(provenanceRecords=rs));assertFalse(v.ok);assertTrue(v.errors.joinToString("\n"),v.errors.any { it.contains("frozen distinct guided context and preserved persuasion chain") })
    }
    @Test fun coordinatedOriginalPersuasionMeaningSwapCannotReverseTheFrozenEvidence() {
        val p=pack();val i=p.item("u24-i-word-order-contrast-2")!!;val a=i.assessment!!;val wrong=i.options[1];val reasons=listOf(i.distractorRationales[1],"")
        val changed=i.copy(answer=wrong,distractorRationales=reasons,assessment=a.copy(distractorSourceIds=listOf(a.answerSourceIds.single(),""),distractorDifference=reasons))
        val rs=p.provenanceRecords.map { r -> if(r.itemId !in setOf("prov-ex-u24-word-order-contrast-0-en","prov-ex-u24-word-order-contrast-1-en"))r else {
            val text=if(r.itemId.endsWith("0-en"))wrong else i.answer;r.copy(shippedText=text,contentHash=sha(text))
        } }
        assertTrue(rs.zip(p.provenanceRecords).all { (x,y)->x.sourceHash==y.sourceHash && x.originalSourceText==y.originalSourceText })
        val v=V2Validator.validate(p.copy(items=p.items.map { if(it.id==i.id)changed else it },provenanceRecords=rs));assertFalse(v.ok)
        assertTrue(v.errors.joinToString("\n"),v.errors.any { it.contains("frozen distinct guided context and preserved persuasion chain") })
    }
    @Test fun historicalThreeAnswerCompletionStaysSavedButCannotCreditCurrentContent() {
        val p=pack();val old=baseline();val o=objective(old);LocalStore.openInMemory(context()).use { store ->
            record(old,store,o.practiceStepIds.flatMap { old.step(it)!!.second.itemIds }+o.checkItemIds);assertEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS,objectiveState(o,store.v2LearnerView(),old));assertEquals(3,store.v2Attempts().size)
            assertTrue(creditItemIds(p,store.v2LearnerView()).isEmpty());assertNotEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS,objectiveState(objective(p),store.v2LearnerView(),p))
        }
    }
    @Test fun exactSelectionsAndAllOldExamplesValidateWithoutIndependentSubjectFocusClaim() {
        val p=pack();val old=baseline();assertEquals(old.examples,p.examples);val rs=p.provenanceRecords.filter { it.itemId.startsWith("prov-u24-context-transfer-native-") };assertEquals(8,rs.size)
        val source=File(root(),"catalog/sources/units-26-30/wikipedia/russian-grammar-1369617627.txt").readText();rs.forEach { assertTrue(it.originalSourceText in source);assertTrue(it.shippedText in V2Validator.unfoldWikiLabels(it.originalSourceText));assertEquals(sha(it.originalSourceText),it.sourceHash) }
        val ref=p.unit(24)!!.objectives.single { it.id=="obj-u24-persuasion-reference" };assertFalse(ref.gating);assertEquals("GUIDANCE",ref.kind)
        val v=V2Validator.validate(p);assertTrue(v.errors.joinToString("\n"),v.ok)
    }
    private fun record(p:V2CurriculumPack,store:LocalStore,ids:List<String>) {
        for(id in ids) { val step=p.unit(24)!!.sessions.flatMap { it.steps }.first { id in it.itemIds };store.v2RecordGovernedAttempt(prepareGovernedAttempt(p,id,p.item(id)!!.answer,PRESCRIBED_CHECK,step.id,"2026-10-13T21:00")!!) }
    }
    private fun baseline():V2CurriculumPack { val process=ProcessBuilder("git","show","3b8b6df0150d406f8f71a2bc7eebd1a19a301eba:app/src/main/assets/content/v2-curriculum.json").directory(root()).start();val p=V2PackLoader.parse(process.inputStream.bufferedReader().readText());assertEquals(0,process.waitFor());return p }
    private fun objective(p:V2CurriculumPack)=p.unit(24)!!.objectives.single { it.id=="obj-u24-word-order-contrast" }
    private fun sha(s:String)=java.security.MessageDigest.getInstance("SHA-256").digest(s.toByteArray(Charsets.UTF_8)).joinToString(""){"%02x".format(it.toInt() and 255)}
    private fun context():Context=ApplicationProvider.getApplicationContext()
    private fun root()=File(if(File("app").isDirectory)"." else "..")
    private fun pack()=V2PackLoader.parse(File(root(),"app/src/main/assets/content/v2-curriculum.json").readText())
}
