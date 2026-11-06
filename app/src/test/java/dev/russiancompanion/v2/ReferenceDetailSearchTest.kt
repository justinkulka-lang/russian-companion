package dev.russiancompanion.v2

import dev.russiancompanion.v2.curriculum.*
import org.junit.Assert.*
import org.junit.Test
import java.io.File

class ReferenceDetailSearchTest {
    @Test fun shippedLetterNameIsSearchableInsideTheExplanation() {
        val entries = pack().references
        val target = entries.single { it.id == "ref-ca-letter-names" }
        val query = "и КРАТКОЕ"
        assertFalse(target.title.contains(query, ignoreCase = true))
        assertFalse(target.summary.contains(query, ignoreCase = true))
        assertTrue(target.detail.contains(query, ignoreCase = true))
        assertTrue(target in searchReferences(entries, query))
    }

    @Test fun explicitAliasesMatchRegardlessOfLetterCase() {
        val entry = pack().references.first().copy(searchTerms = listOf("CYRILLIC_LOOKUP_ALIAS"))
        assertEquals(listOf(entry), searchReferences(listOf(entry), "cyrillic_lookup_alias"))
    }

    @Test fun blankQueriesAndMissingResultsPreserveTheExistingContract() {
        val entries = pack().references
        assertEquals(entries, searchReferences(entries, ""))
        assertEquals(entries, searchReferences(entries, " \n\t "))
        assertTrue(searchReferences(entries, "unmatched-reference-query-cc017").isEmpty())
        assertTrue(searchReferences(emptyList(), "и краткое").isEmpty())
    }

    @Test fun searchingDetailsDoesNotExposeUnreachedNoticesOrReclassifyThem() {
        val p = pack()
        val query = "completion over a period"
        assertTrue(searchReferences(visibleReferences(p, referenceSearchLearner(1)), query).isEmpty())
        val found = searchReferences(visibleReferences(p, referenceSearchLearner(21)), query)
        assertTrue(found.any { it.id == "ref-u2125-attribution" })
        assertEquals("LICENSE_OR_ATTRIBUTION", found.single { it.id == "ref-u2125-attribution" }.classification)
        assertTrue(visibleReferences(p, referenceSearchLearner(1)).all { it.introducedInUnit <= 1 })
    }

    @Test fun largeDetailLookupPreservesEntriesAndResultOrder() {
        val template = pack().references.first()
        val body = "offline reference paragraph ".repeat(10_000) + "needle-at-end-cc017"
        val entries = listOf(template.copy(id = "large-a", detail = body), template.copy(id = "large-b", detail = body))
        val before = entries.toList()
        assertEquals(entries, searchReferences(entries, "NEEDLE-AT-END-CC017"))
        assertEquals(before, entries)
    }

    private fun pack() = V2PackLoader.parse(File(if (File("app").isDirectory) "." else "..",
        "app/src/main/assets/content/v2-curriculum.json").readText())
}

internal fun referenceSearchLearner(unit: Int) = V2LearnerView(
    onboardingCompleted = true, currentUnitNumber = unit, startingPoint = V2StartingPoint.NEW_TO_CYRILLIC,
    hasPenguin = true, hasAnki = false, hasBinder = false, hasTutor = false, dailyMinutes = 15,
    transliterationEnabled = false, currentSessionId = null, currentStepIndex = 0,
    completedStepIds = emptySet(), correctItemIds = emptySet(), bookAttestedUnits = emptySet(),
    completedUnitNumbers = emptySet(),
)
