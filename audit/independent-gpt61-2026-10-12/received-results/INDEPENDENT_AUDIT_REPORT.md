# Independent diagnostic audit of Russian Companion

Target: `ebd71fd0d1226a5ce3ec9a310bb3638c3bc34fb9`. Instruction commit: `80731e6f17d7ee8b72bd4a25593e0ffc3f0418f0`. Repository: `justinkulka-lang/russian-companion`; branch `cursor/content-audit-corrections-964f`; Draft PR64. Date: 2026-10-12. User-requested label: GPT-6.1, High reasoning; the runtime does not independently expose an exact model/version or reasoning-setting identity, so those are not certified here.

Disposition: **DIAGNOSTIC_ONLY — INCOMPLETE_DIAGNOSTIC_WITH_FINDINGS**. This is a substantive static diagnostic, not a comprehensive acceptance pass. It identifies 18 new findings (11 high, 7 medium); two are explicitly static risks requiring full regression evidence. No critical finding is asserted. The current app remains unfrozen and public release remains NOT_APPROVED. No repository implementation, merge, signing, installation or acceptance action occurred.

## Most consequential results

- Optional labs omit the governed Russian cue (IA001).
- Unversioned completion rows can continue to satisfy prerequisites after governed content changes or unsupported completion claims are restored (IA002).
- Units 3–30 do not require due cumulative review (IA003).
- Units 1–2 lack app-native decoding/model audio/handwriting scope required for the planned product (IA004). These are honest development absences, not hidden production-ready features.
- Unit 5 prints the answers to six question-word checks (IA006).
- Units 20, 29 and 30 have gaps between required grammar capabilities and what their independent questions demonstrate (IA011–IA014). Some keys correctly answer deliberately narrow source-layout questions; they are not automatically mistranslations.
- Remaining mutable synthesis metadata needs full production forgery regressions (IA015); a host word-membership counterexample is insufficient to certify a whole-validator bypass.

## Input integrity and access

Private GitHub access succeeded through the selected GitHub plugin. Both required instruction files were read completely at their pinned commit. The recursive target tree contains 2,476 tree/blob entries and is not truncated. Downloaded target files were independently matched to their pinned Git blob IDs; the exact successful inventory is in AUDIT_SCOPE_AND_EVIDENCE.json. Git CLI read access through the available proxy failed for lack of credentials; no clone or synthetic Git history was created.

The separately attached private PDF exactly matches 13,572,361 bytes and SHA256 `0c441ffd8855b292922a40b10935d5ffb1b088b7118acfcc1856e259dc01867a`. It is a 528-page scanned PDF. Text extraction produced no readable pages. Two pages were rendered and inspected privately: Lesson 1 sections 1.1–1.2, printed p1/PDF13; Lesson 29 heading and sections 29.1–29.2, printed p355/PDF367. **Full Lessons 1–30 owned-edition alignment is not verified.** The outputs contain no private PDF, book text, page image or extracted private notes.

The input audit ZIP was unavailable. **ZIP integrity and ZIP-only projections, packet identity, history exports and comparisons were not inspected.** The preparation receipt is not substituted for archive inspection.

The authoritative pooled curriculum is a 14,967,494-byte Git blob. Connector file reads returned an empty content payload, and full-blob attempts failed with transport/size limitations. Its expected SHA256 `36d6c4b8fc55e0f8ca1d002536a0466006bd908befd57d609c609539ee42210e` was **not independently verified**. All 30 tracked review packets were obtained, including larger packets via blob reads, and every item record was accounted for. They remain derived repository evidence; equivalence with the unavailable raw asset was not established.

## Actual inspection coverage

All 586 unique assessment records were inventoried and their packet cue, choices, key and feedback text screened. ITEM_ASSESSMENT_AUDIT.csv contains exactly one row per record. Rows without independently supported defects are **NOT_VERIFIED**, not presumed passes, because comprehensive semantic/context/source-tuple/teaching-chain inspection and raw equivalence remain incomplete. Fields identify the limited inspection actually performed. No item is given formal linguistic approval.

