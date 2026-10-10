#!/usr/bin/env python3
"""Explain existing Unit 16 form contrasts; preserve keys and Russian assets."""
import hashlib,json
from copy import deepcopy
from pathlib import Path
HERE=Path(__file__).resolve().parent; ROOT=HERE.parents[1]
PACK=ROOT/'app/src/main/assets/content/v2-curriculum.json'
def sha(s):return hashlib.sha256(s.encode()).hexdigest()
def main():
 raw=PACK.read_bytes(); p=json.loads(raw)
 if any(r['itemId']=='prov-ca-u16-worked-inst-sg-teach-1-body' for r in p['provenanceRecords']):
  print('Already repaired; retained evidence unchanged');return
 u=next(u for u in p['units'] if u['number']==16)
 template=next(r for r in p['provenanceRecords'] if r['itemId']=='prov-ca-16-353')
 bibliography=('Cornell Russian Language Program, Instrumental case forms (nouns and adjectives), https://russian.cornell.edu/grammar/html/gr06_a_1_a.htm, primary HTML consulted 2026-10-10 UTC. '
  'Retained Wikibooks Russian/Grammar/Instrumental case oldid 4219602, catalog/sources/units-16-20/wikibooks/instrumental-4219602.txt, supplies noun/adjective rules and examples. '
  'Existing OpenRussian frozen 50e210c4 records supply the exact noun/adjective contrast fields. '
  'Application inference: label existing case/number contrasts and work choices from those retained records; no new Russian form or sentence. Independently authored English 2026-10-10 UTC; final linguistic audit pending.')
 added=[]
 def record(text,obj,name):
  r=deepcopy(template);original=bibliography+'\n'+text
  r.update(itemId='prov-ca-u16-worked-'+name,objectiveId=obj,sourceRecordId='ca-coverage:'+obj+':worked:'+name,recordType='label',originalSourceText=original,shippedText=text,sourceHash=sha(original),contentHash=sha(text))
  r['synthesis'].update(bibliography=bibliography,objectiveOrClaimBinding=obj+':worked:'+name,authorshipDeclaration='Independently authored English for Russian Companion on 2026-10-10 UTC, applying source rules to retained forms; no owned-book prose.',applications=[])
  p['provenanceRecords'].append(r);added.append(r['itemId'])
 lessons={
 'inst-sg':[
  ('One noun, different cases','The instrumental case can mark the means used to do something. These checks first teach its forms. сто́л is the dictionary form, meaning table; столо́м is instrumental singular. The word still names a table: the ending marks its grammatical role, not a new vocabulary meaning.'),
  ('A bounded noun ending','The instrumental singular -ом fits the hard stem of the relevant noun class. Soft stems use their own instrumental ending, so -ом is not universal. Here, keep the stem of сто́л and add -ом to obtain столо́м. Learn the stress with the whole form.'),
  ('Compare the other cases','столу́ is dative singular and столе́ is prepositional singular. Both belong to the same noun, but neither has the instrumental singular ending. For an instrumental singular request, select столо́м rather than selecting a form merely because its English gloss is table.'),
  ('Work the house form','до́м means house in its dictionary form. до́мом is instrumental singular: keep the stem and add -ом. Given до́м and an instrumental singular request, choose до́мом. This is the same ending pattern as the table form.'),
  ('Check case before choosing','до́му is dative singular; до́ме is prepositional singular. They do not answer an instrumental singular request. Compare the entire ending with до́мом, and notice that this noun keeps its stem stress.'),
  ('Meaning and form together','поездом is instrumental singular of the noun meaning train; карандашом is instrumental singular of the noun meaning pencil. The pencil check asks for that meaning as well as the case. Its other options refer to train and husband. A correct case alone does not identify the requested noun.'),
  ('A different singular pattern','книгой is the instrumental singular form for book. This noun has a different ending from the -ом shown for table and house. Do not attach the same ending to every noun. These examples introduce selected singular patterns, not the full instrumental paradigm.')],
 'inst-pl':[
  ('Instrumental plural of book','Instrumental plural refers to more than one. кни́га is the dictionary singular form for book; кни́гами is instrumental plural. Keep the stem and use -ами in this pattern. Given an instrumental plural request, choose кни́гами.'),
  ('Plural noun endings','Instrumental plural nouns commonly use -ами or -ями, with exceptions. The book, house and table forms shown here use -ами. This is a noun ending; adjectives have their own agreement endings. These checks ask you to recognize the displayed pattern, not derive every noun form.'),
  ('Compare plural cases','кни́гам is dative plural; кни́гах is prepositional plural. Neither is instrumental plural. Compare their endings with кни́гами: the final vowel is part of the instrumental plural ending here.'),
  ('Work the house plural','до́м names a house in the dictionary singular. дома́ми is instrumental plural. For this plural case request choose дома́ми, including the stress shown in the retained form.'),
  ('Case and number both matter','дома́м is dative plural. до́мом is instrumental singular. The first has the wrong case; the second has the right case but the wrong number for an instrumental plural request. дома́ми meets both requirements.'),
  ('Work the table plural','сто́л is the dictionary singular form for table. стола́ми is instrumental plural. For an instrumental plural request, select стола́ми and learn its stress with the ending.'),
  ('Reject a singular instrumental','стола́м is dative plural; столо́м is instrumental singular. Neither meets both parts of an instrumental plural request. стола́ми supplies instrumental case and plural number together.')],
 'inst-adj':[
  ('Adjective agreement','An adjective agrees with its noun in case and number, and in gender in the singular. хоро́ший is the masculine dictionary form for good. хоро́шим is its masculine or neuter instrumental singular form. Here the check requests masculine instrumental; it does not ask for an English gloss.'),
  ('Adjective endings differ','Hard-stem masculine and neuter adjectives take -ым in the instrumental, and the soft or spelling-restricted stems take -им. These are adjective declension endings. хоро́шим uses -им because of the spelling restriction after its stem-final consonant. Do not apply the noun ending -ом to this adjective.'),
  ('Compare dative and plural','хоро́шему is dative masculine or neuter singular. хоро́шие is nominative plural. Neither is the masculine instrumental singular form requested here. Work the choice by selecting хоро́шим.'),
  ('Instrumental plural agreement','хоро́шими is instrumental plural, with a plural adjective ending. хоро́шая is feminine nominative singular. For a plural instrumental request select хоро́шими; хоро́шим has singular number. Recognizing these forms does not by itself demonstrate using adjective agreement in a sentence.')]
 }
 changed=[]
 for slug,cards in lessons.items():
  obj='obj-u16-'+slug;s=next(s for s in u['sessions'] if s['id']=='u16-s-'+slug)
  title={'inst-sg':'Recognize instrumental singular nouns','inst-pl':'Recognize instrumental plural nouns','inst-adj':'Recognize instrumental adjective agreement'}[slug]
  s['title']=title;next(o for o in u['objectives'] if o['id']==obj)['title']=title;record(title,obj,slug+'-objective-title')
  for n,(title,body) in enumerate(cards,1):
   st=next(st for st in s['steps'] if st['id']==f'u16-st-teach-{slug}-{n}')
   st.update(title=title,body=body)
   record(title,obj,f'{slug}-teach-{n}-title');record(body,obj,f'{slug}-teach-{n}-body')
  # All relevant explanations must precede the first guided choice.
  teaches=[st for st in s['steps'] if st['kind']=='TEACH'];others=[st for st in s['steps'] if st['kind']!='TEACH'];s['steps']=teaches+others
  rem=next(st for st in s['steps'] if st['kind']=='REMEDIATE');rem.update(title='Review the instrumental contrasts',body='\n\n'.join(body for _,body in cards),exampleIds=list(dict.fromkeys(e for st in teaches for e in st['exampleIds'])))
  for field in ['title','body']:record(rem[field],obj,slug+'-rem-'+field)
  for kind in ['GUIDED','CHECK']:
   st=next(st for st in s['steps'] if st['kind']==kind)
   st['body']='Identify the requested case and number, then compare the displayed forms. A missed check returns to the full explanation and examples.'
   record(st['body'],obj,slug+'-'+kind.lower()+'-body')
 descriptions={
 'u16-g-inst-sg-0':('Which form of the shown noun is instrumental singular?','столо́м is instrumental singular of the noun meaning table.',['столу́ is dative singular, not instrumental singular.','столе́ is prepositional singular, not instrumental singular.']),
 'u16-i-inst-sg-1':('Which form of the shown noun is instrumental singular?','до́мом is instrumental singular of the noun meaning house.',['до́му is dative singular, not instrumental singular.','до́ме is prepositional singular, not instrumental singular.']),
 'u16-i-inst-sg-2':('Which instrumental singular noun means pencil?','карандашом is the instrumental singular form of the noun meaning pencil.',['поездом means train in instrumental singular; it does not identify pencil.','мужем means husband in instrumental singular; it does not identify pencil.']),
 'u16-g-inst-pl-0':('Which form of the shown noun is instrumental plural?','кни́гами is instrumental plural of the noun meaning book.',['кни́гам is dative plural, not instrumental plural.','кни́гах is prepositional plural, not instrumental plural.']),
 'u16-i-inst-pl-1':('Which form of the shown noun is instrumental plural?','дома́ми is instrumental plural of the noun meaning house.',['дома́м is dative plural, not instrumental plural.','до́мом is instrumental singular, not instrumental plural.']),
 'u16-i-inst-pl-2':('Which form of the shown noun is instrumental plural?','стола́ми is instrumental plural of the noun meaning table.',['стола́м is dative plural, not instrumental plural.','столо́м is instrumental singular, not instrumental plural.']),
 'u16-g-inst-adj-0':('Which form of the shown adjective is masculine instrumental singular?','хоро́шим is masculine instrumental singular here; the same form also serves neuter instrumental singular.',['хоро́шему is dative singular, not masculine instrumental.','хоро́шими is instrumental plural, not masculine instrumental singular.']),
 'u16-i-inst-adj-1':('Which form of the shown adjective is plural instrumental?','хоро́шими is plural instrumental of the adjective meaning good.',['хоро́шим is instrumental singular, not plural instrumental.','хоро́шая is feminine nominative singular, not plural instrumental.']),
 'u16-i-inst-adj-2':('Which form of the shown adjective is masculine instrumental singular?','хоро́шим is masculine instrumental singular here; the cue shows the dative form of the same adjective.',['хоро́шая is feminine nominative singular, not masculine instrumental.','хоро́шие is nominative plural, not masculine instrumental.'])}
 for id,(prompt,feedback,distractors) in descriptions.items():
  i=next(i for i in p['items'] if i['id']==id);i.update(prompt=prompt,feedback=feedback,distractorRationales=['']+distractors)
  i['assessment'].update(whyAnswerDemonstratesObjective=feedback,distractorDifference=['']+distractors)
  for text,label in [(prompt,'prompt'),(feedback,'feedback')]+[(text,'distractor-'+str(n)) for n,text in enumerate(distractors,1)]:record(text,i['objectiveId'],id+'-'+label)
  changed.append(id)
 after=(json.dumps(p,ensure_ascii=False,indent=1)+'\n').encode();PACK.write_bytes(after)
 ledger=dict(schemaVersion=1,previousHead='928b9e41b007af5f2005590506cf13f693a4800e',unitNumber=16,disposition='IMPLEMENTED_PENDING_FINAL_AUDIT',beforeCurriculumFileSha256=hashlib.sha256(raw).hexdigest(),afterCurriculumFileSha256=hashlib.sha256(after).hexdigest(),addedProvenanceIds=added,changedItemIds=changed,bibliography=bibliography,scope='Three noun/adjective form sessions, their objective titles, complete remediation and nine item English fields; plural teaching moved before guided practice. Keys, options, Russian cues/assets, assessment source relations and advancement preserved.')
 (HERE/'UNIT16_INSTRUMENTAL_INSTRUCTION_CORRECTION.json').write_text(json.dumps(ledger,ensure_ascii=False,indent=2)+'\n',encoding='utf-8')
 print(f'Unit 16 instruction repaired; {len(added)} pending coverage records; keys and Russian assets unchanged')
if __name__=='__main__':main()
