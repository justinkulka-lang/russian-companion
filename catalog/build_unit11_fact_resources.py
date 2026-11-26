#!/usr/bin/env python3
"""T16: write app/src/test/resources/unit11-fact-dispositions.json and unit11-phrase-attestations.json from the pack.

Dispositions follow the Codex prep report (audit/prep/units-11-12-prep-at-9a9b9760.txt, Unit 11 section 3); 1290 is
reworded because its "the question asks" wording is A2-26 assessment commentary. Marker reviews name, for every Unit 11
marker without a Cyrillic word, the frozen lines its cited records come from. Phrase attestations name a frozen line for
every multi-word Russian phrase Unit 11 shows.
"""
from __future__ import annotations

import csv
import json
import re
import sys
import unicodedata
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / "catalog"))
from curriculum_storage import loads  # noqa: E402

PACK = ROOT / "app/src/main/assets/content/v2-curriculum.json"
OUT = ROOT / "app/src/test/resources"
PAST = "catalog/sources/units-11-15/wikibooks/past-tense-4567337.txt"
VERBS = "catalog/sources/units-3-5/openrussian/verbs.csv"
INVENTORY = ROOT / "audit/T15-n0-unmarked-russian-facts.csv"

DISPOSITIONS = {
    1283: ("MARK", None, None, "Retained exact sentence; marker cites past-tense:38 and :56."),
    1290: ("REWORD", "The cue shows the feminine singular form, but the requested form is masculine singular of the same verb.", "MARKED",
           "\"the question asks\" is A2-26 assessment commentary; the reworded sentence keeps the fact and cites verbs.csv чита́ть past_f/past_m and past-tense:56."),
    1294: ("REWORD", "Choose the requested sentence.", "INSTRUCTION", "Item-direction commentary, not needed teaching; reworded to an instruction (report section 3)."),
    1303: ("REWORD", "The cue shows feminine singular, but the requested form is plural; past agreement follows the subject number.", "MARKED",
           "\"row\" wording removed (report section 3); marker cites verbs.csv быть and past-tense:38, :65."),
    1307: ("REWORD", "Check the requested gender and number.", "INSTRUCTION",
           "The baseline wording is stale after the constant-key repair of u11-i-past-be-2 (report sections 1 and 3)."),
    1311: ("MARK", None, None, "Retained exact sentence; marker cites past-tense:38, :65 and verbs.csv прочита́ть."),
    1313: ("MARK", None, None, "Retained exact sentence; marker cites past-tense:62 and :65."),
    1315: ("MARK", None, None, "Retained exact sentence; marker cites verbs.csv прочита́ть past_m/f/n and past-tense:38."),
    1326: ("MARK", None, None, "Retained exact sentence; marker cites past-tense:104 and :107."),
    1329: ("REWORD", "Copy the whole form.", "INSTRUCTION", "A pure instruction; the formation explanation stays sourced in the teaching (report section 3)."),
    1333: ("MARK", None, None, "Retained exact sentence; marker cites past-tense:107 and :109."),
    1337: ("REWORD", "Both retain the reflexive ending, so that ending alone does not distinguish them; the past ending shows gender and number.", "MARKED",
           "T16-n1 (D28c): \"the question requires\" is A2-26 assessment commentary; the reworded sentence keeps the fact and cites verbs.csv учи́ться past_f/past_pl and past-tense:38, :113, :115."),
}

# T17f (owner decision D42b): the T16-n2 (D31c) rewording that update_t16n2_fact_resources.py carried into the committed
# resource, by CSV line: a MARK sentence's current wording, or a REWORD sentence's new replacement. With it the builder
# regenerates app/src/test/resources/unit11-*.json byte for byte from the current pack (it used to look markers up under
# the pre-T16-n2 wording and stop). FactResourceBuildersReproducibleTest checks the round trip.
T16N2 = {
    1283: "The past form matches the subject in gender and number; choose feminine singular.",
    1290: "The cue is feminine singular; change it to masculine singular of the same verb.",
    1294: "Choose the correct sentence.",
    1303: "The cue is feminine singular; change it to plural, since past agreement follows the subject’s number.",
    1307: "Check the gender and number.",
}


