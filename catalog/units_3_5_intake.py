#!/usr/bin/env python3
"""Build proposed Units 3–5 from frozen admitted sources only.

Russian strings are copied from the frozen OpenRussian rows or from exact
Wikibooks excerpts. This module does not conjugate, decline, or compose
new Russian sentences. Learner-facing dictionary glosses are delimited
sense selections; the full source field stays on the provenance record.
"""

from __future__ import annotations

import hashlib
import json
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
FREEZE_PATH = ROOT / "catalog" / "sources" / "units-3-5" / "FREEZE.json"
SOURCE_ROOT = FREEZE_PATH.parent

OPENRUSSIAN_ID = "src-openrussian-50e210c4"
WIKIBOOKS_ID = "src-wikibooks-russian-cc-by-sa"
OPENRUSSIAN_RIGHTS = "rights-openrussian-50e210c4"
WIKIBOOKS_RIGHTS = "rights-wikibooks-russian-cc-by-sa"
LICENSE_ID = "CC BY-SA 4.0"
LICENSE_URL = "https://creativecommons.org/licenses/by-sa/4.0/"
SHARE_ALIKE = (
    "Adapted curriculum data, selection, and arrangement are distributed under CC BY-SA 4.0. "
    "Application code, user data, and user recordings are outside that statement. "
    "No audio is relicensed because this unit ships no audio."
)
PENDING = "PENDING_QUALIFIED_REVIEW"
SCAFFOLD = "INDEPENDENTLY_AUTHORED_SCAFFOLD"
CYRILLIC = re.compile(r"[\u0400-\u04FF]")
REGION = re.compile(r"[\u0400-\u04FF\u0301](?:[\u0400-\u04FF\u0301]|[ \t]+(?=[\u0400-\u04FF]))*[?!.,]*")
VOWELS = set("аеёиоуыэюяАЕЁИОУЫЭЮЯ")
CLAIM = re.compile(
    r"(?i)(\brussian\b.*\b(has|have|is|are)\b|\b(has|have)\b.*\b(gender|genders|cases|tense)\b|\bten genders\b|\b\d+\s+genders\b)"
)
GRAMMAR_TERM = re.compile(
    r"(?i)\b(genders?|cases?|tenses?|verbs?|nouns?|pronouns?|masculine|feminine|neuter|nominative|prepositional|indeclinable|declinable)\b"
)
DIRTY_LEARNER_TEXT = ("methinks", "tea-party", "auscultate", "she, it, he", "gapirish", "womanfolk")

NOUN_FIELDS = [
    "bare", "accented", "translations_en", "translations_de", "gender", "partner", "animate",
    "indeclinable", "sg_only", "pl_only", "sg_nom", "sg_gen", "sg_dat", "sg_acc", "sg_inst",
    "sg_prep", "pl_nom", "pl_gen", "pl_dat", "pl_acc", "pl_inst", "pl_prep",
]
VERB_FIELDS = [
    "bare", "accented", "translations_en", "translations_de", "aspect", "partner",
    "imperative_sg", "imperative_pl", "past_m", "past_f", "past_n", "past_pl",
    "presfut_sg1", "presfut_sg2", "presfut_sg3", "presfut_pl1", "presfut_pl2", "presfut_pl3",
]
OTHER_FIELDS = ["bare", "accented", "translations_en", "translations_de"]
FIELDS = {"nouns.csv": NOUN_FIELDS, "verbs.csv": VERB_FIELDS, "others.csv": OTHER_FIELDS}
GENDER_LABEL = {"m": "masculine", "f": "feminine", "n": "neuter"}
VERB_PERSON = [
    ("presfut_sg1", "я"),
    ("presfut_sg2", "ты"),
    ("presfut_sg3", "он"),
    ("presfut_pl1", "мы"),
    ("presfut_pl2", "вы"),
    ("presfut_pl3", "они"),
]
GENDER_SENTENCE = (
    "So, in Russian, nouns have an intrinsic property called 'gender', which comes in three types: "
    "masculine, feminine, and neuter."
)
PRESENT_SENTENCE = (
    "So, to form the present tense, you simply take the ''imperfective aspect'' of the verb and add the appropriate endings."
)
INDECLINABLE_SENTENCE = (
    "these have irregular genders, and are indeclinable (that is, they don't change according to case or number)"
)
CASES_SIX = "Russian has six cases"
CASES_DEFAULT = (
    "The nominative case is the default case of a word, as found in dictionaries and when no other case is specifically used."
)
DYADYA_SENTENCE = (
    "though it has a 'feminine' ending and conjugates like a feminine noun, 'uncle' is 'inherently' masculine"
)

# Full source field, then the delimited beginner sense. Equal values ship the whole field.
GLOSS = {
    "nouns.csv:стол": ("table, desk, board; diet, cooking, cuisine; department, section", "table"),
    "nouns.csv:дом": ("house, home, house-hold", "house"),
    "nouns.csv:город": ("town", "town"),
    "nouns.csv:брат": ("brother", "brother"),
    "nouns.csv:учитель": ("teacher, tutor", "teacher"),
    "nouns.csv:хлеб": ("bread, loaf, corn, crops, cereals, grain, living, means of subsistence, daily bread", "bread"),
    "nouns.csv:чай": ("tea, tea-party, methinks, maybe", "tea"),
    "nouns.csv:друг": ("friend", "friend"),
    "nouns.csv:книга": ("book, volume", "book"),
    "nouns.csv:вода": ("water", "water"),
    "nouns.csv:школа": ("school, schoolhouse", "school"),
    "nouns.csv:работа": ("work, working, job", "work"),
    "nouns.csv:мама": ("mom, mama, mommy, mummy", "mom"),
    "nouns.csv:женщина": ("woman, women, womanfolk, womankind", "woman"),
    "nouns.csv:семья": ("family", "family"),
    "nouns.csv:дверь": ("door", "door"),
    "nouns.csv:окно": ("window, casement-window, window-sill, gap, opening, port, slot, free period, break", "window"),
    "nouns.csv:письмо": ("letter, writing, script", "letter"),
    "nouns.csv:молоко": ("milk", "milk"),
    "nouns.csv:море": ("sea, ocean", "sea"),
    "nouns.csv:место": ("place, spot, site, locality; region, area; site, scene; room, space; job, position; piece of luggage", "place"),
    "nouns.csv:слово": ("word; speech", "word"),
    "nouns.csv:мужчина": ("man", "man"),
    "nouns.csv:дядя": ("uncle, Mister, man, fellow", "uncle"),
    "nouns.csv:радио": ("radio, radio set, wireless", "radio"),
    "nouns.csv:такси": ("taxi", "taxi"),
    "nouns.csv:метро": ("metro; subway; underground", "metro"),
    "nouns.csv:кино": ("cinema, pictures, movies", "cinema"),
    "nouns.csv:пальто": ("coat, topcoat", "coat"),
    "nouns.csv:кофе": ("coffee", "coffee"),
    "nouns.csv:кафе": ("café, coffee-house", "café"),
    "nouns.csv:меню": ("menu, bill of fare", "menu"),
    "others.csv:я": ("I", "I"),
    "others.csv:мы": ("we", "we"),
    "others.csv:ты": ("you", "you"),
    "others.csv:вы": ("you (plural); you (formal)", "you (plural)"),
    "others.csv:он": ("he; it (masculine inanimate nouns)", "he"),
    "others.csv:она": ("she, it, he, she, it, he, she", "she"),
    "others.csv:оно": ("it, he, she", "it"),
    "others.csv:они": ("they", "they"),
    "others.csv:здравствуйте": ("hello", "hello"),
    "others.csv:спасибо": ("thanks, thank you", "thank you"),
    "others.csv:пожалуйста": ("please; you are welcome; here you go", "please"),
    "others.csv:да": ("yes", "yes"),
    "others.csv:нет": ("no; not", "no"),
    "others.csv:это": ("this is; that is; it", "this is"),
    "others.csv:в": ("in; at; into; to; for; on; within", "in"),
    "others.csv:на": ("on (place); on (day/week); on (transport means); to; onto, upon; at", "on (place)"),
    "others.csv:о": ("about; of; on; against", "about"),
    "verbs.csv:чита'ть": ("read", "read"),
    "verbs.csv:де'лать": ("make, do", "make"),
    "verbs.csv:знать": ("know, be aware, be acquainted, have a knowledge, aristocracy, nobility, the elite, evidently, it seems", "know"),
    "verbs.csv:рабо'тать": ("work, operate", "work"),
    "verbs.csv:понима'ть": ("understand, comprehend, realize", "understand"),
    "verbs.csv:слу'шать": ("listen, hear, attend, examine chest, auscultate, obey", "listen"),
    "verbs.csv:жить": ("live", "live"),
    "verbs.csv:ви'деть": ("see", "see"),
    "verbs.csv:смотре'ть": ("look, watch", "look"),
    "verbs.csv:писа'ть": ("write; paint", "write"),
    "verbs.csv:идти'": ("go, walk", "go"),
}


def sha256_text(text: str) -> str:
    return hashlib.sha256(text.encode("utf-8")).hexdigest()


def git_blob_sha1(data: bytes) -> str:
    return hashlib.sha1(f"blob {len(data)}\0".encode() + data).hexdigest()


def delimited_senses(original: str) -> list[str]:
    return [part.strip() for part in re.split(r"[,;]", original) if part.strip()]


_WIKI_VOWEL = "аеёиоуыэюяАЕЁИОУЫЭЮЯ"
_WIKI_STRESS = re.compile("'''([" + _WIKI_VOWEL + "])'''")


def _wiki_word_char(char: str) -> bool:
    return bool(char) and "\u0400" <= char <= "\u04FF" and char.isalpha()


