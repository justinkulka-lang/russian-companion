# Unit 3 linguistic and owned-edition mapping packet

This packet prepares review. It records an unsigned structural mapping proposal. It does not record a qualified review, an approved edition alignment, or an install decision.

The owned-edition mapping worksheet separates observed edition facts, the proposed lesson and printed page range, unresolved edition questions, and matters that still require qualified linguistic review. ISBN, owned-copy verification, variance, reviewer identity, qualification, date, and approval stay blank. The disposition remains PENDING_CURRICULUM_REVIEWER.

Unit 3 remains in development. Its readiness is content-complete and review-pending. The Lesson 3 relationship is a proposed, unsigned mapping. It is not an approved edition alignment.

## Package identity

- Schema version: 3
- Application: dev.russiancompanion
- Version: 2.0.0-dev (5)
- Curriculum source commit: 81933b52d85153ea848f63125ea3b1afe12de83d
- Unit content-review SHA-256: 5480d8e87a1651d1bc16ad6c5a0856724f7bad1b746a62bbfab2bb60f3cc3e0b
- Mapping-package SHA-256: e40e0ce030f9221b6e2fceba66fe946d85ad172703c47bd34d795e77e43120b4
- Pack content-review SHA-256: 3d1a191e3f876304daa096c398d93ee6526e4ed062ea4aa1b8d77dd7c4bde025
- Review-package SHA-256: 304ce17f2b7471b82964b394e95b40f8d9bf9d33a6796024d93dee595cfb5d8c
- Repository source SHA-256: 8388621f700c87fc827ad859f7c98aa809b448f23510eb4569ff62569715fff0

`unitContentReviewDigest` binds this unit's instructional curriculum. It covers this unit and the reachable instructional records, relationships, shared dependencies, sources, rights, assets, and advancement rule. It does not cover any other unit. Recording a review status, reviewer signature, mapping approval, install state, or readiness does not change it.

`mappingPackageDigest` binds the proposed edition-mapping facts for Unit 3: edition identity, lesson, printed page range, topic description, observation source, ISBN observation state, and the pending disposition. Reviewer identity, qualification, date, notes, correction notes, and signature stay outside that digest.

`packContentReviewDigest` is the repository aggregate of instructional curriculum. It is not an approval of this unit, and it is not part of the review-package digest.

`reviewPackageDigest` identifies this complete pending review package. It binds the ordered instructional records and the mapping-package digest. Changing a proposed page range or topic changes the mapping-package digest and therefore this package digest.

`repositorySourceDigest` binds repository and release inputs. Recording an approval later changes that repository hash. That change does not replace the signed content-review digest or the mapping-package digest.

The marker scan used by this package detects only a fixed list of known prohibited mapping-input markers. It is not comprehensive copyright detection and cannot recognize every copy or close paraphrase of protected material.

## Unit

number=3
title=Everyday phrases, naming things, basic grammar, and gender
topicSummary=Proposed Unit 3 is a development unit awaiting qualified review. It contains admitted records for gender, everyday phrases, and naming, plus separate book self-attestation. It is not installed and the owned-edition mapping is not signed.
installState=DEVELOPMENT
readiness=CONTENT_COMPLETE_REVIEW_PENDING
mappingStatus=PENDING_CURRICULUM_REVIEWER
mappingNote=Proposed Lesson 3 mapping remains pending. The structural edition facts are unchanged. This proposed curriculum is not an approved alignment.
penguinLessonField=3
prerequisites=2
estimatedMinutes=54

## Not yet approved

No reviewer has signed this packet. Reviewer identity, qualification, review date, disposition, and reviewed digest are blank. Do not treat this development unit as reviewed, installable, or installed.

## Records

### UNIT_REVIEW `rev-pending-unit-3`

- Objectives: none
- Sessions: none
- Source: none
- Rights: none
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 63441e1b079ca581b64d3383588b7fcccd89a705d940e76033d23dd5d66aff02

status=IMPLEMENTED_PENDING_FINAL_AUDIT
reviewer=
reviewerQualification=
packVersion=
reviewedContentDigest=
curriculumMappingId=
notes=No qualified reviewer has signed Unit 3.

### UNIT `unit-3`

- Objectives: obj-u3-gender, obj-u3-phrases, obj-u3-naming, obj-u3-book
- Sessions: u3-s-orient, u3-s-gender, u3-s-phrases, u3-s-naming, u3-s-book
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 3e0af417eb29ca730604c6461a8e1288bc66ec4dcb3d9624c45d42dba4a9d3cf

number=3
title=Everyday phrases, naming things, basic grammar, and gender
topicSummary=Proposed Unit 3 is a development unit awaiting qualified review. It contains admitted records for gender, everyday phrases, and naming, plus separate book self-attestation. It is not installed and the owned-edition mapping is not signed.
installState=DEVELOPMENT
readiness=CONTENT_COMPLETE_REVIEW_PENDING
mappingStatus=PENDING_CURRICULUM_REVIEWER
mappingNote=Proposed Lesson 3 mapping remains pending. The structural edition facts are unchanged. This proposed curriculum is not an approved alignment.
penguinLessonField=3
prerequisites=2
estimatedMinutes=54

### ADVANCEMENT `unit-3-advancement`

- Objectives: none
- Sessions: none
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 8f6463c19bf2802ebf2882d679c1143960d20c9d2d2fad1dcd8b3f9f48542689

requireInstruction=true
requireBookSelfAttestation=true
requireAppChecks=true
minimumDistinctChecks=12
requireDueReview=true
tutorOptional=true

### OBJECTIVE `obj-u3-gender`

- Objectives: obj-u3-gender
- Sessions: none
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: b7e91492357d4d233cbc604beaadaf3aec5cd04db1e2086cef3b0e56b6d2d06e

title=Recognize admitted noun genders
kind=RECOGNITION
unitNumber=3
instruction=u3-st-orient,u3-st-teach-gender-1,u3-st-teach-gender-2,u3-st-teach-gender-3,u3-st-teach-gender-4,u3-st-teach-gender-5
examples=ex-u3-стол,ex-u3-дом,ex-u3-город,ex-u3-брат,ex-u3-книга,ex-u3-вода,ex-u3-школа,ex-u3-работа,ex-u3-окно,ex-u3-письмо,ex-u3-молоко,ex-u3-море,ex-u3-мужчина,ex-u3-дядя
practice=u3-st-guided-gender-2,u3-st-guided-gender-3,u3-st-guided-gender-4,u3-st-guided-gender-5
checks=u3-i-gender-город,u3-i-gender-друг,u3-i-gender-дверь,u3-i-gender-море,u3-i-gender-дядя
remediation=u3-st-rem-gender

### OBJECTIVE `obj-u3-phrases`

- Objectives: obj-u3-phrases
- Sessions: none
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: f74845210e6c98c9d9bdf2a5a1f5ae2462e4b7b8f1d990d9a05e20015a4ac752

title=Recognize admitted everyday phrases
kind=RECOGNITION
unitNumber=3
instruction=u3-st-orient,u3-st-teach-phrases-1,u3-st-teach-phrases-2,u3-st-teach-phrases-3
examples=ex-u3-phrase-здравствуйте,ex-u3-phrase-спасибо,ex-u3-phrase-пожалуйста,ex-u3-phrase-да,ex-u3-phrase-нет,ex-u3-phrase-это,ex-u3-phrase-kak-dela,ex-u3-phrase-gde-vy
practice=u3-st-guided-phrases-1
checks=u3-i-phrase-пожалуйста,u3-i-phrase-да,u3-i-phrase-нет,u3-i-phrase-это
remediation=u3-st-rem-phrases

### OBJECTIVE `obj-u3-naming`

- Objectives: obj-u3-naming
- Sessions: none
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 5316e10fb95ecc356e71745ef2a505d798aaf636dd0dd6b30a8126529e669403

title=Recognize admitted naming items
kind=RECOGNITION
unitNumber=3
instruction=u3-st-orient,u3-st-teach-naming-1,u3-st-teach-naming-2
examples=ex-u3-phrase-moy-stol,ex-u3-phrase-moya-sobaka,ex-u3-phrase-chto-eto,ex-u3-phrase-kto-eto
practice=u3-st-guided-naming-1
checks=u3-i-name-moya-sobaka,u3-i-name-chto-eto,u3-i-name-kto-eto
remediation=u3-st-rem-naming

### OBJECTIVE `obj-u3-book`

- Objectives: obj-u3-book
- Sessions: none
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 80caa363d4759bd73544ceef25e5571b9af9a919476c942494541c87dff7bb6d

title=Self-attest Lesson 3 book work
kind=SELF_ATTESTATION
unitNumber=3
instruction=u3-st-book-work,u3-st-book
examples=
practice=
checks=
remediation=

### SESSION `u3-s-orient`

- Objectives: obj-u3-gender, obj-u3-phrases, obj-u3-naming, obj-u3-book
- Sessions: u3-s-orient
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: b8413f107c021c845f4efc46fd6648bc3317f3fb3c20b14a0c879d7d82728c2b

title=Everyday phrases, naming, and grouping
estimatedMinutes=8
objectives=obj-u3-gender,obj-u3-phrases,obj-u3-naming,obj-u3-book
steps=u3-st-orient

### STEP `u3-st-orient`

- Objectives: obj-u3-gender, obj-u3-phrases, obj-u3-naming, obj-u3-book
- Sessions: u3-s-orient
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 91b8e688a91d3ba5e153da821e982e78a89f7193729e48b2f34c6567ce9b7615

kind=ORIENT
title=Everyday phrases, naming, and grouping
body=Proposed Unit 3 uses frozen dictionary rows and grammar excerpts for the groups, a small phrase set, and naming items. The owned book remains separate work. Qualified review is pending.
dominantAction=Continue
references=ref-u35-attribution
items=
examples=
unavailableReason=
bookDirection=Open Lesson 3 in the copy you own. The app records do not replace that lesson.
companionDirection=
remediationStepId=

### SESSION `u3-s-gender`

- Objectives: obj-u3-gender
- Sessions: u3-s-gender
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 606e90b8c01d1699253d6879b74701d4126d094efe058bb0b3961b13ae300b9e

title=Recognize admitted noun genders
estimatedMinutes=12
objectives=obj-u3-gender
steps=u3-st-teach-gender-1,u3-st-teach-gender-2,u3-st-guided-gender-2,u3-st-teach-gender-3,u3-st-guided-gender-3,u3-st-teach-gender-4,u3-st-guided-gender-4,u3-st-teach-gender-5,u3-st-guided-gender-5,u3-st-rem-gender,u3-st-check-gender

### STEP `u3-st-teach-gender-1`

- Objectives: obj-u3-gender
- Sessions: u3-s-gender
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 02fb6394c37ccdeb3634aa4dfdb95a86676abd8d9055cdca165394073bbf351c

kind=TEACH
title=Read the admitted grouping sentence
body=The frozen page says: So, in Russian, nouns have an intrinsic property called 'gender', which comes in three types: masculine, feminine, and neuter. An ending is not a substitute for the admitted record.
dominantAction=Continue
references=ref-u35-attribution
items=
examples=
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u3-st-teach-gender-2`

- Objectives: obj-u3-gender
- Sessions: u3-s-gender
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 1866eea25696819056a7e039893c56dfeecca62f5f13c1b113e68ed4c17d2d7a

kind=TEACH
title=Four records in the first group
body=These admitted records are listed as masculine. стол — table. дом — house. го́род — town. брат — brother. 
dominantAction=Continue
references=ref-u35-attribution
items=
examples=ex-u3-стол,ex-u3-дом,ex-u3-город,ex-u3-брат
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u3-st-guided-gender-2`

- Objectives: obj-u3-gender
- Sessions: u3-s-gender
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 1bda3ae8cda6c5c67b1b95380266daeaa8eb08cb64f8f5949dfb87c88f71cfb5

kind=GUIDED
title=Guided practice
body=Choose the admitted record. The feedback states the frozen field.
dominantAction=Continue
references=
items=u3-i-gender-стол,u3-i-gender-дом
examples=
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=u3-st-rem-gender

### STEP `u3-st-teach-gender-3`

- Objectives: obj-u3-gender
- Sessions: u3-s-gender
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: adcf124a83e0dbcaf1c2317ee31c5e984279ce59e8d5fc2aba323020a9925d4f

kind=TEACH
title=Four records in the second group
body=These admitted records are listed as feminine. кни́га — book. вода́ — water. шко́ла — school. рабо́та — work. 
dominantAction=Continue
references=ref-u35-attribution
items=
examples=ex-u3-книга,ex-u3-вода,ex-u3-школа,ex-u3-работа
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u3-st-guided-gender-3`

- Objectives: obj-u3-gender
- Sessions: u3-s-gender
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: eced7d660a9796128ceb08d48d0977b48a16d979cd406ebd47a7978e37d0d365

kind=GUIDED
title=Guided practice
body=Choose the admitted record. The feedback states the frozen field.
dominantAction=Continue
references=
items=u3-i-gender-книга
examples=
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=u3-st-rem-gender

### STEP `u3-st-teach-gender-4`

- Objectives: obj-u3-gender
- Sessions: u3-s-gender
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 71dcbb45b3a95dae4cbe873e725f4c349fc975cfc7e8f7ed442e96c0dfaf2e55

kind=TEACH
title=Four records in the third group
body=These admitted records are listed as neuter. окно́ — window. письмо́ — letter. молоко́ — milk. мо́ре — sea. 
dominantAction=Continue
references=ref-u35-attribution
items=
examples=ex-u3-окно,ex-u3-письмо,ex-u3-молоко,ex-u3-море
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u3-st-guided-gender-4`

- Objectives: obj-u3-gender
- Sessions: u3-s-gender
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 76d3e01fee43791ba8f45f537bbac179a8fc704a11d6f18ce53ef804d6489430

kind=GUIDED
title=Guided practice
body=Choose the admitted record. The feedback states the frozen field.
dominantAction=Continue
references=
items=u3-i-gender-окно
examples=
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=u3-st-rem-gender

### STEP `u3-st-teach-gender-5`

- Objectives: obj-u3-gender
- Sessions: u3-s-gender
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: b49dd2b7dad73eafd9af9b011d8851e4a12adef90b509df6f4bc2ccbd596b7bd

kind=TEACH
title=Two records the page still places in the first group
body=дя́дя and мужчи́на decline like nouns ending in -а, and they are masculine. Say decline for these nouns. Do not say they conjugate.
dominantAction=Continue
references=ref-u35-attribution
items=
examples=ex-u3-мужчина,ex-u3-дядя
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u3-st-guided-gender-5`

- Objectives: obj-u3-gender
- Sessions: u3-s-gender
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: b109266e73513f4bbfe2d9169a062330de09837d3a30b2846b622aec008f8ede

kind=GUIDED
title=Guided practice
body=Choose the admitted record. The feedback states the frozen field.
dominantAction=Continue
references=
items=u3-i-gender-мужчина
examples=
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=u3-st-rem-gender

### STEP `u3-st-rem-gender`

- Objectives: obj-u3-gender
- Sessions: u3-s-gender
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 40f036ac502ce1f6586c1df6398fa68a37547e107ee68140311b75eb5ee79cfc

kind=REMEDIATE
title=Remediation
body=дя́дя and мужчи́на decline like nouns ending in -а, and they are masculine. Say decline for these nouns. Do not say they conjugate.
dominantAction=Continue
references=
items=
examples=ex-u3-стол,ex-u3-дом
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u3-st-check-gender`

- Objectives: obj-u3-gender
- Sessions: u3-s-gender
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 4d7627abd4cc2740e87dc7e952bc3641e904862311a65e7a534e38410dadf1c0

kind=CHECK
title=Independent checks
body=These checks use only admitted records. A miss returns to the explanation.
dominantAction=Continue
references=
items=u3-i-gender-город,u3-i-gender-друг,u3-i-gender-дверь,u3-i-gender-море,u3-i-gender-дядя
examples=
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=u3-st-rem-gender

### SESSION `u3-s-phrases`

- Objectives: obj-u3-phrases
- Sessions: u3-s-phrases
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 46ade31473c71ce40707df1edd8077d98913f91e49ec61285febd2834cc916f2

title=Recognize admitted everyday phrases
estimatedMinutes=12
objectives=obj-u3-phrases
steps=u3-st-teach-phrases-1,u3-st-guided-phrases-1,u3-st-teach-phrases-2,u3-st-teach-phrases-3,u3-st-rem-phrases,u3-st-check-phrases

