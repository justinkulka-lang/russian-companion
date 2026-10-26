"""Read-only verification of exact preserved assertions and historical source scopes;zero test execution."""
from pathlib import Path
import json,subprocess,hashlib,zipfile
D=Path(__file__).resolve().parent;R=next(p for p in D.parents if (p/'.git').exists());git=lambda *a:subprocess.check_output(['git',*a],cwd=R);sha=lambda b:hashlib.sha256(b).hexdigest();rec=json.loads((D/'COMPARISON_RECONCILIATION.json').read_text());applied=json.loads((D/'COMPARISON_APPLIED_COMMIT.json').read_text())['commit'];prep=json.loads((D/'HISTORICAL_COMPARISON_PREPARATION.json').read_text());review=json.loads((D/'FULL_FAILURE_REVIEW.json').read_text());assert len(review['failures'])==8 and review['allEightCompleteXmlMessagesReadAndHashMatched'];assert sum('exactWholeValueDifference' in x for x in review['failures'])==3
with zipfile.ZipFile(D/'ORIGINAL_COMPARISONS.zip') as z:
 assert z.testzip() is None and len(z.namelist())==5
 for row in rec['modifiedFiles']:
  n=row['path'];a=z.read(n);b=git('show',applied+':'+n);assert a==git('show',row['sourceCommit']+':'+n);assert sha(a)==row['originalSha256'] and sha(b)==row['preparedSha256'];assert git('rev-parse',row['sourceCommit']+':'+n).decode().strip()==row['gitBlob']
  old=[x.strip() for x in a.decode().splitlines() if x.strip() and x.strip() not in row['allOriginalNonblankLinesInOrderExcept']];new=iter(x.strip() for x in b.decode().splitlines() if x.strip());assert all(any(y==x for y in new) for x in old),n
  assert all(not x.lstrip().startswith('assert') for x in row['allOriginalNonblankLinesInOrderExcept'])
for n,h in rec['otherTestHashes'].items():assert sha(git('show',applied+':'+n))==h,n
for n,h in rec['currentRecordHashes'].items():assert sha(git('show',applied+':'+n))==h,n
for n,h in rec['fixedInputTrees'].items():assert git('rev-parse',applied+':'+n).decode().strip()==h,n
proof=prep['rawSourceProof'];path='app/src/main/assets/content/v2-curriculum.json';a=git('show',proof['oldSource']+':'+path);b=git('show',proof['newSource']+':'+path);assert sha(a)==proof['oldSha256'] and sha(b)==proof['newSha256'];assert a.count(proof['oldToken'].encode())==1 and a.replace(proof['oldToken'].encode(),proof['newToken'].encode())==b
pair=prep['genuineReferencePair'];r=git('show',pair['commit']+':'+pair['referencePath']);k=git('show',pair['commit']+':'+pair['kernelPath']);assert sha(r)==pair['referenceSha256'] and sha(k)==pair['kernelSha256'];ref=json.loads(r);kernel=next(x for x in ref['files'] if x['path']=='src/main/java/dev/russiancompanion/v2/curriculum/V2CumulativeReview.kt');assert kernel['sha256']==sha(k) and len(ref['subsequentEngineeringReferenceReviews'])==5
for row in review['failures']:
 with zipfile.ZipFile(D/'ATTEMPT_1/RAW.zip') as z:assert sha(z.read(row['xmlMember']))==row['xmlSha256']
print(json.dumps(dict(verified=True,comparisonAppliedSource=applied,modifiedFiles=5,allOriginalAssertionsAndPinsRetained=True,nonassertInputExpressionExceptions=2,allEightOriginalFailuresPreserved=True,completeFailureValuesRead=True,threeExactOnePolicyTokenDifferences=True,productionAnd155RecordsAndOtherTestsConserved=True,genuineHistoricalReferenceKernelPairVerified=True,currentControlsRetained=True,testsExecutedByVerifier=0,all48Findings='OPEN',audit='INCOMPLETE',physicalAcceptance='OUTSTANDING_LAST')))
