"""Read-only exact core evidence/conservation verification; zero test execution."""
from pathlib import Path
from datetime import datetime,timezone
from collections import Counter
import subprocess,json,hashlib,zipfile,re,xml.etree.ElementTree as E
D=Path(__file__).resolve().parent;R=next(p for p in D.parents if (p/'.git').exists());sha=lambda b:hashlib.sha256(b).hexdigest();read=lambda n:json.loads((D/n).read_text());blob=lambda s,n:subprocess.check_output(['git','show',s+':'+n],cwd=R);g=lambda *a:subprocess.check_output(['git',*a],cwd=R,text=True).strip()
m=read('SHA256.json');assert set(m)=={p.relative_to(D).as_posix() for p in D.rglob('*') if p.is_file() and p.name!='SHA256.json'};tracked=set(g('ls-files').splitlines())
for n,h in m.items():assert sha((D/n).read_bytes())==h and (D/n).relative_to(R).as_posix() in tracked,n
prep=read('PREPARATION.json');r=read('REGRESSION_RESULT.json');S=r['sourceCommit'];assert r['classes']==3 and r['testCounts']['reported']==15
for n,h in prep['preexistingTestFiles'].items():assert sha(blob(S,n))==h,n
for n,h in prep['currentRecordHashes'].items():assert sha(blob(S,n))==h,n
assert len(prep['currentRecordHashes'])==155 and len(prep['archives'])==73
for n,h in prep['archives'].items():
 assert sha((R/n).read_bytes())==h
 with zipfile.ZipFile(R/n) as z:assert z.testzip() is None
for n in ['catalog','docs','review','release']:assert g('rev-parse',S+':'+n)==g('rev-parse',prep['parentCommit']+':'+n)
with zipfile.ZipFile(D/'ORIGINAL_PRODUCTION.zip') as z:
 assert z.testzip() is None
 for n in prep['changedProductionFiles']:assert z.read(n)==blob(prep['parentCommit'],n)
a=blob(prep['parentCommit'],'app/src/main/assets/content/v2-curriculum.json');b=blob(S,'app/src/main/assets/content/v2-curriculum.json');assert a.replace(b'unit12-prior-reading-past-gender-v1',b'unit12-prior-gender-past-gender-v2')==b
kernel=blob(S,'app/src/main/java/dev/russiancompanion/v2/curriculum/V2CumulativeReview.kt').decode()
objects={name:sha(body.encode()) for name,body in re.findall(r'internal object (\w+CumulativeReviewPolicy)\s*\{(.*?)\n\}',kernel,re.S)}
assert len(objects)==39 and all(objects[n]==h for n,h in prep['oldPolicyObjects'].items())
with zipfile.ZipFile(D/'VERIFIED_TEST_SOURCES.zip') as z:
 for n in z.namelist():assert z.read(n)==blob(S,n)
with zipfile.ZipFile(D/'RAW_CORE.zip') as z:
 assert z.testzip() is None;idx=read('RAW_CORE_INDEX.json');assert set(idx)==set(z.namelist())
 for n,v in idx.items():assert len(z.read(n))==v['bytes'] and sha(z.read(n))==v['sha256']
 phases={}
 for mode in ['DISCOVERY','REGRESSION']:
  c=json.loads(z.read(mode+'/CONTEXT.json'));s=json.loads(z.read(mode+'/RESULT.json'));assert c['sourceCommit']==s['sourceCommit']==S and c['sourceRootTree']==g('rev-parse',S+'^{tree}');assert c['inputTrees']==s['inputTrees']
  for n,h in c['inputTrees'].items():assert g('rev-parse',S+':'+n)==h
  assert sha(z.read('run.py'))==c['runnerSha256']
  for n,h in c['initScriptSha256'].items():assert sha(z.read('INIT/'+Path(n).name))==h
  rows={};start=datetime.fromisoformat(c['startedUtc'])
  for n in z.namelist():
   if not n.startswith(mode+'/XML/') or not n.endswith('.xml'):continue
   suite=E.fromstring(z.read(n));t=datetime.fromisoformat(suite.get('timestamp').replace('Z','+00:00'));t=t.replace(tzinfo=timezone.utc) if t.tzinfo is None else t;assert t.timestamp()>=start.timestamp()-1
   for t in suite.findall('testcase'):
    key=(t.get('classname'),t.get('name'));assert key not in rows;rows[key]='FAILED' if t.find('failure') is not None else 'ERROR' if t.find('error') is not None else 'SKIPPED' if t.find('skipped') is not None else 'PASSED'
  assert len(rows)==15 and len({k[0] for k in rows})==3 and rows=={(t['testClass'],t['method']):t['outcome'] for t in s['tests']}
  events=[json.loads(l) for l in z.read(mode+'/EVENTS.jsonl').splitlines()];events=[e for e in events if e['kind']=='RESULT'];assert len(events)==15 and {(e['className'],e['name']):e['resultType'] for e in events}=={k:{'PASSED':'SUCCESS','FAILED':'FAILURE','ERROR':'FAILURE','SKIPPED':'SKIPPED'}[v] for k,v in rows.items()}
  n=Counter(rows.values());assert s['testCounts']==dict(reported=15,passed=n['PASSED'],failed=n['FAILED'],errors=n['ERROR'],skipped=n['SKIPPED']);done=json.loads(z.read(mode+'_COMPLETION.json'));assert done['runnerExit']==s['exitCode']==int(done['gradleExit'])==int(z.read(mode+'/RUN.exit'))
  for n,h in json.loads(z.read(mode+'/SHA256.json')).items():assert sha(z.read(mode+'/'+n))==h
  assert c['exclusions']=={'VisualReviewCaptureTest':2,'Units35ChunkCaptureTest':2,'Units610ChunkCaptureTest':10,'Units1115GapCaptureTest':2};phases[mode]=(rows,s)
 assert set(phases['DISCOVERY'][0])==set(phases['REGRESSION'][0]) and set(phases['DISCOVERY'][0].values())=={'SKIPPED'}
 monitor=json.loads(z.read('DURABLE_MONITOR_STATUS.json'));assert monitor['completionExists'] and not monitor['newNativeReports']
assert r['discoveryComparison']['exactSameMethodSet'] and r['discoveryComparison']['sameInputTrees']
if (D/'OFFICIAL_SOURCE_DIGEST.json').exists():
 o=read('OFFICIAL_SOURCE_DIGEST.json');assert o['sourceCommit']==S and o['inputTrees']==r['inputTrees'] and all(c['exitCode']==0 for c in o['commands']);assert re.fullmatch('[0-9a-f]{64}',o['canonicalSourceDigest'])
 with zipfile.ZipFile(D/'OFFICIAL_COMPUTATION.zip') as z:
  assert z.testzip() is None
  for n,v in read('OFFICIAL_COMPUTATION_INDEX.json').items():assert sha(z.read(n))==v['sha256'] and len(z.read(n))==v['bytes']
  for n,v in o['compiledClasses'].items():assert sha(z.read('COMPILED/'+n))==v['sha256']
  for n,h in o['helperSha256'].items():assert sha(z.read(n))==h
print(json.dumps(dict(verified=True,testedSource=S,counts=r['testCounts'],classes=3,exitCode=r['exitCode'],oldPolicyObjectsByteExact=38,allOtherPreexistingTestsConserved=True,baselineWholeFileUnchanged=True,archivesShaCrc=73,currentRecords='155_STALE_UNCHANGED',newRuntimeMethodsByVerifier=0,all48Findings='OPEN',audit='INCOMPLETE',physicalAcceptance='OUTSTANDING_LAST',completeSuite=False)))
