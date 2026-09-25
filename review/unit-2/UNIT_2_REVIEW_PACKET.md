# Unit 2 linguistic and owned-edition mapping packet

This packet prepares review. It records an unsigned structural mapping proposal. It does not record a qualified review, an approved edition alignment, or an install decision.

The owned-edition mapping worksheet separates observed edition facts, the proposed lesson and printed page range, unresolved edition questions, and matters that still require qualified linguistic review. ISBN, owned-copy verification, variance, reviewer identity, qualification, date, and approval stay blank. The disposition remains PENDING_CURRICULUM_REVIEWER.

Unit 2 remains in development. Its readiness is content-complete and review-pending. The Lesson 2 relationship is a proposed, unsigned mapping. It is not an approved edition alignment.

## Package identity

- Schema version: 3
- Application: dev.russiancompanion
- Version: 2.0.0-dev (5)
- Curriculum source commit: 4a6c21da07b515df226a090cc0ff6b1ff4a78aa1
- Unit content-review SHA-256: d51310f9ac1596ab50d2c5df7dffd9b10db23d712875680dbfb942bf577e056b
- Mapping-package SHA-256: 9f1b5a6f3efe2a92180b73da24d8291c4f6d6d79f193d034baab9414637f2ae3
- Pack content-review SHA-256: 3c56dc9f54ae26c06bcd765523b700ee1ea09cecf5fbd04ba581a5c51a0ad169
- Review-package SHA-256: bb4ed50ed544251586d42c7cd6dfe12d04dde8bd2cab301ff0a28033ab76dacb
- Repository source SHA-256: c8e6eacc58ae60743a448514c50851aa0b62df6a566dd5ae540112dc70c41a85

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 483160b2c02b74ce86c002de4e7c324bc5042a36d6b7e768a23c14bb572a80fe

status=PENDING_QUALIFIED_REVIEW
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
- Content hash: e64f77e7f73a718e6e7a38cf0e823858a1a87113f2f36b898f6e962f0315e1ca

kind=BOOK
title=Handwritten forms stay in the owned book
body=Verified handwriting images are not in this app. Use Lesson 2 in the book you own to see handwritten forms, and copy them onto paper. This app does not describe those forms and does not grade the page.
dominantAction=Continue
references=ref-limits
items=
examples=
unavailableReason=
bookDirection=Copy the handwritten forms from Lesson 2 in your book onto paper. This app does not show or grade them.
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
- Content hash: 070aaa18de576f506919cf847ad979fe8882f958068ddce3a7a94f86fdd3caca

kind=TEACH
title=Keep letter identities available
body=Review the printed letters from Unit 1, especially Latin look-alikes and print pairs.
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
- Content hash: 5ce768069cd45ba5d00eb0f96235e44c830abd6223e15b1aa8d24b0936a7a4a0

kind=BOOK
title=Additional pronunciation stays in the owned book
body=No licensed pronunciation audio is mapped to Unit 2. Use the Lesson 2 pronunciation notes in the book you own. This app does not state those sounds and does not score a recording.
dominantAction=Continue
references=ref-pronunciation
items=
examples=
unavailableReason=
bookDirection=Use the Lesson 2 pronunciation notes in your copy. Model audio is not in this app.
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
- Content hash: 43a5767c597e0da2b2bbf0bdf264a92b6fd0ba2972d961a445d000ad00d6274e

kind=BOOK
title=Lesson 2 in your copy
body=Complete Lesson 2 handwriting and pronunciation work in the edition you own. Self-attest when finished.
dominantAction=Continue
references=
items=
examples=
unavailableReason=
bookDirection=Do Lesson 2 in your book, including handwritten-form practice.
companionDirection=
remediationStepId=

### SESSION `u2-s-check`

- Objectives: obj-u2-review
- Sessions: u2-s-check
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 408a0c5f5877aee1d7fb81a68502423f19a19aa08dabee826d535d466295a28a

title=Unit 2 letter check
estimatedMinutes=8
objectives=obj-u2-review
steps=u2-st-check

### STEP `u2-st-check`

- Objectives: obj-u2-review
- Sessions: u2-s-check
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 42166e0f4ed6670adf09e0ec210ed6f13a8ea459b2511a568221003150915c95

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

### EXAMPLE `ex-print-letter-0411`

