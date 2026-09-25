# Unit 5 linguistic and owned-edition mapping packet

This packet prepares review. It records an unsigned structural mapping proposal. It does not record a qualified review, an approved edition alignment, or an install decision.

The owned-edition mapping worksheet separates observed edition facts, the proposed lesson and printed page range, unresolved edition questions, and matters that still require qualified linguistic review. ISBN, owned-copy verification, variance, reviewer identity, qualification, date, and approval stay blank. The disposition remains PENDING_CURRICULUM_REVIEWER.

Unit 5 remains in development. Its readiness is content-complete and review-pending. The Lesson 5 relationship is a proposed, unsigned mapping. It is not an approved edition alignment.

## Package identity

- Schema version: 3
- Application: dev.russiancompanion
- Version: 2.0.0-dev (5)
- Curriculum source commit: b22fa0cca895a677386a77d03a63301f7454aa07
- Unit content-review SHA-256: 9470cc61d50236425fb01771a4d20788c88d6c98f8a8694420609f86d7b9710d
- Mapping-package SHA-256: a1affd13da11e1173c23a2cab33b22f7ad6ea99aa54c99d54fc945fd6d7d0806
- Pack content-review SHA-256: c1ed0048e4e62070939fcf5ea0dd02a3b90a4dbfcaf56403d8352f97b4231b92
- Review-package SHA-256: 4f749c803872aa56fd72c304a6900d40cc6039f23cb46f83d6f270139a43fdd2
- Repository source SHA-256: 78268a4ad0e2bd82accd154c30e7a3ca4252d484b6cb9afc79f3ebf22bd2b2cd

`unitContentReviewDigest` binds this unit's instructional curriculum. It covers this unit and the reachable instructional records, relationships, shared dependencies, sources, rights, assets, and advancement rule. It does not cover any other unit. Recording a review status, reviewer signature, mapping approval, install state, or readiness does not change it.

`mappingPackageDigest` binds the proposed edition-mapping facts for Unit 5: edition identity, lesson, printed page range, topic description, observation source, ISBN observation state, and the pending disposition. Reviewer identity, qualification, date, notes, correction notes, and signature stay outside that digest.

`packContentReviewDigest` is the repository aggregate of instructional curriculum. It is not an approval of this unit, and it is not part of the review-package digest.

`reviewPackageDigest` identifies this complete pending review package. It binds the ordered instructional records and the mapping-package digest. Changing a proposed page range or topic changes the mapping-package digest and therefore this package digest.

`repositorySourceDigest` binds repository and release inputs. Recording an approval later changes that repository hash. That change does not replace the signed content-review digest or the mapping-package digest.

The marker scan used by this package detects only a fixed list of known prohibited mapping-input markers. It is not comprehensive copyright detection and cannot recognize every copy or close paraphrase of protected material.

## Unit

number=5
title=Questions, prepositional case, and indeclinable nouns
topicSummary=Proposed Unit 5 is a development unit awaiting qualified review. It contains admitted question, prepositional, and indeclinable records, plus separate book self-attestation. Question intonation is not taught. It is not installed and the owned-edition mapping is not signed.
installState=DEVELOPMENT
readiness=CONTENT_COMPLETE_REVIEW_PENDING
mappingStatus=PENDING_CURRICULUM_REVIEWER
mappingNote=Proposed Lesson 5 mapping remains pending. The structural edition facts are unchanged. This proposed curriculum is not an approved alignment.
penguinLessonField=5
prerequisites=4
estimatedMinutes=54

## Not yet approved

No reviewer has signed this packet. Reviewer identity, qualification, review date, disposition, and reviewed digest are blank. Do not treat this development unit as reviewed, installable, or installed.

## Records

### UNIT_REVIEW `rev-pending-unit-5`

- Objectives: none
- Sessions: none
- Source: none
- Rights: none
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: d540b5cb282e86909a05ab20d900014d8852e241c7f181cfc423a79d1740cbc9

status=PENDING_QUALIFIED_REVIEW
reviewer=
reviewerQualification=
packVersion=
reviewedContentDigest=
curriculumMappingId=
notes=No qualified reviewer has signed Unit 5.

### UNIT `unit-5`

- Objectives: obj-u5-questions, obj-u5-prepositional, obj-u5-indeclinable, obj-u5-book
- Sessions: u5-s-orient, u5-s-questions, u5-s-prepositional, u5-s-indeclinable, u5-s-book
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 9a4960eff2ebe4544b8895963f7f37e4153fcc6ae3d35dd2ddb15850a51eb6eb

number=5
title=Questions, prepositional case, and indeclinable nouns
topicSummary=Proposed Unit 5 is a development unit awaiting qualified review. It contains admitted question, prepositional, and indeclinable records, plus separate book self-attestation. Question intonation is not taught. It is not installed and the owned-edition mapping is not signed.
installState=DEVELOPMENT
readiness=CONTENT_COMPLETE_REVIEW_PENDING
mappingStatus=PENDING_CURRICULUM_REVIEWER
mappingNote=Proposed Lesson 5 mapping remains pending. The structural edition facts are unchanged. This proposed curriculum is not an approved alignment.
penguinLessonField=5
prerequisites=4
estimatedMinutes=54

### ADVANCEMENT `unit-5-advancement`

- Objectives: none
- Sessions: none
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 213c5f5037d7f6a5b3d3a0bf008140467aca3055a2bd2f983b5a5c5997564cdf

requireInstruction=true
requireBookSelfAttestation=true
requireAppChecks=true
minimumDistinctChecks=14
requireDueReview=false
tutorOptional=true

### OBJECTIVE `obj-u5-questions`

- Objectives: obj-u5-questions
- Sessions: none
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 94b72589a1b7157fc7302917893d3855b2d88fe7fa49b63ddaf84325b8e136f3

title=Recognize admitted question words and written questions
kind=RECOGNITION
unitNumber=5
instruction=u5-st-orient,u5-st-teach-questions-1,u5-st-teach-questions-2,u5-st-teach-questions-3,u5-st-teach-questions-4,u5-st-teach-questions-5
examples=ex-u5-q-kto,ex-u5-q-chto,ex-u5-q-kogda,ex-u5-q-pochemu,ex-u5-q-kak,ex-u5-q-gde,ex-u5-q-kuda,ex-u5-q-otkuda,ex-u5-q-skolko,ex-u5-q-kakoy,ex-u5-pattern-pattern-chto-eto,ex-u5-pattern-pattern-kto-eto,ex-u5-pattern-pattern-kak-dela,ex-u5-pattern-pattern-gde-zhivete
practice=u5-st-guided-questions-1,u5-st-guided-questions-4
checks=u5-i-q-kogda,u5-i-q-pochemu,u5-i-q-kak,u5-i-q-gde,u5-i-pattern-pattern-kto-eto,u5-i-pattern-pattern-kak-dela,u5-i-pattern-pattern-gde-zhivete
remediation=u5-st-rem-questions

### OBJECTIVE `obj-u5-prepositional`

- Objectives: obj-u5-prepositional
- Sessions: none
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: f63bdcf1ce13856c59af6c6adf66ac8b46c540e218ef350fea48c0474a771a94

title=Recognize admitted prepositional examples
kind=RECOGNITION
unitNumber=5
instruction=u5-st-orient,u5-st-teach-prepositional-1,u5-st-teach-prepositional-2,u5-st-teach-prepositional-3,u5-st-teach-prepositional-4,u5-st-teach-prepositional-5,u5-st-teach-prepositional-6
examples=ex-u5-prep-o-knige,ex-u5-prep-o-pisme,ex-u5-prep-o-sade,ex-u5-prep-v-moskve,ex-u5-prep-na-kryshe,ex-u5-prep-v-teatre,ex-u5-prep-na-stole,ex-u5-prep-na-stene
practice=u5-st-guided-prepositional-1,u5-st-guided-prepositional-6
checks=u5-i-prep-o-sade,u5-i-prep-v-moskve
remediation=u5-st-rem-prepositional

### OBJECTIVE `obj-u5-indeclinable`

- Objectives: obj-u5-indeclinable
- Sessions: none
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: ab5d57a2a81dd881c02f24ca0580281305dbe3f6d68ccee77f225c2ca372a695

title=Recognize admitted indeclinable nouns
kind=RECOGNITION
unitNumber=5
instruction=u5-st-orient,u5-st-teach-indeclinable-1,u5-st-teach-indeclinable-2,u5-st-teach-indeclinable-3
examples=ex-u5-indecl-радио,ex-u5-indecl-такси,ex-u5-indecl-метро,ex-u5-indecl-кино,ex-u5-indecl-пальто,ex-u5-indecl-кофе,ex-u5-indecl-кафе,ex-u5-indecl-меню
practice=u5-st-guided-indeclinable-1
checks=u5-i-indecl-такси,u5-i-indecl-метро,u5-i-indecl-кино,u5-i-decl-книга,u5-i-decl-окно
remediation=u5-st-rem-indeclinable

### OBJECTIVE `obj-u5-book`

- Objectives: obj-u5-book
- Sessions: none
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: b2eadb7dc868c18986f67acd99aef2714ce93f93e771a90a70771d7bd031c64a

title=Self-attest Lesson 5 book work
kind=SELF_ATTESTATION
unitNumber=5
instruction=u5-st-book-work,u5-st-book
examples=
practice=
checks=
remediation=

### SESSION `u5-s-orient`

- Objectives: obj-u5-questions, obj-u5-prepositional, obj-u5-indeclinable, obj-u5-book
- Sessions: u5-s-orient
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 473bc7a13ec6b51bce545b3464010cfa8b22fc9e806b62e3c53448b47753b3e9

title=Questions, place examples, and words that do not change form
estimatedMinutes=8
objectives=obj-u5-questions,obj-u5-prepositional,obj-u5-indeclinable,obj-u5-book
steps=u5-st-orient

### STEP `u5-st-orient`

- Objectives: obj-u5-questions, obj-u5-prepositional, obj-u5-indeclinable, obj-u5-book
- Sessions: u5-s-orient
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: d077490d402b1d5ef1b5727ca6028ea8b7ee0fa65c6fc96790fadc0479d6c206

kind=ORIENT
title=Questions, place examples, and words that do not change form
body=Proposed Unit 5 uses frozen grammar excerpts and dictionary rows for questions, place examples, and words that do not change form. It does not teach question intonation and it does not supply audio. The owned book remains separate work. Qualified review is pending.
dominantAction=Continue
references=ref-u35-attribution
items=
examples=
unavailableReason=
bookDirection=Open Lesson 5 in the copy you own. The app records do not replace that lesson.
companionDirection=
remediationStepId=

### SESSION `u5-s-questions`

- Objectives: obj-u5-questions
- Sessions: u5-s-questions
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: f82fa0e502e5b71b598741c832bccbd743a4ada68a48bcd7f80ee807a18f6645

title=Recognize admitted question words and written questions
estimatedMinutes=12
objectives=obj-u5-questions
steps=u5-st-teach-questions-1,u5-st-guided-questions-1,u5-st-teach-questions-2,u5-st-teach-questions-3,u5-st-teach-questions-4,u5-st-guided-questions-4,u5-st-teach-questions-5,u5-st-rem-questions,u5-st-check-questions

### STEP `u5-st-teach-questions-1`

- Objectives: obj-u5-questions
- Sessions: u5-s-questions
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 098e7d749959245aac39bcd5d32c295139b630c2a3449939453939987483ead1

kind=TEACH
title=Four question words
body=These words are copied from the frozen table. Кто — Who. Что — What. Когда — When. Почему — Why. 
dominantAction=Continue
references=ref-u35-attribution
items=
examples=ex-u5-q-kto,ex-u5-q-chto,ex-u5-q-kogda,ex-u5-q-pochemu
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u5-st-guided-questions-1`

- Objectives: obj-u5-questions
- Sessions: u5-s-questions
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: a319c1f2afad2e038043deb754bcf77c397d272dc6614b7d2e67353b2b008465

kind=GUIDED
title=Guided practice
body=Choose the admitted record. The feedback states the frozen field.
dominantAction=Continue
references=
items=u5-i-q-kto,u5-i-q-chto
examples=
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=u5-st-rem-questions

### STEP `u5-st-teach-questions-2`

- Objectives: obj-u5-questions
- Sessions: u5-s-questions
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 434b73d6d92223890461d78f4f79d5b3bc29771aea691cfc0f01ea0bac48530d

kind=TEACH
title=Three more question words
body=These words are copied from the frozen table. Как — How. Где — Where (location). Куда — Whither, where to. 
dominantAction=Continue
references=ref-u35-attribution
items=
examples=ex-u5-q-kak,ex-u5-q-gde,ex-u5-q-kuda
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u5-st-teach-questions-3`

- Objectives: obj-u5-questions
- Sessions: u5-s-questions
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 3bc9e51cc60de22d6040feee7d2c28e23468c311c5bd2a94cc8fe411f33b5201

kind=TEACH
title=Three further question words
body=These words are copied from the frozen table. Откуда — Whence, where from. Сколько — How much, how many. Какой — What kind. 
dominantAction=Continue
references=ref-u35-attribution
items=
examples=ex-u5-q-otkuda,ex-u5-q-skolko,ex-u5-q-kakoy
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u5-st-teach-questions-4`

- Objectives: obj-u5-questions
- Sessions: u5-s-questions
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 261b5d4a486bbc5052cdfcdb4d128092567c614b2fc944c60ec00a45078eef9f

kind=TEACH
title=Two written questions
body=These written questions are exact frozen excerpts. Что это? — What is this?. Кто это? — Who is this?. 
dominantAction=Continue
references=ref-u35-attribution
items=
examples=ex-u5-pattern-pattern-chto-eto,ex-u5-pattern-pattern-kto-eto
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u5-st-guided-questions-4`

- Objectives: obj-u5-questions
- Sessions: u5-s-questions
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 37250cebc7ae64f88e06f0d592221992201050b5128784f33c3dd858730565c6

kind=GUIDED
title=Guided practice
body=Choose the admitted record. The feedback states the frozen field.
dominantAction=Continue
references=
items=u5-i-pattern-pattern-chto-eto
examples=
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=u5-st-rem-questions

### STEP `u5-st-teach-questions-5`

- Objectives: obj-u5-questions
- Sessions: u5-s-questions
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 90c3e22fa6792ac0d10baae84fe25425a2e538aa83da8ad9b30b55c2447c59ba

kind=TEACH
title=Two more written questions
body=These written questions are exact frozen excerpts. Как дела? — How do you do?/How're things?. Где вы живёте? — Where do you live?. 
dominantAction=Continue
references=ref-u35-attribution
items=
examples=ex-u5-pattern-pattern-kak-dela,ex-u5-pattern-pattern-gde-zhivete
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u5-st-rem-questions`

- Objectives: obj-u5-questions
- Sessions: u5-s-questions
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: ea06f8705582756b25e4f56375a5a9b06ecb3e5faff26d9c40095d6efc1ef014

kind=REMEDIATE
title=Remediation
body=Return to the explanation. Compare the shown form with the admitted record. Do not invent a form that is absent from the record.
dominantAction=Continue
references=
items=
examples=ex-u5-q-kto,ex-u5-q-chto
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u5-st-check-questions`

- Objectives: obj-u5-questions
- Sessions: u5-s-questions
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 90480d20b84fcae308391885e3d9fd6d5abcf341019dc1535cf7cb7a212bdcca

kind=CHECK
title=Independent checks
body=These checks use only admitted records. A miss returns to the explanation.
dominantAction=Continue
references=
items=u5-i-q-kogda,u5-i-q-pochemu,u5-i-q-kak,u5-i-q-gde,u5-i-pattern-pattern-kto-eto,u5-i-pattern-pattern-kak-dela,u5-i-pattern-pattern-gde-zhivete
examples=
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=u5-st-rem-questions

### SESSION `u5-s-prepositional`

- Objectives: obj-u5-prepositional
- Sessions: u5-s-prepositional
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 1e2d4149ab181f5fd34d1f711d2803e3ee3fc84a0e6e3dbf911894cb25004c0e

title=Recognize admitted prepositional examples
estimatedMinutes=12
objectives=obj-u5-prepositional
steps=u5-st-teach-prepositional-1,u5-st-guided-prepositional-1,u5-st-teach-prepositional-2,u5-st-teach-prepositional-3,u5-st-teach-prepositional-4,u5-st-teach-prepositional-5,u5-st-teach-prepositional-6,u5-st-guided-prepositional-6,u5-st-rem-prepositional,u5-st-check-prepositional

### STEP `u5-st-teach-prepositional-1`

- Objectives: obj-u5-prepositional
- Sessions: u5-s-prepositional
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 75e6a159d31cae4aaa80f9ed1a3eb28cce94757255ec4dc7ae7a0b63b0e1340e