_LANG_RU = re.compile(r"\{\{lang\|ru\|([^{}]*)\}\}")
_WIKI_LINK = re.compile(r"\[\[([^\[\]]+)\]\]")


def unfold_wiki_labels(text: str) -> str:
    """Replace lang templates and wiki links with their visible labels, then emphasis.

    The replacement is mechanical. It does not paraphrase the retained span.
    """

    def link(match: re.Match) -> str:
        body = match.group(1)
        if "|" in body:
            return body.split("|", 1)[1]
        return body

    return format_wikibooks_emphasis(_WIKI_LINK.sub(link, _LANG_RU.sub(r"\1", text)))


def text_in_frozen(original: str, shipped: str) -> bool:
    if not shipped:
        return False
    if shipped in original or shipped in format_wikibooks_emphasis(original):
        return True
    return shipped in unfold_wiki_labels(original)


def format_wikibooks_emphasis(text: str) -> str:
    """Turn admitted Wikibooks emphasis into learner-facing text.

    A bold single vowel inside a word is that source's stress mark. Any other
    bold or italic emphasis is removed. Stress is not invented for an unmarked vowel.
    """

    def mark(match: re.Match) -> str:
        vowel = match.group(1)
        start, end = match.span()
        before = text[start - 1] if start else ""
        after = text[end] if end < len(text) else ""
        if vowel in "ёЁ":
            return vowel
        if _wiki_word_char(before) or _wiki_word_char(after):
            return vowel + "\u0301"
        return vowel

    cleaned = _WIKI_STRESS.sub(mark, text)
    return cleaned.replace("'''", "").replace("''", "")


def format_stress(accented: str) -> str:
    if "'" not in accented:
        return accented
    out: list[str] = []
    index = 0
    while index < len(accented):
        char = accented[index]
        nxt = accented[index + 1] if index + 1 < len(accented) else ""
        if nxt == "'" and ("\u0400" <= char <= "\u04FF") and char.isalpha():
            out.append(char + "\u0301")
            index += 2
            continue
        if char == "'":
            raise SystemExit(f"Unplaced stress mark in {accented!r}")
        out.append(char)
        index += 1
    return "".join(out)


def stress_position(shipped: str) -> int:
    vowels: list[int] = []
    marked = None
    chars = list(shipped)
    for index, char in enumerate(chars):
        if char in VOWELS:
            vowels.append(index)
            if index + 1 < len(chars) and chars[index + 1] == "\u0301":
                marked = len(vowels)
    if marked is None:
        raise SystemExit(f"No source stress mark in {shipped!r}")
    return marked


def english_span_boundary(host: str, index: int, end: int) -> bool:
    span = host[index:end]
    if any(ord(char) > 127 for char in span):
        return True
    before = " " if index == 0 else host[index - 1]
    after = " " if end == len(host) else host[end]
    start_ok = (not before.isalpha()) or (not span[0].isalpha())
    end_ok = (not after.isalpha()) or (not span[-1].isalpha())
    return start_ok and end_ok


def unbound_letters(host: str, spans: list[str]) -> str:
    covered = [False] * len(host)
    ordered = sorted({span for span in spans if span}, key=len, reverse=True)
    for span in ordered:
        start = 0
        while start <= len(host) - len(span):
            index = host.find(span, start)
            if index < 0:
                break
            end = index + len(span)
            if not any(covered[index:end]) and english_span_boundary(host, index, end):
                for cursor in range(index, end):
                    covered[cursor] = True
            start = index + 1
    return "".join(char for index, char in enumerate(host) if not covered[index] and char.isalpha())


class FrozenSources:
    def __init__(self) -> None:
        self.freeze = json.loads(FREEZE_PATH.read_text(encoding="utf-8"))
        self.pages: dict[str, str] = {}
        for page in self.freeze["wikibooks"]["pages"]:
            path = SOURCE_ROOT / page["file"]
            data = path.read_bytes()
            text = data.decode("utf-8")
            digest = hashlib.sha256(data).hexdigest()
            if digest != page["sha256"]:
                raise SystemExit(f"Wiki snapshot hash mismatch for {page['file']}")
            revision = json.loads((SOURCE_ROOT / page["revisionFile"]).read_text(encoding="utf-8"))
            if str(revision["revid"]) != str(page["oldid"]):
                raise SystemExit(f"Wiki revision id does not match oldid for {page['file']}")
            if not revision.get("parentid") or not revision.get("timestamp") or not revision.get("sha1"):
                raise SystemExit(f"Wiki revision metadata is incomplete for {page['file']}")
            if hashlib.sha1(data).hexdigest() != revision["sha1"] or len(data) != revision["size"]:
                raise SystemExit(f"Wiki snapshot bytes do not match the retained revision for {page['file']}")
            self.pages[page["key"]] = text
        self.file_lines: dict[str, set[str]] = {}
        for name, meta in self.freeze["openrussian"]["files"].items():
            data = (SOURCE_ROOT / meta["path"]).read_bytes()
            if hashlib.sha256(data).hexdigest() != meta["sha256"]:
                raise SystemExit(f"Retained {name} sha256 does not match the freeze")
            if git_blob_sha1(data) != meta["gitBlob"]:
                raise SystemExit(f"Retained {name} git blob does not match the freeze")
            self.file_lines[name] = set(data.decode("utf-8").splitlines())
        self.rows: dict[str, dict] = {}
        commit = self.freeze["openrussian"]["commit"]
        if len(commit) != 40:
            raise SystemExit("OpenRussian commit is unresolved")
        for row in self.freeze["openrussian"]["rows"]:
            if row["line"] not in self.file_lines[row["file"]]:
                raise SystemExit(f"Selected row {row['recordId']} does not occur in the retained {row['file']} bytes")
            fields = FIELDS[row["file"]]
            parts = row["line"].split("\t")
            if len(parts) != len(fields):
                raise SystemExit(f"Row {row['recordId']} has {len(parts)} fields")
            if sha256_text(row["line"]) != row["sha256"]:
                raise SystemExit(f"Row hash mismatch for {row['recordId']}")
            self.rows[row["recordId"]] = dict(zip(fields, parts))
        license_path = SOURCE_ROOT / self.freeze["openrussian"]["licenseFile"]
        license_hash = hashlib.sha256(license_path.read_bytes()).hexdigest()
        if license_hash != self.freeze["openrussian"]["licenseSha256"]:
            raise SystemExit("OpenRussian license hash mismatch")
        if "Attribution-ShareAlike 4.0 International" not in license_path.read_text(encoding="utf-8"):
            raise SystemExit("OpenRussian license text is not CC BY-SA 4.0")
        copyrights = self.pages["copyrights"]
        if "Creative Commons Attribution-ShareAlike 4.0" not in copyrights:
            raise SystemExit("Frozen Wikibooks copyrights page does not state CC BY-SA 4.0")

    def row(self, record_id: str) -> dict:
        if record_id not in self.rows:
            raise SystemExit(f"Selected row {record_id} is not in the freeze")
        return self.rows[record_id]

    def require(self, page: str, needle: str) -> str:
        if needle not in self.pages[page]:
            raise SystemExit(f"Required excerpt missing from {page}: {needle}")
        return needle

    def oldid(self, page: str) -> str:
        for item in self.freeze["wikibooks"]["pages"]:
            if item["key"] == page:
                return item["oldid"]
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
        self.openrussian_attr = (
            "Badestrand/russian-dictionary contributors, commit "
            f"{commit}, CC BY-SA 4.0."
        )
        self.wikibooks_attr = (
            "Wikibooks Russian grammar contributors, CC BY-SA 4.0. "
            "Page history for each frozen oldid identifies the contributors."
        )

    def say(self, unit: int, text: str) -> str:
        if CYRILLIC.search(text):
            raise SystemExit(f"English scaffold contains Cyrillic: {text!r}")
        if CLAIM.search(text) or (GRAMMAR_TERM.search(text) and "?" not in text):
            raise SystemExit(f"independently authored English makes a linguistic claim: {text!r}")
        key = (unit, text)
        if key in self._said:
            return text
        self._said_count += 1
        self._said[key] = f"en-u{unit}-{self._said_count:03d}"
        self.authored.append(
            {
                "id": self._said[key],
                "unitNumber": unit,
                "text": text,
                "classification": SCAFFOLD,
            }
        )
        return text

    def add_provenance(
        self,
        item_id: str,
        unit: int,
        objective: str,
        record_type: str,
        source_id: str,
        source_url: str,
        source_revision: str,
        source_record_id: str,
        original: str,
        shipped: str,
        transformation: str,
        attribution: str,
    ) -> dict:
        if not source_revision:
            raise SystemExit(f"{item_id} has an unresolved source revision")
        if "/tree/" in source_url:
            raise SystemExit(f"{item_id} source URL uses a tree link")
        record = {
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
        }
        self.provenance.append(record)
        return record

    def wiki_url(self, page: str) -> str:
        titles = {
            "gender": "Russian/Grammar/Gender",
            "pronouns": "Russian/Grammar/Pronouns",
            "verbs": "Russian/Grammar/Verbs",
            "cases": "Russian/Grammar/Cases",
            "nominative": "Russian/Grammar/Nominative",
            "prepositional": "Russian/Grammar/Prepositional_case",
            "interrogative": "Russian/Interrogative_Pronouns",
            "prepositions": "Russian/Prepositions",
            "personal-pronouns": "Russian/Personal_Pronouns",
            "copyrights": "Wikibooks:Copyrights",
        }
        return f"https://en.wikibooks.org/wiki/{titles[page]}?oldid={self.frozen.oldid(page)}"

    def lexical(
        self,
        item_id: str,
        unit: int,
        objective: str,
        record_id: str,
        field: str,
        record_type: str = "lexical record",
    ) -> str:
        row = self.frozen.row(record_id)
        original = row[field]
        if original == "":
            raise SystemExit(f"{record_id} field {field} is blank")
        shipped = format_stress(original) if field in {"accented", "sg_nom", "sg_prep", "pl_nom"} or field.startswith("presfut") else original
        file_name = record_id.split(":", 1)[0]
        self.add_provenance(
            item_id,
            unit,
            objective,
            record_type,
            OPENRUSSIAN_ID,
            self.openrussian_url + "/" + file_name,
            self.openrussian_revision,
            record_id + "#" + field,
            original,
            shipped,
            "formatting" if shipped != original else "selection",
            self.openrussian_attr,
        )
        return shipped

    def gloss(self, item_id: str, unit: int, objective: str, record_id: str) -> str:
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
            item_id,
            unit,
            objective,
            "lexical record",
            OPENRUSSIAN_ID,
            self.openrussian_url + "/" + file_name,
            self.openrussian_revision,
            record_id + "#translations_en",
            original,
            shipped,
            transformation,
            self.openrussian_attr,
        )
        return shipped

    def flag(self, item_id: str, unit: int, objective: str, record_id: str) -> str:
        value = self.frozen.row(record_id)["indeclinable"]
        if value not in {"0", "1"}:
            raise SystemExit(f"{record_id} indeclinable flag is not 0 or 1")
        self.add_provenance(
            item_id,
            unit,
            objective,
            "lexical record",
            OPENRUSSIAN_ID,
            self.openrussian_url + "/nouns.csv",
            self.openrussian_revision,
            record_id + "#indeclinable",
            value,
            value,
            "selection",
            self.openrussian_attr,
        )
        return value

    def wiki_quote(
        self,
        item_id: str,
        unit: int,
        objective: str,
        page: str,
        needle: str,
        shipped: str,
        record_type: str,
        transformation: str = "selection",
    ) -> str:
        original = self.frozen.require(page, needle)
        plain = shipped.replace("\u0301", "")
        source_plain = original.replace("'", "")
        if shipped not in original and plain not in original and plain not in source_plain:
            raise SystemExit(f"Shipped text {shipped!r} is not in the {page} excerpt")
        self.add_provenance(
            item_id,
            unit,
            objective,
            record_type,
            WIKIBOOKS_ID,
            self.wiki_url(page),
            self.frozen.oldid(page),
            page + ":" + needle[:80],
            original,
            shipped,
            transformation,
            self.wikibooks_attr,
        )
        return shipped

    def example(self, example_id: str, display: str, note: str, objective: str, source_id: str) -> None:
        if CYRILLIC.search(note):
            raise SystemExit(f"Example note contains unsourced Cyrillic: {example_id}")
        self.examples.append(
            {
                "id": example_id,
                "kind": "LEXICAL",
                "letterId": "",
                "display": display,
                "note": note,
                "sourceId": source_id,
                "reviewStatus": PENDING,
                "objectiveId": objective,
            }
        )

    def item(
        self,
        item_id: str,
        prompt: str,
        prompt_letter: str,
        options: list[str],
        answer: str,
        feedback: str,
        objective: str,
        source_id: str,
        kind: str,
    ) -> None:
        if answer not in options or len(options) != len(set(options)):
            raise SystemExit(f"Unusable answer key for {item_id}")
        if CYRILLIC.search(prompt):
            raise SystemExit(f"Prompt contains Cyrillic: {item_id}")
        self.items.append(
            {
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
            }
        )


