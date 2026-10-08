# Physical-device acceptance package

Status: **prepared, not executed**.

This package is the handoff for physical-device and catalog-audit
procedures on a Galaxy S26 Ultra. It is not retained evidence. It does
not satisfy AT-001, AT-002, AT-004, AT-008, AT-009, AT-010, AT-020,
AT-030, or AT-031. Public release remains **NOT APPROVED**.

AT-031 requires results from AT-001 through AT-030. This package
therefore includes AT-002, AT-008, AT-009, and AT-010 as well as the
device procedures. Do not proceed to AT-031 until those results are
actually retained.

Do not mark `release/retained-evidence.json` `RETAINED`. Do not fill
AT-031. Do not run `assembleRelease` as a substitute for device
evidence. Do not invent Russian, Penguin text, or procedure results.

Immutable acceptance procedures live in
`release/acceptance/DEVICE_ACCEPTANCE.md` and
`release/acceptance/templates/`. Those files are part of the release
source digest. `release/acceptance/forms/` is the working copy for
execution notes and is excluded from the digest. Machine-readable
results go only to `release/evidence/`.

Governing documents, in this order:

1. `docs/governance/MASTER_AUTHORITY_v1.0.md`
2. `docs/design/PRODUCT_BRIEF_v1.1.md`
3. `docs/design/FUNCTIONAL_DESIGN_v1.0.md`
4. `docs/design/TECHNICAL_ARCHITECTURE_v1.0.md`
5. `docs/design/ACCEPTANCE_CRITERIA_v1.0.md`

## 1. Candidate identification

The installable acceptance candidate is the **debug APK** produced by
the existing ungated Gradle task `assembleDebug` and **published as the
GitHub Actions artifact** `acceptance-candidate-debug-apk`. It is not a
public release. Do not rebuild locally and expect the same debug APK
hash. Reproducible-build equivalence has not been demonstrated.

| Field | Value |
|---|---|
| Application | Russian Companion (`dev.russiancompanion`) |
| versionName | `2.0.0-dev` |
| versionCode | `5` |
| Gradle task | `assembleDebug` |
| Artifact name | `acceptance-candidate-debug-apk` |
| Identity file in artifact | `artifact-identity.json` |
| 2.0.0-dev candidate | **not frozen**. `sourceCommit` is null. Artifact status is `NOT_RECORDED`. |
| Preserved 0.0.0 APK | run `35740338300`, SHA-256 `1a95949ba7e2edd0ca72dea18e728ec105e427a2e5f5c093ff33fe82dfcba33e` |
| Public release | `NOT_APPROVED` |

Do not download the preserved 0.0.0 APK as the Galaxy S26 Ultra
candidate. Keep it as a historical artifact. Do not freeze this
candidate, do not fill `sourceCommit`, and do not record an artifact
hash until a later independent audit says that work is in scope.

`assembleRelease`, `bundleRelease`, `packageRelease`,
`packageReleaseBundle`, and other release output tasks remain
fail-closed until AT-001, AT-002, AT-004, AT-008, AT-009, AT-010,
AT-020, AT-030, and AT-031 evidence is actually retained under
`release/evidence/` as machine-readable PASS documents.

## 2. Release-candidate / release-gate circularity

Release APK, AAB, and publish tasks depend on `checkReleaseGate`.
That gate requires retained machine-readable evidence for every
mandatory procedure, and AT-031 cannot clear until the others are
retained under the same candidate commit and APK hash. The physical
procedures require an installed debug candidate first.

The existing governed mechanism is `assembleDebug`:

- GitHub Actions already assembles debug and runs unit tests.
- `checkReleaseGate` is attached to every release output path
  (`assembleRelease`, `bundleRelease`, `packageRelease`,
  `packageReleaseBundle`, publish, install, and variant-specific
  release packaging).
- Architecture §14 distinguishes a native Android build from the
  public-release confirmation gate.

Using the debug APK as the **acceptance candidate** does not weaken,
bypass, or pre-clear the release gate. Share-alike confirmation stays
`CONFIRMED` / `ACTIVATED` / `SHARE_ALIKE_CONFIRMED` from `0602eda` and
is not revised here.