kind=TEACH
title=Two about-examples
body=These examples are exact frozen excerpts. О книге — About the book. О письме — About the letter. 
dominantAction=Continue
references=ref-u35-attribution
items=
examples=ex-u5-prep-o-knige,ex-u5-prep-o-pisme
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u5-st-guided-prepositional-1`

- Objectives: obj-u5-prepositional
- Sessions: u5-s-prepositional
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 06df452532240152d87d4f9cefbae761bf6eb1cb6442cde649f888cea99f51c3

kind=GUIDED
title=Guided practice
body=Choose the admitted record. The feedback states the frozen field.
dominantAction=Continue
references=
items=u5-i-prep-o-knige,u5-i-prep-o-pisme
examples=
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=u5-st-rem-prepositional

### STEP `u5-st-teach-prepositional-2`

- Objectives: obj-u5-prepositional
- Sessions: u5-s-prepositional
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 58a6d8ac4c542ce4c90121d2a50fb901ac575823e705e80371405e6df5ab20f5

kind=TEACH
title=Two more about-examples and a place example
body=These examples are exact frozen excerpts. О саде — About the garden. я в Москве — I'm in Moscow. 
dominantAction=Continue
references=ref-u35-attribution
items=
examples=ex-u5-prep-o-sade,ex-u5-prep-v-moskve
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u5-st-teach-prepositional-3`

- Objectives: obj-u5-prepositional
- Sessions: u5-s-prepositional
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 19e39c10f89fb4dca894e995615c3b40f2b1f49816b73e5677b61b5373d80d00

kind=TEACH
title=Two place examples
body=These examples are exact frozen excerpts. Он на крыше — He's on the roof. Он в театре — He is at the theatre. 
dominantAction=Continue
references=ref-u35-attribution
items=
examples=ex-u5-prep-na-kryshe,ex-u5-prep-v-teatre
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u5-st-teach-prepositional-4`

- Objectives: obj-u5-prepositional
- Sessions: u5-s-prepositional
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: d6c6dc1ce1f5cc0c63b523d9c974031b044009714282d9931c48eb3deee3c8ec

kind=TEACH
title=Two more place examples
body=These examples are exact frozen excerpts. Ваза стоит на столе. — The vase stands on the table. Картина висит на стене. — The painting hangs on the wall. 
dominantAction=Continue
references=ref-u35-attribution
items=
examples=ex-u5-prep-na-stole,ex-u5-prep-na-stene
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u5-st-teach-prepositional-5`

- Objectives: obj-u5-prepositional
- Sessions: u5-s-prepositional
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 3b050213d11c22a5b6134560697ac04dce2e83fa9c5130bbd14da6879e59e7ee

kind=TEACH
title=Three admitted function words
body=Use these rows only as quoted. Do not build a new phrase from them. в — in. на — on (place). о — about. 
dominantAction=Continue
references=ref-u35-attribution
items=
examples=
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u5-st-teach-prepositional-6`

- Objectives: obj-u5-prepositional
- Sessions: u5-s-prepositional
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: cb87a848f8f99aa94e95b801f352935c40da9d7f76c7349f8a58f9d142664c83

kind=TEACH
title=Two admitted field pairs
body=Each pair is the first field, then the second field, from the same admitted row. кни́га / кни́ге. письмо́ / письме́. 
dominantAction=Continue
references=ref-u35-attribution
items=
examples=
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u5-st-guided-prepositional-6`

- Objectives: obj-u5-prepositional
- Sessions: u5-s-prepositional
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 7e975404dfa6b4fbe2d6d2d42322adb44af7e0e98ba734afc6f2e0ebbed8f33a

kind=GUIDED
title=Guided practice
body=Choose the admitted record. The feedback states the frozen field.
dominantAction=Continue
references=
items=u5-i-prep-field-книга,u5-i-prep-field-письмо
examples=
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=u5-st-rem-prepositional

### STEP `u5-st-rem-prepositional`

- Objectives: obj-u5-prepositional
- Sessions: u5-s-prepositional
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 8c96f4c098a5672a5855e900a55f44ec58c9962fc64deb6b0a2b53799f424487

kind=REMEDIATE
title=Remediation
body=Return to the explanation. Compare the shown form with the admitted record. Do not invent a form that is absent from the record.
dominantAction=Continue
references=
items=
examples=ex-u5-prep-o-knige,ex-u5-prep-o-pisme
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u5-st-check-prepositional`

- Objectives: obj-u5-prepositional
- Sessions: u5-s-prepositional
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: e631efa454bad2d17902fe90afcef1f893d5bb3d9a7555ed47a8e5b3dc3e50e5

kind=CHECK
title=Independent checks
body=These checks use only admitted records. A miss returns to the explanation.
dominantAction=Continue
references=
items=u5-i-prep-o-sade,u5-i-prep-v-moskve
examples=
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=u5-st-rem-prepositional

### SESSION `u5-s-indeclinable`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-indeclinable
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: f0edc3e893da3c516ccd6e1ab8df3baf2cfc66da04fae21894c35c718c1359a5

title=Recognize admitted indeclinable nouns
estimatedMinutes=12
objectives=obj-u5-indeclinable
steps=u5-st-teach-indeclinable-1,u5-st-guided-indeclinable-1,u5-st-teach-indeclinable-2,u5-st-teach-indeclinable-3,u5-st-rem-indeclinable,u5-st-check-indeclinable

### STEP `u5-st-teach-indeclinable-1`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-indeclinable
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: a662eeda9fad34bb51d00162c056b222f864184ba4c5a317f282dbfdd3babcef

kind=TEACH
title=What the frozen page says about words that do not change
body=The frozen page says these have irregular genders, and are indeclinable (that is, they don't change according to case or number). ра́дио — radio. такси́ — taxi. 
dominantAction=Continue
references=ref-u35-attribution
items=
examples=ex-u5-indecl-радио,ex-u5-indecl-такси
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u5-st-guided-indeclinable-1`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-indeclinable
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 1008551bd9f0ac0c79b28eed66b3c5e2e79fa640869c90a3043d670a5255bd7d

kind=GUIDED
title=Guided practice
body=Choose the admitted record. The feedback states the frozen field.
dominantAction=Continue
references=
items=u5-i-indecl-радио
examples=
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=u5-st-rem-indeclinable

### STEP `u5-st-teach-indeclinable-2`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-indeclinable
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: faf0390f24a62293617b2972734c7ef33f728ce1c7dc5b56b4cfad04f2958093

kind=TEACH
title=Four admitted rows with the flag set
body=The admitted rows below have the flag set. That flag is the record used here. метро́ — metro. кино́ — cinema. пальто́ — coat. ко́фе — coffee. 
dominantAction=Continue
references=ref-u35-attribution
items=
examples=ex-u5-indecl-метро,ex-u5-indecl-кино,ex-u5-indecl-пальто,ex-u5-indecl-кофе
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u5-st-teach-indeclinable-3`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-indeclinable
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 8e36e5e5dae2addd6759d635248df0d91523eaff5ee51df963f9b164b0cc10bd

kind=TEACH
title=Two more admitted rows with the flag set
body=The admitted rows below have the flag set. кафе́ — café. меню́ — menu. 
dominantAction=Continue
references=ref-u35-attribution
items=
examples=ex-u5-indecl-кафе,ex-u5-indecl-меню
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u5-st-rem-indeclinable`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-indeclinable
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: e928698bca3cde4a1c4020f04193b54e791accb3290897180cffa29ee2bfcebf

kind=REMEDIATE
title=Remediation
body=Return to the explanation. Compare the shown form with the admitted record. Do not invent a form that is absent from the record.
dominantAction=Continue
references=
items=
examples=ex-u5-indecl-радио,ex-u5-indecl-такси
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u5-st-check-indeclinable`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-indeclinable
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: f06b3744dbc5bf2e4c2d4329ec693832f6c8c0a4b9061f2352721ba9f2355e5d

kind=CHECK
title=Independent checks
body=These checks use only admitted records. A miss returns to the explanation.
dominantAction=Continue
references=
items=u5-i-indecl-такси,u5-i-indecl-метро,u5-i-indecl-кино,u5-i-decl-книга,u5-i-decl-окно
examples=
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=u5-st-rem-indeclinable

### SESSION `u5-s-book`

- Objectives: obj-u5-book
- Sessions: u5-s-book
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 1a391aa6934d86d57b75d4af381a862149d307200d6b278afb4a4eebaedf4e19

title=Self-attest Lesson 5
estimatedMinutes=10
objectives=obj-u5-book
steps=u5-st-book-work,u5-st-book

### STEP `u5-st-book-work`

- Objectives: obj-u5-book
- Sessions: u5-s-book
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: b95a352ae5e41b9c5cabdabeaef0807af31ff7e36d9369201f4ac309099a351a

kind=BOOK
title=Work Lesson 5 in your copy
body=Do Lesson 5 in the copy you own. Use that book's explanations and practice. The proposed records in this unit are a separate licensed set and do not copy the book.
dominantAction=Continue
references=ref-book-topics,ref-u35-attribution
items=
examples=
unavailableReason=
bookDirection=Open Lesson 5 in your copy and complete its lesson work.
companionDirection=
remediationStepId=

### STEP `u5-st-book`

- Objectives: obj-u5-book
- Sessions: u5-s-book
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: f107026f965f2b928f4f9a7c4305c66ba0a1cc2b7834c89fe1408deb33cfbe66

kind=BOOK
title=Record Lesson 5 book work
body=When you have done Lesson 5 in your copy, self-attest here. Attestation records your report. It does not grade the lesson, approve a mapping, or approve the proposed records.
dominantAction=Continue
references=
items=
examples=
unavailableReason=
bookDirection=Self-attest only after the Lesson 5 work in your copy.
companionDirection=
remediationStepId=

### EXAMPLE `ex-u5-q-kto`

- Objectives: obj-u5-questions
- Sessions: u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: ffa08348392b83520a43a3e744bc6fb15f4f298bb373478e28f5b95d67d19117

kind=LEXICAL
letterId=
display=Кто
note=Who
objectiveId=obj-u5-questions
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-u5-q-chto`

- Objectives: obj-u5-questions
- Sessions: u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 8ecd5da148ec03b1aa3b66c39557158a3ba7c83656f50bb108370fec5dd85397

kind=LEXICAL
letterId=
display=Что
note=What
objectiveId=obj-u5-questions
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-u5-q-kogda`

- Objectives: obj-u5-questions
- Sessions: u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 72692040f2ba8e1f2449f10ef71f16785fdd387ea05597ee7e4cdefa46ea70ed

kind=LEXICAL
letterId=
display=Когда
note=When
objectiveId=obj-u5-questions
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-u5-q-pochemu`

- Objectives: obj-u5-questions
- Sessions: u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 60a317ee292c3e74785c09b6b53f3fda10726914851112f5cf603da89c8c9d44

kind=LEXICAL
letterId=
display=Почему
note=Why
objectiveId=obj-u5-questions
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-u5-q-kak`

- Objectives: obj-u5-questions
- Sessions: u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: b008dfbe142d08887005a16028955e8f9a243407841519fcd10aa43c2a19c913

kind=LEXICAL
letterId=
display=Как
note=How
objectiveId=obj-u5-questions
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-u5-q-gde`

- Objectives: obj-u5-questions
- Sessions: u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: f4b6f5aa28c2e1fe449ec0093906c0af6311b5e0a7c2d271f21bbb6952a3d94e

kind=LEXICAL
letterId=
display=Где
note=Where (location)
objectiveId=obj-u5-questions
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-u5-q-kuda`

- Objectives: obj-u5-questions
- Sessions: u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 9dc89d27f66795491f39617f88e1531bebff8b6c941576a82c729834f2178e00

kind=LEXICAL
letterId=
display=Куда
note=Whither, where to
objectiveId=obj-u5-questions
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-u5-q-otkuda`

- Objectives: obj-u5-questions
- Sessions: u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: d10381d4f77249def940a6dfda2bc60d47e1803507e170e1d3e49a02a5593253

kind=LEXICAL
letterId=
display=Откуда
note=Whence, where from
objectiveId=obj-u5-questions
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-u5-q-skolko`

- Objectives: obj-u5-questions
- Sessions: u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: d480edae2b44dbe476cf2b5323c5ac29a709a53624e0edccdf9364c15ee6f3e6

kind=LEXICAL
letterId=
display=Сколько
note=How much, how many
objectiveId=obj-u5-questions
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-u5-q-kakoy`

- Objectives: obj-u5-questions
- Sessions: u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 975cfe490f5290a049be8e97b9f78ce697c6084ff442ced6d6f08b7da52fa964

kind=LEXICAL
letterId=
display=Какой
note=What kind
objectiveId=obj-u5-questions
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-u5-pattern-pattern-chto-eto`

- Objectives: obj-u5-questions
- Sessions: u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: dae12630751adacf49c2e7b3f823ee1178750956618d87746ecf6577786ec570

kind=LEXICAL
letterId=
display=Что это?
note=What is this?
objectiveId=obj-u5-questions
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-u5-pattern-pattern-kto-eto`

- Objectives: obj-u5-questions
- Sessions: u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: f172e112ab51d028d119b52a86c7acaad0dfcecfdad4710a1ac5f81f87360709

kind=LEXICAL
letterId=
display=Кто это?
note=Who is this?
objectiveId=obj-u5-questions
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-u5-pattern-pattern-kak-dela`

- Objectives: obj-u5-questions
- Sessions: u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 32ca8e9a8cffa63b8ba010151e1c398c6524b2fcb1ca298e864e0f629cab1dce

kind=LEXICAL
letterId=
display=Как дела?
note=How do you do?/How're things?
objectiveId=obj-u5-questions
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-u5-pattern-pattern-gde-zhivete`

- Objectives: obj-u5-questions
- Sessions: u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 04c8c9e9c781011700e3027ac45cf023af41a2ecfaf628af109cb2c937571b9b

kind=LEXICAL
letterId=
display=Где вы живёте?
note=Where do you live?
objectiveId=obj-u5-questions
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-u5-prep-o-knige`

- Objectives: obj-u5-prepositional
- Sessions: u5-s-prepositional
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 910c0bbdbe2c40d72f3b3a2daa9911718385c6d745b7c1f8847739f20b77699f

kind=LEXICAL
letterId=
display=О книге
note=About the book
objectiveId=obj-u5-prepositional
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-u5-prep-o-pisme`

- Objectives: obj-u5-prepositional
- Sessions: u5-s-prepositional
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 8f735e4293751e1f4d2dfa4117afc7baf1e5a080ef07c5e871f62630226a3907

kind=LEXICAL
letterId=
display=О письме
note=About the letter
objectiveId=obj-u5-prepositional
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-u5-prep-o-sade`

- Objectives: obj-u5-prepositional
- Sessions: u5-s-prepositional
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 751fe03887bd0aad6209c7f5e99034d0268338569b9d05011b964f9df85d7f94

kind=LEXICAL
letterId=
display=О саде
note=About the garden
objectiveId=obj-u5-prepositional
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-u5-prep-v-moskve`

- Objectives: obj-u5-prepositional
- Sessions: u5-s-prepositional
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: e8a59f9b877013eb60eb43e94b67150b66ca899b36039ede79b9ae43b32adec5

kind=LEXICAL
letterId=
display=я в Москве
note=I'm in Moscow
objectiveId=obj-u5-prepositional
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-u5-prep-na-kryshe`

- Objectives: obj-u5-prepositional
- Sessions: u5-s-prepositional
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: e2d52028f391ad3f3fea0999f5fc74383a63b65f630df333867a1878e1102b60

kind=LEXICAL
letterId=
display=Он на крыше
note=He's on the roof
objectiveId=obj-u5-prepositional
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-u5-prep-v-teatre`

- Objectives: obj-u5-prepositional
- Sessions: u5-s-prepositional
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 237669aaa95fa398331e6651b98df4c4bb6faacd16d8e16da3a0f065280bdffa

kind=LEXICAL
letterId=
display=Он в театре
note=He is at the theatre
objectiveId=obj-u5-prepositional
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-u5-prep-na-stole`

- Objectives: obj-u5-prepositional
- Sessions: u5-s-prepositional
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 8f2422bec4ed5c8c237d6fb51c2ecb20d47f9f90a78c77c6cea5dc80545c5d8e

kind=LEXICAL
letterId=
display=Ваза стоит на столе.
note=The vase stands on the table
objectiveId=obj-u5-prepositional
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-u5-prep-na-stene`

- Objectives: obj-u5-prepositional
- Sessions: u5-s-prepositional
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: b7439995f3309cc490f8c011d9417bd417edc2727ba8caa970e1c1870d6fd7ff

kind=LEXICAL
letterId=
display=Картина висит на стене.
note=The painting hangs on the wall
objectiveId=obj-u5-prepositional
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-u5-indecl-радио`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-indeclinable
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 78fa7f20c83c7d2b30622dbe1c03a69e1eb2b66d1843c5d9c769230e864d34c2

kind=LEXICAL
letterId=
display=ра́дио
note=radio
objectiveId=obj-u5-indeclinable
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-u5-indecl-такси`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-indeclinable
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 002483f5815bacc81b21f8994fa0931fa66b1a1efcdcf826e0bfa1639c4dddff

