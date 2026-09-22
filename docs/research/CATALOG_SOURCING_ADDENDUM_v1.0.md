# CATALOG SOURCING ADDENDUM v1.0
## Verified audio + stress-text sources for the offline Russian catalog

**Companion to:** `RESEARCH_DOSSIER_v1.0.md` (this is EVIDENCE, not authority).
**Occasion:** PR #12 correctly excluded all candidate audio (no verified licenses).
This addendum identifies sources that DO verify, so the existing catalog-intake
pipeline has a real, legal input. It defines no new product scope and writes no
requirements; it supplies sourcing evidence against the requirements that already
exist in `MASTER_AUTHORITY_v1.0.md`.

---

## 1. THE DECIDING CONSTRAINT: go word-level, not sentence-level

MA-038 requires each audio model to be matched to verified source-supplied
**stress-marked** text. That single requirement selects the source:

- Sentence-level audio (Tatoeba) pairs with sentence text that is NOT stress-
  marked, and Russian stress is unpredictable and unmarked in normal orthography.
- Word-level audio pairs cleanly with a stress-marked **dictionary** headword.

Therefore the v1 catalog is built from word-level native audio matched to a
stress-marked dictionary, not from sentence audio.

---

## 2. SOURCES, RANKED (verified this session)

### 2.1 Shtooka — Russian word audio — LEAD SOURCE
- "Base Audio Libre de Mots Russes": approximately 5,000 Russian words/expressions,
  native-recorded. (Some Shtooka pages cite larger server-wide totals across all
  languages and inflected forms; confirm the exact Russian deliverable count on
  the SWAC download page before scoping.)
- License: **Creative Commons Attribution 3.0 (CC-BY)** — attribution only, NO
  share-alike. Cleanest license in the set. Each SWAC collection states its
  license in its README; verify per collection.
- Access: bulk-downloadable as a SWAC collection from swac-collections.org.
- Note: this is the same audio set behind widely used "Top 1000 Russian words"
  lists (recordings by Streit/Vion, distributed CC-BY).

### 2.2 OpenRussian dictionary TEXT — stress + declension layer
- Provides stress-marked headwords and declension/conjugation data.
- License: **CC BY-SA 4.0** — attribution AND share-alike.
- Reconciliation with PR #12: the PR excluded OpenRussian *audio* (publisher flags
  possible copyright issues on those samples) — correct, and unaffected here,
  because this addendum uses OpenRussian only for **text**, which is separately and
  legitimately licensed CC BY-SA 4.0. Its audio stays excluded.

### 2.3 Lingua Libre — word audio — OPTIONAL VOLUME EXPANSION
- Wikimedia France's modern successor to Shtooka; per-word native recordings.
- License: **CC BY-SA 4.0** — attribution AND share-alike.
- Access: bulk via Wikimedia Commons category `Lingua Libre pronunciation-rus`
  (replace the language code in the category name).
- Scale: large and growing; the English category alone exceeds 106,000 files.
  The Russian count was NOT verified this session — confirm before relying on it.
- Tradeoff: more volume, but CC-BY-SA audio adds share-alike obligations that the
  CC-BY Shtooka set does not.

### 2.4 Wikimedia Commons — access/verification layer
- Not a separate corpus so much as the host for Shtooka and Lingua Libre files;
  each file's license is verifiable individually. Use it to confirm per-file
  licenses, not as an independent source.

### 2.5 EXCLUDED
- **Tatoeba audio** — per-recording licenses; the most common is CC BY-NC
  (non-commercial), alongside CC-BY-NC-ND and "no offsite use" (all unusable);
  only a CC-BY/CC0 minority is usable; and there is NO bulk download (files must
  be fetched one by one). Its sentence text is also unstressed. Defer entirely for
  v1; if sentence audio is ever wanted, filter the CC-BY/CC0 subset from the
  `sentences_with_audio` export (fields: id, user, license, attribution URL).
- **Forvo** — CC BY-NC-SA 3.0 (non-commercial) and not bulk-distributable. Out.

---

## 3. THE UNAVOIDABLE SHARE-ALIKE FACT

Every open source of Russian **stress marks** (OpenRussian, Wiktionary) is
CC-BY-SA. So even if audio is taken only from CC-BY Shtooka, the stress-text layer
carries share-alike. Conclusion: MA-035's public-release block on confirmed
share-alike treatment is not avoidable, only satisfiable. Plan to satisfy it
(attribution + SA treatment recorded, derivative-data obligations confirmed), not
to engineer around it. The only SA-free route to stress marks is authoring/
verifying them by hand — not worth it, and outside the no-authoring posture.

