"""T15h content (owner decisions D25b, D25g, D25i). Exact-baseline transformer; runs once on the S1 pack.

- D25b: the four Unit 6 English sentences "In мой стол, стол is ..." / "In моя собака, собака is ..." put English between
  the comma and the repeated noun ("In мой стол, the noun стол is ..."). No Russian changes.
- D25g: prov-t15g-u10-107 (его) is re-cited to the pronoun passage genitive-4567338.txt:141 and prov-t15g-u10-116 (ей) to
  the feminine adjective ending at genitive-4567338.txt:121, each passing D19a. The adjective-ending sentence of
  ref-u10-genitive covers soft adjectives (adjectives-4292379.txt:45, 47). Its Russian tokens are unchanged.
- D25i: Unit 10 regains a possession counterexample for obj-u10-absence: u10-i-absence-5 shows the frozen sentence
  У него есть дом (personal-pronouns-3598527.txt:28, glossed "He has a house") with the possession explanation as its key
  and the absence explanation as the distractor. It is built from the retired u10-i-absence-3 (same options, feedback
  pattern and rule records) with only the cue replaced, plus a frozen selection record for the cue.
- D25e (owner decision 2026-11-20): the offline-notice attributions of Обособление and Больтраффио equal the attribution
  recorded in catalog/sources/s1-intake/FREEZE.json; the appended process notes leave the learner-facing notice.
"""
from __future__ import annotations

import copy
import hashlib
import json
import subprocess
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / "catalog"))
from curriculum_storage import loads, encode  # noqa: E402
import apply_d7b_selection_records as d7b  # noqa: E402

ASSET = ROOT / "app/src/main/assets/content/v2-curriculum.json"
BASELINE_SHA256 = "0441b5ba0dfe3e8abe41e82a165dba79de984a5f0c7b2932726bbb5cdb29f5cd"  # the S1 pack (cdfba75e)
RETIRED_FROM = "ee02c68e"  # last commit whose pack still holds u10-i-absence-3
GENITIVE = "units-6-10/wikibooks/genitive-4567338.txt"
PRONOUNS = "units-3-5/wikibooks/personal-pronouns-3598527.txt"
OLD_CUE, NEW_CUE = "Да, у меня есть сын.", "У него есть дом"
NEW_ITEM = "u10-i-absence-5"
UNIT6 = [
    ("In мой стол, стол is masculine singular.", "In мой стол, the noun стол is masculine singular."),
    ("In моя собака, собака is feminine singular.", "In моя собака, the noun собака is feminine singular."),
]
ADJECTIVE = ("masculine and neuter -ого (-его after a hushing consonant), feminine -ой (-ей)",
             "masculine and neuter -ого (-его for soft adjectives and after a hushing consonant), feminine -ой (-ей for soft "
             "adjectives and after a hushing consonant)")
RECITE = [("prov-t15g-u10-107", GENITIVE, 141), ("prov-t15g-u10-116", GENITIVE, 121)]


def sha(text: str) -> str:
    return hashlib.sha256(text.encode("utf-8")).hexdigest()


def line(rel: str, n: int) -> str:
    return (d7b.SOURCES / rel).read_text(encoding="utf-8").split("\n")[n - 1]


def bound(pack: dict, tmpl: dict, base: dict, rel: str, n: int) -> dict:
    """base re-cited to rel:n with the binding copied from an existing frozen record of that file (apply_t15g_recite)."""
    raw = line(rel, n)
    if not d7b.two_part(d7b.displayed(raw), base["shippedText"]):
        raise SystemExit(f"{base['itemId']}: {base['shippedText']} fails D19a against {rel}:{n}")
    t = tmpl[rel]
    prefix = t["sourceRecordId"].split(":", 1)[0]
    r = copy.deepcopy(t)
    r.update({k: base[k] for k in ("itemId", "unitNumber", "objectiveId", "shippedText", "contentHash", "recordType")})
    r.update({"originalSourceText": raw, "sourceHash": sha(raw), "transformation": "selection", "sourceRecordId": f"{prefix}:{raw[:80]}"})
    if not d7b.frozen_record(pack, r):
        raise SystemExit(f"{base['itemId']}: not a frozen-source record")
    return r


def swap(value):
    if isinstance(value, str):
        return value.replace(OLD_CUE, NEW_CUE).replace("u10-i-absence-3", NEW_ITEM).replace("u10-absence-3", "u10-absence-5")
    if isinstance(value, list):
        return [swap(v) for v in value]
    if isinstance(value, dict):
        return {swap(k): swap(v) for k, v in value.items()}
    return value


