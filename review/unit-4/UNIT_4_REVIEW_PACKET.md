# Unit 4 linguistic and owned-edition mapping packet

This packet prepares review. It records an unsigned structural mapping proposal. It does not record a qualified review, an approved edition alignment, or an install decision.

The owned-edition mapping worksheet separates observed edition facts, the proposed lesson and printed page range, unresolved edition questions, and matters that still require qualified linguistic review. ISBN, owned-copy verification, variance, reviewer identity, qualification, date, and approval stay blank. The disposition remains PENDING_CURRICULUM_REVIEWER.

Unit 4 remains in development. Its readiness is content-complete and review-pending. The Lesson 4 relationship is a proposed, unsigned mapping. It is not an approved edition alignment.

## Package identity

- Schema version: 3
- Application: dev.russiancompanion
- Version: 2.0.0-dev (5)
- Curriculum source commit: b22fa0cca895a677386a77d03a63301f7454aa07
- Unit content-review SHA-256: 5a9be201954e4a4c37188ed7aca9fae1ee67e20622c7134271c1a68cd520e59c
- Mapping-package SHA-256: 889c15ed7bf5d3189af391210f35bae5c57a9fd9c9dc3b3a439e8ebc1143b6fd
- Pack content-review SHA-256: ff2d4cd848036f7b164f9a68d24ef3d26d3442f4673c2bf1cf560e8d1b8662f1
- Review-package SHA-256: 5c83907c84d7bb8caaced3455abde7e30c8c9e415c31c03d807ca67cc33cf5df
- Repository source SHA-256: 87b29b045c071c76ffd23288977fe4f5a370c7c406f9060d01ee985497803f84

`unitContentReviewDigest` binds this unit's instructional curriculum. It covers this unit and the reachable instructional records, relationships, shared dependencies, sources, rights, assets, and advancement rule. It does not cover any other unit. Recording a review status, reviewer signature, mapping approval, install state, or readiness does not change it.

`mappingPackageDigest` binds the proposed edition-mapping facts for Unit 4: edition identity, lesson, printed page range, topic description, observation source, ISBN observation state, and the pending disposition. Reviewer identity, qualification, date, notes, correction notes, and signature stay outside that digest.

`packContentReviewDigest` is the repository aggregate of instructional curriculum. It is not an approval of this unit, and it is not part of the review-package digest.

`reviewPackageDigest` identifies this complete pending review package. It binds the ordered instructional records and the mapping-package digest. Changing a proposed page range or topic changes the mapping-package digest and therefore this package digest.

`repositorySourceDigest` binds repository and release inputs. Recording an approval later changes that repository hash. That change does not replace the signed content-review digest or the mapping-package digest.

The marker scan used by this package detects only a fixed list of known prohibited mapping-input markers. It is not comprehensive copyright detection and cannot recognize every copy or close paraphrase of protected material.

## Unit

number=4
title=Verbs, personal pronouns, present tense, conjugation, word stress, and introduction to nouns and cases
topicSummary=Proposed Unit 4 is a development unit awaiting qualified review. It contains admitted pronoun, present-tense, written-stress, and nominative records, plus separate book self-attestation. Written stress is not a pronunciation score. It is not installed and the owned-edition mapping is not signed.
installState=DEVELOPMENT
readiness=CONTENT_COMPLETE_REVIEW_PENDING
mappingStatus=PENDING_CURRICULUM_REVIEWER
mappingNote=Proposed Lesson 4 mapping remains pending. The structural edition facts are unchanged. This proposed curriculum is not an approved alignment.
penguinLessonField=4
prerequisites=3
estimatedMinutes=66

## Not yet approved

No reviewer has signed this packet. Reviewer identity, qualification, review date, disposition, and reviewed digest are blank. Do not treat this development unit as reviewed, installable, or installed.

## Records

### UNIT_REVIEW `rev-pending-unit-4`

- Objectives: none
- Sessions: none
- Source: none
- Rights: none
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 499e2907007380b141cbcb81fde9ca4830097d33311aa60372b9cf308419b42e

status=IMPLEMENTED_PENDING_FINAL_AUDIT
reviewer=
reviewerQualification=
packVersion=
reviewedContentDigest=
curriculumMappingId=
notes=No qualified reviewer has signed Unit 4.

### UNIT `unit-4`

- Objectives: obj-u4-pronouns, obj-u4-present, obj-u4-stress, obj-u4-nominative, obj-u4-book
- Sessions: u4-s-orient, u4-s-pronouns, u4-s-present, u4-s-stress, u4-s-nominative, u4-s-book
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: e90352df437fea9f534d48627e8c7b92d6582845a016a8f1af0a32553d073402

number=4
title=Verbs, personal pronouns, present tense, conjugation, word stress, and introduction to nouns and cases
topicSummary=Proposed Unit 4 is a development unit awaiting qualified review. It contains admitted pronoun, present-tense, written-stress, and nominative records, plus separate book self-attestation. Written stress is not a pronunciation score. It is not installed and the owned-edition mapping is not signed.
installState=DEVELOPMENT
readiness=CONTENT_COMPLETE_REVIEW_PENDING
mappingStatus=PENDING_CURRICULUM_REVIEWER
mappingNote=Proposed Lesson 4 mapping remains pending. The structural edition facts are unchanged. This proposed curriculum is not an approved alignment.
penguinLessonField=4
prerequisites=3
estimatedMinutes=66

### ADVANCEMENT `unit-4-advancement`

- Objectives: none
- Sessions: none
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 7da0b4f2ceae8da0d7700b3f226da496d4e834c36b79b74acb737fa36ae8cdbd

requireInstruction=true
requireBookSelfAttestation=true
requireAppChecks=true
minimumDistinctChecks=14
requireDueReview=false
tutorOptional=true

### OBJECTIVE `obj-u4-pronouns`

- Objectives: obj-u4-pronouns
- Sessions: none
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 9c9be0291a6addc5e70d48359c8e50f5417660d8a5b781ed219774bc2b61ecf5

title=Recognize admitted personal pronouns
kind=RECOGNITION
unitNumber=4
instruction=u4-st-orient,u4-st-teach-pronouns-1,u4-st-teach-pronouns-2,u4-st-teach-pronouns-3
examples=ex-u4-я,ex-u4-мы,ex-u4-ты,ex-u4-вы,ex-u4-он,ex-u4-она,ex-u4-оно,ex-u4-они
practice=u4-st-guided-pronouns-1
checks=u4-i-pronoun-ты,u4-i-pronoun-вы,u4-i-pronoun-он,u4-i-pronoun-она,u4-i-pronoun-оно,u4-i-pronoun-они
remediation=u4-st-rem-pronouns

### OBJECTIVE `obj-u4-present`

- Objectives: obj-u4-present
- Sessions: none
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: a45ffc99673943001bcfce2f2ced11552d8a483bb3ec9c9f1d341bbdf62a9651

title=Recognize admitted present-tense forms
kind=RECOGNITION
unitNumber=4
instruction=u4-st-orient,u4-st-teach-present-1,u4-st-teach-present-2,u4-st-teach-present-3,u4-st-teach-present-4,u4-st-teach-present-5,u4-st-teach-present-6,u4-st-teach-present-7
examples=ex-u4-verb-читать,ex-u4-verb-делать
practice=u4-st-guided-present-2,u4-st-guided-present-4
checks=u4-i-verb-говорить-мы,u4-i-verb-видеть-он,u4-i-verb-писать-вы,u4-i-verb-идти-они
remediation=u4-st-rem-present

### OBJECTIVE `obj-u4-stress`

- Objectives: obj-u4-stress
- Sessions: none
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: c8027d4637371241281c6b10bf228976f34d77d4bcb9d901927b73f658b48238

title=Notice the admitted written stress mark
kind=RECOGNITION
unitNumber=4
instruction=u4-st-orient,u4-st-teach-stress-1
examples=ex-u4-stress-6f15e9f1,ex-u4-stress-1f37b6b2,ex-u4-stress-2114b12f
practice=u4-st-guided-stress-1
checks=u4-i-stress-1f37b6b2,u4-i-stress-2114b12f
remediation=u4-st-rem-stress

### OBJECTIVE `obj-u4-nominative`

- Objectives: obj-u4-nominative
- Sessions: none
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 22f10f19825ed15fe01616b7416d33797dbcb099623c916bc2e03698b7f7ee99

title=Recognize the admitted nominative role
kind=RECOGNITION
unitNumber=4
instruction=u4-st-orient,u4-st-teach-nominative-1,u4-st-teach-nominative-2,u4-st-teach-nominative-3
examples=ex-u4-nom-muzh,ex-u4-nom-poezd
practice=u4-st-teach-nominative-1
checks=u4-i-nom-muzh,u4-i-nom-poezd
remediation=u4-st-rem-nominative

### OBJECTIVE `obj-u4-book`

- Objectives: obj-u4-book
- Sessions: none
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: fee39cd11f07b0243f3ffbdb3c446d68741601438c2131db65376d1bd600c032

title=Self-attest Lesson 4 book work
kind=SELF_ATTESTATION
unitNumber=4
instruction=u4-st-book-work,u4-st-book
examples=
practice=
checks=
remediation=

### SESSION `u4-s-orient`

- Objectives: obj-u4-pronouns, obj-u4-present, obj-u4-stress, obj-u4-nominative, obj-u4-book
- Sessions: u4-s-orient
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 8e6382390550043e48d26a68657d89f5888af6ea4400c3c38232216d542190c7

title=Person words, present forms, a written mark, and dictionary-form examples
estimatedMinutes=8
objectives=obj-u4-pronouns,obj-u4-present,obj-u4-stress,obj-u4-nominative,obj-u4-book
steps=u4-st-orient

### STEP `u4-st-orient`

- Objectives: obj-u4-pronouns, obj-u4-present, obj-u4-stress, obj-u4-nominative, obj-u4-book
- Sessions: u4-s-orient
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 9ce240991bde8e6afa019a847d9a96cf380f7ca94a42322c2ae69b3c5658fc8a

kind=ORIENT
title=Person words, present forms, a written mark, and dictionary-form examples
body=Proposed Unit 4 uses frozen rows for the person words, the present forms, the written mark, and the dictionary-form examples. The written mark is not a listening score. The owned book remains separate work. Qualified review is pending.
dominantAction=Continue
references=ref-u35-attribution
items=
examples=
unavailableReason=
bookDirection=Open Lesson 4 in the copy you own. The app records do not replace that lesson.
companionDirection=
remediationStepId=

### SESSION `u4-s-pronouns`

- Objectives: obj-u4-pronouns
- Sessions: u4-s-pronouns
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 8470de375e84ccb4b4e81fd3571ed03062f1116ea2bfb6cc28fdaaa819b6d4d0

title=Recognize admitted personal pronouns
estimatedMinutes=12
objectives=obj-u4-pronouns
steps=u4-st-teach-pronouns-1,u4-st-guided-pronouns-1,u4-st-teach-pronouns-2,u4-st-teach-pronouns-3,u4-st-rem-pronouns,u4-st-check-pronouns

### STEP `u4-st-teach-pronouns-1`

- Objectives: obj-u4-pronouns
- Sessions: u4-s-pronouns
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: f46344dbb1e218ef0be4238cd3c3c45c513700eac450ddfba641f305fcec5922

kind=TEACH
title=Two speaker rows
body=The admitted dictionary rows supply this pair. я — I. мы — we. 
dominantAction=Continue
references=ref-u35-attribution
items=
examples=ex-u4-я,ex-u4-мы
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u4-st-guided-pronouns-1`

- Objectives: obj-u4-pronouns
- Sessions: u4-s-pronouns
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 85189cf46db94acadae1652c595043be8913e765238eb5dcec94e1a9ca35b090

kind=GUIDED
title=Guided practice
body=Choose the admitted record. The feedback states the frozen field.
dominantAction=Continue
references=
items=u4-i-pronoun-я,u4-i-pronoun-мы
examples=
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=u4-st-rem-pronouns

### STEP `u4-st-teach-pronouns-2`

- Objectives: obj-u4-pronouns
- Sessions: u4-s-pronouns
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: c678b4bb8b410353028f2faf881933af61f5a51d5659ea0645a4751a96677b78

kind=TEACH
title=Two addressee rows
body=The admitted dictionary rows supply this pair. ты — you. вы — you (plural). 
dominantAction=Continue
references=ref-u35-attribution
items=
examples=ex-u4-ты,ex-u4-вы
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u4-st-teach-pronouns-3`

- Objectives: obj-u4-pronouns
- Sessions: u4-s-pronouns
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 1d51acaa9bbb33a8838172df387284ca99160e4b323ab425945ee16ca0ee19c0

kind=TEACH
title=Four other-person rows
body=The admitted dictionary rows supply this group. он — he. она́ — she. оно́ — it. они́ — they. 
dominantAction=Continue
references=ref-u35-attribution
items=
examples=ex-u4-он,ex-u4-она,ex-u4-оно,ex-u4-они
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u4-st-rem-pronouns`

- Objectives: obj-u4-pronouns
- Sessions: u4-s-pronouns
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 8bccc4c5f13bf04344b7430387dd4511ced23ac5a27e8eb36b9638d7fc66c7b5

kind=REMEDIATE
title=Remediation
body=Return to the explanation. Compare the shown form with the admitted record. Do not invent a form that is absent from the record.
dominantAction=Continue
references=
items=
examples=ex-u4-я,ex-u4-мы
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u4-st-check-pronouns`

- Objectives: obj-u4-pronouns
- Sessions: u4-s-pronouns
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: c9b52821c2b515a2888457a5177dad45ea4a85440514d101c7b143614636864f

kind=CHECK
title=Independent checks
body=These checks use only admitted records. A miss returns to the explanation.
dominantAction=Continue
references=
items=u4-i-pronoun-ты,u4-i-pronoun-вы,u4-i-pronoun-он,u4-i-pronoun-она,u4-i-pronoun-оно,u4-i-pronoun-они
examples=
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=u4-st-rem-pronouns

### SESSION `u4-s-present`

- Objectives: obj-u4-present
- Sessions: u4-s-present
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: e74668a5006c12f8d308fe4f5d243a467c7bcfe8679a5995ca347614f44befc8

title=Recognize admitted present-tense forms
estimatedMinutes=12
objectives=obj-u4-present
steps=u4-st-teach-present-1,u4-st-teach-present-2,u4-st-guided-present-2,u4-st-teach-present-3,u4-st-teach-present-4,u4-st-guided-present-4,u4-st-teach-present-5,u4-st-teach-present-6,u4-st-teach-present-7,u4-st-rem-present,u4-st-check-present

### STEP `u4-st-teach-present-1`

- Objectives: obj-u4-present
- Sessions: u4-s-present
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 7b6559de5c8db08427b72d131b43b7a33a58455b4c1e9b6dd3c21c00fe5b21f9

kind=TEACH
title=How the frozen page forms the present
body=The frozen page says: So, to form the present tense, you simply take the ''imperfective aspect'' of the verb and add the appropriate endings.
dominantAction=Continue
references=ref-u35-attribution
items=
examples=
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u4-st-teach-present-2`

- Objectives: obj-u4-present
- Sessions: u4-s-present
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 2dc0530594d14ab32ef16d301ddf798603611ba97ff490b9b96390e7c5688141

kind=TEACH
title=Admitted fields from one row
body=These fields are copied from the admitted чита́ть row. Row glossed I: чита́ю. Row glossed you: чита́ешь. Row glossed he: чита́ет. 
dominantAction=Continue
references=ref-u35-attribution
items=
examples=ex-u4-verb-читать
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u4-st-guided-present-2`

- Objectives: obj-u4-present
- Sessions: u4-s-present
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: d90cd90d22d655f5229b638917b5eb307176541d7de36a0406dd6fd8d42d60ad

kind=GUIDED
title=Guided practice
body=Choose the admitted record. The feedback states the frozen field.
dominantAction=Continue
references=
items=u4-i-verb-читать-ты
examples=
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=u4-st-rem-present

### STEP `u4-st-teach-present-3`

- Objectives: obj-u4-present
- Sessions: u4-s-present
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: a5342dc10a8fd2f0ebd8f932fc68b92458103b3957e4dce6be67aef33df9ec4c

kind=TEACH
title=Admitted fields from one row
body=These fields are copied from the admitted чита́ть row. Row glossed we: чита́ем. Row glossed you (plural): чита́ете. Row glossed they: чита́ют. 
dominantAction=Continue
references=ref-u35-attribution
items=
examples=
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u4-st-teach-present-4`

- Objectives: obj-u4-present
- Sessions: u4-s-present
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 6524bc72dcaa856976caaed9ae085f7a9a10b8622ec073d443a6e421194e69ae

kind=TEACH
title=One admitted row for a second infinitive
body=де́лать — make. The admitted row records де́лаю for the row glossed I.
dominantAction=Continue
references=ref-u35-attribution
items=
examples=ex-u4-verb-делать
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u4-st-guided-present-4`

