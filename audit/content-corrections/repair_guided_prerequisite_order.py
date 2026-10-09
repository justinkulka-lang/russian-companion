#!/usr/bin/env python3
"""Move existing teaching before one guided step in four reviewed sessions."""
import hashlib,json
from pathlib import Path
HERE=Path(__file__).resolve().parent
ROOT=HERE.parents[1]
PACK=ROOT/'app/src/main/assets/content/v2-curriculum.json'
TARGETS=[
 (6,'possessive','The guided choice contains моя́, мо́й and мои́; the exact stressed forms and possessive contrast appear on later teaching cards.'),
 (21,'purpose','The prompt asks for ordinary purpose and the feedback contrasts the bookish counterpart; that register explanation appears after guided practice.'),
 (28,'imperfective-adverb','The timing check contrasts simultaneous action, absolute tense and a different subject; the explicit no-independent-tense and shared-subject explanation follows the choice.'),
 (28,'perfective-adverb','The check asks about anteriority before a future main action; the explicit past-or-future main-action explanation follows the choice.'),
]
def main():
 raw=PACK.read_bytes();p=json.loads(raw);rows=[]
 for number,topic,reason in TARGETS:
  u=next(u for u in p['units'] if u['number']==number)
  s=next(s for s in u['sessions'] if s['id']==f'u{number}-s-{topic}')
  old=[st['id'] for st in s['steps']]
  assert sum(st['kind']=='GUIDED' for st in s['steps'])==1,s['id']
  guided=next(st for st in s['steps'] if st['kind']=='GUIDED')
  s['steps']=[st for st in s['steps'] if st['kind']=='TEACH']+[st for st in s['steps'] if st['kind']!='TEACH']
  rows.append(dict(unitNumber=number,sessionId=s['id'],guidedItemIds=guided['itemIds'],reason=reason,
   previousStepOrder=old,correctedStepOrder=[st['id'] for st in s['steps']],disposition='IMPLEMENTED_PENDING_FINAL_AUDIT'))
 if all(r['previousStepOrder']==r['correctedStepOrder'] for r in rows):
  print('Already repaired; retained historical evidence unchanged');return
 assert all(r['previousStepOrder']!=r['correctedStepOrder'] for r in rows),'Unexpected partially repaired input'
 after=(json.dumps(p,ensure_ascii=False,indent=2)+'\n').encode();PACK.write_bytes(after)
 result=dict(schemaVersion=1,previousHead='cf6dbe57dacf13535c9c8c547eef72b6702feae7',
  beforeCurriculumFileSha256=hashlib.sha256(raw).hexdigest(),afterCurriculumFileSha256=hashlib.sha256(after).hexdigest(),
  scope='Only existing step order changes in four cue-reviewed sessions; teaching and non-teaching relative order are each retained.',
  limitation='This fixes access to existing prerequisite teaching. It does not attest to linguistic accuracy, complete worked-example coverage, UI behavior or semantic audit closure.',
  rows=rows)
 (HERE/'GUIDED_PREREQUISITE_ORDER_CORRECTION.json').write_text(json.dumps(result,ensure_ascii=False,indent=2)+'\n')
 print('Reordered four reviewed sessions; no step contents, keys, gates or provenance changed')
if __name__=='__main__':main()
