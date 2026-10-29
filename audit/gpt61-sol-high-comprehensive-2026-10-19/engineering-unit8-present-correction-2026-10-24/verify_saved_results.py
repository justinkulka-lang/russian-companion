"""Read-only verification of exact Unit8 core scope, conservation and raw outcomes; no Android tasks."""
from pathlib import Path
from datetime import datetime,timezone
from collections import Counter
import json,subprocess,hashlib,zipfile,xml.etree.ElementTree as ET,re
D=Path(__file__).resolve().parent;R=next(p for p in D.parents if (p/'.git').exists());S='77e3d26eab138796a699d9133e45e4f793e95cc1';sha=lambda b:hashlib.sha256(b).hexdigest();git=lambda *a:subprocess.check_output(['git',*a],cwd=R)
m=json.loads((D/'SHA256.json').read_text());assert set(m)=={p.relative_to(D).as_posix() for p in D.rglob('*') if p.is_file() and p.name!='SHA256.json'}
t=set(git('ls-files').decode().splitlines())
for n,h in m.items():assert str((D/n).relative_to(R)) in t and sha((D/n).read_bytes())==h,n
prep=json.loads((D/'PREPARATION.json').read_text());parent=prep['baselineParent']
for n,h in prep['preexistingTestFiles'].items():assert sha(git('show',S+':'+n))==sha(git('show',parent+':'+n))==h,n
kernel=git('show',S+':app/src/main/java/dev/russiancompanion/v2/curriculum/V2CumulativeReview.kt').decode();objects={m.group(1):sha(m.group(0).encode()) for m in re.finditer(r'^internal object (\w+CumulativeReviewPolicy) \{.*?^}',kernel,re.M|re.S)}
assert len(prep['originalPolicyObjects'])==34 and len(objects)==35
for n,h in prep['originalPolicyObjects'].items():assert objects[n]==h,n
asset='app/src/main/assets/content/v2-curriculum.json';new=git('show',S+':'+asset);old=git('show',parent+':'+asset);a=b'unit8-prior-reading-nominative-agreement-v1';b=b'unit8-prior-present-nominative-agreement-v2';assert old.count(a)==new.count(b)==1 and new.replace(b,a)==old
original=R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit8-present-baseline-2026-10-24/ORIGINAL_BASELINE_TEST.zip'
with zipfile.ZipFile(original) as z:
 name='app/src/test/java/dev/russiancompanion/v2/Unit8PresentReviewBaselineTest.kt';assert z.read(name)==git('show',S+':'+name)
con=json.loads((D/'CONSERVATION.json').read_text())
for n,h in con['currentGeneratedRecordHashes'].items():assert sha(git('show',S+':'+n))==h and sha(git('show',parent+':'+n))==h
assert len(con['currentGeneratedRecordHashes'])==155
for n,h in con['immutablePriorEvidenceHashes'].items():assert sha((R/n).read_bytes())==h,n
for n,h in con['archives'].items():
 assert sha((R/n).read_bytes())==h
 with zipfile.ZipFile(R/n) as z:assert z.testzip() is None
assert len(con['archives'])==69
with zipfile.ZipFile(D/'RAW_CORE.zip') as z:
 assert z.testzip() is None;index=json.loads((D/'RAW_CORE_INDEX.json').read_text());assert set(z.namelist())==set(index)
 for n,v in index.items():assert len(z.read(n))==v['bytes'] and sha(z.read(n))==v['sha256'],n
 scopes={}
 for mode in ['DISCOVERY','REGRESSION']:
  c=json.loads(z.read(mode+'/CONTEXT.json'));s=json.loads(z.read(mode+'/RESULT.json'));assert c['sourceCommit']==s['sourceCommit']==S
  assert c['sourceRootTree']==git('rev-parse',S+'^{tree}').decode().strip();assert c['inputTrees']==s['inputTrees']
  for n,h in c['inputTrees'].items():assert git('rev-parse',S+':'+n).decode().strip()==h
  assert c['selectors']==prep['verificationInventoryNotExecution']['classes'] and sum(c['exclusions'].values())==16 and len(c['exclusions'])==4
  assert s['testCounts']['reported']==12 and s['classes']==3
  assert sha(z.read('run.py'))==c['runnerSha256']
  for n,h in c['initScriptSha256'].items():assert sha(z.read('INIT/'+Path(n).name))==h
  rows={};start=datetime.fromisoformat(c['startedUtc'])
  for n in z.namelist():
   if n.startswith(mode+'/XML/') and n.endswith('.xml'):
    suite=ET.fromstring(z.read(n));stamp=datetime.fromisoformat(suite.get('timestamp').replace('Z','+00:00'));stamp=stamp.replace(tzinfo=timezone.utc) if stamp.tzinfo is None else stamp
    assert stamp.timestamp()>=start.timestamp()-1
    for x in suite.findall('testcase'):
     key=(x.get('classname'),x.get('name'));assert key not in rows
     rows[key]='FAILED' if x.find('failure') is not None else 'ERROR' if x.find('error') is not None else 'SKIPPED' if x.find('skipped') is not None else 'PASSED'
  assert len(rows)==12 and rows=={(x['testClass'],x['method']):x['outcome'] for x in s['tests']}
  events=[json.loads(x) for x in z.read(mode+'/EVENTS.jsonl').splitlines()];events=[x for x in events if x['kind']=='RESULT'];assert len(events)==12
  assert {(x['className'],x['name']):x['resultType'] for x in events}=={k:{'PASSED':'SUCCESS','FAILED':'FAILURE','ERROR':'FAILURE','SKIPPED':'SKIPPED'}[v] for k,v in rows.items()}
  counts=Counter(rows.values());assert s['testCounts']==dict(reported=12,passed=counts['PASSED'],failed=counts['FAILED'],errors=counts['ERROR'],skipped=counts['SKIPPED'])
  end=json.loads(z.read(mode+'_COMPLETION.json'));assert end['runnerExit']==s['exitCode']==int(end['gradleExit'])==int(z.read(mode+'/RUN.exit'))
  for n,h in json.loads(z.read(mode+'/SHA256.json')).items():assert sha(z.read(mode+'/'+n))==h
  scopes[mode]=(rows,s)
 assert set(scopes['DISCOVERY'][0])==set(scopes['REGRESSION'][0]) and scopes['DISCOVERY'][1]['testCounts']==dict(reported=12,passed=0,failed=0,errors=0,skipped=12)
 s=scopes['REGRESSION'][1];assert s['discoveryComparison']['sameInputTrees'] and s['discoveryComparison']['exactSameMethodSet']
 assert 'CompileCommand: exclude androidx/sqlite/driver/SupportSQLiteConnection.prepare bool exclude = true' in z.read('REGRESSION/RUN.log').decode()
print(json.dumps(dict(verified=True,sourceCommit=S,testCounts=s['testCounts'],classes=3,exitCode=s['exitCode'],lint=s['lintIssuesBySeverity'],gateAttachment=s['tasks']['verifyReleaseGateAttachment'],baselineByteExact=True,existingPolicyObjectsConserved=34,preexistingTestFiles=len(prep['preexistingTestFiles']),archiveShaCrc=69,generatedRecords='155 STALE_UNCHANGED',completeSuite=False,testsExecutedByVerifier=0)))
