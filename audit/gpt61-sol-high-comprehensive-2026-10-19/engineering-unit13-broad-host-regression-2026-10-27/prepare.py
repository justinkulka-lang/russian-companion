"""Prepare broad host verification without modifying app/test/review/source inputs."""
from pathlib import Path
import json,hashlib,subprocess,zipfile,os
R=Path('/workspace/russian-companion');W=Path(__file__).parent
B=R/'audit/gpt61-sol-high-comprehensive-2026-10-19'
D=B/'engineering-unit13-broad-host-regression-2026-10-27'
P=B/'engineering-unit13-historical-comparison-followup-2026-10-27'
OLD=B/'engineering-unit12-source-reconciliation-2026-10-27/ATTEMPT_3'
git=lambda *a:subprocess.check_output(['git',*a],cwd=R,text=True).strip()
sha=lambda b:hashlib.sha256(b).hexdigest();save=lambda p,v:p.write_text(json.dumps(v,indent=2)+'\n')
S='57f2705005ad93112a890798250822e718d6bf51'
assert git('rev-parse','HEAD')==S and not git('status','--porcelain')
assert git('branch','--show-current')=='cursor/content-audit-corrections-964f'
for p in Path('/proc').iterdir():
    if not p.name.isdigit():continue
    try:cmd=(p/'cmdline').read_bytes().replace(b'\0',b' ').decode(errors='replace')
    except OSError:continue
    assert not ('Gradle Test Executor' in cmd and 'java' in cmd),(p.name,cmd[:150])
assert not any(v=='1' for k,v in os.environ.items() if k.startswith('WRITE_'))
verification=json.loads(subprocess.check_output(['python3','-B',str(P/'verify_results.py')],cwd=R,text=True));assert verification['verified'] and verification['allSixPassed']
prior=json.loads((OLD/'REGRESSION_RESULT.json').read_text());assert prior['sourceCommit']=='44cb3e669d287d2f9848113f1722ab1224af9c37' and prior['testCounts']['reported']==1458
ps=json.loads((P/'SCOPE.json').read_text());newClasses={'Unit13PresentReviewBaselineTest','Unit13PresentCompatibilityTest','Unit13PresentReviewUiTest','Unit13BindingConservationTest'}
new=[x for x in ps['expectedMethods'] if x['testClass'].split('.')[-1] in newClasses]
assert len(new)==19 and len({x['testClass'] for x in new})==4
before={(t['testClass'],t['method']) for t in prior['tests']};added={(t['testClass'],t['method']) for t in new}
assert len(before)==1458 and not before&added
expected=sorted(before|added);assert len(expected)==1477 and len({k[0] for k in expected})==257
trees={n:git('rev-parse','HEAD:'+n) for n in ['app/src/main','app/src/test','catalog','docs','review','release']}
groups=[dict(name='Unit13 interrupted affected failures',originalSource='75e2d31c441e87e7f53c550b2bf34e9804737bfb',identities=ps['priorFailedIdentities']),dict(name='Unit12 affected failures',originalSource='6e485a79fd617c20f6f7ece94c5e1bdc9c505ec3',identities=[dict(testClass=t['testClass'],method=t['method']) for t in json.loads((OLD/'ORIGINAL_FAILED_IDENTITIES_RECHECKED.json').read_text())]),dict(name='Unit11 broad failures',originalSource='3434fb8391a6bb4bee91c312a13bd79523db7799',identities=[dict(testClass=t['testClass'],method=t['method']) for t in json.loads((OLD/'PRIOR_BROAD_FAILURES_RECHECKED.json').read_text())['rechecked']])]
allFailed=sorted({(x['testClass'],x['method']) for g in groups for x in g['identities']});assert set(allFailed)<=set(expected)
scope=dict(priorBroadSource=prior['sourceCommit'],priorBroadResultPath=(OLD/'REGRESSION_RESULT.json').relative_to(R).as_posix(),priorBroadResultSha256=sha((OLD/'REGRESSION_RESULT.json').read_bytes()),priorBroadReported=1458,methodCount=1477,classes=257,expectedMethods=[dict(testClass=c,method=m) for c,m in expected],expectedAddedMethods=new,expectedRemovedMethods=[],inputTrees=trees,broadHostTask=True,noMethodOrClassFilters=True,priorFailedIdentities=[dict(testClass=c,method=m) for c,m in allFailed],failureGroups=groups,currentFollowupMethods=ps['expectedMethods'],predictionOnlyUntilFreshDiscovery=True,completeSuite=False)
prepOld=json.loads((P/'PREPARATION.json').read_text());testHashes={n:sha((R/n).read_bytes()) for n in prepOld['existingTestFiles']}
assert len(testHashes)==276
for n,h in prepOld['currentRecordHashes'].items():assert sha((R/n).read_bytes())==h
for n,h in prepOld['archives'].items():
    assert sha((R/n).read_bytes())==h
    with zipfile.ZipFile(R/n) as z:assert z.testzip() is None
