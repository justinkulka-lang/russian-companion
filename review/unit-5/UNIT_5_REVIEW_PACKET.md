# Unit 5 linguistic and owned-edition mapping packet

This packet prepares review. It records an unsigned structural mapping proposal. It does not record a qualified review, an approved edition alignment, or an install decision.

The owned-edition mapping worksheet separates observed edition facts, the proposed lesson and printed page range, unresolved edition questions, and matters that still require qualified linguistic review. ISBN, owned-copy verification, variance, reviewer identity, qualification, date, and approval stay blank. The disposition remains PENDING_CURRICULUM_REVIEWER.

Unit 5 remains in development. Its readiness is content-complete and review-pending. The Lesson 5 relationship is a proposed, unsigned mapping. It is not an approved edition alignment.

## Package identity

- Schema version: 3
- Application: dev.russiancompanion
- Version: 2.0.0-dev (5)
- Curriculum source commit: 2fb455d4cf0b69808d431a2b9db3583b4d4b0dcb
- Unit content-review SHA-256: f38caf6305957349e80c88eda55c1a33a6df4ce4926868d11bbb2f9b561eafad
- Mapping-package SHA-256: 2a08c3d25c46aa6820a3798cafb90e415a873dbaa13bb003c42f330d15fe40ec
- Pack content-review SHA-256: 15c6ae393767cb7ffc190882867bfad923c9e81b07d0f4285cf0a678f2489280
- Review-package SHA-256: 4a7e53377faacbe4c8161c64b6e2c2f8bb1c711ea7ab13a67a77c27424e1d104
- Repository source SHA-256: 3c0acbb95297cf10f08da2bd8391463067eece22fec785158b5d2680d37dc2ba

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 1cff4be19da662136f17ed36d7a74813201f7897fcaa750300adb1b95b0f0d9c

status=IMPLEMENTED_PENDING_FINAL_AUDIT
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
- Content hash: fe33d69c7bfb8d797ea9b42cdff56effb75824b841f21f6517c78f83fa288be6

requireInstruction=true
requireBookSelfAttestation=true
requireAppChecks=true
minimumDistinctChecks=14
requireDueReview=true
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
- Content hash: 569dd3570b908d8dab32db4aff98e32861a763c5cf6421f2e58e5b9dc3859bf3

title=Recognize admitted indeclinable nouns
kind=RECOGNITION
unitNumber=5
instruction=u5-st-orient,u5-st-teach-indeclinable-1,u5-st-teach-indeclinable-2,u5-st-teach-indeclinable-3,u5-st-teach-indeclinable-4
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
- Content hash: b88e1c62677536876d3c88d72f23705f813431e23c39748c12fbcd6225cfcbb1

title=Recognize admitted indeclinable nouns
estimatedMinutes=12
objectives=obj-u5-indeclinable
steps=u5-st-teach-indeclinable-1,u5-st-guided-indeclinable-1,u5-st-teach-indeclinable-2,u5-st-teach-indeclinable-3,u5-st-rem-indeclinable,u5-st-teach-indeclinable-4,u5-st-check-indeclinable

### STEP `u5-st-teach-indeclinable-1`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-indeclinable
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 088b665a604a06565e49f6f29bab2c63ad5962378c0e92b4bb6cccfcf3936ead

kind=TEACH
title=Nouns that keep their form
body=An indeclinable noun keeps the same form for case or number. In these examples, ра́дио and такси́ are indeclinable. Recognize the behavior of the shown noun.
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
- Content hash: d12f92be5812c5d5d83a6f64e987d4cd06928732d6cdcff8758f86b2f56cb212

kind=GUIDED
title=Guided noun classification
body=Choose how the shown noun behaves. Read the explanation after choosing.
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
- Content hash: 43699131cb55b96ba7ff69f369d343770ffd9c078e3840afff4cea4653e0ab21

kind=TEACH
title=Four more unchanged nouns
body=The nouns метро́, кино́, пальто́ and ко́фе are also indeclinable. Their forms stay unchanged for case or number. Learn the noun with this behavior; do not infer that every noun with a similar ending behaves the same way.
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
- Content hash: 52189e7bd6a1d1e12882270f5486ac7b6b3701ab9e2011b3ec6b2f48a62701e1

kind=TEACH
title=Recognize the behavior
body=кафе́ and меню́ are two more indeclinable nouns. An unchanged word form does not mean that a sentence has no case relationships. These checks classify the shown noun; they do not test production of a sentence or a complete paradigm.
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
- Content hash: f16b4404e4c7349195315dccf867b712742059de53c0a5d7c7c8b9612cdc95df

kind=REMEDIATE
title=Review noun behavior
body=An indeclinable noun keeps the same form for case or number. In these examples, ра́дио and такси́ are indeclinable. Recognize the behavior of the shown noun.

The nouns метро́, кино́, пальто́ and ко́фе are also indeclinable. Their forms stay unchanged for case or number. Learn the noun with this behavior; do not infer that every noun with a similar ending behaves the same way.

кафе́ and меню́ are two more indeclinable nouns. An unchanged word form does not mean that a sentence has no case relationships. These checks classify the shown noun; they do not test production of a sentence or a complete paradigm.

Contrast кни́га and окно́: these nouns are declinable, so they can change for case. Their nominative and genitive forms differ. Choose the behavior of the shown noun: unchanged for case or number, or able to change for case. No new form needs to be invented.
dominantAction=Continue
references=
items=
examples=ex-u5-indecl-радио,ex-u5-indecl-такси,ex-u5-indecl-метро,ex-u5-indecl-кино,ex-u5-indecl-пальто,ex-u5-indecl-кофе,ex-u5-indecl-кафе,ex-u5-indecl-меню
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u5-st-teach-indeclinable-4`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-indeclinable
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: eee4243e6e83a9271351b0074b18c8f6c5f71f71e46933506eb002d911b887ba

kind=TEACH
title=Contrast nouns that can change
body=Contrast кни́га and окно́: these nouns are declinable, so they can change for case. Their nominative and genitive forms differ. Choose the behavior of the shown noun: unchanged for case or number, or able to change for case. No new form needs to be invented.
dominantAction=Continue
references=ref-u35-attribution
items=
examples=
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
- Content hash: 23575e07fed630152eae99239d79aba80dfb16c306e36bf81a4905180615b422

kind=CHECK
title=Independent noun classification
body=Classify the shown noun by its case behavior. A miss returns to all four explanations and the retained examples.
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
- Content hash: b8556187576aa6c31722e35d048691ec76222c97b6446c79d78f554897483d2a

title=Self-attest Lesson 5
estimatedMinutes=10
objectives=obj-u5-book
steps=u5-st-book-work,u5-st-book,u5-st-anki-direction,u5-st-binder-direction,u5-st-tutor-direction,u5-st-cumulative-direction

### STEP `u5-st-book-work`

- Objectives: obj-u5-book
- Sessions: u5-s-book
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: d7792830304075ee56a097ddecbee04647afed90991ea74d1b802dfc42a04ba5

kind=BOOK
title=Work Lesson 5 in your copy
body=Do Lesson 5 in the copy you own. Use that book's explanations and practice. The proposed records in this unit are a separate licensed set and do not copy the book.
dominantAction=Continue
references=ref-book-topics,ref-u35-attribution
items=
examples=
unavailableReason=
bookDirection=Owned-book assignment for Lesson 5: printed pages 46-55. PDF indexes 58-67 are file locators, not printed pages. Observed section locators: §5.1 p46 (PDF 58); §5.2 p46 (PDF 58); §5.3 p47 (PDF 59); §5.4 p47 (PDF 59); §5.5 p49 (PDF 61); §5.6 p49 (PDF 61); §5.7 p50 (PDF 62); §5.8 p50 (PDF 62); §5.9 p50 (PDF 62); §5.10 p51 (PDF 63); §5.11 p52 (PDF 64); §5.12 p52 (PDF 64). Observed exercise locators: 5/1 p54/PDF66; 5/2 p54/PDF66; 5/3 p54/PDF66; 5/4 p54/PDF66; 5/5 p54/PDF66. These locators are pending reviewer assignment and are not an approved mapping. Do the work in the copy you own. Book activity is not a demonstrated app check.
companionDirection=
remediationStepId=

### STEP `u5-st-book`

- Objectives: obj-u5-book
- Sessions: u5-s-book
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: c42c1f68234aa6dac78cd7b7b7d4fc7626a518bc48c1592c4b7d42608cfe4d06

kind=BOOK
title=Record Lesson 5 book work
body=When you have done Lesson 5 in your copy, self-attest here. Attestation records your report. It does not grade the lesson, approve a mapping, or approve the proposed records.
dominantAction=Continue
references=
items=
examples=
unavailableReason=
bookDirection=Owned-book assignment for Lesson 5: printed pages 46-55. PDF indexes 58-67 are file locators, not printed pages. Observed section locators: §5.1 p46 (PDF 58); §5.2 p46 (PDF 58); §5.3 p47 (PDF 59); §5.4 p47 (PDF 59); §5.5 p49 (PDF 61); §5.6 p49 (PDF 61); §5.7 p50 (PDF 62); §5.8 p50 (PDF 62); §5.9 p50 (PDF 62); §5.10 p51 (PDF 63); §5.11 p52 (PDF 64); §5.12 p52 (PDF 64). Observed exercise locators: 5/1 p54/PDF66; 5/2 p54/PDF66; 5/3 p54/PDF66; 5/4 p54/PDF66; 5/5 p54/PDF66. These locators are pending reviewer assignment and are not an approved mapping. Do the work in the copy you own. Book activity is not a demonstrated app check.
companionDirection=
remediationStepId=

### STEP `u5-st-anki-direction`

- Objectives: obj-u5-book
- Sessions: u5-s-book
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 4446d66e69b4a2aebf890d07bde90b09f0d2219696dafbbe0b45631cbb5bae42

kind=ANKI
title=Unit 5 AnkiDroid direction
body=Unit 5 AnkiDroid self-attestation uses only cards you are allowed to make. Binder: write one form from this unit that you missed and the correction you will use. Tutor conversation is optional and is not a demonstrated check. Cumulative review revisits completed objectives from earlier units. Activity is not demonstrated ability. Open the offline grammar reference for this unit when one is linked.
dominantAction=Self-attest
references=
items=
examples=
unavailableReason=
bookDirection=
companionDirection=Self-attest AnkiDroid work. It remains user-reported and does not demonstrate a check.
remediationStepId=

### STEP `u5-st-binder-direction`

- Objectives: obj-u5-book
- Sessions: u5-s-book
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 3c45f0418e34fd5924840d0893484e7d80fed78d7888d38cb90560bc8c500808

kind=BINDER
title=Unit 5 binder prompt
body=Unit 5 AnkiDroid self-attestation uses only cards you are allowed to make. Binder: write one form from this unit that you missed and the correction you will use. Tutor conversation is optional and is not a demonstrated check. Cumulative review revisits completed objectives from earlier units. Activity is not demonstrated ability. Open the offline grammar reference for this unit when one is linked.
dominantAction=Continue
references=
items=
examples=
unavailableReason=
bookDirection=
companionDirection=A binder prompt is one specific note, not a score.
remediationStepId=

### STEP `u5-st-tutor-direction`

- Objectives: obj-u5-book
- Sessions: u5-s-book
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: c1b4d80906d68eaa308dbe2123254ea1fd3058444197af30b521dba31d8e44e1

kind=TUTOR
title=Unit 5 optional tutor
body=Unit 5 AnkiDroid self-attestation uses only cards you are allowed to make. Binder: write one form from this unit that you missed and the correction you will use. Tutor conversation is optional and is not a demonstrated check. Cumulative review revisits completed objectives from earlier units. Activity is not demonstrated ability. Open the offline grammar reference for this unit when one is linked.
dominantAction=Continue
references=
items=
examples=
unavailableReason=
bookDirection=
companionDirection=Tutor work is optional and does not count as demonstrated ability.
remediationStepId=

### STEP `u5-st-cumulative-direction`

- Objectives: obj-u5-book
- Sessions: u5-s-book
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: fd2ffc45c4df7a4af52f266b27ac4fc6d5c5e2af317a190b589e46de996c809a

kind=ORIENT
title=Unit 5 cumulative review
body=Unit 5 AnkiDroid self-attestation uses only cards you are allowed to make. Binder: write one form from this unit that you missed and the correction you will use. Tutor conversation is optional and is not a demonstrated check. Cumulative review revisits completed objectives from earlier units. Activity is not demonstrated ability. Open the offline grammar reference for this unit when one is linked.
dominantAction=Continue
references=
items=
examples=
unavailableReason=
bookDirection=
companionDirection=Unit 5 AnkiDroid self-attestation uses only cards you are allowed to make. Binder: write one form from this unit that you missed and the correction you will use. Tutor conversation is optional and is not a demonstrated check. Cumulative review revisits completed objectives from earlier units. Activity is not demonstrated ability. Open the offline grammar reference for this unit when one is linked.
remediationStepId=

### EXAMPLE `ex-u5-q-kto`

- Objectives: obj-u5-questions
- Sessions: u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 645ee202474077e330240e10604374d4bcc87aa89de4bbd47f3d93b271bae533

kind=LEXICAL
letterId=
display=Кто
note=Who
objectiveId=obj-u5-questions
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-u5-q-chto`

- Objectives: obj-u5-questions
- Sessions: u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: fa15ac249644eb5a35d88fde59b73b3e51d66f5dce4259f4c806aefcfdc1f13c

kind=LEXICAL
letterId=
display=Что
note=What
objectiveId=obj-u5-questions
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-u5-q-kogda`

- Objectives: obj-u5-questions
- Sessions: u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: c0100dde692873b125b4e483044759617278402852e68c5dbf3c0079957f8ab4

kind=LEXICAL
letterId=
display=Когда
note=When
objectiveId=obj-u5-questions
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-u5-q-pochemu`

- Objectives: obj-u5-questions
- Sessions: u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 3943e90767a67263830f3620ed9597575602591ed0d108cc6e194e2d6cdc405f

