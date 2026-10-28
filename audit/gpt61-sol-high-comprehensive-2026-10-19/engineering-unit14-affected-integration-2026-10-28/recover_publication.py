"""Lossless packaging recovery only. Never runs Gradle or changes app/test inputs."""
from evidence import *
from datetime import datetime, timezone
import shutil

assert git('rev-parse', 'HEAD') == 'd9ad33ec63df2212a81b05468c837f648fa1894b'
assert not git('status', '--porcelain')
for pid in [71491, 71424, 71427]:
    p = Path('/proc') / str(pid) / 'stat'
    assert not p.exists() or p.read_text().rsplit(')', 1)[1].split()[0] == 'Z'
original = (D/'ACTUAL_FAILURES.json').read_bytes()
full = json.loads(original)
assert full['count'] == 14
backup = W/'PUBLICATION_RECOVERY_ORIGINALS'
backup.mkdir(exist_ok=False)
(backup/'ACTUAL_FAILURES.json').write_bytes(original)
names = ['collect.py', 'verify_results.py', 'completion_worker.py', 'REPORT.md', 'SHA256.json']
with zipfile.ZipFile(D/'PUBLICATION_RECOVERY_ORIGINALS.zip', 'x', zipfile.ZIP_DEFLATED) as z:
    for name in names:
        z.writestr(name, (D/name).read_bytes())
    for name in ['AUTO_ERROR.json', 'FINAL_VERIFICATION.json', 'COLLECTION.log']:
        z.writestr(name, (W/name).read_bytes())
with zipfile.ZipFile(D/'ACTUAL_FAILURES_FULL.zip', 'x', zipfile.ZIP_DEFLATED) as z:
    z.writestr('ACTUAL_FAILURES.json', original)
with zipfile.ZipFile(D/'ACTUAL_FAILURES_FULL.zip') as z:
    assert z.testzip() is None and z.read('ACTUAL_FAILURES.json') == original
archive = D/'ACTUAL_FAILURES_FULL.zip'
index = dict(sourceCommit=full['sourceCommit'], count=full['count'],
    originalAssertionsNotWaived=full['originalAssertionsNotWaived'],
    storage=dict(format='LOSSLESS_ZIP', archive=archive.name, archiveSha256=sha(archive.read_bytes()),
        member='ACTUAL_FAILURES.json', memberBytes=len(original), memberSha256=sha(original)),
    failures=[dict(testClass=f['testClass'], method=f['method'], kind=f['kind'],
        **{key+suffix: value for key in ['message', 'trace']
           for suffix, value in [('Bytes', len(f[key].encode())), ('Sha256', sha(f[key].encode()))]})
        for f in full['failures']])
save(D/'ACTUAL_FAILURES.json', index)
save(D/'PUBLICATION_RECOVERY.json', dict(
    recoveredUtc=datetime.now(timezone.utc).isoformat(),
    testedSource=full['sourceCommit'], rejectedUnpublishedCommit=git('rev-parse','HEAD'),
    remoteAtRejection='dd736e1a8657bd02a34063752afd82146ab2f842',
    originalFileBytes=len(original), originalFileSha256=sha(original),
    exactFullFailureStorage=index['storage'],
    rejection='GitHub GH001: ACTUAL_FAILURES.json 140.37 MiB exceeds 100 MiB file limit',
    originalError='PUBLICATION_RECOVERY_ORIGINALS.zip/AUTO_ERROR.json',
    originalVerifier='PUBLICATION_RECOVERY_ORIGINALS.zip/verify_results.py',
    handling='Amend only this session-owned rejected/unpublished audit-docs commit; normal push, no force push',
    runtimeRerun=False, inputsChanged=False, failureBytesRemoved=False,
    all48Findings='OPEN', audit='INCOMPLETE', physicalAcceptance='OUTSTANDING_LAST'))
shutil.copy2(W/'READ_ONLY_ALL_FAILURE_TRIAGE.json', D/'READ_ONLY_ALL_FAILURE_TRIAGE.json')
print(json.dumps(dict(originalBytes=len(original), compressedBytes=archive.stat().st_size,
    originalSha256=sha(original), byteExact=True, failureCount=full['count'])))
