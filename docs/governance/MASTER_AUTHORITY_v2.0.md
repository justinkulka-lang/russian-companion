# MASTER AUTHORITY v2.0
## Premium Offline Russian Self-Teaching Companion

**Status:** Replacement governing requirements for owner ratification  
**Supersedes:** `MASTER_AUTHORITY_v1.0.md` and its subordinate product/design assumptions upon ratification  
**Evidence:** `RESEARCH_FOUNDATION_v2.0.md`  
**Product definition:** `PREMIUM_OFFLINE_APP_BLUEPRINT_v2.0.md`

This authority governs outcomes. Cursor shall not reinterpret missing curriculum as permission to create placeholders, random bindings, generated Russian, or structural substitutes.

`MASTER_AUTHORITY_v2.0_AMENDMENT_1.md` controls where it conflicts with the review-chain clauses named in that amendment. It does not replace this document.

---

## 1. Product identity and release reset

**MA2-001.** The app shall function as a structured self-teaching Russian course companion for an adult beginning from zero.

**MA2-002.** The app shall coordinate the learner's owned copy of *The New Penguin Russian Course*, AnkiDroid, study binder, app-native verified practice, and optional tutor work.

**MA2-003.** The app shall make the learner's next appropriate learning action unambiguous without requiring a separate setup guide.

**MA2-004.** The app shall teach prerequisite foundations rather than assuming the learner can already read Russian.

**MA2-005.** The app shall not represent numbered containers, generic task labels, or catalog-order bindings as a curriculum.

**MA2-006.** The app shall not represent the failed v1.1.0 or v1.1.1 product line as pedagogically accepted.

**MA2-007.** Public release shall remain blocked until the complete v2 curriculum, functionality, rights, review, and physical-device evidence required by this authority are retained.

**MA2-008.** A green automated build shall not override a missing-content, failed-usability, failed-rights, failed-review, or failed-device blocker.

## 2. Offline and privacy boundary

**MA2-009.** Every learner-facing capability shall operate in airplane mode after installation.

**MA2-010.** The app shall contain no runtime network dependency.

**MA2-011.** The production manifest shall not request INTERNET permission.

**MA2-012.** The app shall require no account, sign-in, subscription check, server, cloud service, or remote time source.

**MA2-013.** The app shall not download, stream, or remotely validate instructional content at runtime.

**MA2-014.** The app shall not invoke runtime artificial intelligence.

**MA2-015.** The app shall not send learner data, recordings, activity, diagnostics, or analytics off device.

**MA2-016.** Locally persisted data shall be the canonical source of truth for user state.

**MA2-017.** Required instructional content shall ship locally in a versioned content pack.

## 3. Curriculum spine

**MA2-018.** The required curriculum shall use Penguin Lessons 1–30 as its controlling instructional sequence.

**MA2-019.** The default first-run route shall place a learner who cannot read Cyrillic at Penguin-aligned Unit 1.

**MA2-020.** Penguin-aligned Units 1–2 shall constitute the required literacy and pronunciation foundation rather than a separate competing course.

**MA2-021.** Units 1–2 shall cover the book-aligned alphabet, reading, pronunciation, transliteration, handwriting-recognition, and additional reviewed foundation practice.

**MA2-022.** Each required curriculum module shall declare stable identifiers, objectives, prerequisites, estimated effort, required steps, review tags, and completion rules.

**MA2-023.** Each taught concept shall have reviewed instruction, examples, guided practice, an independent check, explanatory feedback, and remediation.

**MA2-024.** Each Russian exercise item shall trace to a previously taught objective.

**MA2-025.** Each required audio assignment shall trace to the objective it practices.

**MA2-026.** Curriculum ordering shall follow the normative Penguin alignment schedule and shall never be determined by source-file order, frequency rank, identifier order, or lesson-number arithmetic.

**MA2-027.** Cumulative reviews shall sample previously taught objectives across more than one skill where applicable.

**MA2-028.** The pathway shall develop reading, listening, controlled speaking, controlled writing, vocabulary, morphology, syntax, and pragmatic use through the current and previously completed Penguin-aligned units.

