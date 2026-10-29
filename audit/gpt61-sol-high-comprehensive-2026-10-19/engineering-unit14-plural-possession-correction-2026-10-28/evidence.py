"""Preserve and verify actual Unit14 core results. No test launch or source mutation."""
from pathlib import Path
from datetime import datetime,timezone
import hashlib,json,re,shutil,subprocess,sys,xml.etree.ElementTree as ET,zipfile
R=Path('/workspace/russian-companion');W=Path('/workspace/artifacts/unit14-plural-possession-correction-2026-10-28')
D=R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit14-plural-possession-correction-2026-10-28'
sha=lambda b:hashlib.sha256(b).hexdigest()
git=lambda *a:subprocess.check_output(['git',*a],cwd=R)
read=lambda n:json.loads((D/n).read_text())
def save(n,v):(D/n).write_text(json.dumps(v,indent=2)+'\n')
def makezip(name,files):
    index=[]
    with zipfile.ZipFile(D/name,'x',zipfile.ZIP_DEFLATED) as z:
        for n,p in sorted(files.items()):
            assert not n.lower().endswith('.pdf');data=p.read_bytes() if isinstance(p,Path) else p
            z.writestr(n,data);index.append(dict(path=n,bytes=len(data),sha256=sha(data)))
    save(name.removesuffix('.zip')+'_INDEX.json',index)
def checkzip(name):
    with zipfile.ZipFile(D/name) as z:
        assert z.testzip() is None
        idx=read(name.removesuffix('.zip')+'_INDEX.json');assert set(z.namelist())=={x['path'] for x in idx}
        for x in idx:assert sha(z.read(x['path']))==x['sha256'] and len(z.read(x['path']))==x['bytes'],x['path']
def conserve(c,current):
    prep=read('PREPARATION.json');base=prep['baselineSourceCommit'];S=c['sourceCommit']
    for p,h in c['inputTrees'].items():assert git('rev-parse',S+':'+p).decode().strip()==h,p
    for p,h in prep['inputTrees'].items():
        if p not in ['app/src/main','app/src/test']:assert c['inputTrees'][p]==h,p
    tests=read('EXISTING_TEST_HASHES.json');assert len(tests)==277
    for p,h in tests.items():assert sha(git('show',S+':'+p))==h,p
    assert len(git('ls-tree','-r','--name-only',S,'app/src/test').decode().splitlines())==280
    assert set(git('diff','--name-only',base,S,'--','app/src/test').decode().splitlines())==set(prep['newTestFiles'])
    assert set(git('diff','--name-only',base,S,'--','app/src/main').decode().splitlines())==set(prep['changedMainFiles'])
    proof=read('OLD_OBJECT_AND_SCHEMA_PROOF.json');kernel='app/src/main/java/dev/russiancompanion/v2/curriculum/V2CumulativeReview.kt'
    text=git('show',S+':'+kernel).decode();objects=dict(re.findall(r'(?m)^internal object (\w+) \{\n(.*?)^\}',text,re.S))
    assert len(objects)==41
    for n,h in proof['oldObjects'].items():assert sha(objects[n].encode())==h,n
    constants=dict(re.findall(r'^internal const val (\w+) = (\d+)$',text,re.M))
    for n,v in proof['oldConstants'].items():assert constants[n]==v,n
    asset='app/src/main/assets/content/v2-curriculum.json'
    assert git('show',S+':'+asset).replace(b'unit14-prior-plural-possession-reported-time-v2',b'unit14-prior-reading-reported-time-v1')==git('show',base+':'+asset)
    for p,x in prep['changedMainFiles'].items():assert sha(git('show',S+':'+p))==x['after'],p
    for p,h in prep['newTestFiles'].items():assert sha(git('show',S+':'+p))==h,p
    records=read('SOURCE_BINDING_HASHES.json');assert len(records)==155
    for p,h in records.items():assert sha(git('show',S+':'+p))==h,p
    protected=read('PROTECTED_EVIDENCE_HASHES.json');exceptions=set(protected)&set(prep['changedMainFiles'])
    if current:
        for p,h in {**protected,**tests,**prep['newTestFiles'],**records}.items():
            if p not in exceptions:assert sha((R/p).read_bytes())==h,p
        for p,h in c['inputTrees'].items():assert git('rev-parse','HEAD:'+p).decode().strip()==h,p
        for p,x in prep['changedMainFiles'].items():assert sha((R/p).read_bytes())==x['after'],p
    archives=read('PROTECTED_ARCHIVE_INVENTORY.json');assert len(archives)==77
    for a in archives:
        data=(R/a['path']).read_bytes() if current else git('show',S+':'+a['path'])
        assert sha(data)==a['sha256'] and len(data)==a['bytes'],a['path']
        import io
        with zipfile.ZipFile(io.BytesIO(data)) as z:assert z.testzip() is None,a['path']
    assert not any(p.lower().endswith('.pdf') for p in git('ls-tree','-r','--name-only',S).decode().splitlines())
    return dict(sourceCommit=S,baselineParent=base,inputTrees=c['inputTrees'],priorTestFilesUnchanged=277,wholeUnit14BaselineUnchanged=True,newTestFiles=3,newJUnitMethods=18,selectedMethods=20,oldPolicyObjectsBytePreserved=40,newPolicyObjects=1,currentPolicyObjects=41,oldSchemas=list(range(1,16)),newSchema=16,protectedArchivesShaCrc=77,protectedFiles=len(protected),authorizedProtectedProductionMutations=sorted(exceptions),sourceBindingsUnchanged=155,sourceBindingsStatus='STALE_UNCHANGED_NOT_REGENERATED',apkIdentity=None,apkSignature=None,all48Findings='OPEN',audit='INCOMPLETE',physicalAcceptance='OUTSTANDING_LAST',environmentSettingsChanged=False,completeSuite=False,scopesNotSummed=True)