- Objectives: obj-u4-present
- Sessions: u4-s-present
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 3553b5147ca69e4711bead22c320b476b387f6287b5c1350324c8ac19b7a1c0f

kind=GUIDED
title=Guided practice
body=Choose the admitted record. The feedback states the frozen field.
dominantAction=Continue
references=
items=u4-i-verb-делать-я
examples=
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=u4-st-rem-present

### STEP `u4-st-teach-present-5`

- Objectives: obj-u4-present
- Sessions: u4-s-present
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 18b03dfa511e3be47c8e6d267647e3ceb8fb5abc6f63d9c2bc7afd7778c23362

kind=TEACH
title=Four more admitted infinitives
body=These infinitives are copied from admitted rows. знать — know. рабо́тать — work. понима́ть — understand. слу́шать — listen. 
dominantAction=Continue
references=ref-u35-attribution
items=
examples=
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u4-st-teach-present-6`

- Objectives: obj-u4-present
- Sessions: u4-s-present
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: a6f6f765bda2525d560027924c487991c3ecba826d211f339442be3ab21676e6

kind=TEACH
title=Four further admitted infinitives
body=These infinitives are copied from admitted rows. жить — live. говори́ть — to speak. ви́деть — see. смотре́ть — look. 
dominantAction=Continue
references=ref-u35-attribution
items=
examples=
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u4-st-teach-present-7`

- Objectives: obj-u4-present
- Sessions: u4-s-present
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: f57b89ff92556c22a606bab3e8287af12673305092e3359726e95f19ec6e90a8

kind=TEACH
title=Two further admitted infinitives
body=These infinitives are copied from admitted rows. писа́ть — write. идти́ — go. 
dominantAction=Continue
references=ref-u35-attribution
items=
examples=
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u4-st-rem-present`

- Objectives: obj-u4-present
- Sessions: u4-s-present
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: bf53fd0beac00659da47faffd058bf0b9e9de45288b63c088cdabe7e7904341b

kind=REMEDIATE
title=Remediation
body=Return to the explanation. Compare the shown form with the admitted record. Do not invent a form that is absent from the record.
dominantAction=Continue
references=
items=
examples=ex-u4-verb-читать,ex-u4-verb-делать
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u4-st-check-present`

- Objectives: obj-u4-present
- Sessions: u4-s-present
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 285673b44156fa743a5e51f6a70927e98526a5e490a2c60ae2196f0fc2920435

kind=CHECK
title=Independent checks
body=These checks use only admitted records. A miss returns to the explanation.
dominantAction=Continue
references=
items=u4-i-verb-говорить-мы,u4-i-verb-видеть-он,u4-i-verb-писать-вы,u4-i-verb-идти-они
examples=
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=u4-st-rem-present

### SESSION `u4-s-stress`

- Objectives: obj-u4-stress
- Sessions: u4-s-stress
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 1e01cd4de0e2394258570f295814a325d204f56dce8880259a4f04ad4a51a36a

title=Notice the admitted written stress mark
estimatedMinutes=12
objectives=obj-u4-stress
steps=u4-st-teach-stress-1,u4-st-guided-stress-1,u4-st-rem-stress,u4-st-check-stress

### STEP `u4-st-teach-stress-1`

- Objectives: obj-u4-stress
- Sessions: u4-s-stress
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 3cd22a6b0070ed8ac51885bd8477e460d70dfe298a6154be91ad4e5127026bb5

kind=TEACH
title=The admitted written mark
body=The mark in the admitted form is written. Listening and speaking stay with the book. No audio is mapped to these forms. чита́ть. говори́ть. писа́ть.
dominantAction=Continue
references=ref-u35-attribution
items=
examples=ex-u4-stress-6f15e9f1,ex-u4-stress-1f37b6b2,ex-u4-stress-2114b12f
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u4-st-guided-stress-1`

- Objectives: obj-u4-stress
- Sessions: u4-s-stress
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 16c36c768b1999c9bb97a19b454f6b64264a44dc3a6643c580a4aa42738586b8

kind=GUIDED
title=Guided practice
body=Choose the admitted record. The feedback states the frozen field.
dominantAction=Continue
references=
items=u4-i-stress-6f15e9f1
examples=
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=u4-st-rem-stress

### STEP `u4-st-rem-stress`

- Objectives: obj-u4-stress
- Sessions: u4-s-stress
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 38de427d58a002d13d6717c3b1a9db34e27e958d75cb50451eaf3c5c70b95283

kind=REMEDIATE
title=Remediation
body=Return to the explanation. Compare the shown form with the admitted record. Do not invent a form that is absent from the record.
dominantAction=Continue
references=
items=
examples=ex-u4-stress-6f15e9f1,ex-u4-stress-1f37b6b2
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u4-st-check-stress`

- Objectives: obj-u4-stress
- Sessions: u4-s-stress
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 9f1a1cd3e7cda540c859c29ef62b8062dfc97bfcfa6462fc06bdcaaf72a401f5

kind=CHECK
title=Independent checks
body=These checks use only admitted records. A miss returns to the explanation.
dominantAction=Continue
references=
items=u4-i-stress-1f37b6b2,u4-i-stress-2114b12f
examples=
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=u4-st-rem-stress

### SESSION `u4-s-nominative`

- Objectives: obj-u4-nominative
- Sessions: u4-s-nominative
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 8e8a3c8e79d2510edea61680999fa52ed4b098757bb9f56ff1b1c87d6f38b8a2

title=Recognize the admitted nominative role
estimatedMinutes=12
objectives=obj-u4-nominative
steps=u4-st-teach-nominative-1,u4-st-teach-nominative-2,u4-st-teach-nominative-3,u4-st-rem-nominative,u4-st-check-nominative

### STEP `u4-st-teach-nominative-1`

- Objectives: obj-u4-nominative
- Sessions: u4-s-nominative
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 3494469bb822491bf998aa11d420528398525c38da6243223a45bf76b7ffcf2b

kind=TEACH
title=The count named on the frozen page
body=The frozen page says: Russian has six cases.
dominantAction=Continue
references=ref-u35-attribution
items=
examples=
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u4-st-teach-nominative-2`

- Objectives: obj-u4-nominative
- Sessions: u4-s-nominative
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 90755d3588407ca828e6d195456a0b0dd5f97cc3374157c6b7f97cb088bb35ca

kind=TEACH
title=The default form on the frozen page
body=The frozen page says: The nominative case is the default case of a word, as found in dictionaries and when no other case is specifically used.
dominantAction=Continue
references=ref-u35-attribution
items=
examples=
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u4-st-teach-nominative-3`

- Objectives: obj-u4-nominative
- Sessions: u4-s-nominative
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: b8b274ecce985afcbdacf60956d6067c070a6703d7a8a562dcc069c134b58706

kind=TEACH
title=Two admitted examples
body=These examples are exact frozen excerpts. Муж говорит — The husband is talking. Поезд старый — The train is old. 
dominantAction=Continue
references=ref-u35-attribution
items=
examples=ex-u4-nom-muzh,ex-u4-nom-poezd
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u4-st-rem-nominative`

- Objectives: obj-u4-nominative
- Sessions: u4-s-nominative
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 3e1dea1e6004548eed82a9663939f1c82e92599174ce7188bd29676bd12ef067

kind=REMEDIATE
title=Remediation
body=Return to the explanation. Compare the shown form with the admitted record. Do not invent a form that is absent from the record.
dominantAction=Continue
references=
items=
examples=ex-u4-nom-muzh,ex-u4-nom-poezd
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u4-st-check-nominative`

- Objectives: obj-u4-nominative
- Sessions: u4-s-nominative
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 2ac8b94c051a80cc3894632ec3a398ed9feef3a2100a18dda45ea603e508a9bd

kind=CHECK
title=Independent checks
body=These checks use only admitted records. A miss returns to the explanation.
dominantAction=Continue
references=
items=u4-i-nom-muzh,u4-i-nom-poezd
examples=
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=u4-st-rem-nominative

### SESSION `u4-s-book`

- Objectives: obj-u4-book
- Sessions: u4-s-book
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: bdb54047a22b73cd98e8942b8c71c64be6deeeb6a46bd29dcfb43cb26b422eb0

title=Self-attest Lesson 4
estimatedMinutes=10
objectives=obj-u4-book
steps=u4-st-book-work,u4-st-book

### STEP `u4-st-book-work`

- Objectives: obj-u4-book
- Sessions: u4-s-book
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: cca59187e7b4689cd66b6855dae3d8bc3f3185cfd99347de5fe700ff8d9d2d96

kind=BOOK
title=Work Lesson 4 in your copy
body=Do Lesson 4 in the copy you own. Use that book's explanations and practice. The proposed records in this unit are a separate licensed set and do not copy the book.
dominantAction=Continue
references=ref-book-topics,ref-u35-attribution
items=
examples=
unavailableReason=
bookDirection=Open Lesson 4 in your copy and complete its lesson work.
companionDirection=
remediationStepId=

### STEP `u4-st-book`

- Objectives: obj-u4-book
- Sessions: u4-s-book
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: b00c1e321c4a91179f7f9c8e9bba1a2a4ac67e869c7fcbdc0be04e1e1a5049b9

kind=BOOK
title=Record Lesson 4 book work
body=When you have done Lesson 4 in your copy, self-attest here. Attestation records your report. It does not grade the lesson, approve a mapping, or approve the proposed records.
dominantAction=Continue
references=
items=
examples=
unavailableReason=
bookDirection=Self-attest only after the Lesson 4 work in your copy.
companionDirection=
remediationStepId=

### EXAMPLE `ex-u4-я`

- Objectives: obj-u4-pronouns
- Sessions: u4-s-pronouns
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: f316fcc0c1d7bb0412d4441a75474d4b34c5982fc7365ba7b872fbaa9372e0f9

kind=LEXICAL
letterId=
display=я
note=I
objectiveId=obj-u4-pronouns
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-u4-мы`

- Objectives: obj-u4-pronouns
- Sessions: u4-s-pronouns
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: b458ec5e2cd0d2d834cc6753282b910b51ce657ae523699c6a2555b4ee388fe0

kind=LEXICAL
letterId=
display=мы
note=we
objectiveId=obj-u4-pronouns
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-u4-ты`

- Objectives: obj-u4-pronouns
- Sessions: u4-s-pronouns
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 97861bda0e8c3f47172551c425c2d55249ae68dfdee0f6cac5aa7b1f434314b5

kind=LEXICAL
letterId=
display=ты
note=you
objectiveId=obj-u4-pronouns
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-u4-вы`

- Objectives: obj-u4-pronouns
- Sessions: u4-s-pronouns
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 540ed5ccd950da18bcd7263e75c539d4371b437d4a6a4c4f3dcccff5ac6e6006

kind=LEXICAL
letterId=
display=вы
note=you (plural)
objectiveId=obj-u4-pronouns
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-u4-он`

- Objectives: obj-u4-pronouns
- Sessions: u4-s-pronouns
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 16c3053f0b6ad53bfd5350ab5b3da1fb03cc30cfa06543e8283b45dda66a8ab7

kind=LEXICAL
letterId=
display=он
note=he
objectiveId=obj-u4-pronouns
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-u4-она`

- Objectives: obj-u4-pronouns
- Sessions: u4-s-pronouns
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 3ec96b0968818022e282631c12410a146c7e396c0a752657511d9f101074e9ee

kind=LEXICAL
letterId=
display=она́
note=she
objectiveId=obj-u4-pronouns
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-u4-оно`

- Objectives: obj-u4-pronouns
- Sessions: u4-s-pronouns
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 1037c5faffb77640a41076aede8e3a4923d654f64ede629a0fa6e798e6fbfe2c

kind=LEXICAL
letterId=
display=оно́
note=it
objectiveId=obj-u4-pronouns
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-u4-они`

- Objectives: obj-u4-pronouns
- Sessions: u4-s-pronouns
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 2b91f64245c4ff4446aa44074dc46fd32706782adb5dda9db4c2965a1bbc0863

kind=LEXICAL
letterId=
display=они́
note=they
objectiveId=obj-u4-pronouns
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-u4-verb-читать`

- Objectives: obj-u4-present
- Sessions: u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 1218cb5b83b8e5d4f278ce66ad95bbb0020793fec57bc1656d95e75ead86a74c

kind=LEXICAL
letterId=
display=чита́ть
note=read
objectiveId=obj-u4-present
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-u4-verb-делать`

- Objectives: obj-u4-present
- Sessions: u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: f61f42c1d9979f0abfb1fe967157b2dcd9e822f00524094df090bbf2533b6fe1

kind=LEXICAL
letterId=
display=де́лать
note=make
objectiveId=obj-u4-present
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-u4-stress-6f15e9f1`

- Objectives: obj-u4-stress
- Sessions: u4-s-stress
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: b6e00d182ba73e5b739e8c0b55924de4ff15269bee3f8bc11a02a3985d9d28a9

kind=LEXICAL
letterId=
display=чита́ть
note=Written mark from the admitted row
objectiveId=obj-u4-stress
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-u4-stress-1f37b6b2`

- Objectives: obj-u4-stress
- Sessions: u4-s-stress
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: af1d346c0f50d246cad0955ad061f3a89041a0a06a0fe858b14b8d110b8a3a30

kind=LEXICAL
letterId=
display=говори́ть
note=Written mark from the admitted row
objectiveId=obj-u4-stress
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-u4-stress-2114b12f`

- Objectives: obj-u4-stress
- Sessions: u4-s-stress
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 16222676112cb8ef2754d61b61bd563b7158f74c88ce5cd6a43ec64a918e74e8

kind=LEXICAL
letterId=
display=писа́ть
note=Written mark from the admitted row
objectiveId=obj-u4-stress
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-u4-nom-muzh`

- Objectives: obj-u4-nominative
- Sessions: u4-s-nominative
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 389267af89f37131cc6801f80c285668281a19a3d52cd44d877cbf21306b607c

kind=LEXICAL
letterId=
display=Муж говорит
note=The husband is talking
objectiveId=obj-u4-nominative
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-u4-nom-poezd`

- Objectives: obj-u4-nominative
- Sessions: u4-s-nominative
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 9f1b2d39498603cfcdb001c59f630f2cf6ac2855137da2435f4f0ed875910b80

kind=LEXICAL
letterId=
display=Поезд старый
note=The train is old
objectiveId=obj-u4-nominative
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### GUIDED_PRACTICE `u4-i-pronoun-я`

- Objectives: obj-u4-pronouns
- Sessions: u4-s-pronouns
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 0658c05f21da92d654ccb508fc9fa6330735d8523b29f3f331c77ab7befa775a

kind=SOURCE_MEANING
language=en
prompt=Which admitted gloss is recorded for the shown personal word?
promptLetter=я
options=I | we | you | you (plural) | he | she | it | they
answer=I
feedback=The frozen dictionary row records this gloss: I.
objectiveId=obj-u4-pronouns
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### GUIDED_PRACTICE `u4-i-pronoun-мы`

- Objectives: obj-u4-pronouns
- Sessions: u4-s-pronouns
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 9abbec6aa56650d18e891c20b824107597f68a72fb89ed4460114d059e8d5ec7

kind=SOURCE_MEANING
language=en
prompt=Which admitted gloss is recorded for the shown personal word?
promptLetter=мы
options=I | we | you | you (plural) | he | she | it | they
answer=we
feedback=The frozen dictionary row records this gloss: we.
objectiveId=obj-u4-pronouns
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### GUIDED_PRACTICE `u4-i-verb-читать-ты`

- Objectives: obj-u4-present
- Sessions: u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 87c0d5ebc00b5da14c356a107fdc545891bee875fd1531f94f7fb7b72de9045f

kind=SOURCE_FORM
language=en
prompt=Which admitted present form is recorded for the row glossed you?
promptLetter=чита́ть
options=чита́ешь | чита́ю | чита́ет | чита́ем
answer=чита́ешь
feedback=The frozen row records чита́ешь for the row glossed you.
objectiveId=obj-u4-present
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### GUIDED_PRACTICE `u4-i-verb-делать-я`

- Objectives: obj-u4-present
- Sessions: u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 788f570711bc31bf9acdcc18f51571956ea05b36eae5cd142ba1662e73909180

kind=SOURCE_FORM
language=en
prompt=Which admitted present form is recorded for the row glossed I?
promptLetter=де́лать
options=де́лаю | де́лаешь | де́лает | де́лаем
answer=де́лаю
feedback=The frozen row records де́лаю for the row glossed I.
objectiveId=obj-u4-present
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### GUIDED_PRACTICE `u4-i-stress-6f15e9f1`

- Objectives: obj-u4-stress
- Sessions: u4-s-stress
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 9d5e32b716596ca5ad689f007929303ebc7a9a04cba183efd0f93eb6ba74c7e2

