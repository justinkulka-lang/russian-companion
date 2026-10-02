package dev.russiancompanion.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.core.view.WindowCompat
import dev.russiancompanion.RussianCompanionApplication
import dev.russiancompanion.v2.curriculum.V2PackLoader
import dev.russiancompanion.v2.ui.V2App
import dev.russiancompanion.v2.ui.V2StartupHost
import dev.russiancompanion.v2.ui.V2StartupResult

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        val app = application as RussianCompanionApplication
        setContent {
            var store by remember { mutableStateOf(app.localStore) }
            val forceRetry = remember { java.util.concurrent.atomic.AtomicBoolean(false) }
            val scope = rememberCoroutineScope()
            V2StartupHost(
                load = {
                    val opened = app.awaitLocalStore(force = forceRetry.getAndSet(false))
                    val loaded = runCatching { V2PackLoader.load(this@MainActivity) }
                    V2StartupResult(
                        store = opened,
                        pack = loaded.getOrNull(),
                        message = when {
                            opened == null -> "Local data could not be opened."
                            loaded.isFailure -> "The curriculum pack is unavailable."
                            else -> ""
                        },
                    )
                },
                onRetryOpen = { forceRetry.set(true) },
            ) { opened, pack ->
                V2App(
                    store = store ?: opened,
                    pack = pack,
                    onRetryOpen = {
                        scope.launch {
                            val openedStore = withContext(Dispatchers.IO) {
                                app.retryOpen()
                                app.localStore
                            }
                            store = openedStore
                        }
                    },
                    appearanceStore = app.appearanceStore,
                )
            }
        }
    }

    override fun onStop() {
        super.onStop()
        val app = application as RussianCompanionApplication
        app.practiceAudio.releaseForBackground()
    }
}