def main() -> None:
    text = ASSET.read_text(encoding="utf-8")
    if sha(text) != BASELINE_SHA256:
        raise SystemExit("Asset is not the T15h baseline; this transformer runs only once")
    original_order = list(json.loads(text).keys())
    pack = loads(text)
    tmpl = d7b.templates(pack)
    units = {u["number"]: u for u in pack["units"]}
    steps = {st["id"]: st for u in pack["units"] for s in u["sessions"] for st in s["steps"]}
    # D25b
    for sid in ("u6-st-teach-possessive-1", "u6-st-rem-possessive"):
        for old, new in UNIT6:
            if steps[sid]["body"].count(old) != 1:
                raise SystemExit(f"{sid}: sentence not found once")
            steps[sid]["body"] = steps[sid]["body"].replace(old, new)
            print(f"D25b {sid}: {new}")
        facts = steps[sid].get("russianFacts") or []
        steps[sid]["russianFacts"] = [dict(UNIT6).get(f, f) for f in facts]
        sources = steps[sid].get("russianFactSources") or {}
        steps[sid]["russianFactSources"] = {dict(UNIT6).get(k, k): v for k, v in sources.items()}
    # The content-audit English coverage records that copy those sentences follow them.
    for r in pack["provenanceRecords"]:
        if r["sourceId"] != "src-content-audit-research":
            continue
        changed = False
        for key in ("shippedText", "originalSourceText"):
            for old, new in UNIT6:
                if old in r[key]:
                    r[key] = r[key].replace(old, new)
                    changed = True
        if changed:
            r["sourceHash"] = sha(r["originalSourceText"])
            r["contentHash"] = sha(r["shippedText"])
            print(f"D25b coverage record {r['itemId']}")
    # D25g
    records = {r["itemId"]: r for r in pack["provenanceRecords"]}
    for rid, rel, n in RECITE:
        new = bound(pack, tmpl, records[rid], rel, n)
        records[rid].clear()
        records[rid].update(new)
        print(f"D25g {rid}: {new['shippedText']} <- catalog/sources/{rel}:{n}")
    ref = next(r for r in pack["references"] if r["id"] == "ref-u10-genitive")
    if ref["detail"].count(ADJECTIVE[0]) != 1:
        raise SystemExit("ref-u10-genitive: adjective sentence not found once")
    ref["detail"] = ref["detail"].replace(*ADJECTIVE)
    # D25e
    freeze = json.loads((d7b.SOURCES / "s1-intake/FREEZE.json").read_text(encoding="utf-8"))
    recorded = {page["sourceId"]: page["attribution"] for page in freeze["pages"]}
    for source in pack["sources"]:
        if source["id"] in ("src-ruwiki-obosoblenie-144053197", "src-ruwiki-boltraffio-149826784"):
            source["attribution"] = recorded[source["id"]]
            print(f"D25e {source['id']}: attribution = FREEZE.json attribution")
    # D25i
    old_pack = loads(subprocess.check_output(["git", "show", f"{RETIRED_FROM}:app/src/main/assets/content/v2-curriculum.json"], cwd=ROOT).decode())
    retired = next(i for i in old_pack["items"] if i["id"] == "u10-i-absence-3")
    if any(i["id"] == NEW_ITEM for i in pack["items"]):
        raise SystemExit("u10-i-absence-5 already exists")
    cue_record = bound(pack, tmpl, {"itemId": "prov-t15h-u10-u-nego-est-dom", "unitNumber": 10, "objectiveId": "obj-u10-absence",
                                    "shippedText": NEW_CUE, "contentHash": sha(NEW_CUE), "recordType": "example"}, PRONOUNS, 28)
    pack["provenanceRecords"].append(cue_record)
    item = swap(copy.deepcopy(retired))
    item["russianFacts"] = [f"{NEW_CUE} uses the simple possession construction."] + retired["russianFacts"][2:]
    facts = retired["russianFactSources"]
    item["russianFactSources"] = {item["russianFacts"][0]: [cue_record["itemId"]] + facts["uses the simple possession construction."]}
    item["russianFactSources"].update({k: v for k, v in facts.items() if k in item["russianFacts"][1:]})
    pack["items"].append(item)
    for r in old_pack["provenanceRecords"]:
        if r["itemId"].startswith("prov-ca-rule-u10-i-absence-3-"):
            nr = swap(copy.deepcopy(r))
            if nr["sourceRecordId"] == f"ca-rule-context:{NEW_ITEM}":
                head, tuple_part = nr["originalSourceText"].rsplit("\nTuple-v1:", 1)
                a = nr["synthesis"]["applications"][0]
                def field(v: str) -> str:
                    return f"{len(v.encode('utf-8'))}:{v}"
                canonical = f"tuple-v1:1;{field(a['cue'])}{field(a['answer'])}{len(a['distractors'])};" + "".join(field(d) for d in a["distractors"])
                nr["originalSourceText"] = f"{head}\nTuple-v1:{canonical}"
            nr["sourceHash"] = sha(nr["originalSourceText"])
            nr["contentHash"] = sha(nr["shippedText"])
            pack["provenanceRecords"].append(nr)
    context = next(r for r in pack["provenanceRecords"] if r["itemId"] == f"prov-ca-rule-{NEW_ITEM}-0")
    objective = next(o for o in units[10]["objectives"] if o["id"] == "obj-u10-absence")
    objective["checkItemIds"].append(NEW_ITEM)
    steps["u10-st-check-absence"]["itemIds"].append(NEW_ITEM)
    print(f"D25i {NEW_ITEM}: cue {NEW_CUE} <- catalog/sources/{PRONOUNS}:28; context pin ca-rule-context:{NEW_ITEM} = {context['sourceHash']}")
    encoded = encode(pack)
    ordered = {k: encoded[k] for k in original_order if k in encoded}
    ordered.update({k: v for k, v in encoded.items() if k not in ordered})
    ASSET.write_text(json.dumps(ordered, ensure_ascii=False, separators=(",", ":")) + "\n", encoding="utf-8")
    print(f"wrote {ASSET.relative_to(ROOT)} sha256={sha(ASSET.read_text(encoding='utf-8'))}")


if __name__ == "__main__":
    main()