## 3. Device and preconditions

Device: **Samsung Galaxy S26 Ultra**.

Clean-install only. Do not upgrade over a previous Russian Companion
install.

1. Download the CI artifact `acceptance-candidate-debug-apk` from the
   successful Build run. Do not use a locally rebuilt APK.
2. Verify SHA-256 of the extracted `app-debug.apk` against
   `artifact-identity.json` before copying it to the device. Use
   `sha256sum` (or equivalent) on the APK and compare it to the
   identity file. Record the run URL, SHA-256, and byte count on every
   result form.
3. Copy that exact `app-debug.apk` to the device (USB). Record the
   on-device path.
4. Settings → Apps → search **Russian Companion**. If present,
   Uninstall. Confirm it is gone.
5. Settings → Security and privacy → Install unknown apps → allow the
   Files app (or the installer you will use) for this one install.
6. Install the APK. Open App info and confirm:
   - App name: Russian Companion
   - Version: `2.0.0-dev`
7. Do **not** open the app yet.
8. Enable Airplane mode from Quick settings. Then open Settings →
   Connections and confirm:
   - Airplane mode is on
   - Mobile data is off
   - Wi-Fi is off (Samsung can re-enable Wi-Fi in airplane mode; leave
     it off)
   - Bluetooth is off
9. Confirm the status bar shows airplane mode and no cellular or Wi-Fi
   connected indicator. Take screenshot
   `AT-004-00-airplane-mode.png`.
10. There is no account or sign-in. Do not add a Google restore of this
    app's data. `android:allowBackup` is false.

Keep airplane mode on for the entire session, including process
restart and reboot.

Microphone: when the app first records, grant **Microphone** only.
Do not grant network-related permissions; the app does not request
`INTERNET`.

AnkiDroid and the book remain outside the app. Perform that work with
the user's own book and, if present, AnkiDroid. The app only records
self-attestation.

## 4. Combined physical run

Execute the following sequence once on the clean install. It covers
AT-001, AT-002, AT-004, AT-020 / MA-073, and AT-030. AT-008, AT-009,
and AT-010 use the same installed candidate plus the complete catalog
audits under `release/acceptance/audits/`. Fill the matching form
under `release/acceptance/forms/` as you go. Default result on every
form is **NOT_EXECUTED**. Change it only after the work. An AT-030
result does not stand in for AT-002, AT-008, AT-009, or AT-010.

Exact on-screen labels are quoted.

### A. Launch and AT-001 inspection

1. Open **Russian Companion**.
2. Onboarding page `Welcome` must show:
   - `Russian Companion is a fully offline course companion. It tells you the current unit, what to study in your own book, and what the app can practice with verified material.`
   - `Your data stays on this device. There is no account and no network.`
   Screenshot: `AT-001-01-identity.png`.
   The phrases `independent adult beginner` and `The New Penguin Russian Course`
   must not appear on screen.
3. Tap `Next` through `Study system`, `Starting point`, and
   `Daily commitment`. Leave `I have a tutor now` unselected.
   Daily time choices are `20 minutes`, `35 minutes`, and `50 minutes`.
   `Resume from a book lesson` must say later units are not yet
   installed and that the course still begins at Unit 1.
   Screenshot: `AT-001-02-limits.png`.
4. The last page must show `Unit 1 — Learning to read Russian`.
   Tap `Start Unit 1`.
5. Open `Course`. Confirm **Unit 1** through **Unit 30** are listed.
   Units that are still in development must show `In development` or
   `Not yet installed`. They must not be labeled `Installed`.
   Confirm there is no B1, B2, C1, handwriting course, dictionary,
   media library, social, or tutor-scheduling entry.
   Screenshots: `AT-001-03-lesson-1.png`, `AT-001-04-lesson-30.png`.
6. Confirm the tabs are `Today`, `Course`, `Practice`, `Reference`,
   and `Progress`. The obsolete label `Optional drills` must **not**
   appear. `Practice` does show optional practice cards (`Alphabet recognition`,
   `Reading Lab`, `Grammar Lab`, `Vocabulary and forms`, and `Mixed review`).
   Those cards are optional and must say that
   optional practice does not complete a required check. Listening,
   pronunciation, and handwriting must not be offered as scored work.
