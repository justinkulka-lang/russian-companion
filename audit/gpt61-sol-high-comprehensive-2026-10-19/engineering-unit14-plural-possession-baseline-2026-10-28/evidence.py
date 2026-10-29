"""Collect or independently verify the pinned Unit14 baseline. Never runs app tests."""
from pathlib import Path
from datetime import datetime,timezone
from zoneinfo import ZoneInfo
import hashlib,json,shutil,subprocess,sys,xml.etree.ElementTree as ET,zipfile
R=Path('/workspace/russian-companion');W=Path('/workspace/artifacts/unit14-plural-possession-baseline-2026-10-28')
D=R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit14-plural-possession-baseline-2026-10-28'
sha=lambda b:hashlib.sha256(b).hexdigest()
git=lambda *a:subprocess.check_output(['git',*a],cwd=R)
read=lambda n:json.loads((D/n).read_text())
def save(n,v):(D/n).write_text(json.dumps(v,indent=2)+'\n')
positive='actualCurrentV1AndDownstreamUnit15RoundtripReopenAndCompleteTheirOwnObligations'
contrast='earlierPluralPossessionAndOptionalAnswersCannotReplaceFreshAssignedDueAnswers'
klass='dev.russiancompanion.v2.Unit14PluralPossessionReviewBaselineTest'
exclusions={'VisualReviewCaptureTest':2,'Units35ChunkCaptureTest':2,'Units610ChunkCaptureTest':10,'Units1115GapCaptureTest':2}
failureMessage='The proposed Unit14 obligation must refuse advancement without its fresh assigned plural and possession answers'
captureNames=['PARTIAL_UNIT14_V1','COMPLETED_UNIT14_V1','PARTIAL_UNIT15_OWNER14_V1']
def archive_members(z):
    assert z.testzip() is None
    return [dict(path=i.filename,bytes=i.file_size,crc32=f'{i.CRC:08x}',sha256=sha(z.read(i))) for i in z.infolist()]
