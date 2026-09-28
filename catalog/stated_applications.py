"""Explicit construction relationships for admitted ReadyRussian spans.

A co-occurring string is not a relationship. Each cue maps to one keyed form
and the competing forms the same frozen text pairs with a different meaning.
"""

from __future__ import annotations

from remaining_spans import CLOCK_SPAN, LETTER_SPAN

CLOCK_APPLICATIONS = {
    "In Russian we can state the number of minutes into the hour which is in progress.": (
        "в пятнадцать минут второго",
        frozenset({"в час пятнадцать минут"}),
    ),
    "This is used after the half hour.": (
        "без двадцати пяти два",
        frozenset({"в час тридцать пять минут"}),
    ),
    "This option is always available.": (
        "в двенадцать часов десять минут",
        frozenset({"в десять минут первого"}),
    ),
}

LETTER_APPLICATIONS = {
    "In English we begin with a topic sentence and then follow with supporting facts.": (
        "In Russian the facts come first with the topic sentence as the conclusion.",
        frozenset({"Strangers and people who are not closed friends should never be addressed as “dear”."}),
    ),
    "Здравствуйте!": (
        "Strangers and people who are not closed friends should never be addressed as “dear”.",
        frozenset({"In Russian the facts come first with the topic sentence as the conclusion."}),
    ),
    "Body": (
        "Closing and signature",
        frozenset({"Greeting", "Sender’s address and date"}),
    ),
}

STATED_APPLICATIONS = {
    CLOCK_SPAN: CLOCK_APPLICATIONS,
    LETTER_SPAN: LETTER_APPLICATIONS,
}
