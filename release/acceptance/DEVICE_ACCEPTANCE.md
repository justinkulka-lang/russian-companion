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
| versionName | `0.0.0` |
| versionCode | `1` |
| App content commit | `0602edaeb05a353de5e994c127eb87b5b97078a3` |
| Gradle task | `assembleDebug` |
| Artifact name | `acceptance-candidate-debug-apk` |
| Identity file in artifact | `artifact-identity.json` |
| SHA-256 | `1a95949ba7e2edd0ca72dea18e728ec105e427a2e5f5c093ff33fe82dfcba33e` |
| Byte count | `29081799` |
| Artifact run URL | https://github.com/justinkulka-lang/russian-companion/actions/runs/35740338300 |
| Public release | `NOT_APPROVED` |

Download the exact APK:

1. Open the successful Build run for this package branch / PR #20.
2. Download artifact `acceptance-candidate-debug-apk`.
3. Extract `app-debug.apk` and `artifact-identity.json`.
4. `sha256sum app-debug.apk` and compare to `artifact-identity.json`
   `sha256` and to `release/acceptance/candidate.json` once that file
   records the same run.
5. Install only that file. Record the run URL, SHA-256, and byte count
   on every result form.

`assembleRelease` remains fail-closed until AT-004, AT-020, AT-030, and
AT-031 evidence is actually retained under `release/evidence/`.

## 2. Release-candidate / release-gate circularity

`assembleRelease` and `packageRelease` depend on `checkReleaseGate`.
That gate requires retained AT-004, AT-020, AT-030, and AT-031 files
before a public-release APK can be produced. The physical procedures
require an installed candidate first.

The existing governed mechanism is `assembleDebug`:

- GitHub Actions already assembles debug and runs unit tests.
- `checkReleaseGate` is attached only to `assembleRelease` /
  `packageRelease`.
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
   `artifact-identity.json` before copying it to the device. Record the
   run URL, SHA-256, and byte count on every result form.
3. Copy that exact `app-debug.apk` to the device (USB). Record the
   on-device path.
4. Settings → Apps → search **Russian Companion**. If present,
   Uninstall. Confirm it is gone.
5. Settings → Security and privacy → Install unknown apps → allow the
   Files app (or the installer you will use) for this one install.
6. Install the APK. Open App info and confirm:
   - App name: Russian Companion
   - Version: `0.0.0`
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
2. Onboarding page 1 must show:
   - `This offline app is a study companion for an independent adult beginner working through The New Penguin Russian Course.`
   - `This app coordinates your book, AnkiDroid work, listening and recording, gates, and logs.`
   Screenshot: `AT-001-01-identity.png`.
3. Tap `Next`.
4. Page 2 must show:
   - `This app does not teach from reproduced Penguin content, connect to AnkiDroid, judge Russian, or certify proficiency.`
   Screenshot: `AT-001-02-limits.png`.
5. Tap `Next`.
6. Page 3 must show:
   - `Default gates do not require a tutor. Tutor sign-off can be added later only as stricter evidence you choose. All data stays on this device. Backup is a local export you start.`
7. Scroll the lesson list. Confirm **Lesson 1** through **Lesson 30**
   are present and selectable. Confirm there is no B1, B2, C1,
   handwriting, dictionary, media library, social, gamification, or
   tutor-scheduling entry.
   Screenshots: `AT-001-03-lesson-1.png`, `AT-001-04-lesson-30.png`.
8. Confirm the listed roles are available after onboarding: Today's
   Plan, Practice, Gates, Progress, and Local export (Local export is
   reached from Progress). Optional drills must **not** appear.
9. Select `Lesson 1`. Tap `Confirm lesson`.
10. Home must show `Current lesson: Lesson 1` and
    `Required activities completed: 0 of 3`.
    Screenshot: `AT-001-05-home.png`.

AT-001 expected result: the app identifies itself as a companion for
an independent adult beginner using The New Penguin Russian Course;
offers only the Penguin-concurrent beginner arc (Lessons 1–30); and
exposes daily conductor, listen-record-compare, gates, and
progress/local-export roles without a post-book B1, B2, or C1
curriculum.