### STEP `u3-st-teach-phrases-1`

- Objectives: obj-u3-phrases
- Sessions: u3-s-phrases
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: d0d260a8a4698e004079f18cd3ad5fea184390350131fc6c25fe02f5671718e0

kind=TEACH
title=Three short greetings
body=These greetings are copied from admitted rows. здра́вствуйте — hello. спаси́бо — thank you. пожа́луйста — please. 
dominantAction=Continue
references=ref-u35-attribution
items=
examples=ex-u3-phrase-здравствуйте,ex-u3-phrase-спасибо,ex-u3-phrase-пожалуйста
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u3-st-guided-phrases-1`

- Objectives: obj-u3-phrases
- Sessions: u3-s-phrases
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 56079e04c5a496ded165a57479d69332ff947bd4965b662825b417913cb910e6

kind=GUIDED
title=Guided practice
body=Choose the admitted record. The feedback states the frozen field.
dominantAction=Continue
references=
items=u3-i-phrase-здравствуйте,u3-i-phrase-спасибо
examples=
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=u3-st-rem-phrases

### STEP `u3-st-teach-phrases-2`

- Objectives: obj-u3-phrases
- Sessions: u3-s-phrases
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 38d1e71c401e6e690dc81a259d6359dabb67b10b5743513e8db7fede2843fe11

kind=TEACH
title=Three short answers
body=These short answers are copied from admitted rows. да — yes. нет — no. э́то — this is. 
dominantAction=Continue
references=ref-u35-attribution
items=
examples=ex-u3-phrase-да,ex-u3-phrase-нет,ex-u3-phrase-это
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u3-st-teach-phrases-3`

- Objectives: obj-u3-phrases
- Sessions: u3-s-phrases
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 12b973fa091a8e4c144ab1a82b0c5a4880b0ed334acbeb7c2f0742f454290afb

kind=TEACH
title=Two written questions from the frozen page
body=These written questions are exact frozen excerpts. Как дела? — How do you do?/How're things?. Где вы? — Where are you?. 
dominantAction=Continue
references=ref-u35-attribution
items=
examples=ex-u3-phrase-kak-dela,ex-u3-phrase-gde-vy
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u3-st-rem-phrases`

- Objectives: obj-u3-phrases
- Sessions: u3-s-phrases
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 9c7cd37ea77163d3cbe70adfb88a2e86cbab18cd87cfdf49fb599c76d3e95487

kind=REMEDIATE
title=Remediation
body=Return to the explanation. Compare the shown form with the admitted record. Do not invent a form that is absent from the record.
dominantAction=Continue
references=
items=
examples=ex-u3-phrase-здравствуйте,ex-u3-phrase-спасибо
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u3-st-check-phrases`

- Objectives: obj-u3-phrases
- Sessions: u3-s-phrases
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: ebdbe57e0334955fbf2b8cbd7b2aa89770b5a43e6f08df1656378cf322608364

kind=CHECK
title=Independent checks
body=These checks use only admitted records. A miss returns to the explanation.
dominantAction=Continue
references=
items=u3-i-phrase-пожалуйста,u3-i-phrase-да,u3-i-phrase-нет,u3-i-phrase-это
examples=
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=u3-st-rem-phrases

### SESSION `u3-s-naming`

- Objectives: obj-u3-naming
- Sessions: u3-s-naming
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: b9d2a97c85f274f2132ab371294ba1971855e520ca403825368b8f8bfae03627

title=Recognize admitted naming items
estimatedMinutes=12
objectives=obj-u3-naming
steps=u3-st-teach-naming-1,u3-st-guided-naming-1,u3-st-teach-naming-2,u3-st-rem-naming,u3-st-check-naming

### STEP `u3-st-teach-naming-1`

- Objectives: obj-u3-naming
- Sessions: u3-s-naming
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 1490c95efdb517a92773be4f8b4d2b54e23b9980039ea6d403480a68c36db983

kind=TEACH
title=Two naming items from the frozen page
body=These two items are exact frozen excerpts. Do not substitute another word into them. мой стол — my table. моя собака — my dog. 
dominantAction=Continue
references=ref-u35-attribution
items=
examples=ex-u3-phrase-moy-stol,ex-u3-phrase-moya-sobaka
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u3-st-guided-naming-1`

- Objectives: obj-u3-naming
- Sessions: u3-s-naming
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 9a209f19428b0459abfedad439f2f051b4125732cd5da4568bb450dfbf7bd896

kind=GUIDED
title=Guided practice
body=Choose the admitted record. The feedback states the frozen field.
dominantAction=Continue
references=
items=u3-i-name-moy-stol
examples=
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=u3-st-rem-naming

### STEP `u3-st-teach-naming-2`

- Objectives: obj-u3-naming
- Sessions: u3-s-naming
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 76a95c8b512f7f00639d3ad537507f83c1b516aa1a79ffac912d423d4f722532

kind=TEACH
title=Two naming questions from the frozen page
body=These two questions are exact frozen excerpts. Что это? — What is this?. Кто это? — Who is this?. 
dominantAction=Continue
references=ref-u35-attribution
items=
examples=ex-u3-phrase-chto-eto,ex-u3-phrase-kto-eto
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u3-st-rem-naming`

- Objectives: obj-u3-naming
- Sessions: u3-s-naming
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: d6cba4bbc16fc9d268d6baab37593cd12171b371a088518982c2899d643222b2

kind=REMEDIATE
title=Remediation
body=Return to the explanation. Compare the shown form with the admitted record. Do not invent a form that is absent from the record.
dominantAction=Continue
references=
items=
examples=ex-u3-phrase-moy-stol,ex-u3-phrase-moya-sobaka
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u3-st-check-naming`

- Objectives: obj-u3-naming
- Sessions: u3-s-naming
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: ee59c5bc2b8f18db486f51b33dbcc44bf87d5cfe64c3f4c493e9f920e777e70e

kind=CHECK
title=Independent checks
body=These checks use only admitted records. A miss returns to the explanation.
dominantAction=Continue
references=
items=u3-i-name-moya-sobaka,u3-i-name-chto-eto,u3-i-name-kto-eto
examples=
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=u3-st-rem-naming

### SESSION `u3-s-book`

- Objectives: obj-u3-book
- Sessions: u3-s-book
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: acdd90e17608398e7ccbc761714305e4dab6009bd800f4a2c1104000d943ae2a

title=Self-attest Lesson 3
estimatedMinutes=10
objectives=obj-u3-book
steps=u3-st-book-work,u3-st-book,u3-st-anki-direction,u3-st-binder-direction,u3-st-tutor-direction,u3-st-cumulative-direction

### STEP `u3-st-book-work`

- Objectives: obj-u3-book
- Sessions: u3-s-book
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 394631eab3a6ffbe8dd958a9c19c8f632f13c31a963332e7a682db7b54dc5246

kind=BOOK
title=Work Lesson 3 in your copy
body=Do Lesson 3 in the copy you own. Use that book's explanations and practice. The proposed records in this unit are a separate licensed set and do not copy the book.
dominantAction=Continue
references=ref-book-topics,ref-u35-attribution
items=
examples=
unavailableReason=
bookDirection=Owned-book assignment for Lesson 3: printed pages 24-35. PDF indexes 36-47 are file locators, not printed pages. Observed section locators: §3.1 p24 (PDF 36); §3.2 p25 (PDF 37); §3.3 p25 (PDF 37); §3.4 p26 (PDF 38); §3.5 p26 (PDF 38); §3.6 p26 (PDF 38); §3.7 p26 (PDF 38); §3.8 p27 (PDF 39); §3.9 p29 (PDF 41); §3.10 p29 (PDF 41); §3.11 p31 (PDF 43); §3.12 p32 (PDF 44). Observed exercise locators: 3/2 p32/PDF44; 3/3 p33/PDF45; 3/4 p33/PDF45; 3/5 p33/PDF45; 3/6 p33/PDF45. These locators are pending reviewer assignment and are not an approved mapping. Do the work in the copy you own. Book activity is not a demonstrated app check.
companionDirection=
remediationStepId=

### STEP `u3-st-book`

- Objectives: obj-u3-book
- Sessions: u3-s-book
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 2e39422443ea578ac6c2ab2bcf9758d62470015f591f0f0462e1a002d9736b1b

kind=BOOK
title=Record Lesson 3 book work
body=When you have done Lesson 3 in your copy, self-attest here. Attestation records your report. It does not grade the lesson, approve a mapping, or approve the proposed records.
dominantAction=Continue
references=
items=
examples=
unavailableReason=
bookDirection=Owned-book assignment for Lesson 3: printed pages 24-35. PDF indexes 36-47 are file locators, not printed pages. Observed section locators: §3.1 p24 (PDF 36); §3.2 p25 (PDF 37); §3.3 p25 (PDF 37); §3.4 p26 (PDF 38); §3.5 p26 (PDF 38); §3.6 p26 (PDF 38); §3.7 p26 (PDF 38); §3.8 p27 (PDF 39); §3.9 p29 (PDF 41); §3.10 p29 (PDF 41); §3.11 p31 (PDF 43); §3.12 p32 (PDF 44). Observed exercise locators: 3/2 p32/PDF44; 3/3 p33/PDF45; 3/4 p33/PDF45; 3/5 p33/PDF45; 3/6 p33/PDF45. These locators are pending reviewer assignment and are not an approved mapping. Do the work in the copy you own. Book activity is not a demonstrated app check.
companionDirection=
remediationStepId=

### STEP `u3-st-anki-direction`

- Objectives: obj-u3-book
- Sessions: u3-s-book
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: dd6c006439f8febdd3829c88ccef3139fe6735fe56d165251ce0bc79ad246579

kind=ANKI
title=Unit 3 AnkiDroid direction
body=Unit 3 AnkiDroid self-attestation uses only cards you are allowed to make. Binder: write one form from this unit that you missed and the correction you will use. Tutor conversation is optional and is not a demonstrated check. Cumulative review revisits completed objectives from earlier units. Activity is not demonstrated ability. Open the offline grammar reference for this unit when one is linked.
dominantAction=Self-attest
references=
items=
examples=
unavailableReason=
bookDirection=
companionDirection=Self-attest AnkiDroid work. It remains user-reported and does not demonstrate a check.
remediationStepId=

### STEP `u3-st-binder-direction`

- Objectives: obj-u3-book
- Sessions: u3-s-book
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 6c8d9b38456b3eff53f22827ff0f44e832f73abc50e189bfd06c87c5de9792a9

kind=BINDER
title=Unit 3 binder prompt
body=Unit 3 AnkiDroid self-attestation uses only cards you are allowed to make. Binder: write one form from this unit that you missed and the correction you will use. Tutor conversation is optional and is not a demonstrated check. Cumulative review revisits completed objectives from earlier units. Activity is not demonstrated ability. Open the offline grammar reference for this unit when one is linked.
dominantAction=Continue
references=
items=
examples=
unavailableReason=
bookDirection=
companionDirection=A binder prompt is one specific note, not a score.
remediationStepId=

### STEP `u3-st-tutor-direction`

- Objectives: obj-u3-book
- Sessions: u3-s-book
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: abda439968b4faf25764d507c1489678e6abddbc752104a4c6eb83492e540f10

kind=TUTOR
title=Unit 3 optional tutor
body=Unit 3 AnkiDroid self-attestation uses only cards you are allowed to make. Binder: write one form from this unit that you missed and the correction you will use. Tutor conversation is optional and is not a demonstrated check. Cumulative review revisits completed objectives from earlier units. Activity is not demonstrated ability. Open the offline grammar reference for this unit when one is linked.
dominantAction=Continue
references=
items=
examples=
unavailableReason=
bookDirection=
companionDirection=Tutor work is optional and does not count as demonstrated ability.
remediationStepId=

### STEP `u3-st-cumulative-direction`

- Objectives: obj-u3-book
- Sessions: u3-s-book
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 41e7c9b1fc5aefc4e9ea07473cced477ee8e739e3715ef4d69944a5115e904c1

kind=ORIENT
title=Unit 3 cumulative review
body=Unit 3 AnkiDroid self-attestation uses only cards you are allowed to make. Binder: write one form from this unit that you missed and the correction you will use. Tutor conversation is optional and is not a demonstrated check. Cumulative review revisits completed objectives from earlier units. Activity is not demonstrated ability. Open the offline grammar reference for this unit when one is linked.
dominantAction=Continue
references=
items=
examples=
unavailableReason=
bookDirection=
companionDirection=Unit 3 AnkiDroid self-attestation uses only cards you are allowed to make. Binder: write one form from this unit that you missed and the correction you will use. Tutor conversation is optional and is not a demonstrated check. Cumulative review revisits completed objectives from earlier units. Activity is not demonstrated ability. Open the offline grammar reference for this unit when one is linked.
remediationStepId=

### EXAMPLE `ex-u3-стол`

- Objectives: obj-u3-gender
- Sessions: u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 34891b34ba135bc78ce683b204b3e7ebf95ff6667caa5903c6c30c3950554a46

kind=LEXICAL
letterId=
display=стол
note=table
objectiveId=obj-u3-gender
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-u3-дом`

- Objectives: obj-u3-gender
- Sessions: u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 3d61a5ea830153ad897844de6fe8dba2833ca31a82e7803740ce438b753c5b7c

kind=LEXICAL
letterId=
display=дом
note=house
objectiveId=obj-u3-gender
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-u3-город`

- Objectives: obj-u3-gender
- Sessions: u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: ba0e86e24c8b4d91bf3e1524158407bca7901f5a8c60518dd23aa2587a6154bd

kind=LEXICAL
letterId=
display=го́род
note=town
objectiveId=obj-u3-gender
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-u3-брат`

- Objectives: obj-u3-gender
- Sessions: u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 8959b95e22c321c52d9ab4c8b2217137a9940a89a539c1c5e6a5cc19fabdef4c

kind=LEXICAL
letterId=
display=брат
note=brother
objectiveId=obj-u3-gender
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-u3-книга`

- Objectives: obj-u3-gender
- Sessions: u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 2c82a582592e7c6d1017ccc4caf822accb1ea3bf0d65ddda2df7c415971bf917

kind=LEXICAL
letterId=
display=кни́га
note=book
objectiveId=obj-u3-gender
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-u3-вода`

- Objectives: obj-u3-gender
- Sessions: u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 42346d1ed065899c49e10af3c1c5a63594ad486c4db9d23324d239cf13ae5193

kind=LEXICAL
letterId=
display=вода́
note=water
objectiveId=obj-u3-gender
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-u3-школа`

- Objectives: obj-u3-gender
- Sessions: u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 5ce5fd2a5ed293f11c63e0f69f5f2a89e2a3d1fe1d735ef441ad8fcdd506a85f

kind=LEXICAL
letterId=
display=шко́ла
note=school
objectiveId=obj-u3-gender
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-u3-работа`

- Objectives: obj-u3-gender
- Sessions: u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 1381bff797e2f5c6c5347a990a2fb4565c688c7302f7be6d5eda1c9d4dcb7fcb

kind=LEXICAL
letterId=
display=рабо́та
note=work
objectiveId=obj-u3-gender
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-u3-окно`

- Objectives: obj-u3-gender
- Sessions: u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 0d940ff8ae45c432ade7896b930755c87aaea26a98f20127bd313e4ef3333c68

kind=LEXICAL
letterId=
display=окно́
note=window
objectiveId=obj-u3-gender
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-u3-письмо`

- Objectives: obj-u3-gender
- Sessions: u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 9499c77360029a4bc2322a157c83bbcd0b47125319b52ad5a05a872716248dfc

kind=LEXICAL
letterId=
display=письмо́
note=letter
objectiveId=obj-u3-gender
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-u3-молоко`

- Objectives: obj-u3-gender
- Sessions: u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 96aa6255276a02165fe9ed6dd37e5df01f5fc6c99300f8bc9357e1d02e424088

kind=LEXICAL
letterId=
display=молоко́
note=milk
objectiveId=obj-u3-gender
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-u3-море`

- Objectives: obj-u3-gender
- Sessions: u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 159e8995373c609fcd3369197bcd356031d4fe8137e3782d7b1de0db23054ae2

kind=LEXICAL
letterId=
display=мо́ре
note=sea
objectiveId=obj-u3-gender
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-u3-мужчина`

- Objectives: obj-u3-gender
- Sessions: u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 8b00174a5c9e1b5f92a8b760260dc0ddee9fd52528b98ce34ed3ddac52e255bd

kind=LEXICAL
letterId=
display=мужчи́на
note=man
objectiveId=obj-u3-gender
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-u3-дядя`

