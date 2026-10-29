from pathlib import Path
from datetime import datetime,timezone
import subprocess,json,time
W=Path(__file__).parent;R=Path('/workspace/russian-companion')
assert not (W/'REGRESSION_LAUNCHER.json').exists() and not subprocess.check_output(['git','status','--porcelain'],cwd=R,text=True).strip()
subprocess.run(['python3',str(W/'launch.py'),'REGRESSION'],cwd=R,check=True)
for _ in range(60):
 if (W/'REGRESSION/CONTEXT.json').exists():break
 time.sleep(.5)
assert (W/'REGRESSION/CONTEXT.json').exists()
processes={}
for name in ['durable_monitor.py','completion_worker.py']:
 with (W/('DURABLE_MONITOR.log' if name=='durable_monitor.py' else 'AUTO_COMPLETION.log')).open('xb') as out:
  p=subprocess.Popen(['python3',str(W/name)],cwd=R,stdin=subprocess.DEVNULL,stdout=out,stderr=subprocess.STDOUT,start_new_session=True)
 processes[name]=p.pid
(W/'COMPLETION_OWNERSHIP_PROCESS.json').write_text(json.dumps(dict(startedUtc=datetime.now(timezone.utc).isoformat(),processes=processes),indent=2)+'\n');print(json.dumps(processes))
