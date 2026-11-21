#!/usr/bin/env python3
"""T14-a: Units 1-2 corrections (A2-23, A2-24, A2-26) and the owner's Unit 2 pairs.

Exact-baseline transformer. It refuses any input other than the recorded
baseline asset, so it cannot be re-run against a later file.

Russian text added here comes only from:
- Unicode Cyrillic letter identities (public-domain writing-system facts), and
- the frozen OpenRussian snapshot already in the source inventory
  (catalog/sources/units-3-5/openrussian/nouns.csv, commit 50e210c4, CC BY-SA 4.0).
No Russian is generated. English text is independently authored.
"""
from __future__ import annotations

import csv
import hashlib
import json
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / "catalog"))
from curriculum_storage import loads, encode  # noqa: E402

ASSET = ROOT / "app/src/main/assets/content/v2-curriculum.json"
BASELINE_SHA256 = "8f1277ba47d73fa75424ee1b25ac8feb1469846b955392d44425ffeba20b9ef1"
FREEZE = ROOT / "catalog/sources/units-3-5/FREEZE.json"
NOUNS = ROOT / "catalog/sources/units-3-5/openrussian/nouns.csv"

PENDING = "IMPLEMENTED_PENDING_FINAL_AUDIT"
OR_SOURCE = "src-openrussian-50e210c4"
OR_REVISION = "50e210c4803237779cb562bc1abcea529066031c"
OR_URL = f"https://github.com/Badestrand/russian-dictionary/blob/{OR_REVISION}/nouns.csv"
OR_ATTRIBUTION = f"Badestrand/russian-dictionary contributors, commit {OR_REVISION}, CC BY-SA 4.0."
SHARE_ALIKE = (
    "Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. "
    "Application code, user data, and user recordings are outside that statement. "
    "No audio is relicensed because this unit ships no audio."
)
STRESS = "́"


def sha(text: str) -> str:
    return hashlib.sha256(text.encode("utf-8")).hexdigest()


# --- Owner addition (2026-11-11): Unit 2 stress pairs and final-devoicing pairs -------------
# (accented source field, English sense selected from translations_en)
PAIR_ROWS = [
    ("за'мок", "castle"),
    ("замо'к", "lock"),
    ("му'ка", "torment"),
    ("мука'", "flour"),
    ("луг", "meadow"),
    ("лук", "onion"),
    ("код", "code"),
    ("кот", "cat"),
    ("плод", "fruit"),
    ("плот", "raft"),
]


def shown(accented: str) -> str:
    return accented.replace("'", STRESS)


def frozen_rows() -> dict[str, dict]:
    freeze = json.loads(FREEZE.read_text(encoding="utf-8"))
    expected = freeze["openrussian"]["files"]["nouns.csv"]["sha256"]
    raw = NOUNS.read_bytes()
    if hashlib.sha256(raw).hexdigest() != expected:
        raise SystemExit("Frozen OpenRussian nouns.csv does not match FREEZE.json")
    rows = {}
    for row in csv.DictReader(raw.decode("utf-8").splitlines(), delimiter="\t", quoting=csv.QUOTE_NONE):
        if any(row["accented"] == a for a, _ in PAIR_ROWS):
            if row["accented"] in rows:
                raise SystemExit(f"Ambiguous frozen row {row['accented']}")
            rows[row["accented"]] = row
    missing = [a for a, _ in PAIR_ROWS if a not in rows]
    if missing:
        raise SystemExit(f"Frozen rows missing: {missing}")
    return rows


def provenance(unit: int, objective: str, item_id: str, record_id: str, original: str, shipped: str,
               transformation: str) -> dict:
    return {
        "itemId": item_id,
        "unitNumber": unit,
        "objectiveId": objective,
        "recordType": "lexical record",
        "sourceId": OR_SOURCE,
        "sourceUrl": OR_URL,
        "sourceRevision": OR_REVISION,
        "sourceRecordId": record_id,
        "originalSourceText": original,
        "shippedText": shipped,
        "transformation": transformation,
        "licenseId": "CC BY-SA 4.0",
        "licenseUrl": "https://creativecommons.org/licenses/by-sa/4.0/",
        "attribution": OR_ATTRIBUTION,
        "shareAlikeTreatment": SHARE_ALIKE,
        "sourceHash": sha(original),
        "contentHash": sha(shipped),
        "reviewStatus": PENDING,
    }


