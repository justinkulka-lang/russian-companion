# ACCEPTANCE CRITERIA v1.0
## Offline Russian Learning Companion — Test Map

**Authority:** `MASTER_AUTHORITY_v1.0.md`  
**Scope source:** `PRODUCT_BRIEF_v1.1.md`

## 1. Test-Map Rules

This document verifies governed outcomes without adding functionality. A release passes only when every applicable criterion passes, every shipped optional feature passes its conditional criteria, and the coverage table contains no uncovered MA requirement. “Inspect” includes review of the distributable app, its bundled content and metadata, its user-visible behavior, and release-governance evidence as applicable. *(Trace: MA-110–MA-123; Product Brief §§9–11)*

## 2. Scope, Role, and Exclusions

### AT-001 — Intended learner and bounded pathway

**Preconditions:** A release candidate is installed with its complete bundled content.

**Steps:**
1. Complete onboarding.
2. Inspect every available course segment and course-selection path.
3. Inspect the stated role of the app.

**Expected result:** The app identifies itself as a companion for an independent adult beginner using *The New Penguin Russian Course*; offers only the Penguin-concurrent beginner arc; and exposes the daily conductor, listen-record-compare, gates, and progress/local-export roles without a post-book B1, B2, or C1 curriculum.

**MA IDs covered:** MA-001, MA-002, MA-003, MA-007.

### AT-002 — Full tutor-free use

**Preconditions:** Fresh installation; no tutor data exists; no optional tutor configuration is enabled.

**Steps:**
1. Complete onboarding without entering tutor information.
2. Create and complete a daily plan.
3. Complete listen-record-compare.
4. Enter book and AnkiDroid self-attested evidence sufficient for a default gate.
5. Review progress and create a local export.

**Expected result:** All required v1 capabilities work and the default gate can be satisfied without a tutor, dedicated content author, original item bank, or ongoing tutor-validation role.

**MA IDs covered:** MA-004, MA-005, MA-006, MA-058, MA-117.

### AT-003 — Excluded feature inventory

**Preconditions:** Release candidate and complete navigation inventory are available.

**Steps:**
1. Traverse every screen and action.
2. Inspect the packaged feature and content inventory.
3. Search user-visible labels and bundled content for excluded capabilities.

**Expected result:** The release contains no handwriting instruction, open-ended dictionary, general media library, social feature, gamification system, tutor scheduling, post-book curriculum, original grammar-review bank, or minimal-pair drill.

**MA IDs covered:** MA-007, MA-008, MA-099, MA-100, MA-114.

## 3. Offline and Runtime Boundaries

### AT-004 — Complete airplane-mode capability matrix

**Preconditions:** Install the release candidate and complete any first-run local initialization; enable airplane mode; ensure no account is configured.

**Steps:**
1. Complete onboarding on a fresh local state.
2. Open and complete Today's Plan, including local completion records.
3. Play bundled native audio, record locally, replay both recordings, and mark one for tutor review.
4. Enter gate evidence and obtain a gate decision.
5. Add and review progress records.
6. Create a local export.
7. Open Attributions & Licenses.
8. If optional drills ship, complete an optional drill.
9. Restart the app and repeat access to each area while airplane mode remains enabled.

**Expected result:** Every shipped capability completes without sign-in, runtime download, streaming, remote API, cloud, synchronization, runtime AI, or network time; all required content and user data remain locally usable.

**MA IDs covered:** MA-009, MA-010, MA-011, MA-012, MA-013, MA-014, MA-015, MA-016, MA-017, MA-018, MA-019, MA-110, MA-119.

### AT-005 — Prohibited runtime dependency audit

**Preconditions:** Final build manifest, dependency inventory, packaged permissions, and runtime traffic observation are available.

**Steps:**
1. Inspect dependencies and packaged components for networking, authentication, cloud synchronization, analytics, advertising, AI, ASR, transcription, and AnkiDroid connectivity.
2. Exercise all app paths while observing attempted network activity.
3. Inspect runtime prompts and permissions.

**Expected result:** No prohibited runtime component, remote call, account flow, cloud/sync behavior, analytics behavior, AI model, ASR/transcription component, AnkiDroid connector, or network-dependent product operation is present or attempted.

