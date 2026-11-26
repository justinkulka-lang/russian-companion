#!/usr/bin/env python3
"""T17f (Block 4 close-out): WC001 for Unit 17 — the 26 template wrong-option rationales.

T17b kept 26 Unit 17 rationales of the form "<option> is a different meaning or construction and does not match the reading
of the shown form." (WC001: template splices that do not explain the specific option). Each is rewritten in English to say
what the chosen option is and why it does not give this item's reading. No Russian is generated: the only Russian in the
new text is the option itself, already shown. Each new sentence is marked with the frozen records its old sentence cited
(the grammar20 clock lines, the Pressbooks quarter-hour lines, the Wikipedia date-notation lines, the Wikibooks interrogative
lines). assessment.distractorDifference follows where it held the old rationale. New English wording gets a coverage record.
Every change is logged in audit/T17f-block4-changes.csv.
"""
from __future__ import annotations

import copy
import csv
import json
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / "catalog"))
from curriculum_storage import loads, encode  # noqa: E402
from apply_t17a_unit_16 import sentences, sha  # noqa: E402

ASSET = ROOT / "app/src/main/assets/content/v2-curriculum.json"
LOG = ROOT / "audit/T17f-block4-changes.csv"
BASELINE_SHA256 = "412ba91930d90a0981c6b58e9378d1cb5750babefc193276d9a74ea60b34d90d"  # the T17e pack (80c7219b)
TASK, DATE, UNIT = "T17f", "2026-11-26", 17
OLD_TAIL = " is a different meaning or construction and does not match the reading of the shown form."

HOUR = "counts a quarter into the hour in progress"
REWRITES = {
    ("u17-g-clock-0", "Which book do you want?"): "Which book do you want? asks which book someone wants, not the current clock time.",
    ("u17-i-clock-1", "Which book do you want?"): "Which book do you want? asks which book someone wants, not the current clock time.",
    ("u17-i-clock-2", "Which book do you want?"): "Which book do you want? asks which book someone wants, not the current clock time.",
    ("u17-g-clock-answers-0", "7:30"): "7:30 is half of the eighth hour; the shown form is a quarter of the eighth hour.",
    ("u17-g-clock-answers-0", "7:45"): "7:45 is eight without a quarter; the shown form is a quarter of the eighth hour.",
    ("u17-i-clock-answers-1", "7:15"): "7:15 is a quarter of the eighth hour; the shown form is half of the eighth hour.",
    ("u17-i-clock-answers-1", "7:45"): "7:45 is eight without a quarter; the shown form is half of the eighth hour.",
    ("u17-i-clock-answers-2", "7:15"): "7:15 is a quarter of the eighth hour; the shown form is eight without a quarter.",
    ("u17-i-clock-answers-2", "7:30"): "7:30 is half of the eighth hour; the shown form is eight without a quarter.",
    ("u17-g-quarter-hour-0", "7.15 – quarter of the eighth hour"):
        f"7.15 – quarter of the eighth hour {HOUR}; the shown sentence gives a time on the hour.",
    ("u17-g-quarter-hour-0", "4.30 – half of the fifth hour"):
        "4.30 – half of the fifth hour counts half of the hour in progress; the shown sentence gives a time on the hour.",
    ("u17-i-quarter-hour-1", "18.00"): "18.00 is a time on the hour; the shown sentence gives a quarter of the eighth hour.",
    ("u17-i-quarter-hour-1", "4.30 – half of the fifth hour"):
        "4.30 – half of the fifth hour counts half of the hour in progress; the shown sentence gives a quarter of the eighth hour.",
    ("u17-i-quarter-hour-2", "18.00"): "18.00 is a time on the hour; the shown sentence gives half of the fifth hour.",
    ("u17-i-quarter-hour-2", "7.15 – quarter of the eighth hour"):
        f"7.15 – quarter of the eighth hour {HOUR}; the shown sentence gives half of the fifth hour.",
    ("u17-i-quarter-hour-3", "18.00"): "18.00 is a time on the hour; the shown sentence gives 3 without a quarter.",
    ("u17-i-quarter-hour-3", "7.15 – quarter of the eighth hour"):
        f"7.15 – quarter of the eighth hour {HOUR}; the shown sentence gives 3 without a quarter.",
    ("u17-g-clock-productive-0", "в час пятнадцать минут"):
        "в час пятнадцать минут reads the hours and minutes off the dial instead of stating the minutes into the hour in progress.",
    ("u17-i-clock-productive-1", "в час тридцать пять минут"):
        "в час тридцать пять минут reads the hours and minutes off the dial instead of stating the minutes that remain after the half hour.",
    ("u17-i-clock-productive-2", "в десять минут первого"):
        "в десять минут первого states the minutes into the hour in progress instead of reading the numbers off the dial.",
    ("u17-g-calendar-date-0", "the month name"): "the month name is a different part of a date; the shown abbreviation marks the year.",
    ("u17-g-calendar-date-0", "the day number"): "the day number is a different part of a date; the shown abbreviation marks the year.",
    ("u17-i-calendar-date-1", "the month in genitive case"): "the month in genitive case describes one part of the date, not the order of the whole date.",
    ("u17-i-calendar-date-1", "28.08.17"): "28.08.17 is the all-numeric form of the date, not the order of its parts.",
    ("u17-i-calendar-date-2", "the month alone"): "the month alone translates one part of the shown date, not the whole date.",
    ("u17-i-calendar-date-2", "the year abbreviation alone"): "the year abbreviation alone translates one part of the shown date, not the whole date.",
}


