"""Typed Units 6–10 checks. Every Russian span and rule label is an exact frozen substring.

Governed relationships are resolved from provenance records in the intake builder.
This module does not infer a relationship from script shape, and it does not rewrite
a rationale so that a later validator can find an acceptance phrase.
"""

import re
from pathlib import Path

from units_3_5_intake import CYRILLIC, OPENRUSSIAN_ID, WIKIBOOKS_ID

W = WIKIBOOKS_ID
O = OPENRUSSIAN_ID

_PAGE_FILE = {
    "accusative": "accusative-4330404.txt",
    "adjectives": "adjectives-4292379.txt",
    "genitive": "genitive-4567338.txt",
    "numbers": "numbers-4371358.txt",
}
_LEMMA = re.compile(r"^(nouns|adjectives|others)\.csv:(.+)#([A-Za-z0-9_]+)$")
_NUMBER_CUE = re.compile(r"[0-9][0-9,~/.\-]*")
_ENGLISH_CUE = re.compile(r"(?i)which admitted\b.{0,80}\brecorded for\b")
_LATIN_LOOKALIKE = {"e": "е", "y": "у", "a": "а", "o": "о", "p": "р", "c": "с", "x": "х"}
RELATION_KINDS = (
    "SAME_LEMMA_DIFFERENT_FIELD",
    "SAME_CONSTRUCTION_DIFFERENT_FORM",
    "SAME_RULE_DIFFERENT_BOUNDARY",
    "SOURCE_SUPPLIED_PAIR",
    "RULE_CLASSIFIES_SOURCE_EXAMPLE",
)


def _wiki(page, needle, ru, en):
    return ("wiki", page, needle, ru, en)


def _field(record, name):
    return ("field", record, name)


def _span(page: str, start: str, end: str) -> str:
    path = Path(__file__).resolve().parent / "sources/units-6-10/wikibooks" / _PAGE_FILE[page]
    text = path.read_text(encoding="utf-8")
    begin = text.find(start)
    finish = text.find(end, begin if begin >= 0 else 0)
    if begin < 0 or finish < 0:
        raise SystemExit(f"Frozen span is missing on {page}: {start!r} .. {end!r}")
    return text[begin:finish + len(end)]


def _fold(text: str) -> str:
    return text.replace("\u0301", "").replace("ё", "е").replace("Ё", "е").casefold()


def _tokens(text: str) -> list[str]:
    return re.findall(r"[0-9]+|[a-zа-я]+", _fold(text))


def _contained(small: list[str], big: list[str]) -> bool:
    if not small or len(small) >= len(big):
        return False
    width = len(small)
    return any(big[index:index + width] == small for index in range(len(big) - width + 1))


def qualifying_overlap(shown: str, answer: str) -> str | None:
    """Reject a qualifying check that can be solved by repeating or spotting tokens.

    Same-lemma stem pairs such as мама/маму are not token overlap. No Unit 6–10
    task is granted the embedded-feature exception: a visible answer token, a
    repeated cue, or a sentence chosen because it contains the cue token is invalid.
    """
    shown_tokens = _tokens(shown)
    answer_tokens = _tokens(answer)
    if shown_tokens and shown_tokens == answer_tokens:
        return "repeats"
    if _contained(answer_tokens, shown_tokens):
        return "answer-in-cue"
    if _contained(shown_tokens, answer_tokens):
        return "cue-in-answer"
    folded_answer = _fold(answer)
    if len(folded_answer) == 1:
        letter = _LATIN_LOOKALIKE.get(folded_answer, folded_answer)
        folded_shown = _fold(shown)
        if folded_answer in folded_shown or letter in folded_shown:
            return "answer-in-cue"
    return None


def _lemma(record: dict):
    match = _LEMMA.fullmatch(record["sourceRecordId"])
    if not match:
        return None
    return f"{match.group(1)}:{match.group(2)}", match.group(3)


def _cyr(text: str) -> bool:
    return bool(CYRILLIC.search(text))


def _page(record: dict) -> str:
    return record["sourceRecordId"].split(":", 1)[0]


def classify_relation(cue: dict, answer: dict, distractors: list[dict]) -> str | None:
    """Return the governed relationship the records actually have, or None."""
    group = [cue, answer, *distractors]
    parsed = [_lemma(record) for record in group]
    if all(parsed):
        lemmas = {item[0] for item in parsed}
        answer_field = parsed[1][1]
        if len(lemmas) == 1 and answer_field != parsed[0][1] and all(answer_field != item[1] for item in parsed[2:]):
            return "SAME_LEMMA_DIFFERENT_FIELD"
    if (
        _cyr(cue["shippedText"])
        and not _cyr(answer["shippedText"])
        and all(not _cyr(record["shippedText"]) for record in distractors)
        and len({record["sourceRecordId"] for record in group}) == 1
        and cue["shippedText"] != answer["shippedText"]
    ):
        return "RULE_CLASSIFIES_SOURCE_EXAMPLE"
    if (
        cue["sourceRecordId"].startswith("numbers:")
        and cue["sourceRecordId"] == answer["sourceRecordId"]
        and distractors
        and all(
            record["sourceRecordId"].startswith("numbers:") and record["sourceRecordId"] != cue["sourceRecordId"]
            for record in distractors
        )
    ):
        return "SAME_RULE_DIFFERENT_BOUNDARY"
    if len({record["sourceRecordId"] for record in group}) == 1 and cue["shippedText"] != answer["shippedText"]:
        return "SOURCE_SUPPLIED_PAIR"
    page = _page(cue)
    if (
        cue["sourceRecordId"] == answer["sourceRecordId"]
        and "||" in cue["originalSourceText"]
        and all(
            _page(record) == page and "||" in record["originalSourceText"] and record["sourceRecordId"] != cue["sourceRecordId"]
            for record in distractors
        )
    ):
        return "SAME_CONSTRUCTION_DIFFERENT_FORM"
    answer_lemma = _lemma(answer)
    distractor_lemmas = [_lemma(record) for record in distractors]
    if (
        answer_lemma
        and distractor_lemmas
        and all(distractor_lemmas)
        and all(item[0] == answer_lemma[0] and item[1] != answer_lemma[1] for item in distractor_lemmas)
    ):
        original = _fold(cue["originalSourceText"])
        folded_answer = _fold(answer["shippedText"])
        folded_cue = _fold(cue["shippedText"])
        if folded_answer and folded_answer in original and folded_cue in original and folded_cue != folded_answer:
            return "SAME_CONSTRUCTION_DIFFERENT_FORM"
    if (
        distractors
        and all(record["sourceRecordId"] == answer["sourceRecordId"] for record in distractors)
        and answer["sourceRecordId"] != cue["sourceRecordId"]
    ):
        original = _fold(cue["originalSourceText"])
        folded_answer = _fold(answer["shippedText"])
        folded_cue = _fold(cue["shippedText"])
        if folded_answer and folded_answer in original and folded_cue in original and folded_cue != folded_answer:
            return "SAME_CONSTRUCTION_DIFFERENT_FORM"
    return None


def _cue_language(shown: str) -> str:
    if _NUMBER_CUE.fullmatch(shown):
        return "number"
    if CYRILLIC.search(shown):
        return "ru"
    return "rule"


def _check(prompt, shown, answer, distractors, source=W, feature="GOVERNED_FORM", mode=None, cue=None, why=None):
    del mode  # Relationship kind is derived from provenance records, not from this argument.
    if _ENGLISH_CUE.search(prompt) and not CYRILLIC.search(shown):
        if _cue_language(shown) == "number":
            prompt = "Which admitted form matches the shown number?"
        else:
            raise SystemExit(f"English-cue prompt is not a grammar check: {prompt!r} shown={shown!r}")
    normalized = []
    for row in distractors:
        if len(row) == 2:
            text, rationale = row
            difference = rationale
        else:
            text, rationale, difference = row
        normalized.append((text, rationale, difference))
    return {
        "prompt": prompt,
        "shown": shown,
        "answer": answer,
        "distractors": normalized,
        "source": source,
        "feature": feature,
        "cue": cue or _cue_language(shown),
        "why": why or "Does the admitted record show the governed contrast?",
    }


def _other(text, rationale, difference=None):
    return (text, rationale, difference or rationale)


def populate(lesson, number: int) -> None:
    {
        6: _unit_6,
        7: _unit_7,
        8: _unit_8,
        9: _unit_9,
        10: _unit_10,
    }[number](lesson)