**MA IDs covered:** MA-010, MA-011, MA-012, MA-013, MA-014, MA-015, MA-016, MA-017, MA-027, MA-084, MA-116.

## 4. Russian Content and Assessment Boundaries

### AT-006 — No Russian origination or fallback substitution

**Preconditions:** Release candidate; content inventory; a test state with one required content asset intentionally unavailable or corrupt.

**Steps:**
1. Inspect all Russian-presenting paths and packaged content origins.
2. Attempt to use the unavailable asset.
3. Search for controls that request generated words, sentences, explanations, drills, feedback, or replacement content.

**Expected result:** Every presented Russian asset maps to pre-made already-corrected licensed source material; the unavailable asset produces an unavailable/error state; and the app neither originates, validates, generates, nor substitutes Russian content.

**MA IDs covered:** MA-020, MA-021, MA-022, MA-023, MA-111, MA-112, MA-116, MA-123.

### AT-007 — No speech, writing, or pronunciation judgment

**Preconditions:** At least one eligible audio model and microphone access.

**Steps:**
1. Record clear speech, unclear speech, silence, and speech different from the displayed model.
2. Replay each recording.
3. Inspect all resulting labels, progress records, gate evidence choices, and exports.
4. Inspect the app for free-speech and free-writing submission paths.

**Expected result:** The app stores and replays recordings but provides no transcription, ASR result, pronunciation/accent score, correctness judgment, free-speech judgment, or free-writing judgment; it does not represent itself as replacing human correction, conversation, immersion, or a tutor.

**MA IDs covered:** MA-024, MA-025, MA-026, MA-027, MA-028, MA-055, MA-065.

## 5. Content Rights and Preparation

### AT-008 — Rights-record completeness and offline readability

**Preconditions:** Final bundled asset inventory, rights records, and installed release candidate.

**Steps:**
1. For every bundled content asset, resolve its source, license, redistribution-rights basis, modification status, attribution requirement, and share-alike treatment.
2. Open Attributions & Licenses in airplane mode.
3. Compare the user-visible records with the bundled inventory.

**Expected result:** Every bundled asset has complete recorded rights metadata; required attributions are readable in the distributed app; the content-license inventory is readable offline; and no inventory asset lacks a corresponding record.

**MA IDs covered:** MA-029, MA-030, MA-031, MA-032, MA-033, MA-034, MA-039, MA-040, MA-113, MA-121.

### AT-009 — Share-alike public-release block

**Preconditions:** A public-release candidate containing derivative data subject to share-alike terms; release status can be evaluated with confirmation absent and present.

**Steps:**
1. Evaluate release readiness with share-alike obligations unconfirmed.
2. Record confirmation and required treatment.
3. Re-evaluate release readiness.

**Expected result:** Public release is blocked while obligations are unconfirmed and becomes eligible on this condition only after confirmation and required treatment are recorded.

**MA IDs covered:** MA-034, MA-035, MA-121.

### AT-010 — Per-file audio licensing and text matching

**Preconditions:** Candidate audio source set includes compatible, absent-license, unclear-license, and incompatible-license cases; matched and unmatched text cases are present in preparation evidence.

**Steps:**
1. Run or inspect the governed content-preparation result for each source audio file.
2. Compare inclusion decisions with exact per-file license metadata.
3. For every included audio file, resolve the linked verified source-supplied stress-marked text.
4. Inspect the installed Practice Library.

**Expected result:** Only audio with a clear compatible redistribution license is included; every inclusion/exclusion decision is documented per file; every included model has exactly the verified source-supplied stress-marked text it represents; and absent, unclear, incompatible, or unmatched assets are excluded.

**MA IDs covered:** MA-036, MA-037, MA-038, MA-050, MA-051, MA-118.

### AT-011 — Penguin copyright boundary

**Preconditions:** Installed release candidate, bundled database/assets, and all user-visible course references.

**Steps:**
1. Inspect every Penguin reference in the UI and packaged content.
2. Compare references with the allowed purposes of course sequence, lesson identification, and directions to use the user's copy.
3. Search for reproduced, scanned, summarized, or bundled Penguin lesson text and exercises.

**Expected result:** Penguin content is limited to allowed sequence/reference purposes; no protected lesson text or exercise is reproduced, scanned, summarized, or bundled; grammar practice remains directed to the user's book and answer key.

