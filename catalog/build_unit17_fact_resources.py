#!/usr/bin/env python3
"""T17b: write app/src/test/resources/unit17-fact-dispositions.json and unit17-phrase-attestations.json from the pack.

Dispositions follow the Codex prep (audit/prep/units-16-17-prep-at-20bfde5c.txt, Unit 17 section 3), the S1 verdicts for lines
1920, 1947 and 1948 (audit/prep/s1-claim-verification.csv: SUPPORTED by ReadyRussian Grammar 24 — Capitalization, 933d0deb) and
the Units 16-30 sweep wording (currentSentence where a marked sentence was reworded by a sweep row). Departures from the prep are
named in REASONS: sweep rows that turn a marked sentence into an instruction (1918, 1919, 2016, 2035). Marker reviews name the
frozen lines of each marker without a Cyrillic word; phrase attestations name a frozen line for every multi-word Russian phrase
Unit 17 shows.
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
import apply_t17b_unit_17 as t  # noqa: E402

PACK = ROOT / "app/src/main/assets/content/v2-curriculum.json"
OUT = ROOT / "app/src/test/resources"
INVENTORY = ROOT / "audit/T15-n0-unmarked-russian-facts.csv"
UNIT = 17


def rw(replacement: str, kind: str = "INSTRUCTION"):
    return ("REWORD", replacement, kind)


REQ = " does not match the requested reading of the shown form."
NOW = " does not match the reading of the shown form."
COUNT = {1827: "one-hour", 1842: "two-hour", 1845: "five-hour", 1848: "one-year", 1863: "two-year", 1866: "five-year",
         1869: "one-minute", 1884: "two-minute", 1887: "five-minute"}
EVENT = "Interpret the whole event-time expression in context; a bare hour alone does not specify AM or PM."
# MARK entries whose learner wording a Units 16-30 sweep row changed: (line) -> the marked wording now shown.
CURRENT = {n: f"This is the {c} expression." for n, c in COUNT.items()}
CURRENT.update({n: EVENT for n in (2027, 2058, 2064, 2070)})
CURRENT.update({
    1948: "These vocabulary examples begin with capitals; that presentation is not a rule to capitalize every month name.",
    1950: "A month in a full date can have a different case form from its standalone name.",
    2011: "The compact half-hour expression is written as one word in this example.",
    2031: "The event sentences use at-time expressions.",
    2036: "The bare six-hour and one-hour expressions do not themselves distinguish evening from morning; use the sentence context.",
    2048: "Their preposition and noun forms differ; recognize each complete construction rather than exchanging endings mechanically.",
})
REWORDS = {
    1810: rw(t.NUMBER_REVIEW), 1814: rw(t.EXAMPLE_REVIEW), 1899: rw(t.EXAMPLE_REVIEW), 1923: rw(t.WEEKDAY_PRACTICE),
    1940: rw(t.MONTH_PRACTICE), 2012: rw(t.CLOCK_MATCH), 2105: rw(t.DATE_PRACTICE),
    1918: rw("Distinguish Thursday from Friday and Saturday."), 1919: rw("Distinguish Sunday from Monday and Saturday."),
    2016: rw("Interpret each time expression in context to distinguish morning from evening."),
    2035: rw("Interpret these time expressions in their sentence contexts."),
}
REMOVES = {1840, 1900, 1963, 1976, 1987, 1996}
S1 = {1920: "SUPPORTED", 1947: "SUPPORTED", 1948: "SUPPORTED"}
REASONS = {
    1920: "S1 SUPPORTED (audit/prep/s1-claim-verification.csv): marker cites ReadyRussian Grammar 24 lines 60, 62 and 65 (first word of a sentence capitalized; weekday names are common names, not capitalized) and Wikibooks Days line 7.",
    1947: "S1 SUPPORTED (audit/prep/s1-claim-verification.csv): marker cites ReadyRussian Grammar 24 lines 62 and 64 (month names are not capitalized).",
    1948: "S1 SUPPORTED (audit/prep/s1-claim-verification.csv): marker cites ReadyRussian Grammar 24 lines 62 and 64 and Wikibooks Months line 19 (the capitalized vocabulary label); a sweep row rewords the sentence.",
    1810: "Reworded to an instruction (prep section 3): the unsupported general-definition wording is removed.",
    1814: "Reworded to an instruction (prep section 3): commentary about how much of a sentence a fragment translates is removed.",
    1899: "Reworded to an instruction (prep section 3): the answer-selection script is removed.",
    1923: "Reworded to an instruction (prep section 3): the unsupported general weekday-construction claim is removed.",
    1940: "Reworded to an instruction (prep section 3).",
    2012: "Reworded to an instruction (prep section 3).",
    2105: "Reworded to an instruction (prep section 3): the unsupported general claim about ordinal case in event-date sentences is removed.",
    1918: "Departure: the sweep row rewrites this check commentary as an instruction (prep: MARK).",
    1919: "Departure: the sweep row rewrites this check commentary as an instruction (prep: MARK).",
    2016: "Departure: the sweep row rewrites this commentary as an instruction (prep: MARK).",
    2035: "Departure: the sweep row rewrites this source framing as an instruction (prep: MARK).",
    1840: "Commentary about what recognition demonstrates is removed (prep section 3); the sweep instruction takes its place.",
    1900: "Irrelevant English distractor commentary is removed (prep section 3).",
    1963: "Commentary about the supplied interpretation is removed (prep section 3); the sweep row removed it.",
    1976: "Description of the guided example is removed (prep section 3).",
    1987: "Commentary about the supplied interpretation is removed (prep section 3); the sweep row removed it.",
    1996: "Commentary about the supplied interpretation is removed (prep section 3); the sweep row removed it.",
}


def disposition(n: int, sentence: str):
    if n in REWORDS:
        return REWORDS[n]
    if n in REMOVES:
        return ("REMOVE", None, None)
    if sentence.endswith(REQ):
        CURRENT[n] = sentence[: -len(REQ)] + NOW
    return ("MARK", None, None)


def unstressed(text: str) -> str:
    # HTML lines (Pressbooks, ReadyRussian) are compared by their visible text: tags removed, entities decoded.
    text = re.sub("<[^>]+>", "", re.sub("(?i)<br\\s*/?>", "\n", text)).replace("&nbsp;", "\u00a0").replace("&amp;", "&")
    text = re.sub(r"\[\[[^\[\]|]*\|([^\[\]|]*)\]\]", r"\1", text).replace("'''", "").replace("''", "")
    return unicodedata.normalize("NFC", unicodedata.normalize("NFD", text).replace("́", "")).lower()  # T16c-n2 (D41c): yo and ye stay distinct


def main() -> None:
    pack = loads(PACK.read_text(encoding="utf-8"))
    records = {r["itemId"]: r for r in pack["provenanceRecords"]}
    unit = next(u for u in pack["units"] if u["number"] == UNIT)
    owners = {i["id"]: i for i in pack["items"] if i["objectiveId"].startswith(f"obj-u{UNIT}-")}
    owners.update({s["id"]: s for se in unit["sessions"] for s in se["steps"]})
    inv = list(csv.reader(INVENTORY.open(encoding="utf-8")))
    entries, totals = [], {}
    lines = [n for n, row in enumerate(inv, 1) if row[0] == str(UNIT) and row[4].startswith("English-only")]
    for n in lines:
        row = inv[n - 1]
        owner, field, sentence = row[1], row[2], row[3]
        disp, replacement, kind = disposition(n, sentence)
        target = replacement if kind == "MARKED" else CURRENT.get(n, sentence)
        cited = owners[owner].get("russianFactSources", {}).get(target, []) if disp == "MARK" or kind == "MARKED" else []
        if (disp == "MARK" or kind == "MARKED") and not cited:
            raise SystemExit(f"{n}: no marker for {target}")
        entry = {"entry": f"U17-E{len(entries) + 1:03d}", "csvLine": n, "ownerId": owner, "field": field,
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
    files.update({"nouns.csv:": "catalog/sources/units-3-5/openrussian/nouns.csv",
                  "adjectives.csv:": "catalog/sources/units-6-10/openrussian/adjectives.csv"})

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
        "task": "T17b (Claude Code, 2026-11-23); dispositions from audit/prep/units-16-17-prep-at-20bfde5c.txt, Unit 17 section 3, and audit/prep/s1-claim-verification.csv",
        "totals": totals, "entries": entries, "markerReviews": reviews,
        "markerReviewsNote": ("Each Unit 17 marker with no Cyrillic word names the frozen files and lines of its cited records: a page "
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
        json.dumps({"task": "T17b (Claude Code, 2026-11-23)", "phrases": phrases}, ensure_ascii=False, indent=1) + "\n", encoding="utf-8")
    print(f"dispositions {totals}; marker reviews {len(reviews)}; phrases {len(phrases)}: {[(p['phrase'], p['file'].split('/')[-1], p['line']) for p in phrases]}")


if __name__ == "__main__":
    main()
