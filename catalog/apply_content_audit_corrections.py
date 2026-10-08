#!/usr/bin/env python3
"""Apply preliminary content-audit corrections to v2-curriculum.json.

Historical frozen provenance rows stay in the pack. Learner-facing corrections
that are not inside the faulty excerpt are content-audit research notes.
Review status stays IMPLEMENTED_PENDING_FINAL_AUDIT. This script does not
approve linguistics, mapping, assets, installation, or release.
"""

import csv
import hashlib
import json
import re
from copy import deepcopy
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
PACK_PATH = ROOT / "app/src/main/assets/content/v2-curriculum.json"
MAPPING_CSV = Path(
    "/home/ubuntu/.cursor/projects/workspace/uploads/Penguin_mapping_matrix_bd7d.csv"
)

SOURCE_ID = "src-content-audit-research"
REVISION = "content-audit-2026-10-08"
TRANSFORMATION = "content-audit research synthesis"
DATE = "2026-10-08"
LICENSE = "Original app text"
REVIEW = "IMPLEMENTED_PENDING_FINAL_AUDIT"
FILE_HASH = "2bcc0c45fac4508adf283f4dd939a7e578cf2c1306fdadf4966300532beb6303"

BIBLIO = (
    "Content-audit research note dated 2026-10-08. "
    "Bibliography includes the Gramota dictionary entries for некто and быть, "
    "the Gramota statement of vowels after hushers and ц, "
    "and Litnevskaya on the verbal adverb as search evidence for relative time. "
    "An imperfective form can report a completed event without asserting completion. "
    "Authorship: independently authored for Russian Companion. "
    "Status remains IMPLEMENTED_PENDING_FINAL_AUDIT. "
    "This note is not linguistic acceptance, mapping approval, installation, or owner acceptance. "
    "No owned-book prose, exercise, or answer key is copied."
)

CYRILLIC_REGION = re.compile(
    r"[\u0400-\u04FF\u0301](?:[\u0400-\u04FF\u0301]|[ \t]+(?=[\u0400-\u04FF]))*[?!.,]*"
)

PENDING = (
    "Read this as a review-pending explanation. "
    "Worked examples come before the guided choice, and the independent check is separate. "
    "A wrong answer returns to this contrast. Activity is not demonstrated ability."
)


def sha256(text: str) -> str:
    return hashlib.sha256(text.encode("utf-8")).hexdigest()


def cyrillic_regions(text: str):
    found = []
    for match in CYRILLIC_REGION.finditer(text or ""):
        value = match.group(0).strip()
        trimmed = value.rstrip("?!,.")
        for candidate in (value, trimmed):
            if candidate and candidate not in found:
                found.append(candidate)
    return found


class Pack:
    def __init__(self, data):
        self.data = data
        self.items = {item["id"]: item for item in data["items"]}
        self.examples = {example["id"]: example for example in data["examples"]}
        self.steps = {}
        self.units = {unit["number"]: unit for unit in data["units"]}
        for unit in data["units"]:
            for session in unit["sessions"]:
                for step in session["steps"]:
                    self.steps[step["id"]] = (unit, session, step)
        self.provenance = data["provenanceRecords"]
        self.by_prov = {}
        for record in self.provenance:
            self.by_prov.setdefault(record["itemId"], record)
        self._seq = 0

    def unit_of_objective(self, objective_id):
        for unit in self.data["units"]:
            for objective in unit["objectives"]:
                if objective["id"] == objective_id:
                    return unit, objective
        raise KeyError(objective_id)

    def shipped(self, unit_number):
        return {record["shippedText"] for record in self.provenance if record["unitNumber"] == unit_number}

    def add_source(self):
        if any(source["id"] == SOURCE_ID for source in self.data["sources"]):
            return
        self.data["sources"].append(
            {
                "id": SOURCE_ID,
                "title": "Content-audit research notes",
                "license": LICENSE,
                "attribution": (
                    "Russian Companion content-audit research synthesis, "
                    "content-audit-2026-10-08. Bibliography is on each note. "
                    "No owned-book prose is included."
                ),
                "modification": "Original research synthesis for registered content corrections. Not a quotation.",
            }
        )
        self.data["rights"].append(
            {
                "id": "rights-content-audit-research",
                "sourceId": SOURCE_ID,
                "license": LICENSE,
                "permitsAppUse": True,
            }
        )

    def add_authored(self, unit_number, text):
        if any(record["unitNumber"] == unit_number and record["text"] == text for record in self.data["authoredEnglishRecords"]):
            return
        self._seq += 1
        self.data["authoredEnglishRecords"].append(
            {
                "id": f"en-ca-{unit_number}-{self._seq}",
                "unitNumber": unit_number,
                "text": text,
                "classification": "INDEPENDENTLY_AUTHORED_SCAFFOLD",
            }
        )

    def add_provenance(self, *, unit, objective, shipped, original, record_id, binding, synthesis_applications=None, role="label"):
        self._seq += 1
        item_id = f"prov-ca-{unit}-{self._seq}"
        synthesis = None
        if synthesis_applications is not None:
            synthesis = {
                "bibliography": BIBLIO,
                "sourceDateOrVersion": DATE,
                "authorshipDeclaration": (
                    "Independently authored for Russian Companion on 2026-10-08. "
                    "This corrects a learner-facing claim and keeps the earlier frozen excerpt in the pack."
                ),
                "objectiveOrClaimBinding": binding,
                "finalAudit": {
                    "artifactIdentity": "",
                    "artifactDigest": "",
                    "auditedContentDigest": "",
                    "disposition": "",
                    "modelVersion": "",
                    "date": "",
                },
                "applications": synthesis_applications,
            }
        record = {
            "itemId": item_id,
            "unitNumber": unit,
            "objectiveId": objective,
            "recordType": role,
            "sourceId": SOURCE_ID,
            "sourceUrl": "urn:russian-companion:content-audit-research:2026-10-08",
            "sourceRevision": REVISION,
            "sourceRecordId": record_id,
            "originalSourceText": original,
            "shippedText": shipped,
            "transformation": TRANSFORMATION,
            "licenseId": LICENSE,
            "licenseUrl": "urn:russian-companion:license:original-app-text",
            "attribution": (
                "Russian Companion content-audit research synthesis, content-audit-2026-10-08. "
                "No owned-book prose is included."
            ),
            "shareAlikeTreatment": (
                "Original research synthesis authored for this companion. "
                "It is not a CC BY-SA quotation. Application code, user data, and user recordings are outside this statement."
            ),
            "sourceHash": sha256(original),
            "contentHash": sha256(shipped),
            "reviewStatus": REVIEW,
            "reviewerIdentity": "",
            "reviewerQualification": "",
            "reviewDate": "",
            "reviewedDigest": "",
            "audioAssetId": None,
        }
        if synthesis is not None:
            record["synthesis"] = synthesis
        self.provenance.append(record)
        self.by_prov[item_id] = record
        return item_id

    def cover(self, unit, objective, text, binding):
        text = text.strip()
        regions = cyrillic_regions(text)
        original = BIBLIO + "\n" + text
        record_id = f"ca-coverage:{binding}"
        self.add_provenance(
            unit=unit,
            objective=objective,
            shipped=text,
            original=original,
            record_id=record_id,
            binding=binding,
        )
        already = self.shipped(unit)
        for region in regions:
            if region not in already and region != text:
                self.add_provenance(
                    unit=unit,
                    objective=objective,
                    shipped=region,
                    original=original,
                    record_id=record_id,
                    binding=binding,
                    role="example",
                )
                already.add(region)
        return text

    def set_body(self, step_id, body, objective_id=None):
        unit, _session, step = self.steps[step_id]
        if objective_id is None:
            objective_id = next(
                (
                    objective["id"]
                    for objective in unit["objectives"]
                    if step_id in objective["instructionStepIds"]
                    or step_id in objective["practiceStepIds"]
                    or step_id in objective["remediationStepIds"]
                ),
                unit["objectives"][0]["id"],
            )
        step["body"] = self.cover(unit["number"], objective_id, body, f"{objective_id}:{step_id}")

    def append_body(self, step_id, sentence, objective_id=None):
        _unit, _session, step = self.steps[step_id]
        if sentence in step["body"]:
            return
        unit, _session, step = self.steps[step_id]
        if objective_id is None:
            objective_id = next(
                (
                    objective["id"]
                    for objective in unit["objectives"]
                    if step_id in objective["instructionStepIds"]
                    or step_id in objective["remediationStepIds"]
                    or step_id in objective["practiceStepIds"]
                ),
                unit["objectives"][0]["id"],
            )
        covered = self.cover(unit["number"], objective_id, sentence, f"{objective_id}:{step_id}:append")
        step["body"] = step["body"].rstrip() + " " + covered

    def rebind(self, item_id, *, cue, answer, distractors, prompt, feedback, why, rationales, tested_feature=None):
        item = self.items[item_id]
        unit, objective = self.unit_of_objective(item["objectiveId"])
        options = []
        for option in [answer] + list(distractors):
            if option not in options:
                options.append(option)
        if answer not in options:
            raise SystemExit(f"{item_id} answer missing")
        strings = [cue, answer, prompt, feedback, why] + list(distractors) + list(rationales.values())
        regions = []
        for value in strings:
            regions.extend(cyrillic_regions(value))
        unique = []
        for value in strings + regions:
            if value and value not in unique:
                unique.append(value)
        original = BIBLIO + "\n" + "\n".join(unique)
        binding = f"{item['objectiveId']}:{tested_feature or item_id}"
        record_id = f"ca-contrast:{item_id}"
        application = [{"cue": cue, "answer": answer, "distractors": list(distractors)}]
        ids = {}
        for value in unique:
            ids[value] = self.add_provenance(
                unit=unit["number"],
                objective=item["objectiveId"],
                shipped=value,
                original=original,
                record_id=record_id,
                binding=binding,
                synthesis_applications=application,
                role="meaning" if value == answer else "label",
            )
        cue_id = ids[cue]
        answer_id = ids[answer]
        participant_ids = [cue_id, answer_id] + [ids[option] for option in options if option != answer]
        difference = []
        source_ids = []
        rationale_row = []
        for option in options:
            if option == answer:
                difference.append("")
                source_ids.append("")
                rationale_row.append("")
            else:
                rationale = rationales[option]
                difference.append(rationale)
                source_ids.append(ids[option])
                rationale_row.append(rationale)
        item["prompt"] = prompt
        item["promptLetter"] = cue
        item["options"] = options
        item["answer"] = answer
        item["feedback"] = feedback
        item["sourceId"] = SOURCE_ID
        item["containsGeneratedRussian"] = False
        item["reviewStatus"] = REVIEW
        item["distractorRationales"] = rationale_row
        assessment = item.get("assessment") or {}
        assessment.update(
            {
                "testedFeature": tested_feature or assessment.get("testedFeature") or "CONTENT_AUDIT_CORRECTION",
                "evidenceMode": "STATED_APPLICATION",
                "contrastSetId": assessment.get("contrastSetId") or item_id,
                "cueLanguage": "ru" if any("\u0400" <= ch <= "\u04ff" for ch in cue) else "rule",
                "independent": assessment.get("independent", True),
                "answerSourceIds": [answer_id],
                "distractorSourceIds": source_ids,
                "distractorDifference": difference,
                "whyAnswerDemonstratesObjective": why,
                "contrastMembers": options,
                "relationKind": "STATED_APPLICATION",
                "relationParticipantIds": participant_ids,
            }
        )
        if "responseKind" not in assessment:
            assessment["responseKind"] = "RUSSIAN_TOKEN" if any("\u0400" <= ch <= "\u04ff" for ch in answer) else "ENGLISH_GLOSS"
        item["assessment"] = assessment
        if item["kind"] not in {
            "FORM_SELECTION",
            "CASE_RECOGNITION",
            "CONTRAST_SELECTION",
            "PATTERN_CLASSIFICATION",
            "NUMBER_FORM_SELECTION",
        }:
            item["kind"] = "CONTRAST_SELECTION"

    def set_example(self, example_id, display=None, note=None):
        example = self.examples[example_id]
        unit, _objective = self.unit_of_objective(example["objectiveId"])
        if display is not None:
            example["display"] = display
        if note is not None:
            example["note"] = note
        text = f"{example['display']} — {example['note']}"
        self.cover(unit["number"], example["objectiveId"], example["display"], f"{example['objectiveId']}:{example_id}:display")
        self.cover(unit["number"], example["objectiveId"], example["note"], f"{example['objectiveId']}:{example_id}:note")
        if text != example["display"] and text != example["note"]:
            self.cover(unit["number"], example["objectiveId"], text, f"{example['objectiveId']}:{example_id}:pair")


