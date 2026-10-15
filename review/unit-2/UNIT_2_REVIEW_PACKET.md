# Unit 2 linguistic and owned-edition mapping packet

This packet prepares review. It records an unsigned structural mapping proposal. It does not record a qualified review, an approved edition alignment, or an install decision.

The owned-edition mapping worksheet separates observed edition facts, the proposed lesson and printed page range, unresolved edition questions, and matters that still require qualified linguistic review. ISBN, owned-copy verification, variance, reviewer identity, qualification, date, and approval stay blank. The disposition remains PENDING_CURRICULUM_REVIEWER.

Unit 2 remains in development. Its readiness is content-complete and review-pending. The Lesson 2 relationship is a proposed, unsigned mapping. It is not an approved edition alignment.

## Package identity

- Schema version: 3
- Application: dev.russiancompanion
- Version: 2.0.0-dev (5)
- Curriculum source commit: 4f5cb8024d3f68a37052fdf85b5755bffa2a0904
- Unit content-review SHA-256: 4fd67bff0431a11416115de023e076c52b7879304fc4a98b508d871170703e9c
- Mapping-package SHA-256: def2280a90229cec52750f12cb7dc2f7564df8508c113a69cf6ed585cee1c0c6
- Pack content-review SHA-256: 0bbb988b2700850392c3d396668b94e0dc30f1bf87831e739454489e01afbf3e
- Review-package SHA-256: 0c6bbc7ac312c0ecab9426e24ac8b7401cb795aed937bb9357e3f548c58ef72b
- Repository source SHA-256: 7f9b118edf1ff97c288b236463d0676939f058d7acc189bf672ae7c9183979c6

`unitContentReviewDigest` binds this unit's instructional curriculum. It covers this unit and the reachable instructional records, relationships, shared dependencies, sources, rights, assets, and advancement rule. It does not cover any other unit. Recording a review status, reviewer signature, mapping approval, install state, or readiness does not change it.

`mappingPackageDigest` binds the proposed edition-mapping facts for Unit 2: edition identity, lesson, printed page range, topic description, observation source, ISBN observation state, and the pending disposition. Reviewer identity, qualification, date, notes, correction notes, and signature stay outside that digest.

`packContentReviewDigest` is the repository aggregate of instructional curriculum. It is not an approval of this unit, and it is not part of the review-package digest.

`reviewPackageDigest` identifies this complete pending review package. It binds the ordered instructional records and the mapping-package digest. Changing a proposed page range or topic changes the mapping-package digest and therefore this package digest.

`repositorySourceDigest` binds repository and release inputs. Recording an approval later changes that repository hash. That change does not replace the signed content-review digest or the mapping-package digest.

The marker scan used by this package detects only a fixed list of known prohibited mapping-input markers. It is not comprehensive copyright detection and cannot recognize every copy or close paraphrase of protected material.

## Unit

number=2
title=Handwriting and additional pronunciation
topicSummary=Content-complete development unit after Unit 1: printed-letter review, then book-directed handwriting and additional pronunciation. Verified handwriting images and mapped pronunciation audio are not in this app. Qualified review and the owned-edition mapping are still pending.
installState=DEVELOPMENT
readiness=CONTENT_COMPLETE_REVIEW_PENDING
mappingStatus=PENDING_CURRICULUM_REVIEWER
mappingNote=Proposed Lesson 2 mapping from the publisher's public table of contents: handwriting recognition and additional pronunciation. Owned-edition comparison is not signed, so this unit is not labeled aligned.
penguinLessonField=2
prerequisites=1
estimatedMinutes=94

## Not yet approved

No reviewer has signed this packet. Reviewer identity, qualification, review date, disposition, and reviewed digest are blank. Do not treat this development unit as reviewed, installable, or installed.

## Records

### UNIT_REVIEW `rev-pending-unit-2`

- Objectives: none
- Sessions: none
- Source: none
- Rights: none
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 878e83ce6de60b5e2c3be801012eec843431b96296c899eccc111a719fa81676

status=IMPLEMENTED_PENDING_FINAL_AUDIT
reviewer=
reviewerQualification=
packVersion=
reviewedContentDigest=
curriculumMappingId=
notes=No qualified reviewer has signed Unit 2.

### UNIT `unit-2`

- Objectives: obj-u2-review, obj-u2-book
- Sessions: u2-s-orient, u2-s-handwriting, u2-s-review, u2-s-pronunciation, u2-s-reading, u2-s-record, u2-s-book, u2-s-check
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: e4377c50779016cd88457fbc5e988d54eee0589387229c04d2527b1366932823

