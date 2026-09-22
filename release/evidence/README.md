# Retained evidence directory

This directory is the only path the release gate accepts for
mandatory physical and owner procedures (`release/evidence/…json`).

A retained file is accepted only when it is a machine-readable
procedure-evidence document that records `PASS`, the candidate
commit, the APK SHA-256, the current source digest, `versionName`,
`versionCode`, `applicationId`, the execution timestamp, the device
for physical procedures, and referenced artifact hashes. The gate
recomputes the source digest on every release build and fails closed
if it no longer matches. The digest includes the immutable acceptance
procedures (`DEVICE_ACCEPTANCE.md` and `release/acceptance/templates/`)
and excludes executed evidence. AT-031 cannot clear release until
AT-001, AT-002, AT-004, AT-008, AT-009, AT-010, AT-020, and AT-030
are retained under the same frozen candidate commit, APK hash, and
current source digest. The candidate must be frozen with a recorded
artifact before evidence can approve release.

No device procedure has been executed. Do not place placeholder
pass files here. `release/retained-evidence.json` stays `MISSING`
until a real evidence document exists.

See `release/acceptance/DEVICE_ACCEPTANCE.md` for filenames after a
physical Galaxy S26 Ultra run.
