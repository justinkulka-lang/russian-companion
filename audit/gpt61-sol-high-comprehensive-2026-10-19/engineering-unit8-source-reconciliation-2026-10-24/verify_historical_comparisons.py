from pathlib import Path
import json,subprocess,hashlib,zipfile
R=Path('/workspace/russian-companion');D=Path(__file__).parent
a=json.loads((D/'ATTEMPT_1/REGRESSION_RESULT.json').read_text());b=json.loads((D/'ATTEMPT_2/REGRESSION_RESULT.json').read_text());assert b['testCounts']==dict(reported=50,passed=50,failed=0,errors=0,skipped=0) and b['exitCode']==0
assert b['lintIssuesBySeverity']==a['lintIssuesBySeverity']=={'Fatal':0,'Error':0,'Warning':20,'Hint':1}
assert b['tasks']['verifyReleaseGateAttachment']==a['tasks']['verifyReleaseGateAttachment']=='> Task :app:verifyReleaseGateAttachment'
key=lambda x:(x['testClass'],x['method']);failed={key(x) for x in a['tests'] if x['outcome'] in ['FAILED','ERROR']};passed={key(x) for x in b['tests'] if x['outcome']=='PASSED'};assert len(failed)==6 and failed<=passed
m=json.loads((D/'ASSERTION_CONSERVATION.json').read_text());proof=[]
with zipfile.ZipFile(D/'ORIGINAL_HISTORICAL_COMPARISON_TESTS.zip') as z:
 for p,v in m.items():
  old=z.read(p);new=(R/p).read_bytes();assert hashlib.sha256(old).hexdigest()==v['originalSha256'];assert hashlib.sha256(new).hexdigest()==v['preparedSha256'];it=iter(x.strip() for x in new.decode().splitlines() if x.strip());assert all(any(x.strip()==y for y in it) for x in old.decode().splitlines() if x.strip());proof.append(dict(path=p,allOriginalNonblankLinesRetainedInOrderIgnoringIndentation=True,originalSha256=v['originalSha256'],testedSha256=hashlib.sha256(new).hexdigest()))
changed=subprocess.check_output(['git','diff','8a5f6442eab1aa8194edd0694b7b82dcffa9ac61',b['sourceCommit'],'--name-only','--','app/src/test'],cwd=R,text=True).splitlines();assert set(changed)==set(m)
assert not subprocess.check_output(['git','diff','8a5f6442eab1aa8194edd0694b7b82dcffa9ac61',b['sourceCommit'],'--name-only','--','app/src/main','catalog','docs','review','release'],cwd=R,text=True).strip()
print(json.dumps(dict(verified=True,originalFailureIdentitiesRecheckedPassed=len(failed),originalTestFilesConserved=len(proof),productionAnd155RecordsUnchanged=True,testsExecutedByVerifier=0)))
