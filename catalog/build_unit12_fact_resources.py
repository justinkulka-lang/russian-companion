#!/usr/bin/env python3
"""T16b: write app/src/test/resources/unit12-fact-dispositions.json and unit12-phrase-attestations.json from the pack.

Dispositions follow the Codex prep (audit/prep/units-11-12-prep-at-9a9b9760.txt, Unit 12 section 3) and the S1 verdicts in
audit/prep/s1-claim-verification.csv (1369 SUPPORTED -> MARK; 1370 and 1380 PARTIAL -> reworded to the frozen text, marked).
1365, 1390, 1418, 1419 and 1429 are reworded because their "the question asks" / "table forms" wording is A2-26 assessment
commentary; 1392 and 1395 lose wiki markup and "row" wording. Marker reviews name the frozen lines of each marker without a
Cyrillic word; phrase attestations name a frozen line for every multi-word Russian phrase Unit 12 shows.
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
import apply_t16b_unit_12 as t  # noqa: E402

PACK = ROOT / "app/src/main/assets/content/v2-curriculum.json"
OUT = ROOT / "app/src/test/resources"
INVENTORY = ROOT / "audit/T15-n0-unmarked-russian-facts.csv"
UNIT = 12
M = ("MARK", None, None)


def rw(replacement: str, kind: str = "MARKED"):
    return ("REWORD", replacement, kind)


DISPOSITIONS = {
    1341: M, 1347: M, 1350: M, 1352: M, 1356: M,
    1358: rw("Use the person forms taught here.", "INSTRUCTION"),
    1361: rw("Choose the requested person and number.", "INSTRUCTION"),
    1365: rw("Both refer to the future with a single conjugated perfective verb; the singular form refers to one speaker."),
    1369: M, 1370: rw(t.S1370), 1371: M, 1372: M,
    1374: rw(t.S1374, "INSTRUCTION"), 1375: M, 1379: M, 1380: rw(t.S1380), 1383: M,
    1390: rw("The same spelling as the dative singular can also be prepositional singular in a sentence."),
    1392: rw(t.DATIVE2),
    1395: rw("Keep the noun singular and select its dative form, rather than the plural nominative form."),
    1399: M, 1402: M, 1409: M, 1412: M, 1414: M,
    1418: rw("Neither singular use matches the plural form requested."),
    1419: rw("The prepositional case is used with a governing preposition in a sentence."),
    1424: M, 1425: M,
    1429: rw("A sentence uses the prepositional case after a governing preposition."),
}
REASONS = {
    1358: "The unrestricted perfective-future recipe is replaced by the bounded teaching (report section 4); an instruction remains.",
    1361: "Table-position commentary; reworded to an instruction (report section 3).",
    1369: "S1 SUPPORTED: grammatical-aspect-1384289498.txt:74 and :7.",
    1370: "S1 PARTIAL: reworded to what grammatical-aspect-1384289498.txt:63 and :7 state (process; repeated or habitual event); the general-factual clause is not stated and is removed.",
    1374: "No frozen statement supports the tense-free formulation; reworded to an instruction (report section 3).",
    1380: "S1 PARTIAL: reworded to what grammatical-aspect-1384289498.txt:63 and :20 and verbal-aspect:12 state (complete whole; process); the occurrence clause is removed.",
    1392: "Wiki emphasis markup removed (report section 3); dative-case:1 and :6.",
    1395: "\"row\" wording removed (report section 3); nouns.csv стол sg_dat and pl_nom.",
}
COMMENTARY = "A2-26 assessment commentary (\"the question asks\" / \"table forms\") removed; the fact stays marked."
for _n in (1365, 1390, 1418, 1419, 1429):
    REASONS[_n] = COMMENTARY


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
        target = replacement if kind == "MARKED" else sentence
        cited = owners[owner].get("russianFactSources", {}).get(target, []) if disp == "MARK" or kind == "MARKED" else []
        if (disp == "MARK" or kind == "MARKED") and not cited:
            raise SystemExit(f"{n}: no marker for {target}")
        entry = {"entry": f"U12-E{len(entries) + 1:02d}", "csvLine": n, "ownerId": owner, "field": field,
                 "baselineSentence": sentence, "disposition": disp}
        if replacement:
            entry.update({"replacement": replacement, "replacementKind": kind})
        entry.update({"sourceRecordIds": cited, "reason": REASONS.get(n, "Retained exact sentence; marker cites frozen-source records.")})
        entries.append(entry)
        totals[disp] = totals.get(disp, 0) + 1

    files = {prefix: "catalog/sources/" + rel for rel, _, prefix in t.PAGES.values()}
    files.update({"nouns.csv:": "catalog/sources/units-3-5/openrussian/nouns.csv", "verbs.csv:": "catalog/sources/units-3-5/openrussian/verbs.csv",
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
        "task": "T16b (Claude Code, 2026-11-20); dispositions from audit/prep/units-11-12-prep-at-9a9b9760.txt, Unit 12 section 3, and audit/prep/s1-claim-verification.csv",
        "totals": totals, "entries": entries, "markerReviews": reviews,
        "markerReviewsNote": ("Each Unit 12 marker with no Cyrillic word names the frozen files and lines of its cited records: a page "
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
        json.dumps({"task": "T16b (Claude Code, 2026-11-20)", "phrases": phrases}, ensure_ascii=False, indent=1) + "\n", encoding="utf-8")
    print(f"dispositions {totals}; marker reviews {len(reviews)}; phrases {len(phrases)}: {[(p['phrase'], p['file'].split('/')[-1], p['line']) for p in phrases]}")


if __name__ == "__main__":
    main()
