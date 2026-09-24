# Unit 3 linguistic and owned-edition mapping packet

This packet prepares review. It records an unsigned structural mapping proposal. It does not record a qualified review, an approved edition alignment, or an install decision.

The owned-edition mapping worksheet separates observed edition facts, the proposed lesson and printed page range, unresolved edition questions, and matters that still require qualified linguistic review. ISBN, owned-copy verification, variance, reviewer identity, qualification, date, and approval stay blank. The disposition remains PENDING_CURRICULUM_REVIEWER.

Unit 3 remains in development. Its readiness is content-complete and review-pending. The Lesson 3 relationship is a proposed, unsigned mapping. It is not an approved edition alignment.

## Package identity

- Schema version: 3
- Application: dev.russiancompanion
- Version: 2.0.0-dev (5)
- Curriculum source commit: b22fa0cca895a677386a77d03a63301f7454aa07
- Unit content-review SHA-256: deadcf10b8af7efca034b8d89921412f309d291180c09c4af490eb132b1bbb86
- Mapping-package SHA-256: 090a3fec638b8a19d34e11d8be88a7796ce18a56a87b7b5b06d626ed777d6448
- Pack content-review SHA-256: dae62d02feae1d2a1e5fe02dd216db6dc549421bf1ecc4cc8c7e3396614c1005
- Review-package SHA-256: ad313aac4c44fed5f091d39b86c442ada3ea3c02997f0451b47226f3498e200f
- Repository source SHA-256: 9189b52de515ad4629245abce45044b5340220b1c74167245d3d59f6436ba839

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 15039bbd27f79fa9ded2a59da96fe116b5f46f614bdd19966fb0f4254663d5af

status=PENDING_QUALIFIED_REVIEW
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
- Content hash: ff482051e4a685d7f61769a6676eeaa3f8395d4e9a2aca525f97f1c209780955

requireInstruction=true
requireBookSelfAttestation=true
requireAppChecks=true
minimumDistinctChecks=12
requireDueReview=false
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
- Content hash: 0fe39146ea3b8356ff33f38b52bfd01be5a3fa6bc8e2c8b93b9c4695d35e7d55

kind=TEACH
title=Two records the page still places in the first group
body=These admitted records are listed as masculine. The frozen page says though it has a 'feminine' ending and conjugates like a feminine noun, 'uncle' is 'inherently' masculine. мужчи́на — man. дя́дя — uncle. 
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
- Content hash: 8a686f314ce7b920515ee508df638b72e2dcd139e5b3bf15be505ed11f503c7c

kind=REMEDIATE
title=Remediation
body=Return to the explanation. Compare the shown form with the admitted record. Do not invent a form that is absent from the record.
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
- Content hash: ebc66d9d5ca16d9aca1578faf51fb948e17e729aa37e572116573a557593d030

title=Self-attest Lesson 3
estimatedMinutes=10
objectives=obj-u3-book
steps=u3-st-book-work,u3-st-book

### STEP `u3-st-book-work`

- Objectives: obj-u3-book
- Sessions: u3-s-book
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 1e0156e68e765c4df7317d5154d0c7d3302478445c6a9efb5f005e0031455fd7

kind=BOOK
title=Work Lesson 3 in your copy
body=Do Lesson 3 in the copy you own. Use that book's explanations and practice. The proposed records in this unit are a separate licensed set and do not copy the book.
dominantAction=Continue
references=ref-book-topics,ref-u35-attribution
items=
examples=
unavailableReason=
bookDirection=Open Lesson 3 in your copy and complete its lesson work.
companionDirection=
remediationStepId=

### STEP `u3-st-book`

- Objectives: obj-u3-book
- Sessions: u3-s-book
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: e9e711e8ec8c20fd5ec3eaa7aff0c55198db67c4bf307faf62a062239c0aa658

kind=BOOK
title=Record Lesson 3 book work
body=When you have done Lesson 3 in your copy, self-attest here. Attestation records your report. It does not grade the lesson, approve a mapping, or approve the proposed records.
dominantAction=Continue
references=
items=
examples=
unavailableReason=
bookDirection=Self-attest only after the Lesson 3 work in your copy.
companionDirection=
remediationStepId=

### EXAMPLE `ex-u3-стол`

- Objectives: obj-u3-gender
- Sessions: u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 3fea098752cde4bc3222323dcafc26c51b57bc29bf11367ed08ae5f8dc3e0abe

kind=LEXICAL
letterId=
display=стол
note=table
objectiveId=obj-u3-gender
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-u3-дом`

- Objectives: obj-u3-gender
- Sessions: u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 0e879695244101c5b3c00023d9feba2c96b2b7000edf486442de0ba9a1dc0565

kind=LEXICAL
letterId=
display=дом
note=house
objectiveId=obj-u3-gender
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-u3-город`

- Objectives: obj-u3-gender
- Sessions: u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: f81aa37f8e4c5b00809fd15c4b27a94dd6a58a6cd9436d425057971340817785