number=2
title=Handwriting and additional pronunciation
topicSummary=Content-complete development unit after Unit 1: printed-letter review, then book-directed handwriting and additional pronunciation. Verified handwriting images and mapped pronunciation audio are not in this app. Qualified review and the owned-edition mapping are still pending.
installState=DEVELOPMENT
readiness=CONTENT_COMPLETE_REVIEW_PENDING
mappingStatus=PENDING_CURRICULUM_REVIEWER
mappingNote=Proposed Lesson 2 mapping from the publisher's public table of contents: handwriting recognition and additional pronunciation. Owned-edition comparison is not signed, so this unit is not labeled aligned.
penguinLessonField=2
prerequisites=1
estimatedMinutes=94

### ADVANCEMENT `unit-2-advancement`

- Objectives: none
- Sessions: none
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 2326b261ff9ec8365dca3a7a1984b6d37941c7c51fc01d4df641e9ef285201a9

requireInstruction=true
requireBookSelfAttestation=true
requireAppChecks=true
minimumDistinctChecks=2
requireDueReview=true
tutorOptional=true

### OBJECTIVE `obj-u2-review`

- Objectives: obj-u2-review
- Sessions: none
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 8ff9f77cc9e2ff815f9ddee8a587a65b63197e3f3c304c02586f7aeefa6dc43b

title=Review Unit 1 printed letters
kind=RECOGNITION
unitNumber=2
instruction=u2-st-review
examples=ex-print-letter-0411,ex-letter-0412,ex-letter-0423,ex-letter-0425
practice=u2-st-review-guided
checks=u2-i-в-not-b,u2-i-у-not-y,u2-i-х-not-x,u2-i-print-б,u2-i-я-identity
remediation=u2-st-review

### OBJECTIVE `obj-u2-book`

- Objectives: obj-u2-book
- Sessions: none
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: b643b0394a297a024a4462d65e56a1ecc5c875c13b2c97fff26de0975e646419

title=Complete Lesson 2 work in the owned book
kind=SELF_ATTESTATION
unitNumber=2
instruction=u2-st-book
examples=
practice=
checks=
remediation=

### SESSION `u2-s-orient`

- Objectives: obj-u2-review
- Sessions: u2-s-orient
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 758db582dfbd5ee25c4f30890114e59a509fab8b60898bd80fe1c92c647e83d7

title=What this unit is for
estimatedMinutes=6
objectives=obj-u2-review
steps=u2-st-orient

### STEP `u2-st-orient`

- Objectives: obj-u2-review
- Sessions: u2-s-orient
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: cbef51d28ee9ed1b3e094e3a188cee70ce03630863128cc537dd90d5989065d1

kind=ORIENT
title=Unit 2 — Book handwriting and letter review
body=Proposed Lesson 2 continues handwritten forms and pronunciation in the book. This unit reviews printed letters from Unit 1. Verified handwriting images and mapped pronunciation audio are not in the app, so those parts stay in the book you own.
dominantAction=Continue
references=
items=
examples=
unavailableReason=
bookDirection=Open Lesson 2 in your copy for handwritten forms and the author's pronunciation notes.
companionDirection=
remediationStepId=

### SESSION `u2-s-handwriting`

- Objectives: obj-u2-book
- Sessions: u2-s-handwriting
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 6043bda84c8aa23768a82260dcfc9b334f8f88be4f930e28494ef920a6a01780

title=Handwritten forms
estimatedMinutes=15
objectives=obj-u2-book
steps=u2-st-hw

### STEP `u2-st-hw`

- Objectives: obj-u2-book
- Sessions: u2-s-handwriting
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: f8cbc09621d354fd9313aa268397cd006547c029c97a2efa0dd4279d266d0d3d

kind=BOOK
title=Handwritten forms stay in the owned book
body=Verified handwriting images are not in this app. Use Lesson 2 in the book you own to see handwritten forms, and copy them onto paper. This app does not describe those forms and does not grade the page.
dominantAction=Continue
references=ref-limits
items=
examples=
unavailableReason=
bookDirection=Owned-book assignment for Lesson 2: printed pages 12-23. PDF indexes 24-35 are file locators, not printed pages. Observed section locators: §2.1 p12 (PDF 24); §2.2 p16 (PDF 28); §2.3 p17 (PDF 29); §2.4 p18 (PDF 30); §2.5 p18 (PDF 30); §2.6 p18 (PDF 30); §2.7 p19 (PDF 31); §2.8 p20 (PDF 32); §2.9 p21 (PDF 33); §2.10 p21 (PDF 33); §2.11 p21 (PDF 33); §2.12 p22 (PDF 34). Observed exercise locators: 2/1 p23/PDF35; 2/2 p23/PDF35; 2/3 p23/PDF35. These locators are pending reviewer assignment and are not an approved mapping. Do the work in the copy you own. Book activity is not a demonstrated app check.
companionDirection=
remediationStepId=

