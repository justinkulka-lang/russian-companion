"""Read-only verification of one experimental selected-host run; executes no tests."""
from pathlib import Path
from collections import Counter
from datetime import datetime,timezone
import json,hashlib,zipfile,subprocess,xml.etree.ElementTree as ET
D=Path(__file__).resolve().parent
R=next(p for p in D.parents if (p/'AGENTS.md').exists())
sha=lambda b:hashlib.sha256(b).hexdigest()
for name,value in json.loads((D/'SHA256.json').read_text()).items():
 assert sha((D/name).read_bytes())==value,name
for name,value in json.loads((D/'CONSERVED_INPUTS.json').read_text()).items():
 assert sha((R/name).read_bytes())==value,name
archives=json.loads((R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit56-conservation-2026-10-23/ORIGINAL_ARCHIVE_CONSERVATION.json').read_text())
assert len(archives)==66
for name,value in archives.items():
 assert sha((R/name).read_bytes())==value,name
 with zipfile.ZipFile(R/name) as z:assert z.testzip() is None,name
with zipfile.ZipFile(D/'RAW_BROAD.zip') as z:
 assert z.testzip() is None
 ix=json.loads((D/'RAW_BROAD_INDEX.json').read_text())
 assert sorted(z.namelist())==sorted(x['path'] for x in ix)
 for x in ix:
  assert not x['path'].lower().endswith('.pdf')
  b=z.read(x['path']);assert len(b)==x['bytes'] and sha(b)==x['sha256'],x['path']
 get=lambda n:json.loads(z.read(n))
 c=get('REGRESSION/CONTEXT.json');r=get('REGRESSION/RESULT.json');d=get('DISCOVERY/RESULT.json')
 assert c['sourceCommit']==r['sourceCommit']=='d70675321a043688cf270963ab1e84d99a77c193'
 assert c['inputTrees']==r['inputTrees']==d['inputTrees']
 assert d['sourceCommit']=='a6e06bd5375e033f19d2496a626a506452159949'
 assert d['syntheticDryRun'] and d['testCounts']==dict(reported=1365,passed=0,failed=0,errors=0,skipped=1365)
 completion=get('REGRESSION_COMPLETION.json')
 assert completion['sourceCommit']==c['sourceCommit'] and completion['resultExists'] and completion['manifestExists']
 assert completion['runnerExit']==r['exitCode']==int(completion['gradleExit'])
 assert subprocess.check_output(['git','rev-parse',c['sourceCommit']+'^{tree}'],cwd=R,text=True).strip()==c['sourceRootTree']
 for path,value in c['inputTrees'].items():assert subprocess.check_output(['git','rev-parse',c['sourceCommit']+':'+path],cwd=R,text=True).strip()==value
 for p,v in c['inputTrees'].items():assert subprocess.check_output(['git','rev-parse','HEAD:'+p],cwd=R,text=True).strip()==v,p
 assert sha(z.read('run.py'))==c['runnerSha256']
 for p,v in c['initScriptSha256'].items():assert sha(z.read('INIT/'+Path(p).name))==v,p
 for mode in ['DISCOVERY','REGRESSION']:
  for p,v in get(mode+'/SHA256.json').items():assert sha(z.read(mode+'/'+p))==v,p
 assert not r['syntheticDryRun'] and not r['completeSuite']
 assert c['apkAssemblyInstallSigning']=='NOT_RUN' and c['physicalAcceptance']=='OUTSTANDING_LAST'
 assert all('--tests'!=x and '--test-dry-run'!=x for x in c['command'])
 assert r['selectedFreshXmlAndEventsMatch'] and r['eventOutcomeCountsMatchXml'] and r['allFreshSelectedMethodsReported']
 assert r['exitCode']==int(z.read('REGRESSION/RUN.exit'))
 assert int(z.read('REGRESSION_LAUNCHER.exit'))==r['exitCode']
 expected={(t['testClass'],t['method']) for t in d['tests']};assert len(expected)==1365 and d['classes']==229
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
 assert classes==r['classes']==229 and xml==rows
 events=[json.loads(line) for line in z.read('REGRESSION/EVENTS.jsonl').splitlines()]
 outcomes=[e for e in events if e['kind']=='RESULT'];em={(e['className'],e['name']):e['resultType'] for e in outcomes}
 assert len(outcomes)==len(em)==1365
 assert em=={k:{'PASSED':'SUCCESS','FAILED':'FAILURE','ERROR':'FAILURE','SKIPPED':'SKIPPED'}[v] for k,v in rows.items()}
 excluded={'VisualReviewCaptureTest':2,'Units35ChunkCaptureTest':2,'Units610ChunkCaptureTest':10,'Units1115GapCaptureTest':2}
 assert c['exclusions']==excluded and c['selectors']==[]
 assert not any(k[0].split('.')[-1] in excluded for k in rows)
 physical=('dev.russiancompanion.acceptance.AcceptanceBoundaryTest','at020_physicalRebootPersistsGovernedRecords')
 assert rows[physical]=='SKIPPED'
 count=Counter(rows.values());assert r['testCounts']==dict(reported=1365,passed=count['PASSED'],failed=count['FAILED'],errors=count['ERROR'],skipped=count['SKIPPED'])
 log=z.read('REGRESSION/RUN.log').decode()
 assert 'CompileCommand: exclude androidx/sqlite/driver/SupportSQLiteConnection.prepare bool exclude = true' in log
 assert '-XX:CompileCommand=exclude,androidx/sqlite/driver/SupportSQLiteConnection.prepare' in z.read('INIT/resources.gradle').decode()
 issues=ET.fromstring(z.read('REGRESSION/LINT.xml')).findall('issue')
 lint={s:sum(i.get('severity')==s for i in issues) for s in ['Fatal','Error','Warning','Hint']}
 assert lint==r['lintIssuesBySeverity']
 assert r['tasks']['verifyReleaseGateAttachment']=='> Task :app:verifyReleaseGateAttachment'
 assert r['tasks']['lintDebug']=='> Task :app:lintDebug'
 assert r['discoveryComparison']['exactSameMethodSet'] and r['discoveryComparison']['sameInputTrees']
 print(json.dumps(dict(verified=True,sourceCommit=c['sourceCommit'],testCounts=r['testCounts'],classes=classes,exitCode=r['exitCode'],originalArchivesShaCrc=66,conservedFiles=len(json.loads((D/'CONSERVED_INPUTS.json').read_text())),lint=lint,releaseGateAttachment='PASS',experimentalDirective=True,nativeRootCause='UNCONFIRMED',completeSuite=False,testsExecutedByVerifier=0)))
