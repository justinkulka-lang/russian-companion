from pathlib import Path
import json,subprocess,hashlib,zipfile,shutil,sys
R=Path('/workspace/russian-companion');W=Path(sys.argv[1]);D=R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit7-source-reconciliation-2026-10-24'/sys.argv[2];D.mkdir()
sha=lambda p:hashlib.sha256(p.read_bytes()).hexdigest();save=lambda n,v:(D/n).write_text(json.dumps(v,indent=2)+'\n')
c=json.loads((W/'PIPELINE_COMPLETION.json').read_text());r=json.loads((W/'REGRESSION/RESULT.json').read_text());ctx=json.loads((W/'REGRESSION/CONTEXT.json').read_text());assert c['sourceCommit']==r['sourceCommit']==ctx['sourceCommit'];assert r['testCounts']['reported']==(124 if sys.argv[2]=='ATTEMPT_1' else 14) and r['classes']==(26 if sys.argv[2]=='ATTEMPT_1' else 4)
assert subprocess.check_output(['git','rev-parse','HEAD'],cwd=R,text=True).strip()==r['sourceCommit'];assert not subprocess.check_output(['git','status','--porcelain'],cwd=R,text=True).strip()
files={}
for mode in ['DISCOVERY','REGRESSION']:
 for p in (W/mode).rglob('*'):
  if p.is_file():files[mode+'/'+p.relative_to(W/mode).as_posix()]=p
 for name in ['CONTEXT.json','RESULT.json','RESULTS.csv','SHA256.json']:shutil.copy2(W/mode/name,D/(mode+'_'+name))
 for suffix in ['COMPLETION.json','LAUNCHER.json','RUNNER_PROCESS.json','LAUNCHER.exit','RUNNER.log','LAUNCHER.log']:files[mode+'_'+suffix]=W/(mode+'_'+suffix)
for p in (W/'INIT').iterdir():files['INIT/'+p.name]=p
for name in ['run.py','resources.gradle','supervise.py','pipeline.py','PIPELINE_CONTEXT.json','PIPELINE_PROCESS.json','PIPELINE_COMPLETION.json','PIPELINE.log','TOOLCHAIN.txt']:files[name]=W/name
ix=[]
with zipfile.ZipFile(D/'RAW.zip','x',compression=zipfile.ZIP_DEFLATED,compresslevel=9) as z:
 for n,p in sorted(files.items()):
  assert not n.lower().endswith('.pdf');ix.append(dict(path=n,bytes=p.stat().st_size,sha256=sha(p)));z.write(p,n)
save('RAW_INDEX.json',ix);save('FAILED_METHODS.json',[t for t in r['tests'] if t['outcome'] in ['FAILED','ERROR']]);save('SCOPE.json',dict(sourceCommit=r['sourceCommit'],testCounts=r['testCounts'],classes=r['classes'],exitCode=r['exitCode'],lint=r['lintIssuesBySeverity'],tasks=r['tasks'],completeSuite=False,independentAudit='INCOMPLETE_ALL48_OPEN',physicalAcceptance='OUTSTANDING_LAST',apkIdentity=None,apkSignature=None))
save('SHA256.json',{p.name:sha(p) for p in sorted(D.iterdir()) if p.is_file() and p.name!='SHA256.json'})
print((D/'SCOPE.json').read_text())
