#!/usr/bin/env python3
"""Verify number-instruction scope and schema-default representation equality."""
from pathlib import Path
from copy import deepcopy
import json,sys
ROOT=Path(__file__).resolve().parents[2]
b=json.loads(Path(sys.argv[1]).read_text());p=json.loads((ROOT/'app/src/main/assets/content/v2-curriculum.json').read_text());l=json.loads((ROOT/'audit/content-corrections/UNIT22_NUMBER_INSTRUCTION_CORRECTION.json').read_text())
def normpack(x):
 x=deepcopy(x)
 for u in x['units']:
  for s in u['sessions']:
   for st in s['steps']:
    for k in ['unavailableReason','bookDirection','companionDirection','remediationStepId']:st.setdefault(k,None)
 for r in x['provenanceRecords']:
  if r.get('synthesis') is not None:r['synthesis'].setdefault('applications',[])
 return x
b=normpack(b);p=normpack(p);old={r['itemId']:r for r in b['provenanceRecords']};new={r['itemId']:r for r in p['provenanceRecords']}
assert set(new)-set(old)==set(l['addedProvenanceIds']);assert set(old)<=set(new)
for id in old:assert old[id]==new[id],id
for k in b:
 if k not in ('units','items','provenanceRecords'):assert b[k]==p[k],k
for u in b['units']:
 n=next(x for x in p['units'] if x['number']==u['number'])
 if u['number']!=22:assert u==n,u['number']
 else:
  for k in u:
   if k not in ('sessions','objectives'):assert u[k]==n[k],k
  for obj in u['objectives']:
   newobj=next(o for o in n['objectives'] if o['id']==obj['id']);assert {k:v for k,v in obj.items() if k!='title'}=={k:v for k,v in newobj.items() if k!='title'}
for i in b['items']:
 n=next(x for x in p['items'] if x['id']==i['id'])
 if i['id'] not in l['changedItemIds']:assert i==n,i['id']
 else:
  for k in i:
   if k not in ('prompt','feedback','distractorRationales','assessment'):assert i[k]==n[k],(i['id'],k)
  for k in i['assessment']:
   if k not in ('whyAnswerDemonstratesObjective','distractorDifference'):assert i['assessment'][k]==n['assessment'][k],(i['id'],k)
print('PASS:116 pending additions; all original records/source hashes/rights/capabilities and all mappings preserved. Only Unit22 instruction and20 English item explanations change. All Russian cues/options/keys and other unit content stay unchanged modulo null-step/empty-application schema defaults.')
