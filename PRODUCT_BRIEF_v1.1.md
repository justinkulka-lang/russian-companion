# PRODUCT BRIEF v1.1
## Offline Russian Learning Companion

**Status:** Product definition for v1  
**Companion course:** *The New Penguin Russian Course*  
**Evidence base:** `RESEARCH_DOSSIER_v1.0.md`, treated as evidence rather than authority

## 1. Product Definition

The product is a fully offline Android companion for an independent adult learning Russian while progressing through *The New Penguin Russian Course*. It turns the learner's course progress into a clear daily practice routine, supports listen-and-repeat practice with licensed native audio, records completion and advancement evidence, and prevents premature advancement through explicit gates.

The app is a conductor, practice companion, gatekeeper, and scorekeeper. It is not the source of Russian authority. Correct Russian comes from the user's book and answer key, AnkiDroid materials selected by the user, properly licensed pre-made content, and human correction when available. Speaking and writing quality remain subject to human correction.

The initial user is a serious self-directed beginner progressing from zero or near-zero proficiency through the book's approximate beginner-to-A2 arc. The user uses the owned book and AnkiDroid alongside the app and may engage a qualified Russian tutor for high-value live correction. The app must be useful before a tutor is engaged and remain useful over sustained daily study rather than promise rapid fluency.

## 2. v1 Product Goal

Make each study day unambiguous and accountable while the learner completes the companion course: know what to do, complete book and AnkiDroid practice, capture pronunciation work, prove the advancement conditions, preserve the record locally, and see whether any language skill is being neglected.

v1 ends with the Penguin-concurrent beginner pathway. Post-book B1, B2, or C1 pathways are not part of this release.

## 3. Primary User Capabilities

v1 must let the user do these four things:

1. **Run today's study plan.** View and complete a lesson-aligned checklist covering book work, assigned AnkiDroid work, listen-record-compare practice, and any scheduled tutor task. The app may reference lesson numbers or user-entered assignments but must not reproduce the book's protected exercises or text. Spaced vocabulary and form drilling occurs in AnkiDroid; grammar practice occurs in the user's book and its answer key. The app orchestrates and records that work rather than hosting equivalent banks.

2. **Listen, record, and compare.** Play a properly licensed native model, record the user's imitation locally, replay both for A/B comparison, and mark the recording for later tutor review. The app reports completion only; it does not score pronunciation quality. This is v1's additive app-native practice function.

3. **Check and satisfy advancement gates.** See the requirements for the next course segment and record the permitted evidence: self-entered book revision score, self-attested completion or maturity status from the user's AnkiDroid work, and tutor sign-off when available or required by the displayed rule. There is no AnkiDroid integration. The app applies the stated logical rule to recorded evidence but does not independently verify the self-attestation or judge the learner's Russian.

4. **Log, review, and preserve progress.** Record study time and completion by reading, listening, vocabulary/forms, grammar, speaking/pronunciation, writing, and tutor work; review cumulative progress and visible skill imbalances; and export logs, gate evidence, progress data, and recordings to local files such as CSV or JSON plus audio. Local export provides a user-controlled backup that can be copied off the device without adding network access, sync, or an account. Activity measures describe work completed, not certified proficiency.

Optional curation-only drills may be included only if they satisfy Section 7 and the Feature Admission Test. They are not a required primary capability or release dependency.

## 4. Explicit Non-Goals for v1

v1 does not:

- generate Russian words, sentences, explanations, drills, feedback, or study content;
- use runtime AI, network services, cloud sync, web lookup, streaming, or remote accounts;
- judge pronunciation, accent quality, free speaking, free writing, or open-ended Russian;
- include automatic speech recognition or transcription;
- replace a qualified tutor, corrected output, conversation, or immersion;
- claim to deliver fluency, ILR 3, C1, or professional mastery;
- reproduce, scan, summarize, or bundle copyrighted Penguin lesson text or exercises;
- build an integrated spaced-repetition scheduler, duplicate AnkiDroid, or host equivalent vocabulary or word-form banks;
- host original grammar-review items or minimal-pair drills;
- integrate with AnkiDroid or treat self-attested AnkiDroid status as independently verified;
- import arbitrary third-party decks or accept unverified community content as authoritative;
- require an original Russian item bank, a dedicated content author, or ongoing tutor validation of app content;
- provide post-book B1–C1 curricula, handwriting instruction, an open-ended dictionary, a general media library, social features, gamification systems, or tutor scheduling;
- infer ability from hours, streaks, completion, or self-reported results.