kind=SOURCE_STRESS
language=en
prompt=Which vowel carries the admitted written stress mark? Count vowels from the left.
promptLetter=чита́ть
options=1 | 2 | 3 | 4
answer=2
feedback=The frozen accented field places the written mark on vowel 2.
objectiveId=obj-u4-stress
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### INDEPENDENT_CHECK `u4-i-pronoun-ты`

- Objectives: obj-u4-pronouns
- Sessions: u4-s-pronouns
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 756160d155acf36cc4cc5e9bc382ef4dd89d5184beab6a3e8bd5c66e80483375

kind=SOURCE_MEANING
language=en
prompt=Which admitted gloss is recorded for the shown personal word?
promptLetter=ты
options=I | we | you | you (plural) | he | she | it | they
answer=you
feedback=The frozen dictionary row records this gloss: you.
objectiveId=obj-u4-pronouns
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### INDEPENDENT_CHECK `u4-i-pronoun-вы`

- Objectives: obj-u4-pronouns
- Sessions: u4-s-pronouns
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 0423071813e0d82b3cfa5ae53c74acebf8ab463160b9695c2be6056eeca47c8d

kind=SOURCE_MEANING
language=en
prompt=Which admitted gloss is recorded for the shown personal word?
promptLetter=вы
options=I | we | you | you (plural) | he | she | it | they
answer=you (plural)
feedback=The frozen dictionary row records this gloss: you (plural).
objectiveId=obj-u4-pronouns
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### INDEPENDENT_CHECK `u4-i-pronoun-он`

- Objectives: obj-u4-pronouns
- Sessions: u4-s-pronouns
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 6176088bed3cbb33a46bfa0a33bfd8e3da08a82c85c9c538b391462ef44efb31

kind=SOURCE_MEANING
language=en
prompt=Which admitted gloss is recorded for the shown personal word?
promptLetter=он
options=I | we | you | you (plural) | he | she | it | they
answer=he
feedback=The frozen dictionary row records this gloss: he.
objectiveId=obj-u4-pronouns
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### INDEPENDENT_CHECK `u4-i-pronoun-она`

- Objectives: obj-u4-pronouns
- Sessions: u4-s-pronouns
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 4b04ee6949740328baf4eaa5da8ccd2c7818c387288d48eb781d3b07cdeeeb9c

kind=SOURCE_MEANING
language=en
prompt=Which admitted gloss is recorded for the shown personal word?
promptLetter=она́
options=I | we | you | you (plural) | he | she | it | they
answer=she
feedback=The frozen dictionary row records this gloss: she.
objectiveId=obj-u4-pronouns
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### INDEPENDENT_CHECK `u4-i-pronoun-оно`

- Objectives: obj-u4-pronouns
- Sessions: u4-s-pronouns
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 79b1d5d60e694b1a6e10950d6f7ff4091de91c7f760dacd222b488da8471b364

kind=SOURCE_MEANING
language=en
prompt=Which admitted gloss is recorded for the shown personal word?
promptLetter=оно́
options=I | we | you | you (plural) | he | she | it | they
answer=it
feedback=The frozen dictionary row records this gloss: it.
objectiveId=obj-u4-pronouns
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### INDEPENDENT_CHECK `u4-i-pronoun-они`

- Objectives: obj-u4-pronouns
- Sessions: u4-s-pronouns
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 39a0f3d52b024deb3018272676286299a6017edc3a63d9832c7ddc59f9f9316c

kind=SOURCE_MEANING
language=en
prompt=Which admitted gloss is recorded for the shown personal word?
promptLetter=они́
options=I | we | you | you (plural) | he | she | it | they
answer=they
feedback=The frozen dictionary row records this gloss: they.
objectiveId=obj-u4-pronouns
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### INDEPENDENT_CHECK `u4-i-verb-говорить-мы`

- Objectives: obj-u4-present
- Sessions: u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 16b05b24b04552b01920bb28d1fe630a992b7bcbd5062f160451e1c68f90dab5

kind=SOURCE_FORM
language=en
prompt=Which admitted present form is recorded for the row glossed we?
promptLetter=говори́ть
options=говори́м | говорю́ | говори́шь | говори́т
answer=говори́м
feedback=The frozen row records говори́м for the row glossed we.
objectiveId=obj-u4-present
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### INDEPENDENT_CHECK `u4-i-verb-видеть-он`

- Objectives: obj-u4-present
- Sessions: u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 282f6bb644af8400e2708e769add05e4c37a764a5704fed7a9a27b7cea416fde

kind=SOURCE_FORM
language=en
prompt=Which admitted present form is recorded for the row glossed he?
promptLetter=ви́деть
options=ви́дит | ви́жу | ви́дишь | ви́дим
answer=ви́дит
feedback=The frozen row records ви́дит for the row glossed he.
objectiveId=obj-u4-present
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### INDEPENDENT_CHECK `u4-i-verb-писать-вы`

- Objectives: obj-u4-present
- Sessions: u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 05386928c80abeb6971fb9ef1524df0aec7bb3c8e004784c6b12a396875b5902

kind=SOURCE_FORM
language=en
prompt=Which admitted present form is recorded for the row glossed you (plural)?
promptLetter=писа́ть
options=пи́шете | пишу́ | пи́шешь | пи́шет
answer=пи́шете
feedback=The frozen row records пи́шете for the row glossed you (plural).
objectiveId=obj-u4-present
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### INDEPENDENT_CHECK `u4-i-verb-идти-они`

- Objectives: obj-u4-present
- Sessions: u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 071bf39b42b1aed9c412a07affd70f1a2d55868c9c86a52ac763746a0ffadd94

kind=SOURCE_FORM
language=en
prompt=Which admitted present form is recorded for the row glossed they?
promptLetter=идти́
options=иду́т | иду́ | идёшь | идёт
answer=иду́т
feedback=The frozen row records иду́т for the row glossed they.
objectiveId=obj-u4-present
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### INDEPENDENT_CHECK `u4-i-stress-1f37b6b2`

- Objectives: obj-u4-stress
- Sessions: u4-s-stress
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 8e53b6b4d978bd93fa776ba3961c41ae831404fe8f8b3852638fbcf73f9177e4

kind=SOURCE_STRESS
language=en
prompt=Which vowel carries the admitted written stress mark? Count vowels from the left.
promptLetter=говори́ть
options=1 | 2 | 3 | 4
answer=3
feedback=The frozen accented field places the written mark on vowel 3.
objectiveId=obj-u4-stress
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### INDEPENDENT_CHECK `u4-i-stress-2114b12f`

- Objectives: obj-u4-stress
- Sessions: u4-s-stress
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: aba2635799b7ec4bb8f8cf7004f2adae26fe9074c7a8437dadbd4b4014112c45

kind=SOURCE_STRESS
language=en
prompt=Which vowel carries the admitted written stress mark? Count vowels from the left.
promptLetter=писа́ть
options=1 | 2 | 3 | 4
answer=2
feedback=The frozen accented field places the written mark on vowel 2.
objectiveId=obj-u4-stress
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### INDEPENDENT_CHECK `u4-i-nom-muzh`

- Objectives: obj-u4-nominative
- Sessions: u4-s-nominative
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: d994e57efbe67381f51dcbbcf215fc9fd812720da609ee8f9ec057fca09a1edd

kind=SOURCE_CASE
language=en
prompt=Which admitted example is the shown sentence?
promptLetter=Муж говорит
options=The husband is talking | The train is old
answer=The husband is talking
feedback=The frozen page records this example as: The husband is talking.
objectiveId=obj-u4-nominative
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### INDEPENDENT_CHECK `u4-i-nom-poezd`

- Objectives: obj-u4-nominative
- Sessions: u4-s-nominative
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 055d0bdb5d8a843aa7554d3c63c8e4bfb6dc9e91702aca36920f91d25891fd78

kind=SOURCE_CASE
language=en
prompt=Which admitted example is the shown sentence?
promptLetter=Поезд старый
options=The husband is talking | The train is old
answer=The train is old
feedback=The frozen page records this example as: The train is old.
objectiveId=obj-u4-nominative
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### REFERENCE `ref-u35-attribution`

- Objectives: none
- Sessions: u4-s-orient, u4-s-pronouns, u4-s-present, u4-s-stress, u4-s-nominative, u4-s-book
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: LICENSE_OR_ATTRIBUTION
- Current review status: NOT_REQUIRED
- Content hash: cccdcbee8041484eef0712c9b7b6cf9e4d9660f95e00fcbbdfba0e7cef98afb9

title=Units 3–5 source notice
summary=Proposed Units 3–5 adapt CC BY-SA 4.0 dictionary rows and Wikibooks grammar excerpts.
detail=OpenRussian dictionary data is frozen at commit 50e210c4803237779cb562bc1abcea529066031c under CC BY-SA 4.0. Wikibooks Russian grammar excerpts are frozen by oldid under the CC BY-SA 4.0 route. The adapted selection and arrangement are share-alike. Application code, user data, and user recordings are outside that statement. No audio is included in this proposed curriculum. Review is pending. The packaged CC BY-SA 4.0 legal code is cc-by-sa-4.0.txt.
classification=LICENSE_OR_ATTRIBUTION
introducedInUnit=3
searchTerms=units 3-5 source notice
reviewStatus=NOT_REQUIRED

### REFERENCE `ref-book-topics`

- Objectives: none
- Sessions: u4-s-book
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: OPERATIONAL_GUIDANCE
- Current review status: NOT_REQUIRED
- Content hash: 73201da8816156c60a714eebfe89e4098bd2332d8daa2920a127ec14ab85d1bd

title=Where lesson bodies are packaged
summary=Some units contain proposed lesson bodies that still require qualified review.
detail=A unit's Course status shows whether its lesson body is prepared. Units marked Not prepared or Not yet installed do not contain a usable lesson body. Open the matching lesson in the copy you own. This reference does not approve the packaged records.
classification=OPERATIONAL_GUIDANCE
introducedInUnit=3
searchTerms=where lesson bodies are packaged
reviewStatus=NOT_REQUIRED

### PROVENANCE `prov-u4-cases-default`

- Objectives: obj-u4-nominative
- Sessions: u4-s-orient, u4-s-nominative
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: f186e7a1028f3a5f0d1df43ca5567d0d916180f436ff619e452ae4f02df11b0d

