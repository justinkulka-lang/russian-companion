package dev.russiancompanion.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import dev.russiancompanion.RussianCompanionApplication
import dev.russiancompanion.presentation.navigation.CompanionApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = application as RussianCompanionApplication
        setContent {
            var gateway by remember { mutableStateOf(app.gateway()) }
            CompanionApp(
                gateway = gateway,
                onRetryOpen = { gateway = app.retryOpen() },
                practiceAudio = app.practiceAudio,
            )
        }
    }

    override fun onStop() {
        super.onStop()
        val app = application as RussianCompanionApplication
        app.practiceAudio.releaseForBackground()
    }
}
