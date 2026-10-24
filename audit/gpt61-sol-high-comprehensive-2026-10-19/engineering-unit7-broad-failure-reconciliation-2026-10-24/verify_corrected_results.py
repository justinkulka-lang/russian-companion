"""Read-only verification of saved focused evidence; executes no Android tests."""
from pathlib import Path
from datetime import datetime,timezone
import json,hashlib,zipfile,subprocess,xml.etree.ElementTree as ET
D=Path(__file__).resolve().parent
R=next(p for p in D.parents if (p/'.git').exists())
S='e5cffa702e39b6e693de4d4ee310ff7a85062b72';B='8d6252ab399616ddae4a1f880add63d14db79038'
sha=lambda b:hashlib.sha256(b).hexdigest()
git=lambda *a:subprocess.check_output(['git',*a],cwd=R)
manifest=json.loads((D/'SHA256.json').read_text());assert set(manifest)=={p.relative_to(D).as_posix() for p in D.rglob('*') if p.is_file() and p.name!='SHA256.json'}
for n,h in manifest.items():assert sha((D/n).read_bytes())==h,n
tracked=set(git('ls-files').decode().splitlines());assert all(str(p.relative_to(R)) in tracked for p in D.rglob('*') if p.is_file())
with zipfile.ZipFile(D/'RAW_CORRECTED.zip') as z:
 assert z.testzip() is None
 index=json.loads((D/'CORRECTED_RAW_MEMBERS.json').read_text());assert set(index)==set(z.namelist())
 for n,h in index.items():assert sha(z.read(n))==h,n
 results={}
 for mode in ['DISCOVERY','REGRESSION']:
  c=json.loads(z.read(mode+'/CONTEXT.json'));s=json.loads(z.read(mode+'/RESULT.json'));assert c['sourceCommit']==s['sourceCommit']==S
  assert c['sourceRootTree']==git('rev-parse',S+'^{tree}').decode().strip()
  for p,h in c['inputTrees'].items():assert git('rev-parse',S+':'+p).decode().strip()==h
  assert s['classes']==1 and s['testCounts']['reported']==9
  assert len(c['selectors'])==1 and len(c['exclusions'])==4 and sum(c['exclusions'].values())==16
  assert all(not any(x in a.lower() for x in ['assemble','install','signing']) for a in c['command'])
  start=datetime.fromisoformat(c['startedUtc']);rows={}
  for n in z.namelist():
   if n.startswith(mode+'/XML/') and n.endswith('.xml'):
    suite=ET.fromstring(z.read(n));t=datetime.fromisoformat(suite.get('timestamp').replace('Z','+00:00'));t=t.replace(tzinfo=timezone.utc) if t.tzinfo is None else t
    assert t.timestamp()>=start.timestamp()-1
    for x in suite.findall('testcase'):
     key=(x.get('classname'),x.get('name'));assert key not in rows
     rows[key]='FAILED' if x.find('failure') is not None else 'ERROR' if x.find('error') is not None else 'SKIPPED' if x.find('skipped') is not None else 'PASSED'
  events=[json.loads(x) for x in z.read(mode+'/EVENTS.jsonl').decode().splitlines()];events=[x for x in events if x['kind']=='RESULT'];assert len(events)==len(rows)==9
  actual={(x['testClass'],x['method']):x['outcome'] for x in s['tests']};assert actual==rows
  assert {(x['className'],x['name']):x['resultType'] for x in events}=={k:{'PASSED':'SUCCESS','FAILED':'FAILURE','ERROR':'FAILURE','SKIPPED':'SKIPPED'}[v] for k,v in rows.items()}
  assert s['testCounts']==dict(reported=9,passed=list(rows.values()).count('PASSED'),failed=list(rows.values()).count('FAILED'),errors=list(rows.values()).count('ERROR'),skipped=list(rows.values()).count('SKIPPED'))
  for n,h in json.loads(z.read(mode+'/SHA256.json')).items():assert sha(z.read(mode+'/'+n))==h
  results[mode]=(rows,s)
 assert results['DISCOVERY'][1]['testCounts']['skipped']==9
 assert set(results['DISCOVERY'][0])==set(results['REGRESSION'][0])
 assert results['REGRESSION'][1]['discoveryComparison']['sameInputTrees']
 for n,h in json.loads(z.read('REGRESSION/CONTEXT.json'))['initScriptSha256'].items():
  if n.endswith('/resources.gradle'):assert sha((D/'resources.gradle').read_bytes())==h
with zipfile.ZipFile(D/'ORIGINAL_TESTS.zip') as z:
 for f in z.namelist():
  assert z.read(f)==git('show',B+':'+f)
  old=[x.strip() for x in z.read(f).decode().splitlines() if x.strip()];current=iter(x.strip() for x in git('show',S+':'+f).decode().splitlines() if x.strip());assert all(any(y==x for y in current) for x in old)
for p in ['app/src/main','catalog','docs','review','release']:assert git('rev-parse',B+':'+p)==git('rev-parse',S+':'+p)
records=json.loads((D/'CURRENT_RECORD_INPUTS.json').read_text())['files'];assert len(records)==155
for n,h in records.items():assert sha(git('show',S+':'+n))==h
prior=R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit7-integrated-broad-2026-10-24/verify_saved_broad.py'
p=json.loads(subprocess.check_output(['python3',str(prior)],cwd=R,text=True));assert p['verified'] and p['originalArchivesShaCrc']==66
with zipfile.ZipFile(D/'INITIAL_FAILED_TEST_SOURCE.zip') as z:
 for f in z.namelist():
  current=iter(x.strip() for x in git('show',S+':'+f).decode().splitlines() if x.strip())
  assert all(any(y==x.strip() for y in current) for x in z.read(f).decode().splitlines() if x.strip())
s=results['REGRESSION'][1]
print(json.dumps(dict(verified=True,sourceCommit=S,testCounts=s['testCounts'],classes=1,exitCode=s['exitCode'],lint=s['lintIssuesBySeverity'],attachment=s['tasks']['verifyReleaseGateAttachment'],originalBroadRemains=p['testCounts'],originalAssertionLinesRetained=True,generatedRecords=155,originalArchivesShaCrc=66,completeSuite=False,testsExecutedByVerifier=0)))