def load_mapping_rows():
    with MAPPING_CSV.open(newline="", encoding="utf-8") as handle:
        return list(csv.DictReader(handle))


def add_mappings(pack: Pack):
    rows = {int(row["unit"]): row for row in load_mapping_rows()}
    mappings = []
    for number in range(1, 31):
        row = rows[number]
        sections = row["observed_section_evidence"]
        exercises = row["observed_exercise_locators"]
        mappings.append(
            {
                "id": f"map-u{number}-owned-file",
                "unitNumber": number,
                "status": "PENDING_CURRICULUM_REVIEWER",
                "reviewer": None,
                "reviewerQualification": None,
                "reviewedContentDigest": None,
                "editionTitle": "Nicholas J. Brown beginner Russian course file",
                "author": "Nicholas J. Brown",
                "publisherImprint": "Penguin Books",
                "proposedLessonNumber": number,
                "printedPageStart": int(row["printed_page_start"]),
                "printedPageEnd": int(row["printed_page_end"]),
                "topicDescription": (
                    f"Lesson {number} printed pages {row['printed_page_start']}-{row['printed_page_end']}. "
                    f"PDF indexes {row['pdf_index_start_1_based']}-{row['pdf_index_end_1_based']} are 1-based file locators, not printed pages. "
                    f"Observed section locators: {sections}. Observed exercise locators: {exercises}. "
                    "Locators are partial OCR observations and are not an approved exercise assignment. "
                    f"Owned file SHA-256 {FILE_HASH}. "
                    "Back-cover ISBN 0-14-012041-6 and barcode 9780140120417 were observed and are not an accepted identifier. "
                    "Copyright year and printing remain unresolved. Required versus optional book work stays pending review."
                ),
                "observationSource": (
                    f"Supplied private file SHA-256 {FILE_HASH}; 528 PDF pages; "
                    "observed back-cover ISBN 0-14-012041-6; barcode 9780140120417; "
                    "copyright year and printing unresolved."
                ),
                "isbn": None,
                "isbnObservation": "UNRESOLVED",
            }
        )
    pack.data["ownedEditionMappings"] = mappings


