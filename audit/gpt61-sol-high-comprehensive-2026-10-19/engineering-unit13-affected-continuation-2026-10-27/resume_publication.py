"""Resume only audit evidence publication after preserved Git-status parsing guard error."""
from pathlib import Path
from datetime import datetime, timezone
import subprocess, json, hashlib, shutil
R=Path('/workspace/russian-companion');W=Path(__file__).parent
D=R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit13-affected-continuation-2026-10-27'
rel=D.relative_to(R).as_posix();branch='cursor/content-audit-corrections-964f'
git=lambda *a:subprocess.check_output(['git',*a],cwd=R,text=True).strip()
save=lambda p,v:p.write_text(json.dumps(v,indent=2)+'\n')
sha=lambda p:hashlib.sha256(p.read_bytes()).hexdigest()
def invoke(args):
    p=subprocess.run(args,cwd=R,text=True,stdout=subprocess.PIPE,stderr=subprocess.STDOUT)
    assert p.returncode==0,dict(command=args,exit=p.returncode,output=p.stdout[-6000:])
    return p.stdout
assert (W/'AUTO_ERROR.json').exists() and not (W/'AUTO_RESULT.json').exists()
for pid in [2015,2014,1948]:
    p=Path('/proc')/str(pid)
    assert not p.exists() or (p/'stat').read_text().rsplit(')',1)[1].split()[0]=='Z'
r=json.loads((W/'REGRESSION/RESULT.json').read_text());S=r['sourceCommit']
assert git('rev-parse','HEAD')==S=='3248ab597dc13e8746464ba7601cf29e440995fa'
assert git('branch','--show-current')==branch
assert not git('diff','--cached','--name-only')
assert r['testCounts']==dict(reported=165,passed=165,failed=0,errors=0,skipped=0) and r['exitCode']==0
assert r['inputTrees']==json.loads((D/'INTERRUPTION.json').read_text())['inputTrees']
assert all(git('rev-parse','HEAD:'+p)==h for p,h in r['inputTrees'].items())
v=json.loads(invoke(['python3',str(W/'verify_results.py')]));assert v['verified']
allowed={'AGENTS.md','audit/content-corrections/CODEX_ENGINEERING_CONTINUATION.md','audit/content-corrections/CODEX_CHECKPOINT_2026-10-12.md'}
def checked_changes():
    raw=subprocess.check_output(['git','status','--porcelain=v1','-z','--untracked-files=all'],cwd=R)
    rows=[x for x in raw.split(b'\0') if x]
    assert rows
    paths=[]
    for x in rows:
        assert x[2:3]==b' ' and not any(c in x[:2] for c in [b'R',b'C']),x
        path=x[3:].decode();assert path in allowed or path.startswith(rel+'/'),path
        paths.append(path)
    return paths
