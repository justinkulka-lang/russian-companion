from evidence import *
import shutil
r=read(W/'DISCOVERY/RESULT.json');scope=read(W/'SCOPE.json')
actual={(x['testClass'],x['method']) for x in r['tests']};expected={(x['testClass'],x['method']) for x in scope['expectedMethods']}
assert r['testCounts']==dict(reported=79,passed=0,failed=0,errors=0,skipped=79) and r['classes']==13 and r['exitCode']==0
assert len(actual)==79 and actual==expected and r['inputTrees']==scope['inputTrees']
for key,n in [('priorFailedIdentities',14),('currentUnit13ControlMethods',19),('currentUnit14ControlMethods',22)]:
    assert len(scope[key])==n and {(x['testClass'],x['method']) for x in scope[key]}<=actual
scope['inventoryOnlyUntilFreshDiscovery']=False;scope['runtimeStatus']='NOT_RUN'
save(W/'SCOPE.json',scope);save(D/'SCOPE.json',scope)
for n in ['CONTEXT.json','RESULT.json','RESULTS.csv','RUN.exit','SHA256.json']:shutil.copy2(W/'DISCOVERY'/n,D/('DISCOVERY_'+n))
archive_mode('DISCOVERY','RAW_DISCOVERY')
v=dict(sourceCommit=r['sourceCommit'],methods=79,classes=13,syntheticSkips=79,runtimePasses=0,exactPreparedIdentities=True,all14FailedIdentitiesIncluded=True,all19CurrentUnit13ControlsIncluded=True,all22CurrentUnit14ControlsIncluded=True,allSelectorsFullClasses=True,inputTreesMatchPrepared=True,completeSuite=False)
save(D/'DISCOVERY_SCOPE_VERIFICATION.json',v)
print(json.dumps(dict(verified=True,**v)))
