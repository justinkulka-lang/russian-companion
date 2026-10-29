from pathlib import Path
from datetime import datetime,timezone
import subprocess,json,sys
W=Path(__file__).parent;R=Path('/workspace/russian-companion');mode=sys.argv[1];assert mode in ['DISCOVERY','REGRESSION'];assert not (W/(mode+'_LAUNCHER.json')).exists();assert not subprocess.check_output(['git','status','--porcelain'],cwd=R,text=True).strip()
with (W/(mode+'_LAUNCHER.log')).open('xb') as log:p=subprocess.Popen(['python3',str(W/'supervise.py'),mode],cwd=R,stdin=subprocess.DEVNULL,stdout=log,stderr=subprocess.STDOUT,start_new_session=True)
v=dict(pid=p.pid,startedUtc=datetime.now(timezone.utc).isoformat(),sourceCommit=subprocess.check_output(['git','rev-parse','HEAD'],cwd=R,text=True).strip(),mode=mode);(W/(mode+'_LAUNCHER.json')).write_text(json.dumps(v,indent=2)+'\n');print(json.dumps(v))