def add_directions(pack: Pack):
    rows = {int(row["unit"]): row for row in load_mapping_rows()}
    for number, unit in pack.units.items():
        row = rows[number]
        book = (
            f"Owned-book assignment for Lesson {number}: printed pages "
            f"{row['printed_page_start']}-{row['printed_page_end']}. "
            f"PDF indexes {row['pdf_index_start_1_based']}-{row['pdf_index_end_1_based']} "
            "are file locators, not printed pages. "
            f"Observed section locators: {row['observed_section_evidence']}. "
            f"Observed exercise locators: {row['observed_exercise_locators']}. "
            "These locators are pending reviewer assignment and are not an approved mapping. "
            "Do the work in the copy you own. Book activity is not a demonstrated app check."
        )
        companion = (
            f"Unit {number} AnkiDroid self-attestation uses only cards you are allowed to make. "
            "Binder: write one form from this unit that you missed and the correction you will use. "
            "Tutor conversation is optional and is not a demonstrated check. "
            "Cumulative review revisits completed objectives from earlier units. "
            "Activity is not demonstrated ability. Open the offline grammar reference for this unit when one is linked."
        )
        if number >= 3:
            pack.add_authored(number, book)
            pack.add_authored(number, companion)
            pack.add_authored(number, PENDING)
        for session in unit["sessions"]:
            for step in session["steps"]:
                if step["kind"] == "BOOK":
                    step["bookDirection"] = book
        objective_id = next(objective["id"] for objective in unit["objectives"] if objective["kind"] == "GUIDANCE" or objective["kind"] == "SELF_ATTESTATION")
        session = unit["sessions"][-1]
        session["steps"].extend(
            [
                {
                    "id": f"u{number}-st-anki-direction",
                    "kind": "ANKI",
                    "title": f"Unit {number} AnkiDroid direction",
                    "body": companion,
                    "dominantAction": "Self-attest",
                    "referenceIds": [],
                    "itemIds": [],
                    "exampleIds": [],
                    "unavailableReason": None,
                    "bookDirection": None,
                    "companionDirection": "Self-attest AnkiDroid work. It remains user-reported and does not demonstrate a check.",
                    "remediationStepId": None,
                },
                {
                    "id": f"u{number}-st-binder-direction",
                    "kind": "BINDER",
                    "title": f"Unit {number} binder prompt",
                    "body": companion,
                    "dominantAction": "Continue",
                    "referenceIds": [],
                    "itemIds": [],
                    "exampleIds": [],
                    "unavailableReason": None,
                    "bookDirection": None,
                    "companionDirection": "A binder prompt is one specific note, not a score.",
                    "remediationStepId": None,
                },
                {
                    "id": f"u{number}-st-tutor-direction",
                    "kind": "TUTOR",
                    "title": f"Unit {number} optional tutor",
                    "body": companion,
                    "dominantAction": "Continue",
                    "referenceIds": [],
                    "itemIds": [],
                    "exampleIds": [],
                    "unavailableReason": None,
                    "bookDirection": None,
                    "companionDirection": "Tutor work is optional and does not count as demonstrated ability.",
                    "remediationStepId": None,
                },
                {
                    "id": f"u{number}-st-cumulative-direction",
                    "kind": "ORIENT",
                    "title": f"Unit {number} cumulative review",
                    "body": companion,
                    "dominantAction": "Continue",
                    "referenceIds": [],
                    "itemIds": [],
                    "exampleIds": [],
                    "unavailableReason": None,
                    "bookDirection": book if number < 3 else None,
                    "companionDirection": companion,
                    "remediationStepId": None,
                },
            ]
        )
        if objective_id not in session["objectiveIds"]:
            session["objectiveIds"].append(objective_id)
        for objective in unit["objectives"]:
            if objective["kind"] == "RECOGNITION":
                bodies = []
                for step_id in objective["instructionStepIds"]:
                    step = pack.steps[step_id][2]
                    if step["kind"] == "TEACH":
                        bodies.append(step["body"])
                latin = sum(1 for char in " ".join(bodies) if char.isascii() and char.isalpha())
                if latin < 80 and objective["instructionStepIds"]:
                    teach_id = next(
                        step_id
                        for step_id in objective["instructionStepIds"]
                        if pack.steps[step_id][2]["kind"] == "TEACH"
                    )
                    if number >= 3:
                        pack.append_body(teach_id, PENDING, objective["id"])
                    else:
                        step = pack.steps[teach_id][2]
                        if PENDING not in step["body"]:
                            step["body"] = step["body"].rstrip() + " " + PENDING


def add_guided_routes(pack: Pack):
    specs = [
        (
            "u1-st-confusable",
            "u1-st-guided-confusable",
            "obj-u1-confusable",
            "CONFUSABLE",
            "The shape looks like Latin P. Which identity is the Cyrillic letter?",
            "Р",
            "The Cyrillic letter Р",
            ["The Latin letter P", "The Cyrillic letter П"],
            "Р is the Cyrillic letter, even when the shape looks like Latin P.",
        ),
        (
            "u1-st-iotated",
            "u1-st-guided-iotated",
            "obj-u1-iotated",
            "LETTER_IDENTITY",
            "Which printed form is the iotated letter named in this group?",
            "Я",
            "The Cyrillic letter Я",
            ["The Cyrillic letter А", "The Cyrillic letter И"],
            "Я belongs to the iotated group. Exact phonetics stay with the book model you own.",
        ),
        (
            "u1-st-signs",
            "u1-st-guided-signs",
            "obj-u1-signs",
            "SIGN",
            "What does this sign do?",
            "Ь",
            "It marks softness and is not a vowel",
            ["It is a spoken vowel", "It is the Latin letter b"],
            "Ь and Ъ are signs. Do not invent a spoken vowel for them.",
        ),
        (
            "u1-st-print",
            "u1-st-guided-print",
            "obj-u1-print-forms",
            "PRINT_PAIR",
            "Which lowercase print form pairs with this uppercase letter?",
            "А",
            "а",
            ["б", "В"],
            "Match the printed uppercase and lowercase pair. Handwriting assets are not installed.",
        ),
        (
            "u1-st-decoding",
            "u1-st-guided-decoding",
            "obj-u1-decoding",
            "PRINT_PAIR",
            "Read the printed pair before any word. Which lowercase form matches?",
            "М",
            "м",
            ["н", "т"],
            "Decode the print pair first. This guided step still does not score a whole word.",
        ),
    ]
    for teach_id, guided_id, objective_id, kind, prompt, letter, answer, distractors, feedback in specs:
        unit, session, teach = pack.steps[teach_id]
        options = [answer] + distractors
        item_id = guided_id.replace("u1-st-", "u1-g-")
        pack.data["items"].append(
            {
                "id": item_id,
                "kind": kind,
                "language": "en",
                "prompt": prompt,
                "promptLetter": letter,
                "options": options,
                "answer": answer,
                "feedback": feedback,
                "objectiveId": objective_id,
                "sourceId": "src-unicode-cyrillic",
                "reviewStatus": REVIEW,
                "containsGeneratedRussian": False,
            }
        )
        pack.items[item_id] = pack.data["items"][-1]
        guided = {
            "id": guided_id,
            "kind": "GUIDED",
            "title": "Guided contrast",
            "body": teach["body"] + " Choose once with the explanation still in view. This is practice, not the independent check.",
            "dominantAction": "Check",
            "referenceIds": [],
            "itemIds": [item_id],
            "exampleIds": list(teach.get("exampleIds") or []),
            "unavailableReason": None,
            "bookDirection": teach.get("bookDirection"),
            "companionDirection": None,
            "remediationStepId": teach_id,
        }
        index = session["steps"].index(teach)
        session["steps"].insert(index + 1, guided)
        pack.steps[guided_id] = (unit, session, guided)
        objective = next(item for item in unit["objectives"] if item["id"] == objective_id)
        objective["practiceStepIds"] = [guided_id] + [step_id for step_id in objective["practiceStepIds"] if step_id != guided_id]

    unit, session, rem = pack.steps["u4-st-rem-nominative"]
    source = pack.items["u4-i-nom-muzh"]
    guided_item = deepcopy(source)
    guided_item["id"] = "u4-g-nominative-1"
    guided_item["feedback"] = (
        "Муж говорит uses the dictionary form as the subject: The husband is talking. "
        "Compare that subject with the admitted record before the independent check."
    )
    pack.data["items"].append(guided_item)
    pack.items[guided_item["id"]] = guided_item
    pack.cover(4, "obj-u4-nominative", guided_item["feedback"], "obj-u4-nominative:guided-feedback")
    guided = {
        "id": "u4-st-guided-nominative",
        "kind": "GUIDED",
        "title": "Guided nominative subject",
        "body": (
            "The nominative case is the default case of a word, as found in dictionaries and when no other case is specifically used. "
            "Use that subject form in Муж говорит — The husband is talking. Then answer the guided choice."
        ),
        "dominantAction": "Check",
        "referenceIds": [],
        "itemIds": ["u4-g-nominative-1"],
        "exampleIds": ["ex-u4-nom-muzh", "ex-u4-nom-poezd"],
        "unavailableReason": None,
        "bookDirection": None,
        "companionDirection": None,
        "remediationStepId": "u4-st-rem-nominative",
    }
    index = session["steps"].index(rem)
    session["steps"].insert(index, guided)
    pack.steps[guided["id"]] = (unit, session, guided)
    objective = next(item for item in unit["objectives"] if item["id"] == "obj-u4-nominative")
    objective["practiceStepIds"] = ["u4-st-guided-nominative"] + [
        step_id for step_id in objective["practiceStepIds"] if step_id != "u4-st-guided-nominative"
    ]
    rem["body"] = (
        "The nominative case is the default case of a word, as found in dictionaries and when no other case is specifically used. "
        "Муж говорит — The husband is talking. Return to that subject contrast."
    )


