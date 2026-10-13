package dev.russiancompanion.v2.curriculum
import java.security.MessageDigest
/** Exact supplied question meanings and distinct cues; review pending, no productive-time claim. */
internal object Unit17QuestionPins {
    val checkIds=listOf("u17-i-question-clock","u17-i-question-book")
    private data class Native(val id:String,val url:String,val record:String,val hash:String,val text:String)
    private val native=listOf(
        Native("prov-u17-question-native-apple-ru", "https://en.wikibooks.org/wiki/Russian/Interrogative_Pronouns?oldid=4220316", "interrogative-question-transfer:=== Который? ===\nThe primary meaning is simply 'which one?', though '''которы", "086ee74b9864c2c2e799910cecdf36f255750909e989504323b33f9cb70091fa", "Которое яблоко моё?"),
        Native("prov-u17-question-native-apple-en", "https://en.wikibooks.org/wiki/Russian/Interrogative_Pronouns?oldid=4220316", "interrogative-question-transfer:=== Который? ===\nThe primary meaning is simply 'which one?', though '''которы", "086ee74b9864c2c2e799910cecdf36f255750909e989504323b33f9cb70091fa", "Which apple is mine?"),
        Native("prov-u17-question-native-clock-ru", "https://en.wikibooks.org/wiki/Russian/Interrogative_Pronouns?oldid=4220316", "interrogative-question-transfer:=== Который? ===\nThe primary meaning is simply 'which one?', though '''которы", "086ee74b9864c2c2e799910cecdf36f255750909e989504323b33f9cb70091fa", "Который час?"),
        Native("prov-u17-question-native-clock-en", "https://en.wikibooks.org/wiki/Russian/Interrogative_Pronouns?oldid=4220316", "interrogative-question-transfer:=== Который? ===\nThe primary meaning is simply 'which one?', though '''которы", "086ee74b9864c2c2e799910cecdf36f255750909e989504323b33f9cb70091fa", "What time is it?"),
        Native("prov-u17-question-native-clock-word", "https://en.wikibooks.org/wiki/Russian/Interrogative_Pronouns?oldid=4220316", "interrogative-question-transfer:=== Который? ===\nThe primary meaning is simply 'which one?', though '''которы", "086ee74b9864c2c2e799910cecdf36f255750909e989504323b33f9cb70091fa", "который"),
        Native("prov-u17-question-native-book-ru", "https://en.wikibooks.org/wiki/Russian/Interrogative_Pronouns?oldid=4220316", "interrogative-question-transfer:=== Который? ===\nThe primary meaning is simply 'which one?', though '''которы", "086ee74b9864c2c2e799910cecdf36f255750909e989504323b33f9cb70091fa", "Которую книгу вы хотите?"),
        Native("prov-u17-question-native-book-en", "https://en.wikibooks.org/wiki/Russian/Interrogative_Pronouns?oldid=4220316", "interrogative-question-transfer:=== Который? ===\nThe primary meaning is simply 'which one?', though '''которы", "086ee74b9864c2c2e799910cecdf36f255750909e989504323b33f9cb70091fa", "Which book do you want?"),
    )
    private fun sha(s:String)=MessageDigest.getInstance("SHA-256").digest(s.toByteArray(Charsets.UTF_8)).joinToString(""){"%02x".format(it.toInt() and 255)}
    fun matches(pack:V2CurriculumPack):Boolean {
        val obj=pack.unit(17)?.objectives?.singleOrNull{it.id=="obj-u17-clock"}?:return false
        if(obj.kind!="RECOGNITION" || !obj.gating || obj.checkItemIds!=checkIds)return false
        val ref=pack.unit(17)?.objectives?.singleOrNull{it.id=="obj-u17-clock-vocabulary"}?:return false
        if(ref.gating || ref.kind!="GUIDANCE" || ref.checkItemIds.isNotEmpty() || ref.practiceStepIds.isNotEmpty())return false
        if(!native.all{pin->pack.provenanceRecords.singleOrNull{it.itemId==pin.id}?.let{r->
            r.unitNumber==17 && r.objectiveId==obj.id && r.sourceId=="src-wikibooks-russian-cc-by-sa" && r.sourceRevision=="4220316" &&
            r.sourceUrl==pin.url && r.sourceRecordId==pin.record && r.sourceHash==pin.hash && sha(r.originalSourceText)==pin.hash &&
            r.shippedText==pin.text && r.contentHash==sha(pin.text) && pin.text in V2Validator.unfoldWikiLabels(r.originalSourceText) &&
            r.transformation=="selection" && r.licenseId=="CC BY-SA 4.0"
        }==true})return false
        val guided=obj.practiceStepIds.flatMap{pack.step(it)?.second?.itemIds.orEmpty()}
        if(guided!=listOf("u17-g-question-apple"))return false
        return (guided+checkIds).all{id->val item=pack.item(id)
            item!=null && item.objectiveId==obj.id && item.assessment?.independent==(id in checkIds) &&
                item.assessment.testedFeature==FEATURES[id] && CompleteRuleContextPins.itemMatches(pack,item) &&
                V2Validator.structureValid(pack,item).isEmpty()
        } && checkIds.all{id->pack.item(id)?.promptLetter!=pack.item(guided.single())?.promptLetter}
    }
    private val FEATURES=mapOf("u17-g-question-apple" to "QUESTION_OBJECT_SELECTION_GUIDED", "u17-i-question-clock" to "QUESTION_CLOCK_RECOGNITION", "u17-i-question-book" to "QUESTION_BOOK_SELECTION")
}