kind=LEXICAL
letterId=
display=Почему
note=Why
objectiveId=obj-u5-questions
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-u5-q-kak`

- Objectives: obj-u5-questions
- Sessions: u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 3789fd6efb6c457aa6fe057140f87d7e4a0c2175eae4c89961b1e022a76c6790

kind=LEXICAL
letterId=
display=Как
note=How
objectiveId=obj-u5-questions
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-u5-q-gde`

- Objectives: obj-u5-questions
- Sessions: u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 89b41375cd78b065f847c92c977c29d25e75c6655201f8f303b4ecbb118da388

kind=LEXICAL
letterId=
display=Где
note=Where (location)
objectiveId=obj-u5-questions
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-u5-q-kuda`

- Objectives: obj-u5-questions
- Sessions: u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: b4adda9d3e7d2e003263659fa96216530d6fd37caa6901c84115f8bd4767dede

kind=LEXICAL
letterId=
display=Куда
note=Whither, where to
objectiveId=obj-u5-questions
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-u5-q-otkuda`

- Objectives: obj-u5-questions
- Sessions: u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 8376b0cb08599d0a0bbc5b673174ec5d509699f8e67a139e123a0ea918b78aec

kind=LEXICAL
letterId=
display=Откуда
note=Whence, where from
objectiveId=obj-u5-questions
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-u5-q-skolko`

- Objectives: obj-u5-questions
- Sessions: u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 7c7729ce7dd234e1781d24d1a6cb680aab3c3b6360b29afa5205e92d0a27dcd6

kind=LEXICAL
letterId=
display=Сколько
note=How much, how many
objectiveId=obj-u5-questions
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-u5-q-kakoy`

- Objectives: obj-u5-questions
- Sessions: u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: b569814d87cdba5899c1deaa2c94e8489e96cf967e08a0e5204147ad68e40e5c

kind=LEXICAL
letterId=
display=Какой
note=What kind
objectiveId=obj-u5-questions
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-u5-pattern-pattern-chto-eto`

- Objectives: obj-u5-questions
- Sessions: u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 8cf254165a3ea805894ab0a71e2db8cc2d1154b5fd88d184d0b3b965eae36854

kind=LEXICAL
letterId=
display=Что это?
note=What is this?
objectiveId=obj-u5-questions
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-u5-pattern-pattern-kto-eto`

- Objectives: obj-u5-questions
- Sessions: u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 3de2b3abfae4046ac9e71098d6794b8ebd62f8ac9a2fc0a9da81c38390404b0a

kind=LEXICAL
letterId=
display=Кто это?
note=Who is this?
objectiveId=obj-u5-questions
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-u5-pattern-pattern-kak-dela`

- Objectives: obj-u5-questions
- Sessions: u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 04c83aa6e479964a938daafae1ab971ef4b701ebf3f666bda8c2a059adf33f17

kind=LEXICAL
letterId=
display=Как дела?
note=How do you do?/How're things?
objectiveId=obj-u5-questions
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-u5-pattern-pattern-gde-zhivete`

- Objectives: obj-u5-questions
- Sessions: u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 92b0c82965d87bc675551559406032ab32eb78252c67efcb2bce3ecf7837e713

kind=LEXICAL
letterId=
display=Где вы живёте?
note=Where do you live?
objectiveId=obj-u5-questions
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-u5-prep-o-knige`

- Objectives: obj-u5-prepositional
- Sessions: u5-s-prepositional
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: b015a7369e800a5c7a7f914ef29eef11adab1632668d4d9169fdc64d7858248b

kind=LEXICAL
letterId=
display=О книге
note=About the book
objectiveId=obj-u5-prepositional
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-u5-prep-o-pisme`

- Objectives: obj-u5-prepositional
- Sessions: u5-s-prepositional
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: b031a6efde570b1b7ab9a90d9ad5ad1b45739c79fd8f6ae617ec0c63a6034a34

kind=LEXICAL
letterId=
display=О письме
note=About the letter
objectiveId=obj-u5-prepositional
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-u5-prep-o-sade`

- Objectives: obj-u5-prepositional
- Sessions: u5-s-prepositional
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 42c1ffb7eb2513075e009613cce2815eb1beb60063cfd4ca76c054d03c294c16

kind=LEXICAL
letterId=
display=О саде
note=About the garden
objectiveId=obj-u5-prepositional
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-u5-prep-v-moskve`

- Objectives: obj-u5-prepositional
- Sessions: u5-s-prepositional
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 6fc6fc1c05d447ee0b909f887d57130f05f24cc1d3b014f71a71be1627e0beeb

kind=LEXICAL
letterId=
display=я в Москве
note=I'm in Moscow
objectiveId=obj-u5-prepositional
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-u5-prep-na-kryshe`

- Objectives: obj-u5-prepositional
- Sessions: u5-s-prepositional
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 4e45d600be9dc35394dbd49e8877b9af98678fdce100ffd41e73dc8aa53e3ef7

kind=LEXICAL
letterId=
display=Он на крыше
note=He's on the roof
objectiveId=obj-u5-prepositional
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-u5-prep-v-teatre`

- Objectives: obj-u5-prepositional
- Sessions: u5-s-prepositional
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 6b9cbb1fe5b2cb63229db7d203446a5613178b4dfb5c31abef7f022736de337a

kind=LEXICAL
letterId=
display=Он в театре
note=He is at the theatre
objectiveId=obj-u5-prepositional
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-u5-prep-na-stole`

- Objectives: obj-u5-prepositional
- Sessions: u5-s-prepositional
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 24f1c626f17d53330621cb16759f691a3f565d49a03768a030351ea088a5e363

kind=LEXICAL
letterId=
display=Ваза стоит на столе.
note=The vase stands on the table
objectiveId=obj-u5-prepositional
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-u5-prep-na-stene`

- Objectives: obj-u5-prepositional
- Sessions: u5-s-prepositional
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 2206db3980012912bdd45c51f5624ef8ac5c3eac6da503d11cee5268ee725432

kind=LEXICAL
letterId=
display=Картина висит на стене.
note=The painting hangs on the wall
objectiveId=obj-u5-prepositional
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-u5-indecl-радио`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-indeclinable
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 007b005cb9056f558cd43266f96801f0297dd51cbf9633a72beddc81e3cdbeb5

kind=LEXICAL
letterId=
display=ра́дио
note=radio
objectiveId=obj-u5-indeclinable
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-u5-indecl-такси`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-indeclinable
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 6d2487d11140715724cbccd070ecfc12d0d3f2a2543b16a126f091f99ba491f6

kind=LEXICAL
letterId=
display=такси́
note=taxi
objectiveId=obj-u5-indeclinable
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-u5-indecl-метро`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-indeclinable
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: ca35855cc04c8423926fe54d223073a40ce8cdd8be41ec4958de1f2a46963424

kind=LEXICAL
letterId=
display=метро́
note=metro
objectiveId=obj-u5-indeclinable
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-u5-indecl-кино`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-indeclinable
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 5c3192b09cd07bced54b29c96c935272fc84ad03cb73d28baca875c871023345

kind=LEXICAL
letterId=
display=кино́
note=cinema
objectiveId=obj-u5-indeclinable
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-u5-indecl-пальто`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-indeclinable
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 11f21cec530e2e607b61754ff8b1acdf6f8222e49d3eebe8c8a49f0d40762345

kind=LEXICAL
letterId=
display=пальто́
note=coat
objectiveId=obj-u5-indeclinable
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-u5-indecl-кофе`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-indeclinable
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 885dc8b7e5ede12feb59b7cc191cc8571b0bf772ed7428b0dd914731686f78c6