**MA2-029.** Grammar and literacy topics shall first appear in the unit assigned by the normative Penguin alignment schedule.

**MA2-030.** Supplemental instruction shall prepare for or reinforce the current Penguin topic without jumping forward to a later unit's new grammar.

### Normative Penguin alignment schedule

The following sequence is binding. Titles are topic-level identifiers derived from the publisher's public table of contents; they do not authorize reproduction of protected book content.

| App unit | Required first-introduction scope |
|---:|---|
| 1 | Learning to read Russian: alphabet, pronunciation, transliteration, and controlled sign reading |
| 2 | Russian handwriting recognition and additional pronunciation |
| 3 | Everyday phrases, naming things, basic grammar, and gender |
| 4 | Verbs, personal pronouns, present tense, conjugation, word stress, and introduction to nouns/cases |
| 5 | Questions, prepositional case, and indeclinable nouns |
| 6 | Possession, going places, accusative case, and personal names |
| 7 | Adjectives, agreement, demonstratives, adjective case forms, and adverbs |
| 8 | Plurals, first spelling rules, existence questions, and buying things |
| 9 | Numbers, genitive singular/plural, quantities, and money |
| 10 | “To have,” expanded genitive functions, absence, genitive prepositions, pronouns, and adjectives |
| 11 | Past tense and reflexive verbs |
| 12 | Future, aspect introduction, dative case, additional spelling rules, and prepositional plural |
| 13 | Aspect in the past, tense use, infinitive aspect, and reported speech |
| 14 | Aspect in the future and impersonal constructions |
| 15 | Requests, imperative, and consolidation of aspect use |
| 16 | Instrumental case, selected demonstratives, and surname declension |
| 17 | Time, date, age, ordinal numbers, months, and years |
| 18 | Comparative, superlative, and relative clauses |
| 19 | Conditional, obligation, and prefixes |
| 20 | Verbs of motion: going, running, and bringing |
| 21 | Further possession and purpose |
| 22 | Number declension, both, collective numerals, and fractions |
| 23 | Time expressions covering when and duration |
| 24 | Negation and placement/use of не |
| 25 | Diminutives, proper names, and politeness |
| 26 | Indefinite pronouns, word order, and letter writing |
| 27 | Participles: types and stress |
| 28 | Verbal adverbs |
| 29 | Bookish style, active participles, punctuation, and short-form adjectives |
| 30 | Abbreviations, names of Russian letters, and particles |

**MA2-175.** A unit may subdivide its assigned scope into smaller sessions without reordering first introduction of the listed topics.

**MA2-176.** A unit may review any earlier topic at any time.

**MA2-177.** A fixed expression containing an untaught form may appear only when the reviewer approves it as an unanalyzed communicative chunk and the app does not teach the later grammar through that expression.

**MA2-178.** The app shall identify any deliberate variance from the publisher's public sequence as a release-blocking conflict until the Product Owner accepts it under Amendment 1, after final curriculum audit and independent verification, with the exact sequence difference, bounded resolution, and versioned digest recorded. Units 27, 29, and 30 remain `PROPOSED_NOT_ACTIVATED` until that acceptance. Green CI does not activate a variance.

## 4. Penguin companion integrity

**MA2-031.** The app shall not reproduce Penguin lesson prose, passages, exercises, answer keys, translations, tables, or distinctive explanatory content.

**MA2-032.** The app shall direct the learner to use a legally owned copy for Penguin work.

**MA2-033.** A Penguin lesson mapping shall identify the owned edition and mapped lesson.

**MA2-034.** A Penguin lesson mapping shall contain independently written objectives and directions.

**MA2-035.** A Penguin lesson mapping shall be accepted by the Product Owner after comparison with the owned edition, as specified in Amendment 1. The final curriculum audit checks internal consistency and recorded scope and does not claim physical possession of the book. Codex verifies signed mapping artifacts and their digests. CI does not approve a mapping. Penguin content never enters Git, the APK, logs, screenshots, or review packets.

