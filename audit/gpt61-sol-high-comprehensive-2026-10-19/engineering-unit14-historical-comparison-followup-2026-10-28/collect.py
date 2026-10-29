from evidence import *
import shutil,xml.etree.ElementTree as ET
r=read(W/'REGRESSION/RESULT.json');c=read(W/'REGRESSION/CONTEXT.json');end=read(W/'REGRESSION_COMPLETION.json');scope=read(W/'SCOPE.json');S=r['sourceCommit']
assert git('rev-parse','HEAD')==S==c['sourceCommit'] and not git('status','--porcelain')
assert end['runnerExit']==r['exitCode']==int(end['gradleExit'])
for n in ['CONTEXT.json','RESULT.json','RESULTS.csv','RUN.exit','SHA256.json']:shutil.copy2(W/'REGRESSION'/n,D/('REGRESSION_'+n))
archive_mode('REGRESSION','RAW_AFFECTED',monitor=True)
failures=[]
for p in (W/'REGRESSION/XML').glob('TEST-*.xml'):
    for t in ET.parse(p).getroot().findall('testcase'):
        for kind in ['failure','error']:
            e=t.find(kind)
            if e is not None:failures.append(dict(testClass=t.get('classname'),method=t.get('name'),kind=kind,message=e.get('message',''),trace=e.text or ''))
full=dict(sourceCommit=S,count=len(failures),failures=failures,originalAssertionsNotWaived=True)
data=(json.dumps(full,indent=2)+'\n').encode()
archive=D/'ACTUAL_FAILURES_FULL.zip'
with zipfile.ZipFile(archive,'x',zipfile.ZIP_DEFLATED) as z:z.writestr('ACTUAL_FAILURES.json',data)
with zipfile.ZipFile(archive) as z:assert z.testzip() is None and z.read('ACTUAL_FAILURES.json')==data
save(D/'ACTUAL_FAILURES.json',dict(sourceCommit=S,count=len(failures),originalAssertionsNotWaived=True,
    storage=dict(format='LOSSLESS_ZIP',archive=archive.name,archiveSha256=sha(archive.read_bytes()),
        member='ACTUAL_FAILURES.json',memberBytes=len(data),memberSha256=sha(data)),
    failures=[dict(testClass=f['testClass'],method=f['method'],kind=f['kind'],
        **{key+suffix:value for key in ['message','trace']
           for suffix,value in [('Bytes',len(f[key].encode())),('Sha256',sha(f[key].encode()))]})
        for f in failures]))
save(D/'SKIPPED_METHODS.json',dict(sourceCommit=S,methods=[x for x in r['tests'] if x['outcome']=='SKIPPED'],physicalAcceptance='OUTSTANDING_LAST'))
assert r['testCounts']['reported']==scope['methodCount'] and r['classes']==scope['classes']==13
save(D/'FINAL_CONSERVATION.json',dict(sourceCommit=S,**conservation()))
by={(x['testClass'],x['method']):x['outcome'] for x in r['tests']}
save(D/'UNIT14_CONTROLS_RECHECKED.json',dict(sourceCommit=S,methods=[dict(**x,outcome=by[x['testClass'],x['method']]) for x in scope['currentUnit14ControlMethods']],all22Included=True,allPassed=all(by[x['testClass'],x['method']]=='PASSED' for x in scope['currentUnit14ControlMethods']),countsNotSummed=True))
print(json.dumps(dict(sourceCommit=S,counts=r['testCounts'],classes=13,exitCode=r['exitCode'],actualFailures=len(failures))))

for label,key in [('UNIT13_CONTROLS_RECHECKED','currentUnit13ControlMethods'),('PRIOR_FAILURES_RECHECKED','priorFailedIdentities')]:
    methods=[dict(**x,outcome=by[x['testClass'],x['method']]) for x in scope[key]]
    save(D/(label+'.json'),dict(sourceCommit=S,methods=methods,allIncluded=True,allPassed=all(t['outcome']=='PASSED' for t in methods),scopesNotSummed=True))
