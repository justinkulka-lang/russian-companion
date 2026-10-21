package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.*
import java.io.File
import org.junit.Assert.*
import org.junit.Test

/** Keep historical whole-pack contracts separate from current target-content proof. */
class HistoricalTeachingFixtureScopeTest {
    @Test fun currentUnit14And15RetainTheirExactCorrectionWhileTheWholePackHasAdvanced() {
        val current = V2PackLoader.parse(File(root(), "app/src/main/assets/content/v2-curriculum.json").readText())
        // Preserve the original Unit14/15 contract before later admitted Unit15 preparation.
        val live = RecordedCurriculumConservationScope.historicalTransition(root(), current,
            "49a926c5ace0459edf7712ca5d90db4a63ff9d31", 14, 15)
        val historical = correctionEraPack()
        assertFalse("The live pack is a later version, not the historical fixture", historical == live)
        for (number in listOf(14, 15)) {
            assertEquals("Current Unit $number", historical.unit(number), live.unit(number))
            val objectives = historical.unit(number)!!.objectives.map { it.id }.toSet()
            assertEquals(historical.items.filter { it.objectiveId in objectives }, live.items.filter { it.objectiveId in objectives })
            val examples = historical.unit(number)!!.objectives.flatMap { it.exampleIds }.distinct()
            assertEquals(examples.map { historical.example(it) }, examples.map { live.example(it) })
            assertEquals(historical.provenanceRecords.filter { it.unitNumber == number }, live.provenanceRecords.filter { it.unitNumber == number })
            assertEquals(historical.sourceCapabilities.filter { it.unitNumber == number }, live.sourceCapabilities.filter { it.unitNumber == number })
        }
    }

    companion object {
        private fun root() = File(if (File("app").isDirectory) "." else "..")
        private val historical by lazy {
            val command = ProcessBuilder("git", "show",
                "96242a17a87cc2ec0f5d7418cd98a9d3220a9e99:app/src/main/assets/content/v2-curriculum.json")
                .directory(root()).start()
            V2PackLoader.parse(command.inputStream.bufferedReader().readText()).also {
                assertEquals(0, command.waitFor())
                V2PackIdentityIndex.warm(it)
            }
        }
        internal fun correctionEraPack(): V2CurriculumPack = historical
    }
}
