#!/usr/bin/env python3
"""Build the v2 curriculum pack. Independently authored English only.

Cyrillic letters are public-domain writing-system facts. No Penguin prose,
no generated example words, no catalog-order audio bindings.

Units 1–2 are content-complete DEVELOPMENT units awaiting qualified
review and an owned-edition mapping. Units 3–5 are DEVELOPMENT units with
proposed curriculum taken only from frozen CC BY-SA 4.0 sources. They are
not installed, not review-approved, and not mapping-approved. Units 1–2
advancement checks are letter identities, print pairs, confusable forms,
and signs. Units 3–10 checks use admitted source records only. Units 6–10 are the same kind of proposed, review-pending curriculum.
"""

from __future__ import annotations

import json
from pathlib import Path

from units_3_5_intake import build_units_3_5
from units_6_10_intake import build_units_6_10
from units_11_15_intake import build_units_11_15
from units_16_20_intake import build_units_16_20
from units_21_25_intake import build_units_21_25
from units_26_30_intake import build_units_26_30

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

RIGHTS = [
    {
        "id": "rights-unicode-cyrillic",
        "sourceId": SOURCE_LETTERS["id"],
        "license": SOURCE_LETTERS["license"],
        "permitsAppUse": True,
    },
    {
        "id": "rights-app-english-v2",
        "sourceId": SOURCE_ENGLISH["id"],
        "license": SOURCE_ENGLISH["license"],
        "permitsAppUse": True,
    },
]

REVIEW_PENDING = {
    "id": "rev-pending-v2-curriculum",
    "status": "PENDING_QUALIFIED_REVIEW",
    "reviewer": None,
    "packVersion": None,
    "reviewedContentDigest": None,
    "curriculumMappingId": None,
    "reviewerQualification": None,
    "notes": "Pack-level status is not a unit approval. Each unit has its own pending review record.",
}


def pending_unit_review(number: int) -> dict:
    return {
        "id": f"rev-pending-unit-{number}",
        "unitNumber": number,
        "status": "PENDING_QUALIFIED_REVIEW",
        "reviewer": None,
        "reviewerQualification": None,
        "packVersion": None,
        "reviewedContentDigest": None,
        "curriculumMappingId": None,
        "notes": f"No qualified reviewer has signed Unit {number}.",
    }

