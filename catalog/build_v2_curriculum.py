#!/usr/bin/env python3
"""Build the v2 curriculum pack. Independently authored English only.

Cyrillic letters are public-domain writing-system facts. No Penguin prose,
no generated example words, no catalog-order audio bindings.
"""

from __future__ import annotations

import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / "app/src/main/assets/content/v2-curriculum.json"

SOURCE_LETTERS = {
    "id": "src-unicode-cyrillic",
    "title": "Unicode Cyrillic letter forms",
    "license": "Public domain writing-system facts",
    "attribution": "Letter identities are facts of the Cyrillic script as encoded in Unicode.",
    "modification": "Unmodified letter identities; English notes independently authored.",
}

SOURCE_ENGLISH = {
    "id": "src-app-english-v2",
    "title": "Independently authored companion English",
    "license": "Original app text",
    "attribution": "Russian Companion project",
    "modification": "Original",
}

REVIEW_PENDING = {
    "id": "rev-pending-units-1-2",
    "status": "PENDING_QUALIFIED_REVIEW",
    "reviewer": None,
    "notes": "No qualified Russian Curriculum Reviewer has signed this pack. Phonetic English notes remain unverified.",
}

UNITS_3_30 = [
    (3, "Everyday phrases, naming things, basic grammar, and gender"),
    (4, "Verbs, personal pronouns, present tense, conjugation, word stress, and introduction to nouns and cases"),
    (5, "Questions, prepositional case, and indeclinable nouns"),
    (6, "Possession, going places, accusative case, and personal names"),
    (7, "Adjectives, agreement, demonstratives, adjective case forms, and adverbs"),
    (8, "Plurals, first spelling rules, existence questions, and buying things"),
    (9, "Numbers, genitive singular and plural, quantities, and money"),
    (10, "Expressions of having, expanded genitive functions, absence, genitive prepositions, pronouns, and adjectives"),
    (11, "Past tense and reflexive verbs"),
    (12, "Future, aspect introduction, dative case, additional spelling rules, and prepositional plural"),
    (13, "Aspect in the past, tense use, infinitive aspect, and reported speech"),
    (14, "Aspect in the future and impersonal constructions"),
    (15, "Requests, the imperative, and consolidation of aspect use"),
    (16, "Instrumental case, selected demonstratives, and surname declension"),
    (17, "Time, date, age, ordinal numbers, months, and years"),
    (18, "Comparative, superlative, and relative clauses"),
    (19, "Conditional, obligation, and prefixes"),
    (20, "Verbs of motion: going, running, and bringing"),
    (21, "Further possession and purpose"),
    (22, "Number declension, both, collective numerals, and fractions"),
    (23, "Time expressions covering when and duration"),
    (24, "Negation and placement of the particle не"),
    (25, "Diminutives, proper names, and politeness"),
    (26, "Indefinite pronouns, word order, and letter writing"),
    (27, "Participles: types and stress"),
    (28, "Verbal adverbs"),
    (29, "Bookish style, active participles, punctuation, and short-form adjectives"),
    (30, "Abbreviations, names of Russian letters, and particles"),
]

