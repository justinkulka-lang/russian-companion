# Unit 1 linguistic and owned-edition mapping packet

This packet prepares review. It records an unsigned structural mapping proposal. It does not record a qualified review, an approved edition alignment, or an install decision.

The owned-edition mapping worksheet separates observed edition facts, the proposed lesson and printed page range, unresolved edition questions, and matters that still require qualified linguistic review. ISBN, owned-copy verification, variance, reviewer identity, qualification, date, and approval stay blank. The disposition remains PENDING_CURRICULUM_REVIEWER.

Unit 1 remains in development. Its readiness is content-complete and review-pending. The Lesson 1 relationship is a proposed, unsigned mapping. It is not an approved edition alignment.

## Package identity

- Schema version: 3
- Application: dev.russiancompanion
- Version: 2.0.0-dev (5)
- Curriculum source commit: 1c6d6cca475d956b15cb7f43c78bfccc8c0b6592
- Unit content-review SHA-256: 161a578ba556d709b621b61f138ad36290da57869a87ed57979e0bc7058b0aa7
- Mapping-package SHA-256: b85c056f7c940f045fcb9270edcfda31371ae36f7277c75b673158fcc52872d0
- Pack content-review SHA-256: 1e91dc575c48ce9679fcd8b9ba94e6f15aacdcec01af2ffe83ff3651638f0003
- Review-package SHA-256: c64ce6d4265bf947f37146365a1ae8de016779c735d367cffe8839f1635b2e1e
- Repository source SHA-256: 49f912a07581fc56df14df234615f11f4ec4f7b2da85cc41fe408f18d67f674e

`unitContentReviewDigest` binds this unit's instructional curriculum. It covers this unit and the reachable instructional records, relationships, shared dependencies, sources, rights, assets, and advancement rule. It does not cover any other unit. Recording a review status, reviewer signature, mapping approval, install state, or readiness does not change it.

`mappingPackageDigest` binds the proposed edition-mapping facts for Unit 1: edition identity, lesson, printed page range, topic description, observation source, ISBN observation state, and the pending disposition. Reviewer identity, qualification, date, notes, correction notes, and signature stay outside that digest.

`packContentReviewDigest` is the repository aggregate of instructional curriculum. It is not an approval of this unit, and it is not part of the review-package digest.

`reviewPackageDigest` identifies this complete pending review package. It binds the ordered instructional records and the mapping-package digest. Changing a proposed page range or topic changes the mapping-package digest and therefore this package digest.

`repositorySourceDigest` binds repository and release inputs. Recording an approval later changes that repository hash. That change does not replace the signed content-review digest or the mapping-package digest.

The marker scan used by this package detects only a fixed list of known prohibited mapping-input markers. It is not comprehensive copyright detection and cannot recognize every copy or close paraphrase of protected material.

## Unit

number=1
title=Learning to read Russian
topicSummary=Content-complete development unit for printed letters: identities, print pairs, look-alikes, signs, and letter-level decoding. Qualified review and the owned-edition mapping are still pending.
installState=DEVELOPMENT
readiness=CONTENT_COMPLETE_REVIEW_PENDING
mappingStatus=PENDING_CURRICULUM_REVIEWER
mappingNote=Proposed Lesson 1 mapping from the publisher's public table of contents. Owned-edition comparison is not signed, so this unit is not labeled aligned.
penguinLessonField=1
prerequisites=
estimatedMinutes=152

## Not yet approved

No reviewer has signed this packet. Reviewer identity, qualification, review date, disposition, and reviewed digest are blank. Do not treat this development unit as reviewed, installable, or installed.

## Records

### UNIT_REVIEW `rev-pending-unit-1`

- Objectives: none
- Sessions: none
- Source: none
- Rights: none
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 39c8500a2f5b1408fcada7a7366c03179f746cecf6dc6d395700f8e2a0525ba2

status=IMPLEMENTED_PENDING_FINAL_AUDIT
reviewer=
reviewerQualification=
packVersion=
reviewedContentDigest=
curriculumMappingId=
notes=No qualified reviewer has signed Unit 1.

### UNIT `unit-1`

- Objectives: obj-u1-letter-identity, obj-u1-confusable, obj-u1-iotated, obj-u1-signs, obj-u1-print-forms, obj-u1-decoding, obj-u1-pronunciation, obj-u1-stress, obj-u1-book, obj-u1-letter-names
- Sessions: u1-s-orient, u1-s-writing-system, u1-s-vowels, u1-s-early-consonants, u1-s-confusable, u1-s-iotated, u1-s-signs, u1-s-print, u1-s-decoding, u1-s-pronunciation, u1-s-stress, u1-s-book, u1-s-companions, u1-s-check, u1-s-letter-names
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 8ebe8efe4c40e6427ff2509036951eafd00733187f9a2ab320b8aef9b0271ec7

number=1
title=Learning to read Russian
topicSummary=Content-complete development unit for printed letters: identities, print pairs, look-alikes, signs, and letter-level decoding. Qualified review and the owned-edition mapping are still pending.
installState=DEVELOPMENT
readiness=CONTENT_COMPLETE_REVIEW_PENDING
mappingStatus=PENDING_CURRICULUM_REVIEWER
mappingNote=Proposed Lesson 1 mapping from the publisher's public table of contents. Owned-edition comparison is not signed, so this unit is not labeled aligned.
penguinLessonField=1
prerequisites=
estimatedMinutes=152

### ADVANCEMENT `unit-1-advancement`

- Objectives: none
- Sessions: none
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: e3fa007fed82c35f166cbb0f4ad23871d00e8e265a15237567bacf662b29914d

requireInstruction=true
requireBookSelfAttestation=true
requireAppChecks=true
minimumDistinctChecks=2
requireDueReview=false
tutorOptional=true

### OBJECTIVE `obj-u1-letter-identity`

- Objectives: obj-u1-letter-identity
- Sessions: none
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 289828da85db5b9c751621e93d3c0cff88b28d3cdfce7e6477b9507e90b363a2

title=Identify staged printed letters
kind=RECOGNITION
unitNumber=1
instruction=u1-st-vowels,u1-st-cons,u1-st-system
examples=ex-letter-0410,ex-letter-0411,ex-letter-0413,ex-letter-0414,ex-letter-0416,ex-letter-0417,ex-letter-0418,ex-letter-041a,ex-letter-041b,ex-letter-041c,ex-letter-041e,ex-letter-041f,ex-letter-0422,ex-letter-0424,ex-letter-0426,ex-letter-0427,ex-letter-0428,ex-letter-0429,ex-letter-042b,ex-letter-042d
practice=u1-st-vowels-check,u1-st-cons-check
checks=u1-i-а-identity,u1-i-м-identity
remediation=u1-st-vowels,u1-st-cons

### OBJECTIVE `obj-u1-confusable`

- Objectives: obj-u1-confusable
- Sessions: none
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 46c44b36f84f6fdbdc4de0e4e9e7c446cb9dbdd2bea1dab7d576dc3be6d7919d

title=Separate Cyrillic letters from Latin look-alikes
kind=RECOGNITION
unitNumber=1
instruction=u1-st-confusable
examples=ex-letter-0412,ex-letter-041d,ex-letter-0420,ex-letter-0421,ex-letter-0423,ex-letter-0425
practice=u1-st-guided-confusable,u1-st-confusable-r,u1-st-confusable-n,u1-st-confusable-s,u1-st-confusable-rest
checks=u1-i-р-not-p,u1-i-н-not-h,u1-i-с-not-c,u1-i-в-not-b,u1-i-у-not-y,u1-i-х-not-x
remediation=u1-st-confusable

### OBJECTIVE `obj-u1-iotated`

- Objectives: obj-u1-iotated
- Sessions: none
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: b7d8bccb98b816c23b62696e556d5585681cd33124d14ec31a8c6287f8b1ceeb

title=Recognize Е Ё Ю Я as a group
kind=RECOGNITION
unitNumber=1
instruction=u1-st-iotated
examples=ex-letter-0415,ex-letter-0401,ex-letter-042e,ex-letter-042f
practice=u1-st-guided-iotated,u1-st-iotated-check
checks=u1-i-е-identity,u1-i-ё-identity,u1-i-ю-identity,u1-i-я-identity
remediation=u1-st-iotated

### OBJECTIVE `obj-u1-signs`

- Objectives: obj-u1-signs
- Sessions: none
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 1ae952bbd7d682cd36b8f63331290e09e838566b1b3bea324df2919a75270346

title=Treat Ь Ъ Й as non-ordinary-vowel letters
kind=RECOGNITION
unitNumber=1
instruction=u1-st-signs
examples=ex-letter-0419,ex-letter-042a,ex-letter-042c
practice=u1-st-guided-signs,u1-st-signs-check,u1-st-hard-check,u1-st-short-i-check
checks=u1-i-ь-not-vowel,u1-i-ъ-not-vowel,u1-i-й-not-vowel
remediation=u1-st-signs

### OBJECTIVE `obj-u1-print-forms`

- Objectives: obj-u1-print-forms
- Sessions: none
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 6ab89580e257fa3244b6e35ccb3d3a9c90f1dc2c33d39d15261be3b1cc305990

title=Match uppercase and lowercase print pairs
kind=RECOGNITION
unitNumber=1
instruction=u1-st-print
examples=ex-print-letter-0410,ex-print-letter-041c
practice=u1-st-guided-print,u1-st-print-check
checks=u1-i-print-pair
remediation=u1-st-print

### OBJECTIVE `obj-u1-decoding`

- Objectives: obj-u1-decoding
- Sessions: none
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 43ca162a13212ed9e9c779c7460b6054c968ba4a4658267f8aa761021f2904fa

title=Decode a printed letter pair
kind=RECOGNITION
unitNumber=1
instruction=u1-st-decoding
examples=ex-print-letter-0410,ex-print-letter-041c
practice=u1-st-guided-decoding,u1-st-decoding-check
checks=u1-i-decode-pair
remediation=u1-st-decoding

### OBJECTIVE `obj-u1-pronunciation`

- Objectives: obj-u1-pronunciation
- Sessions: none
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: a4f6129a4a30a7716f4e5c064e6587efd4604788a3d28c5cb65dac8bf2a9ed90

title=Confirm letter sounds in the book
kind=GUIDANCE
unitNumber=1
instruction=u1-st-pronunciation
examples=
practice=
checks=
remediation=

### OBJECTIVE `obj-u1-stress`

- Objectives: obj-u1-stress
- Sessions: none
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: b833b512f43ae7bccd9bfa81ffbfe0f455435f5cfa93154916ec648a883b2807

title=Treat stress as book-marked, not guessed
kind=GUIDANCE
unitNumber=1
instruction=u1-st-stress
examples=
practice=
checks=
remediation=

### OBJECTIVE `obj-u1-book`

- Objectives: obj-u1-book
- Sessions: none
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: a6454cc73248b8777f31918418b5f410e167fb34ba2b8a91f88524ddacc107e1

title=Complete Lesson 1 work in the owned book
kind=SELF_ATTESTATION
unitNumber=1
instruction=u1-st-book
examples=
practice=
checks=
remediation=

### OBJECTIVE `obj-u1-letter-names`

- Objectives: obj-u1-letter-names
- Sessions: none
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 480f56390a5468798a930245d8b2ae5a1013a5785bcbc6c77586815da520594d

title=Name the printed letter
kind=RECOGNITION
unitNumber=1
instruction=u1-st-teach-letter-names
examples=ex-u1-name-a,ex-u1-name-be
practice=u1-st-guided-letter-names
checks=u1-i-name-be
remediation=u1-st-rem-letter-names

### SESSION `u1-s-orient`

- Objectives: obj-u1-letter-identity
- Sessions: u1-s-orient
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 223d9a222e457df78d31e036fffc1c2419fefd4a4c22f929a21a58714849ddc8

title=What this unit is for
estimatedMinutes=8
objectives=obj-u1-letter-identity
steps=u1-st-orient,u1-st-why

### STEP `u1-st-orient`

- Objectives: obj-u1-letter-identity
- Sessions: u1-s-orient
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 65ff0dbb9b93a8c7037a5993d0994957d074dc71673598deccce94d6762428a4

kind=ORIENT
title=Unit 1 — Printed letters
body=This is a proposed Lesson 1 mapping, not an approved owned-edition alignment. The development unit teaches printed letter identities with public-domain letter forms. Example words and model audio are not part of this unit. Qualified review is still pending.
dominantAction=Continue
references=
items=
examples=
unavailableReason=
bookDirection=Open your copy to Lesson 1. Use the book for the author's explanations, exercises, and any example words.
companionDirection=
remediationStepId=

### STEP `u1-st-why`

- Objectives: obj-u1-letter-identity
- Sessions: u1-s-orient
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 797274b6e5b4321c18a83e3d586db806403db47b81a1f2c57695f8936dd037dc

kind=TEACH
title=Identify letters first
body=Work letter shapes, print pairs, and Latin look-alikes before later reading. Transliteration, if you use it, is a temporary support.
dominantAction=Continue
references=ref-transliteration
items=
examples=
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### SESSION `u1-s-writing-system`

- Objectives: obj-u1-letter-identity
- Sessions: u1-s-writing-system
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 662d9bf64c0ac3b82ab2073117f8faec14d4f07074a328fa1aa7db5d4dcf4f36

