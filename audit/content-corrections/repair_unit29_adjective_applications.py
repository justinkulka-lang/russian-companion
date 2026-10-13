#!/usr/bin/env python3
"""Add bounded agreement and native predicate/attribute applications, without inventing Russian."""
import hashlib,json
from copy import deepcopy
from pathlib import Path
HERE=Path(__file__).resolve().parent
ROOT=HERE.parents[1]
PACK=ROOT/'app/src/main/assets/content/v2-curriculum.json'
PIN_FILE=ROOT/'app/src/main/java/dev/russiancompanion/v2/curriculum/Unit29AdjectiveNativePins.kt'
BASELINE='98b5b30ffc224b1119bc975fc698386ce8b91d3f'
OBJ='obj-u29-short-adjective'
PRED='A predicate describing the subject.'
ATTR='An attributive adjective modifying the noun.'
def sha(s):return hashlib.sha256(s.encode()).hexdigest()
def canonical(a):
 def field(s):return str(len(s.encode()))+':'+s
 return 'tuple-v1:1;'+field(a['cue'])+field(a['answer'])+str(len(a['distractors']))+';'+''.join(field(s) for s in a['distractors'])
def main():
 raw=PACK.read_bytes();p=json.loads(raw)
 if any(i['id']=='u29-i-short-agreement-f' for i in p['items']):print('Already repaired; evidence unchanged');return
 sources=['catalog/sources/units-26-30/wikipedia/russian-grammar-1369617627.txt','catalog/sources/units-3-5/wikibooks/nominative-4213267.txt','catalog/sources/units-3-5/wikibooks/copyrights-4622060.txt']
 wiki=(ROOT/sources[0]).read_text();nom=(ROOT/sources[1]).read_text()
 assert 'short form appears only in the nominative' in wiki and 'нова́' in wiki and 'новы́' in wiki
 template=next(r for r in p['provenanceRecords'] if r['itemId']=='prov-ca-u5-behavior-teach-1-body')
 native_wiki=next(r for r in p['provenanceRecords'] if r['itemId']=='prov-ex-u29-short-adjective-1-ru')
 native_nom=next(r for r in p['provenanceRecords'] if r['itemId']=='prov-u5-indecl-rule')
 bibliography=('Retained Wikipedia Russian grammar oldid1369617627, '+sources[0]+', CC BY-SA 4.0 under retained FREEZE rights attestation: complete Adjectives introduction, full/short declension table and stress/formation exceptions; admitted short-form span sourceHash0b5b1bad657a8c901ba9f471ff7adff3a945be6f281aa760f58c59fd0f8c78e8. '
  'Retained Wikibooks Russian/Grammar/Nominative oldid4213267, '+sources[1]+', CC BY-SA 4.0 via retained Wikibooks:Copyrights oldid4622060 ('+sources[2]+') and existing rights declaration: complete Nouns and Adjectives context, explicit old-train/new-student clauses and new-pencil/book/letter/books attributive phrases. Only these specific native examples and their subject gender/number are adopted, not universal endings, simplistic default-case or borrowed-noun gender claims elsewhere in the snapshot. '
  'Short forms agree for gender/number, not case. Long form does not force attributive function: the actual complete clause predicates a property of the subject. A noun phrase modifies its head; no universal word-position or permanent/temporary rule is taught. Independent agreement questions use directly selected native noun phrases as their cues; no Russian sentence is synthesized. '
  'The Wikibooks adjectives4292379 snapshot with overgeneralized rules, stress/spelling and Latin-letter problems is not used. '
  'Independently authored English on 2026-10-13 within content-audit-2026-10-08; no new generated Russian, copied explanatory primary prose or private Penguin text; qualified source/rights/semantic audit pending.')
 added=[];native=[];examples=[];items=[];changes=[];pins={}
 def record(value,name,original=None,applications=None,rid=None,target=OBJ):
  original=original or bibliography+'\n'+value;r=deepcopy(template)
  r.update(itemId='prov-ca-u29-application-'+name,unitNumber=29,objectiveId=target,recordType='label',sourceRecordId=rid or 'ca-coverage:'+target+':adjective-application:'+name,
   originalSourceText=original,shippedText=value,sourceHash=sha(original),contentHash=sha(value))
  r['synthesis'].update(bibliography=bibliography,authorshipDeclaration='Independently authored English on 2026-10-13 from retained complete grammar context and source-native examples; no invented Russian or private book prose.',objectiveOrClaimBinding=target+':adjective-application:'+name,applications=applications or [])
  p['provenanceRecords'].append(r);added.append(r['itemId']);return r['itemId']
 def select(t,value,name,span=None):
  r=deepcopy(t);r.update(itemId='prov-u29-adjective-native-'+name,unitNumber=29,objectiveId=OBJ,shippedText=value,contentHash=sha(value),recordType='meaning' if name.endswith('-en') else 'example')
  if span is not None:
   r.pop('originalSourceTextRef',None);r.update(originalSourceText=span,sourceHash=sha(span),sourceRecordId='nominative-context:'+span[:77])
   r['attribution'] += ' License route: Wikibooks:Copyrights oldid 4622060; CC BY-SA 4.0.'
  original=r.get('originalSourceText') or p['sourceTextPool'][r['originalSourceTextRef']]
  assert value in original,(name,value)
  p['provenanceRecords'].append(r);added.append(r['itemId']);native.append(deepcopy(r));return r
 extemplate=next(e for e in p['examples'] if e['id']=='ex-u29-short-adjective-1')
 def example(name,display,note,source):
  e=deepcopy(extemplate);e.update(id='ex-u29-adjective-context-'+name,display=display,note=note,sourceId=source);p['examples'].append(e);examples.append(deepcopy(e))
 for name,form in [('f','нова́'),('n','но́во'),('pl','новы́')]:
  select(native_wiki,form,name+'-ru');select(next(r for r in p['provenanceRecords'] if r['itemId']=='prov-ex-u29-short-adjective-0-en'),'new',name+'-en');example(name,form,'new',native_wiki['sourceId'])
 contexts=[('train','Поезд старый','The train is old'),('pencil','новый карандаш','new pencil'),('student','Студент новый','The student is new'),('letter','новое письмо','new letter'),('book','новая книга','new book'),('books','новые книги','new books')]
 spans={}
 for name,display,note in contexts:
  span=next(line for line in nom.splitlines() if display in line and note.lower() in line.lower());spans[name]=span
  select(native_nom,display,name+'-ru',span);select(native_nom,note,name+'-en',span);example(name,display,note,native_nom['sourceId'])
 for name,noun in [('book','книга'),('letter','письмо'),('books','книги')]:select(native_nom,noun,name+'-subject',spans[name])
 itemtemplate=next(i for i in p['items'] if i['id']=='u29-i-adjective-form-1')
 def application(id,cue,options,answer,feedback,rationales,feature,independent,cue_language,response):
  prompt=('Which supplied short adjective meaning new would agree with the head noun of this phrase?' if independent else 'Which supplied short adjective agrees with the described subject?') if feature=='SHORT_AGREEMENT' else 'Which English explanation describes the adjective function in this whole example?'
  i=deepcopy(itemtemplate);i.update(id=id,prompt=prompt,promptLetter=cue,options=options,answer=answer,feedback=feedback,distractorRationales=rationales)
  a={'cue':cue,'answer':answer,'distractors':[v for v in options if v!=answer]};rid='ca-rule-context:'+id;span=bibliography+'\nContext explanation:\n'+feedback+'\nTuple-v1:'+canonical(a)
  ids=[record(v,id+'-'+str(n),span,[a],rid) for n,v in enumerate([cue,answer]+a['distractors'])]
  i['assessment'].update(testedFeature=feature,independent=independent,contrastSetId=id,cueLanguage=cue_language,responseKind=response,evidenceMode='STATED_APPLICATION',relationKind='STATED_APPLICATION',relationParticipantIds=ids,answerSourceIds=[ids[1]],distractorSourceIds=['' if v==answer else ids[2+a['distractors'].index(v)] for v in options],contrastMembers=options,distractorDifference=rationales,whyAnswerDemonstratesObjective=feedback)
  p['items'].append(i);items.append(deepcopy(i));pins[rid]={'sourceHash':sha(span),'applications':[a],'objectiveId':OBJ}
  for field in ['prompt','feedback']:record(i[field],id+'-'+field)
  for n,v in enumerate(rationales):
   if v:record(v,id+'-rationale-'+str(n))
 forms={'нов':'masculine singular','нова́':'feminine singular','но́во':'neuter singular','новы́':'plural'}
 agreements=[('u29-g-short-agreement-m','One masculine singular subject is described as new. Choose the short adjective.',['нов','нова́','но́во'],'нов',False),
  ('u29-g-short-agreement-n','One neuter singular subject is described as new. Choose the short adjective.',['но́во','новы́','нова́'],'но́во',False),
  ('u29-i-short-agreement-f','новая книга',['нова́','но́во','новы́'],'нова́',True),
  ('u29-i-short-agreement-n','новое письмо',['но́во','нова́','нов'],'но́во',True),
  ('u29-i-short-agreement-pl','новые книги',['новы́','нов','нова́'],'новы́',True)]
 for id,cue,opts,answer,independent in agreements:
  gender=forms[answer];feedback=f'{answer} is the supplied {gender} short form of new. It agrees with the described subject in gender and number; short adjectives have no case paradigm in the taught modern use. The other supplied forms express different gender or number. This applies the admitted paradigm, without inventing a Russian sentence or a general short-form formation algorithm.'
  rationales=['' if v==answer else f'{v} is the supplied {forms[v]} short form, rather than the {gender} form required by this subject.' for v in opts]
  application(id,cue,opts,answer,feedback,rationales,'SHORT_AGREEMENT',independent,'ru' if independent else 'rule','RUSSIAN_TOKEN')
 functions=[('u29-g-adjective-function-p','Поезд старый',PRED,False,'The whole clause says the train is old. The long adjective predicates a property of the train; it is not merely a noun phrase naming an old train.'),
  ('u29-g-adjective-function-a','новый карандаш',ATTR,False,'The supplied phrase means new pencil. The long adjective modifies pencil inside a noun phrase; this phrase alone does not assert that the pencil is new in a complete sentence.'),
  ('u29-i-adjective-function-p','Студент новый',PRED,True,'The whole clause says the student is new. The long adjective predicates a property of the student; long form does not automatically mean attributive function. There is no overt present copula in this supplied clause.'),
  ('u29-i-adjective-function-a','новое письмо',ATTR,True,'The supplied phrase means new letter. The long adjective modifies letter inside a noun phrase. It is not a complete assertion that the letter is new. This interpretation uses the whole source example and its meaning, not a universal word-position rule.')]
 for id,cue,answer,independent,reason in functions:
  opts=[answer,ATTR if answer==PRED else PRED];feedback=reason+' '+answer
  rationale='An attributive modifier belongs inside the noun phrase; the supplied example instead predicates a property of its subject.' if answer==PRED else 'A predicate would assert a property of a subject in a clause; the supplied example is instead an attributive noun phrase.'
  application(id,cue,opts,answer,feedback,['',rationale],'ADJECTIVE_FUNCTION',independent,'ru','ENGLISH_CLASSIFICATION')
 unit=p['units'][28];obj=next(o for o in unit['objectives'] if o['id']==OBJ);before=deepcopy(obj)
 obj['checkItemIds'] += [i['id'] for i in items if i['assessment']['independent']];obj['exampleIds'] += [e['id'] for e in examples]
 newsteps=[{'id':'u29-st-teach-short-agreement','kind':'TEACH','title':'Apply short-form gender and number agreement','body':'The supplied short forms of new are нов for masculine singular, нова́ for feminine singular, но́во for neuter singular and новы́ for plural. Short adjectives agree with their subject in gender and number; they do not have a modern case paradigm. In the retained noun examples, книга is feminine singular, письмо is neuter singular, and книги has the supplied plural reading. Choose the short form for that subject rather than another form with the same lexical meaning. Some adjectives lack short forms and stress can change; these supplied forms are not a universal truncation recipe.','referenceIds':['ref-u2630-attribution'],'exampleIds':['ex-u29-short-adjective-1']+[e['id'] for e in examples if e['id'].endswith(('-f','-n','-pl','-book','-books','-letter'))]},
  {'id':'u29-st-teach-adjective-predicate','kind':'TEACH','title':'A long form can be a predicate','body':'The complete clauses Поезд старый and Студент новый say that the train is old and the student is new. Their long adjectives predicate a property of the subject. No overt present copula appears in these supplied clauses. Long form therefore does not force attributive function, and a temporary-versus-permanent slogan does not decide every form choice. Read the whole example and its supplied meaning rather than its adjective ending alone.','referenceIds':['ref-u2630-attribution'],'exampleIds':['ex-u29-adjective-context-train','ex-u29-adjective-context-student']},
  {'id':'u29-st-teach-adjective-attributive','kind':'TEACH','title':'Distinguish a noun phrase from a predication','body':'The supplied новый карандаш and новое письмо mean new pencil and new letter. Each is an attributive noun phrase: the adjective modifies the head noun. These phrases alone do not assert that a pencil or letter is new in a complete clause. The adjective and noun agree in gender and number in the supplied singular examples. Do not infer an exceptionless word-position rule or every possible case from an isolated phrase. These exercises assess bounded agreement and function recognition, not unrestricted production or every long/short meaning distinction.','referenceIds':['ref-u2630-attribution'],'exampleIds':['ex-u29-adjective-context-pencil','ex-u29-adjective-context-letter']}]
 obj['instructionStepIds'] += [s['id'] for s in newsteps];changes.append({'before':before,'after':deepcopy(obj)})
 session=next(s for s in unit['sessions'] if s['id']=='u29-s-short-adjective');before=deepcopy(session)
 body2=next(s for s in session['steps'] if s['id']=='u29-st-teach-short-adjective-2');body2['body']='In the taught modern use, short adjectives occur only in the nominative and are used predicatively to describe their subject. A long adjective can modify a noun attributively and can also be a predicate. The choice depends on the sentence, not a universal rule about one fixed word position. The following supplied forms, complete clauses and noun phrases support bounded agreement and function checks; they do not settle every long-versus-short semantic or stylistic choice.'
 record(body2['body'],body2['id']+'-body');record(body2['body'],'vocabulary-'+body2['id']+'-body',target='obj-u29-adjective-vocabulary')
 index=next(n for n,s in enumerate(session['steps']) if s['kind']=='GUIDED');session['steps'][index:index]=newsteps
 for s in newsteps:
  record(s['title'],s['id']+'-title');record(s['body'],s['id']+'-body')
 guided=next(s for s in session['steps'] if s['kind']=='GUIDED');guided['itemIds'] += [i['id'] for i in items if not i['assessment']['independent']]
 check=next(s for s in session['steps'] if s['kind']=='CHECK');check['itemIds']=obj['checkItemIds']
 rem=next(s for s in session['steps'] if s['kind']=='REMEDIATE');rem['body']='\n\n'.join(s['body'] for s in session['steps'] if s['kind']=='TEACH');rem['exampleIds']=list(dict.fromkeys(e for s in session['steps'] if s['kind']=='TEACH' for e in s.get('exampleIds',[])));record(rem['body'],rem['id']+'-body')
 changes.append({'before':before,'after':deepcopy(session)})
 def q(s):return json.dumps(s,ensure_ascii=False)
 rows=[]
 for r in native:
  rows.append('        Native('+', '.join(q(r[k]) for k in ['itemId','sourceId','sourceRevision','sourceUrl','sourceRecordId','sourceHash','shippedText'])+'),')
 guided_ids=guided['itemIds'];features={i['id']:i['assessment']['testedFeature'] for i in p['items'] if i['id'] in obj['checkItemIds']+guided_ids}
 code='''package dev.russiancompanion.v2.curriculum

import java.security.MessageDigest

/** Frozen native selections for bounded agreement/function applications; no audit acceptance. */
internal object Unit29AdjectiveNativePins {
    val checkIds = listOf(CHECK_IDS)
    val guidedIds = listOf(GUIDED_IDS)
    private val features = mapOf(FEATURES)
    fun feature(id: String) = features[id]
    private data class Native(val id: String, val source: String, val revision: String, val url: String,
        val record: String, val hash: String, val text: String)
    private val native = listOf(
NATIVE_ROWS
    )
    private fun sha(s: String) = MessageDigest.getInstance("SHA-256").digest(s.toByteArray(Charsets.UTF_8))
        .joinToString("") { "%02x".format(it.toInt() and 255) }
    fun matches(pack: V2CurriculumPack): Boolean = native.all { pin ->
        val records = pack.provenanceRecords.filter { it.itemId == pin.id }
        val r = records.singleOrNull()
        r != null && r.unitNumber == 29 && r.objectiveId == "obj-u29-short-adjective" &&
            r.sourceId == pin.source && r.sourceRevision == pin.revision && r.sourceUrl == pin.url &&
            r.sourceRecordId == pin.record && r.sourceHash == pin.hash && sha(r.originalSourceText) == pin.hash &&
            r.shippedText == pin.text && r.contentHash == sha(pin.text) && pin.text in r.originalSourceText &&
            r.transformation == "selection" && r.licenseId == "CC BY-SA 4.0"
    }
}
'''.replace('CHECK_IDS',', '.join(map(q,obj['checkItemIds']))).replace('GUIDED_IDS',', '.join(map(q,guided_ids))).replace('FEATURES',', '.join(q(k)+' to '+q(v) for k,v in features.items())).replace('NATIVE_ROWS','\n'.join(rows))
 PIN_FILE.write_text(code)
 after=(json.dumps(p,ensure_ascii=False,separators=(',',':'))+'\n').encode();PACK.write_bytes(after)
 (HERE/'UNIT29_ADJECTIVE_APPLICATION_CORRECTION.json').write_text(json.dumps(dict(baselineCommit=BASELINE,beforeSha256=hashlib.sha256(raw).hexdigest(),afterSha256=hashlib.sha256(after).hexdigest(),frozenPublicSources={s:hashlib.sha256((ROOT/s).read_bytes()).hexdigest() for s in sources},addedProvenanceIds=added,nativeSelections=native,addedExamples=examples,addedItems=items,changes=changes,contextIdentities=pins,bibliography=bibliography,nativePinCodeSha256=sha(code),coverageScope='BOUNDED_AGREEMENT_AND_NATIVE_LONG_PREDICATE_ATTRIBUTIVE_CONTEXTS_PENDING_INDEPENDENT_VERIFICATION',unrestrictedGrammarClaim=False,semanticAcceptance=False),ensure_ascii=False,indent=2)+'\n')
 print('Added',len(items),'applications,',len(examples),'native examples and',len(added),'pending records;',len(native),'native selections')
if __name__=='__main__':main()