**MA IDs covered:** MA-041, MA-042, MA-081.

## 6. Daily Conductor and Practice

### AT-012 — Daily plan clarity and completion

**Preconditions:** Onboarding is complete with a valid current lesson and plan definitions.

**Steps:**
1. Open Home.
2. Identify the next required work without consulting a separate guide.
3. Open Today's Plan.
4. Verify presence of book, AnkiDroid, and listen-record-compare activities.
5. Add a tutor task, then remove it.
6. Record completion of each applicable activity.

**Expected result:** A lesson-aligned checklist clearly identifies and starts required work; covers book, AnkiDroid, and listen-record-compare; supports but does not require a user-chosen tutor task; and persists recorded completion.

**MA IDs covered:** MA-043, MA-044, MA-045, MA-046, MA-047, MA-048, MA-049.

### AT-013 — Listen-record-compare complete behavior

**Preconditions:** Eligible bundled native model; microphone permission granted.

**Steps:**
1. Open the model and verify the matched stress-marked text.
2. Play the local native model.
3. Record an imitation.
4. Replay the model and user recording separately.
5. Mark the recording for tutor review.
6. Complete the practice.

**Expected result:** Playback, local recording, A/B replay, tutor-review marking, and completion succeed; completion contains no pronunciation-quality score or judgment.

**MA IDs covered:** MA-050, MA-051, MA-052, MA-053, MA-054, MA-055.

## 7. Advancement Gates

### AT-014 — Self-attested evidence labels

**Preconditions:** A default gate accepting book and AnkiDroid evidence.

**Steps:**
1. Open Gate Detail.
2. Enter a book-derived revision score.
3. Enter AnkiDroid completion or maturity status.
4. Inspect the saved evidence, gate decision, progress views, and export representation.

**Expected result:** Both evidence types are explicitly labeled as user self-attestation and not independently verified; AnkiDroid evidence also states or conveys that no AnkiDroid integration exists.

**MA IDs covered:** MA-060, MA-061, MA-062, MA-063, MA-064, MA-084.

### AT-015 — Gate rule application and reproducibility

**Preconditions:** A gate with a displayed rule and controlled evidence values.

**Steps:**
1. Record evidence that does not satisfy one displayed condition.
2. Capture the decision and condition breakdown.
3. Change only that evidence to a satisfying value.
4. Capture the new decision.
5. Recreate both evaluations from the displayed rule and recorded evidence.

**Expected result:** Each decision matches the displayed rule, changes only as its recorded evidence changes, can be independently reproduced, and contains no linguistic judgment by the app.

**MA IDs covered:** MA-056, MA-057, MA-065, MA-066.

### AT-016 — Tutor-free defaults and user-added strictness

**Preconditions:** Fresh default gate configuration and no tutor data.

**Steps:**
1. Satisfy the default rule using only its non-tutor evidence.
2. Confirm the gate is satisfied.
3. Add tutor sign-off as stricter evidence.
4. Leave tutor sign-off absent and re-evaluate.
5. Record tutor sign-off and re-evaluate.

**Expected result:** The shipped default gate is satisfiable without a tutor; tutor sign-off appears only after user action; the stricter rule waits for that sign-off; and recording it satisfies only the user-modified condition.

**MA IDs covered:** MA-004, MA-058, MA-059.

### AT-017 — Optional drills excluded from gate evidence

**Preconditions:** A build containing admitted optional drills and completed optional-drill activity.

**Steps:**
1. Complete optional drill activity.
2. Open every gate evidence-entry path.
3. Inspect stored gate evidence types and the resulting decision.

**Expected result:** No optional-drill activity or result is available or stored as gate evidence and it has no effect on the decision.

**MA IDs covered:** MA-067, MA-087, MA-088.

## 8. Progress, Persistence, and Export

### AT-018 — Activity logging and skill-balance review

**Preconditions:** Clean user-data state.

**Steps:**
1. Record study time and completion once in each governed category: reading, listening, vocabulary/forms, grammar, speaking/pronunciation, writing, and tutor work.
2. Open Progress.
3. Compare cumulative totals and category comparisons with the source logs.

**Expected result:** All governed categories are recordable; cumulative progress equals recorded activity; visible imbalances reflect category activity only; and no ability inference or certification is shown.

