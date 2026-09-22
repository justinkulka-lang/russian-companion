# MASTER AUTHORITY v1.0
## Offline Russian Learning Companion

**Status:** Governing requirements for v1  
**Normative source:** `PRODUCT_BRIEF_v1.1.md`  
**Evidence-only source:** `RESEARCH_DOSSIER_v1.0.md`

This document governs required product outcomes for v1. It does not prescribe functional design, technical architecture, acceptance criteria, or implementation.

## 1. Product Scope and Role

**MA-001.** The app shall serve an independent adult beginner studying Russian concurrently with *The New Penguin Russian Course*. *(Trace: Product Brief §1)*

**MA-002.** The v1 learning pathway shall remain limited to the Penguin-concurrent beginner arc. *(Trace: Product Brief §§2, 8.1)*

**MA-003.** The app shall function as a daily conductor, listen-record-compare practice companion, advancement gatekeeper, and progress scorekeeper. *(Trace: Product Brief §§1, 3)*

**MA-004.** The app shall be fully usable before the user engages a tutor. *(Trace: Product Brief §§1, 7, 8.5)*

**MA-005.** The app shall not depend on an original Russian item bank. *(Trace: Product Brief §§4, 7, 11)*

**MA-006.** The app shall not depend on a dedicated content author or an ongoing tutor content-validation role. *(Trace: Product Brief §§4, 7, 11)*

**MA-007.** The app shall exclude post-book B1, B2, and C1 curricula from v1. *(Trace: Product Brief §§2, 4, 8.1)*

**MA-008.** The app shall exclude handwriting instruction, an open-ended dictionary, a general media library, social features, gamification systems, and tutor scheduling from v1. *(Trace: Product Brief §4)*

## 2. Offline and Runtime Boundaries

**MA-009.** Every shipped capability shall operate in airplane mode after installation. *(Trace: Product Brief §§5, 9, 10)*

**MA-010.** The app shall have no runtime network dependency. *(Trace: Product Brief §§4, 5, 9)*

**MA-011.** The app shall have no runtime artificial-intelligence dependency. *(Trace: Product Brief §§4, 5, 9)*

**MA-012.** The app shall not invoke runtime artificial intelligence. *(Trace: Product Brief §§4, 5)*

**MA-013.** The app shall not call a remote application programming interface at runtime. *(Trace: Product Brief §5)*

**MA-014.** The app shall not download or stream content at runtime. *(Trace: Product Brief §5)*

**MA-015.** The app shall not require a remote account or sign-in. *(Trace: Product Brief §§4, 5, 10)*

**MA-016.** The app shall not provide cloud backup or cloud synchronization. *(Trace: Product Brief §§4, 5)*

**MA-017.** The app shall not depend on a network time source. *(Trace: Product Brief §5)*

**MA-018.** All product content required by a shipped capability shall be available locally after installation. *(Trace: Product Brief §5)*

**MA-019.** All user data required by a shipped capability shall remain usable locally without network access. *(Trace: Product Brief §5)*

## 3. Russian-Content Authority and Assessment Boundaries

**MA-020.** The app shall not generate Russian words, sentences, explanations, drills, feedback, or study content. *(Trace: Product Brief §§4, 8.4)*

**MA-021.** The app shall not originate Russian content. *(Trace: Product Brief §§1, 6, 7)*

**MA-022.** The app shall not validate the linguistic correctness of Russian content. *(Trace: Product Brief §§1, 7, 10)*

**MA-023.** Any Russian material presented by the app shall be pre-made material from an already-corrected source with documented compatible redistribution rights. *(Trace: Product Brief §§6, 7, 9, 10)*

**MA-024.** The app shall not judge pronunciation or accent quality. *(Trace: Product Brief §§3, 4, 8.3, 10)*

**MA-025.** The app shall not judge free speech or open-ended spoken Russian. *(Trace: Product Brief §4)*

**MA-026.** The app shall not judge free writing or open-ended written Russian. *(Trace: Product Brief §4)*

**MA-027.** The app shall not perform automatic speech recognition or transcription. *(Trace: Product Brief §§4, 8.3)*

**MA-028.** The app shall not represent itself as replacing qualified human correction, conversation, immersion, or a tutor. *(Trace: Product Brief §§1, 4)*

## 4. Content Rights and Release Governance

**MA-029.** Every bundled content asset shall have documented redistribution rights. *(Trace: Product Brief §§6, 9, 10)*

**MA-030.** Every bundled content asset shall have recorded source provenance. *(Trace: Product Brief §§6, 9, 10)*

**MA-031.** Every bundled content asset shall have its applicable license recorded. *(Trace: Product Brief §§6, 7, 9)*