7. `Today` must show `Unit 1` and a `Continue` action.
   Screenshot: `AT-001-05-home.png`.

AT-001 expected result: the installed candidate is version `2.0.0-dev`
and offers the 30-unit map without describing unreviewed units as
installed. Onboarding stays on this device and does not display the
banned identity phrases. This procedure remains outstanding until a
later physical run records a result.

### B. AT-030 lesson cycle (also satisfies most of AT-004)

Keep airplane mode on.

#### B1. Today

1. On `Today`, confirm `Unit 1` and `Continue`.
2. Tap `Continue` and confirm the first Unit 1 step opens.
   Do not enter Penguin text.
   Screenshot: `AT-030-01-plan.png`.

#### B2. Book work outside the app

1. Using the user's own copy of the book, do the Unit 1 book work
   outside the app. Do not type book text into the app.
2. Open `Course`, open Unit 1, and tap
   `I completed the book work for this unit`.
3. Open `Progress` and confirm the book self-attestation line includes
   unit 1. This is a self-attestation, not a score and not linguistic
   acceptance.

#### B3. Closed-response check

1. Return to the Unit 1 step that asks for a closed response.
2. Select one displayed option. Confirm the app records that selection.
3. Confirm the screen does not show a pronunciation score, a
   transcription, or a fluency claim.
   Screenshot: `AT-030-02-check-recorded.png`.

#### B4. Practice that must stay unavailable or unscored

1. Open `Practice`.
2. Confirm `Listening Lab` says mapped audio is not installed and
   listening practice cannot be completed.
3. Confirm `Pronunciation Lab` says model audio is not installed and
   `Pronunciation is not scored.`
4. Confirm `Handwriting` says handwriting assets are not installed and
   handwriting is not scored.
5. Optional alphabet practice, if used, must say that optional practice
   does not complete a required check. Do not treat it as Unit 1
   completion.
   Do not grant Microphone for a scored pronunciation attempt. There is
   no scored listen-record-compare flow on this candidate.

#### B5. Advancement stays on the displayed rule

1. Open Unit 1 from `Course`.
2. Read the advancement requirements on that screen. Do not mark the
   unit advanced unless every displayed requirement is already met by
   this run.
3. If `Advance if requirements are met` is disabled, leave it disabled
   and record the visible remaining requirements. That is not a failure
   of the procedure; Unit 1 is still in development.
   Screenshot: `AT-030-03-advancement.png`.

Do not add tutor sign-off. `Mark a question for a tutor` is optional
and is not advancement evidence.

#### B6. Progress review

1. Tap `Progress`.
2. Confirm `Recorded activity and curriculum state. This is not fluency, mastery, or proficiency.`
3. Confirm the current unit and any book self-attestation from this run
   are visible.
4. Confirm there is no fluency, ILR, C1, or certified-proficiency claim.
   Screenshot: `AT-030-04-progress.png`.

#### B7. Local backup and mandatory saved-work retrieval

1. On Progress, tap `Back up or restore`.
2. Confirm `Backup and restore stay on this device. Nothing is uploaded.`
3. Tap `Create backup`.
4. In the system file picker, save on this device. Record the filename.
5. Confirm the backup message says the backup was saved.
6. Copy that zip off the device (USB) before reboot.
7. On a computer, confirm the zip is a local backup package. Do not
   require `activity-logs.csv`, `gate-evidence.csv`, or a pronunciation
   recording. This candidate does not export a v1 lesson-cycle archive.
   Screenshot: `AT-030-05-export-completed.png`.
8. Create the saved records before retrieval. Saved work is not a
   Progress control. From `Today`, tap `Saved work`. The same list is
   on `Practice` → `Saved work`.