def check_raw():
    contexts={};results={};checkzip('RAW_CORE.zip')
    with zipfile.ZipFile(D/'RAW_CORE.zip') as z:
        for mode in ['DISCOVERY','REGRESSION']:
            c=json.loads(z.read(mode+'/CONTEXT.json'));r=json.loads(z.read(mode+'/RESULT.json'))
            contexts[mode]=c;results[mode]=r;S=c['sourceCommit']
            assert c==read(mode+'_CONTEXT.json') and r==read(mode+'_RESULT.json')
            assert S==r['sourceCommit'] and c['sourceRootTree']==git('rev-parse',S+'^{tree}').decode().strip()
            assert c['inputTrees']==r['inputTrees'] and c['workingTreeAtStart']=='CLEAN'
            assert c['completeSuite']==r['completeSuite']==False
            assert c['exclusions']=={'VisualReviewCaptureTest':2,'Units35ChunkCaptureTest':2,'Units610ChunkCaptureTest':10,'Units1115GapCaptureTest':2}
            done=json.loads(z.read(mode+'_COMPLETION.json'))
            assert done['runnerExit']==r['exitCode']==int(done['gradleExit'])==int(z.read(mode+'/RUN.exit'))
            assert done['resultExists'] and done['manifestExists']
            for p,h in read(mode+'_SHA256.json').items():assert sha(z.read(mode+'/'+p))==h,(mode,p)
            for p,h in c['initScriptSha256'].items():assert sha(z.read('INIT/'+Path(p).name))==h
            xmls=[p for p in z.namelist() if p.startswith(mode+'/XML/') and p.endswith('.xml')];assert len(xmls)==3
            tests=[];outputs=[]
            for p in xmls:
                suite=ET.fromstring(z.read(p));tests+=suite.findall('testcase');outputs.append(suite.findtext('system-out',''))
                stamp=datetime.fromisoformat(suite.get('timestamp').replace('Z','+00:00'))
                if stamp.tzinfo is None:stamp=stamp.replace(tzinfo=timezone.utc)
                assert stamp.timestamp()>=datetime.fromisoformat(c['startedUtc']).timestamp()-1
            er=[json.loads(s) for s in z.read(mode+'/EVENTS.jsonl').decode().splitlines() if json.loads(s)['kind']=='RESULT']
            names={(x.get('classname'),x.get('name')) for x in tests}
            assert len(names)==len(tests)==len(er)==20 and names=={(x['className'],x['name']) for x in er}
            assert {x[0] for x in names}==set(c['selectors'])
            counts=dict(reported=20,passed=0,failed=0,errors=0,skipped=0)
            for t in tests:
                status='FAILED' if t.find('failure') is not None else 'ERROR' if t.find('error') is not None else 'SKIPPED' if t.find('skipped') is not None else 'PASSED'
                counts[{'FAILED':'failed','ERROR':'errors','SKIPPED':'skipped','PASSED':'passed'}[status]]+=1
                row=next(x for x in r['tests'] if x['testClass']==t.get('classname') and x['method']==t.get('name'))
                element=next((t.find(k) for k in ['failure','error','skipped'] if t.find(k) is not None),None)
                msg=element.get('message','') if element is not None else ''
                assert row['outcome']==status and row['fullMessageSha256']==sha(msg.encode()) and row['fullMessageBytes']==len(msg.encode())
                event=next(x for x in er if x['className']==t.get('classname') and x['name']==t.get('name'))
                assert event['resultType']=={'PASSED':'SUCCESS','FAILED':'FAILURE','ERROR':'FAILURE','SKIPPED':'SKIPPED'}[status]
            assert counts==r['testCounts'] and r['classes']==3
            if mode=='DISCOVERY':assert counts==dict(reported=20,passed=0,failed=0,errors=0,skipped=20) and r['exitCode']==0
            else:
                assert r['discoveryComparison']['sameInputTrees'] and r['discoveryComparison']['exactSameMethodSet']
                assert r['selectedFreshXmlAndEventsMatch'] and r['eventOutcomeCountsMatchXml']
                if (D/'CURRENT_POLICY_OBSERVATION.json').exists():
                    o=read('CURRENT_POLICY_OBSERVATION.json');assert o==json.loads(z.read('REGRESSION/CURRENT_POLICY_OBSERVATION.json'))
                    assert any('UNIT14_COMPILED_OBSERVATION '+json.dumps(o,separators=(',',':'),ensure_ascii=False) in s for s in outputs)
                if r['testCounts']['failed']==r['testCounts']['errors']==0:
                    assert r['exitCode']==0 and r['testCounts']['passed']==20
                    assert any('UNIT14_ALLOCATION_VARIANTS count=24' in s for s in outputs)
                if 'REGRESSION/LINT.xml' in z.namelist():
                    issues=ET.fromstring(z.read('REGRESSION/LINT.xml')).findall('issue')
                    assert r['lintIssuesBySeverity']=={s:sum(x.get('severity')==s for x in issues) for s in ['Fatal','Error','Warning','Hint']}
        assert contexts['DISCOVERY']['sourceCommit']==contexts['REGRESSION']['sourceCommit']
        assert contexts['DISCOVERY']['inputTrees']==contexts['REGRESSION']['inputTrees']
    return contexts['REGRESSION'],results['REGRESSION']
