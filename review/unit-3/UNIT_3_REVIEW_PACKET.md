# Unit 3 linguistic and owned-edition mapping packet

This packet prepares review. It records an unsigned structural mapping proposal. It does not record a qualified review, an approved edition alignment, or an install decision.

The owned-edition mapping worksheet separates observed edition facts, the proposed lesson and printed page range, unresolved edition questions, and matters that still require qualified linguistic review. ISBN, owned-copy verification, variance, reviewer identity, qualification, date, and approval stay blank. The disposition remains PENDING_CURRICULUM_REVIEWER.

Unit 3 remains in development. Its readiness is content-complete and review-pending. The Lesson 3 relationship is a proposed, unsigned mapping. It is not an approved edition alignment.

## Package identity

- Schema version: 3
- Application: dev.russiancompanion
- Version: 2.0.0-dev (5)
- Curriculum source commit: fb7fdbfdac934e2a4df20c6d3b4e12e41d07b112
- Unit content-review SHA-256: d13ea0fcbc14b2a4f76cc9b92a7fe4762bbe126ba8f4c20f2b2bd17f04717718
- Mapping-package SHA-256: 090a3fec638b8a19d34e11d8be88a7796ce18a56a87b7b5b06d626ed777d6448
- Pack content-review SHA-256: 00fbc256c90b3021fb76c8cf325d663c3ea4372bb16a1e03b894e61303421203
- Review-package SHA-256: b9183f0c8fbf870387ff21d99478db5e1b48d7bb2ebd73238b4945085e8ed86d
- Repository source SHA-256: a6e01bfddf780cb5de141367a81878639ca9d88783a64a180cf60fdf7fd05bc0

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
- Content hash: 1d49955346ad7bcc4e80739cbeeab54ddc4d8baea1399c1242666498fbcc1674

title=Recognize admitted noun genders
kind=RECOGNITION
unitNumber=3
instruction=u3-st-orient,u3-st-teach-gender
examples=ex-u3-стол,ex-u3-дом,ex-u3-город,ex-u3-брат,ex-u3-учитель,ex-u3-хлеб
practice=u3-st-guided-gender
checks=u3-i-gender-город,u3-i-gender-друг,u3-i-gender-книга,u3-i-gender-дверь,u3-i-gender-окно,u3-i-gender-море,u3-i-gender-мужчина,u3-i-gender-дядя
remediation=u3-st-rem-gender

### OBJECTIVE `obj-u3-phrases`

- Objectives: obj-u3-phrases
- Sessions: none
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 8584acdbff34a69cbd39d1c359c17fc4e9ad61447804cb78348981919cbad73c

title=Recognize admitted everyday phrases
kind=RECOGNITION
unitNumber=3
instruction=u3-st-orient,u3-st-teach-phrases
examples=ex-u3-phrase-здравствуйте,ex-u3-phrase-спасибо,ex-u3-phrase-пожалуйста,ex-u3-phrase-да
practice=u3-st-guided-phrases
checks=u3-i-phrase-пожалуйста,u3-i-phrase-да,u3-i-phrase-нет,u3-i-phrase-это
remediation=u3-st-rem-phrases

### OBJECTIVE `obj-u3-naming`

- Objectives: obj-u3-naming
- Sessions: none
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 52e74fd7c19fcde56a0d86d23a638edd6d49a2fa09e0f374606b292b8e877b76

title=Recognize admitted naming items
kind=RECOGNITION
unitNumber=3
instruction=u3-st-orient,u3-st-teach-naming
examples=ex-u3-phrase-moy-stol,ex-u3-phrase-moya-sobaka,ex-u3-phrase-chto-eto,ex-u3-phrase-kto-eto
practice=u3-st-guided-naming
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
- Content hash: f3f83c475770fce6623f42c9b42148cc25cebf345f8102d8c70345f04e1737b6

title=Everyday phrases, naming, and gender
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
- Content hash: 1afe6d8638faf3cab192d46bd73606b341543af1a8307a84f1acfdbe9e6b1e5e

kind=ORIENT
title=Everyday phrases, naming, and gender
body=Proposed Unit 3 uses frozen dictionary rows and grammar excerpts for gender, a small phrase set, and naming items. The owned book remains separate work. Qualified review is pending.
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
- Content hash: 0201b2860e454dd2035b1d53c0fed4fee778d4b7e31a2a2ee48b731de8191724

title=Recognize admitted noun genders
estimatedMinutes=12
objectives=obj-u3-gender
steps=u3-st-teach-gender,u3-st-rem-gender,u3-st-guided-gender,u3-st-check-gender

### STEP `u3-st-teach-gender`

- Objectives: obj-u3-gender
- Sessions: u3-s-gender
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 7c6bd052a9c626fd9afd66abdaefdbef908f55e60434c2832b94490f4681c155

