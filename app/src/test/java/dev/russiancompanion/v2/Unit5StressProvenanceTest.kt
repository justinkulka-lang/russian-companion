package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.V2PackLoader
import dev.russiancompanion.v2.curriculum.item
import org.junit.Assert.*
import org.junit.Test
import java.io.File
import java.security.MessageDigest

class Unit5StressProvenanceTest {
    @Test fun stressedQuestionKeysUseInventoriedCyrillicAccentFields() {
        val root = listOf(File("."), File("..")).first { File(it, "app/src/main").isDirectory }
        val pack = V2PackLoader.parse(File(root, "app/src/main/assets/content/v2-curriculum.json").readText())
        for (id in listOf("kuda", "otkuda", "skolko", "kakoy")) {
            val record = pack.provenanceRecords.single { it.itemId == "prov-u5-key-$id-stressed" }
            assertEquals("src-openrussian-50e210c4", record.sourceId)
            assertEquals("50e210c4803237779cb562bc1abcea529066031c", record.sourceRevision)
            assertEquals("formatting", record.transformation)
            assertTrue(record.sourceRecordId.endsWith("#accented"))
            val name = record.sourceRecordId.substringBefore(':')
            val directory = if (name == "adjectives.csv") "units-6-10" else "units-3-5"
            val bare = record.sourceRecordId.substringAfter(':').substringBefore('#')
            val row = File(root, "catalog/sources/$directory/openrussian/$name").readLines()
                .map { it.split('\t') }.single { it.first() == bare }
            assertEquals(row[1], record.originalSourceText)
            assertTrue(record.originalSourceText.contains('\''))
            val formatted = row[1].replace("'", "\u0301").replaceFirstChar { it.uppercaseChar() }
            assertEquals(formatted, record.shippedText)
            val hash = MessageDigest.getInstance("SHA-256").digest(row[1].toByteArray())
                .joinToString("") { "%02x".format(it.toInt() and 255) }
            assertEquals(hash, record.sourceHash)
            assertEquals(record.sourceId, pack.item("u5-i-type-$id")!!.sourceId)
            val plain = pack.provenanceRecords.single { it.itemId == "prov-u5-key-$id-unstressed" }
            assertEquals(row[0], plain.originalSourceText)
            assertEquals(row[0].replaceFirstChar { it.uppercaseChar() }, plain.shippedText)
            assertEquals(plain.shippedText, pack.item("u5-i-type-$id")!!.typedAnswerKey!!.acceptedAnswers[1])
            assertEquals(record.shippedText, pack.item("u5-i-type-$id")!!.answer)
            assertEquals(record.shippedText, pack.item("u5-i-type-$id")!!.typedAnswerKey!!.acceptedAnswers.first())
        }
    }
}
