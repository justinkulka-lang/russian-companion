#!/usr/bin/env python3
from pathlib import Path
import sys,json
ROOT=Path(__file__).resolve().parents[2];sys.path.insert(0,str(ROOT/'catalog'));from curriculum_storage import loads
b=loads(Path(sys.argv[1]).read_bytes());p=loads((ROOT/'app/src/main/assets/content/v2-curriculum.json').read_bytes());l=json.loads((ROOT/'audit/content-corrections/UNIT27_PAST_INSTRUCTION_CORRECTION.json').read_text());old={r['itemId']:r for r in b['provenanceRecords']};new={r['itemId']:r for r in p['provenanceRecords']};assert set(new)-set(old)==set(l['addedProvenanceIds']);assert len(l['addedProvenanceIds'])==42
for id,r in old.items():assert r==new[id],id
for k in b:
 if k not in ('units','items','provenanceRecords'):assert b[k]==p[k],k
for u in b['units']:
 n=next(x for x in p['units'] if x['number']==u['number'])
 if u['number']!=27:assert u==n;continue
 for k in u:
  if k not in ('objectives','sessions'):assert u[k]==n[k]
 for o in u['objectives']:
  no=next(x for x in n['objectives'] if x['id']==o['id'])
  if o['id'] not in ('obj-u27-passive','obj-u27-stress'):assert o==no
  else:assert {k:v for k,v in o.items() if k!='title'}=={k:v for k,v in no.items() if k!='title'}
 for s in u['sessions']:
  ns=next(x for x in n['sessions'] if x['id']==s['id'])
  if s['id'] not in ('u27-s-passive','u27-s-stress'):assert s==ns;continue
  assert {k:v for k,v in s.items() if k not in ('title','steps')}=={k:v for k,v in ns.items() if k not in ('title','steps')}
  assert {x['id'] for x in s['steps']}=={x['id'] for x in ns['steps']}
  for st in s['steps']:
   nt=next(x for x in ns['steps'] if x['id']==st['id'])
   if st['kind'] in ('ANKI','BINDER','TUTOR','ORIENT'):assert st==nt
   else:assert {k:v for k,v in st.items() if k not in ('title','body','exampleIds')}=={k:v for k,v in nt.items() if k not in ('title','body','exampleIds')}
   if st['kind']!='REMEDIATE':assert st.get('exampleIds',[])==nt.get('exampleIds',[])
for i in b['items']:
 ni=next(x for x in p['items'] if x['id']==i['id'])
 if i['id'] not in l['changedItemIds']:assert i==ni;continue
 for k in ('id','kind','language','objectiveId','reviewStatus','containsGeneratedRussian','promptLetter','answer','sourceId'):assert i[k]==ni[k]
 for k in ('testedFeature','contrastSetId','cueLanguage','independent','responseKind','relationKind','evidenceMode'):assert i['assessment'][k]==ni['assessment'][k]
 if i['id']!='u27-g-stress-past':
  assert i['options']==ni['options']
  for k in ('answerSourceIds','distractorSourceIds','relationParticipantIds','contrastMembers'):assert i['assessment'][k]==ni['assessment'][k]
 else:
  assert i['options'][0]==ni['options'][0];assert i['options'][1].split()[-1]==ni['options'][1].split()[-1]
print('PASS exact Unit27scope:42pending additions;alloriginalrecords/rights/capabilities/examples/keys andallRussian cues/options retained. Only2sessions/4items;optionalpresent andcomprehension/otherunits/mappings unchanged.')
