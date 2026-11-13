# AI audio pilot: Units 1–5

Status: **UNVERIFIED. Nothing here is bound to the app.** The audio stays in the owner's ElevenLabs account; this folder holds the script, the timing check and the listen-check sheet.

## Why this exists

The owner chose to try AI-generated audio for Units 1–5 in place of tutor recordings (owner direction, 11/12/2026). docs/audio/AUDIO_PLAN.md §1 and §9 still say AI audio is not adopted and can be revisited only by owner amendment with a native-speaker check of every clip. That amendment has not been written yet. Until it is, these clips must not be bound.

## How it was made

- Date: 11/12/2026 (America/Chicago).
- Tool: ElevenLabs, owner's account, paid plan (owner-stated). Model eleven_v3. Voice "Ivan – The Educator" (voice_id 1ikyXz7KIkVIgmGOM6QM).
- Source text: docs/audio/audio-manifest.csv at f6fa7cc5, every row with first_unit 1–5 (156 rows), in manifest order.
- One file per unit, items separated by pauses, two takes per unit (10 takes).
- ElevenLabs flow: sQo9wpGwsDLzfNyU9ROW ("Russian Companion – Unit 2 stress test").

| Unit | Take | generation_id | Length (s) |
|---|---|---|---|
| 1 | 1 | dZyNgCzztpYLijMwc73i | 58.4 |
| 1 | 2 | Zr7oZvY56aqXkTaZcuAP | 57.0 |
| 2 | 1 | 5509GjjLvAL11oXw6JL4 | 18.2 |
| 2 | 2 | P9qsORleVNgJxcELpUy3 | 18.8 |
| 3 | 1 | gWpkmqpFpKEHNAyWhTKy | 93.0 |
| 3 | 2 | XtG4AZENRyQvvhANr1MW | 94.7 |
| 4 | 1 | IL4Co6ofs9vyZa3cAlio | 96.3 |
| 4 | 2 | qUTl9ayxG5FsMfX09DPa | 173.0 |
| 5 | 1 | GdMgcSF45aLXqL6eWpaF | 60.9 |
| 5 | 2 | F1vzAsbNknkvtRnevLrw | 62.3 |

## Differences between the spoken script and the manifest text

See units1-5_script.csv, columns course_text, spoken and note.

- Letters (33 rows): only the letter name was spoken. Letter sounds were not generated. Those still need the tutor (AUDIO_PLAN.md §4).
- 20 rows the course shows without a stress mark: the script added stress marks, and question marks on questions, so the voice would read them correctly. These marks were added by Claude, not taken from a source, so they need checking by the tutor. The course text itself is unchanged.
- "я в Москве" was capitalised as "Я в Москве́" in the script only.

## Files

- units1-5_script.csv: manifest ID, unit, kind, course text, spoken text, note.
- timing_audit.csv: start/end of every item in every take, from ElevenLabs Scribe alignment, plus flags.
- listen-check-units1-5.html: owner listen-check sheet. Each item has Take 1 OK / Take 2 OK / Both OK / Both wrong / Unsure. Copy results gives JSON.

## Limits of the automated check

The Scribe transcripts reproduced the script exactly, including pause tags and stress marks, so they are an alignment to the script, not an independent recognition. They confirm every item has a time slot in every take. They cannot detect a wrong word or wrong stress. Only the owner's listen-check and the tutor can.

36 timing flags: mostly very short one-syllable items (possible clipping, or alignment error) and a few items with under 0.25 s gap before them (hard to split). Unit 4 take 2 is slow, with long pauses, not extra content.

## Next steps

1. Owner completes the listen-check sheet.
2. Items marked Both wrong are regenerated.
3. Owner writes the AUDIO_PLAN amendment, including the tutor review of every clip.
4. Owner downloads the chosen takes from ElevenLabs into this folder (or a location the owner names).
5. Codex splits each chosen take into one clip per manifest ID, hashes each, builds T13-format listen-check sheets, and binding follows the existing A2-17 process.
