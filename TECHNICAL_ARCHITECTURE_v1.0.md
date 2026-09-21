# TECHNICAL ARCHITECTURE v1.0
## Offline Russian Learning Companion

**Authority:** `MASTER_AUTHORITY_v1.0.md`  
**Scope source:** `PRODUCT_BRIEF_v1.1.md`

## 1. Architecture Scope and Principles

The app is a native Android application implemented with Kotlin and Jetpack Compose. It uses a layered presentation/domain/data structure, Navigation Compose, Room for structured local data, DataStore for lightweight local state, packaged assets and a seeded local database for bundled content, Gradle for builds, Git/GitHub for version control, and GitHub Actions for continuous integration. These choices are subordinate to the required outcomes and introduce no additional user capability. *(Trace: MA-001–MA-008, MA-117; Product Brief §§1–4, 11)*

The governing architectural principles are:

1. **Offline is the only runtime mode.** Required behavior never branches to a network-backed alternative. *(Trace: MA-009–MA-019, MA-110, MA-119; Product Brief §5)*
2. **Bundled Russian is immutable authority input.** Runtime code presents eligible pre-made content but neither creates nor linguistically validates it. *(Trace: MA-020–MA-023, MA-111–MA-113; Product Brief §§6–7, 9)*
3. **User evidence is local and explicit.** Book and AnkiDroid evidence remains self-attested, while gates apply only displayed deterministic rules. *(Trace: MA-056–MA-067; Product Brief §§3.3, 8.5)*
4. **Rights metadata travels with content.** No asset is eligible without its license and provenance record. *(Trace: MA-029–MA-040; Product Brief §6)*
5. **Optional means removable.** Curation-only drills are an independently omittable content-and-feature module with no gate or release dependency. *(Trace: MA-087–MA-100; Product Brief §§3, 7, 11)*

## 2. System Context and Prohibited Dependencies

At runtime, the system boundary contains the Android app, its packaged content, its private local structured store, its private recording files, and user-initiated local export files. The user's book, AnkiDroid, and any tutor remain outside the system boundary. Their work enters only through user-entered records. *(Trace: MA-003–MA-004, MA-018–MA-019, MA-060–MA-064, MA-080–MA-084; Product Brief §§1, 3, 5)*

The runtime dependency graph contains no networking client, remote API, authentication provider, cloud store, synchronization service, analytics service, advertising service, AI model, generative engine, ASR engine, transcription engine, or AnkiDroid connector. The application does not request general network behavior as part of its product operation. *(Trace: MA-010–MA-017, MA-020, MA-027, MA-082–MA-084; Product Brief §§4–5)*

Device-local platform services are limited to capabilities already governed: local storage, audio playback, microphone capture, device-local time values, and user-directed local file creation. None supplies Russian judgments or remote data. *(Trace: MA-017–MA-019, MA-050–MA-055, MA-072–MA-079; Product Brief §§3, 5)*

## 3. Logical Layers

### 3.1 Presentation layer

Jetpack Compose presents onboarding, Home, Today's Plan, Practice, Gates, Progress, Local Export, Attributions & Licenses, and the conditional Optional Drills area. Navigation Compose owns navigation between those destinations. Presentation consumes domain outcomes and never reads packaged assets or persistence stores directly. *(Trace: MA-003, MA-039–MA-040, MA-043–MA-079, MA-087–MA-098; Product Brief §3)*

### 3.2 Domain layer

The domain layer owns only the governed application rules:

- assemble the lesson-aligned plan from shipped non-linguistic plan definitions and local state;
- record activity completion and categorized study time;
- coordinate licensed model playback and local recording state;
- apply displayed gate rules to recorded evidence;
- calculate activity totals and skill-balance comparisons without ability inference;
- assemble a complete local export;
- determine content eligibility from bundled rights metadata;
- expose optional deterministic drill transformations only when admitted.

