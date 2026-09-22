package dev.russiancompanion.v2

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.curriculum.V2PackLoader
import dev.russiancompanion.v2.curriculum.V2StartingPoint
import dev.russiancompanion.v2.curriculum.V2UnitState
import dev.russiancompanion.v2.curriculum.V2Validator
import dev.russiancompanion.v2.curriculum.advancementDecision
import dev.russiancompanion.v2.curriculum.canOpenUnit
import dev.russiancompanion.v2.curriculum.continueTarget
import dev.russiancompanion.v2.curriculum.unit
import dev.russiancompanion.v2.curriculum.unitState
import dev.russiancompanion.v2.ui.V2App
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33], qualifiers = "w411dp-h914dp-xxhdpi")
class V2RebuildTest {
    @get:Rule
    val compose = createComposeRule()

    private val stores = mutableListOf<LocalStore>()

    @After
    fun close() {
        stores.forEach { it.close() }
    }

    @Test
    fun packValidatesAndRefusesGeneratedOrCatalogBoundContent() {
        val pack = pack()
        val validation = V2Validator.validate(pack)
        assertTrue(validation.errors.joinToString(), validation.ok)
        assertEquals(30, pack.units.size)
        assertEquals("INSTALLED", pack.unit(1)?.installState)
        assertEquals("INSTALLED", pack.unit(2)?.installState)
        assertTrue(pack.units.drop(2).all { it.installState == "NOT_YET_INSTALLED" })
        assertEquals(0, pack.admissions.generatedRussianWords)
        assertEquals(0, pack.admissions.penguinProseAssets)
        assertEquals(0, pack.admissions.catalogAudioBoundToUnits)
        assertEquals("PENDING_QUALIFIED_REVIEW", pack.linguisticReview.status)
        assertTrue(pack.units.none { unit ->
            unit.sessions.any { session -> session.steps.any { it.itemIds.isNotEmpty() && it.body.contains("The New Penguin Russian Course") && it.body.contains("exercise") } }
        })
        val seed = File(repo(), "app/src/main/assets/content/v2-curriculum.json").readText()
        assertFalse(seed.contains("The New Penguin Russian Course lesson prose"))
    }

    @Test
    fun unitsThreeToThirtyAreNotInstalledAndDoNotJump() {
        val pack = pack()
        val learner = LocalStore.openInMemory(context()).also { stores += it }.v2LearnerView()
        val unit3 = pack.unit(3)!!
        assertEquals(V2UnitState.NOT_YET_INSTALLED, unitState(pack, unit3, learner))
        assertFalse(canOpenUnit(unitState(pack, unit3, learner)))
        assertTrue(unit3.sessions.isEmpty())
        assertTrue(unit3.objectives.isEmpty())
    }

    @Test
    fun unitOneProgressesInOrderAndRestoresAfterRestart() {
        val store = LocalStore.openInMemory(context()).also { stores += it }
        val pack = pack()
        val first = pack.unit(1)!!.sessions.first()
        store.v2SaveProfile(
            store.v2Profile().copy(
                onboardingCompleted = true,
                currentSessionId = first.id,
                currentStepIndex = 0,
            ),
        )
        val before = continueTarget(pack, store.v2LearnerView())
        assertEquals(first.id, before.sessionId)
        assertEquals(0, before.stepIndex)
        store.v2CompleteStep(first.steps.first().id, "2026-09-22T12:00")
        store.v2SaveProfile(store.v2Profile().copy(currentSessionId = first.id, currentStepIndex = 1))
        val after = continueTarget(pack, store.v2LearnerView())
        assertEquals(first.id, after.sessionId)
        assertEquals(1, after.stepIndex)
        val reopened = LocalStore.openInMemory(context()).also { stores += it }
        reopened.v2SaveProfile(
            reopened.v2Profile().copy(
                onboardingCompleted = true,
                currentSessionId = first.id,
                currentStepIndex = 1,
            ),
        )
        reopened.v2CompleteStep(first.steps.first().id, "2026-09-22T12:01")
        assertEquals(1, continueTarget(pack, reopened.v2LearnerView()).stepIndex)
    }