kind=TEACH
title=Recognize admitted noun genders
body=The frozen grammar page says Russian groups nouns as masculine, feminine, and neuter, and that an ending cue is not enough for every noun. Use the gender on the admitted record. стол — masculine, table, desk, board; diet, cooking, cuisine; department, section. дом — masculine, house, home, house-hold. го́род — masculine, town. брат — masculine, brother. учи́тель — masculine, teacher, tutor. хлеб — masculine, bread, loaf, corn, crops, cereals, grain, living, means of subsistence, daily bread. чай — masculine, tea, tea-party, methinks, maybe. друг — masculine, friend. кни́га — feminine, book, volume. вода́ — feminine, water. шко́ла — feminine, school, schoolhouse. рабо́та — feminine, work, working, job. ма́ма — feminine, mom, mama, mommy, mummy. же́нщина — feminine, woman, women, womanfolk, womankind. семья́ — feminine, family. дверь — feminine, door. окно́ — neuter, window, casement-window, window-sill, gap, opening, port, slot, free period, break. письмо́ — neuter, letter, writing, script. молоко́ — neuter, milk. мо́ре — neuter, sea, ocean. ме́сто — neuter, place, spot, site, locality; region, area; site, scene; room, space; job, position; piece of luggage. сло́во — neuter, word; speech. мужчи́на — masculine, man. дя́дя — masculine, uncle, Mister, man, fellow. Adjective agreement and past-tense endings are outside this unit.
dominantAction=Continue
references=ref-u35-attribution
items=
examples=ex-u3-стол,ex-u3-дом,ex-u3-город,ex-u3-брат,ex-u3-учитель,ex-u3-хлеб
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u3-st-rem-gender`

- Objectives: obj-u3-gender
- Sessions: u3-s-gender
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: b4608a4e138369363e577e7af0cc6ff3a178fb1c27fa6490b308c11763e5a83c

kind=REMEDIATE
title=Remediation
body=Return to the explanation. Compare the shown form with the admitted record. Do not invent a form that is not in the record.
dominantAction=Continue
references=
items=
examples=ex-u3-стол,ex-u3-дом
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u3-st-guided-gender`

- Objectives: obj-u3-gender
- Sessions: u3-s-gender
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 85ae58926aed36dd65464984081ae644225038fc4ea3b8537ecacc1c29dd18ab

kind=GUIDED
title=Guided practice
body=Choose the admitted record. Feedback states the frozen field.
dominantAction=Continue
references=
items=u3-i-gender-стол,u3-i-gender-дом
examples=
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=u3-st-rem-gender

### STEP `u3-st-check-gender`

- Objectives: obj-u3-gender
- Sessions: u3-s-gender
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: cc6df0525ce2af834a7add16d9cfd9c4b8c0f5d3e1907ae165318dad96caa80d

kind=CHECK
title=Independent checks
body=These checks use only admitted records. A miss returns to the explanation.
dominantAction=Continue
references=
items=u3-i-gender-город,u3-i-gender-друг,u3-i-gender-книга,u3-i-gender-дверь,u3-i-gender-окно,u3-i-gender-море,u3-i-gender-мужчина,u3-i-gender-дядя
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
- Content hash: 448d4577bc5f26d4cfc47c347fccf0116ec48d189964f3bc60802c72a072bd34

title=Recognize admitted everyday phrases
estimatedMinutes=12
objectives=obj-u3-phrases
steps=u3-st-teach-phrases,u3-st-rem-phrases,u3-st-guided-phrases,u3-st-check-phrases

### STEP `u3-st-teach-phrases`

- Objectives: obj-u3-phrases
- Sessions: u3-s-phrases
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: d1dc6361d9d147e793e3bb4375e419f317530d19c6e9eff830b0771e3a453f9a

kind=TEACH
title=Recognize admitted everyday phrases
body=These everyday items are copied from admitted records. They are not patterns for new sentences. здра́вствуйте — hello. спаси́бо — thanks, thank you. пожа́луйста — please; you are welcome; here you go. да — yes. нет — no; not. э́то — this is; that is; it. Как дела? — How do you do?/How're things?. Где вы? — Where are you?. 
dominantAction=Continue
references=ref-u35-attribution
items=
examples=ex-u3-phrase-здравствуйте,ex-u3-phrase-спасибо,ex-u3-phrase-пожалуйста,ex-u3-phrase-да
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
- Content hash: a0860b15894232905e033a92aa8eb0c62224da0ee68e3743f6fd4a2fe5382af5

