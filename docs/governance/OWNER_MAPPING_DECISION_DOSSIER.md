# Owned-edition mapping decision dossier

Status: PAGE_BOUNDARIES_ACCEPTED_REVIEW_STILL_PENDING

This dossier records the Product Owner acceptance of the printed-page boundaries for private comparison. Every worksheet disposition stays `PENDING_CURRICULUM_REVIEWER`. That acceptance is not qualified curriculum review. ISBN stays blank. `isbn_observation` stays `UNRESOLVED`. `owned_copy_verified` stays blank. Reviewer identity, review date, reviewed digest, and reviewer qualification stay blank.

A recorded inspection note is not an accepted mapping. A publisher-public topic title is not a page mapping. A proposed lesson label, page range, or topic string is not an owned-edition fact until the Product Owner confirms it against the physical or lawful digital book.

## Where the retained evidence lives

Structural observations are the constants in `app/src/test/java/dev/russiancompanion/v2/review/Unit1ReviewPackage.kt`:

- `OBSERVED_EDITION_TITLE`
- `OBSERVED_EDITION_AUTHOR`
- `OBSERVED_PUBLISHER_IMPRINT`
- `OBSERVED_EDITION_NOTES`
- `UNITS_11_15_EDITION_NOTES`
- `UNITS_16_20_EDITION_NOTES`
- `UNITS_21_25_EDITION_NOTES`
- `UNITS_26_30_EDITION_NOTES`
- `STRUCTURAL_MAPPING_OBSERVATIONS`
- `MAPPING_UNRESOLVED_QUESTIONS`

Each unit’s worksheet is `review/unit-N/UNIT_N_OWNED_EDITION_MAPPING_WORKSHEET.tsv`. The worksheet column `mapping_package_digest` is `mappingPackageDigest(governedMappingFacts(N))`. That digest binds the proposed row. It does not accept the row.

The private PDF is not in the repository. A recorded private-file SHA-256 is not ownership verification.

## Facts recorded from an inspected copy

These strings are already in the retained notes. They are inspection notes. They are not an ISBN, not `owned_copy_verified`, and not a Product Owner mapping signature.

Recorded on every unit worksheet, from `OBSERVED_EDITION_TITLE`, `OBSERVED_EDITION_AUTHOR`, and `OBSERVED_PUBLISHER_IMPRINT`:

- Edition title: `The New Penguin Russian Course: A Complete Course for Beginners`
- Author: `Nicholas J. Brown`
- Publisher imprint: `Penguin Books`

Recorded inside the edition-note constants: introduction signed Nicholas J. Brown, London, 1996; scanned file extent 528 pages; the earlier note that the ISBN was not visible. A later reference-file comparison is appended to every edition-note constant. It records a hash mismatch, a blank copyright page, and a back-cover ISBN reading. That comparison is not an owner inspection.

The ownership sentence “The Product Owner asserts that the inspected file is a legally owned copy. That assertion is not reviewer verification.” is present only in `UNITS_11_15_EDITION_NOTES`, `UNITS_16_20_EDITION_NOTES`, `UNITS_21_25_EDITION_NOTES`, and `UNITS_26_30_EDITION_NOTES`. Units 1–10 use `OBSERVED_EDITION_NOTES`, which does not contain that sentence.

The historical private-file SHA-256 `2bcc0c45fac4508adf283f4dd939a7e578cf2c1306fdadf4966300532beb6303` remains in the original sentences of `UNITS_11_15_EDITION_NOTES` and `UNITS_16_20_EDITION_NOTES` only. The reference-file comparison appended to every band names that hash in order to record the mismatch. The historical sentences for Units 1–10 and 21–30 still do not record it as the inspected file.

## Facts supported only by publisher-public material

`docs/governance/MASTER_AUTHORITY_v2.0.md` lines 85–116 name the public first-introduction scope for units 1–30. Those titles have no page numbers, no ISBN, and no lesson-boundary measurement from the owned copy. They do not authorize Penguin prose. They are not the worksheet topic strings.

## Proposed mappings

Every lesson label, printed page range, and topic string below is a proposed structural observation. Disposition for each row is `PENDING_CURRICULUM_REVIEWER`.

