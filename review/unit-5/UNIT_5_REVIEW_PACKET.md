# Unit 5 linguistic and owned-edition mapping packet

This packet prepares review. It records an unsigned structural mapping proposal. It does not record a qualified review, an approved edition alignment, or an install decision.

The owned-edition mapping worksheet separates observed edition facts, the proposed lesson and printed page range, unresolved edition questions, and matters that still require qualified linguistic review. ISBN, owned-copy verification, variance, reviewer identity, qualification, date, and approval stay blank. The disposition remains PENDING_CURRICULUM_REVIEWER.

Unit 5 remains in development. Its readiness is content-complete and review-pending. The Lesson 5 relationship is a proposed, unsigned mapping. It is not an approved edition alignment.

## Package identity

- Schema version: 3
- Application: dev.russiancompanion
- Version: 2.0.0-dev (5)
- Curriculum source commit: fb7fdbfdac934e2a4df20c6d3b4e12e41d07b112
- Unit content-review SHA-256: ce049d52d81f3a4f51e67edf3c782856c9b651a8c253fb4f6acbf4b3f2eed69e
- Mapping-package SHA-256: a1affd13da11e1173c23a2cab33b22f7ad6ea99aa54c99d54fc945fd6d7d0806
- Pack content-review SHA-256: 00fbc256c90b3021fb76c8cf325d663c3ea4372bb16a1e03b894e61303421203
- Review-package SHA-256: 5f3c9914c40260be3d59a0cacf8efec6e6abb41c12bd2d56a16b7dc38a2d6be1
- Repository source SHA-256: a6e01bfddf780cb5de141367a81878639ca9d88783a64a180cf60fdf7fd05bc0

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
- Content hash: c81f49aa7a070686f9559623e295d4048db10683046b82127b20f2ab630a71f2

title=Recognize admitted question words and written questions
kind=RECOGNITION
unitNumber=5
instruction=u5-st-orient,u5-st-teach-questions
examples=ex-u5-q-kto,ex-u5-q-chto,ex-u5-q-kogda,ex-u5-q-pochemu
practice=u5-st-guided-questions
checks=u5-i-q-kogda,u5-i-q-pochemu,u5-i-q-kak,u5-i-q-gde,u5-i-pattern-pattern-chto-eto,u5-i-pattern-pattern-kto-eto,u5-i-pattern-pattern-kak-dela,u5-i-pattern-pattern-gde-zhivete
remediation=u5-st-rem-questions

### OBJECTIVE `obj-u5-prepositional`

- Objectives: obj-u5-prepositional
- Sessions: none
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: fb6adaef8ede99b6a5765903f7401a8613ac34da391a7e18f5814d939ee13fc9

title=Recognize admitted prepositional examples
kind=RECOGNITION
unitNumber=5
instruction=u5-st-orient,u5-st-teach-prepositional
examples=ex-u5-prep-o-knige,ex-u5-prep-o-pisme,ex-u5-prep-o-sade,ex-u5-prep-v-moskve
practice=u5-st-guided-prepositional
checks=u5-i-prep-o-sade,u5-i-prep-v-moskve,u5-i-prep-field-книга,u5-i-prep-field-письмо
remediation=u5-st-rem-prepositional

### OBJECTIVE `obj-u5-indeclinable`

- Objectives: obj-u5-indeclinable
- Sessions: none
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 9193b44f111f29f1cdb527d036f9a36ba5205edc2ae7b773f95655c968587a8c

title=Recognize admitted indeclinable nouns
kind=RECOGNITION
unitNumber=5
instruction=u5-st-orient,u5-st-teach-indeclinable
examples=ex-u5-indecl-радио,ex-u5-indecl-такси,ex-u5-indecl-метро,ex-u5-indecl-кино
practice=u5-st-teach-indeclinable
checks=u5-i-indecl-радио,u5-i-indecl-такси,u5-i-indecl-метро,u5-i-indecl-кино,u5-i-decl-книга,u5-i-decl-окно
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
- Content hash: 5a4b52b8f909a3cb1d7c1d4a9c7371aeb61d09e1be17d107b6da78e6abf73999

title=Questions, the prepositional case, and indeclinable nouns
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
- Content hash: fbe615f2ea8c39c0731550299d151ae6b85d3665f034ae99eea06febfe8120d1

kind=ORIENT
title=Questions, the prepositional case, and indeclinable nouns
body=Proposed Unit 5 uses frozen grammar excerpts and dictionary rows. It does not teach question intonation and it does not supply audio. The owned book remains separate work. Qualified review is pending.
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
- Content hash: 63c03ee2bef360efb3128df976b2639e7c7b4cdfe354bbeb5eefdb813a456bb3

title=Recognize admitted question words and written questions
estimatedMinutes=12
objectives=obj-u5-questions
steps=u5-st-teach-questions,u5-st-rem-questions,u5-st-guided-questions,u5-st-check-questions

### STEP `u5-st-teach-questions`

- Objectives: obj-u5-questions
- Sessions: u5-s-questions
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 5c95f927a55222fd1f1455caed552fdf6e6562670424568ba4dd7609a0e24d37

kind=TEACH
title=Recognize admitted question words and written questions
body=The frozen interrogative page supplies these question words and written questions. Intonation is not taught because no exact licensed audio is matched. Кто — Who. Что — What. Когда — When. Почему — Why. Как — How. Где — Where (location). Куда — Whither, where to. Откуда — Whence, where from. Сколько — How much, how many. Какой — What kind. Written examples: Что это? — What is this?. Кто это? — Who is this?. Как дела? — How do you do?/How're things?. Где вы живёте? — Where do you live?. Куда вы идёте? — Where are you walking to?. Почему он не знает? — Why doesn't he know?. Какой сыр? — What kind of cheese?. 
dominantAction=Continue
references=ref-u35-attribution
items=
examples=ex-u5-q-kto,ex-u5-q-chto,ex-u5-q-kogda,ex-u5-q-pochemu
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
- Content hash: c83f0f5955fba3d66dcd24adecf6c1eb4a85a9eaaa83fd388ab1b0c9e8c69386

