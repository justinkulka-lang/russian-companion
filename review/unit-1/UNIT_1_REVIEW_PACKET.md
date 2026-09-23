# Unit 1 linguistic and owned-edition mapping packet

This packet prepares review. It records an unsigned structural mapping proposal. It does not record a qualified review, an approved edition alignment, or an install decision.

The owned-edition mapping worksheet separates observed edition facts, the proposed lesson and printed page range, unresolved edition questions, and matters that still require qualified linguistic review. ISBN, owned-copy verification, variance, reviewer identity, qualification, date, and approval stay blank. The disposition remains PENDING_CURRICULUM_REVIEWER.

Unit 1 remains in development. Its readiness is content-complete and review-pending. The Lesson 1 relationship is a proposed, unsigned mapping. It is not an approved edition alignment.

## Package identity

- Schema version: 3
- Application: dev.russiancompanion
- Version: 2.0.0-dev (5)
- Curriculum source commit: 4a6c21da07b515df226a090cc0ff6b1ff4a78aa1
- Unit content-review SHA-256: 9adca20544f28770210fe3029c2e0e4c437e3b409216e9288227c316e9ebf61a
- Mapping-package SHA-256: 3f4f2543804372be1d6ef62fb280d452bc017fbadc0dd6534e863c86be50050c
- Pack content-review SHA-256: 74b9d02735467b27ca3fab75e6b88c408d1950829b7826673a3244ec77e8504f
- Review-package SHA-256: 1335b22c65ebb01690b39239bdde49d2332ba8ff52d411ccbc3b04d5a5b3075c
- Repository source SHA-256: 70cea951b8f47f26387102e1ad30aaf618271c8a12d5b20bc3015b8cfdbae15e

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: ac6016538e32f5248a79efa112112696c23e9229466416c3ad2ccbfdc41a9ee9

status=PENDING_QUALIFIED_REVIEW
reviewer=
reviewerQualification=
packVersion=
reviewedContentDigest=
curriculumMappingId=
notes=No qualified reviewer has signed Unit 1.

### UNIT `unit-1`

- Objectives: obj-u1-letter-identity, obj-u1-confusable, obj-u1-iotated, obj-u1-signs, obj-u1-print-forms, obj-u1-decoding, obj-u1-pronunciation, obj-u1-stress, obj-u1-book
- Sessions: u1-s-orient, u1-s-writing-system, u1-s-vowels, u1-s-early-consonants, u1-s-confusable, u1-s-iotated, u1-s-signs, u1-s-print, u1-s-decoding, u1-s-pronunciation, u1-s-stress, u1-s-book, u1-s-companions, u1-s-check
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 44642b5621e62dddfe93ecacda8301dcb40db3c7f3a72d2655e7f97847434abe

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
- Content hash: 53159a07da74f47a6c6346163a87d3f327c3a10ab775219891509aca97a70ed6

title=Separate Cyrillic letters from Latin look-alikes
kind=RECOGNITION
unitNumber=1
instruction=u1-st-confusable
examples=ex-letter-0412,ex-letter-041d,ex-letter-0420,ex-letter-0421,ex-letter-0423,ex-letter-0425
practice=u1-st-confusable-r,u1-st-confusable-n,u1-st-confusable-s,u1-st-confusable-rest
checks=u1-i-р-not-p,u1-i-н-not-h,u1-i-с-not-c,u1-i-в-not-b,u1-i-у-not-y,u1-i-х-not-x
remediation=u1-st-confusable

### OBJECTIVE `obj-u1-iotated`

- Objectives: obj-u1-iotated
- Sessions: none
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 221d6c108655548f531650bebe10b25ff8b63721af4698f14c1b42d0e2b859ab

title=Recognize Е Ё Ю Я as a group
kind=RECOGNITION
unitNumber=1
instruction=u1-st-iotated
examples=ex-letter-0415,ex-letter-0401,ex-letter-042e,ex-letter-042f
practice=u1-st-iotated-check
checks=u1-i-е-identity,u1-i-ё-identity,u1-i-ю-identity,u1-i-я-identity
remediation=u1-st-iotated

### OBJECTIVE `obj-u1-signs`

- Objectives: obj-u1-signs
- Sessions: none
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 650c5e8649a6c6ee0337e9355c4e57cd10f1a9821bd7570ade578f19a3e6a506

title=Treat Ь Ъ Й as non-ordinary-vowel letters
kind=RECOGNITION
unitNumber=1
instruction=u1-st-signs
examples=ex-letter-0419,ex-letter-042a,ex-letter-042c
practice=u1-st-signs-check,u1-st-hard-check,u1-st-short-i-check
checks=u1-i-ь-not-vowel,u1-i-ъ-not-vowel,u1-i-й-not-vowel
remediation=u1-st-signs

### OBJECTIVE `obj-u1-print-forms`

- Objectives: obj-u1-print-forms
- Sessions: none
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: e3176d209ac80cf63027b3af16eb27bf7b4046c970ebb4b469b9131916c0848f

title=Match uppercase and lowercase print pairs
kind=RECOGNITION
unitNumber=1
instruction=u1-st-print
examples=ex-print-letter-0410,ex-print-letter-041c
practice=u1-st-print-check
checks=u1-i-print-pair
remediation=u1-st-print

### OBJECTIVE `obj-u1-decoding`

- Objectives: obj-u1-decoding
- Sessions: none
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 9c55a56cbaa27c6bf8f562a53a4263aff452fc1deafbf9123f10ecaa272e3b99

title=Decode a printed letter pair
kind=RECOGNITION
unitNumber=1
instruction=u1-st-decoding
examples=ex-print-letter-0410,ex-print-letter-041c
practice=u1-st-decoding-check
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
- Content hash: 2466f3e1b9909fc72d63ab1e1a02ba91311609352db29045927297cb3161e0ee

title=Letters that look like Latin
estimatedMinutes=15
objectives=obj-u1-confusable
steps=u1-st-confusable,u1-st-confusable-r,u1-st-confusable-n,u1-st-confusable-s,u1-st-confusable-rest

### STEP `u1-st-confusable`

- Objectives: obj-u1-confusable
- Sessions: u1-s-confusable
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 128016542dfa041319d0651bc7609ea5f02d292ec3160e296cb1d8e17ad94a2c

kind=TEACH
title=Shape traps
body=These printed letters often fool Latin-alphabet readers: В, Н, Р, С, У, Х. Name the Cyrillic identity.
dominantAction=Continue
references=ref-confusable
items=
examples=ex-letter-0420,ex-letter-041d,ex-letter-0421
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

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
- Content hash: 8e5759a3c97e6b808ec4bad9c3ab141f78c8c19b45cc1eeacc831a6159449d11

