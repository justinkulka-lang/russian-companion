#!/usr/bin/env python3
"""Keep the two correct independent persuasion readings; supply a different guided example."""
import json,hashlib,re
from copy import deepcopy
from pathlib import Path
HERE=Path(__file__).resolve().parent;ROOT=HERE.parents[1];PACK=ROOT/'app/src/main/assets/content/v2-curriculum.json';PIN_FILE=ROOT/'app/src/main/java/dev/russiancompanion/v2/curriculum/Unit24ContextPins.kt'
BASELINE='3b8b6df0150d406f8f71a2bc7eebd1a19a301eba';OBJ='obj-u24-word-order-contrast';REF='obj-u24-persuasion-reference';NEW='u24-g-context-subject-focus';OLD='u24-g-word-order-contrast-0'
def sha(s):return hashlib.sha256(s.encode()).hexdigest()
def visible(s):return re.sub(r'\[\[([^\[\]]+)\]\]',lambda m:m[1].split('|',1)[-1],s).replace("'''",'').replace("''",'')
def canonical(a):
 def field(s):return str(len(s.encode()))+':'+s
 return 'tuple-v1:1;'+field(a['cue'])+field(a['answer'])+str(len(a['distractors']))+';'+''.join(field(s) for s in a['distractors'])
def main():
 raw=PACK.read_bytes();p=json.loads(raw)
 if any(i['id']==NEW for i in p['items']):print('Already repaired; evidence unchanged');return
 f=ROOT/'catalog/sources/units-26-30/wikipedia/russian-grammar-1369617627.txt';source=f.read_text();span=source[source.index('== Syntax =='):source.index('=== Impersonal sentences ===')].rstrip()
 basis=('Complete retained Wikipedia Russian grammar1369617627 Syntax section, actual retained Wikipedia:Copyrights1369466282/CC BY-SA4.0 contributor-history/stable-oldid attribution. Exact native and English selections with existing mechanical emphasis/link-label unfolding; no native generation, stress insertion, punctuation repair, private Penguin prose/media or new source/rights declaration. '
 'Guided subject-focus example В магазин пошёл я. / It was me who went to the shop. is distinct from both independent persuasion sentences. The objective explicitly covers supplied readings in stated contexts; independently assessed claims remain the two original persuasion readings, unchanged. Subject-focus is introductory guided evidence, not independently demonstrated emphasis mastery. '
 'Preserve original persuasion native examples, both correct independent items, all prior provenance/capabilities/source/rights/mappings and other units. Original repeated correct guided persuasion item stays optional SOURCE_MEANING on non-gating GUIDANCE, outside prescribed credit. '
 'No universal word-order/intonation meaning switch, all possible permutations, inference of present time from a past verb, on-foot meaning without its supplied context or productive grammar claim. The broad/inaccurate original source claims remain raw historical evidence but are not adopted. English explanation keeps actual context and ambiguity; never infer prohibition versus no-need from word order alone. '
 'All three teaching cards precede practice; full remediation remains. Independently authored English2026-10-13 within content-audit-2026-10-08; qualified source/rights/linguistic review pending.')
 template=next(r for r in p['provenanceRecords'] if r['itemId']=='prov-ca-u5-behavior-teach-1-body');primary=next(r for r in p['provenanceRecords'] if r['itemId']=='prov-ex-u24-word-order-contrast-0-ru');added=[];native=[];changes=[];rebindings=[]
 def record(value,name,obj=OBJ,original=None,apps=None,rid=None):
  original=original or basis+'\n'+value;r=deepcopy(template);r.update(itemId='prov-ca-u24-context-transfer-'+name,unitNumber=24,objectiveId=obj,recordType='label',sourceRecordId=rid or 'ca-coverage:'+obj+':transfer:'+name,originalSourceText=original,shippedText=value,sourceHash=sha(original),contentHash=sha(value));r['synthesis'].update(bibliography=basis,authorshipDeclaration='Independently authored English from full frozen supplied context; no native generation/private prose.',objectiveOrClaimBinding=obj+':transfer:'+name,applications=apps or []);p['provenanceRecords'].append(r);added.append(r['itemId']);return r['itemId']
 for suffix,text in [('guide-ru','В магазин пошёл я.'),('guide-en','It was me who went to the shop.'),('destination-ru','В магазин я пошёл.'),('destination-en',"To the shop—that's where I went.")]:
  assert text in visible(span);r=deepcopy(primary);r.pop('originalSourceTextRef',None);r.update(itemId='prov-u24-context-transfer-native-'+suffix,recordType='example' if suffix.endswith('ru') else 'meaning',sourceRecordId='context-guided-subject-focus:'+span[:77],originalSourceText=span,shippedText=text,sourceHash=sha(span),contentHash=sha(text));p['provenanceRecords'].append(r);native.append(deepcopy(r));added.append(r['itemId'])
 originals=[next(r for r in p['provenanceRecords'] if r['itemId']=='prov-ex-u24-word-order-contrast-'+n+'-'+lang) for n in ['0','1'] for lang in ['ru','en']]
 for old in originals:
  r=deepcopy(old);r.update(itemId='prov-u24-context-transfer-native-reference-'+old['itemId'],objectiveId=REF);r['originalSourceText']=r.get('originalSourceText') or p['sourceTextPool'][r.pop('originalSourceTextRef')];p['provenanceRecords'].append(r);native.append(deepcopy(r));added.append(r['itemId']);rebindings.append(dict(originalId=old['itemId'],newId=r['itemId'],sourceHash=r['sourceHash'],shippedText=r['shippedText']))
 old=next(i for i in p['items'] if i['id']==OLD);before=deepcopy(old);old.update(kind='SOURCE_MEANING',objectiveId=REF);old.pop('assessment');changes.append(dict(before=before,after=deepcopy(old)))
 for field in ['prompt','feedback']:record(old[field],'reference-'+field,obj=REF)
 for n,value in enumerate(old['distractorRationales']):
  if value:record(value,'reference-rationale-'+str(n),obj=REF)
 item=deepcopy(before);answer='It was me who went to the shop.';other="To the shop—that's where I went.";cue='В магазин пошёл я.'
 feedback=cue+' has the supplied subject-focus interpretation '+answer+' The context highlights the speaker as the person who went. This is an introductory supplied example; it does not establish every word order, a unique interpretation without context or independent subject-focus mastery.'
 rationale=other+' is the source’s destination-focus interpretation for В магазин я пошёл. in its supplied response context, rather than the highlighted-person reading requested here.'
 a=dict(cue=cue,answer=answer,distractors=[other]);rid='ca-rule-context:'+NEW;original=basis+'\nComplete raw context:\n'+span+'\nContext explanation:\n'+feedback+'\nTuple-v1:'+canonical(a);ids=[record(v,NEW+'-'+str(n),original=original,apps=[a],rid=rid) for n,v in enumerate([cue,answer,other])]
 item.update(id=NEW,sourceId=template['sourceId'],prompt='The supplied context emphasizes who went to the shop. Which English interpretation fits the sentence?',promptLetter=cue,answer=answer,options=[answer,other],feedback=feedback,distractorRationales=['',rationale]);item['assessment'].update(testedFeature='WORD_ORDER_SUBJECT_FOCUS_GUIDED',independent=False,contrastSetId=NEW,cueLanguage='ru',responseKind='ENGLISH_CLASSIFICATION',evidenceMode='STATED_APPLICATION',relationKind='STATED_APPLICATION',relationParticipantIds=ids,answerSourceIds=[ids[1]],distractorSourceIds=['',ids[2]],distractorDifference=['',rationale],contrastMembers=item['options'],whyAnswerDemonstratesObjective=feedback);p['items'].append(item)
 for field in ['prompt','feedback']:record(item[field],'guided-'+field)
 record(rationale,'guided-rationale')
 unit=p['units'][23];obj=next(o for o in unit['objectives'] if o['id']==OBJ);before=deepcopy(obj);obj['title']='Interpret the supplied readings in stated contexts';obj['instructionStepIds'].append('u24-st-teach-word-order-contrast-3');changes.append(dict(before=before,after=deepcopy(obj)));record(obj['title'],'objective-title')
 ref=dict(id=REF,unitNumber=24,title='Optional persuasion reference',kind='GUIDANCE',gating=False,instructionStepIds=['u24-st-teach-word-order-contrast-1'],exampleIds=['ex-u24-word-order-contrast-0','ex-u24-word-order-contrast-1'],practiceStepIds=[],checkItemIds=[],remediationStepIds=[]);unit['objectives'].append(ref);record(ref['title'],'reference-objective-title',obj=REF)
 session=next(s for s in unit['sessions'] if s['id']=='u24-s-word-order-contrast');before=deepcopy(session);session['title']=obj['title'];record(session['title'],'session-title')
 originalTeach=next(s for s in session['steps'] if s['id']=='u24-st-teach-word-order-contrast-1');record(originalTeach['body'],'reference-teaching-body',obj=REF);record(originalTeach['title'],'reference-teaching-title',obj=REF)
 title='Use context to identify the supplied emphasis';body=cue+' — '+answer+' In this supplied example the person who went is highlighted. Compare В магазин я пошёл. — '+other+' in the source’s destination-response context. Guided practice uses the highlighted-person example, with a different complete sentence and requested information from either independent persuasion check. The independent requirements still test both original persuasion contexts:the speaker will do it anyway, or the speaker would never agree. Subject-focus remains introductory guided evidence, not an independently demonstrated skill. Intonation and discourse context can affect readings; these examples do not establish a universal word-order switch or permission to infer present time from a past verb. The original repeated persuasion question remains optional reference practice and cannot credit or prevent the required objective.'
 session['steps'].append(dict(id='u24-st-teach-word-order-contrast-3',kind='TEACH',title=title,body=body,referenceIds=['ref-u2125-attribution']));record(title,'teach-3-title');record(body,'teach-3-body');teaching=[s for s in session['steps'] if s['kind']=='TEACH'];session['steps']=teaching+[s for s in session['steps'] if s['kind']!='TEACH']
 for step in session['steps']:
  if step['kind']=='GUIDED':step['itemIds']=[NEW]
  if step['kind']=='REMEDIATE':step['body']='\n\n'.join(s['body'] for s in teaching);step['exampleIds']=list(dict.fromkeys(e for s in teaching for e in s.get('exampleIds',[])));record(step['body'],'remediation-body')
 changes.append(dict(before=before,after=deepcopy(session)))
 q=lambda s:json.dumps(s,ensure_ascii=False);rows=['        Native('+', '.join(q(r[k]) for k in ['itemId','objectiveId','sourceRecordId','sourceHash','shippedText'])+'),' for r in native];core_rows=['        Native('+', '.join(q(r[k] if k!='originalSourceText' else '') for k in ['itemId','objectiveId','sourceRecordId','sourceHash','shippedText'])+'),' for r in originals]
 code='''package dev.russiancompanion.v2.curriculum
import java.security.MessageDigest
/** Distinct introductory context; two original independent persuasion claims preserved. */
internal object Unit24ContextPins {
    private data class Native(val id:String,val obj:String,val record:String,val hash:String,val text:String)
    private val native=listOf(
ROWS
    )
    private val originalPersuasion=listOf(
CORE_ROWS
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
'''.replace('CORE_ROWS','\n'.join(core_rows)).replace('ROWS','\n'.join(rows));PIN_FILE.write_text(code);after=(json.dumps(p,ensure_ascii=False,separators=(',',':'))+'\n').encode();PACK.write_bytes(after)
 (HERE/'UNIT24_CONTEXT_TRANSFER_CORRECTION.json').write_text(json.dumps(dict(baselineCommit=BASELINE,beforeSha256=hashlib.sha256(raw).hexdigest(),afterSha256=hashlib.sha256(after).hexdigest(),changes=changes,addedItems=[item],addedObjectives=[ref],addedProvenanceIds=added,nativeSelections=native,nativeRebindings=rebindings,contextIdentities={rid:dict(sourceHash=sha(original),applications=[a],objectiveId=OBJ)},bibliography=basis,nativePinCodeSha256=sha(code),frozenPublicSources={str(f.relative_to(ROOT)):sha(source)},semanticAcceptance=False,productiveWordOrderClaim=False,independentSubjectFocusClaim=False),ensure_ascii=False,indent=2)+'\n');print('Added',len(added),'pending records,',len(native),'native selections/rebindings;original independent items unchanged')
if __name__=='__main__':main()
