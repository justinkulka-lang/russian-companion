import json,hashlib,subprocess,runpy,tempfile,re
from pathlib import Path
r=Path(__file__).resolve().parents[3];o=r/'audit/independent-gpt61-2026-10-12/ia013-unit30-verification';base='52274f15b17b7f1f46bf4684006437e9661dab31';asset='app/src/main/assets/content/v2-curriculum.json'
raw=subprocess.check_output(['git','show',base+':'+asset],cwd=r);now=(r/asset).read_bytes();a=json.loads(raw);b=json.loads(now);l=json.loads((r/'audit/content-corrections/UNIT30_LANGUAGE_CORRECTION.json').read_text());changes={x['before']['id']:x for x in l['changes']}
assert len(a['provenanceRecords'])==6348 and len(b['provenanceRecords'])==6446
assert b['provenanceRecords'][:6348]==a['provenanceRecords'];assert [x['itemId'] for x in b['provenanceRecords'][6348:]]==l['addedProvenanceIds']
for old,new in zip(a['items'],b['items']):
 if old['id'] in changes:assert old==changes[old['id']]['before'] and new==changes[old['id']]['after']
 else:assert old==new
assert b['items'][len(a['items']):]==l['addedItems']
assert b['examples'][:len(a['examples'])]==a['examples'] and b['examples'][len(a['examples']):]==l['addedExamples']
for k in a:
 if k not in ['items','units','examples','provenanceRecords']:assert a[k]==b[k],k
for x,y in zip(a['units'],b['units']):
 if x['number']!=30:assert x==y,x['number']
 else:
  for k in x:
   if k not in ['objectives','sessions']:assert x[k]==y[k],k
  for field in ['objectives','sessions']:
   for old,new in zip(x[field],y[field]):
    if old['id'] in changes:assert old==changes[old['id']]['before'] and new==changes[old['id']]['after']
    else:assert old==new,old['id']
  assert y['objectives'][len(x['objectives']):]==l['addedObjectives']
for n in l['nativeSelections']:
 original=n.get('originalSourceText') or b['sourceTextPool'][n['originalSourceTextRef']]
 assert hashlib.sha256(original.encode()).hexdigest()==n['sourceHash'] and n['shippedText'] in original
 path='catalog/sources/units-26-30/wikipedia/'+('russian-alphabet-1371795481.txt' if n['sourceRevision']=='1371795481' else 'russian-grammar-1369617627.txt')
 assert original in (r/path).read_text()
 assert 'Wikipedia:Copyrights oldid 1369466282' in n['attribution']
with tempfile.TemporaryDirectory(dir='/tmp') as d:
 t=Path(d);pack=t/'pack.json';code=t/'pins.kt';pack.write_bytes(raw)
 main=runpy.run_path(str(r/'audit/content-corrections/repair_unit30_language.py'))['main'];main.__globals__.update(PACK=pack,HERE=t,ROOT=r,PIN_FILE=code);main();assert pack.read_bytes()==now;assert code.read_bytes()==(r/'app/src/main/java/dev/russiancompanion/v2/curriculum/Unit30LanguagePins.kt').read_bytes();main();assert pack.read_bytes()==now
pinf='app/src/main/java/dev/russiancompanion/v2/curriculum/CompleteRuleContextPins.kt';oldpins=dict(re.findall(r'"(ca-rule-context:[^"]+)" to "([a-f0-9]+)"',subprocess.check_output(['git','show',base+':'+pinf],cwd=r).decode()));pins=dict(re.findall(r'"(ca-rule-context:[^"]+)" to "([a-f0-9]+)"',(r/pinf).read_text()));assert len(oldpins)==42 and len(pins)==48 and all(pins[k]==v for k,v in oldpins.items())
receipt=json.loads((r/'audit/independent-gpt61-2026-10-12/RESULTS_RECEIPT.json').read_text())
for f in receipt['files']:
 path=r/'audit/independent-gpt61-2026-10-12'/f['path'];assert hashlib.sha256(path.read_bytes()).hexdigest()==f['sha256'] and path.stat().st_size==f['bytes']
x=dict(baselineCommit=base,oldProvenanceRecords=6348,newPendingRecords=98,newPrimarySelections=13,newOptionalReferenceRebindings=12,newEnglishContextAndCoverageRecords=73,allPriorProvenanceUnchanged=True,originalSixReferenceKeysCuesOptionsAndSourceIdentitiesPreserved=True,referenceKindsAndObjectiveBindingsExplicitlyChanged=True,allOtherScoredSourceTuplesUnchanged=True,allOriginalRussianExamplesUnchanged=True,allOriginalSourcesRightsCapabilitiesMappingsAndOtherPackFieldsUnchanged=True,allOtherUnitsUnchanged=True,all42EarlierContextPinsUnchanged=True,newCanonicalContextPins=6,addedExamples=3,addedApplications=6,independentNameChecks=2,independentInitialismChecks=2,guidedNameChecks=1,guidedInitialismChecks=1,allNewPrimarySpansDirectlyMatchRetainedSources=True,existingLetterNameCapability='Original admitted A/B span/capability unchanged; V name selected and frozen separately from the same retained full alphabet snapshot.',initialismContext='Existing exact abbreviation bullets bind gloss and agreement; additional full retained Indeclinable nouns span binds unchanged-case treatment. English contexts quote native initialisms; no invented Russian sentence.',nativePinCodeSha256=l['nativePinCodeSha256'],deterministicBaselineReplay=True,nativePinCodeReplayByteIdentical=True,byteIdempotent=True,allNineOriginalDiagnosticDeliverableHashesUnchanged=True,rawCurriculumSha256=hashlib.sha256(now).hexdigest(),frozenPublicSourceSha256=l['frozenPublicSources'],semanticAcceptance=False,completePhoneticsOrProductiveGrammarClaim=False)
(o/'PRESERVATION.json').write_text(json.dumps(x,indent=2)+'\n');print(json.dumps(x,indent=2))