kind=REMEDIATE
title=Remediation
body=Return to the explanation. Compare the shown form with the admitted record. Do not invent a form that is not in the record.
dominantAction=Continue
references=
items=
examples=ex-u3-phrase-здравствуйте,ex-u3-phrase-спасибо
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u3-st-guided-phrases`

- Objectives: obj-u3-phrases
- Sessions: u3-s-phrases
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: d42f178f7469a79994868f7056839aa6419e50aac16b58e7bbadc8f9b5457a19

kind=GUIDED
title=Guided practice
body=Choose the admitted record. Feedback states the frozen field.
dominantAction=Continue
references=
items=u3-i-phrase-здравствуйте,u3-i-phrase-спасибо
examples=
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=u3-st-rem-phrases

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
- Content hash: f6dc90065549fe58b7e9ca553a1851dd433b0c274f3a70445183151d28e945a4

title=Recognize admitted naming items
estimatedMinutes=12
objectives=obj-u3-naming
steps=u3-st-teach-naming,u3-st-rem-naming,u3-st-guided-naming,u3-st-check-naming

### STEP `u3-st-teach-naming`

- Objectives: obj-u3-naming
- Sessions: u3-s-naming
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: d9ec4a8a06b7822b78d79243382903a873773b03b8134b7aa2fe87a7c67fb03b

kind=TEACH
title=Recognize admitted naming items
body=These naming items are exact frozen excerpts. Do not substitute another noun into them. мой стол — my table. моя собака — my dog. Что это? — What is this?. Кто это? — Who is this?. 
dominantAction=Continue
references=ref-u35-attribution
items=
examples=ex-u3-phrase-moy-stol,ex-u3-phrase-moya-sobaka,ex-u3-phrase-chto-eto,ex-u3-phrase-kto-eto
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
- Content hash: f2940f7903a0e1b3e9754316b35652a2986d9ace982415f5e6c1d5bab0c8e853

kind=REMEDIATE
title=Remediation
body=Return to the explanation. Compare the shown form with the admitted record. Do not invent a form that is not in the record.
dominantAction=Continue
references=
items=
examples=ex-u3-phrase-moy-stol,ex-u3-phrase-moya-sobaka
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=

### STEP `u3-st-guided-naming`

- Objectives: obj-u3-naming
- Sessions: u3-s-naming
- Source: none
- Rights: none
- Classification: none
- Current review status: NO_SEPARATE_REVIEW_FIELD
- Content hash: 627fa9b0f8d898ffbae460efea631037fe05630440fae9ee364896d97aafd4a1

kind=GUIDED
title=Guided practice
body=Choose the admitted record. Feedback states the frozen field.
dominantAction=Continue
references=
items=u3-i-name-moy-stol
examples=
unavailableReason=
bookDirection=
companionDirection=
remediationStepId=u3-st-rem-naming

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
- Content hash: 2c72b3935d2ab6b97a13ca0736b98016962f892a4cd223928f1710552353ca36

kind=LEXICAL
letterId=
display=стол
note=table, desk, board; diet, cooking, cuisine; department, section
objectiveId=obj-u3-gender
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-u3-дом`

- Objectives: obj-u3-gender
- Sessions: u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: ce97e66311f8aa2eb840b67f24e69a0d9e26d16d089da87743936eecd28f61b9

kind=LEXICAL
letterId=
display=дом
note=house, home, house-hold
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

### EXAMPLE `ex-u3-учитель`

- Objectives: obj-u3-gender
- Sessions: u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: d1acb667bf39b5a655c3cb9b5fbcb96a036c5352e5e07044fed32864ac8de33d

