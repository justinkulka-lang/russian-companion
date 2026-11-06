"""Lossless, hash-bound source-text pooling for the single offline curriculum asset.

loads returns the ordinary model document. dumps pools repeated long provenance
spans while preserving every record. Historical transformers can consume an
expanded file via the CLI and pool their result afterwards.
"""
from __future__ import annotations
from collections import Counter
from copy import deepcopy
import hashlib,json,re,sys
from pathlib import Path
POOL='sourceTextPool'
REF='originalSourceTextRef'
def digest(text:str)->str:return hashlib.sha256(text.encode('utf-8')).hexdigest()
def expand(document:dict)->dict:
 d=deepcopy(document);pool=d.pop(POOL,{})
 if not isinstance(pool,dict):raise ValueError('sourceTextPool must be an object')
 for key,text in pool.items():
  if not re.fullmatch('[0-9a-f]{64}',key) or not isinstance(text,str) or digest(text)!=key:raise ValueError('Source pool hash mismatch')
 used=set()
 for r in d.get('provenanceRecords',[]):
  if REF not in r:continue
  key=r.pop(REF)
  if 'originalSourceText' in r or not isinstance(key,str) or key not in pool or r.get('sourceHash')!=key:raise ValueError('Invalid source-text reference')
  r['originalSourceText']=pool[key];used.add(key)
 if used!=set(pool):raise ValueError('Unreferenced source-pool entry')
 return d
def loads(text:str|bytes)->dict:return expand(json.loads(text))
def encode(document:dict)->dict:
 d=expand(document);counts=Counter(r['originalSourceText'] for r in d.get('provenanceRecords',[]));pool={}
 for r in d.get('provenanceRecords',[]):
  text=r['originalSourceText']
  if counts[text]<2 or len(text.encode('utf-8'))<=256:continue
  key=digest(text)
  if r.get('sourceHash')!=key:raise ValueError('Record source hash does not bind its text')
  pool[key]=text;del r['originalSourceText'];r[REF]=key
 if pool:d[POOL]=dict(sorted(pool.items()))
 return d
def dumps(document:dict)->str:return json.dumps(encode(document),ensure_ascii=False,separators=(',',':'))+'\n'
def main():
 action,src,dest=sys.argv[1:]
 d=loads(Path(src).read_bytes())
 text=dumps(d) if action=='pool' else json.dumps(d,ensure_ascii=False,separators=(',',':'))+'\n' if action=='expand' else None
 if text is None:raise ValueError('Expected pool or expand')
 Path(dest).write_text(text,encoding='utf-8')
if __name__=='__main__':main()
