#!/usr/bin/env python3
"""Build proposed Units 6–10 from frozen admitted sources only.

Russian strings are copied from retained OpenRussian fields or from exact
Wikibooks substrings. This module does not decline, conjugate, or compose
new Russian. Learner glosses are delimited sense selections.
"""

from __future__ import annotations

import hashlib
import json
import re
from pathlib import Path

from units_3_5_intake import (
    CLAIM,
    CYRILLIC,
    DIRTY_LEARNER_TEXT,
    GRAMMAR_TERM,
    LICENSE_ID,
    LICENSE_URL,
    OPENRUSSIAN_ID,
    PENDING,
    REGION,
    SHARE_ALIKE,
    WIKIBOOKS_ID,
    delimited_senses,
    format_stress,
    sha256_text,
    unbound_letters,
)

ROOT = Path(__file__).resolve().parents[1]
FREEZE_PATH = ROOT / "catalog" / "sources" / "units-6-10" / "FREEZE.json"
SOURCE_ROOT = FREEZE_PATH.parent
SCAFFOLD = "INDEPENDENTLY_AUTHORED_SCAFFOLD"

NOUN_FIELDS = [
    "bare", "accented", "translations_en", "translations_de", "gender", "partner", "animate",
    "indeclinable", "sg_only", "pl_only", "sg_nom", "sg_gen", "sg_dat", "sg_acc", "sg_inst",
    "sg_prep", "pl_nom", "pl_gen", "pl_dat", "pl_acc", "pl_inst", "pl_prep",
]
OTHER_FIELDS = ["bare", "accented", "translations_en", "translations_de"]
ADJ_FIELDS = [
    "bare", "accented", "translations_en", "translations_de", "comparative", "superlative",
    "short_m", "short_f", "short_n", "short_pl",
    "decl_m_nom", "decl_m_gen", "decl_m_dat", "decl_m_acc", "decl_m_inst", "decl_m_prep",
    "decl_f_nom", "decl_f_gen", "decl_f_dat", "decl_f_acc", "decl_f_inst", "decl_f_prep",
    "decl_n_nom", "decl_n_gen", "decl_n_dat", "decl_n_acc", "decl_n_inst", "decl_n_prep",
    "decl_pl_nom", "decl_pl_gen", "decl_pl_dat", "decl_pl_acc", "decl_pl_inst", "decl_pl_prep",
]
FIELDS = {"nouns.csv": NOUN_FIELDS, "others.csv": OTHER_FIELDS, "adjectives.csv": ADJ_FIELDS}
GLOSS = {
    "adjectives.csv:мой": ("my, mine, my people", "my"),
    "adjectives.csv:твой": ("your, yours, thy, thine, your people", "your"),
    "adjectives.csv:новый": ("new, novel", "new"),
    "adjectives.csv:хороший": ("good, beautiful, handsome", "good"),
    "adjectives.csv:синий": ("dark blue", "dark blue"),
    "adjectives.csv:этот": ("this", "this"),
    "adjectives.csv:какой": ("which, what, what kind of", "what kind of"),
    "others.csv:его": ('genetive of "he"; genetive of "it"', 'genetive of "he"'),
    "others.csv:её": ('genetive of "she"', 'genetive of "she"'),
    "others.csv:их": ("their, them", "their"),
    "others.csv:есть": ("there is, there are", "there is"),
    "others.csv:один": ("one, 1; alone", "one"),
    "others.csv:два": ("two, 2", "two"),
    "others.csv:без": ("without; to (time)", "without"),
    "others.csv:для": ("for", "for"),
    "others.csv:из": ("from; out of", "from"),
    "others.csv:от": ("from (away); from (part of a whole); from (cause); from (distance); from (someone); from (range); for, against a purpose", "from (away)"),
    "others.csv:у": ("(possession); by, at (place); with; from (take, borrow)", "(possession)"),
    "others.csv:нет": ("no; not", "no"),
    "nouns.csv:книга": ("book, volume", "book"),
    "nouns.csv:стол": ("table, desk, board; diet, cooking, cuisine; department, section", "table"),
    "nouns.csv:брат": ("brother", "brother"),
    "nouns.csv:мама": ("mom, mama, mommy, mummy", "mom"),
    "nouns.csv:окно": ("window, casement-window, window-sill, gap, opening, port, slot, free period, break", "window"),
    "nouns.csv:сын": ("son", "son"),
    "nouns.csv:дом": ("house, home, house-hold", "house"),
    "nouns.csv:молоко": ("milk", "milk"),
    "nouns.csv:учитель": ("teacher, tutor", "teacher"),
    "nouns.csv:город": ("town", "town"),
    "nouns.csv:женщина": ("woman, women, womanfolk, womankind", "woman"),
    "nouns.csv:друг": ("friend", "friend"),
    "nouns.csv:человек": ("person, people; man", "person"),
    "nouns.csv:день": ("day, afternoon", "day"),
    "nouns.csv:слово": ("word; speech", "word"),
}