recordType=explanation
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Cases?oldid=4195114
sourceRevision=4195114
sourceRecordId=cases:The nominative case is the default case of a word, as found in dictionaries and 
originalSourceText=The nominative case is the default case of a word, as found in dictionaries and when no other case is specifically used.
shippedText=The nominative case is the default case of a word, as found in dictionaries and when no other case is specifically used.
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=ff4b3a7886932f557c2d9829deffd6b14cf549d2c01997e66b23bb4fe9e746f3
contentHash=ff4b3a7886932f557c2d9829deffd6b14cf549d2c01997e66b23bb4fe9e746f3
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-cases-six`

- Objectives: obj-u4-nominative
- Sessions: u4-s-orient, u4-s-nominative
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 91f3738ab12c0b92dd75bd903aad37233d2a747ef4f6ee899166d2bfada7661f

recordType=explanation
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Cases?oldid=4195114
sourceRevision=4195114
sourceRecordId=cases:Russian has six cases
originalSourceText=Russian has six cases
shippedText=Russian has six cases
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=f256c705f2146460c6dadfcce77a5ae6c2bd069d8ac0205b4240940a1c7035e5
contentHash=f256c705f2146460c6dadfcce77a5ae6c2bd069d8ac0205b4240940a1c7035e5
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-form-видеть-вы`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 258d89beb06d4c6ae6ab63c264f85753e65515df894032270013fdbebcb23512

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:ви'деть#presfut_pl2
originalSourceText=ви'дите
shippedText=ви́дите
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=7e2c665655727b68bbf4286cb6dc43e0a9732aa65ce38c14a9f0dc5b1e3e6d1b
contentHash=b4d1d7ecd083777ce779789235231e5f6fde642ac4a124ca6c9e5a14668198be
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-form-видеть-мы`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 64e9de3e29cb05116c11949d4b744ae704166c84ed1677223e25d71ec74e2d34

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:ви'деть#presfut_pl1
originalSourceText=ви'дим
shippedText=ви́дим
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=dda0a5380dc0d288e14789a22cd3672b03c7a28c991bf5a7a559693733bb8ed1
contentHash=559b0d3a5c2a757a4c63d016da21ad85f64490a09ce35995d216617a76f6def4
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-form-видеть-он`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 6658c37cfd9d6d0a6f100cbefcd6709c205583e7e05286e39fc19577cfb3c06f

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:ви'деть#presfut_sg3
originalSourceText=ви'дит
shippedText=ви́дит
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=9441e603a5a5651a71c6148a14cfeff1fbacf8a235965397c913441ae71e76c5
contentHash=d18914f234e60a197a98233d3b0def070efcb2c535d7dc66ad9d9a311f89dffb
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-form-видеть-они`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 99a3c2505a0fce48fd6610a1c20475fdf8c34012851122b6755f47a3dd348147

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:ви'деть#presfut_pl3
originalSourceText=ви'дят
shippedText=ви́дят
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=0a5caa5755d2193aadba749b487c40f22586cac44b32050bbf4b46524e0b799b
contentHash=23be4d0414d3afa4dc18c6625d9c7374a261c5377dc28cb578abf9d1188ebe2d
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-form-видеть-ты`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: ac7d5b14c1a5ad960a6027a48cd31e1c45d6556ea43b071e31385db8cae7fcd6

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:ви'деть#presfut_sg2
originalSourceText=ви'дишь
shippedText=ви́дишь
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=a815dfdefadc318f40afd52c9bd17cc70d0a234a5555eb1efb50d2ca3b3de47e
contentHash=1656522610eae444ba6eb8e5c9c0633b0a9d6daec71f34be60ef5d6d7151f26f
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-form-видеть-я`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 8fc4db744666e540a7ed1a068b3a19443c3013d373beb8ede1dbf5d170cb943c

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:ви'деть#presfut_sg1
originalSourceText=ви'жу
shippedText=ви́жу
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=abd32306943d580dd05ff4ac898538368021d4084b306056c7c93590c61d1515
contentHash=d02a8cde4582dd5b878dfdd8a4d109af46176d6cf7a036d5d9f2bf391ba7ab69
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-form-говорить-вы`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 6fdceb4965f71fb4d7922903c704e2d60ce36f720c98a45f031e7bf56cc42f79

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:говори'ть#presfut_pl2
originalSourceText=говори'те
shippedText=говори́те
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=5bed49bd4150e3ae698322405ff483ebcd889c9a9d9aadc75e275cabde787d3e
contentHash=b4956508f9a6e1bdc1fe23b390a78a00f9b0d40c2cee34f0d8bd71c3d44ae00a
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-form-говорить-мы`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: d392ec8b32cb7721600556771f5c6662eefb3eb3a4da1214797bdffe03114fdc

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:говори'ть#presfut_pl1
originalSourceText=говори'м
shippedText=говори́м
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=9d86c1d7760f25e3920767184ea293b8e545fbf2a74dc65c24f065b4adba7388
contentHash=11370e24767aee380cb0b30f35e4b95892a588c0f4eaef5344840885e6a6f85e
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-form-говорить-он`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 2cfd6cd9f0ea20023225b4a51947e71c852d1136f8bf52bf77ee51279a061baf

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:говори'ть#presfut_sg3
originalSourceText=говори'т
shippedText=говори́т
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=75d5f1d67237f08acddeae3f59f21778062d788e4999fd5e4bd3bbdd6b5f7933
contentHash=356a21b059770a5d703e05f5f1d84ba31cb88cc06d9e42fe08810069f929e8d0
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-form-говорить-они`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: e384f7bc51e8d8f79e775c0d4058c42f852d849b91c960ae0ba2f17f466850c3

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:говори'ть#presfut_pl3
originalSourceText=говоря'т
shippedText=говоря́т
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=9cd2e8f93947ab3e3ade76c42ef8cb8a53820be874d01889816ed2d23121ebad
contentHash=4071d71e1330e70d6d2954c74b240c557d7f452cec656b10924a6bea441fae26
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-form-говорить-ты`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 4c0a6dd1d6f191cedb9494d42e70fee46bf0b345af1b7dd059c044250270fec6

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:говори'ть#presfut_sg2
originalSourceText=говори'шь
shippedText=говори́шь
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=832a04a15a8259a60a4e2fea8835cbdbd4e74d1aa2a01ca6bcc813993b44b8e2
contentHash=984e6be71cb45ae712a93c9efc97b30d8d735bfe24f1891b1b10ef428ddb08d9
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-form-говорить-я`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 559cff8db6b3249f9fbd674441c0cd17568df873f0eaad44eb0b7b54ec58f922

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:говори'ть#presfut_sg1
originalSourceText=говорю'
shippedText=говорю́
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=dd944a96c8ad71935280d4e47eae05ef61149c595d5c9ad389eed1a47b6510b9
contentHash=1da7e603d5de973ed4a9f180bda8699dc65f8d64745ae3ee388777db96750878
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-form-делать-вы`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 057555b954c9bfa38da59ec35311af407e6605d89ec09aad181481335bcf466e

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:де'лать#presfut_pl2
originalSourceText=де'лаете
shippedText=де́лаете
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=d81920caca50a2102a37fa6e06eb39ea73928a5c1e16091b8c5b20fb928f4ac6
contentHash=552a329c6c839cc118fb2fdc91c972363de95838704e09f495a2a9f6f9d3a86f
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-form-делать-мы`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 6ef85a62c8334d7c3c673a35fac08fbd607336d04e22d00d2eb15f6f4b3a2856

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:де'лать#presfut_pl1
originalSourceText=де'лаем
shippedText=де́лаем
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=ce4017a443cbdf6b798debcd7e2aea12710aff22cb84e320b39690b77104b178
contentHash=7af5df24c45d21bc06a4412edab7a2e929cb679429f3821ca34733044dc41bdc
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-form-делать-он`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 79d448e978d22748bc991ba2824f61c25d4966987ad8ac39d5806969b7b8f813

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:де'лать#presfut_sg3
originalSourceText=де'лает
shippedText=де́лает
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=e7b64152136eead9a8ee67e35b57b04786565e497286b096b28cb56e54253187
contentHash=f280526c3102a21b152c23ac4f32a1cb0c8c2a9278727b1b69fea07cd5d1f564
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-form-делать-они`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 6eb7275a77733ba65533ff72d732c95156d5befed4987c81ccf6796d52fb592f

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:де'лать#presfut_pl3
originalSourceText=де'лают
shippedText=де́лают
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=2611ed2bd9ca3500a906e03ac2e4ad9e13fe736c180b8701763c8eb8766c9859
contentHash=71cc6fb2596b2e7d62df1c635db503b439485e811d4d2a294a0f9560703f123e
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-form-делать-ты`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 23f2a19f8edaef3879bb5a425554f756fb80b2e68ae5c422d01db4be64fd5a05

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:де'лать#presfut_sg2
originalSourceText=де'лаешь
shippedText=де́лаешь
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=1df34b27577ff11a1249d4cf36924093d30b5ee1a9b42bdb93268e4cf35a05af
contentHash=935a85def3e1206d04ce16bb547f7ca761034f61a0baf04905590bc84eabc812
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-form-делать-я`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 907539b8f31dc03b643f5928886ef7c8d330253839bb58951b6440f22aa2750a

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:де'лать#presfut_sg1
originalSourceText=де'лаю
shippedText=де́лаю
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=cf62d471aee7e1fb38315573fd2dc265cc874489cc378532a5daeaade96931f6
contentHash=18bceec7e60459c7dc2a0b631a8d0888ac01da2400b8a7d6cfa3e29ea695fdb7
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-form-жить-вы`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: d10f9c0df046f99af715ff8e85e453ab65debcc0035e7041bc3b3361c5c768c2

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:жить#presfut_pl2
originalSourceText=живёте
shippedText=живёте
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=a496e5474aa71589e1eee0a16b45c3f605c61f7cccef4028ab061ab6c3834e56
contentHash=a496e5474aa71589e1eee0a16b45c3f605c61f7cccef4028ab061ab6c3834e56
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-form-жить-мы`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 61e2cb8149fc911f47d4b225e1b41006087b99879e51aeafb0938a37d6fc5a82

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:жить#presfut_pl1
originalSourceText=живём
shippedText=живём
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=4c295951a8509ff952a877cb0f8a5c581c34dbe5702c82bc3ad8533e44a59622
contentHash=4c295951a8509ff952a877cb0f8a5c581c34dbe5702c82bc3ad8533e44a59622
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-form-жить-он`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: ac2c9d4d0878dec7790130ecbccf8099c720aaabf363b92447010c99cd422711

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:жить#presfut_sg3
originalSourceText=живёт
shippedText=живёт
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=d5bdda40a8d3f014b31b33962eb5428ea2c075cb8118233cdd3eb62e975a53ba
contentHash=d5bdda40a8d3f014b31b33962eb5428ea2c075cb8118233cdd3eb62e975a53ba
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-form-жить-они`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 00a6480934057ea1296be40abb41f7028a27ce25df213396de34218b9f71fc7c

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:жить#presfut_pl3
originalSourceText=живу'т
shippedText=живу́т
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=9966d10819fd2d442e5fb14a4edea383ac9052e7ffc8bbd243e0d8a97059a2e8
contentHash=15a71cc29934a50baa949ececdfc1d7d0ea856b7b21eb9841ad43b128a531a98
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-form-жить-ты`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 55c98555af52fd8ed06b0078cfe7a43d765cb63f9e52be764ab31afa8a8af096

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:жить#presfut_sg2
originalSourceText=живёшь
shippedText=живёшь
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=e3d17376ab0a3694513351b4fb1fc42fd6f0ad0e376d9fcd0d0b9bdcb66b8150
contentHash=e3d17376ab0a3694513351b4fb1fc42fd6f0ad0e376d9fcd0d0b9bdcb66b8150
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-form-жить-я`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 4f35559f331c93521951375d5c41a68f43243564108f14bdc228e91eaaad1537

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:жить#presfut_sg1
originalSourceText=живу'
shippedText=живу́
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=73114ecf20ef059d7a10ab7d436253b0b46dfddf5cb10bdd4ae413f21c2c732b
contentHash=dbb3eaf7105bd6a3e35e5a6aa127cfa71999dcf227cced866dd66a7829fb9533
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-form-знать-вы`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 2359854dde1063dcbfcdad4c7943a3acf55d457e5a5156fbbedd68a791a6c6ed

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:знать#presfut_pl2
originalSourceText=зна'ете
shippedText=зна́ете
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=94aa8faf415a93b6a373e12377a5e98c22bfbeef399f91ec26e04d824a9e4b47
contentHash=70fbb37f459796466e90f5cd63f4111c6b0ae759ae79cb8f92774b4968c4adca
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-form-знать-мы`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 82e898895cc591fee6e36a03e42453626da5280385776c45227319e3a635c68b

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:знать#presfut_pl1
originalSourceText=зна'ем
shippedText=зна́ем
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=dfb24c699d03b0a5b01d32a06e20b15e43153ae973868a7479fe7442da7bd2d9
contentHash=16afd6ba923747b15124d1bd9f78c86dbc33b06330ad373c92774df6d8972994
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-form-знать-он`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: e708ef1ac56f21376385b04e987ab8478bfe49ad9f8141f234265782bdc9b293

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:знать#presfut_sg3
originalSourceText=зна'ет
shippedText=зна́ет
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=4a59db901dee82fca9f989c5ee69f9d1df81db33614413a64523753b77604513
contentHash=abf468b4ff2b6fd62b3cae6b5583667a3552a3c56a6eece557e6cae9e32183a6
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-form-знать-они`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 1ec7a7608e23715d5eabe202add2a02e84f484528929ebb28228065acd5f0b12

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:знать#presfut_pl3
originalSourceText=зна'ют
shippedText=зна́ют
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=03e9c32bf7317e1b3d6a826bf4ceeb42704c543dc8a1eef6cdf0ee781658ff67
contentHash=71599e11ae7b796d6eda1c822c2995406c39f57b229015f16cd5d8e95caddab5
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-form-знать-ты`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: aa56dd45d2c076ac1dead21fc8f8b651a1105642a02b18692b69a7f4e80ea05a

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:знать#presfut_sg2
originalSourceText=зна'ешь
shippedText=зна́ешь
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=17537478d17cd22a55854dd47ea5ad1b296bce78a7059e3520135048ae76053c
contentHash=b8d136bb05afbbbea938df4decbc3bacfa50b5a99d0725ae77c70e51eef29575
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-form-знать-я`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 74c1f4a8e02c55dd8c9a669d6ee9a4eb3f9670d20c07c071f2b0e8c7eebc6c39

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:знать#presfut_sg1
originalSourceText=зна'ю
shippedText=зна́ю
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=bf4c52315705340aa2a146d2b659256dd192742ebb89524460b2a72550ac37e7
contentHash=b3b92a68657bec8a0c32db1993e4f2df382d3b10d9ab78696fbe2ef5a1e0f5af
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-form-идти-вы`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 17d2e767bd4ceb4826cb685e640c1b69368a63ceb566d0863ae4493cb3cdbaef

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:идти'#presfut_pl2
originalSourceText=идёте
shippedText=идёте
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=de9f50e5500ae54b1c92a9e1db45be6c76d73c32851c5c390d720e7c88ef8961
contentHash=de9f50e5500ae54b1c92a9e1db45be6c76d73c32851c5c390d720e7c88ef8961
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-form-идти-мы`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 52e3d6535415e5e3e99dd9fb0158c54741816195ff48d9b55c45a8ac83c1f28b

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:идти'#presfut_pl1
originalSourceText=идём
shippedText=идём
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=bc0dabd76d6ce601134ce1598c6aab471f0a9cbb55b59e2a56264dfedc35482e
contentHash=bc0dabd76d6ce601134ce1598c6aab471f0a9cbb55b59e2a56264dfedc35482e
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-form-идти-он`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 2c94bb2f0e11e5b17e179f7833fafde1cb519970429ba4263979387194fa87e6

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:идти'#presfut_sg3
originalSourceText=идёт
shippedText=идёт
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=5d2f73185e86270d20e42ceb26554dcaf27d5d98717c0a091f3f4fc4d101acdf
contentHash=5d2f73185e86270d20e42ceb26554dcaf27d5d98717c0a091f3f4fc4d101acdf
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-form-идти-они`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 225df2cb6150353718654d964be8ff30cebc1f825f2fe3ec9696c5bf096b02e9

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:идти'#presfut_pl3
originalSourceText=иду'т
shippedText=иду́т
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=170e62ca1d3dd6009e5c0946473a256c484f0abb029b14b3d453b66113b6e727
contentHash=1911e2009b9caff38bc2462938ec607bf35525788ad165faccf679b0b1cc2519
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-form-идти-ты`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 12d466260a196fe66137405f7287b38190914b80bf971a25204a933fce3c2bea

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:идти'#presfut_sg2
originalSourceText=идёшь
shippedText=идёшь
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=499b8d35f58fdd4d4c71b1fcac4eda4351c76bdbe5a2895fc54c8b2cf269caea
contentHash=499b8d35f58fdd4d4c71b1fcac4eda4351c76bdbe5a2895fc54c8b2cf269caea
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-form-идти-я`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: f1197c131a2a135c3e52a1387317f1c51918942135f9d1992a73ec9562b59915

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:идти'#presfut_sg1
originalSourceText=иду'
shippedText=иду́
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=52af9160576d0af91e1888d137e85552f8054877a34c2212c4ce0f47d8421780
contentHash=4bbfcda75ab0b3cf9cb9a3d44ce842ad0c302998b7cdbe4aa36fe5843c8b38ab
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-form-писать-вы`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: e0367df041cc599a848f47e750b911738768844a445c7b0539a987790a224532

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:писа'ть#presfut_pl2
originalSourceText=пи'шете
shippedText=пи́шете
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=9c11b0993b610a7e7c1a237f5cc2146afad31d0e02e13b7e3d8692756b3251b5
contentHash=5782e957c01e72b4940723c26fdee4cb569de72c8c5a11575e27af18cdab9bec
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-form-писать-мы`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 2506303a5dc18b7d21b038209f9db05f43d55f93b9ba4ad0f3c3fa12bff82da9

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:писа'ть#presfut_pl1
originalSourceText=пи'шем
shippedText=пи́шем
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=b24d7e2bd1e0bc273238d75f5d67c4a4d922afc3b859012f4857508f8f12f039
contentHash=babab5ffaf28ade57b117f9b430a41e4a271bef3c7d4c4917793933ac8c66a85
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-form-писать-он`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 89dbd44960b94bc120dc9a08278a62fff3b67b50b962a5b806bca2718bbaac77

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:писа'ть#presfut_sg3
originalSourceText=пи'шет
shippedText=пи́шет
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=8f21dd3a4418d26ebd3038bee1ecf8a00342cd475915fe328b23238de7240c7c
contentHash=0c56d2aa06b012c6802f7c86396c467627c98f892050948e0d77d133cfe901fd
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-form-писать-они`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 1cd303e8e41d105a22b28542bc69a3ae3d9ae86dd7d8d249591b2dbcca35a396

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:писа'ть#presfut_pl3
originalSourceText=пи'шут
shippedText=пи́шут
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=3876364405488a8758844f8575f02bde96bc035c57f21a6dc23d5b159c4c99ec
contentHash=6cc0e45f0f391c3252d6e96f4f848b9395415909455a69af6f337ec405cf0ab7
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-form-писать-ты`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 2bd4c424246c63c2ce42e39ea96026a943fe97a33a4f3d68417c6d01259646d4

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:писа'ть#presfut_sg2
originalSourceText=пи'шешь
shippedText=пи́шешь
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=eb73414370cf8fb9b35d90510653687a86cc98d7e8d89a8fc411b256b9f4565d
contentHash=1fe95363de9a5aa41a20496401738790afecb4a2e161ec35d33b4bb25f91dfad
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-form-писать-я`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 532b8c949d1b10a1c0efc7479a6c7c468baadaca626c9db2dba4b1dd5fa50375

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:писа'ть#presfut_sg1
originalSourceText=пишу'
shippedText=пишу́
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=25835aefadf667c3ba3fc4aa5b939aeb58e95df90cc55f80041fbff3bcac7c1d
contentHash=44da78bfeeb8fab7e8cc9ea8506fc555b157ac0c531581c558ab4dd2eb394ca4
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-form-понимать-вы`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 378023d91fc1712cbb1792f2f5c1c91436a9ed5da438aafcfb51822e3a439a91

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:понима'ть#presfut_pl2
originalSourceText=понима'ете
shippedText=понима́ете
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=27c89dbcff31c146d2a9be9d990a7d6b4e2eca69440616ad00b1eaf5bd34f4d6
contentHash=e2b9ab87081e9b670c69c7d386be70056a533e494c01d23cbf104ae210936150
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-form-понимать-мы`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 8f1fdadb9a9ad81f5b66c26e2f4926f23618d328398942226f3ecc887c439b40

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:понима'ть#presfut_pl1
originalSourceText=понима'ем
shippedText=понима́ем
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=cf64742afeabc1f4dc014c8d7303dbde50982a58ad881d1cb255c5d95249a58b
contentHash=3428899f32b45c13dd8bd91261502d624f49c538a448b4e7b043e88b80165e39
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-form-понимать-он`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 4a0d8632179a163d8635c57523e2749b4d3f896bf710e33426935d2b75cb6ab1

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:понима'ть#presfut_sg3
originalSourceText=понима'ет
shippedText=понима́ет
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=fc65ea5b70ea44c2af5c202b0a17c5123a357e4d42891b64c9ba9d8b2f1c125f
contentHash=3309efff4f647fee9a9c36aff5bbbae8425aaa2957c18ee5a9c0d48fbbc9ea34
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-form-понимать-они`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 25b0e4574bd960096a16b546c793ed5aa90959f0678e961cc16da2770a003b6f

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:понима'ть#presfut_pl3
originalSourceText=понима'ют
shippedText=понима́ют
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=cf0464b1d0143d0dca4d7522f11b35d2791286592fc23ad3b2561e65b318e9eb
contentHash=ac8e5c4756d6e5bfec93d243f5f83727cd74c82a9ce1494a6be139cd222d2929
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-form-понимать-ты`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: e52c574e6ee89a25075c4caaab7868ac0b57956b278286394aa6d96558cf22a3

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:понима'ть#presfut_sg2
originalSourceText=понима'ешь
shippedText=понима́ешь
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=45a4525031101071cdfcc4933833c1d1c5019d811b9c703dc4f35f5afefb2d32
contentHash=ca6e2572e2bf70eb5d470e0346034c64576e783850395709852de277ecb7aadc
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-form-понимать-я`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: e2116efb780b571472c656fea697224bde294f0786a270d3f62782074d5463ba

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:понима'ть#presfut_sg1
originalSourceText=понима'ю
shippedText=понима́ю
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=5e0c7332e0bdafc9b71e29b3f10896e1c4c23059f3030080ce1e1f48e731ec24
contentHash=fdaa188bfd35b19b365da2805c9e1dfc1b845b4c896544e8e14cdd7d5704c3fc
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-form-работать-вы`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 6e170db268c7bb28edb37e9b98f98ff369f2e3e15cc475dd1394ad9bdad3d07e

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:рабо'тать#presfut_pl2
originalSourceText=рабо'таете
shippedText=рабо́таете
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=ee970d09aeeb43cf163599a7b169b77f7dccaa1746fb527f729f802e2d22e02a
contentHash=ba3b1ae8a6c2caf7b3ebbf2094fe6ac339e1467616c28e11f886cc535d962a98
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-form-работать-мы`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 4902fe4c94cd432f1516a2a489f3ff07254985288e7f4d35dc631acce05be26b

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:рабо'тать#presfut_pl1
originalSourceText=рабо'таем
shippedText=рабо́таем
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=d7bf77f42868599624db0fba20b25c15e9949e98ed2bd6679e0c8905a77f0514
contentHash=a18bec1616f5d3a38453f94d671758423704227ff55db0446ae3f1dbded05595
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-form-работать-он`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 19b77c3175745f2c64bb98542b0c365e8cfd0413ab6fc2716e5a226b2271c45d

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:рабо'тать#presfut_sg3
originalSourceText=рабо'тает
shippedText=рабо́тает
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=dee069ebb428bf6f8f505fbb44c2c533b4ece426c27dbed8f865c6293986ffdc
contentHash=13fe081c9da8798225928fd7a6d31c6aa1d96e98211b72b274c53ab0e5de444a
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-form-работать-они`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: f754fba283ef61c7b172a47c357176aada4c514d949d3466fb8b3ef3a62c058c

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:рабо'тать#presfut_pl3
originalSourceText=рабо'тают
shippedText=рабо́тают
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=9e2039e5bfb7b247b0626e18137185032977daaee6c7bc7b4e13e7a90adfd4ac
contentHash=35b55489a19ea83f87b66993009bb95319e57ab1f7e6ae781f45b76648bfebb3
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-form-работать-ты`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: d5a23ef761565115a9955c6a6c4fdd62a4eb7dea224090862441defe4a8e4bf6

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:рабо'тать#presfut_sg2
originalSourceText=рабо'таешь
shippedText=рабо́таешь
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=2f13bca9985caab37bc20fce3424de81d4499c32245957933aae00e89334eba9
contentHash=9d4f6c9d334fb9911b633d416348cbd88e26e4294c663ecc51476823ae474a6a
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-form-работать-я`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 419757364a1be75e8e57c81baee52f4ee9fd5cbfc21faf4ac3b193f9d77476c1

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:рабо'тать#presfut_sg1
originalSourceText=рабо'таю
shippedText=рабо́таю
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=3b677cd0086eb21e4bf119109fa5aa0fe87a2e8e4c097cea1a9db2c5e4b9aaf6
contentHash=975aac6e17c57029ffe03d22f075745717662c6faaa050cf06db90bbd5eca734
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-form-слушать-вы`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 41d963695a53373173c836250213d0d3939256d6bac29835407e0a17c2c395d3

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:слу'шать#presfut_pl2
originalSourceText=слу'шаете
shippedText=слу́шаете
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=8afc2f0bd6be466aa1a79bc99fd4d351ff24ee1af6e238c37791f51697f94bdc
contentHash=ef1a0395d97695b3e154c810977d4fde67d160ab883a3086ae4c6c0153d17320
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-form-слушать-мы`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 2ea04805771cc25601e7cb19acca8cf76952d4017fb3c4b528d874078698f11c

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:слу'шать#presfut_pl1
originalSourceText=слу'шаем
shippedText=слу́шаем
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=4c11942729c896690fae29151a78aac9a819ca744668b7ce21838254c35794a5
contentHash=d22a485b50c3812832f117e3ead34348cf7ad735dd7d06892e4bd77cdddbacc1
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-form-слушать-он`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 253c03c60c286edfd237858c7931aed42961327a76cb45b1debe48e37e540af0

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:слу'шать#presfut_sg3
originalSourceText=слу'шает
shippedText=слу́шает
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=99e07b14af81a58d8b2ee1fbc711906c0785684ba2f10e8e75d4869a3605f217
contentHash=3131161b935ae42abb7e38ed118db538ab39bdc47339054c9f210cf495b46e18
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-form-слушать-они`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 8faf9146a6116f15b2eb7b3dd4513a0c4f61218c3ac82671571eca018d4b4927

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:слу'шать#presfut_pl3
originalSourceText=слу'шают
shippedText=слу́шают
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=0ec4a4c7c7b84ed46375dd739f6d7443b21050b6d01711938750fc5092f0aa3b
contentHash=33079c97bef5bcb1a8ecabdbab3de21dfd0128cb610d1f1ae56625f50e8b706e
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-form-слушать-ты`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 5ec9fc4e8264945a09e61e940539f2fed8fbe99fa094e6500100f2f910bb0d01

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:слу'шать#presfut_sg2
originalSourceText=слу'шаешь
shippedText=слу́шаешь
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=6a261ddbdfa4cd0717346199b954eb5e6a8d6625d7edf8ac74aabee2d647fe9d
contentHash=6a4afdcd2cb2fc6eca92bcc248fcfd69aca289d70fac9185aca92a6b60853403
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-form-слушать-я`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: e2b3635d8b39bbe2eb9c809247829f5ef177dcc2caad7bbdeccfa4630d3cdb0a

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:слу'шать#presfut_sg1
originalSourceText=слу'шаю
shippedText=слу́шаю
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=6a22091fcf64284b1a0c7d6c25ffb07827b90501abd3ddc81ffa414dcf7fc803
contentHash=4cf45715711fb505a0332283715eba09211dafd5b73f53ffc51f1f29543be1d6
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-form-смотреть-вы`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 276ccdb64a85f573f06377bdc3ab351ef980f2ea3f462e1de3204d4147f829b8

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:смотре'ть#presfut_pl2
originalSourceText=смо'трите
shippedText=смо́трите
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=acacd8fababcbb06e7dd0ed833e9f4ef23525ccd6d5d355ba3c6904debe0754a
contentHash=76b442e95eaafe1574ddfac1507ffad25c3a4020fbce78de8abec6812fceb6b3
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-form-смотреть-мы`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: e86857a991ff3cdb6d680a582e8c1e60e6716a9cc0cf47d2b2fda5484e78a01c

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:смотре'ть#presfut_pl1
originalSourceText=смо'трим
shippedText=смо́трим
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=2c143f3b67e93f4562e4cd38cdc5f006569e9610f4b02b80d4c53288e86fcd70
contentHash=832a35e64aefe09b50c32cd3fad21b4b517064d2d6076f71d459abf0dad784d1
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-form-смотреть-он`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 0c276e2620c83791e538ccad52879750f34ecdebbdf9a82a31c8eadaef0b4bb0

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:смотре'ть#presfut_sg3
originalSourceText=смо'трит
shippedText=смо́трит
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=bf94b6ffce000938952e3475186a965097fee7b6ef1e56d6be9020d554ae8c57
contentHash=87e610d2bc1e1f4975f150aebcc5ce3269265c49af10de36e421b16662f86971
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-form-смотреть-они`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 8ec2ad0ffeef85652904d7770e315ce8858f9d040240ebb7f4cecd0342f87c94

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:смотре'ть#presfut_pl3
originalSourceText=смо'трят
shippedText=смо́трят
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=c4ccb65cb9549e3bab6aee59388f58e84ade05da4d5c2892e1c1889b1f06aa89
contentHash=7997d19f5e02f28a4c01bde09293335d42d7f8697b3deafb06654d9b0bcdf8bc
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-form-смотреть-ты`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: c818aa130051736247ac5cbc3506721e8cd899a873933b6a3415f87922a6fae8

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:смотре'ть#presfut_sg2
originalSourceText=смо'тришь
shippedText=смо́тришь
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=e05ff3273bba3961f9ef7c158875f267ab84794d40d61460128579362d3755ad
contentHash=82e9cb649151752a6272b98c03a1aefb8a50f31ab584c7bebbf0c6fa4e377ee0
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-form-смотреть-я`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 20f354c257eef9a8427c20e483f11e74a80e21156d73bf77cc89c3ba4194ace2

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:смотре'ть#presfut_sg1
originalSourceText=смотрю'
shippedText=смотрю́
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=32a77ce0f7e457c66e9e4b5b4fbb169822d45bee5cd3fcde748271401e69f219
contentHash=15f7c8f68bbe060284602c20163d12af1d52005ac0809a3cbdbed706b7e624e9
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-form-читать-вы`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: ec99ffef19d06bc28372a205fe68a0dc86d11467f4c5ae36a2342cdafe7136db

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:чита'ть#presfut_pl2
originalSourceText=чита'ете
shippedText=чита́ете
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=152ff6f0123c90e8cb0ee6d5f00334b474c79a6e49ec2349c7e818a6c5f29c6d
contentHash=93d9e20adaff7098843032423c4e2c0f3c69cc1e513d22fdda02eb8ee24bc68a
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-form-читать-мы`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 9bc3a98c6f3be2c513034d58b6a1c9269773624d15eaf787a40cc36ec11d330b

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:чита'ть#presfut_pl1
originalSourceText=чита'ем
shippedText=чита́ем
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=e7d774e651e130d0a70f349f2ad35cce949143dfc14a8182e21824d2abc2700c
contentHash=0e3973cf21e3ee0f9dd1356c17985344b886e5323bd0d3ed07f72b5eca8a5a76
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-form-читать-он`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 6d9f7e1692136a0cc19fb75cbe64cb700fcce43b5b36b00334e59c343cd51e8f

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:чита'ть#presfut_sg3
originalSourceText=чита'ет
shippedText=чита́ет
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=77316964189d0e9e716b1bb6b8bf85596aa0d4e09f87b500948ed4583e7c96b3
contentHash=7c3a2c4a1ba027d657fb500f80158bd9265c7c0da4a8d7fa26a891edb52c6788
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-form-читать-они`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: f85968be55fb0834a367446fbf35c624db105e8e77c1e0094045937d9565b245

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:чита'ть#presfut_pl3
originalSourceText=чита'ют
shippedText=чита́ют
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=481dcb1f8a62c817a876a8be77847abd65d2c8b821e9240c02f89021677c6733
contentHash=03c55fb62ffb36cd544fff551fb9b4d92732368641eac9e3b326a95db3ca363a
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-form-читать-ты`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: bbf9fe9f74111504d98c289b180b40e128534612e3d2f19e6d6d3a795f678465

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:чита'ть#presfut_sg2
originalSourceText=чита'ешь
shippedText=чита́ешь
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=ed02240dc2babe99b750d88b0ad987043cc76f39ecfa5e0f327b7517de71c1f2
contentHash=4493355ae95e9efcc58476c1241e3bbd55a29bfeaf740a358b56c06963ea5cef
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-form-читать-я`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: bb749e1b742f38d47d953930fd1f3e2bd3f1f4b3a14c5f8e00119786c2f608ee

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:чита'ть#presfut_sg1
originalSourceText=чита'ю
shippedText=чита́ю
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=fe760576de9cc45e69048ffdcfb10e48d93f26181165b2a433963d1dacfdca7b
contentHash=4a25e88da78d7f5ed1e9c843abbc21206f9fb3fd8caceca62add2080bf382abd
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-govorit-gloss`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 1100801408bc8b13677321188c63496cf67cf8f971896c62d2b09c5274c8f278