**MA IDs covered:** MA-068, MA-069, MA-070, MA-071, MA-105, MA-106, MA-107, MA-108, MA-109.

### AT-019 — Persistence across app restart

**Preconditions:** Saved plan state, activity logs, gate evidence and decision, progress data, and at least one recording.

**Steps:**
1. Capture the complete saved state.
2. Terminate the app process.
3. Relaunch the app.
4. Compare all governed records and playable recording with the captured state.

**Expected result:** Plans, logs, gate evidence, gate decisions, progress, recording metadata, and recording audio remain available and unchanged after restart.

**MA IDs covered:** MA-019, MA-072.

### AT-020 — Persistence across device reboot

**Preconditions:** The saved state used in AT-019 exists.

**Steps:**
1. Reboot the device while offline.
2. Launch the app.
3. Compare all governed records and playable recording with the pre-reboot state.

**Expected result:** Plans, logs, gate evidence, gate decisions, progress, recording metadata, and recording audio remain available and unchanged after reboot without network access.

**MA IDs covered:** MA-009, MA-019, MA-073.

### AT-021 — Complete local export and recoverability

**Preconditions:** Saved logs, gate evidence, progress data, and at least two recordings; a writable local destination.

**Steps:**
1. Initiate export from the app.
2. Select the local destination.
3. Complete the export.
4. Copy the resulting local package away from the device under test.
5. Inspect the package using independent file-reading tools.
6. Match structured records and audio to the app state.

**Expected result:** The export is user-initiated and local; contains readable logs, gate evidence, progress data, and every recording with stable associations; requires no account or network; and remains usable after being copied elsewhere for recovery or reference.

**MA IDs covered:** MA-074, MA-075, MA-076, MA-077, MA-078, MA-079, MA-120.

### AT-022 — Export failure is not reported as success

**Preconditions:** Governed export data exists; test conditions can deny destination access, exhaust space, interrupt writing, and make one recording unreadable.

**Steps:**
1. Attempt export under each failure condition.
2. Inspect the status presented to the user and any produced package.

**Expected result:** Each incomplete export is reported as unsuccessful; no governed dataset or unreadable recording is silently omitted from a package represented as complete; and the user can retry to a valid local destination.

**MA IDs covered:** MA-074, MA-075, MA-076, MA-077, MA-078, MA-079, MA-120.

## 9. AnkiDroid and Book Boundaries

### AT-023 — Orchestration without duplication or integration

**Preconditions:** Installed release candidate; AnkiDroid absent for one run and installed for another.

**Steps:**
1. Complete the AnkiDroid-related plan and gate flow in both conditions.
2. Inspect the app for SRS scheduling, equivalent vocabulary/form banks, deck import, inter-app reads/writes, or independent Anki verification.
3. Inspect grammar-practice paths.

**Expected result:** The flow remains manual and self-attested regardless of AnkiDroid installation; the app contains no integrated SRS, duplicate vocabulary/form drills, AnkiDroid integration, or authoritative third-party deck/community-content import; and grammar practice directs the user to the book and answer key.

**MA IDs covered:** MA-062, MA-063, MA-064, MA-080, MA-081, MA-082, MA-083, MA-084, MA-085, MA-086.

## 10. Optional Curation-Only Drills

### AT-024 — Optional package absence does not block release

**Preconditions:** A release candidate built without any optional drill package.

**Steps:**
1. Run the four primary capability tests.
2. Inspect release readiness and navigation.

**Expected result:** The four capabilities remain complete; no empty optional-drill entry is required; and absence of optional drills does not block release.

**MA IDs covered:** MA-087, MA-088, MA-114, MA-117.

### AT-025 — Optional drill deterministic-source audit

**Preconditions:** A candidate optional drill package, its source dataset, transformation declaration, and rights records.

**Steps:**
1. Reproduce every candidate prompt and correct response from recorded source fields and the declared transformation.
2. Inspect for authored items, distractors, explanations, answer keys, or per-item linguistic judgments.
3. Resolve provenance and rights for every record.

**Expected result:** Every prompt and response is a deterministic presentation or transformation of already-corrected properly licensed structured data; no original authoring or per-item judgment is present; and every item retains source provenance and rights.

**MA IDs covered:** MA-089, MA-090, MA-091, MA-092, MA-093, MA-094, MA-112, MA-113.