def pair_content(rows: dict[str, dict]) -> tuple[list[dict], list[dict]]:
    examples, records = [], []
    for accented, sense in PAIR_ROWS:
        row = rows[accented]
        word = shown(accented)
        bare = row["bare"]
        key = word.replace(STRESS, "") + ("-" + str(accented.index("'")) if "'" in accented else "")
        ex_id = f"ex-u2-pair-{key}"
        record_id = f"nouns.csv:{bare}#accented"
        records.append(provenance(2, "obj-u2-pairs", f"prov-u2-noun-{key}", record_id, accented, word,
                                  "formatting" if accented != word else "selection"))
        gloss_field = row["translations_en"]
        senses = [s.strip() for s in gloss_field.replace(";", ",").split(",") if s.strip()]
        if sense not in senses:
            raise SystemExit(f"{accented}: sense {sense} not in {gloss_field}")
        records.append(provenance(2, "obj-u2-pairs", f"prov-u2-gloss-{key}", f"nouns.csv:{bare}#translations_en",
                                  gloss_field, sense, "sense selection" if gloss_field != sense else "selection"))
        examples.append({
            "id": ex_id,
            "kind": "LEXICAL",
            "letterId": "",
            "display": word,
            "note": sense,
            "sourceId": OR_SOURCE,
            "reviewStatus": PENDING,
            "objectiveId": "obj-u2-pairs",
        })
    return examples, records


# --- Unicode letter facts for the added print-pair checks -----------------------------------
# letter -> confusable partner used only as a distractor
PRINT_REVIEW_U1 = [("О", "Э"), ("Т", "Г")]
PRINT_REVIEW_U2 = [
    ("Г", "Т"), ("Д", "Л"), ("Ж", "К"), ("З", "Э"), ("И", "Н"),
    ("К", "Ж"), ("Л", "П"), ("П", "Л"), ("Ф", "Р"), ("Ц", "Щ"),
    ("Ч", "Ц"), ("Ш", "Щ"), ("Щ", "Ш"), ("Ы", "Ь"), ("Э", "З"),
]


def print_item(item_id: str, objective: str, upper: str, partner: str, rotation: int) -> dict:
    lower, partner_lower = upper.lower(), partner.lower()
    right = f"{upper} / {lower}"
    wrong_a = f"{upper} / {partner_lower}"
    wrong_b = f"{partner} / {lower}"
    rationales = {
        right: "",
        wrong_a: f"{upper} pairs with {lower}, not {partner_lower}. The lowercase {partner_lower} is the partner of {partner}.",
        wrong_b: f"{partner} pairs with {partner_lower}, not {lower}. The lowercase {lower} is the partner of {upper}.",
    }
    base = [right, wrong_a, wrong_b]
    options = base[-rotation % 3:] + base[:-rotation % 3]
    return {
        "id": item_id,
        "kind": "PRINT_PAIR",
        "language": "en",
        "prompt": "Which pair is the same letter in uppercase and lowercase print?",
        "promptLetter": upper,
        "options": options,
        "answer": right,
        "feedback": f"{right} is one letter in uppercase and lowercase print. {partner} is a different letter.",
        "objectiveId": objective,
        "sourceId": "src-unicode-cyrillic",
        "reviewStatus": PENDING,
        "containsGeneratedRussian": False,
        "distractorRationales": [rationales[o] for o in options],
    }


