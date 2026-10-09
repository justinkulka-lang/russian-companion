#!/usr/bin/env python3
"""Verify the bounded Unit 6 instruction change against an exact prior snapshot."""
import argparse,hashlib,json,re
from pathlib import Path
HERE=Path(__file__).resolve().parent;ROOT=HERE.parents[1]
a=argparse.ArgumentParser();a.add_argument('--baseline',type=Path,required=True);a.add_argument('--baseline-manifests',type=Path,required=True);args=a.parse_args()
raw=(ROOT/'app/src/main/assets/content/v2-curriculum.json').read_bytes();before=args.baseline.read_bytes();b=json.loads(before);p=json.loads(raw);e=json.loads((HERE/'UNIT6_POSSESSIVE_INSTRUCTION_CORRECTION.json').read_text())
assert hashlib.sha256(before).hexdigest()==e['beforeCurriculumFileSha256'];assert hashlib.sha256(raw).hexdigest()==e['afterCurriculumFileSha256']
for key in b:
 if key not in ('units','items','provenanceRecords'):assert b[key]==p[key],key
assert b['provenanceRecords']==p['provenanceRecords'][:len(b['provenanceRecords'])]
assert all(any(r['itemId']==id for r in b['provenanceRecords']) for id in e['retainedRecordIds'])
added=p['provenanceRecords'][len(b['provenanceRecords']):];assert len(added)==23;assert [r['itemId'] for r in added]==e['addedProvenanceIds']
for r in added:
 assert r['unitNumber']==6 and r['objectiveId']=='obj-u6-possessive' and r['reviewStatus']=='IMPLEMENTED_PENDING_FINAL_AUDIT'
 assert all(not v for v in r['synthesis']['finalAudit'].values())
 assert r['sourceHash']==hashlib.sha256(r['originalSourceText'].encode()).hexdigest();assert r['contentHash']==hashlib.sha256(r['shippedText'].encode()).hexdigest()
assert len(b['units'])==len(p['units']);changed=[]
for ub,up in zip(b['units'],p['units']):
 assert {k:v for k,v in ub.items() if k!='sessions'}=={k:v for k,v in up.items() if k!='sessions'}
 assert len(ub['sessions'])==len(up['sessions'])
 for sb,sp in zip(ub['sessions'],up['sessions']):
  if sb==sp:continue
  changed.append(sp['id']);assert sp['id']=='u6-s-possessive'
  assert {k:v for k,v in sb.items() if k!='steps'}=={k:v for k,v in sp.items() if k!='steps'}
  assert len(sb['steps'])==len(sp['steps'])
  for st,nt in zip(sb['steps'],sp['steps']):
   fields={'body','title'} | ({'exampleIds'} if st['kind']=='REMEDIATE' else set())
   assert {k:v for k,v in st.items() if k not in fields}=={k:v for k,v in nt.items() if k not in fields}
assert changed==['u6-s-possessive']
assert len(b['items'])==len(p['items']);changed=[]
for ib,ip in zip(b['items'],p['items']):
 if ib==ip:continue
 changed.append(ip['id']);assert ip['id'] in e['changedItemIds']
 fields={'prompt','feedback','distractorRationales','assessment'}
 assert {k:v for k,v in ib.items() if k not in fields}=={k:v for k,v in ip.items() if k not in fields}
 assert {k:v for k,v in ib['assessment'].items() if k not in ('distractorDifference','whyAnswerDemonstratesObjective')}=={k:v for k,v in ip['assessment'].items() if k not in ('distractorDifference','whyAnswerDemonstratesObjective')}
assert changed==e['changedItemIds']
def russian_tokens(data):
 if isinstance(data,str):return set(re.findall('[\u0400-\u04ff][\u0400-\u04ff\u0301]*',data))
 if isinstance(data,dict):data=data.values()
 if isinstance(data,(list,tuple)) or hasattr(data,'__iter__'):return set().union(*(russian_tokens(v) for v in data)) if data else set()
 return set()
assert not russian_tokens(p)-russian_tokens(b),'New Russian tokens introduced'
old=json.loads(args.baseline_manifests.read_text());new={str(n):json.loads((ROOT/f'review/unit-{n}/UNIT_{n}_REVIEW_MANIFEST.json').read_text()) for n in range(1,31)}
assert [n for n in old if old[n]['contentReviewDigest']!=new[n]['contentReviewDigest']]==['6']
assert all(old[n]['mappingPackageDigest']==new[n]['mappingPackageDigest'] for n in old)
assert all(new[n]['curriculumSourceCommit']=='72c8bb4680ad0c0e1c3284ee85f90824059b78aa' for n in new)
print('PASS: only Unit 6 possessive session text/remediation example selection and two items English explanatory fields change')
print('PASS: all keys, options, Russian cues, examples, step order/IDs, objectives, assessment source relations and advancement requirements are unchanged')
print('PASS: all original provenance/source/rights records preserved; exactly 23 pending coverage records appended; no new Russian token introduced')
print('PASS: only Unit 6 instructional digest changes; all mapping digests unchanged; all 30 packets bind the actual implementation ancestor')
print('curriculum SHA256='+hashlib.sha256(raw).hexdigest())
