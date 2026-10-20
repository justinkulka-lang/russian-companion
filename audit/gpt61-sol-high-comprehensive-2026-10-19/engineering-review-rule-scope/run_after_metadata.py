"""Preserve legacy assertions and check new allocation after prior jobs finish."""
from pathlib import Path
import subprocess, json, hashlib, time, shutil, zipfile

ROOT = Path('/workspace/russian-companion')
BASE = Path('/workspace/artifacts/post-correction-broad-2026-10-20')
HERE = BASE / 'review-rule-diagnostic'
def command(*args):
    return subprocess.check_output(list(args), cwd=ROOT, text=True).strip()
def sha(path):
    return hashlib.sha256(path.read_bytes()).hexdigest()
def save(path, value):
    path.write_text(json.dumps(value, indent=2) + '\n')

deadline = time.monotonic() + 8 * 3600
marker = BASE / 'metadata-proposal/PUBLICATION_COMPLETE.json'
while not marker.exists():
    if time.monotonic() > deadline:
        raise RuntimeError('Prior metadata publication still unverified; no patch applied')
    time.sleep(15)
previous = json.loads(marker.read_text())
counts = previous['counts']
assert counts == dict(reported=20, passed=20, failed=0, errors=0, skipped=0), counts
prior_result = json.loads((BASE / 'metadata-proposal/VERIFIED/RESULT.json').read_text())
assert prior_result['exitCode'] == 0
assert command('git', 'rev-parse', 'HEAD') == previous['evidenceCommit']
assert not command('git', 'status', '--porcelain')
assert command('git', 'branch', '--show-current') == 'cursor/content-audit-corrections-964f'
assert command('git', 'ls-remote', 'origin', 'refs/heads/cursor/content-audit-corrections-964f').split()[0] == previous['evidenceCommit']
pr = json.loads(command('gh', 'api', 'repos/justinkulka-lang/russian-companion/pulls/64?rule=' + str(int(time.time())), '--jq', '{state,draft,sha:.head.sha}'))
assert pr == dict(state='open', draft=True, sha=previous['evidenceCommit'])
action = json.loads((HERE / 'QUEUED_ACTION.json').read_text())
target = ROOT / action['file']
assert sha(target) == action['beforeSha256']
assert sha(HERE / 'PROPOSED_RULE_ASSERTIONS.patch') == action['patchSha256']
prior_context = json.loads((BASE / 'metadata-proposal/VERIFIED/CONTEXT.json').read_text())
for path, digest in prior_context['inputTrees'].items():
    assert command('git', 'rev-parse', 'HEAD:' + path) == digest, path
subprocess.run(['git', 'apply', '--check', str(HERE / 'PROPOSED_RULE_ASSERTIONS.patch')], cwd=ROOT, check=True)
subprocess.run(['git', 'apply', str(HERE / 'PROPOSED_RULE_ASSERTIONS.patch')], cwd=ROOT, check=True)
assert sha(target) == action['afterSha256']
assert command('git', 'diff', '--name-only').splitlines() == [action['file']]
subprocess.run(['git', 'add', '--', action['file']], cwd=ROOT, check=True)
subprocess.run(['git', 'diff', '--cached', '--check'], cwd=ROOT, check=True)
subprocess.run(['git', 'commit', '-qm', 'Assert legacy review preservation separately from new presentation allocation'], cwd=ROOT, check=True)
source = command('git', 'rev-parse', 'HEAD')
save(HERE / 'APPLIED.json', dict(sourceCommit=source, previousEvidenceCommit=previous['evidenceCommit'], appCodeChanged=False, legacySemanticsChanged=False))
subprocess.run(['python', str(HERE / 'prepare_run.py'), 'VERIFIED'], cwd=ROOT, check=True)
run = HERE / 'VERIFIED'
execution = subprocess.run(['bash', str(run / 'run.sh')], cwd=ROOT)
subprocess.run(['python', str(HERE / 'collect_run.py'), 'VERIFIED'], cwd=ROOT, check=True)
result = json.loads((run / 'RESULT.json').read_text())
assert result['exitCode'] == execution.returncode
assert result['testCounts']['reported'] == 9
assert result['selectedSourceInventoryMatchesFreshXmlAndEvents'] and result['xmlFreshRelativeToRunScript']
assert command('git', 'rev-parse', 'HEAD') == source and not command('git', 'status', '--porcelain')
for path in ['app/src/main', 'catalog', 'docs', 'review']:
    assert command('git', 'rev-parse', 'HEAD:' + path) == prior_context['inputTrees'][path]
