#!/usr/bin/env python3
"""T14-b, first commit: owner decisions of 2026-11-11 on Units 1-2, and CC001 for T14-a items.

Exact-baseline transformer; it refuses any input other than the recorded baseline asset.

1. Typed items so Units 1-2 can carry an A2-34 unit test (at least 30 percent typed):
   - Unit 1: type the printed letter for a taught letter name. Only the three names that do not
     spell their own letter are used (и краткое, твёрдый знак, мягкий знак); every other taught
     name contains its letter, which would make the key inferable from the prompt (A2-24).
     Case cannot be checked: A2-21 folds the first letter's case, so "type the lowercase of Д"
     would accept the prompt itself.
   - Unit 2: type a final-consonant pair word from its English meaning. Keys and meanings come
     from the frozen OpenRussian snapshot (catalog/sources/units-3-5/openrussian/nouns.csv,
     commit 50e210c4, CC BY-SA 4.0); each key cites its snapshot row in a provenance record.
     кот is not used, so the Block 1 listen-check candidates do not change.
   Each key is signed "prepared by Claude Code (implementer); final audit pending" (owner decision).
2. u1-i-name-be keeps its key but no longer follows the step that states it: letter names are
   taught before pronunciation, and the name check moves to the end-of-unit check session.
3. CC001: the five T14-a print-pair checks that stored the answer first are reordered.
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
from apply_t14a_units_1_2 import (  # noqa: E402
    FREEZE, NOUNS, OR_SOURCE, PENDING, provenance, sha, step, unit_hosts,
)

ASSET = ROOT / "app/src/main/assets/content/v2-curriculum.json"
BASELINE_SHA256 = "1f46ca9d9d0b9ef8dac3b29e9a44d05d7175783ede2dc51213a7d6edd84c1762"
KEY_REVIEWER = "prepared by Claude Code (implementer); final audit pending"


# --- Typed answer key identity (mirrors V2TypedResponse.kt and V2GovernedIdentity.kt) ---------
def canonical_parts(parts: list[str]) -> str:
    return f"{len(parts)}{{" + ";".join(f"{len(p.encode('utf-8'))}:{p}" for p in parts) + "}"


def review_hash(text: str) -> str:
    return hashlib.sha256(text.encode("utf-8")).hexdigest()


TYPED_RULE_DIGEST = review_hash(canonical_parts([
    "closed-typed-response-v2",
    "finite reviewed versioned key; exact membership; trim response leading/trailing whitespace before comparison",
    "only first-letter simple case, combining acute U+0301 removal, and U+0401/U+0451 to U+0415/U+0435",
    "no inner-whitespace, punctuation, other-letter case, Unicode decomposition, spelling, synonym or fuzzy normalization",
]))


def typed_key(key_id: str, answers: list[str]) -> dict:
    content = canonical_parts(["typed-answer-key", "1", key_id, "1", "a2-21-owner-trim-v2", TYPED_RULE_DIGEST,
                               canonical_parts(answers)])
    return {"id": key_id, "version": 1, "acceptedAnswers": answers, "schema": 1,
            "review": {"reviewer": KEY_REVIEWER, "status": PENDING, "reviewedDigest": review_hash(content)}}


def typed_item(item_id: str, objective: str, prompt: str, cue: str, answer: str, feedback: str, source: str,
               accepted: list[str] | None = None) -> dict:
    return {
        "id": item_id, "kind": "TYPED_FORM", "language": "en", "prompt": prompt, "promptLetter": cue, "options": [],
        "answer": answer, "feedback": feedback, "objectiveId": objective, "sourceId": source,
        "reviewStatus": PENDING, "containsGeneratedRussian": False,
        "typedAnswerKey": typed_key("key-" + item_id, accepted or [answer]),
    }


# --- Unit 1: letters whose taught names do not contain them ------------------------------------
U1_TYPED = [("й", "Й", "и краткое"), ("ъ", "Ъ", "твёрдый знак"), ("ь", "Ь", "мягкий знак")]

# --- Unit 2: final-consonant pair words (accented field, English sense, partner) ---------------
U2_TYPED = [("луг", "meadow", "лук"), ("лук", "onion", "луг"), ("код", "code", "кот"),
            ("плод", "fruit", "плот"), ("плот", "raft", "плод")]
U2_GUIDED = ("за'мок", "castle")


def frozen_rows(wanted: set[str]) -> dict[str, dict]:
    freeze = json.loads(FREEZE.read_text(encoding="utf-8"))
    raw = NOUNS.read_bytes()
    if hashlib.sha256(raw).hexdigest() != freeze["openrussian"]["files"]["nouns.csv"]["sha256"]:
        raise SystemExit("Frozen OpenRussian nouns.csv does not match FREEZE.json")
    rows: dict[str, dict] = {}
    for row in csv.DictReader(raw.decode("utf-8").splitlines(), delimiter="\t", quoting=csv.QUOTE_NONE):
        if row["accented"] in wanted:
            if row["accented"] in rows:
                raise SystemExit(f"Ambiguous frozen row {row['accented']}")
            rows[row["accented"]] = row
    if set(rows) != wanted:
        raise SystemExit(f"Frozen rows missing: {sorted(wanted - set(rows))}")
    return rows


def senses(row: dict) -> list[str]:
    return [s.strip() for s in row["translations_en"].replace(";", ",").split(",") if s.strip()]


def apply(pack: dict) -> dict:
    units = {u["number"]: u for u in pack["units"]}
    items = {i["id"]: i for i in pack["items"]}
    steps = {s["id"]: s for u in pack["units"] for se in u["sessions"] for s in se["steps"]}
    objectives = {o["id"]: o for u in pack["units"] for o in u["objectives"]}

    # 3. CC001: answer moves from first to second place, rationales follow their options.
    for letter in ("г", "з", "л", "ц", "щ"):
        item = items[f"u2-i-print-{letter}"]
        if item["options"][0] != item["answer"]:
            raise SystemExit(f"{item['id']} baseline order changed")
        order = [1, 0, 2]
        item["options"] = [item["options"][i] for i in order]
        item["distractorRationales"] = [item["distractorRationales"][i] for i in order]

    # 2. Unit 1: letter names are taught before pronunciation; the name check closes the unit.
    u1 = units[1]
    sessions = {s["id"]: s for s in u1["sessions"]}
    names = sessions["u1-s-letter-names"]
    check = sessions["u1-s-check"]
    expected = ["u1-st-teach-letter-names", "u1-st-guided-letter-names", "u1-st-rem-letter-names",
                "u1-st-check-letter-names", "u1-st-anki-direction", "u1-st-binder-direction",
                "u1-st-tutor-direction", "u1-st-cumulative-direction"]
    if [s["id"] for s in names["steps"]] != expected or u1["sessions"][-2:] != [check, names]:
        raise SystemExit("Unit 1 letter-name baseline changed")
    taught, moved = names["steps"][:3], names["steps"][3:]
    names["steps"] = taught
    names["objectiveIds"] = ["obj-u1-letter-names"]
    u1["sessions"].remove(names)
    u1["sessions"].insert(next(i for i, s in enumerate(u1["sessions"]) if s["id"] == "u1-s-pronunciation"), names)
    u1["sessions"].remove(check)
    u1["sessions"].append(check)

    u1_typed = [typed_item(f"u1-i-type-{lower}", "obj-u1-letter-names",
                           "Type the printed letter that has this name.", name, lower,
                           f"{name} is the name of {upper}. Uppercase or lowercase is accepted.",
                           "src-unicode-cyrillic") for lower, upper, name in U1_TYPED]
    type_step = step("u1-st-type-letter-names", "CHECK", "Type letters from their names",
                     "Type each letter on a Russian keyboard, without looking back at the list. "
                     "Uppercase or lowercase is accepted.",
                     itemIds=[i["id"] for i in u1_typed], remediationStepId="u1-st-rem-letter-names")
    check["steps"] = check["steps"][:1] + [moved[0], type_step] + moved[1:]
    if "obj-u1-letter-names" not in check["objectiveIds"]:
        check["objectiveIds"].append("obj-u1-letter-names")
    rem = steps["u1-st-rem-letter-names"]
    rem["body"] += " " + " ".join(f"{upper} is named {name}." for _, upper, name in U1_TYPED)
    objectives["obj-u1-letter-names"]["checkItemIds"] += [i["id"] for i in u1_typed]

    # 1. Unit 2: type a final-consonant pair word from its meaning, checked at the end of the unit.
    u2 = units[2]
    rows = frozen_rows({w for w, _, _ in U2_TYPED} | {U2_GUIDED[0]})
    objective = "obj-u2-final-letters"
    records, u2_typed = [], []
    for word, sense, partner in U2_TYPED:
        if sense not in senses(rows[word]):
            raise SystemExit(f"{word}: sense {sense} not in {rows[word]['translations_en']}")
        partner_sense = next(s for w, s, _ in U2_TYPED + [("кот", "cat", "код")] if w == partner)
        item = typed_item(f"u2-i-type-{word}", objective,
                          "Type the word from this unit that has this meaning.", sense, word,
                          f"{word} ({sense}) ends with the letter {word[-1]}. {partner} ({partner_sense}) "
                          f"ends with {partner[-1]}. Both end in the same sound, so spell the word you mean.",
                          OR_SOURCE)
        u2_typed.append(item)
        records.append(provenance(2, objective, f"prov-u2-key-{word}", f"nouns.csv:{rows[word]['bare']}#accented",
                                  word, word, "selection"))
    accented, sense = U2_GUIDED
    shown = accented.replace("'", "́")
    if sense not in senses(rows[accented]):
        raise SystemExit(f"{accented}: sense {sense} missing")
    guided = typed_item("u2-g-type-castle", objective,
                        "Practice: type the word from this unit that has this meaning. The stress mark is optional.",
                        sense, shown, f"{shown} means {sense}. Typing it without the stress mark is accepted.", OR_SOURCE)
    records.append(provenance(2, objective, "prov-u2-key-castle", f"nouns.csv:{rows[accented]['bare']}#accented",
                              accented, shown, "formatting"))
    teach = step("u2-st-final-letters", "TEACH", "Spell the final letter",
                 "A final voiced consonant letter sounds like its voiceless partner, so hearing a word does not "
                 "tell you which letter ends it. Learn each word with its spelling. At the end of this unit you "
                 "type words from the final-consonant pairs from their meanings: write the letter the word is "
                 "spelled with, not the letter you hear.")
    practice = step("u2-st-final-letters-guided", "GUIDED", "Guided typing",
                    "Type one word from this unit from its meaning. This is practice, not the independent check.",
                    dominantAction="Check", itemIds=[guided["id"]], remediationStepId="u2-st-final-letters")
    pairs_session = next(s for s in u2["sessions"] if s["id"] == "u2-s-pairs")
    pairs_session["steps"] += [teach, practice]
    pairs_session["objectiveIds"].append(objective)
    final_check = step("u2-st-type-final", "CHECK", "Type the final-consonant words",
                       "Type each word from its meaning on a Russian keyboard. Spell the final letter.",
                       itemIds=[i["id"] for i in u2_typed], remediationStepId="u2-st-final-letters")
    u2_check = next(s for s in u2["sessions"] if s["id"] == "u2-s-check")
    u2_check["steps"].insert(1, final_check)
    u2_check["objectiveIds"].append(objective)
    pair_examples = [e for e in objectives["obj-u2-pairs"]["exampleIds"] if e.startswith("ex-u2-pair-")]
    u2["objectives"].insert(next(i for i, o in enumerate(u2["objectives"]) if o["id"] == "obj-u2-pairs") + 1, {
        "id": objective, "unitNumber": 2, "title": "Spell final-consonant pair words from their meanings",
        "kind": "RECOGNITION", "instructionStepIds": ["u2-st-final-letters"],
        "exampleIds": pair_examples, "practiceStepIds": ["u2-st-final-letters-guided"],
        "checkItemIds": [i["id"] for i in u2_typed], "remediationStepIds": ["u2-st-final-letters"],
    })
    u2["estimatedMinutes"] += 6

    pack["items"].extend(u1_typed + u2_typed + [guided])
    pack["provenanceRecords"].extend(records)

    # Unit 2 English hosts not covered by a shipped source span are recorded as authored scaffold.
    shipped = {r["shippedText"] for r in pack["provenanceRecords"] if r["unitNumber"] == 2}
    known = {r["text"] for r in pack["authoredEnglishRecords"] if r["unitNumber"] == 2}
    n = sum(1 for r in pack["authoredEnglishRecords"] if r["unitNumber"] == 2)
    for text in unit_hosts(pack, u2):
        if text and text not in shipped and text not in known:
            n += 1
            known.add(text)
            pack["authoredEnglishRecords"].append(
                {"id": f"en-u2-{n:03d}", "unitNumber": 2, "text": text, "classification": "INDEPENDENTLY_AUTHORED_SCAFFOLD"})
    return pack


def main() -> None:
    text = ASSET.read_text(encoding="utf-8")
    if sha(text) != BASELINE_SHA256:
        raise SystemExit("Asset is not the T14-b owner-decision baseline; this transformer runs only once")
    original_order = list(json.loads(text).keys())
    encoded = encode(apply(loads(text)))
    ordered = {k: encoded[k] for k in original_order if k in encoded}
    ordered.update({k: v for k, v in encoded.items() if k not in ordered})
    ASSET.write_text(json.dumps(ordered, ensure_ascii=False, separators=(",", ":")) + "\n", encoding="utf-8")
    print(f"wrote {ASSET.relative_to(ROOT)} sha256={sha(ASSET.read_text(encoding='utf-8'))}")


if __name__ == "__main__":
    main()
