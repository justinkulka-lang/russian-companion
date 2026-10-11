#!/usr/bin/env python3
"""Repair English instruction around retained Unit 19 forms; leave source keys untouched."""
import hashlib, json
from copy import deepcopy
from pathlib import Path
HERE=Path(__file__).resolve().parent; ROOT=HERE.parents[1]
PACK=ROOT/'app/src/main/assets/content/v2-curriculum.json'
def sha(s): return hashlib.sha256(s.encode()).hexdigest()
def main():
 raw=PACK.read_bytes(); p=json.loads(raw)
 if any(r['itemId']=='prov-ca-u19-instruction-conditional-objective' for r in p['provenanceRecords']):
  print('Already repaired; retained evidence unchanged'); return
 u=next(u for u in p['units'] if u['number']==19)
 template=next(r for r in p['provenanceRecords'] if r['itemId']=='prov-ca-17-72')
 bibliography=('Retained Unit19 Wikipedia grammar, Wikibooks obligation and Wiktionary dolzhen revision84965088. Primary Cornell grammar le87_94_d.htm, gr02_c_1.htm and le95_102_c.htm, https://russian.cornell.edu/grammar/html/, consulted2026-10-11: conditional, agreement and obligation. Independent English synthesis; linguistic review pending.')
 added=[]
 def record(text,obj,name):
  r=deepcopy(template); original=bibliography+'\n'+text
  r.update(itemId='prov-ca-u19-instruction-'+name,unitNumber=19,objectiveId=obj,sourceRecordId='ca-coverage:'+obj+':instruction:'+name,recordType='label',originalSourceText=original,shippedText=text,sourceHash=sha(original),contentHash=sha(text))
  r['synthesis'].update(bibliography=bibliography,objectiveOrClaimBinding=obj+':instruction:'+name,authorshipDeclaration='Independently authored English for Russian Companion, 2026-10-11 UTC; no owned-book prose.',applications=[])
  p['provenanceRecords'].append(r); added.append(r['itemId'])
 lessons={'conditional': ('Interpret conditional structure and speaker agreement',
                 [('The past form with a conditional particle',
                   'я спал бы is used by a male speaker; я спала́ бы is used by a female speaker. These labels '
                   'describe the speaker marked by the past-form agreement; they are not translations of the whole '
                   'expressions. Both use the first-person pronoun, a past-form verb and the conditional particle. In '
                   'this construction the past form need not report an actual past event: it can express a '
                   'hypothetical situation, such as I would sleep. Context supplies the time reference.'),
                  ('Read agreement rather than an English sentence label',
                   'Compare the two retained forms я спал бы and я спала́ бы. The feminine past form has a final vowel '
                   'that the masculine form lacks. For the first choose a male speaker; for the second choose a female '
                   'speaker. I would like to sleep is a sentence meaning, not a label identifying the gender marked by '
                   'the shown past form. Speaker agreement and sentence meaning are different questions.'),
                  ('A wish and a different position for the particle',
                   'я бы поспа́л — I would like to sleep. я бы поспала́ has feminine speaker agreement: the speaker is '
                   'marked feminine. The first-person pronoun identifies the speaker, rather than another person. '
                   'These retained wish expressions have the conditional particle before the verb, whereas the earlier '
                   'examples put it after the verb. It cannot begin its clause. The wish reading comes from the '
                   'construction and context, not from translating every conditional particle as would like. These '
                   'choices do not demonstrate independently forming a conditional sentence or an aspect choice.')]),
 'obligation': ('Interpret obligation and subject agreement',
                [('An obligation for a masculine speaker',
                  'я должен — I have to. This retained phrase expresses obligation for a masculine singular speaker. '
                  'The short adjective agrees with the subject, and an infinitive can name the required action. The '
                  'subject is nominative. The shown phrase does not name an action, so do not infer a particular task '
                  'from it.'),
                 ('Feminine, neuter and plural agreement',
                  'должна — If the subject is feminine. должно — If the subject is neuter. должны — If the subject is '
                  'plural. These retained English labels describe agreement; they are not literal translations of each '
                  'Russian word. Select the feminine label for the feminine form and the plural label for the plural '
                  'form. The short adjective changes with the subject, while the infinitive naming the action does not '
                  'acquire a gender ending.'),
                 ('Separate an agreement label from a meaning',
                  'I have to is the meaning of the retained first-person phrase. If the subject is feminine and If the '
                  'subject is plural are labels describing which form to use. A question asking for an agreement label '
                  'differs from one asking what the whole phrase means. Obligation is one use of this adjective; an '
                  'expected-event reading is taught separately. These selections do not demonstrate independently '
                  'producing an obligation statement or using all tense forms.')]),
 'probability': ('Distinguish expected events from obligation',
                 [('An expected eclipse',
                   "Затме́ние должно́ наступи́ть в три часа́. — The eclipse should occur at three o'clock. Here should "
                   'expresses an expected event, not a duty imposed on the eclipse. The short adjective agrees with '
                   'the neuter singular subject; the following infinitive names the event. The retained source '
                   'description is (with a verb) Used to express likelihood. Usually translated as "should".'),
                  ('A duty and the limits of the current checks',
                   'Слу́жащие должны́ во́время явля́ться на рабо́ту. — Employees must report to work on time. This '
                   'example states an obligation for plural employees rather than predicting an eclipse. Meaning comes '
                   'from context as well as the shared adjective-plus-infinitive structure. In the likelihood '
                   'selection choose the eclipse example. For its full English meaning choose the entire sentence, not '
                   'the dictionary usage label (with a verb). Both independent checks currently repeat this same '
                   'eclipse sentence; recognizing it twice does not demonstrate broader sense discrimination or '
                   'independent sentence production.')])}
 for slug,(title,cards) in lessons.items():
  obj='obj-u19-'+slug; s=next(s for s in u['sessions'] if s['id']=='u19-s-'+slug)
  s['title']=title; next(o for o in u['objectives'] if o['id']==obj)['title']=title; record(title,obj,slug+'-objective')
  for n,(ct,body) in enumerate(cards,1):
   st=next(st for st in s['steps'] if st['id']==f'u19-st-teach-{slug}-{n}');st.update(title=ct,body=body)
   record(ct,obj,slug+f'-teach-{n}-title');record(body,obj,slug+f'-teach-{n}-body')
  teaches=[st for st in s['steps'] if st['kind']=='TEACH'];s['steps']=teaches+[st for st in s['steps'] if st['kind']!='TEACH']
  rem=next(st for st in s['steps'] if st['kind']=='REMEDIATE');rem.update(title='Review the complete contrast',body='\n\n'.join(body for _,body in cards),exampleIds=list(dict.fromkeys(e for st in teaches for e in st['exampleIds'])))
  record(rem['title'],obj,slug+'-rem-title');record(rem['body'],obj,slug+'-rem-body')
  for kind in ['GUIDED','CHECK']:
   st=next(st for st in s['steps'] if st['kind']==kind);st['body']=('Read the shown form and select the requested English speaker description using the agreement explained above.' if slug=='conditional' else 'Read the shown form and select its English meaning or subject-agreement label as requested.' if slug=='obligation' else 'Compare the expected-event and employee-obligation examples, then select the requested example or complete English meaning.')+' A missed check returns to the complete explanation and examples.';record(st['body'],obj,slug+'-'+kind.lower())
 explanations={'u19-g-conditional-0': ('a male speaker is marked by the masculine past form, not supplied as the sentence '
                         'translation.',
                         ['a female speaker would require feminine past agreement.',
                          'I would like to sleep is a sentence meaning rather than a speaker-agreement label.']),
 'u19-i-conditional-1': ('a female speaker is marked by the feminine past form; the pronoun identifies the speaker.',
                         ['a male speaker would require masculine past agreement.',
                          'I would like to sleep is a sentence meaning rather than a speaker-agreement label.']),
 'u19-i-conditional-2': ('The first-person pronoun identifies the speaker; the feminine past form marks feminine '
                         'agreement.',
                         ['the speaker is marked masculine would require masculine past agreement.',
                          'the sentence is about another person does not match the first-person pronoun.']),
 'u19-g-obligation-0': ('The retained first-person phrase expresses obligation with masculine singular agreement.',
                        ['If the subject is feminine is an agreement label rather than the meaning of this '
                         'first-person phrase.',
                         'If the subject is plural is an agreement label rather than the meaning of this first-person '
                         'phrase.']),
 'u19-i-obligation-1': ('This is the retained subject-agreement label for the feminine form, not its literal '
                        'translation.',
                        ['I have to is a phrase meaning rather than the requested subject-agreement label.',
                         'If the subject is plural names plural agreement rather than feminine singular agreement.']),
 'u19-i-obligation-2': ('This is the retained subject-agreement label for the plural form, not its literal '
                        'translation.',
                        ['I have to is a phrase meaning rather than the requested subject-agreement label.',
                         'If the subject is feminine names feminine singular agreement rather than plural agreement.']),
 'u19-g-probability-0': ('The eclipse example predicts an expected event.',
                         ['Слу́жащие должны́ во́время явля́ться на рабо́ту. states an employee obligation rather than '
                          'the expected eclipse.']),
 'u19-i-probability-1': ('The complete sentence gives an expected eclipse and its time.',
                         ['(with a verb) Used to express likelihood. Usually translated as "should". is a dictionary '
                          'sense description, not the full sentence meaning.']),
 'u19-i-probability-2': ('The complete sentence gives an expected eclipse and its time.',
                         ['(with a verb) is a dictionary usage label, not the full sentence meaning.'])}
 changed=[]
 for i in p['items']:
  if i['id'] not in explanations:continue
  explanation,distractors=explanations[i['id']]
  i['prompt']=('Choose the English speaker description marked by the shown form.' if i['id'] in ['u19-g-conditional-0','u19-i-conditional-1'] else 'Choose the English subject-agreement label for the shown form.' if i['id'] in ['u19-i-obligation-1','u19-i-obligation-2'] else i['prompt'] if i['id'] in ['u19-i-conditional-2','u19-g-probability-0'] else 'Which English meaning matches the shown form?');i['feedback']='Choose '+i['answer']+(' ' if i['answer'].endswith('.') else '. ')+explanation;i['distractorRationales']=['']+distractors
  i['assessment']['whyAnswerDemonstratesObjective']+='\n'+i['feedback'];i['assessment']['distractorDifference']=['']+distractors
  for text,label in [(i['prompt'],'prompt'),(i['feedback'],'feedback'),(i['assessment']['whyAnswerDemonstratesObjective'],'rationale')]+[(d,'distractor-'+str(n)) for n,d in enumerate(distractors,1)]:record(text,i['objectiveId'],i['id']+'-'+label)
  changed.append(i['id'])
 after=(json.dumps(p,ensure_ascii=False,separators=(',',':'))+'\n').encode();PACK.write_bytes(after)
 ledger=dict(schemaVersion=1,previousHead='d13c07f2d253bab43e94cde3647699aae5486b0c',unitNumber=19,disposition='IMPLEMENTED_PENDING_FINAL_AUDIT',beforeCurriculumFileSha256=hashlib.sha256(raw).hexdigest(),afterCurriculumFileSha256=hashlib.sha256(after).hexdigest(),addedProvenanceIds=added,changedItemIds=changed,bibliography=bibliography,scope='Three conditional/obligation/likelihood sessions, eight teaching cards before guided practice and complete remediation; nine item English explanations. Russian forms, answer keys, original evidence and mappings preserved. Prefix caution and duplicate likelihood checks remain pending repair.')
 (HERE/'UNIT19_CONDITIONAL_OBLIGATION_INSTRUCTION_CORRECTION.json').write_text(json.dumps(ledger,ensure_ascii=False,indent=2)+'\n')
 print('Unit 19 repaired;',len(added),'new pending provenance records')
if __name__=='__main__':main()
