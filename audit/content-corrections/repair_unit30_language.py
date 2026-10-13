#!/usr/bin/env python3
"""Replace language-gating table recall with bounded native names and abbreviation treatment."""
import hashlib,json
from copy import deepcopy
from pathlib import Path
HERE=Path(__file__).resolve().parent
ROOT=HERE.parents[1]
PACK=ROOT/'app/src/main/assets/content/v2-curriculum.json'
PIN_FILE=ROOT/'app/src/main/java/dev/russiancompanion/v2/curriculum/Unit30LanguagePins.kt'
BASELINE='52274f15b17b7f1f46bf4684006437e9661dab31'
NAMES='obj-u30-letter-names';ABBR='obj-u30-abbrev-examples';REF='obj-u30-reference-literacy'
MASC='USSR; masculine agreement; the initialism remains unchanged for case.'
PLURAL='USA; plural agreement; the initialism remains unchanged for case.'
WRONG_M='USSR; plural agreement; the initialism takes case endings.'
WRONG_P='USA; singular agreement; the initialism takes case endings.'
def sha(s):return hashlib.sha256(s.encode()).hexdigest()
def canonical(a):
 def f(s):return str(len(s.encode()))+':'+s
 return 'tuple-v1:1;'+f(a['cue'])+f(a['answer'])+str(len(a['distractors']))+';'+''.join(f(x) for x in a['distractors'])
