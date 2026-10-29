# MASTER AUTHORITY v2.0 — Amendment 2

Status: RATIFIED by the Product Owner on 10/29/2026. Controlling once committed under docs/governance/.
Basis: Year-One Full Scope Audit of 10/29/2026 (commit 9101af8c). Finding IDs below (L, G, E) refer to that audit. CC and IA IDs refer to the 10/22 recheck and the 10/12 audit.
Scope: delivery sequence, evidence limits, dev-data handling, audio verification, practice types, review, coursework features, the Units 27/29/30 status, legacy-code removal and the Codex operating procedure.
Unchanged: every other MA2 rule and Amendment 1 rule, including offline only (MA2-009 to MA2-015), no runtime AI (MA2-014), no Penguin reproduction (MA2-031, MA2-111), no scoring of pronunciation or open writing (MA2-059), no speech recognition (MA2-060), rights rules (MA2-092 to MA2-111) and public-release blocking (MA2-007, MA2-174).

Precedence: where this amendment conflicts with MA2-074, MA2-097, MA2-098, MA2-143, MA2-144, MA2-159 to MA2-164 or Amendment 1 sections 1, 3 and 4, this amendment controls for the matters it names.

This is product governance, not legal advice.

---

## Part 1 — Delivery sequence

A2-01. Build blocks. Implementation work on content runs in six blocks:
- Block 1: Units 1–5
- Block 2: Units 6–10
- Block 3: Units 11–15
- Block 4: Units 16–20
- Block 5: Units 21–25
- Block 6: Units 26–30

A block is BLOCK_COMPLETE when its queue tasks are done, CI passes and every CRITICAL or HIGH finding scoped to its units is fixed. BLOCK_COMPLETE is a work state, not an approval.

A2-02. One final audit. After all six blocks are BLOCK_COMPLETE, Claude performs one final curriculum audit of Units 1–30 (Amendment 1 stage FINAL_CURRICULUM_AUDIT_PASSED). No per-unit or per-block approval stages run before it.

A2-03. Audit corrections. Codex fixes the audit findings. Claude then performs a delta audit of only the records whose content changed. Amendment 1's invalidation applies only to the changed records' digests and to the pack digest, and a passed delta audit restores FINAL_CURRICULUM_AUDIT_PASSED for the current pack digest. The full audit is not repeated unless the delta audit finds a systemic problem.

A2-04. Independent verification. After the final audit passes, Codex performs INDEPENDENT_AUDIT_VERIFIED on the exact commit and builds a test APK with a recorded SHA-256.

A2-05. User testing comes last. Product Owner user testing on the Galaxy S26 Ultra happens only after FINAL_CURRICULUM_AUDIT_PASSED and INDEPENDENT_AUDIT_VERIFIED, using a written user-test script that covers MA2-131 and MA2-165 to MA2-171. No device acceptance session is required earlier. The audio listen-check in A2-17 is a content check, not user testing.

A2-06. User-test fixes. Problems found in user testing are fixed by Codex, delta-audited by Claude and re-verified by Codex. The Product Owner re-tests only the affected workflows.

A2-07. Acceptance and freeze. OWNER_ACCEPTED follows a passing user test. The accepted APK is the personal study build for the Product Owner's own device. Public release stays governed by MA2-007 and MA2-174 unchanged.

## Part 2 — Development data and history

A2-08. No production schema exists yet. Until OWNER_ACCEPTED, no database schema, review-assignment schema, backup format or learner record is a "supported production schema" under MA2-143 or MA2-144.

A2-09. Dev records may be discarded. Until OWNER_ACCEPTED, the implementation may drop, reset or re-create development learner data and saved review assignments. It need not keep earlier assignment-schema meanings. One schema per record type is the target.

A2-10. After acceptance. After OWNER_ACCEPTED, MA2-143 and MA2-144 apply in full from that build's schemas forward.

A2-11. Consolidation. The cumulative-review assignment schemas 1–15 and any proposed schema 16 shall be consolidated into one current schema.

## Part 3 — Evidence budget