If a proposed feature cannot operate solely on pre-made verified content or the user's own logged data, it is out of scope.

## 5. Offline Requirement

Every v1 capability must work with the device in airplane mode after installation. This includes onboarding, daily planning, licensed native-audio playback, local voice recording and replay, optional admitted curation-only drills, gates, logs, progress views, local data and recording export, search or filtering included in v1, attributions, and license notices.

No feature may have a degraded "offline mode" because there is no online mode. No runtime action may download content, call an API, invoke remote AI, stream media, require sign-in, or depend on a network time source. All product content ships pre-made with the app; all user data remains usable locally. Backup is an explicit local-file export initiated by the user, not cloud backup or synchronization.

## 6. Content and Rights Policy

Only content with documented redistribution rights may ship. Each included data or audio asset must have traceable source, license, attribution, modification status, and any required share-alike treatment. Audio with absent, unclear, or incompatible licensing is excluded. The distributed app must include readable attributions and a content-license inventory. Public release is blocked until share-alike obligations for the distributed derivative data have been confirmed.

The Penguin book supplies sequence context only. v1 may identify the learner's current lesson and prescribe use of the user's copy, but it does not reproduce or replace the book's practice content. Any app-native Russian practice uses licensed, already-corrected source material under Section 7; it is not independently authored for the app.

## 7. Content Preparation and Ownership

v1 introduces no dependency on a person authoring or validating an original Russian item bank. There is no Russian Content Editor role, no authored drill-bank requirement, and no ongoing tutor obligation to review app content.

The **Product Owner** owns scope, release approval, lesson-to-activity mappings, non-linguistic plan and gate defaults, licensing records, and selection and formatting of any optional curation-only material. The user owns self-entered logs, scores, AnkiDroid self-attestations, completion confirmations, recordings, exports, and tutor sign-offs.

The required native-audio practice content is limited to properly licensed, pre-existing native recordings and their source-supplied verified text. Preparation may select, filter, package, and format this material but may not write new Russian prompts, answers, explanations, or assessment criteria.

Optional curation-only drills are admitted only when all of the following are true:

- they are constructed directly and mechanically from already-corrected, properly licensed structured data, such as OpenRussian stress, declension, or conjugation fields;
- their prompts and correct responses are direct presentations or deterministic transformations of source fields, not newly written items or answer keys;
- preparation consists only of selection, license filtering, mechanical formatting, and provenance recording;
- no included item requires original authoring or per-item linguistic judgment;
- the complete feature passes the Feature Admission Test.

A qualified tutor may perform a one-time spot-check of a sample batch before release, but this is optional assurance rather than an ongoing role, per-item validation requirement, or release dependency. If a proposed drill requires original authoring, an invented distractor, an original explanation, or per-item linguistic judgment, it is excluded from v1. Minimal-pair drills and original grammar items are deferred to a later version contingent on a qualified validator being available.

## 8. Decisions on Dossier Recommendations

### 8.1 Staged scope — RATIFIED

v1 is limited to a Penguin-concurrent beginner companion; later proficiency stages require separate product decisions. This keeps the first release coherent and usable without pretending an offline app can contain advanced mastery.

### 8.2 AnkiDroid orchestration vs. integrated SRS — RATIFIED: ORCHESTRATE ANKIDROID

v1 prescribes and logs AnkiDroid work but does not implement an SRS. Spaced vocabulary and form drilling lives in AnkiDroid. Rebuilding a mature scheduler or equivalent drill bank adds substantial scope without improving the core daily-conductor goal. There is no AnkiDroid integration: the user self-reports completion or maturity status locally, and the app does not independently verify it. A future integrated SRS is not presumed; it would require a separate brief, a proven algorithm such as FSRS, verified content, and a demonstrated need.

### 8.3 Playback/record vs. ASR — RATIFIED: PLAYBACK AND RECORDING ONLY

v1 includes licensed native-model playback, local recording, and A/B replay. ASR is excluded. Transcription is not reliable pronunciation judgment, cannot serve as an advancement authority, and adds complexity without resolving the core need for human correction. Minimal-pair drills are deferred because they require a qualified validator and are not necessary for the additive listen-record-compare function.

