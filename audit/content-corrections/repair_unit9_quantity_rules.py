#!/usr/bin/env python3
"""Complete bounded Unit9 counted-noun rules; retain original records and Russian cues."""
import hashlib,json
from copy import deepcopy
from pathlib import Path
HERE=Path(__file__).resolve().parent
ROOT=HERE.parents[1]
PACK=ROOT/'app/src/main/assets/content/v2-curriculum.json'
BASELINE='016ad0211e4471c2ee4b6249acd4455499e3f861'
NOM='In this basic counting phrase, the noun is nominative singular.'
SG='In this basic counting phrase, the noun is genitive singular.'
PL='In this basic counting phrase, the noun is genitive plural.'
ANIMATE='In this line, the animate object is accusative and both numeral and noun are declined.'
BASIC='In this line, use the basic nominative or inanimate accusative counting pattern.'
OBJECTIVES=['obj-u9-quantity','obj-u9-pattern']
def sha(s):return hashlib.sha256(s.encode()).hexdigest()
def canonical(applications):
 def field(s):return str(len(s.encode()))+':'+s
 return 'tuple-v1:'+str(len(applications))+';'+''.join(field(a['cue'])+field(a['answer'])+str(len(a['distractors']))+';'+''.join(field(d) for d in a['distractors']) for a in applications)
