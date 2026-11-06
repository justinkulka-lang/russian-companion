package dev.russiancompanion.v2.curriculum
import java.security.MessageDigest
/** Exact supplied chair context and required animate/inanimate evidence; pending qualified audit. */
internal object Unit6AnimacyPins {
 private fun sha(s:String)=MessageDigest.getInstance("SHA-256").digest(s.toByteArray(Charsets.UTF_8)).joinToString(""){"%02x".format(it.toInt() and 255)}
 fun matches(pack:V2CurriculumPack):Boolean {
  val r=pack.provenanceRecords.singleOrNull{it.itemId=="prov-u6-animacy-transfer-native-chair"}?:return false
  if(r.unitNumber!=6 || r.objectiveId!="obj-u6-animacy" || r.sourceId!="src-wikibooks-russian-cc-by-sa" || r.sourceRevision!="4330404" || r.sourceUrl!="https://en.wikibooks.org/wiki/Russian/Grammar/Accusative_case?oldid=4330404" || r.sourceRecordId!="accusative-nouns-full-context:3c0f8565ede2345d57ef60cd27ce8c719dacfc3019e9a9e2bb6b8c8ae37be858" || r.sourceHash!="3c0f8565ede2345d57ef60cd27ce8c719dacfc3019e9a9e2bb6b8c8ae37be858" || sha(r.originalSourceText)!="3c0f8565ede2345d57ef60cd27ce8c719dacfc3019e9a9e2bb6b8c8ae37be858" || r.shippedText!="Я де́лал стул." || r.contentHash!=sha(r.shippedText) || r.shippedText !in V2Validator.unfoldWikiLabels(r.originalSourceText) || r.transformation!="selection" || r.licenseId!="CC BY-SA 4.0")return false
  val o=pack.unit(6)?.objectives?.singleOrNull{it.id=="obj-u6-animacy"}?:return false
  if(o.kind!="RECOGNITION" || !o.gating || o.checkItemIds!=listOf("u6-i-animacy-1","u6-i-animacy-2","u6-i-animacy-3"))return false
  if(o.practiceStepIds.flatMap{pack.step(it)?.second?.itemIds.orEmpty()}!=listOf("u6-g-animacy-0"))return false
  val i=pack.item("u6-i-animacy-3")?:return false
  return i.objectiveId==o.id && i.assessment?.independent==true && i.assessment.testedFeature=="ANIMACY_INANIMATE_CONTEXT" && i.promptLetter==r.shippedText && CompleteRuleContextPins.itemMatches(pack,i) && V2Validator.structureValid(pack,i).isEmpty()
 }
}