### 8.4 Human-authored quizzes and assignments — RATIFIED AS A CONTENT-SAFETY RULE; AUTHORED ITEMS EXCLUDED FROM v1

The recommendation that the app never generate items at runtime remains ratified. However, v1 contains no human-authored app-native quiz or assignment bank because no qualified content-authoring role is funded and book and AnkiDroid practice already cover those functions. Any optional automatically scored drill must be a curation-only presentation of already-corrected licensed structured data under Section 7, with fixed responses derived directly from source fields. Original grammar items, authored distractors, explanations, and minimal-pair items are deferred.

### 8.5 Rubric-based advancement — RATIFIED WITH A MINIMAL v1 BOUNDARY

v1 applies explicit gates using only user-entered book scores, self-attested AnkiDroid status, completed prescribed work, and tutor sign-off when included in the displayed rule. There is no AnkiDroid integration, and AnkiDroid-derived evidence is expressly recorded as the user's self-report rather than independently verified data. Gates may block the app's next planned segment, but they never claim to certify language proficiency. Gate rules ship pre-made and may be made stricter by the user; the app does not invent new criteria.

## 9. Feature Admission Test

A proposed v1 feature is admitted only when every answer below is **yes**:

1. Can it run completely offline with no runtime AI or network dependency?
2. Does it operate exclusively on pre-made human-verified content or the user's own logged data?
3. If it presents or scores Russian, are the Russian and any correct response already established by the licensed, corrected source data without original authoring or per-item linguistic judgment?
4. Are all bundled inputs legally redistributable with provenance, attribution, and share-alike duties recorded?
5. Is it necessary for one of the four primary user capabilities, or does an optional curation-only drill satisfy every condition in Section 7 without becoming a release dependency?

Any **no** rejects the feature from v1. A feature is not admitted on the promise that AI, a future network service, an unreviewed corpus, original item authoring, per-item validation, or later content work will make it function.

## 10. Success Criteria

v1 is successful when all of the following are true:

- **Complete offline operation:** every shipped capability passes its end-to-end test in airplane mode with no prior sign-in or runtime download.
- **Daily clarity:** from the home entry point, the user can identify today's required work and begin each required activity without consulting a separate setup guide.
- **Core-loop completeness:** the user can finish a full lesson cycle—plan, book and AnkiDroid work, listening/recording, evidence entry, gate decision, progress review, and local export—using only shipped content and local data.
- **Truthful practice and scoring:** listen-record-compare reports completion without judging pronunciation. Any optional automatically scored curation-only drill derives its correct response directly from already-corrected licensed structured data and does not rely on an original item bank.
- **Content readiness:** every bundled Russian asset comes from an already-corrected licensed source and has recorded provenance and compatible licensing. There are no placeholder, generated, originally authored, or linguistically unreviewed production items presented as authoritative.
- **Gate integrity:** advancement decisions are reproducible from the displayed rule and the recorded evidence, including clearly labeled self-attested AnkiDroid evidence, with no claim of independent Anki verification and no hidden inference of proficiency.
- **Durable local continuity:** plans, logs, gate evidence, progress, and recordings remain available after app restart and device reboot without network access.
- **Local recoverability:** the user can export logs, gate evidence, progress, and recordings as usable local files so they can be copied elsewhere and remain available for recovery or reference after device loss, without an account or network dependency in the app.
- **Scope discipline:** v1 contains no feature that fails the admission test and makes no claim that activity metrics equal fluency or professional proficiency.

Usage streaks, total hours, optional drill volume, and course completion may be reported, but none alone constitutes product success or proof of Russian proficiency.

## 11. v1 Release Boundary

The release is ready only when the four primary capabilities are complete, required licensed audio and metadata are prepared, offline behavior and local export are verified, rights records and user-facing attributions are present, and the full lesson-cycle success test passes. Optional curation-only drills do not block release and may ship only after clearing Sections 7 and 9. Missing content cannot be substituted with generation, network access, unverified material, original item authoring, or unfunded validation work. Features outside this brief remain excluded until a later product-definition pass explicitly admits them.

**Confirmation:** No remaining v1 feature depends on an unfunded content-authoring role.
