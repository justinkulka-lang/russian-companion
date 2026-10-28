from pathlib import Path
from datetime import datetime
from zoneinfo import ZoneInfo
import json,hashlib,zipfile,shutil,subprocess
R=Path('/workspace/russian-companion');W=Path(__file__).parent;C=W/'BINDING_CHECKS'
D=R/'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit14-source-reconciliation-2026-10-28'
sha=lambda b:hashlib.sha256(b).hexdigest()
git=lambda *a:subprocess.check_output(['git',*a],cwd=R)
def save(n,v):(D/n).write_text(json.dumps(v,indent=2)+'\n')
assert not git('status','--porcelain=v1','-z')
r=json.loads((C/'REGRESSION/RESULT.json').read_text());c=json.loads((C/'REGRESSION/CONTEXT.json').read_text());S=c['sourceCommit']
assert git('rev-parse','HEAD').decode().strip()==S==r['sourceCommit']
files={}
for mode in ['DISCOVERY','REGRESSION']:
    done=json.loads((C/(mode+'_COMPLETION.json')).read_text());res=json.loads((C/mode/'RESULT.json').read_text())
    assert done['sourceCommit']==S and done['runnerExit']==res['exitCode']==int(done['gradleExit'])
    for n,h in json.loads((C/mode/'SHA256.json').read_text()).items():assert sha((C/mode/n).read_bytes())==h,n
    for p in (C/mode).rglob('*'):
        if p.is_file():files[p.relative_to(C).as_posix()]=p
    for n in ['CONTEXT.json','RESULT.json','RESULTS.csv','RUN.exit','SHA256.json']:shutil.copy2(C/mode/n,D/('BINDING_'+mode+'_'+n))
    for suffix in ['COMPLETION.json','LAUNCHER.json','RUNNER_PROCESS.json','LAUNCHER.exit','RUNNER.log','LAUNCHER.log']:files[mode+'_'+suffix]=C/(mode+'_'+suffix)
for n in ['run.py','resources.gradle','launch.py','supervise.py']:files['RUNNER/'+n]=C/n
for n,h in c['initScriptSha256'].items():
    p=Path(n);assert sha(p.read_bytes())==h;files['INIT/'+p.name]=p
index={}
with zipfile.ZipFile(D/'RAW_BINDING_CHECKS.zip','x',zipfile.ZIP_DEFLATED) as z:
    for n,p in sorted(files.items()):
        assert not n.lower().endswith('.pdf');z.write(p,n);index[n]=dict(bytes=p.stat().st_size,sha256=sha(p.read_bytes()))
save('RAW_BINDING_CHECKS_INDEX.json',index)
save('ACTUAL_BINDING_FAILURES.json',dict(sourceCommit=S,count=sum(t['outcome'] in ['FAILED','ERROR'] for t in r['tests']),tests=[t for t in r['tests'] if t['outcome'] in ['FAILED','ERROR']]))
# Preserve actual raw evidence before asserting success.
assert r['testCounts']==dict(reported=2,passed=2,failed=0,errors=0,skipped=0) and r['classes']==1 and r['exitCode']==0
assert r['discoveryComparison']['sameInputTrees'] and r['discoveryComparison']['exactSameMethodSet']
for n in ['verify_generation.py','verify_binding_checks.py','complete_bindings.py']:shutil.copy2(W/n,D/n)
start=datetime.fromisoformat(c['startedUtc']).astimezone(ZoneInfo('America/Chicago'));end=datetime.fromisoformat(r['finishedUtc']).astimezone(ZoneInfo('America/Chicago'))
verification=dict(sourceCommit=S,inputTrees=c['inputTrees'],testCounts=r['testCounts'],classes=1,exitCode=0,exactCommand=c['command'],activation=c['activation'],startedUtc=c['startedUtc'],finishedUtc=r['finishedUtc'],americaChicago=dict(started=start.isoformat(),finished=end.isoformat()),tasks=r['tasks'],lintIssuesBySeverity=r['lintIssuesBySeverity'],releaseGateAttachment='PASS_ATTACHMENT_ONLY_NOT_APPROVAL',discovery=dict(sourceCommit=S,reported=2,syntheticSkips=2,runtimePasses=0),apkIdentity=None,apkSignature=None,apkAssemblyInstallSigning='NOT_RUN',exclusions=c['exclusions'],fullAffectedScope=dict(plannedClasses=87,methodCount='NOT_MEASURED',runtime='NOT_LAUNCHED'),completeSuite=False,scopesNotSummed=True,all48Findings='OPEN',audit='INCOMPLETE',physicalAcceptance='OUTSTANDING_LAST')
save('VERIFICATION_SUMMARY.json',verification)
for script,out in [('verify_generation.py','GENERATION_VERIFICATION.json'),('verify_binding_checks.py','BINDING_VERIFICATION.json')]:
    result=subprocess.run(['python3','-B',str(D/script)],cwd=R,text=True,stdout=subprocess.PIPE,stderr=subprocess.STDOUT)
    (W/(out+'.log')).write_text(result.stdout);assert result.returncode==0,result.stdout
    save(out,json.loads(result.stdout))
