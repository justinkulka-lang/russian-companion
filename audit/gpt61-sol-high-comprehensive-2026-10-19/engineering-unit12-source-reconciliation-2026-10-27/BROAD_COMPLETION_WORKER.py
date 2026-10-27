"""Exclusive single-agent broad evidence owner; never changes test/application inputs."""
from pathlib import Path
from datetime import datetime, timezone
import hashlib
import json
import os
import subprocess
import time
import traceback
import zipfile

R = Path('/workspace/russian-companion')
W = Path(__file__).resolve().parent
D = R / 'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-unit12-source-reconciliation-2026-10-27'
branch = 'cursor/content-audit-corrections-964f'
run = lambda args: subprocess.check_output(args, cwd=R, text=True).strip()
sha = lambda p: hashlib.sha256(p.read_bytes()).hexdigest()
save = lambda p, v: p.write_text(json.dumps(v, indent=2) + '\n')
now = lambda: datetime.now(timezone.utc).isoformat()

try:
    with (W / 'AUTO_COMPLETION.lock').open('x') as f:
        f.write(str(os.getpid()) + '\n')
    save(W / 'AUTO_COMPLETION_PROCESS.json', dict(pid=os.getpid(), startedUtc=now(),
         scope='Exclusive collection/verification/audit-docs-only commit/normal push/DraftPR64 update; no app/test edits'))
    while not (W / 'REGRESSION_COMPLETION.json').exists():
        time.sleep(45)
    end = json.loads((W / 'REGRESSION_COMPLETION.json').read_text())
    assert end['resultExists'] and end['manifestExists'], 'Incomplete runner; preserve raw evidence and diagnose manually'
    r = json.loads((W / 'REGRESSION/RESULT.json').read_text())
    c = json.loads((W / 'REGRESSION/CONTEXT.json').read_text())
    source = r['sourceCommit']
    assert source == c['sourceCommit'] == run(['git', 'rev-parse', 'HEAD'])
    assert not run(['git', 'status', '--porcelain'])
    assert run(['git', 'branch', '--show-current']) == branch
    assert r['testCounts']['reported'] == 1458 and r['classes'] == 253
    assert end['runnerExit'] == r['exitCode'] == int(end['gradleExit'])
    while True:
        status = W / 'DURABLE_MONITOR_STATUS.json'
        if status.exists() and json.loads(status.read_text())['completionExists']:
            break
        time.sleep(5)
    m = json.loads(status.read_text())
    verification = json.loads(run(['python3', str(D / 'verify_broad_conservation.py')]))
    old = json.loads((D / 'ATTEMPT_1/FAILED_METHODS.json').read_text())
    by = {(t['testClass'], t['method']): t for t in r['tests']}
    rechecks = []
    for t in old:
        key = (t['testClass'], t['method'])
        assert key in by
        rechecks.append(dict(testClass=key[0], method=key[1], originalOutcome='FAILED',
                             followupOutcome=by[key]['outcome']))
    run(['python3', str(D / 'BROAD_DISCOVERY/verify.py')])
    run(['python3', str(W / 'collect_attempt.py'), 'ATTEMPT_3'])
    A = D / 'ATTEMPT_3'
    index = {}
    with zipfile.ZipFile(A / 'MONITORING.zip', 'x', zipfile.ZIP_DEFLATED) as z:
        for n in ['durable_monitor.py', 'DURABLE_MONITOR.log', 'DURABLE_MONITOR_STATUS.json']:
            p = W / n
            z.write(p, n)
            index[n] = dict(bytes=p.stat().st_size, sha256=sha(p))
    save(A / 'MONITORING_INDEX.json', index)
    save(A / 'COMPARISON_VERIFICATION.json', verification)
    save(A / 'ORIGINAL_FAILED_IDENTITIES_RECHECKED.json', rechecks)
    prior_path = R / json.loads((D / 'BROAD_INVENTORY.json').read_text())['priorBroadResultPath']
    prior_broad = json.loads(prior_path.read_text())
    previous_rechecks = []
    for t in prior_broad['tests']:
        if t['outcome'] not in ['FAILED', 'ERROR']:
            continue
        key = (t['testClass'], t['method'])
        assert key in by
        previous_rechecks.append(dict(testClass=key[0], method=key[1], originalOutcome=t['outcome'], followupOutcome=by[key]['outcome']))
    assert len(previous_rechecks) == 2
    save(A / 'PRIOR_BROAD_FAILURES_RECHECKED.json', dict(originalSource=prior_broad['sourceCommit'], originalCounts=prior_broad['testCounts'], broadSource=source, rechecked=previous_rechecks, scopesNotSummed=True))
    n = r['testCounts']
    counts = f"{n['reported']} reported / {n['passed']} passed / {n['failed']} failed / {n['errors']} errors / {n['skipped']} skipped"
    failures = [dict(testClass=t['testClass'], method=t['method']) for t in r['tests'] if t['outcome'] in ['FAILED', 'ERROR']]
    skips = [dict(testClass=t['testClass'], method=t['method']) for t in r['tests'] if t['outcome'] == 'SKIPPED']
    physical_skip = [dict(testClass='dev.russiancompanion.acceptance.AcceptanceBoundaryTest', method='at020_physicalRebootPersistsGovernedRecords')]
    good = r['exitCode'] == 0 and not failures and not m['newNativeReports'] and skips == physical_skip and all(t['followupOutcome'] == 'PASSED' for t in rechecks + previous_rechecks)
    report = f'''# Unit12 current broad selected-host regression completed

Actual tested source `{source}`. Started {c['startedUtc']}; finished {r['finishedUtc']} (UTC). **{counts}**,253classes,actual Gradle/runner exit{r['exitCode']}. Fresh discovery/XML/unique events/outcomes/exact1458method identities/source roots/six input trees match. All1441prior broad identities plus17Unit12methods/fourclasses retained,zero removals. Discovery preserves its own actual source/root separately across audit-only checkpoints;all six inputs remain identical. This is selected-host regression,not complete app/curriculum/device acceptance or independent audit/finding closure.

Failed/error identities:{json.dumps(failures)}. Skips:{json.dumps(skips)}. Exact commands/activation/initializers/source/root/inputs/XML/events/GC/lint/rawlogs/exits/full failure messages are in ATTEMPT_3/RAW.zip with indexed SHA256. All8original Unit12integration failures re-executed;exact outcomes in ORIGINAL_FAILED_IDENTITIES_RECHECKED.json. The2original Unit11broad failure identities are recorded separately in PRIOR_BROAD_FAILURES_RECHECKED.json;overlapping identities are not added.

Original6e485a79462reported/453pass/8fail/1physicalskip/exit1(ATTEMPT_1) and separate6959ab29140/140methods/20classes/exit0(ATTEMPT_2) remain immutable,separate and unsummed. Previous3434fb831441reported/1438pass/2fail/1physicalskip/exit1 remains immutable in the Unit11evidence directory. Both Unit11mapping correctionff737699 and Unit12comparison correction6959ab29 are ancestors of this broad source. All271testfiles/six input trees match reviewed b8f554bf inputs;actual test tree equals6959ab29. The older Unit11worker's pre-mapping test-tree assumption was not reused. Original assertions/source pins/current controls remain intact;155records and73archives verified unchanged. No generator ran here.

Lint:{json.dumps(r['lintIssuesBySeverity'],sort_keys=True)}. Exact task/cache states:{json.dumps(r['tasks'],sort_keys=True)}. Gate attachment is not release approval. Native reports:{json.dumps(m['newNativeReports'])}. APK identity/signature not recorded;assembly/install/signingNOT_RUN. Production sourceca0fdaeca8de81ab6a5d4935fbcdcb068c4af913/canonical84b6fda72017ec2dfc2b4add6c0e82b1a1e9f40769773f18d50caee4cf7a2355 unchanged. Earlier two153file renders/all30packages each/95identity-only binding changes/60worksheets/95historical versions conserved;reviewer/owner/rights/mapping/admission/approval fields unchanged. Candidate unfrozen/sourceCommitnull/artifactNOT_RECORDED/publicReleaseNOT_APPROVED.

Historical mainff539220563reported562pass1skip was a different selected scope,not complete-suite evidence. PR64420f41ff691was source inventory/unverified handoff execution,with67focused methods verified separately. No counts are added across overlapping sources/scopes.

Four screenshot exporters16methods explicitly excluded:VisualReviewCaptureTest2,Units35ChunkCaptureTest2,Units610ChunkCaptureTest10,Units1115GapCaptureTest2. Provisional SQLite wrapper JITdirective excludes compilation,not tests;unadopted/native causeUNCONFIRMED. All48independent findingsOPEN/auditINCOMPLETE;20print-singleton policies/qualified linguistic-source-rights-mapping-owner reviews/CC048authority conflict/originalUnit7restorecause remain outstanding. Physical Galaxy S26 Ultra airplane-mode/reboot acceptance LAST after final independent comprehensive nonphysical audit. No merge/freeze/formal advancement/releaseapproval. Private Penguin unread/unpackaged/environment settings unchanged;earlier publication user-confirmed independently unobserved.
'''
    (D / 'BROAD_REPORT.md').write_text(report)
    (D / 'BROAD_STATUS.md').write_text(report)
    next_task = ('Next large task: bounded Unit13 cumulative-review research using actual current source,authority requirements and admitted material. Inventory objective/teaching/practice/check/feedback/repair/source chains and genuine historical compatibility before proposing any new obligation. Preserve existing policies,saved versions,captures and assertions. Research is not qualified linguistic/rights/mapping/owner acceptance or automatic finding closure.'
                 if good else 'Required stop: inspect ATTEMPT_3/FAILED_METHODS.json and every full raw failure or unexpected skip before edits. Preserve this actual broad result and original assertions/pins/current controls;distinguish current defects from exact historical assumptions and verify affected full classes separately without relabeling or summing runs.')
    (D / 'NEXT_TASK.md').write_text('# Next task after Unit12 broad regression\n\n' + next_task + '\n\nRead BROAD_REPORT.md. One agent/existing checkout/DraftPR64. No active test job at this completed checkpoint. All48OPEN/auditINCOMPLETE/four exporters excluded/physicalLAST/noAPK/signing/merge/freeze/approval.\n')
    pointers = [R / 'AGENTS.md', R / 'audit/content-corrections/CODEX_ENGINEERING_CONTINUATION.md', R / 'audit/content-corrections/CODEX_CHECKPOINT_2026-10-12.md']
    for p in pointers:
        link = ('audit/' if p.name == 'AGENTS.md' else '../') + D.relative_to(R / 'audit').as_posix() + '/BROAD_REPORT.md'
        pointer = f'Current checkpoint: [Unit12 current broad selected-host regression completed]({link}). Actual{source}:{counts},253classes,exit{r["exitCode"]}. Fresh discovery/XML/events/six inputs verified;all1441priorIDs+17Unit12methods/no removals. All8original integration and2prior broad failures rechecked separately;original462/140/1441scopes retained/unsummed. All271testfiles/assertions/pins/155records/73archives conserved. Read NEXT_TASK.md. All48OPEN/auditINCOMPLETE/four exporters excluded/physicalLAST/noAPK/signing/merge/freeze/approval.\n\n'
        p.write_text(pointer + p.read_text())

    def manifest():
        save(D / 'SHA256.json', {p.relative_to(D).as_posix(): sha(p) for p in sorted(D.rglob('*')) if p.is_file() and p.name != 'SHA256.json'})
        run(['git', 'add', str(D), *map(str, pointers)])

    manifest()
    final = json.loads(run(['python3', str(D / 'verify_saved_results.py')]))
    save(A / 'FINAL_VERIFICATION.json', final)
    manifest()
    allowed = {p.relative_to(R).as_posix() for p in pointers}
    changes = run(['git', 'diff', '--cached', '--name-only']).splitlines()
    assert changes and all(p in allowed or p.startswith(D.relative_to(R).as_posix() + '/') for p in changes)
    run(['git', 'diff', '--cached', '--check'])
    run(['git', 'commit', '-m', 'Preserve actual Unit12 broad host regression evidence and checkpoint'])
    head = run(['git', 'rev-parse', 'HEAD'])
    run(['git', 'push', 'origin', 'HEAD:' + branch])
    body = f'''Russian Companion versioned curriculum corrections remain in progress. Current Unit12gender/past-agreement review preserves genuine saved-v1 obligations and downstream owner compatibility;authentic source bindings and original historical assertions/pins remain intact. Checkpoint `{head}`;DraftPR64 remains unmerged/unapproved.

Actual selected-host broad regression at `{source}`: **{counts}**,253classes,exit{r['exitCode']}. Fresh discovery/XML/events/exact1458identities/source roots/six input trees verified;all1441priorIDs+17Unit12methods/no removals. Failed/error identities:{json.dumps(failures)}. Skips:{json.dumps(skips)}. Lint:{json.dumps(r['lintIssuesBySeverity'])};gate:{r['tasks']['verifyReleaseGateAttachment']}. Full raw evidence in ATTEMPT_3. Not complete app/curriculum/device acceptance or independent finding closure.

Original6e485a79462scope453pass/8fail/1physicalskip/exit1 and separate6959ab29140/140follow-up remain immutable,separate/unsummed;previous3434fb831441scope1438pass/2fail/1physicalskip/exit1 also retained. All8original integration failures and2earlier broad failures rechecked in separate tables;overlapping identities not added. Both ff737699mapping and6959ab29comparison corrections included;all271testfiles match reviewed checkpoint. All original assertions/source pins/current controls/155records/73archives conserved. No generator or application/test/source edit during this run.

Report:`{D.relative_to(R)}/BROAD_REPORT.md`;exact commands/input identities/XML/events/exits/lint/full failures in ATTEMPT_3/RAW.zip. AGENTS/continuation/checkpoint/NEXT_TASK updated. {next_task}

All48findingsOPEN/auditINCOMPLETE;four screenshot exporters16methods excluded. Physical Galaxy S26 Ultra airplane-mode/reboot acceptance outstanding/LAST after final independent comprehensive nonphysical audit. Qualified reviews/20print-singleton policies/CC048authority conflict/originalUnit7restorecause outstanding. No APKassembly/install/signing/merge/freeze/formal advancement/releaseapproval;APKidentity/signature not recorded. Private PDF/environment settings unchanged;prior publication user-confirmed independently unobserved. Provisional JITdirective unadopted/native causeUNCONFIRMED.
'''
    body_path = W / 'PR64_BROAD_COMPLETED_BODY.md'
    body_path.write_text(body)
    pr = json.loads(run(['gh', 'api', '--method', 'PATCH', 'repos/justinkulka-lang/russian-companion/pulls/64', '-F', 'body=@' + str(body_path), '--jq', '{number,draft,state,head:.head.sha}']))
    assert pr['draft'] and pr['state'] == 'open' and pr['head'] == head
    assert not run(['git', 'status', '--porcelain'])
    save(W / 'AUTO_COMPLETION_RESULT.json', dict(completedUtc=now(), testedSource=source, evidenceCommit=head,
         counts=n, classes=253, exitCode=r['exitCode'], originalEightRechecked=rechecks, priorBroadTwoRechecked=previous_rechecks, failures=failures, skipped=skips,
         pushed=True, prUpdated=True, all48Findings='OPEN', audit='INCOMPLETE', physicalAcceptance='OUTSTANDING_LAST'))
    print((W / 'AUTO_COMPLETION_RESULT.json').read_text(), flush=True)
except BaseException:
    save(W / 'AUTO_COMPLETION_ERROR.json', dict(atUtc=now(), traceback=traceback.format_exc(),
         instruction='Preserve all raw/staged evidence; diagnose before duplicate launch/collection or edits; no force-push'))
    raise
