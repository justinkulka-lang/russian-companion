from evidence import *
import shutil
r=read(W/'DISCOVERY/RESULT.json');s=read(W/'SCOPE.json')
assert r['testCounts']==dict(reported=119,passed=0,failed=0,errors=0,skipped=119)and r['classes']==17 and r['exitCode']==0
assert {(t['testClass'],t['method'])for t in r['tests']}=={(t['testClass'],t['method'])for t in s['expectedMethods']}
assert r['inputTrees']==s['inputTrees']
s['inventoryOnlyUntilFreshDiscovery']=False;save(W/'SCOPE.json',s);save(D/'SCOPE.json',s)
for n in ['CONTEXT.json','RESULT.json','RESULTS.csv','RUN.exit','SHA256.json']:shutil.copyfile(W/'DISCOVERY'/n,D/('DISCOVERY_'+n))
archive_mode('DISCOVERY','RAW_DISCOVERY');save(D/'DISCOVERY_SCOPE_VERIFICATION.json',{'sourceCommit':r['sourceCommit'],'methods':119,'classes':17,'syntheticSkips':119,'runtimePasses':0,'all60FailedIdsIncluded':True,'all45SiblingIdsIncluded':True,'all14ControlsIncluded':True,'allSelectorsFullClasses':True,'inputTreesMatchPrepared':True,'completeSuite':False})
manifest();print(json.dumps({'verified':True,'sourceCommit':r['sourceCommit'],'methods':119,'classes':17,'syntheticSkips':119,'runtimePasses':0}))
