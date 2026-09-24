# Unit 13 linguistic and owned-edition mapping packet

This packet prepares review. It records an unsigned structural mapping proposal. It does not record a qualified review, an approved edition alignment, or an install decision.

The owned-edition mapping worksheet separates observed edition facts, the proposed lesson and printed page range, unresolved edition questions, and matters that still require qualified linguistic review. ISBN, owned-copy verification, variance, reviewer identity, qualification, date, and approval stay blank. The disposition remains PENDING_CURRICULUM_REVIEWER.

Unit 13 remains in development. Its readiness is content-complete and review-pending. The Lesson 13 relationship is a proposed, unsigned mapping. It is not an approved edition alignment.

## Package identity

- Schema version: 3
- Application: dev.russiancompanion
- Version: 2.0.0-dev (5)
- Curriculum source commit: 7e6b3f2de508062126ead6dcaa7cceae9fa9b49f
- Unit content-review SHA-256: fae727446e997181f3b0109cccb5825431536966646130997074913bb67eab56
- Mapping-package SHA-256: 8a1ca19f20178d0c0882bb24e7cc74914684c8735ff4089dc95727f3a7427f7f
- Pack content-review SHA-256: 74e0cbc8db1287db0b8786cd5d8b57468ac9df9f3c79d45ace4729550afd271a
- Review-package SHA-256: a48586506c306de76f8e83f86526d24fc5a72b173d9a9b482870b371a6deef54
- Repository source SHA-256: 86fc66ec7d4634bf6043b9a0e7aa3857203768abb74b7e7c75edabd6845e8101

`unitContentReviewDigest` binds this unit's instructional curriculum. It covers this unit and the reachable instructional records, relationships, shared dependencies, sources, rights, assets, and advancement rule. It does not cover any other unit. Recording a review status, reviewer signature, mapping approval, install state, or readiness does not change it.

`mappingPackageDigest` binds the proposed edition-mapping facts for Unit 13: edition identity, lesson, printed page range, topic description, observation source, ISBN observation state, and the pending disposition. Reviewer identity, qualification, date, notes, correction notes, and signature stay outside that digest.

`packContentReviewDigest` is the repository aggregate of instructional curriculum. It is not an approval of this unit, and it is not part of the review-package digest.

`reviewPackageDigest` identifies this complete pending review package. It binds the ordered instructional records and the mapping-package digest. Changing a proposed page range or topic changes the mapping-package digest and therefore this package digest.

`repositorySourceDigest` binds repository and release inputs. Recording an approval later changes that repository hash. That change does not replace the signed content-review digest or the mapping-package digest.

The marker scan used by this package detects only a fixed list of known prohibited mapping-input markers. It is not comprehensive copyright detection and cannot recognize every copy or close paraphrase of protected material.

## Unit

number=13
title=Aspect in the past, tense use, infinitive aspect, and reported speech
topicSummary=Proposed Unit 13 is a development unit awaiting qualified review. It contains admitted records for this lesson's governed objectives, plus separate book self-attestation. It is not installed and the owned-edition mapping is not signed.
installState=DEVELOPMENT
readiness=CONTENT_COMPLETE_REVIEW_PENDING
mappingStatus=PENDING_CURRICULUM_REVIEWER
mappingNote=Proposed Lesson 13, printed pages 149-161. Independently stated topic scope: Aspect in past-tense contexts, tense selection, infinitive aspect, and reported speech. The Product Owner asserts that the inspected file is a legally owned copy. That assertion is not reviewer verification. Mapping status is PENDING_CURRICULUM_REVIEWER. This note does not approve coverage, variance, or alignment.
penguinLessonField=13
prerequisites=12
estimatedMinutes=66

## Not yet approved

No reviewer has signed this packet. Reviewer identity, qualification, review date, disposition, and reviewed digest are blank. Do not treat this development unit as reviewed, installable, or installed.

## Records

### UNIT_REVIEW `rev-pending-unit-13`

- Objectives: none
- Sessions: none
- Source: none
- Rights: none
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 810847b035daaadd4903eabb7357cbdae2e2e0e4443d6f898cb42ce76fa4604d

status=PENDING_QUALIFIED_REVIEW
reviewer=
reviewerQualification=
packVersion=
reviewedContentDigest=
curriculumMappingId=
notes=No qualified reviewer has signed Unit 13.

### UNIT `unit-13`

- Objectives: obj-u13-past-aspect, obj-u13-tense-keep, obj-u13-infinitive, obj-u13-reported, obj-u13-book
- Sessions: u13-s-orient, u13-s-past-aspect, u13-s-tense-keep, u13-s-infinitive, u13-s-reported, u13-s-book
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: de19a59a99179304657bbec701924781312f87723e2f3578218fe430a1fd66c0

number=13
title=Aspect in the past, tense use, infinitive aspect, and reported speech
topicSummary=Proposed Unit 13 is a development unit awaiting qualified review. It contains admitted records for this lesson's governed objectives, plus separate book self-attestation. It is not installed and the owned-edition mapping is not signed.
installState=DEVELOPMENT
readiness=CONTENT_COMPLETE_REVIEW_PENDING
mappingStatus=PENDING_CURRICULUM_REVIEWER
mappingNote=Proposed Lesson 13, printed pages 149-161. Independently stated topic scope: Aspect in past-tense contexts, tense selection, infinitive aspect, and reported speech. The Product Owner asserts that the inspected file is a legally owned copy. That assertion is not reviewer verification. Mapping status is PENDING_CURRICULUM_REVIEWER. This note does not approve coverage, variance, or alignment.
penguinLessonField=13
prerequisites=12
estimatedMinutes=66

### ADVANCEMENT `unit-13-advancement`

- Objectives: none
- Sessions: none
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 4ebdfa46c8173024b08b244ef9d64af446aeb6f5b7e5d490be14285d9a42e99a

requireInstruction=true
requireBookSelfAttestation=true
requireAppChecks=true
minimumDistinctChecks=8
requireDueReview=false
tutorOptional=true

### OBJECTIVE `obj-u13-past-aspect`

- Objectives: obj-u13-past-aspect
- Sessions: none
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: afdbb49acc9f23e0ab3eaee1036ad1630aa312bcbbcaa3c50c44c7271841fe82

title=Choose the admitted past aspect line
kind=RECOGNITION
unitNumber=13
instruction=u13-st-orient,u13-st-teach-past-aspect-1,u13-st-teach-past-aspect-2,u13-st-teach-past-aspect-3,u13-st-teach-past-aspect-4
examples=ex-u13-past-aspect-0,ex-u13-past-aspect-1,ex-or-u13-past-aspect-2,ex-or-u13-past-aspect-3,ex-or-u13-past-aspect-4
practice=u13-st-guided-past-aspect-1
checks=u13-i-past-aspect-1,u13-i-past-aspect-2
remediation=u13-st-rem-past-aspect

### OBJECTIVE `obj-u13-tense-keep`

- Objectives: obj-u13-tense-keep
- Sessions: none
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 276339aa4b982e5c2d61d9280a8da69964c560c464684d8ab204c65b69082294

title=Choose the admitted report label
kind=RECOGNITION
unitNumber=13
instruction=u13-st-orient,u13-st-teach-tense-keep-1,u13-st-teach-tense-keep-2,u13-st-teach-tense-keep-3
examples=ex-u13-tense-keep-0,ex-u13-tense-keep-1,ex-u13-tense-keep-2
practice=u13-st-guided-tense-keep-1
checks=u13-i-tense-keep-1,u13-i-tense-keep-2
remediation=u13-st-rem-tense-keep

### OBJECTIVE `obj-u13-infinitive`

- Objectives: obj-u13-infinitive
- Sessions: none
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 46694b3f9759a88098be3d64b8e3f953cd3bb62ccffbbab928b1c5beb1f7c565

title=Choose the admitted infinitive partner
kind=RECOGNITION
unitNumber=13
instruction=u13-st-orient,u13-st-teach-infinitive-1,u13-st-teach-infinitive-2,u13-st-teach-infinitive-3
examples=ex-u13-infinitive-0,ex-u13-infinitive-1,ex-u13-infinitive-2,ex-u13-infinitive-3
practice=u13-st-guided-infinitive-1
checks=u13-i-infinitive-1,u13-i-infinitive-2
remediation=u13-st-rem-infinitive

### OBJECTIVE `obj-u13-reported`

- Objectives: obj-u13-reported
- Sessions: none
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 8520d6f311e9bc30d240d836ab7980507c92d8a42d4db5295fb10a47a7926033

title=Choose the admitted reported line
kind=RECOGNITION
unitNumber=13
instruction=u13-st-orient,u13-st-teach-reported-1,u13-st-teach-reported-2,u13-st-teach-reported-3
examples=ex-u13-reported-0,ex-u13-reported-1,ex-u13-reported-2,ex-u13-reported-3
practice=u13-st-guided-reported-1
checks=u13-i-reported-1,u13-i-reported-2
remediation=u13-st-rem-reported

### OBJECTIVE `obj-u13-book`

- Objectives: obj-u13-book
- Sessions: none
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 9418ab0735dab3ccf56e267893fe71c3b4eda0df0a64b0526e80d795941432d5

title=Self-attest Lesson 13 book work
kind=SELF_ATTESTATION
unitNumber=13
instruction=u13-st-book-work,u13-st-book
examples=
practice=
checks=
remediation=

### SESSION `u13-s-orient`

- Objectives: obj-u13-past-aspect, obj-u13-tense-keep, obj-u13-infinitive, obj-u13-reported, obj-u13-book
- Sessions: u13-s-orient
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 5796b49bc88b7679106a57f2c5e2a711ec2222f36d3e7db104d9d4d7eda5dd3d

title=Admitted past aspect and report lines
estimatedMinutes=8
objectives=obj-u13-past-aspect,obj-u13-tense-keep,obj-u13-infinitive,obj-u13-reported,obj-u13-book
steps=u13-st-orient

### STEP `u13-st-orient`

- Objectives: obj-u13-past-aspect, obj-u13-tense-keep, obj-u13-infinitive, obj-u13-reported, obj-u13-book
- Sessions: u13-s-orient
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: fd0ecb16c7ececdd7967cfdf754ec353ac420522babb06206821f5ffb05332e1

kind=ORIENT
title=Admitted past aspect and report lines
body=Proposed Unit 13 uses frozen records for past aspect lines, report labels, and infinitive partners. Qualified review is pending.
dominantAction=Continue
references=ref-u1115-attribution
items=
examples=
unavailableReason=
bookDirection=Open Lesson 13 in the copy you own. The app records do not replace that lesson.
companionDirection=
remediationStepId=

### SESSION `u13-s-past-aspect`

- Objectives: obj-u13-past-aspect
- Sessions: u13-s-past-aspect
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 16d10f77f15ff0d65832b5cb3e20137c205fc7f8b03180bc948c33f240d0a6ab

title=Choose the admitted past aspect line
estimatedMinutes=12
objectives=obj-u13-past-aspect
steps=u13-st-teach-past-aspect-1,u13-st-guided-past-aspect-1,u13-st-teach-past-aspect-2,u13-st-teach-past-aspect-3,u13-st-teach-past-aspect-4,u13-st-rem-past-aspect,u13-st-check-past-aspect

### STEP `u13-st-teach-past-aspect-1`

- Objectives: obj-u13-past-aspect
- Sessions: u13-s-past-aspect
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 6534c0c3e6fe62b0dd323c59b0a83f29ca5e4eacaf37e72e296b5f30299d00dd

kind=TEACH
title=Admitted lines
body=я читал — I was reading. я прочитал — I read. 
dominantAction=Continue
references=ref-u1115-attribution
items=
examples=ex-u13-past-aspect-0,ex-u13-past-aspect-1
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u13-st-guided-past-aspect-1`

- Objectives: obj-u13-past-aspect
- Sessions: u13-s-past-aspect
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: ab66281fc7cc3ca554cc37c8fb35e7d167bc67add1eaba26cb10b7be7b86fc37

kind=GUIDED
title=Guided practice
body=Choose the admitted record. The feedback states the frozen field.
dominantAction=Continue
references=
items=u13-g-past-aspect-0
examples=
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=u13-st-rem-past-aspect

### STEP `u13-st-teach-past-aspect-2`

- Objectives: obj-u13-past-aspect
- Sessions: u13-s-past-aspect
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: d967873f6a684d686388fb1386030ed7429d769179089b2fa4a4d7cd44648e23

kind=TEACH
title=Admitted instruction
body=So the English ''past progressive'' sentence  "I was reading" would correspond to the Russian imperfective ('''я читал'''), since it refers to an action that wasn't completed. Likewise, the English ''simple past'' sentence "I read" corresponds to the Russian perfective ('''я прочитал''')
dominantAction=Continue
references=ref-u1115-attribution
items=
examples=
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u13-st-teach-past-aspect-3`

- Objectives: obj-u13-past-aspect
- Sessions: u13-s-past-aspect
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 2a9e50e94a294f7d1cc73e7528c9cf25ffddbb0cd10d245737ec93d2cbbc68c9

kind=TEACH
title=Admitted lines
body=чита́ть — read. чита́л — read. 
dominantAction=Continue
references=ref-u1115-attribution
items=
examples=ex-or-u13-past-aspect-2,ex-or-u13-past-aspect-3
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u13-st-teach-past-aspect-4`

- Objectives: obj-u13-past-aspect
- Sessions: u13-s-past-aspect
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: ab1aa8a61d9921e4faa93cf1eb163358de221beb51dd633c14b55eadce9442ad

kind=TEACH
title=Admitted lines
body=чита́ла — read. 
dominantAction=Continue
references=ref-u1115-attribution
items=
examples=ex-or-u13-past-aspect-4
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u13-st-rem-past-aspect`

- Objectives: obj-u13-past-aspect
- Sessions: u13-s-past-aspect
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 55f9df0dbe39543fba3e35a00d3728dd48ce6963999d0a10d3b9b88866af2837