    @Test
    fun unitTwoRequiresUnitOneAndCannotUseUnrelatedCatalogAudio() {
        val pack = pack()
        val store = LocalStore.openInMemory(context()).also { stores += it }
        val locked = unitState(pack, pack.unit(2)!!, store.v2LearnerView())
        assertEquals(V2UnitState.LOCKED, locked)
        store.v2CompleteUnit(1, "2026-09-22T12:00")
        store.v2SaveProfile(store.v2Profile().copy(currentUnitNumber = 2, onboardingCompleted = true))
        assertEquals(V2UnitState.CURRENT, unitState(pack, pack.unit(2)!!, store.v2LearnerView()))
        assertTrue(pack.unit(1)!!.sessions.flatMap { it.steps }.any { it.unavailableReason != null })
        assertTrue(pack.unit(2)!!.sessions.flatMap { it.steps }.any { it.unavailableReason != null })
        assertEquals(0, pack.admissions.catalogAudioBoundToUnits)
    }

    @Test
    fun advancementNeedsMoreThanOneCheckAndBookAttestation() {
        val pack = pack()
        val store = LocalStore.openInMemory(context()).also { stores += it }
        val unit = pack.unit(1)!!
        unit.sessions.flatMap { it.steps }.filter { it.kind == "ORIENT" || it.kind == "TEACH" }.forEach {
            store.v2CompleteStep(it.id, "2026-09-22T12:00")
        }
        val early = advancementDecision(pack, unit, store.v2LearnerView())
        assertFalse(early.allowed)
        store.v2AttestBook(1, "2026-09-22T12:01")
        store.v2RecordAttempt("u1-i-а-identity", "Cyrillic А", true, "2026-09-22T12:02")
        val still = advancementDecision(pack, unit, store.v2LearnerView())
        assertFalse(still.allowed)
        store.v2RecordAttempt("u1-i-р-not-p", "The Cyrillic letter Р", true, "2026-09-22T12:03")
        val ready = advancementDecision(pack, unit, store.v2LearnerView())
        assertTrue(ready.remaining.joinToString(), ready.allowed)
    }

    @Test
    fun onboardingAndPrimaryControlsAreVisible() {
        val store = LocalStore.openInMemory(context()).also { stores += it }
        compose.setContent { V2App(store = store, pack = pack(), onRetryOpen = {}) }
        compose.onNodeWithTag("v2-onboarding-welcome").assertIsDisplayed()
        compose.onNodeWithTag("v2-onboarding-next").performClick()
        compose.onNodeWithText("I have the book").assertIsDisplayed()
        repeat(3) { compose.onNodeWithTag("v2-onboarding-next").performClick() }
        compose.onNodeWithText("Start Unit 1").performClick()
        compose.onNodeWithTag("v2-today-unit").assertIsDisplayed()
        compose.onNodeWithTag("v2-continue").assertIsDisplayed()
        compose.onNodeWithText("Course").performClick()
        compose.onNodeWithText("Unit 1 · Learning to read Russian", substring = true).assertIsDisplayed()
        compose.onAllNodesWithText("Not yet installed")[0].assertIsDisplayed()
        compose.onNodeWithText("Practice").performClick()
        compose.onNodeWithText("Letter recognition").assertIsDisplayed()
        compose.onAllNodesWithText("Unavailable", substring = true)[0].assertIsDisplayed()
        compose.onNodeWithText("Reference").performClick()
        compose.onNodeWithText("Cyrillic print forms").assertIsDisplayed()
        compose.onNodeWithText("Progress").performClick()
        compose.onNodeWithText("not fluency", substring = true).assertIsDisplayed()
    }

    @Test
    fun noInternetPermissionIsDeclared() {
        val manifest = File(repo(), "app/src/main/AndroidManifest.xml").readText()
        assertFalse(manifest.contains("INTERNET"))
    }

    @Test
    fun upgradeKeepsExportAndRecordingRows() {
        val store = LocalStore.openInMemory(context()).also { stores += it }
        store.applySeed(LocalStore.readPackagedSeed(context()))
        val before = store.v2LearnerView()
        assertFalse(before.onboardingCompleted)
        store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = 1))
        assertTrue(store.v2LearnerView().onboardingCompleted)
        assertTrue(File(repo(), "app/src/main/java/dev/russiancompanion/data/local/DatabaseMigrations.kt").readText().contains("MIGRATION_6_7"))
    }

    private fun pack() = V2PackLoader.parse(File(repo(), "app/src/main/assets/content/v2-curriculum.json").readText())

    private fun context(): Context = ApplicationProvider.getApplicationContext()

    private fun repo(): File = listOf(File("."), File("..")).first { File(it, "app/src/main").isDirectory }
}
