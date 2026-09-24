"""Typed Units 21, 22, 24, and 25 checks. Every Russian span is an exact frozen substring.

Unit 23 has no lesson body here. This module does not conjugate, decline,
or compose new Russian.
"""

from pathlib import Path

from units_11_15_lessons import _target, _wiki
from units_3_5_intake import format_wikibooks_emphasis

ROOT = Path(__file__).resolve().parents[1]
_PAGE_FILE = {
    "possessive": ROOT / "catalog/sources/units-21-25/wikibooks/possessive-pronouns-4220315.txt",
    "numbers": ROOT / "catalog/sources/units-6-10/wikibooks/numbers-4371358.txt",
    "verbs": ROOT / "catalog/sources/units-3-5/wikibooks/verbs-4233876.txt",
    "cases": ROOT / "catalog/sources/units-3-5/wikibooks/cases-4195114.txt",
    "names": ROOT / "catalog/sources/units-21-25/wikibooks/names-4660786.txt",
    "useful": ROOT / "catalog/sources/units-21-25/wikibooks/useful-words-3353859.txt",
}


def _span(page: str, start: str, end: str) -> str:
    text = _PAGE_FILE[page].read_text(encoding="utf-8")
    begin = text.find(start)
    finish = text.find(end, begin if begin >= 0 else 0)
    if begin < 0 or finish < 0:
        raise SystemExit(f"Frozen span is missing on {page}: {start!r} .. {end!r}")
    return text[begin:finish + len(end)]


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
        "(Borya put on his own jeans.)",
    )
    film = "Бо́ря рассказа́л о своём люби́мом фи́льме."
    anna_film = "А́нна рассказа́ла о своём люби́мом фи́льме."
    book = "Свое́й кни́ги она́ никогда́ не написа́ла."
    jeans = "Бо́ря наде́л свои́ джи́нсы."
    lesson.objective(
        "svoy",
        "Choose the admitted свой agreement line",
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
            _wiki("possessive", agreement, jeans, "(Borya put on his own jeans.)"),
        ],
        [
            _target("Which admitted line is the film agreement line?", book, film, [jeans], feature="FILM_AGREEMENT_LINE", phrase="film agreement line"),
            _target("Which admitted line is the book agreement line?", film, book, [jeans], feature="BOOK_AGREEMENT_LINE", phrase="book agreement line"),
            _target("Which admitted line is the jeans agreement line?", film, jeans, [book], feature="JEANS_AGREEMENT_LINE", phrase="jeans agreement line"),
        ],
    )