def git_blob_sha1(data: bytes) -> str:
    return hashlib.sha1(f"blob {len(data)}\0".encode() + data).hexdigest()


class FrozenSources:
    def __init__(self) -> None:
        self.freeze = json.loads(FREEZE_PATH.read_text(encoding="utf-8"))
        self.pages: dict[str, str] = {}
        for page in self.freeze["wikibooks"]["pages"]:
            path = SOURCE_ROOT / page["file"]
            data = path.read_bytes()
            if hashlib.sha256(data).hexdigest() != page["sha256"]:
                raise SystemExit(f"Wiki snapshot hash mismatch for {page['file']}")
            revision = json.loads((SOURCE_ROOT / page["revisionFile"]).read_text(encoding="utf-8"))
            if str(revision["revid"]) != str(page["oldid"]):
                raise SystemExit(f"Wiki revision id does not match oldid for {page['file']}")
            if hashlib.sha1(data).hexdigest() != revision["sha1"] or len(data) != revision["size"]:
                raise SystemExit(f"Wiki snapshot bytes do not match the retained revision for {page['file']}")
            self.pages[page["key"]] = data.decode("utf-8")
        self.file_lines: dict[str, set[str]] = {}
        for name, meta in self.freeze["openrussian"]["files"].items():
            data = (SOURCE_ROOT / meta["path"]).read_bytes()
            if hashlib.sha256(data).hexdigest() != meta["sha256"]:
                raise SystemExit(f"Retained {name} sha256 does not match the freeze")
            if git_blob_sha1(data) != meta["gitBlob"]:
                raise SystemExit(f"Retained {name} git blob does not match the freeze")
            self.file_lines[name] = set(data.decode("utf-8").splitlines())
        self.rows: dict[str, dict] = {}
        for row in self.freeze["openrussian"]["rows"]:
            if row["line"] not in self.file_lines[row["file"]]:
                raise SystemExit(f"Selected row {row['recordId']} does not occur in the retained {row['file']} bytes")
            parts = row["line"].split("\t")
            fields = FIELDS[row["file"]]
            if len(parts) != len(fields) or sha256_text(row["line"]) != row["sha256"]:
                raise SystemExit(f"Row hash mismatch for {row['recordId']}")
            self.rows[row["recordId"]] = dict(zip(fields, parts))
        license_path = SOURCE_ROOT / self.freeze["openrussian"]["licenseFile"]
        if hashlib.sha256(license_path.read_bytes()).hexdigest() != self.freeze["openrussian"]["licenseSha256"]:
            raise SystemExit("OpenRussian license hash mismatch")
        if "Creative Commons Attribution-ShareAlike 4.0" not in self.pages["copyrights"]:
            raise SystemExit("Frozen Wikibooks copyrights page does not state CC BY-SA 4.0")

    def row(self, record_id: str) -> dict:
        return self.rows[record_id]

    def require(self, page: str, needle: str) -> str:
        if needle not in self.pages[page]:
            raise SystemExit(f"Required excerpt missing from {page}: {needle}")
        return needle

    def oldid(self, page: str) -> str:
        for item in self.freeze["wikibooks"]["pages"]:
            if item["key"] == page:
                return str(item["oldid"])
        raise SystemExit(f"No oldid for {page}")


