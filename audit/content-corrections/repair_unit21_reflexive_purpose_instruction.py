#!/usr/bin/env python3
"""Repair Unit21 ownership, object and conjunction instruction; retain pending review."""
import hashlib,json
from pathlib import Path
from copy import deepcopy
ROOT=Path(__file__).resolve().parents[2]
PACK=ROOT/'app/src/main/assets/content/v2-curriculum.json'
def sha(s):return hashlib.sha256(s.encode()).hexdigest()
def main():
 raw=PACK.read_bytes();p=json.loads(raw)
 if any(r['itemId']=='prov-ca-u21-svoy-objective' for r in p['provenanceRecords']):
  print('Already repaired');return
 template=next(r for r in p['provenanceRecords'] if r['itemId']=='prov-ca-17-72');added=[]
 bibliography='Retained Wikipedia Russian grammar revision1369617627 possession/reflexive/conjunction spans. Cornell Beginning Russian Grammar le12_21_d.htm (subject reference) and gr11_c_1.htm (purpose with infinitive), https://russian.cornell.edu/grammar/html/, consulted2026-10-11. Independently authored English explanation; linguistic review pending.'
 def record(text,obj,name,original=None,applications=None):
  r=deepcopy(template);original=original or bibliography+'\n'+text
  r.update(itemId='prov-ca-u21-'+name,unitNumber=int(obj.split('-')[1][1:]),objectiveId=obj,sourceRecordId='ca-coverage:'+obj+':'+name,recordType='label',originalSourceText=original,shippedText=text,sourceHash=sha(original),contentHash=sha(text))
  r['synthesis'].update(bibliography=bibliography,objectiveOrClaimBinding=obj+':'+name,authorshipDeclaration='Independently authored English for Russian Companion, 2026-10-11 UTC; no owned-book prose.',applications=applications or [])
  p['provenanceRecords'].append(r);added.append(r['itemId']);return r['itemId']
 def lesson(unit,slug,title,cards,name):
  u=next(u for u in p['units'] if u['number']==unit);obj=f'obj-u{unit}-'+slug;s=next(s for s in u['sessions'] if s['id']==f'u{unit}-s-'+slug)
  s['title']=title;next(o for o in u['objectives'] if o['id']==obj)['title']=title;record(title,obj,name+'-objective')
  teaches=[st for st in s['steps'] if st['kind']=='TEACH'];assert len(teaches)==len(cards)
  for n,(st,(t,b)) in enumerate(zip(teaches,cards),1):
   st.update(title=t,body=b);record(t,obj,name+f'-teach-{n}-title');record(b,obj,name+f'-teach-{n}-body')
  s['steps']=teaches+[st for st in s['steps'] if st['kind']!='TEACH']
  rem=next(st for st in s['steps'] if st['kind']=='REMEDIATE');rem.update(title='Review the complete contrast',body='\n\n'.join(b for _,b in cards),exampleIds=list(dict.fromkeys(e for st in teaches for e in st['exampleIds'])))
  record(rem['title'],obj,name+'-rem-title');record(rem['body'],obj,name+'-rem-body')
  for st in s['steps']:
   if st['kind'] in ('GUIDED','CHECK'):
    st['body']='Read the shown Russian sentence or conjunction and choose the requested meaning or matching object example using the explanation above. A missed check returns to the complete explanation.';record(st['body'],obj,name+'-'+st['kind'].lower())
 lesson(21,'svoy','Connect own possession to the subject and the possessed noun',[
 ('Who owns it and which noun controls the form','The reflexive possessive refers to the subject as the owner. Its gender, number and case agree with the possessed noun, not with the owner. The retained dictionary forms are свой, своя, своё and свои. A masculine owner and a feminine owner can therefore use the same possessive form with the same noun.'),
 ('The movie belongs to the subject',"Бо́ря рассказа́л о своём люби́мом фи́льме. — (Borya told about his favorite movie.). А́нна рассказа́ла о своём люби́мом фи́льме. — (Anna told about her favorite movie.). In each sentence the favorite movie belongs to the subject. The movie noun has the same gender, number and case in both, so the possessive form stays the same. The name and past-verb agreement change. The exercise asks you to preserve that movie object while changing the subject."),
 ('The same own book with two subjects',"Свое́й кни́ги она́ никогда́ не написа́ла. — (She never wrote her own book.). Свое́й кни́ги он никогда́ не написа́л. — (He never wrote his own book.). The possessive agrees with the feminine singular book noun; the shown form is genitive in this negative sentence. Changing the subject changes the past verb's agreement, not the book phrase. These sentences are about a book, not a movie or jeans."),
 ('Plural jeans and the limits of the checks','Бо́ря наде́л свои́ джи́нсы. — (Borya put on his own jeans.). А́нна наде́ла свои́ джи́нсы. — (Anna put on her own jeans.). Plural jeans require plural possessive agreement regardless of whether Borya or Anna owns them. The exercise keeps this object and changes the subject. The checks select supplied matched sentences; they do not demonstrate independently declining the possessive or constructing an ownership sentence.')],'svoy')
 lesson(21,'object-reference','Distinguish oneself from a possessed object',[
 ('The person loved and the person who owns','Я люблю́ себя́ — I love myself. Я люблю́ (свою́) жену́ — I love my wife. In the first sentence the object refers back to the subject. In the second the object is a wife; the parenthesized possessive makes ownership explicit. Loving oneself and loving a wife describe different objects.'),
 ('A whole sentence is more than its object word','жену́ is the accusative form of the noun meaning wife. It does not mean I love. In Я люблю́ свою́ жену́ the verb supplies loving and the noun supplies its object. The full displayed sentence Я люблю́ (свою́) жену́ means I love my wife; the full displayed sentence Я люблю́ себя́ means I love myself. Choose the whole-sentence meaning rather than translating the object word as the verb. These checks recognize two retained sentences; they do not demonstrate independently choosing every reflexive case form.')],'object')
 lesson(21,'purpose','Distinguish purpose, condition, cause and concession',[
 ('Purpose and condition','чтобы — so that. если — if. The first conjunction can introduce a purpose; the second introduces a condition. In a purpose construction with the same subject for both actions, an infinitive after the purpose conjunction can name the intended action. This rule is explained here, but the current choices do not require composing such a sentence.'),
 ('Ordinary purpose wording','чтобы is an ordinary purpose conjunction. The bookish or archaic qualifier in the retained source belongs to дабы, not to чтобы. The English meaning so that differs from the conditional if and the causal because. Select the meaning of the displayed conjunction; do not infer an archaic register for the ordinary form.'),
 ('Cause, concession and time','потому что — because. хотя — although. The first gives a reason; the second introduces a concession. The English label after names a temporal relationship rather than a reason. English since can also express a reason, so it is not a contrasting wrong answer to because in this exercise. The causal check instead contrasts because with although and after. These conjunction meanings are distinct from purpose and condition.')],'purpose')
 items={i['id']:i for i in p['items']};changed=[]
 i=items['u21-i-purpose-2'];i['options'][1]='although';a=i['assessment'];a['contrastMembers']=i['options'];a['distractorSourceIds'][1]='prov-ex-u21-purpose-3-en';a['relationParticipantIds'][2]='prov-ex-u21-purpose-3-en'
 explanations={
 'u21-g-svoy-0': ('Which Russian sentence keeps the movie object while changing the subject?',"Бо́ря рассказа́л о своём люби́мом фи́льме. changes to А́нна рассказа́ла о своём люби́мом фи́льме. The movie phrase retains its agreement; the subject and past verb change.",['Свое́й кни́ги она́ никогда́ не написа́ла. concerns a book, not the movie object.','Бо́ря наде́л свои́ джи́нсы. concerns jeans and keeps Borya as subject.']),
 'u21-i-svoy-1':('Which Russian sentence keeps the jeans object while changing the subject?','Бо́ря наде́л свои́ джи́нсы. changes to А́нна наде́ла свои́ джи́нсы. The plural possessive stays plural with jeans while subject and past-verb agreement change.',['А́нна рассказа́ла о своём люби́мом фи́льме. concerns a movie, not jeans.','Свое́й кни́ги она́ никогда́ не написа́ла. concerns a book, not jeans.']),
 'u21-i-svoy-2':('Which Russian sentence keeps the book object while changing the subject?','Свое́й кни́ги он никогда́ не написа́л. changes to Свое́й кни́ги она́ никогда́ не написа́ла. The feminine singular book phrase retains its form while the subject and past-verb agreement change.',['А́нна рассказа́ла о своём люби́мом фи́льме. concerns a movie, not the book object.','А́нна наде́ла свои́ джи́нсы. concerns jeans, not the book object.']),
 'u21-g-object-reference-0':('Which English meaning matches the shown sentence?','Я люблю́ себя́ means I love myself: the object refers back to the subject.',['I love my wife names a wife as the object rather than the subject themself.']),
 'u21-i-object-reference-1':('Which English meaning matches the shown sentence?','Я люблю́ (свою́) жену́ means I love my wife: the object noun names a wife.',['I love myself makes the subject themself the object.']),
 'u21-i-object-reference-2':('Which English meaning matches the shown sentence?','Я люблю́ себя́ means I love myself: the person who loves is also the person loved.',['I love my wife names a wife as the object.']),
 'u21-g-purpose-0':('Which English meaning matches the shown conjunction?','чтобы means so that: the ordinary conjunction can introduce a purpose.',['if introduces a condition.','because supplies a reason.']),
 'u21-i-purpose-1':('Which English meaning matches the shown conjunction?','если means if: the conjunction introduces a condition.',['so that introduces a purpose.','because supplies a reason.']),
 'u21-i-purpose-2':('Which English meaning matches the shown conjunction?','потому что means because: the conjunction introduces a reason.',['although introduces a concession.','after introduces a temporal relationship.'])}
 for id,(prompt,feedback,wrong) in explanations.items():
  i=items[id];oldwhy=i['assessment']['whyAnswerDemonstratesObjective'].replace('жену́ = I love','Я люблю́ (свою́) жену́ = I love my wife')
  if i['objectiveId']=='obj-u21-object-reference':feedback=oldwhy+'\n'+feedback
  i.update(prompt=prompt,feedback=feedback,distractorRationales=['']+wrong);i['assessment']['whyAnswerDemonstratesObjective']=feedback if i['objectiveId']=='obj-u21-object-reference' else oldwhy+'\n'+feedback;i['assessment']['distractorDifference']=['']+wrong
  for text,label in [(prompt,'prompt'),(feedback,'feedback'),(i['assessment']['whyAnswerDemonstratesObjective'],'rationale')]+[(w,'wrong-'+str(n)) for n,w in enumerate(wrong)]:record(text,i['objectiveId'],id+'-'+label)
  changed.append(id)
 removed=0
 for r in p['provenanceRecords']:
  for k,v in [('reviewerIdentity',''),('reviewerQualification',''),('reviewDate',''),('reviewedDigest',''),('audioAssetId',None)]:
   if k in r and r[k]==v:del r[k]
  sy=r.get('synthesis')
  if sy and 'finalAudit' in sy and all(v=='' for v in sy['finalAudit'].values()):del sy['finalAudit'];removed+=1
 after=(json.dumps(p,ensure_ascii=False,separators=(',',':'))+'\n').encode();PACK.write_bytes(after)
 ledger=dict(schemaVersion=1,previousHead='6ec0453169d9a9be087c15bd4b3006589a92e242',unitNumber=21,disposition='IMPLEMENTED_PENDING_FINAL_AUDIT',beforeCurriculumFileSha256=hashlib.sha256(raw).hexdigest(),afterCurriculumFileSha256=hashlib.sha256(after).hexdigest(),addedProvenanceIds=added,changedItemIds=changed,omittedDefaultFinalAuditObjects=removed,bibliography=bibliography,scope='Three sessions/nine cards before practice/full remediation; false object-word/verb equation removed from live remediation; causal since distractor replaced by already admitted although. Russian forms, keys, original licensed records and mappings preserved.')
 (ROOT/'audit/content-corrections/UNIT21_REFLEXIVE_PURPOSE_CORRECTION.json').write_text(json.dumps(ledger,ensure_ascii=False,indent=2)+'\n');print(len(added),'pending records; omitted',removed,'default empty audit objects; bytes',len(after))
if __name__=='__main__':main()
