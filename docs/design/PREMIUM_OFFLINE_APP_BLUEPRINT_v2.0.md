# PREMIUM OFFLINE APP BLUEPRINT v2.0
## Russian Companion — Curriculum-First Reset

**Status:** Product and build blueprint  
**Audience:** Product owner, curriculum reviewer, designer, and Cursor implementation agent  
**Authority:** Subordinate to `MASTER_AUTHORITY_v2.0.md`  
**Evidence:** `RESEARCH_FOUNDATION_v2.0.md`

---

## 1. Product promise

Russian Companion is a premium, fully offline Android course companion for an adult beginning Russian from zero while using *The New Penguin Russian Course*, AnkiDroid, a study binder, and optional tutor support.

The app is the **operational backbone** of the learning system. It tells the learner exactly what to learn next, teaches prerequisite foundations, provides licensed and reviewed practice, directs work in the learner's own book, records evidence, exposes concise references, and preserves progress locally.

It is not a thin tracker, an unrelated audio catalog, a digital copy of Penguin, a chatbot, or an automated language judge.

---

## 2. Non-negotiable product outcomes

The learner must always be able to answer:

1. What am I learning now?
2. Why does it matter?
3. What do I do next?
4. Where is the explanation?
5. How do I practice it?
6. What must I repeat if I struggle?
7. What counts as completing this step?
8. Where does the book, AnkiDroid, binder, or tutor enter?

If a screen does not support one of these answers or a required system function, it is probably clutter.

---

## 3. Primary navigation

The compact-phone application uses five destinations:

1. **Today** — the single guided session and Continue action.
2. **Course** — the Penguin-aligned Lessons 1–30 pathway and cumulative reviews.
3. **Practice** — alphabet, listening, reading, pronunciation, grammar, vocabulary, and mixed review tools.
4. **Reference** — searchable offline alphabet, pronunciation, grammar, vocabulary, and study guidance.
5. **Progress** — curriculum state, activity, recordings, notes, backup/restore, and settings.

Use a Material 3 bottom navigation bar on compact width. On larger widths use adaptive navigation rail/list-detail layouts. Do not expose attributions, storage diagnostics, appearance, release identity, or technical status as primary Home actions.

---

## 4. First-launch experience

### Screen 1 — Welcome

- One-paragraph product purpose
- “Fully offline” and “your data stays local” reassurance
- Primary action: Begin setup
- Secondary action: Licenses and attributions

### Screen 2 — Study system

The learner selects which companions are available:

- Owned Penguin book
- AnkiDroid
- Physical/digital binder
- Tutor now or later

No account is required. The selection changes guidance, not curriculum correctness.

### Screen 3 — Starting point

- “New to Cyrillic” is the default and recommended path.
- “I can already read Cyrillic” launches a diagnostic.
- “Resume from book lesson” requires a diagnostic plus explicit confirmation.

### Screen 4 — Daily commitment

- 20, 35, 50, or custom minutes
- Preferred study days
- No streak pressure or punitive language

### Screen 5 — Ready

- Shows Penguin-aligned Unit 1: Learning to Read Russian
- Explains what the learner will do
- Primary action: Start Unit 1

---

## 5. Curriculum architecture

### 5.1 Controlling course spine

The Course contains exactly thirty primary units corresponding in order to Penguin Lessons 1–30. Unit 1 begins with Cyrillic reading, alphabet, pronunciation, and transliteration. Unit 2 continues with handwriting recognition and pronunciation. There is no competing generic foundation track and no independent grammar order.

The normative topic sequence is the thirty-unit schedule in `MASTER_AUTHORITY_v2.0.md`. Each unit may contain multiple short sessions, but no new topic from a later Penguin lesson may be taught early. Prerequisite repair may revisit an earlier topic; it may not preview a later one.

Every unit contains:

- Neutral lesson identifier
- Independently written objectives
- Prerequisites
- “Before the book” micro-lesson when needed
- Direction to read specific user-owned pages or the user-entered page range
- Direction to complete specific user-entered or verified exercise references
- App practice built from separate licensed/reviewed material
- AnkiDroid assignment or self-attested review
- Binder note prompt
- Optional tutor prompt
- Cumulative check
- Remediation route
- Completion summary

