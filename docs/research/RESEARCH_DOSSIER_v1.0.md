# RESEARCH DOSSIER v1.0
## Offline Android application for self-directed Russian acquisition (Penguin-concurrent → advanced)

**Status:** SUPERSEDED historical evidence. Current evidence dossier is `docs/research/RESEARCH_FOUNDATION_v2.0.md`.

Role of this document: EVIDENCE, not authority. Per the project pipeline, this
dossier informs the Product Brief and Master Authority; it does not define
requirements and contains no source code. Recommendations here are proposals for
the product-definition stage to ratify or reject.

Researcher scoping mandate: the requester delegated scope decisions to this
stage. Where a call is made, it is labeled RECOMMENDATION and reasoned.

---

## 0. THE GOVERNING CONSTRAINT (read before anything else)

The architecture specifies **offline, with no runtime AI dependency and no
network**. That single decision determines what this app can and cannot be, and
every feature below is sorted by it:

- **The app can ORIGINATE:** scheduling, sequencing, drilling, delivery, scoring
  against known-correct answers, progress tracking, advancement-gate logic. All
  of this runs on pre-made correct content or on the user's own logged data.
- **The app CANNOT ORIGINATE:** correct Russian. It cannot generate vocabulary,
  sentences, quiz items, grammar explanations, or pronunciation judgments,
  because there is no verified-content engine at runtime. Any Russian the app
  shows must be **correct before it ships**, sourced from a verified corpus or
  authored and human-validated.

This is not a limitation to design around — it is the design. An offline app is
an excellent CONDUCTOR (runs the plan, drills the content, tracks the climb, and
gates advancement) and a poor PROFESSOR (it cannot assess the correctness of the
learner's Russian). The professor's judgment stays human: the answer keys, the
verified corpus, and the tutor. The app's job is to orchestrate those, relentlessly.

Consequence for the "all the way to mastery" goal: see Section 5 (Unsupported
Assumptions). Short version — the app can carry drilling and structure a long
way, but professional mastery (ILR 3) is won through human interaction and
immersion the app can schedule but cannot contain.

---

## 1. ESTABLISHED FACTS

### 1.1 Proficiency framework and time cost (U.S. government model)
- The government scale is the ILR (0–5). "Professional working proficiency" =
  **ILR 3 ≈ CEFR C1.** There is no rung called "fluent."
- Russian is **FSI Category III** ("hard"): ~1,012–1,100 classroom hours to an
  ILR-3 integrated score, on a schedule of ~23 class + ~17 self-study hrs/week —
  so the real immersive total is ~1,760 hours. Self-learners without immersion
  typically need **1.5–2× longer**.
- Implication: this is a multi-year endeavor. The app must be built to sustain
  effort over years, not to "finish."

### 1.2 What the learning science supports (well-established SLA principles)
- **Spaced repetition** is the most efficient known method for durable vocabulary
  and form retention. Any serious vocab feature must be SRS-based.
- **Comprehensible input** (listening/reading slightly above current level) drives
  acquisition; must be present from day one, scaling in difficulty.
- **Output with correction** (speaking/writing that gets corrected) converts
  passive knowledge into production ability. Correction is the load-bearing part —
  uncorrected output drills in errors.
- **Pronunciation:** perception precedes production. Minimal-pair discrimination
  (hearing the difference) is trainable by an app with correct audio; accurate
  production requires modeling native audio and getting corrective feedback.
- **Russian-specific:** stress is unpredictable, mobile, and unmarked in normal
  text; wrong stress is a primary fossilized error. Any word/sentence the app
  shows should carry a stress mark, and audio is not optional.

### 1.3 Content sourcing — what correct Russian can legally be bundled offline
This is the enabling finding. A comprehensive offline app is achievable WITHOUT
reproducing any copyrighted course, because openly licensed Russian corpora exist:

- **OpenRussian dataset** — comprehensive Russian dictionary with **stress marks,
  declensions, and conjugations**, derived from Wiktionary + community correction.
  Licensed **CC BY-SA 4.0**; published as downloadable CSV/DB. This is the single
  most valuable spine for word-level content.
- **Tatoeba** — 13.4M+ example sentences (Russian is among the largest sets),
  **CC-BY 2.0 FR** (some CC0). Over 1M sentences carry audio.
- **Tatoeba audio caveat (critical):** audio license VARIES per recording, and
  "if the license field is empty, you may not reuse the audio outside Tatoeba."
  Audio must be programmatically filtered to only explicitly-licensed files.
- **Shtooka** — free spoken-word Russian audio (used by OpenRussian), suitable
  for word-level pronunciation.
- **Wiktionary** — definitions/grammar attributes, **CC BY-SA 3.0**.

Compliance obligations that attach (manageable, but mandatory): attribution for
all sources, and **share-alike** for CC-BY-SA data (a derivative dataset may
inherit the SA obligation). An ATTRIBUTIONS file and a documented data-license
inventory must ship with the app. Given the requester's background-investigation
sensitivity, clean provenance here is a feature, not overhead.

### 1.4 Offline speech recognition — what's actually possible
- **Vosk** provides a genuine offline, open-source Russian ASR model (~50MB,
  runs on Android). So on-device transcription ("did I say the intended word")
  is technically feasible with no network.
- BUT: open-ended recognition with small models is weak; accuracy varies by
  language and vocabulary. More importantly, **transcription is not pronunciation
  assessment.** Judging how native-like a learner sounds is a distinct, harder
  problem that offline consumer tooling does not solve reliably.
- Conclusion: offline ASR can support constrained "say-this-word/sentence"
  confirmation, but it **cannot be a trustworthy authority on pronunciation
  quality** for a beginner, and must not gate advancement.

---

## 2. STRONG RECOMMENDATIONS (scoping calls made at this stage)

### 2.1 Scope — comprehensive vision, STAGED delivery
- RECOMMENDATION: build the comprehensive app in tiers, not at once.
  - **v1 = Penguin-concurrent companion**, covering the book's arc (~A2). The app
    runs alongside the book from day one — its purpose in v1 is the daily
    structure, drilling, pronunciation, and advancement-gating the book and
    AnkiDroid don't provide.
  - **v2+ = post-book advancement** toward B1→B2, then a defined pathway toward
    C1, added as later phases once v1 is in daily use.
- Reasoning: the pipeline's own guidance is to define the core first and build
  vertically. A staged build is also usable while the user studies, so
  constructing the app never blocks lesson one.

### 2.2 The app's role — conductor, drill sergeant, gatekeeper, scorekeeper
Not professor. Concretely, v1 should own:
- **Daily conductor:** today's prescribed work across all tools ("Lesson 9
  exercises; add these words to SRS; clear reviews; 15 min listening; tutor Thu").
- **Drilling:** SRS vocabulary/forms; minimal-pair perception; grammar-rule review.
- **Advancement gates:** a rubric engine (see 2.6).
- **Progress + skill-balance tracking:** hours vs. the ILR ladder; hours by skill
  so speaking/listening don't silently lag.

### 2.3 Pronunciation — playback-first, defer scoring
- RECOMMENDATION for v1: **model-and-imitate + minimal-pair perception drills +
  record-yourself.** The app plays correct native audio (Shtooka/Tatoeba,
  license-filtered), drills the user's ear on contrasts, and lets the user record
  their own voice to A/B against the model and to bring to the tutor.
- **Defer Vosk/ASR** to a later, optional tier, scoped only to constrained
  word/sentence confirmation — never as a pronunciation-quality authority. The
  tutor remains the pronunciation authority.

### 2.4 Quizzes/assignments — delivered by app, authored by humans
- RECOMMENDATION: the app delivers and scores; it never generates items.
- Item bank must be **human-verified**, sourced from: original items authored
  against public grammar facts, plus example sentences drawn from the licensed
  Tatoeba corpus, validated by the tutor. Do NOT copy Brown's exercises (that
  reproduces the book); author independent items that exercise the same public
  grammar.
