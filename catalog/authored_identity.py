"""Stable authored-English identities for Units 16-20.

Units 16, 18, 19, and 20 keep the historical id bound to each exact text.
That binding does not use a shared counter, so Unit 17 inserts cannot renumber
them. Records without a historical binding, including Unit 17, use a semantic
id from the unit, owner, role, and text. An id is never reused for different text.
"""

from __future__ import annotations

import hashlib
import json
import re

STABLE_UNITS = frozenset({16, 18, 19, 20})

_PIN_ROWS = json.loads(r"""[[16, "Admitted lines", "en-u16-001"], [16, "Does this row fail the instrumental singular?", "en-u16-002"], [16, "Does this answer show the instrumental singular?", "en-u16-003"], [16, "Which admitted row is the instrumental singular?", "en-u16-004"], [16, "Admitted instruction", "en-u16-005"], [16, "Does this row fail the pencil instrumental?", "en-u16-006"], [16, "Does this answer show the pencil instrumental?", "en-u16-007"], [16, "Which admitted line is the pencil instrumental?", "en-u16-008"], [16, "Does this row fail the instrumental plural?", "en-u16-009"], [16, "Does this answer show the instrumental plural?", "en-u16-010"], [16, "Which admitted row is the instrumental plural?", "en-u16-011"], [16, "Does this row fail the masculine instrumental?", "en-u16-012"], [16, "Does this answer show the masculine instrumental?", "en-u16-013"], [16, "Which admitted row is the masculine instrumental?", "en-u16-014"], [16, "Does this row fail the plural instrumental?", "en-u16-015"], [16, "Does this answer show the plural instrumental?", "en-u16-016"], [16, "Which admitted row is the plural instrumental?", "en-u16-017"], [16, "Does this row fail the accompaniment line?", "en-u16-018"], [16, "Does this answer show the accompaniment line?", "en-u16-019"], [16, "Which admitted line is the accompaniment line?", "en-u16-020"], [16, "Does this row fail the means instrumental?", "en-u16-021"], [16, "Does this answer show the means instrumental?", "en-u16-022"], [16, "Which admitted line is the means instrumental?", "en-u16-023"], [16, "Does this row fail the between line?", "en-u16-024"], [16, "Does this answer show the between line?", "en-u16-025"], [16, "Which admitted line is the between line?", "en-u16-026"], [16, "Does this row fail the over label?", "en-u16-027"], [16, "Does this answer show the over label?", "en-u16-028"], [16, "Which admitted English label is the over label?", "en-u16-029"], [16, "Does this row fail the under label?", "en-u16-030"], [16, "Does this answer show the under label?", "en-u16-031"], [16, "Which admitted English label is the under label?", "en-u16-032"], [16, "Does this row fail the before label?", "en-u16-033"], [16, "Does this answer show the before label?", "en-u16-034"], [16, "Which admitted English label is the before label?", "en-u16-035"], [16, "The frozen source pairs the shown form", "en-u16-036"], [16, "with the admitted gloss", "en-u16-037"], [16, "Which admitted English gloss is written beside the shown form?", "en-u16-038"], [16, "Admitted instrumental rows", "en-u16-039"], [16, "Proposed Unit 16 uses frozen instrumental rows and the admitted complement span. Qualified review is pending.", "en-u16-040"], [16, "Open Lesson 16 in the copy you own. The app records do not replace that lesson.", "en-u16-041"], [16, "Guided practice", "en-u16-042"], [16, "Choose the admitted record. The feedback states the frozen field.", "en-u16-043"], [16, "Remediation", "en-u16-044"], [16, "Independent checks", "en-u16-045"], [16, "These checks use only admitted records. A miss returns to the explanation.", "en-u16-046"], [16, "Work Lesson 16 in your copy", "en-u16-047"], [16, "Do Lesson 16 in the copy you own. Use that book's explanations and practice. The proposed records in this unit are a separate licensed set and do not copy the book. Listening stays with the book. No audio is mapped here.", "en-u16-048"], [16, "Open Lesson 16 in your copy and complete its lesson work.", "en-u16-049"], [16, "Record Lesson 16 book work", "en-u16-050"], [16, "When you have done Lesson 16 in your copy, self-attest here. Attestation records your report. It does not grade the lesson, approve a mapping, or approve the proposed records.", "en-u16-051"], [16, "Self-attest only after the Lesson 16 work in your copy.", "en-u16-052"], [18, "The frozen source pairs the shown form", "en-u18-112"], [18, "with the admitted gloss", "en-u18-113"], [18, "Admitted lines", "en-u18-114"], [18, "Which admitted English gloss is written beside the shown form?", "en-u18-115"], [18, "Admitted instruction", "en-u18-116"], [18, "Admitted comparison and relative lines", "en-u18-117"], [18, "Proposed Unit 18 uses admitted comparison lines and admitted relative lines. Qualified review is pending.", "en-u18-118"], [18, "Open Lesson 18 in the copy you own. The app records do not replace that lesson.", "en-u18-119"], [18, "Guided practice", "en-u18-120"], [18, "Choose the admitted record. The feedback states the frozen field.", "en-u18-121"], [18, "Remediation", "en-u18-122"], [18, "Independent checks", "en-u18-123"], [18, "These checks use only admitted records. A miss returns to the explanation.", "en-u18-124"], [18, "Work Lesson 18 in your copy", "en-u18-125"], [18, "Do Lesson 18 in the copy you own. Use that book's explanations and practice. The proposed records in this unit are a separate licensed set and do not copy the book. Listening stays with the book. No audio is mapped here.", "en-u18-126"], [18, "Open Lesson 18 in your copy and complete its lesson work.", "en-u18-127"], [18, "Record Lesson 18 book work", "en-u18-128"], [18, "When you have done Lesson 18 in your copy, self-attest here. Attestation records your report. It does not grade the lesson, approve a mapping, or approve the proposed records.", "en-u18-129"], [18, "Self-attest only after the Lesson 18 work in your copy.", "en-u18-130"], [19, "Admitted lines", "en-u19-131"], [19, "Does this row fail the dissuade meaning?", "en-u19-132"], [19, "Does this answer show the dissuade meaning?", "en-u19-133"], [19, "Which admitted English label is the dissuade meaning?", "en-u19-134"], [19, "Admitted instruction", "en-u19-135"], [19, "Does this row fail the agree meaning?", "en-u19-136"], [19, "Does this answer show the agree meaning?", "en-u19-137"], [19, "Which admitted English label is the agree meaning?", "en-u19-138"], [19, "Does this row fail the dissuade perfective?", "en-u19-139"], [19, "Does this answer show the dissuade perfective?", "en-u19-140"], [19, "Which admitted line is the dissuade perfective?", "en-u19-141"], [19, "The frozen source pairs the shown form", "en-u19-142"], [19, "with the admitted gloss", "en-u19-143"], [19, "Which admitted English gloss is written beside the shown form?", "en-u19-144"], [19, "Admitted prefix cautions", "en-u19-145"], [19, "Proposed Unit 19 uses frozen prefix cautions plus admitted conditional and obligation spans. Probability stays unresolved. Qualified review is pending.", "en-u19-146"], [19, "Proposed Unit 19 uses frozen prefix cautions plus admitted conditional, obligation, and likelihood spans. Qualified review is pending.", "en-u19-181"], [19, "Which displayed example expresses likelihood?", "en-u19-182"], [19, "Open Lesson 19 in the copy you own. The app records do not replace that lesson.", "en-u19-147"], [19, "Guided practice", "en-u19-148"], [19, "Choose the admitted record. The feedback states the frozen field.", "en-u19-149"], [19, "Remediation", "en-u19-150"], [19, "Independent checks", "en-u19-151"], [19, "These checks use only admitted records. A miss returns to the explanation.", "en-u19-152"], [19, "Work Lesson 19 in your copy", "en-u19-153"], [19, "Do Lesson 19 in the copy you own. Use that book's explanations and practice. The proposed records in this unit are a separate licensed set and do not copy the book. Listening stays with the book. No audio is mapped here.", "en-u19-154"], [19, "Open Lesson 19 in your copy and complete its lesson work.", "en-u19-155"], [19, "Record Lesson 19 book work", "en-u19-156"], [19, "When you have done Lesson 19 in your copy, self-attest here. Attestation records your report. It does not grade the lesson, approve a mapping, or approve the proposed records.", "en-u19-157"], [19, "Self-attest only after the Lesson 19 work in your copy.", "en-u19-158"], [19, "Probability stays in the copy you own", "en-u19-159"], [19, "Probability stays in the copy you own. The admitted conditional and obligation spans do not state it.", "en-u19-160"], [20, "The frozen source pairs the shown form", "en-u20-161"], [20, "with the admitted gloss", "en-u20-162"], [20, "Admitted lines", "en-u20-163"], [20, "Which admitted English gloss is written beside the shown form?", "en-u20-164"], [20, "Admitted instruction", "en-u20-165"], [20, "Admitted motion lines", "en-u20-166"], [20, "Proposed Unit 20 uses admitted motion lines for direction, foot and transport, carrying and leading, setting out, and selected prefixed paths. Qualified review is pending.", "en-u20-167"], [20, "Open Lesson 20 in the copy you own. The app records do not replace that lesson.", "en-u20-168"], [20, "Guided practice", "en-u20-169"], [20, "Choose the admitted record. The feedback states the frozen field.", "en-u20-170"], [20, "Remediation", "en-u20-171"], [20, "Independent checks", "en-u20-172"], [20, "These checks use only admitted records. A miss returns to the explanation.", "en-u20-173"], [20, "Work Lesson 20 in your copy", "en-u20-174"], [20, "Do Lesson 20 in the copy you own. Use that book's explanations and practice. The proposed records in this unit are a separate licensed set and do not copy the book. Listening stays with the book. No audio is mapped here.", "en-u20-175"], [20, "Open Lesson 20 in your copy and complete its lesson work.", "en-u20-176"], [20, "Record Lesson 20 book work", "en-u20-177"], [20, "When you have done Lesson 20 in your copy, self-attest here. Attestation records your report. It does not grade the lesson, approve a mapping, or approve the proposed records.", "en-u20-178"], [20, "Self-attest only after the Lesson 20 work in your copy.", "en-u20-179"]]""")
HISTORICAL_AUTHORED_IDS = {(unit, text): identity for unit, text, identity in _PIN_ROWS}
_ID_TO_TEXT: dict[str, str] = {}


