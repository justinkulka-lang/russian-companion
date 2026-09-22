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
import dev.russiancompanion.data.local.seed.PlanDefinitionSeed
import dev.russiancompanion.data.local.seed.SeedJson
import dev.russiancompanion.data.navigation.RoomNavigationGateway
import dev.russiancompanion.domain.gate.defaultDisplayedRuleText
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

        compose.onNodeWithText("Next").performClick()
        compose.onNodeWithText("Lesson 1", substring = false).performClick()
        compose.onNodeWithText("Confirm lesson").performClick()
        click("Gates")
        compose.onNodeWithText("Activity gate, not proficiency certification.").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText(
            "Locked — remaining: Book revision score, AnkiDroid completion or maturity, Prescribed work completion",
        )
            .performScrollTo()
            .assertIsDisplayed()
        click(defaultDisplayedRuleText())
        show("Self-attested — not independently verified")
        show("Self-attested — no AnkiDroid integration; not independently verified")
        show("At least 1")
        show("Equals Mature")
        show("Evidence is required")
        show("No advancement decision is recorded.")
        compose.onNodeWithText("fluency").assertDoesNotExist()
        compose.onNodeWithText("Optional drills").assertDoesNotExist()

        replaceField(0, "nope")
        click("Save evidence")
        show("This entry is not valid.")
        show("No advancement decision is recorded.")

        replaceField(0, "0")
        click("Completed")
        click("Confirm prescribed work")
        click("Save evidence")
        show("Recorded gate status: Not yet satisfied")
        compose.onAllNodesWithText("Condition not met").assertCountEquals(2)
        show("Locked — remaining: Book revision score, AnkiDroid completion or maturity")
        show("Recorded value: 0 at")
        show("Recorded value: COMPLETED at")
        show("Recorded value: recorded at")
        compose.onAllNodesWithText("Self-attested — not independently verified").assertCountEquals(2)
        compose.onAllNodesWithText("Self-attested — no AnkiDroid integration; not independently verified").assertCountEquals(2)

        replaceField(0, "1")
        click("Mature")
        click("Save evidence")
        show("Recorded gate status: Satisfied")
        compose.onAllNodesWithText("Condition met").assertCountEquals(3)
        compose.onNodeWithText("Locked — remaining", substring = true).assertDoesNotExist()

        click("Add tutor sign-off")
        compose.onNodeWithText("Tutor sign-off", substring = false).performScrollTo().assertIsDisplayed()
        show("Recorded gate status: Not evaluable")
        show("Awaiting user evidence")
        show("Locked — remaining: Tutor sign-off")
        compose.onNodeWithText("Recorded gate status: Satisfied").assertDoesNotExist()

        click("Record tutor sign-off")
        click("Save evidence")
        show("Recorded gate status: Satisfied")
        compose.onAllNodesWithText("Condition met").assertCountEquals(4)
        compose.onNodeWithText("Awaiting user evidence").assertDoesNotExist()
        compose.onNodeWithText("Locked — remaining", substring = true).assertDoesNotExist()

        click("Home")
        click("Gates")
        show("Recorded gate status: Satisfied")
        compose.onNodeWithText("Locked — remaining", substring = true).assertDoesNotExist()
    }

    private fun click(text: String) {
        val node = compose.onNodeWithText(text, substring = false)
        runCatching { node.performScrollTo() }
        node.performClick()
    }

    private fun show(text: String) {
        val node = compose.onNodeWithText(text, substring = true)
        runCatching { node.performScrollTo() }
        node.assertIsDisplayed()
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
                lessonNumber = 1,
            ),
        ),
        gateDefinitions = listOf(gate()),
    )
}
