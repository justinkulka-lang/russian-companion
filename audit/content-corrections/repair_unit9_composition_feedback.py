#!/usr/bin/env python3
"""Explain five composition and six agreement contrasts; preserve scored/source tuples."""
import csv,hashlib,json
from copy import deepcopy
from pathlib import Path
HERE=Path(__file__).resolve().parent
ROOT=HERE.parents[1]
PACK=ROOT/'app/src/main/assets/content/v2-curriculum.json'
BASELINE='92e09df3a6ec8f4871b52bf44a73c80ec983b46e'
def sha(s):return hashlib.sha256(s.encode()).hexdigest()
def main():
 raw=PACK.read_bytes();p=json.loads(raw)
 if any(r['itemId']=='prov-ca-u9-composition-feedback-u9-g-thousands-0' for r in p['provenanceRecords']):
  print('Already repaired; evidence unchanged');return
 rs={r['itemId']:r for r in p['provenanceRecords']};template=rs['prov-ca-u5-behavior-teach-1-body']
 source=(ROOT/'catalog/sources/units-6-10/wikibooks/numbers-4371358.txt').read_text()
 with (ROOT/'catalog/sources/units-3-5/openrussian/nouns.csv').open() as f:nouns={r['bare']:r for r in csv.DictReader(f,delimiter='\t')}
 assert [nouns[w]['gender'] for w in ['мальчик','девушка','животное']]==['m','f','n']
 bibliography=('Retained Wikibooks Russian/Numbers oldid4371358, catalog/sources/units-6-10/wikibooks/numbers-4371358.txt, CC BY-SA 4.0: '
  'complete Cardinal numbers table/examples for21,101,215,1984 and whole thousands2/3/4 versus5-through9, plus the complete Use of numerals in context paragraphs for one/two, gender/number agreement, genitive singular/plural and restriction to nominative/inanimate accusative constructions. '
  'Retained OpenRussian nouns.csv commit50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0: gender=m/f/n for мальчик/девушка/животное. '
  'The Numbers source explicitly gives the plural-form one-pair-of-jeans example; empty OpenRussian fields for jeans are not used as proof. '
  'Bounded independently authored English and arithmetic decomposition; no new Russian form, unrestricted numeral-declension rule or productive-language claim. '
  'Original Russian cues/options, keys and source participants remain unchanged. Independently authored on 2026-10-13 within content-audit-2026-10-08; no copied primary prose or private Penguin text; final semantic audit pending.')
 corrections={
  'u9-g-thousands-0':('два́дцать оди́н represents 21: 20 + 1. The other option gives only 20 and omits the final unit.', 'The shorter option gives 20 instead of 21; it omits the final unit.'),
  'u9-i-thousands-1':('сто оди́н represents 101: 100 + 1. The other option gives only 100 and omits the final unit.', 'The shorter option gives 100 instead of 101; it omits the final unit.'),
  'u9-i-thousands-2':('две́сти пятна́дцать represents 215: 200 + 15. The other option gives only 200 and omits the final fifteen.', 'The shorter option gives 200 instead of 215; it omits the final fifteen.'),
  'u9-i-thousands-3':('ты́сяча девятьсо́т во́семьдесят четы́ре represents 1,984: 1,000 + 900 + 80 + 4. The other option gives only 1,000 and omits the remaining 984.', 'The shorter option gives 1,000 instead of 1,984; it omits the remaining 984.'),
  'u9-i-thousands-4':('The shown group counts two, three or four thousands. In these whole-thousand examples, the counted word for thousand takes the genitive singular. The other option counts five through nine thousands and uses the genitive plural. These examples use the basic nominative or inanimate accusative counting construction.', 'The other option counts five through nine thousands with genitive plural instead of the shown two, three or four thousands with genitive singular.'),
  'u9-g-odindva-0':('один is the masculine singular form of one and agrees with the shown word for boy in this example. одна is feminine singular instead. Match the grammatical gender of the noun in the retained counting phrase.', 'одна is feminine singular instead of the masculine singular agreement required by the shown noun.'),
  'u9-i-odindva-1':('одна is the feminine singular form of one and agrees with the shown word for girl in this example. один is masculine singular instead. Match the grammatical gender of the noun in the retained counting phrase.', 'один is masculine singular instead of the feminine singular agreement required by the shown noun.'),
  'u9-i-odindva-2':('одно is the neuter singular form of one and agrees with the shown word for animal in this example. один is masculine singular instead. The retained lexical row classifies this noun as neuter; choose by that grammatical gender.', 'один is masculine singular instead of the neuter singular agreement required by the shown noun.'),
  'u9-i-odindva-3':('одни is the plural form of one used with the shown word for jeans. The retained example means one pair of jeans; plural grammatical agreement does not mean several pairs here. один is masculine singular and does not match this plural-form noun.', 'один is masculine singular instead of the plural agreement used for one pair of jeans in the retained example.'),
  'u9-i-odindva-4':('один is the masculine singular form of one and agrees with the shown word for boy in this example. одна is feminine singular instead. Match the grammatical gender of the noun in the retained counting phrase.', 'одна is feminine singular instead of the masculine singular agreement required by the shown noun.'),
  'u9-i-odindva-5':('две is the feminine form of two used in the retained phrase for two girls. два is used with masculine or neuter nouns instead. Choose by grammatical gender here; both options express the same numeric value.', 'два expresses two with masculine or neuter nouns instead of the feminine agreement used in the retained phrase.')}
 added=[];changes=[]
 def record(text,item,name):
  r=deepcopy(template);original=bibliography+'\n'+text;objective=item['objectiveId']
  r.update(itemId='prov-ca-u9-composition-feedback-'+name,unitNumber=9,objectiveId=objective,sourceRecordId='ca-coverage:'+objective+':composition-feedback:'+name,
   originalSourceText=original,shippedText=text,sourceHash=sha(original),contentHash=sha(text))
  r['synthesis'].update(bibliography=bibliography,objectiveOrClaimBinding=objective+':composition-feedback:'+name,
   authorshipDeclaration='Independently authored English on 2026-10-13 from complete retained examples/context and lexical gender fields; no copied primary prose or private book text.',applications=[])
  assert r['itemId'] not in rs;p['provenanceRecords'].append(r);added.append(r['itemId'])
 for item in p['items']:
  if item['id'] not in corrections:continue
  assert item['feedback'].startswith('Does') and len(item['options'])==2;before=deepcopy(item)
  for id in item['assessment']['relationParticipantIds']:
   r=rs[id];original=r.get('originalSourceText') or p['sourceTextPool'][r['originalSourceTextRef']];assert r['sourceRevision']=='4371358' and original in source
  feedback,rationale=corrections[item['id']];rationales=['' if word==item['answer'] else rationale for word in item['options']]
  item.update(feedback=feedback,distractorRationales=rationales);item['assessment'].update(whyAnswerDemonstratesObjective=feedback,distractorDifference=rationales)
  record(feedback,item,item['id']);record(rationale,item,item['id']+'-distractor');changes.append({'before':before,'after':deepcopy(item)})
 assert len(changes)==11 and len(added)==22
 after=(json.dumps(p,ensure_ascii=False,separators=(',',':'))+'\n').encode();PACK.write_bytes(after)
 (HERE/'UNIT9_COMPOSITION_FEEDBACK_CORRECTION.json').write_text(json.dumps(dict(baselineCommit=BASELINE,beforeSha256=hashlib.sha256(raw).hexdigest(),afterSha256=hashlib.sha256(after).hexdigest(),addedProvenanceIds=added,changes=changes,bibliography=bibliography,semanticAcceptance=False),ensure_ascii=False,indent=2)+'\n')
 print('Explained five composition and six agreement rows;22 pending coverage records')
if __name__=='__main__':main()
