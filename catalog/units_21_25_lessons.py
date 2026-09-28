"""Typed Units 21, 22, 24, and 25 checks. Every Russian span is an exact frozen substring.

Each check asks for one distinction the frozen source states. It does not ask the
learner to recall a line label. Unit 23 has no lesson body here. This module does
not conjugate, decline, or compose new Russian.
"""

from pathlib import Path

from units_11_15_lessons import _shared_response_kind, _wiki
from units_6_10_lessons import _check

ROOT = Path(__file__).resolve().parents[1]
_PAGE_FILE = {
    "possessive": ROOT / "catalog/sources/units-21-25/wikibooks/possessive-pronouns-4220315.txt",
    "numbers": ROOT / "catalog/sources/units-6-10/wikibooks/numbers-4371358.txt",
    "verbs": ROOT / "catalog/sources/units-3-5/wikibooks/verbs-4233876.txt",
    "cases": ROOT / "catalog/sources/units-3-5/wikibooks/cases-4195114.txt",
    "names": ROOT / "catalog/sources/units-21-25/wikibooks/names-4660786.txt",
    "useful": ROOT / "catalog/sources/units-21-25/wikibooks/useful-words-3353859.txt",
    "lichnoe-imya": ROOT / "catalog/sources/units-21-25/supplemental/lichnoe-imya-154915954.txt",
}
LICHNOE_SOURCE = "src-ruwiki-lichnoe-imya-154915954"
_CIRCULAR = (
    "does this answer",
    "does this row",
    "does the admitted record show the governed contrast",
)


def _span(page: str, start: str, end: str) -> str:
    text = _PAGE_FILE[page].read_text(encoding="utf-8")
    begin = text.find(start)
    finish = text.find(end, begin if begin >= 0 else 0)
    if begin < 0 or finish < 0:
        raise SystemExit(f"Frozen span is missing on {page}: {start!r} .. {end!r}")
    return text[begin:finish + len(end)]


def _distinction(prompt, shown, answer, wrongs, feature, why):
    folded = why.casefold()
    if why.endswith("?") or any(phrase in folded for phrase in _CIRCULAR):
        raise SystemExit(f"Circular feedback is not a source distinction: {why!r}")
    if "which admitted line is the " in prompt.casefold() or "which admitted english label is the " in prompt.casefold():
        raise SystemExit(f"Prompt discloses a line label: {prompt!r}")
    labels = [text for text, _rationale, _difference in wrongs]
    if len({rationale for _text, rationale, _difference in wrongs}) != len(wrongs):
        raise SystemExit(f"Repeated distractor rationale: {prompt!r}")
    if len({difference for _text, _rationale, difference in wrongs}) != len(wrongs):
        raise SystemExit(f"Repeated distractor difference: {prompt!r}")
    return _check(
        prompt,
        shown,
        answer,
        wrongs,
        feature=feature,
        why=why,
        response_kind=_shared_response_kind([answer, *labels]),
    )


def populate(lesson, number: int) -> None:
    {
        21: _unit_21,
        22: _unit_22,
        24: _unit_24,
        25: _unit_25,
    }[number](lesson)


