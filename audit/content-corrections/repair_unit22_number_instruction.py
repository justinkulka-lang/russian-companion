#!/usr/bin/env python3
"""Complete Unit22 number instruction while keeping source/answer admission pending."""
import hashlib,json
from pathlib import Path
from copy import deepcopy
ROOT=Path(__file__).resolve().parents[2]
PACK=ROOT/'app/src/main/assets/content/v2-curriculum.json'
def sha(s):return hashlib.sha256(s.encode()).hexdigest()
def main():
 raw=PACK.read_bytes();p=json.loads(raw)
 if any(r['itemId']=='prov-ca-u22-noun-forms-objective' for r in p['provenanceRecords']):print('Already repaired');return
 template=next(r for r in p['provenanceRecords'] if r['itemId']=='prov-ca-17-72');added=[];seen={}
 bibliography='Retained licensed numeral spans; Cornell Beginning Russian Grammar gr11_d_1.htm, le51_56_b_num.htm, gr11_d_3.htm; Gramota Pismovnik numeral usage. Consulted 2026-10-11; independently authored English; review pending.'
 def record(text,obj,name):
  if (obj,text) in seen:return seen[(obj,text)]
  r=deepcopy(template);original=bibliography+'\n'+text
  r.update(itemId='prov-ca-u22-'+name,unitNumber=22,objectiveId=obj,sourceRecordId='ca-coverage:'+obj+':'+name,recordType='label',originalSourceText=original,shippedText=text,sourceHash=sha(original),contentHash=sha(text))
  r['synthesis'].update(bibliography=bibliography,sourceDateOrVersion='2026-10-08',objectiveOrClaimBinding=obj+':'+name,authorshipDeclaration='Independently authored English for Russian Companion, 2026-10-11; no owned-book prose.',applications=[])
  r['attribution']='Russian Companion independently authored content-audit synthesis, 2026-10-11. No owned-book prose.'
  p['provenanceRecords'].append(r);added.append(r['itemId']);seen[(obj,text)]=r['itemId'];return r['itemId']
 lessons={
 'noun-forms':('Connect quantity to gender and noun case',[
 ('One agrees with its noun','один мальчик — one boy. одна девушка — one girl. The numeral meaning one agrees with its noun in gender, number and case. These displayed counting phrases use a singular noun: the masculine boy takes the masculine numeral, and the feminine girl takes the feminine numeral. The guided choice asks for another phrase with one, not the same noun.'),
 ('The scope of the counting rule','For nominative counting phrases, one takes a singular noun, two through four take a genitive singular noun, and five through twenty take a genitive plural noun. Apply this shortcut to an accusative phrase only where its forms coincide with the nominative. Animate accusative phrases and other cases need their own case forms; the shortcut is not a rule that all number phrases keep these endings.'),
 ('Two, three and four form a group','две девушки — two girls. три кошки — three cats. Both displayed nouns are genitive singular after these numerals, although the quantity is greater than one and these endings can resemble nominative plural. The numeral for two has a feminine form; the form for three does not change for the noun gender. The check asks for another example in the two-through-four group, not an English translation of the cue.'),
 ('Five and higher in the displayed group','пять девушек — five girls. десять копеек — ten kopeks. Here the nouns are genitive plural. Five and ten belong to the same noun-form group; one and two do not. Numbers whose last two digits are 11 through 14 also use this group, including 111 through 114. These choices recognize supplied number phrases; they do not test independently producing every noun ending.')]),
 'compounds':('Read the final number component with the teen exception',[
 ('Three endings in compound quantities','тридцать три рюмки — thirty-three shot glasses. сорок один год — forty-one years. In these nominative counting phrases, 33 ends in three and takes a genitive singular noun; 41 ends in one and takes a singular noun. The choice may ask for a different final-digit group from the displayed reference phrase. Read the requested group in the question.'),
 ('Check the last two digits first','For these whole-number counting phrases, check the last two digits before the final digit. Endings 11 through 14 require genitive plural. Otherwise a final one takes singular, final two through four take genitive singular, and final zero or five through nine take genitive plural. For example, 111 differs from 41; 113 differs from 33. These are numeric rule comparisons, not newly supplied Russian phrases. In other cases the numeral and noun need the relevant declined forms.'),
 ('Nine and the requested category','шестьдесят девять слов — sixty-nine words. The final nine places 69 in the genitive-plural group. In the three choices, сорок один год ends in one, тридцать три рюмки ends in three, and шестьдесят девять слов ends in nine. Identify the category requested by the question. A displayed 41 phrase does not make the answer to a question about nine a translation or a same-quantity counterpart.')]),
 'declension':('Distinguish dative, instrumental and prepositional numeral forms',[
 ('A changed form can name a different case','двум — Дательный, the dative. двумя — Творительный, the instrumental. Both are declined forms of the numeral meaning two. Dative commonly marks a recipient or goal; instrumental can mark accompaniment or means. The final part of the form distinguishes these two shown examples; it is not enough to recognize the quantity two.'),
 ('Read a row in its case context','Дательный means dative; Творительный means instrumental; Предложный means prepositional. A form must be interpreted with its construction. The bare form двух can occur in more than one case. The admitted row includes the preposition and is labelled prepositional; the check does not claim that a bare ending always uniquely identifies a case. In oblique number phrases the plural noun normally carries the case required by the construction.'),
 ('The displayed prepositional row','(о) двух / (об) обоих / (о) полутора — Предложный. This complete row gives the prepositional forms for two, both and one and a half. The parentheses show the accompanying preposition; it is not part of the numeral itself. The prepositional choice contrasts with the dative двум and instrumental двумя. Recognizing this row does not demonstrate independently declining a complete number phrase.')]),
 'both-forms':('Connect both to gender and case',[
 ('Dative both has two gender forms','обеим — Дательный. обоим — Дательный. Both forms mean both in the dative. The feminine series has the stem seen in обеим; the masculine/neuter series has the stem seen in обоим. Gender refers to the counted noun. Changing the gender series does not change the case: both displayed forms are dative.'),
 ('Meaning, gender and the dative row','Both refers to the full pair, rather than an unspecified quantity of two. The retained nominative table distinguishes masculine/neuter and feminine forms. The dative row двум / обеим / полутора places two, feminine both and one and a half together by case, not by equal meaning. Дательный is dative, Творительный is instrumental, and Предложный is prepositional.'),
 ('Instrumental adds a different case contrast','обеими — Творительный. обоими — Творительный. The feminine and masculine/neuter series remain distinct, but both shown forms are instrumental. Compare обеим with обеими and обоим with обоими: the extra final vowel distinguishes these dative and instrumental examples. The current choices check the case of a supplied form; they do not test producing the correct gender-and-case form in a new sentence.')]),
 'half-forms':('Explain gender, case and the noun after one and a half',[
 ('One and a half changes for gender','полтора́ рубля́ — 1.5 roubles. полторы́ мину́ты — 1.5 minutes. The first numeral form is masculine/neuter; the second is feminine. In the nominative and accusative construction, one and a half takes a genitive singular noun. The noun determines the numeral gender even though the amount exceeds one. The retained alternative gloss one and a half hours / an hour and a half names hours, not roubles or minutes.'),
 ('The oblique form does not fix the noun ending','In the other cases, the numeral has the shared form полутора, and the noun is plural in the case required by the construction. The numeral form alone therefore cannot tell you whether a phrase is genitive, dative or instrumental. Compare the preposition and the noun ending. This differs from the genitive singular noun after the nominative/accusative forms.'),
 ('From, to and with have different constructions','из полу́тора рубле́й — from 1.5 roubles: the preposition requires genitive plural on the noun. к полу́тора рубля́м — to 1.5 roubles: the preposition requires dative plural. The English distractor with 1.5 roubles names accompaniment/means with an instrumental noun in the corresponding retained source construction. The amount stays one and a half while the preposition changes the relationship. The check asks for the meaning of the whole shown phrase, including from or to.')]),
 'collective':('Distinguish cardinal, ordinal and collective number forms',[
 ('Two and three as collective quantities','два — дво́е. три — тро́е. A cardinal states a quantity; a collective presents that quantity as a group. An ordinal states position in a sequence. Thus дво́е is the collective for two, and тро́е is the collective for three. The option второ́й means second, and тре́тий means third; these are ordinal positions, not collective quantities.'),
 ('Collectives have restricted uses','Collective number forms are not a general replacement for cardinal numbers with every noun. Standard uses include groups of male people, children or people expressed by appropriate group nouns, personal pronouns, and certain nouns used only in the plural. Use ordinary cardinals with female-person nouns in standard usage; do not infer that any feminine noun takes a collective. This lesson explains the restriction without introducing new Russian noun combinations.'),
 ('Four and the limits of these checks','четы́ре — че́тверо. The collective form keeps the quantity four while presenting it as a group. The ordinal distractor четвёртый means fourth, a position; дво́е is a group of two. In the earlier check, че́тверо is a collective but has the wrong quantity for three. Selecting the collective column does not demonstrate choosing its use with every noun or constructing a declined collective phrase.')])}
 u=next(u for u in p['units'] if u['number']==22)
 for slug,(title,cards) in lessons.items():
  obj='obj-u22-'+slug;s=next(s for s in u['sessions'] if s['id']=='u22-s-'+slug)
  s['title']=title;next(o for o in u['objectives'] if o['id']==obj)['title']=title;record(title,obj,slug+'-objective')
  teaches=[st for st in s['steps'] if st['kind']=='TEACH'];assert len(teaches)==len(cards)
  for n,(st,(t,b)) in enumerate(zip(teaches,cards),1):
   st.update(title=t,body=b);record(t,obj,slug+f'-teach-{n}-title');record(b,obj,slug+f'-teach-{n}-body')
  s['steps']=teaches+[st for st in s['steps'] if st['kind']!='TEACH']
  rem=next(st for st in s['steps'] if st['kind']=='REMEDIATE');rem.update(title='Review the complete number contrast',body='\n\n'.join(b for _,b in cards),exampleIds=list(dict.fromkeys(e for st in teaches for e in st['exampleIds'])))
  record(rem['title'],obj,slug+'-rem-title');record(rem['body'],obj,slug+'-rem-body')
  for st in s['steps']:
   if st['kind'] in ('GUIDED','CHECK'):
    st['body']='Read the shown form and the requested category. Use the full explanation and examples above. A missed check returns to that complete explanation.';record(st['body'],obj,slug+'-'+st['kind'].lower())
 prompts={
 'u22-g-noun-forms-0':'Which choice is another counting phrase with one, using the appropriate gender form?',
 'u22-i-noun-forms-1':'Which choice belongs to the same two-through-four noun-form group as the shown phrase?',
 'u22-i-noun-forms-2':'Which choice belongs to the same five-through-twenty noun-form group as the shown phrase?',
 'u22-g-compounds-0':'Use the shown phrase as a reference. Which choice has a final number component of one?',
 'u22-i-compounds-1':'Use the shown phrase as a reference. Which choice has a final number component of nine?',
 'u22-i-compounds-2':'Use the shown phrase as a reference. Which choice has a final number component of three?'}
 notes={
 'noun-forms':{'один мальчик':'quantity one with a masculine numeral and a singular noun','одна девушка':'quantity one with a feminine numeral and a singular noun','две девушки':'quantity two with a feminine numeral and a genitive singular noun','три кошки':'quantity three with a genitive singular noun','пять девушек':'quantity five with a genitive plural noun','десять копеек':'quantity ten with a genitive plural noun'},
 'compounds':{'сорок один год':'41 ends in one and has a singular noun','тридцать три рюмки':'33 ends in three and has a genitive singular noun','шестьдесят девять слов':'69 ends in nine and has a genitive plural noun'},
 'declension':{'Дательный':'dative, represented by двум','Творительный':'instrumental, represented by двумя','Предложный':'prepositional, represented by the full row with its prepositions'},
 'both-forms':{'Дательный':'dative, represented by обеим and обоим','Творительный':'instrumental, represented by обеими and обоими','Предложный':'prepositional, a different row from the displayed dative or instrumental forms'},
 'half-forms':{'1.5 roubles':'a money quantity without a from/to/with relationship','1.5 minutes':'a time quantity measured in minutes','one and a half hours / an hour and a half':'a time quantity measured in hours','from 1.5 roubles':'the from relationship with a genitive plural noun','to 1.5 roubles':'the to relationship with a dative plural noun','with 1.5 roubles':'the with relationship with an instrumental plural noun'},
 'collective':{'дво́е':'a collective quantity of two','тро́е':'a collective quantity of three','че́тверо':'a collective quantity of four','второ́й':'the ordinal position second','тре́тий':'the ordinal position third','четвёртый':'the ordinal position fourth'}}
 changed=[]
 for i in p['items']:
  if not i['objectiveId'].startswith('obj-u22-'):continue
  slug=i['objectiveId'].removeprefix('obj-u22-');cue=i['promptLetter'];answer=i['answer'];oldwhy=i['assessment']['whyAnswerDemonstratesObjective']
  prompt=prompts.get(i['id'],'Which case is the complete shown form or row?' if slug in ('declension','both-forms') else 'Which English meaning matches the whole shown phrase?' if slug=='half-forms' else 'Which choice is the collective form with the same quantity as the shown cardinal?')
  detail=notes[slug][answer]
  if slug=='compounds': explanation=f'{answer}: {detail}. The question requests this final component, not the quantity or translation of the reference {cue}.'
  elif slug=='noun-forms': explanation=f'{answer} has {detail}. It shares the requested noun-form group with {cue}; the nouns themselves need not be the same.'
  else:explanation=f'{cue} matches {answer}: {detail}.'
  feedback=oldwhy+'\n'+explanation
  wrong=['']+[f'{o}: {notes[slug][o]}. It does not match the category or whole-phrase meaning requested here.' for o in i['options'][1:]]
  i.update(prompt=prompt,feedback=feedback,distractorRationales=wrong);i['assessment'].update(whyAnswerDemonstratesObjective=feedback,distractorDifference=wrong)
  for text,label in [(prompt,'prompt'),(feedback,'feedback')]+[(w,'wrong-'+str(n)) for n,w in enumerate(wrong[1:])]:record(text,i['objectiveId'],i['id']+'-'+label)
  changed.append(i['id'])
 omittedSteps=0;omittedApps=0
 for unit in p['units']:
  for session in unit['sessions']:
   for st in session['steps']:
    for k in ['unavailableReason','bookDirection','companionDirection','remediationStepId']:
     if k in st and st[k] is None:del st[k];omittedSteps+=1
 for r in p['provenanceRecords']:
  sy=r.get('synthesis')
  if sy and sy.get('applications')==[]:del sy['applications'];omittedApps+=1
 after=(json.dumps(p,ensure_ascii=False,separators=(',',':'))+'\n').encode();PACK.write_bytes(after)
 ledger=dict(schemaVersion=1,previousHead='95a170755ef2cffc9fc63378b67c452579097e46',unitNumber=22,disposition='IMPLEMENTED_PENDING_FINAL_AUDIT',beforeCurriculumFileSha256=hashlib.sha256(raw).hexdigest(),afterCurriculumFileSha256=hashlib.sha256(after).hexdigest(),addedProvenanceIds=added,changedItemIds=changed,omittedNullStepDefaults=omittedSteps,omittedEmptyApplicationDefaults=omittedApps,bibliography=bibliography,scope='Six sessions/19 complete cards before practice/full remediation; noun case and gender, last-two-digit teen exception, case ambiguity, both, half and bounded collective usage. All existing Russian cues/options/keys, licensed provenance, source capabilities and mappings preserved.')
 (ROOT/'audit/content-corrections/UNIT22_NUMBER_INSTRUCTION_CORRECTION.json').write_text(json.dumps(ledger,ensure_ascii=False,indent=2)+'\n');print(len(added),'pending records;',omittedSteps,'null step defaults;',omittedApps,'empty application defaults; bytes',len(after),'request bytes',len(json.dumps(dict(repository_full_name='justinkulka-lang/russian-companion',encoding='utf-8',content=after.decode()),ensure_ascii=False).encode()))
if __name__=='__main__':main()
