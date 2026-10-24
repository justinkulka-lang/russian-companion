"""Read-only Unit7binding/replay/observed-failure reconciliation; no tests or generation."""
from pathlib import Path
from collections import Counter
import json,hashlib,zipfile,subprocess
D=Path(__file__).resolve().parent;R=next(p for p in D.parents if (p/'AGENTS.md').exists());sha=lambda b:hashlib.sha256(b).hexdigest();read=lambda n:json.loads((D/n).read_text());git=lambda *a:subprocess.check_output(['git',*a],cwd=R)
tracked=set(git('ls-files').decode().splitlines())
for n,v in read('SHA256.json').items():
 assert sha((D/n).read_bytes())==v,n
 assert (D/n).relative_to(R).as_posix() in tracked,('Untracked evidence',n)
run=lambda script,args=[]:subprocess.run(['python3',str(D/script),*args],cwd=R,text=True,stdout=subprocess.PIPE,stderr=subprocess.PIPE)
p=run('verify_generation.py');assert p.returncode==0,p.stderr;generation=json.loads(p.stdout)
expected={'ATTEMPT_1':('531fa3e9018d8165b10720126f41133dafe4f1cf',dict(reported=124,passed=121,failed=2,errors=0,skipped=1)),'ATTEMPT_2':('3bf0a0dd01fc45d2617b2ea01986f4f86c14cb24',dict(reported=14,passed=14,failed=0,errors=0,skipped=0))};summaries={}
for n,(source,counts) in expected.items():
 p=run('verify_attempt.py',[str(D/n)]);assert p.returncode==0,p.stderr;summaries[n]=json.loads(p.stdout);assert summaries[n]['sourceCommit']==source and summaries[n]['counts']==counts
first=read('ATTEMPT_1/REGRESSION_RESULT.json');last=read('ATTEMPT_2/REGRESSION_RESULT.json');ids=lambda r:{(t['testClass'],t['method']):t['outcome'] for t in r['tests']};before=ids(first);after=ids(last);failed={k for k,v in before.items() if v in ['FAILED','ERROR']};assert len(failed)==2 and all(after[k]=='PASSED' for k in failed)
for n,v in first['inputTrees'].items():
 if n!='app/src/test':assert last['inputTrees'][n]==v,n
fixes=read('HISTORICAL_COMPARISON_FIXES.json');assert len(fixes)==2
with zipfile.ZipFile(D/'ORIGINAL_HISTORICAL_COMPARISON_TESTS.zip') as z:
 assert z.testzip() is None and set(z.namelist())=={x['path'] for x in fixes}
 for fix in fixes:
  a=z.read(fix['path']);b=git('show',expected['ATTEMPT_2'][0]+':'+fix['path']);assert sha(a)==fix['oldSha256'] and sha(b)==fix['newSha256']
  assert a==git('show',expected['ATTEMPT_1'][0]+':'+fix['path'])
  lines=iter(x.strip() for x in b.decode().splitlines() if x.strip());assert all(any(x==l.strip() for x in lines) for l in a.decode().splitlines() if l.strip())
  assert fix['allOriginalNonblankLinesAndAssertionsRetainedInOrderIgnoringIndentation'] and not fix['expectedHashesReplaced'] and not fix['fullClassConvertedToHistorical']
changed=git('diff','--name-only',expected['ATTEMPT_1'][0],expected['ATTEMPT_2'][0],'--','app/src/test').decode().splitlines();assert set(changed)=={x['path'] for x in fixes}
with zipfile.ZipFile(D/'ATTEMPT_1/RAW.zip') as z:
 rows=[json.loads(l) for l in z.read('REGRESSION/HISTORY_CASES.jsonl').splitlines()];original=[r for r in rows if r['kind']=='original_archive'];assert len(rows)==66 and len(original)==64 and len({r['path'] for r in original})==64
 assert all(r['restoredAndReopenedUnchanged'] and r['unit6ReviewRefusedWithoutCurrentPrerequisites'] for r in original)
 counts=Counter(tuple(r['currentCompletedUnits']) for r in original);assert counts=={(1,2):24,(1,2,3):40}
 mixed=[r for r in rows if r['kind']!='original_archive'];assert len(mixed)==2 and {r['freshUnit6Schema'] for r in mixed}=={4,8} and all(r['restoredRoundtripAndReopenedUnchanged'] and r['wrongSchemaAndChangedOwnerRejected'] for r in mixed)
 assert next(r for r in mixed if r['freshUnit6Schema']==4)['scope']=='HISTORICAL_PRE_UNIT6_V3_PACK'
# Existing saved examples, genuine captures and original archive allowlist were not replaced.
for name,h in read('FINAL_CONSERVATION.json')['additionalGenuineCaptures'].items():
 assert sha((R/name).read_bytes())==h
 with zipfile.ZipFile(R/name) as z:assert z.testzip() is None
for row in read('PROPOSAL_REVIEW.json')['files']:assert sha((R/row['path']).read_bytes())==row['newSha256'],row['path']
policy=read('POLICY_INVENTORY/CURRENT_POLICY_INVENTORY.json');assert policy['activePolicies']==28 and policy['objectivePools']==59 and policy['candidatePlacements']==86 and policy['uniqueCandidateItems']==48 and policy['policiesStillContainingPrintCaseSingleton']==25
assert policy['exportSha256']==sha((D/'POLICY_INVENTORY/EXPORT.log.txt').read_bytes()) and policy['canonicalSourceDigest']==generation['sourceDigest']
assert read('FINAL_CONSERVATION.json')['independentAudit']=='INCOMPLETE_ALL48_OPEN'
b=run('BROAD/verify_broad_discovery.py');assert b.returncode==0,b.stderr;broad=json.loads(b.stdout)
assert broad['syntheticSkips']==1378 and broad['runtimePasses']==0 and broad['priorMethodsRetained']==1365 and broad['addedMethods']==13
print(json.dumps(dict(verified=True,broadDiscovery=broad,generation=generation,attempts={n:s['counts'] for n,s in summaries.items()},bothObservedFailuresPassedInSeparateFullClassFollowup=True,scopesNotSummed=True,originalAssertionsAndHashesRetained=True,originalHistoryCasesRestored=64,caseObservationsNotTestCounts=True,currentOriginalHistoryCredit={'Units1_2':24,'Units1_3':40},mixedLifecycleObservations=2,extraAuthenticCapturesConserved=2,remainingPrintSingletonPolicies=25,completeSuite=False,independentAudit='INCOMPLETE_ALL48_OPEN',physicalAcceptance='OUTSTANDING_LAST',apkIdentity=None,apkSignature=None,additionalJUnitMethodsExecuted=0)))