def _unit_22(lesson) -> None:
    cardinals = _span("numbers", "* 21 два́дцать оди́н", "* 333 три́ста три́дцать три")
    compounds = _span("numbers", "тридцать три рюмки (thirty-three shot glasses)", "шестьдесят девять слов (sixty-nine words)")
    nouns = _span("numbers", "один мальчик (one boy)", "пять девушек (five girls)")
    ordinals = _span("numbers", "==Ordinal numbers==", "# четв'''ё'''ртый")
    twenty_one = "два́дцать оди́н"
    hundred_one = "сто оди́н"
    two_fifteen = "две́сти пятна́дцать"
    three_thirty = "три́ста три́дцать три"
    glasses = "тридцать три рюмки"
    forty_one = "сорок один год"
    sixty_nine = "шестьдесят девять слов"
    one_boy = "один мальчик"
    two_girls = "две девушки"
    five_girls = "пять девушек"
    raw_first = "п'''е'''рвый"
    raw_second = "втор'''о'''й"
    raw_third = "тр'''е'''тий"
    raw_fourth = "четв'''ё'''ртый"
    first = format_wikibooks_emphasis(raw_first)
    second = format_wikibooks_emphasis(raw_second)
    third = format_wikibooks_emphasis(raw_third)
    fourth = format_wikibooks_emphasis(raw_fourth)
    lesson.objective(
        "cardinals",
        "Choose the admitted compound cardinal line",
        "FORM_SELECTION",
        ("numbers", cardinals, "* 21 два́дцать оди́н"),
        [
            _wiki("numbers", cardinals, twenty_one, "21"),
            _wiki("numbers", cardinals, hundred_one, "101"),
            _wiki("numbers", cardinals, two_fifteen, "215"),
            _wiki("numbers", cardinals, three_thirty, "333"),
        ],
        [
            _target("Which admitted line is the twenty-one line?", hundred_one, twenty_one, [two_fifteen, three_thirty], feature="TWENTY_ONE_LINE", phrase="twenty-one line"),
            _target("Which admitted line is the hundred-one line?", twenty_one, hundred_one, [two_fifteen, three_thirty], feature="HUNDRED_ONE_LINE", phrase="hundred-one line"),
            _target("Which admitted line is the two-hundred-fifteen line?", twenty_one, two_fifteen, [hundred_one, three_thirty], feature="TWO_HUNDRED_FIFTEEN_LINE", phrase="two-hundred-fifteen line"),
        ],
    )
    lesson.objective(
        "compounds",
        "Choose the admitted compound quantity line",
        "FORM_SELECTION",
        ("numbers", compounds, "тридцать три рюмки (thirty-three shot glasses)"),
        [
            _wiki("numbers", compounds, glasses, "thirty-three shot glasses"),
            _wiki("numbers", compounds, forty_one, "forty-one years"),
            _wiki("numbers", compounds, sixty_nine, "sixty-nine words"),
        ],
        [
            _target("Which admitted line is the shot-glass line?", forty_one, glasses, [sixty_nine], feature="SHOT_GLASS_LINE", phrase="shot-glass line"),
            _target("Which admitted line is the forty-one-year line?", glasses, forty_one, [sixty_nine], feature="FORTY_ONE_YEAR_LINE", phrase="forty-one-year line"),
            _target("Which admitted line is the sixty-nine-word line?", glasses, sixty_nine, [forty_one], feature="SIXTY_NINE_WORD_LINE", phrase="sixty-nine-word line"),
        ],
    )
    lesson.objective(
        "ordinals",
        "Choose the admitted ordinal line",
        "FORM_SELECTION",
        ("numbers", ordinals, "Ordinal numbers"),
        [
            _wiki("numbers", ordinals, raw_first, "Ordinal numbers"),
            _wiki("numbers", ordinals, raw_second, "Ordinal numbers"),
            _wiki("numbers", ordinals, raw_third, "Ordinal numbers"),
        ],
        [
            _target("Which admitted line is the first ordinal?", second, first, [third, fourth], feature="FIRST_ORDINAL", phrase="first ordinal"),
            _target("Which admitted line is the second ordinal?", first, second, [third, fourth], feature="SECOND_ORDINAL", phrase="second ordinal"),
            _target("Which admitted line is the third ordinal?", first, third, [second, fourth], feature="THIRD_ORDINAL", phrase="third ordinal"),
        ],
        extra=[
            ("numbers", ordinals, raw_fourth, "example"),
        ],
    )
    lesson.objective(
        "noun-forms",
        "Choose the admitted noun form after the numeral",
        "FORM_SELECTION",
        ("numbers", nouns, "один мальчик (one boy)"),
        [
            _wiki("numbers", nouns, one_boy, "one boy"),
            _wiki("numbers", nouns, two_girls, "two girls"),
            _wiki("numbers", nouns, five_girls, "five girls"),
        ],
        [
            _target("Which admitted line is the one-boy line?", two_girls, one_boy, [five_girls], feature="ONE_BOY_LINE", phrase="one-boy line"),
            _target("Which admitted line is the two-girl line?", one_boy, two_girls, [five_girls], feature="TWO_GIRL_LINE", phrase="two-girl line"),
            _target("Which admitted line is the five-girl line?", one_boy, five_girls, [two_girls], feature="FIVE_GIRL_LINE", phrase="five-girl line"),
        ],
    )


