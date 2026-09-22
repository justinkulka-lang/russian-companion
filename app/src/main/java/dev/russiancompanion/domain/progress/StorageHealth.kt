package dev.russiancompanion.domain.progress

internal data class StorageHealth(
    val databaseReadable: Boolean?,
    val catalogAvailable: Boolean?,
    val packagedAudioCount: Int?,
    val userRecordingCount: Int?,
    val recordingBytes: Long?,
    val missingRecordingCount: Int?,
    val availableBytes: Long?,
    val applicationVersion: String?,
    val bundledContentIdentity: String?,
) {
    companion object {
        fun unavailable(): StorageHealth {
            return StorageHealth(
                databaseReadable = null,
                catalogAvailable = null,
                packagedAudioCount = null,
                userRecordingCount = null,
                recordingBytes = null,
                missingRecordingCount = null,
                availableBytes = null,
                applicationVersion = null,
                bundledContentIdentity = null,
            )
        }
    }
}

internal fun bundledContentIdentity(contentVersion: Int?, contentHash: String?): String? {
    if (contentVersion == null && contentHash.isNullOrBlank()) return null
    val version = contentVersion?.toString() ?: "unavailable"
    val hash = contentHash?.takeIf { it.isNotBlank() } ?: "unavailable"
    return "contentVersion=$version;contentHash=$hash"
}

internal fun recordingStorage(
    sizes: List<Long?>,
): Pair<Long?, Int> {
    var total = 0L
    var missing = 0
    var anyKnown = false
    for (size in sizes) {
        if (size == null || size <= 0L) {
            missing += 1
        } else {
            total += size
            anyKnown = true
        }
    }
    return (if (anyKnown || sizes.isEmpty()) total else null) to missing
}
