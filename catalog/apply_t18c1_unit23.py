#!/usr/bin/env python3
"""T18c-1 (owner decision D57a): Unit 23 completion objective, one bounded intake attempt; outcome path B.

The one intake attempt (2026-11-27, MediaWiki API, current revisions only):
- ru.wikipedia "Вид глагола": revid 4372581 (sha1 1fdb908b89934a0edce5dc999dcd7f2a27dc2b04, 45 bytes) is a redirect to another
  page; it contains no sentence. Following the redirect would fetch another page, which D57a does not allow.
- en.wiktionary "за": revid 94001837 (sha1 08621b99654399a0eeb5aaf199a2061e0d88da1a, 39,181 bytes). Its Russian section has time
  phrases (for example the "of time" sense with "in three days"), but no sentence with a perfective verb and за + a time span,
  and no contrasting imperfective sentence with a bare-accusative duration.
Neither page is admitted and nothing is frozen (path B). obj-u23-completion is retired from the app: its three unattested
sentences, its two required checks and guided item, its teaching and remediation cards (and the check and guided steps that
carried them), its two examples, its source capability (cap-u23-completion-over-period) and every provenance record bound to
it (its Phase 2 synthesis records and their content-audit coverage) leave the pack. The session that held them keeps the
unit's AnkiDroid, binder, tutor and cumulative-review directions. The duration objective and its anchors are not touched.
No Russian is constructed, translated or edited. Every removal is logged in audit/T18c-1-unit23-changes.csv.
"""
from __future__ import annotations

import csv
import hashlib
import json
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / "catalog"))
from curriculum_storage import loads, encode  # noqa: E402

ASSET = ROOT / "app/src/main/assets/content/v2-curriculum.json"
LOG = ROOT / "audit/T18c-1-unit23-changes.csv"
BASELINE_SHA256 = "5853ecf214bd8d185a512fb0e5cf75fa85536acdde930acb1e17f5506da2adff"  # the T18b pack (a1e9d21e)
TASK = "T18c-1"
OBJECTIVE = "obj-u23-completion"
SESSION = "u23-s-gap-completion"
STEPS = {"u23-st-teach-completion-1", "u23-st-teach-completion-2", "u23-st-guided-completion-1", "u23-st-rem-completion",
         "u23-st-check-completion"}
ITEMS = {"u23-g-completion-1", "u23-i-completion-1", "u23-i-completion-2"}
EXAMPLES = {"ex-u23-completion-0", "ex-u23-completion-1"}
CAPABILITY = "cap-u23-completion-over-period"
SESSION_TITLE = "Unit 23 directions"
TOPIC_OLD = "Admitted time-expression spans and an original duration-versus-completion contrast await qualified review."
TOPIC_NEW = "Admitted time-expression spans await qualified review."


def sha(text: str) -> str:
    return hashlib.sha256(text.encode("utf-8")).hexdigest()


