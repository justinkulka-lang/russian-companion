#!/usr/bin/env python3
"""Deterministic intake for the Shtooka word recordings and OpenRussian headwords.

Copies source-supplied license text, filenames, and accented headwords.
Does not invent spellings, stress marks, frequency ranks, or Penguin lesson
mappings. A 3-lesson placeholder conductor is merged from
catalog/placeholder_pathway.json and bound to already-packaged audio IDs.
"""

import os
import csv
import gzip
import hashlib
import json
import re
import subprocess
import time
import urllib.error
import urllib.parse
import urllib.request
from collections import Counter
from datetime import timezone
from email.utils import parsedate_to_datetime
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
WORK = Path("/tmp/catalog-src")
ASSETS = ROOT / "app/src/main/assets"
AUDIO_DIR = ASSETS / "audio"
CACHE_DIR = ROOT / "catalog" / "audio-cache"
SEED = ASSETS / "content/bundled-content.json"
PLACEHOLDER_PATHWAY = ROOT / "catalog" / "placeholder_pathway.json"
INTAKE = ROOT / "catalog/intake.json"
DECISIONS = ROOT / "catalog/source-decisions.json"
CHAINS = ROOT / "catalog/license-chains.json"
REPORT = ROOT / "catalog/preparation-record.json"
SOURCES = ROOT / "catalog" / "sources"
FREQ_DIR = SOURCES / "openrussian-frequency"
OPENRUSSIAN_COMMIT = "1fd608d858f88dd2c2f5fe7ba3a1c4057294caac"
OPENRUSSIAN_EXPORT = "Update 2020-09-09"
LOOKUP_PATH = WORK / "commons-lookup.json"

USER_AGENT = "russian-companion-catalog-prep/1.0 (offline study app; license verification)"
OPENRUSSIAN_LIST = "https://en.openrussian.org/list/all?start={start}"
COMMONS_API = "https://commons.wikimedia.org/w/api.php"
TEXT_SOURCE = "https://github.com/Badestrand/russian-dictionary"
TEXT_LICENSE = "https://creativecommons.org/licenses/by-sa/4.0/"
TOP_N = 1000
REQUEST_GAP_SECONDS = 1.0
PACKAGES = {
    "sakhno": "rus-balm-voc-sakhno",
    "streit": "rus-balm-voc",
}
README_LICENSE_URLS = {
    "http://creativecommons.org/licenses/by/3.0/us/": "CC BY 3.0 US",
    "https://creativecommons.org/licenses/by/3.0/us/": "CC BY 3.0 US",
    "http://creativecommons.org/licenses/by/2.0/fr/": "CC BY 2.0 FR",
    "https://creativecommons.org/licenses/by/2.0/fr/": "CC BY 2.0 FR",
    "http://creativecommons.org/licenses/by/3.0/fr/": "CC BY 3.0 FR",
    "https://creativecommons.org/licenses/by/3.0/fr/": "CC BY 3.0 FR",
}
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


class HostUnavailable(RuntimeError):
    pass


class FetchState:
    def __init__(self):
        self.last_request = {}
        self.down = {}
        self.retry_not_before = {}
        self.readme_cache = {}
        self.yojik_files = {}

    def throttle(self, host):
        gap = 10.0 if host == "upload.wikimedia.org" else REQUEST_GAP_SECONDS
        wait = self.last_request.get(host, 0) + gap - time.time()
        if wait > 0:
            time.sleep(wait)
        self.last_request[host] = time.time()

    def ready(self, host):
        if host in self.down:
            return False
        return time.time() >= self.retry_not_before.get(host, 0)


FETCH = FetchState()
STAMP_PATH = WORK / "host-not-before.json"


def wait_retry_after(seconds):
    if isinstance(seconds, bool) or not isinstance(seconds, (int, float)) or seconds < 0:
        raise RuntimeError("retry-after must be a non-negative number of seconds")
    deadline = time.time() + float(seconds)
    while True:
        remain = deadline - time.time()
        if remain <= 0:
            return
        time.sleep(min(remain, 5))


def load_fetch_stamp():
    if not STAMP_PATH.is_file():
        return
    try:
        data = json.loads(STAMP_PATH.read_text(encoding="utf-8"))
    except json.JSONDecodeError:
        return
    for host, moment in data.items():
        if isinstance(moment, (int, float)):
            FETCH.retry_not_before[host] = max(FETCH.retry_not_before.get(host, 0), float(moment))


def save_fetch_stamp():
    STAMP_PATH.parent.mkdir(parents=True, exist_ok=True)
    STAMP_PATH.write_text(json.dumps(FETCH.retry_not_before), encoding="utf-8")


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
    host = urllib.parse.urlparse(url).hostname or ""
    delay = 1
    waited_for_limit = False
    last_error = None
    for attempt in range(4):
        if not FETCH.ready(host):
            raise RateLimited(url, max(0, int(FETCH.retry_not_before.get(host, 0) - time.time())))
        FETCH.throttle(host)
        request = urllib.request.Request(url, headers={"User-Agent": USER_AGENT})
        try:
            with urllib.request.urlopen(request, timeout=60) as response:
                return json.loads(response.read().decode("utf-8"))
        except urllib.error.HTTPError as error:
            last_error = error
            if error.code == 503:
                FETCH.down[host] = "503"
                raise HostUnavailable("503") from error
            if error.code != 429:
                raise
            header = error.headers.get("Retry-After") if error.headers else None
            seconds = parse_retry_after(header) if header else None
            if seconds is None:
                if attempt == 3:
                    raise RateLimited(url, delay) from error
                time.sleep(delay)
                delay *= 2
                continue
            if waited_for_limit:
                FETCH.retry_not_before[host] = time.time() + seconds
                save_fetch_stamp()
                raise RateLimited(url, seconds) from error
            print(f"retry-after {seconds}s for {host}", flush=True)
            until = time.time() + seconds
            FETCH.retry_not_before[host] = until
            save_fetch_stamp()
            wait_retry_after(seconds)
            FETCH.retry_not_before[host] = 0
            save_fetch_stamp()
            waited_for_limit = True
    raise RateLimited(url, delay) if last_error else RuntimeError(url)


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


