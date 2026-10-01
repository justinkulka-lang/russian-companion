# Product Owner variance decision packet

Status: DECISION_PACKAGE_NOT_ACTIVATED

This packet prepares a Product Owner decision. It does not activate a variance. It does not amend `MASTER_AUTHORITY_v2.0.md` or Amendment 1. It does not rewrite `UNITS_26_30_AUTHORITY_VARIANCE_PROPOSAL.md` or `UNITS_26_30_AUTHORITY_VARIANCE_PROPOSED_RESOLUTION.md`. Production validation does not read this file as an activated decision.

Units 27, 29, and 30 remain `PROPOSED_NOT_ACTIVATED`. Their packaged readiness remains `NOT_PREPARED`. Green CI, a counter, a review status, and a model name do not activate a variance.

## Governing instruments

- **MA2-178** in `docs/governance/MASTER_AUTHORITY_v2.0.md`. A deliberate variance from the publisher public sequence stays release-blocking until the Product Owner accepts it under Amendment 1, after final curriculum audit and independent verification, with the exact sequence difference, bounded resolution, and versioned digest recorded. Units 27, 29, and 30 remain `PROPOSED_NOT_ACTIVATED` until that acceptance.
- **Amendment 1, section 3.** Units 27, 29, and 30 stay `PROPOSED_NOT_ACTIVATED`. Activation still requires the exact observed sequence difference, the bounded implementation resolution, final curriculum-audit disposition, Product Owner acceptance, and a versioned digest and date.
- **Proposal** `docs/governance/UNITS_26_30_AUTHORITY_VARIANCE_PROPOSAL.md`, status `PROPOSED_NOT_ACTIVATED`.
- **Proposed resolution** `docs/governance/UNITS_26_30_AUTHORITY_VARIANCE_PROPOSED_RESOLUTION.md`, status `PROPOSED_NOT_ACTIVATED`, recommendation `ACTIVATE_WITH_CORRECTIONS`. Item 7 now states the shipped participle-punctuation instruction. That correction is inactive. Production validation does not read the resolution as an activated decision.

The publisher public sequence is the normative table in Master Authority, “Required first-introduction scope.” Those titles are topic-level identifiers. They do not authorize Penguin prose.

## Live curriculum state used by this packet

Required source capabilities for the governed curriculum are resolved. `contentCompletenessErrors` is empty for Units 27, 29, and 30. That emptiness does not change readiness. The validator still applies the unresolved variance to those three units, so each stays `NOT_PREPARED` and cannot advance.

Content-review digests and mapping-package digests below are the current packaged values. This file lives under `docs/governance`, so it changes the repository source digest. This packet does not embed that repository digest. The live repository source digest is recorded in `release/acceptance/candidate.json` and in `audit/owner-governance/FINAL_AUDIT_PREPARATION_MANIFEST.json`.

Pack content-review digest: `dc56345a9309e7dc8f5fe5e159f3ec935958d64b83443d4a9f17d6c653911a2f`.

Curriculum source commit for Units 26–30: `345448c16f326114ccb2f6738c75fb4206798cae`.

## Unit 27

Recommendation: ACTIVATE

This recommendation is not an activation.

### Exact governing clause

MA2-178 and Amendment 1 section 3. The proposal records: “Lesson 27 is passive-participle material. Active participles first appear in Lesson 29. Unit 27 must not contain active-participle instruction.” The proposed resolution, if later activated, would say past passive material is required, present passive material is optional and non-gating, and Unit 27 teaches passive participles only.

### Publisher-sequence issue

The public schedule’s required first-introduction line for unit 27 is “Participles: types and stress.” That line is broader than a passive-only lesson. It does not itself say that active participles wait until unit 29.

### App sequence and pedagogical reason

Shipped Unit 27 teaches past passive forms, participle stress, and participle comprehension. Present passive is optional and `gating` is false on `obj-u27-present-passive`. The orientation step `u27-st-orient` says: “Present passive lines are optional and do not gate this unit. Comprehension passages stay in the copy you own. Active forms are not taught here.”

The reference file’s Lesson 27 title is “Participles: types and stress,” the same broader public line. Its body teaches past passive formation and stress as the main lesson, marks present passive as an extra section, and does not teach active participle formation. Active participles open in Lesson 29. That comparison supports the passive-only recommendation. It is not an accepted mapping and not an activation. File continuity with the retained private-file hash is not established.