title=Iotated vowel letters
estimatedMinutes=10
objectives=obj-u1-iotated
steps=u1-st-iotated,u1-st-iotated-check

### STEP `u1-st-iotated`

- Objectives: obj-u1-iotated
- Sessions: u1-s-iotated
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 253b89b14b3d418415f6ebf22e5fbe968225041bfd6edbb225df340947d71878

kind=TEACH
title=Е Ё Ю Я
body=Recognize these printed forms as a group. Exact phonetics belong in your book.
dominantAction=Continue
references=ref-iotated
items=
examples=ex-letter-0415,ex-letter-042f
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

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
- Content hash: b5e4bd0027f9049879baf64d364a08532995c41f7d9a368cd69321b7a92cf3e4

title=Soft sign, hard sign, and Й
estimatedMinutes=10
objectives=obj-u1-signs
steps=u1-st-signs,u1-st-signs-check,u1-st-hard-check,u1-st-short-i-check

### STEP `u1-st-signs`

- Objectives: obj-u1-signs
- Sessions: u1-s-signs
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: caafbcbba9319dfd95e99995d0d7267a43ef70e94a3241c6582fbd42060ef78f

kind=TEACH
title=Marks that are not ordinary vowels
body=Ь, Ъ, and Й need special attention. Do not invent a spoken vowel for the signs.
dominantAction=Continue
references=ref-signs
items=
examples=ex-letter-042c,ex-letter-042a,ex-letter-0419
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

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
- Content hash: 88ab3a50ac338cb0d8b73d03b93d0cc76ea29d57eed372894b4c5f8341d54244

title=Print forms
estimatedMinutes=10
objectives=obj-u1-print-forms
steps=u1-st-print,u1-st-print-check

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
- Content hash: 253ca3980a3d8a2fc81b5b229a2e1d5acdc3ee268fe7a34590e8802b066cd152

title=Staged decoding
estimatedMinutes=8
objectives=obj-u1-decoding
steps=u1-st-decoding,u1-st-decoding-check

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
- Content hash: 78960536ec172893b37a253c3d15fb38a1f0bfa680eec4eba9b2c3573c08ce44

kind=BOOK
title=Lesson 1 in your copy
body=Complete the Lesson 1 reading and pronunciation work in the edition you own.
dominantAction=Continue
references=
items=
examples=
unavailableReason=
bookDirection=Read and do Lesson 1 in your book. Use the book's answer key, not this app, for book exercises.
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

### EXAMPLE `ex-letter-0410`

- Objectives: obj-u1-letter-identity
- Sessions: u1-s-writing-system, u1-s-vowels
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 1cf995b9653bacba43d6d623a73d5a52b8095b84ce34e2f819ef050c13ad6fdc

kind=PRINT_PAIR
letterId=letter-0410
display=А / а
note=Printed pair А а.
objectiveId=obj-u1-letter-identity
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-letter-0411`

- Objectives: obj-u1-letter-identity
- Sessions: u1-s-early-consonants
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 04c82d3ef852399595d354f9acd8a3247a5ba7c90bbb49a58ef1d06f79b3ceca

kind=PRINT_PAIR
letterId=letter-0411
display=Б / б
note=Printed pair Б б.
objectiveId=obj-u1-letter-identity
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-letter-0413`

- Objectives: obj-u1-letter-identity
- Sessions: none
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 071a24a96b7888ffad1ad617a74dbd66fa5b31b1de60d2a40fa8e4f4a94c93de

kind=LETTER_FORM
letterId=letter-0413
display=Г / г
note=Printed pair Г г.
objectiveId=obj-u1-letter-identity
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-letter-0414`

- Objectives: obj-u1-letter-identity
- Sessions: none
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 92834a83fe2054002205047263f3a9ccfaae57196bae4c08b70b28168792d65d

kind=LETTER_FORM
letterId=letter-0414
display=Д / д
note=Printed pair Д д.
objectiveId=obj-u1-letter-identity
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-letter-0416`

- Objectives: obj-u1-letter-identity
- Sessions: none
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: a28c4f1d920fe44472e46cf3c6a2dd77a4f7c7c1ea3dcabf2777a123a134e7d4

kind=LETTER_FORM
letterId=letter-0416
display=Ж / ж
note=Printed pair Ж ж.
objectiveId=obj-u1-letter-identity
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-letter-0417`

- Objectives: obj-u1-letter-identity
- Sessions: none
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 98cfb72051f7ac42da045890c1e6b5f4dbac724a3aaea870127a663eda08b526

kind=LETTER_FORM
letterId=letter-0417
display=З / з
note=Printed pair З з.
objectiveId=obj-u1-letter-identity
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-letter-0418`

- Objectives: obj-u1-letter-identity
- Sessions: u1-s-vowels
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 96e0dbdae005c16f5ee9e0d3643baf608005b431dcfee01dd1b26c31c9827430

kind=PRINT_PAIR
letterId=letter-0418
display=И / и
note=Printed pair И и.
objectiveId=obj-u1-letter-identity
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-letter-041a`

- Objectives: obj-u1-letter-identity
- Sessions: none
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 16f951eae3257a6c1327d0b5987282173f976e18f04fe1379d1c04d678dfec20

kind=LETTER_FORM
letterId=letter-041a
display=К / к
note=Printed pair К к.
objectiveId=obj-u1-letter-identity
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-letter-041b`

- Objectives: obj-u1-letter-identity
- Sessions: none
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: fc622286b91edf4f5bbea7fa1997c4ee570e52cf3d53d1162c5d7fa41f0f3fed

kind=LETTER_FORM
letterId=letter-041b
display=Л / л
note=Printed pair Л л.
objectiveId=obj-u1-letter-identity
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-letter-041c`

- Objectives: obj-u1-letter-identity
- Sessions: u1-s-writing-system, u1-s-early-consonants
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 3c924f52db199c5830770e2efe6cf74ddb9850786439285470c0d5c4c874b072

kind=PRINT_PAIR
letterId=letter-041c
display=М / м
note=Printed pair М м.
objectiveId=obj-u1-letter-identity
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-letter-041e`

- Objectives: obj-u1-letter-identity
- Sessions: u1-s-vowels
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: bd76711235eda8439215c4cb7a5f316c59063d265a9ad0e4eda6283a4cd5e875

kind=PRINT_PAIR
letterId=letter-041e
display=О / о
note=Printed pair О о.
objectiveId=obj-u1-letter-identity
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-letter-041f`

- Objectives: obj-u1-letter-identity
- Sessions: none
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 0edbeae15b67bc71d3e940b99a139d0a935a2f64cb8ae960e9e37e81f954b2d3

kind=LETTER_FORM
letterId=letter-041f
display=П / п
note=Printed pair П п.
objectiveId=obj-u1-letter-identity
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-letter-0422`

