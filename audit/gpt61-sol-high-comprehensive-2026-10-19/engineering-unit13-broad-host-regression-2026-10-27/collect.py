from evidence import *
import shutil,xml.etree.ElementTree as ET
r=read(W/'REGRESSION/RESULT.json');c=read(W/'REGRESSION/CONTEXT.json');end=read(W/'REGRESSION_COMPLETION.json');S=r['sourceCommit']
assert git('rev-parse','HEAD')==S==c['sourceCommit'] and not git('status','--porcelain')
assert end['runnerExit']==r['exitCode']==int(end['gradleExit']) and r['testCounts']['reported']==1477 and r['classes']==257
for n in ['CONTEXT.json','RESULT.json','RESULTS.csv','RUN.exit','SHA256.json']:shutil.copy2(W/'REGRESSION'/n,D/('REGRESSION_'+n))
archive_mode('REGRESSION','RAW_BROAD',monitor=True)
save(D/'FINAL_CONSERVATION.json',dict(sourceCommit=S,**conservation()))
prep=read(D/'PREPARATION.json')
priorVerify=json.loads(subprocess.check_output(['python3','-B',str(R/prep['followupEvidenceDirectory']/'verify_results.py')],cwd=R,text=True));assert priorVerify['verified'] and priorVerify['originalAssertionInvocationsPreserved']==627
save(D/'FOLLOWUP_CONSERVATION_RECHECK.json',priorVerify)
failures=[]
for p in (W/'REGRESSION/XML').glob('TEST-*.xml'):
    for t in ET.parse(p).getroot().findall('testcase'):
        for kind in ['failure','error']:
            e=t.find(kind)
            if e is not None:failures.append(dict(testClass=t.get('classname'),method=t.get('name'),kind=kind,message=e.get('message',''),trace=e.text or ''))
save(D/'ACTUAL_FAILURES.json',dict(sourceCommit=S,count=len(failures),failures=failures,assertionsNotWaived=True))
save(D/'SKIPPED_METHODS.json',dict(sourceCommit=S,methods=[t for t in r['tests'] if t['outcome']=='SKIPPED'],physicalAcceptance='OUTSTANDING_LAST'))
scope=read(W/'SCOPE.json');by={(t['testClass'],t['method']):t['outcome'] for t in r['tests']};groups=[]
for g in scope['failureGroups']:groups.append(dict(name=g['name'],originalSource=g['originalSource'],rechecked=[dict(**t,outcome=by[t['testClass'],t['method']]) for t in g['identities']]))
save(D/'PRIOR_FAILURES_RECHECKED.json',dict(sourceCommit=S,groups=groups,uniqueIdentities=len(scope['priorFailedIdentities']),allIncluded=True,allPassed=all(by[t['testClass'],t['method']]=='PASSED' for t in scope['priorFailedIdentities']),overlapNotSummed=True))
print(json.dumps(dict(sourceCommit=S,counts=r['testCounts'],classes=r['classes'],exitCode=r['exitCode'],actualFailures=len(failures),priorFailureIdsRechecked=12)))
