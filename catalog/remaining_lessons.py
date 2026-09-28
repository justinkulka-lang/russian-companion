"""Instructional chains for the remaining closeable curriculum claims.

Russian answers and distractors are exact retained substrings. This module
does not generate Russian, admit audio, or clear a HOLD claim.
"""

from __future__ import annotations

from source_transforms import html_visible
from remaining_spans import (
    CAPSTONE_SPAN,
    CLOCK_SPAN,
    CORRESPONDENCE_SPAN,
    DICTIONARY_SPAN,
    GRAMMAR20_SHA,
    LANGGEN3_SHA,
    LETTER_SHA,
    LETTER_SPAN,
    NEGATION_SPAN,
    PARTICIPLE_SPANS,
    PRONUNCIATION_SPAN,
    READING5_SHA,
    READING_SPAN,
)
from units_11_15_lessons import _shared_response_kind
from units_6_10_lessons import _check

CLOCK_SOURCE = "src-readyrussian-grammar20"
NEGATION_SOURCE = "src-readyrussian-langgen3"
LETTER_SOURCE = "src-readyrussian-letter"
READING_SOURCE = "src-readyrussian-reading5"
GUIDE_SOURCE = "src-enwiktionary-russian-entry-guidelines-92541199"
VECHERA_SOURCE = "src-ruwikisource-vechera-5336197"
PARTICIPLE_SOURCES = {
    "ostavlyaemyj": "src-enwiktionary-ostavlyaemyj-74231596",
    "obsuzhdaemyj": "src-enwiktionary-obsuzhdaemyj-74233962",
    "napisannyj": "src-enwiktionary-napisannyj-74228228",
    "prochitannyj": "src-enwiktionary-prochitannyj-74232159",
}
MEANING_PROMPT = "Which admitted English meaning matches the shown sentence?"
STRESSED_FORMS = ("оставля́емый", "обсужда́емый", "напи́санный", "прочи́танный")


def _pair(shown: str, answer: str) -> str:
    return (
        f'The frozen source pairs the shown form "{shown}" with the admitted gloss "{answer}".'
    )


def _competing(option: str, form: str, gloss: str, limitation: str) -> tuple[str, str, str]:
    """Cite the competing form's real source gloss, then say why it is not the keyed pair."""
    if form == gloss:
        raise SystemExit(f"Refusing a self-pair rationale for {form!r}")
    rationale = f'{_pair(form, gloss)[:-1]}. {limitation}'
    if option not in rationale or gloss not in rationale:
        raise SystemExit(f"Competing rationale omits {option!r} / {gloss!r}")
    return (option, rationale, rationale)


def _application(prompt, shown, answer, wrongs, feature, why, source):
    if shown not in why or answer not in why:
        raise SystemExit(f"Explanation does not name {shown!r} -> {answer!r}")
    labeled = []
    for text, rationale, difference in wrongs:
        if text == rationale or _pair(text, text) in rationale:
            raise SystemExit(f"Distractor rationale is a self-pair: {rationale}")
        if answer not in text and answer in rationale:
            raise SystemExit(f"Distractor rationale names the keyed answer: {rationale}")
        if text not in why or text in shown:
            pass
        if text in why and text not in answer:
            raise SystemExit(f"Explanation names competing option {text!r}")
        labeled.append((text, rationale, difference))
    return _check(
        prompt,
        shown,
        answer,
        labeled,
        source=source,
        feature=feature,
        why=why,
        response_kind=_shared_response_kind([answer, *[text for text, _rationale, _difference in labeled]]),
        frozen_quote=True,
    )


def _wiki_row(page, needle, ru, en):
    return ("wiki", page, needle, ru, en)


def _frame(lesson) -> None:
    lesson.builder.say(lesson.number, "The frozen source pairs the shown form")
    lesson.builder.say(lesson.number, "with the admitted gloss")


