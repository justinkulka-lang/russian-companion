#!/usr/bin/env python3
"""Verify historical preservation and narrow Unit27 changes against a supplied baseline."""
import json,sys
from pathlib import Path
ROOT=Path(__file__).resolve().parents[2];sys.path.insert(0,str(ROOT/'catalog'))
from curriculum_storage import loads
before=loads(Path(sys.argv[1]).read_bytes());after=loads((ROOT/'app/src/main/assets/content/v2-curriculum.json').read_bytes())
ledger=json.loads((ROOT/'audit/content-corrections/UNIT27_REMAINING_INSTRUCTION_CORRECTION.json').read_text())
old={r['itemId']:r for r in before['provenanceRecords']};new={r['itemId']:r for r in after['provenanceRecords']}
assert all(new[k]==v for k,v in old.items())
assert set(new)-set(old)==set(ledger['addedProvenanceIds']);assert len(set(new)-set(old))==73
for key in before:
 if key not in ('units','items','provenanceRecords','sourceCapabilities'):assert before[key]==after[key],key
for u,n in zip(before['units'],after['units']):
 if u['number']!=27:assert u==n,u['number'];continue
 for key in u:
  if key not in ('sessions','objectives'):assert u[key]==n[key],key
 for o,v in zip(u['objectives'],n['objectives']):
  assert {k:v for k,v in o.items() if k!='title'}=={k:v for k,v in v.items() if k!='title'}
 for s,v in zip(u['sessions'],n['sessions']):
  if s['objectiveIds'] not in [[obj] for obj in ledger['sessionObjectives']]:assert s==v
  else:
   assert {k:v for k,v in s.items() if k not in ('title','steps')}=={k:v for k,v in v.items() if k not in ('title','steps')}
   assert {t['id'] for t in s['steps']}=={t['id'] for t in v['steps']}
changed=[]
for i,n in zip(before['items'],after['items']):
 if i==n:continue
 changed.append(i['id'])
 for key in ('id','kind','language','promptLetter','options','answer','sourceId','reviewStatus','containsGeneratedRussian','objectiveId'):assert i[key]==n[key],(i['id'],key)
 for key in ('testedFeature','contrastSetId','cueLanguage','independent','responseKind','relationKind','evidenceMode'):assert i['assessment'][key]==n['assessment'][key]
assert set(changed)==set(ledger['changedItemIds'])
for c,n in zip(before['sourceCapabilities'],after['sourceCapabilities']):
 if c['id']!='cap-obj-u27-comprehension':assert c==n;continue
 assert c==ledger['previousCapability'];assert c['exactSpan'] in n['exactSpan']
 assert {k:v for k,v in c.items() if k not in ('exactSpan','sourceHash')}=={k:v for k,v in n.items() if k not in ('exactSpan','sourceHash')}
print('PASS: original provenance/Russian/options/keys/mappings/rights/other units preserved; one bounded pending capability corpus refreshed.')
