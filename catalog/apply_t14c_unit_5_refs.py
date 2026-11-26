#!/usr/bin/env python3
"""T14-c: Unit 5 (A2-23, A2-24, A2-26, A2-32, A2-33, A2-34) and Block 1 reference entries (A2-36).

Exact-baseline transformer; it refuses any input other than the recorded baseline asset.

- Unit 5 follows the T14-b pattern: governance terms leave learner text (quoted spans unchanged);
  every independent check moves to an end-of-unit check session (owner's narrowed A2-24 reading);
  six typed checks ask for question words the unit teaches but did not check (Кто, Что, Куда,
  Откуда, Сколько, Какой) from their English meanings, taken from the frozen Wikibooks table whose
  rows are already cited in provenance. Unit 5 reaches 20 checked items, 6 typed.
- A2-36: one reference entry for each grammar topic Block 1 first teaches (gender, personal
  pronouns, present forms, the nominative subject, question words, the prepositional, indeclinable
  nouns). Each entry restates the unit's own sourced teaching; no new Russian or claim is added.
Keys are signed "prepared by Claude Code (implementer); final audit pending" (owner decision).
"""
from __future__ import annotations

import json
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / "catalog"))
from curriculum_storage import loads, encode  # noqa: E402
from apply_t14a_units_1_2 import PENDING, sha, step  # noqa: E402
from apply_t14b_units_1_2_owner import typed_item  # noqa: E402
from apply_t14b_units_3_4 import (  # noqa: E402
    CYRILLIC, GOVERNANCE, GRAMMAR_TERM, LINGUISTIC_CLAIM, coverage, gaps, hosts, reword,
)

ASSET = ROOT / "app/src/main/assets/content/v2-curriculum.json"
BASELINE_SHA256 = "81496d053ee1a35ddc4eff460cfde10dffb3fd54384c1de6e6ab59a894b9a10e"
WB_SOURCE = "src-wikibooks-russian-cc-by-sa"

U5_REWORD = [
    ("Proposed Unit 5 uses frozen grammar excerpts and dictionary rows for ", "Unit 5 uses grammar excerpts and dictionary entries for "),
    ("These words are copied from the frozen table. ", "These words come from the grammar table. "),
    ("These examples are exact frozen excerpts. ", "These examples are quoted exactly from the grammar page. "),
    ("Three admitted function words", "Three function words"),
    ("Two admitted field pairs", "Two dictionary form pairs"),
    ("Each pair is the first field, then the second field, from the same admitted row.",
     "Each pair shows the first form, then the second form, from the same dictionary entry."),
    ("Use these rows only as quoted.", "Use these words only as quoted."),
    ("Classify the shown noun by its case behavior. A miss returns to all four explanations and the retained examples.",
     "Classify how the shown word behaves. A miss returns to all four explanations and the examples."),
    ("Which admitted question word is recorded for this English purpose: ", "Which question word has this English meaning: "),
    ("The frozen table records ", "The grammar table gives "),
    ("Which string is the admitted second field, not the first field?", "Which is the second form in the dictionary entry, not the first?"),
    ("The frozen row lists that field as ", "The dictionary entry gives the second form as "),
    ("Which admitted meaning is recorded for the shown written question?", "What does the shown written question mean?"),
    ("Which admitted description is recorded for the shown example?", "What does the shown example mean?"),
    ("Recognize admitted question words and written questions", "Recognize question words and written questions"),
    ("Recognize admitted prepositional examples", "Recognize prepositional examples"),
    ("Recognize admitted indeclinable nouns", "Recognize indeclinable nouns"),
    ("It contains admitted question, prepositional, and indeclinable records",
     "It contains grammar excerpts and dictionary entries for questions, the prepositional, and indeclinable nouns"),
]


def reword5(text: str) -> str:
    for old, new in U5_REWORD:
        text = text.replace(old, new)
    return reword(text)


TYPED_QUESTIONS = [("kto", "Кто", "Who"), ("chto", "Что", "What"), ("kuda", "Куда", "Whither, where to"),
                   ("otkuda", "Откуда", "Whence, where from"), ("skolko", "Сколько", "How much, how many"),
                   ("kakoy", "Какой", "What kind")]

