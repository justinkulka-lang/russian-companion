#!/usr/bin/env python3
"""T17d: write app/src/test/resources/unit19-fact-dispositions.json and unit19-phrase-attestations.json from the pack.

Dispositions follow the Codex prep (audit/prep/unit-19-prep-at-20bfde5c.txt, section 3), the S1 verdicts for lines 2225
(SUPPORTED, ru Сослагательное наклонение 140019162) and 2234 (PARTIAL, Wiktionary бы 94001807) and the Units 16-30 sweep wording
(currentSentence where a sweep row or a T17d edit changed a marked sentence). Departures from the prep are named in REASONS: the
rebuilt obligation and probability option sets (2259, 2260, 2262, 2263, 2276, 2284). Marker reviews name the frozen lines of each
marker without a Cyrillic word; phrase attestations name a frozen line for every multi-word Russian phrase Unit 19 shows.
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
import apply_t17d_unit_19 as t  # noqa: E402

PACK = ROOT / "app/src/main/assets/content/v2-curriculum.json"
OUT = ROOT / "app/src/test/resources"
INVENTORY = ROOT / "audit/T15-n0-unmarked-russian-facts.csv"
UNIT = 19


def rw(replacement: str, kind: str = "INSTRUCTION"):
    return ("REWORD", replacement, kind)


# MARK entries whose learner wording changed (sweep rows or the T17d edits): (line) -> the marked wording now shown.
CURRENT = {
    2210: "When finding an aspect partner, preserve the lexical meaning while changing aspect.",
    2215: "Learn these two verbs as a pair with the same dissuading meaning.",
    2233: "These wish expressions have the conditional particle before the verb, whereas the earlier examples put it after the verb.",
    2242: "The first-person phrase expresses obligation with masculine singular agreement.",
    2246: "This phrase expresses obligation for a masculine singular speaker.",
    2253: "These English labels describe agreement; they are not literal translations of each Russian word.",
    2255: "I have to is the meaning of the first-person phrase.",
    2258: "This is the subject-agreement label for the feminine form, not its literal translation.",
    2261: "This is the subject-agreement label for the plural form, not its literal translation.",
    2271: "With a verb, this form can express likelihood.",
    2283: "expresses obligation: the employees must report on time.",
}
REWORDS = {
    2209: rw("Translate the verb when identifying its meaning."),
    2227: rw(t.AGREEMENT_REVIEW),
    2234: rw("The particle cannot begin a sentence.", "MARKED"),
    2260: rw("If the subject is plural calls for a different form of the adjective.", "MARKED"),
    2263: rw("If the subject is feminine calls for a different form of the adjective.", "MARKED"),
    2276: rw("Obligation describes a required action, likelihood an expected event such as the eclipse, and indebtedness owing"
             " something to someone.", "MARKED"),
    2284: rw("likelihood describes an expected event such as the eclipse, not the required employee action.", "MARKED"),
}
REMOVES = {2202, 2229, 2259, 2262, 2279, 2280, 2281}
S1 = {2225: "SUPPORTED", 2234: "PARTIAL"}
REASONS = {
    2225: "S1 SUPPORTED (audit/prep/s1-claim-verification.csv): marker cites ru Сослагательное наклонение 140019162 line 18 (time is shown lexically or by the situation) and Russian grammar line 1215.",
    2234: "S1 PARTIAL (audit/prep/s1-claim-verification.csv): Wiktionary бы 94001807 line 58 says the particle may appear anywhere in a sentence except the beginning, not clause; reworded to that frozen text and marked with line 58.",
    2209: "Reworded to an instruction (prep section 3); the sweep row supplies the wording.",
    2227: "Reworded to an instruction (prep section 3): the answer-selection script is removed.",
    2260: "Departure: the option set now contrasts feminine, neuter and plural subjects (prep section 1); the rationale names the plural option against the new contrast and is marked.",
    2263: "Departure: the option set now contrasts plural, feminine and neuter subjects (prep section 1); the rationale names the feminine option against the new contrast and is marked.",
    2276: "Departure: the sentence named the old option labels; it now names the three senses the probability check contrasts (W:33, W:36, W:39) and is marked.",
    2284: "Departure: the option set is now obligation, likelihood and indebtedness (prep section 1, W:36, W:39, W:33); the rationale is reworded for the likelihood option and marked.",
    2202: "Provenance narration is removed (prep section 3); the sweep instruction takes its place.",
    2229: "Commentary about kinds of assessment question is removed (prep section 3).",
    2259: "Departure: the I have to option leaves the obligation check (prep section 1), so its rationale is removed.",
    2262: "Departure: the I have to option leaves the obligation check (prep section 1), so its rationale is removed.",
    2279: "The dictionary-description distractor is replaced by complete translations (prep section 3, D2); its rationale is removed.",
    2280: "The dictionary-description distractor is replaced by complete translations (prep section 3, D2); its rationale is removed.",
    2281: "The dictionary-description distractor is replaced by complete translations (prep section 3, D2); its rationale is removed.",
}


def disposition(n: int, sentence: str):
    if n in REWORDS:
        return REWORDS[n]
    if n in REMOVES:
        return ("REMOVE", None, None)
    return ("MARK", None, None)


def unstressed(text: str) -> str:
    # HTML lines (Pressbooks, ReadyRussian) are compared by their visible text: tags removed, entities decoded.
    text = re.sub("<[^>]+>", "", re.sub("(?i)<br\\s*/?>", "\n", text)).replace("&nbsp;", "\u00a0").replace("&amp;", "&")
    text = re.sub(r"\[\[[^\[\]|]*\|([^\[\]|]*)\]\]", r"\1", text).replace("'''", "").replace("''", "")
    text = text.replace("[[", "").replace("]]", "")  # unpiped links read as their text (as TwoPartPhraseRule does)
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
        entry = {"entry": f"U19-E{len(entries) + 1:03d}", "csvLine": n, "ownerId": owner, "field": field,
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
                  "verbs.csv:": "catalog/sources/units-3-5/openrussian/verbs.csv"})

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
        "task": "T17d (Claude Code, 2026-11-23); dispositions from audit/prep/unit-19-prep-at-20bfde5c.txt, section 3, and audit/prep/s1-claim-verification.csv",
        "totals": totals, "entries": entries, "markerReviews": reviews,
        "markerReviewsNote": ("Each Unit 19 marker with no Cyrillic word names the frozen files and lines of its cited records: a page "
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
        json.dumps({"task": "T17d (Claude Code, 2026-11-23)", "phrases": phrases}, ensure_ascii=False, indent=1) + "\n", encoding="utf-8")
    print(f"dispositions {totals}; marker reviews {len(reviews)}; phrases {len(phrases)}: {[(p['phrase'], p['file'].split('/')[-1], p['line']) for p in phrases]}")


if __name__ == "__main__":
    main()