def check_raw():
    prep=read('PREPARATION.json');contexts={};results={}
    with zipfile.ZipFile(D/'RAW_BASELINE.zip') as z:
        index=read('RAW_BASELINE_INDEX.json');assert z.testzip() is None
        assert set(z.namelist())=={x['path'] for x in index}
        for x in index:assert len(z.read(x['path']))==x['bytes'] and sha(z.read(x['path']))==x['sha256'],x['path']
        for mode in ['DISCOVERY','REGRESSION']:
            c=json.loads(z.read(mode+'/CONTEXT.json'));r=json.loads(z.read(mode+'/RESULT.json'))
            contexts[mode]=c;results[mode]=r
            assert c==read(mode+'_CONTEXT.json') and r==read(mode+'_RESULT.json')
            S=c['sourceCommit'];assert S==r['sourceCommit'] and c['sourceRootTree']==git('rev-parse',S+'^{tree}').decode().strip()
            assert c['workingTreeAtStart']=='CLEAN' and c['selectors']==[klass]
            assert c['exclusions']==exclusions and c['completeSuite']==r['completeSuite']==False
            assert c['inputTrees']==r['inputTrees']
            for p,h in c['inputTrees'].items():assert git('rev-parse',S+':'+p).decode().strip()==h
            for p,h in prep['inputTrees'].items():
                if p!='app/src/test':assert c['inputTrees'][p]==h,p
            for p,h in c['initScriptSha256'].items():assert sha(z.read('INIT/'+Path(p).name))==h
            done=json.loads(z.read(mode+'_COMPLETION.json'))
            assert done['runnerExit']==int(done['gradleExit'])==int(z.read(mode+'/RUN.exit'))==r['exitCode']
            assert done['resultExists'] and done['manifestExists'] and done['sourceCommit']==S
            assert json.loads(z.read(mode+'/SHA256.json'))==read(mode+'_SHA256.json')
            for p,h in read(mode+'_SHA256.json').items():assert sha(z.read(mode+'/'+p))==h,(mode,p)
            xmls=[p for p in z.namelist() if p.startswith(mode+'/XML/') and p.endswith('.xml')];assert len(xmls)==1
            suite=ET.fromstring(z.read(xmls[0]));tests=suite.findall('testcase')
            stamp=datetime.fromisoformat(suite.get('timestamp').replace('Z','+00:00'))
            if stamp.tzinfo is None:stamp=stamp.replace(tzinfo=timezone.utc)
            assert stamp.timestamp()>=datetime.fromisoformat(c['startedUtc']).timestamp()-1
            events=[json.loads(s) for s in z.read(mode+'/EVENTS.jsonl').decode().splitlines()]
            events=[x for x in events if x['kind']=='RESULT'];assert len(tests)==len(events)==len(r['tests'])==2
            assert {(x.get('classname'),x.get('name')) for x in tests}=={(klass,positive),(klass,contrast)}
            for t in tests:
                name=t.get('name');row=next(x for x in r['tests'] if x['method']==name)
                element=next((t.find(k) for k in ['failure','error','skipped'] if t.find(k) is not None),None)
                outcome='FAILED' if t.find('failure') is not None else 'ERROR' if t.find('error') is not None else 'SKIPPED' if t.find('skipped') is not None else 'PASSED'
                msg=element.get('message','') if element is not None else ''
                assert row['outcome']==outcome and row['fullMessageSha256']==sha(msg.encode()) and row['fullMessageBytes']==len(msg.encode())
                event=next(x for x in events if x['name']==name and x['className']==klass)
                assert event['resultType']=={'PASSED':'SUCCESS','FAILED':'FAILURE','ERROR':'FAILURE','SKIPPED':'SKIPPED'}[outcome]
            expected=dict(reported=2,passed=0,failed=0,errors=0,skipped=2) if mode=='DISCOVERY' else dict(reported=2,passed=1,failed=1,errors=0,skipped=0)
            assert r['testCounts']==expected and r['classes']==1 and r['exitCode']==(0 if mode=='DISCOVERY' else 1)
            if mode=='REGRESSION':
                by={x['method']:x for x in r['tests']};assert by[positive]['outcome']=='PASSED' and by[contrast]['outcome']=='FAILED'
                assert failureMessage in by[contrast]['messagePreview']
                assert 'actualAdvanceWithoutNewPluralPossession=true' in suite.findtext('system-out','')
                assert r['discoveryComparison']['sameInputTrees'] and r['discoveryComparison']['exactSameMethodSet']
                issues=ET.fromstring(z.read('REGRESSION/LINT.xml')).findall('issue')
                assert r['lintIssuesBySeverity']=={s:sum(x.get('severity')==s for x in issues) for s in ['Fatal','Error','Warning','Hint']}
                assert r['lintIssuesBySeverity']['Fatal']==r['lintIssuesBySeverity']['Error']==0
                log=z.read('REGRESSION/RUN.log').decode()
                assert '> Task :app:verifyReleaseGateAttachment\n' in log and '> Task :app:verifyReleaseGateAttachment FAILED' not in log
                assert '> Task :app:lintDebug\n' in log and '> Task :app:lintDebug FAILED' not in log
                for n in captureNames:
                    for suffix in ['.zip','.IDENTITY.json']:assert z.read('REGRESSION/CAPTURE/'+n+suffix)==(D/(n+suffix)).read_bytes()
                for n in ['COMPLETED_UNIT15_V1.json','LIFECYCLE_VERIFIED.json']:assert z.read('REGRESSION/CAPTURE/'+n)==(D/n).read_bytes()
    assert contexts['DISCOVERY']['sourceCommit']==contexts['REGRESSION']['sourceCommit']
    assert contexts['DISCOVERY']['inputTrees']==contexts['REGRESSION']['inputTrees']
    return contexts['REGRESSION'],results['REGRESSION']