9. On the Unit 1 step opened by `Continue`, tap `Bookmark this step`.
   In `Local note — never scored`, enter `AT-030 note alpha` and tap
   `Save note`. Confirm the step shows
   `Saved note, not scored: AT-030 note alpha`. With that text still in
   the field, tap `Mark a question for a tutor`. Confirm
   `Tutor question, not scored: AT-030 note alpha`. If the field is
   empty, the stored question is `Please review this step.`
10. Return to `Today` and tap `Saved work`. Retrieval is mandatory for
    every record this run created: the resume position, the bookmark,
    the note `AT-030 note alpha`, and the tutor question
    `AT-030 note alpha`. Confirm each one is readable in full. Notes
    and tutor questions must stay labeled not scored. Do not skip this
    step because a record is optional.

#### B7a. Restore replacement, rejection, cancellation, and recovery

Still on the device, still in airplane mode. This is a physical
procedure. An automated unit test does not satisfy it.

1. Save a distinguishable local note, then create a second backup.
2. Restore the earlier backup and confirm replacement: the later note
   is gone and the earlier records are present. Restore replaces the learner records.
   It does not merge them.
3. Attempt to restore a file that is not a valid backup. Confirm the
   app reports failure and the current learner records are unchanged.
4. Start restore, reach confirmation, and cancel before replacement.
   Confirm the learner records are unchanged and disposable incoming
   staging is not left behind.
5. Do not treat a message that records are unchanged as true after a
   replacement has already committed. If replacement finishes and only
   the appearance preference fails, the learner records were still
   replaced.
6. If the app reports that recovery is required, keep the recovery
   journal and do not delete `rollback/` or an in-progress replacement
   snapshot. A failed confirmation before replacement may remove only
   disposable incoming staging.
7. Two recovery messages are different. Read the journal phase and the
   learner records after the database is opened again.
   - Journal not `COMMITTED`: the message says local recovery is still
     required and that the next open finishes restoring the learner
     records from before this attempt. After reopen, the records are
     the ones from before the attempt. The message must not say the
     backup replacement finished.
   - Journal `COMMITTED` with recording cleanup still required: the
     message says learner records were replaced with the backup and
     recording cleanup is still required. After reopen, the records
     are the backup, not the earlier copy. The message must not say
     the earlier records were restored.
   - If appearance persistence fails after either committed outcome,
     the learner records stay the backup and the appearance message
     says the preference could not be saved.
   - Cancelling before mutation leaves records unchanged. Cancelling
     after a committed replacement does not report that records are
     unchanged.

#### B7b. Legacy, migration, and digest invalidation

Automated checks below are not this physical procedure. Record them
only as automated evidence. A physical run does not satisfy them by
copying a unit-test log, and a unit test does not satisfy AT-030.

1. On `Progress`, confirm the historical line begins
   `In-app checks marked correct:` and states that the count includes
   historical rows and is not current prescribed credit. That tally is
   not governed prescribed credit.
2. A saved position or completion that does not match the current
   lesson must not be opened or scored as current work. On `Saved work`,
   that resume says it will not be opened or scored.
3. Rows whose identity schema is not the current schema, including
   older step identities and `UNRECOVERABLE` digests, do not count as
   the current step or the current answer. Do not invent an insertion
   order for attempts that have no recorded accepted-write order.
4. File-backed schema 8 to schema 9 migration: existing attempts gain
   `acceptedOrder` 0 and stay historical. They are not renumbered from
   their timestamps. The next accepted answer receives the next
   positive order. Order 0 may repeat. A backup whose zip hashes match
   but whose learner document has a negative `acceptedOrder`, or two
   attempts with the same positive `acceptedOrder`, is malformed.
   Restoring it leaves the learner records already on the device
   unchanged. A valid backup may contain repeated order 0 rows.
5. Optional practice: a recorded selection is restored only when the
   schema, item, unit-content, rule, role, and practice-context
   identities match the current content. A stale row that selected the
   same option must not suppress a new submission of that option.
   Check this after content invalidation, after backup replacement,
   and after the database is closed and opened again.
6. Database migration and digest checks in automated tests are not
   this physical procedure. Record them only as automated evidence.

