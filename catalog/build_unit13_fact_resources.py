#!/usr/bin/env python3
"""T16c: write app/src/test/resources/unit13-fact-dispositions.json and unit13-phrase-attestations.json from the pack.

Dispositions follow the Codex prep (audit/prep/units-13-15-prep-at-4c402ef7.txt, Unit 13 section 3), the S1 verdicts in
audit/prep/s1-claim-verification.csv (1462, 1470, 1482, 1511, 1528 SUPPORTED -> marked; 1443, 1508, 1523, 1542, 1543 PARTIAL ->
reworded to the frozen text, marked; 1517 NOT_SUPPORTED -> removed) and the D31c sweep wording (currentSentence where a marked
sentence was reworded by the sweep). Marker reviews name the frozen lines of each marker without a Cyrillic word; phrase
attestations name a frozen line for every multi-word Russian phrase Unit 13 shows.
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
import apply_t16c_unit_13 as t  # noqa: E402

PACK = ROOT / "app/src/main/assets/content/v2-curriculum.json"
OUT = ROOT / "app/src/test/resources"
INVENTORY = ROOT / "audit/T15-n0-unmarked-russian-facts.csv"
UNIT = 13
M = ("MARK", None, None)


def rw(replacement: str, kind: str = "MARKED"):
    return ("REWORD", replacement, kind)


DISPOSITIONS = {
    1433: M, 1434: M, 1436: M, 1439: M, 1440: M, 1441: M, 1442: M, 1443: rw(t.S1443), 1445: ("REMOVE", None, None),
    1449: M, 1450: M, 1453: M, 1457: M, 1459: M, 1461: M, 1462: M, 1463: M, 1465: M, 1467: M, 1468: M, 1469: M, 1470: M,
    1471: M, 1473: rw("Compare the supplied direct and reported evening examples.", "INSTRUCTION"), 1476: M, 1477: M,
    1478: rw("Compare the supplied examples.", "INSTRUCTION"),
    1480: rw("In English, the reported verb usually changes tense after a past reporting verb."),
    1481: rw("In Russian, this report keeps the tense of the original statement."),
    1482: M,
    1483: rw("Он сказа́л, что он был англича́нином adds a past copula, so it does not keep the same tense as the original statement."),
    1486: rw("Use the saying/telling pair shown here.", "INSTRUCTION"), 1489: rw("Use the saying/telling pair shown here.", "INSTRUCTION"),
    1490: rw("Learn the supplied infinitives.", "INSTRUCTION"),
    1491: rw("Keep both the aspect and the meaning of the pair in view.", "INSTRUCTION"),
    1492: rw("Learn the two verb pairs, then practise choosing aspect in sentence contexts and producing verbs separately.", "INSTRUCTION"),
    1496: M, 1499: M, 1503: rw("Identify the imperfective member of the verb pair while keeping the meaning the same.", "INSTRUCTION"),
    1506: M, 1508: rw(t.S1508),
    1509: rw("A past nationality statement does not describe this report, whose nationality statement has no past copula."),
    1510: rw("A past nationality statement does not describe this report, whose nationality statement has no past copula."),
    1511: M, 1513: M, 1514: rw("It has a past reporting verb with a present nationality statement."), 1515: ("REMOVE", None, None),
    1516: M, 1517: ("REMOVE", None, None), 1518: rw("Review the nationality example you learned earlier.", "INSTRUCTION"),
    1521: rw("It has a past reporting verb with a future embedded action."),
    1522: rw("The reporting verb said is past, and the embedded telling action keeps its future form."),
    1523: rw(t.S1523), 1524: rw("Use the supplied examples.", "INSTRUCTION"), 1526: M,
    1527: rw("Distinguish the Moscow plan from the direct evening statement.", "INSTRUCTION"), 1528: M,
    1529: rw("Study these examples in context; practise creating reports separately.", "INSTRUCTION"),
    1531: M, 1532: M, 1533: M, 1536: M,
    1538: rw("Only the evening report has a past reporting frame."), 1541: M, 1542: rw(t.S1542), 1543: rw(t.S1508),
    1544: rw("A future reporting verb with a past embedded action does not describe this report, whose reporting verb is past and"
             " whose embedded verb is future."),
    1545: rw("A future reporting verb with a past embedded action does not describe this report, whose reporting verb is past and"
             " whose embedded verb is future."),
}
# MARK entries whose learner wording the D31c sweep changed: (line) -> the marked wording now shown.
CURRENT = {
    1482: "English backshift can follow a past reporting verb, but is not mandatory in every report.",
    1434: "English simple past wording alone does not determine Russian aspect; identify the perfective form.",
    1440: "These English labels describe the examples; they do not match every English past tense to one Russian aspect.",
    1450: "The pronoun I does not determine past-tense gender for every speaker; match the past form to the speaker’s gender.",
    1457: "Both forms belong to the same imperfective verb; changing gender agreement does not change aspect.",
    1465: "Keeping the tense the same describes this Russian reporting comparison; it does not mean every verb in a report has the same tense.",
    1468: "In this comparison, English can shift a present statement to a past form after a past reporting verb.",
    1496: "Learn the two taught pairs rather than assuming that any prefix gives the corresponding perfective of any verb.",
    1499: "Learn this pair rather than assuming that adding any prefix gives the matching perfective of any verb.",
    1516: "The alternative with a past copula changes the stated reading.",
    1528: ("English backshift is not compulsory in every report; preserving these readings is not a universal rule that every tense or"
           " word stays unchanged."),
}
S1 = {1462: "SUPPORTED", 1470: "SUPPORTED", 1482: "SUPPORTED", 1511: "SUPPORTED", 1528: "SUPPORTED", 1443: "PARTIAL",
      1508: "PARTIAL", 1523: "PARTIAL", 1542: "PARTIAL", 1543: "PARTIAL", 1517: "NOT_SUPPORTED"}
REASONS = {
    1443: "S1 PARTIAL: reworded to what grammatical-aspect-1384289498.txt:63 states (process; repeated or habitual event); the ended-activity clause is not stated and is removed.",
    1462: "S1 SUPPORTED: sequence-of-tenses-1363198135.txt:4 and :34.",
    1470: "S1 SUPPORTED: sequence-of-tenses-1363198135.txt:21 and :22.",
    1482: "S1 SUPPORTED: sequence-of-tenses-1363198135.txt:9 and :21. The redesigned check (report section 1) keeps the supported clause; the clause about what the question classifies describes the item and is removed.",
    1508: "S1 PARTIAL: reworded to what sequence-of-tenses-1363198135.txt:21 and :39 state (English backshift optional; Russian indirect speech generally keeps the natural sequence).",
    1511: "S1 SUPPORTED: sequence-of-tenses-1363198135.txt:17 and :39.",
    1517: "S1 NOT_SUPPORTED: no frozen text supports the past-copula alternative being grammatical elsewhere; removed.",
    1523: "S1 PARTIAL: reworded to what sequence-of-tenses-1363198135.txt:39 and :17 state.",
    1528: "S1 SUPPORTED: sequence-of-tenses-1363198135.txt:21, :4 and :34 (sweep wording).",
    1542: "S1 PARTIAL: reworded to what sequence-of-tenses-1363198135.txt:17 and :39 state (the natural sequence); the completion clause is removed.",
    1543: "S1 PARTIAL: reworded to what sequence-of-tenses-1363198135.txt:21 and :39 state.",
    1445: "Dictionary-label commentary removed (report section 3).",
    1515: "The fragment \"is the matching description\" removed (report section 3); the description is now a sentence (1514).",
    1480: "u13-i-tense-keep-2 now chooses between the two Russian nationality reports of F:22 (report section 1); the English label sentence becomes the backshift statement it names (F:22, SOT:9).",
    1481: "Redesigned check (report section 1): the label sentence becomes the Russian statement it names (F:22, SOT:39).",
    1483: "Redesigned check (report section 1): the rationale names the rejected Russian alternative (F:22).",
    1509: "The complete rejecting explanation is one marked sentence; the wrong alternative is not marked as a positive assertion (report section 3).",
    1510: "The complete rejecting explanation is one marked sentence (report section 3).",
    1514: "The description becomes a full sentence (F:22).",
    1521: "The description becomes a full sentence (F:30).",
    1522: "The fragment joins its sentence (F:30).",
    1538: "u13-i-reported-2 takes the evening report as its foil (report section 1); the sentence states the contrast (F:30, :31).",
    1544: "The complete rejecting explanation is one marked sentence (report section 3).",
    1545: "The complete rejecting explanation is one marked sentence (report section 3).",
}
for _n in (1473, 1478, 1486, 1489, 1490, 1491, 1492, 1503, 1518, 1524, 1527, 1529):
    REASONS.setdefault(_n, "Reworded to an instruction (report section 3; D31c sweep wording where the sweep gave one).")


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
        entry = {"entry": f"U13-E{len(entries) + 1:02d}", "csvLine": n, "ownerId": owner, "field": field,
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
        "task": "T16c (Claude Code, 2026-11-21); dispositions from audit/prep/units-13-15-prep-at-4c402ef7.txt, Unit 13 section 3, and audit/prep/s1-claim-verification.csv",
        "totals": totals, "entries": entries, "markerReviews": reviews,
        "markerReviewsNote": ("Each Unit 13 marker with no Cyrillic word names the frozen files and lines of its cited records: a page "
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
        json.dumps({"task": "T16c (Claude Code, 2026-11-21)", "phrases": phrases}, ensure_ascii=False, indent=1) + "\n", encoding="utf-8")
    print(f"dispositions {totals}; marker reviews {len(reviews)}; phrases {len(phrases)}: {[(p['phrase'], p['file'].split('/')[-1], p['line']) for p in phrases]}")


if __name__ == "__main__":
    main()
