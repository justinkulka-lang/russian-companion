package dev.russiancompanion.v2.curriculum
import java.security.MessageDigest
/** Supplied subject-role interpretation, pending qualified audit; legacy glosses are optional. */
internal object Unit4NominativeRolePins {
 private data class Native(val id:String,val text:String)
 private val native=listOf(
  Native("prov-u4-role-native-husband","Муж говорит"),
  Native("prov-u4-role-native-train","Поезд старый"),
  Native("prov-u4-role-native-student","Студент новый"),
 )
 private fun sha(s:String)=MessageDigest.getInstance("SHA-256").digest(s.toByteArray(Charsets.UTF_8)).joinToString(""){"%02x".format(it.toInt() and 255)}
 fun matches(p:V2CurriculumPack):Boolean {
  val o=p.unit(4)?.objectives?.singleOrNull{it.id=="obj-u4-nominative"}?:return false
  if(!o.gating || o.kind!="RECOGNITION" || o.practiceStepIds!=listOf("u4-st-guided-nominative") || o.checkItemIds!=listOf("u4-i-nom-role-train","u4-i-nom-role-student"))return false
  if(p.step(o.practiceStepIds.single())?.second?.itemIds!=listOf("u4-g-nom-role"))return false
  if(!native.all{pin->p.provenanceRecords.singleOrNull{it.itemId==pin.id}?.let{r->r.unitNumber==4 && r.objectiveId==o.id && r.sourceId=="src-wikibooks-russian-cc-by-sa" && r.sourceRevision=="4213267" && r.sourceUrl=="https://en.wikibooks.org/wiki/Russian/Grammar/Nominative?oldid=4213267" && r.sourceRecordId=="nominative-subject-full-context:e322951175f99463f6cd2a6bf089b359616b073be118129ec3f914dcb4e84de6" && r.sourceHash=="e322951175f99463f6cd2a6bf089b359616b073be118129ec3f914dcb4e84de6" && sha(r.originalSourceText)=="e322951175f99463f6cd2a6bf089b359616b073be118129ec3f914dcb4e84de6" && r.shippedText==pin.text && r.contentHash==sha(pin.text) && pin.text in V2Validator.unfoldWikiLabels(r.originalSourceText) && r.transformation=="selection" && r.licenseId=="CC BY-SA 4.0"}==true})return false
  if(!listOf("u4-g-nom-role","u4-i-nom-role-train","u4-i-nom-role-student").zip(native).all{(id,n)->p.item(id)?.let{i->i.objectiveId==o.id && i.promptLetter==n.text && i.assessment?.independent==(id!="u4-g-nom-role") && i.assessment.testedFeature=="NOMINATIVE_CASE_ROLE" && CompleteRuleContextPins.itemMatches(p,i) && V2Validator.structureValid(p,i).isEmpty()}==true})return false
  val ref=p.unit(4)?.objectives?.singleOrNull{it.id=="obj-u4-nominative-gloss-reference"}?:return false
  return !ref.gating && ref.kind=="GUIDANCE" && ref.checkItemIds.isEmpty() && ref.practiceStepIds.isEmpty() && listOf("u4-g-nominative-1","u4-i-nom-muzh","u4-i-nom-poezd").all{id->p.item(id)?.let{it.kind=="SOURCE_MEANING" && it.objectiveId==ref.id && it.assessment==null}==true}
 }
}
