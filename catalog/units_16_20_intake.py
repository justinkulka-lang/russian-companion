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
    format_wikibooks_emphasis,
    sha256_text,
    text_in_frozen,
    unbound_letters,
    unfold_wiki_labels,
)
from explicit_spans import DATE_SPAN
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
UNIT17_REFERENCE = "ref-u17-attribution"


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
        self.page_meta = {page["key"]: page for page in self.freeze["wikibooks"]["pages"]}
        admitted = {item["id"] for item in self.freeze["decisions"] if item["disposition"] == "ADMITTED"}
        required = {
            "openrussian-50e210c4803237779cb562bc1abcea529066031c",
            "wikibooks-instrumental-4219602",
            "wikibooks-adjectives-4292379",
            "wikibooks-numbers-4371358",
            "wikibooks-verbal-aspect-4059032",
            "wikipedia-russian-grammar-1369617627",
            "wikibooks-days-3594124",
            "wikibooks-months-4376719",
            "wikibooks-interrogative-4220316",
            "wikibooks-lesson3-4460699",
            "wikipedia-date-time-country-1366690592",
            "wikipedia-date-notation-russia-1357134172",
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
        from authored_identity import begin_authored_ids

        begin_authored_ids()
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
            "grammar": "Russian_grammar",
            "days": "Russian/Vocabulary/Days_of_the_Week",
            "months": "Russian/Vocabulary/Months",
            "interrogative": "Russian/Interrogative_Pronouns",
            "lesson3": "Russian/Lesson_3",
        }

    def say(self, unit: int, text: str, role: str = "scaffold", owner: str = "") -> str:
        from authored_identity import semantic_authored_id
        from units_3_5_intake import CLAIM, GRAMMAR_TERM

        if CYRILLIC.search(text) or CLAIM.search(text) or (GRAMMAR_TERM.search(text) and "?" not in text):
            raise SystemExit(f"independently authored English makes a linguistic claim: {text!r}")
        key = (unit, text)
        if key not in self._said:
            self._said[key] = semantic_authored_id(
                unit,
                text,
                role,
                owner or f"u{unit}-scaffold",
            )
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
        cleaned = format_wikibooks_emphasis(shipped)
        if format_wikibooks_emphasis(cleaned) != cleaned:
            raise SystemExit(f"{item_id} emphasis transform is not stable")
        if not text_in_frozen(original, cleaned):
            raise SystemExit(f"Shipped text {cleaned!r} is not in {page}")
        if cleaned in original:
            transformation = "selection"
        elif cleaned in format_wikibooks_emphasis(original):
            transformation = "wikibooks emphasis formatting"
        else:
            transformation = "wiki label selection"
        meta = self.frozen.page_meta[page]
        source_id = meta.get("sourceId", WIKIBOOKS_ID)
        source_url = meta.get("url") or (
            f"https://en.wikibooks.org/wiki/{self.titles[page]}?oldid={self.frozen.oldid(page)}"
        )
        attribution = meta.get("attribution", self.wikibooks_attr)
        self.add_provenance(
            item_id, unit, objective, record_type, source_id,
            source_url,
            self.frozen.oldid(page), page + ":" + needle[:80], original, cleaned, transformation, attribution,
        )
        return cleaned

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
        if not example_id.startswith("ex-or-"):
            for record in reversed(self.provenance):
                if record["shippedText"] == display and record["objectiveId"] == objective:
                    source = record["sourceId"]
                    break
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
        reference_ids=[notice_for(number)],
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
            reference_ids=[notice_for(number)],
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
            steps.append(_step(teach, "TEACH", card["title"], card["body"], example_ids=card["examples"], reference_ids=[notice_for(number)]))
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
            reference_ids=[notice_for(number)],
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
            reference_ids=[notice_for(number)],
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
                reference_ids=[notice_for(number)],
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


def notice_for(number: int) -> str:
    return UNIT17_REFERENCE if number == 17 else REFERENCE