### AT-026 — Optional drill source-error and validation disclosure

**Preconditions:** A build containing an admitted optional drill package; include a controlled source record with a known source error for testing.

**Steps:**
1. Open the drill and its source disclosure.
2. Observe the controlled source-error item.
3. Inspect release evidence for tutor review obligations.

**Expected result:** The drill reproduces the deterministic source result without claiming correction; states that it inherits possible source errors and does not independently validate source data; and has no tutor obligation beyond a permitted optional one-time sample spot-check.

**MA IDs covered:** MA-022, MA-095, MA-096, MA-097, MA-098.

### AT-027 — Optional drills remain isolated

**Preconditions:** A build containing admitted optional drills and a complete required-capability dataset.

**Steps:**
1. Complete, skip, and make optional drill data unavailable in separate runs.
2. Compare daily-plan completion, gate decisions, required progress, export completeness, and release readiness.

**Expected result:** Optional-drill state does not control any required capability, gate evidence, gate decision, required export, or release result.

**MA IDs covered:** MA-067, MA-087, MA-088.

## 11. Claims, Admission, and Release

### AT-028 — No proficiency claims or ability inference

**Preconditions:** Test data representing zero activity, high hours, long streaks, full completion, high self-reported scores, and mixed skill categories.

**Steps:**
1. Inspect onboarding, Home, Progress, Gates, completion messages, exports, and all descriptive text under each dataset.
2. Search packaged user-facing strings for fluency, ILR 3, C1, professional mastery, certified proficiency, or ability conclusions.

**Expected result:** The app makes no fluency, ILR 3, C1, or professional-mastery claim; derives no ability conclusion from hours, streaks, completion, or self-report; and labels measures as recorded activity rather than certified proficiency.

**MA IDs covered:** MA-101, MA-102, MA-103, MA-104, MA-105, MA-106, MA-107, MA-108, MA-109.

### AT-029 — Feature Admission Test enforcement

**Preconditions:** Proposals representing each failure mode: network dependency, runtime AI, non-verified/non-user data, authored Russian answer, incompatible rights, out-of-scope purpose, and future-content dependency; one compliant required feature proposal; one compliant optional-drill proposal.

**Steps:**
1. Apply all five Feature Admission Test questions to each proposal.
2. Record the decision and supporting evidence.
3. Compare admitted proposals with the four primary capabilities and optional-drill rules.

**Expected result:** Every proposal with any “no” answer is rejected; only a necessary primary-capability feature or fully compliant optional curation-only drill can pass; and no proposal passes based on promised future AI, network service, unreviewed corpus, authoring, per-item validation, or later content work.

**MA IDs covered:** MA-110, MA-111, MA-112, MA-113, MA-114, MA-115, MA-116.

### AT-030 — Complete lesson cycle

**Preconditions:** Fresh user state; eligible audio content; valid default gate; airplane mode enabled; writable local export destination.

**Steps:**
1. Use Home and Today's Plan to identify required work.
2. Perform book work outside the app and record completion or score as self-attested.
3. Perform AnkiDroid work outside the app and record completion or maturity as self-attested.
4. Complete listen-record-compare and mark the recording for tutor review.
5. Enter remaining gate evidence and obtain the decision.
6. Review progress and skill balance.
7. Create and inspect the local export.

**Expected result:** The entire governed lesson cycle completes offline; the gate decision is reproducible; self-attestations remain labeled; pronunciation is not scored; progress remains activity-only; and the export contains all governed user data.

**MA IDs covered:** MA-009, MA-043–MA-079, MA-080, MA-081, MA-084, MA-109, MA-119, MA-120, MA-122.

### AT-031 — Final release-readiness decision

**Preconditions:** Final release candidate and results from AT-001 through AT-030.

**Steps:**
1. Confirm all four primary capabilities are complete.
2. Confirm licensed native audio and matching metadata preparation is complete.
3. Confirm all shipped capabilities pass offline testing.
4. Confirm local export passes.
5. Confirm rights records, attributions, and share-alike disposition are complete.
6. Confirm the full lesson cycle passes.
7. Confirm optional drills, if any, pass their conditional criteria but are not treated as release dependencies.
8. Inspect for any substitution of missing required content.