(D/'COMPLETED_BINDINGS.md').write_text(f'''# Unit14 source bindings reconciled; focused proofs passed

Actual binding-test source `{S}`: **2 reported / 2 passed / 0 failed / 0 errors / 0 skipped**, one full selected class, Gradle and runner exit0. Run {start.isoformat()} → {end.isoformat()} America/Chicago; raw UTC retained. Discovery had two synthetic skips and zero runtime passes. Fresh XML, events, discovery identities and six input trees agree. This is focused engineering verification, not a complete suite or independent acceptance.

The two methods verify strict before14→13→12→11→10→9→8→7→6→5 whole-pack conservation and the exact kernel reference update. Unit14v1 restoration matches the entire actual9101af8c baseline and previous bd00e508 producer pack. The original96242a17 Unit14 source guard remains in that proven prior scope; current14 has a separate d72afd12 guard. The actual historical matching pair at9101af8c/f670 was verified by SHA; the post-core ab5795a0 pair did not match. Prior13's31ac matching pair remains exact. All original historical pins/assertions remain; no wider allowlist or resnapshot. The kernel reference retains19files/292bindings and all prior history; only its current kernel hash and one pending engineering append changed, with semantic/rights acceptance false.

Two independent actual compiled JVM renders matched all153outputs and validated all30packages in each render. Measured95of155records changed only identity values;all60reviewer/mapping worksheets are byte-exact. The95prior versions and original Git blobs are archived as historical evidence. Only Unit14's effective source moved from e88130c42b21fc2440cb82792a3bc3841fe69563 to actual20/20verified production d72afd1232857a857624c20c58f4bcd3cb5c8564. The other29source choices and every reviewer/owner/rights/mapping/admission/approval field are unchanged. Dossier updates only its two identity headers and preserves historical citations. Candidate remains unfrozen/sourceCommitnull/artifactNOT_RECORDED/publicReleaseNOT_APPROVED. This identity reconciliation grants no qualified acceptance.

Repository source digest **487d9118d7154dd59162dbb7024f1349bc53d5b7bf89e9d79022d97eaa1eeb9a** is DISTINCT from pack content-review digest **39c3563ba1b149b36977ae0fc2c0f920e282101e9e7870c06d141fe1a10e65ec**. Production digest was actually computed before generation, matched the verified core, matched both real and projected source roots in both renders, and was recomputed after application. No new digest-label correction or hidden rerun occurred here; the prior Unit13 correction remains immutable. Compile/generation/application executed zero JUnit methods. These later two focused JUnit methods are recorded separately from prior20core methods and are not summed.

All77inherited protected archives passed SHA/CRC. Production is unchanged from the verified Unit14core. Wholebaseline/core tests and277of280prior test-source files are exact;three intentional tooling/reference files were archived before modification. One new two-method class gives281current test-source files. All41policy objects and schemas1–16 remain unchanged in this phase;genuine14v1/15owner14v1captures and old13bridges remain preserved. The155binding records are now reconciled;larger affected integration is still pending.

Lint severity counts `{r['lintIssuesBySeverity']}`. Exact task/cache states are in VERIFICATION_SUMMARY.json and BINDING_REGRESSION_RESULT.json. Initial tooling compile executed compileDebugUnitTestKotlin with zero JUnit methods;focused runtime compilation status is retained. Lint and release-gate attachment passed;attachment is not release approval. Exact commands/activation/init hashes/fullXML/events/GC/lint/exits and both complete renders are archived. No APKassembly/install/newsigning;APKidentity/signaturenull.

Next large task: fresh affected integration from NEXT_AFFECTED_SCOPE.json,87planned full classes (the prior83plus4newUnit14classes).87is an inventory,not an executed count;method count is not measured here. Preserve original comparison/support files before scoped edits. Older Unit13 global policy projections, old Unit14-specific comparisons/teaching/source pins and prior kernel-reference contracts may need narrowly proven before14 scopes, while current guards remain current. Preserve every original assertion/pin/raw failure;do not waive or resnapshot. Fresh broad selected-host verification follows resolved affected work.

Prior d72afd12core20/20 and cd2b1c91broad1477reported/1476passed/1physicalskip/257classes/exit0 remain separate/unsummed. Original f2628b36baseline1positive/1intentionalfuturefailure/exit1,316interrupted/165continuation/63followup/1458broad and other historical scopes remain immutable. The420f41ff6c2a4417cd829fdee0179b6a3b797eff handoff691source inventory/reported execution is unverified beyond67focused verified tests. Historicalmainff539220reported563/562passed/1skipped after16export exclusions is a differentcommit/scope,not complete-suite proof.

All48independent findings remain OPEN;audit INCOMPLETE. Four screenshot exporters16methods explicitly excluded:VisualReviewCaptureTest2,Units35ChunkCaptureTest2,Units610ChunkCaptureTest10,Units1115GapCaptureTest2. Physical Galaxy S26 Ultra airplane-mode/reboot acceptance remains outstanding and LAST after final independent comprehensive nonphysical audit. Qualified linguistic/source/rights/mapping/owner review,CC024risk,CC048authority conflict and originalUnit7restorecause outstanding. No merge/freeze/formal advancement/release approval. PrivatePDF unread/unpackaged/zero trackedPDFs;environment settings unchanged/publication user-confirmed independently unobserved;SQLiteprepareJITdirective provisional/unadopted,not test exclusion,nativecauseUNCONFIRMED. Oneagent/existingcheckout/DraftPR64.
''')
(D/'NEXT_TASK.md').write_text(f'''# Next large task: fresh Unit14 affected integration

Read COMPLETED_BINDINGS.md,APPLIED_RECORDS.json,PROPOSAL_REVIEW.json,MATCHING_HISTORICAL_KERNEL_REFERENCE.json,GENERATION_VERIFICATION.json,BINDING_VERIFICATION.json and VERIFICATION_SUMMARY.json. Actual focused source `{S}`:2/2methods/1fullclass passed,exit0. Two153file renders/30packages each;95identity-only changes/60worksheets exact/95priorversions historical.155current bindings reconciled. No active job remains after verified collection. Oneagent/existingcheckout/DraftPR64;no new confirmation needed.

1. Pin all155current record hashes and77protected archives;preserve wholebaseline/newcore/41objects/schemas1–16/old14v1and15owner14v1captures/current13v3+old13bridges. NEXT_AFFECTED_SCOPE.json is87planned fullclasses (prior83plus4Unit14classes),NOT execution. Discover actual method count/IDs against clean current inputs. Preserve four exporter16method exclusions. Do not add this2method run to20core or prior1477broad counts.
2. Archive original comparison/support files before narrowly justified reconciliation. Full before14pack proof now exists and executes. Preserve every assertion,old hash/source literal,wholebaseline and currentcontrol. Old Unit13 declaration-only/globalpolicy projections describe before14;do not blanket-make all runtime/eligibility/mutation checks historical. A historical policy projection must use the full-pack-proven DECLARED policy selection,not pretend the current global registry still has59pools. NewUnit14current controls keep60pools/current16. Older Unit14 fixedv1slot/label/teaching/source comparisons may expose assumptions;inspect actual scopes and failures before edits,never resnapshot expectations to forcegreen.
3. Older kernel/reference comparisons need actual matching historical bytes,not just oldreference with newkernel.9101af8c182b3415426ed9226cb211a707a2c393 and f670c24e04def2829a7949e83b91b83a0dedf338 have matching prior13kernel/reference;ab5795a0postcore doesnot. Prior13conservation's31ac807387253082c44bd2f6d67021478f0df66e pair remains exact. NewUnit14BindingConservationTest retains actual current controls. Authenticate every source/blob/SHA and prior-append scope;retain all prior citations/pins.
4. Fresh full affected classes must include source tooling/conservation,all current14baseline/compatibility/UI,downstream15,and historical/current controls. Record exact source/commands/XML/events/counts/exits/lint/task/cache/gate/native/archive state;preserve EVERY actual failure before correction. Do not weaken guards or widen allowlists. After justified followups resolve actual affected failures,fresh broad selected-host discovery/runtime. No complete-suite/app-curriculum/device approval inference from selected host passes.

Repository source487d9118d7154dd59162dbb7024f1349bc53d5b7bf89e9d79022d97eaa1eeb9a differs from packcontent39c3563ba1b149b36977ae0fc2c0f920e282101e9e7870c06d141fe1a10e65ec. No production/sourcebinding changes needed merely to scope historical assertions. All48externalfindingsOPEN/auditINCOMPLETE. Qualifiedreviews/CC024risk/CC048authority/originalUnit7restorecause unresolved. Physical Galaxy S26 Ultra airplane-mode/reboot acceptance LAST after final independent comprehensive nonphysical audit. No APKassembly/install/newsigning/merge/freeze/formal advancement/releaseapproval;APKidentity/signaturenull. PrivatePDFunread/unpackaged/environmentunchanged/publication user-confirmed independently unobserved/JITdirectiveunadopted/nativecauseUNCONFIRMED.
''')
rel=D.relative_to(R).as_posix()
for name in ['AGENTS.md','audit/content-corrections/CODEX_ENGINEERING_CONTINUATION.md','audit/content-corrections/CODEX_CHECKPOINT_2026-10-12.md']:
    f=R/name;link=rel+'/COMPLETED_BINDINGS.md' if name=='AGENTS.md' else '../'+rel.removeprefix('audit/')+'/COMPLETED_BINDINGS.md'
    f.write_text(f'Current checkpoint: [Unit14bindings reconciled;focused proofs passed]({link}).Actual{S[:8]}:2/2methods/1class pass,exit0;separate from20core. Two153renders/30packages/95identity-only changes/60worksheets exact/95historicalversions/77archives conserved;155bindingsCURRENT_ENGINEERING_RECONCILED. Repository487d9118 DISTINCT pack39c3563b. Strict14→13→12→11→10→9→8→7→6→5/oldpins/currentguards verified. NEXT_TASK.md:87classaffected inventory requiresfreshdiscovery/runtime;broaderintegrationPENDING. All48OPEN/auditINCOMPLETE/four exporters16methods excluded/physicalLAST/noAPK/signing/merge/freeze/approval.\n\n'+f.read_text())
save('SHA256.json',{p.relative_to(D).as_posix():sha(p.read_bytes()) for p in sorted(D.rglob('*')) if p.is_file() and p.name!='SHA256.json'})
print(json.dumps(dict(completed=True,sourceCommit=S,testCounts=r['testCounts'],lint=r['lintIssuesBySeverity'],bindings=155,affectedClassInventory=87,affectedRuntime='NOT_LAUNCHED')))
