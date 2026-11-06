package dev.russiancompanion.v2.curriculum
import java.security.MessageDigest
/** Frozen bounded motion contexts; no source/rights/semantic acceptance token. */
internal object Unit20DirectionalityPins {
    val checkIds = listOf("u20-i-motion-1", "u20-i-motion-2", "u20-i-motion-3", "u20-i-motion-4", "u20-i-direction-birds", "u20-i-direction-city", "u20-i-direction-weekly", "u20-i-direction-round")
    val guidedIds = listOf("u20-g-direction-library", "u20-g-direction-ability")
    private data class Native(val id: String,val source: String,val revision: String,val url: String,
        val record: String,val hash: String,val text: String)
    private val native = listOf(
        Native("prov-u20-direction-native-library-ru", "src-wikipedia-russian-grammar-1369617627", "1369617627", "https://en.wikipedia.org/w/index.php?title=Russian_grammar&oldid=1369617627", "motion-native-context:===  Verbs of motion ===\nVerbs of motion are a distinct class of verbs found ", "144527c7aff47147bbe8d5f4176e26d67bd5324a92439e1012965a6974f546b9", "Мы идём в библиотеку."),
        Native("prov-u20-direction-native-library-en", "src-wikipedia-russian-grammar-1369617627", "1369617627", "https://en.wikipedia.org/w/index.php?title=Russian_grammar&oldid=1369617627", "motion-native-context:===  Verbs of motion ===\nVerbs of motion are a distinct class of verbs found ", "144527c7aff47147bbe8d5f4176e26d67bd5324a92439e1012965a6974f546b9", "We are headed to the library."),
        Native("prov-u20-direction-native-ability-ru", "src-wikipedia-russian-grammar-1369617627", "1369617627", "https://en.wikipedia.org/w/index.php?title=Russian_grammar&oldid=1369617627", "motion-native-context:===  Verbs of motion ===\nVerbs of motion are a distinct class of verbs found ", "144527c7aff47147bbe8d5f4176e26d67bd5324a92439e1012965a6974f546b9", "Ребёнок ходит шесть месяцев."),
        Native("prov-u20-direction-native-ability-en", "src-wikipedia-russian-grammar-1369617627", "1369617627", "https://en.wikipedia.org/w/index.php?title=Russian_grammar&oldid=1369617627", "motion-native-context:===  Verbs of motion ===\nVerbs of motion are a distinct class of verbs found ", "144527c7aff47147bbe8d5f4176e26d67bd5324a92439e1012965a6974f546b9", "The child has been walking for six months."),
        Native("prov-u20-direction-native-birds-ru", "src-wikipedia-russian-grammar-1369617627", "1369617627", "https://en.wikipedia.org/w/index.php?title=Russian_grammar&oldid=1369617627", "motion-native-context:===  Verbs of motion ===\nVerbs of motion are a distinct class of verbs found ", "144527c7aff47147bbe8d5f4176e26d67bd5324a92439e1012965a6974f546b9", "Птицы летят на юг."),
        Native("prov-u20-direction-native-birds-en", "src-wikipedia-russian-grammar-1369617627", "1369617627", "https://en.wikipedia.org/w/index.php?title=Russian_grammar&oldid=1369617627", "motion-native-context:===  Verbs of motion ===\nVerbs of motion are a distinct class of verbs found ", "144527c7aff47147bbe8d5f4176e26d67bd5324a92439e1012965a6974f546b9", "The birds are flying south."),
        Native("prov-u20-direction-native-city-ru", "src-wikipedia-russian-grammar-1369617627", "1369617627", "https://en.wikipedia.org/w/index.php?title=Russian_grammar&oldid=1369617627", "motion-native-context:===  Verbs of motion ===\nVerbs of motion are a distinct class of verbs found ", "144527c7aff47147bbe8d5f4176e26d67bd5324a92439e1012965a6974f546b9", "Мы ходили по городу весь день."),
        Native("prov-u20-direction-native-city-en", "src-wikipedia-russian-grammar-1369617627", "1369617627", "https://en.wikipedia.org/w/index.php?title=Russian_grammar&oldid=1369617627", "motion-native-context:===  Verbs of motion ===\nVerbs of motion are a distinct class of verbs found ", "144527c7aff47147bbe8d5f4176e26d67bd5324a92439e1012965a6974f546b9", "We walked around the city all day."),
        Native("prov-u20-direction-native-weekly-ru", "src-wikipedia-russian-grammar-1369617627", "1369617627", "https://en.wikipedia.org/w/index.php?title=Russian_grammar&oldid=1369617627", "motion-native-context:===  Verbs of motion ===\nVerbs of motion are a distinct class of verbs found ", "144527c7aff47147bbe8d5f4176e26d67bd5324a92439e1012965a6974f546b9", "Она ходит в супермаркет каждую неделю."),
        Native("prov-u20-direction-native-weekly-en", "src-wikipedia-russian-grammar-1369617627", "1369617627", "https://en.wikipedia.org/w/index.php?title=Russian_grammar&oldid=1369617627", "motion-native-context:===  Verbs of motion ===\nVerbs of motion are a distinct class of verbs found ", "144527c7aff47147bbe8d5f4176e26d67bd5324a92439e1012965a6974f546b9", "She goes to the supermarket every week."),
        Native("prov-u20-direction-native-round-ru", "src-wikipedia-russian-grammar-1369617627", "1369617627", "https://en.wikipedia.org/w/index.php?title=Russian_grammar&oldid=1369617627", "motion-native-context:===  Verbs of motion ===\nVerbs of motion are a distinct class of verbs found ", "144527c7aff47147bbe8d5f4176e26d67bd5324a92439e1012965a6974f546b9", "В прошлом году я ездил в Россию."),
        Native("prov-u20-direction-native-round-en", "src-wikipedia-russian-grammar-1369617627", "1369617627", "https://en.wikipedia.org/w/index.php?title=Russian_grammar&oldid=1369617627", "motion-native-context:===  Verbs of motion ===\nVerbs of motion are a distinct class of verbs found ", "144527c7aff47147bbe8d5f4176e26d67bd5324a92439e1012965a6974f546b9", "I went to Russia (and returned) last year."),
    )
    private fun sha(s: String) = MessageDigest.getInstance("SHA-256").digest(s.toByteArray(Charsets.UTF_8))
        .joinToString("") { "%02x".format(it.toInt() and 255) }
    fun matches(pack: V2CurriculumPack): Boolean {
        val obj = pack.unit(20)?.objectives?.singleOrNull { it.id == "obj-u20-motion" } ?: return false
        if (obj.checkItemIds != checkIds || obj.kind != "RECOGNITION" || !obj.gating) return false
        if (!native.all { pin ->
            val r = pack.provenanceRecords.singleOrNull { it.itemId == pin.id }
            r != null && r.unitNumber == 20 && r.objectiveId == obj.id && r.sourceId == pin.source &&
                r.sourceRevision == pin.revision && r.sourceUrl == pin.url && r.sourceRecordId == pin.record &&
                r.sourceHash == pin.hash && sha(r.originalSourceText) == pin.hash && r.shippedText == pin.text &&
                r.contentHash == sha(pin.text) && pin.text in r.originalSourceText &&
                r.transformation == "selection" && r.licenseId == "CC BY-SA 4.0"
        }) return false
        val practice = obj.practiceStepIds.flatMap { pack.step(it)?.second?.itemIds.orEmpty() }
        fun valid(id: String,independent: Boolean): Boolean {
            val item = pack.item(id) ?: return false
            return item.objectiveId == obj.id && item.assessment?.independent == independent &&
                item.assessment.testedFeature == "MOTION_DIRECTIONAL_APPLICATION" &&
                CompleteRuleContextPins.itemMatches(pack,item) && V2Validator.structureValid(pack,item).isEmpty()
        }
        return checkIds.filter { "-direction-" in it }.all { valid(it,true) } &&
            guidedIds.all { it in practice && valid(it,false) }
    }
}
