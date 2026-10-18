"""Evidence construction only; output requires literal/context review, not automatic approval."""
from pathlib import Path
import json,re,hashlib,collections
base=Path('audit/independent-gpt61-2026-10-12');out=base/'source-guard-scope';out.mkdir(exist_ok=True)
p=json.loads(Path('app/src/main/assets/content/v2-curriculum.json').read_text())
records=p['provenanceRecords']
for r in records:r['originalSourceText']=r.get('originalSourceText') or p['sourceTextPool'][r['originalSourceTextRef']]
review=[];categories=collections.Counter()
for entry in json.loads((base/'batch-a-rationale-scope/SOURCE_GUARD_PENDING.json').read_text())['offenders']:
 path=Path(entry['path']);text=path.read_text();values=set();fileids=set(re.findall(r'"(prov-[^"\\]*)"',text))
 for line in text.splitlines():
  if not any(0x400<=ord(c)<=0x4ff for c in line):continue
  remaining=re.sub(r'"(?:\\.|[^"\\])*"','',line);assert not any(0x400<=ord(c)<=0x4ff for c in remaining),line
  for token in re.findall(r'"(?:\\.|[^"\\])*"',line):
   value=json.loads(token)
   if any(0x400<=ord(c)<=0x4ff for c in value):values.add(value)
 bindings=[]
 for value in sorted(values):
  matches=[(r,f) for r in records for f in ['shippedText','sourceUrl','sourceRecordId'] if r.get(f)==value]
  matches.sort(key=lambda pair:('synthesis' in pair[0]['transformation'],pair[0]['itemId'] not in fileids,not (path.name=='V2Validator.kt' and pair[0]['itemId'].startswith('prov-u5-indecl-')),'native' not in pair[0]['itemId'],pair[0]['itemId'],pair[1]))
  if matches:
   r,field=matches[0];assert hashlib.sha256(r['originalSourceText'].encode()).hexdigest()==r['sourceHash']
   purpose='EXISTING_RESEARCH_CUE_COMPARISON_PENDING_SOURCE_REVIEW' if 'synthesis' in r['transformation'] else 'EXISTING_SOURCE_RECORD_COMPARISON'
   expected={k:r[k] for k in ['itemId','sourceId','sourceRevision','sourceUrl','sourceRecordId','sourceHash','contentHash','transformation','licenseId','shippedText','reviewStatus']}
   b={'kind':'PROVENANCE_FIELD','value':value,'field':field,'purpose':purpose,'record':expected}
  else:
   assert sum(i['id']==value for i in p['items'])==1
   b={'kind':'ITEM_ID','value':value,'purpose':'PERSISTED_ITEM_ID_REFERENCE'}
  categories[b['purpose']]+=1;bindings.append(b)
 review.append({'path':str(path.relative_to('app')),'sha256':hashlib.sha256(path.read_bytes()).hexdigest(),'purpose':'Exact existing comparison/policy-reference implementation; no rendering or Russian construction introduced','bindings':bindings})
manifest={'schemaVersion':1,'reviewedRepositoryCommit':'01ae46c3682a036fe957db1b2c3e03819c17d55b','semanticAcceptance':False,'rightsAcceptance':False,'files':review}
pth=Path('app/src/test/resources/reviewed-curriculum-cyrillic.json');pth.parent.mkdir(parents=True,exist_ok=True);pth.write_text(json.dumps(manifest,ensure_ascii=False,indent=2)+'\n')
(out/'LITERAL_REVIEW_SUMMARY.json').write_text(json.dumps({'files':len(review),'bindings':sum(len(r['bindings']) for r in review),'purposes':dict(categories),'testResource':str(pth),'allCyrillicOccurrencesInsideEnumeratedOrdinaryStringLiterals':True,'noProductionChanges':True,'formalAcceptance':False},indent=2)+'\n')
for r in review:
 print(r['path'].split('/')[-1])
 for b in r['bindings']:print(b['kind'],b.get('field',''),repr(b['value']),b.get('record',{}).get('itemId',''))
print(dict(categories))
