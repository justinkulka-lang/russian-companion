# Year-One Audio Plan

This is the single source for audio in the year-one app. It records what needs audio, where each piece of audio comes from, how it is verified, and how the list stays current. The data behind it is audio-manifest.csv. The tutor's copy is TUTOR_READING_LIST.md.

Governing rules: Amendment 2, A2-17 to A2-20 (verified audio match, listen-check sheets, audio in the app, foundation audio). MA2-092 to MA2-111 (rights).

Built from: curriculum pack at commit 877f4145 (11/11/2026), and the 1,000 bundled Shtooka clips.

---

## 1. Where things stand

- The app plays audio and records takes (T04, T04-n1). No lesson has audio bound to it yet.
- 1,000 licensed Shtooka word clips are bundled. Only 30 of them match a word the course actually uses.
- No clip is verified yet. Verification needs the listen-check sheet (T13) and the owner's listen-check (T13-O).
- AI-generated audio is not used. Russian stress is unmarked in normal text, text-to-speech can misplace it, and a beginner cannot catch those errors. Native recordings are the plan.

## 2. What needs audio

807 rows in all. Every learner-facing Russian word, phrase and sentence in the course gets one row, plus the 33 letters.

| Status | Rows | Meaning |
|---|---|---|
| NEEDS_RECORDING | 767 | No matching clip. A native speaker records it. |
| EXISTING_CLIP_CANDIDATE | 30 | A bundled Shtooka clip has the same text. It still needs the owner's listen-check before binding. |
| PLANNED_T14 | 10 | Homograph and final-devoicing pairs approved for Units 1–2, recorded once T14 adds them from a licensed source. |

| Kind | Rows |
|---|---|
| Letters (name, then sound) | 33 |
| Words | 451 |
| Phrases (2–4 words) | 189 |
| Sentences and questions | 134 |

| Priority | Units | Rows |
|---|---|---|
| P1 | 1–5 | 146 |
| P2 | 6–15 | 310 |
| P3 | 16–30 | 351 |

Rows per unit:

| Unit | Rows | Existing clip candidates | Needs recording | Planned (T14) |
|---|---|---|---|---|
| 1 | 33 | 0 | 33 | 0 |
| 2 | 10 | 0 | 0 | 10 |
| 3 | 28 | 5 | 23 | 0 |
| 4 | 45 | 6 | 39 | 0 |
| 5 | 30 | 5 | 25 | 0 |
| 6 | 41 | 2 | 39 | 0 |
| 7 | 59 | 0 | 59 | 0 |
| 8 | 22 | 1 | 21 | 0 |
| 9 | 80 | 3 | 77 | 0 |
| 10 | 33 | 2 | 31 | 0 |
| 11 | 27 | 1 | 26 | 0 |
| 12 | 24 | 0 | 24 | 0 |
| 13 | 7 | 0 | 7 | 0 |
| 14 | 8 | 0 | 8 | 0 |
| 15 | 9 | 1 | 8 | 0 |
| 16 | 29 | 2 | 27 | 0 |
| 17 | 48 | 0 | 48 | 0 |
| 18 | 10 | 0 | 10 | 0 |
| 19 | 14 | 0 | 14 | 0 |
| 20 | 15 | 0 | 15 | 0 |
| 21 | 23 | 0 | 23 | 0 |
| 22 | 22 | 0 | 22 | 0 |
| 23 | 16 | 0 | 16 | 0 |
| 24 | 26 | 2 | 24 | 0 |
| 25 | 31 | 0 | 31 | 0 |
| 26 | 22 | 0 | 22 | 0 |
| 27 | 16 | 0 | 16 | 0 |
| 28 | 18 | 0 | 18 | 0 |
| 29 | 34 | 0 | 34 | 0 |
| 30 | 27 | 0 | 27 | 0 |

## 3. What was left out, and why