All 30 units have rows in UNIT_COVERAGE.csv. Objective declarations, packet relationships and assessment inventories were inspected; instructional/example/remediation semantics were targeted rather than complete. The 58 required capability contracts were read from V2Validator; only 21 current capability objects appear in the obtained packet records. The other 37 current objects are explicitly unidentified/unverified, not invented from a naming convention.

All 5,689 exported provenance records passed independent SHA256 checks of originalSourceText and shippedText against their recorded fields. These are **packet-internal byte consistency** checks, not truth, rights, raw-pack identity or linguistic approval. Seven retained external text files were checked for span membership; counts and paths are in SOURCE_AND_BINDING_REVIEW.json. Complete cue/answer/distractor tuples were not uniformly established. Historical preservation of all preceding 5,548 records and deterministic asset replay remains producer evidence.

The 75 retained targets all have independent current-scope dispositions: 72 narrow RESOLVED_DIAGNOSTIC, 2 UNRESOLVED, and 1 NOT_VERIFIED. A resolved old issue does not accept the full item or cancel a new finding. CR070/CR071 retain conflicting gap notices (IA017). CR064's full digest regression was not reproduced. Original historical audit/four original CSVs remain missing. The 56 sequence candidates were read as an inventory; their count is not a defect count and they were not individually comprehensively adjudicated.

## Verification evidence

The GitHub commit comparison independently confirms that `39740d16f8ac078841e9b9c9390e6ac87e6feb7b` to the target consists of one documentation/evidence commit affecting 20 audit paths. The comparison from `0136eb4000641f6b1edd020ed9caa29f59cdc35c` identifies it as the merge-base, two commits behind the target. This is GitHub compare evidence, not a local merge-base command or a complete Git object database.

All ten retained latest test XML files were read, hash-matched to the manifest, and independently summed: **22 tests, zero failures/errors/skips**. The retained lint XML hash matches; it contains **20 warnings, one hint, zero errors**. These are producer execution reports with independently reproduced counts/hashes. **No Android test, compile, lint or gate was executed by this auditor.** No Gradle/Android toolchain or full executable checkout was available. The four screenshot exporters were not run. Source annotation counts are not executed test counts; overlapping focused runs are not added.

The historical main 562-pass/one-skip result, baseline reported 691 execution and later 680-test/31-test repair narrative remain qualified historical producer claims. No fresh full post-repair success is asserted. The latest Unit29/30 tests mostly assert teaching phrases, ordering/remediation and structural validation; they do not independently establish that the unchanged questions demonstrate contextual grammar. Green checks do not resolve IA011–IA014.

The pack digest `ec1debe60310f19952a7d4bf676fe18ff6e25b1dfb848c60761d7cd9d40d7ff1` and repository source digest `9bf60851690b6f1c880a25f39c0afece1b5bf87130b45e9a2fc3411c69191467` were read from current records. Their canonicalization definitions differ from a raw-file SHA256. Full recomputation was not performed. Objective gating is included in the current canonicalizer, but that static observation does not reproduce CR064's historical regression.

## Application, rights and governance conclusions

The main manifest omits INTERNET permission and disables Android automatic backup. The inspected architecture uses Room/local assets and explicit local backup. Schema v9 has registered additive migrations 1→9. Backup validates application/pack/schema identity, hashes, declared files, duplicate records, profile semantics and recording paths before replacement; restore uses a recording snapshot/journal and an epoch fence. These are useful structural protections, not proof of crash/reboot/cancellation/device behavior. IA002 and IA018 identify narrower unresolved evidence-consistency paths; no broad claim of data loss is made.

Runtime practice uses five primary destinations, transparent evidence labels, optional tutor work, and no inspected ASR/proficiency scoring. Listening/Pronunciation/handwriting explicitly report missing assets. Persistence, accessibility, 200% text, audio focus and backup recovery were not physically tested. The merged manifest and current APK were unavailable.