REFERENCES = [
    ("ref-u3-gender", 3, "Noun gender groups", "Each noun belongs to one of three groups; learn the group with the word.",
     ["u3-st-teach-gender-1", "u3-st-teach-gender-2", "u3-st-teach-gender-3", "u3-st-teach-gender-4", "u3-st-teach-gender-5"],
     WB_SOURCE, ["gender", "masculine", "feminine", "neuter"]),
    ("ref-u4-pronouns", 4, "Personal pronouns", "The person words я, ты, он, она́, оно́, мы, вы and они́ with their meanings.",
     ["u4-st-teach-pronouns-1", "u4-st-teach-pronouns-2", "u4-st-teach-pronouns-3"],
     "src-openrussian-50e210c4", ["pronouns", "person words"]),
    ("ref-u4-present", 4, "Present-tense forms", "How the grammar page forms the present, with the forms of чита́ть and де́лать.",
     ["u4-st-teach-present-1", "u4-st-teach-present-2", "u4-st-teach-present-3", "u4-st-teach-present-4"],
     WB_SOURCE, ["present tense", "conjugation"]),
    ("ref-u4-nominative", 4, "The nominative subject", "The dictionary form as the subject of a supplied sentence.",
     ["u4-st-teach-nominative-1", "u4-st-teach-nominative-2", "u4-st-teach-nominative-3"],
     WB_SOURCE, ["nominative", "subject"]),
    ("ref-u5-questions", 5, "Question words", "The question words and written questions from the grammar table.",
     ["u5-st-teach-questions-1", "u5-st-teach-questions-2", "u5-st-teach-questions-3", "u5-st-teach-questions-4",
      "u5-st-teach-questions-5"], WB_SOURCE, ["questions", "question words"]),
    ("ref-u5-prepositional", 5, "The prepositional", "Quoted about- and place-examples and the dictionary form pairs.",
     ["u5-st-teach-prepositional-1", "u5-st-teach-prepositional-2", "u5-st-teach-prepositional-3",
      "u5-st-teach-prepositional-4", "u5-st-teach-prepositional-5", "u5-st-teach-prepositional-6"],
     WB_SOURCE, ["prepositional", "place"]),
    ("ref-u5-indeclinable", 5, "Indeclinable nouns", "Nouns that keep one form, contrasted with nouns that can change.",
     ["u5-st-teach-indeclinable-1", "u5-st-teach-indeclinable-2", "u5-st-teach-indeclinable-3", "u5-st-teach-indeclinable-4"],
     "src-openrussian-50e210c4", ["indeclinable", "declinable"]),
]


