package dev.russiancompanion.data.appearance

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import dev.russiancompanion.domain.appearance.AppearancePreference
import dev.russiancompanion.domain.appearance.defaultAppearancePreference
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class AppearanceStoreTest {
    @Test
    fun darkIsDefaultAndFollowSystemPersistsAcrossRestart() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val first = AppearanceStore.onDisk(context)
        assertEquals(defaultAppearancePreference(), first.preference.first())
        assertTrue(first.set(AppearancePreference.FOLLOW_SYSTEM).isSuccess)
        val restarted = AppearanceStore.onDisk(context)
        assertEquals(AppearancePreference.FOLLOW_SYSTEM, restarted.preference.first())
    }

    @Test
    fun readFailureFallsBackToDark() = runBlocking {
        val store = AppearanceStore.failing(readFails = true)
        assertEquals(AppearancePreference.DARK, store.preference.first())
    }

    @Test
    fun writeFailureKeepsThePriorPreference() = runBlocking {
        val store = AppearanceStore.failing(
            initial = AppearancePreference.DARK,
            writeFails = true,
        )
        val result = store.set(AppearancePreference.FOLLOW_SYSTEM)
        assertTrue(result.isFailure)
        assertEquals(AppearancePreference.DARK, store.preference.first())
    }
}
