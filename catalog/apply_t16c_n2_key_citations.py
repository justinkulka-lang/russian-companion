"""T16c-n2 item 10 (owner decisions D39b, D40a, D42a): every typed item cites the frozen cell or line its key comes from.

- keyCitation (on the item) names one frozen-source provenance record whose shipped text is the key, and the key occurs as a
  whole word with its exact stress in that record's frozen line under the validator's display views (raw line, OpenRussian
  stress apostrophe as the stress mark, Wikibooks bold stress, wiki links and lang templates unfolded, inline HTML joined).
- D42a: only where the prompt asks for the word as it starts a question ("Type the question word that has this meaning.") may
  the key differ from the cited text, and only in the case of its first letter.
- D40a: u1-i-type-й, -ъ and -ь cite their letter cells on the frozen alphabet page (russian-alphabet-1371795481.txt, the
  letter-table rows at lines 59-171); the key must occur exactly in that cell.
- Writes app/src/test/resources/typed-key-citations.tsv, the cross-unit pin of (item, prompt, key, citation).

Stops (exit 1) on any typed item without an exact citation. Exact-baseline: refuses to run twice.
"""
import hashlib
import html
import pathlib
import re
import sys

sys.path.insert(0, str(pathlib.Path(__file__).resolve().parent))
import apply_d7b_selection_records as d7b  # noqa: E402
from curriculum_storage import dumps, loads  # noqa: E402

ROOT = pathlib.Path(__file__).resolve().parent.parent
PACK = ROOT / "app/src/main/assets/content/v2-curriculum.json"
ALPHABET = ROOT / "catalog/sources/units-26-30/wikipedia/russian-alphabet-1371795481.txt"
PINS = ROOT / "app/src/test/resources/typed-key-citations.tsv"
AC = "́"
FIRST_LETTER_CASE_PROMPTS = {"Type the question word that has this meaning."}
LETTER_LINES = {"u1-i-type-й": 94, "u1-i-type-ъ": 162, "u1-i-type-ь": 170}


def sha(text: str) -> str:
    return hashlib.sha256(text.encode("utf-8")).hexdigest()


def views(original: str) -> list[str]:
    """The validator's display views, without folding stress or case (V2Validator.keyDisplayedExactly)."""
    out = [original, re.sub("(?<=[Ѐ-ӿ])'(?!')", AC, original), d7b.format_wikibooks_emphasis(original)]
    unfolded = re.sub(r"\{\{lang\|ru\|([^{}]*)\}\}", r"\1", original)
    unfolded = re.sub(r"\[\[([^\[\]]+)\]\]", lambda m: m.group(1).split("|")[-1], unfolded)
    out.append(d7b.format_wikibooks_emphasis(unfolded))
    visible = re.sub(r"(?i)<br\s*/?>", "\n", original)
    out.append(html.unescape(re.sub(r"<[^>]+>", "", visible)))
    return out


def variants(item: dict) -> list[str]:
    key = item["answer"]
    if item["prompt"] in FIRST_LETTER_CASE_PROMPTS and key:
        return list(dict.fromkeys([key, key[0].lower() + key[1:], key[0].upper() + key[1:]]))
    return [key]


def exact(record: dict, item: dict) -> bool:
    return record["shippedText"] == item["answer"] and any(
        d7b.contains_whole(view, v) for view in views(record["originalSourceText"]) for v in variants(item))


def main() -> None:
    pack = loads(PACK.read_text(encoding="utf-8"))
    typed = [i for i in pack["items"] if i.get("typedAnswerKey")]
    assert typed and not any("keyCitation" in i for i in typed), "key citations already applied"
    unit_of = {o["id"]: u["number"] for u in pack["units"] for o in u["objectives"]}
    frozen = [r for r in pack["provenanceRecords"] if d7b.frozen_record(pack, r)]

    # D40a: the three letter items get selection records on their letter cells.
    lines = ALPHABET.read_text(encoding="utf-8").split("\n")
    template = next(r for r in pack["provenanceRecords"] if r["itemId"] == "prov-t15g-u1-name-01")
    for item_id, number in LETTER_LINES.items():
        item = next(i for i in typed if i["id"] == item_id)
        line = lines[number - 1]
        assert 59 <= number <= 171 and d7b.contains_whole(line, item["answer"]), (item_id, line)
        record = dict(template)
        record.update(itemId=f"prov-t16cn2-key-{item_id}", objectiveId=item["objectiveId"], unitNumber=unit_of[item["objectiveId"]],
                      sourceRecordId="alphabet:" + line[:80], shippedText=item["answer"], originalSourceText=line,
                      sourceHash=sha(line), contentHash=sha(item["answer"]))
        pack["provenanceRecords"].append(record)
        frozen.append(record)

    missing, rows = [], []
    for item in typed:
        unit = unit_of[item["objectiveId"]]
        candidates = [r for r in frozen if r["unitNumber"] <= unit and exact(r, item)]
        if not candidates:
            missing.append(item["id"])
            continue
        candidates.sort(key=lambda r: (r["objectiveId"] != item["objectiveId"], r["sourceId"] != item["sourceId"],
                                       r["unitNumber"] != unit, -r["unitNumber"], r["itemId"]))
        chosen = candidates[0]
        ordered = {}
        for k, v in item.items():
            ordered[k] = v
            if k == "typedAnswerKey":
                ordered["keyCitation"] = chosen["itemId"]
        item.clear()
        item.update(ordered)
        rows.append((item["id"], item["prompt"], item["answer"], chosen["itemId"], chosen["sourceId"], chosen["sourceRecordId"]))
    if missing:
        print("STOP: typed items without an exact citation:", missing)
        sys.exit(1)

    PACK.write_text(dumps(pack), encoding="utf-8")
    clean = lambda s: s.replace("\t", " ").replace("\n", "\\n")  # noqa: E731
    PINS.write_text("item\tprompt\tkey\tcitation\tsourceId\tsourceRecordId\n" +
                    "".join("\t".join(clean(x) for x in row) + "\n" for row in rows), encoding="utf-8")
    print(len(rows), "typed items cited")


if __name__ == "__main__":
    main()
