#!/usr/bin/env python3
"""Explain 17 native-source number-value contrasts, retaining scored/source tuples."""
import hashlib,json,re
from copy import deepcopy
from pathlib import Path
HERE=Path(__file__).resolve().parent
ROOT=HERE.parents[1]
PACK=ROOT/'app/src/main/assets/content/v2-curriculum.json'
BASELINE='3558b70bea28e8f40198025573538b8f612ed26d'
OBJECTIVES={'obj-u9-cardinal','obj-u9-teens','obj-u9-tens','obj-u9-hundreds'}
NAMES={0:'zero',1:'one',2:'two',3:'three',4:'four',5:'five',6:'six',9:'nine',10:'ten',11:'eleven',12:'twelve',14:'fourteen',15:'fifteen',19:'nineteen',20:'twenty',30:'thirty',40:'forty',50:'fifty',60:'sixty',80:'eighty',90:'ninety',100:'one hundred',200:'two hundred',300:'three hundred',500:'five hundred',900:'nine hundred'}
def sha(s):return hashlib.sha256(s.encode()).hexdigest()
def main():
 raw=PACK.read_bytes();p=json.loads(raw)
 if any(r['itemId']=='prov-ca-u9-value-feedback-u9-g-cardinal-0' for r in p['provenanceRecords']):
  print('Already repaired; evidence unchanged');return
 source=(ROOT/'catalog/sources/units-6-10/wikibooks/numbers-4371358.txt').read_text()
 records={r['itemId']:r for r in p['provenanceRecords']};template=records['prov-ca-u5-behavior-teach-1-body']
 bibliography=('Retained Wikibooks Russian/Numbers oldid4371358, catalog/sources/units-6-10/wikibooks/numbers-4371358.txt, CC BY-SA 4.0. '
  'Complete Cardinal numbers table and each existing answer/distractor provenance row bind displayed Russian number words to their numeric values. '
  'Bounded independently authored value comparison and arithmetic place-value explanation; no numeral declension, noun-case or productive-language claim. '
  'Original Russian cues/options, scored keys and source participant relationships remain unchanged. '
  'Independently authored English on 2026-10-13 within content-audit-2026-10-08; no copied primary prose or private Penguin text; final semantic audit pending.')
 added=[];changes=[];bindings=[]
 def value(id,word):
  r=records[id];original=r.get('originalSourceText') or p['sourceTextPool'][r['originalSourceTextRef']]
  assert r['sourceRevision']=='4371358' and r['sourceRecordId'].startswith('numbers:') and r['shippedText']==word
  assert original in source,original
  m=re.match(r'(\d+) ',original);assert m
  n=int(m.group(1));assert n in NAMES
  bindings.append({'sourceRecordId':id,'sourceHash':r['sourceHash'],'word':word,'value':n});return n
 def record(text,item,name):
  r=deepcopy(template);original=bibliography+'\n'+text;objective=item['objectiveId']
  r.update(itemId='prov-ca-u9-value-feedback-'+name,unitNumber=9,objectiveId=objective,sourceRecordId='ca-coverage:'+objective+':value-feedback:'+name,
   originalSourceText=original,shippedText=text,sourceHash=sha(original),contentHash=sha(text))
  r['synthesis'].update(bibliography=bibliography,objectiveOrClaimBinding=objective+':value-feedback:'+name,
   authorshipDeclaration='Independently authored English on 2026-10-13 from retained numeric-value rows; no copied primary prose or private book text.',applications=[])
  assert r['itemId'] not in records;p['provenanceRecords'].append(r);added.append(r['itemId'])
 for item in p['items']:
  if item['objectiveId'] not in OBJECTIVES:continue
  assert item['feedback'].startswith('Does') and len(item['options'])==2;before=deepcopy(item);a=item['assessment']
  av=value(a['answerSourceIds'][0],item['answer']);assert av==int(item['promptLetter']);index=next(n for n,x in enumerate(item['options']) if x!=item['answer']);wrong=item['options'][index]
  dv=value(a['distractorSourceIds'][index],wrong);assert av!=dv
  feedback=f"{item['answer']} names {NAMES[av]} ({av}), the value shown. {wrong} names {NAMES[dv]} ({dv}), a different value."
  if av>=100 and dv<100:
   groups=f'{NAMES[av//100]} hundreds' if av>100 else 'one hundred'
   other=f'{NAMES[dv//10]} tens' if dv>=20 else f'{NAMES[dv]} single units'
   feedback+=f' Compare place value: {av} has {groups}; {dv} has {other}.'
  elif (11<=av<=19 and dv>=20) or (11<=dv<=19 and av>=20):feedback+=' Distinguish the teen value from a multiple of ten.'
  elif av==0:feedback+=' Zero counts no units; one counts a single unit.'
  rationales=['',''];rationales[index]=f'{wrong} names {NAMES[dv]} ({dv}) instead of the shown value {av}.'
  item.update(feedback=feedback,distractorRationales=rationales);a.update(whyAnswerDemonstratesObjective=feedback,distractorDifference=rationales)
  record(feedback,item,item['id']);record(rationales[index],item,item['id']+f'-distractor-{index}');changes.append({'before':before,'after':deepcopy(item)})
 assert len(changes)==17 and len(added)==34
 after=(json.dumps(p,ensure_ascii=False,separators=(',',':'))+'\n').encode();PACK.write_bytes(after)
 (HERE/'UNIT9_NUMBER_FEEDBACK_CORRECTION.json').write_text(json.dumps(dict(baselineCommit=BASELINE,beforeSha256=hashlib.sha256(raw).hexdigest(),afterSha256=hashlib.sha256(after).hexdigest(),addedProvenanceIds=added,changes=changes,valueBindings=bindings,bibliography=bibliography,semanticAcceptance=False),ensure_ascii=False,indent=2)+'\n')
 print('Explained 17 number contrasts; 34 pending coverage records')
if __name__=='__main__':main()