def lettered(text: str) -> list[str]:
    return [x for x in sentences(text) if any(ch.isalpha() for ch in x)]


def coverage(pack: dict, records: dict, objective: str, key: str, text: str, unit: int = UNIT) -> None:
    template = records["prov-t15an1-u6-possessive-our-your"]
    record = copy.deepcopy(template)
    note = (f"New English wording authored on {DATE} ({TASK}); it did not exist before that date. It covers the wording"
            " only; the Russian facts it states are cited from frozen source records in the marker's russianFactSources."
            " 2026-10-08 is the date of the reused research context only.")
    record["synthesis"].update({"bibliography": note, "authorshipDeclaration": note + f' New text: "{text}"',
                                "objectiveOrClaimBinding": f"{objective}:{key}"})
    original = note + "\n" + text
    item_id = f"prov-ca-t17f-{key}"
    if item_id in records:
        raise SystemExit(f"Duplicate record {item_id}")
    record.update({"itemId": item_id, "unitNumber": unit, "objectiveId": objective, "sourceRecordId": f"ca-coverage:{objective}:{key}",
                   "shippedText": text, "originalSourceText": original, "sourceHash": sha(original), "contentHash": sha(text),
                   "attribution": "Russian Companion content-audit research synthesis, content-audit-2026-10-08;"
                                  f" this record's English text is new, authored {DATE} for {TASK}."})
    pack["provenanceRecords"].append(record)
    records[item_id] = record


