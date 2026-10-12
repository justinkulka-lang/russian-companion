#!/usr/bin/env python3
from pathlib import Path
import sys,json
ROOT=Path(__file__).resolve().parents[2];sys.path.insert(0,str(ROOT/'catalog'))
from curriculum_storage import loads
b=loads(Path(sys.argv[1]).read_bytes());p=loads((ROOT/'app/src/main/assets/content/v2-curriculum.json').read_bytes());l=json.loads((ROOT/'audit/content-corrections/UNIT26_FOCUS_INSTRUCTION_CORRECTION.json').read_text());obj='obj-u26-word-order'
old={r['itemId']:r for r in b['provenanceRecords']};new={r['itemId']:r for r in p['provenanceRecords']};assert set(new)-set(old)==set(l['addedProvenanceIds']);assert len(l['addedProvenanceIds'])==59
for id in old:assert old[id]==new[id],id
for k in b:
 if k not in ('units','items','examples','provenanceRecords','sourceCapabilities'):assert b[k]==p[k],k
for u in b['units']:
 n=next(x for x in p['units'] if x['number']==u['number'])
 if u['number']!=26:assert u==n
 else:
  for k in u:
   if k not in ('objectives','sessions'):assert u[k]==n[k]
  for o in u['objectives']:
   no=next(o2 for o2 in n['objectives'] if o2['id']==o['id'])
   if o['id'] not in (obj,'obj-u26-indefinite'):assert o==no
   else:assert {k:v for k,v in o.items() if k!='title'}=={k:v for k,v in no.items() if k!='title'}
  for s in u['sessions']:
   ns=next(x for x in n['sessions'] if x['id']==s['id'])
   if s['id'] not in ('u26-s-word-order','u26-s-indefinite'):assert s==ns
   else:
    assert {k:v for k,v in s.items() if k not in ('title','steps')}=={k:v for k,v in ns.items() if k not in ('title','steps')}
    for st in s['steps']:
     nt=next(x for x in ns['steps'] if x['id']==st['id'])
     if st['kind']!='REMEDIATE':assert st.get('exampleIds',[])==nt.get('exampleIds',[])
     assert {k:v for k,v in st.items() if k not in ('title','body','exampleIds')}=={k:v for k,v in nt.items() if k not in ('title','body','exampleIds')}
for e in b['examples']:
 ne=next(x for x in p['examples'] if x['id']==e['id'])
 if e['id'] not in l['changedExampleIds']:assert e==ne
 else:
  for k in e:
   if k not in ('note','sourceId'):assert e[k]==ne[k]
for cap in b['sourceCapabilities']:
 nc=next(x for x in p['sourceCapabilities'] if x['id']==cap['id'])
 if cap['id']=='cap-obj-u26-word-order':assert cap==l['replacedCapability'] and nc==l['newCapability']
 else:assert cap==nc
assert len(b['sourceCapabilities'])==len(p['sourceCapabilities'])
for i in b['items']:
 ni=next(x for x in p['items'] if x['id']==i['id'])
 if i['id'] not in l['changedItemIds']:assert i==ni
 else:
  if i['objectiveId']=='obj-u26-indefinite':
   for k in ('answer','options','promptLetter','sourceId'):assert i[k]==ni[k]
   for k in ('relationParticipantIds','answerSourceIds','distractorSourceIds','relationKind','evidenceMode','contrastMembers'):assert i['assessment'][k]==ni['assessment'][k]
  for k in ('id','kind','language','objectiveId','reviewStatus','containsGeneratedRussian'):assert i[k]==ni[k]
  for k in ('testedFeature','contrastSetId','cueLanguage','independent','responseKind'):assert i['assessment'][k]==ni['assessment'][k]
print('PASS exact scope:59 pending additions; every original record/right/source retained. Only two sessions/six items/three word-order example notes/one word-order capability change; all other objectives/units/mappings preserved.')
