"""Collect a completed exact Unit7baseline only; do not run inside tracked evidence."""
from pathlib import Path
from datetime import datetime,timezone
import subprocess,json,hashlib,zipfile,shutil
R=Path('/workspace/russian-companion');W=Path(__file__).parent;B=R/'audit/gpt61-sol-high-comprehensive-2026-10-19';D=B/'engineering-unit7-gender-baseline-2026-10-24';sha=lambda p:hashlib.sha256(p.read_bytes()).hexdigest()
source='38542882cd9ab2e075691eca19442ddfabd17b6b';git=lambda *a:subprocess.check_output(['git',*a],cwd=R,text=True).strip();save=lambda n,v:(D/n).write_text(json.dumps(v,indent=2)+'\n')
assert git('rev-parse','HEAD')==source and not git('status','--porcelain')
assert not (D/'RAW_BASELINE.zip').exists()
c=json.loads((W/'PIPELINE_COMPLETION.json').read_text());r=json.loads((W/'REGRESSION/RESULT.json').read_text());ctx=json.loads((W/'REGRESSION/CONTEXT.json').read_text());prep=json.loads((D/'PREPARATION.json').read_text())
assert c['sourceCommit']==r['sourceCommit']==ctx['sourceCommit']==source
assert r['testCounts']==dict(reported=2,passed=1,failed=1,errors=0,skipped=0) and r['exitCode']==1
for n,v in prep['preexistingTestFiles'].items():assert sha(R/n)==v,n
assert all(git('rev-parse','HEAD:'+n)==v for n,v in prep['unchangedTrees'].items())
files={}
for mode in ['DISCOVERY','REGRESSION']:
 for p in (W/mode).rglob('*'):
  if p.is_file() and p.relative_to(W/mode).as_posix() not in ['CAPTURE/PARTIAL_UNIT7_V1.zip','CAPTURE/IDENTITY.json']:files[mode+'/'+p.relative_to(W/mode).as_posix()]=p
 for prefix in ['CONTEXT','RESULT','SHA256']:
  shutil.copy2(W/mode/(prefix+'.json'),D/(mode+'_'+prefix+'.json'))
 shutil.copy2(W/mode/'RESULTS.csv',D/(mode+'_RESULTS.csv'))
 for suffix in ['COMPLETION.json','LAUNCHER.json','RUNNER_PROCESS.json','LAUNCHER.exit','RUNNER.log','LAUNCHER.log']:files[mode+'_'+suffix]=W/(mode+'_'+suffix)
for n in ['run.py','resources.gradle','supervise.py','pipeline.py','PIPELINE_CONTEXT.json','PIPELINE_PROCESS.json','PIPELINE_COMPLETION.json','PIPELINE.log','TOOLCHAIN.txt']:files[n]=W/n
for p in (W/'INIT').iterdir():files['INIT/'+p.name]=p
index=[]
with zipfile.ZipFile(D/'RAW_BASELINE.zip','x',compression=zipfile.ZIP_DEFLATED,compresslevel=9) as z:
 for n,p in sorted(files.items()):
  assert not n.lower().endswith('.pdf');index.append(dict(path=n,bytes=p.stat().st_size,sha256=sha(p)));z.write(p,n)
save('RAW_BASELINE_INDEX.json',index)
shutil.copy2(W/'REGRESSION/CAPTURE/PARTIAL_UNIT7_V1.zip',D/'BASELINE_CURRENT_UNIT7_V1.zip');shutil.copy2(W/'REGRESSION/CAPTURE/IDENTITY.json',D/'CAPTURE_IDENTITY.json')
for n in ['verify_saved_results.py','persist_baseline.py','pipeline.py']:shutil.copy2(W/n,D/n)
archives=json.loads((B/'engineering-unit56-conservation-2026-10-23/ORIGINAL_ARCHIVE_CONSERVATION.json').read_text());original=[]
for n,v in archives.items():
 assert sha(R/n)==v,n
 with zipfile.ZipFile(R/n) as z:assert z.testzip() is None,n
 original.append(dict(path=n,sha256=v))
extra=B/'engineering-unit6-present-baseline-2026-10-23/BASELINE_CURRENT_UNIT6_V2.zip';assert sha(extra)=='5244052c97346b4d3542edb2469eee2c714d69d2b00567dca8d668970f5ade37'
with zipfile.ZipFile(extra) as z:assert z.testzip() is None
identity=json.loads((D/'CAPTURE_IDENTITY.json').read_text())
save('CONSERVATION.json',dict(inputTrees=ctx['inputTrees'],productionUnchangedFrom=prep['baselineParent'],originalArchives=original,rawCurriculumSHA256=identity['rawCurriculumSHA256'],preexistingTestFilesVerified=len(prep['preexistingTestFiles']),currentGeneratedRecordsUnchanged=155,currentGeneratedRecordBasis='Same main/catalog/docs/review/release trees as verified preceding source; no generation or production change',canonicalSourceDigest='2507d56bf2bd910b198d9ff317a605598765e13d947fa4f4435aaacd2a626892',sourceDigestBasis='Unchanged production/source inputs; preceding official Kotlin computation, no approximate/new calculation',additionalUnit6V2CaptureSha256=sha(extra),newUnit7V1CaptureSha256=identity['zipSHA256'],nativeRootCause='UNCONFIRMED',directive='EXPERIMENTAL_NOT_ADOPTED',persistentSetupChanges=[],publication='Previously user-confirmed; independently unobserved; no new save/publication',apkIdentity=None,apkSignature=None,independentAudit='INCOMPLETE_ALL48_OPEN',physicalAcceptance='OUTSTANDING_LAST'))
save('BASELINE_OBSERVATION.json',dict(sourceCommit=source,testCounts=r['testCounts'],actualAdvanceWithoutAssignedGenderAnswer=True,observationBasis='Raw XML system-out and intended actual-advance assertion; independently checked by verifier',ordinaryGenderKnowledgeBypassed=False,proposedPolicyActivated=False,currentPolicy=identity['policyId'],currentSchema=identity['schema'],lint=r['lintIssuesBySeverity'],tasks=r['tasks'],apkIdentity=None,apkSignature=None,completeSuite=False))
print(json.dumps(dict(collected=True,source=source,counts=r['testCounts'],capture=identity,rawZipBytes=(D/'RAW_BASELINE.zip').stat().st_size)))
