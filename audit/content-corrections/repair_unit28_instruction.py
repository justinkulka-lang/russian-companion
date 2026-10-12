#!/usr/bin/env python3
"""Bounded Unit28 instruction repair; retained examples and assessment keys unchanged."""
import sys,json,hashlib,subprocess
from pathlib import Path
from copy import deepcopy
ROOT=Path(__file__).resolve().parents[2]
sys.path.insert(0,str(ROOT/'catalog'))
from curriculum_storage import loads,dumps
PACK=ROOT/'app/src/main/assets/content/v2-curriculum.json'
PREFIX='prov-ca-u28-instruction-'
BIB='Retained Russian grammar revision 1369617627, existing hash-bound Unit28 contextual research applications, and retained public-domain Wikisource revision 5336197. Independently authored English explanation, 2026-10-12. No private book prose, new Russian, or general productive formation algorithm. Pending final linguistic audit.'
LESSONS={
'obj-u28-imperfective-adverb':[
('Read two actions happening together','An imperfective verbal adverb describes an accompanying action. In the supplied читая письмо, он улыбался, he was reading the letter while he smiled. Reading and smiling overlap: simultaneous means during the same interval as the main predicate. The main verb places this example in the past; читая does not independently mean absolute present time. The retained examples думая and учась illustrate imperfective verbal-adverb forms. An isolated form cannot establish the tense of an absent main verb.'),
('Keep the two actions with the same person','In читая письмо, он улыбался, The pronoun he is the explicit main-clause subject. The reader and the person smiling are the same person. The verbal adverb supplies an accompanying action by that subject, not an action by a second unrelated person. Identify the main verb and its subject before deciding who performs the accompanying action. These checks recognize timing and shared subject in the supplied construction; they do not demonstrate forming an unfamiliar verbal adverb or judging every possible literary use.')],
'obj-u28-perfective-adverb':[
('Read the completed action before the main action','A perfective verbal adverb normally presents an action completed before the main action in this taught construction. Anterior means earlier relative to that action. The retained сказав and научившись illustrate these forms; their labels alone do not fix an absolute past tense. Read the accompanying main predicate to establish the sentence time.'),
('Earlier does not necessarily mean past','In написав письмо, она отправит его завтра, she will send the letter tomorrow after writing it. The writing precedes the future sending: anterior to a future main action is the matching description. The sentence does not require the writing to have happened before the present moment. The pronoun she names the main subject, and the writer and sender are the same person. An absolute present label loses the relative timing; assigning a different subject loses the supplied construction.'),
('Recognize the retained form without inventing a formation rule','The retained войдя illustrates another perfective verbal-adverb form with an action normally anterior to its main predicate. It does not by itself identify a main subject or an absolute sentence tense. Compare the supplied forms and read their complete contexts when available. This small example set supplies recognition practice, not a complete productive algorithm for unlisted verbs.')],
'obj-u28-readings':[
('Follow the sequence from writing to rest','In the retained reading, устав писать introduces having grown tired of writing. The same person then ложился на диван, lay down on the sofa, and отдыхал читая, rested while reading. Becoming tired of writing precedes the rest; reading accompanies the resting. Writing and reading are distinct actions. For the supplied resting episode, choose the phrase about tiredness from writing rather than the folded-arms posture.'),
('Separate standing posture from the resting episode','The other supplied fragment joins стоит, stands, with сложа руки, with arms folded. It describes the standing posture, not the earlier resting while reading. For стоит, select сложа руки. For устав писать, the supplied main action is ложился на диван. For отдыхал читая, the earlier circumstance in that resting episode is устав писать. These are passage-specific relationships. The fragments alone do not establish that every episode has the same actor, or a general rule for producing the fixed expression.')]
}
def digest(s):return hashlib.sha256(s.encode()).hexdigest()
def repair(p):
 if any(r['itemId'].startswith(PREFIX) for r in p['provenanceRecords']):return p,None
 u=next(u for u in p['units'] if u['number']==28)
 template=next(r for r in p['provenanceRecords'] if r['itemId']=='prov-ca-17-72')
 added=[];changed=[];caps=[]
 def record(text,obj,label):
  r=deepcopy(template);original=BIB+'\n'+text
  r.update(itemId=PREFIX+label,unitNumber=28,objectiveId=obj,sourceRecordId='ca-coverage:'+obj+':'+label,originalSourceText=original,shippedText=text,sourceHash=digest(original),contentHash=digest(text),attribution='Russian Companion independently authored English instruction, 2026-10-12; retained examples; no book prose.')
  r['synthesis'].update(bibliography=BIB,authorshipDeclaration='Independent English instruction, 2026-10-12. No new Russian or copied book expression.',objectiveOrClaimBinding=obj+':'+label)
  r['synthesis'].pop('applications',None);p['provenanceRecords'].append(r);added.append(r['itemId'])
 for obj,cards in LESSONS.items():
  s=next(s for s in u['sessions'] if s['objectiveIds']==[obj]);teaching=[t for t in s['steps'] if t['kind']=='TEACH'];assert len(teaching)==len(cards)
  for t,(title,body) in zip(teaching,cards):t.update(title=title,body=body)
  s['steps']=teaching+[t for t in s['steps'] if t['kind']!='TEACH']
  rem=next(t for t in s['steps'] if t['kind']=='REMEDIATE');rem.update(title='Review the complete timing and reading contrast',body='\n\n'.join(b for _,b in cards),exampleIds=list(dict.fromkeys(e for t in teaching for e in t.get('exampleIds',[]))))
  for t in s['steps']:
   if t['kind'] in ['GUIDED','CHECK']:t['body']='Interpret the supplied sentence or passage. A missed choice returns to every teaching card and worked example.'
  for n,text in enumerate(dict.fromkeys(v for t in s['steps'] for v in [t['title'],t['body']])):record(text,obj,obj+'-lesson-'+str(n))
 # Replace source-layout questions with passage-specific meaning questions; retain every answer and cue.
 prompts={'u28-g-readings-0':'Which main action follows becoming tired of writing in this passage?','u28-i-readings-1':'Which phrase describes the standing posture in this passage?','u28-i-readings-2':'Which earlier circumstance belongs to this resting-and-reading episode?'}
 feedback={'u28-g-readings-0':'The person became tired of writing and lay down on the sofa. Folded arms belong to the standing fragment, not this main action.','u28-i-readings-1':'The person stands with arms folded. Resting while reading belongs to the other episode.','u28-i-readings-2':'The person had grown tired of writing before lying down and resting while reading. The folded-arms posture belongs to the standing fragment.'}
 for i in p['items']:
  if i['id'] in prompts:
   explanation=i['assessment']['whyAnswerDemonstratesObjective'].replace('The admitted line keeps', 'The passage keeps')+' '+feedback[i['id']];i.update(prompt=prompts[i['id']],feedback=explanation);i['assessment']['whyAnswerDemonstratesObjective']=explanation;changed.append(i['id'])
   record(i['prompt'],i['objectiveId'],i['id']+'-prompt');record(i['feedback'],i['objectiveId'],i['id']+'-feedback')
 for obj in ['obj-u28-imperfective-adverb','obj-u28-perfective-adverb']:
  cap=next(c for c in p['sourceCapabilities'] if c['id']=='cap-'+obj);old=deepcopy(cap)
  hosts=[r for r in p['provenanceRecords'] if r['sourceRecordId']==cap['sourceRecordId'] and r['sourceHash']==cap['sourceHash']];assert len(hosts)==23
  apps=hosts[0]['synthesis']['applications'];assert all(r['synthesis']['applications']==apps for r in hosts)
  s=next(s for s in u['sessions'] if s['objectiveIds']==[obj]);corpus=cap['exactSpan']+'\n'+BIB+'\n'+'\n'.join(t['body'] for t in s['steps'] if t['kind'] in ['TEACH','REMEDIATE'])+'\n'+json.dumps(apps,ensure_ascii=False)
  cap.update(exactSpan=corpus,sourceHash=digest(corpus));ids={}
  for n,h in enumerate(hosts):
   r=deepcopy(h);r.update(itemId=PREFIX+obj+'-host-'+str(n),originalSourceText=corpus,sourceHash=digest(corpus));r['synthesis']['bibliography']=BIB;p['provenanceRecords'].append(r);ids[h['itemId']]=r['itemId'];added.append(r['itemId'])
  for i in p['items']:
   if i['objectiveId']==obj:
    for key in ['relationParticipantIds','answerSourceIds','distractorSourceIds']:i['assessment'][key]=[ids.get(v,v) for v in i['assessment'][key]]
  caps.append({'previous':old,'updatedSha256':cap['sourceHash']})
 return p,dict(addedProvenanceIds=added,changedItemIds=changed,capabilities=caps)
if __name__=='__main__':
 before=PACK.read_bytes();p,changes=repair(loads(before))
 if changes:
  encoded=dumps(p).encode();PACK.write_bytes(encoded)
  pins=ROOT/'app/src/main/java/dev/russiancompanion/v2/curriculum/ContentAuditCapabilityPins.kt';text=pins.read_text()
  for c in changes['capabilities']:text=text.replace(c['previous']['sourceHash'],c['updatedSha256'])
  pins.write_text(text)
  ledger=dict(schemaVersion=1,previousHead=subprocess.check_output(['git','rev-parse','HEAD'],text=True).strip(),disposition='IMPLEMENTED_PENDING_FINAL_AUDIT',bibliography=BIB,beforeCurriculumFileSha256=hashlib.sha256(before).hexdigest(),afterCurriculumFileSha256=hashlib.sha256(encoded).hexdigest(),**changes)
  (ROOT/'audit/content-corrections/UNIT28_INSTRUCTION_CORRECTION.json').write_text(json.dumps(ledger,ensure_ascii=False,indent=2)+'\n');print(len(changes['addedProvenanceIds']),'pending records')
 else:print('Already repaired')
