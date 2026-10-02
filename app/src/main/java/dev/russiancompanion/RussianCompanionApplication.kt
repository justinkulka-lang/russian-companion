package dev.russiancompanion

import android.app.Application
import android.os.StatFs
import java.util.concurrent.Executors
import dev.russiancompanion.data.appearance.AppearanceStore
import dev.russiancompanion.data.audio.AndroidPracticeAudio
import dev.russiancompanion.data.export.openAppRecording
import dev.russiancompanion.data.local.LocalStore
import dev.russiancompanion.data.navigation.RoomNavigationGateway
import dev.russiancompanion.domain.navigation.NavigationGateway
import dev.russiancompanion.domain.practice.PracticeAudio

class RussianCompanionApplication : Application() {
    internal val practiceAudio: PracticeAudio by lazy { AndroidPracticeAudio(this) }

    internal val appearanceStore: AppearanceStore by lazy { AppearanceStore.onDisk(this) }

    internal var localStore: LocalStore? = null
        private set

    var localDataFailedToOpen: Boolean = false
        private set

    private val openLock = Any()
    private val startup = Executors.newSingleThreadExecutor { runnable ->
        Thread(runnable, "russian-companion-startup").apply { isDaemon = true }
    }
    private var storeOpenAttempted = false

    override fun onCreate() {
        super.onCreate()
        startup.execute { awaitLocalStore() }
    }

    internal fun awaitLocalStore(force: Boolean = false): LocalStore? {
        if (force) {
            retryOpen()
            return localStore
        }
        synchronized(openLock) {
            if (localStore != null) return localStore
            if (storeOpenAttempted && localDataFailedToOpen) return null
            storeOpenAttempted = true
            openStore()
            return localStore
        }
    }

    internal fun gateway(): NavigationGateway? {
        val store = localStore ?: return null
        return RoomNavigationGateway(
            store,
            { LocalStore.readPackagedSeed(this) },
            practiceAudio,
            openRecording = { reference -> openAppRecording(this, reference) },
            readCatalogNotice = { LocalStore.readPackagedCatalogNotice(this) },
            readLicenseNotices = { LocalStore.readPackagedLicenseNotices(this) },
            availableBytes = {
                try {
                    StatFs(filesDir.absolutePath).availableBytes
                } catch (_: Exception) {
                    null
                }
            },
            applicationVersion = BuildConfig.VERSION_NAME,
        )
    }

    internal fun retryOpen(): NavigationGateway? {
        synchronized(openLock) {
            val opened = try {
                LocalStore.open(this)
            } catch (_: Exception) {
                localDataFailedToOpen = localStore == null
                storeOpenAttempted = true
                return gateway()
            }
            val previous = localStore
            localStore = opened
            if (previous != null && previous != opened) previous.close()
            localDataFailedToOpen = false
            storeOpenAttempted = true
            return gateway()
        }
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
