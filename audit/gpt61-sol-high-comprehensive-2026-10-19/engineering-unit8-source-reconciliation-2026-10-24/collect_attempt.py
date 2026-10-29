"""Save a completed exact attempt without overwriting any original runtime evidence."""
from pathlib import Path
import sys,json,subprocess,hashlib,zipfile,shutil
W=Path(__file__).parent;R=Path('/workspace/russian-companion');D=R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit8-source-reconciliation-2026-10-24';name=sys.argv[1];O=W if len(sys.argv)==2 else W/sys.argv[2];A=D/name;assert not A.exists()
source=subprocess.check_output(['git','rev-parse','HEAD'],cwd=R,text=True).strip();assert not subprocess.check_output(['git','status','--porcelain'],cwd=R,text=True).strip()
r=json.loads((O/'REGRESSION/RESULT.json').read_text());c=json.loads((O/'REGRESSION/CONTEXT.json').read_text());done=json.loads((O/'REGRESSION_COMPLETION.json').read_text());assert r['sourceCommit']==c['sourceCommit']==source and done['resultExists'] and done['manifestExists'];assert done['runnerExit']==r['exitCode']
A.mkdir();sha=lambda p:hashlib.sha256(p.read_bytes()).hexdigest();files={}
for mode in ['DISCOVERY','REGRESSION']:
 for p in (O/mode).rglob('*'):
  if p.is_file():files[p.relative_to(O).as_posix()]=p
 for n in ['CONTEXT.json','RESULT.json','RESULTS.csv','RUN.exit','SHA256.json']:shutil.copy2(O/mode/n,A/(mode+'_'+n))
 for suffix in ['COMPLETION.json','LAUNCHER.json','RUNNER_PROCESS.json','LAUNCHER.exit','RUNNER.log','LAUNCHER.log']:files[mode+'_'+suffix]=O/(mode+'_'+suffix)
for n in ['run.py','resources.gradle','supervise.py']:files[n]=O/n
for n in c['initScriptSha256']:files['INIT/'+Path(n).name]=Path(n)
index={}
with zipfile.ZipFile(A/'RAW.zip','x',zipfile.ZIP_DEFLATED) as z:
 for n,p in sorted(files.items()):assert not n.lower().endswith('.pdf');z.write(p,n);index[n]={'bytes':p.stat().st_size,'sha256':sha(p)}
(A/'RAW_INDEX.json').write_text(json.dumps(index,indent=2)+'\n')
failed=[t for t in r['tests'] if t['outcome'] in ['FAILED','ERROR']];(A/'FAILED_METHODS.json').write_text(json.dumps(failed,indent=2)+'\n');(A/'SKIPPED_METHODS.json').write_text(json.dumps([t for t in r['tests'] if t['outcome']=='SKIPPED'],indent=2)+'\n')
reg=D/'ATTEMPTS.json';records=json.loads(reg.read_text()) if reg.exists() else [];assert name not in [x['directory'] for x in records];records.append({'directory':name,'sourceCommit':source,'testCounts':r['testCounts'],'classes':r['classes'],'selectors':c['selectors'],'exitCode':r['exitCode']});reg.write_text(json.dumps(records,indent=2)+'\n')
for n in ['verify_saved_results.py','collect_attempt.py','durable_monitor.py']:
 if (W/n).exists():shutil.copy2(W/n,D/n)
def manifest():
 (D/'SHA256.json').write_text(json.dumps({p.relative_to(D).as_posix():sha(p) for p in sorted(D.rglob('*')) if p.is_file() and p.name!='SHA256.json'},indent=2)+'\n');subprocess.run(['git','add',str(D)],cwd=R,check=True)
manifest();v=json.loads(subprocess.check_output(['python3',str(D/'verify_saved_results.py')],cwd=R,text=True));(A/'VERIFICATION.json').write_text(json.dumps(v,indent=2)+'\n');manifest();print(json.dumps({'collected':name,'source':source,'counts':r['testCounts'],'classes':r['classes'],'failedIdentities':[(x['testClass'],x['method']) for x in failed]}))
