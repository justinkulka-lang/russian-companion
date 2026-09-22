package dev.russiancompanion.release

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.File
import java.security.MessageDigest

internal const val ACCEPTANCE_CANDIDATE_PATH = "release/acceptance/candidate.json"

internal const val CANDIDATE_SCHEMA_VERSION = 4

private val includedRoots = listOf(
    "app/src/main",
    "app/build.gradle.kts",
    "app/schemas",
    "build.gradle.kts",
    "settings.gradle.kts",
    "gradle.properties",
    "gradle/libs.versions.toml",
    "gradle/wrapper/gradle-wrapper.properties",
    "catalog",
    "docs/governance",
    "docs/design",
    ".github/workflows",
    "release/acceptance/DEVICE_ACCEPTANCE.md",
    "release/acceptance/templates",
)

internal data class ReleaseBinding(
    val sourceDigest: String,
    val applicationId: String,
    val versionName: String,
    val versionCode: Int,
)

internal data class FrozenCandidateIdentity(
    val sourceCommit: String,
    val apkSha256: String,
    val apkBytes: Long,
    val runId: String,
)

@Serializable
internal data class AcceptanceCandidateDocument(
    val schemaVersion: Int,
    val applicationId: String,
    val versionName: String,
    val versionCode: Int,
    val sourceDigest: String,
    val publicRelease: String,
    val frozen: Boolean = false,
    val sourceCommit: String? = null,
    val artifact: CandidateArtifactIdentity? = null,
)

@Serializable
internal data class CandidateArtifactIdentity(
    val status: String,
    val sha256: String? = null,
    val apkBytes: Long? = null,
    val runId: String? = null,
    val runUrl: String? = null,
)

private val candidateJson = Json {
    ignoreUnknownKeys = true
    explicitNulls = true
}

internal fun currentReleaseBinding(repositoryRoot: File): ReleaseBinding? {
    val identity = readReleaseIdentity(repositoryRoot) ?: return null
    if (!releaseInputsPresent(repositoryRoot)) return null
    return ReleaseBinding(
        sourceDigest = computeReleaseSourceDigest(repositoryRoot),
        applicationId = identity.applicationId,
        versionName = identity.versionName,
        versionCode = identity.versionCode,
    )
}