**MA-032.** Every bundled content asset shall have its modification status recorded. *(Trace: Product Brief §6)*

**MA-033.** Every bundled content asset subject to attribution shall have its required attribution recorded and presented with the distributed app. *(Trace: Product Brief §§5, 6, 10, 11)*

**MA-034.** Every bundled content asset subject to share-alike terms shall have the required share-alike treatment recorded. *(Trace: Product Brief §§6, 9)*

**MA-035.** Public release shall remain blocked until the share-alike obligations applicable to distributed derivative data are confirmed. *(Trace: Product Brief §6)*

**MA-036.** Audio with an absent, unclear, or incompatible redistribution license shall be excluded. *(Trace: Product Brief §§6, 7)*

**MA-037.** Required audio-content preparation shall include documented per-file license filtering. *(Trace: Product Brief §§6, 7, 10, 11)*

**MA-038.** Required audio-content preparation shall include documented matching of licensed native audio to verified source-supplied stress-marked text. *(Trace: Product Brief §§6, 7, 10, 11)*

**MA-039.** The distributed app shall include a readable attribution record. *(Trace: Product Brief §§5, 6, 11)*

**MA-040.** The distributed app shall include a readable content-license inventory. *(Trace: Product Brief §6)*

**MA-041.** The app shall not reproduce, scan, summarize, or bundle protected Penguin lesson text or exercises. *(Trace: Product Brief §§3, 4, 6)*

**MA-042.** References to *The New Penguin Russian Course* shall be limited to sequence context, lesson identification, and prescribed use of the user's own copy. *(Trace: Product Brief §§3, 6)*

## 5. Daily Conductor

**MA-043.** The app shall present the user with a lesson-aligned daily study checklist. *(Trace: Product Brief §3.1)*

**MA-044.** The daily study checklist shall cover prescribed work in the user's book. *(Trace: Product Brief §3.1)*

**MA-045.** The daily study checklist shall cover assigned AnkiDroid work. *(Trace: Product Brief §3.1)*

**MA-046.** The daily study checklist shall cover listen-record-compare practice. *(Trace: Product Brief §3.1)*

**MA-047.** The daily study checklist shall support a tutor task when the user has chosen to include one. *(Trace: Product Brief §§3.1, 7)*

**MA-048.** The app shall allow the user to record completion of daily study activities. *(Trace: Product Brief §§1, 3.1, 3.4)*

**MA-049.** The app shall allow the user to identify and begin the day's required work without consulting a separate setup guide. *(Trace: Product Brief §10)*

## 6. Listen-Record-Compare Practice

**MA-050.** The app shall play a properly licensed pre-existing native-audio model. *(Trace: Product Brief §§3.2, 6, 7)*

**MA-051.** The app shall present the verified source-supplied stress-marked text matched to the native-audio model. *(Trace: Product Brief §§6, 7)*

**MA-052.** The app shall allow the user to record an imitation of the native-audio model locally. *(Trace: Product Brief §§3.2, 5)*

**MA-053.** The app shall allow the user to replay the native-audio model and the user's recording for A/B comparison. *(Trace: Product Brief §§3.2, 8.3)*

**MA-054.** The app shall allow the user to mark a recording for later tutor review. *(Trace: Product Brief §3.2)*

**MA-055.** Listen-record-compare practice shall report completion without scoring pronunciation quality. *(Trace: Product Brief §§3.2, 8.3, 10)*

## 7. Advancement Gates and Evidence

**MA-056.** The app shall display the evidence rule governing advancement to the next course segment. *(Trace: Product Brief §§3.3, 8.5, 10)*

**MA-057.** The app shall apply the displayed advancement rule to the evidence recorded for that gate. *(Trace: Product Brief §§3.3, 8.5, 10)*

**MA-058.** The shipped default advancement gates shall be satisfiable without tutor sign-off. *(Trace: Product Brief §§1, 3.3, 7, 8.5)*

**MA-059.** Tutor sign-off shall appear in an advancement gate only when the user adds it as stricter evidence. *(Trace: Product Brief §§3.3, 7, 8.5)*

**MA-060.** The app shall allow the user to record a book-derived revision score as gate evidence. *(Trace: Product Brief §§3.3, 8.5)*

**MA-061.** Book-derived gate evidence shall be labeled as the user's self-attestation. *(Trace: Product Brief §§3.3, 8.5, 10)*

**MA-062.** The app shall allow the user to record AnkiDroid completion or maturity status as gate evidence. *(Trace: Product Brief §§3.3, 8.2, 8.5)*

**MA-063.** AnkiDroid-derived gate evidence shall be labeled as the user's self-attestation. *(Trace: Product Brief §§3.3, 4, 8.2, 8.5, 10)*

