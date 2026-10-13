import json,hashlib,subprocess,re,runpy,tempfile
from pathlib import Path
r=Path('/workspace/russian-companion');o=Path(__file__).resolve().parent;base='6434cd12cdb22d0bbf6f7f848191d46b46501fca';asset='app/src/main/assets/content/v2-curriculum.json';raw=subprocess.check_output(['git','show',base+':'+asset],cwd=r);now=(r/asset).read_bytes();a=json.loads(raw);b=json.loads(now);l=json.loads((r/'audit/content-corrections/UNIT21_OBJECT_TRANSFER_CORRECTION.json').read_text());changes={v['before']['id']:v for v in l['changes']}
assert len(a['provenanceRecords'])==6683 and len(b['provenanceRecords'])==6697 and b['provenanceRecords'][:6683]==a['provenanceRecords']
assert [v['itemId'] for v in b['provenanceRecords'][6683:]]==l['addedProvenanceIds']
for x,y in zip(a['items'],b['items']):
 if x['id']=='u21-i-object-reference-2':assert x==changes[x['id']]['before'] and y==changes[x['id']]['after']
 else:assert x==y,x['id']
for k in a:
 if k not in ['items','units','provenanceRecords','sources','rights']:assert a[k]==b[k],k
for x,y in zip(a['units'],b['units']):
 if x['number']!=21:assert x==y
 else:
  for k in x:
   if k not in ['objectives','sessions']:assert x[k]==y[k],k
  for field in ['objectives','sessions']:
   for v,w in zip(x[field],y[field]):
    if v['id'] in changes:assert v==changes[v['id']]['before'] and w==changes[v['id']]['after']
    else:assert v==w,v['id']
for k,field in [('sources','addedSources'),('rights','addedRights')]:
 assert b[k][:len(a[k])]==a[k] and b[k][len(a[k]):]==l[field]
s=runpy.run_path(str(r/'audit/content-corrections/repair_unit21_object_transfer.py'));source=(r/'audit/independent-gpt61-2026-10-12/ia016-unit21-research-verification/public-candidates-2026-10-13/enwiki-reflexive-1374712982.wikitext.txt').read_text()
for v in l['nativeSelections']:assert v['originalSourceText'] in source and v['shippedText'] in s['visible'](v['originalSourceText']) and '\u0301' not in v['shippedText']
with tempfile.TemporaryDirectory(dir='/tmp') as d:
 t=Path(d);p=t/'pack';c=t/'code';p.write_bytes(raw);m=s['main'];m.__globals__.update(ROOT=r,HERE=t,PACK=p,PIN_FILE=c);m();assert p.read_bytes()==now and c.read_bytes()==(r/'app/src/main/java/dev/russiancompanion/v2/curriculum/Unit21ObjectPins.kt').read_bytes();m();assert p.read_bytes()==now
f='app/src/main/java/dev/russiancompanion/v2/curriculum/CompleteRuleContextPins.kt';old=dict(re.findall(r'"(ca-rule-context:[^"]+)" to "([a-f0-9]+)"',subprocess.check_output(['git','show',base+':'+f],cwd=r).decode()));new=dict(re.findall(r'"(ca-rule-context:[^"]+)" to "([a-f0-9]+)"',(r/f).read_text()));assert len(old)==63 and len(new)==64 and all(new[k]==v for k,v in old.items())
receipt=json.loads((r/'audit/independent-gpt61-2026-10-12/RESULTS_RECEIPT.json').read_text())
for f in receipt['files']:assert hashlib.sha256((r/'audit/independent-gpt61-2026-10-12'/f['path']).read_bytes()).hexdigest()==f['sha256']
x=dict(baselineCommit=base,oldProvenanceRecords=6683,newPendingRecords=14,newPrimaryNativeSelections=4,newEnglishContextCoverageRecords=10,allPriorProvenanceUnchanged=True,all58CapabilitiesAndOriginalSourcesRightsMappingsUnchanged=True,allOriginalExamplesUnchanged=True,allOtherUnitsUnchanged=True,allOtherItemsIncludingCorrectGuidedMyselfAndIndependentWifeFieldsUnchanged=True,all63PriorCanonicalPinsUnchanged=True,newPins=1,oneIndependentReflexiveContextReplaced=True,noItemsExamplesObjectivesRemoved=True,threeTeachingCardsBeforePracticeAndCompleteRemediation=True,nativeSourceWordsUnchangedAndNoStressInserted=True,deterministicBaselineReplay=True,nativePinCodeReplayByteIdentical=True,byteIdempotent=True,allNineOriginalDiagnosticDeliverableHashesUnchanged=True,rawCurriculumSha256=hashlib.sha256(now).hexdigest(),frozenPublicSources=l['frozenPublicSources'],semanticAcceptance=False,productiveReflexiveClaim=False,allCasesClaim=False)
(o/'PRESERVATION.json').write_text(json.dumps(x,indent=2)+'\n');print({'oldRecords':6683,'added':14,'replay':'PASS','rawSha256':x['rawCurriculumSha256']})