def add_letter_names(pack: Pack):
    names = [
        ("А", "а"),
        ("Б", "бэ"),
        ("В", "вэ"),
        ("Г", "гэ"),
        ("Д", "дэ"),
        ("Е", "е"),
        ("Ё", "ё"),
        ("Ж", "жэ"),
        ("З", "зэ"),
        ("И", "и"),
        ("Й", "и краткое"),
        ("К", "ка"),
        ("Л", "эль"),
        ("М", "эм"),
        ("Н", "эн"),
        ("О", "о"),
        ("П", "пэ"),
        ("Р", "эр"),
        ("С", "эс"),
        ("Т", "тэ"),
        ("У", "у"),
        ("Ф", "эф"),
        ("Х", "ха"),
        ("Ц", "цэ"),
        ("Ч", "че"),
        ("Ш", "ша"),
        ("Щ", "ща"),
        ("Ъ", "твёрдый знак"),
        ("Ы", "ы"),
        ("Ь", "мягкий знак"),
        ("Э", "э"),
        ("Ю", "ю"),
        ("Я", "я"),
    ]
    by_upper = {letter["uppercase"]: letter for letter in pack.data["letters"]}
    body = "Unit 1 letter names, review-pending: " + "; ".join(f"{upper} is named {name}" for upper, name in names) + "."
    unit = pack.units[1]
    objective = {
        "id": "obj-u1-letter-names",
        "unitNumber": 1,
        "title": "Name the printed letter",
        "kind": "RECOGNITION",
        "instructionStepIds": ["u1-st-teach-letter-names"],
        "exampleIds": ["ex-u1-name-a", "ex-u1-name-be"],
        "practiceStepIds": ["u1-st-guided-letter-names"],
        "checkItemIds": ["u1-i-name-be"],
        "remediationStepIds": ["u1-st-rem-letter-names"],
        "gating": True,
    }
    unit["objectives"].append(objective)
    steps = [
        {
            "id": "u1-st-teach-letter-names",
            "kind": "TEACH",
            "title": "Printed letter names",
            "body": body,
            "dominantAction": "Continue",
            "referenceIds": ["ref-ca-letter-names"],
            "itemIds": [],
            "exampleIds": ["ex-u1-name-a", "ex-u1-name-be"],
            "unavailableReason": None,
            "bookDirection": "Use your Lesson 1 pages for the author's pronunciation notes. This list is the app's name inventory.",
            "companionDirection": None,
            "remediationStepId": None,
        },
        {
            "id": "u1-st-guided-letter-names",
            "kind": "GUIDED",
            "title": "Guided letter name",
            "body": "The printed letter А is named а. Choose that name before the independent check.",
            "dominantAction": "Check",
            "referenceIds": ["ref-ca-letter-names"],
            "itemIds": ["u1-g-name-a"],
            "exampleIds": ["ex-u1-name-a"],
            "unavailableReason": None,
            "bookDirection": None,
            "companionDirection": None,
            "remediationStepId": "u1-st-rem-letter-names",
        },
        {
            "id": "u1-st-rem-letter-names",
            "kind": "REMEDIATE",
            "title": "Letter-name remediation",
            "body": "Return to the printed name. А is named а. Б is named бэ. Do not use the Latin letter name.",
            "dominantAction": "Return",
            "referenceIds": ["ref-ca-letter-names"],
            "itemIds": [],
            "exampleIds": [],
            "unavailableReason": None,
            "bookDirection": None,
            "companionDirection": None,
            "remediationStepId": None,
        },
        {
            "id": "u1-st-check-letter-names",
            "kind": "CHECK",
            "title": "Letter-name check",
            "body": "Name the printed letter without looking back at the list.",
            "dominantAction": "Check",
            "referenceIds": [],
            "itemIds": ["u1-i-name-be"],
            "exampleIds": [],
            "unavailableReason": None,
            "bookDirection": None,
            "companionDirection": None,
            "remediationStepId": "u1-st-rem-letter-names",
        },
    ]
    session = {
        "id": "u1-s-letter-names",
        "title": "Letter names",
        "estimatedMinutes": 8,
        "objectiveIds": ["obj-u1-letter-names"],
        "steps": steps,
    }
    unit["sessions"].append(session)
    for step in steps:
        pack.steps[step["id"]] = (unit, session, step)
    for example_id, upper, note in (
        ("ex-u1-name-a", "А", "The printed letter А is named а."),
        ("ex-u1-name-be", "Б", "The printed letter Б is named бэ."),
    ):
        letter = by_upper[upper]
        example = {
            "id": example_id,
            "kind": "LEXICAL",
            "letterId": letter["id"],
            "display": upper,
            "note": note,
            "sourceId": "src-unicode-cyrillic",
            "reviewStatus": REVIEW,
            "objectiveId": "obj-u1-letter-names",
        }
        pack.data["examples"].append(example)
        pack.examples[example_id] = example
    pack.data["items"].extend(
        [
            {
                "id": "u1-g-name-a",
                "kind": "LETTER_IDENTITY",
                "language": "en",
                "prompt": "Which name belongs to this printed letter?",
                "promptLetter": "А",
                "options": ["а", "бэ", "эр"],
                "answer": "а",
                "feedback": "The printed letter А is named а.",
                "objectiveId": "obj-u1-letter-names",
                "sourceId": "src-unicode-cyrillic",
                "reviewStatus": REVIEW,
                "containsGeneratedRussian": False,
            },
            {
                "id": "u1-i-name-be",
                "kind": "LETTER_IDENTITY",
                "language": "en",
                "prompt": "Which name belongs to this printed letter?",
                "promptLetter": "Б",
                "options": ["бэ", "вэ", "пэ"],
                "answer": "бэ",
                "feedback": "The printed letter Б is named бэ.",
                "objectiveId": "obj-u1-letter-names",
                "sourceId": "src-unicode-cyrillic",
                "reviewStatus": REVIEW,
                "containsGeneratedRussian": False,
            },
        ]
    )
    for item in pack.data["items"]:
        pack.items[item["id"]] = item
    pack.data["references"].append(
        {
            "id": "ref-ca-letter-names",
            "title": "Printed letter names",
            "summary": "Unit 1 introduces the printed letter names used again in Unit 30.",
            "detail": body,
            "introducedInUnit": 1,
            "sourceId": "src-unicode-cyrillic",
            "reviewStatus": REVIEW,
            "classification": "LINGUISTIC_INSTRUCTION",
            "searchTerms": ["letter name", "алфавит"],
        }
    )