**Expected result:** Release is approved only when all mandatory conditions pass; optional drills do not control approval; and no missing content is replaced with generation, network access, unverified material, original item authoring, or unfunded validation work.

**MA IDs covered:** MA-117, MA-118, MA-119, MA-120, MA-121, MA-122, MA-123.

## 11A. Subordinate v1.1 Usability

### AT-032 — Dark-by-default appearance

**Objective:** Verify the local appearance control.

**Preconditions:** A new or upgraded installation with no prior user appearance choice.

**Procedure:** Confirm Dark is applied; change to Follow system; restart the app and confirm Follow system persists; confirm no Light-only option exists; confirm the preference is stored locally and applied at theme level.

**Expected result:** Dark is the default; Follow system persists; no Light-only option; no network, account, analytics, or permission change.

**MA IDs covered:** MA-124, MA-125, MA-126, MA-127, MA-128, MA-129.

### AT-033 — Continue destinations

**Objective:** Verify Continue opens only the next unfinished required destination.

**Preconditions:** Packaged plan definitions and local user data for book, AnkiDroid, listen-record-compare, and gate states.

**Procedure:** Exercise Continue for each governed destination, for no active lesson, for completed required work, for unavailable content, and for corrupt data. Confirm Book and AnkiDroid destinations carry the plan-definition identity and present that item and its completion action. Confirm it never opens an unrelated lesson or audio model.

**Expected result:** Continue resolves only from packaged plans and local data and fails safely when the destination is unavailable or corrupt.

**MA IDs covered:** MA-130, MA-131, MA-132, MA-133, MA-134, MA-135, MA-136, MA-137.

### AT-034 — Listen-record-compare ergonomics

**Objective:** Verify A/B playback, speeds, replace-take, and cleanup.

**Preconditions:** A playable licensed model and a readable local recording.

**Procedure:** Play the model at 0.75× and 1.0×; play the user recording; run automatic A/B and a repeat loop for at least two complete cycles; stop during model playback and during recording playback; background the app, navigate away, or lose audio focus; cause a playback error; replace the current take; clear the tutor-review mark. Confirm conflicting playback actions are disabled while A/B is active.

**Expected result:** Model speed does not rewrite the packaged file; recordings play at 1.0×; looping stops on background, navigation away, focus loss, or error; replace-take persists; completion remains performance, not pronunciation judgment.

**MA IDs covered:** MA-138, MA-139, MA-140, MA-141, MA-142, MA-143, MA-144, MA-145.

### AT-035 — Backup status

**Objective:** Verify backup status from local export records.

**Preconditions:** Local export records covering never exported, completed, failed, and incomplete recording-export cases.

**Procedure:** Display each state and confirm counts, filename, time, missing-recording warning, and the absence of a protection claim for unsuccessful exports. Confirm recording coverage uses the exported recording-set digest, including same-count replacement, added recordings, missing files, failed export, and an unchanged complete export.

**Expected result:** Status is factual, local, and does not add import, cloud backup, or network access.

**MA IDs covered:** MA-146, MA-147, MA-148.

### AT-036 — Data and storage health

**Objective:** Verify factual health calculations and unavailable states.

**Preconditions:** Readable and unreadable database, catalog, recording, and storage conditions.

**Procedure:** Confirm each displayed field is calculated off the main thread and that unreadable values appear as unavailable rather than guessed.

**Expected result:** No sensitive paths, telemetry, or destructive cleanup controls appear.

**MA IDs covered:** MA-149, MA-150, MA-151, MA-152.

### AT-037 — Factual study balance

**Objective:** Verify seven-day and thirty-day category calculations.

**Preconditions:** Local logs including a completed zero-minute activity and a negative stored duration.

**Procedure:** Confirm category counts and minutes, recorded versus unrecorded categories, zero-minute completed labeling, and hidden totals when a negative duration is stored.

**Expected result:** No proficiency, streak, badge, ranking, or deficiency characterization is shown.

**MA IDs covered:** MA-153, MA-154, MA-155, MA-156, MA-157.

### AT-038 — Accessibility of affected controls

**Objective:** Verify TalkBack semantics, touch targets, and large-font behavior on affected screens.

**Preconditions:** Home Continue, listen-record-compare controls, backup status, and health/balance views.