kind=LEXICAL
letterId=
display=ко́фе
note=coffee
objectiveId=obj-u5-indeclinable
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-u5-indecl-кафе`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-indeclinable
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: f8dbbaa8293d7d5f3d946f50509fa9c5f6cba762ba0434fab78f7c4dd9fa0a8d

kind=LEXICAL
letterId=
display=кафе́
note=café
objectiveId=obj-u5-indeclinable
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### EXAMPLE `ex-u5-indecl-меню`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-indeclinable
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 576bb9daf203c9392316f23c7e9cbd1eaff8d7f78681509141c4e07ffb6ae852

kind=LEXICAL
letterId=
display=меню́
note=menu
objectiveId=obj-u5-indeclinable
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### GUIDED_PRACTICE `u5-i-q-kto`

- Objectives: obj-u5-questions
- Sessions: u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: eec75fe8bde49d6873af45374a5b9f8c6febb2984814e9509fb9397dc94fd8e8

kind=SOURCE_QUESTION
language=en
prompt=Which admitted question word is recorded for this English purpose: Who?
promptLetter=Кто
options=Кто | Что | Когда | Почему | Как | Где
answer=Кто
feedback=The frozen table records Кто for Who.
objectiveId=obj-u5-questions
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### GUIDED_PRACTICE `u5-i-q-chto`

- Objectives: obj-u5-questions
- Sessions: u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 4d62a2dadc0e4fb68ed39b2e83074d154a33ad9a9c0587b960301b1459cb339b

kind=SOURCE_QUESTION
language=en
prompt=Which admitted question word is recorded for this English purpose: What?
promptLetter=Что
options=Кто | Что | Когда | Почему | Как | Где
answer=Что
feedback=The frozen table records Что for What.
objectiveId=obj-u5-questions
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### GUIDED_PRACTICE `u5-i-pattern-pattern-chto-eto`

- Objectives: obj-u5-questions
- Sessions: u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: ad6feb4c14049f60dc1c39067d40f17f4e89d211c74c9926113bc78c79b5834f

kind=SOURCE_QUESTION
language=en
prompt=Which admitted meaning is recorded for the shown written question?
promptLetter=Что это?
options=What is this? | Who is this? | How do you do?/How're things? | Where do you live?
answer=What is this?
feedback=The frozen page records this meaning: What is this?. Written punctuation is not listening practice.
objectiveId=obj-u5-questions
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### GUIDED_PRACTICE `u5-i-prep-o-knige`

- Objectives: obj-u5-prepositional
- Sessions: u5-s-prepositional
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 67be9d85663489497db9641a6505b09c55567a7ee8c4dde0e623c4e36a91f644

kind=SOURCE_CASE
language=en
prompt=Which admitted description is recorded for the shown example?
promptLetter=О книге
options=About the book | About the letter | About the garden | I'm in Moscow
answer=About the book
feedback=The frozen page records this example as: About the book.
objectiveId=obj-u5-prepositional
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### GUIDED_PRACTICE `u5-i-prep-o-pisme`

- Objectives: obj-u5-prepositional
- Sessions: u5-s-prepositional
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 0735698070d063381d773a31477b8a930d0c4840d181025029d9ac4f8e22a4ac

kind=SOURCE_CASE
language=en
prompt=Which admitted description is recorded for the shown example?
promptLetter=О письме
options=About the book | About the letter | About the garden | I'm in Moscow
answer=About the letter
feedback=The frozen page records this example as: About the letter.
objectiveId=obj-u5-prepositional
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### GUIDED_PRACTICE `u5-i-prep-field-книга`

- Objectives: obj-u5-prepositional
- Sessions: u5-s-prepositional
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: a5826c8776bb95b20243ecc25a1d4797778d6d684e3c6e8c0d92a145c37f1b71

kind=SOURCE_FORM
language=en
prompt=Which string is the admitted second field, not the first field?
promptLetter=кни́га
options=кни́га | кни́ге
answer=кни́ге
feedback=The frozen row lists that field as кни́ге.
objectiveId=obj-u5-prepositional
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### GUIDED_PRACTICE `u5-i-prep-field-письмо`

- Objectives: obj-u5-prepositional
- Sessions: u5-s-prepositional
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 28ab0be3816df2fd0d319e6470dff89c0e24dc1a1a7018d7c55e67f0e18c36a0

kind=SOURCE_FORM
language=en
prompt=Which string is the admitted second field, not the first field?
promptLetter=письмо́
options=письмо́ | письме́
answer=письме́
feedback=The frozen row lists that field as письме́.
objectiveId=obj-u5-prepositional
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### GUIDED_PRACTICE `u5-i-indecl-радио`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-indeclinable
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 41033429885e3c5a8c414175e74befa743a26f95a67f97789fea251324e54788

kind=SOURCE_INDECLINABLE
language=en
prompt=Which behavior applies to the shown noun?
promptLetter=ра́дио
options=Indeclinable: unchanged for case or number | Declinable: can change for case
answer=Indeclinable: unchanged for case or number
feedback=ра́дио is indeclinable: its form stays unchanged for case or number. Choose the unchanged behavior, rather than the behavior of a declinable noun.
objectiveId=obj-u5-indeclinable
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### INDEPENDENT_CHECK `u5-i-q-kogda`

- Objectives: obj-u5-questions
- Sessions: u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: f149a839b0965e8a23abcf7bb3b13bcd7e05749608080c4b4c5611d9b378973f

kind=SOURCE_QUESTION
language=en
prompt=Which admitted question word is recorded for this English purpose: When?
promptLetter=Когда
options=Кто | Что | Когда | Почему | Как | Где
answer=Когда
feedback=The frozen table records Когда for When.
objectiveId=obj-u5-questions
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### INDEPENDENT_CHECK `u5-i-q-pochemu`

- Objectives: obj-u5-questions
- Sessions: u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: cf32b313e1764b236ae9b4cf04d6a4b007d68e99f0dda0a2f4491c16245593a1

kind=SOURCE_QUESTION
language=en
prompt=Which admitted question word is recorded for this English purpose: Why?
promptLetter=Почему
options=Кто | Что | Когда | Почему | Как | Где
answer=Почему
feedback=The frozen table records Почему for Why.
objectiveId=obj-u5-questions
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### INDEPENDENT_CHECK `u5-i-q-kak`

- Objectives: obj-u5-questions
- Sessions: u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 1b2bf04884b2173a4f6df008cc38991b77495f2621f7853b3cfb6c8a5d5f4175

kind=SOURCE_QUESTION
language=en
prompt=Which admitted question word is recorded for this English purpose: How?
promptLetter=Как
options=Кто | Что | Когда | Почему | Как | Где
answer=Как
feedback=The frozen table records Как for How.
objectiveId=obj-u5-questions
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### INDEPENDENT_CHECK `u5-i-q-gde`

- Objectives: obj-u5-questions
- Sessions: u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 7eebb23d0598a23267a48ba3bd717852b13ebea3d76a17a8d513def55efca8f0

kind=SOURCE_QUESTION
language=en
prompt=Which admitted question word is recorded for this English purpose: Where (location)?
promptLetter=Где
options=Кто | Что | Когда | Почему | Как | Где
answer=Где
feedback=The frozen table records Где for Where (location).
objectiveId=obj-u5-questions
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### INDEPENDENT_CHECK `u5-i-pattern-pattern-kto-eto`

- Objectives: obj-u5-questions
- Sessions: u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: e83549c5ee19ec1b10730e43f978a14ac59f7233a296552356de5dd607430c81

kind=SOURCE_QUESTION
language=en
prompt=Which admitted meaning is recorded for the shown written question?
promptLetter=Кто это?
options=What is this? | Who is this? | How do you do?/How're things? | Where do you live?
answer=Who is this?
feedback=The frozen page records this meaning: Who is this?. Written punctuation is not listening practice.
objectiveId=obj-u5-questions
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### INDEPENDENT_CHECK `u5-i-pattern-pattern-kak-dela`

- Objectives: obj-u5-questions
- Sessions: u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: cec51610fb82514d477bf586a30b6edd99609008ddef0c54c9d069ccbfb92452

kind=SOURCE_QUESTION
language=en
prompt=Which admitted meaning is recorded for the shown written question?
promptLetter=Как дела?
options=What is this? | Who is this? | How do you do?/How're things? | Where do you live?
answer=How do you do?/How're things?
feedback=The frozen page records this meaning: How do you do?/How're things?. Written punctuation is not listening practice.
objectiveId=obj-u5-questions
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### INDEPENDENT_CHECK `u5-i-pattern-pattern-gde-zhivete`

- Objectives: obj-u5-questions
- Sessions: u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: f57e6c8733c60a4b32871465282b0f6d966ea1d41cfc44e673f01068b2aa8148

kind=SOURCE_QUESTION
language=en
prompt=Which admitted meaning is recorded for the shown written question?
promptLetter=Где вы живёте?
options=What is this? | Who is this? | How do you do?/How're things? | Where do you live?
answer=Where do you live?
feedback=The frozen page records this meaning: Where do you live?. Written punctuation is not listening practice.
objectiveId=obj-u5-questions
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### INDEPENDENT_CHECK `u5-i-prep-o-sade`

- Objectives: obj-u5-prepositional
- Sessions: u5-s-prepositional
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 97cff2a253d79504c23369c7dbf3dca8bb7d42e26001aea132d916cccfe8addb

kind=SOURCE_CASE
language=en
prompt=Which admitted description is recorded for the shown example?
promptLetter=О саде
options=About the book | About the letter | About the garden | I'm in Moscow
answer=About the garden
feedback=The frozen page records this example as: About the garden.
objectiveId=obj-u5-prepositional
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### INDEPENDENT_CHECK `u5-i-prep-v-moskve`

- Objectives: obj-u5-prepositional
- Sessions: u5-s-prepositional
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 67521029e3ec46c8733c9a79f8ffd76d0b8c383f5982991b8f6f8ff556a0981f

kind=SOURCE_CASE
language=en
prompt=Which admitted description is recorded for the shown example?
promptLetter=я в Москве
options=About the book | About the letter | About the garden | I'm in Moscow
answer=I'm in Moscow
feedback=The frozen page records this example as: I'm in Moscow.
objectiveId=obj-u5-prepositional
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### INDEPENDENT_CHECK `u5-i-indecl-такси`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-indeclinable
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 3346f078c2a342304c5a4febc9c81de543c2567dd12870e924997720d77d021f

kind=SOURCE_INDECLINABLE
language=en
prompt=Which behavior applies to the shown noun?
promptLetter=такси́
options=Indeclinable: unchanged for case or number | Declinable: can change for case
answer=Indeclinable: unchanged for case or number
feedback=такси́ is indeclinable: its form stays unchanged for case or number. Choose the unchanged behavior, rather than the behavior of a declinable noun.
objectiveId=obj-u5-indeclinable
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### INDEPENDENT_CHECK `u5-i-indecl-метро`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-indeclinable
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 5f8abc00525010d352876a80a0834f6a87301a76f2a94118361d752259bb491b

kind=SOURCE_INDECLINABLE
language=en
prompt=Which behavior applies to the shown noun?
promptLetter=метро́
options=Indeclinable: unchanged for case or number | Declinable: can change for case
answer=Indeclinable: unchanged for case or number
feedback=метро́ is indeclinable: its form stays unchanged for case or number. Choose the unchanged behavior, rather than the behavior of a declinable noun.
objectiveId=obj-u5-indeclinable
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### INDEPENDENT_CHECK `u5-i-indecl-кино`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-indeclinable
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: c9bb6c783bafa4c22097957d40394ccddd63693efcd32a1cef814421fd4ff55e

kind=SOURCE_INDECLINABLE
language=en
prompt=Which behavior applies to the shown noun?
promptLetter=кино́
options=Indeclinable: unchanged for case or number | Declinable: can change for case
answer=Indeclinable: unchanged for case or number
feedback=кино́ is indeclinable: its form stays unchanged for case or number. Choose the unchanged behavior, rather than the behavior of a declinable noun.
objectiveId=obj-u5-indeclinable
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### INDEPENDENT_CHECK `u5-i-decl-книга`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-indeclinable
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: e658a871614c0fc994cbd1a5b02a9a5695e16694f0c4a99bf4f0e6115e0ffdd8

kind=SOURCE_INDECLINABLE
language=en
prompt=Which behavior applies to the shown noun?
promptLetter=кни́га
options=Indeclinable: unchanged for case or number | Declinable: can change for case
answer=Declinable: can change for case
feedback=кни́га is declinable: it can change for case. Its nominative and genitive forms differ, so the unchanged behavior does not describe this noun.
objectiveId=obj-u5-indeclinable
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

### INDEPENDENT_CHECK `u5-i-decl-окно`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-indeclinable
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: bcf1f0dab68ef39d5c6194ce626d6e7f76830f558e30e35dbaf2ce617d7137b4

kind=SOURCE_INDECLINABLE
language=en
prompt=Which behavior applies to the shown noun?
promptLetter=окно́
options=Indeclinable: unchanged for case or number | Declinable: can change for case
answer=Declinable: can change for case
feedback=окно́ is declinable: it can change for case. Its nominative and genitive forms differ, so the unchanged behavior does not describe this noun.
objectiveId=obj-u5-indeclinable
containsGeneratedRussian=false
reviewStatus=IMPLEMENTED_PENDING_FINAL_AUDIT

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

### PROVENANCE `prov-ca-u5-behavior-changeable-choice`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-orient, u5-s-indeclinable
- Source: src-content-audit-research
- Rights: rights-content-audit-research
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 8d430b42229229bae929244542e9553183a5e381e94a8c465a983925fb20207b

recordType=label
sourceUrl=urn:russian-companion:content-audit-research:2026-10-08
sourceRevision=content-audit-2026-10-08
sourceRecordId=ca-coverage:obj-u5-indeclinable:behavior:changeable-choice
originalSourceText=Retained Wikibooks Russian/Grammar/Nominative oldid4213267, prov-u5-indecl-rule: indeclinable means no change according to case or number. Retained OpenRussian nouns.csv at commit50e210c4803237779cb562bc1abcea529066031c: indeclinable=1 for радио,такси,метро,кино,пальто,кофе,кафе,меню; indeclinable=0 for книга,окно. The latter rows have different sg_nom and sg_gen forms. Existing Russian display/flag provenance is retained byte-for-byte. Bounded application inference: classify these already admitted nouns by their recorded behavior, without producing new Russian forms or teaching a general ending-based guess. Independently authored English on 2026-10-12 within content-audit-2026-10-08; no primary prose or owned-book text copied; final semantic audit pending.
Declinable: can change for case
shippedText=Declinable: can change for case
transformation=content-audit research synthesis
licenseId=Original app text
licenseUrl=urn:russian-companion:license:original-app-text
attribution=Russian Companion content-audit research synthesis, content-audit-2026-10-08. No owned-book prose is included.
shareAlikeTreatment=Original research synthesis authored for this companion. It is not a CC BY-SA quotation. Application code, user data, and user recordings are outside this statement.
sourceHash=aea5887bdc8c7b10654b12f0b24569b51f1f34479b18ff9eb6c77bad3cf708fb
contentHash=bfeae062f05e0cf0c42e73a9740680f67aff9c46e2a528e6098646d462102d53
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-ca-u5-behavior-prompt`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-orient, u5-s-indeclinable
- Source: src-content-audit-research
- Rights: rights-content-audit-research
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 28b309f8b1403b6d4ae5f37be1f5a98f285085436c10413cd39be3c5243b18d4

recordType=label
sourceUrl=urn:russian-companion:content-audit-research:2026-10-08
sourceRevision=content-audit-2026-10-08
sourceRecordId=ca-coverage:obj-u5-indeclinable:behavior:prompt
originalSourceText=Retained Wikibooks Russian/Grammar/Nominative oldid4213267, prov-u5-indecl-rule: indeclinable means no change according to case or number. Retained OpenRussian nouns.csv at commit50e210c4803237779cb562bc1abcea529066031c: indeclinable=1 for радио,такси,метро,кино,пальто,кофе,кафе,меню; indeclinable=0 for книга,окно. The latter rows have different sg_nom and sg_gen forms. Existing Russian display/flag provenance is retained byte-for-byte. Bounded application inference: classify these already admitted nouns by their recorded behavior, without producing new Russian forms or teaching a general ending-based guess. Independently authored English on 2026-10-12 within content-audit-2026-10-08; no primary prose or owned-book text copied; final semantic audit pending.
Which behavior applies to the shown noun?
shippedText=Which behavior applies to the shown noun?
transformation=content-audit research synthesis
licenseId=Original app text
licenseUrl=urn:russian-companion:license:original-app-text
attribution=Russian Companion content-audit research synthesis, content-audit-2026-10-08. No owned-book prose is included.
shareAlikeTreatment=Original research synthesis authored for this companion. It is not a CC BY-SA quotation. Application code, user data, and user recordings are outside this statement.
sourceHash=ccf20c3c96b1a45d2d866831d50b4f3a7f1757324a15a49fa1397c887b138e17
contentHash=67df1cb1c1e73526a86b9e04ea6cbb3b5eea2b6ad19955cc90f59c724fa93986
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-ca-u5-behavior-rem-body`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-orient, u5-s-indeclinable
- Source: src-content-audit-research
- Rights: rights-content-audit-research
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 3b0a149cba53728c331e57e7f7921fec84c59fd223586b9c8269adc7d5d7898a

recordType=label
sourceUrl=urn:russian-companion:content-audit-research:2026-10-08
sourceRevision=content-audit-2026-10-08
sourceRecordId=ca-coverage:obj-u5-indeclinable:behavior:rem-body
originalSourceText=Retained Wikibooks Russian/Grammar/Nominative oldid4213267, prov-u5-indecl-rule: indeclinable means no change according to case or number. Retained OpenRussian nouns.csv at commit50e210c4803237779cb562bc1abcea529066031c: indeclinable=1 for радио,такси,метро,кино,пальто,кофе,кафе,меню; indeclinable=0 for книга,окно. The latter rows have different sg_nom and sg_gen forms. Existing Russian display/flag provenance is retained byte-for-byte. Bounded application inference: classify these already admitted nouns by their recorded behavior, without producing new Russian forms or teaching a general ending-based guess. Independently authored English on 2026-10-12 within content-audit-2026-10-08; no primary prose or owned-book text copied; final semantic audit pending.
An indeclinable noun keeps the same form for case or number. In these examples, ра́дио and такси́ are indeclinable. Recognize the behavior of the shown noun.

The nouns метро́, кино́, пальто́ and ко́фе are also indeclinable. Their forms stay unchanged for case or number. Learn the noun with this behavior; do not infer that every noun with a similar ending behaves the same way.

кафе́ and меню́ are two more indeclinable nouns. An unchanged word form does not mean that a sentence has no case relationships. These checks classify the shown noun; they do not test production of a sentence or a complete paradigm.

Contrast кни́га and окно́: these nouns are declinable, so they can change for case. Their nominative and genitive forms differ. Choose the behavior of the shown noun: unchanged for case or number, or able to change for case. No new form needs to be invented.
shippedText=An indeclinable noun keeps the same form for case or number. In these examples, ра́дио and такси́ are indeclinable. Recognize the behavior of the shown noun.

The nouns метро́, кино́, пальто́ and ко́фе are also indeclinable. Their forms stay unchanged for case or number. Learn the noun with this behavior; do not infer that every noun with a similar ending behaves the same way.

кафе́ and меню́ are two more indeclinable nouns. An unchanged word form does not mean that a sentence has no case relationships. These checks classify the shown noun; they do not test production of a sentence or a complete paradigm.

Contrast кни́га and окно́: these nouns are declinable, so they can change for case. Their nominative and genitive forms differ. Choose the behavior of the shown noun: unchanged for case or number, or able to change for case. No new form needs to be invented.
transformation=content-audit research synthesis
licenseId=Original app text
licenseUrl=urn:russian-companion:license:original-app-text
attribution=Russian Companion content-audit research synthesis, content-audit-2026-10-08. No owned-book prose is included.
shareAlikeTreatment=Original research synthesis authored for this companion. It is not a CC BY-SA quotation. Application code, user data, and user recordings are outside this statement.
sourceHash=6e3c58690842f9185e758457e54765c9050cdd223598509d45d26aa5b2b750a7
contentHash=fed63a2d64fd3e887bbc88fa4a9376799a6e1c4671a7d5821929556658700956
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-ca-u5-behavior-rem-title`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-orient, u5-s-indeclinable
- Source: src-content-audit-research
- Rights: rights-content-audit-research
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 32b98cad6717786af1973266061c5c18a577fa859a39310f45f9fc79d53a25be

