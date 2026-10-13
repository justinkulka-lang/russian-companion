import json,hashlib,subprocess,runpy,tempfile,re
from pathlib import Path
r=Path(__file__).resolve().parents[3];o=r/'audit/independent-gpt61-2026-10-12/ia014-unit20-verification';base='1ce6184a5bbe894624807c066600fa51de42ace3';asset='app/src/main/assets/content/v2-curriculum.json'
raw=subprocess.check_output(['git','show',base+':'+asset],cwd=r);now=(r/asset).read_bytes();a=json.loads(raw);b=json.loads(now);l=json.loads((r/'audit/content-corrections/UNIT20_DIRECTIONALITY_CORRECTION.json').read_text());changes={x['before']['id']:x for x in l['changes']}
assert len(a['provenanceRecords'])==6446 and len(b['provenanceRecords'])==6525
assert b['provenanceRecords'][:6446]==a['provenanceRecords'];assert [x['itemId'] for x in b['provenanceRecords'][6446:]]==l['addedProvenanceIds']
for old,new in zip(a['items'],b['items']):
 if old['id'] in changes:
  assert old==changes[old['id']]['before'] and new==changes[old['id']]['after']
  for k in old:
   if k!='assessment':assert old[k]==new[k],(old['id'],k)
  for k in old['assessment']:
   if k not in ['relationParticipantIds','answerSourceIds','distractorSourceIds']:assert old['assessment'][k]==new['assessment'][k],k
 else:assert old==new
assert b['items'][len(a['items']):]==l['addedItems']
assert b['examples'][:len(a['examples'])]==a['examples'] and b['examples'][len(a['examples']):]==l['addedExamples']
for k in a:
 if k not in ['items','units','examples','provenanceRecords','sourceCapabilities']:assert a[k]==b[k],k
for old,new in zip(a['sourceCapabilities'],b['sourceCapabilities']):
 if old['id'] in changes:
  assert old==changes[old['id']]['before'] and new==changes[old['id']]['after']
  for k in old:
   if k not in ['sourceHash','exactSpan']:assert old[k]==new[k],k
  assert new['exactSpan'].startswith(old['exactSpan']) and hashlib.sha256(new['exactSpan'].encode()).hexdigest()==new['sourceHash']
 else:assert old==new
for x,y in zip(a['units'],b['units']):
 if x['number']!=20:assert x==y,x['number']
 else:
  for k in x:
   if k not in ['objectives','sessions']:assert x[k]==y[k],k
  for field in ['objectives','sessions']:
   for old,new in zip(x[field],y[field]):
    if old['id'] in changes:assert old==changes[old['id']]['before'] and new==changes[old['id']]['after']
    else:assert old==new,old['id']
for n in l['nativeSelections']:
 assert hashlib.sha256(n['originalSourceText'].encode()).hexdigest()==n['sourceHash'] and n['shippedText'] in n['originalSourceText']
 assert n['originalSourceText'] in (r/'catalog/sources/units-26-30/wikipedia/russian-grammar-1369617627.txt').read_text()
 assert 'Wikipedia:Copyrights oldid 1369466282' in n['attribution']
with tempfile.TemporaryDirectory(dir='/tmp') as d:
 t=Path(d);pack=t/'pack.json';code=t/'pins.kt';pack.write_bytes(raw)
 main=runpy.run_path(str(r/'audit/content-corrections/repair_unit20_directionality.py'))['main'];main.__globals__.update(PACK=pack,HERE=t,ROOT=r,PIN_FILE=code);main();assert pack.read_bytes()==now;assert code.read_bytes()==(r/'app/src/main/java/dev/russiancompanion/v2/curriculum/Unit20DirectionalityPins.kt').read_bytes();main();assert pack.read_bytes()==now
pinf='app/src/main/java/dev/russiancompanion/v2/curriculum/CompleteRuleContextPins.kt';oldpins=dict(re.findall(r'"(ca-rule-context:[^"]+)" to "([a-f0-9]+)"',subprocess.check_output(['git','show',base+':'+pinf],cwd=r).decode()));pins=dict(re.findall(r'"(ca-rule-context:[^"]+)" to "([a-f0-9]+)"',(r/pinf).read_text()));assert len(oldpins)==48 and len(pins)==54 and all(pins[k]==v for k,v in oldpins.items())
f='app/src/main/java/dev/russiancompanion/v2/curriculum/ContentAuditCapabilityPins.kt';old=subprocess.check_output(['git','show',base+':'+f],cwd=r).decode();new=(r/f).read_text()
for x in l['changes']:
 if x['before']['id'] in ['cap-u20-motion-po-setting-out','cap-u20-motion-prefixed-path']:old=old.replace(x['before']['sourceHash'],x['after']['sourceHash'])
assert old==new
receipt=json.loads((r/'audit/independent-gpt61-2026-10-12/RESULTS_RECEIPT.json').read_text())
for f in receipt['files']:
 path=r/'audit/independent-gpt61-2026-10-12'/f['path'];assert hashlib.sha256(path.read_bytes()).hexdigest()==f['sha256'] and path.stat().st_size==f['bytes']
x=dict(baselineCommit=base,oldProvenanceRecords=6446,newPendingRecords=79,newPrimarySelections=12,newExistingApplicationProvenanceRebindings=8,newEnglishContextAndCoverageRecords=59,allPriorProvenanceUnchanged=True,allEarlierScoredTuplesAndSourceIdsUnchanged=True,twoEarlierApplicationParticipantIdsExplicitlyRebound=True,allOriginalRussianExamplesUnchanged=True,allOriginalSourcesRightsMappingsAndOtherPackFieldsUnchanged=True,unchangedCapabilities=56,explicitlyExtendedCorrectedCapabilities=['cap-u20-motion-po-setting-out','cap-u20-motion-prefixed-path'],correctedCapabilityPolicy='Original spans retained as exact prefixes and all original hosts kept. Append current complete teaching/remediation, freeze new hashes, add eight existing-application hosts and six teaching/remediation coverage hosts; preserve claim/unit/objective/source/revision/record identities and keyed tuples.',allOtherUnitsUnchanged=True,all48EarlierContextPinsUnchanged=True,newCanonicalContextPins=6,addedExamples=6,addedApplications=6,independentContextualDirectionalityChecks=4,guidedContextualDirectionalityChecks=2,allNewPrimarySpansDirectlyMatchRetainedSource=True,nativePinCodeSha256=l['nativePinCodeSha256'],deterministicBaselineReplay=True,nativePinCodeReplayByteIdentical=True,byteIdempotent=True,allNineOriginalDiagnosticDeliverableHashesUnchanged=True,rawCurriculumSha256=hashlib.sha256(now).hexdigest(),frozenPublicSourceSha256=l['frozenPublicSources'],semanticAcceptance=False,unrestrictedMotionGrammarClaim=False)
(o/'PRESERVATION.json').write_text(json.dumps(x,indent=2)+'\n');print(json.dumps(x,indent=2))