**MA-064.** The app shall not represent book-derived or AnkiDroid-derived gate evidence as independently verified. *(Trace: Product Brief §§3.3, 4, 8.2, 8.5, 10)*

**MA-065.** The app shall not independently judge the learner's Russian when determining whether a gate is satisfied. *(Trace: Product Brief §§3.3, 8.5)*

**MA-066.** An advancement decision shall be reproducible from the displayed rule and the recorded evidence. *(Trace: Product Brief §10)*

**MA-067.** A gate shall not use optional curation-only drill activity or results as advancement evidence. *(Trace: Product Brief §§3, 7, 8.5, 11)*

## 8. Progress, Persistence, and Local Export

**MA-068.** The app shall allow the user to record study time and completion. *(Trace: Product Brief §§2, 3.4)*

**MA-069.** The app shall categorize recorded study activity across reading, listening, vocabulary and forms, grammar, speaking and pronunciation, writing, and tutor work. *(Trace: Product Brief §3.4)*

**MA-070.** The app shall present cumulative progress derived from the user's recorded activity. *(Trace: Product Brief §3.4)*

**MA-071.** The app shall present visible skill imbalances derived from the user's recorded activity categories. *(Trace: Product Brief §§2, 3.4)*

**MA-072.** Plans, logs, gate evidence, progress data, and recordings shall persist across app restart. *(Trace: Product Brief §10)*

**MA-073.** Plans, logs, gate evidence, progress data, and recordings shall persist across device reboot. *(Trace: Product Brief §10)*

**MA-074.** The app shall allow the user to initiate a local-file export. *(Trace: Product Brief §§3.4, 5, 10, 11)*

**MA-075.** A local-file export shall include the user's logs. *(Trace: Product Brief §§3.4, 10)*

**MA-076.** A local-file export shall include the user's gate evidence. *(Trace: Product Brief §§3.4, 10)*

**MA-077.** A local-file export shall include the user's progress data. *(Trace: Product Brief §§3.4, 10)*

**MA-078.** A local-file export shall include the user's recordings. *(Trace: Product Brief §§3.4, 10)*

**MA-079.** Exported data shall remain usable as local files that the user can copy elsewhere for recovery or reference after device loss. *(Trace: Product Brief §§3.4, 10)*

## 9. AnkiDroid and Book Boundaries

**MA-080.** Spaced vocabulary and word-form drilling shall remain assigned to AnkiDroid rather than hosted by the app. *(Trace: Product Brief §§3.1, 4, 8.2)*

**MA-081.** Grammar practice shall remain assigned to the user's book and its answer key rather than hosted as an equivalent app-native bank. *(Trace: Product Brief §§3.1, 4, 6)*

**MA-082.** The app shall not include an integrated spaced-repetition scheduler. *(Trace: Product Brief §§4, 8.2)*

**MA-083.** The app shall not duplicate AnkiDroid's spaced-repetition or equivalent vocabulary and word-form drill functions. *(Trace: Product Brief §§4, 8.2)*

**MA-084.** The app shall not integrate with AnkiDroid. *(Trace: Product Brief §§3.3, 4, 8.2, 8.5)*

**MA-085.** The app shall not import arbitrary third-party decks as authoritative content. *(Trace: Product Brief §4)*

**MA-086.** The app shall not accept unverified community content as authoritative content. *(Trace: Product Brief §4)*

## 10. Optional Curation-Only Drills

**MA-087.** Curation-only drills, if present, shall remain optional. *(Trace: Product Brief §§3, 7, 11)*

**MA-088.** Curation-only drills shall not block v1 release. *(Trace: Product Brief §§3, 11)*

**MA-089.** Curation-only drills shall be constructed only from already-corrected, properly licensed structured data. *(Trace: Product Brief §§7, 8.4, 9)*

**MA-090.** Curation-only drill prompts shall be direct presentations or deterministic transformations of fields in the licensed source data. *(Trace: Product Brief §7)*

**MA-091.** Curation-only drill correct responses shall be direct presentations or deterministic transformations of fields in the licensed source data. *(Trace: Product Brief §§7, 8.4, 10)*

**MA-092.** Curation-only drills shall not contain originally authored Russian items, distractors, explanations, or answer keys. *(Trace: Product Brief §§7, 8.4)*

**MA-093.** Curation-only drills shall not require per-item linguistic judgment. *(Trace: Product Brief §§7, 9)*

**MA-094.** Every curation-only drill item shall retain source provenance. *(Trace: Product Brief §§7, 9)*

**MA-095.** Curation-only drills shall be treated as inheriting any errors present in their source data. *(Trace: Product Brief §§7, 10)*

