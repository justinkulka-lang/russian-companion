package dev.russiancompanion.v2.ui

import android.os.Build
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

internal fun snapshotDispatcher(): CoroutineDispatcher {
    return if (isRobolectricRuntime()) {
        Dispatchers.Unconfined
    } else {
        Dispatchers.IO
    }
}

internal fun isRobolectricRuntime(): Boolean {
    return Build.FINGERPRINT.contains("robolectric", ignoreCase = true)
}
