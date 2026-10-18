package dev.russiancompanion.acceptance

import dev.russiancompanion.v2.curriculum.V2CurriculumPack
import dev.russiancompanion.v2.curriculum.V2PackLoader
import dev.russiancompanion.v2.curriculum.V2ProvenanceRecord
import kotlinx.serialization.json.*
import java.io.File
import java.security.MessageDigest

/** Exact existing comparison implementations, not admission of new language or semantic approval. */
internal object ReviewedCurriculumSourceGuard {
    fun root(): File = listOf(File("."), File("..")).first { File(it, "app/src/main").isDirectory }
    fun pack(): V2CurriculumPack = V2PackLoader.parse(File(root(), "app/src/main/assets/content/v2-curriculum.json").readText())
    val entries: List<JsonObject> by lazy {
        val document = Json.parseToJsonElement(File(root(), "app/src/test/resources/reviewed-curriculum-cyrillic.json").readText()).jsonObject
        require(document["schemaVersion"]!!.jsonPrimitive.int == 1)
        require(document["semanticAcceptance"]!!.jsonPrimitive.boolean == false)
        require(document["rightsAcceptance"]!!.jsonPrimitive.boolean == false)
        document["files"]!!.jsonArray.map { it.jsonObject }.also { rows ->
            require(rows.map { it.text("path") }.distinct().size == rows.size)
        }
    }
    fun sources(): Map<String, String> = listOf("src/main/java", "src/main/res").flatMap { relative ->
        File(root(), "app/$relative").walkTopDown().filter { it.isFile && it.extension != "ogg" }.map {
            it.relativeTo(File(root(), "app")).invariantSeparatorsPath to it.readText()
        }.toList()
    }.toMap()

    fun offenders(sources: Map<String, String>, pack: V2CurriculumPack,
        catalogText: (String) -> String? = { path -> File(root(), path).takeIf { it.isFile }?.readText() },
    ): List<String> {
        val reviewed = entries.associateBy { it.text("path") }
        val records = pack.provenanceRecords.associateBy { it.itemId }
        val items = pack.items.map { it.id }.toSet()
        val failures = sources.mapNotNull { (path, text) ->
            val entry = reviewed[path]
            if (entry == null) {
                if (hasCyrillic(text)) path else null
            } else if (hash(text) != entry.text("sha256") ||
                entry["bindings"]!!.jsonArray.isEmpty() ||
                !entry["bindings"]!!.jsonArray.all { validBinding(it.jsonObject, records, items, catalogText) }) path else null
        }
        return (failures + (reviewed.keys - sources.keys)).sorted()
    }

    private fun validBinding(binding: JsonObject, records: Map<String, V2ProvenanceRecord>, items: Set<String>, catalogText: (String) -> String?): Boolean {
        val value = binding.text("value")
        if (!hasCyrillic(value)) return false
        return when (binding.text("kind")) {
            "ITEM_ID" -> value in items && binding.text("purpose") == "PERSISTED_ITEM_ID_REFERENCE"
            "PROVENANCE_FIELD", "PROVENANCE_FRAGMENT" -> {
                val expected = binding["record"]!!.jsonObject
                val record = records[expected.text("itemId")] ?: return false
                val actual = fields(record)
                if (expected.keys != actual.keys) return false
                expected.all { (key, v) -> actual[key] == v.jsonPrimitive.content } &&
                    (if (binding.text("kind") == "PROVENANCE_FRAGMENT") value in transform(record.originalSourceText, binding.text("mode"))
                    else (if (binding.text("field") == "originalSourceText") record.originalSourceText else actual[binding.text("field")]) == value) &&
                    hash(record.originalSourceText) == record.sourceHash &&
                    hash(record.shippedText) == record.contentHash &&
                    binding.text("purpose") == if (binding.text("kind") == "PROVENANCE_FRAGMENT")
                        "EXISTING_SOURCE_FRAGMENT_COMPARISON" else if (record.transformation.contains("synthesis"))
                        "EXISTING_RESEARCH_CUE_COMPARISON_PENDING_SOURCE_REVIEW" else "EXISTING_SOURCE_RECORD_COMPARISON"
            }
            "CATALOG_FRAGMENT" -> {
                val path = binding.text("catalogPath")
                if (!path.startsWith("catalog/sources/") || path.contains("..")) return false
                val source = catalogText(path) ?: return false
                hash(source) == binding.text("catalogSha256") &&
                    value in transform(source, binding.text("mode")) &&
                    binding.text("purpose") == "RETAINED_SOURCE_COMPARISON_NOT_CAPABILITY_ADMISSION"
            }
            else -> false
        }
    }
    private fun transform(text: String, mode: String): String = when (mode) {
        "RAW" -> text
        "REMOVE_WIKI_EMPHASIS" -> text.replace("'''", "").replace("''", "")
        "LOWERCASE" -> text.lowercase(java.util.Locale.ROOT)
        else -> error("Unreviewed source comparison mode: $mode")
    }
    private fun fields(r: V2ProvenanceRecord) = mapOf(
        "itemId" to r.itemId, "sourceId" to r.sourceId, "sourceRevision" to r.sourceRevision,
        "sourceUrl" to r.sourceUrl, "sourceRecordId" to r.sourceRecordId, "sourceHash" to r.sourceHash,
        "contentHash" to r.contentHash, "transformation" to r.transformation, "licenseId" to r.licenseId,
        "shippedText" to r.shippedText, "reviewStatus" to r.reviewStatus,
    )
    private fun hasCyrillic(text: String) = text.any { it.code in 0x0400..0x04FF } ||
        Regex("""\\u04[0-9a-fA-F]{2}""").containsMatchIn(text)
    fun hash(text: String): String = MessageDigest.getInstance("SHA-256").digest(text.toByteArray(Charsets.UTF_8))
        .joinToString("") { "%02x".format(it.toInt() and 255) }
    fun JsonObject.text(key: String): String = getValue(key).jsonPrimitive.content
}
