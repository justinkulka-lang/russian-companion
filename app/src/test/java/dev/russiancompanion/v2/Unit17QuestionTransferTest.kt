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
class Unit17QuestionTransferTest {
    @get:Rule val compose=createComposeRule()
    @Test fun questionCompletionRequiresIndependentCuesDistinctFromGuidedPractice() {
        val p=pack();val o=p.unit(17)!!.objectives.single { it.id=="obj-u17-clock" }
        LocalStore.openInMemory(context()).use { store ->
            for(id in o.practiceStepIds.flatMap { p.step(it)!!.second.itemIds }+o.checkItemIds) {
                val step=p.unit(17)!!.sessions.flatMap { it.steps }.first { id in it.itemIds }
                store.v2RecordGovernedAttempt(prepareGovernedAttempt(p,id,p.item(id)!!.answer,PRESCRIBED_CHECK,step.id,"2026-10-13T19:00")!!)
            }
            assertEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS,objectiveState(o,store.v2LearnerView(),p))
            val guided=o.practiceStepIds.flatMap { p.step(it)!!.second.itemIds }.map { p.item(it)!!.promptLetter }
            assertTrue("Independent question recognition needs distinct complete native cues, rather than replaying the same guided gloss tuple",
                o.checkItemIds.all { p.item(it)!!.promptLetter !in guided })
        }
    }

    @Test fun optionalOriginalGlossesKeepTeachingAndSavedSelectionsWithoutCredit() {
        val p=pack()
        LocalStore.openInMemory(context()).use { store ->
            store.v2SaveProfile(store.v2Profile().copy(currentUnitNumber=17,startingPoint="RESUME_FROM_BOOK"))
            val queue=practiceQueue(p,store.v2LearnerView(),"vocabulary")
            for(id in listOf("u17-g-clock-0","u17-i-clock-1","u17-i-clock-2")) {
                val i=p.item(id)!!;assertEquals("What time is it?",i.answer)
                assertTrue(queue.any { it.id==id && it.kind=="SOURCE_MEANING" });assertEquals(2,labExplanationSteps(p,i).size)
                store.v2RecordGovernedAttempt(prepareGovernedAttempt(p,id,i.answer,OPTIONAL_PRACTICE,practiceStepId("vocabulary"),"2026-10-13T19:01")!!)
                assertNotNull(optionalPracticeSelection(p,store.v2LearnerView(),"vocabulary",id))
            }
            assertEquals(3,store.v2Attempts().size);assertTrue(creditItemIds(p,store.v2LearnerView()).isEmpty())
        }
    }
    @Test fun guidedAppleChoiceCannotCompleteIndependentQuestionChecks() {
        val p=pack();val o=objective(p)
        LocalStore.openInMemory(context()).use { store ->
            record(p,store,listOf("u17-g-question-apple"))
            assertEquals(1,creditItemIds(p,store.v2LearnerView()).size)
            assertEquals(V2ObjectiveState.REVIEW_REQUIRED,objectiveState(o,store.v2LearnerView(),p))
            assertTrue(currentUnitRequirements(p,p.unit(17)!!,store.v2LearnerView()).remaining.contains("Demonstrate every check for ${o.title}"))
        }
    }
    @Test fun playerRejectsBookSelectionAsTheClockQuestionMeaning() = wrong("u17-i-question-clock","Asks which book the listener wants.")
    @Test fun playerRejectsClockTimeAsTheBookQuestionMeaning() = wrong("u17-i-question-book","Asks for the current clock time.")
    private fun wrong(id:String,answer:String) {
        val p=pack();val item=p.item(id)!!;val step=p.unit(17)!!.sessions.flatMap { it.steps }.first { id in it.itemIds };val owner=p.step(step.id)!!
        LocalStore.openInMemory(context()).use { store ->
            store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted=true,currentUnitNumber=17,startingPoint="RESUME_FROM_BOOK",
                currentSessionId=owner.first.id,currentStepIndex=owner.first.steps.indexOf(step)).verifiedResume(p))
            compose.setContent { V2App(store=store,pack=p,onRetryOpen={}) }
            compose.onNodeWithTag("v2-continue").performClick()
            compose.onNodeWithTag("v2-option-$id-$answer").performScrollTo().performClick()
            compose.onNodeWithTag("v2-item-feedback-$id").performScrollTo().assertTextEquals("Not the reviewed answer. ${item.feedback}")
            assertFalse(id in creditItemIds(p,store.v2LearnerView()))
        }
    }
    @Test fun authenticHistoricalClockCompletionStaysSavedButCannotCreditNewQuestions() {
        val p=pack();val old=baseline();val o=objective(old)
        LocalStore.openInMemory(context()).use { store ->
            record(old,store,o.practiceStepIds.flatMap { old.step(it)!!.second.itemIds }+o.checkItemIds)
            assertEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS,objectiveState(o,store.v2LearnerView(),old));assertEquals(3,store.v2Attempts().size)
            assertTrue(creditItemIds(p,store.v2LearnerView()).isEmpty())
            assertNotEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS,objectiveState(objective(p),store.v2LearnerView(),p))
        }
    }
    @Test fun restoringValidOldContrastItemsCannotClearTheQuestionCapability() {
        val p=pack();val old=baseline();val oldIds=setOf("u17-g-clock-0","u17-i-clock-1","u17-i-clock-2")
        assertTrue(old.items.filter { it.id in oldIds }.all { it.kind=="CONTRAST_SELECTION" })
        val u=p.unit(17)!!.copy(objectives=p.unit(17)!!.objectives.map { if(it.id=="obj-u17-clock")it.copy(checkItemIds=objective(old).checkItemIds) else it },
            sessions=p.unit(17)!!.sessions.map { if(it.id!="u17-s-clock")it else it.copy(steps=it.steps.map { s -> if(s.id=="u17-st-guided-clock-1")s.copy(itemIds=listOf("u17-g-clock-0")) else s }) })
        val result=V2Validator.validate(p.copy(units=p.units.map { if(it.number==17)u else it },items=p.items.map { if(it.id in oldIds)old.item(it.id)!! else it }))
        assertFalse(result.ok);assertTrue(result.errors.joinToString("\n"),result.errors.any { it.contains("cap-u17-clock-question has no reviewable instruction and check chain") })
    }
    @Test fun coordinatedKeyAndApplicationCannotMakeTheClockQuestionAskForABook() {
        val p=pack();val item=p.item("u17-i-question-clock")!!;val a=item.assessment!!;val ids=a.relationParticipantIds;val wrong=item.options[2]
        val reasons=listOf(item.distractorRationales[0],item.distractorRationales[2],"")
        val changed=item.copy(answer=wrong,distractorRationales=reasons,assessment=a.copy(relationParticipantIds=listOf(ids[0],ids[3],ids[2],ids[1]),
            answerSourceIds=listOf(ids[3]),distractorSourceIds=listOf(ids[2],ids[1],""),distractorDifference=reasons))
        val records=p.provenanceRecords.map { r -> if(r.sourceRecordId!="ca-rule-context:${item.id}")r else r.copy(synthesis=r.synthesis!!.copy(
            applications=r.synthesis.applications.map { it.copy(answer=wrong,distractors=listOf(item.options[0],item.answer)) })) }
        assertTrue(records.zip(p.provenanceRecords).all { (x,y)->x.originalSourceText==y.originalSourceText && x.sourceHash==y.sourceHash })
        val result=V2Validator.validate(p.copy(items=p.items.map { if(it.id==item.id)changed else it },provenanceRecords=records))
        assertFalse(result.ok);assertTrue(result.errors.joinToString("\n"),result.errors.any { it.contains("complete rule context") })
    }
    @Test fun coherentNativeTextAndHashForgeryCannotChangeTheAppleQuestion() {
        val p=pack();val records=p.provenanceRecords.map { r -> if(r.itemId!="prov-u17-question-native-apple-ru")r else {
            val text=r.originalSourceText.replace("Которое яблоко моё?","Которое яблоко твоё?");val shipped="Которое яблоко твоё?"
            r.copy(originalSourceText=text,sourceHash=sha(text),shippedText=shipped,contentHash=sha(shipped))
        } }
        val result=V2Validator.validate(p.copy(provenanceRecords=records));assertFalse(result.ok)
        assertTrue(result.errors.joinToString("\n"),result.errors.any { it.contains("frozen distinct question application chain") })
    }
    @Test fun completeNativeQuestionsAndOptionalScopeValidateWithoutProductionClaims() {
        val p=pack();val rs=p.provenanceRecords.filter { it.itemId.startsWith("prov-u17-question-native-") };assertEquals(7,rs.size)
        val raw=File(root(),"catalog/sources/units-3-5/wikibooks/interrogative-4220316.txt").readText()
        rs.forEach { assertTrue(it.originalSourceText in raw);assertTrue(it.shippedText in V2Validator.unfoldWikiLabels(it.originalSourceText));assertEquals(sha(it.originalSourceText),it.sourceHash) }
        assertEquals(setOf("Которое яблоко моё?","Который час?","Которую книгу вы хотите?"),p.items.filter { it.id in setOf("u17-g-question-apple","u17-i-question-clock","u17-i-question-book") }.map { it.promptLetter }.toSet())
        val ref=p.unit(17)!!.objectives.single { it.id=="obj-u17-clock-vocabulary" };assertFalse(ref.gating);assertEquals("GUIDANCE",ref.kind)
        val v=V2Validator.validate(p);assertTrue(v.errors.joinToString("\n"),v.ok)
    }
    private fun record(p:V2CurriculumPack,store:LocalStore,ids:List<String>) {
        for(id in ids) { val step=p.unit(17)!!.sessions.flatMap { it.steps }.first { id in it.itemIds }
            store.v2RecordGovernedAttempt(prepareGovernedAttempt(p,id,p.item(id)!!.answer,PRESCRIBED_CHECK,step.id,"2026-10-13T19:00")!!) }
    }
    private fun baseline():V2CurriculumPack {
        val process=ProcessBuilder("git","show","151cae10b653be9038423e0d19afca2cf9a48b18:app/src/main/assets/content/v2-curriculum.json").directory(root()).start()
        val p=V2PackLoader.parse(process.inputStream.bufferedReader().readText());assertEquals(0,process.waitFor());return p
    }
    private fun objective(p:V2CurriculumPack)=p.unit(17)!!.objectives.single { it.id=="obj-u17-clock" }
    private fun sha(s:String)=java.security.MessageDigest.getInstance("SHA-256").digest(s.toByteArray(Charsets.UTF_8)).joinToString(""){"%02x".format(it.toInt() and 255)}
    private fun context():Context=ApplicationProvider.getApplicationContext()
    private fun root()=File(if(File("app").isDirectory)"." else "..")
    private fun pack()=V2PackLoader.parse(File(root(),"app/src/main/assets/content/v2-curriculum.json").readText())
}
