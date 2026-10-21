"""Read-only mechanical inventory verification; never semantic audit completion."""
from pathlib import Path
import argparse,csv,hashlib,json,subprocess
p=argparse.ArgumentParser();p.add_argument('--repository',required=True);a=p.parse_args()
r=Path(a.repository);d=Path(__file__).parent;identity=json.loads((d/'PREPARATION_IDENTITY.json').read_text());source=identity['targetCommit']
def git(*args):return subprocess.check_output(['git',*args],cwd=r)
assert git('rev-parse',source+'^{tree}').decode().strip()==identity['targetRootTree']
expected={}
for row in git('ls-tree','-rlz',source).split(b'\0'):
 if not row:continue
 meta,path=row.split(b'\t',1);mode,typ,oid,size=meta.decode().split();expected[path.decode()]=(mode,typ,oid,size if size!='-' else '')
with (d/'TARGET_FILE_INVENTORY.csv').open(newline='') as f:rows=list(csv.DictReader(f))
assert len(rows)==len(expected)==identity['trackedFiles']
assert len({v['path'] for v in rows})==len(rows)
for v in rows:
 assert (v['mode'],v['type'],v['git_blob_oid'],v['bytes'])==expected[v['path']]
 assert v['independent_review_status']=='UNINSPECTED'
raw=git('show',source+':'+identity['rawAssetPath']);assert len(raw)==identity['rawAssetBytes'] and hashlib.sha256(raw).hexdigest()==identity['rawAssetSha256'];value=json.loads(raw);objects={}
def walk(v,path=''):
 if isinstance(v,dict):
  objects[path]=v
  for k,x in v.items():walk(x,path+'/'+k.replace('~','~0').replace('/','~1'))
 elif isinstance(v,list):
  for i,x in enumerate(v):walk(x,path+'/'+str(i))
walk(value)
with (d/'RAW_JSON_OBJECT_INDEX.csv').open(newline='') as f:rows=list(csv.DictReader(f))
assert len(rows)==len(objects)==identity['jsonObjectRows'];assert len({v['json_pointer'] for v in rows})==len(rows)
assert {v['json_pointer'] for v in rows}==set(objects)
for v in rows:
 lo,hi=int(v['raw_start_byte']),int(v['raw_end_byte_exclusive']);assert 0<=lo<hi<=len(raw);span=raw[lo:hi]
 assert len(span)==int(v['raw_bytes']) and hashlib.sha256(span).hexdigest()==v['sha256_raw_span'];assert json.loads(span)==objects[v['json_pointer']];assert v['independent_review_status']=='UNINSPECTED'
print(json.dumps({'targetCommit':source,'trackedFiles':len(expected),'rawDictionaryObjects':len(objects),'exactGitInventoryAndRawSpansVerified':True,'semanticInspection':False,'allReviewStatuses':'UNINSPECTED'}))
