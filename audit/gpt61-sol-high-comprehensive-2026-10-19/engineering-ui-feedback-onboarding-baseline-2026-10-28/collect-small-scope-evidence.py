"""Preserve a completed small selected scope; never edits app/tests or commits."""
from pathlib import Path
import sys,json,hashlib,zipfile,shutil,subprocess,xml.etree.ElementTree as ET
R=Path('/workspace/russian-companion');W=Path(sys.argv[1]);D=Path(sys.argv[2])
read=lambda p:json.loads(p.read_text());sha=lambda b:hashlib.sha256(b).hexdigest();save=lambda p,v:p.write_text(json.dumps(v,indent=2)+'\n')
assert(W/'REGRESSION_COMPLETION.json').exists(),'Supervisor still active'
assert not subprocess.check_output(['git','status','--porcelain=v1','-z','--untracked-files=all'],cwd=R)
prep=read(D/'PREPARATION.json');scope=read(W/'SCOPE.json');expected={(t['testClass'],t['method'])for t in scope['expectedMethods']}
assert len(expected)==scope['methodCount']
for key in ['preExistingTestHashes','newBaselineTestHashes','testHashes','currentRecordHashes','protectedEvidenceHashes']:
 for n,h in prep.get(key,{}).items():assert sha((R/n).read_bytes())==h,n
for mode,label in [('DISCOVERY','RAW_DISCOVERY'),('REGRESSION','RAW_RUNTIME')]:
 c=read(W/mode/'CONTEXT.json');r=read(W/mode/'RESULT.json');end=read(W/(mode+'_COMPLETION.json'))
 assert r['inputTrees']==c['inputTrees']==scope['inputTrees']==prep['inputTrees']and c['sourceCommit']==r['sourceCommit']
 assert subprocess.check_output(['git','rev-parse','HEAD'],cwd=R).decode().strip()==r['sourceCommit']
 assert r['exitCode']==end['runnerExit']==int(end['gradleExit'])and c['selectors']==scope['selectors']
 assert {(t['testClass'],t['method'])for t in r['tests']}==expected
 events=[json.loads(x)for x in(W/mode/'EVENTS.jsonl').read_text().splitlines()];events=[x for x in events if x.get('kind')=='RESULT'];assert len(events)==len(expected)
 assert {(x['className'],x['name'])for x in events}==expected
 xmlrows={}
 for path in(W/mode/'XML').glob('*.xml'):
  for t in ET.parse(path).getroot().findall('testcase'):
   key=(t.get('classname'),t.get('name'));assert key not in xmlrows;xmlrows[key]='FAILED'if t.find('failure')is not None else'ERROR'if t.find('error')is not None else'SKIPPED'if t.find('skipped')is not None else'PASSED'
 assert xmlrows=={(x['testClass'],x['method']):x['outcome']for x in r['tests']}
 files={p.relative_to(W).as_posix():p for p in(W/mode).rglob('*')if p.is_file()}
 for p in W.glob(mode+'_*'):
  if p.is_file():files[p.name]=p
 for n in ['run.py','launch.py','supervise.py','resources.gradle','prepare.py']:files['TOOLING/'+n]=W/n
 files['TOOLING/SCOPE.json']=W/'SCOPE.json';files['TOOLING/collect-small-scope-evidence.py']=Path(__file__)
 for n,h in c['initScriptSha256'].items():
  p=Path(n);assert sha(p.read_bytes())==h;files['INIT/'+p.name]=p
 idx={}
 with zipfile.ZipFile(D/(label+'.zip'),'x',zipfile.ZIP_DEFLATED)as z:
  for n,p in sorted(files.items()):
   assert not n.lower().endswith('.pdf');data=p.read_bytes();z.writestr(n,data);idx[n]={'bytes':len(data),'sha256':sha(data)}
 save(D/(label+'_INDEX.json'),idx)
 with zipfile.ZipFile(D/(label+'.zip'))as z:
  assert z.testzip()is None and set(z.namelist())==set(idx)
  for n,h in idx.items():data=z.read(n);assert len(data)==h['bytes']and sha(data)==h['sha256']
 for n in ['CONTEXT.json','RESULT.json','RESULTS.csv','RUN.exit','SHA256.json']:shutil.copyfile(W/mode/n,D/(mode+'_'+n))
r=read(W/'REGRESSION/RESULT.json');failures=[]
for p in(W/'REGRESSION/XML').glob('*.xml'):
 for t in ET.parse(p).getroot().findall('testcase'):
  for kind in ['failure','error']:
   f=t.find(kind)
   if f is not None:failures.append({'testClass':t.get('classname'),'method':t.get('name'),'kind':kind,'message':f.get('message',''),'trace':f.text or ''})
save(D/'ACTUAL_FAILURES.json',{'sourceCommit':r['sourceCommit'],'count':len(failures),'failures':failures,'runtimeAssertionsNotWaived':True})
save(D/'RAW_SCOPE_VERIFICATION.json',{'sourceCommit':r['sourceCommit'],'inputTrees':r['inputTrees'],'counts':r['testCounts'],'classes':r['classes'],'exitCode':r['exitCode'],'uniqueMethodSetExact':True,'xmlEventsAndResultsMatch':True,'archiveShaCrcVerified':True,'testHashesMatchPreparation':True,'completeSuite':False,'all48Findings':'OPEN','audit':'INCOMPLETE','physicalAcceptance':'OUTSTANDING_LAST','apkIdentity':None,'apkSignature':None})
shutil.copyfile(__file__,D/'collect-small-scope-evidence.py')
print(json.dumps({'source':r['sourceCommit'],'counts':r['testCounts'],'classes':r['classes'],'exit':r['exitCode'],'lint':r['lintIssuesBySeverity']}))
