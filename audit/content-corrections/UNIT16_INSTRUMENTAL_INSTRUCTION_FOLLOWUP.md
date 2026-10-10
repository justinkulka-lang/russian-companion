# Unit 16 instrumental noun and adjective instruction

PR #64 stays draft. This is a bounded implementation correction, not final curriculum acceptance.

The three existing form lessons previously displayed rows with shared English glosses, a short ending rule, and generic question-shaped feedback. They now label singular/plural instrumental, dative and prepositional contrasts, distinguish noun endings from adjective agreement, and work the retained choices. Eighteen existing teaching cards contain 730 words including titles. All plural teaching now precedes its guided choice. Each remediation screen retains every explanation and every teaching example from its session.

Nine questions receive specific prompts, answer explanations and distractor rationales. The pencil question names its lexical target; feedback distinguishes the train and husband options. Adjective instruction identifies the neuter use of the singular form and distinguishes its plural ending. Recognition checks are not presented as sentence-production proficiency.

Keys, options, Russian cues, example objects, objective requirements, tested features, assessment source relations, step identities and the Unit 16 ten-check advancement requirement remain unchanged. Only the plural session changes step order. Every other unit's curriculum record is identical. Original source, rights and provenance records remain intact; 87 pending original-synthesis coverage records are appended. No new Russian token, inflection or example object is introduced.

Research uses the retained Wikibooks instrumental revision 4219602 and existing frozen OpenRussian noun/adjective fields. Cornell's [instrumental case forms](https://russian.cornell.edu/grammar/html/gr06_a_1_a.htm), consulted 2026-10-10 UTC, corroborates the ending and spelling rules. The English applications are independently authored and remain pending final linguistic audit. No source prose or owned-book exercise is packaged.

Implementation ancestor: `bf610f06c9f1ec2a8ea7ec5c8307f80899b53444`, parent `928b9e41b007af5f2005590506cf13f693a4800e`. All 30 refreshed packets bind that ancestor. Only Unit 16's instructional digest changes; all mapping digests remain unchanged. Candidate, readiness, preparation and dossier bindings are refreshed without granting acceptance.

Verification evidence is in `pr64-unit16-verification-2026-10-10/`. Initial production validation rejected three standalone Cyrillic ending fragments, each repeated in remediation. The wording was adjusted to describe those endings through the already admitted whole forms; the validator was not weakened. That failed attempt is retained. A subsequent prebinding run passed all 38 focused host tests. All three new tests reject the exact preceding curriculum. Scope verification, repair reproduction and idempotence pass. Production validation on the final pack reports zero errors.

The host harness compiles production curriculum, progress, domain and release code with Android asset-access and Room annotation stubs. It uses portable Java 21, Kotlin 2.2.20, serialization 1.9.0 and JUnit Platform 1.13.4. These differ from the project's Android build toolchain. Stub asset operations fail explicitly if called. Host execution does not verify Compose, Android persistence, APK execution or physical-device behavior. Current-head Android CI is separate.

The target-evidence generator now emits repository-relative paths with forward slashes and UTF-8 bytes with stable newlines on Windows as well as Linux. Its exact-byte `--check` passes; the retained target CSV is unchanged.

Expanded host verification initially completed 46 checks: 42 passed and four review-pipeline tests failed before content assertions because they redirected Git error output to the Linux-only `/dev/null` path. The retained failure log identifies this environment issue. Both review utility and pipeline test now use Java's platform-independent `ProcessBuilder.Redirect.DISCARD`. The checks still execute real Git ancestry, curriculum and package validation. The first rerun exposed the same Linux-only path on the helper's output redirect. That redirect was also replaced with `ProcessBuilder.Redirect.DISCARD`; the intermediate failure is retained. The final targeted rerun passes all four affected methods with zero failures or skips. Together with the earlier 42 successful checks, this gives 46 successful host checks across the runs; it is not a single fresh 46-test pass. Real Git lineage, generated packets, isolation/mutation rejection and pending-review assertions remain intact.

- Repository-source digest: `75dc267c41a8e133b71537f5322a684764be6daaf9240f9d5ff681858213ee23`.
- Schema-3 pack content-review digest: `af7b848116f01c49b5c86b6c4d0b4e91c52eda470a180595c149351483ff6d9a`.
- Unit 16 instructional digest: `7229613a7e7fab2531d2526599513d741805032ee1778a371e375b3454301e98`.
- Raw curriculum SHA-256: `5c0f88bc08065ce7aa7e03e992f3f46cc7381ee9fb9413f95f5c9aefcd34bebc`.

The sequence inventory now has 129 unresolved cue-level flags, not confirmed defects. Multi-guided sessions remain outside that scan. All 75 targets resolve uniquely and remain pending semantic acceptance: 59 unchanged, 12 changed, four absent from the historical snapshot. The 19-finding audit remains open.

Next work includes Unit 16 means/accompaniment, preposition and complement explanations; other foundational teaching and worked examples; cumulative review and reference coverage; detailed owned-book mappings; required pronunciation audio and handwriting assets; final comprehensive Claude curriculum audit; independent verification; and owner/device acceptance. All 30 units remain development and review-pending. Candidate stays unfrozen, sourceCommit null, artifact NOT_RECORDED, publicRelease NOT_APPROVED. Nothing is marked ready, merged, frozen or released.