def add_clock_productive(lesson) -> None:
    _frame(lesson)
    lesson.builder.say(lesson.number, "The shown rule is")
    lesson.builder.say(lesson.number, "That states the minutes into the hour in progress.")
    lesson.builder.say(lesson.number, "That says how many minutes remain after the half hour.")
    lesson.builder.say(lesson.number, "That reads the numbers off the dial.")
    into_rule = "In Russian we can state the number of minutes into the hour which is in progress."
    remain_rule = "This is used after the half hour."
    direct_rule = "This option is always available."
    into = "в пятнадцать минут второго"
    into_en = "at the fifteenth minute of the second [hour]"
    into_direct = "в час пятнадцать минут"
    into_direct_en = "at [one] hour fifteen minutes"
    remain = "без двадцати пяти два"
    remain_en = "lacking twenty five minutes to the two"
    remain_direct = "в час тридцать пять минут"
    remain_direct_en = "at [one] hour thirty five minutes"
    direct = "в двенадцать часов десять минут"
    direct_en = "at twelve hours ten minutes"
    direct_into = "в десять минут первого"
    direct_into_en = "at ten minutes of the first"
    for text in (
        into_rule, remain_rule, direct_rule, into, into_en, into_direct, into_direct_en,
        remain, remain_en, remain_direct, remain_direct_en, direct, direct_en, direct_into, direct_into_en,
    ):
        if text not in CLOCK_SPAN:
            raise SystemExit(f"Clock application text is outside the extracted span: {text}")
    lesson.objective(
        "clock-productive",
        "Apply the admitted clock construction",
        "CONTRAST_SELECTION",
        ("grammar20", CLOCK_SPAN, into_rule),
        [
            _wiki_row("grammar20", CLOCK_SPAN, into, into_en),
            _wiki_row("grammar20", CLOCK_SPAN, remain, remain_en),
            _wiki_row("grammar20", CLOCK_SPAN, direct, direct_en),
        ],
        [
            _application(
                "Which admitted expression states the minutes into the hour in progress?",
                into_rule,
                into,
                [_competing(into_direct, into_direct, into_direct_en, "That reads the numbers off the dial.")],
                "CLOCK_MINUTES_INTO_HOUR",
                f'{_pair(into, into_en)[:-1]}. The shown rule is "{into_rule}" That states the minutes into the hour in progress.',
                CLOCK_SOURCE,
            ),
            _application(
                "Which admitted expression states the minutes that remain after the half hour?",
                remain_rule,
                remain,
                [_competing(remain_direct, remain_direct, remain_direct_en, "That reads the numbers off the dial.")],
                "CLOCK_MINUTES_REMAINING",
                f'{_pair(remain, remain_en)[:-1]}. The shown rule is "{remain_rule}" That says how many minutes remain after the half hour.',
                CLOCK_SOURCE,
            ),
            _application(
                "Which admitted expression reads the numbers off the dial?",
                direct_rule,
                direct,
                [_competing(direct_into, direct_into, direct_into_en, "That states the minutes into the hour in progress.")],
                "CLOCK_DIRECT_READING",
                f'{_pair(direct, direct_en)[:-1]}. The shown rule is "{direct_rule}" That reads the numbers off the dial.',
                CLOCK_SOURCE,
            ),
        ],
        extra=[
            ("grammar20", CLOCK_SPAN, into_rule, "label"),
            ("grammar20", CLOCK_SPAN, remain_rule, "label"),
            ("grammar20", CLOCK_SPAN, direct_rule, "label"),
            ("grammar20", CLOCK_SPAN, into_direct, "example"),
            ("grammar20", CLOCK_SPAN, into_direct_en, "label"),
            ("grammar20", CLOCK_SPAN, remain_direct, "example"),
            ("grammar20", CLOCK_SPAN, remain_direct_en, "label"),
            ("grammar20", CLOCK_SPAN, direct_into, "example"),
            ("grammar20", CLOCK_SPAN, direct_into_en, "label"),
        ],
    )
    from units_16_20_batch_a import _bind

    _bind(lesson, "clock-productive", "productive-clock-time", CLOCK_SPAN)
    if GRAMMAR20_SHA != "b6702e45884f1ab6ec0334c884393f23f1d61a7ed1fa3d764508ef1de9cd87ff":
        raise SystemExit("Grammar 20 bytes changed")


