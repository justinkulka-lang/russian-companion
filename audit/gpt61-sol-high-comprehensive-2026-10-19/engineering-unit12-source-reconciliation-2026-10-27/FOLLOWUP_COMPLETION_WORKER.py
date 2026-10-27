"""Exclusive single-agent follow-up evidence owner; never changes test/application inputs."""
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
    assert r['testCounts']['reported'] == 140 and r['classes'] == 20
    assert end['runnerExit'] == r['exitCode'] == int(end['gradleExit'])
    while True:
        status = W / 'DURABLE_MONITOR_STATUS.json'
        if status.exists() and json.loads(status.read_text())['completionExists']:
            break
        time.sleep(5)
    m = json.loads(status.read_text())
    verification = json.loads(run(['python3', str(D / 'verify_followup_comparisons.py')]))
    old = json.loads((D / 'ATTEMPT_1/FAILED_METHODS.json').read_text())
    by = {(t['testClass'], t['method']): t for t in r['tests']}
    rechecks = []
    for t in old:
        key = (t['testClass'], t['method'])
        assert key in by
        rechecks.append(dict(testClass=key[0], method=key[1], originalOutcome='FAILED',
                             followupOutcome=by[key]['outcome']))
    run(['python3', str(W / 'collect_attempt.py'), 'ATTEMPT_2'])
    A = D / 'ATTEMPT_2'
    index = {}
    with zipfile.ZipFile(A / 'MONITORING.zip', 'x', zipfile.ZIP_DEFLATED) as z:
        for n in ['durable_monitor.py', 'DURABLE_MONITOR.log', 'DURABLE_MONITOR_STATUS.json']:
            p = W / n
            z.write(p, n)
            index[n] = dict(bytes=p.stat().st_size, sha256=sha(p))
    save(A / 'MONITORING_INDEX.json', index)
    save(A / 'COMPARISON_VERIFICATION.json', verification)
    save(A / 'ORIGINAL_FAILED_IDENTITIES_RECHECKED.json', rechecks)
    n = r['testCounts']
    counts = f"{n['reported']} reported / {n['passed']} passed / {n['failed']} failed / {n['errors']} errors / {n['skipped']} skipped"
    failures = [dict(testClass=t['testClass'], method=t['method']) for t in r['tests'] if t['outcome'] in ['FAILED', 'ERROR']]
    good = r['exitCode'] == 0 and not failures and all(t['followupOutcome'] == 'PASSED' for t in rechecks)
    report = f'''# Unit12 historical comparison follow-up completed

Actual tested source `{source}`. Started {c['startedUtc']}; finished {r['finishedUtc']} (UTC). **{counts}**, 20 full classes, actual Gradle/runner exit {r['exitCode']}. Fresh discovery/XML/unique events/outcomes/source root/six input trees match. All eight original failed identities re-executed; their exact follow-up outcomes are in ATTEMPT_2/ORIGINAL_FAILED_IDENTITIES_RECHECKED.json. New failed/error identities: {json.dumps(failures)}. This is a selected host follow-up, not complete-suite/app/curriculum/device or independent acceptance. Overlapping scopes are not summed.

Original source `6e485a79fd617c20f6f7ece94c5e1bdc9c505ec3` remains 462 reported / 453 passed / 8 failed / 0 errors / 1 physical skip, 79 classes, exit 1 in immutable ATTEMPT_1. FAILURE_REVIEW.md and FULL_FAILURE_REVIEW.json preserve the full-message diagnosis and all hashes. This follow-up does not relabel that original run.

Six test/support files preserve all 618 original assertion expressions and all source/digest pins. Originals/Gitblobs/SHA are archived in FOLLOWUP_ORIGINAL_TESTS.zip. Two nonassert reference reads use the genuine matching Unit11 reference/kernel pair at 05407a70d1d70b196ef21b25cff51357c072c087. Current Unit12 hash/append controls remain unchanged. Only the older Unit11 whole-value equalities scope through strict before12; lifecycle and mutation checks still use actual-current input. Two original Unit12 v1 objective/owner scenarios use the exact before12 declaration; all current Unit12 v2 baseline/core/UI/binding controls remain unchanged. Three mapping comparisons retain the old v1 digest on full-pack-proven historical input with added current-source/digest guards. All four direct mapping-helper callers are included. The remaining 265 preexisting test files and production/catalog/docs/review/release are unchanged.

All 155 source-binding records remain unchanged by these test corrections. The previous two153file JVM renders/95identity-only changes/60unchanged worksheets/95historical versions remain verified. All 73 learner/report archives remain SHA/CRC verified. Production source ca0fdaeca8de81ab6a5d4935fbcdcb068c4af913; canonical digest84b6fda72017ec2dfc2b4add6c0e82b1a1e9f40769773f18d50caee4cf7a2355 unchanged. Candidate unfrozen/sourceCommitnull/artifactNOT_RECORDED/publicReleaseNOT_APPROVED.

Lint: {json.dumps(r['lintIssuesBySeverity'], sort_keys=True)}. Exact task/cache statuses: {json.dumps(r['tasks'], sort_keys=True)}. Gate attachment is not release approval. Full commands, activation, initializers, input identities, XML, events, GC, lint and exits are retained in ATTEMPT_2/RAW.zip with indexed hashes. Native reports observed: {json.dumps(m['newNativeReports'])}. APK identity/signature not recorded; assembly/install/signing NOT_RUN.

Earlier core15/15, Unit11selected69/69 and broad1441reported/1438pass/2fail/1physicalskip are separate, unsummed sources/scopes. Historical mainff539220563reported562pass1skip is not a complete-suite claim. PR64420f41ff691was source inventory/unverified handoff execution, with67focused methods verified separately.

Four screenshot exporters /16methods explicitly excluded: VisualReviewCaptureTest(2), Units35ChunkCaptureTest(2), Units610ChunkCaptureTest(10), Units1115GapCaptureTest(2). All48 independent findings OPEN; audit INCOMPLETE. Physical Galaxy S26 Ultra airplane-mode/reboot acceptance remains outstanding and LAST after the final independent comprehensive nonphysical audit. Qualified linguistic/source/rights/mapping/owner reviews,20print-singleton policies,CC048authority conflict and original Unit7restorecause remain outstanding. No merge/freeze/formal advancement/release approval. Private Penguin unread/unpackaged; environment settings unchanged, prior publication user-confirmed independently unobserved. Provisional JIT directive excludes wrapper compilation,not tests; unadopted/native cause UNCONFIRMED.
'''
    (D / 'FOLLOWUP_REPORT.md').write_text(report)
    (D / 'FOLLOWUP_STATUS.md').write_text(report)
    next_task = ('Next large task: fresh broad discovery and selected-host regression after Unit12 production/source changes. Compare all1441prior broad identities and derive actual additions/removals. Include current follow-up test tree; no broad result at this Unit12 source is claimed. Keep all input trees fixed and preserve every actual outcome.'
                 if good else 'Required stop: review every new ATTEMPT_2 full raw failure before further edits. Preserve both actual scopes, original assertions and pins; do not relabel or sum the runs.')
    (D / 'NEXT_TASK.md').write_text('# Next task after Unit12 comparison follow-up\n\n' + next_task + '\n\nRead FOLLOWUP_REPORT.md. One agent/existing checkout/DraftPR64. No active test job at this completed checkpoint. All48OPEN/auditINCOMPLETE/four exporters excluded/physicalLAST/noAPK/signing/merge/freeze/approval.\n')
    pointers = [R / 'AGENTS.md', R / 'audit/content-corrections/CODEX_ENGINEERING_CONTINUATION.md', R / 'audit/content-corrections/CODEX_CHECKPOINT_2026-10-12.md']
    for p in pointers:
        link = ('audit/' if p.name == 'AGENTS.md' else '../') + D.relative_to(R / 'audit').as_posix() + '/FOLLOWUP_REPORT.md'
        pointer = f'Current checkpoint: [Unit12 historical comparison follow-up completed]({link}). Actual{source}:{counts},20classes,exit{r["exitCode"]}. All8original failed identities rechecked;original462scope453pass/8fail/1physicalskip retained,separate/unsummed. Sixfiles/all618original assertions/pins/currentguards/155records/73archives conserved. Read NEXT_TASK.md. All48OPEN/auditINCOMPLETE/four exporters excluded/physicalLAST/noAPK/signing/merge/freeze/approval.\n\n'
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
    run(['git', 'commit', '-m', 'Preserve actual Unit12 comparison follow-up and original failures'])
    head = run(['git', 'rev-parse', 'HEAD'])
    run(['git', 'push', 'origin', 'HEAD:' + branch])
    body = f'''Unit12 versioned gender/past-agreement review and authentic source bindings are implemented; historical-comparison follow-up is recorded at `{head}`.

Actual source `{source}`: **{counts}**,20fullclasses,exit{r['exitCode']}. All eight original failed identities re-executed; fresh discovery/XML/events/source root/six input trees verified. New failures: {json.dumps(failures)}. Lint: {json.dumps(r['lintIssuesBySeverity'])}; gate: {r['tasks']['verifyReleaseGateAttachment']}. Selected host scope,not complete-suite or independent acceptance.

Original6e485a79462scope453pass/8fail/1physicalskip/exit1 remains immutable and separate. Six test/support files preserve all618original assertions and source pins, genuine historical reference/kernel pair, and actual-current Unit11/Unit12/mutation/owner controls. All four mapping-helper callers included. Production,155bindings,265other testfiles and73archives unchanged by comparison corrections. Prior two153file renders/95identity-only binding updates/60worksheets/95historical versions conserved; canonical digest84b6fda7…a2355. Overlapping scopes never summed.

Report:`{D.relative_to(R)}/FOLLOWUP_REPORT.md`; exact raw commands/XML/events/exits/lint/input identities in ATTEMPT_2/RAW.zip; original failures in ATTEMPT_1. AGENTS/continuation/checkpoint/NEXT_TASK updated.

All48findingsOPEN/auditINCOMPLETE; four screenshot exporters16methods excluded. Physical Galaxy S26 Ultra airplane-mode/reboot acceptance outstanding/LAST after final independent comprehensive nonphysical audit. Qualified reviews/20print-singleton policies/CC048authority conflict/originalUnit7restorecause outstanding. No APKassembly/install/signing/merge/freeze/formal advancement/releaseapproval; APKidentity/signature not recorded. Private PDF/environment settings unchanged; prior publication user-confirmed independently unobserved. Provisional JIT directive unadopted/native causeUNCONFIRMED.
'''
    body_path = W / 'PR64_FOLLOWUP_COMPLETED_BODY.md'
    body_path.write_text(body)
    pr = json.loads(run(['gh', 'api', '--method', 'PATCH', 'repos/justinkulka-lang/russian-companion/pulls/64', '-F', 'body=@' + str(body_path), '--jq', '{number,draft,state,head:.head.sha}']))
    assert pr['draft'] and pr['state'] == 'open' and pr['head'] == head
    assert not run(['git', 'status', '--porcelain'])
    save(W / 'AUTO_COMPLETION_RESULT.json', dict(completedUtc=now(), testedSource=source, evidenceCommit=head,
         counts=n, classes=20, exitCode=r['exitCode'], originalEightRechecked=rechecks, failures=failures,
         pushed=True, prUpdated=True, all48Findings='OPEN', audit='INCOMPLETE', physicalAcceptance='OUTSTANDING_LAST'))
    print((W / 'AUTO_COMPLETION_RESULT.json').read_text(), flush=True)
except BaseException:
    save(W / 'AUTO_COMPLETION_ERROR.json', dict(atUtc=now(), traceback=traceback.format_exc(),
         instruction='Preserve all raw/staged evidence; diagnose before duplicate launch/collection or edits; no force-push'))
    raise
