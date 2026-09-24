# Reviewer instructions for the Unit 5 packet

Schema version: 3
Application: dev.russiancompanion 2.0.0-dev (5)
Curriculum source commit: 7e6b3f2de508062126ead6dcaa7cceae9fa9b49f
Unit content-review digest: 7a4f0d2602bacfc0e32f6ec07ebdd5cd3013a01480e996c370067ef3ccb1d937
Mapping-package digest: a1affd13da11e1173c23a2cab33b22f7ad6ea99aa54c99d54fc945fd6d7d0806
Pack content-review digest: 74e0cbc8db1287db0b8786cd5d8b57468ac9df9f3c79d45ace4729550afd271a
Review-package digest: f11c7ef10bc682f54b82a743406d17639049e6c68e94a69ead1759a1a779933f
Repository source digest: 86fc66ec7d4634bf6043b9a0e7aa3857203768abb74b7e7c75edabd6845e8101

This packet was generated from the packaged curriculum at curriculum source commit 7e6b3f2de508062126ead6dcaa7cceae9fa9b49f. That commit contains this unit's reviewed records. A later commit that changes only other units does not change this digest. This packet commit records that identity and does not perform review.

## Digests

`unitContentReviewDigest` (manifest field `contentReviewDigest`) binds the instructional curriculum for Unit 5. It is canonical JSON, digest schema 2, with scope `unit`. It includes this unit and the reachable instructional records, relationships, shared dependencies, sources, rights, assets, and advancement rule. It excludes review status, reviewer signature fields, the reviewed digest, mapping approval status, install state, and readiness. Recording those workflow values leaves this digest unchanged. A later change to substantive content reachable from this unit changes it and invalidates this unit's approval. A change that this unit cannot reach does not. Approval binds only to version 2.0.0-dev and this unit digest. Signing Unit 5 does not approve any other unit.

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

A later approval applies only to version 2.0.0-dev, unit content-review digest 7a4f0d2602bacfc0e32f6ec07ebdd5cd3013a01480e996c370067ef3ccb1d937, and mapping-package digest a1affd13da11e1173c23a2cab33b22f7ad6ea99aa54c99d54fc945fd6d7d0806. Confirm review-package digest f11c7ef10bc682f54b82a743406d17639049e6c68e94a69ead1759a1a779933f and curriculum source commit 7e6b3f2de508062126ead6dcaa7cceae9fa9b49f before relying on this packet. Any later substantive instructional change reachable from Unit 5 invalidates this unit's content approval. Any later change to the proposed edition facts invalidates the mapping-package digest. Leave correction comment, reviewer identity, reviewer qualification, review date, and reviewed digest blank until that human review happens. Allowed linguistic-review disposition values are APPROVED, CORRECTION_REQUIRED, and REJECTED. The mapping disposition in this packet remains PENDING_CURRICULUM_REVIEWER.

Unresolved findings, corrections, or a missing owned-edition comparison prevent APPROVED, MAPPING_APPROVED, INSTALLABLE, and INSTALLED. This packet must not be used to mark Unit 5 reviewed, installable, installed, or as an approved edition alignment. A packet for one unit cannot approve another unit.

## Generator

The deterministic generator and validator live in `app/src/test/java/dev/russiancompanion/v2/review/Unit1ReviewPackage.kt` and accept a unit number from 1 through 30. Regenerate the files in `review/unit-5/` from the packaged curriculum after any instructional change, then review the new digest.
