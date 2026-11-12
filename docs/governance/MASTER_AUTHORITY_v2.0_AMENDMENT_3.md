# MASTER AUTHORITY v2.0 — Amendment 3 (AI voice audio)

Status: RATIFIED by the Product Owner on 11/12/2026. Controlling once committed under docs/governance/.
Basis: Owner direction of 11/12/2026: "Let's use AI where it's needed." Year-One Audio Plan (docs/audio/AUDIO_PLAN.md), sections 1 and 9.
Scope: synthetic (AI-generated) audio of reviewed pack text only.
Unchanged: every other MA2, Amendment 1 and Amendment 2 rule, including offline only and no runtime AI (MA2-009 to MA2-015), generate no Russian text, no scoring of pronunciation or open writing (MA2-059), no speech recognition (MA2-060), rights rules (MA2-092 to MA2-111) and public-release blocking (MA2-007, MA2-174).

This is product governance, not legal advice.

---

## Part 1 — What AI audio may do

A3-01. Gap filling only. A synthetic clip may be used for a manifest row only when no verified native clip exists for it. Native audio comes first: a verified Shtooka match or a commissioned tutor recording.

A3-02. Text, not new Russian. A synthetic clip speaks one exact string from the reviewed pack. It never creates, alters or completes Russian text. Generating audio of reviewed text is not "generated Russian" under the existing rule, but the clip is still treated as unverified until A3-05 is met.

A3-03. Build time only. Clips are generated outside the app, during development, and bundled as ordinary audio files. The app stays offline and calls no AI service at runtime (MA2-014 unchanged). API keys are never committed to the repository.

## Part 2 — Where AI audio is not allowed

A3-04. Native only. These rows stay native-only and are never filled with a synthetic clip:
- the 33 letter rows (name and sound)
- Units 1–2 foundation pronunciation content
- stress pairs and homographs (за́мок/замо́к, му́ка/мука́, о́кна/окна́ and any later pair)
- final-devoicing and other sound-contrast pairs
- dictation items (A2-20, A2-22, A2-23)
- any row whose text has no stress mark from a licensed source. The stress mark must come from the source, never inferred.

These rows wait for the tutor recording.

## Part 3 — Verification

A3-05. Stress-marked input. Every synthetic clip is generated from the stress-marked form of the text, with stress taken from a licensed source and recorded in the manifest row.

A3-06. Pilot gate. Before any synthetic clip is bound, run a pilot: 30 lines from Units 3–5, generated with the chosen voice and settings. A native speaker (the italki tutor) listens to all 30 and marks each correct or wrong, especially for stress. If 2 or more are wrong, synthetic audio is not used until the voice or settings change and a new pilot passes.

A3-07. Per-clip check. After the pilot passes, every synthetic clip goes through the unit's listen-check sheet (A2-18). Only clips marked Match are bound. Each is recorded as SYNTHETIC with:
- the clip SHA-256
- the text
- the service, model, voice ID and settings
- the generation date
- OWNER_LISTEN_VERIFIED

A3-08. Native spot check. At each tutor session, the tutor reviews a random 10 percent of the synthetic clips bound since the last session, at least 10 clips. Any stress or pronunciation error found triggers:
- the clip is unbound
- a full tutor review of every synthetic clip from that unit
- an entry on the re-record list

## Part 4 — Learner display and replacement

A3-09. Label. A synthetic clip shows a visible "AI voice" label wherever it plays. A native clip shows no label.

A3-10. Replacement. When a verified native clip becomes available for a row, it replaces the synthetic clip. The manifest status records the change.

A3-11. Manifest. audio-manifest.csv gains a source column (NATIVE_SHTOOKA, NATIVE_TUTOR, SYNTHETIC) and the A3-07 fields. Status moves NEEDS_RECORDING → SYNTHETIC_GENERATED → VERIFIED → BOUND, or to NATIVE once replaced.

## Part 5 — Rights

A3-12. Source record. Before any synthetic clip is bound, the service is added to the source inventory as a new source, "synthetic voice audio." The record holds the plan type and the service's terms for use of generated audio, as they stand on the date of generation. A personal study build is the intended use. Public release stays blocked under MA2-007 and MA2-174 until the terms are checked again.

---

## Owner ratification

- [x] I ratify Amendment 3 as written.
- [ ] I ratify with these changes: ______________________
- Product Owner: Justin Kulka  Date: 11/12/2026
- Recorded by Claude at the Product Owner's written direction ("ratify A3") on 11/12/2026, because the Product Owner was on a phone.
