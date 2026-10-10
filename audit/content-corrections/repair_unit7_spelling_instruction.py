#!/usr/bin/env python3
"""Explain the retained adjective contrasts without changing keys or Russian forms."""
import hashlib,json
from copy import deepcopy
from pathlib import Path
HERE=Path(__file__).resolve().parent;ROOT=HERE.parents[1];PACK=ROOT/'app/src/main/assets/content/v2-curriculum.json';OBJ='obj-u7-spelling'
def sha(s):return hashlib.sha256(s.encode()).hexdigest()
def main():
 raw=PACK.read_bytes();p=json.loads(raw);u=next(u for u in p['units'] if u['number']==7);s=next(s for s in u['sessions'] if s['id']=='u7-s-spelling')
 if any(r['itemId']=='prov-ca-u7-worked-teach-1-body' for r in p['provenanceRecords']):print('Already repaired; retained evidence unchanged');return
 template=next(r for r in p['provenanceRecords'] if r['itemId']=='prov-ca-7-489')
 bibliography=('Cornell Russian Language Program, Leed lessons 9-12, https://russian.cornell.edu/russian.web/courses/121/Leed_Less_9-12.pdf, printed pages 33 and 37 (PDF pages 3 and 7), text retrieved 2026-10-09: adjective nominative/accusative endings and agreement in gender, number and case. '
  'Gramota, Rules of Russian Orthography and Punctuation (1956), vowels after hushers, https://gramota.ru/biblioteka/spravochniki/pravila-russkoj-orfografii-i-punktuacii/glasnye-posle-shipyashchih: primary search excerpts retrieved 2026-10-09 for spelling with и after ш; direct page retrieval returned HTTP 403. Retained five-letter unstressed о/е condition remains unchanged. '
  'Retained OpenRussian commit 50e210c4803237779cb562bc1abcea529066031c, adjectives.csv:хороший#decl_m_nom, #decl_f_nom, #decl_n_nom and #decl_pl_nom; existing labels and phrases supplied by prov-ex-u7-spelling-0-ru, prov-ex-or-u7-spelling-1, prov-ex-or-u7-spelling-2, prov-ex-or-u7-spelling-3, prov-ex-or-u7-spelling-4, prov-ca-7-489, prov-ca-7-490, prov-ca-7-494 and prov-ca-7-495. '
  'Application inference: apply those rules to the retained forms; form-selection questions do not isolate spelling ability. Independently authored English on 2026-10-09 within content-audit-2026-10-08; no primary prose or new Russian form copied. Final semantic audit pending.')
 retained_ids=['prov-ex-u7-spelling-0-ru','prov-ex-or-u7-spelling-1','prov-ex-or-u7-spelling-2','prov-ex-or-u7-spelling-3','prov-ex-or-u7-spelling-4','prov-ca-7-489','prov-ca-7-490','prov-ca-7-494','prov-ca-7-495']
 assert all(any(r['itemId']==id for r in p['provenanceRecords']) for id in retained_ids)
 added=[]
 def record(text,name):
  r=deepcopy(template);original=bibliography+'\n'+text;r.update(itemId='prov-ca-u7-worked-'+name,recordType='label',sourceRecordId='ca-coverage:'+OBJ+':worked:'+name,originalSourceText=original,shippedText=text,sourceHash=sha(original),contentHash=sha(text))
  r['synthesis'].update(bibliography=bibliography,objectiveOrClaimBinding=OBJ+':worked:'+name,authorshipDeclaration='Independently authored English for Russian Companion on 2026-10-09, applying reference rules to previously retained Russian forms; no owned-book prose.',applications=[])
  assert not any(x['itemId']==r['itemId'] for x in p['provenanceRecords']);p['provenanceRecords'].append(r);added.append(r['itemId'])
 bodies={
  1:'An adjective agrees with its noun in gender, number and case. In хорошее письмо, the adjective describes a neuter singular noun. хоро́ший is the dictionary form: masculine singular nominative. The following forms belong to the same adjective meaning good.',
  2:'After ж, ц, ч, ш, or щ, an unstressed о in an ending is written е. That replacement is not a rule for every unstressed о. In хоро́шее, the unstressed ending after ш uses е. The и in хоро́шие follows a different spelling condition: write и rather than ы after ш. Spelling limits the letters in an ending; gender, number and case determine which form is needed.',
  3:'хоро́шая is feminine singular nominative; хоро́шее is neuter singular nominative. In хоро́шая книга, both words are feminine singular nominative. To put the phrase in the accusative singular, change both words: хоро́шая книга becomes хоро́шую книгу. Keep the same noun and feminine singular agreement. хоро́шего друга is masculine animate; хоро́шие книги is plural.',
  4:'хоро́шие is plural nominative. To change хоро́шее from neuter singular nominative to plural nominative, choose хоро́шие. To go back to neuter singular, choose хоро́шее. To supply the dictionary form, choose хоро́ший. These choices test the requested form of the adjective; all the options are already correctly spelled.'}
 titles={1:'One adjective, several forms',2:'Two spelling conditions',3:'Gender and a worked case change',4:'Singular, plural and dictionary form'}
 for n in bodies:
  st=next(st for st in s['steps'] if st['id']==f'u7-st-teach-spelling-{n}');st['body']=bodies[n];st['title']=titles[n];record(st['body'],f'teach-{n}-body');record(st['title'],f'teach-{n}-title')
 title='Recognize adjective forms after ш';s['title']=title;next(o for o in u['objectives'] if o['id']==OBJ)['title']=title;record(title,'objective-title')
 rem=next(st for st in s['steps'] if st['kind']=='REMEDIATE');rem['body']='\n\n'.join(bodies[n] for n in [1,2,3,4]);rem['title']='Review forms and spelling';rem['exampleIds']=list(next(o for o in u['objectives'] if o['id']==OBJ)['exampleIds']);record(rem['body'],'rem-body');record(rem['title'],'rem-title')
 for kind,text,name in [('GUIDED','Choose the requested adjective form. Use its gender, number and case, then read the explanation.','guided-body'),('CHECK','Choose the requested nominative form or feminine singular accusative phrase. A missed check returns to the explanation.','check-body')]:
  st=next(st for st in s['steps'] if st['kind']==kind);st['body']=text;record(text,name)
 for id in ['u7-g-spelling-0','u7-i-spelling-1','u7-i-spelling-2']:
  next(i for i in p['items'] if i['id']==id)['assessment']['testedFeature']='ADJECTIVE_NOMINATIVE_FORM'
 record('ADJECTIVE_NOMINATIVE_FORM','nominative-feature')
 i=next(i for i in p['items'] if i['id']=='u7-i-spelling-3');i['assessment']['testedFeature']='FEMININE_ADJECTIVE_ACCUSATIVE';record('FEMININE_ADJECTIVE_ACCUSATIVE','accusative-feature')
 i['prompt']='Which feminine singular accusative phrase corresponds to the shown nominative phrase?'
 i['feedback']='To put хоро́шая книга in the accusative singular, choose хоро́шую книгу. Both words change while the same noun and feminine singular agreement are kept.'
 i['assessment']['whyAnswerDemonstratesObjective']=i['feedback'];record(i['prompt'],'check-3-prompt');record(i['feedback'],'check-3-feedback')
 assert len(added)==17,added
 after=(json.dumps(p,ensure_ascii=False,indent=2)+'\n').encode();PACK.write_bytes(after)
 e=dict(schemaVersion=1,previousHead='6353c61bf3a228338dbe0898d48855dc411c52ad',unitNumber=7,objectiveId=OBJ,disposition='IMPLEMENTED_PENDING_FINAL_AUDIT',beforeCurriculumFileSha256=hashlib.sha256(raw).hexdigest(),afterCurriculumFileSha256=hashlib.sha256(after).hexdigest(),addedProvenanceIds=added,retainedRecordIds=retained_ids,changedItemIds=['u7-g-spelling-0','u7-i-spelling-1','u7-i-spelling-2','u7-i-spelling-3'],bibliography=bibliography,scope='Existing spelling-session instruction, titles, complete remediation; objective/session title; four tested-feature labels; third independent check English prompt/feedback/why only. Keys, Russian forms, source relations and advancement unchanged.')
 (HERE/'UNIT7_SPELLING_INSTRUCTION_CORRECTION.json').write_text(json.dumps(e,ensure_ascii=False,indent=2)+'\n');print('Added Unit 7 form explanations; 17 pending coverage records; keys and Russian forms unchanged')
if __name__=='__main__':main()
