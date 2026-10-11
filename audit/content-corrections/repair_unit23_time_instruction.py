#!/usr/bin/env python3
"""Complete the six Unit23 temporal lessons; retain licensed source and pending review."""
import hashlib,json
from pathlib import Path
from copy import deepcopy
ROOT=Path(__file__).resolve().parents[2];PACK=ROOT/'app/src/main/assets/content/v2-curriculum.json'
def sha(s):return hashlib.sha256(s.encode()).hexdigest()
def main():
 raw=PACK.read_bytes();p=json.loads(raw)
 if any(r['itemId']=='prov-ca-u23-ranges-objective' for r in p['provenanceRecords']):print('Already repaired');return
 template=next(r for r in p['provenanceRecords'] if r['itemId']=='prov-ca-17-72');added=[];seen={}
 bibliography='Retained licensed Pressbooks time spans and pending Phase2 completion evidence; Cornell grammar le103_110_f.htm, gr10_d_c.htm, le71_78_l.htm. Consulted 2026-10-11; independently authored English; review pending.'
 def record(text,obj,name):
  if (obj,text) in seen:return seen[(obj,text)]
  r=deepcopy(template);original=bibliography+'\n'+text
  r.update(itemId='prov-ca-u23-'+name,unitNumber=23,objectiveId=obj,sourceRecordId='ca-coverage:'+obj+':'+name,recordType='label',originalSourceText=original,shippedText=text,sourceHash=sha(original),contentHash=sha(text),attribution='Russian Companion independently authored content-audit synthesis, 2026-10-11. No owned-book prose.')
  r['synthesis'].update(bibliography=bibliography,objectiveOrClaimBinding=obj+':'+name,authorshipDeclaration='Independently authored English for Russian Companion, 2026-10-11; no owned-book prose.')
  r['synthesis'].pop('applications',None)
  p['provenanceRecords'].append(r);added.append(r['itemId']);seen[(obj,text)]=r['itemId'];return r['itemId']
 lessons={
 'ranges':('Read decade ranges in their century context',[
 ('A decade is a range of years','В шестидесятых годах — in the 1960s. В девяностых годах — in the 1990s. These expressions refer to decade ranges rather than a single year. The ordinal describing the decade and the noun meaning years are in the prepositional plural. The two displayed choices distinguish the sixties from the nineties.'),
 ('The century comes from context','Here the supplied English glosses assume the twentieth century. A decade expression by itself does not fix the century in every context; an explicit century or the surrounding discussion can identify it. Read the decade word, not just the shared ending for years. The checks recognize these two supplied ranges; they do not test forming a new decade expression.')]),
 'beginning':('Locate the beginning, middle or end of a period',[
 ('The end of a year and the beginning of a century','В конце года — at the end of the year. В начале века — at the beginning of the century. The position word identifies which part of the period is meant, while the following noun identifies the period. Both parts matter: an end is not a beginning, and a year is not a century.'),
 ('The two nouns have different case roles','In these expressions, the location noun after the preposition is prepositional, and the noun naming the period is genitive. The English of relationship links the part to the larger period. Do not assign one case to both nouns simply because they appear in the same time phrase.'),
 ('The middle of the week','В середине недели — in the middle of the week. This locates an event within the central part of a week; it does not mean the end of a year or the beginning of a century. The checks match complete supplied expressions, not independently produced period-and-position combinations.')]),
 'during-event':('Place one action during another event',[
 ('An event supplies the time frame','Она родилась во время войны. — She was born during the war. Во время поездки я познакомился с новыми друзьями. — During the trip I met new friends. The war and trip are the surrounding events during which the birth and meeting happen. During does not mean that the birth or meeting continued for the entire war or trip.'),
 ('Meaning and case of the surrounding event','The compound preposition meaning during takes the event noun in the genitive. Match the whole sentence: birth during a war differs from meeting friends during a trip. This expresses when an event happens within another event, not the time needed to complete a task. The supplied translations do not demonstrate constructing every during-an-event sentence.')]),
 'duration':('Read the interval over which activity occurs',[
 ('A lesson and a day as intervals','В течение урока ученики работали над проектом. — Throughout the lesson, the students worked on the project. В течение дня погода менялась несколько раз. — During the course of the day, the weather changed several times. The interval contains the activity or repeated events. Project work over the lesson does not by itself assert that the project was finished.'),
 ('Duration does not require an unbroken event','It emphasizes the duration over which something occurs. The compound preposition takes a genitive noun naming the interval. The weather example explicitly describes several changes during the day, so the expression need not mean one continuous unchanged action. Compare the whole proposition as well as the interval: students and a project differ from weather changes. These checks identify supplied meanings, not task-completion time.')]),
 'elapsed':('Distinguish before, after, in a period and ago',[
 ('Before and after a reference event','Перед ужином мы ходили в парк. — before. После работы я смотрела фильм. — after. The first places the park visit earlier than dinner; the second places watching a film later than work. The preposition meaning before takes an instrumental noun; the one meaning after takes a genitive noun. The English labels classify only this time relation, not the full sentence translation.'),
 ('A time point is different from a duration','The future-looking in expression locates an event after an interval from the reference time. Ago locates an event before the reference time. Neither meaning alone states how long the activity lasts or how long it takes to finish a task. Before and after a named event likewise express order relative to that event, not completion.'),
 ('The forward and backward interval examples','Мы едем в Латвию через год. — in (a period of time). Мы ездили в Казахстан год назад. — ago. In the first sentence the planned Latvia journey is a year after the reference time; in the second the Kazakhstan journey was a year before it. The forward-looking time construction takes accusative, not genitive; the time noun here has the same surface form as nominative. Ago follows an accusative time expression. The choices classify the shown time relation, not independently produce the whole sentence.')]),
 'completion':('Distinguish time spent from completion within a period',[
 ('Time spent does not assert the task was finished','An imperfective verb with a bare accusative time expression states time spent on an activity and does not assert completion. Анна читала статью два часа. — time spent reading without asserting completion. Борис писал письмо час. — time spent writing without asserting completion. These report time devoted to reading or writing. Not asserting completion does not mean asserting that the task was never finished.'),
 ('The completed task and its time frame','A perfective verb with an accusative time expression after the preposition states the period within which a bounded task was completed. Анна прочитала статью за два часа. — the article was finished within two hours. Here the perfective reading verb and the completion-time construction present the article as finished within the two-hour period. Contrast that with time spent reading without asserting completion. This completion interval differs from the forward-looking time until the Latvia journey. The current choices classify three supplied sentences; they do not test independently choosing aspect and constructing a time phrase.')])}
 u=next(u for u in p['units'] if u['number']==23)
 for slug,(title,cards) in lessons.items():
  obj='obj-u23-'+slug;s=next(s for s in u['sessions'] if obj in s['objectiveIds'] and any(st['kind']=='TEACH' for st in s['steps']))
  s['title']=title;next(o for o in u['objectives'] if o['id']==obj)['title']=title;record(title,obj,slug+'-objective')
  teaches=[st for st in s['steps'] if st['kind']=='TEACH'];assert len(teaches)==len(cards)
  for n,(st,(t,b)) in enumerate(zip(teaches,cards),1):st.update(title=t,body=b);record(t,obj,slug+f'-teach-{n}-title');record(b,obj,slug+f'-teach-{n}-body')
  s['steps']=teaches+[st for st in s['steps'] if st['kind']!='TEACH']
  rem=next(st for st in s['steps'] if st['kind']=='REMEDIATE');rem.update(title='Review the complete time contrast',body='\n\n'.join(b for _,b in cards),exampleIds=list(dict.fromkeys(e for st in teaches for e in st['exampleIds'])))
  record(rem['title'],obj,slug+'-rem-title');record(rem['body'],obj,slug+'-rem-body')
  for st in s['steps']:
   if st['kind'] in ('GUIDED','CHECK'):st['body']='Read the full Russian cue and identify the requested time meaning or aspect-and-construction contrast. A missed check returns to the complete explanation.';record(st['body'],obj,slug+'-'+st['kind'].lower())
 notes={
 'in the 1960s':'the sixties, with the twentieth century assumed in this supplied gloss',
 'in the 1990s':'the nineties, with the twentieth century assumed in this supplied gloss',
 'at the end of the year':'the final part of a year',
 'at the beginning of the century':'the initial part of a century',
 'in the middle of the week':'the central part of a week',
 'She was born during the war.':'birth within the war period',
 'During the trip I met new friends.':'meeting friends within the trip period',
 'Throughout the lesson, the students worked on the project.':'project work over the lesson interval without asserting project completion',
 'During the course of the day, the weather changed several times.':'repeated weather changes within the day',
 'before':'earlier than the named reference event',
 'after':'later than the named reference event',
 'in (a period of time)':'an event after the forward-looking interval, not the duration of the activity',
 'ago':'an event before the reference time, not completion within a period',
 'time spent reading without asserting completion':'imperfective reading with a bare accusative duration; completion is not asserted',
 'time spent writing without asserting completion':'imperfective writing with a bare accusative duration; completion is not asserted',
 'the article was finished within two hours':'perfective reading with an accusative completion interval; the article is presented as finished'}
 changed=[]
 for i in p['items']:
  if not i['objectiveId'].startswith('obj-u23-'):continue
  cue=i['promptLetter'];answer=i['answer'];oldwhy=i['assessment']['whyAnswerDemonstratesObjective'];feedback=oldwhy+'\n'+f'{cue} matches {answer}: {notes[answer]}.'
  wrong=['']+[f'{o}: {notes[o]}. This is a different time relation, interval, event or completion claim from the requested meaning.' for o in i['options'][1:]]
  i.update(feedback=feedback,distractorRationales=wrong);i['assessment'].update(whyAnswerDemonstratesObjective=feedback,distractorDifference=wrong)
  for text,label in [(feedback,'feedback')]+[(w,'wrong-'+str(n)) for n,w in enumerate(wrong[1:])]:record(text,i['objectiveId'],i['id']+'-'+label)
  changed.append(i['id'])
 omitted=0
 for unit in p['units']:
  for session in unit['sessions']:
   for st in session['steps']:
    for k,default in [('dominantAction','Continue'),('referenceIds',[]),('itemIds',[]),('exampleIds',[])]:
     if k in st and st[k]==default:del st[k];omitted+=1
 after=(json.dumps(p,ensure_ascii=False,separators=(',',':'))+'\n').encode();PACK.write_bytes(after)
 ledger=dict(schemaVersion=1,previousHead='9fa857fb7514d56efcee97f0e270390a54f3e8c2',unitNumber=23,disposition='IMPLEMENTED_PENDING_FINAL_AUDIT',beforeCurriculumFileSha256=hashlib.sha256(raw).hexdigest(),afterCurriculumFileSha256=hashlib.sha256(after).hexdigest(),addedProvenanceIds=added,changedItemIds=changed,omittedContinueAndEmptyStepDefaults=omitted,bibliography=bibliography,scope='Six sessions/14 complete cards before guided/full remediation. Correct accusative interval case; distinguish during/overcourse/time until/time spent/completion; no implication unfinished from nonassertion. All existing Russian cues/options/keys/prompts/relationships, licensed provenance, source capabilities and mappings preserved.')
 (ROOT/'audit/content-corrections/UNIT23_TIME_INSTRUCTION_CORRECTION.json').write_text(json.dumps(ledger,ensure_ascii=False,indent=2)+'\n');print(len(added),'pending records;',omitted,'step defaults omitted; bytes',len(after),'request bytes',len(json.dumps(dict(repository_full_name='justinkulka-lang/russian-companion',encoding='utf-8',content=after.decode()),ensure_ascii=False).encode()))
if __name__=='__main__':main()
