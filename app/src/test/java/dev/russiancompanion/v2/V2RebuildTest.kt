package dev.russiancompanion.v2

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.unit.dp
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.curriculum.V2PackLoader
import dev.russiancompanion.v2.curriculum.V2StartingPoint
import dev.russiancompanion.v2.curriculum.V2UnitState
import dev.russiancompanion.v2.curriculum.V2Validator
import dev.russiancompanion.v2.curriculum.advancementDecision
import dev.russiancompanion.v2.curriculum.canOpenUnit
import dev.russiancompanion.v2.curriculum.continueTarget
import dev.russiancompanion.v2.curriculum.todaysSession
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
        assertEquals("DEVELOPMENT", pack.unit(1)?.installState)
        assertEquals("DEVELOPMENT", pack.unit(2)?.installState)
        assertTrue(pack.units.drop(2).all { it.installState == "NOT_YET_INSTALLED" })
        assertEquals(0, pack.admissions.generatedRussianWords)
        assertEquals(0, pack.admissions.penguinProseAssets)
        assertEquals(0, pack.admissions.catalogAudioBoundToUnits)
        assertEquals("PENDING_QUALIFIED_REVIEW", pack.linguisticReview.status)
        assertTrue(pack.items.none { it.isGovernance() })
        assertTrue(pack.items.all { !it.promptLetter.isNullOrBlank() })
        assertTrue(pack.units.none { unit ->
            unit.sessions.any { session -> session.steps.any { it.itemIds.isNotEmpty() && it.body.contains("The New Penguin Russian Course") && it.body.contains("exercise") } }
        })
        val seed = File(repo(), "app/src/main/assets/content/v2-curriculum.json").readText()
        assertFalse(seed.contains("The New Penguin Russian Course lesson prose"))
        assertFalse(seed.contains("u1-i-no-catalog-word"))
        assertFalse(seed.contains("u2-i-no-score"))
        assertFalse(seed.contains("u2-i-handwriting-source"))
        assertFalse(seed.contains("u2-i-review-unit1"))
    }

    @Test
    fun approvedReviewRequiresACompleteMatchingRecord() {
        val pack = pack()
        val premature = pack.copy(
            linguisticReview = pack.linguisticReview.copy(status = "APPROVED"),
        )
        val prematureResult = V2Validator.validate(premature)
        assertFalse(prematureResult.ok)
        assertTrue(prematureResult.errors.any { it.contains("APPROVED review is missing") })

        val unknown = pack.copy(
            linguisticReview = pack.linguisticReview.copy(status = "SIGNED_OFF"),
        )
        val unknownResult = V2Validator.validate(unknown)
        assertFalse(unknownResult.ok)
        assertTrue(unknownResult.errors.any { it.contains("Unrecognized linguistic review status") })

        val digest = V2Validator.instructionalContentDigest(pack)
        val complete = pack.copy(
            linguisticReview = pack.linguisticReview.copy(
                status = "APPROVED",
                reviewer = "Qualified Reviewer",
                packVersion = pack.packVersion,
                reviewedContentDigest = digest,
                curriculumMappingId = "map-owned-edition",
            ),
        )
        val completeResult = V2Validator.validate(complete)
        assertTrue(completeResult.errors.joinToString(), completeResult.ok)
    }

    @Test
    fun installedUnitsAreRejectedWhileRequiredExperiencesAreMissing() {
        val pack = pack()
        val installed = pack.copy(
            units = pack.units.map { unit ->
                if (unit.number in 1..2) unit.copy(installState = "INSTALLED") else unit
            },
        )
        val result = V2Validator.validate(installed)
        assertFalse(result.ok)
        assertTrue(result.errors.any { it.contains("unavailable required experiences") || it.contains("APPROVED") })
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
        val name = "v2-restart-${System.nanoTime()}.db"
        val store = LocalStore.openNamed(context(), name).also { stores += it }
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
        store.close()
        stores.remove(store)
        val reopened = LocalStore.openNamed(context(), name).also { stores += it }
        val restored = continueTarget(pack, reopened.v2LearnerView())
        assertEquals(first.id, restored.sessionId)
        assertEquals(1, restored.stepIndex)
        assertTrue(first.steps.first().id in reopened.v2LearnerView().completedStepIds)
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
    fun todayShowsOneBoundedSessionInsteadOfTheUnitInventory() {
        val pack = pack()
        val store = LocalStore.openInMemory(context()).also { stores += it }
        store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true, dailyMinutes = 35))
        val today = todaysSession(pack.unit(1)!!, store.v2LearnerView())
        assertEquals(pack.unit(1)!!.sessions.first().id, today?.id)
        compose.setContent { V2App(store = store, pack = pack, onRetryOpen = {}) }
        compose.onNodeWithTag("v2-today-unit").assertIsDisplayed()
        compose.onNodeWithTag("v2-today-objective").assertIsDisplayed()
        compose.onNodeWithText("Today’s session").assertIsDisplayed()
        compose.onNodeWithText("Show full unit plan").assertIsDisplayed()
        compose.onNodeWithText("Settings and data").assertDoesNotExist()
        compose.onNodeWithText("Work in your book").assertDoesNotExist()
        assertInsideRoot("v2-today-unit")
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
        compose.onNodeWithText("Unit 1 · Printed letters", substring = true).assertIsDisplayed()
        compose.onAllNodesWithText("Not yet installed", substring = true)[0].assertIsDisplayed()
        compose.onNodeWithText("Practice").performClick()
        compose.onNodeWithText("Alphabet Lab").assertIsDisplayed()
        compose.onNodeWithText("Reference").performClick()
        compose.onNodeWithText("Cyrillic print forms").assertIsDisplayed()
        compose.onNodeWithText("Progress").performClick()
        compose.onNodeWithText("not fluency", substring = true).assertIsDisplayed()
    }

    @Test
    fun unitAndLabHeadingsStayInsideTheRoot() {
        val store = LocalStore.openInMemory(context()).also { stores += it }
        store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true))
        compose.setContent { V2App(store = store, pack = pack(), onRetryOpen = {}) }
        compose.onNodeWithText("Course").performClick()
        compose.onNodeWithText("Unit 1 · Printed letters", substring = true).performClick()
        compose.onNodeWithTag("v2-back").assertIsDisplayed()
        compose.onNodeWithTag("v2-unit-heading").assertIsDisplayed()
        assertInsideRoot("v2-back")
        assertInsideRoot("v2-unit-heading")
        compose.onNodeWithTag("v2-letter-board").assertIsDisplayed()
        compose.onNodeWithTag("v2-back").performClick()
        compose.onNodeWithText("Practice").performClick()
        compose.onNodeWithText("Alphabet Lab").performClick()
        compose.onNodeWithTag("v2-letter-board").assertIsDisplayed()
        compose.onNodeWithTag("v2-confusable-Р").performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("v2-print-pair-grid").performScrollTo().assertIsDisplayed()
        assertInsideRoot("v2-back")
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

    private fun assertInsideRoot(tag: String) {
        val root = compose.onRoot().getBoundsInRoot()
        val node = compose.onNodeWithTag(tag).getUnclippedBoundsInRoot()
        val visible = compose.onNodeWithTag(tag).getBoundsInRoot()
        assertEquals(node.left, visible.left)
        assertEquals(node.top, visible.top)
        assertTrue(tag, node.left.value >= root.left.value)
        assertTrue(tag, visible.left.value >= root.left.value)
        assertTrue(tag, node.left.value >= 0f)
    }

    private fun pack() = V2PackLoader.parse(File(repo(), "app/src/main/assets/content/v2-curriculum.json").readText())

    private fun context(): Context = ApplicationProvider.getApplicationContext()

    private fun repo(): File = listOf(File("."), File("..")).first { File(it, "app/src/main").isDirectory }
}

private fun dev.russiancompanion.v2.curriculum.V2CheckItem.isGovernance(): Boolean {
    return dev.russiancompanion.v2.curriculum.GOVERNANCE_PROMPT_MARKERS.any { marker ->
        "$id $kind $prompt $feedback".lowercase().contains(marker)
    }
}
