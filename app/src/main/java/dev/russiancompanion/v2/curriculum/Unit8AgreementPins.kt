package dev.russiancompanion.v2.curriculum
import java.security.MessageDigest
/** Supplied unaccented blue adjective evidence; no universal morphology/acceptance claim. */
internal object Unit8AgreementPins {
    private data class Native(val id:String,val url:String,val record:String,val hash:String,val text:String)
    private val native=listOf(
        Native("prov-u8-agreement-transfer-native-singular", "https://en.wikibooks.org/wiki/Russian/Grammar/Adjectives?oldid=4292379", "adjectives-independent-blue:===Soft endings===\nOne rather rare exception to the above rules is if the adj", "472f310b08dfe456837a47126e81dc2a40dc0c19551245589230eba3b43ce894", "синий"),
        Native("prov-u8-agreement-transfer-native-plural", "https://en.wikibooks.org/wiki/Russian/Grammar/Adjectives?oldid=4292379", "adjectives-independent-blue:===Soft endings===\nOne rather rare exception to the above rules is if the adj", "472f310b08dfe456837a47126e81dc2a40dc0c19551245589230eba3b43ce894", "синие"),
    )
    private fun sha(s:String)=MessageDigest.getInstance("SHA-256").digest(s.toByteArray(Charsets.UTF_8)).joinToString(""){"%02x".format(it.toInt() and 255)}
    fun matches(pack:V2CurriculumPack):Boolean {
        val obj=pack.unit(8)?.objectives?.singleOrNull{it.id=="obj-u8-agreement"}?:return false
        if(obj.kind!="RECOGNITION" || !obj.gating || obj.checkItemIds!=listOf("u8-i-agreement-1","u8-i-agreement-2","u8-i-agreement-3"))return false
        if(!native.all{pin->pack.provenanceRecords.singleOrNull{it.itemId==pin.id}?.let{r->
            r.unitNumber==8 && r.objectiveId==obj.id && r.sourceId=="src-wikibooks-russian-cc-by-sa" && r.sourceRevision=="4292379" &&
            r.sourceUrl==pin.url && r.sourceRecordId==pin.record && r.sourceHash==pin.hash && sha(r.originalSourceText)==pin.hash &&
            r.shippedText==pin.text && r.contentHash==sha(pin.text) && pin.text in V2Validator.unfoldWikiLabels(r.originalSourceText) &&
            r.transformation=="selection" && r.licenseId=="CC BY-SA 4.0"
        }==true})return false
        val guided=obj.practiceStepIds.flatMap{pack.step(it)?.second?.itemIds.orEmpty()}
        if(guided!=listOf("u8-g-agreement-0"))return false
        val item=pack.item("u8-i-agreement-3")?:return false
        return item.assessment?.independent==true && item.assessment.testedFeature=="PLURAL_AGREEMENT" &&
            item.promptLetter!=pack.item(guided.single())?.promptLetter && CompleteRuleContextPins.itemMatches(pack,item) && V2Validator.structureValid(pack,item).isEmpty()
    }
}