A2-12. Evidence bundles. Governance evidence is limited to one bundle per stage: final audit, each delta audit, independent verification, user test and owner acceptance. Each bundle holds:
- the commit
- the pack digest
- the CI test result for that commit
- the findings disposition list
- the report for that stage

A2-13. Test evidence. The CI test result on the exact commit is the test evidence. Interrupted runs, dry discovery inventories, method-count predictions and "conserved" bindings are not retained as governance evidence. They may remain in CI logs.

A2-14. Location. Historical audit material under audit/ older than this amendment shall move off the working branch to a single archive branch, archive/audit-history-2026-10-29, with its commit hash recorded in audit/ARCHIVE_INDEX.md. The working branch keeps only the A2-12 bundles, that index and any path the app code reads, such as audit/final-curriculum-audit/. Tests that only check historical audit evidence are retired with it.

A2-15. Provenance size. Provenance records may be kept in a separate non-bundled file if the shipped pack keeps the IDs needed for MA2-092 and MA2-109. The learner-facing pack shall not carry data the app does not read at runtime.

A2-16. Content pins. Content checks belong in the pack validator, driven by data. Unit-specific pin files in Kotlin shall be removed or folded into data-driven checks.

## Part 4 — Audio

A2-17. Verified audio match. A clip may be bound to text when either:
- (a) its source file page names the exact word or phrase and the Product Owner or a reviewer listens and confirms the match, recorded per clip as OWNER_LISTEN_VERIFIED or REVIEWER_LISTEN_VERIFIED with the clip SHA-256 and the text, or
- (b) a reviewed text-match record from the source dataset ties the clip to the text.

This is the one-to-one verification MA2-098 requires. It does not use filename similarity or rank as the basis (MA2-097). The file title only proposes the candidate, and the human check decides.

A2-18. Listen-check sheets. Codex prepares an offline listen-check sheet per block: a single HTML file that plays each candidate clip beside its proposed text, with Match / No match / Unsure buttons and an export of the results. The Product Owner completes it on a computer. Only clips marked Match are bound. The listen-check may run while other tasks continue.

A2-19. Audio in v2. The v2 app shall provide:
- model playback
- slowed playback that leaves the source file unchanged (MA2-065)
- local recording, replay and A/B comparison (MA2-054)

These stay unscored (MA2-059, MA2-060). The unreachable v1 audio code may be moved into v2 or rewritten.

A2-20. Foundation audio. Units 1–2 shall include letter and word audio from verified clips, closing CC002.

## Part 5 — Practice and assessment

A2-21. Production items. The pack shall include typed-response items scored by exact match against a reviewed, versioned answer key with listed accepted variants. This is closed-response under MA2-061.

Normalization limits:
- the case of the first letter may be ignored
- stress marks may be ignored
- ё/е may be treated as equal
- nothing else is normalized

A2-22. Item kinds. Allowed typed kinds include:
- form production (give the requested case, person or tense)
- transformation
- English-to-Russian for taught phrases
- dictation from a verified clip

Free composition stays unscored (MA2-059).

A2-23. Minimum per unit. Each unit shall have:
- at least 20 checked items
- at least 30 percent of its required checks typed, from Unit 3 on
- at least one dictation item once its audio is verified

A unit may fall short only with a recorded reason accepted by the Product Owner.

A2-24. Item validity. A required check is invalid if:
- the key is inferable from the prompt
- the only distractor is the displayed cue
- the teaching step states the item's exact key
- it tests recall of a label or position rather than Russian

The validator shall report these, and none may remain when a block is marked BLOCK_COMPLETE (CC011, CC022, CC026, CC027).

A2-25. Wrong-answer feedback. A wrong answer shall show why that answer is wrong (its distractor rationale) and link to remediation (MA2-045, MA2-046, MA2-062, CC034).

A2-26. Learner text. Governance terms ("admitted," "frozen," "retained," "review-pending," source IDs) and raw source markup shall not appear in learner-facing text (CC023, CC029).

## Part 6 — Review