kind=LEXICAL
letterId=
display=учи́тель
note=teacher, tutor
objectiveId=obj-u3-gender
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-u3-хлеб`

- Objectives: obj-u3-gender
- Sessions: u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 81c7da009fba80294049d786e871474a07a15027b66329fbc115683e76a7c64c

kind=LEXICAL
letterId=
display=хлеб
note=bread, loaf, corn, crops, cereals, grain, living, means of subsistence, daily bread
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
- Content hash: 6ac2b15114ff4e2c939ee734e38b0f9503a9830a170d25d3aa7a01b3a84fe829

kind=LEXICAL
letterId=
display=спаси́бо
note=thanks, thank you
objectiveId=obj-u3-phrases
reviewStatus=PENDING_QUALIFIED_REVIEW

### EXAMPLE `ex-u3-phrase-пожалуйста`

- Objectives: obj-u3-phrases
- Sessions: u3-s-phrases
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 7fd089aed1386150332faeba57f5bd17f6391bbf84c8342a518d8c927cf9a456

kind=LEXICAL
letterId=
display=пожа́луйста
note=please; you are welcome; here you go
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
- Content hash: 5aedf75c9b95733047da6ce2ef14524b391cbf22d564ef3da4c267343fcf3b03

kind=SOURCE_GENDER
language=en
prompt=Which gender does the admitted record list for the shown noun?
promptLetter=стол
options=masculine | feminine | neuter
answer=masculine
feedback=The admitted record lists this noun as masculine. The shown form is стол.
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
- Content hash: d2f1e61835b8d10a85e09473f4fbfb5bd7014404155c6c74f98870bd9f24f646

kind=SOURCE_GENDER
language=en
prompt=Which gender does the admitted record list for the shown noun?
promptLetter=дом
options=masculine | feminine | neuter
answer=masculine
feedback=The admitted record lists this noun as masculine. The shown form is дом.
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
- Content hash: 3e75d05123405d1c5672a8089cc960465e2793822121d54de3d07c98fffc5162

kind=SOURCE_MEANING
language=en
prompt=Which admitted English gloss is recorded for the shown item?
promptLetter=здра́вствуйте
options=hello | thanks, thank you | please; you are welcome; here you go | yes | no; not | this is; that is; it
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
- Content hash: e149fe5300e8c0f91653cbe79b376fcfd5615a05048174dafb47b3b9f73e815b

kind=SOURCE_MEANING
language=en
prompt=Which admitted English gloss is recorded for the shown item?
promptLetter=спаси́бо
options=hello | thanks, thank you | please; you are welcome; here you go | yes | no; not | this is; that is; it
answer=thanks, thank you
feedback=The frozen dictionary row records this gloss: thanks, thank you.
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
- Content hash: 14b7b569f71fd7a735125b2e98c465de9b9729d9aa171f28a7316a0162115bdb

kind=SOURCE_MEANING
language=en
prompt=Which admitted English meaning is recorded for this source-supplied naming item?
promptLetter=мой стол
options=my table | my dog | What is this? | Who is this?
answer=my table
feedback=The frozen grammar page records this meaning: my table.
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
- Content hash: a05e71f21d7a7ed1c647ae7594ea00613d3190f489eb570fec85b16494b098e2

kind=SOURCE_GENDER
language=en
prompt=Which gender does the admitted record list for the shown noun?
promptLetter=го́род
options=masculine | feminine | neuter
answer=masculine
feedback=The admitted record lists this noun as masculine. The shown form is го́род.
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
- Content hash: a7f8ee0980aaa78d9f5965caf191aa9da24b83fb5b9788a60e3d6a7e5ee9c6b3

kind=SOURCE_GENDER
language=en
prompt=Which gender does the admitted record list for the shown noun?
promptLetter=друг
options=masculine | feminine | neuter
answer=masculine
feedback=The admitted record lists this noun as masculine. The shown form is друг.
objectiveId=obj-u3-gender
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW

### INDEPENDENT_CHECK `u3-i-gender-книга`

- Objectives: obj-u3-gender
- Sessions: u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 0a1ea5e7f8ccada261f5f6233f5933ba4f2286d44dcf1993b5ec40ebbc8e4c1d

kind=SOURCE_GENDER
language=en
prompt=Which gender does the admitted record list for the shown noun?
promptLetter=кни́га
options=masculine | feminine | neuter
answer=feminine
feedback=The admitted record lists this noun as feminine. The shown form is кни́га.
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
- Content hash: 872882a10a7aa1cc00224e9b6cf47164d7626aba675114af19d0dddae4311118

kind=SOURCE_GENDER
language=en
prompt=Which gender does the admitted record list for the shown noun?
promptLetter=дверь
options=masculine | feminine | neuter
answer=feminine
feedback=The admitted record lists this noun as feminine. The shown form is дверь.
objectiveId=obj-u3-gender
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW

### INDEPENDENT_CHECK `u3-i-gender-окно`

- Objectives: obj-u3-gender
- Sessions: u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 7c1e37c7aef389c1e4f075cfa0341d1bafeefbf1a0116c9bdf753ade26c2bba6

kind=SOURCE_GENDER
language=en
prompt=Which gender does the admitted record list for the shown noun?
promptLetter=окно́
options=masculine | feminine | neuter
answer=neuter
feedback=The admitted record lists this noun as neuter. The shown form is окно́.
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
- Content hash: a5fa54bea20776a7de0105c2e3f6f8931c10e50fa9e4b0e039d0ace282acea9e

kind=SOURCE_GENDER
language=en
prompt=Which gender does the admitted record list for the shown noun?
promptLetter=мо́ре
options=masculine | feminine | neuter
answer=neuter
feedback=The admitted record lists this noun as neuter. The shown form is мо́ре.
objectiveId=obj-u3-gender
containsGeneratedRussian=false
reviewStatus=PENDING_QUALIFIED_REVIEW

### INDEPENDENT_CHECK `u3-i-gender-мужчина`

- Objectives: obj-u3-gender
- Sessions: u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 8a9115d47d8f9ea6878db3bc5cce764747857660dc745821246bad76d0e454d8

kind=SOURCE_GENDER
language=en
prompt=Which gender does the admitted record list for the shown noun?
promptLetter=мужчи́на
options=masculine | feminine | neuter
answer=masculine
feedback=The admitted record lists this noun as masculine. The shown form is мужчи́на.
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
- Content hash: 4b19331d35652d08ecd6a64d7cb478ef614527fe475bea45fbe7303a9aa24dfd

kind=SOURCE_GENDER
language=en
prompt=Which gender does the admitted record list for the shown noun?
promptLetter=дя́дя
options=masculine | feminine | neuter
answer=masculine
feedback=The admitted record lists this noun as masculine. The shown form is дя́дя.
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
- Content hash: 6ac7728493d8842aa6b0d5496340f64ce1dc3dd60e22bf83445c09ef56078871

kind=SOURCE_MEANING
language=en
prompt=Which admitted English gloss is recorded for the shown item?
promptLetter=пожа́луйста
options=hello | thanks, thank you | please; you are welcome; here you go | yes | no; not | this is; that is; it
answer=please; you are welcome; here you go
feedback=The frozen dictionary row records this gloss: please; you are welcome; here you go.
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
- Content hash: de9b5f3fa78fd9272b3aef5286a70bbd17e5edf149341e1176ab7424e7f5288b

kind=SOURCE_MEANING
language=en
prompt=Which admitted English gloss is recorded for the shown item?
promptLetter=да
options=hello | thanks, thank you | please; you are welcome; here you go | yes | no; not | this is; that is; it
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
- Content hash: 61bfdf27317e189c71b2ee8a3d46bf5273a59d16a963c29c6eae541279cec066

kind=SOURCE_MEANING
language=en
prompt=Which admitted English gloss is recorded for the shown item?
promptLetter=нет
options=hello | thanks, thank you | please; you are welcome; here you go | yes | no; not | this is; that is; it
answer=no; not
feedback=The frozen dictionary row records this gloss: no; not.
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
- Content hash: f304e75ad09af62a5ac3eb7c0eb527ce51e25e7e39c4cac24da1a6804ce4e791

kind=SOURCE_MEANING
language=en
prompt=Which admitted English gloss is recorded for the shown item?
promptLetter=э́то
options=hello | thanks, thank you | please; you are welcome; here you go | yes | no; not | this is; that is; it
answer=this is; that is; it
feedback=The frozen dictionary row records this gloss: this is; that is; it.
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
- Content hash: 621042ac59741954a74eb0b45bc93b3ba334d57775fe54c1182165479e891903

kind=SOURCE_MEANING
language=en
prompt=Which admitted English meaning is recorded for this source-supplied naming item?
promptLetter=моя собака
options=my table | my dog | What is this? | Who is this?
answer=my dog
feedback=The frozen grammar page records this meaning: my dog.
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
- Content hash: 681b941987c21df8a039fc25f05f9d80954053758fbd98c9987d7a585000d8e2

kind=SOURCE_MEANING
language=en
prompt=Which admitted English meaning is recorded for this source-supplied naming item?
promptLetter=Что это?
options=my table | my dog | What is this? | Who is this?
answer=What is this?
feedback=The frozen grammar page records this meaning: What is this?.
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
- Content hash: 3ecc888cbc00293f5a56e3a5c9e5fe78fbc404f785019aeb27665896bed963d3

kind=SOURCE_MEANING
language=en
prompt=Which admitted English meaning is recorded for this source-supplied naming item?
promptLetter=Кто это?
options=my table | my dog | What is this? | Who is this?
answer=Who is this?
feedback=The frozen grammar page records this meaning: Who is this?.
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
- Content hash: 17ba96c9f8e0574526b4445857ee5b61d1a0c2b7869edf2665832d45a0d16f03

title=Later lesson topics stay in the book
summary=Units 6 and later do not contain lesson bodies.
detail=Units 3–5 contain proposed licensed records that still need qualified review. Open the matching lesson in the copy you own. This reference does not approve those records.
classification=OPERATIONAL_GUIDANCE
introducedInUnit=3
searchTerms=later lesson topics stay in the book
reviewStatus=NOT_REQUIRED

### PROVENANCE `prov-u3-dyadya-gender`

- Objectives: obj-u3-gender
- Sessions: u3-s-orient, u3-s-gender
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 2af9fa26acb3b1a7779ae3345dfc86bcc4e29844079b03a61b9ccf2478a29d76

recordType=lexical record
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Gender?oldid=4310736
sourceRevision=4310736
sourceRecordId=gender:inherently' masculine, and so '''дядя''' is too
originalSourceText=inherently' masculine, and so '''дядя''' is too
shippedText=дя́дя
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=a4e7831d644c7fce450cc3e68c7759187b7780fed30f1d2f986c7b0fe131c29c
contentHash=c21b8f3ed9a08e6c5708a94a4b2a81a6fecd39e85174f8dc2bfb5dc9658a3855
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
- Content hash: db97eabee760208c7e633824e442963ccc0e38c47c992d4dddde4f5f0f1c47c6

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/tree/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
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
- Content hash: 24015ec98d898a50dc79fac8b65f5cced2a3e2b414797cc869db621ea8edc794

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/tree/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
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
- Content hash: e73107c298e7995d0670c0ca8038937c0f1b9c4535921f560037348274bab951

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/tree/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
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
- Content hash: caa01883db70d786911b73b25f234e6b30f9afc7d5b554a10b3e877121e4a42b

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/tree/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
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
- Content hash: a3db2d2a6703de8681ddb43f241483d070eb1a3ed503b03d8ed6e5eb1e827ad1

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/tree/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
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
- Content hash: a05925afa33c66447ba2e0fb8e7b19f8ab2248556471e66d4f84ef091cbf65d4

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/tree/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
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
- Content hash: 65a6f34384247becd9d362c5d65c12ddd5cb91ecdad113a3eb956e45a9bb568a

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/tree/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
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
- Content hash: 1cb5f54f4498e343dcb1ca46755ea0c630b22a6794e10a8af4dabd3baeb54d03

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/tree/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
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
- Content hash: 58a81c533833c91964244fc47d508e5d869827ede9f36ba1586d171adfe211e2

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

### PROVENANCE `prov-u3-noun-мама`

- Objectives: obj-u3-gender
- Sessions: u3-s-orient, u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 6603828e2f91d2fba18eb2aa37a68065f166753ab0cd6005a5e1211e5429de42

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/tree/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
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
- Content hash: 3071d6010d4f644a17f7c2b0c97f121063e34a6a45c24a51814a2ab690d7a242

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/tree/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
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
- Content hash: a4c8dd400d58c7b6046a022c71fbc2611e31e0704347b4b16be5d73660e74438

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/tree/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
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
- Content hash: 6f3210c98739ad9b77559d7b26ee671e58883eff4f07c7ae761b373b552015b8

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/tree/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
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
- Content hash: bcf6a19034987e6d367aadb5c13f2b89b3fcd6b66d6ced5d7dcc926ce57fe3c8

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/tree/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
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
- Content hash: 99a5f27ef593e150dd1c1ea2835275e2967cf5654b300e6de2f5d4ac18a8d459

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

### PROVENANCE `prov-u3-noun-письмо`

- Objectives: obj-u3-gender
- Sessions: u3-s-orient, u3-s-gender
- Source: src-openrussian-50e210c4
- Rights: rights-openrussian-50e210c4
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 15e5f1c56394b8cba8a1055063b69c32bbaba5904b06bf08ddb48a9a89c99315

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/tree/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
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
- Content hash: fd1bf4f947d85be4e0a04b380eb7d1d43adb01172ba374d0e966fb9e95358951

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/tree/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
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
- Content hash: 0486a766d7a08590eefa365457f0f4e9f99dc1be7dbeb7126bee755c3fb953f6

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/tree/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
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
- Content hash: a54d0addf5fbd57f5e899af3ab3e0b0be83e0d1acb2f2b0c2184d2d68d4e71aa

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/tree/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
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
- Content hash: 4bc0008a1215f1f6be025e68a9094840debc14d0ea39375de3e1442b486290e8

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/tree/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
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
- Content hash: 4f612bdbcf9a3f71045ffb0ea0c1942cdeb1c90f50724caf32bcbd07e05a42ce

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/tree/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
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
- Content hash: af8fa0b98b3098975921b5c47d36318b592f100610baaee72dd50621fa2d51a3

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/tree/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
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
- Content hash: 877ce678dd7cac5ba2c725b3cc8e662f39c083d6fdb1bcc936a8c02d7ce4b2c4

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/tree/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
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
- Content hash: 7ddc75497a3422937a4d83149a47d6069abf66527485ea635bd68ef6dbbd3868

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/tree/50e210c4803237779cb562bc1abcea529066031c/nouns.csv
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
- Content hash: 7b7fcbb3a2741e9fca9ab220a6221c36e6f3983f3dca14bf4cc048d32a3655de

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

### PROVENANCE `prov-u3-phrase-gde-vy`

- Objectives: obj-u3-phrases
- Sessions: u3-s-orient, u3-s-phrases
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: c7b5b353d6ed2592e26bdb2078c42bc02464be837551409b167969e90d7ddc35

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Personal_Pronouns?oldid=3598527
sourceRevision=3598527
sourceRecordId=personal-pronouns:'''Где вы?'''
originalSourceText='''Где вы?'''
shippedText=Где вы?
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=74af680e3fa7a93f282fdf0c5e786e73b1d994fbc7743de25c92738d7a16edda
contentHash=9b3ccf75fb745f3774f33287d92bb92a5ff8ddb0b8072108137c9300897d7c75
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
- Content hash: 3d44450533c6f3f8e9fbec7f52f9cdb15860e86a3e57b638ed1f6872e69768a8

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

### PROVENANCE `prov-u3-phrase-kto-eto`

- Objectives: obj-u3-naming
- Sessions: u3-s-orient, u3-s-naming
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: a7786ed851a95ac3565937e710d589888023e55b43147341cb6c56cb4356ff8d

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

### PROVENANCE `prov-u3-phrase-moy-stol`

- Objectives: obj-u3-naming
- Sessions: u3-s-orient, u3-s-naming
- Source: src-wikibooks-russian-cc-by-sa
- Rights: rights-wikibooks-russian-cc-by-sa
- Classification: none
- Current review status: PENDING_QUALIFIED_REVIEW
- Content hash: 33fa6503d31051be674c7b8a9557ffd931f24dfb71b2b6300f5c89d1156fa1d1

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Gender?oldid=4310736
sourceRevision=4310736
sourceRecordId=gender:'''мой стол'''
originalSourceText='''мой стол'''
shippedText=мой стол
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=33bdfcd3cb40bb1f392cd19cb156718176d1506bb660edc5c0168425a89b5016
contentHash=2879fccf51dbf2edc6bf6261a7ffc585f1cb2ae5cc44756a53359c4e7159f109
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
- Content hash: 2be6a48c4234b92ff388733937a8397559903965875249214d40274be2d34eeb

recordType=example
sourceUrl=https://en.wikibooks.org/wiki/Russian/Grammar/Gender?oldid=4310736
sourceRevision=4310736
sourceRecordId=gender:'''моя собака'''
originalSourceText='''моя собака'''
shippedText=моя собака
transformation=selection
licenseId=CC BY-SA 4.0
licenseUrl=https://creativecommons.org/licenses/by-sa/4.0/
attribution=Wikibooks Russian grammar contributors, CC BY-SA 4.0. Page history for each frozen oldid identifies the contributors.
shareAlikeTreatment=Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. Application code, user data, and user recordings are outside that statement. No audio is relicensed because this unit ships no audio.
sourceHash=96b416892ea8294bb51c9c59ce3490190423e9798123d8a1b8ffdf194f1d29ae
contentHash=0cc1d7edfc689373a1837831b7432b54a1d85ced56a2b0916cca0f403bd597ed
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
- Content hash: aaaf98b6eb6d21ab1119806daccea9bd725a051f677111b8a97907aaee565e28

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/tree/50e210c4803237779cb562bc1abcea529066031c/others.csv
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
- Content hash: 27ac3f5215175a3daa4c2794d8f1611ca4372e416d09e6938bd5f3f2fc41082a

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/tree/50e210c4803237779cb562bc1abcea529066031c/others.csv
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
- Content hash: 568a103a28bdb47eb732119361c1de9e223c5b8aa259f8b598abb6133bdba81a

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/tree/50e210c4803237779cb562bc1abcea529066031c/others.csv
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
- Content hash: 9c315397a5028b6fc8bebcbfe3949fbcc64b2ed02c04116d61a9beb64d93d2c1

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/tree/50e210c4803237779cb562bc1abcea529066031c/others.csv
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
- Content hash: 1406d60e1b0891ca04d514e8243ad28e442a4d40852dd20c9c63cfc6f07aec78

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/tree/50e210c4803237779cb562bc1abcea529066031c/others.csv
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
- Content hash: 8f6062df215d220c1200eb3484c6626cc7695a88d55bb0d61cd7eb2a2932a492

recordType=lexical record
sourceUrl=https://github.com/Badestrand/russian-dictionary/tree/50e210c4803237779cb562bc1abcea529066031c/others.csv
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
- `obj-u3-gender` INSTRUCTION `u3-st-teach-gender`
- `obj-u3-gender` EXAMPLE `ex-u3-стол`
- `obj-u3-gender` EXAMPLE `ex-u3-дом`
- `obj-u3-gender` EXAMPLE `ex-u3-город`
- `obj-u3-gender` EXAMPLE `ex-u3-брат`
- `obj-u3-gender` EXAMPLE `ex-u3-учитель`
- `obj-u3-gender` EXAMPLE `ex-u3-хлеб`
- `obj-u3-gender` PRACTICE `u3-st-guided-gender`
- `obj-u3-gender` CHECK `u3-i-gender-город`
- `obj-u3-gender` CHECK `u3-i-gender-друг`
- `obj-u3-gender` CHECK `u3-i-gender-книга`
- `obj-u3-gender` CHECK `u3-i-gender-дверь`
- `obj-u3-gender` CHECK `u3-i-gender-окно`
- `obj-u3-gender` CHECK `u3-i-gender-море`
- `obj-u3-gender` CHECK `u3-i-gender-мужчина`
- `obj-u3-gender` CHECK `u3-i-gender-дядя`
- `obj-u3-gender` REMEDIATION `u3-st-rem-gender`
- `obj-u3-phrases` INSTRUCTION `u3-st-orient`
- `obj-u3-phrases` INSTRUCTION `u3-st-teach-phrases`
- `obj-u3-phrases` EXAMPLE `ex-u3-phrase-здравствуйте`
- `obj-u3-phrases` EXAMPLE `ex-u3-phrase-спасибо`
- `obj-u3-phrases` EXAMPLE `ex-u3-phrase-пожалуйста`
- `obj-u3-phrases` EXAMPLE `ex-u3-phrase-да`
- `obj-u3-phrases` PRACTICE `u3-st-guided-phrases`
- `obj-u3-phrases` CHECK `u3-i-phrase-пожалуйста`
- `obj-u3-phrases` CHECK `u3-i-phrase-да`
- `obj-u3-phrases` CHECK `u3-i-phrase-нет`
- `obj-u3-phrases` CHECK `u3-i-phrase-это`
- `obj-u3-phrases` REMEDIATION `u3-st-rem-phrases`
- `obj-u3-naming` INSTRUCTION `u3-st-orient`
- `obj-u3-naming` INSTRUCTION `u3-st-teach-naming`
- `obj-u3-naming` EXAMPLE `ex-u3-phrase-moy-stol`
- `obj-u3-naming` EXAMPLE `ex-u3-phrase-moya-sobaka`
- `obj-u3-naming` EXAMPLE `ex-u3-phrase-chto-eto`
- `obj-u3-naming` EXAMPLE `ex-u3-phrase-kto-eto`
- `obj-u3-naming` PRACTICE `u3-st-guided-naming`
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
- `u3-st-teach-gender` EXAMPLE `ex-u3-стол`
- `u3-st-teach-gender` EXAMPLE `ex-u3-дом`
- `u3-st-teach-gender` EXAMPLE `ex-u3-город`
- `u3-st-teach-gender` EXAMPLE `ex-u3-брат`
- `u3-st-teach-gender` EXAMPLE `ex-u3-учитель`
- `u3-st-teach-gender` EXAMPLE `ex-u3-хлеб`
- `u3-st-teach-gender` REFERENCE `ref-u35-attribution`
- `u3-st-rem-gender` EXAMPLE `ex-u3-стол`
- `u3-st-rem-gender` EXAMPLE `ex-u3-дом`
- `u3-st-guided-gender` REMEDIATION `u3-st-rem-gender`
- `u3-st-guided-gender` ITEM `u3-i-gender-стол`
- `u3-st-guided-gender` ITEM `u3-i-gender-дом`
- `u3-st-check-gender` REMEDIATION `u3-st-rem-gender`
- `u3-st-check-gender` ITEM `u3-i-gender-город`
- `u3-st-check-gender` ITEM `u3-i-gender-друг`
- `u3-st-check-gender` ITEM `u3-i-gender-книга`
- `u3-st-check-gender` ITEM `u3-i-gender-дверь`
- `u3-st-check-gender` ITEM `u3-i-gender-окно`
- `u3-st-check-gender` ITEM `u3-i-gender-море`
- `u3-st-check-gender` ITEM `u3-i-gender-мужчина`
- `u3-st-check-gender` ITEM `u3-i-gender-дядя`
- `u3-s-phrases` OBJECTIVE `obj-u3-phrases`
- `u3-st-teach-phrases` EXAMPLE `ex-u3-phrase-здравствуйте`
- `u3-st-teach-phrases` EXAMPLE `ex-u3-phrase-спасибо`
- `u3-st-teach-phrases` EXAMPLE `ex-u3-phrase-пожалуйста`
- `u3-st-teach-phrases` EXAMPLE `ex-u3-phrase-да`
- `u3-st-teach-phrases` REFERENCE `ref-u35-attribution`
- `u3-st-rem-phrases` EXAMPLE `ex-u3-phrase-здравствуйте`
- `u3-st-rem-phrases` EXAMPLE `ex-u3-phrase-спасибо`
- `u3-st-guided-phrases` REMEDIATION `u3-st-rem-phrases`
- `u3-st-guided-phrases` ITEM `u3-i-phrase-здравствуйте`
- `u3-st-guided-phrases` ITEM `u3-i-phrase-спасибо`
- `u3-st-check-phrases` REMEDIATION `u3-st-rem-phrases`
- `u3-st-check-phrases` ITEM `u3-i-phrase-пожалуйста`
- `u3-st-check-phrases` ITEM `u3-i-phrase-да`
- `u3-st-check-phrases` ITEM `u3-i-phrase-нет`
- `u3-st-check-phrases` ITEM `u3-i-phrase-это`
- `u3-s-naming` OBJECTIVE `obj-u3-naming`
- `u3-st-teach-naming` EXAMPLE `ex-u3-phrase-moy-stol`
- `u3-st-teach-naming` EXAMPLE `ex-u3-phrase-moya-sobaka`
- `u3-st-teach-naming` EXAMPLE `ex-u3-phrase-chto-eto`
- `u3-st-teach-naming` EXAMPLE `ex-u3-phrase-kto-eto`
- `u3-st-teach-naming` REFERENCE `ref-u35-attribution`
- `u3-st-rem-naming` EXAMPLE `ex-u3-phrase-moy-stol`
- `u3-st-rem-naming` EXAMPLE `ex-u3-phrase-moya-sobaka`
- `u3-st-guided-naming` REMEDIATION `u3-st-rem-naming`
- `u3-st-guided-naming` ITEM `u3-i-name-moy-stol`
- `u3-st-check-naming` REMEDIATION `u3-st-rem-naming`
- `u3-st-check-naming` ITEM `u3-i-name-moya-sobaka`
- `u3-st-check-naming` ITEM `u3-i-name-chto-eto`
- `u3-st-check-naming` ITEM `u3-i-name-kto-eto`
- `u3-s-book` OBJECTIVE `obj-u3-book`
- `u3-st-book-work` REFERENCE `ref-book-topics`
- `u3-st-book-work` REFERENCE `ref-u35-attribution`
- `obj-u3-gender` PROVENANCE `prov-u3-dyadya-gender`
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
- `obj-u3-phrases` PROVENANCE `prov-u3-phrase-gde-vy`
- `obj-u3-phrases` PROVENANCE `prov-u3-phrase-kak-dela`
- `obj-u3-naming` PROVENANCE `prov-u3-phrase-kto-eto`
- `obj-u3-naming` PROVENANCE `prov-u3-phrase-moy-stol`
- `obj-u3-naming` PROVENANCE `prov-u3-phrase-moya-sobaka`
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
