# Reviewer instructions for the Unit 22 packet

Schema version: 3
Application: dev.russiancompanion 2.0.0-dev (5)
Curriculum source commit: f8b8b09a3f82b14abbaa16f39ccbc514c1241fd7
Unit content-review digest: 5eedc53e9a70ce5450ad9ee6f4925047b94eab0517421114d742d605f730c3e7
Mapping-package digest: 135262f03abc4c4ef1dad6c992bbbf53993657e3ce3af4fe789fadcbebbff021
Pack content-review digest: ff2d4cd848036f7b164f9a68d24ef3d26d3442f4673c2bf1cf560e8d1b8662f1
Review-package digest: cd1ee74347d2315761749d9255bfb071108f7eaec1f67a516bc4df4be60d26c3
Repository source digest: f35d9a74cb095276e3c62e52fae2f3d7ef0dd013e4230a459411f3e3f458c5bc

This packet was generated from the packaged curriculum at curriculum source commit f8b8b09a3f82b14abbaa16f39ccbc514c1241fd7. That commit contains this unit's reviewed records. A later commit that changes only other units does not change this digest. This packet commit records that identity and does not perform review.

## Digests

`unitContentReviewDigest` (manifest field `contentReviewDigest`) binds the instructional curriculum for Unit 22. It is canonical JSON, digest schema 2, with scope `unit`. It includes this unit and the reachable instructional records, relationships, shared dependencies, sources, rights, assets, and advancement rule. It excludes review status, reviewer signature fields, the reviewed digest, mapping approval status, install state, and readiness. Recording those workflow values leaves this digest unchanged. A later change to substantive content reachable from this unit changes it and invalidates this unit's approval. A change that this unit cannot reach does not. Approval binds only to version 2.0.0-dev and this unit digest. Signing Unit 22 does not approve any other unit.

`mappingPackageDigest` binds the proposed edition-mapping facts in fixed order: unit number, mapping record id, edition title, author, publisher imprint, observed-edition notes, proposed lesson number, printed page start, printed page end, topic description, observation source, ISBN observation state, ISBN value or null, and the current mapping disposition. Reviewer identity, reviewer qualification, review date, reviewer notes, correction notes, and signature are outside this digest. Completing those workflow fields does not rewrite the mapping facts. Changing the lesson, page range, title, author, publisher, topic, observation source, ISBN state, or ISBN value changes this digest.

`packContentReviewDigest` is the pack-wide instructional aggregate used for repository and release integrity. It is not the digest a unit reviewer signs, and it is not hashed into `reviewPackageDigest`.

`reviewPackageDigest` identifies the complete pending review package. It binds the manifest metadata, ordered instructional records, relationship hashes, book-directed and unavailable lists, the pending instructional workflow fields, the curriculum source commit, the unit content-review digest, and `mappingPackageDigest`. A changed mapping fact changes this package digest because the mapping-package digest is an input. It does not approve the curriculum by itself.

`repositorySourceDigest` binds repository and release inputs. It tracks the repository and release state after an approval is recorded. A later commit changes it. Release-gate validation and the state-transition rules still have to accept that repository state. It is not the digest the reviewer signs, and it is not a substitute for `mappingPackageDigest`.

`curriculumSourceCommit` is the commit that contains this unit's governed curriculum. It is an ancestor of the packet commit. A packet that names its own commit, or a commit that does not contain this curriculum, is rejected.

## Qualifications

The repository does not name, appoint, or attest a reviewer. Reviewer identity and qualification stay blank. IMPLEMENTED_PENDING_FINAL_AUDIT records implementation only. It is not RESEARCH_VERIFIED, FINAL_CURRICULUM_AUDIT_PASSED, INDEPENDENT_AUDIT_VERIFIED, or OWNER_ACCEPTED. Green CI, a counter, self-attestation, or a model name does not fill those stages.

## What must be checked

Check every Cyrillic identity, phonetic statement, instructional explanation, answer key, feedback item, remediation statement, lesson placement, and mapping row. Compare the mapping worksheet with the Product Owner's legally owned edition. Do not copy protected text, exercises, answer keys, screenshots, or scans into the repository or the worksheet.

The package marker scan detects only a fixed list of known prohibited mapping-input markers, such as the private mapping-input filename or a PDF extension inside the mapping worksheet. It is not comprehensive copyright detection. It cannot recognize every copy or close paraphrase of protected material. The final curriculum audit still has to make that comparison. It does not claim physical possession of the owned book.

## What approval means

A later OWNER_ACCEPTED disposition applies only to version 2.0.0-dev, unit content-review digest 5eedc53e9a70ce5450ad9ee6f4925047b94eab0517421114d742d605f730c3e7, and mapping-package digest 135262f03abc4c4ef1dad6c992bbbf53993657e3ce3af4fe789fadcbebbff021, and only after RESEARCH_VERIFIED, IMPLEMENTED_PENDING_FINAL_AUDIT, FINAL_CURRICULUM_AUDIT_PASSED, and INDEPENDENT_AUDIT_VERIFIED are recorded for that same digest. Confirm review-package digest cd1ee74347d2315761749d9255bfb071108f7eaec1f67a516bc4df4be60d26c3 and curriculum source commit f8b8b09a3f82b14abbaa16f39ccbc514c1241fd7 before relying on this packet. Any later substantive instructional change reachable from Unit 22 invalidates downstream audit states. Any later change to the proposed edition facts invalidates the mapping-package digest. Leave correction comment, reviewer identity, reviewer qualification, review date, and reviewed digest blank. The mapping disposition in this packet remains PENDING_CURRICULUM_REVIEWER. CI does not approve that mapping.

Unresolved findings, corrections, or a missing owned-edition comparison prevent APPROVED, MAPPING_APPROVED, INSTALLABLE, and INSTALLED. This packet must not be used to mark Unit 22 reviewed, installable, installed, or as an approved edition alignment. A packet for one unit cannot approve another unit.

## Generator

The deterministic generator and validator live in `app/src/test/java/dev/russiancompanion/v2/review/Unit1ReviewPackage.kt` and accept a unit number from 1 through 30. Regenerate the files in `review/unit-22/` from the packaged curriculum after any instructional change, then review the new digest.
