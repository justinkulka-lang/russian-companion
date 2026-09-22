# V2 Rebuild Reconciliation

**Status:** Repository inspection against `MASTER_AUTHORITY_v2.0.md`  
**Inspected commit:** `ab498d30d8d7c9cc4545b162f27811f897bb081d` (`main` after PR #26)  
**Authority:** `docs/governance/MASTER_AUTHORITY_v2.0.md`  
**Date:** 22 September 2026

This document is an inventory and conflict map. Passing v1 automated tests are not treated as proof that the product is correct.

---

## 1. Current product (as shipped)

The installed application is a fully offline Compose companion (`applicationId` `dev.russiancompanion`, `versionName` `1.1.1`, `versionCode` 4). It is **not** an acceptable v2 baseline.

What the learner actually gets:

1. Two-page onboarding: generic offline-companion copy, then a scrollable wall of Lessons 1–30.
2. Home with a Continue card, compact links, appearance, backup/health summaries, and build identity.
3. A daily checklist of Book / AnkiDroid / Listen-record-compare / optional Tutor.
4. A practice library of 1,000 frequency-ranked licensed words assigned in catalog order.
5. Advancement gates that score self-attested book/Anki work plus prescribed listen completions.
6. Progress, local ZIP export, and an attribution inventory.

This is a tracking spine with a catalog attached. It is not a curriculum.

Physical review of frozen v1.1.0 failed. The v1.1.1 correction repaired theme, insets, and onboarding chrome. It did not repair pedagogy, sequence, or usefulness.

---

## 2. Inventory

### 2.1 Screens and navigation

| Surface | Path | v2 judgment |
|---|---|---|
| Onboarding intro + 30-lesson list | `OnboardingScreens.kt` | Defective. Violates MA2-019, MA2-043, MA2-115. |
| Local data failure | `CompanionNavHost.kt` | Reusable pattern. |
| Home | `CompanionNavHost.kt` | Defective information architecture (MA2-112–114). |
| Today’s Plan | `PlanScreens.kt` | Defective. Generic task labels, catalog-order audio. |
| Practice library + LRC session | `PracticeScreens.kt` | LRC engine reusable; uncurated library excluded (MA2-073). |
| Gates / gate detail | `GateScreens.kt` | Evidence model reusable; rules must become curriculum gates. |
| Progress / log / export | `ProgressScreens.kt` | Export and factual activity reusable; labels must match MA2-082–090. |
| Attributions | `AttributionsScreens.kt` | Reusable secondary destination. |
| Theme / insets / 48 dp actions | `CompanionTheme.kt`, `ShellPage.kt` | Reusable; need bottom navigation and stronger hierarchy. |

No Course destination, no Reference destination, no guided lesson player, no Alphabet/Reading/Listening/Pronunciation/Grammar labs as curriculum tools.

### 2.2 Data

- Room **schema v6**: bundled segments, plans, gates, sources, texts, rights, audio, optional drills; user learner state, plan instances, activity logs, recordings, gate evidence/decisions, exports.
- Migrations 1→6 preserve user rows. This migration chain is reusable.
- Seed: `app/src/main/assets/content/bundled-content.json` (`contentVersion` 1).
- `LearnerState` is a selected segment plus onboarding flag. There is no objective, attempt, bookmark, note, tutor-queue, or content-review entity.

### 2.3 Catalog and audio

- `catalog/prepare_shtooka_catalog.py` matches the top 1,000 OpenRussian frequency headwords to Shtooka OGG.
- Licenses recorded: CC BY 3.0 US, CC BY 2.0 FR, public domain, plus CC BY-SA 4.0 share-alike on catalog arrangement.
- `bind_beginner_pathway()` assigns listen plan *n* the *n*th packaged audio ID. Lesson 1 receives `я`. That is catalog order, not Penguin Lesson 1.
- No per-item curriculum-objective link, no linguistic review record, no Penguin mapping approval.

### 2.4 Tests

Forty-nine JVM/Robolectric files. Many **enforce superseded v1 behavior**: 30-button lesson selection, catalog-order listen bindings, structural pathway as “complete,” Home as the primary hub. Safety tests that remain binding: no `INTERNET`, offline seed, share-alike packaging, export, appearance persistence, audio-focus, release-gate attachment, fail-closed `assembleRelease`.

### 2.5 Release and candidates

| Record | State |
|---|---|
| Public release | `NOT_APPROVED` |
| v1.1.1 | `NOT_FROZEN`, artifact `NOT_RECORDED` |
| Frozen v1.1.0 | Preserved failed candidate, SHA `33a57f94…`, run `35765579094` |
| Frozen v1.0.0 | Historical, SHA `dd873f42…`, run `35754785768` |
| 0.0.0 | Historical |
| AT-001–AT-031 device procedures | Outstanding / `MISSING` |
| `assembleRelease` | Fail-closed |

---

## 3. MA2 requirement map (summary)

| Cluster | Result on current main |
|---|---|
| MA2-001–008 identity / reset | Fail. Product is a logger/checklist. v1.1.x must not be treated as accepted. |
| MA2-009–017 offline / privacy | Pass structurally (no INTERNET, local store, no AI). |
| MA2-018–030 curriculum spine | Fail. Numbered containers + catalog order. |
| MA2-031–038 Penguin integrity | Partial. Book work is not copied, but mappings are not independently written or reviewer-approved. |
| MA2-039–050 instructional experience | Fail. No guided player, no remediation path, no bookmarks/notes queue. |
| MA2-051–066 practice tools | Fail as curriculum tools. LRC hardware exists; it is bound to unrelated words. |
| MA2-067–073 reference | Fail. No reference library. |
| MA2-074–081 Anki/binder/tutor | Partial. Self-attestation labels exist; prompts are generic. |
| MA2-082–091 evidence / claims | Partial. Activity vs proficiency copy exists; objective states do not. |
| MA2-092–103 content authority | Fail for instructional Russian. Catalog items have rights but not review, placement, or one-to-one pedagogical match. |
| MA2-104–111 rights / attribution | Pass for packaged catalog assets. |
| MA2-112–121 IA / usability | Fail. No five-destination compact nav. |
| MA2-122–131 a11y / visual | Partial after v1.1.1 (dark default, insets, 48 dp). Missing bottom nav, reference, large-font primary destinations. |
| MA2-132–139 backup / restore | Partial. Export exists; restore/preview/snapshot does not. |
| MA2-140–147 technical quality | Partial. IO off main thread in places; schema is v1. |
| MA2-148–153 completeness gate | Fail. Placeholders and unrelated audio currently satisfy the old pathway check. |
| MA2-154–164 feature / build governance | Fail. Structural 30-shell was admitted as curriculum. |
| MA2-165–174 acceptance | Fail. No device evidence. Public release correctly blocked. |

---

## 4. Classification

### Reusable

- Offline boundary, no `INTERNET`, no runtime AI.
- Room + migration discipline; user recordings, logs, and exports.
- Dark-default Material 3 theme, safe insets, 48 dp actions.
- Licensed catalog **as a rights-bearing archive**, not as a lesson sequence.
- Listen-record-compare playback/record/A–B/audio-focus (unbound from catalog-order plans).
- Attribution inventory and packaged license texts.
- Local export engine and backup-status reporting.
- Release-gate attachment to every release-producing Gradle task.
- Historical candidate records.

### Defective — replace

- 30-item lesson wall and “confirm lesson” as first-run.
- Home as a diagnostic dashboard.
- Generic BOOK / ANKI / LISTEN plans as the curriculum.
- Catalog-order audio bindings.
- Structural pathway treated as release-complete.
- Gates that ignore instruction, checks, and cumulative review.
- Practice library as an uncurated word browser.
- Tests that freeze any of the above as required behavior.

### Obsolete v1 behavior

- MA-005 / MA-006 (no original item bank; no curriculum reviewer) are **superseded**.
- “The app is only a conductor” is **superseded**.
- Frequency rank as lesson sequence is **forbidden** (MA2-026, MA2-102).
- v1.1.0 / v1.1.1 must be labeled pedagogically rejected (MA2-006).

### Content lacking provenance for v2 instruction

- Every frequency-catalog word lacks a curriculum-objective link and a qualified review record.
- No licensed letter-name audio pack is installed.
- No verified handwriting assets are installed.
- No reviewer-approved Penguin edition mapping exists.
- Therefore Units 1–2 may ship English instruction, public-domain letter forms, and honest unavailable states. They may **not** ship invented example words or arbitrary catalog audio as lesson practice.

### Nonfunctional / misleading controls

- Lesson rows that only set invisible selection until a distant Confirm (fixed in chrome, still the wrong model).
- Continue that leads into catalog-order listen items.
- Unit numbers that imply Penguin alignment without an approved mapping.
- Release gate that can become APPROVED from a structural 30-shell plus retained-evidence fixtures.

---

## 5. Binding decisions recorded for this cycle

1. **Today** is the compact-width primary destination name (MA2-112). The user-facing “Home” request is implemented as Today.
2. Units 3–30 exist in the map and are labeled **Not yet installed**. They have no invented lesson bodies.
3. Catalog audio remains packaged for rights continuity and is **not** assigned to Units 1–2.
4. Linguistic review is **unavailable** in this cycle. No review record is fabricated. Russian example words are not generated. Letter forms are admitted as public-domain writing-system facts with independently written English notes marked `PENDING_QUALIFIED_REVIEW`.
5. Penguin mappings for Units 1–2 are independently written topic-level directions. Mapping approval is `PENDING_CURRICULUM_REVIEWER` and blocks public release.
6. Identity increments to `2.0.0-dev` / `versionCode` 5. Not frozen. Public release remains `NOT_APPROVED`.
7. Restore-from-backup (MA2-134–138) is specified and scaffolded; a complete restore preview is a later phase if it cannot be finished without inventing a new backup format mid-cycle. Export and fail-closed restore absence remain truthful.
8. Old documents are retained and marked superseded. They are not deleted.

---

## 6. Blockers that remain after this vertical slice

These are expected and must stay visible:

- Qualified Russian Curriculum Reviewer has not signed Units 1–2.
- Penguin edition comparison has not been performed against the owner’s copy.
- Licensed letter/example audio for Units 1–2 is not installed.
- Verified handwriting assets are not installed.
- Units 3–30 are not installed.
- Restore preview/snapshot is not a complete v2 implementation.
- No physical Galaxy S26 Ultra acceptance evidence.
- Public release `NOT_APPROVED`.
