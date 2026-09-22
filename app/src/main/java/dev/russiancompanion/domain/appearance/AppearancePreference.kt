package dev.russiancompanion.domain.appearance

internal enum class AppearancePreference {
    DARK,
    FOLLOW_SYSTEM,
}

internal fun defaultAppearancePreference(): AppearancePreference = AppearancePreference.DARK

internal fun appearancePreferenceFromStored(value: String?): AppearancePreference {
    return when (value) {
        AppearancePreference.FOLLOW_SYSTEM.name -> AppearancePreference.FOLLOW_SYSTEM
        else -> AppearancePreference.DARK
    }
}

internal fun lightOnlyAppearanceForbidden(option: String): Boolean {
    return option.equals("LIGHT", ignoreCase = true) ||
        option.equals("LIGHT_ONLY", ignoreCase = true)
}
