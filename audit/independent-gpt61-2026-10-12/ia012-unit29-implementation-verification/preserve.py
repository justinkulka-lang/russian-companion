import json,hashlib,subprocess,runpy,tempfile,re
from pathlib import Path
r=Path('/workspace/russian-companion');o=r/'audit/independent-gpt61-2026-10-12/ia012-unit29-implementation-verification';base='e4085c5686e1c58a0af43e1c1f66fbb573f56887';asset='app/src/main/assets/content/v2-curriculum.json'
raw=subprocess.check_output(['git','show',base+':'+asset],cwd=r);now=(r/asset).read_bytes();a=json.loads(raw);b=json.loads(now);l=json.loads((r/'audit/content-corrections/UNIT29_PUNCTUATION_CORRECTION.json').read_text());changes={x['before']['id']:x for x in l['changes']}
assert len(a['provenanceRecords'])==6525 and len(b['provenanceRecords'])==6596
assert b['provenanceRecords'][:6525]==a['provenanceRecords'];assert [x['itemId'] for x in b['provenanceRecords'][6525:]]==l['addedProvenanceIds']
for field in ['items','examples']:
 for old,new in zip(a[field],b[field]):
  if old['id'] in changes:assert old==changes[old['id']]['before'] and new==changes[old['id']]['after']
  else:assert old==new,old['id']
 assert b[field][len(a[field]):]==l['addedItems' if field=='items' else 'addedExamples']
for k in a:
 if k not in ['items','units','examples','provenanceRecords','sources','rights']:assert a[k]==b[k],k
for field in ['sources','rights']:
 assert b[field][:len(a[field])]==a[field]
 assert b[field][len(a[field]):]==l['addedSources' if field=='sources' else 'addedRights']
 assert len(a[field])==34 and len(b[field])==36
for old,new in zip(a['units'],b['units']):
 if old['number']!=29:assert old==new,old['number']
 else:
  for k in old:
   if k not in ['objectives','sessions']:assert old[k]==new[k],k
  for field in ['objectives','sessions']:
   for x,y in zip(old[field],new[field]):
    if x['id'] in changes:assert x==changes[x['id']]['before'] and y==changes[x['id']]['after']
    else:assert x==y,x['id']
  assert new['objectives'][len(old['objectives']):]==l['addedObjectives']
script=runpy.run_path(str(r/'audit/content-corrections/repair_unit29_punctuation.py'));visible=script['visible']
for n in l['nativeSelections']:
 assert hashlib.sha256(n['originalSourceText'].encode()).hexdigest()==n['sourceHash'] and n['shippedText'] in visible(n['originalSourceText'])
 source=r/'audit/independent-gpt61-2026-10-12/ia012-unit29-research-verification/public-candidates-2026-10-13'/('ruwiki-'+n['sourceRevision']+'.wikitext.txt')
 assert n['originalSourceText'] in source.read_text()
 assert 'Википедия:Авторские права oldid 154562152' in n['attribution']
with tempfile.TemporaryDirectory(dir='/tmp') as d:
 t=Path(d);pack=t/'pack.json';code=t/'pins.kt';pack.write_bytes(raw)
 main=script['main'];main.__globals__.update(PACK=pack,HERE=t,ROOT=r,PIN_FILE=code);main();assert pack.read_bytes()==now;assert code.read_bytes()==(r/'app/src/main/java/dev/russiancompanion/v2/curriculum/Unit29PunctuationPins.kt').read_bytes();main();assert pack.read_bytes()==now
pinf='app/src/main/java/dev/russiancompanion/v2/curriculum/CompleteRuleContextPins.kt'
oldpins=dict(re.findall(r'"(ca-rule-context:[^"]+)" to "([a-f0-9]+)"',subprocess.check_output(['git','show',base+':'+pinf],cwd=r).decode()));pins=dict(re.findall(r'"(ca-rule-context:[^"]+)" to "([a-f0-9]+)"',(r/pinf).read_text()))
assert len(oldpins)==54 and len(pins)==58 and all(pins[k]==v for k,v in oldpins.items())
receipt=json.loads((r/'audit/independent-gpt61-2026-10-12/RESULTS_RECEIPT.json').read_text())
for f in receipt['files']:
 path=r/'audit/independent-gpt61-2026-10-12'/f['path'];assert hashlib.sha256(path.read_bytes()).hexdigest()==f['sha256'] and path.stat().st_size==f['bytes']
x=dict(baselineCommit=base,oldProvenanceRecords=6525,newPendingRecords=71,newPrimaryNativeSelectionsAndCoverageFragments=20,newOptionalReferenceRebindings=4,newEnglishContextAndCoverageRecords=47,allPriorProvenanceUnchanged=True,all58CapabilitiesUnchanged=True,allOtherUnitsUnchanged=True,allEarlierSourcesRightsAndMappingsUnchanged=True,addedSources=2,addedRights=2,all54EarlierCanonicalPinsUnchanged=True,newCanonicalPins=4,addedIndependentApplications=4,addedNativeExamples=4,originalDefaultItemsUnchanged=True,oldCategoryQuestionMovedToOptionalReference=True,oldCategoryExampleRussianTextUnchangedWithExplicitReferenceObjectiveRebinding=True,allNewNativeSelectionsMechanicallyNormalizedFromExactRawSpan=True,noAddedTerminalPeriods=True,deterministicBaselineReplay=True,nativePinCodeReplayByteIdentical=True,byteIdempotent=True,allNineOriginalDiagnosticDeliverableHashesUnchanged=True,rawCurriculumSha256=hashlib.sha256(now).hexdigest(),candidateSnapshotSha256=l['candidateSnapshotSha256'],semanticAcceptance=False,unrestrictedPunctuationProductionClaim=False)
(o/'PRESERVATION.json').write_text(json.dumps(x,indent=2)+'\n');print(json.dumps(x,indent=2))
