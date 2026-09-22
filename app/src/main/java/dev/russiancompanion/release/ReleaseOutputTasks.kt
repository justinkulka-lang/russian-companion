package dev.russiancompanion.release

/**
 * Release artifact tasks that must fail closed through [checkReleaseGate].
 * Intermediate compile/package-resources tasks are excluded.
 */
internal fun isReleaseOutputTask(name: String): Boolean {
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
