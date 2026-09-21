package dev.russiancompanion.presentation.navigation

import android.content.Context
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextReplacement
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.data.local.gate
import dev.russiancompanion.data.local.minimalDocument
import dev.russiancompanion.data.local.seed.GateConditionSeed
import dev.russiancompanion.data.local.seed.PlanDefinitionSeed
import dev.russiancompanion.data.local.seed.SeedJson
import dev.russiancompanion.data.navigation.RoomNavigationGateway
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class GateUiTest {
    @get:Rule
    val compose = createComposeRule()

    private val stores = mutableListOf<LocalStore>()
    private var stamp = 0

    @After
    fun closeStores() {
        stores.forEach { it.close() }
    }

    @Test
    fun gateEvidenceIsSelfAttestedAndTheDecisionFollowsTheDisplayedRule() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val store = LocalStore.openInMemory(context).also { stores += it }
        store.applySeed(SeedJson.encode(gateDocument()))
        compose.setContent {
            CompanionApp(
                gateway = RoomNavigationGateway(store, { null }, PermissivePracticeAudio()),
                onRetryOpen = {},
                today = { "2026-09-21" },
                recordedAt = { "2026-09-21T09:%02d".format(stamp++) },
                practiceAudio = PermissivePracticeAudio(),
            )
        }

        repeat(3) { compose.onNodeWithText("Next").performClick() }
        compose.onNodeWithText("Lesson 1", substring = false).performClick()
        compose.onNodeWithText("Confirm lesson").performClick()
        click("Gates")
        compose.onNodeWithText("Activity gate, not proficiency certification.").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Missing recorded evidence: Book revision score, AnkiDroid completion or maturity")
            .performScrollTo()
            .assertIsDisplayed()
        click("Displayed test rule")
        show("Self-attested — not independently verified")
        show("Self-attested — no AnkiDroid integration; not independently verified")
        show("At least 1")
        show("Equals mature")
        show("No advancement decision is recorded.")
        compose.onNodeWithText("fluency").assertDoesNotExist()
        compose.onNodeWithText("Optional drills").assertDoesNotExist()

        replaceField(0, "nope")
        click("Save evidence")
        show("This entry is not valid.")
        show("No advancement decision is recorded.")

        replaceField(0, "0")
        replaceField(1, "started")
        click("Save evidence")
        show("Recorded gate status: Not yet satisfied")
        compose.onAllNodesWithText("Condition not met").assertCountEquals(2)
        show("Recorded value: 0 at")
        show("Recorded value: started at")

        replaceField(0, "1")
        replaceField(1, "mature")
        click("Save evidence")
        show("Recorded gate status: Satisfied")
        compose.onAllNodesWithText("Condition met").assertCountEquals(2)

        click("Add tutor sign-off")
        show("Tutor sign-off")
        show("Recorded gate status: Not evaluable")
        show("Awaiting user evidence")
        compose.onNodeWithText("Recorded gate status: Satisfied").assertDoesNotExist()

        click("Record evidence")
        click("Save evidence")
        show("Recorded gate status: Satisfied")
        compose.onAllNodesWithText("Condition met").assertCountEquals(3)
        compose.onNodeWithText("Awaiting user evidence").assertDoesNotExist()

        click("Home")
        show("Recorded gate status: Satisfied")
    }

    private fun click(text: String) {
        compose.onNodeWithText(text, substring = false).performScrollTo().performClick()
    }

    private fun show(text: String) {
        compose.onNodeWithText(text, substring = true).performScrollTo().assertIsDisplayed()
    }

    private fun replaceField(index: Int, value: String) {
        compose.onAllNodes(hasSetTextAction())[index].performScrollTo().performTextReplacement(value)
    }

    private fun gateDocument() = minimalDocument().copy(
        planDefinitions = listOf(
            PlanDefinitionSeed(
                id = "plan-book",
                segmentId = "segment-1",
                activityType = "BOOK",
                instruction = "Use your copy of the book for this lesson",
                sortOrder = 1,
                required = true,
            ),
        ),
        gateDefinitions = listOf(
            gate(
                conditions = listOf(
                    GateConditionSeed("c-book", "BOOK_REVISION_SCORE", "AT_LEAST", "1", 0),
                    GateConditionSeed("c-anki", "ANKI_DROID_STATUS", "EQUALS", "mature", 1),
                ),
            ),
        ),
    )
}