def _unit_24(lesson) -> None:
    negation = _span("verbs", "To negate a verb, simply add '''не'''", "different meaning from the '''нет''' that means 'no'.")
    absence = _span("cases", "If it isn't there, we use '''нет''' followed by the genitive.", "followed by the genitive.")
    not_speaking = "не говорю́"
    not_student = "не студент"
    not_coming = "не приходит"
    exists = "есть"
    absent = "нет"
    particle = "не"
    lesson.objective(
        "verbal-ne",
        "Choose the admitted verbal negation line",
        "FORM_SELECTION",
        ("verbs", negation, "To negate a verb, simply add '''не''' (''nye'') before the verb:"),
        [
            _wiki("verbs", negation, not_speaking, "No, I'm not speaking"),
            _wiki("verbs", negation, not_student, "He's not a student"),
            _wiki("verbs", negation, not_coming, "Boris isn't coming"),
        ],
        [
            _target("Which admitted line is the not-speaking line?", not_student, not_speaking, [not_coming], feature="NOT_SPEAKING_LINE", phrase="not-speaking line"),
            _target("Which admitted line is the not-student line?", not_speaking, not_student, [not_coming], feature="NOT_STUDENT_LINE", phrase="not-student line"),
            _target("Which admitted line is the not-coming line?", not_speaking, not_coming, [not_student], feature="NOT_COMING_LINE", phrase="not-coming line"),
        ],
    )
    lesson.objective(
        "absence",
        "Choose the admitted absence form",
        "CONTRAST_SELECTION",
        ("cases", absence, "If it isn't there, we use '''нет''' followed by the genitive."),
        [
            _wiki("verbs", negation, exists, "exists"),
            _wiki("verbs", negation, absent, "no"),
            _wiki("verbs", negation, particle, "nye"),
        ],
        [
            _target("Which admitted line is the absence form?", exists, absent, [particle], feature="ABSENCE_FORM", phrase="absence form"),
            _target("Which admitted line is the existence form?", absent, exists, [particle], feature="EXISTENCE_FORM", phrase="existence form"),
            _target("Which admitted line is the particle form?", absent, particle, [exists], feature="PARTICLE_FORM", phrase="particle form"),
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
    sasha = "Sasha for Alexander"
    alyosha = "Alyosha for Alexei"
    dima = "Dima for Dmitri"
    please = "Пожа́луйста"
    thanks = "Спаси́бо"
    apology = "Извини́те"
    question = "Как Вас зову́т?"
    answer = "Меня́ зову́т"
    formal_apology = "Прошу́ проще́ния"
    lesson.objective(
        "name-structure",
        "Choose the admitted personal-name form",
        "FORM_SELECTION",
        ("names", structure, "Russians use three names: first name, or имя; middle or patronymic name, or отчество, which is their father's first name plus a suffix meaning \"son of\" (ович) or \"daughter of\" (овна); and the last name or family name, or фамилия."),
        [
            _wiki("names", structure, first_name, "first name"),
            _wiki("names", structure, patronymic, "patronymic name"),
            _wiki("names", structure, family, "family name"),
            _wiki("names", structure, "Василий Иванович", "For instance"),
        ],
        [
            _target("Which admitted line is the first-name form?", patronymic, first_name, [family, ovich], feature="FIRST_NAME_FORM", phrase="first-name form"),
            _target("Which admitted line is the patronymic form?", first_name, patronymic, [family, ovna], feature="PATRONYMIC_FORM", phrase="patronymic form"),
            _target("Which admitted line is the family-name form?", first_name, family, [patronymic, ovich], feature="FAMILY_NAME_FORM", phrase="family-name form"),
        ],
        extra=[
            ("names", structure, ovich, "example"),
            ("names", structure, ovna, "example"),
        ],
    )
    lesson.objective(
        "diminutives",
        "Choose the admitted listed diminutive pair",
        "CONTRAST_SELECTION",
        ("names", pairs, "Some of the most common examples include Sasha for Alexander, Alyosha for Alexei, Misha for Mikhail, Dima for Dmitri, Lena for Elena, Olya for Olga, Natasha for Natalya and Katya for Ekaterina."),
        [
            _wiki("names", pairs, alexander, sasha),
            _wiki("names", pairs, alexei, alyosha),
            _wiki("names", pairs, dmitri, dima),
        ],
        [
            _target("Which admitted English label is the Alexander diminutive?", alexander, sasha, [alyosha, dima], feature="ALEXANDER_DIMINUTIVE", phrase="Alexander diminutive"),
            _target("Which admitted English label is the Alexei diminutive?", alexei, alyosha, [sasha, dima], feature="ALEXEI_DIMINUTIVE", phrase="Alexei diminutive"),
            _target("Which admitted English label is the Dmitri diminutive?", dmitri, dima, [sasha, alyosha], feature="DMITRI_DIMINUTIVE", phrase="Dmitri diminutive"),
        ],
    )
    lesson.objective(
        "courtesy",
        "Choose the admitted courtesy line",
        "FORM_SELECTION",
        ("useful", courtesy, ";Пожа́луйста (''Pozhalusta'') :please"),
        [
            _wiki("useful", courtesy, please, "please"),
            _wiki("useful", courtesy, thanks, "thanks"),
            _wiki("useful", courtesy, apology, "I'm sorry / excuse me (informal)"),
        ],
        [
            _target("Which admitted line is the please line?", thanks, please, [apology], feature="PLEASE_LINE", phrase="please line"),
            _target("Which admitted line is the thanks line?", please, thanks, [apology], feature="THANKS_LINE", phrase="thanks line"),
            _target("Which admitted line is the apology line?", please, apology, [thanks], feature="APOLOGY_LINE", phrase="apology line"),
        ],
    )
    lesson.objective(
        "name-question",
        "Choose the admitted formal-name question",
        "FORM_SELECTION",
        ("useful", courtesy, ";Как Вас зову́т? (''Kak Vas zovut?'') :What is your name?"),
        [
            _wiki("useful", courtesy, question, "What is your name?"),
            _wiki("useful", courtesy, answer, "My name is"),
            _wiki("useful", courtesy, formal_apology, "I'm sorry / excuse me (formal)"),
        ],
        [
            _target("Which admitted line is the name-question line?", answer, question, [formal_apology], feature="NAME_QUESTION_LINE", phrase="name-question line"),
            _target("Which admitted line is the name-answer line?", question, answer, [formal_apology], feature="NAME_ANSWER_LINE", phrase="name-answer line"),
            _target("Which admitted line is the formal-apology line?", question, formal_apology, [answer], feature="FORMAL_APOLOGY_LINE", phrase="formal-apology line"),
        ],
    )
