package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.*
import org.junit.Assert.*
import java.io.File

/** Exact recorded sources for intake-era assertions; no semantic acceptance or current hash refresh. */
internal object RecordedIntakeCorrectionScope {
    fun load(root: File, commit: String): V2CurriculumPack {
        val process = ProcessBuilder("git", "show", "$commit:app/src/main/assets/content/v2-curriculum.json")
            .directory(root).start()
        val text = process.inputStream.bufferedReader().readText()
        assertEquals(0, process.waitFor())
        return V2PackLoader.parse(text).also { V2PackIdentityIndex.warm(it) }
    }

    fun assertCurrentUnitMatches(current: V2CurriculumPack, source: V2CurriculumPack, number: Int) {
        val expected = source.unit(number)!!
        assertEquals(expected, current.unit(number))
        val objectiveIds = expected.objectives.map { it.id }.toSet()
        assertEquals(source.items.filter { it.objectiveId in objectiveIds }, current.items.filter { it.objectiveId in objectiveIds })
        assertEquals(source.provenanceRecords.filter { it.unitNumber == number }, current.provenanceRecords.filter { it.unitNumber == number })
        assertEquals(source.sourceCapabilities.filter { it.unitNumber == number }, current.sourceCapabilities.filter { it.unitNumber == number })
        val exampleIds = (expected.objectives.flatMap { it.exampleIds } +
            expected.sessions.flatMap { it.steps }.flatMap { it.exampleIds }).toSet()
        assertEquals(source.examples.filter { it.id in exampleIds }, current.examples.filter { it.id in exampleIds })
        assertEquals(V2Validator.unitContentReviewDigest(source, number), V2Validator.unitContentReviewDigest(current, number))
    }
}