- HONEST FLAG: this is the largest non-code cost in the whole project and the most
  likely point of stall. The app's teaching quality equals the quality of the
  item bank put into it. This is ongoing content work, not a one-time task.

### 2.5 SRS — orchestrate, don't rebuild (with one caveat)
- RECOMMENDATION for v1: do not duplicate AnkiDroid's mature engine and shared-deck
  ecosystem; orchestrate alongside it (the app tells you what Anki work is due and
  tracks that you did it).
- CAVEAT / decide-later: if a single unified app is judged essential to the
  "one professor" experience, an integrated SRS is defensible ONLY if it uses a
  proven algorithm (FSRS) rather than an invented one. Treat as a disputed item
  (Section 4), not a v1 default.

### 2.6 Advancement authority — a rubric engine, not an oracle
- RECOMMENDATION: encode advancement as user-defined gates built from the three
  trustworthy signals: Penguin revision-exercise scores (self-entered), SRS
  maturity for the lesson's material, and tutor sign-off. Example gate: "advance
  when revision score ≥ 90% AND lesson vocab mature in SRS AND tutor confirms."
  The app enforces the gate; it does not judge the Russian itself.

### 2.7 Architecture alignment
- The recommendations fit the stated stack (Kotlin / Jetpack Compose / Room /
  DataStore / offline / no network / no runtime AI). Bundle the licensed corpora
  as a seeded Room database; ship the attributions/license inventory with it.

---

