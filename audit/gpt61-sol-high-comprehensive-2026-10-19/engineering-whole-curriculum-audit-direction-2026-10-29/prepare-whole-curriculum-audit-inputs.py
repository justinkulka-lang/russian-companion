"""Pinned read-only auditor inputs; inventories are not independent inspection results."""
from pathlib import Path
from datetime import datetime,timezone
import subprocess,json,hashlib,csv,copy,collections,argparse
roots=[q for q in Path(__file__).resolve().parents if(q/'.git').exists()]
parser=argparse.ArgumentParser();parser.add_argument('--repo',default=str(roots[0])if roots else'/workspace/russian-companion');parser.add_argument('--output',required=True);args=parser.parse_args();R=Path(args.repo).resolve();W=Path(args.output).resolve();W.mkdir(parents=True,exist_ok=True);S='5a3411a76b88753617ea18b3e1a54f44f6923b9f';OLD='8606c4d74185f594d93941bef6858b2af28e0b75';ASSET='app/src/main/assets/content/v2-curriculum.json';git=lambda *a:subprocess.check_output(['git',*a],cwd=R);sha=lambda b:hashlib.sha256(b).hexdigest();canon=lambda x:json.dumps(x,ensure_ascii=False,sort_keys=True,separators=(',',':')).encode();save=lambda n,x:(W/n).write_text(json.dumps(x,ensure_ascii=False,indent=2)+'\n');escape=lambda s:str(s).replace('~','~0').replace('/','~1')
raw=git('show',S+':'+ASSET);p=json.loads(raw);old=json.loads(git('show',OLD+':'+ASSET));expanded=copy.deepcopy(p);pool=expanded.pop('sourceTextPool');used=set();refs=0
for k,text in pool.items():assert len(k)==64 and sha(text.encode())==k
for record in expanded['provenanceRecords']:
 if 'originalSourceTextRef'in record:
  key=record.pop('originalSourceTextRef');assert 'originalSourceText'not in record and record['sourceHash']==key;record['originalSourceText']=pool[key];used.add(key);refs+=1
assert used==set(pool)
def leaves(v,path=''):
 if isinstance(v,dict):
  for k,x in v.items():yield from leaves(x,path+'/'+escape(k))
 elif isinstance(v,list):
  for k,x in enumerate(v):yield from leaves(x,path+'/'+str(k))
 elif isinstance(v,str):yield path,v
rawLeaves=list(leaves(p));expandedLeaves=list(leaves(expanded));rows=[];units={u['number']:u for u in p['units']};objectiveOwner={o['id']:u['number']for u in p['units']for o in u['objectives']}
def row(kind,value,pointer,unit=''):
 entityId=value.get('id',value.get('itemId',value.get('sourceId',str(value.get('unitNumber',value.get('number','')))))) or 'json_pointer:'+pointer;owner=value.get('unitNumber',unit or objectiveOwner.get(value.get('objectiveId'),''));rows.append({'entity_type':kind,'entity_id':entityId,'unit_number':owner,'json_pointer':pointer,'canonical_json_sha256':sha(canon(value)),'preparer_status':'INVENTORY_ONLY_NOT_INDEPENDENTLY_AUDITED'})
keys=['sources','rights','letters','examples','items','references','audioAssets','handwritingAssets','ownedEditionMappings','provenanceRecords','authoredEnglishRecords','sourceCapabilities','unitReviews']
for key in keys:
 for i,value in enumerate(p[key]):row(key,value,'/'+key+'/'+str(i))
unitRows=[]
for i,u in enumerate(p['units']):
 up='/units/'+str(i);row('units',u,up,u['number']);steps=[st for se in u['sessions']for st in se['steps']];checks=sorted({iid for st in steps if st['kind']=='CHECK'for iid in st.get('itemIds',[])});guided=sorted({iid for st in steps if st['kind']=='GUIDED'for iid in st.get('itemIds',[])})
 for k,o in enumerate(u['objectives']):row('objectives',o,up+'/objectives/'+str(k),u['number'])
 for k,se in enumerate(u['sessions']):
  sp=up+'/sessions/'+str(k);row('sessions',se,sp,u['number'])
  for n,st in enumerate(se['steps']):row('steps',st,sp+'/steps/'+str(n),u['number'])
 unitRows.append({'unit_number':u['number'],'unit_id':u['id'],'title':u['title'],'json_pointer':up,'objectives':len(u['objectives']),'sessions':len(u['sessions']),'steps':len(steps),'teach_steps':sum(st['kind']=='TEACH'for st in steps),'check_item_ids':';'.join(checks),'guided_item_ids':';'.join(guided),'book_lesson':u.get('penguinLesson',''),'cumulative_policy':u.get('advancement',{}).get('cumulativeReviewPolicyId',''),'preparer_status':'INVENTORY_ONLY_NOT_INDEPENDENTLY_AUDITED'})
for name,data in [('ENTITY_INVENTORY.csv',rows),('UNIT_INPUT_INVENTORY.csv',unitRows)]:
 with(W/name).open('w',newline='')as f:w=csv.DictWriter(f,fieldnames=list(data[0]),lineterminator='\n');w.writeheader();w.writerows(data)