prep=dict(parentCommit=S,inputTrees=trees,productionTree=trees['app/src/main'],existingTestFiles=testHashes,currentRecordHashes=prepOld['currentRecordHashes'],archives=prepOld['archives'],followupEvidenceDirectory=P.relative_to(R).as_posix(),followupVerifiedSource=verification['sourceCommit'],original627AssertionInvocationsPreserved=True,original107Unit12ComparisonAssertionsPreserved=True,noSourceMutationInBroadPhase=True,repositorySourceDigest=prepOld['repositorySourceDigest'],packContentReviewDigest=prepOld['packContentReviewDigest'],all48Findings='OPEN',audit='INCOMPLETE',physicalAcceptance='OUTSTANDING_LAST')
D.mkdir();save(W/'SCOPE.json',scope);save(D/'SCOPE.json',scope);save(D/'PREPARATION.json',prep);save(D/'PRIOR_FOLLOWUP_VERIFICATION.json',verification)
(D/'STATUS.md').write_text('''# Unit13 broad selected-host regression prepared

Current parent `57f2705005ad93112a890798250822e718d6bf51`;no app/test/catalog/docs/review/release mutation. Inventory predicts1477methods/257classes:all1458prior broad identities plus19Unit13methods in4classes,zero removals. **Prediction only;fresh actual discovery required. No new runtime result.** The Gradle debug unit-test task has no method/class filters;the four exporters16methods remain explicitly excluded. All63latest full-class follow-up IDs included. Original failure groups6/8/2 retained with overlap tracked;do not sum groups or overlapping runs.

All276current test-source files are pinned byte-exact,including the five corrections retaining627original assertions/pins and earlier two Unit12corrections retaining107original assertions. All155current bindings and74protected archives SHA+CRC verified. Prior63/63full-class follow-up independently reverified without running tests. Original316partial/165continuation/63follow-up and earlier1458broad scopes remain separate/unsummed. Historical main562passes and420f41ff691sourceinventory are not complete-suite evidence.

Fresh discovery must save exactXML/events/context/exits and a SHA-indexed ZIP in Git before runtime. Verify actual1477set against1458+19/no removals;do not silently adjust scope. Then pin unchanged six input trees,commit clean ownership checkpoint,launch one detached runtime with two6GiBforks/forkEvery4/GC logs. Exclusive worker collects actual outcomes,verifies rawZIP CRC/memberSHA/source identities/assertion conservation/lint/gate,commits only this evidence folder and three pointers,normal-pushes authorized branch and updates DraftPR64. It must preserve incomplete/error outcomes as incomplete,never invent counts or final exits. Inspect external unit13-broad-host-regression-2026-10-27 contexts/processes/completions/monitor/AUTO_RESULT or ERROR before launch/edit/collection. Do not duplicate jobs or mutate active inputs.

Four screenshot exporters16methods excluded explicitly:VisualReviewCaptureTest2,Units35ChunkCaptureTest2,Units610ChunkCaptureTest10,Units1115GapCaptureTest2. One physical acceptance @Ignore is expected from prior broad identity;it is NOT actual Galaxy evidence. Provisional SQLite JITcompilationdirective unchanged/unadopted/nativecauseUNCONFIRMED;no test methods excluded by it. No APKassembly/install/signing;APKidentity/signature null. Repositorysource3c0acbb95297cf10f08da2bd8391463067eece22fec785158b5d2680d37dc2ba differs from packcontent15c6ae393767cb7ffc190882867bfad923c9e81b07d0f4285cf0a678f2489280.

All48independent findingsOPEN/auditINCOMPLETE. Physical Galaxy S26 Ultra airplane-mode/reboot acceptance LAST after final independent comprehensive nonphysical audit. Qualifiedreviews/CC024risk/19print-singletonpolicies/CC048authority/originalUnit7restorecause outstanding. No merge/freeze/formal advancement/releaseapproval. PrivatePDF unread/unpackaged/environmentsettings unchanged/publication user-confirmed independently unobserved. One agent/currentcheckout/DraftPR64.
''')
rel=D.relative_to(R).as_posix()
for n in ['AGENTS.md','audit/content-corrections/CODEX_ENGINEERING_CONTINUATION.md','audit/content-corrections/CODEX_CHECKPOINT_2026-10-12.md']:
    p=R/n;link=rel+'/STATUS.md' if n=='AGENTS.md' else '../'+rel.removeprefix('audit/')+'/STATUS.md'
    p.write_text(f'Current checkpoint: [Unit13 broad selected-host regression prepared]({link}).1477method/257class inventory PREDICTION only;1458priorIDs+19Unit13/no removals expected,fresh discovery/runtime required.276testfiles/sixinputs/155bindings/74archives pinned;all627+107originalcomparisonassertions/currentcontrols conserved. Inspect external unit13-broad-host-regression-2026-10-27 before launch/edit. Original316partial/165continuation/63followup/prior1458broad scopes separate/unsummed. All48OPEN/auditINCOMPLETE/four exporters excluded/physicalLAST/noAPK/signing/merge/freeze/approval.\n\n'+p.read_text())
print(json.dumps(dict(parent=S,predictedMethods=1477,predictedClasses=257,prior=1458,added=19,removed=0,testFilesPinned=276,recordsPinned=155,archivesShaCrc=74,uniquePriorFailureIds=len(allFailed),runtimePasses=0)))