**MA2-036.** The app shall not label Russian content or audio as Penguin-aligned without an approved mapping record.

**MA2-037.** User-entered page ranges and exercise numbers shall be stored as user data and shall not be represented as publisher-supplied metadata.

**MA2-038.** Missing Penguin mapping data shall produce a visible unavailable state and shall block release of the affected required lesson.

## 5. Instructional experience

**MA2-039.** Every guided session shall state what the learner will learn and why it belongs at that point in the sequence.

**MA2-040.** Every guided session shall present one visually dominant next action.

**MA2-041.** Every guided session shall save position after each completed step.

**MA2-042.** Every interrupted guided session shall resume at the last incomplete step.

**MA2-043.** A required session shall not rely on an unexplained wall of links, buttons, or resources.

**MA2-044.** Book, AnkiDroid, binder, and tutor tasks shall explain exactly what the learner is expected to do.

**MA2-045.** The app shall provide a specific remediation path after an unsuccessful closed-response check.

**MA2-046.** Remediation shall link to the relevant explanation and reviewed practice rather than merely repeat an error message.

**MA2-047.** The app shall allow the learner to bookmark instructional and reference content.

**MA2-048.** The app shall allow the learner to create local notes linked to a lesson, objective, example, or error.

**MA2-049.** User-authored notes or Russian shall never be presented as verified course content.

**MA2-050.** The app shall maintain a local queue of questions and recordings marked for optional tutor review.

## 6. Practice tools

**MA2-051.** The app shall provide an Alphabet Lab supporting visual recognition and licensed audio practice.

**MA2-052.** The app shall provide a Reading Lab supporting syllable, word, and short-phrase decoding with stress-marked text where pedagogically appropriate.

**MA2-053.** The app shall provide a Listening Lab using reviewed closed-response items and locally packaged audio.

**MA2-054.** The app shall provide a Pronunciation Lab with model playback, local recording, replacement takes, phrase looping, A/B comparison, and guided shadowing.

**MA2-055.** The app shall provide a Grammar Lab using reviewed rules, answer keys, and explanatory feedback.

**MA2-056.** The app shall provide lesson-filtered vocabulary and form practice derived from reviewed content.

**MA2-057.** Mixed review shall use only objectives already taught to the learner.

**MA2-058.** Practice availability shall be filtered by the learner's completed prerequisites.

**MA2-059.** The app shall not score pronunciation, accent, spontaneous speech, or open-ended writing.

**MA2-060.** The app shall not perform automatic speech recognition or transcription.

**MA2-061.** Closed-response scoring shall compare only against a reviewed, versioned answer key.

**MA2-062.** Each incorrect closed response shall expose reviewed explanatory feedback or an explicit reference link.

**MA2-063.** Transliteration shall be limited to the earliest Foundation modules and shall be removable from the learner's view.

**MA2-064.** User recordings shall play at their original speed.

**MA2-065.** Slowed model playback shall not modify the source audio asset.

**MA2-066.** Recording and playback shall stop safely on backgrounding, navigation away, audio-focus loss, or error.

## 7. Reference and resources

**MA2-067.** The app shall provide a searchable offline reference library.

**MA2-068.** The reference library shall cover the alphabet, pronunciation, stress, spelling, grammar terminology, cases, agreement, verbs, aspect, motion, numbers, time, questions, negation, and core prepositions at the installed curriculum level.

**MA2-069.** Each reference entry containing Russian shall have provenance, rights, version, and an evidence record under `QUOTED_OR_ADAPTED_SOURCE` or `ORIGINAL_RESEARCH_SYNTHESIS`, as specified in Amendment 1. A pending final-audit state is not a completed record.

**MA2-070.** Lessons, practice feedback, and error review shall deep-link to applicable reference entries.

**MA2-071.** Reference entries shall present a concise summary before optional deeper detail.

**MA2-072.** The resources area shall include only curated material with a defined instructional purpose.

**MA2-073.** The app shall not expose an uncurated general media browser or arbitrary document dump.

## 8. AnkiDroid, binder, and tutor boundaries

