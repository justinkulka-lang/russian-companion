package dev.russiancompanion.v2.notices

import android.content.Context
import dev.russiancompanion.v2.curriculum.V2CurriculumPack
import dev.russiancompanion.v2.curriculum.V2SourceRecord
import dev.russiancompanion.v2.curriculum.installed
import java.io.File
import java.io.IOException

internal data class PackagedNoticeFile(val name: String, val body: String)

internal data class OfflineSourceNotice(
    val id: String,
    val creatorOrProject: String,
    val license: String,
    val attribution: String,
    val modification: String,
    val licenseUrls: List<String>,
    val shareAlike: String,
    val availability: String,
)

internal data class OfflineLicenseNotice(
    val name: String,
    val title: String,
    val body: String,
    val availability: String,
)

internal data class OfflineNoticeCatalog(
    val sources: List<OfflineSourceNotice>,
    val licenses: List<OfflineLicenseNotice>,
    val penguinStatement: String,
)

internal const val PENGUIN_NOT_REDISTRIBUTED =
    "Penguin books are not part of this app. The app does not redistribute Penguin text."

internal fun loadOfflineNotices(context: Context, pack: V2CurriculumPack): OfflineNoticeCatalog {
    val files = mutableListOf<PackagedNoticeFile>()
    val licenseNames = try {
        context.assets.list("licenses")?.sorted().orEmpty()
    } catch (_: IOException) {
        emptyList()
    }
    for (name in licenseNames) {
        val body = readAsset(context, "licenses/$name") ?: continue
        if (body.isNotBlank()) files += PackagedNoticeFile(name, body)
    }
    val catalog = readAsset(context, "content/catalog-notice.txt")
    if (!catalog.isNullOrBlank()) files += PackagedNoticeFile("catalog-notice.txt", catalog)
    return offlineNoticeCatalog(pack, files)
}

internal fun offlineNoticeCatalog(pack: V2CurriculumPack, files: List<PackagedNoticeFile>): OfflineNoticeCatalog {
    val byName = files.associateBy { it.name }
    val sources = pack.sources.map { source -> sourceNotice(pack, source) }
    val licenses = files.map { file ->
        OfflineLicenseNotice(
            name = file.name,
            title = licenseTitle(file.name),
            body = file.body,
            availability = licenseAvailability(file.name, byName),
        )
    }
    return OfflineNoticeCatalog(
        sources = sources,
        licenses = licenses,
        penguinStatement = PENGUIN_NOT_REDISTRIBUTED,
    )
}

internal fun packagedNoticeErrors(repositoryRoot: File, pack: V2CurriculumPack): List<String> {
    val errors = mutableListOf<String>()
    val files = packagedNoticeFiles(repositoryRoot)
    val byName = files.associateBy { it.name }
    requiredNoticeNames(pack).forEach { name ->
        val body = byName[name]?.body
        if (body.isNullOrBlank()) errors += "Required attribution notice is not packaged ($name)"
    }
    pack.sources.forEach { source ->
        if (source.attribution.isBlank()) errors += "Source ${source.id} has no attribution"
        if (penguinRedistributionClaim(source.title) || penguinRedistributionClaim(source.attribution)) {
            errors += "Source ${source.id} presents Penguin material as redistributed content"
        }
    }
    val catalog = offlineNoticeCatalog(pack, files)
    if (catalog.sources.map { it.id } != pack.sources.map { it.id }) {
        errors += "Offline attribution notices do not match the source records"
    }
    if (catalog.penguinStatement != PENGUIN_NOT_REDISTRIBUTED) {
        errors += "Penguin redistribution notice is missing"
    }
    return errors.distinct()
}

internal fun penguinRedistributionClaim(text: String): Boolean {
    val value = text.lowercase()
    if (!value.contains("penguin")) return false
    if (value.contains("no penguin")) return false
    return value.contains("redistribut") || value.contains("excerpt") || value.contains("reproduced")
}

