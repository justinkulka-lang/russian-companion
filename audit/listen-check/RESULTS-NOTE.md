# Listen-check results: source note

The results files in this folder (block1-results.json to block6-results.json) and owner-verified-clips.json record the
owner listen-check of the 34 EXISTING_CLIP_CANDIDATE clips.

- Listener: Justin (owner). The schema requires the verifier value "OWNER" (owner decision D36d).
- Source: owner decision D35 (2026-11-23) and the owner's results file audit/prep/owner-listen-check-2026-11-23.json on
  owner/prep-reports, which gives each clip ID, its SHA-256 and the decision. All 34 were marked MATCH. The recorded time,
  2026-11-23T21:22:00Z, is the completion time in that file.
- The owner compared the clips against purchased book recordings. No book audio, and nothing derived from it, is in this
  repository.
- Each block sheet was regenerated before the results were recorded (D36d), under the matching rule of the owner ruling
  of 2026-11-24 (first-letter case). A results file lists only the sheet's clips that are among the 34; the other sheet
  clips (the single-letter clips а, в, и, к, о, с and я) have no decision and stay open.
- The registry app/src/main/assets/content/audio-verification.json holds exactly the records these results produce
  (listenCheckRecords). Seven of the 34 clips appear on no sheet, because no letter, example or item shows their text;
  owner-verified-clips.json records them, and the audio manifest lists them as VERIFIED, not bound.
- Binding (owner decision D52a, 2026-11-26): each result binds by its clip ID and SHA-256. Its manifestId is a locator only;
  manifest row IDs are positional and renumber when a course word is added, so a renumbering never changes which clip a
  decision applies to (OwnerResultsBindByClipTest). A stale locator is updated without changing the decision (T17f re-pointed
  A16-012 -> A16-013 and A16-018 -> A16-019; D52a confirmed it).