**MA2-074.** AnkiDroid shall remain the default long-term spaced-repetition system for vocabulary and forms in the initial v2 release.

**MA2-075.** The app shall not claim AnkiDroid results were independently verified.

**MA2-076.** AnkiDroid completion recorded in the app shall be clearly labeled as user self-attestation.

**MA2-077.** An optional outbound launch of AnkiDroid shall not read or modify AnkiDroid data.

**MA2-078.** Binder prompts shall identify a specific useful note rather than instructing the learner merely to “take notes.”

**MA2-079.** Tutor participation shall remain optional for default curriculum advancement.

**MA2-080.** Tutor sign-off may be added only as user-selected strictness or review evidence.

**MA2-081.** The app shall not represent itself as replacing live interaction or qualified correction of spontaneous output.

## 9. Evidence, advancement, and claims

**MA2-082.** The app shall distinguish activity completion, self-attested external work, and demonstrated closed-response performance.

**MA2-083.** The app shall label curriculum objective states as Not started, Learning, Review required, Demonstrated in app checks, or Completed by self-attestation only.

**MA2-084.** The app shall not label an objective mastered solely because one check was passed.

**MA2-085.** Advancement rules shall be displayed before the learner attempts the applicable gate.

**MA2-086.** Advancement decisions shall be reproducible from the versioned rule and recorded evidence.

**MA2-087.** A Foundation gate shall require evidence across more than one check where the objective requires durable recognition or reading.

**MA2-088.** A main-path gate shall require completion of its instruction, required book work, required app checks, and due cumulative review.

**MA2-089.** The app shall not infer CEFR, ACTFL, ILR, fluency, professional ability, or general proficiency from curriculum completion.

**MA2-090.** Study time, streaks, confidence ratings, and self-attestation shall not be represented as demonstrated language ability.

**MA2-091.** The app shall not use punitive streak loss, rankings, leaderboards, or coercive motivational claims.

## 10. Content authority and review

**MA2-092.** Every shipped Russian word, phrase, sentence, translation, explanation, exercise, distractor, answer, and feedback block shall have recorded provenance.

**MA2-093.** Every shipped Russian instructional item shall have a compatible redistribution basis.

**MA2-094.** Every shipped Russian instructional item shall have a versioned evidence record for its shipped version, classified as `QUOTED_OR_ADAPTED_SOURCE` or `ORIGINAL_RESEARCH_SYNTHESIS`, and remains unapproved until the Amendment 1 chain reaches `OWNER_ACCEPTED` for that version.

**MA2-095.** Every shipped audio file shall have per-file rights, provenance, file hash, and text-match records.

**MA2-096.** Audio with missing, ambiguous, incompatible, or collection-conflicted rights shall be excluded.

**MA2-097.** Audio shall not be paired to text by filename similarity, frequency rank, or automated guess.

**MA2-098.** Each audio-to-text relation shall be verified as the intended one-to-one instructional match.

**MA2-099.** Cyrillic, phonetics, grammar, examples, translations, answer keys, feedback, lesson placement, and Penguin mappings shall pass the Amendment 1 chain before release: `RESEARCH_VERIFIED`, `IMPLEMENTED_PENDING_FINAL_AUDIT`, `FINAL_CURRICULUM_AUDIT_PASSED`, `INDEPENDENT_AUDIT_VERIFIED`, and `OWNER_ACCEPTED`. No stage is inferred from green CI, a counter, self-attestation, or a model name.

**MA2-100.** Review shall be the bounded, versioned Amendment 1 chain rather than an implied ongoing unfunded obligation or a mandatory outside-professor engagement.

**MA2-101.** A post-review change to Russian content or its answer key shall invalidate the affected review record.

**MA2-102.** Source data shall not be treated as curriculum merely because it is large, frequent, structured, or openly licensed.

**MA2-103.** Catalog size shall not substitute for instructional coverage or quality.

## 11. Rights and attribution

**MA2-104.** Every bundled asset shall have its source, license, attribution, modification status, and share-alike treatment recorded.

**MA2-105.** Rights shall be evaluated at the asset layer rather than inferred from a surrounding website or collection.