def apply(pack: dict) -> dict:
    units = {u["number"]: u for u in pack["units"]}
    examples = {e["id"]: e for e in pack["examples"]}
    items = {i["id"]: i for i in pack["items"]}
    u5 = units[5]

    # --- A2-26 rewording over Unit 5 learner text ------------------------------------------------
    u5["topicSummary"] = reword5(u5["topicSummary"])
    unit_items, unit_examples = set(), set()
    for o in u5["objectives"]:
        o["title"] = reword5(o["title"])
        unit_items.update(o["checkItemIds"])
        unit_examples.update(o["exampleIds"])
    for se in u5["sessions"]:
        se["title"] = reword5(se["title"])
        for st in se["steps"]:
            for f in ("title", "body", "bookDirection", "companionDirection"):
                if st.get(f):
                    st[f] = reword5(st[f])
            unit_items.update(st.get("itemIds", []))
            unit_examples.update(st.get("exampleIds", []))
    for i in unit_items:
        x = items[i]
        x["prompt"], x["feedback"] = reword5(x["prompt"]), reword5(x["feedback"])
        if x.get("distractorRationales"):
            x["distractorRationales"] = [reword5(r) for r in x["distractorRationales"]]
    for e in unit_examples:
        examples[e]["note"] = reword5(examples[e]["note"])

    # --- Typed question words (frozen Wikibooks table, records already in provenance) -------------
    obj = next(o for o in u5["objectives"] if o["id"] == "obj-u5-questions")
    shipped = {r["shippedText"] for r in pack["provenanceRecords"] if r["unitNumber"] == 5}
    typed = []
    for key, word, meaning in TYPED_QUESTIONS:
        if word not in shipped or meaning not in shipped:
            raise SystemExit(f"{word}/{meaning} is not a shipped Unit 5 span")
        typed.append(typed_item(f"u5-i-type-{key}", "obj-u5-questions", "Type the question word that has this meaning.",
                                meaning, word, f"{word} means {meaning}. Capital or small first letter is accepted.", WB_SOURCE))
    guided = typed_item("u5-g-type-gde", "obj-u5-questions", "Practice: type the question word that has this meaning.",
                        "Where (location)", "Где", "Где means Where (location). Capital or small first letter is accepted.", WB_SOURCE)
    obj["checkItemIds"] += [i["id"] for i in typed]
    guided_step = step("u5-st-guided-type-questions", "GUIDED", "Guided typing",
                       "Type one question word from its meaning on a Russian keyboard. This is practice, not the independent check.",
                       dominantAction="Check", itemIds=[guided["id"]], remediationStepId="u5-st-rem-questions")
    # The typed guided step stays in the lesson; obj-u5-questions keeps its closed guided practice as the
    # review-remediation route (the typed step is not added to practiceStepIds).
    type_check = step("u5-st-type-questions", "CHECK", "Type question words",
                      "Type each question word from its meaning. A miss returns to the explanation.",
                      itemIds=[i["id"] for i in typed], remediationStepId="u5-st-rem-questions")
    # Remediation shows every question word the checks ask for.
    rem = next(st for se in u5["sessions"] for st in se["steps"] if st["id"] == "u5-st-rem-questions")
    rem["exampleIds"] = list(obj["exampleIds"])
    for o in u5["objectives"]:
        if o["id"] in ("obj-u5-prepositional", "obj-u5-indeclinable"):
            r = next(st for se in u5["sessions"] for st in se["steps"] if st["id"] == o["remediationStepIds"][0])
            r["exampleIds"] = list(dict.fromkeys(r["exampleIds"] + o["exampleIds"]))

    # --- End-of-unit check session (owner's narrowed A2-24 reading) -------------------------------
    check_ids = ["u5-st-check-questions", "u5-st-check-prepositional", "u5-st-check-indeclinable"]
    moved = []
    for se in u5["sessions"]:
        keep = []
        for st in se["steps"]:
            (moved if st["id"] in check_ids else keep).append(st)
        se["steps"] = keep
    moved.sort(key=lambda s: check_ids.index(s["id"]))
    if len(moved) != 3:
        raise SystemExit("Unit 5 check steps changed")
    u5["sessions"].insert(next(i for i, se in enumerate(u5["sessions"]) if se["id"] == "u5-s-book"), {
        "id": "u5-s-check", "title": "Unit 5 checks", "estimatedMinutes": 12,
        "objectiveIds": ["obj-u5-questions", "obj-u5-prepositional", "obj-u5-indeclinable"],
        "steps": [guided_step] + moved + [type_check]})
    u5["estimatedMinutes"] += 12

    # --- A2-32 binder and A2-33 tutor tasks -------------------------------------------------------
    u5["binderTask"] = {
        "id": "binder-u5-questions",
        "prompt": "In your binder, copy the question words from this unit with their meanings, then copy the two "
                  "about-examples and the two place-examples with their meanings. Underline the ending that differs in "
                  "кни́га / кни́ге and письмо́ / письме́.",
        "objectiveIds": ["obj-u5-questions", "obj-u5-prepositional"],
    }
    u5["tutorTask"] = {"id": "tutor-u5-questions", "bring": "The Unit 5 written questions, to ask and answer them in a short exchange."}

    pack["items"].extend(typed + [guided])

    # --- A2-36 reference entries: each restates the unit's own sourced teaching -------------------
    all_steps = {st["id"]: st for u in pack["units"] for se in u["sessions"] for st in se["steps"]}
    known = {r["id"] for r in pack["references"]}
    for ref_id, unit, title, summary, step_ids, source, terms in REFERENCES:
        if ref_id in known:
            raise SystemExit(f"{ref_id} already exists")
        detail = "\n\n".join(all_steps[s]["body"].strip() for s in step_ids)
        pack["references"].append({
            "id": ref_id, "title": title, "summary": summary, "detail": detail, "introducedInUnit": unit,
            "sourceId": source, "classification": "LINGUISTIC_INSTRUCTION", "reviewStatus": PENDING, "searchTerms": terms,
        })
        first = all_steps[step_ids[0]]
        first["referenceIds"] = list(dict.fromkeys(first.get("referenceIds", []) + [ref_id]))

    # --- Coverage: new English fragments become authored scaffold records -------------------------
    shipped_list = [r["shippedText"] for r in pack["provenanceRecords"] if r["unitNumber"] == 5]
    scaffolds = [r["text"] for r in pack["authoredEnglishRecords"] if r["unitNumber"] == 5]
    count = 0
    for host in hosts(pack, u5):
        for g in gaps(host, coverage(host, shipped_list + scaffolds)):
            if g in scaffolds:
                continue
            if CYRILLIC.search(g) or LINGUISTIC_CLAIM.search(g) or (GRAMMAR_TERM.search(g) and "?" not in g):
                raise SystemExit(f"Unit 5: fragment needs a source span, not authored English: {g!r} in {host[:80]!r}")
            count += 1
            scaffolds.append(g)
            pack["authoredEnglishRecords"].append({"id": f"en-u5-t14c-{count:03d}", "unitNumber": 5, "text": g,
                                                   "classification": "INDEPENDENTLY_AUTHORED_SCAFFOLD"})
    for host in hosts(pack, u5) + [u5["topicSummary"]] + [o["title"] for o in u5["objectives"]] + [se["title"] for se in u5["sessions"]]:
        if GOVERNANCE.search(host):
            raise SystemExit(f"Unit 5: governance term left in learner text: {host[:100]!r}")
    return pack


def main() -> None:
    text = ASSET.read_text(encoding="utf-8")
    if sha(text) != BASELINE_SHA256:
        raise SystemExit("Asset is not the T14-c baseline; this transformer runs only once")
    original_order = list(json.loads(text).keys())
    encoded = encode(apply(loads(text)))
    ordered = {k: encoded[k] for k in original_order if k in encoded}
    ordered.update({k: v for k, v in encoded.items() if k not in ordered})
    ASSET.write_text(json.dumps(ordered, ensure_ascii=False, separators=(",", ":")) + "\n", encoding="utf-8")
    print(f"wrote {ASSET.relative_to(ROOT)} sha256={sha(ASSET.read_text(encoding='utf-8'))}")


if __name__ == "__main__":
    main()
