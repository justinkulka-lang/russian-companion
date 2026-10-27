from pathlib import Path
from datetime import datetime,timezone
import subprocess,json,sys,os,traceback
W=Path(__file__).parent;R=Path('/workspace/russian-companion');mode=sys.argv[1];assert mode in ['DISCOVERY','REGRESSION'];now=lambda:datetime.now(timezone.utc).isoformat()
def save(name,v):
 with (W/name).open('x') as f:json.dump(v,f,indent=2);f.write('\n')
try:
 source=subprocess.check_output(['git','rev-parse','HEAD'],cwd=R,text=True).strip()
 with (W/(mode+'_RUNNER.log')).open('xb') as out:
  p=subprocess.Popen(['python3',str(W/'run.py'),mode],cwd=R,stdin=subprocess.DEVNULL,stdout=out,stderr=subprocess.STDOUT)
  save(mode+'_RUNNER_PROCESS.json',dict(pid=p.pid,supervisorPid=os.getpid(),sourceCommit=source,startedUtc=now(),argv=p.args))
  code=p.wait()
 (W/(mode+'_LAUNCHER.exit')).write_text(str(code)+'\n')
 save(mode+'_COMPLETION.json',dict(runnerExit=code,finishedUtc=now(),sourceCommit=source,gradleExit=(W/mode/'RUN.exit').read_text().strip() if (W/mode/'RUN.exit').exists() else None,resultExists=(W/mode/'RESULT.json').exists(),manifestExists=(W/mode/'SHA256.json').exists()))
except BaseException:
 (W/(mode+'_SUPERVISOR_ERROR.txt')).write_text(traceback.format_exc());raise
