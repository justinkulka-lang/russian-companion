#!/usr/bin/env python3
"""T16-n2: carry the Units 6-12 fact-disposition resources and reviewed-instruction lists over the T16-n2 rewording.

A sentence is followed through the same rewrites apply_t16n2.py made (the D31c sweep rows of its owner, the adjusted
rows, then the lint substitutions). Every disposition keeps its baseline sentence, disposition and frozen records:
- MARK: if the marked wording changed, currentSentence names the new wording (the same russianFactSources);
- REWORD: the replacement follows the rewording;
- markerReviews: the sentence follows the rewording; its evidence (frozen file, line and quote) is unchanged.
reviewedNonFacts entries in Unit6..Unit12FactProvenanceTest.kt follow the rewording too (each with a "was" comment).
Writes the JSON files; prints the instruction changes. Run once after apply_t16n2.py.
"""
from __future__ import annotations

import csv
import json
import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / "catalog"))
import apply_t16n2 as t  # noqa: E402
from curriculum_storage import loads  # noqa: E402

RES = ROOT / "app/src/test/resources"
TESTS = ROOT / "app/src/test/java/dev/russiancompanion/v2"
NAMES = {6: "Six", 7: "Seven", 8: "Eight", 9: "Nine", 10: "Ten", 11: "Eleven", 12: "Twelve"}


def rows_by_owner() -> dict[str, list[tuple[str, str]]]:
    out: dict[str, list[tuple[str, str]]] = {}
    for row in csv.DictReader(t.SWEEP.open(encoding="utf-8", newline="")):
        if int(row["unit"]) > t.MAX_UNIT or row["owner.field"].endswith(".reviewStatus"):
            continue
        owner = row["owner.field"].split(".", 1)[0]
        new = t.ADJUSTED.get((row["owner.field"], row["current text"]), row["suggested rewrite"])
        out.setdefault(owner, []).append((row["current text"], new))
    return out


ROWS = rows_by_owner()


def rewrite(owner: str | None, sentence: str) -> str:
    owners = [owner] if owner else list(ROWS)
    for o in owners:
        for old, new in ROWS.get(o, []):
            if old in sentence:
                sentence = sentence.replace(old, new)
    return t.fix(sentence)


def main() -> None:
    pack = loads(t.ASSET.read_text(encoding="utf-8"))
    texts: dict[str, str] = {}
    for u in pack["units"]:
        for se in u["sessions"]:
            for st in se["steps"]:
                texts[st["id"]] = st.get("body") or ""
    for i in pack["items"]:
        texts[i["id"]] = " ".join([i.get("feedback") or ""] + i.get("distractorRationales", []))
    facts = {h["id"]: h for h in pack["items"]}
    facts.update({st["id"]: st for u in pack["units"] for se in u["sessions"] for st in se["steps"]})
    log = []
    for n in range(6, 13):
        path = RES / f"unit{n}-fact-dispositions.json"
        data = json.loads(path.read_text(encoding="utf-8"))
        for e in data["entries"]:
            owner = e["ownerId"]
            if e["disposition"] in ("MARK", "MARKED"):
                old = e.get("currentSentence", e["baselineSentence"])
                new = rewrite(owner, old)
                if new != old:
                    assert new in texts[owner] and new in facts[owner]["russianFacts"], (owner, new)
                    e["currentSentence"] = new
                    e["reason"] += f" T16-n2 (D31c): marked wording now \"{new}\" (was \"{old}\"); the same frozen records."
                    log.append((n, owner, "currentSentence", old, new))
            elif e["disposition"] == "REWORD":
                old = e["replacement"]
                new = rewrite(owner, old)
                if new != old:
                    assert new in texts[owner], (owner, new)
                    if e["replacementKind"] == "MARKED":
                        assert new in facts[owner]["russianFacts"], (owner, new)
                    e["replacement"] = new
                    e["reason"] += f" T16-n2 (D31c): replacement now \"{new}\" (was \"{old}\")."
                    log.append((n, owner, "replacement", old, new))
        for r in data.get("markerReviews", []):
            old = r["sentence"]
            new = rewrite(r["ownerId"], old)
            if new != old:
                assert new in facts[r["ownerId"]]["russianFacts"], (r["ownerId"], new)
                r["sentence"] = new
                log.append((n, r["ownerId"], "markerReview", old, new))
        path.write_text(json.dumps(data, ensure_ascii=False, indent=1) + "\n", encoding="utf-8")

        test = TESTS / f"Unit{n}FactProvenanceTest.kt"
        src = test.read_text(encoding="utf-8")
        block = re.search(r"private val reviewedNonFacts = setOf\((.*?)\n    \)", src, re.S)
        if block:
            body = block.group(1)
            new_body = body
            for m in re.finditer(r'^(\s*)"((?:[^"\\]|\\.)*)",[ \t]*$', body, re.M):
                old = m.group(2)
                new = rewrite(None, old)
                if new != old:
                    new_body = new_body.replace(m.group(0), f'{m.group(1)}"{new}", // T16-n2 (D31c): was "{old}"', 1)
                    log.append((n, "reviewedNonFacts", "entry", old, new))
            src = src.replace(body, new_body, 1)
            test.write_text(src, encoding="utf-8")
    for row in log:
        print(" | ".join(map(str, row)))


if __name__ == "__main__":
    main()
