"""Read-only saved core evidence verification; no test/build execution."""
from pathlib import Path
from datetime import datetime,timezone
import json,subprocess,hashlib,zipfile,re
import xml.etree.ElementTree as ET
D=Path(__file__).resolve().parent;R=Path(subprocess.check_output(['git','rev-parse','--show-toplevel'],cwd=D,text=True).strip());sha=lambda b:hashlib.sha256(b).hexdigest();read=lambda n:json.loads((D/n).read_text());blob=lambda s,n:subprocess.check_output(['git','show',s+':'+n],cwd=R)
manifest=read('SHA256.json');assert set(manifest)=={p.relative_to(D).as_posix() for p in D.rglob('*') if p.is_file() and p.name!='SHA256.json'};tracked=set(subprocess.check_output(['git','ls-files'],cwd=R,text=True).splitlines())
for n,h in manifest.items():assert sha((D/n).read_bytes())==h and (D/n).relative_to(R).as_posix() in tracked
r=read('REGRESSION_RESULT.json');d=read('DISCOVERY_RESULT.json');c=read('REGRESSION_CONTEXT.json');S=c['sourceCommit'];p=read('PREPARATION.json');assert r['sourceCommit']==d['sourceCommit']==S;assert r['testCounts']['reported']==14 and r['classes']==3 and d['testCounts']==dict(reported=14,passed=0,failed=0,errors=0,skipped=14);assert r['discoveryComparison']['sameInputTrees'] and r['discoveryComparison']['exactSameMethodSet'];assert r['inputTrees']==d['inputTrees']
with zipfile.ZipFile(D/'RAW_CORE.zip') as z:
 assert z.testzip() is None;index=read('RAW_CORE_INDEX.json');assert set(z.namelist())==set(index)
 for n,v in index.items():assert sha(z.read(n))==v['sha256'] and len(z.read(n))==v['bytes']
 for mode in ['DISCOVERY','REGRESSION']:
  ctx=json.loads(z.read(mode+'/CONTEXT.json'));result=json.loads(z.read(mode+'/RESULT.json'));assert ctx['sourceCommit']==result['sourceCommit']==S;assert ctx['inputTrees']==result['inputTrees'];assert ctx['sourceRootTree']==subprocess.check_output(['git','rev-parse',S+'^{tree}'],cwd=R,text=True).strip();assert sum(ctx['exclusions'].values())==16 and len(ctx['exclusions'])==4;assert sha(z.read('run.py'))==ctx['runnerSha256']
  for n,h in ctx['inputTrees'].items():assert subprocess.check_output(['git','rev-parse',S+':'+n],cwd=R,text=True).strip()==h
  for n,h in ctx['initScriptSha256'].items():assert sha(z.read('INIT/'+Path(n).name))==h
  rows={}
  for n in z.namelist():
   if not n.startswith(mode+'/XML/') or not n.endswith('.xml'):continue
   suite=ET.fromstring(z.read(n));stamp=datetime.fromisoformat(suite.get('timestamp').replace('Z','+00:00'));stamp=stamp.replace(tzinfo=timezone.utc) if stamp.tzinfo is None else stamp;assert stamp.timestamp()>=datetime.fromisoformat(ctx['startedUtc']).timestamp()-1
   for t in suite.findall('testcase'):
    k=(t.get('classname'),t.get('name'));assert k not in rows;rows[k]='FAILED' if t.find('failure') is not None else 'ERROR' if t.find('error') is not None else 'SKIPPED' if t.find('skipped') is not None else 'PASSED'
  events=[json.loads(x) for x in z.read(mode+'/EVENTS.jsonl').splitlines()];events=[x for x in events if x['kind']=='RESULT'];assert len(events)==len(rows)==14;assert rows=={(x['testClass'],x['method']):x['outcome'] for x in result['tests']};assert {(x['className'],x['name']):x['resultType'] for x in events}=={k:{'PASSED':'SUCCESS','FAILED':'FAILURE','ERROR':'FAILURE','SKIPPED':'SKIPPED'}[v] for k,v in rows.items()};assert set(k[0] for k in rows)==set(ctx['selectors']);counts=dict(reported=len(rows),passed=list(rows.values()).count('PASSED'),failed=list(rows.values()).count('FAILED'),errors=list(rows.values()).count('ERROR'),skipped=list(rows.values()).count('SKIPPED'));assert counts==result['testCounts'];done=json.loads(z.read(mode+'_COMPLETION.json'));assert done['runnerExit']==result['exitCode']==int(done['gradleExit'])==int(z.read(mode+'/RUN.exit'))
  for n,h in json.loads(z.read(mode+'/SHA256.json')).items():assert sha(z.read(mode+'/'+n))==h