def add_negation_scope(lesson) -> None:
    _frame(lesson)
    lesson.builder.say(lesson.number, "Moving the modifier in front of a different word produces that meaning.")
    lesson.builder.say(lesson.number, "That placement negates the following word.")
    lesson.builder.say(lesson.number, "That parenthetical belongs to the other placement.")
    first = "Мне не холодно."
    first_en = "To me it is not cold."
    second = "Не мне холодно."
    second_en = "It is not to me that it is cold."
    lesson.objective(
        "placement",
        "Choose the admitted meaning for the shown placement",
        "CONTRAST_SELECTION",
        ("langgen3", NEGATION_SPAN, "Note that the modifier не negates the word which comes right after it."),
        [
            _wiki_row("langgen3", NEGATION_SPAN, first, first_en),
            _wiki_row("langgen3", NEGATION_SPAN, second, second_en),
        ],
        [
            _application(
                MEANING_PROMPT,
                first,
                first_en,
                [_competing(second_en, second, second_en, "Moving the modifier in front of a different word produces that meaning.")],
                "NEGATION_FOLLOWING_WORD",
                _pair(first, first_en),
                NEGATION_SOURCE,
            ),
            _application(
                MEANING_PROMPT,
                second,
                second_en,
                [_competing(first_en, first, first_en, "That placement negates the following word.")],
                "NEGATION_SCOPE_CONTRAST",
                _pair(second, second_en),
                NEGATION_SOURCE,
            ),
            _application(
                MEANING_PROMPT,
                second,
                second_en,
                [_competing("I am not cold.", first, "I am not cold.", "That parenthetical belongs to the other placement.")],
                "NEGATION_SCOPE_NOT_COLD",
                _pair(second, second_en),
                NEGATION_SOURCE,
            ),
        ],
        extra=[
            ("langgen3", NEGATION_SPAN, "If you were to move it in front of a different word, you would produce a different meaning.", "label"),
            ("langgen3", NEGATION_SPAN, "I am not cold.", "label"),
            ("langgen3", NEGATION_SPAN, "I am not the one who thinks it is cold.", "label"),
        ],
    )
    if not hasattr(lesson, "capabilities"):
        lesson.capabilities = []
    lesson.capabilities.append({
        "objectiveId": "obj-u24-placement",
        "supportedClaim": "negation-scope",
        "exactSpan": NEGATION_SPAN,
    })
    if LANGGEN3_SHA != "27c28efba83399060a6d7145da619d250b3428cb8c91dc6c0bb66f885af8a46c":
        raise SystemExit("Language Generator 3 bytes changed")


def add_productive_diminutive(lesson) -> None:
    """Listed name pairs do not apply a formation rule. The claim stays unresolved."""
    del lesson


def _visible_piece(span: str, start: str, end: str) -> str:
    visible = html_visible(span)
    begin = visible.find(start)
    finish = visible.find(end, begin)
    if begin < 0 or finish < 0:
        raise SystemExit(f"Visible piece missing: {start!r} .. {end!r}")
    return visible[begin:finish + len(end)]


