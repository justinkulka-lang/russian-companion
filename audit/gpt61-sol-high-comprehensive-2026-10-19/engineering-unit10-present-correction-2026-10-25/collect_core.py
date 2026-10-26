"""Collect completed actual core verification without overwriting any earlier attempt."""
from pathlib import Path
import json,subprocess,hashlib,zipfile,shutil
R=Path('/workspace/russian-companion');W=Path(__file__).parent;O=W/'VERIFIED';D=R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit10-present-correction-2026-10-25';sha=lambda p:hashlib.sha256(p.read_bytes()).hexdigest();git=lambda *a:subprocess.check_output(['git',*a],cwd=R,text=True).strip();r=json.loads((O/'REGRESSION/RESULT.json').read_text());c=json.loads((O/'REGRESSION/CONTEXT.json').read_text());done=json.loads((O/'REGRESSION_COMPLETION.json').read_text());S=c['sourceCommit'];assert git('rev-parse','HEAD')==S==r['sourceCommit'] and not git('status','--porcelain');assert done['runnerExit']==r['exitCode']==int(done['gradleExit']) and done['manifestExists'] and done['resultExists'];assert r['testCounts']['reported']==13 and r['classes']==3;assert not (D/'RAW_CORE.zip').exists();prep=json.loads((W/'PREPARATION.json').read_text())
for n,h in prep['preexistingTestFiles'].items():assert sha(R/n)==h
for n,h in prep['currentRecordHashes'].items():assert sha(R/n)==h
for n,h in prep['archives'].items():
 assert sha(R/n)==h
 with zipfile.ZipFile(R/n) as z:assert z.testzip() is None
files={}
for mode in ['DISCOVERY','REGRESSION']:
 for p in (O/mode).rglob('*'):
  if p.is_file():files[p.relative_to(O).as_posix()]=p
 for n in ['CONTEXT.json','RESULT.json','RESULTS.csv','RUN.exit','SHA256.json']:shutil.copy2(O/mode/n,D/(mode+'_'+n))
 for suffix in ['COMPLETION.json','LAUNCHER.json','RUNNER_PROCESS.json','LAUNCHER.exit','RUNNER.log','LAUNCHER.log']:files[mode+'_'+suffix]=O/(mode+'_'+suffix)
for n in ['run.py','resources.gradle','supervise.py','durable_monitor.py','DURABLE_MONITOR.log','DURABLE_MONITOR_STATUS.json']:files[n]=O/n
for n in c['initScriptSha256']:files['INIT/'+Path(n).name]=Path(n)
index={}
with zipfile.ZipFile(D/'RAW_CORE.zip','x',zipfile.ZIP_DEFLATED) as z:
 for n,p in sorted(files.items()):assert not n.lower().endswith('.pdf');z.write(p,n);index[n]=dict(bytes=p.stat().st_size,sha256=sha(p))
(D/'RAW_CORE_INDEX.json').write_text(json.dumps(index,indent=2)+'\n')
for n in ['collect_core.py','verify_saved_results.py']:shutil.copy2(W/n,D/n)
with zipfile.ZipFile(D/'VERIFIED_TEST_SOURCES.zip','x',zipfile.ZIP_DEFLATED) as z:
 for n in ['Unit10PresentCompatibilityTest','Unit10PresentReviewUiTest','Unit10PresentFixtures']:
  p=R/'app/src/test/java/dev/russiancompanion/v2'/(n+'.kt');z.write(p,p.relative_to(R).as_posix())
(D/'CONSERVATION.json').write_text(json.dumps(dict(sourceCommit=S,preexistingTestFilesUnchanged=len(prep['preexistingTestFiles']),oldPolicyObjectsConserved=36,baselineWholeFileUnchanged=True,historicalArchivesShaCrcConserved=71,currentRecords=155,currentRecordStatus='STALE_UNCHANGED_NOT_REGENERATED',officialDigest='PENDING',all48Findings='OPEN',audit='INCOMPLETE',physicalAcceptance='OUTSTANDING_LAST',apkIdentity=None,apkSignature=None,settingsChanged=False),indent=2)+'\n')
print(json.dumps(dict(collected=True,source=S,counts=r['testCounts'],classes=r['classes'],exitCode=r['exitCode'])))
