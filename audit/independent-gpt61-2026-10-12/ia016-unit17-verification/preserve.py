import json,hashlib,subprocess,runpy,tempfile,re
from pathlib import Path
r=Path('/workspace/russian-companion');o=r/'audit/independent-gpt61-2026-10-12/ia016-unit17-verification';base='151cae10b653be9038423e0d19afca2cf9a48b18';asset='app/src/main/assets/content/v2-curriculum.json';raw=subprocess.check_output(['git','show',base+':'+asset],cwd=r);now=(r/asset).read_bytes();a=json.loads(raw);b=json.loads(now);l=json.loads((r/'audit/content-corrections/UNIT17_QUESTION_TRANSFER_CORRECTION.json').read_text());changes={x['before']['id']:x for x in l['changes']}
assert len(a['provenanceRecords'])==6596 and len(b['provenanceRecords'])==6638 and b['provenanceRecords'][:6596]==a['provenanceRecords']
assert [x['itemId'] for x in b['provenanceRecords'][6596:]]==l['addedProvenanceIds']
for x,y in zip(a['items'],b['items']):
 if x['id'] in changes:
  assert x==changes[x['id']]['before'] and y==changes[x['id']]['after']
  assert x['kind']=='CONTRAST_SELECTION' and y['kind']=='SOURCE_MEANING' and y['objectiveId']=='obj-u17-clock-vocabulary' and 'assessment' not in y
  for k in x:
   if k not in ['kind','objectiveId','assessment']:assert x[k]==y[k],(x['id'],k)
 else:assert x==y,x['id']
assert b['items'][len(a['items']):]==l['addedItems']
assert b['examples'][:len(a['examples'])]==a['examples'] and b['examples'][len(a['examples']):]==l['addedExamples']
for k in a:
 if k not in ['items','examples','units','provenanceRecords']:assert a[k]==b[k],k
assert len(b['sourceCapabilities'])==58 and len(b['sources'])==len(b['rights'])==36
for x,y in zip(a['units'],b['units']):
 if x['number']!=17:assert x==y,x['number']
 else:
  for k in x:
   if k not in ['objectives','sessions']:assert x[k]==y[k],k
  for field in ['objectives','sessions']:
   for old,new in zip(x[field],y[field]):
    if old['id'] in changes:assert old==changes[old['id']]['before'] and new==changes[old['id']]['after']
    else:assert old==new,old['id']
  assert y['objectives'][len(x['objectives']):]==l['addedObjectives']
script=runpy.run_path(str(r/'audit/content-corrections/repair_unit17_question_transfer.py'))
source=(r/'catalog/sources/units-3-5/wikibooks/interrogative-4220316.txt').read_text()
for n in l['nativeSelections']:
 assert n['originalSourceText'] in source and n['shippedText'] in script['visible'](n['originalSourceText']) and hashlib.sha256(n['originalSourceText'].encode()).hexdigest()==n['sourceHash']
 assert 'Wikibooks:Copyrights oldid 4622060' in n['attribution']
with tempfile.TemporaryDirectory(dir='/tmp') as d:
 t=Path(d);pack=t/'pack.json';code=t/'pins.kt';pack.write_bytes(raw)
 main=script['main'];main.__globals__.update(PACK=pack,HERE=t,ROOT=r,PIN_FILE=code);main();assert pack.read_bytes()==now;assert code.read_bytes()==(r/'app/src/main/java/dev/russiancompanion/v2/curriculum/Unit17QuestionPins.kt').read_bytes();main();assert pack.read_bytes()==now
f='app/src/main/java/dev/russiancompanion/v2/curriculum/CompleteRuleContextPins.kt';old=dict(re.findall(r'"(ca-rule-context:[^"]+)" to "([a-f0-9]+)"',subprocess.check_output(['git','show',base+':'+f],cwd=r).decode()));new=dict(re.findall(r'"(ca-rule-context:[^"]+)" to "([a-f0-9]+)"',(r/f).read_text()));assert len(old)==58 and len(new)==61 and all(new[k]==v for k,v in old.items())
receipt=json.loads((r/'audit/independent-gpt61-2026-10-12/RESULTS_RECEIPT.json').read_text())
for f in receipt['files']:
 p=r/'audit/independent-gpt61-2026-10-12'/f['path'];assert hashlib.sha256(p.read_bytes()).hexdigest()==f['sha256'] and p.stat().st_size==f['bytes']
x=dict(baselineCommit=base,oldProvenanceRecords=6596,newPendingRecords=42,newNativeSelections=7,newOptionalNativeRebindings=3,newEnglishContextCoverageRecords=32,allPriorProvenanceUnchanged=True,all58CapabilitiesUnchanged=True,allSourcesRightsMappingsAndOtherPackFieldsUnchanged=True,allOtherUnitsUnchanged=True,allOriginalExamplesUnchanged=True,originalThreeGlossItemsKeepCueKeyOptionsFeedbackSourceAndRationales=True,threeOldGlossItemsExplicitlyMovedToOptionalVocabulary=True,all58PriorCanonicalPinsUnchanged=True,newContextPins=3,addedApplications=3,newGuidedApplications=1,newIndependentApplications=2,addedExamples=2,allNativeWordsCaseEndingsSpellingAndQuestionMarksUnchanged=True,allNewNativeSelectionsOccurInExactFrozenSource=True,deterministicBaselineReplay=True,nativePinCodeReplayByteIdentical=True,byteIdempotent=True,allNineOriginalDiagnosticDeliverableHashesUnchanged=True,rawCurriculumSha256=hashlib.sha256(now).hexdigest(),frozenPublicSources=l['frozenPublicSources'],semanticAcceptance=False,productiveClockQuestionClaim=False,allQuestionTransferClaim=False)
(o/'PRESERVATION.json').write_text(json.dumps(x,indent=2)+'\n');print(json.dumps(x,indent=2))