kind=LEXICAL
letterId=
display=такси́
note=taxi
objectiveId=obj-u5-indeclinable
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-u5-indecl-метро`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-indeclinable
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 521a30ceece99b378a6d39711d5ef8cb927b85455cf5e71b7570f841a36fa6bd

kind=LEXICAL
letterId=
display=метро́
note=metro
objectiveId=obj-u5-indeclinable
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-u5-indecl-кино`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-indeclinable
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 94befebee3a19773a7a3400a59e72a76fcb87aa05ecb48f458993d541b607454

kind=LEXICAL
letterId=
display=кино́
note=cinema
objectiveId=obj-u5-indeclinable
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-u5-indecl-пальто`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-indeclinable
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 1211f2719828fb7d94b7cf62ede9f8cf1ad53caec2f471112430b93d9e3a787e

kind=LEXICAL
letterId=
display=пальто́
note=coat
objectiveId=obj-u5-indeclinable
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-u5-indecl-кофе`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-indeclinable
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: f7844bc979e037cfd761f21d46a38212a06ea52353a0c290dcf3062e6ec7fb6a

kind=LEXICAL
letterId=
display=ко́фе
note=coffee
objectiveId=obj-u5-indeclinable
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-u5-indecl-кафе`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-indeclinable
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 18d3dc880e3a55981c1cf7dd907f4b752e9dbf3158c6076edc72cce2df6423a1

kind=LEXICAL
letterId=
display=кафе́
note=café
objectiveId=obj-u5-indeclinable
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-u5-indecl-меню`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-indeclinable
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 7171ecb8fe9ad72c47543c918c837184069b1f102e1378536822574051d0b277

kind=LEXICAL
letterId=
display=меню́
note=menu
objectiveId=obj-u5-indeclinable
reviewStatus=PENDING_QUALIFIED_REVIEW

### GUIDED_PRACTICE `u5-i-q-kto`

- Objectives: obj-u5-questions
- Sessions: u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 779141365ac79a3dc7bdc6eee9b5a8170882a521fe99f879ceb6fa667ef43686

kind=SOURCE_QUESTION
language=en
prompt=Which admitted question word is recorded for this English purpose: Who?
promptLetter=Кто
options=Кто | Что | Когда | Почему | Как | Где
answer=Кто
feedback=The frozen table records Кто for Who.
objectiveId=obj-u5-questions
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW

### GUIDED_PRACTICE `u5-i-q-chto`

- Objectives: obj-u5-questions
- Sessions: u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: e1d42579e18632ec9db62b763fc8e9e5a08ed8ba11ba8c4115054ea4bd2125dc

kind=SOURCE_QUESTION
language=en
prompt=Which admitted question word is recorded for this English purpose: What?
promptLetter=Что
options=Кто | Что | Когда | Почему | Как | Где
answer=Что
feedback=The frozen table records Что for What.
objectiveId=obj-u5-questions
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW

### GUIDED_PRACTICE `u5-i-pattern-pattern-chto-eto`

- Objectives: obj-u5-questions
- Sessions: u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 2395be01776de2c66d7e820ff9b683f952ff3c096764f848edb869132c40c91f

kind=SOURCE_QUESTION
language=en
prompt=Which admitted meaning is recorded for the shown written question?
promptLetter=Что это?
options=What is this? | Who is this? | How do you do?/How're things? | Where do you live?
answer=What is this?
feedback=The frozen page records this meaning: What is this?. Written punctuation is not listening practice.
objectiveId=obj-u5-questions
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW

### GUIDED_PRACTICE `u5-i-prep-o-knige`

- Objectives: obj-u5-prepositional
- Sessions: u5-s-prepositional
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 308419ab53759c323feb5e14489c4c56677c4e89d0cef4b0ecc37376bc6bdd89

kind=SOURCE_CASE
language=en
prompt=Which admitted description is recorded for the shown example?
promptLetter=О книге
options=About the book | About the letter | About the garden | I'm in Moscow
answer=About the book
feedback=The frozen page records this example as: About the book.
objectiveId=obj-u5-prepositional
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW

### GUIDED_PRACTICE `u5-i-prep-o-pisme`

- Objectives: obj-u5-prepositional
- Sessions: u5-s-prepositional
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 88449be1b99692ed0ac48a35f45e091992d15aa0ddee4f63b664980c8a71dce2

kind=SOURCE_CASE
language=en
prompt=Which admitted description is recorded for the shown example?
promptLetter=О письме
options=About the book | About the letter | About the garden | I'm in Moscow
answer=About the letter
feedback=The frozen page records this example as: About the letter.
objectiveId=obj-u5-prepositional
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW

### GUIDED_PRACTICE `u5-i-prep-field-книга`

- Objectives: obj-u5-prepositional
- Sessions: u5-s-prepositional
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: f2bdbaea2880e43a116ce6f152fb297cc38d9d5e194c11a636b91edbad010da0

kind=SOURCE_FORM
language=en
prompt=Which string is the admitted second field, not the first field?
promptLetter=кни́га
options=кни́га | кни́ге
answer=кни́ге
feedback=The frozen row lists that field as кни́ге.
objectiveId=obj-u5-prepositional
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW

### GUIDED_PRACTICE `u5-i-prep-field-письмо`

- Objectives: obj-u5-prepositional
- Sessions: u5-s-prepositional
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 188e63e72df6fd1908b5d948a911f5b3393cc9a62c0973b156689797f970c29f

kind=SOURCE_FORM
language=en
prompt=Which string is the admitted second field, not the first field?
promptLetter=письмо́
options=письмо́ | письме́
answer=письме́
feedback=The frozen row lists that field as письме́.
objectiveId=obj-u5-prepositional
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW

### GUIDED_PRACTICE `u5-i-indecl-радио`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-indeclinable
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: ef0508b7ab6e2728a06c40c728fb80e38370341edf428735100beb2e82a5244f

kind=SOURCE_INDECLINABLE
language=en
prompt=Which flag does the admitted row record?
promptLetter=ра́дио
options=1 | 0
answer=1
feedback=The frozen row sets the flag to 1.
objectiveId=obj-u5-indeclinable
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW

### INDEPENDENT_CHECK `u5-i-q-kogda`

- Objectives: obj-u5-questions
- Sessions: u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: b69b03b4ef6467a40c5f1384119f4cec8b2e2c028306d95b05a6482cc7769eab

kind=SOURCE_QUESTION
language=en
prompt=Which admitted question word is recorded for this English purpose: When?
promptLetter=Когда
options=Кто | Что | Когда | Почему | Как | Где
answer=Когда
feedback=The frozen table records Когда for When.
objectiveId=obj-u5-questions
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW

### INDEPENDENT_CHECK `u5-i-q-pochemu`

- Objectives: obj-u5-questions
- Sessions: u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 23e5255957964a4167f04edd5afc8945b9d64e16a106758285cdf59af12b34c7

kind=SOURCE_QUESTION
language=en
prompt=Which admitted question word is recorded for this English purpose: Why?
promptLetter=Почему
options=Кто | Что | Когда | Почему | Как | Где
answer=Почему
feedback=The frozen table records Почему for Why.
objectiveId=obj-u5-questions
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW

### INDEPENDENT_CHECK `u5-i-q-kak`

- Objectives: obj-u5-questions
- Sessions: u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 15548abb04517d25dc5e7ed14f84b08ceeebd1e4824aa24e601bb0be1a503bef

kind=SOURCE_QUESTION
language=en
prompt=Which admitted question word is recorded for this English purpose: How?
promptLetter=Как
options=Кто | Что | Когда | Почему | Как | Где
answer=Как
feedback=The frozen table records Как for How.
objectiveId=obj-u5-questions
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW

### INDEPENDENT_CHECK `u5-i-q-gde`

- Objectives: obj-u5-questions
- Sessions: u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 6f7b7af8e184b12ab4fa644170eab6b1f87dfd9121b6034dc6684a863d9d39fe

kind=SOURCE_QUESTION
language=en
prompt=Which admitted question word is recorded for this English purpose: Where (location)?
promptLetter=Где
options=Кто | Что | Когда | Почему | Как | Где
answer=Где
feedback=The frozen table records Где for Where (location).
objectiveId=obj-u5-questions
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW

### INDEPENDENT_CHECK `u5-i-pattern-pattern-kto-eto`

- Objectives: obj-u5-questions
- Sessions: u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 41c5c790befd37b23d6507a0c34266cd39118fc6142b2cf0deda8e07012fee3e

kind=SOURCE_QUESTION
language=en
prompt=Which admitted meaning is recorded for the shown written question?
promptLetter=Кто это?
options=What is this? | Who is this? | How do you do?/How're things? | Where do you live?
answer=Who is this?
feedback=The frozen page records this meaning: Who is this?. Written punctuation is not listening practice.
objectiveId=obj-u5-questions
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW

### INDEPENDENT_CHECK `u5-i-pattern-pattern-kak-dela`

- Objectives: obj-u5-questions
- Sessions: u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: b9c486001111072f90736ae6ce6803cb8719726bfb277d21c25dca061a1fd392

kind=SOURCE_QUESTION
language=en
prompt=Which admitted meaning is recorded for the shown written question?
promptLetter=Как дела?
options=What is this? | Who is this? | How do you do?/How're things? | Where do you live?
answer=How do you do?/How're things?
feedback=The frozen page records this meaning: How do you do?/How're things?. Written punctuation is not listening practice.
objectiveId=obj-u5-questions
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW

### INDEPENDENT_CHECK `u5-i-pattern-pattern-gde-zhivete`

- Objectives: obj-u5-questions
- Sessions: u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 8de3d7b4760fa9684b4bf36470c052b7e504b88326717eced404d4e61d0bff4f

kind=SOURCE_QUESTION
language=en
prompt=Which admitted meaning is recorded for the shown written question?
promptLetter=Где вы живёте?
options=What is this? | Who is this? | How do you do?/How're things? | Where do you live?
answer=Where do you live?
feedback=The frozen page records this meaning: Where do you live?. Written punctuation is not listening practice.
objectiveId=obj-u5-questions
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW

### INDEPENDENT_CHECK `u5-i-prep-o-sade`

- Objectives: obj-u5-prepositional
- Sessions: u5-s-prepositional
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 2761eb33644bd6633662ce2fb22b206c247c6b1a6439e1e0f2c2d7bb8a49abce

kind=SOURCE_CASE
language=en
prompt=Which admitted description is recorded for the shown example?
promptLetter=О саде
options=About the book | About the letter | About the garden | I'm in Moscow
answer=About the garden
feedback=The frozen page records this example as: About the garden.
objectiveId=obj-u5-prepositional
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW

### INDEPENDENT_CHECK `u5-i-prep-v-moskve`

- Objectives: obj-u5-prepositional
- Sessions: u5-s-prepositional
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: be751b43c576722dd30f10b57574b6981922fed6424306c720a88214e5cf6e4e

kind=SOURCE_CASE
language=en
prompt=Which admitted description is recorded for the shown example?
promptLetter=я в Москве
options=About the book | About the letter | About the garden | I'm in Moscow
answer=I'm in Moscow
feedback=The frozen page records this example as: I'm in Moscow.
objectiveId=obj-u5-prepositional
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW

### INDEPENDENT_CHECK `u5-i-indecl-такси`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-indeclinable
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: f592e98dfe3df8f1e37087439e0b8e5d592dfe0ac476420a8f0be7444bb7f497

kind=SOURCE_INDECLINABLE
language=en
prompt=Which flag does the admitted row record?
promptLetter=такси́
options=1 | 0
answer=1
feedback=The frozen row sets the flag to 1.
objectiveId=obj-u5-indeclinable
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW

### INDEPENDENT_CHECK `u5-i-indecl-метро`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-indeclinable
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 8722b0edc85f0cd064661c51cd012bd50fde2601847979ae255b0c4aa580417d

kind=SOURCE_INDECLINABLE
language=en
prompt=Which flag does the admitted row record?
promptLetter=метро́
options=1 | 0
answer=1
feedback=The frozen row sets the flag to 1.
objectiveId=obj-u5-indeclinable
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW

### INDEPENDENT_CHECK `u5-i-indecl-кино`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-indeclinable
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 462209215ae18ae7f2f88a13c4f2cfbd1c54eaea532a898acb36b0107cad3a4c

kind=SOURCE_INDECLINABLE
language=en
prompt=Which flag does the admitted row record?
promptLetter=кино́
options=1 | 0
answer=1
feedback=The frozen row sets the flag to 1.
objectiveId=obj-u5-indeclinable
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW

### INDEPENDENT_CHECK `u5-i-decl-книга`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-indeclinable
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 291e7297db80e9067929554f655a37ed8087a6dd9fd7d86f3eea529651fa68c9

kind=SOURCE_INDECLINABLE
language=en
prompt=Which flag does the admitted row record?
promptLetter=кни́га
options=1 | 0
answer=0
feedback=The frozen row sets the flag to 0.
objectiveId=obj-u5-indeclinable
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW

### INDEPENDENT_CHECK `u5-i-decl-окно`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-indeclinable
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: a2daa3b9599686e001478486bdae337f7e3f251be03da2fb4d21b75db957bac5

kind=SOURCE_INDECLINABLE
language=en
prompt=Which flag does the admitted row record?
promptLetter=окно́
options=1 | 0
answer=0
feedback=The frozen row sets the flag to 0.
objectiveId=obj-u5-indeclinable
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW

### REFERENCE `ref-u35-attribution`

- Objectives: none
- Sessions: u5-s-orient, u5-s-questions, u5-s-prepositional, u5-s-indeclinable, u5-s-book
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: LICENSE_OR_ATTRIBUTION
- Current review status: NOT_REQUIRED
- Content hash: e7a751050f5aacb4c971a378b4103d08cecef29957988182e854e05834b204db

title=Units 3–5 source notice
summary=Proposed Units 3–5 adapt CC BY-SA 4.0 dictionary rows and Wikibooks grammar excerpts.
detail=OpenRussian dictionary data is frozen at commit 50e210c4803237779cb562bc1abcea529066031c under CC BY-SA 4.0. Wikibooks Russian grammar excerpts are frozen by oldid under the CC BY-SA 4.0 route. The adapted selection and arrangement are share-alike. Application code, user data, and user recordings are outside that statement. No audio is included in this proposed curriculum. Review is pending. The packaged CC BY-SA 4.0 legal code is cc-by-sa-4.0.txt.
classification=LICENSE_OR_ATTRIBUTION
introducedInUnit=3
searchTerms=units 3-5 source notice
reviewStatus=NOT_REQUIRED

### REFERENCE `ref-book-topics`

- Objectives: none
- Sessions: u5-s-book
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: OPERATIONAL_GUIDANCE
- Current review status: NOT_REQUIRED
- Content hash: c5726abaa94b03a1b22aedf666520d91afbd445ebedd6e39c3961f9a9a9e70e2

title=Where lesson bodies are packaged
summary=Some units contain proposed lesson bodies that still require qualified review.
detail=A unit's Course status shows whether its lesson body is prepared. Units marked Not prepared or Not yet installed do not contain a usable lesson body. Open the matching lesson in the copy you own. This reference does not approve the packaged records.
classification=OPERATIONAL_GUIDANCE
introducedInUnit=3
searchTerms=where lesson bodies are packaged
reviewStatus=NOT_REQUIRED

### PROVENANCE `prov-u5-contrast-flag-книга`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-orient, u5-s-indeclinable
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: d0a0699a2f1541a07b193bcf1f5bdd26aa78bd13c3d4f3b3b04953854a009677

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:книга#indeclinable
originalSourceText=0
shippedText=0
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=5feceb66ffc86f38d952786c6d696c79c2dbc239dd4e91b46729d73a27fb57e9
contentHash=5feceb66ffc86f38d952786c6d696c79c2dbc239dd4e91b46729d73a27fb57e9
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-contrast-flag-окно`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-orient, u5-s-indeclinable
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 813b31ccfd98b9c027286ffce2384a9b7b4ad49673d217ef9db62dc54166721b

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:окно#indeclinable
originalSourceText=0
shippedText=0
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=5feceb66ffc86f38d952786c6d696c79c2dbc239dd4e91b46729d73a27fb57e9
contentHash=5feceb66ffc86f38d952786c6d696c79c2dbc239dd4e91b46729d73a27fb57e9
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-contrast-книга`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-orient, u5-s-indeclinable
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: caef57da2e77c7f20a2a204c605000602f943a3f5edccbf0ae72074141c13e02

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

### PROVENANCE `prov-u5-contrast-окно`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-orient, u5-s-indeclinable
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 2c889591e46b07759701563f1bdc4c231fb7dceae20efdb2780b32b215fe27a7

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