recordType=reference
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Verbs?oldid=4233876
sourceRevision=4233876
sourceRecordId=verbs:to speak
originalSourceText=to speak
shippedText=to speak
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=4ca762c41972c065576195229be6704587e0aacec753eebbaa8e26b288a97741
contentHash=4ca762c41972c065576195229be6704587e0aacec753eebbaa8e26b288a97741
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-nom-muzh`

- Objectives: obj-u4-nominative
- Sessions: u4-s-orient, u4-s-nominative
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 5deb719d410e66d32bcb6d19d42735dcb34d2282979a570e8198c51c8f71224a

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Nominative?oldid=4213267
sourceRevision=4213267
sourceRecordId=nominative:The husband is talking - '''Муж говорит'''
originalSourceText=The husband is talking - '''Муж говорит'''
shippedText=Муж говорит
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=71cb141a4b029463c5cfc084975242e455c60c6d88eb1abc088ace292a3bb2bc
contentHash=ba3965803d5aecf88c4a13beb04c1183c54c54b3a1e7c3f61e5b6572f3b8e65c
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-nom-muzh-en`

- Objectives: obj-u4-nominative
- Sessions: u4-s-orient, u4-s-nominative
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 025ef4ff3c01adb0c0d0e319f9256ea1609a53b4babe2f21c626cd4489504d49

recordType=reference
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Nominative?oldid=4213267
sourceRevision=4213267
sourceRecordId=nominative:The husband is talking - '''Муж говорит'''
originalSourceText=The husband is talking - '''Муж говорит'''
shippedText=The husband is talking
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=71cb141a4b029463c5cfc084975242e455c60c6d88eb1abc088ace292a3bb2bc
contentHash=378f10254a3a0f2a45d014fbc9225ee82b277e3ac0a69f1bd0443c1ef6545635
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-nom-poezd`

- Objectives: obj-u4-nominative
- Sessions: u4-s-orient, u4-s-nominative
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 4f23ccb66adc315b3b448db9ea8ce633e69e818121d43dd085b45f0a20bcee6c

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Nominative?oldid=4213267
sourceRevision=4213267
sourceRecordId=nominative:The train is old - '''Поезд старый'''
originalSourceText=The train is old - '''Поезд старый'''
shippedText=Поезд старый
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=f6c5162db8d13ccc98e77278be5ad6465a037414879e0fdcdb3494be8b45bfe1
contentHash=593100e07231bc9baa2d67875b75a5ce1d54a6bc15287a3edcebcf8bd0350426
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-nom-poezd-en`

- Objectives: obj-u4-nominative
- Sessions: u4-s-orient, u4-s-nominative
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: d6655947c4603ff836eba18a31381e9aae5aee8ed6780198eab34a6c3017e8f2

recordType=reference
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Nominative?oldid=4213267
sourceRevision=4213267
sourceRecordId=nominative:The train is old - '''Поезд старый'''
originalSourceText=The train is old - '''Поезд старый'''
shippedText=The train is old
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=f6c5162db8d13ccc98e77278be5ad6465a037414879e0fdcdb3494be8b45bfe1
contentHash=920a6f8c4d84a3a9835b89834095398b4bd92336758f32836fc9273ce085b713
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-present-rule`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: c5334810c700c8cbb42d920e9aebd1bf267a29ff060d5faf015c8d05e429e908

recordType=explanation
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Verbs?oldid=4233876
sourceRevision=4233876
sourceRecordId=verbs:So, to form the present tense, you simply take the ''imperfective aspect'' of th
originalSourceText=So, to form the present tense, you simply take the ''imperfective aspect'' of the verb and add the appropriate endings.
shippedText=So, to form the present tense, you simply take the ''imperfective aspect'' of the verb and add the appropriate endings.
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=91a0ed4f87f46e4308662044a9ebe98ccc9ab17d53b0489ea2fb75e96ce2e109
contentHash=91a0ed4f87f46e4308662044a9ebe98ccc9ab17d53b0489ea2fb75e96ce2e109
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-pronoun-gloss-вы`

- Objectives: obj-u4-pronouns
- Sessions: u4-s-orient, u4-s-pronouns
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 4fe461229cf3ec92a05a5df8c525cbbc5d472dc1e3754f85b4e1078b1672ec61

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/others.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=others.csv:вы#translations_en
originalSourceText=you (plural); you (formal)
shippedText=you (plural)
transformation=sense selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=b5f36d3873df6e86c7114c974af687e60432bb2ac9fe7846807effa863eb911b
contentHash=8a58bb4e67309189fcaa744ec41cab8e1e84b6d23c9dce45aacc2566842384dd
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-pronoun-gloss-мы`

- Objectives: obj-u4-pronouns
- Sessions: u4-s-orient, u4-s-pronouns
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 28dcdb70cc1040bdc650aef854043a18f8e3908407223324d5f2eada141bf421

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/others.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=others.csv:мы#translations_en
originalSourceText=we
shippedText=we
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=dc7c811b9561739d9b75bb3e9e1715970a868834e62251b0b9ca02e74d0f42c9
contentHash=dc7c811b9561739d9b75bb3e9e1715970a868834e62251b0b9ca02e74d0f42c9
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-pronoun-gloss-он`

- Objectives: obj-u4-pronouns
- Sessions: u4-s-orient, u4-s-pronouns
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: c64f2c7083b6b4e651cdbfff7bd2046f8988d7289092a4987f5bcf9bc8e6b395

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/others.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=others.csv:он#translations_en
originalSourceText=he; it (masculine inanimate nouns)
shippedText=he
transformation=sense selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=4c54fa9cdb7bb07b2fffb7f61f966b6eb816cd06bf818087671e3ee5768f41a0
contentHash=372f7e2fd2d01ce2a1d71dc072acbba4c6fd25a1087cd7f153f4ec0ce37e1ede
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-pronoun-gloss-она`

- Objectives: obj-u4-pronouns
- Sessions: u4-s-orient, u4-s-pronouns
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: de6d5b729e7d08af918a0c01685270de9efb6adc436ca9b55e60ebd65242ac52

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/others.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=others.csv:она#translations_en
originalSourceText=she, it, he, she, it, he, she
shippedText=she
transformation=sense selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=7c11ba0aaa654efe14af96290307ff74af7bf30a44b9acdce9abd2858d2a8d19
contentHash=2cda85e504e155f30a40f1f1cc4e83f3f8ec178033f6b6170cc9381f5e4f9d3d
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-pronoun-gloss-они`