### Affected objectives and capabilities

- `obj-u27-passive` — required claim `passive-participle`.
- `obj-u27-stress` — required claim `participle-stress`. Gating true.
- `obj-u27-comprehension` — required claim `participle-comprehension`. Gating true.
- `obj-u27-present-passive` — optional, gating false. Not a required source capability.
- `obj-u27-stress-reference` — gating false.
- `obj-u27-book` — book self-attestation. A page range does not clear the variance.

### Current content digest and mapping-package digest

- Unit content-review digest: `b43404505a7bc8bc72a34d2af039d5695d1e919cdc309643ecc7c835cfd99db0`.
- Mapping-package digest: `a15bf8dc76fa2ab5d7bc1448a9f2129da7ee281461338e280c503ee3a5150bdb`.
- Review-package digest: `3a68a30ed98a92fccec81208ebc4cb3f8f15ecd478a06893693637fff62c0303`.
- Mapping disposition remains `PENDING_CURRICULUM_REVIEWER`.
- Unit review status remains `IMPLEMENTED_PENDING_FINAL_AUDIT`.

### Risks and limitations

Activating this boundary accepts a passive-only Unit 27 against the broader public title “Participles: types and stress.” Present passive stays optional and does not clear a present-passive requirement. The proposal’s closing statement that no admitted frozen source exists is historical for that file. The three required claims are now implemented and remain `IMPLEMENTED_PENDING_FINAL_AUDIT`. Implementation is not approval, not a mapping, and not a final audit. The owned-edition page range 332–342 is a proposed observation, not an accepted alignment.

### Consequences

If activated later: a subsequent explicit Product Owner instruction, after final curriculum audit and independent verification, would still be required before production validation could stop treating Unit 27 as an unresolved variance. Content that stays complete could then leave `NOT_PREPARED` and become `CONTENT_COMPLETE_REVIEW_PENDING`. The unit would still be uninstalled, unmapped, unaudited, and outside any release approval.

If rejected: the passive-only sequence remains a release-blocking conflict. Realignment to the broader public first-introduction line would be a curriculum change. The resolved passive, stress, and comprehension claims would have to be reopened if that realignment changes their scope. Until then the unit stays `NOT_PREPARED`.

If revised: the decision text must be rewritten before any activation. The unit stays `NOT_PREPARED`. A revision that puts active participles into Unit 27 would conflict with the shipped sentence “Active forms are not taught here” and would be a curriculum change, not a signature on this packet.

### Product Owner disposition

Product Owner disposition:
Product Owner name:
Product Owner date:
Product Owner signature:

## Unit 29

Recommendation: REVISE

This recommendation is not an activation and does not revise the proposed-resolution file.

### Exact governing clause

MA2-178 and Amendment 1 section 3. The proposal records that active participles first appear in Lesson 29. The reference file’s Lesson 29 title includes active participles and punctuation, and the punctuation section is marked extra. Corrected proposed-resolution item 7 says: Unit 29 punctuation is in scope and is not unavailable. The shipped instruction on `obj-u29-punctuation`, claim `participle-punctuation`, is: when a participial phrase stands before the word it modifies, commas are not used, except when that word is a personal pronoun or the participial phrase states a cause. A phrase after the modified word is set off by commas on both sides. The withdrawn sentence “Unit 29 punctuation remains optional and unavailable until sufficiently sourced and reviewed” is not the governing text. Item 7 does not activate the variance.

### Publisher-sequence issue

The public schedule’s required first-introduction line for unit 29 is “Bookish style, active participles, punctuation, and short-form adjectives.” Active participles on unit 29 agree with that public line and with the owned-edition observation that active participles first appear in Lesson 29. That placement is not an unresolved sequence conflict.

The remaining decision is punctuation. The public schedule lists punctuation. The reference file includes it as an extra section. The shipped objective implements the admitted participial-phrase comma rule and is not unavailable. The corrected item 7 records that shipped instruction. It stays inactive.

### App sequence and pedagogical reason