def add_letter_writing(lesson) -> None:
    greeting = "Здравствуйте!"
    greetings = "Greetings"
    respected = "Уважаемый Иван Иванович"
    respected_en = "Respected Ivan Ivanovich"
    english_order = "In English we begin with a topic sentence and then follow with supporting facts."
    russian_order = "In Russian the facts come first with the topic sentence as the conclusion."
    dear_limit = "Strangers and people who are not closed friends should never be addressed as “dear”."
    body = "Body"
    closing = "Closing and signature"
    greeting_element = "Greeting"
    sender = "Sender’s address and date"
    structure = _visible_piece(LETTER_SPAN, "They have the same basic elements:", "Closing and signature")
    for text in (greeting, greetings, respected, respected_en, english_order, russian_order, dear_limit, body, closing, greeting_element, sender):
        if text not in html_visible(LETTER_SPAN):
            raise SystemExit(f"Letter convention text is outside the visible span: {text}")
    lesson.builder.say(lesson.number, "The source states")
    lesson.builder.say(lesson.number, "The source names")
    lesson.builder.say(lesson.number, "The shown greeting is")
    lesson.builder.say(lesson.number, "That is the address limit, not the organizational difference.")
    lesson.builder.say(lesson.number, "That is the organizational difference, not the address limit.")
    lesson.builder.say(lesson.number, "That element does not follow the body.")
    lesson.objective(
        "letter-writing",
        "Apply the admitted letter convention",
        "CONTRAST_SELECTION",
        ("letter", LETTER_SPAN, structure),
        [
            _wiki_row("letter", LETTER_SPAN, greeting, greetings),
            _wiki_row("letter", LETTER_SPAN, respected, respected_en),
        ],
        [
            _application(
                "Which admitted English statement is the Russian organizational difference?",
                english_order,
                russian_order,
                [(dear_limit, f"The source states {dear_limit} That is the address limit, not the organizational difference.", f"The source states {dear_limit} That is the address limit, not the organizational difference.")],
                "LETTER_ORGANIZATION",
                f"The source states {english_order} {russian_order}",
                LETTER_SOURCE,
            ),
            _application(
                "Which admitted English limit applies to the shown neutral greeting?",
                greeting,
                dear_limit,
                [(russian_order, f"The source states {russian_order} That is the organizational difference, not the address limit.", f"The source states {russian_order} That is the organizational difference, not the address limit.")],
                "LETTER_ADDRESS_LIMIT",
                f"The source states {dear_limit} The shown greeting is {greeting}",
                LETTER_SOURCE,
            ),
            _application(
                "Which admitted English element follows the body?",
                body,
                closing,
                [
                    (greeting_element, f"The source names {greeting_element}. That element does not follow the body.", f"The source names {greeting_element}. That element does not follow the body."),
                    (sender, f"The source names {sender}. That element does not follow the body.", f"The source names {sender}. That element does not follow the body."),
                ],
                "LETTER_STRUCTURE",
                f"The source names {body}. The source names {closing}. They have the same basic elements:",
                LETTER_SOURCE,
            ),
        ],
        extra=[
            ("letter", LETTER_SPAN, english_order, "label"),
            ("letter", LETTER_SPAN, russian_order, "label"),
            ("letter", LETTER_SPAN, dear_limit, "instruction"),
            ("letter", LETTER_SPAN, body, "label"),
            ("letter", LETTER_SPAN, closing, "label"),
            ("letter", LETTER_SPAN, greeting_element, "label"),
            ("letter", LETTER_SPAN, sender, "label"),
            ("letter", LETTER_SPAN, "They have the same basic elements:", "label"),
        ],
    )
    from units_26_30_lessons import _bind

    _bind(lesson, "obj-u26-letter-writing", "letter-writing", "letter", LETTER_SPAN)
    if LETTER_SHA != "aa93d51920f4686153636da3dc626069acd10c893c96e07ca9dd019fe4a36218":
        raise SystemExit("Letter page bytes changed")


