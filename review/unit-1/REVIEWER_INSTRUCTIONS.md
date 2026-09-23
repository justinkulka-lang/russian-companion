# Reviewer instructions for the Unit 1 packet

Schema version: 3
Application: dev.russiancompanion 2.0.0-dev (5)
Curriculum source commit: 49eae12f175574add477ba4f15224650abaa2293
Content-review digest: 048d9e59d3a8f4fea4f88a392d40d28b5eb62677f78f5461fe9ff2bf61a6d627
Review-package digest: 1a6d633f8bbb716d27a97ba4e44825ea4cb88208334d0e167f111854fcdcd296
Repository source digest: 82aa64e96d2ef5edbba5ae9bfb58ffd9b39fbe06741ecff5b26cfd9e322783fe

This packet was generated from the packaged curriculum. It does not perform review.

## Digests

`contentReviewDigest` is what the reviewer approves. It is canonical JSON, digest schema 2. It includes Russian and English instructional content, examples, options, answers, feedback, remediation content and relationships, objectives, sessions, steps, mapping notes, sources, rights, provenance, classifications, audio and text relationships, and advancement rules. It excludes review status, reviewer signature fields, the reviewed digest, mapping approval status, install state, and readiness. Recording those workflow values leaves this digest unchanged. A later change to substantive instructional, mapping, source, or rights content changes it and invalidates the affected approval. Approval binds only to version 2.0.0-dev and this digest.

`reviewPackageDigest` identifies which packet was reviewed: manifest metadata, ordered records, per-record hashes including objective and session relationships, every instructional edge, book-directed and unavailable lists, the pending workflow fields in this packet, and the content-review digest. It does not approve the curriculum by itself.

`repositorySourceDigest` is the release-gate hash of repository inputs. It tracks the repository and release state after an approval is recorded. A later commit changes it. Release-gate validation and the state-transition rules still have to accept that repository state. It is not the digest the reviewer signs.

## Qualifications

A reviewer must possess demonstrable advanced Russian competence and sufficient language-teaching or curriculum-review competence to validate Cyrillic letter identity, phonetics, instructional explanations, answer keys, feedback, remediation, and owned-edition placement. The repository does not name, appoint, or attest any reviewer. The reviewer qualification field stays blank until that person attests their own qualification.

## What must be checked

Check every Cyrillic identity, phonetic statement, instructional explanation, answer key, feedback item, remediation statement, lesson placement, and mapping row. Compare the mapping worksheet with the Product Owner's legally owned edition. Do not copy Penguin text, exercises, answer keys, screenshots, or scans into the repository or the worksheet.

## What approval means

A later approval applies only to version 2.0.0-dev and content-review digest 048d9e59d3a8f4fea4f88a392d40d28b5eb62677f78f5461fe9ff2bf61a6d627. Confirm review-package digest 1a6d633f8bbb716d27a97ba4e44825ea4cb88208334d0e167f111854fcdcd296 before relying on this packet. Any later substantive instructional change invalidates the affected approval. Leave disposition, correction comment, reviewer identity, reviewer qualification, review date, and reviewed digest blank until that human review happens. Allowed disposition values are APPROVED, CORRECTION_REQUIRED, and REJECTED.

Unresolved findings, corrections, or a missing owned-edition comparison prevent APPROVED, MAPPING_APPROVED, INSTALLABLE, and INSTALLED. This packet must not be used to mark Unit 1 reviewed, installable, installed, or as an approved edition alignment.

## Generator

The deterministic generator and validator live in `app/src/test/java/dev/russiancompanion/v2/review/Unit1ReviewPackage.kt`. Regenerate the files in `review/unit-1/` from the packaged curriculum after any instructional change, then review the new digest.