title=The writing system
estimatedMinutes=8
objectives=obj-u1-letter-identity
steps=u1-st-system

### STEP `u1-st-system`

- Objectives: obj-u1-letter-identity
- Sessions: u1-s-writing-system
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: a255b0b8d8de64b23fd1954189620ac050a809cba91794c3a25efe461a16419f

kind=TEACH
title=Cyrillic is a different alphabet
body=Russian is written with 33 Cyrillic letters. Some printed shapes look like Latin letters and are not the same letters. Learn identity first.
dominantAction=Continue
references=ref-cyrillic-print
items=
examples=ex-letter-0410,ex-letter-041c
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### SESSION `u1-s-vowels`

- Objectives: obj-u1-letter-identity
- Sessions: u1-s-vowels
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: f9d1de9ac0103fde965f3753606e94bd9de84cd6e8dd635b678e7c2f9f628f87

title=Plain vowel letters
estimatedMinutes=12
objectives=obj-u1-letter-identity
steps=u1-st-vowels,u1-st-vowels-check

### STEP `u1-st-vowels`

- Objectives: obj-u1-letter-identity
- Sessions: u1-s-vowels
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: e7c68283f4ee24b4c85e0b946326e71e8680f663020ee12503f77f0f132f4e55

kind=TEACH
title=First vowel set
body=Work these printed pairs: А а, О о, У у, Э э, Ы ы, И и. English sound notes are pending qualified review.
dominantAction=Continue
references=ref-cyrillic-print
items=
examples=ex-letter-0410,ex-letter-041e,ex-letter-0418
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u1-st-vowels-check`

- Objectives: obj-u1-letter-identity
- Sessions: u1-s-vowels
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: b0e9885f26e381b3bfef563e4b2ea177bb47b02f157ea9b8d9daee7f9eccfcad

kind=GUIDED
title=Identify А
body=Choose the Cyrillic identity for the printed letter.
dominantAction=Continue
references=
items=u1-i-а-identity
examples=ex-letter-0410
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=u1-st-vowels

### SESSION `u1-s-early-consonants`

- Objectives: obj-u1-letter-identity
- Sessions: u1-s-early-consonants
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 09029e738bd973e4f027c328950824704d71ea36154077032351319ee8d0c7f4

title=Early consonant letters
estimatedMinutes=12
objectives=obj-u1-letter-identity
steps=u1-st-cons,u1-st-cons-check

### STEP `u1-st-cons`

- Objectives: obj-u1-letter-identity
- Sessions: u1-s-early-consonants
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 22b6b22eaf4eff9d61a3d7870a73885850fb0d06ae91dadbd48c0b8f5f185606

kind=TEACH
title=Low-confusion consonants
body=Next printed pairs: М м, Т т, К к, П п, Б б, Д д, Г г. Confirm each spoken sound in your book. This unit does not play model audio.
dominantAction=Continue
references=ref-cyrillic-print
items=
examples=ex-letter-041c,ex-letter-0422,ex-letter-0411
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u1-st-cons-check`

- Objectives: obj-u1-letter-identity
- Sessions: u1-s-early-consonants
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 6c54b3cd4112495c76de80228a486de02092f36adceeffaeec905ab47fe805c5

kind=GUIDED
title=Identify М
body=Choose the Cyrillic identity for the printed letter.
dominantAction=Continue
references=
items=u1-i-м-identity
examples=ex-letter-041c
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=u1-st-cons

### SESSION `u1-s-confusable`

- Objectives: obj-u1-confusable
- Sessions: u1-s-confusable
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 02ac91fb16d30acacdf62ce71b066d3da97693c1cb0654e5b28de2d081e1412b

title=Letters that look like Latin
estimatedMinutes=15
objectives=obj-u1-confusable
steps=u1-st-confusable,u1-st-guided-confusable,u1-st-confusable-r,u1-st-confusable-n,u1-st-confusable-s,u1-st-confusable-rest

### STEP `u1-st-confusable`

- Objectives: obj-u1-confusable
- Sessions: u1-s-confusable
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: b8f17c716cf8110b53ad2846e4c483285c4fedf622e8a5c7a4165ccab684efc6

kind=TEACH
title=Shape traps
body=These printed letters often fool Latin-alphabet readers: В, Н, Р, С, У, Х. Name the Cyrillic identity. Read this as a review-pending explanation. Worked examples come before the guided choice, and the independent check is separate. A wrong answer returns to this contrast. Activity is not demonstrated ability.
dominantAction=Continue
references=ref-confusable
items=
examples=ex-letter-0420,ex-letter-041d,ex-letter-0421
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u1-st-guided-confusable`

- Objectives: obj-u1-confusable
- Sessions: u1-s-confusable
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: da18515ce682de0b8690bb58c0e7df1a0eadc537a7089260e08b57baa7a342be

kind=GUIDED
title=Guided contrast
body=These printed letters often fool Latin-alphabet readers: В, Н, Р, С, У, Х. Name the Cyrillic identity. Choose once with the explanation still in view. This is practice, not the independent check.
dominantAction=Check
references=
items=u1-g-guided-confusable
examples=ex-letter-0420,ex-letter-041d,ex-letter-0421
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=u1-st-confusable

### STEP `u1-st-confusable-r`

- Objectives: obj-u1-confusable
- Sessions: u1-s-confusable
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: a793fb5eb75536304d2c97465d5783ca07379b7f9bbb04c9f667c0183a6b0f2f

kind=CHECK
title=Р is not P
body=Identity check.
dominantAction=Continue
references=
items=u1-i-р-not-p
examples=ex-letter-0420
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=u1-st-confusable

### STEP `u1-st-confusable-n`

- Objectives: obj-u1-confusable
- Sessions: u1-s-confusable
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: c81321bf01224d400a7a2209fb977deeef7b932d5f8d9682a13382126d331e28

kind=CHECK
title=Н is not H
body=Identity check.
dominantAction=Continue
references=
items=u1-i-н-not-h
examples=ex-letter-041d
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=u1-st-confusable

### STEP `u1-st-confusable-s`

- Objectives: obj-u1-confusable
- Sessions: u1-s-confusable
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 0a39ee5e523cdb209c3e8ccd85c866f781438e5880290ad18d09e2c739368542

kind=CHECK
title=С is not C
body=Identity check.
dominantAction=Continue
references=
items=u1-i-с-not-c
examples=ex-letter-0421
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=u1-st-confusable

### STEP `u1-st-confusable-rest`

- Objectives: obj-u1-confusable
- Sessions: u1-s-confusable
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 63ce180a8c55108b20aa3c18a309d26e9b7c5c14928e4c730d1e65337e923090

kind=CHECK
title=В, У, and Х
body=Identity check for the remaining look-alikes.
dominantAction=Continue
references=
items=u1-i-в-not-b,u1-i-у-not-y,u1-i-х-not-x
examples=ex-letter-0412,ex-letter-0423,ex-letter-0425
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=u1-st-confusable

### SESSION `u1-s-iotated`

- Objectives: obj-u1-iotated
- Sessions: u1-s-iotated
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 1138c6978b6377ebabd4c060accab682fd04b1b71cb49539f01b19611e8de654

title=Iotated vowel letters
estimatedMinutes=10
objectives=obj-u1-iotated
steps=u1-st-iotated,u1-st-guided-iotated,u1-st-iotated-check

### STEP `u1-st-iotated`

- Objectives: obj-u1-iotated
- Sessions: u1-s-iotated
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 67aba441448b179173358262f9ebd3f72661028a662987d3aecb9d4731340a16

kind=TEACH
title=Е Ё Ю Я
body=Recognize these printed forms as a group. Exact phonetics belong in your book. Read this as a review-pending explanation. Worked examples come before the guided choice, and the independent check is separate. A wrong answer returns to this contrast. Activity is not demonstrated ability.
dominantAction=Continue
references=ref-iotated
items=
examples=ex-letter-0415,ex-letter-042f
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u1-st-guided-iotated`

- Objectives: obj-u1-iotated
- Sessions: u1-s-iotated
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 1c806259e858bda1c36e53f2317dbf58bc91f2e8067af33c7fe46386d36d85f7

kind=GUIDED
title=Guided contrast
body=Recognize these printed forms as a group. Exact phonetics belong in your book. Choose once with the explanation still in view. This is practice, not the independent check.
dominantAction=Check
references=
items=u1-g-guided-iotated
examples=ex-letter-0415,ex-letter-042f
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=u1-st-iotated

### STEP `u1-st-iotated-check`

- Objectives: obj-u1-iotated
- Sessions: u1-s-iotated
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 82bca967c5934cc7853059a80048253acb0541b667f5895565ec23bc77efe3c9

kind=CHECK
title=Identify Е, Ё, Ю, and Я
body=Identity check.
dominantAction=Continue
references=
items=u1-i-е-identity,u1-i-ё-identity,u1-i-ю-identity,u1-i-я-identity
examples=ex-letter-0415,ex-letter-0401,ex-letter-042e,ex-letter-042f
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=u1-st-iotated

### SESSION `u1-s-signs`

- Objectives: obj-u1-signs
- Sessions: u1-s-signs
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 450a95702ecadb742a81947e4f0d1c99bab353180d70e934c336401cf926fd07

title=Soft sign, hard sign, and Й
estimatedMinutes=10
objectives=obj-u1-signs
steps=u1-st-signs,u1-st-guided-signs,u1-st-signs-check,u1-st-hard-check,u1-st-short-i-check

### STEP `u1-st-signs`

- Objectives: obj-u1-signs
- Sessions: u1-s-signs
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 6b88df7bec63d206016ade071a61f0251f08bf13910eb3f209d7f900556c2c6d

kind=TEACH
title=Marks that are not ordinary vowels
body=Ь, Ъ, and Й need special attention. Do not invent a spoken vowel for the signs. Read this as a review-pending explanation. Worked examples come before the guided choice, and the independent check is separate. A wrong answer returns to this contrast. Activity is not demonstrated ability.
dominantAction=Continue
references=ref-signs
items=
examples=ex-letter-042c,ex-letter-042a,ex-letter-0419
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u1-st-guided-signs`

- Objectives: obj-u1-signs
- Sessions: u1-s-signs
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: e67beacb7dea7d7df92c6fa42731c86bfcada075afbcad9d7c59df4693697898

kind=GUIDED
title=Guided contrast
body=Ь, Ъ, and Й need special attention. Do not invent a spoken vowel for the signs. Choose once with the explanation still in view. This is practice, not the independent check.
dominantAction=Check
references=
items=u1-g-guided-signs
examples=ex-letter-042c,ex-letter-042a,ex-letter-0419
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=u1-st-signs

### STEP `u1-st-signs-check`

- Objectives: obj-u1-signs
- Sessions: u1-s-signs
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 105abcd3cff813d9c3598fb2aca631786abab1d348e9b2ea60a83ba4f71f15db

kind=CHECK
title=Soft sign
body=Identity check.
dominantAction=Continue
references=
items=u1-i-ь-not-vowel
examples=ex-letter-042c
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=u1-st-signs

### STEP `u1-st-hard-check`

- Objectives: obj-u1-signs
- Sessions: u1-s-signs
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: c6c9d47b2005976511d03cfd4e0c360744c095e1ec9e6dee340f2b8dfbde4c53

kind=CHECK
title=Hard sign
body=Identity check.
dominantAction=Continue
references=
items=u1-i-ъ-not-vowel
examples=ex-letter-042a
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=u1-st-signs

### STEP `u1-st-short-i-check`

- Objectives: obj-u1-signs
- Sessions: u1-s-signs
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 5564feb625026859255f90dc0d7cfa2d32cd7c54219fb477d280b8f5ebc35a88

kind=CHECK
title=Й is not a vowel
body=Identity check.
dominantAction=Continue
references=
items=u1-i-й-not-vowel
examples=ex-letter-0419
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=u1-st-signs

### SESSION `u1-s-print`

- Objectives: obj-u1-print-forms
- Sessions: u1-s-print
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: b8596c31d18c136c1772a014016a7d33ae8f6ccb363cc1c893ca95af42961b1c

title=Print forms
estimatedMinutes=10
objectives=obj-u1-print-forms
steps=u1-st-print,u1-st-guided-print,u1-st-print-check

### STEP `u1-st-print`

- Objectives: obj-u1-print-forms
- Sessions: u1-s-print
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 12d47e7c9e2c8e160e87c9962bfa49fecce82081f3e60ebdf82347542447e46c

kind=TEACH
title=Uppercase and lowercase together
body=Every letter has a print pair. Match them on the letter board. Handwritten forms wait for verified assets.
dominantAction=Continue
references=ref-cyrillic-print
items=
examples=ex-print-letter-0410
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u1-st-guided-print`

- Objectives: obj-u1-print-forms
- Sessions: u1-s-print
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 2dac5bcd4d2aedb929bf607107ef0b7b89e120e4a5180c807643566018ca0b87

kind=GUIDED
title=Guided contrast
body=Every letter has a print pair. Match them on the letter board. Handwritten forms wait for verified assets. Choose once with the explanation still in view. This is practice, not the independent check.
dominantAction=Check
references=
items=u1-g-guided-print
examples=ex-print-letter-0410
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=u1-st-print