It contains no Russian generator, linguistic validator, pronunciation evaluator, proficiency estimator, SRS scheduler, or AnkiDroid adapter. *(Trace: MA-020–MA-028, MA-043–MA-109; Product Brief §§3–4, 7–8)*

### 3.3 Data layer

The data layer separates:

- **read-only bundled content:** course-sequence references, plan definitions, gate definitions, eligible native-audio metadata, verified source-supplied stress-marked text, provenance, licenses, attributions, and any admitted optional structured drill source;
- **mutable structured user data:** onboarding state, active lesson, plan instances, completion records, activity logs, gate evidence, gate decisions, recording metadata, tutor-review marks, and export history;
- **mutable user files:** voice recordings and completed export packages;
- **lightweight state:** onboarding completion and minimal presentation preferences required to restore the user's place.

Room stores structured bundled and user records with a logical separation between immutable source records and mutable user records. DataStore stores only lightweight local state that does not replace the authoritative structured records. *(Trace: MA-018–MA-019, MA-029–MA-040, MA-043–MA-079; Product Brief §§3, 5–7)*

## 4. Data Model

The following model defines information responsibilities, not implementation-level schemas.

### 4.1 Bundled content entities

| Entity | Essential fields and relationships | Governing trace |
|---|---|---|
| `CourseSegment` | Stable segment identity, Penguin lesson reference, order within the shipped beginner arc | MA-001–MA-002, MA-007, MA-041–MA-042 |
| `PlanDefinition` | Stable identity, segment link, required activity type, user-facing non-Penguin instruction, ordering | MA-043–MA-047, MA-080–MA-081 |
| `GateDefinition` | Stable identity, segment link, displayed logical rule, evidence types, default status, optional user-added tutor condition | MA-056–MA-059, MA-065–MA-067 |
| `AudioModel` | Stable asset identity, local audio reference, matched text identity, source identity, rights-record identity, eligibility status | MA-023, MA-036–MA-038, MA-050–MA-051 |
| `SourceText` | Stable identity, exact verified source-supplied stress-marked text, source-record link, modification status | MA-023, MA-030–MA-032, MA-038, MA-051 |
| `SourceRecord` | Source identity, provenance, redistribution basis, applicable license, attribution, modification status, share-alike treatment, confirmation status | MA-029–MA-040 |
| `AssetRightsRecord` | Per-asset source link, asset identifier, license status, compatibility decision, attribution requirement, inclusion/exclusion result | MA-029–MA-038 |
| `OptionalDrillDefinition` | Optional package identity, deterministic transform identifier, source-field references, provenance link, inherited-error disclosure | MA-087–MA-098 |

The bundled model contains no protected Penguin text or exercises, original Russian prompts, authored distractors, original answer keys, original grammar explanations, or minimal-pair items. *(Trace: MA-005, MA-020–MA-023, MA-041–MA-042, MA-092, MA-099–MA-100; Product Brief §§4, 6–7)*

### 4.2 Mutable user entities

| Entity | Essential fields and relationships | Governing trace |
|---|---|---|
| `LearnerState` | Active course segment, onboarding completion, current local plan context | MA-001–MA-004, MA-043, MA-049 |
| `PlanInstance` | Local date value, plan-definition link, item states, completion times | MA-043–MA-048, MA-068 |
| `ActivityLog` | Category, duration, completion, local timestamp, optional segment and plan-item links, self-attestation marker where applicable | MA-048, MA-061, MA-063–MA-064, MA-068–MA-071 |
| `RecordingRecord` | Stable recording identity, AudioModel link, private local file reference, creation time, tutor-review mark, completion state | MA-052–MA-055, MA-072–MA-079 |
| `GateEvidence` | Gate link, evidence type, user-entered value, self-attestation status, local timestamp, optional user-added tutor sign-off | MA-056–MA-064 |
| `GateDecision` | Gate link, evaluated rule version, referenced evidence identities, condition outcomes, final status, evaluation time | MA-056–MA-067 |
| `ProgressSnapshot` | Reproducible aggregate of ActivityLog identities and category totals or a recalculable equivalent | MA-068–MA-071, MA-105–MA-109 |
| `ExportRecord` | Export time, local destination reference when available, included-record counts, completion/failure status | MA-074–MA-079, MA-120 |

