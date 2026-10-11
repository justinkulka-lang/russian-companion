#!/usr/bin/env python3
"""Complete Unit24 negation instruction using the hash-bound offline storage codec."""
import hashlib,json,sys
from pathlib import Path
from copy import deepcopy
ROOT=Path(__file__).resolve().parents[2];sys.path.insert(0,str(ROOT/'catalog'))
from curriculum_storage import loads,dumps
PACK=ROOT/'app/src/main/assets/content/v2-curriculum.json'
def sha(s):return hashlib.sha256(s.encode()).hexdigest()
def main():
 raw=PACK.read_bytes();p=loads(raw)
 if any(r['itemId']=='prov-ca-u24-instruction-verbal-ne-objective' for r in p['provenanceRecords']):print('Already repaired');return
 template=next(r for r in p['provenanceRecords'] if r['itemId']=='prov-ca-17-72');added=[];seen={}
 bibliography='Retained licensed negation spans and pending absence corrections; Cornell grammar le71_78_i.htm; Gramota negative-pronoun/NE-NI guidance. Consulted 2026-10-11; independently authored English; review pending.'
 def record(text,obj,name):
  if (obj,text) in seen:return seen[(obj,text)]
  r=deepcopy(template);original=bibliography+'\n'+text
  r.update(itemId='prov-ca-u24-instruction-'+name,unitNumber=24,objectiveId=obj,sourceRecordId='ca-coverage:'+obj+':'+name,recordType='label',originalSourceText=original,shippedText=text,sourceHash=sha(original),contentHash=sha(text),attribution='Russian Companion independently authored content-audit synthesis, 2026-10-11. No owned-book prose.')
  r['synthesis'].update(bibliography=bibliography,objectiveOrClaimBinding=obj+':'+name,authorshipDeclaration='Independently authored English for Russian Companion, 2026-10-11; no owned-book prose.');r['synthesis'].pop('applications',None)
  p['provenanceRecords'].append(r);added.append(r['itemId']);seen[(obj,text)]=r['itemId'];return r['itemId']
 lessons={
 'verbal-ne':('Distinguish a negative verb from a negative noun predicate',[
 ('A speaking reply and its negative','Да, говорю́ — Yes, I\'m speaking. Нет, не говорю́ — No, I\'m not speaking. The first word of each reply answers yes or no. The separate negative particle before the speaking verb negates that predicate; it is not merely the initial no reply. The guided choice keeps the speaking action and changes it to a negative reply.'),
 ('A verb and a zero present copula','To negate a verb, simply add не (nye) before the verb: the shown examples illustrate this ordinary predicate-negation pattern. In a present-tense noun predicate, the verb corresponding to is is normally omitted. The negative particle can then stand before the noun predicate itself. That is a different structure from a speaking verb with a negative particle.'),
 ('The question and the student statement','Вы говори́те? — Are you speaking? Он не студент — He\'s not a student. The speaking question is not itself negated. The student statement has a noun predicate and no overt present-tense is. The check asks for that negative nominal sentence, not another speaking reply.'),
 ('A complete negative coming sentence','Борис не приходит — Boris isn\'t coming. The negative particle precedes the coming verb. This complete sentence identifies Boris as the subject and coming as the action. The options не студент and не говорю́ are shorter fragments about being a student or speaking; they do not express the complete Boris-coming sentence. These choices recognize supplied structures rather than independently constructing every negative predicate.')]),
 'absence':('Distinguish existence, absence and the negative particle',[
 ('Existence and absence are not yes and no replies','есть means exists in the supplied existence pattern. нет expresses absence in its negative counterpart. The same word can also be a no reply, but that conversational use is different from the absence construction. Да is a yes reply. The separate particle не negates a predicate; it is not the standalone replacement for existence in this pattern.'),
 ('The noun after the absence construction','If it isn\'t there, we use нет followed by the genitive. In this construction the person or thing whose existence or possession is denied is expressed in the genitive. Do not replace the existence word with the separate particle and leave the construction unchanged. The checks identify the supplied existence/absence forms, not independently produce a genitive noun phrase.'),
 ('A pronunciation label is not a translation','не — nye in the retained example is a pronunciation/transliteration label, not an English meaning. This particle is different from нет expressing absence and from есть asserting existence. A reference cue can be shown while the question asks for a contrasting form; read whether the question asks for existence, absence or ordinary predicate negation.')]),
 'alternation':('Keep noun number and case distinct under negation',[
 ('The singular and plural examples used by the checks','Я не ви́жу э́ту кни́гу. — I don\'t see this book, singular. Я не ви́жу кни́гу — I don\'t see the book. Я не ви́жу книг. — I don\'t see any books. The first two show a singular accusative object; the last shows a genitive plural object. Negation remains in every sentence. Changing noun form does not turn the statement into a positive one.'),
 ('Genitive is not simply a plural marker',"A negated direct object can be genitive or accusative depending on meaning, context and usage. Indefinite reference often favors genitive, but this is not a mechanical rule that every negative sentence must use it. The older book ending in the retained source can also be genitive singular or accusative plural; it is not unambiguous genitive plural. The worked example now uses the same explicit plural-genitive phrase as the repaired check. I don't see the one book is still a singular description. I see this book and I see the books are positive descriptions and therefore do not match the negated cues. These choices distinguish supplied meanings and number, not independently choose every case under negation.")]),
 'stand-ins':('Distinguish negative pronouns from absence and indefinite forms',[
 ('Nobody and nothing refer to different categories','кто — никто́. что — ничто́. The first pair concerns a person and nobody; the second concerns a thing and nothing. The indefinite options не́кто and не́что refer to an unspecified person or thing, not the corresponding nobody/nothing form. Do not treat a similar-looking prefix as proof of the same meaning.'),
 ('No person available for an action','Negative predicative constructions express the absence of a person or thing for an action. The construction selects a case form; it has no nominative form. Compare the indefinite pronoun separately. не́кого спросить — there is nobody to ask. This is absence of a person available for the infinitive action; it differs from an unspecified person and from there is nothing to ask.'),
 ('The case comes from the construction','не́кто is an indefinite pronoun, a person whose identity is not specified. It is not the predicative negative of кто. Absence of a person uses a case form: не́кого has the same form in the genitive and accusative, while не́кому is dative and не́кем instrumental. The sentence determines the case; absence meaning alone does not. There is no nominative form in this negative paradigm. The current checks recognize the absence meaning; they do not uniquely identify genitive from the shared genitive/accusative form.')]),
 'concord':('Combine negative pronouns with the predicate particle',[
 ('One negative meaning across pronoun and predicate','никто́ — не. ничто́ — не. In the supplied negative-concord pattern, the negative pronoun requires the separate negative particle in the predicate. They work together to express one overall negative statement. Do not remove the predicate particle by applying the English idea that two negatives must cancel.'),
 ('A prefix and a whole pronoun are not the predicate particle','The option ни- is a pronoun prefix, not the separate predicate particle. никому is a declined pronoun referring to nobody in the dative; it is not that particle either. The answer не is the required predicate particle in the illustrated pattern. The choices recognize this supplied concord rule; they do not test producing every negative pronoun case or all exceptional constructions.')]),
 'word-order-contrast':('Interpret the supplied persuasion readings in context',[
 ('Two supplied readings with their stated contexts','Не надо меня уговаривать. — One should not persuade me in the supplied context where the speaker would never agree. Меня не надо уговаривать. — There is no need to persuade me in the supplied context where the speaker will do it anyway. The questions provide these contexts rather than asking you to infer one unique interpretation from word order alone.'),
 ('Word order is not a universal meaning switch','Word order can change what is in focus or in the scope of a particle. It does not by itself assign one unique meaning to every sentence. Intonation and discourse context matter as well. The current source illustrates these two contextual readings; a speaker could use a sentence with a different intended emphasis in another context. The checks recognize the supplied reading, not a universal rule that moving the subject always changes prohibition into lack of necessity.')]),
 'placement':('Distinguish negating the state from negating its experiencer',[
 ('Not cold and not me are different scopes','Мне не холодно. — To me it is not cold. This can also be expressed in English as I am not cold. Не мне холодно. — It is not to me that it is cold. The first negates the cold state for the experiencer; the second contrasts the experiencer, so the person who is cold is not me. The second does not by itself identify that other person.'),
 ('Use the demonstrated contrast with its limits','Note that the modifier не negates the word which comes right after it. In this supplied pair, its position helps distinguish the state from the experiencer. Wider sentences also depend on construction, emphasis and context; do not apply adjacency as an automatic complete interpretation of every Russian sentence. The check asks for the scope shown in this pair, not free production of every negative-focus construction.')])}
 u=next(u for u in p['units'] if u['number']==24)
 for slug,(title,cards) in lessons.items():
  obj='obj-u24-'+slug;s=next(s for s in u['sessions'] if s['id']=='u24-s-'+slug);s['title']=title;next(o for o in u['objectives'] if o['id']==obj)['title']=title;record(title,obj,slug+'-objective')
  teaches=[st for st in s['steps'] if st['kind']=='TEACH'];assert len(teaches)==len(cards)
  for n,(st,(t,b)) in enumerate(zip(teaches,cards),1):st.update(title=t,body=b);record(t,obj,slug+f'-teach-{n}-title');record(b,obj,slug+f'-teach-{n}-body')
  s['steps']=teaches+[st for st in s['steps'] if st['kind']!='TEACH']
  rem=next(st for st in s['steps'] if st['kind']=='REMEDIATE');rem.update(title='Review the complete negation contrast',body='\n\n'.join(b for _,b in cards),exampleIds=list(dict.fromkeys(e for st in teaches for e in st.get('exampleIds',[]))))
  record(rem['title'],obj,slug+'-rem-title');record(rem['body'],obj,slug+'-rem-body')
  for st in s['steps']:
   if st['kind'] in ('GUIDED','CHECK'):st['body']='Read the requested negative meaning, structure or stated context. Use the complete explanation above. A missed check returns to that full explanation.';record(st['body'],obj,slug+'-'+st['kind'].lower())
 items={i['id']:i for i in p['items']};e=next(e for e in p['examples'] if e['id']=='ex-u24-alternation-0');e.update(display=items['u24-i-alternation-2']['promptLetter'],note=items['u24-i-alternation-2']['answer'],sourceId=items['u24-i-alternation-2']['sourceId'])
 prompts={
 'u24-g-verbal-ne-0':'Which complete reply negates the speaking action in the shown affirmative reply?',
 'u24-i-verbal-ne-1':'Which supplied sentence negates a noun predicate with the usual omitted present-tense copula?',
 'u24-i-verbal-ne-2':'Which complete sentence negates Boris\'s coming?',
 'u24-g-absence-0':'Which form expresses absence instead of the shown positive expression?',
 'u24-i-absence-1':'Which form asserts existence instead of the shown negative expression?',
 'u24-i-absence-2':'Which form expresses absence rather than merely negating the next predicate?',
 'u24-g-word-order-contrast-0':'The speaker would never agree. Which supplied English interpretation fits the sentence in that context?',
 'u24-i-word-order-contrast-1':'The speaker will do it anyway. Which supplied English interpretation fits the sentence in that context?',
 'u24-i-word-order-contrast-2':'The speaker would never agree. Which supplied English interpretation fits the sentence in that context?'}
 details={
  'Нет, не говорю́':'a negative first-person speaking reply', 'Он не студент':'a negative noun predicate with an omitted present copula', 'Вы говори́те?':'a speaking question without negation', 'Да, говорю́':'an affirmative speaking reply', 'Борис не приходит':'a complete negative sentence about Boris coming', 'не студент':'a negative student-predicate fragment', 'не говорю́':'a negative first-person speaking fragment',
  'нет':'absence in the existence pattern', 'есть':'existence in the positive pattern', 'не':'the separate predicate-negation particle', 'Да':'a yes reply',
  "I don't see this book, singular":'negation with one specified accusative object', "I don't see the book":'the supplied singular accusative reading', "I don't see any books":'the explicit genitive-plural reading', "I don't see the one book":'a singular-object reading', 'I see this book':'a positive singular-object statement', 'I see the books':'a positive plural-object statement',
  'никто́':'a negative pronoun referring to nobody', 'ничто́':'a negative pronoun referring to nothing', 'не́кто':'an indefinite pronoun referring to an unspecified person', 'не́что':'an indefinite pronoun referring to an unspecified thing', 'there is nobody to ask':'absence of a person available for the infinitive action', 'an unspecified person':'indefinite reference to a person', 'there is nothing to ask':'absence of a thing for the action', 'ни-':'a negative-pronoun prefix', 'никому':'a declined negative pronoun in the dative',
  'One should not persuade me':'the supplied interpretation where the speaker would never agree', 'There is no need to persuade me':'the supplied interpretation where the speaker will do it anyway', 'To me it is not cold.':'negation of the cold state for the experiencer', 'It is not to me that it is cold.':'contrastive negation of the experiencer', 'I am not cold.':'negation of the cold state for the speaker'}
 changed=[]
 for i in p['items']:
  if not i['objectiveId'].startswith('obj-u24-'):continue
  slug=i['objectiveId'].removeprefix('obj-u24-');oldwhy=i['assessment']['whyAnswerDemonstratesObjective'];cue=i['promptLetter'];answer=i['answer'];summary=lessons[slug][1][-1][1]
  feedback=oldwhy+'\n'+f'{cue} is the reference cue; {answer} matches the requested meaning or structure. '+details[answer]+'.'
  wrong=['']+[f'{o}: {details[o]}. This differs from the meaning or structure requested in this context. '+i['distractorRationales'][n] for n,o in enumerate(i['options']) if n>0]
  if i['id']=='u24-i-alternation-1':wrong[1]="I don't see any books is the supplied plural reading, illustrated here by Я не ви́жу книг. It differs from the singular-object reading requested by this cue."
  i.update(feedback=feedback,distractorRationales=wrong);i['assessment'].update(whyAnswerDemonstratesObjective=feedback,distractorDifference=wrong)
  for text,label in [(feedback,'feedback')]+[(w,'wrong-'+str(n)) for n,w in enumerate(wrong[1:])]:record(text,i['objectiveId'],i['id']+'-'+label)
  if i['id'] in prompts:i['prompt']=prompts[i['id']];record(i['prompt'],i['objectiveId'],i['id']+'-prompt')
  changed.append(i['id'])
 after=dumps(p).encode();PACK.write_bytes(after)
 ledger=dict(schemaVersion=1,previousHead='15a4c1dddff226710a427d4d0cf40de41fea466d',unitNumber=24,disposition='IMPLEMENTED_PENDING_FINAL_AUDIT',beforeCurriculumFileSha256=hashlib.sha256(raw).hexdigest(),afterCurriculumFileSha256=hashlib.sha256(after).hexdigest(),addedProvenanceIds=added,changedItemIds=changed,changedExampleIds=['ex-u24-alternation-0'],bibliography=bibliography,scope='Seven sessions/18 complete cards before guided/full remediation; worked example uses already admitted explicit genitive plural; verb/existence/pronoun/concord/scope distinctions and contextual persuasion questions. All existing keys/options/cues/source capabilities/mappings and all original provenance retained. Hash-bound source-text pooling is storage-only.')
 (ROOT/'audit/content-corrections/UNIT24_NEGATION_INSTRUCTION_CORRECTION.json').write_text(json.dumps(ledger,ensure_ascii=False,indent=2)+'\n');print(len(added),'pending records;',len(changed),'items;bytes',len(after))
if __name__=='__main__':main()
