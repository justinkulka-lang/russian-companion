"""Preserve actual full-class discovery/runtime exactly; no source changes or assertion waivers."""
from pathlib import Path
import json,hashlib,subprocess,zipfile,shutil,xml.etree.ElementTree as ET
R=Path('/workspace/russian-companion');W=Path(__file__).parent
D=R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit13-historical-comparison-followup-2026-10-27'
git=lambda *a:subprocess.check_output(['git',*a],cwd=R,text=True).strip()
sha=lambda b:hashlib.sha256(b).hexdigest();save=lambda n,v:(D/n).write_text(json.dumps(v,indent=2)+'\n')
c=json.loads((W/'REGRESSION/CONTEXT.json').read_text());r=json.loads((W/'REGRESSION/RESULT.json').read_text());prep=json.loads((D/'PREPARATION.json').read_text());S=r['sourceCommit']
assert git('rev-parse','HEAD')==S==c['sourceCommit'] and not git('status','--porcelain')
files={}
for mode in ['DISCOVERY','REGRESSION']:
    end=json.loads((W/(mode+'_COMPLETION.json')).read_text());result=json.loads((W/mode/'RESULT.json').read_text())
    assert end['sourceCommit']==result['sourceCommit'] and end['runnerExit']==result['exitCode']==int(end['gradleExit'])
    for n,h in json.loads((W/mode/'SHA256.json').read_text()).items():assert sha((W/mode/n).read_bytes())==h,(mode,n)
    for p in (W/mode).rglob('*'):
        if p.is_file():files[p.relative_to(W).as_posix()]=p
    for n in ['CONTEXT.json','RESULT.json','RESULTS.csv','RUN.exit','SHA256.json']:shutil.copy2(W/mode/n,D/(mode+'_'+n))
    for suffix in ['COMPLETION.json','LAUNCHER.json','RUNNER_PROCESS.json','LAUNCHER.exit','RUNNER.log','LAUNCHER.log']:files[mode+'_'+suffix]=W/(mode+'_'+suffix)
for n in ['prepare.py','assertion_calls.py','run.py','resources.gradle','supervise.py','launch.py','start_runtime.py','durable_monitor.py','collect.py','verify_results.py','record_discovery.py','completion_worker.py','SCOPE.json']:files['TOOLING/'+n]=W/n
for n in ['DURABLE_MONITOR.log','DURABLE_MONITOR_STATUS.json']:files['MONITOR/'+n]=W/n
for n,h in c['initScriptSha256'].items():
    p=Path(n);assert sha(p.read_bytes())==h;files['INIT/'+p.name]=p
index={}
with zipfile.ZipFile(D/'RAW_FOLLOWUP.zip','x',zipfile.ZIP_DEFLATED) as z:
    for n,p in sorted(files.items()):
        assert not n.lower().endswith('.pdf');data=p.read_bytes();z.writestr(n,data);index[n]=dict(bytes=len(data),sha256=sha(data))
save('RAW_FOLLOWUP_INDEX.json',index)
assert r['testCounts']['reported']==63 and r['classes']==9 and r['discoveryComparison']['sameInputTrees'] and r['discoveryComparison']['exactSameMethodSet']
assert c['inputTrees']==prep['preparedInputTrees']
for n,h in prep['existingTestFiles'].items():
    if n not in prep['changedComparisonFiles']:assert sha((R/n).read_bytes())==h,n
for row in prep['comparisonReview']:assert sha((R/row['path']).read_bytes())==row['newSha256']
for n,h in prep['currentRecordHashes'].items():assert sha((R/n).read_bytes())==h
for n,h in prep['archives'].items():
    assert sha((R/n).read_bytes())==h
    with zipfile.ZipFile(R/n) as z:assert z.testzip() is None
failures=[]
for p in (W/'REGRESSION/XML').glob('TEST-*.xml'):
    for t in ET.parse(p).getroot().findall('testcase'):
        for kind in ['failure','error']:
            f=t.find(kind)
            if f is not None:failures.append(dict(testClass=t.get('classname'),method=t.get('name'),kind=kind,message=f.get('message',''),trace=f.text or ''))
save('ACTUAL_FAILURES.json',dict(sourceCommit=S,count=len(failures),failures=failures,originalAssertionsNotWaived=True))
scope=json.loads((W/'SCOPE.json').read_text());by={(t['testClass'],t['method']):t['outcome'] for t in r['tests']}
rechecked=[dict(**x,outcome=by[x['testClass'],x['method']]) for x in scope['priorFailedIdentities']]
save('ORIGINAL_FAILURE_RECHECK.json',dict(sourceCommit=S,rechecked=rechecked,allSixIdentitiesRechecked=len(rechecked)==6,allSixPassed=all(t['outcome']=='PASSED' for t in rechecked),originalInterruptedFailureEvidenceUnchanged=True))
save('FINAL_CONSERVATION.json',dict(sourceCommit=S,inputTrees=c['inputTrees'],productionUnchanged=True,otherTestFilesUnchanged=271,wholeBaselineCurrentCoreAndOriginalUnit12CorrectionsUnchanged=True,all627OriginalAssertionInvocationsRetained=True,newAssertionInvocations=635,currentRecordsConserved=155,archivesShaCrc=74,repositorySourceDigest=prep['repositorySourceDigest'],packContentReviewDigest=prep['packContentReviewDigest'],apkIdentity=None,apkSignature=None,environmentSettingsChanged=False,all48Findings='OPEN',audit='INCOMPLETE',physicalAcceptance='OUTSTANDING_LAST',completeSuite=False))
print(json.dumps(dict(source=S,counts=r['testCounts'],classes=9,exitCode=r['exitCode'],failures=len(failures),rechecked=rechecked)))