- Objectives: obj-u1-letter-identity
- Sessions: u1-s-early-consonants
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 8add23fc12e49b183d8574d96635fdc423701d95d65783115aeccc62a32d3489

kind=PRINT_PAIR
letterId=letter-0422
display=Т / т
note=Printed pair Т т.
objectiveId=obj-u1-letter-identity
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-letter-0424`

- Objectives: obj-u1-letter-identity
- Sessions: none
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 3accd75f36901f01c0f2b140a34330f99b799a61446eadd408540824f7bbde59

kind=LETTER_FORM
letterId=letter-0424
display=Ф / ф
note=Printed pair Ф ф.
objectiveId=obj-u1-letter-identity
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-letter-0426`

- Objectives: obj-u1-letter-identity
- Sessions: none
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: c8f0c08534c4b387e26cef2e5f33543688d8191c2795ded8c114e811ca29cd3f

kind=LETTER_FORM
letterId=letter-0426
display=Ц / ц
note=Printed pair Ц ц.
objectiveId=obj-u1-letter-identity
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-letter-0427`

- Objectives: obj-u1-letter-identity
- Sessions: none
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 70aac9f0cc1d57925c1c31b01c0195c20b4f0789145ac7e6d7783e0edbf76c0c

kind=LETTER_FORM
letterId=letter-0427
display=Ч / ч
note=Printed pair Ч ч.
objectiveId=obj-u1-letter-identity
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-letter-0428`

- Objectives: obj-u1-letter-identity
- Sessions: none
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 63602b4129122bdd7ff93a92e9b7fac373ce39bcb5a59e457a7e4c084a23661f

kind=LETTER_FORM
letterId=letter-0428
display=Ш / ш
note=Printed pair Ш ш.
objectiveId=obj-u1-letter-identity
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-letter-0429`

- Objectives: obj-u1-letter-identity
- Sessions: none
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: f7c2f249737ba17cb94cd9c4e3ede8e669239593d1fd68fe288d19ab98f87976

kind=LETTER_FORM
letterId=letter-0429
display=Щ / щ
note=Printed pair Щ щ.
objectiveId=obj-u1-letter-identity
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-letter-042b`

- Objectives: obj-u1-letter-identity
- Sessions: none
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 09a342fdab82004b8a6b3f610ee72fe2c2a01231f0a604271d60c6c8d3594a21

kind=LETTER_FORM
letterId=letter-042b
display=Ы / ы
note=Printed pair Ы ы.
objectiveId=obj-u1-letter-identity
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-letter-042d`

- Objectives: obj-u1-letter-identity
- Sessions: none
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 153b7bb90ef1f626fcf30461082d891d4567d06dfd0fe22f452aada58c7be0bf

kind=LETTER_FORM
letterId=letter-042d
display=Э / э
note=Printed pair Э э.
objectiveId=obj-u1-letter-identity
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-letter-0412`

- Objectives: obj-u1-confusable
- Sessions: u1-s-confusable
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 0366174966a4fdf463119d148e772465c656153c0dea0a185a5848b1c5d042da

kind=CONFUSABLE_PAIR
letterId=letter-0412
display=В / в
note=Printed pair В в. Treat the Cyrillic identity, not a Latin look-alike.
objectiveId=obj-u1-confusable
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-letter-041d`

- Objectives: obj-u1-confusable
- Sessions: u1-s-confusable
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 685e0f293195355475e585e0d3c39dcc36ab2c17707be2b348dec3df164f3094

kind=CONFUSABLE_PAIR
letterId=letter-041d
display=Н / н
note=Printed pair Н н. Treat the Cyrillic identity, not a Latin look-alike.
objectiveId=obj-u1-confusable
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-letter-0420`

- Objectives: obj-u1-confusable
- Sessions: u1-s-confusable
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 8b99de37dc1e6bfb23c4b41d58382d65ccd366348e46cd14bee94b35579d2857

kind=CONFUSABLE_PAIR
letterId=letter-0420
display=Р / р
note=Printed pair Р р. Treat the Cyrillic identity, not a Latin look-alike.
objectiveId=obj-u1-confusable
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-letter-0421`

- Objectives: obj-u1-confusable
- Sessions: u1-s-confusable
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: ef8f3a40ff8e1dab6a9d4f3df31bce8e5e07f60ee7fc685b616cfb37c5c60776

kind=CONFUSABLE_PAIR
letterId=letter-0421
display=С / с
note=Printed pair С с. Treat the Cyrillic identity, not a Latin look-alike.
objectiveId=obj-u1-confusable
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-letter-0423`

- Objectives: obj-u1-confusable
- Sessions: u1-s-confusable
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 203d975b6ca272c22f8ec32f29d3930cf0eebbb80b56fb163723fe3f9cb41b95

kind=CONFUSABLE_PAIR
letterId=letter-0423
display=У / у
note=Printed pair У у. Treat the Cyrillic identity, not a Latin look-alike.
objectiveId=obj-u1-confusable
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-letter-0425`

- Objectives: obj-u1-confusable
- Sessions: u1-s-confusable
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 60ccd4a3e4a9b7069593bcb5f9746cfa705135a9bd5fc7ae8e5175b9aebe185f

kind=CONFUSABLE_PAIR
letterId=letter-0425
display=Х / х
note=Printed pair Х х. Treat the Cyrillic identity, not a Latin look-alike.
objectiveId=obj-u1-confusable
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-letter-0415`

- Objectives: obj-u1-iotated
- Sessions: u1-s-iotated
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 0b7982c09647ba911b63fcc92139b32688a461fe1d5ad67597f30845559b5928

kind=LETTER_FORM
letterId=letter-0415
display=Е / е
note=Printed pair Е е.
objectiveId=obj-u1-iotated
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-letter-0401`

- Objectives: obj-u1-iotated
- Sessions: u1-s-iotated
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 207913352165dd70e5259c0298146186fe7559f3447d764b54f1116c3a451202

kind=LETTER_FORM
letterId=letter-0401
display=Ё / ё
note=Printed pair Ё ё.
objectiveId=obj-u1-iotated
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-letter-042e`

- Objectives: obj-u1-iotated
- Sessions: u1-s-iotated
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 51e1710ebaedfed546640bd36080fa11245025eac6550134704849b15837d034

kind=LETTER_FORM
letterId=letter-042e
display=Ю / ю
note=Printed pair Ю ю.
objectiveId=obj-u1-iotated
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-letter-042f`

- Objectives: obj-u1-iotated
- Sessions: u1-s-iotated
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 990e10dba143202bcd7a46b9816e3a9fd08678574b6d4bc11f07e14f11e54fe9

kind=LETTER_FORM
letterId=letter-042f
display=Я / я
note=Printed pair Я я.
objectiveId=obj-u1-iotated
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-letter-0419`

