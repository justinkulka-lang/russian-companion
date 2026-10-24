from pathlib import Path
from datetime import datetime,timezone
import subprocess,json,traceback,os,shutil
W=Path(__file__).parent;R=Path('/workspace/russian-companion');now=lambda:datetime.now(timezone.utc).isoformat()
def save(n,v):
 with (W/n).open('x') as f:json.dump(v,f,indent=2);f.write('\n')
try:
 assert not subprocess.check_output(['git','status','--porcelain'],cwd=R,text=True).strip()
 source=subprocess.check_output(['git','rev-parse','HEAD'],cwd=R,text=True).strip()
 save('PIPELINE_CONTEXT.json',dict(sourceCommit=source,pid=os.getpid(),startedUtc=now()))
 (W/'INIT').mkdir()
 for p in ['/workspace/setup/cloud-validation.gradle',str(R/'audit/independent-gpt61-2026-10-12/broader-regression-2026-10-18/broader-tests.gradle'),str(W/'resources.gradle')]:shutil.copy2(p,W/'INIT'/Path(p).name)
 with (W/'TOOLCHAIN.txt').open('w') as f:subprocess.run(['bash','-c','source /workspace/setup/android-env.sh; java -version; ./gradlew --version'],cwd=R,stdout=f,stderr=subprocess.STDOUT,check=True)
 for mode in ['DISCOVERY','REGRESSION']:
  assert not (W/mode).exists()
  with (W/(mode+'_LAUNCHER.log')).open('xb') as out:
   p=subprocess.Popen(['python3',str(W/'supervise.py'),mode],cwd=R,stdin=subprocess.DEVNULL,stdout=out,stderr=subprocess.STDOUT)
   save(mode+'_LAUNCHER.json',dict(pid=p.pid,sourceCommit=source,startedUtc=now(),argv=p.args))
   exitcode=p.wait()
  c=json.loads((W/(mode+'_COMPLETION.json')).read_text());assert c['sourceCommit']==source and c['resultExists'] and c['manifestExists'],c
  r=json.loads((W/mode/'RESULT.json').read_text());assert r['testCounts']['reported']==2 and r['classes']==1
  if mode=='DISCOVERY':assert r['testCounts']==dict(reported=2,passed=0,failed=0,errors=0,skipped=2) and r['exitCode']==0
 save('PIPELINE_COMPLETION.json',dict(sourceCommit=source,finishedUtc=now(),result=str(W/'REGRESSION/RESULT.json'),counts=r['testCounts'],gradleExit=r['exitCode']))
except BaseException:
 (W/'PIPELINE_ERROR.txt').write_text(traceback.format_exc());raise
