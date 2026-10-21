from pathlib import Path
from datetime import datetime,timezone
import json,hashlib,zipfile,subprocess,collections,shutil,xml.etree.ElementTree as ET
R=Path('/workspace/russian-companion');W=Path(__file__).parent;O=W/'SECOND_STOPPED_RECOVERY';O.mkdir();S=W/'CONTINUATION'
git=lambda *a:subprocess.check_output(['git',*a],cwd=R,text=True).strip();sha=lambda p:hashlib.sha256(p.read_bytes()).hexdigest();save=lambda n,v:(O/n).write_text(json.dumps(v,indent=2)+'\n')
c=json.loads((S/'CONTEXT.json').read_text());assert git('rev-parse','HEAD')==c['sourceCommit'] and not git('status','--porcelain')
assert not(S/'RUN.exit').exists() and not(S/'RESULT.json').exists()
for k,v in c['inputTrees'].items():assert git('rev-parse','HEAD:'+k)==v
for p in Path('/proc').iterdir():
 if not p.name.isdigit():continue
 try:
  if (p/'comm').read_text().strip()!='java':continue
  cmd=(p/'cmdline').read_bytes();assert not(b'GradleWorkerMain' in cmd or b'GradleWrapperMain' in cmd),'Client/worker still active'
 except (FileNotFoundError,ProcessLookupError):pass
events=[json.loads(b) for b in (S/'EVENTS.jsonl').read_bytes().splitlines(keepends=True) if b.endswith(b'\n')];results=[v for v in events if v['kind']=='RESULT'];by={v['className']+'#'+v['name']:v for v in results};assert len(by)==len(results)==237
expected={v.rsplit('.',1)[0]+'#'+v.rsplit('.',1)[1] for v in c['selectors']};assert len(expected)==744 and set(by)<=expected
interrupted=sorted(k for k,v in by.items() if v['resultType']=='SKIPPED');missing=sorted(expected-set(by));selected=sorted(set(missing+interrupted));assert len(missing)==507 and len(interrupted)==2 and len(selected)==509
save('CONTINUATION_SELECTORS.json',[v.replace('#','.') for v in selected])
observed=[dict(identity=k,resultType=v['resultType'],seconds=(v['endTime']-v['startTime'])/1000,exceptions=[dict(type=e.get('type'),messagePreview=str(e.get('message') or '')[:1000],messageBytes=len(str(e.get('message') or '').encode()),messageSha256=hashlib.sha256(str(e.get('message') or '').encode()).hexdigest()) for e in v.get('exceptions',[])]) for k,v in sorted(by.items())];save('OBSERVED_OUTCOMES.json',observed)
sources={}
for base,prefix in [(S,'CONTINUATION'),(W/'CONTINUATION_CLI_REJECTED','CLI_REJECTED'),(R/'app/build/test-results/testDebugUnitTest','BUILD_RESULTS')]:
 for p in sorted(base.rglob('*')):
  if p.is_file():sources[prefix+'/'+p.relative_to(base).as_posix()]=p
for n in ['run_continuation.py','monitor_continuation.py','resources.gradle','CONTINUATION_SESSION.json','CONTINUATION_MONITOR_STATE.json','CONTINUATION_PROGRESS.json']:sources['EXTERNAL/'+n]=W/n
index=[]
with zipfile.ZipFile(O/'RAW_STOPPED_CONTINUATION.zip','w',zipfile.ZIP_DEFLATED) as z:
 for n,p in sources.items():z.write(p,n);index.append(dict(path=n,bytes=p.stat().st_size,sha256=sha(p)))
with zipfile.ZipFile(O/'RAW_STOPPED_CONTINUATION.zip') as z:
 assert z.testzip() is None
 for v in index:assert hashlib.sha256(z.read(v['path'])).hexdigest()==v['sha256']
save('RAW_STOPPED_INDEX.json',dict(archive='RAW_STOPPED_CONTINUATION.zip',bytes=(O/'RAW_STOPPED_CONTINUATION.zip').stat().st_size,sha256=sha(O/'RAW_STOPPED_CONTINUATION.zip'),members=index,crcAndAllMemberHashesVerified=True))
xml=list((R/'app/build/test-results/testDebugUnitTest').glob('TEST-*.xml'));start=datetime.fromisoformat(c['startedUtc']).timestamp()
for p in xml:
 t=datetime.fromisoformat(ET.parse(p).getroot().get('timestamp').replace('Z','+00:00'))
 if t.tzinfo is None:t=t.replace(tzinfo=timezone.utc)
 assert t.timestamp()<start-1
summary=dict(sourceCommit=c['sourceCommit'],observedUtc=datetime.now(timezone.utc).isoformat(),inputTrees=c['inputTrees'],actualExitCode=None,freshFinalXmlCount=0,staleDiscoveryXmlRejected=len(xml),partialEventOutcomes=dict(collections.Counter(v['resultType'] for v in results)),partialReportedIdentities=len(by),selectedIdentities=744,unreported=missing,interruptionTimeSkips=interrupted,continuationMethods=509,stopCause='UNCONFIRMED;exec-server transport disconnected was observed;launcher/client/workers absent afterward,no actual exit record',completeSuite=False,scopesNotSummed=True,physicalAcceptance='OUTSTANDING_LAST',apkAssemblyInstallSigning='NOT_RUN');save('STOPPED_MANIFEST.json',summary)
save('SHA256.json',{p.name:sha(p) for p in O.iterdir() if p.is_file() and p.name!='SHA256.json'})
print(json.dumps({k:summary[k] for k in ['sourceCommit','partialEventOutcomes','partialReportedIdentities','continuationMethods','stopCause']}))