## 3. DESIGN POSSIBILITIES (options for the Product Brief to weigh, not decide here)
- Minimal-pair perception trainer (ear training on hard Russian contrasts).
- Shadowing module: play native audio → user records → A/B compare → flag for tutor.
- Cyrillic handwriting/tracing module for Phase 0 (bounded, motor-learning value).
- Spaced grammar-rule review (SRS applied to rules/tables, not just words).
- Error journal that captures recurring mistakes and auto-compiles a tutor agenda.
- Listening library with transcripts + stress-marked text (from licensed corpora).
- ILR self-assessment prompts fired at milestone hour-thresholds.
- Configurable daily plan templates (short day vs. 4–6 hr day) so big days bank
  volume without cramming new grammar.

---

## 4. DISPUTED / UNCERTAIN (must be resolved before or during product definition)
- **Exact per-file audio licenses (Tatoeba)** must be verified and filtered before
  any audio ships; empty-license files are unusable. Needs an engineering pass.
- **Share-alike scope:** confirm how CC BY-SA 4.0 obligations apply to a bundled
  derivative dataset inside a distributed app; may warrant a brief legal check
  before public release (not before personal use).
- **Integrated SRS vs. orchestrate AnkiDroid** (see 2.5) — genuine UX-vs-maintenance
  tradeoff; recommend deciding at Product Brief.
- **Offline ASR feasibility/value** — whether constrained Vosk confirmation earns
  its complexity, or whether record-yourself + tutor is sufficient indefinitely.
- **How far the "advanced tier" can go offline** — beyond ~B1/B2 the dominant
  needs (spontaneous speech, immersion, native interaction) fall outside any
  offline app's reach; the advanced tier is mostly a delivery/curation layer over
  native content, not new app capability.

---

## 5. UNSUPPORTED ASSUMPTIONS TO CORRECT (flagged per pipeline requirement)
- **"An offline app can deliver true mastery."** It cannot. ILR 3 mastery requires
  spontaneous human interaction and immersion. The app is scaffolding toward
  mastery — it drills, structures, and schedules the human work — but the mastery
  itself is won with the tutor and in the language, not in the app. Calibrate the
  goal accordingly.
- **"The app can generate quizzes/content."** No — offline + no runtime AI means
  all content is pre-made and human-verified (Section 0, 2.4).
- **"The app can replace the tutor / judge my speaking."** No — pronunciation and
  production correctness stay human-judged (Section 1.4, 2.3).
- **"Comprehensive means build everything now."** No — comprehensive is the
  destination; staged is the route (Section 2.1).
- **"The book can be the app's content foundation."** No — the book is copyrighted;
  the app's content foundation is the openly licensed corpora (Section 1.3), and
  the book runs concurrently as the user's own owned spine.

---

## 6. HANDOFF TO STAGE 2 (Product Brief) — what the next document should settle
- The 3–5 core things v1 must let the user do (candidates: see today's plan;
  drill vocab/forms; drill pronunciation via native audio; check an advancement
  gate; log/track progress and skill balance).
- What v1 explicitly does NOT do (originate Russian; judge pronunciation; replace
  the tutor; carry the user to C1 alone; duplicate Anki).
- The integrated-SRS-vs-orchestrate decision (2.5).
- The content-authoring plan and owner for the item bank (2.4) — the true critical
  path.

---

## SOURCES (accessed September 2026)
- U.S. Department of State, Foreign Language Training (FSI categories; Cat III
  ~1,012 class hrs to ILR-3; 23+17 hr/week model): state.gov/national-foreign-affairs-training-center/foreign-language-training
- FSI category confirmations (Russian = Category III, ~1,100 hrs): Wikipedia
  "Second-language acquisition"; multiple secondary summaries (Mar–Jul 2026).
- ILR-3 ≈ "professional working proficiency" ≈ upper-B2/low-C1; self-learners
  1.5–2× FSI hours: secondary FSI summaries (Mar 2026).
- Tatoeba corpus + license (CC-BY 2.0 FR; some CC0; audio license varies; empty
  license = no reuse): en.wiki.tatoeba.org/articles/show/using-the-tatoeba-corpus;
  tatoeba.org/en/downloads; en.wikipedia.org/wiki/Tatoeba (Apr 2026 figures).
- OpenRussian dataset license (CC BY-SA 4.0; Wiktionary + Tatoeba + Shtooka
  sources; stress/declensions): github.com/Badestrand/russian-dictionary;
  en.openrussian.org/about; en.openrussian.org/dictionary-data.
- Wiktionary definitions CC BY-SA 3.0: github.com/Vuizur/tatoeba-to-anki.
- Vosk offline ASR (Russian model, ~50MB, Android; accuracy varies; open-ended
  recognition weak with small models): alphacephei.com/vosk/;
  github.com/alphacep/vosk-api; secondary guide (Sep 2024).

END OF DOSSIER v1.0
