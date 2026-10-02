# Product Owner edition-verification decision sheet

Status: APPLIED_IN_OWNER_MAPPING_AND_VARIANCE_ACCEPTANCE

The written disposition is recorded in `OWNER_MAPPING_AND_VARIANCE_ACCEPTANCE.md`. That acceptance is private mapping comparison and an activated pedagogical boundary. It is not qualified curriculum review and it does not approve release. Reviewer identity, qualification, date, digest acceptance, and signature stay blank. `owned_copy_verified` stays blank. `isbn_observation` stays `UNRESOLVED`. Mapping disposition stays `PENDING_CURRICULUM_REVIEWER`. Historical proposal files stay `PROPOSED_NOT_ACTIVATED`.

The repository source digest is not printed here. This file is an input to that digest. The live value is `sourceDigest` on the unfrozen candidate and `repositorySourceDigest` in `audit/owner-governance/FINAL_AUDIT_PREPARATION_MANIFEST.json`.

## Verified edition identity and file hash

Observed from the three-part reference file. PDF indices 2 and 4 are blank, so no copyright year was read. The introduction signature is the only year reading.

| Fact | Observation |
|---|---|
| Title | The New Penguin Russian Course: A Complete Course for Beginners |
| Author | Nicholas J. Brown |
| Imprint | Penguin Books |
| Year | Introduction signed Nicholas J. Brown, SSEES, London, 1996. Not a copyright-page year. |
| ISBN | Back cover prints `0-14-012041-6`. The barcode digits read `9780140120417`. Unresolved. The worksheet isbn column stays blank. |
| PDF extent | 528 PDF indices: parts 1–176, 177–352, and 353–528 |
| Printed pages | For the lesson body, printed page N is PDF index N+12. PDF index 528 is the back cover. Grammatical tables begin at printed page 378. |
| Contents crop | The contents page for lessons 11–27 does not show the right-edge page numbers. Those boundaries were taken from the lesson-opening pages. |

File identity. None of these hashes is the retained private-file hash.

| File | SHA-256 |
|---|---|
| Part 1, PDF indices 1–176 | `de6715e5ff4c52394a5a68462fafbf1d9608906fe01704af73d44eceb4da1bcd` |
| Part 2, PDF indices 177–352 | `7bab8f041ed0a2f6f8e4a27c336c39c395b97b6fcd9d542781f755cef42ee304` |
| Part 3, PDF indices 353–528 | `72384a3305f64115ecf9b6028944ac06b40ca33705ff2b113f9e3ecb7eb3a1da` |
| Byte concatenation of the three parts | `4b3374d001f007a6dd92dfdc8f1a386d5b2591a102a166cf5c003642fdc793bc` |
| Retained private-file hash | `2bcc0c45fac4508adf283f4dd939a7e578cf2c1306fdadf4966300532beb6303` |

The byte-hash continuity test is rejected as proof of continuity. Splitting rewrites PDF object structure, so concatenation cannot reproduce the original PDF hash. Continuity is not established by binary identity. The mismatch is not evidence of altered instructional content. The part hashes, ordered ranges, 528-index extent, and uninterrupted page sequence are reference-file evidence only.

## Proposed mappings

The Product Owner accepted these printed-page boundaries for private comparison. Each mapping record is still `PENDING_CURRICULUM_REVIEWER`. No shared printed page was found at a lesson boundary. The obsolete Unit 25 range 307-321 is not a live boundary.

| Unit | Lesson | Printed pages | Discrepancy |
|---:|---|---|---|
| 1 | Lesson 1 | 1–11 | None on this copy |
| 2 | Lesson 2 | 12–23 | None on this copy |
| 3 | Lesson 3 | 24–35 | None on this copy |
| 4 | Lesson 4 | 36–45 | None on this copy |
| 5 | Lesson 5 | 46–55 | None on this copy |
| 6 | Lesson 6 | 56–67 | None on this copy |
| 7 | Lesson 7 | 68–80 | None on this copy |
| 8 | Lesson 8 | 81–91 | None on this copy |
| 9 | Lesson 9 | 92–106 | None on this copy |
| 10 | Lesson 10 | 107–121 | None on this copy |
| 11 | Lesson 11 | 122–132 | None on this copy |
| 12 | Lesson 12 | 133–148 | None on this copy |
| 13 | Lesson 13 | 149–161 | None on this copy |
| 14 | Lesson 14 | 162–170 | None on this copy |
| 15 | Lesson 15 | 171–187 | None on this copy |
| 16 | Lesson 16 | 188–200 | None on this copy |
| 17 | Lesson 17 | 201–214 | None on this copy |
| 18 | Lesson 18 | 215–229 | None on this copy |
| 19 | Lesson 19 | 230–239 | None on this copy |
| 20 | Lesson 20 | 240–256 | None on this copy |
| 21 | Lesson 21 | 257–268 | Accepted. Packaged note uses 257-268. Prior proposal 259-270 is obsolete. |
| 22 | Lesson 22 | 269–282 | Accepted. Packaged note uses 269-282. Prior proposal 271-284 is obsolete. |
| 23 | Lesson 23 | 283–293 | Accepted. Packaged note uses 283-293. Prior proposal 285-295 is obsolete. |
| 24 | Lesson 24 | 294–304 | Accepted. Packaged note uses 294-304. Prior proposal 296-306 is obsolete. |
| 25 | Lesson 25 | 305–319 | Accepted. Packaged note uses 305-319. Prior proposal 307-321 is obsolete. |
| 26 | Lesson 26 | 320–331 | Accepted. The obsolete 307-321 range is not a shared page with 320-331. |
| 27 | Lesson 27 | 332–342 | None on the page range. Topic now names types and stress. |
| 28 | Lesson 28 | 343–354 | None on this copy |
| 29 | Lesson 29 | 355–367 | Page range matches. Topic no longer says punctuation is optional. |
| 30 | Lesson 30 | 368–377 | None on the page range. Letter names here review Lesson 1 section 1.5. |

