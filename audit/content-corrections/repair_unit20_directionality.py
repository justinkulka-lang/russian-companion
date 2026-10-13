#!/usr/bin/env python3
"""Add bounded source-native directional applications; no invented Russian or acceptance."""
import hashlib,json
from copy import deepcopy
from pathlib import Path
HERE=Path(__file__).resolve().parent
ROOT=HERE.parents[1]
PACK=ROOT/'app/src/main/assets/content/v2-curriculum.json'
PIN_FILE=ROOT/'app/src/main/java/dev/russiancompanion/v2/curriculum/Unit20DirectionalityPins.kt'
BASELINE='1ce6184a5bbe894624807c066600fa51de42ace3'
OBJ='obj-u20-motion';FEATURE='MOTION_DIRECTIONAL_APPLICATION'
UNI='Motion in progress in one direction (imperfective).'
GENERAL='General walking ability (imperfective).'
VARIOUS='Movement in various directions (imperfective).'
REPEATED='Repeated trips (imperfective).'
ROUND='One completed round trip in the past (imperfective).'
PERF='The form is perfective because this trip is completed.'
def sha(s):return hashlib.sha256(s.encode()).hexdigest()
def canonical(a):
 def f(s):return str(len(s.encode()))+':'+s
 return 'tuple-v1:1;'+f(a['cue'])+f(a['answer'])+str(len(a['distractors']))+';'+''.join(f(x) for x in a['distractors'])