- Objectives: obj-u3-gender
- Sessions: u3-s-gender
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 61f77808c0824450ced978be10f1e468649d7dc9ed6eef5e0a9c905b8c291c58

kind=LEXICAL
letterId=
display=дя́дя
note=uncle
objectiveId=obj-u3-gender
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-u3-phrase-здравствуйте`

- Objectives: obj-u3-phrases
- Sessions: u3-s-phrases
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: a6121355c8402358a166ab0d033eaa636c9ef273595d51810f94c2e2bf56c650

kind=LEXICAL
letterId=
display=здра́вствуйте
note=hello
objectiveId=obj-u3-phrases
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-u3-phrase-спасибо`

- Objectives: obj-u3-phrases
- Sessions: u3-s-phrases
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 199ab3bbfcec864f08ecc038866e9c3f71d149615c05154eed4c9b504d401b8f

kind=LEXICAL
letterId=
display=спаси́бо
note=thank you
objectiveId=obj-u3-phrases
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-u3-phrase-пожалуйста`

- Objectives: obj-u3-phrases
- Sessions: u3-s-phrases
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: df2466275554e64bbb596bdaaed0604adff36584eedc008a7382b91067f92da9

kind=LEXICAL
letterId=
display=пожа́луйста
note=please
objectiveId=obj-u3-phrases
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-u3-phrase-да`

- Objectives: obj-u3-phrases
- Sessions: u3-s-phrases
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 98bdede48f6890037e73bd3e6ed1ee7608dda232be98a02c1a90c04b53ac9df0

kind=LEXICAL
letterId=
display=да
note=yes
objectiveId=obj-u3-phrases
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-u3-phrase-нет`

- Objectives: obj-u3-phrases
- Sessions: u3-s-phrases
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 00849e3f490e364bb1263d5e4630e948917f8b7c0362d66280a9aa925c736ae4

kind=LEXICAL
letterId=
display=нет
note=no
objectiveId=obj-u3-phrases
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-u3-phrase-это`

- Objectives: obj-u3-phrases
- Sessions: u3-s-phrases
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: ee337e6a5c6aee2f3f51925490a675e3205ecc5daee046db73828358c98da43f

kind=LEXICAL
letterId=
display=э́то
note=this is
objectiveId=obj-u3-phrases
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-u3-phrase-kak-dela`

- Objectives: obj-u3-phrases
- Sessions: u3-s-phrases
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: a68cb960f29e1a8e976923032b2e7f39139bd1964487fa462fdd7237e3601115

kind=LEXICAL
letterId=
display=Как дела?
note=How do you do?/How're things?
objectiveId=obj-u3-phrases
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-u3-phrase-gde-vy`

- Objectives: obj-u3-phrases
- Sessions: u3-s-phrases
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 54d0612f84bd4ee1c33ef1682bc40d3af97f2066de5aa3d4b09d9223a29600fe

kind=LEXICAL
letterId=
display=Где вы?
note=Where are you?
objectiveId=obj-u3-phrases
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-u3-phrase-moy-stol`

- Objectives: obj-u3-naming
- Sessions: u3-s-naming
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 21f747767d183b701e2f19c4202067fa56df7346256ca4a12719a725cc6f1a74

kind=LEXICAL
letterId=
display=мой стол
note=my table
objectiveId=obj-u3-naming
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-u3-phrase-moya-sobaka`

- Objectives: obj-u3-naming
- Sessions: u3-s-naming
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 88d297d42ee6c305c9b664116ec18ab183e902122147469efd23bb2ccce3c77b

kind=LEXICAL
letterId=
display=моя собака
note=my dog
objectiveId=obj-u3-naming
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-u3-phrase-chto-eto`

- Objectives: obj-u3-naming
- Sessions: u3-s-naming
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 3b62b9751f07b2ec6c37ce8912d3a3196afd5cb28e2b9f0bd107e821fd5de993

kind=LEXICAL
letterId=
display=Что это?
note=What is this?
objectiveId=obj-u3-naming
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-u3-phrase-kto-eto`

- Objectives: obj-u3-naming
- Sessions: u3-s-naming
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 65f19a0b338857e1996dde7117b8d26ca3d16dd9fa4e538c5b53399137dbc888

kind=LEXICAL
letterId=
display=Кто это?
note=Who is this?
objectiveId=obj-u3-naming
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### GUIDED_PRACTICE `u3-i-gender-стол`

- Objectives: obj-u3-gender
- Sessions: u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: a0fb8f835b56251d5c83489f90b01d459567f24d49dc7f9d5c209e5589a6641d

kind=SOURCE_GENDER
language=en
prompt=Which admitted label is recorded for the shown word?
promptLetter=стол
options=masculine | feminine | neuter
answer=masculine
feedback=The admitted record lists this as masculine.
objectiveId=obj-u3-gender
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### GUIDED_PRACTICE `u3-i-gender-дом`

- Objectives: obj-u3-gender
- Sessions: u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: abed741501f4ee0f9fc06115e636b8e578f8be9a86a50d5d8caf74a5dc1ee7ce

kind=SOURCE_GENDER
language=en
prompt=Which admitted label is recorded for the shown word?
promptLetter=дом
options=masculine | feminine | neuter
answer=masculine
feedback=The admitted record lists this as masculine.
objectiveId=obj-u3-gender
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### GUIDED_PRACTICE `u3-i-gender-книга`

- Objectives: obj-u3-gender
- Sessions: u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: a2b04f39a2941d2bfdaf20bf53e6995c8f5b7c3dd1c90c144fa4f2090d5c4d4c

kind=SOURCE_GENDER
language=en
prompt=Which admitted label is recorded for the shown word?
promptLetter=кни́га
options=masculine | feminine | neuter
answer=feminine
feedback=The admitted record lists this as feminine.
objectiveId=obj-u3-gender
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### GUIDED_PRACTICE `u3-i-gender-окно`

- Objectives: obj-u3-gender
- Sessions: u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: bfca440485ead8be0e618ce6fd4f3224557e671636ca815ad26b5d3dfc7915b2

kind=SOURCE_GENDER
language=en
prompt=Which admitted label is recorded for the shown word?
promptLetter=окно́
options=masculine | feminine | neuter
answer=neuter
feedback=The admitted record lists this as neuter.
objectiveId=obj-u3-gender
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### GUIDED_PRACTICE `u3-i-gender-мужчина`

- Objectives: obj-u3-gender
- Sessions: u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: b1cf3aa2dded27fac7dc7da1abcd1258b135c32980932e705db78e509fa942d8

kind=SOURCE_GENDER
language=en
prompt=Which admitted label is recorded for the shown word?
promptLetter=мужчи́на
options=masculine | feminine | neuter
answer=masculine
feedback=The admitted record lists this as masculine.
objectiveId=obj-u3-gender
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### GUIDED_PRACTICE `u3-i-phrase-здравствуйте`

- Objectives: obj-u3-phrases
- Sessions: u3-s-phrases
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 656b9ec3db1c1bb8e3689e3d825bca21ba66747bfcc4a24bb90547ca5e6da1d1

kind=SOURCE_MEANING
language=en
prompt=Which admitted gloss is recorded for the shown item?
promptLetter=здра́вствуйте
options=hello | thank you | please | yes | no | this is
answer=hello
feedback=The frozen dictionary row records this gloss: hello.
objectiveId=obj-u3-phrases
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### GUIDED_PRACTICE `u3-i-phrase-спасибо`

- Objectives: obj-u3-phrases
- Sessions: u3-s-phrases
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 05c7658a7de98a1e902a9f050d89d927849c9151a7ec285e0f0f5a5cff9e724c

kind=SOURCE_MEANING
language=en
prompt=Which admitted gloss is recorded for the shown item?
promptLetter=спаси́бо
options=hello | thank you | please | yes | no | this is
answer=thank you
feedback=The frozen dictionary row records this gloss: thank you.
objectiveId=obj-u3-phrases
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### GUIDED_PRACTICE `u3-i-name-moy-stol`

- Objectives: obj-u3-naming
- Sessions: u3-s-naming
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 35bd956a1fb3787827dd7553f1f32d8152d586f6ea171808931e74d09ed051c3

kind=SOURCE_MEANING
language=en
prompt=Which admitted meaning is recorded for the shown item?
promptLetter=мой стол
options=my table | my dog | What is this? | Who is this?
answer=my table
feedback=The frozen page records this meaning: my table.
objectiveId=obj-u3-naming
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### INDEPENDENT_CHECK `u3-i-gender-город`

- Objectives: obj-u3-gender
- Sessions: u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 579661e75f8c8117faaef2d99c3f6764cad8fa5bb2373ab44aba10b835a2af7d

kind=SOURCE_GENDER
language=en
prompt=Which admitted label is recorded for the shown word?
promptLetter=го́род
options=masculine | feminine | neuter
answer=masculine
feedback=The admitted record lists this as masculine.
objectiveId=obj-u3-gender
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### INDEPENDENT_CHECK `u3-i-gender-друг`

- Objectives: obj-u3-gender
- Sessions: u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 639a5f89cc33742c2ce23b0affc53c09989c00dc4e84b735ffb295d0bba311c5

kind=SOURCE_GENDER
language=en
prompt=Which admitted label is recorded for the shown word?
promptLetter=друг
options=masculine | feminine | neuter
answer=masculine
feedback=The admitted record lists this as masculine.
objectiveId=obj-u3-gender
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### INDEPENDENT_CHECK `u3-i-gender-дверь`

- Objectives: obj-u3-gender
- Sessions: u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: f3855e4bdb5f9d044eec5548150b4339e78911ec668aced1f2473f10930d5cb1

kind=SOURCE_GENDER
language=en
prompt=Which admitted label is recorded for the shown word?
promptLetter=дверь
options=masculine | feminine | neuter
answer=feminine
feedback=The admitted record lists this as feminine.
objectiveId=obj-u3-gender
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### INDEPENDENT_CHECK `u3-i-gender-море`

- Objectives: obj-u3-gender
- Sessions: u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: f209491df1be7ec866ee6403457f830122a61b4729d282150abb4d45a701852f

kind=SOURCE_GENDER
language=en
prompt=Which admitted label is recorded for the shown word?
promptLetter=мо́ре
options=masculine | feminine | neuter
answer=neuter
feedback=The admitted record lists this as neuter.
objectiveId=obj-u3-gender
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### INDEPENDENT_CHECK `u3-i-gender-дядя`

- Objectives: obj-u3-gender
- Sessions: u3-s-gender
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: c6c44bf3bb8b2c8733b364f37da9414d392f0df48746ddac2eb9c9fe9e655ffc

kind=SOURCE_GENDER
language=en
prompt=Which admitted label is recorded for the shown word?
promptLetter=дя́дя
options=masculine | feminine | neuter
answer=masculine
feedback=The admitted record lists this as masculine.
objectiveId=obj-u3-gender
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### INDEPENDENT_CHECK `u3-i-phrase-пожалуйста`

- Objectives: obj-u3-phrases
- Sessions: u3-s-phrases
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: c92cfec97712bac827d8b9895a3f955f7159b155c05ef96fddb762b0a81057a4

kind=SOURCE_MEANING
language=en
prompt=Which admitted gloss is recorded for the shown item?
promptLetter=пожа́луйста
options=hello | thank you | please | yes | no | this is
answer=please
feedback=The frozen dictionary row records this gloss: please.
objectiveId=obj-u3-phrases
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### INDEPENDENT_CHECK `u3-i-phrase-да`

- Objectives: obj-u3-phrases
- Sessions: u3-s-phrases
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: e04dfbcd18037af23ca294f4b1ebe591f8a8040b1b6d6284179b5433b9dd7a5e

kind=SOURCE_MEANING
language=en
prompt=Which admitted gloss is recorded for the shown item?
promptLetter=да
options=hello | thank you | please | yes | no | this is
answer=yes
feedback=The frozen dictionary row records this gloss: yes.
objectiveId=obj-u3-phrases
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### INDEPENDENT_CHECK `u3-i-phrase-нет`

- Objectives: obj-u3-phrases
- Sessions: u3-s-phrases
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: dd4ca8e69aa963762ab3e2dcce4c5368670121551316e3953aefcee861855fa1

kind=SOURCE_MEANING
language=en
prompt=Which admitted gloss is recorded for the shown item?
promptLetter=нет
options=hello | thank you | please | yes | no | this is
answer=no
feedback=The frozen dictionary row records this gloss: no.
objectiveId=obj-u3-phrases
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### INDEPENDENT_CHECK `u3-i-phrase-это`

- Objectives: obj-u3-phrases
- Sessions: u3-s-phrases
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 0b644f14ec66781c050622f196f605ffb2d5eddbf111e06e446f7e29b6fb1a4f

kind=SOURCE_MEANING
language=en
prompt=Which admitted gloss is recorded for the shown item?
promptLetter=э́то
options=hello | thank you | please | yes | no | this is
answer=this is
feedback=The frozen dictionary row records this gloss: this is.
objectiveId=obj-u3-phrases
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### INDEPENDENT_CHECK `u3-i-name-moya-sobaka`

- Objectives: obj-u3-naming
- Sessions: u3-s-naming
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 4b822e3d06c8c905ce038dcbadb00ee0a2f89a820b8643ccc9d131e94000fdc9

kind=SOURCE_MEANING
language=en
prompt=Which admitted meaning is recorded for the shown item?
promptLetter=моя собака
options=my table | my dog | What is this? | Who is this?
answer=my dog
feedback=The frozen page records this meaning: my dog.
objectiveId=obj-u3-naming
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### INDEPENDENT_CHECK `u3-i-name-chto-eto`

- Objectives: obj-u3-naming
- Sessions: u3-s-naming
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: de7ce2ad3b8cc11d14c163e2662ee81397bffdb679f54e24a42c578da5a59637

kind=SOURCE_MEANING
language=en
prompt=Which admitted meaning is recorded for the shown item?
promptLetter=Что это?
options=my table | my dog | What is this? | Who is this?
answer=What is this?
feedback=The frozen page records this meaning: What is this?.
objectiveId=obj-u3-naming
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### INDEPENDENT_CHECK `u3-i-name-kto-eto`

- Objectives: obj-u3-naming
- Sessions: u3-s-naming
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 47492ad10d7768f317abea45623f04cdbe7f85668b88f7bd0796d29a027f8c50

kind=SOURCE_MEANING
language=en
prompt=Which admitted meaning is recorded for the shown item?
promptLetter=Кто это?
options=my table | my dog | What is this? | Who is this?
answer=Who is this?
feedback=The frozen page records this meaning: Who is this?.
objectiveId=obj-u3-naming
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### REFERENCE `ref-u35-attribution`

- Objectives: none
- Sessions: u3-s-orient, u3-s-gender, u3-s-phrases, u3-s-naming, u3-s-book
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: LICENSE_OR_ATTRIBUTION
- Current review status: NOT_REQUIRED
- Content hash: d4ceea9bc21ad162d1a51a5204b752339438c4cd2a1ef29da1b8d40b7581b432

title=Units 3–5 source notice
summary=Proposed Units 3–5 adapt CC BY-SA 4.0 dictionary rows and Wikibooks grammar excerpts.
detail=OpenRussian dictionary data is frozen at commit 50e210c4803237779cb562bc1abcea529066031c under CC BY-SA 4.0. Wikibooks Russian grammar excerpts are frozen by oldid under the CC BY-SA 4.0 route. The adapted selection and arrangement are share-alike. Application code, user data, and user recordings are outside that statement. No audio is included in this proposed curriculum. Review is pending. The packaged CC BY-SA 4.0 legal code is cc-by-sa-4.0.txt.
classification=LICENSE_OR_ATTRIBUTION
introducedInUnit=3
searchTerms=units 3-5 source notice
reviewStatus=NOT_REQUIRED

### REFERENCE `ref-book-topics`

- Objectives: none
- Sessions: u3-s-book
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: OPERATIONAL_GUIDANCE
- Current review status: NOT_REQUIRED
- Content hash: ceb8a2ed6317a8a3dea34ff9a767837eaaa554ba89cb2882acdf9dfaafcb705d