### STEP `u1-st-print-check`

- Objectives: obj-u1-print-forms
- Sessions: u1-s-print
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 4a5011e3b47f170a194f25b360ee3af23a240a2f266c3cd5907b2c0c1ab9e86e

kind=CHECK
title=Match a print pair
body=Identity check.
dominantAction=Continue
references=
items=u1-i-print-pair
examples=ex-print-letter-0410
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=u1-st-print

### SESSION `u1-s-decoding`

- Objectives: obj-u1-decoding
- Sessions: u1-s-decoding
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 1720688bfdbad5751de47e378eb40fbbc30234fd5458835a4bcf16e9f2edc27a

title=Staged decoding
estimatedMinutes=8
objectives=obj-u1-decoding
steps=u1-st-decoding,u1-st-guided-decoding,u1-st-decoding-check

### STEP `u1-st-decoding`

- Objectives: obj-u1-decoding
- Sessions: u1-s-decoding
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 9218ebe733e8d743664ba5c76717ffea41964bd6a1c64730909877ad874be641

kind=TEACH
title=Decode print pairs
body=Name the letter, then match its uppercase and lowercase print forms. This unit does not introduce words.
dominantAction=Continue
references=ref-decoding
items=
examples=ex-print-letter-0410,ex-print-letter-041c
unavailableReason=
bookDirection=Use the elementary reading work in your Lesson 1 pages for anything beyond a single letter.
companionDirection=
remediationStepId=

### STEP `u1-st-guided-decoding`

- Objectives: obj-u1-decoding
- Sessions: u1-s-decoding
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 9355ab10c2755633540229878327f1f246dc12daca84df8cf4e247cf87ac7d80

kind=GUIDED
title=Guided contrast
body=Name the letter, then match its uppercase and lowercase print forms. This unit does not introduce words. Choose once with the explanation still in view. This is practice, not the independent check.
dominantAction=Check
references=
items=u1-g-guided-decoding
examples=ex-print-letter-0410,ex-print-letter-041c
unavailableReason=
bookDirection=Use the elementary reading work in your Lesson 1 pages for anything beyond a single letter.
companionDirection=
remediationStepId=u1-st-decoding

### STEP `u1-st-decoding-check`

- Objectives: obj-u1-decoding
- Sessions: u1-s-decoding
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 571f35e0fc5755da1ffa4863eeb7a06dbc1e7238bb614d0775cd2f8e478cdd95

kind=CHECK
title=One letter, two cases
body=Choose the line that is a single letter.
dominantAction=Continue
references=
items=u1-i-decode-pair
examples=ex-print-letter-0410
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=u1-st-decoding

### SESSION `u1-s-pronunciation`

- Objectives: obj-u1-pronunciation
- Sessions: u1-s-pronunciation
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 548b89ac877754e252f2421a49e6d18277bd3040720830327e96918337c84d4b

title=Pronunciation support
estimatedMinutes=8
objectives=obj-u1-pronunciation
steps=u1-st-pronunciation

### STEP `u1-st-pronunciation`

- Objectives: obj-u1-pronunciation
- Sessions: u1-s-pronunciation
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 7533d558cfef55dce53bd027dec85bee8a71e5d35db9b84ae0529e3ecd26c085

kind=TEACH
title=Confirm sounds in your book
body=This unit does not play model audio. Use the pronunciation notes in your book for each new letter. A recording you make is stored and is not scored.
dominantAction=Continue
references=ref-pronunciation
items=
examples=
unavailableReason=
bookDirection=Read the Lesson 1 pronunciation notes in the edition you own.
companionDirection=
remediationStepId=

### SESSION `u1-s-stress`

- Objectives: obj-u1-stress
- Sessions: u1-s-stress
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: bcb3851eec500d007b0ca21c7d31f9eefbb23f20a8faa1f204fe3639878aeb58

title=Stress awareness
estimatedMinutes=6
objectives=obj-u1-stress
steps=u1-st-stress

### STEP `u1-st-stress`

- Objectives: obj-u1-stress
- Sessions: u1-s-stress
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 55d816b73c4e974dcbbc96d11743c6ce209a48376b467bfa73efcce91efce301

kind=TEACH
title=Stress is part of reading
body=Russian word stress is not predictable from spelling alone. Marked example words are not installed.
dominantAction=Continue
references=ref-stress
items=
examples=
unavailableReason=
bookDirection=When your Lesson 1 pages mark stress, copy that mark into your binder.
companionDirection=
remediationStepId=

### SESSION `u1-s-book`

- Objectives: obj-u1-book
- Sessions: u1-s-book
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 6a1afa97f790ec4b0e90d6617c4695176fcc4b93adc1f71857008c7ab2d9a505

title=Work in your book
estimatedMinutes=25
objectives=obj-u1-book
steps=u1-st-book

### STEP `u1-st-book`

- Objectives: obj-u1-book
- Sessions: u1-s-book
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 18a0a6659097b4e96b9d4a10f4850aa0962534381742fb40c1f9d8ce21e2d6cb

kind=BOOK
title=Lesson 1 in your copy
body=Complete the Lesson 1 reading and pronunciation work in the edition you own.
dominantAction=Continue
references=
items=
examples=
unavailableReason=
bookDirection=Owned-book assignment for Lesson 1: printed pages 1-11. PDF indexes 13-23 are file locators, not printed pages. Observed section locators: §1.1 p1 (PDF 13); §1.2 p1 (PDF 13); §1.3 p7 (PDF 19); §1.4 p8 (PDF 20); §1.5 p10 (PDF 22). Observed exercise locators: 1/1 p8/PDF20; 1/2 p8/PDF20; 1/3 p9/PDF21. These locators are pending reviewer assignment and are not an approved mapping. Do the work in the copy you own. Book activity is not a demonstrated app check.
companionDirection=Afterward, mark book work complete as a self-attestation. That is not demonstrated in-app ability.
remediationStepId=

### SESSION `u1-s-companions`

- Objectives: obj-u1-book
- Sessions: u1-s-companions
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: f55b5b27a9a8da96ae72893a61ce73b3084074be9ece28ab98c879e66efc9432

title=AnkiDroid, binder, and tutor
estimatedMinutes=10
objectives=obj-u1-book
steps=u1-st-anki,u1-st-binder,u1-st-tutor

### STEP `u1-st-anki`

- Objectives: obj-u1-book
- Sessions: u1-s-companions
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: c22c6a98867638f28471e83fd2020646cd6d2d5c49068979fbb2642e9f5c8fe4

kind=ANKI
title=AnkiDroid if you use it
body=If you selected AnkiDroid, make cards only from material you are allowed to use.
dominantAction=Continue
references=
items=
examples=
unavailableReason=
bookDirection=
companionDirection=Self-attest AnkiDroid work. It remains user-reported.
remediationStepId=

### STEP `u1-st-binder`

- Objectives: obj-u1-book
- Sessions: u1-s-companions
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: daf0a0514efde08c36b991c6afdb6ab2ae7d62388040b5448f79129fc50713a7

kind=BINDER
title=One useful binder note
body=Write one confusable letter pair that tricked you and the identity you will trust next time.
dominantAction=Continue
references=
items=
examples=
unavailableReason=
bookDirection=
companionDirection=A binder prompt is a specific note, not 'take notes.'
remediationStepId=

### STEP `u1-st-tutor`

- Objectives: obj-u1-book
- Sessions: u1-s-companions
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 601523a088d01cb0ec46b9451f371fe6169d7523be4144050ca8f5414cc39ee2

kind=TUTOR
title=Optional tutor
body=Tutor work is optional for default advancement.
dominantAction=Continue
references=
items=
examples=
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### SESSION `u1-s-check`

- Objectives: obj-u1-letter-identity, obj-u1-confusable, obj-u1-print-forms
- Sessions: u1-s-check
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: b6b0e380e4f652e832392d9c3e713b1680216bf7da9363a4002dc1ab9c8015cd

title=Cumulative letter check
estimatedMinutes=10
objectives=obj-u1-letter-identity,obj-u1-confusable,obj-u1-print-forms
steps=u1-st-cumul

### STEP `u1-st-cumul`

- Objectives: obj-u1-letter-identity, obj-u1-confusable, obj-u1-print-forms
- Sessions: u1-s-check
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: a7611b428d975a6d88274b77a96b5f18bb06cb122fa609f3af40fea392cac532

kind=CHECK
title=Letter identities
body=Revisit any missed letter, then open the explanation it names.
dominantAction=Continue
references=
items=u1-i-а-identity,u1-i-р-not-p,u1-i-е-identity,u1-i-ь-not-vowel,u1-i-print-pair,u1-i-decode-pair
examples=
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=u1-st-confusable

### SESSION `u1-s-letter-names`

- Objectives: obj-u1-letter-names, obj-u1-pronunciation
- Sessions: u1-s-letter-names
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: ddb7943ab474d33f760e4a86750079d5d7f68ea2ae667a9660f3825e6077b5cf

title=Letter names
estimatedMinutes=8
objectives=obj-u1-letter-names,obj-u1-pronunciation
steps=u1-st-teach-letter-names,u1-st-guided-letter-names,u1-st-rem-letter-names,u1-st-check-letter-names,u1-st-anki-direction,u1-st-binder-direction,u1-st-tutor-direction,u1-st-cumulative-direction

### STEP `u1-st-teach-letter-names`

- Objectives: obj-u1-letter-names, obj-u1-pronunciation
- Sessions: u1-s-letter-names
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: a5d3c2ce469a3d6e30367d9c8e2149c59cbf5a39e59cc6e09eb1b65c84980d96

kind=TEACH
title=Printed letter names
body=Unit 1 letter names, review-pending: А is named а; Б is named бэ; В is named вэ; Г is named гэ; Д is named дэ; Е is named е; Ё is named ё; Ж is named жэ; З is named зэ; И is named и; Й is named и краткое; К is named ка; Л is named эль; М is named эм; Н is named эн; О is named о; П is named пэ; Р is named эр; С is named эс; Т is named тэ; У is named у; Ф is named эф; Х is named ха; Ц is named цэ; Ч is named че; Ш is named ша; Щ is named ща; Ъ is named твёрдый знак; Ы is named ы; Ь is named мягкий знак; Э is named э; Ю is named ю; Я is named я.
dominantAction=Continue
references=ref-ca-letter-names
items=
examples=ex-u1-name-a,ex-u1-name-be
unavailableReason=
bookDirection=Use your Lesson 1 pages for the author's pronunciation notes. This list is the app's name inventory.
companionDirection=
remediationStepId=

### STEP `u1-st-guided-letter-names`

- Objectives: obj-u1-letter-names, obj-u1-pronunciation
- Sessions: u1-s-letter-names
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 846039de5d535181bad2afd33063d2a233a7a5ad3eac910b872913881d8f5eb0

kind=GUIDED
title=Guided letter name
body=The printed letter А is named а. Choose that name before the independent check.
dominantAction=Check
references=ref-ca-letter-names
items=u1-g-name-a
examples=ex-u1-name-a
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=u1-st-rem-letter-names

### STEP `u1-st-rem-letter-names`

- Objectives: obj-u1-letter-names, obj-u1-pronunciation
- Sessions: u1-s-letter-names
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: d4030fe3123e5c65630cbb36908c37f44b4a37ebaea41743f803d5678d0d4ada

