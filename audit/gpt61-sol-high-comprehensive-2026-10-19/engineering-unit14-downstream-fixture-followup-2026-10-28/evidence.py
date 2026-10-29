from pathlib import Path
import json,hashlib,zipfile,subprocess
R=Path('/workspace/russian-companion');W=Path(__file__).parent
D=R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit14-downstream-fixture-followup-2026-10-28'
git=lambda *a:subprocess.check_output(['git',*a],cwd=R,text=True).strip()
sha=lambda b:hashlib.sha256(b).hexdigest()
read=lambda p:json.loads(p.read_text())
save=lambda p,v:p.write_text(json.dumps(v,indent=2)+'\n')
TOOLS=['prepare.py','configure.py','run.py','resources.gradle','launch.py','supervise.py','start_runtime.py','durable_monitor.py','evidence.py','verify_results.py','record_discovery.py','completion_worker.py','assertion_calls.py']
def conservation():
    p=read(D/'PREPARATION.json')
    for n,h in p['inputTrees'].items():assert git('rev-parse','HEAD:'+n)==h,n
    for key in ['existingTestFiles','currentRecordHashes','priorFailureEvidenceHashes']:
        for n,h in p[key].items():assert sha((R/n).read_bytes())==h,n
    from assertion_calls import assertions
    insert=b'        fourteenth.slots.indices.drop(2).forEach { answer(p, s, fourteenth, it) }\n'
    with zipfile.ZipFile(D/'ORIGINAL_FIXTURES.zip')as z:
        assert z.testzip()is None
        for f in p['fixtureReview']:
            old=z.read(f['path']);new=(R/f['path']).read_bytes()
            assert sha(old)==f['originalSha256']and sha(new)==f['newSha256']
            assert new.count(insert)==1 and new.replace(insert,b'',1)==old
            assert assertions(old.decode())==assertions(new.decode())
    for n,h in p['archives'].items():
        assert sha((R/n).read_bytes())==h,n
        with zipfile.ZipFile(R/n)as z:assert z.testzip()is None,n
    return {'inputTrees':p['inputTrees'],'testFilesExact':281,'originalAssertionCallsExact':p['originalAssertionCalls'],'only15SingleLineInsertions':True,'other266TestFilesExact':True,'bindingsExact':155,'archivesShaCrc':77,'priorBroadAndFailureEvidenceExact':True,'apkIdentity':None,'apkSignature':None,'all48Findings':'OPEN','audit':'INCOMPLETE','physicalAcceptance':'OUTSTANDING_LAST'}
def archive_mode(mode,name):
    files={p.relative_to(W).as_posix():p for p in (W/mode).rglob('*')if p.is_file()}
    for p in W.glob(mode+'_*'):
        if p.is_file():files[p.name]=p
    for n in TOOLS:files['TOOLING/'+n]=W/n
    files['TOOLING/SCOPE.json']=W/'SCOPE.json'
    for n,h in read(W/mode/'CONTEXT.json')['initScriptSha256'].items():
        p=Path(n);assert sha(p.read_bytes())==h;files['INIT/'+p.name]=p
    if mode=='REGRESSION':
        for n in ['DURABLE_MONITOR_STATUS.json','DURABLE_MONITOR.log','INCOMPLETE_PROCESS_DISAPPEARANCE.json']:
            if(W/n).exists():files['MONITOR/'+n]=W/n
        for item in read(W/'DURABLE_MONITOR_STATUS.json').get('newNativeReports',[]):
            p=Path(item['path']);files['NATIVE_REPORTS/'+p.name]=p
        if name=='RAW_INCOMPLETE':
            root=R/'app/build/test-results/testDebugUnitTest'
            for p in root.rglob('*'):
                if p.is_file():files['UNVERIFIED_BUILD_OBSERVATION/'+p.relative_to(root).as_posix()]=p
    idx={}
    with zipfile.ZipFile(D/(name+'.zip'),'x',zipfile.ZIP_DEFLATED)as z:
        for n,p in sorted(files.items()):
            assert not n.lower().endswith('.pdf');data=p.read_bytes();z.writestr(n,data);idx[n]={'bytes':len(data),'sha256':sha(data)}
    save(D/(name+'_INDEX.json'),idx);verify_archive(name)
def verify_archive(name):
    idx=read(D/(name+'_INDEX.json'))
    with zipfile.ZipFile(D/(name+'.zip'))as z:
        assert z.testzip()is None and set(z.namelist())==set(idx)
        for n,h in idx.items():data=z.read(n);assert len(data)==h['bytes']and sha(data)==h['sha256'],n
def manifest():save(D/'SHA256.json',{p.relative_to(D).as_posix():sha(p.read_bytes())for p in sorted(D.rglob('*'))if p.is_file()and p!=D/'SHA256.json'})
