"""Read-only, independent display-position reconstruction. Run at repository root; output path argument."""
import sys
sys.dont_write_bytecode = True
import csv
import hashlib
import json
from collections import Counter
from pathlib import Path
from catalog.curriculum_storage import loads

root = Path.cwd()
out = Path(sys.argv[1])
d = loads((root / 'app/src/main/assets/content/v2-curriculum.json').read_bytes())
items = {i['id']: i for i in d['items']}

def parts(xs):
    return str(len(xs)) + '{' + ';'.join(str(len(x.encode('utf-8'))) + ':' + x for x in xs) + '}'

def order(item, context):
    return sorted(item['options'], key=lambda option: (
        hashlib.sha256(parts(['sha256-option-order-v1', context, item['id'], option]).encode('utf-8')).hexdigest(), option))

rows = []
for unit in d['units']:
    for session in unit['sessions']:
        for step in session['steps']:
            for item_id in step.get('itemIds', []):
                item = items[item_id]
                rows.append(dict(route='lesson', context=step['id'], itemId=item_id,
                    optionCount=len(item['options']), rawCorrectIndex=item['options'].index(item['answer']),
                    displayCorrectIndex=order(item, parts(['lesson', step['id']])).index(item['answer'])))
required = {i for u in d['units'] for o in u['objectives'] if o.get('gating', True) for i in o.get('checkItemIds', [])}
declared = {i for u in d['units'] for o in u['objectives'] for i in o.get('checkItemIds', [])}
for item_id in sorted(required):
    row = next(r for r in rows if r['itemId'] == item_id)
    rows.append(dict(row, route='required'))
for route, context in [('lab', parts(['lab', 'mixed'])), ('review', parts(['review', 'corpus-distribution', '0']))]:
    for item in items.values():
        rows.append(dict(route=route, context='mixed' if route == 'lab' else 'corpus-distribution/0',
            itemId=item['id'], optionCount=len(item['options']), rawCorrectIndex=item['options'].index(item['answer']),
            displayCorrectIndex=order(item, context).index(item['answer'])))
with (out / 'POSITION_INVENTORY.csv').open('w') as f:
    w = csv.DictWriter(f, fieldnames=list(rows[0]), lineterminator='\n')
    w.writeheader()
    w.writerows(rows)
summary = dict(rawItems=len(items), rawCorrectPositionCounts=dict(Counter(i['options'].index(i['answer']) for i in items.values())),
    rawRequiredItems=len(required), rawRequiredCorrectPositionCounts=dict(Counter(items[i]['options'].index(items[i]['answer']) for i in required)),
    allDeclaredCheckItems=len(declared), nonGatingDeclaredCheckItems=sorted(declared - required), routes={})
for route in ['lesson', 'required', 'lab', 'review']:
    selected = [r for r in rows if r['route'] == route]
    summary['routes'][route] = dict(displays=len(selected), correctPositionCounts=dict(Counter(r['displayCorrectIndex'] for r in selected)),
        alwaysFirstCorrect=sum(r['displayCorrectIndex'] == 0 for r in selected))
summary['scope'] = ('Static independent reconstruction, to compare with executed Kotlin corpus test. Lab mixed and synthetic corpus review context '
    'are bounded projections, not every possible review assignment. The 429 required items exclude three non-gating Unit27 checks.')
assert len(required) == 429 and sum(items[i]['options'][0] == items[i]['answer'] for i in required) == 379
(out / 'POSITION_SUMMARY.json').write_text(json.dumps(summary, indent=2) + '\n')
print(json.dumps(summary['routes'], indent=2))