kind=REMEDIATE
title=Remediation
body=Return to the explanation. Compare the shown form with the admitted record. Do not invent a form that is not in the record.
dominantAction=Continue
references=
items=
examples=ex-u5-q-kto,ex-u5-q-chto
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u5-st-guided-questions`

- Objectives: obj-u5-questions
- Sessions: u5-s-questions
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 394e1df80ef484480479b8e04c8296cddf5f5452bed7cb2b2e5df187feb44f28

kind=GUIDED
title=Guided practice
body=Choose the admitted record. Feedback states the frozen field.
dominantAction=Continue
references=
items=u5-i-q-kto,u5-i-q-chto
examples=
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=u5-st-rem-questions

### STEP `u5-st-check-questions`

- Objectives: obj-u5-questions
- Sessions: u5-s-questions
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 1887addba7e5738b5decdaf18804562c61c1bc63431690401ec9365214b29f8b

kind=CHECK
title=Independent checks
body=These checks use only admitted records. A miss returns to the explanation.
dominantAction=Continue
references=
items=u5-i-q-kogda,u5-i-q-pochemu,u5-i-q-kak,u5-i-q-gde,u5-i-pattern-pattern-chto-eto,u5-i-pattern-pattern-kto-eto,u5-i-pattern-pattern-kak-dela,u5-i-pattern-pattern-gde-zhivete
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
- Content hash: 86cc63b913db33c37959bf7b6220421e1ec032fdb0fb67642ce24062dd41aa2d

title=Recognize admitted prepositional examples
estimatedMinutes=12
objectives=obj-u5-prepositional
steps=u5-st-teach-prepositional,u5-st-rem-prepositional,u5-st-guided-prepositional,u5-st-check-prepositional

### STEP `u5-st-teach-prepositional`

- Objectives: obj-u5-prepositional
- Sessions: u5-s-prepositional
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 6807e457ec64d06fed9e4d6b2994193a65b630ace798bcf66ef562810cd894dd

kind=TEACH
title=Recognize admitted prepositional examples
body=The frozen prepositional page presents location examples and about-examples. Use only the admitted examples, the admitted singular prepositional fields, and the admitted preposition rows. Do not build a new phrase from those parts. О книге — About the book. О письме — About the letter. О саде — About the garden. я в Москве — I'm in Moscow. Он на крыше — He's on the roof. Он в театре — He is at the theatre. Ваза стоит на столе. — The vase stands on the table.. Картина висит на стене. — The painting hangs on the wall.. Admitted singular prepositional fields, after the nominative field from the same row: кни́га / кни́ге. письмо́ / письме́. окно́ / окне́. шко́ла / шко́ле. Admitted preposition rows: в — in; at; into; to; for; on; within. на — on (place); on (day/week); on (transport means); to; onto, upon; at. о — about; of; on; against. 
dominantAction=Continue
references=ref-u35-attribution
items=
examples=ex-u5-prep-o-knige,ex-u5-prep-o-pisme,ex-u5-prep-o-sade,ex-u5-prep-v-moskve
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u5-st-rem-prepositional`

- Objectives: obj-u5-prepositional
- Sessions: u5-s-prepositional
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 2af97b477f3c60607c2d277b4c83668b4052a4b71107f6bd05bd222f52bc9e36

kind=REMEDIATE
title=Remediation
body=Return to the explanation. Compare the shown form with the admitted record. Do not invent a form that is not in the record.
dominantAction=Continue
references=
items=
examples=ex-u5-prep-o-knige,ex-u5-prep-o-pisme
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u5-st-guided-prepositional`

- Objectives: obj-u5-prepositional
- Sessions: u5-s-prepositional
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 6da43b2dc83b0b19e62d22e6352320b9c23724306fa5b1dada265da8482ca419

kind=GUIDED
title=Guided practice
body=Choose the admitted record. Feedback states the frozen field.
dominantAction=Continue
references=
items=u5-i-prep-o-knige,u5-i-prep-o-pisme
examples=
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=u5-st-rem-prepositional

### STEP `u5-st-check-prepositional`

- Objectives: obj-u5-prepositional
- Sessions: u5-s-prepositional
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 5d1ea0e3e91181e49304ff896e61b08f6c733d8fb4da1ebe028c556185c63464

kind=CHECK
title=Independent checks
body=These checks use only admitted records. A miss returns to the explanation.
dominantAction=Continue
references=
items=u5-i-prep-o-sade,u5-i-prep-v-moskve,u5-i-prep-field-книга,u5-i-prep-field-письмо
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
- Content hash: 1e037c0dda930d12e96812c94a6bb1af7b4928ab8fe7efdf35b8a0663b254c07

title=Recognize admitted indeclinable nouns
estimatedMinutes=12
objectives=obj-u5-indeclinable
steps=u5-st-teach-indeclinable,u5-st-rem-indeclinable,u5-st-check-indeclinable

### STEP `u5-st-teach-indeclinable`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-indeclinable
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 51c2beb1b3eab3b0410284571a745deafe7d4c633e03a19a2f8e201773439d2a

kind=TEACH
title=Recognize admitted indeclinable nouns
body=The frozen nominative page says some foreign-origin nouns do not change between singular and plural. It names ра́дио and такси́. The admitted noun rows below are flagged indeclinable. That flag is the record used here, not a rule applied to a new word. ра́дио — radio, radio set, wireless. такси́ — taxi. метро́ — metro; subway; underground. кино́ — cinema, pictures, movies. пальто́ — coat, topcoat. ко́фе — coffee. кафе́ — café, coffee-house. меню́ — menu, bill of fare. 
dominantAction=Continue
references=ref-u35-attribution
items=
examples=ex-u5-indecl-радио,ex-u5-indecl-такси,ex-u5-indecl-метро,ex-u5-indecl-кино
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
- Content hash: 0a1414626207130eb790701aad561d057f6a038a16cf9d8c71763a6258e2b94e

kind=REMEDIATE
title=Remediation
body=Return to the explanation. Compare the shown form with the admitted record. Do not invent a form that is not in the record.
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
- Content hash: 793689f478753f8bb270e92c8cdadc016c1b4ef189f34c1fa01166ba0979efb2

kind=CHECK
title=Independent checks
body=These checks use only admitted records. A miss returns to the explanation.
dominantAction=Continue
references=
items=u5-i-indecl-радио,u5-i-indecl-такси,u5-i-indecl-метро,u5-i-indecl-кино,u5-i-decl-книга,u5-i-decl-окно
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

### EXAMPLE `ex-u5-indecl-радио`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-indeclinable
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 70e4bfe37bd2933e7ec016dec831a0ff79f80fe25d8f3e17b7d8bd9167c9d12f

kind=LEXICAL
letterId=
display=ра́дио
note=radio, radio set, wireless
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
- Content hash: 4157bde18d28200e4a84510db97f5f4db48d2521282db38618dea4748cef7c42

kind=LEXICAL
letterId=
display=метро́
note=metro; subway; underground
objectiveId=obj-u5-indeclinable
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-u5-indecl-кино`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-indeclinable
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: d654c23fca6d614a9c9141bb97907fa9fafa40c01a4c1071158438258b1ddf90

