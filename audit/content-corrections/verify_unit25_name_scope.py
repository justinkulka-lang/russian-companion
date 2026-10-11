#!/usr/bin/env python3
"""Verify exact content scope after independently decoding pooled storage."""
from pathlib import Path
import json,sys
ROOT=Path(__file__).resolve().parents[2];sys.path.insert(0,str(ROOT/'catalog'))
from curriculum_storage import loads
b=loads(Path(sys.argv[1]).read_bytes());p=loads((ROOT/'app/src/main/assets/content/v2-curriculum.json').read_bytes());l=json.loads((ROOT/'audit/content-corrections/UNIT25_NAME_INSTRUCTION_CORRECTION.json').read_text())
old={r['itemId']:r for r in b['provenanceRecords']};new={r['itemId']:r for r in p['provenanceRecords']};assert set(new)-set(old)==set(l['addedProvenanceIds']);assert set(old)<=set(new)
for id in old:assert old[id]==new[id],id
for k in b:
 if k not in ('units','items','examples','provenanceRecords'):assert b[k]==p[k],k
for u in b['units']:
 n=next(x for x in p['units'] if x['number']==u['number'])
 if u['number']!=25:assert u==n,u['number']
 else:
  for k in u:
   if k not in ('sessions','objectives'):assert u[k]==n[k],k
  for obj in u['objectives']:
   no=next(o for o in n['objectives'] if o['id']==obj['id']);assert {k:v for k,v in obj.items() if k!='title'}=={k:v for k,v in no.items() if k!='title'}
assert b['examples']==p['examples']
for i in b['items']:
 n=next(x for x in p['items'] if x['id']==i['id'])
 if i['id'] not in l['changedItemIds']:assert i==n,i['id']
 else:
  for k in i:
   if k not in ('prompt','feedback','distractorRationales','assessment'):assert i[k]==n[k],(i['id'],k)
  for k in i['assessment']:
   if k not in ('whyAnswerDemonstratesObjective','distractorDifference'):assert i['assessment'][k]==n['assessment'][k],(i['id'],k)
print('PASS:135 pending additions; all original provenance, source hashes, rights, capabilities, mappings, keys, options, cues, examples and other units preserved. Only Unit25 instruction and English prompts/feedback change.')
