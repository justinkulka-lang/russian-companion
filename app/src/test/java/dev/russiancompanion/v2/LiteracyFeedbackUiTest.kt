package dev.russiancompanion.v2

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.RussianCompanionApplication
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.curriculum.*
import dev.russiancompanion.v2.ui.*
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class LiteracyFeedbackUiTest {
    @get:Rule val compose = createComposeRule()
    @Test fun letterIdentityExplainsEachWrongValueWithoutGrantingRequiredCredit() = feedback("u1-i-м-identity", 1)
    @Test fun lowercaseCaseVariantExplainsEachWrongValueAndPreservesTheKey() = feedback("u2-i-в-not-b", 2)

    private fun feedback(id: String, unit: Int) {
        val context = ApplicationProvider.getApplicationContext<Context>()
        (context.applicationContext as RussianCompanionApplication).closeStartupStoreForTests()
        val p = Unit8PresentFixtures.pack; val item = p.item(id)!!
        val located = p.units.flatMap { it.sessions }.first { session -> session.steps.any { it.kind == "CHECK" && id in it.itemIds } }
        val index = located.steps.indexOfFirst { it.kind == "CHECK" && id in it.itemIds }
        val selected = mutableStateOf<Map<String, String>>(emptyMap())
        LocalStore.openInMemory(context).use { store ->
            store.v2SaveProfile(store.v2Profile().copy(onboardingCompleted = true, currentUnitNumber = unit))
            compose.setContent {
                V2SessionScreen(p, store.v2LearnerView(), located.id, index, false, selected.value,
                    emptySet(), emptySet(), emptyMap(), {}, { _, _ -> }, { itemId, value, stepId ->
                        store.v2RecordGovernedAttempt(prepareGovernedAttempt(p, itemId, value, PRESCRIBED_CHECK, stepId, "literacy-ui")!!)
                        selected.value = mapOf(governedSelectionKey(stepId, itemId) to value); true
                    }, { _, _ -> }, { _, _ -> }, { _, _ -> }, { _, _ -> }, {}, {}, {}, { _, _ -> }, {})
            }
            val wrongs = item.options.filter { it != item.answer }
            assertEquals(2, wrongs.size)
            assertNotEquals(selectedWrongOptionRationale(item, wrongs[0]), selectedWrongOptionRationale(item, wrongs[1]))
            for (wrong in wrongs) {
                compose.onNodeWithTag("v2-option-$id-$wrong").performScrollTo().performClick().assertIsSelected()
                compose.onNodeWithTag("v2-item-rationale-$id").performScrollTo()
                    .assertTextEquals(selectedWrongOptionRationale(item, wrong)!!).assertIsDisplayed()
                assertEquals(wrong, store.v2Attempts().last().selected)
                assertFalse(store.v2Attempts().last().correct)
                assertFalse(id in creditItemIds(p, store.v2LearnerView()))
            }
            compose.onNodeWithTag("v2-option-$id-${item.answer}").performScrollTo().performClick()
            compose.onNodeWithTag("v2-item-rationale-$id").assertDoesNotExist()
            assertTrue(store.v2Attempts().last().correct)
            assertEquals(item.answer, store.v2Attempts().last().selected)
            assertFalse(advancementDecision(p, p.unit(unit)!!, store.v2LearnerView()).allowed)
        }
    }
}