kind=LEXICAL
letterId=
display=кино́
note=cinema, pictures, movies
objectiveId=obj-u5-indeclinable
reviewStatus=PENDING_QUALIFIED_REVIEW

### GUIDED_PRACTICE `u5-i-q-kto`

- Objectives: obj-u5-questions
- Sessions: u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: d0bc11549dcbcf55d4d368b2ca5d9ee0933d9a42d5193ddceb1961ed6e205471

kind=SOURCE_QUESTION
language=en
prompt=Which admitted question word is recorded for this English purpose: Who?
promptLetter=Кто
options=Кто | Что | Когда | Почему | Как | Где
answer=Кто
feedback=The frozen interrogative table records Кто for Who.
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
- Content hash: 406eed5bf0f7f418b54457c8889cfcfbbd9bf27677e864fbf42f4841d7b1e6d9

kind=SOURCE_QUESTION
language=en
prompt=Which admitted question word is recorded for this English purpose: What?
promptLetter=Что
options=Кто | Что | Когда | Почему | Как | Где
answer=Что
feedback=The frozen interrogative table records Что for What.
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
- Content hash: 8d333fac89eb9cc879e4665316f2d89143e638e670ff0354f0c50e653dd94fb7

kind=SOURCE_CASE
language=en
prompt=Which admitted English description is recorded for the shown prepositional example?
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
- Content hash: 383133daf528b66f50002d830043c4ee4404b7792857d09478679c43d818ff17

kind=SOURCE_CASE
language=en
prompt=Which admitted English description is recorded for the shown prepositional example?
promptLetter=О письме
options=About the book | About the letter | About the garden | I'm in Moscow
answer=About the letter
feedback=The frozen page records this example as: About the letter.
objectiveId=obj-u5-prepositional
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW

### INDEPENDENT_CHECK `u5-i-q-kogda`

- Objectives: obj-u5-questions
- Sessions: u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: a068cb4032a6204b1d958a7de6781d8294e1405d0f7842e2f03ee815f8f8cbb4

kind=SOURCE_QUESTION
language=en
prompt=Which admitted question word is recorded for this English purpose: When?
promptLetter=Когда
options=Кто | Что | Когда | Почему | Как | Где
answer=Когда
feedback=The frozen interrogative table records Когда for When.
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
- Content hash: c67e5b2915bd4e511d9c59fcba6e2b034bc763c6c6dbe24fcee7baa7e19cbc22

kind=SOURCE_QUESTION
language=en
prompt=Which admitted question word is recorded for this English purpose: Why?
promptLetter=Почему
options=Кто | Что | Когда | Почему | Как | Где
answer=Почему
feedback=The frozen interrogative table records Почему for Why.
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
- Content hash: d67debf38d06664dc6b776982ad318028b7e4ecc6bac60c11d964432f888f879

kind=SOURCE_QUESTION
language=en
prompt=Which admitted question word is recorded for this English purpose: How?
promptLetter=Как
options=Кто | Что | Когда | Почему | Как | Где
answer=Как
feedback=The frozen interrogative table records Как for How.
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
- Content hash: 8c59cb355e1d538d06a878c7a6e2a10bbe61b770072e622d3e7a9e612172dd01

kind=SOURCE_QUESTION
language=en
prompt=Which admitted question word is recorded for this English purpose: Where (location)?
promptLetter=Где
options=Кто | Что | Когда | Почему | Как | Где
answer=Где
feedback=The frozen interrogative table records Где for Where (location).
objectiveId=obj-u5-questions
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW

### INDEPENDENT_CHECK `u5-i-pattern-pattern-chto-eto`

- Objectives: obj-u5-questions
- Sessions: u5-s-questions
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: e30745aff13f701abcb85d109a0ad406a15de86ac8fbe3d401bb943751fb6b38

kind=SOURCE_QUESTION
language=en
prompt=Which admitted English meaning is recorded for the shown written question?
promptLetter=Что это?
options=What is this? | Who is this? | How do you do?/How're things? | Where do you live?
answer=What is this?
feedback=The frozen interrogative page records this meaning: What is this?. Written punctuation is not pronunciation practice.
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
- Content hash: 5fb568bbfbd907f5b83239f80ef4843ee983fae53daa1595789e49f28f4f3d33

kind=SOURCE_QUESTION
language=en
prompt=Which admitted English meaning is recorded for the shown written question?
promptLetter=Кто это?
options=What is this? | Who is this? | How do you do?/How're things? | Where do you live?
answer=Who is this?
feedback=The frozen interrogative page records this meaning: Who is this?. Written punctuation is not pronunciation practice.
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
- Content hash: 878f41d419bbb8e53eec836a7f8ee5650f88ffd3f607a6f43097d9dddcb56b2c

kind=SOURCE_QUESTION
language=en
prompt=Which admitted English meaning is recorded for the shown written question?
promptLetter=Как дела?
options=What is this? | Who is this? | How do you do?/How're things? | Where do you live?
answer=How do you do?/How're things?
feedback=The frozen interrogative page records this meaning: How do you do?/How're things?. Written punctuation is not pronunciation practice.
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
- Content hash: 306fc355379a71019b24e9321d38b52b43cbd0805d38850a1d0969c6c01c605f

