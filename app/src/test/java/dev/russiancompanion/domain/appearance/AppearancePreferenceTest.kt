package dev.russiancompanion.domain.appearance

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AppearancePreferenceTest {
    @Test
    fun darkIsTheDefaultForUnknownOrMissingValues() {
        assertEquals(AppearancePreference.DARK, defaultAppearancePreference())
        assertEquals(AppearancePreference.DARK, appearancePreferenceFromStored(null))
        assertEquals(AppearancePreference.DARK, appearancePreferenceFromStored("LIGHT"))
        assertEquals(AppearancePreference.FOLLOW_SYSTEM, appearancePreferenceFromStored("FOLLOW_SYSTEM"))
        assertTrue(lightOnlyAppearanceForbidden("LIGHT"))
        assertFalse(lightOnlyAppearanceForbidden("DARK"))
    }
}
