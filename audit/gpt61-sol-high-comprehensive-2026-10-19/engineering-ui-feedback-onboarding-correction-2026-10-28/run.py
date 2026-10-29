"""One synchronous launcher and completion collector; never changes repository inputs."""
from pathlib import Path
from datetime import datetime,timezone
import subprocess,json,hashlib,shutil,sys,xml.etree.ElementTree as ET,csv,os
R=Path('/workspace/russian-companion');W=Path(__file__).parent;mode=sys.argv[1];assert mode in ['DISCOVERY','REGRESSION'];dry=mode=='DISCOVERY';O=W/mode;O.mkdir()
git=lambda *a:subprocess.check_output(['git',*a],cwd=R,text=True).strip()
sha=lambda p:hashlib.sha256(p.read_bytes()).hexdigest()
save=lambda n,v:(O/n).write_text(json.dumps(v,indent=2)+'\n')
trees={p:git('rev-parse','HEAD:'+p) for p in ['app/src/main','app/src/test','catalog','docs','review','release']};assert not git('status','--porcelain')
assert trees==json.loads((W/'SCOPE.json').read_text())['inputTrees']
assert not any(v=='1' for k,v in os.environ.items() if k.startswith('WRITE_'))
inits=['/workspace/setup/cloud-validation.gradle',str(R/'audit/independent-gpt61-2026-10-12/broader-regression-2026-10-18/broader-tests.gradle'),str(W/'resources.gradle')]
args=[]
for p in inits:args+=['-I',p]
args+=['-Daudit.history.output='+str(O/'HISTORY_CASES.jsonl'),'-Daudit.events.path='+str(O/'EVENTS.jsonl'),'-Daudit.gc.dir='+str(O),'testDebugUnitTest','--rerun']
selectors=json.loads((W/'SCOPE.json').read_text())['selectors']
for selector in selectors:args+=['--tests',selector]
args+=['--test-dry-run'] if dry else ['compileDebugSources','lintDebug','verifyReleaseGateAttachment','--continue']
args+=['--offline','--max-workers=2','--console=plain']
start=datetime.now(timezone.utc)
c=dict(sourceCommit=git('rev-parse','HEAD'),sourceRootTree=git('rev-parse','HEAD^{tree}'),workingTreeAtStart='CLEAN',workingDirectory=str(R),activation='source /workspace/setup/android-env.sh',startedUtc=start.isoformat(),inputTrees=trees,mode=mode,syntheticDryRun=dry,command=['./gradlew',*args],runnerSha256=sha(Path(__file__)),initScriptSha256={p:sha(Path(p)) for p in inits},selectors=selectors,workerPolicy='2forks/6GiB/forkEvery4;GC logs retained',resources=dict(cgroupMemoryMax=Path('/sys/fs/cgroup/memory.max').read_text().strip(),cgroupMemoryCurrent=Path('/sys/fs/cgroup/memory.current').read_text().strip(),cpus=len(os.sched_getaffinity(0))),exclusions={'VisualReviewCaptureTest':2,'Units35ChunkCaptureTest':2,'Units610ChunkCaptureTest':10,'Units1115GapCaptureTest':2},scope='61 methods /6FULLclasses: unchanged6methodUIbaseline plus existing option presentation/runtime/rebuild/Unit14feedback controls;not complete suite',physicalAcceptance='OUTSTANDING_LAST',apkAssemblyInstallSigning='NOT_RUN',completeSuite=False)
save('CONTEXT.json',c)
with (O/'RUN.log').open('w') as log:
 p=subprocess.run(['bash','-c','source /workspace/setup/android-env.sh; exec ./gradlew "$@"','host-regression',*args],cwd=R,stdout=log,stderr=subprocess.STDOUT)
(O/'RUN.exit').write_text(str(p.returncode)+'\n')
assert not git('status','--porcelain'),'Repository changed during run'
assert all(git('rev-parse','HEAD:'+k)==v for k,v in trees.items()),'Input trees changed'
(O/'XML').mkdir();rows=[];suite_rows=[]
for path in sorted((R/'app/build/test-results/testDebugUnitTest').glob('TEST-*.xml')):
 suite=ET.parse(path).getroot();stamp=datetime.fromisoformat(suite.get('timestamp').replace('Z','+00:00'))
 if stamp.tzinfo is None:stamp=stamp.replace(tzinfo=timezone.utc)
 assert stamp.timestamp()>=start.timestamp()-1,(path,stamp,start)
 shutil.copy2(path,O/'XML'/path.name);suite_rows.append(suite.attrib)
 for t in suite.findall('testcase'):
  status='FAILED' if t.find('failure') is not None else 'ERROR' if t.find('error') is not None else 'SKIPPED' if t.find('skipped') is not None else 'PASSED';element=next((t.find(x) for x in ['failure','error','skipped'] if t.find(x) is not None),None);message=element.get('message','') if element is not None else ''
  rows.append(dict(testClass=t.get('classname'),method=t.get('name'),outcome=status,seconds=t.get('time'),messagePreview=message[:1000],fullMessageBytes=len(message.encode()),fullMessageSha256=hashlib.sha256(message.encode()).hexdigest()))