for n,h in p['preexistingTestFiles'].items():assert sha(blob(S,n))==h
for n,h in p['currentRecordHashes'].items():assert sha(blob(S,n))==h
for n,h in p['archiveSha256'].items():
 assert sha((R/n).read_bytes())==h
 with zipfile.ZipFile(R/n) as z:assert z.testzip() is None
with zipfile.ZipFile(D/'ORIGINAL_PRODUCTION.zip') as z:
 assert z.testzip() is None
 for n,h in p['originalProductionSha256'].items():assert sha(z.read(n))==h and z.read(n)==blob(p['parentSource'],n)
k='app/src/main/java/dev/russiancompanion/v2/curriculum/V2CumulativeReview.kt';objects={m.group(1):m.group() for m in re.finditer(r'internal object (\w+CumulativeReviewPolicy) \{.*?^\}',blob(S,k).decode(),re.M|re.S)};assert len(objects)==36 and all(sha(objects[n].encode())==h for n,h in p['oldPolicyObjectSha256'].items());asset='app/src/main/assets/content/v2-curriculum.json';assert blob(S,asset)==blob(p['parentSource'],asset).replace(b'unit9-prior-reading-plural-gender-v2',b'unit9-prior-noun-behavior-plural-gender-v3')
with zipfile.ZipFile(D/'VERIFIED_TEST_SOURCES.zip') as z:
 for n in z.namelist():assert z.read(n)==blob(S,n)
with zipfile.ZipFile(D/'INITIAL_COMPILATION.zip') as z:
 assert z.testzip() is None
 for n,v in read('INITIAL_COMPILATION_INDEX.json').items():assert sha(z.read(n))==v['sha256'] and len(z.read(n))==v['bytes']
initial=read('INITIAL_COMPILATION_RESULT.json');assert initial['JUnitMethodsExecuted']==0 and not initial['freshDiscoveryResult']
assert blob(initial['sourceCommit'],'app/src/main/java/dev/russiancompanion/v2/curriculum/V2CumulativeReview.kt')==blob(S,'app/src/main/java/dev/russiancompanion/v2/curriculum/V2CumulativeReview.kt')
for name in ['Unit9NounBehaviorReviewBaselineTest','Unit9NounBehaviorCompatibilityTest','Unit9NounBehaviorReviewUiTest']:
 n='app/src/test/java/dev/russiancompanion/v2/'+name+'.kt';assert blob(initial['sourceCommit'],n)==blob(S,n)
identity=read('OFFICIAL_SOURCE_DIGEST.json');assert identity['sourceCommit']==S and identity['inputTrees']==r['inputTrees'];assert identity['canonicalSourceDigest']==read('CONSERVATION.json')['officialDigest'] and identity['canonicalSourceDigest'] in (D/'OFFICIAL_DIGEST.txt').read_text();assert identity['method']=='ReleaseSourceDigestKt.computeReleaseSourceDigest(root,Collections.emptyMap())';assert all(x['exitCode']==0 for x in identity['commands'])
for n,h in identity['helperSha256'].items():assert sha((D/n).read_bytes())==h
obs=read('CURRENT_POLICY_OBSERVATION.json');assert obs['sourceCommit']==S and (obs['activePolicies'],obs['objectivePools'],obs['candidatePlacements'],obs['uniqueCandidateItems'],obs['printSingletonPolicies'])==(28,59,93,48,23);assert obs['unitsWithPrintSingleton']==[3,4]+list(range(10,31));assert next(x for x in obs['policies'] if x['unit']==9)['schema']==11
print(json.dumps(dict(verified=True,sourceCommit=S,counts=r['testCounts'],classes=r['classes'],exitCode=r['exitCode'],oldPoliciesConserved=35,priorTestFilesConserved=len(p['preexistingTestFiles']),archivesConserved=70,recordsStaleUnchanged=155,lint=r['lintIssuesBySeverity'],gate=r['tasks']['verifyReleaseGateAttachment'],initialCompilationJUnitExecutions=0,newRuntimeMethodsExecutedByVerifier=0,completeSuite=False,all48Findings='OPEN',audit='INCOMPLETE',physicalAcceptance='OUTSTANDING_LAST')))
