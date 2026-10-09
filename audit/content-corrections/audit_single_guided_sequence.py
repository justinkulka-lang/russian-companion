#!/usr/bin/env python3
"""Retain sequence-review candidates without claiming semantic acceptance."""
import hashlib,json,sys
from pathlib import Path
HERE=Path(__file__).resolve().parent
ROOT=HERE.parents[1]
raw=(ROOT/'app/src/main/assets/content/v2-curriculum.json').read_bytes();pack=json.loads(raw)
rows=[]
for unit in pack['units']:
    for session in unit['sessions']:
        positions=[i for i,s in enumerate(session['steps']) if s['kind']=='GUIDED']
        if len(positions)!=1:continue
        index=positions[0];late=[s['id'] for s in session['steps'][index+1:] if s['kind']=='TEACH']
        if late:rows.append(dict(unitNumber=unit['number'],sessionId=session['id'],guidedStepId=session['steps'][index]['id'],
            guidedItemIds=session['steps'][index]['itemIds'],laterTeachingStepIds=late,disposition='CUE_LEVEL_SEQUENCE_REVIEW_REQUIRED'))
evidence=dict(schemaVersion=1,curriculumFileSha256=hashlib.sha256(raw).hexdigest(),candidateCount=len(rows),
    affectedUnits=sorted({r['unitNumber'] for r in rows}),
    scope='Exactly one GUIDED step per session; flag later TEACH steps. Multi-guided sessions are outside this scan.',
    limitation='Ordering alone is not proof of a defect. Inspect each guided cue, answer and distractor against prior teaching. This is not semantic closure or approval.',
    rows=rows)
text=json.dumps(evidence,ensure_ascii=False,indent=2)+'\n'
md='# Single-guided session sequence-review candidates\n\n'
md+=f'Curriculum file SHA-256: `{evidence["curriculumFileSha256"]}`.\n\n'
md+=f'**{len(rows)} sessions require cue-level sequence review.** The scan finds teaching after the single guided-practice step. It does not establish that every flagged session is defective: each cue, answer and distractor must be checked against the teaching available before that step. Multiple guided-step sessions are outside this scan.\n\n'
md+='The Unit 24 absence session and Unit 25 courtesy session no longer have this ordering flag. Their implemented corrections remain pending final semantic audit. This inventory does not close CA009/CA010 or approve any other session.\n\n'
md+='| Unit | Session | Guided step | Later teaching steps |\n|---|---|---|---|\n'
for row in rows:md+=f'| {row["unitNumber"]} | `{row["sessionId"]}` | `{row["guidedStepId"]}` | '+', '.join('`'+x+'`' for x in row['laterTeachingStepIds'])+' |\n'
outputs={'SINGLE_GUIDED_SEQUENCE_REVIEW.json':text,'SINGLE_GUIDED_SEQUENCE_REVIEW.md':md}
for name,content in outputs.items():
    target=HERE/name
    if '--check' in sys.argv:
        if not target.is_file() or target.read_text()!=content:raise SystemExit('Stale sequence inventory: '+name)
    else:target.write_text(content)
print(json.dumps({'sequenceReviewCandidates':len(rows),'semanticAccepted':0,'mode':'check' if '--check' in sys.argv else 'write'}))