LETTERS = [
    ("А", "а", "vowel-plain", "A vowel. English notes treat it as a low open vowel similar to the vowel in father. Pending review."),
    ("Б", "б", "consonant-early", "A voiced bilabial stop. Pending review."),
    ("В", "в", "confusable", "Looks like Latin B. It is not the /b/ letter. Pending review."),
    ("Г", "г", "consonant-early", "A voiced velar stop in careful speech. Pending review."),
    ("Д", "д", "consonant-early", "A voiced dental/alveolar stop. Pending review."),
    ("Е", "е", "iotated", "An iotated vowel letter. After a consonant it can mark palatalization. Pending review."),
    ("Ё", "ё", "iotated", "An iotated vowel letter. Stress is usually on this vowel when the dots are printed. Pending review."),
    ("Ж", "ж", "consonant-later", "A voiced postalveolar fricative. Pending review."),
    ("З", "з", "consonant-later", "A voiced dental/alveolar fricative. Pending review."),
    ("И", "и", "vowel-plain", "A close front vowel letter. Pending review."),
    ("Й", "й", "signs", "A palatal approximant letter, not a vowel by itself. Pending review."),
    ("К", "к", "consonant-early", "A voiceless velar stop. Pending review."),
    ("Л", "л", "consonant-later", "A lateral approximant. Pending review."),
    ("М", "м", "consonant-early", "A bilabial nasal. Pending review."),
    ("Н", "н", "confusable", "Looks like Latin H. It is the /n/ letter. Pending review."),
    ("О", "о", "vowel-plain", "A mid back vowel letter. Unstressed value is a later listening topic. Pending review."),
    ("П", "п", "consonant-early", "A voiceless bilabial stop. Pending review."),
    ("Р", "р", "confusable", "Looks like Latin P. It is the trilled /r/ letter. Pending review."),
    ("С", "с", "confusable", "Looks like Latin C. It is the /s/ letter. Pending review."),
    ("Т", "т", "consonant-early", "A voiceless dental/alveolar stop. Pending review."),
    ("У", "у", "confusable", "Looks like Latin Y. It is a close back vowel. Pending review."),
    ("Ф", "ф", "consonant-later", "A voiceless labiodental fricative. Pending review."),
    ("Х", "х", "confusable", "Looks like Latin X. It is a voiceless velar fricative. Pending review."),
    ("Ц", "ц", "consonant-later", "An affricate. Pending review."),
    ("Ч", "ч", "consonant-later", "A palatal affricate. Pending review."),
    ("Ш", "ш", "consonant-later", "A voiceless postalveolar fricative. Pending review."),
    ("Щ", "щ", "consonant-later", "A long palatal fricative in the standard description used here. Pending review."),
    ("Ъ", "ъ", "signs", "The hard sign. It does not represent a spoken vowel. Pending review."),
    ("Ы", "ы", "vowel-plain", "A close central/back unrounded vowel letter. Pending review."),
    ("Ь", "ь", "signs", "The soft sign. It marks palatalization and is not a vowel. Pending review."),
    ("Э", "э", "vowel-plain", "A mid front vowel letter used where iotation is not wanted. Pending review."),
    ("Ю", "ю", "iotated", "An iotated vowel letter. Pending review."),
    ("Я", "я", "iotated", "An iotated vowel letter. Pending review."),
]


def letter_obj(upper: str, lower: str, group: str, note: str) -> dict:
    return {
        "id": f"letter-{ord(upper):04x}",
        "uppercase": upper,
        "lowercase": lower,
        "group": group,
        "englishNote": note,
        "sourceId": SOURCE_LETTERS["id"],
        "reviewStatus": "PENDING_QUALIFIED_REVIEW",
        "audioAssetId": None,
        "exampleWordId": None,
    }


def step(step_id: str, kind: str, title: str, body: str, **extra) -> dict:
    payload = {
        "id": step_id,
        "kind": kind,
        "title": title,
        "body": body,
        "dominantAction": extra.get("dominantAction", "Continue"),
        "referenceIds": extra.get("referenceIds", []),
        "itemIds": extra.get("itemIds", []),
        "unavailableReason": extra.get("unavailableReason"),
        "bookDirection": extra.get("bookDirection"),
        "companionDirection": extra.get("companionDirection"),
        "remediationStepId": extra.get("remediationStepId"),
    }
    return payload


def item(item_id: str, prompt: str, options: list[str], answer: str, feedback: str, objective: str) -> dict:
    return {
        "id": item_id,
        "kind": "CLOSED_RESPONSE",
        "language": "en",
        "prompt": prompt,
        "options": options,
        "answer": answer,
        "feedback": feedback,
        "objectiveId": objective,
        "sourceId": SOURCE_ENGLISH["id"],
        "reviewStatus": "PENDING_QUALIFIED_REVIEW",
        "containsGeneratedRussian": False,
    }


def session(session_id: str, title: str, objective_ids: list[str], steps: list[dict], minutes: int) -> dict:
    return {
        "id": session_id,
        "title": title,
        "estimatedMinutes": minutes,
        "objectiveIds": objective_ids,
        "steps": steps,
    }


def reference(entry_id: str, title: str, summary: str, detail: str, unit: int) -> dict:
    return {
        "id": entry_id,
        "title": title,
        "summary": summary,
        "detail": detail,
        "introducedInUnit": unit,
        "sourceId": SOURCE_ENGLISH["id"],
        "reviewStatus": "PENDING_QUALIFIED_REVIEW",
        "searchTerms": [title.lower()],
    }