def _unit_6(lesson) -> None:
    gender = "gender"
    acc = "accusative"
    my_line = "the 'my' in 'my dog' ('''моя собака''') and 'my table' ('''мой стол''') are different"
    lesson.objective(
        "possessive",
        "Choose the admitted possessive form",
        "FORM_SELECTION",
        (gender, my_line, my_line),
        [
            _wiki(gender, "my table' ('''мой стол''')", "мой стол", "my table"),
            _wiki(gender, "my dog' ('''моя собака''')", "моя собака", "my dog"),
            _wiki(acc, "| Our || Наш || Нашего || нашу || Наше || Наши || Наших", "Наш", "Our"),
            _wiki(acc, "| Your <small>''(formal/plural)''</small> || Ваш || Вашего ||вашу || Ваше || Ваши || Ваших", "Ваш", "Your"),
            _field("adjectives.csv:мой", "decl_m_nom"),
            _field("adjectives.csv:мой", "decl_f_nom"),
            _field("adjectives.csv:мой", "decl_n_nom"),
            _field("adjectives.csv:мой", "decl_pl_nom"),
            _field("adjectives.csv:твой", "decl_m_nom"),
            _field("adjectives.csv:твой", "decl_f_nom"),
        ],
        [
            _check("Which admitted form agrees with the shown word?", "собака", "моя́", [
                _other("мо́й", "This form is the other agreement row of the same word."),
                _other("мои́", "This form is the plural row of the same word."),
            ], O, feature="POSSESSIVE_AGREEMENT"),
            _check("Which admitted form agrees with the shown word?", "стол", "мо́й", [
                _other("моя́", "This form is the other agreement row of the same word."),
            ], O, feature="POSSESSIVE_AGREEMENT"),
            _check("Which admitted form is the object row of the shown form?", "Наш", "нашу", [
                _other("Наш", "This form is the dictionary row of the same word, not the object row."),
                _other("Наши", "This form is the plural row of the same word, not the object row."),
            ], feature="POSSESSIVE_AGREEMENT"),
            _check("Which admitted form is the other agreement row of the shown form?", "тво́й", "твоя́", [
                _other("тво́й", "This form is the dictionary row of the same word, not the other agreement row."),
            ], O, feature="POSSESSIVE_AGREEMENT"),
        ],
        extra=[
            (gender, "my dog' ('''моя собака''')", "собака", "example"),
            (gender, "my table' ('''мой стол''')", "стол", "example"),
            (acc, "| Our || Наш || Нашего || нашу || Наше || Наши || Наших", "нашу", "example"),
            (acc, "| Our || Наш || Нашего || нашу || Наше || Наши || Наших", "Наши", "example"),
        ],
    )
    third_span = _span(
        acc,
        "For the third-person, the masculine and neuter possessive pronouns",
        "These do not decline according to the gender or number of the modified noun.",
    )
    masculine = "masculine and neuter possessive pronouns"
    feminine = "The feminine"
    plural = "third-person plural"
    lesson.objective(
        "third",
        "Choose the admitted third-person form",
        "CONTRAST_SELECTION",
        (acc, third_span, "These do not decline according to the gender or number of the modified noun."),
        [
            _wiki(acc, third_span, "его", "его"),
            _wiki(acc, third_span, "её", "её"),
            _wiki(acc, third_span, "их", "их"),
        ],
        [
            _check("Which admitted form matches the shown category?", masculine, "его", [
                _other("её", "This form is another admitted form in the same frozen sentence."),
                _other("их", "This form is another admitted form in the same frozen sentence."),
            ], feature="THIRD_PERSON_FORM"),
            _check("Which admitted form matches the shown category?", feminine, "её", [
                _other("его", "This form is another admitted form in the same frozen sentence."),
                _other("их", "This form is another admitted form in the same frozen sentence."),
            ], feature="THIRD_PERSON_FORM"),
            _check("Which admitted form matches the shown category?", plural, "их", [
                _other("его", "This form is another admitted form in the same frozen sentence."),
                _other("её", "This form is another admitted form in the same frozen sentence."),
            ], feature="THIRD_PERSON_FORM"),
        ],
        extra=[
            (acc, third_span, masculine, "pattern"),
            (acc, third_span, feminine, "pattern"),
            (acc, third_span, plural, "pattern"),
        ],
    )
    lesson.objective(
        "object",
        "Choose the admitted object line",
        "FORM_SELECTION",
        (acc, "I like dogs - '''Я люблю собак'''", "I like dogs - '''Я люблю собак'''"),
        [
            _wiki(acc, "I like dogs - '''Я люблю собак'''", "Я люблю собак", "I like dogs"),
            _wiki(acc, "I love you\" as '''Я люблю тебя''' and be grammatically correct, it is more commonly said as '''Я тебя люблю'''", "Я тебя люблю", "I love you"),
            _wiki(acc, "I love you\" as '''Я люблю тебя''' and be grammatically correct, it is more commonly said as '''Я тебя люблю'''", "Я люблю тебя", "I love you"),
            _wiki(acc, _span(acc, "Dogs hate cats.", "Коты ненавидят собак.'''"), "Собаки ненавидят котов.", "Dogs hate cats."),
            _wiki(acc, _span(acc, "Dogs hate cats.", "Коты ненавидят собак.'''"), "Коты ненавидят собак.", "Cats hate dogs."),
        ],
        [
            _check("Which admitted line is the other order of the shown line?", "Я люблю тебя", "Я тебя люблю", [
                _other("Я люблю тебя", "This line repeats the shown line, not the other order of the same words."),
            ], feature="OBJECT_ORDER", mode="FEATURE_IN_LINE"),
            _check("Which admitted line is the other order of the shown line?", "Я тебя люблю", "Я люблю тебя", [
                _other("Я тебя люблю", "This line repeats the shown line, not the other order of the same words."),
            ], feature="OBJECT_ORDER", mode="FEATURE_IN_LINE"),
            _check("Which admitted line reverses the object of the shown line?", "Собаки ненавидят котов.", "Коты ненавидят собак.", [
                _other("Собаки ненавидят котов.", "This line repeats the shown line, not the reversed object pair."),
            ], feature="ACCUSATIVE_OBJECT", mode="FEATURE_IN_LINE"),
            _check("Which admitted line reverses the object of the shown line?", "Коты ненавидят собак.", "Собаки ненавидят котов.", [
                _other("Коты ненавидят собак.", "This line repeats the shown line, not the reversed object pair."),
            ], feature="ACCUSATIVE_OBJECT", mode="FEATURE_IN_LINE"),
        ],
    )
    feminine = "Feminine words are the only words to have their own accusative ending:"
    lesson.objective(
        "feminine",
        "Choose the admitted object form",
        "FORM_SELECTION",
        (acc, feminine, feminine),
        [
            _field("nouns.csv:книга", "sg_nom"),
            _field("nouns.csv:книга", "sg_acc"),
            _field("nouns.csv:мама", "sg_nom"),
            _field("nouns.csv:мама", "sg_acc"),
            _field("nouns.csv:женщина", "sg_nom"),
            _field("nouns.csv:женщина", "sg_acc"),
        ],
        [
            _check("Which admitted form is the object row of the shown form?", "кни́га", "кни́гу", [
                _other("кни́га", "This form is the dictionary row of the same word, not the object row."),
            ], O, feature="FEMININE_ACCUSATIVE"),
            _check("Which admitted form is the object row of the shown form?", "ма́ма", "ма́му", [
                _other("ма́ма", "This form is the dictionary row of the same word, not the object row."),
            ], O, feature="FEMININE_ACCUSATIVE"),
            _check("Which admitted form is the object row of the shown form?", "же́нщина", "же́нщину", [
                _other("же́нщина", "This form is the dictionary row of the same word, not the object row."),
            ], O, feature="FEMININE_ACCUSATIVE"),
        ],
    )
    animate = "If they're animated nouns, they follow the genitive rules"
    lesson.objective(
        "animacy",
        "Choose the admitted animate or inanimate object form",
        "CONTRAST_SELECTION",
        (acc, animate, animate),
        [
            _field("nouns.csv:стол", "sg_nom"),
            _field("nouns.csv:стол", "sg_acc"),
            _field("nouns.csv:дом", "sg_nom"),
            _field("nouns.csv:дом", "sg_acc"),
            _field("nouns.csv:брат", "sg_nom"),
            _field("nouns.csv:брат", "sg_acc"),
            _field("nouns.csv:учитель", "sg_nom"),
            _field("nouns.csv:учитель", "sg_acc"),
            _field("nouns.csv:друг", "sg_nom"),
            _field("nouns.csv:друг", "sg_acc"),
        ],
        [
            _check("Which admitted form is the object row of the shown form?", "брат", "бра́та", [
                _other("брат", "This form is the dictionary row of the same word, not the object row."),
            ], O, feature="ANIMACY"),
            _check("Which admitted form is the object row of the shown form?", "учи́тель", "учи́теля", [
                _other("учи́тель", "This form is the dictionary row of the same word, not the object row."),
            ], O, feature="ANIMACY"),
            _check("Which admitted form is the object row of the shown form?", "друг", "дру́га", [
                _other("друг", "This form is the dictionary row of the same word, not the object row."),
            ], O, feature="ANIMACY"),
            _check("Which admitted form is the object row of the shown form?", "учи́теля", "учи́тель", [
                _other("учи́теля", "This form is the object row of the same word, not the dictionary row."),
            ], O, feature="ANIMACY"),
        ],
        extra=[(acc, "So, masculine nouns denoting inanimate objects in the accusative case are the same as in the nominative case.", "So, masculine nouns denoting inanimate objects in the accusative case are the same as in the nominative case.", "instruction")],
    )
    lesson.objective(
        "pronoun",
        "Choose the admitted personal object form",
        "FORM_SELECTION",
        (acc, "| I/me || Я || Меня", "| I/me || Я || Меня"),
        [
            _wiki(acc, "| I/me || Я || Меня", "Меня", "I/me"),
            _wiki(acc, "| We/us || Мы || Нас", "Нас", "We/us"),
            _wiki(acc, "| You || Ты || Тебя", "Тебя", "You"),
            _wiki(acc, "| You || Вы || Вас", "Вас", "You"),
            _wiki(acc, "| He/him || Он || Его", "Его", "He/him"),
            _wiki(acc, "| She/her || Она || Её", "Её", "She/her"),
            _wiki(acc, "| They/them || Они || Их", "Их", "They/them"),
        ],
        [
            _check("Which admitted form is the object row of the shown form?", "Я", "Меня", [
                _other("Нас", "This form is not the object row of the shown line."),
            ], feature="PERSONAL_OBJECT_CASE", mode="FEATURE_IN_LINE"),
            _check("Which admitted form is the object row of the shown form?", "Мы", "Нас", [
                _other("Меня", "This form is not the object row of the shown line."),
            ], feature="PERSONAL_OBJECT_CASE", mode="FEATURE_IN_LINE"),
            _check("Which admitted form is the object row of the shown form?", "Она", "Её", [
                _other("Его", "This form is not the object row of the shown line."),
            ], feature="PERSONAL_OBJECT_CASE", mode="FEATURE_IN_LINE"),
            _check("Which admitted form is the object row of the shown form?", "Они", "Их", [
                _other("Вас", "This form is not the object row of the shown line."),
            ], feature="PERSONAL_OBJECT_CASE", mode="FEATURE_IN_LINE"),
        ],
        extra=[
            (acc, "| I/me || Я || Меня", "Я", "example"),
            (acc, "| We/us || Мы || Нас", "Мы", "example"),
            (acc, "| She/her || Она || Её", "Она", "example"),
            (acc, "| They/them || Они || Их", "Они", "example"),
        ],
    )
    direction_span = _span(
        acc,
        "The other use of the accusative case is with two major prepositions",
        "while the prepositional case denotes ''location'' ('I stayed ''at'' this hotel').",
    )
    lesson.objective(
        "direction",
        "Identify direction or location",
        "CASE_RECOGNITION",
        (acc, direction_span, "generally the accusative case denotes ''direction''"),
        [
            _wiki(acc, direction_span, "Я шёл в дом", "the house"),
            _wiki(acc, direction_span, "Я жил в Лондоне", "I lived in London"),
            _wiki(acc, direction_span, "Я шёл на улицу", "the street"),
            _wiki(acc, direction_span, "Оно на столе", "It's on the table"),
            _wiki(acc, "I drove to the museum. - '''Я поехал в музей.'''", "Я поехал в музей.", "I drove to the museum."),
        ],
        [
            _check("Which admitted function does the shown line use?", "Я шёл в дом", "direction", [
                _other("location", "This label names the other admitted function."),
            ], feature="DIRECTION_LOCATION"),
            _check("Which admitted function does the shown line use?", "Я жил в Лондоне", "location", [
                _other("direction", "This label names the other admitted function."),
            ], feature="DIRECTION_LOCATION"),
            _check("Which admitted function does the shown line use?", "Я шёл на улицу", "direction", [
                _other("location", "This label names the other admitted function."),
            ], feature="DIRECTION_LOCATION"),
            _check("Which admitted function does the shown line use?", "Оно на столе", "location", [
                _other("direction", "This label names the other admitted function."),
            ], feature="DIRECTION_LOCATION"),
        ],
        extra=[
            (acc, direction_span, "direction", "pattern"),
            (acc, direction_span, "location", "pattern"),
        ],
    )


