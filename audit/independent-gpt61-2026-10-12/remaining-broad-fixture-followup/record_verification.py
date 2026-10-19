from pathlib import Path
from datetime import datetime,timezone
import json,hashlib,subprocess,re
B=Path('audit/independent-gpt61-2026-10-12');O=B/'remaining-broad-fixture-followup'
write=lambda p,d:p.write_text(json.dumps(d,indent=2)+'\n')
sha=lambda p:hashlib.sha256(p.read_bytes()).hexdigest()
git=lambda *a:subprocess.check_output(['git',*a],text=True).strip()
m=json.loads((O/'INITIAL/MANIFEST.json').read_text());d=json.loads((O/'CORRECTION.json').read_text())
assert m['scopeComplete'] and m['eventsMatchFreshXml'] and m['exitCode']==0
assert m['testCounts']==dict(reported=31,passed=31,failed=0,errors=0,skipped=0) and m['classes']==12
assert m['lint']['Error']==0 and m['lint']['Fatal']==0
old=json.loads((B/'broader-regression-2026-10-18/CURRENT_BROAD_RESULTS.json').read_text())
first=json.loads((B/'current-broad-conservation-followup/CORRECTION.json').read_text())
first_run=json.loads((B/'current-broad-conservation-followup/INITIAL/MANIFEST.json').read_text())
assert first_run['scopeComplete'] and first_run['testCounts']==dict(reported=26,passed=26,failed=0,errors=0,skipped=0)
context=m['sourceContext'];source=context['sourceCommit'];broad_source=old['sourceCommit']
for p in ['app/src/main','catalog','docs','review']:assert context['inputTrees'][p]==git('rev-parse',broad_source+':'+p)
cases={(t['class'],t['name']) for t in old['newBroadFailureRecords']}
resolved_first={(f'dev.russiancompanion.v2.{c}',n) for c,v in first['classes'].items() for n in v['changedMethods']}
resolved_here={(f'dev.russiancompanion.v2.{c.replace("/",".")}',n) for c,v in d['changedClasses'].items() for n in v['changedMethods']}
assert len(cases)==16 and len(resolved_first)==4 and len(resolved_here)==12 and not resolved_first&resolved_here and cases==resolved_first|resolved_here
outcomes={(t['class'],t['name']):t['outcome'] for t in m['tests']}
assert all(outcomes[c]=='PASSED' for c in resolved_here)

def without_function(text,name):
    p=re.search(r'(?m)^([ \t]*)(?:@Test\s+)?(?:private\s+)?fun '+re.escape(name)+r'\(',text);assert p,name
    end=text.index('\n'+p[1]+'}',p.end())+len('\n'+p[1]+'}')
    return text[:p.start()]+text[end:]

unchanged={}
for cls,v in d['changedClasses'].items():
    p=Path('app/src/test/java/dev/russiancompanion/v2')/(cls+'.kt')
    assert sha(p)==v['afterSha256'],p
    before=subprocess.check_output(['git','show',d['sourceBefore']+':'+str(p)],text=True);after=p.read_text()
    names=v['changedMethods'] if cls!='V2RebuildTest' else ['assertObjectiveSkipBlocks']
    for name in names:before=without_function(before,name);after=without_function(after,name)
    if cls=='Unit4NominativeRoleTest':
        lines=after.splitlines(keepends=True);matches=[l for l in lines if l.startswith(' private fun historicalConservationPack(')];assert len(matches)==1;after=after.replace(matches[0],'',1)
    assert before==after,('Unexpected bytes outside reviewed methods/helpers',cls)
    unchanged[str(p)]=hashlib.sha256(before.encode()).hexdigest()
