package dev.russiancompanion.v2.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import kotlinx.coroutines.delay

/** Refresh due queries while visible; scheduling remains a durable accepted-answer operation. */
@Composable
internal fun rememberReviewMinute(): Long {
    val minute by produceState(System.currentTimeMillis() / 60_000) {
        while (true) {
            delay(60_000)
            value = System.currentTimeMillis() / 60_000
        }
    }
    return minute
}