| Unit | Retained worksheet | Proposed lesson | Proposed pages | Proposed topic |
|---:|---|---|---|---|
| 1 | `review/unit-1/UNIT_1_OWNED_EDITION_MAPPING_WORKSHEET.tsv` | Lesson 1 | 1-11 | Cyrillic reading foundation: alphabet, pronunciation orientation, transliteration, and street-sign reading |
| 2 | `review/unit-2/UNIT_2_OWNED_EDITION_MAPPING_WORKSHEET.tsv` | Lesson 2 | 12-23 | Russian handwriting and additional pronunciation |
| 3 | `review/unit-3/UNIT_3_OWNED_EDITION_MAPPING_WORKSHEET.tsv` | Lesson 3 | 24-35 | Everyday phrases, naming things, introductory grammar, and grammatical gender |
| 4 | `review/unit-4/UNIT_4_OWNED_EDITION_MAPPING_WORKSHEET.tsv` | Lesson 4 | 36-45 | Verbs, personal pronouns, present tense, conjugation, word stress, and introduction to nouns and cases |
| 5 | `review/unit-5/UNIT_5_OWNED_EDITION_MAPPING_WORKSHEET.tsv` | Lesson 5 | 46-55 | Asking questions, the prepositional case, indeclinable nouns, and dialogue work |
| 6 | `review/unit-6/UNIT_6_OWNED_EDITION_MAPPING_WORKSHEET.tsv` | Lesson 6 | 56-67 | Possession, destinations and movement, the accusative, and personal-name orientation |
| 7 | `review/unit-7/UNIT_7_OWNED_EDITION_MAPPING_WORKSHEET.tsv` | Lesson 7 | 68-80 | Adjectives, agreement, question and demonstrative forms, case agreement, and introductory adverbs |
| 8 | `review/unit-8/UNIT_8_OWNED_EDITION_MAPPING_WORKSHEET.tsv` | Lesson 8 | 81-91 | Noun plurals, spelling rules, and availability |
| 9 | `review/unit-9/UNIT_9_OWNED_EDITION_MAPPING_WORKSHEET.tsv` | Lesson 9 | 92-106 | Cardinal numbers, quantity-driven genitive, and neutral quantity practice |
| 10 | `review/unit-10/UNIT_10_OWNED_EDITION_MAPPING_WORKSHEET.tsv` | Lesson 10 | 107-121 | Possession and existence, genitive pronouns and prepositions, and genitive adjective agreement |
| 11 | `review/unit-11/UNIT_11_OWNED_EDITION_MAPPING_WORKSHEET.tsv` | Lesson 11 | 122-132 | Past-tense formation and use, including reflexive verbs |
| 12 | `review/unit-12/UNIT_12_OWNED_EDITION_MAPPING_WORKSHEET.tsv` | Lesson 12 | 133-148 | Future formation, introductory verbal aspect, the dative case, additional spelling rules, and prepositional plural |
| 13 | `review/unit-13/UNIT_13_OWNED_EDITION_MAPPING_WORKSHEET.tsv` | Lesson 13 | 149-161 | Aspect in past-tense contexts, tense selection, infinitive aspect, and reported speech |
| 14 | `review/unit-14/UNIT_14_OWNED_EDITION_MAPPING_WORKSHEET.tsv` | Lesson 14 | 162-170 | Aspect in future-tense contexts and impersonal constructions |
| 15 | `review/unit-15/UNIT_15_OWNED_EDITION_MAPPING_WORKSHEET.tsv` | Lesson 15 | 171-187 | Requests, imperative forms, and consolidation of aspect use |
| 16 | `review/unit-16/UNIT_16_OWNED_EDITION_MAPPING_WORKSHEET.tsv` | Lesson 16 | 188-200 | Instrumental case: principal uses; singular and plural noun forms; adjective agreement; common instrumental-governing constructions; instrumental complements with identity, role, or becoming |
| 17 | `review/unit-17/UNIT_17_OWNED_EDITION_MAPPING_WORKSHEET.tsv` | Lesson 17 | 201-214 | Time, weekdays, calendar dates, years, age expressions, and ordinal numbers |
| 18 | `review/unit-18/UNIT_18_OWNED_EDITION_MAPPING_WORKSHEET.tsv` | Lesson 18 | 215-229 | Comparative forms, superlative expression, and relative clauses built with the inflected relative pronoun который |
| 19 | `review/unit-19/UNIT_19_OWNED_EDITION_MAPPING_WORKSHEET.tsv` | Lesson 19 | 230-239 | Conditional constructions with бы; obligation or probability with forms of должен; productive meanings of selected verbal prefixes |
| 20 | `review/unit-20/UNIT_20_OWNED_EDITION_MAPPING_WORKSHEET.tsv` | Lesson 20 | 240-256 | Unidirectional and multidirectional verbs of motion; motion by foot or transport; carrying/leading motion pairs; selected prefixed motion verbs; cumulative review through Lesson 20 |
| 21 | `review/unit-21/UNIT_21_OWNED_EDITION_MAPPING_WORKSHEET.tsv` | Lesson 21 | 257-268 | Possession with the reflexive possessive pronoun; reflexive reference; and purpose clauses |
| 22 | `review/unit-22/UNIT_22_OWNED_EDITION_MAPPING_WORKSHEET.tsv` | Lesson 22 | 269-282 | Numeral behavior beyond basic counting, including declined numerals, forms for both and one and a half, compound quantities, and collective or quantity expressions |
| 23 | `review/unit-23/UNIT_23_OWNED_EDITION_MAPPING_WORKSHEET.tsv` | Lesson 23 | 283-293 | Expressions of elapsed time, duration, ago, during, and distinctions involving completion over a period |
| 24 | `review/unit-24/UNIT_24_OWNED_EDITION_MAPPING_WORKSHEET.tsv` | Lesson 24 | 294-304 | Negation, case behavior under negation, negative pronouns or particles, and the position or scope of не |
| 25 | `review/unit-25/UNIT_25_OWNED_EDITION_MAPPING_WORKSHEET.tsv` | Lesson 25 | 305-319 | Diminutives, Russian personal-name forms, social address, requests, apologies, and polite expressions |
| 26 | `review/unit-26/UNIT_26_OWNED_EDITION_MAPPING_WORKSHEET.tsv` | Lesson 26 | 320-331 | Stock expressions; indefinite pronouns and adverbs; word order; postal addresses; letter writing; vocabulary and correspondence work |
| 27 | `review/unit-27/UNIT_27_OWNED_EDITION_MAPPING_WORKSHEET.tsv` | Lesson 27 | 332-342 | Participles: types and stress. Past passive formation and stress are the main lesson. Present passive is marked extra. Active participle formation is not in this lesson. |
| 28 | `review/unit-28/UNIT_28_OWNED_EDITION_MAPPING_WORKSHEET.tsv` | Lesson 28 | 343-354 | Imperfective and perfective verbal adverbs, formation and use, vocabulary and readings |
| 29 | `review/unit-29/UNIT_29_OWNED_EDITION_MAPPING_WORKSHEET.tsv` | Lesson 29 | 355-367 | Bookish style, present and past active participles, shipped participial-phrase punctuation, short-form adjectives, and bookish reading |
| 30 | `review/unit-30/UNIT_30_OWNED_EDITION_MAPPING_WORKSHEET.tsv` | Lesson 30 | 368-377 | Course orientation, dictionary use, abbreviations, review and application of letter names, particles, vocabulary and a final reading |

