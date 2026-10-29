"""Source-pinned historical verification; original per-attempt checkers remain immutable.
Checks recorded source/root/six trees, not a later HEAD. No test execution.
"""
from pathlib import Path
from collections import Counter
from datetime import datetime,timezone
import json,hashlib,zipfile,subprocess,xml.etree.ElementTree as ET
import sys
D=Path(sys.argv[1]).resolve()
expected_source={'ATTEMPT_1':'cf9415bebd7ea9a4fc20e5847cf4a81e74b67d85','ATTEMPT_2':'a6e06bd5375e033f19d2496a626a506452159949'}[D.name]
R=next(p for p in D.parents if (p/'AGENTS.md').exists())
sha=lambda b:hashlib.sha256(b).hexdigest()
for name,value in json.loads((D/'SHA256.json').read_text()).items():
 assert sha((D/name).read_bytes())==value,name
for name,value in json.loads((D/'PROTECTED_BASELINE.json').read_text()).items():
 assert sha((R/name).read_bytes())==value,name
archives=json.loads((R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit56-conservation-2026-10-23/ORIGINAL_ARCHIVE_CONSERVATION.json').read_text())
assert len(archives)==66
for name,value in archives.items():
 assert sha((R/name).read_bytes())==value,name
 with zipfile.ZipFile(R/name) as z:assert z.testzip() is None,name
with zipfile.ZipFile(D/'RAW_FOLLOWUP.zip') as z:
 assert z.testzip() is None
 ix=json.loads((D/'RAW_FOLLOWUP_INDEX.json').read_text())
 assert sorted(z.namelist())==sorted(x['path'] for x in ix)
 for x in ix:
  assert not x['path'].lower().endswith('.pdf')
  b=z.read(x['path']);assert len(b)==x['bytes'] and sha(b)==x['sha256'],x['path']
 get=lambda n:json.loads(z.read(n))
 c=get('REGRESSION/CONTEXT.json');r=get('REGRESSION/RESULT.json');d=get('DISCOVERY/RESULT.json')
 assert c['sourceCommit']==r['sourceCommit']==expected_source
 assert subprocess.check_output(['git','rev-parse',expected_source+'^{tree}'],cwd=R,text=True).strip()==c['sourceRootTree']
 assert c['inputTrees']==r['inputTrees']==d['inputTrees']
 for p,v in c['inputTrees'].items():assert subprocess.check_output(['git','rev-parse',expected_source+':'+p],cwd=R,text=True).strip()==v,p
 assert sha(z.read('run.py'))==c['runnerSha256']
 for p,v in c['initScriptSha256'].items():assert sha(z.read('INIT/'+Path(p).name))==v,p
 for mode in ['DISCOVERY','REGRESSION']:
  for p,v in get(mode+'/SHA256.json').items():assert sha(z.read(mode+'/'+p))==v,p
 assert not r['syntheticDryRun'] and not r['completeSuite']
 assert r['exitCode']==int(z.read('REGRESSION/RUN.exit'))
 assert int(z.read('REGRESSION_LAUNCHER.exit'))==r['exitCode']
 expected={(t['testClass'],t['method']) for t in d['tests']};assert len(expected)==56 and d['classes']==9
 rows={(t['testClass'],t['method']):t['outcome'] for t in r['tests']};assert len(rows)==len(r['tests']) and set(rows)==expected
 start=datetime.fromisoformat(c['startedUtc']);xml={};classes=0
 for name in z.namelist():
  if not name.startswith('REGRESSION/XML/') or not name.endswith('.xml'):continue
  classes+=1;suite=ET.fromstring(z.read(name));stamp=datetime.fromisoformat(suite.get('timestamp').replace('Z','+00:00'))
  if stamp.tzinfo is None:stamp=stamp.replace(tzinfo=timezone.utc)
  assert stamp.timestamp()>=start.timestamp()-1
  for t in suite.findall('testcase'):
   key=(t.get('classname'),t.get('name'));assert key not in xml
   xml[key]='FAILED' if t.find('failure') is not None else 'ERROR' if t.find('error') is not None else 'SKIPPED' if t.find('skipped') is not None else 'PASSED'
 assert classes==r['classes']==9 and xml==rows
 events=[json.loads(line) for line in z.read('REGRESSION/EVENTS.jsonl').splitlines()]
 outcomes=[e for e in events if e['kind']=='RESULT'];em={(e['className'],e['name']):e['resultType'] for e in outcomes}
 assert len(outcomes)==len(em)==56
 assert em=={k:{'PASSED':'SUCCESS','FAILED':'FAILURE','ERROR':'FAILURE','SKIPPED':'SKIPPED'}[v] for k,v in rows.items()}
 excluded={'VisualReviewCaptureTest':2,'Units35ChunkCaptureTest':2,'Units610ChunkCaptureTest':10,'Units1115GapCaptureTest':2}
 assert c['exclusions']==excluded and sorted(c['selectors'])==sorted(json.loads((D/'FOLLOWUP_PREPARATION.json').read_text())['classes'])
 assert not any(k[0].split('.')[-1] in excluded for k in rows)
 physical=('dev.russiancompanion.acceptance.AcceptanceBoundaryTest','at020_physicalRebootPersistsGovernedRecords')
 assert rows[physical]=='SKIPPED'
 count=Counter(rows.values());assert r['testCounts']==dict(reported=56,passed=count['PASSED'],failed=count['FAILED'],errors=count['ERROR'],skipped=count['SKIPPED'])
 log=z.read('REGRESSION/RUN.log').decode()
 assert 'CompileCommand: exclude androidx/sqlite/driver/SupportSQLiteConnection.prepare bool exclude = true' in log
 assert '-XX:CompileCommand=exclude,androidx/sqlite/driver/SupportSQLiteConnection.prepare' in z.read('INIT/resources.gradle').decode()
 issues=ET.fromstring(z.read('REGRESSION/LINT.xml')).findall('issue')
 lint={s:sum(i.get('severity')==s for i in issues) for s in ['Fatal','Error','Warning','Hint']}
 assert lint==r['lintIssuesBySeverity']
 assert r['tasks']['verifyReleaseGateAttachment']=='> Task :app:verifyReleaseGateAttachment'
 assert r['tasks']['lintDebug']=='> Task :app:lintDebug'
 assert r['discoveryComparison']['exactSameMethodSet'] and r['discoveryComparison']['sameInputTrees']
 baseline=json.loads((R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-host-regression-triage-2026-10-24/FAILED_CLASS_SCOPE.json').read_text())
 old={(x['testClass'],x['method']) for x in baseline['methodIdentities']}
 additions={('dev.russiancompanion.acceptance.ReviewedCurriculumSourceGuardTest','presentItemReferencesAddOnlyFourBindingsAndEachMissingItemStaysBlocked'),('dev.russiancompanion.v2.Unit6CumulativeReviewTest','currentPlacementWithOnlyEarlyPrintTeachingRepairsUnit4BeforeUnit5WithoutCredit')}
 assert old <= expected and expected-old==additions and len(old)==54
 assert r['exitCode']==0 if count['FAILED']==count['ERROR']==0 else r['exitCode']==1
 print(json.dumps(dict(verified=True,sourceCommit=c['sourceCommit'],testCounts=r['testCounts'],classes=classes,exitCode=r['exitCode'],originalArchivesShaCrc=66,conservedFiles=len(json.loads((D/'PROTECTED_BASELINE.json').read_text())),lint=lint,releaseGateAttachment='PASS',experimentalDirective=True,nativeRootCause='UNCONFIRMED',completeSuite=False,testsExecutedByVerifier=0)))