def main():
 raw=PACK.read_bytes();p=json.loads(raw)
 if any(r['itemId']=='prov-ca-u9-rule-u9-g-quantity-0-0' for r in p['provenanceRecords']):print('Already repaired; evidence unchanged');return
 source=ROOT/'catalog/sources/units-6-10/wikibooks/numbers-4371358.txt';text=source.read_text()
 for marker in ['using the last two digits','двести тринадцать собак','Я видел двух друзей','in addition to the declension of the items following them']:assert marker in text
 template=next(r for r in p['provenanceRecords'] if r['itemId']=='prov-ca-u5-behavior-teach-1-body')
 bibliography=('Retained Wikibooks Russian/Numbers oldid4371358, catalog/sources/units-6-10/wikibooks/numbers-4371358.txt, CC BY-SA 4.0: complete Use of numerals in context, one/two/three/four and5-through20 examples, last-digit/last-two-digit interpretation,203 versus213, zero endings, and non-basic-case example I saw two friends. '
  'Retained Wikibooks Russian/Grammar/Accusative_case oldid4330404, CC BY-SA 4.0, Usage and Nouns: direct object and animate plural accusative shapes. '
  'Retained OpenRussian nouns.csv commit50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0: стол sg_gen and pl_gen fields for the already admitted isolated table forms. '
  'Bounded inference for the already admitted examples: basic standalone counting phrases use nominative context; choose noun nominative singular for final1 except11, genitive singular for final2/3/4 except12/13/14, genitive plural otherwise; inspect last two digits first. '
  'The animate object sentence lies outside the basic nominative/inanimate accusative pattern and declines both numeral and noun. No new Russian form or productive paradigm is supplied. '
  'Independently authored English on 2026-10-13 within content-audit-2026-10-08; no copied primary prose or private Penguin text; qualified semantic audit pending.')
 added=[];changes=[];pins={}
 def record(value,obj,name,original=None,applications=None,rid=None):
  r=deepcopy(template);original=original or bibliography+'\n'+value
  r.update(itemId='prov-ca-u9-rule-'+name,unitNumber=9,objectiveId=obj,recordType='label',sourceRecordId=rid or 'ca-coverage:'+obj+':quantity-rule:'+name,
   originalSourceText=original,shippedText=value,sourceHash=sha(original),contentHash=sha(value))
  r['synthesis'].update(bibliography=bibliography,authorshipDeclaration='Independently authored English on 2026-10-13, applying complete retained rules to admitted examples; no copied primary or owned-book prose.',objectiveOrClaimBinding=obj+':quantity-rule:'+name,applications=applications or [])
  assert not any(x['itemId']==r['itemId'] for x in p['provenanceRecords']);p['provenanceRecords'].append(r);added.append(r['itemId']);return r['itemId']
 values={'u9-g-quantity-0':1,'u9-i-quantity-1':22,'u9-i-quantity-2':5,'u9-i-quantity-3':21,'u9-i-quantity-4':16,'u9-i-quantity-5':41,'u9-i-quantity-6':213,'u9-i-quantity-7':10,'u9-i-quantity-8':2,'u9-i-quantity-9':203,'u9-g-pattern-0':1,'u9-i-pattern-1':3,'u9-i-pattern-2':5,'u9-i-pattern-3':16}
 mapping={'1 -> nominative':NOM,'2, 3, 4 -> genitive singular':SG,'5 and more -> genitive plural':PL}
 for item in p['items']:
  if item['objectiveId'] not in OBJECTIVES:continue
  before=deepcopy(item);obj=item['objectiveId'];special=item['id']=='u9-i-pattern-4'
  if special:
   options=[ANIMATE,BASIC];answer=ANIMATE
   feedback='The line means I saw two friends. The friends are an animate accusative object. Both the numeral and noun are declined, so the basic nominative or inanimate accusative counting pattern does not describe this sentence.'
  else:
   n=values[item['id']];last_two=n%100;last=n%10;case=PL if 11<=last_two<=14 else NOM if last==1 else SG if last in [2,3,4] else PL
   options=[mapping[v] for v in item['options']];answer=mapping[item['answer']];assert answer==case
   reason=(f'The value is {n}; its last two digits are {last_two}, so the 11-through14 exception takes priority over the final digit.' if 11<=last_two<=14 else
    f'The value is {n}; the last two digits do not fall in 11-through14, and the final digit is {last}.')
   case_name={NOM:'nominative singular',SG:'genitive singular',PL:'genitive plural'}[case]
   feedback=reason+f' In this basic counting phrase, the counted noun is {case_name}. '+('A final zero follows the same noun-case pattern as ten.' if last==0 else 'Classify the complete number rather than treating every value above five as the same pattern.')
  rationale={NOM:'The nominative-singular explanation applies to a final one outside the teen exception, rather than this shown counting pattern.',SG:'The genitive-singular explanation applies to final two, three or four outside the teen exception, rather than this shown counting pattern.',PL:'The genitive-plural explanation applies to zero, five-through-nine endings and the teen exception, rather than this shown counting pattern.',BASIC:'The basic nominative/inanimate accusative pattern omits the animate accusative object and declined numeral in this sentence.'}
  item.update(kind='CONTRAST_SELECTION',sourceId=template['sourceId'],prompt='Which complete case explanation fits the shown example?',options=options,answer=answer,feedback=feedback,distractorRationales=['' if v==answer else rationale[v] for v in options])
  app={'cue':item['promptLetter'],'answer':answer,'distractors':[v for v in options if v!=answer]};rid='ca-rule-context:'+item['id'];original=bibliography+'\nContext explanation:\n'+feedback+'\nTuple-v1:'+canonical([app])
  ids=[record(v,obj,item['id']+'-'+str(n),original,[app],rid) for n,v in enumerate([item['promptLetter'],answer]+app['distractors'])]
  a=item['assessment'];a.update(evidenceMode='STATED_APPLICATION',relationKind='STATED_APPLICATION',relationParticipantIds=ids,answerSourceIds=[ids[1]],distractorSourceIds=['' if v==answer else ids[2+app['distractors'].index(v)] for v in options],distractorDifference=item['distractorRationales'],contrastMembers=options,whyAnswerDemonstratesObjective=feedback)
  record(item['prompt'],obj,item['id']+'-prompt');record(feedback,obj,item['id']+'-feedback')
  for n,r in enumerate(item['distractorRationales']):
   if r:record(r,obj,item['id']+f'-rationale-{n}')
  pins[rid]={'sourceHash':sha(original),'applications':[app],'objectiveId':obj};changes.append({'before':before,'after':deepcopy(item)})
 scope='These basic counting examples use nominative phrases. The related noun-case pattern also applies in inanimate accusative contexts, with the appropriate noun form. Other sentence cases require numeral declension as well as noun declension.'
 exception='For a compound count, inspect the last two digits first. If they are 11, 12, 13 or 14, use genitive plural for the counted noun in the basic construction. Otherwise, the final digit selects the pattern: one, two-through-four, or zero/five-through-nine.'
 lessons={
 'u9-s-quantity':[
  ('Start with the construction',scope+' A count ending in one outside the teen exception uses a nominative singular noun in the shown basic phrases.'),
  ('Two, three and four','A count ending in two, three or four outside the teen exception uses a genitive singular noun in the basic construction. This is a grammatical singular form even though the count is greater than one.'),
  ('Genitive plural counts','Counts ending in zero or five through nine use genitive plural in the basic construction. The teen exception also uses genitive plural. Ten and sixteen therefore fit this pattern.'),
  ('Compound counts',exception+' Compare 21 and 22: their final digits select different noun cases; being larger than five is not enough to choose genitive plural.'),
  ('Teen values and exceptions',exception+' All counts from 11 through19 use genitive plural here. For 15 through19 the final digit already leads to genitive plural;11 through14 override the one/two/three/four ending pattern.'),
  ('One and zero endings','The example with 41 follows the final-one pattern and uses nominative singular for the counted noun. The example with 10 follows the zero-ending pattern and uses genitive plural.'),
  ('Hundreds preserve the boundary','The count 203 ends in 03, so final three selects genitive singular in the basic counting phrase. The count 213 ends in 13, so the teen exception selects genitive plural.'),
  ('Compare the supplied hundreds',exception+' Compare the supplied 203 and 213 examples and the two kopeck examples. Count value and sentence construction govern the explanation.'),
  ('Keep lexical forms separate','The isolated table forms below illustrate genitive singular and genitive plural noun forms. They do not by themselves state a quantity or sentence context; apply the complete counted-noun rules to whole examples.'),
  ('Review the complete decision',scope+' '+exception)],
 'u9-s-pattern':[
  ('Identify the counting context',scope+' In these basic examples, a final one outside the teen exception uses nominative singular; final two, three or four use genitive singular.'),
  ('Check the teen exception',exception+' All teen counts in the supplied basic examples take genitive plural.'),
  ('Genitive plural examples','Five and sixteen both use genitive plural in these counting examples. The complete value matters: a compound count ending in one outside the teen exception has a different pattern.'),
  ('A sentence outside the basic pattern','The I saw two friends sentence has an animate accusative object. Both the numeral and noun are declined; do not classify it with the basic nominative/inanimate accusative rule. The one-cat example below instead states existence in the basic nominative context.'),
  ('Review before practice',scope+' '+exception)]}
 for unit in p['units']:
  for session in unit['sessions']:
   if session['id'] not in lessons:continue
   obj=session['objectiveIds'][0];teaching=[s for s in session['steps'] if s['kind']=='TEACH'];assert len(teaching)==len(lessons[session['id']])
   for st,(title,body) in zip(teaching,lessons[session['id']]):
    before=deepcopy(st);st.update(title=title,body=body+(' '+st['body'] if st.get('exampleIds') else ''));changes.append({'before':before,'after':deepcopy(st)});record(title,obj,st['id']+'-title');record(st['body'],obj,st['id']+'-body')
   session['steps']=teaching+[s for s in session['steps'] if s['kind']!='TEACH']
   rem=next(s for s in session['steps'] if s['kind']=='REMEDIATE');before=deepcopy(rem);rem.update(title='Review the complete counted-noun rules',body='\n\n'.join(s['body'] for s in teaching),exampleIds=list(dict.fromkeys(id for s in teaching for id in s.get('exampleIds',[]))));changes.append({'before':before,'after':deepcopy(rem)});record(rem['title'],obj,rem['id']+'-title');record(rem['body'],obj,rem['id']+'-body')
   for st in session['steps']:
    if st['kind'] in ['GUIDED','CHECK']:
     before=deepcopy(st);st['body']='Read the whole example and choose the complete case explanation. Review all teaching and examples after a miss.';changes.append({'before':before,'after':deepcopy(st)});record(st['body'],obj,st['id']+'-body')
 after=(json.dumps(p,ensure_ascii=False,separators=(',',':'))+'\n').encode();PACK.write_bytes(after)
 (HERE/'UNIT9_QUANTITY_RULE_CORRECTION.json').write_text(json.dumps(dict(baselineCommit=BASELINE,beforeSha256=hashlib.sha256(raw).hexdigest(),afterSha256=hashlib.sha256(after).hexdigest(),frozenPublicSource=str(source.relative_to(ROOT)),frozenPublicSourceSha256=hashlib.sha256(source.read_bytes()).hexdigest(),addedProvenanceIds=added,changes=changes,contextIdentities=pins,bibliography=bibliography,semanticAcceptance=False),ensure_ascii=False,indent=2)+'\n')
 print('Corrected15 complete choices and two lessons;',len(added),'pending coverage/context records')
if __name__=='__main__':main()
