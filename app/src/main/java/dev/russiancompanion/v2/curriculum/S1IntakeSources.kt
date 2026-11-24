package dev.russiancompanion.v2.curriculum

/**
 * T16c (owner decision D29a, approved rule change): every revision frozen by S1 under catalog/sources/s1-intake is admitted to
 * its licence route, matched by exact source ID and revision. The list is exactly the FREEZE.json pages and supplemental
 * entries (S1IntakeAdmissionTest checks it against that file); there is no wildcard and no revision outside FREEZE.json.
 */
internal object S1IntakeSources {
    enum class Route { WIKIPEDIA, RU_WIKIPEDIA, WIKTIONARY, READY_RUSSIAN }

    data class Entry(val sourceId: String, val route: Route, val revision: String)

    val ENTRIES: List<Entry> = listOf(
        Entry("src-enwiki-grammatical-aspect-1384289498", Route.WIKIPEDIA, "1384289498"),
        Entry("src-ruwiki-vid-lingvistika-153277825", Route.RU_WIKIPEDIA, "153277825"),
        Entry("src-enwiki-sequence-of-tenses-1363198135", Route.WIKIPEDIA, "1363198135"),
        Entry("src-ruwiki-povelitelnoe-naklonenie-152579064", Route.RU_WIKIPEDIA, "152579064"),
        Entry("src-enwiki-reflexive-pronoun-1374712982", Route.WIKIPEDIA, "1374712982"),
        Entry("src-ruwiki-obosoblenie-144053197", Route.RU_WIKIPEDIA, "144053197"),
        Entry("src-ruwiki-boltraffio-149826784", Route.RU_WIKIPEDIA, "149826784"),
        Entry("src-ruwiki-soslagatelnoe-naklonenie-140019162", Route.RU_WIKIPEDIA, "140019162"),
        Entry("src-enwiktionary-pod-92761073", Route.WIKTIONARY, "92761073"),
        Entry("src-enwiktionary-by-94001807", Route.WIKTIONARY, "94001807"),
        Entry("src-enwiktionary-chtoby-91135223", Route.WIKTIONARY, "91135223"),
        Entry("src-enwiktionary-daby-83982684", Route.WIKTIONARY, "83982684"),
        Entry("src-enwiktionary-dvoe-83716615", Route.WIKTIONARY, "83716615"),
        Entry("src-enwiktionary-nazad-93809546", Route.WIKTIONARY, "93809546"),
        Entry("src-enwiktionary-za-94001837", Route.WIKTIONARY, "94001837"),
        Entry("src-enwiktionary-nekogo-80719285", Route.WIKTIONARY, "80719285"),
        Entry("src-readyrussian-grammar24", Route.READY_RUSSIAN, "933d0deb0742157fcc8eb469f448146f73c3d7e211ec8706ae9632198feefc85"),
    )

    /** The frozen revision for this source ID on this route, or null when S1 froze no such page. */
    fun revision(sourceId: String, route: Route): String? = ENTRIES.singleOrNull { it.sourceId == sourceId && it.route == route }?.revision

    fun admits(sourceId: String, revision: String, route: Route): Boolean = revision(sourceId, route) == revision

    /**
     * T16c-n1 (owner decision D36a): an S1 source ID, on any route. Such an ID is admitted only at its exact S1 revision; a legacy
     * revision allowlist never admits it at another revision.
     */
    fun listed(sourceId: String): Boolean = ENTRIES.any { it.sourceId == sourceId }
}
