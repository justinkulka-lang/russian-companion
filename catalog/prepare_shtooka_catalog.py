#!/usr/bin/env python3
"""Deterministic intake for the Shtooka word recordings and OpenRussian headwords.

Copies source-supplied license text, filenames, and accented headwords.
Does not invent spellings, stress marks, frequency ranks, or lesson mappings.
"""

import os
import csv
import gzip
import json
import re
import subprocess
import time
import urllib.error
import urllib.parse
import urllib.request
from collections import Counter
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
WORK = Path("/tmp/catalog-src")
ASSETS = ROOT / "app/src/main/assets"
AUDIO_DIR = ASSETS / "audio"
SEED = ASSETS / "content/bundled-content.json"
INTAKE = ROOT / "catalog/intake.json"
DECISIONS = ROOT / "catalog/source-decisions.json"
REPORT = ROOT / "catalog/preparation-record.json"
FREQ_DIR = WORK / "freq"
LOOKUP_PATH = WORK / "commons-lookup.json"

USER_AGENT = "russian-companion-catalog-prep/1.0 (offline study app; license verification)"
OPENRUSSIAN_LIST = "https://en.openrussian.org/list/all?start={start}"
COMMONS_API = "https://commons.wikimedia.org/w/api.php"
TEXT_SOURCE = "https://github.com/Badestrand/russian-dictionary"
TEXT_LICENSE = "https://creativecommons.org/licenses/by-sa/4.0/"
TOP_N = 1000
SHARE_TREATMENT_BY = (
    "OpenRussian stress-marked headwords are CC BY-SA 4.0. "
    "The matched audio license has no share-alike term."
)
SHARE_TREATMENT_SA = (
    "OpenRussian stress-marked headwords are CC BY-SA 4.0. "
    "The matched audio license is also share-alike."
)


class RateLimited(RuntimeError):
    def __init__(self, url, retry_after=60):
        super().__init__(url)
        self.retry_after = retry_after


def norm(value: str) -> str:
    return value.casefold().replace("\u0301", "").strip()


def license_class(name: str) -> str:
    lowered = " ".join((name or "").casefold().split())
    if not lowered.strip():
        return "absent"
    if "no offsite" in lowered or "no off-site" in lowered:
        return "no-offsite"
    if any(token in lowered for token in ("noncommercial", "non-commercial", "by-nc", "cc by-nc", "-nc")):
        return "nc"
    if any(token in lowered for token in ("noderiv", "no deriv", "by-nd", "-nd")):
        return "nd"
    if "cc0" in lowered or "public domain" in lowered:
        return "compatible"
    if "by-sa" in lowered or "attribution-sharealike" in lowered or "share alike" in lowered:
        return "compatible"
    if any(
        token in lowered
        for token in (
            "creative commons by",
            "creative commons attribution",
            "creative commons paternité",
            "creative commons paternite",
            "cc by",
            "cc-by",
        )
    ):
        return "compatible"
    return "unclear"


def read_text(path: Path) -> str:
    raw = path.read_bytes()
    if raw[:2] == b"\x1f\x8b":
        raw = gzip.decompress(raw)
    return raw.decode("utf-8", "replace")


def require_readme(path: Path, url: str, stated_name: str) -> dict:
    text = read_text(path)
    if url not in text:
        raise SystemExit(f"{path.name} does not contain {url}")
    if license_class(text) != "compatible":
        raise SystemExit(f"{path.name} is not an attribution-compatible license")
    return {
        "licenseName": stated_name,
        "licenseUrl": url,
        "shareAlike": False,
    }


def parse_swac(path: Path):
    section = None
    fields = {}
    files = []
    for line in path.read_text(encoding="utf-8", errors="replace").splitlines():
        if line.startswith("[") and line.endswith("]"):
            if section and section != "GLOBAL":
                files.append((section, fields))
            section = line[1:-1]
            fields = {}
            continue
        if "=" in line and section and section != "GLOBAL":
            key, value = line.split("=", 1)
            fields[key] = value
    if section and section != "GLOBAL":
        files.append((section, fields))
    return files


