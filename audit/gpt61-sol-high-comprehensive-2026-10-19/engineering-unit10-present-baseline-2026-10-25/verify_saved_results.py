"""Read-only actual Unit10 baseline/capture verification; executes zero tests."""
from pathlib import Path
from datetime import datetime,timezone
import subprocess,json,hashlib,zipfile,xml.etree.ElementTree as ET
D=Path(__file__).resolve().parent;R=Path(subprocess.check_output(['git','rev-parse','--show-toplevel'],cwd=D,text=True).strip());sha=lambda b:hashlib.sha256(b).hexdigest();read=lambda n:json.loads((D/n).read_text());blob=lambda s,n:subprocess.check_output(['git','show',s+':'+n],cwd=R)
manifest=read('SHA256.json');assert set(manifest)=={p.relative_to(D).as_posix() for p in D.rglob('*') if p.is_file() and p.name!='SHA256.json'};tracked=set(subprocess.check_output(['git','ls-files'],cwd=R,text=True).splitlines())
for n,h in manifest.items():assert sha((D/n).read_bytes())==h and (D/n).relative_to(R).as_posix() in tracked
r=read('REGRESSION_RESULT.json');d=read('DISCOVERY_RESULT.json');identity=read('CAPTURE_IDENTITY.json');S=identity['producerCommit'];prep=read('PREPARATION.json');con=read('CONSERVATION.json');assert r['sourceCommit']==d['sourceCommit']==S;assert subprocess.check_output(['git','rev-parse',S+'^'],cwd=R,text=True).strip()==prep['baselineParent']
assert r['testCounts']==dict(reported=2,passed=1,failed=1,errors=0,skipped=0) and r['classes']==1 and r['exitCode']==1;assert d['testCounts']==dict(reported=2,passed=0,failed=0,errors=0,skipped=2) and d['exitCode']==0
assert {x['method']:x['outcome'] for x in r['tests']}=={'printAndNumeralAnswersCannotReplaceFreshAssignedPresentAnswer':'FAILED','actualCurrentPartialTaskRoundtripsReopensAndCompletesItsOwnObligation':'PASSED'};assert r['lintIssuesBySeverity']==dict(Fatal=0,Error=0,Warning=20,Hint=1);assert r['tasks']['verifyReleaseGateAttachment']=='> Task :app:verifyReleaseGateAttachment';assert r['discoveryComparison']['sameInputTrees'] and r['discoveryComparison']['exactSameMethodSet'];assert r['inputTrees']==d['inputTrees']==con['inputTrees']
for n,h in prep['preexistingTestFiles'].items():assert sha(blob(S,n))==h
for n,h in prep['unchangedTrees'].items():assert subprocess.check_output(['git','rev-parse',S+':'+n],cwd=R,text=True).strip()==h
assert sha(blob(S,prep['newTestFile']))==prep['newTestSha256']
with zipfile.ZipFile(D/'RAW_BASELINE.zip') as z:
 assert z.testzip() is None;index=read('RAW_BASELINE_INDEX.json');assert set(z.namelist())=={x['path'] for x in index}
 for x in index:assert len(z.read(x['path']))==x['bytes'] and sha(z.read(x['path']))==x['sha256']
 for mode in ['DISCOVERY','REGRESSION']:
  c=json.loads(z.read(mode+'/CONTEXT.json'));s=json.loads(z.read(mode+'/RESULT.json'));assert c['sourceCommit']==s['sourceCommit']==S and c['inputTrees']==s['inputTrees'];assert c['sourceRootTree']==subprocess.check_output(['git','rev-parse',S+'^{tree}'],cwd=R,text=True).strip();assert all(subprocess.check_output(['git','rev-parse',S+':'+n],cwd=R,text=True).strip()==h for n,h in c['inputTrees'].items());assert c['exclusions']==prep['exclusions'] and c['selectors']==['dev.russiancompanion.v2.Unit10PresentReviewBaselineTest'];assert sha(z.read('run.py'))==c['runnerSha256']
  for n,h in c['initScriptSha256'].items():assert sha(z.read('INIT/'+Path(n).name))==h
  done=json.loads(z.read(mode+'_COMPLETION.json'));assert done['runnerExit']==s['exitCode']==int(done['gradleExit'])==int(z.read(mode+'/RUN.exit'));suites=[n for n in z.namelist() if n.startswith(mode+'/XML/') and n.endswith('.xml')];assert len(suites)==1;suite=ET.fromstring(z.read(suites[0]));stamp=datetime.fromisoformat(suite.get('timestamp').replace('Z','+00:00'));stamp=stamp.replace(tzinfo=timezone.utc) if stamp.tzinfo is None else stamp;assert stamp.timestamp()>=datetime.fromisoformat(c['startedUtc']).timestamp()-1
  rows={}
  for t in suite.findall('testcase'):
   k=(t.get('classname'),t.get('name'));assert k not in rows;rows[k]='FAILED' if t.find('failure') is not None else 'ERROR' if t.find('error') is not None else 'SKIPPED' if t.find('skipped') is not None else 'PASSED'
  events=[json.loads(line) for line in z.read(mode+'/EVENTS.jsonl').splitlines()];events=[e for e in events if e['kind']=='RESULT'];assert len(events)==len(rows)==2;assert rows=={(t['testClass'],t['method']):t['outcome'] for t in s['tests']};assert {(e['className'],e['name']):e['resultType'] for e in events}=={k:{'PASSED':'SUCCESS','FAILED':'FAILURE','ERROR':'FAILURE','SKIPPED':'SKIPPED'}[v] for k,v in rows.items()}
  for n,h in json.loads(z.read(mode+'/SHA256.json')).items():
   data=(D/'BASELINE_CURRENT_UNIT10_V1.zip').read_bytes() if n=='CAPTURE/PARTIAL_UNIT10_V1.zip' else (D/'CAPTURE_IDENTITY.json').read_bytes() if n=='CAPTURE/IDENTITY.json' else z.read(mode+'/'+n);assert sha(data)==h
 xml=ET.fromstring(z.read('REGRESSION/XML/TEST-dev.russiancompanion.v2.Unit10PresentReviewBaselineTest.xml'));assert 'actualAdvanceWithoutPresent=true' in xml.findtext('system-out','');failures=xml.findall('testcase/failure');assert len(failures)==1 and 'proposed Unit10 obligation must refuse actual advancement' in failures[0].get('message');original_document=z.read('REGRESSION/CAPTURE/PARTIAL_UNIT10_V1.json');assert sha(original_document)==identity['documentSHA256']
 assert '-XX:CompileCommand=exclude,androidx/sqlite/driver/SupportSQLiteConnection.prepare' in z.read('INIT/resources.gradle').decode()
