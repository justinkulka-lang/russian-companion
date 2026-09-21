package dev.russiancompanion

import android.app.Application
import dev.russiancompanion.data.audio.AndroidPracticeAudio
import dev.russiancompanion.data.export.readAppRecording
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.data.navigation.RoomNavigationGateway
import dev.russiancompanion.domain.navigation.NavigationGateway
import dev.russiancompanion.domain.practice.PracticeAudio

class RussianCompanionApplication : Application() {
    internal val practiceAudio: PracticeAudio by lazy { AndroidPracticeAudio(this) }

    internal var localStore: LocalStore? = null
        private set

    var localDataFailedToOpen: Boolean = false
        private set

    override fun onCreate() {
        super.onCreate()
        openStore()
    }

    internal fun gateway(): NavigationGateway? {
        val store = localStore ?: return null
        return RoomNavigationGateway(
            store,
            { LocalStore.readPackagedSeed(this) },
            practiceAudio,
            readRecordingBytes = { reference -> readAppRecording(this, reference) },
        )
    }

    internal fun retryOpen(): NavigationGateway? {
        val opened = try {
            LocalStore.open(this)
        } catch (_: Exception) {
            localDataFailedToOpen = localStore == null
            return gateway()
        }
        val previous = localStore
        localStore = opened
        previous?.close()
        localDataFailedToOpen = false
        return gateway()
    }

    private fun openStore() {
        try {
            localStore = LocalStore.open(this)
            localDataFailedToOpen = false
        } catch (_: Exception) {
            localStore = null
            localDataFailedToOpen = true
        }
    }
}
