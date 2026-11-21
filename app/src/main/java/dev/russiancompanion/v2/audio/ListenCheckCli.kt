package dev.russiancompanion.v2.audio

import dev.russiancompanion.v2.curriculum.V2PackLoader
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.File

/** Bundled clips from the app's audio catalog: id, catalog text and the file's SHA-256. */
internal fun loadCatalogClips(assets: File): List<V2CatalogClip> {
    val root = Json.parseToJsonElement(File(assets, "content/bundled-content.json").readText()).jsonObject
    val texts = root.getValue("sourceTexts").jsonArray.associate {
        it.jsonObject.getValue("id").jsonPrimitive.content to it.jsonObject.getValue("stressMarkedText").jsonPrimitive.content
    }
    return root.getValue("audioModels").jsonArray.map { it.jsonObject }.mapNotNull { model ->
        val id = model.getValue("id").jsonPrimitive.content
        val text = texts[model.getValue("sourceTextId").jsonPrimitive.content] ?: return@mapNotNull null
        val file = File(assets, model.getValue("localAudioReference").jsonPrimitive.content)
        if (!file.isFile) null else V2CatalogClip(id, text, sha256Hex(file.readBytes()))
    }
}

internal fun clipFile(assets: File, clipId: String): File = File(assets, "audio/$clipId.ogg")

/** Developer-only: decodes one clip to 16-bit PCM WAV with ffmpeg, without metadata. */
internal fun ffmpegWav(source: File): ByteArray {
    val out = File.createTempFile("listen-check-", ".wav")
    try {
        val process = ProcessBuilder("ffmpeg", "-v", "error", "-y", "-i", source.path, "-map_metadata", "-1",
            "-fflags", "+bitexact", "-flags:a", "+bitexact", "-c:a", "pcm_s16le", out.path)
            .redirectErrorStream(true).start()
        val log = process.inputStream.bufferedReader().readText()
        check(process.waitFor() == 0) { "ffmpeg could not decode $source: $log" }
        return out.readBytes()
    } finally { out.delete() }
}

/** Block number to its units (A2-01). */
internal fun blockUnits(block: Int): IntRange {
    require(block in 1..6) { "Blocks are 1 to 6" }
    return (block * 5 - 4)..(block * 5)
}

/**
 * Developer-only entry point. Writes the offline listen-check sheet for one block (A2-18).
 * Usage: --repo /repo --block 1 --output /repo/audit/listen-check/block1.html
 */
fun main(args: Array<String>) {
    val options = args.toList().chunked(2).associate { it[0] to it.getOrElse(1) { "" } }
    val repo = File(options["--repo"] ?: error("--repo is required")).canonicalFile
    val block = (options["--block"] ?: error("--block is required")).toInt()
    val output = File(options["--output"] ?: error("--output is required")).canonicalFile
    val assets = File(repo, "app/src/main/assets")
    val pack = V2PackLoader.parse(File(assets, "content/v2-curriculum.json").readText())
    val sheet = "block$block"
    val candidates = listenCheckCandidates(pack, blockUnits(block), loadCatalogClips(assets))
    output.parentFile.mkdirs()
    output.writeText(listenCheckSheetHtml(sheet, "Block $block listen-check", candidates,
        { clipFile(assets, it).readBytes() }, { ffmpegWav(clipFile(assets, it)) }))
    println("Wrote ${candidates.size} candidate clips to $output")
}
