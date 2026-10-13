#!/usr/bin/env python3
"""Replace category/connective credit with bounded frozen native punctuation contexts."""
import hashlib,json,re
from copy import deepcopy
from pathlib import Path
HERE=Path(__file__).resolve().parent
ROOT=HERE.parents[1]
PACK=ROOT/'app/src/main/assets/content/v2-curriculum.json'
PIN_FILE=ROOT/'app/src/main/java/dev/russiancompanion/v2/curriculum/Unit29PunctuationPins.kt'
BASELINE='e4085c5686e1c58a0af43e1c1f66fbb573f56887'
OBJ='obj-u29-punctuation';REF='obj-u29-punctuation-reference'
TWO='Separating commas mark both boundaries of the participial phrase before the sentence continues.'
AFTER='A separating comma follows the opening participial phrase before the main clause.'
BEFORE='A separating comma precedes the terminal participial phrase, with no closing comma at sentence end.'
NONE='No separating comma is used between the participial phrase and the word it describes.'
def sha(s):return hashlib.sha256(s.encode()).hexdigest()
def visible(s):
 s=re.sub(r'\[\[([^\[\]]+)\]\]',lambda m:m[1].split('|',1)[-1],s)
 return s.replace("'''",'').replace("''",'')
def canonical(a):
 def f(s):return str(len(s.encode()))+':'+s
 return 'tuple-v1:1;'+f(a['cue'])+f(a['answer'])+str(len(a['distractors']))+';'+''.join(f(x) for x in a['distractors'])