def collect():
    assert not git('status','--porcelain=v1','-z')
    c=json.loads((W/'REGRESSION/CONTEXT.json').read_text());r=json.loads((W/'REGRESSION/RESULT.json').read_text());S=c['sourceCommit']
    assert git('rev-parse','HEAD').decode().strip()==S
    files={p.relative_to(W).as_posix():p for mode in ['DISCOVERY','REGRESSION'] for p in (W/mode).rglob('*') if p.is_file()}
    for mode in ['DISCOVERY','REGRESSION']:
        for n in ['CONTEXT.json','RESULT.json','RESULTS.csv','RUN.exit','SHA256.json']:shutil.copy2(W/mode/n,D/(mode+'_'+n))
        for n in ['COMPLETION.json','LAUNCHER.json','RUNNER_PROCESS.json','LAUNCHER.exit','RUNNER.log','LAUNCHER.log']:files[mode+'_'+n]=W/(mode+'_'+n)
    for n in ['run.py','resources.gradle','launch.py','supervise.py','prepare.py']:files['TOOLING/'+n]=W/n
    for p,h in c['initScriptSha256'].items():assert sha(Path(p).read_bytes())==h;files['INIT/'+Path(p).name]=Path(p)
    makezip('RAW_CORE.zip',files)
    # All actual failures are retained before any success assertion or correction.
    save('ACTUAL_FAILURES.json',dict(sourceCommit=S,count=sum(x['outcome'] in ['FAILED','ERROR'] for x in r['tests']),tests=[x for x in r['tests'] if x['outcome'] in ['FAILED','ERROR']],originalAssertionsPreserved=True))
    obs=W/'REGRESSION/CURRENT_POLICY_OBSERVATION.json'
    if obs.exists():shutil.copy2(obs,D/obs.name)
    records=json.loads((R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit13-source-reconciliation-2026-10-27/APPLIED_RECORDS.json').read_text())['currentRecordHashes']
    save('SOURCE_BINDING_HASHES.json',records)
    c,r=check_raw();save('CONSERVATION.json',conserve(c,True))
    prep=read('PREPARATION.json');makezip('ORIGINAL_PRODUCTION.zip',{p:git('show',prep['baselineSourceCommit']+':'+p) for p in prep['changedMainFiles']})
    baseTest='app/src/test/java/dev/russiancompanion/v2/Unit14PluralPossessionReviewBaselineTest.kt'
    makezip('VERIFIED_TEST_SOURCES.zip',{p:(R/p) for p in [*prep['newTestFiles'],baseTest]})
    classRoot=R/'app/build/intermediates/built_in_kotlinc/debug/compileDebugKotlin/classes'
    classes={p.relative_to(classRoot).as_posix():p for p in (classRoot/'dev/russiancompanion/v2/curriculum').rglob('*.class')}
    assert 'dev/russiancompanion/v2/curriculum/Unit14PluralPossessionCumulativeReviewPolicy.class' in classes
    makezip('COMPILED_CURRICULUM.zip',classes)
    save('COMPILED_IDENTITY.json',dict(sourceCommit=S,inputTrees=c['inputTrees'],classRoot=str(classRoot),classes=len(classes),scope='Actual debug curriculum bytecode after matching fresh discovery compilation and selected runtime;no extra JVM/render/regeneration/APK run',index='COMPILED_CURRICULUM_INDEX.json'))
    if obs.exists():
        o=read('CURRENT_POLICY_OBSERVATION.json');assert (o['activePolicies'],o['pools'],o['placements'],o['uniqueCandidates'],o['printSingletonPolicies'])==(28,60,121,57,18)
        assert o['unit14BeforeV1Digest']=='9e136e9af2fe3611d6862fa46e3217f85ad45c8b23ad622eec6f36e48ab97f38'
        save('OFFICIAL_DIGEST_IDENTITIES.json',dict(sourceCommit=S,repositorySourceDigest=o['repositorySourceDigest'],packContentReviewDigest=o['packContentReviewDigest'],unit14CurrentDigest=o['unit14CurrentDigest'],unit14PriorV1Digest=o['unit14BeforeV1Digest'],producer='Actual JUnit method calls production computeReleaseSourceDigest and V2Validator.contentReviewDigest/unitContentReviewDigest;raw output unchanged',compiledClasses=len(classes),sourceBindings='155_STALE_UNCHANGED_PENDING_AUTHENTIC_RECONCILIATION',twoRenders='NOT_RUN'))
    shutil.copy2(__file__,D/'evidence.py')
    print(json.dumps(dict(collected=True,sourceCommit=S,counts=r['testCounts'],exitCode=r['exitCode'],classes=3,compiledObservation=obs.exists(),compiledCurriculumClasses=len(classes),bindings='155_STALE_UNCHANGED')))
def verify(current):
    for n,h in read('SHA256.json').items():assert sha((D/n).read_bytes())==h,n
    c,r=check_raw();assert conserve(c,current)==read('CONSERVATION.json')
    for n in ['INITIAL_DISCOVERY.zip','ORIGINAL_PRODUCTION.zip','VERIFIED_TEST_SOURCES.zip','COMPILED_CURRICULUM.zip']:checkzip(n)
    with zipfile.ZipFile(D/'VERIFIED_TEST_SOURCES.zip') as z:
        for p in z.namelist():assert z.read(p)==git('show',c['sourceCommit']+':'+p)
    proof=read('SELECTION_PREFLIGHT.json');assert proof['runtimeRunsBeforeCorrection']==0 and proof['assertionsChanged']==False
    assert read('COMPILED_IDENTITY.json')['inputTrees']==c['inputTrees']
    failures=[x for x in r['tests'] if x['outcome'] in ['FAILED','ERROR']]
    assert read('ACTUAL_FAILURES.json')['tests']==failures
    print(json.dumps(dict(verified=True,sourceCommit=c['sourceCommit'],currentConservation=current,counts=r['testCounts'],exitCode=r['exitCode'],classes=3,oldTestFilesConserved=277,oldObjectsConserved=40,oldSchemasConserved=15,protectedArchivesShaCrc=77,sourceBindings='155_STALE_UNCHANGED',repositorySourceDigest=read('OFFICIAL_DIGEST_IDENTITIES.json')['repositorySourceDigest'],packContentReviewDigest=read('OFFICIAL_DIGEST_IDENTITIES.json')['packContentReviewDigest'],completeSuite=False,all48Findings='OPEN',audit='INCOMPLETE',physicalAcceptance='OUTSTANDING_LAST')))
if __name__=='__main__':
    assert sys.argv[1:] in [['collect'],['verify'],['verify','--current']]
    if sys.argv[1]=='collect':collect()
    else:verify('--current' in sys.argv)
