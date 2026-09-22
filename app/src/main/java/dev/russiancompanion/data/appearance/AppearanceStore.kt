package dev.russiancompanion.data.appearance

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dev.russiancompanion.domain.appearance.AppearancePreference
import dev.russiancompanion.domain.appearance.appearancePreferenceFromStored
import dev.russiancompanion.domain.appearance.defaultAppearancePreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private val Context.appearanceDataStore by preferencesDataStore(name = "appearance")

private val APPEARANCE_KEY = stringPreferencesKey("appearance_preference")

internal class AppearanceStore(
    val preference: Flow<AppearancePreference>,
    private val persist: suspend (AppearancePreference) -> Result<Unit>,
) {
    suspend fun set(preference: AppearancePreference): Result<Unit> = persist(preference)

    companion object {
        fun memory(
            initial: AppearancePreference = defaultAppearancePreference(),
        ): AppearanceStore {
            val stored = MutableStateFlow(initial)
            return AppearanceStore(
                preference = stored,
                persist = { next ->
                    stored.value = next
                    Result.success(Unit)
                },
            )
        }

        fun failing(
            initial: AppearancePreference = defaultAppearancePreference(),
            readFails: Boolean = false,
            writeFails: Boolean = false,
        ): AppearanceStore {
            val stored = MutableStateFlow(initial)
            val flow = if (readFails) {
                kotlinx.coroutines.flow.flow<AppearancePreference> {
                    throw IllegalStateException("appearance-unreadable")
                }.catch { emit(defaultAppearancePreference()) }
            } else {
                stored
            }
            return AppearanceStore(
                preference = flow,
                persist = { next ->
                    if (writeFails) {
                        Result.failure(IllegalStateException("appearance-not-saved"))
                    } else {
                        stored.value = next
                        Result.success(Unit)
                    }
                },
            )
        }

        fun onDisk(context: Context): AppearanceStore {
            val appContext = context.applicationContext
            return AppearanceStore(
                preference = appContext.appearanceDataStore.data
                    .map { prefs -> appearancePreferenceFromStored(prefs[APPEARANCE_KEY]) }
                    .catch { emit(defaultAppearancePreference()) },
                persist = { preference ->
                    runCatching {
                        appContext.appearanceDataStore.edit { prefs ->
                            prefs[APPEARANCE_KEY] = preference.name
                        }
                    }
                },
            )
        }
    }
}