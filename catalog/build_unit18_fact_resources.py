#!/usr/bin/env python3
"""T17c: write app/src/test/resources/unit18-fact-dispositions.json and unit18-phrase-attestations.json from the pack.

Dispositions follow the Codex prep (audit/prep/unit-18-prep-at-20bfde5c.txt, section 3) and the Units 16-30 sweep wording
(currentSentence where a sweep row or a prep section 4 qualification changed the wording of a marked sentence). Line 2183 is
REMOVE; every other entry is MARK. Marker reviews name the frozen lines of each marker without a Cyrillic word; phrase
attestations name a frozen line for every multi-word Russian phrase Unit 18 shows.
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
import apply_t17c_unit_18 as t  # noqa: E402

PACK = ROOT / "app/src/main/assets/content/v2-curriculum.json"
OUT = ROOT / "app/src/test/resources"
INVENTORY = ROOT / "audit/T15-n0-unmarked-russian-facts.csv"
UNIT = 18

# MARK entries whose learner wording changed: (line) -> the marked wording now shown.
CURRENT = {
    2132: "The full adjective compares size.",
    2137: "These forms concern degree of a quality, rather than naming an unrelated object.",
    2139: "The second gloss can mean best in context; better is not its universal translation.",
    2140: ("These full adjective forms can change for gender, number and case; they are not the same construction as an unchanging"
           " simple comparative."),
    2143: ("The first is a simple comparative with an ending expressing a higher degree of redness; this simple comparative does not"
           " change for gender, number or case."),
    2148: ("This superlative identifies the highest degree in the relevant group, rather than just a greater degree; besides the"
           " biggest it can mean the greatest or the largest."),
    2150: "More and less express comparison; most identifies the highest degree and least the lowest.",
    2154: "This superlative identifies the highest degree in the relevant group.",
    2182: "Russian keeps this preposition before the relative form, while the English translation places with at the end.",
}
REMOVES = {2183}
REASONS = {
    2132: "A sweep row removes the provenance narration (retained); the claim is marked.",
    2137: "A sweep row removes the provenance narration (retained); the claim is marked.",
    2139: "A sweep row removes the provenance narration (source gloss, existing choice); the better/best qualification is marked with OpenRussian лучший.",
    2140: "Prep section 4: simple comparative replaces short comparative; the claim is marked.",
    2143: "Prep section 4: simple comparative replaces short comparative and short form; the claim is marked.",
    2148: "A sweep row removes retained; prep section 4: the biggest is the example gloss, not a restriction to size (OpenRussian наибольший: the greatest, the largest).",
    2150: "Most (наибо́лее, G:303) joins the contrast so the cue of the least check is taught; the claim is marked.",
    2154: "Prep section 4: the superlative is not limited to physical size; the claim is marked.",
    2182: "A sweep row replaces the retained English rendering with the English translation; the claim is marked.",
    2183: "Commentary about what recognition demonstrates is removed (prep section 3); a practice instruction takes its place.",
}


def disposition(n: int, sentence: str):
    if n in REMOVES:
        return ("REMOVE", None, None)
    return ("MARK", None, None)


def unstressed(text: str) -> str:
    # HTML lines (Pressbooks, ReadyRussian) are compared by their visible text: tags removed, entities decoded.
    text = re.sub("<[^>]+>", "", re.sub("(?i)<br\\s*/?>", "\n", text)).replace("&nbsp;", "\u00a0").replace("&amp;", "&")
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
    lines = [n for n, row in enumerate(inv, 1) if row[0] == str(UNIT) and row[4].startswith("English-only")]
    for n in lines:
        row = inv[n - 1]
        owner, field, sentence = row[1], row[2], row[3]
        disp, replacement, kind = disposition(n, sentence)
        target = replacement if kind == "MARKED" else CURRENT.get(n, sentence)
        cited = owners[owner].get("russianFactSources", {}).get(target, []) if disp == "MARK" or kind == "MARKED" else []
        if (disp == "MARK" or kind == "MARKED") and not cited:
            raise SystemExit(f"{n}: no marker for {target}")
        entry = {"entry": f"U18-E{len(entries) + 1:03d}", "csvLine": n, "ownerId": owner, "field": field,
                 "baselineSentence": sentence, "disposition": disp}
        if n in CURRENT:
            entry["currentSentence"] = CURRENT[n]
        if replacement:
            entry.update({"replacement": replacement, "replacementKind": kind})
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
        "task": "T17c (Claude Code, 2026-11-23); dispositions from audit/prep/unit-18-prep-at-20bfde5c.txt, section 3",
        "totals": totals, "entries": entries, "markerReviews": reviews,
        "markerReviewsNote": ("Each Unit 18 marker with no Cyrillic word names the frozen files and lines of its cited records: a page "
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
        json.dumps({"task": "T17c (Claude Code, 2026-11-23)", "phrases": phrases}, ensure_ascii=False, indent=1) + "\n", encoding="utf-8")
    print(f"dispositions {totals}; marker reviews {len(reviews)}; phrases {len(phrases)}: {[(p['phrase'], p['file'].split('/')[-1], p['line']) for p in phrases]}")


if __name__ == "__main__":
    main()