def _unit_7(lesson) -> None:
    adj = "adjectives"
    default = "The default form of an adjective is its nominative, masculine, singular form, and this is the form given in dictionaries."
    lesson.objective(
        "default",
        "Choose the admitted dictionary form",
        "FORM_SELECTION",
        (adj, default, default),
        [
            _field("adjectives.csv:новый", "decl_m_nom"),
            _field("adjectives.csv:новый", "decl_f_nom"),
            _field("adjectives.csv:новый", "decl_n_nom"),
            _field("adjectives.csv:новый", "decl_pl_nom"),
            _field("adjectives.csv:хороший", "decl_m_nom"),
            _field("adjectives.csv:хороший", "decl_f_nom"),
            _field("adjectives.csv:синий", "decl_m_nom"),
            _field("adjectives.csv:синий", "decl_f_nom"),
            _field("adjectives.csv:какой", "decl_m_nom"),
            _field("adjectives.csv:какой", "decl_f_nom"),
        ],
        [
            _check("Which admitted form is the dictionary row of the shown form?", "но́вая", "но́вый", [
                _other("но́вая", "This form is the other row of the same word, not the dictionary row."),
            ], O, feature="ADJECTIVE_DICTIONARY"),
            _check("Which admitted form is the dictionary row of the shown form?", "но́вое", "но́вый", [
                _other("но́вое", "This form is the other row of the same word, not the dictionary row."),
            ], O, feature="ADJECTIVE_DICTIONARY"),
            _check("Which admitted form is the dictionary row of the shown form?", "хоро́шая", "хоро́ший", [
                _other("хоро́шая", "This form is the other row of the same word, not the dictionary row."),
            ], O, feature="ADJECTIVE_DICTIONARY"),
            _check("Which admitted form is the dictionary row of the shown form?", "си́няя", "си́ний", [
                _other("си́няя", "This form is the other row of the same word, not the dictionary row."),
            ], O, feature="ADJECTIVE_DICTIONARY"),
            _check("Which admitted form is the dictionary row of the shown form?", "кака́я", "како́й", [
                _other("кака́я", "This form is the other row of the same word, not the dictionary row."),
            ], O, feature="ADJECTIVE_DICTIONARY"),
        ],
    )
    lesson.objective(
        "endings",
        "Choose the admitted agreement line",
        "FORM_SELECTION",
        (adj, '"new pencil" is \'\'\'новый карандаш\'\'\'', '"new pencil" is \'\'\'новый карандаш\'\'\''),
        [
            _wiki(adj, '"new pencil" is \'\'\'новый карандаш\'\'\'', "новый карандаш", "new pencil"),
            _wiki(adj, '"new car" is \'\'\'новая машина\'\'\'', "новая машина", "new car"),
            _wiki(adj, '"new dress" is \'\'\'новое платье\'\'\'', "новое платье", "new dress"),
            _wiki(adj, "the dark blue table - '''синий стол.'''", "синий стол", "the dark blue table"),
            _wiki(adj, "the dark blue book - '''синяя книга'''", "синяя книга", "the dark blue book"),
            _wiki(adj, "the dark blue letter - '''синее письмо'''", "синее письмо", "the dark blue letter"),
            _wiki(adj, "the dark blue books - '''синие книги'''", "синие книги", "the dark blue books"),
            _field("adjectives.csv:новый", "decl_m_nom"),
            _field("adjectives.csv:новый", "decl_f_nom"),
            _field("adjectives.csv:новый", "decl_n_nom"),
            _field("adjectives.csv:новый", "decl_pl_nom"),
        ],
        [
            _check("Which admitted form is the other row of the shown form?", "но́вый", "но́вая", [
                _other("но́вое", "This form is another row of the same word."),
                _other("но́вый", "This form repeats the shown row of the same word."),
            ], O, feature="ADJECTIVE_AGREEMENT"),
            _check("Which admitted form is the other row of the shown form?", "но́вый", "но́вое", [
                _other("но́вая", "This form is another row of the same word."),
            ], O, feature="ADJECTIVE_AGREEMENT"),
            _check("Which admitted form is the plural row of the shown form?", "но́вый", "но́вые", [
                _other("но́вая", "This form is the other singular row of the same word, not the plural row."),
            ], O, feature="ADJECTIVE_AGREEMENT"),
            _check("Which admitted form is the dictionary row of the shown form?", "но́вая", "но́вый", [
                _other("но́вое", "This form is another row of the same word, not the dictionary row."),
            ], O, feature="ADJECTIVE_AGREEMENT"),
            _check("Which admitted form agrees with the shown word?", "машина", "но́вая", [
                _other("но́вый", "This form is the other row of the same word, not the row of the shown word."),
                _other("но́вые", "This form is the plural row of the same word, not the row of the shown word."),
            ], O, feature="ADJECTIVE_AGREEMENT"),
        ],
        extra=[
            (adj, '"new pencil" is \'\'\'новый карандаш\'\'\'', "карандаш", "example"),
            (adj, '"new car" is \'\'\'новая машина\'\'\'', "машина", "example"),
            (adj, '"new dress" is \'\'\'новое платье\'\'\'', "платье", "example"),
            (adj, "the dark blue books - '''синие книги'''", "книги", "example"),
            (adj, "the dark blue book - '''синяя книга'''", "книга", "example"),
        ],
    )
    spelling = "any unstressed '''o''' in an ending is instead written as '''e'''"
    lesson.objective(
        "spelling",
        "Identify the admitted spelling",
        "PATTERN_CLASSIFICATION",
        (adj, spelling, spelling),
        [
            _wiki(adj, "good letter' is written '''хорошее письмо'''", "хорошее письмо", "good letter"),
            _field("adjectives.csv:хороший", "decl_m_nom"),
            _field("adjectives.csv:хороший", "decl_f_nom"),
            _field("adjectives.csv:хороший", "decl_n_nom"),
            _field("adjectives.csv:хороший", "decl_pl_nom"),
        ],
        [
            _check("Which admitted form is the plural row of the shown form?", "хоро́шее", "хоро́шие", [
                _other("хоро́шее", "This form is the other row of the same word, not the plural row."),
            ], O, feature="ADJECTIVE_SPELLING"),
            _check("Which admitted form is the other row of the shown form?", "хоро́шие", "хоро́шее", [
                _other("хоро́шие", "This form is the plural row of the same word, not the other row."),
            ], O, feature="ADJECTIVE_SPELLING"),
            _check("Which admitted form is the dictionary row of the shown form?", "хоро́шее", "хоро́ший", [
                _other("хоро́шие", "This form is the plural row of the same word, not the dictionary row."),
            ], O, feature="ADJECTIVE_SPELLING"),
            _check("Which admitted form is the other row of the shown form?", "хоро́ший", "хоро́шая", [
                _other("хоро́шее", "This form is another row of the same word."),
            ], O, feature="ADJECTIVE_SPELLING"),
        ],
        extra=[
            (adj, spelling, "e", "pattern"),
            (adj, "you add '''и''', not '''ы'''", "и", "pattern"),
            (adj, "you add '''и''', not '''ы'''", "ы", "pattern"),
        ],
    )
    lesson.objective(
        "question",
        "Choose the admitted question form",
        "FORM_SELECTION",
        (adj, default, default),
        [
            _field("adjectives.csv:какой", "decl_m_nom"),
            _field("adjectives.csv:какой", "decl_f_nom"),
            _field("adjectives.csv:какой", "decl_n_nom"),
            _field("adjectives.csv:какой", "decl_pl_nom"),
        ],
        [
            _check("Which admitted form is the plural row of the shown form?", "како́й", "каки́е", [
                _other("кака́я", "This form is the other row of the same word, not the plural row."),
            ], O, feature="QUESTION_AGREEMENT"),
            _check("Which admitted form is the other singular row of the shown form?", "како́й", "како́е", [
                _other("како́й", "This form is the dictionary row of the same word, not the other singular row."),
            ], O, feature="QUESTION_AGREEMENT"),
            _check("Which admitted form is the other singular row of the shown form?", "како́й", "кака́я", [
                _other("каки́е", "This form is the plural row of the same word, not the other singular row."),
            ], O, feature="QUESTION_AGREEMENT"),
            _check("Which admitted form is the dictionary row of the shown form?", "кака́я", "како́й", [
                _other("како́е", "This form is the other row of the same word, not the dictionary row."),
            ], O, feature="QUESTION_AGREEMENT"),
        ],
    )
    students = "these good new students\" is '''эти хорошие новые студенты'''"
    lesson.objective(
        "demonstrative",
        "Choose the admitted demonstrative form",
        "FORM_SELECTION",
        (adj, students, students),
        [
            _wiki(adj, "these good new students\" is '''эти хорошие новые студенты'''", "эти хорошие новые студенты", "these good new students"),
            _field("adjectives.csv:этот", "decl_f_nom"),
            _field("adjectives.csv:этот", "decl_n_nom"),
            _field("adjectives.csv:этот", "decl_pl_nom"),
            _field("adjectives.csv:этот", "decl_f_acc"),
        ],
        [
            _check("Which admitted form does the shown line use?", "эти хорошие новые студенты", "э́ти", [
                _other("э́та", "This form is the other row of the same word, and the shown line uses another row."),
            ], O, feature="DEMONSTRATIVE_AGREEMENT", mode="FEATURE_IN_LINE"),
            _check("Which admitted form is the object row of the shown form?", "э́та", "э́ту", [
                _other("э́та", "This form is the other row of the same word, not the object row."),
            ], O, feature="DEMONSTRATIVE_AGREEMENT"),
            _check("Which admitted form is the plural row of the shown form?", "э́то", "э́ти", [
                _other("э́то", "This form is the other row of the same word, not the plural row."),
            ], O, feature="DEMONSTRATIVE_AGREEMENT"),
            _check("Which admitted form is the other singular row of the shown form?", "э́ту", "э́та", [
                _other("э́ту", "This form is the object row of the same word, not the other singular row."),
            ], O, feature="DEMONSTRATIVE_AGREEMENT"),
        ],
    )
    cinema = "They were talking about a good new cinema' - '''Они говорили о хорошем новом кино'''"
    lesson.objective(
        "place",
        "Choose the admitted place-agreement line",
        "FORM_SELECTION",
        (adj, cinema, cinema),
        [
            _wiki(adj, cinema, "Они говорили о хорошем новом кино", "They were talking about a good new cinema"),
            _wiki(adj, "They were in the old Russian school' - '''Они были в старой русской школе'''", "Они были в старой русской школе", "They were in the old Russian school"),
            _wiki(adj, "I want to live in a clean new city - '''Я хочу́ жить в чи́стом но́вом го́роде'''", "Я хочу́ жить в чи́стом но́вом го́роде", "I want to live in a clean new city"),
            _field("adjectives.csv:новый", "decl_m_nom"),
            _field("adjectives.csv:новый", "decl_m_prep"),
            _field("adjectives.csv:новый", "decl_f_acc"),
            _field("adjectives.csv:хороший", "decl_m_nom"),
            _field("adjectives.csv:хороший", "decl_m_prep"),
        ],
        [
            _check("Which admitted form is the place row of the shown form?", "но́вый", "но́вом", [
                _other("но́вую", "This form is the object row of the same word, not the place row."),
            ], O, feature="PLACE_AGREEMENT"),
            _check("Which admitted form is the dictionary row of the shown form?", "но́вом", "но́вый", [
                _other("но́вую", "This form is the object row of the same word, not the dictionary row."),
            ], O, feature="PLACE_AGREEMENT"),
            _check("Which admitted form is the place row of the shown form?", "хоро́ший", "хоро́шем", [
                _other("хоро́ший", "This form is the dictionary row of the same word, not the place row."),
            ], O, feature="PLACE_AGREEMENT"),
            _check("Which admitted form is the dictionary row of the shown form?", "хоро́шем", "хоро́ший", [
                _other("хоро́шем", "This form is the place row of the same word, not the dictionary row."),
            ], O, feature="PLACE_AGREEMENT"),
        ],
    )
    rabbit = "I want a new rabbit - '''Я хочу нового кролика.'''"
    lesson.objective(
        "objectadj",
        "Choose the admitted object-agreement line",
        "CONTRAST_SELECTION",
        (adj, rabbit, rabbit),
        [
            _wiki(adj, "I want a new chair and a new letter - '''Я хочу новый стул и новое письмо'''", "Я хочу новый стул и новое письмо", "I want a new chair and a new letter"),
            _wiki(adj, rabbit, "Я хочу нового кролика.", "I want a new rabbit"),
            _wiki(adj, "My Russian cat likes my new dog - '''Моя русская кошка любит мою новую собаку'''", "Моя русская кошка любит мою новую собаку", "My Russian cat likes my new dog"),
            _wiki("accusative", "I like these women. - '''Я люблю ''этих женщин.'''", "этих женщин", "I like these women."),
            _field("adjectives.csv:новый", "decl_m_nom"),
            _field("adjectives.csv:новый", "decl_m_gen"),
            _field("adjectives.csv:новый", "decl_f_acc"),
        ],
        [
            _check("Which admitted form is the animate object row of the shown form?", "но́вый", "но́вого", [
                _other("но́вую", "This form is the other object row of the same word, not the animate object row."),
            ], O, feature="OBJECT_ADJECTIVE"),
            _check("Which admitted form is the dictionary row of the shown form?", "но́вого", "но́вый", [
                _other("но́вую", "This form is the other object row of the same word, not the dictionary row."),
            ], O, feature="OBJECT_ADJECTIVE"),
            _check("Which admitted form is the dictionary row of the shown form?", "но́вую", "но́вый", [
                _other("но́вого", "This form is the other object row of the same word, not the dictionary row."),
            ], O, feature="OBJECT_ADJECTIVE"),
            _check("Which admitted form is the object row of the shown form?", "но́вый", "но́вую", [
                _other("но́вый", "This form is the dictionary row of the same word, not the object row."),
            ], O, feature="OBJECT_ADJECTIVE"),
        ],
    )