No mutable user entity contains an app-authored linguistic judgment, pronunciation score, proficiency score, inferred ability, or independent verification flag for book or AnkiDroid evidence. *(Trace: MA-022, MA-024–MA-027, MA-061–MA-065, MA-101–MA-109; Product Brief §§3–4)*

## 5. Bundled Content Preparation and Seeding

### 5.1 Governed preparation boundary

Content preparation occurs before app distribution and produces a versioned release input. It is a mechanical selection, filtering, matching, formatting, and provenance-recording process; it does not author or linguistically validate Russian. *(Trace: MA-005–MA-006, MA-020–MA-023, MA-037–MA-038, MA-089–MA-098; Product Brief §§6–7)*

For every proposed audio file, the preparation record contains the source asset identifier, exact license field, redistribution compatibility outcome, required attribution, modification status, and inclusion/exclusion result. An absent, unclear, or incompatible license produces exclusion. *(Trace: MA-029–MA-037; Product Brief §6)*

Every included audio model is linked to the already-corrected, source-supplied stress-marked text that the audio represents. A missing, ambiguous, or unverified match produces exclusion. The match and the source identities are retained in the packaged rights inventory. *(Trace: MA-023, MA-030, MA-036–MA-038, MA-050–MA-051, MA-118; Product Brief §§6–7, 11)*

Share-alike status is recorded at source and derived-dataset levels. A public-release build is not release-eligible until the applicable share-alike obligations are marked confirmed. *(Trace: MA-034–MA-035, MA-121; Product Brief §§6, 11)*

### 5.2 Packaging

The release package contains:

- a seeded local database holding structured course references, plan and gate definitions, content metadata, rights records, and eligible text;
- packaged local native-audio assets addressed by stable asset identity;
- a readable attribution record and content-license inventory;
- optional structured drill data only when its feature-admission evidence is complete.

No runtime download or content update path exists in v1. Content changes require a new app release package. *(Trace: MA-014, MA-018, MA-029–MA-040, MA-087–MA-098, MA-110–MA-118; Product Brief §§5–7, 9, 11)*

### 5.3 Seed integrity

At first use and compatible upgrades, the app establishes the bundled content locally without overwriting mutable user data. If required bundled content is absent, corrupt, unmatched, or ineligible, the affected content is unavailable rather than regenerated or substituted. *(Trace: MA-018–MA-023, MA-036–MA-038, MA-072–MA-073, MA-123; Product Brief §§5, 10–11)*

## 6. Audio Architecture

### 6.1 Native-model playback

Native-model audio is packaged inside the installed application and played only from its local asset. Playback resolves the matching `SourceText` and `AssetRightsRecord` before the model is eligible to appear. No streaming fallback exists. *(Trace: MA-014, MA-018, MA-036–MA-038, MA-050–MA-051; Product Brief §§3.2, 5–7)*

### 6.2 User recording

Microphone input is captured as a local audio file in app-private storage. `RecordingRecord` holds the model association and tutor-review mark separately from the bytes. Recording processing is limited to capture, storage, and playback; it performs no transcription, ASR, pronunciation scoring, or linguistic analysis. *(Trace: MA-024–MA-027, MA-052–MA-055; Product Brief §§3.2, 4, 8.3)*

User recordings remain available across app restart and device reboot as long as the app's local data remains intact. A recording becomes externally portable only through the governed local export. *(Trace: MA-072–MA-079; Product Brief §§3.4, 10)*

## 7. Daily Plan and External-Work Architecture

Shipped `PlanDefinition` records identify lesson-aligned book, AnkiDroid, listen-record-compare, and optional user-chosen tutor activities. Penguin references contain only course-sequence context and directions to use the user's copy. *(Trace: MA-041–MA-047; Product Brief §§3.1, 6)*

