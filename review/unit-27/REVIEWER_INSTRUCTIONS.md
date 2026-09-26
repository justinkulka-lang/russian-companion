# Reviewer instructions for the Unit 27 packet

Schema version: 3
Application: dev.russiancompanion 2.0.0-dev (5)
Curriculum source commit: 94b6647abd51848ec8208a52d08ce57c5ff4790a
Unit content-review digest: 35a9b402c66b5a657d991bd6aded6ecf550a9e818f1db7b8dbc26a2ded7f851d
Mapping-package digest: f5da4227d7bdfa500293d76f3b48ab8ac7646ac0dd6c90b4e8ac4f10cc69a385
Pack content-review digest: 02fcae8d023970ce7abfaa6cbc94feaa358bbfa0fef2ac2f645fb5b454ce8a46
Review-package digest: de4ceeaf9d898ddc808cfdd2cd8b089f0b06a8ecbb70ef73236d982e16f82e3f
Repository source digest: e5a04c23be99fb1434f457ad7241d0e7835cf71b98f06f385e74ce188a2ded1f

This packet was generated from the packaged curriculum at curriculum source commit 94b6647abd51848ec8208a52d08ce57c5ff4790a. That commit contains this unit's reviewed records. A later commit that changes only other units does not change this digest. This packet commit records that identity and does not perform review.

## Digests

`unitContentReviewDigest` (manifest field `contentReviewDigest`) binds the instructional curriculum for Unit 27. It is canonical JSON, digest schema 2, with scope `unit`. It includes this unit and the reachable instructional records, relationships, shared dependencies, sources, rights, assets, and advancement rule. It excludes review status, reviewer signature fields, the reviewed digest, mapping approval status, install state, and readiness. Recording those workflow values leaves this digest unchanged. A later change to substantive content reachable from this unit changes it and invalidates this unit's approval. A change that this unit cannot reach does not. Approval binds only to version 2.0.0-dev and this unit digest. Signing Unit 27 does not approve any other unit.

`mappingPackageDigest` binds the proposed edition-mapping facts in fixed order: unit number, mapping record id, edition title, author, publisher imprint, observed-edition notes, proposed lesson number, printed page start, printed page end, topic description, observation source, ISBN observation state, ISBN value or null, and the current mapping disposition. Reviewer identity, reviewer qualification, review date, reviewer notes, correction notes, and signature are outside this digest. Completing those workflow fields does not rewrite the mapping facts. Changing the lesson, page range, title, author, publisher, topic, observation source, ISBN state, or ISBN value changes this digest.

`packContentReviewDigest` is the pack-wide instructional aggregate used for repository and release integrity. It is not the digest a unit reviewer signs, and it is not hashed into `reviewPackageDigest`.

`reviewPackageDigest` identifies the complete pending review package. It binds the manifest metadata, ordered instructional records, relationship hashes, book-directed and unavailable lists, the pending instructional workflow fields, the curriculum source commit, the unit content-review digest, and `mappingPackageDigest`. A changed mapping fact changes this package digest because the mapping-package digest is an input. It does not approve the curriculum by itself.

`repositorySourceDigest` binds repository and release inputs. It tracks the repository and release state after an approval is recorded. A later commit changes it. Release-gate validation and the state-transition rules still have to accept that repository state. It is not the digest the reviewer signs, and it is not a substitute for `mappingPackageDigest`.

`curriculumSourceCommit` is the commit that contains this unit's governed curriculum. It is an ancestor of the packet commit. A packet that names its own commit, or a commit that does not contain this curriculum, is rejected.

## Qualifications

A reviewer must possess demonstrable advanced Russian competence and sufficient language-teaching or curriculum-review competence to validate Cyrillic letter identity, phonetics, instructional explanations, answer keys, feedback, remediation, and owned-edition placement. The repository does not name, appoint, or attest any reviewer. The reviewer qualification field stays blank until that person attests their own qualification.

## What must be checked

Check every Cyrillic identity, phonetic statement, instructional explanation, answer key, feedback item, remediation statement, lesson placement, and mapping row. Compare the mapping worksheet with the Product Owner's legally owned edition. Do not copy protected text, exercises, answer keys, screenshots, or scans into the repository or the worksheet.

The package marker scan detects only a fixed list of known prohibited mapping-input markers, such as the private mapping-input filename or a PDF extension inside the mapping worksheet. It is not comprehensive copyright detection. It cannot recognize every copy or close paraphrase of protected material. A qualified reviewer still has to make that comparison.

## What approval means

A later approval applies only to version 2.0.0-dev, unit content-review digest 35a9b402c66b5a657d991bd6aded6ecf550a9e818f1db7b8dbc26a2ded7f851d, and mapping-package digest f5da4227d7bdfa500293d76f3b48ab8ac7646ac0dd6c90b4e8ac4f10cc69a385. Confirm review-package digest de4ceeaf9d898ddc808cfdd2cd8b089f0b06a8ecbb70ef73236d982e16f82e3f and curriculum source commit 94b6647abd51848ec8208a52d08ce57c5ff4790a before relying on this packet. Any later substantive instructional change reachable from Unit 27 invalidates this unit's content approval. Any later change to the proposed edition facts invalidates the mapping-package digest. Leave correction comment, reviewer identity, reviewer qualification, review date, and reviewed digest blank until that human review happens. Allowed linguistic-review disposition values are APPROVED, CORRECTION_REQUIRED, and REJECTED. The mapping disposition in this packet remains PENDING_CURRICULUM_REVIEWER.

Unresolved findings, corrections, or a missing owned-edition comparison prevent APPROVED, MAPPING_APPROVED, INSTALLABLE, and INSTALLED. This packet must not be used to mark Unit 27 reviewed, installable, installed, or as an approved edition alignment. A packet for one unit cannot approve another unit.

## Generator

The deterministic generator and validator live in `app/src/test/java/dev/russiancompanion/v2/review/Unit1ReviewPackage.kt` and accept a unit number from 1 through 30. Regenerate the files in `review/unit-27/` from the packaged curriculum after any instructional change, then review the new digest.