def inspect_captures(c):
    prep=read('PREPARATION.json');S=c['sourceCommit'];base=prep['baselineSourceCommit']
    pack=json.loads(git('show',S+':app/src/main/assets/content/v2-curriculum.json'))
    units={x['number']:x for x in pack['units']};items={x['id']:x for x in pack['items']}
    documents={};observations={}
    for n in captureNames:
        path=D/(n+'.zip');raw=path.read_bytes();identity=read(n+'.IDENTITY.json')
        assert identity['producerCommit']==S and identity['curriculumSourceCommit']==base
        assert identity['rawCurriculumSHA256']==sha(git('show',S+':app/src/main/assets/content/v2-curriculum.json'))
        assert identity['zipSHA256']==sha(raw) and identity['zipBytes']==len(raw)
        with zipfile.ZipFile(path) as z:
            members=archive_members(z);assert set(z.namelist())=={'manifest.json','learner.json'}
            m=json.loads(z.read('manifest.json'));d=json.loads(z.read('learner.json'))
            for f in m['files']:assert f['bytes']==len(z.read(f['path'])) and f['sha256']==sha(z.read(f['path']))
        target=15 if 'UNIT15' in n else 14;profile=15 if n!='PARTIAL_UNIT14_V1' else 14
        completedThrough=14 if profile==15 else 13
        a=next(x for x in d['reviewAssignments'] if x['targetUnit']==target)
        answers=[x for x in d['reviewAnswers'] if x['assignmentId']==a['id']]
        assert a['schema']==identity['assignmentSchema']==1 and a['policyId']==identity['policyId']
        assert a['digest']==identity['assignmentDigest'] and a['policyDigest']==identity['policyDigest']
        assert m['formatVersion']==identity['backupFormatVersion']==3 and d['schemaVersion']==identity['documentSchemaVersion']==3
        assert d['recordings']==[] and m['recordingCount']==identity['recordingCount']==0
        assert d['profile']['currentUnitNumber']==identity['profileUnit']==profile
        assert sorted(x['unitNumber'] for x in d['unitCompletions'])==list(range(1,completedThrough+1))
        assert [x['ownerUnit'] for x in a['slots']]==([1,14] if target==15 else [1,13])
        assert [x['objectiveId'] for x in a['slots']]==(['obj-u1-decoding','obj-u14-impersonal'] if target==15 else ['obj-u1-decoding','obj-u13-reported'])
        assert [x['itemId'] for x in a['slots']]==['u1-i-decode-pair','u14-i-impersonal-2' if target==15 else 'u13-i-report-frame-time']
        expectedSlots={0,1} if n=='COMPLETED_UNIT14_V1' else {0}
        assert {x['slotIndex'] for x in answers}==expectedSlots and len(answers)==len(expectedSlots)
        for x in answers:
            assert x['schema']==1 and x['role']=='CUMULATIVE_REVIEW' and x['assignmentDigest']==a['digest']
            assert x['acceptedOrder']>a['boundary'] and x['selected']==items[a['slots'][x['slotIndex']]['itemId']]['answer']
        prereqs={x['targetUnit']:x for x in d['reviewAssignments']}
        assert [(prereqs[k]['policyId'],prereqs[k]['schema']) for k in [11,12,13]]==[
            ('unit11-prior-noun-behavior-possession-absence-v2',13),('unit12-prior-gender-past-gender-v2',14),('unit13-prior-present-aspect-past-gender-v3',15)]
        ordinary=[]
        for k in range(1,target+1):
            steps=[s for session in units[k]['sessions'] for s in session['steps']]
            ids={s['id'] for s in steps};assert ids<={s['stepId'] for s in d['steps']}
            assert k in {x['unitNumber'] for x in d['bookAttestations']}
            for s in steps:
                for iid in s.get('itemIds',[]):
                    matched=[x for x in d['attempts'] if x['itemId']==iid and x['stepId']==s['id'] and x['attemptRole']=='PRESCRIBED_CHECK']
                    assert matched and all(x['correct'] and x['selected']==items[iid]['answer'] and x['acceptedOrder']>0 and x['identitySchema']>0 for x in matched),(k,s['id'],iid)
            ordinary.append(dict(unit=k,stepsCompleted=len(ids),checkIds=list(dict.fromkeys(i for s in steps if s['kind']=='CHECK' for i in s.get('itemIds',[]))),guidedIds=list(dict.fromkeys(i for s in steps if s['kind']=='GUIDED' for i in s.get('itemIds',[]))),bookAttested=True))
        if n=='COMPLETED_UNIT14_V1':
            row=next(x for x in d['unitCompletions'] if x['unitNumber']==14)
            assert row['reviewAssignmentId']==a['id'] and row['reviewPolicyDigest']==a['policyDigest']
            assert row['reviewCompletionOrder']==max(x['acceptedOrder'] for x in answers)>a['boundary']
        else:assert not any(x['unitNumber']==target for x in d['unitCompletions'])
        documents[n]=d
        observations[n]=dict(path=path.relative_to(R).as_posix(),zipBytes=len(raw),zipSha256=sha(raw),zipCrcPassed=True,members=members,backupFormatVersion=m['formatVersion'],documentSchemaVersion=d['schemaVersion'],assignmentSchema=a['schema'],answerRecordSchemas=sorted({x['schema'] for x in answers}),assignment=a,answers=answers,profile=d['profile'],recordingCount=len(d['recordings']),unitCompletions=d['unitCompletions'],prerequisitePolicies={str(k):dict(policyId=prereqs[k]['policyId'],schema=prereqs[k]['schema']) for k in [11,12,13]},ordinaryUnits=ordinary,syntheticLearner=True,oldArchiveRelabelled=False)
    partial=documents['PARTIAL_UNIT14_V1'];completed=documents['COMPLETED_UNIT14_V1'];downstream=documents['PARTIAL_UNIT15_OWNER14_V1']
    a=next(x for x in partial['reviewAssignments'] if x['targetUnit']==14)
    assert next(x for x in completed['reviewAssignments'] if x['targetUnit']==14)==a
    assert next(x for x in downstream['reviewAssignments'] if x['targetUnit']==14)==a
    assert completed['unitCompletions']==downstream['unitCompletions']
    assert all(x in completed['reviewAnswers'] for x in partial['reviewAnswers'])
    final=read('COMPLETED_UNIT15_V1.json');task=next(x for x in downstream['reviewAssignments'] if x['targetUnit']==15)
    assert next(x for x in final['reviewAssignments'] if x['targetUnit']==15)==task
    assert final['profile']['currentUnitNumber']==16 and sorted(x['unitNumber'] for x in final['unitCompletions'])==list(range(1,16))
    assert all(x in final['reviewAnswers'] for x in downstream['reviewAnswers'])
    answers=[x for x in final['reviewAnswers'] if x['assignmentId']==task['id']]
    assert len(answers)==2 and {x['slotIndex'] for x in answers}=={0,1}
    row=next(x for x in final['unitCompletions'] if x['unitNumber']==15)
    assert row['reviewAssignmentId']==task['id'] and row['reviewPolicyDigest']==task['policyDigest']
    assert row['reviewCompletionOrder']==max(x['acceptedOrder'] for x in answers)>task['boundary']
    life=read('LIFECYCLE_VERIFIED.json');assert life['producerCommit']==S and life['curriculumSourceCommit']==base
    for k in ['partial14ReadReplaceNamedReopen','completed14ReadReplaceNamedReopen','partial15ReadReplaceNamedReopen','actualAdvance14To15','actualAdvance15To16','completed15NamedReopen']:assert life[k] is True
    return dict(producerCommit=S,curriculumSourceCommit=base,captures=observations,finalUnit15Assignment=task,finalUnit15Answers=answers,finalUnit15Completion=row,finalProfile=final['profile'],finalRecordingCount=len(final['recordings']),lifecycle=life)