kind=REMEDIATE
title=Remediation
body=So the English ''past progressive'' sentence  "I was reading" would correspond to the Russian imperfective ('''я читал'''), since it refers to an action that wasn't completed. Likewise, the English ''simple past'' sentence "I read" corresponds to the Russian perfective ('''я прочитал''')
dominantAction=Continue
references=
items=
examples=ex-u13-past-aspect-0,ex-u13-past-aspect-1
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u13-st-check-past-aspect`

- Objectives: obj-u13-past-aspect
- Sessions: u13-s-past-aspect
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 7b3ee5126da5bb4d84c3d5d40a2c5ae93814a948be1497a667abfa3e8bd5ac7b

kind=CHECK
title=Independent checks
body=These checks use only admitted records. A miss returns to the explanation.
dominantAction=Continue
references=
items=u13-i-past-aspect-1,u13-i-past-aspect-2
examples=
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=u13-st-rem-past-aspect

### SESSION `u13-s-tense-keep`

- Objectives: obj-u13-tense-keep
- Sessions: u13-s-tense-keep
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 3efc63367cbed6712374d7779782d6eab164f28b8a857911e20c113950aa36f2

title=Choose the admitted report label
estimatedMinutes=12
objectives=obj-u13-tense-keep
steps=u13-st-teach-tense-keep-1,u13-st-guided-tense-keep-1,u13-st-teach-tense-keep-2,u13-st-teach-tense-keep-3,u13-st-rem-tense-keep,u13-st-check-tense-keep

### STEP `u13-st-teach-tense-keep-1`

- Objectives: obj-u13-tense-keep
- Sessions: u13-s-tense-keep
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 578ade54e53fc635ea80782f3cdb85f97f4449feba0a5728e31e7fc5e54ece91

kind=TEACH
title=Admitted lines
body=Он сказа́л, что он англича́нин — we keep the tense the same. Я скажу ему ве́чером — I will tell him in the evening. 
dominantAction=Continue
references=ref-u1115-attribution
items=
examples=ex-u13-tense-keep-0,ex-u13-tense-keep-1
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u13-st-guided-tense-keep-1`

- Objectives: obj-u13-tense-keep
- Sessions: u13-s-tense-keep
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 4d73c964d2f65868f0ce9ca24197a113825dbb86cee2080bc2df9bc72b7f6270

kind=GUIDED
title=Guided practice
body=Choose the admitted record. The feedback states the frozen field.
dominantAction=Continue
references=
items=u13-g-tense-keep-0
examples=
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=u13-st-rem-tense-keep

### STEP `u13-st-teach-tense-keep-2`

- Objectives: obj-u13-tense-keep
- Sessions: u13-s-tense-keep
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: db1b1bdf06ee9f1920ab664aeb7e21a8e0b387173882fef6bdb8db2882c9a809

kind=TEACH
title=Admitted instruction
body=In English, when reporting a story, we usually change the tense of what was said: compare "I ''am'' English" with "He said he ''was'' English" - the reported verb changes tense. In Russian, however, we keep the tense the same, so the same report is literally, "He said he '''is''' English" - "'''Он сказа́л, что он англича́нин'''", not "'''Он сказа́л, что он ''был'' англича́нином'''".
dominantAction=Continue
references=ref-u1115-attribution
items=
examples=
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u13-st-teach-tense-keep-3`

- Objectives: obj-u13-tense-keep
- Sessions: u13-s-tense-keep
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 71340b94a8e6654d91fb387e0e42e35e9362548de8b0a6239c699e4f7dd6fad3

kind=TEACH
title=Admitted lines
body=Она́ сказа́ла, что она́ ска́жет ему́ ве́чером — She said that she would speak to him in the evening. 
dominantAction=Continue
references=ref-u1115-attribution
items=
examples=ex-u13-tense-keep-2
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u13-st-rem-tense-keep`

- Objectives: obj-u13-tense-keep
- Sessions: u13-s-tense-keep
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 5946479a55831a98e99354c7decaec231784254542559920853e56b680712d25

kind=REMEDIATE
title=Remediation
body=In English, when reporting a story, we usually change the tense of what was said: compare "I ''am'' English" with "He said he ''was'' English" - the reported verb changes tense. In Russian, however, we keep the tense the same, so the same report is literally, "He said he '''is''' English" - "'''Он сказа́л, что он англича́нин'''", not "'''Он сказа́л, что он ''был'' англича́нином'''".
dominantAction=Continue
references=
items=
examples=ex-u13-tense-keep-0,ex-u13-tense-keep-1
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u13-st-check-tense-keep`

- Objectives: obj-u13-tense-keep
- Sessions: u13-s-tense-keep
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: bfc23f2d86f00bd5e4665109c523efb898aaf075561ae2cdf80f558a5bbd37e4

kind=CHECK
title=Independent checks
body=These checks use only admitted records. A miss returns to the explanation.
dominantAction=Continue
references=
items=u13-i-tense-keep-1,u13-i-tense-keep-2
examples=
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=u13-st-rem-tense-keep

### SESSION `u13-s-infinitive`

- Objectives: obj-u13-infinitive
- Sessions: u13-s-infinitive
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 71526c6591d087f1ffcc9df0834a537141c1fe97be89568f3a3e797e378ac47e

title=Choose the admitted infinitive partner
estimatedMinutes=12
objectives=obj-u13-infinitive
steps=u13-st-teach-infinitive-1,u13-st-guided-infinitive-1,u13-st-teach-infinitive-2,u13-st-teach-infinitive-3,u13-st-rem-infinitive,u13-st-check-infinitive

### STEP `u13-st-teach-infinitive-1`

- Objectives: obj-u13-infinitive
- Sessions: u13-s-infinitive
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: db895034aec451cbcb0c18d6e72f0edbad24ef2be41ec5e46e67e08afcca423c

kind=TEACH
title=Admitted lines
body=говори́ть — to speak. сказа́ть — to speak. 
dominantAction=Continue
references=ref-u1115-attribution
items=
examples=ex-u13-infinitive-0,ex-u13-infinitive-1
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u13-st-guided-infinitive-1`

- Objectives: obj-u13-infinitive
- Sessions: u13-s-infinitive
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 3465774cf85e6d8908805f7118883de81a1243df9ca542bea68eb17648f2f5d4

kind=GUIDED
title=Guided practice
body=Choose the admitted record. The feedback states the frozen field.
dominantAction=Continue
references=
items=u13-g-infinitive-0
examples=
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=u13-st-rem-infinitive

### STEP `u13-st-teach-infinitive-2`

- Objectives: obj-u13-infinitive
- Sessions: u13-s-infinitive
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 85bea88cc8c358bd907e8e57d1b0ae337b941c23d7496dd3d1a7a572284f0df6

kind=TEACH
title=Admitted instruction
body=Let us take the verbs "to speak" (imperfective: '''говори́ть''', perfective: '''сказа́ть''') and "to do" (imper.: '''де́лать''', per.: '''сде́лать''').
dominantAction=Continue
references=ref-u1115-attribution
items=
examples=
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u13-st-teach-infinitive-3`

- Objectives: obj-u13-infinitive
- Sessions: u13-s-infinitive
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 8dbf861578456c3cf1393f601ecfad726a0a867690672bf1299854d8080cec11

kind=TEACH
title=Admitted lines
body=де́лать — to do. сде́лать — to do. 
dominantAction=Continue
references=ref-u1115-attribution
items=
examples=ex-u13-infinitive-2,ex-u13-infinitive-3
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u13-st-rem-infinitive`

- Objectives: obj-u13-infinitive
- Sessions: u13-s-infinitive
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: eb75ba12c88a630eeb6aab973d563b8267980a531e3a52738c3b9747f5c00dc7

kind=REMEDIATE
title=Remediation
body=Let us take the verbs "to speak" (imperfective: '''говори́ть''', perfective: '''сказа́ть''') and "to do" (imper.: '''де́лать''', per.: '''сде́лать''').
dominantAction=Continue
references=
items=
examples=ex-u13-infinitive-0,ex-u13-infinitive-1
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u13-st-check-infinitive`

- Objectives: obj-u13-infinitive
- Sessions: u13-s-infinitive
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 1994c4b1a9e8023b1840b1cf29180887ef1b117a9b185a406b85765f512c5b35

kind=CHECK
title=Independent checks
body=These checks use only admitted records. A miss returns to the explanation.
dominantAction=Continue
references=
items=u13-i-infinitive-1,u13-i-infinitive-2
examples=
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=u13-st-rem-infinitive

### SESSION `u13-s-reported`

- Objectives: obj-u13-reported
- Sessions: u13-s-reported
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 3e17eb9fd215b443543ba7842522ed0db03c976119e5bff5343c4883dd73849c

title=Choose the admitted reported line
estimatedMinutes=12
objectives=obj-u13-reported
steps=u13-st-teach-reported-1,u13-st-guided-reported-1,u13-st-teach-reported-2,u13-st-teach-reported-3,u13-st-rem-reported,u13-st-check-reported

### STEP `u13-st-teach-reported-1`

- Objectives: obj-u13-reported
- Sessions: u13-s-reported
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: fc37bb25a7aef8e593c6b62e27983391a2c0217c637b800cda7b7c2990e8b2ce

kind=TEACH
title=Admitted lines
body=Она́ сказа́ла, что она́ ска́жет ему́ ве́чером — She said that she would speak to him in the evening. Я скажу ему ве́чером — I will tell him in the evening. 
dominantAction=Continue
references=ref-u1115-attribution
items=
examples=ex-u13-reported-0,ex-u13-reported-1
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u13-st-guided-reported-1`

- Objectives: obj-u13-reported
- Sessions: u13-s-reported
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 91eda681ff1064ae2644a5798a3f90e56bb25b9bcd6d48c85514acb8e47baf1d

kind=GUIDED
title=Guided practice
body=Choose the admitted record. The feedback states the frozen field.
dominantAction=Continue
references=
items=u13-g-reported-0
examples=
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=u13-st-rem-reported

### STEP `u13-st-teach-reported-2`

- Objectives: obj-u13-reported
- Sessions: u13-s-reported
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 5b3754ff5340f7c93bdcbdaf638b23689d2bba99fa2959644629695644dab777

kind=TEACH
title=Admitted instruction
body=:I will tell him in the evening - '''Я скажу ему ве́чером'''
:She said that she would speak to him in the evening - '''Она́ сказа́ла, что она́ ска́жет ему́ ве́чером''' - ''perfective past and future as actions completed, definite, etc.''
:When I'm in Moscow, I'll speak only in Russian - '''Когда́ я бу́ду в Москве́, я бу́ду говори́ть то́лько по-ру́сски'''
dominantAction=Continue
references=ref-u1115-attribution
items=
examples=
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u13-st-teach-reported-3`

- Objectives: obj-u13-reported
- Sessions: u13-s-reported
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 4fd4174fae7901680ee1aceb82744e3c7c98b80bff53a1f259b3f79aa3629228

kind=TEACH
title=Admitted lines
body=Он сказа́л, что он англича́нин — we keep the tense the same. Он сказа́л, что он ''был'' англича́нином — был. 
dominantAction=Continue
references=ref-u1115-attribution
items=
examples=ex-u13-reported-2,ex-u13-reported-3
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u13-st-rem-reported`

- Objectives: obj-u13-reported
- Sessions: u13-s-reported
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 00730260ed587db179abde9c803c1c135d00387a1eb5c778fb709e7c1b839e1a

kind=REMEDIATE
title=Remediation
body=:I will tell him in the evening - '''Я скажу ему ве́чером'''
:She said that she would speak to him in the evening - '''Она́ сказа́ла, что она́ ска́жет ему́ ве́чером''' - ''perfective past and future as actions completed, definite, etc.''
:When I'm in Moscow, I'll speak only in Russian - '''Когда́ я бу́ду в Москве́, я бу́ду говори́ть то́лько по-ру́сски'''
dominantAction=Continue
references=
items=
examples=ex-u13-reported-0,ex-u13-reported-1
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u13-st-check-reported`

- Objectives: obj-u13-reported
- Sessions: u13-s-reported
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 09583287d54fe421fa6a8e53cc27ebe682c6ef1e69578dd69731ca5a548bc9cb

kind=CHECK
title=Independent checks
body=These checks use only admitted records. A miss returns to the explanation.
dominantAction=Continue
references=
items=u13-i-reported-1,u13-i-reported-2
examples=
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=u13-st-rem-reported

### SESSION `u13-s-book`

- Objectives: obj-u13-book
- Sessions: u13-s-book
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 441557df603f8e3263dc0b79d938a90b6fbfec9f4a0b75564041b901864022e6

title=Self-attest Lesson 13
estimatedMinutes=10
objectives=obj-u13-book
steps=u13-st-book-work,u13-st-book

### STEP `u13-st-book-work`

- Objectives: obj-u13-book
- Sessions: u13-s-book
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 6dbdd921306eccab365907aa780aa79fe0885fb294083540a9885f7ce9c4b8f7

kind=BOOK
title=Work Lesson 13 in your copy
body=Do Lesson 13 in the copy you own. Use that book's explanations and practice. The proposed records in this unit are a separate licensed set and do not copy the book. Listening stays with the book. No audio is mapped here.
dominantAction=Continue
references=ref-u1115-attribution
items=
examples=
unavailableReason=
bookDirection=Open Lesson 13 in your copy and complete its lesson work.
companionDirection=
remediationStepId=

### STEP `u13-st-book`

- Objectives: obj-u13-book
- Sessions: u13-s-book
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 2bcf1039711c604c414b2e0619f695ac8fb003b234cb79ce499387b63f0b851f

kind=BOOK
title=Record Lesson 13 book work
body=When you have done Lesson 13 in your copy, self-attest here. Attestation records your report. It does not grade the lesson, approve a mapping, or approve the proposed records.
dominantAction=Continue
references=
items=
examples=
unavailableReason=
bookDirection=Self-attest only after the Lesson 13 work in your copy.
companionDirection=
remediationStepId=

### EXAMPLE `ex-u13-past-aspect-0`

- Objectives: obj-u13-past-aspect
- Sessions: u13-s-past-aspect
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 82f6d665a0adb45e34bab2b343a89a5c8b9ccf32a13541b07eddca5fda88f149

kind=LEXICAL
letterId=
display=я читал
note=I was reading
objectiveId=obj-u13-past-aspect
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-u13-past-aspect-1`

- Objectives: obj-u13-past-aspect
- Sessions: u13-s-past-aspect
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: a466c7b6d0b016c88616155bc892161225bae3a5e41ae8bae2d87683480b41ba

kind=LEXICAL
letterId=
display=я прочитал
note=I read
objectiveId=obj-u13-past-aspect
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-or-u13-past-aspect-2`

- Objectives: obj-u13-past-aspect
- Sessions: u13-s-past-aspect
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 9197485ee10b6b1fec0f2e8d7d9351a43891da4d512bc1dd2a042b4e8dd6910e

kind=LEXICAL
letterId=
display=чита́ть
note=read
objectiveId=obj-u13-past-aspect
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-or-u13-past-aspect-3`

- Objectives: obj-u13-past-aspect
- Sessions: u13-s-past-aspect
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: a6a735abc8d6cca7740af825dfee93664d5decca9c420729ee97b84d2b895ac3

kind=LEXICAL
letterId=
display=чита́л
note=read
objectiveId=obj-u13-past-aspect
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-or-u13-past-aspect-4`

- Objectives: obj-u13-past-aspect
- Sessions: u13-s-past-aspect
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: bb53d0f551cbba0925ff406d43acda937037e42074503037eb611d846917e37e

kind=LEXICAL
letterId=
display=чита́ла
note=read
objectiveId=obj-u13-past-aspect
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-u13-tense-keep-0`

- Objectives: obj-u13-tense-keep
- Sessions: u13-s-tense-keep
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 09a25fedfdd8e090646cf468c3e543ff6c11d38ae4f30415ac46454fa119cdca

kind=LEXICAL
letterId=
display=Он сказа́л, что он англича́нин
note=we keep the tense the same
objectiveId=obj-u13-tense-keep
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-u13-tense-keep-1`

- Objectives: obj-u13-tense-keep
- Sessions: u13-s-tense-keep
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 04118d2b94744ad85d76c595eb0f0d1ee921af0ddc900e5efce5f97c4783360b

kind=LEXICAL
letterId=
display=Я скажу ему ве́чером
note=I will tell him in the evening
objectiveId=obj-u13-tense-keep
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-u13-tense-keep-2`

- Objectives: obj-u13-tense-keep
- Sessions: u13-s-tense-keep
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 1b6c11be9a3e0459b394bed4d4d0623ae6e5967f62a61d79a6a2433f837f5637

kind=LEXICAL
letterId=
display=Она́ сказа́ла, что она́ ска́жет ему́ ве́чером
note=She said that she would speak to him in the evening
objectiveId=obj-u13-tense-keep
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-u13-infinitive-0`

- Objectives: obj-u13-infinitive
- Sessions: u13-s-infinitive
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 4b2941a8518b0936bc58a46c8a7b0e61bd42364e95c5f5bcfae3d2d749c0f81c

kind=LEXICAL
letterId=
display=говори́ть
note=to speak
objectiveId=obj-u13-infinitive
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-u13-infinitive-1`

- Objectives: obj-u13-infinitive
- Sessions: u13-s-infinitive
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 78ef20f900ec95a8ce74d00826196b7ddce31503f8bbfe3711357c267d81c1b7

kind=LEXICAL
letterId=
display=сказа́ть
note=to speak
objectiveId=obj-u13-infinitive
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-u13-infinitive-2`

- Objectives: obj-u13-infinitive
- Sessions: u13-s-infinitive
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: efcc806f5c18153dc455fee09e9e1142ab8c9d7fc68e03956ae9b05f463926a4

kind=LEXICAL
letterId=
display=де́лать
note=to do
objectiveId=obj-u13-infinitive
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-u13-infinitive-3`

- Objectives: obj-u13-infinitive
- Sessions: u13-s-infinitive
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: dba1909a435b5d2061a86a5c46d91b19b07d592a3818d788fa966134db392584

kind=LEXICAL
letterId=
display=сде́лать
note=to do
objectiveId=obj-u13-infinitive
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-u13-reported-0`

- Objectives: obj-u13-reported
- Sessions: u13-s-reported
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 071c929514733ee50416fd69a46aee8e6c991e6ddbee3467b435c1e6c185223f

kind=LEXICAL
letterId=
display=Она́ сказа́ла, что она́ ска́жет ему́ ве́чером
note=She said that she would speak to him in the evening
objectiveId=obj-u13-reported
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-u13-reported-1`

- Objectives: obj-u13-reported
- Sessions: u13-s-reported
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 8b9eec8c1aeca59d24157715a8ec4b8e88a45fba83f1e122d432ed085543b61d

kind=LEXICAL
letterId=
display=Я скажу ему ве́чером
note=I will tell him in the evening
objectiveId=obj-u13-reported
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-u13-reported-2`

- Objectives: obj-u13-reported
- Sessions: u13-s-reported
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 6abda5d25184fab39639a925365a32e8227af45bb8cb43e7523688aff9a9ee30

kind=LEXICAL
letterId=
display=Он сказа́л, что он англича́нин
note=we keep the tense the same
objectiveId=obj-u13-reported
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-u13-reported-3`

- Objectives: obj-u13-reported
- Sessions: u13-s-reported
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 0c2a5a6df61ef63349efb80a6c30f0af06b80344086d9adfbcd0a3f00acc2b80

kind=LEXICAL
letterId=
display=Он сказа́л, что он ''был'' англича́нином
note=был
objectiveId=obj-u13-reported
reviewStatus=PENDING_QUALIFIED_REVIEW

### GUIDED_PRACTICE `u13-g-past-aspect-0`

- Objectives: obj-u13-past-aspect
- Sessions: u13-s-past-aspect
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 1fab11c7d3b600ac4043cdddf9e7ff045af0860a22ed4268424091d6eedd3e99

kind=CONTRAST_SELECTION
language=en
prompt=Which admitted line is the perfective past?
promptLetter=I read
options=я прочитал | я читал
answer=я прочитал
feedback=Does this answer show the perfective past? я прочитал.
objectiveId=obj-u13-past-aspect
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW
distractorRationales= | Does this row fail the perfective past?
testedFeature=PERFECTIVE_PAST
evidenceMode=SOURCE_SUPPLIED_PAIR
contrastSetId=u13-past-aspect-0
cueLanguage=rule
independent=false
answerSourceIds=prov-ex-u13-past-aspect-1-ru
distractorSourceIds= | prov-ex-u13-past-aspect-0-ru
distractorDifference= | Does this row fail the perfective past?
whyAnswerDemonstratesObjective=Does this answer show the perfective past?
contrastMembers=я прочитал | я читал
relationKind=SOURCE_SUPPLIED_PAIR
relationParticipantIds=prov-ex-u13-past-aspect-1-en | prov-ex-u13-past-aspect-1-ru | prov-ex-u13-past-aspect-0-ru

### GUIDED_PRACTICE `u13-g-tense-keep-0`

- Objectives: obj-u13-tense-keep
- Sessions: u13-s-tense-keep
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 7a9cb767ca7dbabec488a5229a75c6b995229374a6b22cb09f5c29da3910b392

kind=PATTERN_CLASSIFICATION
language=en
prompt=Which admitted label keeps the same tense?
promptLetter=Он сказа́л, что он англича́нин
options=we keep the tense the same | we usually change the tense
answer=we keep the tense the same
feedback=Does this answer show the same tense? we keep the tense the same.
objectiveId=obj-u13-tense-keep
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW
distractorRationales= | Does this row fail the same tense?
testedFeature=TENSE_KEPT
evidenceMode=RULE_CLASSIFIES_SOURCE_EXAMPLE
contrastSetId=u13-tense-keep-0
cueLanguage=ru
independent=false
answerSourceIds=prov-ex-u13-tense-keep-0-en
distractorSourceIds= | prov-u13-tense-keep-x-1
distractorDifference= | Does this row fail the same tense?
whyAnswerDemonstratesObjective=Does this answer show the same tense?
contrastMembers=we keep the tense the same | we usually change the tense
relationKind=RULE_CLASSIFIES_SOURCE_EXAMPLE
relationParticipantIds=prov-ex-u13-tense-keep-0-ru | prov-ex-u13-tense-keep-0-en | prov-u13-tense-keep-x-1

### GUIDED_PRACTICE `u13-g-infinitive-0`

- Objectives: obj-u13-infinitive
- Sessions: u13-s-infinitive
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 6c587010309f28d805a590be3b34b00f52a875a6d8e07e301dc3de8fff84b541

kind=CONTRAST_SELECTION
language=en
prompt=Which admitted line is the perfective partner?
promptLetter=говори́ть
options=сказа́ть | де́лать
answer=сказа́ть
feedback=Does this answer show the perfective partner? сказа́ть.
objectiveId=obj-u13-infinitive
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW
distractorRationales= | Does this row fail the perfective partner?
testedFeature=PERFECTIVE_PARTNER
evidenceMode=SOURCE_SUPPLIED_PAIR
contrastSetId=u13-infinitive-0
cueLanguage=ru
independent=false
answerSourceIds=prov-ex-u13-infinitive-1-ru
distractorSourceIds= | prov-ex-u13-infinitive-2-ru
distractorDifference= | Does this row fail the perfective partner?
whyAnswerDemonstratesObjective=Does this answer show the perfective partner?
contrastMembers=сказа́ть | де́лать
relationKind=SOURCE_SUPPLIED_PAIR
relationParticipantIds=prov-ex-u13-infinitive-0-ru | prov-ex-u13-infinitive-1-ru | prov-ex-u13-infinitive-2-ru

### GUIDED_PRACTICE `u13-g-reported-0`

- Objectives: obj-u13-reported
- Sessions: u13-s-reported
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 389e9368f8dfcbe2915a06106122faaebe7313a245948bf64aa6c007954c8075

kind=CONTRAST_SELECTION
language=en
prompt=Which admitted line is the reported evening?
promptLetter=Я скажу ему ве́чером
options=Она́ сказа́ла, что она́ ска́жет ему́ ве́чером | Когда́ я бу́ду в Москве́, я бу́ду говори́ть то́лько по-ру́сски
answer=Она́ сказа́ла, что она́ ска́жет ему́ ве́чером
feedback=Does this answer show the reported evening? Она́ сказа́ла, что она́ ска́жет ему́ ве́чером.
objectiveId=obj-u13-reported
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW
distractorRationales= | Does this row fail the reported evening?
testedFeature=REPORTED_EVENING
evidenceMode=SOURCE_SUPPLIED_PAIR
contrastSetId=u13-reported-0
cueLanguage=ru
independent=false
answerSourceIds=prov-ex-u13-reported-0-ru
distractorSourceIds= | prov-u13-reported-x-1
distractorDifference= | Does this row fail the reported evening?
whyAnswerDemonstratesObjective=Does this answer show the reported evening?
contrastMembers=Она́ сказа́ла, что она́ ска́жет ему́ ве́чером | Когда́ я бу́ду в Москве́, я бу́ду говори́ть то́лько по-ру́сски
relationKind=SOURCE_SUPPLIED_PAIR
relationParticipantIds=prov-ex-u13-reported-1-ru | prov-ex-u13-reported-0-ru | prov-u13-reported-x-1

### INDEPENDENT_CHECK `u13-i-past-aspect-1`

- Objectives: obj-u13-past-aspect
- Sessions: u13-s-past-aspect
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 10ef2aa5fe2a8ce3718ef976a424bdcb193ab3f60126582024c379512fd74411

kind=CONTRAST_SELECTION
language=en
prompt=Which admitted line is the imperfective past?
promptLetter=I was reading
options=я читал | я прочитал
answer=я читал
feedback=Does this answer show the imperfective past? я читал.
objectiveId=obj-u13-past-aspect
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW
distractorRationales= | Does this row fail the imperfective past?
testedFeature=IMPERFECTIVE_PAST
evidenceMode=SOURCE_SUPPLIED_PAIR
contrastSetId=u13-past-aspect-1
cueLanguage=rule
independent=true
answerSourceIds=prov-ex-u13-past-aspect-0-ru
distractorSourceIds= | prov-ex-u13-past-aspect-1-ru
distractorDifference= | Does this row fail the imperfective past?
whyAnswerDemonstratesObjective=Does this answer show the imperfective past?
contrastMembers=я читал | я прочитал
relationKind=SOURCE_SUPPLIED_PAIR
relationParticipantIds=prov-ex-u13-past-aspect-0-en | prov-ex-u13-past-aspect-0-ru | prov-ex-u13-past-aspect-1-ru

### INDEPENDENT_CHECK `u13-i-past-aspect-2`

- Objectives: obj-u13-past-aspect
- Sessions: u13-s-past-aspect
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 7bf0388cd75c80ddc977333d4b754db43dbf3a91ea440f1b1948f2b5820c0352

kind=CONTRAST_SELECTION
language=en
prompt=Which admitted row is the masculine past?
promptLetter=чита́ть
options=чита́л | чита́ла
answer=чита́л
feedback=Does this answer show the masculine past? чита́л.
objectiveId=obj-u13-past-aspect
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW
distractorRationales= | Does this row fail the masculine past?
testedFeature=MASCULINE_PAST
evidenceMode=SAME_LEMMA_DIFFERENT_FIELD
contrastSetId=u13-past-aspect-2
cueLanguage=ru
independent=true
answerSourceIds=prov-ex-or-u13-past-aspect-3
distractorSourceIds= | prov-ex-or-u13-past-aspect-4
distractorDifference= | Does this row fail the masculine past?
whyAnswerDemonstratesObjective=Does this answer show the masculine past?
contrastMembers=чита́л | чита́ла
relationKind=SAME_LEMMA_DIFFERENT_FIELD
relationParticipantIds=prov-ex-or-u13-past-aspect-2 | prov-ex-or-u13-past-aspect-3 | prov-ex-or-u13-past-aspect-4

### INDEPENDENT_CHECK `u13-i-tense-keep-1`

- Objectives: obj-u13-tense-keep
- Sessions: u13-s-tense-keep
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 3ef25149d1c9ab6988615a9f41255f1eccd04ed1ede31c8177ffaf95f4e7967e

kind=PATTERN_CLASSIFICATION
language=en
prompt=Which admitted line is the reported evening?
promptLetter=Я скажу ему ве́чером
options=Она́ сказа́ла, что она́ ска́жет ему́ ве́чером | Когда́ я бу́ду в Москве́, я бу́ду говори́ть то́лько по-ру́сски
answer=Она́ сказа́ла, что она́ ска́жет ему́ ве́чером
feedback=Does this answer show the reported evening? Она́ сказа́ла, что она́ ска́жет ему́ ве́чером.
objectiveId=obj-u13-tense-keep
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW
distractorRationales= | Does this row fail the reported evening?
testedFeature=REPORTED_EVENING
evidenceMode=SOURCE_SUPPLIED_PAIR
contrastSetId=u13-tense-keep-1
cueLanguage=ru
independent=true
answerSourceIds=prov-ex-u13-tense-keep-2-ru
distractorSourceIds= | prov-u13-tense-keep-x-3
distractorDifference= | Does this row fail the reported evening?
whyAnswerDemonstratesObjective=Does this answer show the reported evening?
contrastMembers=Она́ сказа́ла, что она́ ска́жет ему́ ве́чером | Когда́ я бу́ду в Москве́, я бу́ду говори́ть то́лько по-ру́сски
relationKind=SOURCE_SUPPLIED_PAIR
relationParticipantIds=prov-ex-u13-tense-keep-1-ru | prov-ex-u13-tense-keep-2-ru | prov-u13-tense-keep-x-3

### INDEPENDENT_CHECK `u13-i-tense-keep-2`

- Objectives: obj-u13-tense-keep
- Sessions: u13-s-tense-keep
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 38409e2c951ee69bbf378eb6356541d8e7dd13cf1cf7d2ef808c32dabdc8023f

kind=PATTERN_CLASSIFICATION
language=en
prompt=Which admitted label is about changing the reported tense?
promptLetter=the reported verb changes tense
options=we usually change the tense | we keep the tense the same
answer=we usually change the tense
feedback=Does this answer show the changing the reported tense? we usually change the tense.
objectiveId=obj-u13-tense-keep
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW
distractorRationales= | Does this row fail the changing the reported tense?
testedFeature=ENGLISH_TENSE_CHANGE
evidenceMode=SOURCE_SUPPLIED_PAIR
contrastSetId=u13-tense-keep-2
cueLanguage=rule
independent=true
answerSourceIds=prov-u13-tense-keep-x-1
distractorSourceIds= | prov-ex-u13-tense-keep-0-en
distractorDifference= | Does this row fail the changing the reported tense?
whyAnswerDemonstratesObjective=Does this answer show the changing the reported tense?
contrastMembers=we usually change the tense | we keep the tense the same
relationKind=SOURCE_SUPPLIED_PAIR
relationParticipantIds=prov-u13-tense-keep-x-2 | prov-u13-tense-keep-x-1 | prov-ex-u13-tense-keep-0-en

### INDEPENDENT_CHECK `u13-i-infinitive-1`

- Objectives: obj-u13-infinitive
- Sessions: u13-s-infinitive
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 2116aad7703e1771d0de70e0bacede56af0f90e009920836b9f810eac42cf548

kind=CONTRAST_SELECTION
language=en
prompt=Which admitted line is the perfective partner?
promptLetter=де́лать
options=сде́лать | говори́ть
answer=сде́лать
feedback=Does this answer show the perfective partner? сде́лать.
objectiveId=obj-u13-infinitive
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW
distractorRationales= | Does this row fail the perfective partner?
testedFeature=PERFECTIVE_PARTNER
evidenceMode=SOURCE_SUPPLIED_PAIR
contrastSetId=u13-infinitive-1
cueLanguage=ru
independent=true
answerSourceIds=prov-ex-u13-infinitive-3-ru
distractorSourceIds= | prov-ex-u13-infinitive-0-ru
distractorDifference= | Does this row fail the perfective partner?
whyAnswerDemonstratesObjective=Does this answer show the perfective partner?
contrastMembers=сде́лать | говори́ть
relationKind=SOURCE_SUPPLIED_PAIR
relationParticipantIds=prov-ex-u13-infinitive-2-ru | prov-ex-u13-infinitive-3-ru | prov-ex-u13-infinitive-0-ru

### INDEPENDENT_CHECK `u13-i-infinitive-2`

- Objectives: obj-u13-infinitive
- Sessions: u13-s-infinitive
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: ccf8cd54b46ebc77e996d24737fab11334c67e1d01b75d9365c20dc2f0077d68

kind=CONTRAST_SELECTION
language=en
prompt=Which admitted line is the imperfective partner?
promptLetter=сказа́ть
options=говори́ть | сде́лать
answer=говори́ть
feedback=Does this answer show the imperfective partner? говори́ть.
objectiveId=obj-u13-infinitive
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW
distractorRationales= | Does this row fail the imperfective partner?
testedFeature=IMPERFECTIVE_PARTNER
evidenceMode=SOURCE_SUPPLIED_PAIR
contrastSetId=u13-infinitive-2
cueLanguage=ru
independent=true
answerSourceIds=prov-ex-u13-infinitive-0-ru
distractorSourceIds= | prov-ex-u13-infinitive-3-ru
distractorDifference= | Does this row fail the imperfective partner?
whyAnswerDemonstratesObjective=Does this answer show the imperfective partner?
contrastMembers=говори́ть | сде́лать
relationKind=SOURCE_SUPPLIED_PAIR
relationParticipantIds=prov-ex-u13-infinitive-1-ru | prov-ex-u13-infinitive-0-ru | prov-ex-u13-infinitive-3-ru

### INDEPENDENT_CHECK `u13-i-reported-1`

- Objectives: obj-u13-reported
- Sessions: u13-s-reported
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 17c488996d2cec2e9655c70ef6ece25df2f5de262fd615e83ee04badc1123f84

kind=CONTRAST_SELECTION
language=en
prompt=Which admitted line is the direct evening?
promptLetter=Она́ сказа́ла, что она́ ска́жет ему́ ве́чером
options=Я скажу ему ве́чером | Когда́ я бу́ду в Москве́, я бу́ду говори́ть то́лько по-ру́сски
answer=Я скажу ему ве́чером
feedback=Does this answer show the direct evening? Я скажу ему ве́чером.
objectiveId=obj-u13-reported
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW
distractorRationales= | Does this row fail the direct evening?
testedFeature=DIRECT_EVENING
evidenceMode=SOURCE_SUPPLIED_PAIR
contrastSetId=u13-reported-1
cueLanguage=ru
independent=true
answerSourceIds=prov-ex-u13-reported-1-ru
distractorSourceIds= | prov-u13-reported-x-1
distractorDifference= | Does this row fail the direct evening?
whyAnswerDemonstratesObjective=Does this answer show the direct evening?
contrastMembers=Я скажу ему ве́чером | Когда́ я бу́ду в Москве́, я бу́ду говори́ть то́лько по-ру́сски
relationKind=SOURCE_SUPPLIED_PAIR
relationParticipantIds=prov-ex-u13-reported-0-ru | prov-ex-u13-reported-1-ru | prov-u13-reported-x-1

### INDEPENDENT_CHECK `u13-i-reported-2`

- Objectives: obj-u13-reported
- Sessions: u13-s-reported
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: a7a5e0c40893a2b09fa6f9644e1d931b9582cbae83874f81c2d9d1e393fcc138

kind=CONTRAST_SELECTION
language=en
prompt=Which admitted line is the future plan about Moscow?
promptLetter=Она́ сказа́ла, что она́ ска́жет ему́ ве́чером
options=Когда́ я бу́ду в Москве́, я бу́ду говори́ть то́лько по-ру́сски | Я скажу ему ве́чером
answer=Когда́ я бу́ду в Москве́, я бу́ду говори́ть то́лько по-ру́сски
feedback=Does this answer show the Moscow? Когда́ я бу́ду в Москве́, я бу́ду говори́ть то́лько по-ру́сски.
objectiveId=obj-u13-reported
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW
distractorRationales= | Does this row fail the Moscow?
testedFeature=MOSCOW_FUTURE
evidenceMode=SOURCE_SUPPLIED_PAIR
contrastSetId=u13-reported-2
cueLanguage=ru
independent=true
answerSourceIds=prov-u13-reported-x-1
distractorSourceIds= | prov-ex-u13-reported-1-ru
distractorDifference= | Does this row fail the Moscow?
whyAnswerDemonstratesObjective=Does this answer show the Moscow?
contrastMembers=Когда́ я бу́ду в Москве́, я бу́ду говори́ть то́лько по-ру́сски | Я скажу ему ве́чером
relationKind=SOURCE_SUPPLIED_PAIR
relationParticipantIds=prov-ex-u13-reported-0-ru | prov-u13-reported-x-1 | prov-ex-u13-reported-1-ru

### REFERENCE `ref-u1115-attribution`

- Objectives: none
- Sessions: u13-s-orient, u13-s-past-aspect, u13-s-tense-keep, u13-s-infinitive, u13-s-reported, u13-s-book
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: LICENSE_OR_ATTRIBUTION
- Current review status: NOT_REQUIRED
- Content hash: 4bda0b8d8e8208815ddeaa0d8a9dc7df092308df559b3aa28796e3e473a5bf3d

title=Units 11–15 source notice
summary=Proposed Units 11–15 adapt CC BY-SA 4.0 dictionary rows and Wikibooks grammar excerpts.
detail=OpenRussian dictionary data is frozen at commit 50e210c4803237779cb562bc1abcea529066031c under CC BY-SA 4.0. Wikibooks excerpts are frozen by oldid under the CC BY-SA 4.0 route. The adapted selection and arrangement are share-alike. Application code, user data, and user recordings are outside that statement. No audio is included. Learner-facing dictionary glosses are delimited sense selections. Review is pending.
classification=LICENSE_OR_ATTRIBUTION
introducedInUnit=11
searchTerms=units 11-15 source notice
reviewStatus=NOT_REQUIRED

### PROVENANCE `prov-ex-or-u13-past-aspect-2`

- Objectives: obj-u13-past-aspect
- Sessions: u13-s-orient, u13-s-past-aspect
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 02835ac4feb8ebfbc466234961f1e18849fe9699bc907e8160e49018ae368e75

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

### PROVENANCE `prov-ex-or-u13-past-aspect-2-gloss`

- Objectives: obj-u13-past-aspect
- Sessions: u13-s-orient, u13-s-past-aspect
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 503100e3f5d20473545368efbca8cc4ba71f6c42d5c9bfc8443e0b186d21fc83

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

### PROVENANCE `prov-ex-or-u13-past-aspect-3`

- Objectives: obj-u13-past-aspect
- Sessions: u13-s-orient, u13-s-past-aspect
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: aa7f5cc7da09289483edeaf4b2b0cbf42d9004c70b1d8d38a0f44b762a830bc2

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:чита'ть#past_m
originalSourceText=чита'л
shippedText=чита́л
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=d701eedd90bc4ba253cf2b6fb66dec7c7d0af8b001c5175730b45e812052568f
contentHash=75473f27c41bc680f7fbec7f2109a5804e5175dbeb46de6c8b757784ed4640f8
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-ex-or-u13-past-aspect-3-gloss`

- Objectives: obj-u13-past-aspect
- Sessions: u13-s-orient, u13-s-past-aspect
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 9c89654e8f98d174cea23e101e4f33b8d2c9214d7c57b0d3215a8a94de4ad34a

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

### PROVENANCE `prov-ex-or-u13-past-aspect-4`

- Objectives: obj-u13-past-aspect
- Sessions: u13-s-orient, u13-s-past-aspect
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: eb35b12daad760f4a1a28fe0804ffa487f6af51f051e9b3ca9b4baf0fd9c82bb

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/verbs.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=verbs.csv:чита'ть#past_f
originalSourceText=чита'ла
shippedText=чита́ла
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=37817181771c1c03c77a203be7f2344956b7fb5c66414c6e8f44c5ff8e46d4b7
contentHash=bfb5bfdb4df678f7b1eef222ec8da869a8cea3f7d718b5eab3dcfdb9a01712a0
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-ex-or-u13-past-aspect-4-gloss`

- Objectives: obj-u13-past-aspect
- Sessions: u13-s-orient, u13-s-past-aspect
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: fc4b1caaad7f22f24a53ab7ebb5d02ecc03f8f5b73f9c48411f58237c68c6c35

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

### PROVENANCE `prov-ex-u13-infinitive-0-en`

- Objectives: obj-u13-infinitive
- Sessions: u13-s-orient, u13-s-infinitive
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: f6a4c170f7e364a4ae0740935033e0c95921574e9bc639b68adf769fe9f36586

recordType=meaning
sourceUrl=https://en.wikibooks.org/wiki/Russian/Verbal_Aspect?oldid=4059032
sourceRevision=4059032
sourceRecordId=aspect:Let us take the verbs "to speak" (imperfective: '''говори́ть''', perfective: '''
originalSourceText=Let us take the verbs "to speak" (imperfective: '''говори́ть''', perfective: '''сказа́ть''') and "to do" (imper.: '''де́лать''', per.: '''сде́лать''').
shippedText=to speak
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=fddb8c4118a7145fbebc56cb52193b4c7d4bbe413a8f87cafc04e0e6a1a812d8
contentHash=4ca762c41972c065576195229be6704587e0aacec753eebbaa8e26b288a97741
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-ex-u13-infinitive-0-ru`

- Objectives: obj-u13-infinitive
- Sessions: u13-s-orient, u13-s-infinitive
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: a2870789e3965be0e25a88b17de9ec66692e0081dfcb88a65b079d12c58775cb

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Verbal_Aspect?oldid=4059032
sourceRevision=4059032
sourceRecordId=aspect:Let us take the verbs "to speak" (imperfective: '''говори́ть''', perfective: '''
originalSourceText=Let us take the verbs "to speak" (imperfective: '''говори́ть''', perfective: '''сказа́ть''') and "to do" (imper.: '''де́лать''', per.: '''сде́лать''').
shippedText=говори́ть
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=fddb8c4118a7145fbebc56cb52193b4c7d4bbe413a8f87cafc04e0e6a1a812d8
contentHash=1f37b6b2cea84a10b3a4fb1a11f4d155460f649d86b38cd375ea5ee336fcd5e4
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-ex-u13-infinitive-1-en`