def _unit_21(lesson) -> None:
    agreement = _span(
        "possessive",
        "The reflexive pronouns (свой",
        "(Anna put on her own jeans.)",
    )
    film = "Бо́ря рассказа́л о своём люби́мом фи́льме."
    anna_film = "А́нна рассказа́ла о своём люби́мом фи́льме."
    book = "Свое́й кни́ги она́ никогда́ не написа́ла."
    he_book = "Свое́й кни́ги он никогда́ не написа́л."
    jeans = "Бо́ря наде́л свои́ джи́нсы."
    anna_jeans = "А́нна наде́ла свои́ джи́нсы."
    lesson.objective(
        "svoy",
        "Choose the form that follows the object in possession",
        "CONTRAST_SELECTION",
        (
            "possessive",
            agreement,
            "The reflexive pronouns (свой his/her/its own m., своя his/her/its own f., своё his/her/its own n., свои his/her/its own pl.), as well as all possessive nouns except the 3rd person ones take the gender and the quantity of the object in possession:",
        ),
        [
            _wiki("possessive", agreement, film, "(Borya told about his favorite movie.)"),
            _wiki("possessive", agreement, anna_film, "(Anna told about her favorite movie.)"),
            _wiki("possessive", agreement, book, "(She never wrote her own book.)"),
            _wiki("possessive", agreement, he_book, "(He never wrote his own book.)"),
            _wiki("possessive", agreement, jeans, "(Borya put on his own jeans.)"),
            _wiki("possessive", agreement, anna_jeans, "(Anna put on her own jeans.)"),
        ],
        [
            _distinction(
                "Which admitted line keeps the same movie object for the other speaker?",
                film,
                anna_film,
                [
                    (book, "This line follows the book object, not the movie object in the shown line.", "The frozen book line does not keep the movie object."),
                    (jeans, "This line follows the jeans object, not the movie object in the shown line.", "The frozen jeans line does not keep the movie object."),
                ],
                feature="OBJECT_POSSESSION_FORM",
                why="The frozen note says the form follows the quantity of the object in possession. This line keeps the movie object.",
            ),
            _distinction(
                "Which admitted line keeps the same jeans object for the other speaker?",
                jeans,
                anna_jeans,
                [
                    (anna_film, "This line follows the movie object, not the jeans object in the shown line.", "The frozen movie line does not keep the jeans object."),
                    (book, "This line follows the book object, not the jeans object in the shown line.", "The frozen book line does not keep the jeans object."),
                ],
                feature="OBJECT_POSSESSION_FORM",
                why="The frozen note says the form follows the quantity of the object in possession. This line keeps the jeans object.",
            ),
            _distinction(
                "Which admitted line keeps the same book object for the other speaker?",
                he_book,
                book,
                [
                    (anna_film, "This line follows the movie object, not the book object in the shown line.", "The frozen movie line does not keep the book object."),
                    (anna_jeans, "This line follows the jeans object, not the book object in the shown line.", "The frozen jeans line does not keep the book object."),
                ],
                feature="OBJECT_POSSESSION_FORM",
                why="The frozen note says the form follows the quantity of the object in possession. This line keeps the book object.",
            ),
        ],
    )


