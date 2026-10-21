"""Reproduce content integrity from separately obtained pinned projections; no Android/PDF access."""
import argparse, hashlib, importlib.util, json
from pathlib import Path

def git_hash(kind,data):
    return hashlib.sha1(kind.encode()+b' '+str(len(data)).encode()+b'\0'+data).hexdigest()

def entries(data):
    result={};offset=0
    while offset<len(data):
        sep=data.index(b' ',offset);zero=data.index(b'\0',sep)
        mode=data[offset:sep].decode();name=data[sep+1:zero].decode()
        result[name]=(mode,data[zero+1:zero+21].hex());offset=zero+21
    assert offset==len(data)
    return result

ap=argparse.ArgumentParser()
ap.add_argument('--index',type=Path,required=True)
ap.add_argument('--projection-root',type=Path,required=True)
ap.add_argument('--codec',type=Path,required=True)
ap.add_argument('--proof',type=Path,required=True)
ap.add_argument('--inventory',type=Path,required=True)
args=ap.parse_args()
idx=json.loads(args.index.read_text());pack={};count=0
for name,spec in idx['collections'].items():
    chunks=spec.get('chunks',[spec]) if spec['kind']=='array' else [spec]
    values=[]
    for chunk in chunks:
        data=(args.projection_root/chunk['path']).read_bytes()
        assert hashlib.sha256(data).hexdigest()==chunk['sha256'],chunk['path']
        if 'bytes' in chunk:assert len(data)==chunk['bytes']
        value=json.loads(data);count+=1
        if spec['kind']=='array':
            assert len(values)==chunk['startIndex'];values+=value
        else:values=value
    pack[name]=values
    if spec['kind']=='array':assert len(values)==spec['recordCount']
assert count==357
codec=args.codec.read_bytes()
assert git_hash('blob',codec)=='2a061577a2a18bd68c8352581c02fb784955cf5e'
module_spec=importlib.util.spec_from_file_location('pinned_codec',args.codec)
module=importlib.util.module_from_spec(module_spec);module_spec.loader.exec_module(module)
raw=module.dumps(pack).encode('utf-8')
assert len(raw)==20036214
assert hashlib.sha256(raw).hexdigest()=='33e5655c99d6d5003da0ab1af9249dc814472e3436e0d28afd2f878f6ade4940'
assert git_hash('blob',raw)=='e2f75a6fcac82b97637492fb8410cf48ca9fb4ef'
proof=json.loads(args.proof.read_text())
commit=bytes.fromhex(proof['commitObjectHex'])
assert git_hash('commit',commit)=='5c176eb22f4a93bf5f7fd20af15e79ec8c4748ed'
root=commit.decode().splitlines()[0].split()[1]
assert root=='f95bfa3c1ae6b8ac07b42b6db9fa69c5e247830f'
trees={key:bytes.fromhex(value) for key,value in proof['treeObjectsHex'].items()}
for key,value in trees.items():assert git_hash('tree',value)==key
for path,expected in proof['paths'].items():
    actual=root
    for part in path.split('/'):actual=entries(trees[actual])[part][1]
    assert actual==expected
inventory=json.loads(args.inventory.read_text())['tree']
actual_trees={x['path']:x for x in inventory if x['type']=='tree'}
blobs=[x for x in inventory if x['type']=='blob'];assert len(blobs)==6427
rebuilt={}
for parent in sorted(['']+list(actual_trees),key=lambda x:x.count('/')+(bool(x)),reverse=True):
    children=[]
    for x in inventory:
        path=x['path'];folder,_,name=path.rpartition('/')
        if folder==parent:
            child_sha=rebuilt[path] if x['type']=='tree' else x['sha']
            children.append((name,x['mode'],child_sha,x['type']))
    children.sort(key=lambda x:(x[0]+('/' if x[3]=='tree' else '')).encode())
    data=b''.join((('40000' if typ=='tree' else mode)+' '+name).encode()+b'\0'+bytes.fromhex(h) for name,mode,h,typ in children)
    digest=git_hash('tree',data)
    assert digest==(actual_trees[parent]['sha'] if parent else root),parent
    rebuilt[parent]=digest
assert len(rebuilt)==453
for record in pack['provenanceRecords']:
    for field,h in [('originalSourceText','sourceHash'),('shippedText','contentHash')]:
        assert hashlib.sha256(record[field].encode()).hexdigest()==record[h]
for cap in pack['sourceCapabilities']:assert hashlib.sha256(cap['exactSpan'].encode()).hexdigest()==cap['sourceHash']
required={iid for unit in pack['units'] for o in unit['objectives'] if o.get('gating',True) for iid in o['checkItemIds']}
first={item['id'] for item in pack['items'] if item['options'][0]==item['answer']}
assert len(first)==561 and len(required)==429 and len(first & required)==379
print(json.dumps({'outcome':'PASS_INPUT_INTEGRITY_ONLY','projectionFiles':count,'rawBytes':len(raw),'rawSha256':hashlib.sha256(raw).hexdigest(),'commit':proof['target'],'tree':root,'treeObjects':len(rebuilt),'items':len(pack['items']),'examples':len(pack['examples']),'capabilities':len(pack['sourceCapabilities']),'provenance':len(pack['provenanceRecords']),'firstKeys':len(first),'requiredChecks':len(required),'firstRequired':len(first & required),'zipVerified':False,'androidExecuted':False},indent=2))
