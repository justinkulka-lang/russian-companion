#!/usr/bin/env python3
"""Complete Unit25 name and register instruction using the hash-bound offline storage codec."""
import hashlib,json,sys
from pathlib import Path
from copy import deepcopy
ROOT=Path(__file__).resolve().parents[2];sys.path.insert(0,str(ROOT/'catalog'))
from curriculum_storage import loads,dumps
PACK=ROOT/'app/src/main/assets/content/v2-curriculum.json'
def sha(s):return hashlib.sha256(s.encode()).hexdigest()
def main():
 raw=PACK.read_bytes();p=loads(raw)
 if any(r['itemId']=='prov-ca-u25-instruction-name-structure-objective' for r in p['provenanceRecords']):print('Already repaired');return
 template=next(r for r in p['provenanceRecords'] if r['itemId']=='prov-ca-17-72');added=[];seen={}
 bibliography='Retained licensed name and courtesy spans; frozen personal-name and address revisions; existing Gramota names-dictionary research synthesis consulted 2026-09-29. Independently authored English 2026-10-11; review pending. Current Gramota pages returned 403; no fresh-source verification claimed.'
 def record(text,obj,name):
  if (obj,text) in seen:return seen[(obj,text)]
  r=deepcopy(template);original=bibliography+'\n'+text
  r.update(itemId='prov-ca-u25-instruction-'+name,unitNumber=25,objectiveId=obj,sourceRecordId='ca-coverage:'+obj+':'+name,recordType='label',originalSourceText=original,shippedText=text,sourceHash=sha(original),contentHash=sha(text),attribution='Russian Companion independently authored content-audit synthesis, 2026-10-11. No owned-book prose.')
  r['synthesis'].update(bibliography=bibliography,objectiveOrClaimBinding=obj+':'+name,authorshipDeclaration='Independently authored English for Russian Companion, 2026-10-11; no owned-book prose.');r['synthesis'].pop('applications',None)
  p['provenanceRecords'].append(r);added.append(r['itemId']);seen[(obj,text)]=r['itemId'];return r['itemId']
 lessons={
 'name-structure':('Distinguish the parts of a Russian personal name',[
 ('First name, patronymic and family name','''имя means first name. отчество means patronymic name. фамилия means family name. A patronymic identifies the father-derived part of a name; it is not the family name. These labels identify three name components rather than three alternative words for the same component.'''),
 ('A suffix is not a whole name category','''The retained introductory pattern describes a father's first name with ович, meaning son of, or овна, meaning daughter of. These are suffix labels, not whole words meaning first name, patronymic name or family name. They illustrate common patronymic patterns; they are not an exhaustive formation rule for every name.'''),
 ('Read the two-part worked example','''Василий Иванович shows a first name followed by a patronymic. The surname is not shown. The older note For instance is a source fragment, not a translation of the name. A complete three-component name also has a family name. The choices recognize these components and do not ask you to construct a new patronymic.''')]),
 'diminutives':('Recognize conventional short forms of the supplied names',[
 ('Full names and the names used in the checks','''Александр is Alexander; Алексей is Alexei; Дмитрий is Dmitri. The checked conventional pairs are Alexander with Sasha, Alexei with Alyosha and Dmitri with Dima. A short form is a conventional personal-name form; it is not obtained by simply translating the full name into English.'''),
 ('Use the right conventional pair','''Sasha belongs to the supplied Alexander pair, Alyosha to the Alexei pair and Dima to the Dmitri pair. The other checked short forms belong to different full names in these examples. The retained list also gives Misha for Mikhail, Lena for Elena, Olya for Olga, Natasha for Natalya and Katya for Ekaterina. One name can have several documented forms.'''),
 ('Recognition is not automatic name formation','''These checks use English transliterations of documented pairs. They do not test inventing a Russian spelling or adding a suffix to an unfamiliar name. Short and affectionate names can carry social meaning. Use a documented form and consider how the person wishes to be addressed rather than assuming every familiar form suits every situation.''')]),
 'courtesy':('Distinguish a request, thanks and an apology',[
 ('Request and thanks have different functions','''Пожа́луйста means please in the supplied request use. Спаси́бо means thanks or thank you. A request asks for something; thanks responds with gratitude. The checks use these functions rather than treating all courtesy words as interchangeable.'''),
 ('An apology is not thanks','''Извини́те means excuse me with polite or plural address. Извини́ is the familiar singular form. In the checked descriptions, excuse me, polite or plural identifies the first form; excuse me, familiar singular identifies the second. Neither is the thank you response.'''),
 ('Read the address distinction','''The ending in Извини́те permits polite address to one person or address to more than one person. It does not mean that every use has several listeners. The checked contrast identifies the supplied form, not a rule that a familiar apology is suitable for every relationship. The pronunciation label Pozhalusta in the older source is not another English meaning of please.''')]),
 'name-question':('Recognize a name question and the start of its reply',[
 ('Ask for a name','''Как Вас зову́т? means What is your name? This supplied formula uses polite or plural address. The capital letter is a written courtesy convention; it does not create a different spoken pronoun. The question asks for a name rather than for an apology or a general report of how someone is doing.'''),
 ('Begin the reply and then give the name','''Меня́ зову́т means My name is... It starts the name reply and must be followed by a name to give the requested information. The check recognizes that reply opening; it does not treat the fragment alone as a completed personal introduction.'''),
 ('Distinguish the other conversational functions','''Прошу́ проще́ния means I'm sorry / excuse me (formal), an apology. Как дела́? means How are you?, another question. Neither supplies the name requested by Как Вас зову́т? The matching reply opening is Меня́ зову́т. Use the conversational function of each formula rather than choosing another question.''')]),
 'nickname-rule':('Classify the documented name forms and their stated coloring',[
 ('Explain the Russian classification labels','''Марюня is classified here as от полной формы имени, meaning from the full form of the name. Машенька is classified as от краткой формы, meaning from the short form. The retained examples relate these to Мария and Маша respectively. These are documented examples, not a rule for generating a derivative from any full or short name.'''),
 ('Distinguish derivation from social coloring','''Сашка is classified in this material as оттенок пренебрежения, meaning a shade of dismissiveness. That label describes social coloring; the two labels about full and short forms describe derivational bases. A form can have a base and social coloring at the same time. The question asks for the classification documented for this example, not mutually exclusive properties of every name.'''),
 ('Keep the coloring claim within its context','''The retained text associates diminutive forms with the suffix -к- with dismissive coloring. Treat this as the source's classification of its examples, not proof that every use of every such name is insulting. Relationship, tone and setting affect interpretation. A documented label does not authorize inventing a new personal-name form or addressing someone that way without regard to their preference.''')]),
 'address':('Distinguish name-form categories and the illustrated register',[
 ('Full and short forms','''Ирина is classified as полные имена, meaning full names. Ира is classified as краткие имена, meaning short names. These are forms of the supplied personal name, not different family names. A category identifies the form; it does not by itself settle what is courteous in every conversation.'''),
 ('An affectionate form is another category','''Ирочка is classified as уменьшительно-ласкательные, meaning diminutive or affectionate forms. The full-name category полные имена and the short-name category краткие имена differ from this affectionate category. The examples recognize conventional forms; they do not license mechanical suffix formation for an unfamiliar name.'''),
 ('Read the supplied register example without overgeneralizing','''For address by полному имени, meaning the full name, the source describes отстранённо-официального регистра, a detached formal register. It also discusses use in an official or полуофициальной обстановке, a semi-official setting; short forms are described in дружески-неофициальном общении, friendly informal communication. The check asks for the stated register, not every possible setting where a full name occurs. This example does not mean full first names are always formal, or that they always replace a patronymic in formal address. Relationship, setting and the person's preference matter.''')]),
 'documented-name-form':('Recognize documented name forms without inventing new ones',[
 ('Conventional forms have documented bases','''Familiar, short, affectionate, and diminutive personal-name forms are conventional and socially marked. Марюня is the supplied documented derivative of the full name; Машенька is the supplied documented derivative of the short form. These English descriptions classify the examples already studied. They are not instructions to create an unfamiliar name.'''),
 ('Several forms and a stated coloring','''One personal name may have several documented forms. Сашка carries documented dismissive coloring in the supplied material. That description concerns social coloring, while documented derivative of the full name and documented derivative of the short form concern derivational bases. Tone and relationship can affect actual use; the classification does not prove every utterance is dismissive.'''),
 ('Verify a form before treating it as acceptable','''A suffix may add affectionate, familiar, diminutive, or dismissive coloring, but it does not mechanically generate an acceptable form for every name. For an unstudied personal name, the checked answer is unsupported until a names dictionary documents that form. The alternative the suffix mechanically generates an acceptable name overclaims what a suffix rule establishes. These checks recognize documented classifications and the verification limit; they do not assess free productive name formation.''')])}
 u=next(u for u in p['units'] if u['number']==25)
 for slug,(title,cards) in lessons.items():
  obj='obj-u25-'+slug;s=next(s for s in u['sessions'] if s['objectiveIds']==[obj]);s['title']=title;next(o for o in u['objectives'] if o['id']==obj)['title']=title;record(title,obj,slug+'-objective')
  teaches=[st for st in s['steps'] if st['kind']=='TEACH'];assert len(teaches)==len(cards)
  for n,(st,(t,b)) in enumerate(zip(teaches,cards),1):st.update(title=t,body=b);record(t,obj,slug+f'-teach-{n}-title');record(b,obj,slug+f'-teach-{n}-body')
  s['steps']=teaches+[st for st in s['steps'] if st['kind']!='TEACH']
  rem=next(st for st in s['steps'] if st['kind']=='REMEDIATE');rem.update(title='Review the complete name or courtesy distinction',body='\n\n'.join(b for _,b in cards),exampleIds=list(dict.fromkeys(e for st in teaches for e in st.get('exampleIds',[]))))
  record(rem['title'],obj,slug+'-rem-title');record(rem['body'],obj,slug+'-rem-body')
  for st in s['steps']:
   if st['kind'] in ('GUIDED','CHECK'):st['body']='Use the explanation and the supplied example to identify the requested name form, meaning or register. A missed check returns to the full explanation.';record(st['body'],obj,slug+'-'+st['kind'].lower())
 prompts={
 'name-structure':'Which Russian label names the component described by the cue?',
 'diminutives':'Which English short form corresponds to the full name in the cue?',
 'courtesy':'Which expression performs the courtesy function in the cue?',
 'name-question':'Which Russian formula performs the conversational function in the cue?',
 'nickname-rule':'Which classification describes this documented example?',
 'address':'Which name-form category describes this example?',
 'documented-name-form':'Which English classification describes this documented example?'}
 details={'имя':'the first-name label','отчество':'the patronymic-name label','фамилия':'the family-name label','ович':'the retained son-of suffix label','овна':'the retained daughter-of suffix label','Sasha':'the supplied short form for Alexander','Alyosha':'the supplied short form for Alexei','Dima':'the supplied short form for Dmitri','Пожа́луйста':'please in the supplied request use','Спаси́бо':'thanks or thank you','Извини́те':'excuse me with polite or plural address','excuse me, polite or plural':'the polite-or-plural apology description','excuse me, familiar singular':'the familiar-singular apology description','thank you':'gratitude rather than an apology','Как Вас зову́т?':'the name question','Меня́ зову́т':'the reply opening followed by a name','Прошу́ проще́ния':'a formal apology','Как дела́?':'a question asking how someone is doing','от полной формы имени':'derivation from the full form of the name','от краткой формы':'derivation from the short form','оттенок пренебрежения':'a shade of dismissiveness in the supplied classification','полные имена':'the full-name category','краткие имена':'the short-name category','уменьшительно-ласкательные':'the diminutive or affectionate category','отстранённо-официального регистра':'the stated detached formal register','дружески-неофициальном общении':'friendly informal communication','полуофициальной обстановке':'a semi-official setting rather than the requested register label','documented derivative of the full name':'a documented full-name derivational base','documented derivative of the short form':'a documented short-form derivational base','documented dismissive coloring':'the social coloring documented for the supplied example','unsupported until a names dictionary documents that form':'the required documentation limit for an unfamiliar form','the suffix mechanically generates an acceptable name':'an unsupported claim of automatic acceptable formation'}
 changed=[]
 for i in p['items']:
  if not i['objectiveId'].startswith('obj-u25-'):continue
  slug=i['objectiveId'].removeprefix('obj-u25-');oldwhy=i['assessment']['whyAnswerDemonstratesObjective'];cue=i['promptLetter'];answer=i['answer']
  feedback=oldwhy+'\n'+f'{cue} is the cue; {answer} matches the requested function or classification: '+details[answer]+'.'
  wrong=['']+[f'{o}: {details[o]}. This differs from the classification or conversational function requested here. '+i['distractorRationales'][n] for n,o in enumerate(i['options']) if n>0]
  i.update(feedback=feedback,distractorRationales=wrong);i['assessment'].update(whyAnswerDemonstratesObjective=feedback,distractorDifference=wrong)
  for text,label in [(feedback,'feedback')]+[(w,'wrong-'+str(n)) for n,w in enumerate(wrong[1:])]:record(text,i['objectiveId'],i['id']+'-'+label)
  prompt=prompts[slug]
  if i['id']=='u25-g-name-form-1':prompt='Which English classification describes the shown documented name form?'
  if i['id']=='u25-i-name-form-1':prompt='Which English description classifies this documented personal-name example?'
  if i['id']=='u25-i-name-form-2':prompt='Which English classification matches this supplied personal-name example?'
  if i['id']=='u25-i-courtesy-2':prompt=i['prompt']
  if i['id']=='u25-i-name-question-2':prompt='Which formula begins an answer to this name question?'
  if i['id']=='u25-i-address-3':prompt='Which register does the supplied full-name address example illustrate?'
  if i['id']=='u25-i-name-form-3':prompt=i['prompt']
  if prompt!=i['prompt']:i['prompt']=prompt;record(prompt,i['objectiveId'],i['id']+'-prompt')
  changed.append(i['id'])
 after=dumps(p).encode();PACK.write_bytes(after)
 ledger=dict(schemaVersion=1,previousHead='571232f002df7937a4ed743df45fe7b4d875f494',unitNumber=25,disposition='IMPLEMENTED_PENDING_FINAL_AUDIT',beforeCurriculumFileSha256=hashlib.sha256(raw).hexdigest(),afterCurriculumFileSha256=hashlib.sha256(after).hexdigest(),addedProvenanceIds=added,changedItemIds=changed,changedExampleIds=[],bibliography=bibliography,scope='Seven sessions/21 complete cards before guided/full remediation; English name categories, conventional pairs, request/thanks/apology and address/register limits. Answer-disclosing name-form questions replaced by neutral classifications. All existing keys/options/cues/source capabilities/mappings/examples and every original provenance retained.')
 (ROOT/'audit/content-corrections/UNIT25_NAME_INSTRUCTION_CORRECTION.json').write_text(json.dumps(ledger,ensure_ascii=False,indent=2)+'\n');print(len(added),'pending records;',len(changed),'items;bytes',len(after))
if __name__=='__main__':main()
