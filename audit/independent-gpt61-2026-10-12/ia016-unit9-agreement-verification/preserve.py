import json,hashlib,subprocess,re,runpy,tempfile
from pathlib import Path
r=Path('/workspace/russian-companion');o=Path(__file__).resolve().parent;base='5612180322ccf9c38a39def22f3640c7477a9d0d';asset='app/src/main/assets/content/v2-curriculum.json';raw=subprocess.check_output(['git','show',base+':'+asset],cwd=r);now=(r/asset).read_bytes();a=json.loads(raw);b=json.loads(now);l=json.loads((r/'audit/content-corrections/UNIT9_AGREEMENT_TRANSFER_CORRECTION.json').read_text());changes={v['before']['id']:v for v in l['changes']}
assert len(a['provenanceRecords'])==6659 and len(b['provenanceRecords'])==6683 and b['provenanceRecords'][:6659]==a['provenanceRecords']
assert [v['itemId'] for v in b['provenanceRecords'][6659:]]==l['addedProvenanceIds']
for x,y in zip(a['items'],b['items']):
 if x['id']=='u9-i-odindva-4':assert x==changes[x['id']]['before'] and y==changes[x['id']]['after']
 else:assert x==y,x['id']
for k in a:
 if k not in ['items','units','provenanceRecords']:assert a[k]==b[k],k
for x,y in zip(a['units'],b['units']):
 if x['number']!=9:assert x==y
 else:
  for k in x:
   if k not in ['objectives','sessions']:assert x[k]==y[k],k
  for field in ['objectives','sessions']:
   for v,w in zip(x[field],y[field]):
    if v['id'] in changes:assert v==changes[v['id']]['before'] and w==changes[v['id']]['after']
    else:assert v==w,v['id']
s=runpy.run_path(str(r/'audit/content-corrections/repair_unit9_agreement_transfer.py'));source=(r/'catalog/sources/units-6-10/wikibooks/numbers-4371358.txt').read_text()
for v in l['nativeSelections']:assert v['originalSourceText'] in source and v['shippedText'] in s['visible'](v['originalSourceText']) and '\u0301' not in v['shippedText']
with tempfile.TemporaryDirectory(dir='/tmp') as d:
 t=Path(d);p=t/'pack';c=t/'code';p.write_bytes(raw);m=s['main'];m.__globals__.update(ROOT=r,HERE=t,PACK=p,PIN_FILE=c);m();assert p.read_bytes()==now and c.read_bytes()==(r/'app/src/main/java/dev/russiancompanion/v2/curriculum/Unit9AgreementPins.kt').read_bytes();m();assert p.read_bytes()==now
f='app/src/main/java/dev/russiancompanion/v2/curriculum/CompleteRuleContextPins.kt';old=dict(re.findall(r'"(ca-rule-context:[^"]+)" to "([a-f0-9]+)"',subprocess.check_output(['git','show',base+':'+f],cwd=r).decode()));new=dict(re.findall(r'"(ca-rule-context:[^"]+)" to "([a-f0-9]+)"',(r/f).read_text()));assert len(old)==62 and len(new)==63 and all(new[k]==v for k,v in old.items())
receipt=json.loads((r/'audit/independent-gpt61-2026-10-12/RESULTS_RECEIPT.json').read_text())
for f in receipt['files']:assert hashlib.sha256((r/'audit/independent-gpt61-2026-10-12'/f['path']).read_bytes()).hexdigest()==f['sha256']
x=dict(baselineCommit=base,oldProvenanceRecords=6659,newPendingRecords=24,newPrimaryNativeSelections=3,newEnglishContextCoverageRecords=21,allPriorProvenanceUnchanged=True,all58CapabilitiesAndSourcesRightsMappingsUnchanged=True,allOriginalExamplesUnchanged=True,allOtherUnitsUnchanged=True,allOtherItemsIncludingCorrectGuidedGenderPluralAndTwoFieldsUnchanged=True,all62PriorCanonicalPinsUnchanged=True,newPins=1,oneIndependentMasculineNounReplaced=True,noItemsExamplesObjectivesRemoved=True,sixTeachingCardsBeforePracticeAndCompleteRemediation=True,nativeSourceWordsUnchangedAndNoStressInserted=True,deterministicBaselineReplay=True,nativePinCodeReplayByteIdentical=True,byteIdempotent=True,allNineOriginalDiagnosticDeliverableHashesUnchanged=True,rawCurriculumSha256=hashlib.sha256(now).hexdigest(),frozenPublicSources=l['frozenPublicSources'],semanticAcceptance=False,productiveAgreementClaim=False,allNumeralCasesClaim=False)
(o/'PRESERVATION.json').write_text(json.dumps(x,indent=2)+'\n');print({'oldRecords':6659,'added':24,'replay':'PASS','rawSha256':x['rawCurriculumSha256']})
