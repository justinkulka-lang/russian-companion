#!/usr/bin/env python3
"""T15a (Claude Code): Unit 6 meets A2-23/24/26.

Exact-baseline transformer. Russian comes only from the frozen OpenRussian rows and the frozen Wikibooks
accusative page already in the inventory (catalog/sources/units-3-5/openrussian/nouns.csv,
catalog/sources/units-6-10/openrussian/adjectives.csv, catalog/sources/units-6-10/wikibooks/accusative-4330404.txt).

- A2-24: independent checks move to an end-of-unit check session; cue-only items get a third sourced form from the
  same OpenRussian row (feminine-1, animacy-1, animacy-2, possessive-3); the two dogs/cats lines become meaning checks
  with the English sentences from the same Wikibooks span; label/position prompts are reworded.
- A2-23: eight typed items with keys from shipped OpenRussian/Wikibooks forms; three closed items whose only
  distractor was the cue (object-1, pronoun-1, pronoun-3, feminine-2) are replaced by typed items.
- A2-26: governance wording, wiki markup and provenance narration leave Unit 6 learner text.
English assessment text that keeps the full source requirement (T15-n0) is covered by content-audit coverage records
authored on 2026-11-13 within the content-audit-2026-10-08 research context, as the existing Unit 6 coverage records are.
"""
from __future__ import annotations

import copy
import csv
import hashlib
import json
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / "catalog"))
from curriculum_storage import loads, encode  # noqa: E402
from apply_t14b_units_1_2_owner import typed_item  # noqa: E402

ASSET = ROOT / "app/src/main/assets/content/v2-curriculum.json"
BASELINE_SHA256 = "87e3fc725da146df4a27cbf7595c966fca7844960bdba32465d2bc877c444fc4"
NOUNS = ROOT / "catalog/sources/units-3-5/openrussian/nouns.csv"
ADJECTIVES = ROOT / "catalog/sources/units-6-10/openrussian/adjectives.csv"
ACUTE = "́"
AUTHORED = ("Independently authored English on 2026-11-13 (T15a, Claude Code) from retained complete source context; "
            "no copied primary prose or private book text.")


def sha(text: str) -> str:
    return hashlib.sha256(text.encode("utf-8")).hexdigest()


def stressed(original: str) -> str:
    return original.replace("'", ACUTE)


def frozen_row(path: Path, bare: str) -> dict:
    with path.open(encoding="utf-8") as handle:
        for row in csv.DictReader(handle, delimiter="\t", quoting=csv.QUOTE_NONE):
            if row["bare"] == bare:
                return row
    raise SystemExit(f"No frozen row {bare} in {path}")