def mirror_readme_source(host, collection, audio_license_name):
    text_note = "Stress-marked text from OpenRussian, CC BY-SA 4.0."
    text_basis = f"Dictionary text: CC BY-SA 4.0, {TEXT_LICENSE}. Source export: {TEXT_SOURCE}."
    package = PACKAGES.get(collection)
    if package is None:
        return None
    if collection == "sakhno" and audio_license_name == "CC BY 3.0 US":
        attribution = f"Sergueï Sakhno and Nicolas Vion, Shtooka Project. {text_note}"
        license_line = (
            "Mirror README: Creative Commons Attribution 3.0 United States, "
            "http://creativecommons.org/licenses/by/3.0/us/. "
        )
        slug = "sakhno-cc-by-3.0-us"
        license_name = "CC BY 3.0 US; CC BY-SA 4.0"
    elif collection == "streit" and audio_license_name == "CC BY 2.0 FR":
        attribution = f"Streit Goula, Streit Eric, and Nicolas Vion, Shtooka Project. {text_note}"
        license_line = (
            "Mirror README: Creative Commons Attribution 2.0 France, "
            "http://creativecommons.org/licenses/by/2.0/fr/. "
        )
        slug = "streit-cc-by-2.0-fr"
        license_name = "CC BY 2.0 FR; CC BY-SA 4.0"
    else:
        return None
    host_slug = "".join(ch.lower() if ch.isalnum() else "-" for ch in host).strip("-")
    return source_record(
        f"shtooka-{host_slug}-{slug}",
        f"{host} {package}; OpenRussian accented headword",
        license_line + text_basis,
        license_name,
        attribution,
        False,
    )


def grouped_source(audio_license_name, collection, file_page_license, mirror="upload.wikimedia.org"):
    if mirror != "upload.wikimedia.org":
        return mirror_readme_source(mirror, collection, audio_license_name)
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
        try:
            payload = fetch_json(COMMONS_API + "?" + query)
        except (RateLimited, HostUnavailable, urllib.error.URLError) as error:
            print(f"commons metadata stopped: {error}", flush=True)
            break
        pages = payload.get("query", {}).get("pages", {})
        by_title = {page.get("title"): page for page in pages.values()}
        for title in chunk:
            page = by_title.get(title)
            stored[title] = None if page is None or page.get("missing") is not None else page
        LOOKUP_PATH.write_text(json.dumps(stored), encoding="utf-8")
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


def valid_ogg(path: Path) -> bool:
    try:
        return path.is_file() and path.stat().st_size > 32 and path.read_bytes()[:4] == b"OggS"
    except OSError:
        return False


def parse_retry_after(value: str):
    text = (value or "").strip()
    if not text:
        return None
    if text.isdigit():
        return int(text)
    try:
        return int(float(text))
    except ValueError:
        pass
    try:
        moment = parsedate_to_datetime(text)
        if moment.tzinfo is None:
            moment = moment.replace(tzinfo=timezone.utc)
        return max(0, int(moment.timestamp() - time.time()))
    except (TypeError, ValueError, OverflowError):
        return None


def response_status(header_text: str):
    blocks = []
    current = []
    for line in header_text.splitlines():
        if line.startswith("HTTP/"):
            if current:
                blocks.append(current)
            current = [line]
        else:
            current.append(line)
    if current:
        blocks.append(current)
    if not blocks:
        return 0, None
    parts = blocks[-1][0].split()
    status = int(parts[1]) if len(parts) > 1 and parts[1].isdigit() else 0
    retry_after = None
    for line in blocks[-1]:
        if line.lower().startswith("retry-after:"):
            retry_after = parse_retry_after(line.split(":", 1)[1])
    return status, retry_after


def request_bytes(url, host, timeout):
    if host in FETCH.down:
        raise HostUnavailable(FETCH.down[host])
    if time.time() < FETCH.retry_not_before.get(host, 0):
        remaining = max(0, int(FETCH.retry_not_before[host] - time.time()))
        raise RateLimited(url, remaining)
    FETCH.throttle(host)
    temporary = CACHE_DIR / "request.partial"
    header_path = CACHE_DIR / "request.headers"
    CACHE_DIR.mkdir(parents=True, exist_ok=True)
    result = subprocess.run(
        [
            "curl", "-sS", "-L", "--max-time", str(timeout), "--retry", "0",
            "-A", USER_AGENT,
            "-D", str(header_path),
            "-o", str(temporary),
            "-w", "%{http_code}",
            url,
        ],
        capture_output=True,
    )
    header_text = ""
    if header_path.is_file():
        header_text = header_path.read_text(encoding="utf-8", errors="replace")
        header_path.unlink()
    status, retry_after = response_status(header_text)
    stderr = result.stderr.decode("utf-8", "replace")
    body = temporary.read_bytes() if temporary.is_file() else b""
    temporary.unlink(missing_ok=True)
    if result.returncode == 6 or "Could not resolve host" in stderr:
        raise HostUnavailable("dns")
    if result.returncode == 60 or "SSL certificate problem" in stderr:
        raise HostUnavailable("tls")
    if status == 429 or "429" in stderr:
        if retry_after is None:
            raise RateLimited(url, None)
        raise RateLimited(url, retry_after)
    if status == 503:
        raise HostUnavailable("503")
    if status == 404:
        raise FileNotFoundError(url)
    if result.returncode != 0 or status != 200:
        raise RuntimeError(stderr.strip() or f"curl exit {result.returncode} status {status}")
    return body


