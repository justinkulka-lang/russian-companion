package dev.russiancompanion.v2.audit

import dev.russiancompanion.v2.curriculum.V2CurriculumPack
import dev.russiancompanion.v2.curriculum.V2PackLoader
import java.io.File

internal fun loadCurriculumPack(repositoryRoot: File): V2CurriculumPack {
    val file = File(repositoryRoot, "app/src/main/assets/content/v2-curriculum.json")
    return V2PackLoader.parse(file.readText())
}
