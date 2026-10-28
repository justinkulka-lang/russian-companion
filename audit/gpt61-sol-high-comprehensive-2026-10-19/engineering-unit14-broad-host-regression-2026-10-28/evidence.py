from pathlib import Path
import json,hashlib,zipfile,subprocess
R=Path('/workspace/russian-companion');W=Path(__file__).parent
D=R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit14-broad-host-regression-2026-10-28'
git=lambda *a:subprocess.check_output(['git',*a],cwd=R,text=True).strip()
sha=lambda b:hashlib.sha256(b).hexdigest()
save=lambda p,v:p.write_text(json.dumps(v,indent=2)+'\n')
read=lambda p:json.loads(p.read_text())
TOOLS=['prepare.py','run.py','resources.gradle','launch.py','supervise.py','start_runtime.py','durable_monitor.py','evidence.py','record_discovery.py','collect.py','verify_results.py','completion_worker.py','configure.py']
def archive_mode(mode,name,monitor=False):
    files={p.relative_to(W).as_posix():p for p in (W/mode).rglob('*') if p.is_file()}
    for suffix in ['COMPLETION.json','LAUNCHER.json','RUNNER_PROCESS.json','LAUNCHER.exit','RUNNER.log','LAUNCHER.log','SUPERVISOR_ERROR.txt']:
        p=W/(mode+'_'+suffix)
        if p.exists():files[p.name]=p
    for n in TOOLS:files['TOOLING/'+n]=W/n
    for n in ['SCOPE.json']:files['TOOLING/'+n]=W/n
    c=read(W/mode/'CONTEXT.json')
    for n,h in c['initScriptSha256'].items():
        p=Path(n);assert sha(p.read_bytes())==h;files['INIT/'+p.name]=p
    if monitor:
        for n in ['DURABLE_MONITOR.log','DURABLE_MONITOR_STATUS.json','INCOMPLETE_PROCESS_DISAPPEARANCE.json']:
            p=W/n
            if p.exists():files['MONITOR/'+n]=p
    if mode=='REGRESSION':
        if name=='RAW_INCOMPLETE':
            root=R/'app/build/test-results/testDebugUnitTest'
            for p in root.rglob('*'):
                if p.is_file():files['UNVERIFIED_BUILD_OBSERVATION/'+p.relative_to(root).as_posix()]=p
        status=W/'DURABLE_MONITOR_STATUS.json'
        if status.exists():
            for info in read(status).get('newNativeReports',[]):
                p=Path(info['path'])
                if p.is_file():files['NATIVE_REPORTS/'+p.relative_to(R).as_posix()]=p
    idx={}
    with zipfile.ZipFile(D/(name+'.zip'),'x',zipfile.ZIP_DEFLATED) as z:
        for n,p in sorted(files.items()):
            assert not n.lower().endswith('.pdf');data=p.read_bytes();z.writestr(n,data);idx[n]=dict(bytes=len(data),sha256=sha(data))
    save(D/(name+'_INDEX.json'),idx)
    verify_archive(name)
    return idx
def verify_archive(name):
    idx=read(D/(name+'_INDEX.json'))
    with zipfile.ZipFile(D/(name+'.zip')) as z:
        assert z.testzip() is None and set(z.namelist())==set(idx)
        for n,h in idx.items():assert len(z.read(n))==h['bytes'] and sha(z.read(n))==h['sha256'],n
def conservation():
    prep=read(D/'PREPARATION.json')
    assert git('rev-parse','HEAD:app/src/main')==prep['productionTree']
    assert all(git('rev-parse','HEAD:'+p)==h for p,h in prep['inputTrees'].items())
    for n,h in prep['existingTestFiles'].items():assert sha((R/n).read_bytes())==h,n
    for n,h in prep['currentRecordHashes'].items():assert sha((R/n).read_bytes())==h,n
    for n,h in prep['priorFailureEvidenceHashes'].items():assert sha((R/n).read_bytes())==h,n
    prior=json.loads(subprocess.check_output(['python3','-B',str(R/prep['followupEvidenceDirectory']/'verify_results.py')],cwd=R,text=True))
    assert prior['verified'] and prior['counts']==dict(reported=79,passed=79,failed=0,errors=0,skipped=0)
    for n,h in prep['archives'].items():
        assert sha((R/n).read_bytes())==h,n
        with zipfile.ZipFile(R/n) as z:assert z.testzip() is None,n
    return dict(inputTrees=prep['inputTrees'],testFilesByteExact=281,bindingsByteExact=155,archivesShaCrc=77,allPriorAssertionsAndCurrentControlsByteExact=True,repositorySourceDigest=prep['repositorySourceDigest'],packContentReviewDigest=prep['packContentReviewDigest'],apkIdentity=None,apkSignature=None,environmentSettingsChanged=False,all48Findings='OPEN',audit='INCOMPLETE',physicalAcceptance='OUTSTANDING_LAST')
def manifest():save(D/'SHA256.json',{p.relative_to(D).as_posix():sha(p.read_bytes()) for p in sorted(D.rglob('*')) if p.is_file() and p.name!='SHA256.json'})