- Objectives: obj-u1-signs
- Sessions: u1-s-signs
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 97e3f8923dbfd25ebf829c2c561f53bca016cd06657327eb19beb1c25c7cec1e

kind=LETTER_FORM
letterId=letter-0419
display=Й / й
note=Printed pair Й й.
objectiveId=obj-u1-signs
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-letter-042a`

- Objectives: obj-u1-signs
- Sessions: u1-s-signs
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 481d1444f940fdb438a40e6e6af1e73442ef105488aedc0749406d7e1d2592fb

kind=LETTER_FORM
letterId=letter-042a
display=Ъ / ъ
note=Printed pair Ъ ъ.
objectiveId=obj-u1-signs
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-letter-042c`

- Objectives: obj-u1-signs
- Sessions: u1-s-signs
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 01c3bb98ee791e0ddaf967b40f125f110b8f27e45ac4e5f89dd3fa42b116e862

kind=LETTER_FORM
letterId=letter-042c
display=Ь / ь
note=Printed pair Ь ь.
objectiveId=obj-u1-signs
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-print-letter-0410`

- Objectives: obj-u1-print-forms
- Sessions: u1-s-print, u1-s-decoding
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 97e40eb3e8e958e25e3695796433e13f3e45696f207cdf2ef06b99acca60a6ed

kind=PRINT_PAIR
letterId=letter-0410
display=А / а
note=А and а are the same letter in two print cases.
objectiveId=obj-u1-print-forms
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-print-letter-041c`

- Objectives: obj-u1-print-forms
- Sessions: u1-s-decoding
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 6d5531dbcf82d0c605cf53112bd678eb987c468b3c638ddbc3adf689f33488bd

kind=PRINT_PAIR
letterId=letter-041c
display=М / м
note=М and м are the same letter in two print cases.
objectiveId=obj-u1-print-forms
reviewStatus=PENDING_QUALIFIED_REVIEW

### GUIDED_AND_INDEPENDENT_CHECK `u1-i-а-identity`

- Objectives: obj-u1-letter-identity
- Sessions: u1-s-vowels, u1-s-check
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: b0dd7cb4ada3148c4de81ad8c81ffc7441a4d8caf3aed2ae8b1140bc292c466c

kind=LETTER_IDENTITY
language=en
prompt=Which printed letter is shown?
promptLetter=А
options=Latin A | Cyrillic А | Cyrillic Р
answer=Cyrillic А
feedback=The printed shape can match Latin A. In this course it is the Cyrillic vowel letter А.
objectiveId=obj-u1-letter-identity
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW

### GUIDED_PRACTICE `u1-i-м-identity`

- Objectives: obj-u1-letter-identity
- Sessions: u1-s-early-consonants
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 79352187c8024a48be297f63a62189740d3d3fdc747642a1f859622593ff33d2

kind=LETTER_IDENTITY
language=en
prompt=Which printed letter is shown?
promptLetter=М
options=Cyrillic М | Cyrillic Н | Cyrillic Т
answer=Cyrillic М
feedback=М is an early consonant letter. It is not Н.
objectiveId=obj-u1-letter-identity
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW

### INDEPENDENT_CHECK `u1-i-р-not-p`

- Objectives: obj-u1-confusable
- Sessions: u1-s-confusable, u1-s-check
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 643f33176652c5a5f2c8f7425861b1a9ae842a97b403a34cc5dce46ae580be8d

kind=CONFUSABLE
language=en
prompt=This letter looks like Latin P. What is it?
promptLetter=Р
options=The Latin letter P | The Cyrillic letter Р | The Cyrillic letter П
answer=The Cyrillic letter Р
feedback=Shape overlap with Latin P is a recognition trap. This is Cyrillic Р, not English P.
objectiveId=obj-u1-confusable
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW

### INDEPENDENT_CHECK `u1-i-н-not-h`

- Objectives: obj-u1-confusable
- Sessions: u1-s-confusable
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 3677eb784471ac9fa4184ac7dc8ead333fa5c6e3d449cc0a61f9ebefd0175d65

kind=CONFUSABLE
language=en
prompt=This letter looks like Latin H. What is it?
promptLetter=Н
options=Latin H | Cyrillic Н | Cyrillic И
answer=Cyrillic Н
feedback=Do not read Н as English H. It is a Cyrillic letter.
objectiveId=obj-u1-confusable
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW

### INDEPENDENT_CHECK `u1-i-с-not-c`

- Objectives: obj-u1-confusable
- Sessions: u1-s-confusable
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: dc2acd3cd9cfea2f41fc483136d913c946dfb73542528442a2384286991fd80a

kind=CONFUSABLE
language=en
prompt=This letter looks like Latin C. What is it?
promptLetter=С
options=Latin C | Cyrillic С | Cyrillic К
answer=Cyrillic С
feedback=С is a Cyrillic letter. Do not treat the Latin look-alike as the identity.
objectiveId=obj-u1-confusable
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW

### INDEPENDENT_CHECK `u1-i-в-not-b`

- Objectives: obj-u1-confusable
- Sessions: u1-s-confusable
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 7aa84caba1025831dbf32804bf5745bcb4a2910ccc88c38e3c8e5971ffad6d07

kind=CONFUSABLE
language=en
prompt=This letter looks like Latin B. What is it?
promptLetter=В
options=Latin B | Cyrillic В | Cyrillic Б
answer=Cyrillic В
feedback=В is not Latin B and it is not Cyrillic Б.
objectiveId=obj-u1-confusable
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW

### INDEPENDENT_CHECK `u1-i-у-not-y`

- Objectives: obj-u1-confusable
- Sessions: u1-s-confusable
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 50cf3721908230639384ab7c376e368fab4fcd8760b2af1f86cbeaedea50a65c

kind=CONFUSABLE
language=en
prompt=This letter looks like Latin Y. What is it?
promptLetter=У
options=Latin Y | Cyrillic У | Cyrillic И
answer=Cyrillic У
feedback=У is a Cyrillic vowel letter, not Latin Y.
objectiveId=obj-u1-confusable
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW

### INDEPENDENT_CHECK `u1-i-х-not-x`

- Objectives: obj-u1-confusable
- Sessions: u1-s-confusable
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 16a252f1f23a69b68175f49547d9c9db2b631c021d8b973eba406343d2748a53

kind=CONFUSABLE
language=en
prompt=This letter looks like Latin X. What is it?
promptLetter=Х
options=Latin X | Cyrillic Х | Cyrillic К
answer=Cyrillic Х
feedback=Х is a Cyrillic letter, not Latin X.
objectiveId=obj-u1-confusable
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW

### INDEPENDENT_CHECK `u1-i-е-identity`