Lesson titles on this copy match the public schedule titles at topic level. Topics in the worksheets are the proposed descriptions, not book prose.

## Activated variance wording

Production validation records Units 27, 29, and 30 as activated pedagogical boundaries. That activation is not qualified review. Historical proposal files stay `PROPOSED_NOT_ACTIVATED`.

### Unit 27

Disposition: ACTIVATE the passive-only boundary.

Unit 27 teaches passive participles only. Past passive material is required. Present passive material is optional and non-gating. Active participles first appear in Unit 29. The public schedule line remains “Participles: types and stress.” The reference file uses that title. Its body teaches past passive formation and stress as the main lesson, marks present passive as an extra section, and does not teach active participle formation.

### Unit 29

Disposition: ACTIVATE the corrected wording below. The withdrawn unavailable sentence does not govern.

Corrected proposed-resolution item 7, in full:

Unit 29 punctuation is in scope and is not unavailable. The shipped instruction on `obj-u29-punctuation`, claim `participle-punctuation`, steps `u29-st-teach-punctuation-2` and `u29-st-rem-punctuation`, is: when a participial phrase stands before the word it modifies, commas are not used, except when that word is a personal pronoun or the participial phrase states a cause. The paired admitted examples distinguish a phrase after the modified word, set off by commas on both sides, from a phrase before the modified word, which is not set off. The reference-file lesson includes a punctuation section marked extra. That book label does not make the shipped objective unavailable. The withdrawn sentence “Unit 29 punctuation remains optional and unavailable until sufficiently sourced and reviewed” is not the governing text.

Effects: the unresolved-variance set no longer contains Unit 29. Unit 29 readiness is `CONTENT_COMPLETE_REVIEW_PENDING`. The unit stays uninstalled, mapping-pending, and unaudited. Activation does not approve release.

### Unit 30

Disposition: ACTIVATE the letter-name-review and separately sourced dictionary-use boundary.

Letter names are introduced in Lesson 1, section 1.5, of the reference file. Lesson 30 reviews and applies those names for abbreviations and does not introduce them for the first time. The public schedule places “names of Russian letters” on unit 30. Dictionary use is absent from that public line. The reference file has a dictionaries section in Lesson 30. The shipped dictionary objective is original research synthesis on `obj-u30-dictionary` and is not a copy of that section. Unit 1’s shipped letter work does not by itself prove the full Lesson 1.5 inventory.

## Applied disposition

The written disposition of 2026-10-02 is applied in `OWNER_MAPPING_AND_VARIANCE_ACCEPTANCE.md`. Qualified-reviewer identity, qualification, date, digest acceptance, and signature stay blank. Public release remains `NOT_APPROVED`.

Product Owner name: Justin Kulka
Product Owner date: 2026-10-02
Product Owner signature:
Reviewer identity:
Reviewer qualification:
Review date:
Reviewed version digest:
Reviewer signature:

## Digest identities

Live content-review, mapping-package, and review-package digests are regenerated in the readiness reconciliation and the review packets. This sheet does not print the repository source digest, because this file is an input to that digest. Units 1–20 curriculum source commits are unchanged. Units 21–30 use the commit that contains the mapping-note edits, recorded on the review packets.

Curriculum source commits for Units 1–20: Units 1–2 `4a6c21da07b515df226a090cc0ff6b1ff4a78aa1`; Units 3–5 and 11–15 `b22fa0cca895a677386a77d03a63301f7454aa07`; Units 6–10 `ae777d85fe3616fc8e065890201d40c241d113bc`; Units 16–20 `100ed31a3e3a8fee3ce6b7f8fc186a7ff1422cee`.