def begin_authored_ids() -> None:
    _ID_TO_TEXT.clear()


def _slug(value: str) -> str:
    slug = re.sub(r"[^a-z0-9]+", "-", value.lower()).strip("-")
    return slug or "record"


def semantic_authored_id(unit: int, text: str, role: str, owner: str) -> str:
    """Return the stable id for this unit, owner, role, and exact text."""
    if unit in STABLE_UNITS:
        identity = HISTORICAL_AUTHORED_IDS.get((unit, text))
        if identity is None:
            raise SystemExit(f"Unit {unit} authored text has no stable identity: {text!r}")
    else:
        material = f"{unit}\n{owner}\n{role}\n{text}".encode()
        digest = hashlib.sha256(material).hexdigest()[:12]
        identity = f"en-u{unit}-{_slug(owner)}-{_slug(role)}-{digest}"
    previous = _ID_TO_TEXT.get(identity)
    if previous is not None and previous != text:
        raise SystemExit(f"Authored id {identity} is already bound to different text")
    _ID_TO_TEXT[identity] = text
    return identity


def isolation_errors() -> list[str]:
    """Inserting, deleting, or reordering Unit 17 records must not move later units."""
    from units_16_20_intake import build_units_16_20

    def stable(built: dict) -> dict:
        authored = {
            unit: tuple(
                (record["id"], record["text"])
                for record in built["authored"]
                if record["unitNumber"] == unit
            )
            for unit in STABLE_UNITS
        }
        references = {}
        sources = {}
        for unit in built["units"]:
            number = unit["number"]
            if number not in STABLE_UNITS and number != 17:
                continue
            ref_ids = tuple(sorted({
                ref_id
                for session in unit["sessions"]
                for step in session["steps"]
                for ref_id in step["referenceIds"]
            }))
            references[number] = ref_ids
            objective_ids = {item["id"] for item in unit["objectives"]}
            source_ids = {
                record["sourceId"]
                for record in built["examples"]
                if record["objectiveId"] in objective_ids
            }
            source_ids.update(
                record["sourceId"]
                for record in built["items"]
                if record["objectiveId"] in objective_ids
            )
            ref_by_id = {record["id"]: record for record in built["references"]}
            source_ids.update(ref_by_id[ref_id]["sourceId"] for ref_id in ref_ids)
            sources[number] = tuple(sorted(source_ids))
        return {"authored": authored, "references": references, "sources": sources}

    baseline = build_units_16_20()
    base = stable(baseline)
    probes = {
        "insert": build_units_16_20(probe_says=[("Probe insert record.", "probe", "u17-probe")], probe_at="start"),
        "delete": build_units_16_20(probe_says=[("Probe insert record.", "probe", "u17-probe")], probe_at="end"),
        "reorder-a": build_units_16_20(probe_says=[
            ("Probe first record.", "probe", "u17-probe"),
            ("Probe second record.", "probe", "u17-probe"),
        ], probe_at="start"),
        "reorder-b": build_units_16_20(probe_says=[
            ("Probe second record.", "probe", "u17-probe"),
            ("Probe first record.", "probe", "u17-probe"),
        ], probe_at="start"),
    }
    errors = []
    for name, built in probes.items():
        snap = stable(built)
        for unit in STABLE_UNITS:
            if snap["authored"][unit] != base["authored"][unit]:
                errors.append(f"{name} changed unit {unit} authored identities")
            if snap["references"][unit] != base["references"][unit]:
                errors.append(f"{name} changed unit {unit} references")
            if snap["sources"][unit] != base["sources"][unit]:
                errors.append(f"{name} changed unit {unit} sources")
    if stable(probes["reorder-a"])["authored"] != stable(probes["reorder-b"])["authored"]:
        errors.append("reordering Unit 17 probes changed stable authored identities")
    again = stable(build_units_16_20())
    if again != base:
        errors.append("regeneration changed stable identities")
    first = {record["id"]: record["text"] for record in baseline["authored"]}
    second = {record["id"]: record["text"] for record in build_units_16_20()["authored"]}
    if first != second:
        errors.append("regeneration mapped an authored id to different text")
    if len(first) != len(baseline["authored"]):
        errors.append("an authored id is duplicated")
    for number in STABLE_UNITS:
        if "ref-u17-attribution" in base["references"][number]:
            errors.append(f"unit {number} reaches the Unit 17 attribution")
        clock = "src-wikipedia-date-time-country-1366690592"
        date = "src-wikipedia-date-notation-russia-1357134172"
        if clock in base["sources"][number] or date in base["sources"][number]:
            errors.append(f"unit {number} reaches a Unit 17 Wikipedia source")
    if "ref-u17-attribution" not in base["references"][17]:
        errors.append("unit 17 does not reach its own attribution")
    return errors