def fetch_with_backoff(url, host, timeout):
    delay = 1
    waited_for_limit = False
    for attempt in range(4):
        if not FETCH.ready(host):
            return None, "deferred"
        try:
            return request_bytes(url, host, timeout), "downloaded"
        except RateLimited as error:
            if error.retry_after is None:
                if attempt == 3:
                    return None, "failed"
                time.sleep(delay)
                delay *= 2
                continue
            if waited_for_limit:
                FETCH.retry_not_before[host] = time.time() + error.retry_after
                save_fetch_stamp()
                print(
                    f"retry-after {error.retry_after}s still in force for {host}",
                    flush=True,
                )
                return None, "deferred"
            print(f"retry-after {error.retry_after}s for {host}", flush=True)
            until = time.time() + error.retry_after
            FETCH.retry_not_before[host] = until
            save_fetch_stamp()
            wait_retry_after(error.retry_after)
            FETCH.retry_not_before[host] = 0
            save_fetch_stamp()
            waited_for_limit = True
        except HostUnavailable as error:
            FETCH.down[host] = str(error)
            print(f"host unavailable {host}: {error}", flush=True)
            return None, "host-down"
        except FileNotFoundError:
            return None, "missing"
        except Exception as error:
            print(f"fetch failed {url}: {error}", flush=True)
            if attempt == 3:
                return None, "failed"
            time.sleep(delay)
            delay *= 2
    return None, "failed"


def fetch_text(url, host):
    body, _status = fetch_with_backoff(url, host, 60)
    if body is None:
        return None
    return body.decode("utf-8", "replace")


def readme_license(url, host):
    if url in FETCH.readme_cache:
        return FETCH.readme_cache[url]
    text = fetch_text(url, host)
    resolved = None
    if text and license_class(text) == "compatible":
        found = []
        for marker, name in README_LICENSE_URLS.items():
            if marker in text:
                found.append(name)
        if len(set(found)) == 1:
            resolved = found[0]
    FETCH.readme_cache[url] = resolved
    return resolved


def load_yojik_names():
    names = {}
    for collection, package in PACKAGES.items():
        cache = WORK / f"yojik-{package}-ogg-names.json"
        if cache.is_file():
            names[collection] = set(json.loads(cache.read_text(encoding="utf-8")))
            continue
        listing = (
            "https://fsi-languages.yojik.eu/audiocollections/detailled/"
            f"{package}/ogg/"
        )
        text = fetch_text(listing, "fsi-languages.yojik.eu")
        found = set(re.findall(r'href="(rus-[0-9a-f]+\.ogg)"', text or ""))
        if not found:
            names[collection] = None
            continue
        cache.write_text(json.dumps(sorted(found)), encoding="utf-8")
        names[collection] = found
    return names


def seed_audio_cache():
    CACHE_DIR.mkdir(parents=True, exist_ok=True)
    AUDIO_DIR.mkdir(parents=True, exist_ok=True)
    for path in AUDIO_DIR.glob("*.ogg"):
        destination = CACHE_DIR / path.name
        if valid_ogg(path) and not valid_ogg(destination):
            destination.write_bytes(path.read_bytes())


def sidecar_path(filename):
    return CACHE_DIR / f"{filename}.license.json"


def read_sidecar(filename):
    path = sidecar_path(filename)
    if not path.is_file():
        return None
    try:
        return json.loads(path.read_text(encoding="utf-8"))
    except json.JSONDecodeError:
        return None


def write_sidecar(filename, mirror, license_name, collection, file_page_license):
    sidecar_path(filename).write_text(
        json.dumps(
            {
                "mirror": mirror,
                "licenseName": license_name,
                "collection": collection,
                "filePageLicense": file_page_license,
            },
            ensure_ascii=False,
        ),
        encoding="utf-8",
    )


def remember_license(item, mirror, from_cache):
    write_sidecar(
        item["filename"],
        mirror,
        item["audioLicense"],
        item["collection"],
        item.get("filePageLicense") or "",
    )
    item["byteMirror"] = mirror
    item["fromCache"] = from_cache


def mirrors_for(item):
    filename = item["filename"]
    collection = item["collection"]
    package = PACKAGES[collection]
    mirrors = [
        {
            "host": "packs.shtooka.net",
            "kind": "readme",
            "collection": collection,
            "url": f"http://packs.shtooka.net/{package}/ogg/{filename}",
            "readme_url": f"http://packs.shtooka.net/{package}/ogg/readme.txt",
        },
        {
            "host": "swac-collections.org",
            "kind": "readme",
            "collection": collection,
            "url": f"https://swac-collections.org/{package}/ogg/{filename}",
            "readme_url": f"https://swac-collections.org/{package}/ogg/readme.txt",
        },
    ]
    audio_url = (item.get("audioUrl") or "").split("?", 1)[0]
    if audio_url:
        mirrors.append(
            {
                "host": "upload.wikimedia.org",
                "kind": "commons",
                "url": audio_url,
            }
        )
    hits = []
    for name, filenames in FETCH.yojik_files.items():
        if filenames and filename in filenames:
            hits.append(name)
    if len(hits) == 1:
        chosen = hits[0]
        chosen_package = PACKAGES[chosen]
        mirrors.append(
            {
                "host": "fsi-languages.yojik.eu",
                "kind": "readme",
                "collection": chosen,
                "url": (
                    "https://fsi-languages.yojik.eu/audiocollections/detailled/"
                    f"{chosen_package}/ogg/{filename}"
                ),
                "readme_url": (
                    "https://fsi-languages.yojik.eu/audiocollections/detailled/"
                    f"{chosen_package}/ogg/readme.txt"
                ),
            }
        )
    elif len(hits) > 1:
        mirrors.append({"host": "fsi-languages.yojik.eu", "kind": "unclear"})
    return mirrors


def store_ogg(filename, body):
    if not body.startswith(b"OggS") or len(body) < 32:
        return False
    destination = CACHE_DIR / filename
    temporary = destination.with_suffix(".ogg.partial")
    temporary.write_bytes(body)
    temporary.replace(destination)
    return True


def acquire(item):
    filename = item["filename"]
    cache_file = CACHE_DIR / filename
    sidecar = read_sidecar(filename)
    if valid_ogg(cache_file) and sidecar:
        kind = license_class(sidecar.get("licenseName") or "")
        if (
            kind != "compatible"
            or not sidecar.get("mirror")
            or not sidecar.get("collection")
        ):
            item["acquireError"] = "cached mirror license is absent or unclear"
            return False
        item["audioLicense"] = sidecar["licenseName"]
        item["collection"] = sidecar["collection"]
        item["filePageLicense"] = sidecar.get("filePageLicense") or ""
        item["byteMirror"] = sidecar["mirror"]
        item["fromCache"] = True
        return True
    if (
        valid_ogg(cache_file)
        and item.get("audioUrl")
        and (item.get("filePageLicense") or "").strip()
        and license_class(item.get("audioLicense") or "") == "compatible"
    ):
        remember_license(item, "upload.wikimedia.org", True)
        return True
    for mirror in mirrors_for(item):
        if mirror["kind"] == "unclear":
            if item.get("commonsDeferred"):
                item["deferCommons"] = True
                return False
            item["acquireError"] = (
                "Yojik mirror lists the file under two README licenses "
                "and no single mirror license was resolved"
            )
            return False
        if not FETCH.ready(mirror["host"]):
            if mirror["kind"] == "commons" and mirror["host"] not in FETCH.down:
                item["commonsDeferred"] = True
            continue
        body, status = fetch_with_backoff(mirror["url"], mirror["host"], 45)
        if body is None:
            continue
        if not store_ogg(filename, body):
            continue
        if mirror["kind"] == "commons":
            stated = item.get("audioLicense") or ""
            if license_class(stated) != "compatible" or not (item.get("filePageLicense") or "").strip():
                cache_file.unlink(missing_ok=True)
                item["acquireError"] = "Commons file page license is absent or unclear"
                return False
            remember_license(item, "upload.wikimedia.org", False)
            return True
        resolved = readme_license(mirror["readme_url"], mirror["host"])
        if not resolved:
            cache_file.unlink(missing_ok=True)
            item["acquireError"] = f"{mirror['host']} README license is absent or unclear"
            return False
        item["audioLicense"] = resolved
        item["filePageLicense"] = ""
        item["collection"] = mirror["collection"]
        remember_license(item, mirror["host"], False)
        return True
    return False


def prior_byte_misses():
    if not DECISIONS.is_file():
        return set()
    try:
        payload = json.loads(DECISIONS.read_text(encoding="utf-8"))
    except json.JSONDecodeError:
        return set()
    names = set()
    for row in payload.get("decisions") or []:
        source_id = row.get("sourceId") or ""
        reason = row.get("reason") or ""
        if source_id.startswith("file:") and reason.startswith(
            "excluded: audio bytes were not retrieved"
        ):
            names.add(source_id[len("file:") :])
    return names


def inclusion_note(item):
    cached = "cached " if item.get("fromCache") else ""
    chain = item.get("yojikChain")
    if item.get("byteMirror") == "fsi-languages.yojik.eu" and chain:
        return (
            f"{cached}acquisition fsi-languages.yojik.eu; "
            f"license authority {chain['package']} README {chain['license']}"
        ).strip()
    if item.get("byteMirror") == "upload.wikimedia.org":
        return f"{cached}acquisition upload.wikimedia.org; Commons file page license {item.get('filePageLicense') or ''}".strip()
    return f"{cached}{item.get('byteMirror')} README license {item.get('audioLicense')}"


def sha256_file(path: Path) -> str:
    digest = hashlib.sha256()
    with path.open("rb") as handle:
        for chunk in iter(lambda: handle.read(1024 * 1024), b""):
            digest.update(chunk)
    return digest.hexdigest()


