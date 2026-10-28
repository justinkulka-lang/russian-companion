## Separate reconciliation addendum for CURSOR-R2-001

This addendum does not edit the report, either CSV, or the scope JSON. CC047 stays `NOT_CLOSED`. Every original execution status stays `NOT_EXECUTED`. No broad-run outcome is claimed. The active worker was not inspected.

The cited archive was read in memory only. ZIP SHA-256 is `3ee399dfada9ecdcee7b79e89235264100e201fa4d0f432cfed10cc33ced5a37`, 63,397 bytes, matching `PARTIAL_UNIT14_V1.IDENTITY.json`. `testzip` found no bad CRC. Members:

- `manifest.json`, CRC `5b293f99`, 442 bytes, SHA-256 `7bbcde72115f18500b084f2664034f5a93b877e74c73ad947976b1be134126fb`
- `learner.json`, CRC `e3e3e288`, 336,085 bytes, SHA-256 `37ac5b88cdad3b01e645f7a59313db5bcf2c25f34081270ea12b5a26b2103959`

The manifest’s file hash matches that learner member. Counts are 12 review assignments and 26 review answers. Identity: `syntheticLearner` true, producer `f2628b363b241bce8bc71c25ca56b66aadd21dcd`, curriculum `9101af8c182b3415426ed9226cb211a707a2c393`, format 3, document schema 3, zero recordings. That curriculum commit is not pin `8eb9d3d4`.

`reviewAssignments[].schema` in this zip:

- schema 1: Unit 4 `unit4-prior-reading-gender-v1`, and Unit 14 `unit14-prior-reading-reported-time-v1`
- schema 2: Unit 3 `unit3-prior-literacy-v1`
- schema 7: Unit 5 `unit5-prior-gender-subject-v2`
- schema 8: Unit 6 `unit6-prior-present-subject-noun-behavior-v3`
- schema 9: Unit 7 `unit7-prior-gender-inanimate-case-v2`
- schema 10: Unit 8 `unit8-prior-present-nominative-agreement-v2`
- schema 11: Unit 9 `unit9-prior-noun-behavior-plural-gender-v3`
- schema 12: Unit 10 `unit10-prior-present-numeral-gender-v2`
- schema 13: Unit 11 `unit11-prior-noun-behavior-possession-absence-v2`
- schema 14: Unit 12 `unit12-prior-gender-past-gender-v2`
- schema 15: Unit 13 `unit13-prior-present-aspect-past-gender-v3`

Absent from these 12 assignments: schemas 3, 4, 5, 6, and 16. All 26 `reviewAnswers[].schema` values are 1. Answer schema 1 is not assignment schema 7–15.

Revised disposition, separate from the original rows:

- Schemas 7–15: evidence exists and the mapping omitted the citation. Each of those assignments is stored in this synthetic partial export, with its slot answers and a Unit 3–13 completion record. That corrects the original sentence that schemas 7–15 had no cited retained capture.
- That presence is not compatibility coverage. The zip has no import log, replaced-store copy, or reopen transcript. Profile is Unit 14, session `u14-s-orient`, step 0. The Unit 14 assignment is schema 1 / v1, and only slot 0 is answered (`А / а`). Unit 14 is not in `unitCompletions`. This file is a partial v1 export, not a completed Unit 14 review and not an import or reopen fixture.
- Schema 16: no assignment in this zip. The same absence holds in the two sibling captures opened for the gap check: `COMPLETED_UNIT14_V1.zip` (`ae9f31fa0949878092e7433cc512e0c6982ea25aeb5eb26ff40071f6ca9bfde2`, Unit 14 schema 1 slots 0 and 1, profile Unit 15) and `PARTIAL_UNIT15_OWNER14_V1.zip` (`5affb336bc4a4949603c149f923c5dc91033502aa2be152c0da2c0e827644392`, Unit 15 schema 1 slot 0 only). Neither uses `unit14-prior-plural-possession-reported-time-v2`. The baseline status text says schema 16 was not activated at that capture. The older 64 histories were not rehashed, so this does not reopen their schema 1–5 inventory.
- Schemas 3, 4, 5, and 6: not in this zip. Schema 6 remains the gap CC047 already reported for the older histories. This archive does not fill it.
- Completed Unit 14 v1 is the other file, `COMPLETED_UNIT14_V1.zip`, not this partial zip. Its Unit 14 record is still schema 1, slots 0 and 1.

CC047 remains `NOT_CLOSED`. Request 70 remains `NOT_EXECUTED`. The learner is synthetic host-lifecycle evidence, not one of the 64 historical archives, and not current-pin runtime evidence. Audit status remains INCOMPLETE. Physical Galaxy S26 Ultra airplane-mode and reboot acceptance remains outstanding and last. No merge, freeze, signing, or release approval.

Message 2 will contain `CURSOR_EXECUTION_EVIDENCE_MAP.csv`, header and rows 1–58 only. Rows 59–70 were already returned.