def apply(pack: dict) -> list[dict]:
    items = {i["id"]: i for i in pack["items"]}
    records = {r["itemId"]: r for r in pack["provenanceRecords"]}
    shipped = {r["shippedText"] for r in pack["provenanceRecords"] if r["unitNumber"] == UNIT}
    log, covered = [], set()
    for (item_id, option), new in REWRITES.items():
        item = items[item_id]
        k = item["options"].index(option)
        old = item["distractorRationales"][k]
        assert old == option + OLD_TAIL, (item_id, old)
        facts, old_sources = list(item["russianFacts"]), dict(item["russianFactSources"])
        gone = [x for x in lettered(old) if x in old_sources]
        assert gone, (item_id, option)
        cited = list(dict.fromkeys(x for g in gone for x in old_sources[g]))
        item["distractorRationales"][k] = new
        log.append({"task": TASK, "finding": "WC001", "owner.field": f"{item_id}.distractorRationales[{k}]", "old": old, "new": new})
        difference = (item.get("assessment") or {}).get("distractorDifference")
        if difference and difference[k] == old:
            difference[k] = new
            log.append({"task": TASK, "finding": "WC001", "owner.field": f"{item_id}.assessment.distractorDifference[{k}]", "old": old, "new": new})
        # The old rationale's marked sentences give way to the new one's, at the same place and with the same frozen records;
        # a sentence that other text of the item still shows stays marked.
        still = {x for t in [item["feedback"]] + [r for r in item["distractorRationales"] if r] for x in lettered(t)}
        at = facts.index(gone[0])
        facts = [x for x in facts if x not in gone or x in still]
        at = min(at, len(facts))
        for x in lettered(new):
            if x not in facts:
                facts.insert(at, x)
                at += 1
                old_sources[x] = old_sources.get(x, cited)
        item["russianFacts"] = facts
        item["russianFactSources"] = {x: old_sources[x] for x in facts}
        for s in sentences(new):
            if any(ch.isalpha() for ch in s) and s not in shipped and s not in covered:
                coverage(pack, records, item["objectiveId"], f"{item_id}-r{k}-{len(covered)}", s)
                covered.add(s)
        if new not in shipped and new not in covered:
            coverage(pack, records, item["objectiveId"], f"{item_id}-rationale{k}", new)
            covered.add(new)
    assert not any(OLD_TAIL.strip() in r for i in pack["items"] for r in i.get("distractorRationales", []))
    print(f"{len(REWRITES)} rationales rewritten; {len(covered)} coverage records")
    log += wc005_unit16(pack, records)
    log += wc004_unit17(pack, records)
    log += cc028_unit17(pack, records)
    return log


# WC005 (Unit 16): u16-i-inst-sg-2 shows утром as its cue and мужем as a distractor, and no step before the checks presents
# either form. Both are on the frozen Wikibooks instrumental line its records quote ("Morning - утро --> утром",
# "Husband - муж - мужем"; prov-u16-inst-sg-x-0 and -x-1). The pencil card now gives both, and its remediation repeats it.
WC005_AFTER = "поездом is instrumental singular of the noun meaning train; карандашом is instrumental singular of the noun meaning pencil."
WC005_NEW = "мужем is instrumental singular of the noun meaning husband; утром is instrumental singular of the noun meaning morning."
WC005_SOURCES = ["prov-u16-inst-sg-x-1", "prov-u16-inst-sg-x-0"]


def wc005_unit16(pack: dict, records: dict) -> list[dict]:
    unit = next(u for u in pack["units"] if u["number"] == 16)
    steps = {s["id"]: s for se in unit["sessions"] for s in se["steps"]}
    for rid, form, gloss in (("prov-u16-inst-sg-x-1", "мужем", "Husband"), ("prov-u16-inst-sg-x-0", "утром", "Morning")):
        line = next(l for l in records[rid]["originalSourceText"].split("\n") if form in l)
        assert gloss in line and records[rid]["shippedText"] == form, (rid, line)
    log = []
    for sid in ("u16-st-teach-inst-sg-6", "u16-st-rem-inst-sg"):
        step = steps[sid]
        old = step["body"]
        assert old.count(WC005_AFTER) == 1 and WC005_NEW not in old, sid
        step["body"] = old.replace(WC005_AFTER, WC005_AFTER + " " + WC005_NEW)
        log.append({"task": TASK, "finding": "WC005", "owner.field": f"{sid}.body", "old": old, "new": step["body"]})
        if step.get("russianFacts") is not None:
            facts = step["russianFacts"]
            facts.insert(facts.index(WC005_AFTER) + 1, WC005_NEW)
            step["russianFactSources"][WC005_NEW] = list(WC005_SOURCES)
            step["russianFactSources"] = {f: step["russianFactSources"][f] for f in facts}
        objective = next(o["id"] for o in unit["objectives"] if sid in o["instructionStepIds"] + o["remediationStepIds"])
        coverage(pack, records, objective, f"{sid}-body", step["body"], 16)
    coverage(pack, records, "obj-u16-inst-sg", "u16-st-rem-inst-sg-wc005-fact", WC005_NEW, 16)
    print("WC005: the Unit 16 pencil card and its remediation present мужем and утром")
    return log