a={x['id']:x for x in old['sourceCapabilities']};b={x['id']:x for x in p['sourceCapabilities']};added=sorted(b.keys()-a.keys());removed=sorted(a.keys()-b.keys());changed=sorted(k for k in a.keys()&b.keys()if a[k]!=b[k]);earlySource=git('rev-parse','5c176eb2').decode().strip();early=json.loads(git('show',earlySource+':'+ASSET));ea={x['id']:x for x in early['sourceCapabilities']}
history=[{'sourceCommit':source,'count':len(objects),'addedSinceThisSource':sorted(b.keys()-objects.keys()),'removedSinceThisSource':sorted(objects.keys()-b.keys()),'changedExistingIds':sorted(k for k in objects.keys()&b.keys()if objects[k]!=b[k])}for source,objects in [(earlySource,ea),(OLD,a)]]
save('CAPABILITY_SCOPE_RECONCILIATION.json',{'currentSource':S,'currentCapabilityCount':len(b),'historicalSources':history,'allCurrent58RequireActualIndependentInspection':True,'reportedEarlierCount':37,'reportedEarlierCountVerified':False,'scopeNote':'Raw inspection finds58capabilityobjects at both5c176eb2and8606c4d7aswellascurrent5a3411a7. Earlier37wasareportedhandoffcount,notverifiedcapscope;itmustnotrestrictreviewordescribe21newobjects.'})
required=['AGENTS.md','docs/governance/MASTER_AUTHORITY_v2.0.md','docs/governance/MASTER_AUTHORITY_v2.0_AMENDMENT_1.md','docs/design/PREMIUM_OFFLINE_APP_BLUEPRINT_v2.0.md','audit/claude-code-comprehensive-2026-10-19/PRIVATE_REFERENCE_ACCESS.md','audit/claude-code-comprehensive-2026-10-19/fetch_private_reference.py','audit/gpt61-sol-high-comprehensive-2026-10-19/final-independent-recheck-results/2026-10-22-claude-code/FINDINGS.csv',ASSET]
files=git('ls-tree','-r','--name-only',S,'--','app/src/main/java/dev/russiancompanion/v2','app/src/main/java/dev/russiancompanion/data/local','catalog/sources','review','docs/governance').decode().splitlines();files=sorted(set(files+required));fileRows={}
for n in files:
 data=git('show',S+':'+n);fileRows[n]={'bytes':len(data),'sha256':sha(data),'gitBlob':git('rev-parse',S+':'+n).decode().strip(),'status':'INPUT_FILE_NOT_INSPECTION_RESULT'}
save('RELEVANT_INPUT_FILES.json',{'source':S,'scope':'Required references and implementation/frozen source/review inputs;not a whole-repository audit or assertion every file was read','files':fileRows})
manifest={'sourceCommit':S,'sourceRootTree':git('rev-parse',S+'^{tree}').decode().strip(),'inputTrees':{n:git('rev-parse',S+':'+n).decode().strip()for n in ['app/src/main','app/src/test','catalog','docs','review','release']},'rawAsset':{'path':ASSET,'bytes':len(raw),'sha256':sha(raw),'gitBlob':git('rev-parse',S+':'+ASSET).decode().strip()},'sourcePoolEntriesVerified':len(pool),'sourceReferencesExpanded':refs,'allPoolEntriesUsed':True,'expandedDocumentDiagnosticCanonicalSha256':sha(canon(expanded)),'diagnosticHashAlgorithm':'Python JSON sort_keys=True,ensure_ascii=False,separators=comma/colon,UTF8;not V2Validator contentReviewDigest or repository sourceDigest','rawStringLeaves':len(rawLeaves),'expandedStringLeaves':len(expandedLeaves),'counts':dict(collections.Counter(x['entity_type']for x in rows)),'entityInventoryRows':len(rows),'inventoryRowsOverlapParentAndChildObjects':'Do not sum nested object string counts or treat rows as tests','newJUnitOrGradleExecution':0,'privatePdfReadOrIncluded':False,'independentAuditorInspection':'NOT_STARTED','currentCapabilityObjects':len(b),'previousCapabilityObjects':len(ea),'latestPriorRecheckCapabilityObjects':len(a),'reportedHistorical37IsNotAnObservedCapabilityCount':True,'scopeReconciliation':'CAPABILITY_SCOPE_RECONCILIATION.json','targetIsFrozenForAudit':'Later evidence-only commits do not replace this source identity'};save('INPUT_IDENTITY.json',manifest)
assert len(p['units'])==30 and len(p['items'])==626 and len(p['examples'])==821 and len(b)==58
(W/'prepare-whole-curriculum-audit-inputs.py').write_bytes(Path(__file__).read_bytes());print(json.dumps({k:manifest[k]for k in ['sourceCommit','sourcePoolEntriesVerified','sourceReferencesExpanded','rawStringLeaves','expandedStringLeaves','counts','entityInventoryRows','currentCapabilityObjects','previousCapabilityObjects']}))