title=Where lesson bodies are packaged
summary=Some units contain proposed lesson bodies that still require qualified review.
detail=A unit's Course status shows whether its lesson body is prepared. Units marked Not prepared or Not yet installed do not contain a usable lesson body. Open the matching lesson in the copy you own. This reference does not approve the packaged records.
classification=OPERATIONAL_GUIDANCE
introducedInUnit=3
searchTerms=where lesson bodies are packaged
reviewStatus=NOT_REQUIRED

### PROVENANCE `prov-ca-3-346`

- Objectives: obj-u3-gender
- Sessions: u3-s-orient, u3-s-gender
- Source: src-content-audit-research
- Rights: rights-content-audit-research
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: a7dd6b3ad60d5abfbeb810aee8f0dbaea396097979ec62ca0275e81e742c9f9f

recordType=label
sourceUrl=urn:russian-companion:content-audit-research:2026-10-08
sourceRevision=content-audit-2026-10-08
sourceRecordId=ca-coverage:obj-u3-gender:u3-st-teach-gender-5
originalSourceText=Content-audit research note dated 2026-10-08. Bibliography includes the Gramota dictionary entries for некто and быть, the Gramota statement of vowels after hushers and ц, and Litnevskaya on the verbal adverb as search evidence for relative time. An imperfective form can report a completed event without asserting completion. Authorship: independently authored for Russian Companion. Status remains IMPLEMENTED_PENDING_FINAL_AUDIT. This note is not linguistic acceptance, mapping approval, installation, or owner acceptance. No owned-book prose, exercise, or answer key is copied.
дя́дя and мужчи́на decline like nouns ending in -а, and they are masculine. Say decline for these nouns. Do not say they conjugate.
shippedText=дя́дя and мужчи́на decline like nouns ending in -а, and they are masculine. Say decline for these nouns. Do not say they conjugate.
transformation=content-audit research synthesis
licenseId=Original app text
licenseUrl=urn:russian-companion:license:original-app-text
attribution=Russian Companion content-audit research synthesis, content-audit-2026-10-08. No owned-book prose is included.
shareAlikeTreatment=Original research synthesis authored for this companion. It is not a CC BY-SA quotation. Application code, user data, and user recordings are outside this statement.
sourceHash=9a328d2ff90e38c0621fc2504f544fd46075f780399c86c898f8876e8e88c0a8
contentHash=ef2713a40b21643197b991e4ed4016f8564c9e57ab73a545e1d798930cbfd361
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-ca-3-347`

- Objectives: obj-u3-gender
- Sessions: u3-s-orient, u3-s-gender
- Source: src-content-audit-research
- Rights: rights-content-audit-research
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 1afce8070932cce2bd50a5a223d7fe5cec791c4bf984a0077622621837bf1a1a

recordType=example
sourceUrl=urn:russian-companion:content-audit-research:2026-10-08
sourceRevision=content-audit-2026-10-08
sourceRecordId=ca-coverage:obj-u3-gender:u3-st-teach-gender-5
originalSourceText=Content-audit research note dated 2026-10-08. Bibliography includes the Gramota dictionary entries for некто and быть, the Gramota statement of vowels after hushers and ц, and Litnevskaya on the verbal adverb as search evidence for relative time. An imperfective form can report a completed event without asserting completion. Authorship: independently authored for Russian Companion. Status remains IMPLEMENTED_PENDING_FINAL_AUDIT. This note is not linguistic acceptance, mapping approval, installation, or owner acceptance. No owned-book prose, exercise, or answer key is copied.
дя́дя and мужчи́на decline like nouns ending in -а, and they are masculine. Say decline for these nouns. Do not say they conjugate.
shippedText=а,
transformation=content-audit research synthesis
licenseId=Original app text
licenseUrl=urn:russian-companion:license:original-app-text
attribution=Russian Companion content-audit research synthesis, content-audit-2026-10-08. No owned-book prose is included.
shareAlikeTreatment=Original research synthesis authored for this companion. It is not a CC BY-SA quotation. Application code, user data, and user recordings are outside this statement.
sourceHash=9a328d2ff90e38c0621fc2504f544fd46075f780399c86c898f8876e8e88c0a8
contentHash=3dcaa4fdd9e579c37782614b0e535a9f8171a9d26827cdb0d5bca63b55dabf06
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-ca-3-348`

- Objectives: obj-u3-gender
- Sessions: u3-s-orient, u3-s-gender
- Source: src-content-audit-research
- Rights: rights-content-audit-research
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 5933153b10f3449fe39100f46c989212545a49d4495e9eb4f30a0fe7c1ffd2c1

recordType=example
sourceUrl=urn:russian-companion:content-audit-research:2026-10-08
sourceRevision=content-audit-2026-10-08
sourceRecordId=ca-coverage:obj-u3-gender:u3-st-teach-gender-5
originalSourceText=Content-audit research note dated 2026-10-08. Bibliography includes the Gramota dictionary entries for некто and быть, the Gramota statement of vowels after hushers and ц, and Litnevskaya on the verbal adverb as search evidence for relative time. An imperfective form can report a completed event without asserting completion. Authorship: independently authored for Russian Companion. Status remains IMPLEMENTED_PENDING_FINAL_AUDIT. This note is not linguistic acceptance, mapping approval, installation, or owner acceptance. No owned-book prose, exercise, or answer key is copied.
дя́дя and мужчи́на decline like nouns ending in -а, and they are masculine. Say decline for these nouns. Do not say they conjugate.
shippedText=а
transformation=content-audit research synthesis
licenseId=Original app text
licenseUrl=urn:russian-companion:license:original-app-text
attribution=Russian Companion content-audit research synthesis, content-audit-2026-10-08. No owned-book prose is included.
shareAlikeTreatment=Original research synthesis authored for this companion. It is not a CC BY-SA quotation. Application code, user data, and user recordings are outside this statement.
sourceHash=9a328d2ff90e38c0621fc2504f544fd46075f780399c86c898f8876e8e88c0a8
contentHash=823c4eb3e895adc925a755d89cea1c6c46954c999d23604e0091788b75496159
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-dyadya-note`

- Objectives: obj-u3-gender
- Sessions: u3-s-orient, u3-s-gender
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: c85432203f0de5e70176fc4413808c0f18b76b3e8022deaaef61273a12708bf9

recordType=explanation
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Gender?oldid=4310736
sourceRevision=4310736
sourceRecordId=gender:though it has a 'feminine' ending and conjugates like a feminine noun, 'uncle' i
originalSourceText=though it has a 'feminine' ending and conjugates like a feminine noun, 'uncle' is 'inherently' masculine
shippedText=though it has a 'feminine' ending and conjugates like a feminine noun, 'uncle' is 'inherently' masculine
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=77ddf6906536584b1db04d17b6ea8729c4b27f518bb6ac0ae49e0f59fcc8fb9d
contentHash=77ddf6906536584b1db04d17b6ea8729c4b27f518bb6ac0ae49e0f59fcc8fb9d
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-gender-sentence`

- Objectives: obj-u3-gender
- Sessions: u3-s-orient, u3-s-gender
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 1b9842b779ea31a12b77479c7383f74cd1b28ec3cbbca9ac7efb6c51f7efedb1

recordType=explanation
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Gender?oldid=4310736
sourceRevision=4310736
sourceRecordId=gender:So, in Russian, nouns have an intrinsic property called 'gender', which comes in
originalSourceText=So, in Russian, nouns have an intrinsic property called 'gender', which comes in three types: masculine, feminine, and neuter.
shippedText=So, in Russian, nouns have an intrinsic property called 'gender', which comes in three types: masculine, feminine, and neuter.
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=4fc74bf7c3fc6662336d7cb9f8485e1d357fd2e7222c4ee11f4670d03aef047e
contentHash=4fc74bf7c3fc6662336d7cb9f8485e1d357fd2e7222c4ee11f4670d03aef047e
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-gloss-брат`

- Objectives: obj-u3-gender
- Sessions: u3-s-orient, u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: d0de8d74ba9ba66bcd5a8dc4e7e1305274b2a974a34b174133b609aa25722b90

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:брат#translations_en
originalSourceText=brother
shippedText=brother
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=0b4bd77cec705cf5df016344c0fc673fb3c2240078e6af572b2c5c6fa76114af
contentHash=0b4bd77cec705cf5df016344c0fc673fb3c2240078e6af572b2c5c6fa76114af
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-gloss-вода`

- Objectives: obj-u3-gender
- Sessions: u3-s-orient, u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: b2c2d1a16f7603ed3fe0ff87f8361ef81aa45b65b2e9dacee6acbd12ff21c4e9

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:вода#translations_en
originalSourceText=water
shippedText=water
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=0f4168490e38b8447e11ba4bd656aa11b925bd22af30bac464bc153fdb608501
contentHash=0f4168490e38b8447e11ba4bd656aa11b925bd22af30bac464bc153fdb608501
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-gloss-город`

- Objectives: obj-u3-gender
- Sessions: u3-s-orient, u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: a159ff0f075068a439fc6e16f6effc127f647f7bbebd432cc7285abe59719560

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:город#translations_en
originalSourceText=town
shippedText=town
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=50ac81f970325a3e011f2437818adde426e1278dee5670dd359edfca43a72d79
contentHash=50ac81f970325a3e011f2437818adde426e1278dee5670dd359edfca43a72d79
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-gloss-дверь`

- Objectives: obj-u3-gender
- Sessions: u3-s-orient, u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 21dae51a710d2b2c4cc8db692bf3d8e0e4cab7515b4c73dfb8154d90028dd759

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:дверь#translations_en
originalSourceText=door
shippedText=door
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=2534e2de1dec24fedab4c00266b55ba9ba60dde03a349c5b5662189dd561ba9e
contentHash=2534e2de1dec24fedab4c00266b55ba9ba60dde03a349c5b5662189dd561ba9e
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-gloss-дом`

- Objectives: obj-u3-gender
- Sessions: u3-s-orient, u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: ccd46ec228f31c6642fe423286a96100468aa6565ca204d50b2a0e26aa63cbf5

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:дом#translations_en
originalSourceText=house, home, house-hold
shippedText=house
transformation=sense selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=d5140b4f5c763738e8327a10a66a10858446112daaa55417c81397dd4c9da2f0
contentHash=d6e21286621a8586f7e54720ab5a39c93acc2f4b8fba7a16ec1c24d69a08c613
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-gloss-друг`

- Objectives: obj-u3-gender
- Sessions: u3-s-orient, u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 0eeded8c1489b155b73738dcd3bf5789123168085e40ce638538f133e765f667

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:друг#translations_en
originalSourceText=friend
shippedText=friend
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=cde48537ca2c28084ff560826d0e6388b7c57a51497a6cb56f397289e52ff41b
contentHash=cde48537ca2c28084ff560826d0e6388b7c57a51497a6cb56f397289e52ff41b
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-gloss-дядя`

- Objectives: obj-u3-gender
- Sessions: u3-s-orient, u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: b61dc84f26e6ae14499b10af1ca649d8ef0335fae6630c50f92c05d8ad9cfacd

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:дядя#translations_en
originalSourceText=uncle, Mister, man, fellow
shippedText=uncle
transformation=sense selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=31fa2bb18d052c4e86d21767b4d000d02dea8422ad5cc7fcd41daa9e9785011a
contentHash=3107ea0301a27894a212be51c76b7c5b94a8700b6ace0024fc023cf1e685558d
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-gloss-женщина`

- Objectives: obj-u3-gender
- Sessions: u3-s-orient, u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 9fe41373efa8b5edff4b90a6a68198b37e186b1009cc10492221c0fea0940750

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:женщина#translations_en
originalSourceText=woman, women, womanfolk, womankind
shippedText=woman
transformation=sense selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=76587fa3848559ed4a1dc5188bedbe0b082834b94616f16b3bbac002543a61b2
contentHash=cf2e5bff1ef03dcd20d1a0b18ef7d89fc80a3554434165753672f6f40fde1d25
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-gloss-книга`

- Objectives: obj-u3-gender
- Sessions: u3-s-orient, u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 50172c9f69db53a98f3d60fe6aef34c519f9ac599fe047e914eb68ac2eb0503a

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:книга#translations_en
originalSourceText=book, volume
shippedText=book
transformation=sense selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=c8b9c6eacf6d3e19e7d5e0dcdfe1ad0d3bf4d8fca70084d9dd3f6a62ed1ad36d
contentHash=92719fe0cf8cd51592af31ee8a5736d79f7273777fa3f7b70bfe993a4cd32180
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-gloss-мама`

- Objectives: obj-u3-gender
- Sessions: u3-s-orient, u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 82d7be2246d7ff324b8b89ad7d0658e7e5d214c669d7a4b887e47e606f1e7e0f

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:мама#translations_en
originalSourceText=mom, mama, mommy, mummy
shippedText=mom
transformation=sense selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=d4cf71370a668da26fe34070d59753e8c82f2477176bc4eee3a06dc421b77bc6
contentHash=bcb9dae6ea88dbf28c262998e6661ec60f32a760faa5aef96745b39c38dbf235
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-gloss-место`

- Objectives: obj-u3-gender
- Sessions: u3-s-orient, u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 19dd16cce8874475b90be6bbad87252763e3d7abc204109e6ca2b389f9e484e6

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:место#translations_en
originalSourceText=place, spot, site, locality; region, area; site, scene; room, space; job, position; piece of luggage
shippedText=place
transformation=sense selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=017c9db03b04b9b953d95d117d51589fecb72de69829f3917b1e017f172237b1
contentHash=81df63580cc9ff1550c90d918ec234f1834013b800b5321201e88d96e785277c
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-gloss-молоко`

- Objectives: obj-u3-gender
- Sessions: u3-s-orient, u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 5007eb63f0e0e9424a8b7abe376229da80fc792e7ea2de7c6c25c25f72c7c064

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:молоко#translations_en
originalSourceText=milk
shippedText=milk
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=8825e76530f87ff5c737b3254c6936bc0a90bba3a10a91bbba739268208d378a
contentHash=8825e76530f87ff5c737b3254c6936bc0a90bba3a10a91bbba739268208d378a
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-gloss-море`

- Objectives: obj-u3-gender
- Sessions: u3-s-orient, u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: d44562a0b9c8081ea829d620164828fcdffad3f7342726a3ec1dd3a26c70b8ba

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:море#translations_en
originalSourceText=sea, ocean
shippedText=sea
transformation=sense selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=f7f885bf05cc652337b7aaeb74baa15ed5811e0e1935238014a85835193fe701
contentHash=4a69f19c8c264850d0f0fca1d7cd8ac0d07771cb9aaf5923c2c621a3e0f74475
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-gloss-мужчина`

- Objectives: obj-u3-gender
- Sessions: u3-s-orient, u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: b257ece7abda738a33b9cf65511bb70a9faafc8f2d36d13293ebdf7e1faa7661

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:мужчина#translations_en
originalSourceText=man
shippedText=man
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=48b676e2b107da679512b793d5fd4cc4329f0c7c17a97cf6e0e3d1005b600b03
contentHash=48b676e2b107da679512b793d5fd4cc4329f0c7c17a97cf6e0e3d1005b600b03
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-gloss-окно`

- Objectives: obj-u3-gender
- Sessions: u3-s-orient, u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 092d7be1d5e35079e18e2fd6664d072c1e646ba851bf5f48641e8ae814638d08

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:окно#translations_en
originalSourceText=window, casement-window, window-sill, gap, opening, port, slot, free period, break
shippedText=window
transformation=sense selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=a6a1fa753c5df949c2ba867785897dc3c3168b683ded74a198414ff0ffd1da29
contentHash=44ff7b02c80d38b26dd6aa31d9470aed81b32e10331a3c994fb1a9945fd847ba
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-gloss-письмо`

- Objectives: obj-u3-gender
- Sessions: u3-s-orient, u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: e2e110fd186322bd74b9013e09577725ffdfe7f578ade32434111266a825d8a2

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:письмо#translations_en
originalSourceText=letter, writing, script
shippedText=letter
transformation=sense selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=48d7514d4d9457b94bc51af37b423c42fd81e75d0ab62e12d4d9e420125a158c
contentHash=f80105efab6d863fd8fc243d269094469e2277e8f12e5a0a9f401e88494f7b4b
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-gloss-работа`

- Objectives: obj-u3-gender
- Sessions: u3-s-orient, u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 52ed6b824740f5d065fda8dc3af90f1f3661d681f33e8e45e8def5cff7276fcc

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:работа#translations_en
originalSourceText=work, working, job
shippedText=work
transformation=sense selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=b770cc266e4a2d0d992269ba934710aaaaa4686e06ff06be830c861af688044c
contentHash=00e13ed7af55b27622f1d6eab5bec0147e68efe28dc2b12461117afa1a5ed40e
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-gloss-семья`

