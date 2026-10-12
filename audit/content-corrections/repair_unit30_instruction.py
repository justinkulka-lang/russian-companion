#!/usr/bin/env python3
"""Explain retained Unit30 examples without changing assessments or licensed spans."""
import sys,json,hashlib,subprocess
from pathlib import Path
from copy import deepcopy
ROOT=Path(__file__).resolve().parents[2];sys.path.insert(0,str(ROOT/'catalog'))
from curriculum_storage import loads,dumps
PACK=ROOT/'app/src/main/assets/content/v2-curriculum.json';PREFIX='prov-ca-u30-instruction-'
BIB='Retained Russian alphabet revision 1371795481, Russian grammar revision 1369617627, Wikibooks revision 3353858, morphology revision 148939075, licensed ReadyRussian reading5 and existing hash-bound Unit30 dictionary research corpus. Independently authored English instruction, 2026-10-12. No private book prose, new Russian or universal productive algorithm. Pending final linguistic audit.'
LESSONS={'u30-s-letter-names': [('Separate a letter name from an approximate sound cue',
                         'The retained а has the English name respelling ah. The retained бэ has be/beh. '
                         'These respellings guide recognition of the supplied letter names; they are not '
                         'words naming an object or an English translation of a Russian sentence.'),
                        ('A comparison word is not the name respelling',
                         'The retained alphabet row uses father as an approximate English sound comparison '
                         'for а. Choose father when asked for that comparison word, but ah when asked for '
                         'the name respelling. The alternatives bad and both are different comparison words, '
                         'and be/beh belongs to the other supplied name. English comparisons depend on '
                         'accent and are approximate; these text checks do not demonstrate listening or '
                         'pronunciation.')],
 'u30-s-abbrev-examples': [('Recognize the two supplied country abbreviations',
                            'The retained СССР corresponds to USSR and США to USA. Choose the English '
                            'country abbreviation matching the Russian form. These are two admitted '
                            'examples, not a complete list or a productive rule for pronouncing every '
                            'Russian abbreviation.'),
                           ('A heading describes the group rather than one country',
                            'Abbreviations is the category heading for the supplied examples, not the '
                            'English gloss of СССР or США. One retained check asks specifically for that '
                            'heading; its answer is Abbreviations rather than USSR or USA. Distinguish a '
                            'question about category from a question about the meaning of an individual '
                            'example.')],
 'u30-s-address-marks': [('Identify the city mark in its line',
                          'In the supplied 115114 г. Москва, the mark identifies the city. The digits are '
                          'the postal code and the following name the city; the city mark does not name the '
                          'street or building. Interpret the mark within the address line rather than '
                          'treating every number as a building number.'),
                         ('Read the first mark without ignoring the later marks',
                          'In ул. Петрова, д. 14, кв. 15, the first mark identifies the street. The line '
                          'then gives a building and an apartment. A question about the first mark therefore '
                          'selects street rather than city or apartment. The individual address fields have '
                          'different roles even when they share one printed line.'),
                         ('Distinguish building from the other address fields',
                          'The retained д. is glossed building. In the longer supplied line it precedes 14; '
                          'the apartment field is separate. Choose building for this supplied mark rather '
                          'than city or street. This small set checks recognition of retained marks, not the '
                          'production of a complete unfamiliar postal address or acceptance of an '
                          'owned-edition mapping.')],
 'u30-s-abbreviation': [('Read the retained category as a source grouping',
                         'The source groups letter-name abbreviations and sound abbreviations ending in a '
                         'vowel together under буквенные аббревиатуры и звуковые аббревиатуры на гласную. '
                         'Its examples include СНГ and ЭВМ. This combined label does not say that every '
                         'member is pronounced as a single word. The passage discusses their treatment as '
                         'indeclinable forms.'),
                        ('Keep the supplied vowel-ending examples in that group',
                         'The retained ГАИ and гороно are also included under the combined label in this '
                         'passage. Select that supplied grouping when asked about гороно here. The separate '
                         'phrase сложносокращённые слова names another group in the passage; do not replace '
                         'the retained source grouping simply because a form is shortened. These examples do '
                         'not establish an exhaustive taxonomy of all shortened words.'),
                        ('Compare the consonant-ending abbreviation and its two uses',
                         'The supplied в МИД and в МИДе illustrate the source group звуковые аббревиатуры на '
                         'согласную, sound abbreviations ending in a consonant. One retains the abbreviation '
                         'unchanged and the other has a case ending. The source permits declined and '
                         'undeclined use here. Its category contrasts with the combined '
                         'letter-name/vowel-ending grouping.'),
                        ('Do not infer a universal ending rule from this list',
                         'The category названия населённых пунктов refers to place names and is separate '
                         'from the abbreviation groups in this exercise. These checks classify retained '
                         'examples and the source explicitly allows both treatments for the consonant-ending '
                         'example. They do not justify declining every consonant-ending abbreviation or '
                         'predicting an unlisted abbreviation from spelling alone.')],
 'u30-s-particle': [('Recognize a request or wish rather than reported past action',
                     'The supplied Пусть он читает expresses let him read, and Да будет мир! expresses let '
                     'there be peace. The source classifies these as Повелительное наклонение, imperative '
                     'mood, using particles. These examples express a request or wish rather than reporting '
                     'that the action has already happened.'),
                    ('Read the possibility meaning of the supplied conditional forms',
                     'The retained читал бы and читали бы are grouped under предположительность или '
                     'возможность, supposition or possibility. The particle combines with the supplied verb '
                     'form to present a conditional or hypothetical meaning; the past-shaped form alone '
                     'should not make this a report of an actual completed reading event. An English '
                     'translation using would depends on context.'),
                    ('Keep the two functions separate',
                     'Choose the imperative label for the two let examples and the supposition/possibility '
                     'label for the supplied conditional example. This is recognition of the retained '
                     'constructions. It does not demonstrate forming every imperative, deciding every use of '
                     'a particle, or assigning one universal meaning to that particle outside these '
                     'examples.')],
 'u30-s-capstone': [('Follow the rescue before the second return',
                     'In the retained story a woman reports that a two-year-old girl remains in the burning '
                     'house. Bob, the dog sent by the firefighters, returns carrying the girl. The supplied '
                     'нёс де́вочку belongs to that rescue, before the later discovery involving a doll. The '
                     'mother is glad her daughter is alive; the passage does not describe the rescue itself '
                     'as the comic discovery.'),
                    ('Connect the laughter to what the dog carries next',
                     'After the rescue Bob goes back into the house and returns carrying a large doll, '
                     'expressed by несла́ большу́ю ку́клу. When the people recognize it, все расхохота́лись '
                     'describes their laughter. The female grammatical form refers to the dog in that part '
                     'of the narration, not the rescued girl carrying the doll. Match the reaction to the '
                     'doll discovery and keep the earlier rescue separate. These checks interpret the '
                     'supplied story, not an unfamiliar passage.')],
 'u30-s-dictionary': [('Use headword forms and sense together',
                       'The retained entry for стол identifies a noun, gives стол, стола, столу, столом, '
                       'столе as forms and states the sense a piece of furniture with a flat top. Thus '
                       'столом and the noun form in на столе belong to the furniture entry. Книга лежит на '
                       'столе. uses that furniture sense in its example. The form row helps locate the '
                       'entry; it does not by itself state the sense without the sense field.'),
                      ('A grammatical label alone cannot supply the sense',
                       'The retained стоять entry is a verb with стоять, стою, стоит, стоял and the sense '
                       'remaining upright on a base. Ваза стоит на столе. illustrates that standing action. '
                       'Use the sense field to distinguish стоит from the furniture noun. The label сущ. '
                       'identifies a part of speech; it does not state the furniture sense. When a check '
                       'shows only a label or a row of forms, select the stated information limit rather '
                       'than guessing that it contains a definition. These checks use two supplied entries, '
                       'not an unrestricted external dictionary or a claim of completed book work.')]}