recordType=label
sourceUrl=urn:russian-companion:content-audit-research:2026-10-08
sourceRevision=content-audit-2026-10-08
sourceRecordId=ca-coverage:obj-u5-indeclinable:behavior:rem-title
originalSourceText=Retained Wikibooks Russian/Grammar/Nominative oldid4213267, prov-u5-indecl-rule: indeclinable means no change according to case or number. Retained OpenRussian nouns.csv at commit50e210c4803237779cb562bc1abcea529066031c: indeclinable=1 for радио,такси,метро,кино,пальто,кофе,кафе,меню; indeclinable=0 for книга,окно. The latter rows have different sg_nom and sg_gen forms. Existing Russian display/flag provenance is retained byte-for-byte. Bounded application inference: classify these already admitted nouns by their recorded behavior, without producing new Russian forms or teaching a general ending-based guess. Independently authored English on 2026-10-12 within content-audit-2026-10-08; no primary prose or owned-book text copied; final semantic audit pending.
Review noun behavior
shippedText=Review noun behavior
transformation=content-audit research synthesis
licenseId=Original app text
licenseUrl=urn:russian-companion:license:original-app-text
attribution=Russian Companion content-audit research synthesis, content-audit-2026-10-08. No owned-book prose is included.
shareAlikeTreatment=Original research synthesis authored for this companion. It is not a CC BY-SA quotation. Application code, user data, and user recordings are outside this statement.
sourceHash=c7817066d147336fc748d594a8cd5a4ce486112f6f1db7b8aa150e3653cd3608
contentHash=998e80e475cb8b3e9f4cf15c27f44f19969973cc9ab9ebbdf357933a5dc4261e
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-ca-u5-behavior-teach-1-body`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-orient, u5-s-indeclinable
- Source: src-content-audit-research
- Rights: rights-content-audit-research
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 0f7a35a6b6c168bfd91f7620e71a424bf92af73a1b40f3d94fd119d14d775114

recordType=label
sourceUrl=urn:russian-companion:content-audit-research:2026-10-08
sourceRevision=content-audit-2026-10-08
sourceRecordId=ca-coverage:obj-u5-indeclinable:behavior:teach-1-body
originalSourceText=Retained Wikibooks Russian/Grammar/Nominative oldid4213267, prov-u5-indecl-rule: indeclinable means no change according to case or number. Retained OpenRussian nouns.csv at commit50e210c4803237779cb562bc1abcea529066031c: indeclinable=1 for радио,такси,метро,кино,пальто,кофе,кафе,меню; indeclinable=0 for книга,окно. The latter rows have different sg_nom and sg_gen forms. Existing Russian display/flag provenance is retained byte-for-byte. Bounded application inference: classify these already admitted nouns by their recorded behavior, without producing new Russian forms or teaching a general ending-based guess. Independently authored English on 2026-10-12 within content-audit-2026-10-08; no primary prose or owned-book text copied; final semantic audit pending.
An indeclinable noun keeps the same form for case or number. In these examples, ра́дио and такси́ are indeclinable. Recognize the behavior of the shown noun.
shippedText=An indeclinable noun keeps the same form for case or number. In these examples, ра́дио and такси́ are indeclinable. Recognize the behavior of the shown noun.
transformation=content-audit research synthesis
licenseId=Original app text
licenseUrl=urn:russian-companion:license:original-app-text
attribution=Russian Companion content-audit research synthesis, content-audit-2026-10-08. No owned-book prose is included.
shareAlikeTreatment=Original research synthesis authored for this companion. It is not a CC BY-SA quotation. Application code, user data, and user recordings are outside this statement.
sourceHash=c5e327f8dfaa4f2304cceb80e5077515a4220f7dc48eb0febb1bfa82ba137bc9
contentHash=2012e1e5998f40d13a53909eac1d744d3115b04095fe58f9ac3e188c60b7c657
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-ca-u5-behavior-teach-1-title`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-orient, u5-s-indeclinable
- Source: src-content-audit-research
- Rights: rights-content-audit-research
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 9cfd79faf1c0d938d55dcc0c10b9635baffe648adcd8b35e38c875280ae25a6f

recordType=label
sourceUrl=urn:russian-companion:content-audit-research:2026-10-08
sourceRevision=content-audit-2026-10-08
sourceRecordId=ca-coverage:obj-u5-indeclinable:behavior:teach-1-title
originalSourceText=Retained Wikibooks Russian/Grammar/Nominative oldid4213267, prov-u5-indecl-rule: indeclinable means no change according to case or number. Retained OpenRussian nouns.csv at commit50e210c4803237779cb562bc1abcea529066031c: indeclinable=1 for радио,такси,метро,кино,пальто,кофе,кафе,меню; indeclinable=0 for книга,окно. The latter rows have different sg_nom and sg_gen forms. Existing Russian display/flag provenance is retained byte-for-byte. Bounded application inference: classify these already admitted nouns by their recorded behavior, without producing new Russian forms or teaching a general ending-based guess. Independently authored English on 2026-10-12 within content-audit-2026-10-08; no primary prose or owned-book text copied; final semantic audit pending.
Nouns that keep their form
shippedText=Nouns that keep their form
transformation=content-audit research synthesis
licenseId=Original app text
licenseUrl=urn:russian-companion:license:original-app-text
attribution=Russian Companion content-audit research synthesis, content-audit-2026-10-08. No owned-book prose is included.
shareAlikeTreatment=Original research synthesis authored for this companion. It is not a CC BY-SA quotation. Application code, user data, and user recordings are outside this statement.
sourceHash=f7e1057f4a9b92c87db80200ca62781b154c692a7c892b8d8236ffb0a262c071
contentHash=d3db9a7b18926b4bb6ffb940254c157253bab58074fd3d74bea9dd1cc8c160eb
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-ca-u5-behavior-teach-2-body`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-orient, u5-s-indeclinable
- Source: src-content-audit-research
- Rights: rights-content-audit-research
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 2072e0f0beff1af6d0c997f13df658e3b155b4536420767c81976d82473a8a2e

recordType=label
sourceUrl=urn:russian-companion:content-audit-research:2026-10-08
sourceRevision=content-audit-2026-10-08
sourceRecordId=ca-coverage:obj-u5-indeclinable:behavior:teach-2-body
originalSourceText=Retained Wikibooks Russian/Grammar/Nominative oldid4213267, prov-u5-indecl-rule: indeclinable means no change according to case or number. Retained OpenRussian nouns.csv at commit50e210c4803237779cb562bc1abcea529066031c: indeclinable=1 for радио,такси,метро,кино,пальто,кофе,кафе,меню; indeclinable=0 for книга,окно. The latter rows have different sg_nom and sg_gen forms. Existing Russian display/flag provenance is retained byte-for-byte. Bounded application inference: classify these already admitted nouns by their recorded behavior, without producing new Russian forms or teaching a general ending-based guess. Independently authored English on 2026-10-12 within content-audit-2026-10-08; no primary prose or owned-book text copied; final semantic audit pending.
The nouns метро́, кино́, пальто́ and ко́фе are also indeclinable. Their forms stay unchanged for case or number. Learn the noun with this behavior; do not infer that every noun with a similar ending behaves the same way.
shippedText=The nouns метро́, кино́, пальто́ and ко́фе are also indeclinable. Their forms stay unchanged for case or number. Learn the noun with this behavior; do not infer that every noun with a similar ending behaves the same way.
transformation=content-audit research synthesis
licenseId=Original app text
licenseUrl=urn:russian-companion:license:original-app-text
attribution=Russian Companion content-audit research synthesis, content-audit-2026-10-08. No owned-book prose is included.
shareAlikeTreatment=Original research synthesis authored for this companion. It is not a CC BY-SA quotation. Application code, user data, and user recordings are outside this statement.
sourceHash=3701181b534f7f6724b4dfd2dc86d221174861190da4a75e6be91d3f8bab010d
contentHash=c3096faf6e20105ec96f390fe1a9a0c8ec6d9fd21eb67bc21be91e1dacc44a7e
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-ca-u5-behavior-teach-2-title`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-orient, u5-s-indeclinable
- Source: src-content-audit-research
- Rights: rights-content-audit-research
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 010469a7af5807ec71af453bf691cbc8f3aa6bfc96ab64155a2c1fb54d722b5c

recordType=label
sourceUrl=urn:russian-companion:content-audit-research:2026-10-08
sourceRevision=content-audit-2026-10-08
sourceRecordId=ca-coverage:obj-u5-indeclinable:behavior:teach-2-title
originalSourceText=Retained Wikibooks Russian/Grammar/Nominative oldid4213267, prov-u5-indecl-rule: indeclinable means no change according to case or number. Retained OpenRussian nouns.csv at commit50e210c4803237779cb562bc1abcea529066031c: indeclinable=1 for радио,такси,метро,кино,пальто,кофе,кафе,меню; indeclinable=0 for книга,окно. The latter rows have different sg_nom and sg_gen forms. Existing Russian display/flag provenance is retained byte-for-byte. Bounded application inference: classify these already admitted nouns by their recorded behavior, without producing new Russian forms or teaching a general ending-based guess. Independently authored English on 2026-10-12 within content-audit-2026-10-08; no primary prose or owned-book text copied; final semantic audit pending.
Four more unchanged nouns
shippedText=Four more unchanged nouns
transformation=content-audit research synthesis
licenseId=Original app text
licenseUrl=urn:russian-companion:license:original-app-text
attribution=Russian Companion content-audit research synthesis, content-audit-2026-10-08. No owned-book prose is included.
shareAlikeTreatment=Original research synthesis authored for this companion. It is not a CC BY-SA quotation. Application code, user data, and user recordings are outside this statement.
sourceHash=eb44d04a760d742d1698103ea87a4a395b008adf893d07a938f29ab1cdc79753
contentHash=3fe610783477f309e59c134248dbba09edb95bdc26ef979d74bac9d358fa3105
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-ca-u5-behavior-teach-3-body`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-orient, u5-s-indeclinable
- Source: src-content-audit-research
- Rights: rights-content-audit-research
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 45734c8cf5040742acaf6a4d317c9c5e2b08969d71ec71821a509ed07f4cad60

recordType=label
sourceUrl=urn:russian-companion:content-audit-research:2026-10-08
sourceRevision=content-audit-2026-10-08
sourceRecordId=ca-coverage:obj-u5-indeclinable:behavior:teach-3-body
originalSourceText=Retained Wikibooks Russian/Grammar/Nominative oldid4213267, prov-u5-indecl-rule: indeclinable means no change according to case or number. Retained OpenRussian nouns.csv at commit50e210c4803237779cb562bc1abcea529066031c: indeclinable=1 for радио,такси,метро,кино,пальто,кофе,кафе,меню; indeclinable=0 for книга,окно. The latter rows have different sg_nom and sg_gen forms. Existing Russian display/flag provenance is retained byte-for-byte. Bounded application inference: classify these already admitted nouns by their recorded behavior, without producing new Russian forms or teaching a general ending-based guess. Independently authored English on 2026-10-12 within content-audit-2026-10-08; no primary prose or owned-book text copied; final semantic audit pending.
кафе́ and меню́ are two more indeclinable nouns. An unchanged word form does not mean that a sentence has no case relationships. These checks classify the shown noun; they do not test production of a sentence or a complete paradigm.
shippedText=кафе́ and меню́ are two more indeclinable nouns. An unchanged word form does not mean that a sentence has no case relationships. These checks classify the shown noun; they do not test production of a sentence or a complete paradigm.
transformation=content-audit research synthesis
licenseId=Original app text
licenseUrl=urn:russian-companion:license:original-app-text
attribution=Russian Companion content-audit research synthesis, content-audit-2026-10-08. No owned-book prose is included.
shareAlikeTreatment=Original research synthesis authored for this companion. It is not a CC BY-SA quotation. Application code, user data, and user recordings are outside this statement.
sourceHash=8a7778edbd63d33eb73a1414a930bac4364bc2a34f1d6861633ebe8af26805aa
contentHash=975f5c7918d66d00de2bd610b5012d63e4f1712a04e82f0e550f311cc69d594c
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-ca-u5-behavior-teach-3-title`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-orient, u5-s-indeclinable
- Source: src-content-audit-research
- Rights: rights-content-audit-research
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 381951e95eae82a2817f7fe84e4c21e2d8bd636816e356ec9c6991497b2c15b4

