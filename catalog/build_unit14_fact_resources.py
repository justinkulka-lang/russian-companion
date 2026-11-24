#!/usr/bin/env python3
"""T16d: write app/src/test/resources/unit14-fact-dispositions.json and unit14-phrase-attestations.json from the pack.

Dispositions follow the Codex prep (audit/prep/units-13-15-prep-at-4c402ef7.txt, Unit 14 section 3), the S1 verdicts in
audit/prep/s1-claim-verification.csv (1554 and 1559 PARTIAL -> reworded to the frozen text, marked) and the D31c sweep wording
(currentSentence where a marked sentence was reworded by the sweep). Marker reviews name the frozen lines of each marker
without a Cyrillic word; phrase attestations name a frozen line for every multi-word Russian phrase Unit 14 shows.
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
import apply_t16d_unit_14 as t  # noqa: E402

PACK = ROOT / "app/src/main/assets/content/v2-curriculum.json"
OUT = ROOT / "app/src/test/resources"
INVENTORY = ROOT / "audit/T15-n0-unmarked-russian-facts.csv"
UNIT = 14
M = ("MARK", None, None)


def rw(replacement: str, kind: str = "MARKED"):
    return ("REWORD", replacement, kind)


DISPOSITIONS = {
    1548: M, 1549: M, 1551: rw("Read the two future examples.", "INSTRUCTION"), 1554: rw(t.S1554), 1555: M,
    1559: rw(t.S1559), 1563: M, 1568: M, 1569: M, 1573: M, 1577: M, 1586: M, 1590: M,
}
# MARK entries whose learner wording the D31c sweep changed: (line) -> the marked wording now shown.
CURRENT = {
    1563: "The imperfective infinitive stays the same; the subject and auxiliary distinguish the number.",
    1568: "The cue shows third-person singular; change it to third-person plural of the same verb.",
    1577: "Both options are impersonal past and keep the same neuter singular verb; the person and state distinguish them.",
    1590: "Both forms are impersonal past; a female experiencer changes the pronoun, not the verb to feminine agreement.",
}
S1 = {1554: "PARTIAL", 1559: "PARTIAL"}
REASONS = {
    1551: "The unqualified future definition is bounded to the supplied statements (report section 4); an instruction remains, and the teaching states that both statements refer to future situations (F:15-18).",
    1554: "S1 PARTIAL: reworded to what vid-lingvistika-153277825.txt:4 states (a delimitative way of acting, separate from completion; F:16 is the supplied example); the claim about this item's work period is not stated and is removed.",
    1559: "S1 PARTIAL: reworded to what grammatical-aspect-1384289498.txt:86 and past-tense:17 state (activities have duration; an imperfective verb takes a duration phrase) and to the supplied option (F:17).",
}


def unstressed(text: str) -> str:
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
    for n, (disp, replacement, kind) in sorted(DISPOSITIONS.items()):
        row = inv[n - 1]
        assert row[0] == str(UNIT) and row[4].startswith("English-only"), (n, row)
        owner, field, sentence = row[1], row[2], row[3]
        target = replacement if kind == "MARKED" else CURRENT.get(n, sentence)
        cited = owners[owner].get("russianFactSources", {}).get(target, []) if disp == "MARK" or kind == "MARKED" else []
        if (disp == "MARK" or kind == "MARKED") and not cited:
            raise SystemExit(f"{n}: no marker for {target}")
        entry = {"entry": f"U14-E{len(entries) + 1:02d}", "csvLine": n, "ownerId": owner, "field": field,
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
        "task": "T16d (Claude Code, 2026-11-22); dispositions from audit/prep/units-13-15-prep-at-4c402ef7.txt, Unit 14 section 3, and audit/prep/s1-claim-verification.csv",
        "totals": totals, "entries": entries, "markerReviews": reviews,
        "markerReviewsNote": ("Each Unit 14 marker with no Cyrillic word names the frozen files and lines of its cited records: a page "
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
        json.dumps({"task": "T16d (Claude Code, 2026-11-22)", "phrases": phrases}, ensure_ascii=False, indent=1) + "\n", encoding="utf-8")
    print(f"dispositions {totals}; marker reviews {len(reviews)}; phrases {len(phrases)}: {[(p['phrase'], p['file'].split('/')[-1], p['line']) for p in phrases]}")


if __name__ == "__main__":
    main()