kind=LEXICAL
letterId=
display=го́род
note=town
objectiveId=obj-u3-gender
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-u3-брат`

- Objectives: obj-u3-gender
- Sessions: u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 30dc78be6cb2fdcb0f34b5f4479a75eb4425dd97b347701310b0b1da2806ad65

kind=LEXICAL
letterId=
display=брат
note=brother
objectiveId=obj-u3-gender
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-u3-книга`

- Objectives: obj-u3-gender
- Sessions: u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 0f5e2da5d33e26495977f43d43e1865f6b71096fc0aae34f427edb444d6ea102

kind=LEXICAL
letterId=
display=кни́га
note=book
objectiveId=obj-u3-gender
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-u3-вода`

- Objectives: obj-u3-gender
- Sessions: u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: a7c4e5890f7bf0bd9ecdedae538d431906106d8ba888ace6d2eaa420afc9194a

kind=LEXICAL
letterId=
display=вода́
note=water
objectiveId=obj-u3-gender
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-u3-школа`

- Objectives: obj-u3-gender
- Sessions: u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 2bd753c5b68133abc7b5fb8b49b8411eb233a445578537e7adb766e8d9f026c2

kind=LEXICAL
letterId=
display=шко́ла
note=school
objectiveId=obj-u3-gender
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-u3-работа`

- Objectives: obj-u3-gender
- Sessions: u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 0cd00729d30284d914ec3284e46731c9ec15d0d6f5d0d8c5a614aa9ef84f1ba0

kind=LEXICAL
letterId=
display=рабо́та
note=work
objectiveId=obj-u3-gender
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-u3-окно`

- Objectives: obj-u3-gender
- Sessions: u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 71ad8e09d4eb99fd0d556e0d7fdd1b74bda2108547f657b200c900d60e23d5e6

kind=LEXICAL
letterId=
display=окно́
note=window
objectiveId=obj-u3-gender
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-u3-письмо`

- Objectives: obj-u3-gender
- Sessions: u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: de9ef2e58db387425bd3289718922b8807f7957dc684c97c857783efccbbbcee

kind=LEXICAL
letterId=
display=письмо́
note=letter
objectiveId=obj-u3-gender
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-u3-молоко`

- Objectives: obj-u3-gender
- Sessions: u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 1aeac80d8db0b73c87b3879cb1333670e9661b920913dfc81342b26109bf3d4f

kind=LEXICAL
letterId=
display=молоко́
note=milk
objectiveId=obj-u3-gender
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-u3-море`

- Objectives: obj-u3-gender
- Sessions: u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: f43ac4e0630d5f8ab0bbf266befa8a220f7eaa8d3311974956943a44ed6d5805

kind=LEXICAL
letterId=
display=мо́ре
note=sea
objectiveId=obj-u3-gender
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-u3-мужчина`

- Objectives: obj-u3-gender
- Sessions: u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: c2b91d2fbf34d05d38f099c725733a29cfa89cd614e9fc0d04885ca0bf62ae8c

kind=LEXICAL
letterId=
display=мужчи́на
note=man
objectiveId=obj-u3-gender
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-u3-дядя`

- Objectives: obj-u3-gender
- Sessions: u3-s-gender
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 237c5a539cb0073357aec91c905b08d9454b2e01034350d2bebafd92977d039a

kind=LEXICAL
letterId=
display=дя́дя
note=uncle
objectiveId=obj-u3-gender
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-u3-phrase-здравствуйте`

- Objectives: obj-u3-phrases
- Sessions: u3-s-phrases
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: c4f25fc1a955e0589a16cb78fe23970f32b51d33df71f3d51b94f443b14a08b1

kind=LEXICAL
letterId=
display=здра́вствуйте
note=hello
objectiveId=obj-u3-phrases
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-u3-phrase-спасибо`

- Objectives: obj-u3-phrases
- Sessions: u3-s-phrases
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 72add77f2aafe4284bc3ce148a9f8892f4c29faf0b46e99722ed16842a4480f6

kind=LEXICAL
letterId=
display=спаси́бо
note=thank you
objectiveId=obj-u3-phrases
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-u3-phrase-пожалуйста`

- Objectives: obj-u3-phrases
- Sessions: u3-s-phrases
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 2fbab99f58518b166af2eee32a9ccde7cf7987ae0e5b722169fcf77d9ececcb2

kind=LEXICAL
letterId=
display=пожа́луйста
note=please
objectiveId=obj-u3-phrases
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-u3-phrase-да`

- Objectives: obj-u3-phrases
- Sessions: u3-s-phrases
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 81a00305e457db0a069d631e717aea194fb91eb88f2295986c7a661d81d764cc

kind=LEXICAL
letterId=
display=да
note=yes
objectiveId=obj-u3-phrases
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-u3-phrase-нет`

- Objectives: obj-u3-phrases
- Sessions: u3-s-phrases
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: ee768561b2a5d60b9193da2f9e325e091639e55effe97f8899f29a2e4a8bf45c

kind=LEXICAL
letterId=
display=нет
note=no
objectiveId=obj-u3-phrases
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-u3-phrase-это`

- Objectives: obj-u3-phrases
- Sessions: u3-s-phrases
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: d90b6fc516aeeac203cd04c649b9fab349f2f1f0ef2b1468c010208d743be87b

kind=LEXICAL
letterId=
display=э́то
note=this is
objectiveId=obj-u3-phrases
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-u3-phrase-kak-dela`