def conserve(c,current):
    prep=read('PREPARATION.json');S=c['sourceCommit'];base=prep['baselineSourceCommit'];new=prep['newTestFile']
    assert not git('diff','--name-only',base,S,'--','app/src/main','catalog','docs','review','release')
    assert git('diff','--name-only',base,S,'--','app/src/test').decode().splitlines()==[new]
    old=read('EXISTING_TEST_HASHES.json');assert len(old)==276
    for p,h in old.items():assert sha(git('show',S+':'+p))==h,p
    assert len(git('ls-tree','-r','--name-only',S,'app/src/test').decode().splitlines())==277
    with zipfile.ZipFile(D/'ORIGINAL_BASELINE_TEST.zip') as z:
        assert z.testzip() is None and z.namelist()==[new]
        assert sha(z.read(new))==prep['newTestSha256']==sha(git('show',S+':'+new))
    prior=read('PRIOR_ARCHIVE_INVENTORY.json');assert len(prior)==74
    protected=read('PROTECTED_EVIDENCE_HASHES.json')
    if current:
        for p,h in {**protected,**old}.items():assert sha((R/p).read_bytes())==h,p
        assert sha((R/new).read_bytes())==prep['newTestSha256']
        for p,h in c['inputTrees'].items():assert git('rev-parse','HEAD:'+p).decode().strip()==h
        for a in prior:
            assert sha((R/a['path']).read_bytes())==a['sha256']
            with zipfile.ZipFile(R/a['path']) as z:assert z.testzip() is None
    assert not any(p.lower().endswith('.pdf') for p in git('ls-tree','-r','--name-only',S).decode().splitlines())
    return dict(sourceCommit=S,baselineSourceCommit=base,inputTrees=c['inputTrees'],protectedFilesConserved=len(protected),existingTestsConserved=276,newTestFiles=1,currentTestSourceFiles=277,newMethods=2,priorProtectedArchivesShaCrc=74,newLearnerArchives=3,protectedArchiveInventoryTotal=77,sourceBindingsConserved=155,registeredObjectsConserved=40,schemasConserved=list(range(1,16)),productionCatalogDocsReviewReleaseUnchanged=True,proposal='NOT_ACTIVATED',apkIdentity=None,apkSignature=None,apkAssemblyInstallSigning='NOT_RUN',all48Findings='OPEN',audit='INCOMPLETE',physicalAcceptance='OUTSTANDING_LAST',environmentSettings='UNCHANGED',publication='USER_CONFIRMED_NOT_INDEPENDENTLY_OBSERVED',privatePdf='NOT_READ_OR_PACKAGED',completeSuite=False,scopesNotSummed=True)
