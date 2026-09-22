package dev.russiancompanion.data.appearance

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dev.russiancompanion.domain.appearance.AppearancePreference
import dev.russiancompanion.domain.appearance.appearancePreferenceFromStored
import dev.russiancompanion.domain.appearance.defaultAppearancePreference
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

private val Context.appearanceDataStore by preferencesDataStore(name = "appearance")

private val APPEARANCE_KEY = stringPreferencesKey("appearance_preference")

internal class AppearanceStore(
    private val load: () -> AppearancePreference,
    private val persist: (AppearancePreference) -> Unit,
) {
    fun current(): AppearancePreference = load()

    fun set(preference: AppearancePreference) {
        persist(preference)
    }

    companion object {
        fun memory(
            initial: AppearancePreference = defaultAppearancePreference(),
        ): AppearanceStore {
            var stored = initial
            return AppearanceStore(load = { stored }, persist = { stored = it })
        }

        fun onDisk(context: Context): AppearanceStore {
            val appContext = context.applicationContext
            return AppearanceStore(
                load = {
                    runBlocking {
                        val stored = appContext.appearanceDataStore.data.first()[APPEARANCE_KEY]
                        appearancePreferenceFromStored(stored)
                    }
                },
                persist = { preference ->
                    runBlocking {
                        appContext.appearanceDataStore.edit { prefs ->
                            prefs[APPEARANCE_KEY] = preference.name
                        }
                    }
                },
            )
        }
    }
}