Rights records distinguish original English, pending synthesis, Wikimedia/Pressbooks/ReadyRussian/OpenRussian material and separately licensed Shtooka audio. Offline legal notices are present in inspected files. No corpus or collection license is treated as universal asset clearance. Per-audio rights/text matches and all share-alike obligations were not independently verified. No broad no-Penguin-reproduction finding is possible from a two-page private comparison; marker scans cannot prove that boundary.

The requirements matrix contains all 178 MA2 clauses and 17 controlling amendment groups, including clauses without findings. NOT_VERIFIED means the required evidence was not established, not that the requirement was silently waived. Pending review/mapping/device conditions are recorded as unmet acceptance preconditions. Amendment 1's named chain remains controlling. This diagnostic does not create any formal-stage artifact or signature.

Candidate records directly confirm frozen=false, sourceCommit=null, artifact NOT_RECORDED and publicRelease NOT_APPROVED. There is no current APK identity/signature or Galaxy S26 Ultra airplane-mode/reboot acceptance. Historical APK identities cannot substitute. Environment publication is user-reported in the pinned checkpoint; no independent publication-status tool was available.

## Findings and next actions

FINDINGS.csv is the controlling detailed finding list. Each row includes current path/locator, exact trigger, actual versus expected behavior, evidence, confidence, bounded correction and acceptance test. Findings are new IA IDs and are independent of the incomplete historical audit labels.

### IA001 — HIGH — OBSERVED_CODE_DEFECT

`app/src/main/java/dev/russiancompanion/v2/ui/V2PracticeFlows.kt` — V2LabScreen, question rendering.

Optional labs render item.prompt and options but never item.promptLetter. For u1-i-м-identity the Cyrillic letter disappears; for form and meaning checks the word/sentence disappears.

Expected: Show the complete governed cue, with an accessible label, in every route offering that item. Evidence: Compare V2PracticeFlows.kt question block with V2Screens.kt item.promptLetter rendering and the item records in all 30 packets. Static predicate independently confirms absence. Runtime not executed. Confidence: HIGH; status: OPEN.

Bounded correction: Render the same cue component in lesson and lab; preserve answer keys and optional-practice credit boundary. Acceptance test: Open alphabet item u1-i-м-identity and grammar item u6-g-feminine-0 in optional labs; assert cue visible/readable to TalkBack before answering. Verify recording optional answers still cannot grant required credit.

### IA002 — HIGH — OBSERVED_CODE_DEFECT

`app/src/main/java/dev/russiancompanion/v2/curriculum/V2ProgressRules.kt` — unitState; LocalStore.readV2Learner; referencesResolve; V2Entities.kt.

Completed units are read directly from unversioned unitNumber/timestamp rows. unitState trusts them to show Completed and unlock prerequisites, even when content-bound attempts become stale. Restore accepts completion rows without reconciling them against current curriculum evidence.

Expected: Keep historical activity, but only current reproducible governed evidence may satisfy current prerequisites; incompatible/forged completion claims must be rejected or visibly unverified. Evidence: V2Entities.kt and V2BackupFormat.kt completion models lack digest; LocalStore.kt readV2Learner maps rows to completedUnitNumbers; V2ProgressRules.kt unitState trusts the set. referencesResolve does not check v2 completion evidence. Static trace; full restore regression not run. Confidence: HIGH; status: OPEN.

Bounded correction: Bind completion to unit/rule identity or recompute eligibility from governed evidence; separate historical completion from current advancement. Acceptance test: Complete Unit 6, change its governed content/key, reload/restore: old attempts must not be current evidence and Unit 7 must not unlock solely from old Unit 6 row. Restore a hash-correct backup claiming completion with no supporting evidence and assert rejection or explicit unverified state.

### IA003 — HIGH — UNMET_CURRICULUM_REQUIREMENT

`app/src/main/assets/content/v2-curriculum.json` — advancement.requireDueReview; V2ProgressRules.advancementDecision.

All later-unit advancement records have requireDueReview=false. Only Unit 2 requires a review, and the gate implementation looks for Unit 1 review steps. Mixed review is optional and sorted by recent misses/ID, without a due-review obligation.

