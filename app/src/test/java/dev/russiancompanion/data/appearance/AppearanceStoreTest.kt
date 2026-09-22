package dev.russiancompanion.data.appearance

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.domain.appearance.AppearancePreference
import dev.russiancompanion.domain.appearance.defaultAppearancePreference
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class AppearanceStoreTest {
    @Test
    fun followSystemPersistsAcrossANewStoreInstance() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val first = AppearanceStore.onDisk(context)
        assertEquals(defaultAppearancePreference(), first.current())
        first.set(AppearancePreference.FOLLOW_SYSTEM)
        val restarted = AppearanceStore.onDisk(context)
        assertEquals(AppearancePreference.FOLLOW_SYSTEM, restarted.current())
    }
}
