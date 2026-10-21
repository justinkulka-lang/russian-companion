from pathlib import Path
from datetime import datetime,timezone
import json,hashlib,zipfile,subprocess,collections,shutil,xml.etree.ElementTree as ET
R=Path('/workspace/russian-companion');W=Path(__file__).parent;O=W/'STOPPED_RECOVERY';O.mkdir()
git=lambda *a:subprocess.check_output(['git',*a],cwd=R,text=True).strip()
sha=lambda b:hashlib.sha256(b).hexdigest()
save=lambda n,v:(O/n).write_text(json.dumps(v,indent=2)+'\n')
c=json.loads((W/'REGRESSION/CONTEXT.json').read_text());assert git('rev-parse','HEAD')==c['sourceCommit'] and not git('status','--porcelain')
assert not (W/'REGRESSION/RUN.exit').exists() and not (W/'REGRESSION/RESULT.json').exists()
for k,v in c['inputTrees'].items():assert git('rev-parse','HEAD:'+k)==v
workers=[]
for p in Path('/proc').iterdir():
 if not p.name.isdigit():continue
 try:
  cmd=(p/'cmdline').read_bytes().replace(b'\0',b' ')
  if b'worker.org.gradle.process.internal.worker.GradleWorkerMain' in cmd and b'Gradle Test Executor' in cmd:workers.append(p.name)
 except (OSError,PermissionError):pass
assert not workers,workers
raw=(W/'REGRESSION/EVENTS.jsonl').read_bytes();lines=raw.splitlines(keepends=True);events=[json.loads(b) for b in lines if b.endswith(b'\n')]
results=[v for v in events if v['kind']=='RESULT'];by={v['className']+'#'+v['name']:v for v in results};assert len(by)==len(results)
disc=json.loads((W/'DISCOVERY/RESULT.json').read_text());expected={v['testClass']+'#'+v['method'] for v in disc['tests']};assert set(by)<=expected and len(expected)==1320
physical='dev.russiancompanion.acceptance.AcceptanceBoundaryTest#at020_physicalRebootPersistsGovernedRecords'
skips=[k for k,v in by.items() if v['resultType']=='SKIPPED'];assert physical in skips
interrupted=sorted(set(skips)-{physical});missing=sorted(expected-set(by));selected=sorted(set(missing+interrupted));assert len(missing)==742 and len(interrupted)==2 and len(selected)==744
selectors=[v.replace('#','.') for v in selected];save('CONTINUATION_SELECTORS.json',selectors)
save('OBSERVED_OUTCOMES.json',[dict(identity=k,resultType=v['resultType'],seconds=(v['endTime']-v['startTime'])/1000,exceptions=[dict(type=e.get('type'),messagePreview=str(e.get('message') or '')[:1000],messageBytes=len(str(e.get('message') or '').encode()),messageSha256=sha(str(e.get('message') or '').encode())) for e in v.get('exceptions',[])]) for k,v in sorted(by.items())])
save('FAILURE_LEDGER.json',[dict(identity=k,**v) for k,v in sorted(by.items()) if v['resultType']=='FAILURE'])
sources={}
for p in sorted((W/'REGRESSION').rglob('*')):
 if p.is_file():sources['REGRESSION/'+p.relative_to(W/'REGRESSION').as_posix()]=p
for n in ['ACTIVE_SESSION.json','MONITOR_STATE.json','PROGRESS.json','run.py','resources.gradle','monitor.py']:
 if (W/n).exists():sources['EXTERNAL/'+n]=W/n
for p in sorted((R/'app/build/test-results/testDebugUnitTest').rglob('*')):
 if p.is_file():sources['BUILD_RESULTS/'+p.relative_to(R/'app/build/test-results/testDebugUnitTest').as_posix()]=p
index=[]
with zipfile.ZipFile(O/'RAW_STOPPED.zip','w',zipfile.ZIP_DEFLATED) as z:
 for n,p in sources.items():
  b=p.read_bytes();z.writestr(n,b);index.append(dict(path=n,bytes=len(b),sha256=sha(b)))
with zipfile.ZipFile(O/'RAW_STOPPED.zip') as z:
 assert z.testzip() is None
 for v in index:assert sha(z.read(v['path']))==v['sha256']
save('RAW_STOPPED_INDEX.json',dict(archive='RAW_STOPPED.zip',bytes=(O/'RAW_STOPPED.zip').stat().st_size,sha256=sha((O/'RAW_STOPPED.zip').read_bytes()),members=index,crcAndAllMemberHashesVerified=True))
xml=list((R/'app/build/test-results/testDebugUnitTest').glob('TEST-*.xml'));start=datetime.fromisoformat(c['startedUtc']).timestamp();stale=[]
for p in xml:
 t=datetime.fromisoformat(ET.parse(p).getroot().get('timestamp').replace('Z','+00:00'))
 if t.tzinfo is None:t=t.replace(tzinfo=timezone.utc)
 if t.timestamp()<start-1:stale.append(p.name)
summary=dict(sourceCommit=c['sourceCommit'],observedUtc=datetime.now(timezone.utc).isoformat(),inputTrees=c['inputTrees'],workingTree='CLEAN',activeTestWorkers=[],launcherPresent=Path('/proc/352777').exists(),actualExitCode=None,finalResultAvailable=False,freshFinalXmlCount=len(xml)-len(stale),staleDiscoveryXmlRejected=stale,partialEventOutcomes=dict(collections.Counter(v['resultType'] for v in results)),partialReportedIdentities=len(by),discoveredIdentities=len(expected),unreported=missing,interruptionTimeSkips=interrupted,declaredPhysicalSkip=physical,continuationMethods=len(selected),trailingIncompleteEventBytes=len(raw)-sum(len(v) for v in lines if v.endswith(b'\n')),stopCause='UNCONFIRMED;environment starting notification and tool-call interruption observed;launcher/workers absent afterward;no exit record',completeSuite=False,scopesNotSummed=True,physicalAcceptance='OUTSTANDING_LAST',apkAssemblyInstallSigning='NOT_RUN')
save('STOPPED_MANIFEST.json',summary)
save('SHA256.json',{p.name:sha(p.read_bytes()) for p in O.iterdir() if p.is_file() and p.name!='SHA256.json'})
print(json.dumps({k:summary[k] for k in ['sourceCommit','partialEventOutcomes','partialReportedIdentities','freshFinalXmlCount','continuationMethods','stopCause']}))