Expected: Retain meaningful cumulative reviews across applicable earlier skills and make due required review part of advancement. Evidence: All 30 ADVANCEMENT records independently inventoried: requireDueReview=true only in Unit 2. V2GovernedIdentity.kt practiceQueue and V2ProgressRules.kt gate trace. Confidence: HIGH; status: OPEN.

Bounded correction: Define bounded versioned cumulative-review obligations and governed completion, using taught objectives; do not create another vocabulary SRS. Acceptance test: Given due cumulative review and otherwise complete current work, advancement must remain blocked; complete the assigned multi-objective review and re-evaluate successfully. Never select untaught objectives.

### IA004 — HIGH — UNMET_CURRICULUM_REQUIREMENT

`app/src/main/assets/content/v2-curriculum.json` — Units 1-2 foundations and unavailable labs.

Foundation assessment is printed-letter/print-pair recognition. u1-st-decoding explicitly introduces no words; marked example words/model audio/handwriting assets are absent. Unit 2 has five repeated print-recognition checks and book-directed reading. Listening and Pronunciation labs explicitly cannot run.

Expected: Supply the required reviewed decoding, pronunciation/listening and handwriting-recognition foundation, while keeping unscored speech unscored. Evidence: Unit 1/2 packet objectives, teaching, five Unit 2 items; practiceOffers marks audio/handwriting absent. Private reference Lesson 1 sections 1.1-1.2 printed p1 (PDF13), sampled only. No claim of full book alignment. Confidence: HIGH; status: OPEN.

Bounded correction: Research/admit bounded licensed foundation assets and checks, preserve truthful unavailable states until admitted; do not substitute arbitrary catalog audio. Acceptance test: A novice completes letter recognition, syllable/word decoding, model listening, unscored recording and handwriting recognition offline; prove each assignment traces to taught reviewed content and correct asset rights.

### IA005 — HIGH — ASSESSMENT_CONSTRUCT_DEFECT

`app/src/main/assets/content/v2-curriculum.json` — obj-u4-nominative assessment chain.

The objective is nominative role, but its choices select sentence translations. Recognizing husband/train vocabulary can pass without identifying the subject or nominative function; guided and first independent check are identical.