def load_dictionary():
    rows = {}
    ambiguous = set()
    for name in ("nouns.csv", "verbs.csv", "adjectives.csv", "others.csv"):
        with (WORK / name).open(encoding="utf-8", newline="") as handle:
            reader = csv.DictReader(handle, delimiter="\t")
            for row in reader:
                bare = (row.get("bare") or "").strip()
                accented = (row.get("accented") or "").strip()
                key = norm(bare)
                if not key or not accented:
                    continue
                previous = rows.get(key)
                if previous is None:
                    rows[key] = (bare, accented)
                elif previous != (bare, accented):
                    ambiguous.add(key)
    for key in ambiguous:
        rows.pop(key, None)
    return rows, ambiguous


def fetch_json(url: str):
    request = urllib.request.Request(url, headers={"User-Agent": USER_AGENT})
    try:
        with urllib.request.urlopen(request, timeout=60) as response:
            return json.loads(response.read().decode("utf-8"))
    except urllib.error.HTTPError as error:
        if error.code == 429:
            raise RateLimited(url)
        raise


def canonical_license(name: str) -> str:
    folded = " ".join((name or "").casefold().split())
    return {
        "cc by 3.0 us": "CC BY 3.0 US",
        "cc by 2.0 fr": "CC BY 2.0 FR",
        "cc by 3.0 fr": "CC BY 3.0 FR",
    }.get(folded, name)


def frequency_entries(start: int):
    FREQ_DIR.mkdir(parents=True, exist_ok=True)
    cache = FREQ_DIR / f"start-{start}.json"
    if cache.is_file():
        return json.loads(cache.read_text(encoding="utf-8"))
    request = urllib.request.Request(
        OPENRUSSIAN_LIST.format(start=start),
        headers={"User-Agent": USER_AGENT},
    )
    with urllib.request.urlopen(request, timeout=60) as response:
        html = response.read().decode("utf-8")
    marker = html.find("__NEXT_DATA__")
    if marker < 0:
        raise SystemExit(f"OpenRussian frequency page {start} has no data")
    start_json = html.find(">", marker) + 1
    end_json = html.find("</script>", start_json)
    data = json.loads(html[start_json:end_json])
    entries = data["props"]["pageProps"]["entries"]
    slim = [
        {
            "rank": entry.get("rank"),
            "bare": entry.get("bare") or "",
            "accented": entry.get("accented") or "",
        }
        for entry in entries
    ]
    cache.write_text(json.dumps(slim, ensure_ascii=False), encoding="utf-8")
    time.sleep(0.2)
    return slim


def speaker_matches(collection: str, artist: str) -> bool:
    if collection == "sakhno":
        return any(token in artist for token in ("Сахно", "Sakhno", "Sergueï", "Sergei"))
    if collection == "streit":
        return any(token in artist for token in ("Streit", "Gulnara", "Goula", "Гульнар"))
    return False


def source_record(record_id, provenance, basis, license_name, attribution, share_alike_audio):
    return {
        "id": record_id,
        "provenance": provenance,
        "redistributionBasis": basis,
        "licenseName": license_name,
        "attribution": attribution,
        "modificationStatus": "UNMODIFIED",
        "shareAlikeTreatment": SHARE_TREATMENT_SA if share_alike_audio else SHARE_TREATMENT_BY,
        "shareAlikeConfirmation": "UNCONFIRMED",
    }