def _unit_8(lesson) -> None:
    gender = "gender"
    plural = "There's no 'masculine plural' and 'feminine plural' - there's just, 'plural'."
    lesson.objective(
        "plural",
        "Choose the admitted plural form",
        "FORM_SELECTION",
        (gender, plural, plural),
        [
            _field("nouns.csv:стол", "sg_nom"),
            _field("nouns.csv:стол", "pl_nom"),
            _field("nouns.csv:город", "sg_nom"),
            _field("nouns.csv:город", "pl_nom"),
            _field("nouns.csv:книга", "sg_nom"),
            _field("nouns.csv:книга", "pl_nom"),
            _field("nouns.csv:окно", "sg_nom"),
            _field("nouns.csv:окно", "pl_nom"),
            _field("nouns.csv:мама", "sg_nom"),
            _field("nouns.csv:мама", "pl_nom"),
            _field("nouns.csv:дом", "sg_nom"),
            _field("nouns.csv:дом", "pl_nom"),
            _field("nouns.csv:слово", "sg_nom"),
            _field("nouns.csv:слово", "pl_nom"),
        ],
        [
            _check("Which admitted form is the plural row of the shown form?", "сто́л", "столы́", [
                _other("сто́л", "This form is the dictionary row of the same word, not the plural row."),
            ], O, feature="PLURAL_FORMATION"),
            _check("Which admitted form is the plural row of the shown form?", "го́род", "города́", [
                _other("го́род", "This form is the dictionary row of the same word, not the plural row."),
            ], O, feature="PLURAL_FORMATION"),
            _check("Which admitted form is the plural row of the shown form?", "окно́", "о́кна", [
                _other("окно́", "This form is the dictionary row of the same word, not the plural row."),
            ], O, feature="PLURAL_FORMATION"),
            _check("Which admitted form is the plural row of the shown form?", "ма́ма", "ма́мы", [
                _other("ма́ма", "This form is the dictionary row of the same word, not the plural row."),
            ], O, feature="PLURAL_FORMATION"),
            _check("Which admitted form is the plural row of the shown form?", "до́м", "дома́", [
                _other("до́м", "This form is the dictionary row of the same word, not the plural row."),
            ], O, feature="PLURAL_FORMATION"),
            _check("Which admitted form is the plural row of the shown form?", "сло́во", "слова́", [
                _other("сло́во", "This form is the dictionary row of the same word, not the plural row."),
            ], O, feature="PLURAL_FORMATION"),
            _check("Which admitted form is the plural row of the shown form?", "кни́га", "кни́ги", [
                _other("кни́га", "This form is the dictionary row of the same word, not the plural row."),
            ], O, feature="PLURAL_FORMATION"),
        ],
    )
    add_i = "you add '''и''', not '''ы'''"
    lesson.objective(
        "spelling",
        "Identify the admitted plural spelling",
        "PATTERN_CLASSIFICATION",
        ("adjectives", add_i, add_i),
        [
            _wiki("adjectives", "good letter' is written '''хорошее письмо'''", "хорошее письмо", "good letter"),
            _field("adjectives.csv:хороший", "decl_m_nom"),
            _field("adjectives.csv:хороший", "decl_pl_nom"),
            _field("nouns.csv:книга", "sg_nom"),
            _field("nouns.csv:книга", "pl_nom"),
            _field("nouns.csv:день", "pl_nom"),
            _field("adjectives.csv:хороший", "decl_n_nom"),
        ],
        [
            _check("Which admitted form is the plural row of the shown form?", "кни́га", "кни́ги", [
                _other("кни́га", "This form is the dictionary row of the same word, not the plural row."),
            ], O, feature="PLURAL_SPELLING"),
            _check("Which admitted form is the dictionary row of the shown form?", "кни́ги", "кни́га", [
                _other("кни́ги", "This form is the plural row of the same word, not the dictionary row."),
            ], O, feature="PLURAL_SPELLING"),
            _check("Which admitted form is the plural row of the shown form?", "хоро́шее", "хоро́шие", [
                _other("хоро́шее", "This form is the other row of the same word, not the plural row."),
            ], O, feature="PLURAL_SPELLING"),
            _check("Which admitted form is the other row of the shown form?", "хоро́шие", "хоро́шее", [
                _other("хоро́шие", "This form is the plural row of the same word, not the other row."),
            ], O, feature="PLURAL_SPELLING"),
            _check("Which admitted form is the dictionary row of the shown form?", "хоро́шее", "хоро́ший", [
                _other("хоро́шие", "This form is the plural row of the same word, not the dictionary row."),
            ], O, feature="PLURAL_SPELLING"),
        ],
        extra=[
            ("adjectives", "any unstressed '''o''' in an ending is instead written as '''e'''", "e", "pattern"),
            ("adjectives", add_i, "и", "pattern"),
            ("adjectives", add_i, "ы", "pattern"),
        ],
    )
    lesson.objective(
        "irregular",
        "Choose the admitted irregular plural",
        "FORM_SELECTION",
        ("genitive", "Table, five tables - '''Стол, пять столов'''", "Table, five tables - '''Стол, пять столов'''"),
        [
            _wiki("genitive", "Table, five tables - '''Стол, пять столов'''", "пять столов", "five tables"),
            _field("nouns.csv:брат", "sg_nom"),
            _field("nouns.csv:брат", "pl_nom"),
            _field("nouns.csv:друг", "sg_nom"),
            _field("nouns.csv:друг", "pl_nom"),
            _field("nouns.csv:человек", "sg_nom"),
            _field("nouns.csv:человек", "pl_nom"),
            _field("nouns.csv:сын", "sg_nom"),
            _field("nouns.csv:сын", "pl_nom"),
        ],
        [
            _check("Which admitted form is the plural row of the shown form?", "брат", "бра́тья", [
                _other("брат", "This form is the dictionary row of the same word, not the plural row."),
            ], O, feature="IRREGULAR_PLURAL"),
            _check("Which admitted form is the plural row of the shown form?", "челове́к", "лю́ди", [
                _other("челове́к", "This form is the dictionary row of the same word, not the plural row."),
            ], O, feature="IRREGULAR_PLURAL"),
            _check("Which admitted form is the plural row of the shown form?", "друг", "друзья́", [
                _other("друг", "This form is the dictionary row of the same word, not the plural row."),
            ], O, feature="IRREGULAR_PLURAL"),
            _check("Which admitted form is the plural row of the shown form?", "сы́н", "сыновья́", [
                _other("сы́н", "This form is the dictionary row of the same word, not the plural row."),
            ], O, feature="IRREGULAR_PLURAL"),
        ],
    )
    lesson.objective(
        "agreement",
        "Choose the admitted plural agreement form",
        "FORM_SELECTION",
        ("adjectives", "the dark blue books - '''синие книги'''", "the dark blue books - '''синие книги'''"),
        [
            _wiki("adjectives", "the dark blue books - '''синие книги'''", "синие книги", "the dark blue books"),
            _wiki("accusative", "| Our || Наш || Нашего || нашу || Наше || Наши || Наших", "Наши", "Our"),
            _field("adjectives.csv:мой", "decl_m_nom"),
            _field("adjectives.csv:мой", "decl_pl_nom"),
            _field("adjectives.csv:новый", "decl_m_nom"),
            _field("adjectives.csv:новый", "decl_pl_nom"),
            _field("adjectives.csv:этот", "decl_f_nom"),
            _field("adjectives.csv:этот", "decl_pl_nom"),
        ],
        [
            _check("Which admitted form is the plural row of the shown form?", "но́вый", "но́вые", [
                _other("но́вый", "This form is the dictionary row of the same word, not the plural row."),
            ], O, feature="PLURAL_AGREEMENT"),
            _check("Which admitted form is the plural row of the shown form?", "мо́й", "мои́", [
                _other("мо́й", "This form is the dictionary row of the same word, not the plural row."),
            ], O, feature="PLURAL_AGREEMENT"),
            _check("Which admitted form is the plural row of the shown form?", "э́та", "э́ти", [
                _other("э́та", "This form is the other row of the same word, not the plural row."),
            ], O, feature="PLURAL_AGREEMENT"),
            _check("Which admitted form is the plural row of the shown form?", "но́вый", "но́вые", [
                _other("но́вый", "This form is the dictionary row of the same word, not the plural row."),
            ], O, feature="PLURAL_AGREEMENT"),
        ],
    )
    possession_span = _span(
        "genitive",
        "Another use of the genitive is in the phrase",
        "У моего́ бра́та нет де́нег",
    )
    exist_rule = "It is followed by the nominative case"
    absent_rule = "Conversely, to say 'There isn't...', 'There aren't...', we use the word"
    lesson.objective(
        "availability",
        "Choose between the admitted existence and absence lines",
        "CONTRAST_SELECTION",
        ("genitive", possession_span, absent_rule),
        [
            _wiki("genitive", possession_span, "На э́той у́лице есть францу́зы", "There are Frenchmen on this street"),
            _wiki("genitive", possession_span, "В Москве́ есть ста́рые зда́ния", "There are old buildings in Moscow"),
            _wiki("genitive", possession_span, "Нет рестора́нов", "There are no restaurants"),
            _wiki("genitive", possession_span, "В Москве́ нет ста́рых зда́ний", "There are no old buildings in Moscow"),
        ],
        [
            _check("Which admitted rule classifies the shown line?", "На э́той у́лице есть францу́зы", exist_rule, [
                _other(absent_rule, "This admitted rule is the other construction in the same frozen span."),
            ], feature="EXISTENCE_ABSENCE"),
            _check("Which admitted rule classifies the shown line?", "В Москве́ нет ста́рых зда́ний", absent_rule, [
                _other(exist_rule, "This admitted rule is the other construction in the same frozen span."),
            ], feature="EXISTENCE_ABSENCE"),
            _check("Which admitted rule classifies the shown line?", "Нет рестора́нов", absent_rule, [
                _other(exist_rule, "This admitted rule is the other construction in the same frozen span."),
            ], feature="EXISTENCE_ABSENCE"),
            _check("Which admitted rule classifies the shown line?", "В Москве́ есть ста́рые зда́ния", exist_rule, [
                _other(absent_rule, "This admitted rule is the other construction in the same frozen span."),
            ], feature="EXISTENCE_ABSENCE"),
        ],
        extra=[
            ("genitive", possession_span, exist_rule, "instruction"),
            ("genitive", possession_span, absent_rule, "instruction"),
        ],
    )


