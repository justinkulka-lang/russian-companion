package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.GRAMMAR_ITEM_KINDS
import dev.russiancompanion.v2.curriculum.V2LearnerView
import dev.russiancompanion.v2.curriculum.V2PackLoader
import dev.russiancompanion.v2.curriculum.V2StartingPoint
import dev.russiancompanion.v2.curriculum.V2Validator
import dev.russiancompanion.v2.curriculum.advancementDecision
import dev.russiancompanion.v2.curriculum.unit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class Units610InstructionalValidityTest {
    @Test
    fun everyGovernedObjectiveHasAGrammarCheckAndAdmittedDistractors() {
        val pack = pack()
        val validation = V2Validator.validate(pack)
        assertTrue(validation.errors.joinToString("\n"), validation.ok)
        val generic = "Return to the explanation. Compare the shown form with the admitted record."
        (6..10).forEach { number ->
            val unit = pack.unit(number)!!
            val shipped = pack.provenanceRecords.filter { it.unitNumber == number }.map { it.shippedText }.toSet()
            unit.objectives.filter { it.kind == "RECOGNITION" }.forEach { objective ->
                val checks = objective.checkItemIds.map { pack.items.first { item -> item.id == it } }
                assertTrue("${objective.id} has no grammar check", checks.any { it.kind in GRAMMAR_ITEM_KINDS })
                assertTrue("${objective.id} is only gloss matching", checks.any { it.kind != "SOURCE_MEANING" })
                checks.forEach { item ->
                    assertEquals(item.options.size, item.options.toSet().size)
                    assertEquals(1, item.options.count { it == item.answer })
                    assertEquals(item.options.size, item.distractorRationales.size)
                    item.options.forEachIndexed { index, option ->
                        assertTrue("$option has no admitted record", option in shipped)
                        if (option == item.answer) {
                            assertEquals("", item.distractorRationales[index])
                        } else {
                            assertTrue("${item.id} distractor rationale", item.distractorRationales[index].isNotBlank())
                        }
                    }
                }
                objective.exampleIds.forEach { id ->
                    val example = pack.examples.first { it.id == id }
                    assertTrue("${example.id} is English prose", example.display.any { it.code in 0x0400..0x04FF })
                }
                val remediation = unit.sessions.flatMap { it.steps }.first { it.id in objective.remediationStepIds }
                assertFalse(remediation.body == generic)
                assertTrue(
                    "${objective.id} remediation is not source-bound",
                    shipped.any { span -> span.length >= 8 && span in remediation.body },
                )
            }
        }
    }

    @Test
    fun sourceMeaningAloneCannotSatisfyAGrammarObjective() {
        val pack = pack()
        val objective = pack.unit(6)!!.objectives.first { it.kind == "RECOGNITION" }
        val stripped = pack.copy(
            items = pack.items.map { item ->
                if (item.id in objective.checkItemIds) item.copy(kind = "SOURCE_MEANING", distractorRationales = emptyList()) else item
            },
        )
        val errors = V2Validator.validate(stripped).errors
        assertTrue(errors.joinToString("\n"), errors.any { it.contains("SOURCE_MEANING alone cannot satisfy") })
    }

    @Test
    fun englishRuleProseCannotBeStoredAsARussianExample() {
        val pack = pack()
        val seed = pack.examples.first { it.objectiveId.startsWith("obj-u9-") }
        val injected = pack.copy(
            examples = pack.examples + seed.copy(id = "ex-u9-english-rule", display = "1 -> nominative", note = "rule"),
        )
        val errors = V2Validator.validate(injected).errors
        assertTrue(errors.joinToString("\n"), errors.any { it.contains("English instructional prose") })
    }

    @Test
    fun bookAttestationAndPartialObjectivesDoNotAdvance() {
        val pack = pack()
        (6..10).forEach { number ->
            val unit = pack.unit(number)!!
            val attested = advancementDecision(pack, unit, learner(number, book = true, correct = emptySet(), steps = emptySet()))
            assertFalse(attested.allowed)
            val recognition = unit.objectives.filter { it.kind == "RECOGNITION" }
            val skipped = recognition.first()
            val rest = recognition.drop(1).flatMap { it.checkItemIds }.toSet()
            val partial = advancementDecision(
                pack,
                unit,
                learner(number, book = true, correct = rest, steps = unit.sessions.flatMap { it.steps }.map { it.id }.toSet()),
            )
            assertFalse(partial.allowed)
            assertTrue(partial.remaining.joinToString("\n"), partial.remaining.any { it.contains(skipped.title) || it.contains("Demonstrate") })
        }
    }

    @Test
    fun unitNineCoversTheRequiredNumberScopeAndUnitSevenNamesTheMissingAdverb() {
        val pack = pack()
        val unit9 = pack.unit(9)!!
        val visible = unit9.sessions.flatMap { it.steps }.joinToString("\n") { it.body } +
            unit9.objectives.flatMap { it.exampleIds }.joinToString("\n") { id ->
                val example = pack.examples.first { it.id == id }
                example.display + " " + example.note
            }
        listOf(
            "ноль", "оди́н", "два́дцать", "три́дцать", "со́рок", "пятьдеся́т", "шестьдеся́т", "се́мьдесят", "во́семьдесят", "девяно́сто",
            "сто", "две́сти", "три́ста", "четы́реста", "пятьсо́т", "шестьсо́т", "семьсо́т", "восемьсо́т", "девятьсо́т",
            "ты́сяча", "два́дцать оди́н", "сто оди́н", "две́сти пятна́дцать", "три́ста три́дцать три",
            "ты́сяча девятьсо́т во́семьдесят четы́ре", "две/три/четы́ре ты́сячи", "пять ~ де́вять ты́сяч",
            "один мальчик", "одна девушка", "одно животное", "одни джинсы", "две",
            "один час", "два часа", "пять часов", "двадцать один час", "шестнадцать рублей",
            "11, 12, 13, 14", "сорок один год", "двести тринадцать собак",
        ).forEach { span ->
            assertTrue("missing $span", span in visible)
        }
        val pattern = unit9.objectives.first { it.id == "obj-u9-pattern" }
        val patternChecks = pattern.checkItemIds.map { id -> pack.items.first { it.id == id } }
        assertTrue(patternChecks.any { it.kind == "PATTERN_CLASSIFICATION" && it.answer == "5 and more -> genitive plural" })
        assertTrue(patternChecks.any { it.answer.contains("must be declined") })
        val unit7 = pack.unit(7)!!
        assertEquals("NOT_PREPARED", unit7.readiness)
        val lesson = unit7.sessions.flatMap { it.steps }.joinToString("\n") { it.body + (it.bookDirection ?: "") }
        assertTrue(lesson.contains("introductory adverb objective is not prepared"))
        assertFalse(lesson.contains("хорошое"))
    }

    private fun learner(number: Int, book: Boolean, correct: Set<String>, steps: Set<String>) = V2LearnerView(
        onboardingCompleted = true,
        currentUnitNumber = number,
        startingPoint = V2StartingPoint.NEW_TO_CYRILLIC,
        hasPenguin = true,
        hasAnki = false,
        hasBinder = false,
        hasTutor = false,
        dailyMinutes = 25,
        transliterationEnabled = true,
        currentSessionId = null,
        currentStepIndex = 0,
        completedStepIds = steps,
        correctItemIds = correct,
        bookAttestedUnits = if (book) setOf(number) else emptySet(),
        completedUnitNumbers = emptySet(),
    )

    private fun pack() = V2PackLoader.parse(File(repo(), "app/src/main/assets/content/v2-curriculum.json").readText())

    private fun repo(): File = listOf(File("."), File("..")).first { File(it, "app/src/main").isDirectory }
}
