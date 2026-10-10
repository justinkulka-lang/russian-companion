#!/usr/bin/env python3
"""Teach existing instrumental uses without changing Russian assets or keys."""
import hashlib, json
from copy import deepcopy
from pathlib import Path
HERE=Path(__file__).resolve().parent; ROOT=HERE.parents[1]
PACK=ROOT/'app/src/main/assets/content/v2-curriculum.json'
def sha(text): return hashlib.sha256(text.encode()).hexdigest()
def main():
 raw=PACK.read_bytes(); p=json.loads(raw)
 if any(r['itemId']=='prov-ca-u16-uses-inst-means-teach-1-body' for r in p['provenanceRecords']):
  print('Already repaired; retained evidence unchanged'); return
 u=next(u for u in p['units'] if u['number']==16)
 template=next(r for r in p['provenanceRecords'] if r['itemId']=='prov-ca-16-353')
 bibliography=('Retained Wikibooks Russian/Grammar/Instrumental case oldid 4219602, catalog/sources/units-16-20/wikibooks/instrumental-4219602.txt: means, accompaniment and spatial prepositions. '
  'Cornell Russian Language Program, The preposition s/so; Instrumental vs. Genitive, https://russian.cornell.edu/grammar/html/le41_50_d.htm, primary HTML consulted 2026-10-10 UTC: accompaniment distinction. '
  'Retained Wikipedia Russian grammar oldid 1369617627, catalog/sources/units-26-30/wikipedia/russian-grammar-1369617627.txt: connective-verb complements. '
  'Previously admitted Unit 16 corrected infinitive records supply to become. Application inference: explain the roles and gloss contrasts of existing records; retain the historical became option as an incorrect distractor, not a correct infinitive gloss. Independently authored English 2026-10-10 UTC; final linguistic audit pending.')
 added=[]
 def record(text,obj,name):
  r=deepcopy(template); original=bibliography+'\n'+text
  r.update(itemId='prov-ca-u16-uses-'+name,objectiveId=obj,sourceRecordId='ca-coverage:'+obj+':uses:'+name,recordType='label',originalSourceText=original,shippedText=text,sourceHash=sha(original),contentHash=sha(text))
  r['synthesis'].update(bibliography=bibliography,objectiveOrClaimBinding=obj+':uses:'+name,authorshipDeclaration='Independently authored English for Russian Companion on 2026-10-10 UTC, applying retained source rules; no owned-book prose.',applications=[])
  p['provenanceRecords'].append(r); added.append(r['itemId'])
 lessons={
 'inst-means':('Distinguish means, accompaniment and location',[
  ('Two meanings of with','Я написал хорошим новым карандашом — I wrote with a good new pencil. The pencil is the tool used for writing: this is the means instrumental, without a preposition before the tool phrase. Я хочу встречу с интересной русской женщиной — I want a meeting with an interesting Russian woman. Here with introduces the person involved in the meeting: this is the accompaniment line, using a preposition followed by instrumental forms.'),
  ('Work the role contrast','For a means instrumental request, choose the writing sentence because its pencil is the instrument. For an accompaniment line request, choose the meeting sentence because it describes being with a person. English with can express both roles; its presence alone does not decide which Russian construction is used. Read what the phrase contributes to the event.'),
  ('A separate spatial role','Я живу́ ме́жду двумя но́выми дома́ми — I live between two new houses. This is the between line: the phrase locates the speaker relative to two houses. The houses are neither a writing tool nor a companion. Work the choice by identifying location rather than treating every instrumental phrase as a means phrase.')]),
 'inst-prep':('Recognize selected instrumental preposition meanings',[
  ('Above and below','над — over. под — under. These spatial meanings contrast vertical positions. The over label belongs to the first preposition; the under label belongs to the second. In a location construction, both can govern instrumental forms. The questions here recognize meanings from isolated prepositions; they do not ask you to choose a noun ending.'),
  ('Case depends on the construction','The same preposition can govern different cases in different uses. For the preposition meaning under, instrumental describes location, while accusative can describe a destination under something. Movement within a location does not by itself require accusative: distinguish where something is from where it is going. These isolated-word checks contain no movement context and therefore do not assess that case choice.'),
  ('Before and between','перед — before. между — between. In the spatial use introduced here, before means in front of; it is not a universal translation for every English time expression with before. Between places something relative to two things. When choosing a displayed English label, compare the specific spatial meaning, including the alternative behind. Learn the meaning first; sentence use still requires the appropriate case.')]),
 'complements':('Recognize verbs used with instrumental complements',[
  ('Becoming and remaining','стать means to become. остаться means to remain. Both are infinitives: they name an action or state without marking a past event. Becoming expresses a change; remaining expresses continuation. For the first form choose to become. For the second form choose remain among the displayed choices. The English past-tense word became does not correctly label either infinitive.'),
  ('What a complement describes','An instrumental complement can describe the role or state of the subject after a linking verb, rather than an instrument used to carry out an action. The verbs shown in this lesson can occur in that construction. These checks show only the verbs and ask for their meanings. Recognizing a verb meaning is a first step; it does not demonstrate forming a complete sentence with an instrumental complement.'),
  ('Appearance and discovery','казаться — appear to be. оказаться — turn out to be. The first concerns how something seems; the second concerns what it proves to be. Work the second choice by selecting turn out to be, rather than the meaning of continuation. A displayed infinitive still does not specify a past event. Keep these meanings distinct from becoming and remaining.')])}
 for slug,(sessionTitle,cards) in lessons.items():
  obj='obj-u16-'+slug; s=next(s for s in u['sessions'] if s['id']=='u16-s-'+slug)
  s['title']=sessionTitle; next(o for o in u['objectives'] if o['id']==obj)['title']=sessionTitle; record(sessionTitle,obj,slug+'-objective-title')
  for n,(title,body) in enumerate(cards,1):
   st=next(st for st in s['steps'] if st['id']==f'u16-st-teach-{slug}-{n}')
   st.update(title=title,body=body); record(title,obj,f'{slug}-teach-{n}-title'); record(body,obj,f'{slug}-teach-{n}-body')
  teaches=[st for st in s['steps'] if st['kind']=='TEACH']; s['steps']=teaches+[st for st in s['steps'] if st['kind']!='TEACH']
  rem=next(st for st in s['steps'] if st['kind']=='REMEDIATE')
  rem.update(title='Review meanings and roles',body='\n\n'.join(body for _,body in cards),exampleIds=list(dict.fromkeys(e for st in teaches for e in st['exampleIds'])))
  for field in ['title','body']: record(rem[field],obj,slug+'-rem-'+field)
  for kind in ['GUIDED','CHECK']:
   st=next(st for st in s['steps'] if st['kind']==kind)
   st['body']='Compare the meanings and roles explained above, then choose from the displayed alternatives. A missed check returns to all explanations and examples.'
   record(st['body'],obj,slug+'-'+kind.lower()+'-body')
 descriptions={
 'u16-g-inst-means-0':('Which sentence is the accompaniment line, describing a meeting with a person?','The accompaniment line describes a meeting with a person, not an instrument used to perform an action.',['The sentence about living between two houses describes location, not an accompaniment line.']),
 'u16-i-inst-means-1':('Which sentence uses the means instrumental to name a tool used for an action?','The means instrumental in the writing sentence names the pencil used as the tool.',['The sentence about living between two houses describes location, not a means instrumental naming a tool.']),
 'u16-i-inst-means-2':('Which sentence is the between line, describing a location relative to two houses?','The between line locates the speaker relative to two houses.',['The meeting sentence is an accompaniment line describing a person involved in a meeting, not location between houses.']),
 'u16-g-inst-prep-0':('Which English spatial meaning fits the shown preposition?','The over label identifies the meaning of the shown preposition: above something.',['The under label places something below, not above.','The before label describes being in front of, not above.']),
 'u16-i-inst-prep-1':('Which English spatial meaning fits the shown preposition?','The under label identifies the meaning of the shown preposition: below something.',['The over label places something above, not below.','The behind label places something at the back, not below.']),
 'u16-i-inst-prep-2':('Which English spatial meaning fits the shown preposition?','The before label identifies the spatial meaning of the shown preposition: in front of something.',['The over label places something above, not in front of.','The between label places something relative to two things, not in front of.']),
 'u16-g-complements-0':('Which English meaning fits the shown infinitive?','стать means to become: the infinitive expresses a change without marking a past event.',['to remain expresses continuation, not a change of state.','to turn out to be expresses what something proves to be, not becoming.']),
 'u16-i-complements-1':('Which English meaning fits the shown infinitive?','остаться means to remain; choose remain for continuation of a state. It belongs to the connective verbs introduced here.',['became is an English past-tense word and does not correctly label the shown infinitive.','turn out to be expresses what something proves to be, not continuation.']),
 'u16-i-complements-2':('Which English meaning fits the shown infinitive?','оказаться means to turn out to be: it describes what something proves to be.',['became is an English past-tense word and does not correctly label the shown infinitive.','remain expresses continuation of a state, not what something proves to be.'])}
 changed=[]
 for id,(prompt,feedback,distractors) in descriptions.items():
  i=next(i for i in p['items'] if i['id']==id); i.update(prompt=prompt,feedback=feedback,distractorRationales=['']+distractors)
  i['assessment'].update(whyAnswerDemonstratesObjective=feedback,distractorDifference=['']+distractors)
  for text,label in [(prompt,'prompt'),(feedback,'feedback')]+[(text,'distractor-'+str(n)) for n,text in enumerate(distractors,1)]:record(text,i['objectiveId'],id+'-'+label)
  changed.append(id)
 after=(json.dumps(p,ensure_ascii=False,indent=1)+'\n').encode(); PACK.write_bytes(after)
 ledger=dict(schemaVersion=1,previousHead='aeb39cad1826c38567d6ae2a42011e8a49aced94',unitNumber=16,disposition='IMPLEMENTED_PENDING_FINAL_AUDIT',beforeCurriculumFileSha256=hashlib.sha256(raw).hexdigest(),afterCurriculumFileSha256=hashlib.sha256(after).hexdigest(),addedProvenanceIds=added,changedItemIds=changed,bibliography=bibliography,scope='Three instrumental use sessions and objective titles, teaching-before-guided order, complete remediation, nine item English fields. Keys, Russian assets, source relationships and advancement unchanged. Preposition prompts ask for meaning without supplying the English answer.')
 (HERE/'UNIT16_INSTRUMENTAL_USES_CORRECTION.json').write_bytes((json.dumps(ledger,ensure_ascii=False,indent=2)+'\n').encode())
 print(f'Unit 16 uses repaired; {len(added)} pending coverage records; keys and Russian assets unchanged')
if __name__=='__main__':main()
