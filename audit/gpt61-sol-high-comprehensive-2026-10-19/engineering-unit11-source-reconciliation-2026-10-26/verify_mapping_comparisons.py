"""Read-only mapping-scope/identity conservation;executes zero JUnit methods."""
from pathlib import Path
import json,subprocess,hashlib,zipfile,xml.etree.ElementTree as E
D=Path(__file__).resolve().parent;R=next(p for p in D.parents if (p/'.git').exists());g=lambda *a:subprocess.check_output(['git',*a],cwd=R);sha=lambda b:hashlib.sha256(b).hexdigest();rec=json.loads((D/'MAPPING_RECONCILIATION.json').read_text());source=json.loads((D/'MAPPING_APPLIED_COMMIT.json').read_text())['commit'];review=json.loads((D/'MAPPING_FULL_FAILURE_REVIEW.json').read_text());assert len(review['failures'])==2 and review['bothCompleteXmlMessagesReadAndHashMatched'];exceptions=0
with zipfile.ZipFile(D/'MAPPING_ORIGINAL_COMPARISONS.zip') as z:
 assert z.testzip() is None and len(z.namelist())==2
 for row in rec['files']:
  n=row['path'];a=z.read(n);b=g('show',source+':'+n);assert a==g('show',row['sourceCommit']+':'+n);assert sha(a)==row['originalSha256'] and sha(b)==row['appliedSha256'];assert g('rev-parse',row['sourceCommit']+':'+n).decode().strip()==row['gitBlob'];exclude=row['allOriginalNonblankLinesRetainedExcept'];exceptions+=len(exclude);assert all(not x.startswith('assert') for x in exclude);old=[s.strip() for s in a.decode().splitlines() if s.strip() and s.strip() not in exclude];new=iter(s.strip() for s in b.decode().splitlines() if s.strip());assert all(any(t==s for t in new) for s in old),n
assert exceptions==1
for n,h in rec['otherTestHashes'].items():assert sha(g('show',source+':'+n))==h,n
for n,h in rec['currentRecordHashes'].items():assert sha(g('show',source+':'+n))==h,n
for n,h in rec['fixedInputTrees'].items():assert g('rev-parse',source+':'+n).decode().strip()==h,n
with zipfile.ZipFile(D/'ATTEMPT_3/RAW.zip') as z:
 for row in review['failures']:
  b=z.read(row['xmlMember']);assert sha(b)==row['xmlSha256'];s=E.fromstring(b);e=next(x for x in s.findall('testcase') if x.get('name')==row['method']).find('failure');m=e.get('message');assert sha(m.encode())==row['fullMessageSha256'] and len(m.encode())==row['fullMessageBytes'];a,b=m.split(' expected:<',1)[1].rsplit('> but was:<',1);b=b[:-1]
  if 'exactDifference' in row:
   p=row['exactDifference'];assert a.count(p['expectedToken'])==1 and a.replace(p['expectedToken'],p['actualToken'])==b
  else:assert a==row['expectedValue'] and b==row['actualValue']
proof=json.loads((D/'HISTORICAL_COMPARISON_PREPARATION.json').read_text())['rawSourceProof'];n='app/src/main/assets/content/v2-curriculum.json';a=g('show',proof['oldSource']+':'+n);b=g('show',proof['newSource']+':'+n);assert sha(a)==proof['oldSha256'] and sha(b)==proof['newSha256'] and a.count(proof['oldToken'].encode())==1 and a.replace(proof['oldToken'].encode(),proof['newToken'].encode())==b;assert g('rev-parse',source+':app/src/main')==g('rev-parse',proof['newSource']+':app/src/main')
print(json.dumps(dict(verified=True,mappingAppliedSource=source,modifiedFiles=2,allOriginalAssertionsAndPinsRetained=True,oneNonassertHistoricalInputSelectorChanged=True,bothCompleteBroadFailuresVerified=True,wholeUnitFailureOnePolicyTokenOnly=True,genuineRawPackOnlyOnePolicyTokenChanged=True,productionAnd155RecordsAndOtherTestsConserved=True,currentSourceAndDigestControlsAdded=True,all48Findings='OPEN',audit='INCOMPLETE',physicalAcceptance='OUTSTANDING_LAST',completeSuite=False,testsExecutedByVerifier=0)))
