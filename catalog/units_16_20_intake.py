#!/usr/bin/env python3
"""Build proposed Units 16–20 from frozen admitted sources only.

Russian strings are copied from retained OpenRussian fields or from exact
Wikibooks substrings. This module does not decline, conjugate, or compose
new Russian. Units 18 and 20 stay unprepared.
"""

from __future__ import annotations

import hashlib
import json
from pathlib import Path

from units_3_5_intake import (
    CYRILLIC,
    DIRTY_LEARNER_TEXT,
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
from units_6_10_intake import GRAMMAR_KINDS, Lesson, _step
from units_11_15_intake import ADJ_FIELDS, NOUN_FIELDS, git_blob_sha1

ROOT = Path(__file__).resolve().parents[1]
FREEZE_PATH = ROOT / "catalog" / "sources" / "units-16-20" / "FREEZE.json"
SOURCE_ROOT = FREEZE_PATH.parent
SCAFFOLD = "INDEPENDENTLY_AUTHORED_SCAFFOLD"
FIELDS = {"nouns.csv": NOUN_FIELDS, "adjectives.csv": ADJ_FIELDS}
GLOSS = {
    "nouns.csv:кни'га": ("book, volume", "book"),
    "nouns.csv:стол": ("table, desk, board; diet, cooking, cuisine; department, section", "table"),
    "nouns.csv:дом": ("house, home, house-hold", "house"),
    "adjectives.csv:хоро'ший": ("good, beautiful, handsome", "good"),
}
REFERENCE = "ref-u1620-attribution"


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
        admitted = {item["id"] for item in self.freeze["decisions"] if item["disposition"] == "ADMITTED"}
        required = {
            "openrussian-50e210c4803237779cb562bc1abcea529066031c",
            "wikibooks-instrumental-4219602",
            "wikibooks-adjectives-4292379",
            "wikibooks-numbers-4371358",
            "wikibooks-verbal-aspect-4059032",
        }
        if admitted != required:
            raise SystemExit(f"Admitted source decisions changed: {sorted(admitted)}")

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
            "instrumental": "Russian/Grammar/Instrumental_case",
            "adjectives": "Russian/Grammar/Adjectives",
            "numbers": "Russian/Numbers",
            "aspect": "Russian/Verbal_Aspect",
            "copyrights": "Wikibooks:Copyrights",
        }

    def say(self, unit: int, text: str) -> str:
        from units_3_5_intake import CLAIM, GRAMMAR_TERM

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
        source = OPENRUSSIAN_ID if example_id.startswith("ex-or-") else WIKIBOOKS_ID
        self.examples.append({
            "id": example_id,
            "kind": "LEXICAL",
            "letterId": "",
            "display": display,
            "note": note,
            "sourceId": source,
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


def _gap(builder: Builder, number: int, slug: str, title: str, body: str, reason: str) -> tuple[dict, dict]:
    step_id = f"u{number}-st-gap-{slug}"
    step = _step(
        step_id, "UNAVAILABLE", builder.say(number, title), builder.say(number, body),
        reference_ids=[REFERENCE],
    )
    step["unavailableReason"] = reason
    objective = {
        "id": f"obj-u{number}-{slug}",
        "unitNumber": number,
        "title": title,
        "kind": "GUIDANCE",
        "instructionStepIds": [step_id],
        "exampleIds": [],
        "practiceStepIds": [],
        "checkItemIds": [],
        "remediationStepIds": [],
    }
    session = {
        "id": f"u{number}-s-gap-{slug}",
        "title": title,
        "estimatedMinutes": 4,
        "objectiveIds": [objective["id"]],
        "steps": [step],
    }
    return session, objective


def _chain(builder: Builder, number: int, specs: list[dict], orient_title: str, orient_body: str):
    sessions = [{
        "id": f"u{number}-s-orient",
        "title": orient_title,
        "estimatedMinutes": 8,
        "objectiveIds": [spec["id"] for spec in specs] + [f"obj-u{number}-book"],
        "steps": [_step(
            f"u{number}-st-orient", "ORIENT", builder.say(number, orient_title), builder.say(number, orient_body),
            book=builder.say(number, f"Open Lesson {number} in the copy you own. The app records do not replace that lesson."),
            reference_ids=[REFERENCE],
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
            steps.append(_step(teach, "TEACH", card["title"], card["body"], example_ids=card["examples"], reference_ids=[REFERENCE]))
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
            builder.say(number, f"Do Lesson {number} in the copy you own. Use that book's explanations and practice. The proposed records in this unit are a separate licensed set and do not copy the book. Listening stays with the book. No audio is mapped here."),
            book=builder.say(number, f"Open Lesson {number} in your copy and complete its lesson work."),
            reference_ids=[REFERENCE],
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


def _closed_unit(builder: Builder, number: int, title: str, body: str, gaps: list[tuple[str, str, str, str]]) -> dict:
    sessions = [{
        "id": f"u{number}-s-orient",
        "title": title,
        "estimatedMinutes": 6,
        "objectiveIds": [f"obj-u{number}-book"],
        "steps": [_step(
            f"u{number}-st-orient", "ORIENT", builder.say(number, title), builder.say(number, body),
            book=builder.say(number, f"Open Lesson {number} in the copy you own. This unit does not replace that lesson."),
            reference_ids=[REFERENCE],
        )],
    }]
    objectives = []
    for slug, gap_title, gap_body, reason in gaps:
        session, objective = _gap(builder, number, slug, gap_title, gap_body, reason)
        sessions.append(session)
        objectives.append(objective)
        sessions[0]["objectiveIds"].append(objective["id"])
    sessions.append({
        "id": f"u{number}-s-book",
        "title": f"Self-attest Lesson {number}",
        "estimatedMinutes": 8,
        "objectiveIds": [f"obj-u{number}-book"],
        "steps": [
            _step(
                f"u{number}-st-book-work", "BOOK", builder.say(number, f"Work Lesson {number} in your copy"),
                builder.say(number, f"Do Lesson {number} in the copy you own. This unit has no proposed lesson body for the missing topics. Listening stays with the book. No audio is mapped here."),
                book=builder.say(number, f"Open Lesson {number} in your copy and complete its lesson work."),
                reference_ids=[REFERENCE],
            ),
            _step(
                f"u{number}-st-book", "BOOK", builder.say(number, f"Record Lesson {number} book work"),
                builder.say(number, f"When you have done Lesson {number} in your copy, self-attest here. Attestation records your report. It does not grade the lesson, approve a mapping, or approve proposed records."),
                book=builder.say(number, f"Self-attest only after the Lesson {number} work in your copy."),
            ),
        ],
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
    return {"number": number, "sessions": sessions, "objectives": objectives, "minimum_checks": 0}


def build_units_16_20() -> dict:
    from units_16_20_lessons import populate

    frozen = FrozenSources()
    builder = Builder(frozen)
    prepared = [
        _unit(builder, 16, "Admitted instrumental rows", "Proposed Unit 16 uses frozen instrumental singular rows, instrumental plural rows, agreement rows, means and accompaniment lines, and selected governing lines. Identity, role, and becoming complements stay in the copy you own. Qualified review is pending.", 10, populate, [
            ("complements", "Complements stay in the copy you own", "Identity, role, and becoming complements stay in the copy you own. No admitted frozen span states that pattern.", "Identity, role, and becoming complements have no admitted frozen span."),
        ]),
        _unit(builder, 17, "Admitted number and quantity rows", "Proposed Unit 17 uses frozen number rows, ordinal rows, hour rows, year rows, and minute rows. Clock time, weekdays, dates, and age stay in the copy you own. Qualified review is pending.", 8, populate, [
            ("calendar", "Clock time and dates stay in the copy you own", "Clock time, weekdays, dates, and age stay in the copy you own. The frozen number rows do not establish those patterns.", "Telling time, weekdays, calendar dates, and age have no admitted frozen span."),
        ]),
        _unit(builder, 19, "Admitted prefix cautions", "Proposed Unit 19 uses frozen prefix cautions. Conditional patterns and obligation patterns stay in the copy you own. Qualified review is pending.", 2, populate, [
            ("conditional", "Conditional patterns stay in the copy you own", "Conditional patterns stay in the copy you own. Prefix cautions do not supply that pattern.", "Conditional patterns have no admitted frozen span."),
            ("obligation", "Obligation patterns stay in the copy you own", "Obligation patterns stay in the copy you own. Prefix cautions do not supply that pattern.", "Obligation patterns have no admitted frozen span."),
        ]),
    ]
    closed = [
        _closed_unit(
            builder, 18, "Comparison stays unprepared",
            "Proposed Unit 18 is not prepared. Comparison and relative-clause agreement stay in the copy you own. Qualified review is pending.",
            [
                ("comparison", "Comparison stays in the copy you own", "Comparison stays in the copy you own. The frozen adjective page is not a comparison source.", "Comparison forms have no admitted frozen instructional source."),
                ("relative", "Relative-clause agreement stays in the copy you own", "Relative-clause agreement stays in the copy you own. No frozen instructional source for that pattern is admitted here.", "Relative-clause agreement has no admitted frozen instructional source."),
            ],
        ),
        _closed_unit(
            builder, 20, "Motion pairs stay unprepared",
            "Proposed Unit 20 is not prepared. Motion pairs stay in the copy you own. Qualified review is pending.",
            [
                ("motion", "Motion pairs stay in the copy you own", "Motion pairs stay in the copy you own. A red link is not an instructional source. A general page is not enough. Dictionary rows are not a motion curriculum.", "Unidirectional and multidirectional motion pairs have no admitted frozen instructional source. A red link, a general page, and dictionary rows are not that source."),
            ],
        ),
    ]
    units = prepared + closed
    _assert(builder, units)
    return {
        "units": units,
        "items": builder.items,
        "examples": builder.examples,
        "provenance": builder.provenance,
        "authored": builder.authored,
        "references": [_attribution_reference()],
    }


def _unit(builder, number, title, body, minimum, populate, gaps):
    lesson = Lesson(builder, number)
    populate(lesson, number)
    independent = sum(len(spec["checks"]) for spec in lesson.specs)
    if independent < minimum:
        raise SystemExit(f"Unit {number} has {independent} independent checks, needs {minimum}")
    sessions, objectives = _chain(builder, number, lesson.specs, title, body)
    for slug, gap_title, gap_body, reason in gaps:
        session, objective = _gap(builder, number, slug, gap_title, gap_body, reason)
        sessions.append(session)
        objectives.append(objective)
    return {"number": number, "sessions": sessions, "objectives": objectives, "minimum_checks": minimum}


def _attribution_reference() -> dict:
    return {
        "id": REFERENCE,
        "title": "Units 16–20 source notice",
        "summary": "Proposed Units 16, 17, and 19 adapt CC BY-SA 4.0 dictionary rows and Wikibooks grammar excerpts. Units 18 and 20 do not.",
        "detail": (
            "OpenRussian dictionary data is frozen at commit 50e210c4803237779cb562bc1abcea529066031c "
            "under CC BY-SA 4.0. Wikibooks excerpts are frozen by oldid under the CC BY-SA 4.0 route. "
            "The adapted selection and arrangement are share-alike. Application code, user data, and user recordings are outside that statement. "
            "No audio is included. Learner-facing dictionary glosses are delimited sense selections. "
            "Comparison, relative-clause agreement, conditional patterns, obligation patterns, and motion pairs are not prepared. Review is pending."
        ),
        "introducedInUnit": 16,
        "sourceId": WIKIBOOKS_ID,
        "classification": "LICENSE_OR_ATTRIBUTION",
        "reviewStatus": "NOT_REQUIRED",
        "searchTerms": ["units 16-20 source notice"],
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
    for number in (18, 20):
        if any(record["unitNumber"] == number and CYRILLIC.search(record["shippedText"]) for record in builder.provenance):
            raise SystemExit(f"Unit {number} shipped Russian without an admitted instructional source")


if __name__ == "__main__":
    built = build_units_16_20()
    print(
        f"provenance={len(built['provenance'])} authored={len(built['authored'])} "
        f"items={len(built['items'])} examples={len(built['examples'])} units={len(built['units'])}"
    )
