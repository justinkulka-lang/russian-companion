from pathlib import Path
import json,collections,subprocess
w=Path(__file__).parent
for mode in ['DISCOVERY','REGRESSION']:
 o=w/mode
 if not o.exists():continue
 events=[json.loads(s) for s in (o/'EVENTS.jsonl').read_text().splitlines()] if (o/'EVENTS.jsonl').exists() else []
 finished=[x for x in events if x['kind']=='RESULT'];done={(x['className'],x['name']) for x in finished};active=[x['className']+'#'+x['name'] for x in events if x['kind']=='START' and (x['className'],x['name']) not in done]
 v=dict(mode=mode,reportedEvents=len(finished),outcomes=dict(collections.Counter(x['resultType'] for x in finished)),active=active,realExit=(o/'RUN.exit').read_text().strip() if (o/'RUN.exit').exists() else None,collectorComplete=(o/'SHA256.json').exists())
 if (o/'RESULT.json').exists():
  r=json.loads((o/'RESULT.json').read_text());v.update(counts=r['testCounts'],classes=r['classes'])
 print(json.dumps(v))
 print((o/'RUN.log').read_text()[-1200:])