class Builder:
    def __init__(self, frozen: FrozenSources) -> None:
        self.frozen = frozen
        self.provenance: list[dict] = []
        self.authored: list[dict] = []
        self.examples: list[dict] = []
        self.items: list[dict] = []
        self._said: dict[tuple[int, str], str] = {}
        self._said_count = 0
        commit = frozen.freeze["openrussian"]["commit"]
        self.openrussian_url = f"https://github.com/Badestrand/russian-dictionary/blob/{commit}"
        self.openrussian_revision = commit
        self.openrussian_attr = f"Badestrand/russian-dictionary contributors, commit {commit}, CC BY-SA 4.0."
        self.wikibooks_attr = (
            "Wikibooks Russian grammar contributors, CC BY-SA 4.0. "
            "Page history for each frozen oldid identifies the contributors."
        )
        self.titles = {
            "accusative": "Russian/Grammar/Accusative_case",
            "adjectives": "Russian/Grammar/Adjectives",
            "genitive": "Russian/Grammar/Genitive_case",
            "numbers": "Russian/Numbers",
            "gender": "Russian/Grammar/Gender",
            "nominative": "Russian/Grammar/Nominative",
            "prepositional": "Russian/Grammar/Prepositional_case",
            "prepositions": "Russian/Prepositions",
            "personal-pronouns": "Russian/Personal_Pronouns",
            "pronouns": "Russian/Grammar/Pronouns",
            "copyrights": "Wikibooks:Copyrights",
        }

    def say(self, unit: int, text: str) -> str:
        if CYRILLIC.search(text) or CLAIM.search(text) or (GRAMMAR_TERM.search(text) and "?" not in text):
            raise SystemExit(f"independently authored English makes a linguistic claim: {text!r}")
        key = (unit, text)
        if key not in self._said:
            self._said_count += 1
            self._said[key] = f"en-u{unit}-{self._said_count:03d}"
            self.authored.append({
                "id": self._said[key],
                "unitNumber": unit,
                "text": text,
                "classification": SCAFFOLD,
            })
        return text

    def add_provenance(self, item_id, unit, objective, record_type, source_id, source_url, source_revision, source_record_id, original, shipped, transformation, attribution) -> None:
        if "/tree/" in source_url:
            raise SystemExit(f"{item_id} source URL uses a tree link")
        self.provenance.append({
            "itemId": item_id,
            "unitNumber": unit,
            "objectiveId": objective,
            "recordType": record_type,
            "sourceId": source_id,
            "sourceUrl": source_url,
            "sourceRevision": source_revision,
            "sourceRecordId": source_record_id,
            "originalSourceText": original,
            "shippedText": shipped,
            "transformation": transformation,
            "licenseId": LICENSE_ID,
            "licenseUrl": LICENSE_URL,
            "attribution": attribution,
            "shareAlikeTreatment": SHARE_ALIKE,
            "sourceHash": sha256_text(original),
            "contentHash": sha256_text(shipped),
            "reviewStatus": PENDING,
            "reviewerIdentity": "",
            "reviewerQualification": "",
            "reviewDate": "",
            "reviewedDigest": "",
            "audioAssetId": None,
        })

    def wiki(self, item_id, unit, objective, page, needle, shipped, record_type="example") -> str:
        original = self.frozen.require(page, needle)
        if shipped not in original and shipped.replace("\u0301", "") not in original:
            raise SystemExit(f"Shipped text {shipped!r} is not in {page}")
        self.add_provenance(
            item_id, unit, objective, record_type, WIKIBOOKS_ID,
            f"https://en.wikibooks.org/wiki/{self.titles[page]}?oldid={self.frozen.oldid(page)}",
            self.frozen.oldid(page), page + ":" + needle[:80], original, shipped, "selection", self.wikibooks_attr,
        )
        return shipped

    def field(self, item_id, unit, objective, record_id, name) -> str:
        original = self.frozen.row(record_id)[name]
        if original == "" or "," in original:
            raise SystemExit(f"{record_id} field {name} is not a single admitted value: {original!r}")
        shipped = format_stress(original)
        file_name = record_id.split(":", 1)[0]
        self.add_provenance(
            item_id, unit, objective, "lexical record", OPENRUSSIAN_ID,
            self.openrussian_url + "/" + file_name, self.openrussian_revision,
            record_id + "#" + name, original, shipped,
            "formatting" if shipped != original else "selection", self.openrussian_attr,
        )
        return shipped

    def gloss(self, item_id, unit, objective, record_id) -> str:
        full, sense = GLOSS[record_id]
        original = self.frozen.row(record_id)["translations_en"]
        if original != full:
            raise SystemExit(f"Gloss field changed for {record_id}: {original!r}")
        if sense == original:
            shipped, transformation = original, "selection"
        elif sense in delimited_senses(original):
            shipped, transformation = sense, "sense selection"
        else:
            raise SystemExit(f"{sense!r} is not a delimited sense of {original!r}")
        file_name = record_id.split(":", 1)[0]
        self.add_provenance(
            item_id, unit, objective, "lexical record", OPENRUSSIAN_ID,
            self.openrussian_url + "/" + file_name, self.openrussian_revision,
            record_id + "#translations_en", original, shipped, transformation, self.openrussian_attr,
        )
        return shipped

    def example(self, example_id, display, note, objective) -> None:
        source = OPENRUSSIAN_ID if example_id.startswith("ex-lex-") else WIKIBOOKS_ID
        if example_id.startswith("ex-mix-"):
            source = WIKIBOOKS_ID
        self.examples.append({
            "id": example_id,
            "kind": "LEXICAL",
            "letterId": "",
            "display": display,
            "note": note,
            "sourceId": source if not example_id.startswith("ex-or-") else OPENRUSSIAN_ID,
            "reviewStatus": PENDING,
            "objectiveId": objective,
        })

    def item(self, item_id, prompt, prompt_letter, options, answer, feedback, objective, source_id, kind, rationales, assessment=None) -> None:
        if answer not in options or len(options) != len(set(options)) or options.count(answer) != 1:
            raise SystemExit(f"Unusable answer key for {item_id}: {options} / {answer}")
        if len(rationales) != len(options):
            raise SystemExit(f"{item_id} distractor rationales do not match the options")
        for option, rationale in zip(options, rationales):
            if option == answer and rationale != "":
                raise SystemExit(f"{item_id} records a rationale for the answer")
            if option != answer and not rationale:
                raise SystemExit(f"{item_id} distractor has no rationale")
        self.items.append({
            "id": item_id,
            "kind": kind,
            "language": "en",
            "prompt": prompt,
            "promptLetter": prompt_letter,
            "options": options,
            "answer": answer,
            "feedback": feedback,
            "objectiveId": objective,
            "sourceId": source_id,
            "reviewStatus": PENDING,
            "containsGeneratedRussian": False,
            "distractorRationales": rationales,
        })
        if assessment is not None:
            self.items[-1]["assessment"] = assessment