def build_units_16_20(
    probe_says: list[tuple[str, str, str]] | None = None,
    probe_at: str = "end",
) -> dict:
    from units_16_20_lessons import populate

    frozen = FrozenSources()
    builder = Builder(frozen)
    if probe_at == "start":
        for text, role, owner in probe_says or []:
            builder.say(17, text, role=role, owner=owner)
    units = [
        _unit(builder, 16, "Admitted instrumental rows", "Proposed Unit 16 uses frozen instrumental rows and the admitted complement span. Qualified review is pending.", 10, populate, []),
        _unit(builder, 17, "Admitted number and quantity rows", "Proposed Unit 17 uses frozen number rows plus admitted age, weekday, month, clock-question, and calendar-date spans. Productive clock-time answers stay unresolved. Qualified review is pending.", 8, populate, [
            ("clock-productive", "Productive clock-time answers stay in the copy you own", "The three shown clock phrases are recognition only. Productive clock-time answers have no admitted frozen span.", "Productive clock-time answers have no admitted frozen span."),
        ]),
        _unit(builder, 18, "Admitted comparison and relative lines", "Proposed Unit 18 uses admitted comparison lines and admitted relative lines. Qualified review is pending.", 2, populate, []),
        _unit(builder, 19, "Admitted prefix cautions", "Proposed Unit 19 uses frozen prefix cautions plus admitted conditional and obligation spans. Probability stays unresolved. Qualified review is pending.", 2, populate, [
            ("probability", "Probability stays in the copy you own", "Probability stays in the copy you own. The admitted conditional and obligation spans do not state it.", "Probability use has no admitted frozen span."),
        ]),
        _unit(builder, 20, "Admitted motion lines", "Proposed Unit 20 uses admitted motion lines for direction, foot and transport, carrying and leading, setting out, and selected prefixed paths. Qualified review is pending.", 2, populate, []),
    ]
    if probe_at == "end":
        for text, role, owner in probe_says or []:
            builder.say(17, text, role=role, owner=owner)
    _assert(builder, units)
    capabilities = [item for unit in units for item in unit["capabilities"]]
    return {
        "units": units,
        "items": builder.items,
        "examples": builder.examples,
        "provenance": builder.provenance,
        "authored": builder.authored,
        "references": [_attribution_reference(), _unit17_reference()],
        "capabilities": capabilities,
        "sources": _admitted_sources(frozen),
        "rights": _admitted_rights(frozen),
    }


def _admitted_sources(frozen: FrozenSources) -> list[dict]:
    wanted = {
        "src-wikipedia-date-time-country-1366690592",
        "src-wikipedia-date-notation-russia-1357134172",
    }
    sources = []
    for page in frozen.freeze["wikibooks"]["pages"]:
        if page.get("sourceId") not in wanted:
            continue
        sources.append({
            "id": page["sourceId"],
            "title": page["title"],
            "license": LICENSE_ID,
            "attribution": page["attribution"],
            "modification": "Selected spans only. Russian quotations are unchanged. English claims are quoted, not paraphrased.",
        })
    if {item["id"] for item in sources} != wanted:
        raise SystemExit(f"Clock and date sources were not frozen: {sources}")
    return sources


def _admitted_rights(frozen: FrozenSources) -> list[dict]:
    return [
        {
            "id": "rights-" + source["id"].removeprefix("src-"),
            "sourceId": source["id"],
            "license": LICENSE_ID,
            "permitsAppUse": True,
        }
        for source in _admitted_sources(frozen)
    ]