No unit may contain a random word or audio assignment. Every item must trace to an objective in that unit and may use only language structures already introduced by that unit or an earlier one, except for fixed expressions explicitly approved as unanalyzed chunks.

### 5.2 Consolidation

Insert cumulative reviews after appropriate clusters rather than waiting for the end. Reviews must mix prior reading, listening, vocabulary, and grammar. The final consolidation module produces a continuation plan but no proficiency certification.

---

## 6. Guided lesson player

Each session is a step-by-step player, not a long scrolling document.

Required step types:

1. **Orient** — objective, estimated time, prerequisite warning.
2. **Teach** — concise explanation with expandable detail.
3. **See and hear** — reviewed examples with native audio where licensed.
4. **Guided practice** — scaffolded closed-response items.
5. **Book work** — exact direction to the learner's copy without reproduction.
6. **AnkiDroid work** — deck/tag instruction and self-attested completion; optional outbound launch only.
7. **Speak** — listen, record, A/B compare, shadow, and optionally mark for tutor.
8. **Binder note** — concise prompt for a rule, example, or error pattern.
9. **Check** — reviewed closed-response items sampling the objective.
10. **Reflect** — confidence and difficulty self-report, never treated as proficiency.
11. **Next** — remediation, review, or advancement.

The player saves after every step and resumes exactly where interrupted.

---

## 7. Practice tools

### 7.1 Alphabet Lab

- Letter browser grouped by learning sequence
- Uppercase/lowercase/italic recognition
- Licensed native letter and example audio
- Sound and visual-confusion notes
- Recognition, audio-to-letter, letter-to-sound, and timed-but-unpressured review
- Optional handwriting/cursive reference; no handwriting scoring

### 7.2 Reading Lab

- Syllable blending
- Stress-marked word reading
- Phrase reading
- Revealable transliteration only during earliest foundations; disabled by default afterward
- Model audio and self-recording

### 7.3 Listening Lab

- Listen and choose
- Listen and order
- Controlled dictation from reviewed answer keys
- Replay count and slower model playback
- Transcript reveal after submission

### 7.4 Pronunciation Lab

- Native model playback
- 0.75× and 1.0× model speeds
- Phrase looping
- Record/replace take
- Model→user and user→model A/B
- Guided shadowing mode
- Stress and articulation cue from reviewed content
- Mark for tutor review
- No ASR, transcription, accent score, or automated judgment

### 7.5 Grammar Lab

- Rule cards linked to reference
- Form identification
- Inflection selection
- Sentence ordering
- Gap fill
- Case-function recognition
- Conjugation and tense practice
- Aspect/motion selection only after those concepts are taught
- Explanatory feedback for every option

### 7.6 Vocabulary Lab

- Lesson-filtered word lists
- Meaning recognition
- Form recognition
- Audio recognition
- Example retrieval
- “Send to AnkiDroid” is not permitted without a reviewed integration design; instead provide exportable local TSV/APKG preparation only if separately authorized
- App does not duplicate long-term SRS in the initial reset release

### 7.7 Mixed Review

- Uses only previously taught content
- Interleaves skills
- Prioritizes recent errors and due curriculum reviews using a transparent local rule
- Does not silently become an SRS clone

---

## 8. Reference library

Reference is searchable and cross-linked from every lesson and correction.

Required sections:

- Cyrillic chart
- Pronunciation and stress
- Hard/soft consonants
- Spelling rules
- Grammar terminology in plain English
- Noun gender and number
- Six-case overview and case-by-case guides
- Pronouns
- Adjectives and agreement
- Present, past, and future tense
- Conjugation patterns and reviewed exceptions
- Reflexive verbs
- Aspect
- Verbs of motion
- Numbers, dates, and time
- Questions, negation, and word order
- Common prepositions with governed cases
- Russian keyboard setup
- Study method and troubleshooting
- Content source and attribution per entry

Reference pages use layered disclosure: one-screen summary, examples, common errors, then deeper detail.

---

## 9. Today screen

The Today screen is deliberately sparse:

1. Current track/module and lesson objective
2. One large Continue button
3. Today's step list with time estimates and completion states
4. “Quick review” when genuinely due
5. Compact access to notes or resume recording

Do not place appearance controls, build hashes, storage counts, licensing inventories, or seven diagnostic summaries on Today.

---

## 10. Mastery and progress model

Use three separate concepts:

### Activity

