#!/usr/bin/env python3
"""Bounded distinct question recognition; preserve earlier native evidence and optional glosses."""
import hashlib,json,re
from copy import deepcopy
from pathlib import Path
HERE=Path(__file__).resolve().parent;ROOT=HERE.parents[1]
PACK=ROOT/'app/src/main/assets/content/v2-curriculum.json'
PIN_FILE=ROOT/'app/src/main/java/dev/russiancompanion/v2/curriculum/Unit17QuestionPins.kt'
BASELINE='151cae10b653be9038423e0d19afca2cf9a48b18';OBJ='obj-u17-clock';REF='obj-u17-clock-vocabulary'
def sha(s):return hashlib.sha256(s.encode()).hexdigest()
def visible(s):return re.sub(r'\[\[([^\[\]]+)\]\]',lambda m:m[1].split('|',1)[-1],s).replace("'''",'').replace("''",'')
def canonical(a):
 def f(s):return str(len(s.encode()))+':'+s
 return 'tuple-v1:1;'+f(a['cue'])+f(a['answer'])+str(len(a['distractors']))+';'+''.join(f(x) for x in a['distractors'])
def main():
 before=PACK.read_bytes();p=json.loads(before)
 if any(i['id']=='u17-g-question-apple' for i in p['items']):print('Already repaired; evidence unchanged');return
 source=ROOT/'catalog/sources/units-3-5/wikibooks/interrogative-4220316.txt';raw=source.read_text();span=raw[raw.index('=== Который? ==='):raw.index('It can also be used as an adjective meaning')].rstrip()
 template=next(r for r in p['provenanceRecords'] if r['itemId']=='prov-ca-u5-behavior-teach-1-body');primary=next(r for r in p['provenanceRecords'] if r['itemId']=='prov-ex-u17-clock-0-ru')
 bibliography=('Complete retained Wikibooks Russian/Interrogative Pronouns4220316, particularly Который section introduction and three complete supplied English/Russian question pairs; actual retained Wikibooks copyrights4622060 and CC BY-SA4.0 contributor-history attribution. '
  'Mechanical existing emphasis/link-label unfolding only; original words, case endings, spelling and question marks unchanged. No generated Russian, stress insertion, private Penguin prose, media or new source/license declaration. '
  'The article has inaccurate introductory language about pronouns/adverbs and conjugation; those claims and unrelated relative-pronoun/mixed example translations are not adopted. '
  'Scope explicitly distinguishes these supplied current-clock-time and object-selection questions. Guided apple ownership differs in whole native cue and requested information from the independent current-time and listener-book-choice cues. This is bounded interpretation, not a second native clock-question variant, productive asking/giving time, an algorithm for every interrogative or unrestricted transfer mastery. The separate clock-answer/clock-construction objectives remain unchanged. '
  'The three original repeated correct clock glosses remain optional SOURCE_MEANING practice on non-gating GUIDANCE with teaching links and native provenance, not grammar credit. Independently authored English on2026-10-13 within content-audit-2026-10-08; source/rights/linguistic review pending, no qualified acceptance token.')
 added=[];native=[];items=[];examples=[];changes=[];pins={};rebindings=[]
 def record(value,name,original=None,apps=None,rid=None,obj=OBJ):
  original=original or bibliography+'\n'+value;r=deepcopy(template);r.update(itemId='prov-ca-u17-question-'+name,unitNumber=17,objectiveId=obj,recordType='label',sourceRecordId=rid or 'ca-coverage:'+obj+':question:'+name,originalSourceText=original,shippedText=value,sourceHash=sha(original),contentHash=sha(value));r['synthesis'].update(bibliography=bibliography,authorshipDeclaration='Independently authored English2026-10-13 from complete frozen supplied question pairs; no native generation/private prose.',objectiveOrClaimBinding=obj+':question:'+name,applications=apps or []);p['provenanceRecords'].append(r);added.append(r['itemId']);return r['itemId']
 contexts=[('apple','u17-g-question-apple','Которое яблоко моё?','Which apple is mine?','Asks which apple belongs to the speaker.',False,'QUESTION_OBJECT_SELECTION_GUIDED'),('clock','u17-i-question-clock','Который час?','What time is it?','Asks for the current clock time.',True,'QUESTION_CLOCK_RECOGNITION'),('book','u17-i-question-book','Которую книгу вы хотите?','Which book do you want?','Asks which book the listener wants.',True,'QUESTION_BOOK_SELECTION')]
 opts=[x[4] for x in contexts];it=next(i for i in p['items'] if i['id']=='u17-i-clock-1');ex=next(e for e in p['examples'] if e['id']=='ex-u17-clock-0')
 for name,id,cue,gloss,answer,independent,feature in contexts:
  assert cue in visible(span) and gloss in span
  for suffix,text in [('ru',cue),('en',gloss)]+([('word','который')] if name=='clock' else []):
   r=deepcopy(primary);r.pop('originalSourceTextRef',None);r.update(itemId='prov-u17-question-native-'+name+'-'+suffix,recordType='example' if suffix=='ru' else 'meaning',sourceRecordId='interrogative-question-transfer:'+span[:77],originalSourceText=span,shippedText=text,sourceHash=sha(span),contentHash=sha(text));p['provenanceRecords'].append(r);native.append(deepcopy(r));added.append(r['itemId'])
  if name!='clock':
   e=deepcopy(ex);e.update(id='ex-u17-question-'+name,display=cue,note=gloss);p['examples'].append(e);examples.append(deepcopy(e))
  feedback=cue+' — '+gloss+' '+answer+' Choose '+answer+' Read the complete question and its noun, rather than treating every form of который as a clock question. This is the supplied interpretation, not independently forming a new question.'
  reasons=[v+' describes a different requested piece of information; the supplied whole question '+cue+' means '+gloss for v in opts];rationales=['' if v==answer else reasons[n] for n,v in enumerate(opts)]
  a={'cue':cue,'answer':answer,'distractors':[v for v in opts if v!=answer]};rid='ca-rule-context:'+id;original=bibliography+'\nComplete raw selected context:\n'+span+'\nContext explanation:\n'+feedback+'\nTuple-v1:'+canonical(a);ids=[record(v,id+'-'+str(n),original,[a],rid) for n,v in enumerate([cue,answer]+a['distractors'])]
  i=deepcopy(it);i.update(id=id,sourceId=template['sourceId'],prompt='Which English interpretation fits this complete supplied question?',promptLetter=cue,options=opts,answer=answer,feedback=feedback,distractorRationales=rationales)
  i['assessment'].update(testedFeature=feature,independent=independent,contrastSetId=id,cueLanguage='ru',responseKind='ENGLISH_CLASSIFICATION',evidenceMode='STATED_APPLICATION',relationKind='STATED_APPLICATION',relationParticipantIds=ids,answerSourceIds=[ids[1]],distractorSourceIds=['' if v==answer else ids[2+a['distractors'].index(v)] for v in opts],distractorDifference=rationales,contrastMembers=opts,whyAnswerDemonstratesObjective=feedback)
  p['items'].append(i);items.append(deepcopy(i));pins[rid]={'sourceHash':sha(original),'applications':[a],'objectiveId':OBJ}
  for field in ['prompt','feedback']:record(i[field],id+'-'+field)
  for n,v in enumerate(rationales):
   if v:record(v,id+'-rationale-'+str(n))
 old_ids=['u17-g-clock-0','u17-i-clock-1','u17-i-clock-2'];hosts=[]
 for id in old_ids:
  old=next(i for i in p['items'] if i['id']==id);before_item=deepcopy(old);hosts+=old['assessment']['relationParticipantIds'];old.update(kind='SOURCE_MEANING',objectiveId=REF);old.pop('assessment');changes.append({'before':before_item,'after':deepcopy(old)})
 for id in dict.fromkeys(hosts):
  r=deepcopy(next(r for r in p['provenanceRecords'] if r['itemId']==id));r.update(itemId='prov-u17-question-vocabulary-'+id,objectiveId=REF);p['provenanceRecords'].append(r);added.append(r['itemId']);rebindings.append(r['itemId'])
 unit=p['units'][16];obj=next(o for o in unit['objectives'] if o['id']==OBJ);before_obj=deepcopy(obj);obj['title']='Distinguish the supplied clock-time and object-selection questions';obj['checkItemIds']=[i['id'] for i in items if i['assessment']['independent']];obj['exampleIds'] += [e['id'] for e in examples];changes.append({'before':before_obj,'after':deepcopy(obj)});record(obj['title'],OBJ+'-title')
 reference={'id':REF,'unitNumber':17,'title':'Optional clock-question vocabulary','kind':'GUIDANCE','gating':False,'instructionStepIds':['u17-st-teach-clock-1','u17-st-teach-clock-2'],'exampleIds':['ex-u17-clock-0','ex-u17-clock-1'],'practiceStepIds':[],'checkItemIds':[],'remediationStepIds':[]};unit['objectives'].append(reference);record(reference['title'],REF+'-title',obj=REF)
 session=next(s for s in unit['sessions'] if s['id']=='u17-s-clock');before_session=deepcopy(session);session['title']=obj['title'];record(session['title'],session['id']+'-title')
 step=next(s for s in session['steps'] if s['id']=='u17-st-teach-clock-2');step['title']='Read the full question and the requested information';step['body']='Forms of который do not by themselves identify a clock question. Read the complete supplied question and its noun. '+contexts[0][2]+' — '+contexts[0][3]+' '+opts[0]+' This is the guided ownership/selection example. '+contexts[2][2]+' — '+contexts[2][3]+' '+opts[2]+' The listener is asked to choose a book. '+contexts[1][2]+' — '+contexts[1][3]+' '+opts[1]+' This requests the current clock time, not which book the listener wants or which apple belongs to the speaker. The independent clock and book cues are different complete questions from the guided apple cue. These checks recognize the supplied meanings; they do not demonstrate independently asking/giving a new time or forming every interrogative. Optional repeated clock gloss practice is separate from the required checks.';step['exampleIds']=['ex-u17-clock-0']+[e['id'] for e in examples];record(step['title'],step['id']+'-title');record(step['body'],step['id']+'-body')
 for s in session['steps']:
  if s['kind']=='GUIDED':s['itemIds']=['u17-g-question-apple']
  elif s['kind']=='CHECK':s['itemIds']=obj['checkItemIds']
  elif s['kind']=='REMEDIATE':
   teach=[x for x in session['steps'] if x['kind']=='TEACH'];s['body']='\n\n'.join(x['body'] for x in teach);s['exampleIds']=list(dict.fromkeys(e for x in teach for e in x.get('exampleIds',[])));record(s['body'],s['id']+'-body')
 for s in session['steps']:
  if s['kind']=='TEACH':record(s['body'],'vocabulary-'+s['id']+'-body',obj=REF)
 changes.append({'before':before_session,'after':deepcopy(session)})
 q=lambda x:json.dumps(x,ensure_ascii=False)
 rows=['        Native('+', '.join(q(r[k]) for k in ['itemId','sourceUrl','sourceRecordId','sourceHash','shippedText'])+'),' for r in native]
 code='''package dev.russiancompanion.v2.curriculum
import java.security.MessageDigest
/** Exact supplied question meanings and distinct cues; review pending, no productive-time claim. */
internal object Unit17QuestionPins {
    val checkIds=listOf("u17-i-question-clock","u17-i-question-book")
    private data class Native(val id:String,val url:String,val record:String,val hash:String,val text:String)
    private val native=listOf(
ROWS
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
    private val FEATURES=mapOf(FEATURES_DATA)
}
'''.replace('ROWS','\n'.join(rows)).replace('FEATURES_DATA',', '.join(q(i['id'])+' to '+q(i['assessment']['testedFeature']) for i in items))
 PIN_FILE.write_text(code);after=(json.dumps(p,ensure_ascii=False,separators=(',',':'))+'\n').encode();PACK.write_bytes(after)
 (HERE/'UNIT17_QUESTION_TRANSFER_CORRECTION.json').write_text(json.dumps(dict(baselineCommit=BASELINE,beforeSha256=hashlib.sha256(before).hexdigest(),afterSha256=hashlib.sha256(after).hexdigest(),changes=changes,addedObjectives=[reference],addedItems=items,addedExamples=examples,addedProvenanceIds=added,nativeSelections=native,optionalNativeRebindingIds=rebindings,contextIdentities=pins,bibliography=bibliography,nativePinCodeSha256=sha(code),frozenPublicSources={str(source.relative_to(ROOT)):sha(raw),'catalog/sources/units-3-5/wikibooks/copyrights-4622060.txt':sha((ROOT/'catalog/sources/units-3-5/wikibooks/copyrights-4622060.txt').read_text())},nativeTransformation='Mechanical existing wiki-label/emphasis unfolding only; words/case endings/spelling/question marks unchanged.',semanticAcceptance=False,productiveClockQuestionClaim=False,allQuestionTransferClaim=False),ensure_ascii=False,indent=2)+'\n')
 print('Added',len(items),'applications,',len(examples),'examples,',len(added),'pending records;',len(native),'native selections;',len(rebindings),'optional rebindings')
if __name__=='__main__':main()