def _unit_22(lesson) -> None:
    nouns = _span("numbers", "The numeral один (1) should be used", "(sixteen rubles).")
    compounds = _span(
        "numbers",
        "Numerals larger than twenty follow the above rules using the last digit.",
        "(sixty-nine words).",
    )
    one_boy = "один мальчик"
    one_girl = "одна девушка"
    two_girls = "две девушки"
    three_cats = "три кошки"
    five_girls = "пять девушек"
    ten_kopeks = "десять копеек"
    glasses = "тридцать три рюмки"
    forty_one = "сорок один год"
    sixty_nine = "шестьдесят девять слов"
    lesson.objective(
        "noun-forms",
        "Choose the admitted item form for the numeral group",
        "FORM_SELECTION",
        ("numbers", nouns, "The numeral один (1) should be used in the proper form to indicate gender (and, number, believe it or not)."),
        [
            _wiki("numbers", nouns, one_boy, "one boy"),
            _wiki("numbers", nouns, one_girl, "one girl"),
            _wiki("numbers", nouns, two_girls, "two girls"),
            _wiki("numbers", nouns, three_cats, "three cats"),
            _wiki("numbers", nouns, five_girls, "five girls"),
            _wiki("numbers", nouns, ten_kopeks, "ten kopeks"),
        ],
        [
            _distinction(
                "The frozen note says a form of one should be used in the proper form. Which admitted example is the other one with one?",
                one_girl,
                one_boy,
                [
                    (two_girls, "The frozen examples place two girls with two, not with one.", "Two girls follows two, not a form of one."),
                    (five_girls, "The frozen examples place five girls with five, not with one.", "Five girls follows five, not a form of one."),
                ],
                feature="ONE_ITEM_FORM",
                why="The frozen note says a form of one should be used in the proper form. This line is the other example with one.",
            ),
            _distinction(
                "The frozen note groups two, three, and four together. Which admitted line is another example from that group?",
                two_girls,
                three_cats,
                [
                    (one_boy, "The frozen examples place one boy with a form of one, not with two, three, or four.", "One boy follows a form of one, not two, three, or four."),
                    (five_girls, "The frozen examples place five girls with the five group, not with two, three, or four.", "Five girls follows the five group, not two, three, or four."),
                ],
                feature="TWO_ITEM_FORM",
                why="The frozen note says two, three, and four require the following item to be presented together. This line is the three example.",
            ),
            _distinction(
                "The frozen note groups five through twenty together. Which admitted line is another example from that group?",
                five_girls,
                ten_kopeks,
                [
                    (one_boy, "The frozen examples place one boy with a form of one, not with the five group.", "One boy follows a form of one, not the five group."),
                    (two_girls, "The frozen examples place two girls with two, not with the five group.", "Two girls follows two, not the five group."),
                ],
                feature="FIVE_ITEM_FORM",
                why="The frozen note says these numerals require their attached items in the later group. This line is another example from that group.",
            ),
        ],
    )
    lesson.objective(
        "compounds",
        "Choose the admitted example by its last digit",
        "FORM_SELECTION",
        ("numbers", compounds, "Numerals larger than twenty follow the above rules using the last digit."),
        [
            _wiki("numbers", compounds, glasses, "thirty-three shot glasses"),
            _wiki("numbers", compounds, forty_one, "forty-one years"),
            _wiki("numbers", compounds, sixty_nine, "sixty-nine words"),
        ],
        [
            _distinction(
                "The frozen note says numerals larger than twenty follow the above rules using the last digit. Which admitted example ends in one?",
                glasses,
                forty_one,
                [
                    (glasses, "Thirty-three shot glasses ends in three, so it is not the example that ends in one.", "This example ends in three, not in one."),
                    (sixty_nine, "Sixty-nine words ends in nine, so it is not the example that ends in one.", "This example ends in nine, not in one."),
                ],
                feature="LAST_DIGIT_ONE",
                why="The frozen note says numerals larger than twenty follow the above rules using the last digit. This example ends in one.",
            ),
            _distinction(
                "The frozen note says numerals larger than twenty follow the above rules using the last digit. Which admitted example ends in nine?",
                forty_one,
                sixty_nine,
                [
                    (forty_one, "Forty-one years ends in one, so it is not the example that ends in nine.", "This example ends in one, not in nine."),
                    (glasses, "Thirty-three shot glasses ends in three, so it is not the example that ends in nine.", "This example ends in three, not in nine."),
                ],
                feature="LAST_DIGIT_NINE",
                why="The frozen note says numerals larger than twenty follow the above rules using the last digit. This example ends in nine.",
            ),
            _distinction(
                "The frozen note says numerals larger than twenty follow the above rules using the last digit. Which admitted example ends in three?",
                sixty_nine,
                glasses,
                [
                    (sixty_nine, "Sixty-nine words ends in nine, so it is not the example that ends in three.", "This example ends in nine, not in three."),
                    (forty_one, "Forty-one years ends in one, so it is not the example that ends in three.", "This example ends in one, not in three."),
                ],
                feature="LAST_DIGIT_THREE",
                why="The frozen note says numerals larger than twenty follow the above rules using the last digit. This example ends in three.",
            ),
        ],
    )


