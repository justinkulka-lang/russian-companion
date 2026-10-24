from pathlib import Path
from collections import Counter
from datetime import datetime,timezone
import time,json,os
W=Path(__file__).parent;R=Path('/workspace/russian-companion');O=W/'REGRESSION';context=json.loads((O/'CONTEXT.json').read_text());started=datetime.fromisoformat(context['startedUtc']).timestamp();counts=Counter();done=set();active={}
f=(O/'EVENTS.jsonl').open();history=(W/'ACTIVE_MONITOR.jsonl').open('a',buffering=1)
while True:
 while True:
  pos=f.tell();line=f.readline()
  if not line:break
  if not line.endswith('\n'):f.seek(pos);break
  e=json.loads(line);key=(e.get('className'),e.get('name'))
  if e['kind']=='START':active[key]=e['atMillis']/1000
  elif e['kind']=='RESULT':
   assert key not in done,key
   done.add(key);counts[e['resultType']]+=1;active.pop(key,None)
 now=time.time();workers=[]
 for proc in Path('/proc').iterdir():
  if not proc.name.isdigit():continue
  try:
   if Path(os.readlink(proc/'exe')).name!='java':continue
   args=(proc/'cmdline').read_bytes()
   if b'GradleWorkerMain' not in args or str(W).encode() not in args:continue
   stat=(proc/'stat').read_text().split();workers.append({'pid':int(proc.name),'cpuSeconds':round((int(stat[13])+int(stat[14]))/os.sysconf('SC_CLK_TCK'),1)})
  except (FileNotFoundError,PermissionError,ProcessLookupError):pass
 fatal=[str(p) for root in [R,R/'app',W,O] for p in root.glob('hs_err_pid*.log') if p.stat().st_mtime>=started]
 collector=json.loads((W/'COLLECTOR_STATUS.json').read_text());completion=json.loads((W/'LAUNCHER_COMPLETION.json').read_text()) if (W/'LAUNCHER_COMPLETION.json').exists() else None
 snapshot={'utc':datetime.now(timezone.utc).isoformat(),'elapsedMinutes':round((now-started)/60,1),'observedResultEvents':dict(counts),'completedIdentities':len(done),'remainingOfDiscovery':1363-len(done),'activeMethods':[{'name':k[0]+'.'+k[1],'seconds':round(now-v,1)} for k,v in sorted(active.items())],'workers':workers,'newFatalReports':fatal,'collectorState':collector['state'],'launcherCompletion':completion}
 history.write(json.dumps(snapshot)+'\n');print(json.dumps(snapshot),flush=True)
 if fatal or collector['state'] in ['STOPPED_WITH_ERROR','PUSHED_AND_PR_UPDATED']:break
 if completion is None:
  launcher=json.loads((W/'LAUNCHER.json').read_text());p=Path('/proc')/str(launcher['pid'])/'cmdline'
  if not p.exists() or str(W).encode() not in p.read_bytes():print('ALERT: supervisor disappeared without completion',flush=True);break
 time.sleep(50)