assert command('git', 'diff', '--name-only', previous['evidenceCommit'], source).splitlines() == [action['file']]
allow = json.loads((ROOT / 'audit/independent-gpt61-2026-10-12/current-comprehensive-audit/RETAINED_ARCHIVE_ALLOWLIST.json').read_text())['sha256']
assert len(allow) == 66 and all(sha(ROOT / p) == h for p, h in allow.items())
out = ROOT / 'audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-review-rule-scope'
out.mkdir(exist_ok=False)
for name in ['PROPOSED_RULE_ASSERTIONS.patch', 'QUEUED_ACTION.json', 'STOPPED_BASELINE_PROJECTION.json', 'APPLIED.json', 'run_after_metadata.py', 'prepare_run.py', 'collect_run.py']:
    shutil.copy2(HERE / name, out / name)
for name in ['CONTEXT.json', 'RESULT.json', 'RUN.exit', 'LINT.xml']:
    shutil.copy2(run / name, out / name)
files = [p for p in sorted(run.rglob('*')) if p.is_file()]
index = {str(p.relative_to(run)): sha(p) for p in files}
archive = out / 'RAW_VERIFICATION.zip'
with zipfile.ZipFile(archive, 'w', zipfile.ZIP_DEFLATED, compresslevel=6) as z:
    for p in files:
        z.write(p, str(p.relative_to(run)))
with zipfile.ZipFile(archive) as z:
    assert z.testzip() is None
    assert all(hashlib.sha256(z.read(n)).hexdigest() == h for n, h in index.items())
save(out / 'RAW_EVIDENCE_INDEX.json', dict(archiveSha256=sha(archive), archiveBytes=archive.stat().st_size, members=index, crcAndEveryMemberHashVerified=True))
save(out / 'CONSERVATION.json', dict(all66ArchiveHashesMatched=allow, appMainCatalogDocsReviewUnchanged=True, onlyOneTestFileChanged=True, legacySemanticsChanged=False, physicalAcceptance='OUTSTANDING_LAST', apkAssemblyInstallSigning=False, formalApproval=False))
c = result['testCounts']
summary = (f"Review-rule assertion scope tested at `{source}`:9selected existing methods/2classes,"
           f"{c['passed']}passed/{c['failed']}failed/{c['errors']}errors/{c['skipped']}skipped. "
           'Saved legacy assignments keep exact hashes,raw option order and stored history;fresh allocation is checked against the separately versioned presentation rule. '
           'Every other assignment field is compared exactly. No app/content/history/policy/approval change;focused scope,not a complete suite or independent audit closure.')