def collect():
    assert not git('status','--porcelain=v1','-z')
    assert not (D/'RAW_BASELINE.zip').exists()
    files={p.relative_to(W).as_posix():p for mode in ['DISCOVERY','REGRESSION'] for p in (W/mode).rglob('*') if p.is_file()}
    for mode in ['DISCOVERY','REGRESSION']:
        for suffix in ['COMPLETION.json','LAUNCHER.json','RUNNER_PROCESS.json','LAUNCHER.exit','RUNNER.log','LAUNCHER.log']:files[mode+'_'+suffix]=W/(mode+'_'+suffix)
        for n in ['CONTEXT.json','RESULT.json','RESULTS.csv','RUN.exit','SHA256.json']:shutil.copy2(W/mode/n,D/(mode+'_'+n))
    c=read('REGRESSION_CONTEXT.json');assert git('rev-parse','HEAD').decode().strip()==c['sourceCommit']
    for p,h in c['initScriptSha256'].items():
        assert sha(Path(p).read_bytes())==h;files['INIT/'+Path(p).name]=Path(p)
    for n in ['run.py','resources.gradle','launch.py','supervise.py']:files['RUNNER/'+n]=W/n
    index=[]
    with zipfile.ZipFile(D/'RAW_BASELINE.zip','x',zipfile.ZIP_DEFLATED) as z:
        for n,p in sorted(files.items()):
            assert not n.lower().endswith('.pdf');z.write(p,n);index.append(dict(path=n,bytes=p.stat().st_size,sha256=sha(p.read_bytes())))
    save('RAW_BASELINE_INDEX.json',index)
    # Preserve original raw bytes BEFORE making expected-positive/future-contrast assertions.
    for n in captureNames:
        for s in ['.zip','.IDENTITY.json']:shutil.copy2(W/'REGRESSION/CAPTURE'/(n+s),D/(n+s))
    for n in ['COMPLETED_UNIT15_V1.json','LIFECYCLE_VERIFIED.json']:shutil.copy2(W/'REGRESSION/CAPTURE'/n,D/n)
    c,r=check_raw();cap=inspect_captures(c);con=conserve(c,True)
    save('CAPTURE_VERIFICATION.json',cap);save('CONSERVATION.json',con)
    inventory=read('PRIOR_ARCHIVE_INVENTORY.json')
    for n in captureNames:
        x=cap['captures'][n];inventory.append(dict(path=x['path'],sha256=x['zipSha256'],bytes=x['zipBytes'],members=len(x['members']),crcVerified=True,scope='NEW_GENUINE_CURRENT_LEARNER_CAPTURE'))
    assert len(inventory)==77 and len({x['path'] for x in inventory})==77
    save('PROTECTED_ARCHIVE_INVENTORY.json',inventory)
    protected={**read('PROTECTED_EVIDENCE_HASHES.json'),**{x['path']:x['sha256'] for x in inventory}}
    p=read('PREPARATION.json')['newTestFile'];protected[p]=sha((R/p).read_bytes());save('CURRENT_PROTECTED_EVIDENCE_HASHES.json',protected)
    times={k:datetime.fromisoformat(v).astimezone(ZoneInfo('America/Chicago')).isoformat() for k,v in [('started',c['startedUtc']),('finished',r['finishedUtc'])]}
    save('VERIFICATION_SUMMARY.json',dict(sourceCommit=c['sourceCommit'],testCounts=r['testCounts'],classes=r['classes'],exitCode=r['exitCode'],tasks=r['tasks'],lintIssuesBySeverity=r['lintIssuesBySeverity'],releaseGateAttachment='PASS_ATTACHMENT_ONLY',exactCommand=c['command'],activation=c['activation'],startedUtc=c['startedUtc'],finishedUtc=r['finishedUtc'],americaChicago=times,discovery=read('DISCOVERY_RESULT.json')['testCounts'],discoveryIsRuntime=False,currentDeclaredGateBypassed=False,futureContrast='EXPECTED_FAILURE_PRESERVED',positive='PASSED',exclusions=exclusions,completeSuite=False,scopesNotSummed=True,apkIdentity=None,apkSignature=None,physicalAcceptance='OUTSTANDING_LAST'))
    shutil.copy2(__file__,D/'evidence.py')
    print(json.dumps(dict(collected=True,sourceCommit=c['sourceCommit'],testCounts=r['testCounts'],captures={n:dict(bytes=x['zipBytes'],sha256=x['zipSha256']) for n,x in cap['captures'].items()},actualAdvanceTo=16,protectedArchives=77)))
