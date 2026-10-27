# Reviewer instructions for the Unit 24 packet

Schema version: 3
Application: dev.russiancompanion 2.0.0-dev (5)
Curriculum source commit: dc4aa918f6098e17f8ffb8b297b356770a0f10d3
Unit content-review digest: d5cb2b37f1000c8728b529e4167f3ab06fb216cb12c7a6d317531f1b309c2e66
Mapping-package digest: dc6f9ac41a8520dc647df90313e05e547245bfa31fe3b2e883821f3591a24248
Pack content-review digest: 7de1c93911dc80388fa10bd6d0d734fc64ace7e6cebbc5ad8e008d1c25c6366c
Review-package digest: ba85ebee376a139c9198c859bf5410a8db9f3bc6ce329207356f0ff28c92101f
Repository source digest: 84b6fda72017ec2dfc2b4add6c0e82b1a1e9f40769773f18d50caee4cf7a2355

This packet was generated from the packaged curriculum at curriculum source commit dc4aa918f6098e17f8ffb8b297b356770a0f10d3. That commit contains this unit's reviewed records. A later commit that changes only other units does not change this digest. This packet commit records that identity and does not perform review.

## Digests

`unitContentReviewDigest` (manifest field `contentReviewDigest`) binds the instructional curriculum for Unit 24. It is canonical JSON, digest schema 3, with scope `unit`. It includes this unit and the reachable instructional records, relationships, shared dependencies, sources, rights, assets, and advancement rule. It excludes review status, reviewer signature fields, the reviewed digest, mapping approval status, install state, and readiness. Recording those workflow values leaves this digest unchanged. A later change to substantive content reachable from this unit changes it and invalidates this unit's approval. A change that this unit cannot reach does not. Approval binds only to version 2.0.0-dev and this unit digest. Signing Unit 24 does not approve any other unit.

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

A later OWNER_ACCEPTED disposition applies only to version 2.0.0-dev, unit content-review digest d5cb2b37f1000c8728b529e4167f3ab06fb216cb12c7a6d317531f1b309c2e66, and mapping-package digest dc6f9ac41a8520dc647df90313e05e547245bfa31fe3b2e883821f3591a24248, and only after RESEARCH_VERIFIED, IMPLEMENTED_PENDING_FINAL_AUDIT, FINAL_CURRICULUM_AUDIT_PASSED, and INDEPENDENT_AUDIT_VERIFIED are recorded for that same digest. Confirm review-package digest ba85ebee376a139c9198c859bf5410a8db9f3bc6ce329207356f0ff28c92101f and curriculum source commit dc4aa918f6098e17f8ffb8b297b356770a0f10d3 before relying on this packet. Any later substantive instructional change reachable from Unit 24 invalidates downstream audit states. Any later change to the proposed edition facts invalidates the mapping-package digest. Leave correction comment, reviewer identity, reviewer qualification, review date, and reviewed digest blank. The mapping disposition in this packet remains PENDING_CURRICULUM_REVIEWER. CI does not approve that mapping.

Unresolved findings, corrections, or a missing owned-edition comparison prevent APPROVED, MAPPING_APPROVED, INSTALLABLE, and INSTALLED. This packet must not be used to mark Unit 24 reviewed, installable, installed, or as an approved edition alignment. A packet for one unit cannot approve another unit.

## Generator

The deterministic generator and validator live in `app/src/test/java/dev/russiancompanion/v2/review/Unit1ReviewPackage.kt` and accept a unit number from 1 through 30. Regenerate the files in `review/unit-24/` from the packaged curriculum after any instructional change, then review the new digest.