Expected: Independent checks must test the named case role rather than only recognize a whole-sentence gloss. Evidence: Unit 4 packet objective and all three items. Cornell primary grammar confirms subject as principal nominative function (https://russian.cornell.edu/grammar/html/gr01_b_2_b.htm), retrieved 2026-10-12. Confidence: HIGH; status: OPEN.

Bounded correction: Retain valid translations as examples and add a licensed/bound case-role contrast with a new independent application. Acceptance test: A learner who knows the English gloss but chooses a different case role must fail the role check; a distinct taught subject-function application must be scored correctly.

### IA006 — HIGH — ASSESSMENT_ANSWER_DISCLOSURE

`app/src/main/assets/content/v2-curriculum.json` — SOURCE_QUESTION promptLetter and answer.

All six question-word cues visibly equal their keyed answer. The lesson renderer shows the Russian answer before a question asking which Russian word expresses the English purpose.

Expected: Recognition checks should require selecting from the English purpose without printing the target answer as the cue. Evidence: All six packet promptLetter==answer comparisons independently reproduced; V2SessionScreen prints promptLetter. Confidence: HIGH; status: OPEN.

Bounded correction: Remove the redundant Russian answer cue or use a contextual question that tests its function; update bound evidence and review digests. Acceptance test: Assert the pre-answer screen does not disclose the keyed word; use a distinct independently bound question-purpose check and verify correct/wrong scoring.

### IA007 — MEDIUM — ASSESSMENT_CONSTRUCT_DEFECT

`app/src/main/assets/content/v2-curriculum.json` — SOURCE_INDECLINABLE flag questions.

The learner is asked which flag the admitted row records, with options 1/0 and feedback repeating that flag. This is database-field recall rather than language use or a learner-facing grammatical explanation.

Expected: Use explained declinable/indeclinable distinctions or contextual unchanged-form checks. Evidence: Unit 5 packet questions, feedback and obj-u5-indeclinable title. Confidence: HIGH; status: OPEN.

Bounded correction: Keep the original database flag as provenance metadata; teach and assess the grammatical distinction in ordinary learner-facing language. Acceptance test: A novice can explain the answer from taught morphology without seeing or knowing a source CSV flag; wrong answers expose a specific explanation.

### IA008 — MEDIUM — ASSESSMENT_FEEDBACK_DEFECT

`app/src/main/assets/content/v2-curriculum.json` — truncated rule options.

Several options end with the incomplete clause “we use the word” without naming it. Other options say “It is followed” without supplying the referent. These source fragments are presented as rules, and feedback repeats them.

Expected: Rule choices must be complete, self-contained and explain the case/construction relevant to the cue. Evidence: Unit 8 availability and Unit 10 possession/absence packet item fields; exact affected IDs are listed in ITEM_ASSESSMENT_AUDIT.csv. Confidence: HIGH; status: OPEN.

Bounded correction: Independently phrase complete bounded explanations and bind them as pending synthesis; preserve original source records separately. Acceptance test: Every option has a complete proposition and explicit referent. A wrong response explains the required construction/case, not a cut-off source sentence.

### IA009 — MEDIUM — ASSESSMENT_FEEDBACK_DEFECT

`app/src/main/assets/content/v2-curriculum.json` — feedback and distractorRationales.

135 item feedback blocks ask whether the answer shows the requested label/contrast and then repeat the key. They do not state the linguistic reason. Optional labs also omit lesson remediation/reference links.

Expected: Explain the relevant ending, construction, subject, meaning or contrast; expose a directly relevant explanation after mistakes. Evidence: Literal phrase inventory independently counted 135 records. Representative u6-g-third-0, u9-i-quantity-1, u11-i-past-read-1. UI lab feedback displays only item.feedback. Confidence: HIGH; status: OPEN.

Bounded correction: Rewrite feedback in bounded groups from retained sources and provide a remediation/reference route in labs; verify each competing option. Acceptance test: For each affected item, a wrong answer yields an explanatory reason and reachable teaching/reference. A test asserting only key presence must not count as semantic validation.

### IA010 — MEDIUM — ASSESSMENT_CONSTRUCT_DEFECT

`app/src/main/assets/content/v2-curriculum.json` — ACTIVE_TIME opposite-label question.

The past reading form is paired with keyed “in the present” because the question asks for the other time. The latest teaching explicitly coaches this reversal. The key follows the literal prompt; it is not an incorrect translation, but this item rewards complement-label selection rather than interpreting the displayed form.

Expected: Use a distinct contextually supported active-time application to assess time; avoid training reversal as proof of form comprehension. Evidence: Unit 29 item and u29-st-teach-active-2; retained grammar capability contains past/present pair. Confidence: HIGH; status: OPEN.

Bounded correction: Replace the reversal with a new admitted application or retain it as unscored metalinguistic practice outside the time gate. Acceptance test: A displayed past form must not obtain time-comprehension credit solely by choosing the opposite label. Test a distinct governed form/context with explanatory feedback.

### IA011 — HIGH — UNMET_ASSESSMENT_COVERAGE

`app/src/main/assets/content/v2-curriculum.json` — SHORT_FORM tag versus vocabulary gloss.

The objective is deliberately narrowed to choosing glosses. The guided long form and independent short form both select “new”; the other independent item selects “beige” for an invariant colour term. None tests short versus long form, agreement or predicate function, although the source capability is short-form-adjective.

Expected: Do not treat vocabulary glosses as sufficient demonstration of the required short-adjective grammar scope. Evidence: Unit 29 objective, three items, capability and new teaching. Private Lesson 29 heading and sections 29.1-29.2 sampled at printed p355/PDF367; detailed short-adjective book section was not inspected. Confidence: HIGH; status: OPEN.

Bounded correction: Preserve correct glosses as vocabulary; add a licensed/bound grammatical contrast and independent application; keep broader coverage pending. Acceptance test: A learner who recognizes “new” but cannot distinguish long/short form or taught predicate function cannot pass the short-form grammar check. Inspect every answer/distractor for the new context.

### IA012 — HIGH — ASSESSMENT_CUE_INSUFFICIENCY

`app/src/main/assets/content/v2-curriculum.json` — promptLetter=личным местоимением.

The independent punctuation cue is a grammatical category phrase, not a sentence or participial construction. The answer is a source connective meaning “except in those cases”. Without a described word, phrase position or full construction, it cannot demonstrate punctuation application.

Expected: Provide a complete taught construction and ask for its punctuation treatment, including the exception where applicable. Evidence: Unit 29 item and cap-obj-u29-punctuation exact source span independently read. The new instruction teaches the exception label but supplies no assessed contextual punctuation decision here. Confidence: HIGH; status: OPEN.

Bounded correction: Use an admitted licensed example/application; keep the connective as source prose rather than an answer pretending to classify punctuation. Acceptance test: A full exception construction requires a specific comma decision; the learner must not be able to pass by recalling the source connective. Verify default, exception and end-of-sentence boundary separately.

### IA013 — HIGH — UNMET_ASSESSMENT_COVERAGE

`app/src/main/assets/content/v2-curriculum.json` — row comparison and heading recognition.

The letter-name check keys an English comparison word from the same table row; the abbreviation check keys the English heading above the examples. These keys fit the source-layout prompts, but do not ask the learner to name a Russian letter or interpret/use the abbreviation. New teaching explicitly coaches these source positions.

Expected: Treat row/heading recall as limited reference literacy; required letter-name/abbreviation scope needs a distinct language check. Evidence: Unit 30 items, objectives and latest teaching. All source-layout wording is retained; no wrong-key claim is made. Confidence: HIGH; status: OPEN.

Bounded correction: Keep limited examples only if honestly labelled non-gating; add independently bound name and contextual abbreviation checks inside authorized source boundaries. Acceptance test: Show a Russian letter and require its taught name, without English table-row clues. Show an admitted abbreviation in context and test its meaning/treatment; heading recall alone must not satisfy the language objective.

### IA014 — HIGH — UNMET_ASSESSMENT_COVERAGE

`app/src/main/assets/content/v2-curriculum.json` — motion-directionality independent coverage.

The only directionality label check is guided. The four independent checks ask two lexical meanings and two prefixed-sentence readings; none contrasts unidirectional/multidirectional selection, although the objective names directionality and a source capability clears it.

Expected: Require an independent taught directionality application before treating directionality as demonstrated. Evidence: Unit 20 objective, all five assessment items and readiness report listing five resolved capabilities. Source taxonomy is not an independent learner demonstration. Confidence: HIGH; status: OPEN.

Bounded correction: Add a bounded licensed directional contrast/application, avoiding aspect/directionality conflation; reassess claimed coverage. Acceptance test: After guided directionality practice, a distinct contextual directional question must be required and scored from a reviewed key. A vocabulary-only pass cannot demonstrate directionality.

### IA015 — HIGH — STATIC_SOURCE_BINDING_RISK

`app/src/main/java/dev/russiancompanion/v2/curriculum/V2Validator.kt` — phase2ChainDefects;phase3ChainDefects;originalSynthesisRelation;contentAuditProvenanceErrors.

Several synthesis admission paths check that application component strings occur in a frozen span, then trust mutable synthesis.applications for the tuple. Exact corpus-array equality is added for postal/Unit27 comprehension/Unit28 contextual capabilities, but not uniformly for all remaining paths.

Expected: Bind each admitted cue/answer/distractor tuple to immutable corpus data, not merely independent word membership or editable metadata. Evidence: Static branches independently read. Host predicate counterexample preserves phase3:bookish-reading hash 3228475d… and finds mismatched cue/answer/distractors all inside the span. This reproduces only the membership weakness, not a full Kotlin validator bypass. Confidence: MEDIUM; status: NEEDS_EVIDENCE.

Bounded correction: Audit and pin tuple relations for remaining corpora; add full negative validator regressions before concluding which changes are required. Acceptance test: Mutate cue/answer pair and all corresponding mutable application metadata while preserving the frozen corpus/hash. Full production validator must reject the forgery. Test Units 7/20/26 and Phase2/3 separately, retaining their valid legitimate tuples.

### IA016 — MEDIUM — ASSESSMENT_TRANSFER_LIMIT

`app/src/main/assets/content/v2-curriculum.json` — same prompt/cue/key/options signature.

Eleven same-unit groups reuse the exact question tuple in guided and independent roles. Unit 17 repeats its clock-question tuple three times; Unit 28 repeats each timing question from guided practice as an independent check. This permits answer-memory rather than transfer. Distinct-signature advancement already deduplicates the tuple; no inflated distinct-count claim is made.

Expected: Use a distinct bound independent application where the declared skill requires transfer or durable recognition. Evidence: 28 duplicate groups found overall; 11 specifically contain guided and independent roles in one unit. Exact groups retained in scope JSON. Repetition across cumulative review is not classified automatically as a defect. Confidence: HIGH; status: OPEN.

Bounded correction: Preserve legitimate review repetition but separate independent evidence from rehearsed identical choices; research/admit variants rather than random text generation. Acceptance test: Assert a required independent application differs in meaningful governed context/cue from the guided set, and that merely replaying the guided tuple cannot substitute for it.

### IA017 — MEDIUM — OBSERVED_REFERENCE_CONTRADICTION

`app/src/main/assets/content/v2-curriculum.json` — reference detail remaining-gap statements.

The Unit 21–25 notice says completion over a period is not prepared and later says the bounded task is implemented. The Unit 26–30 notice mixes unprepared participle-stress/dictionary wording with implemented bounded tasks. These retained present-tense gap statements confuse actual availability and broader coverage limits.

Expected: Present one current availability statement, separately naming the scope still absent and the pending review status. Evidence: Current objects in ITEM_TARGET_CURRENT_EVIDENCE.json CR070/CR071 independently read in full. Broader mastery limits remain valid; the contradictory availability wording does not. Confidence: HIGH; status: OPEN.

Bounded correction: Replace stale learner-facing sentences with precise current bounded availability and separate remaining-scope statements; retain old versions in historical evidence. Acceptance test: Search current references for contradictory prepared/implemented claims on the same bounded task. Ensure a novice can tell what runs now and what remains unavailable without reading audit history.

### IA018 — MEDIUM — STATIC_BACKUP_CONSISTENCY_RISK

`app/src/main/java/dev/russiancompanion/v2/ui/V2BackupCoordinator.kt` — writeStoreBackup then second governedLearnerDocument.

Backup serializes one learner snapshot, but afterward re-reads live state to populate completed-export counts. The backup screen permits navigation back while work runs, and ordinary learner writes are not fenced by backup export. Counts may describe records written after the archive snapshot.

Expected: Record the exact snapshot that was exported and its coverage; later changes must not be described as backed up. Evidence: V2BackupEngine.writeStoreBackup obtains the first document; V2BackupCoordinator.createBackup obtains a second after output closes; V2BackupScreen onBack remains available. Static risk; concurrency execution was not performed. Confidence: MEDIUM; status: NEEDS_EVIDENCE.

Bounded correction: Use the written snapshot/manifest counts and coverage identity for ExportRecordEntity, or establish a documented consistent export boundary. Acceptance test: Hold export after snapshot capture, navigate back and add an attempt/note, finish export. Recorded coverage must match archive bytes; post-snapshot work must remain visibly unexported.

## Completion boundary

First obtain the exact raw asset bytes and verify their hash, reconcile all 30 packet projections, retrieve all 58 current capability objects and rerun full source-tuple regressions in the real pinned checkout. Then close supported defects in bounded changes under separately authorized implementation, refresh affected review/ancestry/digest evidence, and perform the remaining comprehensive semantic/rights/book review. The engineering handoff supplies precise priorities and preservation constraints. Physical acceptance remains outstanding. No merge, freeze, signing, owner acceptance or release decision follows from this diagnostic.