UNITS_3_30 = [
    (3, "Everyday phrases", "Everyday phrases, naming things, basic grammar, and gender"),
    (4, "Verbs and cases", "Verbs, personal pronouns, present tense, conjugation, word stress, and introduction to nouns and cases"),
    (5, "Questions", "Questions, prepositional case, and indeclinable nouns"),
    (6, "Possession", "Possession, going places, accusative case, and personal names"),
    (7, "Adjectives", "Adjectives, agreement, demonstratives, adjective case forms, and adverbs"),
    (8, "Plurals", "Plurals, first spelling rules, existence questions, and buying things"),
    (9, "Numbers", "Numbers, genitive singular and plural, quantities, and money"),
    (10, "Having and absence", "Expressions of having, expanded genitive functions, absence, genitive prepositions, pronouns, and adjectives"),
    (11, "Past tense", "Past tense and reflexive verbs"),
    (12, "Future and dative", "Future, aspect introduction, dative case, additional spelling rules, and prepositional plural"),
    (13, "Aspect in the past", "Aspect in the past, tense use, infinitive aspect, and reported speech"),
    (14, "Aspect in the future", "Aspect in the future and impersonal constructions"),
    (15, "Requests", "Requests, the imperative, and consolidation of aspect use"),
    (16, "Instrumental", "Instrumental case: principal uses; singular and plural noun forms; adjective agreement; common instrumental-governing constructions; instrumental complements with identity, role, or becoming"),
    (17, "Time and dates", "Time, weekdays, calendar dates, years, age expressions, and ordinal numbers"),
    (18, "Comparison", "Comparative forms, superlative expression, and relative clauses built with the inflected relative pronoun"),
    (19, "Conditionals", "Conditional constructions; obligation or probability; productive meanings of selected verbal prefixes"),
    (20, "Motion verbs", "Unidirectional and multidirectional verbs of motion; motion by foot or transport; carrying and leading pairs; selected prefixed motion verbs"),
    (21, "Possession", "Possession with the reflexive possessive pronoun; reflexive reference; and purpose clauses"),
    (22, "Numerals", "Numeral behavior beyond basic counting, including declined numerals, forms for both and one and a half, compound quantities, and collective or quantity expressions"),
    (23, "Duration", "Expressions of elapsed time, duration, ago, during, and distinctions involving completion over a period"),
    (24, "Negation", "Negation, case behavior under negation, negative pronouns or particles, and the position or scope of не"),
    (25, "Names", "Diminutives, Russian personal-name forms, social address, requests, apologies, and polite expressions"),
    (26, "Word order", "Indefinite pronouns, word order, and letter writing"),
    (27, "Participles", "Participles: types and stress"),
    (28, "Verbal adverbs", "Verbal adverbs"),
    (29, "Bookish style", "Bookish style, active participles, punctuation, and short-form adjectives"),
    (30, "Particles", "Abbreviations, names of Russian letters, and particles"),
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


def letter_id(upper: str) -> str:
    return f"letter-{ord(upper):04x}"


LATIN_LOOKALIKES = {
    "В": "B",
    "Н": "H",
    "Р": "P",
    "С": "C",
    "У": "Y",
    "Х": "X",
}


def letter_obj(upper: str, lower: str, group: str, note: str) -> dict:
    return {
        "id": letter_id(upper),
        "uppercase": upper,
        "lowercase": lower,
        "group": group,
        "englishNote": note,
        "sourceId": SOURCE_LETTERS["id"],
        "reviewStatus": "PENDING_QUALIFIED_REVIEW",
        "audioAssetId": None,
        "exampleWordId": None,
        "latinLookalike": LATIN_LOOKALIKES.get(upper),
    }


def example(example_id: str, kind: str, upper: str, display: str, note: str, objective: str) -> dict:
    return {
        "id": example_id,
        "kind": kind,
        "letterId": letter_id(upper),
        "display": display,
        "note": note,
        "sourceId": SOURCE_LETTERS["id"],
        "reviewStatus": "PENDING_QUALIFIED_REVIEW",
        "objectiveId": objective,
    }


def step(step_id: str, kind: str, title: str, body: str, **extra) -> dict:
    return {
        "id": step_id,
        "kind": kind,
        "title": title,
        "body": body,
        "dominantAction": extra.get("dominantAction", "Continue"),
        "referenceIds": extra.get("referenceIds", []),
        "itemIds": extra.get("itemIds", []),
        "exampleIds": extra.get("exampleIds", []),
        "unavailableReason": extra.get("unavailableReason"),
        "bookDirection": extra.get("bookDirection"),
        "companionDirection": extra.get("companionDirection"),
        "remediationStepId": extra.get("remediationStepId"),
    }


def item(
    item_id: str,
    prompt: str,
    options: list[str],
    answer: str,
    feedback: str,
    objective: str,
    prompt_letter: str,
    kind: str,
) -> dict:
    return {
        "id": item_id,
        "kind": kind,
        "language": "en",
        "prompt": prompt,
        "promptLetter": prompt_letter,
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


REFERENCE_CLASSIFICATIONS = {
    "LINGUISTIC_INSTRUCTION",
    "PRODUCT_HELP",
    "LICENSE_OR_ATTRIBUTION",
    "OPERATIONAL_GUIDANCE",
}


def reference(
    entry_id: str,
    title: str,
    summary: str,
    detail: str,
    unit: int,
    classification: str,
    review: str = "PENDING_QUALIFIED_REVIEW",
) -> dict:
    if classification not in REFERENCE_CLASSIFICATIONS:
        raise ValueError(f"{entry_id} has unknown classification {classification}")
    return {
        "id": entry_id,
        "title": title,
        "summary": summary,
        "detail": detail,
        "introducedInUnit": unit,
        "sourceId": SOURCE_ENGLISH["id"],
        "classification": classification,
        "reviewStatus": review,
        "searchTerms": [title.lower()],
    }


def objective(
    obj_id: str,
    unit: int,
    title: str,
    kind: str,
    instruction: list[str],
    examples: list[str],
    practice: list[str],
    checks: list[str],
    remediation: list[str],
) -> dict:
    return {
        "id": obj_id,
        "unitNumber": unit,
        "title": title,
        "kind": kind,
        "instructionStepIds": instruction,
        "exampleIds": examples,
        "practiceStepIds": practice,
        "checkItemIds": checks,
        "remediationStepIds": remediation,
    }


def book_directed_topics(number: int, topics: list[tuple[str, str, str, str]]) -> dict:
    """Name each public topic and send that work to the owned book.

    topics: (suffix, session title, step title, book direction)
    These sessions are not content-complete. They do not add recognition
    items, examples, or letter drills as a stand-in for the lesson.
    """
    book_id = f"obj-u{number}-book"
    book_steps = []
    book_sessions = []
    for suffix, session_title, step_title, direction in topics:
        step_id = f"u{number}-st-{suffix}"
        book_steps.append(step_id)
        book_sessions.append(
            session(
                f"u{number}-s-{suffix}",
                session_title,
                [book_id],
                [
                    step(
                        step_id,
                        "BOOK",
                        step_title,
                        direction,
                        bookDirection=direction,
                        referenceIds=["ref-book-topics"],
                    ),
                ],
                12,
            )
        )
    attest_id = f"u{number}-st-book"
    sessions = [
        session(
            f"u{number}-s-orient",
            "What this unit does not contain",
            [book_id],
            [
                step(
                    f"u{number}-st-orient",
                    "ORIENT",
                    f"Unit {number} is not content-complete",
                    (
                        f"Unit {number} follows the previous unit in the public lesson order. "
                        "Its new explanations, examples, guided practice, independent checks, "
                        "answer feedback, and remediation are not in this app. "
                        "The later steps name the part of the owned lesson to do. "
                        "They do not copy that lesson, and they do not make this unit content-complete."
                    ),
                    bookDirection=f"Open Lesson {number} in your copy before the later steps.",
                    referenceIds=["ref-book-topics"],
                ),
            ],
            6,
        ),
        *book_sessions,
        session(
            f"u{number}-s-book",
            f"Self-attest Lesson {number}",
            [book_id],
            [
                step(
                    attest_id,
                    "BOOK",
                    f"Record Lesson {number} book work",
                    (
                        f"When you have done Lesson {number} in your copy, self-attest here. "
                        "Attestation records your report. It does not grade the lesson, "
                        "approve a mapping, or mark this unit content-complete."
                    ),
                    bookDirection=(
                        f"After Lesson {number} is finished in your copy, return here and self-attest."
                    ),
                    referenceIds=["ref-book-topics"],
                ),
            ],
            8,
        ),
    ]
    objectives = [
        objective(
            book_id,
            number,
            f"Do Lesson {number} in the owned book and self-attest",
            "SELF_ATTESTATION",
            book_steps + [attest_id],
            [],
            [],
            [],
            [],
        ),
    ]
    return {"items": [], "examples": [], "sessions": sessions, "objectives": objectives}


def build() -> dict:
    letters = [letter_obj(*row) for row in LETTERS]
    items = [
        item(
            "u1-i-а-identity",
            "Which printed letter is shown?",
            ["Latin A", "Cyrillic А", "Cyrillic Р"],
            "Cyrillic А",
            "The printed shape can match Latin A. In this course it is the Cyrillic vowel letter А.",
            "obj-u1-letter-identity",
            "А",
            "LETTER_IDENTITY",
        ),
        item(
            "u1-i-м-identity",
            "Which printed letter is shown?",
            ["Cyrillic М", "Cyrillic Н", "Cyrillic Т"],
            "Cyrillic М",
            "М is an early consonant letter. It is not Н.",
            "obj-u1-letter-identity",
            "М",
            "LETTER_IDENTITY",
        ),
        item(
            "u1-i-р-not-p",
            "This letter looks like Latin P. What is it?",
            ["The Latin letter P", "The Cyrillic letter Р", "The Cyrillic letter П"],
            "The Cyrillic letter Р",
            "Shape overlap with Latin P is a recognition trap. This is Cyrillic Р, not English P.",
            "obj-u1-confusable",
            "Р",
            "CONFUSABLE",
        ),
        item(
            "u1-i-н-not-h",
            "This letter looks like Latin H. What is it?",
            ["Latin H", "Cyrillic Н", "Cyrillic И"],
            "Cyrillic Н",
            "Do not read Н as English H. It is a Cyrillic letter.",
            "obj-u1-confusable",
            "Н",
            "CONFUSABLE",
        ),
        item(
            "u1-i-с-not-c",
            "This letter looks like Latin C. What is it?",
            ["Latin C", "Cyrillic С", "Cyrillic К"],
            "Cyrillic С",
            "С is a Cyrillic letter. Do not treat the Latin look-alike as the identity.",
            "obj-u1-confusable",
            "С",
            "CONFUSABLE",
        ),
        item(
            "u1-i-в-not-b",
            "This letter looks like Latin B. What is it?",
            ["Latin B", "Cyrillic В", "Cyrillic Б"],
            "Cyrillic В",
            "В is not Latin B and it is not Cyrillic Б.",
            "obj-u1-confusable",
            "В",
            "CONFUSABLE",
        ),
        item(
            "u1-i-у-not-y",
            "This letter looks like Latin Y. What is it?",
            ["Latin Y", "Cyrillic У", "Cyrillic И"],
            "Cyrillic У",
            "У is a Cyrillic vowel letter, not Latin Y.",
            "obj-u1-confusable",
            "У",
            "CONFUSABLE",
        ),
        item(
            "u1-i-х-not-x",
            "This letter looks like Latin X. What is it?",
            ["Latin X", "Cyrillic Х", "Cyrillic К"],
            "Cyrillic Х",
            "Х is a Cyrillic letter, not Latin X.",
            "obj-u1-confusable",
            "Х",
            "CONFUSABLE",
        ),
        item(
            "u1-i-ё-identity",
            "Which printed letter is shown?",
            ["Cyrillic Ё", "Cyrillic Е", "Cyrillic Э"],
            "Cyrillic Ё",
            "Ё is its own letter. Е and Э are different letters.",
            "obj-u1-iotated",
            "Ё",
            "LETTER_IDENTITY",
        ),
        item(
            "u1-i-ю-identity",
            "Which printed letter is shown?",
            ["Cyrillic Ю", "Cyrillic У", "Cyrillic Я"],
            "Cyrillic Ю",
            "Ю is an iotated vowel letter. У and Я are different letters.",
            "obj-u1-iotated",
            "Ю",
            "LETTER_IDENTITY",
        ),
        item(
            "u1-i-я-identity",
            "Which printed letter is shown?",
            ["Cyrillic Я", "Cyrillic Р", "Cyrillic А"],
            "Cyrillic Я",
            "Я is an iotated vowel letter. It is not Р or А.",
            "obj-u1-iotated",
            "Я",
            "LETTER_IDENTITY",
        ),
        item(
            "u1-i-й-not-vowel",
            "What is the printed letter Й?",
            ["A standalone vowel", "A consonant letter, not a vowel", "The same mark as Ь"],
            "A consonant letter, not a vowel",
            "Й is not a vowel you read alone and it is not the soft sign.",
            "obj-u1-signs",
            "Й",
            "SIGN",
        ),
        item(
            "u1-i-decode-pair",
            "Which line is one letter written in both print cases?",
            ["А / а", "А / О", "М / Т"],
            "А / а",
            "А / а is one letter. The other lines are two different letters.",
            "obj-u1-decoding",
            "А",
            "PRINT_PAIR",
        ),
        item(
            "u1-i-е-identity",
            "Which printed letter is shown?",
            ["Cyrillic Е", "Cyrillic Э", "Cyrillic Ё"],
            "Cyrillic Е",
            "Е is an iotated vowel letter. Э and Ё are different letters.",
            "obj-u1-iotated",
            "Е",
            "LETTER_IDENTITY",
        ),
        item(
            "u1-i-ь-not-vowel",
            "What is the printed mark Ь?",
            ["A vowel you pronounce by itself", "A mark, not a standalone vowel", "The same letter as Ъ"],
            "A mark, not a standalone vowel",
            "Ь is a sign, not a vowel letter you read alone.",
            "obj-u1-signs",
            "Ь",
            "SIGN",
        ),
        item(
            "u1-i-ъ-not-vowel",
            "What is the printed mark Ъ?",
            ["A standalone vowel", "The hard sign, not a vowel", "The same letter as Ь"],
            "The hard sign, not a vowel",
            "Ъ is the hard sign. It is not a spoken vowel and it is not Ь.",
            "obj-u1-signs",
            "Ъ",
            "SIGN",
        ),
        item(
            "u1-i-print-pair",
            "Which pair is the same letter in uppercase and lowercase print?",
            ["А / а", "Р / п", "Н / и"],
            "А / а",
            "Print pairs are one letter in two cases. Р / п and Н / и are different letters.",
            "obj-u1-print-forms",
            "А",
            "PRINT_PAIR",
        ),
        item(
            "u2-i-в-not-b",
            "This letter looks like Latin B. What is it?",
            ["Latin B", "Cyrillic В", "Cyrillic Б"],
            "Cyrillic В",
            "В is not Latin B and it is not Cyrillic Б.",
            "obj-u2-review",
            "В",
            "CONFUSABLE",
        ),
        item(
            "u2-i-у-not-y",
            "This letter looks like Latin Y. What is it?",
            ["Latin Y", "Cyrillic У", "Cyrillic И"],
            "Cyrillic У",
            "У is a Cyrillic vowel letter, not Latin Y.",
            "obj-u2-review",
            "У",
            "CONFUSABLE",
        ),
        item(
            "u2-i-х-not-x",
            "This letter looks like Latin X. What is it?",
            ["Latin X", "Cyrillic Х", "Cyrillic К"],
            "Cyrillic Х",
            "Х is a Cyrillic letter, not Latin X.",
            "obj-u2-review",
            "Х",
            "CONFUSABLE",
        ),
        item(
            "u2-i-print-б",
            "Which pair is the same letter in uppercase and lowercase print?",
            ["Б / б", "В / б", "Б / в"],
            "Б / б",
            "Б / б is one letter. В is a different letter.",
            "obj-u2-review",
            "Б",
            "PRINT_PAIR",
        ),
        item(
            "u2-i-я-identity",
            "Which printed letter is shown?",
            ["Cyrillic Я", "Cyrillic Р", "Cyrillic А"],
            "Cyrillic Я",
            "Я is an iotated vowel letter. It is not Р or А.",
            "obj-u2-review",
            "Я",
            "LETTER_IDENTITY",
        ),
    ]

    examples = []
    for upper, lower, group, _note in LETTERS:
        lid = letter_id(upper)
        if group == "confusable":
            objective_id = "obj-u1-confusable"
            kind = "CONFUSABLE_PAIR"
            display = f"{upper} / {lower}"
            note = f"Printed pair {upper} {lower}. Treat the Cyrillic identity, not a Latin look-alike."
        elif group == "iotated":
            objective_id = "obj-u1-iotated"
            kind = "LETTER_FORM"
            display = f"{upper} / {lower}"
            note = f"Printed pair {upper} {lower}."
        elif group == "signs":
            objective_id = "obj-u1-signs"
            kind = "LETTER_FORM"
            display = f"{upper} / {lower}"
            note = f"Printed pair {upper} {lower}."
        elif group == "vowel-plain" or upper in {"А", "О", "У", "Э", "Ы", "И"}:
            objective_id = "obj-u1-letter-identity" if upper != "У" else "obj-u1-confusable"
            kind = "PRINT_PAIR" if upper in {"А", "О", "И"} else "LETTER_FORM"
            display = f"{upper} / {lower}"
            note = f"Printed pair {upper} {lower}."
        else:
            objective_id = "obj-u1-letter-identity"
            kind = "PRINT_PAIR" if upper in {"М", "Т", "Б"} else "LETTER_FORM"
            display = f"{upper} / {lower}"
            note = f"Printed pair {upper} {lower}."
        examples.append(example(f"ex-{lid}", kind, upper, display, note, objective_id))
        if upper in {"А", "Б", "М"}:
            examples.append(
                example(
                    f"ex-print-{lid}",
                    "PRINT_PAIR",
                    upper,
                    f"{upper} / {lower}",
                    f"{upper} and {lower} are the same letter in two print cases.",
                    "obj-u1-print-forms" if upper in {"А", "М"} else "obj-u2-review",
                )
            )

    example_ids_by_objective: dict[str, list[str]] = {}
    for ex in examples:
        example_ids_by_objective.setdefault(ex["objectiveId"], []).append(ex["id"])

    unit1_sessions = [
        session(
            "u1-s-orient",
            "What this unit is for",
            ["obj-u1-letter-identity"],
            [
                step(
                    "u1-st-orient",
                    "ORIENT",
                    "Unit 1 — Printed letters",
                    "This is a proposed Lesson 1 mapping, not an approved owned-edition alignment. The development unit teaches printed letter identities with public-domain letter forms. Example words and model audio are not part of this unit. Qualified review is still pending.",
                    bookDirection="Open your copy to Lesson 1. Use the book for the author's explanations, exercises, and any example words.",
                ),
                step(
                    "u1-st-why",
                    "TEACH",
                    "Identify letters first",
                    "Work letter shapes, print pairs, and Latin look-alikes before later reading. Transliteration, if you use it, is a temporary support.",
                    referenceIds=["ref-transliteration"],
                ),
            ],
            8,
        ),
        session(
            "u1-s-writing-system",
            "The writing system",
            ["obj-u1-letter-identity"],
            [
                step(
                    "u1-st-system",
                    "TEACH",
                    "Cyrillic is a different alphabet",
                    "Russian is written with 33 Cyrillic letters. Some printed shapes look like Latin letters and are not the same letters. Learn identity first.",
                    referenceIds=["ref-cyrillic-print"],
                    exampleIds=["ex-letter-0410", "ex-letter-041c"],
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
                    "Work these printed pairs: А а, О о, У у, Э э, Ы ы, И и. English sound notes are pending qualified review.",
                    referenceIds=["ref-cyrillic-print"],
                    exampleIds=["ex-letter-0410", "ex-letter-041e", "ex-letter-0418"],
                ),
                step(
                    "u1-st-vowels-check",
                    "GUIDED",
                    "Identify А",
                    "Choose the Cyrillic identity for the printed letter.",
                    itemIds=["u1-i-а-identity"],
                    exampleIds=["ex-letter-0410"],
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
                    "Next printed pairs: М м, Т т, К к, П п, Б б, Д д, Г г. Confirm each spoken sound in your book. This unit does not play model audio.",
                    referenceIds=["ref-cyrillic-print"],
                    exampleIds=["ex-letter-041c", "ex-letter-0422", "ex-letter-0411"],
                ),
                step(
                    "u1-st-cons-check",
                    "GUIDED",
                    "Identify М",
                    "Choose the Cyrillic identity for the printed letter.",
                    itemIds=["u1-i-м-identity"],
                    exampleIds=["ex-letter-041c"],
                    remediationStepId="u1-st-cons",
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
                    "These printed letters often fool Latin-alphabet readers: В, Н, Р, С, У, Х. Name the Cyrillic identity.",
                    referenceIds=["ref-confusable"],
                    exampleIds=["ex-letter-0420", "ex-letter-041d", "ex-letter-0421"],
                ),
                step(
                    "u1-st-confusable-r",
                    "CHECK",
                    "Р is not P",
                    "Identity check.",
                    itemIds=["u1-i-р-not-p"],
                    exampleIds=["ex-letter-0420"],
                    remediationStepId="u1-st-confusable",
                ),
                step(
                    "u1-st-confusable-n",
                    "CHECK",
                    "Н is not H",
                    "Identity check.",
                    itemIds=["u1-i-н-not-h"],
                    exampleIds=["ex-letter-041d"],
                    remediationStepId="u1-st-confusable",
                ),
                step(
                    "u1-st-confusable-s",
                    "CHECK",
                    "С is not C",
                    "Identity check.",
                    itemIds=["u1-i-с-not-c"],
                    exampleIds=["ex-letter-0421"],
                    remediationStepId="u1-st-confusable",
                ),
                step(
                    "u1-st-confusable-rest",
                    "CHECK",
                    "В, У, and Х",
                    "Identity check for the remaining look-alikes.",
                    itemIds=["u1-i-в-not-b", "u1-i-у-not-y", "u1-i-х-not-x"],
                    exampleIds=["ex-letter-0412", "ex-letter-0423", "ex-letter-0425"],
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
                    "Recognize these printed forms as a group. Exact phonetics belong in your book.",
                    referenceIds=["ref-iotated"],
                    exampleIds=["ex-letter-0415", "ex-letter-042f"],
                ),
                step(
                    "u1-st-iotated-check",
                    "CHECK",
                    "Identify Е, Ё, Ю, and Я",
                    "Identity check.",
                    itemIds=["u1-i-е-identity", "u1-i-ё-identity", "u1-i-ю-identity", "u1-i-я-identity"],
                    exampleIds=["ex-letter-0415", "ex-letter-0401", "ex-letter-042e", "ex-letter-042f"],
                    remediationStepId="u1-st-iotated",
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
                    "Ь, Ъ, and Й need special attention. Do not invent a spoken vowel for the signs.",
                    referenceIds=["ref-signs"],
                    exampleIds=["ex-letter-042c", "ex-letter-042a", "ex-letter-0419"],
                ),
                step(
                    "u1-st-signs-check",
                    "CHECK",
                    "Soft sign",
                    "Identity check.",
                    itemIds=["u1-i-ь-not-vowel"],
                    exampleIds=["ex-letter-042c"],
                    remediationStepId="u1-st-signs",
                ),
                step(
                    "u1-st-hard-check",
                    "CHECK",
                    "Hard sign",
                    "Identity check.",
                    itemIds=["u1-i-ъ-not-vowel"],
                    exampleIds=["ex-letter-042a"],
                    remediationStepId="u1-st-signs",
                ),
                step(
                    "u1-st-short-i-check",
                    "CHECK",
                    "Й is not a vowel",
                    "Identity check.",
                    itemIds=["u1-i-й-not-vowel"],
                    exampleIds=["ex-letter-0419"],
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
                    "Every letter has a print pair. Match them on the letter board. Handwritten forms wait for verified assets.",
                    referenceIds=["ref-cyrillic-print"],
                    exampleIds=["ex-print-letter-0410"],
                ),
                step(
                    "u1-st-print-check",
                    "CHECK",
                    "Match a print pair",
                    "Identity check.",
                    itemIds=["u1-i-print-pair"],
                    exampleIds=["ex-print-letter-0410"],
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
                    "Decode print pairs",
                    "Name the letter, then match its uppercase and lowercase print forms. This unit does not introduce words.",
                    referenceIds=["ref-decoding"],
                    exampleIds=["ex-print-letter-0410", "ex-print-letter-041c"],
                    bookDirection="Use the elementary reading work in your Lesson 1 pages for anything beyond a single letter.",
                ),
                step(
                    "u1-st-decoding-check",
                    "CHECK",
                    "One letter, two cases",
                    "Choose the line that is a single letter.",
                    itemIds=["u1-i-decode-pair"],
                    exampleIds=["ex-print-letter-0410"],
                    remediationStepId="u1-st-decoding",
                ),
            ],
            8,
        ),
        session(
            "u1-s-pronunciation",
            "Pronunciation support",
            ["obj-u1-pronunciation"],
            [
                step(
                    "u1-st-pronunciation",
                    "TEACH",
                    "Confirm sounds in your book",
                    "This unit does not play model audio. Use the pronunciation notes in your book for each new letter. A recording you make is stored and is not scored.",
                    referenceIds=["ref-pronunciation"],
                    bookDirection="Read the Lesson 1 pronunciation notes in the edition you own.",
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
                    "Russian word stress is not predictable from spelling alone. Marked example words are not installed.",
                    referenceIds=["ref-stress"],
                    bookDirection="When your Lesson 1 pages mark stress, copy that mark into your binder.",
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
                    "Complete the Lesson 1 reading and pronunciation work in the edition you own.",
                    bookDirection="Read and do Lesson 1 in your book. Use the book's answer key, not this app, for book exercises.",
                    companionDirection="Afterward, mark book work complete as a self-attestation. That is not demonstrated in-app ability.",
                ),
            ],
            25,
        ),
        session(
            "u1-s-companions",
            "AnkiDroid, binder, and tutor",
            ["obj-u1-book"],
            [
                step(
                    "u1-st-anki",
                    "ANKI",
                    "AnkiDroid if you use it",
                    "If you selected AnkiDroid, make cards only from material you are allowed to use.",
                    companionDirection="Self-attest AnkiDroid work. It remains user-reported.",
                ),
                step(
                    "u1-st-binder",
                    "BINDER",
                    "One useful binder note",
                    "Write one confusable letter pair that tricked you and the identity you will trust next time.",
                    companionDirection="A binder prompt is a specific note, not 'take notes.'",
                ),
                step(
                    "u1-st-tutor",
                    "TUTOR",
                    "Optional tutor",
                    "Tutor work is optional for default advancement.",
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
                    "Letter identities",
                    "Revisit any missed letter, then open the explanation it names.",
                    itemIds=["u1-i-а-identity", "u1-i-р-not-p", "u1-i-е-identity", "u1-i-ь-not-vowel", "u1-i-print-pair", "u1-i-decode-pair"],
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
            ["obj-u2-review"],
            [
                step(
                    "u2-st-orient",
                    "ORIENT",
                    "Unit 2 — Book handwriting and letter review",
                    "Proposed Lesson 2 continues handwritten forms and pronunciation in the book. This unit reviews printed letters from Unit 1. Verified handwriting images and mapped pronunciation audio are not in the app, so those parts stay in the book you own.",
                    bookDirection="Open Lesson 2 in your copy for handwritten forms and the author's pronunciation notes.",
                ),
            ],
            6,
        ),
        session(
            "u2-s-handwriting",
            "Handwritten forms",
            ["obj-u2-book"],
            [
                step(
                    "u2-st-hw",
                    "BOOK",
                    "Handwritten forms stay in the owned book",
                    "Verified handwriting images are not in this app. Use Lesson 2 in the book you own to see handwritten forms, and copy them onto paper. This app does not describe those forms and does not grade the page.",
                    bookDirection="Copy the handwritten forms from Lesson 2 in your book onto paper. This app does not show or grade them.",
                    referenceIds=["ref-limits"],
                ),
            ],
            15,
        ),
        session(
            "u2-s-review",
            "Unit 1 letter review",
            ["obj-u2-review"],
            [
                step(
                    "u2-st-review",
                    "TEACH",
                    "Keep letter identities available",
                    "Review the printed letters from Unit 1, especially Latin look-alikes and print pairs.",
                    referenceIds=["ref-cyrillic-print", "ref-confusable"],
                    exampleIds=["ex-letter-0412", "ex-letter-0423", "ex-letter-0425", "ex-print-letter-0411"],
                ),
                step(
                    "u2-st-review-guided",
                    "GUIDED",
                    "Match the printed pair for Б",
                    "Choose the uppercase and lowercase print pair for the same letter.",
                    itemIds=["u2-i-print-б"],
                    exampleIds=["ex-print-letter-0411"],
                    remediationStepId="u2-st-review",
                ),
                step(
                    "u2-st-review-v",
                    "CHECK",
                    "В is not B",
                    "Identity check.",
                    itemIds=["u2-i-в-not-b"],
                    exampleIds=["ex-letter-0412"],
                    remediationStepId="u2-st-review",
                ),
                step(
                    "u2-st-review-u",
                    "CHECK",
                    "У is not Y",
                    "Identity check.",
                    itemIds=["u2-i-у-not-y"],
                    exampleIds=["ex-letter-0423"],
                    remediationStepId="u2-st-review",
                ),
                step(
                    "u2-st-review-h",
                    "CHECK",
                    "Х is not X",
                    "Identity check.",
                    itemIds=["u2-i-х-not-x"],
                    exampleIds=["ex-letter-0425"],
                    remediationStepId="u2-st-review",
                ),
            ],
            10,
        ),
        session(
            "u2-s-pronunciation",
            "Additional pronunciation",
            ["obj-u2-book"],
            [
                step(
                    "u2-st-pron",
                    "BOOK",
                    "Additional pronunciation stays in the owned book",
                    "No licensed pronunciation audio is mapped to Unit 2. Use the Lesson 2 pronunciation notes in the book you own. This app does not state those sounds and does not score a recording.",
                    bookDirection="Use the Lesson 2 pronunciation notes in your copy. Model audio is not in this app.",
                    referenceIds=["ref-pronunciation"],
                ),
            ],
            10,
        ),
        session(
            "u2-s-reading",
            "Reading in the book",
            ["obj-u2-book"],
            [
                step(
                    "u2-st-reading",
                    "TEACH",
                    "Read what the book provides",
                    "Fluency practice in this unit is book-directed. Re-run letter recognition if a form is still slow.",
                    bookDirection="Read the Lesson 2 material in your copy aloud. Mark difficult letters for review.",
                    referenceIds=["ref-decoding"],
                ),
            ],
            12,
        ),
        session(
            "u2-s-record",
            "Self-record",
            ["obj-u2-book"],
            [
                step(
                    "u2-st-record",
                    "SPEAK",
                    "Optional recording without a score",
                    "You may record yourself reading from your book. Play the take back. The app never transcribes or scores it.",
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
            "Unit 2 letter check",
            ["obj-u2-review"],
            [
                step(
                    "u2-st-check",
                    "CHECK",
                    "Reviewed letter identities",
                    "Closed-response items about printed letters from Unit 1.",
                    itemIds=["u2-i-в-not-b", "u2-i-print-б", "u2-i-я-identity"],
                    remediationStepId="u2-st-review",
                ),
            ],
            8,
        ),
    ]

    objectives = [
        objective(
            "obj-u1-letter-identity",
            1,
            "Identify staged printed letters",
            "RECOGNITION",
            ["u1-st-vowels", "u1-st-cons", "u1-st-system"],
            example_ids_by_objective.get("obj-u1-letter-identity", []),
            ["u1-st-vowels-check", "u1-st-cons-check"],
            ["u1-i-а-identity", "u1-i-м-identity"],
            ["u1-st-vowels", "u1-st-cons"],
        ),
        objective(
            "obj-u1-confusable",
            1,
            "Separate Cyrillic letters from Latin look-alikes",
            "RECOGNITION",
            ["u1-st-confusable"],
            example_ids_by_objective.get("obj-u1-confusable", []),
            ["u1-st-confusable-r", "u1-st-confusable-n", "u1-st-confusable-s", "u1-st-confusable-rest"],
            ["u1-i-р-not-p", "u1-i-н-not-h", "u1-i-с-not-c", "u1-i-в-not-b", "u1-i-у-not-y", "u1-i-х-not-x"],
            ["u1-st-confusable"],
        ),
        objective(
            "obj-u1-iotated",
            1,
            "Recognize Е Ё Ю Я as a group",
            "RECOGNITION",
            ["u1-st-iotated"],
            example_ids_by_objective.get("obj-u1-iotated", []),
            ["u1-st-iotated-check"],
            ["u1-i-е-identity", "u1-i-ё-identity", "u1-i-ю-identity", "u1-i-я-identity"],
            ["u1-st-iotated"],
        ),
        objective(
            "obj-u1-signs",
            1,
            "Treat Ь Ъ Й as non-ordinary-vowel letters",
            "RECOGNITION",
            ["u1-st-signs"],
            example_ids_by_objective.get("obj-u1-signs", []),
            ["u1-st-signs-check", "u1-st-hard-check", "u1-st-short-i-check"],
            ["u1-i-ь-not-vowel", "u1-i-ъ-not-vowel", "u1-i-й-not-vowel"],
            ["u1-st-signs"],
        ),
        objective(
            "obj-u1-print-forms",
            1,
            "Match uppercase and lowercase print pairs",
            "RECOGNITION",
            ["u1-st-print"],
            example_ids_by_objective.get("obj-u1-print-forms", []),
            ["u1-st-print-check"],
            ["u1-i-print-pair"],
            ["u1-st-print"],
        ),
        objective(
            "obj-u1-decoding",
            1,
            "Decode a printed letter pair",
            "RECOGNITION",
            ["u1-st-decoding"],
            ["ex-print-letter-0410", "ex-print-letter-041c"],
            ["u1-st-decoding-check"],
            ["u1-i-decode-pair"],
            ["u1-st-decoding"],
        ),
        objective(
            "obj-u1-pronunciation",
            1,
            "Confirm letter sounds in the book",
            "GUIDANCE",
            ["u1-st-pronunciation"],
            [],
            [],
            [],
            [],
        ),
        objective(
            "obj-u1-stress",
            1,
            "Treat stress as book-marked, not guessed",
            "GUIDANCE",
            ["u1-st-stress"],
            [],
            [],
            [],
            [],
        ),
        objective(
            "obj-u1-book",
            1,
            "Complete Lesson 1 work in the owned book",
            "SELF_ATTESTATION",
            ["u1-st-book"],
            [],
            [],
            [],
            [],
        ),
        objective(
            "obj-u2-review",
            2,
            "Review Unit 1 printed letters",
            "RECOGNITION",
            ["u2-st-review"],
            example_ids_by_objective.get("obj-u2-review", []) + ["ex-letter-0412", "ex-letter-0423", "ex-letter-0425"],
            ["u2-st-review-guided"],
            ["u2-i-в-not-b", "u2-i-у-not-y", "u2-i-х-not-x", "u2-i-print-б", "u2-i-я-identity"],
            ["u2-st-review"],
        ),
        objective(
            "obj-u2-book",
            2,
            "Complete Lesson 2 work in the owned book",
            "SELF_ATTESTATION",
            ["u2-st-book"],
            [],
            [],
            [],
            [],
        ),
    ]

    references = [
        reference(
            "ref-cyrillic-print",
            "Cyrillic print forms",
            "33 printed uppercase/lowercase pairs.",
            "Use the letter board grouped by the Unit 1 sequence. English sound notes are pending review. No example words are attached.",
            1,
            "LINGUISTIC_INSTRUCTION",
        ),
        reference(
            "ref-confusable",
            "Confusable Latin look-alikes",
            "В Н Р С У Х can be misread by Latin-alphabet readers.",
            "Name the Cyrillic identity. Confirm sounds in your book.",
            1,
            "LINGUISTIC_INSTRUCTION",
        ),
        reference(
            "ref-iotated",
            "Iotated vowel letters",
            "Е Ё Ю Я are a recognition group in Unit 1.",
            "Detailed phonetics remain in the book until a reviewer signs an app note.",
            1,
            "LINGUISTIC_INSTRUCTION",
        ),
        reference(
            "ref-signs",
            "Soft sign, hard sign, and Й",
            "Ь and Ъ are signs. Й is not a standalone vowel in this foundation.",
            "Do not invent a spoken vowel for the signs. Use the book for the exact rule.",
            1,
            "LINGUISTIC_INSTRUCTION",
        ),
        reference(
            "ref-transliteration",
            "Transliteration is temporary",
            "Latin respelling may help the first hours. It is not how you should keep reading.",
            "Turn transliteration off in settings as soon as print recognition is stable.",
            1,
            "LINGUISTIC_INSTRUCTION",
        ),
        reference(
            "ref-decoding",
            "Staged decoding limits",
            "This pack decodes letters and print pairs only.",
            "Word, phrase, and sign reading wait for reviewed examples.",
            1,
            "LINGUISTIC_INSTRUCTION",
        ),
        reference(
            "ref-stress",
            "Stress notation",
            "Stress is part of Russian reading and is not guessed by the app.",
            "Copy stress marks from your book into your binder.",
            1,
            "LINGUISTIC_INSTRUCTION",
        ),
        reference(
            "ref-pronunciation",
            "Pronunciation guidance",
            "Model audio is used only when a file is licensed and mapped to the current objective.",
            "User recordings play at original speed. No ASR or score.",
            2,
            "LINGUISTIC_INSTRUCTION",
        ),
        reference(
            "ref-study",
            "How to use this companion",
            "The app coordinates the book, optional AnkiDroid, a binder, and optional tutor work.",
            "It does not reproduce Penguin, judge spontaneous speech, or certify proficiency.",
            1,
            "PRODUCT_HELP",
            "NOT_REQUIRED",
        ),
        reference(
            "ref-limits",
            "App limitations",
            "Offline only. No internet, no account, no AI, no cloud.",
            "Unrelated catalog audio is not assigned to Units 1–2. The app does not score handwriting or pronunciation. Handwriting study uses your book until verified images exist. Unit 2 reviews Unit 1 letters and does not teach Lesson 3 grammar. Public release stays blocked until review and device evidence exist.",
            1,
            "OPERATIONAL_GUIDANCE",
            "NOT_REQUIRED",
        ),
        reference(
            "ref-book-topics",
            "Where lesson bodies are packaged",
            "Some units contain proposed lesson bodies that still require qualified review.",
            "A unit's Course status shows whether its lesson body is prepared. Units marked Not prepared or Not yet installed do not contain a usable lesson body. Open the matching lesson in the copy you own. This reference does not approve the packaged records.",
            3,
            "OPERATIONAL_GUIDANCE",
            "NOT_REQUIRED",
        ),
    ]

    def development_unit(
        number: int,
        title: str,
        short: str,
        summary: str,
        sessions: list,
        prereq: list[int],
        install_state: str = "DEVELOPMENT",
        readiness: str = "NOT_PREPARED",
        mapping_note: str = "Topic-level alignment uses the publisher's public table of contents. Edition comparison is not signed. This unit is incomplete development content, not a production-depth installed unit.",
        require_due_review: bool | None = None,
        minimum_checks: int = 2,
    ) -> dict:
        due_review = number > 1 if require_due_review is None else require_due_review
        return {
            "number": number,
            "id": f"unit-{number}",
            "title": title,
            "shortLabel": short,
            "topicSummary": summary,
            "installState": install_state,
            "readiness": readiness,
            "penguinLesson": number,
            "mappingStatus": "PENDING_CURRICULUM_REVIEWER",
            "mappingNote": mapping_note,
            "prerequisiteUnitNumbers": prereq,
            "estimatedMinutes": sum(s["estimatedMinutes"] for s in sessions),
            "objectives": [o for o in objectives if o["unitNumber"] == number],
            "sessions": sessions,
            "advancement": {
                "requireInstruction": True,
                "requireBookSelfAttestation": True,
                "requireAppChecks": True,
                "minimumDistinctChecks": minimum_checks,
                "requireDueReview": due_review,
                "tutorOptional": True,
            },
        }

    proposed = build_units_3_5()
    later = build_units_6_10()
    following = build_units_11_15()
    intake = build_units_16_20()
    later_intake = build_units_21_25()
    late_intake = build_units_26_30()
    items.extend(proposed["items"])
    items.extend(later["items"])
    items.extend(following["items"])
    items.extend(intake["items"])
    items.extend(later_intake["items"])
    items.extend(late_intake["items"])
    examples.extend(proposed["examples"])
    examples.extend(later["examples"])
    examples.extend(following["examples"])
    examples.extend(intake["examples"])
    examples.extend(later_intake["examples"])
    examples.extend(late_intake["examples"])
    for proposed_unit in proposed["units"]:
        objectives.extend(proposed_unit["objectives"])
    for proposed_unit in later["units"]:
        objectives.extend(proposed_unit["objectives"])
    for proposed_unit in following["units"]:
        objectives.extend(proposed_unit["objectives"])
    for proposed_unit in intake["units"]:
        objectives.extend(proposed_unit["objectives"])
    for proposed_unit in later_intake["units"]:
        objectives.extend(proposed_unit["objectives"])
    for proposed_unit in late_intake["units"]:
        objectives.extend(proposed_unit["objectives"])
    references.extend(proposed["references"])
    references.extend(later["references"])
    references.extend(following["references"])
    references.extend(intake["references"])
    references.extend(later_intake["references"])
    references.extend(late_intake["references"])

    units = [
        development_unit(
            1,
            "Learning to read Russian",
            "Printed letters",
            "Content-complete development unit for printed letters: identities, print pairs, look-alikes, signs, and letter-level decoding. Qualified review and the owned-edition mapping are still pending.",
            unit1_sessions,
            [],
            readiness="CONTENT_COMPLETE_REVIEW_PENDING",
            mapping_note="Proposed Lesson 1 mapping from the publisher's public table of contents. Owned-edition comparison is not signed, so this unit is not labeled aligned.",
        ),
        development_unit(
            2,
            "Handwriting and additional pronunciation",
            "Letter review",
            "Content-complete development unit after Unit 1: printed-letter review, then book-directed handwriting and additional pronunciation. Verified handwriting images and mapped pronunciation audio are not in this app. Qualified review and the owned-edition mapping are still pending.",
            unit2_sessions,
            [1],
            readiness="CONTENT_COMPLETE_REVIEW_PENDING",
            mapping_note="Proposed Lesson 2 mapping from the publisher's public table of contents: handwriting recognition and additional pronunciation. Owned-edition comparison is not signed, so this unit is not labeled aligned.",
        ),
        development_unit(
            3,
            "Everyday phrases, naming things, basic grammar, and gender",
            "Everyday phrases",
            "Proposed Unit 3 is a development unit awaiting qualified review. It contains admitted records for gender, everyday phrases, and naming, plus separate book self-attestation. It is not installed and the owned-edition mapping is not signed.",
            proposed["units"][0]["sessions"],
            [2],
            readiness="CONTENT_COMPLETE_REVIEW_PENDING",
            mapping_note="Proposed Lesson 3 mapping remains pending. The structural edition facts are unchanged. This proposed curriculum is not an approved alignment.",
            require_due_review=False,
            minimum_checks=proposed["units"][0]["minimum_checks"],
        ),
        development_unit(
            4,
            "Verbs, personal pronouns, present tense, conjugation, word stress, and introduction to nouns and cases",
            "Verbs and cases",
            "Proposed Unit 4 is a development unit awaiting qualified review. It contains admitted pronoun, present-tense, written-stress, and nominative records, plus separate book self-attestation. Written stress is not a pronunciation score. It is not installed and the owned-edition mapping is not signed.",
            proposed["units"][1]["sessions"],
            [3],
            readiness="CONTENT_COMPLETE_REVIEW_PENDING",
            mapping_note="Proposed Lesson 4 mapping remains pending. The structural edition facts are unchanged. This proposed curriculum is not an approved alignment.",
            require_due_review=False,
            minimum_checks=proposed["units"][1]["minimum_checks"],
        ),
        development_unit(
            5,
            "Questions, prepositional case, and indeclinable nouns",
            "Questions",
            "Proposed Unit 5 is a development unit awaiting qualified review. It contains admitted question, prepositional, and indeclinable records, plus separate book self-attestation. Question intonation is not taught. It is not installed and the owned-edition mapping is not signed.",
            proposed["units"][2]["sessions"],
            [4],
            readiness="CONTENT_COMPLETE_REVIEW_PENDING",
            mapping_note="Proposed Lesson 5 mapping remains pending. The structural edition facts are unchanged. This proposed curriculum is not an approved alignment.",
            require_due_review=False,
            minimum_checks=proposed["units"][2]["minimum_checks"],
        ),
    ]
    later_by_number = {unit["number"]: unit for unit in later["units"]}
    for number, short, title in UNITS_3_30:
        if number < 6 or number > 10:
            continue
        proposed_unit = later_by_number[number]
        summary = (
            "Proposed Unit 7 is in development and is not prepared. Admitted agreement rows, three adverb form/gloss pairs, the admitted invariance line, and the two admitted suffixal pairs await qualified review. An adjective-to-adverb paradigm has no admitted frozen span. It is not installed and the owned-edition mapping is not signed."
            if number == 7
            else (
                f"Proposed Unit {number} is a development unit awaiting qualified review. It contains admitted records for this lesson's governed objectives, plus separate book self-attestation. It is not installed and the owned-edition mapping is not signed."
            )
        )
        units.append(
            development_unit(
                number,
                title,
                short,
                summary,
                proposed_unit["sessions"],
                [number - 1],
                readiness="NOT_PREPARED" if number == 7 else "CONTENT_COMPLETE_REVIEW_PENDING",
                mapping_note=(
                    "Proposed lesson mapping remains pending. The structural edition facts are recorded separately. This proposed curriculum is not an approved alignment."
                ),
                require_due_review=False,
                minimum_checks=proposed_unit["minimum_checks"],
            )
        )
    following_by_number = {unit["number"]: unit for unit in following["units"]}
    following_pages = {
        11: ("122-132", "Past-tense formation and use, including reflexive verbs"),
        12: ("133-148", "Future formation, introductory verbal aspect, the dative case, additional spelling rules, and prepositional plural"),
        13: ("149-161", "Aspect in past-tense contexts, tense selection, infinitive aspect, and reported speech"),
        14: ("162-170", "Aspect in future-tense contexts and impersonal constructions"),
        15: ("171-187", "Requests, imperative forms, and consolidation of aspect use"),
    }
    for number, short, title in UNITS_3_30:
        if number < 11 or number > 15:
            continue
        proposed_unit = following_by_number[number]
        pages, topic = following_pages[number]
        units.append(
            development_unit(
                number,
                title,
                short,
                f"Proposed Unit {number} is a development unit awaiting qualified review. It contains admitted records for this lesson's governed objectives, plus separate book self-attestation. It is not installed and the owned-edition mapping is not signed.",
                proposed_unit["sessions"],
                [number - 1],
                readiness="CONTENT_COMPLETE_REVIEW_PENDING",
                mapping_note=(
                    f"Proposed Lesson {number}, printed pages {pages}. Independently stated topic scope: {topic}. "
                    "The Product Owner asserts that the inspected file is a legally owned copy. That assertion is not reviewer verification. "
                    "Mapping status is PENDING_CURRICULUM_REVIEWER. This note does not approve coverage, variance, or alignment."
                ),
                require_due_review=False,
                minimum_checks=proposed_unit["minimum_checks"],
            )
        )
    intake_by_number = {unit["number"]: unit for unit in intake["units"]}
    intake_pages = {
        16: ("188-200", "Instrumental case: principal uses; singular and plural noun forms; adjective agreement; common instrumental-governing constructions; instrumental complements with identity, role, or becoming"),
        17: ("201-214", "Time, weekdays, calendar dates, years, age expressions, and ordinal numbers"),
        18: ("215-229", "Comparative forms, superlative expression, and relative clauses built with the inflected relative pronoun"),
        19: ("230-239", "Conditional constructions; obligation or probability; productive meanings of selected verbal prefixes"),
        20: ("240-256", "Unidirectional and multidirectional verbs of motion; motion by foot or transport; carrying/leading motion pairs; selected prefixed motion verbs; cumulative review through Lesson 20"),
    }
    intake_summaries = {
        16: "Proposed Unit 16 is in development. Admitted instrumental rows and the admitted complement span await qualified review. It is not installed and the owned-edition mapping is not signed.",
        17: "Proposed Unit 17 is in development and is not prepared. Admitted number rows, age, weekday names, month names, the clock question, three bounded clock phrases, the stated calendar-date constructions, and the admitted quarter-hour lines await qualified review. Productive clock-time answers have no admitted frozen span. It is not installed and the owned-edition mapping is not signed.",
        18: "Proposed Unit 18 is in development. Admitted comparison lines and admitted relative lines await qualified review. It is not installed and the owned-edition mapping is not signed.",
        19: "Proposed Unit 19 is in development. Admitted prefix cautions, conditional lines, obligation lines, and the admitted likelihood example await qualified review. It is not installed and the owned-edition mapping is not signed.",
        20: "Proposed Unit 20 is in development. Admitted motion lines for direction, foot and transport, carrying and leading, setting out, and selected prefixed paths await qualified review. It is not installed and the owned-edition mapping is not signed.",
    }
    intake_readiness = {
        16: "CONTENT_COMPLETE_REVIEW_PENDING",
        17: "NOT_PREPARED",
        18: "CONTENT_COMPLETE_REVIEW_PENDING",
        19: "CONTENT_COMPLETE_REVIEW_PENDING",
        20: "CONTENT_COMPLETE_REVIEW_PENDING",
    }
    for number, short, title in UNITS_3_30:
        if number < 16 or number > 20:
            continue
        proposed_unit = intake_by_number[number]
        pages, topic = intake_pages[number]
        units.append(
            development_unit(
                number,
                title,
                short,
                intake_summaries[number],
                proposed_unit["sessions"],
                [number - 1],
                readiness=intake_readiness[number],
                mapping_note=(
                    f"Proposed Lesson {number}, printed pages {pages}. Independently stated topic scope: {topic}. "
                    "The Product Owner asserts that the inspected file is a legally owned copy. That assertion is not reviewer verification. "
                    "Mapping status is PENDING_CURRICULUM_REVIEWER. This note does not approve coverage, variance, or alignment."
                ),
                require_due_review=False,
                minimum_checks=max(proposed_unit["minimum_checks"], 1),
            )
        )
    next_intake_by_number = {unit["number"]: unit for unit in later_intake["units"]}
    next_intake_pages = {
        21: ("259-270", "Possession with the reflexive possessive pronoun; reflexive reference; and purpose clauses"),
        22: ("271-284", "Numeral behavior beyond basic counting, including declined numerals, forms for both and one and a half, compound quantities, and collective or quantity expressions"),
        23: ("285-295", "Expressions of elapsed time, duration, ago, during, and distinctions involving completion over a period"),
        24: ("296-306", "Negation, case behavior under negation, negative pronouns or particles, and the position or scope of не"),
        25: ("307-321", "Diminutives, Russian personal-name forms, social address, requests, apologies, and polite expressions"),
    }
    next_intake_summaries = {
        21: "Proposed Unit 21 is in development. Admitted object-possession lines, the frozen reflexive contrast, and the frozen purpose gloss await qualified review. It is not installed and the owned-edition mapping is not signed.",
        22: "Proposed Unit 22 is in development. Admitted numeral lines, the case table, the half-form usage note, and the collective column await qualified review. A numbered list is not taught as an ordinal distinction. It is not installed and the owned-edition mapping is not signed.",
        23: "Proposed Unit 23 is in development. Admitted Diverse Russian time-expression spans await qualified review. Book self-attestation does not replace the app checks. It is not installed and the owned-edition mapping is not signed.",
        24: "Proposed Unit 24 is in development and is not prepared. Admitted negation lines, the book compare, and the prefix table await qualified review. Placement contrasts have no admitted frozen span. It is not installed and the owned-edition mapping is not signed.",
        25: "Proposed Unit 25 is in development and is not prepared. Admitted name lines, listed diminutive pairs, courtesy lines, and the address-register sentences await qualified review. A general nickname-formation rule has no admitted frozen span. It is not installed and the owned-edition mapping is not signed.",
    }
    for number, short, title in UNITS_3_30:
        if number < 21 or number > 25:
            continue
        proposed_unit = next_intake_by_number[number]
        pages, topic = next_intake_pages[number]
        units.append(
            development_unit(
                number,
                title,
                short,
                next_intake_summaries[number],
                proposed_unit["sessions"],
                [number - 1],
                readiness="CONTENT_COMPLETE_REVIEW_PENDING" if number in (21, 22, 23) else "NOT_PREPARED",
                mapping_note=(
                    f"Proposed Lesson {number}, printed pages {pages}. Independently stated topic scope: {topic}. "
                    "The Product Owner asserts that the inspected file is a legally owned copy. That assertion is not reviewer verification. "
                    "Mapping status is PENDING_CURRICULUM_REVIEWER. This note does not approve coverage, variance, or alignment."
                ),
                require_due_review=False,
                minimum_checks=max(proposed_unit["minimum_checks"], 1),
            )
        )
    late_scope = {
        26: "Stock expressions; indefinite pronouns and adverbs; word order; postal addresses; letter writing; vocabulary and correspondence work",
        27: "Passive participles: overview, past passive formation and use, optional present passive material, vocabulary and comprehension",
        28: "Imperfective and perfective verbal adverbs, formation and use, vocabulary and readings",
        29: "Present and past active participles, participles as adjectives, optional punctuation, long and short adjectives, and bookish reading",
        30: "Course orientation, dictionary use, abbreviations, review and application of letter names, particles, vocabulary and a final reading",
    }
    for number, short, title in UNITS_3_30:
        if number <= 25:
            continue
        topic = late_scope[number]
        variance_note = (
            "An unresolved authority variance remains unactivated. "
            if number in (27, 29, 30)
            else ""
        )
        proposed_unit = next(item for item in late_intake["units"] if item["number"] == number)
        units.append(
            development_unit(
                number,
                title,
                short,
                (
                    f"Proposed Unit {number} is in development and is not prepared. "
                    "Admitted frozen spans await qualified review. Unsupported topics stay book-directed. "
                    "It is not installed and the owned-edition mapping is not signed."
                ),
                proposed_unit["sessions"],
                [number - 1],
                readiness="NOT_PREPARED",
                mapping_note=(
                    f"Proposed Lesson {number}. Observed structural scope: {topic}. "
                    "Admitted frozen spans are pending qualified review and do not approve alignment. "
                    f"{variance_note}"
                    "Mapping status is PENDING_CURRICULUM_REVIEWER. This note does not approve coverage, variance, or alignment."
                ),
                require_due_review=False,
                minimum_checks=max(proposed_unit["minimum_checks"], 1),
            )
        )

    pack = {
        "schemaVersion": 1,
        "packId": "v2-curriculum-foundation",
        "packVersion": 3,
        "publicReleaseEligible": False,
        "linguisticReview": REVIEW_PENDING,
        "unitReviews": [pending_unit_review(number) for number in range(1, 31)],
        "sources": [SOURCE_LETTERS, SOURCE_ENGLISH, *proposed["sources"], *late_intake["sources"], *later["sources"], *intake["sources"], *later_intake["sources"]],
        "rights": [*RIGHTS, *proposed["rights"], *late_intake["rights"], *later["rights"], *intake["rights"], *later_intake["rights"]],
        "letters": letters,
        "examples": examples,
        "items": items,
        "references": references,
        "units": units,
        "audioAssets": [],
        "handwritingAssets": [],
        "ownedEditionMappings": [],
        "provenanceRecords": [*proposed["provenance"], *later["provenance"], *following["provenance"], *intake["provenance"], *later_intake["provenance"], *late_intake["provenance"]],
        "authoredEnglishRecords": [*proposed["authored"], *later["authored"], *following["authored"], *intake["authored"], *later_intake["authored"], *late_intake["authored"]],
        "sourceCapabilities": [*later["capabilities"], *intake["capabilities"], *later_intake["capabilities"], *late_intake["capabilities"]],
        "admissions": {
            "generatedRussianWords": 0,
            "penguinProseAssets": 0,
            "catalogAudioBoundToUnits": 0,
            "unverifiedExampleWords": 0,
            "futureRussianItems": 0,
            "handwritingAssets": 0,
            "mappedPronunciationAudio": 0,
        },
    }
    return pack


def main() -> None:
    pack = build()
    OUT.parent.mkdir(parents=True, exist_ok=True)
    OUT.write_text(json.dumps(pack, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    print(
        f"wrote {OUT} units={len(pack['units'])} letters={len(pack['letters'])} "
        f"items={len(pack['items'])} examples={len(pack['examples'])}"
    )


if __name__ == "__main__":
    main()
