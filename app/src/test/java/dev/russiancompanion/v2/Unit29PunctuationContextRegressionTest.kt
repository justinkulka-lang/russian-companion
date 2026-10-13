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
@Config(sdk = [33])
class Unit29PunctuationContextRegressionTest {
    @get:Rule val compose = createComposeRule()
    @Test fun categoryAndConnectiveCreditMustNotDemonstrateContextualPunctuation() {
        val root = File(if (File("app").isDirectory) "." else "..")
        val p = V2PackLoader.parse(File(root,"app/src/main/assets/content/v2-curriculum.json").readText())
        val o = p.unit(29)!!.objectives.single { it.id == "obj-u29-punctuation" }
        LocalStore.openInMemory(ApplicationProvider.getApplicationContext<Context>()).use { store ->
            val ids = o.practiceStepIds.flatMap { p.step(it)!!.second.itemIds } + o.checkItemIds
            for (id in ids) {
                val step = p.unit(29)!!.sessions.flatMap { it.steps }.first { id in it.itemIds }
                store.v2RecordGovernedAttempt(prepareGovernedAttempt(p,id,p.item(id)!!.answer,PRESCRIBED_CHECK,step.id,"2026-10-13T16:00")!!)
            }
            assertEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS,objectiveState(o,store.v2LearnerView(),p))
            assertTrue("A completed punctuation objective must require a complete construction and comma decision, not a grammatical category cue and source connective",
                o.checkItemIds.none { p.item(it)!!.promptLetter == "личным местоимением" || p.item(it)!!.answer == "кроме тех случаев" })
        }
    }
    @Test fun originalDefaultAnswersCannotCompleteExpandedPunctuation() {
        val p=pack();val o=objective(p)
        LocalStore.openInMemory(context()).use { store ->
            record(p,store,listOf("u29-g-punctuation-0","u29-i-punctuation-1"))
            assertEquals(2,creditItemIds(p,store.v2LearnerView()).size)
            assertEquals(V2ObjectiveState.REVIEW_REQUIRED,objectiveState(o,store.v2LearnerView(),p))
        }
    }
    @Test fun optionalReferenceHasTeachingButNoGrammarCredit() {
        val p=pack();val item=p.item("u29-i-punctuation-2")!!
        LocalStore.openInMemory(context()).use { store ->
            store.v2SaveProfile(store.v2Profile().copy(currentUnitNumber=29,startingPoint="RESUME_FROM_BOOK"))
            assertTrue(practiceQueue(p,store.v2LearnerView(),"vocabulary").any { it.id==item.id && it.kind=="SOURCE_QUESTION" })
            assertEquals(1,labExplanationSteps(p,item).size)
            store.v2RecordGovernedAttempt(prepareGovernedAttempt(p,item.id,item.answer,OPTIONAL_PRACTICE,practiceStepId("vocabulary"),"2026-10-13T16:01")!!)
            assertNotNull(optionalPracticeSelection(p,store.v2LearnerView(),"vocabulary",item.id))
            assertTrue(creditItemIds(p,store.v2LearnerView()).isEmpty())
            assertEquals(1,store.v2Attempts().size)
        }
        val result=V2Validator.validate(p);assertTrue(result.errors.joinToString("\n"),result.ok)
    }
    @Test fun playerRejectsNoCommaForPersonalPronoun() = wrong("u29-i-punctuation-pronoun",
        "No separating comma is used between the participial phrase and the word it describes.")
    @Test fun playerRejectsClosingCommaAtSentenceEnd() = wrong("u29-i-punctuation-terminal",
        "Separating commas mark both boundaries of the participial phrase before the sentence continues.")
    private fun wrong(id:String,answer:String) {
        val p=pack();val item=p.item(id)!!;val step=p.unit(29)!!.sessions.flatMap { it.steps }.first { id in it.itemIds };val owner=p.step(step.id)!!
        LocalStore.openInMemory(context()).use { store ->
            store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted=true,currentUnitNumber=29,startingPoint="RESUME_FROM_BOOK",
                currentSessionId=owner.first.id,currentStepIndex=owner.first.steps.indexOf(step)).verifiedResume(p))
            compose.setContent { V2App(store=store,pack=p,onRetryOpen={}) }
            compose.onNodeWithTag("v2-continue").performClick()
            compose.onNodeWithTag("v2-option-$id-$answer").performScrollTo().performClick()
            compose.onNodeWithTag("v2-item-feedback-$id").performScrollTo().assertTextEquals("Not the reviewed answer. ${item.feedback}")
            assertFalse(id in creditItemIds(p,store.v2LearnerView()))
        }
    }
    @Test fun sourceNormalizationPreservesCompleteWordsAndPunctuation() {
        val p=pack();val records=p.provenanceRecords.filter { it.itemId.startsWith("prov-u29-punctuation-native-") }
        assertEquals(20,records.size)
        records.forEach { assertTrue(it.shippedText in V2Validator.unfoldWikiLabels(it.originalSourceText));assertEquals(sha(it.originalSourceText),it.sourceHash) }
        val artist=p.item("u29-i-punctuation-pronoun")!!
        assertEquals("Воспитанный в традициях Фоппы, Бернардо Дзенале и Амброджо Бергоньоне, он прошёл обучение в мастерской Леонардо.",artist.promptLetter)
        assertTrue(artist.feedback.contains("earlier comma separates names"))
        for (id in listOf("after","cause","terminal")) assertFalse(p.item("u29-i-punctuation-$id")!!.promptLetter!!.endsWith("."))
        assertTrue(p.unit(29)!!.sessions.single { it.id=="u29-s-punctuation" }.steps.single { it.id=="u29-st-teach-punctuation-3" }.body.contains("later comma after Бергоньоне"))
    }
    @Test fun restoringOriginalContrastCategoryCannotClearPunctuationCapability() {
        val p=pack();val old=baseline();val oldItem=old.item("u29-i-punctuation-2")!!
        assertEquals("CONTRAST_SELECTION",oldItem.kind);assertTrue(oldItem.assessment!!.independent)
        val unit=p.unit(29)!!.copy(objectives=p.unit(29)!!.objectives.map { if(it.id=="obj-u29-punctuation") it.copy(checkItemIds=objective(old).checkItemIds) else it })
        val result=V2Validator.validate(p.copy(units=p.units.map { if(it.number==29)unit else it },items=p.items.map { if(it.id==oldItem.id)oldItem else it }))
        assertFalse(result.ok);assertTrue(result.errors.joinToString("\n"),result.errors.any { it.contains("cap-obj-u29-punctuation has no reviewable instruction and check chain") })
    }
    @Test fun coordinatedKeyAndApplicationCannotRemovePronounComma() {
        val p=pack();val item=p.item("u29-i-punctuation-pronoun")!!;val a=item.assessment!!;val ids=a.relationParticipantIds;val wrong=item.options[1]
        val reasons=listOf(item.distractorRationales[1],"",item.distractorRationales[2])
        val changed=item.copy(answer=wrong,distractorRationales=reasons,assessment=a.copy(relationParticipantIds=listOf(ids[0],ids[2],ids[1],ids[3]),
            answerSourceIds=listOf(ids[2]),distractorSourceIds=listOf(ids[1],"",ids[3]),distractorDifference=reasons))
        val records=p.provenanceRecords.map { r -> if(r.sourceRecordId!="ca-rule-context:${item.id}")r else r.copy(synthesis=r.synthesis!!.copy(
            applications=r.synthesis.applications.map { it.copy(answer=wrong,distractors=listOf(item.answer,item.options[2])) })) }
        assertTrue(records.zip(p.provenanceRecords).all { (x,y)->x.originalSourceText==y.originalSourceText && x.sourceHash==y.sourceHash })
        val result=V2Validator.validate(p.copy(items=p.items.map { if(it.id==item.id)changed else it },provenanceRecords=records))
        assertFalse(result.ok);assertTrue(result.errors.joinToString("\n"),result.errors.any { it.contains("complete rule context") })
    }
    @Test fun coherentNativeTextAndHashForgeryCannotRemoveTerminalComma() {
        val p=pack();val records=p.provenanceRecords.map { r -> if(r.itemId!="prov-u29-punctuation-native-terminal-ru")r else {
            val text=r.originalSourceText.replace("маяк, стоящий","маяк стоящий");val shipped=r.shippedText.replace("маяк, стоящий","маяк стоящий")
            r.copy(originalSourceText=text,sourceHash=sha(text),shippedText=shipped,contentHash=sha(shipped))
        } }
        val result=V2Validator.validate(p.copy(provenanceRecords=records));assertFalse(result.ok)
        assertTrue(result.errors.joinToString("\n"),result.errors.any { it.contains("frozen native punctuation application chain") })
    }
    @Test fun newSourceCannotBorrowAnOlderAdmittedRevisionOrAddGhostRecord() {
        val p=pack();val r=p.provenanceRecords.single { it.itemId=="prov-u29-punctuation-native-terminal-ru" }
        for (forged in listOf(r.copy(sourceRevision="153078996"),r.copy(itemId="prov-u29-punctuation-native-ghost"))) {
            val result=V2Validator.validate(p.copy(provenanceRecords=p.provenanceRecords.filter { it.itemId!=r.itemId }+forged))
            assertFalse(result.ok);assertTrue(result.errors.joinToString("\n"),result.errors.any { it.contains("license") })
        }
    }
    @Test fun authenticHistoricalCompletionStaysSavedButCannotCreditNewChecks() {
        val p=pack();val old=baseline();val o=objective(old)
        LocalStore.openInMemory(context()).use { store ->
            record(old,store,o.practiceStepIds.flatMap { old.step(it)!!.second.itemIds }+o.checkItemIds)
            assertEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS,objectiveState(o,store.v2LearnerView(),old))
            assertEquals(3,store.v2Attempts().size)
            assertTrue(creditItemIds(p,store.v2LearnerView()).isEmpty())
            assertNotEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS,objectiveState(objective(p),store.v2LearnerView(),p))
        }
    }
    private fun record(p:V2CurriculumPack,store:LocalStore,ids:List<String>) {
        for(id in ids) { val step=p.unit(29)!!.sessions.flatMap { it.steps }.first { id in it.itemIds }
            store.v2RecordGovernedAttempt(prepareGovernedAttempt(p,id,p.item(id)!!.answer,PRESCRIBED_CHECK,step.id,"2026-10-13T16:00")!!) }
    }
    private fun baseline():V2CurriculumPack {
        val process=ProcessBuilder("git","show","e4085c5686e1c58a0af43e1c1f66fbb573f56887:app/src/main/assets/content/v2-curriculum.json").directory(root()).start()
        val p=V2PackLoader.parse(process.inputStream.bufferedReader().readText());assertEquals(0,process.waitFor());return p
    }
    private fun objective(p:V2CurriculumPack)=p.unit(29)!!.objectives.single { it.id=="obj-u29-punctuation" }
    private fun context():Context=ApplicationProvider.getApplicationContext()
    private fun root()=File(if(File("app").isDirectory)"." else "..")
    private fun pack()=V2PackLoader.parse(File(root(),"app/src/main/assets/content/v2-curriculum.json").readText())
    private fun sha(s:String)=java.security.MessageDigest.getInstance("SHA-256").digest(s.toByteArray(Charsets.UTF_8)).joinToString("") { "%02x".format(it.toInt() and 255) }
}
