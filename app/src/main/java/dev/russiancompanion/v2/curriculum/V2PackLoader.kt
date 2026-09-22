package dev.russiancompanion.v2.curriculum

import android.content.Context
import kotlinx.serialization.json.Json

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

    fun parse(text: String): V2CurriculumPack = json.decodeFromString(V2CurriculumPack.serializer(), text)

    fun load(context: Context): V2CurriculumPack {
        val raw = readAsset(context) ?: error("The v2 curriculum pack is missing")
        val pack = parse(raw)
        val validation = V2Validator.validate(pack)
        if (!validation.ok) error(validation.errors.joinToString("; "))
        return pack
    }
}