def _unit_24(lesson) -> None:
    negation = _span("verbs", "To negate a verb, simply add '''не'''", "different meaning from the '''нет''' that means 'no'.")
    absence = _span("cases", "If it isn't there, we use '''нет''' followed by the genitive.", "followed by the genitive.")
    yes_speaking = "Да, говорю́"
    not_speaking = "Нет, не говорю́"
    question = "Вы говори́те?"
    not_student = "Он не студент"
    not_coming = "Борис не приходит"
    particle_student = "не студент"
    particle_speaking = "не говорю́"
    exists = "есть"
    absent = "нет"
    particle = "не"
    yes_token = "Да"
    lesson.objective(
        "verbal-ne",
        "Choose the admitted negation example",
        "FORM_SELECTION",
        ("verbs", negation, "To negate a verb, simply add '''не''' (''nye'') before the verb:"),
        [
            _wiki("verbs", negation, yes_speaking, "Yes, I'm speaking"),
            _wiki("verbs", negation, not_speaking, "No, I'm not speaking"),
            _wiki("verbs", negation, question, "Are you speaking?"),
            _wiki("verbs", negation, not_student, "He's not a student"),
            _wiki("verbs", negation, not_coming, "Boris isn't coming"),
        ],
        [
            _distinction(
                "The frozen examples move from the speaking reply to the line with the particle before that word. Which admitted line is that result?",
                yes_speaking,
                not_speaking,
                [
                    (not_student, "The frozen example He's not a student is omitted in the present form, so the particle stands on its own.", "This example omits the present form instead of adding the particle before the speaking reply."),
                    (question, "The frozen example Are you speaking? is the question before the reply.", "This example is the question, not the reply with the particle added."),
                ],
                feature="PARTICLE_BEFORE_SHOWN",
                why="The frozen example No, I'm not speaking adds the particle before the speaking reply.",
            ),
            _distinction(
                "The frozen note says one example is omitted in the present form and uses the particle on its own. Which admitted line is that example?",
                question,
                not_student,
                [
                    (not_speaking, "The frozen example No, I'm not speaking adds the particle before the speaking word.", "This example adds the particle before the speaking word."),
                    (yes_speaking, "The frozen example Yes, I'm speaking is the agreeing reply, without the particle.", "This example is the agreeing reply, without the particle."),
                ],
                feature="PARTICLE_ALONE",
                why="The frozen example He's not a student is the one omitted in the present form, with the particle on its own.",
            ),
            _distinction(
                "The frozen example Boris isn't coming places the particle before the coming word. Which admitted line is that example?",
                not_speaking,
                not_coming,
                [
                    (particle_student, "The frozen example He's not a student uses the particle on its own because the present form is omitted.", "This short line uses the particle on its own with the student word."),
                    (particle_speaking, "The frozen example No, I'm not speaking places the particle before the speaking word, not the coming word.", "This short line places the particle before the speaking word."),
                ],
                feature="PARTICLE_BEFORE_COMING",
                why="The frozen example Boris isn't coming places the particle before the coming word.",
            ),
        ],
        extra=[
            ("verbs", negation, particle_student, "example"),
            ("verbs", negation, particle_speaking, "example"),
        ],
    )
    lesson.objective(
        "absence",
        "Choose the admitted replacement for the exists word",
        "CONTRAST_SELECTION",
        ("cases", absence, "If it isn't there, we use '''нет''' followed by the genitive."),
        [
            _wiki("verbs", negation, exists, "exists"),
            _wiki("verbs", negation, absent, "no"),
            _wiki("verbs", negation, particle, "nye"),
        ],
        [
            _distinction(
                "The frozen note replaces the exists word when negating the have-phrase. Which admitted form is the replacement?",
                exists,
                absent,
                [
                    (particle, "The frozen note adds this particle before a word. It is not the replacement for the exists word.", "This particle is placed before a word, not used as the replacement."),
                    (yes_token, "The frozen example uses this as the agreeing reply Yes, I'm speaking, not as the replacement.", "This token is the agreeing reply, not the replacement."),
                ],
                feature="EXISTS_REPLACEMENT",
                why="The frozen note says this form has a different meaning from the reply that means no, and it replaces the exists word.",
            ),
            _distinction(
                "The frozen have-phrase uses one form before it is replaced. Which admitted form is the one that means exists?",
                absent,
                exists,
                [
                    (particle, "The frozen note adds this particle before a word. It is not the form that means exists.", "This particle is not the form that means exists."),
                    (yes_token, "The frozen example Yes, I'm speaking uses this as the agreeing reply.", "This token is the agreeing reply, not the exists word."),
                ],
                feature="EXISTS_WORD",
                why="The frozen note uses this form to mean 'exists' in the have-phrase, before the replacement.",
            ),
            _distinction(
                "The frozen note keeps the particle apart from the have-phrase replacement. Which admitted form replaces the exists word?",
                particle,
                absent,
                [
                    (exists, "The frozen note uses this form to mean 'exists' before replacing it.", "This form means exists, so it is the form being replaced."),
                    (yes_token, "The frozen example Yes, I'm speaking uses this as the agreeing reply.", "This token is the agreeing reply, not the replacement."),
                ],
                feature="EXISTS_REPLACEMENT",
                why="The frozen note says this replacement has a different meaning from the reply that means no.",
            ),
        ],
        extra=[
            ("verbs", negation, yes_token, "example"),
        ],
    )


