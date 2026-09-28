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


def _gloss(shown, answer, wrongs, feature, source):
    why = _pair(shown, answer)
    labeled = []
    for text in wrongs:
        rationale = _pair(text, text)
        if answer in rationale and answer not in text:
            raise SystemExit(f"Distractor rationale names the keyed answer: {rationale}")
        labeled.append((text, rationale, rationale))
    return _check(
        MEANING_PROMPT,
        shown,
        answer,
        labeled,
        source=source,
        feature=feature,
        why=why,
        response_kind=_shared_response_kind([answer, *wrongs]),
        frozen_quote=True,
    )


def _wiki_row(page, needle, ru, en):
    return ("wiki", page, needle, ru, en)


def _frame(lesson) -> None:
    lesson.builder.say(lesson.number, "The frozen source pairs the shown form")
    lesson.builder.say(lesson.number, "with the admitted gloss")


def add_clock_productive(lesson) -> None:
    _frame(lesson)
    direct = "в двенадцать часов десять минут"
    direct_en = "at twelve hours ten minutes"
    into = "в десять минут первого"
    into_en = "at ten minutes of the first"
    begin = "в начале первого часа"
    begin_en = "at the beginning of the first hour"
    system = "This makes sense up to the half hour."
    minutes = "minutes into the hour"
    remain = "minutes remain until the little hand"
    lesson.objective(
        "clock-productive",
        "Choose the admitted clock meaning for the shown system",
        "CONTRAST_SELECTION",
        ("grammar20", CLOCK_SPAN, "In Russian we can state the number of minutes into the hour which is in progress."),
        [
            _wiki_row("grammar20", CLOCK_SPAN, direct, direct_en),
            _wiki_row("grammar20", CLOCK_SPAN, into, into_en),
            _wiki_row("grammar20", CLOCK_SPAN, begin, begin_en),
        ],
        [
            _gloss(direct, direct_en, [into_en, begin_en], "CLOCK_DIRECT_READING", CLOCK_SOURCE),
            _gloss(into, into_en, [direct_en, begin_en], "CLOCK_MINUTES_OF_HOUR", CLOCK_SOURCE),
            _gloss(system, minutes, [remain, begin_en], "CLOCK_MINUTES_INTO_HOUR", CLOCK_SOURCE),
        ],
        extra=[
            ("grammar20", CLOCK_SPAN, "We can read off the numbers shown on the dial.", "label"),
            ("grammar20", CLOCK_SPAN, "We can say how many minutes remain until the little hand passes the next number on the dial.", "label"),
            ("grammar20", CLOCK_SPAN, system, "label"),
            ("grammar20", CLOCK_SPAN, minutes, "label"),
            ("grammar20", CLOCK_SPAN, remain, "label"),
        ],
    )
    from units_16_20_batch_a import _bind

    _bind(lesson, "clock-productive", "productive-clock-time", CLOCK_SPAN)
    if GRAMMAR20_SHA != "b6702e45884f1ab6ec0334c884393f23f1d61a7ed1fa3d764508ef1de9cd87ff":
        raise SystemExit("Grammar 20 bytes changed")


