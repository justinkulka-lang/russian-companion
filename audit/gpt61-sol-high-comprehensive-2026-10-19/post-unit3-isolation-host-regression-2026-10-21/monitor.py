from pathlib import Path
import json,collections,hashlib,time,sys
W=Path(__file__).parent;O=W/'REGRESSION';event=O/'EVENTS.jsonl';state_path=W/'MONITOR_STATE.json'
s=json.loads(state_path.read_text()) if state_path.exists() else dict(offset=0,starts={},results={})
new=[]
if event.exists():
 with event.open('r') as f:
  f.seek(s['offset'])
  while True:
   pos=f.tell();line=f.readline()
   if not line or not line.endswith('\n'):f.seek(pos);break
   x=json.loads(line);key=x['className']+'#'+x['name']
   if x['kind']=='START':s['starts'][key]=x['atMillis']
   elif x['kind']=='RESULT':
    assert key not in s['results'],'Duplicate result identity'
    exceptions=x.get('exceptions',[]);previews=[dict(type=e.get('type'),messagePreview=str(e.get('message') or '')[:600],messageBytes=len(str(e.get('message') or '').encode()),messageSha256=hashlib.sha256(str(e.get('message') or '').encode()).hexdigest()) for e in exceptions]
    s['results'][key]=dict(resultType=x['resultType'],seconds=round((x['endTime']-x['startTime'])/1000,3),exceptionPreviews=previews)
    if x['resultType']!='SUCCESS':new.append(dict(identity=key,**s['results'][key]))
  s['offset']=f.tell()
state_path.write_text(json.dumps(s,indent=2)+'\n')
counts=dict(collections.Counter(x['resultType'] for x in s['results'].values()));pending=[dict(identity=k,seconds=round(time.time()-t/1000)) for k,t in s['starts'].items() if k not in s['results']]
out=dict(phase='COMPLETED_PROCESS' if (O/'RUN.exit').exists() else 'IN_PROGRESS',completed=len(s['results']),expected=1323,counts=counts,newNonSuccess=new,pending=pending,exitCode=int((O/'RUN.exit').read_text()) if (O/'RUN.exit').exists() else None,notFinalUntilCollected=(not (O/'RESULT.json').exists()),completeSuite=False)
(W/'PROGRESS.json').write_text(json.dumps(out,indent=2)+'\n');print(json.dumps(out))
