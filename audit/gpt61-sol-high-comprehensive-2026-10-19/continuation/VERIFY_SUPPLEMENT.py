#!/usr/bin/env python3
"""Verify archive entries, authentic input Git objects and all projection values."""
import hashlib,json,sys
from pathlib import Path
sys.dont_write_bytecode=True
root=Path(__file__).resolve().parent
sha=lambda b:hashlib.sha256(b).hexdigest()
def git_hash(kind,b):return hashlib.sha1(kind.encode()+b' '+str(len(b)).encode()+b'\0'+b).hexdigest()
def tree(oid,proof):
 b=bytes.fromhex(proof['treeObjectsHex'][oid]);assert git_hash('tree',b)==oid;out={};pos=0
 while pos<len(b):
  end=b.index(b'\0',pos);mode,name=b[pos:end].split(b' ',1);out[name.decode()]=(mode.decode(),b[end+1:end+21].hex());pos=end+21
 return out
m=json.loads((root/'MANIFEST.json').read_text())
for row in m['files']:
 b=(root/row['path']).read_bytes();assert len(b)==row['bytes'] and sha(b)==row['sha256'],row['path']
assert {p.relative_to(root).as_posix() for p in root.rglob('*') if p.is_file()}=={r['path'] for r in m['files']}|{'MANIFEST.json'}
proof=json.loads((root/'SOURCE_GIT_PROOF.json').read_text());commit=bytes.fromhex(proof['commitObjectHex']);assert git_hash('commit',commit)==proof['target']==m['auditTarget'];assert commit.splitlines()[0].decode()=='tree '+proof['rootTree']
for path,expected in proof['paths'].items():
 oid=proof['rootTree']
 for component in path.split('/'):_,oid=tree(oid,proof)[component]
 assert oid==expected,path
raw=(root/'raw/v2-curriculum.json').read_bytes();loader=(root/'raw/curriculum_storage.py').read_bytes();assert git_hash('blob',raw)==proof['paths']['app/src/main/assets/content/v2-curriculum.json'];assert git_hash('blob',loader)==proof['paths']['catalog/curriculum_storage.py']
index=json.loads((root/'CURRICULUM_INDEX.json').read_text());assert len(raw)==index['rawBytes'] and sha(raw)==index['rawSha256']
namespace={'__name__':'pinned_curriculum_storage'};exec(compile(loader,'pinned_curriculum_storage.py','exec'),namespace);pack=namespace['loads'](raw)
rebuilt={}
for key,spec in index['collections'].items():
 if spec['kind']=='array':
  rows=[]
  for chunk in spec['chunks']:
   assert chunk['startIndex']==len(rows);values=json.loads((root/chunk['path']).read_bytes());assert len(values)==chunk['recordCount'];rows.extend(values)
  assert len(rows)==spec['recordCount'];rebuilt[key]=rows
 else:rebuilt[key]=json.loads((root/spec['path']).read_bytes())
assert rebuilt==pack,'Projection values differ from actual raw pack parsed with pinned loader'
assert [len(pack[k]) for k in ['units','items','examples','sourceCapabilities','provenanceRecords']]==[30,622,817,58,6943]
print('PASS: every entry hash/size; target commit/tree proofs; raw and loader Git blobs; all projection values equal parsed raw input. Integrity is not semantic audit acceptance.')
