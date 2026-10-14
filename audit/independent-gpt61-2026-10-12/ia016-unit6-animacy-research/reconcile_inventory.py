from pathlib import Path
import json,sys,subprocess
from collections import defaultdict
root=Path(__file__).resolve().parents[3];sys.path.insert(0,str(root/'catalog'));from curriculum_storage import loads
p=loads((root/'app/src/main/assets/content/v2-curriculum.json').read_bytes());items={i['id']:i for i in p['items']};buckets=defaultdict(list)
for u in p['units']:
 if not 6<=u['number']<=30:continue
 for o in u['objectives']:
  if o['kind']!='RECOGNITION' or o.get('gating',True) is not True:continue
  guided=[i for s in u['sessions'] for t in s['steps'] if t['id'] in o.get('practiceStepIds',[]) for i in t.get('itemIds',[])]
  for id,role in [(i,'GUIDED') for i in guided]+[(i,'INDEPENDENT') for i in o.get('checkItemIds',[])]:
   i=items[id];sig=(u['number'],i['prompt'],i.get('promptLetter'),i['answer'],tuple(i['options']));buckets[sig].append((i,role))
main=[];same=[]
for sig,rows in buckets.items():
 if len(rows)<2:continue
 g=dict(unitNumber=sig[0],itemIds=[i['id'] for i,r in rows],prompt=sig[1],cue=sig[2],answer=sig[3],options=list(sig[4]),objectiveIds=[i['objectiveId'] for i,r in rows]);roles={r for i,r in rows}
 if len(roles)>1:main.append(g)
 else:g['role']=next(iter(roles));same.append(g)
f=root/'audit/independent-gpt61-2026-10-12/IA016_CURRENT_DUPLICATE_INVENTORY.json';old=json.loads(f.read_text());assert len(main)==2 and len(same)==3;assert all('u6-i-animacy-3' not in g['itemIds'] for g in main+same)
(Path(__file__).parent/'INVENTORY_RECONCILIATION.json').write_text(json.dumps(dict(before=old,after=dict(groups=main,additionalSameRoleGroups=same),formalAcceptance=False),ensure_ascii=False,indent=2)+'\n')
old.update(repositoryCommit=subprocess.check_output(['git','rev-parse','HEAD'],cwd=root,text=True).strip(),groups=main,count=2,priorInventoryCommit=old['repositoryCommit'],priorInventoryCount=old['count'],difference='Unit6 same-objective duplicate independent teacher check replaced by required supplied inanimate-chair classification. Three other same-role groups and two inter-objective reinforcement groups remain pending independent assessment; no semantic closure.')
old['additionalSameRoleGroupsOutsideGuidedIndependentInventory']=same;f.write_text(json.dumps(old,ensure_ascii=False,indent=2)+'\n');print('Two guided/independent groups and three separate same-role groups remain')