AT-030 expected result: Today, Course, a closed-response check, book
self-attestation, Progress, and a local backup can be exercised
offline. Pronunciation is not scored. Listening and handwriting stay
unavailable. Advancement is not forced. This procedure remains
outstanding until a later physical run records a result.

### B8. AT-002 explicit result (same run)

AT-002 shares this run. On the same forms, record a separate AT-002
result:

- onboarding left `I have a tutor now` unselected
- Unit 1 can be studied without tutor sign-off
- `Mark a question for a tutor` was not required
- no dedicated content author, original item bank, or ongoing
  tutor-validation role was required

Fill `release/acceptance/forms/AT-002.md`. Do not copy AT-030 into
AT-002 without those checks.

### C. AT-004 remaining matrix items

Still in airplane mode, still on the same install.

1. `Progress` → `Settings, appearance, and attributions` →
   `Attributions and licenses`. There is no Home attributions entry.
2. Confirm the catalog notice is readable offline, including
   `This app does not own the packaged Russian audio or stress-marked text.`
3. On a source record, confirm the displayed label is `Share-alike:`
   followed by that source's treatment, next to a `License:` row.
   The exact sentence `Share-alike confirmation: Confirmed` is not a
   displayed label. The packaged catalog notice records `CONFIRMED`.
   The public-release block
   `Public release is blocked until share-alike confirmation is recorded.`
   must **not** appear (confirmation is already recorded; this is still
   not a public-release approval).
4. Confirm packaged license texts include `CC BY-SA 4.0`,
   `CC BY 3.0 US`, and `CC BY 2.0 FR`.
   Screenshot: `AT-004-01-attributions.png`.
5. Confirm the obsolete label `Optional drills` does not appear.
   Optional practice cards may appear and must not complete a required
   check. Listening, pronunciation, and handwriting remain unavailable
   or explicitly unscored.
6. On `Progress`, confirm the unit and book self-attestation from this
   run are still visible, and repeat the mandatory saved-work retrieval
   from B7.
7. Process restart, which is not a physical reboot:
   - Settings → Apps → Russian Companion → Force stop.
   - Confirm airplane mode is still on.
   - Relaunch Russian Companion.
   - Confirm `Today` still shows Unit 1, the saved step or progress,
     and `Progress` still shows the book self-attestation and the
     saved notes and tutor questions.
   Screenshot: `AT-004-02-after-force-stop.png`.
   A passing force-stop does not satisfy AT-020.

AT-004 expected result: every shipped capability completed without
sign-in, runtime download, streaming, remote API, cloud, sync, runtime
AI, or network time; required content and user data remained locally
usable in airplane mode.

### D. Capture the AT-020 baseline

Before reboot, write these values onto
`release/acceptance/forms/AT-020.md`:

- Current unit and step shown on `Today`
- Book self-attestation shown on `Progress`
- Closed-response selection still displayed after Force stop
- Local note or tutor question, if one was saved, still readable and
  still labeled not scored
- Backup zip filename, size, and SHA-256 of the copied zip
- Screenshot set listed above

### E. AT-020 / MA-073 physical reboot

Physical reboot is a power-cycle of the device. It is not the
force-stop process restart in AT-004, not an in-memory screen reload,
and not closing and reopening the database inside an automated test.

1. Confirm airplane mode is still on (Wi-Fi still off).
2. Power menu → Restart. Do not connect to a network after boot.
3. After boot, confirm airplane mode and Wi-Fi off again. Screenshot:
   `AT-020-01-airplane-after-reboot.png`.
4. Open Russian Companion. Do not reinstall. Do not restore from cloud.
5. Compare with the captured baseline:
   - `Today` still shows the same unit
   - `Continue` opens the saved step
   - `Progress` still shows the book self-attestation
   - A saved note or tutor question is still complete and still not
     scored
   - The previously copied backup zip still exists in Files
6. Create a second backup after reboot with `Create backup`. Confirm
   the new zip can be copied off the device. Do not require a v1 CSV
   export or a playable pronunciation recording.
   Screenshots: `AT-020-02-home-after-reboot.png`,
   `AT-020-03-saved-work-after-reboot.png`.