- Objectives: obj-u13-infinitive
- Sessions: u13-s-orient, u13-s-infinitive
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 08688c7ea40e78fbe11ea127f85783fccafa07c578f55a337c5001fbf3cd525c

recordType=meaning
sourceUrl=https://en.wikibooks.org/wiki/Russian/Verbal_Aspect?oldid=4059032
sourceRevision=4059032
sourceRecordId=aspect:Let us take the verbs "to speak" (imperfective: '''говори́ть''', perfective: '''
originalSourceText=Let us take the verbs "to speak" (imperfective: '''говори́ть''', perfective: '''сказа́ть''') and "to do" (imper.: '''де́лать''', per.: '''сде́лать''').
shippedText=to speak
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=fddb8c4118a7145fbebc56cb52193b4c7d4bbe413a8f87cafc04e0e6a1a812d8
contentHash=4ca762c41972c065576195229be6704587e0aacec753eebbaa8e26b288a97741
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-ex-u13-infinitive-1-ru`

- Objectives: obj-u13-infinitive
- Sessions: u13-s-orient, u13-s-infinitive
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 8e526291dd666c5a32a2c2ed187e9663547018408c4787f6910f7e48918bce98

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Verbal_Aspect?oldid=4059032
sourceRevision=4059032
sourceRecordId=aspect:Let us take the verbs "to speak" (imperfective: '''говори́ть''', perfective: '''
originalSourceText=Let us take the verbs "to speak" (imperfective: '''говори́ть''', perfective: '''сказа́ть''') and "to do" (imper.: '''де́лать''', per.: '''сде́лать''').
shippedText=сказа́ть
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=fddb8c4118a7145fbebc56cb52193b4c7d4bbe413a8f87cafc04e0e6a1a812d8
contentHash=5547b4749450ac775ed6300c8589dd3d617007a61025ac2ed585dba015b692a8
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-ex-u13-infinitive-2-en`

- Objectives: obj-u13-infinitive
- Sessions: u13-s-orient, u13-s-infinitive
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 14ffb6a7738390c7b8229fbe39fcf3ab65cc53f35c04dcd67736b2853f17ea08

recordType=meaning
sourceUrl=https://en.wikibooks.org/wiki/Russian/Verbal_Aspect?oldid=4059032
sourceRevision=4059032
sourceRecordId=aspect:Let us take the verbs "to speak" (imperfective: '''говори́ть''', perfective: '''
originalSourceText=Let us take the verbs "to speak" (imperfective: '''говори́ть''', perfective: '''сказа́ть''') and "to do" (imper.: '''де́лать''', per.: '''сде́лать''').
shippedText=to do
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=fddb8c4118a7145fbebc56cb52193b4c7d4bbe413a8f87cafc04e0e6a1a812d8
contentHash=217655f48dde4e0a45408a559183ec410abf98188b35ad563cb8c512f13d2269
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-ex-u13-infinitive-2-ru`

- Objectives: obj-u13-infinitive
- Sessions: u13-s-orient, u13-s-infinitive
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: c56b65431311f07739fed2d7adbbeb1b2a821784ae14f1afe733b0a865a62c7a

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Verbal_Aspect?oldid=4059032
sourceRevision=4059032
sourceRecordId=aspect:Let us take the verbs "to speak" (imperfective: '''говори́ть''', perfective: '''
originalSourceText=Let us take the verbs "to speak" (imperfective: '''говори́ть''', perfective: '''сказа́ть''') and "to do" (imper.: '''де́лать''', per.: '''сде́лать''').
shippedText=де́лать
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=fddb8c4118a7145fbebc56cb52193b4c7d4bbe413a8f87cafc04e0e6a1a812d8
contentHash=c472ba4cd4b95d85e2b7686abe28d3860a88b3ecb9eb6de34881b9fd9e089bbb
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-ex-u13-infinitive-3-en`

- Objectives: obj-u13-infinitive
- Sessions: u13-s-orient, u13-s-infinitive
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: d95fa67c1d552c21fbe62cedc57b4e9bdbee4e417fb5b922ecda911282f31c91

recordType=meaning
sourceUrl=https://en.wikibooks.org/wiki/Russian/Verbal_Aspect?oldid=4059032
sourceRevision=4059032
sourceRecordId=aspect:Let us take the verbs "to speak" (imperfective: '''говори́ть''', perfective: '''
originalSourceText=Let us take the verbs "to speak" (imperfective: '''говори́ть''', perfective: '''сказа́ть''') and "to do" (imper.: '''де́лать''', per.: '''сде́лать''').
shippedText=to do
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=fddb8c4118a7145fbebc56cb52193b4c7d4bbe413a8f87cafc04e0e6a1a812d8
contentHash=217655f48dde4e0a45408a559183ec410abf98188b35ad563cb8c512f13d2269
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-ex-u13-infinitive-3-ru`

- Objectives: obj-u13-infinitive
- Sessions: u13-s-orient, u13-s-infinitive
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 79abf9774b1551fb976dc47d36b62c7435a5a340745252ca147ff913d31af84f

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Verbal_Aspect?oldid=4059032
sourceRevision=4059032
sourceRecordId=aspect:Let us take the verbs "to speak" (imperfective: '''говори́ть''', perfective: '''
originalSourceText=Let us take the verbs "to speak" (imperfective: '''говори́ть''', perfective: '''сказа́ть''') and "to do" (imper.: '''де́лать''', per.: '''сде́лать''').
shippedText=сде́лать
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=fddb8c4118a7145fbebc56cb52193b4c7d4bbe413a8f87cafc04e0e6a1a812d8
contentHash=b57324378572bf8c4a9dc00f2daeecb1ed4a8f4975529115e9554a00849c3325
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-ex-u13-past-aspect-0-en`

- Objectives: obj-u13-past-aspect
- Sessions: u13-s-orient, u13-s-past-aspect
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: fc57aa16239ecff6c9053f36fe4b4fbce12722f20f1cd8556a884477b77f3896

recordType=meaning
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Past_tense?oldid=4567337
sourceRevision=4567337
sourceRecordId=past:So the English ''past progressive'' sentence  "I was reading" would correspond t
originalSourceText=So the English ''past progressive'' sentence  "I was reading" would correspond to the Russian imperfective ('''я читал'''), since it refers to an action that wasn't completed. Likewise, the English ''simple past'' sentence "I read" corresponds to the Russian perfective ('''я прочитал''')
shippedText=I was reading
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=b12fad2c641b1c5647b12762968bb5d302adaada5c7cdc620b3c153be64e5181
contentHash=c8bab909d1c42d3cf8c44173383f65b9a9c1c5185756da84a3c7eecc6a8abb8f
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-ex-u13-past-aspect-0-ru`

- Objectives: obj-u13-past-aspect
- Sessions: u13-s-orient, u13-s-past-aspect
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: e99904aa6db9f62031d3b1326607454431ece75399b11272ab7234cbbf7f5d80

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Past_tense?oldid=4567337
sourceRevision=4567337
sourceRecordId=past:So the English ''past progressive'' sentence  "I was reading" would correspond t
originalSourceText=So the English ''past progressive'' sentence  "I was reading" would correspond to the Russian imperfective ('''я читал'''), since it refers to an action that wasn't completed. Likewise, the English ''simple past'' sentence "I read" corresponds to the Russian perfective ('''я прочитал''')
shippedText=я читал
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=b12fad2c641b1c5647b12762968bb5d302adaada5c7cdc620b3c153be64e5181
contentHash=442c85192cb20fba15b79dda33e091ca8ae22a17ab03797e644de11c4b490566
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-ex-u13-past-aspect-1-en`

- Objectives: obj-u13-past-aspect
- Sessions: u13-s-orient, u13-s-past-aspect
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 6b5b27a1a4ad3dac90d2074879feccdb880226e7c1e5c931cfc45e11f39edf34

recordType=meaning
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Past_tense?oldid=4567337
sourceRevision=4567337
sourceRecordId=past:So the English ''past progressive'' sentence  "I was reading" would correspond t
originalSourceText=So the English ''past progressive'' sentence  "I was reading" would correspond to the Russian imperfective ('''я читал'''), since it refers to an action that wasn't completed. Likewise, the English ''simple past'' sentence "I read" corresponds to the Russian perfective ('''я прочитал''')
shippedText=I read
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=b12fad2c641b1c5647b12762968bb5d302adaada5c7cdc620b3c153be64e5181
contentHash=5e0d737af507e7533d4e288ecce80627dc9ed90656ed2e36e15ead74bdcedb0b
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-ex-u13-past-aspect-1-ru`

- Objectives: obj-u13-past-aspect
- Sessions: u13-s-orient, u13-s-past-aspect
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: fe98e4cfd23f63f544f1b9be5ccdb40deddd66715546a85be673fc75fbfab162

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Past_tense?oldid=4567337
sourceRevision=4567337
sourceRecordId=past:So the English ''past progressive'' sentence  "I was reading" would correspond t
originalSourceText=So the English ''past progressive'' sentence  "I was reading" would correspond to the Russian imperfective ('''я читал'''), since it refers to an action that wasn't completed. Likewise, the English ''simple past'' sentence "I read" corresponds to the Russian perfective ('''я прочитал''')
shippedText=я прочитал
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=b12fad2c641b1c5647b12762968bb5d302adaada5c7cdc620b3c153be64e5181
contentHash=59980eb5ea87cb539d534c007a989ca0653d970cb20f3ddbb1fafad0f31c4c31
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-ex-u13-reported-0-en`

- Objectives: obj-u13-reported
- Sessions: u13-s-orient, u13-s-reported
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: c7c6c8889c3964636d305b10b007b6de1f35ebbe57e2563b5c58195215cbc3bb

recordType=meaning
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Future_tense?oldid=3353822
sourceRevision=3353822
sourceRecordId=future::I will tell him in the evening - '''Я скажу ему ве́чером'''
:She said that she 
originalSourceText=:I will tell him in the evening - '''Я скажу ему ве́чером'''
:She said that she would speak to him in the evening - '''Она́ сказа́ла, что она́ ска́жет ему́ ве́чером''' - ''perfective past and future as actions completed, definite, etc.''
:When I'm in Moscow, I'll speak only in Russian - '''Когда́ я бу́ду в Москве́, я бу́ду говори́ть то́лько по-ру́сски'''
shippedText=She said that she would speak to him in the evening
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=50e5900972acaf1dde17a16c7795d9e8687768534625dfad94578b05153981a4
contentHash=87bc25d9d689f11c7000ae30ee4a0adc4c734eb1bc080a2d1d3691cf1e9f7c4c
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-ex-u13-reported-0-ru`

- Objectives: obj-u13-reported
- Sessions: u13-s-orient, u13-s-reported
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 4f2a94e6cb93b57b8fc3bb305e50e39f8515da8b2992d112c6b9a27e6b7de467

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Future_tense?oldid=3353822
sourceRevision=3353822
sourceRecordId=future::I will tell him in the evening - '''Я скажу ему ве́чером'''
:She said that she 
originalSourceText=:I will tell him in the evening - '''Я скажу ему ве́чером'''
:She said that she would speak to him in the evening - '''Она́ сказа́ла, что она́ ска́жет ему́ ве́чером''' - ''perfective past and future as actions completed, definite, etc.''
:When I'm in Moscow, I'll speak only in Russian - '''Когда́ я бу́ду в Москве́, я бу́ду говори́ть то́лько по-ру́сски'''
shippedText=Она́ сказа́ла, что она́ ска́жет ему́ ве́чером
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=50e5900972acaf1dde17a16c7795d9e8687768534625dfad94578b05153981a4
contentHash=c8e6010b0c44c8a1bb53ff367c6e2429431de7167b8a4987f2cfebb8e4a14dbd
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-ex-u13-reported-0-ru-r0`

- Objectives: obj-u13-reported
- Sessions: u13-s-orient, u13-s-reported
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 527ad3a4f3f4f5f8c409c79d937e3bca8b68f519b0a5936d411147bc8a847519

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Future_tense?oldid=3353822
sourceRevision=3353822
sourceRecordId=future::I will tell him in the evening - '''Я скажу ему ве́чером'''
:She said that she 
originalSourceText=:I will tell him in the evening - '''Я скажу ему ве́чером'''
:She said that she would speak to him in the evening - '''Она́ сказа́ла, что она́ ска́жет ему́ ве́чером''' - ''perfective past and future as actions completed, definite, etc.''
:When I'm in Moscow, I'll speak only in Russian - '''Когда́ я бу́ду в Москве́, я бу́ду говори́ть то́лько по-ру́сски'''
shippedText=Она́ сказа́ла
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=50e5900972acaf1dde17a16c7795d9e8687768534625dfad94578b05153981a4
contentHash=64813ba23abd21f0f54299006182c82bb906d5c980f82ac673e60a34a75fdc54
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-ex-u13-reported-0-ru-r1`

- Objectives: obj-u13-reported
- Sessions: u13-s-orient, u13-s-reported
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: f49c3f679271e8cdf40bdd299e788c0f6f0c8610dc4cbbcc7e902dacbc61f95f

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Future_tense?oldid=3353822
sourceRevision=3353822
sourceRecordId=future::I will tell him in the evening - '''Я скажу ему ве́чером'''
:She said that she 
originalSourceText=:I will tell him in the evening - '''Я скажу ему ве́чером'''
:She said that she would speak to him in the evening - '''Она́ сказа́ла, что она́ ска́жет ему́ ве́чером''' - ''perfective past and future as actions completed, definite, etc.''
:When I'm in Moscow, I'll speak only in Russian - '''Когда́ я бу́ду в Москве́, я бу́ду говори́ть то́лько по-ру́сски'''
shippedText=что она́ ска́жет ему́ ве́чером
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=50e5900972acaf1dde17a16c7795d9e8687768534625dfad94578b05153981a4
contentHash=4a9e380df1afca31789ff6de871bef0b21324783c1df4ed4b5764887f7367e38
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-ex-u13-reported-1-en`

- Objectives: obj-u13-reported
- Sessions: u13-s-orient, u13-s-reported
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 3c0e1a24c2eadb1b8cec5c5449926935da2c3e5e87e08714bdd8a7b1863ae15c

recordType=meaning
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Future_tense?oldid=3353822
sourceRevision=3353822
sourceRecordId=future::I will tell him in the evening - '''Я скажу ему ве́чером'''
:She said that she 
originalSourceText=:I will tell him in the evening - '''Я скажу ему ве́чером'''
:She said that she would speak to him in the evening - '''Она́ сказа́ла, что она́ ска́жет ему́ ве́чером''' - ''perfective past and future as actions completed, definite, etc.''
:When I'm in Moscow, I'll speak only in Russian - '''Когда́ я бу́ду в Москве́, я бу́ду говори́ть то́лько по-ру́сски'''
shippedText=I will tell him in the evening
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=50e5900972acaf1dde17a16c7795d9e8687768534625dfad94578b05153981a4
contentHash=5c6bacf9361266d97cd7ffc3b6eaf7630efd6cc38311f40bb27c6e6051c9d25d
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-ex-u13-reported-1-ru`

- Objectives: obj-u13-reported
- Sessions: u13-s-orient, u13-s-reported
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: dd9ecb0d393fc074c9d34796d9b29585acaa4ff09032efc8c89a82dd98f6ce6a

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Future_tense?oldid=3353822
sourceRevision=3353822
sourceRecordId=future::I will tell him in the evening - '''Я скажу ему ве́чером'''
:She said that she 
originalSourceText=:I will tell him in the evening - '''Я скажу ему ве́чером'''
:She said that she would speak to him in the evening - '''Она́ сказа́ла, что она́ ска́жет ему́ ве́чером''' - ''perfective past and future as actions completed, definite, etc.''
:When I'm in Moscow, I'll speak only in Russian - '''Когда́ я бу́ду в Москве́, я бу́ду говори́ть то́лько по-ру́сски'''
shippedText=Я скажу ему ве́чером
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=50e5900972acaf1dde17a16c7795d9e8687768534625dfad94578b05153981a4
contentHash=1d97d4a717ce4c125b89d40e37bd2b5899ce9c2e84ea087d34fe7d6c7a47713b
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-ex-u13-reported-2-en`

- Objectives: obj-u13-reported
- Sessions: u13-s-orient, u13-s-reported
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 3256cc5e5b38c8c9033e845906411d6f0df4a219bf4f179685df440168b47ab8

recordType=meaning
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Future_tense?oldid=3353822
sourceRevision=3353822
sourceRecordId=future:In English, when reporting a story, we usually change the tense of what was said
originalSourceText=In English, when reporting a story, we usually change the tense of what was said: compare "I ''am'' English" with "He said he ''was'' English" - the reported verb changes tense. In Russian, however, we keep the tense the same, so the same report is literally, "He said he '''is''' English" - "'''Он сказа́л, что он англича́нин'''", not "'''Он сказа́л, что он ''был'' англича́нином'''".
shippedText=we keep the tense the same
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=0f7241309a1870c33dc74ab29501295fbb0c2cd90246dffa2d48707d457f4f8a
contentHash=1ae9beddee18e1325dd0eb47c3fcf1e13deced48454094705ee8c8c7b1534528
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-ex-u13-reported-2-ru`

- Objectives: obj-u13-reported
- Sessions: u13-s-orient, u13-s-reported
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: a3e338a4eaae7dbf7ce4c332d3fe264b00703f5861a018a5310db8b923631a5b

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Future_tense?oldid=3353822
sourceRevision=3353822
sourceRecordId=future:In English, when reporting a story, we usually change the tense of what was said
originalSourceText=In English, when reporting a story, we usually change the tense of what was said: compare "I ''am'' English" with "He said he ''was'' English" - the reported verb changes tense. In Russian, however, we keep the tense the same, so the same report is literally, "He said he '''is''' English" - "'''Он сказа́л, что он англича́нин'''", not "'''Он сказа́л, что он ''был'' англича́нином'''".
shippedText=Он сказа́л, что он англича́нин
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=0f7241309a1870c33dc74ab29501295fbb0c2cd90246dffa2d48707d457f4f8a
contentHash=2562d2fa8f1de2890f3a7a0b8088edee6d2893c7609e3036feb64be1808581f8
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-ex-u13-reported-2-ru-r0`

- Objectives: obj-u13-reported
- Sessions: u13-s-orient, u13-s-reported
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 3f7ee78c1c75949f2d42e6b769f1a39430e7cc0ed36941934296c50dda517048

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Future_tense?oldid=3353822
sourceRevision=3353822
sourceRecordId=future:In English, when reporting a story, we usually change the tense of what was said
originalSourceText=In English, when reporting a story, we usually change the tense of what was said: compare "I ''am'' English" with "He said he ''was'' English" - the reported verb changes tense. In Russian, however, we keep the tense the same, so the same report is literally, "He said he '''is''' English" - "'''Он сказа́л, что он англича́нин'''", not "'''Он сказа́л, что он ''был'' англича́нином'''".
shippedText=Он сказа́л
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=0f7241309a1870c33dc74ab29501295fbb0c2cd90246dffa2d48707d457f4f8a
contentHash=3f1885615fe3da667d0a07d0114298cf95fb9495edbe2330311c844037160ac7
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-ex-u13-reported-2-ru-r1`

- Objectives: obj-u13-reported
- Sessions: u13-s-orient, u13-s-reported
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 4bdebddd5c155a36348049d6943f4919d8bbab68d68c3ea3006133d3dd29260a

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Future_tense?oldid=3353822
sourceRevision=3353822
sourceRecordId=future:In English, when reporting a story, we usually change the tense of what was said
originalSourceText=In English, when reporting a story, we usually change the tense of what was said: compare "I ''am'' English" with "He said he ''was'' English" - the reported verb changes tense. In Russian, however, we keep the tense the same, so the same report is literally, "He said he '''is''' English" - "'''Он сказа́л, что он англича́нин'''", not "'''Он сказа́л, что он ''был'' англича́нином'''".
shippedText=что он англича́нин
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=0f7241309a1870c33dc74ab29501295fbb0c2cd90246dffa2d48707d457f4f8a
contentHash=db573248ce368648dc8a8a72bad84cf63d5e75e59f0a0cbe59a27ad8edd7f4ba
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-ex-u13-reported-3-en`

- Objectives: obj-u13-reported
- Sessions: u13-s-orient, u13-s-reported
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 521c0068b79b7d715502eaf4815b0a49857a469f2a4a7e834acb322d0d6aad2c

recordType=meaning
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Future_tense?oldid=3353822
sourceRevision=3353822
sourceRecordId=future:'''Он сказа́л, что он ''был'' англича́нином'''
originalSourceText='''Он сказа́л, что он ''был'' англича́нином'''
shippedText=был
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=d8de57d6f26b59436184d2f398569baead20a2ad4066b11cf14f11610fa299b7
contentHash=aaa52eb6da840c5985ec7ad40637ddbbc1555c75361c6b2b54008a66a606d244
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-ex-u13-reported-3-ru`

- Objectives: obj-u13-reported
- Sessions: u13-s-orient, u13-s-reported
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 713f2d34ac799d58e739027fd98802aa3849054ba9e07871eb840ad0aa3ae467

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Future_tense?oldid=3353822
sourceRevision=3353822
sourceRecordId=future:'''Он сказа́л, что он ''был'' англича́нином'''
originalSourceText='''Он сказа́л, что он ''был'' англича́нином'''
shippedText=Он сказа́л, что он ''был'' англича́нином
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=d8de57d6f26b59436184d2f398569baead20a2ad4066b11cf14f11610fa299b7
contentHash=e3a3040611d768e55142779d1c83d05a41450fa5cb32b1177d4f9aa5f55ea704
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-ex-u13-reported-3-ru-r0`

- Objectives: obj-u13-reported
- Sessions: u13-s-orient, u13-s-reported
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 44b514268552839bc41bf3457efa54c8e67c0ba38d9a17a461b8e0d996576b20

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Future_tense?oldid=3353822
sourceRevision=3353822
sourceRecordId=future:'''Он сказа́л, что он ''был'' англича́нином'''
originalSourceText='''Он сказа́л, что он ''был'' англича́нином'''
shippedText=Он сказа́л
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=d8de57d6f26b59436184d2f398569baead20a2ad4066b11cf14f11610fa299b7
contentHash=3f1885615fe3da667d0a07d0114298cf95fb9495edbe2330311c844037160ac7
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-ex-u13-reported-3-ru-r1`

- Objectives: obj-u13-reported
- Sessions: u13-s-orient, u13-s-reported
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 4ded5ccab60c3fcf2031e00b1310a7ce6d3285c2f470206a0895f4d8dca650a6

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Future_tense?oldid=3353822
sourceRevision=3353822
sourceRecordId=future:'''Он сказа́л, что он ''был'' англича́нином'''
originalSourceText='''Он сказа́л, что он ''был'' англича́нином'''
shippedText=что он
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=d8de57d6f26b59436184d2f398569baead20a2ad4066b11cf14f11610fa299b7
contentHash=fa158961927cdb2bc8b3a8d1d762abaf174caa1e29ddced488ad523f0809a1e3
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-ex-u13-reported-3-ru-r2`

- Objectives: obj-u13-reported
- Sessions: u13-s-orient, u13-s-reported
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 1efe3d13a059e3329d92b8cd7e31d0c67ee9dad0e66e70a359546c892ef4d119

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Future_tense?oldid=3353822
sourceRevision=3353822
sourceRecordId=future:'''Он сказа́л, что он ''был'' англича́нином'''
originalSourceText='''Он сказа́л, что он ''был'' англича́нином'''
shippedText=был
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=d8de57d6f26b59436184d2f398569baead20a2ad4066b11cf14f11610fa299b7
contentHash=aaa52eb6da840c5985ec7ad40637ddbbc1555c75361c6b2b54008a66a606d244
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-ex-u13-reported-3-ru-r3`

- Objectives: obj-u13-reported
- Sessions: u13-s-orient, u13-s-reported
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: fe1331f4a98c97b44c5d31f8ed3dd2e97fb3f91b881e7b34e0001b0675a4ddc3

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Future_tense?oldid=3353822
sourceRevision=3353822
sourceRecordId=future:'''Он сказа́л, что он ''был'' англича́нином'''
originalSourceText='''Он сказа́л, что он ''был'' англича́нином'''
shippedText=англича́нином
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=d8de57d6f26b59436184d2f398569baead20a2ad4066b11cf14f11610fa299b7
contentHash=969ba137a791b66fedd7ad299df5213e8aad1e2331122aa125d5de0c16245f24
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-ex-u13-tense-keep-0-en`

- Objectives: obj-u13-tense-keep
- Sessions: u13-s-orient, u13-s-tense-keep
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: e5b81836eac79f4fb26372077391d940eb9856181dfe5974d68d5bec4efe5417

recordType=meaning
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Future_tense?oldid=3353822
sourceRevision=3353822
sourceRecordId=future:In English, when reporting a story, we usually change the tense of what was said
originalSourceText=In English, when reporting a story, we usually change the tense of what was said: compare "I ''am'' English" with "He said he ''was'' English" - the reported verb changes tense. In Russian, however, we keep the tense the same, so the same report is literally, "He said he '''is''' English" - "'''Он сказа́л, что он англича́нин'''", not "'''Он сказа́л, что он ''был'' англича́нином'''".
shippedText=we keep the tense the same
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=0f7241309a1870c33dc74ab29501295fbb0c2cd90246dffa2d48707d457f4f8a
contentHash=1ae9beddee18e1325dd0eb47c3fcf1e13deced48454094705ee8c8c7b1534528
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-ex-u13-tense-keep-0-ru`

- Objectives: obj-u13-tense-keep
- Sessions: u13-s-orient, u13-s-tense-keep
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 52fc3485d780dddbf15c7a8b870b75676cf5c4654daff3fa7039bd262d3c44ec

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Future_tense?oldid=3353822
sourceRevision=3353822
sourceRecordId=future:In English, when reporting a story, we usually change the tense of what was said
originalSourceText=In English, when reporting a story, we usually change the tense of what was said: compare "I ''am'' English" with "He said he ''was'' English" - the reported verb changes tense. In Russian, however, we keep the tense the same, so the same report is literally, "He said he '''is''' English" - "'''Он сказа́л, что он англича́нин'''", not "'''Он сказа́л, что он ''был'' англича́нином'''".
shippedText=Он сказа́л, что он англича́нин
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=0f7241309a1870c33dc74ab29501295fbb0c2cd90246dffa2d48707d457f4f8a
contentHash=2562d2fa8f1de2890f3a7a0b8088edee6d2893c7609e3036feb64be1808581f8
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-ex-u13-tense-keep-0-ru-r0`

- Objectives: obj-u13-tense-keep
- Sessions: u13-s-orient, u13-s-tense-keep
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 392b6215fc6e1c4f7864cfcacea03eb36012bcb55d3ea8455470c81657bb3f35

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Future_tense?oldid=3353822
sourceRevision=3353822
sourceRecordId=future:In English, when reporting a story, we usually change the tense of what was said
originalSourceText=In English, when reporting a story, we usually change the tense of what was said: compare "I ''am'' English" with "He said he ''was'' English" - the reported verb changes tense. In Russian, however, we keep the tense the same, so the same report is literally, "He said he '''is''' English" - "'''Он сказа́л, что он англича́нин'''", not "'''Он сказа́л, что он ''был'' англича́нином'''".
shippedText=Он сказа́л
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=0f7241309a1870c33dc74ab29501295fbb0c2cd90246dffa2d48707d457f4f8a
contentHash=3f1885615fe3da667d0a07d0114298cf95fb9495edbe2330311c844037160ac7
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-ex-u13-tense-keep-0-ru-r1`

- Objectives: obj-u13-tense-keep
- Sessions: u13-s-orient, u13-s-tense-keep
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: f8f0de47340c1c2c6743c9d233cc4f707cfc94c259e11fd0604c48d1e6bc28ef

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Future_tense?oldid=3353822
sourceRevision=3353822
sourceRecordId=future:In English, when reporting a story, we usually change the tense of what was said
originalSourceText=In English, when reporting a story, we usually change the tense of what was said: compare "I ''am'' English" with "He said he ''was'' English" - the reported verb changes tense. In Russian, however, we keep the tense the same, so the same report is literally, "He said he '''is''' English" - "'''Он сказа́л, что он англича́нин'''", not "'''Он сказа́л, что он ''был'' англича́нином'''".
shippedText=что он англича́нин
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=0f7241309a1870c33dc74ab29501295fbb0c2cd90246dffa2d48707d457f4f8a
contentHash=db573248ce368648dc8a8a72bad84cf63d5e75e59f0a0cbe59a27ad8edd7f4ba
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-ex-u13-tense-keep-1-en`

- Objectives: obj-u13-tense-keep
- Sessions: u13-s-orient, u13-s-tense-keep
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 34f4946b59fc44a1f54f8b811ab35f526922dd96ed1b38d94b906a5b8aca0698

recordType=meaning
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Future_tense?oldid=3353822
sourceRevision=3353822
sourceRecordId=future::I will tell him in the evening - '''Я скажу ему ве́чером'''
:She said that she 
originalSourceText=:I will tell him in the evening - '''Я скажу ему ве́чером'''
:She said that she would speak to him in the evening - '''Она́ сказа́ла, что она́ ска́жет ему́ ве́чером''' - ''perfective past and future as actions completed, definite, etc.''
:When I'm in Moscow, I'll speak only in Russian - '''Когда́ я бу́ду в Москве́, я бу́ду говори́ть то́лько по-ру́сски'''
shippedText=I will tell him in the evening
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=50e5900972acaf1dde17a16c7795d9e8687768534625dfad94578b05153981a4
contentHash=5c6bacf9361266d97cd7ffc3b6eaf7630efd6cc38311f40bb27c6e6051c9d25d
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-ex-u13-tense-keep-1-ru`

- Objectives: obj-u13-tense-keep
- Sessions: u13-s-orient, u13-s-tense-keep
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: b9492c26b601c6d89a6afff8a8cdc7e0a7634be8674d619b9dc1706b5814e800

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Future_tense?oldid=3353822
sourceRevision=3353822
sourceRecordId=future::I will tell him in the evening - '''Я скажу ему ве́чером'''
:She said that she 
originalSourceText=:I will tell him in the evening - '''Я скажу ему ве́чером'''
:She said that she would speak to him in the evening - '''Она́ сказа́ла, что она́ ска́жет ему́ ве́чером''' - ''perfective past and future as actions completed, definite, etc.''
:When I'm in Moscow, I'll speak only in Russian - '''Когда́ я бу́ду в Москве́, я бу́ду говори́ть то́лько по-ру́сски'''
shippedText=Я скажу ему ве́чером
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=50e5900972acaf1dde17a16c7795d9e8687768534625dfad94578b05153981a4
contentHash=1d97d4a717ce4c125b89d40e37bd2b5899ce9c2e84ea087d34fe7d6c7a47713b
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-ex-u13-tense-keep-2-en`

- Objectives: obj-u13-tense-keep
- Sessions: u13-s-orient, u13-s-tense-keep
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: b5827aebea3d6c49a9806ce40e7a3ed1ad260c3d9f0f67974b2c055e43be57ca

recordType=meaning
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Future_tense?oldid=3353822
sourceRevision=3353822
sourceRecordId=future::I will tell him in the evening - '''Я скажу ему ве́чером'''
:She said that she 
originalSourceText=:I will tell him in the evening - '''Я скажу ему ве́чером'''
:She said that she would speak to him in the evening - '''Она́ сказа́ла, что она́ ска́жет ему́ ве́чером''' - ''perfective past and future as actions completed, definite, etc.''
:When I'm in Moscow, I'll speak only in Russian - '''Когда́ я бу́ду в Москве́, я бу́ду говори́ть то́лько по-ру́сски'''
shippedText=She said that she would speak to him in the evening
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=50e5900972acaf1dde17a16c7795d9e8687768534625dfad94578b05153981a4
contentHash=87bc25d9d689f11c7000ae30ee4a0adc4c734eb1bc080a2d1d3691cf1e9f7c4c
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-ex-u13-tense-keep-2-ru`

- Objectives: obj-u13-tense-keep
- Sessions: u13-s-orient, u13-s-tense-keep
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 10abb6459a609c71cf69dae2b44a26fd1ba8de3f56fd72074492cb21542edcd0

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Future_tense?oldid=3353822
sourceRevision=3353822
sourceRecordId=future::I will tell him in the evening - '''Я скажу ему ве́чером'''
:She said that she 
originalSourceText=:I will tell him in the evening - '''Я скажу ему ве́чером'''
:She said that she would speak to him in the evening - '''Она́ сказа́ла, что она́ ска́жет ему́ ве́чером''' - ''perfective past and future as actions completed, definite, etc.''
:When I'm in Moscow, I'll speak only in Russian - '''Когда́ я бу́ду в Москве́, я бу́ду говори́ть то́лько по-ру́сски'''
shippedText=Она́ сказа́ла, что она́ ска́жет ему́ ве́чером
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=50e5900972acaf1dde17a16c7795d9e8687768534625dfad94578b05153981a4
contentHash=c8e6010b0c44c8a1bb53ff367c6e2429431de7167b8a4987f2cfebb8e4a14dbd
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-ex-u13-tense-keep-2-ru-r0`

- Objectives: obj-u13-tense-keep
- Sessions: u13-s-orient, u13-s-tense-keep
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: f4401d6ed778d285e0289e2bc80c6d0cfc7425f2e964702af5a0031c52f95350

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Future_tense?oldid=3353822
sourceRevision=3353822
sourceRecordId=future::I will tell him in the evening - '''Я скажу ему ве́чером'''
:She said that she 
originalSourceText=:I will tell him in the evening - '''Я скажу ему ве́чером'''
:She said that she would speak to him in the evening - '''Она́ сказа́ла, что она́ ска́жет ему́ ве́чером''' - ''perfective past and future as actions completed, definite, etc.''
:When I'm in Moscow, I'll speak only in Russian - '''Когда́ я бу́ду в Москве́, я бу́ду говори́ть то́лько по-ру́сски'''
shippedText=Она́ сказа́ла
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=50e5900972acaf1dde17a16c7795d9e8687768534625dfad94578b05153981a4
contentHash=64813ba23abd21f0f54299006182c82bb906d5c980f82ac673e60a34a75fdc54
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-ex-u13-tense-keep-2-ru-r1`

- Objectives: obj-u13-tense-keep
- Sessions: u13-s-orient, u13-s-tense-keep
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: e5be4769ff0d935147cf2369fa357818efef2e9ec941daa9ef85c6b88d15516a

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Future_tense?oldid=3353822
sourceRevision=3353822
sourceRecordId=future::I will tell him in the evening - '''Я скажу ему ве́чером'''
:She said that she 
originalSourceText=:I will tell him in the evening - '''Я скажу ему ве́чером'''
:She said that she would speak to him in the evening - '''Она́ сказа́ла, что она́ ска́жет ему́ ве́чером''' - ''perfective past and future as actions completed, definite, etc.''
:When I'm in Moscow, I'll speak only in Russian - '''Когда́ я бу́ду в Москве́, я бу́ду говори́ть то́лько по-ру́сски'''
shippedText=что она́ ска́жет ему́ ве́чером
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=50e5900972acaf1dde17a16c7795d9e8687768534625dfad94578b05153981a4
contentHash=4a9e380df1afca31789ff6de871bef0b21324783c1df4ed4b5764887f7367e38
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u13-infinitive-rem`

- Objectives: obj-u13-infinitive
- Sessions: u13-s-orient, u13-s-infinitive
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: bddacb2f6593296ed3ef16d66935019beb9a51e4d1c678dfde3bfebe07b32b44

recordType=instruction
sourceUrl=https://en.wikibooks.org/wiki/Russian/Verbal_Aspect?oldid=4059032
sourceRevision=4059032
sourceRecordId=aspect:Let us take the verbs "to speak" (imperfective: '''говори́ть''', perfective: '''
originalSourceText=Let us take the verbs "to speak" (imperfective: '''говори́ть''', perfective: '''сказа́ть''') and "to do" (imper.: '''де́лать''', per.: '''сде́лать''').
shippedText=Let us take the verbs "to speak" (imperfective: '''говори́ть''', perfective: '''сказа́ть''') and "to do" (imper.: '''де́лать''', per.: '''сде́лать''').
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=fddb8c4118a7145fbebc56cb52193b4c7d4bbe413a8f87cafc04e0e6a1a812d8
contentHash=fddb8c4118a7145fbebc56cb52193b4c7d4bbe413a8f87cafc04e0e6a1a812d8
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u13-infinitive-rem-r0`

- Objectives: obj-u13-infinitive
- Sessions: u13-s-orient, u13-s-infinitive
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 52a50f18ece66fd323e38ba27ee06155655f643afd2aa38eebe15637ad47f305

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Verbal_Aspect?oldid=4059032
sourceRevision=4059032
sourceRecordId=aspect:Let us take the verbs "to speak" (imperfective: '''говори́ть''', perfective: '''
originalSourceText=Let us take the verbs "to speak" (imperfective: '''говори́ть''', perfective: '''сказа́ть''') and "to do" (imper.: '''де́лать''', per.: '''сде́лать''').
shippedText=говори́ть
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=fddb8c4118a7145fbebc56cb52193b4c7d4bbe413a8f87cafc04e0e6a1a812d8
contentHash=1f37b6b2cea84a10b3a4fb1a11f4d155460f649d86b38cd375ea5ee336fcd5e4
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u13-infinitive-rem-r1`

- Objectives: obj-u13-infinitive
- Sessions: u13-s-orient, u13-s-infinitive
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 1a94edb75f2f7da49eb64c08788007530c7d5cb2c33f77703eed18408e9f460f

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Verbal_Aspect?oldid=4059032
sourceRevision=4059032
sourceRecordId=aspect:Let us take the verbs "to speak" (imperfective: '''говори́ть''', perfective: '''
originalSourceText=Let us take the verbs "to speak" (imperfective: '''говори́ть''', perfective: '''сказа́ть''') and "to do" (imper.: '''де́лать''', per.: '''сде́лать''').
shippedText=сказа́ть
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=fddb8c4118a7145fbebc56cb52193b4c7d4bbe413a8f87cafc04e0e6a1a812d8
contentHash=5547b4749450ac775ed6300c8589dd3d617007a61025ac2ed585dba015b692a8
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u13-infinitive-rem-r2`

- Objectives: obj-u13-infinitive
- Sessions: u13-s-orient, u13-s-infinitive
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: ad49f7f8cfaca9ed7c3c4f8b18d620bc2219b00b6ca07fb63f97453d0115f295

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Verbal_Aspect?oldid=4059032
sourceRevision=4059032
sourceRecordId=aspect:Let us take the verbs "to speak" (imperfective: '''говори́ть''', perfective: '''
originalSourceText=Let us take the verbs "to speak" (imperfective: '''говори́ть''', perfective: '''сказа́ть''') and "to do" (imper.: '''де́лать''', per.: '''сде́лать''').
shippedText=де́лать
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=fddb8c4118a7145fbebc56cb52193b4c7d4bbe413a8f87cafc04e0e6a1a812d8
contentHash=c472ba4cd4b95d85e2b7686abe28d3860a88b3ecb9eb6de34881b9fd9e089bbb
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u13-infinitive-rem-r3`

- Objectives: obj-u13-infinitive
- Sessions: u13-s-orient, u13-s-infinitive
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 71d76787e280d26d24239ece58112cc6c5b52952205b23f73629ded0d36259a6

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Verbal_Aspect?oldid=4059032
sourceRevision=4059032
sourceRecordId=aspect:Let us take the verbs "to speak" (imperfective: '''говори́ть''', perfective: '''
originalSourceText=Let us take the verbs "to speak" (imperfective: '''говори́ть''', perfective: '''сказа́ть''') and "to do" (imper.: '''де́лать''', per.: '''сде́лать''').
shippedText=сде́лать
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=fddb8c4118a7145fbebc56cb52193b4c7d4bbe413a8f87cafc04e0e6a1a812d8
contentHash=b57324378572bf8c4a9dc00f2daeecb1ed4a8f4975529115e9554a00849c3325
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u13-past-aspect-rem`

- Objectives: obj-u13-past-aspect
- Sessions: u13-s-orient, u13-s-past-aspect
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: ea1f513e2e00ac0eaf235e792999b028bce2057f9421de46d5de60ab5c5063c3

recordType=instruction
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Past_tense?oldid=4567337
sourceRevision=4567337
sourceRecordId=past:So the English ''past progressive'' sentence  "I was reading" would correspond t
originalSourceText=So the English ''past progressive'' sentence  "I was reading" would correspond to the Russian imperfective ('''я читал'''), since it refers to an action that wasn't completed. Likewise, the English ''simple past'' sentence "I read" corresponds to the Russian perfective ('''я прочитал''')
shippedText=So the English ''past progressive'' sentence  "I was reading" would correspond to the Russian imperfective ('''я читал'''), since it refers to an action that wasn't completed. Likewise, the English ''simple past'' sentence "I read" corresponds to the Russian perfective ('''я прочитал''')
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=b12fad2c641b1c5647b12762968bb5d302adaada5c7cdc620b3c153be64e5181
contentHash=b12fad2c641b1c5647b12762968bb5d302adaada5c7cdc620b3c153be64e5181
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u13-past-aspect-rem-r0`

- Objectives: obj-u13-past-aspect
- Sessions: u13-s-orient, u13-s-past-aspect
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 22e045da112a6c20d969eb423030d4e0e88013c7a64f7c553f75cdaeb0ad6a51

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Past_tense?oldid=4567337
sourceRevision=4567337
sourceRecordId=past:So the English ''past progressive'' sentence  "I was reading" would correspond t
originalSourceText=So the English ''past progressive'' sentence  "I was reading" would correspond to the Russian imperfective ('''я читал'''), since it refers to an action that wasn't completed. Likewise, the English ''simple past'' sentence "I read" corresponds to the Russian perfective ('''я прочитал''')
shippedText=я читал
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=b12fad2c641b1c5647b12762968bb5d302adaada5c7cdc620b3c153be64e5181
contentHash=442c85192cb20fba15b79dda33e091ca8ae22a17ab03797e644de11c4b490566
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u13-past-aspect-rem-r1`

- Objectives: obj-u13-past-aspect
- Sessions: u13-s-orient, u13-s-past-aspect
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 983578b749b9fc45b022c070d0f64c10779edf6f545b464129a74110c1b559ac

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Past_tense?oldid=4567337
sourceRevision=4567337
sourceRecordId=past:So the English ''past progressive'' sentence  "I was reading" would correspond t
originalSourceText=So the English ''past progressive'' sentence  "I was reading" would correspond to the Russian imperfective ('''я читал'''), since it refers to an action that wasn't completed. Likewise, the English ''simple past'' sentence "I read" corresponds to the Russian perfective ('''я прочитал''')
shippedText=я прочитал
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=b12fad2c641b1c5647b12762968bb5d302adaada5c7cdc620b3c153be64e5181
contentHash=59980eb5ea87cb539d534c007a989ca0653d970cb20f3ddbb1fafad0f31c4c31
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u13-past-aspect-x-0`

- Objectives: obj-u13-past-aspect
- Sessions: u13-s-orient, u13-s-past-aspect
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 45b6a77171b3338e472dfe1fb3d908cce0a6de3a68978c3a017823f9660c4216

recordType=meaning
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Past_tense?oldid=4567337
sourceRevision=4567337
sourceRecordId=past:So the English ''past progressive'' sentence  "I was reading" would correspond t
originalSourceText=So the English ''past progressive'' sentence  "I was reading" would correspond to the Russian imperfective ('''я читал'''), since it refers to an action that wasn't completed. Likewise, the English ''simple past'' sentence "I read" corresponds to the Russian perfective ('''я прочитал''')
shippedText=I was reading
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=b12fad2c641b1c5647b12762968bb5d302adaada5c7cdc620b3c153be64e5181
contentHash=c8bab909d1c42d3cf8c44173383f65b9a9c1c5185756da84a3c7eecc6a8abb8f
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u13-past-aspect-x-1`

- Objectives: obj-u13-past-aspect
- Sessions: u13-s-orient, u13-s-past-aspect
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 95ef540339594e057801a9dd52c784a3a83459fdb908a3fdc893647e8c3ba86c

recordType=meaning
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Past_tense?oldid=4567337
sourceRevision=4567337
sourceRecordId=past:So the English ''past progressive'' sentence  "I was reading" would correspond t
originalSourceText=So the English ''past progressive'' sentence  "I was reading" would correspond to the Russian imperfective ('''я читал'''), since it refers to an action that wasn't completed. Likewise, the English ''simple past'' sentence "I read" corresponds to the Russian perfective ('''я прочитал''')
shippedText=I read
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=b12fad2c641b1c5647b12762968bb5d302adaada5c7cdc620b3c153be64e5181
contentHash=5e0d737af507e7533d4e288ecce80627dc9ed90656ed2e36e15ead74bdcedb0b
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u13-reported-rem`

- Objectives: obj-u13-reported
- Sessions: u13-s-orient, u13-s-reported
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 9a1a7e2a53ec1b09f9e5304b974c48fc318b27330c0c460c9575a3f4353c213a

recordType=instruction
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Future_tense?oldid=3353822
sourceRevision=3353822
sourceRecordId=future::I will tell him in the evening - '''Я скажу ему ве́чером'''
:She said that she 
originalSourceText=:I will tell him in the evening - '''Я скажу ему ве́чером'''
:She said that she would speak to him in the evening - '''Она́ сказа́ла, что она́ ска́жет ему́ ве́чером''' - ''perfective past and future as actions completed, definite, etc.''
:When I'm in Moscow, I'll speak only in Russian - '''Когда́ я бу́ду в Москве́, я бу́ду говори́ть то́лько по-ру́сски'''
shippedText=:I will tell him in the evening - '''Я скажу ему ве́чером'''
:She said that she would speak to him in the evening - '''Она́ сказа́ла, что она́ ска́жет ему́ ве́чером''' - ''perfective past and future as actions completed, definite, etc.''
:When I'm in Moscow, I'll speak only in Russian - '''Когда́ я бу́ду в Москве́, я бу́ду говори́ть то́лько по-ру́сски'''
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=50e5900972acaf1dde17a16c7795d9e8687768534625dfad94578b05153981a4
contentHash=50e5900972acaf1dde17a16c7795d9e8687768534625dfad94578b05153981a4
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u13-reported-rem-r0`

- Objectives: obj-u13-reported
- Sessions: u13-s-orient, u13-s-reported
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 33597678f5e3891f78afeb2dc6694822dbcd3ec17cda249692b31836757a0f37

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Future_tense?oldid=3353822
sourceRevision=3353822
sourceRecordId=future::I will tell him in the evening - '''Я скажу ему ве́чером'''
:She said that she 
originalSourceText=:I will tell him in the evening - '''Я скажу ему ве́чером'''
:She said that she would speak to him in the evening - '''Она́ сказа́ла, что она́ ска́жет ему́ ве́чером''' - ''perfective past and future as actions completed, definite, etc.''
:When I'm in Moscow, I'll speak only in Russian - '''Когда́ я бу́ду в Москве́, я бу́ду говори́ть то́лько по-ру́сски'''
shippedText=Я скажу ему ве́чером
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=50e5900972acaf1dde17a16c7795d9e8687768534625dfad94578b05153981a4
contentHash=1d97d4a717ce4c125b89d40e37bd2b5899ce9c2e84ea087d34fe7d6c7a47713b
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u13-reported-rem-r1`

- Objectives: obj-u13-reported
- Sessions: u13-s-orient, u13-s-reported
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 3ea5846117be171f760b634ec982dcec11bd587e97e0d627daa6d166aee92d64

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Future_tense?oldid=3353822
sourceRevision=3353822
sourceRecordId=future::I will tell him in the evening - '''Я скажу ему ве́чером'''
:She said that she 
originalSourceText=:I will tell him in the evening - '''Я скажу ему ве́чером'''
:She said that she would speak to him in the evening - '''Она́ сказа́ла, что она́ ска́жет ему́ ве́чером''' - ''perfective past and future as actions completed, definite, etc.''
:When I'm in Moscow, I'll speak only in Russian - '''Когда́ я бу́ду в Москве́, я бу́ду говори́ть то́лько по-ру́сски'''
shippedText=Она́ сказа́ла
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=50e5900972acaf1dde17a16c7795d9e8687768534625dfad94578b05153981a4
contentHash=64813ba23abd21f0f54299006182c82bb906d5c980f82ac673e60a34a75fdc54
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u13-reported-rem-r2`

- Objectives: obj-u13-reported
- Sessions: u13-s-orient, u13-s-reported
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 64f35f9b3283fa38603cf5d8f43a9ad9dd5c5859de792e30769a4e0459516605

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Future_tense?oldid=3353822
sourceRevision=3353822
sourceRecordId=future::I will tell him in the evening - '''Я скажу ему ве́чером'''
:She said that she 
originalSourceText=:I will tell him in the evening - '''Я скажу ему ве́чером'''
:She said that she would speak to him in the evening - '''Она́ сказа́ла, что она́ ска́жет ему́ ве́чером''' - ''perfective past and future as actions completed, definite, etc.''
:When I'm in Moscow, I'll speak only in Russian - '''Когда́ я бу́ду в Москве́, я бу́ду говори́ть то́лько по-ру́сски'''
shippedText=что она́ ска́жет ему́ ве́чером
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=50e5900972acaf1dde17a16c7795d9e8687768534625dfad94578b05153981a4
contentHash=4a9e380df1afca31789ff6de871bef0b21324783c1df4ed4b5764887f7367e38
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u13-reported-rem-r3`

- Objectives: obj-u13-reported
- Sessions: u13-s-orient, u13-s-reported
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: cb54c9eb02990b39fdd8692dede017f2b16aca2e67e48f77a3132c62ac46d316

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Future_tense?oldid=3353822
sourceRevision=3353822
sourceRecordId=future::I will tell him in the evening - '''Я скажу ему ве́чером'''
:She said that she 
originalSourceText=:I will tell him in the evening - '''Я скажу ему ве́чером'''
:She said that she would speak to him in the evening - '''Она́ сказа́ла, что она́ ска́жет ему́ ве́чером''' - ''perfective past and future as actions completed, definite, etc.''
:When I'm in Moscow, I'll speak only in Russian - '''Когда́ я бу́ду в Москве́, я бу́ду говори́ть то́лько по-ру́сски'''
shippedText=Когда́ я бу́ду в Москве́
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=50e5900972acaf1dde17a16c7795d9e8687768534625dfad94578b05153981a4
contentHash=b03e4cf434996a19407a0a0b983a97efd925a0652e814b1457bf87e09d075271
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u13-reported-rem-r4`

- Objectives: obj-u13-reported
- Sessions: u13-s-orient, u13-s-reported
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 4fc7cc0e99cff4c7e740092ac16949385f3939fead4ea97f101c6dc847584f4c

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Future_tense?oldid=3353822
sourceRevision=3353822
sourceRecordId=future::I will tell him in the evening - '''Я скажу ему ве́чером'''
:She said that she 
originalSourceText=:I will tell him in the evening - '''Я скажу ему ве́чером'''
:She said that she would speak to him in the evening - '''Она́ сказа́ла, что она́ ска́жет ему́ ве́чером''' - ''perfective past and future as actions completed, definite, etc.''
:When I'm in Moscow, I'll speak only in Russian - '''Когда́ я бу́ду в Москве́, я бу́ду говори́ть то́лько по-ру́сски'''
shippedText=я бу́ду говори́ть то́лько по
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=50e5900972acaf1dde17a16c7795d9e8687768534625dfad94578b05153981a4
contentHash=c452297f24fe362880c9f4e2d470876ee37eac3b5703026b74d65dd44197ce4d
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u13-reported-rem-r5`

- Objectives: obj-u13-reported
- Sessions: u13-s-orient, u13-s-reported
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 54112741e99541a5d7aefac0d59582a1bf0ee41d900dd28f6e6e688b14f178d6

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Future_tense?oldid=3353822
sourceRevision=3353822
sourceRecordId=future::I will tell him in the evening - '''Я скажу ему ве́чером'''
:She said that she 
originalSourceText=:I will tell him in the evening - '''Я скажу ему ве́чером'''
:She said that she would speak to him in the evening - '''Она́ сказа́ла, что она́ ска́жет ему́ ве́чером''' - ''perfective past and future as actions completed, definite, etc.''
:When I'm in Moscow, I'll speak only in Russian - '''Когда́ я бу́ду в Москве́, я бу́ду говори́ть то́лько по-ру́сски'''
shippedText=ру́сски
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=50e5900972acaf1dde17a16c7795d9e8687768534625dfad94578b05153981a4
contentHash=b357db2b6a3f724f8e6b875eb98919fb51c150322b4866acd26a6d72dbf71d8f
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u13-reported-x-0`

- Objectives: obj-u13-reported
- Sessions: u13-s-orient, u13-s-reported
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 4d5890188df6a2f700f93ec4962bd3f8366e27bb5fbd15bcd97d431acfa4ad71

recordType=instruction
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Future_tense?oldid=3353822
sourceRevision=3353822
sourceRecordId=future:In English, when reporting a story, we usually change the tense of what was said
originalSourceText=In English, when reporting a story, we usually change the tense of what was said: compare "I ''am'' English" with "He said he ''was'' English" - the reported verb changes tense. In Russian, however, we keep the tense the same, so the same report is literally, "He said he '''is''' English" - "'''Он сказа́л, что он англича́нин'''", not "'''Он сказа́л, что он ''был'' англича́нином'''".
shippedText=we keep the tense the same
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=0f7241309a1870c33dc74ab29501295fbb0c2cd90246dffa2d48707d457f4f8a
contentHash=1ae9beddee18e1325dd0eb47c3fcf1e13deced48454094705ee8c8c7b1534528
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u13-reported-x-1`

- Objectives: obj-u13-reported
- Sessions: u13-s-orient, u13-s-reported
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 538e9fd364db5996870683207b9b1efd39c153775a362aad4ec32ff057984452

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Future_tense?oldid=3353822
sourceRevision=3353822
sourceRecordId=future::I will tell him in the evening - '''Я скажу ему ве́чером'''
:She said that she 
originalSourceText=:I will tell him in the evening - '''Я скажу ему ве́чером'''
:She said that she would speak to him in the evening - '''Она́ сказа́ла, что она́ ска́жет ему́ ве́чером''' - ''perfective past and future as actions completed, definite, etc.''
:When I'm in Moscow, I'll speak only in Russian - '''Когда́ я бу́ду в Москве́, я бу́ду говори́ть то́лько по-ру́сски'''
shippedText=Когда́ я бу́ду в Москве́, я бу́ду говори́ть то́лько по-ру́сски
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=50e5900972acaf1dde17a16c7795d9e8687768534625dfad94578b05153981a4
contentHash=13b6e1030f6ed933a2bb7d97a90ad219d8711dccce1afface9cbc63bdcdfb950
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u13-reported-x-1-r0`

- Objectives: obj-u13-reported
- Sessions: u13-s-orient, u13-s-reported
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 25330f62317d4a4b39578ea96725c7450609805c42e1f4201452184c20a70e20

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Future_tense?oldid=3353822
sourceRevision=3353822
sourceRecordId=future::I will tell him in the evening - '''Я скажу ему ве́чером'''
:She said that she 
originalSourceText=:I will tell him in the evening - '''Я скажу ему ве́чером'''
:She said that she would speak to him in the evening - '''Она́ сказа́ла, что она́ ска́жет ему́ ве́чером''' - ''perfective past and future as actions completed, definite, etc.''
:When I'm in Moscow, I'll speak only in Russian - '''Когда́ я бу́ду в Москве́, я бу́ду говори́ть то́лько по-ру́сски'''
shippedText=Когда́ я бу́ду в Москве́
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=50e5900972acaf1dde17a16c7795d9e8687768534625dfad94578b05153981a4
contentHash=b03e4cf434996a19407a0a0b983a97efd925a0652e814b1457bf87e09d075271
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u13-reported-x-1-r1`

- Objectives: obj-u13-reported
- Sessions: u13-s-orient, u13-s-reported
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 0dd034549fe78d5ea16e6c07dd04ac78b650e86c2fe6b9da576bbd0f6418e693

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Future_tense?oldid=3353822
sourceRevision=3353822
sourceRecordId=future::I will tell him in the evening - '''Я скажу ему ве́чером'''
:She said that she 
originalSourceText=:I will tell him in the evening - '''Я скажу ему ве́чером'''
:She said that she would speak to him in the evening - '''Она́ сказа́ла, что она́ ска́жет ему́ ве́чером''' - ''perfective past and future as actions completed, definite, etc.''
:When I'm in Moscow, I'll speak only in Russian - '''Когда́ я бу́ду в Москве́, я бу́ду говори́ть то́лько по-ру́сски'''
shippedText=я бу́ду говори́ть то́лько по
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=50e5900972acaf1dde17a16c7795d9e8687768534625dfad94578b05153981a4
contentHash=c452297f24fe362880c9f4e2d470876ee37eac3b5703026b74d65dd44197ce4d
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u13-reported-x-1-r2`

- Objectives: obj-u13-reported
- Sessions: u13-s-orient, u13-s-reported
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: b1be596ac7bcf244a1866f535b1068c51bd357797d1feeda267cc980485a24ce

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Future_tense?oldid=3353822
sourceRevision=3353822
sourceRecordId=future::I will tell him in the evening - '''Я скажу ему ве́чером'''
:She said that she 
originalSourceText=:I will tell him in the evening - '''Я скажу ему ве́чером'''
:She said that she would speak to him in the evening - '''Она́ сказа́ла, что она́ ска́жет ему́ ве́чером''' - ''perfective past and future as actions completed, definite, etc.''
:When I'm in Moscow, I'll speak only in Russian - '''Когда́ я бу́ду в Москве́, я бу́ду говори́ть то́лько по-ру́сски'''
shippedText=ру́сски
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=50e5900972acaf1dde17a16c7795d9e8687768534625dfad94578b05153981a4
contentHash=b357db2b6a3f724f8e6b875eb98919fb51c150322b4866acd26a6d72dbf71d8f
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u13-tense-keep-rem`

- Objectives: obj-u13-tense-keep
- Sessions: u13-s-orient, u13-s-tense-keep
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 307497ff54ed102d509cb65a0f363416e0b7467794a03ff36cb97ce299593b6f

recordType=instruction
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Future_tense?oldid=3353822
sourceRevision=3353822
sourceRecordId=future:In English, when reporting a story, we usually change the tense of what was said
originalSourceText=In English, when reporting a story, we usually change the tense of what was said: compare "I ''am'' English" with "He said he ''was'' English" - the reported verb changes tense. In Russian, however, we keep the tense the same, so the same report is literally, "He said he '''is''' English" - "'''Он сказа́л, что он англича́нин'''", not "'''Он сказа́л, что он ''был'' англича́нином'''".
shippedText=In English, when reporting a story, we usually change the tense of what was said: compare "I ''am'' English" with "He said he ''was'' English" - the reported verb changes tense. In Russian, however, we keep the tense the same, so the same report is literally, "He said he '''is''' English" - "'''Он сказа́л, что он англича́нин'''", not "'''Он сказа́л, что он ''был'' англича́нином'''".
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=0f7241309a1870c33dc74ab29501295fbb0c2cd90246dffa2d48707d457f4f8a
contentHash=0f7241309a1870c33dc74ab29501295fbb0c2cd90246dffa2d48707d457f4f8a
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u13-tense-keep-rem-r0`

- Objectives: obj-u13-tense-keep
- Sessions: u13-s-orient, u13-s-tense-keep
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 81bdaa631e7f9449f198175e1fbeb4582bb7a0b43f73e01f47707a317a7190f8

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Future_tense?oldid=3353822
sourceRevision=3353822
sourceRecordId=future:In English, when reporting a story, we usually change the tense of what was said
originalSourceText=In English, when reporting a story, we usually change the tense of what was said: compare "I ''am'' English" with "He said he ''was'' English" - the reported verb changes tense. In Russian, however, we keep the tense the same, so the same report is literally, "He said he '''is''' English" - "'''Он сказа́л, что он англича́нин'''", not "'''Он сказа́л, что он ''был'' англича́нином'''".
shippedText=Он сказа́л
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=0f7241309a1870c33dc74ab29501295fbb0c2cd90246dffa2d48707d457f4f8a
contentHash=3f1885615fe3da667d0a07d0114298cf95fb9495edbe2330311c844037160ac7
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u13-tense-keep-rem-r1`

- Objectives: obj-u13-tense-keep
- Sessions: u13-s-orient, u13-s-tense-keep
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 25796163f3711eb4dd9b89c93ef154a0f5c9ceaca0781a62e74a49d00852d356

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Future_tense?oldid=3353822
sourceRevision=3353822
sourceRecordId=future:In English, when reporting a story, we usually change the tense of what was said
originalSourceText=In English, when reporting a story, we usually change the tense of what was said: compare "I ''am'' English" with "He said he ''was'' English" - the reported verb changes tense. In Russian, however, we keep the tense the same, so the same report is literally, "He said he '''is''' English" - "'''Он сказа́л, что он англича́нин'''", not "'''Он сказа́л, что он ''был'' англича́нином'''".
shippedText=что он англича́нин
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=0f7241309a1870c33dc74ab29501295fbb0c2cd90246dffa2d48707d457f4f8a
contentHash=db573248ce368648dc8a8a72bad84cf63d5e75e59f0a0cbe59a27ad8edd7f4ba
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u13-tense-keep-rem-r2`

- Objectives: obj-u13-tense-keep
- Sessions: u13-s-orient, u13-s-tense-keep
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 732f15d9c0a16a4f462c42756da8adada76c7fec645ab87d26c87836c9889a12

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Future_tense?oldid=3353822
sourceRevision=3353822
sourceRecordId=future:In English, when reporting a story, we usually change the tense of what was said
originalSourceText=In English, when reporting a story, we usually change the tense of what was said: compare "I ''am'' English" with "He said he ''was'' English" - the reported verb changes tense. In Russian, however, we keep the tense the same, so the same report is literally, "He said he '''is''' English" - "'''Он сказа́л, что он англича́нин'''", not "'''Он сказа́л, что он ''был'' англича́нином'''".
shippedText=Он сказа́л
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=0f7241309a1870c33dc74ab29501295fbb0c2cd90246dffa2d48707d457f4f8a
contentHash=3f1885615fe3da667d0a07d0114298cf95fb9495edbe2330311c844037160ac7
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u13-tense-keep-rem-r3`

- Objectives: obj-u13-tense-keep
- Sessions: u13-s-orient, u13-s-tense-keep
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: c8d18d7cb6d472b09af04a84106a8c578c8b592b8419ed5e92bd32c47979ae80

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Future_tense?oldid=3353822
sourceRevision=3353822
sourceRecordId=future:In English, when reporting a story, we usually change the tense of what was said
originalSourceText=In English, when reporting a story, we usually change the tense of what was said: compare "I ''am'' English" with "He said he ''was'' English" - the reported verb changes tense. In Russian, however, we keep the tense the same, so the same report is literally, "He said he '''is''' English" - "'''Он сказа́л, что он англича́нин'''", not "'''Он сказа́л, что он ''был'' англича́нином'''".
shippedText=что он
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=0f7241309a1870c33dc74ab29501295fbb0c2cd90246dffa2d48707d457f4f8a
contentHash=fa158961927cdb2bc8b3a8d1d762abaf174caa1e29ddced488ad523f0809a1e3
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u13-tense-keep-rem-r4`

- Objectives: obj-u13-tense-keep
- Sessions: u13-s-orient, u13-s-tense-keep
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: ff40746a21deef9c3ce5d0a17a8c6347eba8c241279d55cf08fc24580dac9b93

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Future_tense?oldid=3353822
sourceRevision=3353822
sourceRecordId=future:In English, when reporting a story, we usually change the tense of what was said
originalSourceText=In English, when reporting a story, we usually change the tense of what was said: compare "I ''am'' English" with "He said he ''was'' English" - the reported verb changes tense. In Russian, however, we keep the tense the same, so the same report is literally, "He said he '''is''' English" - "'''Он сказа́л, что он англича́нин'''", not "'''Он сказа́л, что он ''был'' англича́нином'''".
shippedText=был
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=0f7241309a1870c33dc74ab29501295fbb0c2cd90246dffa2d48707d457f4f8a
contentHash=aaa52eb6da840c5985ec7ad40637ddbbc1555c75361c6b2b54008a66a606d244
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u13-tense-keep-rem-r5`

- Objectives: obj-u13-tense-keep
- Sessions: u13-s-orient, u13-s-tense-keep
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: f71c915a107c3e2bf6b8165dd4bd276cab919a718b1338875299ff95d927795e

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Future_tense?oldid=3353822
sourceRevision=3353822
sourceRecordId=future:In English, when reporting a story, we usually change the tense of what was said
originalSourceText=In English, when reporting a story, we usually change the tense of what was said: compare "I ''am'' English" with "He said he ''was'' English" - the reported verb changes tense. In Russian, however, we keep the tense the same, so the same report is literally, "He said he '''is''' English" - "'''Он сказа́л, что он англича́нин'''", not "'''Он сказа́л, что он ''был'' англича́нином'''".
shippedText=англича́нином
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=0f7241309a1870c33dc74ab29501295fbb0c2cd90246dffa2d48707d457f4f8a
contentHash=969ba137a791b66fedd7ad299df5213e8aad1e2331122aa125d5de0c16245f24
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u13-tense-keep-x-0`

- Objectives: obj-u13-tense-keep
- Sessions: u13-s-orient, u13-s-tense-keep
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: eae89e9c1ccadbacf8cd38242f36e17686824e8016b050a35dc16659be07837b

recordType=instruction
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Future_tense?oldid=3353822
sourceRevision=3353822
sourceRecordId=future:In English, when reporting a story, we usually change the tense of what was said
originalSourceText=In English, when reporting a story, we usually change the tense of what was said: compare "I ''am'' English" with "He said he ''was'' English" - the reported verb changes tense. In Russian, however, we keep the tense the same, so the same report is literally, "He said he '''is''' English" - "'''Он сказа́л, что он англича́нин'''", not "'''Он сказа́л, что он ''был'' англича́нином'''".
shippedText=we keep the tense the same
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=0f7241309a1870c33dc74ab29501295fbb0c2cd90246dffa2d48707d457f4f8a
contentHash=1ae9beddee18e1325dd0eb47c3fcf1e13deced48454094705ee8c8c7b1534528
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u13-tense-keep-x-1`

- Objectives: obj-u13-tense-keep
- Sessions: u13-s-orient, u13-s-tense-keep
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: c3bb55981d14f00904747f81095d80774a93315dfa23f2641f7bce207e46bf11

recordType=instruction
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Future_tense?oldid=3353822
sourceRevision=3353822
sourceRecordId=future:In English, when reporting a story, we usually change the tense of what was said
originalSourceText=In English, when reporting a story, we usually change the tense of what was said: compare "I ''am'' English" with "He said he ''was'' English" - the reported verb changes tense. In Russian, however, we keep the tense the same, so the same report is literally, "He said he '''is''' English" - "'''Он сказа́л, что он англича́нин'''", not "'''Он сказа́л, что он ''был'' англича́нином'''".
shippedText=we usually change the tense
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=0f7241309a1870c33dc74ab29501295fbb0c2cd90246dffa2d48707d457f4f8a
contentHash=513449b2d790735721ef44f17f265f059b58c67ba6c6ce4a234f9fb0a2690947
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u13-tense-keep-x-2`

- Objectives: obj-u13-tense-keep
- Sessions: u13-s-orient, u13-s-tense-keep
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 8f0192d14e7b23923b31f6a1707f82518923eb6c96d2b2cba7669d2a283745fb

recordType=instruction
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Future_tense?oldid=3353822
sourceRevision=3353822
sourceRecordId=future:In English, when reporting a story, we usually change the tense of what was said
originalSourceText=In English, when reporting a story, we usually change the tense of what was said: compare "I ''am'' English" with "He said he ''was'' English" - the reported verb changes tense. In Russian, however, we keep the tense the same, so the same report is literally, "He said he '''is''' English" - "'''Он сказа́л, что он англича́нин'''", not "'''Он сказа́л, что он ''был'' англича́нином'''".
shippedText=the reported verb changes tense
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=0f7241309a1870c33dc74ab29501295fbb0c2cd90246dffa2d48707d457f4f8a
contentHash=29713a6f6d1aeb961da97c390e7bb3391b61d022fa11c6885d847d3686cececf
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u13-tense-keep-x-3`

- Objectives: obj-u13-tense-keep
- Sessions: u13-s-orient, u13-s-tense-keep
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 77306e2bb4043850a2cc8b8752cb393b3755be87b39354a349196c8617534e2b

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Future_tense?oldid=3353822
sourceRevision=3353822
sourceRecordId=future::I will tell him in the evening - '''Я скажу ему ве́чером'''
:She said that she 
originalSourceText=:I will tell him in the evening - '''Я скажу ему ве́чером'''
:She said that she would speak to him in the evening - '''Она́ сказа́ла, что она́ ска́жет ему́ ве́чером''' - ''perfective past and future as actions completed, definite, etc.''
:When I'm in Moscow, I'll speak only in Russian - '''Когда́ я бу́ду в Москве́, я бу́ду говори́ть то́лько по-ру́сски'''
shippedText=Когда́ я бу́ду в Москве́, я бу́ду говори́ть то́лько по-ру́сски
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=50e5900972acaf1dde17a16c7795d9e8687768534625dfad94578b05153981a4
contentHash=13b6e1030f6ed933a2bb7d97a90ad219d8711dccce1afface9cbc63bdcdfb950
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u13-tense-keep-x-3-r0`

- Objectives: obj-u13-tense-keep
- Sessions: u13-s-orient, u13-s-tense-keep
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 7c2d420387759f597a63e7e36ed8de03430b4ecdbe1bbf77074ceb244a254b53

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Future_tense?oldid=3353822
sourceRevision=3353822
sourceRecordId=future::I will tell him in the evening - '''Я скажу ему ве́чером'''
:She said that she 
originalSourceText=:I will tell him in the evening - '''Я скажу ему ве́чером'''
:She said that she would speak to him in the evening - '''Она́ сказа́ла, что она́ ска́жет ему́ ве́чером''' - ''perfective past and future as actions completed, definite, etc.''
:When I'm in Moscow, I'll speak only in Russian - '''Когда́ я бу́ду в Москве́, я бу́ду говори́ть то́лько по-ру́сски'''
shippedText=Когда́ я бу́ду в Москве́
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=50e5900972acaf1dde17a16c7795d9e8687768534625dfad94578b05153981a4
contentHash=b03e4cf434996a19407a0a0b983a97efd925a0652e814b1457bf87e09d075271
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u13-tense-keep-x-3-r1`

- Objectives: obj-u13-tense-keep
- Sessions: u13-s-orient, u13-s-tense-keep
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: ef1a1358d22bffa101bbe7950bcc3bb6bc444d3801a7cc9e99a4b0885ea314ee

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Future_tense?oldid=3353822
sourceRevision=3353822
sourceRecordId=future::I will tell him in the evening - '''Я скажу ему ве́чером'''
:She said that she 
originalSourceText=:I will tell him in the evening - '''Я скажу ему ве́чером'''
:She said that she would speak to him in the evening - '''Она́ сказа́ла, что она́ ска́жет ему́ ве́чером''' - ''perfective past and future as actions completed, definite, etc.''
:When I'm in Moscow, I'll speak only in Russian - '''Когда́ я бу́ду в Москве́, я бу́ду говори́ть то́лько по-ру́сски'''
shippedText=я бу́ду говори́ть то́лько по
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=50e5900972acaf1dde17a16c7795d9e8687768534625dfad94578b05153981a4
contentHash=c452297f24fe362880c9f4e2d470876ee37eac3b5703026b74d65dd44197ce4d
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u13-tense-keep-x-3-r2`

- Objectives: obj-u13-tense-keep
- Sessions: u13-s-orient, u13-s-tense-keep
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: c09e2eecb52ae0bc3cba1200fd8f70024835decdb5cabd7553b5141bafb74e38

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Future_tense?oldid=3353822
sourceRevision=3353822
sourceRecordId=future::I will tell him in the evening - '''Я скажу ему ве́чером'''
:She said that she 
originalSourceText=:I will tell him in the evening - '''Я скажу ему ве́чером'''
:She said that she would speak to him in the evening - '''Она́ сказа́ла, что она́ ска́жет ему́ ве́чером''' - ''perfective past and future as actions completed, definite, etc.''
:When I'm in Moscow, I'll speak only in Russian - '''Когда́ я бу́ду в Москве́, я бу́ду говори́ть то́лько по-ру́сски'''
shippedText=ру́сски
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=50e5900972acaf1dde17a16c7795d9e8687768534625dfad94578b05153981a4
contentHash=b357db2b6a3f724f8e6b875eb98919fb51c150322b4866acd26a6d72dbf71d8f
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### AUTHORED_ENGLISH `en-u13-080`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 049fd1f7919262f431cf050e6cd8895636f2088ae2228eabe1078cd7df4e1740

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Admitted lines

### AUTHORED_ENGLISH `en-u13-081`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 813c287502237135cfd631ae84bbd6c86a1da80e0690223b199d17648716a39c

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Does this row fail the perfective past?

### AUTHORED_ENGLISH `en-u13-082`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 2301248f8ea9c05e04f548d3dab364271d090577f80ee186f0d2ef8ac69a66f4

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Does this answer show the perfective past?

### AUTHORED_ENGLISH `en-u13-083`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: d512215c1ed43e240c11a0b36d80cc7def0c5eb9a8f38f95502fbf0c5d309aef

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Which admitted line is the perfective past?

### AUTHORED_ENGLISH `en-u13-084`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 97e867106f4bf7fed76bedb143fab12575140efc1dc67fe227165b0647a92bd3

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Admitted instruction

### AUTHORED_ENGLISH `en-u13-085`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: a0ba9684dedf70ae60f074399d3f044c30a242f2db7139ff377e92b38d63623d

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Does this row fail the imperfective past?

### AUTHORED_ENGLISH `en-u13-086`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 13e1de530d2e01bf7a0899627189b1f7075eaf75dd856b6a29cc3b38ced2a7c1

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Does this answer show the imperfective past?

### AUTHORED_ENGLISH `en-u13-087`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 53cf5c3ef9ea4a929c9416dfe376acecaed202eddd23e80a4bf158e796b22d52

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Which admitted line is the imperfective past?

### AUTHORED_ENGLISH `en-u13-088`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 846aecbe1b3fc0945aa4958aff572ba6d17a69245c9ea5c63f471a5a95d3a1f7

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Does this row fail the masculine past?

### AUTHORED_ENGLISH `en-u13-089`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 4a5e7ca4ce3144892c9b4bfe81fac621f66afe5755455813037f390f5db23dd9

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Does this answer show the masculine past?

### AUTHORED_ENGLISH `en-u13-090`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 0ce6cd874ab820ad6b7bc76e39e0c0505386dfd1d086640c6238a5edd40a32c6

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Which admitted row is the masculine past?

### AUTHORED_ENGLISH `en-u13-091`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: ecff2e7d54e6760b958f480be935a1ca48c12599849095e4808a96be62d6555f

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Does this row fail the same tense?

### AUTHORED_ENGLISH `en-u13-092`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 612e24841eae8e4414fee775f4ddbe9aa2ddc5e6fbc44cc503d19856e04f1a7a

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Does this answer show the same tense?

### AUTHORED_ENGLISH `en-u13-093`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: e297d0bfa44f9e8cca74a31e58afeaa6b1fc1be366f579cc4819e895fafc194c

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Which admitted label keeps the same tense?

### AUTHORED_ENGLISH `en-u13-094`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 3191eacbc759e14d16f9c8eb3bb7e55d7a7870848d5e2969ec7b392b9effbb17

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Does this row fail the reported evening?

### AUTHORED_ENGLISH `en-u13-095`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: eba74e2f53205ca24f7cd9e17329971365117283eaeb67c3d57c45782caadd77

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Does this answer show the reported evening?

### AUTHORED_ENGLISH `en-u13-096`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 1dade795ce165020ccbf76631ccfd88ce4459dc0853f7cddba1b9420df5360f8

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Which admitted line is the reported evening?

### AUTHORED_ENGLISH `en-u13-097`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: f33f6c08b3d71983965a34121561e2b8a6cad63bee9ca99b14c4edf19a121158

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Does this row fail the changing the reported tense?

### AUTHORED_ENGLISH `en-u13-098`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 7fe8977c90971f888f744647c088719e5db31745be4b3ee748186b170c455037

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Does this answer show the changing the reported tense?

### AUTHORED_ENGLISH `en-u13-099`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: e64a232c79c3df90f9c9109ba9df1bef388f68593416ae344a0bed05bc0c4642

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Which admitted label is about changing the reported tense?

### AUTHORED_ENGLISH `en-u13-100`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 074f3fc68296f60189c088e170abe45b7a73f20e75d0180f3d6e22c918170ed9

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Does this row fail the perfective partner?

### AUTHORED_ENGLISH `en-u13-101`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 1bab06267f9f8d744a691ee6290ea56ada532e59412db2e6a4e9e1194f657761

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Does this answer show the perfective partner?

### AUTHORED_ENGLISH `en-u13-102`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: f99280b0551b463ec50072ea0a8603feddf1539b2bda9c8f32820775dda79378

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Which admitted line is the perfective partner?

### AUTHORED_ENGLISH `en-u13-103`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: cb78522b1c7fc6c3e01d817ba8fe66f1c62d2829042944614eb41d4fc9172a3b

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Does this row fail the imperfective partner?

### AUTHORED_ENGLISH `en-u13-104`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 854b90be8d1a4f1cbfb6f86eab09ea58719e15833d9ce9cd7dc84d58b3ba506a

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Does this answer show the imperfective partner?

### AUTHORED_ENGLISH `en-u13-105`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 5821af724bf6d3850c6744bcef7877b45bec7e2e40eea8f49f6a766ce8a69b9e

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Which admitted line is the imperfective partner?

### AUTHORED_ENGLISH `en-u13-106`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 4d82b5a861b8058640731b91a6ac65c3a785348972d4065ba087e55c6bfeff3c

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Does this row fail the direct evening?

### AUTHORED_ENGLISH `en-u13-107`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 29b328aa78749f8cc2647d3a043c530e942c1d2ff1d9e21ac9aa4328bd91e928

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Does this answer show the direct evening?

### AUTHORED_ENGLISH `en-u13-108`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: e54d726d02c9107fdf5045afe1938d566cf636a5295eb97cec3ea9eb03fa4d3d

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Which admitted line is the direct evening?

### AUTHORED_ENGLISH `en-u13-109`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: c3e7e062c0e06001f44fefeff6d0e496b1310ff044a53a15148a588eb6572eca

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Does this row fail the Moscow?

### AUTHORED_ENGLISH `en-u13-110`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: f1f9e63f30dee64cd264a8408e20065289e02fab4b3a20111e0533a2dee3591d

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Does this answer show the Moscow?

### AUTHORED_ENGLISH `en-u13-111`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: d93f605912de00e130d360c1d1786963b3ca759879b831f3e620923d3e6cad97

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Which admitted line is the future plan about Moscow?

### AUTHORED_ENGLISH `en-u13-112`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 45af619d41dd7fef275b0a1cedaadf2ecca15b79bd21ec306e833e1496099f20

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Admitted past aspect and report lines

### AUTHORED_ENGLISH `en-u13-113`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: bd37abb9f8f4175b3d8fa658e306513d3b9a3bee971cf12c2514e2d795d31a00

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Proposed Unit 13 uses frozen records for past aspect lines, report labels, and infinitive partners. Qualified review is pending.

### AUTHORED_ENGLISH `en-u13-114`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 7bac41a1d9fac344522fccec21f42b6468220dca3b71dd5d36ab0b112db5a6b7

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Open Lesson 13 in the copy you own. The app records do not replace that lesson.

### AUTHORED_ENGLISH `en-u13-115`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: e2594c73d59de5ab44eaf1c6f00808475386f4ad4489e6ed8fe6e656728a37c1

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Guided practice

### AUTHORED_ENGLISH `en-u13-116`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: de03e18eaa2287700fefae155534790df6ac2456474bc2185f34e22b9cd67c32

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Choose the admitted record. The feedback states the frozen field.

### AUTHORED_ENGLISH `en-u13-117`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 8aa3f39bb8b6cdc488d3d884bdf9d94edcd2b92696b3e19a37872aa0e9de5111

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Remediation

### AUTHORED_ENGLISH `en-u13-118`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: f6788d1d8a8f4af9e87980173a37c90516b64ab8386a42300701ffddf0859767

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Independent checks

### AUTHORED_ENGLISH `en-u13-119`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 71ebfe4b0269841da9ea38eb6a7a5b42e732ff01dc0f78acf813fb1af9895081

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=These checks use only admitted records. A miss returns to the explanation.

### AUTHORED_ENGLISH `en-u13-120`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: a40083e586f5d284c54523544dc3bc6030ba583ac6dbd2fb8cd2436d0a225452

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Work Lesson 13 in your copy

### AUTHORED_ENGLISH `en-u13-121`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: fe48121c2daf3aa8a5df3ffd527e7fa5766a15b290f6af61f215029c621af804

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Do Lesson 13 in the copy you own. Use that book's explanations and practice. The proposed records in this unit are a separate licensed set and do not copy the book. Listening stays with the book. No audio is mapped here.

### AUTHORED_ENGLISH `en-u13-122`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: afa5c7640b201aa1116500f1f32629f93570d582edfa0147886039b69ac71650

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Open Lesson 13 in your copy and complete its lesson work.

### AUTHORED_ENGLISH `en-u13-123`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: c05027d4d6db7c034bb421c4abc5e40f7c6de1b48f3e5caa26f71f3fc2f7fd2c

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Record Lesson 13 book work

### AUTHORED_ENGLISH `en-u13-124`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 0ef2ff7506b35335eb12c78741a5193488ba3766c45384e00774b8f56ec4ed8e

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=When you have done Lesson 13 in your copy, self-attest here. Attestation records your report. It does not grade the lesson, approve a mapping, or approve the proposed records.

### AUTHORED_ENGLISH `en-u13-125`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 9bd4dd5a7868bfd5e08131d08a0c967920baed0075bd008f28f3614b9ee7712b

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Self-attest only after the Lesson 13 work in your copy.

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

- `obj-u13-past-aspect` INSTRUCTION `u13-st-orient`
- `obj-u13-past-aspect` INSTRUCTION `u13-st-teach-past-aspect-1`
- `obj-u13-past-aspect` INSTRUCTION `u13-st-teach-past-aspect-2`
- `obj-u13-past-aspect` INSTRUCTION `u13-st-teach-past-aspect-3`
- `obj-u13-past-aspect` INSTRUCTION `u13-st-teach-past-aspect-4`
- `obj-u13-past-aspect` EXAMPLE `ex-u13-past-aspect-0`
- `obj-u13-past-aspect` EXAMPLE `ex-u13-past-aspect-1`
- `obj-u13-past-aspect` EXAMPLE `ex-or-u13-past-aspect-2`
- `obj-u13-past-aspect` EXAMPLE `ex-or-u13-past-aspect-3`
- `obj-u13-past-aspect` EXAMPLE `ex-or-u13-past-aspect-4`
- `obj-u13-past-aspect` PRACTICE `u13-st-guided-past-aspect-1`
- `obj-u13-past-aspect` CHECK `u13-i-past-aspect-1`
- `obj-u13-past-aspect` CHECK `u13-i-past-aspect-2`
- `obj-u13-past-aspect` REMEDIATION `u13-st-rem-past-aspect`
- `obj-u13-tense-keep` INSTRUCTION `u13-st-orient`
- `obj-u13-tense-keep` INSTRUCTION `u13-st-teach-tense-keep-1`
- `obj-u13-tense-keep` INSTRUCTION `u13-st-teach-tense-keep-2`
- `obj-u13-tense-keep` INSTRUCTION `u13-st-teach-tense-keep-3`
- `obj-u13-tense-keep` EXAMPLE `ex-u13-tense-keep-0`
- `obj-u13-tense-keep` EXAMPLE `ex-u13-tense-keep-1`
- `obj-u13-tense-keep` EXAMPLE `ex-u13-tense-keep-2`
- `obj-u13-tense-keep` PRACTICE `u13-st-guided-tense-keep-1`
- `obj-u13-tense-keep` CHECK `u13-i-tense-keep-1`
- `obj-u13-tense-keep` CHECK `u13-i-tense-keep-2`
- `obj-u13-tense-keep` REMEDIATION `u13-st-rem-tense-keep`
- `obj-u13-infinitive` INSTRUCTION `u13-st-orient`
- `obj-u13-infinitive` INSTRUCTION `u13-st-teach-infinitive-1`
- `obj-u13-infinitive` INSTRUCTION `u13-st-teach-infinitive-2`
- `obj-u13-infinitive` INSTRUCTION `u13-st-teach-infinitive-3`
- `obj-u13-infinitive` EXAMPLE `ex-u13-infinitive-0`
- `obj-u13-infinitive` EXAMPLE `ex-u13-infinitive-1`
- `obj-u13-infinitive` EXAMPLE `ex-u13-infinitive-2`
- `obj-u13-infinitive` EXAMPLE `ex-u13-infinitive-3`
- `obj-u13-infinitive` PRACTICE `u13-st-guided-infinitive-1`
- `obj-u13-infinitive` CHECK `u13-i-infinitive-1`
- `obj-u13-infinitive` CHECK `u13-i-infinitive-2`
- `obj-u13-infinitive` REMEDIATION `u13-st-rem-infinitive`
- `obj-u13-reported` INSTRUCTION `u13-st-orient`
- `obj-u13-reported` INSTRUCTION `u13-st-teach-reported-1`
- `obj-u13-reported` INSTRUCTION `u13-st-teach-reported-2`
- `obj-u13-reported` INSTRUCTION `u13-st-teach-reported-3`
- `obj-u13-reported` EXAMPLE `ex-u13-reported-0`
- `obj-u13-reported` EXAMPLE `ex-u13-reported-1`
- `obj-u13-reported` EXAMPLE `ex-u13-reported-2`
- `obj-u13-reported` EXAMPLE `ex-u13-reported-3`
- `obj-u13-reported` PRACTICE `u13-st-guided-reported-1`
- `obj-u13-reported` CHECK `u13-i-reported-1`
- `obj-u13-reported` CHECK `u13-i-reported-2`
- `obj-u13-reported` REMEDIATION `u13-st-rem-reported`
- `obj-u13-book` INSTRUCTION `u13-st-book-work`
- `obj-u13-book` INSTRUCTION `u13-st-book`
- `u13-s-orient` OBJECTIVE `obj-u13-past-aspect`
- `u13-s-orient` OBJECTIVE `obj-u13-tense-keep`
- `u13-s-orient` OBJECTIVE `obj-u13-infinitive`
- `u13-s-orient` OBJECTIVE `obj-u13-reported`
- `u13-s-orient` OBJECTIVE `obj-u13-book`
- `u13-st-orient` REFERENCE `ref-u1115-attribution`
- `u13-s-past-aspect` OBJECTIVE `obj-u13-past-aspect`
- `u13-st-teach-past-aspect-1` EXAMPLE `ex-u13-past-aspect-0`
- `u13-st-teach-past-aspect-1` EXAMPLE `ex-u13-past-aspect-1`
- `u13-st-teach-past-aspect-1` REFERENCE `ref-u1115-attribution`
- `u13-st-guided-past-aspect-1` REMEDIATION `u13-st-rem-past-aspect`
- `u13-st-guided-past-aspect-1` ITEM `u13-g-past-aspect-0`
- `u13-st-teach-past-aspect-2` REFERENCE `ref-u1115-attribution`
- `u13-st-teach-past-aspect-3` EXAMPLE `ex-or-u13-past-aspect-2`
- `u13-st-teach-past-aspect-3` EXAMPLE `ex-or-u13-past-aspect-3`
- `u13-st-teach-past-aspect-3` REFERENCE `ref-u1115-attribution`
- `u13-st-teach-past-aspect-4` EXAMPLE `ex-or-u13-past-aspect-4`
- `u13-st-teach-past-aspect-4` REFERENCE `ref-u1115-attribution`
- `u13-st-rem-past-aspect` EXAMPLE `ex-u13-past-aspect-0`
- `u13-st-rem-past-aspect` EXAMPLE `ex-u13-past-aspect-1`
- `u13-st-check-past-aspect` REMEDIATION `u13-st-rem-past-aspect`
- `u13-st-check-past-aspect` ITEM `u13-i-past-aspect-1`
- `u13-st-check-past-aspect` ITEM `u13-i-past-aspect-2`
- `u13-s-tense-keep` OBJECTIVE `obj-u13-tense-keep`
- `u13-st-teach-tense-keep-1` EXAMPLE `ex-u13-tense-keep-0`
- `u13-st-teach-tense-keep-1` EXAMPLE `ex-u13-tense-keep-1`
- `u13-st-teach-tense-keep-1` REFERENCE `ref-u1115-attribution`
- `u13-st-guided-tense-keep-1` REMEDIATION `u13-st-rem-tense-keep`
- `u13-st-guided-tense-keep-1` ITEM `u13-g-tense-keep-0`
- `u13-st-teach-tense-keep-2` REFERENCE `ref-u1115-attribution`
- `u13-st-teach-tense-keep-3` EXAMPLE `ex-u13-tense-keep-2`
- `u13-st-teach-tense-keep-3` REFERENCE `ref-u1115-attribution`
- `u13-st-rem-tense-keep` EXAMPLE `ex-u13-tense-keep-0`
- `u13-st-rem-tense-keep` EXAMPLE `ex-u13-tense-keep-1`
- `u13-st-check-tense-keep` REMEDIATION `u13-st-rem-tense-keep`
- `u13-st-check-tense-keep` ITEM `u13-i-tense-keep-1`
- `u13-st-check-tense-keep` ITEM `u13-i-tense-keep-2`
- `u13-s-infinitive` OBJECTIVE `obj-u13-infinitive`
- `u13-st-teach-infinitive-1` EXAMPLE `ex-u13-infinitive-0`
- `u13-st-teach-infinitive-1` EXAMPLE `ex-u13-infinitive-1`
- `u13-st-teach-infinitive-1` REFERENCE `ref-u1115-attribution`
- `u13-st-guided-infinitive-1` REMEDIATION `u13-st-rem-infinitive`
- `u13-st-guided-infinitive-1` ITEM `u13-g-infinitive-0`
- `u13-st-teach-infinitive-2` REFERENCE `ref-u1115-attribution`
- `u13-st-teach-infinitive-3` EXAMPLE `ex-u13-infinitive-2`
- `u13-st-teach-infinitive-3` EXAMPLE `ex-u13-infinitive-3`
- `u13-st-teach-infinitive-3` REFERENCE `ref-u1115-attribution`
- `u13-st-rem-infinitive` EXAMPLE `ex-u13-infinitive-0`
- `u13-st-rem-infinitive` EXAMPLE `ex-u13-infinitive-1`
- `u13-st-check-infinitive` REMEDIATION `u13-st-rem-infinitive`
- `u13-st-check-infinitive` ITEM `u13-i-infinitive-1`
- `u13-st-check-infinitive` ITEM `u13-i-infinitive-2`
- `u13-s-reported` OBJECTIVE `obj-u13-reported`
- `u13-st-teach-reported-1` EXAMPLE `ex-u13-reported-0`
- `u13-st-teach-reported-1` EXAMPLE `ex-u13-reported-1`
- `u13-st-teach-reported-1` REFERENCE `ref-u1115-attribution`
- `u13-st-guided-reported-1` REMEDIATION `u13-st-rem-reported`
- `u13-st-guided-reported-1` ITEM `u13-g-reported-0`
- `u13-st-teach-reported-2` REFERENCE `ref-u1115-attribution`
- `u13-st-teach-reported-3` EXAMPLE `ex-u13-reported-2`
- `u13-st-teach-reported-3` EXAMPLE `ex-u13-reported-3`
- `u13-st-teach-reported-3` REFERENCE `ref-u1115-attribution`
- `u13-st-rem-reported` EXAMPLE `ex-u13-reported-0`
- `u13-st-rem-reported` EXAMPLE `ex-u13-reported-1`
- `u13-st-check-reported` REMEDIATION `u13-st-rem-reported`
- `u13-st-check-reported` ITEM `u13-i-reported-1`
- `u13-st-check-reported` ITEM `u13-i-reported-2`
- `u13-s-book` OBJECTIVE `obj-u13-book`
- `u13-st-book-work` REFERENCE `ref-u1115-attribution`
- `obj-u13-past-aspect` PROVENANCE `prov-ex-or-u13-past-aspect-2`
- `obj-u13-past-aspect` PROVENANCE `prov-ex-or-u13-past-aspect-2-gloss`
- `obj-u13-past-aspect` PROVENANCE `prov-ex-or-u13-past-aspect-3`
- `obj-u13-past-aspect` PROVENANCE `prov-ex-or-u13-past-aspect-3-gloss`
- `obj-u13-past-aspect` PROVENANCE `prov-ex-or-u13-past-aspect-4`
- `obj-u13-past-aspect` PROVENANCE `prov-ex-or-u13-past-aspect-4-gloss`
- `obj-u13-infinitive` PROVENANCE `prov-ex-u13-infinitive-0-en`
- `obj-u13-infinitive` PROVENANCE `prov-ex-u13-infinitive-0-ru`
- `obj-u13-infinitive` PROVENANCE `prov-ex-u13-infinitive-1-en`
- `obj-u13-infinitive` PROVENANCE `prov-ex-u13-infinitive-1-ru`
- `obj-u13-infinitive` PROVENANCE `prov-ex-u13-infinitive-2-en`
- `obj-u13-infinitive` PROVENANCE `prov-ex-u13-infinitive-2-ru`
- `obj-u13-infinitive` PROVENANCE `prov-ex-u13-infinitive-3-en`
- `obj-u13-infinitive` PROVENANCE `prov-ex-u13-infinitive-3-ru`
- `obj-u13-past-aspect` PROVENANCE `prov-ex-u13-past-aspect-0-en`
- `obj-u13-past-aspect` PROVENANCE `prov-ex-u13-past-aspect-0-ru`
- `obj-u13-past-aspect` PROVENANCE `prov-ex-u13-past-aspect-1-en`
- `obj-u13-past-aspect` PROVENANCE `prov-ex-u13-past-aspect-1-ru`
- `obj-u13-reported` PROVENANCE `prov-ex-u13-reported-0-en`
- `obj-u13-reported` PROVENANCE `prov-ex-u13-reported-0-ru`
- `obj-u13-reported` PROVENANCE `prov-ex-u13-reported-0-ru-r0`
- `obj-u13-reported` PROVENANCE `prov-ex-u13-reported-0-ru-r1`
- `obj-u13-reported` PROVENANCE `prov-ex-u13-reported-1-en`
- `obj-u13-reported` PROVENANCE `prov-ex-u13-reported-1-ru`
- `obj-u13-reported` PROVENANCE `prov-ex-u13-reported-2-en`
- `obj-u13-reported` PROVENANCE `prov-ex-u13-reported-2-ru`
- `obj-u13-reported` PROVENANCE `prov-ex-u13-reported-2-ru-r0`
- `obj-u13-reported` PROVENANCE `prov-ex-u13-reported-2-ru-r1`
- `obj-u13-reported` PROVENANCE `prov-ex-u13-reported-3-en`
- `obj-u13-reported` PROVENANCE `prov-ex-u13-reported-3-ru`
- `obj-u13-reported` PROVENANCE `prov-ex-u13-reported-3-ru-r0`
- `obj-u13-reported` PROVENANCE `prov-ex-u13-reported-3-ru-r1`
- `obj-u13-reported` PROVENANCE `prov-ex-u13-reported-3-ru-r2`
- `obj-u13-reported` PROVENANCE `prov-ex-u13-reported-3-ru-r3`
- `obj-u13-tense-keep` PROVENANCE `prov-ex-u13-tense-keep-0-en`
- `obj-u13-tense-keep` PROVENANCE `prov-ex-u13-tense-keep-0-ru`
- `obj-u13-tense-keep` PROVENANCE `prov-ex-u13-tense-keep-0-ru-r0`
- `obj-u13-tense-keep` PROVENANCE `prov-ex-u13-tense-keep-0-ru-r1`
- `obj-u13-tense-keep` PROVENANCE `prov-ex-u13-tense-keep-1-en`
- `obj-u13-tense-keep` PROVENANCE `prov-ex-u13-tense-keep-1-ru`
- `obj-u13-tense-keep` PROVENANCE `prov-ex-u13-tense-keep-2-en`
- `obj-u13-tense-keep` PROVENANCE `prov-ex-u13-tense-keep-2-ru`
- `obj-u13-tense-keep` PROVENANCE `prov-ex-u13-tense-keep-2-ru-r0`
- `obj-u13-tense-keep` PROVENANCE `prov-ex-u13-tense-keep-2-ru-r1`
- `obj-u13-infinitive` PROVENANCE `prov-u13-infinitive-rem`
- `obj-u13-infinitive` PROVENANCE `prov-u13-infinitive-rem-r0`
- `obj-u13-infinitive` PROVENANCE `prov-u13-infinitive-rem-r1`
- `obj-u13-infinitive` PROVENANCE `prov-u13-infinitive-rem-r2`
- `obj-u13-infinitive` PROVENANCE `prov-u13-infinitive-rem-r3`
- `obj-u13-past-aspect` PROVENANCE `prov-u13-past-aspect-rem`
- `obj-u13-past-aspect` PROVENANCE `prov-u13-past-aspect-rem-r0`
- `obj-u13-past-aspect` PROVENANCE `prov-u13-past-aspect-rem-r1`
- `obj-u13-past-aspect` PROVENANCE `prov-u13-past-aspect-x-0`
- `obj-u13-past-aspect` PROVENANCE `prov-u13-past-aspect-x-1`
- `obj-u13-reported` PROVENANCE `prov-u13-reported-rem`
- `obj-u13-reported` PROVENANCE `prov-u13-reported-rem-r0`
- `obj-u13-reported` PROVENANCE `prov-u13-reported-rem-r1`
- `obj-u13-reported` PROVENANCE `prov-u13-reported-rem-r2`
- `obj-u13-reported` PROVENANCE `prov-u13-reported-rem-r3`
- `obj-u13-reported` PROVENANCE `prov-u13-reported-rem-r4`
- `obj-u13-reported` PROVENANCE `prov-u13-reported-rem-r5`
- `obj-u13-reported` PROVENANCE `prov-u13-reported-x-0`
- `obj-u13-reported` PROVENANCE `prov-u13-reported-x-1`
- `obj-u13-reported` PROVENANCE `prov-u13-reported-x-1-r0`
- `obj-u13-reported` PROVENANCE `prov-u13-reported-x-1-r1`
- `obj-u13-reported` PROVENANCE `prov-u13-reported-x-1-r2`
- `obj-u13-tense-keep` PROVENANCE `prov-u13-tense-keep-rem`
- `obj-u13-tense-keep` PROVENANCE `prov-u13-tense-keep-rem-r0`
- `obj-u13-tense-keep` PROVENANCE `prov-u13-tense-keep-rem-r1`
- `obj-u13-tense-keep` PROVENANCE `prov-u13-tense-keep-rem-r2`
- `obj-u13-tense-keep` PROVENANCE `prov-u13-tense-keep-rem-r3`
- `obj-u13-tense-keep` PROVENANCE `prov-u13-tense-keep-rem-r4`
- `obj-u13-tense-keep` PROVENANCE `prov-u13-tense-keep-rem-r5`
- `obj-u13-tense-keep` PROVENANCE `prov-u13-tense-keep-x-0`
- `obj-u13-tense-keep` PROVENANCE `prov-u13-tense-keep-x-1`
- `obj-u13-tense-keep` PROVENANCE `prov-u13-tense-keep-x-2`
- `obj-u13-tense-keep` PROVENANCE `prov-u13-tense-keep-x-3`
- `obj-u13-tense-keep` PROVENANCE `prov-u13-tense-keep-x-3-r0`
- `obj-u13-tense-keep` PROVENANCE `prov-u13-tense-keep-x-3-r1`
- `obj-u13-tense-keep` PROVENANCE `prov-u13-tense-keep-x-3-r2`

## Book-directed experiences

- `u13-st-orient`
- `u13-st-book-work`
- `u13-st-book`

## Unavailable experiences

None.


## Unresolved relationships

None. Every referenced Unit 13 identifier resolves.