### SESSION `u2-s-review`

- Objectives: obj-u2-review
- Sessions: u2-s-review
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 72e85c75d37f528c70cddd36d50ab3806c4f912af11d1f62ac39f2875f3ef2f5

title=Unit 1 letter review
estimatedMinutes=10
objectives=obj-u2-review
steps=u2-st-review,u2-st-review-guided,u2-st-review-v,u2-st-review-u,u2-st-review-h

### STEP `u2-st-review`

- Objectives: obj-u2-review
- Sessions: u2-s-review
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 0aa2e68716cade7cc7e32df54b764d587accd1e3626d4d0a7890415c38d928f6

kind=TEACH
title=Keep letter identities available
body=Review the printed letters from Unit 1, especially Latin look-alikes and print pairs. Read this as a review-pending explanation. Worked examples come before the guided choice, and the independent check is separate. A wrong answer returns to this contrast. Activity is not demonstrated ability.
dominantAction=Continue
references=ref-cyrillic-print,ref-confusable
items=
examples=ex-letter-0412,ex-letter-0423,ex-letter-0425,ex-print-letter-0411
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u2-st-review-guided`

- Objectives: obj-u2-review
- Sessions: u2-s-review
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 91552552bc31d2af32a40dd173892a4bf7cf3f178e893156bcb520a47169da72

kind=GUIDED
title=Match the printed pair for Б
body=Choose the uppercase and lowercase print pair for the same letter.
dominantAction=Continue
references=
items=u2-i-print-б
examples=ex-print-letter-0411
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=u2-st-review

### STEP `u2-st-review-v`

- Objectives: obj-u2-review
- Sessions: u2-s-review
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 90968e8054569a02794a8817e9ac9f4586a577c543cf1a06196bdb099a00e45b

kind=CHECK
title=В is not B
body=Identity check.
dominantAction=Continue
references=
items=u2-i-в-not-b
examples=ex-letter-0412
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=u2-st-review

### STEP `u2-st-review-u`

- Objectives: obj-u2-review
- Sessions: u2-s-review
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 0295a4227d1ef85e3a4eceffc85fe25f3ddddbca64146defa700ce0ed3cb8832

kind=CHECK
title=У is not Y
body=Identity check.
dominantAction=Continue
references=
items=u2-i-у-not-y
examples=ex-letter-0423
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=u2-st-review

### STEP `u2-st-review-h`

- Objectives: obj-u2-review
- Sessions: u2-s-review
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: ab7a816f6c40d8eeb4a252c5b15d0cf36463ad28aa9d1940310b42016c5ef41b

kind=CHECK
title=Х is not X
body=Identity check.
dominantAction=Continue
references=
items=u2-i-х-not-x
examples=ex-letter-0425
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=u2-st-review

### SESSION `u2-s-pronunciation`

- Objectives: obj-u2-book
- Sessions: u2-s-pronunciation
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 0d60fd2ce4c854e59ef565361e11ca585c9f30a2e6cc7a13de3e9e307b2ecb49

title=Additional pronunciation
estimatedMinutes=10
objectives=obj-u2-book
steps=u2-st-pron

### STEP `u2-st-pron`

- Objectives: obj-u2-book
- Sessions: u2-s-pronunciation
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 9413f8d7a9619b204331397205271a6248df6df1085ef1ef204f2ca2acc704f8

kind=BOOK
title=Additional pronunciation stays in the owned book
body=No licensed pronunciation audio is mapped to Unit 2. Use the Lesson 2 pronunciation notes in the book you own. This app does not state those sounds and does not score a recording.
dominantAction=Continue
references=ref-pronunciation
items=
examples=
unavailableReason=
bookDirection=Owned-book assignment for Lesson 2: printed pages 12-23. PDF indexes 24-35 are file locators, not printed pages. Observed section locators: §2.1 p12 (PDF 24); §2.2 p16 (PDF 28); §2.3 p17 (PDF 29); §2.4 p18 (PDF 30); §2.5 p18 (PDF 30); §2.6 p18 (PDF 30); §2.7 p19 (PDF 31); §2.8 p20 (PDF 32); §2.9 p21 (PDF 33); §2.10 p21 (PDF 33); §2.11 p21 (PDF 33); §2.12 p22 (PDF 34). Observed exercise locators: 2/1 p23/PDF35; 2/2 p23/PDF35; 2/3 p23/PDF35. These locators are pending reviewer assignment and are not an approved mapping. Do the work in the copy you own. Book activity is not a demonstrated app check.
companionDirection=
remediationStepId=

### SESSION `u2-s-reading`

- Objectives: obj-u2-book
- Sessions: u2-s-reading
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 43a098fdb089a0392a194c6df148f53741be961fee3c2f91ed4affa421395639

title=Reading in the book
estimatedMinutes=12
objectives=obj-u2-book
steps=u2-st-reading

### STEP `u2-st-reading`

- Objectives: obj-u2-book
- Sessions: u2-s-reading
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 254b3c15bb9d75b1e17f1e3a122c6eec28426ea004fbefa94e8fa531685d4771

kind=TEACH
title=Read what the book provides
body=Fluency practice in this unit is book-directed. Re-run letter recognition if a form is still slow.
dominantAction=Continue
references=ref-decoding
items=
examples=
unavailableReason=
bookDirection=Read the Lesson 2 material in your copy aloud. Mark difficult letters for review.
companionDirection=
remediationStepId=

### SESSION `u2-s-record`

- Objectives: obj-u2-book
- Sessions: u2-s-record
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: b7d73027295cbd4fcf4aebb3c7a0272cf9a60a6bf41572ab5c2a68ccd182143f

title=Self-record
estimatedMinutes=8
objectives=obj-u2-book
steps=u2-st-record

### STEP `u2-st-record`

- Objectives: obj-u2-book
- Sessions: u2-s-record
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 8c8a1e691cdf3a3f268e8a90628a340279779de475ba0964e9a68837b3e26685

kind=SPEAK
title=Optional recording without a score
body=You may record yourself reading from your book. Play the take back. The app never transcribes or scores it.
dominantAction=Continue
references=
items=
examples=
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### SESSION `u2-s-book`

- Objectives: obj-u2-book
- Sessions: u2-s-book
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 69f6268b22471f5c52b0e52aa45e840e133ad57e0a3e877171ab8916bf4b0e99

title=Work in your book
estimatedMinutes=25
objectives=obj-u2-book
steps=u2-st-book

### STEP `u2-st-book`

- Objectives: obj-u2-book
- Sessions: u2-s-book
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: d7e0d6896b49bfc1a5ebf4bfa9e22faa0ac832618be0ac77867c78907ea1d641

kind=BOOK
title=Lesson 2 in your copy
body=Complete Lesson 2 handwriting and pronunciation work in the edition you own. Self-attest when finished.
dominantAction=Continue
references=
items=
examples=
unavailableReason=
bookDirection=Owned-book assignment for Lesson 2: printed pages 12-23. PDF indexes 24-35 are file locators, not printed pages. Observed section locators: §2.1 p12 (PDF 24); §2.2 p16 (PDF 28); §2.3 p17 (PDF 29); §2.4 p18 (PDF 30); §2.5 p18 (PDF 30); §2.6 p18 (PDF 30); §2.7 p19 (PDF 31); §2.8 p20 (PDF 32); §2.9 p21 (PDF 33); §2.10 p21 (PDF 33); §2.11 p21 (PDF 33); §2.12 p22 (PDF 34). Observed exercise locators: 2/1 p23/PDF35; 2/2 p23/PDF35; 2/3 p23/PDF35. These locators are pending reviewer assignment and are not an approved mapping. Do the work in the copy you own. Book activity is not a demonstrated app check.
companionDirection=
remediationStepId=

### SESSION `u2-s-check`

- Objectives: obj-u2-review, obj-u2-book
- Sessions: u2-s-check
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 55ff52850ce3938b8c08191effbc0430f81fb062980e50b8c34e7c4186184aa3

title=Unit 2 letter check
estimatedMinutes=8
objectives=obj-u2-review,obj-u2-book
steps=u2-st-check,u2-st-anki-direction,u2-st-binder-direction,u2-st-tutor-direction,u2-st-cumulative-direction

### STEP `u2-st-check`

- Objectives: obj-u2-review, obj-u2-book
- Sessions: u2-s-check
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: cadb1ec2ba8c4bf85309116626d341dbd4794f940bcaeac43a88ed2bc7828159

kind=CHECK
title=Reviewed letter identities
body=Closed-response items about printed letters from Unit 1.
dominantAction=Continue
references=
items=u2-i-в-not-b,u2-i-print-б,u2-i-я-identity
examples=
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=u2-st-review

### STEP `u2-st-anki-direction`

- Objectives: obj-u2-review, obj-u2-book
- Sessions: u2-s-check
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: cb833212534ec577644b4fa8fd49830b1a09ea600922018d3d5a9bcefb4ecc82

kind=ANKI
title=Unit 2 AnkiDroid direction
body=Unit 2 AnkiDroid self-attestation uses only cards you are allowed to make. Binder: write one form from this unit that you missed and the correction you will use. Tutor conversation is optional and is not a demonstrated check. Cumulative review revisits completed objectives from earlier units. Activity is not demonstrated ability. Open the offline grammar reference for this unit when one is linked.
dominantAction=Self-attest
references=
items=
examples=
unavailableReason=
bookDirection=
companionDirection=Self-attest AnkiDroid work. It remains user-reported and does not demonstrate a check.
remediationStepId=

### STEP `u2-st-binder-direction`

- Objectives: obj-u2-review, obj-u2-book
- Sessions: u2-s-check
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 842f86737871fda66460593a9ee898bcd30e39cc17d581ee44f16fa2593940ee

kind=BINDER
title=Unit 2 binder prompt
body=Unit 2 AnkiDroid self-attestation uses only cards you are allowed to make. Binder: write one form from this unit that you missed and the correction you will use. Tutor conversation is optional and is not a demonstrated check. Cumulative review revisits completed objectives from earlier units. Activity is not demonstrated ability. Open the offline grammar reference for this unit when one is linked.
dominantAction=Continue
references=
items=
examples=
unavailableReason=
bookDirection=
companionDirection=A binder prompt is one specific note, not a score.
remediationStepId=

### STEP `u2-st-tutor-direction`

- Objectives: obj-u2-review, obj-u2-book
- Sessions: u2-s-check
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 5b1148a44f8fb1355f056ba4865eb47ae118d7def938e8e23b65d37ea332b140

kind=TUTOR
title=Unit 2 optional tutor
body=Unit 2 AnkiDroid self-attestation uses only cards you are allowed to make. Binder: write one form from this unit that you missed and the correction you will use. Tutor conversation is optional and is not a demonstrated check. Cumulative review revisits completed objectives from earlier units. Activity is not demonstrated ability. Open the offline grammar reference for this unit when one is linked.
dominantAction=Continue
references=
items=
examples=
unavailableReason=
bookDirection=
companionDirection=Tutor work is optional and does not count as demonstrated ability.
remediationStepId=

### STEP `u2-st-cumulative-direction`

- Objectives: obj-u2-review, obj-u2-book
- Sessions: u2-s-check
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 253a49b6f9a660aa7b81dd8892da8edd098b772ec456924ed89cfbb292fb815e

kind=ORIENT
title=Unit 2 cumulative review
body=Unit 2 AnkiDroid self-attestation uses only cards you are allowed to make. Binder: write one form from this unit that you missed and the correction you will use. Tutor conversation is optional and is not a demonstrated check. Cumulative review revisits completed objectives from earlier units. Activity is not demonstrated ability. Open the offline grammar reference for this unit when one is linked.
dominantAction=Continue
references=
items=
examples=
unavailableReason=
bookDirection=Owned-book assignment for Lesson 2: printed pages 12-23. PDF indexes 24-35 are file locators, not printed pages. Observed section locators: §2.1 p12 (PDF 24); §2.2 p16 (PDF 28); §2.3 p17 (PDF 29); §2.4 p18 (PDF 30); §2.5 p18 (PDF 30); §2.6 p18 (PDF 30); §2.7 p19 (PDF 31); §2.8 p20 (PDF 32); §2.9 p21 (PDF 33); §2.10 p21 (PDF 33); §2.11 p21 (PDF 33); §2.12 p22 (PDF 34). Observed exercise locators: 2/1 p23/PDF35; 2/2 p23/PDF35; 2/3 p23/PDF35. These locators are pending reviewer assignment and are not an approved mapping. Do the work in the copy you own. Book activity is not a demonstrated app check.
companionDirection=Unit 2 AnkiDroid self-attestation uses only cards you are allowed to make. Binder: write one form from this unit that you missed and the correction you will use. Tutor conversation is optional and is not a demonstrated check. Cumulative review revisits completed objectives from earlier units. Activity is not demonstrated ability. Open the offline grammar reference for this unit when one is linked.
remediationStepId=

### EXAMPLE `ex-print-letter-0411`

- Objectives: obj-u2-review
- Sessions: u2-s-review
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 164f0102c86cb102e8b5b6fb4175132755f2e1110483b1fc64ce743ed5741c30

kind=PRINT_PAIR
letterId=letter-0411
display=Б / б
note=Б and б are the same letter in two print cases.
objectiveId=obj-u2-review
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-letter-0412`

