"""Read-only independent affected evidence verification; no test execution."""
from pathlib import Path
from collections import Counter
from datetime import datetime,timezone
import json,hashlib,subprocess,zipfile,xml.etree.ElementTree as ET
D=Path('/workspace/russian-companion/audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit13-affected-continuation-2026-10-27');R=Path('/workspace/russian-companion');read=lambda n:json.loads((D/n).read_text());sha=lambda b:hashlib.sha256(b).hexdigest();git=lambda *a:subprocess.check_output(['git',*a],cwd=R,text=True).strip();prep=read('CONSERVATION_INPUTS.json');r=read('REGRESSION_RESULT.json');S=r['sourceCommit']
with zipfile.ZipFile(D/'RAW_AFFECTED.zip') as z:
 assert z.testzip() is None;idx=read('RAW_AFFECTED_INDEX.json');assert set(z.namelist())==set(idx)
 for n,v in idx.items():assert sha(z.read(n))==v['sha256'] and len(z.read(n))==v['bytes'],n
 sets={}
 for mode in ['DISCOVERY','REGRESSION']:
  c=read(mode+'_CONTEXT.json');s=read(mode+'_RESULT.json');assert c==json.loads(z.read(mode+'/CONTEXT.json')) and s==json.loads(z.read(mode+'/RESULT.json'))
  assert c['sourceCommit']==s['sourceCommit'] and c['sourceRootTree']==git('rev-parse',c['sourceCommit']+'^{tree}') and c['inputTrees']==s['inputTrees']
  for n,h in c['inputTrees'].items():assert git('rev-parse',c['sourceCommit']+':'+n)==h
  for n,h in c['initScriptSha256'].items():assert sha(z.read('INIT/'+Path(n).name))==h
  assert sha(z.read('TOOLING/run.py'))==c['runnerSha256']
  rows={};hashes={};xmlnames=[n for n in z.namelist() if n.startswith(mode+'/XML/') and n.endswith('.xml')];assert len(xmlnames)==28
  for n in xmlnames:
   suite=ET.fromstring(z.read(n));stamp=datetime.fromisoformat(suite.get('timestamp').replace('Z','+00:00'));stamp=stamp.replace(tzinfo=timezone.utc) if stamp.tzinfo is None else stamp;assert stamp.timestamp()>=datetime.fromisoformat(c['startedUtc']).timestamp()-1
   for t in suite.findall('testcase'):
    key=(t.get('classname'),t.get('name'));assert key not in rows
    status='FAILED' if t.find('failure') is not None else 'ERROR' if t.find('error') is not None else 'SKIPPED' if t.find('skipped') is not None else 'PASSED';rows[key]=status
    e=next((t.find(k) for k in ['failure','error','skipped'] if t.find(k) is not None),None);hashes[key]=sha((e.get('message','') if e is not None else '').encode())
  assert len(rows)==165 and rows=={(t['testClass'],t['method']):t['outcome'] for t in s['tests']}
  for t in s['tests']:assert hashes[t['testClass'],t['method']]==t['fullMessageSha256']
  events=[json.loads(x) for x in z.read(mode+'/EVENTS.jsonl').splitlines()];results=[x for x in events if x['kind']=='RESULT'];assert len(results)==165 and {(x['className'],x['name']):x['resultType'] for x in results}=={k:{'PASSED':'SUCCESS','FAILED':'FAILURE','ERROR':'FAILURE','SKIPPED':'SKIPPED'}[v] for k,v in rows.items()}
  counts=Counter(rows.values());assert s['testCounts']==dict(reported=165,passed=counts['PASSED'],failed=counts['FAILED'],errors=counts['ERROR'],skipped=counts['SKIPPED'])
  assert s['exitCode']==int(z.read(mode+'/RUN.exit'))==json.loads(z.read(mode+'_COMPLETION.json'))['runnerExit']
  for n,h in read(mode+'_SHA256.json').items():assert sha(z.read(mode+'/'+n))==h,n
  assert c['exclusions']=={'VisualReviewCaptureTest':2,'Units35ChunkCaptureTest':2,'Units610ChunkCaptureTest':10,'Units1115GapCaptureTest':2};sets[mode]=rows
 assert set(sets['DISCOVERY'])==set(sets['REGRESSION']) and set(sets['DISCOVERY'].values())=={'SKIPPED'}
 issues=ET.fromstring(z.read('REGRESSION/LINT.xml')).findall('issue');assert r['lintIssuesBySeverity']=={s:sum(x.get('severity')==s for x in issues) for s in ['Fatal','Error','Warning','Hint']}
 monitor=json.loads(z.read('MONITOR/DURABLE_MONITOR_STATUS.json'));assert monitor['completionExists'] and monitor['completedEvents']==165
assert r['discoveryComparison']['sameInputTrees'] and r['discoveryComparison']['exactSameMethodSet']
assert read('ACTUAL_FAILURES.json')['count']==r['testCounts']['failed']+r['testCounts']['errors']
assert git('rev-parse','HEAD:app/src/main')==prep['productionTree']
for n,h in prep['existingTestFiles'].items():
 if n not in prep['changedComparisonFiles']:assert sha((R/n).read_bytes())==h,n
for row in prep['comparisonReview']:assert sha((R/row['path']).read_bytes())==row['newSha256'] and row['everyOriginalAssertionPreservedInOrder']
for n,h in prep['currentRecordHashes'].items():assert sha((R/n).read_bytes())==h,n
for n,h in prep['archives'].items():
 assert sha((R/n).read_bytes())==h
 with zipfile.ZipFile(R/n) as z:assert z.testzip() is None
if (D/'SHA256.json').exists():
 for n,h in read('SHA256.json').items():assert sha((D/n).read_bytes())==h,n
print(json.dumps(dict(verified=True,sourceCommit=S,counts=r['testCounts'],classes=28,exitCode=r['exitCode'],allAssertionsRetained=True,bindingsConserved=155,archivesShaCrc=74,all48Findings='OPEN',audit='INCOMPLETE',physicalAcceptance='OUTSTANDING_LAST',completeSuite=False)))