# WC004 (Unit 17): nine count examples were glossed with their own Russian, and the spoken-date example with a label. Each now
# gets the English meaning the unit's items already state for it ("два часа means two hours", cited to the numbers page
# lines 108-133), as T15g did for the same phrases in Unit 9; the spoken date gets the meaning of the date-notation line it
# quotes (day ordinal first, then the month in genitive case: 28 August).
WC004 = {
    "ex-u17-hours-0": "one hour", "ex-u17-hours-1": "two hours", "ex-u17-hours-2": "five hours",
    "ex-u17-years-0": "one year", "ex-u17-years-1": "two years", "ex-u17-years-2": "five years",
    "ex-u17-minutes-0": "one minute", "ex-u17-minutes-1": "two minutes", "ex-u17-minutes-2": "five minutes",
    "ex-u17-calendar-date-0": "the twenty-eighth of August",
}


def wc004_unit17(pack: dict, records: dict) -> list[dict]:
    examples = {e["id"]: e for e in pack["examples"]}
    stated = " ".join(i["feedback"] + " " + " ".join(i.get("distractorRationales", [])) for i in pack["items"]
                      if i["objectiveId"].startswith("obj-u17-"))
    log = []
    for eid, note in WC004.items():
        e = examples[eid]
        old = e.get("note") or ""
        assert old in (e["display"], "the month in genitive case"), (eid, old)
        if eid != "ex-u17-calendar-date-0":
            assert f"{e['display']} means {note}" in stated, (eid, note)  # the unit already states this meaning
        e["note"] = note
        log.append({"task": TASK, "finding": "WC004", "owner.field": f"{eid}.note", "old": old, "new": note})
        coverage(pack, records, e["objectiveId"], f"{eid}-note", note)
    print(f"WC004: {len(WC004)} Unit 17 example glosses")
    return log


# CC028 (owner decisions D50c and D50f): u17-i-age-1 and u17-i-age-2 offered the section label "age statements" as their only
# distractor.
# - u17-i-age-1 (мне двадцать лет -> I am 20 (years old)) takes a competing frozen Unit 17 count gloss that is wrong for the
#   prompt, under D50g (D2 precedent): "forty-one years" (prov-ex-u17-years-3-en, the numbers page, bound to obj-u17-age by the
#   frozen selection record prov-t17f-u17-age-forty-one-years), a different count that states no age. It
#   does not nest in or around the key and is not a label. (D50f rejected the literal reading on the age line, a calque the
#   teaching explains.)
# - u17-i-age-2 (двадцать лет -> 20 years) has no non-nested competitor on its line, so under D50c it becomes a typed check:
#   the key двадцать лет, cited to its frozen record, taught in u17-st-teach-age-2. Its feedback and markers are kept.
LABEL = "age statements"
LABEL_RECORD = "prov-u17-age-x-1"
COMPETITOR = "forty-one years"
COMPETITOR_SOURCE = "prov-ex-u17-years-3-en"
COMPETITOR_RECORD = "prov-t17f-u17-age-forty-one-years"  # D50g: frozen selection record binding the gloss to obj-u17-age
AGE1_RATIONALE = "forty-one years counts a different number of years and does not say whose age is given."
AGE2 = ("Type the Russian quantity phrase that means 20 years.", "двадцать лет", "prov-ex-u17-age-1-ru")


