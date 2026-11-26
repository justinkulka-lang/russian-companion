"""T16c-n3 item 1 (owner decision D46b): every accepted answer of a typed item meets the key's citation rule.

- The key (item.answer) is cited by keyCitation (T16c-n2). Every other entry of typedAnswerKey.acceptedAnswers gets its own
  frozen-source record in item.acceptedAnswerCitations: the record ships that exact text, and the text occurs as a whole word
  with its exact stress in the record's frozen line under the validator's display views (same rule and helpers as the key;
  first-letter case only where the prompt asks for a question word, D42a).
- Audit: prints every typed item whose accepted answers differ from its key, with the chosen citation. Stops (exit 1) on any
  accepted answer without an exact frozen citation; such an answer is a content defect to fix by hand, not to cite loosely.
- Rewrites app/src/test/resources/typed-key-citations.tsv with one row per cited text (key rows unchanged, accepted-answer
  rows added). Exact-baseline: refuses to run twice.
"""
import pathlib
import sys

sys.path.insert(0, str(pathlib.Path(__file__).resolve().parent))
import apply_d7b_selection_records as d7b  # noqa: E402
import apply_t16c_n2_key_citations as n2  # noqa: E402
from curriculum_storage import dumps, loads  # noqa: E402

PACK, PINS = n2.PACK, n2.PINS


def exact(record: dict, item: dict, text: str) -> bool:
    shown = [text]
    if item["prompt"] in n2.FIRST_LETTER_CASE_PROMPTS and text:
        shown = list(dict.fromkeys([text, text[0].lower() + text[1:], text[0].upper() + text[1:]]))
    return record["shippedText"] == text and any(d7b.contains_whole(v, s) for v in n2.views(record["originalSourceText"]) for s in shown)


def main() -> None:
    pack = loads(PACK.read_text(encoding="utf-8"))
    typed = [i for i in pack["items"] if i.get("typedAnswerKey")]
    assert typed and not any("acceptedAnswerCitations" in i for i in typed), "accepted-answer citations already applied"
    unit_of = {o["id"]: u["number"] for u in pack["units"] for o in u["objectives"]}
    frozen = [r for r in pack["provenanceRecords"] if d7b.frozen_record(pack, r)]
    by_id = {r["itemId"]: r for r in pack["provenanceRecords"]}
    missing, rows = [], []
    for item in typed:
        cited = by_id[item["keyCitation"]]
        rows.append((item["id"], item["prompt"], item["answer"], cited["itemId"], cited["sourceId"], cited["sourceRecordId"]))
        extra = [a for a in item["typedAnswerKey"]["acceptedAnswers"] if a != item["answer"]]
        if not extra:
            continue
        unit = unit_of[item["objectiveId"]]
        citations = {}
        for answer in extra:
            candidates = [r for r in frozen if r["unitNumber"] <= unit and exact(r, item, answer)]
            if not candidates:
                missing.append((item["id"], answer))
                continue
            candidates.sort(key=lambda r: (r["objectiveId"] != item["objectiveId"], r["sourceId"] != item["sourceId"],
                                           r["unitNumber"] != unit, -r["unitNumber"], r["itemId"]))
            chosen = candidates[0]
            citations[answer] = chosen["itemId"]
            rows.append((item["id"], item["prompt"], answer, chosen["itemId"], chosen["sourceId"], chosen["sourceRecordId"]))
            print(f"{item['id']}: key {item['answer']!r} accepts {answer!r} -> {chosen['itemId']} ({chosen['sourceId']}): "
                  f"{chosen['originalSourceText'][:70]!r}")
        ordered = {}
        for k, v in item.items():
            ordered[k] = v
            if k == "keyCitation":
                ordered["acceptedAnswerCitations"] = citations
        item.clear()
        item.update(ordered)
    if missing:
        print("STOP: accepted answers without an exact frozen citation:", missing)
        sys.exit(1)
    PACK.write_text(dumps(pack), encoding="utf-8")
    clean = lambda s: s.replace("\t", " ").replace("\n", "\\n")  # noqa: E731
    PINS.write_text("item\tprompt\tkey\tcitation\tsourceId\tsourceRecordId\n" +
                    "".join("\t".join(clean(x) for x in row) + "\n" for row in rows), encoding="utf-8")
    print(len(rows), "cited texts pinned;", sum(1 for i in typed if i.get("acceptedAnswerCitations")), "items with accepted-answer citations")


if __name__ == "__main__":
    main()