def main():
 raw=PACK.read_bytes();p=json.loads(raw)
 if any(i['id']=='u29-i-punctuation-pronoun' for i in p['items']):print('Already repaired; evidence unchanged');return
 candidate=ROOT/'audit/independent-gpt61-2026-10-12/ia012-unit29-research-verification/public-candidates-2026-10-13'
 obos=(candidate/'ruwiki-144053197.wikitext.txt').read_text();bio=(candidate/'ruwiki-149826784.wikitext.txt').read_text()
 assert sha(obos)=='0f5a33ffba100e7a6d5bf2c80987ce72f69e970f004b09e815ebf73ebe3ed638'
 assert sha(bio)=='e2ab8bd93324e7c77f86336c0780ce46314fa9e6fdbee0ba13fd148f143943c1'
 templates=next(r for r in p['provenanceRecords'] if r['itemId']=='prov-ca-u5-behavior-teach-1-body')
 primary=next(r for r in p['provenanceRecords'] if r['itemId']=='prov-ex-u29-punctuation-0-ru')
 bibliography=('Retained Russian Wikipedia Причастие (лингвистика)153078996 complete participial-phrase rule/default examples and copyright154562152. Newly selected Russian Wikipedia Обособление144053197 specific after-noun, cause-labelled and terminal examples; Больтраффио149826784 complete opening participial construction modifying он. '
  'Exact raw revisions and API SHA1/size/SHA256 metadata are retained under audit/independent-gpt61-2026-10-12/ia012-unit29-research-verification/public-candidates-2026-10-13/. Native selection performs only existing mechanical wiki-link label/emphasis unfolding; words, punctuation, spelling, capitalization and sentence limits are unchanged. No added terminal period, generated Russian sentence or private Penguin prose. '
  'Wikipedia first-party selection/arrangement is CC BY-SA4.0 under the actual retained Russian Wikipedia copyrights154562152 route, with stable page histories/contributor attribution. Underlying cloud quotation is Korolenko, В дурном обществе1885 (author died1921); the cause example is the licensed Wikipedia instructional adaptation of Gorky Горемыка Павел1894 (author died1936), not a verbatim original-story quotation. Original public-domain basis/edition/author-date checks are retained separately; no claim that hosting licenses every third-party quotation. The obsolete source reference year5078 is not adopted. '
  'Only these bounded comma decisions are taught:ordinary preceding-noun default, both boundaries before a continuing clause, pronoun-headed or cause-labelled opening modifier, and terminal modifier without a closing comma. The internal name-list comma in the artist sentence is distinct from its modifier/main-clause boundary. No universal comma-insertion, all modifier types or unrestricted production claim. '
  'Independently authored English on2026-10-13 within content-audit-2026-10-08; qualified source/rights/linguistic acceptance pending. The old category/connective question stays optional SOURCE_QUESTION reference practice and cannot credit or prevent completion of the language objective.')
 added=[];native=[];examples=[];items=[];changes=[];pins={};new_sources=[];new_rights=[]
 for slug,title,rev in [('obosoblenie','Обособление','144053197'),('boltraffio','Больтраффио, Джованни','149826784')]:
  sid='src-ruwiki-'+slug+'-'+rev;attribution='Участники русской Википедии, CC BY-SA 4.0, via Википедия:Авторские права oldid 154562152. Page history for oldid '+rev+' identifies contributors. Mechanical link-label/emphasis cleanup only; original words and punctuation unchanged. Qualified source/rights review pending.'
  src={'id':sid,'title':title,'license':'CC BY-SA 4.0','attribution':attribution,'modification':'Specific native constructions selected with deterministic wiki-link label/emphasis unfolding. No native paraphrase, stress insertion or punctuation change. Original quotations/edition/author basis recorded separately; no whole-article grammar adoption.'};rights={'id':'rights-ruwiki-'+slug+'-'+rev,'sourceId':sid,'license':'CC BY-SA 4.0','permitsAppUse':True}
  assert all(s['id']!=sid for s in p['sources']);p['sources'].append(src);p['rights'].append(rights);new_sources.append(src);new_rights.append(rights)
 def record(value,name,original=None,apps=None,rid=None,obj=OBJ):
  original=original or bibliography+'\n'+value;r=deepcopy(templates);r.update(itemId='prov-ca-u29-punctuation-'+name,unitNumber=29,objectiveId=obj,recordType='label',sourceRecordId=rid or 'ca-coverage:'+obj+':punctuation:'+name,originalSourceText=original,shippedText=value,sourceHash=sha(original),contentHash=sha(value));r['synthesis'].update(bibliography=bibliography,authorshipDeclaration='Independently authored English on2026-10-13 from complete frozen native constructions and rule context; only mechanical native markup normalization, no invented Russian/private prose.',objectiveOrClaimBinding=obj+':punctuation:'+name,applications=apps or []);p['provenanceRecords'].append(r);added.append(r['itemId']);return r['itemId']
 cloud='Туча, нависшая над высокими вершинами тополей, уже сыпала дождиком'
 cause='Изумлённый этим хохотом, Павел сел на пол и смотрел куда-то в угол'
 terminal='Вдали виднеется маяк, стоящий у моря'
 pronoun='Воспитанный в традициях Фоппы, Бернардо Дзенале и Амброджо Бергоньоне, он прошёл обучение в мастерской Леонардо.'
 cloud_span=obos[obos.index('|1. Стоят после определяемого слова и выражены причастным оборотом'):obos.index('|-\n|2. Стоят после определяемого слова')].rstrip()
 cause_span=obos[obos.index('|3. Стоят перед определяемым словом и имеют добавочный оттенок'):obos.index('2. '+"'''''Всегда уверенный")].rstrip()
 terminal_span=next(l for l in obos.splitlines() if '* [[причастный оборот]]' in l)
 pronoun_span=bio[bio.index('Воспитанный в традициях'):bio.index(' Его первое произведение')]
 contexts=[('after','144053197',cloud,cloud_span,'Туча',TWO,[TWO,BEFORE,NONE],'PUNCTUATION_CONTINUING_BOUNDARIES','The participial phrase follows the cloud noun, and the main predicate continues after it; both boundaries are marked.'),
  ('pronoun','149826784',pronoun,pronoun_span,'он',AFTER,[AFTER,NONE,TWO],'PUNCTUATION_PERSONAL_PRONOUN','The opening participial phrase describes the personal pronoun он. Its separating comma is before that pronoun/main clause; the earlier comma separates names within the modifier.'),
  ('cause','144053197',cause,cause_span,'Павел',AFTER,[AFTER,NONE,BEFORE],'PUNCTUATION_CAUSE_EXCEPTION','The retained row explicitly assigns a causal shade to this opening modifier describing Pavel. It is separated from the main clause despite preceding the described noun.'),
  ('terminal','144053197',terminal,terminal_span,'маяк',BEFORE,[BEFORE,TWO,NONE],'PUNCTUATION_TERMINAL_BOUNDARY','The following participial phrase describes the lighthouse and ends the supplied construction; its preceding boundary takes a comma, with no closing separating comma at sentence end.')]
 ext=next(e for e in p['examples'] if e['id']=='ex-u29-punctuation-0');it=next(i for i in p['items'] if i['id']=='u29-i-punctuation-1')
 for name,rev,cue,span,head,answer,opts,feature,meaning in contexts:
  sid='src-ruwiki-'+('boltraffio' if rev=='149826784' else 'obosoblenie')+'-'+rev;src=next(s for s in new_sources if s['id']==sid)
  assert cue in visible(span),(name,visible(span));assert head in visible(span)
  parts=[('ru',cue),('head',head)]
  regions=re.findall(r'[\u0400-\u04FF\u0301](?:[\u0400-\u04FF\u0301]|[ \t]+(?=[\u0400-\u04FF]))*[?!.,]*',cue)
  parts += [('region-'+str(n),text) for n,text in enumerate(regions) if text.rstrip('?!.,') not in [cue.rstrip('?!.,'),head]]
  if name=='pronoun':parts += [('list-foppa','Фоппы'),('boundary-bergognone','Бергоньоне')]
  for suffix,text in parts:
   r=deepcopy(primary);r.pop('originalSourceTextRef',None);r.update(itemId='prov-u29-punctuation-native-'+name+'-'+suffix,recordType='example' if suffix=='ru' else 'meaning',sourceId=sid,sourceUrl='https://ru.wikipedia.org/w/index.php?title='+('Больтраффио,_Джованни' if rev=='149826784' else 'Обособление')+'&oldid='+rev,sourceRevision=rev,sourceRecordId='punctuation-native:'+name+':'+span[:77],sourceHash=sha(span),originalSourceText=span,shippedText=text,contentHash=sha(text),attribution=src['attribution']);p['provenanceRecords'].append(r);added.append(r['itemId']);native.append(deepcopy(r))
  e=deepcopy(ext);e.update(id='ex-u29-punctuation-context-'+name,display=cue,note=meaning,sourceId=sid);p['examples'].append(e);examples.append(deepcopy(e));record(meaning,e['id']+'-note')
  id='u29-i-punctuation-'+name;feedback='In '+cue+' '+meaning+' The answer is '+answer+' This is the supplied construction, not a universal rule for every modifier.'
  reasons={TWO:'Two separating boundaries are appropriate only when the parent clause continues after a medial modifier; an opening or terminal modifier does not have that same layout.',AFTER:'An opening modifier is followed by its separating boundary; that is different from a following terminal or medial modifier.',BEFORE:'One preceding separating comma without a closing comma applies to a terminal following modifier; it would omit the other boundary in a continuing clause.',NONE:'The ordinary preceding-noun default has no separating comma, but that default does not remove required commas in the supplied following, pronoun-headed or cause-labelled construction.'}
  rationales=['' if v==answer else v+' '+reasons[v] for v in opts];i=deepcopy(it);i.update(id=id,sourceId=templates['sourceId'],prompt='Which English description of separating-comma treatment fits the participial phrase in this complete supplied construction? Ignore commas inside lists of names.',promptLetter=cue,options=opts,answer=answer,feedback=feedback,distractorRationales=rationales)
  a={'cue':cue,'answer':answer,'distractors':[v for v in opts if v!=answer]};rid='ca-rule-context:'+id;original=bibliography+'\nContext explanation:\n'+feedback+'\nTuple-v1:'+canonical(a);ids=[record(v,id+'-'+str(n),original,[a],rid) for n,v in enumerate([cue,answer]+a['distractors'])]
  i['assessment'].update(testedFeature=feature,independent=True,contrastSetId=id,cueLanguage='ru',responseKind='ENGLISH_CLASSIFICATION',evidenceMode='STATED_APPLICATION',relationKind='STATED_APPLICATION',relationParticipantIds=ids,answerSourceIds=[ids[1]],distractorSourceIds=['' if v==answer else ids[2+a['distractors'].index(v)] for v in opts],distractorDifference=rationales,contrastMembers=opts,whyAnswerDemonstratesObjective=feedback);p['items'].append(i);items.append(deepcopy(i));pins[rid]={'sourceHash':sha(original),'applications':[a],'objectiveId':OBJ}
  for field in ['prompt','feedback']:record(i[field],id+'-'+field)
  for n,v in enumerate(rationales):
   if v:record(v,id+'-rationale-'+str(n))
 old=next(i for i in p['items'] if i['id']=='u29-i-punctuation-2');before=deepcopy(old);ids=old['assessment']['relationParticipantIds'];old.update(kind='SOURCE_QUESTION',objectiveId=REF,prompt='Optional reference wording: '+old['prompt'],feedback='Optional source wording only; this category/connective answer is not a comma decision. '+old['feedback']);old.pop('assessment');changes.append({'before':before,'after':deepcopy(old)});record(old['prompt'],old['id']+'-prompt',obj=REF);record(old['feedback'],old['id']+'-feedback',obj=REF)
 for id in ids:
  r=deepcopy(next(r for r in p['provenanceRecords'] if r['itemId']==id));r.update(itemId='prov-u29-punctuation-reference-'+id,objectiveId=REF);p['provenanceRecords'].append(r);added.append(r['itemId'])
 e=next(e for e in p['examples'] if e['id']=='ex-u29-punctuation-2');before=deepcopy(e);e['objectiveId']=REF;changes.append({'before':before,'after':deepcopy(e)})
 unit=p['units'][28];obj=next(o for o in unit['objectives'] if o['id']==OBJ);before=deepcopy(obj);obj['title']='Decide comma boundaries in the supplied participial constructions';obj['checkItemIds']=['u29-i-punctuation-1']+[i['id'] for i in items];obj['exampleIds']=[x for x in obj['exampleIds'] if x!='ex-u29-punctuation-2']+[e['id'] for e in examples];obj['instructionStepIds'].append('u29-st-teach-punctuation-4');changes.append({'before':before,'after':deepcopy(obj)});record(obj['title'],OBJ+'-title')
 reference={'id':REF,'unitNumber':29,'title':'Optional source wording reference','kind':'GUIDANCE','gating':False,'instructionStepIds':['u29-st-teach-punctuation-3'],'exampleIds':['ex-u29-punctuation-2'],'practiceStepIds':[],'checkItemIds':[],'remediationStepIds':[]};unit['objectives'].append(reference);record(reference['title'],REF+'-title',obj=REF)
 session=next(s for s in unit['sessions'] if s['id']=='u29-s-punctuation');before=deepcopy(session);session['title']=obj['title'];record(session['title'],session['id']+'-title')
 step3=next(s for s in session['steps'] if s['id']=='u29-st-teach-punctuation-3');step3['title']='Apply the pronoun and cause exceptions';step3['body']='The retained rule states exceptions when the described entity is a personal pronoun or the participial phrase expresses a cause. '+pronoun+' The opening phrase describes он, so '+AFTER+' The comma after Фоппы is internal to the list of names. The later comma after Бергоньоне marks the modifier boundary before он; do not treat every visible comma as a modifier boundary. '+cause+' The retained source row explicitly gives this example a causal shade, so '+AFTER+' The ordinary preceding-noun label запятые не ставятся cannot cancel these exceptions. The optional source wording личным местоимением and кроме тех случаев identifies a category/connective, not a complete comma decision; its reference question does not satisfy these language checks.';step3['exampleIds']=['ex-u29-punctuation-context-pronoun','ex-u29-punctuation-context-cause'];record(step3['title'],step3['id']+'-title');record(step3['body'],step3['id']+'-body');record(step3['body'],'reference-'+step3['id']+'-body',obj=REF)
 step4={'id':'u29-st-teach-punctuation-4','kind':'TEACH','title':'Check the continuing clause and sentence end','body':cloud+' The phrase describes Туча and is followed by the continuing predicate, so '+TWO+' '+terminal+' Here the phrase describes маяк and ends the supplied construction, so '+BEFORE+' A closing separating comma would have no continuing clause after it. By contrast, '+NONE+' is the ordinary non-causal, non-pronoun preceding-noun default shown in the earlier program example. These are bounded supplied constructions; not every phrase before a noun needs a comma and not every phrase after a word fits one universal rule.','referenceIds':['ref-u2630-attribution'],'exampleIds':['ex-u29-punctuation-context-after','ex-u29-punctuation-context-terminal']};idx=next(n for n,s in enumerate(session['steps']) if s['kind']=='GUIDED');session['steps'].insert(idx,step4);record(step4['title'],step4['id']+'-title');record(step4['body'],step4['id']+'-body')
 next(s for s in session['steps'] if s['kind']=='CHECK')['itemIds']=obj['checkItemIds'];rem=next(s for s in session['steps'] if s['kind']=='REMEDIATE');teach=[s for s in session['steps'] if s['kind']=='TEACH'];rem['body']='\n\n'.join(s['body'] for s in teach);rem['exampleIds']=list(dict.fromkeys(e for s in teach for e in s.get('exampleIds',[])));record(rem['body'],rem['id']+'-body');changes.append({'before':before,'after':deepcopy(session)})
 def q(s):return json.dumps(s,ensure_ascii=False)
 rows=['        Native('+', '.join(q(r[k]) for k in ['itemId','sourceId','sourceRevision','sourceUrl','sourceRecordId','sourceHash','shippedText'])+'),' for r in native]
 code='''package dev.russiancompanion.v2.curriculum
import java.security.MessageDigest
/** Exact raw native selections and bounded comma decisions; no qualified acceptance token. */
internal object Unit29PunctuationPins {
    val checkIds = listOf(CHECKS)
    private val newIds = listOf(NEW_IDS)
    private val newSources = setOf(SOURCES)
    private data class Native(val id: String,val source: String,val revision: String,val url: String,
        val record: String,val hash: String,val text: String)
    private val native = listOf(
ROWS
    )
    private fun sha(s: String) = MessageDigest.getInstance("SHA-256").digest(s.toByteArray(Charsets.UTF_8))
        .joinToString("") { "%02x".format(it.toInt() and 255) }
    fun ownsSource(id: String) = id in newSources
    fun admittedNative(r: V2ProvenanceRecord): Boolean {
        val pin = native.singleOrNull { it.id == r.itemId } ?: return false
        return r.unitNumber == 29 && r.objectiveId == "obj-u29-punctuation" && r.sourceId == pin.source &&
            r.sourceRevision == pin.revision && r.sourceUrl == pin.url && r.sourceRecordId == pin.record &&
            r.sourceHash == pin.hash && sha(r.originalSourceText) == pin.hash && r.shippedText == pin.text &&
            r.contentHash == sha(pin.text) && pin.text in V2Validator.unfoldWikiLabels(r.originalSourceText) &&
            r.transformation == "selection" && r.licenseId == "CC BY-SA 4.0"
    }
    fun matches(pack: V2CurriculumPack): Boolean {
        val obj = pack.unit(29)?.objectives?.singleOrNull { it.id == "obj-u29-punctuation" } ?: return false
        if (obj.checkItemIds != checkIds || obj.kind != "RECOGNITION" || !obj.gating) return false
        if (!native.all { pin -> pack.provenanceRecords.singleOrNull { it.itemId == pin.id }?.let(::admittedNative) == true }) return false
        if (!newSources.all { sid -> pack.sources.any { it.id == sid && it.license == "CC BY-SA 4.0" } &&
            pack.rights.any { it.sourceId == sid && it.license == "CC BY-SA 4.0" && it.permitsAppUse } }) return false
        val old = pack.item("u29-i-punctuation-1") ?: return false
        if (old.promptLetter != "Написанная в спешке программа выполнила недопустимую операцию." ||
            old.answer != "запятые не ставятся" || old.options != listOf("запятые не ставятся", "выделяется запятыми с двух сторон", "запятая ставится только перед причастным оборотом") ||
            old.assessment?.independent != true || V2Validator.structureValid(pack,old).isNotEmpty()) return false
        val practice = obj.practiceStepIds.flatMap { pack.step(it)?.second?.itemIds.orEmpty() }
        val guided = pack.item("u29-g-punctuation-0") ?: return false
        if (guided.id !in practice || guided.promptLetter != "Программа, написанная в спешке, выполнила недопустимую операцию." ||
            guided.answer != "выделяется запятыми с двух сторон" || guided.assessment?.independent != false ||
            V2Validator.structureValid(pack,guided).isNotEmpty()) return false
        return newIds.all { id ->
            val item = pack.item(id)
            item != null && item.objectiveId == obj.id && item.assessment?.independent == true &&
                item.assessment.testedFeature == FEATURES[id] && CompleteRuleContextPins.itemMatches(pack,item) &&
                V2Validator.structureValid(pack,item).isEmpty()
        }
    }
    private val FEATURES = mapOf(FEATURES_DATA)
}
'''.replace('CHECKS',', '.join(map(q,obj['checkItemIds']))).replace('NEW_IDS',', '.join(q(i['id']) for i in items)).replace('SOURCES',', '.join(q(s['id']) for s in new_sources)).replace('ROWS','\n'.join(rows)).replace('FEATURES_DATA',', '.join(q(i['id'])+' to '+q(i['assessment']['testedFeature']) for i in items))
 PIN_FILE.write_text(code);after=(json.dumps(p,ensure_ascii=False,separators=(',',':'))+'\n').encode();PACK.write_bytes(after)
 (HERE/'UNIT29_PUNCTUATION_CORRECTION.json').write_text(json.dumps(dict(baselineCommit=BASELINE,beforeSha256=hashlib.sha256(raw).hexdigest(),afterSha256=hashlib.sha256(after).hexdigest(),changes=changes,addedObjectives=[reference],addedSources=new_sources,addedRights=new_rights,addedItems=items,addedExamples=examples,addedProvenanceIds=added,nativeSelections=native,contextIdentities=pins,bibliography=bibliography,nativePinCodeSha256=sha(code),candidateSnapshotSha256={str(f.relative_to(ROOT)):hashlib.sha256(f.read_bytes()).hexdigest() for f in [candidate/'ruwiki-144053197.wikitext.txt',candidate/'ruwiki-149826784.wikitext.txt']},nativeTransformation='Deterministic existing wiki-label/emphasis unfolding only; no changed native words or punctuation, no added terminal period.',semanticAcceptance=False,unrestrictedPunctuationProductionClaim=False),ensure_ascii=False,indent=2)+'\n')
 print('Added',len(items),'independent comma decisions,',len(examples),'native examples,',len(added),'pending records;',len(native),'primary selections')
if __name__=='__main__':main()
