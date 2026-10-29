from pathlib import Path
import json,hashlib,zipfile,subprocess,shutil,xml.etree.ElementTree as ET
R=Path('/workspace/russian-companion');W=Path('/workspace/artifacts/unit-identity-cache-baseline-2026-10-28');D=R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit-identity-cache-baseline-2026-10-28'
read=lambda p:json.loads(p.read_text());sha=lambda b:hashlib.sha256(b).hexdigest();save=lambda p,v:p.write_text(json.dumps(v,indent=2)+'\n')
assert(W/'REGRESSION_COMPLETION.json').exists(),'Baseline supervisor still active; do not collect'
assert not subprocess.check_output(['git','status','--porcelain=v1','-z','--untracked-files=all'],cwd=R)
prep=read(D/'PREPARATION.json');scope=read(W/'SCOPE.json');expected={(t['testClass'],t['method'])for t in scope['expectedMethods']}
for path,h in prep['preExistingTestHashes'].items():assert sha((R/path).read_bytes())==h,path
assert sha((R/prep['newBaselineTestPath']).read_bytes())==prep['newBaselineTestSha256']
for mode,label in [('DISCOVERY','RAW_DISCOVERY'),('REGRESSION','RAW_BASELINE')]:
    c=read(W/mode/'CONTEXT.json');r=read(W/mode/'RESULT.json');end=read(W/(mode+'_COMPLETION.json'))
    assert r['inputTrees']==c['inputTrees']==prep['inputTrees']
    assert r['sourceCommit']==c['sourceCommit']and r['exitCode']==end['runnerExit']==int(end['gradleExit'])
    assert c['selectors']==scope['selectors']and len(c['selectors'])==1
    assert {(t['testClass'],t['method'])for t in r['tests']}==expected
    files={p.relative_to(W).as_posix():p for p in(W/mode).rglob('*')if p.is_file()}
    for p in W.glob(mode+'_*'):
        if p.is_file():files[p.name]=p
    for n in ['run.py','launch.py','supervise.py','resources.gradle','prepare.py']:files['TOOLING/'+n]=W/n
    files['TOOLING/SCOPE.json']=W/'SCOPE.json';files['TOOLING/collect_baseline.py']=Path(__file__)
    for n,h in c['initScriptSha256'].items():
        p=Path(n);assert sha(p.read_bytes())==h;files['INIT/'+p.name]=p
    index={}
    with zipfile.ZipFile(D/(label+'.zip'),'x',zipfile.ZIP_DEFLATED)as z:
        for n,p in sorted(files.items()):
            assert not n.lower().endswith('.pdf');data=p.read_bytes();z.writestr(n,data);index[n]={'bytes':len(data),'sha256':sha(data)}
    save(D/(label+'_INDEX.json'),index)
    with zipfile.ZipFile(D/(label+'.zip'))as z:
        assert z.testzip()is None and set(z.namelist())==set(index)
        for n,h in index.items():data=z.read(n);assert len(data)==h['bytes']and sha(data)==h['sha256']
    for n in ['CONTEXT.json','RESULT.json','RESULTS.csv','RUN.exit','SHA256.json']:shutil.copyfile(W/mode/n,D/(mode+'_'+n))
r=read(W/'REGRESSION/RESULT.json');failures=[]
for p in(W/'REGRESSION/XML').glob('*.xml'):
 for t in ET.parse(p).getroot().findall('testcase'):
  for kind in ['failure','error']:
   f=t.find(kind)
   if f is not None:failures.append({'testClass':t.get('classname'),'method':t.get('name'),'kind':kind,'message':f.get('message',''),'trace':f.text or ''})
