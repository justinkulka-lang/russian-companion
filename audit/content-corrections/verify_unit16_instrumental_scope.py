#!/usr/bin/env python3
"""Require exact bounded changes against the retained pre-change curriculum."""
import argparse,hashlib,json,re
from pathlib import Path
HERE=Path(__file__).resolve().parent; ROOT=HERE.parents[1]
a=argparse.ArgumentParser();a.add_argument('--baseline',type=Path,required=True);args=a.parse_args()
braw=args.baseline.read_bytes();raw=(ROOT/'app/src/main/assets/content/v2-curriculum.json').read_bytes()
b=json.loads(braw);p=json.loads(raw);e=json.loads((HERE/'UNIT16_INSTRUMENTAL_INSTRUCTION_CORRECTION.json').read_text(encoding='utf-8'))
assert hashlib.sha256(braw).hexdigest()==e['beforeCurriculumFileSha256']
assert hashlib.sha256(raw).hexdigest()==e['afterCurriculumFileSha256']
for key in b:
 if key not in ('units','items','provenanceRecords'):assert b[key]==p[key],key
assert b['provenanceRecords']==p['provenanceRecords'][:len(b['provenanceRecords'])]
added=p['provenanceRecords'][len(b['provenanceRecords']):]
assert len(added)==87 and [r['itemId'] for r in added]==e['addedProvenanceIds']
for r in added:
 assert r['unitNumber']==16 and r['reviewStatus']=='IMPLEMENTED_PENDING_FINAL_AUDIT'
 assert all(not v for v in r['synthesis']['finalAudit'].values())
 assert r['sourceHash']==hashlib.sha256(r['originalSourceText'].encode()).hexdigest()
 assert r['contentHash']==hashlib.sha256(r['shippedText'].encode()).hexdigest()
slugs=['inst-sg','inst-pl','inst-adj']
for ub,up in zip(b['units'],p['units']):
 if ub['number']!=16:assert ub==up;continue
 assert {k:v for k,v in ub.items() if k not in ('sessions','objectives')}=={k:v for k,v in up.items() if k not in ('sessions','objectives')}
 for ob,op in zip(ub['objectives'],up['objectives']):
  assert {k:v for k,v in ob.items() if k!='title'}=={k:v for k,v in op.items() if k!='title'}
  if ob['id'] not in ['obj-u16-'+slug for slug in slugs]:assert ob==op
 for sb,sp in zip(ub['sessions'],up['sessions']):
  if sb['id'] not in ['u16-s-'+slug for slug in slugs]:assert sb==sp;continue
  assert {k:v for k,v in sb.items() if k not in ('steps','title')}=={k:v for k,v in sp.items() if k not in ('steps','title')}
  assert set(st['id'] for st in sb['steps'])==set(st['id'] for st in sp['steps'])
  if sb['id']!='u16-s-inst-pl':assert [st['id'] for st in sb['steps']]==[st['id'] for st in sp['steps']]
  else:assert [st['id'] for st in sp['steps']]==[st['id'] for st in sb['steps'] if st['kind']=='TEACH']+[st['id'] for st in sb['steps'] if st['kind']!='TEACH']
  for st in sb['steps']:
   nt=next(st2 for st2 in sp['steps'] if st2['id']==st['id'])
   fields={'body','title'}|({'exampleIds'} if st['kind']=='REMEDIATE' else set())
   assert {k:v for k,v in st.items() if k not in fields}=={k:v for k,v in nt.items() if k not in fields}
changed=[]
for ib,ip in zip(b['items'],p['items']):
 if ib==ip:continue
 changed.append(ip['id']);assert ip['id'] in e['changedItemIds']
 fields={'prompt','feedback','distractorRationales','assessment'}
 assert {k:v for k,v in ib.items() if k not in fields}=={k:v for k,v in ip.items() if k not in fields}
 fields={'distractorDifference','whyAnswerDemonstratesObjective'}
 assert {k:v for k,v in ib['assessment'].items() if k not in fields}=={k:v for k,v in ip['assessment'].items() if k not in fields}
assert changed==e['changedItemIds']
def tokens(data):return set(re.findall('[\u0400-\u04ff][\u0400-\u04ff\u0301]*',json.dumps(data,ensure_ascii=False)))
assert not tokens(p)-tokens(b),'New Russian tokens'
print('PASS: only three Unit 16 form lessons, their objective titles, complete remediation and nine item English fields change')
print('PASS: plural teaching precedes guided practice; other step order, all identities, keys, options, cues, examples, source relations and advancement preserved')
print('PASS: all other units and original source/rights/provenance records unchanged; exactly 87 pending evidence records; no new Russian token')
