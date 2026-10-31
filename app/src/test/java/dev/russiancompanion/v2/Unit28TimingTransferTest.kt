package dev.russiancompanion.v2
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.curriculum.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.Rule
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import dev.russiancompanion.v2.ui.V2App
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
/** Source-native guided timing and preserved original independent coverage. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk=[33])
class Unit28TimingTransferTest {
    @get:Rule val compose=createComposeRule()
    @Test fun originalSixGovernedAnswersCompleteBothTimingObjectives() {
        val p=pack()
        LocalStore.openInMemory(ApplicationProvider.getApplicationContext<Context>()).use { store ->
            for (obj in objectives(p)) {
                val ids=obj.practiceStepIds.flatMap { p.step(it)!!.second.itemIds }+obj.checkItemIds
                assertEquals(3,ids.size)
                for(id in ids) {
                    val step=p.unit(28)!!.sessions.flatMap { it.steps }.single { id in it.itemIds }
                    val a=prepareGovernedAttempt(p,id,p.item(id)!!.answer,PRESCRIBED_CHECK,step.id,"2026-10-13T23:00")!!
                    store.v2RecordGovernedAttempt(a)
                }
                assertEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS,objectiveState(obj,store.v2LearnerView(),p))
            }
            assertEquals(6,store.v2Attempts().size)
            assertEquals(6,creditItemIds(p,store.v2LearnerView()).size)
        }
    }
    @Test fun imperfectiveTimingNeedsAnIndependentContextDifferentFromGuidedPractice() {
        assertDistinct("obj-u28-imperfective-adverb","u28-i-imperfective-adverb-1")
    }
    @Test fun perfectiveTimingNeedsAnIndependentContextDifferentFromGuidedPractice() {
        assertDistinct("obj-u28-perfective-adverb","u28-i-perfective-adverb-1")
    }
    @Test fun guidedAnswersAloneCannotCompleteEitherObjective() {
        val p=pack();LocalStore.openInMemory(ApplicationProvider.getApplicationContext<Context>()).use { store ->
            for(o in objectives(p)) {
                record(p,store,o.practiceStepIds.flatMap{p.step(it)!!.second.itemIds})
                assertNotEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS,objectiveState(o,store.v2LearnerView(),p))
            }
            assertEquals(2,creditItemIds(p,store.v2LearnerView()).size)
        }
    }
    @Test fun playerRejectsAbsolutePresentInThePastGuidedClause() {
        val p=pack();val i=p.item("u28-g-perfective-adverb-0")!!;val step=p.unit(28)!!.sessions.flatMap{it.steps}.single{i.id in it.itemIds};val owner=p.step(step.id)!!
        LocalStore.openInMemory(ApplicationProvider.getApplicationContext<Context>()).use { store ->
            store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted=true,currentUnitNumber=28,startingPoint="RESUME_FROM_BOOK",currentSessionId=owner.first.id,currentStepIndex=owner.first.steps.indexOf(step)).verifiedResume(p))
            compose.setContent { V2App(store=store,pack=p,onRetryOpen={}) };compose.onNodeWithTag("v2-continue").performClick()
            compose.onNodeWithTag("v2-option-${i.id}-an absolute present label").performScrollTo().performClick()
            compose.onNodeWithTag("v2-item-feedback-${i.id}").performScrollTo().assertTextEquals("Not the reviewed answer. ${i.feedback}")
            assertFalse(i.id in creditItemIds(p,store.v2LearnerView()))
        }
    }
    @Test fun allCardsPrecedePracticeAndFullRemediationRetainsRelativeFutureCoverage() {
        val p=pack()
        for(kind in listOf("imperfective","perfective")) {
            val s=p.unit(28)!!.sessions.single{it.id=="u28-s-$kind-adverb"};val teaching=s.steps.filter{it.kind=="TEACH"};val rem=s.steps.single{it.kind=="REMEDIATE"}
            assertEquals(if(kind=="imperfective")3 else 4,teaching.size)
            assertEquals(teaching.size,s.steps.takeWhile{it.kind!="GUIDED"}.count{it.kind=="TEACH"})
            assertEquals(teaching.joinToString("\n\n"){it.body},rem.body);assertEquals(teaching.flatMap{it.exampleIds}.distinct(),rem.exampleIds)
            assertTrue(rem.body.contains("Павел Сергеич Вязовский"));assertTrue(rem.body.contains("not unseen evidence"))
            if(kind=="perfective")assertTrue(rem.body.contains("does not require the writing to have happened before the present moment"))
        }
    }
    @Test fun originalIndependentScoredFieldsAndEveryExampleRemainUnchanged() {
        val p=pack();val old=baseline();assertEquals(old.examples,p.examples.filter{e->old.examples.any{it.id==e.id}})
        for(o in objectives(old))for(id in o.checkItemIds) {
            val a=old.item(id)!!;val b=p.item(id)!!
            assertEquals(a.copy(assessment=a.assessment!!.copy(relationParticipantIds=b.assessment!!.relationParticipantIds,answerSourceIds=b.assessment.answerSourceIds,distractorSourceIds=b.assessment.distractorSourceIds)),b)
        }
        val v=V2Validator.validate(p);assertTrue(v.errors.joinToString("\n"),v.ok)
    }
    @Test fun originalRepeatedGuidedContextsCannotBeRestored() {
        val p=pack();val old=baseline()
        for(kind in listOf("imperfective","perfective")) {
            val id="u28-g-$kind-adverb-0";val v=V2Validator.validate(p.copy(items=p.items.map{if(it.id==id)old.item(id)!! else it}))
            assertFalse(v.ok);assertTrue(v.errors.joinToString("\n"),v.errors.any{it.contains("frozen distinct guided timing")})
        }
    }
    @Test fun coherentNativeHashForgeryCannotChangeSourceContext() {
        val p=pack();val id="prov-u28-timing-native-perfective-clause"
        val rs=p.provenanceRecords.map{r->if(r.itemId!=id)r else {val span=r.originalSourceText.replace("устав писать", "отдыхал читая");r.copy(originalSourceText=span,sourceHash=sha(span),shippedText="отдыхал читая",contentHash=sha("отдыхал читая"))}}
        val v=V2Validator.validate(p.copy(provenanceRecords=rs));assertFalse(v.ok);assertTrue(v.errors.joinToString("\n"),v.errors.any{it.contains("frozen distinct guided timing")})
    }
    @Test fun coordinatedGuidedKeyAndApplicationCannotReverseAnteriority() {
        val p=pack();val id="u28-g-perfective-adverb-0";val i=p.item(id)!!;val a=i.assessment!!;val wrong=i.options[1];val ids=a.relationParticipantIds;val reasons=listOf(i.distractorRationales[1],"",i.distractorRationales[2])
        val forged=i.copy(answer=wrong,distractorRationales=reasons,assessment=a.copy(relationParticipantIds=listOf(ids[0],ids[2],ids[1],ids[3]),answerSourceIds=listOf(ids[2]),distractorSourceIds=listOf(ids[1],"",ids[3]),distractorDifference=reasons))
        val cap=p.sourceCapabilities.single{it.id=="cap-obj-u28-perfective-adverb"}
        val rs=p.provenanceRecords.map{r->if(r.sourceRecordId!=cap.sourceRecordId || r.sourceHash!=cap.sourceHash)r else r.copy(synthesis=r.synthesis!!.copy(applications=r.synthesis.applications.map{if(it.cue==i.promptLetter)it.copy(answer=wrong,distractors=listOf(i.answer,i.options[2])) else it}))}
        val v=V2Validator.validate(p.copy(items=p.items.map{if(it.id==id)forged else it},provenanceRecords=rs));assertFalse(v.ok);assertTrue(v.errors.joinToString("\n"),v.errors.any{it.contains("exact guided and independent contextual applications")})
    }
    @Test fun historicalSixAnswerCompletionStaysSavedButCannotCreditRenewedContent() {
        val old=baseline();val p=pack();LocalStore.openInMemory(ApplicationProvider.getApplicationContext<Context>()).use { store ->
            for(o in objectives(old)) {
                record(old,store,o.practiceStepIds.flatMap{old.step(it)!!.second.itemIds}+o.checkItemIds)
                assertEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS,objectiveState(o,store.v2LearnerView(),old))
            }
            assertEquals(6,store.v2Attempts().size);assertTrue(creditItemIds(p,store.v2LearnerView()).isEmpty())
            for(o in objectives(p))assertNotEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS,objectiveState(o,store.v2LearnerView(),p))
        }
    }
    @Test fun coherentCapabilitySpanAndHostHashRewriteCannotChangeTheFrozenIntake() {
        val p=pack();val c=p.sourceCapabilities.single{it.id=="cap-obj-u28-perfective-adverb"};val span=c.exactSpan.replace("anterior to a past main action","an absolute present label");val hash=sha(span)
        val rs=p.provenanceRecords.map{r->if(r.sourceRecordId!=c.sourceRecordId || r.sourceHash!=c.sourceHash)r else r.copy(originalSourceText=span,sourceHash=hash,synthesis=r.synthesis!!.copy(applications=r.synthesis.applications.map{if(it.cue=="устав писать ложился на диван")it.copy(answer="an absolute present label") else it}))}
        val v=V2Validator.validate(p.copy(sourceCapabilities=p.sourceCapabilities.map{if(it.id==c.id)it.copy(exactSpan=span,sourceHash=hash) else it},provenanceRecords=rs));assertFalse(v.ok);assertTrue(v.errors.joinToString("\n"),v.errors.any{it.contains("corrected intake identity does not match")})
    }
    @Test fun coherentDictionaryAspectRowForgeryCannotReverseTheSourceClassification() {
        val p=pack();val id="prov-u28-timing-native-imperfective-aspect"
        val rs=p.provenanceRecords.map{r->if(r.itemId!=id)r else {val span=r.originalSourceText.replace("imperfective","perfective");r.copy(originalSourceText=span,sourceHash=sha(span),shippedText="perfective",contentHash=sha("perfective"))}}
        val v=V2Validator.validate(p.copy(provenanceRecords=rs));assertFalse(v.ok);assertTrue(v.errors.joinToString("\n"),v.errors.any{it.contains("frozen distinct guided timing")})
    }
    private fun record(p:V2CurriculumPack,store:LocalStore,ids:List<String>) {
        for(id in ids) { val step=p.unit(28)!!.sessions.flatMap{it.steps}.single{id in it.itemIds};store.v2RecordGovernedAttempt(prepareGovernedAttempt(p,id,p.item(id)!!.answer,PRESCRIBED_CHECK,step.id,"2026-10-13T23:00")!!) }
    }
    private fun baseline():V2CurriculumPack {val process=ProcessBuilder("git","show","74f17e621a4b7ca2d9faf3228ef8f96c53c08c22:app/src/main/assets/content/v2-curriculum.json").directory(File(if(File("app").isDirectory)"." else "..")).start();val p=V2PackLoader.parse(process.inputStream.bufferedReader().readText());assertEquals(0,process.waitFor());return p}
    private fun sha(s:String)=java.security.MessageDigest.getInstance("SHA-256").digest(s.toByteArray(Charsets.UTF_8)).joinToString(""){"%02x".format(it.toInt() and 255)}
    private fun assertDistinct(id:String,check:String) {
        val p=pack();val o=objectives(p).single { it.id==id }
        val guided=o.practiceStepIds.flatMap { p.step(it)!!.second.itemIds }.map { p.item(it)!!.promptLetter }
        assertTrue("IA016: independent timing context must differ from guided practice: $id",p.item(check)!!.promptLetter !in guided)
    }
    private fun objectives(p:V2CurriculumPack)=p.unit(28)!!.objectives.filter { it.id in listOf("obj-u28-imperfective-adverb","obj-u28-perfective-adverb") }
    private fun pack()=V2PackLoader.parse(File(if(File("app").isDirectory)"." else "..","app/src/main/assets/content/v2-curriculum.json").readText())
}