kind=REMEDIATE
title=Letter-name remediation
body=Return to the printed name. А is named а. Б is named бэ. Do not use the Latin letter name.
dominantAction=Return
references=ref-ca-letter-names
items=
examples=
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u1-st-check-letter-names`

- Objectives: obj-u1-letter-names, obj-u1-pronunciation
- Sessions: u1-s-letter-names
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 44b815893fe6d95bae6d870599214c5c05977ccf5b0ba8369917f45154ccb268

kind=CHECK
title=Letter-name check
body=Name the printed letter without looking back at the list.
dominantAction=Check
references=
items=u1-i-name-be
examples=
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=u1-st-rem-letter-names

### STEP `u1-st-anki-direction`

- Objectives: obj-u1-letter-names, obj-u1-pronunciation
- Sessions: u1-s-letter-names
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 3d016728d47ee19a6fc18ed58cd9b846d7f1990b687edf7f556bcdb13416943a

kind=ANKI
title=Unit 1 AnkiDroid direction
body=Unit 1 AnkiDroid self-attestation uses only cards you are allowed to make. Binder: write one form from this unit that you missed and the correction you will use. Tutor conversation is optional and is not a demonstrated check. Cumulative review revisits completed objectives from earlier units. Activity is not demonstrated ability. Open the offline grammar reference for this unit when one is linked.
dominantAction=Self-attest
references=
items=
examples=
unavailableReason=
bookDirection=
companionDirection=Self-attest AnkiDroid work. It remains user-reported and does not demonstrate a check.
remediationStepId=

### STEP `u1-st-binder-direction`

- Objectives: obj-u1-letter-names, obj-u1-pronunciation
- Sessions: u1-s-letter-names
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 37f33ef7334ad40f8aceb22c57c30f3d46ce47580d4355a57d0e0631410f3189

kind=BINDER
title=Unit 1 binder prompt
body=Unit 1 AnkiDroid self-attestation uses only cards you are allowed to make. Binder: write one form from this unit that you missed and the correction you will use. Tutor conversation is optional and is not a demonstrated check. Cumulative review revisits completed objectives from earlier units. Activity is not demonstrated ability. Open the offline grammar reference for this unit when one is linked.
dominantAction=Continue
references=
items=
examples=
unavailableReason=
bookDirection=
companionDirection=A binder prompt is one specific note, not a score.
remediationStepId=

### STEP `u1-st-tutor-direction`

- Objectives: obj-u1-letter-names, obj-u1-pronunciation
- Sessions: u1-s-letter-names
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 4156f511b5eff37d0649ceb96a5bf089ab23c71eefbd07b5d7f5eaf4bca3e342

kind=TUTOR
title=Unit 1 optional tutor
body=Unit 1 AnkiDroid self-attestation uses only cards you are allowed to make. Binder: write one form from this unit that you missed and the correction you will use. Tutor conversation is optional and is not a demonstrated check. Cumulative review revisits completed objectives from earlier units. Activity is not demonstrated ability. Open the offline grammar reference for this unit when one is linked.
dominantAction=Continue
references=
items=
examples=
unavailableReason=
bookDirection=
companionDirection=Tutor work is optional and does not count as demonstrated ability.
remediationStepId=

### STEP `u1-st-cumulative-direction`

- Objectives: obj-u1-letter-names, obj-u1-pronunciation
- Sessions: u1-s-letter-names
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 355fe24d03ad6e94953158504189d0436fc4eaae21f26e2c03f69121aedbf30f

kind=ORIENT
title=Unit 1 cumulative review
body=Unit 1 AnkiDroid self-attestation uses only cards you are allowed to make. Binder: write one form from this unit that you missed and the correction you will use. Tutor conversation is optional and is not a demonstrated check. Cumulative review revisits completed objectives from earlier units. Activity is not demonstrated ability. Open the offline grammar reference for this unit when one is linked.
dominantAction=Continue
references=
items=
examples=
unavailableReason=
bookDirection=Owned-book assignment for Lesson 1: printed pages 1-11. PDF indexes 13-23 are file locators, not printed pages. Observed section locators: §1.1 p1 (PDF 13); §1.2 p1 (PDF 13); §1.3 p7 (PDF 19); §1.4 p8 (PDF 20); §1.5 p10 (PDF 22). Observed exercise locators: 1/1 p8/PDF20; 1/2 p8/PDF20; 1/3 p9/PDF21. These locators are pending reviewer assignment and are not an approved mapping. Do the work in the copy you own. Book activity is not a demonstrated app check.
companionDirection=Unit 1 AnkiDroid self-attestation uses only cards you are allowed to make. Binder: write one form from this unit that you missed and the correction you will use. Tutor conversation is optional and is not a demonstrated check. Cumulative review revisits completed objectives from earlier units. Activity is not demonstrated ability. Open the offline grammar reference for this unit when one is linked.
remediationStepId=

### EXAMPLE `ex-letter-0410`

- Objectives: obj-u1-letter-identity
- Sessions: u1-s-writing-system, u1-s-vowels
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: d898098ccddc3ef39489f0d7d13014ffc0f02b578dcb23843ef88a78bf38ae82

kind=PRINT_PAIR
letterId=letter-0410
display=А / а
note=Printed pair А а.
objectiveId=obj-u1-letter-identity
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-letter-0411`

- Objectives: obj-u1-letter-identity
- Sessions: u1-s-early-consonants
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 4cd59b4d06245848656e9a05cc93a1819054d9692d4b3c74dfafb84053f6b47e

kind=PRINT_PAIR
letterId=letter-0411
display=Б / б
note=Printed pair Б б.
objectiveId=obj-u1-letter-identity
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-letter-0413`

- Objectives: obj-u1-letter-identity
- Sessions: none
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: ae3e954e824d79ab429d245951ffef1013c7fab2206f31f37c478d54a642fe5c

kind=LETTER_FORM
letterId=letter-0413
display=Г / г
note=Printed pair Г г.
objectiveId=obj-u1-letter-identity
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-letter-0414`

- Objectives: obj-u1-letter-identity
- Sessions: none
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 0cea78e89b6415f09a9a37c7d9f64d2f237b57412a9bb5962b953bcd300204bf

kind=LETTER_FORM
letterId=letter-0414
display=Д / д
note=Printed pair Д д.
objectiveId=obj-u1-letter-identity
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-letter-0416`

- Objectives: obj-u1-letter-identity
- Sessions: none
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 0ec158888252cf2f14f2accb4ebd3e553c9d1b6a5dcabd1035534fd19746212c

kind=LETTER_FORM
letterId=letter-0416
display=Ж / ж
note=Printed pair Ж ж.
objectiveId=obj-u1-letter-identity
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-letter-0417`

- Objectives: obj-u1-letter-identity
- Sessions: none
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 1894fa6384a9d7b7464715f1366c6372ff38ca5821365603d2d9613c27a82ac4

kind=LETTER_FORM
letterId=letter-0417
display=З / з
note=Printed pair З з.
objectiveId=obj-u1-letter-identity
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-letter-0418`

- Objectives: obj-u1-letter-identity
- Sessions: u1-s-vowels
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 0c6929a0bc6a2452029c69eb1669da4708679e3101960f8fd25de30636b33bac

kind=PRINT_PAIR
letterId=letter-0418
display=И / и
note=Printed pair И и.
objectiveId=obj-u1-letter-identity
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-letter-041a`

- Objectives: obj-u1-letter-identity
- Sessions: none
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 8b38a6241bfdcea8e34aa6964b4b865d2e835e509d86f6444823814a146a822d

kind=LETTER_FORM
letterId=letter-041a
display=К / к
note=Printed pair К к.
objectiveId=obj-u1-letter-identity
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-letter-041b`

- Objectives: obj-u1-letter-identity
- Sessions: none
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: e00aef2e47cfe77b3bf1f6cdaa801cebd437e2ac5234340fae2d40cd653d692e

kind=LETTER_FORM
letterId=letter-041b
display=Л / л
note=Printed pair Л л.
objectiveId=obj-u1-letter-identity
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-letter-041c`

- Objectives: obj-u1-letter-identity
- Sessions: u1-s-writing-system, u1-s-early-consonants
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 8cb31a2deba2c5396043556491245e9ce91768555b152e301895517f08012158

kind=PRINT_PAIR
letterId=letter-041c
display=М / м
note=Printed pair М м.
objectiveId=obj-u1-letter-identity
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-letter-041e`

- Objectives: obj-u1-letter-identity
- Sessions: u1-s-vowels
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: f4e634e9668ddd3d45cc98ec6da9825c0a6ea7602e673d29cc9962be35eec0e5

kind=PRINT_PAIR
letterId=letter-041e
display=О / о
note=Printed pair О о.
objectiveId=obj-u1-letter-identity
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-letter-041f`

- Objectives: obj-u1-letter-identity
- Sessions: none
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 9479d9d4380bf8062b48ec30b5b9f0c7d469594a318b14949c08b09a5450ae8c

kind=LETTER_FORM
letterId=letter-041f
display=П / п
note=Printed pair П п.
objectiveId=obj-u1-letter-identity
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-letter-0422`

- Objectives: obj-u1-letter-identity
- Sessions: u1-s-early-consonants
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 52588f5687f85747c6466eb2f64f156c5cd9089f9b06c800ad1f10bbee5a7cf0

kind=PRINT_PAIR
letterId=letter-0422
display=Т / т
note=Printed pair Т т.
objectiveId=obj-u1-letter-identity
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-letter-0424`

- Objectives: obj-u1-letter-identity
- Sessions: none
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 311120b2b11a31a04548c6d5488725d83d08c53ff3c533c49ea5290cafbc0acd

kind=LETTER_FORM
letterId=letter-0424
display=Ф / ф
note=Printed pair Ф ф.
objectiveId=obj-u1-letter-identity
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-letter-0426`

- Objectives: obj-u1-letter-identity
- Sessions: none
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 6f1f501c8882feee108da4e397520c70ea4720cdca30ec4dadab3ee339abe74a

kind=LETTER_FORM
letterId=letter-0426
display=Ц / ц
note=Printed pair Ц ц.
objectiveId=obj-u1-letter-identity
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-letter-0427`

- Objectives: obj-u1-letter-identity
- Sessions: none
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 5a9535e71b9b677fe5958335461cbe21d6ae4694530352abba33e150cd3258fd

kind=LETTER_FORM
letterId=letter-0427
display=Ч / ч
note=Printed pair Ч ч.
objectiveId=obj-u1-letter-identity
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-letter-0428`

- Objectives: obj-u1-letter-identity
- Sessions: none
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 6fc7e3f6060b895ad9f4c4d5b62ee7295a34dd20971adb93d158585c3315fd9a

kind=LETTER_FORM
letterId=letter-0428
display=Ш / ш
note=Printed pair Ш ш.
objectiveId=obj-u1-letter-identity
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-letter-0429`

- Objectives: obj-u1-letter-identity
- Sessions: none
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 0a9866a230a1d1e89261e6624e877cd002877e44c6d60db549e6e9f25cec8536

kind=LETTER_FORM
letterId=letter-0429
display=Щ / щ
note=Printed pair Щ щ.
objectiveId=obj-u1-letter-identity
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-letter-042b`

- Objectives: obj-u1-letter-identity
- Sessions: none
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: bfba33582830be2d18d2544019a28c59507406bda3b45014102563cfbbf8159e

kind=LETTER_FORM
letterId=letter-042b
display=Ы / ы
note=Printed pair Ы ы.
objectiveId=obj-u1-letter-identity
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-letter-042d`

- Objectives: obj-u1-letter-identity
- Sessions: none
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 0313d38a761e1bb146bbe2c074e0942f358ac75a0a59eef49e84c1469515542b

kind=LETTER_FORM
letterId=letter-042d
display=Э / э
note=Printed pair Э э.
objectiveId=obj-u1-letter-identity
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-letter-0412`

- Objectives: obj-u1-confusable
- Sessions: u1-s-confusable
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 59fa97460d207be3fe5662f965b00309eedcbc5a1e8e48203cd3d1172b5e006e

kind=CONFUSABLE_PAIR
letterId=letter-0412
display=В / в
note=Printed pair В в. Treat the Cyrillic identity, not a Latin look-alike.
objectiveId=obj-u1-confusable
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-letter-041d`

- Objectives: obj-u1-confusable
- Sessions: u1-s-confusable
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: f752647ad951acada033a337625acd38069db0a064ad797a5a773172f916153e

kind=CONFUSABLE_PAIR
letterId=letter-041d
display=Н / н
note=Printed pair Н н. Treat the Cyrillic identity, not a Latin look-alike.
objectiveId=obj-u1-confusable
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-letter-0420`

- Objectives: obj-u1-confusable
- Sessions: u1-s-confusable
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 5efc3cadb16c56a409ceb179d3ef55508a13110248b8b221e7fdd9caad8aa22a

kind=CONFUSABLE_PAIR
letterId=letter-0420
display=Р / р
note=Printed pair Р р. Treat the Cyrillic identity, not a Latin look-alike.
objectiveId=obj-u1-confusable
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-letter-0421`

- Objectives: obj-u1-confusable
- Sessions: u1-s-confusable
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 7b81f59cd1099b605b989f4174b09f60b3e2a3c06189087aa8e1804063377c05

kind=CONFUSABLE_PAIR
letterId=letter-0421
display=С / с
note=Printed pair С с. Treat the Cyrillic identity, not a Latin look-alike.
objectiveId=obj-u1-confusable
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-letter-0423`

- Objectives: obj-u1-confusable
- Sessions: u1-s-confusable
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 20ca09a256431b0589e6623f8bd39b847d5358933e99c8525c7ee3db7af6b756

kind=CONFUSABLE_PAIR
letterId=letter-0423
display=У / у
note=Printed pair У у. Treat the Cyrillic identity, not a Latin look-alike.
objectiveId=obj-u1-confusable
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-letter-0425`

- Objectives: obj-u1-confusable
- Sessions: u1-s-confusable
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 80cabc97310b90238f837214be59132c5235fc235ccaee4580569b5ebd4c707c

kind=CONFUSABLE_PAIR
letterId=letter-0425
display=Х / х
note=Printed pair Х х. Treat the Cyrillic identity, not a Latin look-alike.
objectiveId=obj-u1-confusable
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-letter-0415`

- Objectives: obj-u1-iotated
- Sessions: u1-s-iotated
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: a5b4cada70de3433f327c8ad9c2c4f893888be33d8576170670cc50159f7cb79

kind=LETTER_FORM
letterId=letter-0415
display=Е / е
note=Printed pair Е е.
objectiveId=obj-u1-iotated
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-letter-0401`

- Objectives: obj-u1-iotated
- Sessions: u1-s-iotated
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 6818fdd75d15f03907bfee9d865370aaac758ce01e69fbd26df7732bbea4c52f

kind=LETTER_FORM
letterId=letter-0401
display=Ё / ё
note=Printed pair Ё ё.
objectiveId=obj-u1-iotated
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-letter-042e`

- Objectives: obj-u1-iotated
- Sessions: u1-s-iotated
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: a0201368a2fcedc40c14f7a6cc10808afa976d0bcfc4f334e5521ba657a44ea0

kind=LETTER_FORM
letterId=letter-042e
display=Ю / ю
note=Printed pair Ю ю.
objectiveId=obj-u1-iotated
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-letter-042f`