What the learner did: time, completion, attempts, recordings, book/Anki self-attestations.

### Curriculum evidence

Closed-response checks derived from reviewed answer keys. Record accuracy by objective, error type, attempt, and date.

### Proficiency

Not determined by the app. CEFR/ACTFL/ILR claims require appropriate external assessment.

Objective states:

- Not started
- Learning
- Review required
- Demonstrated in app checks
- Completed by self-attestation only

Never label an objective “mastered” solely from one quiz. The product title may use “mastery pathway” descriptively, but the UI must use the evidence labels above.

Advancement rules are transparent. Foundation advancement requires reviewed recognition/reading thresholds across more than one check. Main lesson advancement requires required instruction, book work self-attestation, assigned app checks, and any scheduled cumulative review. Tutor sign-off is optional unless the user deliberately adds it.

---

## 11. Remediation behavior

When the learner struggles, the app must provide a specific route:

- Identify the objective involved
- Open the exact explanation
- Replay relevant examples
- Offer a smaller guided set
- Repeat with new reviewed items
- Recommend a binder note
- Allow marking for tutor help

The app must never respond with only “incorrect,” “try again,” or an unexplained locked gate.

---

## 12. Notes, bookmarks, and binder coordination

- Lesson notes are local free text and never scored.
- A learner can bookmark any explanation, example, or error review.
- Binder prompts are short and purposeful: rule summary, one model example, one personal error, and one question.
- The app maintains a “Questions for tutor” queue containing references and optional recordings.
- User-entered Russian is never presented later as verified instructional content.

---

## 13. Backup and recovery

The reset must support both export and restore.

- Export curriculum state, activity, check evidence, settings, notes, bookmarks, tutor queue, and recordings.
- Use a versioned manifest plus JSON/CSV and original audio files in a ZIP.
- Restore only through user-selected local files.
- Validate schema, hashes, application identity, and conflicts before import.
- Show a preview and require confirmation.
- Never overwrite the current database before a recoverable pre-import snapshot exists.
- No cloud, account, synchronization, or network permission.

---

## 14. Content data model

Every instructional unit and item requires stable IDs.

Core entities:

- Track
- Module
- Objective
- LessonMapping
- InstructionBlock
- Example
- ExerciseItem
- AnswerOption
- FeedbackBlock
- AudioAsset
- ReferenceEntry
- ReviewRule
- CurriculumGate
- ContentSource
- RightsRecord
- ReviewRecord
- UserProgress
- Attempt
- ErrorRecord
- Recording
- Note
- Bookmark
- TutorQueueItem
- BackupRecord

Every content entity that contains Russian must link to:

- Source/provenance
- License and attribution
- Modification status
- Qualified review record
- Curriculum objective
- Difficulty/stage
- Version

---

## 15. Content pipeline

1. Acquire from the rights holder or an approved public-domain/open source.
2. Record original file hash and acquisition evidence.
3. Separate source layers and licenses.
4. Prepare deterministic normalized data.
5. Draft or adapt only under a compatible license.
6. Conduct linguistic and pedagogical review.
7. Correct and re-review affected items.
8. Freeze a versioned content pack.
9. Run automated referential-integrity, rights, audio, answer-key, and orphan checks.
10. Conduct sample-based human inspection plus complete inspection of high-risk material.
11. Package locally.
12. Block release if any required item lacks rights or review.

Catalog size is not a quality metric. One thousand unrelated words are inferior to fifty well-sequenced, fully explained, reviewed items.

---

## 16. Visual and interaction system

- Material 3, dark by default, Follow system optional
- Minimum 48 dp interactive targets
- WCAG AA text contrast target: 4.5:1 for normal text and 3:1 for large text
- Safe drawing insets and no system-bar overlap
- One dominant action per screen
- Cards used for meaningful grouping, not decoration
- No long walls of identical full-width buttons
- Lazy lists for long collections
- Persistent lesson-player footer for Back/Continue
- Visible selected, disabled, loading, saved, and error states
- TalkBack names, roles, states, and logical focus order
- Font scaling through at least 200% without hidden actions
- Color never carries state alone
- Large-phone screenshots and physical testing before freeze
- Loading work off the main thread

---

## 17. Technical direction

