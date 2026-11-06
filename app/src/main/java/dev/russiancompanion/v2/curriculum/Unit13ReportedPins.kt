package dev.russiancompanion.v2.curriculum
import java.security.MessageDigest
/** Supplied reported/direct/time-clause recognition; no unseen or productive mastery claim. */
internal object Unit13ReportedPins {
 private data class Native(val id:String,val record:String,val hash:String,val text:String)
 private val native=listOf(
        Native("prov-u13-report-transfer-native-nationality", "future-report-full-context:83ebe0a793af0039623aecdf1a0b3e548ba94af3d8ef89259089cc01791807b1", "83ebe0a793af0039623aecdf1a0b3e548ba94af3d8ef89259089cc01791807b1", "Он сказа́л, что он англича́нин"),
        Native("prov-u13-report-transfer-native-past-nationality", "future-report-full-context:83ebe0a793af0039623aecdf1a0b3e548ba94af3d8ef89259089cc01791807b1", "83ebe0a793af0039623aecdf1a0b3e548ba94af3d8ef89259089cc01791807b1", "Он сказа́л, что он был англича́нином"),
        Native("prov-u13-report-transfer-native-evening", "future-report-full-context:83ebe0a793af0039623aecdf1a0b3e548ba94af3d8ef89259089cc01791807b1", "83ebe0a793af0039623aecdf1a0b3e548ba94af3d8ef89259089cc01791807b1", "Она́ сказа́ла, что она́ ска́жет ему́ ве́чером"),
 )
 private fun sha(s:String)=MessageDigest.getInstance("SHA-256").digest(s.toByteArray(Charsets.UTF_8)).joinToString(""){"%02x".format(it.toInt() and 255)}
 fun matches(pack:V2CurriculumPack):Boolean {
  if(!native.all{pin->pack.provenanceRecords.singleOrNull{it.itemId==pin.id}?.let{r->r.unitNumber==13 && r.objectiveId=="obj-u13-reported" && r.sourceId=="src-wikibooks-russian-cc-by-sa" && r.sourceRevision=="3353822" && r.sourceUrl=="https://en.wikibooks.org/wiki/Russian/Grammar/Future_tense?oldid=3353822" && r.sourceRecordId==pin.record && r.sourceHash==pin.hash && sha(r.originalSourceText)==pin.hash && r.shippedText==pin.text && r.contentHash==sha(pin.text) && pin.text in V2Validator.unfoldWikiLabels(r.originalSourceText) && r.transformation=="selection" && r.licenseId=="CC BY-SA 4.0"}==true})return false
  val o=pack.unit(13)?.objectives?.singleOrNull{it.id=="obj-u13-reported"}?:return false
  if(o.kind!="RECOGNITION" || !o.gating || o.checkItemIds!=listOf("u13-i-reported-1","u13-i-reported-2","u13-i-report-frame-time"))return false
  val guided=o.practiceStepIds.flatMap{pack.step(it)?.second?.itemIds.orEmpty()};if(guided!=listOf("u13-g-reported-0"))return false
  return listOf("u13-g-reported-0","u13-i-report-frame-time").all{id->pack.item(id)?.let{i->i.objectiveId==o.id && i.assessment?.independent==(id=="u13-i-report-frame-time") && i.assessment.testedFeature==(if(id=="u13-g-reported-0")"REPORTED_NATIONALITY_GUIDED" else "REPORTED_FRAME_EMBEDDED_TIME") && CompleteRuleContextPins.itemMatches(pack,i) && V2Validator.structureValid(pack,i).isEmpty()}==true}
 }
}