- Objectives: obj-u4-pronouns
- Sessions: u4-s-orient, u4-s-pronouns
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 22e014ed9f4cb631f86c297403676ab57df27ea86e263d1d9cf8393c02377e30

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/others.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=others.csv:они#translations_en
originalSourceText=they
shippedText=they
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=151e58a831306903b282edb477bb6c4b713887cdd2b0353c84aff6f43da8e95d
contentHash=151e58a831306903b282edb477bb6c4b713887cdd2b0353c84aff6f43da8e95d
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-pronoun-gloss-оно`

- Objectives: obj-u4-pronouns
- Sessions: u4-s-orient, u4-s-pronouns
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 8e1e7aed035066f2e92101d2eaf3767d1f24dfc737dc453dafec160527bdc5e3

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/others.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=others.csv:оно#translations_en
originalSourceText=it, he, she
shippedText=it
transformation=sense selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=f3ba9da34835aa121835df88786874738b04b4459bc16e338f31eae915abf4eb
contentHash=2ad8a7049d7c5511ac254f5f51fe70a046ebd884729056f0fe57f5160d467153
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-pronoun-gloss-ты`

- Objectives: obj-u4-pronouns
- Sessions: u4-s-orient, u4-s-pronouns
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 862d436d396a1eab57dea182cab077fcba42e7dbed1d4ae61ba6b082cc558ae6

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/others.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=others.csv:ты#translations_en
originalSourceText=you
shippedText=you
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=bb0347a468d97e98a9c00e37cebec1ab930f6f1221cae0f1fbb92b07e1900ba2
contentHash=bb0347a468d97e98a9c00e37cebec1ab930f6f1221cae0f1fbb92b07e1900ba2
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-pronoun-gloss-я`

- Objectives: obj-u4-pronouns
- Sessions: u4-s-orient, u4-s-pronouns
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: aac08f4873af1d50303825c6183b8ba7a83ed10eef7915e82b896a4a12786990

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/others.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=others.csv:я#translations_en
originalSourceText=I
shippedText=I
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=a83dd0ccbffe39d071cc317ddf6e97f5c6b1c87af91919271f9fa140b0508c6c
contentHash=a83dd0ccbffe39d071cc317ddf6e97f5c6b1c87af91919271f9fa140b0508c6c
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-pronoun-вы`

- Objectives: obj-u4-pronouns
- Sessions: u4-s-orient, u4-s-pronouns
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 76bedd787f340c228699548d7a13f4ab780441cc3ade7acf04f97dee67831c28

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/others.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=others.csv:вы#accented
originalSourceText=вы
shippedText=вы
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=36c93704471d664511dfff7d87fedf624f5706f63a285deaaccfe9c2b598c75d
contentHash=36c93704471d664511dfff7d87fedf624f5706f63a285deaaccfe9c2b598c75d
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-pronoun-мы`

- Objectives: obj-u4-pronouns
- Sessions: u4-s-orient, u4-s-pronouns
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: fa44fbadf93a5f7dd309620a8669add6257625c4858cb5a851e6cebb41d17ebe

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/others.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=others.csv:мы#accented
originalSourceText=мы
shippedText=мы
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=7a2d863933e15801d69e1120b4cb553b05321a8b451cd2c2cf0f531e82372d60
contentHash=7a2d863933e15801d69e1120b4cb553b05321a8b451cd2c2cf0f531e82372d60
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-pronoun-он`

- Objectives: obj-u4-pronouns
- Sessions: u4-s-orient, u4-s-pronouns
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: b5ff96dfeea401cdd5a4d22e57cfb0144e110c841bf7ad1024a56fa83a876710

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/others.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=others.csv:он#accented
originalSourceText=он
shippedText=он
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=484518364fdba90e881b95500cc11d4917f8aa783dbbd5dba49485833426d2db
contentHash=484518364fdba90e881b95500cc11d4917f8aa783dbbd5dba49485833426d2db
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-pronoun-она`

- Objectives: obj-u4-pronouns
- Sessions: u4-s-orient, u4-s-pronouns
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: ed635008dd2eee50a072e21095173c80e42ff2ac359563b88dd73ea8f742b77b

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/others.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=others.csv:она#accented
originalSourceText=она'
shippedText=она́
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=e5e045f636d23bfd64a7fe06bc4c50748ca2828f5e86b1fdf6287e7efe2d5b17
contentHash=a11adc54a57f13197a871282fe182f45211739d3f0d72591ca37d3ac4a09458a
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-pronoun-они`

- Objectives: obj-u4-pronouns
- Sessions: u4-s-orient, u4-s-pronouns
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 18f3bdeceb00070a6d7dc11ef57a21005b967e5e02079af046a7d35ccc71b333

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/others.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=others.csv:они#accented
originalSourceText=они'
shippedText=они́
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=dc654486031e4a1bc4cd1c72aa20508ac5a06a3075eb03b1f7e901db7ba65369
contentHash=6480c6bf58a236f3351b9d0357fdc158dd2e7d9b32d538bc17caf73d4ddd66cc
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-pronoun-оно`

- Objectives: obj-u4-pronouns
- Sessions: u4-s-orient, u4-s-pronouns
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: bf64aaf296cc673f9eda13ff9b036c3371d334f6ecb5ab332d9d673b3797d067

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/others.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=others.csv:оно#accented
originalSourceText=оно'
shippedText=оно́
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=f585fe128a05f55a6be3af773e0e606db90532a7e3f8cb3bd951bf258db3c4be
contentHash=59c4f7147db9e5ef4bf998bf4eb956e0694f5c49bf766399905ea6806b521bcf
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-pronoun-ты`

- Objectives: obj-u4-pronouns
- Sessions: u4-s-orient, u4-s-pronouns
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 4e081865c55c761c6e90f9ab707f9594b92690a99541a4ce05b4baca325f72ff

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/others.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=others.csv:ты#accented
originalSourceText=ты
shippedText=ты
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=bca5722b5743f0cfe3c49fec81bd38467b33c7bee166b35f16c073b75f6e3228
contentHash=bca5722b5743f0cfe3c49fec81bd38467b33c7bee166b35f16c073b75f6e3228
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-pronoun-я`

- Objectives: obj-u4-pronouns
- Sessions: u4-s-orient, u4-s-pronouns
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: e788028eb59ce718184be61122262502b4b7f062938308a22082e87048f6807a

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/others.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=others.csv:я#accented
originalSourceText=я
shippedText=я
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=9b21f5cf014c9efb35110dcccf3a74eab7fb903d1ca2f4054b3dad9ffabb7727
contentHash=9b21f5cf014c9efb35110dcccf3a74eab7fb903d1ca2f4054b3dad9ffabb7727
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-verb-gloss-видеть`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 76dab5e51bc7c5a438cd4d273222dc1af58c74a813def22fbe86d382a1b28732

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:ви'деть#translations_en
originalSourceText=see
shippedText=see
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=aa9e9b5c907d50feb410f2a84e81ab72c5ae6724d57ccc53650f9361d33dc734
contentHash=aa9e9b5c907d50feb410f2a84e81ab72c5ae6724d57ccc53650f9361d33dc734
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-verb-gloss-делать`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 255c333fe6183f30a67d99d166e071102c855aed2ac2916633183aa1c8783a01

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:де'лать#translations_en
originalSourceText=make, do
shippedText=make
transformation=sense selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=dffc18cec2273cbec32177da9ca70838604bcb1e2db289259436c71fc1b3e5c8
contentHash=d05aa2a15fb3c40efeeb03bec445393f00074484cf01c8fb2da90ca6695a5531
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-verb-gloss-жить`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: ca627387b28dc465021bc7a2b58833d0ee29f2524d044d3164d535b678562cbb

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:жить#translations_en
originalSourceText=live
shippedText=live
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=247610f4dedd4ab7247d07dbda19c81ca9817f85820742cad49d407ffae9e4ed
contentHash=247610f4dedd4ab7247d07dbda19c81ca9817f85820742cad49d407ffae9e4ed
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-verb-gloss-знать`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 5b380015e88d9b7a6e7957c314fe3be7c8637250fbd2c92555696e53eeb14b95

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:знать#translations_en
originalSourceText=know, be aware, be acquainted, have a knowledge, aristocracy, nobility, the elite, evidently, it seems
shippedText=know
transformation=sense selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=3308a37d82a0e5ded2ad8d755afccd607fc00855d0dca19f7ae84fa075f56f1d
contentHash=cd1bb3012bcfaa0c50e954d03634f33c883c2478584e96f03d0d4b984bc6eadc
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-verb-gloss-идти`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 9c9c0da200ca2f1c16cd618977de5dce9665fc9e395ac1a8af1dbeb1f09a2b81

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:идти'#translations_en
originalSourceText=go, walk
shippedText=go
transformation=sense selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=95fa41582448996e2f719e713253cc70b69081123e5015305584a1a3e85295e2
contentHash=4cd0e21a9a0795a14ec9aa5f0e7d1abff0492565770e43eafdf1e3e8afed1f33
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-verb-gloss-писать`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 5b404b795b45d0c7ccdf6acbf7d71c9f515dcf99d3a6d3a1bfa9bd3d2d55579e

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:писа'ть#translations_en
originalSourceText=write; paint
shippedText=write
transformation=sense selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=2f7f6021ba3c1bb97968d89d7feeff7b3a7b37265234d880d111b5176ad97ecc
contentHash=10fd874b68dad080ed706762c8e163dabb20514bddae38fb159c56f714a3b143
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-verb-gloss-понимать`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 590ed36af67344f67c08f84a146141cb653c060f639728d53933f731d985b447

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:понима'ть#translations_en
originalSourceText=understand, comprehend, realize
shippedText=understand
transformation=sense selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=23d2609c2c843ba13e6f9f1286e28747a99672c6a022710a24116e0adae168aa
contentHash=f9dc689e6d50ad6f35cc97059375b37b8149225f4fb7a192f4ea40051da5416a
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-verb-gloss-работать`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: ef13644127daf2820a1d5bbac8736d66803aea6547a93d041c77cc0993f0c77b

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:рабо'тать#translations_en
originalSourceText=work, operate
shippedText=work
transformation=sense selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=d5321d43294a88e5aa450b84a1db5acc0cfe137a1b295999453f8593d7bfedc6
contentHash=00e13ed7af55b27622f1d6eab5bec0147e68efe28dc2b12461117afa1a5ed40e
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-verb-gloss-слушать`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: a98b647d51a600fad539fb603860943f7d3fb418945666432cfe82d2b04d5874

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:слу'шать#translations_en
originalSourceText=listen, hear, attend, examine chest, auscultate, obey
shippedText=listen
transformation=sense selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=2f8cbd17b7f4f578ca776d8658dba156317539f17060df254c1cf60437e8acf3
contentHash=6f8860eb5103a16b917146f9cbf21aa00fe7d2291b1aad7f4fe83a6db71688cf
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-verb-gloss-смотреть`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 79cd8ece9311e546c5c761f6c6175a30370e2eaa003d274a2bcede7855c907f4

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:смотре'ть#translations_en
originalSourceText=look, watch
shippedText=look
transformation=sense selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=309365578387c75a1a03fd0dc1e82231cec7ba63f60c40ea39810d25d318817c
contentHash=3c01eba119e00d79c82b6f65d70bc5f1044d568618bf41377e6d1432023fc2b8
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-verb-gloss-читать`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: c0b3ce4a7f7356c668ff18135ef3639900fc7de2ef2035154277e4dfc534f2d6

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:чита'ть#translations_en
originalSourceText=read
shippedText=read
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=3316348dbadfb7b11c7c2ea235949419e23f9fa898ad2c198f999617912a9925
contentHash=3316348dbadfb7b11c7c2ea235949419e23f9fa898ad2c198f999617912a9925
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-verb-видеть`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 291b6a781a4a600c9d5ce99701b1681121d05346f26c0624364abd87f5ab1565

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:ви'деть#accented
originalSourceText=ви'деть
shippedText=ви́деть
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=42c858d61cdd773afec281e0cb1291c005c82a73725b01ad9e420edd0dae4574
contentHash=bcbe3e0f397fc49e400c94f585b9f95e3c47e773c0329a004400d93ad8025059
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-verb-говорить`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: c02dd47d847d3f3c90f3ffe4cae82f49db41fae3c0327d68ee95b34c1b3a1a52

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:говори'ть#accented
originalSourceText=говори'ть
shippedText=говори́ть
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=35fc8e0f9f22b3ca57edcbd3992313d1bc0730fca50983d58ece71e0f250f6df
contentHash=1f37b6b2cea84a10b3a4fb1a11f4d155460f649d86b38cd375ea5ee336fcd5e4
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-verb-делать`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 4fc103e33acf870cdd7bc9aee5cfbef76f9ee2151c47272e84a47d39a87ee083

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:де'лать#accented
originalSourceText=де'лать
shippedText=де́лать
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=0403a68d923f8ef72fdb5e73c46c4af2d2112066801bd0f15d966f4c41274f82
contentHash=c472ba4cd4b95d85e2b7686abe28d3860a88b3ecb9eb6de34881b9fd9e089bbb
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-verb-жить`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 479c448fbce64e43c25fbb0bb17a99ef7c1de733497edbb71ec562e9c4fe5922

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:жить#accented
originalSourceText=жить
shippedText=жить
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=e8d8e551e1839e0a6063ce36de4903a2d473fa8d1cb785abe98f92cf6e09e3f4
contentHash=e8d8e551e1839e0a6063ce36de4903a2d473fa8d1cb785abe98f92cf6e09e3f4
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-verb-знать`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 83600730b11d6bd5b25b64dc16ff4f37fa5b74cecc6702162a07224ce77a20f6

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:знать#accented
originalSourceText=знать
shippedText=знать
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=2cc7c3d5023b6573e89a60e287fc8e3b73f998e855e66a1cc8b4030521178e18
contentHash=2cc7c3d5023b6573e89a60e287fc8e3b73f998e855e66a1cc8b4030521178e18
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-verb-идти`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 0c7be6da45e42f0412087337b77b36a939b318a94411236640d3491bc7378dda

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:идти'#accented
originalSourceText=идти'
shippedText=идти́
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=c5d8303726eb927cda49a54f042f63072cf489802aa252ba6a3c82384733a36e
contentHash=c9c68dfd15528de3fc8172fd5caa15324e33df6163213914502b73fc7f070717
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-verb-писать`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 72931e6f872e613369fa5e715629e3a7430857c9d36f8e6957c3a7739329b7c9

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:писа'ть#accented
originalSourceText=писа'ть
shippedText=писа́ть
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=6395c19583956d997dbb67e4ca1ce5977579a293cf1998977acaca1ca3890c38
contentHash=2114b12fd8900149eb453750e32989ed91d53cfbc041c53c8bf14da84077ccfd
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-verb-понимать`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 8558870b6b3da34f50ec7a6c13df1ac6409f932e44580b9ade1a4ea0bad958ed

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:понима'ть#accented
originalSourceText=понима'ть
shippedText=понима́ть
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=2956ed7f73e3e6cf43a4d5db367903e4e9e71a6783dd601df402e9cc8b630524
contentHash=306c0252853ff1be7d2c2da209d167a5b84cf00678da908815d262ca16b4367f
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-verb-работать`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 8e986c06a5bde6cc07e86ad0fba0f94866ba202f5cd0408b3c18dd49dfa5a590

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:рабо'тать#accented
originalSourceText=рабо'тать
shippedText=рабо́тать
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=8d6cb9ab9b5e31dca6732c1e541e76c61fb6bdb188bfe418d7cf6b72200fc476
contentHash=40860d85321dde40d1e83443d4444368ea92b6214c908a30c606bc8f90271d0e
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-verb-слушать`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 9457317047b9050c468002924ab556df4914b2ac1ede271c87c294a957dbcd97

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:слу'шать#accented
originalSourceText=слу'шать
shippedText=слу́шать
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=0d1c856786e2ac38428dcdfeec9a7c2fd08868999f5896ac8b1bb4112d49cba2
contentHash=d65a92deb5eb064cff12b3a51812a52b2239200d96aa67d1ccd822753eab55a9
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-verb-смотреть`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 8f3b9250cd37562ae96030c5597f0e77813ac50d6bcc60af35419abc9dd6387d

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:смотре'ть#accented
originalSourceText=смотре'ть
shippedText=смотре́ть
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=3c0d9eaa3afe0a481ed0c6c0f264992cf04557043566910dfe0f3d0a70b586c9
contentHash=a3f7154682076db2ba6fec241d2d4a0984b79cbcb4dce34ee744c2905cb341e5
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u4-verb-читать`

