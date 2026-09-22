package dev.russiancompanion

import org.junit.Assert.assertFalse
import org.junit.Test
import java.io.File

class ScaffoldBoundaryTest {
    @Test
    fun manifestDoesNotRequestNetworkOrExternalApps() {
        val manifest = manifestFile().readText()
        assertFalse(manifest.contains("android.permission.INTERNET"))
        assertFalse(manifest.contains("android.permission.ACCESS_NETWORK_STATE"))
        assertFalse(manifest.contains("com.ichi2.anki"))
    }

    private fun manifestFile(): File {
        val candidates = listOf(
            File("src/main/AndroidManifest.xml"),
            File("app/src/main/AndroidManifest.xml"),
        )
        return candidates.firstOrNull { it.isFile }
            ?: error("AndroidManifest.xml was not found from ${File(".").absolutePath}")
    }
}