private fun sourceNotice(pack: V2CurriculumPack, source: V2SourceRecord): OfflineSourceNotice {
    val units = pack.provenanceRecords.filter { it.sourceId == source.id }.map { it.unitNumber }.toSet()
    val urls = pack.provenanceRecords.filter { it.sourceId == source.id }.map { it.licenseUrl }.filter { it.isNotBlank() }.distinct()
    val treatments = pack.provenanceRecords.filter { it.sourceId == source.id }.map { it.shareAlikeTreatment }.filter { it.isNotBlank() }.distinct()
    val shareAlike = when {
        treatments.isNotEmpty() -> treatments.joinToString("\n")
        source.license.contains("CC BY-SA") ->
            "CC BY-SA 4.0. The legal code is packaged in this app as cc-by-sa-4.0.txt."
        else -> "No share-alike obligation is recorded for this source."
    }
    return OfflineSourceNotice(
        id = source.id,
        creatorOrProject = source.title,
        license = source.license,
        attribution = source.attribution,
        modification = source.modification,
        licenseUrls = urls,
        shareAlike = shareAlike,
        availability = availability(pack, units),
    )
}

private fun availability(pack: V2CurriculumPack, unitNumbers: Set<Int>): String {
    if (unitNumbers.isEmpty()) return "Not referenced by a course unit."
    val units = unitNumbers.mapNotNull { pack.units.firstOrNull { unit -> unit.number == it } }
    return when {
        units.any { it.installed() } -> "Installed in this app."
        units.isNotEmpty() -> "In development. Present in this pack and not a release installation."
        else -> "Not installed."
    }
}

private fun licenseAvailability(name: String, files: Map<String, PackagedNoticeFile>): String {
    return when (name) {
        "catalog-notice.txt",
        "cc-by-3.0-us.html",
        "cc-by-2.0-fr.html",
        "public-domain-notice.txt",
        "shtooka-readme-sakhno.txt",
        "shtooka-readme-streit.txt" ->
            "Packaged rights archive. Not installed as course audio."
        "units-3-5-curriculum-notice.txt",
        "units-6-10-curriculum-notice.txt",
        "units-26-30-curriculum-notice.txt" ->
            "Curriculum notice for units that are not a release installation."
        else -> if (files[name]?.body.isNullOrBlank()) "Notice text is missing." else "Packaged license text."
    }
}

private fun licenseTitle(name: String): String {
    return when (name) {
        "cc-by-sa-4.0.txt" -> "CC BY-SA 4.0 legal code"
        "cc-by-3.0-us.html" -> "CC BY 3.0 US legal code"
        "cc-by-2.0-fr.html" -> "CC BY 2.0 FR legal code"
        "public-domain-notice.txt" -> "Public-domain mark"
        "shtooka-readme-sakhno.txt" -> "Shtooka README (Sakhno)"
        "shtooka-readme-streit.txt" -> "Shtooka README (Streit)"
        "units-3-5-curriculum-notice.txt" -> "Units 3-5 curriculum notice"
        "units-6-10-curriculum-notice.txt" -> "Units 6-10 curriculum notice"
        "units-26-30-curriculum-notice.txt" -> "Units 26-30 curriculum notice"
        "catalog-notice.txt" -> "Packaged catalog notice"
        else -> name
    }
}

private fun requiredNoticeNames(pack: V2CurriculumPack): List<String> {
    val names = mutableListOf(
        "cc-by-sa-4.0.txt",
        "cc-by-3.0-us.html",
        "cc-by-2.0-fr.html",
        "public-domain-notice.txt",
        "shtooka-readme-sakhno.txt",
        "shtooka-readme-streit.txt",
        "catalog-notice.txt",
    )
    val units = pack.provenanceRecords.map { it.unitNumber }.toSet()
    if (units.any { it in 3..5 }) names += "units-3-5-curriculum-notice.txt"
    if (units.any { it in 6..10 }) names += "units-6-10-curriculum-notice.txt"
    if (units.any { it in 26..30 }) names += "units-26-30-curriculum-notice.txt"
    if (pack.sources.none { it.license.contains("CC BY-SA") }) names -= "cc-by-sa-4.0.txt"
    return names
}

private fun packagedNoticeFiles(repositoryRoot: File): List<PackagedNoticeFile> {
    val files = mutableListOf<PackagedNoticeFile>()
    val directory = File(repositoryRoot, "app/src/main/assets/licenses")
    directory.listFiles()?.sortedBy { it.name }?.forEach { file ->
        if (file.isFile) files += PackagedNoticeFile(file.name, file.readText())
    }
    val catalog = File(repositoryRoot, "app/src/main/assets/content/catalog-notice.txt")
    if (catalog.isFile) files += PackagedNoticeFile(catalog.name, catalog.readText())
    return files
}

private fun readAsset(context: Context, path: String): String? {
    return try {
        context.assets.open(path).bufferedReader(Charsets.UTF_8).use { it.readText() }
    } catch (_: IOException) {
        null
    }
}
