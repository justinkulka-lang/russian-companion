from evidence import *
import shutil
r=read(W/'DISCOVERY/RESULT.json');scope=read(W/'SCOPE.json');n=len(r['tests']);actual={(x['testClass'],x['method']) for x in r['tests']}
assert r['testCounts']==dict(reported=n,passed=0,failed=0,errors=0,skipped=n) and r['classes']==96 and r['exitCode']==0
assert len(actual)==n and {x[0] for x in actual}==set(scope['selectors']) and r['inputTrees']==scope['inputTrees']
import zipfile
priorPath=R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit13-affected-continuation-2026-10-27/INTERRUPTED_RAW.zip'
member='ORIGINAL_WORKSPACE/DISCOVERY/RESULT.json'
with zipfile.ZipFile(priorPath) as z:
    assert z.testzip() is None;raw=z.read(member);prior=json.loads(raw)
before={(x['testClass'],x['method']) for x in prior['tests']};assert len(before)==481 and before<=actual
controls=[]
for d in ['engineering-unit14-plural-possession-correction-2026-10-28','engineering-unit14-source-reconciliation-2026-10-28']:
    name='REGRESSION_RESULT.json' if 'correction-' in d else 'BINDING_REGRESSION_RESULT.json'
    controls += [dict(testClass=x['testClass'],method=x['method']) for x in read(R/'audit/gpt61-sol-high-comprehensive-2026-10-19'/d/name)['tests']]
assert len(controls)==22 and {(x['testClass'],x['method']) for x in controls}<=actual
scope.update(methodCount=n,expectedMethods=[dict(testClass=c,method=m) for c,m in sorted(actual)],priorScopeArchivePath=priorPath.relative_to(R).as_posix(),priorScopeArchiveSha256=sha(priorPath.read_bytes()),priorScopeResultMember=member,priorScopeResultMemberSha256=sha(raw),priorMethodCount=481,addedMethods=[dict(testClass=c,method=m) for c,m in sorted(actual-before)],currentUnit14ControlMethods=controls)
save(W/'SCOPE.json',scope);save(D/'SCOPE.json',scope)
for name in ['CONTEXT.json','RESULT.json','RESULTS.csv','RUN.exit','SHA256.json']:shutil.copy2(W/'DISCOVERY'/name,D/('DISCOVERY_'+name))
archive_mode('DISCOVERY','RAW_DISCOVERY')
v=dict(sourceCommit=r['sourceCommit'],methods=n,classes=96,syntheticSkips=n,runtimePasses=0,all481PriorMethodsRetained=True,addedScopeIdentities=len(actual-before),removedMethods=[],all22CurrentUnit14ControlsIncluded=True,inheritedSelectors=87,addedOlderUnit14And15Classes=9,all96SelectorsFullClasses=True,inputTreesMatchPrepared=True,completeSuite=False)
save(D/'DISCOVERY_SCOPE_VERIFICATION.json',v);print(json.dumps(dict(verified=True,**v)))
