"""Read-only archive/discovery identity verification; zero runtime test executions."""
from pathlib import Path
from datetime import datetime,timezone
import json,zipfile,hashlib,subprocess,xml.etree.ElementTree as E
A=Path(__file__).resolve().parent;R=next(p for p in A.parents if (p/'.git').exists());sha=lambda b:hashlib.sha256(b).hexdigest();git=lambda *a:subprocess.check_output(['git',*a],cwd=R,text=True).strip()
with zipfile.ZipFile(A/'RAW.zip') as z:
 assert z.testzip() is None
 index=json.loads((A/'RAW_INDEX.json').read_text());assert set(index)==set(z.namelist())
 for n,v in index.items():assert len(z.read(n))==v['bytes'] and sha(z.read(n))==v['sha256']
 c=json.loads(z.read('DISCOVERY/CONTEXT.json'));s=json.loads(z.read('DISCOVERY/RESULT.json'));source=c['sourceCommit'];assert source==s['sourceCommit'] and c['sourceRootTree']==git('rev-parse',source+'^{tree}')
 for n,h in c['inputTrees'].items():assert git('rev-parse',source+':'+n)==h
 assert sha(z.read('run.py'))==c['runnerSha256']
 for n,h in c['initScriptSha256'].items():assert sha(z.read('INIT/'+Path(n).name))==h
 for n,h in json.loads(z.read('DISCOVERY/SHA256.json')).items():assert sha(z.read('DISCOVERY/'+n))==h
 assert s['testCounts']==dict(reported=1458,passed=0,failed=0,errors=0,skipped=1458) and s['classes']==253 and s['exitCode']==int(z.read('DISCOVERY/RUN.exit'))==0
 rows=set();classes=set();start=datetime.fromisoformat(c['startedUtc'])
 for n in z.namelist():
  if not n.startswith('DISCOVERY/XML/') or not n.endswith('.xml'):continue
  suite=E.fromstring(z.read(n));classes.add(suite.get('name'));t=datetime.fromisoformat(suite.get('timestamp').replace('Z','+00:00'));t=t.replace(tzinfo=timezone.utc) if t.tzinfo is None else t;assert t.timestamp()>=start.timestamp()-1
  for t in suite.findall('testcase'):
   key=(t.get('classname'),t.get('name'));assert key not in rows and t.find('skipped') is not None and t.find('failure') is None and t.find('error') is None;rows.add(key)
 events=[json.loads(l) for l in z.read('DISCOVERY/EVENTS.jsonl').splitlines()];results=[e for e in events if e['kind']=='RESULT'];assert len(results)==len(rows)==1458 and len(classes)==253
 assert {(e['className'],e['name']) for e in results}==rows and all(e['resultType']=='SKIPPED' for e in results)
 assert rows=={(t['testClass'],t['method']) for t in s['tests']} and all(t['outcome']=='SKIPPED' for t in s['tests'])
 old=json.loads(z.read('PRIOR_BROAD_RESULT.json'));prior={(t['testClass'],t['method']) for t in old['tests']};assert len(prior)==1441 and prior<=rows and len(rows-prior)==17
 delta=json.loads((A/'IDENTITY_DELTA.json').read_text());assert {(t['testClass'],t['method']) for t in delta['added']}==rows-prior and not delta['removed'] and sha(z.read('PRIOR_BROAD_RESULT.json'))==delta['priorBroadResultSha256']
 assert c['exclusions']=={'VisualReviewCaptureTest':2,'Units35ChunkCaptureTest':2,'Units610ChunkCaptureTest':10,'Units1115GapCaptureTest':2} and not any(k[0].split('.')[-1] in c['exclusions'] for k in rows)
 done=json.loads(z.read('DISCOVERY_COMPLETION.json'));assert done['runnerExit']==int(done['gradleExit'])==0
 for n in ['CONTEXT.json','RESULT.json','RESULTS.csv','RUN.exit','SHA256.json']:assert (A/n).read_bytes()==z.read('DISCOVERY/'+n)
print(json.dumps(dict(verified=True,sourceCommit=source,syntheticSkips=1458,classes=253,all1441EarlierMethodsPreserved=True,added=17,removed=0,runtimeTestsExecuted=0,broadRuntime='NOT_EVIDENCED_BY_DISCOVERY')))