Book and AnkiDroid activities have no application connector. Completion and results enter as user data through explicit self-attestation. Vocabulary/forms remain in AnkiDroid; grammar practice remains in the book and answer key. *(Trace: MA-060–MA-064, MA-080–MA-086; Product Brief §§3.1, 8.2, 8.5)*

## 8. Gate Evaluation Architecture

Gate definitions are versioned, shipped rule data. A gate evaluator applies the displayed rule only to persisted evidence identified in the resulting `GateDecision`; this makes the decision reproducible without treating any evidence as a linguistic judgment. *(Trace: MA-056–MA-057, MA-065–MA-066; Product Brief §§3.3, 8.5, 10)*

Default rule data contains no mandatory tutor condition. A user may add tutor sign-off only as an additional condition, and that customization is stored with the gate. Optional drill activity and results are not defined as eligible evidence types. *(Trace: MA-004, MA-058–MA-059, MA-067; Product Brief §§1, 3.3, 7, 8.5)*

Book and AnkiDroid evidence types carry a mandatory self-attested classification through storage, evaluation, display, and export. No independent-verification state exists for them. *(Trace: MA-060–MA-064; Product Brief §§3.3, 8.2, 8.5)*

## 9. Progress Architecture

Progress is calculated solely from the user's local activity records across the eight governed categories. Aggregation produces totals and category comparisons, not proficiency labels, predictions, ranks, or ability scores. *(Trace: MA-068–MA-071, MA-101–MA-109; Product Brief §§3.4, 4, 10)*

If a materialized progress snapshot is used, it remains traceable to its source logs and can be rebuilt locally. The authorities do not require a snapshot, so recomputation from logs is the minimal acceptable alternative. This is an explicit minimal choice where the governing documents are silent.

## 10. Local Persistence and Integrity

Room transactions keep related mutable records consistent, including plan completion with its activity log and gate decisions with their evidence references. Recording metadata is committed only for an identifiable local recording file. *(Trace: MA-048, MA-052–MA-055, MA-057, MA-066, MA-072–MA-073; Product Brief §§3, 10)*

The app reopens the same authoritative local user records after process restart and device reboot. It never reconstructs missing Russian, evidence, or user history through a network or generative fallback. Corrupt required data produces a visible unavailable/error state rather than fabricated content or results. *(Trace: MA-009–MA-023, MA-072–MA-073, MA-123; Product Brief §§5, 10–11)*

## 11. Local Export Architecture

Export is initiated only by the user and writes one portable local export package to a user-selected local file destination. The package contains:

- a UTF-8 JSON manifest with export version, record counts, file inventory, and associations;
- UTF-8 CSV files for activity logs, gate evidence and decisions, and progress/category totals;
- the user's recording audio files with stable export filenames;
- a recording index mapping each audio filename to its model, log, and tutor-review metadata.

The package contains no authentication token or remote reference and requires no network operation. The chosen formats are the minimal widely readable set consistent with the Brief's CSV/JSON-plus-audio direction. *(Trace: MA-074–MA-079, MA-120; Product Brief §§3.4, 5, 10–11)*

Export completion is reported only after all governed datasets and readable recordings have been written and the manifest agrees with the package contents. A failed or partial write is reported as unsuccessful rather than as a valid backup. Import/restore is not added because the governing documents require local export and recoverable files, not an in-app restore capability. *(Trace: MA-075–MA-079, MA-120; Product Brief §§3.4, 10–11)*

## 12. Attributions, Licenses, and Release Readiness

Rights records are stored in the bundled structured store and rendered by the Attributions & Licenses area without network access. Each eligible content asset resolves to its provenance, license, attribution, modification status, and share-alike treatment. *(Trace: MA-029–MA-040, MA-121; Product Brief §§5–6, 11)*