def apply(pack: dict) -> dict:
    unit = next(u for u in pack["units"] if u["number"] == 6)
    items = {i["id"]: i for i in pack["items"]}
    steps = {s["id"]: s for se in unit["sessions"] for s in se["steps"]}
    objectives = {o["id"]: o for o in unit["objectives"]}
    records = {r["itemId"]: r for r in pack["provenanceRecords"]}
    new_records: list[dict] = []

    def lexical(base_id: str, new_id: str, file: str, bare: str, field: str) -> dict:
        base = records[base_id]
        row = frozen_row(NOUNS if file == "nouns.csv" else ADJECTIVES, bare)
        original = row[field].strip()
        if not original or "," in original:
            raise SystemExit(f"Frozen {bare}#{field} is not a single form: {original!r}")
        record = copy.deepcopy(base)
        record.update({"itemId": new_id, "sourceRecordId": f"{file}:{bare}#{field}", "originalSourceText": original,
                       "shippedText": stressed(original), "sourceHash": sha(original),
                       "contentHash": sha(stressed(original))})
        new_records.append(record)
        records[new_id] = record
        return record

    template = records["prov-ca-u6-feedback-u6-i-object-2-distractor-1"]

    def coverage(objective: str, key: str, text: str) -> None:
        record = copy.deepcopy(template)
        synthesis = record["synthesis"]
        # The 2026-10-08 date is the research context this record reuses, not the date of this text: the text is new
        # on 2026-11-13 (T15a) and did not exist before then. Any Russian it repeats is already shipped and traces to
        # its own frozen source record; this record covers only the new English wording.
        synthesis["authorshipDeclaration"] = AUTHORED + (
            " This record's text is new on 2026-11-13 and did not exist before that date; 2026-10-08 is the date of the"
            " reused research context only. New text: \"" + text + "\"")
        synthesis["objectiveOrClaimBinding"] = f"{objective}:{key}"
        synthesis["bibliography"] = synthesis["bibliography"].replace(
            "Independently authored English on 2026-10-12 within content-audit-2026-10-08",
            "New English authored on 2026-11-13 (T15a, Claude Code) using the content-audit-2026-10-08 research context")
        if "2026-11-13" not in synthesis["bibliography"]:
            raise SystemExit("Coverage template bibliography changed")
        original = synthesis["bibliography"] + "\n" + text
        record["attribution"] = (record["attribution"].rstrip(".") +
                                 "; this record's English text is new, authored 2026-11-13 for T15a.")
        record.update({"itemId": f"prov-t15a-{key}", "objectiveId": objective, "sourceRecordId": f"ca-coverage:{objective}:{key}",
                       "shippedText": text, "originalSourceText": original, "sourceHash": sha(original), "contentHash": sha(text)})
        new_records.append(record)

    def add_distractor(item_id: str, record: dict, rationale: str) -> None:
        item = items[item_id]
        a = item["assessment"]
        item["options"].append(record["shippedText"])
        item["distractorRationales"].append(rationale)
        a["contrastMembers"].append(record["shippedText"])
        a["distractorDifference"].append(rationale)
        a["distractorSourceIds"].append(record["itemId"])
        a["relationParticipantIds"].append(record["itemId"])
        coverage(item["objectiveId"], f"{item_id}-distractor-{len(item['options']) - 1}", rationale)

    # --- A2-24: cue-only items get a third form from the same frozen row -------------------------
    add_distractor("u6-i-feminine-1", lexical("prov-ex-or-u6-feminine-2", "prov-t15a-u6-mama-sg-gen", "nouns.csv", "мама", "sg_gen"),
                   "This is the singular genitive form instead, so it does not supply the requested singular accusative form.")
    add_distractor("u6-i-animacy-1", lexical("prov-ex-or-u6-animacy-6", "prov-t15a-u6-uchitel-sg-dat", "nouns.csv", "учитель", "sg_dat"),
                   "This is the singular dative form instead, so it does not supply the requested singular accusative form.")
    add_distractor("u6-i-animacy-2", lexical("prov-ex-or-u6-animacy-8", "prov-t15a-u6-drug-sg-dat", "nouns.csv", "друг", "sg_dat"),
                   "This is the singular dative form instead, so it does not supply the requested singular accusative form.")
    add_distractor("u6-i-possessive-3", lexical("prov-ex-or-u6-possessive-8", "prov-t15a-u6-tvoy-pl-nom", "adjectives.csv", "твой", "decl_pl_nom"),
                   "This is the plural nominative form instead, so it does not agree with a feminine singular noun.")
    tvoyo = lexical("prov-ex-or-u6-possessive-8", "prov-t15a-u6-tvoy-n-nom", "adjectives.csv", "твой", "decl_n_nom")

    # The dogs/cats lines become meaning checks: the English sentences come from the same Wikibooks span.
    for item_id, cue_ru, answer_en, other_en, why, rationale in (
        ("u6-i-object-2", "prov-ex-u6-object-3-ru", "prov-ex-u6-object-3-en", "prov-ex-u6-object-4-en",
         "In this sentence the dogs are the subject and the cats are the accusative object, so it means dogs hate cats. "
         "The other option reverses the subject and object roles.",
         "This option reverses the subject and object roles instead of keeping who hates whom."),
        ("u6-i-object-3", "prov-ex-u6-object-4-ru", "prov-ex-u6-object-4-en", "prov-ex-u6-object-3-en",
         "In this sentence the cats are the subject and the dogs are the accusative object, so it means cats hate dogs. "
         "The other option reverses the subject and object roles.",
         "This option reverses the subject and object roles instead of keeping who hates whom."),
    ):
        item = items[item_id]
        answer, other = records[answer_en]["shippedText"], records[other_en]["shippedText"]
        if item["promptLetter"] != records[cue_ru]["shippedText"]:
            raise SystemExit(f"{item_id} cue changed")
        item.update({"prompt": "Which English sentence has the same meaning as the shown sentence?", "options": [answer, other],
                     "answer": answer, "feedback": why, "distractorRationales": ["", rationale]})
        item["assessment"].update({
            "evidenceMode": "RULE_CLASSIFIES_SOURCE_EXAMPLE", "relationKind": "RULE_CLASSIFIES_SOURCE_EXAMPLE",
            "answerSourceIds": [answer_en], "distractorSourceIds": ["", other_en], "distractorDifference": ["", rationale],
            "whyAnswerDemonstratesObjective": why, "contrastMembers": [answer, other],
            "relationParticipantIds": [cue_ru, answer_en, other_en]})
        coverage("obj-u6-object", f"{item_id}-why", why)
        coverage("obj-u6-object", f"{item_id}-distractor-1", rationale)

    # Label/position prompts.
    items["u6-i-possessive-2"]["prompt"] = "Which phrase is the direct-object form of the shown feminine noun phrase?"
    items["u6-i-pronoun-2"]["prompt"] = "Which object form goes with the shown subject pronoun?"
    for item_id in ("u6-i-third-1", "u6-i-third-2"):
        items[item_id]["prompt"] = "Which possessive matches the shown possessor?"
    for item_id in ("u6-i-direction-1", "u6-i-direction-2", "u6-i-direction-3"):
        items[item_id]["prompt"] = "What does the preposition express in the shown sentence?"

    # --- A2-23: typed items; four cue-only closed items are replaced -------------------------------
    replaced = {"u6-i-object-1", "u6-i-pronoun-1", "u6-i-pronoun-3", "u6-i-feminine-2"}
    feedback = "Compare your answer with the worked examples for this form, then try again. Stress marks are optional when you type."
    typed = [
        ("u6-i-type-moy-neuter", "obj-u6-possessive", "Type the neuter singular nominative form of the shown possessive.",
         "мо́й", "моё", "src-openrussian-50e210c4"),
        ("u6-i-type-moy-plural", "obj-u6-possessive", "Type the plural nominative form of the shown possessive.",
         "мо́й", "мои́", "src-openrussian-50e210c4"),
        ("u6-i-type-tvoy-neuter", "obj-u6-possessive", "Type the neuter singular nominative form of the shown possessive.",
         "тво́й", tvoyo["shippedText"], "src-openrussian-50e210c4"),
        ("u6-i-type-kniga-acc", "obj-u6-feminine", "Type the singular accusative form of the shown noun.",
         "кни́га", "кни́гу", "src-openrussian-50e210c4"),
        ("u6-i-type-zhenshchina-acc", "obj-u6-feminine", "Type the singular accusative form of the shown noun.",
         "же́нщина", "же́нщину", "src-openrussian-50e210c4"),
        ("u6-i-type-object-order", "obj-u6-object", "Type the same sentence with the object after the verb.",
         "Я тебя люблю", "Я люблю тебя", "src-wikibooks-russian-cc-by-sa"),
        ("u6-i-type-pronoun-my", "obj-u6-pronoun", "Type the object form of the shown subject pronoun.",
         "Мы", "Нас", "src-wikibooks-russian-cc-by-sa"),
        ("u6-i-type-pronoun-oni", "obj-u6-pronoun", "Type the object form of the shown subject pronoun.",
         "Они", "Их", "src-wikibooks-russian-cc-by-sa"),
    ]
    shipped = {(r["sourceId"], r["objectiveId"], r["shippedText"]) for r in pack["provenanceRecords"] + new_records}
    added_items = []
    for item_id, objective, prompt, cue, answer, source in typed:
        if (source, objective, answer) not in shipped:
            raise SystemExit(f"{item_id} key {answer} has no shipped record for {objective}")
        added_items.append(typed_item(item_id, objective, prompt, cue, answer, feedback, source))
    for objective in objectives.values():
        objective["checkItemIds"] = [i for i in objective["checkItemIds"] if i not in replaced] + [
            t[0] for t in typed if t[1] == objective["id"]]
    pack["items"] = [i for i in pack["items"] if i["id"] not in replaced] + added_items

    # --- Check session at the end of the unit (A2-24 exact key) -----------------------------------
    check_steps = []
    for se in unit["sessions"]:
        kept = []
        for s in se["steps"]:
            if s["kind"] == "CHECK":
                s["itemIds"] = [i for i in s["itemIds"] if i not in replaced]
                objective = next(o for o in unit["objectives"] if set(o["checkItemIds"]) & set(s["itemIds"]))
                s["itemIds"] += [t[0] for t in typed if t[1] == objective["id"]]
                check_steps.append(s)
            else:
                kept.append(s)
        se["steps"] = kept
    book = next(i for i, se in enumerate(unit["sessions"]) if se["id"] == "u6-s-book")
    unit["sessions"].insert(book, {"id": "u6-s-checks", "title": "Unit 6 checks", "estimatedMinutes": 15,
                                   "objectiveIds": [o["id"] for o in unit["objectives"] if o["kind"] == "RECOGNITION"],
                                   "steps": check_steps})

    # --- A2-26 learner text -------------------------------------------------------------------------
    titles = {"obj-u6-possessive": "Possessives that agree with the noun", "obj-u6-third": "His, her and their",
              "obj-u6-object": "Subject and object", "obj-u6-feminine": "Accusative of а and я nouns",
              "obj-u6-pronoun": "Object pronouns"}
    for objective_id, title in titles.items():
        objectives[objective_id]["title"] = title
    for se in unit["sessions"]:
        if se["id"] == "u6-s-orient":
            se["title"] = "Possession, objects and direction"
        elif se["id"] == "u6-s-animacy":
            se["title"] = "Animate and inanimate objects"
        elif len(se.get("objectiveIds", [])) == 1 and se["objectiveIds"][0] in titles:
            se["title"] = titles[se["objectiveIds"][0]]
    unit["topicSummary"] = ("Unit 6 covers possessives, the accusative case for direct objects, animate and inanimate masculine "
                            "objects, object pronouns, and direction versus location, alongside Lesson 6 in your book.")
    orient = steps["u6-st-orient"]
    orient["title"] = "Possession, objects and direction"
    orient["body"] = "This unit covers possessives, direct objects, object pronouns, and direction versus location."
    for s in steps.values():
        if s["title"] == "Admitted lines":
            s["title"] = "Examples"
        elif s["title"] == "Admitted instruction":
            s["title"] = "The rule"
        if s["kind"] == "CHECK":
            owner = next(o for o in objectives.values() if set(o["checkItemIds"]) & set(s["itemIds"]))
            s["title"] = "Check: " + (titles.get(owner["id"]) or owner["title"])
            if s["id"] != "u6-st-check-animacy":
                s["body"] = "Answer without looking back. A miss returns to the explanation."
        if s["kind"] == "GUIDED" and s["body"] == "Choose the admitted record. The feedback states the frozen field.":
            s["body"] = "Choose an answer, then read the feedback."
        marker = " Read this as a review-pending explanation."
        if s["kind"] == "TEACH" and marker in s["body"]:
            s["body"] = s["body"][: s["body"].index(marker)].rstrip() + " "
        s["body"] = s["body"].replace("The retained clause Я де́лал стул.", "The clause Я де́лал стул.")
    guided_prompts = {"u6-g-direction-0": "What does the preposition express in the shown sentence?",
                      "u6-g-object-0": "Which sentence moves the object after the verb?",
                      "u6-g-pronoun-0": "Which object form goes with the shown subject pronoun?",
                      "u6-g-third-0": "Which possessive matches the shown possessor?"}
    for item_id, prompt in guided_prompts.items():
        items[item_id]["prompt"] = prompt
    narration = " The nominative names the subject or dictionary entry."
    for item_id in ("u6-i-animacy-1", "u6-i-animacy-2", "u6-g-animacy-0"):
        if not items[item_id]["feedback"].endswith(narration):
            raise SystemExit(f"{item_id} feedback baseline changed")
        items[item_id]["feedback"] = items[item_id]["feedback"][: -len(narration)] + " The nominative names the subject."
        coverage("obj-u6-animacy", f"{item_id}-feedback", items[item_id]["feedback"])
    # Teaching bodies changed above keep full coverage through a coverage record of their new text.
    for step_id in ("u6-st-teach-animacy-6", "u6-st-rem-animacy"):
        for paragraph in steps[step_id]["body"].split("\n\n"):
            if "The clause Я де́лал стул." in paragraph:
                coverage("obj-u6-animacy", f"{step_id}-clause", paragraph)
    # Wiki markup: the source records drop the emphasis quotes; the frozen original keeps them.
    for record_id, old, new in (("prov-u6-object-rem", "'''Я люблю собак'''", "Я люблю собак"),
                                ("prov-u6-direction-rem", "''direction''", "direction")):
        record = records[record_id]
        shown = record["shippedText"]
        record["shippedText"] = shown.replace(old, new)
        record["contentHash"] = sha(record["shippedText"])
        for s in steps.values():
            s["body"] = s["body"].replace(shown, record["shippedText"])
    for r in pack["references"]:
        if r["id"] == "ref-ca-cases":
            r["title"] = "Object agreement"
            r["summary"] = r["detail"] = "Match gender, number, and case together, and do not change gender silently."
        elif r["id"] == "ref-u610-attribution":
            r["detail"] = r["detail"].replace(" is frozen at commit ", " is pinned at commit ").replace(
                " are frozen by oldid ", " are pinned by oldid ")

    pack["provenanceRecords"].extend(new_records)
    return pack


def main() -> None:
    text = ASSET.read_text(encoding="utf-8")
    if sha(text) != BASELINE_SHA256:
        raise SystemExit("Asset is not the T15a baseline; this transformer runs only once")
    original_order = list(json.loads(text).keys())
    encoded = encode(apply(loads(text)))
    ordered = {k: encoded[k] for k in original_order if k in encoded}
    ordered.update({k: v for k, v in encoded.items() if k not in ordered})
    ASSET.write_text(json.dumps(ordered, ensure_ascii=False, separators=(",", ":")) + "\n", encoding="utf-8")
    print(f"wrote {ASSET.relative_to(ROOT)} sha256={sha(ASSET.read_text(encoding='utf-8'))}")


if __name__ == "__main__":
    main()
