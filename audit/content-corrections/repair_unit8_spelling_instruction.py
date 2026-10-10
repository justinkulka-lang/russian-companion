#!/usr/bin/env python3
"""Explain the retained adjective contrasts without changing keys or Russian forms."""
import hashlib,json
from copy import deepcopy
from pathlib import Path
HERE=Path(__file__).resolve().parent;ROOT=HERE.parents[1];PACK=ROOT/'app/src/main/assets/content/v2-curriculum.json';OBJ='obj-u8-spelling'
def sha(s):return hashlib.sha256(s.encode()).hexdigest()
def main():
 raw=PACK.read_bytes();p=json.loads(raw);u=next(u for u in p['units'] if u['number']==8);s=next(s for s in u['sessions'] if s['id']=='u8-s-spelling')
 if any(r['itemId']=='prov-ca-u8-worked-teach-1-body' for r in p['provenanceRecords']):print('Already repaired; retained evidence unchanged');return
 template=next(r for r in p['provenanceRecords'] if r['itemId']=='prov-ca-8-324')
 bibliography=('Cornell Russian Language Program, Spelling rules for case endings, https://russian.cornell.edu/grammar/html/spelling_rules.htm, Rule A; Nominative Plural, https://russian.cornell.edu/grammar/html/gr04_a.htm, noun/adjective endings and alternative soft-stem patterns. '
  'University of Kansas, Mezhdu nami, unit 1.8, https://mezhdunami.ku.edu/unit01/1_8/language.shtml, seven-letter spelling rule and worked book plural. Primary HTML text retrieved 2026-10-10 UTC (2026-10-09 America/Chicago). '
  'Retained OpenRussian commit 50e210c4803237779cb562bc1abcea529066031c, adjectives.csv:хороший#decl_m_nom, #decl_pl_nom and #decl_n_nom; nouns.csv:книга#sg_nom, #pl_nom and nouns.csv:день#pl_nom. '
  'Existing form records: prov-ex-u8-spelling-0-ru, prov-ex-or-u8-spelling-1, prov-ex-or-u8-spelling-2, prov-ex-or-u8-spelling-3, prov-ex-or-u8-spelling-4, prov-ex-or-u8-spelling-5 and prov-ex-or-u8-spelling-6; existing rule record prov-ca-8-324. '
  'Application inference: explain those retained forms; selecting among correctly spelled nominative forms does not isolate spelling ability. Independently authored English on 2026-10-10 UTC within content-audit-2026-10-08; no primary prose or new Russian form copied. Final semantic audit pending.')
 retained_ids=['prov-ex-u8-spelling-0-ru']+[f'prov-ex-or-u8-spelling-{n}' for n in range(1,7)]+['prov-ca-8-324']
 assert all(any(r['itemId']==id for r in p['provenanceRecords']) for id in retained_ids)
 added=[]
 def record(text,name):
  r=deepcopy(template);original=bibliography+'\n'+text;r.update(itemId='prov-ca-u8-worked-'+name,recordType='label',sourceRecordId='ca-coverage:'+OBJ+':worked:'+name,originalSourceText=original,shippedText=text,sourceHash=sha(original),contentHash=sha(text))
  r['synthesis'].update(bibliography=bibliography,objectiveOrClaimBinding=OBJ+':worked:'+name,authorshipDeclaration='Independently authored English for Russian Companion on 2026-10-10 UTC, applying reference rules to previously retained Russian forms; no owned-book prose.',applications=[])
  assert not any(x['itemId']==r['itemId'] for x in p['provenanceRecords']);p['provenanceRecords'].append(r);added.append(r['itemId'])
 bodies={
  1:'Adjectives agree with their nouns in gender, number and case. In хорошее письмо, the adjective describes a neuter singular noun. хоро́ший is the dictionary form: masculine singular nominative. This lesson compares singular and plural nominative forms, then explains the spelling in the retained plurals.',
  2:'After г, к, х, ж, ч, ш, or щ, write и rather than ы. The seven-letter condition is required. This rule applies to the spelling of noun and adjective endings. In кни́ги, и follows г; in хоро́шие, и follows ш. The rule controls spelling, but the question determines the required gender, number and case.',
  3:'For these nominative checks, кни́га means book in the singular and кни́ги means books in the plural. To form this plural, replace the singular ending with the plural ending: кни́га becomes кни́ги. The stem ends in г, so the seven-letter rule requires и. To return to singular nominative, choose кни́га.',
  4:'кни́ги is plural nominative in this lesson. дни is also plural nominative and means days. Its и does not follow a letter in the seven-letter set, so дни is not an example of that spelling replacement. Not every plural ending in и comes from the seven-letter rule. Identify number from the requested form and context.',
  5:'хоро́шее is neuter singular nominative; хоро́шие is plural nominative. To change the neuter singular form to plural nominative, choose хоро́шие; to return to neuter singular, choose хоро́шее. For the masculine singular dictionary form, choose хоро́ший. All these options are correctly spelled: the questions test form recognition rather than choosing between correct and incorrect spellings.'}
 titles={1:'Singular and plural forms',2:'The seven-letter spelling rule',3:'A worked noun plural',4:'Two plural forms, different patterns',5:'Adjective number and dictionary form'}
 for n in bodies:
  st=next(st for st in s['steps'] if st['id']==f'u8-st-teach-spelling-{n}');st['body']=bodies[n];st['title']=titles[n];record(st['body'],f'teach-{n}-body');record(st['title'],f'teach-{n}-title')
 title='Recognize nominative forms and plural spelling';s['title']=title;next(o for o in u['objectives'] if o['id']==OBJ)['title']=title;record(title,'objective-title')
 rem=next(st for st in s['steps'] if st['kind']=='REMEDIATE');rem['body']='\n\n'.join(bodies[n] for n in [1,2,3,4,5]);rem['title']='Review nominative forms and spelling';rem['exampleIds']=list(next(o for o in u['objectives'] if o['id']==OBJ)['exampleIds']);record(rem['body'],'rem-body');record(rem['title'],'rem-title')
 for kind,text,name in [('GUIDED','Choose the requested singular or plural nominative form. Then read why the other form does not fit.','guided-body'),('CHECK','Choose the requested noun or adjective nominative form. A missed check returns to the explanations and examples.','check-body')]:
  st=next(st for st in s['steps'] if st['kind']==kind);st['body']=text;record(text,name)
 for id in ['u8-g-spelling-0','u8-i-spelling-1']:
  next(i for i in p['items'] if i['id']==id)['assessment']['testedFeature']='NOUN_NOMINATIVE_NUMBER_FORM'
 record('NOUN_NOMINATIVE_NUMBER_FORM','noun-feature')
 for id in ['u8-i-spelling-2','u8-i-spelling-3','u8-i-spelling-4']:
  next(i for i in p['items'] if i['id']==id)['assessment']['testedFeature']='ADJECTIVE_NOMINATIVE_FORM'
 record('ADJECTIVE_NOMINATIVE_FORM','adjective-feature')
 assert len(added)==17,added
 after=(json.dumps(p,ensure_ascii=False,indent=1)+'\n').encode();PACK.write_bytes(after)
 e=dict(schemaVersion=1,previousHead='8547d99246ee5eeaaebd965f26a5ceab90217f07',unitNumber=8,objectiveId=OBJ,disposition='IMPLEMENTED_PENDING_FINAL_AUDIT',beforeCurriculumFileSha256=hashlib.sha256(raw).hexdigest(),afterCurriculumFileSha256=hashlib.sha256(after).hexdigest(),addedProvenanceIds=added,retainedRecordIds=retained_ids,changedItemIds=['u8-g-spelling-0','u8-i-spelling-1','u8-i-spelling-2','u8-i-spelling-3','u8-i-spelling-4'],bibliography=bibliography,scope='Existing spelling-session instruction, titles, complete remediation; objective/session title; five tested-feature labels only. Keys, Russian forms, source relations and advancement unchanged.')
 (HERE/'UNIT8_SPELLING_INSTRUCTION_CORRECTION.json').write_text(json.dumps(e,ensure_ascii=False,indent=2)+'\n');print('Added Unit 8 form explanations; 17 pending coverage records; keys and Russian forms unchanged')
if __name__=='__main__':main()