kind=SOURCE_QUESTION
language=en
prompt=Which admitted English meaning is recorded for the shown written question?
promptLetter=Где вы живёте?
options=What is this? | Who is this? | How do you do?/How're things? | Where do you live?
answer=Where do you live?
feedback=The frozen interrogative page records this meaning: Where do you live?. Written punctuation is not pronunciation practice.
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
- Content hash: 657f542362345b86ae31ca72aea0cd022f25d376f941ef65a2fb7694a5bb513b

kind=SOURCE_CASE
language=en
prompt=Which admitted English description is recorded for the shown prepositional example?
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
- Content hash: 0a5acfb6e9eb35fad1fcc75bebc0b0ee4a5167788655d090203c63e35fab11c5

kind=SOURCE_CASE
language=en
prompt=Which admitted English description is recorded for the shown prepositional example?
promptLetter=я в Москве
options=About the book | About the letter | About the garden | I'm in Moscow
answer=I'm in Moscow
feedback=The frozen page records this example as: I'm in Moscow.
objectiveId=obj-u5-prepositional
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW

### INDEPENDENT_CHECK `u5-i-prep-field-книга`

- Objectives: obj-u5-prepositional
- Sessions: u5-s-prepositional
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 6497362449158aaef25bb2aa964497a116037675c45dc5214fa57b0dc1a81f19

kind=SOURCE_FORM
language=en
prompt=Which string is the admitted singular prepositional field, not the nominative field?
promptLetter=кни́га
options=кни́га | кни́ге
answer=кни́ге
feedback=The frozen noun row lists the singular prepositional field as кни́ге.
objectiveId=obj-u5-prepositional
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW

### INDEPENDENT_CHECK `u5-i-prep-field-письмо`

- Objectives: obj-u5-prepositional
- Sessions: u5-s-prepositional
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: ddd56b28cd871572b779777e37791299a429bf121715865b9d96bf4bb8e6bf4a

kind=SOURCE_FORM
language=en
prompt=Which string is the admitted singular prepositional field, not the nominative field?
promptLetter=письмо́
options=письмо́ | письме́
answer=письме́
feedback=The frozen noun row lists the singular prepositional field as письме́.
objectiveId=obj-u5-prepositional
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW

### INDEPENDENT_CHECK `u5-i-indecl-радио`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-indeclinable
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 8499e89d5886ff565267964e43db293758d757d898557163c459b84a9bdbc761

kind=SOURCE_INDECLINABLE
language=en
prompt=What does the admitted noun row record for the shown word?
promptLetter=ра́дио
options=indeclinable | declinable
answer=indeclinable
feedback=The frozen noun row sets indeclinable to 1 for ра́дио.
objectiveId=obj-u5-indeclinable
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW

### INDEPENDENT_CHECK `u5-i-indecl-такси`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-indeclinable
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: fabfe92efc8b948272a46e197b413c483fd77fd8f96ee81af9c80e420dadce8e

kind=SOURCE_INDECLINABLE
language=en
prompt=What does the admitted noun row record for the shown word?
promptLetter=такси́
options=indeclinable | declinable
answer=indeclinable
feedback=The frozen noun row sets indeclinable to 1 for такси́.
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
- Content hash: c12bf3c6b36a0112d9f369a8b071fe305c4063c0ed71ad357a8084aba791c229

kind=SOURCE_INDECLINABLE
language=en
prompt=What does the admitted noun row record for the shown word?
promptLetter=метро́
options=indeclinable | declinable
answer=indeclinable
feedback=The frozen noun row sets indeclinable to 1 for метро́.
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
- Content hash: f026c6266f2fd3cde1c2546457ca26e75b5711e5c43c422a724b3eb4485097b7

kind=SOURCE_INDECLINABLE
language=en
prompt=What does the admitted noun row record for the shown word?
promptLetter=кино́
options=indeclinable | declinable
answer=indeclinable
feedback=The frozen noun row sets indeclinable to 1 for кино́.
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
- Content hash: 9beebe4b9a879e3a12dd0724b56a1e120210e4a6ee1f9b6008711b5d5d2544b5

kind=SOURCE_INDECLINABLE
language=en
prompt=What does the admitted noun row record for the shown word?
promptLetter=кни́га
options=indeclinable | declinable
answer=declinable
feedback=The frozen noun row sets indeclinable to 0 for кни́га.
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
- Content hash: bf8901c70315009a9e660288ca8bf129ea849cb03b034acfb75e7c30346c3fe0

kind=SOURCE_INDECLINABLE
language=en
prompt=What does the admitted noun row record for the shown word?
promptLetter=окно́
options=indeclinable | declinable
answer=declinable
feedback=The frozen noun row sets indeclinable to 0 for окно́.
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
- Content hash: 554ac04c2af55ca114cd1627de3107afde28d64a123dacd5dec3d0996b404e22

title=Later lesson topics stay in the book
summary=Units 6 and later do not contain lesson bodies.
detail=Units 3–5 contain proposed licensed records that still need qualified review. Open the matching lesson in the copy you own. This reference does not approve those records.
classification=OPERATIONAL_GUIDANCE
introducedInUnit=3
searchTerms=later lesson topics stay in the book
reviewStatus=NOT_REQUIRED

### PROVENANCE `prov-u5-contrast-книга`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-orient, u5-s-indeclinable
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: da77ef007ecd36c5f851a7993a2f1ed814265f0317b58bdf3d76eed40f0c9ed3

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/tree/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
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
- Content hash: 128fba367d3f4be26b9ccca642f5e027f3de80d53df7035d9a60649f7363a051

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/tree/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
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

### PROVENANCE `prov-u5-indecl-rule`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-orient, u5-s-indeclinable
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 85c2e76b87a9c60f69a6edbd2e0071ee0290aec156df8ac28fd6ae5eac77f1e9