def add_correspondence(lesson) -> None:
    enclosed = "к настоящему письму прилагается"
    enclosed_en = "enclosed please find"
    closing = "С уважением"
    closing_en = "Respectfully"
    prompt = "Which admitted English correspondence label matches the shown fragment?"
    lesson.objective(
        "correspondence",
        "Choose the admitted neutral correspondence fragment",
        "CONTRAST_SELECTION",
        ("letter", CORRESPONDENCE_SPAN, "к настоящему письму прилагается—enclosed please find"),
        [
            _wiki_row("letter", CORRESPONDENCE_SPAN, enclosed, enclosed_en),
            _wiki_row("letter", CORRESPONDENCE_SPAN, closing, closing_en),
        ],
        [
            _check(
                prompt,
                enclosed,
                enclosed_en,
                [(closing_en, f"The source pairs {closing} with {closing_en}.", f"{closing_en} closes the letter. It does not gloss the enclosure line.")],
                source=LETTER_SOURCE,
                feature="CORRESPONDENCE_ENCLOSURE",
                why=f"The source pairs {enclosed} with {enclosed_en}.",
                response_kind=_shared_response_kind([enclosed_en, closing_en]),
            ),
            _check(
                prompt,
                closing,
                closing_en,
                [(enclosed_en, f"The source pairs {enclosed} with {enclosed_en}.", f"{enclosed_en} glosses the enclosure line, not the closing.")],
                source=LETTER_SOURCE,
                feature="CORRESPONDENCE_CLOSING",
                why=f"The source pairs {closing} with {closing_en}.",
                response_kind=_shared_response_kind([closing_en, enclosed_en]),
            ),
            _check(
                "Which admitted fragment matches the shown correspondence meaning?",
                enclosed_en,
                enclosed,
                [(closing, f"The source pairs {closing} with {closing_en}.", f"{closing} is the neutral closing, not the enclosure line.")],
                source=LETTER_SOURCE,
                feature="CORRESPONDENCE_ENCLOSURE_FORM",
                why=f"The source pairs {enclosed} with {enclosed_en}.",
                response_kind=_shared_response_kind([enclosed, closing]),
            ),
        ],
    )
    from units_26_30_lessons import _bind

    _bind(lesson, "obj-u26-correspondence", "correspondence-work", "letter", CORRESPONDENCE_SPAN)


def add_participle_stress(lesson) -> None:
    """Keep the four stressed headwords as reference. Headword-versus-infinitive selection does not test stress."""
    forms = (
        ("ostavlyaemyj", "оставля́емый", "present passive participle"),
        ("obsuzhdaemyj", "обсужда́емый", "present passive participle"),
        ("napisannyj", "напи́санный", "past passive participle"),
        ("prochitannyj", "прочи́танный", "past passive participle"),
    )
    objective = "obj-u27-stress-reference"
    example_ids = []
    for index, (page, form, label) in enumerate(forms):
        span = PARTICIPLE_SPANS[page]
        if form not in span or label not in span:
            raise SystemExit(f"{page} is missing the stressed headword")
        example_id = f"ex-u27-stress-ref-{index}"
        lesson.builder.wiki(f"prov-{example_id}-ru", lesson.number, objective, page, span, form, record_type="example")
        lesson.builder.wiki(f"prov-{example_id}-en", lesson.number, objective, page, span, label, record_type="meaning")
        lesson.builder.example(example_id, form, label, objective)
        example_ids.append(example_id)
    lesson.stress_reference = example_ids
    for form in STRESSED_FORMS:
        if form not in " ".join(PARTICIPLE_SPANS.values()):
            raise SystemExit(f"Missing stressed form {form}")