- Objectives: obj-u4-present
- Sessions: u4-s-orient, u4-s-present
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: f7d15b6240aa55c45b15928bf210fcbea63d5d7f2c61cae9c25012f45a87a7e9

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:чита'ть#accented
originalSourceText=чита'ть
shippedText=чита́ть
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=ddfe5d2975ca337f1b5b58d9c15f3bf80aec57b8f39d578c6b4f5489dbf7208a
contentHash=6f15e9f11f640b8dd0390da79e6a2da50f2d5c953e5a5a67f0eee8dd8bbc6887
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### AUTHORED_ENGLISH `en-u4-043`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 3824acfceec4d0cd574855fd21e051f63a631fb9797ea1cbd9cb6a76a2fe05e3

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Which admitted gloss is recorded for the shown personal word?

### AUTHORED_ENGLISH `en-u4-044`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 39468dded83c8d3844e22ec86448ec479cb12e7a61d70c5d703c65cfcb5cceb0

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=The frozen dictionary row records this gloss: 

### AUTHORED_ENGLISH `en-u4-045`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: da365624f31cfe2c4859a5db6ab64b1d07172f4784e8af763010f1f19b001367

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=.

### AUTHORED_ENGLISH `en-u4-046`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: a19237e613eda89bdb474e40d4f3e32f9cdf30157ac3af1304078f7cac5a0cf1

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Which admitted present form is recorded for the row glossed 

### AUTHORED_ENGLISH `en-u4-047`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 7a212bdeb5226010dfc18b2cc5a8202e4279ec946478776086da1bae9322c042

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=The frozen row records 

### AUTHORED_ENGLISH `en-u4-048`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: ef2341a2376a9abe64479b0636b5d664b4d6ee9d53f3dbf09a5176ef0a633dda

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text= for the row glossed 

### AUTHORED_ENGLISH `en-u4-049`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: e82b0abb52dcbe98f09f105444c47b3a067c23cf591792717813344f8f4fec81

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=1

### AUTHORED_ENGLISH `en-u4-050`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 9a29e7a87b1f4eb3f1229094a1f0d00fdd753e84feb3c795325d9905889ead62

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=2

### AUTHORED_ENGLISH `en-u4-051`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: f4d91c3b21a09a26a86ab46fa4e1ade7667cb9059f51c147ec65ad3659515264

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=3

### AUTHORED_ENGLISH `en-u4-052`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 250fa350ba7799bfbdf037f5c55ef164bcbd97ed9e814399a4544c19ee71b12d

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=4

### AUTHORED_ENGLISH `en-u4-053`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 0031a24fe1e49b9bc7b5bc1f6ed8e2542551b20bfca8c2c1a9afe3a023e61764

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Written mark from the admitted row

### AUTHORED_ENGLISH `en-u4-054`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 84f3bd08fac439163ac29603bdfdf19b4ebfd10b9d9d5e61c326f41b76a29425

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Which vowel carries the admitted written stress mark? Count vowels from the left.

### AUTHORED_ENGLISH `en-u4-055`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: f71ade54b3834611aaea604903a4d0685174ddc0c86dc0d94eb2cf31bc96a71c

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=The frozen accented field places the written mark on vowel 

### AUTHORED_ENGLISH `en-u4-056`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 0990acade5e05287666e6b0396d4905aeb92349a0b3acc09b6efc89fbf5c0006

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Which admitted example is the shown sentence?

### AUTHORED_ENGLISH `en-u4-057`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 5e8e4d8ecd00b49edbe28b117dd88032574fca354d19b3b3560c7129980f6acb

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=The frozen page records this example as: 

### AUTHORED_ENGLISH `en-u4-058`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: a534f5af873945fdaf51af8cfa71456cdd859f728e2b187989a7922d86914a52

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=The admitted dictionary rows supply this pair. 

### AUTHORED_ENGLISH `en-u4-059`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 53633e81d3b88356a76a247aefffefe088eab06d41b2e1b12948d6f69f211b1f

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Two speaker rows

### AUTHORED_ENGLISH `en-u4-060`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 71295ff097f3a5963e5ba44a6c8aaa79e35af3ab0b9dea090f2a68e6273f3725

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Two addressee rows

### AUTHORED_ENGLISH `en-u4-061`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 96076748272aff282b893b33ecde939b403e5c433d97a488eced95e177d0dad8

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=The admitted dictionary rows supply this group. 

### AUTHORED_ENGLISH `en-u4-062`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 916925c1d14e7566bf515b29d355db7b93e3ce260740a7a98e2b8dfcac07db70

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Four other-person rows

### AUTHORED_ENGLISH `en-u4-063`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: e4bc9b72d7554b3871943b9a0670b9d7d57c6da6c90c5b7701938c3d86af5f3f

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=How the frozen page forms the present

### AUTHORED_ENGLISH `en-u4-064`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 4fbd9c3026ec611d0cbde4e46a45cf44a5746790a26cbf0958e61046677ad767

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=The frozen page says: 

### AUTHORED_ENGLISH `en-u4-065`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 1e1de666ff8bccd57de384fdf706fb894136bad006ced388889a855a1d556c07

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=These fields are copied from the admitted 

### AUTHORED_ENGLISH `en-u4-066`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: ed3e09a2066b297e79157e50bc52ba6cf58de1425b19c71815a320310218bf3e

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text= row. 

### AUTHORED_ENGLISH `en-u4-067`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 070a30069e8fd56d3d565fa406431887d7d7c90acd5bbcc5f0e9ef39d2d0e47f

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Row glossed 

### AUTHORED_ENGLISH `en-u4-068`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: d7adac9e28562695abf2288354d0b1d5ffbcedf82af713ad087634ede2c48b96

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=: 

### AUTHORED_ENGLISH `en-u4-069`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 2e96932b72027aa689a20afd314f7a4997086d97ff28daf6191b84e9a75dac11

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=. 

### AUTHORED_ENGLISH `en-u4-070`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 507c88969de26d85659e2a2d9205490e4453d3b606b8f18323cc93e4c663849e

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Admitted fields from one row

### AUTHORED_ENGLISH `en-u4-071`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: e85e07985deb1cc00a6088c55c70873309e8f4c8306b71037140f58072e78080

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=One admitted row for a second infinitive

### AUTHORED_ENGLISH `en-u4-072`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 81beeca75f194b19e7bc2f980f633f49d8e8cf6490162b28bd55e7afdee20144

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=The admitted row records 

### AUTHORED_ENGLISH `en-u4-073`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 2d8a079f9c866df9449dd0770c02be298337e5efea86a8d632b7b7aa55269a1a

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Four more admitted infinitives

### AUTHORED_ENGLISH `en-u4-074`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: bd89117cc3c3804e236e0d0bc552bcdbaf89fbc7663f65e93ac3e236bb13f516

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=These infinitives are copied from admitted rows. 

### AUTHORED_ENGLISH `en-u4-075`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 48e9b065ce19d8f36dbff863f6861f83437183194a7684a6eb674bf04c5abc8d

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Four further admitted infinitives

### AUTHORED_ENGLISH `en-u4-076`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 696fa1691c8fbc297e955770b1b17d3bd34d5af4cb9aabd507af006e48814a8a

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Two further admitted infinitives

### AUTHORED_ENGLISH `en-u4-077`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: e1769098791167f9f9a988fad64af3275e5959e610c229d918e50ae7c0af1050

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=The admitted written mark

### AUTHORED_ENGLISH `en-u4-078`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 9194bdcaa2ba0b128eab299a8347cd1854ae225c3ae889579c04eb749a33071d

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=The mark in the admitted form is written. Listening and speaking stay with the book. No audio is mapped to these forms. 

### AUTHORED_ENGLISH `en-u4-079`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: d66aea38852760ea546cdd1c9bbfd0dd739f6cfcabd4d630504fbdd17934e3c8

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=The count named on the frozen page

### AUTHORED_ENGLISH `en-u4-080`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 8f0e684e7e5c8fe1ab78e583de98fb4fe81d112189cefe46a1c30f323b00fa34

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=The default form on the frozen page

### AUTHORED_ENGLISH `en-u4-081`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 428214ca2d067fd7fb443dc308b0ac35ea55af7fde7c6c35fb2dfcd115423a6e

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=These examples are exact frozen excerpts. 

### AUTHORED_ENGLISH `en-u4-082`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: b1cbac3128c3355b83a9eda4e51d459e7c0fd9a2bdd2feac459e160dc6c46265

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Two admitted examples

### AUTHORED_ENGLISH `en-u4-083`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 5936301ef88ca19b6cd4f3ca0de01d038ab50efbc0dd8876e4ddd48c3088b694

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Person words, present forms, a written mark, and dictionary-form examples

### AUTHORED_ENGLISH `en-u4-084`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 00abf6f17185dfcfd04dbefd1a581e531d10598e523802c5b19b1889f47977da

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Proposed Unit 4 uses frozen rows for the person words, the present forms, the written mark, and the dictionary-form examples. The written mark is not a listening score. The owned book remains separate work. Qualified review is pending.

### AUTHORED_ENGLISH `en-u4-085`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 557ff75688c7cecf8e22a4fab17454fe7a5f7696fe01f94392a4ad102a13ec6e

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Open Lesson 4 in the copy you own. The app records do not replace that lesson.

### AUTHORED_ENGLISH `en-u4-086`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: b3d459073a2dafcacaab97cb3f49c21e9bbbf9e3b4b2eef3d859fcd274915b5c

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Guided practice

### AUTHORED_ENGLISH `en-u4-087`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 6aacd8eb1f811acce828dd37b98628f15251c3baa1ee61c517127db02d3c8fbd

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Choose the admitted record. The feedback states the frozen field.

### AUTHORED_ENGLISH `en-u4-088`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: da7380f0caedf6391ab3101d108380ea96707afa1c2087d92b280cb964dea0be

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Remediation

### AUTHORED_ENGLISH `en-u4-089`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 0e49d73a64ea7f5f41dc0ba535b9567c22078838d87d0fad077105b176b1bd59

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Return to the explanation. Compare the shown form with the admitted record. Do not invent a form that is absent from the record.

### AUTHORED_ENGLISH `en-u4-090`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 9159f31d30f4efedbdece9e79b61318fc1de4f56caf1d8c889fe25a796533f7c

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Independent checks

### AUTHORED_ENGLISH `en-u4-091`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 0ef5f1e996ff63e29b24e21a1df293ba966b7d18dca9e95c244d42b609208865

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=These checks use only admitted records. A miss returns to the explanation.

### AUTHORED_ENGLISH `en-u4-092`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 8a6cd2ce30cdcb2e13c8bafdc773967300c16439286c0f6bab79a20681077ae2

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Work Lesson 4 in your copy

### AUTHORED_ENGLISH `en-u4-093`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: a0c5463a498736826e0cccb9da28750a6c57831636c32f79c0f50dd04f058854

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Do Lesson 4 in the copy you own. Use that book's explanations and practice. The proposed records in this unit are a separate licensed set and do not copy the book.

### AUTHORED_ENGLISH `en-u4-094`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 9b933685d8eee50ea618176982030661ce3a3795491f79e9e3cedebcc591abe4

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Open Lesson 4 in your copy and complete its lesson work.

### AUTHORED_ENGLISH `en-u4-095`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 6161ad568ceb436bc310d4278b41090501081de17307dc64d464153eaf9ef98d

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Record Lesson 4 book work

### AUTHORED_ENGLISH `en-u4-096`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: ed50348036c46fbd0a798428477c416b075e1acc281e099ae7f9015f88b194a0

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=When you have done Lesson 4 in your copy, self-attest here. Attestation records your report. It does not grade the lesson, approve a mapping, or approve the proposed records.

### AUTHORED_ENGLISH `en-u4-097`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: c037ed827dc25d9e975b3d882c95125c926a0296a998355fa3e87137a3de55a7

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Self-attest only after the Lesson 4 work in your copy.

### SOURCE `src-openrussian-50e210c4`

- Objectives: none
- Sessions: none
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 640c21ca6b29d6acb6053b4c9a6bb73487d1512f72fbf388bfa8c595f44d6579

OpenRussian dictionary data
CC BY-SA 4.0
Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
Selected rows, stress-mark formatting, and beginner sense selection. No generated forms.

### SOURCE `src-wikibooks-russian-cc-by-sa`

- Objectives: none
- Sessions: none
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 20039cd983c84c1ba99787b2d548152844f49f4d7eb4fd378f10e3da956e7358

Wikibooks Russian grammar
CC BY-SA 4.0
Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
Selected excerpts. Russian quotations are unchanged. English claims are quoted, not paraphrased.

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

### RIGHTS `rights-openrussian-50e210c4`

- Objectives: none
- Sessions: none
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 7a5216cdcd486f156994849259b489197f16ad81e034b724318597da17ba76cf

src-openrussian-50e210c4
CC BY-SA 4.0
permitsAppUse=true

### RIGHTS `rights-wikibooks-russian-cc-by-sa`

- Objectives: none
- Sessions: none
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 1768f7fe2b673503aa632530903796a2567dca6256676f69a209257314b437e0

src-wikibooks-russian-cc-by-sa
CC BY-SA 4.0
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

