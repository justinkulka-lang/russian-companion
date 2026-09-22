# Packaged catalog notice

This installation redistributes third-party Russian audio and
stress-marked dictionary text. This app does not own the packaged Russian audio or stress-marked text. User recordings and user-entered
data are not third-party catalog material.

Share-alike confirmation in the packaged seed remains UNCONFIRMED.
This notice is evidence for independent review. It is not a public
release approval.

## Shtooka native audio

Packaged audio comes from Shtooka Russian word recordings (SWAC
collections rus-balm-voc and rus-balm-voc-sakhno), acquired from the
collection READMEs and from Wikimedia Commons file pages as recorded in
catalog/license-chains.json.

Recorded audio licenses in this catalog:

- CC BY 3.0 US (http://creativecommons.org/licenses/by/3.0/us/)
- CC BY 2.0 FR (http://creativecommons.org/licenses/by/2.0/fr/)
- Public domain (Commons file-page mark; see licenses/public-domain-notice.txt)

Creators recorded in the packaged source records include Sergei/Sergueï
Sakhno, Nicolas Vion, Streit Goula, Streit Eric, and Gulnara Streit as
supplied by those source records. No additional speaker is invented.

Audio files are packaged unmodified (source ogg bytes). The local
filename is a deterministic rus-<id>.ogg reference.

Full legal codes are stored locally under licenses/.

## OpenRussian stress-marked text

Every packaged source text is a source-supplied OpenRussian accented
headword copied unmodified. License: CC BY-SA 4.0
(https://creativecommons.org/licenses/by-sa/4.0/). Source export:
https://github.com/Badestrand/russian-dictionary (commit recorded in
catalog/license-chains.json).

OpenRussian audio samples remain excluded.

## App code

This catalog notice does not license the application source code. No
app-code license is declared in this compliance package.

## User recordings and user-entered data

Recordings captured on the device and values the user types (book
scores, AnkiDroid status, logs) are user data. They are not packaged
third-party catalog works and are not redistributed by this notice.

## Unmodified versus modified assets

Packaged audio models and source texts are recorded UNMODIFIED.
Preparation does not rewrite Russian, add stress marks, translate, or
create exercises.

Transformations that were performed:

- Deterministic selection of frequency-ranked OpenRussian headwords that
  match already-licensed Shtooka word recordings
- Exact copy of source-supplied stress-marked text
- Exact copy of licensed audio bytes
- Mechanical headword matching
- Packaging into the offline seed with provenance, attribution, license,
  modification, and share-alike-treatment records

## Collection and share-alike treatment (proposed, not confirmed)

Each catalog item pairs independently licensed audio with independently
licensed OpenRussian text. Proposed treatment for independent review:

- The packaged catalog is a collection of those paired items
- The OpenRussian text remains CC BY-SA 4.0 Licensed Material
- Matching and packaging are not a linguistic adaptation of the Russian
  text
- CC BY / public-domain audio is not converted into share-alike audio
- Downstream redistribution of the text layer must keep CC BY-SA 4.0
  notices

This proposed treatment is already stored on every packaged source
record as shareAlikeTreatment. shareAlikeConfirmation remains
UNCONFIRMED until independent review.

## Required notices packaged with this app

- licenses/cc-by-sa-4.0.txt
- licenses/cc-by-3.0-us.html
- licenses/cc-by-2.0-fr.html
- licenses/public-domain-notice.txt
- licenses/shtooka-readme-sakhno.txt
- licenses/shtooka-readme-streit.txt
- content/catalog-notice.txt (this notice)
- catalog/compliance/share-alike-package.json (machine-readable audit)
