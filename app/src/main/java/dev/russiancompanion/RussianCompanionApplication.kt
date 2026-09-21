package dev.russiancompanion

import android.app.Application
import dev.russiancompanion.data.local.LocalStore

class RussianCompanionApplication : Application() {
    internal var localStore: LocalStore? = null
        private set

    var localDataFailedToOpen: Boolean = false
        private set

    override fun onCreate() {
        super.onCreate()
        try {
            localStore = LocalStore.open(this)
        } catch (_: Exception) {
            localDataFailedToOpen = true
        }
    }
}