A2-27. In-app review queue. The app shall keep a local spaced review queue of its own checked items. Missed items return sooner. Correct items return at growing intervals. AnkiDroid stays the default for long-term vocabulary decks (MA2-074). The in-app queue covers app-checked forms and grammar. This applies MA2-027 and MA2-057 and does not conflict with MA2-074.

A2-28. Due review size. A unit's due cumulative review shall have at least 8 items, drawn from at least 3 earlier objectives across more than one skill where available. No single item may appear in more than three consecutive unit reviews.

A2-29. Gate. MA2-088 is unchanged: due review stays required to advance.

## Part 7 — Coursework

A2-30. Assignment list. Today shall show the unit's assignments as a checklist:
- book pages and sections
- named book exercises
- app sessions
- binder task
- review due

A2-31. Target dates. The learner may set a target date per unit. The app shows days remaining. Missing a date has no penalty and resets nothing (MA2-091).

A2-32. Binder tasks. Each unit shall have its own binder assignment: a specific written task tied to the unit's objectives, such as "write six sentences using the prepositional after в/на about places you go." Where possible it includes a model answer the learner compares against afterward. The result is recorded as SELF_CHECK and is never shown as demonstrated ability (MA2-082, MA2-090).

A2-33. Tutor tasks. Tutor steps, when present, name the specific thing to bring to a tutor. Tutor work stays optional (MA2-079).

A2-34. Unit test. Each unit ends with a unit test:
- 10 to 20 checked items from that unit, at least 30 percent typed
- the pass mark is shown before the attempt (MA2-085)
- a fail routes to remediation and allows a retake

A2-35. Gradebook. Progress shall show, per unit and objective:
- attempts
- accuracy
- unit-test results
- review accuracy
- items in the review queue

Labels follow MA2-083 and MA2-084.

A2-36. Reference minimum. Before a block is BLOCK_COMPLETE, the reference library shall have a substantive entry for each grammar topic first taught in that block (MA2-068, CC017).

## Part 8 — Variances, legacy code, review roles

A2-37. Units 27, 29, 30. The Product Owner Mapping and Variance Acceptance of 10/02/2026 (commit f8b8b09a) is ratified as the activation of the Unit 27, 29 and 30 pedagogical boundaries. Amendment 1 section 3's "stay PROPOSED_NOT_ACTIVATED" sentence is superseded. Activation is not review approval. The final audit and owner acceptance still apply (closes the conflict in CC048).

A2-38. Legacy removal. The unreachable v1 UI and its tests shall be removed, along with the release-gate dependencies on the legacy pathway (CC038, CC039, CC044). Code still needed, such as audio and the helpers v2 imports from presentation/navigation, is moved into v2 first.

A2-39. Russian review. A qualified human Russian reviewer is optional for the personal study build and recommended before public release. When one is engaged, their sign-off is recorded as REVIEWER_CHECKED alongside the Amendment 1 chain. It does not replace the chain.

## Part 9 — Codex operating procedure

A2-40. Roles. Codex implements and independently verifies. Claude performs the final audit and delta audits. The Product Owner ratifies, completes listen-checks, performs user testing and accepts. This replaces the Cursor implementation role in MA2-159 to MA2-163 and Amendment 1 section 4. MA2-163's duty to report blockers instead of narrowing a requirement now applies to Codex.

A2-41. Work queue. The ordered task list is WORK_QUEUE.md at the repository root. It is the only source of what to do next. NEXT_TASK.md and the checkpoint lines in the old AGENTS.md are retired.

A2-42. Continue protocol. When the Product Owner says "continue," Codex runs the next task under the protocol written in AGENTS.md and WORK_QUEUE.md. Codex stops only at a task marked STOP, at a blocker, or after finishing one task. MA2-160's stop-after-each-phase rule is replaced by this protocol.

---

## Owner ratification

- [x] I ratify Amendment 2 as written.
- [ ] I ratify with these changes: ______________________
- Product Owner: Justin Kulka  Date: 10/29/2026
- Recorded by Claude at the Product Owner's written direction on 10/29/2026, because the Product Owner was on a phone.