recordType=reference
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Nominative?oldid=4213267
sourceRevision=4213267
sourceRecordId=nominative:'''радио''' means 'radio' or 'radios', and '''такси''' is both 'taxi' and 'taxis
originalSourceText='''радио''' means 'radio' or 'radios', and '''такси''' is both 'taxi' and 'taxis'
shippedText=радио
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=e6375c901d935f2e068eecb37eb1644a7c60ece286ed2598baff656d7443a4bb
contentHash=76d6d871d1993865e83eb348b58dd73b3b228f49d2e3ab05199da66fe86d18be
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
- Content hash: 93765c7708ddd1d65d6667f696626befe767545997f4aab4b429ed6beeb13566

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/tree/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
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
- Content hash: 50ff95add304752f9cd95007f643ce941045ebf46d1ded312af2de82fc6bbefa

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/tree/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
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
- Content hash: 4ba13be2d7b8304f019bfd45348433d857ae2716976ec9ed16b31e9a589f1df4

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/tree/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
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
- Content hash: 414ff2543fd2c4148ad338f25bcd26dab8d8b9238ca87ee62ac541108699bdd6

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/tree/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
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
- Content hash: 035a6c8968c3a8bc0524d554782c1c8e3b7473694f9bddcc96acb0969b001dfb

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/tree/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
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
- Content hash: 36a54a3e3add39412b502cc3e373df11709ae05b235dc716e661bfb66e86d9ea

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/tree/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
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
- Content hash: 0ce43a866bc3668a970a3d0844c24d59b9c43d897992b1edd3decf9eb395577e

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/tree/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
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
- Content hash: 101ab57c3424a83e53c64ca1bd8beeba7ced69463037946ca6aedf4c61a27510

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/tree/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
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
- Content hash: 8c45a32737a78214cf7a3c0b29d0826afd721b14823c2bd7529ba8844bd2b622

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/tree/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
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
- Content hash: f53e89eff43a46d05338a71e670045d7aa51417874eff066c134a268c2484e7e

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/tree/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
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
- Content hash: 496730a24e78f1c3c5c18faec98addcc70addcb6427e7c58e094f47662adb7e7

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/tree/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
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
- Content hash: 2221d9b99b2852b922c50e047f804b506507a7a4524e99bc72b527d689b23eb5

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/tree/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
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
- Content hash: 207f7f4a1fd35ac98b9af2bb49eec1c73e49fd30be52ecd5f784ff623b66dc86

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Interrogative_Pronouns?oldid=4220316
sourceRevision=4220316
sourceRecordId=interrogative:'''Что это?'''
originalSourceText='''Что это?'''
shippedText=Что это?
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=404203e82bcbc39a77abef8ad7ca353ed7dad3bbb2edf227f39441eedbbdfdbc
contentHash=6d01e644b98ee4b25a2f7aaf1a0175929d16504093ff55c9730dff78c8de92c0
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
- Content hash: eef17210767cd0b0218a91256ecd42785d700c9e3ed4df02b14cd12e26802d96

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Interrogative_Pronouns?oldid=4220316
sourceRevision=4220316
sourceRecordId=interrogative:'''Где вы живёте?'''
originalSourceText='''Где вы живёте?'''
shippedText=Где вы живёте?
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=ee500c4d1710b48909ef821e479bda6ce6607363718141120a38f2e9613df949
contentHash=ae551c8bab31d151716bae620c8b9a1f8537b6fdeb22e62440931fcad901cf95
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
- Content hash: 7296d373c307147802e6a7ce1c63c3296647f43ddbacdf4023a4415d286b4f03

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Interrogative_Pronouns?oldid=4220316
sourceRevision=4220316
sourceRecordId=interrogative:'''Как дела?'''
originalSourceText='''Как дела?'''
shippedText=Как дела?
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=7369b74f0eafb201fdeaee664bd3fbdd6da81028485b7d99a333c27bd730a163
contentHash=1a15be1357f7096aa23684c982b679ce6d678395aa608332cfc22c0ef0248275
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
- Content hash: 5786d5ccdca2300a673c13d0029d7f52364b8cc905668cdb42b8c952e6bbce66

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Interrogative_Pronouns?oldid=4220316
sourceRevision=4220316
sourceRecordId=interrogative:'''Какой сыр?'''
originalSourceText='''Какой сыр?'''
shippedText=Какой сыр?
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=e7c8edba4da6c257b481269f420ea89b4362ec789917c7d1f41fba99fcd3e385
contentHash=b1ca4c905c46cba38c5624b0003ad77c1be933b13f75f6dfd9eef6065f5dffc5
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
- Content hash: 9ec5afc14e93bb991453b7d7dca13a067b96eacfc3c3d14c86639ba4dead86a4

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Interrogative_Pronouns?oldid=4220316
sourceRevision=4220316
sourceRecordId=interrogative:'''Кто это?'''
originalSourceText='''Кто это?'''
shippedText=Кто это?
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=4b77f1419e4963bfc7925643695dca6c9ca265272082ece7f4a1ac0410d9c2c2
contentHash=e0e90af0c003af356781bbdcbc783ff6fd44b5b2858edd04e130fc9c75ec7720
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
- Content hash: 963e51f4255dbe9021ec132838b19afea554276d168fc6b6db9603da57acee56

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Interrogative_Pronouns?oldid=4220316
sourceRevision=4220316
sourceRecordId=interrogative:'''Куда вы идёте?'''
originalSourceText='''Куда вы идёте?'''
shippedText=Куда вы идёте?
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=215a4a09fe6f3365c39dd4ecdfc8a777d8bda10c6a9ea9db30f46b06a289c63c
contentHash=5e8a435476a86e56fb4c41fb27d1339c12268c1dc0acb6d4443f7c7adb9f878c
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
- Content hash: fb1e8062aaad31eec0512c72d917fd6c71e588628e0ee037646ee16bbc48a928

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Interrogative_Pronouns?oldid=4220316
sourceRevision=4220316
sourceRecordId=interrogative:'''Почему он не знает?'''
originalSourceText='''Почему он не знает?'''
shippedText=Почему он не знает?
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=1b7b9816ab93f8ee6a0ed0a44bf69898cc24fdaee5c4f68476f84f80c73218fd
contentHash=82fa52c8532702245de84d3f56792d07377ebd574de5a67a3125846ebfdfce54
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
- Content hash: 9c465a51768a2f4a53b23ae801aade0d5ef0c752f6b12d9d87baab92cc10e795

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/tree/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
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
- Content hash: ee4aa7902630656db9e1b65472f7284f53b265c1a4d8a394c077fba7a98b1e26

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/tree/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
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
- Content hash: 845d3b55e9dcb06817893006916e2d3d1c6bfb7cfdc9fc3badf8b5f354344b18

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/tree/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
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
- Content hash: 57fdc936083b3023eac32d6dbc17a1dfa748dbac21a234982af7defc9915113a

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/tree/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
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

