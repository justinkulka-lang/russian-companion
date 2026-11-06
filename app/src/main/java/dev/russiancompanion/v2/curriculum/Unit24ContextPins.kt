package dev.russiancompanion.v2.curriculum
import java.security.MessageDigest
/** Distinct introductory context; two original independent persuasion claims preserved. */
internal object Unit24ContextPins {
    private data class Native(val id:String,val obj:String,val record:String,val hash:String,val text:String)
    private val native=listOf(
        Native("prov-u24-context-transfer-native-guide-ru", "obj-u24-word-order-contrast", "context-guided-subject-focus:== Syntax ==\nBasic word order, both in conversation and written language, is ", "de21f95b9b994a9013d8f6d9796ffde4c38648b48c3156fce81fb66427522205", "В магазин пошёл я."),
        Native("prov-u24-context-transfer-native-guide-en", "obj-u24-word-order-contrast", "context-guided-subject-focus:== Syntax ==\nBasic word order, both in conversation and written language, is ", "de21f95b9b994a9013d8f6d9796ffde4c38648b48c3156fce81fb66427522205", "It was me who went to the shop."),
        Native("prov-u24-context-transfer-native-destination-ru", "obj-u24-word-order-contrast", "context-guided-subject-focus:== Syntax ==\nBasic word order, both in conversation and written language, is ", "de21f95b9b994a9013d8f6d9796ffde4c38648b48c3156fce81fb66427522205", "В магазин я пошёл."),
        Native("prov-u24-context-transfer-native-destination-en", "obj-u24-word-order-contrast", "context-guided-subject-focus:== Syntax ==\nBasic word order, both in conversation and written language, is ", "de21f95b9b994a9013d8f6d9796ffde4c38648b48c3156fce81fb66427522205", "To the shop—that's where I went."),
        Native("prov-u24-context-transfer-native-reference-prov-ex-u24-word-order-contrast-0-ru", "obj-u24-persuasion-reference", "grammar:In some cases, alternative word order can change the meaning entirely:\n* Не надо", "ef9f1bcd815ab4b23f2e073b493930ea41b5ad688f79a77310c415ccb7f9e668", "Не надо меня уговаривать."),
        Native("prov-u24-context-transfer-native-reference-prov-ex-u24-word-order-contrast-0-en", "obj-u24-persuasion-reference", "grammar:In some cases, alternative word order can change the meaning entirely:\n* Не надо", "ef9f1bcd815ab4b23f2e073b493930ea41b5ad688f79a77310c415ccb7f9e668", "One should not persuade me"),
        Native("prov-u24-context-transfer-native-reference-prov-ex-u24-word-order-contrast-1-ru", "obj-u24-persuasion-reference", "grammar:In some cases, alternative word order can change the meaning entirely:\n* Не надо", "ef9f1bcd815ab4b23f2e073b493930ea41b5ad688f79a77310c415ccb7f9e668", "Меня не надо уговаривать."),
        Native("prov-u24-context-transfer-native-reference-prov-ex-u24-word-order-contrast-1-en", "obj-u24-persuasion-reference", "grammar:In some cases, alternative word order can change the meaning entirely:\n* Не надо", "ef9f1bcd815ab4b23f2e073b493930ea41b5ad688f79a77310c415ccb7f9e668", "There is no need to persuade me"),
    )
    private val originalPersuasion=listOf(
        Native("prov-ex-u24-word-order-contrast-0-ru", "obj-u24-word-order-contrast", "grammar:In some cases, alternative word order can change the meaning entirely:\n* Не надо", "ef9f1bcd815ab4b23f2e073b493930ea41b5ad688f79a77310c415ccb7f9e668", "Не надо меня уговаривать."),
        Native("prov-ex-u24-word-order-contrast-0-en", "obj-u24-word-order-contrast", "grammar:In some cases, alternative word order can change the meaning entirely:\n* Не надо", "ef9f1bcd815ab4b23f2e073b493930ea41b5ad688f79a77310c415ccb7f9e668", "One should not persuade me"),
        Native("prov-ex-u24-word-order-contrast-1-ru", "obj-u24-word-order-contrast", "grammar:In some cases, alternative word order can change the meaning entirely:\n* Не надо", "ef9f1bcd815ab4b23f2e073b493930ea41b5ad688f79a77310c415ccb7f9e668", "Меня не надо уговаривать."),
        Native("prov-ex-u24-word-order-contrast-1-en", "obj-u24-word-order-contrast", "grammar:In some cases, alternative word order can change the meaning entirely:\n* Не надо", "ef9f1bcd815ab4b23f2e073b493930ea41b5ad688f79a77310c415ccb7f9e668", "There is no need to persuade me"),
    )
    private fun sha(s:String)=MessageDigest.getInstance("SHA-256").digest(s.toByteArray(Charsets.UTF_8)).joinToString(""){"%02x".format(it.toInt() and 255)}
    private fun matches(r:V2ProvenanceRecord,pin:Native)=r.unitNumber==24 && r.objectiveId==pin.obj && r.sourceId=="src-wikipedia-russian-grammar-1369617627" && r.sourceRevision=="1369617627" &&
        r.sourceUrl=="https://en.wikipedia.org/w/index.php?title=Russian_grammar&oldid=1369617627" && r.sourceRecordId==pin.record && r.sourceHash==pin.hash && sha(r.originalSourceText)==pin.hash &&
        r.shippedText==pin.text && r.contentHash==sha(pin.text) && pin.text in V2Validator.unfoldWikiLabels(r.originalSourceText) && r.transformation=="selection" && r.licenseId=="CC BY-SA 4.0"
    fun admittedNewNative(r:V2ProvenanceRecord)=native.singleOrNull{it.id==r.itemId}?.let{matches(r,it)}==true
    fun matches(pack:V2CurriculumPack):Boolean {
        val obj=pack.unit(24)?.objectives?.singleOrNull{it.id=="obj-u24-word-order-contrast"}?:return false
        if(obj.kind!="RECOGNITION" || !obj.gating || obj.checkItemIds!=listOf("u24-i-word-order-contrast-1","u24-i-word-order-contrast-2"))return false
        if(!(native+originalPersuasion).all{pin->pack.provenanceRecords.singleOrNull{it.itemId==pin.id}?.let{matches(it,pin)}==true})return false
        val ref=pack.unit(24)?.objectives?.singleOrNull{it.id=="obj-u24-persuasion-reference"}?:return false
        if(ref.kind!="GUIDANCE" || ref.gating || ref.practiceStepIds.isNotEmpty() || ref.checkItemIds.isNotEmpty())return false
        val optional=pack.item("u24-g-word-order-contrast-0")?:return false
        if(optional.kind!="SOURCE_MEANING" || optional.objectiveId!=ref.id || optional.assessment!=null || optional.promptLetter!="Не надо меня уговаривать." || optional.answer!="One should not persuade me" || optional.options!=listOf("One should not persuade me","There is no need to persuade me"))return false
        val pairs=listOf(Triple("u24-i-word-order-contrast-1","Меня не надо уговаривать.","There is no need to persuade me"),Triple("u24-i-word-order-contrast-2","Не надо меня уговаривать.","One should not persuade me"))
        if(!pairs.all{(id,cue,answer)->pack.item(id)?.let{it.promptLetter==cue && it.answer==answer && it.assessment?.independent==true && V2Validator.structureValid(pack,it).isEmpty()}==true})return false
        val guided=obj.practiceStepIds.flatMap{pack.step(it)?.second?.itemIds.orEmpty()};if(guided!=listOf("u24-g-context-subject-focus"))return false
        val item=pack.item(guided.single())?:return false
        return item.assessment?.independent==false && item.assessment.testedFeature=="WORD_ORDER_SUBJECT_FOCUS_GUIDED" && pairs.none{it.second==item.promptLetter} && CompleteRuleContextPins.itemMatches(pack,item) && V2Validator.structureValid(pack,item).isEmpty()
    }
}
