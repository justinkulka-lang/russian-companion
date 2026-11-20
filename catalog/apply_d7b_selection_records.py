"""D7b: frozen selection records for Russian that only synthesis records admitted (owner decisions D18a, D19a).

Exact-baseline transformer. Provenance only: no learner text changes.

V2Validator now counts Russian coverage only from frozen-file records and the D7b allowlist. Each learner-visible
Cyrillic region (the validator's CYRILLIC_REGION, unit by unit) that loses its only admitting record and is not exempt
under D18b gets one selection record:
  - originalSourceText: one line of a frozen file under catalog/sources/, verbatim;
  - shippedText: the region exactly as the learner sees it;
  - the region passes the two-part phrase rule against that single line (D19a): case-insensitive presence as whole
    words, with the line read as displayed (inline HTML joined, piped links resolved, Wikibooks bold stress formatted,
    OpenRussian apostrophes as acutes), ё and е kept distinct; every word the learner sees with a stress mark has that
    exact stress in the same line.
Source identity (sourceId, revision, URL, licence, attribution) is copied from an existing frozen-source record already
bound to the same frozen file, so FrozenSourceBindingTest binds the new record through the same FREEZE.json entry.
A region with no qualifying line stops the transformer (report, do not invent).
"""
from __future__ import annotations

import copy
import csv
import hashlib
import json
import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / "catalog"))
from curriculum_storage import loads, encode  # noqa: E402
from units_3_5_intake import format_wikibooks_emphasis  # noqa: E402

ASSET = ROOT / "app/src/main/assets/content/v2-curriculum.json"
ALLOWLIST = ROOT / "audit/d7b-phrase-allowlist.txt"
SOURCES = ROOT / "catalog/sources"
BASELINE_SHA256 = "fe83e677d60617eb767fab29313c7cd4d7a3088f897618e8b220029c5a5f6cab"

CYR = "Ѐ-ӿ"
AC = "́"
REGION = re.compile(f"[{CYR}{AC}](?:[{CYR}{AC}]|[ \\t]+(?=[{CYR}]))*[?!.,]*")
FORM = re.compile(f"[{CYR}][{CYR}{AC}]*")
PIPED = re.compile(r"\[\[[^\[\]|]*\|([^\[\]|]*)\]\]")
# T15h (D25c): every HTML tag is a boundary except the fixed inline list (same list as TwoPartPhraseRule.INLINE_TAGS).
INLINE_TAGS = "span b i em strong a small sub sup u s abbr cite code mark q bdi bdo font wbr".split()
# T16 (D27a): only a complete inline name is inline (it must end at whitespace, "/" or ">"); span:probe is a boundary.
BLOCK = re.compile(r"(?i)</?(?!(?:" + "|".join(INLINE_TAGS) + r")(?=[\s/>]))[A-Za-z][^>]*>")
CONTENT_AUDIT = "src-content-audit-research"


def sha(text: str) -> str:
    return hashlib.sha256(text.encode("utf-8")).hexdigest()


def displayed(raw: str) -> str:
    """A frozen line as displayed: block tags separate, inline tags join, piped links read as display text,
    Wikibooks bold stress formatted, OpenRussian apostrophes as acutes."""
    text = re.sub("<[^>]+>", "", BLOCK.sub(" ", raw))
    text = PIPED.sub(r"\1", text)
    text = format_wikibooks_emphasis(text)
    text = re.sub(r"\[\[|\]\]", "", text)
    return re.sub(f"(?<=[{CYR}])'(?!')", AC, text)


def folded(text: str) -> str:
    return re.sub(r"[ \t\n   ]+", " ", text.replace(AC, "")).lower()


def two_part(line_display: str, region: str) -> bool:
    if not re.search(f"(?<![{CYR}]){re.escape(folded(region))}(?![{CYR}])", folded(line_display)):
        return False
    line_forms = {f.lower() for f in FORM.findall(line_display)}
    return all(f.lower() in line_forms for f in FORM.findall(region) if AC in f)


def frozen_record(pack: dict, r: dict) -> bool:
    """Mirror of V2Validator.isFrozenSourceRecord."""
    sources = {s["id"] for s in pack["sources"]}
    rights = {x["sourceId"] for x in pack["rights"] if x.get("permitsAppUse")}
    return (r["sourceId"] != CONTENT_AUDIT and r.get("licenseId") != "Original app text"
            and r["transformation"] != "content-audit research synthesis" and r.get("synthesis") is None
            and not r["sourceRecordId"].startswith("ca-coverage:") and r.get("sourceRevision", "").strip() != ""
            and r["sourceId"].strip() != "" and r["sourceRecordId"].strip() != "" and r["originalSourceText"].strip() != ""
            and r["sourceHash"] == sha(r["originalSourceText"]) and r["sourceId"] in sources and r["sourceId"] in rights)