def cc028_unit17(pack: dict, records: dict) -> list[dict]:
    from apply_t14b_units_1_2_owner import typed_key
    items = {i["id"]: i for i in pack["items"]}
    log = []
    # u17-i-age-1: a competing frozen count gloss replaces the label, bound to the age objective by a frozen selection record
    # (the numbers-page record copied with its frozen text and hashes unchanged; only the id and objective differ).
    source = records[COMPETITOR_SOURCE]
    assert source["shippedText"] == COMPETITOR and source["unitNumber"] == UNIT and source["objectiveId"] == "obj-u17-years"
    bound = copy.deepcopy(source)
    bound.update({"itemId": COMPETITOR_RECORD, "objectiveId": "obj-u17-age"})
    pack["provenanceRecords"].append(bound)
    records[COMPETITOR_RECORD] = bound
    item, a = items["u17-i-age-1"], items["u17-i-age-1"]["assessment"]
    k = item["options"].index(LABEL)
    old = item["distractorRationales"][k]
    item["options"][k] = COMPETITOR
    a["contrastMembers"] = [COMPETITOR if m == LABEL else m for m in a["contrastMembers"]]
    a["distractorSourceIds"] = [COMPETITOR_RECORD if x == LABEL_RECORD else x for x in a["distractorSourceIds"]]
    a["relationParticipantIds"] = [COMPETITOR_RECORD if x == LABEL_RECORD else x for x in a["relationParticipantIds"]]
    item["distractorRationales"][k] = AGE1_RATIONALE
    a["distractorDifference"][k] = AGE1_RATIONALE
    facts, sources = item["russianFacts"], item["russianFactSources"]
    cited = sources.pop(old)
    facts[facts.index(old)] = AGE1_RATIONALE
    sources[AGE1_RATIONALE] = list(dict.fromkeys(cited + [COMPETITOR_RECORD]))
    item["russianFactSources"] = {f: sources[f] for f in facts}
    log += [{"task": TASK, "finding": "CC028", "owner.field": f"u17-i-age-1.options[{k}]", "old": LABEL, "new": COMPETITOR},
            {"task": TASK, "finding": "CC028", "owner.field": f"u17-i-age-1.distractorRationales[{k}]", "old": old, "new": AGE1_RATIONALE}]
    coverage(pack, records, item["objectiveId"], f"u17-i-age-1-rationale{k}-cc028", AGE1_RATIONALE)
    # u17-i-age-2: typed, key cited to its frozen record.
    item = items["u17-i-age-2"]
    prompt, key, citation = AGE2
    assert records[citation]["shippedText"] == key
    before = {f: item.get(f) for f in ("kind", "prompt", "promptLetter", "options")}
    item.update({"kind": "TYPED_FORM", "prompt": prompt, "promptLetter": "", "options": [], "answer": key,
                 "distractorRationales": [], "typedAnswerKey": typed_key("key-u17-i-age-2", [key]), "keyCitation": citation})
    item.pop("assessment", None)
    keep = [f for f in item["russianFacts"] if f in item["feedback"]]
    item["russianFacts"] = keep
    item["russianFactSources"] = {f: item["russianFactSources"][f] for f in keep}
    log.append({"task": TASK, "finding": "CC028", "owner.field": "u17-i-age-2", "old": json.dumps(before, ensure_ascii=False),
                "new": json.dumps({"kind": "TYPED_FORM", "prompt": prompt, "answer": key, "keyCitation": citation}, ensure_ascii=False)})
    coverage(pack, records, item["objectiveId"], "u17-i-age-2-prompt-cc028", prompt)
    print("CC028: u17-i-age-1 takes a competing frozen count gloss; u17-i-age-2 is a typed check")
    return log


def main() -> None:
    text = ASSET.read_text(encoding="utf-8")
    if sha(text) != BASELINE_SHA256:
        raise SystemExit("Asset is not the T17e baseline; this transformer runs only once")
    original_order = list(json.loads(text).keys())
    pack = loads(text)
    log = apply(pack)
    encoded = encode(pack)
    ordered = {k: encoded[k] for k in original_order if k in encoded}
    ordered.update({k: v for k, v in encoded.items() if k not in ordered})
    out = json.dumps(ordered, ensure_ascii=False, separators=(",", ":")) + "\n"
    if "--dry" in sys.argv:
        print(f"dry run; {len(log)} changes; asset not written")
        return
    ASSET.write_text(out, encoding="utf-8")
    with LOG.open("w", encoding="utf-8", newline="") as f:
        w = csv.DictWriter(f, fieldnames=["task", "finding", "owner.field", "old", "new"], lineterminator="\n")
        w.writeheader()
        w.writerows(log)
    print(f"wrote {ASSET.relative_to(ROOT)} sha256={sha(ASSET.read_text(encoding='utf-8'))}; {len(log)} rows in {LOG.relative_to(ROOT)}")


if __name__ == "__main__":
    main()
