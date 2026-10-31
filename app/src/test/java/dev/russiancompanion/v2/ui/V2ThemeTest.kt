package dev.russiancompanion.v2.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.test.junit4.v2.createComposeRule
import dev.russiancompanion.domain.appearance.AppearancePreference
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class V2ThemeTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun darkThemeUsesDarkBackgroundAndReadableForegroundRoles() {
        var background = 0f
        var onBackground = 0f
        var onSurface = 0f
        var onPrimary = 0f
        compose.setContent {
            CompanionTheme(AppearancePreference.DARK) {
                val colors = MaterialTheme.colorScheme
                background = colors.background.luminance()
                onBackground = colors.onBackground.luminance()
                onSurface = colors.onSurface.luminance()
                onPrimary = colors.onPrimary.luminance()
            }
        }
        assertTrue(background < 0.2f)
        assertTrue(onBackground > 0.7f)
        assertTrue(onSurface > 0.7f)
        assertTrue(onPrimary < 0.3f)
    }
}