def allowlist() -> list[tuple[int, str]]:
    rows = []
    for line in ALLOWLIST.read_text(encoding="utf-8").split("\n"):
        if line.strip():
            unit, _owner, text, _kind = line.split(" | ")
            rows.append((int(unit), text.strip()))
    return rows


def exempt(rows, unit: int, region: str) -> bool:
    word = re.compile(f"(?<![{CYR}{AC}]){re.escape(region)}(?![{CYR}{AC}])")
    return any(u == unit and (t == region or word.search(t)) for u, t in rows)


def hosts(pack: dict, unit: dict) -> list[tuple[str, str]]:
    """(objectiveId, text) in V2Validator.validateProvenance order."""
    examples = {e["id"]: e for e in pack["examples"]}
    items = {i["id"]: i for i in pack["items"]}
    refs = {r["id"]: r for r in pack["references"]}
    first = unit["objectives"][0]["id"]
    step_objective = {}
    for o in unit["objectives"]:
        for s in o.get("instructionStepIds", []) + o.get("remediationStepIds", []) + o.get("practiceStepIds", []):
            step_objective.setdefault(s, o["id"])
    out = [(first, unit["topicSummary"]), (first, unit.get("mappingNote") or ""), (first, unit["title"])]

    def item_hosts(item_id: str):
        i = items.get(item_id)
        if not i:
            return
        texts = [i["prompt"], i.get("promptLetter") or "", i["answer"]] + ((i.get("typedAnswerKey") or {}).get("acceptedAnswers") or [])
        texts += [i["feedback"]] + i.get("options", []) + i.get("distractorRationales", [])
        a = i.get("assessment")
        if a:
            texts += [a.get("whyAnswerDemonstratesObjective") or ""] + (a.get("distractorDifference") or [])
        out.extend((i["objectiveId"], t) for t in texts)

    for o in unit["objectives"]:
        out.append((o["id"], o["title"]))
        for e in o.get("exampleIds", []):
            if e in examples:
                out += [(o["id"], examples[e]["display"]), (o["id"], examples[e].get("note", ""))]
        for c in o.get("checkItemIds", []):
            item_hosts(c)
    for session in unit["sessions"]:
        for step in session["steps"]:
            obj = step_objective.get(step["id"], first)
            for f in ("title", "body", "bookDirection", "companionDirection", "unavailableReason"):
                out.append((obj, step.get(f) or ""))
            for e in step.get("exampleIds") or []:
                if e in examples:
                    out += [(obj, examples[e]["display"]), (obj, examples[e].get("note", ""))]
            for c in step.get("itemIds") or []:
                item_hosts(c)
            for r in step.get("referenceIds") or []:
                if r in refs:
                    out += [(obj, refs[r]["title"]), (obj, refs[r]["summary"]), (obj, refs[r]["detail"])]
    return out


def needed_regions(pack: dict) -> list[tuple[int, str, str]]:
    """(unit, region, objective) that the D7b rule leaves uncovered, first occurrence order."""
    rows = allowlist()
    frozen = {}
    with_records = set()
    for r in pack["provenanceRecords"]:
        with_records.add(r["unitNumber"])
        if frozen_record(pack, r):
            frozen.setdefault(r["unitNumber"], set()).add(r["shippedText"])
    letters = {x for l in pack["letters"] if l.get("sourceId") == "src-unicode-cyrillic" for x in (l["uppercase"], l["lowercase"])}
    out, seen = [], set()
    for unit in pack["units"]:
        n = unit["number"]
        if n not in with_records:
            continue
        for objective, host in hosts(pack, unit):
            for m in REGION.finditer(host):
                text = m.group(0).strip()
                trimmed = text.rstrip("?!.,")
                if text in frozen.get(n, set()) or trimmed in frozen.get(n, set()) or exempt(rows, n, trimmed):
                    continue
                parts = [p for p in re.split(r"[ \t]+", trimmed) if p]
                if n <= 2 and parts and all(p in letters for p in parts):
                    continue
                if (n, trimmed) not in seen:
                    seen.add((n, trimmed))
                    out.append((n, trimmed, objective))
    return out


def frozen_lines() -> list[tuple[str, int, str, str]]:
    out = []
    for f in sorted(SOURCES.rglob("*")):
        if f.is_file() and f.suffix in (".txt", ".csv", ".html"):
            rel = str(f.relative_to(SOURCES))
            for i, raw in enumerate(f.read_text(encoding="utf-8").split("\n"), 1):
                if re.search(f"[{CYR}]", raw):
                    out.append((rel, i, raw, displayed(raw)))
    return out


