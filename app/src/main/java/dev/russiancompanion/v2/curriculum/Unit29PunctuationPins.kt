package dev.russiancompanion.v2.curriculum
import java.security.MessageDigest
/** Exact raw native selections and bounded comma decisions; no qualified acceptance token. */
internal object Unit29PunctuationPins {
    val checkIds = listOf("u29-i-punctuation-1", "u29-i-punctuation-after", "u29-i-punctuation-pronoun", "u29-i-punctuation-cause", "u29-i-punctuation-terminal")
    private val newIds = listOf("u29-i-punctuation-after", "u29-i-punctuation-pronoun", "u29-i-punctuation-cause", "u29-i-punctuation-terminal")
    private val newSources = setOf("src-ruwiki-obosoblenie-144053197", "src-ruwiki-boltraffio-149826784")
    private data class Native(val id: String,val source: String,val revision: String,val url: String,
        val record: String,val hash: String,val text: String)
    private val native = listOf(
        Native("prov-u29-punctuation-native-after-ru", "src-ruwiki-obosoblenie-144053197", "144053197", "https://ru.wikipedia.org/w/index.php?title=Обособление&oldid=144053197", "punctuation-native:after:|1. Стоят после определяемого слова и выражены причастным оборотом\n|''Туча, '", "a2c8e66f9f55fb67f145c16941ebae8a44dbff5efae1da4c5a76b1889697455d", "Туча, нависшая над высокими вершинами тополей, уже сыпала дождиком"),
        Native("prov-u29-punctuation-native-after-head", "src-ruwiki-obosoblenie-144053197", "144053197", "https://ru.wikipedia.org/w/index.php?title=Обособление&oldid=144053197", "punctuation-native:after:|1. Стоят после определяемого слова и выражены причастным оборотом\n|''Туча, '", "a2c8e66f9f55fb67f145c16941ebae8a44dbff5efae1da4c5a76b1889697455d", "Туча"),
        Native("prov-u29-punctuation-native-after-region-1", "src-ruwiki-obosoblenie-144053197", "144053197", "https://ru.wikipedia.org/w/index.php?title=Обособление&oldid=144053197", "punctuation-native:after:|1. Стоят после определяемого слова и выражены причастным оборотом\n|''Туча, '", "a2c8e66f9f55fb67f145c16941ebae8a44dbff5efae1da4c5a76b1889697455d", "нависшая над высокими вершинами тополей,"),
        Native("prov-u29-punctuation-native-after-region-2", "src-ruwiki-obosoblenie-144053197", "144053197", "https://ru.wikipedia.org/w/index.php?title=Обособление&oldid=144053197", "punctuation-native:after:|1. Стоят после определяемого слова и выражены причастным оборотом\n|''Туча, '", "a2c8e66f9f55fb67f145c16941ebae8a44dbff5efae1da4c5a76b1889697455d", "уже сыпала дождиком"),
        Native("prov-u29-punctuation-native-pronoun-ru", "src-ruwiki-boltraffio-149826784", "149826784", "https://ru.wikipedia.org/w/index.php?title=Больтраффио,_Джованни&oldid=149826784", "punctuation-native:pronoun:Воспитанный в традициях [[Фоппа, Винченцо|Фоппы]], [[Дзенале, Бернардо|Бернар", "04798a919d45e77e4162a186ab72313dab529da5b5ac9d664748d1ca5a482acf", "Воспитанный в традициях Фоппы, Бернардо Дзенале и Амброджо Бергоньоне, он прошёл обучение в мастерской Леонардо."),
        Native("prov-u29-punctuation-native-pronoun-head", "src-ruwiki-boltraffio-149826784", "149826784", "https://ru.wikipedia.org/w/index.php?title=Больтраффио,_Джованни&oldid=149826784", "punctuation-native:pronoun:Воспитанный в традициях [[Фоппа, Винченцо|Фоппы]], [[Дзенале, Бернардо|Бернар", "04798a919d45e77e4162a186ab72313dab529da5b5ac9d664748d1ca5a482acf", "он"),
        Native("prov-u29-punctuation-native-pronoun-region-0", "src-ruwiki-boltraffio-149826784", "149826784", "https://ru.wikipedia.org/w/index.php?title=Больтраффио,_Джованни&oldid=149826784", "punctuation-native:pronoun:Воспитанный в традициях [[Фоппа, Винченцо|Фоппы]], [[Дзенале, Бернардо|Бернар", "04798a919d45e77e4162a186ab72313dab529da5b5ac9d664748d1ca5a482acf", "Воспитанный в традициях Фоппы,"),
        Native("prov-u29-punctuation-native-pronoun-region-1", "src-ruwiki-boltraffio-149826784", "149826784", "https://ru.wikipedia.org/w/index.php?title=Больтраффио,_Джованни&oldid=149826784", "punctuation-native:pronoun:Воспитанный в традициях [[Фоппа, Винченцо|Фоппы]], [[Дзенале, Бернардо|Бернар", "04798a919d45e77e4162a186ab72313dab529da5b5ac9d664748d1ca5a482acf", "Бернардо Дзенале и Амброджо Бергоньоне,"),
        Native("prov-u29-punctuation-native-pronoun-region-2", "src-ruwiki-boltraffio-149826784", "149826784", "https://ru.wikipedia.org/w/index.php?title=Больтраффио,_Джованни&oldid=149826784", "punctuation-native:pronoun:Воспитанный в традициях [[Фоппа, Винченцо|Фоппы]], [[Дзенале, Бернардо|Бернар", "04798a919d45e77e4162a186ab72313dab529da5b5ac9d664748d1ca5a482acf", "он прошёл обучение в мастерской Леонардо."),
        Native("prov-u29-punctuation-native-pronoun-list-foppa", "src-ruwiki-boltraffio-149826784", "149826784", "https://ru.wikipedia.org/w/index.php?title=Больтраффио,_Джованни&oldid=149826784", "punctuation-native:pronoun:Воспитанный в традициях [[Фоппа, Винченцо|Фоппы]], [[Дзенале, Бернардо|Бернар", "04798a919d45e77e4162a186ab72313dab529da5b5ac9d664748d1ca5a482acf", "Фоппы"),
        Native("prov-u29-punctuation-native-pronoun-boundary-bergognone", "src-ruwiki-boltraffio-149826784", "149826784", "https://ru.wikipedia.org/w/index.php?title=Больтраффио,_Джованни&oldid=149826784", "punctuation-native:pronoun:Воспитанный в традициях [[Фоппа, Винченцо|Фоппы]], [[Дзенале, Бернардо|Бернар", "04798a919d45e77e4162a186ab72313dab529da5b5ac9d664748d1ca5a482acf", "Бергоньоне"),
        Native("prov-u29-punctuation-native-cause-ru", "src-ruwiki-obosoblenie-144053197", "144053197", "https://ru.wikipedia.org/w/index.php?title=Обособление&oldid=144053197", "punctuation-native:cause:|3. Стоят перед определяемым словом и имеют добавочный оттенок (причины, усту", "60ab6e0afc66709184089243bbeb6d173d336a6e95c01d2baea8ea8c3ccaacdd", "Изумлённый этим хохотом, Павел сел на пол и смотрел куда-то в угол"),
        Native("prov-u29-punctuation-native-cause-head", "src-ruwiki-obosoblenie-144053197", "144053197", "https://ru.wikipedia.org/w/index.php?title=Обособление&oldid=144053197", "punctuation-native:cause:|3. Стоят перед определяемым словом и имеют добавочный оттенок (причины, усту", "60ab6e0afc66709184089243bbeb6d173d336a6e95c01d2baea8ea8c3ccaacdd", "Павел"),
        Native("prov-u29-punctuation-native-cause-region-0", "src-ruwiki-obosoblenie-144053197", "144053197", "https://ru.wikipedia.org/w/index.php?title=Обособление&oldid=144053197", "punctuation-native:cause:|3. Стоят перед определяемым словом и имеют добавочный оттенок (причины, усту", "60ab6e0afc66709184089243bbeb6d173d336a6e95c01d2baea8ea8c3ccaacdd", "Изумлённый этим хохотом,"),
        Native("prov-u29-punctuation-native-cause-region-1", "src-ruwiki-obosoblenie-144053197", "144053197", "https://ru.wikipedia.org/w/index.php?title=Обособление&oldid=144053197", "punctuation-native:cause:|3. Стоят перед определяемым словом и имеют добавочный оттенок (причины, усту", "60ab6e0afc66709184089243bbeb6d173d336a6e95c01d2baea8ea8c3ccaacdd", "Павел сел на пол и смотрел куда"),
        Native("prov-u29-punctuation-native-cause-region-2", "src-ruwiki-obosoblenie-144053197", "144053197", "https://ru.wikipedia.org/w/index.php?title=Обособление&oldid=144053197", "punctuation-native:cause:|3. Стоят перед определяемым словом и имеют добавочный оттенок (причины, усту", "60ab6e0afc66709184089243bbeb6d173d336a6e95c01d2baea8ea8c3ccaacdd", "то в угол"),
        Native("prov-u29-punctuation-native-terminal-ru", "src-ruwiki-obosoblenie-144053197", "144053197", "https://ru.wikipedia.org/w/index.php?title=Обособление&oldid=144053197", "punctuation-native:terminal:* [[причастный оборот]] ({{lang-ru|Вдали виднеется маяк, '''стоящий у моря'''", "875206a0ab7268a29758dcc2b9c6feff6e4a91bfb6b63ce0c7197cf2250a262e", "Вдали виднеется маяк, стоящий у моря"),
        Native("prov-u29-punctuation-native-terminal-head", "src-ruwiki-obosoblenie-144053197", "144053197", "https://ru.wikipedia.org/w/index.php?title=Обособление&oldid=144053197", "punctuation-native:terminal:* [[причастный оборот]] ({{lang-ru|Вдали виднеется маяк, '''стоящий у моря'''", "875206a0ab7268a29758dcc2b9c6feff6e4a91bfb6b63ce0c7197cf2250a262e", "маяк"),
        Native("prov-u29-punctuation-native-terminal-region-0", "src-ruwiki-obosoblenie-144053197", "144053197", "https://ru.wikipedia.org/w/index.php?title=Обособление&oldid=144053197", "punctuation-native:terminal:* [[причастный оборот]] ({{lang-ru|Вдали виднеется маяк, '''стоящий у моря'''", "875206a0ab7268a29758dcc2b9c6feff6e4a91bfb6b63ce0c7197cf2250a262e", "Вдали виднеется маяк,"),
        Native("prov-u29-punctuation-native-terminal-region-1", "src-ruwiki-obosoblenie-144053197", "144053197", "https://ru.wikipedia.org/w/index.php?title=Обособление&oldid=144053197", "punctuation-native:terminal:* [[причастный оборот]] ({{lang-ru|Вдали виднеется маяк, '''стоящий у моря'''", "875206a0ab7268a29758dcc2b9c6feff6e4a91bfb6b63ce0c7197cf2250a262e", "стоящий у моря"),
    )
    private fun sha(s: String) = MessageDigest.getInstance("SHA-256").digest(s.toByteArray(Charsets.UTF_8))
        .joinToString("") { "%02x".format(it.toInt() and 255) }
    fun ownsSource(id: String) = id in newSources
    fun admittedNative(r: V2ProvenanceRecord): Boolean {
        val pin = native.singleOrNull { it.id == r.itemId } ?: return false
        return r.unitNumber == 29 && r.objectiveId == "obj-u29-punctuation" && r.sourceId == pin.source &&
            r.sourceRevision == pin.revision && r.sourceUrl == pin.url && r.sourceRecordId == pin.record &&
            r.sourceHash == pin.hash && sha(r.originalSourceText) == pin.hash && r.shippedText == pin.text &&
            r.contentHash == sha(pin.text) && pin.text in V2Validator.unfoldWikiLabels(r.originalSourceText) &&
            r.transformation == "selection" && r.licenseId == "CC BY-SA 4.0"
    }
    fun matches(pack: V2CurriculumPack): Boolean {
        val obj = pack.unit(29)?.objectives?.singleOrNull { it.id == "obj-u29-punctuation" } ?: return false
        if (obj.checkItemIds != checkIds || obj.kind != "RECOGNITION" || !obj.gating) return false
        if (!native.all { pin -> pack.provenanceRecords.singleOrNull { it.itemId == pin.id }?.let(::admittedNative) == true }) return false
        if (!newSources.all { sid -> pack.sources.any { it.id == sid && it.license == "CC BY-SA 4.0" } &&
            pack.rights.any { it.sourceId == sid && it.license == "CC BY-SA 4.0" && it.permitsAppUse } }) return false
        val old = pack.item("u29-i-punctuation-1") ?: return false
        if (old.promptLetter != "Написанная в спешке программа выполнила недопустимую операцию." ||
            old.answer != "запятые не ставятся" || old.options != listOf("запятые не ставятся", "выделяется запятыми с двух сторон", "запятая ставится только перед причастным оборотом") ||
            old.assessment?.independent != true || V2Validator.structureValid(pack,old).isNotEmpty()) return false
        val practice = obj.practiceStepIds.flatMap { pack.step(it)?.second?.itemIds.orEmpty() }
        val guided = pack.item("u29-g-punctuation-0") ?: return false
        if (guided.id !in practice || guided.promptLetter != "Программа, написанная в спешке, выполнила недопустимую операцию." ||
            guided.answer != "выделяется запятыми с двух сторон" || guided.assessment?.independent != false ||
            V2Validator.structureValid(pack,guided).isNotEmpty()) return false
        return newIds.all { id ->
            val item = pack.item(id)
            item != null && item.objectiveId == obj.id && item.assessment?.independent == true &&
                item.assessment.testedFeature == FEATURES[id] && CompleteRuleContextPins.itemMatches(pack,item) &&
                V2Validator.structureValid(pack,item).isEmpty()
        }
    }
    private val FEATURES = mapOf("u29-i-punctuation-after" to "PUNCTUATION_CONTINUING_BOUNDARIES", "u29-i-punctuation-pronoun" to "PUNCTUATION_PERSONAL_PRONOUN", "u29-i-punctuation-cause" to "PUNCTUATION_CAUSE_EXCEPTION", "u29-i-punctuation-terminal" to "PUNCTUATION_TERMINAL_BOUNDARY")
}