archive=D/'BASELINE_CURRENT_UNIT10_V1.zip';assert sha(archive.read_bytes())==identity['zipSHA256'] and archive.stat().st_size==identity['zipBytes']
with zipfile.ZipFile(archive) as z:
 assert z.testzip() is None and sorted(z.namelist())==['learner.json','manifest.json'];doc=json.loads(z.read('learner.json'));m=json.loads(z.read('manifest.json'));assert doc==json.loads(original_document);assert m['formatVersion']==doc['schemaVersion']==identity['documentSchemaVersion']==3 and m['applicationId']=='dev.russiancompanion' and m['recordingCount']==identity['recordingCount']==0;assert len(m['files'])==1 and m['files'][0]['path']=='learner.json' and m['files'][0]['sha256']==sha(z.read('learner.json')) and m['files'][0]['bytes']==len(z.read('learner.json'))
task=next(x for x in doc['reviewAssignments'] if x['targetUnit']==10);assert task['policyId']==identity['policyId']=='unit10-prior-reading-numeral-gender-v1' and task['schema']==identity['schema']==1;assert task['policyDigest']==identity['policyDigest'] and task['digest']==identity['assignmentDigest'];assert len(task['slots'])==2 and [s['objectiveId'] for s in task['slots']]==['obj-u1-decoding','obj-u9-odindva'] and [s['ownerUnit'] for s in task['slots']]==[1,9];answers=[a for a in doc['reviewAnswers'] if a['assignmentId']==task['id']];assert len(answers)==1 and answers[0]['slotIndex']==0 and answers[0]['acceptedOrder']>task['boundary'] and answers[0]['assignmentDigest']==task['digest'];assert not doc['recordings'] and not any(c['unitNumber']==10 for c in doc['unitCompletions']);assert sorted(c['unitNumber'] for c in doc['unitCompletions'])==list(range(1,10));assert any(a['targetUnit']==8 and a['schema']==10 and a['policyId']=='unit8-prior-present-nominative-agreement-v2' for a in doc['reviewAssignments'])
assert any(a['targetUnit']==9 and a['schema']==11 and a['policyId']=='unit9-prior-noun-behavior-plural-gender-v3' for a in doc['reviewAssignments'])
assert con['priorArchiveCount']==len(con['priorArchives'])==70
for n,h in con['priorArchives'].items():
 assert sha((R/n).read_bytes())==h
 with zipfile.ZipFile(R/n) as z:assert z.testzip() is None
records=read('CURRENT_RECORD_HASHES.json');assert len(records)==155
for n,h in records.items():assert sha(blob(S,n))==h
with zipfile.ZipFile(D/'ORIGINAL_BASELINE_TEST.zip') as z:assert z.read(prep['newTestFile'])==blob(S,prep['newTestFile'])
assert sha(blob(S,'app/src/main/assets/content/v2-curriculum.json'))==identity['rawCurriculumSHA256'];assert con['canonicalSourceDigest']=='64876d1cbd09b189123ba09dd8af59af5245f8902aba2738fc392d5ff85bf77f'
print(json.dumps(dict(verifiedBaseline=S,reported=2,passed=1,intendedFailed=1,currentDeclaredGateBypassed=False,ordinaryPresentKnowledgeBypassed=False,priorArchivesConserved=70,newGenuineUnit10V1Capture=identity['zipSHA256'],captureBytes=identity['zipBytes'],assignmentSchema=1,backupFormatVersion=3,recordings=0,newRuntimeMethodsExecutedByVerifier=0)))
