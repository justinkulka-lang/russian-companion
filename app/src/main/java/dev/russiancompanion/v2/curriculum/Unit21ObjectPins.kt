package dev.russiancompanion.v2.curriculum
import java.security.MessageDigest
/** Distinct supplied direct-object reflexive recognition; qualified review remains pending. */
internal object Unit21ObjectPins {
    const val SOURCE="src-enwiki-reflexive-pronoun-1374712982"
    private data class Native(val id:String,val url:String,val record:String,val hash:String,val text:String)
    private val native=listOf(
        Native("prov-u21-object-transfer-native-self", "https://en.wikipedia.org/w/index.php?title=Reflexive_pronoun&oldid=1374712982", "reflexive-independent-direct-object:=== Russian ===\n\nIn [[Russian Language|Russian]], the pronoun {{lang|ru|себя}", "0111295b473dbe0dd7f6353d48e7bbeccf180e48aa7a93b3cf3827dd62affa50", "Он поранил себя"),
        Native("prov-u21-object-transfer-native-other", "https://en.wikipedia.org/w/index.php?title=Reflexive_pronoun&oldid=1374712982", "reflexive-independent-direct-object:=== Russian ===\n\nIn [[Russian Language|Russian]], the pronoun {{lang|ru|себя}", "0111295b473dbe0dd7f6353d48e7bbeccf180e48aa7a93b3cf3827dd62affa50", "Он поранил его"),
        Native("prov-u21-object-transfer-native-reflexive", "https://en.wikipedia.org/w/index.php?title=Reflexive_pronoun&oldid=1374712982", "reflexive-independent-direct-object:=== Russian ===\n\nIn [[Russian Language|Russian]], the pronoun {{lang|ru|себя}", "0111295b473dbe0dd7f6353d48e7bbeccf180e48aa7a93b3cf3827dd62affa50", "себя"),
        Native("prov-u21-object-transfer-native-personal", "https://en.wikipedia.org/w/index.php?title=Reflexive_pronoun&oldid=1374712982", "reflexive-independent-direct-object:=== Russian ===\n\nIn [[Russian Language|Russian]], the pronoun {{lang|ru|себя}", "0111295b473dbe0dd7f6353d48e7bbeccf180e48aa7a93b3cf3827dd62affa50", "его"),
    )
    private fun sha(s:String)=MessageDigest.getInstance("SHA-256").digest(s.toByteArray(Charsets.UTF_8)).joinToString(""){"%02x".format(it.toInt() and 255)}
    fun admittedNative(r:V2ProvenanceRecord):Boolean {
        val pin=native.singleOrNull{it.id==r.itemId}?:return false
        return r.unitNumber==21 && r.objectiveId=="obj-u21-object-reference" && r.sourceId==SOURCE && r.sourceRevision=="1374712982" &&
            r.sourceUrl==pin.url && r.sourceRecordId==pin.record && r.sourceHash==pin.hash && sha(r.originalSourceText)==pin.hash &&
            r.shippedText==pin.text && r.contentHash==sha(pin.text) && pin.text in r.originalSourceText &&
            r.transformation=="selection" && r.licenseId=="CC BY-SA 4.0" && r.attribution.contains("Wikipedia:Copyrights oldid 1369466282") && r.audioAssetId==null
    }
    fun matches(pack:V2CurriculumPack):Boolean {
        val obj=pack.unit(21)?.objectives?.singleOrNull{it.id=="obj-u21-object-reference"}?:return false
        if(obj.kind!="RECOGNITION" || !obj.gating || obj.checkItemIds!=listOf("u21-i-object-reference-1","u21-i-object-reference-2"))return false
        if(!native.all{pin->pack.provenanceRecords.singleOrNull{it.itemId==pin.id}?.let(::admittedNative)==true})return false
        if(pack.sources.none{it.id==SOURCE && it.license=="CC BY-SA 4.0"} || pack.rights.none{it.sourceId==SOURCE && it.license=="CC BY-SA 4.0" && it.permitsAppUse})return false
        val guided=obj.practiceStepIds.flatMap{pack.step(it)?.second?.itemIds.orEmpty()};if(guided!=listOf("u21-g-object-reference-0"))return false
        val item=pack.item("u21-i-object-reference-2")?:return false
        return item.assessment?.independent==true && item.assessment.testedFeature=="REFLEXIVE_MYSELF_SENTENCE" &&
            item.promptLetter!=pack.item(guided.single())?.promptLetter && CompleteRuleContextPins.itemMatches(pack,item) && V2Validator.structureValid(pack,item).isEmpty()
    }
}