- Objectives: obj-u3-gender
- Sessions: u3-s-orient, u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: e9dd420ad7018700cfe589f7698d084fd8d4997202e15acfe53f9b0c86138f00

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:семья#translations_en
originalSourceText=family
shippedText=family
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=d34a569ab7aaa54dacd715ae64953455d86b768846cd0085ef4e9e7471489b7b
contentHash=d34a569ab7aaa54dacd715ae64953455d86b768846cd0085ef4e9e7471489b7b
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-gloss-слово`

- Objectives: obj-u3-gender
- Sessions: u3-s-orient, u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: bd845a9cea1ee4865e2df88f119c700e21c74c8d823e02a27dd89537a349f4cf

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:слово#translations_en
originalSourceText=word; speech
shippedText=word
transformation=sense selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=6bf95a2fa244e47d0d15e64879c80a44ca8011d47e9aad3ca488acf175c8fe1c
contentHash=98c1eb4ee93476743763878fcb96a25fbc9a175074d64004779ecb5242f645e6
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-gloss-стол`

- Objectives: obj-u3-gender
- Sessions: u3-s-orient, u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 6c8a5fcb78eef05f6edb973146acb5af975231a43ccdca05a7cb3c95c7503620

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:стол#translations_en
originalSourceText=table, desk, board; diet, cooking, cuisine; department, section
shippedText=table
transformation=sense selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=f27cc2c8a71de4aef61cea7eb4777768cd53c906f03627296bd3b8998c919ab6
contentHash=0d4fc4a78d3706edccafb665a8b2fdd9309e82c78625bb0f2b8e7bb9e1c4d21c
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-gloss-учитель`

- Objectives: obj-u3-gender
- Sessions: u3-s-orient, u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: e38d792253644e45ced8bcfef8aec7af377cb1db87b9f93cbc796889e0e3d87f

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:учитель#translations_en
originalSourceText=teacher, tutor
shippedText=teacher
transformation=sense selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=9ea0353cc2b9ccaa9554efddbad2d9c8bbebae73b5f47730461e4b4df72d198e
contentHash=1057a9604e04b274da5a4de0c8f4b4868d9b230989f8c8c6a28221143cc5a755
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-gloss-хлеб`

- Objectives: obj-u3-gender
- Sessions: u3-s-orient, u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: feaf7948f50df6c2573f34f79ee4796bf5ae801946d87ed7eb3b79fad90ce095

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:хлеб#translations_en
originalSourceText=bread, loaf, corn, crops, cereals, grain, living, means of subsistence, daily bread
shippedText=bread
transformation=sense selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=7d4541e8a872401aa53c2b908e7dddaada31c7709bbc200fb61c60cc0ecbb988
contentHash=985604c2b60243122c24d4e18363e6434c535923be05f760204a1aef023aae9b
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-gloss-чай`

- Objectives: obj-u3-gender
- Sessions: u3-s-orient, u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: a23c5d314334ecf03df642bcd43b06efa71f8194da5918a25d8fec36cde99cea

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:чай#translations_en
originalSourceText=tea, tea-party, methinks, maybe
shippedText=tea
transformation=sense selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=3009912d2a1eafb654f47fbc488827c4ec878b8abed3331d65bf486e148d9458
contentHash=a9f74d1ec36ebdeb2da3f6e5868090cd2a2d20b3dcca7b62f60304b1d3d9ef42
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-gloss-школа`

- Objectives: obj-u3-gender
- Sessions: u3-s-orient, u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 3a8d85ae326f9a8721ce689004b199069772d639320de33bb117e62058f6f0e1

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:школа#translations_en
originalSourceText=school, schoolhouse
shippedText=school
transformation=sense selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=744d00a71f25a56c2e34f2373d7e0a8d75d2815665f3c5a70bb277c8d092ad28
contentHash=d64debd942d7dc26a851231583b1721f43ea936fa41932b6dad7556e5f8cd24a
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-label-feminine`

- Objectives: obj-u3-gender
- Sessions: u3-s-orient, u3-s-gender
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: ff923d3a35dcecd2e3be2ea4bdc04cf0b567d7a00b15647b9b8c9529946ca674

recordType=reference
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Gender?oldid=4310736
sourceRevision=4310736
sourceRecordId=gender:So, in Russian, nouns have an intrinsic property called 'gender', which comes in
originalSourceText=So, in Russian, nouns have an intrinsic property called 'gender', which comes in three types: masculine, feminine, and neuter.
shippedText=feminine
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=4fc74bf7c3fc6662336d7cb9f8485e1d357fd2e7222c4ee11f4670d03aef047e
contentHash=584a6898b0a5a6131f4ed1c8c80a773d6a717aabbe86dcd4a9c6af40d893f212
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-label-masculine`

- Objectives: obj-u3-gender
- Sessions: u3-s-orient, u3-s-gender
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 1f4faebefa42dd7b9279fd936de8d431c8fcff75113c5a8e77991bc2a1ff870e

recordType=reference
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Gender?oldid=4310736
sourceRevision=4310736
sourceRecordId=gender:So, in Russian, nouns have an intrinsic property called 'gender', which comes in
originalSourceText=So, in Russian, nouns have an intrinsic property called 'gender', which comes in three types: masculine, feminine, and neuter.
shippedText=masculine
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=4fc74bf7c3fc6662336d7cb9f8485e1d357fd2e7222c4ee11f4670d03aef047e
contentHash=b01cc7b0a6012d960af4357a1e27f2d14f5b605c42d1d1372b0f2d7f6f328087
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-label-neuter`

- Objectives: obj-u3-gender
- Sessions: u3-s-orient, u3-s-gender
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: a8ecc0e845fddfbda0ad8bde8e825b1e6de3f1f90e5b5fa488739fed05708c52

recordType=reference
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Gender?oldid=4310736
sourceRevision=4310736
sourceRecordId=gender:So, in Russian, nouns have an intrinsic property called 'gender', which comes in
originalSourceText=So, in Russian, nouns have an intrinsic property called 'gender', which comes in three types: masculine, feminine, and neuter.
shippedText=neuter
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=4fc74bf7c3fc6662336d7cb9f8485e1d357fd2e7222c4ee11f4670d03aef047e
contentHash=de9b83328c6a435fd501b5b858c7c391b9e1643bcab174fa925d0f87c5f8d98c
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-noun-брат`

- Objectives: obj-u3-gender
- Sessions: u3-s-orient, u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 5fad9a21d8a08cd7eebc0c45ff7dfe34ed5e716788770548a28adb0abf3e7e3c

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:брат#accented
originalSourceText=брат
shippedText=брат
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=d99306448e987d2ba1fcec05cfa520ffb0d0df90d6caea50b1263dc62637e5e4
contentHash=d99306448e987d2ba1fcec05cfa520ffb0d0df90d6caea50b1263dc62637e5e4
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-noun-вода`

- Objectives: obj-u3-gender
- Sessions: u3-s-orient, u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 94313148b470957e799cff48e53d09507688504f77c23089e50c86bc7f5e3ee9

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:вода#accented
originalSourceText=вода'
shippedText=вода́
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=a4ed5e6d90edd8d25bfd73d299f41c3bb0c6a576a6ea8a4e2fab786dfc226daf
contentHash=a65ab4b8f92074796d43ac3fe935a546900f64a0b58494c71e26f479b450709a
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-noun-город`

- Objectives: obj-u3-gender
- Sessions: u3-s-orient, u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 3b392696dbbc509d036bdd8ce7bc1d084f1d576282b602e1b727309e65a346dd

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:город#accented
originalSourceText=го'род
shippedText=го́род
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=090db28c5c196fa3f1b9b6a45f113d4dc8f38ee8869563581ead01c30d2cc011
contentHash=066ebba99dbdd529d7e462191087c6809b63515f5440e80b41d89b14f1c4450f
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-noun-дверь`

- Objectives: obj-u3-gender
- Sessions: u3-s-orient, u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 1e9de79e0d03a99e33dcc164f52d30933c0bf63600852d9dd52a3ff4f2e44bb9

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:дверь#accented
originalSourceText=дверь
shippedText=дверь
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=2415daa86a0b2b6411c755047ee2ed10c4c6f41397b2f6c43d31aad27b53d6df
contentHash=2415daa86a0b2b6411c755047ee2ed10c4c6f41397b2f6c43d31aad27b53d6df
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-noun-дом`

- Objectives: obj-u3-gender
- Sessions: u3-s-orient, u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: cb6357d87e3bf0dca9cddf12aa8c720fd71c947985d94cd24d6971edaebfd0dd

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:дом#accented
originalSourceText=дом
shippedText=дом
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=f5b3d757a732cd0f69fa04ad173952cc6d9e325118b3e83364bd017a0fbda02c
contentHash=f5b3d757a732cd0f69fa04ad173952cc6d9e325118b3e83364bd017a0fbda02c
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-noun-друг`

- Objectives: obj-u3-gender
- Sessions: u3-s-orient, u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: e90b1557267cd69ac94ecd09e180c494dd5c53c144a2a37028f86aff750f7da1

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:друг#accented
originalSourceText=друг
shippedText=друг
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=a963d21beea64b7e2630a5ddd1a7e8673fd457896ffdae03242bda9f147aa354
contentHash=a963d21beea64b7e2630a5ddd1a7e8673fd457896ffdae03242bda9f147aa354
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-noun-дядя`

- Objectives: obj-u3-gender
- Sessions: u3-s-orient, u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 5eedf3c14104e37db0350d46f80806d4cd49b33b5462332d0f3980ab81a65e71

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:дядя#accented
originalSourceText=дя'дя
shippedText=дя́дя
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=141cbed19cbf737da4af6ffb006072b5487ea99a05c54b91fcbf561fecc30692
contentHash=c21b8f3ed9a08e6c5708a94a4b2a81a6fecd39e85174f8dc2bfb5dc9658a3855
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-noun-женщина`

- Objectives: obj-u3-gender
- Sessions: u3-s-orient, u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: c9b8b0d5797798442ca15115447d0124d271d458a8d341cbeadbd4b2bc41f504

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:женщина#accented
originalSourceText=же'нщина
shippedText=же́нщина
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=d9a13b025f0e6b4f7717fda208817d80af07700cea9b3f2e156ebc1460262c5e
contentHash=79c5e99469ee12c11c7e40b6f78487be6c43f02ecfdddffea233621811f428f0
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-noun-книга`

- Objectives: obj-u3-gender
- Sessions: u3-s-orient, u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 4e37677c30c6eda15b71a1cf8951dabcac022d2d84f2cbe78cfdc5ca904ad46c

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:книга#accented
originalSourceText=кни'га
shippedText=кни́га
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=c2f70c7689eb1b282bbb1f165038ea6eeb4c785f1d7abebf94a9a2f4a861059a
contentHash=47bf48ae649e95e468ffbf192033c48627e3022a30cd0040c9b860d6f3ec9d9d
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-noun-мама`

- Objectives: obj-u3-gender
- Sessions: u3-s-orient, u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 08ee5e258982d2112f968f2b11b4a76da92ce6385574722c6faa7fcd10981401

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:мама#accented
originalSourceText=ма'ма
shippedText=ма́ма
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=90fa96f6c07057bb5647bdb089766761636f5ddb2da623520f1f7f149048be34
contentHash=1cc52d4a8513a3b381822de8fdf4fd0d5314592a82e9e70af02311cefaf3023d
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-noun-место`

- Objectives: obj-u3-gender
- Sessions: u3-s-orient, u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: b4cad04265a02bb9e31e48652fbd1b30977449e5ea80ab11d17d1d4950875d8e

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:место#accented
originalSourceText=ме'сто
shippedText=ме́сто
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=c30eeb27da37f2b97be9fda7aa57dedee761aedfa53c5a2aa7329fc74e76e4f5
contentHash=2ffa7e23cd62033fc702f610b7b7c7e5913992585b944ab933953980367356f4
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-noun-молоко`

- Objectives: obj-u3-gender
- Sessions: u3-s-orient, u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 689345cd6401a38a8e63d9c47961b761e097113c7fe7e9242bcc298563c6b5cd

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:молоко#accented
originalSourceText=молоко'
shippedText=молоко́
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=10bf7d4419062fc168c35fc41b7ba5933f61abeddf942ff9003b59c6f7889143
contentHash=950b35935ab509ec3b9cca9d9f39bf4a011d2c8fd3f18770b69ead99c52eaa4b
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-noun-море`

- Objectives: obj-u3-gender
- Sessions: u3-s-orient, u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: a15442f692d5432c1e943bf5984c3907d2f93793bcb816fefdeba0386c40e4da

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:море#accented
originalSourceText=мо'ре
shippedText=мо́ре
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=35fc5f3a0262539ac8d28b0a7c8d9737f7bbbacf1cf06c8135687bc98d770a70
contentHash=5e5597931690dbb0b8edc3cb9edaf82b43ed23bee5f7a7e76965213959d29743
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-noun-мужчина`

- Objectives: obj-u3-gender
- Sessions: u3-s-orient, u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: e8feb16f577abc67be5126733c678eb4cb7e3955c02566489ccafd21f8f6e937

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:мужчина#accented
originalSourceText=мужчи'на
shippedText=мужчи́на
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=662078666ea35556a233f0bfbd7db0411d732188789e79fc5f5f6f4bf12b344a
contentHash=2f125021876e6e5acbc534118d9abbecef9be49659ddd1ae5dbbf0432ca58fbf
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-noun-окно`

- Objectives: obj-u3-gender
- Sessions: u3-s-orient, u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: b9d6d3e3b11db69c6e32ab8db524b0bfce98475caf1c260ccc048dc01dd6d37c

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:окно#accented
originalSourceText=окно'
shippedText=окно́
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=c34e235593d5b441845aaa54c61a2fd7b95eef367b9274284b75a2d4c37ad5bd
contentHash=a91dcd700e39c50c5c483c4028f0c413e6d0d01e3fd1825c72d1d99b5067880f
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-noun-письмо`

- Objectives: obj-u3-gender
- Sessions: u3-s-orient, u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 31964aca903ea946db7682954a68bbdae6c0d33a33f5d07da7aef66e3cf428ef

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:письмо#accented
originalSourceText=письмо'
shippedText=письмо́
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=b4fe9923ef6996feb055ba7d160f18648b97dafd1e994a0b6ad82fbb2b0f6a17
contentHash=79902eb2445ef138027e177fcfcbad8efa4e83ebc068db54b8e0cda32b5c025c
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-noun-работа`

- Objectives: obj-u3-gender
- Sessions: u3-s-orient, u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 11c88f912d61376a8c0477a4b6932710900b7abd6694ffe5e030a83aa56e0e3a

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:работа#accented
originalSourceText=рабо'та
shippedText=рабо́та
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=f28c270b7e5015b74c8312823d11d70753690af5170f699f95177ffa183ee2ef
contentHash=f74712cffc7c92eefeed280264f7acdc772dffa21760be25132d66e6ea4f3be9
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-noun-семья`

- Objectives: obj-u3-gender
- Sessions: u3-s-orient, u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 49f670ed632f571d1dba507700f6dabaa1be466924173df556ece936c1b7e08b

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:семья#accented
originalSourceText=семья'
shippedText=семья́
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=6339f830fba7247776f4a5576daecdb6d2b1069f5782cb86eae3d442280fe303
contentHash=0b7e61f1b62c80eeb8f66c255cfefa3215e3fabb43b90103c59cde35b572dc65
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-noun-слово`

- Objectives: obj-u3-gender
- Sessions: u3-s-orient, u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 8be48737c5890728e637fc97be7de6b67b590c36017324e96d035fd62cd9db39

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:слово#accented
originalSourceText=сло'во
shippedText=сло́во
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=8defb47561d90140232f33f2f8df573bdeb2913ac09306a041d6b144b9afe2a2
contentHash=6fcea07b919f3e48199d961bf5027eb922483fb5cc104b01f7f3cd7a6083aab2
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-noun-стол`

- Objectives: obj-u3-gender
- Sessions: u3-s-orient, u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: e45d9aa1a05a5f623c2aaef3d1d9dc0bb8a93bc47808f00748c0df6c3fa30ce3

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:стол#accented
originalSourceText=стол
shippedText=стол
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=129dde4ecad0c02e88fb74a179b08337e5ac02fcfb4146ed681c12f8baa6d532
contentHash=129dde4ecad0c02e88fb74a179b08337e5ac02fcfb4146ed681c12f8baa6d532
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-noun-учитель`