- Objectives: obj-u3-phrases
- Sessions: u3-s-phrases
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 9022d919b9bef0faded1f80c8cb30ee28255ad5a5cc0e4cb42506937001aa703

kind=LEXICAL
letterId=
display=Как дела?
note=How do you do?/How're things?
objectiveId=obj-u3-phrases
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-u3-phrase-gde-vy`

- Objectives: obj-u3-phrases
- Sessions: u3-s-phrases
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 5db629cbf59dc8d0f3a8d74d749ea1e3e95d55129bad8661964ce09242ba9b4c

kind=LEXICAL
letterId=
display=Где вы?
note=Where are you?
objectiveId=obj-u3-phrases
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-u3-phrase-moy-stol`

- Objectives: obj-u3-naming
- Sessions: u3-s-naming
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 5da3ec785bd14d403191308dd8363fabd369828b0db43bfa25f84a2738f9d192

kind=LEXICAL
letterId=
display=мой стол
note=my table
objectiveId=obj-u3-naming
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-u3-phrase-moya-sobaka`

- Objectives: obj-u3-naming
- Sessions: u3-s-naming
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 5f5d28e19c688ea8d2146b71287545b370f9d84a26dec92455efed3e68be7e9e

kind=LEXICAL
letterId=
display=моя собака
note=my dog
objectiveId=obj-u3-naming
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-u3-phrase-chto-eto`

- Objectives: obj-u3-naming
- Sessions: u3-s-naming
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: dd8dca9785ed17e99f214232161ef1be791e50bfac978458f9cbdbdeb3cfe591