def print_example(upper: str, objective: str) -> dict:
    return {
        "id": f"ex-print-letter-{ord(upper):04x}",
        "kind": "PRINT_PAIR",
        "letterId": f"letter-{ord(upper):04x}",
        "display": f"{upper} / {upper.lower()}",
        "note": f"{upper} and {upper.lower()} are the same letter in uppercase and lowercase print.",
        "sourceId": "src-unicode-cyrillic",
        "reviewStatus": PENDING,
        "objectiveId": objective,
    }


def unit_hosts(pack: dict, unit: dict) -> list[str]:
    """The Unit 2 learner strings that the validator binds to shipped spans or authored scaffolds."""
    examples = {e["id"]: e for e in pack["examples"]}
    items = {i["id"]: i for i in pack["items"]}
    hosts: list[str] = []

    def item_hosts(item_id: str) -> None:
        item = items.get(item_id)
        if item is None:
            return
        hosts.extend([item["prompt"], item.get("promptLetter") or "", item["answer"], item["feedback"]])
        hosts.extend(item["options"])
        hosts.extend((item.get("typedAnswerKey") or {}).get("acceptedAnswers", []))
        hosts.extend(item.get("distractorRationales", []))

    for session in unit["sessions"]:
        for st in session["steps"]:
            hosts.extend([st["title"], st["body"], st.get("bookDirection") or "", st.get("companionDirection") or ""])
            for ex_id in st.get("exampleIds", []):
                hosts.extend([examples[ex_id]["display"], examples[ex_id]["note"]])
            for item_id in st.get("itemIds", []):
                item_hosts(item_id)
    for objective in unit["objectives"]:
        for ex_id in objective["exampleIds"]:
            hosts.extend([examples[ex_id]["display"], examples[ex_id]["note"]])
        for item_id in objective["checkItemIds"]:
            item_hosts(item_id)
    return hosts


def step(step_id: str, kind: str, title: str, body: str, **extra) -> dict:
    out = {"id": step_id, "kind": kind, "title": title, "body": body}
    out.update(extra)
    return out


REVIEW_PENDING_SENTENCE = " Read this as a review-pending explanation."