### B. AT-030 lesson cycle (also satisfies most of AT-004)

Keep airplane mode on.

#### B1. Plan

1. On Home, confirm `Next required activity` and
   `Use your copy of the book for this lesson`.
2. Tap `Today's Plan`.
3. Confirm these three required lines:
   - `Use your copy of the book for this lesson`
   - `Do the assigned AnkiDroid work`
   - `Listen to the licensed model`
4. Confirm helper text:
   - `Grammar practice stays in your copy of the book and its answer key.`
   - `Vocabulary and forms practice stays in AnkiDroid. The app records only what you report completing.`
   - `Listen-record-compare practice.`
5. Tap `Add tutor task`, confirm `Remove tutor task`, then tap
   `Remove tutor task`. The tutor task must disappear. Do not leave it
   added.
   Screenshot: `AT-030-01-plan.png`.

#### B2. Book practice and self-attestation

1. Using the user's own copy of the book (outside the app), do the
   Lesson 1 book work. Do not enter Penguin text into the app.
2. On Today's Plan, tap `Mark complete` for the book row.
3. Confirm that row shows `Completed` and `Self-attested`.
4. Optional extra log (AT-004/AT-018 coverage): Home → `Progress` →
   `Log activity` → `Grammar` → confirm `Book practice.` → duration
   `15` → `Mark completed` → `Save activity`.

#### B3. AnkiDroid practice and self-attestation

1. Using AnkiDroid if installed, or recording that the work was done
   as a self-report if AnkiDroid is absent, complete the assigned
   vocabulary/forms work outside the app. The app must not open,
   connect to, or verify AnkiDroid.
2. On Today's Plan, tap `Mark complete` for the AnkiDroid row.
3. Confirm `Completed` and `Self-attested`.
4. Optional extra log: `Progress` → `Log activity` →
   `Vocabulary and forms` → confirm `AnkiDroid practice.` → duration
   `10` → `Mark completed` → `Save activity`.

#### B4. Listen-record-compare

1. On Today's Plan, tap `Start listen-record-compare`.
2. Confirm `Practice session` and `No pronunciation score is provided.`
3. Confirm a stress-marked source text is shown (catalog headword, not
   Penguin lesson text).
4. Tap `Play model`. Confirm local audio plays with airplane mode on.
5. Tap `Record`. Grant Microphone if prompted. Speak an imitation.
   Tap `Stop`.
6. Tap `Replay model`. Tap `Play your recording`. Confirm both play.
7. Tap `Mark for tutor review`.
8. Tap `Complete practice`.
9. Return to Today's Plan / Home. Confirm
   `Required activities completed: 3 of 3` and
   `Today's required activities are complete.` and
   `Completion is recorded work, not proficiency certification.`
   Screenshot: `AT-030-02-practice-complete.png`.

There must be no pronunciation score, transcription, fluency claim, or
correctness judgment.

#### B5. Gate evidence and decision

1. Tap `Gates`. Confirm `Activity gate, not proficiency certification.`
2. Open the Lesson 1 rule:
   `Book revision score at least 1 AND AnkiDroid completion or maturity equals MATURE AND Prescribed work completion is recorded`
3. Confirm labels:
   - `Book revision score`
   - `Self-attested — not independently verified`
   - `AnkiDroid completion or maturity`
   - `Self-attested — no AnkiDroid integration; not independently verified`
   - `Prescribed work completion`
4. Enter book revision score `1`.
5. Tap `Mature`.
6. Tap `Confirm prescribed work`.
7. Tap `Save evidence`.
8. Confirm `Recorded gate status: Satisfied`.
   Screenshot: `AT-030-03-gate-satisfied.png`.

Do not add tutor sign-off.

#### B6. Progress review

1. Tap `Progress`.
2. Confirm `Recorded activity is work completed, not certified ability.`
3. Confirm recorded time/completions reflect the logs from this run.
4. Confirm category totals exist for the categories you logged
   (Grammar, Vocabulary and forms, Speaking and pronunciation at
   minimum).
5. Confirm there is no fluency, ILR, C1, or certified-proficiency
   claim.
   Screenshot: `AT-030-04-progress.png`.

#### B7. Local export

1. On Progress, tap `Local export`.
2. Confirm:
   `Export will include logs, gate evidence, progress data, and recordings. This is local export, not cloud backup.`
3. Tap `Choose a local destination`.
4. In the system file picker, save to device storage as
   `russian-companion-export.zip` (or the picker default).
5. Confirm `Export completed.`
6. Copy that zip off the device (USB) before reboot.
7. On a computer, unzip and confirm these members exist:
   - `activity-logs.csv`
   - `gate-evidence.csv`
   - `gate-decisions.csv`
   - `progress.csv`
   - `recordings.csv`
   - `recordings/` audio file
8. Confirm the CSVs contain book and AnkiDroid self-attestation labels,
   a `SATISFIED` decision, and the recording association.
   Screenshot: `AT-030-05-export-completed.png`.

AT-030 expected result: the governed lesson cycle completes offline;
the gate decision is reproducible from the displayed rule and recorded
evidence; self-attestations remain labeled; pronunciation is not
scored; progress remains activity-only; the export contains logs, gate
evidence, progress, and the recording.

### B8. AT-002 explicit result (same run)

AT-002 shares this lesson-cycle run. On the same forms, record a
separate AT-002 result:

- onboarding had no tutor information
- the tutor task was added and removed and was not left enabled
- the default gate was satisfied without tutor sign-off
- no dedicated content author, original item bank, or ongoing
  tutor-validation role was required

Fill `release/acceptance/forms/AT-002.md`. Do not copy AT-030 into
AT-002 without those checks.

### C. AT-004 remaining matrix items

Still in airplane mode, still on the same install.

1. Home → `Attributions and licenses`.
2. Confirm the catalog notice is readable offline, including
   `This app does not own the packaged Russian audio or stress-marked text.`
3. Confirm `Share-alike confirmation: Confirmed` on a source record
   (`License: …` row). The public-release block
   `Public release is blocked until share-alike confirmation is recorded.`
   must **not** appear (confirmation is already recorded; this is still
   not a public-release approval).
4. Confirm packaged license texts include `CC BY-SA 4.0`,
   `CC BY 3.0 US`, and `CC BY 2.0 FR`.
   Screenshot: `AT-004-01-attributions.png`.
5. Confirm `Optional drills` does not appear. Skip optional-drill
   steps; AT-025/026/027 remain `NOT_APPLICABLE`.
6. Add at least one more Progress log in a category not yet used
   (for example `Reading`, duration `5`, `Mark completed`,
   `Save activity`) so AT-004 step 5 is explicit.
7. Process restart (AT-004 step 9 / AT-019 prelude to AT-020):
   - Settings → Apps → Russian Companion → Force stop.
   - Confirm airplane mode is still on.
   - Relaunch Russian Companion.
   - Confirm Home still shows Lesson 1, completed plan, Satisfied gate,
     playable recording, and Progress totals.
   Screenshot: `AT-004-02-after-force-stop.png`.

AT-004 expected result: every shipped capability completed without
sign-in, runtime download, streaming, remote API, cloud, sync, runtime
AI, or network time; required content and user data remained locally
usable in airplane mode.

### D. Capture the AT-020 baseline

Before reboot, write these values onto
`release/acceptance/forms/AT-020.md`:

- Current lesson
- Required activities completed (expected `3 of 3`)
- Gate status (expected `Satisfied`)
- Book score `1`, AnkiDroid `Mature`, prescribed work recorded
- Progress totals (minutes and completions)
- Recording: model text shown, tutor-review mark present, both
  playbacks work
- Export zip filename, size, and SHA-256 of the copied zip
- Screenshot set listed above

### E. AT-020 / MA-073 physical reboot

1. Confirm airplane mode is still on (Wi-Fi still off).
2. Power menu → Restart. Do not connect to a network after boot.
3. After boot, confirm airplane mode and Wi-Fi off again. Screenshot:
   `AT-020-01-airplane-after-reboot.png`.