def build_units_3_5() -> dict:
    frozen = FrozenSources()
    builder = Builder(frozen)
    nouns = _nouns(builder)
    phrases = _phrases(builder)
    pronouns = _pronouns(builder)
    verbs = _verbs(builder)
    questions = _questions(builder)
    prepositional = _prepositional(builder)
    indeclinable = _indeclinable(builder)
    units = [
        _unit_3(builder, nouns, phrases),
        _unit_4(builder, pronouns, verbs),
        _unit_5(builder, questions, prepositional, indeclinable),
    ]
    _assert_coverage(builder)
    _assert_unit_text(units, builder.provenance)
    _assert_english(builder, units)
    _assert_cards(units)
    return {
        "sources": [
            {
                "id": OPENRUSSIAN_ID,
                "title": "OpenRussian dictionary data",
                "license": LICENSE_ID,
                "attribution": builder.openrussian_attr,
                "modification": "Selected rows, stress-mark formatting, and beginner sense selection. No generated forms.",
            },
            {
                "id": WIKIBOOKS_ID,
                "title": "Wikibooks Russian grammar",
                "license": LICENSE_ID,
                "attribution": builder.wikibooks_attr,
                "modification": "Selected excerpts. Russian quotations are unchanged. English claims are quoted, not paraphrased.",
            },
        ],
        "rights": [
            {
                "id": OPENRUSSIAN_RIGHTS,
                "sourceId": OPENRUSSIAN_ID,
                "license": LICENSE_ID,
                "permitsAppUse": True,
            },
            {
                "id": WIKIBOOKS_RIGHTS,
                "sourceId": WIKIBOOKS_ID,
                "license": LICENSE_ID,
                "permitsAppUse": True,
            },
        ],
        "references": [_attribution_reference()],
        "examples": builder.examples,
        "items": builder.items,
        "provenance": builder.provenance,
        "authored": builder.authored,
        "units": units,
    }


def _entry(form: str, gloss: str) -> str:
    return form + " — " + gloss + ". "


def _nouns(builder: Builder) -> list[dict]:
    groups = {
        "masculine": ["стол", "дом", "город", "брат", "учитель", "хлеб", "чай", "друг"],
        "feminine": ["книга", "вода", "школа", "работа", "мама", "женщина", "семья", "дверь"],
        "neuter": ["окно", "письмо", "молоко", "море", "место", "слово"],
    }
    records = []
    for label, bares in groups.items():
        for bare in bares:
            row = builder.frozen.row(f"nouns.csv:{bare}")
            if GENDER_LABEL.get(row["gender"]) != label:
                raise SystemExit(f"{bare} gender {row['gender']} is not {label}")
            if row["indeclinable"] != "0":
                raise SystemExit(f"{bare} is indeclinable and is not in the declinable noun set")
            shipped = builder.lexical(f"prov-u3-noun-{bare}", 3, "obj-u3-gender", f"nouns.csv:{bare}", "accented")
            gloss = builder.gloss(f"prov-u3-gloss-{bare}", 3, "obj-u3-gender", f"nouns.csv:{bare}")
            records.append({"bare": bare, "shipped": shipped, "gloss": gloss, "gender": label, "source": OPENRUSSIAN_ID})
    builder.frozen.require("gender", "'''Мужчина''' - Man")
    row = builder.frozen.row("nouns.csv:мужчина")
    if row["gender"] != "m":
        raise SystemExit("мужчина is not masculine in the frozen row")
    shipped = builder.lexical("prov-u3-noun-мужчина", 3, "obj-u3-gender", "nouns.csv:мужчина", "accented")
    gloss = builder.gloss("prov-u3-gloss-мужчина", 3, "obj-u3-gender", "nouns.csv:мужчина")
    records.append({"bare": "мужчина", "shipped": shipped, "gloss": gloss, "gender": "masculine", "source": OPENRUSSIAN_ID, "exception": True})
    builder.wiki_quote(
        "prov-u3-dyadya-note",
        3,
        "obj-u3-gender",
        "gender",
        DYADYA_SENTENCE,
        DYADYA_SENTENCE,
        "explanation",
    )
    form = builder.lexical("prov-u3-noun-дядя", 3, "obj-u3-gender", "nouns.csv:дядя", "accented")
    gloss = builder.gloss("prov-u3-gloss-дядя", 3, "obj-u3-gender", "nouns.csv:дядя")
    records.append({"bare": "дядя", "shipped": form, "gloss": gloss, "gender": "masculine", "source": WIKIBOOKS_ID, "exception": True})
    builder.wiki_quote("prov-u3-gender-sentence", 3, "obj-u3-gender", "gender", GENDER_SENTENCE, GENDER_SENTENCE, "explanation")
    for label in ("masculine", "feminine", "neuter"):
        builder.wiki_quote(f"prov-u3-label-{label}", 3, "obj-u3-gender", "gender", GENDER_SENTENCE, label, "reference")
    return records


def _phrases(builder: Builder) -> list[dict]:
    phrases = []
    for bare in ["здравствуйте", "спасибо", "пожалуйста", "да", "нет", "это"]:
        shipped = builder.lexical(f"prov-u3-phrase-{bare}", 3, "obj-u3-phrases", f"others.csv:{bare}", "accented")
        gloss = builder.gloss(f"prov-u3-phrase-gloss-{bare}", 3, "obj-u3-phrases", f"others.csv:{bare}")
        phrases.append({"id": bare, "shipped": shipped, "gloss": gloss, "source": OPENRUSSIAN_ID})
    quoted = [
        ("moy-stol", "gender", "'my table' ('''мой стол''')", "мой стол", "my table", "obj-u3-naming"),
        ("moya-sobaka", "gender", "'my dog' ('''моя собака''')", "моя собака", "my dog", "obj-u3-naming"),
        ("chto-eto", "interrogative", "What is this? - '''Что это?'''", "Что это?", "What is this?", "obj-u3-naming"),
        ("kto-eto", "interrogative", "Who is this? - '''Кто это?'''", "Кто это?", "Who is this?", "obj-u3-naming"),
        ("kak-dela", "interrogative", "How do you do?/How're things? - '''Как дела?'''", "Как дела?", "How do you do?/How're things?", "obj-u3-phrases"),
        ("gde-vy", "personal-pronouns", "Where are you? - '''Где вы?'''", "Где вы?", "Where are you?", "obj-u3-phrases"),
    ]
    for item_id, page, needle, shipped, gloss, objective in quoted:
        builder.wiki_quote(f"prov-u3-phrase-{item_id}", 3, objective, page, needle, shipped, "example")
        builder.wiki_quote(f"prov-u3-phrase-{item_id}-en", 3, objective, page, needle, gloss, "reference")
        phrases.append({"id": item_id, "shipped": shipped, "gloss": gloss, "source": WIKIBOOKS_ID, "objective": objective})
    return phrases