def main():
 raw=PACK.read_bytes();p=json.loads(raw)
 if any(i['id']=='u20-i-direction-birds' for i in p['items']):print('Already repaired; evidence unchanged');return
 path='catalog/sources/units-26-30/wikipedia/russian-grammar-1369617627.txt';source=(ROOT/path).read_text()
 span=source[source.index('===  Verbs of motion ==='):source.index('==== Unidirectional perfectives with по- ====')].strip()
 assert 'which are all imperfective' in span
 template=next(r for r in p['provenanceRecords'] if r['itemId']=='prov-ca-u5-behavior-teach-1-body')
 primary=next(r for r in p['provenanceRecords'] if r['itemId']=='prov-ex-u20-motion-0-ru')
 bibliography=('Retained Wikipedia Russian grammar oldid1369617627, '+path+': complete motion introduction, unprefixed pair table and Directionality section with all supplied examples. CC BY-SA4.0 through retained Wikipedia:Copyrights oldid1369466282 and existing source/rights declarations. '
  'The source states unprefixed pairs are imperfective. One-direction motion in progress, ability/general motion, movement in various directions, repeated trips and a past completed round trip are distinct supplied uses. Completion of a round trip does not turn the unprefixed form into a perfective. Prefixed setting-out/path meanings remain separately taught and are not inferred from these labels. '
  'Native clauses and bilingual notes are direct selections unchanged; no generated sentence, punctuation deletion, stress insertion or private Penguin text. All earlier lexical/prefixed checks and guided classification remain preserved. '
  'Independently authored English on 2026-10-13 within content-audit-2026-10-08; bounded contextual recognition rather than unrestricted production/all motion grammar. Qualified source/rights/semantic audit pending.')
 added=[];native=[];examples=[];items=[];changes=[];pins={}
 def record(value,name,original=None,apps=None,rid=None):
  original=original or bibliography+'\n'+value;r=deepcopy(template)
  r.update(itemId='prov-ca-u20-direction-'+name,unitNumber=20,objectiveId=OBJ,recordType='label',sourceRecordId=rid or 'ca-coverage:'+OBJ+':direction:'+name,originalSourceText=original,shippedText=value,sourceHash=sha(original),contentHash=sha(value))
  r['synthesis'].update(bibliography=bibliography,authorshipDeclaration='Independently authored English on 2026-10-13 from complete retained directional native contexts; no generated Russian or private text.',objectiveOrClaimBinding=OBJ+':direction:'+name,applications=apps or [])
  p['provenanceRecords'].append(r);added.append(r['itemId']);return r['itemId']
 def select(text,name,meaning):
  assert text in span,(name,text);r=deepcopy(primary);r.pop('originalSourceTextRef',None)
  r.update(itemId='prov-u20-direction-native-'+name,unitNumber=20,objectiveId=OBJ,recordType='meaning' if meaning else 'example',sourceRecordId='motion-native-context:'+span[:77],originalSourceText=span,sourceHash=sha(span),shippedText=text,contentHash=sha(text))
  p['provenanceRecords'].append(r);added.append(r['itemId']);native.append(deepcopy(r))
 contexts=[('library','Мы идём в библиотеку.','We are headed to the library.',UNI,False,[UNI,REPEATED,PERF]),
  ('ability','Ребёнок ходит шесть месяцев.','The child has been walking for six months.',GENERAL,False,[GENERAL,UNI,PERF]),
  ('birds','Птицы летят на юг.','The birds are flying south.',UNI,True,[UNI,VARIOUS,PERF]),
  ('city','Мы ходили по городу весь день.','We walked around the city all day.',VARIOUS,True,[VARIOUS,REPEATED,UNI]),
  ('weekly','Она ходит в супермаркет каждую неделю.','She goes to the supermarket every week.',REPEATED,True,[REPEATED,UNI,PERF]),
  ('round','В прошлом году я ездил в Россию.','I went to Russia (and returned) last year.',ROUND,True,[ROUND,UNI,PERF])]
 reasons={UNI:'The supplied context describes motion in progress in one direction; it does not assert completed arrival.',GENERAL:'The supplied child example describes walking ability over time, rather than a journey now aimed at one destination.',VARIOUS:'The supplied city example describes movement around the city in various directions, not a single directed journey.',REPEATED:'The supplied weekly example describes repeated trips, not one current journey toward the shop.',ROUND:'The retained past example is a completed round trip in the supplied interpretation; its unprefixed verb is still imperfective.'}
 wrongs={UNI:'One directed journey in progress is not the supplied general, various-direction, repeated-trip or completed-return reading.',GENERAL:'A general ability reading is not the supplied one-direction journey or repeated-trip context.',VARIOUS:'Various-direction movement around an area is not the supplied directed flight or weekly repeated trips.',REPEATED:'Repeated trips are not a single movement now in progress or merely movement around an area.',ROUND:'A supplied past completed round trip is a different context from current movement or repeated trips.',PERF:'Unprefixed motion forms in the supplied pairs are imperfective; a completed trip does not by itself make the form perfective.'}
 extemplate=next(e for e in p['examples'] if e['id']=='ex-u20-motion-0');itemtemplate=next(i for i in p['items'] if i['id']=='u20-i-motion-1')
 for name,cue,note,answer,independent,opts in contexts:
  select(cue,name+'-ru',False);select(note,name+'-en',True)
  e=deepcopy(extemplate);e.update(id='ex-u20-direction-'+name,display=cue,note=note);p['examples'].append(e);examples.append(deepcopy(e))
  id='u20-'+('i' if independent else 'g')+'-direction-'+name;feedback='In '+cue+' '+reasons[answer]+' The answer is '+answer+' Read this whole source example rather than translating an isolated verb or naming a table column. The supplied interpretation is bounded; these labels do not classify every prefixed verb.'
  rationales=['' if v==answer else v+' '+wrongs[v] for v in opts];i=deepcopy(itemtemplate);i.update(id=id,sourceId=template['sourceId'],prompt='Which English explanation fits the directionality and aspect of this whole supplied example?',promptLetter=cue,options=opts,answer=answer,feedback=feedback,distractorRationales=rationales)
  a={'cue':cue,'answer':answer,'distractors':[v for v in opts if v!=answer]};rid='ca-rule-context:'+id;original=bibliography+'\nContext explanation:\n'+feedback+'\nTuple-v1:'+canonical(a);ids=[record(v,id+'-'+str(n),original,[a],rid) for n,v in enumerate([cue,answer]+a['distractors'])]
  i['assessment'].update(testedFeature=FEATURE,independent=independent,contrastSetId=id,cueLanguage='ru',responseKind='ENGLISH_CLASSIFICATION',evidenceMode='STATED_APPLICATION',relationKind='STATED_APPLICATION',relationParticipantIds=ids,answerSourceIds=[ids[1]],distractorSourceIds=['' if v==answer else ids[2+a['distractors'].index(v)] for v in opts],distractorDifference=rationales,contrastMembers=opts,whyAnswerDemonstratesObjective=feedback)
  p['items'].append(i);items.append(deepcopy(i));pins[rid]={'sourceHash':sha(original),'applications':[a],'objectiveId':OBJ}
  for field in ['prompt','feedback']:record(i[field],id+'-'+field)
  for n,v in enumerate(rationales):
   if v:record(v,id+'-rationale-'+str(n))
 unit=p['units'][19];obj=next(o for o in unit['objectives'] if o['id']==OBJ);before=deepcopy(obj)
 obj['checkItemIds'] += [i['id'] for i in items if i['assessment']['independent']];obj['exampleIds'] += [e['id'] for e in examples]
 steps=[{'id':'u20-st-teach-direction-contexts','kind':'TEACH','title':'Interpret directionality in a whole example','body':'Мы идём в библиотеку. — We are headed to the library. Птицы летят на юг. — The birds are flying south. These supplied contexts show '+UNI+' Ребёнок ходит шесть месяцев. — The child has been walking for six months. This is '+GENERAL+' Мы ходили по городу весь день. — We walked around the city all day. This is '+VARIOUS+' Directionality is a property of the whole supplied use, not an English lexical gloss or an automatic claim that the traveller arrived. The basic unprefixed pairs are imperfective.','referenceIds':['ref-u1620-attribution'],'exampleIds':[e['id'] for e in examples[:4]]},
 {'id':'u20-st-teach-direction-return','kind':'TEACH','title':'Distinguish repeated trips and a completed return','body':'Она ходит в супермаркет каждую неделю. — She goes to the supermarket every week. This is '+REPEATED+' В прошлом году я ездил в Россию. — I went to Russia (and returned) last year. In the retained supplied context this is '+ROUND+' The past round-trip interpretation includes a return; the unprefixed form nevertheless remains imperfective. A choice saying “'+PERF+'” makes an incorrect aspect inference for these forms. Do not equate a destination with unidirectionality or event completion with perfective aspect. Prefixed setting-out and path examples require their separately taught meanings.','referenceIds':['ref-u1620-attribution'],'exampleIds':[e['id'] for e in examples[4:]]}]
 obj['instructionStepIds'] += [s['id'] for s in steps];changes.append({'before':before,'after':deepcopy(obj)})
 session=next(s for s in unit['sessions'] if s['id']=='u20-s-motion');before=deepcopy(session);idx=next(n for n,s in enumerate(session['steps']) if s['kind']=='GUIDED');session['steps'][idx:idx]=steps
 for s in steps:record(s['title'],s['id']+'-title');record(s['body'],s['id']+'-body')
 next(s for s in session['steps'] if s['kind']=='GUIDED')['itemIds'] += [i['id'] for i in items if not i['assessment']['independent']]
 next(s for s in session['steps'] if s['kind']=='CHECK')['itemIds'] += [i['id'] for i in items if i['assessment']['independent']]
 rem=next(s for s in session['steps'] if s['kind']=='REMEDIATE');teach=[s for s in session['steps'] if s['kind']=='TEACH'];rem['body']='\n\n'.join(s['body'] for s in teach);rem['exampleIds']=list(dict.fromkeys(e for s in teach for e in s.get('exampleIds',[])));record(rem['body'],rem['id']+'-body');changes.append({'before':before,'after':deepcopy(session)})
 # Two pre-existing corrected intakes share the complete motion objective chain.
 # Preserve their original hosts and append replacements covering the new teaching.
 for cap in p['sourceCapabilities']:
  if cap['id'] not in ['cap-u20-motion-po-setting-out','cap-u20-motion-prefixed-path']:continue
  before=deepcopy(cap);hosts=[r for r in p['provenanceRecords'] if r['sourceRecordId']==cap['sourceRecordId'] and r['sourceHash']==cap['sourceHash']]
  cap['exactSpan'] += '\n'+bibliography+'\n'+rem['body'];cap['sourceHash']=sha(cap['exactSpan']);changes.append({'before':before,'after':deepcopy(cap)})
  old_item=next(i for i in p['items'] if i['id']==('u20-i-motion-3' if cap['supportedClaim']=='motion-po-setting-out' else 'u20-i-motion-4'));before_item=deepcopy(old_item);mapping={}
  for id in old_item['assessment']['relationParticipantIds']:
   old=next(r for r in hosts if r['itemId']==id);new=deepcopy(old);new.pop('originalSourceTextRef',None);new.update(itemId='prov-u20-direction-rebind-'+id,originalSourceText=cap['exactSpan'],sourceHash=cap['sourceHash']);new['synthesis']['bibliography'] += ' '+bibliography;p['provenanceRecords'].append(new);added.append(new['itemId']);mapping[id]=new['itemId']
  for field in ['relationParticipantIds','answerSourceIds','distractorSourceIds']:old_item['assessment'][field]=[mapping.get(id,id) for id in old_item['assessment'][field]]
  changes.append({'before':before_item,'after':deepcopy(old_item)})
  for step in steps+[rem]:
   new=deepcopy(hosts[0]);new.pop('originalSourceTextRef',None);new.update(itemId='prov-u20-direction-cap-'+cap['id']+'-'+step['id'],recordType='label',originalSourceText=cap['exactSpan'],sourceHash=cap['sourceHash'],shippedText=step['body'],contentHash=sha(step['body']));new['synthesis']['bibliography'] += ' '+bibliography;p['provenanceRecords'].append(new);added.append(new['itemId'])
 def q(s):return json.dumps(s,ensure_ascii=False)
 rows=['        Native('+', '.join(q(r[k]) for k in ['itemId','sourceId','sourceRevision','sourceUrl','sourceRecordId','sourceHash','shippedText'])+'),' for r in native]
 code='''package dev.russiancompanion.v2.curriculum
import java.security.MessageDigest
/** Frozen bounded motion contexts; no source/rights/semantic acceptance token. */
internal object Unit20DirectionalityPins {
    val checkIds = listOf(CHECKS)
    val guidedIds = listOf(GUIDED)
    private data class Native(val id: String,val source: String,val revision: String,val url: String,
        val record: String,val hash: String,val text: String)
    private val native = listOf(
ROWS
    )
    private fun sha(s: String) = MessageDigest.getInstance("SHA-256").digest(s.toByteArray(Charsets.UTF_8))
        .joinToString("") { "%02x".format(it.toInt() and 255) }
    fun matches(pack: V2CurriculumPack): Boolean {
        val obj = pack.unit(20)?.objectives?.singleOrNull { it.id == "obj-u20-motion" } ?: return false
        if (obj.checkItemIds != checkIds || obj.kind != "RECOGNITION" || !obj.gating) return false
        if (!native.all { pin ->
            val r = pack.provenanceRecords.singleOrNull { it.itemId == pin.id }
            r != null && r.unitNumber == 20 && r.objectiveId == obj.id && r.sourceId == pin.source &&
                r.sourceRevision == pin.revision && r.sourceUrl == pin.url && r.sourceRecordId == pin.record &&
                r.sourceHash == pin.hash && sha(r.originalSourceText) == pin.hash && r.shippedText == pin.text &&
                r.contentHash == sha(pin.text) && pin.text in r.originalSourceText &&
                r.transformation == "selection" && r.licenseId == "CC BY-SA 4.0"
        }) return false
        val practice = obj.practiceStepIds.flatMap { pack.step(it)?.second?.itemIds.orEmpty() }
        fun valid(id: String,independent: Boolean): Boolean {
            val item = pack.item(id) ?: return false
            return item.objectiveId == obj.id && item.assessment?.independent == independent &&
                item.assessment.testedFeature == "MOTION_DIRECTIONAL_APPLICATION" &&
                CompleteRuleContextPins.itemMatches(pack,item) && V2Validator.structureValid(pack,item).isEmpty()
        }
        return checkIds.filter { "-direction-" in it }.all { valid(it,true) } &&
            guidedIds.all { it in practice && valid(it,false) }
    }
}
'''.replace('CHECKS',', '.join(map(q,obj['checkItemIds']))).replace('GUIDED',', '.join(q(i['id']) for i in items if not i['assessment']['independent'])).replace('ROWS','\n'.join(rows))
 PIN_FILE.write_text(code);after=(json.dumps(p,ensure_ascii=False,separators=(',',':'))+'\n').encode();PACK.write_bytes(after)
 (HERE/'UNIT20_DIRECTIONALITY_CORRECTION.json').write_text(json.dumps(dict(baselineCommit=BASELINE,beforeSha256=hashlib.sha256(raw).hexdigest(),afterSha256=hashlib.sha256(after).hexdigest(),frozenPublicSources={path:hashlib.sha256((ROOT/path).read_bytes()).hexdigest()},changes=changes,addedExamples=examples,addedItems=items,addedProvenanceIds=added,nativeSelections=native,contextIdentities=pins,bibliography=bibliography,nativePinCodeSha256=sha(code),semanticAcceptance=False,unrestrictedMotionGrammarClaim=False),ensure_ascii=False,indent=2)+'\n')
 print('Added',len(items),'applications,',len(examples),'examples,',len(added),'pending records;',len(native),'native selections')
if __name__=='__main__':main()