recordType=label
sourceUrl=urn:russian-companion:content-audit-research:2026-10-08
sourceRevision=content-audit-2026-10-08
sourceRecordId=ca-coverage:obj-u5-indeclinable:behavior:teach-3-title
originalSourceText=Retained Wikibooks Russian/Grammar/Nominative oldid4213267, prov-u5-indecl-rule: indeclinable means no change according to case or number. Retained OpenRussian nouns.csv at commit50e210c4803237779cb562bc1abcea529066031c: indeclinable=1 for радио,такси,метро,кино,пальто,кофе,кафе,меню; indeclinable=0 for книга,окно. The latter rows have different sg_nom and sg_gen forms. Existing Russian display/flag provenance is retained byte-for-byte. Bounded application inference: classify these already admitted nouns by their recorded behavior, without producing new Russian forms or teaching a general ending-based guess. Independently authored English on 2026-10-12 within content-audit-2026-10-08; no primary prose or owned-book text copied; final semantic audit pending.
Recognize the behavior
shippedText=Recognize the behavior
transformation=content-audit research synthesis
licenseId=Original app text
licenseUrl=urn:russian-companion:license:original-app-text
attribution=Russian Companion content-audit research synthesis, content-audit-2026-10-08. No owned-book prose is included.
shareAlikeTreatment=Original research synthesis authored for this companion. It is not a CC BY-SA quotation. Application code, user data, and user recordings are outside this statement.
sourceHash=602cbb0d2b67ab9bad2e7f63b3a829e43441032d9d699fab45291d1698f0a0cb
contentHash=b7104c04e2c0d2868f16df61ca040bfda17fa0dfb5e15626454390213e523303
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-ca-u5-behavior-teach-4-body`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-orient, u5-s-indeclinable
- Source: src-content-audit-research
- Rights: rights-content-audit-research
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 840a1813a78ecd9c980e245d1f5ec682aa590d5b74f205b52f8170a7a6987ca8

recordType=label
sourceUrl=urn:russian-companion:content-audit-research:2026-10-08
sourceRevision=content-audit-2026-10-08
sourceRecordId=ca-coverage:obj-u5-indeclinable:behavior:teach-4-body
originalSourceText=Retained Wikibooks Russian/Grammar/Nominative oldid4213267, prov-u5-indecl-rule: indeclinable means no change according to case or number. Retained OpenRussian nouns.csv at commit50e210c4803237779cb562bc1abcea529066031c: indeclinable=1 for радио,такси,метро,кино,пальто,кофе,кафе,меню; indeclinable=0 for книга,окно. The latter rows have different sg_nom and sg_gen forms. Existing Russian display/flag provenance is retained byte-for-byte. Bounded application inference: classify these already admitted nouns by their recorded behavior, without producing new Russian forms or teaching a general ending-based guess. Independently authored English on 2026-10-12 within content-audit-2026-10-08; no primary prose or owned-book text copied; final semantic audit pending.
Contrast кни́га and окно́: these nouns are declinable, so they can change for case. Their nominative and genitive forms differ. Choose the behavior of the shown noun: unchanged for case or number, or able to change for case. No new form needs to be invented.
shippedText=Contrast кни́га and окно́: these nouns are declinable, so they can change for case. Their nominative and genitive forms differ. Choose the behavior of the shown noun: unchanged for case or number, or able to change for case. No new form needs to be invented.
transformation=content-audit research synthesis
licenseId=Original app text
licenseUrl=urn:russian-companion:license:original-app-text
attribution=Russian Companion content-audit research synthesis, content-audit-2026-10-08. No owned-book prose is included.
shareAlikeTreatment=Original research synthesis authored for this companion. It is not a CC BY-SA quotation. Application code, user data, and user recordings are outside this statement.
sourceHash=71b1826d9ba8d8973203d1b5ba3e33b0d6a40a5e4c45d569587218dab62b058d
contentHash=92f90ea31543d60c4b3f123429eed606e5e0eb19d2bc93c2abe2395e1e88ae0b
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-ca-u5-behavior-teach-4-title`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-orient, u5-s-indeclinable
- Source: src-content-audit-research
- Rights: rights-content-audit-research
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: f74b5b3eae32cc03d5071f71e66c9c81d76942f40b4ea675a1537068c6eba0a0

recordType=label
sourceUrl=urn:russian-companion:content-audit-research:2026-10-08
sourceRevision=content-audit-2026-10-08
sourceRecordId=ca-coverage:obj-u5-indeclinable:behavior:teach-4-title
originalSourceText=Retained Wikibooks Russian/Grammar/Nominative oldid4213267, prov-u5-indecl-rule: indeclinable means no change according to case or number. Retained OpenRussian nouns.csv at commit50e210c4803237779cb562bc1abcea529066031c: indeclinable=1 for радио,такси,метро,кино,пальто,кофе,кафе,меню; indeclinable=0 for книга,окно. The latter rows have different sg_nom and sg_gen forms. Existing Russian display/flag provenance is retained byte-for-byte. Bounded application inference: classify these already admitted nouns by their recorded behavior, without producing new Russian forms or teaching a general ending-based guess. Independently authored English on 2026-10-12 within content-audit-2026-10-08; no primary prose or owned-book text copied; final semantic audit pending.
Contrast nouns that can change
shippedText=Contrast nouns that can change
transformation=content-audit research synthesis
licenseId=Original app text
licenseUrl=urn:russian-companion:license:original-app-text
attribution=Russian Companion content-audit research synthesis, content-audit-2026-10-08. No owned-book prose is included.
shareAlikeTreatment=Original research synthesis authored for this companion. It is not a CC BY-SA quotation. Application code, user data, and user recordings are outside this statement.
sourceHash=9273a0cb078753bc250ce595cf0dcd021f76ee5800c5d1245cc8e226a8c2a2c5
contentHash=5797dea7b46b2c0911007726b56701557e9f7d3ea2388f588e1d23a336005482
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-ca-u5-behavior-u5-i-decl-книга-feedback`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-orient, u5-s-indeclinable
- Source: src-content-audit-research
- Rights: rights-content-audit-research
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: ec64116668e8768d0e6306fc6fcba78425dacb6f8c56766de9ad3beffb484998

recordType=label
sourceUrl=urn:russian-companion:content-audit-research:2026-10-08
sourceRevision=content-audit-2026-10-08
sourceRecordId=ca-coverage:obj-u5-indeclinable:behavior:u5-i-decl-книга-feedback
originalSourceText=Retained Wikibooks Russian/Grammar/Nominative oldid4213267, prov-u5-indecl-rule: indeclinable means no change according to case or number. Retained OpenRussian nouns.csv at commit50e210c4803237779cb562bc1abcea529066031c: indeclinable=1 for радио,такси,метро,кино,пальто,кофе,кафе,меню; indeclinable=0 for книга,окно. The latter rows have different sg_nom and sg_gen forms. Existing Russian display/flag provenance is retained byte-for-byte. Bounded application inference: classify these already admitted nouns by their recorded behavior, without producing new Russian forms or teaching a general ending-based guess. Independently authored English on 2026-10-12 within content-audit-2026-10-08; no primary prose or owned-book text copied; final semantic audit pending.
кни́га is declinable: it can change for case. Its nominative and genitive forms differ, so the unchanged behavior does not describe this noun.
shippedText=кни́га is declinable: it can change for case. Its nominative and genitive forms differ, so the unchanged behavior does not describe this noun.
transformation=content-audit research synthesis
licenseId=Original app text
licenseUrl=urn:russian-companion:license:original-app-text
attribution=Russian Companion content-audit research synthesis, content-audit-2026-10-08. No owned-book prose is included.
shareAlikeTreatment=Original research synthesis authored for this companion. It is not a CC BY-SA quotation. Application code, user data, and user recordings are outside this statement.
sourceHash=01b7cb1640a38726e35177bd40d03a6ae16490718576b00905d0d9905f7f7f28
contentHash=a9701a214ce6d2dea0f305c2d3c5f6ea9fc063c74c5113a6940a638bfe7856a9
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-ca-u5-behavior-u5-i-decl-окно-feedback`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-orient, u5-s-indeclinable
- Source: src-content-audit-research
- Rights: rights-content-audit-research
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 71239cc4ba287833c416492023bcfe0000276b208107ca4ff7116ecde02e5113

recordType=label
sourceUrl=urn:russian-companion:content-audit-research:2026-10-08
sourceRevision=content-audit-2026-10-08
sourceRecordId=ca-coverage:obj-u5-indeclinable:behavior:u5-i-decl-окно-feedback
originalSourceText=Retained Wikibooks Russian/Grammar/Nominative oldid4213267, prov-u5-indecl-rule: indeclinable means no change according to case or number. Retained OpenRussian nouns.csv at commit50e210c4803237779cb562bc1abcea529066031c: indeclinable=1 for радио,такси,метро,кино,пальто,кофе,кафе,меню; indeclinable=0 for книга,окно. The latter rows have different sg_nom and sg_gen forms. Existing Russian display/flag provenance is retained byte-for-byte. Bounded application inference: classify these already admitted nouns by their recorded behavior, without producing new Russian forms or teaching a general ending-based guess. Independently authored English on 2026-10-12 within content-audit-2026-10-08; no primary prose or owned-book text copied; final semantic audit pending.
окно́ is declinable: it can change for case. Its nominative and genitive forms differ, so the unchanged behavior does not describe this noun.
shippedText=окно́ is declinable: it can change for case. Its nominative and genitive forms differ, so the unchanged behavior does not describe this noun.
transformation=content-audit research synthesis
licenseId=Original app text
licenseUrl=urn:russian-companion:license:original-app-text
attribution=Russian Companion content-audit research synthesis, content-audit-2026-10-08. No owned-book prose is included.
shareAlikeTreatment=Original research synthesis authored for this companion. It is not a CC BY-SA quotation. Application code, user data, and user recordings are outside this statement.
sourceHash=815fd98bfdaca1e8d64c075073ba1bdef75def448dbaef80c698af5256761ce9
contentHash=06809529845d4946a5199ad061e5de02aa6afc02e5b9795ddd4456b0a390d319
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-ca-u5-behavior-u5-i-indecl-кино-feedback`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-orient, u5-s-indeclinable
- Source: src-content-audit-research
- Rights: rights-content-audit-research
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 9d0ae4233b60933d5ef03861f646527bbf4eab254bda2e18ecc2ee003a2a2562

recordType=label
sourceUrl=urn:russian-companion:content-audit-research:2026-10-08
sourceRevision=content-audit-2026-10-08
sourceRecordId=ca-coverage:obj-u5-indeclinable:behavior:u5-i-indecl-кино-feedback
originalSourceText=Retained Wikibooks Russian/Grammar/Nominative oldid4213267, prov-u5-indecl-rule: indeclinable means no change according to case or number. Retained OpenRussian nouns.csv at commit50e210c4803237779cb562bc1abcea529066031c: indeclinable=1 for радио,такси,метро,кино,пальто,кофе,кафе,меню; indeclinable=0 for книга,окно. The latter rows have different sg_nom and sg_gen forms. Existing Russian display/flag provenance is retained byte-for-byte. Bounded application inference: classify these already admitted nouns by their recorded behavior, without producing new Russian forms or teaching a general ending-based guess. Independently authored English on 2026-10-12 within content-audit-2026-10-08; no primary prose or owned-book text copied; final semantic audit pending.
кино́ is indeclinable: its form stays unchanged for case or number. Choose the unchanged behavior, rather than the behavior of a declinable noun.
shippedText=кино́ is indeclinable: its form stays unchanged for case or number. Choose the unchanged behavior, rather than the behavior of a declinable noun.
transformation=content-audit research synthesis
licenseId=Original app text
licenseUrl=urn:russian-companion:license:original-app-text
attribution=Russian Companion content-audit research synthesis, content-audit-2026-10-08. No owned-book prose is included.
shareAlikeTreatment=Original research synthesis authored for this companion. It is not a CC BY-SA quotation. Application code, user data, and user recordings are outside this statement.
sourceHash=b0018f5d295c555a5b741a6f4a18857ab681a48bbcde18f88d0c06c3737adfc7
contentHash=84aede3c7883962318fd0701830a8bc9d256e634fd19a6fa6ee6e92271d3b652
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-ca-u5-behavior-u5-i-indecl-метро-feedback`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-orient, u5-s-indeclinable
- Source: src-content-audit-research
- Rights: rights-content-audit-research
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: f4cd1fd973a1ca98c53bc53ef2cabf5e0d268d4170cb38640020d0a9a0528f0b

recordType=label
sourceUrl=urn:russian-companion:content-audit-research:2026-10-08
sourceRevision=content-audit-2026-10-08
sourceRecordId=ca-coverage:obj-u5-indeclinable:behavior:u5-i-indecl-метро-feedback
originalSourceText=Retained Wikibooks Russian/Grammar/Nominative oldid4213267, prov-u5-indecl-rule: indeclinable means no change according to case or number. Retained OpenRussian nouns.csv at commit50e210c4803237779cb562bc1abcea529066031c: indeclinable=1 for радио,такси,метро,кино,пальто,кофе,кафе,меню; indeclinable=0 for книга,окно. The latter rows have different sg_nom and sg_gen forms. Existing Russian display/flag provenance is retained byte-for-byte. Bounded application inference: classify these already admitted nouns by their recorded behavior, without producing new Russian forms or teaching a general ending-based guess. Independently authored English on 2026-10-12 within content-audit-2026-10-08; no primary prose or owned-book text copied; final semantic audit pending.
метро́ is indeclinable: its form stays unchanged for case or number. Choose the unchanged behavior, rather than the behavior of a declinable noun.
shippedText=метро́ is indeclinable: its form stays unchanged for case or number. Choose the unchanged behavior, rather than the behavior of a declinable noun.
transformation=content-audit research synthesis
licenseId=Original app text
licenseUrl=urn:russian-companion:license:original-app-text
attribution=Russian Companion content-audit research synthesis, content-audit-2026-10-08. No owned-book prose is included.
shareAlikeTreatment=Original research synthesis authored for this companion. It is not a CC BY-SA quotation. Application code, user data, and user recordings are outside this statement.
sourceHash=da67d98291efc686640e2e0b88162e322e576618a9421f6e63f7131b5d305bd4
contentHash=e8ba94db74bcb042f744ea27d70bf030eeae929255692920016e3e83cc012408
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-ca-u5-behavior-u5-i-indecl-радио-feedback`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-orient, u5-s-indeclinable
- Source: src-content-audit-research
- Rights: rights-content-audit-research
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 1fa795549ca96f30bf0327e7b6834ad3443385d10265e879e5773bc40b9a1c58

recordType=label
sourceUrl=urn:russian-companion:content-audit-research:2026-10-08
sourceRevision=content-audit-2026-10-08
sourceRecordId=ca-coverage:obj-u5-indeclinable:behavior:u5-i-indecl-радио-feedback
originalSourceText=Retained Wikibooks Russian/Grammar/Nominative oldid4213267, prov-u5-indecl-rule: indeclinable means no change according to case or number. Retained OpenRussian nouns.csv at commit50e210c4803237779cb562bc1abcea529066031c: indeclinable=1 for радио,такси,метро,кино,пальто,кофе,кафе,меню; indeclinable=0 for книга,окно. The latter rows have different sg_nom and sg_gen forms. Existing Russian display/flag provenance is retained byte-for-byte. Bounded application inference: classify these already admitted nouns by their recorded behavior, without producing new Russian forms or teaching a general ending-based guess. Independently authored English on 2026-10-12 within content-audit-2026-10-08; no primary prose or owned-book text copied; final semantic audit pending.
ра́дио is indeclinable: its form stays unchanged for case or number. Choose the unchanged behavior, rather than the behavior of a declinable noun.
shippedText=ра́дио is indeclinable: its form stays unchanged for case or number. Choose the unchanged behavior, rather than the behavior of a declinable noun.
transformation=content-audit research synthesis
licenseId=Original app text
licenseUrl=urn:russian-companion:license:original-app-text
attribution=Russian Companion content-audit research synthesis, content-audit-2026-10-08. No owned-book prose is included.
shareAlikeTreatment=Original research synthesis authored for this companion. It is not a CC BY-SA quotation. Application code, user data, and user recordings are outside this statement.
sourceHash=2df31a39f4bb67a8cd8670c5a55a7d1e14a6850f96f4de42d860cc22d96f4053
contentHash=7231fe6b730cb5c92d05a16c33b3936457b2c30f56bddd11d317fbd9a2088034
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-ca-u5-behavior-u5-i-indecl-такси-feedback`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-orient, u5-s-indeclinable
- Source: src-content-audit-research
- Rights: rights-content-audit-research
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: d14917b2b496ac9077ab94a2507b46bf97f892ddd8dc0018aa8f5477290fb913

