package dev.russiancompanion.v2

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.Density
import dev.russiancompanion.v2.curriculum.*
import dev.russiancompanion.v2.ui.V2ReferenceDetailScreen
import dev.russiancompanion.v2.ui.V2ReferenceScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class ReferenceDetailSearchUiTest {
    @get:Rule val compose = createComposeRule()

    @Test fun russianDetailQueryOpensTheOriginalExplanationAtLargeText() {
        val p = V2PackLoader.parse(File(if (File("app").isDirectory) "." else "..",
            "app/src/main/assets/content/v2-curriculum.json").readText())
        val entry = p.references.single { it.id == "ref-ca-letter-names" }
        compose.setContent {
            var openId by remember { mutableStateOf<String?>(null) }
            CompositionLocalProvider(LocalDensity provides Density(LocalDensity.current.density, fontScale = 1.8f)) {
                MaterialTheme {
                    if (openId == null) {
                        V2ReferenceScreen(p, referenceSearchLearner(1), {}, { openId = it })
                    } else {
                        V2ReferenceDetailScreen(p, openId!!, { openId = null }, { _, _ -> })
                    }
                }
            }
        }
        compose.onNodeWithTag("v2-reference-search").performTextInput("и КРАТКОЕ")
        compose.onNodeWithText(entry.title).performScrollTo().assertIsDisplayed().performClick()
        compose.onNodeWithText(entry.detail).performScrollTo().assertIsDisplayed()
    }
}