- Objectives: obj-u1-iotated
- Sessions: u1-s-iotated, u1-s-check
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 53a4f8f31177bb3c03de087c7ac4a929dd552c9c0d77b1cd1117daccc5755eff

kind=LETTER_IDENTITY
language=en
prompt=Which printed letter is shown?
promptLetter=Е
options=Cyrillic Е | Cyrillic Э | Cyrillic Ё
answer=Cyrillic Е
feedback=Е is an iotated vowel letter. Э and Ё are different letters.
objectiveId=obj-u1-iotated
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW

### INDEPENDENT_CHECK `u1-i-ё-identity`

- Objectives: obj-u1-iotated
- Sessions: u1-s-iotated
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 845510880391f05e647e31bc8b9e9fbaf8dede0d4d7ee2d6d461fbf8705e5092

kind=LETTER_IDENTITY
language=en
prompt=Which printed letter is shown?
promptLetter=Ё
options=Cyrillic Ё | Cyrillic Е | Cyrillic Э
answer=Cyrillic Ё
feedback=Ё is its own letter. Е and Э are different letters.
objectiveId=obj-u1-iotated
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW

### INDEPENDENT_CHECK `u1-i-ю-identity`

- Objectives: obj-u1-iotated
- Sessions: u1-s-iotated
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: d2471fc6b39465fe8f1224c9a066505926aa266c598ca2d104b8b0d53cbcee5d

kind=LETTER_IDENTITY
language=en
prompt=Which printed letter is shown?
promptLetter=Ю
options=Cyrillic Ю | Cyrillic У | Cyrillic Я
answer=Cyrillic Ю
feedback=Ю is an iotated vowel letter. У and Я are different letters.
objectiveId=obj-u1-iotated
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW

### INDEPENDENT_CHECK `u1-i-я-identity`

- Objectives: obj-u1-iotated
- Sessions: u1-s-iotated
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 6f68aaa9bf7858427bb81cb44ec69d013ae6efe6644b3f65adf390a490e64d81

kind=LETTER_IDENTITY
language=en
prompt=Which printed letter is shown?
promptLetter=Я
options=Cyrillic Я | Cyrillic Р | Cyrillic А
answer=Cyrillic Я
feedback=Я is an iotated vowel letter. It is not Р or А.
objectiveId=obj-u1-iotated
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW

### INDEPENDENT_CHECK `u1-i-ь-not-vowel`

- Objectives: obj-u1-signs
- Sessions: u1-s-signs, u1-s-check
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 3dbdb62bec39b10bc8eeb4674c9d1022f7c716aab58e959c5c3724ce0edbdf84

kind=SIGN
language=en
prompt=What is the printed mark Ь?
promptLetter=Ь
options=A vowel you pronounce by itself | A mark, not a standalone vowel | The same letter as Ъ
answer=A mark, not a standalone vowel
feedback=Ь is a sign, not a vowel letter you read alone.
objectiveId=obj-u1-signs
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW

### INDEPENDENT_CHECK `u1-i-ъ-not-vowel`

- Objectives: obj-u1-signs
- Sessions: u1-s-signs
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 23f545100463d9db841b8300cdf9ba5cef217f4d1047faf3b09c1a223b36e4e4

kind=SIGN
language=en
prompt=What is the printed mark Ъ?
promptLetter=Ъ
options=A standalone vowel | The hard sign, not a vowel | The same letter as Ь
answer=The hard sign, not a vowel
feedback=Ъ is the hard sign. It is not a spoken vowel and it is not Ь.
objectiveId=obj-u1-signs
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW

### INDEPENDENT_CHECK `u1-i-й-not-vowel`

- Objectives: obj-u1-signs
- Sessions: u1-s-signs
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: c173443c5ba1430d1c3245de5598a46e4f2a82f9ae75d657b9e302c261f0b9bb

kind=SIGN
language=en
prompt=What is the printed letter Й?
promptLetter=Й
options=A standalone vowel | A consonant letter, not a vowel | The same mark as Ь
answer=A consonant letter, not a vowel
feedback=Й is not a vowel you read alone and it is not the soft sign.
objectiveId=obj-u1-signs
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW

### INDEPENDENT_CHECK `u1-i-print-pair`

- Objectives: obj-u1-print-forms
- Sessions: u1-s-print, u1-s-check
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 246407b492863015ee79c849f6cf7af27fac15e717d0e087a6c3ffda2bb8b5aa

kind=PRINT_PAIR
language=en
prompt=Which pair is the same letter in uppercase and lowercase print?
promptLetter=А
options=А / а | Р / п | Н / и
answer=А / а
feedback=Print pairs are one letter in two cases. Р / п and Н / и are different letters.
objectiveId=obj-u1-print-forms
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW

### INDEPENDENT_CHECK `u1-i-decode-pair`

- Objectives: obj-u1-decoding
- Sessions: u1-s-decoding, u1-s-check
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 4ef578407bc1c0f4aa96ca6c2417a40c4e522202bdcc6e2af2475ad6e9541012

kind=PRINT_PAIR
language=en
prompt=Which line is one letter written in both print cases?
promptLetter=А
options=А / а | А / О | М / Т
answer=А / а
feedback=А / а is one letter. The other lines are two different letters.
objectiveId=obj-u1-decoding
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW

### REFERENCE `ref-transliteration`

- Objectives: none
- Sessions: u1-s-orient
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: LINGUISTIC_INSTRUCTION
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 5ad5ed568ae87454cf66d918364fbccd6130bf4c33d24007f3ebebf9181d13a9

title=Transliteration is temporary
summary=Latin respelling may help the first hours. It is not how you should keep reading.
detail=Turn transliteration off in settings as soon as print recognition is stable.
classification=LINGUISTIC_INSTRUCTION
introducedInUnit=1
searchTerms=transliteration is temporary
reviewStatus=PENDING_QUALIFIED_REVIEW

### REFERENCE `ref-cyrillic-print`

- Objectives: none
- Sessions: u1-s-writing-system, u1-s-vowels, u1-s-early-consonants, u1-s-print
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: LINGUISTIC_INSTRUCTION
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: d1a562b7f9cc607e8bc3f474d64c2175269f455c5159c6df82b493cf800575e8

title=Cyrillic print forms
summary=33 printed uppercase/lowercase pairs.
detail=Use the letter board grouped by the Unit 1 sequence. English sound notes are pending review. No example words are attached.
classification=LINGUISTIC_INSTRUCTION
introducedInUnit=1
searchTerms=cyrillic print forms
reviewStatus=PENDING_QUALIFIED_REVIEW

### REFERENCE `ref-confusable`

- Objectives: none
- Sessions: u1-s-confusable
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: LINGUISTIC_INSTRUCTION
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 219aa5bbb06be37f1021f8b0c81939a8231734ce79812d5c007e76b241db01b2

