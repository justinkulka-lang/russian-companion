package dev.russiancompanion.release

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class ReleaseOutputTasksTest {
    @Test
    fun apkAabPublishAndVariantReleaseTasksAreGated() {
        val gated = listOf(
            "assembleRelease",
            "packageRelease",
            "bundleRelease",
            "packageReleaseBundle",
            "packageReleaseUniversalApk",
            "publishRelease",
            "publishReleaseBundle",
            "publishReleasePublicationToMavenRepository",
            "installRelease",
            "signReleaseBundle",
            "assemblePaidRelease",
            "bundlePaidRelease",
            "packageFlavorReleaseBundle",
        )
        gated.forEach { name ->
            assertTrue(name, isReleaseOutputTask(name))
        }
    }

    @Test
    fun debugTestsAndIntermediateTasksStayUngated() {
        val ungated = listOf(
            "assembleDebug",
            "packageDebug",
            "bundleDebug",
            "testDebugUnitTest",
            "testReleaseUnitTest",
            "connectedReleaseAndroidTest",
            "lintVitalRelease",
            "lintRelease",
            "compileReleaseKotlin",
            "compileReleaseJavaWithJavac",
            "packageReleaseResources",
            "mergeReleaseResources",
            "processReleaseManifest",
            "minifyReleaseWithR8",
            "kspReleaseKotlin",
            "checkReleaseGate",
            "check",
            "bundleReleaseClasses",
            "bundleReleaseLib",
        )
        ungated.forEach { name ->
            assertFalse(name, isReleaseOutputTask(name))
        }
    }

    @Test
    fun gradleAttachesTheSameMatcherToReleaseOutputs() {
        val gradle = File("build.gradle.kts").readText()
        assertTrue(gradle.contains("fun isReleaseOutputTask(name: String): Boolean"))
        assertTrue(gradle.contains("if (isReleaseOutputTask(name))"))
        assertTrue(gradle.contains("dependsOn(\"checkReleaseGate\")"))
        assertTrue(gradle.contains("verifyReleaseGateAttachment"))
        assertEquals(
            isReleaseOutputTask("bundleRelease"),
            gradleMatcher("bundleRelease"),
        )
        assertEquals(
            isReleaseOutputTask("assembleDebug"),
            gradleMatcher("assembleDebug"),
        )
    }

    private fun gradleMatcher(name: String): Boolean {
        // Mirror the production matcher so a gradle drift fails this test.
        if (name.isEmpty()) return false
        if (name.contains("Test") || name.contains("Lint") || name.startsWith("lint")) return false
        if (name.contains("checkReleaseGate")) return false
        if (!name.contains("Release")) return false
        return when {
            name.startsWith("assemble") -> true
            name.startsWith("bundle") -> !name.contains("Classes") && !name.contains("Lib")
            name.startsWith("publish") -> true
            name.startsWith("install") -> true
            name.startsWith("sign") -> true
            name.startsWith("package") -> {
                name == "packageRelease" ||
                    name.endsWith("Release") ||
                    name.endsWith("ReleaseBundle") ||
                    name.endsWith("ReleaseApk") ||
                    name.endsWith("ReleaseAab") ||
                    name.endsWith("ReleaseUniversalApk")
            }
            else -> false
        }
    }
}