AT-020 expected result: the saved unit position, book
self-attestation, notes, tutor questions, and the copied backup file
remain available after reboot without network access. Pronunciation
is not scored. This procedure remains outstanding.

### F. AT-008 complete rights inventory

Do not stop at one attribution screenshot.

1. Open `release/acceptance/audits/AT-008-rights-inventory.tsv` and
   `catalog-audit-rollup.json`. Confirm 1,000 rows and 0 issues.
2. Confirm every source record in the rollup has attribution,
   provenance, redistribution basis, license, modification status, and
   the approved share-alike treatment.
3. On the device, still in airplane mode, open
   `Progress` → `Settings, appearance, and attributions` →
   `Attributions and licenses`. Compare source rows, catalog notice,
   and packaged license texts with the inventory. Screenshot
   `AT-008-01-attributions-inventory.png` is supporting, not the
   complete result.
4. Fill `release/acceptance/forms/AT-008.md`. Retain the TSV and rollup
   with the form.

### G. AT-009 before-and-after release gate

1. Read `release/acceptance/audits/AT-009-before-after.md`.
2. Confirm commit `2c7bf44` recorded `UNCONFIRMED` /
   `READY_FOR_INDEPENDENT_SHARE_ALIKE_REVIEW` / `NOT_ACTIVATED` and
   tests required `SHARE_ALIKE_UNCONFIRMED`.
3. Confirm independent review approved the conservative catalog-wide
   treatment and `0602eda` activated `CONFIRMED` /
   `SHARE_ALIKE_CONFIRMED` / `ACTIVATED`.
4. Read `AT-009-after-activation-assembleRelease.txt`. Confirm
   share-alike is no longer a blocker and AT-004, AT-020, AT-030, and
   AT-031 still prevent release.
5. Fill `release/acceptance/forms/AT-009.md`. Public release remains
   NOT APPROVED.

### H. AT-010 per-file license and text match

Do not stop at one Practice Library screenshot.

1. Open `release/acceptance/audits/AT-010-audio-license-match.tsv`
   (1,000 included files) and `AT-010-source-decisions.tsv` (included
   and excluded sources).
2. Confirm included licenses are only CC BY 2.0 FR, CC BY 3.0 US, or
   Public domain, every included file has matchVerified text, and
   issue count is 0.
3. Confirm excluded / not-acquired / incompatible sources remain
   excluded in source-decisions.
4. On the device, open `Practice`. Confirm Listening Lab and
   Pronunciation Lab stay unavailable because mapped audio is not
   installed. Do not record a played model or a pronunciation score.
   Screenshot `AT-010-01-practice-library.png` is supporting only, and
   it is not a playback pass.
5. Fill `release/acceptance/forms/AT-010.md`. Retain both TSVs with
   the form.

## 5. Pass / fail recording

For each of AT-001, AT-002, AT-004, AT-008, AT-009, AT-010, AT-020,
and AT-030:

- `NOT_EXECUTED` — default in this package
- `PASS` — every expected result above was observed and screenshots /
  files were retained
- `FAIL` — any expected result failed; describe the first failure

A skipped, partial, or emulator run is not a pass. Automated unit
tests, including migration, digest, backup, and restart tests, are
automated evidence only. They are not physical evidence and they are
not a pass for these procedures. In-memory reload, database reopen,
process restart, and physical reboot are different events. Only the
procedure that names one of them can satisfy it.

## 6. Evidence filenames and repository destinations

After a real device run, copy artifacts into the repository. Do not
do this until the device work exists.