def _pronouns(builder: Builder) -> list[dict]:
    builder.frozen.require("nominative", "Я ('I')")
    builder.frozen.require("nominative", "Мы ('We')")
    records = []
    for bare in ["я", "мы", "ты", "вы", "он", "она", "оно", "они"]:
        shipped = builder.lexical(f"prov-u4-pronoun-{bare}", 4, "obj-u4-pronouns", f"others.csv:{bare}", "accented")
        gloss = builder.gloss(f"prov-u4-pronoun-gloss-{bare}", 4, "obj-u4-pronouns", f"others.csv:{bare}")
        records.append({"bare": bare, "id": bare, "shipped": shipped, "gloss": gloss})
    return records


def _verbs(builder: Builder) -> list[dict]:
    selected = [
        "verbs.csv:чита'ть",
        "verbs.csv:де'лать",
        "verbs.csv:знать",
        "verbs.csv:рабо'тать",
        "verbs.csv:понима'ть",
        "verbs.csv:слу'шать",
        "verbs.csv:жить",
        "verbs.csv:говори'ть",
        "verbs.csv:ви'деть",
        "verbs.csv:смотре'ть",
        "verbs.csv:писа'ть",
        "verbs.csv:идти'",
    ]
    builder.wiki_quote("prov-u4-govorit-gloss", 4, "obj-u4-present", "verbs", "to speak", "to speak", "reference")
    builder.wiki_quote("prov-u4-present-rule", 4, "obj-u4-present", "verbs", PRESENT_SENTENCE, PRESENT_SENTENCE, "explanation")
    records = []
    for record_id in selected:
        row = builder.frozen.row(record_id)
        if row["aspect"] != "imperfective":
            raise SystemExit(f"{record_id} is not imperfective")
        if record_id == "verbs.csv:говори'ть":
            if row["translations_en"] != "speak, talk gapirish":
                raise SystemExit("говорить gloss changed; the dirty field must not be shipped")
            meaning = "to speak"
        else:
            meaning = builder.gloss(f"prov-u4-verb-gloss-{row['bare']}", 4, "obj-u4-present", record_id)
        infinitive = builder.lexical(f"prov-u4-verb-{row['bare']}", 4, "obj-u4-present", record_id, "accented")
        forms = {}
        for field, pronoun in VERB_PERSON:
            if not row[field]:
                raise SystemExit(f"{record_id} is missing {field}")
            forms[pronoun] = builder.lexical(
                f"prov-u4-form-{row['bare']}-{pronoun}",
                4,
                "obj-u4-present",
                record_id,
                field,
            )
        records.append({"bare": row["bare"], "infinitive": infinitive, "gloss": meaning, "forms": forms})
    return records


def _questions(builder: Builder) -> list[dict]:
    rows = [
        ("kto", "Кто", "Who", "| Кто || ''kto'' || Who"),
        ("chto", "Что", "What", "| Что || ''što'' || What"),
        ("kogda", "Когда", "When", "| Когда || ''kogdá'' || When"),
        ("pochemu", "Почему", "Why", "| Почему || ''počemú'' || Why"),
        ("kak", "Как", "How", "| Как || ''kak'' || How"),
        ("gde", "Где", "Where (location)", "| Где || ''gde'' || Where (location)"),
        ("kuda", "Куда", "Whither, where to", "| Куда || ''kudá'' || Whither, where to"),
        ("otkuda", "Откуда", "Whence, where from", "| Откуда || ''otkúda'' || Whence, where from"),
        ("skolko", "Сколько", "How much, how many", "| Сколько || ''skól'ko'' || How much, how many"),
        ("kakoy", "Какой", "What kind", "| Какой || ''kakój'' || What kind"),
    ]
    records = []
    for item_id, word, gloss, needle in rows:
        builder.wiki_quote(f"prov-u5-q-{item_id}", 5, "obj-u5-questions", "interrogative", needle, word, "lexical record")
        builder.wiki_quote(f"prov-u5-q-{item_id}-en", 5, "obj-u5-questions", "interrogative", needle, gloss, "reference")
        records.append({"id": item_id, "shipped": word, "gloss": gloss})
    patterns = [
        ("chto-eto", "What is this? - '''Что это?'''", "Что это?", "What is this?"),
        ("kto-eto", "Who is this? - '''Кто это?'''", "Кто это?", "Who is this?"),
        ("kak-dela", "How do you do?/How're things? - '''Как дела?'''", "Как дела?", "How do you do?/How're things?"),
        ("gde-zhivete", "Where do you live? - '''Где вы живёте?'''", "Где вы живёте?", "Where do you live?"),
        ("kuda-idete", "Where are you walking to? - '''Куда вы идёте?'''", "Куда вы идёте?", "Where are you walking to?"),
        ("pochemu", "Why doesn't he know? - '''Почему он не знает?'''", "Почему он не знает?", "Why doesn't he know?"),
        ("kakoy-syr", "What kind of cheese? - '''Какой сыр?'''", "Какой сыр?", "What kind of cheese?"),
    ]
    for item_id, needle, shipped, gloss in patterns:
        builder.wiki_quote(f"prov-u5-pattern-{item_id}", 5, "obj-u5-questions", "interrogative", needle, shipped, "example")
        builder.wiki_quote(f"prov-u5-pattern-{item_id}-en", 5, "obj-u5-questions", "interrogative", needle, gloss, "reference")
        records.append({"id": "pattern-" + item_id, "shipped": shipped, "gloss": gloss, "pattern": True})
    return records


def _prepositional(builder: Builder) -> list[dict]:
    examples = [
        ("o-knige", "prepositional", "About the book - '''Книга --> О книге'''", "О книге", "About the book"),
        ("o-pisme", "prepositional", "About the letter - '''Письмо --> О письме'''", "О письме", "About the letter"),
        ("o-sade", "prepositional", "About the garden - '''Сад --> О саде'''", "О саде", "About the garden"),
        ("v-moskve", "prepositional", "'I'm in Moscow', '''я в Москве'''", "я в Москве", "I'm in Moscow"),
        ("na-kryshe", "prepositional", "'He's on the roof', '''Он на крыше'''", "Он на крыше", "He's on the roof"),
        ("v-teatre", "prepositional", "'He is at the theatre', use '''в''' + ''prep'', as he is ''in'' that particular place: '''Он в театре'''", "Он в театре", "He is at the theatre"),
        ("na-stole", "prepositions", "Ваза стоит на столе. :The vase stands on the table", "Ваза стоит на столе.", "The vase stands on the table"),
        ("na-stene", "prepositions", "Картина висит на стене. :The painting hangs on the wall", "Картина висит на стене.", "The painting hangs on the wall"),
    ]
    records = []
    for item_id, page, needle, shipped, gloss in examples:
        builder.wiki_quote(f"prov-u5-prep-{item_id}", 5, "obj-u5-prepositional", page, needle, shipped, "example")
        builder.wiki_quote(f"prov-u5-prep-{item_id}-en", 5, "obj-u5-prepositional", page, needle, gloss, "reference")
        records.append({"id": item_id, "shipped": shipped, "gloss": gloss})
    for bare in ["книга", "письмо", "окно", "школа"]:
        row = builder.frozen.row(f"nouns.csv:{bare}")
        if "," in row["sg_prep"] or not row["sg_prep"]:
            raise SystemExit(f"{bare} prepositional field is not a single source form")
        shipped = builder.lexical(f"prov-u5-prep-field-{bare}", 5, "obj-u5-prepositional", f"nouns.csv:{bare}", "sg_prep")
        nominative = format_stress(row["sg_nom"])
        builder.add_provenance(
            f"prov-u5-nom-field-{bare}",
            5,
            "obj-u5-prepositional",
            "lexical record",
            OPENRUSSIAN_ID,
            builder.openrussian_url + "/nouns.csv",
            builder.openrussian_revision,
            f"nouns.csv:{bare}#sg_nom",
            row["sg_nom"],
            nominative,
            "formatting" if nominative != row["sg_nom"] else "selection",
            builder.openrussian_attr,
        )
        records.append({"id": "field-" + bare, "shipped": shipped, "nominative": nominative, "bare": bare, "field": True})
    for bare in ["в", "на", "о"]:
        shipped = builder.lexical(f"prov-u5-prep-word-{bare}", 5, "obj-u5-prepositional", f"others.csv:{bare}", "accented")
        gloss = builder.gloss(f"prov-u5-prep-gloss-{bare}", 5, "obj-u5-prepositional", f"others.csv:{bare}")
        records.append({"id": "prep-" + bare, "shipped": shipped, "gloss": gloss, "preposition": True})
    return records