events=[json.loads(x) for x in (O/'EVENTS.jsonl').read_text().splitlines()];results=[x for x in events if x['kind']=='RESULT'];names=[(x['testClass'],x['method']) for x in rows];en=[(x['className'],x['name']) for x in results]
assert len(names)==len(set(names)) and sorted(names)==sorted(en)
by={(x['className'],x['name']):x['resultType'] for x in results}
for x in rows:assert by[(x['testClass'],x['method'])]=={'PASSED':'SUCCESS','FAILED':'FAILURE','ERROR':'FAILURE','SKIPPED':'SKIPPED'}[x['outcome']]
assert not any(x['testClass'].split('.')[-1] in c['exclusions'] for x in rows)
inventory=json.loads((W/'SCOPE.json').read_text())
if not dry:
 assert set(names)=={(x['testClass'],x['method']) for x in inventory['expectedMethods']}
 assert len(names)==inventory['methodCount'] and len(set(n[0] for n in names))==inventory['classes']
counts=dict(reported=len(rows),passed=sum(x['outcome']=='PASSED' for x in rows),failed=sum(x['outcome']=='FAILED' for x in rows),errors=sum(x['outcome']=='ERROR' for x in rows),skipped=sum(x['outcome']=='SKIPPED' for x in rows));log=(O/'RUN.log').read_text();tasks={}
for name in ['testDebugUnitTest','compileDebugKotlin','compileDebugUnitTestKotlin','compileDebugSources','lintAnalyzeDebug','lintAnalyzeDebugUnitTest','lintDebug','verifyReleaseGateAttachment']:
 prefix='> Task :app:'+name;tasks[name]=next((s for s in log.splitlines() if s==prefix or s.startswith(prefix+' ')),'NOT_REACHED')
lint=None
if not dry and tasks['lintDebug']!='NOT_REACHED':
 shutil.copy2(R/'app/build/reports/lint-results-debug.xml',O/'LINT.xml');issues=ET.parse(O/'LINT.xml').getroot().findall('issue');lint={s:sum(x.get('severity')==s for x in issues) for s in ['Fatal','Error','Warning','Hint']}
comparison=None
if not dry:
 discovery=json.loads((W/'DISCOVERY/RESULT.json').read_text());expected={(x['testClass'],x['method']) for x in discovery['tests']};actual=set(names)
 comparison=dict(sameInputTrees=discovery['inputTrees']==trees,discoveredNotReported=sorted(expected-actual),reportedNotDiscovered=sorted(actual-expected),exactSameMethodSet=expected==actual)
 assert comparison['sameInputTrees'] and comparison['exactSameMethodSet'],comparison
summary=dict(sourceCommit=c['sourceCommit'],inputTrees=trees,syntheticDryRun=dry,exitCode=p.returncode,finishedUtc=datetime.now(timezone.utc).isoformat(),testCounts=counts,classes=len(suite_rows),tests=rows,suites=suite_rows,tasks=tasks,lintIssuesBySeverity=lint,selectedFreshXmlAndEventsMatch=True,eventOutcomeCountsMatchXml=True,discoveryComparison=comparison,allFreshSelectedMethodsReported=(not dry and comparison['exactSameMethodSet']),completeSuite=False,scopesNotSummed=True,physicalAcceptance='OUTSTANDING_LAST',apkAssemblyInstallSigning='NOT_RUN')
save('RESULT.json',summary)
with (O/'RESULTS.csv').open('w',newline='') as f:
 writer=csv.DictWriter(f,fieldnames=list(rows[0]),lineterminator='\n');writer.writeheader();writer.writerows(rows)
save('SHA256.json',{p.relative_to(O).as_posix():sha(p) for p in sorted(O.rglob('*')) if p.is_file() and p.name!='SHA256.json'})
print(json.dumps(dict(mode=mode,source=c['sourceCommit'],counts=counts,classes=len(suite_rows),exitCode=p.returncode,actualExecution=not dry)))
sys.exit(p.returncode)
