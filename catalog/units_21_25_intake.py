#!/usr/bin/env python3
"""Build proposed Units 21–25 from frozen admitted sources only.

Russian strings are exact retained substrings. This module does not decline,
conjugate, or compose new Russian. Unit 24 stays unprepared without negation-scope.
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
    PENDING,
    REGION,
    SHARE_ALIKE,
    WIKIBOOKS_ID,
    format_wikibooks_emphasis,
    sha256_text,
    unbound_letters,
)
from units_6_10_intake import GRAMMAR_KINDS, Lesson, _step

ROOT = Path(__file__).resolve().parents[1]
FREEZE_PATH = ROOT / "catalog" / "sources" / "units-21-25" / "FREEZE.json"
SOURCE_ROOT = FREEZE_PATH.parent
SCAFFOLD = "INDEPENDENTLY_AUTHORED_SCAFFOLD"
REFERENCE = "ref-u2125-attribution"
EXCLUDED_LEARNER_TEXT = (
    "don't deserve the title Comrade",
    "wealthy foreigners",
    "Ordinary citizens of foreign countries",
    "only a dozen",
    "Creativity in baby-naming",
    ".ogg",
    "[[Media:",
    "listen",
)


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
        if "Creative Commons Attribution-ShareAlike 4.0" not in self.pages["copyrights"]:
            raise SystemExit("Frozen Wikibooks copyrights page does not state CC BY-SA 4.0")
        if self.freeze["license"]["licenseId"] != LICENSE_ID:
            raise SystemExit("Retained license identifier is not CC BY-SA 4.0")
        admitted = {item["id"] for item in self.freeze["decisions"] if item["disposition"] == "ADMITTED"}
        required = {
            "wikibooks-possessive-pronouns-4220315",
            "wikibooks-numbers-4371358",
            "wikibooks-verbs-4233876",
            "wikibooks-cases-4195114",
            "wikibooks-names-4660786",
            "wikibooks-useful-words-3353859",
            "pressbooks-usa-grammar-87815b8724cb11aecba5bb6d8b6438f783408f2354e2560a3c0fe026fac1e171",
        }
        if admitted != required:
            raise SystemExit(f"Admitted source decisions changed: {sorted(admitted)}")
        self.page_meta = {}
        from source_transforms import load_supplemental_pages

        load_supplemental_pages(self, SOURCE_ROOT)

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
        self.wikibooks_attr = (
            "Wikibooks contributors, CC BY-SA 4.0. "
            "Page history for each frozen oldid identifies the contributors."
        )
        self.titles = {
            "possessive": "Russian/Possessive_Pronouns",
            "numbers": "Russian/Numbers",
            "verbs": "Russian/Grammar/Verbs",
            "cases": "Russian/Grammar/Cases",
            "names": "Russian/Names",
            "useful": "Russian/Useful_Words_and_Expressions",
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
        from source_transforms import special_transformation
        from units_3_5_intake import text_in_frozen

        original = self.frozen.require(page, needle)
        meta = self.frozen.page_meta.get(page)
        if meta is None:
            cleaned = format_wikibooks_emphasis(shipped)
            if format_wikibooks_emphasis(cleaned) != cleaned:
                raise SystemExit(f"{item_id} emphasis transform is not stable")
            formatted = format_wikibooks_emphasis(original)
            if cleaned not in original and cleaned not in formatted:
                raise SystemExit(f"Shipped text {cleaned!r} is not in {page}")
            transformation = "selection" if cleaned in original else "wikibooks emphasis formatting"
            self.add_provenance(
                item_id, unit, objective, record_type, WIKIBOOKS_ID,
                f"https://en.wikibooks.org/wiki/{self.titles[page]}?oldid={self.frozen.oldid(page)}",
                self.frozen.oldid(page), page + ":" + needle[:80], original, cleaned, transformation, self.wikibooks_attr,
            )
            return cleaned
        transformation = special_transformation(meta, original, shipped)
        if transformation is None:
            if not text_in_frozen(original, shipped):
                raise SystemExit(f"Shipped text {shipped!r} is not in {page}")
            transformation = "selection"
        self.add_provenance(
            item_id, unit, objective, record_type, meta["sourceId"],
            meta["url"], str(meta["oldid"]), page + ":" + needle[:80],
            original, shipped, transformation, meta["attribution"],
        )
        return shipped

    def example(self, example_id, display, note, objective) -> None:
        source = WIKIBOOKS_ID
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
                builder.say(number, f"Do Lesson {number} in the copy you own. The bounded lines in this unit do not establish elapsed time and duration. Listening stays with the book. No audio is mapped here."),
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


def build_units_21_25() -> dict:
    from units_21_25_lessons import populate

    frozen = FrozenSources()
    builder = Builder(frozen)
    prepared = [
        _unit(builder, 21, "Admitted object and purpose lines", "Proposed Unit 21 uses frozen object-possession lines, the frozen reflexive contrast, and the frozen purpose gloss. Qualified review is pending.", 2, populate, []),
        _unit(builder, 22, "Admitted numeral forms", "Proposed Unit 22 uses frozen item-form lines, the frozen numeral table, the frozen half-form usage note, and the frozen collective column. A numbered list is not taught as an ordinal distinction. Qualified review is pending.", 4, populate, []),
        _unit(builder, 23, "Admitted time expressions", "Proposed Unit 23 uses frozen time-expression spans for decade ranges, beginning and middle expressions, during an event, duration throughout a period, and before, after, in, and ago. Each contrast stays on its own card. Completion over a period stays in the copy you own. Qualified review is pending.", 10, populate, [
            ("completion", "Completion over a period stays in the copy you own", "Completion over a period stays in the copy you own. A duration span does not state that distinction.", "Completion over a period has no admitted frozen span."),
        ]),
        _unit(builder, 24, "Admitted negation lines", "Proposed Unit 24 uses the frozen genitive and accusative compare, the frozen ni-form and ne-form formation, the frozen concord rule, and a bounded word-order contrast. That contrast states two meanings. Scope-sensitive placement stays in the copy you own. Qualified review is pending.", 6, populate, [
            ("placement", "Scope-sensitive placement stays in the copy you own", "Scope-sensitive placement stays in the copy you own. A word-order pair does not state that distinction.", "Scope-sensitive placement has no admitted frozen span."),
        ]),
        _unit(builder, 25, "Admitted name and courtesy lines", "Proposed Unit 25 uses frozen personal-name lines, listed diminutive pairs, courtesy lines, and the frozen address-register sentences. A general nickname-formation rule stays in the copy you own. Qualified review is pending.", 8, populate, [
            ("nickname-rule", "A general nickname-formation rule stays in the copy you own", "A general nickname-formation rule stays in the copy you own. A listed pair is not that rule.", "A general nickname-formation rule has no admitted frozen span."),
        ]),
    ]
    units = prepared
    _assert(builder, units)
    from batch_a_remaining_intake import rights_record, source_record

    pages = [
        page for page in frozen.freeze["supplemental"]
        if page["sourceId"] != "src-wikipedia-russian-grammar-1369617627"
    ]
    return {
        "units": units,
        "items": builder.items,
        "examples": builder.examples,
        "provenance": builder.provenance,
        "authored": builder.authored,
        "references": [_attribution_reference()],
        "sources": [source_record(page) for page in pages],
        "rights": [rights_record(page["sourceId"]) for page in pages],
        "capabilities": [item for unit in units for item in unit.get("capabilities", [])],
    }


def _unit(builder, number, title, body, minimum, populate, gaps):
    from batch_b_units_21_25 import add_batch_b

    lesson = Lesson(builder, number)
    if number == 23:
        from batch_c_unit_23 import add_unit_23

        add_unit_23(lesson)
    else:
        populate(lesson, number)
        add_batch_b(lesson)
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
    for spec in getattr(lesson, "capabilities", []):
        matches = [
            record for record in builder.provenance
            if record["unitNumber"] == number
            and record["objectiveId"] == spec["objectiveId"]
            and record["originalSourceText"] == spec["exactSpan"]
        ]
        if not matches:
            raise SystemExit(f"Capability span was not retained for {spec['objectiveId']}")
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
        "title": "Units 21–25 source notice",
        "summary": "Proposed Units 21–25 adapt admitted CC BY-SA 4.0 spans. Pressbooks evidence is separate from Wikimedia evidence.",
        "detail": (
            "Wikibooks excerpts are frozen by oldid under the CC BY-SA 4.0 route recorded on Wikibooks:Copyrights oldid 4622060. "
            "The adapted selection and arrangement are share-alike. Application code, user data, and user recordings are outside that statement. "
            "No audio is included. Linked audio on the useful-words page is not admitted. "
            "Dated name-page claims about social address, foreigners, naming creativity, and the number of names are not shipped. "
            "English Wikipedia Russian grammar oldid 1369617627 is used under CC BY-SA 4.0 via Wikipedia:Copyrights oldid 1369466282. "
            "Russian Wikipedia pages are used under CC BY-SA 4.0 via the Russian Wikipedia copyrights page oldid 154562152. "
            "The one-and-a-half entry is frozen at English Wiktionary oldid 89737719 under CC BY-SA 4.0 via Wiktionary:Copyrights oldid 87545419. Linked audio is not admitted. "
            "Unit 23 time expressions are frozen Pressbooks chapter bytes sha256 87815b8724cb11aecba5bb6d8b6438f783408f2354e2560a3c0fe026fac1e171, retrieved 2026-09-26T22:52:41Z, under that chapter's own CC BY-SA 4.0 footer. That Pressbooks route is not a Wikipedia, Wiktionary, or Wikibooks license. "
            "Completion over a period and a general nickname-formation rule are not prepared. "
            "The Unit 24 word-order pair is bounded sentence recognition on English Wikipedia Russian grammar oldid 1369617627. It does not clear scope-sensitive placement. Review is pending."
        ),
        "introducedInUnit": 21,
        "sourceId": WIKIBOOKS_ID,
        "classification": "LICENSE_OR_ATTRIBUTION",
        "reviewStatus": "NOT_REQUIRED",
        "searchTerms": ["units 21-25 source notice"],
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
                reason = step.get("unavailableReason") or ""
                if reason:
                    for match in REGION.finditer(reason):
                        text = match.group(0).strip()
                        raise SystemExit(f"Unit {number} gap reason contains Russian: {text}")
                    for excluded in EXCLUDED_LEARNER_TEXT:
                        if excluded in reason:
                            raise SystemExit(f"Unit {number} gap reason ships excluded text: {excluded}")
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
            for excluded in EXCLUDED_LEARNER_TEXT:
                if excluded in host:
                    raise SystemExit(f"Unit {number} ships excluded text: {excluded}")
    from batch_b_units_21_25 import BOUNDED_TIME_SPANS
    from batch_c_unit_23 import BEGIN_SPAN, DURATION_SPAN, DURING_EVENT_SPAN, ELAPSED_SPAN, RANGE_SPAN

    admitted_time = {RANGE_SPAN, BEGIN_SPAN, DURING_EVENT_SPAN, DURATION_SPAN, ELAPSED_SPAN}
    for record in builder.provenance:
        if record["unitNumber"] != 23:
            continue
        if record["originalSourceText"] in BOUNDED_TIME_SPANS:
            raise SystemExit("Unit 23 provenance uses a bounded Wikipedia time span")
        if record["originalSourceText"] not in admitted_time:
            raise SystemExit("Unit 23 provenance is not an admitted Pressbooks time span")


if __name__ == "__main__":
    built = build_units_21_25()
    print(
        f"provenance={len(built['provenance'])} authored={len(built['authored'])} "
        f"items={len(built['items'])} examples={len(built['examples'])} units={len(built['units'])}"
    )