for cls,v in first['classes'].items():assert sha(Path('app/src/test/java/dev/russiancompanion/v2')/(cls+'.kt'))==v['afterSha256']
expected={str(Path('app/src/test/java/dev/russiancompanion/v2')/(c+'.kt')) for c in d['changedClasses']}|{str(Path('app/src/test/java/dev/russiancompanion/v2')/(c+'.kt')) for c in first['classes']}
assert set(git('diff','--name-only',broad_source,source,'--','app/src/test').splitlines())==expected
d['runtime']={'sourceCommit':source,'manifest':'INITIAL/MANIFEST.json','counts':m['testCounts'],'classes':12,'lint':m['lint'],'taskStatuses':m['taskStatuses'],'allBytesOutsideReviewedMethodsAndHelpersExact':True,'allOtherTestFilesExact':True};write(O/'CORRECTION.json',d)
r={'sourceCommit':source,'completeBroadSourceCommit':broad_source,'completeBroadCounts':old['testCounts'],'completeBroadClasses':193,'broadScopeCompleteWithExclusions':True,'broadRawEvidenceIndex':'broader-regression-2026-10-18/CURRENT_RAW_EVIDENCE_INDEX.json','separatePassingFollowups':[{'sourceCommit':first_run['sourceContext']['sourceCommit'],'manifest':'current-broad-conservation-followup/INITIAL/MANIFEST.json','counts':first_run['testCounts'],'classes':3},{'sourceCommit':source,'manifest':'remaining-broad-fixture-followup/INITIAL/MANIFEST.json','counts':m['testCounts'],'classes':12}],'sameMainCatalogDocsReviewTrees':True,'onlyTestFilesChanged':sorted(expected),'allBytesOutsideReviewedMethodsAndHelpersExact':True,'unchangedResidualFileSha256':unchanged,'allOtherTestFilesByteExact':True,'original32CasesAlsoPassedInCompletedBroadRun':True,'newBroadCasesResolved':[{'class':c,'method':n,'engineeringDisposition':'FIXTURE_SCOPE_RECONCILED_WITH_SEPARATE_CURRENT_GUARDS','followupSource':source if (c,n) in resolved_here else first_run['sourceContext']['sourceCommit']} for c,n in sorted(cases)],'newBroadOpenCases':[],'freshAllPassingBroadRunAtCurrentTestSource':False,'separateScopesNotSummed':True,'formalAcceptance':False,'engineeringReadyForIndependentDiagnosticReview':True,'qualifiedSourceLinguisticRightsReview':'PENDING','physicalAcceptance':'OUTSTANDING_LAST_AFTER_FINAL_INDEPENDENT_COMPREHENSIVE_AUDIT','apkAssemblyOrSigning':'NOT_RUN','configuredApkIdentity':'dev.russiancompanion / 2.0.0-dev / code5','artifactSha256':'NOT_RECORDED','artifactSignature':'NOT_RECORDED','environment':{'install_script':'SAVED_UNCHANGED','start_skill':'SAVED_UNCHANGED','publication':'USER_CONFIRMED_INDEPENDENTLY_UNVERIFIED'},'releaseApproval':False}
write(B/'CURRENT_HOST_VERIFICATION_DISPOSITION.json',r)
runtime=f'Complete broad source{broad_source}:1225reported/1208passed/16failed/0errors/1physical skip,193classes. All raw failures remain immutable. Separate first followup at{first_run["sourceContext"]["sourceCommit"]}:26methods/3classes all passed. Separate remaining-case followup at{source}:31methods/12classes all passed. Fresh XML/events match exact selectors;lint0fatal/0errors/20warnings;compileDebugSources and release-gate attachment passed. These scopes are not summed or a fresh all-pass broad result. Main/catalog/docs/review unchanged;all other test files and every byte outside reviewed methods/helpers verified exact. All16observed cases now have scoped engineering reconciliation;this does not close qualified semantic/source/rights/governance findings.'
nextstep='Prepare the current independent comprehensive diagnostic audit packet and copy/paste instructions pinned to a real repository commit,with the complete failed broad evidence and both separately passing followups. Clearly disclose that no fresh all-pass broad run at this final test source has occurred. Preserve all64 genuine lifecycle histories/backups,nine original diagnostics,all original32 and new16failure bytes,pending qualified reviews,two Unit28 research-cue debts,and missing historical audit/four-CSV limitations. Private Penguin remains separate private attachment against continuity SHA256,never Git/assets/logs/packets. Final independent comprehensive audit FIRST;actual Galaxy S26 Ultra airplane-mode/reboot acceptance LAST/outstanding. No merge,freeze,APK assembly/signing or release approval.'
(O/'NEXT_REVIEW.md').write_text('# Current host verification checkpoint\n\n'+runtime+'\n\n'+nextstep+'\n\nFour screenshot exporters16methods remain excluded:VisualReviewCaptureTest2,Units35ChunkCaptureTest2,Units610ChunkCaptureTest10,Units1115GapCaptureTest2. Environment install/start saved/unchanged;publication user-confirmed independently unverified. Configured dev.russiancompanion/2.0.0-dev/code5;artifact hash/signature NOT_RECORDED. Baseline420f41ff691source annotations/handoff execution unverified;current1241annotations (+550),1225discovered after16exclusions. Historical mainff539220563reported/562passed/1skipped remains a different source/scope,not a proven current complete-suite result.\n')
status=B/'CURRENT_FINDING_STATUS.json';s=json.loads(status.read_text());key='latestRemainingBroadFixtureFollowup';s[key]=dict(r,path=str(O),next=nextstep);s['currentEngineeringContinuation'].update(checkpoint=key,priorCheckpoint='latestCurrentBroadConservationFollowup',next=nextstep);write(status,s)
for p in [Path('audit/content-corrections/CODEX_ENGINEERING_CONTINUATION.md'),Path('audit/content-corrections/CODEX_CHECKPOINT_2026-10-12.md')]:
    with p.open('a') as f:f.write('\n## Remaining broad cases — '+datetime.now(timezone.utc).isoformat()+'\n\nRead `'+str(O/'NEXT_REVIEW.md')+'`. '+runtime+'\n\n'+nextstep+'\n')
p=Path('AGENTS.md');a=p.read_text();start=a.index('Latest verification:');end=a.index('\n\nThe notes below',start);a=a[:start]+f'Latest verification: [remaining broad fixture followup]({O}/NEXT_REVIEW.md). '+runtime+' Read latestRemainingBroadFixtureFollowup in CURRENT_FINDING_STATUS.json and CURRENT_HOST_VERIFICATION_DISPOSITION.json. '+nextstep+a[end:];p.write_text(a)
write(O/'CHECKPOINT_INTEGRITY.json',{'actualSourceCommit':source,'broadSourceCommit':broad_source,'unchangedProductionTrees':{p:v for p,v in context['inputTrees'].items() if p!='app/src/test'},'instructionSha256':{str(p):sha(p) for p in [Path('AGENTS.md'),Path('audit/content-corrections/CODEX_ENGINEERING_CONTINUATION.md'),Path('audit/content-corrections/CODEX_CHECKPOINT_2026-10-12.md'),status,B/'CURRENT_HOST_VERIFICATION_DISPOSITION.json']},'evidenceSha256':{str(p.relative_to(O)):sha(p) for p in sorted(O.rglob('*')) if p.is_file() and p.name!='CHECKPOINT_INTEGRITY.json'},'rawFailuresPreserved':True,'freshAllPassingBroadResult':False,'formalAcceptance':False,'releaseApproval':False})
print('31passed;12cases reconciled;all16scoped;no fresh all-pass broad claim')