def verify(current):
    for n,h in read('SHA256.json').items():assert sha((D/n).read_bytes())==h,n
    c,r=check_raw();assert inspect_captures(c)==read('CAPTURE_VERIFICATION.json');assert conserve(c,current)==read('CONSERVATION.json')
    inventory=read('PROTECTED_ARCHIVE_INVENTORY.json');assert len(inventory)==77
    for x in inventory:
        data=(R/x['path']).read_bytes() if current else git('show',c['sourceCommit']+':'+x['path']) if x['path'] not in {v['path'] for v in read('CAPTURE_VERIFICATION.json')['captures'].values()} else (R/x['path']).read_bytes()
        assert sha(data)==x['sha256'] and len(data)==x['bytes']
    if current:
        for p,h in read('CURRENT_PROTECTED_EVIDENCE_HASHES.json').items():assert sha((R/p).read_bytes())==h,p
    print(json.dumps(dict(verified=True,sourceCommit=c['sourceCommit'],currentConservation=current,actualSelectedMethods=2,passed=1,intentionalFutureRequirementFailures=1,errors=0,skipped=0,exitCode=1,genuineNewLearnerCaptures=3,protectedArchives=77,previousTestFilesConserved=276,actualAdvanceTo=16,sourceBindingsConserved=155,proposal='NOT_ACTIVATED',all48Findings='OPEN',audit='INCOMPLETE',physicalAcceptance='OUTSTANDING_LAST',completeSuite=False)))
if __name__=='__main__':
    assert sys.argv[1:] in [['collect'],['verify'],['verify','--current']]
    if sys.argv[1]=='collect':collect()
    else:verify('--current' in sys.argv)