Shipped Unit 29 teaches active participles, short-form adjectives, a bookish-register note, bookish reading, and a sourced punctuation rule. `obj-u29-punctuation` binds required claim `participle-punctuation`. The orientation step `u29-st-orient` says the unit uses “the frozen participial-phrase punctuation rule.” Punctuation is implemented. It is not unavailable.

The pedagogical reason to keep active participles in Unit 29 is agreement between the public schedule and the owned-edition lesson split. The pedagogical reason not to activate the current proposed resolution is that item 7 would freeze a description the shipped curriculum no longer matches.

### Affected objectives and capabilities

- `obj-u29-active` — required claim `active-participle`.
- `obj-u29-punctuation` — required claim `participle-punctuation`.
- `obj-u29-short-adjective` — required claim `short-form-adjective`.
- `obj-u29-register` — required claim `bookish-register`. Gating true.
- `obj-u29-reading` — required claim `bookish-reading`. Gating true.
- `obj-u29-bookish-note` and `obj-u29-book` are in the unit and do not replace the required claims.

### Current content digest and mapping-package digest

- Unit content-review digest: `bb8645fa3be7f3ac9b7cc7a7d61d551843975f8deccb419a3ad529416c3fb0e3`.
- Mapping-package digest: `a40de565a8d86d7288e31bfaeb75d9c41761b810f99ee56afa1add74df334f21`.
- Review-package digest: `cd7eed324e12a8c25f4a38d69290c4aa7ec6d7e65919adac59576b348ee62619`.
- Mapping disposition remains `PENDING_CURRICULUM_REVIEWER`.
- Unit review status remains `IMPLEMENTED_PENDING_FINAL_AUDIT`.

### Risks and limitations

Item 7 now matches the shipped punctuation instruction. Activating the withdrawn “unavailable” sentence would be false for the current pack. Treating punctuation as optional would require a curriculum change that this packet does not make. Bookish register and bookish reading are original research synthesis and are not Penguin prose. The owned-edition page range 355–367 is a proposed observation. The unit stays in the hardcoded unresolved-variance set until a later signed decision changes production validation.

### Consequences

If activated later: activation of the corrected item 7 would accept the shipped participial-phrase comma rule as the variance text. It would still require final curriculum audit, independent verification, and an explicit Product Owner instruction before Unit 29 could leave `NOT_PREPARED`. It would not install the unit, accept the mapping, or approve release.

If rejected: Unit 29 stays a release-blocking variance. Rejecting the whole unit-29 boundary would leave the implemented active-participle, punctuation, short-form, register, and reading claims in place while the validator continues to block preparation. Removing punctuation or moving active participles would be a curriculum change.

Effects of the item 7 correction: the proposed text now matches the shipped participial-phrase comma rule. `V2Validator.UNRESOLVED_VARIANCE_UNITS` is unchanged. Unit 29 stays `PROPOSED_NOT_ACTIVATED` and `NOT_PREPARED`. The Unit 29 content-review digest is unchanged. A later activation of this corrected text would still require final curriculum audit, independent verification, and an explicit Product Owner instruction before Unit 29 could leave `NOT_PREPARED`. That later activation would not install the unit, accept the mapping, pass a final audit, or approve release. Activating the withdrawn “unavailable” sentence would contradict `obj-u29-punctuation`.

If revised again: any further change to the decision text must be written before activation. Sending punctuation back to optional would be a curriculum change. This packet does not make that change. The unit stays `NOT_PREPARED`.

### Product Owner disposition

Product Owner disposition:
Product Owner name:
Product Owner date:
Product Owner signature:

## Unit 30

Recommendation: ACTIVATE

This recommendation is not an activation.

### Exact governing clause

MA2-178 and Amendment 1 section 3. The proposal records: “Letter names are introduced in Lesson 1, section 1.5. Lesson 30 only reviews and applies them for abbreviations. Unit 30 must not claim to introduce letter names for the first time.” It also records: “Lesson 30 includes a dictionary-use subtopic. That subtopic is not named in the current schedule.” The proposed resolution, if later activated, would say Unit 1 first introduces letter names, Unit 30 reviews and applies them, and Unit 30 dictionary use may be book-directed or separately sourced and cannot be invented.

### Publisher-sequence issue

The public schedule’s required first-introduction line for unit 30 is “Abbreviations, names of Russian letters, and particles.” Letter names are placed on unit 30. Dictionary use is absent from that line.