### PROVENANCE `prov-u5-flag-кафе`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-orient, u5-s-indeclinable
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 3846ee84f88eaab156d983ecdd0ac46c4605aeb10fc75ec11b0094d16488af45

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:кафе#indeclinable
originalSourceText=1
shippedText=1
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b
contentHash=6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-flag-кино`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-orient, u5-s-indeclinable
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 3a5a58642718f269c6f7204d7a21429eac8a5f1271f4575ee800497f4a40aa80

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:кино#indeclinable
originalSourceText=1
shippedText=1
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b
contentHash=6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-flag-кофе`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-orient, u5-s-indeclinable
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: aa40234abaa2078f54164a1cb55b5fbf4508d9c085b1aa4586b260fb6845a670

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:кофе#indeclinable
originalSourceText=1
shippedText=1
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b
contentHash=6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-flag-меню`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-orient, u5-s-indeclinable
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: d982c8ac8a58f77dffcf20e7ed8a2efedfa8769bbd38b9313b859d9be2bd37bd

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:меню#indeclinable
originalSourceText=1
shippedText=1
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b
contentHash=6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-flag-метро`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-orient, u5-s-indeclinable
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: fd4b0422159345d48f2372a3551c86b721d2e1ac333cc088c9ba62bdf7e5b7bf

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:метро#indeclinable
originalSourceText=1
shippedText=1
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b
contentHash=6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-flag-пальто`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-orient, u5-s-indeclinable
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 61f8d543fcdf89e9ef30b5cc3c7f62f6fe5e2a3ea0492b484ce996791c0f00a3

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:пальто#indeclinable
originalSourceText=1
shippedText=1
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b
contentHash=6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-flag-радио`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-orient, u5-s-indeclinable
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 94ba09d1cef284de9021194548d75d850028f3c587018823f9b98971b5688363

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:радио#indeclinable
originalSourceText=1
shippedText=1
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b
contentHash=6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-flag-такси`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-orient, u5-s-indeclinable
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 0d3a33129ab8d752a72eb277d8debdc725f81c4b599db28bdb67ca902c12a737

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:такси#indeclinable
originalSourceText=1
shippedText=1
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b
contentHash=6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-indecl-gloss-кафе`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-orient, u5-s-indeclinable
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 3a2e4027f20a8fb8d615cb31ed82dd269004d675c227d848827ba8177a535ce4

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:кафе#translations_en
originalSourceText=café, coffee-house
shippedText=café
transformation=sense selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=ef59b4f763849f319cc6096370c7747b023923ba50a9f0c5da3960390c8da700
contentHash=850f7dc43910ff890f8879c0ed26fe697c93a067ad93a7d50f466a7028a9bf4e
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-indecl-gloss-кино`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-orient, u5-s-indeclinable
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 30f92d2ab0cad09bb17e6e0c57fa077463ea3922712607c333ce8727c5914d9c

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:кино#translations_en
originalSourceText=cinema, pictures, movies
shippedText=cinema
transformation=sense selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=bd106ca0ca006f619a909cb86938a10d50504d3986b3e8dba75f70de58de6b54
contentHash=b815843425f6ac6c018ef9ee434b4a401202a8b175ded4b53b4ba0fd8069887f
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-indecl-gloss-кофе`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-orient, u5-s-indeclinable
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: dfe1e15e6ce3fbcf6b57b85504a2cab10648b6f00265ddfc38db08d8c8a67f1c

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:кофе#translations_en
originalSourceText=coffee
shippedText=coffee
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=37290d74ac4d186e3a8e5785d259d2ec04fac91ae28092e7620ec8bc99e830aa
contentHash=37290d74ac4d186e3a8e5785d259d2ec04fac91ae28092e7620ec8bc99e830aa
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-indecl-gloss-меню`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-orient, u5-s-indeclinable
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: cd88bd05c2f0544b831e4d2c6f8085fac6e1d87d4365979c8c02b36365b39040

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:меню#translations_en
originalSourceText=menu, bill of fare
shippedText=menu
transformation=sense selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=70933cb30015533f412e4c5a00d4214707413a268efe2899a93d3bc3e8024208
contentHash=398991009da1d251792eb353a0b7b185bc83e71e12e489e73228b554fc6cebc5
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-indecl-gloss-метро`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-orient, u5-s-indeclinable
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 3ba01aa5c274da227bd638edc2ec815552fc3ff0a0174f414ec14371cca7ac0d

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:метро#translations_en
originalSourceText=metro; subway; underground
shippedText=metro
transformation=sense selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=4d173361f5a962716b5c723ab95351b8a5801eeb250224e987a7b6b594ff80e8
contentHash=0bc851d8e8861b20da271d23994b9bc2604785313d3369acd3a2f8121b97be61
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-indecl-gloss-пальто`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-orient, u5-s-indeclinable
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: b95b350941dd849d936918d207309a516270689d7e9075efac298ae3b0ebb30e

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:пальто#translations_en
originalSourceText=coat, topcoat
shippedText=coat
transformation=sense selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=ce913900ce30e13b51815ee8cab9efb6b6ef0d700004680ed2b4d24c51c90b9a
contentHash=8218f3d6780abf45a5a43657f5ae015c164c53d463dc85ebe302ef6c4fd517ed
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-indecl-gloss-радио`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-orient, u5-s-indeclinable
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 8592f096bc993b7f74acb2ad8bab91da6586bf719465e750b39438acff166a92

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:радио#translations_en
originalSourceText=radio, radio set, wireless
shippedText=radio
transformation=sense selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=73ff2fbf0421dfee7295b37ca8fd7904ba68efc16396f52905ae79fcf3d41276
contentHash=aa3a05d9d57c08d694cf230a36386221f56890092e54cf8d6b04fab1b2cc0702
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-indecl-gloss-такси`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-orient, u5-s-indeclinable
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: f80908ce4fe58c1935dae5ed0c3c4c429e92f30c2a65b48cc81f2fed50736be3

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:такси#translations_en
originalSourceText=taxi
shippedText=taxi
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=12796d3d5f4e132385f8af8e281efa211cdb2a05b8450039f037a6573e0bcb3f
contentHash=12796d3d5f4e132385f8af8e281efa211cdb2a05b8450039f037a6573e0bcb3f
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-indecl-rule`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-orient, u5-s-indeclinable
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 09f1e998ac45195d51bf5c641b8474eeda8d13ab96ee61d69ec38b7887fbb9e0

