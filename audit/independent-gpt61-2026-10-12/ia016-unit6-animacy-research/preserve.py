from pathlib import Path
import sys,subprocess,json,hashlib,tempfile,shutil
root=Path(__file__).resolve().parents[3];sys.path.insert(0,str(root/'catalog'))
from curriculum_storage import loads
base='550049c63a590c70c0d11cd47258452a1b03c50a';asset='app/src/main/assets/content/v2-curriculum.json';raw=subprocess.check_output(['git','show',base+':'+asset],cwd=root);old=loads(raw);now=loads((root/asset).read_bytes());ledger=json.loads((root/'audit/content-corrections/UNIT6_ANIMACY_TRANSFER_CORRECTION.json').read_text());sha=lambda b:hashlib.sha256(b).hexdigest()
assert len(old['provenanceRecords'])==6844 and len(now['provenanceRecords'])==6869
records={r['itemId']:r for r in now['provenanceRecords']};assert all(records[r['itemId']]==r for r in old['provenanceRecords'])
assert old['sourceCapabilities']==now['sourceCapabilities'] and len(now['sourceCapabilities'])==58
for key in ['sources','rights','ownedEditionMappings','authoredEnglishRecords','admissions','audioAssets','handwritingAssets']:assert old[key]==now[key],key
assert [i for i in old['items'] if i['id']!='u6-i-animacy-3']==[i for i in now['items'] if i['id']!='u6-i-animacy-3']
assert old['examples']==[e for e in now['examples'] if e['id']!='ex-u6-animacy-transfer-chair']
assert [u for u in old['units'] if u['number']!=6]==[u for u in now['units'] if u['number']!=6]
for k in ['objectives','sessions']:
 id='obj-u6-animacy' if k=='objectives' else 'u6-s-animacy';assert [x for x in old['units'][5][k] if x['id']!=id]==[x for x in now['units'][5][k] if x['id']!=id]
pins='app/src/main/java/dev/russiancompanion/v2/curriculum/CompleteRuleContextPins.kt';before=subprocess.check_output(['git','show',base+':'+pins],cwd=root).decode();after=(root/pins).read_text();rows=[s for s in before.splitlines() if ' to ' in s];assert len(rows)==67 and all(s in after for s in rows)
ident=json.loads((Path(__file__).parent/'SOURCE_CONTEXT_AND_BASELINE.json').read_text())
for s in ident['sourceIdentities']+ident['originalDiagnosticDeliverables']:assert sha((root/s['path']).read_bytes())==s['sha256']
with tempfile.TemporaryDirectory(prefix='unit6-animacy-') as tmp:
 t=Path(tmp)
 for f in ['catalog/curriculum_storage.py','audit/content-corrections/repair_unit6_animacy_transfer.py','catalog/sources/units-6-10/wikibooks/accusative-4330404.txt']:
  (t/f).parent.mkdir(parents=True,exist_ok=True);shutil.copyfile(root/f,t/f)
 (t/asset).parent.mkdir(parents=True,exist_ok=True);(t/asset).write_bytes(raw)
 subprocess.run([sys.executable,str(t/'audit/content-corrections/repair_unit6_animacy_transfer.py')],check=True,capture_output=True)
 assert (t/asset).read_bytes()==(root/asset).read_bytes();first=(t/asset).read_bytes()
 subprocess.run([sys.executable,str(t/'audit/content-corrections/repair_unit6_animacy_transfer.py')],check=True,capture_output=True);assert (t/asset).read_bytes()==first
result=dict(baselineCommit=base,rawCurriculumSha256=sha((root/asset).read_bytes()),precedingProvenancePreserved=6844,newPendingProvenance=25,totalProvenance=6869,all58CapabilitiesPreserved=True,all67PriorCanonicalPinsPreserved=True,newCanonicalPins=1,allSourcesRightsMappingsPreserved=True,allPriorExamplesPreserved=True,other29UnitsAndOtherUnit6ObjectivesSessionsPreserved=True,onlyChangedScoredItem='u6-i-animacy-3',deterministicReplay=True,idempotent=True,allNineOriginalDiagnosticHashesPreserved=True,semanticAcceptance=False)
(Path(__file__).parent/'PRESERVATION.json').write_text(json.dumps(result,indent=2)+'\n');print(json.dumps(result))
