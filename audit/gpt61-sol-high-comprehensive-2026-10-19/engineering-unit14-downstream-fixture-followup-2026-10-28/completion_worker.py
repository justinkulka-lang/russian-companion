from evidence import *
from datetime import datetime,timezone
from zoneinfo import ZoneInfo
import time,os,traceback,shutil,xml.etree.ElementTree as ET
now=lambda:datetime.now(timezone.utc).isoformat()
S=git('rev-parse','HEAD');REL=D.relative_to(R).as_posix();BRANCH='cursor/content-audit-corrections-964f'
POINTERS=['AGENTS.md','audit/content-corrections/CODEX_ENGINEERING_CONTINUATION.md','audit/content-corrections/CODEX_CHECKPOINT_2026-10-12.md']
def invoke(args):
    p=subprocess.run(args,cwd=R,text=True,capture_output=True)
    assert p.returncode==0,json.dumps({'command':args,'code':p.returncode,'output':(p.stdout+p.stderr)[-6000:]})
    return p.stdout
def active_jobs():
    owned=read(W/'REGRESSION_RUNNER_PROCESS.json');found=[]
    for p in Path('/proc').iterdir():
        if not p.name.isdigit()or int(p.name)==os.getpid():continue
        try:
            state=(p/'stat').read_text().rsplit(')',1)[1].split()[0];cmd=(p/'cmdline').read_bytes().replace(b'\0',b' ').decode(errors='replace')
            if state!='Z'and(int(p.name)in[owned['pid'],owned['supervisorPid']]or('java'in cmd and str(W)in cmd)):found.append(int(p.name))
        except OSError:pass
    return found
