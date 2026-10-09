#!/usr/bin/env python3
"""Explain the admitted declension-cell contrasts without altering Russian cells.

The bounded item list is retained in the audit register. This improves English
instruction; it does not certify the source, linguistic review, or installation.
"""
import csv
import hashlib
import json
from copy import deepcopy
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
PACK = ROOT / "app/src/main/assets/content/v2-curriculum.json"
REGISTER = ROOT / "audit/content-corrections/DECLENSION_EXPLANATION_TARGETS.tsv"
FIELDS = {
    "sg_nom": "singular nominative", "sg_acc": "singular accusative",
    "sg_gen": "singular genitive", "pl_nom": "plural nominative",
    "pl_acc": "plural accusative", "decl_m_nom": "masculine singular nominative",
    "decl_f_nom": "feminine singular nominative", "decl_n_nom": "neuter singular nominative",
    "decl_pl_nom": "plural nominative", "decl_f_acc": "feminine singular accusative",
    "decl_m_prep": "masculine singular prepositional",
    "decl_m_gen": "masculine singular genitive", "decl_f_gen": "feminine singular genitive",
    "decl_pl_gen": "plural genitive",
}


def repair(data, ids):
    records = {r["itemId"]: r for r in data["provenanceRecords"]}
    items = {i["id"]: i for i in data["items"]}
    unit_for_objective = {o["id"]: u["number"] for u in data["units"] for o in u["objectives"]}
    authored = {(r["unitNumber"], r["text"]) for r in data["authoredEnglishRecords"]}
    # Grammatical statements require provenance, rather than interface scaffolding.
    data["authoredEnglishRecords"] = [r for r in data["authoredEnglishRecords"]
                                      if not r["id"].startswith("en-ca-declension-")]
    authored = {(r["unitNumber"], r["text"]) for r in data["authoredEnglishRecords"]}
    template = next(r for r in data["provenanceRecords"]
                    if r["sourceId"] == "src-content-audit-research" and r.get("synthesis")
                    and not r["synthesis"]["applications"])
    for item_id in ids:
        item = items[item_id]
        assessment = item["assessment"]
        assert assessment["relationKind"] == "SAME_LEMMA_DIFFERENT_FIELD", item_id
        participants = [records[x] for x in assessment["relationParticipantIds"]]
        assert all(r["sourceId"] == "src-openrussian-50e210c4" for r in participants), item_id
        cue = participants[0]
        answer = records[assessment["answerSourceIds"][0]]
        assert cue["shippedText"] == item["promptLetter"], item_id
        assert answer["shippedText"] == item["answer"], item_id
        cue_field = cue["sourceRecordId"].split("#")[1]
        answer_field = answer["sourceRecordId"].split("#")[1]
        cue_label, answer_label = FIELDS[cue_field], FIELDS[answer_field]
        noun = cue["sourceRecordId"].startswith("nouns.csv:")
        kind = "noun" if noun else "adjective"
        item["prompt"] = f"Which {answer_label} form of the same {kind} corresponds to the shown form?"
        why = f"{item['promptLetter']} is the {cue_label} form; {item['answer']} is the {answer_label} form of the same {kind}."
        if item["objectiveId"] == "obj-u6-animacy":
            item["prompt"] = "Which singular accusative form is used when the shown masculine animate noun is the direct object?"
            why += " For this masculine animate noun, the singular accusative has the same form as the genitive. The nominative names the subject or dictionary entry."
        if item_id == "u7-g-objectadj-0":
            item["prompt"] = "Which accusative singular adjective form agrees with a masculine animate noun?"
            why = f"For a masculine animate object, {item['answer']} is the accusative singular form of {item['promptLetter']}. It has the same form as the masculine singular genitive. Feminine agreement uses a different ending."
        if item_id == "u7-i-objectadj-3":
            item["prompt"] = "Which feminine singular accusative adjective form corresponds to the shown feminine nominative?"
            why += " Keep feminine singular agreement; the masculine dictionary form changes gender and does not answer this question."
        if answer_field == "decl_m_nom":
            item["prompt"] = "Which dictionary form (masculine singular nominative) belongs to the same adjective as the shown form?"
        item["feedback"] = why
        assessment["whyAnswerDemonstratesObjective"] = why
        rationales = []
        for index, option in enumerate(item["options"]):
            if option == item["answer"]:
                rationales.append("")
                continue
            record = records[assessment["distractorSourceIds"][index]]
            field = record["sourceRecordId"].split("#")[1]
            rationales.append(f"{option} is the {FIELDS[field]} form, so it does not supply the requested {answer_label} form.")
        item["distractorRationales"] = rationales
        assessment["distractorDifference"] = list(rationales)
        if item_id == "u7-g-objectadj-0":
            for index, text in enumerate(rationales):
                if text:
                    rationales[index] = text.replace("requested masculine singular genitive form", "requested masculine animate accusative form")
            assessment["distractorDifference"] = list(rationales)
        unit = unit_for_objective[item["objectiveId"]]
        for index, text in enumerate([item["prompt"], why] + rationales):
            if not text or (unit, text) in authored:
                continue
            binding = f"{item['objectiveId']}:{item_id}:declension-explanation:{index}"
            record = deepcopy(template)
            bibliography = (
                "OpenRussian noun/adjective inflection cells, commit "
                "50e210c4803237779cb562bc1abcea529066031c. "
                "Resolved cell references: " + "; ".join(r["sourceRecordId"] for r in participants) + ". "
                "Source locations: " + "; ".join(sorted({r["sourceUrl"] for r in participants})) + ". "
                "Case, number and gender labels expand the cited field names. "
                "For animate masculine accusative/genitive coincidence, supplementary "
                "Gramota dictionary/search evidence: https://gramota.ru/meta/uchitel and "
                "https://gramota.ru/spravka/vopros/303478 (search results checked 2026-10-09; "
                "full-page retrieval unavailable). "
                "Independently authored English explanation; no new Russian forms or keys. "
                "Pending linguistic review, not evidence of completed review."
            )
            original = bibliography + "\n" + text
            record.update({
                "itemId": f"prov-ca-declension-{item_id}-{index}", "unitNumber": unit,
                "objectiveId": item["objectiveId"], "sourceRecordId": "ca-coverage:" + binding,
                "originalSourceText": original, "shippedText": text,
                "sourceHash": hashlib.sha256(original.encode()).hexdigest(),
                "contentHash": hashlib.sha256(text.encode()).hexdigest(),
            })
            record["synthesis"].update({
                "bibliography": bibliography, "objectiveOrClaimBinding": binding,
                "authorshipDeclaration": "Independently authored English explanation for Russian Companion; inflected Russian cells remain the separately frozen OpenRussian records.",
            })
            previous = next((r for r in data["provenanceRecords"] if r["itemId"] == record["itemId"]), None)
            if previous is None:
                data["provenanceRecords"].append(record)
            else:
                assert previous == record, record["itemId"]
            authored.add((unit, text))
    return data


def main():
    with REGISTER.open() as handle:
        ids = [r["item_id"] for r in csv.DictReader(handle, delimiter="\t")]
    data = json.loads(PACK.read_text())
    repair(data, ids)
    PACK.write_text(json.dumps(data, ensure_ascii=False, indent=2) + "\n")


if __name__ == "__main__":
    main()