def _unit_25(lesson) -> None:
    structure = _span("names", "Russians use three names:", "Василий Иванович!")
    pairs = _span("names", "Some of the most common examples include Sasha for Alexander", "Dmitry")
    courtesy = _span("useful", ";До свида́ния", "My name is...")
    first_name = "имя"
    patronymic = "отчество"
    family = "фамилия"
    ovich = "ович"
    ovna = "овна"
    alexander = "Александр"
    alexei = "Алексей"
    dmitri = "Дмитрий"
    please = "Пожа́луйста"
    thanks = "Спаси́бо"
    apology = "Извини́те"
    question = "Как Вас зову́т?"
    answer = "Меня́ зову́т"
    formal_apology = "Прошу́ проще́ния"
    how_are_you = "Как дела́?"
    lesson.objective(
        "name-structure",
        "Choose the admitted form paired with the shown name words",
        "FORM_SELECTION",
        ("names", structure, "Russians use three names: first name, or имя; middle or patronymic name, or отчество, which is their father's first name plus a suffix meaning \"son of\" (ович) or \"daughter of\" (овна); and the last name or family name, or фамилия."),
        [
            _wiki("names", structure, first_name, "first name"),
            _wiki("names", structure, patronymic, "patronymic name"),
            _wiki("names", structure, family, "family name"),
            _wiki("names", structure, "Василий Иванович", "For instance"),
        ],
        [
            _distinction(
                "The frozen sentence pairs the shown words with one form. Which admitted form is that pair?",
                "first name",
                first_name,
                [
                    (patronymic, "The frozen sentence pairs this form with patronymic name, not with the shown words.", "This form is paired with patronymic name."),
                    (family, "The frozen sentence pairs this form with family name, not with the shown words.", "This form is paired with family name."),
                    (ovich, "The frozen sentence gives this ending for son of, not as the pair for the shown words.", "This ending means son of, rather than the paired form."),
                ],
                feature="NAME_PAIR",
                why="The frozen sentence says Russians use three names and pairs the shown words with this form.",
            ),
            _distinction(
                "The frozen sentence pairs the shown patronymic words with one form. Which admitted form is that pair?",
                "patronymic name",
                patronymic,
                [
                    (first_name, "The frozen sentence pairs this form with first name, not with the shown words.", "This form is paired with first name."),
                    (family, "The frozen sentence pairs this form with family name, not with the shown words.", "This form is paired with family name."),
                    (ovna, "The frozen sentence gives this ending for daughter of, not as the pair for the shown words.", "This ending means daughter of, rather than the paired form."),
                ],
                feature="NAME_PAIR",
                why="The frozen sentence says Russians use three names and pairs patronymic name with this form.",
            ),
            _distinction(
                "The frozen sentence pairs the shown family words with one form. Which admitted form is that pair?",
                "family name",
                family,
                [
                    (first_name, "The frozen sentence pairs this form with first name, not with the shown words.", "This form is paired with first name."),
                    (patronymic, "The frozen sentence pairs this form with patronymic name, not with the shown words.", "This form is paired with patronymic name."),
                    (ovich, "The frozen sentence gives this ending for son of, not as the pair for the shown words.", "This ending means son of, rather than the paired form."),
                ],
                feature="NAME_PAIR",
                why="The frozen sentence says Russians use three names and pairs family name with this form.",
            ),
        ],
        extra=[
            ("names", structure, ovich, "example"),
            ("names", structure, ovna, "example"),
        ],
    )
    lesson.objective(
        "diminutives",
        "Choose the admitted short form paired with the shown name",
        "CONTRAST_SELECTION",
        ("names", pairs, "Some of the most common examples include Sasha for Alexander, Alyosha for Alexei, Misha for Mikhail, Dima for Dmitri, Lena for Elena, Olya for Olga, Natasha for Natalya and Katya for Ekaterina."),
        [
            _wiki("names", pairs, alexander, "Alexander"),
            _wiki("names", pairs, alexei, "Alexei"),
            _wiki("names", pairs, dmitri, "Dmitri"),
        ],
        [
            _distinction(
                "The frozen English list pairs one short form with the shown name. Which admitted short form is that pair?",
                "Alexander",
                "Sasha",
                [
                    ("Alyosha", "The frozen list pairs Alyosha for Alexei with a different name.", "Alyosha is paired with Alexei, not with the shown name."),
                    ("Dima", "The frozen list pairs Dima for Dmitri with a different name.", "Dima is paired with Dmitri, not with the shown name."),
                ],
                feature="SHORT_FORM_PAIR",
                why="The frozen list includes Sasha for Alexander as the pair for the shown name.",
            ),
            _distinction(
                "The frozen English list pairs one short form with the shown name. Which admitted short form is that pair?",
                "Alexei",
                "Alyosha",
                [
                    ("Sasha", "The frozen list pairs Sasha for Alexander with a different name.", "Sasha is paired with Alexander, not with the shown name."),
                    ("Dima", "The frozen list pairs Dima for Dmitri with a different name.", "Dima is paired with Dmitri, not with the shown name."),
                ],
                feature="SHORT_FORM_PAIR",
                why="The frozen list includes Alyosha for Alexei as the pair for the shown name.",
            ),
            _distinction(
                "The frozen English list pairs one short form with the shown name. Which admitted short form is that pair?",
                "Dmitri",
                "Dima",
                [
                    ("Sasha", "The frozen list pairs Sasha for Alexander with a different name.", "Sasha is paired with Alexander, not with the shown name."),
                    ("Alyosha", "The frozen list pairs Alyosha for Alexei with a different name.", "Alyosha is paired with Alexei, not with the shown name."),
                ],
                feature="SHORT_FORM_PAIR",
                why="The frozen list includes Dima for Dmitri as the pair for the shown name.",
            ),
        ],
        extra=[
            ("names", pairs, "Sasha", "example"),
            ("names", pairs, "Alyosha", "example"),
            ("names", pairs, "Dima", "example"),
        ],
    )
    lesson.objective(
        "courtesy",
        "Choose the admitted form paired with the shown courtesy word",
        "FORM_SELECTION",
        ("useful", courtesy, ";Пожа́луйста (''Pozhalusta'') :please"),
        [
            _wiki("useful", courtesy, please, "please"),
            _wiki("useful", courtesy, thanks, "thanks"),
            _wiki("useful", courtesy, apology, "I'm sorry / excuse me (informal)"),
        ],
        [
            _distinction(
                "The frozen list pairs the shown request word with one form. Which admitted form is that pair?",
                "please",
                please,
                [
                    (thanks, "The frozen list pairs thanks with a different form.", "Thanks is a different courtesy word."),
                    (apology, "The frozen list pairs I'm sorry / excuse me (informal) with a different form.", "The informal apology is a different courtesy word."),
                ],
                feature="COURTESY_PAIR",
                why="The frozen list says please; also used for a request and pairs that word with this form.",
            ),
            _distinction(
                "The frozen list pairs the shown thanks word with one form. Which admitted form is that pair?",
                "thanks",
                thanks,
                [
                    (please, "The frozen list says please; also used for a request, not for thanks.", "Please is the request word, not the thanks word."),
                    (apology, "The frozen list pairs I'm sorry / excuse me (informal) with a different form.", "The informal apology is not the thanks word."),
                ],
                feature="COURTESY_PAIR",
                why="The frozen list pairs thanks with this form and I'm sorry / excuse me (informal) with a different form.",
            ),
            _distinction(
                "The frozen list pairs the shown informal apology with one form. Which admitted form is that pair?",
                "I'm sorry / excuse me (informal)",
                apology,
                [
                    (please, "The frozen list says please; also used for a request, not for the informal apology.", "Please is the request word, not the informal apology."),
                    (thanks, "The frozen list pairs thanks with a different form.", "Thanks is not the informal apology."),
                ],
                feature="COURTESY_PAIR",
                why="The frozen list pairs I'm sorry / excuse me (informal) with this form.",
            ),
        ],
    )
    lesson.objective(
        "name-question",
        "Choose the admitted form paired with the shown name question or reply",
        "FORM_SELECTION",
        ("useful", courtesy, ";Как Вас зову́т? (''Kak Vas zovut?'') :What is your name?"),
        [
            _wiki("useful", courtesy, question, "What is your name?"),
            _wiki("useful", courtesy, answer, "My name is..."),
            _wiki("useful", courtesy, formal_apology, "I'm sorry / excuse me (formal)"),
            _wiki("useful", courtesy, how_are_you, "How are you?"),
        ],
        [
            _distinction(
                "The frozen list pairs the shown question with one form. Which admitted form is that pair?",
                "What is your name?",
                question,
                [
                    (answer, "The frozen list pairs My name is... with the reply, not with the shown question.", "This form is the reply, not the question."),
                    (formal_apology, "The frozen list pairs I'm sorry / excuse me (formal) with a different form.", "This form is the formal apology, not the question."),
                ],
                feature="NAME_QUESTION_PAIR",
                why="The frozen list pairs What is your name? with this form and My name is... with the reply.",
            ),
            _distinction(
                "The frozen list pairs the shown reply with one form. Which admitted form is that pair?",
                "My name is...",
                answer,
                [
                    (question, "The frozen list pairs What is your name? with the question, not with the shown reply.", "This form is the question, not the reply."),
                    (formal_apology, "The frozen list pairs I'm sorry / excuse me (formal) with a different form.", "This form is the formal apology, not the reply."),
                ],
                feature="NAME_ANSWER_PAIR",
                why="The frozen list pairs My name is... with this reply and What is your name? with the question.",
            ),
            _distinction(
                "The frozen list gives the shown question and a separate reply. Which admitted form is the reply rather than another question?",
                question,
                answer,
                [
                    (formal_apology, "The frozen list pairs I'm sorry / excuse me (formal) with this form, not with the name reply.", "This form is the formal apology, not the name reply."),
                    (how_are_you, "The frozen list pairs How are you? with this form, not with the name reply.", "This form asks how someone is, rather than giving the name reply."),
                ],
                feature="NAME_ANSWER_PAIR",
                why="The frozen list pairs My name is... with this reply and What is your name? with the shown question.",
            ),
        ],
    )
    formation = _span(
        "lichnoe-imya",
        "В словообразовании уменьшительных и ласкательных форм имён участвуют",
        "считаются стилистически приниженными",
    )
    full_base = "от полной формы имени"
    short_base = "от краткой формы"
    dismissive = "оттенок пренебрежения"
    limitation = "Уменьшительные имена, образованные при помощи суффикса -к-, несут в себе оттенок пренебрежения"
    lowered = "считаются стилистически приниженными"
    prompt = "Which admitted phrase classifies the shown form?"
    lesson.objective(
        "nickname-rule",
        "Classify the shown name form by its admitted base or shade",
        "CONTRAST_SELECTION",
        ("lichnoe-imya", formation, limitation),
        [
            _wiki("lichnoe-imya", formation, "Марюня", full_base),
            _wiki("lichnoe-imya", formation, "Машенька", short_base),
            _wiki("lichnoe-imya", formation, "Сашка", dismissive),
        ],
        [
            _check(
                prompt,
                "Марюня",
                full_base,
                [
                    (short_base, f"The source assigns {short_base} to Машенька.", f"{short_base} does not classify the shown form."),
                    (dismissive, f"The source assigns {dismissive} to Сашка.", f"{dismissive} does not classify the shown form."),
                ],
                source=LICHNOE_SOURCE,
                feature="PRODUCTIVE_DIMINUTIVE",
                why=f"The source says {full_base} and gives Марюня. The keyed phrase is {full_base}.",
                response_kind=_shared_response_kind([full_base, short_base, dismissive]),
            ),
            _check(
                prompt,
                "Машенька",
                short_base,
                [
                    (full_base, f"The source assigns {full_base} to Марюня.", f"{full_base} does not classify the shown form."),
                    (dismissive, f"The source assigns {dismissive} to Сашка.", f"{dismissive} does not classify this shown form."),
                ],
                source=LICHNOE_SOURCE,
                feature="PRODUCTIVE_DIMINUTIVE",
                why=f"The source says {short_base} and gives Машенька. The keyed phrase is {short_base}.",
                response_kind=_shared_response_kind([full_base, short_base, dismissive]),
            ),
            _check(
                prompt,
                "Сашка",
                dismissive,
                [
                    (full_base, f"The source assigns {full_base} to Марюня.", f"{full_base} does not classify the shown form."),
                    (short_base, f"The source assigns {short_base} to Машенька.", f"{short_base} does not classify the shown form."),
                ],
                source=LICHNOE_SOURCE,
                feature="PRODUCTIVE_DIMINUTIVE",
                why=f"The source says these forms carry {dismissive} and gives Сашка. The keyed phrase is {dismissive}.",
                response_kind=_shared_response_kind([full_base, short_base, dismissive]),
            ),
        ],
        extra=[
            ("lichnoe-imya", formation, lowered, "label"),
            ("lichnoe-imya", formation, "Сашка, Гришка, Светка", "example"),
            ("lichnoe-imya", formation, "Мария", "example"),
            ("lichnoe-imya", formation, "Маша", "example"),
        ],
    )
    if not hasattr(lesson, "capabilities"):
        lesson.capabilities = []
    lesson.capabilities.append({
        "objectiveId": "obj-u25-nickname-rule",
        "supportedClaim": "productive-diminutive",
        "exactSpan": formation,
    })