**MA2-106.** NoDerivatives material shall not be adapted, excerpt-transformed, or integrated without written legal clearance.

**MA2-107.** NonCommercial material shall not ship without a recorded distribution decision consistent with the intended release model.

**MA2-108.** Share-alike obligations shall be satisfied for all affected distributed data and derivatives before public release.

**MA2-109.** User-facing attribution and license texts shall remain available offline.

**MA2-110.** Commercial scans, community mirrors, and “free download” claims shall not establish redistribution rights.

**MA2-111.** Penguin-protected material shall remain excluded except for authorized neutral references and user-entered data.

## 12. Information architecture and usability

**MA2-112.** Compact-width primary navigation shall contain exactly Today, Course, Practice, Reference, and Progress.

**MA2-113.** Today shall prioritize the current objective, one Continue action, and the day's required steps.

**MA2-114.** Diagnostics, licenses, appearance, storage health, and build identity shall not compete with the primary learning action on Today.

**MA2-115.** A screen shall not present a long undifferentiated column of identical actions when meaningful grouping or hierarchy exists.

**MA2-116.** Long collections shall use lazy rendering and shall preserve scroll position.

**MA2-117.** The interface shall provide explicit loading, empty, unavailable, corrupt, permission-denied, in-progress, saved, and failed states where applicable.

**MA2-118.** Every accepted user action shall produce immediate visible state or navigation feedback.

**MA2-119.** Every asynchronous write shall prevent duplicate submission and report success or failure exactly once.

**MA2-120.** Back navigation shall preserve unsaved work or request confirmation before discarding it.

**MA2-121.** Search shall work locally across installed reference and curriculum metadata.

## 13. Accessibility and visual quality

**MA2-122.** Dark shall be the default appearance and Follow system shall be available.

**MA2-123.** Normal text shall meet a minimum 4.5:1 contrast ratio and large text shall meet a minimum 3:1 contrast ratio.

**MA2-124.** Every interactive touch target shall be at least 48 dp in each dimension.

**MA2-125.** Critical state shall not be communicated by color alone.

**MA2-126.** Every meaningful control and state shall expose an accurate TalkBack name, role, state, and action.

**MA2-127.** Logical focus order shall match visual and instructional order.

**MA2-128.** Required content and actions shall remain usable at 200 percent font scale.

**MA2-129.** Content shall respect status-bar, navigation-bar, cutout, keyboard, and safe-drawing insets.

**MA2-130.** Compact and expanded layouts shall be selected from current window size rather than a device-name assumption.

**MA2-131.** Galaxy S26 Ultra portrait screenshots and physical interaction evidence shall be retained for every primary destination before candidate freeze.

## 14. Persistence, backup, and recovery

**MA2-132.** Curriculum state, attempts, activity, settings, notes, bookmarks, tutor queue, and recording metadata shall persist across restart and reboot.

**MA2-133.** The user shall be able to export all user-created learning data and recordings to a user-selected local file.

**MA2-134.** The user shall be able to restore a compatible local backup without network access.

**MA2-135.** Restore shall validate schema, hashes, version compatibility, and required files before mutation.

**MA2-136.** Restore shall show a preview of additions, replacements, omissions, and conflicts before confirmation.

**MA2-137.** Restore shall create a recoverable pre-import snapshot before replacing current data.

**MA2-138.** A failed or cancelled restore shall leave the pre-restore user state usable.

**MA2-139.** The app shall not claim data is protected unless a successful local export exists.

## 15. Performance and technical quality

**MA2-140.** Database, file, hash, export, restore, content validation, and large-list work shall execute off the main thread.

**MA2-141.** The UI shall remain responsive while audio, persistence, export, restore, or validation work is active.

**MA2-142.** The local database shall be the single source of truth for mutable learner data.

**MA2-143.** Database migrations shall preserve user data and shall have migration tests from every supported production schema.

**MA2-144.** Content-pack migrations shall be deterministic and shall preserve references to completed objectives or explicitly report an unmappable state.

**MA2-145.** Missing or corrupt bundled content shall fail closed with a specific learner-readable state.

