#!/usr/bin/env python3
"""Move existing teaching before one guided step in six reviewed sessions."""
import hashlib,json
from pathlib import Path
HERE=Path(__file__).resolve().parent
ROOT=HERE.parents[1]
PACK=ROOT/'app/src/main/assets/content/v2-curriculum.json'
TARGETS=[(6, 'feminine', 'The guided item requests the accusative singular, but the first card gives only book glosses. The existing accusative-declension explanation follows practice.'), (7, 'spelling', 'The guided choice requests хоро́шие; that exact plural form and the ending-spelling explanation appear on later cards.'), (8, 'spelling', 'The guided choice requests кни́ги; that plural form and the seven-letter spelling explanation appear on later cards.'), (12, 'aspect', 'The guided item requests an aspect counterpart, but the first card gives only identical English glosses. The context-dependent counterpart explanation follows practice.'), (16, 'inst-sg', 'The guided item asks for instrumental singular among case forms. The hard-stem ending explanation and distractor-form cards follow practice.'), (16, 'inst-adj', 'The guided item asks for masculine instrumental among adjective forms. The instrumental-ending explanation and distractor-form cards follow practice.')]
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
 result=dict(schemaVersion=1,previousHead='86a2ed63989e38ae5b36ac64e5e8f347bfe076ae',
  beforeCurriculumFileSha256=hashlib.sha256(raw).hexdigest(),afterCurriculumFileSha256=hashlib.sha256(after).hexdigest(),
  scope='Only existing step order changes in six cue-reviewed sessions; teaching and non-teaching relative order are each retained.',
  limitation='This fixes access to existing prerequisite teaching. It does not attest to linguistic accuracy, complete worked-example coverage, UI behavior or semantic audit closure.',
  rows=rows)
 (HERE/'CASE_ASPECT_PREREQUISITE_ORDER_CORRECTION.json').write_text(json.dumps(result,ensure_ascii=False,indent=2)+'\n')
 print('Reordered six reviewed sessions; no step contents, keys, gates or provenance changed')
if __name__=='__main__':main()
