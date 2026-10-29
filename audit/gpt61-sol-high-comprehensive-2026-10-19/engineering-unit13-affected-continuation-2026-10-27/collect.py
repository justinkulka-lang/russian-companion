"""Collect actual affected run exactly once; never modify app/test inputs."""
from pathlib import Path
import hashlib,json,subprocess,zipfile,shutil,xml.etree.ElementTree as ET
R=Path('/workspace/russian-companion');W=Path(__file__).parent;D=R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit13-affected-continuation-2026-10-27';git=lambda *a:subprocess.check_output(['git',*a],cwd=R,text=True).strip();sha=lambda b:hashlib.sha256(b).hexdigest();save=lambda n,v:(D/n).write_text(json.dumps(v,indent=2)+'\n')
c=json.loads((W/'REGRESSION/CONTEXT.json').read_text());r=json.loads((W/'REGRESSION/RESULT.json').read_text());S=c['sourceCommit'];assert not git('status','--porcelain') and git('rev-parse','HEAD')==S==r['sourceCommit'];prep=json.loads((D/'CONSERVATION_INPUTS.json').read_text())
files={}
for mode in ['DISCOVERY','REGRESSION']:
 done=json.loads((W/(mode+'_COMPLETION.json')).read_text());result=json.loads((W/mode/'RESULT.json').read_text());assert done['sourceCommit']==result['sourceCommit'] and done['runnerExit']==result['exitCode']==int(done['gradleExit'])
 for n,h in json.loads((W/mode/'SHA256.json').read_text()).items():assert sha((W/mode/n).read_bytes())==h,(mode,n)
 for p in (W/mode).rglob('*'):
  if p.is_file():files[p.relative_to(W).as_posix()]=p
 for n in ['CONTEXT.json','RESULT.json','RESULTS.csv','RUN.exit','SHA256.json']:shutil.copy2(W/mode/n,D/(mode+'_'+n))
 for suffix in ['COMPLETION.json','LAUNCHER.json','RUNNER_PROCESS.json','LAUNCHER.exit','RUNNER.log','LAUNCHER.log']:files[mode+'_'+suffix]=W/(mode+'_'+suffix)
for n in ['run.py','resources.gradle','launch.py','supervise.py','durable_monitor.py','completion_worker.py','start_runtime.py','collect.py','verify_results.py','SCOPE.json']:files['TOOLING/'+n]=W/n
for n in ['DURABLE_MONITOR.log','DURABLE_MONITOR_STATUS.json']:files['MONITOR/'+n]=W/n
for n,h in c['initScriptSha256'].items():
 p=Path(n);assert sha(p.read_bytes())==h;files['INIT/'+p.name]=p
index={}
with zipfile.ZipFile(D/'RAW_AFFECTED.zip','x',zipfile.ZIP_DEFLATED) as z:
 for n,p in sorted(files.items()):assert not n.lower().endswith('.pdf');z.write(p,n);index[n]=dict(bytes=p.stat().st_size,sha256=sha(p.read_bytes()))
save('RAW_AFFECTED_INDEX.json',index)
assert r['testCounts']['reported']==165 and r['classes']==28 and r['discoveryComparison']['sameInputTrees'] and r['discoveryComparison']['exactSameMethodSet']
assert git('rev-parse','HEAD:app/src/main')==prep['productionTree']
for n,h in prep['existingTestFiles'].items():
 if n not in prep['changedComparisonFiles']:assert sha((R/n).read_bytes())==h,n
for row in prep['comparisonReview']:assert sha((R/row['path']).read_bytes())==row['newSha256']
for n,h in prep['currentRecordHashes'].items():assert sha((R/n).read_bytes())==h,n
for n,h in prep['archives'].items():
 assert sha((R/n).read_bytes())==h,n
 with zipfile.ZipFile(R/n) as z:assert z.testzip() is None,n
failures=[]
for p in (W/'REGRESSION/XML').glob('TEST-*.xml'):
 for t in ET.parse(p).getroot().findall('testcase'):
  for kind in ['failure','error']:
   f=t.find(kind)
   if f is not None:failures.append(dict(testClass=t.get('classname'),method=t.get('name'),kind=kind,message=f.get('message',''),trace=f.text or ''))
save('ACTUAL_FAILURES.json',dict(sourceCommit=S,count=len(failures),failures=failures,originalAssertionsNotWaived=True))
save('FINAL_CONSERVATION.json',dict(sourceCommit=S,inputTrees=c['inputTrees'],productionUnchanged=True,existingTestsByteExactExceptTwoArchivedComparisons=True,otherTestFilesUnchanged=274,all107OriginalComparisonAssertionsPreserved=True,wholeBaselineAndCoreTestsUnchanged=True,currentRecordsUnchanged=155,archivesShaCrc=74,repositorySourceDigest=prep['repositorySourceDigest'],packContentReviewDigest=prep['packContentReviewDigest'],all48Findings='OPEN',audit='INCOMPLETE',physicalAcceptance='OUTSTANDING_LAST',apkIdentity=None,apkSignature=None,environmentSettingsChanged=False))
print(json.dumps(dict(source=S,counts=r['testCounts'],classes=r['classes'],exitCode=r['exitCode'],actualFailures=len(failures))))
