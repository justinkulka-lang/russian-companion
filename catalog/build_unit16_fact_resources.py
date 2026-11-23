#!/usr/bin/env python3
"""T17a: write app/src/test/resources/unit16-fact-dispositions.json and unit16-phrase-attestations.json from the pack.

Dispositions follow the Codex prep (audit/prep/units-16-17-prep-at-20bfde5c.txt, Unit 16 section 3), the S1 verdict for line 1766
(audit/prep/s1-claim-verification.csv: SUPPORTED by Wiktionary под 92761073) and the Units 16-30 sweep wording (currentSentence
where a marked sentence was reworded by a sweep row). Departures from the prep are named in REASONS: sweep rows that turn a
marked sentence into an instruction; "table form" reworded for the widened lint; the two "became" rationales, which must name
their competing gloss. Marker reviews name the frozen lines of each marker without a Cyrillic word; phrase attestations name a
frozen line for every multi-word Russian phrase Unit 16 shows.
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
import apply_t17a_unit_16 as t  # noqa: E402

PACK = ROOT / "app/src/main/assets/content/v2-curriculum.json"
OUT = ROOT / "app/src/test/resources"
INVENTORY = ROOT / "audit/T15-n0-unmarked-russian-facts.csv"
UNIT = 16
M = ("MARK", None, None)


def rw(replacement: str, kind: str = "MARKED"):
    return ("REWORD", replacement, kind)


MARKED_LINES = [1656, 1658, 1663, 1670, 1694, 1696, 1703, 1709, 1720, 1724, 1729, 1740, 1741, 1743, 1745, 1746, 1747, 1748, 1750,
                1751, 1752, 1753, 1754, 1755, 1756, 1757, 1758, 1761, 1762, 1763, 1764, 1765, 1766, 1769, 1770, 1772, 1773, 1774,
                1775, 1776, 1777, 1779, 1780, 1783, 1784, 1788, 1789, 1793, 1794, 1796, 1798, 1801]
DISPOSITIONS = {n: M for n in MARKED_LINES}
DISPOSITIONS.update({
    1668: rw(t.SAME_PATTERN), 1673: rw("Distinguish the pencil meaning from train and husband.", "INSTRUCTION"),
    1674: rw("Identify both the noun and its case.", "INSTRUCTION"), 1677: rw(t.NOUN_REVIEW, "INSTRUCTION"),
    1678: rw(t.NOUN_REVIEW, "INSTRUCTION"), 1688: rw(t.PLURAL_REVIEW, "INSTRUCTION"), 1771: rw(t.EXAMPLE_REVIEW, "INSTRUCTION"),
    1785: rw(t.VERB_REVIEW, "INSTRUCTION"), 1786: rw("The second form means remain."), 1787: rw(t.VERB_REVIEW, "INSTRUCTION"),
    1797: rw(t.BECAME), 1800: rw(t.BECAME), 1733: ("REMOVE", None, None), 1790: ("REMOVE", None, None),
})
# MARK entries whose learner wording a Units 16-30 sweep row changed: (line) -> the marked wording now shown.
CURRENT = {
    1670: "These forms are not instrumental singular.",
    1703: "The first is not instrumental; the second is instrumental singular rather than plural.",
    1709: "Neither is instrumental plural.",
    1729: "Neither is the masculine instrumental singular form.",
}
S1 = {1766: "SUPPORTED"}
REASONS = {
    1766: "S1 SUPPORTED (audit/prep/s1-claim-verification.csv): marker cites Wiktionary под 92761073 lines 106, 135 and 136 (accusative for movement to beneath a place; instrumental for being beneath it, including swimming underwater).",
    1670: "Sweep row (assessment commentary) rewords the sentence; the marked wording cites nouns.csv дом sg_dat, sg_prep and sg_inst.",
    1703: "Sweep row rewords the sentence; the marked wording cites nouns.csv дом pl_dat, sg_inst and pl_inst.",
    1709: "Sweep row rewords the sentence; the marked wording cites nouns.csv кни́га and стол dative, prepositional and instrumental fields.",
    1729: "Sweep row removes the question framing; the marked wording cites adjectives.csv хоро́ший decl_m_dat, decl_pl_nom and decl_m_inst.",
    1668: "\"table form\" is source framing for the widened lint; the same fact names the forms (до́мом, столо́м; nouns.csv sg_inst).",
    1673: "Departure: the sweep row rewrites this answer-option narration as an instruction (prep: MARK).",
    1674: "Departure: the sweep row rewrites this framing as an instruction (prep: MARK).",
    1677: "Reworded to an instruction (prep section 3).",
    1678: "Reworded to an instruction (prep section 3).",
    1688: "Reworded to an instruction (prep section 3); the unrestricted plural formulation is removed.",
    1771: "Reworded to an instruction (prep section 3).",
    1785: "Answer-selection script reworded to an instruction (prep section 3).",
    1786: "Departure: the sweep row rewrites the script as \"The second form means remain.\", a marked fact citing verbs.csv оста́ться (remain) (prep: instruction).",
    1787: "Reworded to the same instruction (prep section 3).",
    1797: "Departure: V2Validator requires a distractor rationale to name its competing gloss, so the rationale keeps became and is marked (russian-grammar:48, Морфология:838, verbs.csv стать become) (prep: instruction).",
    1800: "Departure: V2Validator requires a distractor rationale to name its competing gloss, so the rationale keeps became and is marked (russian-grammar:48, Морфология:838, verbs.csv стать become) (prep: instruction).",
    1733: "Commentary about what recognition demonstrates is removed (prep section 3); the sweep instruction takes its place.",
    1790: "Commentary about what recognition demonstrates is removed (prep section 3); the sweep instruction takes its place.",
}


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
        entry = {"entry": f"U16-E{len(entries) + 1:02d}", "csvLine": n, "ownerId": owner, "field": field,
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
                  "nouns.csv:": "catalog/sources/units-3-5/openrussian/nouns.csv",
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
        "task": "T17a (Claude Code, 2026-11-22); dispositions from audit/prep/units-16-17-prep-at-20bfde5c.txt, Unit 16 section 3, and audit/prep/s1-claim-verification.csv",
        "totals": totals, "entries": entries, "markerReviews": reviews,
        "markerReviewsNote": ("Each Unit 16 marker with no Cyrillic word names the frozen files and lines of its cited records: a page "
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
        json.dumps({"task": "T17a (Claude Code, 2026-11-22)", "phrases": phrases}, ensure_ascii=False, indent=1) + "\n", encoding="utf-8")
    print(f"dispositions {totals}; marker reviews {len(reviews)}; phrases {len(phrases)}: {[(p['phrase'], p['file'].split('/')[-1], p['line']) for p in phrases]}")


if __name__ == "__main__":
    main()
