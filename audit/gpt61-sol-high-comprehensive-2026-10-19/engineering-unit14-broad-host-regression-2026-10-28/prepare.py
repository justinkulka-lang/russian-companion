"""Prepare broad verification; never modify any of the six repository inputs."""
from pathlib import Path
import json,hashlib,subprocess,zipfile,os
R=Path('/workspace/russian-companion');W=Path(__file__).parent
B=R/'audit/gpt61-sol-high-comprehensive-2026-10-19'
D=B/'engineering-unit14-broad-host-regression-2026-10-28'
P=B/'engineering-unit14-historical-comparison-followup-2026-10-28'
OLD=B/'engineering-unit13-broad-host-regression-2026-10-27'
git=lambda *a:subprocess.check_output(['git',*a],cwd=R,text=True).strip()
sha=lambda b:hashlib.sha256(b).hexdigest()
save=lambda p,v:p.write_text(json.dumps(v,indent=2)+'\n')
S='5cffeea7a78ea031f936942aa2631efcf8f2d6c5'
assert git('rev-parse','HEAD')==S and not git('status','--porcelain')
assert git('branch','--show-current')=='cursor/content-audit-corrections-964f'
assert git('ls-remote','origin','refs/heads/cursor/content-audit-corrections-964f').split()[0]==S
for p in Path('/proc').iterdir():
    if not p.name.isdigit():continue
    try:
        args=(p/'cmdline').read_bytes().split(b'\0');cmd=b' '.join(args).decode(errors='replace')
        state=(p/'stat').read_text().rsplit(')',1)[1].split()[0]
    except OSError:continue
    assert not (state!='Z' and Path(args[0].decode(errors='replace')).name=='java' and 'Gradle Test Executor' in cmd),(p.name,cmd[:150])
assert not any(v=='1' for k,v in os.environ.items() if k.startswith('WRITE_'))
verified=json.loads(subprocess.check_output(['python3','-B',str(P/'verify_results.py')],cwd=R,text=True))
assert verified['verified'] and verified['counts']==dict(reported=79,passed=79,failed=0,errors=0,skipped=0)
for n,count in [('PRIOR_FAILURES_RECHECKED.json',14),('UNIT13_CONTROLS_RECHECKED.json',19),('UNIT14_CONTROLS_RECHECKED.json',22)]:
    r=json.loads((P/n).read_text());assert r['allPassed'] and len(r['methods'])==count
prior=json.loads((OLD/'REGRESSION_RESULT.json').read_text())
assert prior['sourceCommit']=='cd2b1c91911040acb190147cf5966d0f23ba2a0d' and prior['testCounts']==dict(reported=1477,passed=1476,failed=0,errors=0,skipped=1)
ps=json.loads((P/'SCOPE.json').read_text());before={(t['testClass'],t['method']) for t in prior['tests']}
added={(t['testClass'],t['method']) for t in ps['currentUnit14ControlMethods']}
assert len(before)==1477 and len(added)==22 and not before&added
expected=sorted(before|added);assert len(expected)==1499 and len({k[0] for k in expected})==261
groups=json.loads((OLD/'SCOPE.json').read_text())['failureGroups']
groups.append(dict(name='Unit14 affected integration original failures',originalSource='dd736e1a8657bd02a34063752afd82146ab2f842',identities=ps['priorFailedIdentities']))
failed=sorted({(t['testClass'],t['method']) for g in groups for t in g['identities']})
affected=json.loads((B/'engineering-unit14-affected-integration-2026-10-28/SCOPE.json').read_text())['expectedMethods']
for methods in [ps['expectedMethods'],ps['currentUnit13ControlMethods'],ps['currentUnit14ControlMethods'],affected]:
    assert {(t['testClass'],t['method']) for t in methods}<=set(expected)
assert set(failed)<=set(expected)
trees={n:git('rev-parse','HEAD:'+n) for n in ['app/src/main','app/src/test','catalog','docs','review','release']}
scope=dict(priorBroadSource=prior['sourceCommit'],priorBroadResultPath=(OLD/'REGRESSION_RESULT.json').relative_to(R).as_posix(),priorBroadResultSha256=sha((OLD/'REGRESSION_RESULT.json').read_bytes()),priorBroadReported=1477,methodCount=1499,classes=261,expectedMethods=[dict(testClass=c,method=m) for c,m in expected],expectedAddedMethods=ps['currentUnit14ControlMethods'],expectedRemovedMethods=[],inputTrees=trees,broadHostTask=True,noMethodOrClassFilters=True,selectors=[],priorFailedIdentities=[dict(testClass=c,method=m) for c,m in failed],failureGroups=groups,currentFollowupMethods=ps['expectedMethods'],currentUnit13ControlMethods=ps['currentUnit13ControlMethods'],currentUnit14ControlMethods=ps['currentUnit14ControlMethods'],originalUnit14FailedIdentities=ps['priorFailedIdentities'],previousAffectedMethods=affected,predictionOnlyUntilFreshDiscovery=True,completeSuite=False)
prepOld=json.loads((P/'PREPARATION.json').read_text());testHashes={n:sha((R/n).read_bytes()) for n in prepOld['existingTestFiles']}
assert len(testHashes)==281
for n,h in prepOld['currentRecordHashes'].items():assert sha((R/n).read_bytes())==h,n
for n,h in prepOld['archives'].items():
    assert sha((R/n).read_bytes())==h,n
    with zipfile.ZipFile(R/n) as z:assert z.testzip() is None,n
protected=dict(prepOld['priorFailureEvidenceHashes'])
protected.update({p.relative_to(R).as_posix():sha(p.read_bytes()) for p in P.iterdir() if p.is_file()})
prep=dict(parentCommit=S,inputTrees=trees,productionTree=trees['app/src/main'],existingTestFiles=testHashes,currentRecordHashes=prepOld['currentRecordHashes'],archives=prepOld['archives'],priorFailureEvidenceHashes=protected,followupEvidenceDirectory=P.relative_to(R).as_posix(),followupVerifiedSource=verified['sourceCommit'],original757AssertionsRetained=True,allCurrentControlsUnchanged=True,noSourceMutationInBroadPhase=True,repositorySourceDigest=prepOld['repositorySourceDigest'],packContentReviewDigest=prepOld['packContentReviewDigest'],all48Findings='OPEN',audit='INCOMPLETE',physicalAcceptance='OUTSTANDING_LAST')
D.mkdir()
for n,v in [('SCOPE.json',scope),('PREPARATION.json',prep),('PRIOR_FOLLOWUP_VERIFICATION.json',verified)]:save(W/n,v);save(D/n,v)
print(json.dumps(dict(parent=S,predictedMethods=1499,predictedClasses=261,priorBroadMethods=1477,added=22,removed=0,currentFollowupMethods=79,previousAffectedMethods=len(affected),distinctPriorFailedIds=len(failed),testFilesPinned=281,bindingsPinned=155,archivesShaCrc=77,runtimePasses=0)))
