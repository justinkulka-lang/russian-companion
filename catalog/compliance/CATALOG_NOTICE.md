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
filename is a deterministic rus-<id>.ogg reference. Shtooka audio bytes
keep those original licenses and are not converted to CC BY-SA.

Full legal codes are stored locally under licenses/.

## OpenRussian stress-marked text

Every packaged source text is a source-supplied OpenRussian accented
headword copied unmodified. License: CC BY-SA 4.0
(https://creativecommons.org/licenses/by-sa/4.0/). Source export:
https://github.com/Badestrand/russian-dictionary (commit recorded in
catalog/license-chains.json).

OpenRussian is a CC BY-SA 4.0 database. This app extracts and
redistributes 1,000 ranked records in another structured catalog.

OpenRussian audio samples remain excluded.

## App code

This catalog notice does not license the application source code. No
app-code license is declared in this compliance package. Application
code is excluded from the catalog share-alike treatment.

## User recordings and user-entered data

Recordings captured on the device and values the user types (book
scores, AnkiDroid status, logs) are user data. They are not packaged
third-party catalog works, are not redistributed by this notice, and
are excluded from the catalog share-alike treatment.

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

## Catalog share-alike treatment (recorded, not confirmed)

Because Creative Commons gives no bright-line threshold for a
"substantial" database extraction, this package applies CC BY-SA 4.0
conservatively to any copyright or database rights held in the packaged
catalog data, selection, and arrangement.

This treatment is adopted without claiming that audio becomes CC BY-SA.
Explicitly excluded:

- Shtooka audio bytes, which retain their original CC BY 3.0 US,
  CC BY 2.0 FR, or public-domain status
- Application code
- User recordings and user-entered data

Downstream redistribution of the catalog data, selection, and
arrangement must keep CC BY-SA 4.0 notices. Audio files must keep their
original license notices.

This recorded treatment is stored on every packaged source record as
shareAlikeTreatment. shareAlikeConfirmation remains UNCONFIRMED.

## Required notices packaged with this app

- licenses/cc-by-sa-4.0.txt
- licenses/cc-by-3.0-us.html
- licenses/cc-by-2.0-fr.html
- licenses/public-domain-notice.txt
- licenses/shtooka-readme-sakhno.txt
- licenses/shtooka-readme-streit.txt
- content/catalog-notice.txt (this notice)
- catalog/compliance/share-alike-package.json (machine-readable audit)