- Objectives: obj-u1-iotated
- Sessions: u1-s-iotated
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: d3b5290052c0bc762daf87d0727b97230cc92dfa35baa8690ac25fede9e7e06f

kind=LETTER_FORM
letterId=letter-042f
display=Я / я
note=Printed pair Я я.
objectiveId=obj-u1-iotated
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-letter-0419`

- Objectives: obj-u1-signs
- Sessions: u1-s-signs
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 2be4ef6fc6ad56e6989cf80e64a8ed67c769306e3a254d716cc923a71f235984

kind=LETTER_FORM
letterId=letter-0419
display=Й / й
note=Printed pair Й й.
objectiveId=obj-u1-signs
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-letter-042a`

- Objectives: obj-u1-signs
- Sessions: u1-s-signs
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 0e10b5f570af578ba7e3625a4ad05db6585e0c340ba91e149bb7805421baf509

kind=LETTER_FORM
letterId=letter-042a
display=Ъ / ъ
note=Printed pair Ъ ъ.
objectiveId=obj-u1-signs
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-letter-042c`

- Objectives: obj-u1-signs
- Sessions: u1-s-signs
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 163c00bb24af6df942a95476445cd9475983a8aedbfba869a16d91bb706b4b60

kind=LETTER_FORM
letterId=letter-042c
display=Ь / ь
note=Printed pair Ь ь.
objectiveId=obj-u1-signs
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-print-letter-0410`

- Objectives: obj-u1-print-forms
- Sessions: u1-s-print, u1-s-decoding
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 6ffa04f33c0bdddb49d067622a92b2bf77bd839abee3b6e605e318a6331b892c

kind=PRINT_PAIR
letterId=letter-0410
display=А / а
note=А and а are the same letter in two print cases.
objectiveId=obj-u1-print-forms
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-print-letter-041c`

- Objectives: obj-u1-print-forms
- Sessions: u1-s-decoding
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 1569c7e83cd2cb5b28f5b0eaa32125e325e9801003c221b7800ba32706ba0c76

kind=PRINT_PAIR
letterId=letter-041c
display=М / м
note=М and м are the same letter in two print cases.
objectiveId=obj-u1-print-forms
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-u1-name-a`

- Objectives: obj-u1-letter-names
- Sessions: u1-s-letter-names
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 514b41c6c8d43e62380821ae196f986727d5dfe42680dfbb779b0350db6a3357