def move_unit27_stress(pack: Pack):
    stress = next(objective for objective in pack.units[27]["objectives"] if objective["id"] == "obj-u27-stress")
    present = next(objective for objective in pack.units[27]["objectives"] if objective["id"] == "obj-u27-present-passive")
    for item_id in ("u27-g-stress-1", "u27-i-stress-1"):
        pack.items[item_id]["objectiveId"] = "obj-u27-present-passive"
    stress["checkItemIds"] = [item_id for item_id in stress["checkItemIds"] if item_id != "u27-i-stress-1"]
    if "u27-i-stress-1" not in present["checkItemIds"]:
        present["checkItemIds"].append("u27-i-stress-1")
    guided_step = pack.steps["u27-stress-guided"][2]
    guided_step["itemIds"] = ["u27-g-stress-past"]
    check_step = pack.steps["u27-stress-check"][2]
    check_step["itemIds"] = [item_id for item_id in check_step["itemIds"] if item_id != "u27-i-stress-1"]
    optional_guided = pack.steps["u27-st-guided-present-passive-1"][2]
    if "u27-g-stress-1" not in optional_guided["itemIds"]:
        optional_guided["itemIds"].append("u27-g-stress-1")
    optional_check = pack.steps["u27-st-check-present-passive"][2]
    if "u27-i-stress-1" not in optional_check["itemIds"]:
        optional_check["itemIds"].append("u27-i-stress-1")
    if "u27-st-guided-present-passive-1" not in present["practiceStepIds"]:
        present["practiceStepIds"].append("u27-st-guided-present-passive-1")
    example = pack.examples["ex-u27-stress-1"]
    example["display"] = "прочи́танный"
    example["note"] = "past passive stress placement"
    pack.cover(27, "obj-u27-stress", "прочи́танный", "obj-u27-stress:example-display")
    pack.cover(27, "obj-u27-stress", "past passive stress placement", "obj-u27-stress:example-note")
    cue = "guided past passive of написать"
    answer = "stem stress напи́санный"
    distractor = "ending stress написа́нный"
    prompt = "Which past-passive stress is admitted for this guided write form?"
    feedback = "The admitted past-passive stress is stem stress напи́санный."
    why = "The guided write form keeps stem stress напи́санный and rejects ending stress написа́нный."
    rationale = "ending stress написа́нный is the rejected accent for this guided past-passive form."
    item = {
        "id": "u27-g-stress-past",
        "kind": "CONTRAST_SELECTION",
        "language": "en",
        "prompt": prompt,
        "promptLetter": cue,
        "options": [answer, distractor],
        "answer": answer,
        "feedback": feedback,
        "objectiveId": "obj-u27-stress",
        "sourceId": SOURCE_ID,
        "reviewStatus": REVIEW,
        "containsGeneratedRussian": False,
        "distractorRationales": ["", rationale],
        "assessment": {
            "testedFeature": "STRESS_NAPISANNY_GUIDED",
            "evidenceMode": "STATED_APPLICATION",
            "contrastSetId": "u27-g-stress-past",
            "cueLanguage": "rule",
            "independent": False,
            "answerSourceIds": [],
            "distractorSourceIds": ["", ""],
            "distractorDifference": ["", rationale],
            "whyAnswerDemonstratesObjective": why,
            "contrastMembers": [answer, distractor],
            "relationKind": "STATED_APPLICATION",
            "relationParticipantIds": [],
            "responseKind": "ENGLISH_GLOSS",
        },
    }
    pack.data["items"].append(item)
    pack.items[item["id"]] = item
    pack.rebind(
        "u27-g-stress-past",
        cue=cue,
        answer=answer,
        distractors=[distractor],
        prompt=prompt,
        feedback=feedback,
        why=why,
        rationales={distractor: rationale},
        tested_feature="STRESS_NAPISANNY_GUIDED",
    )
    pack.items["u27-g-stress-past"]["assessment"]["independent"] = False
    pack.append_body(
        "u27-stress-teach-1",
        "Required stress checks use only the past passive forms напи́санный and прочи́танный. Present passive headwords stay on the optional card and do not gate this unit.",
        "obj-u27-stress",
    )


