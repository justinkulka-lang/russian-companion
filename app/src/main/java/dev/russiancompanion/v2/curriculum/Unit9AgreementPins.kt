package dev.russiancompanion.v2.curriculum
import java.security.MessageDigest
/** Exact one/hour counting evidence; no all-case or productive mastery claim. */
internal object Unit9AgreementPins {
    private data class Native(val id:String,val url:String,val record:String,val hash:String,val text:String)
    private val native=listOf(
        Native("prov-u9-agreement-transfer-native-noun", "https://en.wikibooks.org/wiki/Russian/Numbers?oldid=4371358", "numbers-independent-hour:==Use of numerals in context==\n\nIn English (and in many other Indo-European l", "e05d0a76571644c1d271afe936b827bee4829ffd63c0ea854f4fcbb5fbf95122", "час"),
        Native("prov-u9-agreement-transfer-native-masculine", "https://en.wikibooks.org/wiki/Russian/Numbers?oldid=4371358", "numbers-independent-hour:==Use of numerals in context==\n\nIn English (and in many other Indo-European l", "e05d0a76571644c1d271afe936b827bee4829ffd63c0ea854f4fcbb5fbf95122", "один"),
        Native("prov-u9-agreement-transfer-native-feminine", "https://en.wikibooks.org/wiki/Russian/Numbers?oldid=4371358", "numbers-independent-hour:==Use of numerals in context==\n\nIn English (and in many other Indo-European l", "e05d0a76571644c1d271afe936b827bee4829ffd63c0ea854f4fcbb5fbf95122", "одна"),
    )
    private fun sha(s:String)=MessageDigest.getInstance("SHA-256").digest(s.toByteArray(Charsets.UTF_8)).joinToString(""){"%02x".format(it.toInt() and 255)}
    fun matches(pack:V2CurriculumPack):Boolean {
        val obj=pack.unit(9)?.objectives?.singleOrNull{it.id=="obj-u9-odindva"}?:return false
        if(obj.kind!="RECOGNITION" || !obj.gating || obj.checkItemIds!=listOf("u9-i-odindva-1","u9-i-odindva-2","u9-i-odindva-3","u9-i-odindva-4","u9-i-odindva-5"))return false
        if(!native.all{pin->pack.provenanceRecords.singleOrNull{it.itemId==pin.id}?.let{r->
            r.unitNumber==9 && r.objectiveId==obj.id && r.sourceId=="src-wikibooks-russian-cc-by-sa" && r.sourceRevision=="4371358" &&
            r.sourceUrl==pin.url && r.sourceRecordId==pin.record && r.sourceHash==pin.hash && sha(r.originalSourceText)==pin.hash &&
            r.shippedText==pin.text && r.contentHash==sha(pin.text) && pin.text in V2Validator.unfoldWikiLabels(r.originalSourceText) &&
            r.transformation=="selection" && r.licenseId=="CC BY-SA 4.0"
        }==true})return false
        val guided=obj.practiceStepIds.flatMap{pack.step(it)?.second?.itemIds.orEmpty()}
        if(guided!=listOf("u9-g-odindva-0"))return false
        val item=pack.item("u9-i-odindva-4")?:return false
        return item.assessment?.independent==true && item.assessment.testedFeature=="ODIN_MASCULINE" &&
            item.promptLetter!=pack.item(guided.single())?.promptLetter && CompleteRuleContextPins.itemMatches(pack,item) && V2Validator.structureValid(pack,item).isEmpty()
    }
}