def grouped_source(audio_license_name, collection, file_page_license):
    text_note = "Stress-marked text from OpenRussian, CC BY-SA 4.0."
    text_basis = f"Dictionary text: CC BY-SA 4.0, {TEXT_LICENSE}. Source export: {TEXT_SOURCE}."
    if collection == "sakhno" and audio_license_name == "CC BY 3.0 US":
        return source_record(
            "shtooka-sakhno-cc-by-3.0-us",
            "Shtooka collection rus-balm-voc-sakhno; OpenRussian accented headword",
            "Audio README: Creative Commons Attribution 3.0 United States, "
            "http://creativecommons.org/licenses/by/3.0/us/. " + text_basis,
            "CC BY 3.0 US; CC BY-SA 4.0",
            f"Sergei Sakhno and Nicolas Vion, Shtooka Project. {text_note}",
            False,
        )
    if audio_license_name == "CC BY 2.0 FR":
        return source_record(
            "shtooka-streit-cc-by-2.0-fr",
            "Shtooka collection rus-balm-voc; OpenRussian accented headword",
            "Audio README: Creative Commons Attribution 2.0 France, "
            "http://creativecommons.org/licenses/by/2.0/fr/. " + text_basis,
            "CC BY 2.0 FR; CC BY-SA 4.0",
            "Streit Goula, Streit Eric, and Nicolas Vion, Shtooka Project. "
            f"Speaker on the file page: Gulnara Streit. {text_note}",
            False,
        )
    if audio_license_name == "CC BY 3.0 FR":
        return source_record(
            "shtooka-streit-cc-by-3.0-fr",
            "Shtooka file page states CC BY 3.0 France; OpenRussian accented headword",
            "Per-file Commons license: Creative Commons Attribution 3.0 France, "
            "https://creativecommons.org/licenses/by/3.0/fr/. " + text_basis,
            "CC BY 3.0 FR; CC BY-SA 4.0",
            f"Gulnara Streit, Shtooka Project. {text_note}",
            False,
        )
    slug = "".join(ch.lower() if ch.isalnum() else "-" for ch in audio_license_name).strip("-")
    share = license_class(audio_license_name) == "compatible" and (
        "by-sa" in audio_license_name.casefold() or "share alike" in audio_license_name.casefold()
    )
    basis = file_page_license or audio_license_name
    return source_record(
        f"shtooka-{collection}-{slug}",
        f"Shtooka collection file page; OpenRussian accented headword",
        f"Per-file license: {basis}. {text_basis}",
        f"{audio_license_name}; CC BY-SA 4.0",
        f"Shtooka Project. {text_note}",
        share,
    )


def load_commons_meta():
    path = WORK / "commons-meta.json"
    if not path.is_file():
        return {}
    grouped = {}
    for item in json.loads(path.read_text(encoding="utf-8")):
        title = item.get("title") or ""
        if not title.startswith("File:Ru-") or not title.endswith(".ogg"):
            continue
        word = title[len("File:Ru-") : -len(".ogg")]
        grouped.setdefault(norm(word), []).append(item)
    return grouped


def commons_batch(titles):
    stored = {}
    if LOOKUP_PATH.is_file():
        stored = json.loads(LOOKUP_PATH.read_text(encoding="utf-8"))
    if os.environ.get("CATALOG_DOWNLOAD", "1") != "1":
        return stored
    missing = [title for title in titles if title not in stored]
    for offset in range(0, len(missing), 40):
        chunk = missing[offset : offset + 40]
        query = urllib.parse.urlencode(
            {
                "action": "query",
                "titles": "|".join(chunk),
                "prop": "imageinfo",
                "iiprop": "url|mime|extmetadata",
                "format": "json",
            }
        )
        payload = fetch_json(COMMONS_API + "?" + query)
        pages = payload.get("query", {}).get("pages", {})
        by_title = {page.get("title"): page for page in pages.values()}
        for title in chunk:
            page = by_title.get(title)
            stored[title] = None if page is None or page.get("missing") is not None else page
        LOOKUP_PATH.write_text(json.dumps(stored), encoding="utf-8")
        time.sleep(0.3)
    return stored


def ext_value(page, name):
    info = (page.get("imageinfo") or [{}])[0]
    meta = info.get("extmetadata") or {}
    slot = meta.get(name) or {}
    value = slot.get("value") or ""
    return re.sub(r"<[^>]+>", " ", value).replace("&nbsp;", " ")


def file_page_audio(page):
    info = (page.get("imageinfo") or [{}])[0]
    url = (info.get("url") or "").split("?", 1)[0]
    license_name = ext_value(page, "LicenseShortName")
    artist = ext_value(page, "Artist")
    credit = ext_value(page, "Credit")
    return {
        "url": url,
        "mime": info.get("mime") or "",
        "license": license_name,
        "artist": artist,
        "credit": credit,
        "shtooka": "shtooka" in (artist + " " + credit).casefold(),
    }