def digest(s):return hashlib.sha256(s.encode()).hexdigest()
def repair(p):
 if any(r['itemId'].startswith(PREFIX) for r in p['provenanceRecords']):return p,None
 u=next(u for u in p['units'] if u['number']==30);template=next(r for r in p['provenanceRecords'] if r['itemId']=='prov-ca-17-72');added=[]
 for sid,cards in LESSONS.items():
  s=next(s for s in u['sessions'] if s['id']==sid);obj=s['objectiveIds'][0];ts=[t for t in s['steps'] if t['kind']=='TEACH'];assert len(ts)==len(cards)
  for t,(title,body) in zip(ts,cards):t.update(title=title,body=body)
  s['steps']=ts+[t for t in s['steps'] if t['kind']!='TEACH'];rem=next(t for t in s['steps'] if t['kind']=='REMEDIATE');rem.update(title='Review the complete retained-example contrast',body='\n\n'.join(b for _,b in cards),exampleIds=list(dict.fromkeys(e for t in ts for e in t.get('exampleIds',[]))))
  for t in s['steps']:
   if t['kind'] in ['GUIDED','CHECK']:t['body']='Use the explanation and supplied examples. A missed choice restores every teaching card and worked example.'
  for n,text in enumerate(dict.fromkeys(v for t in s['steps'] for v in [t['title'],t['body']])):
   r=deepcopy(template);original=BIB+'\n'+text;label=sid+'-'+str(n);r.update(itemId=PREFIX+label,unitNumber=30,objectiveId=obj,sourceRecordId='ca-coverage:'+obj+':'+label,originalSourceText=original,shippedText=text,sourceHash=digest(original),contentHash=digest(text),attribution='Russian Companion independently authored English explanation, 2026-10-12; retained examples; no book prose.');r['synthesis'].update(bibliography=BIB,authorshipDeclaration='Independent English instruction, 2026-10-12. No new Russian or copied book expression.',objectiveOrClaimBinding=obj+':'+label);r['synthesis'].pop('applications',None);p['provenanceRecords'].append(r);added.append(r['itemId'])
 return p,added
if __name__=='__main__':
 before=PACK.read_bytes();p,added=repair(loads(before))
 if added:
  after=dumps(p).encode();PACK.write_bytes(after);ledger=dict(previousHead=subprocess.check_output(['git','rev-parse','HEAD'],text=True).strip(),disposition='IMPLEMENTED_PENDING_FINAL_AUDIT',bibliography=BIB,addedProvenanceIds=added,beforeSha256=hashlib.sha256(before).hexdigest(),afterSha256=hashlib.sha256(after).hexdigest());(ROOT/'audit/content-corrections/UNIT30_INSTRUCTION_CORRECTION.json').write_text(json.dumps(ledger,ensure_ascii=False,indent=2)+'\n');print(len(added),'pending records')