- Objectives: obj-u1-confusable
- Sessions: u2-s-review
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 4d235e3fbf055d86684491228ae7f19f2c2b9dfeb5ec67b7b8ea60f310ab11c9

kind=CONFUSABLE_PAIR
letterId=letter-0412
display=В / в
note=Printed pair В в. Treat the Cyrillic identity, not a Latin look-alike.
objectiveId=obj-u1-confusable
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-letter-0423`

- Objectives: obj-u1-confusable
- Sessions: u2-s-review
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 8060cd5f4510b5461f793870d2a68a78c0bd9bb7645652aaa6e4235cc12301bd

kind=CONFUSABLE_PAIR
letterId=letter-0423
display=У / у
note=Printed pair У у. Treat the Cyrillic identity, not a Latin look-alike.
objectiveId=obj-u1-confusable
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-letter-0425`

- Objectives: obj-u1-confusable
- Sessions: u2-s-review
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 089077bb1d39a90d5d57869e26adf78422996132dcaeeabdf6cde127306c5e72

kind=CONFUSABLE_PAIR
letterId=letter-0425
display=Х / х
note=Printed pair Х х. Treat the Cyrillic identity, not a Latin look-alike.
objectiveId=obj-u1-confusable
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### GUIDED_AND_INDEPENDENT_CHECK `u2-i-print-б`