def _entry(form: str, gloss: str) -> str:
    suffix = "" if str(gloss).endswith((".", "?", "!")) else "."
    return f"{form} — {gloss}{suffix} "


def _step(step_id, kind, title, body, item_ids=None, example_ids=None, remediation=None, book=None, reference_ids=None) -> dict:
    return {
        "id": step_id,
        "kind": kind,
        "title": title,
        "body": body,
        "dominantAction": "Continue",
        "referenceIds": reference_ids or [],
        "itemIds": item_ids or [],
        "exampleIds": example_ids or [],
        "unavailableReason": None,
        "bookDirection": book,
        "companionDirection": None,
        "remediationStepId": remediation,
    }


def _chain(builder: Builder, number: int, specs: list[dict], orient_title: str, orient_body: str):
    sessions = [{
        "id": f"u{number}-s-orient",
        "title": orient_title,
        "estimatedMinutes": 8,
        "objectiveIds": [spec["id"] for spec in specs] + [f"obj-u{number}-book"],
        "steps": [_step(
            f"u{number}-st-orient", "ORIENT", builder.say(number, orient_title), builder.say(number, orient_body),
            book=builder.say(number, f"Open Lesson {number} in the copy you own. The app records do not replace that lesson."),
            reference_ids=["ref-u610-attribution"],
        )],
    }]
    objectives = []
    for spec in specs:
        slug = spec["slug"]
        remediation = f"u{number}-st-rem-{slug}"
        steps = []
        teach_ids = []
        guided_ids = []
        for index, card in enumerate(spec["cards"], start=1):
            teach = f"u{number}-st-teach-{slug}-{index}"
            teach_ids.append(teach)
            steps.append(_step(teach, "TEACH", card["title"], card["body"], example_ids=card["examples"], reference_ids=["ref-u610-attribution"]))
            if card["guided"]:
                guided_id = f"u{number}-st-guided-{slug}-{index}"
                guided_ids.append(guided_id)
                steps.append(_step(
                    guided_id, "GUIDED", builder.say(number, "Guided practice"),
                    builder.say(number, "Choose the admitted record. The feedback states the frozen field."),
                    item_ids=card["guided"], remediation=remediation,
                ))
        steps.append(_step(
            remediation, "REMEDIATE", builder.say(number, "Remediation"),
            spec["remediation"],
            example_ids=spec["examples"][:2],
        ))
        check_id = f"u{number}-st-check-{slug}"
        steps.append(_step(
            check_id, "CHECK", builder.say(number, "Independent checks"),
            builder.say(number, "These checks use only admitted records. A miss returns to the explanation."),
            item_ids=spec["checks"], remediation=remediation,
        ))
        sessions.append({
            "id": f"u{number}-s-{slug}",
            "title": spec["title"],
            "estimatedMinutes": 12,
            "objectiveIds": [spec["id"]],
            "steps": steps,
        })
        objectives.append({
            "id": spec["id"],
            "unitNumber": number,
            "title": spec["title"],
            "kind": "RECOGNITION",
            "instructionStepIds": [f"u{number}-st-orient", *teach_ids],
            "exampleIds": spec["examples"],
            "practiceStepIds": guided_ids or [teach_ids[0]],
            "checkItemIds": spec["checks"],
            "remediationStepIds": [remediation],
        })
    book_steps = [
        _step(
            f"u{number}-st-book-work", "BOOK", builder.say(number, f"Work Lesson {number} in your copy"),
            builder.say(number, f"Do Lesson {number} in the copy you own. Use that book's explanations and practice. The proposed records in this unit are a separate licensed set and do not copy the book.")
            + " " + spec_note(builder, number),
            book=builder.say(number, f"Open Lesson {number} in your copy and complete its lesson work."),
            reference_ids=["ref-u610-attribution"],
        ),
        _step(
            f"u{number}-st-book", "BOOK", builder.say(number, f"Record Lesson {number} book work"),
            builder.say(number, f"When you have done Lesson {number} in your copy, self-attest here. Attestation records your report. It does not grade the lesson, approve a mapping, or approve the proposed records."),
            book=builder.say(number, f"Self-attest only after the Lesson {number} work in your copy."),
        ),
    ]
    sessions.append({
        "id": f"u{number}-s-book",
        "title": f"Self-attest Lesson {number}",
        "estimatedMinutes": 10,
        "objectiveIds": [f"obj-u{number}-book"],
        "steps": book_steps,
    })
    objectives.append({
        "id": f"obj-u{number}-book",
        "unitNumber": number,
        "title": f"Self-attest Lesson {number} book work",
        "kind": "SELF_ATTESTATION",
        "instructionStepIds": [f"u{number}-st-book-work", f"u{number}-st-book"],
        "exampleIds": [],
        "practiceStepIds": [],
        "checkItemIds": [],
        "remediationStepIds": [],
    })
    return sessions, objectives


