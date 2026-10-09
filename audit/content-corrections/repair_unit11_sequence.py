#!/usr/bin/env python3
"""Keep Unit 11 checks on past agreement; preserve existing content IDs and gates."""

import hashlib
import json
from copy import deepcopy
from pathlib import Path

HERE = Path(__file__).resolve().parent
ROOT = HERE.parents[1]
PACK = ROOT / "app/src/main/assets/content/v2-curriculum.json"
OBJECTIVE = "obj-u11-past-aspect"  # Stable identity retained; displayed scope changes.


def sha(text):
    return hashlib.sha256(text.encode()).hexdigest()


def main():
    before = PACK.read_bytes()
    data = json.loads(before)
    unit = next(u for u in data["units"] if u["number"] == 11)
    objective = next(o for o in unit["objectives"] if o["id"] == OBJECTIVE)
    if objective["title"] == "Choose past-tense gender agreement":
        print("Unit 11 agreement repair already present; retained evidence unchanged")
        return
    session = next(s for s in unit["sessions"] if s["id"] == "u11-s-past-aspect")
    steps = {s["id"]: s for s in session["steps"]}
    items = {i["id"]: i for i in data["items"]}
    records = {r["itemId"]: r for r in data["provenanceRecords"]}
    template = next(r for r in data["provenanceRecords"]
                    if r["sourceId"] == "src-content-audit-research" and r.get("synthesis")
                    and not r["synthesis"]["applications"])
    cells = {label: records[f"prov-ex-or-u11-past-aspect-{index}"] for label, index in (("masculine", 2), ("feminine", 3), ("neuter", 4))}
    assert all(r["sourceId"] == "src-openrussian-50e210c4" for r in cells.values())
    assert {r["sourceRecordId"].split("#")[0] for r in cells.values()} == {"verbs.csv:прочита'ть"}
    bibliography = (
        "OpenRussian frozen verb cells, commit 50e210c4803237779cb562bc1abcea529066031c: "
        + "; ".join(r["sourceRecordId"] for r in cells.values())
        + ". Source: " + cells["masculine"]["sourceUrl"]
        + ". Gender labels expand past_m, past_f and past_n; every Russian response is an existing admitted cell. "
        "Supplementary Gramota/Litnevskaya search evidence on gender and number: "
        "https://gramota.ru/biblioteka/spravochniki/russkij-yazyk-kratkij-teoreticheskij-kurs-dlya-shkolnikov/rod-chislo-vzaimosvyaz-glagolnykh-kategoriy "
        "(search checked 2026-10-09; full-page retrieval returned 403). "
        "Follow-up authored 2026-10-09 within the existing content-audit-2026-10-08 research batch. "
        "No Penguin expression is reproduced; semantic acceptance remains pending."
    )
    added = []

    def cover(text, name, objective_id=OBJECTIVE, bibliography_override=None):
        binding = f"{objective_id}:past-agreement:{name}"
        record = deepcopy(template)
        cited = bibliography_override or bibliography
        original = cited + "\n" + text
        record.update({"itemId": "prov-ca-u11-agreement-" + name, "unitNumber": 11,
                       "objectiveId": objective_id, "sourceRecordId": "ca-coverage:" + binding,
                       "recordType": "label", "originalSourceText": original,
                       "shippedText": text, "sourceHash": sha(original), "contentHash": sha(text)})
        record["synthesis"].update({"bibliography": cited, "objectiveOrClaimBinding": binding,
                                    "authorshipDeclaration": "Independently authored for Russian Companion on 2026-10-09; Russian cells retain their separate frozen provenance."})
        assert record["itemId"] not in records
        data["provenanceRecords"].append(record)
        records[record["itemId"]] = record
        added.append(record["itemId"])
        return text

    objective["title"] = session["title"] = cover("Choose past-tense gender agreement", "title")
    objective["exampleIds"] = [f"ex-or-u11-past-aspect-{i}" for i in (2, 3, 4)]
    bodies = {
        1: "In the singular, a past-tense verb agrees with its subject in gender. In this recorded set, прочита́л is masculine, прочита́ла feminine, and прочита́ло neuter. Compare the endings while keeping the same verb.",
        2: "Use the gender requested in the question. The pronoun I does not select one past-tense gender for every speaker. Compare the entire recorded forms: прочита́л, прочита́ла and прочита́ло. These are masculine, feminine and neuter singular forms of the same verb, respectively.",
        3: "The recorded feminine singular past form is прочита́ла. Compare its ending with the masculine прочита́л and neuter прочита́ло forms of the same verb.",
        4: "The recorded neuter singular past form is прочита́ло. Compare its ending with the masculine прочита́л and feminine прочита́ла forms of the same verb.",
    }
    for index, text in bodies.items():
        steps[f"u11-st-teach-past-aspect-{index}"]["body"] = cover(text, f"teach-{index}")
    steps["u11-st-teach-past-aspect-1"]["exampleIds"] = list(objective["exampleIds"])
    steps["u11-st-rem-past-aspect"]["body"] = steps["u11-st-teach-past-aspect-2"]["body"]
    reflexive_citation = (
        "OpenRussian frozen record verbs.csv:улыба'ться#accented and its translations_en cell; "
        + records["prov-ex-or-u11-reflexive-0"]["sourceUrl"]
        + ". The infinitive and its smile gloss are separately admitted records. "
        "Follow-up authored 2026-10-09 within the existing content-audit-2026-10-08 research batch; final semantic review pending."
    )
    reflexive = cover("Keep the reflexive ending with the verb. The recorded infinitive is улыба́ться, to smile. Compare the past forms in the worked examples.",
                      "reflexive", "obj-u11-reflexive", reflexive_citation)
    for other_session in unit["sessions"]:
        for step in other_session["steps"]:
            if step["id"] in ("u11-st-teach-reflexive-2", "u11-st-rem-reflexive"):
                step["body"] = reflexive

    def rebind(item_id, cue_label, answer_label, distractor_label):
        item = items[item_id]
        cue, answer, distractor = (cells[x] for x in (cue_label, answer_label, distractor_label))
        prompt = f"Which {answer_label} past form (singular) of the same verb corresponds to the shown {cue_label} form?"
        why = f"{cue['shippedText']} is {cue_label} past (singular); {answer['shippedText']} is {answer_label} past (singular) of the same verb."
        rationale = f"{distractor['shippedText']} is {distractor_label} past (singular), so it does not supply the requested {answer_label} past form."
        for name, text in (("prompt", prompt), ("feedback", why), ("distractor", rationale)):
            cover(text, f"{item_id}-{name}")
        item.update({"prompt": prompt, "promptLetter": cue["shippedText"],
                     "options": [answer["shippedText"], distractor["shippedText"]], "answer": answer["shippedText"],
                     "feedback": why, "sourceId": "src-openrussian-50e210c4", "containsGeneratedRussian": False,
                     "distractorRationales": ["", rationale]})
        item["assessment"].update({"testedFeature": answer_label.upper() + "_PAST", "evidenceMode": "SAME_LEMMA_DIFFERENT_FIELD",
                                   "cueLanguage": "ru", "answerSourceIds": [answer["itemId"]],
                                   "distractorSourceIds": ["", distractor["itemId"]], "distractorDifference": ["", rationale],
                                   "whyAnswerDemonstratesObjective": why, "contrastMembers": item["options"],
                                   "relationKind": "SAME_LEMMA_DIFFERENT_FIELD", "responseKind": "RUSSIAN_TOKEN",
                                   "relationParticipantIds": [cue["itemId"], answer["itemId"], distractor["itemId"]]})

    rebind("u11-g-past-aspect-0", "masculine", "neuter", "feminine")
    rebind("u11-i-past-aspect-1", "neuter", "masculine", "feminine")
    rebind("u11-i-past-aspect-2", "masculine", "feminine", "neuter")
    after = (json.dumps(data, ensure_ascii=False, indent=2) + "\n").encode()
    PACK.write_bytes(after)
    evidence = {"schemaVersion": 1, "findingId": "CA010", "unitNumber": 11,
                "scope": "Retarget the existing early-aspect objective to past agreement; stable IDs and eight required checks retained",
                "beforeCurriculumFileSha256": hashlib.sha256(before).hexdigest(),
                "afterCurriculumFileSha256": hashlib.sha256(after).hexdigest(),
                "objectiveId": OBJECTIVE, "sourceCells": [r["itemId"] for r in cells.values()],
                "addedCoverageIds": added, "disposition": "IMPLEMENTED_PENDING_FINAL_AUDIT"}
    (HERE / "UNIT11_SEQUENCE_CORRECTION.json").write_text(json.dumps(evidence, ensure_ascii=False, indent=2) + "\n")
    print(f"Retargeted Unit 11 checks; added {len(added)} pending coverage records")


if __name__ == "__main__":
    main()
