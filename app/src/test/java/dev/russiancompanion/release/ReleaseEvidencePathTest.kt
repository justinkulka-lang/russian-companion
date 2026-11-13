package dev.russiancompanion.release

import org.junit.Assert.*
import org.junit.Test
import java.io.File
import java.nio.file.Files

class ReleaseEvidencePathTest {
    @Test fun existingNativeContainedPathIsAcceptedAndMissingOrMalformedPathsFailClosed() {
        val root = Files.createTempDirectory("evidence-native-").toFile()
        try {
            val evidence = File(root, "release/evidence/current.json").apply { parentFile.mkdirs(); writeText("{}") }
            assertEquals(evidence.toPath().toRealPath().toFile(), resolveEvidenceFile(root, "release/evidence/current.json"))
            for (path in listOf("", "/absolute.json", "\\absolute.json", "C:/absolute.json", "release\\evidence\\current.json",
                "release/evidence/../current.json", "release/./evidence/current.json", "release//evidence/current.json",
                "release/evidence/missing.json", "release/evidence/invalid\u0000.json")) {
                assertNull(path, resolveEvidenceFile(root, path))
            }
        } finally { root.deleteRecursively() }
    }

    @Test fun symlinkToAnOutsideFileOrSiblingWithTheSamePrefixIsRejected() {
        val parent = Files.createTempDirectory("evidence-links-").toFile()
        val root = File(parent, "repo").apply { mkdirs() }
        val outside = File(parent, "repo-other").apply { mkdirs() }
        val evidence = File(root, "release/evidence").apply { mkdirs() }
        val target = File(outside, "foreign.json").apply { writeText("{}") }
        val fileLink = File(evidence, "foreign.json").toPath()
        val directoryLink = File(evidence, "outside").toPath()
        try {
            Files.createSymbolicLink(fileLink, target.toPath())
            Files.createSymbolicLink(directoryLink, outside.toPath())
            assertNull(resolveEvidenceFile(root, "release/evidence/foreign.json"))
            assertNull(resolveEvidenceFile(root, "release/evidence/outside/foreign.json"))
            val inside = File(evidence, "current.json").apply { writeText("{}") }
            val insideLink = File(evidence, "alias.json").toPath()
            try {
                Files.createSymbolicLink(insideLink, inside.toPath())
                assertEquals(inside.toPath().toRealPath().toFile(), resolveEvidenceFile(root, "release/evidence/alias.json"))
            } finally { Files.deleteIfExists(insideLink) }
        } finally {
            Files.deleteIfExists(fileLink)
            Files.deleteIfExists(directoryLink)
            parent.deleteRecursively()
        }
    }
}
