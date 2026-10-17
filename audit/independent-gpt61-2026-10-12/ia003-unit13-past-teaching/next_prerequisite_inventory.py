#!/usr/bin/env python3
"""Static source inspection at a pinned commit;zero app tests/semantic acceptance."""
from pathlib import Path
import json,subprocess,hashlib,sys
r=Path(__file__).resolve().parents[3];base='baeba7c6545fbc58fc817ec19c85fc079814d95b';asset='app/src/main/assets/content/v2-curriculum.json';raw=subprocess.check_output(['git','show',base+':'+asset],cwd=r);p=json.loads(raw);u=p['units'][12];rows=[]
for o in u['objectives']:
 if o['kind']!='RECOGNITION':continue
 teaching=[s for se in u['sessions'] for s in se['steps'] if s['id'] in o['instructionStepIds'] and s['kind']=='TEACH']
 guide=[s for se in u['sessions'] for s in se['steps'] if s['id'] in o['practiceStepIds']];first=next((se for se in u['sessions'] if any(s['id'] in o['practiceStepIds'] for s in se['steps'])),None);guideIndex=next((i for i,s in enumerate(first['steps']) if s['id'] in o['practiceStepIds']),None) if first else None
 before=[s['id'] for s in (first['steps'][:guideIndex] if guideIndex is not None else []) if s in teaching];repairs=[s for se in u['sessions'] for s in se['steps'] if s['id'] in o['remediationStepIds']];expectedExamples=list(dict.fromkeys(e for s in teaching for e in s.get('exampleIds',[])))
 rows.append({'objective':o['id'],'title':o['title'],'teaching':teaching,'allTeachingBeforeFirstPractice':len(before)==len(teaching),'beforeFirstPractice':before,'practice':guide,'repair':repairs,'repairMatchesJoinedTeaching':len(repairs)==1 and repairs[0]['body']=='\n\n'.join(s['body'] for s in teaching),'repairMatchesAllTeachingExamples':len(repairs)==1 and repairs[0].get('exampleIds',[])==expectedExamples,'expectedTeachingExamples':expectedExamples,'independentItems':[i for i in p['items'] if i['id'] in o['checkItemIds']]})
j={'kind':'STATIC_ENGINEERING_INVENTORY_NOT_TEST_OR_QUALIFIED_REVIEW','inspectedCommit':base,'assetSha256':hashlib.sha256(raw).hexdigest(),'unit':13,'objectives':rows,'openStructuralCandidates':[o['objective'] for o in rows if not all(o[k] for k in ['allTeachingBeforeFirstPractice','repairMatchesJoinedTeaching','repairMatchesAllTeachingExamples'])],'nextRule':'Inspect complete source/rights/context before correcting candidates;no policy activation or numeric scheduling inference','appTestsExecuted':0}
out=Path(__file__).with_name('NEXT_PREREQUISITE_INVENTORY.json');render=json.dumps(j,ensure_ascii=False,indent=2)+'\n'
if '--check' in sys.argv:assert out.read_text()==render;print('Pinned static inventory matches;zero app tests.')
else:out.write_text(render);print(json.dumps({'openStructuralCandidates':j['openStructuralCandidates']}))