### PROVENANCE `prov-u5-prep-na-kryshe`

- Objectives: obj-u5-prepositional
- Sessions: u5-s-orient, u5-s-prepositional
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 3a5393153d3e39a8a87ed1200d0c9cc1d1f9fc4ea4fb63540654db6df16e299a

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Prepositional_case?oldid=4567336
sourceRevision=4567336
sourceRecordId=prepositional:'''Он на крыше'''
originalSourceText='''Он на крыше'''
shippedText=Он на крыше
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=ae93394fdf34708a3b3e4b2e0e168e5d8c4501a761df4a078735a348a13c0bff
contentHash=37b3dbf209e1e112af62f5438c4d1261ff21c8e7f39f727e7e5e8c826417a580
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
- Content hash: 4c1d0c7412da5cddbb36a6e6ad4bd94b1fe4dc212e1ae7822b469916265e4c7b

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Prepositions?oldid=4446858
sourceRevision=4446858
sourceRecordId=prepositions:Картина висит на стене.
originalSourceText=Картина висит на стене.
shippedText=Картина висит на стене.
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=6c51d7ccaf2158f32936c10639355134fe15075bf3a3b3049a1f38765c86e422
contentHash=6c51d7ccaf2158f32936c10639355134fe15075bf3a3b3049a1f38765c86e422
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
- Content hash: c2f6ae8a9df4cfcc183c008659d64b057d536b0181dc4ccacab79977cab6c283

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Prepositions?oldid=4446858
sourceRevision=4446858
sourceRecordId=prepositions:Ваза стоит на столе.
originalSourceText=Ваза стоит на столе.
shippedText=Ваза стоит на столе.
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=6cd5433605e389a037f72ffbf870cfa7100324d8993a01ea96c4859bf4a6ca73
contentHash=6cd5433605e389a037f72ffbf870cfa7100324d8993a01ea96c4859bf4a6ca73
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
- Content hash: 653fb29be3804429b7e10797b82c1a37379f726bdae290700354726aaf68226a

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Prepositional_case?oldid=4567336
sourceRevision=4567336
sourceRecordId=prepositional:'''Книга --> О книге'''
originalSourceText='''Книга --> О книге'''
shippedText=О книге
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=1c6d6049f91fd785026a5dcbea24e874270718db2f87de8822769611d8bda577
contentHash=97d650c686edfb7d5b7e3a81f0f42edfaa6c2ab863672fab5d07c616e39a0be1
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
- Content hash: 729718e691c766971b72201d1f404f46f605a5849342361fa69ae30820407129

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Prepositional_case?oldid=4567336
sourceRevision=4567336
sourceRecordId=prepositional:'''Письмо --> О письме'''
originalSourceText='''Письмо --> О письме'''
shippedText=О письме
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=5c7123c9d27032e353ef15530816420e507a03992d16efa8b7b63d8e01195abc
contentHash=9a23ca34126ac6c33bca99d6b904feb8b207d616127f396e5db4cd043f0153cf
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
- Content hash: 5c58bb0981837d0297f8b4402b2272ce9a57416ee1cf6e70cf1eece73625f5b3

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Prepositional_case?oldid=4567336
sourceRevision=4567336
sourceRecordId=prepositional:'''Сад --> О саде'''
originalSourceText='''Сад --> О саде'''
shippedText=О саде
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=4e8382e83f2de28a9aea619abd9a141f6b7a4f61c2295229de8217441e3ee8fc
contentHash=92711755008b67d37ba7e8f832d6949384d9602534f81b479ba9e77552f30b6a
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
- Content hash: ca03226429c41a0400ee3f00dd6304f3136be6384d90185330cc2818a8be911d

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Prepositional_case?oldid=4567336
sourceRevision=4567336
sourceRecordId=prepositional:'''я в Москве'''
originalSourceText='''я в Москве'''
shippedText=я в Москве
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=90490a55c4e3819ab737f174c66406e5bcfbf3905ef71fd95a1bc53eb2d5b3b0
contentHash=c8c66dd653b73d631347c560409a0efc38d6f0d70cbf887ed7443d7d7ff9e215
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
- Content hash: 7c1ff8c24b1d8f6c8eefc35e280ce71c4181ffbf43095911f6daa3b86c0e88bb

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Prepositional_case?oldid=4567336
sourceRevision=4567336
sourceRecordId=prepositional:'''Он в театре'''
originalSourceText='''Он в театре'''
shippedText=Он в театре
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=fa66792f95d1f254c0638c46c7c1032338fe609fe7188ef839149f5897385104
contentHash=e2e2269ce3884a4ec9266c7b02c77e92732b23664c25785575081713f9c3e388
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
- Content hash: 7b52326eb16bef2c496b43af74fb2163a2303d969431080252aa1225d43308e1

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/tree/50e210c4803237779cb562bc1abcea529066031c/others.csv
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
- Content hash: f5231a89336194c4b808baafb4e8ae2661ca450e3bd1439e2468b2482732951a

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/tree/50e210c4803237779cb562bc1abcea529066031c/others.csv
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
- Content hash: 3990eff83eb5ec13042993b10b8e9434cb75d2017d225ef597250066ed977742

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/tree/50e210c4803237779cb562bc1abcea529066031c/others.csv
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

### PROVENANCE `prov-u5-taksi-mention`

- Objectives: obj-u5-indeclinable
- Sessions: u5-s-orient, u5-s-indeclinable
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: eed6e6824764830f9a3217edf89df8281a00b06cfb826f2ec0b70420fd2ca369