def spec_note(builder: Builder, number: int) -> str:
    notes = {
        6: "Names and dialogues stay in the copy you own. Listening stays with the book. No audio is mapped here.",
        7: "The introductory adverb objective is not prepared because the frozen adjectives page does not supply a bounded adverb paradigm outside the short-form section. That section stays out of this unit. Listening stays with the book. No audio is mapped here.",
        8: "A full buying conversation stays in the copy you own. Listening stays with the book. No audio is mapped here.",
        9: "These frozen quantity examples are not a statement of a present-day amount. Listening stays with the book. No audio is mapped here.",
        10: "This unit does not teach a pattern that leaves the admitted word out. Listening stays with the book. No audio is mapped here.",
    }
    return builder.say(number, notes[number])


GRAMMAR_KINDS = {
    "FORM_SELECTION",
    "CASE_RECOGNITION",
    "CONTRAST_SELECTION",
    "PATTERN_CLASSIFICATION",
    "NUMBER_FORM_SELECTION",
}


class Lesson:
    def __init__(self, builder: Builder, number: int) -> None:
        self.builder = builder
        self.number = number
        self.specs: list[dict] = []

    def _regions(self, item_id: str, objective: str, page: str, needle: str, text: str, record_type: str) -> None:
        self.builder.wiki(item_id, self.number, objective, page, needle, text, record_type=record_type)
        for piece_index, match in enumerate(REGION.finditer(text)):
            piece = match.group(0).strip().rstrip("?!.,")
            if piece and piece != text.strip().rstrip("?!.,"):
                self.builder.wiki(
                    f"{item_id}-r{piece_index}", self.number, objective, page, needle, piece, record_type="example",
                )

    def objective(
        self,
        slug: str,
        title: str,
        kind: str,
        remediation: tuple[str, str, str],
        examples: list[tuple],
        checks: list[tuple],
        extra: list[tuple] | None = None,
    ) -> None:
        if kind not in GRAMMAR_KINDS:
            raise SystemExit(f"{slug} kind {kind} is not a governed grammar check")
        builder = self.builder
        unit = self.number
        objective = f"obj-u{unit}-{slug}"
        example_ids: list[str] = []
        rendered: list[tuple[str, str, str]] = []
        for index, row in enumerate(examples):
            if row[0] == "wiki":
                _, page, needle, ru, en = row
                if not CYRILLIC.search(ru):
                    raise SystemExit(f"{objective} example is English instructional prose: {ru!r}")
                example_id = f"ex-u{unit}-{slug}-{index}"
                self._regions(f"prov-{example_id}-ru", objective, page, needle, ru, "example")
                builder.wiki(f"prov-{example_id}-en", unit, objective, page, needle, en, record_type="meaning")
                builder.example(example_id, ru, en, objective)
            elif row[0] == "field":
                _, record_id, field_name = row
                example_id = f"ex-or-u{unit}-{slug}-{index}"
                ru = builder.field(f"prov-{example_id}", unit, objective, record_id, field_name)
                en = builder.gloss(f"prov-{example_id}-gloss", unit, objective, record_id)
                if not CYRILLIC.search(ru):
                    raise SystemExit(f"{objective} field example has no Russian display")
                builder.example(example_id, ru, en, objective)
            else:
                raise SystemExit(f"Unknown example row {row[0]}")
            example_ids.append(example_id)
            rendered.append((example_id, ru, en))
        if len(example_ids) < 2:
            raise SystemExit(f"{objective} has fewer than two examples")
        page, needle, remediation_text = remediation
        self._regions(f"prov-u{unit}-{slug}-rem", objective, page, needle, remediation_text, "instruction")
        for index, (extra_page, extra_needle, extra_text, record_type) in enumerate(extra or []):
            self._regions(
                f"prov-u{unit}-{slug}-x-{index}", objective, extra_page, extra_needle, extra_text, record_type,
            )
        if len(checks) < 3:
            raise SystemExit(f"{objective} needs one guided check and two independent checks")
        cards = []
        first = rendered[:2]
        cards.append({
            "title": builder.say(unit, "Admitted lines"),
            "body": "".join(_entry(ru, en) for _eid, ru, en in first),
            "examples": [example_id for example_id, _ru, _en in first],
            "guided": [self._check(slug, objective, kind, 0, checks[0], guided=True)],
        })
        cards.append({
            "title": builder.say(unit, "Admitted instruction"),
            "body": remediation_text,
            "examples": [],
            "guided": [],
        })
        for offset in range(2, len(rendered), 2):
            chunk = rendered[offset:offset + 2]
            cards.append({
                "title": builder.say(unit, "Admitted lines"),
                "body": "".join(_entry(ru, en) for _eid, ru, en in chunk),
                "examples": [example_id for example_id, _ru, _en in chunk],
                "guided": [],
            })
        independent = [
            self._check(slug, objective, kind, index, check, guided=False)
            for index, check in enumerate(checks[1:], start=1)
        ]
        self.specs.append({
            "id": objective,
            "slug": slug,
            "title": title,
            "cards": cards,
            "checks": independent,
            "examples": example_ids,
            "remediation": remediation_text,
        })

    def _ids(self, objective: str, text: str) -> list[str]:
        return [
            record["itemId"]
            for record in self.builder.provenance
            if record["objectiveId"] == objective and record["shippedText"] == text
        ]

    def _check(self, slug: str, objective: str, kind: str, index: int, check: dict, guided: bool) -> str:
        prompt = check["prompt"]
        shown = check["shown"]
        answer = check["answer"]
        distractors = check["distractors"]
        source_id = check["source"]
        options = [answer, *[text for text, _rationale, _difference in distractors]]
        rationales = ["", *[self.builder.say(self.number, rationale) for _text, rationale, _difference in distractors]]
        differences = ["", *[self.builder.say(self.number, difference) for _text, _rationale, difference in distractors]]
        why = self.builder.say(self.number, check["why"])
        item_id = f"u{self.number}-{'g' if guided else 'i'}-{slug}-{index}"
        answer_ids = self._ids(objective, answer)
        if not answer_ids:
            raise SystemExit(f"{item_id} answer has no provenance: {answer!r}")
        distractor_ids = [""]
        for text, _rationale, _difference in distractors:
            ids = self._ids(objective, text)
            if not ids:
                raise SystemExit(f"{item_id} distractor has no provenance: {text!r}")
            distractor_ids.append(ids[0])
        feedback = why
        if not str(answer).endswith((".", "?", "!")) and not feedback.endswith((".", "?", "!")):
            feedback += "."
        feedback = feedback + " " + answer
        if not str(answer).endswith((".", "?", "!")):
            feedback += "."
        self.builder.item(
            item_id,
            self.builder.say(self.number, prompt),
            shown,
            options,
            answer,
            feedback,
            objective,
            source_id,
            kind,
            rationales,
            {
                "testedFeature": check["feature"],
                "evidenceMode": check["mode"],
                "contrastSetId": f"u{self.number}-{slug}-{index}",
                "cueLanguage": check["cue"],
                "independent": not guided,
                "answerSourceIds": [answer_ids[0]],
                "distractorSourceIds": distractor_ids,
                "distractorDifference": differences,
                "whyAnswerDemonstratesObjective": why,
                "contrastMembers": options,
            },
        )
        return item_id