- Objectives: obj-u3-gender
- Sessions: u3-s-orient, u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 280e36e8b95627d0240e486ee80a55ae2713f60fed4490ad7399533f00d3df3b

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:учитель#accented
originalSourceText=учи'тель
shippedText=учи́тель
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=d9d921149c4cb276aab3caa34a92aef9e0c3df8bb8460aae611476b1b19e7787
contentHash=037b0dff83964605488915901e620e29f769c25ee275b205a2aca9ee9446b6eb
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-noun-хлеб`

- Objectives: obj-u3-gender
- Sessions: u3-s-orient, u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: b907a91d8082ffdff0d6b3da9a2f8a1978da6d930c3a2a2d47042d34f082ae7e

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:хлеб#accented
originalSourceText=хлеб
shippedText=хлеб
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=c0cbec0ef9a935b04b4a7c9cf689d57ac03980d6b9ae06b7ef10723e96991045
contentHash=c0cbec0ef9a935b04b4a7c9cf689d57ac03980d6b9ae06b7ef10723e96991045
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-noun-чай`

- Objectives: obj-u3-gender
- Sessions: u3-s-orient, u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: d755e9bc435eb3e0cfae947ef3fff8b1360dad556667927c3ea4c79f59dcb522

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:чай#accented
originalSourceText=чай
shippedText=чай
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=7ce1859ed1548a3eb08bc1353d0e555783b965fe66f470d5cf3df41e66a82f4f
contentHash=7ce1859ed1548a3eb08bc1353d0e555783b965fe66f470d5cf3df41e66a82f4f
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-noun-школа`

- Objectives: obj-u3-gender
- Sessions: u3-s-orient, u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 0e6895ff6b5c4f0f78f7b3a476b264203f0497e393722d2b6313c1b47ea55da1

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:школа#accented
originalSourceText=шко'ла
shippedText=шко́ла
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=63ae0cc68fba92c827fd3580cedc129096255736eae80f992ec1993564e58018
contentHash=a1e6566d8932c185b6185cab8d8cc56c3349605f5d991af4901088e235db3e05
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-phrase-chto-eto`

- Objectives: obj-u3-naming
- Sessions: u3-s-orient, u3-s-naming
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 0944a2a40339edfb109de7f17ad7a6893564afa9a6aa5a2de766a5e716c6fb68

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Interrogative_Pronouns?oldid=4220316
sourceRevision=4220316
sourceRecordId=interrogative:What is this? - '''Что это?'''
originalSourceText=What is this? - '''Что это?'''
shippedText=Что это?
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=12ae595239693186b141c4145c4129aa8ff78a0e075834ecc84d251dece36d5e
contentHash=6d01e644b98ee4b25a2f7aaf1a0175929d16504093ff55c9730dff78c8de92c0
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-phrase-chto-eto-en`

- Objectives: obj-u3-naming
- Sessions: u3-s-orient, u3-s-naming
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: b0a9080b28c7433b99f312f4218c880e44eb564c48a98b4d4f545bd73c967139

recordType=reference
sourceUrl=https://en.wikibooks.org/wiki/Russian/Interrogative_Pronouns?oldid=4220316
sourceRevision=4220316
sourceRecordId=interrogative:What is this? - '''Что это?'''
originalSourceText=What is this? - '''Что это?'''
shippedText=What is this?
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=12ae595239693186b141c4145c4129aa8ff78a0e075834ecc84d251dece36d5e
contentHash=0b12ba39ab4d0851c8a36c5bbc9997333c630060bee493b9c2cfe78f6dc25c27
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-phrase-gde-vy`

- Objectives: obj-u3-phrases
- Sessions: u3-s-orient, u3-s-phrases
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 6f1af9e78c9ead371464063326fd354263f5b67bbc0abcbe0f2d8994427807e4

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Personal_Pronouns?oldid=3598527
sourceRevision=3598527
sourceRecordId=personal-pronouns:Where are you? - '''Где вы?'''
originalSourceText=Where are you? - '''Где вы?'''
shippedText=Где вы?
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=00f939a75ec4ca5aacc3e2fb4098334e1fee0d12e1cce004476bd6a07e6ff56c
contentHash=9b3ccf75fb745f3774f33287d92bb92a5ff8ddb0b8072108137c9300897d7c75
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-phrase-gde-vy-en`

- Objectives: obj-u3-phrases
- Sessions: u3-s-orient, u3-s-phrases
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: f49cfa31e3928307868e18bf323c007d380c143cedfcfd46ffbd5b51f6543aea

recordType=reference
sourceUrl=https://en.wikibooks.org/wiki/Russian/Personal_Pronouns?oldid=3598527
sourceRevision=3598527
sourceRecordId=personal-pronouns:Where are you? - '''Где вы?'''
originalSourceText=Where are you? - '''Где вы?'''
shippedText=Where are you?
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=00f939a75ec4ca5aacc3e2fb4098334e1fee0d12e1cce004476bd6a07e6ff56c
contentHash=0217318f31feb0d83fc26b32e06c3399491ac7ede75f58b0da23fe683bb7516c
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-phrase-gloss-да`

- Objectives: obj-u3-phrases
- Sessions: u3-s-orient, u3-s-phrases
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 3feffc5801cd182f5cda40c6a822a793cd31fdcc78304d6bfe59dc76f39a0470

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/others.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=others.csv:да#translations_en
originalSourceText=yes
shippedText=yes
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=8a798890fe93817163b10b5f7bd2ca4d25d84c52739a645a889c173eee7d9d3d
contentHash=8a798890fe93817163b10b5f7bd2ca4d25d84c52739a645a889c173eee7d9d3d
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-phrase-gloss-здравствуйте`

- Objectives: obj-u3-phrases
- Sessions: u3-s-orient, u3-s-phrases
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 1c064f5afbcbaf46021f3fcc7383139345c4119991b672693fb62341c14175f4

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/others.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=others.csv:здравствуйте#translations_en
originalSourceText=hello
shippedText=hello
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824
contentHash=2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-phrase-gloss-нет`

- Objectives: obj-u3-phrases
- Sessions: u3-s-orient, u3-s-phrases
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: ee1fe548c67911f7a1bae42fd4c1f4d1688c195519d74693939ba9037a2d53bb

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/others.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=others.csv:нет#translations_en
originalSourceText=no; not
shippedText=no
transformation=sense selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=a190e343b9687b148d11f172ee614b0a71efbc89c99f343042fc39e25f51dbaa
contentHash=9390298f3fb0c5b160498935d79cb139aef28e1c47358b4bbba61862b9c26e59
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-phrase-gloss-пожалуйста`

- Objectives: obj-u3-phrases
- Sessions: u3-s-orient, u3-s-phrases
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: bd88f3e1ed48481f4e3470f09bf803d199c311e52ddd581ebcbba222f5610afc

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/others.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=others.csv:пожалуйста#translations_en
originalSourceText=please; you are welcome; here you go
shippedText=please
transformation=sense selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=c616f22a7ef90833876c8e2c6bd2c86e556aae8beba94d7068d8fbe2bed2b946
contentHash=56ccc4dcfc96534b06fc0c08a301be24f13b491484d5d984953cc0dba9bbb89a
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-phrase-gloss-спасибо`

- Objectives: obj-u3-phrases
- Sessions: u3-s-orient, u3-s-phrases
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: d814658428e9af8676c415805521d1ff8da0a5e13fbaed659e078e1cdd47a953

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/others.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=others.csv:спасибо#translations_en
originalSourceText=thanks, thank you
shippedText=thank you
transformation=sense selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=040c9af8a14868e1b861b62cd735b8c2e3262c49382d0b1b619d71d3f8c6d31e
contentHash=844347e54f00c4b97fe4736909730faaf8365292b076ea5a1378ebd1b0fd3bbb
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-phrase-gloss-это`

- Objectives: obj-u3-phrases
- Sessions: u3-s-orient, u3-s-phrases
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 62ad84db6b0eacf70e093e81c1a32ee817e6aa447ec80eaabf3de4d04760b8fe

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/others.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=others.csv:это#translations_en
originalSourceText=this is; that is; it
shippedText=this is
transformation=sense selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=2ae4787ff993113572cde2323a608e6d410eb731620c092eade5fff0527692d8
contentHash=42b57632c93fb87d5f6de87d299eeda64dadbb61376eb196bce5c58cefaac594
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-phrase-kak-dela`