Observation-source bands, retained verbatim in the worksheet `observation_source` column:

- Units 1–10: `Product Owner structural mapping input v1.0. Page numbers are printed book pages. The input is private and is not repository curriculum.`
- Units 11–15: visually inspected on 23 September 2026. The private PDF is not repository curriculum.
- Units 16–20: visually inspected on 24 September 2026. The private PDF is not repository curriculum.
- Units 21–25: visually inspected on 24 September 2026. The private PDF is not repository curriculum.
- Units 26–30: visually inspected on 24 September 2026. Classification `RESEARCH_GAPS_BLOCK_IMPLEMENTATION`. The observation is not an approved alignment.

The prior proposal gave Unit 25 pages 307-321 and Unit 26 pages 320-331. That overlap is not a shared printed page and is not a live boundary. Lesson 25 is printed pages 305-319 and Lesson 26 remains 320-331. Packaged curriculum `mappingNote` strings for Units 21-25 use the accepted ranges. No lesson boundary in this copy shares a printed page.

Lessons 26–30 are recorded in the variance proposal as having no following book revision block, with printed page 378 beginning the Grammatical Tables. That sentence is a proposal observation. It is not an accepted page boundary.

## Unresolved facts

The Product Owner accepted the observed edition identity and the printed page ranges for private comparison. The following stay unresolved:

- Formal ISBN verification and the copyright-page year. The back-cover observation is not an accepted ISBN, and the isbn column stays blank.
- `owned_copy_verified`, for every unit. The owned-copy confirmation authorizes private comparison only.
- Binary-identity continuity. The concatenation mismatch is not evidence of altered instructional content.
- `variance_conflict`, for every unit.
- Reviewer identity, review date, reviewed version digest, and reviewer qualification, for every unit.
- Any mapping disposition other than `PENDING_CURRICULUM_REVIEWER`.

The obsolete range 307-321 is not a live Unit 25 boundary.

## Product Owner mapping disposition

Product Owner disposition: accepted for private comparison; mapping remains PENDING_CURRICULUM_REVIEWER
Product Owner name: Justin Kulka
Product Owner date: 2026-10-02
Product Owner signature:
Reviewer identity:
Reviewer qualification:
Review date:
Reviewed version digest:
Reviewer signature:
