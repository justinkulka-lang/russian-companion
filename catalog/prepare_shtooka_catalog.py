#!/usr/bin/env python3
"""Build the packaged seed from verified Shtooka audio and OpenRussian stress text.

The script copies source-supplied fields only. It does not invent spellings,
stress marks, frequency ranks, or lesson mappings. Items with an ambiguous
dictionary match or more than one recording are omitted.
"""

import csv
import json
import time
import urllib.error
import urllib.request
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
WORK = Path("/tmp/catalog-src")
ASSETS = ROOT / "app/src/main/assets"
AUDIO_DIR = ASSETS / "audio"
SEED = ASSETS / "content/bundled-content.json"
REPORT = ROOT / "catalog/preparation-record.json"

USER_AGENT = "russian-companion-catalog-prep/1.0 (offline study app; license verification)"


def norm(value: str) -> str:
    return value.casefold().replace("\u0301", "").strip()


def parse_swac(path: Path):
    section = None
    fields = {}
    global_fields = {}
    files = []
    for line in path.read_text(encoding="utf-8", errors="replace").splitlines():
        if line.startswith("[") and line.endswith("]"):
            if section and section != "GLOBAL":
                files.append((section, fields))
            section = line[1:-1]
            fields = {}
            continue
        if "=" in line and section:
            key, value = line.split("=", 1)
            if section == "GLOBAL":
                global_fields[key] = value
            else:
                fields[key] = value
    if section and section != "GLOBAL":
        files.append((section, fields))
    return global_fields, files


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


def collection_for(item):
    artist = item["artist"]
    license_name = item["license"]
    title = item["title"]
    if not title.startswith("File:Ru-") or not title.endswith(".ogg"):
        return None
    if "Сахно" in artist and license_name == "CC BY 3.0 us":
        return "sakhno"
    if "Gulnara Streit" in artist and license_name in ("CC BY 2.0 fr", "CC BY 3.0 fr"):
        return "streit"
    if "Streit Goula" in artist and license_name == "CC BY 2.0 fr":
        return "streit"
    return None


def source_for(item):
    if item["license"] == "CC BY 3.0 us":
        return "shtooka-sakhno-cc-by-3.0-us"
    if item["license"] == "CC BY 3.0 fr":
        return "shtooka-streit-cc-by-3.0-fr"
    return "shtooka-streit-cc-by-2.0-fr"


class RateLimited(RuntimeError):
    pass


def download(url, destination):
    if destination.is_file() and destination.stat().st_size > 32 and destination.read_bytes()[:4] == b"OggS":
        return
    clean = url.split("?", 1)[0]
    request = urllib.request.Request(clean, headers={"User-Agent": USER_AGENT})
    try:
        with urllib.request.urlopen(request, timeout=60) as response:
            data = response.read()
    except urllib.error.HTTPError as error:
        if error.code == 429:
            raise RateLimited("Commons rate limit")
        raise
    if not data.startswith(b"OggS") or len(data) < 32:
        raise RuntimeError("not an Ogg file")
    destination.parent.mkdir(parents=True, exist_ok=True)
    destination.write_bytes(data)
    time.sleep(1.2)


def main():
    dictionary, ambiguous_dictionary = load_dictionary()
    indexes = {
        "sakhno": parse_swac(WORK / "sakhno-index.tags.txt")[1],
        "streit": parse_swac(WORK / "voc-index.tags.txt")[1],
    }
    by_text = {}
    for collection, files in indexes.items():
        grouped = {}
        for filename, fields in files:
            text = (fields.get("SWAC_TEXT") or "").strip()
            key = norm(text)
            grouped.setdefault(key, []).append(filename)
        by_text[collection] = grouped

    commons = json.loads((WORK / "commons-meta.json").read_text(encoding="utf-8"))
    candidates = []
    excluded = {
        "not_russian_shtooka": 0,
        "index_not_unique": 0,
        "dictionary_not_unique": 0,
        "download_failed": 0,
    }
    for item in commons:
        collection = collection_for(item)
        if collection is None:
            excluded["not_russian_shtooka"] += 1
            continue
        word = item["title"][len("File:Ru-") : -len(".ogg")]
        key = norm(word)
        filenames = by_text[collection].get(key, [])
        if len(filenames) != 1 or key not in dictionary:
            if len(filenames) != 1:
                excluded["index_not_unique"] += 1
            else:
                excluded["dictionary_not_unique"] += 1
            continue
        bare, accented = dictionary[key]
        if norm(bare) != key:
            excluded["dictionary_not_unique"] += 1
            continue
        candidates.append((item, filenames[0], bare, accented, collection))

    chosen = {}
    for item, filename, bare, accented, collection in candidates:
        key = norm(bare)
        chosen.setdefault(key, []).append((item, filename, bare, accented, collection))
    unique = []
    for group in chosen.values():
        if len(group) == 1:
            unique.append(group[0])
        else:
            excluded["index_not_unique"] += len(group)

    AUDIO_DIR.mkdir(parents=True, exist_ok=True)
    installed = []
    rate_limited = False
    for item, filename, bare, accented, collection in sorted(unique, key=lambda row: row[1]):
        audio_id = filename[:-4]
        destination = AUDIO_DIR / filename
        if rate_limited and not (destination.is_file() and destination.stat().st_size > 32):
            excluded["download_failed"] += 1
            continue
        try:
            download(item["url"], destination)
        except RateLimited as error:
            print(f"download failed {filename}: {error}", flush=True)
            rate_limited = True
            excluded["download_failed"] += 1
            continue
        except Exception as error:
            print(f"download failed {filename}: {error}", flush=True)
            excluded["download_failed"] += 1
            if destination.exists() and destination.read_bytes()[:4] != b"OggS":
                destination.unlink()
            continue
        installed.append(
            {
                "audioId": audio_id,
                "filename": filename,
                "commonsTitle": item["title"],
                "commonsUrl": item["url"],
                "license": item["license"],
                "artist": item["artist"],
                "collection": collection,
                "bare": bare,
                "accented": accented,
                "sourceId": source_for(item),
            }
        )

    sources = {
        "shtooka-sakhno-cc-by-3.0-us": source_record(
            "shtooka-sakhno-cc-by-3.0-us",
            "Shtooka collection rus-balm-voc-sakhno; OpenRussian accented headword",
            "Audio README: Creative Commons Attribution 3.0 United States, http://creativecommons.org/licenses/by/3.0/us/. Dictionary text: CC BY-SA 4.0, https://creativecommons.org/licenses/by-sa/4.0/.",
            "CC BY 3.0 US; CC BY-SA 4.0",
            "Sergei Sakhno and Nicolas Vion, Shtooka Project. Stress-marked text from OpenRussian, CC BY-SA 4.0.",
        ),
        "shtooka-streit-cc-by-2.0-fr": source_record(
            "shtooka-streit-cc-by-2.0-fr",
            "Shtooka collection rus-balm-voc; OpenRussian accented headword",
            "Audio README: Creative Commons Attribution 2.0 France, http://creativecommons.org/licenses/by/2.0/fr/. Dictionary text: CC BY-SA 4.0, https://creativecommons.org/licenses/by-sa/4.0/.",
            "CC BY 2.0 FR; CC BY-SA 4.0",
            "Streit Goula, Streit Eric, and Nicolas Vion, Shtooka Project. Speaker on the file page: Gulnara Streit. Stress-marked text from OpenRussian, CC BY-SA 4.0.",
        ),
        "shtooka-streit-cc-by-3.0-fr": source_record(
            "shtooka-streit-cc-by-3.0-fr",
            "Shtooka file page states CC BY 3.0 France; OpenRussian accented headword",
            "Per-file Commons license: Creative Commons Attribution 3.0 France, https://creativecommons.org/licenses/by/3.0/fr/. Dictionary text: CC BY-SA 4.0, https://creativecommons.org/licenses/by-sa/4.0/.",
            "CC BY 3.0 FR; CC BY-SA 4.0",
            "Gulnara Streit, Shtooka Project. Stress-marked text from OpenRussian, CC BY-SA 4.0.",
        ),
    }
    used_sources = []
    texts = []
    rights = []
    audio = []
    for row in installed:
        if row["sourceId"] not in used_sources:
            used_sources.append(row["sourceId"])
        text_id = "text-" + row["audioId"]
        rights_id = "rights-" + row["audioId"]
        texts.append(
            {
                "id": text_id,
                "stressMarkedText": row["accented"],
                "sourceRecordId": row["sourceId"],
                "modificationStatus": "UNMODIFIED",
                "matchVerified": True,
            }
        )
        rights.append(
            {
                "id": rights_id,
                "sourceRecordId": row["sourceId"],
                "assetIdentifier": row["audioId"],
                "licenseStatus": "COMPATIBLE",
                "compatibilityDecision": "COMPATIBLE",
                "attributionRequired": True,
                "inclusionResult": "INCLUDED",
            }
        )
        audio.append(
            {
                "id": row["audioId"],
                "localAudioReference": "audio/" + row["filename"],
                "sourceTextId": text_id,
                "sourceRecordId": row["sourceId"],
                "rightsRecordId": rights_id,
            }
        )

    document = {
        "contentVersion": 1,
        "courseSegments": [],
        "planDefinitions": [],
        "gateDefinitions": [],
        "sourceRecords": [sources[source_id] for source_id in used_sources],
        "sourceTexts": texts,
        "assetRightsRecords": rights,
        "audioModels": audio,
        "optionalDrills": [],
    }
    SEED.write_text(json.dumps(document, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    report = {
        "bulkHost": "packs.shtooka.net did not resolve; individual Ogg bytes were not in the Internet Archive",
        "installedFrom": "Wikimedia Commons files whose pages cite the Shtooka Russian collections",
        "frequencyCap": "not applied; neither SWAC tags nor OpenRussian rows include a frequency rank",
        "shareAlikeConfirmation": "UNCONFIRMED",
        "installedCount": len(installed),
        "excluded": excluded,
        "ambiguousDictionaryRows": len(ambiguous_dictionary),
        "lessonMappings": "not supplied",
    }
    REPORT.write_text(json.dumps(report, indent=2) + "\n", encoding="utf-8")
    print(json.dumps(report, indent=2))


def source_record(record_id, provenance, basis, license_name, attribution):
    return {
        "id": record_id,
        "provenance": provenance,
        "redistributionBasis": basis,
        "licenseName": license_name,
        "attribution": attribution,
        "modificationStatus": "UNMODIFIED",
        "shareAlikeTreatment": "OpenRussian stress-marked headwords are CC BY-SA 4.0. The matched audio license has no share-alike term.",
        "shareAlikeConfirmation": "UNCONFIRMED",
    }


if __name__ == "__main__":
    main()
