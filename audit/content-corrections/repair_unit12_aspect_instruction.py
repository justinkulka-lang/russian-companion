#!/usr/bin/env python3
"""Explain the retained adjective contrasts without changing keys or Russian forms."""
import hashlib,json
from copy import deepcopy
from pathlib import Path
HERE=Path(__file__).resolve().parent;ROOT=HERE.parents[1];PACK=ROOT/'app/src/main/assets/content/v2-curriculum.json';OBJ='obj-u12-aspect'
def sha(s):return hashlib.sha256(s.encode()).hexdigest()
def main():
 raw=PACK.read_bytes();p=json.loads(raw);u=next(u for u in p['units'] if u['number']==12);s=next(s for s in u['sessions'] if s['id']=='u12-s-aspect')
 if any(r['itemId']=='prov-ca-u12-worked-teach-1-body' for r in p['provenanceRecords']):print('Already repaired; retained evidence unchanged');return
 template=next(r for r in p['provenanceRecords'] if r['itemId']=='prov-ca-12-371')
 bibliography=('Cornell Russian Language Program: Verbs, Aspect, https://russian.cornell.edu/grammar/html/gr04_d.htm; Aspect partners, https://russian.cornell.edu/grammar/html/gr08_b.htm. University of Kansas, Mezhdu nami units 6.1 and 6.2, https://mezhdunami.ku.edu/unit06/6_1/language.shtml and https://mezhdunami.ku.edu/unit06/6_2/language.shtml: aspect meanings, prefixed pairs and different-root pairs. Primary HTML retrieved 2026-10-10 UTC. '
  'Existing Wikibooks Russian/Verbal_Aspect oldid 4059032 supplies retained pair forms: prov-ex-u12-aspect-0-ru, prov-ex-u12-aspect-1-ru, prov-ex-u12-aspect-2-ru and prov-ex-u12-aspect-3-ru. Existing limitation record: prov-ca-12-371. '
  'Application inference: explain the retained saying/telling and doing/making counterpart checks; pair recognition does not establish sentence-context aspect selection. Independently authored English on 2026-10-10 UTC within content-audit-2026-10-08; no reference prose, exercise or new Russian form copied. Final semantic audit pending.')
 retained_ids=[f'prov-ex-u12-aspect-{n}-ru' for n in range(4)]+['prov-ca-12-371']
 assert all(any(r['itemId']==id for r in p['provenanceRecords']) for id in retained_ids)
 added=[]
 def record(text,name):
  r=deepcopy(template);original=bibliography+'\n'+text;r.update(itemId='prov-ca-u12-worked-'+name,recordType='label',sourceRecordId='ca-coverage:'+OBJ+':worked:'+name,originalSourceText=original,shippedText=text,sourceHash=sha(original),contentHash=sha(text))
  r['synthesis'].update(bibliography=bibliography,objectiveOrClaimBinding=OBJ+':worked:'+name,authorshipDeclaration='Independently authored English for Russian Companion on 2026-10-10 UTC, applying reference rules to previously retained Russian forms; no owned-book prose.',applications=[])
  assert not any(x['itemId']==r['itemId'] for x in p['provenanceRecords']);p['provenanceRecords'].append(r);added.append(r['itemId'])
 bodies={
  1:'Aspect describes how an action is viewed, not when it happens. Imperfective can focus on an activity, repetition, or the fact that an action occurred without emphasizing its endpoint. Perfective presents an event with its boundary or result. Imperfective does not by itself mean unfinished. говори́ть is imperfective; сказа́ть is perfective. These infinitives do not specify tense on their own.',
  2:'Many Russian verbs are learned in an aspect pair, but not every verb has two words, and a pair is not automatic. говорить and сказать are context-dependent counterparts, not a universal pair for the English verb speak. For the saying/telling pair shown here, the perfective counterpart of говори́ть is сказа́ть; the imperfective counterpart of сказа́ть is говори́ть. These checks ask you to recognize this vocabulary pair, not choose aspect in a sentence.',
  3:'де́лать is imperfective; сде́лать is perfective. Both concern doing or making. The perfective focuses on accomplishing the action; the imperfective can describe the activity or its occurrence without that focus. Given де́лать and a request for its perfective counterpart, choose сде́лать. говори́ть concerns speaking or saying and belongs to the other pair. Learn the shown pair rather than assuming every prefix changes only aspect.'}
 titles={1:'Aspect and time',2:'The saying and telling pair',3:'A worked doing and making pair'}
 for n in bodies:
  st=next(st for st in s['steps'] if st['id']==f'u12-st-teach-aspect-{n}');st['body']=bodies[n];st['title']=titles[n];record(st['body'],f'teach-{n}-body');record(st['title'],f'teach-{n}-title')
 title='Recognize the two taught aspect pairs';s['title']=title;next(o for o in u['objectives'] if o['id']==OBJ)['title']=title;record(title,'objective-title')
 rem=next(st for st in s['steps'] if st['kind']=='REMEDIATE');rem['body']='\n\n'.join(bodies[n] for n in [1,2,3]);rem['title']='Review the two aspect pairs';rem['exampleIds']=list(next(o for o in u['objectives'] if o['id']==OBJ)['exampleIds']);record(rem['body'],'rem-body');record(rem['title'],'rem-title')
 for kind,text,name in [('GUIDED','Choose the counterpart from the saying/telling pair. Then compare its aspect and meaning with the other option.','guided-body'),('CHECK','Recognize the two taught aspect pairs. A missed check returns to the explanations and examples.','check-body')]:
  st=next(st for st in s['steps'] if st['kind']==kind);st['body']=text;record(text,name)
 descriptions={
  'u12-g-aspect-0':('For the saying/telling pair shown here, which verb is the perfective partner?', 'сказа́ть is the perfective partner of говори́ть in the saying/telling pair shown here.', 'де́лать is imperfective and concerns doing/making, not this saying/telling partner.'),
  'u12-i-aspect-1':('For the saying/telling pair shown here, which verb is the imperfective partner?', 'говори́ть is the imperfective partner of сказа́ть in the saying/telling pair shown here.', 'сде́лать is perfective and concerns doing/making, not this saying/telling partner.'),
  'u12-i-aspect-2':('For the doing/making pair shown here, which verb is the perfective partner?', 'сде́лать is the perfective partner of де́лать in the doing/making pair shown here.', 'говори́ть is imperfective and concerns speaking/saying, not this doing/making partner.')}
 for id,(prompt,feedback,rationale) in descriptions.items():
  i=next(i for i in p['items'] if i['id']==id);i['prompt']=prompt;i['feedback']=feedback;i['distractorRationales']=['',rationale];i['assessment']['whyAnswerDemonstratesObjective']=feedback;i['assessment']['distractorDifference']=i['distractorRationales'].copy()
  for text,label in [(prompt,'prompt'),(feedback,'feedback'),(rationale,'distractor')]:record(text,id+'-'+label)
 assert len(added)==20,added
 after=(json.dumps(p,ensure_ascii=False,indent=1)+'\n').encode();PACK.write_bytes(after)
 e=dict(schemaVersion=1,previousHead='41e0f57429a38766ffb7030e67f071acbf929faf',unitNumber=12,objectiveId=OBJ,disposition='IMPLEMENTED_PENDING_FINAL_AUDIT',beforeCurriculumFileSha256=hashlib.sha256(raw).hexdigest(),afterCurriculumFileSha256=hashlib.sha256(after).hexdigest(),addedProvenanceIds=added,retainedRecordIds=retained_ids,changedItemIds=list(descriptions),bibliography=bibliography,scope='Existing aspect-session instruction, titles, complete remediation; objective/session title; three items English prompt, feedback and rationale fields only. Keys, Russian forms, source relations and advancement unchanged.')
 (HERE/'UNIT12_ASPECT_INSTRUCTION_CORRECTION.json').write_text(json.dumps(e,ensure_ascii=False,indent=2)+'\n');print('Added Unit 12 aspect explanations; 20 pending coverage records; keys and Russian forms unchanged')
if __name__=='__main__':main()