recordType=label
sourceUrl=urn:russian-companion:content-audit-research:2026-10-08
sourceRevision=content-audit-2026-10-08
sourceRecordId=ca-coverage:obj-u5-indeclinable:behavior:u5-i-indecl-такси-feedback
originalSourceText=Retained Wikibooks Russian/Grammar/Nominative oldid4213267, prov-u5-indecl-rule: indeclinable means no change according to case or number. Retained OpenRussian nouns.csv at commit50e210c4803237779cb562bc1abcea529066031c: indeclinable=1 for радио,такси,метро,кино,пальто,кофе,кафе,меню; indeclinable=0 for книга,окно. The latter rows have different sg_nom and sg_gen forms. Existing Russian display/flag provenance is retained byte-for-byte. Bounded application inference: classify these already admitted nouns by their recorded behavior, without producing new Russian forms or teaching a general ending-based guess. Independently authored English on 2026-10-12 within content-audit-2026-10-08; no primary prose or owned-book text copied; final semantic audit pending.
такси́ is indeclinable: its form stays unchanged for case or number. Choose the unchanged behavior, rather than the behavior of a declinable noun.
shippedText=такси́ is indeclinable: its form stays unchanged for case or number. Choose the unchanged behavior, rather than the behavior of a declinable noun.
transformation=content-audit research synthesis
licenseId=Original app text
licenseUrl=urn:russian-companion:license:original-app-text
attribution=Russian Companion content-audit research synthesis, content-audit-2026-10-08. No owned-book prose is included.
shareAlikeTreatment=Original research synthesis authored for this companion. It is not a CC BY-SA quotation. Application code, user data, and user recordings are outside this statement.
sourceHash=67784717ba5c84860c6ccfa2018651b64796d1e64d04a8c5fe46bd7b833809f3
contentHash=84ba90f3fd5a1a603556754a283096e3ed3b353c6abee8c290a5579dabbcbc59
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-ca-u5-behavior-u5-st-check-indeclinable-body`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-orient, u5-s-indeclinable
- Source: src-content-audit-research
- Rights: rights-content-audit-research
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 81e678c356806ed87b0586dec2350afea5f0a317e02cfbef522c42581ff820ef

recordType=label
sourceUrl=urn:russian-companion:content-audit-research:2026-10-08
sourceRevision=content-audit-2026-10-08
sourceRecordId=ca-coverage:obj-u5-indeclinable:behavior:u5-st-check-indeclinable-body
originalSourceText=Retained Wikibooks Russian/Grammar/Nominative oldid4213267, prov-u5-indecl-rule: indeclinable means no change according to case or number. Retained OpenRussian nouns.csv at commit50e210c4803237779cb562bc1abcea529066031c: indeclinable=1 for радио,такси,метро,кино,пальто,кофе,кафе,меню; indeclinable=0 for книга,окно. The latter rows have different sg_nom and sg_gen forms. Existing Russian display/flag provenance is retained byte-for-byte. Bounded application inference: classify these already admitted nouns by their recorded behavior, without producing new Russian forms or teaching a general ending-based guess. Independently authored English on 2026-10-12 within content-audit-2026-10-08; no primary prose or owned-book text copied; final semantic audit pending.
Classify the shown noun by its case behavior. A miss returns to all four explanations and the retained examples.
shippedText=Classify the shown noun by its case behavior. A miss returns to all four explanations and the retained examples.
transformation=content-audit research synthesis
licenseId=Original app text
licenseUrl=urn:russian-companion:license:original-app-text
attribution=Russian Companion content-audit research synthesis, content-audit-2026-10-08. No owned-book prose is included.
shareAlikeTreatment=Original research synthesis authored for this companion. It is not a CC BY-SA quotation. Application code, user data, and user recordings are outside this statement.
sourceHash=822e3dd7fd3063b2d60fac88575218138a99c4684a69691ec2528533fc28de88
contentHash=de04f91536547185f524814dafd7a144038bdcc3e245dbc4e273c1a673a028cf
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-ca-u5-behavior-u5-st-check-indeclinable-title`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-orient, u5-s-indeclinable
- Source: src-content-audit-research
- Rights: rights-content-audit-research
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 9db756a542313620be0bea7f97f59f3087c68d795548cf4de8f785535e23785f

recordType=label
sourceUrl=urn:russian-companion:content-audit-research:2026-10-08
sourceRevision=content-audit-2026-10-08
sourceRecordId=ca-coverage:obj-u5-indeclinable:behavior:u5-st-check-indeclinable-title
originalSourceText=Retained Wikibooks Russian/Grammar/Nominative oldid4213267, prov-u5-indecl-rule: indeclinable means no change according to case or number. Retained OpenRussian nouns.csv at commit50e210c4803237779cb562bc1abcea529066031c: indeclinable=1 for радио,такси,метро,кино,пальто,кофе,кафе,меню; indeclinable=0 for книга,окно. The latter rows have different sg_nom and sg_gen forms. Existing Russian display/flag provenance is retained byte-for-byte. Bounded application inference: classify these already admitted nouns by their recorded behavior, without producing new Russian forms or teaching a general ending-based guess. Independently authored English on 2026-10-12 within content-audit-2026-10-08; no primary prose or owned-book text copied; final semantic audit pending.
Independent noun classification
shippedText=Independent noun classification
transformation=content-audit research synthesis
licenseId=Original app text
licenseUrl=urn:russian-companion:license:original-app-text
attribution=Russian Companion content-audit research synthesis, content-audit-2026-10-08. No owned-book prose is included.
shareAlikeTreatment=Original research synthesis authored for this companion. It is not a CC BY-SA quotation. Application code, user data, and user recordings are outside this statement.
sourceHash=76f5a3287bb03ed8695616b965c50d8d2a40083da298786cbcd31128aabb7457
contentHash=09a37283baeb2a13959dbcf927fdc90e13838017bc6b05a4eb77efa05f0809ed
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-ca-u5-behavior-u5-st-guided-indeclinable-1-body`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-orient, u5-s-indeclinable
- Source: src-content-audit-research
- Rights: rights-content-audit-research
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 5bb7429cc28308cf05db1ff5a02f82a97b3b48f67fbb4cc4f39bf56683f24162

recordType=label
sourceUrl=urn:russian-companion:content-audit-research:2026-10-08
sourceRevision=content-audit-2026-10-08
sourceRecordId=ca-coverage:obj-u5-indeclinable:behavior:u5-st-guided-indeclinable-1-body
originalSourceText=Retained Wikibooks Russian/Grammar/Nominative oldid4213267, prov-u5-indecl-rule: indeclinable means no change according to case or number. Retained OpenRussian nouns.csv at commit50e210c4803237779cb562bc1abcea529066031c: indeclinable=1 for радио,такси,метро,кино,пальто,кофе,кафе,меню; indeclinable=0 for книга,окно. The latter rows have different sg_nom and sg_gen forms. Existing Russian display/flag provenance is retained byte-for-byte. Bounded application inference: classify these already admitted nouns by their recorded behavior, without producing new Russian forms or teaching a general ending-based guess. Independently authored English on 2026-10-12 within content-audit-2026-10-08; no primary prose or owned-book text copied; final semantic audit pending.
Choose how the shown noun behaves. Read the explanation after choosing.
shippedText=Choose how the shown noun behaves. Read the explanation after choosing.
transformation=content-audit research synthesis
licenseId=Original app text
licenseUrl=urn:russian-companion:license:original-app-text
attribution=Russian Companion content-audit research synthesis, content-audit-2026-10-08. No owned-book prose is included.
shareAlikeTreatment=Original research synthesis authored for this companion. It is not a CC BY-SA quotation. Application code, user data, and user recordings are outside this statement.
sourceHash=1951e30ed9316bc50f0b94f5ab319eac6c2d7e7c4fc38e7839ad2e42aad59b3f
contentHash=fbc101e3d5ef398cee71888ead7bfcda0539feaf6e4089a836153deceb15e61a
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-ca-u5-behavior-u5-st-guided-indeclinable-1-title`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-orient, u5-s-indeclinable
- Source: src-content-audit-research
- Rights: rights-content-audit-research
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 32b2d22fb1b209d6d8c0fddafcb596c2075197f26a1f9bd33130bdddc917df46