**MA-096.** Curation-only drills shall not be represented as correcting or independently validating their source data. *(Trace: Product Brief §§7, 10)*

**MA-097.** Tutor review of curation-only drills shall be limited to an optional one-time spot-check of a sample batch. *(Trace: Product Brief §7)*

**MA-098.** Curation-only drills shall not create an ongoing tutor-review obligation. *(Trace: Product Brief §7)*

**MA-099.** Original grammar-review items shall be excluded from v1. *(Trace: Product Brief §§4, 7, 8.4)*

**MA-100.** Minimal-pair drills shall be excluded from v1. *(Trace: Product Brief §§4, 7, 8.3, 8.4)*

## 11. Claims and Interpretation

**MA-101.** The app shall not claim to deliver fluency. *(Trace: Product Brief §4)*

**MA-102.** The app shall not claim to deliver ILR 3 proficiency. *(Trace: Product Brief §4)*

**MA-103.** The app shall not claim to deliver C1 proficiency. *(Trace: Product Brief §4)*

**MA-104.** The app shall not claim to deliver professional mastery. *(Trace: Product Brief §4)*

**MA-105.** The app shall not infer language ability from study hours. *(Trace: Product Brief §§4, 10)*

**MA-106.** The app shall not infer language ability from streaks. *(Trace: Product Brief §§4, 10)*

**MA-107.** The app shall not infer language ability from activity completion. *(Trace: Product Brief §§4, 10)*

**MA-108.** The app shall not infer language ability from self-reported results. *(Trace: Product Brief §§4, 10)*

**MA-109.** Activity measures shall be represented as work completed rather than certified proficiency. *(Trace: Product Brief §§3.4, 10)*

## 12. Feature Admission and Release Boundary

**MA-110.** Every proposed v1 feature shall operate completely offline without runtime artificial intelligence or a network dependency. *(Trace: Product Brief §9.1)*

**MA-111.** Every proposed v1 feature shall operate exclusively on pre-made human-verified content or the user's own logged data. *(Trace: Product Brief §9.2)*

**MA-112.** Every proposed v1 feature that presents or scores Russian shall use Russian and correct responses already established by licensed corrected source data without original authoring or per-item linguistic judgment. *(Trace: Product Brief §9.3)*

**MA-113.** Every proposed v1 feature shall use only bundled inputs that are legally redistributable with provenance, attribution, and share-alike duties recorded. *(Trace: Product Brief §9.4)*

**MA-114.** Every proposed v1 feature shall be necessary for one of the four primary capabilities or qualify as an optional curation-only drill under the Product Brief. *(Trace: Product Brief §9.5)*

**MA-115.** Any proposed v1 feature that fails one or more Feature Admission Test conditions shall be rejected from v1. *(Trace: Product Brief §9)*

**MA-116.** A proposed v1 feature shall not be admitted on a dependency on future artificial intelligence, a future network service, an unreviewed corpus, original item authoring, per-item validation, or later content work. *(Trace: Product Brief §9)*

**MA-117.** v1 release shall require completion of all four primary capabilities. *(Trace: Product Brief §11)*

**MA-118.** v1 release shall require preparation of the required licensed native audio and matching metadata. *(Trace: Product Brief §§7, 11)*

**MA-119.** v1 release shall require verified offline operation of every shipped capability. *(Trace: Product Brief §§10, 11)*

**MA-120.** v1 release shall require verified local export of logs, gate evidence, progress data, and recordings. *(Trace: Product Brief §§10, 11)*

**MA-121.** v1 release shall require the required rights records and user-facing attributions. *(Trace: Product Brief §§6, 11)*

**MA-122.** v1 release shall require completion of the full lesson cycle comprising daily planning, book and AnkiDroid work, listening and recording, evidence entry, gate decision, progress review, and local export. *(Trace: Product Brief §§10, 11)*

**MA-123.** Missing required content shall not be substituted with generation, network access, unverified material, original item authoring, or unfunded validation work. *(Trace: Product Brief §11)*

## Authority Confirmation

Every requirement in this Master Authority traces to `PRODUCT_BRIEF_v1.1.md`. No requirement depends on an unfunded content-authoring role.

## Clerical Erratum

**Status:** non-normative correction of identical duplicated lines.

`MA-032` and `MA-078` each appear once in the numbered requirements
above. If any copy of this file contained a second, identical line for
either identifier, that duplicate is a clerical transcription error. It
does not create a second requirement, a conflict, or a change of
scope. The governing text remains the first occurrence of each
identifier:

- **MA-032.** Every bundled content asset shall have its modification status recorded.
- **MA-078.** A local-file export shall include the user's recordings.

