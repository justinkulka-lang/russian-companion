#!/usr/bin/env python3
from pathlib import Path
import sys,json
ROOT=Path(__file__).resolve().parents[2];sys.path.insert(0,str(ROOT/'catalog'))
from curriculum_storage import loads
b=loads(Path(sys.argv[1]).read_bytes());p=loads((ROOT/'app/src/main/assets/content/v2-curriculum.json').read_bytes());l=json.loads((ROOT/'audit/content-corrections/UNIT26_LETTER_INSTRUCTION_CORRECTION.json').read_text());obj='obj-u26-letter-writing'
old={r['itemId']:r for r in b['provenanceRecords']};new={r['itemId']:r for r in p['provenanceRecords']};assert set(new)-set(old)==set(l['addedProvenanceIds']);assert len(l['addedProvenanceIds'])==31
for id in old:assert old[id]==new[id],id
for k in b:
 if k not in ('units','items','provenanceRecords','sourceCapabilities'):assert b[k]==p[k],k
for u in b['units']:
 n=next(x for x in p['units'] if x['number']==u['number'])
 if u['number']!=26:assert u==n
 else:
  for k in u:
   if k not in ('objectives','sessions'):assert u[k]==n[k]
  for o in u['objectives']:
   no=next(o2 for o2 in n['objectives'] if o2['id']==o['id'])
   if o['id']!=obj:assert o==no
   else:assert {k:v for k,v in o.items() if k!='title'}=={k:v for k,v in no.items() if k!='title'}
  for s in u['sessions']:
   ns=next(x for x in n['sessions'] if x['id']==s['id'])
   if s['id']!='u26-s-letter-writing':assert s==ns
   else:
    assert {k:v for k,v in s.items() if k not in ('title','steps')}=={k:v for k,v in ns.items() if k not in ('title','steps')}
    for st in s['steps']:
     nt=next(x for x in ns['steps'] if x['id']==st['id'])
     assert {k:v for k,v in st.items() if k not in ('title','body')}=={k:v for k,v in nt.items() if k not in ('title','body')}
for cap in b['sourceCapabilities']:
 nc=next(x for x in p['sourceCapabilities'] if x['id']==cap['id'])
 if cap['id']=='cap-obj-u26-letter-writing':assert cap==l['replacedCapability'] and nc==l['newCapability']
 else:assert cap==nc
assert len(b['sourceCapabilities'])==len(p['sourceCapabilities'])
for i in b['items']:
 ni=next(x for x in p['items'] if x['id']==i['id'])
 if i['id'] not in l['changedItemIds']:assert i==ni
 else:
  for k in ('id','kind','language','objectiveId','reviewStatus','containsGeneratedRussian'):assert i[k]==ni[k]
  for k in ('testedFeature','contrastSetId','cueLanguage','independent','responseKind'):assert i['assessment'][k]==ni['assessment'][k]
print('PASS exact scope:31 pending additions; all original provenance/examples/other units and objectives/mappings retained; only three letter items, complete letter instruction and one pending source capability change.')
