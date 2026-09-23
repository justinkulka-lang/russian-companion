# Reviewer instructions for the Unit 2 packet

Schema version: 3
Application: dev.russiancompanion 2.0.0-dev (5)
Curriculum source commit: 5dde572d36895eeec24d1159898d9e3d3998a488
Unit content-review digest: d51310f9ac1596ab50d2c5df7dffd9b10db23d712875680dbfb942bf577e056b
Pack content-review digest: c281e870526e733e96f2129bcd8c4deddc748e596698e327c881ac659bac88a4
Review-package digest: 7af9c0720f9c70b87ae1eca477cff0d6747d639f12b6a4e3f8ad77c72be8d1e0
Repository source digest: 3449b7a61e780fd8736e9c8e10e2cd41d68d14439000b93b1831e9b05b3831e9

This packet was generated from the packaged curriculum at curriculum source commit 5dde572d36895eeec24d1159898d9e3d3998a488. That commit contains the reviewed curriculum. This packet commit records that identity and does not perform review.

## Digests

`contentReviewDigest` is the unit-scoped digest the reviewer of Unit 2 approves. It is canonical JSON, digest schema 2, with scope `unit`. It includes this unit and the reachable instructional records, relationships, shared dependencies, sources, rights, assets, and advancement rule. It excludes review status, reviewer signature fields, the reviewed digest, mapping approval status, install state, and readiness. Recording those workflow values leaves this digest unchanged. A later change to substantive content reachable from this unit changes it and invalidates this unit's approval. A change that this unit cannot reach does not. Approval binds only to version 2.0.0-dev and this unit digest. Signing Unit 2 does not approve any other unit.

`packContentReviewDigest` is the pack-wide aggregate used for repository and release integrity. It is not the digest a unit reviewer signs, and it is not hashed into `reviewPackageDigest`.

`reviewPackageDigest` identifies which packet was reviewed: manifest metadata, ordered records, per-record hashes including objective and session relationships, every instructional edge, book-directed and unavailable lists, the pending workflow fields in this packet, the curriculum source commit, and the unit content-review digest. It does not approve the curriculum by itself.

`repositorySourceDigest` is the release-gate hash of repository inputs. It tracks the repository and release state after an approval is recorded. A later commit changes it. Release-gate validation and the state-transition rules still have to accept that repository state. It is not the digest the reviewer signs.

`curriculumSourceCommit` is the commit that contains this unit's governed curriculum. It is an ancestor of the packet commit. A packet that names its own commit, or a commit that does not contain this curriculum, is rejected.

## Qualifications

A reviewer must possess demonstrable advanced Russian competence and sufficient language-teaching or curriculum-review competence to validate Cyrillic letter identity, phonetics, instructional explanations, answer keys, feedback, remediation, and owned-edition placement. The repository does not name, appoint, or attest any reviewer. The reviewer qualification field stays blank until that person attests their own qualification.

## What must be checked

Check every Cyrillic identity, phonetic statement, instructional explanation, answer key, feedback item, remediation statement, lesson placement, and mapping row. Compare the mapping worksheet with the Product Owner's legally owned edition. Do not copy Penguin text, exercises, answer keys, screenshots, or scans into the repository or the worksheet.

## What approval means

A later approval applies only to version 2.0.0-dev and unit content-review digest d51310f9ac1596ab50d2c5df7dffd9b10db23d712875680dbfb942bf577e056b. Confirm review-package digest 7af9c0720f9c70b87ae1eca477cff0d6747d639f12b6a4e3f8ad77c72be8d1e0 and curriculum source commit 5dde572d36895eeec24d1159898d9e3d3998a488 before relying on this packet. Any later substantive change reachable from Unit 2 invalidates this unit's approval. Leave disposition, correction comment, reviewer identity, reviewer qualification, review date, and reviewed digest blank until that human review happens. Allowed disposition values are APPROVED, CORRECTION_REQUIRED, and REJECTED.

Unresolved findings, corrections, or a missing owned-edition comparison prevent APPROVED, MAPPING_APPROVED, INSTALLABLE, and INSTALLED. This packet must not be used to mark Unit 2 reviewed, installable, installed, or as an approved edition alignment. A packet for one unit cannot approve another unit.

## Generator

The deterministic generator and validator live in `app/src/test/java/dev/russiancompanion/v2/review/Unit1ReviewPackage.kt` and accept a unit number from 1 through 30. Regenerate the files in `review/unit-2/` from the packaged curriculum after any instructional change, then review the new digest.
