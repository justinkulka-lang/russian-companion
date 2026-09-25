#!/usr/bin/env python3
"""Build proposed Units 26–30 from frozen admitted spans only.

Russian strings are exact frozen substrings. This module does not decline,
conjugate, or compose new Russian. Broad unsupported claims stay book-directed.
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
    format_wikibooks_emphasis,
    sha256_text,
    unbound_letters,
)
from units_6_10_intake import GRAMMAR_KINDS, Lesson, _step

ROOT = Path(__file__).resolve().parents[1]
FREEZE_PATH = ROOT / "catalog" / "sources" / "units-26-30" / "FREEZE.json"
SOURCE_ROOT = FREEZE_PATH.parent
SCAFFOLD = "INDEPENDENTLY_AUTHORED_SCAFFOLD"
REFERENCE = "ref-u2630-attribution"
EXCLUDED_LEARNER_TEXT = (
    "not vital",
    "predictible",
    "I've been told",
    "I recommend",
    ".ogg",
    "Phonos",
    "[[File:",
    "The New Penguin",
)
OPTIONAL_OBJECTIVES = {"obj-u27-present-passive"}


class FrozenSources:
    def __init__(self) -> None:
        self.freeze = json.loads(FREEZE_PATH.read_text(encoding="utf-8"))
        self.pages: dict[str, str] = {}
        self.by_key = {page["key"]: page for page in self.freeze["pages"]}
        if self.freeze["license"]["licenseId"] != LICENSE_ID:
            raise SystemExit("Retained license identifier is not CC BY-SA 4.0")
        route_errors = license_admission_errors(self.freeze, SOURCE_ROOT)
        if route_errors:
            raise SystemExit("; ".join(route_errors))
        admitted = {item["id"] for item in self.freeze["decisions"] if item["disposition"] == "ADMITTED"}
        required = {
            "wikipedia-russian-grammar-1369617627",
            "wikipedia-russian-alphabet-1371795481",
            "wikibooks-russian-various-stuff-3353858",
        }
        if admitted != required:
            raise SystemExit(f"Admitted source decisions changed: {sorted(admitted)}")
        for page in self.freeze["pages"]:
            path = SOURCE_ROOT / page["file"]
            data = path.read_bytes()
            first = hashlib.sha256(data).hexdigest()
            second = hashlib.sha256(data).hexdigest()
            if first != second or first != page["sha256"]:
                raise SystemExit(f"Frozen source hash mismatch for {page['file']}")
            revision = json.loads((SOURCE_ROOT / page["revisionFile"]).read_text(encoding="utf-8"))
            if str(revision["revid"]) != str(page["oldid"]):
                raise SystemExit(f"Revision id does not match oldid for {page['file']}")
            if hashlib.sha1(data).hexdigest() != revision["sha1"] or len(data) != revision["size"]:
                raise SystemExit(f"Frozen bytes do not match the retained revision for {page['file']}")
            if str(revision.get("parentid")) != str(page["parentid"]) or revision.get("timestamp") != page["timestamp"]:
                raise SystemExit(f"Revision identity is incomplete for {page['file']}")
            if "oldid=" not in page["url"] or page["url"].rstrip("/").endswith(".org"):
                raise SystemExit(f"Source URL is not an immutable oldid for {page['file']}")
            self.pages[page["key"]] = data.decode("utf-8")

    def require(self, page: str, needle: str) -> str:
        if needle not in self.pages[page]:
            raise SystemExit(f"Required excerpt missing from {page}")
        return needle


class Builder:
    def __init__(self, frozen: FrozenSources) -> None:
        self.frozen = frozen
        self.provenance: list[dict] = []
        self.authored: list[dict] = []
        self.examples: list[dict] = []
        self.items: list[dict] = []
        self._said: dict[tuple[int, str], str] = {}
        self._said_count = 0

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

    def wiki(self, item_id, unit, objective, page, needle, shipped, record_type="example") -> str:
        original = self.frozen.require(page, needle)
        cleaned = format_wikibooks_emphasis(shipped)
        if cleaned not in original and cleaned not in format_wikibooks_emphasis(original):
            raise SystemExit(f"Shipped text {cleaned!r} is not in {page}")
        if any(marker in cleaned for marker in ("'''", "''", "{{", "}}", "[[", "]]", ".ogg", "<ref")):
            raise SystemExit(f"{item_id} ships markup: {cleaned!r}")
        meta = self.frozen.by_key[page]
        transformation = "selection" if cleaned in original else "wikimedia emphasis formatting"
        self.provenance.append({
            "itemId": item_id,
            "unitNumber": unit,
            "objectiveId": objective,
            "recordType": record_type,
            "sourceId": meta["sourceId"],
            "sourceUrl": meta["url"],
            "sourceRevision": str(meta["oldid"]),
            "sourceRecordId": page + ":" + needle[:80],
            "originalSourceText": original,
            "shippedText": cleaned,
            "transformation": transformation,
            "licenseId": LICENSE_ID,
            "licenseUrl": LICENSE_URL,
            "attribution": meta["attribution"],
            "shareAlikeTreatment": SHARE_ALIKE,
            "sourceHash": sha256_text(original),
            "contentHash": sha256_text(cleaned),
            "reviewStatus": PENDING,
            "reviewerIdentity": "",
            "reviewerQualification": "",
            "reviewDate": "",
            "reviewedDigest": "",
            "audioAssetId": None,
        })
        return cleaned

    def example(self, example_id, display, note, objective) -> None:
        page = "grammar"
        for record in reversed(self.provenance):
            if record["objectiveId"] == objective and record["shippedText"] == display:
                page = record["sourceId"]
                break
        self.examples.append({
            "id": example_id,
            "kind": "LEXICAL",
            "letterId": "",
            "display": display,
            "note": note,
            "sourceId": page,
            "reviewStatus": PENDING,
            "objectiveId": objective,
        })

    def item(self, item_id, prompt, prompt_letter, options, answer, feedback, objective, source_id, kind, rationales, assessment=None) -> None:
        if answer not in options or len(options) != len(set(options)) or options.count(answer) != 1:
            raise SystemExit(f"Unusable answer key for {item_id}")
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
        admitted = {
            record["shippedText"]
            for record in builder.provenance
            if record["unitNumber"] == number
        } | {
            record["originalSourceText"]
            for record in builder.provenance
            if record["unitNumber"] == number
        }
        for index, card in enumerate(spec["cards"], start=1):
            teach = f"u{number}-st-teach-{slug}-{index}"
            teach_ids.append(teach)
            body = card["body"]
            if CYRILLIC.search(body) and body not in admitted:
                body = builder.say(number, "Read the admitted examples on this card.")
            steps.append(_step(teach, "TEACH", card["title"], body, example_ids=card["examples"], reference_ids=[REFERENCE]))
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
        objective = {
            "id": spec["id"],
            "unitNumber": number,
            "title": spec["title"],
            "kind": "RECOGNITION",
            "instructionStepIds": [f"u{number}-st-orient", *teach_ids],
            "exampleIds": spec["examples"],
            "practiceStepIds": guided_ids or [teach_ids[0]],
            "checkItemIds": spec["checks"],
            "remediationStepIds": [remediation],
        }
        if spec["id"] in OPTIONAL_OBJECTIVES:
            objective["gating"] = False
        objectives.append(objective)
    sessions.append({
        "id": f"u{number}-s-book",
        "title": f"Self-attest Lesson {number}",
        "estimatedMinutes": 10,
        "objectiveIds": [f"obj-u{number}-book"],
        "steps": [
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
    return sessions, objectives


def _unit(builder, number, title, body, populate, gaps, capabilities):
    lesson = Lesson(builder, number)
    populate(lesson, number)
    sessions, objectives = _chain(builder, number, lesson.specs, title, body)
    for slug, gap_title, gap_body, reason in gaps:
        session, objective = _gap(builder, number, slug, gap_title, gap_body, reason)
        sessions.append(session)
        objectives.append(objective)
    independent = 0
    for objective in objectives:
        if objective["kind"] == "RECOGNITION" and objective["id"] not in OPTIONAL_OBJECTIVES:
            independent += len(objective["checkItemIds"])
    resolved = []
    for spec in lesson.capabilities:
        matches = [
            record for record in builder.provenance
            if record["objectiveId"] == spec["objectiveId"] and record["originalSourceText"] == spec["exactSpan"]
        ]
        if not matches:
            raise SystemExit(f"Capability span was not retained for {spec['objectiveId']}")
        host = matches[0]
        if spec["supportedClaim"] not in {
            "word-order",
            "passive-participle",
            "imperfective-verbal-adverb",
            "perfective-verbal-adverb",
            "active-participle",
            "short-form-adjective",
            "letter-name-review",
        }:
            raise SystemExit(f"Refusing to satisfy a broad or unsupported claim: {spec}")
        resolved.append({
            "id": "cap-" + spec["objectiveId"],
            "sourceId": host["sourceId"],
            "sourceRevision": host["sourceRevision"],
            "sourceRecordId": host["sourceRecordId"],
            "sourceHash": host["sourceHash"],
            "exactSpan": host["originalSourceText"],
            "unitNumber": number,
            "objectiveId": spec["objectiveId"],
            "supportedClaim": spec["supportedClaim"],
        })
    expected = {item[0] for item in capabilities}
    if {item["objectiveId"] for item in resolved} != expected:
        raise SystemExit(f"Unit {number} capabilities { {item['objectiveId'] for item in resolved} } != {expected}")
    return {
        "number": number,
        "sessions": sessions,
        "objectives": objectives,
        "minimum_checks": max(independent, 1),
        "capabilities": resolved,
    }


def license_admission_errors(
    freeze: dict,
    source_root: Path,
    file_overrides: dict[str, bytes] | None = None,
) -> list[str]:
    """Admit each frozen page only on its own license-evidence route.

    A Wikipedia oldid is not admitted by the Wikibooks copyrights snapshot.
    """
    errors: list[str] = []
    license_block = freeze.get("license") or {}
    routes = license_block.get("routes") or {}
    overrides = file_overrides or {}
    loaded: dict[str, bytes] = {}
    for name, route in routes.items():
        if name in overrides:
            loaded[name] = overrides[name]
        else:
            path = (source_root / route["file"]).resolve()
            if not path.is_file():
                errors.append(f"{name} license evidence is missing")
                continue
            loaded[name] = path.read_bytes()
        first = hashlib.sha256(loaded[name]).hexdigest()
        second = hashlib.sha256(loaded[name]).hexdigest()
        if first != second or first != route.get("sha256"):
            errors.append(f"{name.capitalize()} license evidence hash does not match")
        statement = route.get("requiredStatement") or ""
        if not statement or statement not in loaded[name].decode("utf-8"):
            errors.append(f"{name.capitalize()} license evidence does not state CC BY-SA 4.0")
        if name == "wikipedia":
            revision_path = source_root / route["revisionFile"]
            if not revision_path.is_file():
                errors.append("Wikipedia license revision identity is missing")
            else:
                revision = json.loads(revision_path.read_text(encoding="utf-8"))
                if str(revision.get("revid")) != str(route.get("oldid")):
                    errors.append("Wikipedia license revision does not match the frozen oldid")
                if hashlib.sha1(loaded[name]).hexdigest() != revision.get("sha1") or len(loaded[name]) != revision.get("size"):
                    errors.append("Wikipedia license bytes do not match the retained revision")
    for page in freeze.get("pages") or []:
        source_id = page.get("sourceId") or ""
        route_name = page.get("licenseRoute")
        route = routes.get(route_name) if route_name else None
        if source_id.startswith("src-wikipedia-") and route_name != "wikipedia":
            errors.append(f"Wikipedia source {source_id} has only Wikibooks license evidence")
            continue
        if source_id.startswith("src-wikibooks-") and route_name != "wikibooks":
            errors.append(f"Wikibooks source {source_id} has only Wikipedia license evidence")
            continue
        if source_id.startswith("src-ruwiki-") and route_name != "ruwiki":
            errors.append(f"Russian Wikipedia source {source_id} has the wrong project license evidence")
            continue
        if source_id.startswith("src-enwiktionary-") and route_name != "wiktionary":
            errors.append(f"Wiktionary source {source_id} has the wrong project license evidence")
            continue
        if source_id.startswith("src-pressbooks-") and route_name != "pressbooks":
            errors.append(f"Pressbooks source {source_id} has the wrong project license evidence")
            continue
        if route is None or source_id not in route.get("appliesToSourceIds", []):
            errors.append(f"Source {source_id} license route does not match its evidence")
    return errors


def _attribution_reference() -> dict:
    return {
        "id": REFERENCE,
        "title": "Units 26–30 source notice",
        "summary": "Proposed Units 26–30 adapt admitted CC BY-SA 4.0 spans. Unsupported topics stay in the copy you own.",
        "detail": (
            "Wikipedia article text is frozen by oldid under CC BY-SA 4.0 via Wikipedia:Copyrights oldid 1369466282. "
            "The Wikibooks address note is frozen by oldid under the separate CC BY-SA 4.0 route recorded on Wikibooks:Copyrights oldid 4622060. "
            "The packaged CC BY-SA 4.0 legal code is cc-by-sa-4.0.txt. "
            "The adapted selection and arrangement are share-alike. "
            "Application code, user data, user recordings, and separately licensed audio are outside that statement. "
            "No audio is included. Linked media, Phonos, and pronunciation files are not admitted. "
            "Anecdotal postal advice is not shipped. "
            "The indefinite series, complete postal practice, general letter conventions, participle stress, participial punctuation, "
            "dictionary use, a general particle system, and the final reading are not prepared. Review is pending."
        ),
        "introducedInUnit": 26,
        "sourceId": "src-wikipedia-russian-grammar-1369617627",
        "classification": "LICENSE_OR_ATTRIBUTION",
        "reviewStatus": "NOT_REQUIRED",
        "searchTerms": ["units 26-30 source notice", "cc by-sa"],
    }


def _sources(frozen: FrozenSources) -> tuple[list[dict], list[dict]]:
    sources = []
    rights = []
    for page in frozen.freeze["pages"]:
        sources.append({
            "id": page["sourceId"],
            "title": page["title"],
            "license": LICENSE_ID,
            "attribution": page["attribution"],
            "modification": "Selected spans only. Russian quotations are unchanged. English claims are quoted, not paraphrased.",
        })
        rights.append({
            "id": "rights-" + page["sourceId"].removeprefix("src-"),
            "sourceId": page["sourceId"],
            "license": LICENSE_ID,
            "permitsAppUse": True,
        })
    return sources, rights


def build_units_26_30() -> dict:
    from units_26_30_lessons import populate

    frozen = FrozenSources()
    builder = Builder(frozen)
    units = [
        _unit(
            builder, 26,
            "Admitted word-order and address lines",
            "Proposed Unit 26 uses admitted arrangement lines, the written capital form, and the frozen address marks. The indefinite series, complete postal practice, and other letter conventions stay in the copy you own. Qualified review is pending.",
            populate,
            [
                ("indefinite", "The indefinite series stays in the copy you own", "The indefinite series stays in the copy you own. No admitted frozen span states that selection.", "The indefinite series has no admitted frozen span."),
                ("postal-address", "Complete postal practice stays in the copy you own", "Complete postal practice stays in the copy you own. The frozen address marks are not that practice.", "Complete modern postal practice has no admitted frozen span."),
                ("letter-writing", "Other letter conventions stay in the copy you own", "Other letter conventions stay in the copy you own. The written capital form is not a whole letter.", "General letter structure, openings, closings, and register have no admitted frozen span."),
                ("correspondence", "Correspondence work stays in the copy you own", "Correspondence work stays in the copy you own. The admitted lines do not supply it.", "Correspondence work has no admitted frozen span."),
            ],
            [("obj-u26-word-order", "word-order")],
        ),
        _unit(
            builder, 27,
            "Admitted past passive lines",
            "Proposed Unit 27 uses admitted past passive lines. Present passive lines are optional and do not gate this unit. Stress stays in the copy you own. Active forms are not taught here. Qualified review is pending.",
            populate,
            [
                ("stress", "Participle stress stays in the copy you own", "Participle stress stays in the copy you own. A marked example is not a stress rule.", "A general participle-stress rule has no admitted frozen span."),
                ("comprehension", "Comprehension passages stay in the copy you own", "Comprehension passages stay in the copy you own. The admitted lines do not supply them.", "Comprehension passages have no admitted frozen span."),
            ],
            [("obj-u27-passive", "passive-participle")],
        ),
        _unit(
            builder, 28,
            "Admitted adverbial lines",
            "Proposed Unit 28 uses admitted imperfective and perfective adverbial lines. Readings stay in the copy you own. Qualified review is pending.",
            populate,
            [
                ("readings", "Readings stay in the copy you own", "Readings stay in the copy you own. The admitted lines do not supply a reading passage.", "Readings have no admitted frozen span."),
            ],
            [
                ("obj-u28-imperfective-adverb", "imperfective-verbal-adverb"),
                ("obj-u28-perfective-adverb", "perfective-verbal-adverb"),
            ],
        ),
        _unit(
            builder, 29,
            "Admitted active and short-form lines",
            "Proposed Unit 29 uses admitted active lines, the short-form contrast, and the bookish-speech note for adverbial forms. Punctuation stays in the copy you own. This note is not a claim about every bookish text. Qualified review is pending.",
            populate,
            [
                ("punctuation", "Participial punctuation stays in the copy you own", "Participial punctuation stays in the copy you own. No admitted span states that rule.", "Participial punctuation has no admitted frozen span."),
                ("register", "A general bookish-register claim stays in the copy you own", "A general bookish-register claim stays in the copy you own. The adverbial note is only about adverbial forms.", "A claim about all bookish text has no admitted frozen span."),
                ("reading", "The bookish reading stays in the copy you own", "The bookish reading stays in the copy you own. The admitted note is not that reading.", "The bookish reading has no admitted frozen span."),
            ],
            [
                ("obj-u29-active", "active-participle"),
                ("obj-u29-short-adjective", "short-form-adjective"),
            ],
        ),
        _unit(
            builder, 30,
            "Review admitted table names and abbreviation examples",
            "Unit 1 introduced the printed letter names. This unit reviews admitted table names and applies them only as review. It also shows two admitted abbreviation examples and the admitted address marks. Those examples are not a complete abbreviation system. Dictionary use, a general particle system, and the final reading stay in the copy you own. Qualified review is pending.",
            populate,
            [
                ("dictionary", "Dictionary use stays in the copy you own", "Dictionary use stays in the copy you own. No admitted span gives that procedure.", "Dictionary use has no admitted frozen span."),
                ("abbreviation", "A general abbreviation system stays in the copy you own", "A general abbreviation system stays in the copy you own. Two examples and the address marks are not that system.", "A complete abbreviation system has no admitted frozen span."),
                ("particle", "A general particle system stays in the copy you own", "A general particle system stays in the copy you own. An isolated label is not that system.", "A general particle system has no admitted frozen span."),
                ("capstone", "The final reading stays in the copy you own", "The final reading stays in the copy you own. No admitted text supplies it.", "The final reading has no admitted frozen span."),
            ],
            [("obj-u30-letter-names", "letter-name-review")],
        ),
    ]
    _assert(builder, units)
    sources, rights = _sources(frozen)
    capabilities = [item for unit in units for item in unit["capabilities"]]
    return {
        "units": units,
        "items": builder.items,
        "examples": builder.examples,
        "provenance": builder.provenance,
        "authored": builder.authored,
        "references": [_attribution_reference()],
        "sources": sources,
        "rights": rights,
        "capabilities": capabilities,
    }


def _assert(builder: Builder, units: list[dict]) -> None:
    for unit in units:
        number = unit["number"]
        spans = [record["shippedText"] for record in builder.provenance if record["unitNumber"] == number]
        spans += [record["text"] for record in builder.authored if record["unitNumber"] == number]
        hosts: list[str] = []
        for session in unit["sessions"]:
            for step in session["steps"]:
                hosts.extend([step["title"], step["body"], step.get("bookDirection") or ""])
                reason = step.get("unavailableReason") or ""
                if reason:
                    if CYRILLIC.search(reason):
                        raise SystemExit(f"Unit {number} gap reason contains Russian")
        for example in builder.examples:
            if example["objectiveId"].startswith(f"obj-u{number}-"):
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
        if number == 27:
            joined = "\n".join(hosts)
            for banned in ("чита́ющая", "чита́вшая", "живу́щие", "active present participle", "active past participle"):
                if banned in joined:
                    raise SystemExit(f"Unit 27 contains active-participle instruction: {banned}")
            for example in builder.examples:
                if example["objectiveId"] == "obj-u27-present-passive" and example["display"].endswith("емый") and example["note"] == "we leave":
                    raise SystemExit("Present passive participle is keyed to we leave")
            for item in builder.items:
                if item["objectiveId"] == "obj-u27-present-passive" and item["promptLetter"].endswith("емый") and item["answer"] == "we leave":
                    raise SystemExit("Present passive check keys the participle to we leave")
        if number == 30:
            orient = next(step["body"] for session in unit["sessions"] for step in session["steps"] if step["id"] == "u30-st-orient")
            if "introduced the printed letter names" not in orient or "reviews admitted table names" not in orient:
                raise SystemExit("Unit 30 does not keep letter names as review")
            if "for the first time" in orient:
                raise SystemExit("Unit 30 claims a first introduction")


if __name__ == "__main__":
    built = build_units_26_30()
    print(
        f"provenance={len(built['provenance'])} items={len(built['items'])} "
        f"examples={len(built['examples'])} capabilities={len(built['capabilities'])}"
    )
    for item in built["capabilities"]:
        print("|".join([
            item["sourceId"], item["sourceRevision"], item["sourceRecordId"], item["sourceHash"],
        ]))
