"""S1 (owner decisions D7c, D24c): frozen selection records for Unit 24's не́кому and не́кем.

Exact-baseline transformer; runs once on the T15g D22 pack. S1 froze English Wiktionary "некого" (oldid 80719285) under
catalog/sources/s1-intake. Its declension table shows не́кому (dat_sg, line 30) and не́кем (ins_sg, line 31), so the D7b
audit no longer fails Unit 24's two stressed forms and their four allowlist lines leave the list (it only shrinks). The
validator admits Russian only through frozen-source records, so each form gets a selection record from its own line,
passing the D19a two-part rule, and the source gets its pack source and rights entries. Learner text does not change.
"""
from __future__ import annotations

import copy
import hashlib
import json
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / "catalog"))
from curriculum_storage import loads, encode  # noqa: E402
import apply_d7b_selection_records as d7b  # noqa: E402

ASSET = ROOT / "app/src/main/assets/content/v2-curriculum.json"
BASELINE_SHA256 = "5bdff2444d82243cd8191f12ab203d6224d7fbb98f9b568e5b149b568bffc7d3"  # the T15g D22 pack
REL = "s1-intake/wiktionary/nekogo-80719285.txt"
SOURCE_ID = "src-enwiktionary-nekogo-80719285"
OLDID = "80719285"
ATTRIBUTION = ("Wiktionary contributors, CC BY-SA 4.0, via Wiktionary:Copyrights oldid 87545419. Page history for oldid "
               f"{OLDID} identifies the contributors. Page text is dual-licensed CC BY-SA 4.0 and GFDL; this intake uses CC BY-SA 4.0. "
               "Audio is not admitted.")
RECORDS = [("prov-s1-u24-nekomu", 30, "не́кому"), ("prov-s1-u24-nekem", 31, "не́кем")]


def sha(text: str) -> str:
    return hashlib.sha256(text.encode("utf-8")).hexdigest()


def main() -> None:
    text = ASSET.read_text(encoding="utf-8")
    if sha(text) != BASELINE_SHA256:
        raise SystemExit("Asset is not the S1 baseline; this transformer runs only once")
    manifest = json.loads((d7b.SOURCES / "s1-intake/FREEZE.json").read_text(encoding="utf-8"))
    page = next(p for p in manifest["pages"] if p["sourceId"] == SOURCE_ID)
    frozen = (d7b.SOURCES / "s1-intake" / page["file"]).read_bytes()
    if hashlib.sha256(frozen).hexdigest() != page["sha256"] or page["oldid"] != OLDID:
        raise SystemExit("Frozen некого file does not match its manifest")
    original_order = list(json.loads(text).keys())
    pack = loads(text)
    if any(s["id"] == SOURCE_ID for s in pack["sources"]):
        raise SystemExit("Source already present")
    template = next(s for s in pack["sources"] if s["id"] == "src-enwiktionary-napisannyj-74228228")
    pack["sources"].append({**copy.deepcopy(template), "id": SOURCE_ID, "title": "некого", "attribution": ATTRIBUTION})
    pack["rights"].append({"id": f"rights-enwiktionary-nekogo-{OLDID}", "sourceId": SOURCE_ID, "license": "CC BY-SA 4.0", "permitsAppUse": True})
    model = next(r for r in pack["provenanceRecords"] if r["sourceId"] == "src-enwiktionary-napisannyj-74228228")
    lines = frozen.decode("utf-8").split("\n")
    for rid, line, region in RECORDS:
        raw = lines[line - 1]
        if not d7b.two_part(d7b.displayed(raw), region):
            raise SystemExit(f"{region} fails D19a against {REL}:{line}")
        r = copy.deepcopy(model)
        r.update({"itemId": rid, "unitNumber": 24, "objectiveId": "obj-u24-stand-ins", "recordType": "lexical record",
                  "sourceId": SOURCE_ID, "sourceUrl": f"https://en.wiktionary.org/w/index.php?title=некого&oldid={OLDID}",
                  "sourceRevision": OLDID, "sourceRecordId": f"nekogo:{raw[:80]}", "shippedText": region,
                  "transformation": "selection", "attribution": ATTRIBUTION, "originalSourceText": raw,
                  "sourceHash": sha(raw), "contentHash": sha(region)})
        pack["provenanceRecords"].append(r)
        if not d7b.frozen_record(pack, r):
            raise SystemExit(f"{rid} is not a frozen-source record")
        print(f"{rid}: {region} <- catalog/sources/{REL}:{line}")
    encoded = encode(pack)
    ordered = {k: encoded[k] for k in original_order if k in encoded}
    ordered.update({k: v for k, v in encoded.items() if k not in ordered})
    ASSET.write_text(json.dumps(ordered, ensure_ascii=False, separators=(",", ":")) + "\n", encoding="utf-8")
    print(f"wrote {ASSET.relative_to(ROOT)} sha256={sha(ASSET.read_text(encoding='utf-8'))}")


if __name__ == "__main__":
    main()