kind=LEXICAL
letterId=
display=Что это?
note=What is this?
objectiveId=obj-u3-naming
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-u3-phrase-kto-eto`

- Objectives: obj-u3-naming
- Sessions: u3-s-naming
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 69362292ceaaffc7d83ca47f514bcf63b50243a39502a76647a6e67546a0c342

kind=LEXICAL
letterId=
display=Кто это?
note=Who is this?
objectiveId=obj-u3-naming
reviewStatus=PENDING_QUALIFIED_REVIEW

### GUIDED_PRACTICE `u3-i-gender-стол`

- Objectives: obj-u3-gender
- Sessions: u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 08ed9f6ed2af84a14770d7b8765dedde6021a4b66510ded8297fc44b3db04a40

kind=SOURCE_GENDER
language=en
prompt=Which admitted label is recorded for the shown word?
promptLetter=стол
options=masculine | feminine | neuter
answer=masculine
feedback=The admitted record lists this as masculine.
objectiveId=obj-u3-gender
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW

### GUIDED_PRACTICE `u3-i-gender-дом`

- Objectives: obj-u3-gender
- Sessions: u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 00fe75533f918b5bd7b2c584d83ddb7f47f5596f69c291bbf55fc3b27dafa788

kind=SOURCE_GENDER
language=en
prompt=Which admitted label is recorded for the shown word?
promptLetter=дом
options=masculine | feminine | neuter
answer=masculine
feedback=The admitted record lists this as masculine.
objectiveId=obj-u3-gender
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW

### GUIDED_PRACTICE `u3-i-gender-книга`

- Objectives: obj-u3-gender
- Sessions: u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 7376b5e1a9ac33f9fd9360d487cd4c6323569870bdc76b7455c0fcf9a075e23a

kind=SOURCE_GENDER
language=en
prompt=Which admitted label is recorded for the shown word?
promptLetter=кни́га
options=masculine | feminine | neuter
answer=feminine
feedback=The admitted record lists this as feminine.
objectiveId=obj-u3-gender
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW

### GUIDED_PRACTICE `u3-i-gender-окно`

- Objectives: obj-u3-gender
- Sessions: u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: c2276498eb88a6cc73da3e391f090de7d52c66cfb774b8e1c2d50b1dab2c93b2

kind=SOURCE_GENDER
language=en
prompt=Which admitted label is recorded for the shown word?
promptLetter=окно́
options=masculine | feminine | neuter
answer=neuter
feedback=The admitted record lists this as neuter.
objectiveId=obj-u3-gender
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW

### GUIDED_PRACTICE `u3-i-gender-мужчина`

- Objectives: obj-u3-gender
- Sessions: u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 86913e814c6f3dcc63e488dff06fb06f34273ceb3ef1567d3ed08cca1244fedf

kind=SOURCE_GENDER
language=en
prompt=Which admitted label is recorded for the shown word?
promptLetter=мужчи́на
options=masculine | feminine | neuter
answer=masculine
feedback=The admitted record lists this as masculine.
objectiveId=obj-u3-gender
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW

### GUIDED_PRACTICE `u3-i-phrase-здравствуйте`

- Objectives: obj-u3-phrases
- Sessions: u3-s-phrases
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 47fa2383f3983be36808f70ad4fc5a58620fae305a95f29a980da2c0a898c521

kind=SOURCE_MEANING
language=en
prompt=Which admitted gloss is recorded for the shown item?
promptLetter=здра́вствуйте
options=hello | thank you | please | yes | no | this is
answer=hello
feedback=The frozen dictionary row records this gloss: hello.
objectiveId=obj-u3-phrases
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW

### GUIDED_PRACTICE `u3-i-phrase-спасибо`

- Objectives: obj-u3-phrases
- Sessions: u3-s-phrases
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: db3faf7979537ac352c0ca5042c44277f35ca3038ead20524e45f9d6bca73c2d

kind=SOURCE_MEANING
language=en
prompt=Which admitted gloss is recorded for the shown item?
promptLetter=спаси́бо
options=hello | thank you | please | yes | no | this is
answer=thank you
feedback=The frozen dictionary row records this gloss: thank you.
objectiveId=obj-u3-phrases
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW

### GUIDED_PRACTICE `u3-i-name-moy-stol`

- Objectives: obj-u3-naming
- Sessions: u3-s-naming
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 8981beea20b005f47c764beb42c6c5ebc5c7ae68d0c0f1faefc667a8864cbec8

kind=SOURCE_MEANING
language=en
prompt=Which admitted meaning is recorded for the shown item?
promptLetter=мой стол
options=my table | my dog | What is this? | Who is this?
answer=my table
feedback=The frozen page records this meaning: my table.
objectiveId=obj-u3-naming
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW

### INDEPENDENT_CHECK `u3-i-gender-город`

- Objectives: obj-u3-gender
- Sessions: u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 97dc07c8f77b4bbf5449a483854f4a93a403d26e68cc3c7ce20d6e1d63c2358f

kind=SOURCE_GENDER
language=en
prompt=Which admitted label is recorded for the shown word?
promptLetter=го́род
options=masculine | feminine | neuter
answer=masculine
feedback=The admitted record lists this as masculine.
objectiveId=obj-u3-gender
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW

### INDEPENDENT_CHECK `u3-i-gender-друг`

- Objectives: obj-u3-gender
- Sessions: u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: bacf3df9bc9dcacc4b2f6f22bcdd9e4815228db5d5b13a577467a25f1c8effcc

kind=SOURCE_GENDER
language=en
prompt=Which admitted label is recorded for the shown word?
promptLetter=друг
options=masculine | feminine | neuter
answer=masculine
feedback=The admitted record lists this as masculine.
objectiveId=obj-u3-gender
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW

### INDEPENDENT_CHECK `u3-i-gender-дверь`

- Objectives: obj-u3-gender
- Sessions: u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 0bd8022c0a447aedf8b62e5d81f24813cb3765f4ed1fb93e6c369e7f379ea8c7

kind=SOURCE_GENDER
language=en
prompt=Which admitted label is recorded for the shown word?
promptLetter=дверь
options=masculine | feminine | neuter
answer=feminine
feedback=The admitted record lists this as feminine.
objectiveId=obj-u3-gender
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW

### INDEPENDENT_CHECK `u3-i-gender-море`

- Objectives: obj-u3-gender
- Sessions: u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: c9461832ed5d102a3b6987d0f2dec0ccff0797dfa4cddb85fdfecf783df055cd

kind=SOURCE_GENDER
language=en
prompt=Which admitted label is recorded for the shown word?
promptLetter=мо́ре
options=masculine | feminine | neuter
answer=neuter
feedback=The admitted record lists this as neuter.
objectiveId=obj-u3-gender
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW

### INDEPENDENT_CHECK `u3-i-gender-дядя`

- Objectives: obj-u3-gender
- Sessions: u3-s-gender
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 900b51d5ba8a340e1e7a78be89874a26eb1f67faee8f30e6191e82da55f2017d

kind=SOURCE_GENDER
language=en
prompt=Which admitted label is recorded for the shown word?
promptLetter=дя́дя
options=masculine | feminine | neuter
answer=masculine
feedback=The admitted record lists this as masculine.
objectiveId=obj-u3-gender
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW

### INDEPENDENT_CHECK `u3-i-phrase-пожалуйста`

- Objectives: obj-u3-phrases
- Sessions: u3-s-phrases
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 657002ed4e31efeafffe024dc182075c8d3960eb36f56bc6cb04d885f0e2e4ec

kind=SOURCE_MEANING
language=en
prompt=Which admitted gloss is recorded for the shown item?
promptLetter=пожа́луйста
options=hello | thank you | please | yes | no | this is
answer=please
feedback=The frozen dictionary row records this gloss: please.
objectiveId=obj-u3-phrases
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW

### INDEPENDENT_CHECK `u3-i-phrase-да`

- Objectives: obj-u3-phrases
- Sessions: u3-s-phrases
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: d9a46cb9eb02b8550a5a705cb0f2a63996a77965974da5006f919726cfed7f71

kind=SOURCE_MEANING
language=en
prompt=Which admitted gloss is recorded for the shown item?
promptLetter=да
options=hello | thank you | please | yes | no | this is
answer=yes
feedback=The frozen dictionary row records this gloss: yes.
objectiveId=obj-u3-phrases
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW

### INDEPENDENT_CHECK `u3-i-phrase-нет`

- Objectives: obj-u3-phrases
- Sessions: u3-s-phrases
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 9ec48de2970090ad3df836008f2ccf8d1ce380dc210a58f43e357ae8950f90ce

kind=SOURCE_MEANING
language=en
prompt=Which admitted gloss is recorded for the shown item?
promptLetter=нет
options=hello | thank you | please | yes | no | this is
answer=no
feedback=The frozen dictionary row records this gloss: no.
objectiveId=obj-u3-phrases
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW

### INDEPENDENT_CHECK `u3-i-phrase-это`

- Objectives: obj-u3-phrases
- Sessions: u3-s-phrases
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 0d9d782cbe22016c89dc8d490ae111d5c2c323d9ac1917938f4abaa9aee00ea5

kind=SOURCE_MEANING
language=en
prompt=Which admitted gloss is recorded for the shown item?
promptLetter=э́то
options=hello | thank you | please | yes | no | this is
answer=this is
feedback=The frozen dictionary row records this gloss: this is.
objectiveId=obj-u3-phrases
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW

### INDEPENDENT_CHECK `u3-i-name-moya-sobaka`

- Objectives: obj-u3-naming
- Sessions: u3-s-naming
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 6ee8bc89de60b24f709011313d5e9b8bc417c7019e25dfdbd1dc4a15fc9f4756

kind=SOURCE_MEANING
language=en
prompt=Which admitted meaning is recorded for the shown item?
promptLetter=моя собака
options=my table | my dog | What is this? | Who is this?
answer=my dog
feedback=The frozen page records this meaning: my dog.
objectiveId=obj-u3-naming
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW

### INDEPENDENT_CHECK `u3-i-name-chto-eto`

- Objectives: obj-u3-naming
- Sessions: u3-s-naming
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: e6e65b6dd87f84a00c8085f496e790d65ef002afcccff0a12c9b9f6966036b46

kind=SOURCE_MEANING
language=en
prompt=Which admitted meaning is recorded for the shown item?
promptLetter=Что это?
options=my table | my dog | What is this? | Who is this?
answer=What is this?
feedback=The frozen page records this meaning: What is this?.
objectiveId=obj-u3-naming
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW

### INDEPENDENT_CHECK `u3-i-name-kto-eto`

- Objectives: obj-u3-naming
- Sessions: u3-s-naming
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 1889a0519083e106b18e57313c8be059c66f6ea0bcd9e95ccf8327450539f3d1

kind=SOURCE_MEANING
language=en
prompt=Which admitted meaning is recorded for the shown item?
promptLetter=Кто это?
options=my table | my dog | What is this? | Who is this?
answer=Who is this?
feedback=The frozen page records this meaning: Who is this?.
objectiveId=obj-u3-naming
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW

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

### PROVENANCE `prov-u3-dyadya-note`

- Objectives: obj-u3-gender
- Sessions: u3-s-orient, u3-s-gender
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: a454ae36a00245bb34591ee06d408ac58c83405ee89843940d83b46d3f7d3c2a

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: e9663e1aa3be90276011c4ef63b69f761255007d5f76ac71a9e9b2e6a7dd1eb9

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 488276ad9a7853ac36855769a9eed3e81528d46a8e93b8e7ecfef03c8e3350fd

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 1d1a9b70c9b953720e57268e83f3635eec59a7c5d8a26518583fdc637253cd48

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 4a5b226ec2d6350e4fca2ca8c1873f21f19d8c53443ad3052f14ce70640bd9e8

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 59c15341216f997e5372514bd513ab593060dd122bd734717170d9d1af3d37a8

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: e89460e83b6b175dc10858337ef7fe439eb97af651fd85018d1a35e889a528dd

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: d30faca6a1fd564824282a3e520635d6b2f5025649662ee3f8ba66d8575203d1

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 8f00b8cb8a682d56475a3aa6c442a0f650d45101858a09705e2ca8ad4c143ab0

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 5fa3d0ea1ea4ddc6d7429eb226d73ee938d17f284c4bf6b39cef193c7cd236e2

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 1bdcb7959cb1cd50e457d1acfdf3497c81eafe0140b26bbbe783250af7055ac8

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 174a3a32fe91e47b9833060faf0b26e5484de1729c2700f385ad027f93effb2c

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: f9c646ef212a825ad5907b3ca0b214539b9583cc6f67423c755b433648ad5524

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 20bd175cf0ecf15efbb0d24bd408a0037c076fcc026b11095b4821751e76f599

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 0dd1f8a9970f8157ca368884c369675d8cbf9664fb77f816c39de1ff55a57539

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: f7cee5aead2fc5583c0ae26558c55b92857f6e4378c4b16ed7f213437ce7e4b3

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 13ca0dd05d7133920d8497cef6fb220e45c9eaa74efc624eb5ed6c9bb4e31e04

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 47ce5232bd67fb13578cd1b7a9cd9e7135ebe4971f73ee08d61940624836b4fe

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 667f0c355f5e1d38b1ecf85caf0dd54c985e8614ea71732a42ab11a1b570429b

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 59652df04a737d2dc8c0427a410917ea83cc11ea52d0248078b9cd09748c6eb4

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: f9f62074b50f13e8b185572f43092035de5497d2f442eade1cacd61c19d28765

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 39d87cc3edbf4d4235c46e1662fdcb9c8fdad32fc2f0d1db2d4fb673f2e15b96

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 235d0483d8b6c8af7a963d9da856db855a307f2a1ad09b98b2dae39b76814838

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 8a773f998f4b0249848168834563a6a9a5355fe20b592dd6444a198797f9fb34

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 1095891f77649e9ca19f6b916376a33218eef450c0cb3dbc0e896b32d908c01d

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 9c2ca27b5b85b236fb640c59fb22ae5c444a02ad356e90c89900d67375f04f45

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 2b57c3148b2442015e3bdca96d3bbbcd07e63f298ead14347a00cd6fdc2f3f07

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: c9bb91ad87bc34c037d2425630d70f131419a09462542dc3d613d5c0cfa55730

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 3cb62c00ee2efaaced56b600d21ff33e587feb8258b95f3d5e9231ce06dd7970

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: a5677c5c8e1e767ad88e3c2f0f0e6057ed074a43f0bbfdb1c25bca00565baa8d

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 2418ca784349d12c21fbe70564eb3b854e9fbb626be23547ab0aadfe4af2c0d5

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 1a6dd499596562884762449cc84946b85734c9feecc31e3a7864664decbc88c9

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 8807e56cc1948bd779aba20adacb1d44a8380ecfa7147765a8b68bf090662087

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 108f455aa2a0e547f07903de9c88747776cec089d9f65bac83d0508e8a7eb75c

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: e33dd963b05d9f874abe7d602694790b0387eb4cdea8e29a2329dc48c3d8cc7a

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: bf22913ee9350228dd39fb0ce82d627852eb15f51ee7c999410898d0fcc67c7c

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 56d0ae81761e51a8893564e824c24b9249688c28cc4b1227108cc60c2b727293

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: e36b32137fedba3eefd8a39eb28332347264e966c8d4c65556fdde0b59cdcb9e

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: fe52c7989008d82ba65bf699759e9c6698dbfb939f1176139b127cabcc899092

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: b43c68d02da1914c7f114ee867657ee599d94b40637a3be00bfa533612eba6b1

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 1d442a5a084cf6799b165d00fd1b0cfc4e5ebdd9f2047c12f1f776b57c044bae

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 2e1a1d9841c8b3e126af41d96144ae8234d58e4bddce1652a1983393f42ad107

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: f619961caa369fe7f8ed576383b20dcf2f972c5acd1ad8de03716aafd2586313

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: cd28ac52c69acf1ee8688a40597cd85ea9eb52897eac4d8aa1a11fbb038a0e97

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 173b584c5775c8c383bb105b23d330ff5d9b65cbcf9645fcaa798bde32a4ca80

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: ff21509860243d596451d37015480342a911f04c96dbf62db73c79d463cdfc25

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 58ab48fddc4ba4aa73c1a15a2d274d868f926a3e4d0c9455e0a467ece88a6fb6

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: f50a171a9d90db1bd404303d68301176095f661cc458d8379530b02c76a38211

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 8d3267c72b894cc2cbe3a3137792d86d14f11bbf4f1821b8eff1110b2bf0452f

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: c60998bdf363e48bce2e52842791a16c0adde09f91343925cd0d6c57f75011d9

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 86cc4ee315bf4ef48f9194f88660fe93a85449787c3ff97734d7148e12808b59

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: e4124efdcbc027173b4d5c3d05631361a415c77cffba00ac90aeb751e7ce37eb

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 33f1810caea5ce6ed4aae21bd3f1d1d14c93fb3dcfdfc38947939419f59bcf59

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 6eff70c878d00ad7e7ba77a1edcc4cef39b0575806fef1c1cd7c30c8faa66926

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: b852d6567d963fc7112aeb55c06909e71d5899d05a8937f9dda5df9fffcd9a71

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 99c4cadaf6b2772cc208b661cef4ff6fb7c550e0a8667019f2f5476d7e4b8c8a

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 5df106dcefd0fe3fec78436e73ca6829710b188e5fb3edd2e038d6cd7ac476a6

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: d50a1722ac923c54f87bc8b2c8c9c90177b8c125ab82ee9d762bad1e636f0290

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 766fc3e0b26084630d03c9f04886e50520a80d402e527b85b776228fdaecc952

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: bbca4fadb8c43927557444ef8c653faf76b29b0327181bb9ef2363ca8b867e25

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: fd5015943526ad38d9962509fa93453075247a07711f80127a38cd1cd19f642b

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: aa59e44d153e33675cf0ace08e15efd73809119b45fb5af547be79b7df841327

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: e4b058409adb6002c52cce7524270394f1679577688ff474bd098623f2256a7c

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: c80a996a3e681de5731247f91da49a047aa58a31fc2f3b9be04b48a9e440ff01

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: d7a907f824448480de20c0ed33b9a46423de34c2e7275a3f0d1b9c5831775a7e

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 527bceeba0790b235b471634d4bdfcd93be50a4621dcdacec39e00d690dec244

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: c5fe9a537ad88ac8e851472d30ee9866bf80054c67be6f3458345dae961c5f9b

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: ba86706129cb34725f7dd763c8e5899a287496f849f3c9bab7a9cbeca97e512d

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 11968cf88e38b5f08afc7eefec4446796163c52d4f7a63b1c0976fc249a1f145

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 4abd14ff9522e9878a7d05fb5cd0e75e11c2a662598cacbf207747e61711a69b

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: e476358ee495642a068b6b1ec34c9e4e8d2c46bf6ebe87523e0fb0d6d294708d

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 622ac6390645303d17d93e19358c7a7f00b8b0f5acabd24da03947211e027215

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 194a075d3a65b59a7995d5f10f3c46321939e5d118768d9497323b6416dd1c13

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 035460ad18682670e35123994cd7e3ab3db4f08becf06614f301f352bd2f6497

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: d862210155ea1ff881cdcc95644985b9d951fc10cadc9f150535a3546f38e790

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: fc55964025e2c4c4e4b32f97e8c114a30e81cf9a5add2d3eac84171096cd1f93

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
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 5cea3695757e6357f98244109c80605ff9fa957852d09420c9261d6bce84089c

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

### AUTHORED_ENGLISH `en-u3-001`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: e23a2a04c1ac558cc5f66e05991ca3998d73e8ec913f8a281a4aa1b5fc0b98ac

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Which admitted label is recorded for the shown word?

### AUTHORED_ENGLISH `en-u3-002`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: c1be804c7b63ab000bd31f1250a29f1cf10c15a5359f2d71c2c01973f9ec4d26

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=The admitted record lists this as 

### AUTHORED_ENGLISH `en-u3-003`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: d35ee91896b578709e498e011b140cefeed1acfcfa39475350e5baaa3a835819

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=.

### AUTHORED_ENGLISH `en-u3-004`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: dbbf39a8881fd829fbdedf4eaae880fc5dca4af6a83b36ca4aadc18f05312a45

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Which admitted gloss is recorded for the shown item?

### AUTHORED_ENGLISH `en-u3-005`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 9216bac785cf630fbc6d83d9337b6543a2c1694a9e60497229608efcff0ac148

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=The frozen dictionary row records this gloss: 

### AUTHORED_ENGLISH `en-u3-006`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 189d878c07557092b7b90a0782cc2a63e926d84e04da37af9e5707158621a9ee

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Which admitted meaning is recorded for the shown item?

### AUTHORED_ENGLISH `en-u3-007`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: d194585ff5f176d79d13eeb29a2f5fa7a2a1e0aa82312937108e451403ac6117

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=The frozen page records this meaning: 

### AUTHORED_ENGLISH `en-u3-008`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 07164cb1c8d4917260c53a8ab30e4b753a737826bf739657c612394d8abae9d4

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Read the admitted grouping sentence

### AUTHORED_ENGLISH `en-u3-009`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: d037d6b0a0920ace25191d49b51cc2413b409e0d689a9afc8cb85ed4729e2da6

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=The frozen page says: 

### AUTHORED_ENGLISH `en-u3-010`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: ebea05b8756cacf80147439ea8a7d931018568c4f326c977e59a54a6898d374f

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text= An ending is not a substitute for the admitted record.

### AUTHORED_ENGLISH `en-u3-011`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: ebd7e78270dda10a111c3e10027fd1c1d2b0e817bf5449d865c298d3f430a3b6

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=These admitted records are listed as 

### AUTHORED_ENGLISH `en-u3-012`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 0f3ea31a4d408695d76d4237ed81e78e094deae7a1e592aa6ed3d5f9270f847a

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=. 

### AUTHORED_ENGLISH `en-u3-013`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 66a0efc49434ec74219a14efa1d7660f72c9c0222d6111ab17159317ddf78aa1

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Four records in the first group

### AUTHORED_ENGLISH `en-u3-014`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 451b54b9eb678c303da6ed6b243b4358835da5b0bfabb6abcae43c149842a7e0

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Four records in the second group

### AUTHORED_ENGLISH `en-u3-015`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: d2120d6097a6de11593dd1d1d462eb58d760d3c982a9d2126403459f513516a3

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Four records in the third group

### AUTHORED_ENGLISH `en-u3-016`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 1208bf1cd11318ecfb40d086b2d3facf84c974b853efdc8acf5dfa8da87f1a40

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=The frozen page says 

### AUTHORED_ENGLISH `en-u3-017`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 8dd2fc6eaa110320eed6501682181d2b7c0c30d14f106d8d504ad9e3001a7afa

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Two records the page still places in the first group

### AUTHORED_ENGLISH `en-u3-018`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 342bcf3d8b6fbb66e93111a94ccc2301cdaf3cc30b0255f6703ad2f5446d2132

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=These greetings are copied from admitted rows. 

### AUTHORED_ENGLISH `en-u3-019`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 236dfe0b75e075842d739d993e8b59567e47980f429a6a5d0413024099894117

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Three short greetings

### AUTHORED_ENGLISH `en-u3-020`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 5ee8601d917605db47ca2104a732da1f449b61afe64d4713e828703ce25c2cbe

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=These short answers are copied from admitted rows. 

### AUTHORED_ENGLISH `en-u3-021`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 1905ab4531fe94158d7cdd97679b1eff1d436dcefd56735f7a7dae2896377b9c

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Three short answers

### AUTHORED_ENGLISH `en-u3-022`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 289be4df87601f4cb5aed1c4abdfe2dc4ff1bca25bbd9e786bd02a9b00b9a4fb

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=These written questions are exact frozen excerpts. 

### AUTHORED_ENGLISH `en-u3-023`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: d9695889c94b2c35850188055b70e6d7fd57ff492faa5ee9e20d8b2546d2254e

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Two written questions from the frozen page

### AUTHORED_ENGLISH `en-u3-024`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 5d47d1d04ca075391af4a25661de16ed7005db2244411a312bc1e10ecfc08ef6

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=These two items are exact frozen excerpts. Do not substitute another word into them. 

### AUTHORED_ENGLISH `en-u3-025`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 7fc24a2764f664b3e5bb56fcb20ad81c915449654f1d0abb5674dc360b5a47bd

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Two naming items from the frozen page

### AUTHORED_ENGLISH `en-u3-026`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 4852566f424451e914f3537de12162fc5f255e4903b4499ba1d33e66617115ec

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=These two questions are exact frozen excerpts. 

### AUTHORED_ENGLISH `en-u3-027`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: bfc0c2f70e92aba9a71d361b99e20ed4281015e9c480d61547fd8eb993fc488c

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Two naming questions from the frozen page

### AUTHORED_ENGLISH `en-u3-028`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 7cbe9f949e1a51e15c541ba0b00e1963d4bfc2f9019e402c510aa9866e4ab3f9

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Everyday phrases, naming, and grouping

### AUTHORED_ENGLISH `en-u3-029`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 00c4eef2c3aa03b04b45c644b7b83c088e3c121aae820e2397b6c19cb5696901

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Proposed Unit 3 uses frozen dictionary rows and grammar excerpts for the groups, a small phrase set, and naming items. The owned book remains separate work. Qualified review is pending.

### AUTHORED_ENGLISH `en-u3-030`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 17e483e0cf5a10a513d867a25570f01d6ce35bd98dcb11295c50c56835d7882c

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Open Lesson 3 in the copy you own. The app records do not replace that lesson.

### AUTHORED_ENGLISH `en-u3-031`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: ca67a6a0139ef8b1c175521b46df74ddeee12bf05be86d4b26c9451018ce7e50

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Guided practice

### AUTHORED_ENGLISH `en-u3-032`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 9ff3b813a5c13bf016f4cadbdb44183fd9b85c99e00252857a53576934834ca5

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Choose the admitted record. The feedback states the frozen field.

### AUTHORED_ENGLISH `en-u3-033`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 3974057599f2de2472fa4362ca487e0cfa8f02ff6b8b1f16c4aadc91c8bde9bb

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Remediation

### AUTHORED_ENGLISH `en-u3-034`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 2402a3c3da85e952451dd650a172db7831d9a45d125d47992a1b08aca9ccee0c

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Return to the explanation. Compare the shown form with the admitted record. Do not invent a form that is absent from the record.

### AUTHORED_ENGLISH `en-u3-035`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 672867bc946813488c93343f374d414f9c38e14842845a1f19a34c77e6b873ec

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Independent checks

### AUTHORED_ENGLISH `en-u3-036`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 433da8d04d401a30eb56c81b34b71bd2491906f4a8bb0939986af16b3835c840

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=These checks use only admitted records. A miss returns to the explanation.

### AUTHORED_ENGLISH `en-u3-037`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 10a42f96db93ead990085e590dc2e608b9f28c734a8092c7535e45ac240ca05f

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Work Lesson 3 in your copy

### AUTHORED_ENGLISH `en-u3-038`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: a5bdf7ff4626154921c91b9ced5f001e3f6ba3ade6a608711ba313be5d4367a6

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Do Lesson 3 in the copy you own. Use that book's explanations and practice. The proposed records in this unit are a separate licensed set and do not copy the book.

### AUTHORED_ENGLISH `en-u3-039`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: ac05a27abd087bbe883a7aedde1309dc4fb966ceb1580f32322d55e496f434de

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Open Lesson 3 in your copy and complete its lesson work.

### AUTHORED_ENGLISH `en-u3-040`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 0fc15518191fc391e9120b06e21044d4025ac0072622ff804dddb472be746e74

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Record Lesson 3 book work

### AUTHORED_ENGLISH `en-u3-041`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 3463a4dbebe5edf0d38763c4691223ffdb9ae0f9a4314966206489908d31e569

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=When you have done Lesson 3 in your copy, self-attest here. Attestation records your report. It does not grade the lesson, approve a mapping, or approve the proposed records.

### AUTHORED_ENGLISH `en-u3-042`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 51fdb8a181fa00572734315d7d3434a0b7c6f91b9ab629b95975574220fb4f6c

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