- `obj-u4-pronouns` INSTRUCTION `u4-st-orient`
- `obj-u4-pronouns` INSTRUCTION `u4-st-teach-pronouns-1`
- `obj-u4-pronouns` INSTRUCTION `u4-st-teach-pronouns-2`
- `obj-u4-pronouns` INSTRUCTION `u4-st-teach-pronouns-3`
- `obj-u4-pronouns` EXAMPLE `ex-u4-я`
- `obj-u4-pronouns` EXAMPLE `ex-u4-мы`
- `obj-u4-pronouns` EXAMPLE `ex-u4-ты`
- `obj-u4-pronouns` EXAMPLE `ex-u4-вы`
- `obj-u4-pronouns` EXAMPLE `ex-u4-он`
- `obj-u4-pronouns` EXAMPLE `ex-u4-она`
- `obj-u4-pronouns` EXAMPLE `ex-u4-оно`
- `obj-u4-pronouns` EXAMPLE `ex-u4-они`
- `obj-u4-pronouns` PRACTICE `u4-st-guided-pronouns-1`
- `obj-u4-pronouns` CHECK `u4-i-pronoun-ты`
- `obj-u4-pronouns` CHECK `u4-i-pronoun-вы`
- `obj-u4-pronouns` CHECK `u4-i-pronoun-он`
- `obj-u4-pronouns` CHECK `u4-i-pronoun-она`
- `obj-u4-pronouns` CHECK `u4-i-pronoun-оно`
- `obj-u4-pronouns` CHECK `u4-i-pronoun-они`
- `obj-u4-pronouns` REMEDIATION `u4-st-rem-pronouns`
- `obj-u4-present` INSTRUCTION `u4-st-orient`
- `obj-u4-present` INSTRUCTION `u4-st-teach-present-1`
- `obj-u4-present` INSTRUCTION `u4-st-teach-present-2`
- `obj-u4-present` INSTRUCTION `u4-st-teach-present-3`
- `obj-u4-present` INSTRUCTION `u4-st-teach-present-4`
- `obj-u4-present` INSTRUCTION `u4-st-teach-present-5`
- `obj-u4-present` INSTRUCTION `u4-st-teach-present-6`
- `obj-u4-present` INSTRUCTION `u4-st-teach-present-7`
- `obj-u4-present` EXAMPLE `ex-u4-verb-читать`
- `obj-u4-present` EXAMPLE `ex-u4-verb-делать`
- `obj-u4-present` PRACTICE `u4-st-guided-present-2`
- `obj-u4-present` PRACTICE `u4-st-guided-present-4`
- `obj-u4-present` CHECK `u4-i-verb-говорить-мы`
- `obj-u4-present` CHECK `u4-i-verb-видеть-он`
- `obj-u4-present` CHECK `u4-i-verb-писать-вы`
- `obj-u4-present` CHECK `u4-i-verb-идти-они`
- `obj-u4-present` REMEDIATION `u4-st-rem-present`
- `obj-u4-stress` INSTRUCTION `u4-st-orient`
- `obj-u4-stress` INSTRUCTION `u4-st-teach-stress-1`
- `obj-u4-stress` EXAMPLE `ex-u4-stress-6f15e9f1`
- `obj-u4-stress` EXAMPLE `ex-u4-stress-1f37b6b2`
- `obj-u4-stress` EXAMPLE `ex-u4-stress-2114b12f`
- `obj-u4-stress` PRACTICE `u4-st-guided-stress-1`
- `obj-u4-stress` CHECK `u4-i-stress-1f37b6b2`
- `obj-u4-stress` CHECK `u4-i-stress-2114b12f`
- `obj-u4-stress` REMEDIATION `u4-st-rem-stress`
- `obj-u4-nominative` INSTRUCTION `u4-st-orient`
- `obj-u4-nominative` INSTRUCTION `u4-st-teach-nominative-1`
- `obj-u4-nominative` INSTRUCTION `u4-st-teach-nominative-2`
- `obj-u4-nominative` INSTRUCTION `u4-st-teach-nominative-3`
- `obj-u4-nominative` EXAMPLE `ex-u4-nom-muzh`
- `obj-u4-nominative` EXAMPLE `ex-u4-nom-poezd`
- `obj-u4-nominative` PRACTICE `u4-st-teach-nominative-1`
- `obj-u4-nominative` CHECK `u4-i-nom-muzh`
- `obj-u4-nominative` CHECK `u4-i-nom-poezd`
- `obj-u4-nominative` REMEDIATION `u4-st-rem-nominative`
- `obj-u4-book` INSTRUCTION `u4-st-book-work`
- `obj-u4-book` INSTRUCTION `u4-st-book`
- `u4-s-orient` OBJECTIVE `obj-u4-pronouns`
- `u4-s-orient` OBJECTIVE `obj-u4-present`
- `u4-s-orient` OBJECTIVE `obj-u4-stress`
- `u4-s-orient` OBJECTIVE `obj-u4-nominative`
- `u4-s-orient` OBJECTIVE `obj-u4-book`
- `u4-st-orient` REFERENCE `ref-u35-attribution`
- `u4-s-pronouns` OBJECTIVE `obj-u4-pronouns`
- `u4-st-teach-pronouns-1` EXAMPLE `ex-u4-я`
- `u4-st-teach-pronouns-1` EXAMPLE `ex-u4-мы`
- `u4-st-teach-pronouns-1` REFERENCE `ref-u35-attribution`
- `u4-st-guided-pronouns-1` REMEDIATION `u4-st-rem-pronouns`
- `u4-st-guided-pronouns-1` ITEM `u4-i-pronoun-я`
- `u4-st-guided-pronouns-1` ITEM `u4-i-pronoun-мы`
- `u4-st-teach-pronouns-2` EXAMPLE `ex-u4-ты`
- `u4-st-teach-pronouns-2` EXAMPLE `ex-u4-вы`
- `u4-st-teach-pronouns-2` REFERENCE `ref-u35-attribution`
- `u4-st-teach-pronouns-3` EXAMPLE `ex-u4-он`
- `u4-st-teach-pronouns-3` EXAMPLE `ex-u4-она`
- `u4-st-teach-pronouns-3` EXAMPLE `ex-u4-оно`
- `u4-st-teach-pronouns-3` EXAMPLE `ex-u4-они`
- `u4-st-teach-pronouns-3` REFERENCE `ref-u35-attribution`
- `u4-st-rem-pronouns` EXAMPLE `ex-u4-я`
- `u4-st-rem-pronouns` EXAMPLE `ex-u4-мы`
- `u4-st-check-pronouns` REMEDIATION `u4-st-rem-pronouns`
- `u4-st-check-pronouns` ITEM `u4-i-pronoun-ты`
- `u4-st-check-pronouns` ITEM `u4-i-pronoun-вы`
- `u4-st-check-pronouns` ITEM `u4-i-pronoun-он`
- `u4-st-check-pronouns` ITEM `u4-i-pronoun-она`
- `u4-st-check-pronouns` ITEM `u4-i-pronoun-оно`
- `u4-st-check-pronouns` ITEM `u4-i-pronoun-они`
- `u4-s-present` OBJECTIVE `obj-u4-present`
- `u4-st-teach-present-1` REFERENCE `ref-u35-attribution`
- `u4-st-teach-present-2` EXAMPLE `ex-u4-verb-читать`
- `u4-st-teach-present-2` REFERENCE `ref-u35-attribution`
- `u4-st-guided-present-2` REMEDIATION `u4-st-rem-present`
- `u4-st-guided-present-2` ITEM `u4-i-verb-читать-ты`
- `u4-st-teach-present-3` REFERENCE `ref-u35-attribution`
- `u4-st-teach-present-4` EXAMPLE `ex-u4-verb-делать`
- `u4-st-teach-present-4` REFERENCE `ref-u35-attribution`
- `u4-st-guided-present-4` REMEDIATION `u4-st-rem-present`
- `u4-st-guided-present-4` ITEM `u4-i-verb-делать-я`
- `u4-st-teach-present-5` REFERENCE `ref-u35-attribution`
- `u4-st-teach-present-6` REFERENCE `ref-u35-attribution`
- `u4-st-teach-present-7` REFERENCE `ref-u35-attribution`
- `u4-st-rem-present` EXAMPLE `ex-u4-verb-читать`
- `u4-st-rem-present` EXAMPLE `ex-u4-verb-делать`
- `u4-st-check-present` REMEDIATION `u4-st-rem-present`
- `u4-st-check-present` ITEM `u4-i-verb-говорить-мы`
- `u4-st-check-present` ITEM `u4-i-verb-видеть-он`
- `u4-st-check-present` ITEM `u4-i-verb-писать-вы`
- `u4-st-check-present` ITEM `u4-i-verb-идти-они`
- `u4-s-stress` OBJECTIVE `obj-u4-stress`
- `u4-st-teach-stress-1` EXAMPLE `ex-u4-stress-6f15e9f1`
- `u4-st-teach-stress-1` EXAMPLE `ex-u4-stress-1f37b6b2`
- `u4-st-teach-stress-1` EXAMPLE `ex-u4-stress-2114b12f`
- `u4-st-teach-stress-1` REFERENCE `ref-u35-attribution`
- `u4-st-guided-stress-1` REMEDIATION `u4-st-rem-stress`
- `u4-st-guided-stress-1` ITEM `u4-i-stress-6f15e9f1`
- `u4-st-rem-stress` EXAMPLE `ex-u4-stress-6f15e9f1`
- `u4-st-rem-stress` EXAMPLE `ex-u4-stress-1f37b6b2`
- `u4-st-check-stress` REMEDIATION `u4-st-rem-stress`
- `u4-st-check-stress` ITEM `u4-i-stress-1f37b6b2`
- `u4-st-check-stress` ITEM `u4-i-stress-2114b12f`
- `u4-s-nominative` OBJECTIVE `obj-u4-nominative`
- `u4-st-teach-nominative-1` REFERENCE `ref-u35-attribution`
- `u4-st-teach-nominative-2` REFERENCE `ref-u35-attribution`
- `u4-st-teach-nominative-3` EXAMPLE `ex-u4-nom-muzh`
- `u4-st-teach-nominative-3` EXAMPLE `ex-u4-nom-poezd`
- `u4-st-teach-nominative-3` REFERENCE `ref-u35-attribution`
- `u4-st-rem-nominative` EXAMPLE `ex-u4-nom-muzh`
- `u4-st-rem-nominative` EXAMPLE `ex-u4-nom-poezd`
- `u4-st-check-nominative` REMEDIATION `u4-st-rem-nominative`
- `u4-st-check-nominative` ITEM `u4-i-nom-muzh`
- `u4-st-check-nominative` ITEM `u4-i-nom-poezd`
- `u4-s-book` OBJECTIVE `obj-u4-book`
- `u4-st-book-work` REFERENCE `ref-book-topics`
- `u4-st-book-work` REFERENCE `ref-u35-attribution`
- `obj-u4-nominative` PROVENANCE `prov-u4-cases-default`
- `obj-u4-nominative` PROVENANCE `prov-u4-cases-six`
- `obj-u4-present` PROVENANCE `prov-u4-form-видеть-вы`
- `obj-u4-present` PROVENANCE `prov-u4-form-видеть-мы`
- `obj-u4-present` PROVENANCE `prov-u4-form-видеть-он`
- `obj-u4-present` PROVENANCE `prov-u4-form-видеть-они`
- `obj-u4-present` PROVENANCE `prov-u4-form-видеть-ты`
- `obj-u4-present` PROVENANCE `prov-u4-form-видеть-я`
- `obj-u4-present` PROVENANCE `prov-u4-form-говорить-вы`
- `obj-u4-present` PROVENANCE `prov-u4-form-говорить-мы`
- `obj-u4-present` PROVENANCE `prov-u4-form-говорить-он`
- `obj-u4-present` PROVENANCE `prov-u4-form-говорить-они`
- `obj-u4-present` PROVENANCE `prov-u4-form-говорить-ты`
- `obj-u4-present` PROVENANCE `prov-u4-form-говорить-я`
- `obj-u4-present` PROVENANCE `prov-u4-form-делать-вы`
- `obj-u4-present` PROVENANCE `prov-u4-form-делать-мы`
- `obj-u4-present` PROVENANCE `prov-u4-form-делать-он`
- `obj-u4-present` PROVENANCE `prov-u4-form-делать-они`
- `obj-u4-present` PROVENANCE `prov-u4-form-делать-ты`
- `obj-u4-present` PROVENANCE `prov-u4-form-делать-я`
- `obj-u4-present` PROVENANCE `prov-u4-form-жить-вы`
- `obj-u4-present` PROVENANCE `prov-u4-form-жить-мы`
- `obj-u4-present` PROVENANCE `prov-u4-form-жить-он`
- `obj-u4-present` PROVENANCE `prov-u4-form-жить-они`
- `obj-u4-present` PROVENANCE `prov-u4-form-жить-ты`
- `obj-u4-present` PROVENANCE `prov-u4-form-жить-я`
- `obj-u4-present` PROVENANCE `prov-u4-form-знать-вы`
- `obj-u4-present` PROVENANCE `prov-u4-form-знать-мы`
- `obj-u4-present` PROVENANCE `prov-u4-form-знать-он`
- `obj-u4-present` PROVENANCE `prov-u4-form-знать-они`
- `obj-u4-present` PROVENANCE `prov-u4-form-знать-ты`
- `obj-u4-present` PROVENANCE `prov-u4-form-знать-я`
- `obj-u4-present` PROVENANCE `prov-u4-form-идти-вы`
- `obj-u4-present` PROVENANCE `prov-u4-form-идти-мы`
- `obj-u4-present` PROVENANCE `prov-u4-form-идти-он`
- `obj-u4-present` PROVENANCE `prov-u4-form-идти-они`
- `obj-u4-present` PROVENANCE `prov-u4-form-идти-ты`
- `obj-u4-present` PROVENANCE `prov-u4-form-идти-я`
- `obj-u4-present` PROVENANCE `prov-u4-form-писать-вы`
- `obj-u4-present` PROVENANCE `prov-u4-form-писать-мы`
- `obj-u4-present` PROVENANCE `prov-u4-form-писать-он`
- `obj-u4-present` PROVENANCE `prov-u4-form-писать-они`
- `obj-u4-present` PROVENANCE `prov-u4-form-писать-ты`
- `obj-u4-present` PROVENANCE `prov-u4-form-писать-я`
- `obj-u4-present` PROVENANCE `prov-u4-form-понимать-вы`
- `obj-u4-present` PROVENANCE `prov-u4-form-понимать-мы`
- `obj-u4-present` PROVENANCE `prov-u4-form-понимать-он`
- `obj-u4-present` PROVENANCE `prov-u4-form-понимать-они`
- `obj-u4-present` PROVENANCE `prov-u4-form-понимать-ты`
- `obj-u4-present` PROVENANCE `prov-u4-form-понимать-я`
- `obj-u4-present` PROVENANCE `prov-u4-form-работать-вы`
- `obj-u4-present` PROVENANCE `prov-u4-form-работать-мы`
- `obj-u4-present` PROVENANCE `prov-u4-form-работать-он`
- `obj-u4-present` PROVENANCE `prov-u4-form-работать-они`
- `obj-u4-present` PROVENANCE `prov-u4-form-работать-ты`
- `obj-u4-present` PROVENANCE `prov-u4-form-работать-я`
- `obj-u4-present` PROVENANCE `prov-u4-form-слушать-вы`
- `obj-u4-present` PROVENANCE `prov-u4-form-слушать-мы`
- `obj-u4-present` PROVENANCE `prov-u4-form-слушать-он`
- `obj-u4-present` PROVENANCE `prov-u4-form-слушать-они`
- `obj-u4-present` PROVENANCE `prov-u4-form-слушать-ты`
- `obj-u4-present` PROVENANCE `prov-u4-form-слушать-я`
- `obj-u4-present` PROVENANCE `prov-u4-form-смотреть-вы`
- `obj-u4-present` PROVENANCE `prov-u4-form-смотреть-мы`
- `obj-u4-present` PROVENANCE `prov-u4-form-смотреть-он`
- `obj-u4-present` PROVENANCE `prov-u4-form-смотреть-они`
- `obj-u4-present` PROVENANCE `prov-u4-form-смотреть-ты`
- `obj-u4-present` PROVENANCE `prov-u4-form-смотреть-я`
- `obj-u4-present` PROVENANCE `prov-u4-form-читать-вы`
- `obj-u4-present` PROVENANCE `prov-u4-form-читать-мы`
- `obj-u4-present` PROVENANCE `prov-u4-form-читать-он`
- `obj-u4-present` PROVENANCE `prov-u4-form-читать-они`
- `obj-u4-present` PROVENANCE `prov-u4-form-читать-ты`
- `obj-u4-present` PROVENANCE `prov-u4-form-читать-я`
- `obj-u4-present` PROVENANCE `prov-u4-govorit-gloss`
- `obj-u4-nominative` PROVENANCE `prov-u4-nom-muzh`
- `obj-u4-nominative` PROVENANCE `prov-u4-nom-muzh-en`
- `obj-u4-nominative` PROVENANCE `prov-u4-nom-poezd`
- `obj-u4-nominative` PROVENANCE `prov-u4-nom-poezd-en`
- `obj-u4-present` PROVENANCE `prov-u4-present-rule`
- `obj-u4-pronouns` PROVENANCE `prov-u4-pronoun-gloss-вы`
- `obj-u4-pronouns` PROVENANCE `prov-u4-pronoun-gloss-мы`
- `obj-u4-pronouns` PROVENANCE `prov-u4-pronoun-gloss-он`
- `obj-u4-pronouns` PROVENANCE `prov-u4-pronoun-gloss-она`
- `obj-u4-pronouns` PROVENANCE `prov-u4-pronoun-gloss-они`
- `obj-u4-pronouns` PROVENANCE `prov-u4-pronoun-gloss-оно`
- `obj-u4-pronouns` PROVENANCE `prov-u4-pronoun-gloss-ты`
- `obj-u4-pronouns` PROVENANCE `prov-u4-pronoun-gloss-я`
- `obj-u4-pronouns` PROVENANCE `prov-u4-pronoun-вы`
- `obj-u4-pronouns` PROVENANCE `prov-u4-pronoun-мы`
- `obj-u4-pronouns` PROVENANCE `prov-u4-pronoun-он`
- `obj-u4-pronouns` PROVENANCE `prov-u4-pronoun-она`
- `obj-u4-pronouns` PROVENANCE `prov-u4-pronoun-они`
- `obj-u4-pronouns` PROVENANCE `prov-u4-pronoun-оно`
- `obj-u4-pronouns` PROVENANCE `prov-u4-pronoun-ты`
- `obj-u4-pronouns` PROVENANCE `prov-u4-pronoun-я`
- `obj-u4-present` PROVENANCE `prov-u4-verb-gloss-видеть`
- `obj-u4-present` PROVENANCE `prov-u4-verb-gloss-делать`
- `obj-u4-present` PROVENANCE `prov-u4-verb-gloss-жить`
- `obj-u4-present` PROVENANCE `prov-u4-verb-gloss-знать`
- `obj-u4-present` PROVENANCE `prov-u4-verb-gloss-идти`
- `obj-u4-present` PROVENANCE `prov-u4-verb-gloss-писать`
- `obj-u4-present` PROVENANCE `prov-u4-verb-gloss-понимать`
- `obj-u4-present` PROVENANCE `prov-u4-verb-gloss-работать`
- `obj-u4-present` PROVENANCE `prov-u4-verb-gloss-слушать`
- `obj-u4-present` PROVENANCE `prov-u4-verb-gloss-смотреть`
- `obj-u4-present` PROVENANCE `prov-u4-verb-gloss-читать`
- `obj-u4-present` PROVENANCE `prov-u4-verb-видеть`
- `obj-u4-present` PROVENANCE `prov-u4-verb-говорить`
- `obj-u4-present` PROVENANCE `prov-u4-verb-делать`
- `obj-u4-present` PROVENANCE `prov-u4-verb-жить`
- `obj-u4-present` PROVENANCE `prov-u4-verb-знать`
- `obj-u4-present` PROVENANCE `prov-u4-verb-идти`
- `obj-u4-present` PROVENANCE `prov-u4-verb-писать`
- `obj-u4-present` PROVENANCE `prov-u4-verb-понимать`
- `obj-u4-present` PROVENANCE `prov-u4-verb-работать`
- `obj-u4-present` PROVENANCE `prov-u4-verb-слушать`
- `obj-u4-present` PROVENANCE `prov-u4-verb-смотреть`
- `obj-u4-present` PROVENANCE `prov-u4-verb-читать`

## Book-directed experiences

- `u4-st-orient`
- `u4-st-book-work`
- `u4-st-book`

## Unavailable experiences

None.


## Unresolved relationships

None. Every referenced Unit 4 identifier resolves.