def build_units_6_10() -> dict:
    from units_6_10_lessons import populate

    frozen = FrozenSources()
    builder = Builder(frozen)
    units = [
        _unit(builder, 6, "Admitted possession, objects, and direction", "Proposed Unit 6 uses frozen records for possession, objects, and direction. Qualified review is pending.", 18, populate),
        _unit(builder, 7, "Admitted agreement rows", "Proposed Unit 7 uses frozen agreement rows. The introductory adverb objective is not prepared because the frozen adjectives page does not supply a bounded adverb paradigm outside the short-form section. Qualified review is pending.", 22, populate),
        _unit(builder, 8, "Admitted plural and availability rows", "Proposed Unit 8 uses frozen plural and availability rows. Qualified review is pending.", 18, populate),
        _unit(builder, 9, "Admitted number and quantity rows", "Proposed Unit 9 uses frozen number and quantity rows. Qualified review is pending.", 26, populate),
        _unit(builder, 10, "Admitted possession and expanded rows", "Proposed Unit 10 uses frozen possession and expanded rows. Qualified review is pending.", 22, populate),
    ]
    _assert(builder, units)
    return {
        "units": units,
        "items": builder.items,
        "examples": builder.examples,
        "provenance": builder.provenance,
        "authored": builder.authored,
        "references": [_attribution_reference()],
    }


