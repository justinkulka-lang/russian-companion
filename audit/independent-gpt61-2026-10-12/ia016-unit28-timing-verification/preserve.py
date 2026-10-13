"""Exact preservation and deterministic repair replay; no qualified acceptance."""
import sys,json,hashlib,subprocess,tempfile,runpy
from pathlib import Path
ROOT=Path(__file__).resolve().parents[3];sys.path.insert(0,str(ROOT/'catalog'))
from curriculum_storage import loads
HERE=Path(__file__).resolve().parent
L=json.loads((ROOT/'audit/content-corrections/UNIT28_TIMING_TRANSFER_CORRECTION.json').read_text())
git=lambda path:subprocess.check_output(['git','show',L['baselineCommit']+':'+path],cwd=ROOT)
asset='app/src/main/assets/content/v2-curriculum.json';old=loads(git(asset));new=loads((ROOT/asset).read_bytes())
records={r['itemId']:r for r in new['provenanceRecords']}
assert all(records[r['itemId']]==r for r in old['provenanceRecords'])
assert len(new['provenanceRecords'])-len(old['provenanceRecords'])==76
for key in old:
 if key not in ['units','items','provenanceRecords','sourceCapabilities']:assert old[key]==new[key],key
for a,b in zip(old['units'],new['units']):
 if a['number']!=28:assert a==b
for a,b in zip(old['items'],new['items']):
 if a['objectiveId'] not in ['obj-u28-imperfective-adverb','obj-u28-perfective-adverb']:assert a==b
 elif a['assessment']['independent']:
  changed=json.loads(json.dumps(b))
  for k in ['relationParticipantIds','answerSourceIds','distractorSourceIds']:changed['assessment'][k]=a['assessment'][k]
  assert changed==a,a['id']
for a,b in zip(old['sourceCapabilities'],new['sourceCapabilities']):
 if a['id'] not in ['cap-obj-u28-imperfective-adverb','cap-obj-u28-perfective-adverb']:assert a==b
 else:
  assert b['exactSpan'].startswith(a['exactSpan']+'\n')
  assert {k:v for k,v in a.items() if k not in ['exactSpan','sourceHash']}=={k:v for k,v in b.items() if k not in ['exactSpan','sourceHash']}
with tempfile.TemporaryDirectory(prefix='u28-replay-',dir='/tmp') as tmp:
 r=Path(tmp);paths=[asset,'catalog/sources/units-26-30/wikisource/vechera-5336197.txt','catalog/sources/units-3-5/openrussian/verbs.csv','app/src/main/java/dev/russiancompanion/v2/curriculum/ContentAuditCapabilityPins.kt']
 for path in paths:
  f=r/path;f.parent.mkdir(parents=True,exist_ok=True);f.write_bytes(git(path))
 (r/'audit/content-corrections').mkdir(parents=True)
 ns=runpy.run_path(str(ROOT/'audit/content-corrections/repair_unit28_timing_transfer.py'));ns['main'].__globals__.update(ROOT=r,PACK=r/asset)
 ns['main']()
 for path in [asset,'app/src/main/java/dev/russiancompanion/v2/curriculum/Unit28TimingPins.kt','app/src/main/java/dev/russiancompanion/v2/curriculum/ContentAuditCapabilityPins.kt']:
  assert (r/path).read_bytes()==(ROOT/path).read_bytes(),path
 before=(r/asset).read_bytes();ns['main']();assert before==(r/asset).read_bytes()
receipt=json.loads((ROOT/'audit/independent-gpt61-2026-10-12/RESULTS_RECEIPT.json').read_text())
for f in receipt['files']:
 raw=(ROOT/'audit/independent-gpt61-2026-10-12'/f['path']).read_bytes();assert len(raw)==f['bytes'] and hashlib.sha256(raw).hexdigest()==f['sha256']
result=dict(baselineCommit=L['baselineCommit'],oldProvenancePreserved=len(old['provenanceRecords']),newPendingProvenance=76,totalProvenance=len(new['provenanceRecords']),otherUnitsIdentical=29,otherCapabilitiesIdentical=56,originalExamplesSourcesRightsMappingsPreserved=True,independentScoredFieldsUnchanged=True,capabilityPrefixesAndIdentityFieldsPreserved=True,deterministicAssetNativePinCapabilityPinReplay=True,byteIdempotence=True,originalDiagnosticDeliverablesPreserved=len(receipt['files']),formalAcceptance=False)
(HERE/'PRESERVATION.json').write_text(json.dumps(result,indent=2)+'\n');print(result)