### App sequence and pedagogical reason

`obj-u30-letter-names` is titled “Review the admitted table name” and binds required claim `letter-name-review`. The orientation step `u30-st-orient` says: “Unit 1 introduced the printed letter names. This unit reviews admitted table names and applies them only as review.” Dictionary use is original research synthesis on `obj-u30-dictionary`, required claim `dictionary-use`. It is not Penguin prose. The orientation also says dictionary-field layout is not practical dictionary use. Clearance requires a governed entry, not a field label.

The reference file supports that split. Lesson 1 section 1.5 is the names of the Russian letters. Lesson 30’s title includes names of Russian letters and points abbreviation reading back to section 1.5. Lesson 30 also has a dictionaries section. The public schedule line does not name dictionary use. The shipped dictionary objective remains original research synthesis on `obj-u30-dictionary`, not a copy of that book section. This comparison is not an activation. File continuity with the retained private-file hash is not established.

Unit 1’s shipped objectives identify printed letters and send pronunciation to the book. `u1-st-pronunciation` says: “This unit does not play model audio. Use the pronunciation notes in your book for each new letter.” Unit 1 does not contain an objective titled as a spoken letter-name inventory. Activation would accept Unit 30 as review rather than first introduction. It would not, by itself, prove that Unit 1 contains the full Lesson 1.5 letter-name inventory.

### Affected objectives and capabilities

- `obj-u30-letter-names` — required claim `letter-name-review`.
- `obj-u30-dictionary` — required claim `dictionary-use`. Gating true.
- `obj-u30-abbreviation` — required claim `abbreviation`.
- `obj-u30-particle` — required claim `particle`.
- `obj-u30-capstone` — required claim `capstone-reading`.
- Abbreviation-example, address-mark, and book objectives are in the unit and do not replace those claims.

### Current content digest and mapping-package digest

- Unit content-review digest: `ed3a589a8d91e48c49675840118ca2951ad752f9784b0c8eeb21a74f66285187`.
- Mapping-package digest: `5361634a4e364130af45cfe253e75c60882f293cd9079bd403373e12cfde6505`.
- Review-package digest: `02a4ddb5c659428962eed21d738bfdccb58d70ad692746d65b5de4fc482cef18`.
- Mapping disposition remains `PENDING_CURRICULUM_REVIEWER`.
- Unit review status remains `IMPLEMENTED_PENDING_FINAL_AUDIT`.

### Risks and limitations

The public schedule lists letter names as unit 30 first-introduction scope. The shipped Unit 30 text denies first introduction and attributes printed letter names to Unit 1. The owner can reject that attribution if Unit 1’s printed-letter work is not the Lesson 1.5 inventory. Dictionary use is an addition to the public line. It is sourced as original synthesis and remains pending final audit. The owned-edition page range 368–377 is a proposed observation. The proposal’s statement that dictionary use, abbreviations, letter-name application, particles, and capstone reading were unsupported is historical for that file. Those required claims are now implemented and are not approved.

### Consequences

If activated later: a subsequent explicit Product Owner instruction, after final curriculum audit and independent verification, would still be required before production validation could stop treating Unit 30 as an unresolved variance. Content that stays complete could then leave `NOT_PREPARED` and become `CONTENT_COMPLETE_REVIEW_PENDING`. The unit would still be uninstalled, unmapped, unaudited, and outside any release approval. Activation would accept the review boundary and the separately sourced dictionary objective. It would not install letter-name audio or approve the mapping.

If rejected: Unit 30 stays a release-blocking variance. Restoring letter names as a first introduction inside Unit 30, or removing dictionary use, would be a curriculum change and would reopen `letter-name-review` or `dictionary-use`.

If revised: the decision text must be rewritten before any activation. A revision that keeps dictionary use but returns letter names to first introduction in Unit 30 must change the shipped review wording. This packet does not make that change. The unit stays `NOT_PREPARED`.

### Product Owner disposition

Product Owner disposition:
Product Owner name:
Product Owner date:
Product Owner signature:

## What this packet does not do

No variance is activated. No mapping is accepted. No review stage is advanced. No unit is installed. The candidate is not frozen. AT-031 is not executed. The premium product-design blueprint is not implemented. Public release stays `NOT_APPROVED`.
