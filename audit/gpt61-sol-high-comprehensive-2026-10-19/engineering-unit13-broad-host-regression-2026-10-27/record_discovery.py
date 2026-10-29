from evidence import *
import shutil
r=read(W/'DISCOVERY/RESULT.json');scope=read(W/'SCOPE.json');expected={(x['testClass'],x['method']) for x in scope['expectedMethods']}
actual={(x['testClass'],x['method']) for x in r['tests']}
assert r['testCounts']==dict(reported=1477,passed=0,failed=0,errors=0,skipped=1477) and r['classes']==257 and r['exitCode']==0
assert actual==expected and len(actual)==1477 and r['inputTrees']==scope['inputTrees']
prior=read(R/scope['priorBroadResultPath']);before={(x['testClass'],x['method']) for x in prior['tests']}
assert sha((R/scope['priorBroadResultPath']).read_bytes())==scope['priorBroadResultSha256']
assert len(before)==1458 and before<=actual and len(actual-before)==19
assert {(x['testClass'],x['method']) for x in scope['currentFollowupMethods']}<=actual
context=read(W/'DISCOVERY/CONTEXT.json');assert context['exclusions']=={'VisualReviewCaptureTest':2,'Units35ChunkCaptureTest':2,'Units610ChunkCaptureTest':10,'Units1115GapCaptureTest':2}
assert not any(x['testClass'].split('.')[-1] in context['exclusions'] for x in r['tests'])
for n in ['CONTEXT.json','RESULT.json','RESULTS.csv','RUN.exit','SHA256.json']:shutil.copy2(W/'DISCOVERY'/n,D/('DISCOVERY_'+n))
archive_mode('DISCOVERY','RAW_DISCOVERY')
save(D/'DISCOVERY_SCOPE_VERIFICATION.json',dict(sourceCommit=r['sourceCommit'],methods=1477,classes=257,syntheticSkips=1477,runtimePasses=0,priorBroadSource=prior['sourceCommit'],priorMethods=1458,addedMethods=[dict(testClass=c,method=m) for c,m in sorted(actual-before)],removedMethods=[],all63FollowupIdsIncluded=True,all12DistinctPriorFailedIdsIncluded=True,noMethodOrClassFilters=True,inputTreesMatchPrepared=True,completeSuite=False))
print(json.dumps(dict(verified=True,source=r['sourceCommit'],discovered=1477,classes=257,syntheticSkips=1477,runtimePasses=0,allPrior1458Retained=True,added=19,removed=0)))
