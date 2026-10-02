package dev.russiancompanion.v2.ui

import dev.russiancompanion.presentation.navigation.isRobolectricRuntime

/**
 * Robolectric UI tests run on the main thread and cannot pump it while a
 * coroutine is waiting. They complete store work inline unless a test opts
 * into the asynchronous path. Production never takes the inline path.
 */
internal object V2UiWork {
    @Volatile
    var blockForTests: Boolean? = null

    fun blocking(): Boolean = blockForTests ?: isRobolectricRuntime()
}