def templates(pack: dict) -> dict[str, dict]:
    """Frozen file -> an existing frozen-source record bound to it (same source, revision and file)."""
    texts = {str(f.relative_to(SOURCES)): f.read_text(encoding="utf-8")
             for f in SOURCES.rglob("*") if f.is_file() and f.suffix in (".txt", ".csv", ".html")}
    out: dict[str, dict] = {}
    for r in pack["provenanceRecords"]:
        if not frozen_record(pack, r) or r["transformation"] == "readyrussian text extraction":
            continue
        if r["sourceId"].startswith("src-openrussian-"):
            name = r["sourceRecordId"].split(":", 1)[0]
            rel = next((k for k in texts if k.endswith("/openrussian/" + name)), None)
            if rel and r["originalSourceText"] in texts[rel]:
                out.setdefault(rel, r)
            continue
        hits = [k for k, t in texts.items() if r["originalSourceText"] in t]
        if len(hits) == 1:
            out.setdefault(hits[0], r)
    return out


def main() -> None:
    text = ASSET.read_text(encoding="utf-8")
    if sha(text) != BASELINE_SHA256:
        raise SystemExit("Asset is not the D7b baseline; this transformer runs only once")
    original_order = list(json.loads(text).keys())
    pack = loads(text)
    regions = needed_regions(pack)
    lines = frozen_lines()
    tmpl = templates(pack)
    unit_sources = {}
    for r in pack["provenanceRecords"]:
        if frozen_record(pack, r):
            unit_sources.setdefault(r["unitNumber"], set()).add(r["sourceId"])
    report, missing = [], []
    for k, (n, region, objective) in enumerate(regions, 1):
        candidates = [(rel, i, raw, d) for rel, i, raw, d in lines if rel in tmpl and two_part(d, region)]
        if not candidates:
            missing.append((n, region))
            continue

        def rank(c):
            rel, i, raw, d = c
            exact_case = re.search(f"(?<![{CYR}{AC}]){re.escape(region)}(?![{CYR}{AC}])", d) is not None
            return (tmpl[rel]["sourceId"] not in unit_sources.get(n, set()), not exact_case, rel, i)

        rel, i, raw, d = sorted(candidates, key=rank)[0]
        t = tmpl[rel]
        record = copy.deepcopy(t)
        prefix = t["sourceRecordId"].split(":", 1)[0]
        record_id = f"prov-d7b-u{n}-{k:03d}"
        record.update({"itemId": record_id, "unitNumber": n, "objectiveId": objective, "recordType": "lexical record",
                       "sourceRecordId": f"{prefix}:{raw[:80]}", "originalSourceText": raw, "shippedText": region,
                       "transformation": "selection", "sourceHash": sha(raw), "contentHash": sha(region)})
        if t["sourceId"].startswith("src-openrussian-"):
            record["sourceRecordId"] = f"{prefix}:{raw.split(chr(9), 1)[0]}"
        if 26 <= n <= 30 and t["sourceId"].startswith("src-wikibooks-") and "Wikibooks:Copyrights oldid 4622060" not in t["attribution"]:
            # Units 26-30 name the Wikibooks licence route (V2Validator); reuse that block's wording for this source.
            routed = next(r for r in pack["provenanceRecords"] if r["sourceId"] == t["sourceId"] and 26 <= r["unitNumber"] <= 30
                          and frozen_record(pack, r) and "Wikibooks:Copyrights oldid 4622060" in r["attribution"])
            record["attribution"] = routed["attribution"]
            record["shareAlikeTreatment"] = routed["shareAlikeTreatment"]
        pack["provenanceRecords"].append(record)
        report.append([n, region, record_id, f"catalog/sources/{rel}:{i}", t["sourceId"]])
    if missing:
        raise SystemExit(f"No single frozen line passes the two-part rule for: {missing}")
    encoded = encode(pack)
    ordered = {key: encoded[key] for key in original_order if key in encoded}
    ordered.update({key: v for key, v in encoded.items() if key not in ordered})
    ASSET.write_text(json.dumps(ordered, ensure_ascii=False, separators=(",", ":")) + "\n", encoding="utf-8")
    if len(sys.argv) > 1:
        with open(sys.argv[1], "w", newline="", encoding="utf-8") as fh:
            w = csv.writer(fh)
            w.writerow(["unit", "region", "record_id", "frozen_file_line", "source_id"])
            w.writerows(report)
    print(f"{len(report)} records; wrote {ASSET.relative_to(ROOT)} sha256={sha(ASSET.read_text(encoding='utf-8'))}")


if __name__ == "__main__":
    main()