def carry_t16n2(entry: dict, n: int) -> None:
    """Apply the recorded T16-n2 rewording to an entry exactly as update_t16n2_fact_resources.py did."""
    new = T16N2.get(n)
    if new is None:
        return
    if entry["disposition"] == "MARK":
        old = entry["baselineSentence"]
        entry["reason"] += f" T16-n2 (D31c): marked wording now \"{new}\" (was \"{old}\"); the same frozen records."
        entry["currentSentence"] = new
    else:
        old = entry["replacement"]
        entry["replacement"] = new
        entry["reason"] += f" T16-n2 (D31c): replacement now \"{new}\" (was \"{old}\")."


def lines(rel: str) -> list[str]:
    return (ROOT / rel).read_text(encoding="utf-8").split("\n")


def unstressed(text: str) -> str:
    text = text.replace("'''", "").replace("''", "")
    return unicodedata.normalize("NFC", unicodedata.normalize("NFD", text).replace("́", "")).lower()  # T16c-n2 (D41c): yo and ye stay distinct


def main() -> None:
    global OUT
    if len(sys.argv) == 3 and sys.argv[1] == "--out":  # T17f (D42b): write to another directory, for the reproducibility test
        OUT = Path(sys.argv[2])
    pack = loads(PACK.read_text(encoding="utf-8"))
    records = {r["itemId"]: r for r in pack["provenanceRecords"]}
    unit = next(u for u in pack["units"] if u["number"] == 11)
    owners = {i["id"]: i for i in pack["items"] if i["objectiveId"].startswith("obj-u11-")}
    owners.update({s["id"]: s for se in unit["sessions"] for s in se["steps"]})
    inv = list(csv.reader(INVENTORY.open(encoding="utf-8")))
    past, verbs = lines(PAST), lines(VERBS)

    entries = []
    for n, (disp, replacement, kind, reason) in sorted(DISPOSITIONS.items()):
        row = inv[n - 1]
        assert row[0] == "11" and row[4].startswith("English-only"), (n, row)
        owner, field, sentence = row[1], row[2], row[3]
        target = T16N2.get(n, replacement if kind == "MARKED" else sentence)
        cited = owners[owner].get("russianFactSources", {}).get(target, []) if disp == "MARK" or kind == "MARKED" else []
        entry = {"entry": f"U11-E{len(entries) + 1:02d}", "csvLine": n, "ownerId": owner, "field": field, "baselineSentence": sentence,
                 "disposition": disp}
        if replacement:
            entry.update({"replacement": replacement, "replacementKind": kind})
        entry.update({"sourceRecordIds": cited, "reason": reason})
        if (disp == "MARK" or kind == "MARKED") and not cited:
            raise SystemExit(f"{n}: no marker for {target}")
        carry_t16n2(entry, n)
        entries.append(entry)
    totals = {}
    for e in entries:
        totals[e["disposition"]] = totals.get(e["disposition"], 0) + 1

    def evidence(record_id: str) -> list[dict]:
        r = records[record_id]
        if r["sourceRecordId"].startswith("verbs.csv:"):
            accented = r["sourceRecordId"].split(":", 1)[1].split("#")[0]
            number = next(i for i, l in enumerate(verbs, 1) if l.split("\t")[1:2] == [accented])
            return [{"file": VERBS, "line": number, "quote": "\t".join(verbs[number - 1].split("\t")[:2])}]
        first = r["originalSourceText"].split("\n")[0].strip()
        number = next(i for i, l in enumerate(past, 1) if first in l)
        return [{"file": PAST, "line": number, "quote": first[:120]}]

    reviews = []
    for owner, host in owners.items():
        for fact in host.get("russianFacts", []):
            if re.search("[Ѐ-ӿ]", fact):
                continue
            ev = []
            for rid in host["russianFactSources"][fact]:
                for e in evidence(rid):
                    if e not in ev:
                        ev.append(e)
            reviews.append({"ownerId": owner, "sentence": fact, "evidence": ev})
    dispositions = {"task": "T16 (Claude Code, 2026-11-20); dispositions from audit/prep/units-11-12-prep-at-9a9b9760.txt, Unit 11 section 3",
                    "totals": totals, "entries": entries, "markerReviews": reviews,
                    "markerReviewsNote": ("Each Unit 11 marker with no Cyrillic word names the frozen files and lines of its cited records: "
                                          "a Wikibooks past-tense line by line number and exact quote, an OpenRussian verbs.csv row by its "
                                          "bare and accented headwords. Lines are 1-based.")}
    (OUT / "unit11-fact-dispositions.json").write_text(json.dumps(dispositions, ensure_ascii=False, indent=1) + "\n", encoding="utf-8")

    # Shown multi-word phrases (the test's walk) and a frozen line for each.
    skipped = {"id", "sourceId", "reviewStatus", "kind", "language", "objectiveId", "letterId", "installState", "readiness",
               "mappingStatus", "classification", "russianFactSources", "assessment", "containsGeneratedRussian", "penguinLesson",
               "mappingNote", "bookDirection", "advancement", "prerequisiteUnitNumbers", "searchTerms"}
    raw = json.loads(PACK.read_text(encoding="utf-8"))
    ru = next(u for u in raw["units"] if u["number"] == 11)
    objects = [{k: v for k, v in ru.items() if k != "sessions"}]
    examples, refs = set(), set()
    for se in ru["sessions"]:
        objects.append({k: v for k, v in se.items() if k != "steps"})
        for st in se["steps"]:
            objects.append(st)
            examples |= set(st.get("exampleIds", []))
            refs |= set(st.get("referenceIds", []))
    for it in raw["items"]:
        if it["objectiveId"].startswith("obj-u11-"):
            objects.append(it)
            examples |= set(it.get("exampleIds", []))
    objects += [e for e in raw["examples"] if e["id"] in examples] + [r for r in raw["references"] if r["id"] in refs]
    strings = []

    def walk(x):
        if isinstance(x, dict):
            for k, v in x.items():
                if k not in skipped and not k.endswith("Id") and not k.endswith("Ids"):
                    walk(v)
        elif isinstance(x, list):
            for v in x:
                walk(v)
        elif isinstance(x, str):
            strings.append(x)
    for o in objects:
        walk(o)
    cyr = "Ѐ-ӿ"
    rx = re.compile(f"[{cyr}][{cyr}́]*(?:-[{cyr}][{cyr}́]*)?(?:[  ]+[{cyr}][{cyr}́]*(?:-[{cyr}][{cyr}́]*)?)+")
    shown = sorted({m.group(0) for s in strings for m in rx.finditer(s)})
    phrases = []
    for phrase in shown:
        needle = re.compile(f"(?<![{cyr}]){re.escape(unstressed(phrase))}(?![{cyr}])")
        hit = next(((PAST, i) for i, l in enumerate(past, 1) if needle.search(unstressed(l))), None)
        if hit is None:
            raise SystemExit(f"No frozen past-tense line attests {phrase}")
        phrases.append({"phrase": phrase, "file": hit[0], "line": hit[1]})
    (OUT / "unit11-phrase-attestations.json").write_text(
        json.dumps({"task": "T16 (Claude Code, 2026-11-20)", "phrases": phrases}, ensure_ascii=False, indent=1) + "\n", encoding="utf-8")
    print(f"dispositions {totals}; marker reviews {len(reviews)}; phrases {len(phrases)}: {[p['phrase'] for p in phrases]}")


if __name__ == "__main__":
    main()