def _indeclinable(builder: Builder) -> list[dict]:
    builder.wiki_quote(
        "prov-u5-indecl-rule",
        5,
        "obj-u5-indeclinable",
        "nominative",
        INDECLINABLE_SENTENCE,
        INDECLINABLE_SENTENCE,
        "explanation",
    )
    builder.wiki_quote("prov-u5-indecl-word", 5, "obj-u5-indeclinable", "nominative", INDECLINABLE_SENTENCE, "indeclinable", "reference")
    builder.frozen.require("nominative", "'''радио''' means 'radio' or 'radios', and '''такси''' is both 'taxi' and 'taxis'")
    records = []
    for bare in ["радио", "такси", "метро", "кино", "пальто", "кофе", "кафе", "меню"]:
        row = builder.frozen.row(f"nouns.csv:{bare}")
        if row["indeclinable"] != "1":
            raise SystemExit(f"{bare} is not flagged indeclinable")
        shipped = builder.lexical(f"prov-u5-indecl-{bare}", 5, "obj-u5-indeclinable", f"nouns.csv:{bare}", "accented")
        gloss = builder.gloss(f"prov-u5-indecl-gloss-{bare}", 5, "obj-u5-indeclinable", f"nouns.csv:{bare}")
        flag = builder.flag(f"prov-u5-flag-{bare}", 5, "obj-u5-indeclinable", f"nouns.csv:{bare}")
        records.append({"bare": bare, "shipped": shipped, "gloss": gloss, "flag": flag})
    return records


def _check(builder: Builder, item_id: str, prompt: str, prompt_letter: str, options: list[str], answer: str, feedback: str, objective: str, source_id: str, kind: str) -> str:
    builder.item(item_id, prompt, prompt_letter, options, answer, feedback, objective, source_id, kind)
    return item_id


def _unit_3(builder: Builder, nouns: list[dict], phrases: list[dict]) -> dict:
    objective = "obj-u3-gender"
    labels = {label: label for label in ("masculine", "feminine", "neuter")}
    by_gender = {label: [noun for noun in nouns if noun["gender"] == label and not noun.get("exception")] for label in labels}
    exceptions = [noun for noun in nouns if noun.get("exception")]
    for noun in by_gender["masculine"][:4] + by_gender["feminine"][:4] + by_gender["neuter"][:4] + exceptions:
        builder.example(f"ex-u3-{noun['bare']}", noun["shipped"], noun["gloss"], objective, noun["source"])
    gender_options = ["masculine", "feminine", "neuter"]
    guided_bares = {"стол", "дом", "книга", "окно", "мужчина"}
    independent_bares = {"город", "дверь", "море", "друг", "дядя"}
    guided, independent = [], []
    for noun in nouns:
        if noun["bare"] not in guided_bares and noun["bare"] not in independent_bares:
            continue
        item_id = f"u3-i-gender-{noun['bare']}"
        _check(
            builder,
            item_id,
            builder.say(3, "Which admitted label is recorded for the shown word?"),
            noun["shipped"],
            gender_options,
            noun["gender"],
            builder.say(3, "The admitted record lists this as ") + noun["gender"] + builder.say(3, "."),
            objective,
            noun["source"],
            "SOURCE_GENDER",
        )
        (guided if noun["bare"] in guided_bares else independent).append(item_id)
    phrase_objective = "obj-u3-phrases"
    naming_objective = "obj-u3-naming"
    for phrase in phrases:
        target = phrase.get("objective", phrase_objective)
        builder.example(f"ex-u3-phrase-{phrase['id']}", phrase["shipped"], phrase["gloss"], target, phrase["source"])
    phrase_checks = [phrase for phrase in phrases if phrase["id"] in {"здравствуйте", "спасибо", "пожалуйста", "да", "нет", "это"}]
    gloss_options = [phrase["gloss"] for phrase in phrase_checks]
    phrase_items = []
    for phrase in phrase_checks:
        item_id = f"u3-i-phrase-{phrase['id']}"
        _check(
            builder,
            item_id,
            builder.say(3, "Which admitted gloss is recorded for the shown item?"),
            phrase["shipped"],
            gloss_options,
            phrase["gloss"],
            builder.say(3, "The frozen dictionary row records this gloss: ") + phrase["gloss"] + builder.say(3, "."),
            phrase_objective,
            phrase["source"],
            "SOURCE_MEANING",
        )
        phrase_items.append(item_id)
    naming = [phrase for phrase in phrases if phrase["id"] in {"moy-stol", "moya-sobaka", "chto-eto", "kto-eto"}]
    naming_options = [phrase["gloss"] for phrase in naming]
    naming_items = []
    for phrase in naming:
        item_id = f"u3-i-name-{phrase['id']}"
        _check(
            builder,
            item_id,
            builder.say(3, "Which admitted meaning is recorded for the shown item?"),
            phrase["shipped"],
            naming_options,
            phrase["gloss"],
            builder.say(3, "The frozen page records this meaning: ") + phrase["gloss"] + builder.say(3, "."),
            naming_objective,
            phrase["source"],
            "SOURCE_MEANING",
        )
        naming_items.append(item_id)

    def noun_card(title: str, chosen: list[dict], label: str, extra: str = "") -> dict:
        body = builder.say(3, "These admitted records are listed as ") + label + builder.say(3, ". ") + extra
        body += "".join(_entry(noun["shipped"], noun["gloss"]) for noun in chosen)
        return {
            "title": builder.say(3, title),
            "body": body,
            "examples": [f"ex-u3-{noun['bare']}" for noun in chosen],
            "guided": [f"u3-i-gender-{noun['bare']}" for noun in chosen if noun["bare"] in guided_bares],
        }

    masculine = by_gender["masculine"][:4]
    feminine = by_gender["feminine"][:4]
    neuter = by_gender["neuter"][:4]
    sessions, objectives = _chain(
        builder,
        3,
        [
            _objective_spec(
                objective,
                "Recognize admitted noun genders",
                [
                    {
                        "title": builder.say(3, "Read the admitted grouping sentence"),
                        "body": builder.say(3, "The frozen page says: ") + GENDER_SENTENCE + builder.say(3, " An ending is not a substitute for the admitted record."),
                        "examples": [],
                        "guided": [],
                    },
                    noun_card("Four records in the first group", masculine, "masculine"),
                    noun_card("Four records in the second group", feminine, "feminine"),
                    noun_card("Four records in the third group", neuter, "neuter"),
                    noun_card(
                        "Two records the page still places in the first group",
                        exceptions,
                        "masculine",
                        builder.say(3, "The frozen page says ") + DYADYA_SENTENCE + builder.say(3, ". "),
                    ),
                ],
                independent,
            ),
            _objective_spec(
                phrase_objective,
                "Recognize admitted everyday phrases",
                [
                    _text_card(builder, 3, "Three short greetings", "These greetings are copied from admitted rows. ", phrase_checks[:3], "ex-u3-phrase-", phrase_items[:2]),
                    _text_card(builder, 3, "Three short answers", "These short answers are copied from admitted rows. ", phrase_checks[3:], "ex-u3-phrase-", []),
                    _text_card(
                        builder,
                        3,
                        "Two written questions from the frozen page",
                        "These written questions are exact frozen excerpts. ",
                        [phrase for phrase in phrases if phrase["id"] in {"kak-dela", "gde-vy"}],
                        "ex-u3-phrase-",
                        [],
                    ),
                ],
                phrase_items[2:],
            ),
            _objective_spec(
                naming_objective,
                "Recognize admitted naming items",
                [
                    _text_card(builder, 3, "Two naming items from the frozen page", "These two items are exact frozen excerpts. Do not substitute another word into them. ", naming[:2], "ex-u3-phrase-", naming_items[:1]),
                    _text_card(builder, 3, "Two naming questions from the frozen page", "These two questions are exact frozen excerpts. ", naming[2:], "ex-u3-phrase-", []),
                ],
                naming_items[1:],
            ),
        ],
        "Everyday phrases, naming, and grouping",
        "Proposed Unit 3 uses frozen dictionary rows and grammar excerpts for the groups, a small phrase set, and naming items. The owned book remains separate work. Qualified review is pending.",
    )
    if len(independent) + len(phrase_items[2:]) + len(naming_items[1:]) < 12:
        raise SystemExit("Unit 3 has fewer than 12 independent checks")
    return {"number": 3, "sessions": sessions, "objectives": objectives, "minimum_checks": 12}


def _text_card(builder: Builder, unit: int, title: str, lead: str, rows: list[dict], example_prefix: str, guided: list[str]) -> dict:
    body = builder.say(unit, lead) + "".join(_entry(row["shipped"], row["gloss"]) for row in rows)
    return {
        "title": builder.say(unit, title),
        "body": body,
        "examples": [example_prefix + (row["id"] if "id" in row else row["bare"]) for row in rows],
        "guided": guided,
    }