- Objectives: obj-u2-review
- Sessions: u2-s-review, u2-s-check
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 000fe3bdfaf1e3375514d6e1088c019dc1385e3dabace00d18c4c3d162bdd107

kind=PRINT_PAIR
language=en
prompt=Which pair is the same letter in uppercase and lowercase print?
promptLetter=Б
options=Б / б | В / б | Б / в
answer=Б / б
feedback=Б / б is one letter. В is a different letter.
objectiveId=obj-u2-review
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### INDEPENDENT_CHECK `u2-i-в-not-b`

- Objectives: obj-u2-review
- Sessions: u2-s-review, u2-s-check
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: fb17d81342f0191fad32bde447c5ddc26c3e343607bae53fd4c1c4b107859413

kind=CONFUSABLE
language=en
prompt=This letter looks like Latin B. What is it?
promptLetter=В
options=Latin B | Cyrillic В | Cyrillic Б
answer=Cyrillic В
feedback=В is not Latin B and it is not Cyrillic Б.
objectiveId=obj-u2-review
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### INDEPENDENT_CHECK `u2-i-у-not-y`

- Objectives: obj-u2-review
- Sessions: u2-s-review
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: bd27a652a7c60302217dfadc5a7c128f83b61fb5e7fe12c0e45b841ae1fe3a3b

