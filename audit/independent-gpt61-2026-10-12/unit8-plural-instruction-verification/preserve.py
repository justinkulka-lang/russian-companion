import sys,json,subprocess,hashlib,tempfile,runpy
from pathlib import Path
R=Path(__file__).resolve().parents[3];sys.path.insert(0,str(R/'catalog'))
from curriculum_storage import loads
O=Path(__file__).resolve().parent;L=json.load(open(R/'audit/content-corrections/UNIT8_PLURAL_INSTRUCTION_CORRECTION.json'));git=lambda p:subprocess.check_output(['git','show',L['baselineCommit']+':'+p],cwd=R)
asset='app/src/main/assets/content/v2-curriculum.json';old=loads(git(asset));new=loads((R/asset).read_bytes());records={r['itemId']:r for r in new['provenanceRecords']}
for key in old:
 if key not in ['units','provenanceRecords']:assert old[key]==new[key],key
assert all(records[r['itemId']]==r for r in old['provenanceRecords']);assert len(new['provenanceRecords'])-len(old['provenanceRecords'])==21
for a,b in zip(old['units'],new['units']):
 if a['number']!=8:assert a==b
 else:
  for o in a['objectives']:
   if o['id']!='obj-u8-plural':assert o==next(x for x in b['objectives'] if x['id']==o['id'])
  for s in a['sessions']:
   if s['id']!='u8-s-plural':assert s==next(x for x in b['sessions'] if x['id']==s['id'])
with tempfile.TemporaryDirectory(dir='/tmp',prefix='u8-plural-replay-') as tmp:
 r=Path(tmp);p=r/asset;p.parent.mkdir(parents=True);p.write_bytes(git(asset));(r/'audit/content-corrections').mkdir(parents=True);ns=runpy.run_path(str(R/'audit/content-corrections/repair_unit8_plural_instruction.py'));ns['main'].__globals__.update(ROOT=r,PACK=p);ns['main']();assert p.read_bytes()==(R/asset).read_bytes();raw=p.read_bytes();ns['main']();assert p.read_bytes()==raw
receipt=json.load(open(R/'audit/independent-gpt61-2026-10-12/RESULTS_RECEIPT.json'))
for f in receipt['files']:
 raw=(R/'audit/independent-gpt61-2026-10-12'/f['path']).read_bytes();assert hashlib.sha256(raw).hexdigest()==f['sha256'] and len(raw)==f['bytes']
result=dict(baseline=L['baselineCommit'],oldProvenancePreserved=len(old['provenanceRecords']),newPendingEnglishRecords=21,totalProvenance=len(new['provenanceRecords']),allScoredItemsNativeExamplesSourceRightsMappingsCapabilitiesIdentical=True,other29UnitsAndOtherUnit8SessionsObjectivesIdentical=True,deterministicByteReplay=True,byteIdempotence=True,originalDiagnosticFilesPreserved=len(receipt['files']),formalAcceptance=False);(O/'PRESERVATION.json').write_text(json.dumps(result,indent=2)+'\n');print(result)
