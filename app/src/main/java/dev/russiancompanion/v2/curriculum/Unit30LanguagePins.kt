package dev.russiancompanion.v2.curriculum
import java.security.MessageDigest
/** Primary native fields and bounded language chains; no source/linguistic acceptance token. */
internal object Unit30LanguagePins {
    private val checks = mapOf("obj-u30-letter-names" to listOf("u30-i-native-name-b", "u30-i-native-name-v"), "obj-u30-abbrev-examples" to listOf("u30-i-initialism-ussr", "u30-i-initialism-usa"))
    private val guided = mapOf("obj-u30-letter-names" to listOf("u30-g-native-name-a"), "obj-u30-abbrev-examples" to listOf("u30-g-initialism-use"))
    private data class Native(val id: String, val objective: String, val source: String, val revision: String,
        val url: String, val record: String, val hash: String, val text: String)
    private val native = listOf(
        Native("prov-u30-language-native-a-glyph", "obj-u30-letter-names", "src-wikipedia-russian-alphabet-1371795481", "1371795481", "https://en.wikipedia.org/w/index.php?title=Russian_alphabet&oldid=1371795481", "alphabet-native:! style=\"text-align: center;\" | <big>{{lang|ru|[[А]]а}}</big>\n| [[File:01-Rus", "b7c684b44a2b49fa2822121d673e95925f67672be42b057df8b674de67b8da5b", "А"),
        Native("prov-u30-language-native-a-name", "obj-u30-letter-names", "src-wikipedia-russian-alphabet-1371795481", "1371795481", "https://en.wikipedia.org/w/index.php?title=Russian_alphabet&oldid=1371795481", "alphabet-native:! style=\"text-align: center;\" | <big>{{lang|ru|[[А]]а}}</big>\n| [[File:01-Rus", "b7c684b44a2b49fa2822121d673e95925f67672be42b057df8b674de67b8da5b", "а"),
        Native("prov-u30-language-native-b-glyph", "obj-u30-letter-names", "src-wikipedia-russian-alphabet-1371795481", "1371795481", "https://en.wikipedia.org/w/index.php?title=Russian_alphabet&oldid=1371795481", "alphabet-native:! style=\"text-align: center;\" | <big>{{lang|ru|[[Б]]б}}</big>\n| [[File:02-Rus", "b9aa27ea4b93b0341a01fdece40fd014ab718f99875672c24fbed7c54319cdfd", "Б"),
        Native("prov-u30-language-native-b-name", "obj-u30-letter-names", "src-wikipedia-russian-alphabet-1371795481", "1371795481", "https://en.wikipedia.org/w/index.php?title=Russian_alphabet&oldid=1371795481", "alphabet-native:! style=\"text-align: center;\" | <big>{{lang|ru|[[Б]]б}}</big>\n| [[File:02-Rus", "b9aa27ea4b93b0341a01fdece40fd014ab718f99875672c24fbed7c54319cdfd", "бэ"),
        Native("prov-u30-language-native-v-glyph", "obj-u30-letter-names", "src-wikipedia-russian-alphabet-1371795481", "1371795481", "https://en.wikipedia.org/w/index.php?title=Russian_alphabet&oldid=1371795481", "alphabet-native:! style=\"text-align: center;\" | <big>{{lang|ru|[[В]]в}}</big>\n| [[File:03-Rus", "901123d470de234ddb4417cc787b1b42f7c88c220d043584c9e2b331a79f5fbb", "В"),
        Native("prov-u30-language-native-v-name", "obj-u30-letter-names", "src-wikipedia-russian-alphabet-1371795481", "1371795481", "https://en.wikipedia.org/w/index.php?title=Russian_alphabet&oldid=1371795481", "alphabet-native:! style=\"text-align: center;\" | <big>{{lang|ru|[[В]]в}}</big>\n| [[File:03-Rus", "901123d470de234ddb4417cc787b1b42f7c88c220d043584c9e2b331a79f5fbb", "вэ"),
        Native("prov-u30-language-native-ussr-ru", "obj-u30-abbrev-examples", "src-wikipedia-russian-grammar-1369617627", "1369617627", "https://en.wikipedia.org/w/index.php?title=Russian_grammar&oldid=1369617627", "grammar:* Abbreviations:\n** СССР ([ɛs ɛs ɛs ˈɛr] \"USSR\", masculine);\n** США ([sɛ ʂɛ ˈa] ", "1e8ce1f79958d66a8d3aec76b7a5176f5b7d1ad14e6ea9c1a3b75189bd76ce5a", "СССР"),
        Native("prov-u30-language-native-usa-ru", "obj-u30-abbrev-examples", "src-wikipedia-russian-grammar-1369617627", "1369617627", "https://en.wikipedia.org/w/index.php?title=Russian_grammar&oldid=1369617627", "grammar:* Abbreviations:\n** СССР ([ɛs ɛs ɛs ˈɛr] \"USSR\", masculine);\n** США ([sɛ ʂɛ ˈa] ", "1e8ce1f79958d66a8d3aec76b7a5176f5b7d1ad14e6ea9c1a3b75189bd76ce5a", "США"),
        Native("prov-u30-language-native-ussr-en", "obj-u30-abbrev-examples", "src-wikipedia-russian-grammar-1369617627", "1369617627", "https://en.wikipedia.org/w/index.php?title=Russian_grammar&oldid=1369617627", "grammar:* Abbreviations:\n** СССР ([ɛs ɛs ɛs ˈɛr] \"USSR\", masculine);\n** США ([sɛ ʂɛ ˈa] ", "1e8ce1f79958d66a8d3aec76b7a5176f5b7d1ad14e6ea9c1a3b75189bd76ce5a", "USSR"),
        Native("prov-u30-language-native-usa-en", "obj-u30-abbrev-examples", "src-wikipedia-russian-grammar-1369617627", "1369617627", "https://en.wikipedia.org/w/index.php?title=Russian_grammar&oldid=1369617627", "grammar:* Abbreviations:\n** СССР ([ɛs ɛs ɛs ˈɛr] \"USSR\", masculine);\n** США ([sɛ ʂɛ ˈa] ", "1e8ce1f79958d66a8d3aec76b7a5176f5b7d1ad14e6ea9c1a3b75189bd76ce5a", "USA"),
        Native("prov-u30-language-native-masculine-flag", "obj-u30-abbrev-examples", "src-wikipedia-russian-grammar-1369617627", "1369617627", "https://en.wikipedia.org/w/index.php?title=Russian_grammar&oldid=1369617627", "grammar:* Abbreviations:\n** СССР ([ɛs ɛs ɛs ˈɛr] \"USSR\", masculine);\n** США ([sɛ ʂɛ ˈa] ", "1e8ce1f79958d66a8d3aec76b7a5176f5b7d1ad14e6ea9c1a3b75189bd76ce5a", "masculine"),
        Native("prov-u30-language-native-plural-flag", "obj-u30-abbrev-examples", "src-wikipedia-russian-grammar-1369617627", "1369617627", "https://en.wikipedia.org/w/index.php?title=Russian_grammar&oldid=1369617627", "grammar:* Abbreviations:\n** СССР ([ɛs ɛs ɛs ˈɛr] \"USSR\", masculine);\n** США ([sɛ ʂɛ ˈa] ", "1e8ce1f79958d66a8d3aec76b7a5176f5b7d1ad14e6ea9c1a3b75189bd76ce5a", "plural"),
        Native("prov-u30-language-native-indeclinable-flag", "obj-u30-abbrev-examples", "src-wikipedia-russian-grammar-1369617627", "1369617627", "https://en.wikipedia.org/w/index.php?title=Russian_grammar&oldid=1369617627", "grammar-native:=== Indeclinable nouns ===\nSome nouns (such as borrowings from other language", "782228a7d5e2e45a791dd0e185c6e0e23a6b430f5af014d75e0972bde2ecab89", "not modified when they change number and case"),
    )
    private fun sha(s: String) = MessageDigest.getInstance("SHA-256").digest(s.toByteArray(Charsets.UTF_8))
        .joinToString("") { "%02x".format(it.toInt() and 255) }
    fun governs(id: String) = id in checks
    fun matches(pack: V2CurriculumPack, objective: V2Objective): Boolean {
        val required = checks[objective.id] ?: return false
        if (objective.checkItemIds != required || objective.kind != "RECOGNITION" || !objective.gating) return false
        val selected = native.filter { it.objective == objective.id }
        if (selected.isEmpty() || !selected.all { pin ->
            val r = pack.provenanceRecords.singleOrNull { it.itemId == pin.id }
            r != null && r.unitNumber == 30 && r.objectiveId == pin.objective && r.sourceId == pin.source &&
                r.sourceRevision == pin.revision && r.sourceUrl == pin.url && r.sourceRecordId == pin.record &&
                r.sourceHash == pin.hash && sha(r.originalSourceText) == pin.hash && r.shippedText == pin.text &&
                r.contentHash == sha(pin.text) && pin.text in r.originalSourceText &&
                r.transformation == "selection" && r.licenseId == "CC BY-SA 4.0"
        }) return false
        val practice = objective.practiceStepIds.flatMap { pack.step(it)?.second?.itemIds.orEmpty() }
        val feature = if (objective.id == "obj-u30-letter-names") "LETTER_NAME" else "ABBREVIATION_TREATMENT"
        fun valid(id: String, independent: Boolean): Boolean {
            val item = pack.item(id) ?: return false
            return item.objectiveId == objective.id && item.assessment?.independent == independent &&
                item.assessment.testedFeature == feature && CompleteRuleContextPins.itemMatches(pack,item) &&
                V2Validator.structureValid(pack,item).isEmpty()
        }
        return required.all { valid(it,true) } && guided.getValue(objective.id).all { it in practice && valid(it,false) }
    }
}
