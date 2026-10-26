from pathlib import Path
from collections import Counter
from datetime import datetime,timezone
import json,time,os
W=Path(__file__).parent; O=W/'REGRESSION'; offset=0; counts=Counter(); active={}; failures=[]; started=datetime.fromisoformat(json.loads((O/'CONTEXT.json').read_text())['startedUtc']).timestamp()
while True:
 f=O/'EVENTS.jsonl'
 if f.exists():
  with f.open() as stream:
   stream.seek(offset)
   while True:
    pos=stream.tell();line=stream.readline()
    if not line or not line.endswith('\n'):break
    offset=stream.tell()
    try:e=json.loads(line)
    except ValueError:continue
    key=(e.get('className'),e.get('name'))
    if e.get('kind')=='START': active[key]=e.get('atMillis')
    if e.get('kind')=='RESULT':
     counts[e.get('resultType')]+=1;active.pop(key,None)
     if e.get('resultType')=='FAILURE':failures.append(key)
 processes=[]
 for d in Path('/proc').iterdir():
  if not d.name.isdigit():continue
  try:
   argv=(d/'cmdline').read_bytes().split(b'\0');cmd=b' '.join(argv).decode(errors='replace')
   if argv and ('java' in Path(argv[0].decode()).name or 'python' in Path(argv[0].decode()).name) and (str(W) in cmd or 'Gradle Test Executor' in cmd) and 'monitor.py' not in cmd:
    fields=(d/'stat').read_text().rsplit(')',1)[1].split();processes.append(dict(pid=int(d.name),state=fields[0],cpuTicks=int(fields[11])+int(fields[12]),command=cmd[:160]))
  except (OSError,ValueError):pass
 tail=[]
 if (O/'RUN.log').exists():
  with (O/'RUN.log').open('rb') as log:
   log.seek(max(0,(O/'RUN.log').stat().st_size-2000));tail=log.read().decode(errors='replace').splitlines()[-3:]
 native=[dict(path=str(p),bytes=p.stat().st_size,mtime=p.stat().st_mtime) for root in [Path('/workspace/russian-companion'),Path('/workspace/russian-companion/app')] for pattern in ['hs_err_pid*.log','replay_pid*.log'] for p in root.glob(pattern) if p.stat().st_mtime>=started]
 status=dict(newNativeReports=native,utc=datetime.now(timezone.utc).isoformat(),partialEvents=dict(counts),completedEvents=sum(counts.values()),expectedMethods=111,activeMethods=list(active)[-4:],failureIdentities=failures,processes=processes,logTail=tail,completionExists=(W/'REGRESSION_COMPLETION.json').exists())
 tmp=W/'DURABLE_MONITOR_STATUS.tmp';tmp.write_text(json.dumps(status,indent=2)+'\n');tmp.replace(W/'DURABLE_MONITOR_STATUS.json');print(json.dumps(status),flush=True)
 if native:(W/'MONITOR_ESCALATION.json').write_text(json.dumps(dict(reason='NEW_NATIVE_REPORT_REQUIRES_INVESTIGATION',reports=native,utc=status['utc']),indent=2))
 if status['completionExists']:
  print((W/'REGRESSION_COMPLETION.json').read_text(),flush=True);break
 time.sleep(45)