---

## 4. RECOMMENDED v1 CATALOG

- **Audio spine:** Shtooka Russian word set (CC-BY 3.0).
- **Text/stress layer:** OpenRussian dictionary (CC BY-SA 4.0), matched by headword.
- **v1 size:** limit to the frequency-ranked top ~1,000–2,000 words that achieve a
  successful audio-plus-stress-text match. Frequency-first is both smaller/
  reviewable and pedagogically correct. This is a complete, shippable
  listen-record-compare catalog.
- **Later expansion:** add Lingua Libre `-rus` (accepting added CC-BY-SA audio).
- **Deferred:** Tatoeba sentence audio.

---

## 5. HOW THIS MAPS TO EXISTING MASTER AUTHORITY REQUIREMENTS

This addendum changes no requirements; it feeds these already-governed ones:

- MA-036 (exclude absent/unclear/incompatible audio licenses) — the intake filter.
- MA-037 (documented per-file license filtering) — recorded in source-decisions.json.
- MA-038 (audio matched to verified stress-marked text) — Shtooka word ↔ OpenRussian
  stress-marked headword.
- MA-033 / MA-034 (attribution / share-alike recorded) — CC-BY needs attribution;
  OpenRussian text and any Lingua Libre audio need SA treatment recorded.
- MA-035 (public release blocked until SA confirmed) — satisfied at release, per §3.
- MA-050 / MA-051 (play native audio; present matched stress-marked text) — enabled.
- MA-023 / MA-123 (only pre-made licensed material; no substitution) — unmatched or
  unlicensed items are excluded, never fabricated.

---

## 6. INTAKE PIPELINE STEPS (deterministic transformation of licensed data — no
generation, no per-item linguistic judgment)

1. Acquire the Shtooka Russian SWAC collection.
2. For each audio file, read its license metadata; keep only compatible licenses
   (CC0 / public domain / CC-BY / CC-BY-SA); exclude absent, unclear, CC-BY-NC,
   CC-BY-ND, and "no offsite use." (MA-036, MA-037)
3. Normalize each headword and match it to its OpenRussian dictionary entry to
   attach the stress-marked form (and declension data if used). Drop any item with
   no confident match. (MA-038)
4. For every retained item, record: source, file, license, author/attribution URL,
   modification status, and required share-alike treatment, into
   source-decisions.json and the shipped attributions inventory. (MA-033, MA-034,
   MA-037)
5. Restrict the v1 seed to the frequency-ranked top N items that passed both the
   license filter and the text match. Never fabricate audio or text to fill gaps.
   (MA-023, MA-123)
6. Before any public release, complete and record the share-alike confirmation for
   the distributed derivative data. (MA-035)

---

## SOURCES (accessed September 2026)
- Shtooka Project / SWAC collections (Russian word base ~5,000 words, CC-BY 3.0;
  per-collection READMEs; bulk SWAC download): swac-collections.org;
  wiki.creativecommons.org/wiki/Shtooka_Project_-_free_audio_collections_of_words;
  commons.wikimedia.org/wiki/Commons:Shtooka.
- "Top 1000 Russian words" audio (Streit/Vion, CC-BY): masterrussian.com/vocabulary.
- OpenRussian data (CC BY-SA 4.0; stress + declensions):
  github.com/Badestrand/russian-dictionary; en.openrussian.org/about.
- Lingua Libre (CC BY-SA 4.0; per-word recordings; Commons category access;
  English category >106,000 files): en.wikipedia.org/wiki/Lingua_Libre;
  commons.wikimedia.org/wiki/Category:Lingua_Libre_pronunciation-eng;
  en.wiktionary.org/wiki/Help:Audio_pronunciations.
- Tatoeba (sentence text CC-BY 2.0 FR / some CC0; audio per-recording, commonly
  CC-BY-NC; no bulk audio download): tatoeba.org/en/downloads;
  en.wiki.tatoeba.org/articles/show/faq; en.wikipedia.org/wiki/Tatoeba.
- Forvo (CC BY-NC-SA 3.0): en.wikipedia.org/wiki/Forvo.

END OF ADDENDUM v1.0
