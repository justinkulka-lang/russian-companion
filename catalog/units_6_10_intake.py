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

    def item(self, item_id, prompt, prompt_letter, options, answer, feedback, objective, source_id, kind) -> None:
        if answer not in options or len(options) != len(set(options)):
            raise SystemExit(f"Unusable answer key for {item_id}: {options} / {answer}")
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
        })


def _entry(form: str, gloss: str) -> str:
    return f"{form} — {gloss}. "


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
            builder.say(number, "Return to the explanation. Compare the shown form with the admitted record. Do not invent a form that is absent from the record."),
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
        7: "The short-form section on the frozen page stays out of this unit. Listening stays with the book. No audio is mapped here.",
        8: "A full buying conversation stays in the copy you own. Listening stays with the book. No audio is mapped here.",
        9: "These frozen quantity examples are not a statement of a present-day amount. Listening stays with the book. No audio is mapped here.",
        10: "This unit does not teach a pattern that leaves the admitted word out. Listening stays with the book. No audio is mapped here.",
    }
    return builder.say(number, notes[number])


class Lesson:
    def __init__(self, builder: Builder, number: int) -> None:
        self.builder = builder
        self.number = number
        self.specs: list[dict] = []

    def objective(self, slug: str, title: str, pairs: list[tuple], fields: list[tuple] | None = None) -> None:
        builder = self.builder
        unit = self.number
        objective = f"obj-u{unit}-{slug}"
        examples: list[str] = []
        rendered: list[tuple[str, str, str]] = []
        for index, (page, needle, ru, en) in enumerate(pairs):
            example_id = f"ex-u{unit}-{slug}-{index}"
            builder.wiki(f"prov-{example_id}-ru", unit, objective, page, needle, ru)
            builder.wiki(f"prov-{example_id}-en", unit, objective, page, needle, en)
            for piece_index, match in enumerate(REGION.finditer(ru)):
                piece = match.group(0).strip().rstrip("?!.,")
                if piece and piece != ru.strip().rstrip("?!.,"):
                    builder.wiki(f"prov-{example_id}-ru-{piece_index}", unit, objective, page, needle, piece)
            builder.example(example_id, ru, en, objective)
            examples.append(example_id)
            rendered.append((example_id, ru, en))
        for index, (record_id, field_name) in enumerate(fields or []):
            example_id = f"ex-or-u{unit}-{slug}-{index}"
            form = builder.field(f"prov-{example_id}", unit, objective, record_id, field_name)
            gloss = builder.gloss(f"prov-{example_id}-gloss", unit, objective, record_id)
            builder.example(example_id, form, gloss, objective)
            examples.append(example_id)
            rendered.append((example_id, form, gloss))
        if len(examples) < 2:
            raise SystemExit(f"{objective} has fewer than two examples")
        cards = []
        checks = []
        guided_offsets = {0}
        if len(rendered) >= 4:
            guided_offsets.add(2)
        pool = []
        seen = set()
        for _example_id, ru, en in rendered:
            if en not in seen:
                seen.add(en)
                pool.append(en)
        if len(pool) < 2:
            raise SystemExit(f"{objective} needs two distinct glosses")
        for offset in range(0, len(rendered), 2):
            chunk = rendered[offset:offset + 2]
            body = builder.say(unit, "The frozen record supplies this group. ") + "".join(_entry(ru, en) for _eid, ru, en in chunk)
            if body.count(" — ") > 4:
                raise SystemExit(f"{objective} card lists too many examples")
            guided = []
            if offset in guided_offsets and offset < len(rendered):
                example_id, ru, en = chunk[0]
                item_id = f"u{unit}-g-{slug}-{offset}"
                options = [en, next(other for other in pool if other != en)]
                builder.item(
                    item_id, builder.say(unit, "Which admitted gloss is recorded for the shown line?"), ru, options, en,
                    builder.say(unit, "The frozen record lists this gloss: ") + en + builder.say(unit, "."),
                    objective, WIKIBOOKS_ID if example_id.startswith("ex-u") else OPENRUSSIAN_ID, "SOURCE_MEANING",
                )
                guided = [item_id]
            cards.append({
                "title": builder.say(unit, "Admitted lines"),
                "body": body,
                "examples": [example_id for example_id, _ru, _en in chunk],
                "guided": guided,
            })
        for index, (example_id, ru, en) in enumerate(rendered):
            if index in guided_offsets:
                continue
            item_id = f"u{unit}-i-{slug}-{index}"
            options = [en]
            for other in pool:
                if other != en and other not in options:
                    options.append(other)
                if len(options) == 3:
                    break
            builder.item(
                item_id, builder.say(unit, "Which admitted gloss is recorded for the shown line?"), ru, options, en,
                builder.say(unit, "The frozen record lists this gloss: ") + en + builder.say(unit, "."),
                objective, WIKIBOOKS_ID if example_id.startswith("ex-u") else OPENRUSSIAN_ID, "SOURCE_FORM",
            )
            checks.append(item_id)
        if len(checks) < 2:
            raise SystemExit(f"{objective} has fewer than two independent checks")
        self.specs.append({"id": objective, "slug": slug, "title": title, "cards": cards, "checks": checks, "examples": examples})