recordType=reference
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Nominative?oldid=4213267
sourceRevision=4213267
sourceRecordId=nominative:'''такси''' is both 'taxi' and 'taxis'
originalSourceText='''такси''' is both 'taxi' and 'taxis'
shippedText=такси
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=efbefdb53a9ea37dab7355843730720e695918e9a15df12726d011c906731ab6
contentHash=8fc3b37d0ab6b3a1dbc1204fbdd26add7216136b54e5463d46b9d0c65694fbe9
audioAssetId=
reviewerIdentity=
reviewerQualification=
reviewDate=
reviewedDigest=

### SOURCE `src-wikibooks-russian-cc-by-sa`

- Objectives: none
- Sessions: none
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 0ec533d3dc70419ba00afc5fb05103bda4be9bd35048470783ff291b6411c20b

Wikibooks Russian grammar
CC BY-SA 4.0
Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
Selected excerpts and short English adaptation. Russian quotations are unchanged.

### SOURCE `src-openrussian-50e210c4`

- Objectives: none
- Sessions: none
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: f981414d884680f3cf85cad2e084231309f7ffbeaa4b2ac1520d7e9851a27350

OpenRussian dictionary data
CC BY-SA 4.0
Badestrand/russian-dictionary contributors, commit 50e210c4803237779cb562bc1abcea529066031c, CC BY-SA 4.0.
Selected rows and stress-mark formatting. No generated forms.

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
- `obj-u5-questions` INSTRUCTION `u5-st-teach-questions`
- `obj-u5-questions` EXAMPLE `ex-u5-q-kto`
- `obj-u5-questions` EXAMPLE `ex-u5-q-chto`
- `obj-u5-questions` EXAMPLE `ex-u5-q-kogda`
- `obj-u5-questions` EXAMPLE `ex-u5-q-pochemu`
- `obj-u5-questions` PRACTICE `u5-st-guided-questions`
- `obj-u5-questions` CHECK `u5-i-q-kogda`
- `obj-u5-questions` CHECK `u5-i-q-pochemu`
- `obj-u5-questions` CHECK `u5-i-q-kak`
- `obj-u5-questions` CHECK `u5-i-q-gde`
- `obj-u5-questions` CHECK `u5-i-pattern-pattern-chto-eto`
- `obj-u5-questions` CHECK `u5-i-pattern-pattern-kto-eto`
- `obj-u5-questions` CHECK `u5-i-pattern-pattern-kak-dela`
- `obj-u5-questions` CHECK `u5-i-pattern-pattern-gde-zhivete`
- `obj-u5-questions` REMEDIATION `u5-st-rem-questions`
- `obj-u5-prepositional` INSTRUCTION `u5-st-orient`
- `obj-u5-prepositional` INSTRUCTION `u5-st-teach-prepositional`
- `obj-u5-prepositional` EXAMPLE `ex-u5-prep-o-knige`
- `obj-u5-prepositional` EXAMPLE `ex-u5-prep-o-pisme`
- `obj-u5-prepositional` EXAMPLE `ex-u5-prep-o-sade`
- `obj-u5-prepositional` EXAMPLE `ex-u5-prep-v-moskve`
- `obj-u5-prepositional` PRACTICE `u5-st-guided-prepositional`
- `obj-u5-prepositional` CHECK `u5-i-prep-o-sade`
- `obj-u5-prepositional` CHECK `u5-i-prep-v-moskve`
- `obj-u5-prepositional` CHECK `u5-i-prep-field-книга`
- `obj-u5-prepositional` CHECK `u5-i-prep-field-письмо`
- `obj-u5-prepositional` REMEDIATION `u5-st-rem-prepositional`
- `obj-u5-indeclinable` INSTRUCTION `u5-st-orient`
- `obj-u5-indeclinable` INSTRUCTION `u5-st-teach-indeclinable`
- `obj-u5-indeclinable` EXAMPLE `ex-u5-indecl-радио`
- `obj-u5-indeclinable` EXAMPLE `ex-u5-indecl-такси`
- `obj-u5-indeclinable` EXAMPLE `ex-u5-indecl-метро`
- `obj-u5-indeclinable` EXAMPLE `ex-u5-indecl-кино`
- `obj-u5-indeclinable` PRACTICE `u5-st-teach-indeclinable`
- `obj-u5-indeclinable` CHECK `u5-i-indecl-радио`
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
- `u5-st-teach-questions` EXAMPLE `ex-u5-q-kto`
- `u5-st-teach-questions` EXAMPLE `ex-u5-q-chto`
- `u5-st-teach-questions` EXAMPLE `ex-u5-q-kogda`
- `u5-st-teach-questions` EXAMPLE `ex-u5-q-pochemu`
- `u5-st-teach-questions` REFERENCE `ref-u35-attribution`
- `u5-st-rem-questions` EXAMPLE `ex-u5-q-kto`
- `u5-st-rem-questions` EXAMPLE `ex-u5-q-chto`
- `u5-st-guided-questions` REMEDIATION `u5-st-rem-questions`
- `u5-st-guided-questions` ITEM `u5-i-q-kto`
- `u5-st-guided-questions` ITEM `u5-i-q-chto`
- `u5-st-check-questions` REMEDIATION `u5-st-rem-questions`
- `u5-st-check-questions` ITEM `u5-i-q-kogda`
- `u5-st-check-questions` ITEM `u5-i-q-pochemu`
- `u5-st-check-questions` ITEM `u5-i-q-kak`
- `u5-st-check-questions` ITEM `u5-i-q-gde`
- `u5-st-check-questions` ITEM `u5-i-pattern-pattern-chto-eto`
- `u5-st-check-questions` ITEM `u5-i-pattern-pattern-kto-eto`
- `u5-st-check-questions` ITEM `u5-i-pattern-pattern-kak-dela`
- `u5-st-check-questions` ITEM `u5-i-pattern-pattern-gde-zhivete`
- `u5-s-prepositional` OBJECTIVE `obj-u5-prepositional`
- `u5-st-teach-prepositional` EXAMPLE `ex-u5-prep-o-knige`
- `u5-st-teach-prepositional` EXAMPLE `ex-u5-prep-o-pisme`
- `u5-st-teach-prepositional` EXAMPLE `ex-u5-prep-o-sade`
- `u5-st-teach-prepositional` EXAMPLE `ex-u5-prep-v-moskve`
- `u5-st-teach-prepositional` REFERENCE `ref-u35-attribution`
- `u5-st-rem-prepositional` EXAMPLE `ex-u5-prep-o-knige`
- `u5-st-rem-prepositional` EXAMPLE `ex-u5-prep-o-pisme`
- `u5-st-guided-prepositional` REMEDIATION `u5-st-rem-prepositional`
- `u5-st-guided-prepositional` ITEM `u5-i-prep-o-knige`
- `u5-st-guided-prepositional` ITEM `u5-i-prep-o-pisme`
- `u5-st-check-prepositional` REMEDIATION `u5-st-rem-prepositional`
- `u5-st-check-prepositional` ITEM `u5-i-prep-o-sade`
- `u5-st-check-prepositional` ITEM `u5-i-prep-v-moskve`
- `u5-st-check-prepositional` ITEM `u5-i-prep-field-книга`
- `u5-st-check-prepositional` ITEM `u5-i-prep-field-письмо`
- `u5-s-indeclinable` OBJECTIVE `obj-u5-indeclinable`
- `u5-st-teach-indeclinable` EXAMPLE `ex-u5-indecl-радио`
- `u5-st-teach-indeclinable` EXAMPLE `ex-u5-indecl-такси`
- `u5-st-teach-indeclinable` EXAMPLE `ex-u5-indecl-метро`
- `u5-st-teach-indeclinable` EXAMPLE `ex-u5-indecl-кино`
- `u5-st-teach-indeclinable` REFERENCE `ref-u35-attribution`
- `u5-st-rem-indeclinable` EXAMPLE `ex-u5-indecl-радио`
- `u5-st-rem-indeclinable` EXAMPLE `ex-u5-indecl-такси`
- `u5-st-check-indeclinable` REMEDIATION `u5-st-rem-indeclinable`
- `u5-st-check-indeclinable` ITEM `u5-i-indecl-радио`
- `u5-st-check-indeclinable` ITEM `u5-i-indecl-такси`
- `u5-st-check-indeclinable` ITEM `u5-i-indecl-метро`
- `u5-st-check-indeclinable` ITEM `u5-i-indecl-кино`
- `u5-st-check-indeclinable` ITEM `u5-i-decl-книга`
- `u5-st-check-indeclinable` ITEM `u5-i-decl-окно`
- `u5-s-book` OBJECTIVE `obj-u5-book`
- `u5-st-book-work` REFERENCE `ref-book-topics`
- `u5-st-book-work` REFERENCE `ref-u35-attribution`
- `obj-u5-indeclinable` PROVENANCE `prov-u5-contrast-книга`
- `obj-u5-indeclinable` PROVENANCE `prov-u5-contrast-окно`
- `obj-u5-indeclinable` PROVENANCE `prov-u5-indecl-rule`
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
- `obj-u5-questions` PROVENANCE `prov-u5-pattern-gde-zhivete`
- `obj-u5-questions` PROVENANCE `prov-u5-pattern-kak-dela`
- `obj-u5-questions` PROVENANCE `prov-u5-pattern-kakoy-syr`
- `obj-u5-questions` PROVENANCE `prov-u5-pattern-kto-eto`
- `obj-u5-questions` PROVENANCE `prov-u5-pattern-kuda-idete`
- `obj-u5-questions` PROVENANCE `prov-u5-pattern-pochemu`
- `obj-u5-prepositional` PROVENANCE `prov-u5-prep-field-книга`
- `obj-u5-prepositional` PROVENANCE `prov-u5-prep-field-окно`
- `obj-u5-prepositional` PROVENANCE `prov-u5-prep-field-письмо`
- `obj-u5-prepositional` PROVENANCE `prov-u5-prep-field-школа`
- `obj-u5-prepositional` PROVENANCE `prov-u5-prep-na-kryshe`
- `obj-u5-prepositional` PROVENANCE `prov-u5-prep-na-stene`
- `obj-u5-prepositional` PROVENANCE `prov-u5-prep-na-stole`
- `obj-u5-prepositional` PROVENANCE `prov-u5-prep-o-knige`
- `obj-u5-prepositional` PROVENANCE `prov-u5-prep-o-pisme`
- `obj-u5-prepositional` PROVENANCE `prov-u5-prep-o-sade`
- `obj-u5-prepositional` PROVENANCE `prov-u5-prep-v-moskve`
- `obj-u5-prepositional` PROVENANCE `prov-u5-prep-v-teatre`
- `obj-u5-prepositional` PROVENANCE `prov-u5-prep-word-в`
- `obj-u5-prepositional` PROVENANCE `prov-u5-prep-word-на`
- `obj-u5-prepositional` PROVENANCE `prov-u5-prep-word-о`
- `obj-u5-questions` PROVENANCE `prov-u5-q-chto`
- `obj-u5-questions` PROVENANCE `prov-u5-q-gde`
- `obj-u5-questions` PROVENANCE `prov-u5-q-kak`
- `obj-u5-questions` PROVENANCE `prov-u5-q-kakoy`
- `obj-u5-questions` PROVENANCE `prov-u5-q-kogda`
- `obj-u5-questions` PROVENANCE `prov-u5-q-kto`
- `obj-u5-questions` PROVENANCE `prov-u5-q-kuda`
- `obj-u5-questions` PROVENANCE `prov-u5-q-otkuda`
- `obj-u5-questions` PROVENANCE `prov-u5-q-pochemu`
- `obj-u5-questions` PROVENANCE `prov-u5-q-skolko`
- `obj-u5-indeclinable` PROVENANCE `prov-u5-taksi-mention`

## Book-directed experiences

- `u5-st-orient`
- `u5-st-book-work`
- `u5-st-book`

## Unavailable experiences

None.


## Unresolved relationships

None. Every referenced Unit 5 identifier resolves.