def _num(cell, form, gloss):
    return _wiki("numbers", cell, form, gloss)


def _unit_9(lesson) -> None:
    cardinals = [
        ("0 ноль", "ноль", "0"),
        ("1 оди́н (одна́, одно́, одни́)", "оди́н", "1"),
        ("2 два (две)", "два", "2"),
        ("3 три", "три", "3"),
        ("4 четы́ре", "четы́ре", "4"),
        ("5 пять", "пять", "5"),
        ("6 шесть", "шесть", "6"),
        ("7 семь", "семь", "7"),
        ("8 во́семь", "во́семь", "8"),
        ("9 де́вять", "де́вять", "9"),
        ("10 де́сять", "де́сять", "10"),
    ]
    teens = [
        ("11 оди́ннадцать", "оди́ннадцать", "11"),
        ("12 двена́дцать", "двена́дцать", "12"),
        ("13 трина́дцать", "трина́дцать", "13"),
        ("14 четы́рнадцать", "четы́рнадцать", "14"),
        ("15 пятна́дцать", "пятна́дцать", "15"),
        ("16 шестна́дцать", "шестна́дцать", "16"),
        ("17 семна́дцать", "семна́дцать", "17"),
        ("18 восемна́дцать", "восемна́дцать", "18"),
        ("19 девятна́дцать", "девятна́дцать", "19"),
        ("20 два́дцать", "два́дцать", "20"),
    ]
    tens = [
        ("30 три́дцать", "три́дцать", "30"),
        ("40 со́рок", "со́рок", "40"),
        ("50 пятьдеся́т", "пятьдеся́т", "50"),
        ("60 шестьдеся́т", "шестьдеся́т", "60"),
        ("70 се́мьдесят", "се́мьдесят", "70"),
        ("80 во́семьдесят", "во́семьдесят", "80"),
        ("90 девяно́сто", "девяно́сто", "90"),
    ]
    hundreds = [
        ("100 сто", "сто", "100"),
        ("200 две́сти", "две́сти", "200"),
        ("300 три́ста", "три́ста", "300"),
        ("400 четы́реста", "четы́реста", "400"),
        ("500 пятьсо́т", "пятьсо́т", "500"),
        ("600 шестьсо́т", "шестьсо́т", "600"),
        ("700 семьсо́т", "семьсо́т", "700"),
        ("800 восемьсо́т", "восемьсо́т", "800"),
        ("900 девятьсо́т", "девятьсо́т", "900"),
    ]
    lesson.objective(
        "cardinal",
        "Choose the admitted form for 0 through 10",
        "NUMBER_FORM_SELECTION",
        ("numbers", "1 оди́н (одна́, одно́, одни́)", "1 оди́н (одна́, одно́, одни́)"),
        [_num(*row) for row in cardinals],
        [
            _check("Which admitted form is recorded for 0?", "0", "ноль", [_other("оди́н", "This admitted form is a different number in the same table.")]),
            _check("Which admitted form is recorded for 1?", "1", "оди́н", [_other("два", "This admitted form is a different number in the same table.")]),
            _check("Which admitted form is recorded for 5?", "5", "пять", [_other("шесть", "This admitted form is a different number in the same table.")]),
            _check("Which admitted form is recorded for 10?", "10", "де́сять", [_other("де́вять", "This admitted form is a different number in the same table.")]),
            _check("Which admitted form is recorded for 4?", "4", "четы́ре", [_other("три", "This admitted form is a different number in the same table.")]),
        ],
    )
    lesson.objective(
        "teens",
        "Choose the admitted form for 11 through 20",
        "NUMBER_FORM_SELECTION",
        ("numbers", "11 оди́ннадцать", "11 оди́ннадцать"),
        [_num(*row) for row in teens],
        [
            _check("Which admitted form is recorded for 11?", "11", "оди́ннадцать", [_other("двена́дцать", "This admitted form is a different teen.")]),
            _check("Which admitted form is recorded for 14?", "14", "четы́рнадцать", [_other("пятна́дцать", "This admitted form is a different teen.")]),
            _check("Which admitted form is recorded for 19?", "19", "девятна́дцать", [_other("два́дцать", "This admitted form is the following ten, not the teen.")]),
            _check("Which admitted form is recorded for 12?", "12", "двена́дцать", [_other("два́дцать", "This admitted form is the ten, not the teen.")]),
        ],
    )
    lesson.objective(
        "tens",
        "Choose the admitted tens form",
        "NUMBER_FORM_SELECTION",
        ("numbers", "30 три́дцать", "30 три́дцать"),
        [_num(*row) for row in tens] + [_num("20 два́дцать", "два́дцать", "20")],
        [
            _check("Which admitted form is recorded for 30?", "30", "три́дцать", [_other("со́рок", "This admitted form is a different ten.")]),
            _check("Which admitted form is recorded for 50?", "50", "пятьдеся́т", [_other("шестьдеся́т", "This admitted form is a different ten.")]),
            _check("Which admitted form is recorded for 90?", "90", "девяно́сто", [_other("во́семьдесят", "This admitted form is a different ten.")]),
            _check("Which admitted form is recorded for 20?", "20", "два́дцать", [_other("двена́дцать", "This admitted form is the teen, not the ten.")]),
        ],
        extra=[("numbers", "12 двена́дцать", "двена́дцать", "example")],
    )
    lesson.objective(
        "hundreds",
        "Choose the admitted hundreds form",
        "NUMBER_FORM_SELECTION",
        ("numbers", "200 две́сти", "200 две́сти"),
        [_num(*row) for row in hundreds],
        [
            _check("Which admitted form is recorded for 100?", "100", "сто", [_other("две́сти", "This admitted form is a different hundred.")]),
            _check("Which admitted form is recorded for 300?", "300", "три́ста", [_other("три́дцать", "This admitted form is the ten, not the hundred.")]),
            _check("Which admitted form is recorded for 500?", "500", "пятьсо́т", [_other("пять", "This admitted form is the one-digit number, not the hundred.")]),
            _check("Which admitted form is recorded for 900?", "900", "девятьсо́т", [_other("девяно́сто", "This admitted form is the ten, not the hundred.")]),
        ],
        extra=[
            ("numbers", "30 три́дцать", "три́дцать", "example"),
            ("numbers", "5 пять", "пять", "example"),
            ("numbers", "90 девяно́сто", "девяно́сто", "example"),
        ],
    )
    thousand = "1,000 ты́сяча"
    band_2 = "2,000/3,000/4,000 две/три/четы́ре ты́сячи (use genitive singular, see below)"
    band_5 = "5,000 ~ 9,000 пять ~ де́вять ты́сяч (use genitive plural, see below)"
    lesson.objective(
        "thousands",
        "Choose the admitted thousands or composed form",
        "NUMBER_FORM_SELECTION",
        ("numbers", thousand, thousand),
        [
            _num(thousand, "ты́сяча", "1,000"),
            _num("21 два́дцать оди́н", "два́дцать оди́н", "21"),
            _num("101 сто оди́н", "сто оди́н", "101"),
            _num("215 две́сти пятна́дцать", "две́сти пятна́дцать", "215"),
            _num("333 три́ста три́дцать три", "три́ста три́дцать три", "333"),
            _num("1,984 ты́сяча девятьсо́т во́семьдесят четы́ре", "ты́сяча девятьсо́т во́семьдесят четы́ре", "1,984"),
            _wiki("numbers", band_2, "две/три/четы́ре ты́сячи", "2,000/3,000/4,000"),
            _wiki("numbers", band_5, "пять ~ де́вять ты́сяч", "5,000 ~ 9,000"),
        ],
        [
            _check("Which admitted form matches the shown number?", "21", "два́дцать оди́н", [
                _other("два́дцать", "This admitted form is only the ten, not the composed number."),
            ], feature="COMPOUND_21"),
            _check("Which admitted form matches the shown number?", "101", "сто оди́н", [
                _other("сто", "This admitted form is only the hundred, not the composed number."),
            ], feature="HUNDREDS_COMPOSITION"),
            _check("Which admitted form matches the shown number?", "215", "две́сти пятна́дцать", [
                _other("две́сти", "This admitted form is only the hundred, not the composed number."),
            ], feature="HUNDREDS_COMPOSITION"),
            _check("Which admitted form matches the shown number?", "1,984", "ты́сяча девятьсо́т во́семьдесят четы́ре", [
                _other("ты́сяча", "This admitted form is only the thousand, not the composed number."),
            ], feature="THOUSANDS_COMPOSITION"),
            _check("Which admitted form matches the shown number?", "2,000/3,000/4,000", "две/три/четы́ре ты́сячи", [
                _other("пять ~ де́вять ты́сяч", "This admitted form is the other thousands band."),
            ], feature="THOUSANDS_COMPOSITION"),
        ],
        extra=[
            ("numbers", "20 два́дцать", "два́дцать", "example"),
            ("numbers", "100 сто", "сто", "example"),
            ("numbers", "200 две́сти", "две́сти", "example"),
        ],
    )
    dva = "The numeral два has two forms: два for masculine and neuter items and две for feminine items."
    lesson.objective(
        "odindva",
        "Choose the admitted form of one or two",
        "CONTRAST_SELECTION",
        ("numbers", dva, dva),
        [
            _wiki("numbers", "один мальчик (one boy), одна девушка (one girl), одно животное (one animal), одни джинсы (one [pair of] jeans)", "один мальчик", "one boy"),
            _wiki("numbers", "один мальчик (one boy), одна девушка (one girl), одно животное (one animal), одни джинсы (one [pair of] jeans)", "одна девушка", "one girl"),
            _wiki("numbers", "один мальчик (one boy), одна девушка (one girl), одно животное (one animal), одни джинсы (one [pair of] jeans)", "одно животное", "one animal"),
            _wiki("numbers", "один мальчик (one boy), одна девушка (one girl), одно животное (one animal), одни джинсы (one [pair of] jeans)", "одни джинсы", "one [pair of] jeans"),
            _num("1 оди́н (одна́, одно́, одни́)", "одна́", "1"),
            _num("1 оди́н (одна́, одно́, одни́)", "одно́", "1"),
            _num("1 оди́н (одна́, одно́, одни́)", "одни́", "1"),
            _num("2 два (две)", "две", "2"),
            _field("others.csv:один", "accented"),
            _field("others.csv:два", "accented"),
        ],
        [
            _check("Which admitted form agrees with the shown word?", "мальчик", "один", [
                _other("одна", "This form is another admitted form in the same frozen line."),
            ], feature="ODIN_MASCULINE"),
            _check("Which admitted form agrees with the shown word?", "девушка", "одна", [
                _other("один", "This form is another admitted form in the same frozen line."),
            ], feature="ODIN_FEMININE"),
            _check("Which admitted form agrees with the shown word?", "животное", "одно", [
                _other("один", "This form is another admitted form in the same frozen line."),
            ], feature="ODIN_NEUTER"),
            _check("Which admitted form agrees with the shown word?", "джинсы", "одни", [
                _other("один", "This form is another admitted form in the same frozen line."),
            ], feature="ODIN_PLURAL"),
            _check("Which admitted form agrees with the shown word?", "мальчик", "один", [
                _other("одна", "This form is another admitted form in the same frozen line."),
            ], feature="ODIN_MASCULINE"),
            _check("Which admitted form agrees with the shown word?", "девушки", "две", [
                _other("два", "This form is the other admitted form of two."),
            ], feature="DVA_DVE"),
        ],
        extra=[
            ("numbers", "один мальчик (one boy), одна девушка (one girl), одно животное (one animal), одни джинсы (one [pair of] jeans)", "мальчик", "example"),
            ("numbers", "один мальчик (one boy), одна девушка (one girl), одно животное (one animal), одни джинсы (one [pair of] jeans)", "девушка", "example"),
            ("numbers", "один мальчик (one boy), одна девушка (one girl), одно животное (one animal), одни джинсы (one [pair of] jeans)", "животное", "example"),
            ("numbers", "один мальчик (one boy), одна девушка (one girl), одно животное (one animal), одни джинсы (one [pair of] jeans)", "джинсы", "example"),
            ("numbers", "один мальчик (one boy), одна девушка (one girl), одно животное (one animal), одни джинсы (one [pair of] jeans)", "один", "example"),
            ("numbers", "один мальчик (one boy), одна девушка (one girl), одно животное (one animal), одни джинсы (one [pair of] jeans)", "одна", "example"),
            ("numbers", "один мальчик (one boy), одна девушка (one girl), одно животное (one animal), одни джинсы (one [pair of] jeans)", "одно", "example"),
            ("numbers", "один мальчик (one boy), одна девушка (one girl), одно животное (one animal), одни джинсы (one [pair of] jeans)", "одни", "example"),
            ("numbers", "2 два (две)", "два", "example"),
            ("numbers", "2 два (две)", "две", "example"),
            ("numbers", "две девушки (two girls)", "девушки", "example"),
        ],
    )
    quantity_span = _span(
        "numbers",
        "If the item needs to be in the nominative or inanimate accusative case, use the following rule.",
        "двадцать два часа",
    )
    rule_scope = "If the item needs to be in the nominative or inanimate accusative case, use the following rule."
    rule_1 = "1 -> nominative"
    rule_2 = "2, 3, 4 -> genitive singular"
    rule_5 = "5 and more -> genitive plural"
    teens_rule = "11, 12, 13, 14"
    last_two = "Numerals larger than 99 follow the above rules using the last two digits."
    last_digit = "Numerals larger than twenty follow the above rules using the last digit."
    outside = "When the items at hand belong not in the nominative or inanimate accusative case, the numerals themselves must be declined"
    lesson.objective(
        "quantity",
        "Choose the admitted counted form",
        "NUMBER_FORM_SELECTION",
        ("numbers", last_digit, last_digit),
        [
            _num("один час", "один час", "один час"),
            _num("два часа", "два часа", "два часа"),
            _num("пять часов", "пять часов", "пять часов"),
            _num("двадцать один час", "двадцать один час", "двадцать один час"),
            _num("двадцать два часа", "двадцать два часа", "двадцать два часа"),
            _wiki("numbers", "две девушки (two girls)", "две девушки", "two girls"),
            _wiki("numbers", "пять девушек (five girls)", "пять девушек", "five girls"),
            _wiki("numbers", "шестнадцать рублей (sixteen rubles)", "шестнадцать рублей", "sixteen rubles"),
            _wiki("numbers", "десять копеек (ten kopeks)", "десять копеек", "ten kopeks"),
            _wiki("numbers", "сорок один год (forty-one years)", "сорок один год", "forty-one years"),
            _wiki("numbers", "пять лет", "пять лет", "пять лет"),
            _wiki("numbers", "двести три собаки (two hundred and three dogs)", "двести три собаки", "two hundred and three dogs"),
            _wiki("numbers", "двести тринадцать собак (two hundred and thirteen dogs)", "двести тринадцать собак", "two hundred and thirteen dogs"),
            _wiki("genitive", "22 kopecks - '''Два́дцать две копе́йки'''", "Два́дцать две копе́йки", "22 kopecks"),
            _wiki("genitive", "25 kopecks - '''Два́дцать пять копе́ек'''", "Два́дцать пять копе́ек", "25 kopecks"),
            _field("nouns.csv:стол", "sg_gen"),
            _field("nouns.csv:стол", "pl_gen"),
        ],
        [
            _check("Which admitted rule classifies the shown line?", "один час", rule_1, [
                _other(rule_2, "This admitted rule is the two-to-four line, not the rule for the shown line."),
                _other(rule_5, "This admitted rule is the five-and-more line, not the rule for the shown line."),
            ], feature="FINAL_DIGIT_ONE"),
            _check("Which admitted rule classifies the shown line?", "двадцать два часа", rule_2, [
                _other(rule_1, "This admitted rule is the one-item line, not the rule for the shown line."),
                _other(rule_5, "This admitted rule is the five-and-more line, not the rule for the shown line."),
            ], feature="COMPOUND_22"),
            _check("Which admitted rule classifies the shown line?", "пять часов", rule_5, [
                _other(rule_2, "This admitted rule is the two-to-four line, not the rule for the shown line."),
            ], feature="FINAL_DIGIT_FIVE_PLUS"),
            _check("Which admitted rule classifies the shown line?", "двадцать один час", rule_1, [
                _other(rule_2, "This admitted rule is the two-to-four line, not the rule for the shown line."),
                _other(rule_5, "This admitted rule is the five-and-more line, not the rule for the shown line."),
            ], feature="COMPOUND_21"),
            _check("Which admitted rule classifies the shown line?", "шестнадцать рублей", rule_5, [
                _other(rule_2, "This admitted rule is the two-to-four line, and the teen pattern overrides that rule for the shown line."),
            ], feature="TEEN_VALUE"),
            _check("Which admitted rule classifies the shown line?", "сорок один год", rule_1, [
                _other(rule_5, "This admitted rule is the five-and-more line, not the rule for the shown line."),
            ], feature="FINAL_DIGIT_ONE"),
            _check("Which admitted rule classifies the shown line?", "двести тринадцать собак", rule_5, [
                _other(rule_2, "This admitted rule is the two-to-four line, and the teen pattern overrides that rule for the shown line."),
            ], feature="TEENS_OVERRIDE"),
            _check("Which admitted rule classifies the shown line?", "десять копеек", rule_5, [
                _other(rule_1, "This admitted rule is the one-item line, not the rule for the shown line."),
            ], feature="FINAL_ZERO_OR_TEN"),
            _check("Which admitted rule classifies the shown line?", "два часа", rule_2, [
                _other(rule_5, "This admitted rule is the five-and-more line, not the rule for the shown line."),
            ], feature="FINAL_DIGIT_TWO"),
            _check("Which admitted rule classifies the shown line?", "двести три собаки", rule_2, [
                _other(rule_5, "This admitted rule is the five-and-more line, not the rule for the shown line."),
            ], feature="HUNDREDS_COMPOSITION"),
        ],
        extra=[
            ("numbers", quantity_span, rule_1, "pattern"),
            ("numbers", quantity_span, rule_2, "pattern"),
            ("numbers", quantity_span, rule_5, "pattern"),
            ("numbers", quantity_span, "один час", "example"),
            ("numbers", quantity_span, "два часа", "example"),
            ("numbers", quantity_span, "пять часов", "example"),
            ("numbers", quantity_span, "двадцать один час", "example"),
            ("numbers", quantity_span, "двадцать два часа", "example"),
            ("numbers", quantity_span, "шестнадцать рублей", "example"),
            ("numbers", quantity_span, "сорок один год", "example"),
            ("numbers", quantity_span, "двести тринадцать собак", "example"),
            ("numbers", quantity_span, "десять копеек", "example"),
            ("numbers", quantity_span, "двести три собаки", "example"),
            ("genitive", "Notice that for the ''teens'' (11, 12, 13, 14, ... 19) ending on ''-надцать'' always the last rule is to be applied, no matter what digit they end on.", teens_rule, "pattern"),
            ("numbers", "21 два́дцать оди́н", "два́дцать оди́н", "example"),
        ],
    )
    lesson.objective(
        "pattern",
        "Identify the admitted quantity pattern",
        "PATTERN_CLASSIFICATION",
        ("genitive", "Notice that for the ''teens'' (11, 12, 13, 14, ... 19) ending on ''-надцать'' always the last rule is to be applied, no matter what digit they end on.", "Notice that for the ''teens'' (11, 12, 13, 14, ... 19) ending on ''-надцать'' always the last rule is to be applied, no matter what digit they end on."),
        [
            _num("один час", "один час", "один час"),
            _num("три кошки (three cats)", "три кошки", "three cats"),
            _num("пять девушек (five girls)", "пять девушек", "five girls"),
            _wiki("numbers", "шестнадцать рублей (sixteen rubles)", "шестнадцать рублей", "sixteen rubles"),
            _wiki("numbers", "Я видел двух друзей (I saw two friends)", "Я видел двух друзей", "I saw two friends"),
            _wiki("genitive", "There is one cat - '''Есть оди́н кот'''", "Есть оди́н кот", "There is one cat"),
            _wiki("genitive", "Five roubles - '''Пять рубле́й'''", "Пять рубле́й", "Five roubles"),
        ],
        [
            _check("Which admitted rule applies to the shown line?", "один час", rule_1, [
                _other(rule_2, "This admitted rule is the two-to-four line."),
                _other(rule_5, "This admitted rule is the five-and-more line."),
            ]),
            _check("Which admitted rule applies to the shown line?", "три кошки", rule_2, [
                _other(rule_1, "This admitted rule is the one-item line."),
                _other(rule_5, "This admitted rule is the five-and-more line."),
            ]),
            _check("Which admitted rule applies to the shown line?", "пять девушек", rule_5, [
                _other(rule_2, "This admitted rule is the two-to-four line."),
            ]),
            _check("Which admitted rule classifies the shown line?", "шестнадцать рублей", rule_5, [
                _other(rule_2, "This admitted rule is the two-to-four line, and the teen pattern overrides that rule for the shown line."),
                _other(rule_1, "This admitted rule is the one-item line, and the teen pattern overrides that rule for the shown line."),
            ], feature="TEENS_OVERRIDE"),
            _check("Which admitted scope sentence covers the shown line?", "Я видел двух друзей", outside, [
                _other(rule_scope, "This admitted sentence is the other scope in the frozen paragraph."),
            ]),
        ],
        extra=[
            ("numbers", quantity_span, rule_1, "pattern"),
            ("numbers", quantity_span, rule_2, "pattern"),
            ("numbers", quantity_span, rule_5, "pattern"),
            ("numbers", quantity_span, outside, "instruction"),
            ("numbers", quantity_span, rule_scope, "instruction"),
            ("numbers", quantity_span, "один час", "example"),
            ("numbers", quantity_span, "три кошки", "example"),
            ("numbers", quantity_span, "пять девушек", "example"),
            ("numbers", quantity_span, "шестнадцать рублей", "example"),
            ("numbers", quantity_span, "Я видел двух друзей", "example"),
            ("genitive", "Notice that for the ''teens'' (11, 12, 13, 14, ... 19) ending on ''-надцать'' always the last rule is to be applied, no matter what digit they end on.", teens_rule, "pattern"),
            ("numbers", last_two, last_two, "instruction"),
        ],
    )