report = '# Review-rule allocation and legacy preservation\n\n' + summary + '\n\n'
report += 'The original stopped broad nine-method projection is8SUCCESS/1FAILURE,not another executed baseline. The original failed whole-assignment equality and its complete event remain immutable in post-correction-broad-2026-10-20/REVIEW_RULE_DIAGNOSTIC. Original expected and actual assignments differ only in responseRuleDigest and its derived digest. Current new-allocation code deliberately uses the versioned presentation rule. The corrected test retains exact comparison of every other field,adds explicit new/legacy rule and hash assertions,and independently checks stored-assignment reuse,raw option order and unchanged full history. All remaining original assertions stay exact. No legacy rule or archived assignment is rewritten.\n\n'
report += 'Exact commands,source/root/input/init/test hashes,fresh XML/event identities,outcomes and underlying exit are retained. Tasks/lint:'+json.dumps(dict(tasks=result['tasks'],lint=result['lintIssuesBySeverity']))+'. Full raw execution ZIP CRC/every member hash verified. Original failures are preserved even if this focused scope passes.\n\n'
report += 'All66retained archive hashes match;all64authentic histories remain unchanged. Four screenshot exporters/16methods explicitly excluded:VisualReviewCaptureTest2,Units35ChunkCaptureTest2,Units610ChunkCaptureTest10,Units1115GapCaptureTest2. This selected scope does not run the separately failing full-corpus OptionPresentationTest method;it remainsOPEN and is not waived. Scope identities/counts are never summed with stopped broad,248continuation or20metadata methods. NoWRITE_* regeneration. ConfiguredAPK dev.russiancompanion/2.0.0-dev/code5;no assembly/install/signing,new artifact identity/signatureNOT_RECORDED.\n\n'
report += 'Remaining restoreUNCHANGED,fixture scope,current review-package bindings,corpus expectations and qualified content/source/rights/reference review remainOPEN. Cache-warm proposal remains unapplied/unqueued. Original audit/fourCSVs unrecovered. AuditINCOMPLETE;physicalGalaxyS26Ultraairplane-mode/reboot acceptanceLAST after final comprehensive independent nonphysical audit/recheck. Environment previouslysaved unchanged/publicationuserconfirmed independentlyunverified. No merge,freeze,formal stage advancement,owner acceptance or release approval.\n'
(out / 'ENGINEERING_CHANGE_REPORT.md').write_text(report)
save(out / 'SHA256.json', {str(p.relative_to(out)): sha(p) for p in sorted(out.rglob('*')) if p.is_file() and p.name != 'SHA256.json'})
for rel in ['audit/content-corrections/CODEX_ENGINEERING_CONTINUATION.md', 'audit/content-corrections/CODEX_CHECKPOINT_2026-10-12.md']:
    p = ROOT / rel
    p.write_text(p.read_text() + '\n\n## Review-rule assertion verification\n\n' + summary + ' Read engineering-review-rule-scope/ENGINEERING_CHANGE_REPORT.md under audit/gpt61-sol-high-comprehensive-2026-10-19/. Remaining failures/qualificationOPEN;physicalLAST.\n')
p = ROOT / 'AGENTS.md'
p.write_text(p.read_text().replace('# Codex continuation\n', '# Codex continuation\n\nCurrent engineering checkpoint: [review-rule scope verification](audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-review-rule-scope/ENGINEERING_CHANGE_REPORT.md). ' + summary + ' AuditINCOMPLETE;physicalLAST.\n', 1))
subprocess.run(['git', 'add', '--', 'AGENTS.md', 'audit/content-corrections/CODEX_ENGINEERING_CONTINUATION.md', 'audit/content-corrections/CODEX_CHECKPOINT_2026-10-12.md', str(out.relative_to(ROOT))], cwd=ROOT, check=True)
subprocess.run(['git', 'diff', '--cached', '--check'], cwd=ROOT, check=True)
subprocess.run(['git', 'commit', '-qm', 'Retain review-rule regression outcomes without closing independent audit findings'], cwd=ROOT, check=True)
subprocess.run(['git', 'push', 'origin', 'HEAD'], cwd=ROOT, check=True)
body = command('gh', 'api', 'repos/justinkulka-lang/russian-companion/pulls/64?body=' + str(int(time.time())), '--jq', '.body')
at = body.index('\n\n') + 2
body_file = HERE / 'PR64_BODY_VERIFIED.md'
body_file.write_text(body[:at] + summary + ' [Report and raw evidence](audit/gpt61-sol-high-comprehensive-2026-10-19/engineering-review-rule-scope/ENGINEERING_CHANGE_REPORT.md).\n\n' + body[at:])
subprocess.run(['gh', 'api', '--method', 'PATCH', 'repos/justinkulka-lang/russian-companion/pulls/64', '-F', 'body=@' + str(body_file), '--jq', '{state,draft,sha:.head.sha}'], cwd=ROOT, check=True)
save(HERE / 'PUBLICATION_COMPLETE.json', dict(sourceCommit=source, evidenceCommit=command('git', 'rev-parse', 'HEAD'), counts=c, draftPr=64, completeSuite=False, appCodeChanged=False))