def download(url, destination):
    if destination.is_file() and destination.stat().st_size > 32 and destination.read_bytes()[:4] == b"OggS":
        return
    destination.parent.mkdir(parents=True, exist_ok=True)
    temporary = destination.with_suffix(destination.suffix + ".partial")
    header_path = destination.with_suffix(destination.suffix + ".headers")
    result = subprocess.run(
        [
            "curl", "-fsSL", "--max-time", "60", "-A", USER_AGENT,
            "-D", str(header_path), "-o", str(temporary), url,
        ],
        capture_output=True,
    )
    retry_after = 60
    if header_path.is_file():
        for line in header_path.read_text(encoding="utf-8", errors="replace").splitlines():
            if line.lower().startswith("retry-after:"):
                try:
                    retry_after = int(line.split(":", 1)[1].strip())
                except ValueError:
                    retry_after = 60
        header_path.unlink()
    error_text = result.stderr.decode("utf-8", "replace")
    if result.returncode != 0:
        temporary.unlink(missing_ok=True)
        if "429" in error_text:
            raise RateLimited(url, retry_after)
        raise RuntimeError(error_text.strip() or f"curl exit {result.returncode}")
    data = temporary.read_bytes()
    if not data.startswith(b"OggS") or len(data) < 32:
        temporary.unlink(missing_ok=True)
        raise RuntimeError("not an Ogg file")
    temporary.replace(destination)
    time.sleep(1.0)


def decision(filename, decision_name, license_name, reason):
    return {
        "sourceId": f"file:{filename}",
        "provenance": f"swac-file:{filename}",
        "decision": decision_name,
        "licenseName": license_name,
        "reason": reason,
    }


