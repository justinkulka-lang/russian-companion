package dev.russiancompanion.v2.curriculum

import android.content.Context
import kotlinx.serialization.json.*
import java.security.MessageDigest

internal object V2PackLoader {
    const val ASSET = "content/v2-curriculum.json"

    private val json = Json {
        ignoreUnknownKeys = false
        explicitNulls = true
    }

    fun readAsset(context: Context): String? {
        return try {
            context.assets.open(ASSET).bufferedReader().use { it.readText() }
        } catch (_: Exception) {
            null
        }
    }

    fun parse(text: String): V2CurriculumPack =
        json.decodeFromJsonElement(V2CurriculumPack.serializer(), expandedDocument(text))

    /** Expand storage references before strict model decoding; retain legacy inline packs. */
    internal fun expandedDocument(text: String): JsonObject {
        val document = json.parseToJsonElement(text)
        require(document is JsonObject) { "The curriculum must be an object" }
        val poolValue = document["sourceTextPool"]
        require(poolValue == null || poolValue is JsonObject) { "sourceTextPool must be an object" }
        val pool = poolValue as? JsonObject ?: JsonObject(emptyMap())
        val spans = pool.mapValues { (key, value) ->
            require(key.matches(Regex("[0-9a-f]{64}")) && value is JsonPrimitive && value.isString) {
                "Invalid source-pool entry"
            }
            val original = (value as JsonPrimitive).content
            val digest = MessageDigest.getInstance("SHA-256").digest(original.toByteArray(Charsets.UTF_8))
                .joinToString("") { "%02x".format(it) }
            require(digest == key) { "Source pool hash mismatch" }
            original
        }
        val used = mutableSetOf<String>()
        val records = document["provenanceRecords"]
        require(records is JsonArray) { "Provenance records must be an array" }
        val expanded = JsonArray(records.map { value ->
            require(value is JsonObject) { "A provenance record must be an object" }
            val reference = value["originalSourceTextRef"]
            if (reference == null) value else {
                require(reference is JsonPrimitive && reference.isString && "originalSourceText" !in value) {
                    "Invalid source-text reference"
                }
                val key = (reference as JsonPrimitive).content
                val recordHash = value["sourceHash"]
                require(recordHash is JsonPrimitive && recordHash.isString && recordHash.content == key) {
                    "Record source hash does not bind its reference"
                }
                val original = spans[key] ?: throw IllegalArgumentException("Missing source-pool entry")
                used += key
                JsonObject((value - "originalSourceTextRef") + ("originalSourceText" to JsonPrimitive(original)))
            }
        })
        require(used == spans.keys) { "Unreferenced source-pool entry" }
        return JsonObject((document - "sourceTextPool") + ("provenanceRecords" to expanded))
    }

    fun load(context: Context): V2CurriculumPack {
        val raw = readAsset(context) ?: error("The v2 curriculum pack is missing")
        val pack = parse(raw)
        val validation = V2Validator.validate(pack)
        if (!validation.ok) error(validation.errors.joinToString("; "))
        return pack
    }
}