def add_verbal_adverb_reading(lesson) -> None:
    tired = "устав писать"
    rest = "ложился на\u00a0диван"
    reading = "отдыхал читая"
    stood = "стоит"
    folded = "сложа руки"
    for text in (tired, rest, reading, stood, folded):
        if text not in READING_SPAN:
            raise SystemExit(f"Reading anchor missing: {text!r}")
    prompt = "Which admitted action is tied to the shown words in the passage?"
    bound = "устав писать ложился на\u00a0диван и\u00a0отдыхал читая"
    lesson.objective(
        "readings",
        "Answer from the admitted verbal-adverb passage",
        "CONTRAST_SELECTION",
        ("vechera", READING_SPAN, bound),
        [
            _wiki_row("vechera", READING_SPAN, tired, rest),
            _wiki_row("vechera", READING_SPAN, stood, folded),
        ],
        [
            _check(
                prompt,
                tired,
                rest,
                [(folded, f"The passage places {folded} with {stood}.", f"{folded} is a later posture, not the rest after {tired}.")],
                source=VECHERA_SOURCE,
                feature="VERBAL_ADVERB_REST",
                why=f"The admitted line keeps {bound} together.",
                response_kind=_shared_response_kind([rest, folded]),
            ),
            _check(
                prompt,
                stood,
                folded,
                [(reading, f"The passage places {reading} with {tired}.", f"{reading} is the earlier rest, not the posture with {stood}.")],
                source=VECHERA_SOURCE,
                feature="VERBAL_ADVERB_POSTURE",
                why=f"The passage says {stood}, {folded}.",
                response_kind=_shared_response_kind([folded, reading]),
            ),
            _check(
                prompt,
                reading,
                tired,
                [(folded, f"The passage places {folded} with {stood}.", f"{folded} does not introduce {reading}.")],
                source=VECHERA_SOURCE,
                feature="VERBAL_ADVERB_READING_LINK",
                why=f"The admitted line keeps {bound} together.",
                response_kind=_shared_response_kind([tired, folded]),
            ),
        ],
        extra=[
            ("vechera", READING_SPAN, reading, "example"),
            ("vechera", READING_SPAN, "ложился на", "example"),
            ("vechera", READING_SPAN, "диван", "example"),
            ("vechera", READING_SPAN, "и", "example"),
        ],
    )
    from units_26_30_lessons import _bind

    _bind(lesson, "obj-u28-readings", "verbal-adverb-reading", "vechera", READING_SPAN)


def add_dictionary_use(lesson) -> None:
    head = "пери́ла"
    head_en = "Headwords"
    exception = "Колорадо"
    decl = "Declensions"
    vowel = "У́"
    section = "Pronunciation"
    stress_phrase = "word with stress marking"
    for text in (head, head_en, exception, decl, "definitions", "Conjugation tables", "nominative singular"):
        if text not in DICTIONARY_SPAN:
            raise SystemExit(f"Dictionary anchor missing: {text}")
    for text in (vowel, section, stress_phrase):
        if text not in PRONUNCIATION_SPAN:
            raise SystemExit(f"Pronunciation anchor missing: {text}")
    prompt = "Which admitted English field label matches the shown form?"
    lesson.objective(
        "dictionary",
        "Recognize the admitted dictionary fields offline",
        "CONTRAST_SELECTION",
        ("guidelines", DICTIONARY_SPAN, "The nominative singular of a noun is its \"basic\" form, on whose page we include the definitions"),
        [
            _wiki_row("guidelines", DICTIONARY_SPAN, head, head_en),
            _wiki_row("guidelines", DICTIONARY_SPAN, exception, decl),
            _wiki_row("guidelines", PRONUNCIATION_SPAN, vowel, section),
        ],
        [
            _check(
                prompt,
                head,
                head_en,
                [(decl, f"The guideline places {exception} under {decl}.", f"{decl} is the declension field, not the headword field.")],
                source=GUIDE_SOURCE,
                feature="DICTIONARY_HEADWORD",
                why=f"The guideline places {head} in {head_en}. Russian entries should be read from that field.",
                response_kind=_shared_response_kind([head_en, decl]),
            ),
            _check(
                prompt,
                exception,
                decl,
                [(head_en, f"The guideline places {head} in {head_en}.", f"{head_en} is the headword field, not the declension field.")],
                source=GUIDE_SOURCE,
                feature="DICTIONARY_DECLENSION",
                why=f"The guideline says there are the occasional exceptions, e.g. {exception}, under {decl}.",
                response_kind=_shared_response_kind([decl, head_en]),
            ),
            _check(
                prompt,
                vowel,
                section,
                [(stress_phrase, f"The guideline names {stress_phrase} as the template parameter.", f"{stress_phrase} is the parameter text, not the section name.")],
                source=GUIDE_SOURCE,
                feature="DICTIONARY_STRESS_MARK",
                why=f"The stressed vowels may be copied, including {vowel}, in the {section} section.",
                response_kind=_shared_response_kind([section, stress_phrase]),
            ),
        ],
        extra=[
            ("guidelines", DICTIONARY_SPAN, "definitions", "label"),
            ("guidelines", DICTIONARY_SPAN, "Conjugation tables", "label"),
            ("guidelines", PRONUNCIATION_SPAN, stress_phrase, "label"),
        ],
    )
    from units_26_30_lessons import _bind

    _bind(lesson, "obj-u30-dictionary", "dictionary-use", "guidelines", DICTIONARY_SPAN)


