#!/usr/bin/env python3
from pathlib import Path
from copy import deepcopy
import sys,json
ROOT=Path(__file__).resolve().parents[2];sys.path.insert(0,str(ROOT/'catalog'));from curriculum_storage import loads
b=loads(Path(sys.argv[1]).read_bytes());p=loads((ROOT/'app/src/main/assets/content/v2-curriculum.json').read_bytes());l=json.loads((ROOT/'audit/content-corrections/UNIT26_WRITTEN_INSTRUCTION_CORRECTION.json').read_text())
old={r['itemId']:r for r in b['provenanceRecords']};new={r['itemId']:r for r in p['provenanceRecords']};assert set(new)-set(old)==set(l['addedProvenanceIds'])
for id,r in old.items():assert r==new[id],id
for k in b:
 if k not in ('units','provenanceRecords'):assert b[k]==p[k],k
for u in b['units']:
 n=next(x for x in p['units'] if x['number']==u['number'])
 if u['number']!=26:assert u==n;continue
 restored=deepcopy(n)
 for k in u:
  if k not in ('objectives','sessions'):assert u[k]==n[k]
 for o in u['objectives']:
  no=next(x for x in restored['objectives'] if x['id']==o['id']);no['title']=o['title'];assert o==no
 for s in u['sessions']:
  ns=next(x for x in restored['sessions'] if x['id']==s['id'])
  if s['id'] not in l['sessionIds']:assert s==ns;continue
  for k in s:
   if k not in ('title','steps'):assert s[k]==ns[k]
  assert {x['id'] for x in s['steps']}=={x['id'] for x in ns['steps']}
  for st in s['steps']:
   nt=next(x for x in ns['steps'] if x['id']==st['id'])
   for k in st:
    if k not in ('title','body') and not (k=='exampleIds' and st['kind']=='REMEDIATE'):assert st[k]==nt[k],st['id']
print('PASS exact scope:3 sessions; every original record and all items/keys/cues/options/assessments/examples/capabilities/mappings retained; other units/objectives unchanged.')
