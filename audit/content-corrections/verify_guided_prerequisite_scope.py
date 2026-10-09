#!/usr/bin/env python3
"""Verify an order-only correction against an exact previous curriculum snapshot."""
import argparse,hashlib,json
from copy import deepcopy
from pathlib import Path
HERE=Path(__file__).resolve().parent;ROOT=HERE.parents[1]
a=argparse.ArgumentParser();a.add_argument('--baseline',type=Path,required=True);a.add_argument('--baseline-manifests',type=Path,required=True);args=a.parse_args()
b=json.loads(args.baseline.read_bytes());raw=(ROOT/'app/src/main/assets/content/v2-curriculum.json').read_bytes();p=json.loads(raw)
e=json.loads((HERE/'GUIDED_PREREQUISITE_ORDER_CORRECTION.json').read_text())
assert hashlib.sha256(args.baseline.read_bytes()).hexdigest()==e['beforeCurriculumFileSha256']
assert hashlib.sha256(raw).hexdigest()==e['afterCurriculumFileSha256']
targets={r['sessionId']:r for r in e['rows']};changed=[]
assert b.keys()==p.keys()
for key in b:
 if key!='units':assert b[key]==p[key],key
assert len(b['units'])==len(p['units'])
for old,new in zip(b['units'],p['units']):
 assert {k:v for k,v in old.items() if k!='sessions'}=={k:v for k,v in new.items() if k!='sessions'}
 assert len(old['sessions'])==len(new['sessions'])
 for os,ns in zip(old['sessions'],new['sessions']):
  assert {k:v for k,v in os.items() if k!='steps'}=={k:v for k,v in ns.items() if k!='steps'}
  assert {s['id']:s for s in os['steps']}=={s['id']:s for s in ns['steps']},ns['id']
  if os!=ns:
   changed.append(ns['id']);row=targets[ns['id']]
   assert [s['id'] for s in os['steps']]==row['previousStepOrder']
   assert [s['id'] for s in ns['steps']]==row['correctedStepOrder']
   assert ns['steps']==[s for s in os['steps'] if s['kind']=='TEACH']+[s for s in os['steps'] if s['kind']!='TEACH']
assert set(changed)==set(targets),changed
old=json.loads(args.baseline_manifests.read_text())
new={str(n):json.loads((ROOT/f'review/unit-{n}/UNIT_{n}_REVIEW_MANIFEST.json').read_text()) for n in range(1,31)}
assert [n for n in old if old[n]['contentReviewDigest']!=new[n]['contentReviewDigest']]==['6','21','28']
assert all(old[n]['mappingPackageDigest']==new[n]['mappingPackageDigest'] for n in old)
assert all(new[n]['curriculumSourceCommit']=='8ff440be0691161ffaf053961d0fdfec1863856b' for n in new)
print('PASS: exactly four reviewed sessions change; step contents and IDs are byte-equivalent as parsed objects')
print('PASS: all other sessions, items, keys, options, examples, Russian cues, objectives, advancement and provenance remain unchanged')
print('PASS: only Units 6, 21 and 28 instructional digests change; all mapping-package digests remain unchanged')
print('PASS: all 30 pending review packets bind the actual implementation ancestor 8ff440be0691161ffaf053961d0fdfec1863856b')
print('curriculum SHA256='+hashlib.sha256(raw).hexdigest())
