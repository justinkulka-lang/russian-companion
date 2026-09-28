"""Exact spans for the remaining-curriculum admissions.

ReadyRussian clock evidence is a recorded text extraction. Other spans are
contiguous substrings of a retained file. This module does not compose Russian.
"""

from __future__ import annotations

import hashlib
import re
from pathlib import Path

from source_transforms import html_visible

ROOT = Path(__file__).resolve().parents[1]
READY = ROOT / "catalog/sources/readyrussian"
LATE = ROOT / "catalog/sources/units-26-30"
RETRIEVED = "2026-09-28T16:43:13Z"


def _read(path: Path) -> str:
    return path.read_text(encoding="utf-8")


def _between(text: str, start: str, end: str, label: str) -> str:
    begin = text.find(start)
    if begin < 0:
        raise SystemExit(f"{label} start is missing: {start!r}")
    finish = text.find(end, begin)
    if finish < 0:
        raise SystemExit(f"{label} end is missing: {end!r}")
    return text[begin:finish + len(end)]


def _sha(path: Path) -> str:
    return hashlib.sha256(path.read_bytes()).hexdigest()


GRAMMAR20_FILE = READY / "grammar20.html"
LANGGEN3_FILE = READY / "langgen3.html"
LETTER_FILE = READY / "letter.html"
READING5_FILE = READY / "reading5.html"
HOME_FILE = READY / "home.html"

GRAMMAR20_SHA = _sha(GRAMMAR20_FILE)
LANGGEN3_SHA = _sha(LANGGEN3_FILE)
LETTER_SHA = _sha(LETTER_FILE)
READING5_SHA = _sha(READING5_FILE)
HOME_SHA = _sha(HOME_FILE)

def _visible_between(text: str, start: str, end: str, label: str) -> str:
    raw = _between(text, start, end, label)
    if re.search(r"<img\b|data:image|base64|<script\b|<audio\b|\.ogg", raw, re.IGNORECASE):
        raise SystemExit(f"{label} piece contains embedded media")
    visible = html_visible(raw).strip()
    if not visible:
        raise SystemExit(f"{label} piece has no visible text")
    return visible


def _clock_extraction(page: str) -> str:
    """Join media-free visible pieces. Quarter-hour lines stay on the frozen page only."""
    pieces = [
        _visible_between(
            page,
            "We can read off the numbers shown on the dial",
            "shaded pie slice.",
            "clock-rules",
        ),
        _visible_between(
            page,
            "The first hour began at 12 o'clock.",
            "at the beginning of the first hour",
            "clock-first",
        ),
        _visible_between(
            page,
            "As the little hand passes the 1",
            "at the fifteenth minute of the second [hour]",
            "clock-into",
        ),
        _visible_between(
            page,
            "в час двадцать пять минут",
            "at twenty five minutes of the second [hour]",
            "clock-twenty-five",
        ),
        _visible_between(
            page,
            "Once the hour is half-way done",
            "lacking twenty five minutes to the two",
            "clock-remain",
        ),
    ]
    extracted = "\n\n".join(pieces)
    for item in ("в четверть", "без четверти", ".ogg", "<img", "data:image", "base64", "<script", "<audio", "<"):
        if item in extracted:
            raise SystemExit(f"clock extraction contains excluded material: {item}")
    return extracted


_GRAMMAR20_PAGE = _read(GRAMMAR20_FILE)
CLOCK_SPAN = _clock_extraction(_GRAMMAR20_PAGE)
NEGATION_SPAN = _between(
    _read(LANGGEN3_FILE),
    "Note that the modifier",
    "I am not the one who thinks it is cold.)",
    "negation",
)
LETTER_SPAN = _between(
    _read(LETTER_FILE),
    "They have the same basic elements",
    "Respected Ivan Ivanovich",
    "letter",
)
CORRESPONDENCE_SPAN = _between(
    _read(LETTER_FILE),
    "к настоящему письму прилагается",
    "Respectfully",
    "correspondence",
)
CAPSTONE_SPAN = _between(
    _read(READING5_FILE),
    "Оди́н раз загоре́лся",
    "большу́ю ку́клу.",
    "capstone",
)

GUIDE_FILE = LATE / "wiktionary/russian-entry-guidelines-92541199.txt"
GUIDE_TEXT = _read(GUIDE_FILE)
PRONUNCIATION_SPAN = _between(
    GUIDE_TEXT,
    "===Pronunciation===",
    "specify the correct pronunciation.",
    "pronunciation",
)
DICTIONARY_SPAN = _between(
    GUIDE_TEXT,
    "==Nouns==",
    "* Conjugation tables",
    "dictionary",
)

VECHERA_FILE = LATE / "wikisource/vechera-5336197.txt"
VECHERA_TEXT = _read(VECHERA_FILE)
READING_SPAN = _between(
    VECHERA_TEXT,
    "устав писать",
    "сложа руки",
    "verbal-adverb-reading",
)

PARTICIPLE_FILES = {
    "ostavlyaemyj": LATE / "wiktionary/ostavlyaemyj-74231596.txt",
    "obsuzhdaemyj": LATE / "wiktionary/obsuzhdaemyj-74233962.txt",
    "napisannyj": LATE / "wiktionary/napisannyj-74228228.txt",
    "prochitannyj": LATE / "wiktionary/prochitannyj-74232159.txt",
}
PARTICIPLE_SPANS = {key: path.read_text(encoding="utf-8") for key, path in PARTICIPLE_FILES.items()}


def _forbid(label: str, span: str, banned: tuple[str, ...]) -> None:
    for item in banned:
        if item in span:
            raise SystemExit(f"{label} span contains excluded material: {item}")


_forbid("pronunciation", PRONUNCIATION_SPAN, (".ogg", "audio"))
_forbid("dictionary", DICTIONARY_SPAN, (".ogg", "audio"))
_forbid("letter", LETTER_SPAN, ("please me informed", "Дорогой", "С любовью", ".ogg"))
_forbid("correspondence", CORRESPONDENCE_SPAN, ("please me informed", "Дорогой", "С любовью", ".ogg"))
_forbid("capstone", CAPSTONE_SPAN, (".ogg", "audio", "<"))
if "в двенадцать часов десять минут" not in CLOCK_SPAN:
    raise SystemExit("Clock span lost the direct-reading example")
if "This makes sense up to the half hour." not in CLOCK_SPAN:
    raise SystemExit("Clock span lost the minutes-into-the-hour statement")
if "ложился на\u00a0диван" not in READING_SPAN:
    raise SystemExit("Reading span lost the exact rest clause")
if READING_SPAN.find("устав писать") > READING_SPAN.find("вставлены") >= 0:
    raise SystemExit("Reading span starts at the earlier false hit")