def build() -> dict:
    letters = [letter_obj(*row) for row in LETTERS]
    items = [
        item(
            "u1-i-а-identity",
            "Which letter is the Cyrillic form shown as А / а?",
            ["Latin A (same shape, different alphabet identity)", "Cyrillic А", "Cyrillic Р"],
            "Cyrillic А",
            "The printed shape can match Latin A. In this course it is the Cyrillic vowel letter А. Confirm shapes in your book.",
            "obj-u1-letter-identity",
        ),
        item(
            "u1-i-р-not-p",
            "The letter Р looks like Latin P. What is the correct identity in this course?",
            ["The Latin letter P", "The Cyrillic letter Р", "The Cyrillic letter П"],
            "The Cyrillic letter Р",
            "Shape overlap with Latin P is a recognition trap. The course treats Р as a Cyrillic letter, not as English P. Confirm the sound in your book; this app does not invent example words.",
            "obj-u1-confusable",
        ),
        item(
            "u1-i-н-not-h",
            "The letter Н looks like Latin H. What is the correct identity?",
            ["Latin H", "Cyrillic Н", "Cyrillic И"],
            "Cyrillic Н",
            "Do not read Н as English H. Confirm the book's pronunciation for this letter.",
            "obj-u1-confusable",
        ),
        item(
            "u1-i-с-not-c",
            "The letter С looks like Latin C. What is the correct identity?",
            ["Latin C", "Cyrillic С", "Cyrillic К"],
            "Cyrillic С",
            "С is a Cyrillic letter. Do not treat the Latin look-alike as the identity.",
            "obj-u1-confusable",
        ),
        item(
            "u1-i-ь-not-vowel",
            "What is the soft sign Ь in this foundation?",
            ["A vowel you pronounce by itself", "A mark, not a standalone vowel", "The same letter as Ъ"],
            "A mark, not a standalone vowel",
            "Ь is introduced as a sign, not as a vowel letter you read alone. Use your book for the exact description.",
            "obj-u1-signs",
        ),
        item(
            "u1-i-print-pair",
            "Which pair is the same letter in uppercase and lowercase print?",
            ["А / а", "Р / п", "Н / и"],
            "А / а",
            "Print pairs must be learned as one letter in two cases. Р / п and Н / и are different letters.",
            "obj-u1-print-forms",
        ),
        item(
            "u1-i-no-catalog-word",
            "If a licensed catalog word is not mapped to this unit, what should the app do?",
            ["Assign it anyway because it is licensed", "Show it as Unit 1 practice", "Keep it out of this unit and say so"],
            "Keep it out of this unit and say so",
            "A license does not make a word appropriate for Lesson 1. This unit does not use frequency-catalog audio.",
            "obj-u1-sequence",
        ),
        item(
            "u2-i-no-score",
            "Can this app score your handwriting or pronunciation?",
            ["Yes, it assigns an accent score", "Yes, it grades handwriting automatically", "No. Comparison and book work stay human-judged"],
            "No. Comparison and book work stay human-judged",
            "The app can play, record, and let you compare. It does not judge handwriting or pronunciation.",
            "obj-u2-limits",
        ),
        item(
            "u2-i-review-unit1",
            "What may Unit 2 review?",
            ["Only brand-new Lesson 3 grammar", "Earlier Unit 1 letter recognition", "Any later Penguin topic"],
            "Earlier Unit 1 letter recognition",
            "Review may revisit earlier topics. It may not teach later-book grammar early.",
            "obj-u2-review",
        ),
        item(
            "u2-i-handwriting-source",
            "Where do you study handwritten forms while this app has no verified handwriting images?",
            ["From invented app drawings", "From your legally owned book", "From an unrelated catalog word"],
            "From your legally owned book",
            "Missing assets stay unavailable. The companion sends you to your book instead of fabricating forms.",
            "obj-u2-handwriting",
        ),
    ]

    unit1_sessions = [
        session(
            "u1-s-orient",
            "What this unit is for",
            ["obj-u1-sequence"],
            [
                step(
                    "u1-st-orient",
                    "ORIENT",
                    "Unit 1 — Learning to read Russian",
                    "You are at Penguin-aligned Unit 1. The book begins with the writing system, pronunciation, and elementary reading. This companion teaches letter recognition with public-domain letter forms and independently written English. It does not copy the book and does not invent Russian example words.",
                    bookDirection="Open your copy to Lesson 1. Use the book for the author's explanations, exercises, and any example words.",
                ),
                step(
                    "u1-st-why",
                    "TEACH",
                    "Why this comes first",
                    "You cannot use later lessons until you can identify printed Russian letters. The app therefore starts with recognition, print pairs, and confusable Latin look-alikes. Transliteration, if you use it, is a temporary support and must not become the way you read.",
                    referenceIds=["ref-transliteration"],
                ),
            ],
            8,
        ),
        session(
            "u1-s-writing-system",
            "The writing system",
            ["obj-u1-writing-system"],
            [
                step(
                    "u1-st-system",
                    "TEACH",
                    "Cyrillic is a different alphabet",
                    "Russian is written with 33 Cyrillic letters. Some printed shapes look like Latin letters and are not the same letters. Learn identity first, then sound, then decoding. This screen is orientation, not an alphabet dump.",
                    referenceIds=["ref-cyrillic-print"],
                ),
            ],
            8,
        ),
        session(
            "u1-s-vowels",
            "Plain vowel letters",
            ["obj-u1-letter-identity"],
            [
                step(
                    "u1-st-vowels",
                    "TEACH",
                    "First vowel set",
                    "Work these printed pairs in your book and in the letter browser: А а, О о, У у, Э э, Ы ы, И и. English sound notes in Reference are pending qualified review. Do not treat them as a substitute for the book.",
                    referenceIds=["ref-cyrillic-print"],
                ),
                step(
                    "u1-st-vowels-check",
                    "GUIDED",
                    "Identify А",
                    "Closed-response check using letter identity only.",
                    itemIds=["u1-i-а-identity"],
                    remediationStepId="u1-st-vowels",
                ),
            ],
            12,
        ),
        session(
            "u1-s-early-consonants",
            "Early consonant letters",
            ["obj-u1-letter-identity"],
            [
                step(
                    "u1-st-cons",
                    "TEACH",
                    "Low-confusion consonants",
                    "Next printed pairs: М м, Т т, К к, П п, Б б, Д д, Г г. Use the letter browser. Licensed letter-name audio is not installed, so listening for these letters is marked unavailable.",
                    unavailableReason="Licensed letter-name audio has not been intake-reviewed for this unit.",
                    referenceIds=["ref-cyrillic-print"],
                ),
            ],
            12,
        ),
        session(
            "u1-s-confusable",
            "Letters that look like Latin",
            ["obj-u1-confusable"],
            [
                step(
                    "u1-st-confusable",
                    "TEACH",
                    "Shape traps",
                    "These printed letters often fool Latin-alphabet readers: В, Н, Р, С, У, Х. The app asks you to name the Cyrillic identity. It does not assign an unrelated catalog word just because that word is licensed.",
                    referenceIds=["ref-confusable"],
                ),
                step(
                    "u1-st-confusable-r",
                    "CHECK",
                    "Р is not P",
                    "Identity check.",
                    itemIds=["u1-i-р-not-p"],
                    remediationStepId="u1-st-confusable",
                ),
                step(
                    "u1-st-confusable-n",
                    "CHECK",
                    "Н is not H",
                    "Identity check.",
                    itemIds=["u1-i-н-not-h"],
                    remediationStepId="u1-st-confusable",
                ),
                step(
                    "u1-st-confusable-s",
                    "CHECK",
                    "С is not C",
                    "Identity check.",
                    itemIds=["u1-i-с-not-c"],
                    remediationStepId="u1-st-confusable",
                ),
            ],
            15,
        ),
        session(
            "u1-s-iotated",
            "Iotated vowel letters",
            ["obj-u1-iotated"],
            [
                step(
                    "u1-st-iotated",
                    "TEACH",
                    "Е Ё Ю Я",
                    "These letters can start with a /j/-like element or mark a soft preceding consonant. Exact phonetics belong in your book and in a later reviewed note. The app only asks you to recognize the printed forms.",
                    referenceIds=["ref-iotated"],
                ),
            ],
            10,
        ),
        session(
            "u1-s-signs",
            "Soft sign, hard sign, and Й",
            ["obj-u1-signs"],
            [
                step(
                    "u1-st-signs",
                    "TEACH",
                    "Marks that are not ordinary vowels",
                    "Ь, Ъ, and Й need special attention. Do not invent a spoken vowel for the signs. Confirm the book's rule, then use the check.",
                    referenceIds=["ref-signs"],
                ),
                step(
                    "u1-st-signs-check",
                    "CHECK",
                    "Soft sign",
                    "Identity check.",
                    itemIds=["u1-i-ь-not-vowel"],
                    remediationStepId="u1-st-signs",
                ),
            ],
            10,
        ),
        session(
            "u1-s-print",
            "Print forms",
            ["obj-u1-print-forms"],
            [
                step(
                    "u1-st-print",
                    "TEACH",
                    "Uppercase and lowercase together",
                    "Every letter has a print pair. Practice matching them in the letter browser. Italic or handwritten forms wait for Unit 2 and for verified assets.",
                    referenceIds=["ref-cyrillic-print"],
                ),
                step(
                    "u1-st-print-check",
                    "CHECK",
                    "Match a print pair",
                    "Identity check.",
                    itemIds=["u1-i-print-pair"],
                    remediationStepId="u1-st-print",
                ),
            ],
            10,
        ),
        session(
            "u1-s-decoding",
            "Staged decoding",
            ["obj-u1-decoding"],
            [
                step(
                    "u1-st-decoding",
                    "TEACH",
                    "From letters toward reading",
                    "Decoding in this unit stays at letter and print-pair level. Word and sign reading require reviewed examples. Those examples are not installed, so the app will not invent them.",
                    unavailableReason="No reviewed Russian example words or sign texts are installed.",
                    bookDirection="Use the elementary reading work in your Lesson 1 pages. Do not type Penguin examples into the app as if they were course content.",
                    referenceIds=["ref-decoding"],
                ),
            ],
            8,
        ),
        session(
            "u1-s-listening",
            "Listening and pronunciation support",
            ["obj-u1-listening"],
            [
                step(
                    "u1-st-listening",
                    "UNAVAILABLE",
                    "Letter audio is not installed",
                    "Listen-record-compare is available as a tool, but this unit has no licensed, reviewer-mapped letter or example audio. Frequency-catalog words such as common function words are not Lesson 1 material.",
                    unavailableReason="No unit-mapped licensed audio exists for Unit 1.",
                    bookDirection="Use any pronunciation guidance in your book. If you record yourself, the app will not score the take.",
                ),
                step(
                    "u1-st-no-catalog",
                    "CHECK",
                    "Why unused catalog audio stays out",
                    "Sequence check.",
                    itemIds=["u1-i-no-catalog-word"],
                    remediationStepId="u1-st-listening",
                ),
            ],
            8,
        ),
        session(
            "u1-s-stress",
            "Stress awareness",
            ["obj-u1-stress"],
            [
                step(
                    "u1-st-stress",
                    "TEACH",
                    "Stress is part of reading",
                    "Russian word stress is not predictable from spelling alone. This unit only introduces the idea. Marked example words are not installed. Your book remains the place to see stress on real Lesson 1 material.",
                    referenceIds=["ref-stress"],
                    bookDirection="When your Lesson 1 pages mark stress, copy that mark into your binder. Do not paste book words into the app as verified content.",
                ),
            ],
            6,
        ),
        session(
            "u1-s-book",
            "Work in your book",
            ["obj-u1-book"],
            [
                step(
                    "u1-st-book",
                    "BOOK",
                    "Lesson 1 in your copy",
                    "Complete the Lesson 1 reading, pronunciation, and elementary reading work in the edition you own. Enter a page range only as your own note. The app does not store publisher page numbers as official metadata.",
                    bookDirection="Read and do Lesson 1 in your book. Use the book's answer key, not this app, for book exercises.",
                    companionDirection="Afterward, mark book work complete as a self-attestation. That is not demonstrated in-app ability.",
                ),
            ],
            25,
        ),
        session(
            "u1-s-companions",
            "AnkiDroid, binder, and tutor",
            ["obj-u1-companions"],
            [
                step(
                    "u1-st-anki",
                    "ANKI",
                    "AnkiDroid if you use it",
                    "If you selected AnkiDroid, make cards only from material you are allowed to use. The app does not read AnkiDroid and does not verify your deck.",
                    companionDirection="Self-attest AnkiDroid work. It remains user-reported.",
                ),
                step(
                    "u1-st-binder",
                    "BINDER",
                    "One useful binder note",
                    "Write: (1) one confusable letter pair that tricked you, (2) the identity you will trust next time, (3) one question for a tutor if you have one.",
                    companionDirection="A binder prompt is a specific note, not 'take notes.'",
                ),
                step(
                    "u1-st-tutor",
                    "TUTOR",
                    "Optional tutor",
                    "Tutor work is optional for default advancement. You may mark a question or a recording for later review. The app does not replace live correction.",
                ),
            ],
            10,
        ),
        session(
            "u1-s-check",
            "Cumulative letter check",
            ["obj-u1-letter-identity", "obj-u1-confusable", "obj-u1-print-forms"],
            [
                step(
                    "u1-st-cumul",
                    "CHECK",
                    "More than one check",
                    "Foundation advancement needs more than a single tap. Revisit any missed item, then open the explanation it names.",
                    itemIds=["u1-i-а-identity", "u1-i-р-not-p", "u1-i-н-not-h", "u1-i-print-pair"],
                    remediationStepId="u1-st-confusable",
                ),
            ],
            10,
        ),
    ]

    unit2_sessions = [
        session(
            "u2-s-orient",
            "What this unit is for",
            ["obj-u2-handwriting"],
            [
                step(
                    "u2-st-orient",
                    "ORIENT",
                    "Unit 2 — Handwriting and more pronunciation",
                    "Penguin-aligned Unit 2 continues handwritten forms and pronunciation. This companion can coordinate that work. It cannot score handwriting or accent, and it will not invent cursive charts.",
                    bookDirection="Open Lesson 2 in your copy for handwritten forms and the author's pronunciation notes.",
                ),
            ],
            6,
        ),
        session(
            "u2-s-handwriting",
            "Handwritten forms",
            ["obj-u2-handwriting"],
            [
                step(
                    "u2-st-hw",
                    "UNAVAILABLE",
                    "Verified handwriting images are not installed",
                    "Print-to-handwriting correspondence needs reviewed images. None are packaged. Use your book. You may self-attest that you copied forms on paper. The app will not pretend to grade the page.",
                    unavailableReason="No verified handwriting assets are installed.",
                    bookDirection="Copy the handwritten forms from Lesson 2 in your book onto paper.",
                ),
                step(
                    "u2-st-hw-check",
                    "CHECK",
                    "Where handwriting study happens now",
                    "Source check.",
                    itemIds=["u2-i-handwriting-source"],
                    remediationStepId="u2-st-hw",
                ),
            ],
            15,
        ),
        session(
            "u2-s-review",
            "Unit 1 review",
            ["obj-u2-review"],
            [
                step(
                    "u2-st-review",
                    "TEACH",
                    "Keep letter identities available",
                    "Unit 2 may review any Unit 1 letter. It may not jump to Lesson 3 grammar.",
                    referenceIds=["ref-cyrillic-print", "ref-confusable"],
                ),
                step(
                    "u2-st-review-check",
                    "CHECK",
                    "What review is allowed",
                    "Sequence check.",
                    itemIds=["u2-i-review-unit1"],
                    remediationStepId="u2-st-review",
                ),
            ],
            10,
        ),
        session(
            "u2-s-pronunciation",
            "Additional pronunciation",
            ["obj-u2-pronunciation"],
            [
                step(
                    "u2-st-pron",
                    "TEACH",
                    "Listen in the book; compare in the app only with mapped audio",
                    "Use Lesson 2 pronunciation work in your book. App listen-record-compare stays unavailable for this unit until reviewer-mapped audio exists. You may still record a take for your own ears or a tutor. The recording is not a score.",
                    unavailableReason="No unit-mapped licensed audio exists for Unit 2.",
                    referenceIds=["ref-pronunciation"],
                ),
                step(
                    "u2-st-no-score",
                    "CHECK",
                    "No automatic judgment",
                    "Limits check.",
                    itemIds=["u2-i-no-score"],
                    remediationStepId="u2-st-pron",
                ),
            ],
            10,
        ),
        session(
            "u2-s-reading",
            "Reading fluency reinforcement",
            ["obj-u2-reading"],
            [
                step(
                    "u2-st-reading",
                    "TEACH",
                    "Read what the book provides",
                    "Fluency practice in this unit is book-directed. The app will not generate short phrases. Re-run letter recognition if a form is still slow.",
                    bookDirection="Read the Lesson 2 material in your copy aloud. Mark difficult letters for review.",
                    referenceIds=["ref-decoding"],
                ),
            ],
            12,
        ),
        session(
            "u2-s-record",
            "Self-record and tutor queue",
            ["obj-u2-limits"],
            [
                step(
                    "u2-st-record",
                    "SPEAK",
                    "Optional recording without a score",
                    "You may record yourself reading from your book or repeating a form. Play the take back. Mark it for a tutor if you want human review. The app never transcribes or scores it.",
                ),
            ],
            8,
        ),
        session(
            "u2-s-book",
            "Work in your book",
            ["obj-u2-book"],
            [
                step(
                    "u2-st-book",
                    "BOOK",
                    "Lesson 2 in your copy",
                    "Complete Lesson 2 handwriting and pronunciation work in the edition you own. Self-attest when finished.",
                    bookDirection="Do Lesson 2 in your book, including handwritten-form practice.",
                ),
            ],
            25,
        ),
        session(
            "u2-s-check",
            "Unit 2 check",
            ["obj-u2-handwriting", "obj-u2-review", "obj-u2-limits"],
            [
                step(
                    "u2-st-check",
                    "CHECK",
                    "Confirm the unit limits",
                    "Closed-response items about sources and scoring limits.",
                    itemIds=["u2-i-handwriting-source", "u2-i-review-unit1", "u2-i-no-score"],
                    remediationStepId="u2-st-hw",
                ),
            ],
            8,
        ),
    ]

    objectives = [
        {"id": "obj-u1-sequence", "unitNumber": 1, "title": "Know why Unit 1 is the literacy start", "kind": "GUIDANCE"},
        {"id": "obj-u1-writing-system", "unitNumber": 1, "title": "Describe Cyrillic as a 33-letter print system", "kind": "GUIDANCE"},
        {"id": "obj-u1-letter-identity", "unitNumber": 1, "title": "Identify staged printed letters", "kind": "RECOGNITION"},
        {"id": "obj-u1-confusable", "unitNumber": 1, "title": "Separate Cyrillic letters from Latin look-alikes", "kind": "RECOGNITION"},
        {"id": "obj-u1-iotated", "unitNumber": 1, "title": "Recognize Е Ё Ю Я as a group", "kind": "RECOGNITION"},
        {"id": "obj-u1-signs", "unitNumber": 1, "title": "Treat Ь Ъ Й as non-ordinary-vowel letters", "kind": "RECOGNITION"},
        {"id": "obj-u1-print-forms", "unitNumber": 1, "title": "Match uppercase and lowercase print pairs", "kind": "RECOGNITION"},
        {"id": "obj-u1-decoding", "unitNumber": 1, "title": "Know that word reading waits for reviewed examples", "kind": "GUIDANCE"},
        {"id": "obj-u1-listening", "unitNumber": 1, "title": "Know that unmapped audio is not Unit 1 practice", "kind": "GUIDANCE"},
        {"id": "obj-u1-stress", "unitNumber": 1, "title": "Know that stress must be learned with real book words", "kind": "GUIDANCE"},
        {"id": "obj-u1-book", "unitNumber": 1, "title": "Complete Lesson 1 work in the owned book", "kind": "SELF_ATTESTATION"},
        {"id": "obj-u1-companions", "unitNumber": 1, "title": "Use Anki, binder, and optional tutor without pretending they are scored", "kind": "GUIDANCE"},
        {"id": "obj-u2-handwriting", "unitNumber": 2, "title": "Practice handwritten forms from the book while assets are absent", "kind": "SELF_ATTESTATION"},
        {"id": "obj-u2-review", "unitNumber": 2, "title": "Review Unit 1 letters without jumping ahead", "kind": "RECOGNITION"},
        {"id": "obj-u2-pronunciation", "unitNumber": 2, "title": "Continue pronunciation with book work and optional unscored recording", "kind": "GUIDANCE"},
        {"id": "obj-u2-reading", "unitNumber": 2, "title": "Reinforce reading using the book, not invented phrases", "kind": "SELF_ATTESTATION"},
        {"id": "obj-u2-limits", "unitNumber": 2, "title": "State that the app does not score handwriting or pronunciation", "kind": "GUIDANCE"},
        {"id": "obj-u2-book", "unitNumber": 2, "title": "Complete Lesson 2 work in the owned book", "kind": "SELF_ATTESTATION"},
    ]

    references = [
        reference(
            "ref-cyrillic-print",
            "Cyrillic print forms",
            "33 printed uppercase/lowercase pairs. Browse letters; do not treat this as a dump to memorize in one sitting.",
            "Use the letter browser grouped by the Unit 1 sequence. English sound notes are pending review. No example words are attached.",
            1,
        ),
        reference(
            "ref-confusable",
            "Confusable Latin look-alikes",
            "В Н Р С У Х can be misread by Latin-alphabet readers.",
            "Name the Cyrillic identity. Confirm sounds in your book. The app does not attach catalog words to these letters.",
            1,
        ),
        reference(
            "ref-iotated",
            "Iotated vowel letters",
            "Е Ё Ю Я are a recognition group in Unit 1.",
            "Detailed phonetics remain in the book until a reviewer signs an app note.",
            1,
        ),
        reference(
            "ref-signs",
            "Soft sign, hard sign, and Й",
            "Ь and Ъ are signs. Й is not a standalone vowel in this foundation.",
            "Do not invent a spoken vowel for the signs. Use the book for the exact rule.",
            1,
        ),
        reference(
            "ref-transliteration",
            "Transliteration is temporary",
            "Latin respelling may help the first hours. It is not how you should keep reading.",
            "Turn transliteration off in settings as soon as print recognition is stable. The default after foundations is off.",
            1,
        ),
        reference(
            "ref-decoding",
            "Staged decoding limits",
            "This pack decodes letters and print pairs only.",
            "Word, phrase, and sign reading wait for reviewed examples. Unavailable is the honest state.",
            1,
        ),
        reference(
            "ref-stress",
            "Stress notation",
            "Stress is part of Russian reading and is not guessed by the app.",
            "Copy stress marks from your book into your binder. The app will not generate stressed example words.",
            1,
        ),
        reference(
            "ref-pronunciation",
            "Pronunciation guidance",
            "Model audio is used only when a file is licensed and mapped to the current objective.",
            "Slowing a model must not rewrite the file. User recordings play at original speed. No ASR or score.",
            2,
        ),
        reference(
            "ref-study",
            "How to use this companion",
            "The app coordinates the book, optional AnkiDroid, a binder, and optional tutor work.",
            "It does not reproduce Penguin, judge spontaneous speech, or certify proficiency. Public release stays blocked until review and device evidence exist.",
            1,
        ),
        reference(
            "ref-limits",
            "App limitations",
            "Offline only. No internet, no account, no AI, no cloud.",
            "Missing content stays unavailable. Catalog size is not a curriculum.",
            1,
        ),
    ]

    def installed_unit(number: int, title: str, summary: str, sessions: list, prereq: list[int]) -> dict:
        return {
            "number": number,
            "id": f"unit-{number}",
            "title": title,
            "topicSummary": summary,
            "installState": "INSTALLED",
            "penguinLesson": number,
            "mappingStatus": "PENDING_CURRICULUM_REVIEWER",
            "mappingNote": "Topic-level alignment uses the publisher's public table of contents. Edition comparison is not signed.",
            "prerequisiteUnitNumbers": prereq,
            "estimatedMinutes": sum(s["estimatedMinutes"] for s in sessions),
            "objectives": [o for o in objectives if o["unitNumber"] == number],
            "sessions": sessions,
            "advancement": {
                "requireInstruction": True,
                "requireBookSelfAttestation": True,
                "requireAppChecks": True,
                "minimumDistinctChecks": 2,
                "requireDueReview": number > 1,
                "tutorOptional": True,
            },
        }

    units = [
        installed_unit(
            1,
            "Learning to read Russian",
            "Alphabet, pronunciation, transliteration, and controlled elementary reading. Independently written companion support only.",
            unit1_sessions,
            [],
        ),
        installed_unit(
            2,
            "Handwriting and additional pronunciation",
            "Handwritten-form recognition and further pronunciation, with Unit 1 review. Book-directed while handwriting assets are absent.",
            unit2_sessions,
            [1],
        ),
    ]
    for number, title in UNITS_3_30:
        units.append(
            {
                "number": number,
                "id": f"unit-{number}",
                "title": title,
                "topicSummary": title,
                "installState": "NOT_YET_INSTALLED",
                "penguinLesson": number,
                "mappingStatus": "NOT_STARTED",
                "mappingNote": "The topic identifier is recorded. No lesson body, audio, or exercises are installed.",
                "prerequisiteUnitNumbers": [number - 1],
                "estimatedMinutes": None,
                "objectives": [],
                "sessions": [],
                "advancement": None,
            }
        )

    pack = {
        "schemaVersion": 1,
        "packId": "v2-units-1-2-foundation",
        "packVersion": 1,
        "publicReleaseEligible": False,
        "linguisticReview": REVIEW_PENDING,
        "sources": [SOURCE_LETTERS, SOURCE_ENGLISH],
        "letters": letters,
        "items": items,
        "references": references,
        "units": units,
        "admissions": {
            "generatedRussianWords": 0,
            "penguinProseAssets": 0,
            "catalogAudioBoundToUnits": 0,
            "unverifiedExampleWords": 0,
        },
    }
    return pack


def main() -> None:
    pack = build()
    OUT.parent.mkdir(parents=True, exist_ok=True)
    OUT.write_text(json.dumps(pack, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    print(f"wrote {OUT} units={len(pack['units'])} letters={len(pack['letters'])} items={len(pack['items'])}")


if __name__ == "__main__":
    main()