- Native Android, Kotlin, Jetpack Compose
- Unidirectional state and lifecycle-aware ViewModels
- Presentation/domain/data separation
- Room as canonical local source of truth
- DataStore for lightweight preferences
- Bundled, versioned content database/assets
- Media3 for local audio playback where appropriate
- Local recording with explicit lifecycle/audio-focus management
- No networking library and no INTERNET permission
- No runtime AI model
- Deterministic content and gate engines
- Versioned non-destructive migrations
- Instrumented UI, accessibility, migration, corruption, and offline tests
- Release gate attached to every release-producing task

---

## 18. Anti-bloat rule

A proposed feature enters the product only if it directly improves one of:

- comprehension
- practice quality
- study guidance
- retrieval/review
- reference access
- progress interpretation
- data preservation
- accessibility

Exclude social feeds, leaderboards, avatars, decorative gamification, AI chat, open-ended media browsing, tutor marketplaces, cloud accounts, notifications unrelated to user-selected study reminders, and dashboards that duplicate information without changing an action.

---

## 19. Build sequence for Cursor

Cursor must stop after each phase for independent review. Later phases may not compensate for incomplete earlier work.

### Phase 0 — Repository reset and governance

- Archive failed candidates and mark them pedagogically rejected.
- Install v2 documents.
- Remove release eligibility from the old 30-shell pathway.
- Add content completeness blockers.

### Phase 1 — Design system and navigation prototype

- Build five-destination shell with synthetic English-only fixture data.
- Produce Galaxy S26 Ultra screenshots.
- Pass accessibility and navigation review before curriculum UI.

### Phase 2 — Content schema and pipeline

- Implement stable entities, rights/review records, pack validator, and seed pipeline.
- Use non-instructional fixtures until an approved content batch exists.

### Phase 3 — Penguin Units 1–2 foundation

- Install reviewed Unit 1 and Unit 2 content aligned to Penguin's literacy and pronunciation sequence.
- Build Alphabet, Reading, Listening, Pronunciation, handwriting-recognition, and check interactions required by those units.
- Device-test the complete Unit 1–2 foundation cycle alongside the user's book.

### Phase 4 — Today and lesson player

- Implement resumable guided sessions, exact next action, remediation, notes, and bookmarks.

### Phase 5 — Remaining Penguin Companion mapping

- Install reviewer-approved mappings and book directions for Units 3–30 in exact published topic order.
- Never create mappings by catalog order, frequency rank, or lesson number arithmetic.

### Phase 6 — Practice and reference completion

- Add only tools needed by installed objectives.
- Complete searchable reference and cross-links.

### Phase 7 — Progress, gates, backup and restore

- Separate activity from demonstrated check performance.
- Implement transparent gates, export, validation, preview, and restore.

### Phase 8 — Content and usability audit

- Complete content inventory and linguistic sign-off.
- Test first-run through multiple lessons on the Galaxy S26 Ultra.
- Conduct TalkBack, font-scale, airplane-mode, restart, reboot, storage, corruption, and recovery tests.

### Phase 9 — Candidate freeze

- Freeze only after all required content and acceptance evidence pass.
- A green unit-test suite cannot override failed physical usability or missing curriculum.

---

## 20. Definition of a complete lesson

A lesson is not complete unless all of the following exist and pass validation:

- Objective and prerequisites
- Teach content
- At least two reviewed examples per taught concept where pedagogically appropriate
- Guided practice
- Independent check
- Explanatory answer feedback
- Book direction when mapped
- Anki/binder/tutor direction when applicable
- Audio where the objective requires listening/pronunciation
- Remediation path
- Cumulative-review tags
- Rights record
- Qualified review record
- Device-readable UI

Placeholder text, empty arrays, generic “do book work,” and unrelated audio do not satisfy completeness.

---

## 21. Success criteria

The reset succeeds when a new learner can install the app in airplane mode and, without a separate setup guide:

1. Learn Cyrillic through structured instruction and practice.
2. Understand exactly how to use the book, AnkiDroid, binder, and optional tutor.
3. Complete a coherent daily session.
4. Find explanations and references immediately after an error.
5. Practice reading, listening, pronunciation, vocabulary, and grammar with verified materials.
6. Resume after interruption without losing position.
7. Understand what progress evidence means and does not mean.
8. Export and restore all personal learning data locally.
9. Navigate comfortably on the Galaxy S26 Ultra in dark mode with TalkBack and large text.