def add_negation_scope(lesson) -> None:
    _frame(lesson)
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
            _gloss(first, first_en, [second_en], "NEGATION_FOLLOWING_WORD", NEGATION_SOURCE),
            _gloss(second, second_en, [first_en], "NEGATION_SCOPE_CONTRAST", NEGATION_SOURCE),
            _gloss(second, second_en, ["I am not cold."], "NEGATION_SCOPE_NOT_COLD", NEGATION_SOURCE),
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
    from units_21_25_lessons import LICHNOE_SOURCE, _span

    formation = _span(
        "lichnoe-imya",
        "В словообразовании уменьшительных и ласкательных форм имён участвуют",
        "считаются стилистически приниженными",
    )
    base = "Маша"
    short = "Машенька"
    full = "Марюня"
    dismissive = "Сашка"
    full_name = "Мария"
    limitation = "Уменьшительные имена, образованные при помощи суффикса -к-, несут в себе оттенок пренебрежения"
    for text in (base, short, full, dismissive, full_name, limitation):
        if text not in formation:
            raise SystemExit(f"Diminutive chain text is outside the frozen span: {text}")
    prompt = "Which admitted short form applies the shown base?"
    lesson.objective(
        "productive-formation",
        "Apply the admitted short-form pattern to the shown base",
        "CONTRAST_SELECTION",
        ("lichnoe-imya", formation, limitation),
        [
            _wiki_row("lichnoe-imya", formation, base, short),
            _wiki_row("lichnoe-imya", formation, full_name, full),
        ],
        [
            _check(
                prompt,
                base,
                short,
                [
                    (full, f"The source forms {full} from {full_name}.", f"{full} comes from the full form, not from {base}."),
                    (dismissive, f"The source gives {dismissive} with the dismissive suffix.", f"{dismissive} is the dismissive suffix example, not the short form of {base}."),
                ],
                source=LICHNOE_SOURCE,
                feature="DIMINUTIVE_SHORT_FORM_APPLICATION",
                why=f"The source shows {short}, \u041c\u0430\u0448\u0443\u043b\u044f as the short-form pair from {base}.",
                response_kind=_shared_response_kind([short, full, dismissive]),
            ),
            _check(
                prompt,
                base,
                short,
                [
                    (full, f"The source forms {full} from {full_name}.", f"{full} is not the short form named for {base}."),
                    (dismissive, f"The source gives {dismissive} with the dismissive suffix.", f"{dismissive} carries the dismissive suffix, unlike {short}."),
                ],
                source=LICHNOE_SOURCE,
                feature="DIMINUTIVE_SHORT_FORM_APPLICATION",
                why=f"The source shows {short}, \u041c\u0430\u0448\u0443\u043b\u044f as the short-form pair from {base}.",
                response_kind=_shared_response_kind([short, full, dismissive]),
            ),
            _check(
                prompt,
                full_name,
                full,
                [
                    (short, f"The source shows the short-form arrow from {base} to {short}.", f"{short} applies to {base}, not to {full_name}."),
                    (dismissive, f"The source gives {dismissive} with the dismissive suffix.", f"{dismissive} is not the full-form example {full}."),
                ],
                source=LICHNOE_SOURCE,
                feature="DIMINUTIVE_FULL_FORM_APPLICATION",
                why=f"The source lists {full}, \u041c\u0430\u0440\u0443\u043d\u044f from the full form {full_name}.",
                response_kind=_shared_response_kind([full, short, dismissive]),
            ),
        ],
        extra=[
            ("lichnoe-imya", formation, dismissive, "example"),
            ("lichnoe-imya", formation, "\u041c\u0430\u0448\u0435\u043d\u044c\u043a\u0430, \u041c\u0430\u0448\u0443\u043b\u044f", "example"),
            ("lichnoe-imya", formation, "\u041c\u0430\u0440\u044e\u043d\u044f, \u041c\u0430\u0440\u0443\u043d\u044f", "example"),
            ("lichnoe-imya", formation, "\u0412\u0430\u0441\u044c\u043a\u0430, \u041c\u0430\u0440\u0438\u043d\u043a\u0430, \u0410\u043b\u043a\u0430", "label"),
            ("lichnoe-imya", formation, limitation, "instruction"),
        ],
    )
    if not hasattr(lesson, "capabilities"):
        lesson.capabilities = []
    lesson.capabilities.append({
        "objectiveId": "obj-u25-productive-formation",
        "supportedClaim": "productive-diminutive",
        "exactSpan": formation,
    })


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
    structure = _visible_piece(LETTER_SPAN, "They have the same basic elements:", "Closing and signature")
    limit = _visible_piece(
        LETTER_SPAN,
        "Strangers and people who are not closed friends",
        "“dear”.",
    )
    prompt = "Which admitted English greeting label matches the shown greeting?"
    lesson.objective(
        "letter-writing",
        "Choose the admitted neutral greeting for the shown letter line",
        "CONTRAST_SELECTION",
        ("letter", LETTER_SPAN, structure),
        [
            _wiki_row("letter", LETTER_SPAN, greeting, greetings),
            _wiki_row("letter", LETTER_SPAN, respected, respected_en),
        ],
        [
            _check(
                prompt,
                greeting,
                greetings,
                [(respected_en, f"The source glosses the respected-name line as {respected_en}.", f"{respected_en} names the respected line, not the greeting {greeting}")],
                source=LETTER_SOURCE,
                feature="LETTER_GREETING",
                why=f"The source pairs {greeting} with {greetings}. Strangers and people who are not closed friends should never be addressed as dear.",
                response_kind=_shared_response_kind([greetings, respected_en]),
            ),
            _check(
                prompt,
                respected,
                respected_en,
                [(greetings, f"The source pairs {greeting} with {greetings}.", f"{greetings} is the neutral greeting, not the respected-name line.")],
                source=LETTER_SOURCE,
                feature="LETTER_RESPECTED_NAME",
                why=f"The source pairs {respected} with {respected_en}.",
                response_kind=_shared_response_kind([respected_en, greetings]),
            ),
            _check(
                prompt,
                greeting,
                greetings,
                [("Closing and signature", "The source names Closing and signature as a separate letter element.", "Closing and signature is a letter element, not the greeting meaning.")],
                source=LETTER_SOURCE,
                feature="LETTER_GREETING_NOT_CLOSING",
                why=f"The source pairs {greeting} with {greetings}. They have the same basic elements.",
                response_kind=_shared_response_kind([greetings, "Closing and signature"]),
            ),
        ],
        extra=[
            ("letter", LETTER_SPAN, limit, "instruction"),
            ("letter", LETTER_SPAN, "Closing and signature", "label"),
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
    forms = (
        ("ostavlyaemyj", "оставля́емый", "оставля́ть", "present passive participle"),
        ("obsuzhdaemyj", "обсужда́емый", "обсужда́ть", "present passive participle"),
        ("napisannyj", "напи́санный", "написа́ть", "past passive participle"),
        ("prochitannyj", "прочи́танный", "прочита́ть", "past passive participle"),
    )
    examples = []
    for page, form, verb, label in forms:
        span = PARTICIPLE_SPANS[page]
        for text in (form, verb, label):
            if text not in span:
                raise SystemExit(f"{page} is missing {text}")
        examples.append(_wiki_row(page, span, form, label))
    host_page, host_form, host_verb, host_label = forms[0]
    host_span = PARTICIPLE_SPANS[host_page]
    past_page, past_form, past_verb, past_label = forms[2]
    past_span = PARTICIPLE_SPANS[past_page]
    prompt = "Which admitted stressed form matches the shown participle label?"
    third_page, third_form, third_verb, third_label = forms[1]
    third_span = PARTICIPLE_SPANS[third_page]
    lesson.objective(
        "stress",
        "Recognize the admitted stressed participle form",
        "CONTRAST_SELECTION",
        (host_page, host_span, host_label),
        examples,
        [
            _check(
                prompt,
                host_label,
                host_form,
                [(host_verb, f"The entry names {host_verb} inside the participle template.", f"{host_verb} is not the head form.")],
                source=PARTICIPLE_SOURCES[host_page],
                feature="PARTICIPLE_STRESSED_HEAD",
                why=f"The entry head {host_form} is the {host_label}.",
                response_kind=_shared_response_kind([host_form, host_verb]),
            ),
            _check(
                prompt,
                host_label,
                host_form,
                [(host_verb, f"The entry names {host_verb} inside the participle template.", f"{host_verb} is inside the template, not the head form.")],
                source=PARTICIPLE_SOURCES[host_page],
                feature="PARTICIPLE_STRESSED_FORM",
                why=f"The entry head {host_form} is the {host_label}.",
                response_kind=_shared_response_kind([host_form, host_verb]),
            ),
            _check(
                prompt,
                past_label,
                past_form,
                [(past_verb, f"The entry names {past_verb} inside the participle template.", f"{past_verb} is not the head form.")],
                source=PARTICIPLE_SOURCES[past_page],
                feature="PARTICIPLE_PAST_STRESSED_FORM",
                why=f"The entry head {past_form} is the {past_label}.",
                response_kind=_shared_response_kind([past_form, past_verb]),
            ),
        ],
        extra=[
            (host_page, host_span, host_verb, "example"),
            (past_page, past_span, past_verb, "example"),
            (third_page, third_span, third_form, "example"),
            (third_page, third_span, third_verb, "example"),
            ("prochitannyj", PARTICIPLE_SPANS["prochitannyj"], "прочи́танный", "example"),
            ("prochitannyj", PARTICIPLE_SPANS["prochitannyj"], "прочита́ть", "example"),
        ],
    )
    from units_26_30_lessons import _bind

    _bind(lesson, "obj-u27-stress", "participle-stress", host_page, host_span)
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
        add_dictionary_use(lesson)
        add_capstone_reading(lesson)