def main():
 raw=PACK.read_bytes();p=json.loads(raw)
 if any(i['id']=='u30-i-native-name-b' for i in p['items']):print('Already repaired; evidence unchanged');return
 paths=['catalog/sources/units-26-30/wikipedia/russian-alphabet-1371795481.txt','catalog/sources/units-26-30/wikipedia/russian-grammar-1369617627.txt']
 alphabet=(ROOT/paths[0]).read_text();grammar=(ROOT/paths[1]).read_text()
 assert '"USSR", masculine' in grammar and '"USA", plural' in grammar
 template=next(r for r in p['provenanceRecords'] if r['itemId']=='prov-ca-u5-behavior-teach-1-body')
 alpha=next(r for r in p['provenanceRecords'] if r['itemId']=='prov-ex-u30-letter-names-0-ru')
 abbrev=next(r for r in p['provenanceRecords'] if r['itemId']=='prov-ex-u30-abbrev-examples-0-ru')
 bibliography=('Retained Wikipedia Russian alphabet oldid1371795481, '+paths[0]+': complete Letters table heading and А/Б/В rows, selecting the modern Russian Name field separately from obsolete Old Church Slavonic word-names, English respellings, approximate sound comparisons and example translations. '
  'Retained Wikipedia Russian grammar oldid1369617627, '+paths[1]+': complete Indeclinable nouns section, specifically the supplied USSR masculine and USA plural examples and unchanged case treatment. '
  'Both CC BY-SA4.0 through retained Wikipedia:Copyrights oldid1369466282 rights attestation; stable primary URLs and contributor attribution preserved. '
  'English contexts ask about naming a shown glyph or interpreting a supplied initialism as a noun in a sentence/adjective agreement context. No Russian sentence is invented. The supplied names and initialisms are literal native selections; naming by text does not demonstrate listening, pronunciation or the complete phonetic foundation. '
  'Six former literal reference questions remain optional SOURCE_QUESTION practice on a non-gating GUIDANCE objective. Their correct keys follow the literal prompts and are preserved; table/heading recall does not clear the language objectives. '
  'Independently authored English on 2026-10-13 within content-audit-2026-10-08; no copied explanatory primary prose or private Penguin text; qualified source/rights/semantic audit pending.')
 added=[];native=[];examples=[];items=[];changes=[];pins={}
 def record(value,obj,name,original=None,apps=None,rid=None):
  original=original or bibliography+'\n'+value;r=deepcopy(template)
  r.update(itemId='prov-ca-u30-language-'+name,unitNumber=30,objectiveId=obj,recordType='label',sourceRecordId=rid or 'ca-coverage:'+obj+':language:'+name,originalSourceText=original,shippedText=value,sourceHash=sha(original),contentHash=sha(value))
  r['synthesis'].update(bibliography=bibliography,authorshipDeclaration='Independently authored English on 2026-10-13 from retained native name fields and indeclinable initialism context; no invented Russian or private book prose.',objectiveOrClaimBinding=obj+':language:'+name,applications=apps or [])
  p['provenanceRecords'].append(r);added.append(r['itemId']);return r['itemId']
 def select(t,text,obj,name,span=None):
  r=deepcopy(t);r.update(itemId='prov-u30-language-native-'+name,unitNumber=30,objectiveId=obj,shippedText=text,contentHash=sha(text),recordType='meaning' if name.endswith(('-name','-en','-flag')) else 'example')
  if span is not None:r.pop('originalSourceTextRef',None);r.update(originalSourceText=span,sourceHash=sha(span),sourceRecordId=('alphabet-native:' if t is alpha else 'grammar-native:')+span[:77])
  original=r.get('originalSourceText') or p['sourceTextPool'][r['originalSourceTextRef']];assert text in original,(name,text)
  p['provenanceRecords'].append(r);added.append(r['itemId']);native.append(deepcopy(r))
 for glyph,name,suffix in [('А','а','a'),('Б','бэ','b'),('В','вэ','v')]:
  start=alphabet.index('! style="text-align: center;" | <big>{{lang|ru|[['+glyph+']]');end=alphabet.index('\n|-',start);span=alphabet[start:end]
  assert '{{lang|ru|'+name+'}}<br/>' in span
  select(alpha,glyph,NAMES,suffix+'-glyph',span);select(alpha,name,NAMES,suffix+'-name',span)
  e=deepcopy(next(e for e in p['examples'] if e['id']=='ex-u30-letter-names-0'));e.update(id='ex-u30-native-name-'+suffix,display=glyph,note=name);p['examples'].append(e);examples.append(deepcopy(e))
 for id,text in [('ussr-ru','СССР'),('usa-ru','США'),('ussr-en','USSR'),('usa-en','USA'),('masculine-flag','masculine'),('plural-flag','plural')]:select(abbrev,text,ABBR,id)
 indeclinable=grammar[grammar.index('=== Indeclinable nouns ==='):grammar.index('=== Additional cases ===')].strip()
 select(abbrev,'not modified when they change number and case',ABBR,'indeclinable-flag',indeclinable)
 olditems=[i for i in p['items'] if i['objectiveId'] in [NAMES,ABBR]];assert len(olditems)==6
 itemtemplate=next(i for i in olditems if i['id']=='u30-i-letter-names-1')
 def application(id,obj,cue,prompt,opts,answer,feedback,rationales,feature,independent,response):
  i=deepcopy(itemtemplate);i.update(id=id,objectiveId=obj,sourceId=template['sourceId'],prompt=prompt,promptLetter=cue,options=opts,answer=answer,feedback=feedback,distractorRationales=rationales)
  a={'cue':cue,'answer':answer,'distractors':[v for v in opts if v!=answer]};rid='ca-rule-context:'+id;span=bibliography+'\nContext explanation:\n'+feedback+'\nTuple-v1:'+canonical(a)
  ids=[record(v,obj,id+'-'+str(n),span,[a],rid) for n,v in enumerate([cue,answer]+a['distractors'])]
  i['assessment'].update(testedFeature=feature,independent=independent,contrastSetId=id,cueLanguage='ru',responseKind=response,evidenceMode='STATED_APPLICATION',relationKind='STATED_APPLICATION',relationParticipantIds=ids,answerSourceIds=[ids[1]],distractorSourceIds=['' if v==answer else ids[2+a['distractors'].index(v)] for v in opts],distractorDifference=rationales,contrastMembers=opts,whyAnswerDemonstratesObjective=feedback)
  p['items'].append(i);items.append(deepcopy(i));pins[rid]={'sourceHash':sha(span),'applications':[a],'objectiveId':obj}
  for field in ['prompt','feedback']:record(i[field],obj,id+'-'+field)
  for n,v in enumerate(rationales):
   if v:record(v,obj,id+'-rationale-'+str(n))
 mapping={'А':'а','Б':'бэ','В':'вэ'}
 for id,glyph,independent in [('u30-g-native-name-a','А',False),('u30-i-native-name-b','Б',True),('u30-i-native-name-v','В',True)]:
  answer=mapping[glyph];opts=[answer]+[v for v in ['а','бэ','вэ'] if v!=answer]
  feedback=f'The modern Russian name of {glyph} is {answer}. Choose the Russian name of the shown letter, not an English comparison word or an obsolete word-name. This is textual name recognition and does not demonstrate listening or pronunciation.'
  rationales=['']+[f'{v} is the modern name of {next(k for k,x in mapping.items() if x==v)}, not the shown letter.' for v in opts[1:]]
  application(id,NAMES,glyph,'Which Russian name belongs to the shown letter?',opts,answer,feedback,rationales,'LETTER_NAME',independent,'RUSSIAN_TOKEN')
 for id,cue,answer,independent in [('u30-g-initialism-use','СССР',MASC,False),('u30-i-initialism-ussr','СССР',MASC,True),('u30-i-initialism-usa','США',PLURAL,True)]:
  if not independent:
   prompt='In a historical reference to the Soviet state, which English meaning and noun treatment fit the shown initialism?';opts=[MASC,PLURAL]
  elif cue=='СССР':
   prompt='A Russian adjective agrees with this noun initialism referring to the historical Soviet state. Which English explanation fits its meaning and treatment?';opts=[MASC,WRONG_M,WRONG_P]
  else:
   prompt='A Russian adjective agrees with this noun initialism referring to the United States. Which English explanation fits its meaning and treatment?';opts=[PLURAL,WRONG_P,WRONG_M]
  feedback=('СССР means USSR and has the supplied masculine agreement treatment. ' if cue=='СССР' else 'США means USA and has the supplied plural agreement treatment. ')+'The initialism is indeclinable here: its written form remains unchanged for case, while an agreeing adjective follows its grammatical gender or number. Grammatical agreement and changing the initialism itself are separate facts. This is limited to these supplied examples, not every abbreviation. '+answer
  reasons={MASC:'The USSR masculine treatment belongs to the other initialism, not the supplied USA reference.',PLURAL:'The USA plural treatment belongs to the other initialism, not the supplied Soviet-state reference.',WRONG_M:'The USSR gloss does not justify plural agreement or adding case endings: the supplied USSR initialism is masculine and indeclinable.',WRONG_P:'The USA gloss does not justify singular agreement or adding case endings: the supplied USA initialism is plural and indeclinable.'}
  application(id,ABBR,cue,prompt,opts,answer,feedback,['' if v==answer else reasons[v] for v in opts],'ABBREVIATION_TREATMENT',independent,'ENGLISH_CLASSIFICATION')
 native_ids=set()
 for i in olditems:
  before=deepcopy(i);native_ids.update(i['assessment']['relationParticipantIds']);i.update(kind='SOURCE_QUESTION',objectiveId=REF,prompt='Optional reference lookup: '+i['prompt'],feedback='Optional reference-table literacy only. '+i['feedback']);i.pop('assessment')
  changes.append({'before':before,'after':deepcopy(i)});record(i['prompt'],REF,i['id']+'-prompt');record(i['feedback'],REF,i['id']+'-feedback')
 for id in sorted(native_ids):
  r=deepcopy(next(r for r in p['provenanceRecords'] if r['itemId']==id));r.update(itemId='prov-u30-reference-'+id,objectiveId=REF);p['provenanceRecords'].append(r);added.append(r['itemId'])
 unit=p['units'][29];assert unit['number']==30
 reference={'id':REF,'unitNumber':30,'title':'Optional reference-table literacy','kind':'GUIDANCE','gating':False,'instructionStepIds':[],'exampleIds':[],'practiceStepIds':[],'checkItemIds':[],'remediationStepIds':[]}
 titles={NAMES:'Name the supplied Russian letters',ABBR:'Interpret the supplied initialisms and their noun treatment'}
 bodies={
 'u30-st-teach-letter-names-1':'The supplied letters and modern Russian names are А — а, Б — бэ and В — вэ. The new checks show the letter and ask for its Russian name. English respellings, approximate sound comparisons and obsolete Old Church Slavonic names are separate reference information. Name recognition by text does not demonstrate listening or pronunciation, and does not complete the phonetic foundation.',
 'u30-st-teach-letter-names-2':'For optional reference questions only, choose father when asked for that comparison word in the А row. bad is the approximate comparison in the Б row, while both is a translation of its example word. These are not Russian letter names. The optional original table questions do not satisfy the language objective. The three supplied name checks do not demonstrate listening or pronunciation or authorize a general rule for sounding out every letter.',
 'u30-st-teach-abbrev-examples-1':'The retained СССР corresponds to USSR and is grammatically masculine in the supplied noun use. США corresponds to USA and is grammatically plural. The source lists both as indeclinable: their written forms do not gain case endings in this taught use. An adjective agreeing with the initialism follows the supplied gender or number even though the initialism itself stays unchanged. These examples do not classify every Russian abbreviation or demonstrate spoken pronunciation.',
 'u30-st-teach-abbrev-examples-2':'When the shown initialism is a noun referring to the historical Soviet state, interpret USSR and use the supplied masculine agreement treatment. For the United States reference, interpret USA and use the supplied plural treatment. Do not replace agreement with an English table heading or add case endings to these indeclinable examples. Abbreviations is a reference heading, not the English gloss of either country example. The optional heading question is reference literacy and cannot satisfy the language checks.'}
 for obj in unit['objectives']:
  if obj['id'] not in titles:continue
  before=deepcopy(obj);obj['title']=titles[obj['id']];obj['checkItemIds']=[i['id'] for i in items if i['objectiveId']==obj['id'] and i['assessment']['independent']]
  if obj['id']==NAMES:obj['exampleIds']=[e['id'] for e in examples]
  changes.append({'before':before,'after':deepcopy(obj)});record(obj['title'],obj['id'],obj['id']+'-title')
 for session in unit['sessions']:
  if session['id'] not in ['u30-s-letter-names','u30-s-abbrev-examples']:continue
  before=deepcopy(session);obj=session['objectiveIds'][0];session['title']=titles[obj];record(session['title'],obj,session['id']+'-title')
  for step in session['steps']:
   if step['id'] in bodies:
    step['body']=bodies[step['id']];record(step['body'],obj,step['id']+'-body');reference['instructionStepIds'].append(step['id']);record(step['body'],REF,'reference-'+step['id']+'-body')
    if obj==NAMES and step['id'].endswith('-1'):step['exampleIds']=[e['id'] for e in examples]
   elif step['kind']=='GUIDED':step['itemIds']=[i['id'] for i in items if i['objectiveId']==obj and not i['assessment']['independent']]
   elif step['kind']=='CHECK':step['itemIds']=[i['id'] for i in items if i['objectiveId']==obj and i['assessment']['independent']]
  rem=next(s for s in session['steps'] if s['kind']=='REMEDIATE');rem['body']='\n\n'.join(s['body'] for s in session['steps'] if s['kind']=='TEACH');rem['exampleIds']=list(dict.fromkeys(e for s in session['steps'] if s['kind']=='TEACH' for e in s.get('exampleIds',[])));record(rem['body'],obj,rem['id']+'-body')
  changes.append({'before':before,'after':deepcopy(session)})
 unit['objectives'].append(reference);record(reference['title'],REF,'reference-title')
 def q(s):return json.dumps(s,ensure_ascii=False)
 rows=[]
 for r in native:rows.append('        Native('+', '.join(q(r[k]) for k in ['itemId','objectiveId','sourceId','sourceRevision','sourceUrl','sourceRecordId','sourceHash','shippedText'])+'),')
 groups={obj:{'checks':[i['id'] for i in items if i['objectiveId']==obj and i['assessment']['independent']],'guided':[i['id'] for i in items if i['objectiveId']==obj and not i['assessment']['independent']]} for obj in [NAMES,ABBR]}
 code='''package dev.russiancompanion.v2.curriculum
import java.security.MessageDigest
/** Primary native fields and bounded language chains; no source/linguistic acceptance token. */
internal object Unit30LanguagePins {
    private val checks = mapOf(CHECKS)
    private val guided = mapOf(GUIDED)
    private data class Native(val id: String, val objective: String, val source: String, val revision: String,
        val url: String, val record: String, val hash: String, val text: String)
    private val native = listOf(
ROWS
    )
    private fun sha(s: String) = MessageDigest.getInstance("SHA-256").digest(s.toByteArray(Charsets.UTF_8))
        .joinToString("") { "%02x".format(it.toInt() and 255) }
    fun governs(id: String) = id in checks
    fun matches(pack: V2CurriculumPack, objective: V2Objective): Boolean {
        val required = checks[objective.id] ?: return false
        if (objective.checkItemIds != required || objective.kind != "RECOGNITION" || !objective.gating) return false
        val selected = native.filter { it.objective == objective.id }
        if (selected.isEmpty() || !selected.all { pin ->
            val r = pack.provenanceRecords.singleOrNull { it.itemId == pin.id }
            r != null && r.unitNumber == 30 && r.objectiveId == pin.objective && r.sourceId == pin.source &&
                r.sourceRevision == pin.revision && r.sourceUrl == pin.url && r.sourceRecordId == pin.record &&
                r.sourceHash == pin.hash && sha(r.originalSourceText) == pin.hash && r.shippedText == pin.text &&
                r.contentHash == sha(pin.text) && pin.text in r.originalSourceText &&
                r.transformation == "selection" && r.licenseId == "CC BY-SA 4.0"
        }) return false
        val practice = objective.practiceStepIds.flatMap { pack.step(it)?.second?.itemIds.orEmpty() }
        val feature = if (objective.id == "obj-u30-letter-names") "LETTER_NAME" else "ABBREVIATION_TREATMENT"
        fun valid(id: String, independent: Boolean): Boolean {
            val item = pack.item(id) ?: return false
            return item.objectiveId == objective.id && item.assessment?.independent == independent &&
                item.assessment.testedFeature == feature && CompleteRuleContextPins.itemMatches(pack,item) &&
                V2Validator.structureValid(pack,item).isEmpty()
        }
        return required.all { valid(it,true) } && guided.getValue(objective.id).all { it in practice && valid(it,false) }
    }
}
'''.replace('CHECKS',', '.join(q(obj)+' to listOf('+', '.join(map(q,data['checks']))+')' for obj,data in groups.items())).replace('GUIDED',', '.join(q(obj)+' to listOf('+', '.join(map(q,data['guided']))+')' for obj,data in groups.items())).replace('ROWS','\n'.join(rows))
 PIN_FILE.write_text(code)
 after=(json.dumps(p,ensure_ascii=False,separators=(',',':'))+'\n').encode();PACK.write_bytes(after)
 (HERE/'UNIT30_LANGUAGE_CORRECTION.json').write_text(json.dumps(dict(baselineCommit=BASELINE,beforeSha256=hashlib.sha256(raw).hexdigest(),afterSha256=hashlib.sha256(after).hexdigest(),frozenPublicSources={x:hashlib.sha256((ROOT/x).read_bytes()).hexdigest() for x in paths},changes=changes,addedItems=items,addedExamples=examples,addedObjectives=[reference],addedProvenanceIds=added,nativeSelections=native,contextIdentities=pins,bibliography=bibliography,nativePinCodeSha256=sha(code),semanticAcceptance=False,unrestrictedAbbreviationOrPhoneticClaim=False),ensure_ascii=False,indent=2)+'\n')
 print('Added',len(items),'language applications,',len(examples),'name examples and',len(added),'pending records;',len(native),'primary native selections;',len(native_ids),'reference rebindings')
if __name__=='__main__':main()