internal fun readReleaseIdentity(repositoryRoot: File): ReleaseBinding? {
    val gradle = File(repositoryRoot, "app/build.gradle.kts")
    if (!gradle.isFile) return null
    val text = gradle.readText()
    val applicationId = Regex("""applicationId\s*=\s*"([^"]+)"""").find(text)?.groupValues?.get(1)
    val versionName = Regex("""versionName\s*=\s*"([^"]+)"""").find(text)?.groupValues?.get(1)
    val versionCode = Regex("""versionCode\s*=\s*(\d+)""").find(text)?.groupValues?.get(1)?.toIntOrNull()
    if (applicationId.isNullOrBlank() || versionName.isNullOrBlank() || versionCode == null || versionCode < 1) {
        return null
    }
    return ReleaseBinding(
        sourceDigest = "",
        applicationId = applicationId,
        versionName = versionName,
        versionCode = versionCode,
    )
}

internal fun readAcceptanceCandidate(repositoryRoot: File): AcceptanceCandidateDocument? {
    val file = File(repositoryRoot, ACCEPTANCE_CANDIDATE_PATH)
    if (!file.isFile || file.length() == 0L) return null
    return try {
        val parsed = candidateJson.decodeFromString(AcceptanceCandidateDocument.serializer(), file.readText())
        if (parsed.schemaVersion != CANDIDATE_SCHEMA_VERSION) null else parsed
    } catch (_: SerializationException) {
        null
    } catch (_: IllegalArgumentException) {
        null
    }
}

internal fun candidateMatchesBinding(
    candidate: AcceptanceCandidateDocument?,
    binding: ReleaseBinding,
): Boolean {
    if (candidate == null) return false
    return candidate.sourceDigest == binding.sourceDigest &&
        candidate.applicationId == binding.applicationId &&
        candidate.versionName == binding.versionName &&
        candidate.versionCode == binding.versionCode &&
        SHA256.matches(candidate.sourceDigest)
}

internal fun recordedCandidateIdentity(candidate: AcceptanceCandidateDocument?): FrozenCandidateIdentity? {
    if (candidate == null || !candidate.frozen) return null
    val commit = candidate.sourceCommit ?: return null
    val artifact = candidate.artifact ?: return null
    val sha256 = artifact.sha256 ?: return null
    val apkBytes = artifact.apkBytes ?: return null
    val runId = artifact.runId?.trim().orEmpty()
    if (artifact.status != "RECORDED") return null
    if (!GIT_COMMIT.matches(commit)) return null
    if (!SHA256.matches(sha256)) return null
    if (apkBytes <= 0L || runId.isBlank()) return null
    return FrozenCandidateIdentity(
        sourceCommit = commit,
        apkSha256 = sha256,
        apkBytes = apkBytes,
        runId = runId,
    )
}

internal fun computeReleaseSourceDigest(
    repositoryRoot: File,
    replacements: Map<String, ByteArray> = emptyMap(),
): String {
    val digest = MessageDigest.getInstance("SHA-256")
    val relatives = linkedSetOf<String>()
    relatives += listReleaseSourceRelatives(repositoryRoot)
    for (relative in replacements.keys) {
        if (isReleaseSourceRelative(relative)) relatives += relative
    }
    for (relative in relatives.sorted()) {
        val bytes = replacements[relative] ?: File(repositoryRoot, relative).readBytes()
        updateDigest(digest, relative, bytes)
    }
    return hex(digest.digest())
}

internal fun listReleaseSourceRelatives(repositoryRoot: File): List<String> {
    val relatives = mutableListOf<String>()
    val root = repositoryRoot.canonicalFile
    for (included in includedRoots) {
        val file = File(root, included)
        if (file.isFile) {
            if (isReleaseSourceRelative(included)) relatives += included
        } else if (file.isDirectory) {
            file.walkTopDown().filter { it.isFile }.forEach { child ->
                val relative = child.relativeTo(root).invariantSeparatorsPath
                if (isReleaseSourceRelative(relative)) relatives += relative
            }
        }
    }
    return relatives.distinct().sorted()
}

internal fun isReleaseSourceRelative(relative: String): Boolean {
    val path = relative.replace('\\', '/')
    if (path.isBlank() || path.startsWith("/") || path.contains("/../") || path.endsWith("/..")) {
        return false
    }
    if (isExcludedReleaseSource(path)) return false
    return includedRoots.any { prefix -> path == prefix || path.startsWith("$prefix/") }
}

internal fun isExcludedReleaseSource(relative: String): Boolean {
    val path = relative.replace('\\', '/')
    return path.startsWith("release/evidence/") ||
        path == "release/retained-evidence.json" ||
        path == ACCEPTANCE_CANDIDATE_PATH ||
        path.startsWith("release/acceptance/forms/") ||
        path.startsWith("catalog/audio-cache/") ||
        path.contains("/__pycache__/") ||
        path.startsWith("__pycache__/") ||
        path.endsWith(".pyc") ||
        path.endsWith(".DS_Store")
}

private fun releaseInputsPresent(repositoryRoot: File): Boolean {
    val gradle = File(repositoryRoot, "app/build.gradle.kts")
    val main = File(repositoryRoot, "app/src/main")
    return gradle.isFile && main.isDirectory && listReleaseSourceRelatives(repositoryRoot).isNotEmpty()
}

private fun updateDigest(digest: MessageDigest, relative: String, bytes: ByteArray) {
    val pathBytes = relative.replace('\\', '/').toByteArray(Charsets.UTF_8)
    digest.update(intBytes(pathBytes.size))
    digest.update(pathBytes)
    digest.update(intBytes(bytes.size))
    digest.update(bytes)
}

private fun intBytes(value: Int): ByteArray {
    return byteArrayOf(
        (value ushr 24).toByte(),
        (value ushr 16).toByte(),
        (value ushr 8).toByte(),
        value.toByte(),
    )
}

private fun hex(bytes: ByteArray): String {
    return bytes.joinToString("") { byte -> "%02x".format(byte) }
}

internal val SHA256 = Regex("^[a-f0-9]{64}$")

internal val GIT_COMMIT = Regex("^[a-f0-9]{40}$")