- Objectives: obj-u2-review
- Sessions: u2-s-review
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: b88846e84765beac6a09389132af7d40b41402911535b3608e8127e4571cdf5b

kind=PRINT_PAIR
letterId=letter-0411
display=Б / б
note=Б and б are the same letter in two print cases.
objectiveId=obj-u2-review
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-letter-0412`

- Objectives: obj-u1-confusable
- Sessions: u2-s-review
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 5a01960d0bbf654ec874afda8f593372b4b7e868888ca978be326e1917e6ff5f

kind=CONFUSABLE_PAIR
letterId=letter-0412
display=В / в
note=Printed pair В в. Treat the Cyrillic identity, not a Latin look-alike.
objectiveId=obj-u1-confusable
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-letter-0423`

- Objectives: obj-u1-confusable
- Sessions: u2-s-review
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: a2a09b91f6aa89106ccf45337b58a38f17f0972c574ceae9d2a8b61343742da0

kind=CONFUSABLE_PAIR
letterId=letter-0423
display=У / у
note=Printed pair У у. Treat the Cyrillic identity, not a Latin look-alike.
objectiveId=obj-u1-confusable
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-letter-0425`

- Objectives: obj-u1-confusable
- Sessions: u2-s-review
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 21b88f9af7925467de55deb54ba3a3b42d41434db712cfce944cbf886e401b67

kind=CONFUSABLE_PAIR
letterId=letter-0425
display=Х / х
note=Printed pair Х х. Treat the Cyrillic identity, not a Latin look-alike.
objectiveId=obj-u1-confusable
reviewStatus=PENDING_QUALIFIED_REVIEW

### GUIDED_AND_INDEPENDENT_CHECK `u2-i-print-б`

- Objectives: obj-u2-review
- Sessions: u2-s-review, u2-s-check
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 772cc9de94262de34a89264a20e6a7e79119739deb25c5ffe63f3873f6af4bbb

kind=PRINT_PAIR
language=en
prompt=Which pair is the same letter in uppercase and lowercase print?
promptLetter=Б
options=Б / б | В / б | Б / в
answer=Б / б
feedback=Б / б is one letter. В is a different letter.
objectiveId=obj-u2-review
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW

### INDEPENDENT_CHECK `u2-i-в-not-b`

- Objectives: obj-u2-review
- Sessions: u2-s-review, u2-s-check
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: f6b6149afd4e78c8cd7d583327d8d7921b099dacb6dd69fe18d4db8db772aa32

kind=CONFUSABLE
language=en
prompt=This letter looks like Latin B. What is it?
promptLetter=В
options=Latin B | Cyrillic В | Cyrillic Б
answer=Cyrillic В
feedback=В is not Latin B and it is not Cyrillic Б.
objectiveId=obj-u2-review
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW

### INDEPENDENT_CHECK `u2-i-у-not-y`

- Objectives: obj-u2-review
- Sessions: u2-s-review
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: f39238ef5cf94836453db6ab35258ca3b781600686e89083e9e5a8b32271cb3d

kind=CONFUSABLE
language=en
prompt=This letter looks like Latin Y. What is it?
promptLetter=У
options=Latin Y | Cyrillic У | Cyrillic И
answer=Cyrillic У
feedback=У is a Cyrillic vowel letter, not Latin Y.
objectiveId=obj-u2-review
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW

### INDEPENDENT_CHECK `u2-i-х-not-x`

- Objectives: obj-u2-review
- Sessions: u2-s-review
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 4c813796da3819c79f70c49500bab2e097fbacd0aaf216caced53431adc93a6a

kind=CONFUSABLE
language=en
prompt=This letter looks like Latin X. What is it?
promptLetter=Х
options=Latin X | Cyrillic Х | Cyrillic К
answer=Cyrillic Х
feedback=Х is a Cyrillic letter, not Latin X.
objectiveId=obj-u2-review
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW

### INDEPENDENT_CHECK `u2-i-я-identity`

- Objectives: obj-u2-review
- Sessions: u2-s-check
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: fdc65f37cdfe506f79534dd025930c299b34d535c305dfc5e2fce88713cf4f3a

kind=LETTER_IDENTITY
language=en
prompt=Which printed letter is shown?
promptLetter=Я
options=Cyrillic Я | Cyrillic Р | Cyrillic А
answer=Cyrillic Я
feedback=Я is an iotated vowel letter. It is not Р or А.
objectiveId=obj-u2-review
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: fd3cafc5faba6d6d2ce79e973e8fd57f6d41eced09ad4ac520eeb7421eec33d9

title=Cyrillic print forms
summary=33 printed uppercase/lowercase pairs.
detail=Use the letter board grouped by the Unit 1 sequence. English sound notes are pending review. No example words are attached.
classification=LINGUISTIC_INSTRUCTION
introducedInUnit=1
searchTerms=cyrillic print forms
reviewStatus=PENDING_QUALIFIED_REVIEW

### REFERENCE `ref-confusable`

- Objectives: none
- Sessions: u2-s-review
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: LINGUISTIC_INSTRUCTION
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: b460f9f6d0980b14e443d912d0951c32ebefd3402cf17f21f929fa6c1cc29428

title=Confusable Latin look-alikes
summary=В Н Р С У Х can be misread by Latin-alphabet readers.
detail=Name the Cyrillic identity. Confirm sounds in your book.
classification=LINGUISTIC_INSTRUCTION
introducedInUnit=1
searchTerms=confusable latin look-alikes
reviewStatus=PENDING_QUALIFIED_REVIEW

### REFERENCE `ref-pronunciation`

- Objectives: none
- Sessions: u2-s-pronunciation
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: LINGUISTIC_INSTRUCTION
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 5bd3b61048ff1231d5d46dd41fde6a33b3aa622431c0b79892fbc7ddc8d86243

title=Pronunciation guidance
summary=Model audio is used only when a file is licensed and mapped to the current objective.
detail=User recordings play at original speed. No ASR or score.
classification=LINGUISTIC_INSTRUCTION
introducedInUnit=2
searchTerms=pronunciation guidance
reviewStatus=PENDING_QUALIFIED_REVIEW

### REFERENCE `ref-decoding`

- Objectives: none
- Sessions: u2-s-reading
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: LINGUISTIC_INSTRUCTION
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: f11ac471c8c19744ba7bd656de27a4a2a9ca37b608012a43d4afb0908923f3e1

title=Staged decoding limits
summary=This pack decodes letters and print pairs only.
detail=Word, phrase, and sign reading wait for reviewed examples.
classification=LINGUISTIC_INSTRUCTION
introducedInUnit=1
searchTerms=staged decoding limits
reviewStatus=PENDING_QUALIFIED_REVIEW

### LETTER `letter-0411`

- Objectives: obj-u1-letter-identity, obj-u2-review
- Sessions: u2-s-review
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 42500c378a80f136d2d4a12afe6e598980e587c247164d2e1708499e050ba902

uppercase=Б
lowercase=б
group=consonant-early
englishNote=A voiced bilabial stop. Pending review.
latinLookalike=
audioAssetId=
exampleWordId=
reviewStatus=PENDING_QUALIFIED_REVIEW

### LETTER `letter-0412`

- Objectives: obj-u1-confusable
- Sessions: u2-s-review
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: a785afc821df95e646371b433645bd611a51875c08f525f651a75b80ee0ba6de

uppercase=В
lowercase=в
group=confusable
englishNote=Looks like Latin B. It is not the /b/ letter. Pending review.
latinLookalike=B
audioAssetId=
exampleWordId=
reviewStatus=PENDING_QUALIFIED_REVIEW

### LETTER `letter-0423`

- Objectives: obj-u1-confusable
- Sessions: u2-s-review
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 101b2146cf8c3d5f18759494a3878cfce171da0e429b80de9c48593ffb576c8e

uppercase=У
lowercase=у
group=confusable
englishNote=Looks like Latin Y. It is a close back vowel. Pending review.
latinLookalike=Y
audioAssetId=
exampleWordId=
reviewStatus=PENDING_QUALIFIED_REVIEW

### LETTER `letter-0425`

- Objectives: obj-u1-confusable
- Sessions: u2-s-review
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: bdb465d0d4694be3fe49ff6cc667b752fbdfe7bc73b67fb629e0a781bee9616b

uppercase=Х
lowercase=х
group=confusable
englishNote=Looks like Latin X. It is a voiceless velar fricative. Pending review.
latinLookalike=X
audioAssetId=
exampleWordId=
reviewStatus=PENDING_QUALIFIED_REVIEW

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

## Unavailable experiences

None.


## Unresolved relationships

None. Every referenced Unit 2 identifier resolves.