recordType=explanation
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Nominative?oldid=4213267
sourceRevision=4213267
sourceRecordId=nominative:these have irregular genders, and are indeclinable (that is, they don't change a
originalSourceText=these have irregular genders, and are indeclinable (that is, they don't change according to case or number)
shippedText=these have irregular genders, and are indeclinable (that is, they don't change according to case or number)
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=6bec55fd46db6aa454810912d195369f920b099dcfa87cd2b1992b0ed7ec95d6
contentHash=6bec55fd46db6aa454810912d195369f920b099dcfa87cd2b1992b0ed7ec95d6
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-indecl-word`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-orient, u5-s-indeclinable
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 682d858a2ca7b50211ad4894ac1ec3a4d325cd9242e66c0de28cc61d533462de

recordType=reference
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Nominative?oldid=4213267
sourceRevision=4213267
sourceRecordId=nominative:these have irregular genders, and are indeclinable (that is, they don't change a
originalSourceText=these have irregular genders, and are indeclinable (that is, they don't change according to case or number)
shippedText=indeclinable
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=6bec55fd46db6aa454810912d195369f920b099dcfa87cd2b1992b0ed7ec95d6
contentHash=9683da8ba20969a1e28af9b19bbd32c7ac52031cb877132632dd1cb9a5d3168a
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-indecl-кафе`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-orient, u5-s-indeclinable
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 85f837d442c766b5492150c372d554ac1a7b38048ade34e8b7fce634f53a03aa

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:кафе#accented
originalSourceText=кафе'
shippedText=кафе́
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=07597495047e0a19ec90f4bc9f13688dba3b3bbc1521caad7a7909359d7ead8d
contentHash=38e251ad0c05661e6261e3d237ff592770738e2dca1be89fc507e6737f34b3ff
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-indecl-кино`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-orient, u5-s-indeclinable
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 6299d6f1e87706a2bf1d4ff7a811255075ef70755af61fdcb9a3e3016853134c

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:кино#accented
originalSourceText=кино'
shippedText=кино́
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=556b93a1035af9e099c3e96d8b150be1d3ad6f74c8bc53f9b8e1af1dd91b9b03
contentHash=13152708c446f60bc6fdae43c3658089d523d8e15833f6d5d13ba43675dc6736
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-indecl-кофе`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-orient, u5-s-indeclinable
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 888adfbbf8acd9e34d6ce9099991158dc96b441711ead8ab5dfd782cb3530e90

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:кофе#accented
originalSourceText=ко'фе
shippedText=ко́фе
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=696a1cf68f373030effbec67b97e31ec86afd26293bbbd6b6fe8dbc53b080694
contentHash=df121b52d6a9a2348d2a79e55c2543cc87c6005e4d5e8b8b274a061cd3b7a7f9
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-indecl-меню`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-orient, u5-s-indeclinable
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 5ff59e60ddb2a534d160f719c8d6d81fa303ececfd1549cc1117863bd083a9c2

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:меню#accented
originalSourceText=меню'
shippedText=меню́
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=0a3782bfa7130faff8fed32cb693b5c24d15927ffb04a82c76b59254d503a9a6
contentHash=dabf46a2e101767cc9ace5c8155118402c02de9b0252f68c44cab6c10b95b671
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-indecl-метро`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-orient, u5-s-indeclinable
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 68c8c01d67a4cefb71c089416e73e24b6548c438d983ea22866cc1656c4da058

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:метро#accented
originalSourceText=метро'
shippedText=метро́
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=49c9edf483c6d0e96dac74ee743abb07ad496ff73c5ad2ed1de9c555ea2b52a9
contentHash=bf78c0cabf44bbcdb12b1d8a60ca7f9af2975ee33e233a3f6d4c3f92c31913ce
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-indecl-пальто`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-orient, u5-s-indeclinable
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: cddc55d4f4420f843454ffd0a22d39f80abb2d2273bdbd3ce68b93fdeb34d36b

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:пальто#accented
originalSourceText=пальто'
shippedText=пальто́
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=7cfbc2f27c1af6c2f132dd00a3c6bb01023993b22e6c1db4b2eb115c4c4c26b5
contentHash=52233811913a53618ca7811588f64166064f307cc9a6512bc99858639d89f67f
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-indecl-радио`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-orient, u5-s-indeclinable
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 9de88344507ac03db14af7f7a86ead346e5644dd596eeb82dfefc3471d8298bd

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:радио#accented
originalSourceText=ра'дио
shippedText=ра́дио
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=790c79fbae8bcc2225862b8915c3cba25c8d5578b0c332f0c7398a574187061d
contentHash=6eecfbe67c90f2dd9c3fc523aedcfa9306f00e6aec42bd722cd3c4ca9a9382c0
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-indecl-такси`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-orient, u5-s-indeclinable
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: bcb629fa58b27af3247c8634bdd545f311ad6b9494045ce634cac60491905081

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:такси#accented
originalSourceText=такси'
shippedText=такси́
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=9a6cd3dd37d85bb42275a929b020e7f4451c11ea64f2828de11cd86a2740e03c
contentHash=8f8979a9db7f67966836015bb8fe76278624bd30423ff7c2484335732e143f33
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-nom-field-книга`

- Objectives: obj-u5-prepositional
- Sessions: u5-s-orient, u5-s-prepositional
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 6abcc9a1adf12a2f33fbf8c2d56f5412a9116d5746cac64eebc48dcb83676b5c

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:книга#sg_nom
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

### PROVENANCE `prov-u5-nom-field-окно`

- Objectives: obj-u5-prepositional
- Sessions: u5-s-orient, u5-s-prepositional
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: c34547b95d1cd1ac8d2c40d1249ecc0da5bd23d98ea356912f2fd5a4f873f219

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:окно#sg_nom
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

### PROVENANCE `prov-u5-nom-field-письмо`

- Objectives: obj-u5-prepositional
- Sessions: u5-s-orient, u5-s-prepositional
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 2f2905227d5b94e8255709ac9349547d452cc5314908245ab13b611865d8705f

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:письмо#sg_nom
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

### PROVENANCE `prov-u5-nom-field-школа`

- Objectives: obj-u5-prepositional
- Sessions: u5-s-orient, u5-s-prepositional
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: cca54a45f716c9078d2df4f796c9774f0ea58781a4f590237954be44d94567e8

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:школа#sg_nom
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

### PROVENANCE `prov-u5-pattern-chto-eto`

- Objectives: obj-u5-questions
- Sessions: u5-s-orient, u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: ea14cee7a67994f2dcb1ec7b6716412691eeb6c3892b95805a46ed65a9c3b871

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

### PROVENANCE `prov-u5-pattern-chto-eto-en`

- Objectives: obj-u5-questions
- Sessions: u5-s-orient, u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: ef902dc552a783bad85160615abb16ce95e158f5ba18524bd95343e4d74febf5

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

### PROVENANCE `prov-u5-pattern-gde-zhivete`

- Objectives: obj-u5-questions
- Sessions: u5-s-orient, u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: b8618c1d020b3724a7d4e8b48e59d672eafccaf08a4a938c9fc396d814d72c6a

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Interrogative_Pronouns?oldid=4220316
sourceRevision=4220316
sourceRecordId=interrogative:Where do you live? - '''Где вы живёте?'''
originalSourceText=Where do you live? - '''Где вы живёте?'''
shippedText=Где вы живёте?
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=7e84a9af570ab7f4e966650ecf5931cf7ab4759125bd23c9bd18c5a95bc6fe1d
contentHash=ae551c8bab31d151716bae620c8b9a1f8537b6fdeb22e62440931fcad901cf95
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-pattern-gde-zhivete-en`

- Objectives: obj-u5-questions
- Sessions: u5-s-orient, u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: b4ba457e0d06200237cf13f3ff50ddb22b49cdf167d951b2bcce9bfdfb722de9

recordType=reference
sourceUrl=https://en.wikibooks.org/wiki/Russian/Interrogative_Pronouns?oldid=4220316
sourceRevision=4220316
sourceRecordId=interrogative:Where do you live? - '''Где вы живёте?'''
originalSourceText=Where do you live? - '''Где вы живёте?'''
shippedText=Where do you live?
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=7e84a9af570ab7f4e966650ecf5931cf7ab4759125bd23c9bd18c5a95bc6fe1d
contentHash=8a38c81b46381d2693666843c8bd213c5d9be3cb02fd1cf73bba5ae9f84b05de
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-pattern-kak-dela`

- Objectives: obj-u5-questions
- Sessions: u5-s-orient, u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 46aad6400a2960c0a52d4756bed6e4167da28277c1e01821fc366849955d4252

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

### PROVENANCE `prov-u5-pattern-kak-dela-en`

- Objectives: obj-u5-questions
- Sessions: u5-s-orient, u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 5013ce9b538dfee64efbfb406a61c40ad287014960241adc27c354c7254d806f

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

### PROVENANCE `prov-u5-pattern-kakoy-syr`

- Objectives: obj-u5-questions
- Sessions: u5-s-orient, u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 095f05868b37fe4d1053736779545b601cac14eee26fa0d4cc3da2cf8cbe4355

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Interrogative_Pronouns?oldid=4220316
sourceRevision=4220316
sourceRecordId=interrogative:What kind of cheese? - '''Какой сыр?'''
originalSourceText=What kind of cheese? - '''Какой сыр?'''
shippedText=Какой сыр?
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=87d4c786d70bf46d2977666fc6f92392e1bb621046e465d43449fb2a084ce343
contentHash=b1ca4c905c46cba38c5624b0003ad77c1be933b13f75f6dfd9eef6065f5dffc5
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-pattern-kakoy-syr-en`

- Objectives: obj-u5-questions
- Sessions: u5-s-orient, u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 05fcea727b10eac9c095ac464d154bfa02272b0176e2edb2ad54d34b8231ad28

recordType=reference
sourceUrl=https://en.wikibooks.org/wiki/Russian/Interrogative_Pronouns?oldid=4220316
sourceRevision=4220316
sourceRecordId=interrogative:What kind of cheese? - '''Какой сыр?'''
originalSourceText=What kind of cheese? - '''Какой сыр?'''
shippedText=What kind of cheese?
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=87d4c786d70bf46d2977666fc6f92392e1bb621046e465d43449fb2a084ce343
contentHash=b07dd82183ca911702ec4d21c7c04fe202b7856ded12e76c3cbe2fc0bc011ddc
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-pattern-kto-eto`

- Objectives: obj-u5-questions
- Sessions: u5-s-orient, u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: a597852e533dd581b9501c75d87ac2700a824e6cfc375e20a8a254ee5cc2e262

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

### PROVENANCE `prov-u5-pattern-kto-eto-en`

- Objectives: obj-u5-questions
- Sessions: u5-s-orient, u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 89d37783f465d2c341e009b4f39a6fc1512ed40cb51ef1e4df471c20caf3eca8

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

### PROVENANCE `prov-u5-pattern-kuda-idete`

- Objectives: obj-u5-questions
- Sessions: u5-s-orient, u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: d7b24afa824af7b555a15a10d15306497b158738e21bfafa83c5b03a31e63370

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Interrogative_Pronouns?oldid=4220316
sourceRevision=4220316
sourceRecordId=interrogative:Where are you walking to? - '''Куда вы идёте?'''
originalSourceText=Where are you walking to? - '''Куда вы идёте?'''
shippedText=Куда вы идёте?
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=bb2acffa30e2dd877bcd72bbe9a9e33f10bd18b81a5c98ad4654e205009b8cf1
contentHash=5e8a435476a86e56fb4c41fb27d1339c12268c1dc0acb6d4443f7c7adb9f878c
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-pattern-kuda-idete-en`

- Objectives: obj-u5-questions
- Sessions: u5-s-orient, u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 5b5f13c934214392226623b93b0bc5b68f05f56bcd82e59543cdf6cd936e21e3

recordType=reference
sourceUrl=https://en.wikibooks.org/wiki/Russian/Interrogative_Pronouns?oldid=4220316
sourceRevision=4220316
sourceRecordId=interrogative:Where are you walking to? - '''Куда вы идёте?'''
originalSourceText=Where are you walking to? - '''Куда вы идёте?'''
shippedText=Where are you walking to?
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=bb2acffa30e2dd877bcd72bbe9a9e33f10bd18b81a5c98ad4654e205009b8cf1
contentHash=37ce3e10e3f3dbd1f43d50fa0a0918877142f2cceac215abd7c1186d08e60819
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-pattern-pochemu`

- Objectives: obj-u5-questions
- Sessions: u5-s-orient, u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 4f12736bc007a07ac1472132a546c8aa011aa3228ec5d3ed70768fc6388f3cb4

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Interrogative_Pronouns?oldid=4220316
sourceRevision=4220316
sourceRecordId=interrogative:Why doesn't he know? - '''Почему он не знает?'''
originalSourceText=Why doesn't he know? - '''Почему он не знает?'''
shippedText=Почему он не знает?
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=4c714c2ae6dea976cbb59b3a4990463282b547ea0eb5f4289393a81c697df3d4
contentHash=82fa52c8532702245de84d3f56792d07377ebd574de5a67a3125846ebfdfce54
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-pattern-pochemu-en`

- Objectives: obj-u5-questions
- Sessions: u5-s-orient, u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: d64d6d6e1a7e802bb283b9733fd6dd34caf639b3fccdeb5062e5fb9ec4d00168

recordType=reference
sourceUrl=https://en.wikibooks.org/wiki/Russian/Interrogative_Pronouns?oldid=4220316
sourceRevision=4220316
sourceRecordId=interrogative:Why doesn't he know? - '''Почему он не знает?'''
originalSourceText=Why doesn't he know? - '''Почему он не знает?'''
shippedText=Why doesn't he know?
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=4c714c2ae6dea976cbb59b3a4990463282b547ea0eb5f4289393a81c697df3d4
contentHash=a7c33adb32b754de8a46d0977a90311d785606499233061bebbb2b22ce927b26
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-prep-field-книга`

- Objectives: obj-u5-prepositional
- Sessions: u5-s-orient, u5-s-prepositional
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 3e730ff8c02ed42fe3d8cc1b2138d077f35dcba373b58572587e833d13ec452c

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:книга#sg_prep
originalSourceText=кни'ге
shippedText=кни́ге
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=3c8e218d705ef6c383049c6f8adad645dda1d7f27c6dfbafb96b2b71568b4533
contentHash=1bb2f44922124550a2470f9f2cc1b5df0958097105a44354503b327ea8d116a3
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-prep-field-окно`

- Objectives: obj-u5-prepositional
- Sessions: u5-s-orient, u5-s-prepositional
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 56ae9d634303410f27e2ea9ec05f64a67be5b4c985603cb3f24f2325747d928f

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:окно#sg_prep
originalSourceText=окне'
shippedText=окне́
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=34af3e9dbdacdcc69b8184e5b2aef2b542b19fa863ae6db687481b45d29b38b0
contentHash=69a4a3c64ee39c6aef5757ff66366e5405092912908a0cb61c7477d82dba4b41
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-prep-field-письмо`

- Objectives: obj-u5-prepositional
- Sessions: u5-s-orient, u5-s-prepositional
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: ede02b152526cf5a07cfc8c7cbdbc4957a0864a6c773022609fed4fa84784130

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:письмо#sg_prep
originalSourceText=письме'
shippedText=письме́
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=77884e0d751cc976a26d9d39d52d968661a556130b99c48650fc660c19e2763f
contentHash=41b6f23f9b40569a9c1022bf4d32f0736c7866db20a0807b11dac031f777ca8c
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-prep-field-школа`

- Objectives: obj-u5-prepositional
- Sessions: u5-s-orient, u5-s-prepositional
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 50c8efbcc52c435808015d867bab7388df38f295ec8edb2c9359d98ff1d4c21d

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=nouns.csv:школа#sg_prep
originalSourceText=шко'ле
shippedText=шко́ле
transformation=formatting
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=fd76b305f3e1dc451e84bf8f76dad3eb07807a13cefb6c0dc965d60917a3d721
contentHash=b440ec597174f9579342efaf7385cdc444bee9cb7f841ac73acfc49f416824bf
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-prep-gloss-в`

- Objectives: obj-u5-prepositional
- Sessions: u5-s-orient, u5-s-prepositional
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: b9073a4cf2e589a40df7d4fb59190cece4f2a00f60a2b77cdf66fa8d226c6171

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/others.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=others.csv:в#translations_en
originalSourceText=in; at; into; to; for; on; within
shippedText=in
transformation=sense selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=b4504b1fffcbfafa12d2970f4fca3c32737e9ae522a47f8ae410a83d6e6765e0
contentHash=582967534d0f909d196b97f9e6921342777aea87b46fa52df165389db1fb8ccf
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-prep-gloss-на`

- Objectives: obj-u5-prepositional
- Sessions: u5-s-orient, u5-s-prepositional
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 2ec14135e714195b468d28e9ea2e1d87a20b8d4405f138bea9d5baf7344db7f5

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/others.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=others.csv:на#translations_en
originalSourceText=on (place); on (day/week); on (transport means); to; onto, upon; at
shippedText=on (place)
transformation=sense selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=418f7aa8d7a17d3d35e6139ddfaaac08551edbafd7309165685b3f42128e050e
contentHash=777f622ca730ff555825de05919e67cefed847becf4b7a8626325b511d6c4375
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-prep-gloss-о`

- Objectives: obj-u5-prepositional
- Sessions: u5-s-orient, u5-s-prepositional
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 6ab4d1677230d0db449bede3f822973f8dbc7a19153d2faedbb914614758e21a

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/others.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=others.csv:о#translations_en
originalSourceText=about; of; on; against
shippedText=about
transformation=sense selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=adb648bf0da552223c593fc1cbb857c5e1026d86428d68d11ff89627361ef7c9
contentHash=a4262e1c9bcbc1721eb3fe13558154460a2b2a2d307daa32532478526ce6ccb1
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-prep-na-kryshe`

- Objectives: obj-u5-prepositional
- Sessions: u5-s-orient, u5-s-prepositional
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: fdd2800b667dcf5e2203f74a8a9a1dd834f3509dc3140b5be07e3dbf2bc61893

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Prepositional_case?oldid=4567336
sourceRevision=4567336
sourceRecordId=prepositional:'He's on the roof', '''Он на крыше'''
originalSourceText='He's on the roof', '''Он на крыше'''
shippedText=Он на крыше
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=3d2cf11286cab776f55b542d2d459578f0c001d24df420e3a66d500cb4727c41
contentHash=37b3dbf209e1e112af62f5438c4d1261ff21c8e7f39f727e7e5e8c826417a580
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-prep-na-kryshe-en`

- Objectives: obj-u5-prepositional
- Sessions: u5-s-orient, u5-s-prepositional
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 70dccbd4fe1faf89e9f14e887b4e46ef70229b93c4fd067e6c801a1999b5ccd3

recordType=reference
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Prepositional_case?oldid=4567336
sourceRevision=4567336
sourceRecordId=prepositional:'He's on the roof', '''Он на крыше'''
originalSourceText='He's on the roof', '''Он на крыше'''
shippedText=He's on the roof
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=3d2cf11286cab776f55b542d2d459578f0c001d24df420e3a66d500cb4727c41
contentHash=a15574cdb5cb32c485992db56cda9c573ce12f0959ce8f9803ab80f5ba2e1b8b
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-prep-na-stene`

- Objectives: obj-u5-prepositional
- Sessions: u5-s-orient, u5-s-prepositional
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 5826ad0d67ac53016fa8307d1618344249b32a9e2b6018da33a7037191de7e3b

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Prepositions?oldid=4446858
sourceRevision=4446858
sourceRecordId=prepositions:Картина висит на стене. :The painting hangs on the wall
originalSourceText=Картина висит на стене. :The painting hangs on the wall
shippedText=Картина висит на стене.
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=4343c6f86da624484bb2604a54b5d64d65a6b5f99772d82335e9044733a2ad9e
contentHash=6c51d7ccaf2158f32936c10639355134fe15075bf3a3b3049a1f38765c86e422
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-prep-na-stene-en`

- Objectives: obj-u5-prepositional
- Sessions: u5-s-orient, u5-s-prepositional
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 6fbf5670cc2d86425190f25f3556e1340723ac858f7f33ab7c455b66c9c61a1f

recordType=reference
sourceUrl=https://en.wikibooks.org/wiki/Russian/Prepositions?oldid=4446858
sourceRevision=4446858
sourceRecordId=prepositions:Картина висит на стене. :The painting hangs on the wall
originalSourceText=Картина висит на стене. :The painting hangs on the wall
shippedText=The painting hangs on the wall
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=4343c6f86da624484bb2604a54b5d64d65a6b5f99772d82335e9044733a2ad9e
contentHash=897a2f76d88ca1ab279f076322930db8d83ce56f95cf00a2aec804f87d5f29ee
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-prep-na-stole`

- Objectives: obj-u5-prepositional
- Sessions: u5-s-orient, u5-s-prepositional
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 44476984e93193cf64781797a0abdd052e44fc1a952a9f929a271c72bc85779c

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Prepositions?oldid=4446858
sourceRevision=4446858
sourceRecordId=prepositions:Ваза стоит на столе. :The vase stands on the table
originalSourceText=Ваза стоит на столе. :The vase stands on the table
shippedText=Ваза стоит на столе.
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=c8c57768cd97ed45644c78754e138ed4ced9ec9e462b4802d97f56193a265560
contentHash=6cd5433605e389a037f72ffbf870cfa7100324d8993a01ea96c4859bf4a6ca73
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-prep-na-stole-en`

- Objectives: obj-u5-prepositional
- Sessions: u5-s-orient, u5-s-prepositional
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 46c15c10cd52e1897ac7373ecc573cddfdfefda40b924b4002a463e115c298d6

recordType=reference
sourceUrl=https://en.wikibooks.org/wiki/Russian/Prepositions?oldid=4446858
sourceRevision=4446858
sourceRecordId=prepositions:Ваза стоит на столе. :The vase stands on the table
originalSourceText=Ваза стоит на столе. :The vase stands on the table
shippedText=The vase stands on the table
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=c8c57768cd97ed45644c78754e138ed4ced9ec9e462b4802d97f56193a265560
contentHash=fe587dabe2b42fd6b9cee0e056431de05c3965500505e8e73c5d5fe9b20e3225
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-prep-o-knige`

- Objectives: obj-u5-prepositional
- Sessions: u5-s-orient, u5-s-prepositional
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: d24b751a4b399aced8247cdd4f3bb9b4103495a37b5e47fd81f54f652d45b02f

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Prepositional_case?oldid=4567336
sourceRevision=4567336
sourceRecordId=prepositional:About the book - '''Книга --> О книге'''
originalSourceText=About the book - '''Книга --> О книге'''
shippedText=О книге
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=f08adaf86354cff7f96cff281d22c359bf87b0d00965eae38db20e60bfd25adb
contentHash=97d650c686edfb7d5b7e3a81f0f42edfaa6c2ab863672fab5d07c616e39a0be1
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-prep-o-knige-en`

- Objectives: obj-u5-prepositional
- Sessions: u5-s-orient, u5-s-prepositional
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 3b13415c3864369aa58c64ce439b9039ef9eb709d0301bc94a0072e2994143a1

recordType=reference
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Prepositional_case?oldid=4567336
sourceRevision=4567336
sourceRecordId=prepositional:About the book - '''Книга --> О книге'''
originalSourceText=About the book - '''Книга --> О книге'''
shippedText=About the book
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=f08adaf86354cff7f96cff281d22c359bf87b0d00965eae38db20e60bfd25adb
contentHash=a1a991368c10aa846eede6cc30173a855cece3a041aa41ffba6fc958eb19aa22
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-prep-o-pisme`

- Objectives: obj-u5-prepositional
- Sessions: u5-s-orient, u5-s-prepositional
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 2a00118c68a5c26c9bc5e88570cc2ae8d7e24486c26b55cff04916354bf2388c

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Prepositional_case?oldid=4567336
sourceRevision=4567336
sourceRecordId=prepositional:About the letter - '''Письмо --> О письме'''
originalSourceText=About the letter - '''Письмо --> О письме'''
shippedText=О письме
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=b5ebada8465143758fe5cacfa6fc3c52c3b2a4adaaff9cc44d28579133725518
contentHash=9a23ca34126ac6c33bca99d6b904feb8b207d616127f396e5db4cd043f0153cf
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-prep-o-pisme-en`

- Objectives: obj-u5-prepositional
- Sessions: u5-s-orient, u5-s-prepositional
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 379c176f87e4891a59cac416e3738b5b4ba67cd1a4502534c051b239a024f91e

recordType=reference
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Prepositional_case?oldid=4567336
sourceRevision=4567336
sourceRecordId=prepositional:About the letter - '''Письмо --> О письме'''
originalSourceText=About the letter - '''Письмо --> О письме'''
shippedText=About the letter
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=b5ebada8465143758fe5cacfa6fc3c52c3b2a4adaaff9cc44d28579133725518
contentHash=08d979229567e9c684eae3d147ca696a709b92a64c98b7711ac16603c530a0fe
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-prep-o-sade`

- Objectives: obj-u5-prepositional
- Sessions: u5-s-orient, u5-s-prepositional
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 3ade39d2a7872c1bc9fadf65e60b965be69c721013a07f4179d733e1d6fe7f31

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Prepositional_case?oldid=4567336
sourceRevision=4567336
sourceRecordId=prepositional:About the garden - '''Сад --> О саде'''
originalSourceText=About the garden - '''Сад --> О саде'''
shippedText=О саде
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=883322015fba0967173a5e1b1fb6dfe8d079813f6766a6e08d0cc0edd016828a
contentHash=92711755008b67d37ba7e8f832d6949384d9602534f81b479ba9e77552f30b6a
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-prep-o-sade-en`

- Objectives: obj-u5-prepositional
- Sessions: u5-s-orient, u5-s-prepositional
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 611a510bf6da1393a3a339f758faf26f69fe7ccfc699e357aa0fc516a4c7e567

recordType=reference
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Prepositional_case?oldid=4567336
sourceRevision=4567336
sourceRecordId=prepositional:About the garden - '''Сад --> О саде'''
originalSourceText=About the garden - '''Сад --> О саде'''
shippedText=About the garden
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=883322015fba0967173a5e1b1fb6dfe8d079813f6766a6e08d0cc0edd016828a
contentHash=8bbfd18863ffe45a725599c65af1205c92fdf077a256f16c65066ce8aab5cac0
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-prep-v-moskve`

- Objectives: obj-u5-prepositional
- Sessions: u5-s-orient, u5-s-prepositional
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: e64156bb55daea6cf13226405be4b8de1e5dccbc95b57b2d67964cc1a228ed05

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Prepositional_case?oldid=4567336
sourceRevision=4567336
sourceRecordId=prepositional:'I'm in Moscow', '''я в Москве'''
originalSourceText='I'm in Moscow', '''я в Москве'''
shippedText=я в Москве
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=026c6124243ad6e2289509070616c0bc1d3da9bdebc6f9a48eb35bc84bcceae0
contentHash=c8c66dd653b73d631347c560409a0efc38d6f0d70cbf887ed7443d7d7ff9e215
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-prep-v-moskve-en`

- Objectives: obj-u5-prepositional
- Sessions: u5-s-orient, u5-s-prepositional
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 8e8dac3e1e0c55e293c852fd85cef1574d9839ada871dfa6d33202d855f96cf4

recordType=reference
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Prepositional_case?oldid=4567336
sourceRevision=4567336
sourceRecordId=prepositional:'I'm in Moscow', '''я в Москве'''
originalSourceText='I'm in Moscow', '''я в Москве'''
shippedText=I'm in Moscow
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=026c6124243ad6e2289509070616c0bc1d3da9bdebc6f9a48eb35bc84bcceae0
contentHash=26049ac2617068a95f500a1e18e754b45230db24668b0defacb2a79798ebccd5
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-prep-v-teatre`

- Objectives: obj-u5-prepositional
- Sessions: u5-s-orient, u5-s-prepositional
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: aa69f98f5b1817954d85be8bf33e8a8a960ef7c9e14c96a3df3c4a0cbb915f3e

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Prepositional_case?oldid=4567336
sourceRevision=4567336
sourceRecordId=prepositional:'He is at the theatre', use '''в''' + ''prep'', as he is ''in'' that particular 
originalSourceText='He is at the theatre', use '''в''' + ''prep'', as he is ''in'' that particular place: '''Он в театре'''
shippedText=Он в театре
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=021ba4d358ad00308b6ecd1e97c97fb9f50bc95ff7f9c1820bbd7bf515277e74
contentHash=e2e2269ce3884a4ec9266c7b02c77e92732b23664c25785575081713f9c3e388
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-prep-v-teatre-en`

- Objectives: obj-u5-prepositional
- Sessions: u5-s-orient, u5-s-prepositional
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 01d3f1f615f11b0ad6505e028f0ea903db19752627780fabecb9325aa49545ce

recordType=reference
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Prepositional_case?oldid=4567336
sourceRevision=4567336
sourceRecordId=prepositional:'He is at the theatre', use '''в''' + ''prep'', as he is ''in'' that particular 
originalSourceText='He is at the theatre', use '''в''' + ''prep'', as he is ''in'' that particular place: '''Он в театре'''
shippedText=He is at the theatre
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=021ba4d358ad00308b6ecd1e97c97fb9f50bc95ff7f9c1820bbd7bf515277e74
contentHash=f8cede0ece1856bd6eef54eb36525f03ce2ebf24a5a7e18e86d7ff38cbe17725
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-prep-word-в`

- Objectives: obj-u5-prepositional
- Sessions: u5-s-orient, u5-s-prepositional
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 3892085686983754a03d4023eab318e26a3daad591405f23410ab6cce3a1b998

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/others.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=others.csv:в#accented
originalSourceText=в
shippedText=в
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=b8012cb642c887a0a4f6f8e52fc6d97946274076ba64b113dff1db0a0ca37caa
contentHash=b8012cb642c887a0a4f6f8e52fc6d97946274076ba64b113dff1db0a0ca37caa
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-prep-word-на`

- Objectives: obj-u5-prepositional
- Sessions: u5-s-orient, u5-s-prepositional
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 190695aca31abeddcd6c840ab47d99fc6c9a232a185921b9bd3cea8934e728b0

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/others.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=others.csv:на#accented
originalSourceText=на
shippedText=на
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=284852b2c5506ed24045eed776295780b4aad9cb01cdb3edf4a6fdb46f268369
contentHash=284852b2c5506ed24045eed776295780b4aad9cb01cdb3edf4a6fdb46f268369
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-prep-word-о`

- Objectives: obj-u5-prepositional
- Sessions: u5-s-orient, u5-s-prepositional
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 9a2586ae2026250979f94319f6dd05e3fb927bae7d03193d454ac082c35a7977

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/blob/50e210c4803237779cb562bc1abcea529066031c/others.csv
sourceRevision=50e210c4803237779cb562bc1abcea529066031c
sourceRecordId=others.csv:о#accented
originalSourceText=о
shippedText=о
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=50338c005c41fb4e6e6b74c9f7f8d0f56ae08517102498639f8a610b06cd9859
contentHash=50338c005c41fb4e6e6b74c9f7f8d0f56ae08517102498639f8a610b06cd9859
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-q-chto`

- Objectives: obj-u5-questions
- Sessions: u5-s-orient, u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 3b1e403df35269583306d5cd68475bd2e0708dd739a365a6ecab35ec7f426399

recordType=lexical record
sourceUrl=https://en.wikibooks.org/wiki/Russian/Interrogative_Pronouns?oldid=4220316
sourceRevision=4220316
sourceRecordId=interrogative:| Что || ''što'' || What
originalSourceText=| Что || ''što'' || What
shippedText=Что
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=6f807da72b03735953c9ff5baaf646b337ff97f3959903457cc0d756c3f07dbb
contentHash=c1c487fabc76337616898f2557514e9402bd84b208c16933cf550a61dff0dc3d
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-q-chto-en`

- Objectives: obj-u5-questions
- Sessions: u5-s-orient, u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: a96b7e4c127bbc53c8eeca14c51de217eec6b192a918980ab72f7479a951a7e9

recordType=reference
sourceUrl=https://en.wikibooks.org/wiki/Russian/Interrogative_Pronouns?oldid=4220316
sourceRevision=4220316
sourceRecordId=interrogative:| Что || ''što'' || What
originalSourceText=| Что || ''što'' || What
shippedText=What
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=6f807da72b03735953c9ff5baaf646b337ff97f3959903457cc0d756c3f07dbb
contentHash=f8cf83a76a98df2dd4799b4d0d4f6ffc9af9a3a72d8648f94ca7cdea4b52fde7
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-q-gde`

- Objectives: obj-u5-questions
- Sessions: u5-s-orient, u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: cb2da84269b4b558c3032613a26e495e224bd359878e04505d82a1ab99cf0660

recordType=lexical record
sourceUrl=https://en.wikibooks.org/wiki/Russian/Interrogative_Pronouns?oldid=4220316
sourceRevision=4220316
sourceRecordId=interrogative:| Где || ''gde'' || Where (location)
originalSourceText=| Где || ''gde'' || Where (location)
shippedText=Где
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=e24e12342a58a6c2b28d35bc7342eae64d2baf200c5d06a96bf7ca24ebfbc3d6
contentHash=4a7ac81df69a39d265449d49c8943e6fbc14af1084399c606530dce1c776354e
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-q-gde-en`

- Objectives: obj-u5-questions
- Sessions: u5-s-orient, u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: e27c99e180fb60d4edb9da82af7f8447806a9cb75afd824092e7a72d047ff011

recordType=reference
sourceUrl=https://en.wikibooks.org/wiki/Russian/Interrogative_Pronouns?oldid=4220316
sourceRevision=4220316
sourceRecordId=interrogative:| Где || ''gde'' || Where (location)
originalSourceText=| Где || ''gde'' || Where (location)
shippedText=Where (location)
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=e24e12342a58a6c2b28d35bc7342eae64d2baf200c5d06a96bf7ca24ebfbc3d6
contentHash=242520732f5c9e940fe3de556c55040d51a489362b805efe059f7cb5c7ec4021
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-q-kak`

- Objectives: obj-u5-questions
- Sessions: u5-s-orient, u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: e7bb63de7103373ba1c465d9a6d5633687acbfd7f4acc3d367d1913580683c09

recordType=lexical record
sourceUrl=https://en.wikibooks.org/wiki/Russian/Interrogative_Pronouns?oldid=4220316
sourceRevision=4220316
sourceRecordId=interrogative:| Как || ''kak'' || How
originalSourceText=| Как || ''kak'' || How
shippedText=Как
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=6ee611286787fe6f97a52591d3f9e640ae595390ab8cfa726a2e6dc33e756690
contentHash=09d3fd9e7c1e5da3c273a6ef3a3d3e34f691589d43defa6e53cd7367d52fd217
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-q-kak-en`

- Objectives: obj-u5-questions
- Sessions: u5-s-orient, u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: f9312996d079bc452cb214bc4a689aea52db3fd149fc5e0b9f3d5e42154266b4

recordType=reference
sourceUrl=https://en.wikibooks.org/wiki/Russian/Interrogative_Pronouns?oldid=4220316
sourceRevision=4220316
sourceRecordId=interrogative:| Как || ''kak'' || How
originalSourceText=| Как || ''kak'' || How
shippedText=How
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=6ee611286787fe6f97a52591d3f9e640ae595390ab8cfa726a2e6dc33e756690
contentHash=7470bd3bc2abfe497af32ee4ccf845cb2aba14878d55c970d38d99629ab5d6a3
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-q-kakoy`

- Objectives: obj-u5-questions
- Sessions: u5-s-orient, u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 205a11f34db350c306341d63f82fbaf153a29d0b74f7cba87041dbeb14515814

recordType=lexical record
sourceUrl=https://en.wikibooks.org/wiki/Russian/Interrogative_Pronouns?oldid=4220316
sourceRevision=4220316
sourceRecordId=interrogative:| Какой || ''kakój'' || What kind
originalSourceText=| Какой || ''kakój'' || What kind
shippedText=Какой
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=293ab733425cc2da1124d7a6eed7fec1954b48a4fb3bfcc26da194120ef34a99
contentHash=20761b5f4bc4db04052219307259915086317785c191c1a7fac0e02c88baafd1
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-q-kakoy-en`

- Objectives: obj-u5-questions
- Sessions: u5-s-orient, u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: a639a5e3da8f95368528ccedac1ef804290c2e2cd74b1ecb2492f2117ac6144f

recordType=reference
sourceUrl=https://en.wikibooks.org/wiki/Russian/Interrogative_Pronouns?oldid=4220316
sourceRevision=4220316
sourceRecordId=interrogative:| Какой || ''kakój'' || What kind
originalSourceText=| Какой || ''kakój'' || What kind
shippedText=What kind
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=293ab733425cc2da1124d7a6eed7fec1954b48a4fb3bfcc26da194120ef34a99
contentHash=24d5c725beebc37853072232d3865df1fb623bd60674105ae566f7c31f919680
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-q-kogda`

- Objectives: obj-u5-questions
- Sessions: u5-s-orient, u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 4e9162e92051b67ee8454229c4dbe1a329bc87249771154c19624a905c9f5e7e

recordType=lexical record
sourceUrl=https://en.wikibooks.org/wiki/Russian/Interrogative_Pronouns?oldid=4220316
sourceRevision=4220316
sourceRecordId=interrogative:| Когда || ''kogdá'' || When
originalSourceText=| Когда || ''kogdá'' || When
shippedText=Когда
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=66b799f9d109aeb0cf53f6947802a610b381188acc3eb3e76eb7961dbb64cafa
contentHash=c8f6109b240e7599565725df849247a72b61a94c8ee5d5daadef9a4a6f208409
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-q-kogda-en`

- Objectives: obj-u5-questions
- Sessions: u5-s-orient, u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 44a067fd4f74bf579c776ad8c032fe9f1ece305d61064b6b9ac68970deffa433

recordType=reference
sourceUrl=https://en.wikibooks.org/wiki/Russian/Interrogative_Pronouns?oldid=4220316
sourceRevision=4220316
sourceRecordId=interrogative:| Когда || ''kogdá'' || When
originalSourceText=| Когда || ''kogdá'' || When
shippedText=When
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=66b799f9d109aeb0cf53f6947802a610b381188acc3eb3e76eb7961dbb64cafa
contentHash=cf9c7aa24a26aac4f0ec4b6395cbfdcc055677c3dac87bd6af898bccd66d4e17
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-q-kto`

- Objectives: obj-u5-questions
- Sessions: u5-s-orient, u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: ee9c7a23d71e6a4f5e3360a237508d5f8481ff83dc2c211617a187b308d769d9

recordType=lexical record
sourceUrl=https://en.wikibooks.org/wiki/Russian/Interrogative_Pronouns?oldid=4220316
sourceRevision=4220316
sourceRecordId=interrogative:| Кто || ''kto'' || Who
originalSourceText=| Кто || ''kto'' || Who
shippedText=Кто
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=360bd51b1ced818ea1eb47bea5e42ee03ed9172c1bf9952b8dd59340b2b791d0
contentHash=6665414a26a757631aae619254a79d053f99a8cd36d05594cc744d96b5efa67d
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-q-kto-en`

- Objectives: obj-u5-questions
- Sessions: u5-s-orient, u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: b1451f0e562b2afe0f79a624ab09ad6858bdc4e052d293eb8098bb681434b32c

recordType=reference
sourceUrl=https://en.wikibooks.org/wiki/Russian/Interrogative_Pronouns?oldid=4220316
sourceRevision=4220316
sourceRecordId=interrogative:| Кто || ''kto'' || Who
originalSourceText=| Кто || ''kto'' || Who
shippedText=Who
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=360bd51b1ced818ea1eb47bea5e42ee03ed9172c1bf9952b8dd59340b2b791d0
contentHash=c17b94b8c9394d40151ba4078f384f0bfe77936d258bc2029bd8b857685cd3f1
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-q-kuda`

- Objectives: obj-u5-questions
- Sessions: u5-s-orient, u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 97e9ec331f92b13b7bbe6412241d9f4d6ceda02cc7cba6026fa52509db429520

recordType=lexical record
sourceUrl=https://en.wikibooks.org/wiki/Russian/Interrogative_Pronouns?oldid=4220316
sourceRevision=4220316
sourceRecordId=interrogative:| Куда || ''kudá'' || Whither, where to
originalSourceText=| Куда || ''kudá'' || Whither, where to
shippedText=Куда
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=f7d710a625246ebb318ed882017340a09f515e643a36f8f3ce567df7e4f6fd18
contentHash=56afd121c817496ccf2e6c04dd3bf4f53ebbec06208306f62a19dfc114931eb7
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-q-kuda-en`

- Objectives: obj-u5-questions
- Sessions: u5-s-orient, u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 46807ab18baf6c8d716da1aa0d235bd000c158716b21215b4316ac8df59f0aa5

recordType=reference
sourceUrl=https://en.wikibooks.org/wiki/Russian/Interrogative_Pronouns?oldid=4220316
sourceRevision=4220316
sourceRecordId=interrogative:| Куда || ''kudá'' || Whither, where to
originalSourceText=| Куда || ''kudá'' || Whither, where to
shippedText=Whither, where to
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=f7d710a625246ebb318ed882017340a09f515e643a36f8f3ce567df7e4f6fd18
contentHash=ead08fa69868708e42b45c9945d487b6d98c1b272d13946bca85f6bd7c053d75
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-q-otkuda`

- Objectives: obj-u5-questions
- Sessions: u5-s-orient, u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: c63dd96b69fcbc2406d3fbe00343dea09c7ed123f9ca55ecc4f1c6f5263de191

recordType=lexical record
sourceUrl=https://en.wikibooks.org/wiki/Russian/Interrogative_Pronouns?oldid=4220316
sourceRevision=4220316
sourceRecordId=interrogative:| Откуда || ''otkúda'' || Whence, where from
originalSourceText=| Откуда || ''otkúda'' || Whence, where from
shippedText=Откуда
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=7114d865a5686b457e5449edb6ff7c86b0b94b41496f91d29c65118811d641fe
contentHash=ba6e2b3cf755fa77dec0c997cc7f7501523d6ade3df1ccfdbc9515963b3ac8ce
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-q-otkuda-en`

- Objectives: obj-u5-questions
- Sessions: u5-s-orient, u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: af21943b1a0bf015e23d4c254140a2eda9f74a8d37f7ec4933088d9c3f8c1aa1

recordType=reference
sourceUrl=https://en.wikibooks.org/wiki/Russian/Interrogative_Pronouns?oldid=4220316
sourceRevision=4220316
sourceRecordId=interrogative:| Откуда || ''otkúda'' || Whence, where from
originalSourceText=| Откуда || ''otkúda'' || Whence, where from
shippedText=Whence, where from
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=7114d865a5686b457e5449edb6ff7c86b0b94b41496f91d29c65118811d641fe
contentHash=128377c90dd81c703d4172d37ff58ce1365e0d7132ad41ba0a6b99e29eb54022
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-q-pochemu`

- Objectives: obj-u5-questions
- Sessions: u5-s-orient, u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: ce02b95ef67497622c7bda6e1b3a9103233aeb9d483cade10a4616f3b74bf4bc

recordType=lexical record
sourceUrl=https://en.wikibooks.org/wiki/Russian/Interrogative_Pronouns?oldid=4220316
sourceRevision=4220316
sourceRecordId=interrogative:| Почему || ''počemú'' || Why
originalSourceText=| Почему || ''počemú'' || Why
shippedText=Почему
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=cdbe87eef1ea9e816de149a9a4e3f39b3df5112f136757097699533dd68b1b68
contentHash=e4c2a66024326631d4da16e3440eb0575beade15ab7c2b2f94ab25cb7315a4ab
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-q-pochemu-en`

- Objectives: obj-u5-questions
- Sessions: u5-s-orient, u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 087f21ed01cbc3237a3e58b2d218d0963ea86ebec3ba3c4e82b3073f4c1ff7d9

recordType=reference
sourceUrl=https://en.wikibooks.org/wiki/Russian/Interrogative_Pronouns?oldid=4220316
sourceRevision=4220316
sourceRecordId=interrogative:| Почему || ''počemú'' || Why
originalSourceText=| Почему || ''počemú'' || Why
shippedText=Why
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=cdbe87eef1ea9e816de149a9a4e3f39b3df5112f136757097699533dd68b1b68
contentHash=d3ae14a50bfe463f804c73c9fd16e88c36bf5a0bf4a56f98ae696a91eb2de149
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-q-skolko`

- Objectives: obj-u5-questions
- Sessions: u5-s-orient, u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 49033e735be4dff586c9eb559774bd054995a8b5291edb486f372ec3809d3f58

recordType=lexical record
sourceUrl=https://en.wikibooks.org/wiki/Russian/Interrogative_Pronouns?oldid=4220316
sourceRevision=4220316
sourceRecordId=interrogative:| Сколько || ''skól'ko'' || How much, how many
originalSourceText=| Сколько || ''skól'ko'' || How much, how many
shippedText=Сколько
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=dec345b0d9a628246aba0f19b7274ec48e9b4a3c09b1c14a2b842163b46ef693
contentHash=51f644a4ece250d6639d11ae465465e37ccd3875f94d29d4fcfc8f138ac277fb
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-q-skolko-en`

- Objectives: obj-u5-questions
- Sessions: u5-s-orient, u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 887d5dd5489788327041270c17d3136de21252fdb9b13ab1e1063b348d32d5cd

recordType=reference
sourceUrl=https://en.wikibooks.org/wiki/Russian/Interrogative_Pronouns?oldid=4220316
sourceRevision=4220316
sourceRecordId=interrogative:| Сколько || ''skól'ko'' || How much, how many
originalSourceText=| Сколько || ''skól'ko'' || How much, how many
shippedText=How much, how many
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=dec345b0d9a628246aba0f19b7274ec48e9b4a3c09b1c14a2b842163b46ef693
contentHash=9da2a763f2d394b4fe595fa9cc15c43b8d4259df5d36bc9be9829e9f3d9bb90f
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### AUTHORED_ENGLISH `en-u5-098`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: dd1cdb078ee8cc7a17e1e6c06150feb27b2f3a436a1a5fb7ff60846d96482ee7

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Which admitted question word is recorded for this English purpose: 

### AUTHORED_ENGLISH `en-u5-099`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 9fe6f4180a550d1acebc911fef96f5c9dd24935ba3d7f2f599ca729375f1d34e

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=The frozen table records 

### AUTHORED_ENGLISH `en-u5-100`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 0511f461189efeaad18b402bb1a2fac5ef10d586c19a29974fbcb77d595ce759

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text= for 

### AUTHORED_ENGLISH `en-u5-101`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: bf1993a802412d5627c91ed76166c058ff2ce02d7408f382c828191fa090ab7e

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=.

### AUTHORED_ENGLISH `en-u5-102`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 271964bfca592dcabbd03a40596c56f8c62639bf74d40052ac565a9581b8c0ba

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Which admitted meaning is recorded for the shown written question?

### AUTHORED_ENGLISH `en-u5-103`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 0fa6a6107f3fbfb40ebfe3e210b5dcea192a9e2cb12c25718552532c84814dc0

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=The frozen page records this meaning: 

### AUTHORED_ENGLISH `en-u5-104`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 845ffe6425155fe41911b49f9f2428e446c6f6f332aa4b2119bad50abecab600

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=. Written punctuation is not listening practice.

### AUTHORED_ENGLISH `en-u5-105`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 38f384464bfbfe24cd9dde2ba1e9b5bf135817eb0a5900b14d55c4a68e5ac7e5

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Which admitted description is recorded for the shown example?

### AUTHORED_ENGLISH `en-u5-106`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: e223649a2fb558b6995701ed14f0b35a6937a9dc71a6a202d2cca87feb59968c

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=The frozen page records this example as: 

### AUTHORED_ENGLISH `en-u5-107`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 90cd0002e9a8ca6383b64711e43139d32ff9e4758a9c27c3813e56e96f714d87

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Which string is the admitted second field, not the first field?

### AUTHORED_ENGLISH `en-u5-108`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 026fbe7c40cc772adc00892a2adcc7d9c7be941535f0416d1991c30e917bbc49

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=The frozen row lists that field as 

### AUTHORED_ENGLISH `en-u5-109`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 17e44f646627aa82634992327ea3fe71b39d8c963bd256bc23635ac91d772b21

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Which flag does the admitted row record?

### AUTHORED_ENGLISH `en-u5-110`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: dc3111776fc351a77da8235c07052ddd54803f4fa4c5f97a7ad2fb3cf1be2225

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=The frozen row sets the flag to 

### AUTHORED_ENGLISH `en-u5-111`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: f708b200253f2b724b3b3990983efe21df2fb29fd69558be71822fb2b6215f4a

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=These words are copied from the frozen table. 

### AUTHORED_ENGLISH `en-u5-112`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 53135c3d9e9e09709d93616b92171388a59a3af0d393d6bae3e075f3230f9ca6

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Four question words

### AUTHORED_ENGLISH `en-u5-113`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 98cbd6dbc3a31b5767d1de923ee7df7bb3f64621a83bf6b6006a9bf895b77ee3

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Three more question words

### AUTHORED_ENGLISH `en-u5-114`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 4a8b1503127bba7659c30d275e4f24fa0c2f371fa7ef29ee7090a616ebb78bd2

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Three further question words

### AUTHORED_ENGLISH `en-u5-115`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 36e68f422a237dbd877f463cc8bf6d61da6989a3674233289aedf8efce82fb8e

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=These written questions are exact frozen excerpts. 

### AUTHORED_ENGLISH `en-u5-116`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 02a2f808b523ca81fdcbdb1bc3e978384b042d7e90dcd0e3f553516459471d4a

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Two written questions

### AUTHORED_ENGLISH `en-u5-117`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: a802c6a345fa3037fac242f00a54c295edb48b011dda07552c9d186e46a08197

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Two more written questions

### AUTHORED_ENGLISH `en-u5-118`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 17fb7e466ac876f4cb2d77f0e521e0a7e888546b9fa3cdd91168173634fa7277

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=These examples are exact frozen excerpts. 

### AUTHORED_ENGLISH `en-u5-119`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 6cdba4090adceaf2ede7cf7ec87e24622221bd1ce70dae5f5b338143a06a3e25

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Two about-examples

### AUTHORED_ENGLISH `en-u5-120`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 529ac4a78db2e253d23c86ce81f88620e32d818ad74ddfb0a4188b159f602ad9

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Two more about-examples and a place example

### AUTHORED_ENGLISH `en-u5-121`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: b0fab8781585c10f0099a096443c4929ac3b940ba49bab1c378c3e50f37257c2

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Two place examples

### AUTHORED_ENGLISH `en-u5-122`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 3aa141849c34733f2589b06b20c0aeab0aaa98b3b0dabcd7cf702aa4b4ddb017

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Two more place examples

### AUTHORED_ENGLISH `en-u5-123`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: e0b3bfc52590d7c12b2e4295cc44747350918f7d3d45c5f9ee96db2979400640

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Three admitted function words

### AUTHORED_ENGLISH `en-u5-124`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 65d558d6bda5c63bca8af12238301c6092c5ae6ae5ac39d64bb56a622c507efc

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Use these rows only as quoted. Do not build a new phrase from them. 

### AUTHORED_ENGLISH `en-u5-125`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 41abbc69722fdc524fcf73f8e869e540321980a695fa818bc26127cafb3ad477

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Two admitted field pairs

### AUTHORED_ENGLISH `en-u5-126`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: b24a718a5f31ccd286b2d104e5b60403107a00bff14e82757a11e352dbf59353

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Each pair is the first field, then the second field, from the same admitted row. 

### AUTHORED_ENGLISH `en-u5-127`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 98816dcd57894afa8eb880ddf387fdfe3a4dde1541463b1dde279ab690735245

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=What the frozen page says about words that do not change

### AUTHORED_ENGLISH `en-u5-128`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: c17ab861bfb7755ad3933909872924076a10314df91019e3590ab2bcd4155668

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=The frozen page says 

### AUTHORED_ENGLISH `en-u5-129`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 98d71009d55a1702ec330234a1168fc9b40c1050e9036e3f0638ba139e493d91

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=. 

### AUTHORED_ENGLISH `en-u5-130`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 06fa49ccbabeaacc974a7d88ccd9df53c0e706e5d84d3dcfc7cc8f4136a29c57

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=The admitted rows below have the flag set. That flag is the record used here. 

### AUTHORED_ENGLISH `en-u5-131`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: e1883ca355b4ea1b1df6305bdf1d92956944cb21239c8fe06ca933187deb769c

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Four admitted rows with the flag set

### AUTHORED_ENGLISH `en-u5-132`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: ca3181fb5ab8716faf24ed909aa472736db0d351b037affc97d495d4c917d87d

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=The admitted rows below have the flag set. 

### AUTHORED_ENGLISH `en-u5-133`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 0c310c850c28af0d7d84651ec2e6a530866ee66d39b8f9c18d7bdfc28d6bef09

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Two more admitted rows with the flag set

### AUTHORED_ENGLISH `en-u5-134`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 10ba89f75fc278bff70fad0d9e590d732c158c66026b681ecc2e99382bed55ca

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Questions, place examples, and words that do not change form

### AUTHORED_ENGLISH `en-u5-135`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: f610419fb5daac4b5a4e52f1f0bc8088218a62653ec4410db4d7af57ec5d76e2

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Proposed Unit 5 uses frozen grammar excerpts and dictionary rows for questions, place examples, and words that do not change form. It does not teach question intonation and it does not supply audio. The owned book remains separate work. Qualified review is pending.

### AUTHORED_ENGLISH `en-u5-136`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 32e24925a49ce0d787bb33c3582ce902148d7447137f62e1adbf0ea050c8f992

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Open Lesson 5 in the copy you own. The app records do not replace that lesson.

### AUTHORED_ENGLISH `en-u5-137`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 1b61379a78b99967f4b2dbe8c9f6029569c0a3c1a4eead1e3c4f5fc3b1d4b8d8

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Guided practice

### AUTHORED_ENGLISH `en-u5-138`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: ba788c2306d8bba9d9124b6ea62618898f1d2f90afa00c61b028191d75c5d592

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Choose the admitted record. The feedback states the frozen field.

### AUTHORED_ENGLISH `en-u5-139`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 0fc6f39e006d3efb1b4ddf63d59e2baee68584583763c02e13125938bd4cbacc

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Remediation

### AUTHORED_ENGLISH `en-u5-140`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: bb366d7cbddd8df5eb759c78f1cbb056591e4954ebc00ba9da7f0bae98f97aa2

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Return to the explanation. Compare the shown form with the admitted record. Do not invent a form that is absent from the record.

### AUTHORED_ENGLISH `en-u5-141`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 5a40fe261642c7c522d0c994aa51f5a09434901798f82c0999f13b627867c936

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Independent checks

### AUTHORED_ENGLISH `en-u5-142`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 06a46d0d470f16c05aa6c785b29ca24324344d221d4609a5790c68371769d81e

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=These checks use only admitted records. A miss returns to the explanation.

### AUTHORED_ENGLISH `en-u5-143`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 7b00cc14e2318429676b8ef3e477f071d7cf0ff088878499f1c65d0f09843a90

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Work Lesson 5 in your copy

### AUTHORED_ENGLISH `en-u5-144`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 8e7a80096e78fcd8239d9cba97469d92e141e93aec7a227f739c029d51276bb4

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Do Lesson 5 in the copy you own. Use that book's explanations and practice. The proposed records in this unit are a separate licensed set and do not copy the book.

### AUTHORED_ENGLISH `en-u5-145`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 749f94a0c75fdf7ef48b2ec6b9f2c2f70992e7dbea4687cc274fa9c10dca409a

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Open Lesson 5 in your copy and complete its lesson work.

### AUTHORED_ENGLISH `en-u5-146`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 9b4e69f0a0944f5acf75e4c8f6c310ff4fca8f96761b2eb2229bcb8ebbfad314

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Record Lesson 5 book work

### AUTHORED_ENGLISH `en-u5-147`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: b1fc0683e87a1b292db89745f90e0626920f6158f5e1a727916000c839b4bafa

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=When you have done Lesson 5 in your copy, self-attest here. Attestation records your report. It does not grade the lesson, approve a mapping, or approve the proposed records.

### AUTHORED_ENGLISH `en-u5-148`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 947f196915ada967246bdb3cbf7c360bf1796a08d9295bc4d1ebba2422687ef9

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Self-attest only after the Lesson 5 work in your copy.

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

- `obj-u5-questions` INSTRUCTION `u5-st-orient`
- `obj-u5-questions` INSTRUCTION `u5-st-teach-questions-1`
- `obj-u5-questions` INSTRUCTION `u5-st-teach-questions-2`
- `obj-u5-questions` INSTRUCTION `u5-st-teach-questions-3`
- `obj-u5-questions` INSTRUCTION `u5-st-teach-questions-4`
- `obj-u5-questions` INSTRUCTION `u5-st-teach-questions-5`
- `obj-u5-questions` EXAMPLE `ex-u5-q-kto`
- `obj-u5-questions` EXAMPLE `ex-u5-q-chto`
- `obj-u5-questions` EXAMPLE `ex-u5-q-kogda`
- `obj-u5-questions` EXAMPLE `ex-u5-q-pochemu`
- `obj-u5-questions` EXAMPLE `ex-u5-q-kak`
- `obj-u5-questions` EXAMPLE `ex-u5-q-gde`
- `obj-u5-questions` EXAMPLE `ex-u5-q-kuda`
- `obj-u5-questions` EXAMPLE `ex-u5-q-otkuda`
- `obj-u5-questions` EXAMPLE `ex-u5-q-skolko`
- `obj-u5-questions` EXAMPLE `ex-u5-q-kakoy`
- `obj-u5-questions` EXAMPLE `ex-u5-pattern-pattern-chto-eto`
- `obj-u5-questions` EXAMPLE `ex-u5-pattern-pattern-kto-eto`
- `obj-u5-questions` EXAMPLE `ex-u5-pattern-pattern-kak-dela`
- `obj-u5-questions` EXAMPLE `ex-u5-pattern-pattern-gde-zhivete`
- `obj-u5-questions` PRACTICE `u5-st-guided-questions-1`
- `obj-u5-questions` PRACTICE `u5-st-guided-questions-4`
- `obj-u5-questions` CHECK `u5-i-q-kogda`
- `obj-u5-questions` CHECK `u5-i-q-pochemu`
- `obj-u5-questions` CHECK `u5-i-q-kak`
- `obj-u5-questions` CHECK `u5-i-q-gde`
- `obj-u5-questions` CHECK `u5-i-pattern-pattern-kto-eto`
- `obj-u5-questions` CHECK `u5-i-pattern-pattern-kak-dela`
- `obj-u5-questions` CHECK `u5-i-pattern-pattern-gde-zhivete`
- `obj-u5-questions` REMEDIATION `u5-st-rem-questions`
- `obj-u5-prepositional` INSTRUCTION `u5-st-orient`
- `obj-u5-prepositional` INSTRUCTION `u5-st-teach-prepositional-1`
- `obj-u5-prepositional` INSTRUCTION `u5-st-teach-prepositional-2`
- `obj-u5-prepositional` INSTRUCTION `u5-st-teach-prepositional-3`
- `obj-u5-prepositional` INSTRUCTION `u5-st-teach-prepositional-4`
- `obj-u5-prepositional` INSTRUCTION `u5-st-teach-prepositional-5`
- `obj-u5-prepositional` INSTRUCTION `u5-st-teach-prepositional-6`
- `obj-u5-prepositional` EXAMPLE `ex-u5-prep-o-knige`
- `obj-u5-prepositional` EXAMPLE `ex-u5-prep-o-pisme`
- `obj-u5-prepositional` EXAMPLE `ex-u5-prep-o-sade`
- `obj-u5-prepositional` EXAMPLE `ex-u5-prep-v-moskve`
- `obj-u5-prepositional` EXAMPLE `ex-u5-prep-na-kryshe`
- `obj-u5-prepositional` EXAMPLE `ex-u5-prep-v-teatre`
- `obj-u5-prepositional` EXAMPLE `ex-u5-prep-na-stole`
- `obj-u5-prepositional` EXAMPLE `ex-u5-prep-na-stene`
- `obj-u5-prepositional` PRACTICE `u5-st-guided-prepositional-1`
- `obj-u5-prepositional` PRACTICE `u5-st-guided-prepositional-6`
- `obj-u5-prepositional` CHECK `u5-i-prep-o-sade`
- `obj-u5-prepositional` CHECK `u5-i-prep-v-moskve`
- `obj-u5-prepositional` REMEDIATION `u5-st-rem-prepositional`
- `obj-u5-indeclinable` INSTRUCTION `u5-st-orient`
- `obj-u5-indeclinable` INSTRUCTION `u5-st-teach-indeclinable-1`
- `obj-u5-indeclinable` INSTRUCTION `u5-st-teach-indeclinable-2`
- `obj-u5-indeclinable` INSTRUCTION `u5-st-teach-indeclinable-3`
- `obj-u5-indeclinable` EXAMPLE `ex-u5-indecl-радио`
- `obj-u5-indeclinable` EXAMPLE `ex-u5-indecl-такси`
- `obj-u5-indeclinable` EXAMPLE `ex-u5-indecl-метро`
- `obj-u5-indeclinable` EXAMPLE `ex-u5-indecl-кино`
- `obj-u5-indeclinable` EXAMPLE `ex-u5-indecl-пальто`
- `obj-u5-indeclinable` EXAMPLE `ex-u5-indecl-кофе`
- `obj-u5-indeclinable` EXAMPLE `ex-u5-indecl-кафе`
- `obj-u5-indeclinable` EXAMPLE `ex-u5-indecl-меню`
- `obj-u5-indeclinable` PRACTICE `u5-st-guided-indeclinable-1`
- `obj-u5-indeclinable` CHECK `u5-i-indecl-такси`
- `obj-u5-indeclinable` CHECK `u5-i-indecl-метро`
- `obj-u5-indeclinable` CHECK `u5-i-indecl-кино`
- `obj-u5-indeclinable` CHECK `u5-i-decl-книга`
- `obj-u5-indeclinable` CHECK `u5-i-decl-окно`
- `obj-u5-indeclinable` REMEDIATION `u5-st-rem-indeclinable`
- `obj-u5-book` INSTRUCTION `u5-st-book-work`
- `obj-u5-book` INSTRUCTION `u5-st-book`
- `u5-s-orient` OBJECTIVE `obj-u5-questions`
- `u5-s-orient` OBJECTIVE `obj-u5-prepositional`
- `u5-s-orient` OBJECTIVE `obj-u5-indeclinable`
- `u5-s-orient` OBJECTIVE `obj-u5-book`
- `u5-st-orient` REFERENCE `ref-u35-attribution`
- `u5-s-questions` OBJECTIVE `obj-u5-questions`
- `u5-st-teach-questions-1` EXAMPLE `ex-u5-q-kto`
- `u5-st-teach-questions-1` EXAMPLE `ex-u5-q-chto`
- `u5-st-teach-questions-1` EXAMPLE `ex-u5-q-kogda`
- `u5-st-teach-questions-1` EXAMPLE `ex-u5-q-pochemu`
- `u5-st-teach-questions-1` REFERENCE `ref-u35-attribution`
- `u5-st-guided-questions-1` REMEDIATION `u5-st-rem-questions`
- `u5-st-guided-questions-1` ITEM `u5-i-q-kto`
- `u5-st-guided-questions-1` ITEM `u5-i-q-chto`
- `u5-st-teach-questions-2` EXAMPLE `ex-u5-q-kak`
- `u5-st-teach-questions-2` EXAMPLE `ex-u5-q-gde`
- `u5-st-teach-questions-2` EXAMPLE `ex-u5-q-kuda`
- `u5-st-teach-questions-2` REFERENCE `ref-u35-attribution`
- `u5-st-teach-questions-3` EXAMPLE `ex-u5-q-otkuda`
- `u5-st-teach-questions-3` EXAMPLE `ex-u5-q-skolko`
- `u5-st-teach-questions-3` EXAMPLE `ex-u5-q-kakoy`
- `u5-st-teach-questions-3` REFERENCE `ref-u35-attribution`
- `u5-st-teach-questions-4` EXAMPLE `ex-u5-pattern-pattern-chto-eto`
- `u5-st-teach-questions-4` EXAMPLE `ex-u5-pattern-pattern-kto-eto`
- `u5-st-teach-questions-4` REFERENCE `ref-u35-attribution`
- `u5-st-guided-questions-4` REMEDIATION `u5-st-rem-questions`
- `u5-st-guided-questions-4` ITEM `u5-i-pattern-pattern-chto-eto`
- `u5-st-teach-questions-5` EXAMPLE `ex-u5-pattern-pattern-kak-dela`
- `u5-st-teach-questions-5` EXAMPLE `ex-u5-pattern-pattern-gde-zhivete`
- `u5-st-teach-questions-5` REFERENCE `ref-u35-attribution`
- `u5-st-rem-questions` EXAMPLE `ex-u5-q-kto`
- `u5-st-rem-questions` EXAMPLE `ex-u5-q-chto`
- `u5-st-check-questions` REMEDIATION `u5-st-rem-questions`
- `u5-st-check-questions` ITEM `u5-i-q-kogda`
- `u5-st-check-questions` ITEM `u5-i-q-pochemu`
- `u5-st-check-questions` ITEM `u5-i-q-kak`
- `u5-st-check-questions` ITEM `u5-i-q-gde`
- `u5-st-check-questions` ITEM `u5-i-pattern-pattern-kto-eto`
- `u5-st-check-questions` ITEM `u5-i-pattern-pattern-kak-dela`
- `u5-st-check-questions` ITEM `u5-i-pattern-pattern-gde-zhivete`
- `u5-s-prepositional` OBJECTIVE `obj-u5-prepositional`
- `u5-st-teach-prepositional-1` EXAMPLE `ex-u5-prep-o-knige`
- `u5-st-teach-prepositional-1` EXAMPLE `ex-u5-prep-o-pisme`
- `u5-st-teach-prepositional-1` REFERENCE `ref-u35-attribution`
- `u5-st-guided-prepositional-1` REMEDIATION `u5-st-rem-prepositional`
- `u5-st-guided-prepositional-1` ITEM `u5-i-prep-o-knige`
- `u5-st-guided-prepositional-1` ITEM `u5-i-prep-o-pisme`
- `u5-st-teach-prepositional-2` EXAMPLE `ex-u5-prep-o-sade`
- `u5-st-teach-prepositional-2` EXAMPLE `ex-u5-prep-v-moskve`
- `u5-st-teach-prepositional-2` REFERENCE `ref-u35-attribution`
- `u5-st-teach-prepositional-3` EXAMPLE `ex-u5-prep-na-kryshe`
- `u5-st-teach-prepositional-3` EXAMPLE `ex-u5-prep-v-teatre`
- `u5-st-teach-prepositional-3` REFERENCE `ref-u35-attribution`
- `u5-st-teach-prepositional-4` EXAMPLE `ex-u5-prep-na-stole`
- `u5-st-teach-prepositional-4` EXAMPLE `ex-u5-prep-na-stene`
- `u5-st-teach-prepositional-4` REFERENCE `ref-u35-attribution`
- `u5-st-teach-prepositional-5` REFERENCE `ref-u35-attribution`
- `u5-st-teach-prepositional-6` REFERENCE `ref-u35-attribution`
- `u5-st-guided-prepositional-6` REMEDIATION `u5-st-rem-prepositional`
- `u5-st-guided-prepositional-6` ITEM `u5-i-prep-field-книга`
- `u5-st-guided-prepositional-6` ITEM `u5-i-prep-field-письмо`
- `u5-st-rem-prepositional` EXAMPLE `ex-u5-prep-o-knige`
- `u5-st-rem-prepositional` EXAMPLE `ex-u5-prep-o-pisme`
- `u5-st-check-prepositional` REMEDIATION `u5-st-rem-prepositional`
- `u5-st-check-prepositional` ITEM `u5-i-prep-o-sade`
- `u5-st-check-prepositional` ITEM `u5-i-prep-v-moskve`
- `u5-s-indeclinable` OBJECTIVE `obj-u5-indeclinable`
- `u5-st-teach-indeclinable-1` EXAMPLE `ex-u5-indecl-радио`
- `u5-st-teach-indeclinable-1` EXAMPLE `ex-u5-indecl-такси`
- `u5-st-teach-indeclinable-1` REFERENCE `ref-u35-attribution`
- `u5-st-guided-indeclinable-1` REMEDIATION `u5-st-rem-indeclinable`
- `u5-st-guided-indeclinable-1` ITEM `u5-i-indecl-радио`
- `u5-st-teach-indeclinable-2` EXAMPLE `ex-u5-indecl-метро`
- `u5-st-teach-indeclinable-2` EXAMPLE `ex-u5-indecl-кино`
- `u5-st-teach-indeclinable-2` EXAMPLE `ex-u5-indecl-пальто`
- `u5-st-teach-indeclinable-2` EXAMPLE `ex-u5-indecl-кофе`
- `u5-st-teach-indeclinable-2` REFERENCE `ref-u35-attribution`
- `u5-st-teach-indeclinable-3` EXAMPLE `ex-u5-indecl-кафе`
- `u5-st-teach-indeclinable-3` EXAMPLE `ex-u5-indecl-меню`
- `u5-st-teach-indeclinable-3` REFERENCE `ref-u35-attribution`
- `u5-st-rem-indeclinable` EXAMPLE `ex-u5-indecl-радио`
- `u5-st-rem-indeclinable` EXAMPLE `ex-u5-indecl-такси`
- `u5-st-check-indeclinable` REMEDIATION `u5-st-rem-indeclinable`
- `u5-st-check-indeclinable` ITEM `u5-i-indecl-такси`
- `u5-st-check-indeclinable` ITEM `u5-i-indecl-метро`
- `u5-st-check-indeclinable` ITEM `u5-i-indecl-кино`
- `u5-st-check-indeclinable` ITEM `u5-i-decl-книга`
- `u5-st-check-indeclinable` ITEM `u5-i-decl-окно`
- `u5-s-book` OBJECTIVE `obj-u5-book`
- `u5-st-book-work` REFERENCE `ref-book-topics`
- `u5-st-book-work` REFERENCE `ref-u35-attribution`
- `obj-u5-indeclinable` PROVENANCE `prov-u5-contrast-flag-книга`
- `obj-u5-indeclinable` PROVENANCE `prov-u5-contrast-flag-окно`
- `obj-u5-indeclinable` PROVENANCE `prov-u5-contrast-книга`
- `obj-u5-indeclinable` PROVENANCE `prov-u5-contrast-окно`
- `obj-u5-indeclinable` PROVENANCE `prov-u5-flag-кафе`
- `obj-u5-indeclinable` PROVENANCE `prov-u5-flag-кино`
- `obj-u5-indeclinable` PROVENANCE `prov-u5-flag-кофе`
- `obj-u5-indeclinable` PROVENANCE `prov-u5-flag-меню`
- `obj-u5-indeclinable` PROVENANCE `prov-u5-flag-метро`
- `obj-u5-indeclinable` PROVENANCE `prov-u5-flag-пальто`
- `obj-u5-indeclinable` PROVENANCE `prov-u5-flag-радио`
- `obj-u5-indeclinable` PROVENANCE `prov-u5-flag-такси`
- `obj-u5-indeclinable` PROVENANCE `prov-u5-indecl-gloss-кафе`
- `obj-u5-indeclinable` PROVENANCE `prov-u5-indecl-gloss-кино`
- `obj-u5-indeclinable` PROVENANCE `prov-u5-indecl-gloss-кофе`
- `obj-u5-indeclinable` PROVENANCE `prov-u5-indecl-gloss-меню`
- `obj-u5-indeclinable` PROVENANCE `prov-u5-indecl-gloss-метро`
- `obj-u5-indeclinable` PROVENANCE `prov-u5-indecl-gloss-пальто`
- `obj-u5-indeclinable` PROVENANCE `prov-u5-indecl-gloss-радио`
- `obj-u5-indeclinable` PROVENANCE `prov-u5-indecl-gloss-такси`
- `obj-u5-indeclinable` PROVENANCE `prov-u5-indecl-rule`
- `obj-u5-indeclinable` PROVENANCE `prov-u5-indecl-word`
- `obj-u5-indeclinable` PROVENANCE `prov-u5-indecl-кафе`
- `obj-u5-indeclinable` PROVENANCE `prov-u5-indecl-кино`
- `obj-u5-indeclinable` PROVENANCE `prov-u5-indecl-кофе`
- `obj-u5-indeclinable` PROVENANCE `prov-u5-indecl-меню`
- `obj-u5-indeclinable` PROVENANCE `prov-u5-indecl-метро`
- `obj-u5-indeclinable` PROVENANCE `prov-u5-indecl-пальто`
- `obj-u5-indeclinable` PROVENANCE `prov-u5-indecl-радио`
- `obj-u5-indeclinable` PROVENANCE `prov-u5-indecl-такси`
- `obj-u5-prepositional` PROVENANCE `prov-u5-nom-field-книга`
- `obj-u5-prepositional` PROVENANCE `prov-u5-nom-field-окно`
- `obj-u5-prepositional` PROVENANCE `prov-u5-nom-field-письмо`
- `obj-u5-prepositional` PROVENANCE `prov-u5-nom-field-школа`
- `obj-u5-questions` PROVENANCE `prov-u5-pattern-chto-eto`
- `obj-u5-questions` PROVENANCE `prov-u5-pattern-chto-eto-en`
- `obj-u5-questions` PROVENANCE `prov-u5-pattern-gde-zhivete`
- `obj-u5-questions` PROVENANCE `prov-u5-pattern-gde-zhivete-en`
- `obj-u5-questions` PROVENANCE `prov-u5-pattern-kak-dela`
- `obj-u5-questions` PROVENANCE `prov-u5-pattern-kak-dela-en`
- `obj-u5-questions` PROVENANCE `prov-u5-pattern-kakoy-syr`
- `obj-u5-questions` PROVENANCE `prov-u5-pattern-kakoy-syr-en`
- `obj-u5-questions` PROVENANCE `prov-u5-pattern-kto-eto`
- `obj-u5-questions` PROVENANCE `prov-u5-pattern-kto-eto-en`
- `obj-u5-questions` PROVENANCE `prov-u5-pattern-kuda-idete`
- `obj-u5-questions` PROVENANCE `prov-u5-pattern-kuda-idete-en`
- `obj-u5-questions` PROVENANCE `prov-u5-pattern-pochemu`
- `obj-u5-questions` PROVENANCE `prov-u5-pattern-pochemu-en`
- `obj-u5-prepositional` PROVENANCE `prov-u5-prep-field-книга`
- `obj-u5-prepositional` PROVENANCE `prov-u5-prep-field-окно`
- `obj-u5-prepositional` PROVENANCE `prov-u5-prep-field-письмо`
- `obj-u5-prepositional` PROVENANCE `prov-u5-prep-field-школа`
- `obj-u5-prepositional` PROVENANCE `prov-u5-prep-gloss-в`
- `obj-u5-prepositional` PROVENANCE `prov-u5-prep-gloss-на`
- `obj-u5-prepositional` PROVENANCE `prov-u5-prep-gloss-о`
- `obj-u5-prepositional` PROVENANCE `prov-u5-prep-na-kryshe`
- `obj-u5-prepositional` PROVENANCE `prov-u5-prep-na-kryshe-en`
- `obj-u5-prepositional` PROVENANCE `prov-u5-prep-na-stene`
- `obj-u5-prepositional` PROVENANCE `prov-u5-prep-na-stene-en`
- `obj-u5-prepositional` PROVENANCE `prov-u5-prep-na-stole`
- `obj-u5-prepositional` PROVENANCE `prov-u5-prep-na-stole-en`
- `obj-u5-prepositional` PROVENANCE `prov-u5-prep-o-knige`
- `obj-u5-prepositional` PROVENANCE `prov-u5-prep-o-knige-en`
- `obj-u5-prepositional` PROVENANCE `prov-u5-prep-o-pisme`
- `obj-u5-prepositional` PROVENANCE `prov-u5-prep-o-pisme-en`
- `obj-u5-prepositional` PROVENANCE `prov-u5-prep-o-sade`
- `obj-u5-prepositional` PROVENANCE `prov-u5-prep-o-sade-en`
- `obj-u5-prepositional` PROVENANCE `prov-u5-prep-v-moskve`
- `obj-u5-prepositional` PROVENANCE `prov-u5-prep-v-moskve-en`
- `obj-u5-prepositional` PROVENANCE `prov-u5-prep-v-teatre`
- `obj-u5-prepositional` PROVENANCE `prov-u5-prep-v-teatre-en`
- `obj-u5-prepositional` PROVENANCE `prov-u5-prep-word-в`
- `obj-u5-prepositional` PROVENANCE `prov-u5-prep-word-на`
- `obj-u5-prepositional` PROVENANCE `prov-u5-prep-word-о`
- `obj-u5-questions` PROVENANCE `prov-u5-q-chto`
- `obj-u5-questions` PROVENANCE `prov-u5-q-chto-en`
- `obj-u5-questions` PROVENANCE `prov-u5-q-gde`
- `obj-u5-questions` PROVENANCE `prov-u5-q-gde-en`
- `obj-u5-questions` PROVENANCE `prov-u5-q-kak`
- `obj-u5-questions` PROVENANCE `prov-u5-q-kak-en`
- `obj-u5-questions` PROVENANCE `prov-u5-q-kakoy`
- `obj-u5-questions` PROVENANCE `prov-u5-q-kakoy-en`
- `obj-u5-questions` PROVENANCE `prov-u5-q-kogda`
- `obj-u5-questions` PROVENANCE `prov-u5-q-kogda-en`
- `obj-u5-questions` PROVENANCE `prov-u5-q-kto`
- `obj-u5-questions` PROVENANCE `prov-u5-q-kto-en`
- `obj-u5-questions` PROVENANCE `prov-u5-q-kuda`
- `obj-u5-questions` PROVENANCE `prov-u5-q-kuda-en`
- `obj-u5-questions` PROVENANCE `prov-u5-q-otkuda`
- `obj-u5-questions` PROVENANCE `prov-u5-q-otkuda-en`
- `obj-u5-questions` PROVENANCE `prov-u5-q-pochemu`
- `obj-u5-questions` PROVENANCE `prov-u5-q-pochemu-en`
- `obj-u5-questions` PROVENANCE `prov-u5-q-skolko`
- `obj-u5-questions` PROVENANCE `prov-u5-q-skolko-en`

## Book-directed experiences

- `u5-st-orient`
- `u5-st-book-work`
- `u5-st-book`

## Unavailable experiences

None.


## Unresolved relationships

None. Every referenced Unit 5 identifier resolves.