save(D/'ACTUAL_FAILURES_FULL.json',{'sourceCommit':r['sourceCommit'],'failures':failures,'count':len(failures),'originalAssertionsNotWaived':True})
expected_failures={'repeatedColdIdentityChecksComputeTheSameUnitOnlyOnce','aLivePackEvictedByOtherPackInstancesIsRepopulatedOnce'}
assert {f['method']for f in failures}==expected_failures and all('expected:<1> but was:<12>'in f['message']for f in failures)
assert r['testCounts']==dict(reported=3,passed=1,failed=2,errors=0,skipped=0)and r['exitCode']==1
assert next(t for t in r['tests']if t['method']=='cachedAndMutatedPackIdentitiesRemainDistinctAndRejectEachOthersCredit')['outcome']=='PASSED'
save(D/'VERIFIED_BASELINE.json',{'sourceCommit':r['sourceCommit'],'inputTrees':r['inputTrees'],'counts':r['testCounts'],'classes':1,'exitCode':1,'coldObservedCanonicalExecutions':12,'evictedObservedCanonicalExecutions':12,'requiredCanonicalExecutions':1,'changedPackIdentityControl':'PASSED','old281TestsByteExact':True,'newBaselineTestByteExact':True,'productionUnchanged':True,'completeSuite':False})
shutil.copyfile(__file__,D/'collect_baseline.py')
for n in ['PR64_BODY_BEFORE_CODEX_ONLY.md','PR64_BODY_CODEX_ONLY.md']:
 if(W/n).exists():shutil.copyfile(W/n,D/n)
(D/'COMPLETED_BASELINE.md').write_text(f'''# Actual cold/evicted cache baseline

Actual source `{r['sourceCommit']}`: **3reported/1passed/2failed/0errors/0skipped;1FULLclass;exit1**. Both real count failures report12canonical executions where one is required for repeated reads of one immutable pack/unit. The distinct changed-pack rejection control passed. This reproduces the source/cache-miss issue without a machine-duration threshold. All281previous tests and the entire new baseline are unchanged;production unchanged. Fresh discovery3syntheticskips/zero runtimepasses is separate, at the same exact source in this small baseline.

Raw discovery/runtime XML/events/commands/source/trees/runner/initSHA/logs/GC/lint/gate/exits plus full failure messages/traces and memberSHA/CRC are preserved. Actual lint: `{json.dumps(r['lintIssuesBySeverity'])}`. Actual task/cache states: `{json.dumps(r['tasks'])}`. Gate attachment is not release approval. No APK assembly/install/signing;identity/signaturenull. Not a complete suite or qualified content/device acceptance.

Next:bounded weak-identity read-through cache using the same canonical calculation; preserve the whole baseline, all old assertions/pins/fixtures and changed-pack refusals. Mark source/review bindings stale after production edit and reconcile authentically before broad verification. Prior1499/79/interrupted119scopes remain separate/unsummed. Codex only;all48OPEN/auditINCOMPLETE/physicalLAST/no merge/freeze/releaseapproval.
''')
rel=D.relative_to(R).as_posix();pointers=['AGENTS.md','audit/content-corrections/CODEX_ENGINEERING_CONTINUATION.md','audit/content-corrections/CODEX_CHECKPOINT_2026-10-12.md']
for n in pointers:
 p=R/n;link=rel+'/COMPLETED_BASELINE.md'if n=='AGENTS.md'else'../'+rel.removeprefix('audit/')+'/COMPLETED_BASELINE.md'
 p.write_text(f'Current checkpoint: [Cold/evicted cache baseline reproduced]({link}).Actual{r["sourceCommit"]}:3reported/1strictidentitycontrolPASS/2realcanonicalcountfailures(12vs1)/exit1. Wholebaseline/all281oldertests/production unchanged. Preserve every failure; next bounded cache correction then actual affected verification. Prior119incomplete and all other scopes separate/unsummed. Codex only;no further Cursor use. All48OPEN/auditINCOMPLETE/four exporters16excluded/physicalLAST/noAPK/signing/merge/freeze/approval.\n\n'+p.read_text())
save(D/'SHA256.json',{p.relative_to(D).as_posix():sha(p.read_bytes())for p in sorted(D.rglob('*'))if p.is_file()and p!=D/'SHA256.json'})
subprocess.run(['git','add',rel,*pointers],cwd=R,check=True)
# Original PR body is retained data, and may have its original trailing blank line.
check=subprocess.run(['git','diff','--cached','--check','--',rel,*pointers,':!'+rel+'/PR64_BODY_BEFORE_CODEX_ONLY.md'],cwd=R,text=True,capture_output=True);assert check.returncode==0,check.stdout
subprocess.run(['git','commit','-m','Preserve reproduced cold and evicted unit digest failures'],cwd=R,check=True,stdout=subprocess.PIPE,stderr=subprocess.STDOUT)
print(json.dumps({'checkpoint':subprocess.check_output(['git','rev-parse','HEAD'],cwd=R).decode().strip(),'sourceCommit':r['sourceCommit'],'counts':r['testCounts'],'lint':r['lintIssuesBySeverity'],'exit':r['exitCode']}))
