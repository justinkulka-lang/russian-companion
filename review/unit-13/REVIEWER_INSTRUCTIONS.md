# Reviewer instructions for the Unit 13 packet

Schema version: 3
Application: dev.russiancompanion 2.0.0-dev (5)
Curriculum source commit: e465017d6e6c14d6ca6cda390be0ef1fd7a95035
Unit content-review digest: 1091aa93b46aca71f711a41b6ee9d26102d98d342e3a41e459156e9f12ac0d13
Mapping-package digest: e8ca3b0ba9b6a48acc54ea18e10a6e6fb1291698252fcbc35b02dbf2a820deb4
Pack content-review digest: 4d2d7078afd288289e300f082e641e637fda261d486fa9f1c6b91c014c5b3192
Review-package digest: 7065433a428496bf23fc6a90d7d3681b982872359a6cfc2f352ee8ed0226d921
Repository source digest: a9cde918178bad5957d1e047d07c81ee3bbbff7d4894dd8105d110bbaf76e46e

This packet was generated from the packaged curriculum at curriculum source commit e465017d6e6c14d6ca6cda390be0ef1fd7a95035. That commit contains this unit's reviewed records. A later commit that changes only other units does not change this digest. This packet commit records that identity and does not perform review.

## Digests

`unitContentReviewDigest` (manifest field `contentReviewDigest`) binds the instructional curriculum for Unit 13. It is canonical JSON, digest schema 3, with scope `unit`. It includes this unit and the reachable instructional records, relationships, shared dependencies, sources, rights, assets, and advancement rule. It excludes review status, reviewer signature fields, the reviewed digest, mapping approval status, install state, and readiness. Recording those workflow values leaves this digest unchanged. A later change to substantive content reachable from this unit changes it and invalidates this unit's approval. A change that this unit cannot reach does not. Approval binds only to version 2.0.0-dev and this unit digest. Signing Unit 13 does not approve any other unit.

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

A later OWNER_ACCEPTED disposition applies only to version 2.0.0-dev, unit content-review digest 1091aa93b46aca71f711a41b6ee9d26102d98d342e3a41e459156e9f12ac0d13, and mapping-package digest e8ca3b0ba9b6a48acc54ea18e10a6e6fb1291698252fcbc35b02dbf2a820deb4, and only after RESEARCH_VERIFIED, IMPLEMENTED_PENDING_FINAL_AUDIT, FINAL_CURRICULUM_AUDIT_PASSED, and INDEPENDENT_AUDIT_VERIFIED are recorded for that same digest. Confirm review-package digest 7065433a428496bf23fc6a90d7d3681b982872359a6cfc2f352ee8ed0226d921 and curriculum source commit e465017d6e6c14d6ca6cda390be0ef1fd7a95035 before relying on this packet. Any later substantive instructional change reachable from Unit 13 invalidates downstream audit states. Any later change to the proposed edition facts invalidates the mapping-package digest. Leave correction comment, reviewer identity, reviewer qualification, review date, and reviewed digest blank. The mapping disposition in this packet remains PENDING_CURRICULUM_REVIEWER. CI does not approve that mapping.

Unresolved findings, corrections, or a missing owned-edition comparison prevent APPROVED, MAPPING_APPROVED, INSTALLABLE, and INSTALLED. This packet must not be used to mark Unit 13 reviewed, installable, installed, or as an approved edition alignment. A packet for one unit cannot approve another unit.

## Generator

The deterministic generator and validator live in `app/src/test/java/dev/russiancompanion/v2/review/Unit1ReviewPackage.kt` and accept a unit number from 1 through 30. Regenerate the files in `review/unit-13/` from the packaged curriculum after any instructional change, then review the new digest.
