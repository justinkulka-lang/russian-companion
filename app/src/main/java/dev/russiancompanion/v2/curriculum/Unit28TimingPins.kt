package dev.russiancompanion.v2.curriculum
import java.security.MessageDigest
/** Bounded guided literary timing; independent past/future and shared-subject evidence retained. */
internal object Unit28TimingPins {
    private data class Native(val id:String,val obj:String,val source:String,val revision:String,val url:String,val record:String,val hash:String,val text:String,val license:String)
    private val native=listOf(
        Native("prov-u28-timing-native-imperfective-clause", "obj-u28-imperfective-adverb", "src-ruwikisource-vechera-5336197", "5336197", "https://ru.wikisource.org/w/index.php?title=Вечера_у_княгини_Старобельской_(Чернышевский)&oldid=5336197", "vechera-timing-full-paragraph:f5a8d1c819401c77eafb9cce88010acdfbee067ae3f9da640bc55ba464bb2c16", "f5a8d1c819401c77eafb9cce88010acdfbee067ae3f9da640bc55ba464bb2c16", "отдыхал читая", "Public domain"),
        Native("prov-u28-timing-native-imperfective-actor", "obj-u28-imperfective-adverb", "src-ruwikisource-vechera-5336197", "5336197", "https://ru.wikisource.org/w/index.php?title=Вечера_у_княгини_Старобельской_(Чернышевский)&oldid=5336197", "vechera-timing-full-paragraph:f5a8d1c819401c77eafb9cce88010acdfbee067ae3f9da640bc55ba464bb2c16", "f5a8d1c819401c77eafb9cce88010acdfbee067ae3f9da640bc55ba464bb2c16", "Павел Сергеич Вязовский", "Public domain"),
        Native("prov-u28-timing-native-imperfective-verb", "obj-u28-imperfective-adverb", "src-openrussian-50e210c4", "50e210c4803237779cb562bc1abcea529066031c", "https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv", "verbs.csv:читать#timing-context", "db48a48e370520d2f199cdf3647c2a7bbd98760401eee7722c22c0e27eafb05d", "читать", "CC BY-SA 4.0"),
        Native("prov-u28-timing-native-imperfective-aspect", "obj-u28-imperfective-adverb", "src-openrussian-50e210c4", "50e210c4803237779cb562bc1abcea529066031c", "https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv", "verbs.csv:читать#timing-context", "db48a48e370520d2f199cdf3647c2a7bbd98760401eee7722c22c0e27eafb05d", "imperfective", "CC BY-SA 4.0"),
        Native("prov-u28-timing-native-perfective-clause", "obj-u28-perfective-adverb", "src-ruwikisource-vechera-5336197", "5336197", "https://ru.wikisource.org/w/index.php?title=Вечера_у_княгини_Старобельской_(Чернышевский)&oldid=5336197", "vechera-timing-full-paragraph:f5a8d1c819401c77eafb9cce88010acdfbee067ae3f9da640bc55ba464bb2c16", "f5a8d1c819401c77eafb9cce88010acdfbee067ae3f9da640bc55ba464bb2c16", "устав писать ложился на диван", "Public domain"),
        Native("prov-u28-timing-native-perfective-actor", "obj-u28-perfective-adverb", "src-ruwikisource-vechera-5336197", "5336197", "https://ru.wikisource.org/w/index.php?title=Вечера_у_княгини_Старобельской_(Чернышевский)&oldid=5336197", "vechera-timing-full-paragraph:f5a8d1c819401c77eafb9cce88010acdfbee067ae3f9da640bc55ba464bb2c16", "f5a8d1c819401c77eafb9cce88010acdfbee067ae3f9da640bc55ba464bb2c16", "Павел Сергеич Вязовский", "Public domain"),
        Native("prov-u28-timing-native-perfective-verb", "obj-u28-perfective-adverb", "src-openrussian-50e210c4", "50e210c4803237779cb562bc1abcea529066031c", "https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv", "verbs.csv:устать#timing-context", "4b77e66fd55de22edba1168e4be7de68d6222fbeeb2b789be1e39952aed0d225", "устать", "CC BY-SA 4.0"),
        Native("prov-u28-timing-native-perfective-aspect", "obj-u28-perfective-adverb", "src-openrussian-50e210c4", "50e210c4803237779cb562bc1abcea529066031c", "https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv", "verbs.csv:устать#timing-context", "4b77e66fd55de22edba1168e4be7de68d6222fbeeb2b789be1e39952aed0d225", "perfective", "CC BY-SA 4.0"),
    )
    private fun sha(s:String)=MessageDigest.getInstance("SHA-256").digest(s.toByteArray(Charsets.UTF_8)).joinToString(""){"%02x".format(it.toInt() and 255)}
    fun matches(pack:V2CurriculumPack):Boolean {
        if(!native.all{pin->pack.provenanceRecords.singleOrNull{it.itemId==pin.id}?.let{r->r.unitNumber==28 && r.objectiveId==pin.obj && r.sourceId==pin.source && r.sourceRevision==pin.revision && r.sourceUrl==pin.url && r.sourceRecordId==pin.record && r.sourceHash==pin.hash && sha(r.originalSourceText)==pin.hash && r.shippedText==pin.text && r.contentHash==sha(pin.text) && pin.text in r.originalSourceText && r.transformation=="selection" && r.licenseId==pin.license}==true})return false
        for(kind in listOf("imperfective","perfective")) {
            val id="obj-u28-$kind-adverb";val obj=pack.unit(28)?.objectives?.singleOrNull{it.id==id}?:return false
            if(obj.kind!="RECOGNITION" || !obj.gating || obj.checkItemIds!=listOf("u28-i-$kind-adverb-1","u28-i-$kind-adverb-2"))return false
            val guided=obj.practiceStepIds.flatMap{pack.step(it)?.second?.itemIds.orEmpty()}
            if(guided!=listOf("u28-g-$kind-adverb-0"))return false
            val g=pack.item(guided.single())?:return false
            val imp=kind=="imperfective"
            if(g.promptLetter!=(if(imp)"отдыхал читая" else "устав писать ложился на диван") || g.answer!=(if(imp)"simultaneous with the main predicate" else "anterior to a past main action") || g.options!=listOf(g.answer,"an absolute present label","a different subject from the main verb") || g.assessment?.testedFeature!=(if(imp)"IMPERFECTIVE_ADVERB_SIMULTANEOUS" else "PERFECTIVE_ADVERB_ANTERIOR") || g.assessment.independent!=false || V2Validator.structureValid(pack,g).isNotEmpty())return false
            val cue=if(imp)"читая письмо, он улыбался" else "написав письмо, она отправит его завтра"
            val answers=if(imp)listOf("simultaneous with the main predicate","the reader and the person smiling are the same person") else listOf("anterior to a future main action","the writer and sender are the same person")
            if(!obj.checkItemIds.mapIndexed{n,check->pack.item(check)?.let{i->i.promptLetter==cue && i.answer==answers[n] && i.assessment?.independent==true && V2Validator.structureValid(pack,i).isEmpty()}==true}.all{it})return false
        }
        return true
    }
}