def _unit_4(builder: Builder, pronouns: list[dict], verbs: list[dict]) -> dict:
    pronoun_objective = "obj-u4-pronouns"
    for pronoun in pronouns:
        builder.example(f"ex-u4-{pronoun['bare']}", pronoun["shipped"], pronoun["gloss"], pronoun_objective, OPENRUSSIAN_ID)
    glosses = [pronoun["gloss"] for pronoun in pronouns]
    pronoun_items = []
    for pronoun in pronouns:
        item_id = f"u4-i-pronoun-{pronoun['bare']}"
        _check(
            builder,
            item_id,
            builder.say(4, "Which admitted gloss is recorded for the shown personal word?"),
            pronoun["shipped"],
            glosses,
            pronoun["gloss"],
            builder.say(4, "The frozen dictionary row records this gloss: ") + pronoun["gloss"] + builder.say(4, "."),
            pronoun_objective,
            OPENRUSSIAN_ID,
            "SOURCE_MEANING",
        )
        pronoun_items.append(item_id)
    by_bare = {pronoun["bare"]: pronoun for pronoun in pronouns}
    present_objective = "obj-u4-present"
    for verb in verbs:
        if verb["bare"] in {"читать", "делать", "знать", "работать"}:
            builder.example(f"ex-u4-verb-{verb['bare']}", verb["infinitive"], verb["gloss"], present_objective, OPENRUSSIAN_ID)
    verb_items = []
    checked = [("читать", "ты"), ("делать", "я"), ("говорить", "мы"), ("видеть", "он"), ("писать", "вы"), ("идти", "они")]
    for verb, pronoun in checked:
        record = next(item for item in verbs if item["bare"] == verb)
        answer = record["forms"][pronoun]
        distractors = [form for form in record["forms"].values() if form != answer][:3]
        item_id = f"u4-i-verb-{verb}-{pronoun}"
        _check(
            builder,
            item_id,
            builder.say(4, "Which admitted present form is recorded for the row glossed ") + by_bare[pronoun]["gloss"] + "?",
            record["infinitive"],
            [answer, *distractors],
            answer,
            builder.say(4, "The frozen row records ") + answer + builder.say(4, " for the row glossed ") + by_bare[pronoun]["gloss"] + builder.say(4, "."),
            present_objective,
            OPENRUSSIAN_ID,
            "SOURCE_FORM",
        )
        verb_items.append(item_id)
    read = next(verb for verb in verbs if verb["bare"] == "читать")
    do = next(verb for verb in verbs if verb["bare"] == "делать")
    stress_objective = "obj-u4-stress"
    for digit in ("1", "2", "3", "4"):
        builder.say(4, digit)
    stress_words = [verbs[0]["infinitive"], verbs[7]["infinitive"], verbs[10]["infinitive"]]
    for word in stress_words:
        builder.example(
            f"ex-u4-stress-{sha256_text(word)[:8]}",
            word,
            builder.say(4, "Written mark from the admitted row"),
            stress_objective,
            OPENRUSSIAN_ID,
        )
    stress_items = []
    for word in stress_words:
        position = str(stress_position(word))
        item_id = f"u4-i-stress-{sha256_text(word)[:8]}"
        _check(
            builder,
            item_id,
            builder.say(4, "Which vowel carries the admitted written stress mark? Count vowels from the left."),
            word,
            ["1", "2", "3", "4"],
            position,
            builder.say(4, "The frozen accented field places the written mark on vowel ") + position + builder.say(4, "."),
            stress_objective,
            OPENRUSSIAN_ID,
            "SOURCE_STRESS",
        )
        stress_items.append(item_id)
    nominative_objective = "obj-u4-nominative"
    nominative_examples = [
        ("muzh", "nominative", "The husband is talking - '''Муж говорит'''", "Муж говорит", "The husband is talking"),
        ("poezd", "nominative", "The train is old - '''Поезд старый'''", "Поезд старый", "The train is old"),
    ]
    nominative_items = []
    for item_id, page, needle, shipped, gloss in nominative_examples:
        builder.wiki_quote(f"prov-u4-nom-{item_id}", 4, nominative_objective, page, needle, shipped, "example")
        builder.wiki_quote(f"prov-u4-nom-{item_id}-en", 4, nominative_objective, page, needle, gloss, "reference")
        builder.example(f"ex-u4-nom-{item_id}", shipped, gloss, nominative_objective, WIKIBOOKS_ID)
    builder.wiki_quote("prov-u4-cases-six", 4, nominative_objective, "cases", CASES_SIX, CASES_SIX, "explanation")
    builder.wiki_quote("prov-u4-cases-default", 4, nominative_objective, "cases", CASES_DEFAULT, CASES_DEFAULT, "explanation")
    for item_id, _page, _needle, shipped, gloss in nominative_examples:
        check_id = f"u4-i-nom-{item_id}"
        _check(
            builder,
            check_id,
            builder.say(4, "Which admitted example is the shown sentence?"),
            shipped,
            [row[4] for row in nominative_examples],
            gloss,
            builder.say(4, "The frozen page records this example as: ") + gloss + builder.say(4, "."),
            nominative_objective,
            WIKIBOOKS_ID,
            "SOURCE_CASE",
        )
        nominative_items.append(check_id)
    infinitive_groups = [
        [verb for verb in verbs if verb["bare"] in {"знать", "работать", "понима'ть", "слушать"} or verb["bare"] in {"знать", "работать", "понимать", "слушать"}],
        [verb for verb in verbs if verb["bare"] in {"жить", "говорить", "видеть", "смотреть"}],
        [verb for verb in verbs if verb["bare"] in {"писать", "идти"}],
    ]
    # понимать bare is понимать without accent in the row bare field.
    sessions, objectives = _chain(
        builder,
        4,
        [
            _objective_spec(
                pronoun_objective,
                "Recognize admitted personal pronouns",
                [
                    _rows_card(builder, 4, "Two speaker rows", "The admitted dictionary rows supply this pair. ", pronouns[:2], "ex-u4-", pronoun_items[:2]),
                    _rows_card(builder, 4, "Two addressee rows", "The admitted dictionary rows supply this pair. ", pronouns[2:4], "ex-u4-", []),
                    _rows_card(builder, 4, "Four other-person rows", "The admitted dictionary rows supply this group. ", pronouns[4:], "ex-u4-", []),
                ],
                pronoun_items[2:],
            ),
            _objective_spec(
                present_objective,
                "Recognize admitted present-tense forms",
                [
                    {
                        "title": builder.say(4, "How the frozen page forms the present"),
                        "body": builder.say(4, "The frozen page says: ") + PRESENT_SENTENCE,
                        "examples": [],
                        "guided": [],
                    },
                    _form_card(builder, read, ["я", "ты", "он"], by_bare, [verb_items[0]]),
                    _form_card(builder, read, ["мы", "вы", "они"], by_bare, []),
                    {
                        "title": builder.say(4, "One admitted row for a second infinitive"),
                        "body": _entry(do["infinitive"], do["gloss"]) + builder.say(4, "The admitted row records ") + do["forms"]["я"] + builder.say(4, " for the row glossed ") + by_bare["я"]["gloss"] + builder.say(4, "."),
                        "examples": ["ex-u4-verb-делать"],
                        "guided": [verb_items[1]],
                    },
                    _infinitive_card(builder, "Four more admitted infinitives", infinitive_groups[0]),
                    _infinitive_card(builder, "Four further admitted infinitives", infinitive_groups[1]),
                    _infinitive_card(builder, "Two further admitted infinitives", infinitive_groups[2]),
                ],
                verb_items[2:],
            ),
            _objective_spec(
                stress_objective,
                "Notice the admitted written stress mark",
                [
                    {
                        "title": builder.say(4, "The admitted written mark"),
                        "body": builder.say(4, "The mark in the admitted form is written. Listening and speaking stay with the book. No audio is mapped to these forms. ") + " ".join(word + "." for word in stress_words),
                        "examples": [f"ex-u4-stress-{sha256_text(word)[:8]}" for word in stress_words],
                        "guided": [stress_items[0]],
                    }
                ],
                stress_items[1:],
            ),
            _objective_spec(
                nominative_objective,
                "Recognize the admitted nominative role",
                [
                    {
                        "title": builder.say(4, "The count named on the frozen page"),
                        "body": builder.say(4, "The frozen page says: ") + CASES_SIX + builder.say(4, "."),
                        "examples": [],
                        "guided": [],
                    },
                    {
                        "title": builder.say(4, "The default form on the frozen page"),
                        "body": builder.say(4, "The frozen page says: ") + CASES_DEFAULT,
                        "examples": [],
                        "guided": [],
                    },
                    _text_card(builder, 4, "Two admitted examples", "These examples are exact frozen excerpts. ", [{"id": row[0], "shipped": row[3], "gloss": row[4]} for row in nominative_examples], "ex-u4-nom-", []),
                ],
                nominative_items,
            ),
        ],
        "Person words, present forms, a written mark, and dictionary-form examples",
        "Proposed Unit 4 uses frozen rows for the person words, the present forms, the written mark, and the dictionary-form examples. The written mark is not a listening score. The owned book remains separate work. Qualified review is pending.",
    )
    independent = pronoun_items[2:] + verb_items[2:] + stress_items[1:] + nominative_items
    if len(independent) < 14:
        raise SystemExit(f"Unit 4 has {len(independent)} independent checks")
    return {"number": 4, "sessions": sessions, "objectives": objectives, "minimum_checks": 14}


def _rows_card(builder: Builder, unit: int, title: str, lead: str, rows: list[dict], example_prefix: str, guided: list[str]) -> dict:
    return _text_card(builder, unit, title, lead, rows, example_prefix, guided)


def _form_card(builder: Builder, verb: dict, persons: list[str], pronouns: dict, guided: list[str]) -> dict:
    body = builder.say(4, "These fields are copied from the admitted ") + verb["infinitive"] + builder.say(4, " row. ")
    for person in persons:
        body += builder.say(4, "Row glossed ") + pronouns[person]["gloss"] + builder.say(4, ": ") + verb["forms"][person] + builder.say(4, ". ")
    return {
        "title": builder.say(4, "Admitted fields from one row"),
        "body": body,
        "examples": ["ex-u4-verb-читать"] if "я" in persons else [],
        "guided": guided,
    }


def _infinitive_card(builder: Builder, title: str, verbs: list[dict]) -> dict:
    if not verbs:
        raise SystemExit(f"Empty infinitive card: {title}")
    return {
        "title": builder.say(4, title),
        "body": builder.say(4, "These infinitives are copied from admitted rows. ") + "".join(_entry(verb["infinitive"], verb["gloss"]) for verb in verbs),
        "examples": [],
        "guided": [],
    }


