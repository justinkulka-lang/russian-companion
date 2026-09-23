# Reviewer instructions for the Unit 3 packet

Schema version: 3
Application: dev.russiancompanion 2.0.0-dev (5)
Curriculum source commit: 6cb691ddf7289cbfdff9f9227c784d2c3e58b591
Unit content-review digest: 0213e05c43a6b185ab5bd6474b6cd8541cf8343189c9f8f729d4cc70f668e364
Pack content-review digest: f284264f8cd213f46194ed637d4a6d8694fea4ed146a6f570116c5fb094bbf9c
Review-package digest: bb9247bd3dfeacbea2105ba60be008f6d386437a3bae5fd2de9f40ce561056f0
Repository source digest: 96bd29ec7785087d1395369fc33f422a914dadbf02fd7a8414b3a9c6c8960c11

This packet was generated from the packaged curriculum at curriculum source commit 6cb691ddf7289cbfdff9f9227c784d2c3e58b591. That commit contains the reviewed curriculum. This packet commit records that identity and does not perform review.

## Digests

`contentReviewDigest` is the unit-scoped digest the reviewer of Unit 3 approves. It is canonical JSON, digest schema 2, with scope `unit`. It includes this unit and the reachable instructional records, relationships, shared dependencies, sources, rights, assets, and advancement rule. It excludes review status, reviewer signature fields, the reviewed digest, mapping approval status, install state, and readiness. Recording those workflow values leaves this digest unchanged. A later change to substantive content reachable from this unit changes it and invalidates this unit's approval. A change that this unit cannot reach does not. Approval binds only to version 2.0.0-dev and this unit digest. Signing Unit 3 does not approve any other unit.

`packContentReviewDigest` is the pack-wide aggregate used for repository and release integrity. It is not the digest a unit reviewer signs, and it is not hashed into `reviewPackageDigest`.

`reviewPackageDigest` identifies which packet was reviewed: manifest metadata, ordered records, per-record hashes including objective and session relationships, every instructional edge, book-directed and unavailable lists, the pending workflow fields in this packet, the curriculum source commit, and the unit content-review digest. It does not approve the curriculum by itself.

`repositorySourceDigest` is the release-gate hash of repository inputs. It tracks the repository and release state after an approval is recorded. A later commit changes it. Release-gate validation and the state-transition rules still have to accept that repository state. It is not the digest the reviewer signs.

`curriculumSourceCommit` is the commit that contains this unit's governed curriculum. It is an ancestor of the packet commit. A packet that names its own commit, or a commit that does not contain this curriculum, is rejected.

## Qualifications

A reviewer must possess demonstrable advanced Russian competence and sufficient language-teaching or curriculum-review competence to validate Cyrillic letter identity, phonetics, instructional explanations, answer keys, feedback, remediation, and owned-edition placement. The repository does not name, appoint, or attest any reviewer. The reviewer qualification field stays blank until that person attests their own qualification.

## What must be checked

Check every Cyrillic identity, phonetic statement, instructional explanation, answer key, feedback item, remediation statement, lesson placement, and mapping row. Compare the mapping worksheet with the Product Owner's legally owned edition. Do not copy Penguin text, exercises, answer keys, screenshots, or scans into the repository or the worksheet.

## What approval means

A later approval applies only to version 2.0.0-dev and unit content-review digest 0213e05c43a6b185ab5bd6474b6cd8541cf8343189c9f8f729d4cc70f668e364. Confirm review-package digest bb9247bd3dfeacbea2105ba60be008f6d386437a3bae5fd2de9f40ce561056f0 and curriculum source commit 6cb691ddf7289cbfdff9f9227c784d2c3e58b591 before relying on this packet. Any later substantive change reachable from Unit 3 invalidates this unit's approval. Leave disposition, correction comment, reviewer identity, reviewer qualification, review date, and reviewed digest blank until that human review happens. Allowed disposition values are APPROVED, CORRECTION_REQUIRED, and REJECTED.

Unresolved findings, corrections, or a missing owned-edition comparison prevent APPROVED, MAPPING_APPROVED, INSTALLABLE, and INSTALLED. This packet must not be used to mark Unit 3 reviewed, installable, installed, or as an approved edition alignment. A packet for one unit cannot approve another unit.

## Generator

The deterministic generator and validator live in `app/src/test/java/dev/russiancompanion/v2/review/Unit1ReviewPackage.kt` and accept a unit number from 1 through 30. Regenerate the files in `review/unit-3/` from the packaged curriculum after any instructional change, then review the new digest.