- Objectives: obj-u3-phrases
- Sessions: u3-s-orient, u3-s-phrases
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 7e7024215265358fbdc0a4e2281278ec62f24f74d88dbc84fb3cbf93efa897c3

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Interrogative_Pronouns?oldid=4220316
sourceRevision=4220316
sourceRecordId=interrogative:How do you do?/How're things? - '''Как дела?'''
originalSourceText=How do you do?/How're things? - '''Как дела?'''
shippedText=Как дела?
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=b331faaaa037cedc1e8e8de6ea9e3a93ea5d5601f12380a2811698d28e6c657c
contentHash=1a15be1357f7096aa23684c982b679ce6d678395aa608332cfc22c0ef0248275
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-phrase-kak-dela-en`

- Objectives: obj-u3-phrases
- Sessions: u3-s-orient, u3-s-phrases
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 4430d168aa22cfeabdb5af96ea47d19e512fbae3c088cb1dadb81a79dc1ba993

recordType=reference
sourceUrl=https://en.wikibooks.org/wiki/Russian/Interrogative_Pronouns?oldid=4220316
sourceRevision=4220316
sourceRecordId=interrogative:How do you do?/How're things? - '''Как дела?'''
originalSourceText=How do you do?/How're things? - '''Как дела?'''
shippedText=How do you do?/How're things?
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=b331faaaa037cedc1e8e8de6ea9e3a93ea5d5601f12380a2811698d28e6c657c
contentHash=0fc06c8a178c6b7ed6f6be37c455dfe12b9d3152e83f676ebf23e9ecb033c6a7
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-phrase-kto-eto`

- Objectives: obj-u3-naming
- Sessions: u3-s-orient, u3-s-naming
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 42ba3d834c56883930fee7712689867d32dd45c112f073f40b632dc32f994c18

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Interrogative_Pronouns?oldid=4220316
sourceRevision=4220316
sourceRecordId=interrogative:Who is this? - '''Кто это?'''
originalSourceText=Who is this? - '''Кто это?'''
shippedText=Кто это?
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=dcad1ab245cf9852c5f80772daa0a09ac6c8134df9114a4a2f63ba7cf6f057f0
contentHash=e0e90af0c003af356781bbdcbc783ff6fd44b5b2858edd04e130fc9c75ec7720
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-phrase-kto-eto-en`

- Objectives: obj-u3-naming
- Sessions: u3-s-orient, u3-s-naming
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: e27b8ef60651875e7e8149c3fb0d00a5ce2d5b6e13a4a64ac361b1448ca40fcf

recordType=reference
sourceUrl=https://en.wikibooks.org/wiki/Russian/Interrogative_Pronouns?oldid=4220316
sourceRevision=4220316
sourceRecordId=interrogative:Who is this? - '''Кто это?'''
originalSourceText=Who is this? - '''Кто это?'''
shippedText=Who is this?
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=dcad1ab245cf9852c5f80772daa0a09ac6c8134df9114a4a2f63ba7cf6f057f0
contentHash=b7f28b09afe416287024cabcba27e176d8516368662a2cd99e283e4ad219e46b
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-phrase-moy-stol`

- Objectives: obj-u3-naming
- Sessions: u3-s-orient, u3-s-naming
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 7d6046c53e9b7b820f7d5a152aaf0895c4ac6b3b401b316c310f1aedd69d5f40

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Gender?oldid=4310736
sourceRevision=4310736
sourceRecordId=gender:'my table' ('''мой стол''')
originalSourceText='my table' ('''мой стол''')
shippedText=мой стол
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=032953c853db8131b96b61bfdfe6e8aa344849dd154ca7c4370645da0050e290
contentHash=2879fccf51dbf2edc6bf6261a7ffc585f1cb2ae5cc44756a53359c4e7159f109
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-phrase-moy-stol-en`

- Objectives: obj-u3-naming
- Sessions: u3-s-orient, u3-s-naming
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: c782ff7f3413ec5be90b3b5623ebed9cd1edc6a1aa9be0404a7a880d4ed819a6

recordType=reference
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Gender?oldid=4310736
sourceRevision=4310736
sourceRecordId=gender:'my table' ('''мой стол''')
originalSourceText='my table' ('''мой стол''')
shippedText=my table
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=032953c853db8131b96b61bfdfe6e8aa344849dd154ca7c4370645da0050e290
contentHash=0d24581ae439efb17cc00f11104ff904c6ce352f4503f2760ee3800eb452ff49
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-phrase-moya-sobaka`

- Objectives: obj-u3-naming
- Sessions: u3-s-orient, u3-s-naming
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: f25acbf57e98343e906ce8e85d60f44ed02982e2ee18d791b83693c073b355fd

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Gender?oldid=4310736
sourceRevision=4310736
sourceRecordId=gender:'my dog' ('''моя собака''')
originalSourceText='my dog' ('''моя собака''')
shippedText=моя собака
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=cbd27fced54bfb6c8df47747a7fca80056a9196184dd2087cd77cbab8cebf4a1
contentHash=0cc1d7edfc689373a1837831b7432b54a1d85ced56a2b0916cca0f403bd597ed
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-phrase-moya-sobaka-en`

- Objectives: obj-u3-naming
- Sessions: u3-s-orient, u3-s-naming
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 39c6e2e4796a5645493d7b00f7e487fe10323a16253cfc21a5a679eed727fbc9

recordType=reference
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Gender?oldid=4310736
sourceRevision=4310736
sourceRecordId=gender:'my dog' ('''моя собака''')
originalSourceText='my dog' ('''моя собака''')
shippedText=my dog
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=cbd27fced54bfb6c8df47747a7fca80056a9196184dd2087cd77cbab8cebf4a1
contentHash=9902cb4b71c6127d15ceb15fe5cd5a4a59283447ff1967dac8e1bcdd1116e7e2
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-phrase-да`

- Objectives: obj-u3-phrases
- Sessions: u3-s-orient, u3-s-phrases
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: b3828d9033f9b7bb97071e160adad2f3e483357cfe740afb069295aa59dcfe87

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/others.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=others.csv:да#accented
originalSourceText=да
shippedText=да
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=cdc5ed90cb4b9a7540b24d1c45c23c851930c98b07b6885667114c9f95fb0a12
contentHash=cdc5ed90cb4b9a7540b24d1c45c23c851930c98b07b6885667114c9f95fb0a12
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-phrase-здравствуйте`

- Objectives: obj-u3-phrases
- Sessions: u3-s-orient, u3-s-phrases
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 94d2b0fd17575a0eb8c3194a66077548c66648187e0861b32c8d375140a6def3

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/others.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=others.csv:здравствуйте#accented
originalSourceText=здра'вствуйте
shippedText=здра́вствуйте
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=f43f3e0c83746c23a6d9cfbc9be1f36c78d9f2ef9fe6dd81a420c48876fc0c01
contentHash=9c09b0ed81d77c5bb12288cebb958c9f85b5923f5cae0b5c2fd6df540fbdeab0
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-phrase-нет`

- Objectives: obj-u3-phrases
- Sessions: u3-s-orient, u3-s-phrases
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 0a6a51f80bd3cbc2206d7e3073306e51d43a0b5d31a7d19e59165e9faa879aaa

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/others.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=others.csv:нет#accented
originalSourceText=нет
shippedText=нет
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=dc9337ff5b423e6828cd8f164d022d3a02b4efeaf7a0d2f60eaa032b7d9dcd3f
contentHash=dc9337ff5b423e6828cd8f164d022d3a02b4efeaf7a0d2f60eaa032b7d9dcd3f
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-phrase-пожалуйста`

- Objectives: obj-u3-phrases
- Sessions: u3-s-orient, u3-s-phrases
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 3fb7094280fec9e21363a5e3162c04928d413a9614730098fe1255d6d68e9320

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/others.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=others.csv:пожалуйста#accented
originalSourceText=пожа'луйста
shippedText=пожа́луйста
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=0a5b051c75054873ebb39d2c216889e23634ffd5985ca26b3cb9c195d9ccfbdd
contentHash=51811bfac87fa2cb92f1ef4d649c430e5bf62501ad56f5d3fbcf3042b6239b97
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-phrase-спасибо`

- Objectives: obj-u3-phrases
- Sessions: u3-s-orient, u3-s-phrases
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: cb6b0fb113d47118e84999d464de9f7508992369198d268a38982869222c10c0

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/others.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=others.csv:спасибо#accented
originalSourceText=спаси'бо
shippedText=спаси́бо
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=0a033c51b7dfa9d94581032d278ff06857aec9caf0ff08ba9e5c986b45640414
contentHash=03f4b03090fb668abaab30f3cca8f8a15d01d3035dc3c63350490f6190276611
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u3-phrase-это`

- Objectives: obj-u3-phrases
- Sessions: u3-s-orient, u3-s-phrases
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 017554c9cdef7cdafb88260d301bade883e02d0d1ffa2672f8c1301954485b54

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/others.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=others.csv:это#accented
originalSourceText=э'то
shippedText=э́то
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=7f4bc6f7b2f4d2429779bc656f080d17eef1cb068d61a6fb53f60d8a3a589dbd
contentHash=ed7301bf2bd7dd4c3996ad40b12376395f5f411e19c620d5519596ee1a3cca11
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### AUTHORED_ENGLISH `en-ca-3-11124`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 68277413165a79992a2cc0e39fc3af30321e24ff1e585ebb4d8e6f841fa13181

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Unit 3 AnkiDroid direction

### AUTHORED_ENGLISH `en-ca-3-11125`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 6f298c576fb10addbf0cdba57b07339134c55cfb67e53ce8509151a5a245d483

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Self-attest AnkiDroid work. It remains user-reported and does not demonstrate a check.

### AUTHORED_ENGLISH `en-ca-3-11126`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: b24cadeb04f9f126e017a8c397f0534715a5e175dc12fca12ac991c1eb62bc68

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Unit 3 binder prompt

### AUTHORED_ENGLISH `en-ca-3-11127`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: bdd2a04fa777039e1d069f79ccbf5b0e4c7bc97bc884d011a3acddd81cd6e4d7

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=A binder prompt is one specific note, not a score.

### AUTHORED_ENGLISH `en-ca-3-11128`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 11391caa02f8fb8f663f2311a3d37c18c46e06195260d7dd8a5e7b98ea5b7f0b

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Unit 3 optional tutor

### AUTHORED_ENGLISH `en-ca-3-11129`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 053945b803b38f9dba9563d5c4177dacac1c63fce7a3849413ee1e9f4eabbc63

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Tutor work is optional and does not count as demonstrated ability.

### AUTHORED_ENGLISH `en-ca-3-11130`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 69d7a93ee7eb90d01273e51297a7227470ad469520cd6d05b50a3df42468241a

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Unit 3 cumulative review

### AUTHORED_ENGLISH `en-ca-3-2`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 758647c43dfbe0127aea18d952b6af34a76cc52aa3e47142e59df421c2c2db17

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Owned-book assignment for Lesson 3: printed pages 24-35. PDF indexes 36-47 are file locators, not printed pages. Observed section locators: §3.1 p24 (PDF 36); §3.2 p25 (PDF 37); §3.3 p25 (PDF 37); §3.4 p26 (PDF 38); §3.5 p26 (PDF 38); §3.6 p26 (PDF 38); §3.7 p26 (PDF 38); §3.8 p27 (PDF 39); §3.9 p29 (PDF 41); §3.10 p29 (PDF 41); §3.11 p31 (PDF 43); §3.12 p32 (PDF 44). Observed exercise locators: 3/2 p32/PDF44; 3/3 p33/PDF45; 3/4 p33/PDF45; 3/5 p33/PDF45; 3/6 p33/PDF45. These locators are pending reviewer assignment and are not an approved mapping. Do the work in the copy you own. Book activity is not a demonstrated app check.

### AUTHORED_ENGLISH `en-ca-3-3`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 15384c8e021227574d0fbb6195d770da033d419a55e08411735e7701b9b94eca

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Unit 3 AnkiDroid self-attestation uses only cards you are allowed to make. Binder: write one form from this unit that you missed and the correction you will use. Tutor conversation is optional and is not a demonstrated check. Cumulative review revisits completed objectives from earlier units. Activity is not demonstrated ability. Open the offline grammar reference for this unit when one is linked.

### AUTHORED_ENGLISH `en-ca-3-4`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: b1020177ef3a9abc16f1f99bf75cdbde6ddc161a4017ac2f6eda2636589047b7

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Read this as a review-pending explanation. Worked examples come before the guided choice, and the independent check is separate. A wrong answer returns to this contrast. Activity is not demonstrated ability.

### AUTHORED_ENGLISH `en-u3-001`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 3d17fd25647d8bbc3ed81e33028ad891f39a678d051d6a3fccb08ba2c670372c

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Which admitted label is recorded for the shown word?

### AUTHORED_ENGLISH `en-u3-002`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: e96bd02faa079e512f71c2d450b17fadd24620db1e31f9e1da34d4e917c58158

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=The admitted record lists this as 

### AUTHORED_ENGLISH `en-u3-003`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: d9bc82017a2ada71bc6cbb5de44a40b1bf7348f291cc5b5996827f03bfc5d62e

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=.

### AUTHORED_ENGLISH `en-u3-004`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 988e135e575c62fd1f276ffc95b3d2e061c622710a14cacbe985ed33bc004c20

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Which admitted gloss is recorded for the shown item?

### AUTHORED_ENGLISH `en-u3-005`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 619c962437444e80170a8b6cf48886631570aac3ddce1b8ab035b2a37aff372d

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=The frozen dictionary row records this gloss: 

### AUTHORED_ENGLISH `en-u3-006`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 63b5de72812ff86e826c276e171fc1d7708aeb6e0543f284652eaeab5bd35fe6

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Which admitted meaning is recorded for the shown item?

### AUTHORED_ENGLISH `en-u3-007`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: f2a16d3591f59b465194599cbef1e6af89a18cb6bbce203a43f3ab127061b7bc

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=The frozen page records this meaning: 

### AUTHORED_ENGLISH `en-u3-008`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 94555f5f6f5cf1761ec90a3e1ea418890ebac080a60b8b004d55d35bafe45071

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Read the admitted grouping sentence

### AUTHORED_ENGLISH `en-u3-009`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: ca0adda2aab1f5c4b1bee5ba2afe70ff73517f44b78f28ac898e2dda8933b198

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=The frozen page says: 

### AUTHORED_ENGLISH `en-u3-010`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: ec1705fedd4c1d04dd4f16a8a0b5e5ed24999971e0b6eb30b1cb3b40180db378

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text= An ending is not a substitute for the admitted record.

### AUTHORED_ENGLISH `en-u3-011`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 90b58c32382865011ce4c1f4747ef6a82679f32470b7a8e031323edc3f5118c2

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=These admitted records are listed as 

### AUTHORED_ENGLISH `en-u3-012`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 5f6534d48b5bd2744101077c231bf67ff2a121e3cda10b448e4e99f7b14a36ca

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=. 

### AUTHORED_ENGLISH `en-u3-013`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 663d4eb47b0ac4d978bd5faca6fed9a4635cc6edc8e09041b44e4059266ced42

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Four records in the first group

### AUTHORED_ENGLISH `en-u3-014`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 6b6c42f6e8a73501412ed059673edc91369d309dbadb7aff2c9a6e7fdca860e5

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Four records in the second group

### AUTHORED_ENGLISH `en-u3-015`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: ddeeec18119b043f55950e6be23ef9433d47905eedd47bbd2a5540811dd8c20d

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Four records in the third group

### AUTHORED_ENGLISH `en-u3-016`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 152baedee0308a4e2299d8890c899405a496a1440ff22cfd677d29f80b17be09

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=The frozen page says 

### AUTHORED_ENGLISH `en-u3-017`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 4ff22b671a1d9e8e42a47beebd16b4035077a78bcb287231710d52399aa962cd

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Two records the page still places in the first group

### AUTHORED_ENGLISH `en-u3-018`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 175e13cc847271b4ecfba2fff9358f37e526cc0ccaa93eb3d6e0bf46c2261f6b

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=These greetings are copied from admitted rows. 

### AUTHORED_ENGLISH `en-u3-019`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 190b369245272608b0a14cc366e7f4c44618ab848aa4128c39b4c02d3b52c467

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Three short greetings

### AUTHORED_ENGLISH `en-u3-020`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 309b0131f1b049e0270818b8b7ea61aeabde8a4ca74e140173dd1571546b742b

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=These short answers are copied from admitted rows. 

### AUTHORED_ENGLISH `en-u3-021`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 4b65ed9ca298e7f7f8997af2f3b0bb648ed4893a386c1aa502cec08fcdd9d117

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Three short answers

### AUTHORED_ENGLISH `en-u3-022`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 8f974cc3dab447d0cbac94ac65b2d10be67feafa965de577dd6894107b3a481a

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=These written questions are exact frozen excerpts. 

### AUTHORED_ENGLISH `en-u3-023`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 80ab168738d8c2c4060a15070d359463b5668200430f6afa4c9d5c11503a7737

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Two written questions from the frozen page

### AUTHORED_ENGLISH `en-u3-024`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 52fd8817fa243a17b7dc956b0837bb6fcba2c94314fc06cf2a1ddae81935eb6f

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=These two items are exact frozen excerpts. Do not substitute another word into them. 

### AUTHORED_ENGLISH `en-u3-025`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 9dd897ac2437a1d1520ce8a371feff61fb1bd281a727ca4112de1d22b82d4dda

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Two naming items from the frozen page

### AUTHORED_ENGLISH `en-u3-026`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 73e03501ee85222b7d6e0d00642b1b318cc2b24c7abe4e4e874e41e074396b69

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=These two questions are exact frozen excerpts. 

### AUTHORED_ENGLISH `en-u3-027`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 8d6613614ccb2789d085758d841408b9995f313cfa37933c51f587c20f234a12

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Two naming questions from the frozen page

### AUTHORED_ENGLISH `en-u3-028`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: f80f7d7b1a6b2e6e75ef344552af1fe04c628edbc95a5e3ee8e3b84be10bca6b

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Everyday phrases, naming, and grouping

### AUTHORED_ENGLISH `en-u3-029`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 821618523899ce5a8ae2bb0a1473ea87a7c21a3c2fe609eb874e1b4306271d02

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Proposed Unit 3 uses frozen dictionary rows and grammar excerpts for the groups, a small phrase set, and naming items. The owned book remains separate work. Qualified review is pending.

### AUTHORED_ENGLISH `en-u3-030`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: bee03d5a04e79de87e2f27660e43d6348165ee39ce2fcbaaf0b827522f671af5

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Open Lesson 3 in the copy you own. The app records do not replace that lesson.

### AUTHORED_ENGLISH `en-u3-031`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 91726e5ab02759bd1c92af2864b18dd24bc46098ed6f95b5091a1abb0231ed7e

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Guided practice

### AUTHORED_ENGLISH `en-u3-032`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 5095a646150f4979031d70ed8cbc452543dafe35147991a3128253a94cca8fe2

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Choose the admitted record. The feedback states the frozen field.

### AUTHORED_ENGLISH `en-u3-033`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 4aed349ce0e5290ceefcad50c59268117e0850c10a198b93de6f6a5cb8356857

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Remediation

### AUTHORED_ENGLISH `en-u3-034`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 9009d4d1677fbfcbdf30700b5c1358e1a5452c4fa383be24d19819556c3e2dd5

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Return to the explanation. Compare the shown form with the admitted record. Do not invent a form that is absent from the record.

### AUTHORED_ENGLISH `en-u3-035`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 714520cb00cbc23bfcb8e1a89821726034a86820a19fb9bc598ffbd4b1f70f48

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Independent checks

### AUTHORED_ENGLISH `en-u3-036`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 591654f682b2bd201a6f314796f819f0121656ba464b860e2fb8e0fe69da9a32

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=These checks use only admitted records. A miss returns to the explanation.

### AUTHORED_ENGLISH `en-u3-037`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: eb16459da9d3ab8c4e7b2c3c09e391706d54a139b1c0ab05abd6d756c355ca61

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Work Lesson 3 in your copy

### AUTHORED_ENGLISH `en-u3-038`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 4c64cf72c09f9292d943c09176c4110cfa1f25f0d1f2db1eeca0442e75315bb0

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Do Lesson 3 in the copy you own. Use that book's explanations and practice. The proposed records in this unit are a separate licensed set and do not copy the book.

### AUTHORED_ENGLISH `en-u3-039`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: e681ad0db0c2436961af7bd4944e62c7b72eae18ed5a528c9a5ec2655af4e666

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Open Lesson 3 in your copy and complete its lesson work.

### AUTHORED_ENGLISH `en-u3-040`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 278b6c68042ee03b4788f875c1cc41461e4870bb058e15b648120a4e35ea9c8b

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Record Lesson 3 book work

### AUTHORED_ENGLISH `en-u3-041`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: b3529e62d74651a0d853f17a0df82c46ca6037d8bf06c17b183c85e56212dc24

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=When you have done Lesson 3 in your copy, self-attest here. Attestation records your report. It does not grade the lesson, approve a mapping, or approve the proposed records.

### AUTHORED_ENGLISH `en-u3-042`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 8b142a075f26781fcb491dc45cf510f27811b04ed7cadb0029473a7c25d72a20

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Self-attest only after the Lesson 3 work in your copy.

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

### SOURCE `src-content-audit-research`

- Objectives: none
- Sessions: none
- Source: src-content-audit-research
- Rights: rights-content-audit-research
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 4bb7355bcdaed471645229162f908afe75e5dc0978ea6ffc5617fbb237190d97

Content-audit research notes
Original app text
Russian Companion content-audit research synthesis, content-audit-2026-10-08. Bibliography is on each note. No owned-book prose is included.
Original research synthesis for registered content corrections. Not a quotation.

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

### RIGHTS `rights-content-audit-research`

- Objectives: none
- Sessions: none
- Source: src-content-audit-research
- Rights: rights-content-audit-research
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 6e474fed734ce50e827ffb29f9c8eced5c10aa037e992e43e76435b822864375

src-content-audit-research
Original app text
permitsAppUse=true

## Instructional relationships

- `obj-u3-gender` INSTRUCTION `u3-st-orient`
- `obj-u3-gender` INSTRUCTION `u3-st-teach-gender-1`
- `obj-u3-gender` INSTRUCTION `u3-st-teach-gender-2`
- `obj-u3-gender` INSTRUCTION `u3-st-teach-gender-3`
- `obj-u3-gender` INSTRUCTION `u3-st-teach-gender-4`
- `obj-u3-gender` INSTRUCTION `u3-st-teach-gender-5`
- `obj-u3-gender` EXAMPLE `ex-u3-стол`
- `obj-u3-gender` EXAMPLE `ex-u3-дом`
- `obj-u3-gender` EXAMPLE `ex-u3-город`
- `obj-u3-gender` EXAMPLE `ex-u3-брат`
- `obj-u3-gender` EXAMPLE `ex-u3-книга`
- `obj-u3-gender` EXAMPLE `ex-u3-вода`
- `obj-u3-gender` EXAMPLE `ex-u3-школа`
- `obj-u3-gender` EXAMPLE `ex-u3-работа`
- `obj-u3-gender` EXAMPLE `ex-u3-окно`
- `obj-u3-gender` EXAMPLE `ex-u3-письмо`
- `obj-u3-gender` EXAMPLE `ex-u3-молоко`
- `obj-u3-gender` EXAMPLE `ex-u3-море`
- `obj-u3-gender` EXAMPLE `ex-u3-мужчина`
- `obj-u3-gender` EXAMPLE `ex-u3-дядя`
- `obj-u3-gender` PRACTICE `u3-st-guided-gender-2`
- `obj-u3-gender` PRACTICE `u3-st-guided-gender-3`
- `obj-u3-gender` PRACTICE `u3-st-guided-gender-4`
- `obj-u3-gender` PRACTICE `u3-st-guided-gender-5`
- `obj-u3-gender` CHECK `u3-i-gender-город`
- `obj-u3-gender` CHECK `u3-i-gender-друг`
- `obj-u3-gender` CHECK `u3-i-gender-дверь`
- `obj-u3-gender` CHECK `u3-i-gender-море`
- `obj-u3-gender` CHECK `u3-i-gender-дядя`
- `obj-u3-gender` REMEDIATION `u3-st-rem-gender`
- `obj-u3-phrases` INSTRUCTION `u3-st-orient`
- `obj-u3-phrases` INSTRUCTION `u3-st-teach-phrases-1`
- `obj-u3-phrases` INSTRUCTION `u3-st-teach-phrases-2`
- `obj-u3-phrases` INSTRUCTION `u3-st-teach-phrases-3`
- `obj-u3-phrases` EXAMPLE `ex-u3-phrase-здравствуйте`
- `obj-u3-phrases` EXAMPLE `ex-u3-phrase-спасибо`
- `obj-u3-phrases` EXAMPLE `ex-u3-phrase-пожалуйста`
- `obj-u3-phrases` EXAMPLE `ex-u3-phrase-да`
- `obj-u3-phrases` EXAMPLE `ex-u3-phrase-нет`
- `obj-u3-phrases` EXAMPLE `ex-u3-phrase-это`
- `obj-u3-phrases` EXAMPLE `ex-u3-phrase-kak-dela`
- `obj-u3-phrases` EXAMPLE `ex-u3-phrase-gde-vy`
- `obj-u3-phrases` PRACTICE `u3-st-guided-phrases-1`
- `obj-u3-phrases` CHECK `u3-i-phrase-пожалуйста`
- `obj-u3-phrases` CHECK `u3-i-phrase-да`
- `obj-u3-phrases` CHECK `u3-i-phrase-нет`
- `obj-u3-phrases` CHECK `u3-i-phrase-это`
- `obj-u3-phrases` REMEDIATION `u3-st-rem-phrases`
- `obj-u3-naming` INSTRUCTION `u3-st-orient`
- `obj-u3-naming` INSTRUCTION `u3-st-teach-naming-1`
- `obj-u3-naming` INSTRUCTION `u3-st-teach-naming-2`
- `obj-u3-naming` EXAMPLE `ex-u3-phrase-moy-stol`
- `obj-u3-naming` EXAMPLE `ex-u3-phrase-moya-sobaka`
- `obj-u3-naming` EXAMPLE `ex-u3-phrase-chto-eto`
- `obj-u3-naming` EXAMPLE `ex-u3-phrase-kto-eto`
- `obj-u3-naming` PRACTICE `u3-st-guided-naming-1`
- `obj-u3-naming` CHECK `u3-i-name-moya-sobaka`
- `obj-u3-naming` CHECK `u3-i-name-chto-eto`
- `obj-u3-naming` CHECK `u3-i-name-kto-eto`
- `obj-u3-naming` REMEDIATION `u3-st-rem-naming`
- `obj-u3-book` INSTRUCTION `u3-st-book-work`
- `obj-u3-book` INSTRUCTION `u3-st-book`
- `u3-s-orient` OBJECTIVE `obj-u3-gender`
- `u3-s-orient` OBJECTIVE `obj-u3-phrases`
- `u3-s-orient` OBJECTIVE `obj-u3-naming`
- `u3-s-orient` OBJECTIVE `obj-u3-book`
- `u3-st-orient` REFERENCE `ref-u35-attribution`
- `u3-s-gender` OBJECTIVE `obj-u3-gender`
- `u3-st-teach-gender-1` REFERENCE `ref-u35-attribution`
- `u3-st-teach-gender-2` EXAMPLE `ex-u3-стол`
- `u3-st-teach-gender-2` EXAMPLE `ex-u3-дом`
- `u3-st-teach-gender-2` EXAMPLE `ex-u3-город`
- `u3-st-teach-gender-2` EXAMPLE `ex-u3-брат`
- `u3-st-teach-gender-2` REFERENCE `ref-u35-attribution`
- `u3-st-guided-gender-2` REMEDIATION `u3-st-rem-gender`
- `u3-st-guided-gender-2` ITEM `u3-i-gender-стол`
- `u3-st-guided-gender-2` ITEM `u3-i-gender-дом`
- `u3-st-teach-gender-3` EXAMPLE `ex-u3-книга`
- `u3-st-teach-gender-3` EXAMPLE `ex-u3-вода`
- `u3-st-teach-gender-3` EXAMPLE `ex-u3-школа`
- `u3-st-teach-gender-3` EXAMPLE `ex-u3-работа`
- `u3-st-teach-gender-3` REFERENCE `ref-u35-attribution`
- `u3-st-guided-gender-3` REMEDIATION `u3-st-rem-gender`
- `u3-st-guided-gender-3` ITEM `u3-i-gender-книга`
- `u3-st-teach-gender-4` EXAMPLE `ex-u3-окно`
- `u3-st-teach-gender-4` EXAMPLE `ex-u3-письмо`
- `u3-st-teach-gender-4` EXAMPLE `ex-u3-молоко`
- `u3-st-teach-gender-4` EXAMPLE `ex-u3-море`
- `u3-st-teach-gender-4` REFERENCE `ref-u35-attribution`
- `u3-st-guided-gender-4` REMEDIATION `u3-st-rem-gender`
- `u3-st-guided-gender-4` ITEM `u3-i-gender-окно`
- `u3-st-teach-gender-5` EXAMPLE `ex-u3-мужчина`
- `u3-st-teach-gender-5` EXAMPLE `ex-u3-дядя`
- `u3-st-teach-gender-5` REFERENCE `ref-u35-attribution`
- `u3-st-guided-gender-5` REMEDIATION `u3-st-rem-gender`
- `u3-st-guided-gender-5` ITEM `u3-i-gender-мужчина`
- `u3-st-rem-gender` EXAMPLE `ex-u3-стол`
- `u3-st-rem-gender` EXAMPLE `ex-u3-дом`
- `u3-st-check-gender` REMEDIATION `u3-st-rem-gender`
- `u3-st-check-gender` ITEM `u3-i-gender-город`
- `u3-st-check-gender` ITEM `u3-i-gender-друг`
- `u3-st-check-gender` ITEM `u3-i-gender-дверь`
- `u3-st-check-gender` ITEM `u3-i-gender-море`
- `u3-st-check-gender` ITEM `u3-i-gender-дядя`
- `u3-s-phrases` OBJECTIVE `obj-u3-phrases`
- `u3-st-teach-phrases-1` EXAMPLE `ex-u3-phrase-здравствуйте`
- `u3-st-teach-phrases-1` EXAMPLE `ex-u3-phrase-спасибо`
- `u3-st-teach-phrases-1` EXAMPLE `ex-u3-phrase-пожалуйста`
- `u3-st-teach-phrases-1` REFERENCE `ref-u35-attribution`
- `u3-st-guided-phrases-1` REMEDIATION `u3-st-rem-phrases`
- `u3-st-guided-phrases-1` ITEM `u3-i-phrase-здравствуйте`
- `u3-st-guided-phrases-1` ITEM `u3-i-phrase-спасибо`
- `u3-st-teach-phrases-2` EXAMPLE `ex-u3-phrase-да`
- `u3-st-teach-phrases-2` EXAMPLE `ex-u3-phrase-нет`
- `u3-st-teach-phrases-2` EXAMPLE `ex-u3-phrase-это`
- `u3-st-teach-phrases-2` REFERENCE `ref-u35-attribution`
- `u3-st-teach-phrases-3` EXAMPLE `ex-u3-phrase-kak-dela`
- `u3-st-teach-phrases-3` EXAMPLE `ex-u3-phrase-gde-vy`
- `u3-st-teach-phrases-3` REFERENCE `ref-u35-attribution`
- `u3-st-rem-phrases` EXAMPLE `ex-u3-phrase-здравствуйте`
- `u3-st-rem-phrases` EXAMPLE `ex-u3-phrase-спасибо`
- `u3-st-check-phrases` REMEDIATION `u3-st-rem-phrases`
- `u3-st-check-phrases` ITEM `u3-i-phrase-пожалуйста`
- `u3-st-check-phrases` ITEM `u3-i-phrase-да`
- `u3-st-check-phrases` ITEM `u3-i-phrase-нет`
- `u3-st-check-phrases` ITEM `u3-i-phrase-это`
- `u3-s-naming` OBJECTIVE `obj-u3-naming`
- `u3-st-teach-naming-1` EXAMPLE `ex-u3-phrase-moy-stol`
- `u3-st-teach-naming-1` EXAMPLE `ex-u3-phrase-moya-sobaka`
- `u3-st-teach-naming-1` REFERENCE `ref-u35-attribution`
- `u3-st-guided-naming-1` REMEDIATION `u3-st-rem-naming`
- `u3-st-guided-naming-1` ITEM `u3-i-name-moy-stol`
- `u3-st-teach-naming-2` EXAMPLE `ex-u3-phrase-chto-eto`
- `u3-st-teach-naming-2` EXAMPLE `ex-u3-phrase-kto-eto`
- `u3-st-teach-naming-2` REFERENCE `ref-u35-attribution`
- `u3-st-rem-naming` EXAMPLE `ex-u3-phrase-moy-stol`
- `u3-st-rem-naming` EXAMPLE `ex-u3-phrase-moya-sobaka`
- `u3-st-check-naming` REMEDIATION `u3-st-rem-naming`
- `u3-st-check-naming` ITEM `u3-i-name-moya-sobaka`
- `u3-st-check-naming` ITEM `u3-i-name-chto-eto`
- `u3-st-check-naming` ITEM `u3-i-name-kto-eto`
- `u3-s-book` OBJECTIVE `obj-u3-book`
- `u3-st-book-work` REFERENCE `ref-book-topics`
- `u3-st-book-work` REFERENCE `ref-u35-attribution`
- `obj-u3-gender` PROVENANCE `prov-ca-3-346`
- `obj-u3-gender` PROVENANCE `prov-ca-3-347`
- `obj-u3-gender` PROVENANCE `prov-ca-3-348`
- `obj-u3-gender` PROVENANCE `prov-u3-dyadya-note`
- `obj-u3-gender` PROVENANCE `prov-u3-gender-sentence`
- `obj-u3-gender` PROVENANCE `prov-u3-gloss-брат`
- `obj-u3-gender` PROVENANCE `prov-u3-gloss-вода`
- `obj-u3-gender` PROVENANCE `prov-u3-gloss-город`
- `obj-u3-gender` PROVENANCE `prov-u3-gloss-дверь`
- `obj-u3-gender` PROVENANCE `prov-u3-gloss-дом`
- `obj-u3-gender` PROVENANCE `prov-u3-gloss-друг`
- `obj-u3-gender` PROVENANCE `prov-u3-gloss-дядя`
- `obj-u3-gender` PROVENANCE `prov-u3-gloss-женщина`
- `obj-u3-gender` PROVENANCE `prov-u3-gloss-книга`
- `obj-u3-gender` PROVENANCE `prov-u3-gloss-мама`
- `obj-u3-gender` PROVENANCE `prov-u3-gloss-место`
- `obj-u3-gender` PROVENANCE `prov-u3-gloss-молоко`
- `obj-u3-gender` PROVENANCE `prov-u3-gloss-море`
- `obj-u3-gender` PROVENANCE `prov-u3-gloss-мужчина`
- `obj-u3-gender` PROVENANCE `prov-u3-gloss-окно`
- `obj-u3-gender` PROVENANCE `prov-u3-gloss-письмо`
- `obj-u3-gender` PROVENANCE `prov-u3-gloss-работа`
- `obj-u3-gender` PROVENANCE `prov-u3-gloss-семья`
- `obj-u3-gender` PROVENANCE `prov-u3-gloss-слово`
- `obj-u3-gender` PROVENANCE `prov-u3-gloss-стол`
- `obj-u3-gender` PROVENANCE `prov-u3-gloss-учитель`
- `obj-u3-gender` PROVENANCE `prov-u3-gloss-хлеб`
- `obj-u3-gender` PROVENANCE `prov-u3-gloss-чай`
- `obj-u3-gender` PROVENANCE `prov-u3-gloss-школа`
- `obj-u3-gender` PROVENANCE `prov-u3-label-feminine`
- `obj-u3-gender` PROVENANCE `prov-u3-label-masculine`
- `obj-u3-gender` PROVENANCE `prov-u3-label-neuter`
- `obj-u3-gender` PROVENANCE `prov-u3-noun-брат`
- `obj-u3-gender` PROVENANCE `prov-u3-noun-вода`
- `obj-u3-gender` PROVENANCE `prov-u3-noun-город`
- `obj-u3-gender` PROVENANCE `prov-u3-noun-дверь`
- `obj-u3-gender` PROVENANCE `prov-u3-noun-дом`
- `obj-u3-gender` PROVENANCE `prov-u3-noun-друг`
- `obj-u3-gender` PROVENANCE `prov-u3-noun-дядя`
- `obj-u3-gender` PROVENANCE `prov-u3-noun-женщина`
- `obj-u3-gender` PROVENANCE `prov-u3-noun-книга`
- `obj-u3-gender` PROVENANCE `prov-u3-noun-мама`
- `obj-u3-gender` PROVENANCE `prov-u3-noun-место`
- `obj-u3-gender` PROVENANCE `prov-u3-noun-молоко`
- `obj-u3-gender` PROVENANCE `prov-u3-noun-море`
- `obj-u3-gender` PROVENANCE `prov-u3-noun-мужчина`
- `obj-u3-gender` PROVENANCE `prov-u3-noun-окно`
- `obj-u3-gender` PROVENANCE `prov-u3-noun-письмо`
- `obj-u3-gender` PROVENANCE `prov-u3-noun-работа`
- `obj-u3-gender` PROVENANCE `prov-u3-noun-семья`
- `obj-u3-gender` PROVENANCE `prov-u3-noun-слово`
- `obj-u3-gender` PROVENANCE `prov-u3-noun-стол`
- `obj-u3-gender` PROVENANCE `prov-u3-noun-учитель`
- `obj-u3-gender` PROVENANCE `prov-u3-noun-хлеб`
- `obj-u3-gender` PROVENANCE `prov-u3-noun-чай`
- `obj-u3-gender` PROVENANCE `prov-u3-noun-школа`
- `obj-u3-naming` PROVENANCE `prov-u3-phrase-chto-eto`
- `obj-u3-naming` PROVENANCE `prov-u3-phrase-chto-eto-en`
- `obj-u3-phrases` PROVENANCE `prov-u3-phrase-gde-vy`
- `obj-u3-phrases` PROVENANCE `prov-u3-phrase-gde-vy-en`
- `obj-u3-phrases` PROVENANCE `prov-u3-phrase-gloss-да`
- `obj-u3-phrases` PROVENANCE `prov-u3-phrase-gloss-здравствуйте`
- `obj-u3-phrases` PROVENANCE `prov-u3-phrase-gloss-нет`
- `obj-u3-phrases` PROVENANCE `prov-u3-phrase-gloss-пожалуйста`
- `obj-u3-phrases` PROVENANCE `prov-u3-phrase-gloss-спасибо`
- `obj-u3-phrases` PROVENANCE `prov-u3-phrase-gloss-это`
- `obj-u3-phrases` PROVENANCE `prov-u3-phrase-kak-dela`
- `obj-u3-phrases` PROVENANCE `prov-u3-phrase-kak-dela-en`
- `obj-u3-naming` PROVENANCE `prov-u3-phrase-kto-eto`
- `obj-u3-naming` PROVENANCE `prov-u3-phrase-kto-eto-en`
- `obj-u3-naming` PROVENANCE `prov-u3-phrase-moy-stol`
- `obj-u3-naming` PROVENANCE `prov-u3-phrase-moy-stol-en`
- `obj-u3-naming` PROVENANCE `prov-u3-phrase-moya-sobaka`
- `obj-u3-naming` PROVENANCE `prov-u3-phrase-moya-sobaka-en`
- `obj-u3-phrases` PROVENANCE `prov-u3-phrase-да`
- `obj-u3-phrases` PROVENANCE `prov-u3-phrase-здравствуйте`
- `obj-u3-phrases` PROVENANCE `prov-u3-phrase-нет`
- `obj-u3-phrases` PROVENANCE `prov-u3-phrase-пожалуйста`
- `obj-u3-phrases` PROVENANCE `prov-u3-phrase-спасибо`
- `obj-u3-phrases` PROVENANCE `prov-u3-phrase-это`

## Book-directed experiences

- `u3-st-orient`
- `u3-st-book-work`
- `u3-st-book`

## Unavailable experiences

None.


## Unresolved relationships

None. Every referenced Unit 3 identifier resolves.