- Single letters and letter-name words (бэ, вэ, эль, мягкий знак, and so on): covered by the 33 letter rows.
- Lists of letters (А а, О о, У у…): covered by the letter rows.
- Grammar endings and abbreviations (-ть, -ами, -ович, кв., ул., сущ.): not spoken on their own.
- Wrong answer options in items: some show deliberately wrong forms. Only the cue and the reviewed answer are listed.
- Four wrong-stress forms shown in Unit 27 teaching only as the rejected contrast (написа́нный, прочита́нный, обсуждаемы́й, оставляемы́й): never recorded.

## 4. Flags in the manifest

- "course text has no stress mark" (346 rows): the course shows the word without a stress mark. The tutor says it with normal stress. The listen-check confirms it.
- "short: check it is a word, not an ending" (39 rows): short strings the script could not confirm are whole words. Check before recording. Most are real words such as нет, она́ or без.
- "HOMOGRAPH" (2 rows): о́кна and окна́, both correct and both needed.
- "existing word clip covers the sound only" (7 letter rows): a Shtooka word clip exists for that letter's sound, but not its name. Record the letter row anyway.

## 5. Recording plan

Who: one native Russian speaker, booked through italki as a paid session. Use the same speaker for the whole course, so the voice stays consistent.

When, in two sessions tied to content work:
1. **Session 1, after T14 (Units 1–5 content complete).** About 150–200 lines with T14's new items. Roughly 15–20 minutes of reading. Do this first, because Units 1–2 are the alphabet and sound lessons.
2. **Session 2, after T19 (Units 6–30 content complete).** About 650–750 lines. Roughly 60–75 minutes of reading, which may need two bookings.

These timings are estimates. They assume about 3 seconds per word and 6 per sentence, plus retakes.

Re-run the manifest script before each session. The content blocks will add new items, so the reading list grows.

How:
- Send the tutor TUTOR_READING_LIST.md for the units in that session.
- Quiet room. Phone or computer recorder. Save as WAV or M4A, mono.
- Read each line once, with a two-second pause between lines. Do not read the ID numbers aloud. Say each letter's name, then its sound.
- One file per unit, named by unit, such as unit-03.wav.
- Ask the tutor to mark any line that looks wrong to them: a misspelling, a wrong stress mark or an odd phrase. That gives the course a partial native-speaker check at no extra cost.

Recording also covers the 30 existing clip candidates. It keeps one voice throughout. The Shtooka clips stay as a backup.

## 6. Permission

Before using the recordings, get a written permission from the tutor, by email or italki message. Suggested text:

"I recorded these Russian words and sentences for Justin Kulka's personal Russian-learning app. I give Justin Kulka a permanent, non-exclusive right to use, edit and include these recordings in that app and its future versions, without further payment."

Save the message. It becomes the rights record for a new source, "commissioned tutor recordings," added to the source inventory before any recording is bound (MA2-092 to MA2-111). This is practical wording, not legal advice.

## 7. From recording to app

1. Codex splits each unit file into one clip per line, using the pauses, and names each clip by its manifest ID.
2. Codex builds that unit's listen-check sheet (T13 format): each clip beside its text, with Match / No match / Unsure.
3. The owner completes the sheet. Each clip marked Match is recorded as OWNER_LISTEN_VERIFIED, with its SHA-256 and text (A2-17).
4. Codex binds only Match clips to their items and letters, and adds dictation items where the unit allows (A2-20, A2-23).
5. No Match or Unsure clips go back on the next session's re-record list.

## 8. Keeping this list current

- The manifest is generated by catalog/build_audio_manifest.py from the pack. Re-run it after every content change. Commit the updated CSV with that change.
- New rows keep their unit-based IDs. IDs of existing rows do not change unless their text changes.
- A row's status moves NEEDS_RECORDING → RECORDED → VERIFIED → BOUND. Record each move in the CSV, with the clip ID and SHA-256.
- No other document tracks audio needs. Anything about audio that is not in this plan or its CSV is not tracked.

## 9. Open items

- Handwriting: covered by a separate owner variance under CC002. A tutor's cursive samples, collected in the same session with the same permission, could close it later.
- AI-generated audio: not adopted. Revisit only by owner amendment, with a native-speaker check of every clip.