kind=CONFUSABLE
language=en
prompt=This letter looks like Latin Y. What is it?
promptLetter=У
options=Latin Y | Cyrillic У | Cyrillic И
answer=Cyrillic У
feedback=У is a Cyrillic vowel letter, not Latin Y.
objectiveId=obj-u2-review
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### INDEPENDENT_CHECK `u2-i-х-not-x`

- Objectives: obj-u2-review
- Sessions: u2-s-review
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 611ad62524dc5b722ab5b92f29f0d3cca9333b5f4b4d59a3b6064d524598bdd6

kind=CONFUSABLE
language=en
prompt=This letter looks like Latin X. What is it?
promptLetter=Х
options=Latin X | Cyrillic Х | Cyrillic К
answer=Cyrillic Х
feedback=Х is a Cyrillic letter, not Latin X.
objectiveId=obj-u2-review
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### INDEPENDENT_CHECK `u2-i-я-identity`

- Objectives: obj-u2-review
- Sessions: u2-s-check
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: cb94ce8360718ee0470122f1a64d5c9d62ae3baaa60f71ef8e0f5944567f6272

kind=LETTER_IDENTITY
language=en
prompt=Which printed letter is shown?
promptLetter=Я
options=Cyrillic Я | Cyrillic Р | Cyrillic А
answer=Cyrillic Я
feedback=Я is an iotated vowel letter. It is not Р or А.
objectiveId=obj-u2-review
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### REFERENCE `ref-limits`

- Objectives: none
- Sessions: u2-s-handwriting
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: OPERATIONAL_GUIDANCE
- Current review status: NOT_REQUIRED
- Content hash: f261198251db018339d57aeb6039bea1b96cd805543f7b8b7377767be613e9d3

