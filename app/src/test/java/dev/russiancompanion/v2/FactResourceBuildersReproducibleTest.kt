package dev.russiancompanion.v2

import dev.russiancompanion.testing.pythonProcess
import org.junit.Assert.*
import org.junit.Test
import java.io.File
import java.nio.file.Files
import java.util.concurrent.TimeUnit

/**
 * T17f (owner decision D42b): the Unit 11 and Unit 12 fact-resource builders regenerate their committed outputs byte for
 * byte from the current inputs (the pack, the frozen sources and the T15-n0 inventory). Before T17f both stopped with "no
 * marker" because they looked markers up under the pre-T16-n2 wording. Each builder runs twice into fresh directories:
 * both runs are identical to each other and to the committed resources.
 */
class FactResourceBuildersReproducibleTest {
    private val root = listOf(File("."), File("..")).first { File(it, "app/src/main").isDirectory }.canonicalFile

    private fun build(unit: Int): Map<String, ByteArray> {
        val out = Files.createTempDirectory("unit$unit-facts").toFile()
        try {
            val process = pythonProcess("-I", File(root, "catalog/build_unit${unit}_fact_resources.py").path, "--out", out.path)
                .directory(root).redirectErrorStream(true).start()
            val log = process.inputStream.bufferedReader().readText()
            assertTrue("builder for Unit $unit timed out", process.waitFor(300, TimeUnit.SECONDS))
            assertEquals("builder for Unit $unit failed:\n$log", 0, process.exitValue())
            return out.listFiles()!!.associate { it.name to it.readBytes() }
        } finally {
            out.deleteRecursively()
        }
    }

    @Test fun unit11And12BuildersRegenerateTheirCommittedResourcesByteForByte() {
        for (unit in listOf(11, 12)) {
            val names = setOf("unit$unit-fact-dispositions.json", "unit$unit-phrase-attestations.json")
            val first = build(unit)
            val second = build(unit)
            assertEquals("Unit $unit outputs", names, first.keys)
            for (name in names) {
                val committed = File(root, "app/src/test/resources/$name").readBytes()
                assertArrayEquals("$name differs between two runs", first.getValue(name), second.getValue(name))
                assertArrayEquals("$name differs from the committed resource", committed, first.getValue(name))
            }
        }
    }
}