**Procedure:** Confirm labels and state descriptions, color-independent control identity, and that long lists remain lazy.

**Expected result:** Loading, empty, unavailable, corrupt-data, and in-progress states exist for every affected screen.

**MA IDs covered:** MA-158, MA-159, MA-160, MA-161.

### AT-039 — v1.1 does not rewrite the frozen 1.0.0 candidate

**Objective:** Verify the historical 1.0.0 candidate record remains unchanged while the v1.1 build stays unfrozen and not approved.

**Preconditions:** The preserved 1.0.0 identity from run 35754785768.

**Procedure:** Confirm that identity, SHA-256, byte count, and source commit remain recorded; confirm the v1.1 digest differs; confirm public release remains NOT_APPROVED.

**Expected result:** v1.1 supersedes the application build only. AT-001 through AT-031 remain outstanding.

**MA IDs covered:** MA-162, MA-163.

## 12. MA Coverage Table

| MA ID | Acceptance test(s) |
|---|---|
| MA-001 | AT-001 |
| MA-002 | AT-001 |
| MA-003 | AT-001 |
| MA-004 | AT-002, AT-016 |
| MA-005 | AT-002, AT-006 |
| MA-006 | AT-002, AT-026 |
| MA-007 | AT-001, AT-003 |
| MA-008 | AT-003 |
| MA-009 | AT-004, AT-020, AT-030 |
| MA-010 | AT-004, AT-005 |
| MA-011 | AT-004, AT-005 |
| MA-012 | AT-004, AT-005 |
| MA-013 | AT-004, AT-005 |
| MA-014 | AT-004, AT-005 |
| MA-015 | AT-004, AT-005 |
| MA-016 | AT-004, AT-005 |
| MA-017 | AT-004, AT-005 |
| MA-018 | AT-004 |
| MA-019 | AT-004, AT-019, AT-020 |
| MA-020 | AT-006 |
| MA-021 | AT-006 |
| MA-022 | AT-006, AT-026 |
| MA-023 | AT-006 |
| MA-024 | AT-007 |
| MA-025 | AT-007 |
| MA-026 | AT-007 |
| MA-027 | AT-005, AT-007 |
| MA-028 | AT-007 |
| MA-029 | AT-008 |
| MA-030 | AT-008 |
| MA-031 | AT-008 |
| MA-032 | AT-008 |
| MA-033 | AT-008 |
| MA-034 | AT-008, AT-009 |
| MA-035 | AT-009 |
| MA-036 | AT-010 |
| MA-037 | AT-010 |
| MA-038 | AT-010 |
| MA-039 | AT-008 |
| MA-040 | AT-008 |
| MA-041 | AT-011 |
| MA-042 | AT-011 |
| MA-043 | AT-012, AT-030 |
| MA-044 | AT-012, AT-030 |
| MA-045 | AT-012, AT-030 |
| MA-046 | AT-012, AT-030 |
| MA-047 | AT-012, AT-030 |
| MA-048 | AT-012, AT-030 |
| MA-049 | AT-012, AT-030 |
| MA-050 | AT-010, AT-013, AT-030 |
| MA-051 | AT-010, AT-013, AT-030 |
| MA-052 | AT-013, AT-030 |
| MA-053 | AT-013, AT-030 |
| MA-054 | AT-013, AT-030 |
| MA-055 | AT-007, AT-013, AT-030 |
| MA-056 | AT-015, AT-030 |
| MA-057 | AT-015, AT-030 |
| MA-058 | AT-002, AT-016 |
| MA-059 | AT-016 |
| MA-060 | AT-014, AT-030 |
| MA-061 | AT-014, AT-030 |
| MA-062 | AT-014, AT-023, AT-030 |
| MA-063 | AT-014, AT-023, AT-030 |
| MA-064 | AT-014, AT-023, AT-030 |
| MA-065 | AT-007, AT-015 |
| MA-066 | AT-015, AT-030 |
| MA-067 | AT-017, AT-027 |
| MA-068 | AT-018, AT-030 |
| MA-069 | AT-018, AT-030 |
| MA-070 | AT-018, AT-030 |
| MA-071 | AT-018, AT-030 |
| MA-072 | AT-019, AT-030 |
| MA-073 | AT-020, AT-030 |
| MA-074 | AT-021, AT-022, AT-030 |
| MA-075 | AT-021, AT-022, AT-030 |
| MA-076 | AT-021, AT-022, AT-030 |
| MA-077 | AT-021, AT-022, AT-030 |
| MA-078 | AT-021, AT-022, AT-030 |
| MA-079 | AT-021, AT-022, AT-030 |
| MA-080 | AT-023, AT-030 |
| MA-081 | AT-011, AT-023, AT-030 |
| MA-082 | AT-023 |
| MA-083 | AT-023 |
| MA-084 | AT-005, AT-014, AT-023, AT-030 |
| MA-085 | AT-023 |
| MA-086 | AT-023 |
| MA-087 | AT-017, AT-024, AT-027 |
| MA-088 | AT-017, AT-024, AT-027 |
| MA-089 | AT-025 |
| MA-090 | AT-025 |
| MA-091 | AT-025 |
| MA-092 | AT-025 |
| MA-093 | AT-025 |
| MA-094 | AT-025 |
| MA-095 | AT-026 |
| MA-096 | AT-026 |
| MA-097 | AT-026 |
| MA-098 | AT-026 |
| MA-099 | AT-003 |
| MA-100 | AT-003 |
| MA-101 | AT-028 |
| MA-102 | AT-028 |
| MA-103 | AT-028 |
| MA-104 | AT-028 |
| MA-105 | AT-018, AT-028 |
| MA-106 | AT-018, AT-028 |
| MA-107 | AT-018, AT-028 |
| MA-108 | AT-018, AT-028 |
| MA-109 | AT-018, AT-028, AT-030 |
| MA-110 | AT-004, AT-029 |
| MA-111 | AT-006, AT-029 |
| MA-112 | AT-006, AT-025, AT-029 |
| MA-113 | AT-008, AT-025, AT-029 |
| MA-114 | AT-003, AT-024, AT-029 |
| MA-115 | AT-029 |
| MA-116 | AT-005, AT-006, AT-029 |
| MA-117 | AT-002, AT-024, AT-031 |
| MA-118 | AT-010, AT-031 |
| MA-119 | AT-004, AT-030, AT-031 |
| MA-120 | AT-021, AT-022, AT-030, AT-031 |
| MA-121 | AT-008, AT-009, AT-031 |
| MA-122 | AT-030, AT-031 |
| MA-123 | AT-006, AT-031 |
| MA-124 | AT-032 |
| MA-125 | AT-032 |
| MA-126 | AT-032 |
| MA-127 | AT-032 |
| MA-128 | AT-032 |
| MA-129 | AT-032, AT-038 |
| MA-130 | AT-033 |
| MA-131 | AT-033 |
| MA-132 | AT-033 |
| MA-133 | AT-033 |
| MA-134 | AT-033, AT-028 |
| MA-135 | AT-033 |
| MA-136 | AT-033 |
| MA-137 | AT-033 |
| MA-138 | AT-034 |
| MA-139 | AT-034 |
| MA-140 | AT-034 |
| MA-141 | AT-034 |
| MA-142 | AT-034 |
| MA-143 | AT-034 |
| MA-144 | AT-034 |
| MA-145 | AT-007, AT-034 |
| MA-146 | AT-035 |
| MA-147 | AT-035 |
| MA-148 | AT-005, AT-035 |
| MA-149 | AT-036 |
| MA-150 | AT-036 |
| MA-151 | AT-036 |
| MA-152 | AT-036 |
| MA-153 | AT-037 |
| MA-154 | AT-037 |
| MA-155 | AT-018, AT-037 |
| MA-156 | AT-018, AT-037 |
| MA-157 | AT-028, AT-037 |
| MA-158 | AT-038 |
| MA-159 | AT-038 |
| MA-160 | AT-038 |
| MA-161 | AT-038 |
| MA-162 | AT-039 |
| MA-163 | AT-031, AT-039 |

**Coverage audit:** No MA requirement is uncovered.

## 13. Conformance Confirmation

This Acceptance Criteria test map introduces no scope beyond, and contradicts nothing in, `MASTER_AUTHORITY_v1.0.md` and `PRODUCT_BRIEF_v1.1.md`. AT-032 through AT-039 are subordinate v1.1 automated coverage. They do not mark AT-001 through AT-031 passed and do not approve public release.