title=Confusable Latin look-alikes
summary=В Н Р С У Х can be misread by Latin-alphabet readers.
detail=Name the Cyrillic identity. Confirm sounds in your book.
classification=LINGUISTIC_INSTRUCTION
introducedInUnit=1
searchTerms=confusable latin look-alikes
reviewStatus=PENDING_QUALIFIED_REVIEW

### REFERENCE `ref-iotated`

- Objectives: none
- Sessions: u1-s-iotated
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: LINGUISTIC_INSTRUCTION
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: ebda3702a58799cab38b3b7ea05fee77761eae86de599e702c60a9fbed280c42

title=Iotated vowel letters
summary=Е Ё Ю Я are a recognition group in Unit 1.
detail=Detailed phonetics remain in the book until a reviewer signs an app note.
classification=LINGUISTIC_INSTRUCTION
introducedInUnit=1
searchTerms=iotated vowel letters
reviewStatus=PENDING_QUALIFIED_REVIEW

### REFERENCE `ref-signs`

- Objectives: none
- Sessions: u1-s-signs
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: LINGUISTIC_INSTRUCTION
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 56043e0e46f70b20cb2d04668a21f43897381a77a62ce61f20cd2ec893f16d04

title=Soft sign, hard sign, and Й
summary=Ь and Ъ are signs. Й is not a standalone vowel in this foundation.
detail=Do not invent a spoken vowel for the signs. Use the book for the exact rule.
classification=LINGUISTIC_INSTRUCTION
introducedInUnit=1
searchTerms=soft sign, hard sign, and й
reviewStatus=PENDING_QUALIFIED_REVIEW

### REFERENCE `ref-decoding`

- Objectives: none
- Sessions: u1-s-decoding
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: LINGUISTIC_INSTRUCTION
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 51c4d253237ca465ceabdf924157462a52e05205a57bd246e53b883cc5db04f1

title=Staged decoding limits
summary=This pack decodes letters and print pairs only.
detail=Word, phrase, and sign reading wait for reviewed examples.
classification=LINGUISTIC_INSTRUCTION
introducedInUnit=1
searchTerms=staged decoding limits
reviewStatus=PENDING_QUALIFIED_REVIEW

### REFERENCE `ref-pronunciation`

- Objectives: none
- Sessions: u1-s-pronunciation
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: LINGUISTIC_INSTRUCTION
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 17f30392b3ca028f329fce85f342f044f6c9366c0e934379d5680eac9c9232e9

title=Pronunciation guidance
summary=Model audio is used only when a file is licensed and mapped to the current objective.
detail=User recordings play at original speed. No ASR or score.
classification=LINGUISTIC_INSTRUCTION
introducedInUnit=2
searchTerms=pronunciation guidance
reviewStatus=PENDING_QUALIFIED_REVIEW

### REFERENCE `ref-stress`

- Objectives: none
- Sessions: u1-s-stress
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: LINGUISTIC_INSTRUCTION
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 05120d56aba324ece71d05915dc39930370d73ab07f7f7609470bf837443d68f

title=Stress notation
summary=Stress is part of Russian reading and is not guessed by the app.
detail=Copy stress marks from your book into your binder.
classification=LINGUISTIC_INSTRUCTION
introducedInUnit=1
searchTerms=stress notation
reviewStatus=PENDING_QUALIFIED_REVIEW

### LETTER `letter-0410`

- Objectives: obj-u1-letter-identity, obj-u1-print-forms
- Sessions: u1-s-writing-system, u1-s-vowels, u1-s-print, u1-s-decoding
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: cf0fd1a72f13e5ea26ad2bf359f612ab4f556629e52bcae2221a8fc816c0353c

uppercase=А
lowercase=а
group=vowel-plain
englishNote=A vowel. English notes treat it as a low open vowel similar to the vowel in father. Pending review.
latinLookalike=
audioAssetId=
exampleWordId=
reviewStatus=PENDING_QUALIFIED_REVIEW

### LETTER `letter-0411`

- Objectives: obj-u1-letter-identity, obj-u2-review
- Sessions: u1-s-early-consonants
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 7f2beeb8c5f09bd3bcb2b853ba69d63608dddce74063eff27fd44c70c71558f4

uppercase=Б
lowercase=б
group=consonant-early
englishNote=A voiced bilabial stop. Pending review.
latinLookalike=
audioAssetId=
exampleWordId=
reviewStatus=PENDING_QUALIFIED_REVIEW

### LETTER `letter-0413`

- Objectives: obj-u1-letter-identity
- Sessions: none
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 57c100372b4e32306d70bc5df58813d3f2f5e71ba3668d7b285932aabefc2a82

uppercase=Г
lowercase=г
group=consonant-early
englishNote=A voiced velar stop in careful speech. Pending review.
latinLookalike=
audioAssetId=
exampleWordId=
reviewStatus=PENDING_QUALIFIED_REVIEW

### LETTER `letter-0414`

- Objectives: obj-u1-letter-identity
- Sessions: none
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: c5d29e3bee0987d5323fc3234e13a9a82ffd4428d805b9f68188620056c9b5d4

uppercase=Д
lowercase=д
group=consonant-early
englishNote=A voiced dental/alveolar stop. Pending review.
latinLookalike=
audioAssetId=
exampleWordId=
reviewStatus=PENDING_QUALIFIED_REVIEW

### LETTER `letter-0416`

- Objectives: obj-u1-letter-identity
- Sessions: none
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: b556f36f35e4d22f80943d971e9ebbe30c561c80f80bd1300a1a927cb1d11aff

uppercase=Ж
lowercase=ж
group=consonant-later
englishNote=A voiced postalveolar fricative. Pending review.
latinLookalike=
audioAssetId=
exampleWordId=
reviewStatus=PENDING_QUALIFIED_REVIEW

### LETTER `letter-0417`

- Objectives: obj-u1-letter-identity
- Sessions: none
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 60f26c23b007cacc09565b50b1162b2aeff60ec62a0e69631ed72d99cbe23c59

uppercase=З
lowercase=з
group=consonant-later
englishNote=A voiced dental/alveolar fricative. Pending review.
latinLookalike=
audioAssetId=
exampleWordId=
reviewStatus=PENDING_QUALIFIED_REVIEW

### LETTER `letter-0418`

- Objectives: obj-u1-letter-identity
- Sessions: u1-s-vowels
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 1f5961b7522bb768f71495674c1920962a0205dc8d74381829fdcc2862c7fee2

uppercase=И
lowercase=и
group=vowel-plain
englishNote=A close front vowel letter. Pending review.
latinLookalike=
audioAssetId=
exampleWordId=
reviewStatus=PENDING_QUALIFIED_REVIEW

### LETTER `letter-041a`

