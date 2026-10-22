"""Read-only mechanical input verification;does not inspect app semantics or close audit findings."""
from pathlib import Path
import argparse,subprocess,csv,json,hashlib
p=argparse.ArgumentParser();p.add_argument('--repository',type=Path,required=True);R=p.parse_args().repository;D=Path(__file__).resolve().parent;read=lambda p:json.loads(p.read_text());sha=lambda b:hashlib.sha256(b).hexdigest();git=lambda *a:subprocess.check_output(['git',*a],cwd=R);identity=read(D/'PREPARATION_IDENTITY.json');source=identity['targetCommit'];assert git('rev-parse',source+'^{tree}').decode().strip()==identity['targetRootTree']
for n,h in read(D/'SHA256_PREPARATION.json').items():assert sha((D/n).read_bytes())==h,n
expected={}
for line in git('ls-tree','-rlz',source).split(b'\0'):
 if not line:continue
 meta,path=line.split(b'\t',1);mode,typ,oid,size=meta.decode().split();expected[path.decode()]=(mode,typ,oid,size if size!='-' else '')
with (D/'TARGET_FILE_INVENTORY.csv').open(newline='') as f:rows=list(csv.DictReader(f))
assert len(rows)==len(expected)==identity['trackedFiles'] and len({v['path'] for v in rows})==len(rows)
for v in rows:assert (v['mode'],v['type'],v['git_blob_oid'],v['bytes'])==expected[v['path']] and v['independent_review_status']=='UNINSPECTED'
refs=read(D/'REUSED_INPUT_IDENTITIES.json')
for n,h in refs['files'].items():assert sha((R/n).read_bytes())==h,n
raw=git('show',source+':'+identity['rawAssetPath']);assert len(raw)==identity['rawAssetBytes'] and sha(raw)==identity['rawAssetSha256'];root=json.loads(raw);objects={}
def walk(v,path=''):
 if isinstance(v,dict):
  objects[path]=v
  for k,x in v.items():walk(x,path+'/'+k.replace('~','~0').replace('/','~1'))
 elif isinstance(v,list):
  for i,x in enumerate(v):walk(x,path+'/'+str(i))
walk(root)
with (R/refs['rawJsonIndexPath']).open(newline='') as f:spans=list(csv.DictReader(f))
assert len(spans)==len(objects)==identity['rawDictionaryObjectRows'];assert len({v['json_pointer'] for v in spans})==len(spans)
for v in spans:
 lo,hi=int(v['raw_start_byte']),int(v['raw_end_byte_exclusive']);b=raw[lo:hi];assert 0<=lo<hi<=len(raw) and len(b)==int(v['raw_bytes']) and sha(b)==v['sha256_raw_span'];assert json.loads(b)==objects[v['json_pointer']] and v['independent_review_status']=='UNINSPECTED'
base=R/'audit/gpt61-sol-high-comprehensive-2026-10-19';br=read(base/'post-recording-guard-host-regression-2026-10-22/REGRESSION/RESULT.json');fu=read(base/'engineering-source-binding-reconciliation-2026-10-22/REGRESSION/RESULT.json');bm={v['testClass']+'#'+v['method']:v for v in br['tests']};fm={v['testClass']+'#'+v['method']:v for v in fu['tests']};cache={}
with (D/'HOST_METHOD_EVIDENCE_INDEX.csv').open(newline='') as f:methods=list(csv.DictReader(f))
assert len(methods)==len(bm)==1334 and len({v['identity'] for v in methods})==1334
for v in methods:
 k=v['identity'];assert v['broad_source']==br['sourceCommit'] and v['broad_outcome']==bm[k]['outcome'];assert v['followup_outcome']==(fm[k]['outcome'] if k in fm else 'NOT_SELECTED');assert v['followup_source']==(fu['sourceCommit'] if k in fm else '');assert v['independent_scenario_review']=='UNINSPECTED'
 rel=v['target_source_file']
 if rel not in cache:cache[rel]=sha(git('show',source+':'+rel))
 assert cache[rel]==v['target_file_sha256']
for k,v in identity['followupInputTrees'].items():assert git('rev-parse',source+':'+k).decode().strip()==v
print(json.dumps(dict(targetCommit=source,trackedFiles=len(expected),rawDictionaryObjects=len(objects),hostIdentityRows=1334,separateFollowupIdentities=56,exactGitInventoryAndAllRawSpansVerified=True,manifestAndReusedInputHashesVerified=True,separateScopeColumnsVerified=True,noCombinedSuiteResult=True,semanticInspection=False,allIndependentStatuses='UNINSPECTED',independentAuditExecuted=False,physicalAcceptance='OUTSTANDING_LAST'),indent=2))