def _unit(builder, number, title, body, minimum, populate):
    lesson = Lesson(builder, number)
    populate(lesson, number)
    independent = sum(len(spec["checks"]) for spec in lesson.specs)
    if independent < minimum:
        raise SystemExit(f"Unit {number} has {independent} independent checks, needs {minimum}")
    sessions, objectives = _chain(builder, number, lesson.specs, title, body)
    return {"number": number, "sessions": sessions, "objectives": objectives, "minimum_checks": minimum}


def _attribution_reference() -> dict:
    return {
        "id": "ref-u610-attribution",
        "title": "Units 6–10 source notice",
        "summary": "Proposed Units 6–10 adapt CC BY-SA 4.0 dictionary rows and Wikibooks grammar excerpts.",
        "detail": (
            "OpenRussian dictionary data is frozen at commit 50e210c4803237779cb562bc1abcea529066031c "
            "under CC BY-SA 4.0. Wikibooks excerpts are frozen by oldid under the CC BY-SA 4.0 route. "
            "The adapted selection and arrangement are share-alike. Application code, user data, and user recordings are outside that statement. "
            "No audio is included. Learner-facing dictionary glosses are delimited sense selections. Review is pending."
        ),
        "introducedInUnit": 6,
        "sourceId": WIKIBOOKS_ID,
        "classification": "LICENSE_OR_ATTRIBUTION",
        "reviewStatus": "NOT_REQUIRED",
        "searchTerms": ["units 6-10 source notice"],
    }


