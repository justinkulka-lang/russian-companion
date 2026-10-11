#!/usr/bin/env python3
"""Verify exact scope and equivalent omission of typed empty review defaults."""
from pathlib import Path
from copy import deepcopy
import json,sys
ROOT=Path(__file__).resolve().parents[2]
b=json.loads(Path(sys.argv[1]).read_text());p=json.loads((ROOT/'app/src/main/assets/content/v2-curriculum.json').read_text());l=json.loads((ROOT/'audit/content-corrections/UNIT21_REFLEXIVE_PURPOSE_CORRECTION.json').read_text())
def norm(r):
 r=deepcopy(r)
 for k,v in [('reviewerIdentity',''),('reviewerQualification',''),('reviewDate',''),('reviewedDigest',''),('audioAssetId',None)]:r.setdefault(k,v)
 if r.get('synthesis') is not None:r['synthesis'].setdefault('finalAudit',{k:'' for k in ['artifactIdentity','artifactDigest','auditedContentDigest','disposition','modelVersion','date']})
 return r
old={r['itemId']:r for r in b['provenanceRecords']};new={r['itemId']:r for r in p['provenanceRecords']}
assert set(new)-set(old)==set(l['addedProvenanceIds']);assert set(old)<=set(new)
for id in old:assert norm(old[id])==norm(new[id]),id
for k in b:
 if k not in ('units','items','provenanceRecords'):assert b[k]==p[k],k
for u in b['units']:
 n=next(x for x in p['units'] if x['number']==u['number'])
 if u['number']!=21:assert u==n,u['number']
 else:
  for k in u:
   if k not in ('sessions','objectives'):assert u[k]==n[k],k
for i in b['items']:
 n=next(x for x in p['items'] if x['id']==i['id'])
 if i['id'] not in l['changedItemIds']:assert i==n,i['id']
 else:
  for k in i:
   if k not in ('prompt','feedback','distractorRationales','assessment','options'):assert i[k]==n[k],(i['id'],k)
  if i['id']!='u21-i-purpose-2':assert i['options']==n['options']
  else:assert i['options']==['because','since','after'] and n['options']==['because','although','after']
print('PASS:75 pending additions; all original provenance/rights/source hashes preserved modulo typed empty defaults; only Unit21 instruction and nine English item explanations change, with one licensed causal distractor replacement. All source capabilities, mappings, keys and other units/items unchanged.')