def main():
    sakhno_readme = require_readme(
        WORK / "sakhno-readme.txt",
        "http://creativecommons.org/licenses/by/3.0/us/",
        "CC BY 3.0 US",
    )
    streit_readme = require_readme(
        WORK / "voc-readme.txt",
        "http://creativecommons.org/licenses/by/2.0/fr/",
        "CC BY 2.0 FR",
    )
    dictionary, ambiguous_dictionary = load_dictionary()
    collections = {
        "sakhno": (parse_swac(WORK / "sakhno-index.tags.txt"), sakhno_readme),
        "streit": (parse_swac(WORK / "voc-index.tags.txt"), streit_readme),
    }
    commons = load_commons_meta()
    files = []
    by_filename = {}
    for collection, (indexed, readme) in collections.items():
        for filename, fields in indexed:
            text = (fields.get("SWAC_TEXT") or "").strip()
            existing = by_filename.get(filename)
            if existing is not None:
                existing["collections"].append(collection)
                if existing["text"] != text:
                    existing["text_conflict"] = True
                continue
            item = {
                "filename": filename,
                "collection": collection,
                "collections": [collection],
                "text": text,
                "key": norm(text),
                "readme": readme,
                "homograph": bool((fields.get("SWAC_HOMOGRAPHIDX") or "").strip()),
                "text_conflict": False,
            }
            by_filename[filename] = item
            files.append(item)

    by_key = {}
    for item in files:
        if item["key"]:
            by_key.setdefault(item["key"], []).append(item)

    reasons = {}
    candidates = {}
    for item in files:
        filename = item["filename"]
        readme = item["readme"]
        if item["text_conflict"]:
            reasons[filename] = (
                "",
                "excluded: the same filename has two different SWAC_TEXT values",
            )
            continue
        if not item["text"]:
            reasons[filename] = (readme["licenseName"], "excluded: SWAC_TEXT is blank")
            continue
        if item["homograph"] or len(by_key.get(item["key"], [])) != 1:
            reasons[filename] = (
                readme["licenseName"],
                "excluded: more than one SWAC recording shares this headword",
            )
            continue
        if item["key"] in ambiguous_dictionary or item["key"] not in dictionary:
            reasons[filename] = (
                readme["licenseName"],
                "excluded: no single OpenRussian accented headword for this text",
            )
            continue
        bare, accented = dictionary[item["key"]]
        if norm(bare) != item["key"] or not accented:
            reasons[filename] = (
                readme["licenseName"],
                "excluded: no single OpenRussian accented headword for this text",
            )
            continue
        candidates[item["key"]] = item
        item["accented"] = accented

    ranked = []
    seen = set()
    mismatched = set()
    start = 0
    while len(ranked) < TOP_N and start < 90096:
        page = frequency_entries(start)
        if not page:
            break
        pending = []
        for entry in page:
            rank = entry.get("rank")
            accented = (entry.get("accented") or "").strip()
            key = norm(entry.get("bare") or "")
            if not isinstance(rank, int) or not key or not accented or key in seen:
                continue
            item = candidates.get(key)
            if item is None:
                continue
            if item.get("accented") != accented:
                mismatched.add(item["filename"])
                continue
            seen.add(key)
            pending.append((rank, item))
        titles = ["File:Ru-" + item["text"] + ".ogg" for _, item in pending]
        pages = commons_batch(titles) if titles else {}
        for rank, item in pending:
            if len(ranked) >= TOP_N:
                break
            title = "File:Ru-" + item["text"] + ".ogg"
            meta_items = [
                meta
                for meta in commons.get(item["key"], [])
                if speaker_matches(item["collection"], meta.get("artist") or "")
                and meta.get("title") == title
            ]
            page_info = pages.get(title)
            audio = file_page_audio(page_info) if isinstance(page_info, dict) else None
            artist = (audio or {}).get("artist") or ""
            speaker_ok = not artist.strip() or speaker_matches(item["collection"], artist)
            if audio and audio["shtooka"] and speaker_ok:
                stated = canonical_license(audio["license"])
                kind = license_class(stated)
                if kind != "compatible":
                    reasons[item["filename"]] = (
                        stated,
                        f"excluded: per-file Commons license is {kind}",
                    )
                    continue
                if not audio["url"]:
                    reasons[item["filename"]] = (
                        stated,
                        "excluded: Commons file page has no original audio URL",
                    )
                    continue
                item["audioLicense"] = stated
                item["audioUrl"] = audio["url"]
                item["filePageLicense"] = audio["license"]
            elif audio and audio["shtooka"] and not speaker_ok:
                reasons[item["filename"]] = (
                    canonical_license(audio["license"]),
                    "excluded: Commons file page names a different speaker from the SWAC collection",
                )
                continue
            elif len(meta_items) == 1:
                stated = canonical_license(meta_items[0].get("license") or "")
                kind = license_class(stated)
                if kind != "compatible":
                    reasons[item["filename"]] = (
                        stated,
                        f"excluded: per-file Commons license is {kind}",
                    )
                    continue
                item["audioLicense"] = stated or item["readme"]["licenseName"]
                item["audioUrl"] = (meta_items[0].get("url") or "").split("?", 1)[0]
                item["filePageLicense"] = meta_items[0].get("license") or ""
            else:
                if len(item["collections"]) > 1:
                    reasons[item["filename"]] = (
                        "CC BY 3.0 US; CC BY 2.0 FR",
                        "excluded: listed in two collections whose README licenses differ and no per-file license was found",
                    )
                    continue
                item["audioLicense"] = item["readme"]["licenseName"]
                item["audioUrl"] = ""
                item["filePageLicense"] = ""
            item["rank"] = rank
            ranked.append(item)
        print(f"frequency start={start} retained_candidates={len(ranked)}", flush=True)
        start += 50

    selected_names = {item["filename"] for item in ranked}
    for item in candidates.values():
        filename = item["filename"]
        if filename in selected_names or filename in reasons:
            continue
        if filename in mismatched:
            reasons[filename] = (
                item["readme"]["licenseName"],
                "excluded: OpenRussian frequency-list accented field did not equal the dictionary accented field",
            )
        else:
            reasons[filename] = (
                item["readme"]["licenseName"],
                "excluded: not in the frequency-ranked top 1000 matched items",
            )

    AUDIO_DIR.mkdir(parents=True, exist_ok=True)
    resolved = ranked
    retained = []
    rate_limited = False
    consecutive_limits = 0
    for item in resolved:
        destination = AUDIO_DIR / item["filename"]
        if rate_limited and not (destination.is_file() and destination.stat().st_size > 32):
            reasons[item["filename"]] = (
                item["audioLicense"],
                "excluded: audio bytes were not retrieved and were not fabricated",
            )
            continue
        if os.environ.get("CATALOG_DOWNLOAD", "1") != "1" and not (
            destination.is_file() and destination.stat().st_size > 32
        ):
            reasons[item["filename"]] = (
                item["audioLicense"],
                "excluded: audio bytes were not retrieved and were not fabricated",
            )
            continue
        if not item["audioUrl"]:
            reasons[item["filename"]] = (
                item["audioLicense"],
                "excluded: SWAC bulk audio is unavailable and no usable Commons original was identified",
            )
            continue
        if item["audioUrl"]:
            try:
                download(item["audioUrl"], destination)
                consecutive_limits = 0
            except RateLimited as error:
                print(f"rate limited {item['filename']}: {error}", flush=True)
                time.sleep(min(error.retry_after, 600))
                try:
                    download(item["audioUrl"], destination)
                    consecutive_limits = 0
                except RateLimited as retry_error:
                    print(f"rate limited again {item['filename']}: {retry_error}", flush=True)
                    consecutive_limits += 1
                    if consecutive_limits >= 8:
                        rate_limited = True
                    reasons[item["filename"]] = (
                        item["audioLicense"],
                        "excluded: audio bytes were not retrieved and were not fabricated",
                    )
                    continue
            except Exception as error:
                print(f"download failed {item['filename']}: {error}", flush=True)
                reasons[item["filename"]] = (
                    item["audioLicense"],
                    "excluded: audio bytes were not retrieved and were not fabricated",
                )
                if destination.exists() and destination.read_bytes()[:4] != b"OggS":
                    destination.unlink()
                continue
        if not destination.is_file() or destination.read_bytes()[:4] != b"OggS":
            reasons[item["filename"]] = (
                item["audioLicense"],
                "excluded: audio bytes were not retrieved and were not fabricated",
            )
            continue
        note = "SWAC index has no per-file LICENSE field; collection README license applies"
        if item["filePageLicense"]:
            note = f"Commons file page license {item['filePageLicense']}"
        reasons[item["filename"]] = (
            item["audioLicense"],
            f"included: frequency rank {item['rank']}; {note}; OpenRussian accented headword matched",
        )
        retained.append(item)
        if len(retained) % 50 == 0:
            print(f"installed {len(retained)}", flush=True)

    retained_names = {item["filename"] for item in retained}
    for path in AUDIO_DIR.glob("*.ogg"):
        if path.name not in retained_names:
            path.unlink()

    sources = {}
    source_order = []
    texts = []
    rights = []
    audio = []
    proposals = []
    ranks = []
    for item in retained:
        record = grouped_source(item["audioLicense"], item["collection"], item["filePageLicense"])
        if record["id"] not in sources:
            sources[record["id"]] = record
            source_order.append(record["id"])
        audio_id = item["filename"][:-4]
        text_id = "text-" + audio_id
        rights_id = "rights-" + audio_id
        texts.append(
            {
                "id": text_id,
                "stressMarkedText": item["accented"],
                "sourceRecordId": record["id"],
                "modificationStatus": "UNMODIFIED",
                "matchVerified": True,
            }
        )
        rights.append(
            {
                "id": rights_id,
                "sourceRecordId": record["id"],
                "assetIdentifier": audio_id,
                "licenseStatus": "COMPATIBLE",
                "compatibilityDecision": "COMPATIBLE",
                "attributionRequired": True,
                "inclusionResult": "INCLUDED",
            }
        )
        audio.append(
            {
                "id": audio_id,
                "localAudioReference": "audio/" + item["filename"],
                "sourceTextId": text_id,
                "sourceRecordId": record["id"],
                "rightsRecordId": rights_id,
            }
        )
        proposals.append(
            {
                "id": audio_id,
                "localAudioReference": "audio/" + item["filename"],
                "licenseStatus": "COMPATIBLE",
                "compatibilityDecision": "COMPATIBLE",
                "stressMarkedText": item["accented"],
                "matchVerified": True,
                "provenance": record["provenance"],
                "redistributionBasis": record["redistributionBasis"],
                "licenseName": record["licenseName"],
                "attribution": record["attribution"],
                "modificationStatus": "UNMODIFIED",
                "shareAlikeTreatment": record["shareAlikeTreatment"],
                "shareAlikeConfirmation": "UNCONFIRMED",
            }
        )
        ranks.append({"id": audio_id, "frequencyRank": item["rank"]})

    document = {
        "contentVersion": 1,
        "courseSegments": [],
        "planDefinitions": [],
        "gateDefinitions": [],
        "sourceRecords": [sources[source_id] for source_id in source_order],
        "sourceTexts": texts,
        "assetRightsRecords": rights,
        "audioModels": audio,
        "optionalDrills": [],
    }
    SEED.write_text(json.dumps(document, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    intake_state = "INSTALLED_DROP" if proposals else "NOT_INSTALLED"
    INTAKE.write_text(
        json.dumps(
            {"intakeVersion": 1, "state": intake_state, "proposals": proposals},
            ensure_ascii=False,
            indent=2,
        )
        + "\n",
        encoding="utf-8",
    )

    file_decisions = []
    for item in sorted(files, key=lambda row: row["filename"]):
        license_name, reason = reasons.get(
            item["filename"],
            (item["readme"]["licenseName"], "excluded: no decision was recorded"),
        )
        file_decisions.append(
            decision(
                item["filename"],
                "INCLUDED" if reason.startswith("included:") else "EXCLUDED",
                license_name,
                reason,
            )
        )
    source_level = [
        {
            "sourceId": "openrussian-audio",
            "provenance": "https://en.openrussian.org/dictionary-data",
            "decision": "EXCLUDED",
            "licenseName": "",
            "reason": "The publisher states that audio samples are not open because of possible copyright issues.",
        },
        {
            "sourceId": "openrussian-dictionary",
            "provenance": TEXT_SOURCE,
            "decision": "STRESS_TEXT_ONLY",
            "licenseName": "CC BY-SA 4.0",
            "reason": "The accented column supplies stress-marked headwords under CC BY-SA 4.0. Share-alike confirmation is not recorded. OpenRussian audio stays excluded. No lesson mapping was supplied.",
        },
        {
            "sourceId": "shtooka-russian-commons",
            "provenance": "https://commons.wikimedia.org/wiki/Commons:Shtooka",
            "decision": "PARTIAL",
            "licenseName": "CC BY 2.0 FR; CC BY 3.0 US; CC BY 3.0 FR",
            "reason": "Collection README licenses are attribution-only. Each SWAC file has its own decision. Commons is the per-file verification layer when a file page exists.",
        },
        {
            "sourceId": "tatoeba-audio",
            "provenance": "https://en.wiki.tatoeba.org/articles/show/using-the-tatoeba-corpus",
            "decision": "EXCLUDED",
            "licenseName": "",
            "reason": "Sentence text is CC BY 2.0 FR. Audio licenses are per recording. No per-file audio license was verified, so no Tatoeba audio is included.",
        },
        {
            "sourceId": "forvo-audio",
            "provenance": "https://en.wikipedia.org/wiki/Forvo",
            "decision": "EXCLUDED",
            "licenseName": "CC BY-NC-SA 3.0",
            "reason": "Forvo audio is CC BY-NC-SA 3.0 and is not bulk-distributable.",
        },
        {
            "sourceId": "penguin-course-text",
            "provenance": "The New Penguin Russian Course",
            "decision": "EXCLUDED",
            "licenseName": "",
            "reason": "Book text, exercises, and answer-key content are not reproduced.",
        },
        {
            "sourceId": "lingua-libre-rus",
            "provenance": "https://commons.wikimedia.org/wiki/Category:Lingua_Libre_pronunciation-rus",
            "decision": "NOT_ACQUIRED",
            "licenseName": "CC BY-SA 4.0",
            "reason": "The Russian file count was not verified, so Lingua Libre audio was not acquired.",
        },
    ]
    payload = {"decisionsVersion": 1, "decisions": source_level + file_decisions}
    encoded = json.dumps(payload, ensure_ascii=False, indent=2)
    if "CONFIRMED" in encoded:
        raise SystemExit("source decisions must not contain CONFIRMED")
    DECISIONS.write_text(encoded + "\n", encoding="utf-8")

    def reason_bucket(reason):
        head = reason.split(";", 1)[0]
        if head.startswith("included:"):
            return "included"
        return head

    reason_counts = Counter(reason_bucket(reason) for _, reason in reasons.values())
    report = {
        "frequencySource": "OpenRussian list rank field at https://en.openrussian.org/list/all",
        "frequencyLicense": "CC BY-SA 4.0",
        "topMatchedItems": TOP_N,
        "frequencyCap": "applied to the source rank of items that passed the license filter and a unique accented-headword match",
        "shareAlikeConfirmation": "UNCONFIRMED",
        "installedCount": len(retained),
        "rankedMatches": len(ranked),
        "indexFiles": len(files),
        "ambiguousDictionaryRows": len(ambiguous_dictionary),
        "reasonCounts": dict(sorted(reason_counts.items())),
        "retainedRanks": ranks,
        "lessonMappings": "not supplied",
        "bulkHost": "packs.shtooka.net did not resolve; swac-collections.org returned 503",
    }
    REPORT.write_text(json.dumps(report, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    print(json.dumps({key: report[key] for key in report if key != "retainedRanks"}, indent=2))


if __name__ == "__main__":
    main()
