from pathlib import Path
import json,hashlib,subprocess,shutil
R=Path('/workspace/russian-companion');W=Path(__file__).parent
B=R/'audit/gpt61-sol-high-comprehensive-2026-10-19'
P=B/'engineering-unit14-affected-integration-2026-10-28'
D=B/'engineering-unit14-historical-comparison-followup-2026-10-28'
git=lambda *a:subprocess.check_output(['git',*a],cwd=R,text=True).strip()
sha=lambda b:hashlib.sha256(b).hexdigest()
save=lambda p,v:p.write_text(json.dumps(v,indent=2)+'\n')
for name in ['run.py','resources.gradle','launch.py','supervise.py','start_runtime.py','durable_monitor.py','evidence.py','collect.py','verify_results.py']:
    t=(P/name).read_text().replace('engineering-unit14-affected-integration-2026-10-28','engineering-unit14-historical-comparison-followup-2026-10-28')
    if name=='run.py':
        t=t.replace('Unit14 affected full classes,96 selectors including nine formerly omitted Unit14/15 classes;method count from fresh discovery;all281tests unchanged;preserve every failure;not complete suite','79 methods / 13 full classes: seven failure classes and six unchanged Unit13/14 baseline/UI/compatibility/binding controls; preserve all failures; not complete suite')
    if name=='evidence.py':
        t=t.replace("'completion_worker.py']","'completion_worker.py','assertion_calls.py','configure.py']")
        t=t.replace("    for n,h in prep['archives'].items():", "    for n,h in prep['priorFailureEvidenceHashes'].items():assert sha((R/n).read_bytes())==h,n\n    from assertion_calls import assertions\n    with zipfile.ZipFile(D/'ORIGINAL_COMPARISONS.zip') as z:\n        assert z.testzip() is None\n        for review in prep['comparisonReview']:\n            n=review['path'];old=z.read(n);assert sha(old)==review['originalSha256']\n            assert sha((R/n).read_bytes())==review['newSha256']\n            before=assertions(old.decode());after=assertions((R/n).read_text());j=0\n            for call in after:\n                if j<len(before) and call==before[j]:j+=1\n            assert j==len(before)==review['originalAssertionInvocations'],n\n    for n,h in prep['archives'].items():")
    if name=='collect.py':
        t=t.replace("==scope['classes']==96","==scope['classes']==13").replace("classes=96", "classes=13")
        t += '''
for label,key in [('UNIT13_CONTROLS_RECHECKED','currentUnit13ControlMethods'),('PRIOR_FAILURES_RECHECKED','priorFailedIdentities')]:
    methods=[dict(**x,outcome=by[x['testClass'],x['method']]) for x in scope[key]]
    save(D/(label+'.json'),dict(sourceCommit=S,methods=methods,allIncluded=True,allPassed=all(t['outcome']=='PASSED' for t in methods),scopesNotSummed=True))
'''
    if name=='verify_results.py':
        t=t.replace("len(c['selectors'])==96 and c['command'].count('--tests')==96","len(c['selectors'])==13 and c['command'].count('--tests')==13")
        a=t.index("archive=R/scope['priorScopeArchivePath']");b=t.index("assert {(t['testClass'],t['method']) for t in scope['currentUnit14ControlMethods']}<=expected",a)
        t=t[:a]+'''assert {(t['testClass'],t['method']) for t in scope['priorFailedIdentities']}<=expected
assert len(scope['priorFailedIdentities'])==14
for label,key,n in [('UNIT13_CONTROLS_RECHECKED','currentUnit13ControlMethods',19),('PRIOR_FAILURES_RECHECKED','priorFailedIdentities',14)]:
    record=read(D/(label+'.json'));assert record['allIncluded'] and len(record['methods'])==n
    assert {(t['testClass'],t['method']) for t in record['methods']}=={(t['testClass'],t['method']) for t in scope[key]}
    for t in record['methods']:assert t['outcome']==sets['REGRESSION'][t['testClass'],t['method']]
    assert record['allPassed']==all(t['outcome']=='PASSED' for t in record['methods'])
'''+t[b:]
        t=t.replace('classes=96,exitCode=', 'classes=13,exitCode=')
        t=t.replace('all481PriorIdentitiesRetained=True,addedScopeIdentities=len(scope[\'addedMethods\']),','all14OriginalFailedIdentitiesIncluded=True,all19Unit13ControlsIncluded=True,')
    (W/name).write_text(t)
shutil.copy2(B/'engineering-unit13-historical-comparison-followup-2026-10-27/assertion_calls.py',W/'assertion_calls.py')
from assertion_calls import assertions
prep=json.loads((W/'PREPARATION.json').read_text());scope=json.loads((W/'SCOPE.json').read_text())
for review in prep['comparisonReview']:
    n=review['path'];before=subprocess.check_output(['git','show',prep['parentCommit']+':'+n],cwd=R).decode();after=(R/n).read_text();a=assertions(before);b=assertions(after);j=0
    for x in b:
        if j<len(a) and x==a[j]:j+=1
    assert j==len(a)
    review.update(newSha256=sha(after.encode()),newAssertionInvocations=len(b))
subprocess.run(['git','add',*prep['changedComparisonFiles']],cwd=R,check=True)
tree=git('write-tree');trees={n:git('rev-parse',tree+':'+n) for n in prep['inputTrees']}
prep['inputTrees']=trees;scope['inputTrees']=trees
prep['existingTestFiles']={n:sha((R/n).read_bytes()) for n in prep['existingTestFiles']}
for name,value in [('PREPARATION.json',prep),('SCOPE.json',scope),('COMPARISON_REVIEW.json',prep['comparisonReview'])]:save(W/name,value);save(D/name,value)
for name in ['prepare.py','configure.py','run.py','resources.gradle','launch.py','supervise.py','start_runtime.py','durable_monitor.py','evidence.py','collect.py','verify_results.py','assertion_calls.py']:
    shutil.copy2(W/name,D/name)
print(json.dumps(dict(methods=scope['methodCount'],classes=scope['classes'],originalAssertions=prep['originalAssertionInvocations'],newAssertions=prep['newAssertionInvocations'],productionUnchanged=trees['app/src/main']==prep['productionTree'])))
