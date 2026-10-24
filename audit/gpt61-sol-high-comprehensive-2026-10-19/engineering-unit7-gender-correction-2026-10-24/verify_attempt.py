"""Read-only exact source/fresh evidence validation for one recorded Unit7core attempt."""
from pathlib import Path
from datetime import datetime,timezone
from collections import Counter
import json,hashlib,zipfile,subprocess,xml.etree.ElementTree as ET,sys
D=Path(sys.argv[1]).resolve();R=next(p for p in D.parents if (p/'AGENTS.md').exists());sha=lambda b:hashlib.sha256(b).hexdigest()
for n,v in json.loads((D/'SHA256.json').read_text()).items():assert sha((D/n).read_bytes())==v,n
with zipfile.ZipFile(D/'RAW.zip') as z:
 assert z.testzip() is None
 ix=json.loads((D/'RAW_INDEX.json').read_text());assert sorted(z.namelist())==sorted(v['path'] for v in ix)
 for v in ix:
  b=z.read(v['path']);assert len(b)==v['bytes'] and sha(b)==v['sha256'],v['path'];assert not v['path'].lower().endswith('.pdf')
 read=lambda n:json.loads(z.read(n));runtime=read('REGRESSION/RESULT.json');discovery=read('DISCOVERY/RESULT.json');source=runtime['sourceCommit'];names=[]
 for mode in ['DISCOVERY','REGRESSION']:
  r=read(mode+'/RESULT.json');c=read(mode+'/CONTEXT.json');start=datetime.fromisoformat(c['startedUtc']);assert r['sourceCommit']==c['sourceCommit']==source;assert r['inputTrees']==c['inputTrees']==runtime['inputTrees']==discovery['inputTrees']
  assert subprocess.check_output(['git','rev-parse',source+'^{tree}'],cwd=R,text=True).strip()==c['sourceRootTree']
  for p,v in c['inputTrees'].items():assert subprocess.check_output(['git','rev-parse',source+':'+p],cwd=R,text=True).strip()==v
  assert sha(z.read('run.py'))==c['runnerSha256']
  for p,v in c['initScriptSha256'].items():assert sha(z.read('INIT/'+Path(p).name))==v
  for p,v in read(mode+'/SHA256.json').items():assert sha(z.read(mode+'/'+p))==v,p
  done=read(mode+'_COMPLETION.json');assert done['sourceCommit']==source and done['resultExists'] and done['manifestExists'];assert done['runnerExit']==r['exitCode']==int(z.read(mode+'/RUN.exit'))==int(done['gradleExit'])
  assert c['exclusions']==dict(VisualReviewCaptureTest=2,Units35ChunkCaptureTest=2,Units610ChunkCaptureTest=10,Units1115GapCaptureTest=2)
  expected={'dev.russiancompanion.v2.'+s for s in ['Unit7GenderReviewBaselineTest','Unit7GenderCompatibilityTest','Unit7GenderReviewUiTest']};assert set(c['selectors'])==expected
  suites=[n for n in z.namelist() if n.startswith(mode+'/XML/') and n.endswith('.xml')];assert len(suites)==r['classes']==3;rows={}
  for n in suites:
   suite=ET.fromstring(z.read(n));stamp=datetime.fromisoformat(suite.get('timestamp').replace('Z','+00:00'))
   if stamp.tzinfo is None:stamp=stamp.replace(tzinfo=timezone.utc)
   assert stamp.timestamp()>=start.timestamp()-1
   for t in suite.findall('testcase'):
    key=(t.get('classname'),t.get('name'));assert key not in rows
    rows[key]='FAILED' if t.find('failure') is not None else 'ERROR' if t.find('error') is not None else 'SKIPPED' if t.find('skipped') is not None else 'PASSED'
  assert rows=={(t['testClass'],t['method']):t['outcome'] for t in r['tests']} and len(rows)==len(r['tests'])==11
  assert {k[0] for k in rows}==expected
  ev=[json.loads(l) for l in z.read(mode+'/EVENTS.jsonl').splitlines()];ev=[e for e in ev if e['kind']=='RESULT'];events={(e['className'],e['name']):e['resultType'] for e in ev};assert len(events)==len(ev)==11
  assert events=={k:{'PASSED':'SUCCESS','FAILED':'FAILURE','ERROR':'FAILURE','SKIPPED':'SKIPPED'}[v] for k,v in rows.items()}
  ct=Counter(rows.values());assert r['testCounts']==dict(reported=11,passed=ct['PASSED'],failed=ct['FAILED'],errors=ct['ERROR'],skipped=ct['SKIPPED'])
  names.append(set(rows))
 assert names[0]==names[1] and discovery['testCounts']==dict(reported=11,passed=0,failed=0,errors=0,skipped=11)
 assert not runtime['completeSuite'] and runtime['discoveryComparison']['exactSameMethodSet'] and runtime['discoveryComparison']['sameInputTrees']
 assert runtime['tasks']['verifyReleaseGateAttachment']=='> Task :app:verifyReleaseGateAttachment' and runtime['tasks']['lintDebug']=='> Task :app:lintDebug'
 issues=ET.fromstring(z.read('REGRESSION/LINT.xml')).findall('issue');assert runtime['lintIssuesBySeverity']=={s:sum(i.get('severity')==s for i in issues) for s in ['Fatal','Error','Warning','Hint']}
 assert 'CompileCommand: exclude androidx/sqlite/driver/SupportSQLiteConnection.prepare bool exclude = true' in z.read('REGRESSION/RUN.log').decode()
 assert '-XX:CompileCommand=exclude,androidx/sqlite/driver/SupportSQLiteConnection.prepare' in z.read('INIT/resources.gradle').decode()
 print(json.dumps(dict(verified=True,sourceCommit=source,counts=runtime['testCounts'],exitCode=runtime['exitCode'],classes=3,lint=runtime['lintIssuesBySeverity'],releaseGateAttachment='PASS',additionalJUnitMethodsExecuted=0,completeSuite=False)))