def _unit_5(builder: Builder, questions: list[dict], prepositional: list[dict], indeclinable: list[dict]) -> dict:
    question_objective = "obj-u5-questions"
    words = [question for question in questions if not question.get("pattern")]
    patterns = [question for question in questions if question.get("pattern")]
    for word in words:
        builder.example(f"ex-u5-q-{word['id']}", word["shipped"], word["gloss"], question_objective, WIKIBOOKS_ID)
    for pattern in patterns[:4]:
        builder.example(f"ex-u5-pattern-{pattern['id']}", pattern["shipped"], pattern["gloss"], question_objective, WIKIBOOKS_ID)
    word_options = [question["shipped"] for question in words[:6]]
    question_items = []
    for word in words[:6]:
        item_id = f"u5-i-q-{word['id']}"
        _check(
            builder,
            item_id,
            builder.say(5, "Which admitted question word is recorded for this English purpose: ") + word["gloss"] + "?",
            word["shipped"],
            word_options,
            word["shipped"],
            builder.say(5, "The frozen table records ") + word["shipped"] + builder.say(5, " for ") + word["gloss"] + builder.say(5, "."),
            question_objective,
            WIKIBOOKS_ID,
            "SOURCE_QUESTION",
        )
        question_items.append(item_id)
    pattern_options = [pattern["gloss"] for pattern in patterns[:4]]
    for pattern in patterns[:4]:
        item_id = f"u5-i-pattern-{pattern['id']}"
        _check(
            builder,
            item_id,
            builder.say(5, "Which admitted meaning is recorded for the shown written question?"),
            pattern["shipped"],
            pattern_options,
            pattern["gloss"],
            builder.say(5, "The frozen page records this meaning: ") + pattern["gloss"] + builder.say(5, ". Written punctuation is not listening practice."),
            question_objective,
            WIKIBOOKS_ID,
            "SOURCE_QUESTION",
        )
        question_items.append(item_id)
    prep_objective = "obj-u5-prepositional"
    sentences = [row for row in prepositional if not row.get("field") and not row.get("preposition")]
    for sentence in sentences:
        builder.example(f"ex-u5-prep-{sentence['id']}", sentence["shipped"], sentence["gloss"], prep_objective, WIKIBOOKS_ID)
    prep_items = []
    sentence_options = [sentence["gloss"] for sentence in sentences[:4]]
    for sentence in sentences[:4]:
        item_id = f"u5-i-prep-{sentence['id']}"
        _check(
            builder,
            item_id,
            builder.say(5, "Which admitted description is recorded for the shown example?"),
            sentence["shipped"],
            sentence_options,
            sentence["gloss"],
            builder.say(5, "The frozen page records this example as: ") + sentence["gloss"] + builder.say(5, "."),
            prep_objective,
            WIKIBOOKS_ID,
            "SOURCE_CASE",
        )
        prep_items.append(item_id)
    for field in [row for row in prepositional if row.get("field")][:2]:
        options = [field["nominative"], field["shipped"]]
        if options[0] == options[1]:
            raise SystemExit("Nominative and prepositional forms are identical")
        item_id = f"u5-i-prep-field-{field['bare']}"
        _check(
            builder,
            item_id,
            builder.say(5, "Which string is the admitted second field, not the first field?"),
            field["nominative"],
            options,
            field["shipped"],
            builder.say(5, "The frozen row lists that field as ") + field["shipped"] + builder.say(5, "."),
            prep_objective,
            OPENRUSSIAN_ID,
            "SOURCE_FORM",
        )
        prep_items.append(item_id)
    indecl_objective = "obj-u5-indeclinable"
    for noun in indeclinable:
        builder.example(f"ex-u5-indecl-{noun['bare']}", noun["shipped"], noun["gloss"], indecl_objective, OPENRUSSIAN_ID)
    contrast_bares = ["книга", "окно"]
    contrast_flags = {}
    for bare in contrast_bares:
        builder.lexical(f"prov-u5-contrast-{bare}", 5, indecl_objective, f"nouns.csv:{bare}", "accented")
        contrast_flags[bare] = builder.flag(f"prov-u5-contrast-flag-{bare}", 5, indecl_objective, f"nouns.csv:{bare}")
    indecl_items = []
    for noun in indeclinable[:4]:
        item_id = f"u5-i-indecl-{noun['bare']}"
        _check(
            builder,
            item_id,
            builder.say(5, "Which flag does the admitted row record?"),
            noun["shipped"],
            ["1", "0"],
            noun["flag"],
            builder.say(5, "The frozen row sets the flag to ") + noun["flag"] + builder.say(5, "."),
            indecl_objective,
            OPENRUSSIAN_ID,
            "SOURCE_INDECLINABLE",
        )
        indecl_items.append(item_id)
    for bare in contrast_bares:
        shipped = format_stress(builder.frozen.row(f"nouns.csv:{bare}")["accented"])
        item_id = f"u5-i-decl-{bare}"
        _check(
            builder,
            item_id,
            builder.say(5, "Which flag does the admitted row record?"),
            shipped,
            ["1", "0"],
            contrast_flags[bare],
            builder.say(5, "The frozen row sets the flag to ") + contrast_flags[bare] + builder.say(5, "."),
            indecl_objective,
            OPENRUSSIAN_ID,
            "SOURCE_INDECLINABLE",
        )
        indecl_items.append(item_id)
    prepositions = [row for row in prepositional if row.get("preposition")]
    fields = [row for row in prepositional if row.get("field")]
    sessions, objectives = _chain(
        builder,
        5,
        [
            _objective_spec(
                question_objective,
                "Recognize admitted question words and written questions",
                [
                    _text_card(builder, 5, "Four question words", "These words are copied from the frozen table. ", words[:4], "ex-u5-q-", question_items[:2]),
                    _text_card(builder, 5, "Three more question words", "These words are copied from the frozen table. ", words[4:7], "ex-u5-q-", []),
                    _text_card(builder, 5, "Three further question words", "These words are copied from the frozen table. ", words[7:], "ex-u5-q-", []),
                    _text_card(builder, 5, "Two written questions", "These written questions are exact frozen excerpts. ", patterns[:2], "ex-u5-pattern-", [question_items[6]]),
                    _text_card(builder, 5, "Two more written questions", "These written questions are exact frozen excerpts. ", patterns[2:4], "ex-u5-pattern-", []),
                ],
                question_items[2:6] + question_items[7:],
            ),
            _objective_spec(
                prep_objective,
                "Recognize admitted prepositional examples",
                [
                    _text_card(builder, 5, "Two about-examples", "These examples are exact frozen excerpts. ", sentences[:2], "ex-u5-prep-", prep_items[:2]),
                    _text_card(builder, 5, "Two more about-examples and a place example", "These examples are exact frozen excerpts. ", sentences[2:4], "ex-u5-prep-", []),
                    _text_card(builder, 5, "Two place examples", "These examples are exact frozen excerpts. ", sentences[4:6], "ex-u5-prep-", []),
                    _text_card(builder, 5, "Two more place examples", "These examples are exact frozen excerpts. ", sentences[6:], "ex-u5-prep-", []),
                    {
                        "title": builder.say(5, "Three admitted function words"),
                        "body": builder.say(5, "Use these rows only as quoted. Do not build a new phrase from them. ") + "".join(_entry(row["shipped"], row["gloss"]) for row in prepositions),
                        "examples": [],
                        "guided": [],
                    },
                    {
                        "title": builder.say(5, "Two admitted field pairs"),
                        "body": builder.say(5, "Each pair is the first field, then the second field, from the same admitted row. ") + "".join(field["nominative"] + " / " + field["shipped"] + ". " for field in fields[:2]),
                        "examples": [],
                        "guided": prep_items[4:6],
                    },
                ],
                prep_items[2:4],
            ),
            _objective_spec(
                indecl_objective,
                "Recognize admitted indeclinable nouns",
                [
                    {
                        "title": builder.say(5, "What the frozen page says about words that do not change"),
                        "body": builder.say(5, "The frozen page says ") + INDECLINABLE_SENTENCE + builder.say(5, ". ") + _entry(indeclinable[0]["shipped"], indeclinable[0]["gloss"]) + _entry(indeclinable[1]["shipped"], indeclinable[1]["gloss"]),
                        "examples": [f"ex-u5-indecl-{indeclinable[0]['bare']}", f"ex-u5-indecl-{indeclinable[1]['bare']}"],
                        "guided": [indecl_items[0]],
                    },
                    _text_card(builder, 5, "Four admitted rows with the flag set", "The admitted rows below have the flag set. That flag is the record used here. ", indeclinable[2:6], "ex-u5-indecl-", []),
                    _text_card(builder, 5, "Two more admitted rows with the flag set", "The admitted rows below have the flag set. ", indeclinable[6:], "ex-u5-indecl-", []),
                ],
                indecl_items[1:],
            ),
        ],
        "Questions, place examples, and words that do not change form",
        "Proposed Unit 5 uses frozen grammar excerpts and dictionary rows for questions, place examples, and words that do not change form. It does not teach question intonation and it does not supply audio. The owned book remains separate work. Qualified review is pending.",
    )
    independent = question_items[2:6] + question_items[7:] + prep_items[2:4] + indecl_items[1:]
    if len(independent) < 14:
        raise SystemExit(f"Unit 5 has {len(independent)} independent checks")
    return {"number": 5, "sessions": sessions, "objectives": objectives, "minimum_checks": 14}


def _objective_spec(objective: str, title: str, cards: list[dict], checks: list[str]) -> dict:
    examples: list[str] = []
    for card in cards:
        for example_id in card["examples"]:
            if example_id not in examples:
                examples.append(example_id)
    if len(examples) < 2:
        raise SystemExit(f"{objective} has fewer than two examples")
    if not checks:
        raise SystemExit(f"{objective} has no independent checks")
    return {"id": objective, "title": title, "cards": cards, "checks": checks, "examples": examples}