def build_units_6_10() -> dict:
    frozen = FrozenSources()
    builder = Builder(frozen)
    units = [
        _unit(builder, 6, "Admitted possession, objects, and direction", "Proposed Unit 6 uses frozen records for possession, objects, and direction. Qualified review is pending.", _unit_6_rows(), 18),
        _unit(builder, 7, "Admitted agreement rows", "Proposed Unit 7 uses frozen agreement rows. Qualified review is pending.", _unit_7_rows(), 22),
        _unit(builder, 8, "Admitted plural and availability rows", "Proposed Unit 8 uses frozen plural and availability rows. Qualified review is pending.", _unit_8_rows(), 18),
        _unit(builder, 9, "Admitted number and quantity rows", "Proposed Unit 9 uses frozen number and quantity rows. Qualified review is pending.", _unit_9_rows(), 26),
        _unit(builder, 10, "Admitted possession and expanded rows", "Proposed Unit 10 uses frozen possession and expanded rows. Qualified review is pending.", _unit_10_rows(), 22),
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


def _unit(builder, number, title, body, rows, minimum):
    lesson = Lesson(builder, number)
    for slug, objective_title, pairs, fields in rows:
        lesson.objective(slug, objective_title, pairs, fields)
    independent = sum(len(spec["checks"]) for spec in lesson.specs)
    if independent < minimum:
        raise SystemExit(f"Unit {number} has {independent} independent checks, needs {minimum}")
    sessions, objectives = _chain(builder, number, lesson.specs, title, body)
    return {"number": number, "sessions": sessions, "objectives": objectives, "minimum_checks": minimum}


def _unit_6_rows():
    return [
        ("possessive", "Recognize admitted possessive rows", [
            ("gender", "my table' ('''мой стол''')", "мой стол", "my table"),
            ("gender", "my dog' ('''моя собака''')", "моя собака", "my dog"),
            ("accusative", "| Our || Наш || Нашего || нашу || Наше || Наши || Наших", "Наш", "Our"),
            ("accusative", "| Your <small>''(formal/plural)''</small> || Ваш || Вашего ||вашу || Ваше || Ваши || Ваших", "Ваш", "Your"),
        ], [
            ("adjectives.csv:мой", "decl_f_nom"),
            ("adjectives.csv:мой", "decl_n_nom"),
            ("adjectives.csv:мой", "decl_pl_nom"),
            ("adjectives.csv:твой", "decl_m_nom"),
            ("adjectives.csv:твой", "decl_f_nom"),
        ]),
        ("third", "Recognize admitted third-person forms", [
            ("accusative", "the masculine and neuter possessive pronouns are the same as the personal pronouns: '''его'''.", "его", "personal pronouns"),
            ("accusative", "These do not decline according to the gender or number of the modified noun.", "These do not decline according to the gender or number of the modified noun.", "do not decline"),
            ("accusative", "I want his cat - '''Я хочу его кота'''", "Я хочу его кота", "I want his cat"),
            ("accusative", "I want his cats - '''Я хочу его котов'''", "Я хочу его котов", "I want his cats"),
        ], [
            ("others.csv:его", "accented"),
            ("others.csv:её", "accented"),
            ("others.csv:их", "accented"),
        ]),
        ("object", "Recognize admitted object lines", [
            ("accusative", "I like dogs - '''Я люблю собак'''", "Я люблю собак", "I like dogs"),
            ("accusative", "I love you\" as '''Я люблю тебя''' and be grammatically correct, it is more commonly said as '''Я тебя люблю'''", "Я тебя люблю", "I love you"),
            ("accusative", "Dogs hate cats. - '''Собаки ненавидят котов.'''", "Собаки ненавидят котов.", "Dogs hate cats."),
            ("accusative", "Cats hate dogs. - '''Коты ненавидят собак.'''", "Коты ненавидят собак.", "Cats hate dogs."),
        ], []),
        ("feminine", "Recognize admitted feminine object fields", [
            ("accusative", "Feminine words are the only words to have their own accusative ending:", "Feminine words are the only words to have their own accusative ending:", "Feminine words are the only words to have their own accusative ending:"),
        ], [
            ("nouns.csv:книга", "sg_nom"),
            ("nouns.csv:книга", "sg_acc"),
            ("nouns.csv:мама", "sg_acc"),
            ("nouns.csv:женщина", "sg_acc"),
        ]),
        ("animacy", "Recognize admitted animate and inanimate fields", [
            ("accusative", "So, masculine nouns denoting inanimate objects in the accusative case are the same as in the nominative case.", "So, masculine nouns denoting inanimate objects in the accusative case are the same as in the nominative case.", "So, masculine nouns denoting inanimate objects in the accusative case are the same as in the nominative case."),
            ("accusative", "If they're animated nouns, they follow the genitive rules", "If they're animated nouns, they follow the genitive rules", "If they're animated nouns, they follow the genitive rules"),
        ], [
            ("nouns.csv:стол", "sg_acc"),
            ("nouns.csv:дом", "sg_acc"),
            ("nouns.csv:брат", "sg_acc"),
            ("nouns.csv:учитель", "sg_acc"),
            ("nouns.csv:друг", "sg_acc"),
        ]),
        ("pronoun", "Recognize admitted personal object forms", [
            ("accusative", "| I/me || Я || Меня", "Меня", "I/me"),
            ("accusative", "| We/us || Мы || Нас", "Нас", "We/us"),
            ("accusative", "| You || Ты || Тебя", "Тебя", "You"),
            ("accusative", "| You || Вы || Вас", "Вас", "You"),
            ("accusative", "| He/him || Он || Его", "Его", "He/him"),
            ("accusative", "| She/her || Она || Её", "Её", "She/her"),
            ("accusative", "| They/them || Они || Их", "Их", "They/them"),
        ], []),
        ("direction", "Recognize admitted direction and location lines", [
            ("accusative", "I walked ''into'' the house' ('''Я шёл в дом''')", "Я шёл в дом", "the house"),
            ("accusative", "I lived in London' ('''Я жил в Лондоне''')", "Я жил в Лондоне", "I lived in London"),
            ("accusative", "I walked ''onto'' the street' ('''Я шёл на улицу''')", "Я шёл на улицу", "the street"),
            ("accusative", "It's on the table' ('''Оно на столе''')", "Оно на столе", "It's on the table"),
            ("accusative", "I drove to the museum. - '''Я поехал в музей.'''", "Я поехал в музей.", "I drove to the museum."),
            ("accusative", "generally the accusative case denotes ''direction''", "direction", "direction"),
            ("accusative", "while the prepositional case denotes ''location''", "location", "location"),
        ], []),
    ]


def _unit_7_rows():
    return [
        ("default", "Recognize admitted dictionary forms", [
            ("adjectives", "The default form of an adjective is its nominative, masculine, singular form, and this is the form given in dictionaries.", "The default form of an adjective is its nominative, masculine, singular form, and this is the form given in dictionaries.", "dictionaries"),
        ], [
            ("adjectives.csv:новый", "decl_m_nom"),
            ("adjectives.csv:хороший", "decl_m_nom"),
            ("adjectives.csv:синий", "decl_m_nom"),
            ("adjectives.csv:какой", "decl_m_nom"),
        ]),
        ("endings", "Recognize admitted agreement lines", [
            ("adjectives", "\"new pencil\" is '''новый карандаш'''", "новый карандаш", "new pencil"),
            ("adjectives", "\"new car\" is '''новая машина'''", "новая машина", "new car"),
            ("adjectives", "\"new dress\" is '''новое платье'''", "новое платье", "new dress"),
            ("adjectives", "the dark blue table - '''синий стол.'''", "синий стол", "the dark blue table"),
            ("adjectives", "the dark blue book - '''синяя книга'''", "синяя книга", "the dark blue book"),
            ("adjectives", "the dark blue letter - '''синее письмо'''", "синее письмо", "the dark blue letter"),
            ("adjectives", "the dark blue books - '''синие книги'''", "синие книги", "the dark blue books"),
        ], [
            ("adjectives.csv:новый", "decl_f_nom"),
            ("adjectives.csv:новый", "decl_n_nom"),
            ("adjectives.csv:новый", "decl_pl_nom"),
        ]),
        ("spelling", "Recognize admitted spelling lines", [
            ("adjectives", "good letter' is written '''хорошее письмо'''", "хорошее письмо", "good letter"),
            ("adjectives", "you add '''и''', not '''ы'''", "и", "и"),
            ("adjectives", "any unstressed '''o''' in an ending is instead written as '''e'''", "e", "e"),
        ], [
            ("adjectives.csv:хороший", "decl_n_nom"),
            ("adjectives.csv:хороший", "decl_pl_nom"),
        ]),
        ("question", "Recognize admitted question forms", [
            ("adjectives", "good letter' is written '''хорошее письмо'''", "хорошее письмо", "good letter"),
        ], [
            ("adjectives.csv:какой", "decl_f_nom"),
            ("adjectives.csv:какой", "decl_n_nom"),
            ("adjectives.csv:какой", "decl_pl_nom"),
        ]),
        ("demonstrative", "Recognize admitted demonstrative forms", [
            ("adjectives", "\"new pencil\" is '''новый карандаш'''", "новый карандаш", "new pencil"),
        ], [
            ("adjectives.csv:этот", "decl_f_nom"),
            ("adjectives.csv:этот", "decl_n_nom"),
            ("adjectives.csv:этот", "decl_pl_nom"),
            ("adjectives.csv:этот", "decl_f_acc"),
        ]),
        ("place", "Recognize admitted place-agreement lines", [
            ("adjectives", "They were talking about a good new cinema' - '''Они говорили о хорошем новом кино'''", "Они говорили о хорошем новом кино", "They were talking about a good new cinema"),
            ("adjectives", "They were in the old Russian school' - '''Они были в старой русской школе'''", "Они были в старой русской школе", "They were in the old Russian school"),
            ("adjectives", "I want to live in a clean new city - '''Я хочу́ жить в чи́стом но́вом го́роде'''", "Я хочу́ жить в чи́стом но́вом го́роде", "I want to live in a clean new city"),
        ], [
            ("adjectives.csv:новый", "decl_m_prep"),
            ("adjectives.csv:хороший", "decl_m_prep"),
        ]),
        ("objectadj", "Recognize admitted object-agreement lines", [
            ("adjectives", "I want a new chair and a new letter - '''Я хочу новый стул и новое письмо'''", "Я хочу новый стул и новое письмо", "I want a new chair and a new letter"),
            ("adjectives", "I want a new rabbit - '''Я хочу нового кролика.'''", "Я хочу нового кролика.", "I want a new rabbit"),
            ("adjectives", "My Russian cat likes my new dog - '''Моя русская кошка любит мою новую собаку'''", "Моя русская кошка любит мою новую собаку", "My Russian cat likes my new dog"),
            ("accusative", "I like these women. - '''Я люблю ''этих женщин.'''", "этих женщин", "I like these women."),
        ], [
            ("adjectives.csv:новый", "decl_f_acc"),
        ]),
    ]


def _unit_8_rows():
    return [
        ("plural", "Recognize admitted plural fields", [
            ("gender", "There's no 'masculine plural' and 'feminine plural' - there's just, 'plural'.", "plural", "There's no 'masculine plural' and 'feminine plural' - there's just, 'plural'."),
        ], [
            ("nouns.csv:стол", "pl_nom"),
            ("nouns.csv:город", "pl_nom"),
            ("nouns.csv:книга", "pl_nom"),
            ("nouns.csv:окно", "pl_nom"),
            ("nouns.csv:мама", "pl_nom"),
            ("nouns.csv:дом", "pl_nom"),
            ("nouns.csv:слово", "pl_nom"),
        ]),
        ("spelling", "Recognize admitted plural spelling lines", [
            ("adjectives", "if the stem ends in '''г, ж, к, х, ч, ш''', or '''щ''', you add '''и''', not '''ы'''", "и", "и"),
            ("adjectives", "good letter' is written '''хорошее письмо'''", "хорошее письмо", "good letter"),
        ], [
            ("adjectives.csv:хороший", "decl_pl_nom"),
            ("nouns.csv:книга", "pl_nom"),
            ("nouns.csv:день", "pl_nom"),
        ]),
        ("irregular", "Recognize admitted irregular plural fields", [
            ("genitive", "Table, five tables - '''Стол, пять столов'''", "пять столов", "five tables"),
        ], [
            ("nouns.csv:брат", "pl_nom"),
            ("nouns.csv:друг", "pl_nom"),
            ("nouns.csv:человек", "pl_nom"),
            ("nouns.csv:сын", "pl_nom"),
        ]),
        ("agreement", "Recognize admitted plural agreement fields", [
            ("adjectives", "the dark blue books - '''синие книги'''", "синие книги", "the dark blue books"),
            ("accusative", "| Our || Наш || Нашего || нашу || Наше || Наши || Наших", "Наши", "Our"),
        ], [
            ("adjectives.csv:мой", "decl_pl_nom"),
            ("adjectives.csv:новый", "decl_pl_nom"),
            ("adjectives.csv:этот", "decl_pl_nom"),
        ]),
        ("availability", "Recognize admitted availability lines", [
            ("genitive", "There are Frenchmen on this street - '''На э́той у́лице есть францу́зы'''", "На э́той у́лице есть францу́зы", "There are Frenchmen on this street"),
            ("genitive", "There are old buildings in Moscow - '''В Москве́ есть ста́рые зда́ния'''", "В Москве́ есть ста́рые зда́ния", "There are old buildings in Moscow"),
            ("genitive", "This is David's house - '''Э́то - дом Дэвида'''", "Э́то - дом Дэвида", "This is David's house"),
            ("genitive", "There are no restaurants - '''Нет рестора́нов'''", "Нет рестора́нов", "There are no restaurants"),
        ], [
            ("others.csv:есть", "accented"),
        ]),
    ]


def _unit_9_rows():
    num = "numbers"
    gen = "genitive"
    return [
        ("cardinal", "Recognize admitted cardinal lines", [
            (num, "0 ноль", "ноль", "0"),
            (num, "1 оди́н (одна́, одно́, одни́)", "оди́н", "1"),
            (num, "1 оди́н (одна́, одно́, одни́)", "одна́", "1"),
            (num, "2 два (две)", "два", "2"),
            (num, "2 два (две)", "две", "2"),
            (num, "3 три", "три", "3"),
            (num, "4 четы́ре", "четы́ре", "4"),
            (num, "5 пять", "пять", "5"),
            (num, "10 де́сять", "де́сять", "10"),
            (num, "11 оди́ннадцать", "оди́ннадцать", "11"),
            (num, "12 двена́дцать", "двена́дцать", "12"),
            (num, "13 трина́дцать", "трина́дцать", "13"),
            (num, "14 четы́рнадцать", "четы́рнадцать", "14"),
            (num, "20 два́дцать", "два́дцать", "20"),
            (num, "21 два́дцать оди́н", "два́дцать оди́н", "21"),
            (num, "100 сто", "сто", "100"),
            (num, "200 две́сти", "две́сти", "200"),
            (num, "215 две́сти пятна́дцать", "две́сти пятна́дцать", "215"),
        ], []),
        ("quantity", "Recognize the admitted quantity lines", [
            (num, "If the item needs to be in the nominative or inanimate accusative case, use the following rule.", "If the item needs to be in the nominative or inanimate accusative case, use the following rule.", "nominative or inanimate accusative"),
            (num, "1 -> nominative", "1 -> nominative", "1"),
            (num, "2, 3, 4 -> genitive singular", "2, 3, 4 -> genitive singular", "2, 3, 4"),
            (num, "5 and more -> genitive plural", "5 and more -> genitive plural", "5 and more"),
            (gen, "Notice that for the ''teens'' (11, 12, 13, 14, ... 19) ending on ''-надцать'' always the last rule is to be applied, no matter what digit they end on.", "11, 12, 13, 14", "11, 12, 13, 14"),
            (num, "When the items at hand belong not in the nominative or inanimate accusative case, the numerals themselves must be declined", "When the items at hand belong not in the nominative or inanimate accusative case, the numerals themselves must be declined", "not in the nominative or inanimate accusative case"),
        ], []),
        ("phrases", "Recognize admitted counted lines", [
            (num, "один час", "один час", "один час"),
            (num, "два часа", "два часа", "два часа"),
            (num, "пять часов", "пять часов", "пять часов"),
            (num, "двадцать один час", "двадцать один час", "двадцать один час"),
            (num, "двадцать два часа", "двадцать два часа", "двадцать два часа"),
            (num, "двести тринадцать собак", "двести тринадцать собак", "двести тринадцать собак"),
            (gen, "There is one cat - '''Есть оди́н кот'''", "Есть оди́н кот", "There is one cat"),
            (gen, "Two roubles - '''Два рубля́'''", "Два рубля́", "Two roubles"),
            (gen, "22 kopecks - '''Два́дцать две копе́йки'''", "Два́дцать две копе́йки", "22 kopecks"),
            (gen, "Five roubles - '''Пять рубле́й'''", "Пять рубле́й", "Five roubles"),
            (gen, "25 kopecks - '''Два́дцать пять копе́ек'''", "Два́дцать пять копе́ек", "25 kopecks"),
        ], []),
        ("endings", "Recognize admitted quantity fields", [
            (gen, "Table, five tables - '''Стол, пять столов'''", "пять столов", "five tables"),
            (gen, "Book, eight books - '''Кни́га, во́семь книг'''", "во́семь книг", "eight books"),
        ], [
            ("nouns.csv:стол", "sg_gen"),
            ("nouns.csv:книга", "sg_gen"),
            ("nouns.csv:окно", "sg_gen"),
            ("nouns.csv:стол", "pl_gen"),
            ("nouns.csv:книга", "pl_gen"),
            ("nouns.csv:слово", "pl_gen"),
            ("adjectives.csv:один", "accented") if False else ("others.csv:один", "accented"),
            ("others.csv:два", "accented"),
        ]),
    ]


def _unit_10_rows():
    gen = "genitive"
    return [
        ("have", "Recognize admitted possession lines", [
            (gen, "to say \"I have X\": '''У меня есть Х'''", "У меня есть Х", "I have X"),
            (gen, "Do you have a son? - '''У вас есть сын?'''", "У вас есть сын?", "Do you have a son?"),
            (gen, "Yes, I have a son - '''Да, у меня есть сын.'''", "Да, у меня есть сын.", "Yes, I have a son"),
            (gen, "Your husband has one friend - '''У твоего́ му́жа есть оди́н друг'''", "У твоего́ му́жа есть оди́н друг", "Your husband has one friend"),
            ("adjectives", "My new husband has many friends - '''У моего нового мужа есть много друзе́й'''", "У моего нового мужа есть много друзе́й", "My new husband has many friends"),
        ], [
            ("others.csv:есть", "accented"),
        ]),
        ("absence", "Recognize admitted absence lines", [
            (gen, "No, I don't have a son - '''У меня́ нет сы́на'''", "У меня́ нет сы́на", "No, I don't have a son"),
            (gen, "There are no restaurants - '''Нет рестора́нов'''", "Нет рестора́нов", "There are no restaurants"),
            (gen, "There are no old buildings in Moscow - '''В Москве́ нет ста́рых зда́ний'''", "В Москве́ нет ста́рых зда́ний", "There are no old buildings in Moscow"),
            (gen, "My brother doesn't have any money - '''У моего́ бра́та нет де́нег'''", "У моего́ бра́та нет де́нег", "My brother doesn't have any money"),
        ], [
            ("others.csv:нет", "accented"),
        ]),
        ("pronoun", "Recognize admitted pronoun forms", [
            (gen, "For him/it - '''Для него́'''", "Для него́", "For him/it"),
            (gen, "Without her - '''Без неё'''", "Без неё", "Without her"),
            (gen, "'''English''' || I || We || You || You || He/she/it|| They\n|-\n! '''Nominative'''\n| Я || Мы || Ты || Вы || Он/она/оно || Они\n|-\n! '''Genitive'''\n| Меня́ || Нас || Тебя́ || Вас || Его́/её || Их", "Меня́", "I"),
            (gen, "'''English''' || I || We || You || You || He/she/it|| They\n|-\n! '''Nominative'''\n| Я || Мы || Ты || Вы || Он/она/оно || Они\n|-\n! '''Genitive'''\n| Меня́ || Нас || Тебя́ || Вас || Его́/её || Их", "Его́/её", "He/she/it"),
            (gen, "'''English''' || I || We || You || You || He/she/it|| They\n|-\n! '''Nominative'''\n| Я || Мы || Ты || Вы || Он/она/оно || Они\n|-\n! '''Genitive'''\n| Меня́ || Нас || Тебя́ || Вас || Его́/её || Их", "Их", "They"),
        ], [
            ("others.csv:его", "accented"),
            ("others.csv:её", "accented"),
            ("others.csv:их", "accented"),
        ]),
        ("preposition", "Recognize admitted preposition lines", [
            (gen, "I want a present for my friend - '''Я хочу́ пода́рок для моего́ дру́га'''", "Я хочу́ пода́рок для моего́ дру́га", "I want a present for my friend"),
            (gen, "Black coffee - '''Ко́фе без молока́'''", "Ко́фе без молока́", "Black coffee"),
            (gen, "I'm from England - '''Я из А́нглии'''", "Я из А́нглии", "I'm from England"),
            (gen, "Get away from the grass - '''Отойти от травы'''", "Отойти от травы", "Get away from the grass"),
            (gen, "I am standing by the fence - '''Я стою́ у забо́ра'''", "Я стою́ у забо́ра", "I am standing by the fence"),
            (gen, "She is sitting by the window - '''Она сидит у окна́'''", "Она сидит у окна́", "She is sitting by the window"),
        ], [
            ("others.csv:для", "accented"),
            ("others.csv:без", "accented"),
            ("others.csv:из", "accented"),
            ("others.csv:от", "accented"),
            ("others.csv:у", "accented"),
        ]),
        ("adjective", "Recognize admitted genitive agreement", [
            (gen, "A kilogram of good Russian cheese - '''Кило́ хоро́шего ру́сского сы́ра'''", "Кило́ хоро́шего ру́сского сы́ра", "A kilogram of good Russian cheese"),
            (gen, "A kilogram of good Russian sausage - '''Кило́ хоро́шей ру́сской колбасы́'''", "Кило́ хоро́шей ру́сской колбасы́", "A kilogram of good Russian sausage"),
            ("adjectives", "I arrived from my old house - '''Я пришёл из моего старого дома'''", "Я пришёл из моего старого дома", "I arrived from my old house"),
        ], [
            ("adjectives.csv:новый", "decl_m_gen"),
            ("adjectives.csv:новый", "decl_f_gen"),
            ("adjectives.csv:новый", "decl_pl_gen"),
            ("adjectives.csv:хороший", "decl_m_gen"),
        ]),
        ("animate", "Recognize admitted animate plural lines", [
            ("accusative", "I like these women. - '''Я люблю ''этих женщин.'''", "этих женщин", "I like these women."),
            ("accusative", "These women like me. - '''''Эти женщины'' любят меня.'''", "Эти женщины", "These women like me."),
            ("accusative", "any plural word that's inanimate is identical to its", "any plural word that's inanimate is identical to its", "inanimate"),
        ], [
            ("nouns.csv:женщина", "pl_acc"),
            ("nouns.csv:брат", "pl_acc"),
            ("nouns.csv:друг", "pl_acc"),
        ]),
    ]


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
                hosts.extend([example["display"], example["note"]])
        for item in builder.items:
            if not item["objectiveId"].startswith(f"obj-u{number}-"):
                continue
            hosts.extend([item["prompt"], item["promptLetter"], item["answer"], item["feedback"], *item["options"]])
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
