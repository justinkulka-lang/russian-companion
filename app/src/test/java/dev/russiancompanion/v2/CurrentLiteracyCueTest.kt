package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class CurrentLiteracyCueTest {
    @Test fun revisedTasksUseActualLowercaseCuesAndThreeCyrillicAlternativesWithLinkedExamples() {
        val step = current.step("u2-st-review")!!.second
        for (id in variants) {
            val item = current.item(id)!!
            val cue = item.promptLetter!!
            assertEquals(cue.lowercase(), cue); assertNotEquals(cue.uppercase(), cue)
            assertEquals("PRINT_PAIR", item.kind)
            assertEquals(3, item.options.size)
            assertEquals(3, item.options.distinct().size)
            assertTrue(item.options.all { it.startsWith("Cyrillic ") && it.last() in 'А'..'Я' })
            assertEquals(cue.uppercase(), item.answer.takeLast(1))
            assertTrue(step.exampleIds.map { current.example(it)!!.display }.any { it.contains(cue) && it.contains(cue.uppercase()) })
        }
    }

    companion object {
        private val root = File(if (File("app").isDirectory) "." else "..")
        private val current by lazy { V2PackLoader.parse(File(root, "app/src/main/assets/content/v2-curriculum.json").readText()).also { V2PackIdentityIndex.warm(it) } }
        private val variants = setOf("u2-i-в-not-b", "u2-i-у-not-y", "u2-i-х-not-x", "u2-i-я-identity")
    }
}