def _chain(builder: Builder, number: int, specs: list[dict], orient_title: str, orient_body: str) -> tuple[list[dict], list[dict]]:
    sessions = [
        {
            "id": f"u{number}-s-orient",
            "title": orient_title,
            "estimatedMinutes": 8,
            "objectiveIds": [spec["id"] for spec in specs] + [f"obj-u{number}-book"],
            "steps": [
                {
                    "id": f"u{number}-st-orient",
                    "kind": "ORIENT",
                    "title": builder.say(number, orient_title),
                    "body": builder.say(number, orient_body),
                    "dominantAction": "Continue",
                    "referenceIds": ["ref-u35-attribution"],
                    "itemIds": [],
                    "exampleIds": [],
                    "unavailableReason": None,
                    "bookDirection": builder.say(number, f"Open Lesson {number} in the copy you own. The app records do not replace that lesson."),
                    "companionDirection": None,
                    "remediationStepId": None,
                }
            ],
        }
    ]
    objectives = []
    for spec in specs:
        slug = spec["id"].split("-")[-1]
        remediation = f"u{number}-st-rem-{slug}"
        steps = []
        teach_ids = []
        guided_ids = []
        for index, card in enumerate(spec["cards"], start=1):
            teach = f"u{number}-st-teach-{slug}-{index}"
            teach_ids.append(teach)
            steps.append(_step(teach, "TEACH", card["title"], card["body"], example_ids=card["examples"], reference_ids=["ref-u35-attribution"]))
            if card["guided"]:
                guided_id = f"u{number}-st-guided-{slug}-{index}"
                guided_ids.append(guided_id)
                steps.append(
                    _step(
                        guided_id,
                        "GUIDED",
                        builder.say(number, "Guided practice"),
                        builder.say(number, "Choose the admitted record. The feedback states the frozen field."),
                        item_ids=card["guided"],
                        remediation=remediation,
                    )
                )
        steps.append(
            _step(
                remediation,
                "REMEDIATE",
                builder.say(number, "Remediation"),
                builder.say(number, "Return to the explanation. Compare the shown form with the admitted record. Do not invent a form that is absent from the record."),
                example_ids=spec["examples"][:2],
            )
        )
        check_id = f"u{number}-st-check-{slug}"
        steps.append(
            _step(
                check_id,
                "CHECK",
                builder.say(number, "Independent checks"),
                builder.say(number, "These checks use only admitted records. A miss returns to the explanation."),
                item_ids=spec["checks"],
                remediation=remediation,
            )
        )
        sessions.append(
            {
                "id": f"u{number}-s-{slug}",
                "title": spec["title"],
                "estimatedMinutes": 12,
                "objectiveIds": [spec["id"]],
                "steps": steps,
            }
        )
        objectives.append(
            {
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
        )
    book_steps = [
        _step(
            f"u{number}-st-book-work",
            "BOOK",
            builder.say(number, f"Work Lesson {number} in your copy"),
            builder.say(number, f"Do Lesson {number} in the copy you own. Use that book's explanations and practice. The proposed records in this unit are a separate licensed set and do not copy the book."),
            book=builder.say(number, f"Open Lesson {number} in your copy and complete its lesson work."),
            reference_ids=["ref-book-topics", "ref-u35-attribution"],
        ),
        _step(
            f"u{number}-st-book",
            "BOOK",
            builder.say(number, f"Record Lesson {number} book work"),
            builder.say(number, f"When you have done Lesson {number} in your copy, self-attest here. Attestation records your report. It does not grade the lesson, approve a mapping, or approve the proposed records."),
            book=builder.say(number, f"Self-attest only after the Lesson {number} work in your copy."),
        ),
    ]
    sessions.append(
        {
            "id": f"u{number}-s-book",
            "title": f"Self-attest Lesson {number}",
            "estimatedMinutes": 10,
            "objectiveIds": [f"obj-u{number}-book"],
            "steps": book_steps,
        }
    )
    objectives.append(
        {
            "id": f"obj-u{number}-book",
            "unitNumber": number,
            "title": f"Self-attest Lesson {number} book work",
            "kind": "SELF_ATTESTATION",
            "instructionStepIds": [f"u{number}-st-book-work", f"u{number}-st-book"],
            "exampleIds": [],
            "practiceStepIds": [],
            "checkItemIds": [],
            "remediationStepIds": [],
        }
    )
    return sessions, objectives


def _step(step_id: str, kind: str, title: str, body: str, item_ids: list[str] | None = None, example_ids: list[str] | None = None, remediation: str | None = None, book: str | None = None, reference_ids: list[str] | None = None) -> dict:
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


def _attribution_reference() -> dict:
    return {
        "id": "ref-u35-attribution",
        "title": "Units 3–5 source notice",
        "summary": "Proposed Units 3–5 adapt CC BY-SA 4.0 dictionary rows and Wikibooks grammar excerpts.",
        "detail": (
            "OpenRussian dictionary data is frozen at commit 50e210c4803237779cb562bc1abcea529066031c "
            "under CC BY-SA 4.0. Wikibooks Russian grammar excerpts are frozen by oldid under the CC BY-SA 4.0 route. "
            "The adapted selection and arrangement are share-alike. Application code, user data, and user recordings are outside that statement. "
            "No audio is included in this proposed curriculum. Review is pending. The packaged CC BY-SA 4.0 legal code is cc-by-sa-4.0.txt."
        ),
        "introducedInUnit": 3,
        "sourceId": WIKIBOOKS_ID,
        "classification": "LICENSE_OR_ATTRIBUTION",
        "reviewStatus": "NOT_REQUIRED",
        "searchTerms": ["units 3-5 source notice"],
    }


def _assert_coverage(builder: Builder) -> None:
    shipped = {record["shippedText"] for record in builder.provenance}
    hosts = []
    for example in builder.examples:
        hosts.append(example["display"])
        hosts.append(example["note"])
    for item in builder.items:
        hosts.extend([item["prompt"], item["promptLetter"], item["answer"], item["feedback"], *item["options"]])
    for record in builder.provenance:
        hosts.append(record["shippedText"])
    for host in hosts:
        for match in REGION.finditer(host):
            text = match.group(0).strip()
            trimmed = text.rstrip("?!.,")
            if text not in shipped and trimmed not in shipped:
                raise SystemExit(f"Unsourced Russian region: {text}")


def _assert_unit_text(units: list[dict], provenance: list[dict]) -> None:
    shipped_by_unit: dict[int, set[str]] = {}
    for record in provenance:
        shipped_by_unit.setdefault(record["unitNumber"], set()).add(record["shippedText"])
    for unit in units:
        shipped = shipped_by_unit.get(unit["number"], set())
        hosts: list[str] = []
        for session in unit["sessions"]:
            for step in session["steps"]:
                hosts.extend([step["title"], step["body"], step.get("bookDirection") or ""])
        for host in hosts:
            for match in REGION.finditer(host):
                text = match.group(0).strip()
                trimmed = text.rstrip("?!.,")
                if text not in shipped and trimmed not in shipped:
                    raise SystemExit(f"Unit {unit['number']} unsourced Russian region: {text}")


def _assert_english(builder: Builder, units: list[dict]) -> None:
    for unit in units:
        number = unit["number"]
        spans = [record["shippedText"] for record in builder.provenance if record["unitNumber"] == number]
        spans += [record["text"] for record in builder.authored if record["unitNumber"] == number]
        allowed = set(spans)
        hosts: list[str] = []
        for session in unit["sessions"]:
            for step in session["steps"]:
                hosts.extend([step["title"], step["body"], step.get("bookDirection") or ""])
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
            leftover = unbound_letters(host, spans)
            if leftover:
                raise SystemExit(f"Unit {number} English linguistic claim has no admitted source record: {leftover} in {host!r}")
            for dirty in DIRTY_LEARNER_TEXT:
                if dirty in host:
                    raise SystemExit(f"Unit {number} ships a dirty gloss to the learner: {dirty}")


def _assert_cards(units: list[dict]) -> None:
    sessions = {session["id"]: session for unit in units for session in unit["sessions"]}
    required = {
        "u3-s-gender": 4,
        "u3-s-phrases": 2,
        "u3-s-naming": 2,
        "u4-s-pronouns": 3,
        "u4-s-present": 3,
        "u5-s-questions": 3,
        "u5-s-prepositional": 3,
        "u5-s-indeclinable": 2,
    }
    for session_id, minimum in required.items():
        teaches = [step for step in sessions[session_id]["steps"] if step["kind"] == "TEACH"]
        if len(teaches) < minimum:
            raise SystemExit(f"{session_id} has {len(teaches)} explanation cards")
        for step in teaches:
            if step["body"].count(" — ") > 4:
                raise SystemExit(f"{step['id']} lists too many examples")
        kinds = [step["kind"] for step in sessions[session_id]["steps"]]
        for index, kind in enumerate(kinds):
            if kind == "GUIDED" and kinds[index - 1] != "TEACH":
                raise SystemExit(f"{session_id} guided practice does not follow its explanation")
    first = next(step for step in sessions["u3-s-gender"]["steps"] if step["kind"] == "TEACH")
    if len(first["body"]) > 500 or "хлеб" in first["body"] or "молоко" in first["body"]:
        raise SystemExit("The first gender card is still a noun dump")


if __name__ == "__main__":
    built = build_units_3_5()
    print(
        f"provenance={len(built['provenance'])} authored={len(built['authored'])} "
        f"items={len(built['items'])} examples={len(built['examples'])} units={len(built['units'])}"
    )
