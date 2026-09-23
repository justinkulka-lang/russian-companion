#!/usr/bin/env python3
"""Build proposed Units 3–5 from frozen admitted sources only.

Russian strings are copied from the frozen OpenRussian rows or from exact
Wikibooks excerpts. This module does not conjugate, decline, or compose
new Russian sentences.
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
CYRILLIC = re.compile(r"[\u0400-\u04FF]")
VOWELS = set("аеёиоуыэюяАЕЁИОУЫЭЮЯ")

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
PRONOUN_GLOSS_EXPECTATION = {
    "я": "I",
    "мы": "we",
    "ты": "you",
    "вы": "you (plural); you (formal)",
    "он": "he; it (masculine inanimate nouns)",
    "она": "she, it, he, she, it, he, she",
    "оно": "it, he, she",
    "они": "they",
}
VERB_PERSON = [
    ("presfut_sg1", "я"),
    ("presfut_sg2", "ты"),
    ("presfut_sg3", "он"),
    ("presfut_pl1", "мы"),
    ("presfut_pl2", "вы"),
    ("presfut_pl3", "они"),
]


def sha256_text(text: str) -> str:
    return hashlib.sha256(text.encode("utf-8")).hexdigest()


def english(text: str) -> str:
    if CYRILLIC.search(text):
        raise SystemExit(f"English scaffold contains Cyrillic: {text!r}")
    return text


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


class FrozenSources:
    def __init__(self) -> None:
        self.freeze = json.loads(FREEZE_PATH.read_text(encoding="utf-8"))
        self.pages: dict[str, str] = {}
        for page in self.freeze["wikibooks"]["pages"]:
            path = SOURCE_ROOT / page["file"]
            text = path.read_text(encoding="utf-8")
            digest = hashlib.sha256(text.encode("utf-8")).hexdigest()
            if digest != page["sha256"]:
                raise SystemExit(f"Wiki snapshot hash mismatch for {page['file']}")
            self.pages[page["key"]] = text
        self.rows: dict[str, dict] = {}
        commit = self.freeze["openrussian"]["commit"]
        if len(commit) != 40:
            raise SystemExit("OpenRussian commit is unresolved")
        for row in self.freeze["openrussian"]["rows"]:
            fields = FIELDS[row["file"]]
            parts = row["line"].split("\t")
            if len(parts) != len(fields):
                raise SystemExit(f"Row {row['recordId']} has {len(parts)} fields")
            if sha256_text(row["line"]) != row["sha256"]:
                raise SystemExit(f"Row hash mismatch for {row['recordId']}")
            parsed = dict(zip(fields, parts))
            self.rows[row["recordId"]] = parsed
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
        self.examples: list[dict] = []
        self.items: list[dict] = []
        self.seen_shipped: set[str] = set()
        commit = frozen.freeze["openrussian"]["commit"]
        self.openrussian_url = f"https://github.com/Badestrand/russian-dictionary/tree/{commit}"
        self.openrussian_revision = commit
        self.openrussian_attr = (
            "Badestrand/russian-dictionary contributors, commit "
            f"{commit}, CC BY-SA 4.0."
        )
        self.wikibooks_attr = (
            "Wikibooks Russian grammar contributors, CC BY-SA 4.0. "
            "Page history for each frozen oldid identifies the contributors."
        )

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
        if CYRILLIC.search(shipped):
            self.seen_shipped.add(shipped)
        return record

    def wiki_url(self, page: str) -> str:
        titles = {
            "gender": "Russian/Grammar/Gender",
            "pronouns": "Russian/Grammar/Pronouns",
            "verbs": "Russian/Grammar/Verbs",
            "cases": "Russian/Grammar/Cases",
            "cases": "Russian/Grammar/Cases",
            "nominative": "Russian/Grammar/Nominative",
            "prepositional": "Russian/Grammar/Prepositional_case",
            "interrogative": "Russian/Interrogative_Pronouns",
            "prepositions": "Russian/Prepositions",
            "personal-pronouns": "Russian/Personal_Pronouns",
            "copyrights": "Wikibooks:Copyrights",
        }
        oldid = self.frozen.oldid(page)
        return f"https://en.wikibooks.org/wiki/{titles[page]}?oldid={oldid}"

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
        if field.startswith("presfut") or field in {"sg_nom", "sg_prep", "pl_nom", "accented"}:
            shipped = format_stress(original)
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
        if CYRILLIC.search(shipped) and shipped not in original and shipped.replace("\u0301", "") not in original.replace("'", ""):
            # Shipped Russian must occur in the excerpt after ignoring only the source's apostrophe stress mark.
            plain = shipped.replace("\u0301", "")
            if plain not in original:
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
    return {
        "sources": [
            {
                "id": OPENRUSSIAN_ID,
                "title": "OpenRussian dictionary data",
                "license": LICENSE_ID,
                "attribution": builder.openrussian_attr,
                "modification": "Selected rows and stress-mark formatting. No generated forms.",
            },
            {
                "id": WIKIBOOKS_ID,
                "title": "Wikibooks Russian grammar",
                "license": LICENSE_ID,
                "attribution": builder.wikibooks_attr,
                "modification": "Selected excerpts and short English adaptation. Russian quotations are unchanged.",
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
        "units": units,
    }


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
            records.append({"bare": bare, "shipped": shipped, "gloss": row["translations_en"], "gender": label, "source": OPENRUSSIAN_ID})
    builder.frozen.require("gender", "'''Мужчина''' - Man")
    row = builder.frozen.row("nouns.csv:мужчина")
    if row["gender"] != "m":
        raise SystemExit("мужчина is not masculine in the frozen row")
    shipped = builder.lexical("prov-u3-noun-мужчина", 3, "obj-u3-gender", "nouns.csv:мужчина", "accented")
    records.append({"bare": "мужчина", "shipped": shipped, "gloss": row["translations_en"], "gender": "masculine", "source": OPENRUSSIAN_ID, "exception": True})
    needle = "inherently' masculine, and so '''дядя''' is too"
    builder.wiki_quote(
        "prov-u3-dyadya-gender",
        3,
        "obj-u3-gender",
        "gender",
        needle,
        format_stress(builder.frozen.row("nouns.csv:дядя")["accented"]),
        "lexical record",
    )
    form = builder.lexical("prov-u3-noun-дядя", 3, "obj-u3-gender", "nouns.csv:дядя", "accented")
    records.append({"bare": "дядя", "shipped": form, "gloss": builder.frozen.row("nouns.csv:дядя")["translations_en"], "gender": "masculine", "source": WIKIBOOKS_ID, "exception": True})
    return records


def _phrases(builder: Builder) -> list[dict]:
    phrases = []
    for bare in ["здравствуйте", "спасибо", "пожалуйста", "да", "нет", "это"]:
        row = builder.frozen.row(f"others.csv:{bare}")
        shipped = builder.lexical(f"prov-u3-phrase-{bare}", 3, "obj-u3-phrases", f"others.csv:{bare}", "accented")
        phrases.append({"id": bare, "shipped": shipped, "gloss": row["translations_en"], "source": OPENRUSSIAN_ID})
    quoted = [
        ("moy-stol", "gender", "'''мой стол'''", "мой стол", "my table", "obj-u3-naming"),
        ("moya-sobaka", "gender", "'''моя собака'''", "моя собака", "my dog", "obj-u3-naming"),
        ("chto-eto", "interrogative", "'''Что это?'''", "Что это?", "What is this?", "obj-u3-naming"),
        ("kto-eto", "interrogative", "'''Кто это?'''", "Кто это?", "Who is this?", "obj-u3-naming"),
        ("kak-dela", "interrogative", "'''Как дела?'''", "Как дела?", "How do you do?/How're things?", "obj-u3-phrases"),
        ("gde-vy", "personal-pronouns", "'''Где вы?'''", "Где вы?", "Where are you?", "obj-u3-phrases"),
    ]
    for item_id, page, needle, shipped, gloss, objective in quoted:
        builder.wiki_quote(f"prov-u3-phrase-{item_id}", 3, objective, page, needle, shipped, "example")
        phrases.append({"id": item_id, "shipped": shipped, "gloss": gloss, "source": WIKIBOOKS_ID, "objective": objective})
    return phrases


def _pronouns(builder: Builder) -> list[dict]:
    builder.frozen.require("nominative", "Я ('I')")
    builder.frozen.require("nominative", "Мы ('We')")
    records = []
    for bare, gloss in PRONOUN_GLOSS_EXPECTATION.items():
        row = builder.frozen.row(f"others.csv:{bare}")
        if row["translations_en"] != gloss:
            raise SystemExit(f"Pronoun gloss changed for {bare}: {row['translations_en']!r}")
        shipped = builder.lexical(f"prov-u4-pronoun-{bare}", 4, "obj-u4-pronouns", f"others.csv:{bare}", "accented")
        records.append({"bare": bare, "shipped": shipped, "gloss": gloss})
    return records


def _verbs(builder: Builder) -> list[dict]:
    selected = [
        ("verbs.csv:чита'ть", "read"),
        ("verbs.csv:де'лать", "make, do"),
        ("verbs.csv:знать", "know, be aware, be acquainted, have a knowledge, aristocracy, nobility, the elite, evidently, it seems"),
        ("verbs.csv:рабо'тать", "work, operate"),
        ("verbs.csv:понима'ть", "understand, comprehend, realize"),
        ("verbs.csv:слу'шать", "listen, hear, attend, examine chest, auscultate, obey"),
        ("verbs.csv:жить", "live"),
        ("verbs.csv:говори'ть", None),
        ("verbs.csv:ви'деть", "see"),
        ("verbs.csv:смотре'ть", "look, watch"),
        ("verbs.csv:писа'ть", "write; paint"),
        ("verbs.csv:идти'", "go, walk"),
    ]
    builder.frozen.require("verbs", "to speak")
    builder.wiki_quote(
        "prov-u4-govorit-gloss",
        4,
        "obj-u4-present",
        "verbs",
        "to speak",
        "to speak",
        "reference",
    )
    records = []
    for record_id, gloss in selected:
        row = builder.frozen.row(record_id)
        if row["aspect"] != "imperfective":
            raise SystemExit(f"{record_id} is not imperfective")
        if gloss is not None and row["translations_en"] != gloss:
            raise SystemExit(f"Verb gloss changed for {record_id}")
        meaning = "to speak" if gloss is None else gloss
        infinitive = builder.lexical(f"prov-u4-verb-{row['bare']}", 4, "obj-u4-present", record_id, "accented", "lexical record")
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
                "lexical record",
            )
        records.append({"bare": row["bare"], "infinitive": infinitive, "gloss": meaning, "forms": forms, "record_id": record_id})
    return records


def _questions(builder: Builder) -> list[dict]:
    # Exact table cells and example sentences from the frozen interrogative page.
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
        records.append({"id": item_id, "shipped": word, "gloss": gloss})
    patterns = [
        ("chto-eto", "'''Что это?'''", "Что это?", "What is this?"),
        ("kto-eto", "'''Кто это?'''", "Кто это?", "Who is this?"),
        ("kak-dela", "'''Как дела?'''", "Как дела?", "How do you do?/How're things?"),
        ("gde-zhivete", "'''Где вы живёте?'''", "Где вы живёте?", "Where do you live?"),
        ("kuda-idete", "'''Куда вы идёте?'''", "Куда вы идёте?", "Where are you walking to?"),
        ("pochemu", "'''Почему он не знает?'''", "Почему он не знает?", "Why doesn't he know?"),
        ("kakoy-syr", "'''Какой сыр?'''", "Какой сыр?", "What kind of cheese?"),
    ]
    for item_id, needle, shipped, gloss in patterns:
        builder.wiki_quote(f"prov-u5-pattern-{item_id}", 5, "obj-u5-questions", "interrogative", needle, shipped, "example")
        records.append({"id": "pattern-" + item_id, "shipped": shipped, "gloss": gloss, "pattern": True})
    return records


def _prepositional(builder: Builder) -> list[dict]:
    examples = [
        ("o-knige", "prepositional", "'''Книга --> О книге'''", "О книге", "About the book"),
        ("o-pisme", "prepositional", "'''Письмо --> О письме'''", "О письме", "About the letter"),
        ("o-sade", "prepositional", "'''Сад --> О саде'''", "О саде", "About the garden"),
        ("v-moskve", "prepositional", "'''я в Москве'''", "я в Москве", "I'm in Moscow"),
        ("na-kryshe", "prepositional", "'''Он на крыше'''", "Он на крыше", "He's on the roof"),
        ("v-teatre", "prepositional", "'''Он в театре'''", "Он в театре", "He is at the theatre"),
        ("na-stole", "prepositions", "Ваза стоит на столе.", "Ваза стоит на столе.", "The vase stands on the table."),
        ("na-stene", "prepositions", "Картина висит на стене.", "Картина висит на стене.", "The painting hangs on the wall."),
    ]
    records = []
    for item_id, page, needle, shipped, gloss in examples:
        builder.wiki_quote(f"prov-u5-prep-{item_id}", 5, "obj-u5-prepositional", page, needle, shipped, "example")
        records.append({"id": item_id, "shipped": shipped, "gloss": gloss})
    for bare in ["книга", "письмо", "окно", "школа"]:
        row = builder.frozen.row(f"nouns.csv:{bare}")
        if "," in row["sg_prep"] or not row["sg_prep"]:
            raise SystemExit(f"{bare} prepositional field is not a single source form")
        shipped = builder.lexical(
            f"prov-u5-prep-field-{bare}",
            5,
            "obj-u5-prepositional",
            f"nouns.csv:{bare}",
            "sg_prep",
            "lexical record",
        )
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
    for bare, gloss_needle in [("в", "in; at; into; to; for; on; within"), ("на", "on (place); on (day/week); on (transport means); to; onto, upon; at"), ("о", "about; of; on; against")]:
        row = builder.frozen.row(f"others.csv:{bare}")
        if row["translations_en"] != gloss_needle:
            raise SystemExit(f"Preposition gloss changed for {bare}")
        shipped = builder.lexical(f"prov-u5-prep-word-{bare}", 5, "obj-u5-prepositional", f"others.csv:{bare}", "accented")
        records.append({"id": "prep-" + bare, "shipped": shipped, "gloss": gloss_needle, "preposition": True})
    return records


def _indeclinable(builder: Builder) -> list[dict]:
    builder.wiki_quote(
        "prov-u5-indecl-rule",
        5,
        "obj-u5-indeclinable",
        "nominative",
        "'''радио''' means 'radio' or 'radios', and '''такси''' is both 'taxi' and 'taxis'",
        "радио",
        "reference",
    )
    builder.wiki_quote(
        "prov-u5-taksi-mention",
        5,
        "obj-u5-indeclinable",
        "nominative",
        "'''такси''' is both 'taxi' and 'taxis'",
        "такси",
        "reference",
    )
    chosen = ["радио", "такси", "метро", "кино", "пальто", "кофе", "кафе", "меню"]
    records = []
    for bare in chosen:
        row = builder.frozen.row(f"nouns.csv:{bare}")
        if row["indeclinable"] != "1":
            raise SystemExit(f"{bare} is not flagged indeclinable")
        shipped = builder.lexical(f"prov-u5-indecl-{bare}", 5, "obj-u5-indeclinable", f"nouns.csv:{bare}", "accented")
        records.append({"bare": bare, "shipped": shipped, "gloss": row["translations_en"]})
    return records


def _check(builder: Builder, item_id: str, prompt: str, prompt_letter: str, options: list[str], answer: str, feedback: str, objective: str, source_id: str, kind: str) -> str:
    builder.item(item_id, english(prompt), prompt_letter, options, answer, english(feedback) if not CYRILLIC.search(feedback) else feedback, objective, source_id, kind)
    return item_id


def _unit_3(builder: Builder, nouns: list[dict], phrases: list[dict]) -> dict:
    objective = "obj-u3-gender"
    for noun in nouns[:6]:
        builder.example(f"ex-u3-{noun['bare']}", noun["shipped"], noun["gloss"], objective, noun["source"])
    gender_options = ["masculine", "feminine", "neuter"]
    guided_bares = [noun["bare"] for noun in nouns[:2]]
    independent_bares = [noun["bare"] for noun in nouns if noun.get("exception") or noun["bare"] in {"город", "книга", "окно", "дверь", "море", "друг"}]
    independent = []
    guided = []
    for noun in nouns:
        if noun["bare"] not in guided_bares and noun["bare"] not in independent_bares:
            continue
        item_id = f"u3-i-gender-{noun['bare']}"
        feedback = english("The admitted record lists this noun as ") + noun["gender"] + english(". The shown form is ") + noun["shipped"] + english(".")
        _check(
            builder,
            item_id,
            "Which gender does the admitted record list for the shown noun?",
            noun["shipped"],
            gender_options,
            noun["gender"],
            feedback,
            objective,
            noun["source"],
            "SOURCE_GENDER",
        )
        if noun["bare"] in guided_bares:
            guided.append(item_id)
        else:
            independent.append(item_id)
    phrase_objective = "obj-u3-phrases"
    naming_objective = "obj-u3-naming"
    for phrase in phrases:
        target = phrase.get("objective", phrase_objective)
        builder.example(f"ex-u3-phrase-{phrase['id']}", phrase["shipped"], phrase["gloss"], target, phrase["source"])
    phrase_checks = [p for p in phrases if p["id"] in {"здравствуйте", "спасибо", "пожалуйста", "да", "нет", "это"}]
    gloss_options = [p["gloss"] for p in phrase_checks]
    phrase_independent = []
    for phrase in phrase_checks:
        item_id = f"u3-i-phrase-{phrase['id']}"
        _check(
            builder,
            item_id,
            "Which admitted English gloss is recorded for the shown item?",
            phrase["shipped"],
            gloss_options,
            phrase["gloss"],
            english("The frozen dictionary row records this gloss: ") + phrase["gloss"] + english("."),
            phrase_objective,
            phrase["source"],
            "SOURCE_MEANING",
        )
        phrase_independent.append(item_id)
    naming = [p for p in phrases if p["id"] in {"moy-stol", "moya-sobaka", "chto-eto", "kto-eto"}]
    naming_options = [p["gloss"] for p in naming]
    naming_independent = []
    for phrase in naming:
        item_id = f"u3-i-name-{phrase['id']}"
        _check(
            builder,
            item_id,
            "Which admitted English meaning is recorded for this source-supplied naming item?",
            phrase["shipped"],
            naming_options,
            phrase["gloss"],
            english("The frozen grammar page records this meaning: ") + phrase["gloss"] + english("."),
            naming_objective,
            phrase["source"],
            "SOURCE_MEANING",
        )
        naming_independent.append(item_id)
    gender_examples = [f"ex-u3-{noun['bare']}" for noun in nouns[:6]]
    phrase_examples = [f"ex-u3-phrase-{phrase['id']}" for phrase in phrases if phrase.get("objective", phrase_objective) == phrase_objective][:4]
    naming_examples = [f"ex-u3-phrase-{phrase['id']}" for phrase in naming]
    sessions, objectives = _chain(
        3,
        [
            _objective_spec(objective, "Recognize admitted noun genders", gender_examples, guided[:2], independent, _gender_body(nouns)),
            _objective_spec(phrase_objective, "Recognize admitted everyday phrases", phrase_examples, phrase_independent[:2], phrase_independent[2:], _phrase_body(phrases)),
            _objective_spec(naming_objective, "Recognize admitted naming items", naming_examples, naming_independent[:1], naming_independent[1:], _naming_body(naming)),
        ],
        "Everyday phrases, naming, and gender",
        "Proposed Unit 3 uses frozen dictionary rows and grammar excerpts for gender, a small phrase set, and naming items. The owned book remains separate work. Qualified review is pending.",
    )
    if len(independent) + len(phrase_independent) + len(naming_independent) < 12:
        raise SystemExit("Unit 3 has fewer than 12 independent checks")
    return {"number": 3, "sessions": sessions, "objectives": objectives, "minimum_checks": 12}


def _unit_4(builder: Builder, pronouns: list[dict], verbs: list[dict]) -> dict:
    pronoun_objective = "obj-u4-pronouns"
    for pronoun in pronouns:
        builder.example(f"ex-u4-{pronoun['bare']}", pronoun["shipped"], pronoun["gloss"], pronoun_objective, OPENRUSSIAN_ID)
    glosses = [p["gloss"] for p in pronouns]
    pronoun_items = []
    for pronoun in pronouns:
        item_id = f"u4-i-pronoun-{pronoun['bare']}"
        _check(
            builder,
            item_id,
            "Which admitted gloss is recorded for the shown personal pronoun?",
            pronoun["shipped"],
            glosses,
            pronoun["gloss"],
            english("The frozen dictionary row records this gloss: ") + pronoun["gloss"] + english("."),
            pronoun_objective,
            OPENRUSSIAN_ID,
            "SOURCE_MEANING",
        )
        pronoun_items.append(item_id)
    present_objective = "obj-u4-present"
    for verb in verbs[:4]:
        builder.example(f"ex-u4-verb-{verb['bare']}", verb["infinitive"], verb["gloss"], present_objective, OPENRUSSIAN_ID)
    verb_items = []
    for verb, pronoun in [("читать", "ты"), ("делать", "я"), ("говорить", "мы"), ("видеть", "он"), ("писать", "вы"), ("идти", "они")]:
        record = next(item for item in verbs if item["bare"] == verb)
        options = [record["forms"][key] for key in ["я", "ты", "он", "мы", "вы", "они"]]
        # Six options are all source forms. Keep four so the key stays readable: the answer plus three other source forms.
        answer = record["forms"][pronoun]
        distractors = [form for form in options if form != answer][:3]
        item_id = f"u4-i-verb-{verb}-{pronoun}"
        _check(
            builder,
            item_id,
            english(f"Which admitted present-tense form is recorded for {pronoun_label(pronoun)}?"),
            record["infinitive"],
            [answer, *distractors],
            answer,
            english("The frozen verb row records ") + answer + english(f" for {pronoun_label(pronoun)}."),
            present_objective,
            OPENRUSSIAN_ID,
            "SOURCE_FORM",
        )
        verb_items.append(item_id)
    stress_objective = "obj-u4-stress"
    stress_words = [verbs[0]["infinitive"], verbs[7]["infinitive"]]
    for word in stress_words:
        builder.example(f"ex-u4-stress-{sha256_text(word)[:8]}", word, "Source stress mark", stress_objective, OPENRUSSIAN_ID)
    stress_items = []
    for word in stress_words:
        position = stress_position(word)
        item_id = f"u4-i-stress-{sha256_text(word)[:8]}"
        _check(
            builder,
            item_id,
            "Which vowel carries the admitted written stress mark? Count vowels from the left.",
            word,
            ["1", "2", "3", "4"],
            str(position),
            english("The frozen accented field places the stress mark on vowel ") + str(position) + english("."),
            stress_objective,
            OPENRUSSIAN_ID,
            "SOURCE_STRESS",
        )
        stress_items.append(item_id)
    nominative_objective = "obj-u4-nominative"
    nominative_examples = [
        ("muzh", "nominative", "'''Муж говорит'''", "Муж говорит", "The husband is talking"),
        ("poezd", "nominative", "'''Поезд старый'''", "Поезд старый", "The train is old"),
    ]
    nominative_items = []
    for item_id, page, needle, shipped, gloss in nominative_examples:
        builder.wiki_quote(f"prov-u4-nom-{item_id}", 4, nominative_objective, page, needle, shipped, "example")
        builder.example(f"ex-u4-nom-{item_id}", shipped, gloss, nominative_objective, WIKIBOOKS_ID)
    builder.wiki_quote(
        "prov-u4-nom-role",
        4,
        nominative_objective,
        "nominative",
        "it is the case that words are written in the dictionaries",
        "dictionary form",
        "explanation",
        "human-reviewed adaptation",
    )
    builder.wiki_quote(
        "prov-u4-cases-six",
        4,
        nominative_objective,
        "cases",
        "Russian has six cases",
        "Russian has six cases",
        "explanation",
    )
    builder.wiki_quote(
        "prov-u4-cases-default",
        4,
        nominative_objective,
        "cases",
        "The nominative case is the default case of a word, as found in dictionaries and when no other case is specifically used.",
        "The nominative case is the default case of a word, as found in dictionaries and when no other case is specifically used.",
        "explanation",
    )
    for item_id, _page, _needle, shipped, gloss in nominative_examples:
        check_id = f"u4-i-nom-{item_id}"
        _check(
            builder,
            check_id,
            "Which admitted example is the shown sentence?",
            shipped,
            [row[4] for row in nominative_examples],
            gloss,
            english("The frozen nominative page records this example as: ") + gloss + english("."),
            nominative_objective,
            WIKIBOOKS_ID,
            "SOURCE_CASE",
        )
        nominative_items.append(check_id)
    sessions, objectives = _chain(
        4,
        [
            _objective_spec(pronoun_objective, "Recognize admitted personal pronouns", [f"ex-u4-{p['bare']}" for p in pronouns], pronoun_items[:2], pronoun_items[2:], _pronoun_body(pronouns)),
            _objective_spec(present_objective, "Recognize admitted present-tense forms", [f"ex-u4-verb-{v['bare']}" for v in verbs[:4]], verb_items[:2], verb_items[2:], _verb_body(verbs)),
            _objective_spec(stress_objective, "Notice the admitted written stress mark", [f"ex-u4-stress-{sha256_text(w)[:8]}" for w in stress_words], [], stress_items, _stress_body(stress_words)),
            _objective_spec(nominative_objective, "Recognize the admitted nominative role", [f"ex-u4-nom-{row[0]}" for row in nominative_examples], [], nominative_items, english("The frozen cases page says Russian has six cases, and that the nominative is the default case of a word, as found in dictionaries and when no other case is specifically used. The frozen nominative page says it is the dictionary form and names the subject of a verb. The admitted examples are ") + nominative_examples[0][3] + english(" and ") + nominative_examples[1][3] + english(". The other five case endings are not taught here.")),
        ],
        "Pronouns, present tense, written stress, and the nominative",
        "Proposed Unit 4 uses frozen pronoun rows, verb rows, and grammar excerpts. Written stress is not a pronunciation score. The owned book remains separate work. Qualified review is pending.",
    )
    independent = pronoun_items[2:] + verb_items[2:] + stress_items + nominative_items
    if len(independent) < 14:
        raise SystemExit(f"Unit 4 has {len(independent)} independent checks")
    return {"number": 4, "sessions": sessions, "objectives": objectives, "minimum_checks": 14}


def _unit_5(builder: Builder, questions: list[dict], prepositional: list[dict], indeclinable: list[dict]) -> dict:
    question_objective = "obj-u5-questions"
    words = [q for q in questions if not q.get("pattern")]
    patterns = [q for q in questions if q.get("pattern")]
    for word in words:
        builder.example(f"ex-u5-q-{word['id']}", word["shipped"], word["gloss"], question_objective, WIKIBOOKS_ID)
    for pattern in patterns[:4]:
        builder.example(f"ex-u5-pattern-{pattern['id']}", pattern["shipped"], pattern["gloss"], question_objective, WIKIBOOKS_ID)
    word_options = [q["shipped"] for q in words[:6]]
    question_items = []
    for word in words[:6]:
        item_id = f"u5-i-q-{word['id']}"
        _check(
            builder,
            item_id,
            english("Which admitted question word is recorded for this English purpose: ") + word["gloss"] + english("?"),
            word["shipped"],
            word_options,
            word["shipped"],
            english("The frozen interrogative table records ") + word["shipped"] + english(" for ") + word["gloss"] + english("."),
            question_objective,
            WIKIBOOKS_ID,
            "SOURCE_QUESTION",
        )
        question_items.append(item_id)
    pattern_options = [p["gloss"] for p in patterns[:4]]
    for pattern in patterns[:4]:
        item_id = f"u5-i-pattern-{pattern['id']}"
        _check(
            builder,
            item_id,
            "Which admitted English meaning is recorded for the shown written question?",
            pattern["shipped"],
            pattern_options,
            pattern["gloss"],
            english("The frozen interrogative page records this meaning: ") + pattern["gloss"] + english(". Written punctuation is not pronunciation practice."),
            question_objective,
            WIKIBOOKS_ID,
            "SOURCE_QUESTION",
        )
        question_items.append(item_id)
    prep_objective = "obj-u5-prepositional"
    sentences = [p for p in prepositional if not p.get("field") and not p.get("preposition")]
    for sentence in sentences:
        builder.example(f"ex-u5-prep-{sentence['id']}", sentence["shipped"], sentence["gloss"], prep_objective, WIKIBOOKS_ID)
    prep_items = []
    sentence_options = [s["gloss"] for s in sentences[:4]]
    for sentence in sentences[:4]:
        item_id = f"u5-i-prep-{sentence['id']}"
        _check(
            builder,
            item_id,
            "Which admitted English description is recorded for the shown prepositional example?",
            sentence["shipped"],
            sentence_options,
            sentence["gloss"],
            english("The frozen page records this example as: ") + sentence["gloss"] + english("."),
            prep_objective,
            WIKIBOOKS_ID,
            "SOURCE_CASE",
        )
        prep_items.append(item_id)
    for field in [p for p in prepositional if p.get("field")][:2]:
        options = [field["nominative"], field["shipped"]]
        if options[0] == options[1]:
            raise SystemExit("Nominative and prepositional forms are identical")
        item_id = f"u5-i-prep-field-{field['bare']}"
        _check(
            builder,
            item_id,
            "Which string is the admitted singular prepositional field, not the nominative field?",
            field["nominative"],
            options,
            field["shipped"],
            english("The frozen noun row lists the singular prepositional field as ") + field["shipped"] + english("."),
            prep_objective,
            OPENRUSSIAN_ID,
            "SOURCE_FORM",
        )
        prep_items.append(item_id)
    indecl_objective = "obj-u5-indeclinable"
    for noun in indeclinable:
        builder.example(f"ex-u5-indecl-{noun['bare']}", noun["shipped"], noun["gloss"], indecl_objective, OPENRUSSIAN_ID)
    # Contrasting declinable nouns already admitted in Unit 3. Their provenance is Unit 3.
    # Unit 5 checks that quote them need those shipped strings in Unit 5 provenance too.
    contrast_bares = ["книга", "окно"]
    for bare in contrast_bares:
        if f"prov-u5-contrast-{bare}" not in {record["itemId"] for record in builder.provenance}:
            builder.lexical(f"prov-u5-contrast-{bare}", 5, indecl_objective, f"nouns.csv:{bare}", "accented")
    indecl_items = []
    for noun in indeclinable[:4]:
        item_id = f"u5-i-indecl-{noun['bare']}"
        _check(
            builder,
            item_id,
            "What does the admitted noun row record for the shown word?",
            noun["shipped"],
            ["indeclinable", "declinable"],
            "indeclinable",
            english("The frozen noun row sets indeclinable to 1 for ") + noun["shipped"] + english("."),
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
            "What does the admitted noun row record for the shown word?",
            shipped,
            ["indeclinable", "declinable"],
            "declinable",
            english("The frozen noun row sets indeclinable to 0 for ") + shipped + english("."),
            indecl_objective,
            OPENRUSSIAN_ID,
            "SOURCE_INDECLINABLE",
        )
        indecl_items.append(item_id)
    sessions, objectives = _chain(
        5,
        [
            _objective_spec(question_objective, "Recognize admitted question words and written questions", [f"ex-u5-q-{q['id']}" for q in words[:4]], question_items[:2], question_items[2:], _question_body(words, patterns)),
            _objective_spec(
                prep_objective,
                "Recognize admitted prepositional examples",
                [f"ex-u5-prep-{s['id']}" for s in sentences[:4]],
                prep_items[:2],
                prep_items[2:],
                _prep_body(
                    sentences,
                    [p for p in prepositional if p.get("field")],
                    [p for p in prepositional if p.get("preposition")],
                ),
            ),
            _objective_spec(indecl_objective, "Recognize admitted indeclinable nouns", [f"ex-u5-indecl-{n['bare']}" for n in indeclinable[:4]], [], indecl_items, _indecl_body(indeclinable)),
        ],
        "Questions, the prepositional case, and indeclinable nouns",
        "Proposed Unit 5 uses frozen grammar excerpts and dictionary rows. It does not teach question intonation and it does not supply audio. The owned book remains separate work. Qualified review is pending.",
    )
    independent = question_items[2:] + prep_items[2:] + indecl_items
    if len(independent) < 14:
        raise SystemExit(f"Unit 5 has {len(independent)} independent checks")
    return {"number": 5, "sessions": sessions, "objectives": objectives, "minimum_checks": 14}


def pronoun_label(bare: str) -> str:
    return {
        "я": "the pronoun glossed I",
        "ты": "the pronoun glossed you",
        "он": "the pronoun glossed he",
        "мы": "the pronoun glossed we",
        "вы": "the pronoun glossed you (plural); you (formal)",
        "они": "the pronoun glossed they",
    }[bare]


def _objective_spec(objective: str, title: str, examples: list[str], guided: list[str], checks: list[str], body: str) -> dict:
    if len(examples) < 2:
        raise SystemExit(f"{objective} has fewer than two examples")
    if not checks:
        raise SystemExit(f"{objective} has no independent checks")
    return {
        "id": objective,
        "title": title,
        "examples": examples,
        "guided": guided,
        "checks": checks,
        "body": body,
    }


def _chain(number: int, specs: list[dict], orient_title: str, orient_body: str) -> tuple[list[dict], list[dict]]:
    objectives = []
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
                    "title": orient_title,
                    "body": english(orient_body),
                    "dominantAction": "Continue",
                    "referenceIds": ["ref-u35-attribution"],
                    "itemIds": [],
                    "exampleIds": [],
                    "unavailableReason": None,
                    "bookDirection": english(f"Open Lesson {number} in the copy you own. The app records do not replace that lesson."),
                    "companionDirection": None,
                    "remediationStepId": None,
                }
            ],
        }
    ]
    for spec in specs:
        remediation = f"u{number}-st-rem-{spec['id'].split('-')[-1]}"
        teach = f"u{number}-st-teach-{spec['id'].split('-')[-1]}"
        guided_id = f"u{number}-st-guided-{spec['id'].split('-')[-1]}"
        check_id = f"u{number}-st-check-{spec['id'].split('-')[-1]}"
        steps = [
            _step(teach, "TEACH", spec["title"], spec["body"], example_ids=spec["examples"], reference_ids=["ref-u35-attribution"]),
            _step(remediation, "REMEDIATE", "Remediation", english("Return to the explanation. Compare the shown form with the admitted record. Do not invent a form that is not in the record."), example_ids=spec["examples"][:2]),
        ]
        if spec["guided"]:
            steps.append(_step(guided_id, "GUIDED", "Guided practice", english("Choose the admitted record. Feedback states the frozen field."), item_ids=spec["guided"], remediation=remediation))
        steps.append(_step(check_id, "CHECK", "Independent checks", english("These checks use only admitted records. A miss returns to the explanation."), item_ids=spec["checks"], remediation=remediation))
        sessions.append(
            {
                "id": f"u{number}-s-{spec['id'].split('-')[-1]}",
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
                "instructionStepIds": [f"u{number}-st-orient", teach],
                "exampleIds": spec["examples"],
                "practiceStepIds": [guided_id] if spec["guided"] else [teach],
                "checkItemIds": spec["checks"],
                "remediationStepIds": [remediation],
            }
        )
    book_steps = [
        _step(
            f"u{number}-st-book-work",
            "BOOK",
            f"Work Lesson {number} in your copy",
            english(f"Do Lesson {number} in the copy you own. Use that book's explanations and practice. The proposed records in this unit are a separate licensed set and do not copy the book."),
            book=english(f"Open Lesson {number} in your copy and complete its lesson work."),
            reference_ids=["ref-book-topics", "ref-u35-attribution"],
        ),
        _step(
            f"u{number}-st-book",
            "BOOK",
            f"Record Lesson {number} book work",
            english(f"When you have done Lesson {number} in your copy, self-attest here. Attestation records your report. It does not grade the lesson, approve a mapping, or approve the proposed records."),
            book=english(f"Self-attest only after the Lesson {number} work in your copy."),
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


def _gender_body(nouns: list[dict]) -> str:
    body = english(
        "The frozen grammar page says Russian groups nouns as masculine, feminine, and neuter, and that an ending cue is not enough for every noun. "
        "Use the gender on the admitted record. "
    )
    for noun in nouns:
        body += noun["shipped"] + english(" — ") + noun["gender"] + english(", ") + noun["gloss"] + english(". ")
    body += english("Adjective agreement and past-tense endings are outside this unit.")
    return body


def _phrase_body(phrases: list[dict]) -> str:
    chosen = [p for p in phrases if p["id"] in {"здравствуйте", "спасибо", "пожалуйста", "да", "нет", "это", "kak-dela", "gde-vy"}]
    body = english("These everyday items are copied from admitted records. They are not patterns for new sentences. ")
    for phrase in chosen:
        body += phrase["shipped"] + english(" — ") + phrase["gloss"] + english(". ")
    return body


def _naming_body(naming: list[dict]) -> str:
    body = english("These naming items are exact frozen excerpts. Do not substitute another noun into them. ")
    for phrase in naming:
        body += phrase["shipped"] + english(" — ") + phrase["gloss"] + english(". ")
    return body


def _pronoun_body(pronouns: list[dict]) -> str:
    body = english("The admitted nominative table and dictionary rows supply these personal pronouns. ")
    for pronoun in pronouns:
        body += pronoun["shipped"] + english(" — ") + pronoun["gloss"] + english(". ")
    body += english("The page uses these as the speaker, addressee, and other-person forms. Other cases of the pronouns are not taught in this unit.")
    return body


def _verb_body(verbs: list[dict]) -> str:
    body = english(
        "The frozen verb page says present-tense forms change with the person and number of the subject, and that the imperfective is the form used for the present. "
        "Each form is a field from the admitted verb row. The app does not create a form that is absent from the row. Admitted infinitives: "
    )
    for verb in verbs:
        body += verb["infinitive"] + english(" — ") + verb["gloss"] + english(". ")
    sample = next(verb for verb in verbs if verb["bare"] == "читать")
    body += english("One complete present-tense row from the admitted ") + sample["infinitive"] + english(" record: ")
    for pronoun in ["я", "ты", "он", "мы", "вы", "они"]:
        body += pronoun_label(pronoun) + english(": ") + sample["forms"][pronoun] + english(". ")
    return body


def _stress_body(words: list[str]) -> str:
    body = english(
        "The mark in the admitted form shows the stressed vowel. It is a written mark. "
        "Listening and speaking stay with the book. No audio is mapped to these forms. "
    )
    for word in words:
        body += word + english(". ")
    return body


def _question_body(words: list[dict], patterns: list[dict]) -> str:
    body = english("The frozen interrogative page supplies these question words and written questions. Intonation is not taught because no exact licensed audio is matched. ")
    for word in words:
        body += word["shipped"] + english(" — ") + word["gloss"] + english(". ")
    body += english("Written examples: ")
    for pattern in patterns:
        body += pattern["shipped"] + english(" — ") + pattern["gloss"] + english(". ")
    return body


def _prep_body(sentences: list[dict], fields: list[dict], prepositions: list[dict]) -> str:
    body = english(
        "The frozen prepositional page presents location examples and about-examples. "
        "Use only the admitted examples, the admitted singular prepositional fields, and the admitted preposition rows. "
        "Do not build a new phrase from those parts. "
    )
    for sentence in sentences:
        body += sentence["shipped"] + english(" — ") + sentence["gloss"] + english(". ")
    body += english("Admitted singular prepositional fields, after the nominative field from the same row: ")
    for field in fields:
        body += field["nominative"] + english(" / ") + field["shipped"] + english(". ")
    body += english("Admitted preposition rows: ")
    for preposition in prepositions:
        body += preposition["shipped"] + english(" — ") + preposition["gloss"] + english(". ")
    return body


def _indecl_body(nouns: list[dict]) -> str:
    named = [noun["shipped"] for noun in nouns if noun["bare"] in {"радио", "такси"}]
    body = english(
        "The frozen nominative page says some foreign-origin nouns do not change between singular and plural. "
        "It names "
    ) + named[0] + english(" and ") + named[1] + english(
        ". The admitted noun rows below are flagged indeclinable. That flag is the record used here, not a rule applied to a new word. "
    )
    for noun in nouns:
        body += noun["shipped"] + english(" — ") + noun["gloss"] + english(". ")
    return body


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
    region = re.compile(r"[\u0400-\u04FF\u0301](?:[\u0400-\u04FF\u0301]|[ \t]+(?=[\u0400-\u04FF]))*[?!.,]*")
    hosts = []
    for example in builder.examples:
        hosts.append(example["display"])
        hosts.append(example["note"])
    for item in builder.items:
        hosts.extend([item["prompt"], item["promptLetter"], item["answer"], item["feedback"], *item["options"]])
    for record in builder.provenance:
        hosts.append(record["shippedText"])
    for host in hosts:
        for match in region.finditer(host):
            text = match.group(0).strip()
            trimmed = text.rstrip("?!.,")
            if text not in shipped and trimmed not in shipped:
                raise SystemExit(f"Unsourced Russian region: {text}")


def _assert_unit_text(units: list[dict], provenance: list[dict]) -> None:
    shipped_by_unit: dict[int, set[str]] = {}
    for record in provenance:
        shipped_by_unit.setdefault(record["unitNumber"], set()).add(record["shippedText"])
    region = re.compile(r"[\u0400-\u04FF\u0301](?:[\u0400-\u04FF\u0301]|[ \t]+(?=[\u0400-\u04FF]))*[?!.,]*")
    for unit in units:
        shipped = shipped_by_unit.get(unit["number"], set())
        hosts: list[str] = []
        for session in unit["sessions"]:
            for step in session["steps"]:
                hosts.extend([step["title"], step["body"], step.get("bookDirection") or ""])
        for host in hosts:
            for match in region.finditer(host):
                text = match.group(0).strip()
                trimmed = text.rstrip("?!.,")
                if text not in shipped and trimmed not in shipped:
                    raise SystemExit(f"Unit {unit['number']} unsourced Russian region: {text}")


if __name__ == "__main__":
    built = build_units_3_5()
    print(
        f"provenance={len(built['provenance'])} items={len(built['items'])} "
        f"examples={len(built['examples'])} units={len(built['units'])}"
    )