def package_evidence():
    evidence = {}
    for collection, package in PACKAGES.items():
        directory = SOURCES / package
        readme_path = directory / "readme.txt"
        index_path = directory / "index.tags.txt"
        listing_path = directory / "yojik-ogg-listing.html"
        readme = readme_path.read_text(encoding="utf-8")
        index = index_path.read_text(encoding="utf-8", errors="replace")
        listing = listing_path.read_text(encoding="utf-8", errors="replace")
        found = []
        for marker, name in README_LICENSE_URLS.items():
            if marker in readme:
                found.append((marker, name))
        licenses = {name for _, name in found}
        urls = {marker for marker, name in found if name in licenses}
        index_licenses = [
            line.split("=", 1)[1]
            for line in index.splitlines()
            if line.startswith("LICENSE=")
        ]
        fields = {}
        for line in index.splitlines():
            if line.startswith("[") and line != "[GLOBAL]":
                break
            if "=" in line and not line.startswith("["):
                key, value = line.split("=", 1)
                fields[key] = value
        filenames = set(re.findall(r"href=\"(rus-[0-9a-f]+\.ogg)\"", listing))
        indexed = set(re.findall(r"^\[(rus-[0-9a-f]+\.ogg)\]$", index, re.M))
        evidence[collection] = {
            "collection": collection,
            "package": package,
            "readmePath": f"catalog/sources/{package}/readme.txt",
            "readmeSha256": sha256_file(readme_path),
            "indexPath": f"catalog/sources/{package}/index.tags.txt",
            "indexSha256": sha256_file(index_path),
            "listingPath": f"catalog/sources/{package}/yojik-ogg-listing.html",
            "listingSha256": sha256_file(listing_path),
            "readme": readme,
            "licenseNames": licenses,
            "licenseUrls": urls,
            "indexLicenses": index_licenses,
            "authors": fields.get("SWAC_COLL_AUTHORS") or "",
            "speaker": fields.get("SWAC_SPEAK_NAME") or "",
            "filenames": filenames,
            "indexed": indexed,
            "compatible": (
                license_class(readme) == "compatible"
                and len(licenses) == 1
                and index_licenses == ["Creative Commons BY"]
                and bool(fields.get("SWAC_COLL_AUTHORS"))
                and bool(fields.get("SWAC_SPEAK_NAME"))
            ),
        }
    return evidence


def yojik_chain(filename, evidence):
    hits = [row for row in evidence.values() if filename in row["indexed"] and filename in row["filenames"]]
    if len(hits) != 1:
        return None
    row = hits[0]
    others = [other for other in evidence.values() if other is not row and filename in other["indexed"]]
    if others or not row["compatible"]:
        return None
    license_name = next(iter(row["licenseNames"]))
    license_url = next(iter(row["licenseUrls"]))
    if license_name == "CC BY 2.0 FR":
        creators = "Streit Goula, Streit Eric, Vion Nicolas"
        attribution = (
            "Streit Goula, Streit Eric, and Nicolas Vion, Shtooka Project. "
            "Stress-marked text from OpenRussian, CC BY-SA 4.0."
        )
    elif license_name == "CC BY 3.0 US":
        creators = "Sergueï Sakhno, Nicolas Vion"
        attribution = (
            "Sergueï Sakhno and Nicolas Vion, Shtooka Project. "
            "Stress-marked text from OpenRussian, CC BY-SA 4.0."
        )
    else:
        return None
    readme_flat = " ".join(row["readme"].split())
    if creators not in readme_flat or not row["speaker"] or not row["authors"]:
        return None
    return {
        "package": row["package"],
        "collection": row["collection"],
        "readmeSource": row["readmePath"],
        "readmeSha256": row["readmeSha256"],
        "indexSource": row["indexPath"],
        "indexSha256": row["indexSha256"],
        "listingSource": row["listingPath"],
        "listingSha256": row["listingSha256"],
        "creators": creators,
        "indexAuthors": row["authors"],
        "speaker": row["speaker"],
        "license": license_name,
        "licenseUrl": license_url,
        "attribution": attribution,
    }


def decision(filename, decision_name, license_name, reason):
    return {
        "sourceId": f"file:{filename}",
        "provenance": f"swac-file:{filename}",
        "decision": decision_name,
        "licenseName": license_name,
        "reason": reason,
    }