def apply_corrections(pack: Pack):
    pack.set_body(
        "u24-st-teach-stand-ins-3",
        "не́кто is an indefinite pronoun, a person whose identity is not specified. It is not the predicative negative of кто. Absence of a person uses a case form such as не́кого for the genitive, не́кому for the dative, or не́кем for the instrumental. There is no nominative некого.",
        "obj-u24-stand-ins",
    )
    pack.set_example("ex-u24-stand-ins-2", display="не́кого спросить", note="there is nobody to ask, genitive absence")
    pack.rebind(
        "u24-i-stand-ins-2",
        cue="не́кого спросить",
        answer="there is nobody to ask",
        distractors=["an unspecified person", "there is nothing to ask"],
        prompt="Which reading is the genitive absence construction?",
        feedback="не́кого спросить means there is nobody to ask. не́кто would mean an unspecified person, and that is not this construction.",
        why="The genitive absence form не́кого with an infinitive means there is nobody to ask.",
        rationales={
            "an unspecified person": "an unspecified person is the reading of не́кто, not of не́кого.",
            "there is nothing to ask": "there is nothing to ask would be a нечего construction, not не́кого.",
        },
        tested_feature="GENITIVE_ABSENCE_NEKOGO",
    )
    pack.append_body(
        "u24-st-rem-stand-ins",
        "не́кто is indefinite. For genitive absence use не́кого, as in не́кого спросить, there is nobody to ask.",
        "obj-u24-stand-ins",
    )

    pack.rebind(
        "u6-i-possessive-2",
        cue="на́ша сестра́",
        answer="на́шу сестру́",
        distractors=["на́шего бра́та", "на́ши"],
        prompt="Which object-row phrase agrees with the shown feminine noun phrase?",
        feedback="на́ша сестра́ takes the feminine object-row phrase на́шу сестру́.",
        why="The shown feminine nominative на́ша сестра́ agrees with the object-row phrase на́шу сестру́.",
        rationales={
            "на́шего бра́та": "на́шего бра́та is a masculine animate object phrase, not the feminine phrase.",
            "на́ши": "на́ши is plural and does not mark the feminine singular object row.",
        },
        tested_feature="FEMININE_POSSESSIVE_OBJECT",
    )

    animacy = pack.items["u6-i-animacy-3"]
    animacy["promptLetter"] = "учи́тель"
    animacy["answer"] = "учи́теля"
    animacy["options"] = ["учи́теля", "учи́тель"]
    animacy["feedback"] = "Does the admitted record show the governed contrast? учи́теля."
    animacy["distractorRationales"] = ["", "This form is the dictionary row of the same word, not the object row."]
    assessment = animacy["assessment"]
    assessment["relationParticipantIds"] = [
        "prov-ex-or-u6-animacy-6",
        "prov-ex-or-u6-animacy-7",
        "prov-ex-or-u6-animacy-6",
    ]
    assessment["answerSourceIds"] = ["prov-ex-or-u6-animacy-7"]
    assessment["distractorSourceIds"] = ["", "prov-ex-or-u6-animacy-6"]
    assessment["contrastMembers"] = ["учи́теля", "учи́тель"]
    assessment["distractorDifference"] = ["", "This form is the dictionary row of the same word, not the object row."]

    donor = next(record for record in pack.provenance if record["shippedText"] == "но́вая" and record["objectiveId"] == "obj-u7-default")
    cloned = deepcopy(donor)
    cloned["itemId"] = "prov-ca-u7-objectadj-f-nom"
    cloned["objectiveId"] = "obj-u7-objectadj"
    cloned["contentHash"] = sha256(cloned["shippedText"])
    pack.provenance.append(cloned)
    adjective = pack.items["u7-i-objectadj-3"]
    adjective["promptLetter"] = "но́вая"
    adjective["answer"] = "но́вую"
    adjective["options"] = ["но́вую", "но́вый"]
    adjective["feedback"] = "Does the admitted record show the governed contrast? но́вую."
    adjective["distractorRationales"] = ["", "This form is the dictionary row of the same word, not the object row."]
    assessment = adjective["assessment"]
    assessment["relationParticipantIds"] = [
        "prov-ca-u7-objectadj-f-nom",
        "prov-ex-or-u7-objectadj-6",
        "prov-ex-or-u7-objectadj-4",
    ]
    assessment["answerSourceIds"] = ["prov-ex-or-u7-objectadj-6"]
    assessment["distractorSourceIds"] = ["", "prov-ex-or-u7-objectadj-4"]
    assessment["contrastMembers"] = ["но́вую", "но́вый"]
    assessment["distractorDifference"] = ["", "This form is the dictionary row of the same word, not the object row."]
    pack.set_example("ex-u7-objectadj-3", display="но́вая книга", note="но́вую книгу, feminine object phrase")

    glosses = {
        "ex-u6-direction-0": ("Я шёл в дом", "I was walking into the house."),
        "ex-u6-direction-2": ("Я шёл на улицу", "I was walking out into the street."),
        "ex-u7-objectadj-3": ("этих женщин", "these women, accusative or genitive plural, context not a full sentence"),
        "ex-u10-animate-0": ("этих женщин", "these women, accusative or genitive plural, context not a full sentence"),
        "ex-u10-animate-1": ("Эти женщины", "these women, nominative plural"),
        "ex-u17-ordinals-3": ("восемнадцатую девушку", "the eighteenth girl, accusative"),
        "ex-u16-complements-0": ("стать", "to become"),
        "ex-u27-comp-2": ("Анна убрала прочитанное письмо.", "Anna put away the letter that had been read."),
        "ex-u4-вы": None,
        "ex-u25-courtesy-2": None,
    }
    for example_id, pair in glosses.items():
        if pair is None or example_id not in pack.examples:
            continue
        pack.set_example(example_id, display=pair[0], note=pair[1])
    if "ex-u4-вы" in pack.examples:
        current = pack.examples["ex-u4-вы"]
        pack.set_example(
            "ex-u4-вы",
            note="you, plural, and also the polite or formal singular",
        )
    if "ex-u25-courtesy-2" in pack.examples:
        pack.set_example(
            "ex-u25-courtesy-2",
            note="Извини́те is the polite or plural form. The familiar singular is Извини́.",
        )

    pack.set_body(
        "u16-st-teach-complements-1",
        "стать means to become. остаться means to remain. The infinitive стать is not the past form became.",
        "obj-u16-complements",
    )
    pack.rebind(
        "u16-g-complements-0",
        cue="стать",
        answer="to become",
        distractors=["to remain", "to turn out to be"],
        prompt="Which infinitive meaning is written for the shown form?",
        feedback="стать means to become. It is an infinitive, not the past form became.",
        why="The infinitive стать means to become, while остаться means to remain.",
        rationales={
            "to remain": "to remain is the meaning of остаться, not стать.",
            "to turn out to be": "to turn out to be is a different complement verb, not стать.",
        },
        tested_feature="INFINITIVE_STAT",
    )
    pack.rebind(
        "u20-i-motion-4",
        cue="Мальчик отошёл от незнакомца",
        answer="The boy stepped back from the stranger.",
        distractors=["They will leave Vladivostok in a month.", "The ball flew onto the roof of the house."],
        prompt="Which translation covers only the shown sentence?",
        feedback="Мальчик отошёл от незнакомца means the boy stepped back from the stranger. No candy offer is in the Russian.",
        why="The Russian sentence contains only the boy stepping back from the stranger.",
        rationales={
            "They will leave Vladivostok in a month.": "They will leave Vladivostok in a month. is a different motion sentence.",
            "The ball flew onto the roof of the house.": "The ball flew onto the roof of the house. is a different motion sentence.",
        },
        tested_feature="MOTION_NO_ADDED_CLAUSE",
    )
    pack.set_body(
        "u21-st-teach-object-reference-2",
        "жену́ is the accusative of жена́, wife. In Я люблю́ свою́ жену́ the whole sentence means I love my wife. The word жену́ by itself does not mean I love.",
        "obj-u21-object-reference",
    )
    pack.rebind(
        "u27-i-comp-2",
        cue="Анна убрала прочитанное письмо.",
        answer="Anna put away the letter that had been read.",
        distractors=["Anna wrote the letter on the table", "the letter put Anna away"],
        prompt="Which reading keeps the reader of the letter unspecified?",
        feedback="прочитанное письмо means the letter that had been read. The sentence does not say that Anna read it.",
        why="The passive participle прочитанное does not name Anna as the reader.",
        rationales={
            "Anna wrote the letter on the table": "Anna wrote the letter on the table invents a different verb.",
            "the letter put Anna away": "the letter put Anna away reverses who did the action.",
        },
        tested_feature="PASSIVE_READER_UNSPECIFIED",
    )
    pack.items["u27-i-comp-2"]["assessment"]["independent"] = True

    pack.set_body(
        "u21-st-teach-purpose-2",
        "чтобы is the ordinary conjunction so that. The bookish or archaic qualifier belongs to дабы, not to чтобы.",
        "obj-u21-purpose",
    )
    if "u21-g-purpose-0" in pack.items:
        pack.rebind(
            "u21-g-purpose-0",
            cue="чтобы",
            answer="so that",
            distractors=["if", "because"],
            prompt="Which ordinary purpose meaning matches the shown conjunction?",
            feedback="чтобы means so that in ordinary purpose clauses. дабы is the bookish or archaic counterpart.",
            why="чтобы is the normal purpose conjunction and means so that.",
            rationales={
                "if": "if is the meaning associated with если, not чтобы.",
                "because": "because is the meaning associated with потому что, not чтобы.",
            },
            tested_feature="PURPOSE_CHTOBY",
        )
    if "u21-i-purpose-1" in pack.items:
        pack.rebind(
            "u21-i-purpose-1",
            cue="если",
            answer="if",
            distractors=["so that", "because"],
            prompt="Which meaning matches the shown conjunction?",
            feedback="если means if. It is not the purpose conjunction чтобы.",
            why="если means if, while чтобы means so that.",
            rationales={
                "so that": "so that is the ordinary meaning of чтобы, not если.",
                "because": "because is the meaning of потому что, not если.",
            },
            tested_feature="CONDITIONAL_ESLI",
        )

    replacements = {
        "u7-st-teach-spelling-2": "After ж, ц, ч, ш, or щ, an unstressed о in an ending is written е. That replacement is not a rule for every unstressed о.",
        "u8-st-teach-spelling-2": "After г, к, х, ж, ч, ш, or щ, write и rather than ы. The seven-letter condition is required.",
        "u6-st-teach-feminine-2": "Nouns in the а or я declension, including masculine nouns such as папа and дядя, have their own accusative singular ending. Feminine gender is not the only group with that ending.",
        "u3-st-teach-gender-5": "дя́дя and мужчи́на decline like nouns ending in -а, and they are masculine. Say decline for these nouns. Do not say they conjugate.",
        "u11-st-teach-past-read-2": "For a regular verb, the past tense removes -ть and adds -л, with gender endings. Irregulars such as идти and the past of быть are outside that recipe and must be learned as listed forms.",
        "u16-st-teach-inst-sg-2": "The instrumental singular -ом fits the hard stem of the relevant noun class. Soft stems use their own instrumental ending, so -ом is not universal.",
        "u16-st-teach-inst-adj-2": "Hard-stem masculine and neuter adjectives take -ым in the instrumental, and the soft or spelling-restricted stems take -им. These are adjective declension endings.",
        "u12-st-teach-future-imp-2": "быть is imperfective. Its future forms буду, будешь, будет, будем, будете, and будут combine with an imperfective infinitive to make the compound future.",
        "u11-st-teach-past-aspect-2": "An English past progressive is not a categorical map onto the Russian imperfective, and a simple past is not a categorical map onto the perfective. An imperfective past can describe a completed event when the speaker does not assert completion. Unit 11 uses these past forms only. The aspect pairing lesson is Unit 12.",
        "u13-st-teach-past-aspect-2": "Do not treat English past progressive and simple past as a fixed Russian aspect code. The imperfective does not always mean the action was unfinished.",
        "u12-st-teach-aspect-2": "Many Russian verbs are learned in an aspect pair, but not every verb has two words, and a pair is not automatic. говорить and сказать are context-dependent counterparts, not a universal pair for the English verb speak.",
        "u28-st-teach-imperfective-adverb-2": "An imperfective verbal adverb is simultaneous with the main predicate. It has no independent tense, so it is not usually present as an absolute label. The subject is the same as the main verb.",
        "u28-st-teach-perfective-adverb-2": "A perfective verbal adverb is anterior to the main predicate. It can precede a past or a future main action. It is not limited to an absolute past.",
        "u24-st-teach-word-order-contrast-2": "Word order can change what is in focus or in the scope of a particle. It does not by itself assign one unique meaning to every sentence.",
        "u26-st-teach-letter-writing-2": "One reviewed letter genre can open with the sender address and date, then a greeting, a body, and a closing. That order is not a universal rule for every Russian letter, and a ban on dear is not universal.",
        "u29-st-teach-short-adjective-2": "A short adjective is often predicative, and the long form is often attributive, but the choice depends on the sentence. Short forms are not limited to a single position rule.",
    }
    for step_id, body in replacements.items():
        if step_id in pack.steps:
            objective_id = None
            pack.set_body(step_id, body, objective_id)

    adverb_items = [
        "u28-g-imperfective-adverb-0",
        "u28-i-imperfective-adverb-1",
        "u28-i-imperfective-adverb-2",
        "u28-g-perfective-adverb-0",
        "u28-i-perfective-adverb-1",
        "u28-i-perfective-adverb-2",
    ]
    for item_id in adverb_items:
        if item_id not in pack.items:
            continue
        perfective = "perfective" in item_id
        if perfective:
            cue = "написав письмо, она отправит его завтра"
            answer = "anterior to a future main action"
            distractors = ["an absolute present label", "a different subject from the main verb"]
            prompt = "When is the perfective verbal adverb interpreted in this sentence?"
            feedback = "написав is anterior to отправит. The main action is future, so the verbal adverb is not an absolute past."
            why = "A perfective verbal adverb is anterior to its main predicate, including a future predicate."
            rationales = {
                "an absolute present label": "an absolute present label is not the time of a perfective verbal adverb.",
                "a different subject from the main verb": "a different subject from the main verb violates the shared-subject constraint.",
            }
            feature = "PERFECTIVE_ADVERB_ANTERIOR"
        else:
            cue = "читая письмо, он улыбался"
            answer = "simultaneous with the main predicate"
            distractors = ["an absolute present label", "a different subject from the main verb"]
            prompt = "How is the imperfective verbal adverb timed in this sentence?"
            feedback = "читая is simultaneous with улыбался. The past main verb shows that the form is not an absolute present."
            why = "An imperfective verbal adverb is simultaneous with the main predicate and shares its subject."
            rationales = {
                "an absolute present label": "an absolute present label ignores the past main verb улыбался.",
                "a different subject from the main verb": "a different subject from the main verb is not the shared-subject use.",
            }
            feature = "IMPERFECTIVE_ADVERB_SIMULTANEOUS"
        pack.rebind(
            item_id,
            cue=cue,
            answer=answer,
            distractors=distractors,
            prompt=prompt,
            feedback=feedback,
            why=why,
            rationales=rationales,
            tested_feature=feature,
        )
        if item_id.startswith("u28-g-"):
            pack.items[item_id]["assessment"]["independent"] = False

    ambiguous = {
        "u7-g-endings-0": (
            "но́вая книга",
            "но́вую книгу",
            ["но́вого друга", "но́вые книги"],
            "Which feminine object phrase matches the shown nominative phrase?",
            "но́вая книга takes the feminine object phrase но́вую книгу.",
            "The shown phrase is feminine singular, so the object row is но́вую книгу.",
            {
                "но́вого друга": "но́вого друга is masculine animate, not the feminine object phrase.",
                "но́вые книги": "но́вые книги is plural, not the feminine singular object phrase.",
            },
            "FEMININE_ADJECTIVE_OBJECT",
        ),
        "u7-i-endings-1": (
            "но́вое окно",
            "но́вого окна",
            ["но́вую книгу", "но́вый дом"],
            "Which genitive phrase continues the shown neuter nominative?",
            "но́вое окно continues as the genitive phrase но́вого окна.",
            "The neuter nominative но́вое окно is distinguished from the feminine object phrase.",
            {
                "но́вую книгу": "но́вую книгу is a feminine object phrase, not the neuter genitive.",
                "но́вый дом": "но́вый дом is a masculine nominative, not the neuter genitive.",
            },
            "NEUTER_ADJECTIVE_GENITIVE",
        ),
        "u7-i-spelling-3": (
            "хоро́шая книга",
            "хоро́шую книгу",
            ["хоро́шего друга", "хоро́шие книги"],
            "Which feminine object phrase matches the shown nominative after ш?",
            "хоро́шая книга takes хоро́шую книгу. The spelling condition is separate from the gender.",
            "The feminine noun книга selects the feminine object ending.",
            {
                "хоро́шего друга": "хоро́шего друга is masculine animate, not the feminine object phrase.",
                "хоро́шие книги": "хоро́шие книги is plural, not the feminine singular object phrase.",
            },
            "FEMININE_HUSHER_OBJECT",
        ),
        "u17-g-age-0": (
            "мне двадцать лет",
            "I am 20 years old",
            ["I own 20 summers", "20 people asked me"],
            "Which natural English states the age in the shown phrase?",
            "мне двадцать лет means I am 20 years old. A labeled literal gloss is a different question.",
            "The age construction мне двадцать лет is natural English I am 20 years old.",
            {
                "I own 20 summers": "I own 20 summers invents possession that the age construction does not state.",
                "20 people asked me": "20 people asked me invents a different predicate.",
            },
            "AGE_NATURAL_ENGLISH",
        ),
        "u17-g-calendar-date-0": (
            "г.",
            "year, the abbreviation",
            ["the month name", "the day number"],
            "What does the abbreviation г. contribute in a date?",
            "г. abbreviates год, year. The rest of a printed date is a separate question.",
            "The abbreviation г. means year.",
            {
                "the month name": "the month name is a separate word in the date, not the abbreviation г.",
                "the day number": "the day number is a separate part of the date, not the abbreviation г.",
            },
            "DATE_YEAR_ABBREVIATION",
        ),
        "u17-i-calendar-date-2": (
            "15 мая 1996 г.",
            "15 May 1996",
            ["the month alone", "the year abbreviation alone"],
            "Which reading translates the whole shown date?",
            "15 мая 1996 г. is the whole date 15 May 1996. Asking only about г. is a different item.",
            "The whole date includes the day, the month, and the year.",
            {
                "the month alone": "the month alone drops the day and the year.",
                "the year abbreviation alone": "the year abbreviation alone drops the day and the month.",
            },
            "WHOLE_DATE",
        ),
        "u19-i-conditional-2": (
            "я бы поспала́",
            "the speaker is marked feminine",
            ["the speaker is marked masculine", "the sentence is about another person"],
            "What does the past form in я бы поспала́ mark?",
            "поспала́ marks a feminine speaker. The wish to sleep is a different question.",
            "The feminine past поспала́ marks the speaker as feminine in this conditional.",
            {
                "the speaker is marked masculine": "the speaker is marked masculine would use a masculine past form.",
                "the sentence is about another person": "the sentence is about another person is not what the first-person pronoun states.",
            },
            "CONDITIONAL_FEMININE_SPEAKER",
        ),
        "u20-i-motion-3": (
            "Он пошёл к другу.",
            "he set off toward a friend",
            ["he was already on the way for a long time", "he refused to go"],
            "Which reading matches the perfective setting-out verb?",
            "пошёл presents setting out toward a friend. A synonym of that same reading is not a second option.",
            "The perfective пошёл presents the start of the trip, one reading.",
            {
                "he was already on the way for a long time": "he was already on the way for a long time is an imperfective duration reading.",
                "he refused to go": "he refused to go contradicts the motion verb.",
            },
            "MOTION_SETTING_OUT",
        ),
        "u24-g-alternation-0": (
            "Я не ви́жу э́ту кни́гу.",
            "I don't see this book, singular",
            ["I don't see any books", "I see this book"],
            "Which reading matches the singular object in the shown sentence?",
            "э́ту кни́гу is a singular object, so the reading is I don't see this book, singular.",
            "The demonstrative and the accusative singular specify one book.",
            {
                "I don't see any books": "I don't see any books would be a plural or bare genitive reading, not э́ту кни́гу.",
                "I see this book": "I see this book drops the negation.",
            },
            "SINGULAR_NEGATED_OBJECT",
        ),
        "u24-i-alternation-2": (
            "Я не ви́жу книг.",
            "I don't see any books",
            ["I don't see the one book", "I see the books"],
            "Which reading matches the bare plural genitive?",
            "книг in Я не ви́жу книг is the plural genitive reading I don't see any books.",
            "The bare genitive plural specifies the plural reading.",
            {
                "I don't see the one book": "I don't see the one book would need a singular object such as э́ту кни́гу.",
                "I see the books": "I see the books drops the negation.",
            },
            "PLURAL_NEGATED_GENITIVE",
        ),
        "u25-i-courtesy-2": (
            "Извини́те",
            "excuse me, polite or plural",
            ["excuse me, familiar singular", "thank you"],
            "Which address does Извини́те mark?",
            "Извини́те is polite or plural. The familiar singular is Извини́.",
            "The -те form marks polite or plural address.",
            {
                "excuse me, familiar singular": "excuse me, familiar singular is Извини́, without -те.",
                "thank you": "thank you is a different courtesy formula.",
            },
            "POLITE_IZVINITE",
        ),
    }
    for item_id, spec in ambiguous.items():
        if item_id not in pack.items:
            continue
        cue, answer, distractors, prompt, feedback, why, rationales, feature = spec
        pack.rebind(
            item_id,
            cue=cue,
            answer=answer,
            distractors=distractors,
            prompt=prompt,
            feedback=feedback,
            why=why,
            rationales=rationales,
            tested_feature=feature,
        )
        if "-g-" in item_id:
            pack.items[item_id]["assessment"]["independent"] = False

    if "u7-i-manner-3" in pack.items:
        pack.rebind(
            "u7-i-manner-3",
            cue="бы́стро",
            answer="quickly, a manner adverb",
            distractors=["a short predicative sentence", "an unanalyzed impersonal chunk"],
            prompt="Which label matches the shown manner adverb from the earlier pattern?",
            feedback="бы́стро is a manner adverb, quickly. Ей трудно is not used here; that impersonal chunk is pending separate approval.",
            why="This check uses the manner adverb бы́стро and does not introduce a short-form predicative.",
            rationales={
                "a short predicative sentence": "a short predicative sentence is a later pattern, not this adverb.",
                "an unanalyzed impersonal chunk": "an unanalyzed impersonal chunk is recorded as pending and is not the keyed answer.",
            },
            tested_feature="MANNER_ADVERB_ONLY",
        )

    notices = {
        "u23-st-orient": (
            "Proposed Unit 23 uses frozen time-expression spans for decade ranges, beginning and middle expressions, during an event, duration throughout a period, and before, after, in, and ago. "
            "Each contrast stays on its own card. A bounded completion-over-a-period check is implemented here and remains review-pending. It does not cover every temporal clause. Qualified review is pending."
        ),
        "u26-st-orient": (
            "Proposed Unit 26 uses admitted arrangement lines, the frozen indefinite-series meanings, the written capital form, the frozen address marks, and the admitted ReadyRussian letter fragments. "
            "A bounded postal-order check is implemented here and remains review-pending. It is not complete postal mastery. Qualified review is pending."
        ),
        "u27-st-orient": (
            "Proposed Unit 27 uses admitted past passive lines. Required stress checks use only past passive forms. Present passive lines are optional and do not gate this unit. "
            "A bounded participial-reading check is implemented here and remains review-pending. Active forms are not taught here. Qualified review is pending."
        ),
        "u30-st-orient": (
            "Unit 1 introduces the printed letter names, and this unit reviews them. It also shows two admitted abbreviation examples and the admitted address marks. "
            "The two-entry dictionary task is implemented and review-pending. It is not broad dictionary proficiency. The admitted capstone story is included. Advancement stays blocked until review. Qualified review is pending."
        ),
    }
    for step_id, body in notices.items():
        pack.set_body(step_id, body)

    reconcile_21 = (
        "Bounded practice for completion over a period is implemented and review-pending. "
        "It is not approved coverage of every temporal clause. Book work stays in the copy you own."
    )
    reconcile_26 = (
        "Bounded postal practice, past-passive stress, and a two-entry dictionary task are implemented and review-pending. "
        "They are not complete postal mastery, a general stress rule, or broad dictionary proficiency. "
        "Present-passive exploration does not gate Unit 27."
    )
    for reference in pack.data["references"]:
        if reference["id"] == "ref-u2125-attribution" and reconcile_21 not in reference["detail"]:
            reference["detail"] = reference["detail"].rstrip() + " " + reconcile_21
            reference["summary"] = reference["summary"].rstrip() + " Bounded completion practice is review-pending, not full coverage."
        if reference["id"] == "ref-u2630-attribution" and reconcile_26 not in reference["detail"]:
            reference["detail"] = reference["detail"].rstrip() + " " + reconcile_26
            reference["summary"] = reference["summary"].rstrip() + " Bounded postal, stress, and dictionary tasks are review-pending."
    for number in range(21, 26):
        pack.cover(number, f"obj-u{number}-book" if any(o["id"] == f"obj-u{number}-book" for o in pack.units[number]["objectives"]) else pack.units[number]["objectives"][0]["id"], reconcile_21, f"u{number}:notice-2125")
        summary = next(ref["summary"] for ref in pack.data["references"] if ref["id"] == "ref-u2125-attribution")
        pack.cover(number, pack.units[number]["objectives"][0]["id"], summary, f"u{number}:notice-2125-summary")
    for number in range(26, 31):
        objective_id = pack.units[number]["objectives"][0]["id"]
        pack.cover(number, objective_id, reconcile_26, f"u{number}:notice-2630")
        summary = next(ref["summary"] for ref in pack.data["references"] if ref["id"] == "ref-u2630-attribution")
        pack.cover(number, objective_id, summary, f"u{number}:notice-2630-summary")

    references = [
        (6, "ref-ca-cases", "Offline reference for object-row agreement, review-pending. Match gender, number, and case together, and do not change gender silently."),
        (12, "ref-ca-aspect", "Offline reference for aspect, review-pending. Aspect pairs are bounded, быть is imperfective, and an imperfective past need not mean the event was unfinished."),
        (20, "ref-ca-motion", "Offline reference for motion, review-pending. Translate only the motion and destination that the Russian sentence contains."),
        (27, "ref-ca-participles", "Offline reference for past passive, review-pending. Required stress stays on напи́санный and прочи́танный. Present passive exploration is optional."),
        (28, "ref-ca-verbal-adverb", "Offline reference for verbal adverbs, review-pending. Imperfective forms are simultaneous with the main predicate and perfective forms are anterior to it."),
    ]
    for unit_number, ref_id, text in references:
        pack.data["references"].append(
            {
                "id": ref_id,
                "title": text,
                "summary": text,
                "detail": text,
                "introducedInUnit": unit_number,
                "sourceId": SOURCE_ID,
                "reviewStatus": REVIEW,
                "classification": "LINGUISTIC_INSTRUCTION",
                "searchTerms": [ref_id],
            }
        )
        objective_id = pack.units[unit_number]["objectives"][0]["id"]
        pack.cover(unit_number, objective_id, text, f"{objective_id}:{ref_id}")
        for session in pack.units[unit_number]["sessions"]:
            for step in session["steps"]:
                if step["kind"] == "REMEDIATE" and ref_id not in step["referenceIds"]:
                    step["referenceIds"].append(ref_id)
                    break


def main():
    data = json.loads(PACK_PATH.read_text(encoding="utf-8"))
    pack = Pack(data)
    pack.add_source()
    add_mappings(pack)
    add_letter_names(pack)
    add_guided_routes(pack)
    add_directions(pack)
    move_unit27_stress(pack)
    apply_corrections(pack)
    if pack.items["u24-i-stand-ins-2"]["answer"] == "не́кто":
        raise SystemExit("negative pronoun key was not replaced")
    PACK_PATH.write_text(json.dumps(data, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    print("items", len(data["items"]), "provenance", len(data["provenanceRecords"]), "mappings", len(data["ownedEditionMappings"]))


if __name__ == "__main__":
    main()