title=App limitations
summary=Offline only. No internet, no account, no AI, no cloud.
detail=Unrelated catalog audio is not assigned to Units 1–2. The app does not score handwriting or pronunciation. Handwriting study uses your book until verified images exist. Unit 2 reviews Unit 1 letters and does not teach Lesson 3 grammar. Public release stays blocked until review and device evidence exist.
classification=OPERATIONAL_GUIDANCE
introducedInUnit=1
searchTerms=app limitations
reviewStatus=NOT_REQUIRED

### REFERENCE `ref-cyrillic-print`

- Objectives: none
- Sessions: u2-s-review
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: LINGUISTIC_INSTRUCTION
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 92b4816279b8312cf8258136eaa5ee847054f030209cb59728774c4c7d50a7f6

title=Cyrillic print forms
summary=33 printed uppercase/lowercase pairs.
detail=Use the letter board grouped by the Unit 1 sequence. English sound notes are pending review. No example words are attached.
classification=LINGUISTIC_INSTRUCTION
introducedInUnit=1
searchTerms=cyrillic print forms
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### REFERENCE `ref-confusable`

- Objectives: none
- Sessions: u2-s-review
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: LINGUISTIC_INSTRUCTION
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 074a197cd1da4bbd087881103227a1968f7f836e8b4b647611bd6514b41cf0be

title=Confusable Latin look-alikes
summary=В Н Р С У Х can be misread by Latin-alphabet readers.
detail=Name the Cyrillic identity. Confirm sounds in your book.
classification=LINGUISTIC_INSTRUCTION
introducedInUnit=1
searchTerms=confusable latin look-alikes
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### REFERENCE `ref-pronunciation`

- Objectives: none
- Sessions: u2-s-pronunciation
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: LINGUISTIC_INSTRUCTION
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: bdd8e4bfbb90a13550ba5288e8c392bfb6c369b308ee788e4ee5684c58bbbfe9

title=Pronunciation guidance
summary=Model audio is used only when a file is licensed and mapped to the current objective.
detail=User recordings play at original speed. No ASR or score.
classification=LINGUISTIC_INSTRUCTION
introducedInUnit=2
searchTerms=pronunciation guidance
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### REFERENCE `ref-decoding`

- Objectives: none
- Sessions: u2-s-reading
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: LINGUISTIC_INSTRUCTION
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 123de65a267f8f5b4257bf3d26e35971a950ce1c2d0c76f9360683c79c6728e1

title=Staged decoding limits
summary=This pack decodes letters and print pairs only.
detail=Word, phrase, and sign reading wait for reviewed examples.
classification=LINGUISTIC_INSTRUCTION
introducedInUnit=1
searchTerms=staged decoding limits
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### LETTER `letter-0411`

- Objectives: obj-u1-letter-identity, obj-u2-review, obj-u1-letter-names
- Sessions: u2-s-review
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: e41c73836ed5c94ecf519b81699cf11182d1825c44e9c3e8751cb8c2881fd60e

uppercase=Б
lowercase=б
group=consonant-early
englishNote=A voiced bilabial stop. Pending review.
latinLookalike=
audioAssetId=
exampleWordId=
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### LETTER `letter-0412`

- Objectives: obj-u1-confusable
- Sessions: u2-s-review
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 6aff973de3666d5298ce7a72e5a5c847483575824f47194d5e9b73b1fd01ac62

uppercase=В
lowercase=в
group=confusable
englishNote=Looks like Latin B. It is not the /b/ letter. Pending review.
latinLookalike=B
audioAssetId=
exampleWordId=
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### LETTER `letter-0423`

- Objectives: obj-u1-confusable
- Sessions: u2-s-review
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 4d354a96402772a54d1dbc3366bec9b3a000f255d2d57a976d7eeb2a4f993044

uppercase=У
lowercase=у
group=confusable
englishNote=Looks like Latin Y. It is a close back vowel. Pending review.
latinLookalike=Y
audioAssetId=
exampleWordId=
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### LETTER `letter-0425`

- Objectives: obj-u1-confusable
- Sessions: u2-s-review
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: d95d1821cc9bfe5862408dbc83ddc8ee154e498e42e9be5f8798d05187103d5d

uppercase=Х
lowercase=х
group=confusable
englishNote=Looks like Latin X. It is a voiceless velar fricative. Pending review.
latinLookalike=X
audioAssetId=
exampleWordId=
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### SOURCE `src-unicode-cyrillic`

