package dev.russiancompanion.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.view.WindowCompat
import dev.russiancompanion.RussianCompanionApplication
import dev.russiancompanion.v2.curriculum.V2PackLoader
import dev.russiancompanion.v2.ui.V2App

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        val app = application as RussianCompanionApplication
        val pack = runCatching { V2PackLoader.load(this) }.getOrNull()
        setContent {
            var store by remember { mutableStateOf(app.localStore) }
            V2App(
                store = store,
                pack = pack,
                onRetryOpen = {
                    app.retryOpen()
                    store = app.localStore
                },
                appearanceStore = app.appearanceStore,
            )
        }
    }

    override fun onStop() {
        super.onStop()
        val app = application as RussianCompanionApplication
        app.practiceAudio.releaseForBackground()
    }
}