| Procedure | Working form (this package) | Governed destination after a real result |
|---|---|---|
| AT-001 | `release/acceptance/forms/AT-001.md` | `release/evidence/AT-001.json` plus screenshots below |
| AT-002 | `release/acceptance/forms/AT-002.md` | `release/evidence/AT-002.json` |
| AT-004 | `release/acceptance/forms/AT-004.md` | `release/evidence/AT-004.json` |
| AT-008 | `release/acceptance/forms/AT-008.md` plus `audits/AT-008-rights-inventory.tsv` | `release/evidence/AT-008.json` and `release/evidence/AT-008-rights-inventory.tsv` |
| AT-009 | `release/acceptance/forms/AT-009.md` plus `audits/AT-009-before-after.md` | `release/evidence/AT-009.json` |
| AT-010 | `release/acceptance/forms/AT-010.md` plus the AT-010 TSVs | `release/evidence/AT-010.json` and the per-file TSVs |
| AT-020 | `release/acceptance/forms/AT-020.md` | `release/evidence/AT-020.json` |
| AT-030 | `release/acceptance/forms/AT-030.md` | `release/evidence/AT-030.json` |
| AT-031 | `release/acceptance/forms/AT-031-OWNER-DECISION.md` | `release/evidence/AT-031.json` — Product Owner only, after AT-001 through AT-030 have real retained results |

Screenshot / file destinations (all under `release/evidence/`):

```
release/evidence/screenshots/AT-001-01-identity.png
release/evidence/screenshots/AT-001-02-limits.png
release/evidence/screenshots/AT-001-03-lesson-1.png
release/evidence/screenshots/AT-001-04-lesson-30.png
release/evidence/screenshots/AT-001-05-home.png
release/evidence/screenshots/AT-004-00-airplane-mode.png
release/evidence/screenshots/AT-004-01-attributions.png
release/evidence/screenshots/AT-004-02-after-force-stop.png
release/evidence/screenshots/AT-030-01-plan.png
release/evidence/screenshots/AT-030-02-check-recorded.png
release/evidence/screenshots/AT-030-03-advancement.png
release/evidence/screenshots/AT-030-04-progress.png
release/evidence/screenshots/AT-030-05-export-completed.png
release/evidence/screenshots/AT-020-01-airplane-after-reboot.png
release/evidence/screenshots/AT-020-02-home-after-reboot.png
release/evidence/screenshots/AT-020-03-saved-work-after-reboot.png
release/evidence/screenshots/AT-008-01-attributions-inventory.png
release/evidence/screenshots/AT-010-01-practice-library.png
release/evidence/exports/AT-030-russian-companion-export.zip
release/evidence/exports/AT-020-post-reboot-export.zip
```

Each `release/evidence/AT-00N.json` must be a procedure-evidence
JSON document with `schemaVersion` 2. Do not rename a Markdown form
or template to `.json`, and do not paste the Markdown into that file.
Write a JSON object whose fields are `schemaVersion` (the number 2),
`procedure`, `result` (`PASS` or `FAIL` from the physical run),
`candidateCommit`, `apkSha256`, `sourceDigest` (the current value in
`release/acceptance/candidate.json`), `versionName`, `versionCode`,
`applicationId`, `executedAt`, `deviceModel` for physical procedures,
and `artifactHashes`. The release gate rejects empty files, non-JSON
text, paths outside `release/evidence/`, boolean flags, and evidence
bound to a previous source digest or version identity. Working forms
in this package stay `NOT_EXECUTED`.

Only after those files exist from a real run may
`release/retained-evidence.json` change a record from `MISSING` to
`RETAINED` with the matching `evidencePath`. That step is out of
scope for this package.

AT-001 is outstanding in the acceptance ledger. Retain its device
result at `release/evidence/AT-001.json` so AT-031 can inspect it.

## 7. AT-031

Do not complete AT-031 in this package. After AT-001 through AT-030
have real retained results, including AT-002, AT-008, AT-009, and
AT-010, the Product Owner uses
`release/acceptance/forms/AT-031-OWNER-DECISION.md`.

## 8. Procedures still blocked

These remain unexecuted and must stay `MISSING` or `OUTSTANDING`:

- AT-001 — physical inspection of the installed candidate
- AT-002 — full tutor-free use (explicit result from the shared run)
- AT-004 — airplane-mode capability matrix on device
- AT-008 — complete rights-record catalog audit
- AT-009 — share-alike before-and-after release-gate evidence
- AT-010 — per-file audio licensing and text matching
- AT-020 — physical reboot
- AT-030 — physical lesson cycle
- AT-031 — owner release decision

Public release remains **NOT APPROVED**.