- Objectives: none
- Sessions: none
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: db1549b00a8330450215097a8f4fc53e7f4a3189a0154466dfff59e2c4690e7b

Unicode Cyrillic letter forms
Public domain writing-system facts
Letter identities are facts of the Cyrillic script as encoded in Unicode.
Unmodified letter identities; English notes independently authored.

### SOURCE `src-app-english-v2`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: f8aa057ba4b036ef61386abb68c5245757812289c96355777ba0c49aacda12c7

Independently authored companion English
Original app text
Russian Companion project
Original

### RIGHTS `rights-unicode-cyrillic`

- Objectives: none
- Sessions: none
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 9c4a1a55c23c1ed1fcefc50140856d181f87ee84e2fef1f179e90b7dec2d3c7e

src-unicode-cyrillic
Public domain writing-system facts
permitsAppUse=true

### RIGHTS `rights-app-english-v2`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: cc164e7d614d14b84cbae9494b688d1f417a9bec507481d9ce113448fc8598bc

src-app-english-v2
Original app text
permitsAppUse=true

## Instructional relationships

- `obj-u2-review` INSTRUCTION `u2-st-review`
- `obj-u2-review` EXAMPLE `ex-print-letter-0411`
- `obj-u2-review` EXAMPLE `ex-letter-0412`
- `obj-u2-review` EXAMPLE `ex-letter-0423`
- `obj-u2-review` EXAMPLE `ex-letter-0425`
- `obj-u2-review` PRACTICE `u2-st-review-guided`
- `obj-u2-review` CHECK `u2-i-в-not-b`
- `obj-u2-review` CHECK `u2-i-у-not-y`
- `obj-u2-review` CHECK `u2-i-х-not-x`
- `obj-u2-review` CHECK `u2-i-print-б`
- `obj-u2-review` CHECK `u2-i-я-identity`
- `obj-u2-review` REMEDIATION `u2-st-review`
- `obj-u2-book` INSTRUCTION `u2-st-book`
- `u2-s-orient` OBJECTIVE `obj-u2-review`
- `u2-s-handwriting` OBJECTIVE `obj-u2-book`
- `u2-st-hw` REFERENCE `ref-limits`
- `u2-s-review` OBJECTIVE `obj-u2-review`
- `u2-st-review` EXAMPLE `ex-letter-0412`
- `u2-st-review` EXAMPLE `ex-letter-0423`
- `u2-st-review` EXAMPLE `ex-letter-0425`
- `u2-st-review` EXAMPLE `ex-print-letter-0411`
- `u2-st-review` REFERENCE `ref-cyrillic-print`
- `u2-st-review` REFERENCE `ref-confusable`
- `u2-st-review-guided` REMEDIATION `u2-st-review`
- `u2-st-review-guided` EXAMPLE `ex-print-letter-0411`
- `u2-st-review-guided` ITEM `u2-i-print-б`
- `u2-st-review-v` REMEDIATION `u2-st-review`
- `u2-st-review-v` EXAMPLE `ex-letter-0412`
- `u2-st-review-v` ITEM `u2-i-в-not-b`
- `u2-st-review-u` REMEDIATION `u2-st-review`
- `u2-st-review-u` EXAMPLE `ex-letter-0423`
- `u2-st-review-u` ITEM `u2-i-у-not-y`
- `u2-st-review-h` REMEDIATION `u2-st-review`
- `u2-st-review-h` EXAMPLE `ex-letter-0425`
- `u2-st-review-h` ITEM `u2-i-х-not-x`
- `u2-s-pronunciation` OBJECTIVE `obj-u2-book`
- `u2-st-pron` REFERENCE `ref-pronunciation`
- `u2-s-reading` OBJECTIVE `obj-u2-book`
- `u2-st-reading` REFERENCE `ref-decoding`
- `u2-s-record` OBJECTIVE `obj-u2-book`
- `u2-s-book` OBJECTIVE `obj-u2-book`
- `u2-s-check` OBJECTIVE `obj-u2-review`
- `u2-s-check` OBJECTIVE `obj-u2-book`
- `u2-st-check` REMEDIATION `u2-st-review`
- `u2-st-check` ITEM `u2-i-в-not-b`
- `u2-st-check` ITEM `u2-i-print-б`
- `u2-st-check` ITEM `u2-i-я-identity`

## Book-directed experiences

- `u2-st-orient`
- `u2-st-hw`
- `u2-st-pron`
- `u2-st-reading`
- `u2-st-book`
- `u2-st-cumulative-direction`

## Unavailable experiences

None.


## Unresolved relationships

None. Every referenced Unit 2 identifier resolves.