def add_capstone_reading(lesson) -> None:
    girl = "двухле́тняя де́вочка"
    carried = "нёс де́вочку"
    doll = "несла́ большу́ю ку́клу"
    laughed = "все расхохота́лись"
    sent = "Пожа́рные посла́ли Бо́ба"
    for text in (girl, carried, doll, laughed, sent, "в до́ме оста́лась двухле́тняя де́вочка"):
        if text not in CAPSTONE_SPAN:
            raise SystemExit(f"Capstone anchor missing: {text}")
    prompt = "Which admitted story event matches the shown sentence?"
    remained = "в до́ме оста́лась двухле́тняя де́вочка"
    lesson.objective(
        "capstone",
        "Answer from the admitted capstone story",
        "CONTRAST_SELECTION",
        ("reading5", CAPSTONE_SPAN, "Че́рез пять мину́т он вы́бежал из до́ма и в зуба́х за рубашо́нку нёс де́вочку."),
        [
            _wiki_row("reading5", CAPSTONE_SPAN, carried, girl),
            _wiki_row("reading5", CAPSTONE_SPAN, doll, laughed),
        ],
        [
            _check(
                prompt,
                remained,
                carried,
                [(doll, f"The story later says she {doll}.", f"{doll} is the later discovery, not the rescue of {girl}.")],
                source=READING_SOURCE,
                feature="CAPSTONE_RESCUE",
                why=f"The story says a {girl} remained, and then he {carried}.",
                response_kind=_shared_response_kind([carried, doll]),
            ),
            _check(
                "Which admitted reaction follows the shown discovery in the story?",
                doll,
                laughed,
                [(carried, f"The story says he {carried} before the doll.", f"{carried} is the earlier rescue, not the reaction to the doll.")],
                source=READING_SOURCE,
                feature="CAPSTONE_REACTION",
                why=f"The story says she {doll}, and then everyone {laughed}.",
                response_kind=_shared_response_kind([laughed, carried]),
            ),
            _check(
                "Which admitted rescue happens earlier in the story than the shown discovery?",
                doll,
                carried,
                [(laughed, f"The story says everyone {laughed} after the doll.", f"{laughed} comes with the doll, not before it.")],
                source=READING_SOURCE,
                feature="CAPSTONE_SEQUENCE",
                why=f"The story says he {carried} and later she {doll}.",
                response_kind=_shared_response_kind([carried, laughed]),
            ),
        ],
        extra=[
            ("reading5", CAPSTONE_SPAN, sent, "example"),
            ("reading5", CAPSTONE_SPAN, remained, "example"),
        ],
    )
    from units_26_30_lessons import _bind

    _bind(lesson, "obj-u30-capstone", "capstone-reading", "reading5", CAPSTONE_SPAN)
    if READING5_SHA != "6f6b42fb644a08f5accc488260f6a3b30b17b0d7da3ee97fa384f803db70d44d":
        raise SystemExit("Reading 5 bytes changed")


def add_late(lesson, number: int) -> None:
    if number == 26:
        add_letter_writing(lesson)
        add_correspondence(lesson)
    elif number == 27:
        add_participle_stress(lesson)
    elif number == 28:
        add_verbal_adverb_reading(lesson)
    elif number == 30:
        add_capstone_reading(lesson)
