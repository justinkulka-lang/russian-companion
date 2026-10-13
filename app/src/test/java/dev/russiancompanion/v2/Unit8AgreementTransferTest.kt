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
class Unit8AgreementTransferTest {
    @get:Rule val compose=createComposeRule()
    @Test fun completedAgreementNeedsADifferentIndependentAdjective() {
        val p=pack();val o=p.unit(8)!!.objectives.single { it.id=="obj-u8-agreement" }
        LocalStore.openInMemory(context()).use { store ->
            for(id in o.practiceStepIds.flatMap { p.step(it)!!.second.itemIds }+o.checkItemIds) {
                val step=p.unit(8)!!.sessions.flatMap { it.steps }.first { id in it.itemIds }
                store.v2RecordGovernedAttempt(prepareGovernedAttempt(p,id,p.item(id)!!.answer,PRESCRIBED_CHECK,step.id,"2026-10-13T20:00")!!)
            }
            assertEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS,objectiveState(o,store.v2LearnerView(),p))
            val guided=o.practiceStepIds.flatMap { p.step(it)!!.second.itemIds }.map { p.item(it)!!.promptLetter }
            assertTrue("Independent adjective evidence must use a different supplied adjective, not repeat the guided form pair",
                p.item("u8-i-agreement-3")!!.promptLetter !in guided)
        }
    }

    @Test fun guidedAndOtherCorrectPairsCannotCompleteTheIndependentAdjective() {
        val p=pack();val o=objective(p)
        LocalStore.openInMemory(context()).use { store ->
            record(p,store,listOf("u8-g-agreement-0","u8-i-agreement-1","u8-i-agreement-2"))
            assertEquals(3,creditItemIds(p,store.v2LearnerView()).size)
            assertEquals(V2ObjectiveState.REVIEW_REQUIRED,objectiveState(o,store.v2LearnerView(),p))
        }
    }
    @Test fun playerRejectsTheSingularBlueFormAndExplainsTheNumber() {
        val p=pack();val item=p.item("u8-i-agreement-3")!!;val step=p.unit(8)!!.sessions.flatMap { it.steps }.first { item.id in it.itemIds };val owner=p.step(step.id)!!
        LocalStore.openInMemory(context()).use { store ->
            store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted=true,currentUnitNumber=8,startingPoint="RESUME_FROM_BOOK",
                currentSessionId=owner.first.id,currentStepIndex=owner.first.steps.indexOf(step)).verifiedResume(p))
            compose.setContent { V2App(store=store,pack=p,onRetryOpen={}) }
            compose.onNodeWithTag("v2-continue").performClick()
            compose.onNodeWithTag("v2-option-${item.id}-синий").performScrollTo().performClick()
            compose.onNodeWithTag("v2-item-feedback-${item.id}").performScrollTo().assertTextEquals("Not the reviewed answer. ${item.feedback}")
            assertFalse(item.id in creditItemIds(p,store.v2LearnerView()))
        }
    }
    @Test fun allSixCardsPrecedePracticeAndRemediationRestoresThem() {
        val p=pack();val session=p.unit(8)!!.sessions.single { it.id=="u8-s-agreement" };val teach=session.steps.filter { it.kind=="TEACH" }
        assertEquals(6,teach.size);assertEquals(6,session.steps.takeWhile { it.kind!="GUIDED" }.count { it.kind=="TEACH" })
        val rem=session.steps.single { it.kind=="REMEDIATE" };assertEquals(teach.joinToString("\n\n") { it.body },rem.body)
        assertEquals(teach.flatMap { it.exampleIds }.distinct(),rem.exampleIds)
        assertTrue(teach.last().body.contains("синий"));assertTrue(teach.last().body.contains("синие"))
    }
    @Test fun restoringTheOriginalValidFormSelectionCannotClearDistinctAdjectiveEvidence() {
        val p=pack();val old=baseline().item("u8-i-agreement-3")!!;assertEquals("FORM_SELECTION",old.kind)
        val v=V2Validator.validate(p.copy(items=p.items.map { if(it.id==old.id)old else it }));assertFalse(v.ok)
        assertTrue(v.errors.joinToString("\n"),v.errors.any { it.contains("frozen distinct adjective application chain") })
    }
    @Test fun coordinatedKeyAndApplicationCannotMakeTheSingularAPluralAnswer() {
        val p=pack();val item=p.item("u8-i-agreement-3")!!;val a=item.assessment!!;val ids=a.relationParticipantIds
        val reasons=listOf(item.distractorRationales[1],"");val changed=item.copy(answer="синий",distractorRationales=reasons,assessment=a.copy(
            relationParticipantIds=listOf(ids[0],ids[2],ids[1]),answerSourceIds=listOf(ids[2]),distractorSourceIds=listOf(ids[1],""),distractorDifference=reasons))
        val rs=p.provenanceRecords.map { r -> if(r.sourceRecordId!="ca-rule-context:${item.id}")r else r.copy(synthesis=r.synthesis!!.copy(
            applications=r.synthesis.applications.map { it.copy(answer="синий",distractors=listOf("синие")) })) }
        assertTrue(rs.zip(p.provenanceRecords).all { (x,y)->x.sourceHash==y.sourceHash && x.originalSourceText==y.originalSourceText })
        val v=V2Validator.validate(p.copy(items=p.items.map { if(it.id==item.id)changed else it },provenanceRecords=rs));assertFalse(v.ok)
        assertTrue(v.errors.joinToString("\n"),v.errors.any { it.contains("complete rule context") })
    }
    @Test fun coherentNativeTextAndHashForgeryCannotChangeTheBlueSource() {
        val p=pack();val rs=p.provenanceRecords.map { r -> if(r.itemId!="prov-u8-agreement-transfer-native-plural")r else {
            val text=r.originalSourceText.replace("синие книги","синий книги");r.copy(originalSourceText=text,sourceHash=sha(text),shippedText="синий",contentHash=sha("синий"))
        } }
        val v=V2Validator.validate(p.copy(provenanceRecords=rs));assertFalse(v.ok)
        assertTrue(v.errors.joinToString("\n"),v.errors.any { it.contains("frozen distinct adjective application chain") })
    }
    @Test fun historicalFourAnswerCompletionStaysSavedButCannotCreditCurrentContent() {
        val p=pack();val old=baseline();val o=objective(old)
        LocalStore.openInMemory(context()).use { store ->
            record(old,store,o.practiceStepIds.flatMap { old.step(it)!!.second.itemIds }+o.checkItemIds)
            assertEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS,objectiveState(o,store.v2LearnerView(),old));assertEquals(4,store.v2Attempts().size)
            assertTrue(creditItemIds(p,store.v2LearnerView()).isEmpty())
            assertNotEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS,objectiveState(objective(p),store.v2LearnerView(),p))
        }
    }
    @Test fun unaccentedSourcePairAndOtherCorrectFieldsArePreserved() {
        val p=pack();val old=baseline();for(id in listOf("u8-g-agreement-0","u8-i-agreement-1","u8-i-agreement-2"))assertEquals(old.item(id),p.item(id))
        assertEquals("синий",p.item("u8-i-agreement-3")!!.promptLetter);assertEquals("синие",p.item("u8-i-agreement-3")!!.answer)
        val rs=p.provenanceRecords.filter { it.itemId.startsWith("prov-u8-agreement-transfer-native-") };assertEquals(2,rs.size)
        val source=File(root(),"catalog/sources/units-6-10/wikibooks/adjectives-4292379.txt").readText()
        rs.forEach { assertTrue(it.originalSourceText in source);assertTrue(it.shippedText in V2Validator.unfoldWikiLabels(it.originalSourceText));assertFalse(it.shippedText.contains('\u0301')) }
        val v=V2Validator.validate(p);assertTrue(v.errors.joinToString("\n"),v.ok)
    }
    private fun record(p:V2CurriculumPack,store:LocalStore,ids:List<String>) {
        for(id in ids) { val step=p.unit(8)!!.sessions.flatMap { it.steps }.first { id in it.itemIds }
            store.v2RecordGovernedAttempt(prepareGovernedAttempt(p,id,p.item(id)!!.answer,PRESCRIBED_CHECK,step.id,"2026-10-13T20:00")!!) }
    }
    private fun baseline():V2CurriculumPack {
        val proc=ProcessBuilder("git","show","2f06e472876d5248a403b8f523305918afca65fa:app/src/main/assets/content/v2-curriculum.json").directory(root()).start()
        val p=V2PackLoader.parse(proc.inputStream.bufferedReader().readText());assertEquals(0,proc.waitFor());return p
    }
    private fun objective(p:V2CurriculumPack)=p.unit(8)!!.objectives.single { it.id=="obj-u8-agreement" }
    private fun sha(s:String)=java.security.MessageDigest.getInstance("SHA-256").digest(s.toByteArray(Charsets.UTF_8)).joinToString(""){"%02x".format(it.toInt() and 255)}
    private fun context():Context=ApplicationProvider.getApplicationContext()
    private fun root()=File(if(File("app").isDirectory)"." else "..")
    private fun pack()=V2PackLoader.parse(File(root(),"app/src/main/assets/content/v2-curriculum.json").readText())
}
