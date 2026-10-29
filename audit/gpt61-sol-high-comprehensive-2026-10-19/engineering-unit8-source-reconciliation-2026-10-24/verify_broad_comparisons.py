"""Read-only verification of the two broad historical-comparison repairs; executes zero tests."""
from pathlib import Path
import json,zipfile,hashlib,subprocess,xml.etree.ElementTree as ET
D=Path(__file__).parent;R=Path('/workspace/russian-companion');sha=lambda b:hashlib.sha256(b).hexdigest();git=lambda *a:subprocess.check_output(['git',*a],cwd=R)
old='f6eb548dce2051252f2c680093923384891631aa';new='ce066d6f9557b38ead4f215337028cde43203b8d';rows=json.loads((D/'ORIGINAL_BROAD_COMPARISON_TESTS_INDEX.json').read_text());assert len(rows)==2
with zipfile.ZipFile(D/'ORIGINAL_BROAD_COMPARISON_TESTS.zip') as z:
 assert z.testzip() is None and set(z.namelist())=={x['path'] for x in rows}
 for x in rows:
  p=x['path'];b=z.read(p);a=git('show',new+':'+p);assert b==git('show',old+':'+p);assert git('rev-parse',old+':'+p).decode().strip()==x['gitBlob'];assert sha(b)==x['originalSha256'] and sha(a)==x['preparedSha256'];assert sha((R/p).read_bytes())==x['preparedSha256'];it=iter(s.strip() for s in a.decode().splitlines() if s.strip());assert all(any(s.strip()==v for v in it) for s in b.decode().splitlines() if s.strip())
assert set(git('diff',old,new,'--name-only','--','app/src/test').decode().splitlines())=={x['path'] for x in rows}
assert not git('diff',old,new,'--name-only','--','app/src/main','catalog','docs','review','release').strip()
a=json.loads((D/'ATTEMPT_3_BROAD/REGRESSION_RESULT.json').read_text());b=json.loads((D/'ATTEMPT_4_BROAD_COMPARE_FOLLOWUP/REGRESSION_RESULT.json').read_text());assert a['sourceCommit']==old and b['sourceCommit']==new;assert b['classes']==4 and b['testCounts']==dict(reported=23,passed=23,failed=0,errors=0,skipped=0) and b['exitCode']==0
key=lambda x:(x['testClass'],x['method']);failed={key(x) for x in a['tests'] if x['outcome'] in ['FAILED','ERROR']};passed={key(x) for x in b['tests'] if x['outcome']=='PASSED'};assert len(failed)==2 and failed<=passed
with zipfile.ZipFile(D/'ATTEMPT_3_BROAD/RAW.zip') as z:
 for c,m in failed:
  suite=ET.fromstring(z.read('REGRESSION/XML/TEST-'+c+'.xml'));t=next(t for t in suite.findall('testcase') if t.get('name')==m);message=t.find('failure').get('message');expected,actual=message.split('expected:<',1)[1].split('> but was:<',1);actual=actual[:-1];old_id='unit8-prior-reading-nominative-agreement-v1';new_id='unit8-prior-present-nominative-agreement-v2';assert expected.count(old_id)==actual.count(new_id)==1 and expected.replace(old_id,new_id)==actual
for attempt in ['ATTEMPT_3_BROAD','ATTEMPT_4_BROAD_COMPARE_FOLLOWUP']:
 target=D/attempt;index=json.loads((target/'MONITORING_INDEX.json').read_text());result=json.loads((target/'REGRESSION_RESULT.json').read_text())
 with zipfile.ZipFile(target/'MONITORING.zip') as z:
  assert z.testzip() is None and set(z.namelist())==set(index)
  for n,v in index.items():assert sha(z.read(n))==v['sha256'] and len(z.read(n))==v['bytes']
  monitor=json.loads(z.read('DURABLE_MONITOR_STATUS.json'));assert monitor['completionExists'] and not monitor['newNativeReports'] and monitor['completedEvents']==result['testCounts']['reported']
  assert monitor['partialEvents'].get('SUCCESS',0)==result['testCounts']['passed'] and monitor['partialEvents'].get('FAILURE',0)==result['testCounts']['failed']+result['testCounts']['errors'] and monitor['partialEvents'].get('SKIPPED',0)==result['testCounts']['skipped']
print(json.dumps(dict(verified=True,originalBroadFailedIdentitiesRecheckedPassed=[dict(testClass=c,method=m) for c,m in sorted(failed)],originalNonblankAssertionsAndPinsRetained=True,originalFilesArchivedWithGitBlobSha=True,onlyTwoTestFilesChanged=True,productionAnd155RecordsUnchanged=True,followupCounts=b['testCounts'],classes=b['classes'],scopesNotSummed=True,freshAllPassBroad=False,independentFindingsClosed=0,testsExecutedByVerifier=0)))