def apply(pack: dict) -> list[dict]:
    log: list[dict] = []

    def removed(kind: str, ident: str, text: str = "") -> None:
        log.append({"task": TASK, "finding": "D57a path B", "owner.field": f"{kind}:{ident}", "old": text, "new": "REMOVED"})

    unit = next(u for u in pack["units"] if u["number"] == 23)
    objective = next(o for o in unit["objectives"] if o["id"] == OBJECTIVE)
    if set(objective["checkItemIds"]) | set(i for s in unit["sessions"] for st in s["steps"] if st["id"] in STEPS for i in st["itemIds"]) != ITEMS:
        raise SystemExit("the completion objective's items changed")
    unit["objectives"] = [o for o in unit["objectives"] if o["id"] != OBJECTIVE]
    removed("objective", OBJECTIVE, objective["title"])
    session = next(s for s in unit["sessions"] if s["id"] == SESSION)
    for step in session["steps"]:
        if step["id"] in STEPS:
            removed("step", step["id"], step["body"])
    session["steps"] = [st for st in session["steps"] if st["id"] not in STEPS]
    if any(st["kind"] not in ("ANKI", "BINDER", "TUTOR", "ORIENT") for st in session["steps"]):
        raise SystemExit("the completion session holds other teaching")
    session["objectiveIds"] = [o for o in session["objectiveIds"] if o != OBJECTIVE]
    log.append({"task": TASK, "finding": "D57a path B", "owner.field": f"{SESSION}.title", "old": session["title"], "new": SESSION_TITLE})
    session["title"] = SESSION_TITLE
    for se in unit["sessions"]:
        if OBJECTIVE in se["objectiveIds"]:
            raise SystemExit(f"{se['id']} still names the retired objective")
    if TOPIC_OLD not in unit["topicSummary"]:
        raise SystemExit("topicSummary changed")
    old = unit["topicSummary"]
    unit["topicSummary"] = old.replace(TOPIC_OLD, TOPIC_NEW)
    log.append({"task": TASK, "finding": "D57a path B", "owner.field": "unit-23.topicSummary", "old": old, "new": unit["topicSummary"]})
    for item in [i for i in pack["items"] if i["id"] in ITEMS]:
        removed("item", item["id"], item["prompt"] + " | " + item.get("promptLetter", "") + " | " + item["answer"])
    pack["items"] = [i for i in pack["items"] if i["id"] not in ITEMS]
    for e in [e for e in pack["examples"] if e["id"] in EXAMPLES]:
        removed("example", e["id"], e["display"] + " | " + (e.get("note") or ""))
    pack["examples"] = [e for e in pack["examples"] if e["id"] not in EXAMPLES]
    if [e for e in pack["examples"] if e.get("objectiveId") == OBJECTIVE]:
        raise SystemExit("an example still names the retired objective")
    caps = [c for c in pack["sourceCapabilities"] if c["objectiveId"] == OBJECTIVE]
    if [c["id"] for c in caps] != [CAPABILITY]:
        raise SystemExit("the completion capability changed")
    removed("sourceCapability", CAPABILITY, caps[0]["supportedClaim"])
    pack["sourceCapabilities"] = [c for c in pack["sourceCapabilities"] if c["objectiveId"] != OBJECTIVE]
    records = [r for r in pack["provenanceRecords"] if r["objectiveId"] == OBJECTIVE]
    for r in records:
        removed("provenanceRecord", r["itemId"], r["shippedText"])
    pack["provenanceRecords"] = [r for r in pack["provenanceRecords"] if r["objectiveId"] != OBJECTIVE]
    for check in pack["contentChecks"]:
        if any(o["id"] == OBJECTIVE for o in check["objectives"]):
            raise SystemExit("a content check pins the retired objective")
    text = json.dumps(pack, ensure_ascii=False)
    for ident in [OBJECTIVE, CAPABILITY, *ITEMS, *STEPS, *EXAMPLES, "phase2:completion-over-period"]:
        if ident in text:
            raise SystemExit(f"{ident} is still in the pack")
    return log


def main() -> None:
    text = ASSET.read_text(encoding="utf-8")
    if sha(text) != BASELINE_SHA256:
        raise SystemExit("Asset is not the T18c-1 baseline; this transformer runs only once")
    original_order = list(json.loads(text).keys())
    pack = loads(text)
    log = apply(pack)
    encoded = encode(pack)
    ordered = {k: encoded[k] for k in original_order if k in encoded}
    ordered.update({k: v for k, v in encoded.items() if k not in ordered})
    out = json.dumps(ordered, ensure_ascii=False, separators=(",", ":")) + "\n"
    if "--dry" in sys.argv:
        print(f"dry run; {len(log)} changes; asset not written")
        return
    ASSET.write_text(out, encoding="utf-8")
    with LOG.open("w", encoding="utf-8", newline="") as f:
        w = csv.DictWriter(f, fieldnames=["task", "finding", "owner.field", "old", "new"], lineterminator="\n")
        w.writeheader()
        w.writerows(log)
    print(f"{len(log)} changes; wrote {ASSET.relative_to(ROOT)} sha256={sha(ASSET.read_text(encoding='utf-8'))}")


if __name__ == "__main__":
    main()
