#!/usr/bin/env python3
"""Teach existing number constructions without changing Russian assets or keys."""
import hashlib, json
from copy import deepcopy
from pathlib import Path
HERE=Path(__file__).resolve().parent; ROOT=HERE.parents[1]
PACK=ROOT/'app/src/main/assets/content/v2-curriculum.json'
def sha(text): return hashlib.sha256(text.encode()).hexdigest()
def main():
 raw=PACK.read_bytes(); p=json.loads(raw)
 if any(r['itemId']=='prov-ca-u17-numbers-ordinals-teach-1-body' for r in p['provenanceRecords']):
  print('Already repaired; retained evidence unchanged'); return
 u=next(u for u in p['units'] if u['number']==17)
 template=next(r for r in p['provenanceRecords'] if r['itemId']=='prov-ca-17-72')
 bibliography=('Retained Wikibooks Russian/Grammar/Numbers oldid 4371358, catalog/sources/units-6-10/wikibooks/numbers-4371358.txt: ordinal adjective agreement, numeral gender, count noun cases, compound counts and teen exceptions. '
  'Primary learning grammar https://russky.info/grammar/numerals?hl=en, consulted 2026-10-11 UTC: ordinal positions and adjective agreement; counting constructions. '
  'Application inference: explain existing Unit 17 forms and the requested positions or quantities; distinguish the retained ordinal noun phrase from the full source sentence gloss. Independently authored English 2026-10-11 UTC; final linguistic audit pending.')
 added=[]
 def record(text,obj,name):
  r=deepcopy(template); original=bibliography+'\n'+text
  r.update(itemId='prov-ca-u17-numbers-'+name,objectiveId=obj,sourceRecordId='ca-coverage:'+obj+':numbers:'+name,recordType='label',originalSourceText=original,shippedText=text,sourceHash=sha(original),contentHash=sha(text))
  r['synthesis'].update(bibliography=bibliography,objectiveOrClaimBinding=obj+':numbers:'+name,authorshipDeclaration='Independently authored English for Russian Companion on 2026-10-11 UTC, applying retained source rules; no owned-book prose.',applications=[])
  p['provenanceRecords'].append(r); added.append(r['itemId'])
 lessons={'ordinals': ('Recognize ordinal positions and agreement',
              [('Position rather than quantity',
                'пе́рвый — first. второ́й — second. тре́тий — third. четвёртый — fourth. An ordinal identifies '
                'position in an ordered sequence; a cardinal tells how many. These isolated ordinal forms are '
                'masculine nominative singular. Learn the position expressed by each form before choosing it.'),
               ('Ordinals agree like adjectives',
                'Ordinals agree with their noun in gender, number and case, like adjectives. восемнадцатую девушку '
                'means the eighteenth girl as a feminine accusative noun phrase. The retained example comes from a '
                'sentence about photographing her; this displayed fragment alone does not mean She just photographed. '
                'The ordinal ending follows the noun phrase, rather than staying in the masculine dictionary form. '
                'These recognition checks do not assess producing agreement in a sentence.'),
               ('Work the requested position',
                'For the first ordinal, choose пе́рвый; for the second ordinal, choose второ́й; for the third ordinal, '
                'choose тре́тий. четвёртый expresses fourth, so it fails each of those requests. Read the English '
                'request: the separately shown Russian cue may name a different position and is not the target to '
                'copy.')]),
 'hours': ('Recognize hour counts and noun forms',
           [('One, two and five hours',
             'один час — one hour. два часа — two hours. пять часов — five hours. The noun changes with the count: '
             'nominative singular after one, genitive singular after two, and genitive plural after five. English uses '
             'hours for both two and five, but Russian uses different forms.'),
            ('Bound the counting rule',
             'For basic counts in nominative or inanimate accusative constructions, use the one pattern for numbers '
             'ending in 1, the two pattern for endings 2, 3 or 4, and the five pattern for endings 0, 5, 6, 7, 8 or 9. '
             'Numbers ending in 11, 12, 13 or 14 take the five pattern instead. Other case constructions require '
             'additional changes to the numeral and noun; this is not a rule for every sentence position.'),
            ('Work an hour choice',
             'For a two-hour row, choose два часа. один час names one hour and пять часов names five hours. For a '
             'five-hour row, choose пять часов, even when the separately shown cue is the one-hour phrase. Read the '
             'requested quantity rather than copying the cue. Recognizing these hour counts does not demonstrate '
             'telling the time on a clock.')]),
 'years': ('Recognize year counts and noun forms',
           [('One, two and five years',
             'один год — one year. два года — two years. пять лет — five years. The basic counting forms are '
             'nominative singular after one, genitive singular after two, and genitive plural after five. The plural '
             'counting form for years uses a different stem: learn пять лет as the complete five-year phrase.'),
            ('Compound counts and the teen exception',
             'сорок один год — forty-one years. The compound ends in 1, so its noun follows the one pattern. In basic '
             'nominative or inanimate accusative counts, endings 2, 3 or 4 follow the two pattern; endings 0 and 5 '
             'through 9 follow the five pattern. Endings 11 through 14 are exceptions to the final-digit rule and take '
             'the five pattern. Other grammatical cases need additional changes.'),
            ('Work a year choice',
             'For a five-year row, choose пять лет, rather than два года, which names two years. For a one-year row, '
             'choose один год; for a two-year row, choose два года. The separately shown cue can have a different '
             'count. These checks recognize quantities of years, not ordinal calendar-year dates or a complete age '
             'sentence.')]),
 'minutes': ('Recognize minute counts and feminine numerals',
             [('Minutes use feminine one and two',
               'одна минута — one minute. две минуты — two minutes. пять минут — five minutes. The noun for minute is '
               'feminine, so one and two use feminine forms here. Compare the masculine hour and year phrases. The '
               'noun is nominative singular after one, genitive singular after two, and genitive plural after five.'),
              ('Use the bounded count pattern',
               'For basic nominative or inanimate accusative counts, an ending of 1 follows the one-minute pattern, '
               'endings 2, 3 or 4 follow the two-minute pattern, and endings 0 or 5 through 9 follow the five-minute '
               'pattern. Endings 11 through 14 take the five-minute pattern instead of following the final digit '
               'alone. Different case constructions require additional numeral and noun changes.'),
              ('Work a minute choice',
               'For a one-minute row, choose одна минута. For a two-minute row, choose две минуты. For a five-minute '
               'row, choose пять минут. Each other phrase names a different quantity. Read the English request even if '
               'the separately shown cue has a different count. These quantity choices do not assess expressing '
               'minutes past an hour on a clock.')])}
 for slug,(sessionTitle,cards) in lessons.items():
  obj='obj-u17-'+slug; s=next(s for s in u['sessions'] if s['id']=='u17-s-'+slug)
  s['title']=sessionTitle; next(o for o in u['objectives'] if o['id']==obj)['title']=sessionTitle; record(sessionTitle,obj,slug+'-objective-title')
  for n,(title,body) in enumerate(cards,1):
   st=next(st for st in s['steps'] if st['id']==f'u17-st-teach-{slug}-{n}')
   st.update(title=title,body=body); record(title,obj,f'{slug}-teach-{n}-title'); record(body,obj,f'{slug}-teach-{n}-body')
  teaches=[st for st in s['steps'] if st['kind']=='TEACH']; s['steps']=teaches+[st for st in s['steps'] if st['kind']!='TEACH']
  rem=next(st for st in s['steps'] if st['kind']=='REMEDIATE')
  rem.update(title='Review positions and counts',body='\n\n'.join(body for _,body in cards),exampleIds=list(dict.fromkeys(e for st in teaches for e in st['exampleIds'])))
  for field in ['title','body']: record(rem[field],obj,slug+'-rem-'+field)
  for kind in ['GUIDED','CHECK']:
   st=next(st for st in s['steps'] if st['kind']==kind)
   st['body']='Read the requested position or quantity and compare the forms explained above, then choose from the displayed alternatives. A missed check returns to all explanations and examples.'
   record(st['body'],obj,slug+'-'+kind.lower()+'-body')
 descriptions={}
 meanings={'ordinals':{'пе́рвый':'first','второ́й':'second','тре́тий':'third','четвёртый':'fourth'},'hours':{'один час':'one hour','два часа':'two hours','пять часов':'five hours'},'years':{'один год':'one year','два года':'two years','пять лет':'five years'},'minutes':{'одна минута':'one minute','две минуты':'two minutes','пять минут':'five minutes'}}
 for i in p['items']:
  slug=i['objectiveId'].removeprefix('obj-u17-')
  if slug not in meanings:continue
  meaningsForSlug=meanings[slug]; answer=i['answer']; meaning=meaningsForSlug[answer]
  target=meaning+' ordinal' if slug=='ordinals' else meaning.replace(' hours','-hour').replace(' hour','-hour').replace(' years','-year').replace(' year','-year').replace(' minutes','-minute').replace(' minute','-minute')+' row'
  feedback=answer+' means '+meaning+'. This is the '+target+'. Choose this phrase for the requested '+('position' if slug=='ordinals' else 'quantity')+', even when the shown cue differs.'
  distractors=[option+' means '+meaningsForSlug[option]+', rather than the requested '+meaning+'.' for option in i['options'][1:]]
  descriptions[i['id']]=(i['prompt'],feedback,distractors)
 changed=[]
 for id,(prompt,feedback,distractors) in descriptions.items():
  i=next(i for i in p['items'] if i['id']==id); i.update(prompt=prompt,feedback=feedback,distractorRationales=['']+distractors)
  i['assessment'].update(whyAnswerDemonstratesObjective=feedback,distractorDifference=['']+distractors)
  for text,label in [(prompt,'prompt'),(feedback,'feedback')]+[(text,'distractor-'+str(n)) for n,text in enumerate(distractors,1)]:record(text,i['objectiveId'],id+'-'+label)
  changed.append(id)
 after=(json.dumps(p,ensure_ascii=False,separators=(",",":"))+'\n').encode(); PACK.write_bytes(after)
 ledger=dict(schemaVersion=1,previousHead='5d4a56e93b9345cee92094d515c4d7f385edb8af',unitNumber=17,disposition='IMPLEMENTED_PENDING_FINAL_AUDIT',beforeCurriculumFileSha256=hashlib.sha256(raw).hexdigest(),afterCurriculumFileSha256=hashlib.sha256(after).hexdigest(),addedProvenanceIds=added,changedItemIds=changed,bibliography=bibliography,scope='Four number sessions and objective titles, teaching-before-guided order, complete remediation, twelve item English feedback fields. Keys, Russian assets, source relationships and advancement unchanged. Preposition prompts ask for meaning without supplying the English answer.')
 (HERE/'UNIT17_NUMBER_INSTRUCTION_CORRECTION.json').write_bytes((json.dumps(ledger,ensure_ascii=False,indent=2)+'\n').encode())
 print(f'Unit 17 number instruction repaired; {len(added)} pending coverage records; keys and Russian assets unchanged')
if __name__=='__main__':main()