before=checked_changes()
shutil.copy2(W/'AUTO_ERROR.json',D/'PUBLICATION_WORKER_ERROR.json')
shutil.copy2(W/'AUTO_COMPLETION.log',D/'PUBLICATION_WORKER_FAILURE.log')
shutil.copy2(__file__,D/'resume_publication.py')
save(D/'PUBLICATION_GUARD_RECOVERY.json',dict(observedUtc=datetime.now(timezone.utc).isoformat(),testedSource=S,originalWorkerExited=True,originalErrorSha256=sha(W/'AUTO_ERROR.json'),failurePhase='After successful raw collection and both evidence verifications, before git add/commit/push',cause='git helper stripped leading whitespace from porcelain output; first path M AGENTS.md was sliced as GENTS.md and rejected',correction='Read untrimmed NUL-terminated porcelain=v1 records; validate exact path allowlist and reject rename/copy entries before staging',validatedChangedPathsBeforeRecovery=before,allowedScope=[*sorted(allowed),rel+'/'],noTestsRerun=True,noCollectorRerun=True,productionTestInputsUnchanged=True,countsUnchanged=True,all48Findings='OPEN',audit='INCOMPLETE',physicalAcceptance='OUTSTANDING_LAST'))
with (D/'REPORT.md').open('a') as f:f.write('\n## Publication guard recovery\n\nAfter actual raw collection and both evidence verifications succeeded, the first completion worker exited before staging: its Git helper stripped leading whitespace from porcelain output, causing the first valid path AGENTS.md to be sliced incorrectly and rejected. PUBLICATION_WORKER_ERROR.json and PUBLICATION_WORKER_FAILURE.log preserve the original error. The separate resume_publication.py reads untrimmed NUL-terminated status records, checks the exact audit-docs allowlist, and rejects rename/copy records before staging. No tests or collector were rerun; source inputs, counts and assertions unchanged. PUBLICATION_GUARD_RECOVERY.json records the correction.\n')
save(D/'SHA256.json',{p.relative_to(D).as_posix():sha(p) for p in sorted(D.rglob('*')) if p.is_file() and p.name!='SHA256.json'})
(W/'RECOVERY_FINAL_VERIFICATION.json').write_text(invoke(['python3',str(W/'verify_results.py')]))
checked_changes()
invoke(['git','add',*sorted(allowed),rel]);invoke(['git','diff','--cached','--check'])
staged=git('diff','--cached','--name-only').splitlines()
assert staged and all(p in allowed or p.startswith(rel+'/') for p in staged)
invoke(['git','commit','-m','Preserve completed Unit13 continuation and interrupted integration evidence'])
head=git('rev-parse','HEAD');(W/'PUSH.log').write_text(invoke(['git','push','origin','HEAD:'+branch]))
body=f'''Unit13 continuation checkpoint `{head}`; actual tested source `{S}`. **165 reported /165 passed /0 failed /0 errors /0 skipped**,165exact methods across28classes,Gradle exit0. Fresh XML/events/discovery/sixinput trees agree. Lint0fatal/errors,20warnings,1hint;lintDebug and verifyReleaseGateAttachment executed successfully;compileDebugSources UP-TO-DATE. Gate attachment is not release approval.

Original75e2d31c run INTERRUPTED:316partial events/309SUCCESS/6FAILURE/1physicalSKIP,no underlyingexit or finalruntime result. Six original failures remain OPEN,all full messages retained. Stop causeUNCONFIRMED after exec-server disconnection and absent original processes. The165continuation selects every unreported identity,including two interrupted methods;zero repeated completed IDs. METHOD_COVERAGE.json proves disjoint identity accounting across481selected IDs at identical six inputs;this is NOT one completed481method run or a complete suite. Do not sum pass counts.

Packet `{rel}`:REPORT.md,NEXT_TASK.md,INTERRUPTED_RAW.zip,RAW_AFFECTED.zip,full failure returns,source/command/cache/exit/lint records,memberSHA/CRC,conservation and METHOD_COVERAGE.json. Publication worker first stopped before staging due to incorrect whitespace trimming of Git porcelain output;original error preserved and separate NUL-safe allowlisted publication recovery verified. No tests or collector rerun;source/counts unchanged.

Next large task:diagnose six original historical comparison failures,narrowly reconcile proven assumptions while retaining every original assertion/pin and all current controls,execute full-class follow-up,then fresh broad discovery/runtime. No app/test changes in recovery.155bindings/74archives conserved. Repositorysource digest3c0acbb95297cf10f08da2bd8391463067eece22fec785158b5d2680d37dc2ba differs from packcontent15c6ae393767cb7ffc190882867bfad923c9e81b07d0f4285cf0a678f2489280.

Four screenshot exporters16methods explicitly excluded:VisualReviewCaptureTest2,Units35ChunkCaptureTest2,Units610ChunkCaptureTest10,Units1115GapCaptureTest2. No APKassembly/install/signing;identity/signature null. All48independent findingsOPEN/auditINCOMPLETE. Physical Galaxy S26 Ultra airplane-mode/reboot acceptance LAST after final independent comprehensive nonphysical audit. Qualifiedreviews/CC024risk/19print-singletons/CC048authority/originalUnit7restorecause outstanding. No merge/freeze/formal advancement/releaseapproval. PrivatePDF unread/unpackaged;environment unchanged/publication user-confirmed independently unobserved/JITdirective unadopted/nativecauseUNCONFIRMED.
'''
(W/'PR64_BODY.md').write_text(body)
(W/'PR_UPDATE.json').write_text(invoke(['gh','api','--method','PATCH','repos/justinkulka-lang/russian-companion/pulls/64','-F','body=@'+str(W/'PR64_BODY.md'),'--jq','{number,state,draft,head:.head.sha}']))
assert not git('status','--porcelain') and git('ls-remote','origin','refs/heads/'+branch).split()[0]==head
save(W/'AUTO_RESULT.json',dict(sourceCommit=S,checkpointCommit=head,counts=r['testCounts'],classes=28,exitCode=0,verified=True,pushed=True,draftPR64Updated=True,clean=True,publicationGuardRecovery=True,originalErrorPreserved=True,noTestsRerun=True,originalInterruptedPartialEvents=316,originalFailuresStillOpen=6,singleCompleted481MethodRun=False,completeSuite=False,all48Findings='OPEN',audit='INCOMPLETE',physicalAcceptance='OUTSTANDING_LAST',finishedUtc=datetime.now(timezone.utc).isoformat()))
print((W/'AUTO_RESULT.json').read_text())