recordType=label
sourceUrl=urn:russian-companion:content-audit-research:2026-10-08
sourceRevision=content-audit-2026-10-08
sourceRecordId=ca-coverage:obj-u5-indeclinable:behavior:u5-st-guided-indeclinable-1-title
originalSourceText=Retained Wikibooks Russian/Grammar/Nominative oldid4213267, prov-u5-indecl-rule: indeclinable means no change according to case or number. Retained OpenRussian nouns.csv at commit50e210c4803237779cb562bc1abcea529066031c: indeclinable=1 for радио,такси,метро,кино,пальто,кофе,кафе,меню; indeclinable=0 for книга,окно. The latter rows have different sg_nom and sg_gen forms. Existing Russian display/flag provenance is retained byte-for-byte. Bounded application inference: classify these already admitted nouns by their recorded behavior, without producing new Russian forms or teaching a general ending-based guess. Independently authored English on 2026-10-12 within content-audit-2026-10-08; no primary prose or owned-book text copied; final semantic audit pending.
Guided noun classification
shippedText=Guided noun classification
transformation=content-audit research synthesis
licenseId=Original app text
licenseUrl=urn:russian-companion:license:original-app-text
attribution=Russian Companion content-audit research synthesis, content-audit-2026-10-08. No owned-book prose is included.
shareAlikeTreatment=Original research synthesis authored for this companion. It is not a CC BY-SA quotation. Application code, user data, and user recordings are outside this statement.
sourceHash=a30674867b8df713c4d5ff87b61edaded37bd6dec4453b41c5d4226ad56c63b8
contentHash=786866c3e6689bfca57afc9a9f524e31bc3935f32268d109a756b1def986c2e8
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-ca-u5-behavior-unchanged-choice`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-orient, u5-s-indeclinable
- Source: src-content-audit-research
- Rights: rights-content-audit-research
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: e6fb0f0da2e47d060bbec27a1b0a6cb2ab47f58fc210818ed978aa0dc5a9d891

recordType=label
sourceUrl=urn:russian-companion:content-audit-research:2026-10-08
sourceRevision=content-audit-2026-10-08
sourceRecordId=ca-coverage:obj-u5-indeclinable:behavior:unchanged-choice
originalSourceText=Retained Wikibooks Russian/Grammar/Nominative oldid4213267, prov-u5-indecl-rule: indeclinable means no change according to case or number. Retained OpenRussian nouns.csv at commit50e210c4803237779cb562bc1abcea529066031c: indeclinable=1 for радио,такси,метро,кино,пальто,кофе,кафе,меню; indeclinable=0 for книга,окно. The latter rows have different sg_nom and sg_gen forms. Existing Russian display/flag provenance is retained byte-for-byte. Bounded application inference: classify these already admitted nouns by their recorded behavior, without producing new Russian forms or teaching a general ending-based guess. Independently authored English on 2026-10-12 within content-audit-2026-10-08; no primary prose or owned-book text copied; final semantic audit pending.
Indeclinable: unchanged for case or number
shippedText=Indeclinable: unchanged for case or number
transformation=content-audit research synthesis
licenseId=Original app text
licenseUrl=urn:russian-companion:license:original-app-text
attribution=Russian Companion content-audit research synthesis, content-audit-2026-10-08. No owned-book prose is included.
shareAlikeTreatment=Original research synthesis authored for this companion. It is not a CC BY-SA quotation. Application code, user data, and user recordings are outside this statement.
sourceHash=1906583af4e058ec00ce9eeabc8566c69f5213001340c5addca35857a844d88d
contentHash=82dacfa055789278c7de8ec0724ae165e14bc8acbbcb7a59960f05e3aa20f1df
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### PROVENANCE `prov-u5-contrast-flag-книга`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-orient, u5-s-indeclinable
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 1246f14a45bd072a0bcd75b44d676778e14cec73c094cf522fe27787409acd9a

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 9a7debf0d68d32b23e522ec38b18c66c392c49843cf4fce28e830424369535c2

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 02ef0e0c8ea242813f7b4f9fb459e15e2349ef80d62611f5441ef24e0d1a594d

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 6d1809b76d596733b640311f5a7cafd614f2be66b01a042dec78470f591a46f7

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: a2312d31ee034ed7aebe12c9d20210561c937505246a72e02fe93d47a6682ff3

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 96188964b5055aee2d8d2df8a55f4d11dece02d506211d83ef18d6bb977890c8

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 8cd2091562076528467ccdd0833fc8e8d236067e56952fd16761bf071404b7c5

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 004dcd2d5c5b4b68862ea8a7fa3cfe8d8aa02006f0c6c5bd955e70bf3426d29a

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 8826372581857e23f99ba39de82bd7af3c675902737822328b8cc1c38db031ed

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 4c79a2d12aa636191fd4d890574ea1bd9f1356c54de099ee98acbcc8d896e095

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: a52fff8893891b2c095ca3555e9196427fcd5f599490353e7b6d32a24a4f74f8

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: a0faefb315b7d27b10cc9bcac2b25b650f4f0778c4a2849f4428306a6dad0653

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 6f1d24f83accd6f8a806e6abbf1cc0f5f94a18cc899236b227dc004f2e1498d2

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 3fd17e7bafb8a505f26e9acaa20517e24c1a207e2490fbe2a3dee36da55c94f3

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 5b31bb5912e9f1c87e7f6c90b1d6b43334fcbced7bf790b9c03a30c7ad6b15e6

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 44d1f8a02a4d1b0253fcd8b49b2e64d70805ec1db665ea6d06a2d1daff5078f6

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 50eac777822bc5bdf630eeee7e675b4ca040b5bd3df9b9ea4845340d05236dcb

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: cbb7bcd512ef90345f44c343bc87ee7cc37c7f006e68c43b1ddac442ba830a92

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: a6302b8412e6b638ea02cd45407b27896e0777d987df130d9f83662ec911b173

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 9be6538e41f2bf7d0318dd16541abbd0b56bedf727fba97217336653769dd248

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 1666a015ba4b43976c5b5c1126fbf74713f2cfd8a40d2fe61c2d15a296411e78

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 7839f6bd02bae2f6f08b672f06b8dd3c30cf0844eee16ab5893cb2e36e355b2b

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 29b110e13657867ed49db2a9ee96b71a7e35a7ce36a77e31b4c73907e1332690

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 11aae2c34a025e9570c0d65b3c7d1ece96a0c789d912215e12bc37bdf115df9b

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: dbade79ccb7b5def67810fde9f6c82dab3c7b37ddf141e4915021026d53002fe

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 69e7fdfbf266417014ecccb9677f17d6162b08751eb8960131f5d317106118d9

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: ab2fb871ad1b767efd19b6fda9a3d734f49041b2bea3a93a0546b1169e9a7afa

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: f288d991f956f4ac9769346ecf6cb76cef9c5bb4b6d5b2f0f9bd02085d64930f

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 9a4dd60da4e194651df36e3540ed1470f8179e8d557772391bab75a542966c17

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: f13b4a3528ca05ae979b4bd0b0c181e33a43cc80127f2f95156a92c4685ea912

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 5d2b98471a06bd71032b0d6ffe0d892ad46e4b8e55ec847e1e7136288889233d

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 43f1449482e5ad82e1d02e4592f5914b437d3890feda0101929fb5b8a55eab42

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 5dc199d8c658c9896760bc950c1fe0520ab98d28ba2667d985804a7f315e9204

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 13ee312c30b7a8429937ff525c4ccda751ca9dc1f4afeb1523afa6e5e5bfec3d

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 011214de96de978cb5ff04f126f2d004ee4388426582ed1283b3c354fe75078a

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 65a6250d22563d21e3cfcb2cb7f7a33c57914cc160c8074a656831ecddd0e4a6

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 92f89a513eba1fbfdaecd9981011e4513f67f738ddfdb28caa57fc7bfd88a9a5

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: b907167e2cef5d32351e4ec922301823122b044da6fb083999773c27d2b16ba3

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: a0d3d511bd2ef36c3b6afd89cd7d2bc0fb2e52cb4c04f6818deba09dad23b40b

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: c06eaf0c93a8df646ec3b34b9b26e725423bb59e3475fda559cb34b7ba4a8679

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 5ca926ed37b7ea666234205659116643d80b29562fe7d6542acdeb27f77b01b3

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 8278823a32cfb7d87739cef92b1411d526ef92efff5c34d2555745d8de03f006

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 3120da29b8945593c64afe5bd126c313c0835746a5dbc9c3754451e6742c7b13

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 0d826e4605acd6a2bde52493c32018bb8e49f9ef2f1e25e3e38ebafb613ac596

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: bb5ca09273082dcd0a8f191ea1560a41869271a93cef84ab967ab2cc9265e743

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: ac8c3c2997c7470aef34586b9944eb667e6a6649845ada1dab237cfaf3dc00b6

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 9841e29fd63ffe8fad69b1ae20cb8c599fe5c59cf23cbcbd1ecced7234702762

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 32b0c874c413305f9e2f53da512d324b0577e3ce942f65726553f298f7cdeccf

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 25d45f3e85c8109bf6c0dfebe6f6fef99f2be765911ad7d13532f3c554d7ea71

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 4db0b31b234a02298cbee80993aa29aea718ab6f5d239934a728755cf58c63b7

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 73295d2c130590fc85250629f832b807b7d24359a93b5ba59db0e03c16175898

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 83c6f6735da84e14e8f3857d738495a4f9f0be91eafecb30136bedbf7c2a0189

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 13902b213a4d42fc8fca8b91d8ccd07a5e88c78b17c1b245cb5269cec71abaa4

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: f6eabf5cf6592183ca0cd8bc7dc10ddb1b0def165003af51500c5b825b205fe8

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 8ebd11e4bfb671eb5a9a128d18d68a2d112ff3b9edabe5328ab33b4038e3dcbb

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 661d9c5443b71aab41a82702960bc83c3d0b379dc3d6634f15cc20f1c102f084

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: d99b2e780f6caa9ab4d6289c416faaf2d80af1e137e6fa2fcbd98dc8bb404f58

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 091f579dd8d74fb5d9d0dc953c50f0b03d60a319f8ea8dd0ad010536953830af

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 1220f4269dfed9b7e153680696edf27981af7a659abaa224b133f9985ad90e02

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 485a635fb5ec875af5f9fd576c7683d10b8f1174f9496eb07db508f0afdca301

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 1ccd8b613f2a941d8a29714a53e8111aa764f034bde1b35e348d90208cb1a1f8

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: bc25546af483d4995d5dbcf9da5f2b70202c8ba27a5dd4652676f571c62c2c83

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: b5e72aee127109e81518804dfa28d2489bbe6607597a348cfcfdc11ae2a61113

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: ec06a8baa79f7380ce26f4f0b67f70f41fca2d6c0f2c489830361d9910e41e31

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 9a788e5719deaea9f57a0057e073b7293d580b86c7e90a7ea57d0162282f7bef

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 75af98c1eca9e4d0f89b4ec4621a65eaf70d977855c6278a86972bde5e0a3f14

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: bf804fba8059ef0f6b440d7b1709346c7740cfb62ad42762a898db8d642fb113

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: a9dd17de5d5f8a7edf147f2c6e0f0ebe061c5d7d9185141683c8ffe0ef37fda8

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: b60053ed737f6b363230f5a0878e6131adf5f4705040f50a5a1c6077b3c8b9d5

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 9bb5b547f1893caa8321917602e7b525affae6eb984090fb2fad4657b03edf4f

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: f0f8cf16bcfaa5d9acf6389172d4a772e9c0ad5b970a718767e359974f4354cd

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 7498e9ec18688c7f3076eefeffa358cb5865d2df3f2fa46866844c5c5726cd65

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 414bf310177207dd365bfe50c81fb1abc68e526664a1eae0649d792e74e5943c

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: a057372c552cddef1a9a6cd84630cf69b81cbb8d93525a362d0d2c5e10f1eebb

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: eed424eea90ccad8dbe80447d3a0c424bd916ff9b164dd29413eb152ed6cbdf5

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: e091efa2665bcf345fc7c4e439088dc8a4b14c5771f6cb051431ebecaa5eca2c

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 0c37bb8aa83f4b0f546c6a643eac6b4e6eb1e642418adf59f21771ce3db4c576

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 41726507ab85cb810eb5e5d972c6fb326a3cef6413922078a53bcb2ac7aedc43

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: dc8901bd3cac0fd33e3f763f006a3c43701c85a4dfcd668fea8184bf7adf7f23

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: c0cee7d9aa78cded81cd67efe92bad249948ec9bac613ebb91da6c040bcbbb7d

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: a2edee5bf63b43c3a7dcbeee357461310aa2f2eb16ffca5ec8920155dbaabfae

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: e97c068add4261bd7183b505e9246638979365849de130ea0ae923b0aec12115

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: bc01cb5a10d4587a4dd6c380cb234165a62f2a68ae0f6a0d6427ac982376937f

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 0056a16c724a957f234b821c9ca29175a82ef8642824574ada31d0024a65149c

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 51c8b8ab9dae36d7ecd7573176ece5517847b7849b18b0929c3c67342c163285

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 0767ef78a1160c352a119426787e3959e9d0e89def0badf5f768981e6733de83

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 2b820e01971e1250921c76d57a4e09df964d7069d5ebe3b6d5a117c57c1d8cd1

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: cdfa87aa113d173d1d1dfe7c4d4f1125b1b4a148fb8773c9b73fdf5b3054ca12

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 2503d2b4c0e2e138370a7b11cb21b0146c105fdf7603ca02f9595b69d2dd2061

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 45d5932a9decec8d8544b16feede2386eff1ae9b3a94ecb182353384f01e103a

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: aed21595ced9c2f85a80626c2811b8e19ede92967420393f8d0864ed14d34f88

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 47c08e3e6688bc82e071c43dad0158ddd76daa479a96b5b99918aeb4cb7ada97

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 40a64e8920dbade21b784169f72db9c461289cc064d6ea77ac210b3ea87ca9cb

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
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: a8bcec7e92ebaa5b3e563c50addbaea94ceb4a1d5997832334b049c20d52bbcf

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

### AUTHORED_ENGLISH `en-ca-5-10`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: d1287dccfb60fc6bc00bd1b8e4a9b9fdc2cb292ff35fb3350ba18f11dbf95cfa

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Read this as a review-pending explanation. Worked examples come before the guided choice, and the independent check is separate. A wrong answer returns to this contrast. Activity is not demonstrated ability.

### AUTHORED_ENGLISH `en-ca-5-11141`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 5bfb35737e2cc2b797ed2aca075ef5a31261f6816ddc33f53c59403d30a245db

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Unit 5 AnkiDroid direction

### AUTHORED_ENGLISH `en-ca-5-11142`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: ee1dff3669d7c9cec219b851e119cc2f743a180e608ebc0865f9d9fcae7d09f8

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Self-attest AnkiDroid work. It remains user-reported and does not demonstrate a check.

### AUTHORED_ENGLISH `en-ca-5-11143`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: e18a06d76392882bd3297c80aadc9ed989ef9f1f3f4c8b7d98033c5c56338d1f

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Unit 5 binder prompt

### AUTHORED_ENGLISH `en-ca-5-11144`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 35460c967f4feb54d830d370ddbbe69889bda21a58f78c12936f6c070ae7c238

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=A binder prompt is one specific note, not a score.

### AUTHORED_ENGLISH `en-ca-5-11145`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 1a7fc7b4c502890ce81ea1fcf46171abbb8a5a8af4ef145956588f6da66f3bfb

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Unit 5 optional tutor

### AUTHORED_ENGLISH `en-ca-5-11146`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: c41953e780c7a5c4a32a6c0dd414499b5d9f7ed3ba5d02c604b697e0b972f94f

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Tutor work is optional and does not count as demonstrated ability.

### AUTHORED_ENGLISH `en-ca-5-11147`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: c4ac6ae00f92c6972aa41884e09cfb4f6eb183e2f2a10ffd51c165692ba4efd0

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Unit 5 cumulative review

### AUTHORED_ENGLISH `en-ca-5-8`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 0b9cedcbe160d9caef19eb16e9d13eef3c8414662f5b77ee09cf0bc4719f047d

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Owned-book assignment for Lesson 5: printed pages 46-55. PDF indexes 58-67 are file locators, not printed pages. Observed section locators: §5.1 p46 (PDF 58); §5.2 p46 (PDF 58); §5.3 p47 (PDF 59); §5.4 p47 (PDF 59); §5.5 p49 (PDF 61); §5.6 p49 (PDF 61); §5.7 p50 (PDF 62); §5.8 p50 (PDF 62); §5.9 p50 (PDF 62); §5.10 p51 (PDF 63); §5.11 p52 (PDF 64); §5.12 p52 (PDF 64). Observed exercise locators: 5/1 p54/PDF66; 5/2 p54/PDF66; 5/3 p54/PDF66; 5/4 p54/PDF66; 5/5 p54/PDF66. These locators are pending reviewer assignment and are not an approved mapping. Do the work in the copy you own. Book activity is not a demonstrated app check.

### AUTHORED_ENGLISH `en-ca-5-9`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 16d9485668dbdec6dbaffd33e75bc06f5c0beab64896166d937e345dd57c4337

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Unit 5 AnkiDroid self-attestation uses only cards you are allowed to make. Binder: write one form from this unit that you missed and the correction you will use. Tutor conversation is optional and is not a demonstrated check. Cumulative review revisits completed objectives from earlier units. Activity is not demonstrated ability. Open the offline grammar reference for this unit when one is linked.

### AUTHORED_ENGLISH `en-u5-098`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 99f212f4b39bdbde9cafea6ecba5e3300e18290fd6b8f2c29f25035502536a3a

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Which admitted question word is recorded for this English purpose: 

### AUTHORED_ENGLISH `en-u5-099`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: db0bad42add6ae3ed1c6c85f8b5919942ed07e86c00fdcb82abda909ff95e0f9

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=The frozen table records 

### AUTHORED_ENGLISH `en-u5-100`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: a9a3c4589458e271fdde762cf007601ba27773b6962a5d781ba407d50751ef5a

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text= for 

### AUTHORED_ENGLISH `en-u5-101`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 4fa99fd3a68d02f11ca398c9131541e9c433a1b06733bfce7508889a186b51f5

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=.

### AUTHORED_ENGLISH `en-u5-102`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 4ecefe901e5d115082c19ae34f9888e353a1a6831acc297a96a2d6168b43ccaf

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Which admitted meaning is recorded for the shown written question?

### AUTHORED_ENGLISH `en-u5-103`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: e1b14bed3ab13868b17b82b33f6159e88b2683e88f3e06fa591748c3e8aa3253

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=The frozen page records this meaning: 

### AUTHORED_ENGLISH `en-u5-104`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 6942abf003ac95fbbac3ab428a7405bfdadc3c766f6bbbd739f0a040e4920f8e

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=. Written punctuation is not listening practice.

### AUTHORED_ENGLISH `en-u5-105`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: ab685ce8652096024d421e16bdb1bceb8e3506f60e9ee3281470cd8ca8e7f5a1

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Which admitted description is recorded for the shown example?

### AUTHORED_ENGLISH `en-u5-106`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 8c4dd213052c5a646c59eb206628c5abb49db17ac426e1b03f556c511bdce7ae

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=The frozen page records this example as: 

### AUTHORED_ENGLISH `en-u5-107`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 9a8d073287a7fabb7a1c5c411949f5a1bcd9f69634fc8333c3bf5120b11e2da5

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Which string is the admitted second field, not the first field?

### AUTHORED_ENGLISH `en-u5-108`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 56bfc42716db4911087babc357b73f7e3bca15321c1385fca621b79c6a7a4534

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=The frozen row lists that field as 

### AUTHORED_ENGLISH `en-u5-109`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 51d6af60cd6ff0a61eedda2082d5983654a345c93131c5d7f1c38d18763ad14d

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Which flag does the admitted row record?

### AUTHORED_ENGLISH `en-u5-110`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 211582dc29c101a57977a6d15118965e4308e9e852478f045ecbc1e7d1bba561

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=The frozen row sets the flag to 

### AUTHORED_ENGLISH `en-u5-111`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 8b2c863df507cc38e42cb9f53f7b1f68a46b7183db9fd57bc03a0aae0285f4cf

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=These words are copied from the frozen table. 

### AUTHORED_ENGLISH `en-u5-112`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: ad9697e8e2f0765b2a1f2157dc4b0740bb3d0b77a5d1675db62c33fe4461c4e2

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Four question words

### AUTHORED_ENGLISH `en-u5-113`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 0c4aa61ab3d335a6de1029c67ce073671aadad50313e5ec60c77515986d8e6c0

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Three more question words

### AUTHORED_ENGLISH `en-u5-114`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 98fa24f1973eb7d6aac3228b5f68a960e5925dd6e41d5380d8445d6cc07e4b26

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Three further question words

### AUTHORED_ENGLISH `en-u5-115`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 7ae55220884e1d760e3d3755e58f0cb65d9fee25242bacfe267694082562a3ca

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=These written questions are exact frozen excerpts. 

### AUTHORED_ENGLISH `en-u5-116`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 24dff9eeed2de476393bd6761c76ba0565d1c91a6293d7aa9ad03bd9dfc0bd83

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Two written questions

### AUTHORED_ENGLISH `en-u5-117`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 48044a047a21c191163421a820be29368f0c6484d23aa3e4843eea3409e935d9

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Two more written questions

### AUTHORED_ENGLISH `en-u5-118`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: b93cc4f45e6c89e25d3cb4dc8d6e4dcd8eb30782c1899639116b367b146fa509

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=These examples are exact frozen excerpts. 

### AUTHORED_ENGLISH `en-u5-119`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 73378f36b7c461d5f1c981aa5cfe5d75661d6cbf73bcb1fb3a23b0edbaa19d85

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Two about-examples

### AUTHORED_ENGLISH `en-u5-120`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 7661e872b2e6ce696db27ae538d4ab3e187ea945f096ed2d63de229f686cc212

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Two more about-examples and a place example

### AUTHORED_ENGLISH `en-u5-121`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 1278eaf6dfd66a996e3a33a62fb461c1d454dd8dfd81d6f9d9b39300c0be7f24

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Two place examples

### AUTHORED_ENGLISH `en-u5-122`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 4efcc447259b330af0ab0c6ae5030a051ee75de3237b847166419758a8d2f155

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Two more place examples

### AUTHORED_ENGLISH `en-u5-123`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: ae02e81ebd16fa214909ab0c9c6d4ff39b4f814abe46a45a51cf3ec8cad2c5e1

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Three admitted function words

### AUTHORED_ENGLISH `en-u5-124`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: fd9029f250cd636b67521780debbdd0429e48f8a59a6764d912471877ab3ca8d

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Use these rows only as quoted. Do not build a new phrase from them. 

### AUTHORED_ENGLISH `en-u5-125`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 1cc378d8d4904be32e414eeedbab5b4d2cb836df90403c55202e4bfadbd7a720

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Two admitted field pairs

### AUTHORED_ENGLISH `en-u5-126`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 996d7088a6992af322664276b5b3d6319a587ca69bd2070e78f48a3b36b5ac5d

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Each pair is the first field, then the second field, from the same admitted row. 

### AUTHORED_ENGLISH `en-u5-127`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 6e95c694b8bc35f2f422e050c81c9cc19c98aaaab8a7514c84b25d12ec47b9df

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=What the frozen page says about words that do not change

### AUTHORED_ENGLISH `en-u5-128`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 72567be9092136746e6e37cd78795589fdd18a10852a3072338692a6ae0e0c93

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=The frozen page says 

### AUTHORED_ENGLISH `en-u5-129`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: cfee8bd045c62d1f3de441942c70966288cd994f62971832fcd61847694fe3ea

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=. 

### AUTHORED_ENGLISH `en-u5-130`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 44be6955fa414a96931d34f01e2d24671b924c49214dacd12e2a083e945ace55

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=The admitted rows below have the flag set. That flag is the record used here. 

### AUTHORED_ENGLISH `en-u5-131`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 9bcda69fff971431c132173125ce2eb4ff1cf790df36790f80e4e9e3dd509e73

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Four admitted rows with the flag set

### AUTHORED_ENGLISH `en-u5-132`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 39d79e99436ee5ed860f8877e0f0cfb530498dcbf06d98ac298ceb6d67c183cf

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=The admitted rows below have the flag set. 

### AUTHORED_ENGLISH `en-u5-133`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 42c7700b28413d78aba9e24cff458ea3cc553ede64c7a3cd496a3679bfc1daa3

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Two more admitted rows with the flag set

### AUTHORED_ENGLISH `en-u5-134`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 4ab7a5ab41f86287727a63ce4c3aa2095d3fd0c9c02496e8b42ed6522753dfbf

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Questions, place examples, and words that do not change form

### AUTHORED_ENGLISH `en-u5-135`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: b9436816917dca9bd8a7736ef42b6fc1b24874c2884d260f03df47e746a64f38

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Proposed Unit 5 uses frozen grammar excerpts and dictionary rows for questions, place examples, and words that do not change form. It does not teach question intonation and it does not supply audio. The owned book remains separate work. Qualified review is pending.

### AUTHORED_ENGLISH `en-u5-136`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 5b889f192fde0b1bf4c3932c57af7dacae44337b4d846a0fa57430d9eee83229

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Open Lesson 5 in the copy you own. The app records do not replace that lesson.

### AUTHORED_ENGLISH `en-u5-137`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: bc8ba62a11cc1a78f815b774ba64bf693832c6eaf821450f9fbfad8b5321139e

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Guided practice

### AUTHORED_ENGLISH `en-u5-138`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: b00c2cde18fc497b5437596866a4fe6a8c1f9e00f8df3a2ee178507e82b3d34d

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Choose the admitted record. The feedback states the frozen field.

### AUTHORED_ENGLISH `en-u5-139`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: d69254fa282d07a1b46222457e046d714a7b1bebc7225f76e9dc5dfb012b8354

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Remediation

### AUTHORED_ENGLISH `en-u5-140`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 7a4a51f77ae8e5ad59e30b158e0b389c6bc46cff3dbd7a53519f9e87ba0a0f09

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Return to the explanation. Compare the shown form with the admitted record. Do not invent a form that is absent from the record.

### AUTHORED_ENGLISH `en-u5-141`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 154103349d7eb08be68baa4549b1a81298338758485d601ac217eaaf61199b76

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Independent checks

### AUTHORED_ENGLISH `en-u5-142`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: b06084f096d9464ab289c501bf6f7c7647cd031c99e675ff6df6f1e506529aa1

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=These checks use only admitted records. A miss returns to the explanation.

### AUTHORED_ENGLISH `en-u5-143`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 13b7f0b4a6cb25434c8f6d57e00c4d4c3307754c73fb73df3a222529c4e4e8f0

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Work Lesson 5 in your copy

### AUTHORED_ENGLISH `en-u5-144`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: ae53da132264dd964681113c257e780bef8f659c53676730ebe456d219ca2ec7

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Do Lesson 5 in the copy you own. Use that book's explanations and practice. The proposed records in this unit are a separate licensed set and do not copy the book.

### AUTHORED_ENGLISH `en-u5-145`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: b979f04b45c2a951edbe9257560b75b935aee2f4adaffafe4dc06e0882d681cf

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Open Lesson 5 in your copy and complete its lesson work.

### AUTHORED_ENGLISH `en-u5-146`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 687d99abeaf9e23de2b40c8f2756c6c44acc870ccf07ec3d54760aa87aeb417f

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=Record Lesson 5 book work

### AUTHORED_ENGLISH `en-u5-147`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: 4825968ecd6a3673bed4e2eaa17fff607f158fa2a3be88b6f045bfb4415d469a

classification=INDEPENDENTLY_AUTHORED_SCAFFOLD
text=When you have done Lesson 5 in your copy, self-attest here. Attestation records your report. It does not grade the lesson, approve a mapping, or approve the proposed records.

### AUTHORED_ENGLISH `en-u5-148`

- Objectives: none
- Sessions: none
- Source: src-app-english-v2
- Rights: rights-app-english-v2
- Classification: INDEPENDENTLY_AUTHORED_SCAFFOLD
- Current review status: IMPLEMENTED_PENDING_FINAL_AUDIT
- Content hash: a26087f237772bd5718e02a307e0d5d792a6b935672933b7b818fd63975abdd6

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
- `obj-u5-indeclinable` INSTRUCTION `u5-st-teach-indeclinable-4`
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
- `u5-st-rem-indeclinable` EXAMPLE `ex-u5-indecl-метро`
- `u5-st-rem-indeclinable` EXAMPLE `ex-u5-indecl-кино`
- `u5-st-rem-indeclinable` EXAMPLE `ex-u5-indecl-пальто`
- `u5-st-rem-indeclinable` EXAMPLE `ex-u5-indecl-кофе`
- `u5-st-rem-indeclinable` EXAMPLE `ex-u5-indecl-кафе`
- `u5-st-rem-indeclinable` EXAMPLE `ex-u5-indecl-меню`
- `u5-st-teach-indeclinable-4` REFERENCE `ref-u35-attribution`
- `u5-st-check-indeclinable` REMEDIATION `u5-st-rem-indeclinable`
- `u5-st-check-indeclinable` ITEM `u5-i-indecl-такси`
- `u5-st-check-indeclinable` ITEM `u5-i-indecl-метро`
- `u5-st-check-indeclinable` ITEM `u5-i-indecl-кино`
- `u5-st-check-indeclinable` ITEM `u5-i-decl-книга`
- `u5-st-check-indeclinable` ITEM `u5-i-decl-окно`
- `u5-s-book` OBJECTIVE `obj-u5-book`
- `u5-st-book-work` REFERENCE `ref-book-topics`
- `u5-st-book-work` REFERENCE `ref-u35-attribution`
- `obj-u5-indeclinable` PROVENANCE `prov-ca-u5-behavior-changeable-choice`
- `obj-u5-indeclinable` PROVENANCE `prov-ca-u5-behavior-prompt`
- `obj-u5-indeclinable` PROVENANCE `prov-ca-u5-behavior-rem-body`
- `obj-u5-indeclinable` PROVENANCE `prov-ca-u5-behavior-rem-title`
- `obj-u5-indeclinable` PROVENANCE `prov-ca-u5-behavior-teach-1-body`
- `obj-u5-indeclinable` PROVENANCE `prov-ca-u5-behavior-teach-1-title`
- `obj-u5-indeclinable` PROVENANCE `prov-ca-u5-behavior-teach-2-body`
- `obj-u5-indeclinable` PROVENANCE `prov-ca-u5-behavior-teach-2-title`
- `obj-u5-indeclinable` PROVENANCE `prov-ca-u5-behavior-teach-3-body`
- `obj-u5-indeclinable` PROVENANCE `prov-ca-u5-behavior-teach-3-title`
- `obj-u5-indeclinable` PROVENANCE `prov-ca-u5-behavior-teach-4-body`
- `obj-u5-indeclinable` PROVENANCE `prov-ca-u5-behavior-teach-4-title`
- `obj-u5-indeclinable` PROVENANCE `prov-ca-u5-behavior-u5-i-decl-книга-feedback`
- `obj-u5-indeclinable` PROVENANCE `prov-ca-u5-behavior-u5-i-decl-окно-feedback`
- `obj-u5-indeclinable` PROVENANCE `prov-ca-u5-behavior-u5-i-indecl-кино-feedback`
- `obj-u5-indeclinable` PROVENANCE `prov-ca-u5-behavior-u5-i-indecl-метро-feedback`
- `obj-u5-indeclinable` PROVENANCE `prov-ca-u5-behavior-u5-i-indecl-радио-feedback`
- `obj-u5-indeclinable` PROVENANCE `prov-ca-u5-behavior-u5-i-indecl-такси-feedback`
- `obj-u5-indeclinable` PROVENANCE `prov-ca-u5-behavior-u5-st-check-indeclinable-body`
- `obj-u5-indeclinable` PROVENANCE `prov-ca-u5-behavior-u5-st-check-indeclinable-title`
- `obj-u5-indeclinable` PROVENANCE `prov-ca-u5-behavior-u5-st-guided-indeclinable-1-body`
- `obj-u5-indeclinable` PROVENANCE `prov-ca-u5-behavior-u5-st-guided-indeclinable-1-title`
- `obj-u5-indeclinable` PROVENANCE `prov-ca-u5-behavior-unchanged-choice`
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