try:
    fd=os.open(W/'AUTO_COMPLETION.lock',os.O_CREAT|os.O_EXCL|os.O_WRONLY);os.close(fd)
    save(W/'AUTO_PROCESS.json',{'pid':os.getpid(),'sourceCommit':S,'startedUtc':now(),'authority':'Own actual complete/incomplete evidence, audit/docs-only commit, normal push, DraftPR64 update; no app/test/binding edits'})
    missing=0
    while not(W/'REGRESSION_COMPLETION.json').exists():
        missing=0 if active_jobs()else missing+1
        if missing>=3:
            save(W/'INCOMPLETE_PROCESS_DISAPPEARANCE.json',{'utc':now(),'sourceCommit':S,'stopCause':'UNCONFIRMED','actualOwnedJobs':[]});break
        time.sleep(15)
    while active_jobs():time.sleep(5)
    while not(W/'DURABLE_MONITOR_STATUS.json').exists():time.sleep(1)
    key='completionExists'if(W/'REGRESSION_COMPLETION.json').exists()else'runtimeInactiveWithoutExit'
    while not read(W/'DURABLE_MONITOR_STATUS.json').get(key):time.sleep(2)
    assert git('rev-parse','HEAD')==S and not git('status','--porcelain')
    complete=(W/'REGRESSION/RESULT.json').exists()
    monitor=read(W/'DURABLE_MONITOR_STATUS.json')
    if complete:
        result=read(W/'REGRESSION/RESULT.json');end=read(W/'REGRESSION_COMPLETION.json')
        assert result['sourceCommit']==S and result['exitCode']==end['runnerExit']==int(end['gradleExit'])
        for n in ['CONTEXT.json','RESULT.json','RESULTS.csv','RUN.exit','SHA256.json']:shutil.copyfile(W/'REGRESSION'/n,D/('REGRESSION_'+n))
        archive_mode('REGRESSION','RAW_FOLLOWUP')
        full=[]
        for p in(W/'REGRESSION/XML').glob('*.xml'):
            for t in ET.parse(p).getroot().findall('testcase'):
                for kind in ['failure','error']:
                    f=t.find(kind)
                    if f is not None:full.append({'testClass':t.get('classname'),'method':t.get('name'),'kind':kind,'message':f.get('message',''),'trace':f.text or ''})
        data=(json.dumps({'sourceCommit':S,'count':len(full),'failures':full,'originalAssertionsNotWaived':True},indent=2)+'\n').encode()
        archive=D/'ACTUAL_FAILURES_FULL.zip'
        with zipfile.ZipFile(archive,'x',zipfile.ZIP_DEFLATED)as z:z.writestr('ACTUAL_FAILURES.json',data)
        save(D/'ACTUAL_FAILURES.json',{'sourceCommit':S,'count':len(full),'originalAssertionsNotWaived':True,'storage':{'archive':archive.name,'archiveSha256':sha(archive.read_bytes()),'member':'ACTUAL_FAILURES.json','memberBytes':len(data),'memberSha256':sha(data)},'failures':[{'testClass':f['testClass'],'method':f['method'],'kind':f['kind'],'messageSha256':sha(f['message'].encode()),'traceSha256':sha(f['trace'].encode())}for f in full]})
        by={(t['testClass'],t['method']):t['outcome']for t in result['tests']}
        roles={key:[dict(t,outcome=by[t['testClass'],t['method']])for t in read(D/'SCOPE.json')[key]]for key in ['failedMethods','passedSiblings','controls']}
        save(D/'RECHECKED_GROUPS.json',{'sourceCommit':S,'groups':roles,'all60OriginalFailuresPassed':all(t['outcome']=='PASSED'for t in roles['failedMethods']),'all45SiblingsPassed':all(t['outcome']=='PASSED'for t in roles['passedSiblings']),'all14ControlsPassed':all(t['outcome']=='PASSED'for t in roles['controls'])})
        counts=result['testCounts'];summary=f'{counts["reported"]} reported / {counts["passed"]} passed / {counts["failed"]} failed / {counts["errors"]} errors / {counts["skipped"]} skipped;17fullclasses;exit{result["exitCode"]}'
        good=result['exitCode']==0 and counts==dict(reported=119,passed=119,failed=0,errors=0,skipped=0)and not monitor['newNativeReports']and not result['lintIssuesBySeverity']['Fatal']and not result['lintIssuesBySeverity']['Error']
        detail=f'Lint/task/cache: {json.dumps(result["lintIssuesBySeverity"])}; {json.dumps(result["tasks"])}. Compilation/cache states are not fresh execution;gate attachment is not release approval. All exact commands/source/trees/initSHA/XML/events/logs/GC/lint/gate/exits/native/full failure bytes retained. Runtime starts {read(W/"REGRESSION/CONTEXT.json")["startedUtc"]};ends{result["finishedUtc"]};America/Chicago counterparts are derived from those UTC times.'
        nexttext='Review actual119outcomes and independent patch review, then plan the justified next broad selected-host verification before curriculum advancement.'if good else'Read every full actual failure/skip/native/task error; preserve it and reconcile narrowly before any broader retry or Unit15 work.'
    else:
        archive_mode('REGRESSION','RAW_INCOMPLETE');good=False
        events=[json.loads(x)for x in(W/'REGRESSION/EVENTS.jsonl').read_text().splitlines()if x.strip()]if(W/'REGRESSION/EVENTS.jsonl').exists()else[]
        rows=[e for e in events if e.get('kind')=='RESULT'];save(D/'PARTIAL_EVENTS.json',rows)
        counts={'partialEvents':len(rows),'expected':119};summary=f'INCOMPLETE: {len(rows)} partial events/119expected;no verified final result';detail='Stop cause UNCONFIRMED unless actual retained logs/process/native evidence establishes it. No final XML/count claim;UNVERIFIED_BUILD_OBSERVATION bytes are not final verification.';nexttext='Inspect original incomplete evidence and actual owned jobs before planning continuation. No blind rerun or invented final counts.'
    save(D/'FINAL_CONSERVATION.json',conservation())
    constraints='All1330originalassertions/pins/targetscenarios and whole original15files conserved except the exact single inserted line each;266other tests/production/155bindings/77archives/priorbroadfailureevidence unchanged. Four exporters16methods excluded:VisualReviewCaptureTest2,Units35ChunkCaptureTest2,Units610ChunkCaptureTest10,Units1115GapCaptureTest2. No APKassembly/install/signing;identity/signaturenull. All48OPEN/auditINCOMPLETE/physicalGalaxyairplane-modeandrebootLASTafterfinalindependentcomprehensivenonphysicalaudit. No merge/freeze/formal advancement/releaseapproval. Qualifiedreviews/CC024/CC048/originalUnit7restorecause remain unresolved;environment unchanged;privatePDFunread/unpackaged;JITdirectiveunadopted/notatestexclusion/nativecauseUNCONFIRMED. Prior1499broad1438pass60fail1physicalskip remains immutable/separate/unsummed;old562passes/691inventory differentcommits/scopes,notcomplete-suiteproof.'
    (D/'REPORT.md').write_text(f'# Downstream Unit14 fixture follow-up\n\nActual tested source `{S}`: **{summary}**. Discovery source `{read(D/"DISCOVERY_RESULT.json")["sourceCommit"]}`:119syntheticskips/zero runtimepasses. No overlapping scopes summed;not complete suite.\n\n{detail}\n\n{constraints}\n')
    (D/'NEXT_TASK.md').write_text('# Next task\n\n'+nexttext+'\n\n'+constraints+'\n')
    for n in POINTERS:
        p=R/n;link=REL+'/REPORT.md'if n=='AGENTS.md'else'../'+REL.removeprefix('audit/')+'/REPORT.md'
        p.write_text(f'Current checkpoint: [Downstream Unit14 full-class follow-up outcome]({link}). Actual{S}: {summary}.15single-line fixture changes/all1330originalassertions/pins/266others/production/155bindings/77archives/priorfailureevidence conserved. Read NEXT_TASK.md. Prior scopes separate/unsummed. All48OPEN/auditINCOMPLETE/four exporters16excluded/physicalLAST/noAPK/signing/merge/freeze/approval.\n\n'+p.read_text())
    manifest()
    if complete:(W/'FINAL_VERIFICATION.json').write_text(invoke(['python3','-B',str(W/'verify_results.py')]))
    else:verify_archive('RAW_INCOMPLETE')
    raw=subprocess.check_output(['git','status','--porcelain=v1','-z','--untracked-files=all'],cwd=R)
    for row in raw.split(b'\0'):
        if not row:continue
        assert row[2:3]==b' 'and not any(k in row[:2]for k in[b'R',b'C']);n=row[3:].decode();assert n in POINTERS or n.startswith(REL+'/'),n
    assert git('branch','--show-current')==BRANCH
    assert all(p.stat().st_size<100*1024*1024 for p in D.rglob('*')if p.is_file())
    invoke(['git','add',REL,*POINTERS]);invoke(['git','diff','--cached','--check']);invoke(['git','commit','-m','Preserve actual downstream Unit14 full-class follow-up outcomes'])
    head=git('rev-parse','HEAD');invoke(['git','push','origin',BRANCH]);assert git('ls-remote','origin','refs/heads/'+BRANCH).split()[0]==head
    prior=invoke(['gh','api','repos/justinkulka-lang/russian-companion/pulls/64','--jq','.body'])
    body=f'Checkpoint `{head}`: downstream Unit14 fixture follow-up at actual source `{S}`. **{summary}**.\n\nEvidence: `{REL}/REPORT.md` and NEXT_TASK.md. {nexttext}\n\n{constraints}\n\n---\n\n'+prior
    (W/'PR64_BODY_COMPLETED.md').write_text(body)
    response=json.loads(invoke(['gh','api','--method','PATCH','repos/justinkulka-lang/russian-companion/pulls/64','-F','body=@'+str(W/'PR64_BODY_COMPLETED.md')]))
    assert response['state']=='open'and response['draft']and response['head']['sha']==head
    assert not git('status','--porcelain')
    save(W/'AUTO_RESULT.json',{'sourceCommit':S,'checkpointCommit':head,'complete':complete,'good':good,'counts':counts,'summary':summary,'verified':True,'pushed':True,'draftPR64Updated':True,'clean':True,'finishedUtc':now(),'all48Findings':'OPEN','audit':'INCOMPLETE','physicalAcceptance':'OUTSTANDING_LAST','completeSuite':False})
except BaseException:
    save(W/'AUTO_ERROR.json',{'utc':now(),'error':traceback.format_exc()});raise
