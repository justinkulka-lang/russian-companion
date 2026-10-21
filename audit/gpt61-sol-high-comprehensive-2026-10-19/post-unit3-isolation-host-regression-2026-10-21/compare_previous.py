from pathlib import Path
import json
W=Path(__file__).parent;s=json.loads((W/'MONITOR_STATE.json').read_text());old=Path('/workspace/artifacts/post-correction-broad-2026-10-20');ledger=json.loads((old/'STOPPED_RECOVERY/FAILURE_LEDGER.json').read_text());continuation=json.loads((old/'continuation/MISSING_AND_INTERRUPTED/RESULT.json').read_text())
rows=[]
for x in ledger:
 key=x['class']+'#'+x['name'];current=s['results'].get(key);rows.append(dict(previousScope='STOPPED_8bff21a3',identity=key,oldOutcome='FAILURE',currentOutcome=current['resultType'] if current else 'NOT_YET_COMPLETED',qualification='NO_AUTOMATIC_FINDING_CLOSURE'))
for x in continuation['tests']:
 if x['outcome'] not in ['FAILED','ERROR']:continue
 key=x['testClass']+'#'+x['method'];current=s['results'].get(key);rows.append(dict(previousScope='CONTINUATION_78e0d170',identity=key,oldOutcome=x['outcome'],currentOutcome=current['resultType'] if current else 'NOT_YET_COMPLETED',qualification='NO_AUTOMATIC_FINDING_CLOSURE'))
(W/'PREVIOUS_FAILURE_COMPARISON_IN_PROGRESS.json').write_text(json.dumps(dict(kind='Different committed inputs;same method identities only;scopes not summed',phase='IN_PROGRESS',rows=rows),indent=2)+'\n')
from collections import Counter
print(dict(Counter(x['currentOutcome'] for x in rows)))