def _assert(builder: Builder, units: list[dict]) -> None:
    for unit in units:
        number = unit["number"]
        spans = [record["shippedText"] for record in builder.provenance if record["unitNumber"] == number]
        spans += [record["text"] for record in builder.authored if record["unitNumber"] == number]
        allowed = set(spans)
        hosts: list[str] = []
        for session in unit["sessions"]:
            for step in session["steps"]:
                hosts.extend([step["title"], step["body"], step.get("bookDirection") or ""])
                if step["kind"] == "TEACH" and step["body"].count(" — ") > 4:
                    raise SystemExit(f"{step['id']} lists too many examples")
        for example in builder.examples:
            if example["objectiveId"].startswith(f"obj-u{number}-"):
                if not CYRILLIC.search(example["display"]):
                    raise SystemExit(f"Unit {number} example {example['id']} stores English instructional prose as a Russian example")
                hosts.extend([example["display"], example["note"]])
        for item in builder.items:
            if not item["objectiveId"].startswith(f"obj-u{number}-"):
                continue
            if item["kind"] not in GRAMMAR_KINDS:
                raise SystemExit(f"Unit {number} item {item['id']} is not a governed grammar check")
            hosts.extend([
                item["prompt"], item["promptLetter"], item["answer"], item["feedback"],
                *item["options"], *item["distractorRationales"],
            ])
            assessment = item.get("assessment")
            if assessment:
                hosts.append(assessment["whyAnswerDemonstratesObjective"])
                hosts.extend(assessment["distractorDifference"])
            for text in [item["answer"], *item["options"]]:
                if text not in allowed:
                    raise SystemExit(f"Unit {number} answer or option has no admitted record: {text}")
        for host in hosts:
            for match in REGION.finditer(host):
                text = match.group(0).strip()
                trimmed = text.rstrip("?!.,")
                if text not in spans and trimmed not in spans:
                    raise SystemExit(f"Unit {number} unsourced Russian region: {text}")
            leftover = unbound_letters(host, spans)
            if leftover:
                raise SystemExit(f"Unit {number} unbound English: {leftover} in {host!r}")
            for dirty in DIRTY_LEARNER_TEXT:
                if dirty in host:
                    raise SystemExit(f"Unit {number} ships a dirty gloss: {dirty}")


if __name__ == "__main__":
    built = build_units_6_10()
    print(
        f"provenance={len(built['provenance'])} authored={len(built['authored'])} "
        f"items={len(built['items'])} examples={len(built['examples'])} units={len(built['units'])}"
    )