**MA2-146.** Recording and export streams shall avoid loading complete large files into memory.

**MA2-147.** Release-producing Gradle tasks shall depend on the governed release gate.

## 16. Content completeness gate

**MA2-148.** A required module shall be release-eligible only when all required objectives, instruction, examples, practice, checks, feedback, remediation, rights, and review records exist.

**MA2-149.** A required mapped lesson shall be release-eligible only when its Penguin mapping and edition record are approved.

**MA2-150.** Placeholder text, empty content arrays, generic task labels, and unrelated audio shall not satisfy content completeness.

**MA2-151.** Orphaned content, unreachable required content, broken reference links, missing answer keys, and missing audio files shall block release.

**MA2-152.** Automated validators shall check identifiers, prerequisites, reachability, rights, review versions, answer keys, audio files, text matches, and curriculum-objective links.

**MA2-153.** Automated structural validation shall not substitute for the Amendment 1 review chain or physical usability testing.

## 17. Feature admission and anti-bloat

**MA2-154.** A proposed feature shall directly improve comprehension, practice, guidance, retrieval, reference access, progress interpretation, preservation, or accessibility.

**MA2-155.** A proposed feature shall operate entirely offline on locally available content or user data.

**MA2-156.** A proposed content feature shall identify its content source, rights, review authority, and curriculum purpose before implementation.

**MA2-157.** Social feeds, leaderboards, avatars, decorative economies, advertisements, cloud accounts, AI chat, tutor marketplaces, and uncurated media browsing shall remain excluded.

**MA2-158.** A feature shall be rejected when its instructional value is merely speculative or duplicates an existing tool without improving the learner's next action.

## 18. Build governance

**MA2-159.** Cursor shall implement the product in the blueprint phase order.

**MA2-160.** Cursor shall stop after each authorized phase for independent review.

**MA2-161.** Cursor shall not advance to curriculum UI using fake Russian, generated Russian, random catalog content, or misleading placeholders.

**MA2-162.** English-only synthetic fixtures may be used for non-release UI mechanics when they are clearly marked test data and cannot enter the packaged curriculum.

**MA2-163.** Cursor shall report incomplete content, unresolved rights, absent review, and failed usability as blockers rather than silently narrowing the requirement.

**MA2-164.** Cursor shall not freeze an APK until the authority's required acceptance evidence exists for the exact source commit and artifact hash.

## 19. Acceptance and release

**MA2-165.** Acceptance shall include a complete first-run Foundation session in airplane mode on the Galaxy S26 Ultra.

**MA2-166.** Acceptance shall include a complete Penguin Companion session using the learner's owned book without reproducing protected content.

**MA2-167.** Acceptance shall include Alphabet, Reading, Listening, Pronunciation, Grammar, mixed review, and reference workflows using installed reviewed content.

**MA2-168.** Acceptance shall verify restart and reboot persistence at multiple points within a guided session.

**MA2-169.** Acceptance shall verify local export and successful restore on a clean or reset installation.

**MA2-170.** Acceptance shall verify TalkBack, 200 percent font scale, contrast, touch targets, safe insets, and absence of unreachable controls.

**MA2-171.** Acceptance shall verify every primary workflow without INTERNET permission and in airplane mode.

**MA2-172.** Acceptance shall inspect the content-rights inventory and the Amendment 1 evidence-class and review-chain records for every shipped Russian instructional asset.

**MA2-173.** Acceptance shall verify that no required lesson uses random, merely frequency-ranked, or pedagogically unmapped content.

**MA2-174.** Owner release approval shall occur only after all independent technical, content, rights, accessibility, and physical-device findings are closed.

---

## Authority confirmation

This authority explicitly rejects the failed structural-pathway model. No requirement authorizes generated Russian, runtime AI, network dependence, Penguin reproduction, arbitrary catalog binding, or release based only on automated tests. Linguistic and pedagogical validation remains mandatory before release and is recorded through the Amendment 1 chain. A separate outside professor is not a mandatory role. No stage of that chain is inferred from green CI.