- Objectives: obj-u1-letter-identity
- Sessions: none
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: bf4c1e084feaab012116294c0385b2ea2d63ef3ae59f636345eb69d0f0d54fd8

uppercase=К
lowercase=к
group=consonant-early
englishNote=A voiceless velar stop. Pending review.
latinLookalike=
audioAssetId=
exampleWordId=
reviewStatus=PENDING_QUALIFIED_REVIEW

### LETTER `letter-041b`

- Objectives: obj-u1-letter-identity
- Sessions: none
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: daa5e7b206d8c0c36505419a68a29acec599cceb9e303ede97d9f21a212266ad

uppercase=Л
lowercase=л
group=consonant-later
englishNote=A lateral approximant. Pending review.
latinLookalike=
audioAssetId=
exampleWordId=
reviewStatus=PENDING_QUALIFIED_REVIEW

### LETTER `letter-041c`

- Objectives: obj-u1-letter-identity, obj-u1-print-forms
- Sessions: u1-s-writing-system, u1-s-early-consonants, u1-s-decoding
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: b161db6572bff0ec0cdd4bfecc4bb2ca0d76ca0f67beb3a46a680469b23f4c22

uppercase=М
lowercase=м
group=consonant-early
englishNote=A bilabial nasal. Pending review.
latinLookalike=
audioAssetId=
exampleWordId=
reviewStatus=PENDING_QUALIFIED_REVIEW

### LETTER `letter-041e`

- Objectives: obj-u1-letter-identity
- Sessions: u1-s-vowels
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 9c3e9a21b7fa0d9e098ae1365f9f0fe43d3c67ec41eab910289a2714c83d2af0

uppercase=О
lowercase=о
group=vowel-plain
englishNote=A mid back vowel letter. Unstressed value is a later listening topic. Pending review.
latinLookalike=
audioAssetId=
exampleWordId=
reviewStatus=PENDING_QUALIFIED_REVIEW

### LETTER `letter-041f`

- Objectives: obj-u1-letter-identity
- Sessions: none
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 3fc8cc634a824df76633ee1635f339020c6689534322ca664d79ee1cd5c2e073

uppercase=П
lowercase=п
group=consonant-early
englishNote=A voiceless bilabial stop. Pending review.
latinLookalike=
audioAssetId=
exampleWordId=
reviewStatus=PENDING_QUALIFIED_REVIEW

### LETTER `letter-0422`

- Objectives: obj-u1-letter-identity
- Sessions: u1-s-early-consonants
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 2f26ff22c132941cc32f552938cb45d039bc46a75d6748b4cdc2777a552b5314

uppercase=Т
lowercase=т
group=consonant-early
englishNote=A voiceless dental/alveolar stop. Pending review.
latinLookalike=
audioAssetId=
exampleWordId=
reviewStatus=PENDING_QUALIFIED_REVIEW

### LETTER `letter-0424`

- Objectives: obj-u1-letter-identity
- Sessions: none
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: d2e029d101967f3392cb20d0a4504669a4da03ff5e8b5fa04218c90e419a6085

uppercase=Ф
lowercase=ф
group=consonant-later
englishNote=A voiceless labiodental fricative. Pending review.
latinLookalike=
audioAssetId=
exampleWordId=
reviewStatus=PENDING_QUALIFIED_REVIEW

### LETTER `letter-0426`

- Objectives: obj-u1-letter-identity
- Sessions: none
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 8a6bc49dd6f69ae32d022681403241de2ce58c6419fc26bebbbab83b67c4ae40

uppercase=Ц
lowercase=ц
group=consonant-later
englishNote=An affricate. Pending review.
latinLookalike=
audioAssetId=
exampleWordId=
reviewStatus=PENDING_QUALIFIED_REVIEW

### LETTER `letter-0427`

- Objectives: obj-u1-letter-identity
- Sessions: none
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 5724577a5ecbe9dfa158ba1bbc80cd2b0bf0f94d6dea4c6ed4b7f5ca7afa40c1

uppercase=Ч
lowercase=ч
group=consonant-later
englishNote=A palatal affricate. Pending review.
latinLookalike=
audioAssetId=
exampleWordId=
reviewStatus=PENDING_QUALIFIED_REVIEW

### LETTER `letter-0428`

- Objectives: obj-u1-letter-identity
- Sessions: none
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 14b33aeddfbec71a966887fbb99f4ae57bf7373760a2de31e91427c4f046b4b6

uppercase=Ш
lowercase=ш
group=consonant-later
englishNote=A voiceless postalveolar fricative. Pending review.
latinLookalike=
audioAssetId=
exampleWordId=
reviewStatus=PENDING_QUALIFIED_REVIEW

### LETTER `letter-0429`

- Objectives: obj-u1-letter-identity
- Sessions: none
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 3b44d6b9972e681db81635c4e47a0b39ce6c1ca2f8215e4a1b2346c68d29f761

uppercase=Щ
lowercase=щ
group=consonant-later
englishNote=A long palatal fricative in the standard description used here. Pending review.
latinLookalike=
audioAssetId=
exampleWordId=
reviewStatus=PENDING_QUALIFIED_REVIEW

### LETTER `letter-042b`

- Objectives: obj-u1-letter-identity
- Sessions: none
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 4dbce4389355cb57395811a127623e33a35848c30f3ded979ffb577117ada986

uppercase=Ы
lowercase=ы
group=vowel-plain
englishNote=A close central/back unrounded vowel letter. Pending review.
latinLookalike=
audioAssetId=
exampleWordId=
reviewStatus=PENDING_QUALIFIED_REVIEW

### LETTER `letter-042d`

- Objectives: obj-u1-letter-identity
- Sessions: none
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 05bea9b874c8cc69d5ff0da51064f2a9e6b75b066ec6385f823448f376212784

uppercase=Э
lowercase=э
group=vowel-plain
englishNote=A mid front vowel letter used where iotation is not wanted. Pending review.
latinLookalike=
audioAssetId=
exampleWordId=
reviewStatus=PENDING_QUALIFIED_REVIEW

### LETTER `letter-0412`

- Objectives: obj-u1-confusable
- Sessions: u1-s-confusable
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 0fa60dcf7a0fd5eec2ac0035ad677b2fa02e667133e7321f3a58357bf669a87e

uppercase=В
lowercase=в
group=confusable
englishNote=Looks like Latin B. It is not the /b/ letter. Pending review.
latinLookalike=B
audioAssetId=
exampleWordId=
reviewStatus=PENDING_QUALIFIED_REVIEW

### LETTER `letter-041d`

- Objectives: obj-u1-confusable
- Sessions: u1-s-confusable
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 25a5ef50825c0917533483c1a8b9daccda721ddb626dc6bbee41bab45d9e83bd