def apply(pack: dict) -> dict:
    units = {u["number"]: u for u in pack["units"]}
    steps = {s["id"]: s for u in pack["units"] for se in u["sessions"] for s in se["steps"]}
    objectives = {o["id"]: o for u in pack["units"] for o in u["objectives"]}
    refs = {r["id"]: r for r in pack["references"]}

    # A2-26: governance terms in Unit 1 learner text.
    for sid in ("u1-st-confusable", "u1-st-iotated", "u1-st-signs"):
        body = steps[sid]["body"]
        if REVIEW_PENDING_SENTENCE not in body:
            raise SystemExit(f"{sid} baseline text changed")
        steps[sid]["body"] = body.replace(REVIEW_PENDING_SENTENCE, "")
    for holder, field in ((steps["u1-st-teach-letter-names"], "body"), (refs["ref-ca-letter-names"], "detail")):
        if not holder[field].startswith("Unit 1 letter names, review-pending: "):
            raise SystemExit("letter-name baseline text changed")
        holder[field] = holder[field].replace("Unit 1 letter names, review-pending: ", "Unit 1 letter names: ", 1)

    # Teaching gap for Ё and Ю: the iotated teaching names all four letters and shows each one.
    iotated = steps["u1-st-iotated"]
    iotated["body"] = iotated["body"].replace(
        "Recognize these printed forms as a group.",
        "Recognize these four printed letters as a group: Е, Ё, Ю, and Я. Ё is Е with two dots; Ю joins an upright to a rounded part.",
        1,
    )
    for ex in ("ex-letter-0401", "ex-letter-042e"):
        if ex not in iotated["exampleIds"]:
            iotated["exampleIds"].append(ex)

    # A2-24: teaching showed the exact print-pair key that the Unit 1 and Unit 2 checks accept.
    added_examples = [print_example("Е", "obj-u1-print-forms")]
    u1_print_examples = ["ex-print-letter-041c", "ex-print-letter-0415"]
    for sid in ("u1-st-print", "u1-st-decoding", "u1-st-guided-print", "u1-st-guided-decoding"):
        steps[sid]["exampleIds"] = list(u1_print_examples)
    objectives["obj-u1-print-forms"]["exampleIds"] = list(u1_print_examples)
    objectives["obj-u1-decoding"]["exampleIds"] = list(u1_print_examples)
    # Unit 2 shows Б / б only in guided practice; its note avoids the bare word "cases" (scaffold rule).
    for e in pack["examples"]:
        if e["id"] == "ex-print-letter-0411":
            e["note"] = "Б and б are the same letter in uppercase and lowercase print."
    review_body = steps["u2-st-review"]["body"]
    if "practise the case pairs" not in review_body:
        raise SystemExit("u2-st-review baseline text changed")
    steps["u2-st-review"]["body"] = review_body.replace("practise the case pairs", "practise the uppercase and lowercase pairs") + \
        " This review covers В, У, Х, Б, and Я."
    # Name the letters each print check uses (the cue), never the accepted pair (the key).
    steps["u1-st-print"]["body"] += " The checks ask about А, О, and Т."
    steps["u1-st-decoding"]["body"] += " The check asks about А."
    steps["u2-st-review"]["exampleIds"] = [e for e in steps["u2-st-review"]["exampleIds"] if e != "ex-print-letter-0411"]
    objectives["obj-u2-review"]["exampleIds"] = [e for e in objectives["obj-u2-review"]["exampleIds"] if e != "ex-print-letter-0411"]

    # A2-23: Unit 1 reaches 20 checked items with two more print pairs.
    new_items = []
    u1_ids = []
    for n, (upper, partner) in enumerate(PRINT_REVIEW_U1):
        item = print_item(f"u1-i-print-{upper.lower()}", "obj-u1-print-forms", upper, partner, n + 1)
        new_items.append(item)
        u1_ids.append(item["id"])
    objectives["obj-u1-print-forms"]["checkItemIds"] += u1_ids
    steps["u1-st-print-check"]["itemIds"] += u1_ids
    steps["u1-st-print-check"]["title"] = "Match print pairs"

    # A2-23: Unit 2 alphabet review (Unicode letter facts) reaches 20 checked items.
    u2 = units[2]
    u2_ids = []
    for n, (upper, partner) in enumerate(PRINT_REVIEW_U2):
        item = print_item(f"u2-i-print-{upper.lower()}", "obj-u2-alphabet", upper, partner, n)
        new_items.append(item)
        u2_ids.append(item["id"])
    added_examples += [print_example("Н", "obj-u2-alphabet"), print_example("Р", "obj-u2-alphabet")]
    alphabet_examples = ["ex-print-letter-041d", "ex-print-letter-0420"]
    new_items.append({
        "id": "u2-g-guided-alphabet",
        "kind": "PRINT_PAIR",
        "language": "en",
        "prompt": "Which lowercase print form pairs with this uppercase letter?",
        "promptLetter": "Н",
        "options": ["и", "н", "п"],
        "answer": "н",
        "feedback": "Н and н are one letter. The crossbar stays level in both forms.",
        "objectiveId": "obj-u2-alphabet",
        "sourceId": "src-unicode-cyrillic",
        "reviewStatus": PENDING,
        "containsGeneratedRussian": False,
        "distractorRationales": [
            "и pairs with И. Its stroke runs diagonally, not across as in н.",
            "",
            "п pairs with П. Its bar runs along the top, not across the middle as in н.",
        ],
    })
    alphabet_steps = [
        step("u2-st-alphabet", "TEACH", "Print pairs across the alphabet",
             "Unit 1 checked a staged set of letters. Every other printed letter also has one uppercase and one lowercase form. "
             "Most lowercase print forms are smaller copies of the capital. Some differ more, so look at the stroke that makes "
             "each letter distinct: a tail, a crossbar, a diagonal, or the number of uprights. Use the letter board to compare "
             "the pairs before the checks. This session covers Г, Д, Ж, З, И, К, Л, П, Ф, Ц, Ч, Ш, Щ, Ы, and Э.",
             exampleIds=list(alphabet_examples), referenceIds=["ref-cyrillic-print"]),
        step("u2-st-alphabet-guided", "GUIDED", "Guided contrast",
             "Match the uppercase letter to its lowercase print form with the letter board still in view. "
             "This is practice, not the independent check.",
             dominantAction="Check", itemIds=["u2-g-guided-alphabet"], exampleIds=list(alphabet_examples),
             remediationStepId="u2-st-alphabet"),
        step("u2-st-alphabet-check-1", "CHECK", "Print pairs: Г to К", "Identity check.",
             itemIds=u2_ids[0:5], remediationStepId="u2-st-alphabet"),
        step("u2-st-alphabet-check-2", "CHECK", "Print pairs: Л to Ч", "Identity check.",
             itemIds=u2_ids[5:10], remediationStepId="u2-st-alphabet"),
        step("u2-st-alphabet-check-3", "CHECK", "Print pairs: Ш to Э", "Identity check.",
             itemIds=u2_ids[10:15], remediationStepId="u2-st-alphabet"),
    ]
    u2["objectives"].insert(1, {
        "id": "obj-u2-alphabet",
        "unitNumber": 2,
        "title": "Match print pairs across the alphabet",
        "kind": "RECOGNITION",
        "instructionStepIds": ["u2-st-alphabet"],
        "exampleIds": list(alphabet_examples),
        "practiceStepIds": ["u2-st-alphabet-guided"],
        "checkItemIds": u2_ids,
        "remediationStepIds": ["u2-st-alphabet"],
    })

    # Owner addition: stress pairs and final devoicing, explained, from the frozen OpenRussian rows.
    examples, records = pair_content(frozen_rows())
    ex = {e["display"]: e["id"] for e in examples}
    pair_steps = [
        step("u2-st-stress-pairs", "TEACH", "Same letters, different stress",
             "Some words are spelled with the same letters and differ only in which vowel is stressed. In this course a "
             "stress mark over a vowel shows the stressed vowel; the mark is a reading aid that ordinary print leaves out. "
             "The dictionary gives за́мок, stressed on the first vowel, the meaning castle, and замо́к, stressed on the "
             "second vowel, the meaning lock. It gives му́ка the meaning torment and мука́ the meaning flour. "
             "When the mark is shown, read it before you decide which word you have.",
             exampleIds=[ex["за́мок"], ex["замо́к"], ex["му́ка"], ex["мука́"]], referenceIds=["ref-u2-pairs"]),
        step("u2-st-devoicing", "TEACH", "Different letters, same final sound",
             "At the end of a word said on its own, a voiced consonant letter is pronounced like its voiceless partner: "
             "г like к, д like т. So луг (meadow) and лук (onion) end in the same sound, as do код (code) and кот (cat), "
             "and плод (fruit) and плот (raft). The spelling keeps the letter, so read the letter to know the word. "
             "Confirm the sounds with the Lesson 2 pronunciation notes in your book.",
             exampleIds=[ex["луг"], ex["лук"], ex["код"], ex["кот"], ex["плод"], ex["плот"]], referenceIds=["ref-u2-pairs"]),
    ]
    u2["objectives"].insert(2, {
        "id": "obj-u2-pairs",
        "unitNumber": 2,
        "title": "Read stress pairs and final-consonant pairs",
        "kind": "GUIDANCE",
        "instructionStepIds": ["u2-st-stress-pairs", "u2-st-devoicing"],
        "exampleIds": [e["id"] for e in examples],
        "practiceStepIds": [],
        "checkItemIds": [],
        "remediationStepIds": [],
    })
    sessions = u2["sessions"]
    review_index = next(i for i, s in enumerate(sessions) if s["id"] == "u2-s-review")
    sessions.insert(review_index + 1, {
        "id": "u2-s-alphabet", "title": "Print pairs across the alphabet", "estimatedMinutes": 10,
        "objectiveIds": ["obj-u2-alphabet"], "steps": alphabet_steps,
    })
    pron_index = next(i for i, s in enumerate(sessions) if s["id"] == "u2-s-pronunciation")
    sessions.insert(pron_index + 1, {
        "id": "u2-s-pairs", "title": "Stress pairs and final consonants", "estimatedMinutes": 8,
        "objectiveIds": ["obj-u2-pairs"], "steps": pair_steps,
    })
    u2["estimatedMinutes"] += 18

    # A2-32 binder tasks and A2-33 tutor tasks (independently authored English).
    units[1]["binderTask"] = {
        "id": "binder-u1-print-pairs",
        "prompt": "On one binder page, copy every printed letter from the letter board as an uppercase and lowercase pair, "
                  "in alphabet order. Circle the six letters that look like Latin letters but are different letters.",
        "objectiveIds": ["obj-u1-print-forms", "obj-u1-confusable"],
    }
    units[1]["tutorTask"] = {"id": "tutor-u1-letter-names", "bring": "Your binder letter page, to name each printed letter aloud and hear any you are unsure of."}
    units[2]["binderTask"] = {
        "id": "binder-u2-pairs",
        "prompt": "In your binder, copy the two stress pairs and the three final-consonant pairs from this unit with their "
                  "meanings. Mark the stressed vowel in each stress pair and underline the final letter in each final-consonant pair.",
        "objectiveIds": ["obj-u2-pairs"],
    }
    units[2]["tutorTask"] = {"id": "tutor-u2-pairs", "bring": "The five Unit 2 word pairs, to hear each pair said aloud and say them back."}

    pack["references"].append({
        "id": "ref-u2-pairs",
        "title": "Stress pairs and final consonants",
        "summary": "Words that differ only by stress, and words whose final consonants sound alike.",
        "detail": "за́мок (castle) and замо́к (lock), му́ка (torment) and мука́ (flour) differ only in the stressed vowel. "
                  "луг (meadow) and лук (onion), код (code) and кот (cat), плод (fruit) and плот (raft) end in the same "
                  "sound when said on their own, because a final г sounds like к and a final д like т.",
        "introducedInUnit": 2,
        "sourceId": OR_SOURCE,
        "classification": "LINGUISTIC_INSTRUCTION",
        "reviewStatus": PENDING,
        "searchTerms": ["stress pairs", "final devoicing"],
    })
    pack["items"].extend(new_items)
    pack["examples"].extend(added_examples + examples)
    pack["provenanceRecords"].extend(records)

    # Unit 2 now ships source records, so its English is recorded as independently authored scaffold text.
    shipped = {r["shippedText"] for r in records}
    texts: list[str] = []
    for text in unit_hosts(pack, u2):
        if text and text not in shipped and text not in texts:
            texts.append(text)
    pack["authoredEnglishRecords"].extend(
        {"id": f"en-u2-{n:03d}", "unitNumber": 2, "text": text, "classification": "INDEPENDENTLY_AUTHORED_SCAFFOLD"}
        for n, text in enumerate(texts, start=1)
    )
    return pack


def main() -> None:
    text = ASSET.read_text(encoding="utf-8")
    if sha(text) != BASELINE_SHA256:
        raise SystemExit("Asset is not the T14-a baseline; this transformer runs only once")
    original_order = list(json.loads(text).keys())
    encoded = encode(apply(loads(text)))
    ordered = {k: encoded[k] for k in original_order if k in encoded}
    ordered.update({k: v for k, v in encoded.items() if k not in ordered})
    ASSET.write_text(json.dumps(ordered, ensure_ascii=False, separators=(",", ":")) + "\n", encoding="utf-8")
    print(f"wrote {ASSET.relative_to(ROOT)} sha256={sha(ASSET.read_text(encoding='utf-8'))}")


if __name__ == "__main__":
    main()
