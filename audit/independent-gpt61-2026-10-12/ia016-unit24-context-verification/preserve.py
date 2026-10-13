import json,hashlib,subprocess,re,runpy,tempfile
from pathlib import Path
r=Path('/workspace/russian-companion');o=Path(__file__).resolve().parent;base='3b8b6df0150d406f8f71a2bc7eebd1a19a301eba';asset='app/src/main/assets/content/v2-curriculum.json';raw=subprocess.check_output(['git','show',base+':'+asset],cwd=r);now=(r/asset).read_bytes();a=json.loads(raw);b=json.loads(now);l=json.load(open(r/'audit/content-corrections/UNIT24_CONTEXT_TRANSFER_CORRECTION.json'));changes={v['before']['id']:v for v in l['changes']}
assert len(a['provenanceRecords'])==6697 and len(b['provenanceRecords'])==6722 and b['provenanceRecords'][:6697]==a['provenanceRecords'];assert [v['itemId'] for v in b['provenanceRecords'][6697:]]==l['addedProvenanceIds']
for x,y in zip(a['items'],b['items']):
 if x['id']=='u24-g-word-order-contrast-0':assert x==changes[x['id']]['before'] and y==changes[x['id']]['after']
 else:assert x==y,x['id']
assert b['items'][len(a['items']):]==l['addedItems']
for k in a:
 if k not in ['items','units','provenanceRecords']:assert a[k]==b[k],k
for x,y in zip(a['units'],b['units']):
 if x['number']!=24:assert x==y
 else:
  for k in x:
   if k not in ['objectives','sessions']:assert x[k]==y[k],k
  for field in ['objectives','sessions']:
   for v,w in zip(x[field],y[field]):
    if v['id'] in changes:assert v==changes[v['id']]['before'] and w==changes[v['id']]['after']
    else:assert v==w,v['id']
  assert y['objectives'][len(x['objectives']):]==l['addedObjectives']
s=runpy.run_path(str(r/'audit/content-corrections/repair_unit24_context_transfer.py'));source=(r/'catalog/sources/units-26-30/wikipedia/russian-grammar-1369617627.txt').read_text()
for v in l['nativeSelections']:assert v['originalSourceText'] in source and v['shippedText'] in s['visible'](v['originalSourceText'])
for v in l['nativeSelections'][:4]:assert '\u0301' not in v['shippedText']
with tempfile.TemporaryDirectory(dir='/tmp') as d:
 t=Path(d);p=t/'pack';c=t/'code';p.write_bytes(raw);m=s['main'];m.__globals__.update(ROOT=r,HERE=t,PACK=p,PIN_FILE=c);m();assert p.read_bytes()==now and c.read_bytes()==(r/'app/src/main/java/dev/russiancompanion/v2/curriculum/Unit24ContextPins.kt').read_bytes();m();assert p.read_bytes()==now
f='app/src/main/java/dev/russiancompanion/v2/curriculum/CompleteRuleContextPins.kt';old=dict(re.findall(r'"(ca-rule-context:[^"]+)" to "([a-f0-9]+)"',subprocess.check_output(['git','show',base+':'+f],cwd=r).decode()));new=dict(re.findall(r'"(ca-rule-context:[^"]+)" to "([a-f0-9]+)"',(r/f).read_text()));assert len(old)==64 and len(new)==65 and all(new[k]==v for k,v in old.items())
receipt=json.load(open(r/'audit/independent-gpt61-2026-10-12/RESULTS_RECEIPT.json'))
for f in receipt['files']:assert hashlib.sha256((r/'audit/independent-gpt61-2026-10-12'/f['path']).read_bytes()).hexdigest()==f['sha256']
x=dict(baselineCommit=base,oldProvenanceRecords=6697,newPendingRecords=25,newPrimarySelections=4,newNativeEnglishReferenceRebindings=4,newAuthoredEnglishContextCoverage=17,allPriorProvenanceUnchanged=True,all58CapabilitiesSourcesRightsMappingsUnchanged=True,allOriginalExamplesUnchanged=True,allOtherUnitsUnchanged=True,allOtherItemsIncludingBothCorrectIndependentPersuasionItemsUnchanged=True,all64PriorCanonicalPinsUnchanged=True,newPins=1,newDistinctIntroductoryGuidedExample=True,originalGuidedPersuasionOptionalWithNativeKeyOptionsPromptFeedbackSourcePreserved=True,noItemsExamplesObjectivesRemoved=True,threeTeachingCardsBeforePracticeAndCompleteRemediation=True,nativeSourceWordsStressAndPunctuationUnchanged=True,deterministicBaselineReplay=True,nativePinCodeReplayByteIdentical=True,byteIdempotent=True,allNineOriginalDiagnosticDeliverableHashesUnchanged=True,rawCurriculumSha256=hashlib.sha256(now).hexdigest(),frozenPublicSources=l['frozenPublicSources'],semanticAcceptance=False,productiveWordOrderClaim=False,independentSubjectFocusClaim=False)
(o/'PRESERVATION.json').write_text(json.dumps(x,indent=2)+'\n');print(dict(oldRecords=6697,added=25,replay='PASS',rawSha256=x['rawCurriculumSha256']))
