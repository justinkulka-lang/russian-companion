#!/usr/bin/env python3
"""Remove contradictory current availability claims while preserving source/rights prose."""
import sys,json,hashlib,subprocess
from pathlib import Path
ROOT=Path(__file__).resolve().parents[2];sys.path.insert(0,str(ROOT/'catalog'))
from curriculum_storage import loads,dumps
PACK=ROOT/'app/src/main/assets/content/v2-curriculum.json'
CHANGES={
 'ref-u2125-attribution':('Completion over a period is not prepared. ',''),
 'ref-u2630-attribution':('Complete postal practice, participle stress, a general bookish-register claim, and dictionary use are not prepared.','Complete postal mastery, general participle-stress rules, universal register judgments, and broad dictionary proficiency are not supplied by these bounded tasks.')
}
def repair(p):
 changes=[]
 for id,(old,new) in CHANGES.items():
  r=next(r for r in p['references'] if r['id']==id)
  if old not in r['detail']:continue
  assert r['detail'].count(old)==1
  before=r['detail'];r['detail']=before.replace(old,new);changes.append(dict(referenceId=id,beforeDetail=before,afterDetail=r['detail']))
 return p,changes
if __name__=='__main__':
 raw=PACK.read_bytes();p,changes=repair(loads(raw))
 if not changes:print('Already repaired')
 else:
  assert len(changes)==2
  encoded=dumps(p).encode();PACK.write_bytes(encoded)
  ledger=dict(previousHead=subprocess.check_output(['git','rev-parse','HEAD'],text=True).strip(),finding='IA017',disposition='IMPLEMENTED_PENDING_INDEPENDENT_VERIFICATION',beforeSha256=hashlib.sha256(raw).hexdigest(),afterSha256=hashlib.sha256(encoded).hexdigest(),changes=changes,noNewRussianOrPrivateBookText=True,noSourceOrRightsRecordsChanged=True)
  (ROOT/'audit/content-corrections/IA017_REFERENCE_NOTICE_CORRECTION.json').write_text(json.dumps(ledger,ensure_ascii=False,indent=2)+'\n');print('Two current status clauses corrected; all source/rights prose preserved')
