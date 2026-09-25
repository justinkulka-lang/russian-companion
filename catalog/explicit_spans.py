"""Exact frozen spans for the admitted Unit 7 and Unit 17 capabilities.

Each span is a unique slice of retained bytes. This module does not paraphrase,
invent stress, or admit a construction the slice does not state.
"""

from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]


def _slice(path: Path, start: str, end: str) -> str:
    text = path.read_text(encoding="utf-8")
    begin = text.find(start)
    if begin < 0 or text.find(start, begin + 1) >= 0:
        raise SystemExit(f"Span start is not unique in {path.name}: {start!r}")
    finish = text.find(end, begin)
    if finish < 0:
        raise SystemExit(f"Span end is missing in {path.name}: {end!r}")
    span = text[begin:finish + len(end)]
    if span.count(start) != 1 or not span.endswith(end):
        raise SystemExit(f"Span identity failed in {path.name}")
    return span


ADVERB_SPAN = _slice(
    ROOT / "catalog/sources/units-6-10/wikipedia/adverb-1374548415.txt",
    "most adverbs are formed",
    "(quickly, well, wonderfully).",
)
CLOCK_SPAN = _slice(
    ROOT / "catalog/sources/units-16-20/wikipedia/date-time-country-1366690592.txt",
    "uses the same convention:",
    "7:45 respectively",
)
DATE_SPAN = _slice(
    ROOT / "catalog/sources/units-16-20/wikipedia/date-notation-russia-1357134172.txt",
    "In [[Russia]], dates are usually written",
    'Двадцать восьмое августа}}").',
)

EXPLICIT_GLOSSES = {
    ADVERB_SPAN: {
        "быстро": frozenset({"quickly"}),
        "хорошо": frozenset({"well"}),
        "прекрасно": frozenset({"wonderfully"}),
    },
    CLOCK_SPAN: {
        "четверть восьмого": frozenset({"7:15", "quarter of the eighth hour"}),
        "полвосьмого": frozenset({"7:30", "half of eight"}),
        "без четверти восемь": frozenset({"7:45", "eight without a quarter"}),
    },
    DATE_SPAN: {
        "Двадцать восьмое августа": frozenset({"the month in genitive case"}),
        "28 августа 2017 г.": frozenset({"day month year", "of the year"}),
    },
}
