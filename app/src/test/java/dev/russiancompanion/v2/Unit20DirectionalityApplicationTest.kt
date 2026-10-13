package dev.russiancompanion.v2

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.curriculum.*
import dev.russiancompanion.v2.ui.V2App
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class Unit20DirectionalityApplicationTest {
    @get:Rule val compose = createComposeRule()
    @Test fun motionCompletionMustRequireDistinctIndependentDirectionalityContexts() {
        val p = pack(); val o = p.unit(20)!!.objectives.single { it.id == "obj-u20-motion" }
        val ids = o.practiceStepIds.flatMap { p.step(it)!!.second.itemIds } + o.checkItemIds
        LocalStore.openInMemory(context()).use { store ->
            for (id in ids) {
                val step = p.unit(20)!!.sessions.flatMap { it.steps }.first { id in it.itemIds }
                store.v2RecordGovernedAttempt(prepareGovernedAttempt(p, id, p.item(id)!!.answer, PRESCRIBED_CHECK, step.id, "2026-10-13T09:00")!!)
            }
            assertEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS, objectiveState(o, store.v2LearnerView(), p))
            val features = o.checkItemIds.map { p.item(it)!!.assessment!!.testedFeature }.toSet()
            assertTrue("A completed motion objective must require independent contextual directionality, not guided labels and lexical or prefixed readings alone",
                features.containsAll(setOf("MOTION_DIRECTIONAL_APPLICATION")))
        }
        val validation = V2Validator.validate(p)
        assertTrue(validation.errors.joinToString("\n"), validation.ok)
    }
    @Test fun originalGuidedLabelAndFourReadingsCannotCompleteDirectionality() {
        val p = pack(); val o = p.unit(20)!!.objectives.single { it.id == "obj-u20-motion" }
        LocalStore.openInMemory(context()).use { store ->
            for (id in listOf("u20-g-motion-0","u20-i-motion-1","u20-i-motion-2","u20-i-motion-3","u20-i-motion-4")) {
                val step = p.unit(20)!!.sessions.flatMap { it.steps }.first { id in it.itemIds }
                store.v2RecordGovernedAttempt(prepareGovernedAttempt(p,id,p.item(id)!!.answer,PRESCRIBED_CHECK,step.id,"2026-10-13T16:01")!!)
            }
            assertEquals(5,creditItemIds(p,store.v2LearnerView()).size)
            assertEquals(V2ObjectiveState.REVIEW_REQUIRED,objectiveState(o,store.v2LearnerView(),p))
            assertTrue(currentUnitRequirements(p,p.unit(20)!!,store.v2LearnerView()).remaining.contains("Demonstrate every check for ${o.title}"))
        }
    }
    @Test fun playerRejectsVariousDirectionReadingForTheDirectedFlight() = wrong("u20-i-direction-birds",
        "Movement in various directions (imperfective).")
    @Test fun playerRejectsPerfectiveInferenceFromACompletedRoundTrip() = wrong("u20-i-direction-round",
        "The form is perfective because this trip is completed.")
    private fun wrong(id: String,answer: String) {
        val p = pack(); val item = p.item(id)!!
        val step = p.unit(20)!!.sessions.flatMap { it.steps }.first { id in it.itemIds };val owner = p.step(step.id)!!
        LocalStore.openInMemory(context()).use { store ->
            store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true,currentUnitNumber = 20,startingPoint = "RESUME_FROM_BOOK",
                currentSessionId = owner.first.id,currentStepIndex = owner.first.steps.indexOf(step)).verifiedResume(p))
            compose.setContent { V2App(store = store,pack = p,onRetryOpen = {}) }
            compose.onNodeWithTag("v2-continue").performClick()
            compose.onNodeWithTag("v2-option-$id-$answer").performScrollTo().performClick()
            compose.onNodeWithTag("v2-item-feedback-$id").performScrollTo().assertTextEquals("Not the reviewed answer. ${item.feedback}")
            assertFalse(id in creditItemIds(p,store.v2LearnerView()))
        }
    }
    @Test fun independentContextsAreMeaningfullyDifferentFromGuidedContexts() {
        val p = pack();val items = p.items.filter { it.assessment?.testedFeature == "MOTION_DIRECTIONAL_APPLICATION" }
        val guided = items.filter { it.assessment?.independent == false };val independent = items.filter { it.assessment?.independent == true }
        assertEquals(2,guided.size);assertEquals(4,independent.size)
        assertTrue(independent.all { i -> guided.none { g -> g.promptLetter == i.promptLetter } })
        assertEquals(6,items.map { it.promptLetter }.distinct().size)
    }
    @Test fun coordinatedKeyAndApplicationCannotReverseTheDirectedFlight() {
        val p = pack();val item = p.item("u20-i-direction-birds")!!;val a = item.assessment!!;val ids = a.relationParticipantIds;val wrong = item.options[1]
        val reasons = listOf(item.distractorRationales[1],"",item.distractorRationales[2])
        val changed = item.copy(answer = wrong,distractorRationales = reasons,assessment = a.copy(
            relationParticipantIds = listOf(ids[0],ids[2],ids[1],ids[3]),answerSourceIds = listOf(ids[2]),
            distractorSourceIds = listOf(ids[1],"",ids[3]),distractorDifference = reasons))
        val records = p.provenanceRecords.map { r -> if (r.sourceRecordId != "ca-rule-context:${item.id}") r else r.copy(
            synthesis = r.synthesis!!.copy(applications = r.synthesis.applications.map { it.copy(answer = wrong,distractors = listOf(item.answer,item.options[2])) })) }
        assertTrue(records.zip(p.provenanceRecords).all { (x,y) -> x.originalSourceText == y.originalSourceText && x.sourceHash == y.sourceHash && x.shippedText == y.shippedText && x.contentHash == y.contentHash })
        val result = V2Validator.validate(p.copy(items = p.items.map { if (it.id == item.id) changed else it },provenanceRecords = records))
        assertFalse(result.ok);assertTrue(result.errors.joinToString("\n"),result.errors.any { it.contains("complete rule context") })
    }
    @Test fun coherentNativeTextAndHashForgeryCannotChangeTheFlightExample() {
        val p = pack();val records = p.provenanceRecords.map { r -> if (r.itemId != "prov-u20-direction-native-birds-ru") r else {
            val text = r.originalSourceText.replace("Птицы летят на юг.","Птицы летят на север.")
            r.copy(originalSourceText = text,sourceHash = sha(text),shippedText = "Птицы летят на север.",contentHash = sha("Птицы летят на север."))
        } }
        val result = V2Validator.validate(p.copy(provenanceRecords = records));assertFalse(result.ok)
        assertTrue(result.errors.joinToString("\n"),result.errors.any { it.contains("cap-u20-motion-directionality has no reviewable instruction and check chain") })
    }
    @Test fun restoringOldFourChecksCannotClearTheDirectionalityCapability() {
        val p = pack();val old = baseline();val checks = old.unit(20)!!.objectives.single { it.id == "obj-u20-motion" }.checkItemIds
        val unit = p.unit(20)!!.copy(objectives = p.unit(20)!!.objectives.map { if (it.id != "obj-u20-motion") it else it.copy(checkItemIds = checks) })
        val result = V2Validator.validate(p.copy(units = p.units.map { if (it.number == 20) unit else it }))
        assertFalse(result.ok);assertTrue(result.errors.joinToString("\n"),result.errors.any { it.contains("cap-u20-motion-directionality has no reviewable instruction and check chain") })
    }
    @Test fun authenticEarlierAttemptsStayHistoricalAndCannotCreditNewContent() {
        val p = pack();val old = baseline();val o = old.unit(20)!!.objectives.single { it.id == "obj-u20-motion" }
        LocalStore.openInMemory(context()).use { store ->
            for (id in o.practiceStepIds.flatMap { old.step(it)!!.second.itemIds }+o.checkItemIds) {
                val step = old.unit(20)!!.sessions.flatMap { it.steps }.first { id in it.itemIds }
                store.v2RecordGovernedAttempt(prepareGovernedAttempt(old,id,old.item(id)!!.answer,PRESCRIBED_CHECK,step.id,"2026-10-13T16:00")!!)
            }
            assertEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS,objectiveState(o,store.v2LearnerView(),old))
            assertTrue(creditItemIds(p,store.v2LearnerView()).isEmpty())
            assertNotEquals(V2ObjectiveState.DEMONSTRATED_IN_APP_CHECKS,objectiveState(p.unit(20)!!.objectives.single { it.id == o.id },store.v2LearnerView(),p))
            assertEquals(5,store.v2Attempts().size)
        }
    }
    private fun baseline(): V2CurriculumPack {
        val process = ProcessBuilder("git","show","1ce6184a5bbe894624807c066600fa51de42ace3:app/src/main/assets/content/v2-curriculum.json").directory(root()).start()
        val p = V2PackLoader.parse(process.inputStream.bufferedReader().readText());assertEquals(0,process.waitFor());return p
    }
    private fun sha(s: String) = java.security.MessageDigest.getInstance("SHA-256").digest(s.toByteArray(Charsets.UTF_8)).joinToString("") { "%02x".format(it.toInt() and 255) }
    private fun context(): Context = ApplicationProvider.getApplicationContext()
    private fun root() = File(if (File("app").isDirectory) "." else "..")
    private fun pack() = V2PackLoader.parse(File(root(), "app/src/main/assets/content/v2-curriculum.json").readText())
}