4. Open Russian Companion. Do not reinstall. Do not restore from cloud.
5. Compare with the captured baseline:
   - Home lesson, plan completion, gate Satisfied
   - Today's Plan completion marks
   - Gates evidence values and decision
   - Progress totals and log entries
   - Practice: play model and play your recording
   - The previously copied export zip still exists in Files
6. Create a second local export after reboot. Confirm it still contains
   the same governed CSVs and recording bytes. This is the observable
   check that export history/data survived; the Local export screen
   does not list prior export rows.
   Screenshots: `AT-020-02-home-after-reboot.png`,
   `AT-020-03-recording-after-reboot.png`.

AT-020 expected result: plans, logs, gate evidence, gate decisions,
progress, recording metadata, recording audio, and prior export files
remain available and unchanged after reboot without network access.

### F. AT-008 complete rights inventory

Do not stop at one attribution screenshot.

1. Open `release/acceptance/audits/AT-008-rights-inventory.tsv` and
   `catalog-audit-rollup.json`. Confirm 1,000 rows and 0 issues.
2. Confirm every source record in the rollup has attribution,
   provenance, redistribution basis, license, modification status, and
   the approved share-alike treatment.
3. On the device, still in airplane mode, open
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
4. On the device, open Practice and confirm a packaged model plays
   with its matched stress-marked text. Screenshot
   `AT-010-01-practice-library.png` is supporting, not the complete
   result.
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
tests are not a pass for these procedures.

## 6. Evidence filenames and repository destinations

After a real device run, copy artifacts into the repository. Do not
do this until the device work exists.

| Procedure | Working form (this package) | Governed destination after a real result |
|---|---|---|
| AT-001 | `release/acceptance/forms/AT-001.md` | `release/evidence/AT-001.txt` plus screenshots below |
| AT-002 | `release/acceptance/forms/AT-002.md` | `release/evidence/AT-002.txt` |
| AT-004 | `release/acceptance/forms/AT-004.md` | `release/evidence/AT-004.txt` |
| AT-008 | `release/acceptance/forms/AT-008.md` plus `audits/AT-008-rights-inventory.tsv` | `release/evidence/AT-008.txt` and `release/evidence/AT-008-rights-inventory.tsv` |
| AT-009 | `release/acceptance/forms/AT-009.md` plus `audits/AT-009-before-after.md` | `release/evidence/AT-009.txt` |
| AT-010 | `release/acceptance/forms/AT-010.md` plus the AT-010 TSVs | `release/evidence/AT-010.txt` and the per-file TSVs |
| AT-020 | `release/acceptance/forms/AT-020.md` | `release/evidence/AT-020.txt` |
| AT-030 | `release/acceptance/forms/AT-030.md` | `release/evidence/AT-030.txt` |
| AT-031 | `release/acceptance/forms/AT-031-OWNER-DECISION.md` | `release/evidence/AT-031.txt` — Product Owner only, after AT-001 through AT-030 have real retained results |

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
release/evidence/screenshots/AT-030-02-practice-complete.png
release/evidence/screenshots/AT-030-03-gate-satisfied.png
release/evidence/screenshots/AT-030-04-progress.png
release/evidence/screenshots/AT-030-05-export-completed.png
release/evidence/screenshots/AT-020-01-airplane-after-reboot.png
release/evidence/screenshots/AT-020-02-home-after-reboot.png
release/evidence/screenshots/AT-020-03-recording-after-reboot.png
release/evidence/screenshots/AT-008-01-attributions-inventory.png
release/evidence/screenshots/AT-010-01-practice-library.png
release/evidence/exports/AT-030-russian-companion-export.zip
release/evidence/exports/AT-020-post-reboot-export.zip
```

Each `release/evidence/AT-00N.txt` must be non-empty and must start
with `release/evidence/` as its manifest path. The release gate
rejects empty files, paths outside that prefix, and boolean flags.

Only after those files exist from a real run may
`release/retained-evidence.json` change a record from `MISSING` to
`RETAINED` with the matching `evidencePath`. That step is out of
scope for this package.

AT-001 is outstanding in the acceptance ledger. It is not one of the
four gate-manifest procedures. Still retain its device result at
`release/evidence/AT-001.txt` so AT-031 can inspect it.

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