def assign_license(item, pages, commons_meta, reasons):
    title = "File:Ru-" + item["text"] + ".ogg"
    meta_items = [
        meta
        for meta in commons_meta.get(item["key"], [])
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
            return False
        if not audio["url"]:
            reasons[item["filename"]] = (
                stated,
                "excluded: Commons file page has no original audio URL",
            )
            return False
        item["audioLicense"] = stated
        item["audioUrl"] = audio["url"]
        item["filePageLicense"] = audio["license"]
    elif audio and audio["shtooka"] and not speaker_ok:
        reasons[item["filename"]] = (
            canonical_license(audio["license"]),
            "excluded: Commons file page names a different speaker from the SWAC collection",
        )
        return False
    elif len(meta_items) == 1:
        stated = canonical_license(meta_items[0].get("license") or "")
        kind = license_class(stated)
        if kind != "compatible":
            reasons[item["filename"]] = (
                stated,
                f"excluded: per-file Commons license is {kind}",
            )
            return False
        item["audioLicense"] = stated or item["readme"]["licenseName"]
        item["audioUrl"] = (meta_items[0].get("url") or "").split("?", 1)[0]
        item["filePageLicense"] = meta_items[0].get("license") or ""
    else:
        if len(item["collections"]) > 1:
            reasons[item["filename"]] = (
                "CC BY 3.0 US; CC BY 2.0 FR",
                "excluded: listed in two collections whose README licenses differ and no per-file license was found",
            )
            return False
        item["audioLicense"] = item["readme"]["licenseName"]
        item["audioUrl"] = ""
        item["filePageLicense"] = ""
    return True


def bind_placeholder_pathway(audio_ids):
    """Install the 3-lesson placeholder conductor. Audio IDs are catalog words, not book items."""
    raw = json.loads(PLACEHOLDER_PATHWAY.read_text(encoding="utf-8"))
    listen_index = 0
    for plan in raw["planDefinitions"]:
        if plan["activityType"] != "LISTEN_RECORD_COMPARE":
            continue
        if listen_index >= len(audio_ids):
            raise SystemExit("placeholder pathway needs packaged audio")
        plan["audioModelIds"] = [audio_ids[listen_index]]
        listen_index += 1
    return raw


def main():
    sakhno_readme = require_readme(
        SOURCES / "rus-balm-voc-sakhno" / "readme.txt",
        "http://creativecommons.org/licenses/by/3.0/us/",
        "CC BY 3.0 US",
    )
    streit_readme = require_readme(
        SOURCES / "rus-balm-voc" / "readme.txt",
        "http://creativecommons.org/licenses/by/2.0/fr/",
        "CC BY 2.0 FR",
    )
    dictionary, ambiguous_dictionary = load_dictionary()
    collections = {
        "sakhno": (parse_swac(SOURCES / "rus-balm-voc-sakhno" / "index.tags.txt"), sakhno_readme),
        "streit": (parse_swac(SOURCES / "rus-balm-voc" / "index.tags.txt"), streit_readme),
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
    overflow = []
    seen = set()
    mismatched = set()
    consulted_starts = []
    start = 0
    while len(ranked) < TOP_N and start < 90096:
        page = frequency_entries(start)
        if not page:
            break
        consulted_starts.append(start)
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
                overflow.append((rank, item, pages))
                continue
            if not assign_license(item, pages, commons, reasons):
                continue
            item["rank"] = rank
            ranked.append(item)
        print(f"frequency start={start} retained_candidates={len(ranked)}", flush=True)
        start += 50

    prior_misses = prior_byte_misses()
    previous_assets = {path.name for path in AUDIO_DIR.glob("*.ogg")}
    reviewed_yojik = set()
    for filename in previous_assets:
        sidecar = read_sidecar(filename)
        if sidecar and sidecar.get("mirror") == "fsi-languages.yojik.eu":
            reviewed_yojik.add(filename)
    load_fetch_stamp()
    seed_audio_cache()
    evidence = package_evidence()
    FETCH.yojik_files = {
        collection: row["filenames"] for collection, row in evidence.items()
    }
    retained = []

    def accept_item(item):
        filename = item["filename"]
        cache_file = CACHE_DIR / filename
        offline = os.environ.get("CATALOG_DOWNLOAD", "1") != "1"
        cached_bytes = valid_ogg(cache_file) and (
            read_sidecar(filename) is not None or bool(item.get("audioUrl"))
        )
        if offline and not cached_bytes:
            reasons[filename] = (
                item["audioLicense"],
                "excluded: audio bytes were not retrieved and were not fabricated",
            )
            return
        item.pop("deferCommons", None)
        item.pop("commonsDeferred", None)
        item.pop("acquireError", None)
        if not acquire(item):
            if item.get("deferCommons"):
                return "defer"
            if item.get("acquireError"):
                reasons[filename] = (
                    item.get("audioLicense") or "",
                    "excluded: " + item["acquireError"],
                )
            else:
                reasons[filename] = (
                    item.get("audioLicense") or "",
                    "excluded: audio bytes were not retrieved and were not fabricated",
                )
            return
        if item.get("byteMirror") == "fsi-languages.yojik.eu":
            chain = yojik_chain(filename, evidence)
            if chain is None or chain["license"] != item.get("audioLicense"):
                reasons[filename] = (
                    item.get("audioLicense") or "",
                    "excluded: Yojik package license chain is incomplete or ambiguous",
                )
                return
            item["yojikChain"] = chain
            item["collection"] = chain["collection"]
            item["filePageLicense"] = ""
        if license_class(item.get("audioLicense") or "") != "compatible":
            reasons[filename] = (
                item.get("audioLicense") or "",
                "excluded: mirror license is absent or unclear",
            )
            return
        checked = grouped_source(
            item["audioLicense"],
            item["collection"],
            item.get("filePageLicense") or "",
            item.get("byteMirror") or "",
        )
        if checked is None:
            reasons[filename] = (
                item.get("audioLicense") or "",
                "excluded: mirror license is absent or unclear",
            )
            return
        reasons[filename] = (
            item["audioLicense"],
            "included: frequency rank "
            f"{item['rank']}; {inclusion_note(item)}; OpenRussian accented headword matched",
        )
        retained.append(item)
        if len(retained) % 50 == 0:
            print(f"installed {len(retained)}", flush=True)

    deferred = []
    for item in ranked:
        if accept_item(item) == "defer":
            deferred.append(item)
    if deferred:
        until = FETCH.retry_not_before.get("upload.wikimedia.org", 0)
        remain = until - time.time()
        if remain > 0:
            print(f"waiting {int(remain)}s before deferred Commons originals", flush=True)
            wait_retry_after(remain)
        FETCH.retry_not_before["upload.wikimedia.org"] = 0
        save_fetch_stamp()
        print(f"retrying {len(deferred)} deferred Commons originals", flush=True)
        still_deferred = []
        for item in deferred:
            if accept_item(item) == "defer":
                still_deferred.append(item)
        if still_deferred:
            until = FETCH.retry_not_before.get("upload.wikimedia.org", 0)
            remain = until - time.time()
            if remain > 0:
                print(f"waiting {int(remain)}s before the last Commons attempt", flush=True)
                wait_retry_after(remain)
            FETCH.retry_not_before["upload.wikimedia.org"] = 0
            save_fetch_stamp()
            for item in still_deferred:
                if accept_item(item) == "defer":
                    reasons[item["filename"]] = (
                        item.get("audioLicense") or "",
                        "excluded: Commons retry-after was honored and the Yojik mirror "
                        "lists the file under two README licenses",
                    )

    def consider(rank, item, pages):
        filename = item["filename"]
        if filename in reasons or any(row["filename"] == filename for row in retained):
            return
        if not assign_license(item, pages, commons, reasons):
            return
        item["rank"] = rank
        ranked.append(item)
        if accept_item(item) == "defer":
            deferred.append(item)

    if len(retained) < TOP_N:
        for rank, item, pages in overflow:
            if len(retained) >= TOP_N:
                break
            consider(rank, item, pages)
    while len(retained) < TOP_N and start < 90096:
        page = frequency_entries(start)
        if not page:
            break
        consulted_starts.append(start)
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
            if len(retained) >= TOP_N:
                break
            consider(rank, item, pages)
        print(f"frequency start={start} installed={len(retained)}", flush=True)
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

    retained_names = {item["filename"] for item in retained}
    AUDIO_DIR.mkdir(parents=True, exist_ok=True)
    for path in AUDIO_DIR.glob("*.ogg"):
        if path.name not in retained_names:
            path.unlink()
    for item in retained:
        source = CACHE_DIR / item["filename"]
        destination = AUDIO_DIR / item["filename"]
        if not valid_ogg(source):
            raise SystemExit(f"retained audio missing from cache: {item['filename']}")
        if not valid_ogg(destination) or destination.stat().st_size != source.stat().st_size:
            destination.write_bytes(source.read_bytes())

    sources = {}
    source_order = []
    texts = []
    rights = []
    audio = []
    proposals = []
    ranks = []
    for item in retained:
        record = grouped_source(
            item["audioLicense"],
            item["collection"],
            item.get("filePageLicense") or "",
            item.get("byteMirror") or "",
        )
        if record is None:
            raise SystemExit(f"retained audio has no mirror license record: {item['filename']}")
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

    pathway = bind_placeholder_pathway([row["id"] for row in audio])
    document = {
        "contentVersion": 1,
        "courseSegments": pathway["courseSegments"],
        "planDefinitions": pathway["planDefinitions"],
        "gateDefinitions": pathway["gateDefinitions"],
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

    chain_rows = []
    for item in retained:
        audio_path = AUDIO_DIR / item["filename"]
        record = grouped_source(
            item["audioLicense"],
            item["collection"],
            item.get("filePageLicense") or "",
            item.get("byteMirror") or "",
        )
        if item.get("yojikChain"):
            chain = item["yojikChain"]
            authority = {
                "kind": "package-readme",
                "package": chain["package"],
                "readmeSource": chain["readmeSource"],
                "readmeSha256": chain["readmeSha256"],
                "indexSource": chain["indexSource"],
                "indexSha256": chain["indexSha256"],
                "listingSource": chain["listingSource"],
                "listingSha256": chain["listingSha256"],
                "creators": chain["creators"],
                "indexAuthors": chain["indexAuthors"],
                "speaker": chain["speaker"],
                "license": chain["license"],
                "licenseUrl": chain["licenseUrl"],
            }
            acquisition_url = (
                "https://fsi-languages.yojik.eu/audiocollections/detailled/"
                f"{chain['package']}/ogg/{item['filename']}"
            )
            attribution = chain["attribution"]
        else:
            authority = {
                "kind": "commons-file-page",
                "license": item.get("audioLicense") or "",
                "filePageLicense": item.get("filePageLicense") or "",
            }
            acquisition_url = (item.get("audioUrl") or "").split("?", 1)[0]
            attribution = record["attribution"] if record else ""
        chain_rows.append(
            {
                "filename": item["filename"],
                "sha256": sha256_file(audio_path),
                "acquisition": {
                    "host": item.get("byteMirror") or "",
                    "url": acquisition_url,
                },
                "licenseAuthority": authority,
                "attribution": attribution,
            }
        )
    frequency_pages = []
    for start_value in consulted_starts:
        path = FREQ_DIR / f"start-{start_value}.json"
        frequency_pages.append(
            {
                "start": start_value,
                "path": f"catalog/sources/openrussian-frequency/{path.name}",
                "sha256": sha256_file(path),
            }
        )
    snapshot_rows = []
    for row in evidence.values():
        for role, path_key, hash_key in (
            ("package-readme", "readmePath", "readmeSha256"),
            ("package-index", "indexPath", "indexSha256"),
            ("acquisition-listing", "listingPath", "listingSha256"),
        ):
            snapshot_rows.append(
                {
                    "role": role,
                    "package": row["package"],
                    "path": row[path_key],
                    "sha256": row[hash_key],
                }
            )
    verified_yojik = sorted(name for name in reviewed_yojik if name in retained_names)
    excluded_yojik = []
    for name in sorted(reviewed_yojik - retained_names):
        excluded_yojik.append(
            {
                "filename": name,
                "reason": reasons.get(name, ("", "excluded: no decision was recorded"))[1],
            }
        )
    backfilled = [item["filename"] for item in retained if item["filename"] not in previous_assets]
    chains_document = {
        "chainsVersion": 1,
        "openRussianDictionary": {
            "repository": "https://github.com/Badestrand/russian-dictionary",
            "commit": OPENRUSSIAN_COMMIT,
            "export": OPENRUSSIAN_EXPORT,
            "license": "CC BY-SA 4.0",
            "files": [
                {
                    "path": "nouns.csv",
                    "gitBlob": "202e6e19918681f1de061aa8b1839885721e60e9",
                    "sha256": "c388f9e6dde51932832be8d7e9afdbe9f0acee72fcf70677f0fe25ea61293c84",
                },
                {
                    "path": "verbs.csv",
                    "gitBlob": "efc1aebf79e36dc97d86c9bbce93810b7b1a997a",
                    "sha256": "8659de6799b949fb35f080b08088fb7d347ed300490954ebb380a31642646e1d",
                },
                {
                    "path": "adjectives.csv",
                    "gitBlob": "09986b701d1b232abb50db96c30b81ceab1c6526",
                    "sha256": "89ab5d10dcd2f21f6b485de704aef372f32251468e3aa96f18b21e259f26b80a",
                },
                {
                    "path": "others.csv",
                    "gitBlob": "2f819ba39652fceab3afe39ff9ef2a69b0dbd49d",
                    "sha256": "9f22a16b17fc9a564298112168b667fced11aafb254ccebf5b7544fc37cdaa92",
                },
            ],
        },
        "frequencySnapshot": {
            "source": "https://en.openrussian.org/list/all",
            "license": "CC BY-SA 4.0",
            "consultedStarts": consulted_starts,
            "pages": frequency_pages,
        },
        "snapshots": snapshot_rows,
        "files": chain_rows,
    }
    CHAINS.write_text(json.dumps(chains_document, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")

    def reason_bucket(reason):
        head = reason.split(";", 1)[0]
        if head.startswith("included:"):
            return "included"
        return head

    reason_counts = Counter(reason_bucket(reason) for _, reason in reasons.values())
    previous_report = {}
    if REPORT.is_file():
        try:
            previous_report = json.loads(REPORT.read_text(encoding="utf-8"))
        except json.JSONDecodeError:
            previous_report = {}
    offline = os.environ.get("CATALOG_DOWNLOAD", "1") != "1"
    if prior_misses:
        recovered = 0
        still_reasons = Counter()
        for name in prior_misses:
            reason = reasons.get(name, ("", "excluded: no decision was recorded"))[1]
            if reason.startswith("included:"):
                recovered += 1
            else:
                still_reasons[reason_bucket(reason)] += 1
        prior_count = len(prior_misses)
        still_count = prior_count - recovered
        still_payload = dict(sorted(still_reasons.items()))
    elif previous_report.get("priorDownloadMisses"):
        prior_count = previous_report["priorDownloadMisses"]
        recovered = previous_report.get("recoveredDownloadMisses", 0)
        still_count = previous_report.get("stillFailingDownloadMisses", 0)
        still_payload = previous_report.get("stillFailingDownloadReasons") or {}
    else:
        prior_count = 0
        recovered = 0
        still_count = 0
        still_payload = {}
    if offline and previous_report.get("bulkHost"):
        bulk_host = previous_report["bulkHost"]
    elif offline:
        bulk_host = "offline replay; no host was contacted"
    else:
        host_notes = []
        for host in (
            "packs.shtooka.net",
            "swac-collections.org",
            "upload.wikimedia.org",
            "fsi-languages.yojik.eu",
        ):
            if host in FETCH.down:
                host_notes.append(f"{host} unavailable ({FETCH.down[host]})")
            else:
                host_notes.append(f"{host} attempted")
        bulk_host = "; ".join(host_notes)
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
        "priorDownloadMisses": prior_count,
        "recoveredDownloadMisses": recovered,
        "stillFailingDownloadMisses": still_count,
        "stillFailingDownloadReasons": still_payload,
        "frequencySnapshotStarts": consulted_starts,
        "byteMirrors": dict(sorted(Counter(item.get("byteMirror") or "" for item in retained).items())),
        "yojikReviewed": len(reviewed_yojik),
        "yojikChainVerified": len(verified_yojik),
        "yojikChainExcluded": len(excluded_yojik),
        "yojikChainExcludedRows": excluded_yojik,
        "backfilledSlots": len(backfilled),
        "openRussianCommit": OPENRUSSIAN_COMMIT,
        "openRussianExport": OPENRUSSIAN_EXPORT,
        "retainedRanks": ranks,
        "lessonMappings": "not supplied",
        "bulkHost": bulk_host,
    }
    REPORT.write_text(json.dumps(report, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    print(json.dumps({key: report[key] for key in report if key != "retainedRanks"}, indent=2))


if __name__ == "__main__":
    main()
