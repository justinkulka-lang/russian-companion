#!/usr/bin/env python3
"""Source-native reported context, independent frame interpretation; no generated Russian."""
import sys,json,hashlib
from pathlib import Path
from copy import deepcopy
ROOT=Path(__file__).resolve().parents[2];sys.path.insert(0,str(ROOT/'catalog'))
from curriculum_storage import loads,dumps
PACK=ROOT/'app/src/main/assets/content/v2-curriculum.json';BASELINE='d7d84571fa8374c91561e1cdfde52d43ff79a787';OBJ='obj-u13-reported'
sha=lambda s:hashlib.sha256(s.encode()).hexdigest()
def main():
 raw=PACK.read_bytes();p=loads(raw)
 if any(i['id']=='u13-i-report-frame-time' for i in p['items']):return
 source=(ROOT/'catalog/sources/units-11-15/wikibooks/future-tense-3353822.txt').read_text();span=source[source.index('=== Present or future? ==='):source.index('== The imperfective future ==')].strip()
 primary=next(r for r in p['provenanceRecords'] if r['itemId']=='prov-ex-u13-tense-keep-0-ru');english=next(r for r in p['provenanceRecords'] if r['itemId']=='prov-ca-u5-behavior-teach-1-body')
 basis=('Complete retained Wikibooks Future3353822 Present or future subsection with nationality comparison and direct/reported evening examples, existing CC BY-SA4.0 contributor-history/rights route Copyrights4622060. Native clauses preserve source stress/words/punctuation; only mechanical emphasis/link-label unfolding. Independent English,2026-10-13. Bounded supplied-context recognition, not generating reports or universally mandatory English backshift/Russian tense retention/pronoun replacement. The source mislabels быть perfective and overgeneralizes aspect/completion/repetition and every original tense; those claims are excluded. Guided nationality example has already been taught earlier; new report uses existing evening context, not unseen evidence. Source/rights/linguistic acceptance pending; no private book prose.')
 added=[];native=[];changes=[];pins={}
 def record(text,name,original=None,apps=None,rid=None):
  original=original or basis+'\n'+text;r=deepcopy(english);r.update(itemId='prov-ca-u13-report-transfer-'+name,unitNumber=13,objectiveId=OBJ,sourceRecordId=rid or 'ca-coverage:'+OBJ+':report-transfer:'+name,originalSourceText=original,shippedText=text,sourceHash=sha(original),contentHash=sha(text));r['synthesis'].update(bibliography=basis,authorshipDeclaration='Independently authored English,2026-10-13; separately pinned exact native selections; no generated native clause/private book prose.',objectiveOrClaimBinding=OBJ+':report-transfer:'+name,applications=apps or []);p['provenanceRecords'].append(r);added.append(r['itemId']);return r['itemId']
 for name,text in [('nationality','Он сказа́л, что он англича́нин'),('past-nationality','Он сказа́л, что он был англича́нином'),('evening','Она́ сказа́ла, что она́ ска́жет ему́ ве́чером')]:
  assert text in source.replace("'''",'').replace("''",'');r=deepcopy(primary);r.pop('originalSourceTextRef',None);r.update(itemId='prov-u13-report-transfer-native-'+name,unitNumber=13,objectiveId=OBJ,sourceRecordId='future-report-full-context:'+sha(span),originalSourceText=span,sourceHash=sha(span),shippedText=text,contentHash=sha(text));p['provenanceRecords'].append(r);added.append(r['itemId']);native.append(deepcopy(r))
 template=next(i for i in p['items'] if i['id']=='u13-g-reported-0')
 for id,cue,key,wrong,feature,ind in [('u13-g-reported-0',native[0]['shippedText'],'Past reporting verb with present nationality statement.','Past reporting verb with past nationality statement.','REPORTED_NATIONALITY_GUIDED',False),('u13-i-report-frame-time',native[2]['shippedText'],'Past reporting verb with a future embedded action.','Future reporting verb with a past embedded action.','REPORTED_FRAME_EMBEDDED_TIME',True)]:
  i=deepcopy(template);was=deepcopy(template) if not ind else None;opts=[key,wrong]
  prompt=('In this supplied report of “I am English”, which English interpretation preserves the original present nationality statement?' if not ind else 'Which English interpretation identifies the reporting verb and embedded action in this supplied evening report?')
  detail=('The reporting verb is past and the nationality statement has no past copula. The alternative with был explicitly supplies a past copula and changes the stated reading; it is not universally ungrammatical.' if not ind else 'The reporting verb said is past; the embedded verb about telling him retains future form. Future is interpreted within the reported content, not a guarantee that the action is after the present moment or has been completed.')
  feedback='In '+cue+', '+key+' '+detail+' Recognize this supplied context; English backshift and Russian tense/pronoun choices are not automatic rules for every report.'
  reasons=['',wrong+' This does not describe the finite forms in the supplied target context.'];i.update(id=id,kind='CONTRAST_SELECTION',sourceId=english['sourceId'],prompt=prompt,promptLetter=cue,options=opts,answer=key,feedback=feedback,distractorRationales=reasons)
  app=dict(cue=cue,answer=key,distractors=[wrong]);f=lambda s:str(len(s.encode()))+':'+s;canonical='tuple-v1:1;'+f(cue)+f(key)+'1;'+f(wrong);original=basis+'\nContext:\n'+feedback+'\nTuple-v1:'+canonical;rid='ca-rule-context:'+id;ids=[record(v,id+'-'+str(n),original,[app],rid) for n,v in enumerate([cue,key,wrong])]
  i['assessment'].update(testedFeature=feature,independent=ind,contrastSetId=id,cueLanguage='ru',responseKind='ENGLISH_CLASSIFICATION',evidenceMode='STATED_APPLICATION',relationKind='STATED_APPLICATION',relationParticipantIds=ids,answerSourceIds=[ids[1]],distractorSourceIds=['',ids[2]],distractorDifference=reasons,contrastMembers=opts,whyAnswerDemonstratesObjective=feedback)
  pins[rid]=dict(sourceHash=sha(original),applications=[app],objectiveId=OBJ)
  for name,text in [('prompt',prompt),('feedback',feedback),('reason',reasons[1])]:record(text,id+'-'+name)
  if ind:p['items'].append(i)
  else:p['items'][p['items'].index(template)]=i
  changes.append(dict(before=was,after=deepcopy(i)))
 unit=p['units'][12];obj=next(o for o in unit['objectives'] if o['id']==OBJ);was=deepcopy(obj);obj['title']='Recognize supplied reports, direct statements and future time clauses';obj['checkItemIds'].append('u13-i-report-frame-time');changes.append(dict(before=was,after=deepcopy(obj)));record(obj['title'],'objective-title')
 session=next(s for s in unit['sessions'] if s['id']=='u13-s-reported');was=deepcopy(session);bodies=[
  'A past reporting frame can contain a statement whose tense is interpreted from the reported speaker’s perspective. Он сказа́л, что он англича́нин has a past said verb and no past copula in the nationality statement. The supplied original is “I am English”. '+p['items'][next(n for n,i in enumerate(p['items']) if i['id']=='u13-g-reported-0')]['answer']+' is the matching description. The source’s alternative with a past copula changes the stated reading. That alternative can be grammatical in a different context; it is not this supplied present-nationality report. This nationality context was also taught earlier, so it is not unseen evidence.',
  'Я скажу ему ве́чером is the direct statement “I will tell him in the evening”. Она́ сказа́ла, что она́ ска́жет ему́ ве́чером is the supplied reported evening context. Past reporting verb with a future embedded action. describes its two finite verbs: said is past, and the embedded telling action retains future form within reported content. This does not imply the action must still be future compared with the present moment. The source supplies these speaker/pronoun choices; do not mechanically replace every I with she or manufacture new reported clauses.',
  'Когда́ я бу́ду в Москве́, я бу́ду говори́ть то́лько по-ру́сски gives a future time clause and a plan to speak Russian. It has no past reporting frame. Existing independent choices distinguish this supplied Moscow plan and the direct evening statement. The added independent check must also interpret the evening report’s past reporting verb and future embedded action. English backshift is not compulsory in every report; preserving these source readings is not a universal rule that every tense or word stays unchanged. This small set supports recognition of supplied contexts, not unrestricted reported-speech production.'
 ]
 teaching=[s for s in session['steps'] if s['kind']=='TEACH'];assert len(teaching)==3
 for n,(step,body) in enumerate(zip(teaching,bodies)):step.update(title=['Interpret the nationality report','Separate reporting time from embedded time','Distinguish a report, direct statement and time clause'][n],body=body);record(step['title'],'teach-'+str(n)+'-title');record(body,'teach-'+str(n)+'-body')
 session['steps']=teaching+[s for s in session['steps'] if s['kind']!='TEACH']
 for step in session['steps']:
  if step['kind']=='CHECK':step['itemIds'].append('u13-i-report-frame-time')
  if step['kind'] in ['GUIDED','CHECK']:step['body']='Interpret the complete supplied context. A missed choice returns to all teaching cards and worked examples.';record(step['body'],step['id']+'-body')
  if step['kind']=='REMEDIATE':step['body']='\n\n'.join(bodies);step['exampleIds']=list(dict.fromkeys(e for t in teaching for e in t.get('exampleIds',[])));record(step['body'],'remediation-body')
 changes.append(dict(before=was,after=deepcopy(session)))
 q=lambda x:json.dumps(x,ensure_ascii=False)
 rows=['        Native('+', '.join(q(r[k]) for k in ['itemId','sourceRecordId','sourceHash','shippedText'])+'),' for r in native]
 code='''package dev.russiancompanion.v2.curriculum
import java.security.MessageDigest
/** Supplied reported/direct/time-clause recognition; no unseen or productive mastery claim. */
internal object Unit13ReportedPins {
 private data class Native(val id:String,val record:String,val hash:String,val text:String)
 private val native=listOf(
ROWS
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
'''.replace('ROWS','\n'.join(rows));(ROOT/'app/src/main/java/dev/russiancompanion/v2/curriculum/Unit13ReportedPins.kt').write_text(code)
 file=ROOT/'app/src/main/java/dev/russiancompanion/v2/curriculum/CompleteRuleContextPins.kt';s=file.read_text();anchor='    )\n    fun required';s=s.replace(anchor,''.join('        '+q(k)+' to '+q(v['sourceHash'])+',\n' for k,v in pins.items())+anchor);s=s.replace('            item.id == "u24-g-context-subject-focus"', '            item.id in setOf("u13-g-reported-0","u13-i-report-frame-time") -> "obj-u13-reported"\n            item.id == "u24-g-context-subject-focus"');file.write_text(s)
 after=dumps(p).encode();PACK.write_bytes(after)
 (ROOT/'audit/content-corrections/UNIT13_REPORTED_TRANSFER_CORRECTION.json').write_text(json.dumps(dict(baselineCommit=BASELINE,beforeSha256=hashlib.sha256(raw).hexdigest(),afterSha256=hashlib.sha256(after).hexdigest(),changes=changes,addedProvenanceIds=added,nativeSelections=native,contextIdentities=pins,bibliography=basis,nativePinCodeSha256=sha(code),semanticAcceptance=False,unseenContextClaim=False,productiveReportedSpeechClaim=False),ensure_ascii=False,indent=2)+'\n');print('Added',len(added),'pending records,one substantive independent check; original checks preserved')
if __name__=='__main__':main()
