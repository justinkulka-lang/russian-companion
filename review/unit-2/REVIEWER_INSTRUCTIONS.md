# Reviewer instructions for the Unit 2 packet

Schema version: 3
Application: dev.russiancompanion 2.0.0-dev (5)
Curriculum source commit: 4a6c21da07b515df226a090cc0ff6b1ff4a78aa1
Unit content-review digest: d51310f9ac1596ab50d2c5df7dffd9b10db23d712875680dbfb942bf577e056b
Mapping-package digest: 9f1b5a6f3efe2a92180b73da24d8291c4f6d6d79f193d034baab9414637f2ae3
Pack content-review digest: f5857a7268eb71ff985151ed98f6d2d8054e429621ae18014fe13760752684b5
Review-package digest: bb4ed50ed544251586d42c7cd6dfe12d04dde8bd2cab301ff0a28033ab76dacb
Repository source digest: eeba77e6c7969558d3e0dd303b1a43748aeb46a0e5c26dec75d909127c686e0e

This packet was generated from the packaged curriculum at curriculum source commit 4a6c21da07b515df226a090cc0ff6b1ff4a78aa1. That commit contains the reviewed curriculum. This packet commit records that identity and does not perform review.

## Digests

`unitContentReviewDigest` (manifest field `contentReviewDigest`) binds the instructional curriculum for Unit 2. It is canonical JSON, digest schema 2, with scope `unit`. It includes this unit and the reachable instructional records, relationships, shared dependencies, sources, rights, assets, and advancement rule. It excludes review status, reviewer signature fields, the reviewed digest, mapping approval status, install state, and readiness. Recording those workflow values leaves this digest unchanged. A later change to substantive content reachable from this unit changes it and invalidates this unit's approval. A change that this unit cannot reach does not. Approval binds only to version 2.0.0-dev and this unit digest. Signing Unit 2 does not approve any other unit.

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

A later approval applies only to version 2.0.0-dev, unit content-review digest d51310f9ac1596ab50d2c5df7dffd9b10db23d712875680dbfb942bf577e056b, and mapping-package digest 9f1b5a6f3efe2a92180b73da24d8291c4f6d6d79f193d034baab9414637f2ae3. Confirm review-package digest bb4ed50ed544251586d42c7cd6dfe12d04dde8bd2cab301ff0a28033ab76dacb and curriculum source commit 4a6c21da07b515df226a090cc0ff6b1ff4a78aa1 before relying on this packet. Any later substantive instructional change reachable from Unit 2 invalidates this unit's content approval. Any later change to the proposed edition facts invalidates the mapping-package digest. Leave correction comment, reviewer identity, reviewer qualification, review date, and reviewed digest blank until that human review happens. Allowed linguistic-review disposition values are APPROVED, CORRECTION_REQUIRED, and REJECTED. The mapping disposition in this packet remains PENDING_CURRICULUM_REVIEWER.

Unresolved findings, corrections, or a missing owned-edition comparison prevent APPROVED, MAPPING_APPROVED, INSTALLABLE, and INSTALLED. This packet must not be used to mark Unit 2 reviewed, installable, installed, or as an approved edition alignment. A packet for one unit cannot approve another unit.

## Generator

The deterministic generator and validator live in `app/src/test/java/dev/russiancompanion/v2/review/Unit1ReviewPackage.kt` and accept a unit number from 1 through 30. Regenerate the files in `review/unit-2/` from the packaged curriculum after any instructional change, then review the new digest.
