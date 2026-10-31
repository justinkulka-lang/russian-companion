package dev.russiancompanion.v2

import android.content.Context
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.v2.backup.*
import dev.russiancompanion.v2.curriculum.*
import dev.russiancompanion.v2.ui.V2CumulativeReviewScreen
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class Unit5GrammarReviewUiTest {
    @get:Rule val compose = createComposeRule()
    @Test fun currentDeclarationDescribesGenderAndSubjectsWithoutClaimingReading() {
        LocalStore.openInMemory(context()).use { store ->
            assertEquals("unit5-prior-gender-subject-v2", cumulativeReviewDeclaredPolicy(pack, 5)!!.id)
            show(store)
            compose.onNodeWithText("Review earlier noun genders and supplied subjects").assertExists()
            compose.onNodeWithText("Review recorded noun genders from Unit 3", substring = true).performScrollTo().assertIsDisplayed()
            compose.onNodeWithTag("v2-review-start").assertDoesNotExist()
        }
    }
    private fun show(store: LocalStore) {
        compose.setContent { V2CumulativeReviewScreen(pack, store.v2LearnerView(), 5, false,
            {}, {}, {}, { _, _, _ -> }, {}, {}, {}) }
    }
    private fun context(): Context = ApplicationProvider.getApplicationContext()
    companion object {
        private val root = File(if (File("app").isDirectory) "." else "..")
        private val pack by lazy { V2PackLoader.parse(File(root, "app/src/main/assets/content/v2-curriculum.json").readText()).also { V2PackIdentityIndex.warm(it) } }
    }
}