def _unit_10(lesson) -> None:
    gen = "genitive"
    have = "to say \"I have X\": '''У меня есть Х'''"
    possess_rule = "The thing that is possessed remains in the nominative"
    absence_rule = "Conversely, to say 'There isn't...', 'There aren't...', we use the word"
    possession_span = _span(gen, "Another use of the genitive is in the phrase", "У моего́ бра́та нет де́нег")
    lesson.objective(
        "have",
        "Choose the admitted possession line",
        "FORM_SELECTION",
        (gen, possession_span, possess_rule),
        [
            _wiki(gen, possession_span, "У меня есть Х", "I have X"),
            _wiki(gen, possession_span, "У вас есть сын?", "Do you have a son?"),
            _wiki(gen, possession_span, "Да, у меня есть сын.", "Yes, I have a son"),
            _wiki(gen, "Your husband has one friend - '''У твоего́ му́жа есть оди́н друг'''", "У твоего́ му́жа есть оди́н друг", "Your husband has one friend"),
            _wiki("adjectives", "My new husband has many friends - '''У моего нового мужа есть много друзе́й'''", "У моего нового мужа есть много друзе́й", "My new husband has many friends"),
            _wiki(gen, possession_span, "У меня́ нет сы́на", "No, I don't have a son"),
            _wiki(gen, possession_span, "У моего́ бра́та нет де́нег", "My brother doesn't have any money"),
        ],
        [
            _check("Which admitted rule classifies the shown line?", "У меня есть Х", possess_rule, [
                _other(absence_rule, "This admitted rule is the absence line, not the rule for the shown line."),
            ], feature="POSSESSION_EST", mode="RULE_CLASSIFICATION"),
            _check("Which admitted rule classifies the shown line?", "Да, у меня есть сын.", possess_rule, [
                _other(absence_rule, "This admitted rule is the absence line, not the rule for the shown line."),
            ], feature="POSSESSION_EST", mode="RULE_CLASSIFICATION"),
            _check("Which admitted rule classifies the shown line?", "У вас есть сын?", possess_rule, [
                _other(absence_rule, "This admitted rule is the absence line, not the rule for the shown line."),
            ], feature="POSSESSION_EST", mode="RULE_CLASSIFICATION"),
            _check("Which admitted rule classifies the shown line?", "У меня́ нет сы́на", absence_rule, [
                _other(possess_rule, "This admitted rule is the possession line, not the rule for the shown line."),
            ], feature="POSSESSION_EST", mode="RULE_CLASSIFICATION"),
            _check("Which admitted rule classifies the shown line?", "У моего́ бра́та нет де́нег", absence_rule, [
                _other(possess_rule, "This admitted rule is the possession line, not the rule for the shown line."),
            ], feature="POSSESSION_EST", mode="RULE_CLASSIFICATION"),
        ],
        extra=[
            (gen, possession_span, possess_rule, "instruction"),
            (gen, possession_span, absence_rule, "instruction"),
        ],
    )
    lesson.objective(
        "absence",
        "Choose the admitted absence line",
        "CONTRAST_SELECTION",
        (gen, possession_span, absence_rule),
        [
            _wiki(gen, possession_span, "У меня́ нет сы́на", "No, I don't have a son"),
            _wiki(gen, possession_span, "Нет рестора́нов", "There are no restaurants"),
            _wiki(gen, possession_span, "В Москве́ нет ста́рых зда́ний", "There are no old buildings in Moscow"),
            _wiki(gen, possession_span, "У моего́ бра́та нет де́нег", "My brother doesn't have any money"),
            _wiki(gen, possession_span, "Да, у меня есть сын.", "Yes, I have a son"),
        ],
        [
            _check("Which admitted rule classifies the shown line?", "У меня́ нет сы́на", absence_rule, [
                _other(possess_rule, "This admitted rule is the possession line, not the rule for the shown line."),
            ], feature="ABSENCE_NET", mode="RULE_CLASSIFICATION"),
            _check("Which admitted rule classifies the shown line?", "Нет рестора́нов", absence_rule, [
                _other(possess_rule, "This admitted rule is the possession line, not the rule for the shown line."),
            ], feature="ABSENCE_NET", mode="RULE_CLASSIFICATION"),
            _check("Which admitted rule classifies the shown line?", "В Москве́ нет ста́рых зда́ний", absence_rule, [
                _other(possess_rule, "This admitted rule is the possession line, not the rule for the shown line."),
            ], feature="ABSENCE_NET", mode="RULE_CLASSIFICATION"),
            _check("Which admitted rule classifies the shown line?", "Да, у меня есть сын.", possess_rule, [
                _other(absence_rule, "This admitted rule is the absence line, not the rule for the shown line."),
            ], feature="ABSENCE_NET", mode="RULE_CLASSIFICATION"),
            _check("Which admitted rule classifies the shown line?", "У моего́ бра́та нет де́нег", absence_rule, [
                _other(possess_rule, "This admitted rule is the possession line, not the rule for the shown line."),
            ], feature="ABSENCE_NET", mode="RULE_CLASSIFICATION"),
        ],
        extra=[
            (gen, possession_span, possess_rule, "instruction"),
            (gen, possession_span, absence_rule, "instruction"),
        ],
    )
    table = "'''English''' || I || We || You || You || He/she/it|| They\n|-\n! '''Nominative'''\n| Я || Мы || Ты || Вы || Он/она/оно || Они\n|-\n! '''Genitive'''\n| Меня́ || Нас || Тебя́ || Вас || Его́/её || Их"
    lesson.objective(
        "pronoun",
        "Choose the admitted genitive pronoun form",
        "FORM_SELECTION",
        (gen, "For him/it - '''Для него́'''", "For him/it - '''Для него́'''"),
        [
            _wiki(gen, "For him/it - '''Для него́'''", "Для него́", "For him/it"),
            _wiki(gen, "Without her - '''Без неё'''", "Без неё", "Without her"),
            _wiki(gen, table, "Меня́", "I"),
            _wiki(gen, table, "Его́/её", "He/she/it"),
            _wiki(gen, table, "Их", "They"),
            _wiki(gen, table, "Я", "I"),
            _wiki(gen, table, "Мы", "We"),
            _wiki(gen, table, "Нас", "We"),
            _wiki(gen, table, "Ты", "You"),
            _wiki(gen, table, "Вы", "You"),
            _wiki(gen, table, "Тебя́", "You"),
            _wiki(gen, table, "Вас", "You"),
            _wiki(gen, table, "Они", "They"),
            _field("others.csv:его", "accented"),
            _field("others.csv:её", "accented"),
            _field("others.csv:их", "accented"),
        ],
        [
            _check("Which admitted form is the genitive row of the shown form?", "Я", "Меня́", [
                _other("Нас", "This form is the genitive cell of another row in the same frozen table."),
            ], feature="GENITIVE_PRONOUN"),
            _check("Which admitted form is the genitive row of the shown form?", "Мы", "Нас", [
                _other("Меня́", "This form is the genitive cell of another row in the same frozen table."),
            ], feature="GENITIVE_PRONOUN"),
            _check("Which admitted form is the genitive row of the shown form?", "Ты", "Тебя́", [
                _other("Вас", "This form is the genitive cell of another row in the same frozen table."),
            ], feature="GENITIVE_PRONOUN"),
            _check("Which admitted form is the genitive row of the shown form?", "Вы", "Вас", [
                _other("Тебя́", "This form is the genitive cell of another row in the same frozen table."),
            ], feature="GENITIVE_PRONOUN"),
            _check("Which admitted form is the genitive row of the shown form?", "Они", "Их", [
                _other("Нас", "This form is the genitive cell of another row in the same frozen table."),
            ], feature="GENITIVE_PRONOUN"),
        ],
    )
    lesson.objective(
        "preposition",
        "Choose the admitted preposition line",
        "FORM_SELECTION",
        (gen, "I want a present for my friend - '''Я хочу́ пода́рок для моего́ дру́га'''", "I want a present for my friend - '''Я хочу́ пода́рок для моего́ дру́га'''"),
        [
            _wiki(gen, "I want a present for my friend - '''Я хочу́ пода́рок для моего́ дру́га'''", "Я хочу́ пода́рок для моего́ дру́га", "I want a present for my friend"),
            _wiki(gen, "Black coffee - '''Ко́фе без молока́'''", "Ко́фе без молока́", "Black coffee"),
            _wiki(gen, "I'm from England - '''Я из А́нглии'''", "Я из А́нглии", "I'm from England"),
            _wiki(gen, "Get away from the grass - '''Отойти от травы'''", "Отойти от травы", "Get away from the grass"),
            _wiki(gen, "I am standing by the fence - '''Я стою́ у забо́ра'''", "Я стою́ у забо́ра", "I am standing by the fence"),
            _wiki(gen, "She is sitting by the window - '''Она сидит у окна́'''", "Она сидит у окна́", "She is sitting by the window"),
            _field("others.csv:для", "accented"),
            _field("others.csv:без", "accented"),
            _field("others.csv:из", "accented"),
            _field("others.csv:от", "accented"),
            _field("others.csv:у", "accented"),
            _field("nouns.csv:молоко", "sg_nom"),
            _field("nouns.csv:молоко", "sg_gen"),
            _field("nouns.csv:окно", "sg_nom"),
            _field("nouns.csv:окно", "sg_gen"),
            _field("nouns.csv:друг", "sg_nom"),
            _field("nouns.csv:друг", "sg_gen"),
            _field("adjectives.csv:мой", "decl_m_nom"),
            _field("adjectives.csv:мой", "decl_m_gen"),
            _field("adjectives.csv:мой", "decl_f_nom"),
        ],
        [
            _check("Which admitted form is the genitive row of the shown form?", "молоко́", "молока́", [
                _other("молоко́", "This form is the dictionary row of the same word, not the genitive row."),
            ], O, feature="GENITIVE_PREPOSITION"),
            _check("Which admitted form is the genitive row of the shown form?", "окно́", "окна́", [
                _other("окно́", "This form is the dictionary row of the same word, not the genitive row."),
            ], O, feature="GENITIVE_PREPOSITION"),
            _check("Which admitted form is the genitive row of the shown form?", "друг", "дру́га", [
                _other("друг", "This form is the dictionary row of the same word, not the genitive row."),
            ], O, feature="GENITIVE_PREPOSITION"),
            _check("Which admitted form is the genitive row of the shown form?", "мо́й", "моего́", [
                _other("моя́", "This form is the other agreement row of the same word, not the genitive row."),
            ], O, feature="GENITIVE_PREPOSITION"),
            _check("Which admitted form is the dictionary row of the shown form?", "моего́", "мо́й", [
                _other("моего́", "This form is the genitive row of the same word, not the dictionary row."),
                _other("моя́", "This form is the other agreement row of the same word, not the dictionary row."),
            ], O, feature="GENITIVE_PREPOSITION"),
        ],
    )
    cheese = "A kilogram of good Russian cheese - '''Кило́ хоро́шего ру́сского сы́ра'''"
    lesson.objective(
        "adjective",
        "Choose the admitted genitive agreement form",
        "FORM_SELECTION",
        (gen, cheese, cheese),
        [
            _wiki(gen, cheese, "Кило́ хоро́шего ру́сского сы́ра", "A kilogram of good Russian cheese"),
            _wiki(gen, "A kilogram of good Russian sausage - '''Кило́ хоро́шей ру́сской колбасы́'''", "Кило́ хоро́шей ру́сской колбасы́", "A kilogram of good Russian sausage"),
            _wiki("adjectives", "I arrived from my old house - '''Я пришёл из моего старого дома'''", "Я пришёл из моего старого дома", "I arrived from my old house"),
            _field("adjectives.csv:новый", "decl_m_nom"),
            _field("adjectives.csv:новый", "decl_m_gen"),
            _field("adjectives.csv:новый", "decl_f_gen"),
            _field("adjectives.csv:новый", "decl_pl_gen"),
            _field("adjectives.csv:хороший", "decl_m_nom"),
            _field("adjectives.csv:хороший", "decl_m_gen"),
            _field("adjectives.csv:хороший", "decl_f_gen"),
        ],
        [
            _check("Which admitted form is the dictionary row of the shown form?", "но́вого", "но́вый", [
                _other("но́вой", "This form is the other genitive row of the same word."),
            ], O, feature="GENITIVE_ADJECTIVE"),
            _check("Which admitted form is the dictionary row of the shown form?", "хоро́шего", "хоро́ший", [
                _other("хоро́шей", "This form is the other genitive row of the same word."),
            ], O, feature="GENITIVE_ADJECTIVE"),
            _check("Which admitted form is the genitive row of the shown form?", "но́вый", "но́вого", [
                _other("но́вой", "This form is the other genitive row of the same word."),
                _other("но́вых", "This form is the plural genitive row of the same word."),
            ], O, feature="GENITIVE_ADJECTIVE"),
            _check("Which admitted form is the genitive row of the shown form?", "хоро́ший", "хоро́шего", [
                _other("хоро́шей", "This form is the other genitive row of the same word."),
            ], O, feature="GENITIVE_ADJECTIVE"),
        ],
    )
    women_span = _span("accusative", "These women like me.", "этих женщин.")
    lesson.objective(
        "animate",
        "Choose the admitted animate plural object form",
        "CONTRAST_SELECTION",
        ("accusative", women_span, "I like these women."),
        [
            _wiki("accusative", women_span, "этих женщин", "I like these women."),
            _wiki("accusative", women_span, "Эти женщины", "These women like me."),
            _field("nouns.csv:женщина", "pl_acc"),
            _field("nouns.csv:женщина", "pl_nom"),
            _field("nouns.csv:брат", "pl_nom"),
            _field("nouns.csv:брат", "pl_acc"),
            _field("nouns.csv:друг", "pl_acc"),
        ],
        [
            _check("Which admitted form is the object row of the shown form?", "Эти женщины", "этих женщин", [
                _other("Эти женщины", "This form is the other row of the same word, not the object row."),
            ], feature="ANIMATE_PLURAL_OBJECT"),
            _check("Which admitted form is the other plural row of the shown form?", "этих женщин", "Эти женщины", [
                _other("этих женщин", "This form is the object row of the same word, not the other plural row."),
            ], feature="ANIMATE_PLURAL_OBJECT"),
            _check("Which admitted form is the object row of the shown form?", "же́нщины", "же́нщин", [
                _other("же́нщины", "This form is the other plural row of the same word, not the object row."),
            ], O, feature="ANIMATE_PLURAL_OBJECT"),
            _check("Which admitted form is the object row of the shown form?", "бра́тья", "бра́тьев", [
                _other("бра́тья", "This form is the other plural row of the same word, not the object row."),
            ], O, feature="ANIMATE_PLURAL_OBJECT"),
        ],
        extra=[("accusative", "any plural word that's inanimate is identical to its", "any plural word that's inanimate is identical to its", "instruction")],
    )
