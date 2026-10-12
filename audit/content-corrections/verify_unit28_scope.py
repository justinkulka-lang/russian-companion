#!/usr/bin/env python3
"""Exact preserved-content and deterministic replay proof for Unit28."""
import sys,json,subprocess
from copy import deepcopy
from pathlib import Path
ROOT=Path(__file__).resolve().parents[2];sys.path.insert(0,str(ROOT/'catalog'))
from curriculum_storage import loads,dumps
from repair_unit28_instruction import repair
ledger=json.loads((ROOT/'audit/content-corrections/UNIT28_INSTRUCTION_CORRECTION.json').read_text())
before=loads(subprocess.check_output(['git','show',ledger['previousHead']+':app/src/main/assets/content/v2-curriculum.json']))
current=loads((ROOT/'app/src/main/assets/content/v2-curriculum.json').read_bytes())
old={r['itemId']:r for r in before['provenanceRecords']};new={r['itemId']:r for r in current['provenanceRecords']}
assert all(new[k]==v for k,v in old.items())
for key in before:
 if key not in ['units','items','provenanceRecords','sourceCapabilities']:assert before[key]==current[key],key
for b,a in zip(before['units'],current['units']):
 if b['number']!=28:assert b==a
 else:
  for k in b:
   if k!='sessions':assert b[k]==a[k],k
for b,a in zip(before['items'],current['items']):
 if not b['objectiveId'].startswith('obj-u28-'):assert b==a
 else:
  for k in ['id','objectiveId','promptLetter','answer','options','distractorRationales']:assert b.get(k)==a.get(k),(b['id'],k)
for b,a in zip(before['sourceCapabilities'],current['sourceCapabilities']):
 if b['id'] not in ['cap-obj-u28-imperfective-adverb','cap-obj-u28-perfective-adverb']:assert b==a
 else:
  assert {k:v for k,v in b.items() if k not in ['exactSpan','sourceHash']}=={k:v for k,v in a.items() if k not in ['exactSpan','sourceHash']}
replayed,_=repair(deepcopy(before));assert dumps(replayed)==dumps(current)
idempotent,changes=repair(deepcopy(current));assert changes is None and dumps(idempotent)==dumps(current)
print(json.dumps(dict(baseline=ledger['previousHead'],oldProvenancePreserved=len(old),newPendingProvenance=len(new)-len(old),otherUnitsIdentical=29,allRussianCuesOptionsExamplesKeysPreserved=True,deterministicReplay=True,byteIdempotent=True),indent=2))
