"""Collect only a completed exact Unit8 baseline; preserves actual failure and genuine capture."""
from pathlib import Path
from datetime import datetime,timezone
import subprocess,json,hashlib,zipfile,shutil
R=Path('/workspace/russian-companion');W=Path(__file__).parent;B=R/'audit/gpt61-sol-high-comprehensive-2026-10-19';D=B/'engineering-unit8-present-baseline-2026-10-24';S='6fd0406da6dd687196eea76ff56ad7cadc06087d';sha=lambda p:hashlib.sha256(p.read_bytes()).hexdigest();git=lambda *a:subprocess.check_output(['git',*a],cwd=R,text=True).strip();save=lambda n,v:(D/n).write_text(json.dumps(v,indent=2)+'\n')
assert git('rev-parse','HEAD')==S and not git('status','--porcelain')
completion=json.loads((W/'REGRESSION_COMPLETION.json').read_text());r=json.loads((W/'REGRESSION/RESULT.json').read_text());ctx=json.loads((W/'REGRESSION/CONTEXT.json').read_text());assert completion['resultExists'] and completion['manifestExists'];assert completion['runnerExit']==r['exitCode']==1
assert r['sourceCommit']==ctx['sourceCommit']==completion['sourceCommit']==S and r['testCounts']==dict(reported=2,passed=1,failed=1,errors=0,skipped=0)
assert not (D/'RAW_BASELINE.zip').exists()
prep=json.loads((W/'PREPARATION.json').read_text())
for n,h in prep['preexistingTestFiles'].items():assert sha(R/n)==h,n
for n,h in prep['unchangedTrees'].items():assert git('rev-parse','HEAD:'+n)==h
shutil.copy2(W/'PREPARATION.json',D/'PREPARATION.json');files={}
for mode in ['DISCOVERY','REGRESSION']:
 for p in (W/mode).rglob('*'):
  if p.is_file() and p.relative_to(W/mode).as_posix() not in ['CAPTURE/PARTIAL_UNIT8_V1.zip','CAPTURE/IDENTITY.json']:files[p.relative_to(W).as_posix()]=p
 for n in ['CONTEXT.json','RESULT.json','SHA256.json','RESULTS.csv','RUN.exit']:shutil.copy2(W/mode/n,D/(mode+'_'+n))
 for suffix in ['COMPLETION.json','LAUNCHER.json','RUNNER_PROCESS.json','LAUNCHER.exit','RUNNER.log','LAUNCHER.log']:files[mode+'_'+suffix]=W/(mode+'_'+suffix)
for n in ['run.py','resources.gradle','supervise.py','PREPARATION.json']:files[n]=W/n
for n in ctx['initScriptSha256']:files['INIT/'+Path(n).name]=Path(n)
index=[]
with zipfile.ZipFile(D/'RAW_BASELINE.zip','x',zipfile.ZIP_DEFLATED,compresslevel=9) as z:
 for n,p in sorted(files.items()):
  assert p.is_file() and not n.lower().endswith('.pdf');index.append(dict(path=n,bytes=p.stat().st_size,sha256=sha(p)));z.write(p,n)
save('RAW_BASELINE_INDEX.json',index)
shutil.copy2(W/'REGRESSION/CAPTURE/PARTIAL_UNIT8_V1.zip',D/'BASELINE_CURRENT_UNIT8_V1.zip');shutil.copy2(W/'REGRESSION/CAPTURE/IDENTITY.json',D/'CAPTURE_IDENTITY.json')
for n in ['verify_saved_results.py','persist_baseline.py']:shutil.copy2(W/n,D/n)
with zipfile.ZipFile(D/'ORIGINAL_BASELINE_TEST.zip','x',zipfile.ZIP_DEFLATED) as z:
 n='app/src/test/java/dev/russiancompanion/v2/Unit8PresentReviewBaselineTest.kt';z.write(R/n,n)
archives=json.loads((B/'engineering-unit56-conservation-2026-10-23/ORIGINAL_ARCHIVE_CONSERVATION.json').read_text());rows=[]
for n,h in archives.items():
 assert sha(R/n)==h
 with zipfile.ZipFile(R/n) as z:assert z.testzip() is None
 rows.append(dict(path=n,sha256=h))
records=json.loads((B/'engineering-unit7-reconciled-broad-2026-10-24/CURRENT_RECORD_INPUTS.json').read_text())['files'];assert len(records)==155
for n,h in records.items():assert sha(R/n)==h
save('CURRENT_RECORD_HASHES.json',records)
extras={str(p.relative_to(R)):sha(p) for p in [B/'engineering-unit6-present-baseline-2026-10-23/BASELINE_CURRENT_UNIT6_V2.zip',B/'engineering-unit7-gender-baseline-2026-10-24/BASELINE_CURRENT_UNIT7_V1.zip']}
identity=json.loads((D/'CAPTURE_IDENTITY.json').read_text())
save('CONSERVATION.json',dict(inputTrees=ctx['inputTrees'],productionUnchangedFrom=prep['baselineParent'],originalArchives=rows,rawCurriculumSHA256=identity['rawCurriculumSHA256'],preexistingTestFilesVerified=len(prep['preexistingTestFiles']),currentGeneratedRecordsUnchanged=155,currentGeneratedRecordBasis='Exact155live hashes and sourceGitobjects verified; no generators run',canonicalSourceDigest='d48aa7e0090bd30422172f7b7c87fe52fe2859fa3c5be9b0851d2f9704f30e3a',sourceDigestBasis='Same production/source trees as preceding official Kotlin computation; no approximate/new computation',additionalUnit6Unit7Captures=extras,newUnit8V1CaptureSha256=identity['zipSHA256'],nativeRootCause='UNCONFIRMED',directive='EXPERIMENTAL_NOT_ADOPTED',persistentSetupChanges=[],publication='Earlier user-confirmed; independently unobserved; no changes',apkIdentity=None,apkSignature=None,independentAudit='INCOMPLETE_ALL48_OPEN',physicalAcceptance='OUTSTANDING_LAST'))
save('BASELINE_OBSERVATION.json',dict(sourceCommit=S,testCounts=r['testCounts'],actualAdvanceWithoutAssignedPresentAnswer=True,observationBasis='Exact actualadvanceassertion and rawXML system-out; verified separately',ordinaryPresentKnowledgeBypassed=False,proposedPolicyActivated=False,currentPolicy=identity['policyId'],currentSchema=identity['schema'],lint=r['lintIssuesBySeverity'],tasks=r['tasks'],apkIdentity=None,apkSignature=None,completeSuite=False))
print(json.dumps({'collected':True,'source':S,'counts':r['testCounts'],'capture':identity}))
