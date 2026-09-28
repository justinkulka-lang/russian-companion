"""Mechanical transforms for retained source bytes.

Each transform removes markup only. It does not paraphrase, unstress, or
invent a form the retained bytes do not display.
"""

from __future__ import annotations

import hashlib
import html
import json
import re
from pathlib import Path

from units_3_5_intake import unfold_wiki_labels

_N_G = re.compile(r"\{\{n-g\|((?:[^{}]|\{\{[^{}]*\}\})*)\}\}")
_UXI = re.compile(r"\{\{uxi\|ru\|(.*)\|([^|{}]*)\}\}")
_TAG = re.compile(r"<[^>]+>")
_BREAK = re.compile(r"<br\s*/?>", re.IGNORECASE)


def unfold_wiktionary(text: str) -> str:
    """Show the text a Wiktionary sense or example template displays."""
    text = text.replace("{{lb|ru|with a {{lg|verb}}}}", "(with a verb)")
    text = _N_G.sub(lambda match: match.group(1), text)

    def uxi(match: re.Match) -> str:
        russian = unfold_wiki_labels(match.group(1)).strip()
        gloss = match.group(2).strip()
        return russian + "\n" + gloss

    text = _UXI.sub(uxi, text)
    return unfold_wiki_labels(text)


def html_visible(text: str) -> str:
    """Show the text a retained Pressbooks HTML span displays."""
    without_breaks = _BREAK.sub("\n", text)
    without_tags = _TAG.sub("", without_breaks)
    return html.unescape(without_tags)


def special_transformation(meta: dict, original: str, shipped: str) -> str | None:
    """Return the recorded transform label, or None when the page has none."""
    kind = meta.get("transform")
    if not kind:
        return None
    if kind == "wiktionary-template":
        visible = unfold_wiktionary(original)
        if shipped not in visible and shipped not in original:
            raise SystemExit(f"Shipped text is not in the Wiktionary unfold: {shipped!r}")
        if "{{" in shipped or "[[" in shipped or ".ogg" in shipped:
            raise SystemExit(f"Shipped Wiktionary text keeps markup or audio: {shipped!r}")
        return "selection" if shipped in original else "wiktionary template selection"
    if kind in ("pressbooks-html", "readyrussian-html"):
        lowered = original.lower()
        if any(token in lowered for token in ("<img", "data:image", "base64", "<script", "<audio", ".ogg", "data:audio")):
            raise SystemExit(f"Governed HTML span contains embedded media or audio: {shipped!r}")
        if "<" not in original and ">" not in original:
            if shipped not in original:
                raise SystemExit(f"Shipped text is not in the extracted span: {shipped!r}")
            if "<" in shipped or ">" in shipped or ".ogg" in shipped:
                raise SystemExit(f"Shipped extracted text keeps markup or audio: {shipped!r}")
            if kind == "pressbooks-html":
                return "pressbooks markup selection"
            return "readyrussian text extraction"
        visible = html_visible(original)
        if shipped not in visible:
            raise SystemExit(f"Shipped text is not in the HTML span: {shipped!r}")
        if "<" in shipped or ">" in shipped or ".ogg" in shipped:
            raise SystemExit(f"Shipped HTML text keeps markup or audio: {shipped!r}")
        if kind == "pressbooks-html":
            return "pressbooks markup selection"
        return "readyrussian markup selection"
    raise SystemExit(f"Unknown source transform {kind}")


def load_supplemental_pages(frozen, source_root: Path) -> None:
    """Load frozen pages that are not in the historical Wikibooks page list."""
    for page in frozen.freeze.get("supplemental") or []:
        path = source_root / page["file"]
        data = path.read_bytes()
        if hashlib.sha256(data).hexdigest() != page["sha256"]:
            raise SystemExit(f"Supplemental snapshot hash mismatch for {page['file']}")
        if page.get("byteCount") is not None and len(data) != page["byteCount"]:
            raise SystemExit(f"Supplemental byte count mismatch for {page['file']}")
        if page.get("revisionFile"):
            revision = json.loads((source_root / page["revisionFile"]).read_text(encoding="utf-8"))
            if str(revision["revid"]) != str(page["oldid"]):
                raise SystemExit(f"Supplemental revision id does not match oldid for {page['file']}")
            if hashlib.sha1(data).hexdigest() != revision["sha1"] or len(data) != revision["size"]:
                raise SystemExit(f"Supplemental bytes do not match the retained revision for {page['file']}")
        route_name = page.get("licenseRoute")
        route = (frozen.freeze.get("license") or {}).get("routes", {}).get(route_name)
        if route is None or page["sourceId"] not in route.get("appliesToSourceIds", []):
            raise SystemExit(f"Supplemental source {page['sourceId']} has no matching license route")
        license_bytes = (source_root / route["file"]).read_bytes()
        if hashlib.sha256(license_bytes).hexdigest() != route["sha256"]:
            raise SystemExit(f"License evidence hash mismatch for {route_name}")
        statement = route.get("requiredStatement") or ""
        if statement not in license_bytes.decode("utf-8"):
            raise SystemExit(f"License evidence for {route_name} does not state the required CC BY-SA terms")
        if route.get("revisionFile"):
            revision = json.loads((source_root / route["revisionFile"]).read_text(encoding="utf-8"))
            if str(revision.get("revid")) != str(route.get("oldid")):
                raise SystemExit(f"License revision id mismatch for {route_name}")
            if hashlib.sha1(license_bytes).hexdigest() != revision.get("sha1") or len(license_bytes) != revision.get("size"):
                raise SystemExit(f"License bytes do not match the retained revision for {route_name}")
        frozen.pages[page["key"]] = data.decode("utf-8")
        frozen.page_meta[page["key"]] = page
