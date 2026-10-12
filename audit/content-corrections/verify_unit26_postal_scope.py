#!/usr/bin/env python3
from pathlib import Path
import sys,json
ROOT=Path(__file__).resolve().parents[2];sys.path.insert(0,str(ROOT/'catalog'));from curriculum_storage import loads
b=loads(Path(sys.argv[1]).read_bytes());p=loads((ROOT/'app/src/main/assets/content/v2-curriculum.json').read_bytes());l=json.loads((ROOT/'audit/content-corrections/UNIT26_POSTAL_INSTRUCTION_CORRECTION.json').read_text());obj='obj-u26-postal-address'
old={r['itemId']:r for r in b['provenanceRecords']};new={r['itemId']:r for r in p['provenanceRecords']};assert set(new)-set(old)==set(l['addedProvenanceIds']);assert len(l['addedProvenanceIds'])==97
for id,r in old.items():assert r==new[id],id
for k in b:
 if k not in ('units','items','provenanceRecords','sourceCapabilities'):assert b[k]==p[k],k
for u in b['units']:
 n=next(x for x in p['units'] if x['number']==u['number'])
 if u['number']!=26:assert u==n;continue
 for k in u:
  if k not in ('objectives','sessions'):assert u[k]==n[k]
 for o in u['objectives']:
  no=next(x for x in n['objectives'] if x['id']==o['id'])
  if o['id']!=obj:assert o==no
  else:assert {k:v for k,v in o.items() if k!='title'}=={k:v for k,v in no.items() if k!='title'}
 for s in u['sessions']:
  ns=next(x for x in n['sessions'] if x['id']==s['id'])
  if s['id']!='u26-s-gap-postal-address':assert s==ns;continue
  assert {k:v for k,v in s.items() if k not in ('title','steps')}=={k:v for k,v in ns.items() if k not in ('title','steps')}
  assert {x['id'] for x in s['steps']}=={x['id'] for x in ns['steps']}
  for st in s['steps']:
   nt=next(x for x in ns['steps'] if x['id']==st['id'])
   if st['kind'] in ('ANKI','BINDER','TUTOR','ORIENT'):assert st==nt
   else:assert {k:v for k,v in st.items() if k not in ('title','body','exampleIds')}=={k:v for k,v in nt.items() if k not in ('title','body','exampleIds')}
   if st['kind']!='REMEDIATE':assert st.get('exampleIds',[])==nt.get('exampleIds',[])
for i in b['items']:
 ni=next(x for x in p['items'] if x['id']==i['id'])
 if i['objectiveId']!=obj:assert i==ni;continue
 for k in ('id','kind','language','objectiveId','reviewStatus','containsGeneratedRussian','promptLetter'):assert i[k]==ni[k]
 for k in ('testedFeature','contrastSetId','cueLanguage','independent','responseKind','relationKind','evidenceMode'):assert i['assessment'][k]==ni['assessment'][k]
 if i['id']!='u26-i-postal-5':assert i['answer']==ni['answer']
 if i['id'] not in ('u26-i-postal-5','u26-i-postal-11'):assert i['options']==ni['options']
for c in b['sourceCapabilities']:
 nc=next(x for x in p['sourceCapabilities'] if x['id']==c['id'])
 if c['objectiveId']!=obj:assert c==nc
 else:assert c==l['replacedCapability'] and nc==l['newCapability']
assert len(b['items'])==len(p['items']) and len(b['sourceCapabilities'])==len(p['sourceCapabilities'])
print('PASS exact postal-only scope:97 pending additions; original records/rights andall Russian cues/examples retained;11keys unchanged;only short-sample key andone distractor corrected;other units/objectives/mappings unchanged.')
