#!/usr/bin/env python3
"""Repair source-bound prefix pairing and motion instruction; retain pending review."""
import hashlib,json
from pathlib import Path
from copy import deepcopy
ROOT=Path(__file__).resolve().parents[2]
PACK=ROOT/'app/src/main/assets/content/v2-curriculum.json'
def sha(s):return hashlib.sha256(s.encode()).hexdigest()
def main():
 raw=PACK.read_bytes();p=json.loads(raw)
 if any(r['itemId']=='prov-ca-u1920-prefix-objective' for r in p['provenanceRecords']):
  print('Already repaired');return
 template=next(r for r in p['provenanceRecords'] if r['itemId']=='prov-ca-17-72');added=[]
 bibliography='Retained Wikibooks Russian aspect paragraph explicitly pairs отговаривать with отговорить; Gramota https://gramota.ru/meta/otgovorit and /meta/otgovarivat corroborate the pair. Retained Wikipedia motion table and examples, and Wiktionary dolzhen revision84965088. Cornell Beginning Russian Grammar https://russian.cornell.edu/grammar/html/gr12_b_a.htm le57_62_a.htm (directionality), le57_62_b.htm (setting out), and subject index consulted2026-10-11. Independent English synthesis; linguistic review pending.'
 def record(text,obj,name,original=None,applications=None):
  r=deepcopy(template);original=original or bibliography+'\n'+text
  r.update(itemId='prov-ca-u1920-'+name,unitNumber=int(obj.split('-')[1][1:]),objectiveId=obj,sourceRecordId='ca-coverage:'+obj+':'+name,recordType='label',originalSourceText=original,shippedText=text,sourceHash=sha(original),contentHash=sha(text))
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
    st['body']='Read the shown Russian form and choose the requested meaning, directionality label or aspect partner using the examples above. A missed check returns to the complete explanation.';record(st['body'],obj,name+'-'+st['kind'].lower())
 # Admit the actual imperfective from the retained, explicitly paired licensed paragraph.
 cue=deepcopy(next(r for r in p['provenanceRecords'] if r['itemId']=='prov-u19-prefix-caution-x-0'))
 assert 'отговаривать' in cue['originalSourceText']
 cue.update(itemId='prov-u1920-prefix-imperfective',shippedText='отговаривать',contentHash=sha('отговаривать'))
 p['provenanceRecords'].append(cue);added.append(cue['itemId'])
 lesson(19,'prefix-caution','Distinguish changed meanings and genuine aspect partners',[
 ('Different prefixes can change the meaning','отговорить — to dissuade. договориться — to agree. These are different meanings, even though the words share material with a verb of speaking. A prefix is not always just an aspect marker: learn the whole verb and its meaning.'),
 ('The actual dissuade aspect pair','отговаривать is the imperfective partner of perfective отговорить: both concern dissuading. The retained source explicitly identifies this pair. сказать belongs to a separate speaking pair; it is not the imperfective partner of отговорить. For a question asking for the perfective partner of отговаривать, select отговорить. договориться is a different verb meaning to agree.'),
 ('Keep the other meanings separate','написать — to write. сделать — to do. Neither expresses dissuading or agreeing. When a question asks for a meaning, translate the displayed verb. When it asks for an aspect partner, preserve the lexical meaning while changing aspect. These selections do not demonstrate creating new prefixed verbs or using aspect in a sentence.')],'prefix')
 lesson(19,'probability','Distinguish expected events from obligation',[
 ('An expected eclipse',"Затме́ние должно́ наступи́ть в три часа́. — The eclipse should occur at three o'clock. Here should expresses an expected event, not a duty imposed on the eclipse. The short adjective agrees with the neuter subject and the infinitive names the event. The retained usage description is (with a verb) Used to express likelihood. Usually translated as \"should\"."),
 ('An employee obligation','Слу́жащие должны́ во́время явля́ться на рабо́ту. — Employees must report to work on time. This states an obligation for plural employees. The English label an obligation describes a required action; an expected event describes a prediction such as the eclipse. Context determines which sense the shared adjective-plus-infinitive structure expresses. One independent check asks for the eclipse sentence meaning; the other classifies the employee obligation. These two examples do not demonstrate independent sentence production or every use of the adjective.')],'probability')
 lesson(20,'motion','Distinguish motion meaning, directionality and setting out',[
 ('Walking and travelling by vehicle','идти́ — to go, walk. е́хать — to go by vehicle, ride. The first describes movement on foot; the second movement by vehicle. These are different means of moving, not an aspect pair.'),
 ('Directionality is separate from aspect','The table places идти́ in the unidirectional column: motion in one direction. Its other column is multidirectional, covering repeated, return or different-direction movement according to context. These directionality labels are not English verb meanings. The basic unprefixed forms in these columns are imperfective; unidirectional does not itself mean perfective or arrival.'),
 ('Carrying, leading and transporting','нести́ — to carry. вести́ — to lead. везти́ — to convey, transport. Carrying on foot, leading someone and transporting by vehicle are distinct meanings. For нести́ choose to carry. A question about its meaning is different from a question about directionality.'),
 ('Setting out does not assert arrival',"Он пошёл к другу. — he set off toward a friend. The perfective prefixed verb presents the beginning of the journey; it does not say that he arrived. Он шёл к другу. — He went to a friend's place. This retained English rendering accompanies an imperfective form describing motion toward the friend; the Russian form alone does not specify how long he was on the way. The distractor he was already on the way for a long time adds duration that is not supplied, and he refused to go contradicts the setting-out sentence."),
 ('Read the path and only the displayed clause','Мальчик отошёл от незнакомца — The boy stepped back from the stranger. The verb and the phrase after it express movement away from the stranger. There is no additional relative clause in this displayed sentence. Они у́летят из Владивостока через месяц. — They will leave Vladivostok in a month. Мяч залетел на крышу дома. — The ball flew onto the roof of the house. These are different subjects and paths. The prefix contributes to the motion meaning, while the accompanying phrases identify the path and time; do not substitute a translation of a different example.')],'motion')
 items={i['id']:i for i in p['items']};changed=[]
 def explain(id,prompt,feedback,wrong):
  i=items[id];i.update(prompt=prompt,feedback=feedback,distractorRationales=['']+wrong)
  oldwhy=i['assessment']['whyAnswerDemonstratesObjective'];i['assessment']['whyAnswerDemonstratesObjective']=oldwhy+'\n'+feedback if id=='u20-i-motion-2' else feedback;i['assessment']['distractorDifference']=['']+wrong
  for text,label in [(prompt,'prompt'),(feedback,'feedback')]+[(w,'wrong-'+str(n)) for n,w in enumerate(wrong)]:record(text,i['objectiveId'],id+'-'+label)
  changed.append(id)
 explain('u19-g-prefix-caution-0','Which English meaning matches the shown Russian verb?','отговорить has the dissuade meaning: to dissuade; it concerns persuading someone against an action.',['to agree is the meaning of the agreement verb.','to write is the meaning of the writing verb.'])
 explain('u19-i-prefix-caution-1','Which English meaning matches the shown Russian verb?','договориться has the agree meaning: to agree; it expresses reaching an agreement.',['to dissuade is the meaning of a different prefixed verb.','to do is the meaning of the doing verb.'])
 i=items['u19-i-prefix-caution-2'];i['promptLetter']='отговаривать';i['assessment']['relationParticipantIds'][0]=cue['itemId'];i['assessment']['testedFeature']='PERFECTIVE_PARTNER'
 explain(i['id'],'Which Russian verb is the perfective partner of the shown imperfective?','отговаривать has the perfective partner отговорить. The retained source explicitly pairs these two verbs with the same dissuading meaning.',['договориться expresses reaching an agreement, a different lexical meaning.','сделать means to do, a different lexical meaning.'])
 i=items['u19-i-probability-2'];i.update(promptLetter='Слу́жащие должны́ во́время явля́ться на рабо́ту.',options=['an obligation','an expected event'],answer='an obligation',sourceId='src-content-audit-research')
 app=dict(cue=i['promptLetter'],answer=i['answer'],distractors=['an expected event'])
 original=bibliography+'\n'+i['promptLetter']+'\nan obligation\nan expected event\nThe employees are required to report on time; this use imposes a duty rather than predicts an event.'
 ids=[record(t,i['objectiveId'],'probability-contrast-'+str(n),original,[app]) for n,t in enumerate([i['promptLetter']]+i['options'])]
 for rid in ids:
  next(r for r in p['provenanceRecords'] if r['itemId']==rid)['sourceRecordId']='ca-coverage:obj-u19-probability:employee-contrast'
 a=i['assessment'];a.update(testedFeature='OBLIGATION_VS_EXPECTATION',evidenceMode='STATED_APPLICATION',relationKind='STATED_APPLICATION',relationParticipantIds=ids,answerSourceIds=[ids[1]],distractorSourceIds=['',ids[2]],contrastMembers=i['options'])
 explain(i['id'],'Which English label describes the use of the adjective in the shown sentence?','Слу́жащие должны́ во́время явля́ться на рабо́ту. expresses an obligation: the employees must report on time.',['an expected event describes a prediction rather than the required employee action.'])
 explain('u20-g-motion-0','Which English directionality label applies to the shown unprefixed motion verb?','идти́ is unidirectional in the retained table: it describes motion in one direction. This label identifies directionality, not aspect.',['multidirectional is the other motion column, covering different-direction or repeated movement according to context.'])
 explain('u20-i-motion-1','Which English meaning matches the shown motion verb?','идти́ means to go, walk: movement on foot.',['to go by vehicle, ride describes movement by vehicle.','to carry describes carrying something on foot.'])
 explain('u20-i-motion-2','Which English meaning matches the shown motion verb?','нести́ means to carry: the carrying-on-foot meaning.',['to lead describes leading someone.','to convey, transport describes transporting by vehicle.'])
 # Append complete updated instruction to the two pending motion research spans.
 revised=[]
 for cap in p['sourceCapabilities']:
  if cap['id'] not in ('cap-u20-motion-po-setting-out','cap-u20-motion-prefixed-path'):continue
  old=cap['sourceHash'];steps=next(u for u in p['units'] if u['number']==20)['sessions'];session=next(s for s in steps if s['id']=='u20-s-motion')
  span=cap['exactSpan']+'\\n'+bibliography+'\\n'+'\\n'.join(st['body'] for st in session['steps'] if st['kind'] in ('TEACH','REMEDIATE'))
  cap.update(exactSpan=span,sourceHash=sha(span))
  for host in p['provenanceRecords']:
   if host['sourceRecordId']==cap['sourceRecordId'] and host['sourceHash']==old:
    host.update(originalSourceText=span,sourceHash=sha(span));revised.append(host['itemId'])
  pinfile=ROOT/'app/src/main/java/dev/russiancompanion/v2/curriculum/ContentAuditCapabilityPins.kt';pins=pinfile.read_text();assert old in pins;pinfile.write_text(pins.replace(old,sha(span)))
 # Omit only schema defaults; no source text, rights, status or evidence is removed.
 removed=0
 for r in p['provenanceRecords']:
  for k,v in [('reviewerIdentity',''),('reviewerQualification',''),('reviewDate',''),('reviewedDigest',''),('audioAssetId',None)]:
   if k in r and r[k]==v:del r[k];removed+=1
 after=(json.dumps(p,ensure_ascii=False,separators=(',',':'))+'\n').encode();PACK.write_bytes(after)
 ledger=dict(schemaVersion=1,previousHead='4c8239bc3232a9afda01871eb19e49cfd20b1d8e',unitNumbers=[19,20],disposition='IMPLEMENTED_PENDING_FINAL_AUDIT',beforeCurriculumFileSha256=hashlib.sha256(raw).hexdigest(),afterCurriculumFileSha256=hashlib.sha256(after).hexdigest(),addedProvenanceIds=added,revisedResearchProvenanceIds=revised,changedItemIds=changed,omittedDefaultFields=removed,bibliography=bibliography,scope='True licensed dissuade aspect cue; distinct employee-obligation check; complete prefix and motion teaching before practice. Original records retained modulo explicit schema-default omissions. All mappings and other units preserved.')
 (ROOT/'audit/content-corrections/UNIT19_PREFIX_UNIT20_MOTION_CORRECTION.json').write_text(json.dumps(ledger,ensure_ascii=False,indent=2)+'\n');print(len(added),'pending records;',removed,'default fields omitted; bytes',len(after))
if __name__=='__main__':main()
