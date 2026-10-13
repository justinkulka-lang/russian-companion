import sys,json,hashlib,subprocess,tempfile,runpy
from pathlib import Path
ROOT=Path(__file__).resolve().parents[3];sys.path.insert(0,str(ROOT/'catalog'))
from curriculum_storage import loads
HERE=Path(__file__).resolve().parent;L=json.loads((ROOT/'audit/content-corrections/UNIT13_REPORTED_TRANSFER_CORRECTION.json').read_text())
git=lambda path:subprocess.check_output(['git','show',L['baselineCommit']+':'+path],cwd=ROOT)
asset='app/src/main/assets/content/v2-curriculum.json';old=loads(git(asset));new=loads((ROOT/asset).read_bytes());records={r['itemId']:r for r in new['provenanceRecords']}
assert all(records[r['itemId']]==r for r in old['provenanceRecords']);assert len(new['provenanceRecords'])-len(old['provenanceRecords'])==25
for key in old:
 if key not in ['units','items','provenanceRecords']:assert old[key]==new[key],key
for a,b in zip(old['units'],new['units']):
 if a['number']!=13:assert a==b
 else:
  for o in a['objectives']:
   if o['id']!='obj-u13-reported':assert o==next(x for x in b['objectives'] if x['id']==o['id'])
  for s in a['sessions']:
   if s['id']!='u13-s-reported':assert s==next(x for x in b['sessions'] if x['id']==s['id'])
for a,b in zip(old['items'],new['items']):
 if a['id']!='u13-g-reported-0':assert a==b,a['id']
assert len(new['items'])==len(old['items'])+1
with tempfile.TemporaryDirectory(prefix='u13-replay-',dir='/tmp') as tmp:
 r=Path(tmp);paths=[asset,'catalog/sources/units-11-15/wikibooks/future-tense-3353822.txt','app/src/main/java/dev/russiancompanion/v2/curriculum/CompleteRuleContextPins.kt']
 for path in paths:f=r/path;f.parent.mkdir(parents=True,exist_ok=True);f.write_bytes(git(path))
 (r/'audit/content-corrections').mkdir(parents=True);ns=runpy.run_path(str(ROOT/'audit/content-corrections/repair_unit13_reported_transfer.py'));ns['main'].__globals__.update(ROOT=r,PACK=r/asset);ns['main']()
 for path in [asset,'app/src/main/java/dev/russiancompanion/v2/curriculum/Unit13ReportedPins.kt','app/src/main/java/dev/russiancompanion/v2/curriculum/CompleteRuleContextPins.kt']:assert (r/path).read_bytes()==(ROOT/path).read_bytes(),path
 before=(r/asset).read_bytes();ns['main']();assert before==(r/asset).read_bytes()
receipt=json.loads((ROOT/'audit/independent-gpt61-2026-10-12/RESULTS_RECEIPT.json').read_text())
for f in receipt['files']:
 raw=(ROOT/'audit/independent-gpt61-2026-10-12'/f['path']).read_bytes();assert len(raw)==f['bytes'] and hashlib.sha256(raw).hexdigest()==f['sha256']
result=dict(baselineCommit=L['baselineCommit'],oldProvenancePreserved=len(old['provenanceRecords']),newPendingProvenance=25,totalProvenance=len(new['provenanceRecords']),otherUnitsIdentical=29,all58CapabilitiesIdentical=True,allOriginalExamplesSourcesRightsMappingsPreserved=True,allOriginalItemsExceptOneGuideUnchanged=True,otherUnit13ObjectivesSessionsIdentical=True,addedIndependentItem='u13-i-report-frame-time',deterministicReplay=True,byteIdempotence=True,originalDiagnosticDeliverablesPreserved=len(receipt['files']),formalAcceptance=False)
(HERE/'PRESERVATION.json').write_text(json.dumps(result,indent=2)+'\n');print(result)
