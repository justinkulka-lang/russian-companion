#!/usr/bin/env python3
"""T16e: write app/src/test/resources/unit15-fact-dispositions.json and unit15-phrase-attestations.json from the pack.

Dispositions follow the Codex prep (audit/prep/units-13-15-prep-at-4c402ef7.txt and units-13-15-prep-unit-15-remainder.txt,
Unit 15 section 3; no S1 verdicts for Unit 15) and the D31c sweep wording (currentSentence where a marked sentence was reworded
by the sweep). Where the prep's suggested instruction contains wording the widened lint now rejects ("the requested", "requested
here"), the sweep's instruction or a plain equivalent is used instead. Marker reviews name the frozen lines of each marker without a
Cyrillic word; phrase attestations name a frozen line for every multi-word Russian phrase Unit 15 shows.
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
import apply_t16e_unit_15 as t  # noqa: E402

PACK = ROOT / "app/src/main/assets/content/v2-curriculum.json"
OUT = ROOT / "app/src/test/resources"
INVENTORY = ROOT / "audit/T15-n0-unmarked-russian-facts.csv"
UNIT = 15
M = ("MARK", None, None)


def rw(replacement: str, kind: str = "MARKED"):
    return ("REWORD", replacement, kind)


SINGULAR = "The cue shows the plural/polite imperative; choose the singular imperative."
DISPOSITIONS = {
    1593: M, 1595: rw(t.SHOWN, "INSTRUCTION"), 1600: M, 1602: rw(t.SHOWN, "INSTRUCTION"), 1605: rw(SINGULAR, "INSTRUCTION"),
    1608: M, 1611: M, 1614: rw(SINGULAR, "INSTRUCTION"), 1618: rw("Both are imperative forms of the same imperfective verb."),
    1622: rw("Copy the displayed stress.", "INSTRUCTION"), 1625: ("REMOVE", None, None), 1627: M, 1632: M,
    1636: rw(t.PAIR, "INSTRUCTION"), 1639: rw(t.PAIR, "INSTRUCTION"), 1641: rw("Copy each displayed form carefully.", "INSTRUCTION"),
    1642: rw(t.PREFIX), 1643: rw("Identify each member of the two infinitive pairs.", "INSTRUCTION"), 1644: ("REMOVE", None, None),
    1647: rw("Identify the imperfective member of the verb pair while keeping the meaning the same.", "INSTRUCTION"), 1651: M,
}
# MARK entries whose learner wording the D31c sweep changed: (line) -> the marked wording now shown.
CURRENT = {
    1608: ("Both are imperative forms of the shown infinitive; the singular form is informal, while spelling alone does not identify"
           " the actual number of listeners."),
    1632: ("The cue shows the plural/polite imperative; choose the singular imperative of the same say/tell verb, keeping meaning and"
           " aspect the same."),
}
S1: dict = {}
REASONS = {
    1593: "Retained exact sentence; marker cites russian-grammar:1169 (claim span), povelitelnoe-naklonenie:58 (S1) and verbs.csv чита́ть imperative fields.",
    1600: "Retained exact sentence; marker cites russian-grammar:1169 (claim span), povelitelnoe-naklonenie:58 (S1) and verbs.csv чита́ть imperative fields.",
    1595: "Assessment-scope commentary removed (report section 3); the prep's instruction \"Use the imperative form requested here\" is written without \"requested here\" (widened lint).",
    1602: "Assessment-scope commentary removed (report section 3); the prep's instruction is written without \"requested here\" (widened lint).",
    1605: "Table-row narration removed (report section 3); the D31c sweep instruction replaces the prep's \"Choose the requested singular form\" (widened lint).",
    1614: "Table-row narration removed (report section 3); the D31c sweep instruction replaces the prep's \"Choose the requested singular form\" (widened lint).",
    1618: "u15-i-imperative-2 now asks for the plural imperative (prep section 1, constant-key repair); the sentence states what both forms share (verbs.csv чита́ть aspect and imperative fields).",
    1622: "The generalized pragmatics claim is removed; the exact imperative stress is in verbs.csv сказа́ть (report section 3).",
    1625: "The malformed gloss fragment is removed; u15-st-teach-say-request-2 gives the complete pairing and gloss of verbal-aspect:42.",
    1627: "Retained exact sentence; marker cites russian-grammar:1169 (claim span) and povelitelnoe-naklonenie:58 (S1).",
    1642: "\"does not establish\" is A2-26 assessment commentary (widened lint); the prefix fact stays marked on verbal-aspect:25-29.",
    1643: "Reworded to an instruction (report section 3); the D31c sweep instruction replaces the prep's \"Choose the requested partner\" (widened lint).",
    1644: "Assessment-scope commentary removed (report section 3). The contextual skill is a documented gap (owner decision D4): the teaching directs aspect in requests to the book.",
    1647: "Reworded to an instruction (report section 3); the D31c sweep instruction replaces the prep's \"Use the requested pair and aspect\" (widened lint).",
}
for _n in (1636, 1639, 1641):
    REASONS.setdefault(_n, "Reworded to an instruction (report section 3).")


def unstressed(text: str) -> str:
    text = re.sub(r"\[\[[^\[\]|]*\|([^\[\]|]*)\]\]", r"\1", text).replace("'''", "").replace("''", "")
    return unicodedata.normalize("NFC", unicodedata.normalize("NFD", text).replace("́", "")).replace("ё", "е").lower()


def main() -> None:
    pack = loads(PACK.read_text(encoding="utf-8"))
    records = {r["itemId"]: r for r in pack["provenanceRecords"]}
    unit = next(u for u in pack["units"] if u["number"] == UNIT)
    owners = {i["id"]: i for i in pack["items"] if i["objectiveId"].startswith(f"obj-u{UNIT}-")}
    owners.update({s["id"]: s for se in unit["sessions"] for s in se["steps"]})
    inv = list(csv.reader(INVENTORY.open(encoding="utf-8")))
    entries, totals = [], {}
    for n, (disp, replacement, kind) in sorted(DISPOSITIONS.items()):
        row = inv[n - 1]
        assert row[0] == str(UNIT) and row[4].startswith("English-only"), (n, row)
        owner, field, sentence = row[1], row[2], row[3]
        target = replacement if kind == "MARKED" else CURRENT.get(n, sentence)
        cited = owners[owner].get("russianFactSources", {}).get(target, []) if disp == "MARK" or kind == "MARKED" else []
        if (disp == "MARK" or kind == "MARKED") and not cited:
            raise SystemExit(f"{n}: no marker for {target}")
        entry = {"entry": f"U15-E{len(entries) + 1:02d}", "csvLine": n, "ownerId": owner, "field": field,
                 "baselineSentence": sentence, "disposition": disp}
        if n in CURRENT:
            entry["currentSentence"] = CURRENT[n]
        if replacement:
            entry.update({"replacement": replacement, "replacementKind": kind})
        if n in S1:
            entry["s1Verdict"] = S1[n]
        entry.update({"sourceRecordIds": cited, "reason": REASONS.get(n, "Retained exact sentence; marker cites frozen-source records.")})
        entries.append(entry)
        totals[disp] = totals.get(disp, 0) + 1

    files = {prefix: "catalog/sources/" + rel for rel, _, prefix in t.PAGES.values()}
    files.update({"verbs.csv:": "catalog/sources/units-3-5/openrussian/verbs.csv",
                  "future-report-full-context:": "catalog/sources/units-11-15/wikibooks/future-tense-3353822.txt"})

    def evidence(record_id: str) -> dict:
        r = records[record_id]
        prefix = next(p for p in files if r["sourceRecordId"].startswith(p))
        lines = (ROOT / files[prefix]).read_text(encoding="utf-8").split("\n")
        if prefix.endswith(".csv:"):
            accented = r["sourceRecordId"].split(":", 1)[1].split("#")[0]
            number = next(i for i, l in enumerate(lines, 1) if l.split("\t")[1:2] == [accented])
            return {"file": files[prefix], "line": number, "quote": "\t".join(lines[number - 1].split("\t")[:2])}
        first = r["originalSourceText"].split("\n")[0].strip()
        number = next(i for i, l in enumerate(lines, 1) if first in l)
        return {"file": files[prefix], "line": number, "quote": first[:120]}

    reviews = []
    for owner, host in owners.items():
        for fact in host.get("russianFacts", []):
            if re.search("[Ѐ-ӿ]", fact):
                continue
            ev = []
            for rid in host["russianFactSources"][fact]:
                e = evidence(rid)
                if e not in ev:
                    ev.append(e)
            reviews.append({"ownerId": owner, "sentence": fact, "evidence": ev})
    (OUT / f"unit{UNIT}-fact-dispositions.json").write_text(json.dumps({
        "task": "T16e (Claude Code, 2026-11-22); dispositions from audit/prep/units-13-15-prep-at-4c402ef7.txt and units-13-15-prep-unit-15-remainder.txt, Unit 15 section 3, and audit/prep/s1-claim-verification.csv",
        "totals": totals, "entries": entries, "markerReviews": reviews,
        "markerReviewsNote": ("Each Unit 15 marker with no Cyrillic word names the frozen files and lines of its cited records: a page "
                              "line by number and exact quote, an OpenRussian row by its bare and accented headwords. Lines are 1-based.")},
        ensure_ascii=False, indent=1) + "\n", encoding="utf-8")

    skipped = {"id", "sourceId", "reviewStatus", "kind", "language", "objectiveId", "letterId", "installState", "readiness",
               "mappingStatus", "classification", "russianFactSources", "assessment", "containsGeneratedRussian", "penguinLesson",
               "mappingNote", "bookDirection", "advancement", "prerequisiteUnitNumbers", "searchTerms"}
    raw = json.loads(PACK.read_text(encoding="utf-8"))
    ru = next(u for u in raw["units"] if u["number"] == UNIT)
    objects = [{k: v for k, v in ru.items() if k != "sessions"}]
    examples, refs = set(), set()
    for se in ru["sessions"]:
        objects.append({k: v for k, v in se.items() if k != "steps"})
        for st in se["steps"]:
            objects.append(st)
            examples |= set(st.get("exampleIds", []))
            refs |= set(st.get("referenceIds", []))
    for it in raw["items"]:
        if it["objectiveId"].startswith(f"obj-u{UNIT}-"):
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
    candidates = list(dict.fromkeys(files[p] for p in files))
    phrases = []
    for phrase in shown:
        needle = re.compile(f"(?<![{cyr}]){re.escape(unstressed(phrase))}(?![{cyr}])")
        hit = None
        for rel in candidates:
            for i, l in enumerate((ROOT / rel).read_text(encoding="utf-8").split("\n"), 1):
                if needle.search(unstressed(l)):
                    hit = (rel, i)
                    break
            if hit:
                break
        if hit is None:
            raise SystemExit(f"No frozen line attests {phrase}")
        phrases.append({"phrase": phrase, "file": hit[0], "line": hit[1]})
    (OUT / f"unit{UNIT}-phrase-attestations.json").write_text(
        json.dumps({"task": "T16e (Claude Code, 2026-11-22)", "phrases": phrases}, ensure_ascii=False, indent=1) + "\n", encoding="utf-8")
    print(f"dispositions {totals}; marker reviews {len(reviews)}; phrases {len(phrases)}: {[(p['phrase'], p['file'].split('/')[-1], p['line']) for p in phrases]}")


if __name__ == "__main__":
    main()
