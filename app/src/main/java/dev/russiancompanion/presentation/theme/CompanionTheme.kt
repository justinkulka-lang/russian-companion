package dev.russiancompanion.presentation.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import dev.russiancompanion.domain.appearance.AppearancePreference

internal val CompanionDarkColors = darkColorScheme(
    primary = Color(0xFFE4D6C0),
    onPrimary = Color(0xFF1C160C),
    primaryContainer = Color(0xFF3D3426),
    onPrimaryContainer = Color(0xFFF4E8D6),
    secondary = Color(0xFFB8C4C0),
    onSecondary = Color(0xFF121212),
    background = Color(0xFF121212),
    onBackground = Color(0xFFF4EFE7),
    surface = Color(0xFF1C1B1A),
    onSurface = Color(0xFFF4EFE7),
    surfaceVariant = Color(0xFF2C2A28),
    onSurfaceVariant = Color(0xFFE3DCD3),
    outline = Color(0xFF9A9188),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
)

internal val CompanionLightColors = lightColorScheme(
    primary = Color(0xFF5C4A32),
    onPrimary = Color(0xFFFFF8EE),
    primaryContainer = Color(0xFFE8D7C0),
    onPrimaryContainer = Color(0xFF241A0D),
    background = Color(0xFFF7F2EA),
    onBackground = Color(0xFF1C1B1A),
    surface = Color(0xFFFFFBF6),
    onSurface = Color(0xFF1C1B1A),
    surfaceVariant = Color(0xFFE8E1D7),
    onSurfaceVariant = Color(0xFF4A453E),
    outline = Color(0xFF7A736B),
)

@Composable
internal fun CompanionTheme(
    appearance: AppearancePreference,
    content: @Composable () -> Unit,
) {
    val followSystemDark = isSystemInDarkTheme()
    val useDark = appearance == AppearancePreference.DARK ||
        (appearance == AppearancePreference.FOLLOW_SYSTEM && followSystemDark)
    val colors = if (useDark) CompanionDarkColors else CompanionLightColors
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val activity = view.context as? Activity ?: return@SideEffect
            val window = activity.window
            window.statusBarColor = colors.background.toArgb()
            window.navigationBarColor = colors.background.toArgb()
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = !useDark
            controller.isAppearanceLightNavigationBars = !useDark
        }
    }
    MaterialTheme(colorScheme = colors) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground,
            content = content,
        )
    }
}