kind=LEXICAL
letterId=letter-0410
display=А
note=The printed letter А is named а.
objectiveId=obj-u1-letter-names
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-u1-name-be`

- Objectives: obj-u1-letter-names
- Sessions: u1-s-letter-names
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 1ec22a03f2f13c86fa081aeac64f6db4e75824fa88daa841efaabaf3cd72634e

kind=LEXICAL
letterId=letter-0411
display=Б
note=The printed letter Б is named бэ.
objectiveId=obj-u1-letter-names
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### GUIDED_AND_INDEPENDENT_CHECK `u1-i-а-identity`

- Objectives: obj-u1-letter-identity
- Sessions: u1-s-vowels, u1-s-check
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: d17171564f49ccf2c0063f73770c26f51dd37af2d5ca96b784f834db61c5c2c7

kind=LETTER_IDENTITY
language=en
prompt=Which printed letter is shown?
promptLetter=А
options=Latin A | Cyrillic А | Cyrillic Р
answer=Cyrillic А
feedback=The printed shape can match Latin A. In this course it is the Cyrillic vowel letter А.
objectiveId=obj-u1-letter-identity
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### GUIDED_PRACTICE `u1-i-м-identity`

- Objectives: obj-u1-letter-identity
- Sessions: u1-s-early-consonants
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: a28826087e6459a6dbd8b33f697952af95756523d8ebe2bd3eec8363dd4cb56e

kind=LETTER_IDENTITY
language=en
prompt=Which printed letter is shown?
promptLetter=М
options=Cyrillic М | Cyrillic Н | Cyrillic Т
answer=Cyrillic М
feedback=М is an early consonant letter. It is not Н.
objectiveId=obj-u1-letter-identity
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### GUIDED_PRACTICE `u1-g-guided-confusable`

- Objectives: obj-u1-confusable
- Sessions: u1-s-confusable
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 5cb48ad0b30b6dc1527cb653700319515d31bdc1086745681a20a6ce904c0de4

kind=CONFUSABLE
language=en
prompt=The shape looks like Latin P. Which identity is the Cyrillic letter?
promptLetter=Р
options=The Cyrillic letter Р | The Latin letter P | The Cyrillic letter П
answer=The Cyrillic letter Р
feedback=Р is the Cyrillic letter, even when the shape looks like Latin P.
objectiveId=obj-u1-confusable
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### GUIDED_PRACTICE `u1-g-guided-iotated`

- Objectives: obj-u1-iotated
- Sessions: u1-s-iotated
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 6d1ce9892d2615e83bd0b51aa0f5a2f26dd12c91962c894fe07e2553e973483d

kind=LETTER_IDENTITY
language=en
prompt=Which printed form is the iotated letter named in this group?
promptLetter=Я
options=The Cyrillic letter Я | The Cyrillic letter А | The Cyrillic letter И
answer=The Cyrillic letter Я
feedback=Я belongs to the iotated group. Exact phonetics stay with the book model you own.
objectiveId=obj-u1-iotated
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### GUIDED_PRACTICE `u1-g-guided-signs`

- Objectives: obj-u1-signs
- Sessions: u1-s-signs
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 880e8d4a4f36931e9ea35ac28672262ccf9e87512ef5ba9d63b9320574d00057

kind=SIGN
language=en
prompt=What does this sign do?
promptLetter=Ь
options=It marks softness and is not a vowel | It is a spoken vowel | It is the Latin letter b
answer=It marks softness and is not a vowel
feedback=Ь and Ъ are signs. Do not invent a spoken vowel for them.
objectiveId=obj-u1-signs
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### GUIDED_PRACTICE `u1-g-guided-print`

- Objectives: obj-u1-print-forms
- Sessions: u1-s-print
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 9b0a288d6e275a806170abae33ff1a4e6144a3aeea3cf963186c8bfcc319fb7d

kind=PRINT_PAIR
language=en
prompt=Which lowercase print form pairs with this uppercase letter?
promptLetter=А
options=а | б | В
answer=а
feedback=Match the printed uppercase and lowercase pair. Handwriting assets are not installed.
objectiveId=obj-u1-print-forms
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### GUIDED_PRACTICE `u1-g-guided-decoding`

- Objectives: obj-u1-decoding
- Sessions: u1-s-decoding
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 2e40f55753bdd3a1164bad75045279f29d6efdb49bcadba7e28e63b101f035a7

kind=PRINT_PAIR
language=en
prompt=Read the printed pair before any word. Which lowercase form matches?
promptLetter=М
options=м | н | т
answer=м
feedback=Decode the print pair first. This guided step still does not score a whole word.
objectiveId=obj-u1-decoding
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### GUIDED_PRACTICE `u1-g-name-a`

- Objectives: obj-u1-letter-names
- Sessions: u1-s-letter-names
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: fc377b5eb4826acba1ded0834165687682b80a7855212c3bde98f23e1ceddade

kind=LETTER_IDENTITY
language=en
prompt=Which name belongs to this printed letter?
promptLetter=А
options=а | бэ | эр
answer=а
feedback=The printed letter А is named а.
objectiveId=obj-u1-letter-names
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### INDEPENDENT_CHECK `u1-i-р-not-p`

- Objectives: obj-u1-confusable
- Sessions: u1-s-confusable, u1-s-check
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 0fbff81b247c4e0890c58122f56d06f68de91a4af9f05d27944606384b14257f

kind=CONFUSABLE
language=en
prompt=This letter looks like Latin P. What is it?
promptLetter=Р
options=The Latin letter P | The Cyrillic letter Р | The Cyrillic letter П
answer=The Cyrillic letter Р
feedback=Shape overlap with Latin P is a recognition trap. This is Cyrillic Р, not English P.
objectiveId=obj-u1-confusable
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### INDEPENDENT_CHECK `u1-i-н-not-h`

- Objectives: obj-u1-confusable
- Sessions: u1-s-confusable
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: aafa7022761fdfc057bc98875dbf93a681e52e2fd5f05b4140138199b90ab568

kind=CONFUSABLE
language=en
prompt=This letter looks like Latin H. What is it?
promptLetter=Н
options=Latin H | Cyrillic Н | Cyrillic И
answer=Cyrillic Н
feedback=Do not read Н as English H. It is a Cyrillic letter.
objectiveId=obj-u1-confusable
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### INDEPENDENT_CHECK `u1-i-с-not-c`

- Objectives: obj-u1-confusable
- Sessions: u1-s-confusable
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: b2aed5584b72d1da7913feefc3242d7fb5f3d28aa4b06c7980fa79e26f1e6ee5

kind=CONFUSABLE
language=en
prompt=This letter looks like Latin C. What is it?
promptLetter=С
options=Latin C | Cyrillic С | Cyrillic К
answer=Cyrillic С
feedback=С is a Cyrillic letter. Do not treat the Latin look-alike as the identity.
objectiveId=obj-u1-confusable
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### INDEPENDENT_CHECK `u1-i-в-not-b`

- Objectives: obj-u1-confusable
- Sessions: u1-s-confusable
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 33fa310917e35876620b5a9dc4a2e8fdda544c646e1b7ccd80f5e373980c0a1e

kind=CONFUSABLE
language=en
prompt=This letter looks like Latin B. What is it?
promptLetter=В
options=Latin B | Cyrillic В | Cyrillic Б
answer=Cyrillic В
feedback=В is not Latin B and it is not Cyrillic Б.
objectiveId=obj-u1-confusable
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### INDEPENDENT_CHECK `u1-i-у-not-y`

- Objectives: obj-u1-confusable
- Sessions: u1-s-confusable
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: d3345f42e60aabc9d906bf09e205fbc310a1246f9d1b14cfebf1c5fa1e6e9d41

kind=CONFUSABLE
language=en
prompt=This letter looks like Latin Y. What is it?
promptLetter=У
options=Latin Y | Cyrillic У | Cyrillic И
answer=Cyrillic У
feedback=У is a Cyrillic vowel letter, not Latin Y.
objectiveId=obj-u1-confusable
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### INDEPENDENT_CHECK `u1-i-х-not-x`

- Objectives: obj-u1-confusable
- Sessions: u1-s-confusable
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 735d879fbd2e4a240397051990d7d6a543911b6de1449bfbb478cd3abcd9e2f9

kind=CONFUSABLE
language=en
prompt=This letter looks like Latin X. What is it?
promptLetter=Х
options=Latin X | Cyrillic Х | Cyrillic К
answer=Cyrillic Х
feedback=Х is a Cyrillic letter, not Latin X.
objectiveId=obj-u1-confusable
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### INDEPENDENT_CHECK `u1-i-е-identity`

- Objectives: obj-u1-iotated
- Sessions: u1-s-iotated, u1-s-check
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 4eb9dd2ba7664a67e6784b9cc2eadadf7eda30757f0b806d5d7f69fe123224c5

kind=LETTER_IDENTITY
language=en
prompt=Which printed letter is shown?
promptLetter=Е
options=Cyrillic Е | Cyrillic Э | Cyrillic Ё
answer=Cyrillic Е
feedback=Е is an iotated vowel letter. Э and Ё are different letters.
objectiveId=obj-u1-iotated
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### INDEPENDENT_CHECK `u1-i-ё-identity`

- Objectives: obj-u1-iotated
- Sessions: u1-s-iotated
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: e5daf3478caa617c0d279c338d053dcd30d25c48041a43bf5ba6aeaaf4a80c6a

kind=LETTER_IDENTITY
language=en
prompt=Which printed letter is shown?
promptLetter=Ё
options=Cyrillic Ё | Cyrillic Е | Cyrillic Э
answer=Cyrillic Ё
feedback=Ё is its own letter. Е and Э are different letters.
objectiveId=obj-u1-iotated
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### INDEPENDENT_CHECK `u1-i-ю-identity`

- Objectives: obj-u1-iotated
- Sessions: u1-s-iotated
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 3c3df14b56a11471430fa56e258e2ae9f175ec62999a428bb1fdeae5401a2de0

kind=LETTER_IDENTITY
language=en
prompt=Which printed letter is shown?
promptLetter=Ю
options=Cyrillic Ю | Cyrillic У | Cyrillic Я
answer=Cyrillic Ю
feedback=Ю is an iotated vowel letter. У and Я are different letters.
objectiveId=obj-u1-iotated
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### INDEPENDENT_CHECK `u1-i-я-identity`

- Objectives: obj-u1-iotated
- Sessions: u1-s-iotated
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: f1d8479d54654f91cab6576b7857a8fb5c76308188cc6f266f742eb508b17402

kind=LETTER_IDENTITY
language=en
prompt=Which printed letter is shown?
promptLetter=Я
options=Cyrillic Я | Cyrillic Р | Cyrillic А
answer=Cyrillic Я
feedback=Я is an iotated vowel letter. It is not Р or А.
objectiveId=obj-u1-iotated
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### INDEPENDENT_CHECK `u1-i-ь-not-vowel`

- Objectives: obj-u1-signs
- Sessions: u1-s-signs, u1-s-check
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 8d8b685e86ecf1c2c0f746dd9f9bb7da3d9ce6f7143444c990b5afa9d1c686c2

kind=SIGN
language=en
prompt=What is the printed mark Ь?
promptLetter=Ь
options=A vowel you pronounce by itself | A mark, not a standalone vowel | The same letter as Ъ
answer=A mark, not a standalone vowel
feedback=Ь is a sign, not a vowel letter you read alone.
objectiveId=obj-u1-signs
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### INDEPENDENT_CHECK `u1-i-ъ-not-vowel`

- Objectives: obj-u1-signs
- Sessions: u1-s-signs
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 35d21f53f5fbd7e9b238af3a6fbebd1d0c832557fe979e20c2e1b0bcaec8e260

kind=SIGN
language=en
prompt=What is the printed mark Ъ?
promptLetter=Ъ
options=A standalone vowel | The hard sign, not a vowel | The same letter as Ь
answer=The hard sign, not a vowel
feedback=Ъ is the hard sign. It is not a spoken vowel and it is not Ь.
objectiveId=obj-u1-signs
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### INDEPENDENT_CHECK `u1-i-й-not-vowel`

- Objectives: obj-u1-signs
- Sessions: u1-s-signs
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: f806dd86d612d94d9106f8f74d6c04e9b8a36a6ee3f6a2f849b2ce3b6a0c3112

kind=SIGN
language=en
prompt=What is the printed letter Й?
promptLetter=Й
options=A standalone vowel | A consonant letter, not a vowel | The same mark as Ь
answer=A consonant letter, not a vowel
feedback=Й is not a vowel you read alone and it is not the soft sign.
objectiveId=obj-u1-signs
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### INDEPENDENT_CHECK `u1-i-print-pair`

- Objectives: obj-u1-print-forms
- Sessions: u1-s-print, u1-s-check
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 89154b3972479d556984b4cf3c9d8e414a5d348a48e8292cbc33f03c64868bbf

kind=PRINT_PAIR
language=en
prompt=Which pair is the same letter in uppercase and lowercase print?
promptLetter=А
options=А / а | Р / п | Н / и
answer=А / а
feedback=Print pairs are one letter in two cases. Р / п and Н / и are different letters.
objectiveId=obj-u1-print-forms
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### INDEPENDENT_CHECK `u1-i-decode-pair`

- Objectives: obj-u1-decoding
- Sessions: u1-s-decoding, u1-s-check
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 455678bd09981ada59773d80047ba392e01a01a547fcbcf6084b0866e2c9ab5f

kind=PRINT_PAIR
language=en
prompt=Which line is one letter written in both print cases?
promptLetter=А
options=А / а | А / О | М / Т
answer=А / а
feedback=А / а is one letter. The other lines are two different letters.
objectiveId=obj-u1-decoding
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### INDEPENDENT_CHECK `u1-i-name-be`

- Objectives: obj-u1-letter-names
- Sessions: u1-s-letter-names
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 7199daf378b78cf0d5573aa308856bee21779a8b35dbe282dcb8798df0137189

kind=LETTER_IDENTITY
language=en
prompt=Which name belongs to this printed letter?
promptLetter=Б
options=бэ | вэ | пэ
answer=бэ
feedback=The printed letter Б is named бэ.
objectiveId=obj-u1-letter-names
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### REFERENCE `ref-transliteration`

- Objectives: none
- Sessions: u1-s-orient
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: LINGUISTIC_INSTRUCTION
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 103cedf7ae1136c7e0fc4fb199e8041a167084f65abf43f735f06d30a12d1264

title=Transliteration is temporary
summary=Latin respelling may help the first hours. It is not how you should keep reading.
detail=Turn transliteration off in settings as soon as print recognition is stable.
classification=LINGUISTIC_INSTRUCTION
introducedInUnit=1
searchTerms=transliteration is temporary
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### REFERENCE `ref-cyrillic-print`

- Objectives: none
- Sessions: u1-s-writing-system, u1-s-vowels, u1-s-early-consonants, u1-s-print
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: LINGUISTIC_INSTRUCTION
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 2c0db212d5e219bfd7afc15cbae2aebbf996866e339c1dffc69051bfd458e23c

title=Cyrillic print forms
summary=33 printed uppercase/lowercase pairs.
detail=Use the letter board grouped by the Unit 1 sequence. English sound notes are pending review. No example words are attached.
classification=LINGUISTIC_INSTRUCTION
introducedInUnit=1
searchTerms=cyrillic print forms
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### REFERENCE `ref-confusable`

- Objectives: none
- Sessions: u1-s-confusable
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: LINGUISTIC_INSTRUCTION
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 41caee9d8b908eed4697eae73a3cac0804e0dbb5d2fd4f2d8cb449b2241bfb14

title=Confusable Latin look-alikes
summary=В Н Р С У Х can be misread by Latin-alphabet readers.
detail=Name the Cyrillic identity. Confirm sounds in your book.
classification=LINGUISTIC_INSTRUCTION
introducedInUnit=1
searchTerms=confusable latin look-alikes
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### REFERENCE `ref-iotated`

- Objectives: none
- Sessions: u1-s-iotated
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: LINGUISTIC_INSTRUCTION
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 7ffaaaafee91dd0722e3756f66b03258c5421035ad87515f5ad8f861bc1ad0a2

title=Iotated vowel letters
summary=Е Ё Ю Я are a recognition group in Unit 1.
detail=Detailed phonetics remain in the book until a reviewer signs an app note.
classification=LINGUISTIC_INSTRUCTION
introducedInUnit=1
searchTerms=iotated vowel letters
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### REFERENCE `ref-signs`

- Objectives: none
- Sessions: u1-s-signs
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: LINGUISTIC_INSTRUCTION
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 8f1671d1489764866f46cf520238b7c1005b8528836f9232820b4d6be7169bf3

title=Soft sign, hard sign, and Й
summary=Ь and Ъ are signs. Й is not a standalone vowel in this foundation.
detail=Do not invent a spoken vowel for the signs. Use the book for the exact rule.
classification=LINGUISTIC_INSTRUCTION
introducedInUnit=1
searchTerms=soft sign, hard sign, and й
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### REFERENCE `ref-decoding`

- Objectives: none
- Sessions: u1-s-decoding
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: LINGUISTIC_INSTRUCTION
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: c2931f52a401860f57e557bbefc9a8b607bbecbf9a7ab72698dc8168787c8e0e

title=Staged decoding limits
summary=This pack decodes letters and print pairs only.
detail=Word, phrase, and sign reading wait for reviewed examples.
classification=LINGUISTIC_INSTRUCTION
introducedInUnit=1
searchTerms=staged decoding limits
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### REFERENCE `ref-pronunciation`

- Objectives: none
- Sessions: u1-s-pronunciation
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: LINGUISTIC_INSTRUCTION
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 52eb7ea1250759d4ac49243127eb796ead83531c46d8b0f9deb28e5c0456afd2

title=Pronunciation guidance
summary=Model audio is used only when a file is licensed and mapped to the current objective.
detail=User recordings play at original speed. No ASR or score.
classification=LINGUISTIC_INSTRUCTION
introducedInUnit=2
searchTerms=pronunciation guidance
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### REFERENCE `ref-stress`

- Objectives: none
- Sessions: u1-s-stress
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: LINGUISTIC_INSTRUCTION
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: dfb139ba8bbbed1639efdaad4f24214b462a5c65e5577948002488ab7e880379

title=Stress notation
summary=Stress is part of Russian reading and is not guessed by the app.
detail=Copy stress marks from your book into your binder.
classification=LINGUISTIC_INSTRUCTION
introducedInUnit=1
searchTerms=stress notation
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### REFERENCE `ref-ca-letter-names`

- Objectives: none
- Sessions: u1-s-letter-names
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: LINGUISTIC_INSTRUCTION
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: d3c9b4a2db950dfe839ebcaba6f37a973416b33e50aa59ef34e02f65cc5e6ff3

title=Printed letter names
summary=Unit 1 introduces the printed letter names used again in Unit 30.
detail=Unit 1 letter names, review-pending: А is named а; Б is named бэ; В is named вэ; Г is named гэ; Д is named дэ; Е is named е; Ё is named ё; Ж is named жэ; З is named зэ; И is named и; Й is named и краткое; К is named ка; Л is named эль; М is named эм; Н is named эн; О is named о; П is named пэ; Р is named эр; С is named эс; Т is named тэ; У is named у; Ф is named эф; Х is named ха; Ц is named цэ; Ч is named че; Ш is named ша; Щ is named ща; Ъ is named твёрдый знак; Ы is named ы; Ь is named мягкий знак; Э is named э; Ю is named ю; Я is named я.
classification=LINGUISTIC_INSTRUCTION
introducedInUnit=1
searchTerms=letter name,алфавит
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### LETTER `letter-0410`

- Objectives: obj-u1-letter-identity, obj-u1-print-forms, obj-u1-letter-names
- Sessions: u1-s-writing-system, u1-s-vowels, u1-s-print, u1-s-decoding, u1-s-letter-names
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 64cf5877019c1897e16f5986f4c6f74370c516d20ff4d15741fc460a73a7d208

uppercase=А
lowercase=а
group=vowel-plain
englishNote=A vowel. English notes treat it as a low open vowel similar to the vowel in father. Pending review.
latinLookalike=
audioAssetId=
exampleWordId=
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### LETTER `letter-0411`

- Objectives: obj-u1-letter-identity, obj-u2-review, obj-u1-letter-names
- Sessions: u1-s-early-consonants, u1-s-letter-names
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: ac07f726f73ef9117b8ef3e2b1db8ad5734dc29b5e90ed1dfb430d72632dd763

uppercase=Б
lowercase=б
group=consonant-early
englishNote=A voiced bilabial stop. Pending review.
latinLookalike=
audioAssetId=
exampleWordId=
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### LETTER `letter-0413`

- Objectives: obj-u1-letter-identity
- Sessions: none
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: f6a277b7e6b09539fdd2bb3aca67ac4f3175154280d45850be3b8cac6fe76fc4

uppercase=Г
lowercase=г
group=consonant-early
englishNote=A voiced velar stop in careful speech. Pending review.
latinLookalike=
audioAssetId=
exampleWordId=
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### LETTER `letter-0414`

- Objectives: obj-u1-letter-identity
- Sessions: none
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 4b650f327bdcb8b09ec6a5845134eeda9366e57467cdb4b9a529082c84054be7

uppercase=Д
lowercase=д
group=consonant-early
englishNote=A voiced dental/alveolar stop. Pending review.
latinLookalike=
audioAssetId=
exampleWordId=
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### LETTER `letter-0416`

- Objectives: obj-u1-letter-identity
- Sessions: none
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 7103364bd194f6f2d9ae925e6396c7e1eb71aec19c9ce3812c1612fb669763ff

uppercase=Ж
lowercase=ж
group=consonant-later
englishNote=A voiced postalveolar fricative. Pending review.
latinLookalike=
audioAssetId=
exampleWordId=
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### LETTER `letter-0417`

- Objectives: obj-u1-letter-identity
- Sessions: none
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 6fd7ab79daaed88daed9bdff23f500aeb19dc05996cd9131ba8c774d35469ec0

uppercase=З
lowercase=з
group=consonant-later
englishNote=A voiced dental/alveolar fricative. Pending review.
latinLookalike=
audioAssetId=
exampleWordId=
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### LETTER `letter-0418`

- Objectives: obj-u1-letter-identity
- Sessions: u1-s-vowels
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 69c135f892b2ab66693b2e20411c8865784e1a0b7656155aa8622a8ee8d89d21

uppercase=И
lowercase=и
group=vowel-plain
englishNote=A close front vowel letter. Pending review.
latinLookalike=
audioAssetId=
exampleWordId=
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### LETTER `letter-041a`

- Objectives: obj-u1-letter-identity
- Sessions: none
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: da5a9ec960f7c20a988b09025f2d7fe2148b07314724e6d72759bfff25ef6eed

uppercase=К
lowercase=к
group=consonant-early
englishNote=A voiceless velar stop. Pending review.
latinLookalike=
audioAssetId=
exampleWordId=
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### LETTER `letter-041b`

- Objectives: obj-u1-letter-identity
- Sessions: none
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 964c5daa2d3348cf9ac24b47fdf5d75aa991519142e58213a86dd09e444f40e7

uppercase=Л
lowercase=л
group=consonant-later
englishNote=A lateral approximant. Pending review.
latinLookalike=
audioAssetId=
exampleWordId=
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### LETTER `letter-041c`

- Objectives: obj-u1-letter-identity, obj-u1-print-forms
- Sessions: u1-s-writing-system, u1-s-early-consonants, u1-s-decoding
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: b075ef36e3ac806f207a9643cefed7379c11899347211b6d98f2ed21ee72f58f

uppercase=М
lowercase=м
group=consonant-early
englishNote=A bilabial nasal. Pending review.
latinLookalike=
audioAssetId=
exampleWordId=
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### LETTER `letter-041e`

- Objectives: obj-u1-letter-identity
- Sessions: u1-s-vowels
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 2294369721948b34476cff3fc1d04434bc0d5353036975fb8c2fed74d7cbd6a2

uppercase=О
lowercase=о
group=vowel-plain
englishNote=A mid back vowel letter. Unstressed value is a later listening topic. Pending review.
latinLookalike=
audioAssetId=
exampleWordId=
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### LETTER `letter-041f`

- Objectives: obj-u1-letter-identity
- Sessions: none
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 6cf8561ea39528ca41cced5b7914b40e097206a282d2324833b8459d968601a7

uppercase=П
lowercase=п
group=consonant-early
englishNote=A voiceless bilabial stop. Pending review.
latinLookalike=
audioAssetId=
exampleWordId=
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### LETTER `letter-0422`

- Objectives: obj-u1-letter-identity
- Sessions: u1-s-early-consonants
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 815be37ae28d1676b5a141da0d2faf6b3a0ff5c6f1c6c677301c25f93cb9bcb4

uppercase=Т
lowercase=т
group=consonant-early
englishNote=A voiceless dental/alveolar stop. Pending review.
latinLookalike=
audioAssetId=
exampleWordId=
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### LETTER `letter-0424`

- Objectives: obj-u1-letter-identity
- Sessions: none
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: ef554eb928bd6f996d77b3ab00d00449e0870b5e702752968b42154a426e1f66

uppercase=Ф
lowercase=ф
group=consonant-later
englishNote=A voiceless labiodental fricative. Pending review.
latinLookalike=
audioAssetId=
exampleWordId=
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### LETTER `letter-0426`

- Objectives: obj-u1-letter-identity
- Sessions: none
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 35c006f1ce6c8554adb1ff30ba04b8d41f0847b79cca75d58350fd80c182844e

uppercase=Ц
lowercase=ц
group=consonant-later
englishNote=An affricate. Pending review.
latinLookalike=
audioAssetId=
exampleWordId=
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### LETTER `letter-0427`

- Objectives: obj-u1-letter-identity
- Sessions: none
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: a98390e4fa717ac0cc09ea459edba2ed5269e71df112f311f1828e7d87ce414a

uppercase=Ч
lowercase=ч
group=consonant-later
englishNote=A palatal affricate. Pending review.
latinLookalike=
audioAssetId=
exampleWordId=
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### LETTER `letter-0428`

- Objectives: obj-u1-letter-identity
- Sessions: none
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 4dc3b75eb324483b753131a29891b15054dcac431dc1970caa2666bdadef8e75

uppercase=Ш
lowercase=ш
group=consonant-later
englishNote=A voiceless postalveolar fricative. Pending review.
latinLookalike=
audioAssetId=
exampleWordId=
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### LETTER `letter-0429`

- Objectives: obj-u1-letter-identity
- Sessions: none
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: a8cca0e747205ab22f500fbdd689298dca9825d4a525562dc082e26b92e9247a

uppercase=Щ
lowercase=щ
group=consonant-later
englishNote=A long palatal fricative in the standard description used here. Pending review.
latinLookalike=
audioAssetId=
exampleWordId=
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### LETTER `letter-042b`

- Objectives: obj-u1-letter-identity
- Sessions: none
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 226549516bc5971ac3249a757eaec56cf4c3feb84b0c25a68551c944ed4eecc9

uppercase=Ы
lowercase=ы
group=vowel-plain
englishNote=A close central/back unrounded vowel letter. Pending review.
latinLookalike=
audioAssetId=
exampleWordId=
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### LETTER `letter-042d`

- Objectives: obj-u1-letter-identity
- Sessions: none
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: ac9f95f4a102735349fe9e9281b1c440eb71c2f8b2a10b7f2b32c0fd51a691a7

uppercase=Э
lowercase=э
group=vowel-plain
englishNote=A mid front vowel letter used where iotation is not wanted. Pending review.
latinLookalike=
audioAssetId=
exampleWordId=
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### LETTER `letter-0412`

- Objectives: obj-u1-confusable
- Sessions: u1-s-confusable
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: af352438934764a2d33d5462a14cf0ec2a028f78423d7dab4002426a6427cb59

uppercase=В
lowercase=в
group=confusable
englishNote=Looks like Latin B. It is not the /b/ letter. Pending review.
latinLookalike=B
audioAssetId=
exampleWordId=
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### LETTER `letter-041d`

- Objectives: obj-u1-confusable
- Sessions: u1-s-confusable
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 3a8aa541bab0af668e85e52ab58e6da726d42021a1578024e99a6bc7ee5bd869

uppercase=Н
lowercase=н
group=confusable
englishNote=Looks like Latin H. It is the /n/ letter. Pending review.
latinLookalike=H
audioAssetId=
exampleWordId=
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### LETTER `letter-0420`

- Objectives: obj-u1-confusable
- Sessions: u1-s-confusable
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 41b106435b12c94597e7d2341dcea7fb910b05a97709816cd7328c00938e35d9

uppercase=Р
lowercase=р
group=confusable
englishNote=Looks like Latin P. It is the trilled /r/ letter. Pending review.
latinLookalike=P
audioAssetId=
exampleWordId=
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### LETTER `letter-0421`

- Objectives: obj-u1-confusable
- Sessions: u1-s-confusable
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: d6cb8404d56a918a7c0585e1f3fc0de09521279cef5393edf011d514d8ca1eaf

uppercase=С
lowercase=с
group=confusable
englishNote=Looks like Latin C. It is the /s/ letter. Pending review.
latinLookalike=C
audioAssetId=
exampleWordId=
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### LETTER `letter-0423`

- Objectives: obj-u1-confusable
- Sessions: u1-s-confusable
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: dfb193243cb3897e2fdc2e706fc45f66fd392df78f1c05eb9e5b650e72fd591d

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
- Sessions: u1-s-confusable
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: e55f4024159449cc908fdff769155c98e2b13e063f9bb446b1d1f6406d3452a7

uppercase=Х
lowercase=х
group=confusable
englishNote=Looks like Latin X. It is a voiceless velar fricative. Pending review.
latinLookalike=X
audioAssetId=
exampleWordId=
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### LETTER `letter-0415`

- Objectives: obj-u1-iotated
- Sessions: u1-s-iotated
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 290ebb331ae65a6b69c91596d71c0c37196755530a00974fda1a53c0eed41a01

uppercase=Е
lowercase=е
group=iotated
englishNote=An iotated vowel letter. After a consonant it can mark palatalization. Pending review.
latinLookalike=
audioAssetId=
exampleWordId=
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### LETTER `letter-0401`

- Objectives: obj-u1-iotated
- Sessions: u1-s-iotated
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: b9bb21572d33d1cfeae2494aa69cec5d7d0233a6e0fe1c092955d04f2c2456e5

uppercase=Ё
lowercase=ё
group=iotated
englishNote=An iotated vowel letter. Stress is usually on this vowel when the dots are printed. Pending review.
latinLookalike=
audioAssetId=
exampleWordId=
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### LETTER `letter-042e`

- Objectives: obj-u1-iotated
- Sessions: u1-s-iotated
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: c5e0480843ee119534bf984a1435c22af709fca12e8fdf3f7f100209295903d2

uppercase=Ю
lowercase=ю
group=iotated
englishNote=An iotated vowel letter. Pending review.
latinLookalike=
audioAssetId=
exampleWordId=
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### LETTER `letter-042f`

- Objectives: obj-u1-iotated
- Sessions: u1-s-iotated
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: b18c464983e3fc8c22810b6d0a239d76e654e0bfe9cad518e22e22cd317de245

uppercase=Я
lowercase=я
group=iotated
englishNote=An iotated vowel letter. Pending review.
latinLookalike=
audioAssetId=
exampleWordId=
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### LETTER `letter-0419`

- Objectives: obj-u1-signs
- Sessions: u1-s-signs
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 02a95a8c23bc96607fda69c3d3eeb9615115bc057ce95933d852cb96d1fc0d6c

uppercase=Й
lowercase=й
group=signs
englishNote=A palatal approximant letter, not a vowel by itself. Pending review.
latinLookalike=
audioAssetId=
exampleWordId=
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### LETTER `letter-042a`

- Objectives: obj-u1-signs
- Sessions: u1-s-signs
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 0e8256c90eb10e4c6a58927fd097ae391c15823bad91ca2a8ba3b8b7eb65450a

uppercase=Ъ
lowercase=ъ
group=signs
englishNote=The hard sign. It does not represent a spoken vowel. Pending review.
latinLookalike=
audioAssetId=
exampleWordId=
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### LETTER `letter-042c`

- Objectives: obj-u1-signs
- Sessions: u1-s-signs
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: d568e18b14da2805d00e04dd4cb3b6a406dbfb1fb205d6443525eeaef6ed5860

uppercase=Ь
lowercase=ь
group=signs
englishNote=The soft sign. It marks palatalization and is not a vowel. Pending review.
latinLookalike=
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

- `obj-u1-letter-identity` INSTRUCTION `u1-st-vowels`
- `obj-u1-letter-identity` INSTRUCTION `u1-st-cons`
- `obj-u1-letter-identity` INSTRUCTION `u1-st-system`
- `obj-u1-letter-identity` EXAMPLE `ex-letter-0410`
- `obj-u1-letter-identity` EXAMPLE `ex-letter-0411`
- `obj-u1-letter-identity` EXAMPLE `ex-letter-0413`
- `obj-u1-letter-identity` EXAMPLE `ex-letter-0414`
- `obj-u1-letter-identity` EXAMPLE `ex-letter-0416`
- `obj-u1-letter-identity` EXAMPLE `ex-letter-0417`
- `obj-u1-letter-identity` EXAMPLE `ex-letter-0418`
- `obj-u1-letter-identity` EXAMPLE `ex-letter-041a`
- `obj-u1-letter-identity` EXAMPLE `ex-letter-041b`
- `obj-u1-letter-identity` EXAMPLE `ex-letter-041c`
- `obj-u1-letter-identity` EXAMPLE `ex-letter-041e`
- `obj-u1-letter-identity` EXAMPLE `ex-letter-041f`
- `obj-u1-letter-identity` EXAMPLE `ex-letter-0422`
- `obj-u1-letter-identity` EXAMPLE `ex-letter-0424`
- `obj-u1-letter-identity` EXAMPLE `ex-letter-0426`
- `obj-u1-letter-identity` EXAMPLE `ex-letter-0427`
- `obj-u1-letter-identity` EXAMPLE `ex-letter-0428`
- `obj-u1-letter-identity` EXAMPLE `ex-letter-0429`
- `obj-u1-letter-identity` EXAMPLE `ex-letter-042b`
- `obj-u1-letter-identity` EXAMPLE `ex-letter-042d`
- `obj-u1-letter-identity` PRACTICE `u1-st-vowels-check`
- `obj-u1-letter-identity` PRACTICE `u1-st-cons-check`
- `obj-u1-letter-identity` CHECK `u1-i-а-identity`
- `obj-u1-letter-identity` CHECK `u1-i-м-identity`
- `obj-u1-letter-identity` REMEDIATION `u1-st-vowels`
- `obj-u1-letter-identity` REMEDIATION `u1-st-cons`
- `obj-u1-confusable` INSTRUCTION `u1-st-confusable`
- `obj-u1-confusable` EXAMPLE `ex-letter-0412`
- `obj-u1-confusable` EXAMPLE `ex-letter-041d`
- `obj-u1-confusable` EXAMPLE `ex-letter-0420`
- `obj-u1-confusable` EXAMPLE `ex-letter-0421`
- `obj-u1-confusable` EXAMPLE `ex-letter-0423`
- `obj-u1-confusable` EXAMPLE `ex-letter-0425`
- `obj-u1-confusable` PRACTICE `u1-st-guided-confusable`
- `obj-u1-confusable` PRACTICE `u1-st-confusable-r`
- `obj-u1-confusable` PRACTICE `u1-st-confusable-n`
- `obj-u1-confusable` PRACTICE `u1-st-confusable-s`
- `obj-u1-confusable` PRACTICE `u1-st-confusable-rest`
- `obj-u1-confusable` CHECK `u1-i-р-not-p`
- `obj-u1-confusable` CHECK `u1-i-н-not-h`
- `obj-u1-confusable` CHECK `u1-i-с-not-c`
- `obj-u1-confusable` CHECK `u1-i-в-not-b`
- `obj-u1-confusable` CHECK `u1-i-у-not-y`
- `obj-u1-confusable` CHECK `u1-i-х-not-x`
- `obj-u1-confusable` REMEDIATION `u1-st-confusable`
- `obj-u1-iotated` INSTRUCTION `u1-st-iotated`
- `obj-u1-iotated` EXAMPLE `ex-letter-0415`
- `obj-u1-iotated` EXAMPLE `ex-letter-0401`
- `obj-u1-iotated` EXAMPLE `ex-letter-042e`
- `obj-u1-iotated` EXAMPLE `ex-letter-042f`
- `obj-u1-iotated` PRACTICE `u1-st-guided-iotated`
- `obj-u1-iotated` PRACTICE `u1-st-iotated-check`
- `obj-u1-iotated` CHECK `u1-i-е-identity`
- `obj-u1-iotated` CHECK `u1-i-ё-identity`
- `obj-u1-iotated` CHECK `u1-i-ю-identity`
- `obj-u1-iotated` CHECK `u1-i-я-identity`
- `obj-u1-iotated` REMEDIATION `u1-st-iotated`
- `obj-u1-signs` INSTRUCTION `u1-st-signs`
- `obj-u1-signs` EXAMPLE `ex-letter-0419`
- `obj-u1-signs` EXAMPLE `ex-letter-042a`
- `obj-u1-signs` EXAMPLE `ex-letter-042c`
- `obj-u1-signs` PRACTICE `u1-st-guided-signs`
- `obj-u1-signs` PRACTICE `u1-st-signs-check`
- `obj-u1-signs` PRACTICE `u1-st-hard-check`
- `obj-u1-signs` PRACTICE `u1-st-short-i-check`
- `obj-u1-signs` CHECK `u1-i-ь-not-vowel`
- `obj-u1-signs` CHECK `u1-i-ъ-not-vowel`
- `obj-u1-signs` CHECK `u1-i-й-not-vowel`
- `obj-u1-signs` REMEDIATION `u1-st-signs`
- `obj-u1-print-forms` INSTRUCTION `u1-st-print`
- `obj-u1-print-forms` EXAMPLE `ex-print-letter-0410`
- `obj-u1-print-forms` EXAMPLE `ex-print-letter-041c`
- `obj-u1-print-forms` PRACTICE `u1-st-guided-print`
- `obj-u1-print-forms` PRACTICE `u1-st-print-check`
- `obj-u1-print-forms` CHECK `u1-i-print-pair`
- `obj-u1-print-forms` REMEDIATION `u1-st-print`
- `obj-u1-decoding` INSTRUCTION `u1-st-decoding`
- `obj-u1-decoding` EXAMPLE `ex-print-letter-0410`
- `obj-u1-decoding` EXAMPLE `ex-print-letter-041c`
- `obj-u1-decoding` PRACTICE `u1-st-guided-decoding`
- `obj-u1-decoding` PRACTICE `u1-st-decoding-check`
- `obj-u1-decoding` CHECK `u1-i-decode-pair`
- `obj-u1-decoding` REMEDIATION `u1-st-decoding`
- `obj-u1-pronunciation` INSTRUCTION `u1-st-pronunciation`
- `obj-u1-stress` INSTRUCTION `u1-st-stress`
- `obj-u1-book` INSTRUCTION `u1-st-book`
- `obj-u1-letter-names` INSTRUCTION `u1-st-teach-letter-names`
- `obj-u1-letter-names` EXAMPLE `ex-u1-name-a`
- `obj-u1-letter-names` EXAMPLE `ex-u1-name-be`
- `obj-u1-letter-names` PRACTICE `u1-st-guided-letter-names`
- `obj-u1-letter-names` CHECK `u1-i-name-be`
- `obj-u1-letter-names` REMEDIATION `u1-st-rem-letter-names`
- `u1-s-orient` OBJECTIVE `obj-u1-letter-identity`
- `u1-st-why` REFERENCE `ref-transliteration`
- `u1-s-writing-system` OBJECTIVE `obj-u1-letter-identity`
- `u1-st-system` EXAMPLE `ex-letter-0410`
- `u1-st-system` EXAMPLE `ex-letter-041c`
- `u1-st-system` REFERENCE `ref-cyrillic-print`
- `u1-s-vowels` OBJECTIVE `obj-u1-letter-identity`
- `u1-st-vowels` EXAMPLE `ex-letter-0410`
- `u1-st-vowels` EXAMPLE `ex-letter-041e`
- `u1-st-vowels` EXAMPLE `ex-letter-0418`
- `u1-st-vowels` REFERENCE `ref-cyrillic-print`
- `u1-st-vowels-check` REMEDIATION `u1-st-vowels`
- `u1-st-vowels-check` EXAMPLE `ex-letter-0410`
- `u1-st-vowels-check` ITEM `u1-i-а-identity`
- `u1-s-early-consonants` OBJECTIVE `obj-u1-letter-identity`
- `u1-st-cons` EXAMPLE `ex-letter-041c`
- `u1-st-cons` EXAMPLE `ex-letter-0422`
- `u1-st-cons` EXAMPLE `ex-letter-0411`
- `u1-st-cons` REFERENCE `ref-cyrillic-print`
- `u1-st-cons-check` REMEDIATION `u1-st-cons`
- `u1-st-cons-check` EXAMPLE `ex-letter-041c`
- `u1-st-cons-check` ITEM `u1-i-м-identity`
- `u1-s-confusable` OBJECTIVE `obj-u1-confusable`
- `u1-st-confusable` EXAMPLE `ex-letter-0420`
- `u1-st-confusable` EXAMPLE `ex-letter-041d`
- `u1-st-confusable` EXAMPLE `ex-letter-0421`
- `u1-st-confusable` REFERENCE `ref-confusable`
- `u1-st-guided-confusable` REMEDIATION `u1-st-confusable`
- `u1-st-guided-confusable` EXAMPLE `ex-letter-0420`
- `u1-st-guided-confusable` EXAMPLE `ex-letter-041d`
- `u1-st-guided-confusable` EXAMPLE `ex-letter-0421`
- `u1-st-guided-confusable` ITEM `u1-g-guided-confusable`
- `u1-st-confusable-r` REMEDIATION `u1-st-confusable`
- `u1-st-confusable-r` EXAMPLE `ex-letter-0420`
- `u1-st-confusable-r` ITEM `u1-i-р-not-p`
- `u1-st-confusable-n` REMEDIATION `u1-st-confusable`
- `u1-st-confusable-n` EXAMPLE `ex-letter-041d`
- `u1-st-confusable-n` ITEM `u1-i-н-not-h`
- `u1-st-confusable-s` REMEDIATION `u1-st-confusable`
- `u1-st-confusable-s` EXAMPLE `ex-letter-0421`
- `u1-st-confusable-s` ITEM `u1-i-с-not-c`
- `u1-st-confusable-rest` REMEDIATION `u1-st-confusable`
- `u1-st-confusable-rest` EXAMPLE `ex-letter-0412`
- `u1-st-confusable-rest` EXAMPLE `ex-letter-0423`
- `u1-st-confusable-rest` EXAMPLE `ex-letter-0425`
- `u1-st-confusable-rest` ITEM `u1-i-в-not-b`
- `u1-st-confusable-rest` ITEM `u1-i-у-not-y`
- `u1-st-confusable-rest` ITEM `u1-i-х-not-x`
- `u1-s-iotated` OBJECTIVE `obj-u1-iotated`
- `u1-st-iotated` EXAMPLE `ex-letter-0415`
- `u1-st-iotated` EXAMPLE `ex-letter-042f`
- `u1-st-iotated` REFERENCE `ref-iotated`
- `u1-st-guided-iotated` REMEDIATION `u1-st-iotated`
- `u1-st-guided-iotated` EXAMPLE `ex-letter-0415`
- `u1-st-guided-iotated` EXAMPLE `ex-letter-042f`
- `u1-st-guided-iotated` ITEM `u1-g-guided-iotated`
- `u1-st-iotated-check` REMEDIATION `u1-st-iotated`
- `u1-st-iotated-check` EXAMPLE `ex-letter-0415`
- `u1-st-iotated-check` EXAMPLE `ex-letter-0401`
- `u1-st-iotated-check` EXAMPLE `ex-letter-042e`
- `u1-st-iotated-check` EXAMPLE `ex-letter-042f`
- `u1-st-iotated-check` ITEM `u1-i-е-identity`
- `u1-st-iotated-check` ITEM `u1-i-ё-identity`
- `u1-st-iotated-check` ITEM `u1-i-ю-identity`
- `u1-st-iotated-check` ITEM `u1-i-я-identity`
- `u1-s-signs` OBJECTIVE `obj-u1-signs`
- `u1-st-signs` EXAMPLE `ex-letter-042c`
- `u1-st-signs` EXAMPLE `ex-letter-042a`
- `u1-st-signs` EXAMPLE `ex-letter-0419`
- `u1-st-signs` REFERENCE `ref-signs`
- `u1-st-guided-signs` REMEDIATION `u1-st-signs`
- `u1-st-guided-signs` EXAMPLE `ex-letter-042c`
- `u1-st-guided-signs` EXAMPLE `ex-letter-042a`
- `u1-st-guided-signs` EXAMPLE `ex-letter-0419`
- `u1-st-guided-signs` ITEM `u1-g-guided-signs`
- `u1-st-signs-check` REMEDIATION `u1-st-signs`
- `u1-st-signs-check` EXAMPLE `ex-letter-042c`
- `u1-st-signs-check` ITEM `u1-i-ь-not-vowel`
- `u1-st-hard-check` REMEDIATION `u1-st-signs`
- `u1-st-hard-check` EXAMPLE `ex-letter-042a`
- `u1-st-hard-check` ITEM `u1-i-ъ-not-vowel`
- `u1-st-short-i-check` REMEDIATION `u1-st-signs`
- `u1-st-short-i-check` EXAMPLE `ex-letter-0419`
- `u1-st-short-i-check` ITEM `u1-i-й-not-vowel`
- `u1-s-print` OBJECTIVE `obj-u1-print-forms`
- `u1-st-print` EXAMPLE `ex-print-letter-0410`
- `u1-st-print` REFERENCE `ref-cyrillic-print`
- `u1-st-guided-print` REMEDIATION `u1-st-print`
- `u1-st-guided-print` EXAMPLE `ex-print-letter-0410`
- `u1-st-guided-print` ITEM `u1-g-guided-print`
- `u1-st-print-check` REMEDIATION `u1-st-print`
- `u1-st-print-check` EXAMPLE `ex-print-letter-0410`
- `u1-st-print-check` ITEM `u1-i-print-pair`
- `u1-s-decoding` OBJECTIVE `obj-u1-decoding`
- `u1-st-decoding` EXAMPLE `ex-print-letter-0410`
- `u1-st-decoding` EXAMPLE `ex-print-letter-041c`
- `u1-st-decoding` REFERENCE `ref-decoding`
- `u1-st-guided-decoding` REMEDIATION `u1-st-decoding`
- `u1-st-guided-decoding` EXAMPLE `ex-print-letter-0410`
- `u1-st-guided-decoding` EXAMPLE `ex-print-letter-041c`
- `u1-st-guided-decoding` ITEM `u1-g-guided-decoding`
- `u1-st-decoding-check` REMEDIATION `u1-st-decoding`
- `u1-st-decoding-check` EXAMPLE `ex-print-letter-0410`
- `u1-st-decoding-check` ITEM `u1-i-decode-pair`
- `u1-s-pronunciation` OBJECTIVE `obj-u1-pronunciation`
- `u1-st-pronunciation` REFERENCE `ref-pronunciation`
- `u1-s-stress` OBJECTIVE `obj-u1-stress`
- `u1-st-stress` REFERENCE `ref-stress`
- `u1-s-book` OBJECTIVE `obj-u1-book`
- `u1-s-companions` OBJECTIVE `obj-u1-book`
- `u1-s-check` OBJECTIVE `obj-u1-letter-identity`
- `u1-s-check` OBJECTIVE `obj-u1-confusable`
- `u1-s-check` OBJECTIVE `obj-u1-print-forms`
- `u1-st-cumul` REMEDIATION `u1-st-confusable`
- `u1-st-cumul` ITEM `u1-i-а-identity`
- `u1-st-cumul` ITEM `u1-i-р-not-p`
- `u1-st-cumul` ITEM `u1-i-е-identity`
- `u1-st-cumul` ITEM `u1-i-ь-not-vowel`
- `u1-st-cumul` ITEM `u1-i-print-pair`
- `u1-st-cumul` ITEM `u1-i-decode-pair`
- `u1-s-letter-names` OBJECTIVE `obj-u1-letter-names`
- `u1-s-letter-names` OBJECTIVE `obj-u1-pronunciation`
- `u1-st-teach-letter-names` EXAMPLE `ex-u1-name-a`
- `u1-st-teach-letter-names` EXAMPLE `ex-u1-name-be`
- `u1-st-teach-letter-names` REFERENCE `ref-ca-letter-names`
- `u1-st-guided-letter-names` REMEDIATION `u1-st-rem-letter-names`
- `u1-st-guided-letter-names` EXAMPLE `ex-u1-name-a`
- `u1-st-guided-letter-names` ITEM `u1-g-name-a`
- `u1-st-guided-letter-names` REFERENCE `ref-ca-letter-names`
- `u1-st-rem-letter-names` REFERENCE `ref-ca-letter-names`
- `u1-st-check-letter-names` REMEDIATION `u1-st-rem-letter-names`
- `u1-st-check-letter-names` ITEM `u1-i-name-be`

## Book-directed experiences

- `u1-st-orient`
- `u1-st-decoding`
- `u1-st-guided-decoding`
- `u1-st-pronunciation`
- `u1-st-stress`
- `u1-st-book`
- `u1-st-teach-letter-names`
- `u1-st-cumulative-direction`

## Unavailable experiences

None.


## Unresolved relationships

None. Every referenced Unit 1 identifier resolves.
