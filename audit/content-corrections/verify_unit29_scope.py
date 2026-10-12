#!/usr/bin/env python3
import sys,json,subprocess
from pathlib import Path
from copy import deepcopy
ROOT=Path(__file__).resolve().parents[2];sys.path.insert(0,str(ROOT/'catalog'))
from curriculum_storage import loads,dumps
from repair_unit29_instruction import repair
ledger=json.loads((ROOT/'audit/content-corrections/UNIT29_INSTRUCTION_CORRECTION.json').read_text());b=loads(subprocess.check_output(['git','show',ledger['previousHead']+':app/src/main/assets/content/v2-curriculum.json']));a=loads((ROOT/'app/src/main/assets/content/v2-curriculum.json').read_bytes())
for key in b:
 if key not in ['units','provenanceRecords']:assert b[key]==a[key],key
for u,v in zip(b['units'],a['units']):
 if u['number']!=29:assert u==v
 else:
  for k in u:
   if k!='sessions':assert u[k]==v[k],k
old={r['itemId']:r for r in b['provenanceRecords']};new={r['itemId']:r for r in a['provenanceRecords']};assert all(new[k]==v for k,v in old.items())
r,_=repair(deepcopy(b));assert dumps(r)==dumps(a);r,c=repair(deepcopy(a));assert c is None and dumps(r)==dumps(a)
print(json.dumps(dict(baseline=ledger['previousHead'],oldProvenancePreserved=len(old),newPendingRecords=len(new)-len(old),otherUnitsIdentical=29,allAssessmentsExamplesSourcesCapabilitiesMappingsUnchanged=True,deterministicReplay=True,byteIdempotent=True),indent=2))