uppercase=Н
lowercase=н
group=confusable
englishNote=Looks like Latin H. It is the /n/ letter. Pending review.
latinLookalike=H
audioAssetId=
exampleWordId=
reviewStatus=PENDING_QUALIFIED_REVIEW

### LETTER `letter-0420`

- Objectives: obj-u1-confusable
- Sessions: u1-s-confusable
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 81d4b035e44c90e0bdba6d01f85beea5b338ee34607d3017579ba7ea33cad104

uppercase=Р
lowercase=р
group=confusable
englishNote=Looks like Latin P. It is the trilled /r/ letter. Pending review.
latinLookalike=P
audioAssetId=
exampleWordId=
reviewStatus=PENDING_QUALIFIED_REVIEW

### LETTER `letter-0421`

- Objectives: obj-u1-confusable
- Sessions: u1-s-confusable
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 2ef5bb190fc9546069a1d5eeeb6762cf573a650a4d12d05b0d10c10eb8e40d27

uppercase=С
lowercase=с
group=confusable
englishNote=Looks like Latin C. It is the /s/ letter. Pending review.
latinLookalike=C
audioAssetId=
exampleWordId=
reviewStatus=PENDING_QUALIFIED_REVIEW

### LETTER `letter-0423`

- Objectives: obj-u1-confusable
- Sessions: u1-s-confusable
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 99d06a826ff5e467c283482d6932e66eeb40bb6b1297bc4cee2c6495736d1cd2

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
- Sessions: u1-s-confusable
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 20cc75a64ee5f990c9569b11cfe06ca61d6c74f3eef5a434e28afec1a754ba35

uppercase=Х
lowercase=х
group=confusable
englishNote=Looks like Latin X. It is a voiceless velar fricative. Pending review.
latinLookalike=X
audioAssetId=
exampleWordId=
reviewStatus=PENDING_QUALIFIED_REVIEW

### LETTER `letter-0415`

- Objectives: obj-u1-iotated
- Sessions: u1-s-iotated
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 3428315bed364e01aa44b64d0486be77974f4278fc470d73695c80df19ac729b

uppercase=Е
lowercase=е
group=iotated
englishNote=An iotated vowel letter. After a consonant it can mark palatalization. Pending review.
latinLookalike=
audioAssetId=
exampleWordId=
reviewStatus=PENDING_QUALIFIED_REVIEW

### LETTER `letter-0401`

- Objectives: obj-u1-iotated
- Sessions: u1-s-iotated
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: a38b405d38adda7bcdc27187422ffe7bdc2a8b152e197f51e08aaae1de4adddd

uppercase=Ё
lowercase=ё
group=iotated
englishNote=An iotated vowel letter. Stress is usually on this vowel when the dots are printed. Pending review.
latinLookalike=
audioAssetId=
exampleWordId=
reviewStatus=PENDING_QUALIFIED_REVIEW

### LETTER `letter-042e`

- Objectives: obj-u1-iotated
- Sessions: u1-s-iotated
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 46b7969a5cf558d0e5906170d9adb4cf9c3ed0ade7afa0ef20103212c7eaab6d

uppercase=Ю
lowercase=ю
group=iotated
englishNote=An iotated vowel letter. Pending review.
latinLookalike=
audioAssetId=
exampleWordId=
reviewStatus=PENDING_QUALIFIED_REVIEW

### LETTER `letter-042f`

- Objectives: obj-u1-iotated
- Sessions: u1-s-iotated
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: f349025e696975e3d332f7b052d3f703d2a28b7401688d2f62378d2f68495df3

uppercase=Я
lowercase=я
group=iotated
englishNote=An iotated vowel letter. Pending review.
latinLookalike=
audioAssetId=
exampleWordId=
reviewStatus=PENDING_QUALIFIED_REVIEW

### LETTER `letter-0419`

- Objectives: obj-u1-signs
- Sessions: u1-s-signs
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 2fef72586fcc3f055728ffe3351d07df6dc4130d3bedbfc247449b6096d1c553

uppercase=Й
lowercase=й
group=signs
englishNote=A palatal approximant letter, not a vowel by itself. Pending review.
latinLookalike=
audioAssetId=
exampleWordId=
reviewStatus=PENDING_QUALIFIED_REVIEW

### LETTER `letter-042a`

- Objectives: obj-u1-signs
- Sessions: u1-s-signs
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: a68dada089c939291d546e14e653b1cd1a43844a2f198dbc6c7e5e7e773469bd

uppercase=Ъ
lowercase=ъ
group=signs
englishNote=The hard sign. It does not represent a spoken vowel. Pending review.
latinLookalike=
audioAssetId=
exampleWordId=
reviewStatus=PENDING_QUALIFIED_REVIEW

### LETTER `letter-042c`

- Objectives: obj-u1-signs
- Sessions: u1-s-signs
- Source: src-unicode-cyrillic
- Rights: rights-unicode-cyrillic
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: d47ac5cec7882fa5282ba864d6961bb5bec105d67d7dc333d8e29eb2f27c0b25

uppercase=Ь
lowercase=ь
group=signs
englishNote=The soft sign. It marks palatalization and is not a vowel. Pending review.
latinLookalike=
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
- `obj-u1-print-forms` PRACTICE `u1-st-print-check`
- `obj-u1-print-forms` CHECK `u1-i-print-pair`
- `obj-u1-print-forms` REMEDIATION `u1-st-print`
- `obj-u1-decoding` INSTRUCTION `u1-st-decoding`
- `obj-u1-decoding` EXAMPLE `ex-print-letter-0410`
- `obj-u1-decoding` EXAMPLE `ex-print-letter-041c`
- `obj-u1-decoding` PRACTICE `u1-st-decoding-check`
- `obj-u1-decoding` CHECK `u1-i-decode-pair`
- `obj-u1-decoding` REMEDIATION `u1-st-decoding`
- `obj-u1-pronunciation` INSTRUCTION `u1-st-pronunciation`
- `obj-u1-stress` INSTRUCTION `u1-st-stress`
- `obj-u1-book` INSTRUCTION `u1-st-book`
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
- `u1-st-print-check` REMEDIATION `u1-st-print`
- `u1-st-print-check` EXAMPLE `ex-print-letter-0410`
- `u1-st-print-check` ITEM `u1-i-print-pair`
- `u1-s-decoding` OBJECTIVE `obj-u1-decoding`
- `u1-st-decoding` EXAMPLE `ex-print-letter-0410`
- `u1-st-decoding` EXAMPLE `ex-print-letter-041c`
- `u1-st-decoding` REFERENCE `ref-decoding`
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

## Book-directed experiences

- `u1-st-orient`
- `u1-st-decoding`
- `u1-st-pronunciation`
- `u1-st-stress`
- `u1-st-book`

## Unavailable experiences

None.


## Unresolved relationships

None. Every referenced Unit 1 identifier resolves.
