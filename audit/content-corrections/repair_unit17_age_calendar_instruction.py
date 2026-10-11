#!/usr/bin/env python3
"""Teach existing age and calendar-name constructions without changing Russian assets or keys."""
import hashlib, json
from copy import deepcopy
from pathlib import Path
HERE=Path(__file__).resolve().parent; ROOT=HERE.parents[1]
PACK=ROOT/'app/src/main/assets/content/v2-curriculum.json'
def sha(text): return hashlib.sha256(text.encode()).hexdigest()
def main():
 raw=PACK.read_bytes(); p=json.loads(raw)
 if any(r['itemId']=='prov-ca-u17-calendar-age-teach-1-body' for r in p['provenanceRecords']):
  print('Already repaired; retained evidence unchanged'); return
 u=next(u for u in p['units'] if u['number']==17)
 template=next(r for r in p['provenanceRecords'] if r['itemId']=='prov-ca-17-72')
 bibliography=('Retained Wikibooks days oldid 3594124 and months oldid 4376719, catalog/sources/units-16-20/wikibooks/: exact weekday and month names. '
  'Retained Wikipedia Russian grammar oldid 1369617627, catalog/sources/units-26-30/wikipedia/russian-grammar-1369617627.txt: age construction. '
  'Primary grammar https://www.russianforeveryone.com/Rufe/Lessons/Course1/Grammar/GramUnit15/GramUnit15_2.htm, consulted 2026-10-11 UTC: dative for age. '
  'Primary ReadyRussian Grammar 24 https://www.readyrussian.org/Handouts/Grammar%2024--Capitalization.html, consulted 2026-10-11 UTC: common weekday/month names and sentence-initial capitals. '
  'Retained Pressbooks Diverse Russian traditions grammar chapter sha256 ab2170e37d529a318f395f6e657d8024d0eaed8d7c5c9b34f8831d348b8b76b3: at-what-time examples, catalog/sources/units-16-20/pressbooks/russia-traditions-grammar.html. '
  'Retained ReadyRussian grammar20.html and Wikipedia Russia date notation oldid 1357134172 supply the existing clock and date constructions. '
  'Primary Cornell https://russian.cornell.edu/grammar/html/le79_86_b_b.htm and ReadyRussian https://www.readyrussian.org/Handouts/Grammar%2020--Telling%20Time.html consulted 2026-10-11 UTC: hour in progress, minutes remaining, dial reading and AM/PM context. '
  'Application inference: teach the existing checked names and complete age phrase before practice. Independently authored English 2026-10-11 UTC; final linguistic audit pending.')
 added=[]
 def record(text,obj,name):
  r=deepcopy(template); original=bibliography+'\n'+text
  r.update(itemId='prov-ca-u17-calendar-'+name,objectiveId=obj,sourceRecordId='ca-coverage:'+obj+':calendar:'+name,recordType='label',originalSourceText=original,shippedText=text,sourceHash=sha(original),contentHash=sha(text))
  r['synthesis'].update(bibliography=bibliography,objectiveOrClaimBinding=obj+':calendar:'+name,authorshipDeclaration='Independently authored English for Russian Companion on 2026-10-11 UTC, applying retained source rules; no owned-book prose.',applications=[])
  p['provenanceRecords'].append(r); added.append(r['itemId'])
 lessons={'age': ('Understand the shown age construction',
         [('A person in the dative',
           'мне двадцать лет — I am 20 years old. The person whose age is stated is in the dative case: the first word '
           'means to me in a literal description. The remaining phrase counts twenty years. The present-time Russian '
           'construction has no expressed verb, whereas natural English uses am. This is an age statement, not a '
           'statement of ownership.'),
          ('Work the whole phrase and the quantity',
           'Compare мне двадцать лет with двадцать лет. The full age statement is I am 20 (years old); the shorter '
           'quantity phrase is 20 years and does not by itself identify whose age is meant. The section label age '
           'statements names the topic; it is not a translation of either phrase. For a natural-English choice, select '
           'I am 20 years old. Ownership of summers and a group of people asking a question would describe different '
           'events. These choices recognize this construction; they do not demonstrate independently forming ages for '
           'other people or numbers.')]),
 'weekdays': ('Recognize the retained weekday names',
              [('The first four weekday names',
                'понедельник — Monday. вторник — Tuesday. среда — Wednesday. четверг — Thursday. Each word names a day '
                'of the week. Match the shown word to its English meaning rather than selecting a neighboring day.'),
               ('Complete the weekday contrast',
                'пятница — Friday. суббота — Saturday. воскресенье — Sunday. Together with the previous four names, '
                'these make the seven-day set. In the Thursday check, Friday and Saturday are competing day names. In '
                'the Sunday check, Monday and Saturday name different days. Learn the Sunday form here before meeting '
                'it in an independent check.'),
               ('Capitalization and a worked choice',
                'Weekday names normally stay lowercase within a Russian sentence; the first word of a sentence still '
                'begins with a capital. For the shown четверг, select Thursday. For the shown воскресенье, select '
                'Sunday. Recognizing an isolated name is different from saying on a particular weekday: that use needs '
                'its own preposition and case construction, which these checks do not assess.')]),
 'months': ('Recognize selected month names',
            [('The first four retained month names',
              'Январь — January. Февраль — February. Март — March. Апрель — April. These vocabulary labels name '
              'months, not complete dates. Read the shown Russian word and select its matching English month.'),
             ('Teach the later checked months and alternatives',
              'Июнь — June. Июль — July. Август — August. Декабрь — December. June and July have similar Russian '
              'spellings but differ in the consonant before the final soft sign. December names the final month of the '
              'year. These retained forms include the later check targets and their competing alternatives; this '
              'selected set is not a complete twelve-month production lesson.'),
             ('Capitalization and a worked choice',
              'Month names normally use lowercase within a Russian sentence. These retained vocabulary labels begin '
              'with capitals; that presentation is not a rule to capitalize every month name. For Июнь select June, '
              'and for Декабрь select December. Knowing the standalone name does not demonstrate forming a full date: '
              'a month in a date can have a different case form. Learn those constructions separately.')]),
 'clock': ('Recognize the clock-time question',
           [('Understand the whole question',
             'Который час? — What time is it? This fixed question asks for the current clock time. The noun час '
             'normally means hour; its retained example label time reflects this question context rather than a '
             'universal translation of the isolated noun. Learn the complete question as a unit.'),
            ('Work the meaning choice',
             'For the complete clock question, choose What time is it? A question about which book someone wants would request a '
             'different kind of information. Repeated recognition of this one question is not independent practice in '
             'giving a time, asking when an event occurs, or forming other questions.')]),
 'clock-answers': ('Interpret quarter, half and time remaining',
                   [('A quarter into the next named hour',
                     'четверть восьмого — 7:15. The eighth hour is the period leading up to eight, not the period '
                     'starting at eight. A quarter of that hour has elapsed after seven. The ordinal names the next '
                     'hour and appears in genitive form.'),
                    ('Half of the hour in progress',
                     'полвосьмого — 7:30. Half of the eighth hour has passed, so the clock shows half past seven. The '
                     'compact half-hour expression is written as one word in this retained form. Match the '
                     'construction to the clock reading, rather than treating the named eighth hour as the elapsed '
                     'whole hour.'),
                    ('A quarter remaining before eight',
                     'без четверти восемь — 7:45. This construction counts a quarter hour remaining until eight: '
                     'subtract fifteen minutes from the named upcoming hour. The without construction differs from '
                     'counting a quarter or a half already elapsed. These choices interpret existing forms; context is '
                     'still needed to distinguish morning from evening.')]),
 'quarter-hour': ('Interpret retained at-what-time examples',
                  [('Clock readings and event times',
                    'A current-time question and the time when an event happens are different requests. The retained '
                    'event sentences use at-time expressions. A preposition introduces several of them, while the '
                    'without construction expresses minutes remaining until the upcoming hour. Study the complete '
                    'source examples before selecting their readings.'),
                   ('On the hour and contextual AM or PM',
                    'Концерт начинается в шесть часов. — 18.00. Она была дома в час. — 13.00. These are the readings '
                    'assigned by the retained source. The bare six-hour and one-hour expressions do not themselves '
                    'distinguish evening from morning; the source readings depend on context. Do not infer that every '
                    'occurrence of six means eighteen.'),
                   ('Quarter past and the next ordinal',
                    'Я приду в четверть восьмого. — 7.15 – quarter of the eighth hour. Он нам позвонил в пятнадцать '
                    'минут десятого. — 9.15 – 15 minutes of the tenth hour. A quarter or fifteen minutes has elapsed '
                    'in the hour leading to the named next hour. The ordinal is genitive; the event-time phrase also '
                    'has its introductory preposition.'),
                   ('Half past in two retained constructions',
                    'Наш рейс уезжает в половине пятого. — 4.30 – half of the fifth hour. Мы будем свободны в '
                    'полвторого. — 1.30 – half of the second hour. Both name a half elapsed in the next ordinal hour. '
                    'Their retained preposition and noun forms differ; recognize each complete construction rather '
                    'than exchanging endings mechanically.'),
                   ('A quarter before the upcoming hour',
                    'Передача начинается без четверти три. — 2.45 – 3 without a quarter. Она обычно приходит на работу '
                    'без пятнадцати девять. — 8.45 – 9 without 15 minutes. Subtract the stated remaining minutes from '
                    'the upcoming hour. The preposition meaning without governs the remaining quantity in genitive '
                    'form. These existing choices do not assess independently building a new event-time sentence.')]),
 'clock-productive': ('Recognize three retained clock constructions',
                      [('Minutes in the hour in progress',
                        'в пятнадцать минут второго — at 1:15. The second hour runs from one up to two, so fifteen '
                        'minutes into it is quarter past one. Match this expression to the request for minutes into '
                        'the hour in progress. The next-hour ordinal is genitive. This convention is used up to the '
                        'half hour.'),
                       ('Minutes remaining after the half hour',
                        'без двадцати пяти два — at 1:35. Twenty-five minutes remain before two. Subtract the '
                        'remaining quantity from the upcoming hour. The without construction uses genitive forms for '
                        'the remaining minutes and differs from counting elapsed minutes.'),
                       ('Read the dial numbers',
                        'в двенадцать часов десять минут — at 12:10. This construction states elapsed whole hours and '
                        'then minutes, instead of naming an ordinal hour in progress. Reading the dial is always '
                        'available; context or a 24-hour reading can distinguish AM and PM. These selections identify '
                        'existing construction types and do not demonstrate independently producing a new clock '
                        'expression.')]),
 'calendar-date': ('Interpret retained dates and year abbreviation',
                   [('Read every part of the date',
                     '28 августа 2017 г. gives day month year: 28 August 2017. The abbreviation г. marks the year '
                     'after its number. 15 мая 1996 г. means 15 May 1996. To translate the whole date, include the '
                     'day, month and year; identifying only the month or abbreviation leaves part of the meaning out.'),
                    ('Month case and date naming',
                     'Двадцать восьмое августа names the twenty-eighth of August. This standalone date naming uses a '
                     'neuter ordinal for the day and a genitive month name. A sentence saying when something happens '
                     'can require a different ordinal case; these checks do not assess that change. The month form in '
                     'a date differs from its dictionary form. Numeric day-month-year order and the year abbreviation '
                     'are separate from speaking a complete year in words.')])}
 for slug,(sessionTitle,cards) in lessons.items():
  obj='obj-u17-'+slug; s=next(s for s in u['sessions'] if s['id']=='u17-s-'+slug)
  s['title']=sessionTitle; next(o for o in u['objectives'] if o['id']==obj)['title']=sessionTitle; record(sessionTitle,obj,slug+'-objective-title')
  for n,(title,body) in enumerate(cards,1):
   st=next(st for st in s['steps'] if st['id']==f'u17-st-teach-{slug}-{n}')
   st.update(title=title,body=body); record(title,obj,f'{slug}-teach-{n}-title'); record(body,obj,f'{slug}-teach-{n}-body')
  teaches=[st for st in s['steps'] if st['kind']=='TEACH']; s['steps']=teaches+[st for st in s['steps'] if st['kind']!='TEACH']
  rem=next(st for st in s['steps'] if st['kind']=='REMEDIATE')
  rem.update(title='Review ages and calendar names',body='\n\n'.join(body for _,body in cards),exampleIds=list(dict.fromkeys(e for st in teaches for e in st['exampleIds'])))
  for field in ['title','body']: record(rem[field],obj,slug+'-rem-'+field)
  for kind in ['GUIDED','CHECK']:
   st=next(st for st in s['steps'] if st['kind']==kind)
   st['body']='Read the shown phrase or calendar name and compare the forms explained above, then choose from the displayed alternatives. A missed check returns to all explanations and examples.'
   record(st['body'],obj,slug+'-'+kind.lower()+'-body')
 descriptions={}
 for i in p['items']:
  slug=i['objectiveId'].removeprefix('obj-u17-')
  if slug not in lessons:continue
  cue=i['promptLetter']; answer=i['answer']
  if slug=='age':
   if i['id']=='u17-g-age-0':feedback=cue+' states '+answer+'. The person whose age is given is in the dative case; the present-time Russian construction has no expressed verb.'
   elif i['id']=='u17-i-age-1':feedback=cue+' means '+answer+'. The dative person and the number of years together state the age.'
   else:feedback=cue+' means '+answer+'. This quantity phrase does not by itself identify whose age is meant.'
   if len(i['options'])==3:distractors=['I own 20 summers invents ownership; the shown construction gives an age.','20 people asked me invents people performing an action; the shown construction gives an age.']
   else:distractors=['age statements is a section label rather than the meaning of this phrase.']
  elif slug in ('weekdays','months'):
   feedback=cue+' means '+answer+'. This names a '+('day of the week' if slug=='weekdays' else 'month')+'; choose the matching English name.'
   distractors=[option+' names a different '+('day of the week' if slug=='weekdays' else 'month')+' and does not match the shown word.' for option in i['options'][1:]]
  else:
   feedback=cue+' corresponds to '+answer+'. '+{
    'clock':'The complete question asks for the current clock time.',
    'clock-answers':'Interpret the whole expression as elapsed or remaining parts of the next hour, rather than copying its hour numeral.',
    'quarter-hour':'Interpret the complete event-time expression using the retained source reading; a bare hour alone does not specify AM or PM.',
    'clock-productive':'Select the requested construction type: elapsed minutes in the hour in progress, minutes remaining, or reading the dial numbers.',
    'calendar-date':'Read the relevant date components and distinguish the whole date from an individual component.'
   }[slug]
   distractors=[option+' is a different meaning or construction and does not match the requested reading of the shown form.' for option in i['options'][1:]]
  descriptions[i['id']]=(i['prompt'],feedback,distractors)
 changed=[]
 for id,(prompt,feedback,distractors) in descriptions.items():
  i=next(i for i in p['items'] if i['id']==id); i.update(prompt=prompt,feedback=feedback,distractorRationales=['']+distractors)
  i['assessment'].update(whyAnswerDemonstratesObjective=i['assessment']['whyAnswerDemonstratesObjective']+'\n'+feedback,distractorDifference=['']+distractors)
  record(i['assessment']['whyAnswerDemonstratesObjective'],i['objectiveId'],id+'-rationale')
  for text,label in [(prompt,'prompt'),(feedback,'feedback')]+[(text,'distractor-'+str(n)) for n,text in enumerate(distractors,1)]:record(text,i['objectiveId'],id+'-'+label)
  changed.append(id)
 after=(json.dumps(p,ensure_ascii=False,separators=(",",":"))+'\n').encode(); PACK.write_bytes(after)
 ledger=dict(schemaVersion=1,previousHead='a355cb3a77cee83fd7b364f995cba5ad057116b0',unitNumber=17,disposition='IMPLEMENTED_PENDING_FINAL_AUDIT',beforeCurriculumFileSha256=hashlib.sha256(raw).hexdigest(),afterCurriculumFileSha256=hashlib.sha256(after).hexdigest(),addedProvenanceIds=added,changedItemIds=changed,bibliography=bibliography,scope='Eight age/calendar/time sessions and objective titles, teaching-before-guided order, complete remediation, twenty-five item English explanation fields. Keys, Russian assets, source relationships and advancement unchanged. Preposition prompts ask for meaning without supplying the English answer.')
 (HERE/'UNIT17_AGE_CALENDAR_INSTRUCTION_CORRECTION.json').write_bytes((json.dumps(ledger,ensure_ascii=False,indent=2)+'\n').encode())
 print(f'Unit 17 age/calendar instruction repaired; {len(added)} pending coverage records; keys and Russian assets unchanged')
if __name__=='__main__':main()
