#!/usr/bin/env python3
"""Create lossless, source-bound curriculum inputs for the same ChatGPT audit."""
import argparse,csv,hashlib,io,json,subprocess,sys,zipfile
from pathlib import Path
sys.dont_write_bytecode=True
ROOT=Path(__file__).resolve().parents[3]
BASE=Path(__file__).resolve().parent
TARGET='5c176eb22f4a93bf5f7fd20af15e79ec8c4748ed'
PRIOR=BASE.parent/'received-results/2026-10-19-fa532fe6'
def git(*args):return subprocess.check_output(['git','-C',str(ROOT),*args])
def sha(b):return hashlib.sha256(b).hexdigest()
def jb(o):return (json.dumps(o,ensure_ascii=False,indent=2)+'\n').encode()
def main():
 ap=argparse.ArgumentParser();ap.add_argument('--output',type=Path,required=True);a=ap.parse_args()
 entries={};files=[];collections={};limit=96*1024
 def put(n,b):
  assert n not in entries and not n.startswith('/') and '..' not in Path(n).parts
  entries[n]=b.encode() if isinstance(b,str) else b
 def local(n,b):
  path=BASE/n;path.parent.mkdir(parents=True,exist_ok=True);path.write_bytes(b);put(n,b)
 rawpath='app/src/main/assets/content/v2-curriculum.json';loaderpath='catalog/curriculum_storage.py'
 raw=git('show',TARGET+':'+rawpath);loader=git('show',TARGET+':'+loaderpath)
 scope={'__name__':'pinned_curriculum_storage'};exec(compile(loader,loaderpath,'exec'),scope);pack=scope['loads'](raw)
 assert [len(pack[k]) for k in ['units','items','examples','sourceCapabilities','provenanceRecords']]==[30,622,817,58,6943]
 put('raw/v2-curriculum.json',raw);put('raw/curriculum_storage.py',loader)
 # Complete disjoint top-level projections preserve all values and list ordering.
 for key,value in pack.items():
  if isinstance(value,list):
   batches=[];batch=[]
   for record in value:
    if batch and len(jb(batch+[record]))>limit:batches.append(batch);batch=[]
    batch.append(record)
   if batch or not value:batches.append(batch)
   chunks=[];start=0
   for i,batch in enumerate(batches,1):
    name=f'projections/{key}/{i:03}.json';b=jb(batch);local(name,b);chunks.append({'path':name,'startIndex':start,'recordCount':len(batch),'bytes':len(b),'sha256':sha(b),'ids':[x.get('id') if isinstance(x,dict) else None for x in batch]});start+=len(batch)
   collections[key]={'kind':'array','recordCount':len(value),'chunks':chunks}
  else:
   name=f'projections/scalars/{key}.json';b=jb(value);local(name,b);collections[key]={'kind':'value','path':name,'sha256':sha(b)}
 # Focused unit views are convenient; complete collections above remain authoritative projections.
 owners={o['id']:u['number'] for u in pack['units'] for o in u['objectives']}
 for u in pack['units']:
  n=u['number'];objs={o['id'] for o in u['objectives']}
  examples={eid for o in u['objectives'] for eid in o.get('exampleIds',[])}|{eid for se in u['sessions'] for st in se['steps'] for eid in st.get('exampleIds',[])}|{e['id'] for e in pack['examples'] if e.get('objectiveId') in objs}
  data={'derivedFromCommit':TARGET,'rawSha256':sha(raw),'unit':u,'items':[i for i in pack['items'] if i['objectiveId'] in objs],'examples':[e for e in pack['examples'] if e['id'] in examples],'sourceCapabilities':[c for c in pack['sourceCapabilities'] if c['unitNumber']==n]}
  local(f'unit-views/UNIT_{n:02}.json',jb(data))
 # Minimal authentic Git-object proof binds raw input and parser to the target commit.
 commit=git('cat-file','commit',TARGET);root_tree=commit.splitlines()[0].decode().split()[1];trees={}
 for path in [rawpath,loaderpath]:
  oid=root_tree
  for component in path.split('/')[:-1]:
   trees[oid]=git('cat-file','tree',oid).hex();line=git('ls-tree',oid,component).decode().strip();oid=line.split()[2]
  trees[oid]=git('cat-file','tree',oid).hex()
 proof={'target':TARGET,'rootTree':root_tree,'commitObjectHex':commit.hex(),'treeObjectsHex':trees,'paths':{p:git('rev-parse',TARGET+':'+p).decode().strip() for p in [rawpath,loaderpath]}}
 local('SOURCE_GIT_PROOF.json',jb(proof))
 index={'schemaVersion':1,'auditTarget':TARGET,'rawPath':rawpath,'rawBytes':len(raw),'rawSha256':sha(raw),'rawGitBlob':proof['paths'][rawpath],'parserGitBlob':proof['paths'][loaderpath],'method':'Exact git-show bytes parsed using exact target loader;all expanded top-level values partitioned without semantic edits.','collections':collections,'counts':{k:len(pack[k]) for k in ['units','items','examples','sourceCapabilities','provenanceRecords']},'privatePdfIncluded':False,'runtimeEvidenceIncluded':'Prior supplied records only;no new runtime execution.'}
 local('CURRICULUM_INDEX.json',jb(index))
 # Seed results are immutable originals, not updated audit conclusions.
 for path in PRIOR.iterdir():
  if path.name in ['INDEPENDENT_AUDIT_REPORT.md','FINDINGS.csv','ITEM_ASSESSMENT_AUDIT.csv','UNIT_COVERAGE.csv','TARGET_75_DISPOSITIONS.csv','REQUIREMENTS_MATRIX.csv','SOURCE_AND_BINDING_REVIEW.json','AUDIT_SCOPE_AND_EVIDENCE.json','CODEX_RETURN_HANDOFF.md','APP_ENGINEERING_AUDIT.csv','REPOSITORY_FILE_COVERAGE.csv','CURRICULUM_ENTITY_COVERAGE.csv','SHA256SUMS.txt','RECEIPT_VERIFICATION.json','CODEX_RECEIPT_STATIC_CHECK.json']:put('prior-results/'+path.name,path.read_bytes())
 # Independent auditor must check these candidates; these records do not change statuses.
 prior=json.loads((PRIOR/'AUDIT_SCOPE_AND_EVIDENCE.json').read_text());missing=prior['inventory']['itemsAbsentFromReadablePackets'];assert len(missing)==14
 fill={'scope':'Previously unread item records supplied,not semantic dispositions. All817examples and58capabilities supplied in complete collections.','items':[i for i in pack['items'] if i['id'] in missing],'requiredFollowup':'Inspect all fields,source contexts,teaching relationships and requirements independently;do not mark acceptable merely because supplied.'}
 local('PREVIOUSLY_MISSING_ITEMS.json',jb(fill))
 for n in ['COPY_PASTE_PROMPT.md','CONTINUATION_DELIVERABLES.md','GAP_LEDGER.csv','README.md','VERIFY_SUPPLEMENT.py']:put(n,(BASE/n).read_bytes())
 for f in (BASE/'continuation-templates').iterdir():put('continuation-templates/'+f.name,f.read_bytes())
 put('ORIGINAL_DELIVERABLE_SPEC.md',(BASE.parent/'DELIVERABLES.md').read_bytes())
 put('ORIGINAL_EXECUTION_GUIDE.md',(BASE.parent/'EXECUTION_GUIDE.md').read_bytes())
 put('PRIVATE_REFERENCE_ACCESS.md',(BASE.parent/'PRIVATE_REFERENCE_ACCESS.md').read_bytes())
 manifest={'schemaVersion':1,'auditTarget':TARGET,'manifestSelfExcluded':True,'files':[{'path':n,'bytes':len(b),'sha256':sha(b)} for n,b in sorted(entries.items())]}
 local('MANIFEST.json',jb(manifest))
 a.output.parent.mkdir(parents=True,exist_ok=True)
 with zipfile.ZipFile(a.output,'x',zipfile.ZIP_DEFLATED,compresslevel=9) as z:
  for n,b in sorted(entries.items()):z.writestr(n,b)
 with zipfile.ZipFile(a.output) as z:
  assert z.testzip() is None
  for f in manifest['files']:assert sha(z.read(f['path']))==f['sha256']
 print(json.dumps({'file':str(a.output),'bytes':a.output.stat().st_size,'sha256':sha(a.output.read_bytes()),'entries':len(entries),'projectionFiles':sum(n.startswith('projections/') for n in entries),'target':TARGET,'newAuditPerformed':False,'runtimeTestsExecuted':0}))
if __name__=='__main__':main()