Release-readiness inputs include content inventory completeness, per-file audio license decisions, audio-to-text matches, share-alike confirmation, offline test status, local-export test status, and completion of the four required capabilities and full lesson cycle. These are build/release controls, not additional user features. *(Trace: MA-035–MA-038, MA-117–MA-123; Product Brief §11)*

## 13. Optional Curation-Only Drill Architecture

The base v1 architecture does not require an optional drill package. If one is admitted, it is packaged as read-only structured source records plus a declared deterministic field transformation. The output can be reproduced solely from recorded source fields; no authored prompt, distractor, explanation, answer key, or per-item judgment is stored. *(Trace: MA-087–MA-093; Product Brief §§3, 7, 9)*

Each optional drill record retains its source provenance and rights relationship. Its user-facing metadata states that the app inherits possible source errors and neither corrects nor independently validates the source. Any tutor sample spot-check is external, one-time, optional release evidence and does not become runtime data or an ongoing obligation. *(Trace: MA-094–MA-098; Product Brief §7)*

The optional drill module has no dependency edge into plan completion, gate evidence, gate decisions, required progress, export completeness, or release completeness. Original grammar-review and minimal-pair content have no place in the v1 model. *(Trace: MA-067, MA-087–MA-100; Product Brief §§4, 7, 11)*

## 14. Build, Version Control, and Continuous Integration

Gradle defines reproducible Android builds. Git/GitHub records application, content-manifest, rights-inventory, and governed-document changes. GitHub Actions performs build and automated verification without creating a runtime service dependency. *(Trace: MA-009–MA-019, MA-029–MA-040, MA-110–MA-123; Product Brief §§5–6, 9–11)*

Continuous-integration checks cover, at minimum:

- successful native Android build;
- absence of prohibited runtime dependency categories and network-dependent product behavior;
- completeness and referential integrity of bundled content and rights metadata;
- exclusion of audio lacking a clear compatible license;
- one-to-one eligibility link between each shipped audio model and verified source-supplied stress-marked text;
- deterministic reproducibility and provenance of any optional drill;
- absence of optional drill types from the gate-evidence model;
- successful automated local persistence, gate-evaluation, and export tests;
- the public-release share-alike confirmation gate.

These checks enforce already-governed outcomes and do not add capabilities. *(Trace: MA-029–MA-040, MA-056–MA-067, MA-072–MA-079, MA-087–MA-098, MA-110–MA-123; Product Brief §§6–7, 9–11)*

## 15. Architectural Compliance Matrix

| Concern | Architectural outcome | Governing trace |
|---|---|---|
| Offline | All required content and data paths are local; no network-backed branch exists | MA-009–MA-019, MA-110, MA-119 |
| No runtime AI | No AI or generative component exists in the runtime graph | MA-011–MA-012, MA-020–MA-023, MA-116 |
| No speech judgment | Audio path is capture/playback only | MA-024–MA-027, MA-052–MA-055 |
| Content rights | Eligibility depends on complete provenance and compatible rights metadata | MA-029–MA-040, MA-113, MA-121 |
| Penguin boundary | Only lesson/sequence references are stored | MA-041–MA-042, MA-081 |
| AnkiDroid boundary | External self-attested activity only; no connector or SRS | MA-062–MA-064, MA-080–MA-086 |
| Gate integrity | Displayed versioned rule plus referenced recorded evidence yields decision | MA-056–MA-067 |
| Persistence | Structured data and recordings survive restart/reboot | MA-072–MA-073 |
| Export | User-directed JSON/CSV/audio package is locally portable | MA-074–MA-079, MA-120 |
| Optional drills | Read-only deterministic module with no gate or release dependency | MA-067, MA-087–MA-100 |
| Claims | Aggregation produces activity measures only | MA-101–MA-109 |

## 16. Conformance Confirmation

This Technical Architecture introduces no scope beyond, and contradicts nothing in, `MASTER_AUTHORITY_v1.0.md` and `PRODUCT_BRIEF_v1.1.md`.