RESOLVED_CLAIMS = {
    "instrumental-complements",
    "age",
    "weekdays",
    "months",
    "clock-question",
    "clock-expression-recognition",
    "calendar-date",
    "comparison",
    "relative-kotoryy",
    "conditional-by",
    "obligation-dolzhen",
    "motion-directionality",
    "motion-foot-transport",
    "motion-carrying-leading",
    "motion-po-setting-out",
    "motion-prefixed-path",
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
    resolved = []
    seen = set()
    for spec in lesson.capabilities:
        if spec["supportedClaim"] not in RESOLVED_CLAIMS:
            raise SystemExit(f"Refusing to satisfy an unsupported claim: {spec}")
        matches = [
            record for record in builder.provenance
            if record["unitNumber"] == number
            and record["objectiveId"] == spec["objectiveId"]
            and record["originalSourceText"] == spec["exactSpan"]
        ]
        if not matches:
            raise SystemExit(f"Capability span was not retained for {spec['objectiveId']} {spec['supportedClaim']}")
        host = matches[0]
        key = (spec["objectiveId"], spec["supportedClaim"])
        if key in seen:
            raise SystemExit(f"Duplicate capability {key}")
        seen.add(key)
        resolved.append({
            "id": f"cap-u{number}-{spec['supportedClaim']}",
            "sourceId": host["sourceId"],
            "sourceRevision": host["sourceRevision"],
            "sourceRecordId": host["sourceRecordId"],
            "sourceHash": host["sourceHash"],
            "exactSpan": host["originalSourceText"],
            "unitNumber": number,
            "objectiveId": spec["objectiveId"],
            "supportedClaim": spec["supportedClaim"],
        })
    return {
        "number": number,
        "sessions": sessions,
        "objectives": objectives,
        "minimum_checks": minimum,
        "capabilities": resolved,
    }


def _attribution_reference() -> dict:
    return {
        "id": REFERENCE,
        "title": "Units 16–20 source notice",
        "summary": "Proposed Units 16–20 adapt CC BY-SA 4.0 dictionary rows, Wikibooks excerpts, and selected Wikipedia grammar spans.",
        "detail": (
            "OpenRussian dictionary data is frozen at commit 50e210c4803237779cb562bc1abcea529066031c "
            "under CC BY-SA 4.0. Wikibooks excerpts are frozen by oldid under the CC BY-SA 4.0 route recorded on Wikibooks:Copyrights oldid 4622060. "
            "Wikipedia grammar spans are frozen at oldid 1369617627 under the separate CC BY-SA 4.0 route recorded on Wikipedia:Copyrights oldid 1369466282. "
            "Those license routes are not interchangeable. "
            "The adapted selection and arrangement are share-alike. Application code, user data, and user recordings are outside that statement. "
            "No audio is included. Learner-facing dictionary glosses are delimited sense selections."
        ),
        "introducedInUnit": 16,
        "sourceId": WIKIBOOKS_ID,
        "classification": "LICENSE_OR_ATTRIBUTION",
        "reviewStatus": "NOT_REQUIRED",
        "searchTerms": ["units 16-20 source notice"],
    }


def _unit17_reference() -> dict:
    return {
        "id": UNIT17_REFERENCE,
        "title": "Unit 17 source notice",
        "summary": "Proposed Unit 17 adapts CC BY-SA 4.0 dictionary rows, Wikibooks excerpts, and the admitted Wikipedia clock and date spans.",
        "detail": (
            "OpenRussian dictionary data is frozen at commit 50e210c4803237779cb562bc1abcea529066031c "
            "under CC BY-SA 4.0. Wikibooks excerpts are frozen by oldid under the CC BY-SA 4.0 route recorded on Wikibooks:Copyrights oldid 4622060. "
            "The bounded clock phrases are frozen at Wikipedia oldid 1366690592. "
            "The calendar-date examples are frozen at Wikipedia oldid 1357134172. "
            "Those license routes are not interchangeable. "
            "The admitted calendar-date relationships remain admitted. "
            "The three clock phrases are bounded recognition. "
            "Productive clock-time generation remains unresolved. "
            "Review is pending."
        ),
        "introducedInUnit": 17,
        "sourceId": WIKIBOOKS_ID,
        "classification": "LICENSE_OR_ATTRIBUTION",
        "reviewStatus": "NOT_REQUIRED",
        "searchTerms": ["unit 17 source notice"],
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
            for banned in ("dolphin", "pronounced", "Remember that", "Wade", "Blackwell"):
                if banned.casefold() in host.casefold():
                    if banned == "pronounced" and host == unfold_wiki_labels(DATE_SPAN):
                        continue
                    raise SystemExit(f"Unit {number} ships excluded commentary: {banned}")


if __name__ == "__main__":
    built = build_units_16_20()
    print(
        f"provenance={len(built['provenance'])} authored={len(built['authored'])} "
        f"items={len(built['items'])} examples={len(built['examples'])} units={len(built['units'])}"
    )
